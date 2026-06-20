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
package org.codelibs.fess.cors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import jakarta.servlet.http.HttpServletResponse;

public class DefaultCorsHandlerTest extends UnitFessTestCase {

    private DefaultCorsHandler handler;
    private Map<String, List<String>> headers;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        handler = new DefaultCorsHandler();
        headers = new HashMap<>();
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    private void setupConfig(final boolean allowCredentials) {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isApiCorsAllowCredentials() {
                return allowCredentials;
            }

            @Override
            public String getApiCorsAllowCredentials() {
                return Boolean.toString(allowCredentials);
            }

            @Override
            public String getApiCorsAllowMethods() {
                return "GET, POST, OPTIONS, DELETE, PUT";
            }

            @Override
            public String getApiCorsAllowHeaders() {
                return "Origin, Content-Type, Accept, Authorization, X-Requested-With, X-Fess-CSRF-Token";
            }

            @Override
            public String getApiCorsMaxAge() {
                return "3600";
            }
        });
    }

    @Test
    public void test_process_exact_withCredentials() {
        setupConfig(true);
        final HttpServletResponse response = recordingResponse();

        handler.process("https://app.example.com", CorsMatchType.EXACT, null, response);

        assertEquals(List.of("https://app.example.com"), headers.get("Access-Control-Allow-Origin"));
        assertEquals(List.of("true"), headers.get("Access-Control-Allow-Credentials"));
        assertEquals(List.of("GET, POST, OPTIONS, DELETE, PUT"), headers.get("Access-Control-Allow-Methods"));
        assertEquals(List.of("Origin, Content-Type, Accept, Authorization, X-Requested-With, X-Fess-CSRF-Token"),
                headers.get("Access-Control-Allow-Headers"));
        assertEquals(List.of("3600"), headers.get("Access-Control-Max-Age"));
        // single value, no duplicate ACAO
        assertEquals(1, headers.get("Access-Control-Allow-Origin").size());
    }

    @Test
    public void test_process_exact_withoutCredentials() {
        setupConfig(false);
        final HttpServletResponse response = recordingResponse();

        handler.process("https://app.example.com", CorsMatchType.EXACT, null, response);

        assertEquals(List.of("https://app.example.com"), headers.get("Access-Control-Allow-Origin"));
        assertNull(headers.get("Access-Control-Allow-Credentials"));
    }

    @Test
    public void test_process_wildcard_neverReflects_neverCredentials() {
        setupConfig(true); // credentials enabled in config, but WILDCARD must NOT send them
        final HttpServletResponse response = recordingResponse();

        handler.process("https://evil.example", CorsMatchType.WILDCARD, null, response);

        assertEquals(List.of("*"), headers.get("Access-Control-Allow-Origin"));
        assertNull(headers.get("Access-Control-Allow-Credentials"));
        assertEquals(List.of("GET, POST, OPTIONS, DELETE, PUT"), headers.get("Access-Control-Allow-Methods"));
        assertEquals(1, headers.get("Access-Control-Allow-Origin").size());
    }

    @Test
    public void test_process_doesNotEmitVary() {
        // Vary is emitted by CorsFilter, not by the handler.
        setupConfig(true);
        final HttpServletResponse response = recordingResponse();

        handler.process("https://app.example.com", CorsMatchType.EXACT, null, response);

        assertNull(headers.get("Vary"));
    }

    private HttpServletResponse recordingResponse() {
        return (HttpServletResponse) java.lang.reflect.Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class<?>[] { HttpServletResponse.class }, (proxy, method, args) -> {
                    switch (method.getName()) {
                    case "setHeader":
                        final List<String> set = new ArrayList<>();
                        set.add((String) args[1]);
                        headers.put((String) args[0], set);
                        return null;
                    case "addHeader":
                        headers.computeIfAbsent((String) args[0], k -> new ArrayList<>()).add((String) args[1]);
                        return null;
                    default:
                        final Class<?> rt = method.getReturnType();
                        if (rt == boolean.class) {
                            return false;
                        }
                        if (rt == int.class) {
                            return 0;
                        }
                        return null;
                    }
                });
    }
}
