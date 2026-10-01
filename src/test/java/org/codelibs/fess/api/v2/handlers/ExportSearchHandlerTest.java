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
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.BooleanFunction;
import org.codelibs.fess.util.ComponentUtil;
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

public class ExportSearchHandlerTest extends UnitFessTestCase {

    private static final String PATH = "/api/v2/documents/export";

    private static final String BOM = "\uFEFF";

    /** Settings of the export configuration a test installs with {@link #configure}. */
    private static final class Settings {
        boolean enabled = true;
        int maxSize = 1000;
        String fields = "title,url_link,last_modified,content_length,filetype";
        int rateLimit = 0;
        String csvEncoding = "UTF-8";
        int pageMaxSize = 100;
    }

    private static void configure(final Settings settings) {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isApiSearchExport() {
                return settings.enabled;
            }

            @Override
            public Integer getApiSearchExportMaxSizeAsInteger() {
                return settings.maxSize;
            }

            @Override
            public String getApiSearchExportFields() {
                return settings.fields;
            }

            @Override
            public Integer getApiSearchExportRateLimitPerMinuteAsInteger() {
                return settings.rateLimit;
            }

            @Override
            public String getCsvFileEncoding() {
                return settings.csvEncoding;
            }

            @Override
            public Integer getPagingSearchPageMaxSizeAsInteger() {
                return settings.pageMaxSize;
            }
        });
    }

    /** The search helper and field config the unit container lacks; every field is an API field but "secret". */
    private static void registerSearch(final List<Map<String, Object>> docs, final List<Boolean> cursorResults) {
        ComponentUtil.register(new QueryFieldConfig() {
            @Override
            public boolean isApiResponseField(final String field) {
                return !"secret".equals(field);
            }
        }, "queryFieldConfig");
        ComponentUtil.register(new SearchHelper() {
            @Override
            public long scrollSearch(final SearchRequestParams params, final BooleanFunction<Map<String, Object>> cursor,
                    final OptionalThing<FessUserBean> userBean) {
                long count = 0;
                for (final Map<String, Object> doc : docs) {
                    count++;
                    final boolean more = cursor.apply(doc);
                    cursorResults.add(more);
                    if (!more) {
                        break;
                    }
                }
                return count;
            }
        }, "searchHelper");
    }

    private static Map<String, Object> doc(final String title) {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("title", title);
        doc.put("url_link", "https://example.com/" + title);
        doc.put("filetype", "html");
        doc.put("content_length", 12L);
        doc.put("secret", "hidden");
        return doc;
    }

    @SafeVarargs
    private static CapturingResponse export(final String format, final Map<String, Object>... docs) throws Exception {
        return export(format, Arrays.asList(docs), new ArrayList<>());
    }

    private static CapturingResponse export(final String format, final List<Map<String, Object>> docs, final List<Boolean> cursorResults)
            throws Exception {
        registerSearch(docs, cursorResults);
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        if (format != null) {
            params.put("format", new String[] { format });
        }
        final CapturingResponse res = new CapturingResponse();
        new ExportSearchHandler().handle(new StubRequest(PATH, params), res);
        return res;
    }

    /** The CsvWriter default quotes every cell. */
    private static String row(final String... cells) {
        return Arrays.stream(cells).map(c -> "\"" + c + "\"").collect(java.util.stream.Collectors.joining(","));
    }

    private static final String HEADER = row("title", "url_link", "last_modified", "content_length", "filetype");

    private static String[] lines(final String body) {
        return body.split("\\R");
    }

    @Test
    public void test_export_disabled_isRejected() throws Exception {
        final Settings settings = new Settings();
        settings.enabled = false;
        configure(settings);
        final CapturingResponse res = export("csv", doc("a"));
        assertEquals(res.body(), 400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertTrue(res.body().contains("search export is not available"), res.body());
        assertNull(res.getHeader("Content-Disposition"));
    }

    @Test
    public void test_export_nonGet_isMethodNotAllowed() throws Exception {
        configure(new Settings());
        final CapturingResponse res = new CapturingResponse();
        new ExportSearchHandler().handle(new StubRequest(PATH).withMethod("POST"), res);
        assertEquals(405, res.status);
        assertTrue(res.body().contains("\"code\":\"method_not_allowed\""), res.body());
        assertEquals("Allow header must be set on 405", "GET", res.getHeader("Allow"));
    }

    @Test
    public void test_export_unknownFormat_isRejected() throws Exception {
        configure(new Settings());
        final CapturingResponse res = export("xml", doc("a"));
        assertEquals(res.body(), 400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertTrue(res.body().contains("format must be csv or json"), res.body());
        assertNull(res.getHeader("Content-Disposition"));
    }

    @Test
    public void test_export_csv_headerRowsAndHeaders() throws Exception {
        configure(new Settings());
        final CapturingResponse res = export("csv", doc("a"), doc("b"));
        final String body = res.body();
        assertEquals(body, "text/csv; charset=UTF-8", res.contentType);
        assertEquals("attachment; filename=\"search_results.csv\"", res.getHeader("Content-Disposition"));
        final String[] lines = lines(body);
        assertEquals(body, 3, lines.length);
        // last_modified is absent from the documents: an empty cell.
        assertEquals(BOM + HEADER, lines[0]);
        assertEquals(row("a", "https://example.com/a", "", "12", "html"), lines[1]);
        assertEquals(row("b", "https://example.com/b", "", "12", "html"), lines[2]);
    }

    @Test
    public void test_export_formatDefaultsToCsv() throws Exception {
        configure(new Settings());
        final CapturingResponse res = export(null, doc("a"));
        assertEquals(res.body(), "text/csv; charset=UTF-8", res.contentType);
    }

    @Test
    public void test_export_csv_quotesSpecialCharacters() throws Exception {
        configure(new Settings());
        final Map<String, Object> doc = doc("x");
        doc.put("title", "a,\"b\"");
        final CapturingResponse res = export("csv", doc);
        assertEquals(res.body(), row("a,\"\"b\"\"", "https://example.com/x", "", "12", "html"), lines(res.body())[1]);
    }

    @Test
    public void test_export_csv_multiValuedFieldIsJoinedWithSpace() throws Exception {
        configure(new Settings());
        final Map<String, Object> doc = doc("x");
        doc.put("filetype", List.of("html", "text"));
        final CapturingResponse res = export("csv", doc);
        assertEquals(res.body(), row("x", "https://example.com/x", "", "12", "html text"), lines(res.body())[1]);
    }

    @Test
    public void test_export_csv_formulaCellsGetALeadingQuote() throws Exception {
        configure(new Settings());
        final List<Map<String, Object>> docs = new ArrayList<>();
        for (final String title : new String[] { "=1+1", "+1", "-1", "@SUM(A1)", "\tx", "\rx", "safe=1" }) {
            final Map<String, Object> doc = doc("x");
            doc.put("title", title);
            docs.add(doc);
        }
        final CapturingResponse res = export("csv", docs, new ArrayList<>());
        final String body = res.body();
        assertTrue(body, body.contains("\n\"'=1+1\","));
        assertTrue(body, body.contains("\n\"'+1\","));
        assertTrue(body, body.contains("\n\"'-1\","));
        assertTrue(body, body.contains("\n\"'@SUM(A1)\","));
        assertTrue(body, body.contains("\"'\tx\","));
        assertTrue(body, body.contains("\"'\rx\""));
        // A leading quote only where the first character is the trigger.
        assertTrue(body, body.contains("\n\"safe=1\","));
    }

    @Test
    public void test_export_csv_utf8HasBomOtherEncodingsDoNot() throws Exception {
        final Settings settings = new Settings();
        configure(settings);
        assertTrue(export("csv", doc("a")).body().startsWith(BOM));

        settings.csvEncoding = "Windows-31J";
        configure(settings);
        final CapturingResponse res = export("csv", doc("a"));
        assertEquals("text/csv; charset=Windows-31J", res.contentType);
        assertTrue(res.body(), res.body().startsWith("\"title\","));
    }

    @Test
    public void test_export_csv_noMatchIsAHeaderOnlyFile() throws Exception {
        configure(new Settings());
        final CapturingResponse res = export("csv");
        assertEquals(res.body(), "text/csv; charset=UTF-8", res.contentType);
        assertEquals(res.body(), BOM + HEADER, res.body().trim());
    }

    @Test
    public void test_export_capStopsTheScroll() throws Exception {
        final Settings settings = new Settings();
        settings.maxSize = 3;
        configure(settings);
        final List<Map<String, Object>> docs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            docs.add(doc("d" + i));
        }
        final List<Boolean> cursorResults = new ArrayList<>();
        final CapturingResponse res = export("csv", docs, cursorResults);
        // header + 3 rows, and the scroll was told to stop at the third document.
        assertEquals(res.body(), 4, lines(res.body()).length);
        assertEquals(Arrays.asList(true, true, false), cursorResults);
    }

    @Test
    public void test_export_capStopsTheScroll_json() throws Exception {
        final Settings settings = new Settings();
        settings.maxSize = 2;
        configure(settings);
        final List<Map<String, Object>> docs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            docs.add(doc("d" + i));
        }
        final List<Boolean> cursorResults = new ArrayList<>();
        final CapturingResponse res = export("json", docs, cursorResults);
        assertEquals(res.body(), 2, new JsonMapper().readTree(res.body()).get("data").size());
        assertEquals(Arrays.asList(true, false), cursorResults);
    }

    @Test
    public void test_export_batchSizeIsTheSmallerOfCapAndPageMax() throws Exception {
        final List<Integer> pageSizes = new ArrayList<>();
        ComponentUtil.register(new QueryFieldConfig() {
            @Override
            public boolean isApiResponseField(final String field) {
                return true;
            }
        }, "queryFieldConfig");
        ComponentUtil.register(new SearchHelper() {
            @Override
            public long scrollSearch(final SearchRequestParams params, final BooleanFunction<Map<String, Object>> cursor,
                    final OptionalThing<FessUserBean> userBean) {
                pageSizes.add(params.getPageSize());
                return 0;
            }
        }, "searchHelper");
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        // num is ignored: the batch is page.max.size, or the cap when that is smaller.
        params.put("num", new String[] { "5" });
        final Settings settings = new Settings();
        configure(settings);
        new ExportSearchHandler().handle(new StubRequest(PATH, params), new CapturingResponse());
        settings.maxSize = 30;
        new ExportSearchHandler().handle(new StubRequest(PATH, params), new CapturingResponse());
        assertEquals(Arrays.asList(100, 30), pageSizes);
    }

    @Test
    public void test_export_fieldsOutsideTheApiAllowListAreDropped() throws Exception {
        final Settings settings = new Settings();
        settings.fields = "title, secret ,url_link,title";
        configure(settings);
        final CapturingResponse csv = export("csv", doc("a"));
        assertEquals(csv.body(), BOM + row("title", "url_link"), lines(csv.body())[0]);
        assertFalse(csv.body(), csv.body().contains("hidden"));
        final CapturingResponse json = export("json", doc("a"));
        assertFalse(json.body(), json.body().contains("secret"));
        assertFalse(json.body(), json.body().contains("hidden"));
    }

    @Test
    public void test_export_noExportableFields_isRejected() throws Exception {
        final Settings settings = new Settings();
        settings.fields = "secret";
        configure(settings);
        final CapturingResponse res = export("csv", doc("a"));
        assertEquals(res.body(), 400, res.status);
        assertTrue(res.body().contains("no exportable fields are configured"), res.body());
    }

    @Test
    public void test_export_json_shapeAndEveryDocument() throws Exception {
        configure(new Settings());
        final Map<String, Object> second = doc("b");
        second.put("filetype", List.of("html", "text"));
        final CapturingResponse res = export("json", doc("a"), second, doc("c"));
        assertEquals(res.body(), "application/json; charset=UTF-8", res.contentType);
        assertEquals("attachment; filename=\"search_results.json\"", res.getHeader("Content-Disposition"));
        final JsonNode root = new JsonMapper().readTree(res.body());
        final JsonNode data = root.get("data");
        assertEquals(res.body(), 3, data.size());
        assertEquals("a", data.get(0).get("title").asString());
        assertEquals("https://example.com/b", data.get(1).get("url_link").asString());
        assertEquals("text", data.get(1).get("filetype").get(1).asString());
        assertEquals(12L, data.get(2).get("content_length").asLong());
        // A field the document does not carry is omitted, as /search omits it.
        assertFalse(res.body(), data.get(0).has("last_modified"));
        assertFalse(res.body(), res.body().contains("hidden"));
    }

    @Test
    public void test_export_json_noMatchIsAnEmptyArray() throws Exception {
        configure(new Settings());
        final CapturingResponse res = export("json");
        assertEquals(res.body(), 0, new JsonMapper().readTree(res.body()).get("data").size());
    }

    @Test
    public void test_export_failureBeforeTheFirstByte_isAnEnvelope() throws Exception {
        configure(new Settings());
        ComponentUtil.register(new QueryFieldConfig() {
            @Override
            public boolean isApiResponseField(final String field) {
                return true;
            }
        }, "queryFieldConfig");
        ComponentUtil.register(new SearchHelper() {
            @Override
            public long scrollSearch(final SearchRequestParams params, final BooleanFunction<Map<String, Object>> cursor,
                    final OptionalThing<FessUserBean> userBean) {
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY),
                        "Invalid query");
            }
        }, "searchHelper");
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        final CapturingResponse res = new CapturingResponse();
        new ExportSearchHandler().handle(new StubRequest(PATH, params), res);
        assertEquals(res.body(), 400, res.status);
        assertTrue(res.body(), res.body().contains("\"code\":\"invalid_request\""));
        assertNull(res.getHeader("Content-Disposition"));
        assertFalse(res.body(), res.contentType != null && res.contentType.startsWith("text/csv"));
    }

    @Test
    public void test_export_failureAfterTheFirstByte_leavesTheFileWithoutAnEnvelope() throws Exception {
        configure(new Settings());
        ComponentUtil.register(new QueryFieldConfig() {
            @Override
            public boolean isApiResponseField(final String field) {
                return true;
            }
        }, "queryFieldConfig");
        ComponentUtil.register(new SearchHelper() {
            @Override
            public long scrollSearch(final SearchRequestParams params, final BooleanFunction<Map<String, Object>> cursor,
                    final OptionalThing<FessUserBean> userBean) {
                cursor.apply(doc("a"));
                throw new IllegalStateException("scroll broke");
            }
        }, "searchHelper");
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        final CapturingResponse res = new CapturingResponse();
        new ExportSearchHandler().handle(new StubRequest(PATH, params), res);
        final String body = res.body();
        assertEquals(body, "text/csv; charset=UTF-8", res.contentType);
        assertFalse(body, body.contains("internal_error"));
        assertFalse(body, body.contains("\"response\""));
    }

    @Test
    public void test_export_invalidQueryAfterTheFirstByte_leavesTheFileWithoutAnEnvelope() throws Exception {
        configure(new Settings());
        ComponentUtil.register(new QueryFieldConfig() {
            @Override
            public boolean isApiResponseField(final String field) {
                return true;
            }
        }, "queryFieldConfig");
        ComponentUtil.register(new SearchHelper() {
            @Override
            public long scrollSearch(final SearchRequestParams params, final BooleanFunction<Map<String, Object>> cursor,
                    final OptionalThing<FessUserBean> userBean) {
                // A later scroll page failing is reported as an invalid query too.
                cursor.apply(doc("a"));
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryParseError(UserMessages.GLOBAL_PROPERTY_KEY),
                        "Invalid query");
            }
        }, "searchHelper");
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        final CapturingResponse res = new CapturingResponse();
        new ExportSearchHandler().handle(new StubRequest(PATH, params), res);
        final String body = res.body();
        assertEquals(body, 200, res.status);
        assertEquals(body, "text/csv; charset=UTF-8", res.contentType);
        assertFalse(body, body.contains("invalid_request"));
    }

    @Test
    public void test_export_rateLimitExceeded_is429() throws Exception {
        final Settings settings = new Settings();
        settings.rateLimit = 2;
        configure(settings);
        final String key = "u:alice";
        final LoginRateLimiter limiter = new LoginRateLimiter();
        ComponentUtil.register(limiter, "loginRateLimiter");
        ComponentUtil.register(limiter, LoginRateLimiter.class.getCanonicalName());
        registerSearch(List.of(doc("a")), new ArrayList<>());
        final ExportSearchHandler handler = new ExportSearchHandler() {
            @Override
            protected String getRateLimitKey(final HttpServletRequest req) {
                return key;
            }
        };
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        for (int i = 0; i < 2; i++) {
            final CapturingResponse ok = new CapturingResponse();
            handler.handle(new StubRequest(PATH, params), ok);
            assertEquals(ok.body(), "text/csv; charset=UTF-8", ok.contentType);
        }
        final CapturingResponse res = new CapturingResponse();
        handler.handle(new StubRequest(PATH, params), res);
        assertEquals(res.body(), 429, res.status);
        assertTrue(res.body(), res.body().contains("\"code\":\"rate_limited\""));
        assertTrue(res.body(), res.body().contains("too many export requests"));
        assertEquals("60", res.getHeader("Retry-After"));
        assertNull(res.getHeader("Content-Disposition"));
    }

    @Test
    public void test_export_rateLimitIsSeparateFromChat() throws Exception {
        final Settings settings = new Settings();
        settings.rateLimit = 1;
        configure(settings);
        final String key = "u:alice";
        final LoginRateLimiter limiter = new LoginRateLimiter();
        for (int i = 0; i < 30; i++) {
            limiter.allow(LoginRateLimiter.Scope.CHAT, key, 30, 60);
        }
        ComponentUtil.register(limiter, "loginRateLimiter");
        ComponentUtil.register(limiter, LoginRateLimiter.class.getCanonicalName());
        registerSearch(List.of(doc("a")), new ArrayList<>());
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        final CapturingResponse res = new CapturingResponse();
        new ExportSearchHandler() {
            @Override
            protected String getRateLimitKey(final HttpServletRequest req) {
                return key;
            }
        }.handle(new StubRequest(PATH, params), res);
        assertEquals(res.body(), "text/csv; charset=UTF-8", res.contentType);
    }

    @Test
    public void test_export_rateLimitZeroIsUnlimited() throws Exception {
        final Settings settings = new Settings();
        settings.rateLimit = 0;
        configure(settings);
        // No limiter is registered and the key seam is not pinned: neither may be reached.
        for (int i = 0; i < 3; i++) {
            assertEquals("text/csv; charset=UTF-8", export("csv", doc("a")).contentType);
        }
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
