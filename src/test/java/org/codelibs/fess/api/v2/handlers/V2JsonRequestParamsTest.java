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
package org.codelibs.fess.api.v2.handlers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;

/**
 * Unit tests for {@link V2JsonRequestParams}.
 *
 * <p>Covers the v2 search request adapter's parameter-extraction logic: query,
 * paging (defaults plus clamping at the configured max), language array,
 * {@code fields.*} prefix stripping, and offset defaulting on missing or
 * malformed input. Each test builds a small {@code HttpServletRequest} stub
 * with just the parameter map the production code reads. {@link FessConfig}
 * resolves through the unit DI container, so paging defaults pick up the
 * standard {@code paging.search.page.size=10} / {@code page.max.size=100} from
 * {@code fess_config.properties}.</p>
 */
public class V2JsonRequestParamsTest extends UnitFessTestCase {

    @Test
    public void test_getQuery_returnsRawQueryParam() {
        final V2JsonRequestParams params = newParams(Map.of("q", new String[] { "hello" }));
        assertEquals("hello", params.getQuery());
    }

    @Test
    public void test_getQuery_returnsNullWhenMissing() {
        final V2JsonRequestParams params = newParams(Map.of());
        assertNull(params.getQuery());
    }

    @Test
    public void test_getPageSize_defaultsToFessConfigSizeWhenMissing() {
        // No "num" param ⇒ fall back to fessConfig.paging.search.page.size (default 10).
        final V2JsonRequestParams params = newParams(Map.of());
        assertEquals(10, params.getPageSize());
    }

    @Test
    public void test_getPageSize_clampedByConfiguredMax() {
        // Requesting a page size well above the configured max snaps back to the max
        // (paging.search.page.max.size=100) — protects the engine from oversized scans.
        final V2JsonRequestParams params = newParams(Map.of("num", new String[] { "99999" }));
        assertEquals(100, params.getPageSize());
    }

    @Test
    public void test_getPageSize_nonPositiveValuesClampToMax() {
        // Per the impl, num<=0 also routes to the max (NumberFormatException branch sets
        // pageSize back to the default — but the explicit "0" path goes through the > 0
        // check and lands at max). This locks down that intentional behavior.
        final V2JsonRequestParams params = newParams(Map.of("num", new String[] { "0" }));
        assertEquals(100, params.getPageSize());
    }

    @Test
    public void test_getLanguages_returnsArrayParam() {
        // "lang" is multi-valued (HttpServletRequest.getParameterValues) — comma-splitting
        // is NOT done here; the request layer supplies one entry per value.
        final V2JsonRequestParams params = newParams(Map.of("lang", new String[] { "en", "ja" }));
        assertArrayEquals(new String[] { "en", "ja" }, params.getLanguages());
    }

    @Test
    public void test_getFields_stripsFieldsPrefix() {
        // Parameters starting with "fields." surface in the returned map without the prefix —
        // matches the v1 SearchApiManager wire shape so existing clients keep working.
        final V2JsonRequestParams params = newParams(Map.of( //
                "fields.label", new String[] { "news" }, //
                "fields.tag", new String[] { "tech", "java" }, //
                "q", new String[] { "ignored" })); // q must not leak into fields
        final Map<String, String[]> fields = params.getFields();
        assertTrue(fields.containsKey("label"), fields.toString());
        assertArrayEquals(new String[] { "news" }, fields.get("label"));
        assertArrayEquals(new String[] { "tech", "java" }, fields.get("tag"));
        // The raw "q" param must not be carried over.
        assertTrue(!fields.containsKey("q"), fields.toString());
        // And the "fields." prefix is fully stripped, not preserved.
        assertTrue(!fields.containsKey("fields.label"), fields.toString());
    }

    @Test
    public void test_getOffset_defaultsToZeroForBlankOrInvalid() {
        // Blank "offset" param ⇒ 0.
        assertEquals(0, newParams(Map.of()).getOffset());
        // Non-numeric "offset" ⇒ 0 (NumberFormatException branch in the impl).
        assertEquals(0, newParams(Map.of("offset", new String[] { "abc" })).getOffset());
    }

    private static V2JsonRequestParams newParams(final Map<String, String[]> params) {
        return new V2JsonRequestParams(new StubRequest(params), ComponentUtil.getFessConfig());
    }

    /**
     * Minimal HttpServletRequest stub backed by a {@code Map<String,String[]>}. Only the
     * parameter-map / parameter-getter surface is meaningful — everything else throws or
     * returns a benign default.
     */
    private static class StubRequest implements HttpServletRequest {
        private final Map<String, String[]> params;
        private final Map<String, Object> attrs = new HashMap<>();

        StubRequest(final Map<String, String[]> params) {
            this.params = params == null ? Collections.emptyMap() : params;
        }

        @Override
        public String getParameter(final String name) {
            final String[] vals = params.get(name);
            return vals == null || vals.length == 0 ? null : vals[0];
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(params.keySet());
        }

        @Override
        public String[] getParameterValues(final String name) {
            return params.get(name);
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.unmodifiableMap(params);
        }

        @Override
        public Object getAttribute(final String name) {
            return attrs.get(name);
        }

        @Override
        public void setAttribute(final String name, final Object value) {
            if (value == null) {
                attrs.remove(name);
            } else {
                attrs.put(name, value);
            }
        }

        @Override
        public void removeAttribute(final String name) {
            attrs.remove(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(attrs.keySet());
        }

        @Override
        public String getMethod() {
            return "GET";
        }

        @Override
        public String getServletPath() {
            return "/api/v2/search";
        }

        @Override
        public String getRequestURI() {
            return "/api/v2/search";
        }

        @Override
        public String getContextPath() {
            return "";
        }

        @Override
        public RequestDispatcher getRequestDispatcher(final String path) {
            return null;
        }

        @Override
        public String getAuthType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.Cookie[] getCookies() {
            return null;
        }

        @Override
        public long getDateHeader(final String name) {
            return -1;
        }

        @Override
        public String getHeader(final String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(final String name) {
            return Collections.emptyEnumeration();
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.emptyEnumeration();
        }

        @Override
        public int getIntHeader(final String name) {
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
        public boolean isUserInRole(final String role) {
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
            return new StringBuffer(getRequestURI());
        }

        @Override
        public HttpSession getSession(final boolean create) {
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
        public boolean authenticate(final HttpServletResponse response) {
            return false;
        }

        @Override
        public void login(final String username, final String password) {
        }

        @Override
        public void logout() {
        }

        @Override
        public java.util.Collection<Part> getParts() {
            return Collections.emptyList();
        }

        @Override
        public Part getPart(final String name) {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(final Class<T> handlerClass) {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(final String env) {
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
            throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
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
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public Enumeration<java.util.Locale> getLocales() {
            return Collections.enumeration(Collections.singleton(java.util.Locale.ROOT));
        }

        @Override
        public boolean isSecure() {
            return false;
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
        public ServletContext getServletContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AsyncContext startAsync() {
            throw new UnsupportedOperationException();
        }

        @Override
        public AsyncContext startAsync(final ServletRequest req, final ServletResponse resp) {
            throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
        }

        @Override
        public DispatcherType getDispatcherType() {
            return DispatcherType.REQUEST;
        }

        @Override
        public String getRequestId() {
            return "";
        }

        @Override
        public String getProtocolRequestId() {
            return "";
        }

        @Override
        public jakarta.servlet.ServletConnection getServletConnection() {
            return null;
        }
    }
}
