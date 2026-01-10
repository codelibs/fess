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
package org.codelibs.fess.auth;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.auth.chain.AuthenticationChain;
import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class AuthenticationManagerTest extends UnitFessTestCase {

    private AuthenticationManager authenticationManager;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        authenticationManager = new AuthenticationManager();
    }

    // Test default constructor
    @Test
    public void test_constructor() {
        AuthenticationManager manager = new AuthenticationManager();
        assertNotNull(manager);
    }

    // Test insert with no chains
    @Test
    public void test_insert_noChains() {
        User user = createTestUser("testuser");
        authenticationManager.insert(user);
        // Should not throw exception even with no chains
    }

    // Test insert with single chain
    @Test
    public void test_insert_singleChain() {
        User user = createTestUser("testuser");
        TestAuthenticationChain chain = new TestAuthenticationChain();
        authenticationManager.addChain(chain);

        authenticationManager.insert(user);

        assertEquals(1, chain.updateCallCount);
        assertEquals(user, chain.lastUpdatedUser);
    }

    // Test insert with multiple chains
    @Test
    public void test_insert_multipleChains() {
        User user = createTestUser("testuser");
        TestAuthenticationChain chain1 = new TestAuthenticationChain();
        TestAuthenticationChain chain2 = new TestAuthenticationChain();
        TestAuthenticationChain chain3 = new TestAuthenticationChain();

        authenticationManager.addChain(chain1);
        authenticationManager.addChain(chain2);
        authenticationManager.addChain(chain3);

        authenticationManager.insert(user);

        assertEquals(1, chain1.updateCallCount);
        assertEquals(1, chain2.updateCallCount);
        assertEquals(1, chain3.updateCallCount);
        assertEquals(user, chain1.lastUpdatedUser);
        assertEquals(user, chain2.lastUpdatedUser);
        assertEquals(user, chain3.lastUpdatedUser);
    }

    // Test changePassword with no chains
    @Test
    public void test_changePassword_noChains() {
        boolean result = authenticationManager.changePassword("testuser", "newpass");
        assertTrue(result); // Returns true when no chains present
    }

    // Test changePassword with single chain success
    @Test
    public void test_changePassword_singleChain_success() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.changePasswordResult = true;
        authenticationManager.addChain(chain);

        boolean result = authenticationManager.changePassword("testuser", "newpass");

        assertTrue(result);
        assertEquals(1, chain.changePasswordCallCount);
        assertEquals("testuser", chain.lastChangePasswordUsername);
        assertEquals("newpass", chain.lastChangePasswordPassword);
    }

    // Test changePassword with single chain failure
    @Test
    public void test_changePassword_singleChain_failure() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.changePasswordResult = false;
        authenticationManager.addChain(chain);

        boolean result = authenticationManager.changePassword("testuser", "newpass");

        assertFalse(result);
        assertEquals(1, chain.changePasswordCallCount);
    }

    // Test changePassword with multiple chains all success
    @Test
    public void test_changePassword_multipleChains_allSuccess() {
        TestAuthenticationChain chain1 = new TestAuthenticationChain();
        TestAuthenticationChain chain2 = new TestAuthenticationChain();
        TestAuthenticationChain chain3 = new TestAuthenticationChain();
        chain1.changePasswordResult = true;
        chain2.changePasswordResult = true;
        chain3.changePasswordResult = true;

        authenticationManager.addChain(chain1);
        authenticationManager.addChain(chain2);
        authenticationManager.addChain(chain3);

        boolean result = authenticationManager.changePassword("testuser", "newpass");

        assertTrue(result);
        assertEquals(1, chain1.changePasswordCallCount);
        assertEquals(1, chain2.changePasswordCallCount);
        assertEquals(1, chain3.changePasswordCallCount);
    }

    // Test changePassword with multiple chains one failure
    @Test
    public void test_changePassword_multipleChains_oneFailure() {
        TestAuthenticationChain chain1 = new TestAuthenticationChain();
        TestAuthenticationChain chain2 = new TestAuthenticationChain();
        TestAuthenticationChain chain3 = new TestAuthenticationChain();
        chain1.changePasswordResult = true;
        chain2.changePasswordResult = false;
        chain3.changePasswordResult = true;

        authenticationManager.addChain(chain1);
        authenticationManager.addChain(chain2);
        authenticationManager.addChain(chain3);

        boolean result = authenticationManager.changePassword("testuser", "newpass");

        assertFalse(result);
        assertEquals(1, chain1.changePasswordCallCount);
        assertEquals(1, chain2.changePasswordCallCount);
        // allMatch may short-circuit, so chain3 might not be called
        assertTrue(chain3.changePasswordCallCount <= 1);
    }

    // Test delete with no chains
    @Test
    public void test_delete_noChains() {
        User user = createTestUser("testuser");
        authenticationManager.delete(user);
        // Should not throw exception even with no chains
    }

    // Test delete with single chain
    @Test
    public void test_delete_singleChain() {
        User user = createTestUser("testuser");
        TestAuthenticationChain chain = new TestAuthenticationChain();
        authenticationManager.addChain(chain);

        authenticationManager.delete(user);

        assertEquals(1, chain.deleteCallCount);
        assertEquals(user, chain.lastDeletedUser);
    }

    // Test delete with multiple chains
    @Test
    public void test_delete_multipleChains() {
        User user = createTestUser("testuser");
        TestAuthenticationChain chain1 = new TestAuthenticationChain();
        TestAuthenticationChain chain2 = new TestAuthenticationChain();
        TestAuthenticationChain chain3 = new TestAuthenticationChain();

        authenticationManager.addChain(chain1);
        authenticationManager.addChain(chain2);
        authenticationManager.addChain(chain3);

        authenticationManager.delete(user);

        assertEquals(1, chain1.deleteCallCount);
        assertEquals(1, chain2.deleteCallCount);
        assertEquals(1, chain3.deleteCallCount);
        assertEquals(user, chain1.lastDeletedUser);
        assertEquals(user, chain2.lastDeletedUser);
        assertEquals(user, chain3.lastDeletedUser);
    }

    // Test load with no chains
    @Test
    public void test_load_noChains() {
        User user = createTestUser("testuser");
        User result = authenticationManager.load(user);
        assertEquals(user, result);
    }

    // Test load with single chain
    @Test
    public void test_load_singleChain() {
        User user = createTestUser("testuser");
        User loadedUser = createTestUser("loadeduser");
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.loadResult = loadedUser;
        authenticationManager.addChain(chain);

        User result = authenticationManager.load(user);

        assertEquals(loadedUser, result);
        assertEquals(1, chain.loadCallCount);
        assertEquals(user, chain.lastLoadedUser);
    }

    // Test load with multiple chains (chained loading)
    @Test
    public void test_load_multipleChains() {
        User user = createTestUser("user0");
        User user1 = createTestUser("user1");
        User user2 = createTestUser("user2");
        User user3 = createTestUser("user3");

        TestAuthenticationChain chain1 = new TestAuthenticationChain();
        TestAuthenticationChain chain2 = new TestAuthenticationChain();
        TestAuthenticationChain chain3 = new TestAuthenticationChain();

        chain1.loadResult = user1;
        chain2.loadResult = user2;
        chain3.loadResult = user3;

        authenticationManager.addChain(chain1);
        authenticationManager.addChain(chain2);
        authenticationManager.addChain(chain3);

        User result = authenticationManager.load(user);

        assertEquals(user3, result);
        assertEquals(1, chain1.loadCallCount);
        assertEquals(1, chain2.loadCallCount);
        assertEquals(1, chain3.loadCallCount);

        // Verify chained loading
        assertEquals(user, chain1.lastLoadedUser);
        assertEquals(user1, chain2.lastLoadedUser);
        assertEquals(user2, chain3.lastLoadedUser);
    }

    // Test load with null result from chain
    @Test
    public void test_load_withNullResult() {
        User user = createTestUser("testuser");
        TestAuthenticationChain chain1 = new TestAuthenticationChain();
        TestAuthenticationChain chain2 = new TestAuthenticationChain();

        chain1.loadResult = null;
        chain2.loadResult = createTestUser("finaluser");

        authenticationManager.addChain(chain1);
        authenticationManager.addChain(chain2);

        User result = authenticationManager.load(user);

        assertEquals(chain2.loadResult, result);
        assertEquals(1, chain1.loadCallCount);
        assertEquals(1, chain2.loadCallCount);
        assertEquals(user, chain1.lastLoadedUser);
        assertEquals(null, chain2.lastLoadedUser);
    }

    // Test addChain single
    @Test
    public void test_addChain_single() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        authenticationManager.addChain(chain);

        // Verify chain was added by testing its effect
        User user = createTestUser("testuser");
        authenticationManager.insert(user);
        assertEquals(1, chain.updateCallCount);
    }

    // Test addChain multiple
    @Test
    public void test_addChain_multiple() {
        TestAuthenticationChain chain1 = new TestAuthenticationChain();
        TestAuthenticationChain chain2 = new TestAuthenticationChain();
        TestAuthenticationChain chain3 = new TestAuthenticationChain();

        authenticationManager.addChain(chain1);
        authenticationManager.addChain(chain2);
        authenticationManager.addChain(chain3);

        // Verify all chains were added by testing their effect
        User user = createTestUser("testuser");
        authenticationManager.insert(user);

        assertEquals(1, chain1.updateCallCount);
        assertEquals(1, chain2.updateCallCount);
        assertEquals(1, chain3.updateCallCount);
    }

    // Test addChain null handling
    @Test
    public void test_addChain_withNull() {
        // ArrayUtils.addAll handles null gracefully by adding it to the array
        authenticationManager.addChain(null);

        // Verify null chain doesn't cause issues during operations
        User user = createTestUser("testuser");
        try {
            authenticationManager.insert(user);
            fail("Should throw NullPointerException when processing null chain");
        } catch (NullPointerException e) {
            // Expected behavior when null chain is processed
            assertTrue(true);
        }
    }

    // Test chains order preservation
    @Test
    public void test_chains_orderPreservation() {
        List<TestAuthenticationChain> chains = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TestAuthenticationChain chain = new TestAuthenticationChain();
            chain.id = i;
            chains.add(chain);
            authenticationManager.addChain(chain);
        }

        // Test order by using load which processes chains sequentially
        User user = createTestUser("testuser");
        for (int i = 0; i < chains.size(); i++) {
            User nextUser = createTestUser("user" + (i + 1));
            chains.get(i).loadResult = nextUser;
        }

        authenticationManager.load(user);

        // Verify chains were called in order
        assertEquals(user.getName(), chains.get(0).lastLoadedUser.getName());
        for (int i = 1; i < chains.size(); i++) {
            assertEquals("user" + i, chains.get(i).lastLoadedUser.getName());
        }
    }

    // Helper method to create test user
    private User createTestUser(String username) {
        User user = new User();
        user.setName(username);
        return user;
    }

    // Test implementation of AuthenticationChain
    private static class TestAuthenticationChain implements AuthenticationChain {
        int id;
        int updateCallCount = 0;
        int deleteCallCount = 0;
        int changePasswordCallCount = 0;
        int loadCallCount = 0;

        User lastUpdatedUser;
        User lastDeletedUser;
        User lastLoadedUser;
        String lastChangePasswordUsername;
        String lastChangePasswordPassword;

        boolean changePasswordResult = false;
        User loadResult;

        @Override
        public void update(User user) {
            updateCallCount++;
            lastUpdatedUser = user;
        }

        @Override
        public void delete(User user) {
            deleteCallCount++;
            lastDeletedUser = user;
        }

        @Override
        public boolean changePassword(String username, String password) {
            changePasswordCallCount++;
            lastChangePasswordUsername = username;
            lastChangePasswordPassword = password;
            return changePasswordResult;
        }

        @Override
        public User load(User user) {
            loadCallCount++;
            lastLoadedUser = user;
            return loadResult;
        }
    }
}