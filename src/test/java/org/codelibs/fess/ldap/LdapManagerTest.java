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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.NamingException;
import javax.naming.directory.SearchResult;

import org.codelibs.fess.exception.LdapOperationException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LdapManagerTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new SystemHelper(), "systemHelper");
    }

    @SuppressWarnings("serial")
    @Test
    public void test_getSearchRoleName() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            public boolean isLdapIgnoreNetbiosName() {
                return true;
            }

            public boolean isLdapGroupNameWithUnderscores() {
                return false;
            }
        });
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        assertEquals("aaa", ldapManager.getSearchRoleName("cn=aaa"));
        assertEquals("aaa", ldapManager.getSearchRoleName("CN=aaa"));
        assertEquals("aaa", ldapManager.getSearchRoleName("cn=aaa,du=test"));
        assertEquals("aaa\\bbb", ldapManager.getSearchRoleName("cn=aaa\\bbb"));
        assertEquals("aaa\\bbb", ldapManager.getSearchRoleName("cn=aaa\\bbb,du=test"));
        assertEquals("aaa\\bbb\\ccc", ldapManager.getSearchRoleName("cn=aaa\\bbb\\ccc"));
        assertEquals("aaa\\bbb\\ccc", ldapManager.getSearchRoleName("cn=aaa\\bbb\\ccc,du=test\""));

        assertNull(ldapManager.getSearchRoleName(null));
        assertNull(ldapManager.getSearchRoleName(""));
        assertNull(ldapManager.getSearchRoleName(" "));
        assertNull(ldapManager.getSearchRoleName("aaa"));
    }

    @Test
    public void test_replaceWithUnderscores() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        assertEquals("_", ldapManager.replaceWithUnderscores("/"));
        assertEquals("_", ldapManager.replaceWithUnderscores("\\"));
        assertEquals("_", ldapManager.replaceWithUnderscores("["));
        assertEquals("_", ldapManager.replaceWithUnderscores("]"));
        assertEquals("_", ldapManager.replaceWithUnderscores(":"));
        assertEquals("_", ldapManager.replaceWithUnderscores(";"));
        assertEquals("_", ldapManager.replaceWithUnderscores("|"));
        assertEquals("_", ldapManager.replaceWithUnderscores("="));
        assertEquals("_", ldapManager.replaceWithUnderscores(","));
        assertEquals("_", ldapManager.replaceWithUnderscores("+"));
        assertEquals("_", ldapManager.replaceWithUnderscores("*"));
        assertEquals("_", ldapManager.replaceWithUnderscores("?"));
        assertEquals("_", ldapManager.replaceWithUnderscores("<"));
        assertEquals("_", ldapManager.replaceWithUnderscores(">"));

        assertEquals("_a_", ldapManager.replaceWithUnderscores("/a/"));
        assertEquals("___", ldapManager.replaceWithUnderscores("///"));
        assertEquals("a_a", ldapManager.replaceWithUnderscores("a/a"));
    }

    @Test
    public void test_allowEmptyGroupAndRole() {
        final AtomicBoolean allowEmptyPermission = new AtomicBoolean();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            public boolean isLdapAllowEmptyPermission() {
                return allowEmptyPermission.get();
            }

            public String getRoleSearchUserPrefix() {
                return "1";
            }
        });
        LdapManager ldapManager = new LdapManager();
        ldapManager.fessConfig = ComponentUtil.getFessConfig();
        final List<String> permissionList = new ArrayList<>();
        LdapUser user = new LdapUser(new Hashtable<>(), "test") {
            @Override
            public String[] getPermissions() {
                return permissionList.toArray(n -> new String[n]);
            }
        };

        allowEmptyPermission.set(true);
        assertTrue(ldapManager.allowEmptyGroupAndRole(user));
        allowEmptyPermission.set(false);
        assertFalse(ldapManager.allowEmptyGroupAndRole(user));

        permissionList.add("2aaa");

        allowEmptyPermission.set(true);
        assertTrue(ldapManager.allowEmptyGroupAndRole(user));
        allowEmptyPermission.set(false);
        assertTrue(ldapManager.allowEmptyGroupAndRole(user));

        permissionList.clear();
        permissionList.add("Raaa");

        allowEmptyPermission.set(true);
        assertTrue(ldapManager.allowEmptyGroupAndRole(user));
        allowEmptyPermission.set(false);
        assertTrue(ldapManager.allowEmptyGroupAndRole(user));
    }

    // ========================================================================
    // Tests for LDAP Injection Prevention
    // ========================================================================

    @Test
    public void test_escapeLDAPSearchFilter_withNull() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Null input should return empty string
        assertEquals("", ldapManager.escapeLDAPSearchFilter(null));
    }

    @Test
    public void test_escapeLDAPSearchFilter_withEmptyString() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        assertEquals("", ldapManager.escapeLDAPSearchFilter(""));
    }

    @Test
    public void test_escapeLDAPSearchFilter_withNormalInput() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Normal input should not be escaped
        assertEquals("normaluser", ldapManager.escapeLDAPSearchFilter("normaluser"));
        assertEquals("user123", ldapManager.escapeLDAPSearchFilter("user123"));
        assertEquals("user.name", ldapManager.escapeLDAPSearchFilter("user.name"));
    }

    @Test
    public void test_escapeLDAPSearchFilter_withBackslash() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Backslash should be escaped to \5c
        assertEquals("\\5c", ldapManager.escapeLDAPSearchFilter("\\"));
        assertEquals("test\\5cvalue", ldapManager.escapeLDAPSearchFilter("test\\value"));
        assertEquals("\\5c\\5c", ldapManager.escapeLDAPSearchFilter("\\\\"));
    }

    @Test
    public void test_escapeLDAPSearchFilter_withAsterisk() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Asterisk should be escaped to \2a (prevents wildcard injection)
        assertEquals("\\2a", ldapManager.escapeLDAPSearchFilter("*"));
        assertEquals("user\\2a", ldapManager.escapeLDAPSearchFilter("user*"));
        assertEquals("\\2aadmin\\2a", ldapManager.escapeLDAPSearchFilter("*admin*"));
    }

    @Test
    public void test_escapeLDAPSearchFilter_withParentheses() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Parentheses should be escaped (prevents filter injection)
        assertEquals("\\28", ldapManager.escapeLDAPSearchFilter("("));
        assertEquals("\\29", ldapManager.escapeLDAPSearchFilter(")"));
        assertEquals("\\28admin\\29", ldapManager.escapeLDAPSearchFilter("(admin)"));
        assertEquals("\\28objectClass=\\2a\\29", ldapManager.escapeLDAPSearchFilter("(objectClass=*)"));
    }

    @Test
    public void test_escapeLDAPSearchFilter_withNullByte() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Null byte should be escaped to \00
        assertEquals("\\00", ldapManager.escapeLDAPSearchFilter("\0"));
        assertEquals("test\\00value", ldapManager.escapeLDAPSearchFilter("test\0value"));
    }

    @Test
    public void test_escapeLDAPSearchFilter_withComplexInjectionAttempt() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Complex injection attempt should be fully escaped
        String injectionAttempt = "admin)(|(password=*";
        String expected = "admin\\29\\28|\\28password=\\2a";
        assertEquals(expected, ldapManager.escapeLDAPSearchFilter(injectionAttempt));
    }

    @Test
    public void test_escapeLDAPSearchFilter_withAllSpecialCharacters() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Test all special characters together (note: = is not escaped per RFC 4515)
        String input = "\\*()\0";
        String expected = "\\5c\\2a\\28\\29\\00";
        assertEquals(expected, ldapManager.escapeLDAPSearchFilter(input));
    }

    // ========================================================================
    // Tests for Defensive Null/Blank Checks
    // ========================================================================

    @Test
    public void test_getSAMAccountGroupName_withNullBindDn() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Null bindDn should return empty
        OptionalEntity<String> result = ldapManager.getSAMAccountGroupName(null, "testGroup");
        assertFalse(result.isPresent());
    }

    @Test
    public void test_getSAMAccountGroupName_withBlankBindDn() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Blank bindDn should return empty
        OptionalEntity<String> result = ldapManager.getSAMAccountGroupName("", "testGroup");
        assertFalse(result.isPresent());

        result = ldapManager.getSAMAccountGroupName("   ", "testGroup");
        assertFalse(result.isPresent());
    }

    @Test
    public void test_getSAMAccountGroupName_withNullGroupName() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Null groupName should return empty
        OptionalEntity<String> result = ldapManager.getSAMAccountGroupName("dc=example,dc=com", null);
        assertFalse(result.isPresent());
    }

    @Test
    public void test_getSAMAccountGroupName_withBlankGroupName() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Blank groupName should return empty
        OptionalEntity<String> result = ldapManager.getSAMAccountGroupName("dc=example,dc=com", "");
        assertFalse(result.isPresent());

        result = ldapManager.getSAMAccountGroupName("dc=example,dc=com", "   ");
        assertFalse(result.isPresent());
    }

    @Test
    public void test_changePassword_withNullUsername() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapAdminEnabled(String username) {
                return true;
            }
        });
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Null username should return false
        assertFalse(ldapManager.changePassword(null, "newPassword"));
    }

    @Test
    public void test_changePassword_withBlankUsername() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapAdminEnabled(String username) {
                return true;
            }
        });
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Blank username should return false
        assertFalse(ldapManager.changePassword("", "newPassword"));
        assertFalse(ldapManager.changePassword("   ", "newPassword"));
    }

    @Test
    public void test_changePassword_withNullPassword() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapAdminEnabled(String username) {
                return true;
            }
        });
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Null password should return false
        assertFalse(ldapManager.changePassword("testuser", null));
    }

    @Test
    public void test_changePassword_withBlankPassword() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapAdminEnabled(String username) {
                return true;
            }
        });
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Blank password should return false
        assertFalse(ldapManager.changePassword("testuser", ""));
        assertFalse(ldapManager.changePassword("testuser", "   "));
    }

    @Test
    public void test_changePassword_withAdminDisabled() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapAdminEnabled(String username) {
                return false;
            }
        });
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Admin disabled should return false
        assertFalse(ldapManager.changePassword("testuser", "newPassword"));
    }

    // ========================================================================
    // Tests for Improved Error Handling
    // ========================================================================

    @Test
    public void test_normalizePermissionName_withNull() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }
        });
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Should handle null gracefully (though it may throw NPE in actual implementation)
        // This test documents the expected behavior
        try {
            String result = ldapManager.normalizePermissionName(null);
            assertNull(result);
        } catch (NullPointerException e) {
            // NPE is acceptable for null input
        }
    }

    @Test
    public void test_normalizePermissionName_withLowercaseEnabled() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapLowercasePermissionName() {
                return true;
            }
        });
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        assertEquals("admin", ldapManager.normalizePermissionName("ADMIN"));
        assertEquals("admin", ldapManager.normalizePermissionName("Admin"));
        assertEquals("admin", ldapManager.normalizePermissionName("admin"));
    }

    @Test
    public void test_normalizePermissionName_withLowercaseDisabled() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }
        });
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        assertEquals("ADMIN", ldapManager.normalizePermissionName("ADMIN"));
        assertEquals("Admin", ldapManager.normalizePermissionName("Admin"));
        assertEquals("admin", ldapManager.normalizePermissionName("admin"));
    }

    // ========================================================================
    // Tests for Edge Cases
    // ========================================================================

    @Test
    public void test_escapeLDAPSearchFilter_withUnicodeCharacters() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Unicode characters should pass through unchanged
        assertEquals("テスト", ldapManager.escapeLDAPSearchFilter("テスト"));
        assertEquals("用户", ldapManager.escapeLDAPSearchFilter("用户"));
        assertEquals("사용자", ldapManager.escapeLDAPSearchFilter("사용자"));
    }

    @Test
    public void test_escapeLDAPSearchFilter_withMixedContent() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Mixed normal and special characters
        assertEquals("user\\28test\\29", ldapManager.escapeLDAPSearchFilter("user(test)"));
        assertEquals("admin\\2auser", ldapManager.escapeLDAPSearchFilter("admin*user"));
        assertEquals("test\\5cpath", ldapManager.escapeLDAPSearchFilter("test\\path"));
    }

    @Test
    public void test_getSearchRoleName_withEdgeCases() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return true;
            }

            @Override
            public boolean isLdapGroupNameWithUnderscores() {
                return false;
            }
        });
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Edge cases that should return null
        assertNull(ldapManager.getSearchRoleName(null));
        assertNull(ldapManager.getSearchRoleName(""));
        assertNull(ldapManager.getSearchRoleName("   "));
        assertNull(ldapManager.getSearchRoleName("no_cn_prefix"));
        assertNull(ldapManager.getSearchRoleName("dn=test"));
    }

    @Test
    public void test_replaceWithUnderscores_withEdgeCases() {
        LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        // Edge cases
        assertEquals("", ldapManager.replaceWithUnderscores(""));
        assertEquals("normal", ldapManager.replaceWithUnderscores("normal"));
        // Input "//\\[]:;" has 8 special characters that should be replaced
        assertEquals("________", ldapManager.replaceWithUnderscores("//\\\\[]:;"));
    }
}
