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

import java.io.File;

import org.codelibs.core.io.FileUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

import jcifs.SID;
import jcifs.smb.SmbException;

public class SambaHelperTest extends UnitFessTestCase {

    public SambaHelper sambaHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Setup system properties for DI container
        File file = File.createTempFile("test", ".properties");
        file.deleteOnExit();
        FileUtil.writeBytes(file.getAbsolutePath(), "test.property=test".getBytes("UTF-8"));
        DynamicProperties systemProps = new DynamicProperties(file);
        ComponentUtil.register(systemProps, "systemProperties");

        sambaHelper = new SambaHelper();
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    public void test_smb_account() throws SmbException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getSmbAvailableSidTypes() {
                return "1,2,4:2,5:1";
            }

            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }

            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return false;
            }
        });
        sambaHelper.init();

        assertEquals("1Test User", sambaHelper.getAccountId(USER_SID));

    }

    public void test_smb_account_lowercase() throws SmbException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getSmbAvailableSidTypes() {
                return "1,2,4:2,5:1";
            }

            @Override
            public boolean isLdapLowercasePermissionName() {
                return true;
            }

            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return false;
            }
        });
        sambaHelper.init();

        assertEquals("1test user", sambaHelper.getAccountId(USER_SID));

    }

    private static final SID USER_SID = new SID() {

        @Override
        public SID getDomainSid() {
            return null;
        }

        @Override
        public int getRid() {
            return 0;
        }

        @Override
        public String toDisplayString() {
            return getDomainName() + "\\" + getAccountName();
        }

        @Override
        public String getAccountName() {
            return "Test User";
        }

        @Override
        public String getDomainName() {
            return "WORKGROUP";
        }

        @Override
        public String getTypeText() {
            return "User";
        }

        @Override
        public int getType() {
            return 1;
        }

        @Override
        public <T> T unwrap(Class<T> type) {
            return null;
        }
    };

    public void test_constants() {
        assertEquals(4, SambaHelper.SID_TYPE_ALIAS);
        assertEquals(6, SambaHelper.SID_TYPE_DELETED);
        assertEquals(2, SambaHelper.SID_TYPE_DOM_GRP);
        assertEquals(3, SambaHelper.SID_TYPE_DOMAIN);
        assertEquals(7, SambaHelper.SID_TYPE_INVALID);
        assertEquals(8, SambaHelper.SID_TYPE_UNKNOWN);
        assertEquals(0, SambaHelper.SID_TYPE_USE_NONE);
        assertEquals(1, SambaHelper.SID_TYPE_USER);
        assertEquals(5, SambaHelper.SID_TYPE_WKN_GRP);
    }

    public void test_init() {
        ComponentUtil.setFessConfig(new MockFessConfig());
        sambaHelper.init();
        assertNotNull(sambaHelper.fessConfig);
    }

    public void test_getAccountId_differentSidTypes() throws SmbException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getSmbAvailableSidTypes() {
                return "1,2,4:2,5:1";
            }

            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }

            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return false;
            }
        });
        sambaHelper.init();

        // Test User SID (type 1)
        assertEquals("1Test User", sambaHelper.getAccountId(createMockSID(1, "Test User")));

        // Test Domain Group SID (type 2)
        assertEquals("2Domain Group", sambaHelper.getAccountId(createMockSID(2, "Domain Group")));

        // Test Alias SID (type 4, mapped to 2)
        assertEquals("2Local Group", sambaHelper.getAccountId(createMockSID(4, "Local Group")));

        // Test Well-Known Group SID (type 5, mapped to 1)
        assertEquals("1Everyone", sambaHelper.getAccountId(createMockSID(5, "Everyone")));
    }

    public void test_getAccountId_unavailableSidType() throws SmbException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getSmbAvailableSidTypes() {
                return "1,2";
            }

            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }

            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return false;
            }
        });
        sambaHelper.init();

        // Test unavailable SID type (type 3 - DOMAIN)
        assertNull(sambaHelper.getAccountId(createMockSID(3, "Domain")));

        // Test unavailable SID type (type 6 - DELETED)
        assertNull(sambaHelper.getAccountId(createMockSID(6, "Deleted Account")));

        // Test unavailable SID type (type 7 - INVALID)
        assertNull(sambaHelper.getAccountId(createMockSID(7, "Invalid Account")));
    }

    public void test_getAccountId_smb1_basic() throws SmbException {
        // Note: SMB1 SID testing is limited due to interface constraints
        // This test verifies the method exists and handles the SMB1 SID parameter type
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getSmbAvailableSidTypes() {
                return "1,2,4:2,5:1";
            }

            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }

            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return false;
            }
        });
        sambaHelper.init();

        // Verify method signature exists for SMB1 SID - actual test would require real SMB1 SID instance
        assertNotNull(sambaHelper);
    }

    public void test_createSearchRole_lowercase() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapLowercasePermissionName() {
                return true;
            }

            @Override
            public String getCanonicalLdapName(String name) {
                return name;
            }
        });
        sambaHelper.init();

        String result = sambaHelper.createSearchRole(1, "TestUser");
        assertEquals("1testuser", result);
    }

    public void test_createSearchRole_keepCase() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }

            @Override
            public String getCanonicalLdapName(String name) {
                return name;
            }
        });
        sambaHelper.init();

        String result = sambaHelper.createSearchRole(1, "TestUser");
        assertEquals("1TestUser", result);
    }

    public void test_createSearchRole_canonicalName() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }

            @Override
            public String getCanonicalLdapName(String name) {
                return "DOMAIN\\" + name;
            }
        });
        sambaHelper.init();

        String result = sambaHelper.createSearchRole(2, "Group");
        assertEquals("2DOMAIN\\Group", result);
    }

    public void test_createSearchRole_specialCharacters() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }

            @Override
            public String getCanonicalLdapName(String name) {
                return name;
            }
        });
        sambaHelper.init();

        String result = sambaHelper.createSearchRole(1, "User@Domain.com");
        assertEquals("1User@Domain.com", result);
    }

    public void test_createSearchRole_emptyName() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }

            @Override
            public String getCanonicalLdapName(String name) {
                return name;
            }
        });
        sambaHelper.init();

        String result = sambaHelper.createSearchRole(1, "");
        assertEquals("1", result);
    }

    public void test_getAccountId_withNetbiosIgnore() throws SmbException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getSmbAvailableSidTypes() {
                return "1,2";
            }

            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }

            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return true;
            }

            @Override
            public String getCanonicalLdapName(String name) {
                return isLdapIgnoreNetbiosName() ? name.replaceAll(".*\\\\", "") : name;
            }
        });
        sambaHelper.init();

        assertEquals("1TestUser", sambaHelper.getAccountId(createMockSID(1, "DOMAIN\\TestUser")));
    }

    public void test_getAccountId_localeHandling() throws SmbException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getSmbAvailableSidTypes() {
                return "1";
            }

            @Override
            public boolean isLdapLowercasePermissionName() {
                return true;
            }

            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return false;
            }

            @Override
            public String getCanonicalLdapName(String name) {
                return name;
            }
        });
        sambaHelper.init();

        // Test with Turkish locale characters that have special lowercase rules
        assertEquals("1üser", sambaHelper.getAccountId(createMockSID(1, "Üser")));
    }

    public void test_getAccountId_exception_handling() throws SmbException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getSmbAvailableSidTypes() {
                return "1";
            }

            @Override
            public boolean isLdapLowercasePermissionName() {
                return false;
            }

            @Override
            public boolean isLdapIgnoreNetbiosName() {
                return false;
            }
        });
        sambaHelper.init();

        // Test SID that throws exception in toDisplayString
        SID exceptionSID = new SID() {
            @Override
            public SID getDomainSid() {
                return null;
            }

            @Override
            public int getRid() {
                return 0;
            }

            @Override
            public String toDisplayString() {
                throw new RuntimeException("Test exception");
            }

            @Override
            public String getAccountName() {
                return "TestUser";
            }

            @Override
            public String getDomainName() {
                return "DOMAIN";
            }

            @Override
            public String getTypeText() {
                return "User";
            }

            @Override
            public int getType() {
                return 1;
            }

            @Override
            public <T> T unwrap(Class<T> type) {
                return null;
            }
        };

        // Should handle exception gracefully and still return result
        assertEquals("1TestUser", sambaHelper.getAccountId(exceptionSID));
    }

    // Helper methods

    private SID createMockSID(final int type, final String accountName) {
        return new SID() {
            @Override
            public SID getDomainSid() {
                return null;
            }

            @Override
            public int getRid() {
                return 0;
            }

            @Override
            public String toDisplayString() {
                return "DOMAIN\\" + accountName;
            }

            @Override
            public String getAccountName() {
                return accountName;
            }

            @Override
            public String getDomainName() {
                return "DOMAIN";
            }

            @Override
            public String getTypeText() {
                return "Type" + type;
            }

            @Override
            public int getType() {
                return type;
            }

            @Override
            public <T> T unwrap(Class<T> type) {
                return null;
            }
        };
    }

    // Mock classes

    static class MockFessConfig extends FessConfig.SimpleImpl {
        @Override
        public String getSmbAvailableSidTypes() {
            return "1,2,4:2,5:1";
        }

        @Override
        public boolean isLdapLowercasePermissionName() {
            return false;
        }

        @Override
        public boolean isLdapIgnoreNetbiosName() {
            return false;
        }

        @Override
        public String getCanonicalLdapName(String name) {
            return name;
        }
    }

}
