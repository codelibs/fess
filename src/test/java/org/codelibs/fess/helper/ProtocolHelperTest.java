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

public class ProtocolHelperTest extends UnitFessTestCase {
    public void test_add_httpx() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();
        assertEquals(2, protocolHelper.getWebProtocols().length);
        assertEquals("http:", protocolHelper.getWebProtocols()[0]);
        assertEquals("https:", protocolHelper.getWebProtocols()[1]);
        assertEquals(2, protocolHelper.getFileProtocols().length);
        assertEquals("file:", protocolHelper.getFileProtocols()[0]);
        assertEquals("smb:", protocolHelper.getFileProtocols()[1]);

        assertFalse(protocolHelper.isValidWebProtocol("httpx://test"));

        protocolHelper.addWebProtocol("httpx");
        assertEquals(3, protocolHelper.getWebProtocols().length);
        assertEquals("http:", protocolHelper.getWebProtocols()[0]);
        assertEquals("https:", protocolHelper.getWebProtocols()[1]);
        assertEquals("httpx:", protocolHelper.getWebProtocols()[2]);
        assertEquals(2, protocolHelper.getFileProtocols().length);
        assertEquals("file:", protocolHelper.getFileProtocols()[0]);
        assertEquals("smb:", protocolHelper.getFileProtocols()[1]);

        assertTrue(protocolHelper.isValidWebProtocol("httpx://test"));

        protocolHelper.addWebProtocol("httpx");
        assertEquals(3, protocolHelper.getWebProtocols().length);
        assertEquals(2, protocolHelper.getFileProtocols().length);
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    public void test_add_smbx() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();
        assertEquals(2, protocolHelper.getWebProtocols().length);
        assertEquals("http:", protocolHelper.getWebProtocols()[0]);
        assertEquals("https:", protocolHelper.getWebProtocols()[1]);
        assertEquals(2, protocolHelper.getFileProtocols().length);
        assertEquals("file:", protocolHelper.getFileProtocols()[0]);
        assertEquals("smb:", protocolHelper.getFileProtocols()[1]);

        assertFalse(protocolHelper.isValidFileProtocol("smbx://test"));

        protocolHelper.addFileProtocol("smbx");
        assertEquals(2, protocolHelper.getWebProtocols().length);
        assertEquals("http:", protocolHelper.getWebProtocols()[0]);
        assertEquals("https:", protocolHelper.getWebProtocols()[1]);
        assertEquals(3, protocolHelper.getFileProtocols().length);
        assertEquals("file:", protocolHelper.getFileProtocols()[0]);
        assertEquals("smb:", protocolHelper.getFileProtocols()[1]);
        assertEquals("smbx:", protocolHelper.getFileProtocols()[2]);

        assertTrue(protocolHelper.isValidFileProtocol("smbx://test"));

