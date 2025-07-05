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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codelibs.fess.helper;

import java.util.Base64;

import org.codelibs.fess.entity.RequestParameter;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.http.Cookie;

public class SearchHelperTest extends UnitFessTestCase {
    private SearchHelper searchHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        searchHelper = new SearchHelper();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getCookieSearchParameterRequiredKeys() {
                return "";
            }

            @Override
            public String getCookieSearchParameterKeys() {
                return "q,lang";
            }

            @Override
            public Integer getCookieSearchParameterMaxLengthAsInteger() {
                return 4096;
            }

            @Override
            public String getCookieSearchParameterName() {
                return "FESS_SEARCH_PARAM";
            }

            @Override
            public String getCookieSearchParameterHttpOnly() {
                return "true";
            }

            @Override
            public String getCookieSearchParameterSecure() {
                return "";
            }

            @Override
            public String getCookieSearchParameterDomain() {
                return "";
            }

            @Override
            public String getCookieSearchParameterPath() {
                return "/";
            }

            @Override
            public Integer getCookieSearchParameterMaxAgeAsInteger() {
                return 3600;
            }

            @Override
            public String getCookieSearchParameterSameSite() {
                return "Lax";
            }
        });
    }

    public void test_serializeParameters() {
        RequestParameter[] params = new RequestParameter[] { new RequestParameter("q", new String[] { "test" }),
                new RequestParameter("lang", new String[] { "en", "ja" }) };
        String encoded = searchHelper.serializeParameters(params);
        assertNotNull(encoded);
        byte[] compressed = Base64.getUrlDecoder().decode(encoded);
        byte[] jsonBytes = searchHelper.gzipDecompress(compressed);
        String json = new String(jsonBytes);
        assertTrue(json.contains("q"));
        assertTrue(json.contains("lang"));
    }

    public void test_store_and_getSearchParameters() {
        getMockRequest().setParameter("q", "test");
        getMockRequest().setParameter("lang", new String[] { "en", "ja" });
        searchHelper.storeSearchParameters();
        Cookie[] cookies = getMockResponse().getCookies();
        boolean found = false;
        for (Cookie cookie : cookies) {
            if ("FESS_SEARCH_PARAM".equals(cookie.getName())) {
                found = true;
                assertNotNull(cookie.getValue());
            }
        }
        assertTrue(found);
        getMockRequest().addCookie(cookies[0]);
        RequestParameter[] result = searchHelper.getSearchParameters();
        assertEquals(2, result.length);
        assertEquals("q", result[0].getName());
        assertEquals("test", result[0].getValues()[0]);
        assertEquals("lang", result[1].getName());
        assertEquals("en", result[1].getValues()[0]);
        assertEquals("ja", result[1].getValues()[1]);
    }
}
