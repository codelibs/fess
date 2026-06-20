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
package org.codelibs.fess.mylasta.direction;

import java.util.List;

import org.codelibs.core.misc.Pair;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class FessPropCorsTest extends UnitFessTestCase {

    private FessConfig jsonConfig(final String raw) {
        return new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getApiJsonResponseHeaders() {
                return raw;
            }
        };
    }

    private FessConfig originConfig(final String raw) {
        return new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getApiCorsAllowOrigin() {
                return raw;
            }
        };
    }

    @Test
    public void test_jsonResponseHeaders_stripsCorsAndTimingAllow() {
        final List<Pair<String, String>> list = jsonConfig("Referrer-Policy:strict-origin-when-cross-origin\n"
                + "Access-Control-Allow-Origin:*\n" + "access-control-allow-credentials:true\n" + "Access-Control-Expose-Headers:X-Foo\n"
                + "Timing-Allow-Origin:*\n" + " TIMING-ALLOW-ORIGIN : https://evil ").getApiJsonResponseHeaderList();

        assertEquals(1, list.size());
        assertEquals("Referrer-Policy", list.get(0).getFirst());
    }

    @Test
    public void test_gsaResponseHeaders_stripsCors() {
        final List<Pair<String, String>> list = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getApiGsaResponseHeaders() {
                return "X-Foo:bar\nAccess-Control-Allow-Methods:GET";
            }
        }.getApiGsaResponseHeaderList();
        assertEquals(1, list.size());
        assertEquals("X-Foo", list.get(0).getFirst());
    }

    @Test
    public void test_dashboardResponseHeaders_stripsCors() {
        final List<Pair<String, String>> list = new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getApiDashboardResponseHeaders() {
                return "Access-Control-Allow-Origin:https://evil\nX-Bar:baz";
            }
        }.getApiDashboardResponseHeaderList();
        assertEquals(1, list.size());
        assertEquals("X-Bar", list.get(0).getFirst());
    }

    @Test
    public void test_corsAllowOrigin_newlineAndCommaAndTrim() {
        final List<String> list = originConfig("https://a.example\n https://b.example , https://c.example").getApiCorsAllowOriginList();
        assertEquals(List.of("https://a.example", "https://b.example", "https://c.example"), list);
    }

    @Test
    public void test_corsAllowOrigin_dropsLiteralNullCaseInsensitive() {
        final List<String> list = originConfig("null\nNULL\nNull , https://ok.example").getApiCorsAllowOriginList();
        assertEquals(List.of("https://ok.example"), list);
    }

    @Test
    public void test_corsAllowOrigin_dropsEmptyEntries() {
        final List<String> list = originConfig("\n , \nhttps://ok.example\n,").getApiCorsAllowOriginList();
        assertEquals(List.of("https://ok.example"), list);
    }

    @Test
    public void test_corsAllowOrigin_wildcardAndExplicitCoexist() {
        final List<String> list = originConfig("*, https://app.example.com").getApiCorsAllowOriginList();
        assertEquals(List.of("*", "https://app.example.com"), list);
    }
}
