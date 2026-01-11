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
package org.codelibs.fess.entity;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class FessUserTest extends UnitFessTestCase {

    @Test
    public void test_getName() {
        // Test with normal user name
        FessUser user = new TestFessUser("testuser", new String[] { "role1" }, new String[] { "group1" }, new String[] { "perm1" });
        assertEquals("testuser", user.getName());

        // Test with empty name
        user = new TestFessUser("", new String[] {}, new String[] {}, new String[] {});
        assertEquals("", user.getName());

        // Test with null name
        user = new TestFessUser(null, new String[] {}, new String[] {}, new String[] {});
        assertNull(user.getName());

        // Test with special characters in name
        user = new TestFessUser("user@domain.com", new String[] {}, new String[] {}, new String[] {});
        assertEquals("user@domain.com", user.getName());

        // Test with Unicode characters
        user = new TestFessUser("ユーザー名", new String[] {}, new String[] {}, new String[] {});
        assertEquals("ユーザー名", user.getName());
    }

    @Test
    public void test_getRoleNames() {
        // Test with multiple roles
        String[] roles = { "admin", "user", "manager" };
        FessUser user = new TestFessUser("testuser", roles, new String[] {}, new String[] {});
        assertArrayEquals(roles, user.getRoleNames());

        // Test with single role
        roles = new String[] { "user" };
        user = new TestFessUser("testuser", roles, new String[] {}, new String[] {});
        assertArrayEquals(roles, user.getRoleNames());

        // Test with empty roles
        roles = new String[] {};
        user = new TestFessUser("testuser", roles, new String[] {}, new String[] {});
        assertArrayEquals(roles, user.getRoleNames());

        // Test with null roles
        user = new TestFessUser("testuser", null, new String[] {}, new String[] {});
        assertNull(user.getRoleNames());

        // Test with roles containing special characters
        roles = new String[] { "role-1", "role_2", "role.3" };
        user = new TestFessUser("testuser", roles, new String[] {}, new String[] {});
        assertArrayEquals(roles, user.getRoleNames());
    }

    @Test
    public void test_getGroupNames() {
        // Test with multiple groups
        String[] groups = { "developers", "testers", "managers" };
        FessUser user = new TestFessUser("testuser", new String[] {}, groups, new String[] {});
        assertArrayEquals(groups, user.getGroupNames());

        // Test with single group
        groups = new String[] { "users" };
        user = new TestFessUser("testuser", new String[] {}, groups, new String[] {});
        assertArrayEquals(groups, user.getGroupNames());

        // Test with empty groups
        groups = new String[] {};
        user = new TestFessUser("testuser", new String[] {}, groups, new String[] {});
        assertArrayEquals(groups, user.getGroupNames());

        // Test with null groups
        user = new TestFessUser("testuser", new String[] {}, null, new String[] {});
        assertNull(user.getGroupNames());

        // Test with groups containing spaces
        groups = new String[] { "group one", "group two" };
        user = new TestFessUser("testuser", new String[] {}, groups, new String[] {});
        assertArrayEquals(groups, user.getGroupNames());
    }

    @Test
    public void test_getPermissions() {
        // Test with multiple permissions
        String[] permissions = { "read", "write", "delete", "execute" };
        FessUser user = new TestFessUser("testuser", new String[] {}, new String[] {}, permissions);
        assertArrayEquals(permissions, user.getPermissions());

        // Test with single permission
        permissions = new String[] { "read" };
        user = new TestFessUser("testuser", new String[] {}, new String[] {}, permissions);
        assertArrayEquals(permissions, user.getPermissions());

        // Test with empty permissions
        permissions = new String[] {};
        user = new TestFessUser("testuser", new String[] {}, new String[] {}, permissions);
        assertArrayEquals(permissions, user.getPermissions());

        // Test with null permissions
        user = new TestFessUser("testuser", new String[] {}, new String[] {}, null);
        assertNull(user.getPermissions());

        // Test with complex permission strings
        permissions = new String[] { "admin:*", "user:read", "document:write:123" };
        user = new TestFessUser("testuser", new String[] {}, new String[] {}, permissions);
        assertArrayEquals(permissions, user.getPermissions());
    }

    @Test
    public void test_isEditable_default() {
        // Test default implementation returns false
        FessUser user = new TestFessUser("testuser", new String[] {}, new String[] {}, new String[] {});
        assertFalse(user.isEditable());

        // Test with different user configurations
        user = new TestFessUser("admin", new String[] { "admin" }, new String[] { "admins" }, new String[] { "*" });
        assertFalse(user.isEditable());

        user = new TestFessUser("", new String[] {}, new String[] {}, new String[] {});
        assertFalse(user.isEditable());
    }

    @Test
    public void test_isEditable_custom() {
        // Test custom implementation that returns true
        FessUser user = new TestEditableFessUser("testuser", true);
        assertTrue(user.isEditable());

        // Test custom implementation that returns false
        user = new TestEditableFessUser("testuser", false);
        assertFalse(user.isEditable());
    }

    @Test
    public void test_refresh_default() {
        // Test default implementation returns false
        FessUser user = new TestFessUser("testuser", new String[] {}, new String[] {}, new String[] {});
        assertFalse(user.refresh());

        // Test with different user configurations
        user = new TestFessUser("admin", new String[] { "admin" }, new String[] { "admins" }, new String[] { "*" });
        assertFalse(user.refresh());

        user = new TestFessUser("", new String[] {}, new String[] {}, new String[] {});
        assertFalse(user.refresh());
    }

    @Test
    public void test_refresh_custom() {
        // Test custom implementation that returns true
        TestRefreshableFessUser user = new TestRefreshableFessUser("testuser", true);
        assertTrue(user.refresh());
        assertEquals(1, user.getRefreshCount());

        // Test multiple refreshes
        user.refresh();
        user.refresh();
        assertEquals(3, user.getRefreshCount());

        // Test custom implementation that returns false
        user = new TestRefreshableFessUser("testuser", false);
        assertFalse(user.refresh());
        assertEquals(1, user.getRefreshCount());
    }

    @Test
    public void test_serialization() {
        // Test that implementations can be serialized
        FessUser user = new TestFessUser("testuser", new String[] { "role1", "role2" }, new String[] { "group1", "group2" },
                new String[] { "perm1", "perm2" });

        // Verify the interface extends Serializable
        assertTrue(user instanceof java.io.Serializable);
    }

    @Test
    public void test_allFieldsCombination() {
        // Test with all fields populated
        String name = "complexUser";
        String[] roles = { "admin", "user", "viewer" };
        String[] groups = { "developers", "managers", "qa" };
        String[] permissions = { "read:all", "write:docs", "delete:own", "execute:scripts" };

        FessUser user = new TestFessUser(name, roles, groups, permissions);

        assertEquals(name, user.getName());
        assertArrayEquals(roles, user.getRoleNames());
        assertArrayEquals(groups, user.getGroupNames());
        assertArrayEquals(permissions, user.getPermissions());
        assertFalse(user.isEditable());
        assertFalse(user.refresh());
    }

    // Helper method to assert array equality
    private void assertArrayEquals(String[] expected, String[] actual) {
        if (expected == null && actual == null) {
            return;
        }
        assertNotNull(expected, "Expected array is null but actual is not");
        assertNotNull(actual, "Actual array is null but expected is not");
        assertEquals("Array lengths differ", expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals("Arrays differ at index " + i, expected[i], actual[i]);
        }
    }

    // Test implementation of FessUser interface
    private static class TestFessUser implements FessUser {
        private static final long serialVersionUID = 1L;
        private final String name;
        private final String[] roleNames;
        private final String[] groupNames;
        private final String[] permissions;

        public TestFessUser(String name, String[] roleNames, String[] groupNames, String[] permissions) {
            this.name = name;
            this.roleNames = roleNames;
            this.groupNames = groupNames;
            this.permissions = permissions;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String[] getRoleNames() {
            return roleNames;
        }

        @Override
        public String[] getGroupNames() {
            return groupNames;
        }

        @Override
        public String[] getPermissions() {
            return permissions;
        }
    }

    // Test implementation with custom isEditable
    private static class TestEditableFessUser extends TestFessUser {
        private static final long serialVersionUID = 1L;
        private final boolean editable;

        public TestEditableFessUser(String name, boolean editable) {
            super(name, new String[] {}, new String[] {}, new String[] {});
            this.editable = editable;
        }

        @Override
        public boolean isEditable() {
            return editable;
        }
    }

    // Test implementation with custom refresh
    private static class TestRefreshableFessUser extends TestFessUser {
        private static final long serialVersionUID = 1L;
        private final boolean refreshResult;
        private int refreshCount = 0;

        public TestRefreshableFessUser(String name, boolean refreshResult) {
            super(name, new String[] {}, new String[] {}, new String[] {});
            this.refreshResult = refreshResult;
        }

        @Override
        public boolean refresh() {
            refreshCount++;
            return refreshResult;
        }

        public int getRefreshCount() {
            return refreshCount;
        }
    }
}