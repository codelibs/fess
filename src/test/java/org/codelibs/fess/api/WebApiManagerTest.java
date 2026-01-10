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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class WebApiManagerTest extends UnitFessTestCase {

    @Test
    public void test_matches_withPathBasedImplementation() {
        // Test simple path-based matching
        WebApiManager manager = new TestWebApiManager("/api/v1");

        TestHttpServletRequest request = new TestHttpServletRequest();
        request.setRequestURI("/api/v1/search");
        assertTrue(manager.matches(request));

        request.setRequestURI("/api/v2/search");
        assertFalse(manager.matches(request));

        request.setRequestURI("/admin/api");
        assertFalse(manager.matches(request));
    }

    @Test
    public void test_matches_withNullRequest() {
        // Test null request handling
        WebApiManager manager = new TestWebApiManager("/api");
        assertFalse(manager.matches(null));
    }

    @Test
    public void test_matches_withEmptyPath() {
        // Test empty path matching
        WebApiManager manager = new TestWebApiManager("");

        TestHttpServletRequest request = new TestHttpServletRequest();
        request.setRequestURI("");
        assertTrue(manager.matches(request));

        request.setRequestURI("/any/path");
        assertTrue(manager.matches(request));
    }

    @Test
    public void test_matches_withRootPath() {
        // Test root path matching
        WebApiManager manager = new TestWebApiManager("/");

        TestHttpServletRequest request = new TestHttpServletRequest();
        request.setRequestURI("/");
        assertTrue(manager.matches(request));

        request.setRequestURI("/api");
        assertTrue(manager.matches(request));

        request.setRequestURI("/admin/test");
        assertTrue(manager.matches(request));
    }

    @Test
    public void test_matches_withExactMatch() {
        // Test exact path matching
        WebApiManager manager = new ExactMatchWebApiManager("/api/search");

        TestHttpServletRequest request = new TestHttpServletRequest();
        request.setRequestURI("/api/search");
        assertTrue(manager.matches(request));

        request.setRequestURI("/api/search/");
        assertFalse(manager.matches(request));

        request.setRequestURI("/api");
        assertFalse(manager.matches(request));
    }

    @Test
    public void test_process_basicExecution() throws IOException, ServletException {
        // Test basic process execution
        TestWebApiManager manager = new TestWebApiManager("/api");

        TestHttpServletRequest request = new TestHttpServletRequest();
        request.setRequestURI("/api/test");
        TestHttpServletResponse response = new TestHttpServletResponse();
        TestFilterChain chain = new TestFilterChain();

        manager.process(request, response, chain);

        assertTrue(manager.isProcessCalled());
        assertFalse(chain.wasDoFilterCalled());
    }

    @Test
    public void test_process_withChainContinuation() throws IOException, ServletException {
        // Test process with chain continuation
        ChainingWebApiManager manager = new ChainingWebApiManager();

        TestHttpServletRequest request = new TestHttpServletRequest();
        TestHttpServletResponse response = new TestHttpServletResponse();
        TestFilterChain chain = new TestFilterChain();

        manager.process(request, response, chain);

        assertTrue(manager.isProcessCalled());
        assertTrue(chain.wasDoFilterCalled());
    }

    @Test
    public void test_process_withResponseWriting() throws IOException, ServletException {
        // Test process with response writing
        ResponseWritingWebApiManager manager = new ResponseWritingWebApiManager("test response");

        TestHttpServletRequest request = new TestHttpServletRequest();
        TestHttpServletResponse response = new TestHttpServletResponse();
        TestFilterChain chain = new TestFilterChain();

        manager.process(request, response, chain);

        assertEquals("test response", response.getWrittenContent());
        assertEquals(200, response.getStatus());
    }

    @Test
    public void test_process_withErrorHandling() throws IOException, ServletException {
        // Test error handling in process
        ErrorWebApiManager manager = new ErrorWebApiManager();

        TestHttpServletRequest request = new TestHttpServletRequest();
        TestHttpServletResponse response = new TestHttpServletResponse();
        TestFilterChain chain = new TestFilterChain();

        try {
            manager.process(request, response, chain);
            fail("Expected ServletException");
        } catch (ServletException e) {
            assertEquals("Test error", e.getMessage());
        }
    }

    @Test
    public void test_process_withNullParameters() throws IOException, ServletException {
        // Test null parameter handling
        TestWebApiManager manager = new TestWebApiManager("/api");

        try {
            manager.process(null, null, null);
            assertTrue(manager.isProcessCalled());
        } catch (NullPointerException e) {
            // Expected for some implementations
        }
    }

    @Test
    public void test_process_withStatusCodeSetting() throws IOException, ServletException {
        // Test setting different status codes
        StatusCodeWebApiManager manager = new StatusCodeWebApiManager(404);

        TestHttpServletRequest request = new TestHttpServletRequest();
        TestHttpServletResponse response = new TestHttpServletResponse();
        TestFilterChain chain = new TestFilterChain();

        manager.process(request, response, chain);
        assertEquals(404, response.getStatus());

        // Test with different status code
        manager = new StatusCodeWebApiManager(201);
        response = new TestHttpServletResponse();
        manager.process(request, response, chain);
        assertEquals(201, response.getStatus());
    }

    @Test
    public void test_process_withHeaderSetting() throws IOException, ServletException {
        // Test setting headers
        HeaderSettingWebApiManager manager = new HeaderSettingWebApiManager();

        TestHttpServletRequest request = new TestHttpServletRequest();
        TestHttpServletResponse response = new TestHttpServletResponse();
        TestFilterChain chain = new TestFilterChain();

        manager.process(request, response, chain);

        assertEquals("application/json", response.getHeader("Content-Type"));
        assertEquals("no-cache", response.getHeader("Cache-Control"));
    }

    @Test
    public void test_matches_withPatternMatching() throws IOException, ServletException {
        // Test regex pattern matching
        PatternWebApiManager manager = new PatternWebApiManager("^/api/v[0-9]+/.*");

        TestHttpServletRequest request = new TestHttpServletRequest();

        request.setRequestURI("/api/v1/search");
        assertTrue(manager.matches(request));

        request.setRequestURI("/api/v2/label");
        assertTrue(manager.matches(request));

        request.setRequestURI("/api/search");
        assertFalse(manager.matches(request));

        request.setRequestURI("/other/v1/search");
        assertFalse(manager.matches(request));
    }

    @Test
    public void test_matches_withSpecialCharacters() {
        // Test matching with special characters in path
        WebApiManager manager = new TestWebApiManager("/api/");

        TestHttpServletRequest request = new TestHttpServletRequest();
        request.setRequestURI("/api/search?query=test");
        assertTrue(manager.matches(request));

        request.setRequestURI("/api/data#anchor");
        assertTrue(manager.matches(request));
    }

    @Test
    public void test_process_withMultipleOperations() throws IOException, ServletException {
        // Test multiple operations in process
        ComplexWebApiManager manager = new ComplexWebApiManager();

        TestHttpServletRequest request = new TestHttpServletRequest();
        TestHttpServletResponse response = new TestHttpServletResponse();
        TestFilterChain chain = new TestFilterChain();

        manager.process(request, response, chain);

        assertEquals(201, response.getStatus());
        assertEquals("application/json", response.getHeader("Content-Type"));
        assertEquals("Complex response", response.getWrittenContent());
        assertTrue(chain.wasDoFilterCalled());
    }

    // Test implementation classes

    private static class TestWebApiManager implements WebApiManager {
        private final String basePath;
        private boolean processCalled = false;

        public TestWebApiManager(String basePath) {
            this.basePath = basePath;
        }

        @Override
        public boolean matches(HttpServletRequest request) {
            if (request == null) {
                return false;
            }
            String uri = request.getRequestURI();
            return uri != null && uri.startsWith(basePath);
        }

        @Override
        public void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            processCalled = true;
        }

        public boolean isProcessCalled() {
            return processCalled;
        }
    }

    private static class ExactMatchWebApiManager implements WebApiManager {
        private final String path;

        public ExactMatchWebApiManager(String path) {
            this.path = path;
        }

        @Override
        public boolean matches(HttpServletRequest request) {
            if (request == null) {
                return false;
            }
            return path.equals(request.getRequestURI());
        }

        @Override
        public void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            // No-op
        }
    }

    private static class ChainingWebApiManager implements WebApiManager {
        private boolean processCalled = false;

        @Override
        public boolean matches(HttpServletRequest request) {
            return true;
        }

        @Override
        public void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            processCalled = true;
            chain.doFilter(request, response);
        }

        public boolean isProcessCalled() {
            return processCalled;
        }
    }

    private static class ResponseWritingWebApiManager implements WebApiManager {
        private final String responseContent;

        public ResponseWritingWebApiManager(String responseContent) {
            this.responseContent = responseContent;
        }

        @Override
        public boolean matches(HttpServletRequest request) {
            return true;
        }

        @Override
        public void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            response.setStatus(200);
            response.getWriter().write(responseContent);
        }
    }

    private static class ErrorWebApiManager implements WebApiManager {
        @Override
        public boolean matches(HttpServletRequest request) {
            return true;
        }

        @Override
        public void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            throw new ServletException("Test error");
        }
    }

    private static class StatusCodeWebApiManager implements WebApiManager {
        private final int statusCode;

        public StatusCodeWebApiManager(int statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public boolean matches(HttpServletRequest request) {
            return true;
        }

        @Override
        public void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            response.setStatus(statusCode);
        }
    }

    private static class HeaderSettingWebApiManager implements WebApiManager {
        @Override
        public boolean matches(HttpServletRequest request) {
            return true;
        }

        @Override
        public void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            response.setHeader("Content-Type", "application/json");
            response.setHeader("Cache-Control", "no-cache");
        }
    }

    private static class PatternWebApiManager implements WebApiManager {
        private final String pattern;

        public PatternWebApiManager(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean matches(HttpServletRequest request) {
            if (request == null || request.getRequestURI() == null) {
                return false;
            }
            return request.getRequestURI().matches(pattern);
        }

        @Override
        public void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            // Pattern-based processing
        }
    }

    private static class ComplexWebApiManager implements WebApiManager {
        @Override
        public boolean matches(HttpServletRequest request) {
            return true;
        }

        @Override
        public void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            response.setStatus(201);
            response.setHeader("Content-Type", "application/json");
            response.getWriter().write("Complex response");
            chain.doFilter(request, response);
        }
    }

    // Test helper classes

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

    private static class TestHttpServletRequest implements HttpServletRequest {
        private String requestURI = "";

        public void setRequestURI(String requestURI) {
            this.requestURI = requestURI;
        }

        @Override
        public String getRequestURI() {
            return requestURI;
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
        public String getMethod() {
            return "GET";
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
        private int status = 200;
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

        @Override
        public void setHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            return headers.get(name);
        }

        @Override
        public PrintWriter getWriter() {
            return writer;
        }

        public String getWrittenContent() {
            writer.flush();
            return stringWriter.toString();
        }

        // Minimal implementations for other required methods
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
        public void addHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public void setIntHeader(String name, int value) {
        }

        @Override
        public void addIntHeader(String name, int value) {
        }

        @Override
        public java.util.Collection<String> getHeaders(String name) {
            return java.util.Collections.emptyList();
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return headers.keySet();
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public String getContentType() {
            return headers.get("Content-Type");
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
        public void setContentType(String type) {
            headers.put("Content-Type", type);
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
}