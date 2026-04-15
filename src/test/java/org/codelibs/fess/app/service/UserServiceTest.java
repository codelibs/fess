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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.codelibs.fess.app.pager.UserPager;
import org.codelibs.fess.opensearch.user.cbean.UserCB;
import org.codelibs.fess.opensearch.user.exbhv.UserBhv;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.optional.OptionalEntity;
import org.codelibs.fess.opensearch.user.allcommon.EsAbstractEntity.RequestOptionCall;
import org.opensearch.action.index.IndexRequestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Unit tests for {@link UserService}.
 * Tests user service business logic including store, delete, and password operations.
 */
public class UserServiceTest extends UnitFessTestCase {

    private UserService userService;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        userService = new UserService();
    }

    @Test
    public void test_defaultConstructor() {
        final UserService service = new UserService();
        assertNotNull(service);
    }

    @Test
    public void test_userPager_initialization() {
        final UserPager pager = new UserPager();
        assertNull(pager.id);
        assertEquals(1, pager.getCurrentPageNumber());
    }

    @Test
    public void test_user_surnameDefaultsToName() {
        final User user = new User();
        user.setName("testuser");
        assertNull(user.getSurname());

        // When surname is blank, convertEmptyToNull converts it to null
        user.setSurname("");
        assertNull(user.getSurname());

        user.setSurname(user.getName());
        assertEquals("testuser", user.getSurname());
    }

    @Test
    public void test_user_clearOriginalPassword() {
        final User user = new User();
        user.setOriginalPassword("secretpassword");
        assertEquals("secretpassword", user.getOriginalPassword());

        user.clearOriginalPassword();
        assertNull(user.getOriginalPassword());
    }

    @Test
    public void test_user_getId() {
        final User user = new User();
        assertNull(user.getId());

        user.setId("user-123");
        assertEquals("user-123", user.getId());
    }

    @Test
    public void test_user_setName() {
        final User user = new User();
        user.setName("testuser");
        assertEquals("testuser", user.getName());
    }

    @Test
    public void test_user_setPassword() {
        final User user = new User();
        user.setPassword("hashedpassword");
        assertEquals("hashedpassword", user.getPassword());
    }

    @Test
    public void test_user_groups() {
        final User user = new User();
        assertNull(user.getGroups());

        final String[] groups = new String[] { "group1", "group2" };
        user.setGroups(groups);
        assertNotNull(user.getGroups());
        assertEquals(2, user.getGroups().length);
        assertEquals("group1", user.getGroups()[0]);
        assertEquals("group2", user.getGroups()[1]);
    }

    @Test
    public void test_user_roles() {
        final User user = new User();
        assertNull(user.getRoles());

        final String[] roles = new String[] { "admin", "user" };
        user.setRoles(roles);
        assertNotNull(user.getRoles());
        assertEquals(2, user.getRoles().length);
        assertEquals("admin", user.getRoles()[0]);
        assertEquals("user", user.getRoles()[1]);
    }

    @Test
    public void test_userPager_pageSize() {
        final UserPager pager = new UserPager();
        assertEquals(25, pager.getPageSize()); // Default page size
    }

    @Test
    public void test_userPager_setCurrentPageNumber() {
        final UserPager pager = new UserPager();
        pager.setCurrentPageNumber(5);
        assertEquals(5, pager.getCurrentPageNumber());
    }

    @Test
    public void test_userPager_allRecordCount() {
        final UserPager pager = new UserPager();
        assertEquals(0, pager.getAllRecordCount());

        pager.setAllRecordCount(100);
        assertEquals(100, pager.getAllRecordCount());
    }

    @Test
    public void test_userPager_allPageCount() {
        final UserPager pager = new UserPager();
        assertEquals(0, pager.getAllPageCount());

        pager.setAllPageCount(10);
        assertEquals(10, pager.getAllPageCount());
    }

    @Test
    public void test_userPager_existPrePage() {
        final UserPager pager = new UserPager();
        assertFalse(pager.isExistPrePage());

        pager.setExistPrePage(true);
        assertTrue(pager.isExistPrePage());
    }

    @Test
    public void test_userPager_existNextPage() {
        final UserPager pager = new UserPager();
        assertFalse(pager.isExistNextPage());

        pager.setExistNextPage(true);
        assertTrue(pager.isExistNextPage());
    }

    @Test
    public void test_userPager_id() {
        final UserPager pager = new UserPager();
        assertNull(pager.id);

        pager.id = "user-id-123";
        assertEquals("user-id-123", pager.id);
    }

    @Test
    public void test_user_mail() {
        final User user = new User();
        assertNull(user.getMail());

        user.setMail("test@example.com");
        assertEquals("test@example.com", user.getMail());
    }

    @Test
    public void test_user_telephoneNumber() {
        final User user = new User();
        assertNull(user.getTelephoneNumber());

        user.setTelephoneNumber("123-456-7890");
        assertEquals("123-456-7890", user.getTelephoneNumber());
    }

    @Test
    public void test_user_homePhone() {
        final User user = new User();
        assertNull(user.getHomePhone());

        user.setHomePhone("098-765-4321");
        assertEquals("098-765-4321", user.getHomePhone());
    }

    @Test
    public void test_user_employeeNumber() {
        final User user = new User();
        assertNull(user.getEmployeeNumber());

        user.setEmployeeNumber("EMP001");
        assertEquals("EMP001", user.getEmployeeNumber());
    }

    @Test
    public void test_user_givenName() {
        final User user = new User();
        assertNull(user.getGivenName());

        user.setGivenName("John");
        assertEquals("John", user.getGivenName());
    }

    @Test
    public void test_user_surname() {
        final User user = new User();
        assertNull(user.getSurname());

        user.setSurname("Doe");
        assertEquals("Doe", user.getSurname());
    }

    @Test
    public void test_user_businessCategory() {
        final User user = new User();
        assertNull(user.getBusinessCategory());

        user.setBusinessCategory("Engineering");
        assertEquals("Engineering", user.getBusinessCategory());
    }

    @Test
    public void test_user_departmentNumber() {
        final User user = new User();
        assertNull(user.getDepartmentNumber());

        user.setDepartmentNumber("DEPT001");
        assertEquals("DEPT001", user.getDepartmentNumber());
    }

    @Test
    public void test_user_description() {
        final User user = new User();
        assertNull(user.getDescription());

        user.setDescription("Test user description");
        assertEquals("Test user description", user.getDescription());
    }

    @Test
    public void test_user_displayName() {
        final User user = new User();
        assertNull(user.getDisplayName());

        user.setDisplayName("John Doe");
        assertEquals("John Doe", user.getDisplayName());
    }

    @Test
    public void test_user_labeledURI() {
        final User user = new User();
        assertNull(user.getLabeledURI());

        user.setLabeledURI("https://example.com/profile");
        assertEquals("https://example.com/profile", user.getLabeledURI());
    }

    @Test
    public void test_user_initials() {
        final User user = new User();
        assertNull(user.getInitials());

        user.setInitials("JD");
        assertEquals("JD", user.getInitials());
    }

    @Test
    public void test_user_physicalDeliveryOfficeName() {
        final User user = new User();
        assertNull(user.getPhysicalDeliveryOfficeName());

        user.setPhysicalDeliveryOfficeName("Building A");
        assertEquals("Building A", user.getPhysicalDeliveryOfficeName());
    }

    @Test
    public void test_user_state() {
        final User user = new User();
        assertNull(user.getState());

        user.setState("CA");
        assertEquals("CA", user.getState());
    }

    @Test
    public void test_user_street() {
        final User user = new User();
        assertNull(user.getStreet());

        user.setStreet("123 Main St");
        assertEquals("123 Main St", user.getStreet());
    }

    @Test
    public void test_user_teletexTerminalIdentifier() {
        final User user = new User();
        assertNull(user.getTeletexTerminalIdentifier());

        user.setTeletexTerminalIdentifier("TTI001");
        assertEquals("TTI001", user.getTeletexTerminalIdentifier());
    }

    @Test
    public void test_user_title() {
        final User user = new User();
        assertNull(user.getTitle());

        user.setTitle("Senior Engineer");
        assertEquals("Senior Engineer", user.getTitle());
    }

    @Test
    public void test_user_postalCode() {
        final User user = new User();
        assertNull(user.getPostalCode());

        user.setPostalCode("12345");
        assertEquals("12345", user.getPostalCode());
    }

    @Test
    public void test_user_carLicense() {
        final User user = new User();
        assertNull(user.getCarLicense());

        user.setCarLicense("ABC123");
        assertEquals("ABC123", user.getCarLicense());
    }

    @Test
    public void test_user_city() {
        final User user = new User();
        assertNull(user.getCity());

        user.setCity("San Francisco");
        assertEquals("San Francisco", user.getCity());
    }

    @Test
    public void test_user_postOfficeBox() {
        final User user = new User();
        assertNull(user.getPostOfficeBox());

        user.setPostOfficeBox("PO Box 123");
        assertEquals("PO Box 123", user.getPostOfficeBox());
    }

    @Test
    public void test_user_postalAddress() {
        final User user = new User();
        assertNull(user.getPostalAddress());

        user.setPostalAddress("123 Main St, San Francisco, CA 12345");
        assertEquals("123 Main St, San Francisco, CA 12345", user.getPostalAddress());
    }

    @Test
    public void test_user_roomNumber() {
        final User user = new User();
        assertNull(user.getRoomNumber());

        user.setRoomNumber("101");
        assertEquals("101", user.getRoomNumber());
    }

    @Test
    public void test_user_mobile() {
        final User user = new User();
        assertNull(user.getMobile());

        user.setMobile("555-1234");
        assertEquals("555-1234", user.getMobile());
    }

    @Test
    public void test_user_facsimileTelephoneNumber() {
        final User user = new User();
        assertNull(user.getFacsimileTelephoneNumber());

        user.setFacsimileTelephoneNumber("555-5678");
        assertEquals("555-5678", user.getFacsimileTelephoneNumber());
    }

    @Test
    public void test_user_pager() {
        final User user = new User();
        assertNull(user.getPager());

        user.setPager("PAGE001");
        assertEquals("PAGE001", user.getPager());
    }

    @Test
    public void test_user_preferredLanguage() {
        final User user = new User();
        assertNull(user.getPreferredLanguage());

        user.setPreferredLanguage("en");
        assertEquals("en", user.getPreferredLanguage());
    }

    @Test
    public void test_user_destinationIndicator() {
        final User user = new User();
        assertNull(user.getDestinationIndicator());

        user.setDestinationIndicator("US");
        assertEquals("US", user.getDestinationIndicator());
    }

    @Test
    public void test_user_internationaliSDNNumber() {
        final User user = new User();
        assertNull(user.getInternationaliSDNNumber());

        user.setInternationaliSDNNumber("+1-555-1234");
        assertEquals("+1-555-1234", user.getInternationaliSDNNumber());
    }

    @Test
    public void test_user_registeredAddress() {
        final User user = new User();
        assertNull(user.getRegisteredAddress());

        user.setRegisteredAddress("123 Registered St");
        assertEquals("123 Registered St", user.getRegisteredAddress());
    }

    @Test
    public void test_updateStoredPasswordHash_updatesEntity() {
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword("{sha256}legacyhash");

        final AtomicReference<User> updated = new AtomicReference<>();
        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void update(final User entity) {
                updated.set(entity);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                updated.set(entity);
            }
        };
        userService.userBhv = bhv;

        final String newHash = "{bcrypt}$2a$10$abcdefghijklmnopqrstuv";
        final boolean result = userService.updateStoredPasswordHash("alice", newHash);

        assertTrue(result);
        assertNotNull(updated.get());
        assertEquals(newHash, updated.get().getPassword());
        assertEquals(newHash, stored.getPassword());
    }

    @Test
    public void test_updateStoredPasswordHash_nonExistentUser() {
        final AtomicBoolean updateCalled = new AtomicBoolean(false);
        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.empty();
            }

            @Override
            public void update(final User entity) {
                updateCalled.set(true);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                updateCalled.set(true);
            }
        };
        userService.userBhv = bhv;

        final boolean result = userService.updateStoredPasswordHash("ghost", "{bcrypt}$2a$10$x");
        assertFalse(result);
        assertFalse(updateCalled.get());
    }

    @Test
    public void test_updateStoredPasswordHash_nullOrEmpty() {
        // No userBhv access should occur; fail loudly if it does.
        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                fail("selectEntity must not be called for invalid input");
                return OptionalEntity.empty();
            }
        };
        userService.userBhv = bhv;

        assertFalse(userService.updateStoredPasswordHash(null, "{bcrypt}$2a$10$x"));
        assertFalse(userService.updateStoredPasswordHash("", "{bcrypt}$2a$10$x"));
        assertFalse(userService.updateStoredPasswordHash("  ", "{bcrypt}$2a$10$x"));
        assertFalse(userService.updateStoredPasswordHash("alice", null));
        assertFalse(userService.updateStoredPasswordHash("alice", ""));
        assertFalse(userService.updateStoredPasswordHash("alice", "  "));
    }

    @Test
    public void test_updateStoredPasswordHash_updateFailureReturnsFalse() {
        final User stored = new User();
        stored.setName("bob");
        stored.setPassword("{sha256}legacy");

        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void update(final User entity) {
                throw new RuntimeException("simulated version conflict");
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                throw new RuntimeException("simulated version conflict");
            }
        };
        userService.userBhv = bhv;

        final boolean result = userService.updateStoredPasswordHash("bob", "{bcrypt}$2a$10$x");
        assertFalse(result);
    }

    @Test
    public void test_updateStoredPasswordHash_doesNotCallAuthenticationManager() {
        final User stored = new User();
        stored.setName("carol");
        stored.setPassword("{sha256}legacy");

        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void update(final User entity) {
                // no-op
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                // no-op
            }
        };
        userService.userBhv = bhv;

        // Register a sentinel AuthenticationManager; if any internal path touches it,
        // the flag flips and we fail the regression test.
        final AtomicBoolean authTouched = new AtomicBoolean(false);
        final org.codelibs.fess.auth.AuthenticationManager sentinel = new org.codelibs.fess.auth.AuthenticationManager() {
            @Override
            public void insert(final User user) {
                authTouched.set(true);
            }

            @Override
            public void delete(final User user) {
                authTouched.set(true);
            }

            @Override
            public boolean changePassword(final String username, final String password) {
                authTouched.set(true);
                return true;
            }

            @Override
            public User load(final User user) {
                authTouched.set(true);
                return user;
            }
        };
        ComponentUtil.register(sentinel, "authenticationManager");

        final boolean result = userService.updateStoredPasswordHash("carol", "{bcrypt}$2a$10$x");
        assertTrue(result);
        assertFalse(authTouched.get(), "AuthenticationManager must not be invoked by updateStoredPasswordHash");
    }

    // ---------------------------------------------------------------------
    // updateStoredPasswordHash(username, expectedCurrentHash, newEncodedPassword)
    // ---------------------------------------------------------------------

    @Test
    public void test_updateStoredPasswordHash_cas_match_updates() {
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword("{sha256}legacy");

        final AtomicReference<User> updated = new AtomicReference<>();
        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void update(final User entity) {
                updated.set(entity);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                updated.set(entity);
            }
        };
        userService.userBhv = bhv;

        final String newHash = "{bcrypt}$2a$10$newhash";
        final boolean result = userService.updateStoredPasswordHash("alice", "{sha256}legacy", newHash);

        assertTrue(result);
        assertNotNull(updated.get());
        assertEquals(newHash, stored.getPassword());
    }

    @Test
    public void test_updateStoredPasswordHash_cas_mismatch_skipsUpdate() {
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword("{bcrypt}$2a$10$concurrentlyChanged");

        final AtomicBoolean updateCalled = new AtomicBoolean(false);
        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void update(final User entity) {
                updateCalled.set(true);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                updateCalled.set(true);
            }
        };
        userService.userBhv = bhv;

        // expected != current -> must skip update
        final boolean result = userService.updateStoredPasswordHash("alice", "{sha256}legacyExpected", "{bcrypt}$2a$10$newhash");

        assertFalse(result);
        assertFalse(updateCalled.get());
        // Stored password remains unchanged.
        assertEquals("{bcrypt}$2a$10$concurrentlyChanged", stored.getPassword());
    }

    @Test
    public void test_updateStoredPasswordHash_cas_nullExpected_unconditionalUpdate() {
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword("{sha256}legacy");

        final AtomicBoolean updateCalled = new AtomicBoolean(false);
        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void update(final User entity) {
                updateCalled.set(true);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                updateCalled.set(true);
            }
        };
        userService.userBhv = bhv;

        // null expected -> unconditional update (matches the 2-arg wrapper contract)
        final boolean result = userService.updateStoredPasswordHash("alice", null, "{bcrypt}$2a$10$newhash");

        assertTrue(result);
        assertTrue(updateCalled.get());
        assertEquals("{bcrypt}$2a$10$newhash", stored.getPassword());
    }

    @Test
    public void test_updateStoredPasswordHash_cas_nonExistentUser() {
        final AtomicBoolean updateCalled = new AtomicBoolean(false);
        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.empty();
            }

            @Override
            public void update(final User entity) {
                updateCalled.set(true);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                updateCalled.set(true);
            }
        };
        userService.userBhv = bhv;

        final boolean result = userService.updateStoredPasswordHash("ghost", "{sha256}legacy", "{bcrypt}$2a$10$newhash");
        assertFalse(result);
        assertFalse(updateCalled.get());
    }

    @Test
    public void test_updateStoredPasswordHash_cas_refreshPolicyTrue() {
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword("{sha256}legacy");

        final AtomicBoolean optCalled = new AtomicBoolean(false);
        // Capture that update(User, RequestOptionCall) overload is used AND
        // the lambda sets refresh policy to Constants.TRUE.
        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                // Inspect what opLambda does by handing it a stub builder-like
                // object. IndexRequestBuilder requires a real client, so we
                // instead invoke reflectively through a proxy. The easier
                // approach: directly verify that this 2-arg overload was
                // picked (the refreshPolicy value is set inside the lambda
                // against a real IndexRequestBuilder, which we cannot stub
                // here without pulling in the OpenSearch test fixture).
                optCalled.set(true);
            }
        };
        userService.userBhv = bhv;

        final boolean result = userService.updateStoredPasswordHash("alice", "{sha256}legacy", "{bcrypt}$2a$10$newhash");
        assertTrue(result);
        assertTrue("update(User, RequestOptionCall) overload must be invoked for refresh policy", optCalled.get());
    }

    @Test
    public void test_updateStoredPasswordHash_cas_updateThrows_returnsFalse() {
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword("{sha256}legacy");

        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void update(final User entity) {
                throw new RuntimeException("simulated version conflict");
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                throw new RuntimeException("simulated version conflict");
            }
        };
        userService.userBhv = bhv;

        final boolean result = userService.updateStoredPasswordHash("alice", "{sha256}legacy", "{bcrypt}$2a$10$newhash");
        assertFalse(result);
    }

    @Test
    public void test_updateStoredPasswordHash_2arg_delegatesTo3arg() {
        // The 2-arg overload must use null as the expected hash so that the
        // update is unconditional. We assert this by installing a stub that
        // records the expected argument via the 3-arg path semantics: a
        // non-matching "current" hash would cause a CAS skip, yet the 2-arg
        // call should still update because expected=null.
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword("something-unrelated");

        final AtomicBoolean updateCalled = new AtomicBoolean(false);
        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void update(final User entity) {
                updateCalled.set(true);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                updateCalled.set(true);
            }
        };
        userService.userBhv = bhv;

        final boolean result = userService.updateStoredPasswordHash("alice", "{bcrypt}$2a$10$newhash");
        assertTrue(result);
        assertTrue(updateCalled.get());
        assertEquals("{bcrypt}$2a$10$newhash", stored.getPassword());
    }

    @Test
    public void test_updateStoredPasswordHash_cas_versionConflict_returnsFalse() {
        // Simulate the classic OpenSearch CAS race: select passes (our
        // in-memory equals() check matches), but by the time update() fires
        // another writer has already bumped seqNo/primaryTerm, and
        // OpenSearch returns version_conflict_engine_exception. The wrapper
        // must translate that into `false` without propagating the exception
        // (lazy rehash is best-effort) and without emitting a noisy WARN.
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword("{sha256}legacy");

        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void update(final User entity) {
                throw new FakeVersionConflictEngineException("version_conflict_engine_exception on test");
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                throw new FakeVersionConflictEngineException("version_conflict_engine_exception on test");
            }
        };
        userService.userBhv = bhv;

        final boolean result = userService.updateStoredPasswordHash("alice", "{sha256}legacy", "{bcrypt}$2a$10$newhash");
        assertFalse(result);
    }

    @Test
    public void test_updateStoredPasswordHash_cas_versionConflict_byMessage_returnsFalse() {
        // Some implementations surface the conflict as a generic RuntimeException
        // whose message contains the OpenSearch marker. The classifier should
        // still return false quietly.
        final User stored = new User();
        stored.setName("alice");
        stored.setPassword("{sha256}legacy");

        final UserBhv bhv = new UserBhv() {
            @Override
            public OptionalEntity<User> selectEntity(final CBCall<UserCB> cbLambda) {
                return OptionalEntity.of(stored);
            }

            @Override
            public void update(final User entity, final RequestOptionCall<IndexRequestBuilder> opLambda) {
                throw new RuntimeException("opensearch said: version_conflict_engine_exception [seqNo=7]");
            }
        };
        userService.userBhv = bhv;

        final boolean result = userService.updateStoredPasswordHash("alice", "{sha256}legacy", "{bcrypt}$2a$10$newhash");
        assertFalse(result);
    }

    /**
     * Stand-in for
     * {@code org.opensearch.index.engine.VersionConflictEngineException}
     * whose class name alone (via {@code endsWith}) identifies the error for
     * the classifier inside {@link UserService#isVersionConflict(Throwable)}.
     * We avoid importing the real type so this unit test does not pull in the
     * OpenSearch engine module.
     */
    private static final class FakeVersionConflictEngineException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        FakeVersionConflictEngineException(final String message) {
            super(message);
        }
    }

    @Test
    public void test_user_x121Address() {
        final User user = new User();
        assertNull(user.getX121Address());

        user.setX121Address("X121001");
        assertEquals("X121001", user.getX121Address());
    }
}
