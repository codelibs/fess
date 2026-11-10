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

    private User stored;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        userService = new UserService();
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
        }, "passwordHashHelper");
        ComponentUtil.register(new AuthenticationManager() {
            @Override
            public void insert(final User user) {
            }

            @Override
            public boolean changePassword(final String username, final String password) {
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
