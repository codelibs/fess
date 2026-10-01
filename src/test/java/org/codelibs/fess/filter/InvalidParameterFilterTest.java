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
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.apache.tomcat.util.http.Parameters;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Asserts that a request whose parameters Tomcat cannot decode is answered with the status
 * Tomcat assigns to that failure, and that nothing else is.
 */
public class InvalidParameterFilterTest extends UnitFessTestCase {

    private static final String WEB_XML_PATH = "src/main/webapp/WEB-INF/web.xml";

    private static final String FILTER_NAME = "invalidParameterFilter";

    /** The filter that answers a request an API manager serves. */
    private static final String API_FILTER_NAME = "webApiFilter";

    /** The filter that runs LastaFlute's request handling. */
    private static final String LOGGING_FILTER_NAME = "lastaShowbaseFilter";

    @Override
    protected String prepareMockServletPath() {
        return "/go/";
    }

    /** A request whose first parameter read fails, as it does for {@code ?docId=%ff}. */
    private static class UndecodableRequest extends HttpServletRequestWrapper {
        private final RuntimeException failure;

        UndecodableRequest(final HttpServletRequest request, final RuntimeException failure) {
            super(request);
            this.failure = failure;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            throw failure;
        }
    }

    /** Records what the filter sends instead of touching a container. */
    private static class RecordingResponse extends HttpServletResponseWrapper {
        int errorStatus = -1;
        boolean committed = false;

        RecordingResponse(final HttpServletResponse response) {
            super(response);
        }

        @Override
        public void sendError(final int sc) {
            errorStatus = sc;
        }

        @Override
        public boolean isCommitted() {
            return committed;
        }
    }

    /**
     * Pins the container contract this filter relies on: Tomcat reports an undecodable query
     * string as an {@link InvalidParameterException} with status 400, and decodes a valid one
     * untouched. A Tomcat upgrade that changes either would otherwise leave the filter catching
     * nothing while every test below still passed.
     */
    @Test
    public void test_tomcatReportsUndecodableQueryAsInvalidParameterException() {
        for (final String query : new String[] { "q=%ff", "q=%", "q=%zz", "docId=%e3%81", "start=%ff&q=ok" }) {
            try {
                parseQuery(query);
                fail("Tomcat should reject the query string: " + query);
            } catch (final InvalidParameterException e) {
                assertEquals(query, 400, e.getErrorCode());
            }
        }
        assertEquals("a valid percent-encoding must still decode", "あ", parseQuery("q=%E3%81%82").getParameter("q"));
    }

    private static Parameters parseQuery(final String query) {
        final Parameters parameters = new Parameters();
        parameters.setCharset(StandardCharsets.UTF_8);
        parameters.setQueryStringCharset(StandardCharsets.UTF_8);
        final MessageBytes queryBytes = MessageBytes.newInstance();
        queryBytes.setString(query);
        parameters.setQuery(queryBytes);
        parameters.handleQueryParameters();
        return parameters;
    }

    @Test
    public void test_validParameters_continueTheChain() throws Exception {
        final RecordingResponse response = new RecordingResponse(getMockResponse());
        final AtomicBoolean chained = new AtomicBoolean();

        new InvalidParameterFilter().doFilter(getMockRequest(), response, (req, res) -> chained.set(true));

        assertTrue(chained.get(), "a request whose parameters decode must reach the rest of the chain");
        assertEquals("nothing may be sent for a valid request", -1, response.errorStatus);
    }

    @Test
    public void test_undecodableParameter_isAnsweredBadRequestAndNotPassedOn() throws Exception {
        final HttpServletRequest request = new UndecodableRequest(getMockRequest(),
                new InvalidParameterException("Character decoding failed. Parameter [docId] with value [?] has been ignored."));
        final RecordingResponse response = new RecordingResponse(getMockResponse());
        final AtomicBoolean chained = new AtomicBoolean();

        new InvalidParameterFilter().doFilter(request, response, (req, res) -> chained.set(true));

        assertEquals("an undecodable parameter is the caller's mistake", 400, response.errorStatus);
        assertFalse(chained.get(), "a rejected request must not reach LastaFlute");
    }

