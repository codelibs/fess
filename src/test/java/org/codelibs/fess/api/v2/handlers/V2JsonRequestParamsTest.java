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
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    public void test_getPageSize_zeroThrowsInvalidPageSizeException() {
        // num=0 is invalid — the handler should reject it with INVALID_REQUEST.
        final V2JsonRequestParams params = newParams(Map.of("num", new String[] { "0" }));
        assertThrows(V2JsonRequestParams.InvalidPageSizeException.class, params::getPageSize, "num=0 must throw InvalidPageSizeException");
    }

    @Test
    public void test_getPageSize_negativeThrowsInvalidPageSizeException() {
        // num=-1 is equally invalid.
        final V2JsonRequestParams params = newParams(Map.of("num", new String[] { "-1" }));
        assertThrows(V2JsonRequestParams.InvalidPageSizeException.class, params::getPageSize, "num=-1 must throw InvalidPageSizeException");
    }

    @Test
    public void test_getPageSize_aboveMaxClamps() {
        // num=99999 exceeds the configured max (100); should clamp silently to the max.
        // Clients detect clamping by comparing the request num with the response page_size.
        final V2JsonRequestParams params = newParams(Map.of("num", new String[] { "99999" }));
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

    @Test
    public void test_getOffset_negative_throwsInvalidOffsetException() {
        // M-6: offset=-1 previously collided with the "uninitialised" sentinel and was
        // silently re-parsed on every call; clients passing -1 also bypassed validation
        // and forwarded -1 to the search backend. Now the explicit init-flag means -1 is
        // a real value, and it must be rejected with InvalidOffsetException.
        final V2JsonRequestParams params = newParams(Map.of("offset", new String[] { "-1" }));
        assertThrows(V2JsonRequestParams.InvalidOffsetException.class, params::getOffset, "offset=-1 must throw InvalidOffsetException");
    }

    @Test
    public void test_getOffset_negativeOther_throwsInvalidOffsetException() {
        // Any negative value must throw, not just -1.
        final V2JsonRequestParams params = newParams(Map.of("offset", new String[] { "-42" }));
        assertThrows(V2JsonRequestParams.InvalidOffsetException.class, params::getOffset, "offset=-42 must throw InvalidOffsetException");
    }

    @Test
    public void test_getOffset_cachedAcrossCalls() {
        // M-6: the explicit init-flag must drive caching so repeated calls return the
        // same parsed value without re-reading the request.
        final V2JsonRequestParams params = newParams(Map.of("offset", new String[] { "7" }));
        assertEquals(7, params.getOffset());
        assertEquals(7, params.getOffset());
        assertEquals(7, params.getOffset());
    }

    @Test
    public void test_getOffset_zeroIsCachedNotReparsed() {
        // Boundary case: offset=0 must NOT trigger a re-parse on subsequent calls.
        // Under the old "-1 == uninitialised" sentinel scheme this happened to work
        // because 0 != -1, but the explicit-flag scheme makes the contract intentional.
        final V2JsonRequestParams params = newParams(Map.of("offset", new String[] { "0" }));
        assertEquals(0, params.getOffset());
        assertEquals(0, params.getOffset());
    }

    @Test
    public void test_getStartPosition_negative_throwsInvalidOffsetException() {
        // M-6 (start variant): negative start is equally invalid — reject so the
        // search backend never sees -1.
        final V2JsonRequestParams params = newParams(Map.of("start", new String[] { "-1" }));
        assertThrows(V2JsonRequestParams.InvalidOffsetException.class, params::getStartPosition,
                "start=-1 must throw InvalidOffsetException");
    }

    @Test
    public void test_getStartPosition_cachedAcrossCalls() {
        // Caching invariant for start, same as offset.
        final V2JsonRequestParams params = newParams(Map.of("start", new String[] { "3" }));
        assertEquals(3, params.getStartPosition());
        assertEquals(3, params.getStartPosition());
    }

    @Test
    public void test_getPageSize_cachedAcrossCalls() {
        // The same explicit-flag caching contract applies to pageSize. Ensure repeated
        // calls don't re-clamp / re-validate.
        final V2JsonRequestParams params = newParams(Map.of("num", new String[] { "5" }));
        assertEquals(5, params.getPageSize());
        assertEquals(5, params.getPageSize());
    }

    // -----------------------------------------------------------------------
    // Task 4: String parameter length bounds
    // -----------------------------------------------------------------------

    @Test
    public void test_getQuery_tooLong_throws() {
        final String longValue = "a".repeat(1001);
        final V2JsonRequestParams params = newParams(Map.of("q", new String[] { longValue }));
        assertThrows(InvalidRequestParameterException.class, params::getQuery, "q with 1001 chars must throw");
    }

    @Test
    public void test_getQuery_atLimit_ok() {
        final String atLimit = "a".repeat(1000);
        final V2JsonRequestParams params = newParams(Map.of("q", new String[] { atLimit }));
        assertEquals(atLimit, params.getQuery());
    }

    @Test
    public void test_getTrackTotalHits_tooLong_throws() {
        final String longValue = "t".repeat(101);
        final V2JsonRequestParams params = newParams(Map.of("track_total_hits", new String[] { longValue }));
        assertThrows(InvalidRequestParameterException.class, params::getTrackTotalHits, "track_total_hits with 101 chars must throw");
    }

    @Test
    public void test_getSort_tooLong_throws() {
        final String longValue = "s".repeat(1001);
        final V2JsonRequestParams params = newParams(Map.of("sort", new String[] { longValue }));
        assertThrows(InvalidRequestParameterException.class, params::getSort, "sort with 1001 chars must throw");
    }

    @Test
    public void test_getSimilarDocHash_tooLong_throws() {
        final String longValue = "d".repeat(1001);
        final V2JsonRequestParams params = newParams(Map.of("sdh", new String[] { longValue }));
        assertThrows(InvalidRequestParameterException.class, params::getSimilarDocHash, "sdh with 1001 chars must throw");
    }

    // -----------------------------------------------------------------------
    // Task 5: Array parameter bounds
    // -----------------------------------------------------------------------

    @Test
    public void test_getExtraQueries_tooManyItems_throws() {
        final String[] many = new String[101];
        java.util.Arrays.fill(many, "x");
        final V2JsonRequestParams params = newParams(Map.of("ex_q", many));
        assertThrows(InvalidRequestParameterException.class, params::getExtraQueries, "ex_q with 101 items must throw");
    }

    @Test
    public void test_getExtraQueries_elementTooLong_throws() {
        final String longValue = "x".repeat(1001);
        final V2JsonRequestParams params = newParams(Map.of("ex_q", new String[] { longValue }));
        assertThrows(InvalidRequestParameterException.class, params::getExtraQueries, "ex_q element with 1001 chars must throw");
    }

    @Test
    public void test_getExtraQueries_atLimit_ok() {
        // 100 unique values each at the length limit (1000 chars) — must not throw.
        // Values are unique so simplifyArray() does not deduplicate them.
        final String[] many = new String[100];
        for (int i = 0; i < many.length; i++) {
            many[i] = String.format("%04d", i) + "x".repeat(996);
        }
        final V2JsonRequestParams params = newParams(Map.of("ex_q", many));
        assertEquals(100, params.getExtraQueries().length);
    }

    @Test
    public void test_getFields_tooManyDistinctNames_throws() {
        final Map<String, String[]> paramMap = new HashMap<>();
        for (int i = 0; i < 101; i++) {
            paramMap.put("fields.f" + i, new String[] { "v" });
        }
        final V2JsonRequestParams params = newParams(paramMap);
        assertThrows(InvalidRequestParameterException.class, params::getFields, "fields with 101 distinct names must throw");
    }

    @Test
    public void test_getLanguages_tooManyItems_throws() {
        final String[] many = new String[101];
        java.util.Arrays.fill(many, "en");
        final V2JsonRequestParams params = newParams(Map.of("lang", many));
        assertThrows(InvalidRequestParameterException.class, params::getLanguages, "lang with 101 items must throw");
    }

    // -----------------------------------------------------------------------
    // Task 6: createFacetInfo / getFacetInfo bounds
    // -----------------------------------------------------------------------

    @Test
    public void test_getFacetInfo_fieldTooManyItems_throws() {
        final String[] many = new String[101];
        java.util.Arrays.fill(many, "title");
        final V2JsonRequestParams params = newParams(Map.of("facet.field", many));
        assertThrows(InvalidRequestParameterException.class, params::getFacetInfo, "facet.field with 101 items must throw");
    }

    @Test
    public void test_getFacetInfo_fieldElementTooLong_throws() {
        final String longValue = "f".repeat(1001);
        final V2JsonRequestParams params =
                newParams(Map.of("facet.field", new String[] { longValue }, "facet.query", new String[] { "q:v" }));
        assertThrows(InvalidRequestParameterException.class, params::getFacetInfo, "facet.field element with 1001 chars must throw");
    }

    // -----------------------------------------------------------------------
    // Fix 1: per-name value count and element length for fields.* and as.*
    // -----------------------------------------------------------------------

    @Test
    public void test_getFields_singleName_tooManyValues_throws() {
        // 101 distinct values for a single fields.label key must throw after simplifyArray dedup.
        // Values must be distinct so simplifyArray does not collapse them below the limit.
        final String[] many = new String[101];
        for (int i = 0; i < many.length; i++) {
            many[i] = "val" + i;
        }
        final V2JsonRequestParams params = newParams(Map.of("fields.label", many));
        assertThrows(InvalidRequestParameterException.class, params::getFields, "fields.label with 101 distinct values must throw");
    }

    @Test
    public void test_getFields_singleName_elementTooLong_throws() {
        // An element of 1001 characters in fields.label must throw.
        final String longValue = "x".repeat(1001);
        final V2JsonRequestParams params = newParams(Map.of("fields.label", new String[] { longValue }));
        assertThrows(InvalidRequestParameterException.class, params::getFields, "fields.label element with 1001 chars must throw");
    }

    @Test
    public void test_getFields_singleName_atLimit_ok() {
        // 100 distinct values each at 1000 characters — must not throw.
        final String[] many = new String[100];
        for (int i = 0; i < many.length; i++) {
            many[i] = String.format("%04d", i) + "x".repeat(996);
        }
        final V2JsonRequestParams params = newParams(Map.of("fields.label", many));
        final Map<String, String[]> fields = params.getFields();
        assertEquals(100, fields.get("label").length);
    }

    @Test
    public void test_getConditions_singleName_tooManyValues_throws() {
        // 101 distinct values for a single as.q key must throw after simplifyArray dedup.
        final String[] many = new String[101];
        for (int i = 0; i < many.length; i++) {
            many[i] = "val" + i;
        }
        final V2JsonRequestParams params = newParams(Map.of("as.q", many));
        assertThrows(InvalidRequestParameterException.class, params::getConditions, "as.q with 101 distinct values must throw");
    }

    @Test
    public void test_getConditions_singleName_elementTooLong_throws() {
        // An element of 1001 characters in as.q must throw.
        final String longValue = "x".repeat(1001);
        final V2JsonRequestParams params = newParams(Map.of("as.q", new String[] { longValue }));
        assertThrows(InvalidRequestParameterException.class, params::getConditions, "as.q element with 1001 chars must throw");
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
