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
package org.codelibs.fess.ldap;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.ActivityHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LdapUserTest extends UnitFessTestCase {

    private LdapUser ldapUser;
    private Hashtable<String, String> testEnv;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        testEnv = new Hashtable<>();
        testEnv.put("test.key", "test.value");
        ldapUser = new LdapUser(testEnv, "testuser");

        ComponentUtil.register(new SystemHelper(), "systemHelper");

        // Register a mock LdapManager to avoid NPE
        ComponentUtil.register(new LdapManager() {
            // Override methods that are called in tests
        }, "ldapManager");
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    @Test
    public void test_constructor() {
        // Test constructor initializes fields correctly
        LdapUser user = new LdapUser(testEnv, "username");
        assertEquals("username", user.getName());
        assertSame(testEnv, user.getEnvironment());
        assertNull(user.permissions);
    }

    @Test
    public void test_getName() {
        // Test getName returns the correct name
        assertEquals("testuser", ldapUser.getName());

        // Test with different names
        LdapUser user2 = new LdapUser(testEnv, "anotheruser");
        assertEquals("anotheruser", user2.getName());

        // Test with empty name
        LdapUser user3 = new LdapUser(testEnv, "");
        assertEquals("", user3.getName());

        // Test with null name
        LdapUser user4 = new LdapUser(testEnv, null);
        assertNull(user4.getName());
    }

    @Test
    public void test_getEnvironment() {
        // Test getEnvironment returns the correct environment
        Hashtable<String, String> env = ldapUser.getEnvironment();
        assertSame(testEnv, env);
        assertEquals("test.value", env.get("test.key"));

        // Test with empty environment
        Hashtable<String, String> emptyEnv = new Hashtable<>();
        LdapUser user = new LdapUser(emptyEnv, "user");
        assertTrue(user.getEnvironment().isEmpty());
    }

    @Test
    public void test_getPermissions_withoutLdapConfig() {
        // Test when baseDn or accountFilter is blank
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLdapBaseDn() {
                return "";
            }

            @Override
            public String getLdapAccountFilter() {
                return "";
            }
        });

        String[] permissions = ldapUser.getPermissions();
        assertNotNull(permissions);
        assertEquals(0, permissions.length);

        // Test that permissions are cached
        String[] permissions2 = ldapUser.getPermissions();
        assertSame(permissions, permissions2);
    }

    @Test
    public void test_getPermissions_withBaseDnOnly() {
        // Test when only baseDn is set
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLdapBaseDn() {
                return "dc=example,dc=com";
            }

            @Override
            public String getLdapAccountFilter() {
                return "";
            }
        });

        String[] permissions = ldapUser.getPermissions();
        assertNotNull(permissions);
        assertEquals(0, permissions.length);
    }

    @Test
    public void test_getPermissions_withAccountFilterOnly() {
        // Test when only accountFilter is set
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLdapBaseDn() {
                return "";
            }

            @Override
            public String getLdapAccountFilter() {
                return "(uid=%s)";
            }
        });

        String[] permissions = ldapUser.getPermissions();
        assertNotNull(permissions);
        assertEquals(0, permissions.length);
    }

    @Test
    public void test_getPermissions_withLdapConfig() {
        // Test with LDAP configuration
        final AtomicBoolean activityHelperCalled = new AtomicBoolean(false);
        final AtomicReference<String[]> rolesFromCallback = new AtomicReference<>();

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLdapBaseDn() {
                return "dc=example,dc=com";
            }

            @Override
            public String getLdapAccountFilter() {
                return "(uid=%s)";
            }

            @Override
            public String getLdapGroupFilter() {
                return "(memberOf=%s)";
            }

            @Override
            public String getRoleSearchUserPrefix() {
                return "U";
            }

            @Override

            public boolean isLdapRoleSearchUserEnabled() {

                return true;

            }
        });

        ComponentUtil.register(new LdapManager() {
            @Override
            public String[] getRoles(LdapUser user, String baseDn, String accountFilter, String groupFilter, Consumer<String[]> callback) {
                // Verify parameters
                assertSame(ldapUser, user);
                assertEquals("dc=example,dc=com", baseDn);
                assertEquals("(uid=%s)", accountFilter);
                assertEquals("(memberOf=%s)", groupFilter);

                // Test callback
                String[] callbackRoles = new String[] { "role1", "role2" };
                callback.accept(callbackRoles);
                rolesFromCallback.set(callbackRoles);

                // Return roles from LDAP
                return new String[] { "Rgroup1", "Rgroup2" };
            }

            @Override
            public String normalizePermissionName(String name) {
                return name.toLowerCase();
            }

            @Override
            public String getUserPermissionName(final LdapUser user) {
                // LdapUser takes the whole permission name from LdapManager instead of
                // joining the prefix to it, so the stub spells what it used to build.
                return "U" + normalizePermissionName(user.getName());
            }
        }, "ldapManager");

        ComponentUtil.register(new ActivityHelper() {
            @Override
            public void permissionChanged(OptionalThing<FessUserBean> userBean) {
                activityHelperCalled.set(true);
                assertTrue(userBean.isPresent());
                assertSame(ldapUser, userBean.get().getFessUser());
            }
        }, "activityHelper");

        String[] permissions = ldapUser.getPermissions();

        // The stub publishes through the callback before returning, which is what the nested-group
        // walk does when it wins the race with the synchronous assignment. The published set is
        // what survives; this used to assert the opposite -- the synchronous return value -- which
        // pinned the walk's result being thrown away.
        assertNotNull(permissions);
        assertEquals(3, permissions.length);
        final String rendered = java.util.Arrays.toString(permissions);
        assertTrue(rendered, rendered.contains("role1"));
        assertTrue(rendered, rendered.contains("role2"));
        assertTrue(rendered, rendered.contains("Utestuser"));

        // Verify callback was called
        assertTrue(activityHelperCalled.get());
        assertNotNull(rolesFromCallback.get());
        assertEquals(2, rolesFromCallback.get().length);

        // Test that permissions are cached
        String[] permissions2 = ldapUser.getPermissions();
        assertSame(permissions, permissions2);
    }

    @Test
    public void test_getPermissions_withDuplicates() {
        // Test duplicate removal
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLdapBaseDn() {
                return "dc=example,dc=com";
            }

            @Override
            public String getLdapAccountFilter() {
                return "(uid=%s)";
            }

            @Override
            public String getRoleSearchUserPrefix() {
                return "U";
            }

            @Override

            public boolean isLdapRoleSearchUserEnabled() {

                return true;

            }
        });

        ComponentUtil.register(new LdapManager() {
            @Override
            public String[] getRoles(LdapUser user, String baseDn, String accountFilter, String groupFilter, Consumer<String[]> callback) {
                // Return roles with duplicates
                String[] roles = new String[] { "role1", "role2", "role1", "Uuser", "role2" };
                callback.accept(roles);
                return roles;
            }

            @Override
            public String normalizePermissionName(String name) {
                return "user";
            }

            @Override
            public String getUserPermissionName(final LdapUser user) {
                // LdapUser takes the whole permission name from LdapManager instead of
                // joining the prefix to it, so the stub spells what it used to build.
                return "U" + normalizePermissionName(user.getName());
            }
        }, "ldapManager");

        ComponentUtil.register(new ActivityHelper() {
            @Override
            public void permissionChanged(OptionalThing<FessUserBean> userBean) {
                // Do nothing
            }
        }, "activityHelper");

        String[] permissions = ldapUser.getPermissions();

        // Verify duplicates are removed
        assertNotNull(permissions);
        assertEquals(3, permissions.length);
        assertEquals("role1", permissions[0]);
        assertEquals("role2", permissions[1]);
        assertEquals("Uuser", permissions[2]);
    }

    @Test
    public void test_getRoleNames() {
        // Test getting role names from permissions
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getRoleSearchRolePrefix() {
                return "R";
            }

            @Override
            public String getLdapBaseDn() {
                return "dc=example,dc=com";
            }

            @Override
            public String getLdapAccountFilter() {
                return "(uid=%s)";
            }

            @Override
            public String getRoleSearchUserPrefix() {
                return "U";
            }

            @Override

            public boolean isLdapRoleSearchUserEnabled() {

                return true;

            }
        });

        ComponentUtil.register(new LdapManager() {
            @Override
            public String[] getRoles(LdapUser user, String baseDn, String accountFilter, String groupFilter, Consumer<String[]> callback) {
                return new String[] { "Rrole1", "Rrole2", "Ggroup1", "Uuser1" };
            }

            @Override
            public String normalizePermissionName(String name) {
                return name;
            }

            @Override
            public String getUserPermissionName(final LdapUser user) {
                // LdapUser takes the whole permission name from LdapManager instead of
                // joining the prefix to it, so the stub spells what it used to build.
                return "U" + normalizePermissionName(user.getName());
            }
        }, "ldapManager");

        ComponentUtil.register(new ActivityHelper() {
            @Override
            public void permissionChanged(OptionalThing<FessUserBean> userBean) {
                // Do nothing
            }
        }, "activityHelper");

        String[] roleNames = ldapUser.getRoleNames();
        assertNotNull(roleNames);
        assertEquals(2, roleNames.length);
        assertEquals("role1", roleNames[0]);
        assertEquals("role2", roleNames[1]);
    }

    @Test
    public void test_getRoleNames_empty() {
        // Test when no roles match the prefix
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getRoleSearchRolePrefix() {
                return "R";
            }

            @Override
            public String getLdapBaseDn() {
                return "";
            }
        });

        String[] roleNames = ldapUser.getRoleNames();
        assertNotNull(roleNames);
        assertEquals(0, roleNames.length);
    }

    @Test
    public void test_getGroupNames() {
        // Test getting group names from permissions
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getRoleSearchGroupPrefix() {
                return "G";
            }

            @Override
            public String getLdapBaseDn() {
                return "dc=example,dc=com";
            }

            @Override
            public String getLdapAccountFilter() {
                return "(uid=%s)";
            }

            @Override
            public String getRoleSearchUserPrefix() {
                return "U";
            }

            @Override

            public boolean isLdapRoleSearchUserEnabled() {

                return true;

            }
        });

        ComponentUtil.register(new LdapManager() {
            @Override
            public String[] getRoles(LdapUser user, String baseDn, String accountFilter, String groupFilter, Consumer<String[]> callback) {
                return new String[] { "Rrole1", "Ggroup1", "Ggroup2", "Uuser1" };
            }

            @Override
            public String normalizePermissionName(String name) {
                return name;
            }

            @Override
            public String getUserPermissionName(final LdapUser user) {
                // LdapUser takes the whole permission name from LdapManager instead of
                // joining the prefix to it, so the stub spells what it used to build.
                return "U" + normalizePermissionName(user.getName());
            }
        }, "ldapManager");

        ComponentUtil.register(new ActivityHelper() {
            @Override
            public void permissionChanged(OptionalThing<FessUserBean> userBean) {
                // Do nothing
            }
        }, "activityHelper");

        String[] groupNames = ldapUser.getGroupNames();
        assertNotNull(groupNames);
        assertEquals(2, groupNames.length);
        assertEquals("group1", groupNames[0]);
        assertEquals("group2", groupNames[1]);
    }

    @Test
    public void test_getGroupNames_empty() {
        // Test when no groups match the prefix
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getRoleSearchGroupPrefix() {
                return "G";
            }

            @Override
            public String getLdapBaseDn() {
                return "";
            }
        });

        String[] groupNames = ldapUser.getGroupNames();
        assertNotNull(groupNames);
        assertEquals(0, groupNames.length);
    }

    @Test
    public void test_isEditable_enabled() {
        // Test when admin is enabled for the user
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapAdminEnabled(String name) {
                assertEquals("testuser", name);
                return true;
            }
        });

        assertTrue(ldapUser.isEditable());
    }

    @Test
    public void test_isEditable_disabled() {
        // Test when admin is disabled for the user
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapAdminEnabled(String name) {
                assertEquals("testuser", name);
                return false;
            }
        });

        assertFalse(ldapUser.isEditable());
    }

    @Test
    public void test_distinct_nullInput() {
        // Test distinct with null input
        String[] result = invokeDistinct(null);
        assertNotNull(result);
        assertEquals(0, result.length);
        assertSame(StringUtil.EMPTY_STRINGS, result);
    }

    @Test
    public void test_distinct_emptyArray() {
        // Test distinct with empty array
        String[] input = new String[0];
        String[] result = invokeDistinct(input);
        assertSame(input, result);
    }

    @Test
    public void test_distinct_singleElement() {
        // Test distinct with single element
        String[] input = new String[] { "element" };
        String[] result = invokeDistinct(input);
        assertSame(input, result);
    }

    @Test
    public void test_distinct_noDuplicates() {
        // Test distinct with no duplicates
        String[] input = new String[] { "a", "b", "c" };
        String[] result = invokeDistinct(input);
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
        assertEquals("c", result[2]);
    }

    @Test
    public void test_distinct_withDuplicates() {
        // Test distinct with duplicates
        String[] input = new String[] { "a", "b", "a", "c", "b", "a" };
        String[] result = invokeDistinct(input);
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
        assertEquals("c", result[2]);
    }

    @Test
    public void test_distinct_withNullElements() {
        // Test distinct with null elements
        String[] input = new String[] { "a", null, "b", null, "a" };
        String[] result = invokeDistinct(input);
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
        assertNull(result[1]);
        assertEquals("b", result[2]);
    }

    // Helper method to invoke the private distinct method
    private String[] invokeDistinct(String[] values) {
        try {
            java.lang.reflect.Method method = LdapUser.class.getDeclaredMethod("distinct", String[].class);
            method.setAccessible(true);
            return (String[]) method.invoke(null, (Object) values);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_getPermissionState_defaultsToResolved() {
        // Unlike an Entra ID user, an LdapUser resolves inline on first read and only schedules the
        // nested-group walk when ldap.group.filter is configured. With the shipped defaults nothing
        // is scheduled, so there is no window to report and the state must not start PENDING --
        // that would strand the notice on screen for every LDAP deployment.
        assertEquals(FessUser.PermissionState.RESOLVED, ldapUser.getPermissionState());
    }

    /**
     * {@code ldap.role.search.user.enabled} says whether a user is granted a permission named
     * after themselves.  Both writes have to honour it: the synchronous one and the one the
     * nested-group walk publishes.
     */
    @Test
    public void test_getPermissions_userPermissionFollowsTheSwitch() {
        final AtomicReference<String[]> publishedByCallback = new AtomicReference<>();

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLdapBaseDn() {
                return "dc=example,dc=com";
            }

            @Override
            public String getLdapAccountFilter() {
                return "(uid=%s)";
            }

            @Override
            public String getRoleSearchUserPrefix() {
                return "1";
            }

            @Override
            public boolean isLdapRoleSearchUserEnabled() {
                return false;
            }
        });

        ComponentUtil.register(new LdapManager() {
            @Override
            public String[] getRoles(final LdapUser user, final String baseDn, final String accountFilter, final String groupFilter,
                    final Consumer<String[]> callback) {
                // What LdapManager returns when the switch is off: groups only.
                callback.accept(new String[] { "2group1", "2parent1" });
                publishedByCallback.set(user.permissions);
                return new String[] { "2group1" };
            }

            @Override
            public String normalizePermissionName(final String name) {
                return name;
            }

            @Override
            public String getUserPermissionName(final LdapUser user) {
                // LdapUser takes the whole permission name from LdapManager instead of
                // joining the prefix to it, so the stub spells what it used to build.
                return "1" + normalizePermissionName(user.getName());
            }
        }, "ldapManager");

        ComponentUtil.register(new ActivityHelper() {
            @Override
            public void permissionChanged(final OptionalThing<FessUserBean> user) {
                // no-op
            }
        }, "activityHelper");

        final String[] permissions = ldapUser.getPermissions();
        assertFalse("switched off, but " + java.util.Arrays.toString(permissions) + " still names the user",
                java.util.Arrays.asList(permissions).contains("1testuser"));

        final String[] afterLazyWrite = publishedByCallback.get();
        assertNotNull(afterLazyWrite);
        assertFalse("the lazy write re-added it: " + java.util.Arrays.toString(afterLazyWrite),
                java.util.Arrays.asList(afterLazyWrite).contains("1testuser"));
    }

    @Test
    public void test_getPermissions_lazyWriteKeepsTheUserPermission() {
        // LdapManager derives its own user entry through getCanonicalLdapName, which drops a
        // NetBIOS "DOMAIN\\" prefix, and adds it only when ldap.role.search.user.enabled is set. So
        // the roles it hands the callback need not contain the user's own permission, and a lazy
        // write that published only those would hand back a strictly smaller set than the
        // synchronous pass -- the user losing their own permission at the exact moment the nested
        // group walk succeeded.
        final AtomicReference<String[]> publishedByCallback = new AtomicReference<>();

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLdapBaseDn() {
                return "dc=example,dc=com";
            }

            @Override
            public String getLdapAccountFilter() {
                return "(uid=%s)";
            }

            @Override
            public String getRoleSearchUserPrefix() {
                return "1";
            }

            @Override

            public boolean isLdapRoleSearchUserEnabled() {

                return true;

            }
        });

        ComponentUtil.register(new LdapManager() {
            @Override
            public String[] getRoles(final LdapUser user, final String baseDn, final String accountFilter, final String groupFilter,
                    final Consumer<String[]> callback) {
                // What the nested-group walk publishes: groups only, no user entry.
                callback.accept(new String[] { "2group1", "2parent1" });
                publishedByCallback.set(user.permissions);
                return new String[] { "2group1" };
            }

            @Override
            public String normalizePermissionName(final String name) {
                return name;
            }

            @Override
            public String getUserPermissionName(final LdapUser user) {
                // LdapUser takes the whole permission name from LdapManager instead of
                // joining the prefix to it, so the stub spells what it used to build.
                return "1" + normalizePermissionName(user.getName());
            }
        }, "ldapManager");

        ComponentUtil.register(new ActivityHelper() {
            @Override
            public void permissionChanged(final OptionalThing<FessUserBean> user) {
                // no-op
            }
        }, "activityHelper");

        ldapUser.getPermissions();

        final String[] afterLazyWrite = publishedByCallback.get();
        assertNotNull(afterLazyWrite);
        boolean hasUserPermission = false;
        for (final String permission : afterLazyWrite) {
            if ("1testuser".equals(permission)) {
                hasUserPermission = true;
            }
        }
        assertTrue("user permission missing from " + java.util.Arrays.toString(afterLazyWrite), hasUserPermission);
    }

    @Test
    public void test_getPermissionState_survivesADeserializedSessionWithoutTheField() {
        // LdapUser is Serializable and lives in the session, and serialVersionUID is unchanged, so a
        // session written by a release without this field deserializes with it null -- field
        // initializers do not run during deserialization. FessSearchAction#hookBefore switches on
        // this on every front page, and a bare switch on a null selector throws, so a null here
        // would be a 500 on every request until the session is dropped.
        ldapUser.permissionState = null;
        assertEquals(FessUser.PermissionState.RESOLVED, ldapUser.getPermissionState());
    }

    @Test
    public void test_getPermissions_doesNotOverwriteAWalkThatAlreadyPublished() {
        // getRoles schedules the nested-group walk and then returns, so assigning its return value
        // happens after the walk was handed to the timer. If the walk finished in between, assigning
        // would replace the fuller set with the direct-only one -- and the state would then report
        // RESOLVED over it. Here the stub publishes before returning, which is that interleaving.
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLdapBaseDn() {
                return "dc=example,dc=com";
            }

            @Override
            public String getLdapAccountFilter() {
                return "(uid=%s)";
            }

            @Override
            public String getRoleSearchUserPrefix() {
                return "1";
            }

            @Override

            public boolean isLdapRoleSearchUserEnabled() {

                return true;

            }
        });

        ComponentUtil.register(new LdapManager() {
            @Override
            public String[] getRoles(final LdapUser user, final String baseDn, final String accountFilter, final String groupFilter,
                    final Consumer<String[]> callback) {
                callback.accept(new String[] { "2group1", "2parent1" });
                return new String[] { "2group1" };
            }

            @Override
            public String normalizePermissionName(final String name) {
                return name;
            }

            @Override
            public String getUserPermissionName(final LdapUser user) {
                // LdapUser takes the whole permission name from LdapManager instead of
                // joining the prefix to it, so the stub spells what it used to build.
                return "1" + normalizePermissionName(user.getName());
            }
        }, "ldapManager");

        ComponentUtil.register(new ActivityHelper() {
            @Override
            public void permissionChanged(final OptionalThing<FessUserBean> user) {
                // no-op
            }
        }, "activityHelper");

        final String permissions = java.util.Arrays.toString(ldapUser.getPermissions());
        assertTrue("nested group lost from " + permissions, permissions.contains("2parent1"));
        assertTrue("user permission lost from " + permissions, permissions.contains("1testuser"));
    }
}
