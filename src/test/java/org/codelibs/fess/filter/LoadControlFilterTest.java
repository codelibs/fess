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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LoadControlFilterTest extends UnitFessTestCase {

    private LoadControlFilter loadControlFilter;
    private TestableLoadControlFilter testableFilter;
    private TestHttpServletRequest mockRequest;
    private TestHttpServletResponse mockResponse;
    private TestFilterChain mockFilterChain;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        loadControlFilter = new LoadControlFilter();
        testableFilter = new TestableLoadControlFilter();
        mockRequest = new TestHttpServletRequest();
        mockResponse = new TestHttpServletResponse();
        mockFilterChain = new TestFilterChain();
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    private void resetMocks() {
        mockRequest = new TestHttpServletRequest();
        mockResponse = new TestHttpServletResponse();
        mockFilterChain = new TestFilterChain();
    }

    private void setConfig(final int webLoadControl, final int apiLoadControl) {
        testableFilter.setWebLoadControl(webLoadControl);
        testableFilter.setApiLoadControl(apiLoadControl);
    }

    // ===================================================================================
    //                                                                   Disabled / Bypass
    //                                                                   =================

    @Test
    public void test_doFilter_webDisabled() throws IOException, ServletException {
        setConfig(100, 100);
        testableFilter.setCpuPercent((short) 90);
        mockRequest.setRequestURI("/search");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
        assertEquals(0, mockResponse.getStatus());
    }

    @Test
    public void test_doFilter_apiDisabled() throws IOException, ServletException {
        setConfig(100, 100);
        testableFilter.setCpuPercent((short) 90);
        mockRequest.setRequestURI("/api/v1/search");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
        assertEquals(0, mockResponse.getStatus());
    }

    @Test
    public void test_doFilter_aboveHundredThreshold() throws IOException, ServletException {
        setConfig(200, 200);
        testableFilter.setCpuPercent((short) 90);
        mockRequest.setRequestURI("/search");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    // ===================================================================================
    //                                                                  CPU Below Threshold
    //                                                                  ==================

    @Test
    public void test_doFilter_webBelowThreshold() throws IOException, ServletException {
        setConfig(80, 100);
        testableFilter.setCpuPercent((short) 50);
        mockRequest.setRequestURI("/search");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
        assertEquals(0, mockResponse.getStatus());
    }

    @Test
    public void test_doFilter_apiBelowThreshold() throws IOException, ServletException {
        setConfig(100, 80);
        testableFilter.setCpuPercent((short) 50);
        mockRequest.setRequestURI("/api/v1/search");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
        assertEquals(0, mockResponse.getStatus());
    }

    @Test
    public void test_doFilter_cpuAtZero() throws IOException, ServletException {
        setConfig(80, 80);
        testableFilter.setCpuPercent((short) 0);
        mockRequest.setRequestURI("/search");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    // ===================================================================================
    //                                                                 CPU Above Threshold
    //                                                                 ===================

    @Test
    public void test_doFilter_webAboveThreshold() throws IOException, ServletException {
        setConfig(80, 100);
        testableFilter.setCpuPercent((short) 85);
        mockRequest.setRequestURI("/search");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertFalse(mockFilterChain.wasDoFilterCalled());
        assertEquals(429, mockResponse.getSendErrorCode());
    }

    @Test
    public void test_doFilter_webAtThreshold() throws IOException, ServletException {
        setConfig(80, 100);
        testableFilter.setCpuPercent((short) 80);
        mockRequest.setRequestURI("/search");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertFalse(mockFilterChain.wasDoFilterCalled());
        assertEquals(429, mockResponse.getSendErrorCode());
    }

    @Test
    public void test_doFilter_webJustBelowThreshold() throws IOException, ServletException {
        setConfig(80, 100);
        testableFilter.setCpuPercent((short) 79);
        mockRequest.setRequestURI("/search");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    @Test
    public void test_doFilter_apiAboveThreshold() throws IOException, ServletException {
        setConfig(100, 80);
        testableFilter.setCpuPercent((short) 85);
        mockRequest.setRequestURI("/api/v1/search");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertFalse(mockFilterChain.wasDoFilterCalled());
        assertEquals(429, mockResponse.getStatus());
        assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
        assertEquals("60", mockResponse.getHeaderValue("Retry-After"));
        assertTrue(mockResponse.getWriterOutput().contains("\"status\":9"));
        assertTrue(mockResponse.getWriterOutput().contains("\"retry_after\":60"));
    }

    @Test
    public void test_doFilter_apiAtThreshold() throws IOException, ServletException {
        setConfig(100, 80);
        testableFilter.setCpuPercent((short) 80);
        mockRequest.setRequestURI("/api/v1/search");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertFalse(mockFilterChain.wasDoFilterCalled());
        assertEquals(429, mockResponse.getStatus());
    }

    // ===================================================================================
    //                                                              Independent Thresholds
    //                                                              ======================

    @Test
    public void test_doFilter_webBlockedApiAllowed() throws IOException, ServletException {
        setConfig(50, 90);
        testableFilter.setCpuPercent((short) 60);

        mockRequest.setRequestURI("/search");
        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertFalse(mockFilterChain.wasDoFilterCalled());
        assertEquals(429, mockResponse.getSendErrorCode());

        resetMocks();
        mockRequest.setRequestURI("/api/v1/search");
        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    @Test
    public void test_doFilter_apiBlockedWebAllowed() throws IOException, ServletException {
        setConfig(90, 50);
        testableFilter.setCpuPercent((short) 60);

        mockRequest.setRequestURI("/api/v1/search");
        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertFalse(mockFilterChain.wasDoFilterCalled());
        assertEquals(429, mockResponse.getStatus());

        resetMocks();
        mockRequest.setRequestURI("/search");
        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    // ===================================================================================
    //                                                                      Excluded Paths
    //                                                                      ==============

    @Test
    public void test_doFilter_adminPathExcluded() throws IOException, ServletException {
        setConfig(10, 10);
        testableFilter.setCpuPercent((short) 99);
        mockRequest.setRequestURI("/admin/dashboard/");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    @Test
    public void test_doFilter_adminSubpathExcluded() throws IOException, ServletException {
        setConfig(10, 10);
        testableFilter.setCpuPercent((short) 99);
        mockRequest.setRequestURI("/admin/general/");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    @Test
    public void test_doFilter_errorPathExcluded() throws IOException, ServletException {
        setConfig(10, 10);
        testableFilter.setCpuPercent((short) 99);
        mockRequest.setRequestURI("/error/busy/");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    @Test
    public void test_doFilter_loginPathExcluded() throws IOException, ServletException {
        setConfig(10, 10);
        testableFilter.setCpuPercent((short) 99);
        mockRequest.setRequestURI("/login/");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    // ===================================================================================
    //                                                                   Static Resources
    //                                                                   =================

    @Test
    public void test_doFilter_cssExcluded() throws IOException, ServletException {
        setConfig(10, 10);
        testableFilter.setCpuPercent((short) 99);
        mockRequest.setRequestURI("/css/style.css");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    @Test
    public void test_doFilter_jsExcluded() throws IOException, ServletException {
        setConfig(10, 10);
        testableFilter.setCpuPercent((short) 99);
        mockRequest.setRequestURI("/js/search.js");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    @Test
    public void test_doFilter_imageExcluded() throws IOException, ServletException {
        setConfig(10, 10);
        testableFilter.setCpuPercent((short) 99);

        String[] imageUris = { "/images/logo.png", "/img/bg.jpg", "/icons/icon.gif", "/favicon.ico", "/img/logo.svg" };
        for (String uri : imageUris) {
            resetMocks();
            mockRequest.setRequestURI(uri);
            testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
            assertTrue("Expected pass-through for: " + uri, mockFilterChain.wasDoFilterCalled());
        }
    }

    @Test
    public void test_doFilter_fontExcluded() throws IOException, ServletException {
        setConfig(10, 10);
        testableFilter.setCpuPercent((short) 99);

        String[] fontUris = { "/fonts/roboto.woff", "/fonts/roboto.woff2", "/fonts/icon.ttf", "/fonts/icon.eot" };
        for (String uri : fontUris) {
            resetMocks();
            mockRequest.setRequestURI(uri);
            testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
            assertTrue("Expected pass-through for: " + uri, mockFilterChain.wasDoFilterCalled());
        }
    }

    // ===================================================================================
    //                                                                    isExcludedPath
    //                                                                    ================

    @Test
    public void test_isExcludedPath() {
        assertTrue(loadControlFilter.isExcludedPath("/admin"));
        assertTrue(loadControlFilter.isExcludedPath("/admin/"));
        assertTrue(loadControlFilter.isExcludedPath("/admin/dashboard/"));
        assertTrue(loadControlFilter.isExcludedPath("/error"));
        assertTrue(loadControlFilter.isExcludedPath("/error/"));
        assertTrue(loadControlFilter.isExcludedPath("/error/busy/"));
        assertTrue(loadControlFilter.isExcludedPath("/login"));
        assertTrue(loadControlFilter.isExcludedPath("/login/"));
        assertTrue(loadControlFilter.isExcludedPath("/login/?type=logout"));

        assertTrue(loadControlFilter.isExcludedPath("/css/style.css"));
        assertTrue(loadControlFilter.isExcludedPath("/js/app.js"));
        assertTrue(loadControlFilter.isExcludedPath("/img/logo.png"));
        assertTrue(loadControlFilter.isExcludedPath("/img/bg.jpg"));
        assertTrue(loadControlFilter.isExcludedPath("/img/anim.gif"));
        assertTrue(loadControlFilter.isExcludedPath("/favicon.ico"));
        assertTrue(loadControlFilter.isExcludedPath("/img/logo.svg"));
        assertTrue(loadControlFilter.isExcludedPath("/fonts/f.woff"));
        assertTrue(loadControlFilter.isExcludedPath("/fonts/f.woff2"));
        assertTrue(loadControlFilter.isExcludedPath("/fonts/f.ttf"));
        assertTrue(loadControlFilter.isExcludedPath("/fonts/f.eot"));

        assertFalse(loadControlFilter.isExcludedPath("/search"));
        assertFalse(loadControlFilter.isExcludedPath("/search/"));
        assertFalse(loadControlFilter.isExcludedPath("/api/v1/search"));
        assertFalse(loadControlFilter.isExcludedPath("/"));
        assertFalse(loadControlFilter.isExcludedPath("/help/"));
    }

    // ===================================================================================
    //                                                                  API JSON Response
    //                                                                  ==================

    @Test
    public void test_sendApiResponse() throws IOException {
        loadControlFilter.sendApiResponse(mockResponse);

        assertEquals(429, mockResponse.getStatus());
        assertEquals("application/json;charset=UTF-8", mockResponse.getContentType());
        assertEquals("60", mockResponse.getHeaderValue("Retry-After"));

        String output = mockResponse.getWriterOutput();
        assertTrue(output.contains("\"response\""));
        assertTrue(output.contains("\"status\":9"));
        assertTrue(output.contains("\"message\":\"Server is busy."));
        assertTrue(output.contains("\"retry_after\":60"));
    }

    // ===================================================================================
    //                                                                  Context Path
    //                                                                  ==============

    @Test
    public void test_doFilter_withContextPath() throws IOException, ServletException {
        setConfig(50, 100);
        testableFilter.setCpuPercent((short) 60);
        mockRequest.setContextPath("/fess");
        mockRequest.setRequestURI("/fess/search");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertFalse(mockFilterChain.wasDoFilterCalled());
        assertEquals(429, mockResponse.getSendErrorCode());
    }

    @Test
    public void test_doFilter_adminWithContextPath() throws IOException, ServletException {
        setConfig(10, 10);
        testableFilter.setCpuPercent((short) 99);
        mockRequest.setContextPath("/fess");
        mockRequest.setRequestURI("/fess/admin/dashboard/");

        testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    // ===================================================================================
    //                                                                    Various API Paths
    //                                                                    ================

    @Test
    public void test_doFilter_apiSubpaths() throws IOException, ServletException {
        setConfig(100, 50);
        testableFilter.setCpuPercent((short) 60);

        String[] apiPaths = { "/api/v1/search", "/api/v1/documents", "/api/v2/suggest" };
        for (String path : apiPaths) {
            resetMocks();
            mockRequest.setRequestURI(path);
            testableFilter.doFilter(mockRequest, mockResponse, mockFilterChain);
            assertFalse("Expected block for: " + path, mockFilterChain.wasDoFilterCalled());
            assertEquals(429, mockResponse.getStatus());
        }
    }

    // ===================================================================================
    //                                                            ComponentUtil.available
    //                                                            ========================

    @Test
    public void test_doFilter_componentNotAvailable() throws IOException, ServletException {
        // The real LoadControlFilter with ComponentUtil.available() returning false
        // should pass through without blocking
        mockRequest.setRequestURI("/search");

        loadControlFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        assertTrue(mockFilterChain.wasDoFilterCalled());
    }

    // ===================================================================================
    //                                                                    Helper Classes
    //                                                                    ================

    /**
     * Testable subclass that bypasses ComponentUtil dependencies.
     * This allows testing the filter logic without requiring the DI container.
     */
    private static class TestableLoadControlFilter extends LoadControlFilter {
        private short cpuPercent = 0;
        private int webLoadControl = 0;
        private int apiLoadControl = 0;

        public void setCpuPercent(final short cpuPercent) {
            this.cpuPercent = cpuPercent;
        }

        public void setWebLoadControl(final int webLoadControl) {
            this.webLoadControl = webLoadControl;
        }

        public void setApiLoadControl(final int apiLoadControl) {
            this.apiLoadControl = apiLoadControl;
        }

        @Override
        public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
                throws IOException, ServletException {
            final HttpServletRequest httpRequest = (HttpServletRequest) request;
            final HttpServletResponse httpResponse = (HttpServletResponse) response;
            final String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

            if (isExcludedPath(path)) {
                chain.doFilter(request, response);
                return;
            }

            final boolean isApiPath = path.startsWith("/api/");
            final int threshold = isApiPath ? apiLoadControl : webLoadControl;

            if (threshold >= 100) {
                chain.doFilter(request, response);
                return;
            }

            if (cpuPercent < threshold) {
                chain.doFilter(request, response);
                return;
            }

            if (isApiPath) {
                sendApiResponse(httpResponse);
            } else {
                httpResponse.sendError(429);
            }
        }
    }

    private static class TestHttpServletRequest implements HttpServletRequest {
        private String requestURI = "/";
        private String contextPath = "";

        public void setRequestURI(final String requestURI) {
            this.requestURI = requestURI;
        }

        public void setContextPath(final String contextPath) {
            this.contextPath = contextPath;
        }

        @Override
        public String getRequestURI() {
            return requestURI;
        }

        @Override
        public String getContextPath() {
            return contextPath;
        }

        @Override
        public String getMethod() {
            return "GET";
        }

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
        public String getHeader(String name) {
            return null;
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
        public StringBuffer getRequestURL() {
            return new StringBuffer("http://localhost:8080" + requestURI);
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
        private int sendErrorCode = 0;
        private String contentType = null;
        private final Map<String, String> headers = new HashMap<>();
        private final StringWriter stringWriter = new StringWriter();
        private final PrintWriter writer = new PrintWriter(stringWriter);

        @Override
        public void setStatus(int sc) {
            this.status = sc;
        }

        @Override
        public int getStatus() {
            return status;
        }

        public int getSendErrorCode() {
            return sendErrorCode;
        }

        public String getContentType() {
            return contentType;
        }

        public String getHeaderValue(final String name) {
            return headers.get(name);
        }

        public String getWriterOutput() {
            writer.flush();
            return stringWriter.toString();
        }

        @Override
        public void setContentType(String type) {
            this.contentType = type;
        }

        @Override
        public void setHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public PrintWriter getWriter() {
            return writer;
        }

        @Override
        public void sendError(int sc) {
            this.sendErrorCode = sc;
        }

        @Override
        public void sendError(int sc, String msg) {
            this.sendErrorCode = sc;
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return headers.keySet();
        }

        @Override
        public java.util.Collection<String> getHeaders(String name) {
            String value = headers.get(name);
            return value != null ? java.util.Collections.singletonList(value) : java.util.Collections.emptyList();
        }

        @Override
        public String getHeader(String name) {
            return headers.get(name);
        }

        @Override
        public void addCookie(jakarta.servlet.http.Cookie cookie) {
        }

        @Override
        public boolean containsHeader(String name) {
            return headers.containsKey(name);
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
        public void sendRedirect(String location) {
        }

        @Override
        public void setDateHeader(String name, long date) {
        }

        @Override
        public void addDateHeader(String name, long date) {
        }

        @Override
        public void addHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public void setIntHeader(String name, int value) {
            headers.put(name, String.valueOf(value));
        }

        @Override
        public void addIntHeader(String name, int value) {
            headers.put(name, String.valueOf(value));
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() {
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
