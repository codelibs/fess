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
package org.codelibs.fess.auth.chain;

import org.codelibs.fess.ldap.LdapManager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class LdapChainTest extends UnitFessTestCase {

    private LdapChain ldapChain;
    private TestLdapManager testLdapManager;
    private TestFessConfig testFessConfig;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ldapChain = new LdapChain();
        testLdapManager = new TestLdapManager();
        testFessConfig = new TestFessConfig();
        ComponentUtil.register(testLdapManager, "ldapManager");
        ComponentUtil.setFessConfig(testFessConfig);
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    public void test_update() {
        // Test update method
        User user = createTestUser("testuser", "password123");

        ldapChain.update(user);

        assertEquals(user, testLdapManager.insertedUser);
        assertTrue(testLdapManager.insertCalled);
    }

    public void test_update_withNullUser() {
        // Test update with null user
        ldapChain.update(null);

        assertNull(testLdapManager.insertedUser);
        assertTrue(testLdapManager.insertCalled);
    }

    public void test_delete() {
        // Test delete method
        User user = createTestUser("testuser", "password123");

        ldapChain.delete(user);

        assertEquals(user, testLdapManager.deletedUser);
        assertTrue(testLdapManager.deleteCalled);
    }

    public void test_delete_withNullUser() {
        // Test delete with null user
        ldapChain.delete(null);

        assertNull(testLdapManager.deletedUser);
        assertTrue(testLdapManager.deleteCalled);
    }

    public void test_changePassword_success() {
        // Test successful password change when LDAP admin sync is disabled
        testLdapManager.changePasswordResult = true;
        testFessConfig.ldapAdminSyncPassword = false;

        boolean result = ldapChain.changePassword("testuser", "newpassword");

        assertFalse(result); // returns !changed || syncPassword = !true || false = false
        assertEquals("testuser", testLdapManager.changePasswordUsername);
        assertEquals("newpassword", testLdapManager.changePasswordPassword);
        assertTrue(testLdapManager.changePasswordCalled);
    }

    public void test_changePassword_success_withSync() {
        // Test successful password change when LDAP admin sync is enabled
        testLdapManager.changePasswordResult = true;
        testFessConfig.ldapAdminSyncPassword = true;

        boolean result = ldapChain.changePassword("testuser", "newpassword");

        assertTrue(result); // !changed || sync = !true || true = true
        assertEquals("testuser", testLdapManager.changePasswordUsername);
        assertEquals("newpassword", testLdapManager.changePasswordPassword);
        assertTrue(testLdapManager.changePasswordCalled);
    }

    public void test_changePassword_failure() {
        // Test failed password change when LDAP admin sync is disabled
        testLdapManager.changePasswordResult = false;
        testFessConfig.ldapAdminSyncPassword = false;

        boolean result = ldapChain.changePassword("testuser", "newpassword");

        assertTrue(result); // !changed = !false = true
        assertEquals("testuser", testLdapManager.changePasswordUsername);
        assertEquals("newpassword", testLdapManager.changePasswordPassword);
        assertTrue(testLdapManager.changePasswordCalled);
    }

    public void test_changePassword_failure_withSync() {
        // Test failed password change when LDAP admin sync is enabled
        testLdapManager.changePasswordResult = false;
        testFessConfig.ldapAdminSyncPassword = true;

        boolean result = ldapChain.changePassword("testuser", "newpassword");

        assertTrue(result); // !changed || sync = !false || true = true
        assertEquals("testuser", testLdapManager.changePasswordUsername);
        assertEquals("newpassword", testLdapManager.changePasswordPassword);
        assertTrue(testLdapManager.changePasswordCalled);
    }

    public void test_changePassword_withNullUsername() {
        // Test password change with null username
        testLdapManager.changePasswordResult = false;
        testFessConfig.ldapAdminSyncPassword = false;

        boolean result = ldapChain.changePassword(null, "newpassword");

        assertTrue(result);
        assertNull(testLdapManager.changePasswordUsername);
        assertEquals("newpassword", testLdapManager.changePasswordPassword);
        assertTrue(testLdapManager.changePasswordCalled);
    }

    public void test_changePassword_withNullPassword() {
        // Test password change with null password
        testLdapManager.changePasswordResult = false;
        testFessConfig.ldapAdminSyncPassword = false;

        boolean result = ldapChain.changePassword("testuser", null);

        assertTrue(result);
        assertEquals("testuser", testLdapManager.changePasswordUsername);
        assertNull(testLdapManager.changePasswordPassword);
        assertTrue(testLdapManager.changePasswordCalled);
    }

    public void test_changePassword_withBothNull() {
        // Test password change with both null
        testLdapManager.changePasswordResult = false;
        testFessConfig.ldapAdminSyncPassword = false;

        boolean result = ldapChain.changePassword(null, null);

        assertTrue(result);
        assertNull(testLdapManager.changePasswordUsername);
        assertNull(testLdapManager.changePasswordPassword);
        assertTrue(testLdapManager.changePasswordCalled);
    }

    public void test_load() {
        // Test load method
        User user = createTestUser("testuser", "password123");

        User result = ldapChain.load(user);

        assertSame(user, result);
        assertEquals(user, testLdapManager.appliedUser);
        assertTrue(testLdapManager.applyCalled);
    }

    public void test_load_withNullUser() {
        // Test load with null user
        User result = ldapChain.load(null);

        assertNull(result);
        assertNull(testLdapManager.appliedUser);
        assertTrue(testLdapManager.applyCalled);
    }

    public void test_load_modifiesUser() {
        // Test that load method modifies the user object through apply
        User user = createTestUser("testuser", "password123");
        testLdapManager.applyModifiesUser = true;

        User result = ldapChain.load(user);

        assertSame(user, result);
        assertEquals("modified", user.getName()); // Check that user was modified
        assertTrue(testLdapManager.applyCalled);
    }

    // Helper method to create test user
    private User createTestUser(String username, String password) {
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setRoles(new String[] { "role1", "role2" });
        user.setGroups(new String[] { "group1", "group2" });
        return user;
    }

    // Test LdapManager implementation
    private static class TestLdapManager extends LdapManager {
        boolean insertCalled = false;
        boolean deleteCalled = false;
        boolean changePasswordCalled = false;
        boolean applyCalled = false;
        boolean applyModifiesUser = false;

        User insertedUser;
        User deletedUser;
        User appliedUser;
        String changePasswordUsername;
        String changePasswordPassword;
        boolean changePasswordResult = false;

        @Override
        public void insert(User user) {
            insertCalled = true;
            insertedUser = user;
        }

        @Override
        public void delete(User user) {
            deleteCalled = true;
            deletedUser = user;
        }

        @Override
        public boolean changePassword(String username, String password) {
            changePasswordCalled = true;
            changePasswordUsername = username;
            changePasswordPassword = password;
            return changePasswordResult;
        }

        @Override
        public void apply(User user) {
            applyCalled = true;
            appliedUser = user;
            if (applyModifiesUser && user != null) {
                user.setName("modified");
            }
        }
    }

    // Test FessConfig implementation
    private static class TestFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        boolean ldapAdminSyncPassword = false;

        @Override
        public boolean isLdapAdminSyncPassword() {
            return ldapAdminSyncPassword;
        }
    }
}