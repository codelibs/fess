/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.auth.AuthenticationManager;
import org.codelibs.fess.exception.UserConflictException;
import org.codelibs.fess.helper.PasswordHashHelper;
import org.codelibs.fess.opensearch.user.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.opensearch.user.cbean.UserCB;
import org.codelibs.fess.opensearch.user.exbhv.UserBhv;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fesen.opensearch.action.index.IndexRequestBuilder;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Tests that {@link UserService} records when the password of a local user was changed, and tells
 * the login assist so that the session that changed it is kept.
 */
public class UserServicePasswordChangedAtTest extends UnitFessTestCase {

    private UserService userService;

    private final List<User> written = new ArrayList<>();

    private final List<String> accepted = new ArrayList<>();

    private final List<String> ldapChanged = new ArrayList<>();

    private User stored;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        userService = new UserService();
        // store() removes the passkeys a new user would inherit; there is no passkey index here
        userService.passkeyService = new PasskeyService() {
            @Override
            public void deleteByUserName(final String userName) {
            }
        };
        stored = new User();
        stored.setName("alice");
        stored.setPassword("{bcrypt}old");
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void insertOrUpdate(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                written.add(entity);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                written.add(entity);
            }
        };
        ComponentUtil.register(new PasswordHashHelper() {
            @Override
            public String encode(final String rawPassword) {
                return "{bcrypt}" + rawPassword;
            }

            @Override
            public boolean matches(final String rawPassword, final String storedPassword) {
                // the hash format is not part of the comparison, as with the real helper
                return storedPassword != null && storedPassword.replaceFirst("^\\{[^}]*\\}", "").equals(rawPassword);
            }
        }, "passwordHashHelper");
        ComponentUtil.register(new AuthenticationManager() {
            @Override
            public void insert(final User user) {
            }

            @Override
            public boolean changePassword(final String username, final String password) {
                ldapChanged.add(username);
                return true;
            }
        }, "authenticationManager");
        ComponentUtil.setFessLoginAssist(new FessLoginAssist() {
            @Override
            public void acceptPasswordChange(final String username, final long changedAt) {
                accepted.add(username + "@" + changedAt);
            }
        });
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        ComponentUtil.setFessLoginAssist(null);
        super.tearDown(testInfo);
    }

    @Test
    public void test_changePassword_recordsTheTime() {
        final long before = System.currentTimeMillis();
        userService.changePassword("alice", "new-password");
        final long after = System.currentTimeMillis();

        assertEquals(1, written.size());
        assertEquals("{bcrypt}new-password", written.get(0).getPassword());
        final long changedAt = written.get(0).getPasswordChangedAt();
        assertTrue(changedAt >= before && changedAt <= after);
        assertEquals(List.of("alice@" + changedAt), accepted);
    }

    @Test
    public void test_endOtherSessions_recordsTheTimeAndKeepsThePassword() {
        final long before = System.currentTimeMillis();
        userService.endOtherSessions("alice");
        final long after = System.currentTimeMillis();

        assertEquals(1, written.size());
        assertEquals("{bcrypt}old", written.get(0).getPassword());
        final long changedAt = written.get(0).getPasswordChangedAt();
        assertTrue(changedAt >= before && changedAt <= after);
        assertEquals(List.of("alice@" + changedAt), accepted);
    }

    @Test
    public void test_endOtherSessions_retriesAfterAConcurrentChangeAndKeepsTheNewPassword() {
        final int[] updates = { 0 };
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                // the second read sees the password another request has just changed
                final User u = new User();
                u.setName("alice");
                u.setPassword(updates[0] == 0 ? "{bcrypt}old" : "{bcrypt}changed");
                return OptionalEntity.of(u);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                if (updates[0]++ == 0) {
                    throw new RuntimeException("type=version_conflict_engine_exception");
                }
                written.add(entity);
            }
        };

        userService.endOtherSessions("alice");

        assertEquals(2, updates[0]);
        assertEquals(1, written.size());
        assertEquals("{bcrypt}changed", written.get(0).getPassword());
        assertEquals(1, accepted.size());
    }

    @Test
    public void test_endOtherSessions_givesUpAfterRepeatedConflicts() {
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                throw new RuntimeException("type=version_conflict_engine_exception");
            }
        };

        try {
            userService.endOtherSessions("alice");
            fail();
        } catch (final UserConflictException e) {
            // expected
        }
        assertTrue(written.isEmpty());
        assertTrue(accepted.isEmpty());
    }

    private static User versioned(final String name, final String password) {
        final User u = new User();
        u.setName(name);
        u.setPassword(password);
        u.asDocMeta().seqNo(5L).primaryTerm(1L);
        return u;
    }

    @Test
    public void test_changePassword_retriesAfterAConcurrentChange() {
        final int[] updates = { 0 };
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(versioned("alice", "{bcrypt}old"));
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                if (updates[0]++ == 0) {
                    throw new RuntimeException("type=version_conflict_engine_exception");
                }
                written.add(entity);
            }
        };

        userService.changePassword("alice", "new-password");

        assertEquals(2, updates[0]);
        assertEquals(1, written.size());
        assertEquals("{bcrypt}new-password", written.get(0).getPassword());
        assertEquals(1, accepted.size());
    }

    @Test
    public void test_changePassword_withCurrentPassword_changesWhenItStillMatches() {
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(versioned("alice", "{bcrypt}old"));
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                written.add(entity);
            }
        };

        userService.changePassword("alice", "old", "new-password");

        assertEquals(List.of("alice"), ldapChanged);
        assertEquals(1, written.size());
        assertEquals("{bcrypt}new-password", written.get(0).getPassword());
        assertEquals(1, accepted.size());
    }

    @Test
    public void test_changePassword_withCurrentPassword_staleIsRefusedBeforeLdapAndNothingIsWritten() {
        // the stored password was changed after the caller verified "old"
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(versioned("alice", "{bcrypt}changed"));
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                written.add(entity);
            }
        };

        try {
            userService.changePassword("alice", "old", "new-password");
            fail();
        } catch (final UserConflictException e) {
            assertFalse(e.getMessage().contains("old"), e.getMessage());
            assertFalse(e.getMessage().contains("new-password"), e.getMessage());
        }
        assertTrue(ldapChanged.isEmpty());
        assertTrue(written.isEmpty());
        assertTrue(accepted.isEmpty());
    }

    @Test
    public void test_changePassword_withCurrentPassword_staleFoundInTheLoopIsRefusedAndNothingIsWritten() {
        // another request changes the password after the first read (the one before the external change)
        // and the write of this request loses the race: the retry re-reads the changed user
        final int[] reads = { 0 };
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(versioned("alice", reads[0]++ < 2 ? "{bcrypt}old" : "{bcrypt}changed"));
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                if (written.isEmpty() && reads[0] == 2) {
                    throw new RuntimeException("type=version_conflict_engine_exception");
                }
                written.add(entity);
            }
        };

        try {
            userService.changePassword("alice", "old", "new-password");
            fail();
        } catch (final UserConflictException e) {
            // expected
        }
        assertEquals(3, reads[0]);
        assertTrue(written.isEmpty());
        assertTrue(accepted.isEmpty());
        // the external change is not undone: that is the documented gap between the two writes
        assertEquals(List.of("alice"), ldapChanged);
    }

    @Test
    public void test_changePassword_withCurrentPassword_retryAfterAnUnrelatedChangeSucceeds() {
        // the document is bumped between the read and the write, but the password is still the one that was
        // verified (only its hash format differs): the change goes through
        final int[] reads = { 0 };
        final int[] updates = { 0 };
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(versioned("alice", reads[0]++ < 2 ? "{bcrypt}old" : "{bcrypt-rehashed}old"));
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                if (updates[0]++ == 0) {
                    throw new RuntimeException("type=version_conflict_engine_exception");
                }
                written.add(entity);
            }
        };

        userService.changePassword("alice", "old", "new-password");

        assertEquals(2, updates[0]);
        assertEquals(1, written.size());
        assertEquals("{bcrypt}new-password", written.get(0).getPassword());
        assertEquals(1, accepted.size());
    }

    @Test
    public void test_changePassword_withCurrentPassword_userNotFound() {
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.empty();
            }
        };

        try {
            userService.changePassword("alice", "old", "new-password");
            fail();
        } catch (final org.codelibs.fess.exception.FessUserNotFoundException e) {
            // expected
        }
        assertTrue(written.isEmpty());
    }

    @Test
    public void test_changePassword_givesUpAfterRepeatedConflicts() {
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(versioned("alice", "{bcrypt}old"));
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                throw new RuntimeException("type=version_conflict_engine_exception");
            }
        };

        try {
            userService.changePassword("alice", "new-password");
            fail();
        } catch (final UserConflictException e) {
            // expected
        }
        assertTrue(written.isEmpty());
        assertTrue(accepted.isEmpty());
    }

    @Test
    public void test_store_ofAReadUser_isConditionalAndRefusedWhenTheUserChanged() {
        final List<String> calls = new ArrayList<>();
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectByPK(final String id) {
                return OptionalEntity.of(versioned("alice", "{bcrypt}old"));
            }

            @Override
            public void insertOrUpdate(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                calls.add("insertOrUpdate");
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                calls.add("update");
                throw new RuntimeException("type=version_conflict_engine_exception");
            }
        };
        final User edited = versioned("alice", "{bcrypt}old");
        edited.setId("YWxpY2U");

        try {
            userService.store(edited);
            fail();
        } catch (final UserConflictException e) {
            // expected
        }
        assertEquals(List.of("update"), calls);
        assertTrue(accepted.isEmpty());
    }

    @Test
    public void test_store_ofAReadUser_writesWithTheUpdate() {
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectByPK(final String id) {
                return OptionalEntity.of(versioned("alice", "{bcrypt}old"));
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                written.add(entity);
            }
        };
        final User edited = versioned("alice", "{bcrypt}old");
        edited.setId("YWxpY2U");

        userService.store(edited);

        assertEquals(1, written.size());
    }

    @Test
    public void test_store_ofANewUser_isNotConditional() {
        final List<String> calls = new ArrayList<>();
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectByPK(final String id) {
                return OptionalEntity.empty();
            }

            @Override
            public void insertOrUpdate(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                calls.add("insertOrUpdate");
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                calls.add("update");
            }
        };
        final User created = new User();
        created.setName("carol");
        created.setId("Y2Fyb2w");

        userService.store(created);

        assertEquals(List.of("insertOrUpdate"), calls);
    }

    @Test
    public void test_endOtherSessions_neverGoesBackwards() {
        final long future = System.currentTimeMillis() + 3_600_000L;
        stored.setPasswordChangedAt(future);

        userService.endOtherSessions("alice");

        assertEquals(Long.valueOf(future + 1), written.get(0).getPasswordChangedAt());
    }

    @Test
    public void test_endOtherSessions_unknownUser_changesNothing() {
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.empty();
            }
        };

        userService.endOtherSessions("nobody");

        assertTrue(written.isEmpty());
        assertTrue(accepted.isEmpty());
    }

    @Test
    public void test_changePassword_neverGoesBackwards() {
        // a node whose clock is behind must still produce a time later than the one it replaces
        final long future = System.currentTimeMillis() + 3_600_000L;
        stored.setPasswordChangedAt(future);

        userService.changePassword("alice", "new-password");

        assertEquals(Long.valueOf(future + 1), written.get(0).getPasswordChangedAt());
    }

    @Test
    public void test_store_withNewPassword_recordsTheTime() {
        final User user = new User();
        user.setName("alice");
        user.setPassword("{bcrypt}typed");
        user.setOriginalPassword("typed");
        user.setPasswordChangedAt(5L);

        userService.store(user);

        assertEquals(1, written.size());
        final long changedAt = written.get(0).getPasswordChangedAt();
        assertTrue(changedAt > 5L);
        assertEquals(List.of("alice@" + changedAt), accepted);
    }

    @Test
    public void test_store_withoutNewPassword_keepsTheTime() {
        final User user = new User();
        user.setName("alice");
        user.setPassword("{bcrypt}old");
        user.setPasswordChangedAt(5L);

        userService.store(user);

        assertEquals(Long.valueOf(5L), written.get(0).getPasswordChangedAt());
        assertTrue(accepted.isEmpty());
    }

    @Test
    public void test_store_withoutNewPassword_onUserWithoutTime_stillHasNone() {
        final User user = new User();
        user.setName("alice");
        user.setPassword("{bcrypt}old");

        userService.store(user);

        assertNull(written.get(0).getPasswordChangedAt());
        assertTrue(accepted.isEmpty());
    }

    @Test
    public void test_updateStoredPasswordHash_keepsTheTime() {
        stored.setPasswordChangedAt(7L);
        stored.setPassword("{sha256}legacy");

        assertTrue(userService.updateStoredPasswordHash("alice", "{sha256}legacy", "{bcrypt}rehashed"));

        assertEquals(Long.valueOf(7L), written.get(0).getPasswordChangedAt());
        assertTrue(accepted.isEmpty());
    }

    @Test
    public void test_getPasswordChangedAt() {
        assertEquals(0L, userService.getPasswordChangedAt("alice"));
        stored.setPasswordChangedAt(123L);
        assertEquals(123L, userService.getPasswordChangedAt("alice"));
    }

    @Test
    public void test_getPasswordChangedAt_unknownUser() {
        userService.userBhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.empty();
            }
        };
        assertEquals(0L, userService.getPasswordChangedAt("ghost"));
    }

    @Test
    public void test_changePassword_outsideARequest_stillSucceeds() {
        final AtomicReference<RuntimeException> thrown = new AtomicReference<>();
        ComponentUtil.setFessLoginAssist(new FessLoginAssist() {
            @Override
            public void acceptPasswordChange(final String username, final long changedAt) {
                final RuntimeException e = new IllegalStateException("no request");
                thrown.set(e);
                throw e;
            }
        });

        userService.changePassword("alice", "new-password");

        assertNotNull(thrown.get());
        assertEquals(1, written.size());
    }
}
