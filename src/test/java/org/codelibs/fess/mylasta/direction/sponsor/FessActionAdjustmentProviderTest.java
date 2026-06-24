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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class FessActionAdjustmentProviderTest extends UnitFessTestCase {

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Test
    public void test_write_noConfig() throws Exception {
        final FessConfig fessConfig = createFessConfigWithResponseHeaders("");
        FessActionAdjustmentProvider provider = new FessActionAdjustmentProvider(fessConfig);
        final Map<String, String> headerMap = new HashMap<>();
        provider.adjustActionResponseHeaders("text/plain", (k, v) -> headerMap.put(k, v));

        assertEquals(0, headerMap.size());
    }

    @Test
    public void test_write_defaultHeadersOnly() throws Exception {
        final FessConfig fessConfig = createFessConfigWithResponseHeaders("*=X-Def:def\n*=X-Def2:def2");
        FessActionAdjustmentProvider provider = new FessActionAdjustmentProvider(fessConfig);
        final Map<String, String> headerMap = new HashMap<>();
        provider.adjustActionResponseHeaders("application/xml", (k, v) -> headerMap.put(k, v));
        assertEquals(2, headerMap.size());
        assertEquals("def", headerMap.get("X-Def"));
        assertEquals("def2", headerMap.get("X-Def2"));
    }

    @Test
    public void test_write_defaultAndSpecificHtml() throws Exception {
        final FessConfig fessConfig = createFessConfigWithResponseHeaders("*=X-Def:def\ntext/html=X-Html:htmlval");
        FessActionAdjustmentProvider provider = new FessActionAdjustmentProvider(fessConfig);
        final Map<String, String> headerMap = new HashMap<>();
        provider.adjustActionResponseHeaders("text/html", (k, v) -> headerMap.put(k, v));
        assertEquals(2, headerMap.size());
        assertEquals("def", headerMap.get("X-Def"));
        assertEquals("htmlval", headerMap.get("X-Html"));
    }

    @Test
    public void test_write_defaultAndSpecificJson() throws Exception {
        final FessConfig fessConfig = createFessConfigWithResponseHeaders("*=X-Def:def\napplication/json=X-Json:jsonval");
        FessActionAdjustmentProvider provider = new FessActionAdjustmentProvider(fessConfig);
        final Map<String, String> headerMap = new HashMap<>();
        provider.adjustActionResponseHeaders("application/json", (k, v) -> headerMap.put(k, v));
        assertEquals(2, headerMap.size());
        assertEquals("def", headerMap.get("X-Def"));
        assertEquals("jsonval", headerMap.get("X-Json"));
    }

    @Test
    public void test_write_contentTypeMismatch() throws Exception {
        final FessConfig fessConfig = createFessConfigWithResponseHeaders("*=X-Def:def\ntext/html=X-Html:htmlval");
        FessActionAdjustmentProvider provider = new FessActionAdjustmentProvider(fessConfig);
        final Map<String, String> headerMap = new HashMap<>();
        provider.adjustActionResponseHeaders("application/json", (k, v) -> headerMap.put(k, v));
        assertEquals(1, headerMap.size());
        assertEquals("def", headerMap.get("X-Def"));
    }

    private FessConfig createFessConfigWithResponseHeaders(final String text) {
        return new FessConfig.SimpleImpl() {
            @Override
            public String getResponseHeaders() {
                return text;
            }
        };
    }

    // --- CORS deny-set tests ---

    @Test
    public void test_parse_stripsCorsHeaders_global() {
        final FessConfig fessConfig =
                createFessConfigWithResponseHeaders("*=Access-Control-Allow-Origin:https://evil\n*=X-Custom:ok\n*=Timing-Allow-Origin:*");
        final FessActionAdjustmentProvider provider = new FessActionAdjustmentProvider(fessConfig);
        final List<String> emitted = collectHeaders(provider, "application/json");
        assertEquals(List.of("X-Custom=ok"), emitted);
    }

    @Test
    public void test_parse_stripsCorsHeaders_perMime() {
        final FessConfig fessConfig = createFessConfigWithResponseHeaders(
                "text/html=Access-Control-Allow-Credentials:true\ntext/html=X-Frame-Options:SAMEORIGIN");
        final FessActionAdjustmentProvider provider = new FessActionAdjustmentProvider(fessConfig);
        final List<String> emitted = collectHeaders(provider, "text/html");
        assertEquals(List.of("X-Frame-Options=SAMEORIGIN"), emitted);
    }

    @Test
    public void test_parse_keepsNonCorsHeaders() {
        final FessConfig fessConfig = createFessConfigWithResponseHeaders("*=Referrer-Policy:strict-origin-when-cross-origin");
        final FessActionAdjustmentProvider provider = new FessActionAdjustmentProvider(fessConfig);
        final List<String> emitted = collectHeaders(provider, "application/json");
        assertEquals(List.of("Referrer-Policy=strict-origin-when-cross-origin"), emitted);
    }

    @Test
    public void test_parse_varyIsKept_forMergeByContainer() {
        // Vary is NOT in the deny set; it is allowed through so the container merges it with CorsFilter's Vary: Origin.
        final FessConfig fessConfig = createFessConfigWithResponseHeaders("*=Vary:Accept-Encoding");
        final FessActionAdjustmentProvider provider = new FessActionAdjustmentProvider(fessConfig);
        final List<String> emitted = collectHeaders(provider, "application/json");
        assertEquals(List.of("Vary=Accept-Encoding"), emitted);
    }

    private List<String> collectHeaders(final FessActionAdjustmentProvider provider, final String mimeType) {
        final List<String> emitted = new ArrayList<>();
        provider.adjustActionResponseHeaders(mimeType, (k, v) -> emitted.add(k + "=" + v));
        return emitted;
    }
}
