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

import org.codelibs.fess.unit.UnitFessTestCase;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test class for WebApiManagerFactory.
 * Tests API manager registration and request matching without using Mockito.
 */
public class WebApiManagerFactoryTest extends UnitFessTestCase {

    private WebApiManagerFactory factory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        factory = new WebApiManagerFactory();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        factory = null;
    }

    public void test_add() {
        TestWebApiManager manager = new TestWebApiManager(true);

        assertEquals(0, factory.size());

        factory.add(manager);

        assertEquals(1, factory.size());
    }

    public void test_add_multiple() {
        TestWebApiManager manager1 = new TestWebApiManager(true);
        TestWebApiManager manager2 = new TestWebApiManager(true);
        TestWebApiManager manager3 = new TestWebApiManager(true);

        factory.add(manager1);
        factory.add(manager2);
        factory.add(manager3);

        assertEquals(3, factory.size());
    }

    public void test_add_nullThrowsException() {
        try {
            factory.add(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("webApiManager must not be null", e.getMessage());
        }
    }

    public void test_get_matchingManager() {
        HttpServletRequest request = createMockRequest();
        TestWebApiManager manager1 = new TestWebApiManager(false);
        TestWebApiManager manager2 = new TestWebApiManager(true);

        factory.add(manager1);
        factory.add(manager2);

        WebApiManager result = factory.get(request);

        assertSame(manager2, result);
        assertEquals(1, manager1.getMatchesCallCount());
        assertEquals(1, manager2.getMatchesCallCount());
    }

    public void test_get_firstMatchingManager() {
        HttpServletRequest request = createMockRequest();
        TestWebApiManager manager1 = new TestWebApiManager(false);
        TestWebApiManager manager2 = new TestWebApiManager(true);
        TestWebApiManager manager3 = new TestWebApiManager(true);

        factory.add(manager1);
        factory.add(manager2);
        factory.add(manager3);

        WebApiManager result = factory.get(request);

        // Should return the first matching manager (manager2)
        assertSame(manager2, result);
        assertEquals(1, manager1.getMatchesCallCount());
        assertEquals(1, manager2.getMatchesCallCount());
        assertEquals(0, manager3.getMatchesCallCount()); // Should not check manager3
    }

    public void test_get_noMatchingManager() {
        HttpServletRequest request = createMockRequest();
        TestWebApiManager manager1 = new TestWebApiManager(false);
        TestWebApiManager manager2 = new TestWebApiManager(false);

        factory.add(manager1);
        factory.add(manager2);

        WebApiManager result = factory.get(request);

        assertNull(result);
        assertEquals(1, manager1.getMatchesCallCount());
        assertEquals(1, manager2.getMatchesCallCount());
    }

    public void test_get_emptyFactory() {
        HttpServletRequest request = createMockRequest();

        WebApiManager result = factory.get(request);

        assertNull(result);
    }

    public void test_threadSafety() throws Exception {
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final TestWebApiManager[] managers = new TestWebApiManager[threadCount];

        // Create managers
        for (int i = 0; i < threadCount; i++) {
            managers[i] = new TestWebApiManager(true);
        }

        // Add managers concurrently
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> factory.add(managers[index]));
            threads[i].start();
        }

        // Wait for all threads
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify all managers were added
        assertEquals(threadCount, factory.size());
    }

    public void test_threadSafety_addAndGet() throws Exception {
        final int threadCount = 5;
        final Thread[] addThreads = new Thread[threadCount];
        final Thread[] getThreads = new Thread[threadCount];

        HttpServletRequest request = createMockRequest();

        // Add managers in separate threads
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            addThreads[i] = new Thread(() -> {
                TestWebApiManager manager = new TestWebApiManager(index == 0);
                factory.add(manager);
            });
            addThreads[i].start();
        }

        // Wait for add operations
        for (Thread thread : addThreads) {
            thread.join();
        }

        // Get managers in separate threads
        final WebApiManager[] results = new WebApiManager[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            getThreads[i] = new Thread(() -> {
                results[index] = factory.get(request);
            });
            getThreads[i].start();
        }

        // Wait for get operations
        for (Thread thread : getThreads) {
            thread.join();
        }

        // Verify get operations completed without errors
        // All should return the same manager (first matching one)
        WebApiManager firstResult = results[0];
        for (WebApiManager result : results) {
            assertSame(firstResult, result);
        }
    }

    public void test_size_initiallyZero() {
        assertEquals(0, factory.size());
    }

    public void test_size_afterAdditions() {
        for (int i = 1; i <= 5; i++) {
            factory.add(new TestWebApiManager(true));
            assertEquals(i, factory.size());
        }
    }

    /**
     * Creates a minimal mock HttpServletRequest for testing.
     * Only implements the methods actually needed by the tests.
     */
    private HttpServletRequest createMockRequest() {
        return new HttpServletRequest() {
            @Override
            public String getAuthType() {
                return null;
            }

            @Override
            public jakarta.servlet.http.Cookie[] getCookies() {
                return new jakarta.servlet.http.Cookie[0];
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
                return java.util.Collections.emptyEnumeration();
            }

            @Override
            public java.util.Enumeration<String> getHeaderNames() {
                return java.util.Collections.emptyEnumeration();
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
                return "/";
            }

            @Override
            public StringBuffer getRequestURL() {
                return new StringBuffer("http://localhost/");
            }

            @Override
            public String getServletPath() {
                return "/";
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
            public boolean authenticate(HttpServletResponse response) {
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
                return java.util.Collections.emptyList();
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
                return "UTF-8";
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
                return new String[0];
            }

            @Override
            public java.util.Map<String, String[]> getParameterMap() {
                return java.util.Collections.emptyMap();
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
                return 80;
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
                return java.util.Collections.enumeration(java.util.Collections.singletonList(java.util.Locale.getDefault()));
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
                return 80;
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
            public jakarta.servlet.AsyncContext startAsync(jakarta.servlet.ServletRequest servletRequest,
                    jakarta.servlet.ServletResponse servletResponse) {
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
        };
    }

    /**
     * Test implementation of WebApiManager for testing purposes.
     */
    private static class TestWebApiManager implements WebApiManager {
        private final boolean shouldMatch;
        private final AtomicInteger matchesCallCount = new AtomicInteger(0);

        public TestWebApiManager(boolean shouldMatch) {
            this.shouldMatch = shouldMatch;
        }

        @Override
        public boolean matches(HttpServletRequest request) {
            matchesCallCount.incrementAndGet();
            return shouldMatch;
        }

        @Override
        public void process(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            // Test implementation - no-op
        }

        public int getMatchesCallCount() {
            return matchesCallCount.get();
        }
    }
}
