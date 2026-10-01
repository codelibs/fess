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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fesen.opensearch.action.search.SearchAction;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.log.cbean.SearchLogCB;
import org.codelibs.fess.opensearch.log.exentity.SearchLog;
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
 * Unit tests for {@link SearchHistoryHandler}.
 *
 * <p>The search log query is stubbed by overriding {@link SearchHistoryHandler#selectSearchLogs},
 * so the tests run without a search engine. The condition bean the real query builds is pinned
 * separately through {@link SearchHistoryHandler#setUpConditionBean}.</p>
 */
public class SearchHistoryHandlerTest extends UnitFessTestCase {

    /** Arguments the handler passed to its search log query, captured by {@link StubHandler}. */
    private static class QueryCall {
        String userId;
        String virtualHostKey;
        int fetchSize;
    }

    /** A handler with a fixed user and a canned search log result. */
    private static class StubHandler extends SearchHistoryHandler {
        private final String userId;
        private final List<SearchLog> logs;
        private final RuntimeException failure;
        QueryCall call;

        StubHandler(final String userId, final List<SearchLog> logs) {
            this(userId, logs, null);
        }

        StubHandler(final String userId, final List<SearchLog> logs, final RuntimeException failure) {
            this.userId = userId;
            this.logs = logs;
            this.failure = failure;
        }

        @Override
        protected String getUserId() {
            return userId;
        }

        @Override
        protected List<SearchLog> selectSearchLogs(final String userId, final String virtualHostKey, final int fetchSize) {
            call = new QueryCall();
            call.userId = userId;
            call.virtualHostKey = virtualHostKey;
            call.fetchSize = fetchSize;
            if (failure != null) {
                throw failure;
            }
            return logs;
        }
    }

    /** A configuration whose search history flags and size are set by the test. */
    private static class HistoryFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;
        private final boolean historyEnabled;
        private final boolean searchLog;
        private final int size;

        HistoryFessConfig(final boolean historyEnabled, final boolean searchLog, final int size) {
            this.historyEnabled = historyEnabled;
            this.searchLog = searchLog;
            this.size = size;
        }

        @Override
        public boolean isSearchHistoryEnabled() {
            return historyEnabled;
        }

        @Override
        public boolean isSearchLog() {
            return searchLog;
        }

        @Override
        public Integer getSearchHistorySizeAsInteger() {
            return size;
        }
    }

    private static void useConfig(final boolean historyEnabled, final boolean searchLog, final int size) {
        ComponentUtil.setFessConfig(new HistoryFessConfig(historyEnabled, searchLog, size));
    }

    private static void useVirtualHostKey(final String key) {
        ComponentUtil.register(new VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return key;
            }
        }, "virtualHostHelper");
    }

    private static SearchLog log(final String searchParams, final LocalDateTime requestedAt, final long hitCount) {
        final SearchLog searchLog = new SearchLog();
        searchLog.setSearchParams(searchParams);
        searchLog.setRequestedAt(requestedAt);
        searchLog.setHitCount(hitCount);
        return searchLog;
    }

    private static String iso(final LocalDateTime ldt) {
        return DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.of(ldt, ZoneId.systemDefault()));
    }

    @Test
    public void test_methodGate_rejectsPost() throws Exception {
        final StubHandler handler = new StubHandler("alice", List.of());
        final CapturingResponse res = new CapturingResponse();
        handler.handle(new StubRequest("/api/v2/search-history").withMethod("POST"), res);
        assertEquals(405, res.status);
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
        assertEquals("GET", res.getHeader("Allow"));
        assertNull(handler.call);
    }

    @Test
    public void test_disabled_whenSearchHistoryIsOff() throws Exception {
        useConfig(false, true, 10);
        final StubHandler handler = new StubHandler("alice", List.of());
        final CapturingResponse res = new CapturingResponse();
        handler.handle(new StubRequest("/api/v2/search-history"), res);
        assertEquals(400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertTrue(res.body().contains("search history is not available"), res.body());
        assertNull(handler.call);
    }

    @Test
    public void test_disabled_whenSearchLogIsOff() throws Exception {
        useConfig(true, false, 10);
        final StubHandler handler = new StubHandler("alice", List.of());
        final CapturingResponse res = new CapturingResponse();
        handler.handle(new StubRequest("/api/v2/search-history"), res);
        assertEquals(400, res.status);
        assertTrue(res.body().contains("search history is not available"), res.body());
        assertNull(handler.call);
    }

    @Test
    public void test_anonymous_requiresAuth() throws Exception {
        useConfig(true, true, 10);
        for (final String userId : new String[] { null, "", " " }) {
            final StubHandler handler = new StubHandler(userId, List.of());
            final CapturingResponse res = new CapturingResponse();
            handler.handle(new StubRequest("/api/v2/search-history"), res);
            assertEquals(401, res.status, "userId=" + userId);
            assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
            assertNull(handler.call);
        }
    }

    @Test
    public void test_userCodeParameter_isNotAnIdentity() throws Exception {
        // ?userCode= is client-suppliable; the real getUserId() must only trust the logged-in user bean,
        // so with no logged-in user the request stays anonymous (401) and no search log is queried.
        useConfig(true, true, 10);
        final boolean[] queried = { false };
        final SearchHistoryHandler handler = new SearchHistoryHandler() {
            @Override
            protected List<SearchLog> selectSearchLogs(final String userId, final String virtualHostKey, final int fetchSize) {
                queried[0] = true;
                return List.of();
            }
        };
        final Map<String, String[]> params = new HashMap<>();
        params.put("userCode", new String[] { "alice" });
        final CapturingResponse res = new CapturingResponse();
        handler.handle(new StubRequest("/api/v2/search-history", params), res);
        assertEquals(401, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"auth_required\""), res.body());
        assertFalse(queried[0]);
    }

    @Test
    public void test_dedupeNewestFirstAndSizeCap() throws Exception {
        useConfig(true, true, 2);
        useVirtualHostKey(null);
        final LocalDateTime now = LocalDateTime.of(2026, 10, 1, 12, 0, 0);
        final List<SearchLog> logs = new ArrayList<>();
        logs.add(log("{\"q\":\"fess\"}", now, 3));
        logs.add(log("{\"q\":\"opensearch\"}", now.minusMinutes(1), 5));
        logs.add(log("{\"q\":\"fess\"}", now.minusMinutes(2), 7));
        logs.add(log("{\"q\":\"crawler\"}", now.minusMinutes(3), 9));
        final StubHandler handler = new StubHandler("alice", logs);
        final CapturingResponse res = new CapturingResponse();
        handler.handle(new StubRequest("/api/v2/search-history"), res);
        assertEquals(200, res.status, res.body());
        final String body = res.body();
        assertTrue(body.contains("\"record_count\":2"), body);
        assertTrue(body.contains("{\"q\":\"fess\",\"requested_at\":\"" + iso(now) + "\",\"hit_count\":3}"), body);
        assertTrue(body.contains("{\"q\":\"opensearch\",\"requested_at\":\"" + iso(now.minusMinutes(1)) + "\",\"hit_count\":5}"), body);
        assertTrue(body.indexOf("\"fess\"") < body.indexOf("\"opensearch\""), body);
        assertFalse(body.contains("\"hit_count\":7"), body);
        assertFalse(body.contains("crawler"), body);
        assertEquals("alice", handler.call.userId);
        assertEquals(20, handler.call.fetchSize);
    }

    @Test
    public void test_fetchSize_isCappedAt200() throws Exception {
        useConfig(true, true, 50);
        useVirtualHostKey(null);
        final StubHandler handler = new StubHandler("alice", List.of());
        final CapturingResponse res = new CapturingResponse();
        handler.handle(new StubRequest("/api/v2/search-history"), res);
        assertEquals(200, res.status, res.body());
        assertTrue(res.body().contains("\"record_count\":0"), res.body());
        assertTrue(res.body().contains("\"data\":[]"), res.body());
        assertEquals(200, handler.call.fetchSize);
    }

    @Test
    public void test_skipsBlankAndUnparseableEntries() throws Exception {
        useConfig(true, true, 10);
        useVirtualHostKey(null);
        final LocalDateTime now = LocalDateTime.of(2026, 10, 1, 12, 0, 0);
        final List<SearchLog> logs = new ArrayList<>();
        logs.add(log(null, now, 1));
        logs.add(log("", now, 1));
        logs.add(log("{not json", now, 1));
        logs.add(log("[\"q\"]", now, 1));
        logs.add(log("{\"sort\":\"score.desc\"}", now, 1));
        logs.add(log("{\"q\":\"  \"}", now, 1));
        logs.add(log("{\"q\":42}", now, 1));
        logs.add(log("{\"q\":\"kept\"}", now.minusMinutes(5), 2));
        final StubHandler handler = new StubHandler("alice", logs);
        final CapturingResponse res = new CapturingResponse();
        handler.handle(new StubRequest("/api/v2/search-history"), res);
        assertEquals(200, res.status, res.body());
        final String body = res.body();
        assertTrue(body.contains("\"record_count\":1"), body);
        assertTrue(body.contains("\"q\":\"kept\""), body);
    }

    @Test
    public void test_payloadShape_restoresAllConditions() throws Exception {
        useConfig(true, true, 10);
        useVirtualHostKey(null);
        final LocalDateTime now = LocalDateTime.of(2026, 10, 1, 12, 0, 0);
        final String stored = "{\"q\":\"fess \\\"full text\\\"\",\"fields\":{\"label\":[\"docs\",\"blog\"],\"filetype\":[\"pdf\"]},"
                + "\"ex_q\":[\"title:fess\"],\"sort\":\"last_modified.desc\",\"lang\":[\"ja\"]}";
        final StubHandler handler = new StubHandler("alice", List.of(log(stored, now, 42)));
        final CapturingResponse res = new CapturingResponse();
        handler.handle(new StubRequest("/api/v2/search-history"), res);
        assertEquals(200, res.status, res.body());
        final String body = res.body();
        assertTrue(body.contains("\"status\":0"), body);
        assertTrue(
                body.contains("\"record_count\":1,\"data\":[{\"q\":\"fess \\\"full text\\\"\","
                        + "\"fields\":{\"label\":[\"docs\",\"blog\"],\"filetype\":[\"pdf\"]},\"ex_q\":[\"title:fess\"],"
                        + "\"sort\":\"last_modified.desc\",\"lang\":[\"ja\"],\"requested_at\":\"" + iso(now) + "\",\"hit_count\":42}]"),
                body);
    }

    @Test
    public void test_virtualHostKey_blankIsNormalisedToEmpty() throws Exception {
        useConfig(true, true, 10);
        for (final String key : new String[] { null, "", " " }) {
            useVirtualHostKey(key);
            final StubHandler handler = new StubHandler("alice", List.of());
            handler.handle(new StubRequest("/api/v2/search-history"), new CapturingResponse());
            assertEquals("key=" + key, "", handler.call.virtualHostKey);
        }
    }

    @Test
    public void test_virtualHostKey_isPassedThrough() throws Exception {
        useConfig(true, true, 10);
        useVirtualHostKey("site1");
        final StubHandler handler = new StubHandler("alice", List.of());
        handler.handle(new StubRequest("/api/v2/search-history"), new CapturingResponse());
        assertEquals("site1", handler.call.virtualHostKey);
    }

    @Test
    public void test_setUpConditionBean_filtersOnUserAccessTypeOffsetAndVirtualHost() {
        final SearchLogCB cb = new SearchLogCB();
        new SearchHistoryHandler().setUpConditionBean(cb, "alice", "", 30);
        final String query = cb.query().getQuery().toString().replaceAll("\\s+", "");
        assertTrue(query.contains("{\"term\":{\"user\":{\"value\":\"alice\""), query);
        assertTrue(query.contains("{\"term\":{\"accessType\":{\"value\":\"json\""), query);
        assertTrue(query.contains("{\"term\":{\"queryOffset\":{\"value\":0"), query);
        // An empty virtual host key must still be filtered on: the writer stores "" for it.
        assertTrue(query.contains("{\"term\":{\"virtualHost\":{\"value\":\"\""), query);
        assertFalse(query.contains("searchParams"), query);
        assertEquals("requestedAt", cb.query().getFieldSortBuilderList().get(0).getFieldName());
        assertEquals("DESC", cb.query().getFieldSortBuilderList().get(0).order().name());
        assertEquals(30, cb.getFetchSize());
        // Only the fields the handler reads are fetched; the large "documents" array stays out.
        final SearchRequestBuilder builder = cb.build(new SearchRequestBuilder(new SearchEngineClient(), SearchAction.INSTANCE));
        assertEquals(List.of("requestedAt", "hitCount", "searchParams"), List.of(builder.request().source().fetchSource().includes()));
        assertEquals(0, builder.request().source().fetchSource().excludes().length);
    }

    @Test
    public void test_unexpectedFailure_returnsInternalError() throws Exception {
        useConfig(true, true, 10);
        useVirtualHostKey(null);
        final StubHandler handler = new StubHandler("alice", null, new IllegalStateException("boom"));
        final CapturingResponse res = new CapturingResponse();
        handler.handle(new StubRequest("/api/v2/search-history"), res);
        assertEquals(500, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"internal_error\""), res.body());
        assertFalse(res.body().contains("boom"), res.body());
    }

    /** Minimal HttpServletResponse stub. */
    private static class CapturingResponse implements HttpServletResponse {
        final StringWriter sw = new StringWriter();
        final PrintWriter writer = new PrintWriter(sw);
        int status = 200;
        String contentType;
        final java.util.Map<String, String> headers = new java.util.HashMap<>();

        String body() {
            writer.flush();
            return sw.toString();
        }

        @Override
        public void setStatus(final int sc) {
            this.status = sc;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public void setContentType(final String type) {
            this.contentType = type;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        @Override
        public String getCharacterEncoding() {
            return "UTF-8";
        }

        @Override
        public void setCharacterEncoding(final String s) {
        }

        @Override
        public jakarta.servlet.ServletOutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContentLength(final int len) {
        }

        @Override
        public void setContentLengthLong(final long len) {
        }

        @Override
        public void setBufferSize(final int size) {
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
        public void setLocale(final java.util.Locale loc) {
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public void addCookie(final jakarta.servlet.http.Cookie cookie) {
        }

        @Override
        public boolean containsHeader(final String name) {
            return false;
        }

        @Override
        public String encodeURL(final String url) {
            return url;
        }

        @Override
        public String encodeRedirectURL(final String url) {
            return url;
        }

        @Override
        public void sendError(final int sc, final String msg) {
        }

        @Override
        public void sendError(final int sc) {
        }

        @Override
        public void sendRedirect(final String location) {
        }

        @Override
        public void sendRedirect(final String location, final int sc) {
        }

        @Override
        public void sendRedirect(final String location, final boolean clearBuffer) {
        }

        @Override
        public void sendRedirect(final String location, final int sc, final boolean clearBuffer) {
        }

        @Override
        public void setDateHeader(final String name, final long date) {
        }

        @Override
        public void addDateHeader(final String name, final long date) {
        }

        @Override
        public void setHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void addHeader(final String name, final String value) {
            headers.put(name, value);
        }

        @Override
        public void setIntHeader(final String name, final int value) {
        }

        @Override
        public void addIntHeader(final String name, final int value) {
        }

        @Override
        public String getHeader(final String name) {
            return headers.get(name);
        }

        @Override
        public java.util.Collection<String> getHeaders(final String name) {
            final String v = headers.get(name);
            return v == null ? java.util.Collections.emptyList() : java.util.Collections.singletonList(v);
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return headers.keySet();
        }
    }

    /** Minimal HttpServletRequest stub. */
    private static class StubRequest implements HttpServletRequest {
        private final String uri;
        private final Map<String, Object> attrs = new HashMap<>();
        private final Map<String, String[]> params;
        private String method = "GET";

        StubRequest(final String uri) {
            this(uri, Collections.emptyMap());
        }

        StubRequest(final String uri, final Map<String, String[]> params) {
            this.uri = uri;
            this.params = params == null ? Collections.emptyMap() : params;
        }

        StubRequest withMethod(final String m) {
            this.method = m;
            return this;
        }

        @Override
        public String getServletPath() {
            return uri;
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public String getRequestURI() {
            return uri;
        }

        @Override
        public String getContextPath() {
            return "";
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
        public RequestDispatcher getRequestDispatcher(final String path) {
            return null;
        }

        @Override
        public String getAuthType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public jakarta.servlet.http.Cookie[] getCookies() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getDateHeader(final String name) {
            throw new UnsupportedOperationException();
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
            return new StringBuffer(uri);
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
        public ServletInputStream getInputStream() {
            throw new UnsupportedOperationException();
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
            return Collections.enumeration(java.util.Collections.singleton(java.util.Locale.ROOT));
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
