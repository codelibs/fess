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
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.logging.log4j.Level;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.exception.LdapOperationException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.LogCapturingAppender;
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
        // A backslash inside a CN reaches this method escaped, the way a directory writes it into
        // a DN.  It must survive here: stripping it is getCanonicalLdapName's job, and only for a
        // name a user typed.
        assertEquals("aaa\\bbb", ldapManager.getSearchRoleName("cn=aaa\\\\bbb"));
        assertEquals("aaa\\bbb", ldapManager.getSearchRoleName("cn=aaa\\\\bbb,du=test"));
        assertEquals("aaa\\bbb\\ccc", ldapManager.getSearchRoleName("cn=aaa\\\\bbb\\\\ccc"));
        assertEquals("aaa\\bbb\\ccc", ldapManager.getSearchRoleName("cn=aaa\\\\bbb\\\\ccc,du=test"));

        assertNull(ldapManager.getSearchRoleName(null));
        assertNull(ldapManager.getSearchRoleName(""));
        assertNull(ldapManager.getSearchRoleName(" "));
        assertNull(ldapManager.getSearchRoleName("aaa"));
    }

    /**
     * A comma is legal inside a CN and a directory escapes it in the DN.  Reading the DN as text
     * and cutting at the first comma stops inside the name and hands back a prefix of it, so
     * "sales,EMEA" and "sales,APAC" both become the permission of the unrelated group "sales" --
     * and a group named after a privileged name gains that privilege.
     */
    @SuppressWarnings("serial")
    @Test
    public void test_getSearchRoleName_escapedComma() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            public boolean isLdapIgnoreNetbiosName() {
                return true;
            }

            public boolean isLdapGroupNameWithUnderscores() {
                return false;
            }
        });
        final LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        assertEquals("sales,EMEA", ldapManager.getSearchRoleName("CN=sales\\,EMEA,CN=Users,DC=example,DC=com"));
        assertEquals("sales,APAC", ldapManager.getSearchRoleName("CN=sales\\,APAC,CN=Users,DC=example,DC=com"));
        assertEquals("admin,decoy", ldapManager.getSearchRoleName("CN=admin\\,decoy,OU=Role,DC=example,DC=com"));
        // The hex form of the same escape has to read the same way.
        assertEquals("sales,EMEA", ldapManager.getSearchRoleName("CN=sales\\2CEMEA,CN=Users,DC=example,DC=com"));
        // Other escapes a CN can legally carry.
        assertEquals("a+b", ldapManager.getSearchRoleName("CN=a\\+b,CN=Users,DC=example,DC=com"));
        assertEquals(" pad ", ldapManager.getSearchRoleName("CN=\\ pad\\ ,CN=Users,DC=example,DC=com"));
    }

    /**
     * The CN nearest the leaf names the entry itself.  A DN whose leaf RDN is not a CN must not
     * report a container further up as the entry's name unless that container is the only CN.
     */
    @SuppressWarnings("serial")
    @Test
    public void test_getSearchRoleName_leafCommonName() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            public boolean isLdapIgnoreNetbiosName() {
                return true;
            }

            public boolean isLdapGroupNameWithUnderscores() {
                return false;
            }
        });
        final LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        assertEquals("leaf", ldapManager.getSearchRoleName("CN=leaf,CN=Users,DC=example,DC=com"));
        assertEquals("Users", ldapManager.getSearchRoleName("OU=sub,CN=Users,DC=example,DC=com"));
        // A multi-valued RDN must report its CN half, not whichever half sorts first.
        assertEquals("multi", ldapManager.getSearchRoleName("OU=other+CN=multi,DC=example,DC=com"));
        // A DN the provider cannot parse contributes no permission rather than a guessed one.
        assertNull(ldapManager.getSearchRoleName("CN=broken\\q,DC=example,DC=com"));
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

    // ==============================================================================
    //                                              Nested group resolution reporting
    //                                              ==================================

    /** Builds a search result carrying the given memberOf values. */
    private SearchResult memberOfResult(final String... entryDns) {
        final BasicAttributes attributes = new BasicAttributes();
        final BasicAttribute memberOf = new BasicAttribute("memberOf");
        for (final String entryDn : entryDns) {
            memberOf.add(entryDn);
        }
        attributes.put(memberOf);
        final SearchResult result = new SearchResult("cn=testuser", null, attributes);
        result.setNameInNamespace("cn=testuser,dc=example,dc=com");
        return result;
    }

    /** The configuration the nested-group tests share: a group DN yields a group-typed role. */
    private void registerGroupResolvingConfig() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getLdapMemberofAttribute() {
                return "memberOf";
            }

            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return true;
            }

            @Override
            public boolean isLdapGroupNameWithUnderscores() {
                return false;
            }

            @Override
            public boolean isLdapRoleSearchUserEnabled() {
                return false;
            }

            @Override
            public boolean isLdapRoleSearchGroupEnabled() {
                return true;
            }

            @Override
            public boolean isLdapRoleSearchRoleEnabled() {
                return true;
            }

            @Override
            public String getRoleSearchGroupPrefix() {
                return "2";
            }

            @Override
            public String getRoleSearchRolePrefix() {
                return "R";
            }

            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }

            @Override
            public boolean isLdapSamaccountnameGroup() {
                return false;
            }
        });
    }

    /** Builds a group search result whose name-in-namespace is the given DN. */
    private SearchResult groupResult(final String groupDn) {
        final SearchResult result = new SearchResult(groupDn, null, new BasicAttributes());
        result.setNameInNamespace(groupDn);
        return result;
    }

    @Test
    public void test_getRoles_leavesThePermissionsResolvedWhenNoWalkIsScheduled() {
        // The shipped default leaves ldap.group.filter blank, so subRoleSet stays empty and nothing
        // is ever scheduled. Reporting PENDING here would strand the notice on screen forever,
        // which is why LdapUser starts RESOLVED rather than PENDING the way EntraIdUser does.
        registerGroupResolvingConfig();
        final AtomicBoolean scheduled = new AtomicBoolean(false);
        final LdapManager ldapManager = new LdapManager() {
            @Override
            protected void search(final String baseDn, final String filter, final String[] returningAttrs,
                    final java.util.function.Supplier<Hashtable<String, String>> envSupplier, final SearchConsumer consumer)
                    throws RuntimeException {
                try {
                    consumer.accept(List.of(memberOfResult("CN=group1,OU=group,DC=example,DC=com")));
                } catch (final javax.naming.NamingException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            protected void scheduleSubRoleUpdate(final LdapUser user, final String bindDn, final java.util.Set<String> subRoleSet,
                    final String groupFilter, final java.util.Set<String> roleSet, final java.util.Set<String> sAMAccountGroupNameSet,
                    final java.util.function.Consumer<String[]> lazyLoading) {
                scheduled.set(true);
            }
        };
        ldapManager.fessConfig = ComponentUtil.getFessConfig();

        final LdapUser ldapUser = new LdapUser(new Hashtable<>(), "testuser");
        ldapManager.getRoles(ldapUser, "dc=example,dc=com", "(uid=%s)", "", roles -> {});

        assertFalse(scheduled.get());
        assertEquals(FessUser.PermissionState.RESOLVED, ldapUser.getPermissionState());
    }

    @Test
    public void test_getRoles_marksThePermissionsPendingWhenItSchedulesTheWalk() {
        // Set before the task is handed to the timer, not inside it: the timer only notices a
        // second later, and the user is being given their direct groups right now.
        registerGroupResolvingConfig();
        final AtomicBoolean scheduled = new AtomicBoolean(false);
        final LdapManager ldapManager = new LdapManager() {
            @Override
            protected void search(final String baseDn, final String filter, final String[] returningAttrs,
                    final java.util.function.Supplier<Hashtable<String, String>> envSupplier, final SearchConsumer consumer)
                    throws RuntimeException {
                try {
                    consumer.accept(List.of(memberOfResult("CN=group1,OU=group,DC=example,DC=com")));
                } catch (final javax.naming.NamingException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            protected void scheduleSubRoleUpdate(final LdapUser user, final String bindDn, final java.util.Set<String> subRoleSet,
                    final String groupFilter, final java.util.Set<String> roleSet, final java.util.Set<String> sAMAccountGroupNameSet,
                    final java.util.function.Consumer<String[]> lazyLoading) {
                // The state must already be PENDING by the time the task is scheduled.
                assertEquals(FessUser.PermissionState.PENDING, user.getPermissionState());
                scheduled.set(true);
            }
        };
        ldapManager.fessConfig = ComponentUtil.getFessConfig();

        final LdapUser ldapUser = new LdapUser(new Hashtable<>(), "testuser");
        ldapManager.getRoles(ldapUser, "dc=example,dc=com", "(uid=%s)", "(member=%s)", roles -> {});

        assertTrue(scheduled.get());
        assertEquals(FessUser.PermissionState.PENDING, ldapUser.getPermissionState());
    }

    @Test
    public void test_updateSubRoles_marksTheUserResolvedOnceTheWalkLands() {
        registerGroupResolvingConfig();
        final AtomicReference<String[]> published = new AtomicReference<>();
        final AtomicReference<FessUser.PermissionState> stateWhenPublished = new AtomicReference<>();
        final LdapManager ldapManager = new LdapManager() {
            @Override
            protected void processSubRoles(final LdapUser user, final String bindDn, final java.util.Set<String> subRoleSet,
                    final String groupFilter, final java.util.Set<String> roleSet) {
                roleSet.add("2parent");
            }

            @Override
            protected OptionalEntity<String> getSAMAccountGroupName(final String bindDn, final String groupName) {
                return OptionalEntity.of(groupName + "sam");
            }
        };
        ldapManager.fessConfig = ComponentUtil.getFessConfig();

        final LdapUser ldapUser = new LdapUser(new Hashtable<>(), "testuser");
        ldapUser.setPermissionState(FessUser.PermissionState.PENDING);
        final java.util.Set<String> roleSet = new java.util.HashSet<>(List.of("2group1"));

        ldapManager.updateSubRoles(ldapUser, "dc=example,dc=com", java.util.Set.of("CN=group1"), "(member=%s)", roleSet,
                java.util.Set.of("group1"), roles -> {
                    published.set(roles);
                    // The permissions must be published before the state that describes them, or a
                    // reader that sees RESOLVED can still be looking at the previous set.
                    stateWhenPublished.set(ldapUser.getPermissionState());
                });

        assertEquals(FessUser.PermissionState.RESOLVED, ldapUser.getPermissionState());
        assertEquals(FessUser.PermissionState.PENDING, stateWhenPublished.get());
        assertNotNull(published.get());
        // 2group1 from the direct pass, 2parent from the walk, and the sAMAccountName batch entry.
        assertEquals(3, published.get().length);
    }

    @Test
    public void test_updateSubRoles_doesNotDowngradeAStateAnotherWalkAlreadySettled() {
        // Two concurrent first requests each schedule a walk. If one succeeds and a later one fails,
        // the user still holds the groups the first published, so reporting FAILED over them would
        // show a failure notice on a complete permission set.
        registerGroupResolvingConfig();
        final LdapManager ldapManager = new LdapManager() {
            @Override
            protected void processSubRoles(final LdapUser user, final String bindDn, final java.util.Set<String> subRoleSet,
                    final String groupFilter, final java.util.Set<String> roleSet) {
                throw new org.codelibs.fess.exception.LdapOperationException("Failed to search.");
            }
        };
        ldapManager.fessConfig = ComponentUtil.getFessConfig();

        final LdapUser ldapUser = new LdapUser(new Hashtable<>(), "testuser");
        ldapUser.setPermissionState(FessUser.PermissionState.RESOLVED);

        ldapManager.updateSubRoles(ldapUser, "dc=example,dc=com", java.util.Set.of("CN=group1"), "(member=%s)",
                new java.util.HashSet<>(List.of("2group1")), java.util.Set.of(), roles -> {});

        assertEquals(FessUser.PermissionState.RESOLVED, ldapUser.getPermissionState());
    }

    @Test
    public void test_updateSubRoles_marksTheUserFailedWhenTheWalkThrows() {
        // Without the catch this throw escapes the TimeoutManager task, where corelib logs
        // "Failed to process a task." -- naming neither LDAP nor the user -- and lazyLoading is
        // never called, so the direct-only groups stand for the whole session with nothing saying
        // so. The permissions already published stay; what changes is that the user is told.
        registerGroupResolvingConfig();
        final AtomicBoolean publishedAnything = new AtomicBoolean(false);
        final LdapManager ldapManager = new LdapManager() {
            @Override
            protected void processSubRoles(final LdapUser user, final String bindDn, final java.util.Set<String> subRoleSet,
                    final String groupFilter, final java.util.Set<String> roleSet) {
                throw new org.codelibs.fess.exception.LdapOperationException("Failed to search.");
            }
        };
        ldapManager.fessConfig = ComponentUtil.getFessConfig();

        final LdapUser ldapUser = new LdapUser(new Hashtable<>(), "testuser");
        ldapUser.setPermissionState(FessUser.PermissionState.PENDING);

        ldapManager.updateSubRoles(ldapUser, "dc=example,dc=com", java.util.Set.of("CN=group1"), "(member=%s)",
                new java.util.HashSet<>(List.of("2group1")), java.util.Set.of(), roles -> publishedAnything.set(true));

        assertEquals(FessUser.PermissionState.FAILED, ldapUser.getPermissionState());
        assertFalse(publishedAnything.get());
    }

    @Test
    public void test_processSubRoles_looksUpSamAccountNamesAsTheAdminPrincipal() {
        // getSAMAccountGroupName asks for the admin credentials, but getDirContext discards that
        // request whenever a context is already open on the thread -- and inside search()'s consumer
        // one is, the one search() opened with the end user's own credentials. So the lookups must
        // not run from inside the consumer, or they bind as whoever is logging in. They now run
        // after search() has closed its context, under a single admin context opened for the batch.
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return true;
            }

            @Override
            public boolean isLdapGroupNameWithUnderscores() {
                return false;
            }

            @Override
            public boolean isLdapSamaccountnameGroup() {
                return true;
            }

            @Override
            public boolean isLdapRoleSearchGroupEnabled() {
                return true;
            }

            @Override
            public boolean isLdapRoleSearchRoleEnabled() {
                return true;
            }

            @Override
            public String getRoleSearchGroupPrefix() {
                return "2";
            }

            @Override
            public String getRoleSearchRolePrefix() {
                return "R";
            }

            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }
        });

        final Hashtable<String, String> userEnv = new Hashtable<>();
        userEnv.put("principal", "end-user");
        final Hashtable<String, String> adminEnv = new Hashtable<>();
        adminEnv.put("principal", "admin");

        final AtomicReference<String> envOfOpenContext = new AtomicReference<>();
        final AtomicReference<String> principalAtLookup = new AtomicReference<>();
        final AtomicInteger contextsOpened = new AtomicInteger();

        final LdapManager ldapManager = new LdapManager() {
            @Override
            protected Hashtable<String, String> createSearchEnv() {
                return adminEnv;
            }

            @Override
            protected DirContextHolder getDirContext(final java.util.function.Supplier<Hashtable<String, String>> envSupplier) {
                // Mirrors the real one: an already-open context on this thread is reused, and the
                // supplied environment is then never consulted.
                final DirContextHolder existing = contextLocal.get();
                if (existing != null) {
                    existing.inc();
                    return existing;
                }
                envOfOpenContext.set(envSupplier.get().get("principal"));
                contextsOpened.incrementAndGet();
                final DirContextHolder holder = new DirContextHolder(null);
                contextLocal.set(holder);
                return holder;
            }

            @Override
            protected void search(final String baseDn, final String filter, final String[] returningAttrs,
                    final java.util.function.Supplier<Hashtable<String, String>> envSupplier, final SearchConsumer consumer) {
                try (DirContextHolder holder = getDirContext(envSupplier)) {
                    consumer.accept(List.of(groupResult("CN=group1,OU=group,DC=example,DC=com"),
                            groupResult("CN=group2,OU=group,DC=example,DC=com")));
                } catch (final javax.naming.NamingException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            protected OptionalEntity<String> getSAMAccountGroupName(final String bindDn, final String groupName) {
                principalAtLookup.set(envOfOpenContext.get());
                return OptionalEntity.of(groupName + "sam");
            }
        };
        ldapManager.fessConfig = ComponentUtil.getFessConfig();

        final java.util.Set<String> roleSet = new java.util.HashSet<>();
        ldapManager.processSubRoles(new LdapUser(userEnv, "testuser"), "dc=example,dc=com", java.util.Set.of("CN=user1"), "(member=%s)",
                roleSet);

        // The lookups ran, and under the admin principal rather than the end user's.
        assertEquals("admin", principalAtLookup.get());
        // Two groups, but only two contexts in total: the search's, and one shared by the batch.
        assertEquals(2, contextsOpened.get());
        assertTrue(roleSet.toString(), roleSet.contains("2group1sam"));
        assertTrue(roleSet.toString(), roleSet.contains("2group2sam"));
    }

    @Test
    public void test_updateSubRoles_marksTheUserFailedWhenTheWalkThrowsAnError() {
        // The state write is in a finally, not only in the catch, so that a Throwable that is not an
        // Exception cannot strand the user in PENDING. corelib's own task handler also catches only
        // Exception, so nothing further downstream would settle it.
        registerGroupResolvingConfig();
        final LdapManager ldapManager = new LdapManager() {
            @Override
            protected void processSubRoles(final LdapUser user, final String bindDn, final java.util.Set<String> subRoleSet,
                    final String groupFilter, final java.util.Set<String> roleSet) {
                throw new StackOverflowError("walk blew the stack");
            }
        };
        ldapManager.fessConfig = ComponentUtil.getFessConfig();

        final LdapUser ldapUser = new LdapUser(new Hashtable<>(), "testuser");
        ldapUser.setPermissionState(FessUser.PermissionState.PENDING);

        try {
            ldapManager.updateSubRoles(ldapUser, "dc=example,dc=com", java.util.Set.of("CN=group1"), "(member=%s)",
                    new java.util.HashSet<>(List.of("2group1")), java.util.Set.of(), roles -> {});
            fail("the Error must still propagate");
        } catch (final StackOverflowError expected) {
            // propagating is correct; what matters is that the state was settled on the way out
        }

        assertEquals(FessUser.PermissionState.FAILED, ldapUser.getPermissionState());
    }

    // ========================================================================
    // Tests for Directory Timeouts
    // ========================================================================

    /**
     * Pins the shipped defaults. They are read with the raw keys rather than the generated accessors so this
     * assertion is about what {@code fess_config.properties} actually ships, not about the accessor wiring.
     */
    @Test
    public void test_ldapTimeoutDefaults() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        assertEquals("10000", fessConfig.get("ldap.connect.timeout"));
        assertEquals("30000", fessConfig.get("ldap.read.timeout"));
        assertEquals("60000", fessConfig.get("ldap.search.time.limit"));
    }

    @Test
    public void test_createEnvironment_appliesTimeouts() {
        ComponentUtil.setFessConfig(timeoutConfig(10000, 30000, 60000));
        final LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        final Hashtable<String, String> env =
                ldapManager.createEnvironment("com.sun.jndi.ldap.LdapCtxFactory", "simple", "ldap://localhost:389", "cn=admin", "secret");

        assertEquals("10000", env.get("com.sun.jndi.ldap.connect.timeout"));
        assertEquals("30000", env.get("com.sun.jndi.ldap.read.timeout"));
        // The pre-existing entries must survive.
        assertEquals("ldap://localhost:389", env.get(Context.PROVIDER_URL));
        assertEquals("cn=admin", env.get(Context.SECURITY_PRINCIPAL));
        assertEquals("secret", env.get(Context.SECURITY_CREDENTIALS));
    }

    @Test
    public void test_createEnvironment_omitsNonPositiveTimeouts() {
        ComponentUtil.setFessConfig(timeoutConfig(0, 1500, 60000));
        final LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        Hashtable<String, String> env =
                ldapManager.createEnvironment("com.sun.jndi.ldap.LdapCtxFactory", "simple", "ldap://localhost:389", "cn=admin", "secret");

        // 0 means "leave it to the JDK/OS default", so the property must not be present at all.
        assertNull(env.get("com.sun.jndi.ldap.connect.timeout"));
        assertEquals("1500", env.get("com.sun.jndi.ldap.read.timeout"));

        ComponentUtil.setFessConfig(timeoutConfig(1500, -1, 60000));
        ldapManager.init();
        env = ldapManager.createEnvironment("com.sun.jndi.ldap.LdapCtxFactory", "simple", "ldap://localhost:389", "cn=admin", "secret");

        assertEquals("1500", env.get("com.sun.jndi.ldap.connect.timeout"));
        assertNull(env.get("com.sun.jndi.ldap.read.timeout"));
    }

    @Test
    public void test_search_appliesSearchTimeLimit() throws Exception {
        ComponentUtil.setFessConfig(timeoutConfig(10000, 30000, 45000));
        final CapturingDirContext context = new CapturingDirContext();
        final CapturingLdapManager ldapManager = new CapturingLdapManager(context);
        ldapManager.init();

        final String[] returningAttrs = { "memberOf" };
        ldapManager.search("dc=example,dc=com", "(cn=test)", returningAttrs, Hashtable::new, result -> {});

        assertEquals("dc=example,dc=com", context.capturedName);
        assertEquals("(cn=test)", context.capturedFilter);
        assertEquals(45000, context.capturedControls.getTimeLimit());
        assertEquals(SearchControls.SUBTREE_SCOPE, context.capturedControls.getSearchScope());
        assertEquals(1, context.capturedControls.getReturningAttributes().length);
        assertEquals("memberOf", context.capturedControls.getReturningAttributes()[0]);
    }

    @Test
    public void test_search_nonPositiveTimeLimitMeansNoLimit() throws Exception {
        final CapturingDirContext context = new CapturingDirContext();
        final CapturingLdapManager ldapManager = new CapturingLdapManager(context);

        // 0 is SearchControls' own contract for "wait indefinitely", and a negative value must not be
        // forwarded to SearchControls either.
        for (final int timeLimit : new int[] { 0, -1 }) {
            ComponentUtil.setFessConfig(timeoutConfig(10000, 30000, timeLimit));
            ldapManager.init();
            ldapManager.search("dc=example,dc=com", "(cn=test)", null, Hashtable::new, result -> {});
            assertEquals(0, context.capturedControls.getTimeLimit());
        }

        ComponentUtil.setFessConfig(timeoutConfig(10000, 30000, 5000));
        ldapManager.init();
        ldapManager.search("dc=example,dc=com", "(cn=test)", null, Hashtable::new, result -> {});
        assertEquals(5000, context.capturedControls.getTimeLimit());
    }

    @Test
    public void test_getSAMAccountGroupName_appliesSearchTimeLimit() throws Exception {
        ComponentUtil.setFessConfig(timeoutConfig(10000, 30000, 45000));
        final CapturingDirContext context = new CapturingDirContext();
        // getSAMAccountGroupName runs context.search directly instead of going through search().
        final CapturingLdapManager ldapManager = new CapturingLdapManager(context) {
            @Override
            protected Hashtable<String, String> createSearchEnv() {
                return new Hashtable<>();
            }
        };
        ldapManager.init();

        ldapManager.getSAMAccountGroupName("dc=example,dc=com", "testGroup");

        assertEquals("dc=example,dc=com", context.capturedName);
        assertEquals("(name=testGroup)", context.capturedFilter);
        assertEquals(45000, context.capturedControls.getTimeLimit());
        assertEquals(SearchControls.SUBTREE_SCOPE, context.capturedControls.getSearchScope());
    }

    @Test
    public void test_search_warnsOnlyWhenTheDirectoryDoesNotAnswer() throws Exception {
        ComponentUtil.setFessConfig(timeoutConfig(10000, 30000, 45000));
        final CapturingDirContext context = new CapturingDirContext();
        final CapturingLdapManager ldapManager = new CapturingLdapManager(context);
        ldapManager.init();

        final LogCapturingAppender appender = LogCapturingAppender.attach(LdapManager.class);
        try {
            // A read timeout reaches JNDI callers as a CommunicationException.
            context.searchException = new CommunicationException("LDAP response read timed out, timeout used: 30000 ms.");
            try {
                ldapManager.search("dc=example,dc=com", "(cn=test)", null, Hashtable::new, result -> {});
                fail("LdapOperationException should be thrown.");
            } catch (final LdapOperationException e) {
                assertTrue(e.getMessage().contains("dc=example,dc=com"));
            }

            final List<String> warnings = appender.warnings();
            assertEquals(1, warnings.size());
            assertTrue(warnings.get(0).contains("dc=example,dc=com"));
            assertTrue(warnings.get(0).contains("(cn=test)"));
            assertTrue(warnings.get(0).contains("30000"));
            assertTrue(warnings.get(0).contains("45000"));

            // An ordinary directory error is not a timeout and must not be promoted to WARN.
            context.searchException = new NameNotFoundException("dc=example,dc=com");
            try {
                ldapManager.search("dc=example,dc=com", "(cn=test)", null, Hashtable::new, result -> {});
                fail("LdapOperationException should be thrown.");
            } catch (final LdapOperationException e) {
                // expected
            }
            assertEquals(1, appender.warnings().size());
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_getDirContext_failureNamesTheTarget() {
        ComponentUtil.setFessConfig(timeoutConfig(10000, 30000, 45000));
        final LdapManager ldapManager = new LdapManager();
        ldapManager.init();

        final Hashtable<String, String> env = new Hashtable<>();
        // An unresolvable factory fails inside InitialDirContext without touching the network.
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.codelibs.fess.ldap.NoSuchLdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://ldap.example.com:389");

        try {
            ldapManager.getDirContext(() -> env);
            fail("LdapOperationException should be thrown.");
        } catch (final LdapOperationException e) {
            // validate() reports this message at WARN, so it has to say which directory and which bound.
            assertTrue(e.getMessage().contains("ldap://ldap.example.com:389"), e.getMessage());
            assertTrue(e.getMessage().contains("connectTimeout=10000ms"), e.getMessage());
        }
    }

    private static FessConfig timeoutConfig(final int connectTimeout, final int readTimeout, final int searchTimeLimit) {
        return new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getLdapConnectTimeoutAsInteger() {
                return Integer.valueOf(connectTimeout);
            }

            @Override
            public Integer getLdapReadTimeoutAsInteger() {
                return Integer.valueOf(readTimeout);
            }

            @Override
            public Integer getLdapSearchTimeLimitAsInteger() {
                return Integer.valueOf(searchTimeLimit);
            }
        };
    }

    /** An {@link LdapManager} whose directory context is a {@link CapturingDirContext}. */
    private static class CapturingLdapManager extends LdapManager {
        private final CapturingDirContext context;

        CapturingLdapManager(final CapturingDirContext context) {
            this.context = context;
        }

        @Override
        protected DirContextHolder getDirContext(final Supplier<Hashtable<String, String>> envSupplier) {
            return new DirContextHolder(context);
        }
    }

    /** A directory context that records the {@link SearchControls} it is handed. */
    private static class CapturingDirContext extends InitialDirContext {
        String capturedName;

        String capturedFilter;

        SearchControls capturedControls;

        NamingException searchException;

        CapturingDirContext() throws NamingException {
            super(true); // lazy: do not resolve an initial context
        }

        @Override
        public NamingEnumeration<SearchResult> search(final String name, final String filter, final SearchControls cons)
                throws NamingException {
            capturedName = name;
            capturedFilter = filter;
            capturedControls = cons;
            if (searchException != null) {
                throw searchException;
            }
            return new EmptySearchResults();
        }

        @Override
        public void close() {
            // nothing to release
        }
    }

    /** An empty result enumeration, enough for {@link java.util.Collections#list}. */
    private static class EmptySearchResults implements NamingEnumeration<SearchResult> {
        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public SearchResult nextElement() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean hasMore() {
            return false;
        }

        @Override
        public SearchResult next() {
            throw new NoSuchElementException();
        }

        @Override
        public void close() {
            // nothing to release
        }
    }
}
