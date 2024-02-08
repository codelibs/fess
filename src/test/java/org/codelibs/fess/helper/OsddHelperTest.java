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
}
