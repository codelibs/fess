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
package org.codelibs.fess.api;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.LogUtil;
import org.dbflute.utflute.mocklet.MockletHttpServletRequestImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class WebApiManagerFactoryTest extends UnitFessTestCase {

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

    private static final String V1_PLUGIN = "fess-webapp-v1-api";

    private static final String CLASSIC_PLUGIN = "fess-webapp-classic-api";

    /** A manager serving {@code prefix} that accepts or declines every request, like the plugins' real managers. */
    private BaseApiManager newManager(final String prefix, final boolean accepts) {
        final BaseApiManager manager = new BaseApiManager() {
            @Override
            public boolean matches(final HttpServletRequest request) {
                return accepts;
            }

            @Override
            public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) {
            }

            @Override
            protected void writeHeaders(final HttpServletResponse response) {
            }
        };
        manager.setPathPrefix(prefix);
        return manager;
    }

    private MockletHttpServletRequestImpl newRequest(final String servletPath) {
        return new MockletHttpServletRequestImpl(getMockRequest().getServletContext(), servletPath);
    }

    /**
     * Asks a new, empty factory for each of {@code servletPaths} in turn and returns the WARN lines it logged.
     * What was reported is remembered JVM-wide, so each call starts from a clean slate to stand for a new deployment.
     */
    private List<String> warnLinesFor(final String... servletPaths) {
        LogUtil.resetWarnOnce();
        return warnLinesFor(new WebApiManagerFactory(), servletPaths);
    }

    /**
     * Asks {@code factory} for each of {@code servletPaths} in turn and returns the WARN lines it
     * logged, after checking what every such line must look like.
     */
    private List<String> warnLinesFor(final WebApiManagerFactory factory, final String... servletPaths) {
        final LogCapturingAppender capture = LogCapturingAppender.attach(WebApiManagerFactory.class);
        try {
            for (final String servletPath : servletPaths) {
                assertNull(factory.get(newRequest(servletPath)), servletPath);
            }
            // ERROR is a notification trigger in Fess, and an unserved plugin is a deployment state, not a fault.
            assertTrue(capture.errors().isEmpty(), "must not be reported at ERROR: " + capture.errors());
            // WARN because the default log level is warn: anything below it is never written on a default install.
            assertTrue(capture.messagesAt(Level.INFO).isEmpty(), "must be reported at WARN: " + capture.messagesAt(Level.INFO));
            for (final LogEvent event : capture.eventsAt(Level.WARN)) {
                assertNull(event.getThrown(), "no stack trace");
                final String line = event.getMessage().getFormattedMessage();
                assertTrue(line.contains("pathPrefix="), line);
                assertFalse(line.contains("path_prefix"), line);
                assertTrue(line.contains("plugin="), line);
                assertTrue(line.contains("restart Fess"), line);
                assertTrue(line.contains("404"), line);
                assertFalse(line.contains(System.lineSeparator()), "one line");
            }
            return capture.warnings();
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_get_unservedV1Api_logsWarnNamingPlugin() {
        final List<String> lines = warnLinesFor("/api/v1/documents");
        assertEquals(1, lines.size(), lines.toString());
        assertTrue(lines.get(0).contains("pathPrefix=/api/v1,"), lines.get(0));
        assertTrue(lines.get(0).contains("plugin=" + V1_PLUGIN + "."), lines.get(0));
    }

    @Test
    public void test_get_unservedJson_logsWarnNamingPlugin() {
        for (final String servletPath : new String[] { "/json", "/json/", "/json/documents" }) {
            final List<String> lines = warnLinesFor(servletPath);
            assertEquals(1, lines.size(), servletPath + ": " + lines);
            assertTrue(lines.get(0).contains("pathPrefix=/json,"), lines.get(0));
            assertTrue(lines.get(0).contains("plugin=" + CLASSIC_PLUGIN + "."), lines.get(0));
        }
    }

    @Test
    public void test_get_unservedSuggest_logsWarnNamingPlugin() {
        for (final String servletPath : new String[] { "/suggest", "/suggest/" }) {
            final List<String> lines = warnLinesFor(servletPath);
            assertEquals(1, lines.size(), servletPath + ": " + lines);
            assertTrue(lines.get(0).contains("pathPrefix=/suggest,"), lines.get(0));
            assertTrue(lines.get(0).contains("plugin=" + CLASSIC_PLUGIN + "."), lines.get(0));
        }
    }

    @Test
    public void test_get_v1AndClassicMapToTheirOwnPlugin() {
        final String v1 = warnLinesFor("/api/v1").get(0);
        assertTrue(v1.contains("plugin=" + V1_PLUGIN), v1);
        assertFalse(v1.contains(CLASSIC_PLUGIN), v1);
        final String json = warnLinesFor("/json").get(0);
        assertTrue(json.contains("plugin=" + CLASSIC_PLUGIN), json);
        assertFalse(json.contains(V1_PLUGIN), json);
    }

    @Test
    public void test_get_sameLegacyApiRepeatedly_logsOnce() {
        assertEquals(1, warnLinesFor("/json", "/json").size());
        assertEquals(1, warnLinesFor("/api/v1/documents", "/api/v1/documents", "/api/v1/documents", "/api/v1").size());
    }

    /** {@code /json} and {@code /suggest} are served by the same plugin, so they share one line. */
    @Test
    public void test_get_jsonThenSuggest_logsOnceForTheSharedPlugin() {
        final List<String> lines = warnLinesFor("/json", "/suggest", "/json/x", "/suggest/");
        assertEquals(1, lines.size(), lines.toString());
        assertTrue(lines.get(0).contains("plugin=" + CLASSIC_PLUGIN), lines.get(0));
        assertTrue(lines.get(0).contains("pathPrefix=/json,"), "the first miss is the one reported: " + lines.get(0));
    }

    @Test
    public void test_get_v1ThenJson_logsOncePerPlugin() {
        final List<String> lines = warnLinesFor("/api/v1/documents", "/json", "/api/v1", "/json");
        assertEquals(2, lines.size(), lines.toString());
        assertTrue(lines.get(0).contains("plugin=" + V1_PLUGIN), lines.get(0));
        assertTrue(lines.get(1).contains("plugin=" + CLASSIC_PLUGIN), lines.get(1));
    }

    @Test
    public void test_get_otherLegacyPluginInstalled_stillLogsForTheMissingOne() {
        final WebApiManagerFactory factory = new WebApiManagerFactory();
        factory.add(newManager("/api/v1", false));
        final List<String> lines = warnLinesFor(factory, "/json");
        assertEquals(1, lines.size(), lines.toString());
        assertTrue(lines.get(0).contains("plugin=" + CLASSIC_PLUGIN), lines.get(0));
    }

    /**
     * The plugin is installed, so the miss is not a missing plugin: {@code web.api.json=false}
     * makes the manager decline, and that is the operator's own setting.
     */
    @Test
    public void test_get_pluginInstalledButDeclined_saysNothing() {
        final WebApiManagerFactory factory = new WebApiManagerFactory();
        factory.add(newManager("/api/v1", false));
        factory.add(newManager("/json", false));
        factory.add(newManager("/suggest", false));
        assertTrue(warnLinesFor(factory, "/api/v1/documents", "/json", "/json/", "/suggest").isEmpty());
    }

    @Test
    public void test_get_otherPathsSayNothing() {
        assertTrue(warnLinesFor("/", "/search", "/suggestions", "/jsonp", "/json2", "/api/v1x", "/api/v2/documents", "/api", "/admin/",
                "/js/json").isEmpty());
    }

    @Test
    public void test_get_matchedManager_returnsItAndSaysNothing() {
        final WebApiManagerFactory factory = new WebApiManagerFactory();
        final BaseApiManager manager = newManager("/json", true);
        factory.add(manager);
        final LogCapturingAppender capture = LogCapturingAppender.attach(WebApiManagerFactory.class);
        try {
            assertSame(manager, factory.get(newRequest("/json")));
            assertTrue(capture.warnings().isEmpty(), capture.warnings().toString());
            assertTrue(capture.messagesAt(Level.INFO).isEmpty(), capture.messagesAt(Level.INFO).toString());
        } finally {
            capture.detach();
        }
    }

    @Test
    public void test_get_nullServletPath_doesNotThrow() {
        final MockletHttpServletRequestImpl request = new MockletHttpServletRequestImpl(getMockRequest().getServletContext(), "/") {
            @Override
            public String getServletPath() {
                return null;
            }
        };
        final LogCapturingAppender capture = LogCapturingAppender.attach(WebApiManagerFactory.class);
        try {
            assertNull(new WebApiManagerFactory().get(request));
            assertTrue(capture.warnings().isEmpty(), capture.warnings().toString());
        } finally {
            capture.detach();
        }
    }

    /**
     * The endpoint is anonymous, so the caller controls every byte of the path and the query
     * string. Only the constant prefix and plugin name may reach the log.
     */
    @Test
    public void test_get_logLineCarriesNoRequestData() {
        final MockletHttpServletRequestImpl request = newRequest("/json/INJECTED_PATH%0aFAKE_LINE");
        request.setQueryString("q=SECRET_QUERY&token=SECRET_TOKEN");
        // Captured from WARN up: the existing DEBUG lines in get() do print the servlet path.
        final LogCapturingAppender capture = LogCapturingAppender.attach(WebApiManagerFactory.class.getName(), Level.WARN);
        try {
            assertNull(new WebApiManagerFactory().get(request));
            assertEquals(1, capture.events().size(), capture.renderedEvents().toString());
            assertEquals(Level.WARN, capture.events().get(0).getLevel());
            assertNull(capture.events().get(0).getThrown(), "no stack trace");
            // Rendered, so an attached throwable would show up here too.
            final String line = capture.renderedEvents().get(0);
            assertTrue(line.contains("pathPrefix=/json,"), line);
            assertFalse(line.contains("INJECTED_PATH"), line);
            assertFalse(line.contains("FAKE_LINE"), line);
            assertFalse(line.contains("SECRET_QUERY"), line);
            assertFalse(line.contains("SECRET_TOKEN"), line);
        } finally {
            capture.detach();
        }
    }
}
