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
import java.io.InputStream;
import java.io.OutputStream;

import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.servlet.request.stream.WrittenStreamOut;

public class OsddHelperTest extends UnitFessTestCase {

    public void test_init_nofile() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setContentType("application/opensearchdescription+xml");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());

        try {
            osddHelper.asStream();
            fail();
        } catch (final Exception e) {
            assertEquals("Unsupported Open Search Description Document response.", e.getMessage());
        }
    }

    public void test_init_osddpath() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.setEncoding(Constants.UTF_8);
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());

        final StreamResponse streamResponse = osddHelper.asStream();
        assertEquals("text/xml; charset=UTF-8", streamResponse.getContentType());
        streamResponse.getStreamCall().callback(new WrittenStreamOut() {

            @Override
            public void write(final InputStream ins) throws IOException {
                assertEquals("""
                        <?xml version="1.0" encoding="UTF-8"?>
                        <OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/">
                          <ShortName>Fess</ShortName>
                          <Description>Full Text Search for Your Documents.</Description>
                          <Tags>Full Text Search</Tags>
                          <Contact>fess-user@lists.sourceforge.jp</Contact>
                          <SearchForm>http://localhost:8080/fess/</SearchForm>
                          <Url type="text/html" template="http://localhost:8080/fess/search?q={searchTerms}"/>
                          <InputEncoding>UTF-8</InputEncoding>
                          <OutputEncoding>UTF-8</OutputEncoding>
                        </OpenSearchDescription>
                        """, new String(InputStreamUtil.getBytes(ins)));
            }

            @Override
            public OutputStream stream() {
                return null;
            }
        });
    }

    public void test_init_osddpath_null() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/none.xml");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_init_disabled() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "false";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_init_saml() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "saml";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_init_force() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "true";
            }

            @Override
            public String getSsoType() {
                return "saml";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());
    }

    public void test_setOsddPath() {
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("test/path/osdd.xml");
        assertEquals("test/path/osdd.xml", osddHelper.osddPath);
    }

    public void test_setEncoding() {
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setEncoding("ISO-8859-1");
        assertEquals("ISO-8859-1", osddHelper.encoding);
    }

    public void test_setContentType() {
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setContentType("application/xml");
        assertEquals("application/xml", osddHelper.contentType);
    }

    public void test_isOsddLinkEnabled_blank_ssoType() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_isOsddLinkEnabled_null_ssoType() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return null;
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_init_blank_osddPath() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_init_null_osddPath() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath(null);
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_asStream_customContentType() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.setContentType("application/opensearchdescription+xml");
        osddHelper.setEncoding("UTF-8");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());

        final StreamResponse streamResponse = osddHelper.asStream();
        assertEquals("application/opensearchdescription+xml; charset=UTF-8", streamResponse.getContentType());
    }

    public void test_asStream_customEncoding() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.setEncoding("ISO-8859-1");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());

        final StreamResponse streamResponse = osddHelper.asStream();
        assertEquals("text/xml; charset=ISO-8859-1", streamResponse.getContentType());
    }

    public void test_init_osddLinkEnabled_case_insensitive() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "TRUE";
            }

            @Override
            public String getSsoType() {
                return "saml";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());
    }

    public void test_init_auto_case_insensitive() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "AUTO";
            }

            @Override
            public String getSsoType() {
                return "NONE";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());
    }

    public void test_init_unknown_osddLinkEnabled() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "unknown";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_init_without_post_construct() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        assertFalse(osddHelper.hasOpenSearchFile());
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());
    }

    public void test_isOsddLinkEnabled_false() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "false";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_isOsddLinkEnabled_with_oauth() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "oauth";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_isOsddLinkEnabled_with_oidc() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "oidc";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_default_encoding() {
        final OsddHelper osddHelper = new OsddHelper();
        assertEquals(Constants.UTF_8, osddHelper.encoding);
    }

    public void test_default_contentType() {
        final OsddHelper osddHelper = new OsddHelper();
        assertEquals("text/xml", osddHelper.contentType);
    }

    public void test_asStream_content_type_formatting() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.setContentType("application/opensearchdescription+xml");
        osddHelper.setEncoding("UTF-8");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());

        final StreamResponse streamResponse = osddHelper.asStream();
        assertEquals("application/opensearchdescription+xml; charset=UTF-8", streamResponse.getContentType());
    }

    public void test_asStream_without_charset() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.setContentType("application/opensearchdescription+xml");
        osddHelper.setEncoding(null);
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());

        final StreamResponse streamResponse = osddHelper.asStream();
        assertEquals("application/opensearchdescription+xml; charset=null", streamResponse.getContentType());
    }

    public void test_asStream_exception_handling() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/nonexistent.xml");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());

        try {
            osddHelper.asStream();
            fail("Should throw exception when osddFile is null");
        } catch (final Exception e) {
            assertEquals("Unsupported Open Search Description Document response.", e.getMessage());
        }
    }

    public void test_getOsddFile_null_path() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath(null);
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_getOsddFile_empty_path() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_getOsddFile_whitespace_path() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("   ");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_osddLinkEnabled_case_variations() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "TrUe";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());
    }

    public void test_ssoType_case_variations() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "NoNe";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());
    }

    public void test_asStream_file_name_handling() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.setContentType("application/opensearchdescription+xml");
        osddHelper.setEncoding("UTF-8");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());

        final StreamResponse streamResponse = osddHelper.asStream();
        assertNotNull(streamResponse);
        assertEquals("application/opensearchdescription+xml; charset=UTF-8", streamResponse.getContentType());
    }

    public void test_multiple_init_calls() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());

        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());
    }

    public void test_init_disabled_then_enabled() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "false";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());

        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "true";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());
    }

    public void test_path_change_after_init() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());

        osddHelper.setOsddPath("osdd/nonexistent.xml");
        osddHelper.init();
        assertFalse(osddHelper.hasOpenSearchFile());
    }

    public void test_encoding_change_after_init() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.setEncoding("UTF-8");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());

        osddHelper.setEncoding("ISO-8859-1");
        final StreamResponse streamResponse = osddHelper.asStream();
        assertEquals("text/xml; charset=ISO-8859-1", streamResponse.getContentType());
    }

    public void test_contentType_change_after_init() throws IOException {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOsddLinkEnabled() {
                return "auto";
            }

            @Override
            public String getSsoType() {
                return "none";
            }
        });
        final OsddHelper osddHelper = new OsddHelper();
        osddHelper.setOsddPath("osdd/osdd.xml");
        osddHelper.setContentType("text/xml");
        osddHelper.init();
        assertTrue(osddHelper.hasOpenSearchFile());

        osddHelper.setContentType("application/opensearchdescription+xml");
        final StreamResponse streamResponse = osddHelper.asStream();
        assertEquals("application/opensearchdescription+xml; charset=UTF-8", streamResponse.getContentType());
    }
}
