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

public class CorsHandlerFactoryTest extends UnitFessTestCase {

    private CorsHandlerFactory corsHandlerFactory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        corsHandlerFactory = new CorsHandlerFactory();
    }

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

    public void test_get_nullOrigin() {
        // Setup
        TestCorsHandler wildcardHandler = new TestCorsHandler("wildcard-handler");
        corsHandlerFactory.add("*", wildcardHandler);

        // Execute
        CorsHandler result = corsHandlerFactory.get(null);

        // Verify - should return wildcard handler
        assertEquals(wildcardHandler, result);
    }

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

    public void test_get_emptyOriginWithWildcard() {
        // Setup
        TestCorsHandler wildcardHandler = new TestCorsHandler("wildcard-handler");
        corsHandlerFactory.add("*", wildcardHandler);

        // Execute
        CorsHandler result = corsHandlerFactory.get("");

        // Verify - should return wildcard handler
        assertEquals(wildcardHandler, result);
    }

    public void test_add_nullHandler() {
        // Setup
        String origin = "https://example.com";

        // Execute
        corsHandlerFactory.add(origin, null);
        CorsHandler result = corsHandlerFactory.get(origin);

        // Verify
        assertNull(result);
    }

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

    public void test_constructor() {
        // Execute
        CorsHandlerFactory factory = new CorsHandlerFactory();

        // Verify
        assertNotNull(factory);
        assertNotNull(factory.handlerMap);
        assertTrue(factory.handlerMap.isEmpty());
        assertNotNull(factory.nullOriginHandler);
        assertNull(factory.nullOriginHandler.get());
    }

    // Test thread safety of ConcurrentHashMap
    public void test_concurrentAccess() throws Exception {
        final int threadCount = 10;
        final int operationsPerThread = 100;
        final Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    String origin = "https://thread" + threadId + ".example.com";
                    TestCorsHandler handler = new TestCorsHandler("handler-" + threadId + "-" + j);
                    corsHandlerFactory.add(origin, handler);
                    CorsHandler retrieved = corsHandlerFactory.get(origin);
                    assertNotNull(retrieved);
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify all handlers were added
        assertEquals(threadCount, corsHandlerFactory.handlerMap.size());
    }

    // Test null origin handler with AtomicReference
    public void test_nullOriginHandler_threadSafety() throws Exception {
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final TestCorsHandler[] handlers = new TestCorsHandler[threadCount];

        for (int i = 0; i < threadCount; i++) {
            handlers[i] = new TestCorsHandler("handler-" + i);
            final int threadId = i;
            threads[i] = new Thread(() -> {
                corsHandlerFactory.add(null, handlers[threadId]);
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify one handler is set (last one wins due to race condition)
        CorsHandler result = corsHandlerFactory.get(null);
        assertNotNull(result);
        boolean foundMatch = false;
        for (TestCorsHandler handler : handlers) {
            if (result == handler) {
                foundMatch = true;
                break;
            }
        }
        assertTrue("Result should be one of the handlers", foundMatch);
    }

    // Test removing handler by setting null value
    public void test_removeHandlerBySettingNull() {
        // Setup
        String origin = "https://example.com";
        TestCorsHandler handler = new TestCorsHandler("handler");
        corsHandlerFactory.add(origin, handler);

        // Verify handler is added
        assertNotNull(corsHandlerFactory.get(origin));

        // Remove by setting null
        corsHandlerFactory.add(origin, null);

        // Verify handler is removed (should fall back to wildcard)
        CorsHandler result = corsHandlerFactory.get(origin);
        // Result will be null if no wildcard handler is set
        if (result != null) {
            assertEquals(corsHandlerFactory.get("*"), result);
        }
    }

    // Test nullOriginHandler field is accessible
    public void test_nullOriginHandlerField() {
        assertNotNull("nullOriginHandler field should be initialized", corsHandlerFactory.nullOriginHandler);
    }

    // Test get with null origin when wildcard exists
    public void test_get_nullOriginWithWildcard() {
        // Setup
        TestCorsHandler wildcardHandler = new TestCorsHandler("wildcard-handler");
        corsHandlerFactory.add("*", wildcardHandler);

        // Execute - no null origin handler set
        CorsHandler result = corsHandlerFactory.get(null);

        // Verify - should return wildcard handler
        assertEquals(wildcardHandler, result);
    }

    // Test get with null origin when null handler exists and wildcard exists
    public void test_get_nullOriginWithNullHandlerAndWildcard() {
        // Setup
        TestCorsHandler nullHandler = new TestCorsHandler("null-handler");
        TestCorsHandler wildcardHandler = new TestCorsHandler("wildcard-handler");
        corsHandlerFactory.add(null, nullHandler);
        corsHandlerFactory.add("*", wildcardHandler);

        // Execute
        CorsHandler result = corsHandlerFactory.get(null);

        // Verify - null handler takes precedence over wildcard
        assertEquals(nullHandler, result);
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