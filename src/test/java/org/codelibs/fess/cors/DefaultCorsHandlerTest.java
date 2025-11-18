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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DefaultCorsHandlerTest extends UnitFessTestCase {

    private DefaultCorsHandler defaultCorsHandler;
    private TestHttpServletRequest mockRequest;
    private TestHttpServletResponse mockResponse;
    private TestFessConfig testFessConfig;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        defaultCorsHandler = new DefaultCorsHandler();
        mockRequest = new TestHttpServletRequest();
        mockResponse = new TestHttpServletResponse();
        testFessConfig = new TestFessConfig();
        defaultCorsHandler.fessConfig = testFessConfig;
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    // Test basic CORS headers
    public void test_process_basicHeaders() {
        String origin = "https://example.com";

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertEquals(origin, mockResponse.getHeader("Access-Control-Allow-Origin"));
        assertEquals("GET, POST, OPTIONS, DELETE, PUT", mockResponse.getHeader("Access-Control-Allow-Methods"));
        assertEquals("Origin, Content-Type, Accept, Authorization, X-Requested-With",
                mockResponse.getHeader("Access-Control-Allow-Headers"));
        assertEquals("3600", mockResponse.getHeader("Access-Control-Max-Age"));
    }

    // Test credentials header when set to "true"
    public void test_process_credentialsTrue() {
        String origin = "https://example.com";
        testFessConfig.setApiCorsAllowCredentials("true");

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertEquals("true", mockResponse.getHeader("Access-Control-Allow-Credentials"));
    }

    // Test credentials header when set to "TRUE" (case insensitive)
    public void test_process_credentialsTrueUpperCase() {
        String origin = "https://example.com";
        testFessConfig.setApiCorsAllowCredentials("TRUE");

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertEquals("true", mockResponse.getHeader("Access-Control-Allow-Credentials"));
    }

    // Test credentials header when set to "TrUe" (mixed case)
    public void test_process_credentialsTrueMixedCase() {
        String origin = "https://example.com";
        testFessConfig.setApiCorsAllowCredentials("TrUe");

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertEquals("true", mockResponse.getHeader("Access-Control-Allow-Credentials"));
    }

    // Test credentials header not added when set to "false"
    public void test_process_credentialsFalse() {
        String origin = "https://example.com";
        testFessConfig.setApiCorsAllowCredentials("false");

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertNull(mockResponse.getHeader("Access-Control-Allow-Credentials"));
    }

    // Test credentials header not added when empty
    public void test_process_credentialsEmpty() {
        String origin = "https://example.com";
        testFessConfig.setApiCorsAllowCredentials("");

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertNull(mockResponse.getHeader("Access-Control-Allow-Credentials"));
    }

    // Test credentials header not added when null
    public void test_process_credentialsNull() {
        String origin = "https://example.com";
        testFessConfig.setApiCorsAllowCredentials(null);

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertNull(mockResponse.getHeader("Access-Control-Allow-Credentials"));
    }

    // Test expose headers when configured
    public void test_process_exposeHeadersConfigured() {
        String origin = "https://example.com";
        testFessConfig.setApiCorsExposeHeaders("X-Custom-Header, X-Another-Header");

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertEquals("X-Custom-Header, X-Another-Header", mockResponse.getHeader("Access-Control-Expose-Headers"));
    }

    // Test expose headers not added when empty
    public void test_process_exposeHeadersEmpty() {
        String origin = "https://example.com";
        testFessConfig.setApiCorsExposeHeaders("");

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertNull(mockResponse.getHeader("Access-Control-Expose-Headers"));
    }

    // Test expose headers not added when null
    public void test_process_exposeHeadersNull() {
        String origin = "https://example.com";
        testFessConfig.setApiCorsExposeHeaders(null);

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertNull(mockResponse.getHeader("Access-Control-Expose-Headers"));
    }

    // Test Vary header is always added
    public void test_process_varyHeader() {
        String origin = "https://example.com";

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertEquals("Origin", mockResponse.getHeader("Vary"));
    }

    // Test private network access when request header is "true"
    public void test_process_privateNetworkAccessTrue() {
        String origin = "https://example.com";
        mockRequest.addHeader("Access-Control-Request-Private-Network", "true");

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertEquals("true", mockResponse.getHeader("Access-Control-Allow-Private-Network"));
    }

    // Test private network access when request header is "TRUE" (case insensitive)
    public void test_process_privateNetworkAccessTrueUpperCase() {
        String origin = "https://example.com";
        mockRequest.addHeader("Access-Control-Request-Private-Network", "TRUE");

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertEquals("true", mockResponse.getHeader("Access-Control-Allow-Private-Network"));
    }

    // Test private network access when request header is "false"
    public void test_process_privateNetworkAccessFalse() {
        String origin = "https://example.com";
        mockRequest.addHeader("Access-Control-Request-Private-Network", "false");

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertNull(mockResponse.getHeader("Access-Control-Allow-Private-Network"));
    }

    // Test private network access when request header is not present
    public void test_process_privateNetworkAccessNotPresent() {
        String origin = "https://example.com";

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertNull(mockResponse.getHeader("Access-Control-Allow-Private-Network"));
    }

    // Test with different origin formats
    public void test_process_differentOrigins() {
        String[] origins = { "http://localhost:8080", "https://example.com", "http://subdomain.example.com:3000",
                "https://example.co.uk" };

        for (String origin : origins) {
            mockResponse = new TestHttpServletResponse(); // Reset response
            defaultCorsHandler.process(origin, mockRequest, mockResponse);
            assertEquals(origin, mockResponse.getHeader("Access-Control-Allow-Origin"));
        }
    }

    // Test complete CORS response with all headers
    public void test_process_completeResponse() {
        String origin = "https://example.com";
        testFessConfig.setApiCorsAllowCredentials("true");
        testFessConfig.setApiCorsExposeHeaders("X-Total-Count, X-Page-Number");
        mockRequest.addHeader("Access-Control-Request-Private-Network", "true");

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertEquals(origin, mockResponse.getHeader("Access-Control-Allow-Origin"));
        assertEquals("GET, POST, OPTIONS, DELETE, PUT", mockResponse.getHeader("Access-Control-Allow-Methods"));
        assertEquals("Origin, Content-Type, Accept, Authorization, X-Requested-With",
                mockResponse.getHeader("Access-Control-Allow-Headers"));
        assertEquals("3600", mockResponse.getHeader("Access-Control-Max-Age"));
        assertEquals("true", mockResponse.getHeader("Access-Control-Allow-Credentials"));
        assertEquals("X-Total-Count, X-Page-Number", mockResponse.getHeader("Access-Control-Expose-Headers"));
        assertEquals("Origin", mockResponse.getHeader("Vary"));
        assertEquals("true", mockResponse.getHeader("Access-Control-Allow-Private-Network"));
    }

    // Test FessConfig caching (verify fessConfig is used)
    public void test_fessConfigCaching() {
        String origin = "https://example.com";
        testFessConfig.setApiCorsAllowMethods("GET, POST");

        defaultCorsHandler.process(origin, mockRequest, mockResponse);

        assertEquals("GET, POST", mockResponse.getHeader("Access-Control-Allow-Methods"));
        assertTrue(testFessConfig.wasMethodsCalled());
    }

    // Test constructor
    public void test_constructor() {
        DefaultCorsHandler handler = new DefaultCorsHandler();
        assertNotNull(handler);
    }

    // Helper classes for testing

    private static class TestHttpServletRequest implements HttpServletRequest {
        private final Map<String, String> headers = new HashMap<>();

        public void addHeader(String name, String value) {
            headers.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            return headers.get(name);
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
            return java.util.Collections.enumeration(headers.keySet());
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
        private final Map<String, String> headers = new HashMap<>();

        @Override
        public void addHeader(String name, String value) {
            headers.put(name, value);
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
        public java.util.Collection<String> getHeaderNames() {
            return headers.keySet();
        }

        @Override
        public java.util.Collection<String> getHeaders(String name) {
            String value = headers.get(name);
            if (value == null) {
                return java.util.Collections.emptyList();
            }
            return java.util.Collections.singletonList(value);
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
        public void setIntHeader(String name, int value) {
        }

        @Override
        public void addIntHeader(String name, int value) {
        }

        @Override
        public void setStatus(int sc) {
        }

        @Override
        public int getStatus() {
            return 0;
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

    private static class TestFessConfig implements FessConfig {
        private String apiCorsAllowOrigin = "*";
        private String apiCorsAllowMethods = "GET, POST, OPTIONS, DELETE, PUT";
        private String apiCorsMaxAge = "3600";
        private String apiCorsAllowHeaders = "Origin, Content-Type, Accept, Authorization, X-Requested-With";
        private String apiCorsAllowCredentials = "true";
        private String apiCorsExposeHeaders = "";
        private boolean methodsCalled = false;

        public void setApiCorsAllowMethods(String methods) {
            this.apiCorsAllowMethods = methods;
        }

        public void setApiCorsAllowCredentials(String credentials) {
            this.apiCorsAllowCredentials = credentials;
        }

        public void setApiCorsExposeHeaders(String exposeHeaders) {
            this.apiCorsExposeHeaders = exposeHeaders;
        }

        public boolean wasMethodsCalled() {
            return methodsCalled;
        }

        @Override
        public String getApiCorsAllowOrigin() {
            return apiCorsAllowOrigin;
        }

        @Override
        public String getApiCorsAllowMethods() {
            methodsCalled = true;
            return apiCorsAllowMethods;
        }

        @Override
        public String getApiCorsMaxAge() {
            return apiCorsMaxAge;
        }

        @Override
        public Integer getApiCorsMaxAgeAsInteger() {
            return Integer.parseInt(apiCorsMaxAge);
        }

        @Override
        public String getApiCorsAllowHeaders() {
            return apiCorsAllowHeaders;
        }

        @Override
        public String getApiCorsAllowCredentials() {
            return apiCorsAllowCredentials;
        }

        @Override
        public boolean isApiCorsAllowCredentials() {
            return "true".equals(apiCorsAllowCredentials);
        }

        @Override
        public String getApiCorsExposeHeaders() {
            return apiCorsExposeHeaders;
        }

        @Override
        public List<String> getApiCorsAllowOriginList() {
            List<String> list = new ArrayList<>();
            if (apiCorsAllowOrigin != null && !apiCorsAllowOrigin.isEmpty()) {
                list.add(apiCorsAllowOrigin);
            }
            return list;
        }

        // Implement other required methods with default values
        @Override
        public String getDomainTitle() {
            return "Test";
        }

        @Override
        public String getSystemProperty(String key) {
            return null;
        }

        @Override
        public String getSystemProperty(String key, String defaultValue) {
            return defaultValue;
        }

        @Override
        public String getPasswordInvalidAdminPasswords() {
            return "";
        }
    }
}
