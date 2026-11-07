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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.SearchEngineUnavailableException;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.TagTypeHelper;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.lastaflute.core.message.UserMessages;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

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
 * Unit tests for {@link SimilarDocumentsHandler}: the wire contract of
 * {@code GET /api/v2/documents/{docId}/similar}.
 */
public class SimilarDocumentsHandlerTest extends UnitFessTestCase {

    private static final String DOC_ID = "abc123";

    private static final String PATH = "/api/v2/documents/" + DOC_ID + "/similar";

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    /** Calls of the search helper: the document id and the size it was asked for. */
    private final List<String> calls = new ArrayList<>();

    private final List<OptionalThing<FessUserBean>> callUsers = new ArrayList<>();

    private final List<List<String>> callFields = new ArrayList<>();

    private boolean userTagEnabled = false;

    /** The user the search handler of the request reports; null for a guest. */
    private String userId;

    private long total = -1;

    private int rateLimit = 0;

    private int pageSize = 10;

    private int pageMaxSize = 100;

    /** What the search helper answers: the documents, or null for "no such document". */
    private List<Map<String, Object>> similar = new ArrayList<>();

    private RuntimeException failure;

    @Override
    protected void setUp(final org.junit.jupiter.api.TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getApiSearchSimilarRateLimitPerMinuteAsInteger() {
                return rateLimit;
            }

            @Override
            public Integer getPagingSearchPageSizeAsInteger() {
                return pageSize;
            }

            @Override
            public Integer getPagingSearchPageMaxSizeAsInteger() {
                return pageMaxSize;
            }

            @Override
            public int getApiParamMaxLengthOrDefault() {
                return 1000;
            }

            @Override
            public String getIndexFieldTag() {
                return "tag";
            }

            @Override
            public boolean isUserTagEnabled() {
                return userTagEnabled;
            }
        });
        final QueryFieldConfig queryFieldConfig = new QueryFieldConfig() {
            @Override
            public boolean isApiResponseField(final String field) {
                return !"secret".equals(field);
            }
        };
        queryFieldConfig.setResponseFields(new String[] { "title", "url" });
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");
        ComponentUtil.register(new SearchHelper() {
            @Override
            public OptionalEntity<QueryResponseList> getSimilarDocuments(final String docId, final int size, final String[] responseFields,
                    final OptionalThing<FessUserBean> userBean) {
                calls.add(docId + ":" + size);
                callFields.add(List.of(responseFields));
                callUsers.add(userBean);
                if (failure != null) {
                    throw failure;
                }
                if (similar == null) {
                    return OptionalEntity.empty();
                }
                final QueryResponseList list = new QueryResponseList(similar, total >= 0 ? total : similar.size(), "EQUAL_TO", 3L, false,
                        false, false, null, 0, size, 0);
                list.setExecTime(1234);
                return OptionalEntity.of(list);
            }
        }, "searchHelper");
    }

    private SimilarDocumentsHandler handler() {
        return new SimilarDocumentsHandler() {
            @Override
            protected SearchHandler createSearchHandler() {
                return new SearchHandler() {
                    @Override
                    protected OptionalThing<FessUserBean> getSavedUserBean() {
                        return OptionalThing.empty();
                    }

                    @Override
                    String resolveUserId(final OptionalThing<FessUserBean> userBean) {
                        return userId;
                    }
                };
            }

            @Override
            protected String getRateLimitKey(final HttpServletRequest req) {
                return "u:alice";
            }
        };
    }

    private CapturingResponse get(final String docId, final String num) throws Exception {
        final Map<String, String[]> params = new HashMap<>();
        if (num != null) {
            params.put("num", new String[] { num });
        }
        final CapturingResponse res = new CapturingResponse();
        handler().handle(new StubRequest("/api/v2/documents/" + docId + "/similar", params), res, docId);
        return res;
    }

    private static Map<String, Object> doc(final String title) {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("title", title);
        doc.put("url_link", "https://example.com/" + title);
        doc.put("secret", "hidden");
        doc.put("tag", "encoded-tag-owner");
        return doc;
    }

    private static JsonNode response(final CapturingResponse res) throws Exception {
        return MAPPER.readTree(res.body()).get("response");
    }

    @Test
    public void test_rejectsNonGet() throws Exception {
        final CapturingResponse res = new CapturingResponse();
        handler().handle(new StubRequest(PATH, Collections.emptyMap()).withMethod("POST"), res, DOC_ID);
        assertEquals(405, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
        assertEquals("GET", res.getHeader("Allow"));
        assertTrue(calls.isEmpty());
    }

    @Test
    public void test_invalidDocId_isRejectedBeforeAnythingIsLookedUp() throws Exception {
        for (final String docId : new String[] { "", " ", "a b", "../x", "a;b", "a\"b", "a".repeat(513) }) {
            final CapturingResponse res = get(docId, null);
            assertEquals(400, res.status, "docId=[" + docId + "] " + res.body());
            assertTrue(res.body().contains("invalid doc_id"), res.body());
        }
        assertTrue(calls.isEmpty());
    }

    @Test
    public void test_success_returnsTheWhitelistedFieldsOfEachDocument() throws Exception {
        similar = List.of(doc("one"), doc("two"));
        final CapturingResponse res = get(DOC_ID, null);
        assertEquals(200, res.status, res.body());
        final JsonNode body = response(res);
        assertEquals(0, body.get("status").asInt());
        assertEquals(DOC_ID, body.get("doc_id").asString());
        assertEquals(2, body.get("record_count").asInt());
        assertEquals(10, body.get("page_size").asInt());
        assertFalse(body.get("partial").asBoolean());
        assertFalse(body.get("timed_out").asBoolean());
        assertFalse(body.get("shard_failed").asBoolean());
        final JsonNode data = body.get("data");
        assertEquals(2, data.size());
        assertEquals("one", data.get(0).get("title").asString());
        assertEquals("https://example.com/one", data.get(0).get("url_link").asString());
        assertEquals("two", data.get(1).get("title").asString());
        // a field that is not an API response field, and the raw tag field, never leave the server
        assertFalse(data.get(0).has("secret"), res.body());
        assertFalse(data.get(0).has("tag"), res.body());
    }

    @Test
    public void test_envelope_hasTheMetadataOfASearchUnderTheSameNames() throws Exception {
        similar = List.of(doc("one"), doc("two"));
        total = 57;
        final JsonNode body = response(get(DOC_ID, "2"));
        final List<String> keys = new ArrayList<>();
        body.propertyNames().forEach(keys::add);
        org.junit.jupiter.api.Assertions.assertEquals(List.of("status", "doc_id", "exec_time", "query_time", "page_size", "record_count",
                "record_count_relation", "partial", "timed_out", "shard_failed", "data"), keys);
        assertEquals("1.23", body.get("exec_time").asString());
        assertEquals(3, body.get("query_time").asInt());
        assertEquals(2, body.get("page_size").asInt());
        // the number of documents with the signature, not of those on the page
        assertEquals(57, body.get("record_count").asInt());
        assertEquals("EQUAL_TO", body.get("record_count_relation").asString());
    }

    @Test
    public void test_fields_areReadAsASearchReadsThem() throws Exception {
        get(DOC_ID, null);
        assertEquals(List.of("title", "url"), callFields.get(0));
        // the tag field is read while user tags are enabled
        userTagEnabled = true;
        ComponentUtil.register(new StubTagTypeHelper(), "tagTypeHelper");
        get(DOC_ID, null);
        assertEquals(List.of("title", "url", "tag"), callFields.get(1));
    }

    private static TagType tagType(final String name, final String owner) {
        final TagType tagType = new TagType();
        tagType.setName(name);
        tagType.setOwner(owner);
        tagType.setPermissions(new String[] { "1" + owner });
        return tagType;
    }

    @Test
    public void test_tags_aVisibleTagIsShownAndAnInvisibleOneIsNot() throws Exception {
        userTagEnabled = true;
        userId = "frank";
        final TagType visible = tagType("foo", "frank");
        final TagType hidden = tagType("bar", "bob");
        final StubTagTypeHelper helper = new StubTagTypeHelper();
        helper.visible.put(visible.getTagValue(), visible);
        ComponentUtil.register(helper, "tagTypeHelper");
        final Map<String, Object> tagged = doc("one");
        tagged.put("tag", List.of(visible.getTagValue(), hidden.getTagValue()));
        similar = List.of(tagged, doc("two"));

        final JsonNode data = response(get(DOC_ID, null)).get("data");

        // the raw values decode to the name and the owner of every tag, so only the visible ones are shown
        assertFalse(data.get(0).has("tag"), data.toString());
        assertEquals(1, data.get(0).get("tags").size());
        assertEquals(visible.getTagValue(), data.get(0).get("tags").get(0).get("value").asString());
        assertEquals("foo", data.get(0).get("tags").get(0).get("name").asString());
        assertEquals("frank", data.get(0).get("tags").get(0).get("owner").asString());
        assertTrue(data.get(0).get("tags").get(0).get("mine").asBoolean());
        assertFalse(data.toString().contains(hidden.getTagValue()), data.toString());
        assertFalse(data.get(1).has("tags"), data.toString());
    }

    @Test
    public void test_tags_aGuestSeesNoTagAndNoTagIsLookedUp() throws Exception {
        userTagEnabled = true;
        userId = null;
        final StubTagTypeHelper helper = new StubTagTypeHelper();
        helper.visible.put(tagType("foo", "frank").getTagValue(), tagType("foo", "frank"));
        ComponentUtil.register(helper, "tagTypeHelper");
        final Map<String, Object> tagged = doc("one");
        tagged.put("tag", List.of(tagType("foo", "frank").getTagValue()));
        similar = List.of(tagged);
        final JsonNode data = response(get(DOC_ID, null)).get("data");
        assertFalse(data.get(0).has("tag"), data.toString());
        assertFalse(data.get(0).has("tags"), data.toString());
        assertEquals(0, helper.visibleValuesCalls);
    }

    /** A tag type helper whose visible tags are fixed by the test. */
    private static class StubTagTypeHelper extends TagTypeHelper {
        final Map<String, TagType> visible = new HashMap<>();
        int visibleValuesCalls;

        @Override
        public java.util.Set<String> getVisibleTagValues(final SearchRequestParams.SearchRequestType type) {
            visibleValuesCalls++;
            return java.util.Set.copyOf(visible.keySet());
        }

        @Override
        public Map<String, TagType> getVisibleTagTypes(final java.util.Collection<String> values,
                final SearchRequestParams.SearchRequestType type) {
            final Map<String, TagType> map = new HashMap<>();
            values.forEach(v -> {
                if (visible.containsKey(v)) {
                    map.put(v, visible.get(v));
                }
            });
            return map;
        }

        @Override
        protected List<String> getSharedRoleList() {
            return List.of("Rguest");
        }
    }

    @Test
    public void test_noSimilarDocument_isAnEmptyList() throws Exception {
        final CapturingResponse res = get(DOC_ID, null);
        assertEquals(200, res.status, res.body());
        assertEquals(0, response(res).get("record_count").asInt());
        assertEquals(0, response(res).get("data").size());
    }

    @Test
    public void test_missingOrHiddenSource_isNotFound() throws Exception {
        similar = null;
        final CapturingResponse res = get(DOC_ID, null);
        assertEquals(404, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"not_found\""), res.body());
        assertTrue(res.body().contains("doc not found: " + DOC_ID), res.body());
        assertFalse(res.body().contains("\"data\""), res.body());
    }

    @Test
    public void test_theCallerIsPassedToTheSearch() throws Exception {
        get(DOC_ID, null);
        assertEquals(List.of(DOC_ID + ":10"), calls);
        assertFalse(callUsers.get(0).isPresent());
    }

    @Test
    public void test_size_defaultsToTen() throws Exception {
        get(DOC_ID, null);
        get(DOC_ID, "");
        get(DOC_ID, "many");
        assertEquals(List.of("abc123:10", "abc123:10", "abc123:10"), calls);
    }

    @Test
    public void test_size_numIsHonouredUpToThePageMaxSizeAsInSearch() throws Exception {
        get(DOC_ID, null);
        get(DOC_ID, "1");
        get(DOC_ID, "5");
        get(DOC_ID, "21");
        get(DOC_ID, "100");
        get(DOC_ID, "101");
        get(DOC_ID, "2147483647");
        assertEquals(List.of("abc123:10", "abc123:1", "abc123:5", "abc123:21", "abc123:100", "abc123:100", "abc123:100"), calls);
    }

    @Test
    public void test_size_defaultIsThePageSizeOfSearch() throws Exception {
        pageSize = 25;
        get(DOC_ID, null);
        get(DOC_ID, "");
        assertEquals(List.of("abc123:25", "abc123:25"), calls);
    }

    @Test
    public void test_size_isNeverMoreThanThePageMaxSize() throws Exception {
        pageMaxSize = 8;
        get(DOC_ID, "15");
        get(DOC_ID, "3");
        assertEquals(List.of("abc123:8", "abc123:3"), calls);
    }

    @Test
    public void test_size_aNumThatIsNotAnIntIsTheDefaultAsInSearch() throws Exception {
        for (final String num : new String[] { "abc", "1.5", "99999999999", "+99999999999", "1".repeat(5000) }) {
            get(DOC_ID, num);
        }
        assertEquals(List.of("abc123:10", "abc123:10", "abc123:10", "abc123:10", "abc123:10"), calls);
    }

    @Test
    public void test_size_zeroAndNegativeAreRejected() throws Exception {
        for (final String num : new String[] { "0", "-1", "-2147483648" }) {
            final CapturingResponse res = get(DOC_ID, num);
            assertEquals(400, res.status, "num=" + num + " " + res.body());
            assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        }
        assertTrue(calls.isEmpty());
    }

    @Test
    public void test_start_isNotSupported() throws Exception {
        final Map<String, String[]> params = new HashMap<>();
        params.put("start", new String[] { "10" });
        params.put("offset", new String[] { "10" });
        final CapturingResponse res = new CapturingResponse();
        handler().handle(new StubRequest(PATH, params), res, DOC_ID);
        assertEquals(200, res.status, res.body());
        assertEquals(List.of("abc123:10"), calls);
        assertFalse(res.body().contains("\"page_number\""), res.body());
        assertFalse(res.body().contains("\"next_page\""), res.body());
    }

    private static LoginRateLimiter registerLimiter() {
        final LoginRateLimiter limiter = new LoginRateLimiter();
        ComponentUtil.register(limiter, "loginRateLimiter");
        ComponentUtil.register(limiter, LoginRateLimiter.class.getCanonicalName());
        return limiter;
    }

    @Test
    public void test_rateLimitExceeded_is429AndNothingIsSearched() throws Exception {
        rateLimit = 2;
        registerLimiter();
        assertEquals(200, get(DOC_ID, null).status);
        assertEquals(200, get(DOC_ID, null).status);
        final CapturingResponse res = get(DOC_ID, null);
        assertEquals(429, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"rate_limited\""), res.body());
        assertEquals("60", res.getHeader("Retry-After"));
        assertEquals(2, calls.size());
    }

    @Test
    public void test_rateLimitIsSeparateFromChatAndExport() throws Exception {
        rateLimit = 1;
        final LoginRateLimiter limiter = registerLimiter();
        for (int i = 0; i < 30; i++) {
            limiter.allow(LoginRateLimiter.Scope.CHAT, "u:alice", 30, 60);
            limiter.allow(LoginRateLimiter.Scope.EXPORT, "u:alice", 30, 60);
        }
        assertEquals(200, get(DOC_ID, null).status);
        assertEquals(429, get(DOC_ID, null).status);
    }

    @Test
    public void test_rateLimitZeroIsUnlimited() throws Exception {
        rateLimit = 0;
        // No limiter is registered, so the limiter must not be reached.
        for (int i = 0; i < 3; i++) {
            assertEquals(200, get(DOC_ID, null).status);
        }
    }

    @Test
    public void test_searchEngineUnavailable_is503() throws Exception {
        failure = new SearchEngineUnavailableException("out of capacity", new RuntimeException("rejected"));
        final CapturingResponse res = get(DOC_ID, null);
        assertEquals(503, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"service_unavailable\""), res.body());
        assertNotNull(res.getHeader("Retry-After"));
    }

    @Test
    public void test_invalidQueryOfOurOwn_is500NotTheCallersFault() throws Exception {
        failure = new InvalidQueryException(messages -> messages.addErrorsInvalidQueryCannotProcess(UserMessages.GLOBAL_PROPERTY_KEY),
                "Failed to process the query.", new RuntimeException("index detail"));
        final CapturingResponse res = get(DOC_ID, null);
        assertEquals(500, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"internal_error\""), res.body());
        assertFalse(res.body().contains("index detail"), res.body());
    }

    @Test
    public void test_unexpectedFailure_is500WithoutTheMessage() throws Exception {
        failure = new IllegalStateException("secret connection string");
        final CapturingResponse res = get(DOC_ID, null);
        assertEquals(500, res.status, res.body());
        assertTrue(res.body().contains("\"code\":\"internal_error\""), res.body());
        assertFalse(res.body().contains("secret connection string"), res.body());
    }

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
