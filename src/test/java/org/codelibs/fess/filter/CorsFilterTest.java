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

import java.io.IOException;

import org.codelibs.fess.cors.CorsHandler;
import org.codelibs.fess.cors.CorsHandlerFactory;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CorsFilterTest extends UnitFessTestCase {

    private CorsFilter corsFilter;
    private TestCorsHandlerFactory corsHandlerFactory;
    private TestHttpServletRequest mockRequest;
    private TestHttpServletResponse mockResponse;
    private TestFilterChain mockFilterChain;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        corsFilter = new CorsFilter();
        corsHandlerFactory = new TestCorsHandlerFactory();
        // Initialize mocks
        mockRequest = new TestHttpServletRequest();
        mockResponse = new TestHttpServletResponse();
        mockFilterChain = new TestFilterChain();
        ComponentUtil.register(corsHandlerFactory, "corsHandlerFactory");
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    private void resetMocks() {
        mockRequest = new TestHttpServletRequest();
        mockResponse = new TestHttpServletResponse();
        mockFilterChain = new TestFilterChain();
    }

    // Test with no Origin header
    @Test
    public void test_doFilter_noOriginHeader() throws IOException, ServletException {
        mockRequest.setHeader("Origin", null);

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
        assertFalse(corsHandlerFactory.wasGetCalled());
    }

    // Test with blank Origin header
    @Test
    public void test_doFilter_blankOriginHeader() throws IOException, ServletException {
        mockRequest.setHeader("Origin", "");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
        assertFalse(corsHandlerFactory.wasGetCalled());
    }

    // Test with whitespace-only Origin header
    @Test
    public void test_doFilter_whitespaceOriginHeader() throws IOException, ServletException {
        mockRequest.setHeader("Origin", "   ");

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
        assertFalse(corsHandlerFactory.wasGetCalled());
    }

    // Test with valid Origin but no CorsHandler found
    @Test
    public void test_doFilter_noCorsHandler() throws IOException, ServletException {
        mockRequest.setHeader("Origin", "http://example.com");
        corsHandlerFactory.setHandler(null);

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
        assertTrue(corsHandlerFactory.wasGetCalled());
        assertEquals("http://example.com", corsHandlerFactory.getLastOrigin());
    }

    // Test with valid Origin and CorsHandler found (non-OPTIONS request)
    @Test
    public void test_doFilter_withCorsHandler_nonOptions() throws IOException, ServletException {
        String origin = "http://example.com";
        mockRequest.setHeader("Origin", origin);
        mockRequest.setMethod("GET");
        TestCorsHandler handler = new TestCorsHandler();
        corsHandlerFactory.setHandler(handler);

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
        assertTrue(corsHandlerFactory.wasGetCalled());
        assertTrue(handler.wasProcessCalled());
        assertEquals(origin, handler.getLastOrigin());
        assertSame(mockRequest, handler.getLastRequest());
        assertSame(mockResponse, handler.getLastResponse());
        assertEquals(0, mockResponse.getStatus()); // Status not set for non-OPTIONS
    }

    // Test with valid Origin and CorsHandler found (POST request)
    @Test
    public void test_doFilter_withCorsHandler_post() throws IOException, ServletException {
        String origin = "http://example.com";
        mockRequest.setHeader("Origin", origin);
        mockRequest.setMethod("POST");
        TestCorsHandler handler = new TestCorsHandler();
        corsHandlerFactory.setHandler(handler);

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
        assertTrue(handler.wasProcessCalled());
        assertEquals(0, mockResponse.getStatus());
    }

    // Test with valid Origin and CorsHandler found (PUT request)
    @Test
    public void test_doFilter_withCorsHandler_put() throws IOException, ServletException {
        String origin = "http://example.com";
        mockRequest.setHeader("Origin", origin);
        mockRequest.setMethod("PUT");
        TestCorsHandler handler = new TestCorsHandler();
        corsHandlerFactory.setHandler(handler);

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
        assertTrue(handler.wasProcessCalled());
        assertEquals(0, mockResponse.getStatus());
    }

    // Test with valid Origin and CorsHandler found (DELETE request)
    @Test
    public void test_doFilter_withCorsHandler_delete() throws IOException, ServletException {
        String origin = "http://example.com";
        mockRequest.setHeader("Origin", origin);
        mockRequest.setMethod("DELETE");
        TestCorsHandler handler = new TestCorsHandler();
        corsHandlerFactory.setHandler(handler);

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
        assertTrue(handler.wasProcessCalled());
        assertEquals(0, mockResponse.getStatus());
    }

    // Test with OPTIONS request (preflight)
    @Test
    public void test_doFilter_withCorsHandler_options() throws IOException, ServletException {
        String origin = "http://example.com";
        mockRequest.setHeader("Origin", origin);
        mockRequest.setMethod("OPTIONS");
        TestCorsHandler handler = new TestCorsHandler();
        corsHandlerFactory.setHandler(handler);

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertFalse(mockFilterChain.wasDoFilterCalled()); // Chain should not continue for OPTIONS
        assertTrue(corsHandlerFactory.wasGetCalled());
        assertTrue(handler.wasProcessCalled());
        assertEquals(origin, handler.getLastOrigin());
        assertEquals(HttpServletResponse.SC_ACCEPTED, mockResponse.getStatus());
    }

    // Test with OPTIONS request but no CorsHandler
    @Test
    public void test_doFilter_options_noCorsHandler() throws IOException, ServletException {
        String origin = "http://example.com";
        mockRequest.setHeader("Origin", origin);
        mockRequest.setMethod("OPTIONS");
        corsHandlerFactory.setHandler(null);

        corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled()); // Chain should continue if no handler
        assertTrue(corsHandlerFactory.wasGetCalled());
        assertEquals(0, mockResponse.getStatus()); // Status not set when no handler
    }

    // Test with different origin formats
    @Test
    public void test_doFilter_differentOrigins() throws Exception {
        String[] origins = { "http://localhost:8080", "https://example.com", "http://subdomain.example.com", "https://example.com:3000" };

        for (String origin : origins) {
            resetMocks(); // Reset for each test
            mockRequest.setHeader("Origin", origin);
            mockRequest.setMethod("GET");
            TestCorsHandler handler = new TestCorsHandler();
            corsHandlerFactory.setHandler(handler);

            corsFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

            assertTrue(mockFilterChain.wasDoFilterCalled());
            assertTrue(handler.wasProcessCalled());
            assertEquals(origin, handler.getLastOrigin());
        }
    }

    // Test constructor
    @Test
    public void test_constructor() {
        CorsFilter filter = new CorsFilter();
        assertNotNull(filter);
    }

    // Test OPTIONS constant
    @Test
    public void test_optionsConstant() {
        assertEquals("OPTIONS", CorsFilter.OPTIONS);
    }

    // Helper classes for testing

    private static class TestCorsHandlerFactory extends CorsHandlerFactory {
        private boolean getCalled = false;
        private String lastOrigin = null;
        private CorsHandler handler = null;

        @Override
        public CorsHandler get(String origin) {
            getCalled = true;
            lastOrigin = origin;
            return handler;
        }

        public void setHandler(CorsHandler handler) {
            this.handler = handler;
        }

        public boolean wasGetCalled() {
            return getCalled;
        }

        public String getLastOrigin() {
            return lastOrigin;
        }
    }

    private static class TestCorsHandler extends CorsHandler {
        private boolean processCalled = false;
        private String lastOrigin = null;
        private ServletRequest lastRequest = null;
        private ServletResponse lastResponse = null;

        @Override
        public void process(String origin, ServletRequest request, ServletResponse response) {
            processCalled = true;
            lastOrigin = origin;
            lastRequest = request;
            lastResponse = response;
        }

        public boolean wasProcessCalled() {
            return processCalled;
        }

        public String getLastOrigin() {
            return lastOrigin;
        }

        public ServletRequest getLastRequest() {
            return lastRequest;
        }

        public ServletResponse getLastResponse() {
            return lastResponse;
        }
    }

    private static class TestHttpServletRequest implements HttpServletRequest {
        private String method = "GET";
        private String originHeader = null;

        public void setMethod(String method) {
            this.method = method;
        }

        @Override
        public String getMethod() {
            return method;
        }

        public void setHeader(String name, String value) {
            if ("Origin".equals(name)) {
                originHeader = value;
            }
        }

        @Override
        public String getHeader(String name) {
            if ("Origin".equals(name)) {
                return originHeader;
            }
            return null;
        }

        // Minimal implementations for other required methods
        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public jakarta.servlet.http.Cookie[] getCookies() {
            return null;
        }

        @Override
        public long getDateHeader(String name) {
            return -1;
        }

        @Override
        public java.util.Enumeration<String> getHeaders(String name) {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public java.util.Enumeration<String> getHeaderNames() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public int getIntHeader(String name) {
            return -1;
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getContextPath() {
            return "";
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public java.security.Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public String getRequestURI() {
            return "/";
        }

        @Override
        public StringBuffer getRequestURL() {
            return new StringBuffer("http://localhost:8080/");
        }

        @Override
        public String getServletPath() {
            return "";
        }

        @Override
        public jakarta.servlet.http.HttpSession getSession(boolean create) {
            return null;
        }

        @Override
        public jakarta.servlet.http.HttpSession getSession() {
            return null;
        }

        @Override
        public String changeSessionId() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean authenticate(jakarta.servlet.http.HttpServletResponse response) {
            return false;
        }

        @Override
        public void login(String username, String password) {
        }

        @Override
        public void logout() {
        }

        @Override
        public java.util.Collection<jakarta.servlet.http.Part> getParts() {
            return null;
        }

        @Override
        public jakarta.servlet.http.Part getPart(String name) {
            return null;
        }

        @Override
        public <T extends jakarta.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass) {
            return null;
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public java.util.Enumeration<String> getAttributeNames() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String env) {
        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public jakarta.servlet.ServletInputStream getInputStream() {
            return null;
        }

        @Override
        public String getParameter(String name) {
            return null;
        }

        @Override
        public java.util.Enumeration<String> getParameterNames() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public String[] getParameterValues(String name) {
            return null;
        }

        @Override
        public java.util.Map<String, String[]> getParameterMap() {
            return new java.util.HashMap<>();
        }

        @Override
        public String getProtocol() {
            return "HTTP/1.1";
        }

        @Override
        public String getScheme() {
            return "http";
        }

        @Override
        public String getServerName() {
            return "localhost";
        }

        @Override
        public int getServerPort() {
            return 8080;
        }

        @Override
        public java.io.BufferedReader getReader() {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return "127.0.0.1";
        }

        @Override
        public String getRemoteHost() {
            return "localhost";
        }

        @Override
        public void setAttribute(String name, Object o) {
        }

        @Override
        public void removeAttribute(String name) {
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.getDefault();
        }

        @Override
        public java.util.Enumeration<java.util.Locale> getLocales() {
            return java.util.Collections.emptyEnumeration();
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public jakarta.servlet.RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return "localhost";
        }

        @Override
        public String getLocalAddr() {
            return "127.0.0.1";
        }

        @Override
        public int getLocalPort() {
            return 8080;
        }

        @Override
        public jakarta.servlet.ServletContext getServletContext() {
            return null;
        }

        @Override
        public jakarta.servlet.AsyncContext startAsync() {
            return null;
        }

        @Override
        public jakarta.servlet.AsyncContext startAsync(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
            return null;
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public jakarta.servlet.AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public jakarta.servlet.DispatcherType getDispatcherType() {
            return jakarta.servlet.DispatcherType.REQUEST;
        }

        @Override
        public String getRequestId() {
            return null;
        }

        @Override
        public String getProtocolRequestId() {
            return null;
        }

        @Override
        public jakarta.servlet.ServletConnection getServletConnection() {
            return null;
        }
    }

    private static class TestHttpServletResponse implements HttpServletResponse {
        private int status = 0;

        @Override
        public void setStatus(int sc) {
            this.status = sc;
        }

        public int getStatus() {
            return status;
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return java.util.Collections.emptyList();
        }

        @Override
        public java.util.Collection<String> getHeaders(String name) {
            return java.util.Collections.emptyList();
        }

        // Minimal implementations for other required methods
        @Override
        public void addCookie(jakarta.servlet.http.Cookie cookie) {
        }

        @Override
        public boolean containsHeader(String name) {
            return false;
        }

        @Override
        public String encodeURL(String url) {
            return url;
        }

        @Override
        public String encodeRedirectURL(String url) {
            return url;
        }

        @Override
        public void sendError(int sc, String msg) {
        }

        @Override
        public void sendError(int sc) {
        }

        @Override
        public void sendRedirect(String location) {
        }

        @Override
        public void setDateHeader(String name, long date) {
        }

        @Override
        public void addDateHeader(String name, long date) {
        }

        @Override
        public void setHeader(String name, String value) {
        }

        @Override
        public void addHeader(String name, String value) {
        }

        @Override
        public void setIntHeader(String name, int value) {
        }

        @Override
        public void addIntHeader(String name, int value) {
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() {
            return null;
        }

        @Override
        public java.io.PrintWriter getWriter() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String charset) {
        }

        @Override
        public void setContentLength(int len) {
        }

        @Override
        public void setContentLengthLong(long len) {
        }

        @Override
        public void setContentType(String type) {
        }

        @Override
        public void setBufferSize(int size) {
        }

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() {
        }

        @Override
        public void resetBuffer() {
        }

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {
        }

        @Override
        public void setLocale(java.util.Locale loc) {
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.getDefault();
        }
    }

    private static class TestFilterChain implements FilterChain {
        private boolean doFilterCalled = false;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            doFilterCalled = true;
        }

        public boolean wasDoFilterCalled() {
            return doFilterCalled;
        }
    }
}