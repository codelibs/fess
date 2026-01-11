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
package org.codelibs.fess.opensearch.client;

import org.codelibs.fess.unit.UnitFessTestCase;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CrawlerEngineClientTest extends UnitFessTestCase {

    private CrawlerEngineClient crawlerEngineClient;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        crawlerEngineClient = new CrawlerEngineClient();
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        crawlerEngineClient = null;
        super.tearDown();
    }

    // Test constructor
    @Test
    public void test_constructor() {
        // Test that constructor creates a non-null instance
        assertNotNull(crawlerEngineClient);
    }

    // Test inheritance
    @Test
    public void test_inheritance() {
        // Test that CrawlerEngineClient is properly inherited
        assertTrue(crawlerEngineClient instanceof org.codelibs.fess.crawler.client.FesenClient);
    }

    // Test multiple instances
    @Test
    public void test_multipleInstances() {
        // Test that multiple instances can be created independently
        CrawlerEngineClient client1 = new CrawlerEngineClient();
        CrawlerEngineClient client2 = new CrawlerEngineClient();

        assertNotNull(client1);
        assertNotNull(client2);
        assertNotSame(client1, client2);
    }

    // Test constructor with different thread context
    @Test
    public void test_constructorInDifferentThread() throws Exception {
        // Test that constructor works in different thread
        final CrawlerEngineClient[] clientHolder = new CrawlerEngineClient[1];
        final Exception[] exceptionHolder = new Exception[1];

        Thread thread = new Thread(() -> {
            try {
                clientHolder[0] = new CrawlerEngineClient();
            } catch (Exception e) {
                exceptionHolder[0] = e;
            }
        });

        thread.start();
        thread.join();

        assertNull(exceptionHolder[0]);
        assertNotNull(clientHolder[0]);
    }

    // Test repeated instantiation
    @Test
    public void test_repeatedInstantiation() {
        // Test that repeated instantiation works correctly
        for (int i = 0; i < 10; i++) {
            CrawlerEngineClient client = new CrawlerEngineClient();
            assertNotNull(client);
        }
    }

    // Test instance fields initialization
    @Test
    public void test_instanceFieldsInitialization() {
        // Test that instance fields are properly initialized
        assertNotNull(crawlerEngineClient);
        // The actual client initialization happens lazily when needed
    }

    // Test that close method exists (inherited)
    @Test
    public void test_closeMethodExists() {
        // Test that close method is available (inherited from parent)
        try {
            // Just verify the method exists, don't actually call it
            crawlerEngineClient.getClass().getMethod("close");
            assertTrue(true);
        } catch (NoSuchMethodException e) {
            fail("close method should exist");
        }
    }

    // Test toString method
    @Test
    public void test_toString() {
        // Test that toString returns a non-null value
        String result = crawlerEngineClient.toString();
        assertNotNull(result);
    }

    // Test hashCode method
    @Test
    public void test_hashCode() {
        // Test that hashCode returns consistent value
        int hashCode1 = crawlerEngineClient.hashCode();
        int hashCode2 = crawlerEngineClient.hashCode();
        assertEquals(hashCode1, hashCode2);
    }

    // Test equals method
    @Test
    public void test_equals() {
        // Test equals method basic functionality
        assertTrue(crawlerEngineClient.equals(crawlerEngineClient));
        assertFalse(crawlerEngineClient.equals(null));
        assertFalse(crawlerEngineClient.equals("string"));

        CrawlerEngineClient other = new CrawlerEngineClient();
        // Different instances should not be equal by default
        assertFalse(crawlerEngineClient.equals(other));
    }

    // Test getClass method
    @Test
    public void test_getClass() {
        // Test that getClass returns the correct class
        assertEquals(CrawlerEngineClient.class, crawlerEngineClient.getClass());
    }
}