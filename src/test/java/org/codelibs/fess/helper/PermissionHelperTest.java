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
package org.codelibs.fess.helper;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class PermissionHelperTest extends UnitFessTestCase {

    public PermissionHelper permissionHelper;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        permissionHelper = new PermissionHelper();
        permissionHelper.systemHelper = new SystemHelper();
    }

    @Test
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
            ComponentUtil.setFessConfig(fessConfig);
        }
    }

    @Test
    public void test_decode() {
        assertNull(permissionHelper.decode(null));
        assertNull(permissionHelper.decode(""));
        assertNull(permissionHelper.decode(" "));

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
            ComponentUtil.setFessConfig(fessConfig);
        }
    }

    @Test
    public void test_encode_withComplexValues() {
        assertEquals("1user@domain.com", permissionHelper.encode("{user}user@domain.com"));
        assertEquals("2group/subgroup", permissionHelper.encode("{group}group/subgroup"));
        assertEquals("RAdministrators", permissionHelper.encode("{role}Administrators"));
        assertEquals("D1user with spaces", permissionHelper.encode("(deny){user}user with spaces"));
        assertEquals("DR_role_with_underscores", permissionHelper.encode("(deny){role}_role_with_underscores"));
        assertEquals("D2group123", permissionHelper.encode("(deny){group}group123"));
    }

    @Test
    public void test_encode_withMixedCasePrefixes() {
        assertEquals("1guest", permissionHelper.encode("(Allow){User}guest"));
        assertEquals("Rguest", permissionHelper.encode("(ALLOW){ROLE}guest"));
        assertEquals("2guest", permissionHelper.encode("(allow){Group}guest"));
        assertEquals("D1guest", permissionHelper.encode("(DENY){user}guest"));
        assertEquals("DRguest", permissionHelper.encode("(Deny){Role}guest"));
        assertEquals("D2guest", permissionHelper.encode("(deny){GROUP}guest"));
    }

    @Test
    public void test_encode_withSpecialCharacters() {
        assertEquals("1user@domain.com", permissionHelper.encode("{user}user@domain.com"));
        assertEquals("2group-name", permissionHelper.encode("{group}group-name"));
        assertEquals("Rrole.name", permissionHelper.encode("{role}role.name"));
        assertEquals("1user_123", permissionHelper.encode("{user}user_123"));
        assertEquals("2group$special", permissionHelper.encode("{group}group$special"));
    }

    @Test
    public void test_decode_withComplexValues() {
        assertEquals("{user}user@domain.com", permissionHelper.decode("1user@domain.com"));
        assertEquals("{group}group/subgroup", permissionHelper.decode("2group/subgroup"));
        assertEquals("{role}Administrators", permissionHelper.decode("RAdministrators"));
        assertEquals("(deny){user}user with spaces", permissionHelper.decode("D1user with spaces"));
        assertEquals("(deny){role}_role_with_underscores", permissionHelper.decode("DR_role_with_underscores"));
        assertEquals("(deny){group}group123", permissionHelper.decode("D2group123"));
    }

    @Test
    public void test_decode_withInvalidPrefixes() {
        assertEquals("invalid", permissionHelper.decode("invalid"));
        assertEquals("3guest", permissionHelper.decode("3guest"));
        assertEquals("Xguest", permissionHelper.decode("Xguest"));
        assertEquals("guest", permissionHelper.decode("guest"));
    }

    @Test
    public void test_encode_withWhitespaceValues() {
        assertNull(permissionHelper.encode("{user}   "));
        assertNull(permissionHelper.encode("{group}\t"));
        assertNull(permissionHelper.encode("{role}\n"));
        assertNull(permissionHelper.encode("(allow){user}  "));
        assertNull(permissionHelper.encode("(deny){group}\t\n"));
    }

    @Test
    public void test_encode_decode_symmetry() {
        String[] testValues =
                { "guest", "{user}guest", "{group}guest", "{role}guest", "(deny){user}guest", "(deny){group}guest", "(deny){role}guest" };

        for (String value : testValues) {
            String encoded = permissionHelper.encode(value);
            if (encoded != null) {
                String decoded = permissionHelper.decode(encoded);
                assertNotNull("Decoded value should not be null for: " + value, decoded);
                assertEquals("Decoded value should match original: " + value + " -> " + encoded + " -> " + decoded, value, decoded);
            }
        }
    }

    @Test
    public void test_encode_withLongValues() {
        String longUser = "user" + "x".repeat(1000);
        String longGroup = "group" + "y".repeat(1000);
        String longRole = "role" + "z".repeat(1000);

        String encodedUser = permissionHelper.encode("{user}" + longUser);
        String encodedGroup = permissionHelper.encode("{group}" + longGroup);
        String encodedRole = permissionHelper.encode("{role}" + longRole);

        assertEquals("1" + longUser, encodedUser);
        assertEquals("2" + longGroup, encodedGroup);
        assertEquals("R" + longRole, encodedRole);
    }

    @Test
    public void test_decode_withLongValues() {
        String longUser = "user" + "x".repeat(1000);
        String longGroup = "group" + "y".repeat(1000);
        String longRole = "role" + "z".repeat(1000);

        String decodedUser = permissionHelper.decode("1" + longUser);
        String decodedGroup = permissionHelper.decode("2" + longGroup);
        String decodedRole = permissionHelper.decode("R" + longRole);

        assertEquals("{user}" + longUser, decodedUser);
        assertEquals("{group}" + longGroup, decodedGroup);
        assertEquals("{role}" + longRole, decodedRole);
    }

    @Test
    public void test_nullSafetyChecks() {
        assertNull(permissionHelper.encode(null));
        assertNull(permissionHelper.decode(null));

        assertNull(permissionHelper.encode(""));
        assertNull(permissionHelper.decode(""));

        assertNull(permissionHelper.encode("  "));
        assertNull(permissionHelper.decode("  "));
    }

    @Test
    public void test_allowPrefixBehavior() {
        assertEquals("1guest", permissionHelper.encode("(allow){user}guest"));
        assertEquals("2guest", permissionHelper.encode("(allow){group}guest"));
        assertEquals("Rguest", permissionHelper.encode("(allow){role}guest"));

        assertEquals("1guest", permissionHelper.encode("{user}guest"));
        assertEquals("2guest", permissionHelper.encode("{group}guest"));
        assertEquals("Rguest", permissionHelper.encode("{role}guest"));
    }

    @Test
    public void test_denyPrefixBehavior() {
        assertEquals("D1guest", permissionHelper.encode("(deny){user}guest"));
        assertEquals("D2guest", permissionHelper.encode("(deny){group}guest"));
        assertEquals("DRguest", permissionHelper.encode("(deny){role}guest"));

        assertEquals("(deny){user}guest", permissionHelper.decode("D1guest"));
        assertEquals("(deny){group}guest", permissionHelper.decode("D2guest"));
        assertEquals("(deny){role}guest", permissionHelper.decode("DRguest"));
    }

    @Test
    public void test_caseInsensitivePrefixes() {
        assertEquals("1guest", permissionHelper.encode("{USER}guest"));
        assertEquals("2guest", permissionHelper.encode("{GROUP}guest"));
        assertEquals("Rguest", permissionHelper.encode("{ROLE}guest"));

        assertEquals("1guest", permissionHelper.encode("(ALLOW){user}guest"));
        assertEquals("D1guest", permissionHelper.encode("(DENY){user}guest"));

        assertEquals("D1guest", permissionHelper.encode("(deny){USER}guest"));
        assertEquals("D2guest", permissionHelper.encode("(deny){GROUP}guest"));
        assertEquals("DRguest", permissionHelper.encode("(deny){ROLE}guest"));
    }

    @Test
    public void test_invalidInputs() {
        assertNull(permissionHelper.encode("{user}"));
        assertNull(permissionHelper.encode("{group}"));
        assertNull(permissionHelper.encode("{role}"));

        assertNull(permissionHelper.encode("{user}   "));
        assertNull(permissionHelper.encode("{group}\t"));
        assertNull(permissionHelper.encode("{role}\n"));

        assertNull(permissionHelper.encode("(allow)"));
        assertNull(permissionHelper.encode("(deny)"));
        assertNull(permissionHelper.encode("(allow){user}"));
        assertNull(permissionHelper.encode("(deny){group}"));
    }

    @Test
    public void test_passthroughValues() {
        assertEquals("plaintext", permissionHelper.encode("plaintext"));
        assertEquals("plaintext", permissionHelper.decode("plaintext"));

        assertEquals("abc", permissionHelper.encode("abc"));
        assertEquals("abc", permissionHelper.decode("abc"));

        assertEquals("special@chars", permissionHelper.encode("special@chars"));
        assertEquals("special@chars", permissionHelper.decode("special@chars"));
    }

}