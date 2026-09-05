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
package org.codelibs.fess.filter;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.Level;
import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.lastaflute.web.servlet.filter.RequestLoggingFilter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Asserts that a request ending as a 500 leaves exactly one WARN line behind.
 *
 * <p>The exception is delivered the way {@link RequestLoggingFilter} delivers it in production:
 * through {@code processServerErrorCallback}, which is what {@code sendInternalServerError} calls
 * on the 500 path and only there.</p>
 */
public class ServerErrorLoggingFilterTest extends UnitFessTestCase {

    private static final String WEB_XML_PATH = "src/main/webapp/WEB-INF/web.xml";

    /** The {@code filter-name} web.xml gives this filter. */
    private static final String FILTER_NAME = "serverErrorLoggingFilter";

    /** The filter that runs {@link RequestLoggingFilter} inside itself. */
    private static final String LOGGING_FILTER_NAME = "lastaShowbaseFilter";

    @Override
    protected String prepareMockServletPath() {
        return "/search/advance";
    }

    /** Calls the protected callback dispatch of the real logging filter. */
    private static class ExposedLoggingFilter extends RequestLoggingFilter {
        void fireServerError(final HttpServletRequest request, final HttpServletResponse response, final Throwable cause) {
            processServerErrorCallback(request, response, cause);
        }
    }

    @Test
    public void test_serverError_logsOneWarnLineWithPathAndException() throws Exception {
        final LogCapturingAppender appender = LogCapturingAppender.attach(ServerErrorLoggingFilter.class);
        try {
            final HttpServletRequest request = getMockRequest();
            final HttpServletResponse response = getMockResponse();
            getMockRequest().setMethod("GET");
            final ExposedLoggingFilter loggingFilter = new ExposedLoggingFilter();
            final IllegalStateException cause = new IllegalStateException("lonely validator annotation");

            new ServerErrorLoggingFilter().doFilter(request, response,
                    (req, res) -> loggingFilter.fireServerError(request, response, cause));

            final List<String> warnings = appender.warnings();
            assertEquals("expected exactly one WARN line, got " + warnings, 1, warnings.size());
            final String line = warnings.get(0);
            assertTrue(line + " must name the request path", line.contains(request.getRequestURI()));
            assertTrue(line + " must name the request method", line.contains("GET"));
            assertTrue(line + " must name the exception class", line.contains(IllegalStateException.class.getName()));
            assertTrue(line + " must carry the exception message", line.contains("lonely validator annotation"));
            // The stack trace belongs to debug: a WARN carrying the throwable would print it.
            assertNull(appender.eventsAt(Level.WARN).get(0).getThrown(), "WARN line must not carry a stack trace");
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_noServerError_logsNothing() throws Exception {
        final LogCapturingAppender appender = LogCapturingAppender.attach(ServerErrorLoggingFilter.class);
        try {
            new ServerErrorLoggingFilter().doFilter(getMockRequest(), getMockResponse(), (req, res) -> {
                // an ordinary request: a 4xx or a redirect never reaches the server error callback
            });
            assertEquals("a request that did not fail must log nothing, got " + appender.events(), 0, appender.events().size());
        } finally {
            appender.detach();
        }
    }

    /**
     * Asserts that {@code web.xml} maps this filter, and maps it ahead of the filter that runs
     * {@link RequestLoggingFilter}.
     *
     * <p>The order is the whole registration: the handler has to be on the thread before the
     * logging filter looks it up, so a mapping placed after {@code lastaShowbaseFilter} would
     * leave the 500 unreported again -- with nothing failing anywhere to say so.</p>
     */
    @Test
    public void test_mappedAheadOfTheLoggingFilter() throws Exception {
        final Document webXml = parseWebXml();
        assertEquals("web.xml should declare the filter", ServerErrorLoggingFilter.class.getName(), filterClassOf(webXml, FILTER_NAME));
        final int ownIndex = mappingIndexOf(webXml, FILTER_NAME);
        final int loggingIndex = mappingIndexOf(webXml, LOGGING_FILTER_NAME);
        assertTrue(FILTER_NAME + " should be mapped in web.xml", ownIndex >= 0);
        assertTrue(LOGGING_FILTER_NAME + " should be mapped in web.xml", loggingIndex >= 0);
        assertTrue(FILTER_NAME + " (" + ownIndex + ") must be mapped before " + LOGGING_FILTER_NAME + " (" + loggingIndex + ")",
                ownIndex < loggingIndex);
    }

    private Document parseWebXml() throws Exception {
        final File file = new File(WEB_XML_PATH);
        assertTrue(WEB_XML_PATH + " should exist", file.exists());
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // web.xml carries a schema rather than a DOCTYPE today, so nothing external is fetched;
        // the guard is here so that adding one can never make this test reach the network.
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return factory.newDocumentBuilder().parse(file);
    }

    /** Returns the {@code filter-class} declared for the given {@code filter-name}, or null. */
    private String filterClassOf(final Document webXml, final String filterName) {
        final NodeList filters = webXml.getElementsByTagName("filter");
        for (int i = 0; i < filters.getLength(); i++) {
            final Element filter = (Element) filters.item(i);
            if (filterName.equals(textOf(filter, "filter-name"))) {
                return textOf(filter, "filter-class");
            }
        }
        return null;
    }

    /** Returns the position of the given filter in the {@code filter-mapping} order, or -1. */
    private int mappingIndexOf(final Document webXml, final String filterName) {
        final NodeList mappings = webXml.getElementsByTagName("filter-mapping");
        for (int i = 0; i < mappings.getLength(); i++) {
            if (filterName.equals(textOf((Element) mappings.item(i), "filter-name"))) {
                return i;
            }
        }
        return -1;
    }

    private String textOf(final Element parent, final String tagName) {
        final NodeList elements = parent.getElementsByTagName(tagName);
        return elements.getLength() == 0 ? null : elements.item(0).getTextContent().trim();
    }

    @Test
    public void test_handlerDetachedAfterRequest() throws Exception {
        final HttpServletRequest request = getMockRequest();
        final HttpServletResponse response = getMockResponse();
        final ExposedLoggingFilter loggingFilter = new ExposedLoggingFilter();
        new ServerErrorLoggingFilter().doFilter(request, response, (req, res) -> {
            // nothing failed during the request
        });
        final LogCapturingAppender appender = LogCapturingAppender.attach(ServerErrorLoggingFilter.class);
        try {
            loggingFilter.fireServerError(request, response, new IllegalStateException("after the request"));
            assertEquals("the handler must not outlive the request, got " + appender.events(), 0, appender.events().size());
        } finally {
            appender.detach();
        }
    }
}
