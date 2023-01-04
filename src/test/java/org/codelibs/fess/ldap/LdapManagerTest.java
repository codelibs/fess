/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class LdapManagerTest extends UnitFessTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ComponentUtil.register(new SystemHelper(), "systemHelper");
    }

    @SuppressWarnings("serial")
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
}