        protocolHelper.addFileProtocol("smbx");
        assertEquals(2, protocolHelper.getWebProtocols().length);
        assertEquals(3, protocolHelper.getFileProtocols().length);
    }

    public void test_loadProtocols() {
        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.loadProtocols("org.codelibs.fess.test.net.protocol");

        assertEquals(1, protocolHelper.getWebProtocols().length);
        assertEquals(1, protocolHelper.getFileProtocols().length);
    }

    public void test_init_emptyProtocols() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertEquals(0, protocolHelper.getWebProtocols().length);
        assertEquals(0, protocolHelper.getFileProtocols().length);
    }

    public void test_init_nullProtocols() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return null;
            }

            @Override
            public String getCrawlerFileProtocols() {
                return null;
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertEquals(0, protocolHelper.getWebProtocols().length);
        assertEquals(0, protocolHelper.getFileProtocols().length);
    }

    public void test_init_withSpaces() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return " http , https , ftp ";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return " file , smb ";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertEquals(3, protocolHelper.getWebProtocols().length);
        assertEquals("http:", protocolHelper.getWebProtocols()[0]);
        assertEquals("https:", protocolHelper.getWebProtocols()[1]);
        assertEquals("ftp:", protocolHelper.getWebProtocols()[2]);

        assertEquals(2, protocolHelper.getFileProtocols().length);
        assertEquals("file:", protocolHelper.getFileProtocols()[0]);
        assertEquals("smb:", protocolHelper.getFileProtocols()[1]);
    }

    public void test_init_withBlankEntries() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,,https,  ,ftp";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file, ,smb,,";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertEquals(3, protocolHelper.getWebProtocols().length);
        assertEquals("http:", protocolHelper.getWebProtocols()[0]);
        assertEquals("https:", protocolHelper.getWebProtocols()[1]);
        assertEquals("ftp:", protocolHelper.getWebProtocols()[2]);

        assertEquals(2, protocolHelper.getFileProtocols().length);
        assertEquals("file:", protocolHelper.getFileProtocols()[0]);
        assertEquals("smb:", protocolHelper.getFileProtocols()[1]);
    }

    public void test_isValidWebProtocol_validUrls() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https,ftp";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertTrue(protocolHelper.isValidWebProtocol("http://example.com"));
        assertTrue(protocolHelper.isValidWebProtocol("https://example.com"));
        assertTrue(protocolHelper.isValidWebProtocol("ftp://example.com"));
        assertTrue(protocolHelper.isValidWebProtocol("http://"));
        assertTrue(protocolHelper.isValidWebProtocol("https://test.local/path"));
    }

    public void test_isValidWebProtocol_invalidUrls() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertFalse(protocolHelper.isValidWebProtocol("ftp://example.com"));
        assertFalse(protocolHelper.isValidWebProtocol("file://example.com"));
        assertFalse(protocolHelper.isValidWebProtocol("smb://example.com"));
        assertFalse(protocolHelper.isValidWebProtocol("ldap://example.com"));
        assertFalse(protocolHelper.isValidWebProtocol("example.com"));
        assertFalse(protocolHelper.isValidWebProtocol(""));
        assertFalse(protocolHelper.isValidWebProtocol("xyz"));
    }

    public void test_isValidFileProtocol_validUrls() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb,ftp";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertTrue(protocolHelper.isValidFileProtocol("file:///path/to/file"));
        assertTrue(protocolHelper.isValidFileProtocol("smb://server/share"));
        assertTrue(protocolHelper.isValidFileProtocol("ftp://server/path"));
        assertTrue(protocolHelper.isValidFileProtocol("file://"));
    }

    public void test_isValidFileProtocol_invalidUrls() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertFalse(protocolHelper.isValidFileProtocol("http://example.com"));
        assertFalse(protocolHelper.isValidFileProtocol("https://example.com"));
        assertFalse(protocolHelper.isValidFileProtocol("ftp://example.com"));
        assertFalse(protocolHelper.isValidFileProtocol("ldap://example.com"));
        assertFalse(protocolHelper.isValidFileProtocol("example.com"));
        assertFalse(protocolHelper.isValidFileProtocol(""));
        assertFalse(protocolHelper.isValidFileProtocol("xyz"));
    }

    public void test_addWebProtocol_newProtocol() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertEquals(1, protocolHelper.getWebProtocols().length);
        assertFalse(protocolHelper.isValidWebProtocol("https://example.com"));

        protocolHelper.addWebProtocol("https");
        assertEquals(2, protocolHelper.getWebProtocols().length);
        assertEquals("https:", protocolHelper.getWebProtocols()[1]);
        assertTrue(protocolHelper.isValidWebProtocol("https://example.com"));
    }

    public void test_addWebProtocol_duplicateProtocol() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertEquals(2, protocolHelper.getWebProtocols().length);

        protocolHelper.addWebProtocol("http");
        assertEquals(2, protocolHelper.getWebProtocols().length); // Should not increase

        protocolHelper.addWebProtocol("https");
        assertEquals(2, protocolHelper.getWebProtocols().length); // Should not increase
    }

    public void test_addFileProtocol_newProtocol() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertEquals(1, protocolHelper.getFileProtocols().length);
        assertFalse(protocolHelper.isValidFileProtocol("smb://server/share"));

        protocolHelper.addFileProtocol("smb");
        assertEquals(2, protocolHelper.getFileProtocols().length);
        assertEquals("smb:", protocolHelper.getFileProtocols()[1]);
        assertTrue(protocolHelper.isValidFileProtocol("smb://server/share"));
    }

    public void test_addFileProtocol_duplicateProtocol() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertEquals(2, protocolHelper.getFileProtocols().length);

        protocolHelper.addFileProtocol("file");
        assertEquals(2, protocolHelper.getFileProtocols().length); // Should not increase

        protocolHelper.addFileProtocol("smb");
        assertEquals(2, protocolHelper.getFileProtocols().length); // Should not increase
    }

    public void test_addWebProtocol_emptyString() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        int originalLength = protocolHelper.getWebProtocols().length;
        protocolHelper.addWebProtocol("");
        assertEquals(originalLength + 1, protocolHelper.getWebProtocols().length);
        assertEquals(":", protocolHelper.getWebProtocols()[originalLength]);
    }

    public void test_addFileProtocol_emptyString() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        int originalLength = protocolHelper.getFileProtocols().length;
        protocolHelper.addFileProtocol("");
        assertEquals(originalLength + 1, protocolHelper.getFileProtocols().length);
        assertEquals(":", protocolHelper.getFileProtocols()[originalLength]);
    }

    public void test_loadProtocols_invalidPackage() {
        final ProtocolHelper protocolHelper = new ProtocolHelper();

        // Should not throw exception with invalid package
        protocolHelper.loadProtocols("org.invalid.package.does.not.exist");

        // Should have empty arrays initially
        assertEquals(0, protocolHelper.getWebProtocols().length);
        assertEquals(0, protocolHelper.getFileProtocols().length);
    }

    public void test_loadProtocols_emptyPackage() {
        final ProtocolHelper protocolHelper = new ProtocolHelper();

        // Should not throw exception with empty package
        protocolHelper.loadProtocols("");

        assertEquals(0, protocolHelper.getWebProtocols().length);
        assertEquals(0, protocolHelper.getFileProtocols().length);
    }

    public void test_isValidWebProtocol_nullUrl() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        try {
            protocolHelper.isValidWebProtocol(null);
            fail("Should throw exception for null URL");
        } catch (Exception e) {
            // Expected
            assertNotNull(e);
        }
    }

    public void test_isValidFileProtocol_nullUrl() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        try {
            protocolHelper.isValidFileProtocol(null);
            fail("Should throw exception for null URL");
        } catch (Exception e) {
            // Expected
            assertNotNull(e);
        }
    }

    public void test_getProtocols_arrayImmutability() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        String[] webProtocols = protocolHelper.getWebProtocols();
        String[] fileProtocols = protocolHelper.getFileProtocols();

        assertEquals(2, webProtocols.length);
        assertEquals(2, fileProtocols.length);

        // Verify the content is correct
        assertEquals("http:", webProtocols[0]);
        assertEquals("https:", webProtocols[1]);
        assertEquals("file:", fileProtocols[0]);
        assertEquals("smb:", fileProtocols[1]);
    }

    public void test_multipleAddProtocols() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertEquals(1, protocolHelper.getWebProtocols().length);
        assertEquals(1, protocolHelper.getFileProtocols().length);

        protocolHelper.addWebProtocol("https");
        protocolHelper.addWebProtocol("ftp");
        protocolHelper.addFileProtocol("smb");
        protocolHelper.addFileProtocol("nfs");

        assertEquals(3, protocolHelper.getWebProtocols().length);
        assertEquals(3, protocolHelper.getFileProtocols().length);

        assertTrue(protocolHelper.isValidWebProtocol("https://example.com"));
        assertTrue(protocolHelper.isValidWebProtocol("ftp://example.com"));
        assertTrue(protocolHelper.isValidFileProtocol("smb://server/share"));
        assertTrue(protocolHelper.isValidFileProtocol("nfs://server/path"));
    }

    public void test_specialCharactersInProtocols() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http-s,web+dav";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file_v2,smb.v3";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertEquals(2, protocolHelper.getWebProtocols().length);
        assertEquals("http-s:", protocolHelper.getWebProtocols()[0]);
        assertEquals("web+dav:", protocolHelper.getWebProtocols()[1]);

        assertEquals(2, protocolHelper.getFileProtocols().length);
        assertEquals("file_v2:", protocolHelper.getFileProtocols()[0]);
        assertEquals("smb.v3:", protocolHelper.getFileProtocols()[1]);

        assertTrue(protocolHelper.isValidWebProtocol("http-s://example.com"));
        assertTrue(protocolHelper.isValidWebProtocol("web+dav://example.com"));
        assertTrue(protocolHelper.isValidFileProtocol("file_v2://path"));
        assertTrue(protocolHelper.isValidFileProtocol("smb.v3://server"));
    }
}