    @Test
    public void test_statusIsTheOneTomcatAssigned() throws Exception {
        // A form body larger than the container allows is reported through the same exception.
        final HttpServletRequest request = new UndecodableRequest(getMockRequest(), new InvalidParameterException("Post too large", 413));
        final RecordingResponse response = new RecordingResponse(getMockResponse());

        new InvalidParameterFilter().doFilter(request, response, (req, res) -> fail("must not reach the chain"));

        assertEquals(413, response.errorStatus);
    }

    @Test
    public void test_committedResponse_isLeftAlone() throws Exception {
        final HttpServletRequest request = new UndecodableRequest(getMockRequest(), new InvalidParameterException("Bad parameter"));
        final RecordingResponse response = new RecordingResponse(getMockResponse());
        response.committed = true;
        final AtomicBoolean chained = new AtomicBoolean();

        new InvalidParameterFilter().doFilter(request, response, (req, res) -> chained.set(true));

        assertEquals("sendError on a committed response would throw", -1, response.errorStatus);
        assertFalse(chained.get());
    }

    @Test
    public void test_otherFailureWhileReadingParameters_isNotHidden() throws Exception {
        // Only Tomcat's InvalidParameterException says the caller sent something undecodable.
        // Any other failure is ours and has to stay visible as the server error it is.
        final IllegalStateException failure = new IllegalStateException("not a decoding failure");
        final HttpServletRequest request = new UndecodableRequest(getMockRequest(), failure);
        final RecordingResponse response = new RecordingResponse(getMockResponse());

        try {
            new InvalidParameterFilter().doFilter(request, response, (req, res) -> fail("must not reach the chain"));
            fail("an unrelated IllegalStateException must propagate");
        } catch (final IllegalStateException e) {
            assertSame(failure, e);
        }
        assertEquals("an unrelated failure must not be answered as a client error", -1, response.errorStatus);
    }

    @Test
    public void test_noParameters_isNotARejection() throws Exception {
        // getParameterNames() on a request with none is an empty enumeration, not a failure.
        final HttpServletRequest request = new HttpServletRequestWrapper(getMockRequest()) {
            @Override
            public Enumeration<String> getParameterNames() {
                return Collections.emptyEnumeration();
            }
        };
        final AtomicBoolean chained = new AtomicBoolean();

        new InvalidParameterFilter().doFilter(request, new RecordingResponse(getMockResponse()), (req, res) -> chained.set(true));

        assertTrue(chained.get());
    }

    /**
     * Asserts that {@code web.xml} maps this filter after the filter that answers API requests and
     * ahead of the one that runs LastaFlute.
     *
     * <p>Mapped before {@code webApiFilter}, the filter would answer an API request with a bare
     * status and bypass the API's own error format. Mapped after {@code lastaShowbaseFilter}, it
     * would run only once LastaFlute had already read the parameters and turned the failure into
     * a 500.</p>
     */
    @Test
    public void test_mappedBetweenTheApiFilterAndTheLastaFlute() throws Exception {
        final Document webXml = parseWebXml();
        assertEquals("web.xml should declare the filter", InvalidParameterFilter.class.getName(), filterClassOf(webXml, FILTER_NAME));
        final int ownIndex = mappingIndexOf(webXml, FILTER_NAME);
        final int apiIndex = mappingIndexOf(webXml, API_FILTER_NAME);
        final int lastaIndex = mappingIndexOf(webXml, LOGGING_FILTER_NAME);
        assertTrue(FILTER_NAME + " should be mapped in web.xml", ownIndex >= 0);
        assertTrue(API_FILTER_NAME + " should be mapped in web.xml", apiIndex >= 0);
        assertTrue(LOGGING_FILTER_NAME + " should be mapped in web.xml", lastaIndex >= 0);
        assertTrue(FILTER_NAME + " (" + ownIndex + ") must be mapped after " + API_FILTER_NAME + " (" + apiIndex + ")",
                ownIndex > apiIndex);
        assertTrue(FILTER_NAME + " (" + ownIndex + ") must be mapped before " + LOGGING_FILTER_NAME + " (" + lastaIndex + ")",
                ownIndex < lastaIndex);
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
}
