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
package org.codelibs.fess.helper;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class PermissionHelperTest extends UnitFessTestCase {

    public PermissionHelper permissionHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        permissionHelper = new PermissionHelper();
        permissionHelper.systemHelper = new SystemHelper();
    }

    public void test_encode() {
        assertNull(permissionHelper.encode(null));
        assertNull(permissionHelper.encode(""));
        assertNull(permissionHelper.encode(" "));
        assertNull(permissionHelper.encode("{user}"));
        assertNull(permissionHelper.encode("{role}"));
        assertNull(permissionHelper.encode("{group}"));
        assertNull(permissionHelper.encode("(allow)"));
        assertNull(permissionHelper.encode("(allow){user}"));
        assertNull(permissionHelper.encode("(allow){group}"));
        assertNull(permissionHelper.encode("(allow){group}"));
        assertNull(permissionHelper.encode("(deny)"));
        assertNull(permissionHelper.encode("(deny){user}"));
        assertNull(permissionHelper.encode("(deny){group}"));
        assertNull(permissionHelper.encode("(deny){group}"));

        assertEquals("1guest", permissionHelper.encode("{user}guest"));
        assertEquals("Rguest", permissionHelper.encode("{role}guest"));
        assertEquals("2guest", permissionHelper.encode("{group}guest"));
        assertEquals("1guest", permissionHelper.encode("{USER}guest"));
        assertEquals("Rguest", permissionHelper.encode("{ROLE}guest"));
        assertEquals("2guest", permissionHelper.encode("{GROUP}guest"));
        assertEquals("1guest", permissionHelper.encode("(allow){user}guest"));
        assertEquals("Rguest", permissionHelper.encode("(allow){role}guest"));
        assertEquals("2guest", permissionHelper.encode("(allow){group}guest"));
        assertEquals("1guest", permissionHelper.encode("(allow){USER}guest"));
        assertEquals("Rguest", permissionHelper.encode("(allow){ROLE}guest"));
        assertEquals("2guest", permissionHelper.encode("(allow){GROUP}guest"));
        assertEquals("D1guest", permissionHelper.encode("(deny){user}guest"));
        assertEquals("DRguest", permissionHelper.encode("(deny){role}guest"));
        assertEquals("D2guest", permissionHelper.encode("(deny){group}guest"));
        assertEquals("D1guest", permissionHelper.encode("(deny){USER}guest"));
        assertEquals("DRguest", permissionHelper.encode("(deny){ROLE}guest"));
        assertEquals("D2guest", permissionHelper.encode("(deny){GROUP}guest"));

        assertEquals("guest", permissionHelper.encode("guest"));

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getRoleSearchUserPrefix() {
                return fessConfig.getRoleSearchUserPrefix();
            }

            @Override
            public String getRoleSearchGroupPrefix() {
                return fessConfig.getRoleSearchGroupPrefix();
            }

            @Override
            public String getRoleSearchRolePrefix() {
                return "";
            }

            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return true;
            }
        });
        try {
            assertEquals("guest", permissionHelper.encode("{role}guest"));
            assertEquals("guest", permissionHelper.encode("guest"));
        } finally {
            ComponentUtil.setFessConfig(null);
        }
    }

    public void test_decode() {
        assertNull(permissionHelper.decode(null));
        assertNull(permissionHelper.decode(""));
        assertNull(permissionHelper.decode(" "));
        assertNull(permissionHelper.decode("D"));

        assertEquals("{user}guest", permissionHelper.decode("1guest"));
        assertEquals("{role}guest", permissionHelper.decode("Rguest"));
        assertEquals("{group}guest", permissionHelper.decode("2guest"));
        assertEquals("(deny){user}guest", permissionHelper.decode("D1guest"));
        assertEquals("(deny){role}guest", permissionHelper.decode("DRguest"));
        assertEquals("(deny){group}guest", permissionHelper.decode("D2guest"));

        assertEquals("guest", permissionHelper.decode("guest"));

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getRoleSearchUserPrefix() {
                return fessConfig.getRoleSearchUserPrefix();
            }

            @Override
            public String getRoleSearchGroupPrefix() {
                return fessConfig.getRoleSearchGroupPrefix();
            }

            @Override
            public String getRoleSearchRolePrefix() {
                return "";
            }

            @Override
            public String getRoleSearchDeniedPrefix() {
                return "D";
            }
        });
        try {
            assertEquals("{role}guest", permissionHelper.decode("guest"));
        } finally {
            ComponentUtil.setFessConfig(null);
        }
    }

}
