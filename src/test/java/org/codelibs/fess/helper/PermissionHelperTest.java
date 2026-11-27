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

import java.io.IOException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.EnumSet;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.codelibs.fess.crawler.client.fs.FileSystemClient;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class PermissionHelperTest extends UnitFessTestCase {

    public PermissionHelper permissionHelper;

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

    // encode stays lenient about whitespace after the prefix: it also parses configuration values read
    // while searching. Entries typed into a form are rejected before they get here, see test_isSpacedEntry.
    @Test
    public void test_encode_trimsAfterAclPrefix() {
        assertEquals("D1bob", permissionHelper.encode("(deny) {user}bob"));
        assertEquals("D2staff", permissionHelper.encode("(DENY)\t{group}staff"));
        assertEquals("DRadmin", permissionHelper.encode("(deny)  {role}admin"));
        assertEquals("1bob", permissionHelper.encode("(allow) {user}bob"));
        assertEquals("2staff", permissionHelper.encode("(Allow)  {group}staff"));
        assertEquals("Radmin", permissionHelper.encode("(allow) {role}admin"));
        assertNull(permissionHelper.encode("(deny) "));
        assertNull(permissionHelper.encode("(allow)   {user}"));
    }

    @Test
    public void test_encode_denyWithoutTypeIsRejected() {
        assertNull(permissionHelper.encode("(deny)foo"));
        assertNull(permissionHelper.encode("(deny) foo"));
        assertNull(permissionHelper.encode("(DENY)foo"));
        assertNull(permissionHelper.encode("(deny)(allow)foo"));
        assertNull(permissionHelper.encode("(allow)(deny)foo"));
        assertNull(permissionHelper.encode("(deny)(deny)"));
        // allow keeps its raw pass-through, now without the leading blank
        assertEquals("foo", permissionHelper.encode("(allow)foo"));
        assertEquals("foo", permissionHelper.encode("(allow) foo"));
    }

    @Test
    public void test_encode_stackedPrefixesCollapse() {
        // a single (deny) anywhere among the prefixes denies the entry, whatever the order
        assertEquals("DRx", permissionHelper.encode("(allow)(deny){role}x"));
        assertEquals("DRx", permissionHelper.encode("(deny)(allow){role}x"));
        assertEquals("DRx", permissionHelper.encode("(deny)(deny){role}x"));
        assertEquals("D1bob", permissionHelper.encode("(deny)(allow){user}bob"));
        assertEquals("D2staff", permissionHelper.encode("(allow)(allow)(deny)(allow){group}staff"));
        assertEquals("D1bob", permissionHelper.encode("(deny)(deny){user}bob"));
        // allow only
        assertEquals("Rx", permissionHelper.encode("(allow)(allow){role}x"));
        assertEquals("1bob", permissionHelper.encode("(allow)(allow)(allow){user}bob"));
        assertEquals("foo", permissionHelper.encode("(allow)(allow)foo"));
        // mixed case and whitespace between the prefixes
        assertEquals("DRx", permissionHelper.encode("(ALLOW)(Deny){ROLE}x"));
        assertEquals("DRx", permissionHelper.encode("(Deny) (aLLow)\t{role}x"));
        assertEquals("D2staff", permissionHelper.encode("  (allow) (deny) (allow) {Group}staff  "));
    }

    @Test
    public void test_encode_stackedPrefixesAreFixedPoint() {
        final String[] inputs = { "(allow)(deny){role}x", "(deny)(allow){role}x", "(deny)(deny){user}bob", "(allow)(allow){group}staff",
                "(ALLOW)(Deny) {USER}bob", "(allow)(allow)foo", "(deny)(allow)(deny){role}Dev" };
        for (final String input : inputs) {
            final String encoded = permissionHelper.encode(input);
            assertNotNull("encode should accept: " + input, encoded);
            final String decoded = permissionHelper.decode(encoded);
            assertEquals("displayed form of " + input, decoded, permissionHelper.decode(permissionHelper.encode(decoded)));
            assertEquals("re-saving the displayed form of " + input, encoded, permissionHelper.encode(decoded));
        }
        assertEquals("(deny){role}x", permissionHelper.decode(permissionHelper.encode("(allow)(deny){role}x")));
        assertEquals("(deny){role}x", permissionHelper.decode(permissionHelper.encode("(deny)(allow){role}x")));
        assertEquals("{role}x", permissionHelper.decode(permissionHelper.encode("(allow)(allow){role}x")));
    }

    @Test
    public void test_isSpacedEntry_stackedPrefixes() {
        assertTrue(permissionHelper.isSpacedEntry("(allow)(deny) {user}bob"));
        assertTrue(permissionHelper.isSpacedEntry("(allow) (deny){user}bob"));
        assertFalse(permissionHelper.isSpacedEntry("(allow)(deny){user}bob"));
        assertFalse(permissionHelper.isSpacedEntry("(allow)(allow)foo"));
        assertFalse(permissionHelper.isSpacedEntry("(deny)(allow)"));
    }

    @Test
    public void test_decode_deniedPrefixNeedsTypedName() {
        assertEquals("Developers", permissionHelper.decode("Developers"));
        assertEquals("Dev", permissionHelper.decode("Dev"));
        assertEquals("D", permissionHelper.decode("D"));
        assertEquals("D1", permissionHelper.decode("D1"));
        assertEquals("D2", permissionHelper.decode("D2"));
        assertEquals("DR", permissionHelper.decode("DR"));
        assertEquals("(deny){user}Dave", permissionHelper.decode("D1Dave"));
        assertEquals("(deny){role}Rick", permissionHelper.decode("DRRick"));
        assertEquals("{user}Dave", permissionHelper.decode("1Dave"));
    }

    @Test
    public void test_encode_decode_roundTrip() {
        final String[] inputs =
                { "{user}bob", "{group}staff", "{role}admin", "{USER}bob", "{Group}staff", "{ROLE}admin", "(allow){user}bob",
                        "(allow){group}staff", "(allow){role}admin", "(Allow){User}bob", "(deny){user}bob", "(deny){group}staff",
                        "(deny){role}admin", "(DENY){USER}bob", "(Deny){Group}staff", "(deny) {user}bob", "(allow) {role}admin",
                        // names that start like a type or denied prefix
                        "{user}Dave", "{group}Developers", "{role}Dev", "{user}1abc", "{group}2abc", "{role}Rick", "(deny){user}Dave",
                        "(deny){group}Developers", "(deny){role}Rick", "(deny){user}1abc", "(deny){group}2abc", "(deny){role}Dev",
                        // raw values
                        "guest", "Developers", "D", "Dev", "Rick", "1", "2", "R", "D1", "special@chars" };
        for (final String input : inputs) {
            final String encoded = permissionHelper.encode(input);
            assertNotNull("encode should accept: " + input, encoded);
            final String decoded = permissionHelper.decode(encoded);
            assertNotNull("decode should accept: " + encoded, decoded);
            assertEquals("round trip of " + input + " via " + encoded + " -> " + decoded, encoded, permissionHelper.encode(decoded));
        }
    }

    @Test
    public void test_decode_encode_keepsDeny() {
        assertEquals("D1bob", permissionHelper.encode(permissionHelper.decode("D1bob")));
        assertEquals("(deny){user}bob", permissionHelper.decode(permissionHelper.encode("(deny) {user}bob")));
    }

    @Test
    public void test_isSpacedEntry_spaceAfterPrefix() {
        assertTrue(permissionHelper.isSpacedEntry("(deny) {user}bob"));
        assertTrue(permissionHelper.isSpacedEntry("(allow) {user}bob"));
        assertTrue(permissionHelper.isSpacedEntry("(DENY)\t{group}staff"));
        assertTrue(permissionHelper.isSpacedEntry("(Allow)  {role}admin"));
        assertTrue(permissionHelper.isSpacedEntry("(deny)\u3000{user}bob"));
        assertTrue(permissionHelper.isSpacedEntry("  (deny) {user}bob  "));
        assertTrue(permissionHelper.isSpacedEntry("(deny) foo"));
        assertTrue(permissionHelper.isSpacedEntry("(allow) guest"));
    }

    @Test
    public void test_isSpacedEntry_legacyLeadingSpace() {
        // how Fess 15.9 stored "(deny) {user}bob" and "(allow) {user}bob", as shown by the edit form
        assertTrue(permissionHelper.isSpacedEntry(" {user}bob"));
        assertTrue(permissionHelper.isSpacedEntry(" {group}staff"));
        assertTrue(permissionHelper.isSpacedEntry("\t{role}admin"));
        assertTrue(permissionHelper.isSpacedEntry("\u3000{USER}bob"));
        assertTrue(permissionHelper.isSpacedEntry(permissionHelper.decode(" {user}bob")));
    }

    @Test
    public void test_isSpacedEntry_acceptedEntries() {
        assertFalse(permissionHelper.isSpacedEntry(null));
        assertFalse(permissionHelper.isSpacedEntry(""));
        assertFalse(permissionHelper.isSpacedEntry("  "));
        assertFalse(permissionHelper.isSpacedEntry("{user}bob"));
        assertFalse(permissionHelper.isSpacedEntry("(deny){user}bob"));
        assertFalse(permissionHelper.isSpacedEntry("(allow){role}admin"));
        assertFalse(permissionHelper.isSpacedEntry("(DENY){GROUP}staff"));
        // trailing whitespace is trimmed as before
        assertFalse(permissionHelper.isSpacedEntry("{user}bob  "));
        assertFalse(permissionHelper.isSpacedEntry("(deny){user}bob \r"));
        // whitespace before the prefix and around a plain value is trimmed as before
        assertFalse(permissionHelper.isSpacedEntry("  (deny){user}bob"));
        assertFalse(permissionHelper.isSpacedEntry(" guest "));
        // a prefix without a name, and whitespace inside a name
        assertFalse(permissionHelper.isSpacedEntry("(deny)"));
        assertFalse(permissionHelper.isSpacedEntry("(deny)   "));
        assertFalse(permissionHelper.isSpacedEntry("(deny){user}user with spaces"));
        assertFalse(permissionHelper.isSpacedEntry("{user} bob"));
        assertFalse(permissionHelper.isSpacedEntry("special@chars"));
    }

    @Test
    public void test_hasSpacedEntry_checksEveryLine() {
        assertFalse(permissionHelper.hasSpacedEntry(null));
        assertFalse(permissionHelper.hasSpacedEntry(""));
        assertFalse(permissionHelper.hasSpacedEntry("{user}alice\n(deny){group}staff\r\nguest\n\n"));
        assertTrue(permissionHelper.hasSpacedEntry("{user}alice\n(deny) {group}staff\nguest"));
        assertTrue(permissionHelper.hasSpacedEntry("{user}alice\r\n {role}admin"));
    }

    @Test
    public void test_validateEntries() {
        final FessMessages ok = new FessMessages();
        permissionHelper.validateEntries("{user}alice\n(deny){role}guest", ok);
        assertTrue(ok.isEmpty());

        final FessMessages ng = new FessMessages();
        permissionHelper.validateEntries("{user}alice\n(deny) {role}guest", ng);
        assertTrue(ng.hasMessageOf("permissions"));
    }

    @Test
    public void test_validateEntries_firstEntryWithLeadingSpace() {
        // the admin API reads a JSON body: no parameter, the given text is judged
        final FessMessages api = new FessMessages();
        permissionHelper.validateEntries("{user}bob\n {role}admin", api);
        assertTrue(api.hasMessageOf("permissions"));

        // the form mapping trims the parameter, so the form holds "{user}bob..." while the request still has the space
        getMockRequest().setParameter("permissions", " {user}bob\r\nDevelopers");
        final FessMessages ng = new FessMessages();
        permissionHelper.validateEntries("{user}bob\nDevelopers", ng);
        assertTrue(ng.hasMessageOf("permissions"));

        // a request parameter that is some other text is not used
        getMockRequest().setParameter("permissions", " {user}carol");
        final FessMessages ok = new FessMessages();
        permissionHelper.validateEntries("{user}bob\nDevelopers", ok);
        assertTrue(ok.isEmpty());
    }

    @Test
    public void test_isFullWidthSyntaxEntry_fullWidthPrefixOrMarker() {
        assertTrue(permissionHelper.isFullWidthSyntaxEntry("\uFF08deny\uFF09{user}dave"));
        assertTrue(permissionHelper.isFullWidthSyntaxEntry("\uFF08allow\uFF09{role}admin"));
        assertTrue(permissionHelper.isFullWidthSyntaxEntry("(deny)\uFF5Buser\uFF5Ddave"));
        assertTrue(permissionHelper.isFullWidthSyntaxEntry("\uFF5Brole\uFF5Dguest"));
        assertTrue(permissionHelper.isFullWidthSyntaxEntry("{\uFF55\uFF53\uFF45\uFF52}bob"));
        assertTrue(permissionHelper.isFullWidthSyntaxEntry("\uFF08\uFF44\uFF45\uFF4E\uFF59\uFF09{group}staff"));
        assertTrue(permissionHelper.isFullWidthSyntaxEntry("  \uFF08deny\uFF09{user}dave  "));
    }

    @Test
    public void test_isFullWidthSyntaxEntry_acceptedEntries() {
        assertFalse(permissionHelper.isFullWidthSyntaxEntry(null));
        assertFalse(permissionHelper.isFullWidthSyntaxEntry(""));
        assertFalse(permissionHelper.isFullWidthSyntaxEntry("{user}bob"));
        assertFalse(permissionHelper.isFullWidthSyntaxEntry("(deny){user}bob"));
        assertFalse(permissionHelper.isFullWidthSyntaxEntry("(DENY){GROUP}staff"));
        // the name after the syntax is taken as typed: non-ASCII names stay valid
        assertFalse(permissionHelper.isFullWidthSyntaxEntry("{role}\uFF47uest"));
        assertFalse(permissionHelper.isFullWidthSyntaxEntry("(deny){user}\u7530\u4E2D"));
        assertFalse(permissionHelper.isFullWidthSyntaxEntry("{role}\u958B\u767A\u8005"));
        assertFalse(permissionHelper.isFullWidthSyntaxEntry("\u958B\u767A\u8005"));
        // a value that merely starts with a full-width parenthesis is not a permission syntax
        assertFalse(permissionHelper.isFullWidthSyntaxEntry("\uFF08\u682A\uFF09\u55B6\u696D"));
        // whitespace is the business of isSpacedEntry
        assertFalse(permissionHelper.isFullWidthSyntaxEntry("(deny) {user}bob"));
    }

    @Test
    public void test_isFullWidthSyntaxEntry_followsTheConfiguredPrefixes() {
        permissionHelper.setUserPrefix("{usr}");
        permissionHelper.setRolePrefix("{rl}");
        permissionHelper.allowPrefix = "(ok)";
        permissionHelper.denyPrefix = "(ng)";
        assertTrue(permissionHelper.isFullWidthSyntaxEntry("\uFF08ng\uFF09{usr}dave"));
        assertTrue(permissionHelper.isFullWidthSyntaxEntry("(ng)\uFF5Brl\uFF5Ddave"));
        assertFalse(permissionHelper.isFullWidthSyntaxEntry("(ng){usr}dave"));
        // the default spelling is not the configured one: it is a plain value
        assertFalse(permissionHelper.isFullWidthSyntaxEntry("\uFF08deny\uFF09{user}dave"));
    }

    @Test
    public void test_validateEntries_fullWidthSyntax() {
        final FessMessages ng = new FessMessages();
        permissionHelper.validateEntries("{user}alice\n\uFF08deny\uFF09{user}dave\n(deny){user}\u3000erin", ng);
        assertTrue(ng.hasMessageOf("permissions"));

        final FessMessages ok = new FessMessages();
        permissionHelper.validateEntries("{user}alice\n(deny){user}dave\n{role}\uFF47uest\n\u958B\u767A\u8005", ok);
        assertTrue(ok.isEmpty());
    }

    @Test
    public void test_fullWidthMessage_usesOnlyItsTwoArguments() throws Exception {
        // a literal {user} in a message is read as an argument placeholder, so only the examples {0} and {1} are given
        final java.io.File dir = new java.io.File("src/main/resources");
        final java.io.File[] files = dir.listFiles((d, name) -> name.startsWith("fess_message") && name.endsWith(".properties"));
        assertTrue(files.length >= 17);
        for (final java.io.File file : files) {
            final java.util.Properties props = new java.util.Properties();
            try (java.io.Reader reader = new java.io.InputStreamReader(new java.io.FileInputStream(file), StandardCharsets.UTF_8)) {
                props.load(reader);
            }
            final String message = props.getProperty("errors.invalid_permission_fullwidth");
            assertNotNull(message, file.getName());
            assertTrue(message.contains("{0}") && message.contains("{1}"), file.getName());
            assertTrue(message.replaceAll("\\{[01]\\}", "").replaceAll("[^{}]", "").isEmpty(), file.getName());
        }
    }

    @Test
    public void test_isInvisibleLeadEntry_invisibleCharacterWhereTheSyntaxStarts() {
        // a zero-width space or a byte order mark right after the prefix: encode() would drop the deny
        assertTrue(permissionHelper.isInvisibleLeadEntry("(deny)\u200B{user}bob"));
        assertTrue(permissionHelper.isInvisibleLeadEntry("(deny)\uFEFF{role}x"));
        assertTrue(permissionHelper.isInvisibleLeadEntry("(DENY)\u2060{group}staff"));
        // at the start of the entry, also in front of a prefix, and after stacked prefixes
        assertTrue(permissionHelper.isInvisibleLeadEntry("\u200B{user}bob"));
        assertTrue(permissionHelper.isInvisibleLeadEntry("\uFEFF(deny){user}bob"));
        assertTrue(permissionHelper.isInvisibleLeadEntry("  \uFEFF(deny){user}bob"));
        assertTrue(permissionHelper.isInvisibleLeadEntry("(allow)(deny)\u200B{role}x"));
        // a plain value that starts with one is also refused: it matches nobody
        assertTrue(permissionHelper.isInvisibleLeadEntry("\u200Bdevelopers"));
        // the same character at the start of the name, after the marker
        assertTrue(permissionHelper.isInvisibleLeadEntry("(deny){user}\u200Bbob"));
        assertTrue(permissionHelper.isInvisibleLeadEntry("{role}\uFEFFadmin"));
        // a tab right after the marker is a control character as well
        assertTrue(permissionHelper.isInvisibleLeadEntry("{role}\tadmin"));
        // a control character that strip() keeps
        assertTrue(permissionHelper.isInvisibleLeadEntry("(deny)\u0085{user}bob"));
        assertTrue(permissionHelper.isInvisibleLeadEntry("(deny)\u0000{user}bob"));
    }

    @Test
    public void test_isInvisibleLeadEntry_acceptedEntries() {
        assertFalse(permissionHelper.isInvisibleLeadEntry(null));
        assertFalse(permissionHelper.isInvisibleLeadEntry(""));
        assertFalse(permissionHelper.isInvisibleLeadEntry("{user}bob"));
        assertFalse(permissionHelper.isInvisibleLeadEntry("(deny){user}bob"));
        assertFalse(permissionHelper.isInvisibleLeadEntry("(allow)(deny){role}x"));
        assertFalse(permissionHelper.isInvisibleLeadEntry("developers"));
        // an invisible character inside a name is part of the name (the zero-width joiner of an emoji sequence)
        assertFalse(permissionHelper.isInvisibleLeadEntry("{role}\uD83D\uDC68\u200D\uD83D\uDCBB"));
        assertFalse(permissionHelper.isInvisibleLeadEntry("(deny){user}a\u200Bb"));
        // the end of a name is not judged
        assertFalse(permissionHelper.isInvisibleLeadEntry("(deny){user}bob\u200B"));
        // whitespace is the business of isSpacedEntry
        assertFalse(permissionHelper.isInvisibleLeadEntry("(deny) {user}bob"));
    }

    @Test
    public void test_validateEntries_invisibleLead() {
        final FessMessages ng = new FessMessages();
        permissionHelper.validateEntries("{user}alice\n(deny)\u200B{user}bob", ng);
        assertTrue(ng.hasMessageOf("permissions"));

        final FessMessages ok = new FessMessages();
        permissionHelper.validateEntries("{user}alice\n(deny){user}bob\n{role}\uD83D\uDC68\u200D\uD83D\uDCBB", ok);
        assertTrue(ok.isEmpty());
    }

    @Test
    public void test_invisibleMessage_usesOnlyItsTwoArguments() throws Exception {
        final java.io.File dir = new java.io.File("src/main/resources");
        final java.io.File[] files = dir.listFiles((d, name) -> name.startsWith("fess_message") && name.endsWith(".properties"));
        assertTrue(files.length >= 17);
        for (final java.io.File file : files) {
            final java.util.Properties props = new java.util.Properties();
            try (java.io.Reader reader = new java.io.InputStreamReader(new java.io.FileInputStream(file), StandardCharsets.UTF_8)) {
                props.load(reader);
            }
            final String message = props.getProperty("errors.invalid_permission_invisible");
            assertNotNull(message, file.getName());
            assertTrue(message.contains("{0}") && message.contains("{1}"), file.getName());
            assertTrue(message.replaceAll("\\{[01]\\}", "").replaceAll("[^{}]", "").isEmpty(), file.getName());
        }
    }

    private static UserPrincipal user(final String name) {
        return new UserPrincipal() {
            @Override
            public String getName() {
                return name;
            }
        };
    }

    private static GroupPrincipal group(final String name) {
        return new GroupPrincipal() {
            @Override
            public String getName() {
                return name;
            }
        };
    }

    private static AclEntry aclEntry(final AclEntryType type, final UserPrincipal principal, final AclEntryPermission... permissions) {
        return AclEntry.newBuilder().setType(type).setPrincipal(principal).setPermissions(permissions).build();
    }

    private static AclFileAttributeView aclView(final AclEntry... entries) {
        return new AclFileAttributeView() {
            @Override
            public String name() {
                return "acl";
            }

            @Override
            public List<AclEntry> getAcl() throws IOException {
                return List.of(entries);
            }

            @Override
            public void setAcl(final List<AclEntry> acl) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public UserPrincipal getOwner() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setOwner(final UserPrincipal owner) throws IOException {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static PosixFileAttributeView posixView(final UserPrincipal owner, final GroupPrincipal group) {
        return new PosixFileAttributeView() {
            @Override
            public String name() {
                return "posix";
            }

            @Override
            public PosixFileAttributes readAttributes() throws IOException {
                return new PosixFileAttributes() {
                    @Override
                    public UserPrincipal owner() {
                        return owner;
                    }

                    @Override
                    public GroupPrincipal group() {
                        return group;
                    }

                    @Override
                    public Set<PosixFilePermission> permissions() {
                        return EnumSet.noneOf(PosixFilePermission.class);
                    }

                    @Override
                    public FileTime lastModifiedTime() {
                        return null;
                    }

                    @Override
                    public FileTime lastAccessTime() {
                        return null;
                    }

                    @Override
                    public FileTime creationTime() {
                        return null;
                    }

                    @Override
                    public boolean isRegularFile() {
                        return true;
                    }

                    @Override
                    public boolean isDirectory() {
                        return false;
                    }

                    @Override
                    public boolean isSymbolicLink() {
                        return false;
                    }

                    @Override
                    public boolean isOther() {
                        return false;
                    }

                    @Override
                    public long size() {
                        return 0;
                    }

                    @Override
                    public Object fileKey() {
                        return null;
                    }
                };
            }

            @Override
            public void setTimes(final FileTime lastModifiedTime, final FileTime lastAccessTime, final FileTime createTime)
                    throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setPermissions(final Set<PosixFilePermission> perms) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setGroup(final GroupPrincipal group) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public UserPrincipal getOwner() throws IOException {
                return owner;
            }

            @Override
            public void setOwner(final UserPrincipal owner) throws IOException {
                throw new UnsupportedOperationException();
            }
        };
    }

    private static ResponseData fileResponse(final Object fileAttributeView) {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("file:/data/test.txt");
        responseData.addMetaData(FileSystemClient.FILE_ATTRIBUTE_VIEW, fileAttributeView);
        return responseData;
    }

    @Test
    public void test_getFileRoleTypeList_allowReadGroup() {
        final AclFileAttributeView view = aclView(aclEntry(AclEntryType.ALLOW, group("staff"), AclEntryPermission.READ_DATA));
        assertEquals(List.of("2staff"), permissionHelper.getFileRoleTypeList(fileResponse(view)));
    }

    @Test
    public void test_getFileRoleTypeList_denyReadUser() {
        final AclFileAttributeView view = aclView(aclEntry(AclEntryType.DENY, user("bob"), AclEntryPermission.READ_DATA));
        assertEquals(List.of("D1bob"), permissionHelper.getFileRoleTypeList(fileResponse(view)));
    }

    @Test
    public void test_getFileRoleTypeList_denyReadGroup() {
        final AclFileAttributeView view = aclView(aclEntry(AclEntryType.DENY, group("guests"), AclEntryPermission.READ_DATA));
        assertEquals(List.of("D2guests"), permissionHelper.getFileRoleTypeList(fileResponse(view)));
    }

    @Test
    public void test_getFileRoleTypeList_allowWithoutReadIsIgnored() {
        final AclFileAttributeView view = aclView(aclEntry(AclEntryType.ALLOW, user("alice"), AclEntryPermission.WRITE_DATA));
        assertTrue(permissionHelper.getFileRoleTypeList(fileResponse(view)).isEmpty());
    }

    @Test
    public void test_getFileRoleTypeList_denyWithoutReadIsIgnored() {
        final AclFileAttributeView view = aclView(aclEntry(AclEntryType.DENY, user("alice"), AclEntryPermission.WRITE_DATA));
        assertTrue(permissionHelper.getFileRoleTypeList(fileResponse(view)).isEmpty());
    }

    @Test
    public void test_getFileRoleTypeList_auditAndAlarmAreIgnored() {
        final AclFileAttributeView view = aclView(aclEntry(AclEntryType.AUDIT, user("alice"), AclEntryPermission.READ_DATA),
                aclEntry(AclEntryType.ALARM, group("staff"), AclEntryPermission.READ_DATA));
        assertTrue(permissionHelper.getFileRoleTypeList(fileResponse(view)).isEmpty());
    }

    @Test
    public void test_getFileRoleTypeList_mixedEntries() {
        final AclFileAttributeView view = aclView(aclEntry(AclEntryType.ALLOW, group("staff"), AclEntryPermission.READ_DATA),
                aclEntry(AclEntryType.DENY, user("bob"), AclEntryPermission.READ_DATA, AclEntryPermission.WRITE_DATA),
                aclEntry(AclEntryType.ALLOW, user("alice"), AclEntryPermission.WRITE_DATA),
                aclEntry(AclEntryType.ALLOW, user("carol"), AclEntryPermission.READ_DATA, AclEntryPermission.WRITE_DATA));
        assertEquals(List.of("2staff", "D1bob", "1carol"), permissionHelper.getFileRoleTypeList(fileResponse(view)));
    }

    @Test
    public void test_getFileRoleTypeList_posixOwnerAndGroup() {
        assertEquals(List.of("1bob", "2staff"), permissionHelper.getFileRoleTypeList(fileResponse(posixView(user("bob"), group("staff")))));
    }

    @Test
    public void test_getFileRoleTypeList_posixNullPrincipalsWithDebugLog() {
        final String loggerName = PermissionHelper.class.getName();
        final Level original = LogManager.getLogger(loggerName).getLevel();
        Configurator.setLevel(loggerName, Level.DEBUG);
        try {
            assertTrue(permissionHelper.getFileRoleTypeList(fileResponse(posixView(null, null))).isEmpty());
            assertEquals(List.of("1bob"), permissionHelper.getFileRoleTypeList(fileResponse(posixView(user("bob"), null))));
            assertEquals(List.of("2staff"), permissionHelper.getFileRoleTypeList(fileResponse(posixView(null, group("staff")))));
        } finally {
            Configurator.setLevel(loggerName, original);
        }
    }

    @Test
    public void test_encode_ignoresIdeographicSpaceAroundPrefixes() {
        // U+3000 is not removed by String#trim; a deny entry typed with it must not be dropped
        assertEquals("D1guest", permissionHelper.encode("(deny)\u3000{user}guest"));
        assertEquals("DRguest", permissionHelper.encode("\u3000(deny)\u3000{role}guest\u3000"));
        assertEquals("2guest", permissionHelper.encode("(allow)\u3000{group}guest"));
        assertNull(permissionHelper.encode("(deny)\u3000"));
    }

    // U+00A0, U+2007 and U+202F are space separators that Character.isWhitespace, and so String#strip, does not
    // count as whitespace
    private static final String[] NO_BREAK_SPACES = { "\u00a0", "\u2007", "\u202f" };

    @Test
    public void test_encode_ignoresNoBreakSpaceAroundEntries() {
        for (final String sp : NO_BREAK_SPACES) {
            assertEquals("guest", permissionHelper.encode(sp + "guest" + sp));
            assertEquals("Rguest", permissionHelper.encode("{role}guest" + sp));
            // a deny entry typed with it must not be dropped
            assertEquals("D1bob", permissionHelper.encode(sp + "(deny){user}bob"));
            assertEquals("D1bob", permissionHelper.encode("(deny)" + sp + "{user}bob"));
            assertEquals("DRguest", permissionHelper.encode(sp + "(allow)" + sp + "(deny)" + sp + "{role}guest" + sp));
            assertNull(permissionHelper.encode(sp));
            assertNull(permissionHelper.encode("{user}" + sp));
            assertNull(permissionHelper.encode("(deny)" + sp));
        }
    }

    @Test
    public void test_isSpacedEntry_noBreakSpace() {
        for (final String sp : NO_BREAK_SPACES) {
            assertTrue(permissionHelper.isSpacedEntry(sp + "{role}guest"));
            assertTrue(permissionHelper.isSpacedEntry(sp + "{USER}bob"));
            assertTrue(permissionHelper.isSpacedEntry("(deny)" + sp + "{user}bob"));
            assertTrue(permissionHelper.isSpacedEntry("(allow)(deny)" + sp + "{user}bob"));
            assertTrue(permissionHelper.isSpacedEntry(" " + sp + "{group}staff"));
            // accepted like their ASCII counterparts
            assertFalse(permissionHelper.isSpacedEntry(sp + "guest" + sp));
            assertFalse(permissionHelper.isSpacedEntry(sp + "(deny){user}bob"));
            assertFalse(permissionHelper.isSpacedEntry("{user}bob" + sp));
            assertFalse(permissionHelper.isSpacedEntry("{user}" + sp + "bob"));
            assertFalse(permissionHelper.isSpacedEntry(sp));
        }
    }

    @Test
    public void test_isInvisibleLeadEntry_behindNoBreakSpace() {
        for (final String sp : NO_BREAK_SPACES) {
            assertTrue(permissionHelper.isInvisibleLeadEntry(sp + "\u200b{user}bob"));
            assertTrue(permissionHelper.isInvisibleLeadEntry(sp + "(deny)\ufeff{user}bob"));
            // a space after the prefix is refused by isSpacedEntry, as an ASCII space is
            assertTrue(permissionHelper.isSpacedEntry(sp + "(deny)" + sp + "\ufeff{user}bob"));
            assertFalse(permissionHelper.isInvisibleLeadEntry(sp + "(deny){user}bob" + sp));
        }
    }

    @Test
    public void test_isFullWidthSyntaxEntry_noBreakSpaceAroundEntry() {
        for (final String sp : NO_BREAK_SPACES) {
            assertTrue(permissionHelper.isFullWidthSyntaxEntry(sp + "\uff08deny\uff09{user}bob"));
            assertFalse(permissionHelper.isFullWidthSyntaxEntry(sp + "(deny){user}bob" + sp));
        }
    }

    @Test
    public void test_validateEntries_noBreakSpace() {
        for (final String sp : NO_BREAK_SPACES) {
            final FessMessages lead = new FessMessages();
            permissionHelper.validateEntries("{user}alice\n" + sp + "{role}guest", lead);
            assertTrue(lead.hasMessageOf("permissions"));

            final FessMessages afterPrefix = new FessMessages();
            permissionHelper.validateEntries("{user}alice\n(deny)" + sp + "{user}bob", afterPrefix);
            assertTrue(afterPrefix.hasMessageOf("permissions"));

            final FessMessages ok = new FessMessages();
            permissionHelper.validateEntries(sp + "guest\n" + sp + "(deny){user}bob" + sp, ok);
            assertTrue(ok.isEmpty());
        }
    }

}
