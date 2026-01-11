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
import java.util.concurrent.atomic.AtomicBoolean;

import org.codelibs.fess.api.WebApiManager;
import org.codelibs.fess.api.WebApiManagerFactory;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
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

public class WebApiFilterTest extends UnitFessTestCase {

    private WebApiFilter webApiFilter;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        webApiFilter = new WebApiFilter();
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        // Clear WebApiManagerFactory
        super.tearDown();
    }

    // Test constructor
    @Test
    public void test_constructor() {
        assertNotNull(new WebApiFilter());
    }

    // Test init method
    @Test
    public void test_init() throws ServletException {
        FilterConfig filterConfig = new FilterConfig() {
            @Override
            public String getFilterName() {
                return "testFilter";
            }

            @Override
            public jakarta.servlet.ServletContext getServletContext() {
                return null;
            }

            @Override
            public String getInitParameter(String name) {
                return null;
            }

            @Override
            public java.util.Enumeration<String> getInitParameterNames() {
                return null;
            }
        };

        // Should not throw any exception
        webApiFilter.init(filterConfig);
        assertTrue(true);
    }

    // Test destroy method
    @Test
    public void test_destroy() {
        // Should not throw any exception
        webApiFilter.destroy();
        assertTrue(true);
    }

    // Test doFilter when WebApiManagerFactory returns null WebApiManager
    @Test
    public void test_doFilter_withNullWebApiManager() throws IOException, ServletException {
        // Setup mock objects
        HttpServletRequest request = createMockHttpServletRequest();
        HttpServletResponse response = createMockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                chainCalled.set(true);
                assertSame(request, req);
                assertSame(response, res);
            }
        };

        // Setup WebApiManagerFactory that returns null
        WebApiManagerFactory factory = new WebApiManagerFactory() {
            @Override
            public WebApiManager get(HttpServletRequest req) {
                assertSame(request, req);
                return null;
            }
        };
        ComponentUtil.register(factory, "webApiManagerFactory");

        // Execute
        webApiFilter.doFilter(request, response, chain);

        // Verify chain was called
        assertTrue(chainCalled.get());
    }

    // Test doFilter when WebApiManagerFactory returns a WebApiManager
    @Test
    public void test_doFilter_withWebApiManager() throws IOException, ServletException {
        // Setup mock objects
        HttpServletRequest request = createMockHttpServletRequest();
        HttpServletResponse response = createMockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        AtomicBoolean managerCalled = new AtomicBoolean(false);

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                chainCalled.set(true);
            }
        };

        // Create mock WebApiManager
        WebApiManager webApiManager = new WebApiManager() {
            @Override
            public boolean matches(HttpServletRequest req) {
                return true;
            }

            @Override
            public void process(HttpServletRequest req, HttpServletResponse res, FilterChain ch) throws IOException, ServletException {
                managerCalled.set(true);
                assertSame(request, req);
                assertSame(response, res);
                assertSame(chain, ch);
            }
        };

        // Setup WebApiManagerFactory that returns the mock WebApiManager
        WebApiManagerFactory factory = new WebApiManagerFactory() {
            @Override
            public WebApiManager get(HttpServletRequest req) {
                assertSame(request, req);
                return webApiManager;
            }
        };
        ComponentUtil.register(factory, "webApiManagerFactory");

        // Execute
        webApiFilter.doFilter(request, response, chain);

        // Verify manager was called and chain was not called directly
        assertTrue(managerCalled.get());
        assertFalse(chainCalled.get());
    }

    // Test doFilter with ServletRequest and ServletResponse (not HTTP variants)
    @Test
    public void test_doFilter_withServletRequestResponse() throws IOException, ServletException {
        // Setup mock objects
        ServletRequest request = createMockHttpServletRequest();
        ServletResponse response = createMockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                chainCalled.set(true);
                assertSame(request, req);
                assertSame(response, res);
            }
        };

        // Setup WebApiManagerFactory
        WebApiManagerFactory factory = new WebApiManagerFactory() {
            @Override
            public WebApiManager get(HttpServletRequest req) {
                assertTrue(req instanceof HttpServletRequest);
                return null;
            }
        };
        ComponentUtil.register(factory, "webApiManagerFactory");

        // Execute
        webApiFilter.doFilter(request, response, chain);

        // Verify chain was called
        assertTrue(chainCalled.get());
    }

    // Test doFilter when WebApiManager.process throws IOException
    @Test
    public void test_doFilter_withIOException() {
        // Setup mock objects
        HttpServletRequest request = createMockHttpServletRequest();
        HttpServletResponse response = createMockHttpServletResponse();

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                // Should not be called
            }
        };

        // Create mock WebApiManager that throws IOException
        WebApiManager webApiManager = new WebApiManager() {
            @Override
            public boolean matches(HttpServletRequest req) {
                return true;
            }

            @Override
            public void process(HttpServletRequest req, HttpServletResponse res, FilterChain ch) throws IOException, ServletException {
                throw new IOException("Test IOException");
            }
        };

        // Setup WebApiManagerFactory
        WebApiManagerFactory factory = new WebApiManagerFactory() {
            @Override
            public WebApiManager get(HttpServletRequest req) {
                return webApiManager;
            }
        };
        ComponentUtil.register(factory, "webApiManagerFactory");

        // Execute and expect IOException
        try {
            webApiFilter.doFilter(request, response, chain);
            fail("Expected IOException");
        } catch (IOException e) {
            assertEquals("Test IOException", e.getMessage());
        } catch (ServletException e) {
            fail("Unexpected ServletException: " + e.getMessage());
        }
    }

    // Test doFilter when WebApiManager.process throws ServletException
    @Test
    public void test_doFilter_withServletException() {
        // Setup mock objects
        HttpServletRequest request = createMockHttpServletRequest();
        HttpServletResponse response = createMockHttpServletResponse();

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                // Should not be called
            }
        };

        // Create mock WebApiManager that throws ServletException
        WebApiManager webApiManager = new WebApiManager() {
            @Override
            public boolean matches(HttpServletRequest req) {
                return true;
            }

            @Override
            public void process(HttpServletRequest req, HttpServletResponse res, FilterChain ch) throws IOException, ServletException {
                throw new ServletException("Test ServletException");
            }
        };

        // Setup WebApiManagerFactory
        WebApiManagerFactory factory = new WebApiManagerFactory() {
            @Override
            public WebApiManager get(HttpServletRequest req) {
                return webApiManager;
            }
        };
        ComponentUtil.register(factory, "webApiManagerFactory");

        // Execute and expect ServletException
        try {
            webApiFilter.doFilter(request, response, chain);
            fail("Expected ServletException");
        } catch (IOException e) {
            fail("Unexpected IOException: " + e.getMessage());
        } catch (ServletException e) {
            assertEquals("Test ServletException", e.getMessage());
        }
    }

    // Test doFilter when chain.doFilter throws IOException
    @Test
    public void test_doFilter_chainThrowsIOException() {
        // Setup mock objects
        HttpServletRequest request = createMockHttpServletRequest();
        HttpServletResponse response = createMockHttpServletResponse();

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                throw new IOException("Chain IOException");
            }
        };

        // Setup WebApiManagerFactory that returns null
        WebApiManagerFactory factory = new WebApiManagerFactory() {
            @Override
            public WebApiManager get(HttpServletRequest req) {
                return null;
            }
        };
        ComponentUtil.register(factory, "webApiManagerFactory");

        // Execute and expect IOException
        try {
            webApiFilter.doFilter(request, response, chain);
            fail("Expected IOException");
        } catch (IOException e) {
            assertEquals("Chain IOException", e.getMessage());
        } catch (ServletException e) {
            fail("Unexpected ServletException: " + e.getMessage());
        }
    }

    // Test doFilter when chain.doFilter throws ServletException
    @Test
    public void test_doFilter_chainThrowsServletException() {
        // Setup mock objects
        HttpServletRequest request = createMockHttpServletRequest();
        HttpServletResponse response = createMockHttpServletResponse();

        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse res) throws IOException, ServletException {
                throw new ServletException("Chain ServletException");
            }
        };

        // Setup WebApiManagerFactory that returns null
        WebApiManagerFactory factory = new WebApiManagerFactory() {
            @Override
            public WebApiManager get(HttpServletRequest req) {
                return null;
            }
        };
        ComponentUtil.register(factory, "webApiManagerFactory");

        // Execute and expect ServletException
        try {
            webApiFilter.doFilter(request, response, chain);
            fail("Expected ServletException");
        } catch (IOException e) {
            fail("Unexpected IOException: " + e.getMessage());
        } catch (ServletException e) {
            assertEquals("Chain ServletException", e.getMessage());
        }
    }

    // Helper method to create mock HttpServletRequest
    private HttpServletRequest createMockHttpServletRequest() {
        return new HttpServletRequest() {
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
            public void setCharacterEncoding(String env) throws java.io.UnsupportedEncodingException {
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
            public jakarta.servlet.ServletInputStream getInputStream() throws IOException {
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
            public java.io.BufferedReader getReader() throws IOException {
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
            public jakarta.servlet.AsyncContext startAsync() throws IllegalStateException {
                return null;
            }

            @Override
            public jakarta.servlet.AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
                    throws IllegalStateException {
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
                return null;
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
                return null;
            }

            @Override
            public StringBuffer getRequestURL() {
                return null;
            }

            @Override
            public String getServletPath() {
                return null;
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
            public java.util.Collection<jakarta.servlet.http.Part> getParts() throws IOException, ServletException {
                return null;
            }

            @Override
            public jakarta.servlet.http.Part getPart(String name) throws IOException, ServletException {
                return null;
            }

            @Override
            public <T extends jakarta.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass)
                    throws IOException, ServletException {
                return null;
            }

            @Override
            public jakarta.servlet.http.HttpServletMapping getHttpServletMapping() {
                return null;
            }

            @Override
            public jakarta.servlet.http.PushBuilder newPushBuilder() {
                return null;
            }

            @Override
            public java.util.Map<String, String> getTrailerFields() {
                return null;
            }

            @Override
            public boolean isTrailerFieldsReady() {
                return false;
            }
        };
    }

    // Helper method to create mock HttpServletResponse
    private HttpServletResponse createMockHttpServletResponse() {
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
                return null;
            }

            @Override
            public String encodeRedirectURL(String url) {
                return null;
            }

            @Override
            public void sendError(int sc, String msg) throws IOException {
            }

            @Override
            public void sendError(int sc) throws IOException {
            }

            @Override
            public void sendRedirect(String location) throws IOException {
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
            public void setStatus(int sc) {
            }

            @Override
            public int getStatus() {
                return 0;
            }

            @Override
            public String getHeader(String name) {
                return null;
            }

            @Override
            public java.util.Collection<String> getHeaders(String name) {
                return null;
            }

            @Override
            public java.util.Collection<String> getHeaderNames() {
                return null;
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
            public jakarta.servlet.ServletOutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            public java.io.PrintWriter getWriter() throws IOException {
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
            public void flushBuffer() throws IOException {
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

            @Override
            public void setTrailerFields(java.util.function.Supplier<java.util.Map<String, String>> supplier) {
            }

            @Override
            public java.util.function.Supplier<java.util.Map<String, String>> getTrailerFields() {
                return null;
            }
        };
    }
}