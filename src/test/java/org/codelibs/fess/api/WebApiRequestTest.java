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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class WebApiRequestTest extends UnitFessTestCase {

    private WebApiRequest webApiRequest;
    private MockHttpServletRequest mockRequest;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        mockRequest = new MockHttpServletRequest();
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        webApiRequest = null;
        mockRequest = null;
        super.tearDown();
    }

    // Test constructor with various servlet path values
    @Test
    public void test_constructor_withNormalServletPath() {
        final String servletPath = "/api/v1/search";
        webApiRequest = new WebApiRequest(mockRequest, servletPath);
        assertNotNull(webApiRequest);
        assertEquals(servletPath, webApiRequest.servletPath);
    }

    @Test
    public void test_constructor_withEmptyServletPath() {
        final String servletPath = "";
        webApiRequest = new WebApiRequest(mockRequest, servletPath);
        assertNotNull(webApiRequest);
        assertEquals(servletPath, webApiRequest.servletPath);
    }

    @Test
    public void test_constructor_withNullServletPath() {
        final String servletPath = null;
        webApiRequest = new WebApiRequest(mockRequest, servletPath);
        assertNotNull(webApiRequest);
        assertNull(webApiRequest.servletPath);
    }

    @Test
    public void test_constructor_withSpecialCharactersInPath() {
        final String servletPath = "/api/v1/search?query=test&page=1";
        webApiRequest = new WebApiRequest(mockRequest, servletPath);
        assertNotNull(webApiRequest);
        assertEquals(servletPath, webApiRequest.servletPath);
    }

    // Test getServletPath method without SAStruts.method in query string
    @Test
    public void test_getServletPath_withoutSAStrutsMethod_returnsCustomPath() {
        final String customPath = "/api/v1/search";
        mockRequest.setQueryString("query=test&page=1");
        webApiRequest = new WebApiRequest(mockRequest, customPath);

        assertEquals(customPath, webApiRequest.getServletPath());
    }

    @Test
    public void test_getServletPath_withNullQueryString_returnsCustomPath() {
        final String customPath = "/api/v1/documents";
        mockRequest.setQueryString(null);
        webApiRequest = new WebApiRequest(mockRequest, customPath);

        assertEquals(customPath, webApiRequest.getServletPath());
    }

    @Test
    public void test_getServletPath_withEmptyQueryString_returnsCustomPath() {
        final String customPath = "/api/v1/users";
        mockRequest.setQueryString("");
        webApiRequest = new WebApiRequest(mockRequest, customPath);

        assertEquals(customPath, webApiRequest.getServletPath());
    }

    // Test getServletPath method with SAStruts.method in query string
    @Test
    public void test_getServletPath_withSAStrutsMethodAtBeginning_returnsSuperPath() {
        final String originalPath = "/original/path";
        final String customPath = "/api/v1/search";
        mockRequest.setServletPath(originalPath);
        mockRequest.setQueryString("SAStruts.method=execute&query=test");
        webApiRequest = new WebApiRequest(mockRequest, customPath);

        assertEquals(originalPath, webApiRequest.getServletPath());
    }

    @Test
    public void test_getServletPath_withSAStrutsMethodInMiddle_returnsSuperPath() {
        final String originalPath = "/original/path";
        final String customPath = "/api/v1/search";
        mockRequest.setServletPath(originalPath);
        mockRequest.setQueryString("query=test&SAStruts.method=execute&page=1");
        webApiRequest = new WebApiRequest(mockRequest, customPath);

        assertEquals(originalPath, webApiRequest.getServletPath());
    }

    @Test
    public void test_getServletPath_withSAStrutsMethodAtEnd_returnsSuperPath() {
        final String originalPath = "/original/path";
        final String customPath = "/api/v1/search";
        mockRequest.setServletPath(originalPath);
        mockRequest.setQueryString("query=test&page=1&SAStruts.method=execute");
        webApiRequest = new WebApiRequest(mockRequest, customPath);

        assertEquals(originalPath, webApiRequest.getServletPath());
    }

    @Test
    public void test_getServletPath_withOnlySAStrutsMethod_returnsSuperPath() {
        final String originalPath = "/original/path";
        final String customPath = "/api/v1/search";
        mockRequest.setServletPath(originalPath);
        mockRequest.setQueryString("SAStruts.method=execute");
        webApiRequest = new WebApiRequest(mockRequest, customPath);

        assertEquals(originalPath, webApiRequest.getServletPath());
    }

    // Test edge cases for SAStruts.method detection
    @Test
    public void test_getServletPath_withSAStrutsMethodSubstring_returnsSuperPath() {
        final String originalPath = "/original/path";
        final String customPath = "/api/v1/search";
        mockRequest.setServletPath(originalPath);
        // Contains "SAStruts.method" as substring in parameter value
        mockRequest.setQueryString("query=SAStruts.methodTest&page=1");
        webApiRequest = new WebApiRequest(mockRequest, customPath);

        // Should return super.getServletPath() since query contains "SAStruts.method" substring
        assertEquals(originalPath, webApiRequest.getServletPath());
    }

    @Test
    public void test_getServletPath_withPartialSAStrutsMethod_returnsCustomPath() {
        final String originalPath = "/original/path";
        final String customPath = "/api/v1/search";
        mockRequest.setServletPath(originalPath);
        // Contains partial match but not the exact string "SAStruts.method"
        mockRequest.setQueryString("query=test&SAStruts=value&method=execute");
        webApiRequest = new WebApiRequest(mockRequest, customPath);

        assertEquals(customPath, webApiRequest.getServletPath());
    }

    // Test wrapper functionality inheritance
    @Test
    public void test_inheritedMethods_areProperlyDelegated() {
        final String customPath = "/api/v1/test";
        final String testHeader = "X-Test-Header";
        final String testHeaderValue = "TestValue";

        mockRequest.addHeader(testHeader, testHeaderValue);
        mockRequest.setMethod("POST");
        mockRequest.setRequestURI("/test/uri");

        webApiRequest = new WebApiRequest(mockRequest, customPath);

        // Verify that other methods are properly delegated
        assertEquals("POST", webApiRequest.getMethod());
        assertEquals("/test/uri", webApiRequest.getRequestURI());
        assertEquals(testHeaderValue, webApiRequest.getHeader(testHeader));
    }

    // Test multiple calls to getServletPath
    @Test
    public void test_getServletPath_multipleCallsReturnSameResult() {
        final String customPath = "/api/v1/consistent";
        mockRequest.setQueryString("query=test");
        webApiRequest = new WebApiRequest(mockRequest, customPath);

        String firstCall = webApiRequest.getServletPath();
        String secondCall = webApiRequest.getServletPath();
        String thirdCall = webApiRequest.getServletPath();

        assertEquals(customPath, firstCall);
        assertEquals(firstCall, secondCall);
        assertEquals(secondCall, thirdCall);
    }

    // Test with changing query string (though typically wouldn't change in real scenario)
    @Test
    public void test_getServletPath_withChangingQueryString() {
        final String originalPath = "/original/path";
        final String customPath = "/api/v1/dynamic";
        mockRequest.setServletPath(originalPath);

        webApiRequest = new WebApiRequest(mockRequest, customPath);

        // First call without SAStruts.method
        mockRequest.setQueryString("query=test");
        assertEquals(customPath, webApiRequest.getServletPath());

        // Second call with SAStruts.method
        mockRequest.setQueryString("SAStruts.method=execute");
        assertEquals(originalPath, webApiRequest.getServletPath());

        // Third call without SAStruts.method again
        mockRequest.setQueryString("page=1");
        assertEquals(customPath, webApiRequest.getServletPath());
    }

    // Simple mock implementation of HttpServletRequest for testing
    private static class MockHttpServletRequest implements HttpServletRequest {
        private String queryString;
        private String servletPath;
        private String method = "GET";
        private String requestURI = "/";
        private final Map<String, String> headers = new java.util.HashMap<>();

        public void setQueryString(String queryString) {
            this.queryString = queryString;
        }

        public void setServletPath(String servletPath) {
            this.servletPath = servletPath;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public void setRequestURI(String requestURI) {
            this.requestURI = requestURI;
        }

        public void addHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public String getQueryString() {
            return queryString;
        }

        @Override
        public String getServletPath() {
            return servletPath;
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public String getRequestURI() {
            return requestURI;
        }

        @Override
        public String getHeader(String name) {
            return headers.get(name);
        }

        // All other methods with default implementations
        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public Cookie[] getCookies() {
            return new Cookie[0];
        }

        @Override
        public long getDateHeader(String name) {
            return -1;
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return null;
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
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return null;
        }

        @Override
        public HttpSession getSession(boolean create) {
            return null;
        }

        @Override
        public HttpSession getSession() {
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
        public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
            return false;
        }

        @Override
        public void login(String username, String password) throws ServletException {
        }

        @Override
        public void logout() throws ServletException {
        }

        @Override
        public Collection<Part> getParts() throws IOException, ServletException {
            return null;
        }

        @Override
        public Part getPart(String name) throws IOException, ServletException {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
            return null;
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
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
        public ServletInputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public String getParameter(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return null;
        }

        @Override
        public String[] getParameterValues(String name) {
            return null;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return null;
        }

        @Override
        public String getProtocol() {
            return null;
        }

        @Override
        public String getScheme() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return null;
        }

        @Override
        public String getRemoteHost() {
            return null;
        }

        @Override
        public void setAttribute(String name, Object o) {
        }

        @Override
        public void removeAttribute(String name) {
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public String getLocalAddr() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public AsyncContext startAsync() throws IllegalStateException {
            return null;
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
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
        public AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public DispatcherType getDispatcherType() {
            return null;
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
        public ServletConnection getServletConnection() {
            return null;
        }
    }
}