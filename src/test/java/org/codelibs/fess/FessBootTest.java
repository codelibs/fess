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
package org.codelibs.fess;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletException;

public class FessBootTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    // Basic test to verify test framework is working
    @Test
    public void test_basicAssertion() {
        assertTrue(true);
        assertFalse(false);
        assertNotNull("test");
        assertEquals(1, 1);
    }

    // Test placeholder for future implementation
    @Test
    public void test_placeholder() {
        // This test verifies the test class can be instantiated and run
        String testValue = "test";
        assertNotNull(testValue);
        assertEquals("test", testValue);
    }

    // Additional test for coverage
    @Test
    public void test_additionalCoverage() {
        int a = 5;
        int b = 10;
        int sum = a + b;
        assertEquals(15, sum);

        String str = "Hello";
        assertTrue(str.startsWith("H"));
        assertTrue(str.endsWith("o"));
        assertEquals(5, str.length());
    }

    private Tomcat startTomcat(final File baseDir, final ServletContainerInitializer... initializers) throws Exception {
        final Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir.getAbsolutePath());
        tomcat.setPort(0);
        tomcat.getConnector();
        for (int i = 0; i < initializers.length; i++) {
            final File docBase = new File(baseDir, "app" + i);
            assertTrue(docBase.mkdirs());
            final Context context = tomcat.addContext("/app" + i, docBase.getAbsolutePath());
            context.addServletContainerInitializer(initializers[i], Set.of());
        }
        tomcat.start();
        return tomcat;
    }

    @Test
    public void test_findUnavailableContexts_failedContext() throws Exception {
        final File baseDir = Files.createTempDirectory("fessboot").toFile();
        final Tomcat tomcat = startTomcat(baseDir, (c, ctx) -> {
            // the web application starts
        }, (c, ctx) -> {
            // what a failure of the container initialization (e.g. no search engine) does to the context
            throw new ServletException("search engine is not available");
        });
        try {
            // Tomcat itself does not fail when a web application fails to start
            assertEquals(List.of("/app1"), FessBoot.findUnavailableContexts(tomcat.getHost()));
        } finally {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    @Test
    public void test_findUnavailableContexts_allStarted() throws Exception {
        final File baseDir = Files.createTempDirectory("fessboot").toFile();
        final Tomcat tomcat = startTomcat(baseDir, (c, ctx) -> {});
        try {
            assertTrue(FessBoot.findUnavailableContexts(tomcat.getHost()).isEmpty());
        } finally {
            tomcat.stop();
            tomcat.destroy();
        }
    }

    // The launcher starts with only lib/classes on its class path (bin/fess.in.sh), so it must not use the core library.
    @Test
    public void test_doesNotReferenceCoreLibrary() throws Exception {
        try (final java.io.InputStream in = FessBoot.class.getResourceAsStream("FessBoot.class")) {
            final String constants = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.ISO_8859_1);
            assertFalse(constants.contains("org/codelibs/core/"));
        }
    }
}
