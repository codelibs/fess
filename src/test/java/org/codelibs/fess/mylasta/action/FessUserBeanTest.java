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
package org.codelibs.fess.mylasta.action;

import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.unit.UnitFessTestCase;

public class FessUserBeanTest extends UnitFessTestCase {

    private FessUserBean fessUserBean;
    private TestFessUser testUser;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        testUser = new TestFessUser();
        fessUserBean = new FessUserBean(testUser);
    }

    public void test_getUserId() {
        // Test with normal user
        testUser.setName("testuser");
        assertEquals("testuser", fessUserBean.getUserId());

        // Test with another name
        testUser.setName("admin");
        assertEquals("admin", fessUserBean.getUserId());
    }

    public void test_getPermissions() {
        // Test with empty permissions
        testUser.setPermissions(new String[] {});
        assertNotNull(fessUserBean.getPermissions());
        assertEquals(0, fessUserBean.getPermissions().length);

        // Test with single permission
        testUser.setPermissions(new String[] { "read" });
        String[] permissions = fessUserBean.getPermissions();
        assertEquals(1, permissions.length);
        assertEquals("read", permissions[0]);

        // Test with multiple permissions
        testUser.setPermissions(new String[] { "read", "write", "delete" });
        permissions = fessUserBean.getPermissions();
        assertEquals(3, permissions.length);
        assertEquals("read", permissions[0]);
        assertEquals("write", permissions[1]);
        assertEquals("delete", permissions[2]);
    }

    public void test_getRoles() {
        // Test with empty roles
        testUser.setRoleNames(new String[] {});
        assertNotNull(fessUserBean.getRoles());
        assertEquals(0, fessUserBean.getRoles().length);

        // Test with single role
        testUser.setRoleNames(new String[] { "admin" });
        String[] roles = fessUserBean.getRoles();
        assertEquals(1, roles.length);
        assertEquals("admin", roles[0]);

        // Test with multiple roles
        testUser.setRoleNames(new String[] { "admin", "user", "manager" });
        roles = fessUserBean.getRoles();
        assertEquals(3, roles.length);
        assertEquals("admin", roles[0]);
        assertEquals("user", roles[1]);
        assertEquals("manager", roles[2]);
    }

    public void test_getGroups() {
        // Test with empty groups
        testUser.setGroupNames(new String[] {});
        assertNotNull(fessUserBean.getGroups());
        assertEquals(0, fessUserBean.getGroups().length);

        // Test with single group
        testUser.setGroupNames(new String[] { "developers" });
        String[] groups = fessUserBean.getGroups();
        assertEquals(1, groups.length);
        assertEquals("developers", groups[0]);

        // Test with multiple groups
        testUser.setGroupNames(new String[] { "developers", "testers", "managers" });
        groups = fessUserBean.getGroups();
        assertEquals(3, groups.length);
        assertEquals("developers", groups[0]);
        assertEquals("testers", groups[1]);
        assertEquals("managers", groups[2]);
    }

    public void test_isEditable() {
        // Test with non-editable user (default)
        testUser.setEditable(false);
        assertFalse(fessUserBean.isEditable());

        // Test with editable user
        testUser.setEditable(true);
        assertTrue(fessUserBean.isEditable());
    }

    public void test_hasRole() {
        // Test with no roles
        testUser.setRoleNames(new String[] {});
        assertFalse(fessUserBean.hasRole("admin"));

        // Test with single role - matching
        testUser.setRoleNames(new String[] { "admin" });
        assertTrue(fessUserBean.hasRole("admin"));

        // Test with single role - not matching
        assertFalse(fessUserBean.hasRole("user"));

        // Test with multiple roles
        testUser.setRoleNames(new String[] { "admin", "user", "manager" });
        assertTrue(fessUserBean.hasRole("admin"));
        assertTrue(fessUserBean.hasRole("user"));
        assertTrue(fessUserBean.hasRole("manager"));
        assertFalse(fessUserBean.hasRole("guest"));

        // Test with null check
        assertFalse(fessUserBean.hasRole(null));
    }

    public void test_hasRoles() {
        // Test with no roles
        testUser.setRoleNames(new String[] {});
        assertFalse(fessUserBean.hasRoles(new String[] { "admin", "user" }));

        // Test with single role matching one of accepted
        testUser.setRoleNames(new String[] { "admin" });
        assertTrue(fessUserBean.hasRoles(new String[] { "admin", "user" }));
        assertTrue(fessUserBean.hasRoles(new String[] { "guest", "admin" }));

        // Test with no matching roles
        assertFalse(fessUserBean.hasRoles(new String[] { "guest", "viewer" }));

        // Test with multiple roles
        testUser.setRoleNames(new String[] { "admin", "user", "manager" });
        assertTrue(fessUserBean.hasRoles(new String[] { "admin" }));
        assertTrue(fessUserBean.hasRoles(new String[] { "user", "guest" }));
        assertTrue(fessUserBean.hasRoles(new String[] { "manager", "viewer" }));
        assertFalse(fessUserBean.hasRoles(new String[] { "guest", "viewer" }));

        // Test with empty accepted roles
        assertFalse(fessUserBean.hasRoles(new String[] {}));
    }

    public void test_hasGroup() {
        // Test with no groups
        testUser.setGroupNames(new String[] {});
        assertFalse(fessUserBean.hasGroup("developers"));

        // Test with single group - matching
        testUser.setGroupNames(new String[] { "developers" });
        assertTrue(fessUserBean.hasGroup("developers"));

        // Test with single group - not matching
        assertFalse(fessUserBean.hasGroup("testers"));

        // Test with multiple groups
        testUser.setGroupNames(new String[] { "developers", "testers", "managers" });
        assertTrue(fessUserBean.hasGroup("developers"));
        assertTrue(fessUserBean.hasGroup("testers"));
        assertTrue(fessUserBean.hasGroup("managers"));
        assertFalse(fessUserBean.hasGroup("guests"));

        // Test with null check
        assertFalse(fessUserBean.hasGroup(null));
    }

    public void test_hasGroups() {
        // Test with no groups
        testUser.setGroupNames(new String[] {});
        assertFalse(fessUserBean.hasGroups(new String[] { "developers", "testers" }));

        // Test with single group matching one of accepted
        testUser.setGroupNames(new String[] { "developers" });
        assertTrue(fessUserBean.hasGroups(new String[] { "developers", "testers" }));
        assertTrue(fessUserBean.hasGroups(new String[] { "managers", "developers" }));

        // Test with no matching groups
        assertFalse(fessUserBean.hasGroups(new String[] { "managers", "guests" }));

        // Test with multiple groups
        testUser.setGroupNames(new String[] { "developers", "testers", "managers" });
        assertTrue(fessUserBean.hasGroups(new String[] { "developers" }));
        assertTrue(fessUserBean.hasGroups(new String[] { "testers", "guests" }));
        assertTrue(fessUserBean.hasGroups(new String[] { "managers", "viewers" }));
        assertFalse(fessUserBean.hasGroups(new String[] { "guests", "viewers" }));

        // Test with empty accepted groups
        assertFalse(fessUserBean.hasGroups(new String[] {}));
    }

    public void test_getFessUser() {
        // Test that the same user object is returned
        assertSame(testUser, fessUserBean.getFessUser());

        // Test with different user
        TestFessUser anotherUser = new TestFessUser();
        anotherUser.setName("another");
        FessUserBean anotherBean = new FessUserBean(anotherUser);
        assertSame(anotherUser, anotherBean.getFessUser());
    }

    public void test_empty() {
        // Test empty user bean creation
        FessUserBean emptyBean = FessUserBean.empty();
        assertNotNull(emptyBean);

        // Test getUserId returns EMPTY_USER_ID
        assertEquals(Constants.EMPTY_USER_ID, emptyBean.getUserId());

        // Test hasRoles always returns true for empty user
        assertTrue(emptyBean.hasRoles(new String[] { "admin" }));
        assertTrue(emptyBean.hasRoles(new String[] { "user", "manager" }));
        assertTrue(emptyBean.hasRoles(new String[] {}));
        assertTrue(emptyBean.hasRoles(null));

        // Test getRoles returns empty array
        String[] roles = emptyBean.getRoles();
        assertNotNull(roles);
        assertEquals(0, roles.length);

        // Test isEditable returns false
        assertFalse(emptyBean.isEditable());

        // Test getFessUser returns null
        assertNull(emptyBean.getFessUser());

        // Test other methods still work with null user
        assertNull(emptyBean.getPermissions());
        assertNull(emptyBean.getGroups());

        // Test hasRole and hasGroup methods with null user
        assertFalse(emptyBean.hasRole("admin"));
        assertFalse(emptyBean.hasGroup("developers"));
        assertFalse(emptyBean.hasGroups(new String[] { "developers" }));
    }

    public void test_serialization() {
        // Test that FessUserBean is serializable
        assertTrue(fessUserBean instanceof java.io.Serializable);

        // Test empty user bean is also serializable
        FessUserBean emptyBean = FessUserBean.empty();
        assertTrue(emptyBean instanceof java.io.Serializable);
    }

    // Test implementation of FessUser interface for testing
    private static class TestFessUser implements FessUser {
        private static final long serialVersionUID = 1L;
        private String name;
        private String[] roleNames = new String[0];
        private String[] groupNames = new String[0];
        private String[] permissions = new String[0];
        private boolean editable = false;

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setRoleNames(String[] roleNames) {
            this.roleNames = roleNames;
        }

        @Override
        public String[] getRoleNames() {
            return roleNames;
        }

        public void setGroupNames(String[] groupNames) {
            this.groupNames = groupNames;
        }

        @Override
        public String[] getGroupNames() {
            return groupNames;
        }

        public void setPermissions(String[] permissions) {
            this.permissions = permissions;
        }

        @Override
        public String[] getPermissions() {
            return permissions;
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
        }

        @Override
        public boolean isEditable() {
            return editable;
        }
    }
}