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
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.codelibs.fess.crawler.client.fs.FileSystemClient;
import org.codelibs.fess.crawler.entity.ResponseData;
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
        assertNull(permissionHelper.encode("(deny)(allow){user}bob"));
        assertNull(permissionHelper.encode("(deny)(deny){user}bob"));
        // allow keeps its raw pass-through, now without the leading blank
        assertEquals("foo", permissionHelper.encode("(allow)foo"));
        assertEquals("foo", permissionHelper.encode("(allow) foo"));
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

}
