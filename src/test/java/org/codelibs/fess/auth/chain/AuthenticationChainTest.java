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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.opensearch.user.exentity.User;
import org.codelibs.fess.unit.UnitFessTestCase;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

public class AuthenticationChainTest extends UnitFessTestCase {

    // Test basic update operation
    @Test
    public void test_update_normalUser() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        User user = createTestUser("testuser", "Test User");

        chain.update(user);

        assertEquals(1, chain.updateCalls.size());
        assertEquals(user, chain.updateCalls.get(0));
        assertSame(user, chain.updateCalls.get(0));
    }

    // Test update with null user
    @Test
    public void test_update_nullUser() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.updateThrowsException = false;

        chain.update(null);

        assertEquals(1, chain.updateCalls.size());
        assertNull(chain.updateCalls.get(0));
    }

    // Test update with multiple calls
    @Test
    public void test_update_multipleCalls() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        User user1 = createTestUser("user1", "User One");
        User user2 = createTestUser("user2", "User Two");
        User user3 = createTestUser("user3", "User Three");

        chain.update(user1);
        chain.update(user2);
        chain.update(user3);

        assertEquals(3, chain.updateCalls.size());
        assertEquals(user1, chain.updateCalls.get(0));
        assertEquals(user2, chain.updateCalls.get(1));
        assertEquals(user3, chain.updateCalls.get(2));
    }

    // Test update with exception handling
    @Test
    public void test_update_withException() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.updateThrowsException = true;
        User user = createTestUser("testuser", "Test User");

        try {
            chain.update(user);
            fail("Expected exception to be thrown");
        } catch (RuntimeException e) {
            assertEquals("Update failed", e.getMessage());
        }

        assertEquals(1, chain.updateCalls.size());
    }

    // Test basic delete operation
    @Test
    public void test_delete_normalUser() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        User user = createTestUser("testuser", "Test User");

        chain.delete(user);

        assertEquals(1, chain.deleteCalls.size());
        assertEquals(user, chain.deleteCalls.get(0));
    }

    // Test delete with null user
    @Test
    public void test_delete_nullUser() {
        TestAuthenticationChain chain = new TestAuthenticationChain();

        chain.delete(null);

        assertEquals(1, chain.deleteCalls.size());
        assertNull(chain.deleteCalls.get(0));
    }

    // Test delete with multiple users
    @Test
    public void test_delete_multipleUsers() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            users.add(createTestUser("user" + i, "User " + i));
        }

        for (User user : users) {
            chain.delete(user);
        }

        assertEquals(5, chain.deleteCalls.size());
        for (int i = 0; i < 5; i++) {
            assertEquals(users.get(i), chain.deleteCalls.get(i));
        }
    }

    // Test delete with exception
    @Test
    public void test_delete_withException() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.deleteThrowsException = true;
        User user = createTestUser("testuser", "Test User");

        try {
            chain.delete(user);
            fail("Expected exception to be thrown");
        } catch (RuntimeException e) {
            assertEquals("Delete failed", e.getMessage());
        }

        assertEquals(1, chain.deleteCalls.size());
    }

    // Test changePassword with valid credentials
    @Test
    public void test_changePassword_success() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.changePasswordResult = true;

        boolean result = chain.changePassword("testuser", "newpassword123");

        assertTrue(result);
        assertEquals(1, chain.changePasswordCalls.size());
        assertEquals("testuser", chain.changePasswordCalls.get(0).getKey());
        assertEquals("newpassword123", chain.changePasswordCalls.get(0).getValue());
    }

    // Test changePassword with failure
    @Test
    public void test_changePassword_failure() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.changePasswordResult = false;

        boolean result = chain.changePassword("testuser", "newpassword");

        assertFalse(result);
        assertEquals(1, chain.changePasswordCalls.size());
    }

    // Test changePassword with null username
    @Test
    public void test_changePassword_nullUsername() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.changePasswordResult = false;

        boolean result = chain.changePassword(null, "password");

        assertFalse(result);
        assertEquals(1, chain.changePasswordCalls.size());
        assertNull(chain.changePasswordCalls.get(0).getKey());
        assertEquals("password", chain.changePasswordCalls.get(0).getValue());
    }

    // Test changePassword with null password
    @Test
    public void test_changePassword_nullPassword() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.changePasswordResult = true;

        boolean result = chain.changePassword("testuser", null);

        assertTrue(result);
        assertEquals(1, chain.changePasswordCalls.size());
        assertEquals("testuser", chain.changePasswordCalls.get(0).getKey());
        assertNull(chain.changePasswordCalls.get(0).getValue());
    }

    // Test changePassword with empty strings
    @Test
    public void test_changePassword_emptyStrings() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.changePasswordResult = false;

        boolean result = chain.changePassword("", "");

        assertFalse(result);
        assertEquals(1, chain.changePasswordCalls.size());
        assertEquals("", chain.changePasswordCalls.get(0).getKey());
        assertEquals("", chain.changePasswordCalls.get(0).getValue());
    }

    // Test changePassword with special characters
    @Test
    public void test_changePassword_specialCharacters() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.changePasswordResult = true;

        String specialUsername = "user@domain.com";
        String specialPassword = "P@$$w0rd!#%&*()";

        boolean result = chain.changePassword(specialUsername, specialPassword);

        assertTrue(result);
        assertEquals(1, chain.changePasswordCalls.size());
        assertEquals(specialUsername, chain.changePasswordCalls.get(0).getKey());
        assertEquals(specialPassword, chain.changePasswordCalls.get(0).getValue());
    }

    // Test changePassword with exception
    @Test
    public void test_changePassword_withException() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.changePasswordThrowsException = true;

        try {
            chain.changePassword("user", "pass");
            fail("Expected exception to be thrown");
        } catch (RuntimeException e) {
            assertEquals("Change password failed", e.getMessage());
        }

        assertEquals(1, chain.changePasswordCalls.size());
    }

    // Test load with existing user
    @Test
    public void test_load_existingUser() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        User inputUser = createTestUser("inputuser", "Input User");
        User loadedUser = createTestUser("loadeduser", "Loaded User");
        chain.loadResult = loadedUser;

        User result = chain.load(inputUser);

        assertNotNull(result);
        assertEquals(loadedUser, result);
        assertEquals(1, chain.loadCalls.size());
        assertEquals(inputUser, chain.loadCalls.get(0));
    }

    // Test load with null user
    @Test
    public void test_load_nullUser() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        User loadedUser = createTestUser("loadeduser", "Loaded User");
        chain.loadResult = loadedUser;

        User result = chain.load(null);

        assertEquals(loadedUser, result);
        assertEquals(1, chain.loadCalls.size());
        assertNull(chain.loadCalls.get(0));
    }

    // Test load returning null
    @Test
    public void test_load_returnsNull() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.loadResult = null;
        User inputUser = createTestUser("inputuser", "Input User");

        User result = chain.load(inputUser);

        assertNull(result);
        assertEquals(1, chain.loadCalls.size());
        assertEquals(inputUser, chain.loadCalls.get(0));
    }

    // Test load with multiple calls
    @Test
    public void test_load_multipleCalls() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        User user1 = createTestUser("user1", "User One");
        User user2 = createTestUser("user2", "User Two");
        User result1 = createTestUser("result1", "Result One");
        User result2 = createTestUser("result2", "Result Two");

        chain.loadResult = result1;
        User returnedUser1 = chain.load(user1);

        chain.loadResult = result2;
        User returnedUser2 = chain.load(user2);

        assertEquals(result1, returnedUser1);
        assertEquals(result2, returnedUser2);
        assertEquals(2, chain.loadCalls.size());
        assertEquals(user1, chain.loadCalls.get(0));
        assertEquals(user2, chain.loadCalls.get(1));
    }

    // Test load with exception
    @Test
    public void test_load_withException() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        chain.loadThrowsException = true;
        User user = createTestUser("testuser", "Test User");

        try {
            chain.load(user);
            fail("Expected exception to be thrown");
        } catch (RuntimeException e) {
            assertEquals("Load failed", e.getMessage());
        }

        assertEquals(1, chain.loadCalls.size());
    }

    // Test implementation state tracking
    @Test
    public void test_implementation_stateTracking() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        User user1 = createTestUser("user1", "User One");
        User user2 = createTestUser("user2", "User Two");

        // Perform various operations
        chain.update(user1);
        chain.delete(user2);
        chain.changePassword("user3", "pass3");
        chain.load(user1);

        // Verify state tracking
        assertEquals(1, chain.updateCalls.size());
        assertEquals(1, chain.deleteCalls.size());
        assertEquals(1, chain.changePasswordCalls.size());
        assertEquals(1, chain.loadCalls.size());

        // Verify independence of operations
        assertEquals(user1, chain.updateCalls.get(0));
        assertEquals(user2, chain.deleteCalls.get(0));
        assertEquals("user3", chain.changePasswordCalls.get(0).getKey());
        assertEquals(user1, chain.loadCalls.get(0));
    }

    // Test concurrent operations simulation
    @Test
    public void test_implementation_sequentialOperations() {
        TestAuthenticationChain chain = new TestAuthenticationChain();
        List<User> users = new ArrayList<>();

        // Create test users
        for (int i = 0; i < 10; i++) {
            users.add(createTestUser("user" + i, "User " + i));
        }

        // Perform sequential operations
        for (User user : users) {
            chain.update(user);
            chain.changePassword(user.getName(), "newpass" + user.getName());
            chain.load(user);
            if (users.indexOf(user) % 2 == 0) {
                chain.delete(user);
            }
        }

        // Verify all operations were tracked
        assertEquals(10, chain.updateCalls.size());
        assertEquals(10, chain.changePasswordCalls.size());
        assertEquals(10, chain.loadCalls.size());
        assertEquals(5, chain.deleteCalls.size());
    }

    // Helper method to create test user
    private User createTestUser(String username, String displayName) {
        User user = new User();
        user.setName(username);
        user.setDisplayName(displayName);
        return user;
    }

    // Test implementation of AuthenticationChain for testing
    private static class TestAuthenticationChain implements AuthenticationChain {
        // Track method calls
        List<User> updateCalls = new ArrayList<>();
        List<User> deleteCalls = new ArrayList<>();
        List<Map.Entry<String, String>> changePasswordCalls = new ArrayList<>();
        List<User> loadCalls = new ArrayList<>();

        // Control behavior
        boolean updateThrowsException = false;
        boolean deleteThrowsException = false;
        boolean changePasswordThrowsException = false;
        boolean loadThrowsException = false;
        boolean changePasswordResult = false;
        User loadResult = null;

        @Override
        public void update(User user) {
            updateCalls.add(user);
            if (updateThrowsException) {
                throw new RuntimeException("Update failed");
            }
        }

        @Override
        public void delete(User user) {
            deleteCalls.add(user);
            if (deleteThrowsException) {
                throw new RuntimeException("Delete failed");
            }
        }

        @Override
        public boolean changePassword(String username, String password) {
            changePasswordCalls.add(new HashMap.SimpleEntry<>(username, password));
            if (changePasswordThrowsException) {
                throw new RuntimeException("Change password failed");
            }
            return changePasswordResult;
        }

        @Override
        public User load(User user) {
            loadCalls.add(user);
            if (loadThrowsException) {
                throw new RuntimeException("Load failed");
            }
            return loadResult;
        }
    }
}