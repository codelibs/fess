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

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CorsHandlerFactoryTest extends UnitFessTestCase {

    private CorsHandlerFactory corsHandlerFactory;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        corsHandlerFactory = new CorsHandlerFactory();
    }

    @Test
    public void test_add_and_get_specificOrigin() {
        // Setup
        String origin = "https://example.com";
        TestCorsHandler handler = new TestCorsHandler("example-handler");

        // Execute
        corsHandlerFactory.add(origin, handler);
        CorsHandler result = corsHandlerFactory.get(origin);

        // Verify
        assertNotNull(result);
        assertEquals(handler, result);
        assertEquals("example-handler", ((TestCorsHandler) result).getName());
    }

    @Test
    public void test_add_and_get_multipleOrigins() {
        // Setup
        String origin1 = "https://example1.com";
        String origin2 = "https://example2.com";
        String origin3 = "https://example3.com";
        TestCorsHandler handler1 = new TestCorsHandler("handler1");
        TestCorsHandler handler2 = new TestCorsHandler("handler2");
        TestCorsHandler handler3 = new TestCorsHandler("handler3");

        // Execute
        corsHandlerFactory.add(origin1, handler1);
        corsHandlerFactory.add(origin2, handler2);
        corsHandlerFactory.add(origin3, handler3);

        // Verify
        assertEquals(handler1, corsHandlerFactory.get(origin1));
        assertEquals(handler2, corsHandlerFactory.get(origin2));
        assertEquals(handler3, corsHandlerFactory.get(origin3));
    }

    @Test
    public void test_get_wildcardOrigin() {
        // Setup
        TestCorsHandler wildcardHandler = new TestCorsHandler("wildcard-handler");
        corsHandlerFactory.add("*", wildcardHandler);

        // Execute
        CorsHandler result = corsHandlerFactory.get("https://unknown.com");

        // Verify
        assertNotNull(result);
        assertEquals(wildcardHandler, result);
    }

    @Test
    public void test_get_specificOriginWithWildcardFallback() {
        // Setup
        String specificOrigin = "https://specific.com";
        TestCorsHandler specificHandler = new TestCorsHandler("specific-handler");
        TestCorsHandler wildcardHandler = new TestCorsHandler("wildcard-handler");

        corsHandlerFactory.add(specificOrigin, specificHandler);
        corsHandlerFactory.add("*", wildcardHandler);

        // Execute & Verify - specific origin should return specific handler
        assertEquals(specificHandler, corsHandlerFactory.get(specificOrigin));

        // Execute & Verify - unknown origin should return wildcard handler
        assertEquals(wildcardHandler, corsHandlerFactory.get("https://unknown.com"));
    }

    @Test
    public void test_get_nonExistentOriginWithoutWildcard() {
        // Setup
        String origin = "https://example.com";
        TestCorsHandler handler = new TestCorsHandler("example-handler");
        corsHandlerFactory.add(origin, handler);

        // Execute
        CorsHandler result = corsHandlerFactory.get("https://nonexistent.com");

        // Verify
        assertNull(result);
    }

    @Test
    public void test_add_overwriteExistingOrigin() {
        // Setup
        String origin = "https://example.com";
        TestCorsHandler handler1 = new TestCorsHandler("handler1");
        TestCorsHandler handler2 = new TestCorsHandler("handler2");

        // Execute
        corsHandlerFactory.add(origin, handler1);
        corsHandlerFactory.add(origin, handler2);

        // Verify - should have the latest handler
        CorsHandler result = corsHandlerFactory.get(origin);
        assertEquals(handler2, result);
        assertEquals("handler2", ((TestCorsHandler) result).getName());
    }

    @Test
    public void test_add_nullOrigin() {
        // Setup
        TestCorsHandler handler = new TestCorsHandler("null-origin-handler");

        // Execute
        corsHandlerFactory.add(null, handler);
        CorsHandler result = corsHandlerFactory.get(null);

        // Verify
        assertNotNull(result);
        assertEquals(handler, result);
    }

    @Test
    public void test_get_nullOrigin() {
        // Setup
        TestCorsHandler wildcardHandler = new TestCorsHandler("wildcard-handler");
        corsHandlerFactory.add("*", wildcardHandler);

        // Execute
        CorsHandler result = corsHandlerFactory.get(null);

        // Verify - should return wildcard handler
        assertEquals(wildcardHandler, result);
    }

    @Test
    public void test_add_emptyOrigin() {
        // Setup
        String emptyOrigin = "";
        TestCorsHandler handler = new TestCorsHandler("empty-origin-handler");

        // Execute
        corsHandlerFactory.add(emptyOrigin, handler);
        CorsHandler result = corsHandlerFactory.get(emptyOrigin);

        // Verify
        assertNotNull(result);
        assertEquals(handler, result);
    }

    @Test
    public void test_get_emptyOriginWithWildcard() {
        // Setup
        TestCorsHandler wildcardHandler = new TestCorsHandler("wildcard-handler");
        corsHandlerFactory.add("*", wildcardHandler);

        // Execute
        CorsHandler result = corsHandlerFactory.get("");

        // Verify - should return wildcard handler
        assertEquals(wildcardHandler, result);
    }

    @Test
    public void test_add_nullHandler() {
        // Setup
        String origin = "https://example.com";

        // Execute
        corsHandlerFactory.add(origin, null);
        CorsHandler result = corsHandlerFactory.get(origin);

        // Verify
        assertNull(result);
    }

    @Test
    public void test_add_caseInsensitiveOrigins() {
        // Setup
        String lowerCaseOrigin = "https://example.com";
        String upperCaseOrigin = "HTTPS://EXAMPLE.COM";
        TestCorsHandler lowerHandler = new TestCorsHandler("lower-handler");
        TestCorsHandler upperHandler = new TestCorsHandler("upper-handler");

        // Execute
        corsHandlerFactory.add(lowerCaseOrigin, lowerHandler);
        corsHandlerFactory.add(upperCaseOrigin, upperHandler);

        // Verify - origins are case sensitive
        assertEquals(lowerHandler, corsHandlerFactory.get(lowerCaseOrigin));
        assertEquals(upperHandler, corsHandlerFactory.get(upperCaseOrigin));
        assertNull(corsHandlerFactory.get("https://Example.Com"));
    }

    @Test
    public void test_add_originsWithDifferentPorts() {
        // Setup
        String origin8080 = "https://example.com:8080";
        String origin8443 = "https://example.com:8443";
        TestCorsHandler handler8080 = new TestCorsHandler("handler-8080");
        TestCorsHandler handler8443 = new TestCorsHandler("handler-8443");

        // Execute
        corsHandlerFactory.add(origin8080, handler8080);
        corsHandlerFactory.add(origin8443, handler8443);

        // Verify
        assertEquals(handler8080, corsHandlerFactory.get(origin8080));
        assertEquals(handler8443, corsHandlerFactory.get(origin8443));
        assertNull(corsHandlerFactory.get("https://example.com"));
    }

    @Test
    public void test_add_originsWithTrailingSlash() {
        // Setup
        String originWithSlash = "https://example.com/";
        String originWithoutSlash = "https://example.com";
        TestCorsHandler handlerWithSlash = new TestCorsHandler("handler-with-slash");
        TestCorsHandler handlerWithoutSlash = new TestCorsHandler("handler-without-slash");

        // Execute
        corsHandlerFactory.add(originWithSlash, handlerWithSlash);
        corsHandlerFactory.add(originWithoutSlash, handlerWithoutSlash);

        // Verify - trailing slash matters
        assertEquals(handlerWithSlash, corsHandlerFactory.get(originWithSlash));
        assertEquals(handlerWithoutSlash, corsHandlerFactory.get(originWithoutSlash));
    }

    @Test
    public void test_constructor() {
        // Execute
        CorsHandlerFactory factory = new CorsHandlerFactory();

        // Verify
        assertNotNull(factory);
        assertNotNull(factory.handerMap);
        assertTrue(factory.handerMap.isEmpty());
    }

    // Test implementation of CorsHandler for testing purposes
    private static class TestCorsHandler extends CorsHandler {
        private final String name;

        public TestCorsHandler(String name) {
            this.name = name;
        }

        @Override
        public void process(String origin, ServletRequest request, ServletResponse response) {
            // Test implementation - no operation
        }

        public String getName() {
            return name;
        }
    }
}