/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class LdapManagerTest extends UnitFessTestCase {

    @SuppressWarnings("serial")
    public void test_getSearchRoleName() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            public boolean isLdapIgnoreNetbiosName() {
                return true;
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

}
