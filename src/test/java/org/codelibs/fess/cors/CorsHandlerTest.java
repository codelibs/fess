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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CorsHandlerTest extends UnitFessTestCase {

    private CorsHandler corsHandler;
    private Map<String, List<String>> responseHeaders;
    private boolean processCalled;
    private String processOrigin;
    private ServletRequest processRequest;
    private ServletResponse processResponse;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        responseHeaders = new HashMap<>();
        processCalled = false;
        processOrigin = null;
        processRequest = null;
        processResponse = null;

        // Create a test implementation of CorsHandler
        corsHandler = new CorsHandler() {
            @Override
            public void process(String origin, ServletRequest request, ServletResponse response) {
                processCalled = true;
                processOrigin = origin;
                processRequest = request;
                processResponse = response;

                // Simulate adding headers for testing
                if (response instanceof HttpServletResponse) {
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    httpResponse.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                    httpResponse.addHeader(ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, OPTIONS");
                    httpResponse.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type");
                    httpResponse.addHeader(ACCESS_CONTROL_MAX_AGE, "3600");
                    httpResponse.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                    httpResponse.addHeader(ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK, "true");
                }
            }
        };
    }

    // Test that all CORS header constants are defined correctly
    @Test
    public void test_corsHeaderConstants() {
        assertEquals("Access-Control-Allow-Origin", CorsHandler.ACCESS_CONTROL_ALLOW_ORIGIN);
        assertEquals("Access-Control-Allow-Headers", CorsHandler.ACCESS_CONTROL_ALLOW_HEADERS);
        assertEquals("Access-Control-Allow-Methods", CorsHandler.ACCESS_CONTROL_ALLOW_METHODS);
        assertEquals("Access-Control-Allow-Private-Network", CorsHandler.ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK);
        assertEquals("Access-Control-Allow-Credentials", CorsHandler.ACCESS_CONTROL_ALLOW_CREDENTIALS);
        assertEquals("Access-Control-Max-Age", CorsHandler.ACCESS_CONTROL_MAX_AGE);
    }

    // Test that constants are protected static final
    @Test
    public void test_constantsModifiers() throws Exception {
        Field[] fields = CorsHandler.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().startsWith("ACCESS_CONTROL_")) {
                int modifiers = field.getModifiers();
                assertTrue("Field " + field.getName() + " should be protected", Modifier.isProtected(modifiers));
                assertTrue("Field " + field.getName() + " should be static", Modifier.isStatic(modifiers));
                assertTrue("Field " + field.getName() + " should be final", Modifier.isFinal(modifiers));
            }
        }
    }

    // Test that CorsHandler is abstract
    @Test
    public void test_isAbstractClass() {
        assertTrue("CorsHandler should be abstract", Modifier.isAbstract(CorsHandler.class.getModifiers()));
    }

    // Test that process method is abstract
    @Test
    public void test_processMethodIsAbstract() throws Exception {
        try {
            java.lang.reflect.Method processMethod =
                    CorsHandler.class.getDeclaredMethod("process", String.class, ServletRequest.class, ServletResponse.class);
            assertTrue("process method should be abstract", Modifier.isAbstract(processMethod.getModifiers()));
            assertTrue("process method should be public", Modifier.isPublic(processMethod.getModifiers()));
        } catch (NoSuchMethodException e) {
            fail("process method should exist");
        }
    }

    // Test constructor
    @Test
    public void test_constructor() {
        CorsHandler handler = new CorsHandler() {
            @Override
            public void process(String origin, ServletRequest request, ServletResponse response) {
                // Test implementation
            }
        };
        assertNotNull(handler);
    }

    // Test process method with null origin
    @Test
    public void test_processWithNullOrigin() {
        HttpServletRequest request = createMockRequest();
        HttpServletResponse response = createMockResponse();

        corsHandler.process(null, request, response);

        assertTrue("process should be called", processCalled);
        assertNull(processOrigin, "origin should be null");
        assertEquals(request, processRequest);
        assertEquals(response, processResponse);
    }

    // Test process method with empty origin
    @Test
    public void test_processWithEmptyOrigin() {
        HttpServletRequest request = createMockRequest();
        HttpServletResponse response = createMockResponse();

        corsHandler.process("", request, response);

        assertTrue("process should be called", processCalled);
        assertEquals("", processOrigin);
        assertEquals(request, processRequest);
        assertEquals(response, processResponse);
    }

    // Test process method with valid origin
    @Test
    public void test_processWithValidOrigin() {
        String testOrigin = "https://example.com";
        HttpServletRequest request = createMockRequest();
        HttpServletResponse response = createMockResponse();

        corsHandler.process(testOrigin, request, response);

        assertTrue("process should be called", processCalled);
        assertEquals(testOrigin, processOrigin);
        assertEquals(request, processRequest);
        assertEquals(response, processResponse);

        // Verify headers were added
        List<String> originHeaders = responseHeaders.get("Access-Control-Allow-Origin");
        assertNotNull(originHeaders);
        assertTrue(originHeaders.contains(testOrigin));
    }

    // Test process method with multiple origins
    @Test
    public void test_processWithMultipleOrigins() {
        String[] testOrigins = { "https://example.com", "https://test.example.com", "http://localhost:8080" };

        for (String origin : testOrigins) {
            processCalled = false;
            responseHeaders.clear();

            HttpServletRequest request = createMockRequest();
            HttpServletResponse response = createMockResponse();

            corsHandler.process(origin, request, response);

            assertTrue("process should be called for origin: " + origin, processCalled);
            assertEquals(origin, processOrigin);

            List<String> originHeaders = responseHeaders.get("Access-Control-Allow-Origin");
            assertNotNull(originHeaders, "Headers should be set for origin: " + origin);
            assertTrue(originHeaders.contains(origin));
        }
    }

    // Test process method with wildcard origin
    @Test
    public void test_processWithWildcardOrigin() {
        String wildcardOrigin = "*";
        HttpServletRequest request = createMockRequest();
        HttpServletResponse response = createMockResponse();

        corsHandler.process(wildcardOrigin, request, response);

        assertTrue("process should be called", processCalled);
        assertEquals(wildcardOrigin, processOrigin);

        List<String> originHeaders = responseHeaders.get("Access-Control-Allow-Origin");
        assertNotNull(originHeaders);
        assertTrue(originHeaders.contains(wildcardOrigin));
    }

    // Test that all expected headers are set
    @Test
    public void test_allCorsHeadersAreSet() {
        String testOrigin = "https://example.com";
        HttpServletRequest request = createMockRequest();
        HttpServletResponse response = createMockResponse();

        corsHandler.process(testOrigin, request, response);

        // Check all CORS headers are present
        assertNotNull(responseHeaders.get("Access-Control-Allow-Origin"), "Allow-Origin header should be set");
        assertNotNull(responseHeaders.get("Access-Control-Allow-Methods"), "Allow-Methods header should be set");
        assertNotNull(responseHeaders.get("Access-Control-Allow-Headers"), "Allow-Headers header should be set");
        assertNotNull(responseHeaders.get("Access-Control-Max-Age"), "Max-Age header should be set");
        assertNotNull(responseHeaders.get("Access-Control-Allow-Credentials"), "Allow-Credentials header should be set");
        assertNotNull(responseHeaders.get("Access-Control-Allow-Private-Network"), "Allow-Private-Network header should be set");
    }

    // Test process with non-HttpServletResponse
    @Test
    public void test_processWithNonHttpServletResponse() {
        String testOrigin = "https://example.com";
        HttpServletRequest request = createMockRequest();
        ServletResponse response = new ServletResponse() {
            @Override
            public String getCharacterEncoding() {
                return null;
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
                return null;
            }
        };

        corsHandler.process(testOrigin, request, response);

        assertTrue("process should be called", processCalled);
        assertEquals(testOrigin, processOrigin);
        assertEquals(response, processResponse);
    }

    // Test multiple process calls
    @Test
    public void test_multipleProcessCalls() {
        HttpServletRequest request = createMockRequest();
        HttpServletResponse response = createMockResponse();

        String[] origins = { "https://site1.com", "https://site2.com", "https://site3.com" };

        for (String origin : origins) {
            processCalled = false;
            corsHandler.process(origin, request, response);
            assertTrue("process should be called for " + origin, processCalled);
            assertEquals(origin, processOrigin);
        }
    }

    // Test constants are not modifiable (ensure they're final)
    @Test
    public void test_constantsAreImmutable() throws Exception {
        Field originField = CorsHandler.class.getDeclaredField("ACCESS_CONTROL_ALLOW_ORIGIN");
        assertTrue("ACCESS_CONTROL_ALLOW_ORIGIN should be final", Modifier.isFinal(originField.getModifiers()));

        Field headersField = CorsHandler.class.getDeclaredField("ACCESS_CONTROL_ALLOW_HEADERS");
        assertTrue("ACCESS_CONTROL_ALLOW_HEADERS should be final", Modifier.isFinal(headersField.getModifiers()));

        Field methodsField = CorsHandler.class.getDeclaredField("ACCESS_CONTROL_ALLOW_METHODS");
        assertTrue("ACCESS_CONTROL_ALLOW_METHODS should be final", Modifier.isFinal(methodsField.getModifiers()));

        Field privateNetworkField = CorsHandler.class.getDeclaredField("ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK");
        assertTrue("ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK should be final", Modifier.isFinal(privateNetworkField.getModifiers()));

        Field credentialsField = CorsHandler.class.getDeclaredField("ACCESS_CONTROL_ALLOW_CREDENTIALS");
        assertTrue("ACCESS_CONTROL_ALLOW_CREDENTIALS should be final", Modifier.isFinal(credentialsField.getModifiers()));

        Field maxAgeField = CorsHandler.class.getDeclaredField("ACCESS_CONTROL_MAX_AGE");
        assertTrue("ACCESS_CONTROL_MAX_AGE should be final", Modifier.isFinal(maxAgeField.getModifiers()));
    }

    // Helper method to create mock HttpServletRequest
    private HttpServletRequest createMockRequest() {
        return new HttpServletRequest() {
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
                return 0;
            }

            @Override
            public String getHeader(String name) {
                return null;
            }

            @Override
            public java.util.Enumeration<String> getHeaders(String name) {
                return null;
            }

            @Override
            public java.util.Enumeration<String> getHeaderNames() {
                return null;
            }

            @Override
            public int getIntHeader(String name) {
                return 0;
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
                return "/test";
            }

            @Override
            public StringBuffer getRequestURL() {
                return new StringBuffer("http://test");
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
                return null;
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
                return null;
            }

            @Override
            public String[] getParameterValues(String name) {
                return null;
            }

            @Override
            public java.util.Map<String, String[]> getParameterMap() {
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
            public java.io.BufferedReader getReader() {
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
            public java.util.Locale getLocale() {
                return null;
            }

            @Override
            public java.util.Enumeration<java.util.Locale> getLocales() {
                return null;
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
            public jakarta.servlet.ServletContext getServletContext() {
                return null;
            }

            @Override
            public jakarta.servlet.AsyncContext startAsync() {
                return null;
            }

            @Override
            public jakarta.servlet.AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) {
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
            public jakarta.servlet.ServletConnection getServletConnection() {
                return null;
            }
        };
    }

    // Helper method to create mock HttpServletResponse
    private HttpServletResponse createMockResponse() {
        return new HttpServletResponse() {
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
                List<String> values = new ArrayList<>();
                values.add(value);
                responseHeaders.put(name, values);
            }

            @Override
            public void addHeader(String name, String value) {
                responseHeaders.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
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
                return 200;
            }

            @Override
            public String getHeader(String name) {
                List<String> values = responseHeaders.get(name);
                return values != null && !values.isEmpty() ? values.get(0) : null;
            }

            @Override
            public java.util.Collection<String> getHeaders(String name) {
                return responseHeaders.get(name);
            }

            @Override
            public java.util.Collection<String> getHeaderNames() {
                return responseHeaders.keySet();
            }

            @Override
            public String getCharacterEncoding() {
                return null;
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
                return null;
            }
        };
    }
}