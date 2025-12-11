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

    public void test_s3_gcs_protocols() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb,s3,gcs";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertEquals(4, protocolHelper.getFileProtocols().length);
        assertEquals("file:", protocolHelper.getFileProtocols()[0]);
        assertEquals("smb:", protocolHelper.getFileProtocols()[1]);
        assertEquals("s3:", protocolHelper.getFileProtocols()[2]);
        assertEquals("gcs:", protocolHelper.getFileProtocols()[3]);

        assertTrue(protocolHelper.isValidFileProtocol("s3://bucket/path/to/file"));
        assertTrue(protocolHelper.isValidFileProtocol("gcs://bucket/path/to/file"));
        assertTrue(protocolHelper.isValidFileProtocol("s3://my-bucket/"));
        assertTrue(protocolHelper.isValidFileProtocol("gcs://my-bucket/folder/document.pdf"));

        assertFalse(protocolHelper.isValidWebProtocol("s3://bucket/path"));
        assertFalse(protocolHelper.isValidWebProtocol("gcs://bucket/path"));
    }

    public void test_s3_gcs_protocols_add_dynamically() {
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

        // Initially s3 and gcs should not be valid
        assertFalse(protocolHelper.isValidFileProtocol("s3://bucket/path"));
        assertFalse(protocolHelper.isValidFileProtocol("gcs://bucket/path"));

        // Add s3 protocol dynamically
        protocolHelper.addFileProtocol("s3");
        assertTrue(protocolHelper.isValidFileProtocol("s3://bucket/path"));
        assertFalse(protocolHelper.isValidFileProtocol("gcs://bucket/path"));

        // Add gcs protocol dynamically
        protocolHelper.addFileProtocol("gcs");
        assertTrue(protocolHelper.isValidFileProtocol("s3://bucket/path"));
        assertTrue(protocolHelper.isValidFileProtocol("gcs://bucket/path"));

        assertEquals(4, protocolHelper.getFileProtocols().length);
    }

    public void test_s3_gcs_protocols_various_urls() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb,ftp,storage,s3,gcs";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        // S3 URLs
        assertTrue(protocolHelper.isValidFileProtocol("s3://bucket/"));
        assertTrue(protocolHelper.isValidFileProtocol("s3://my-bucket/path"));
        assertTrue(protocolHelper.isValidFileProtocol("s3://bucket.with.dots/path"));
        assertTrue(protocolHelper.isValidFileProtocol("s3://bucket-with-dashes/path"));
        assertTrue(protocolHelper.isValidFileProtocol("s3://123bucket/path"));
        assertTrue(protocolHelper.isValidFileProtocol("s3://bucket/path/to/deep/nested/file.txt"));
        assertTrue(protocolHelper.isValidFileProtocol("s3://bucket/path/ファイル.txt"));
        assertTrue(protocolHelper.isValidFileProtocol("s3://bucket/path/file%20with%20spaces.txt"));

        // GCS URLs
        assertTrue(protocolHelper.isValidFileProtocol("gcs://bucket/"));
        assertTrue(protocolHelper.isValidFileProtocol("gcs://my-bucket/path"));
        assertTrue(protocolHelper.isValidFileProtocol("gcs://bucket.with.dots/path"));
        assertTrue(protocolHelper.isValidFileProtocol("gcs://bucket-with-dashes/path"));
        assertTrue(protocolHelper.isValidFileProtocol("gcs://bucket_with_underscores/path"));
        assertTrue(protocolHelper.isValidFileProtocol("gcs://bucket/path/to/deep/nested/file.txt"));
        assertTrue(protocolHelper.isValidFileProtocol("gcs://bucket/path/ファイル.txt"));
        assertTrue(protocolHelper.isValidFileProtocol("gcs://bucket/path/file%20with%20spaces.txt"));
    }

    public void test_s3_gcs_protocols_invalid_urls() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb,ftp,storage,s3,gcs";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        // These should NOT be valid file protocols
        assertFalse(protocolHelper.isValidFileProtocol("S3://bucket/path")); // uppercase
        assertFalse(protocolHelper.isValidFileProtocol("GCS://bucket/path")); // uppercase
        assertFalse(protocolHelper.isValidFileProtocol("http://example.com")); // web protocol
        assertFalse(protocolHelper.isValidFileProtocol("https://example.com")); // web protocol
        assertFalse(protocolHelper.isValidFileProtocol("bucket/path")); // no protocol
        assertFalse(protocolHelper.isValidFileProtocol("")); // empty
        assertFalse(protocolHelper.isValidFileProtocol("s4://bucket/path")); // similar but not s3
        assertFalse(protocolHelper.isValidFileProtocol("gcss://bucket/path")); // similar but not gcs
    }

    public void test_all_file_protocols_together() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getCrawlerWebProtocols() {
                return "http,https";
            }

            @Override
            public String getCrawlerFileProtocols() {
                return "file,smb,smb1,ftp,storage,s3,gcs";
            }
        });

        final ProtocolHelper protocolHelper = new ProtocolHelper();
        protocolHelper.init();

        assertEquals(7, protocolHelper.getFileProtocols().length);

        // All file protocols should be valid
        assertTrue(protocolHelper.isValidFileProtocol("file:///path/to/file"));
        assertTrue(protocolHelper.isValidFileProtocol("smb://server/share"));
        assertTrue(protocolHelper.isValidFileProtocol("smb1://server/share"));
        assertTrue(protocolHelper.isValidFileProtocol("ftp://ftp.example.com/file"));
        assertTrue(protocolHelper.isValidFileProtocol("storage://container/blob"));
        assertTrue(protocolHelper.isValidFileProtocol("s3://bucket/key"));
        assertTrue(protocolHelper.isValidFileProtocol("gcs://bucket/object"));

        // Web protocols should NOT be valid as file protocols
        assertFalse(protocolHelper.isValidFileProtocol("http://example.com"));
        assertFalse(protocolHelper.isValidFileProtocol("https://example.com"));
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

    // ==================================================================================
    //                                                              isFilePathProtocol Tests
    //                                                              =========================

    public void test_isFilePathProtocol_validProtocols() {
        final ProtocolHelper protocolHelper = new ProtocolHelper();

        assertTrue(protocolHelper.isFilePathProtocol("smb://server/share"));
        assertTrue(protocolHelper.isFilePathProtocol("smb1://server/share"));
        assertTrue(protocolHelper.isFilePathProtocol("file:///path/to/file"));
        assertTrue(protocolHelper.isFilePathProtocol("file:/path/to/file"));
        assertTrue(protocolHelper.isFilePathProtocol("ftp://ftp.example.com/file"));
        assertTrue(protocolHelper.isFilePathProtocol("s3://bucket/path"));
        assertTrue(protocolHelper.isFilePathProtocol("gcs://bucket/path"));
    }

    public void test_isFilePathProtocol_invalidProtocols() {
        final ProtocolHelper protocolHelper = new ProtocolHelper();

        assertFalse(protocolHelper.isFilePathProtocol("http://example.com"));
        assertFalse(protocolHelper.isFilePathProtocol("https://example.com"));
        assertFalse(protocolHelper.isFilePathProtocol("storage://container/blob"));
        assertFalse(protocolHelper.isFilePathProtocol("ldap://server"));
        assertFalse(protocolHelper.isFilePathProtocol(""));
        assertFalse(protocolHelper.isFilePathProtocol("not-a-url"));
    }

    // ==================================================================================
    //                                                              isFileSystemPath Tests
    //                                                              =======================

    public void test_isFileSystemPath_validProtocols() {
        final ProtocolHelper protocolHelper = new ProtocolHelper();

        assertTrue(protocolHelper.isFileSystemPath("file:///path/to/file"));
        assertTrue(protocolHelper.isFileSystemPath("file:/path/to/file"));
        assertTrue(protocolHelper.isFileSystemPath("smb://server/share"));
        assertTrue(protocolHelper.isFileSystemPath("smb1://server/share"));
        assertTrue(protocolHelper.isFileSystemPath("ftp://ftp.example.com/file"));
        assertTrue(protocolHelper.isFileSystemPath("storage://container/blob"));
        assertTrue(protocolHelper.isFileSystemPath("s3://bucket/path"));
        assertTrue(protocolHelper.isFileSystemPath("gcs://bucket/path"));
    }

    public void test_isFileSystemPath_invalidProtocols() {
        final ProtocolHelper protocolHelper = new ProtocolHelper();

        assertFalse(protocolHelper.isFileSystemPath("http://example.com"));
        assertFalse(protocolHelper.isFileSystemPath("https://example.com"));
        assertFalse(protocolHelper.isFileSystemPath("ldap://server"));
        assertFalse(protocolHelper.isFileSystemPath("mailto:test@example.com"));
        assertFalse(protocolHelper.isFileSystemPath(""));
        assertFalse(protocolHelper.isFileSystemPath("not-a-url"));
    }

    // ==================================================================================
    //                                                              shouldSkipUrlDecode Tests
    //                                                              ==========================

    public void test_shouldSkipUrlDecode_skipProtocols() {
        final ProtocolHelper protocolHelper = new ProtocolHelper();

        assertTrue(protocolHelper.shouldSkipUrlDecode("smb://server/share"));
        assertTrue(protocolHelper.shouldSkipUrlDecode("smb1://server/share"));
        assertTrue(protocolHelper.shouldSkipUrlDecode("ftp://ftp.example.com/file"));
        assertTrue(protocolHelper.shouldSkipUrlDecode("s3://bucket/path"));
        assertTrue(protocolHelper.shouldSkipUrlDecode("gcs://bucket/path"));
    }

    public void test_shouldSkipUrlDecode_decodeProtocols() {
        final ProtocolHelper protocolHelper = new ProtocolHelper();

        assertFalse(protocolHelper.shouldSkipUrlDecode("file:///path/to/file"));
        assertFalse(protocolHelper.shouldSkipUrlDecode("http://example.com"));
        assertFalse(protocolHelper.shouldSkipUrlDecode("https://example.com"));
        assertFalse(protocolHelper.shouldSkipUrlDecode("storage://container/blob"));
        assertFalse(protocolHelper.shouldSkipUrlDecode(""));
        assertFalse(protocolHelper.shouldSkipUrlDecode("not-a-url"));
    }

    // ==================================================================================
    //                                                              hasKnownProtocol Tests
    //                                                              =======================

    public void test_hasKnownProtocol_knownProtocols() {
        final ProtocolHelper protocolHelper = new ProtocolHelper();

        assertTrue(protocolHelper.hasKnownProtocol("http://example.com"));
        assertTrue(protocolHelper.hasKnownProtocol("https://example.com"));
        assertTrue(protocolHelper.hasKnownProtocol("smb://server/share"));
        assertTrue(protocolHelper.hasKnownProtocol("smb1://server/share"));
        assertTrue(protocolHelper.hasKnownProtocol("ftp://ftp.example.com/file"));
        assertTrue(protocolHelper.hasKnownProtocol("storage://container/blob"));
        assertTrue(protocolHelper.hasKnownProtocol("s3://bucket/path"));
        assertTrue(protocolHelper.hasKnownProtocol("gcs://bucket/path"));
    }

    public void test_hasKnownProtocol_unknownProtocols() {
        final ProtocolHelper protocolHelper = new ProtocolHelper();

        assertFalse(protocolHelper.hasKnownProtocol("file:///path/to/file"));
        assertFalse(protocolHelper.hasKnownProtocol("ldap://server"));
        assertFalse(protocolHelper.hasKnownProtocol("mailto:test@example.com"));
        assertFalse(protocolHelper.hasKnownProtocol("/local/path"));
        assertFalse(protocolHelper.hasKnownProtocol("www.example.com"));
        assertFalse(protocolHelper.hasKnownProtocol(""));
        assertFalse(protocolHelper.hasKnownProtocol("not-a-url"));
    }
}
