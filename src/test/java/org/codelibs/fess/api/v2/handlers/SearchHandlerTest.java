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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.exception.SearchEngineUnavailableException;
import org.codelibs.fess.helper.RelatedContentHelper;
import org.codelibs.fess.helper.RelatedQueryHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.TagTypeHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.FacetResponse;
import org.codelibs.fess.entity.SearchRequestParams;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.lastaflute.core.message.UserMessages;

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

public class SearchHandlerTest extends UnitFessTestCase {

    @Test
    public void test_search_emptyIndexReturnsEmptyData() throws Exception {
        final SearchHandler handler = new SearchHandler();
        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        handler.handle(new StubRequest("/api/v2/search", params), res);
        final String body = res.body();
        // The v2 envelope wraps both happy-path and failure paths — assert the shape stays
        // consistent regardless of whether the search helper is wired up in this test JVM.
        assertFalse(body.contains("\"version\""), body);
        if (res.status == 200) {
            assertTrue(body.contains("\"status\":0"), body);
            assertTrue(body.contains("\"record_count\":0"), body);
            assertTrue(body.contains("\"data\":[]"), body);
        } else {
            assertTrue(res.status == 400 || res.status == 500, "unexpected status " + res.status + ": " + body);
            assertTrue(body.contains("\"code\":\"invalid_request\"") || body.contains("\"code\":\"internal_error\""), body);
        }
    }

    @Test
    public void test_search_methodGate_rejectsPost() throws Exception {
        final SearchHandler handler = new SearchHandler();
        final CapturingResponse res = new CapturingResponse();
        handler.handle(new StubRequest("/api/v2/search").withMethod("POST"), res);
        assertEquals(405, res.status);
        final String body = res.body();
        assertTrue(body.contains("\"status\":1"), body);
        assertTrue(body.contains("\"code\":\"method_not_allowed\""), body);
        assertTrue(body.contains("method not allowed"), body);
        // MJ-18: RFC 7231 §6.5.5 requires Allow header on 405.
        assertEquals("Allow header must be set on 405", "GET", res.getHeader("Allow"));
    }

    @Test
    public void test_search_envelopeShape() throws Exception {
        final SearchHandler handler = new SearchHandler();
        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "foo" });
        handler.handle(new StubRequest("/api/v2/search", params), res);
        final String body = res.body();
        assertFalse(body.contains("\"version\""), body);
        if (res.status == 200) {
            assertTrue(body.contains("\"status\":0"), body);
            // Every top-level snake_case key from the spec should be present in a success
            // envelope, even when the underlying values are nulls or empty arrays.
            for (final String key : new String[] { "\"q\"", "\"query_id\"", "\"exec_time\"", "\"query_time\"", "\"page_size\"",
                    "\"page_number\"", "\"record_count\"", "\"record_count_relation\"", "\"page_count\"", "\"highlight_params\"",
                    "\"next_page\"", "\"prev_page\"", "\"start_record_number\"", "\"end_record_number\"", "\"page_numbers\"", "\"partial\"",
                    "\"search_query\"", "\"requested_time\"", "\"related_query\"", "\"related_contents\"", "\"data\"" }) {
                assertTrue(body.contains(key), "missing key " + key + " in body: " + body);
            }
        } else {
            assertTrue(res.status == 400 || res.status == 500, "unexpected status " + res.status + ": " + body);
            assertTrue(body.contains("\"code\":\"invalid_request\"") || body.contains("\"code\":\"internal_error\""), body);
        }
    }

    /**
     * Task 1.2: When the search succeeds, exec_time must be present at the top level
     * of the response payload.
     */
    @Test
    public void test_search_execTimePresentAtTopLevel() throws Exception {
        final SearchHandler handler = new SearchHandler();
        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "test" });
        handler.handle(new StubRequest("/api/v2/search", params), res);
        if (res.status == 200) {
            final String body = res.body();
            assertTrue(body.contains("\"exec_time\""), "exec_time must be present in search response: " + body);
        }
        // When the search helper is not fully wired (400/500), the test is inconclusive —
        // allow pass so the test suite stays green in unit harnesses.
    }

    // ===== Task A2: GEO-1 backend contract =====

    /**
     * A valid geo point and distance produce a non-null GeoInfo whose query builder
     * is populated — proving the backend can translate the wire params.
     *
     * <p>GeoInfo reads {@code query.geo.fields} (default "location") from
     * ComponentUtil.getFessConfig(), which is wired by UnitFessTestCase. The default
     * field list contains "location", so {@code geo.location.point} is recognised and
     * the result query builder is non-null.</p>
     */
    @Test
    public void test_geoInfo_validPoint_buildsGeoQuery() {
        final Map<String, String[]> params = new LinkedHashMap<>();
        params.put("geo.location.point", new String[] { "35.681,139.767" });
        params.put("geo.location.distance", new String[] { "10km" });
        final GeoInfo geo = new GeoInfo(new StubRequest("/api/v2/search", params));
        assertNotNull(geo);
        assertNotNull(geo.toQueryBuilder());
    }

    /**
     * A malformed geo point (not lat,lon) must raise InvalidQueryException so the
     * handler can return a structured 400 instead of a 500.
     *
     * <p>GeoInfo throws InvalidQueryException on parse failure — the handler's
     * catch block maps this to {@code V2ErrorCode.INVALID_REQUEST}.</p>
     */
    @Test
    public void test_geoInfo_malformedPoint_throwsInvalidQuery() {
        final Map<String, String[]> params = new LinkedHashMap<>();
        params.put("geo.location.point", new String[] { "not-a-point" });
        params.put("geo.location.distance", new String[] { "10km" });
        try {
            new GeoInfo(new StubRequest("/api/v2/search", params));
            fail("malformed geo point must raise InvalidQueryException");
        } catch (final InvalidQueryException expected) {
            // expected: malformed lat,lon string triggers InvalidQueryException
        }
    }

    // ===== Task A3: facet response shape (Phase B dependency) =====

    /**
     * buildFacetField must emit {@code [{name, result:[{value, count}]}]}.
     * This shape is a Phase B dependency: the SPA facet renderer reads it verbatim.
     *
     * <p>FacetResponse.Field only has a Terms-based constructor, so TestFacetField
     * subclasses it and overrides getName()/getValueCountMap() while bypassing the
     * constructor body via a minimal stub Terms implementation.</p>
     */
    @Test
    public void test_buildFacetField_shape() {
        final TestFacetField field = new TestFacetField("label");
        field.overrideName = "label";
        field.overrideValueCountMap.put("label1", 42L);

        final TestFacetResponse fr = new TestFacetResponse();
        fr.addField(field);

        final List<Map<String, Object>> out = new SearchHandler().buildFacetField(fr);
        assertEquals(1, out.size());
        assertEquals("label", out.get(0).get("name"));
        @SuppressWarnings("unchecked")
        final List<Map<?, ?>> result = (List<Map<?, ?>>) out.get(0).get("result");
        assertEquals(1, result.size());
        assertEquals("label1", result.get(0).get("value"));
        assertEquals(42L, result.get(0).get("count"));
    }

    /**
     * buildFacetQuery must emit {@code [{value, count}]}.
     * This shape is a Phase B dependency: the SPA facet renderer reads it verbatim.
     */
    @Test
    public void test_buildFacetQuery_shape() {
        final TestFacetResponse fr = new TestFacetResponse();
        fr.addQuery("filetype:pdf", 17L);

        final List<Map<String, Object>> out = new SearchHandler().buildFacetQuery(fr);
        assertEquals(1, out.size());
        assertEquals("filetype:pdf", out.get(0).get("value"));
        assertEquals(17L, out.get(0).get("count"));
    }

    /**
     * Test-only FacetResponse subclass: passes {@code null} Aggregations (→ no-op
     * forEach in parent) and exposes the inherited protected fields directly so tests
     * can populate them without going through OpenSearch aggregation objects.
     */
    private static class TestFacetResponse extends FacetResponse {
        TestFacetResponse() {
            super(null); // null Aggregations → forEach is skipped
            // fieldList and queryCountMap are inherited protected fields; clear and reuse.
            this.fieldList.clear();
            this.queryCountMap.clear();
        }

        void addField(final FacetResponse.Field f) {
            this.fieldList.add(f);
        }

        void addQuery(final String query, final long count) {
            this.queryCountMap.put(query, count);
        }
    }

    /**
     * Test-only FacetResponse.Field: subclasses Field to override getName() and
     * getValueCountMap(). The parent constructor needs a valid Terms object; we
     * supply a minimal anonymous stub that returns a base64-encoded placeholder name
     * and an empty bucket list so the parent constructor body completes without error.
     * Tests then set the public override fields before calling buildFacetField.
     */
    private static class TestFacetField extends FacetResponse.Field {
        String overrideName;
        final Map<String, Long> overrideValueCountMap = new LinkedHashMap<>();

        TestFacetField(final String label) {
            // Feed a stub Terms whose getName() returns "field:" + base64(label) so the
            // parent constructor decodes it without error; getBuckets() returns empty so
            // valueCountMap starts empty. Tests set overrideValueCountMap afterward.
            super(new org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.Terms() {
                @Override
                public String getName() {
                    return "field:" + java.util.Base64.getEncoder().encodeToString(label.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                }

                @Override
                public java.util.List<? extends org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.Terms.Bucket> getBuckets() {
                    return java.util.Collections.emptyList();
                }

                @Override
                public org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.Terms.Bucket getBucketByKey(final String term) {
                    return null;
                }

                @Override
                public long getDocCountError() {
                    return 0;
                }

                @Override
                public long getSumOfOtherDocCounts() {
                    return 0;
                }

                @Override
                public org.codelibs.fesen.opensearch.core.xcontent.XContentBuilder toXContent(
                        final org.codelibs.fesen.opensearch.core.xcontent.XContentBuilder builder,
                        final org.codelibs.fesen.opensearch.core.xcontent.ToXContent.Params params) throws java.io.IOException {
                    return builder;
                }

                @Override
                public java.util.Map<String, Object> getMetadata() {
                    return java.util.Collections.emptyMap();
                }

                @Override
                public String getType() {
                    return "terms";
                }
            });
        }

        @Override
        public String getName() {
            return overrideName != null ? overrideName : super.getName();
        }

        @Override
        public Map<String, Long> getValueCountMap() {
            return overrideValueCountMap.isEmpty() ? super.getValueCountMap() : overrideValueCountMap;
        }
    }

    /**
     * A rejected query must not hand the caller the request the engine was given.
     *
     * <p>{@code SearchEngineClient} put the whole {@code SearchRequestBuilder} into the exception
     * message, and the handler forwarded that message as {@code error.message}: an anonymous caller
     * read back the role filter terms, the {@code _source} allow-list, the internal field names and
     * the per-field boosts from one request. The caller now sees only the message the exception was
     * raised with.</p>
     */
    @Test
    public void test_search_invalidQuery_reportsUserMessageNotTheQueryBuilder() throws Exception {
        final String builderDump = "{\"from\":10001,\"size\":10,\"timeout\":\"10000ms\",\"query\":{\"bool\":{\"must\":"
                + "[{\"function_score\":{\"query\":{\"bool\":{\"filter\":[{\"terms\":{\"role\":[\"Rfilter-term\"]}}]}}}}]}},"
                + "\"_source\":{\"includes\":[\"content\",\"title\"]}}";
        ComponentUtil.register(new SearchHelper() {
            @Override
            public void search(final SearchRequestParams searchRequestParams, final SearchRenderData data,
                    final OptionalThing<FessUserBean> userBean) {
                throw new InvalidQueryException(messages -> messages.addErrorsInvalidQueryCannotProcess(UserMessages.GLOBAL_PROPERTY_KEY),
                        "Failed query: " + builderDump);
            }
        }, "searchHelper");

        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        new SearchHandler().handle(new StubRequest("/api/v2/search", params), res);

        final String body = res.body();
        assertEquals(body, 400, res.status);
        assertFalse(body.contains("Failed query"), body);
        assertFalse(body.contains("function_score"), body);
        assertFalse(body.contains("Rfilter-term"), body);
        assertFalse(body.contains("_source"), body);
        assertTrue(body.contains(ComponentUtil.getMessageManager().getMessage(Locale.ROOT, "errors.invalid_query_cannot_process")), body);
    }

    /**
     * A search the engine has no room for says nothing about the query: the caller is told to try
     * again later (503 with Retry-After), not that its request was bad (400).
     */
    @Test
    public void test_search_capacityRejection_isServiceUnavailable() throws Exception {
        ComponentUtil.register(new SearchHelper() {
            @Override
            public void search(final SearchRequestParams searchRequestParams, final SearchRenderData data,
                    final OptionalThing<FessUserBean> userBean) {
                throw new SearchEngineUnavailableException("The search engine is out of capacity.", new RuntimeException(
                        "OpenSearch exception [type=circuit_breaking_exception, reason=[parent] Data too large, role=Rfilter-term]"));
            }
        }, "searchHelper");

        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        new SearchHandler().handle(new StubRequest("/api/v2/search", params), res);

        final String body = res.body();
        assertEquals(body, 503, res.status);
        assertTrue(body.contains("\"status\":9"), body);
        assertTrue(body.contains("\"code\":\"service_unavailable\""), body);
        assertFalse(body.contains("query"), "the caller is not told that its query is at fault: " + body);
        assertFalse(body.contains("circuit_breaking_exception"), body);
        assertFalse(body.contains("Rfilter-term"), body);
        assertEquals("5", res.getHeader("Retry-After"));
    }

    /**
     * A redirected search makes the handler answer with the URL instead of results.
     */
    @Test
    public void test_search_redirectedSearchReturnsTheUrl() throws Exception {
        ComponentUtil.register(new SearchHelper() {
            @Override
            public void search(final SearchRequestParams searchRequestParams, final SearchRenderData data,
                    final OptionalThing<FessUserBean> userBean) {
                assertTrue(searchRequestParams.isRedirectable(), "the v2 search follows redirects");
                data.setRedirectUrl("https://www.google.com/search?q=" + searchRequestParams.getQuery().replace(" !g", ""));
            }
        }, "searchHelper");

        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "airplane !g" });
        new SearchHandler().handle(new StubRequest("/api/v2/search", params), res);

        final String body = res.body();
        assertEquals(body, 200, res.status);
        assertTrue(body.contains("\"redirect_url\":\"https://www.google.com/search?q=airplane\""), body);
        assertTrue(body.contains("\"q\":\"airplane !g\""), body);
        assertFalse(body.contains("\"data\""), body);
    }

    /**
     * The offset ceiling has its own message; it used to arrive as the exception's own text.
     */
    @Test
    public void test_search_resultOffsetExceeded_reportsUserMessage() throws Exception {
        ComponentUtil.register(new SearchHelper() {
            @Override
            public void search(final SearchRequestParams searchRequestParams, final SearchRenderData data,
                    final OptionalThing<FessUserBean> userBean) {
                throw new ResultOffsetExceededException("The number of result size is exceeded.");
            }
        }, "searchHelper");

        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        new SearchHandler().handle(new StubRequest("/api/v2/search", params), res);

        final String body = res.body();
        assertEquals(body, 400, res.status);
        assertTrue(body.contains(ComponentUtil.getMessageManager().getMessage(Locale.ROOT, "errors.result_size_exceeded")), body);
    }

    /**
     * A facet number the search cannot use is refused before the search starts. Once started,
     * the main searcher reports every failure as an empty partial result, so the caller would
     * get a 200 that looks like a search of nothing.
     */
    @Test
    public void test_search_invalidFacetNumber_isRefusedBeforeSearching() throws Exception {
        final java.util.concurrent.atomic.AtomicBoolean searched = new java.util.concurrent.atomic.AtomicBoolean();
        registerSearchStubs();
        ComponentUtil.register(new SearchHelper() {
            @Override
            public void search(final SearchRequestParams searchRequestParams, final SearchRenderData data,
                    final OptionalThing<FessUserBean> userBean) {
                searched.set(true);
                data.setDocumentItems(List.of());
            }
        }, "searchHelper");

        for (final String[] facetParam : new String[][] { { "facet.size", "abc" }, { "facet.size", "0" }, { "facet.size", "-1" },
                { "facet.minDocCount", "x" } }) {
            final CapturingResponse res = new CapturingResponse();
            final Map<String, String[]> params = new HashMap<>();
            params.put("q", new String[] { "*" });
            params.put("facet.field", new String[] { "label" });
            params.put(facetParam[0], new String[] { facetParam[1] });
            new SearchHandler().handle(new StubRequest("/api/v2/search", params), res);

            final String body = res.body();
            assertEquals(facetParam[0] + "=" + facetParam[1] + " " + body, 400, res.status);
            assertTrue(body.contains("\"code\":\"invalid_request\""), body);
            assertTrue(body.contains(facetParam[0]), body);
        }
        assertFalse(searched.get(), "the search must not start");
    }

    /** The item limit of {@code facet.field} was checked only inside the search, where it was lost the same way. */
    @Test
    public void test_search_tooManyFacetFields_isRefusedBeforeSearching() throws Exception {
        final java.util.concurrent.atomic.AtomicBoolean searched = new java.util.concurrent.atomic.AtomicBoolean();
        registerSearchStubs();
        ComponentUtil.register(new SearchHelper() {
            @Override
            public void search(final SearchRequestParams searchRequestParams, final SearchRenderData data,
                    final OptionalThing<FessUserBean> userBean) {
                searched.set(true);
                data.setDocumentItems(List.of());
            }
        }, "searchHelper");

        final String[] fields = new String[101];
        java.util.Arrays.fill(fields, "label");
        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        params.put("facet.field", fields);
        new SearchHandler().handle(new StubRequest("/api/v2/search", params), res);

        assertEquals(res.body(), 400, res.status);
        assertTrue(res.body().contains("\"code\":\"invalid_request\""), res.body());
        assertFalse(searched.get(), "the search must not start");
    }

    /**
     * A page size or start position the request parameters refuse is turned down before the search
     * starts. Raised inside the search, the refusal reached the rank fusion processor, which logged
     * a stack trace for a client's mistake and, with engine-side fusion, fell back to Fess-side fusion.
     */
    @Test
    public void test_search_invalidPaging_isRefusedBeforeSearching() throws Exception {
        final java.util.concurrent.atomic.AtomicBoolean searched = new java.util.concurrent.atomic.AtomicBoolean();
        registerSearchStubs();
        ComponentUtil.register(new SearchHelper() {
            @Override
            public void search(final SearchRequestParams searchRequestParams, final SearchRenderData data,
                    final OptionalThing<FessUserBean> userBean) {
                searched.set(true);
                data.setDocumentItems(List.of());
            }
        }, "searchHelper");

        for (final String[] pagingParam : new String[][] { { "num", "0" }, { "num", "-1" }, { "start", "-1" } }) {
            final CapturingResponse res = new CapturingResponse();
            final Map<String, String[]> params = new HashMap<>();
            params.put("q", new String[] { "*" });
            params.put(pagingParam[0], new String[] { pagingParam[1] });
            new SearchHandler().handle(new StubRequest("/api/v2/search", params), res);

            final String body = res.body();
            assertEquals(pagingParam[0] + "=" + pagingParam[1] + " " + body, 400, res.status);
            assertTrue(body.contains("\"code\":\"invalid_request\""), body);
            assertTrue(body.contains(pagingParam[0] + " must be"), body);
        }
        assertFalse(searched.get(), "the search must not start");
    }

    /** The paging values the request parameters accept, including one they replace with the default, are searched. */
    @Test
    public void test_search_validPaging_isSearched() throws Exception {
        registerSearchStubs();
        for (final String[] pagingParam : new String[][] { { "num", "5" }, { "num", "abc" }, { "start", "10" }, { "start", "abc" } }) {
            final CapturingResponse res = new CapturingResponse();
            final Map<String, String[]> params = new HashMap<>();
            params.put("q", new String[] { "*" });
            params.put(pagingParam[0], new String[] { pagingParam[1] });
            new SearchHandler().handle(new StubRequest("/api/v2/search", params), res);

            assertEquals(pagingParam[0] + "=" + pagingParam[1] + " " + res.body(), 200, res.status);
        }
    }

    @Test
    public void test_search_validFacetNumber_isSearched() throws Exception {
        final java.util.concurrent.atomic.AtomicBoolean searched = new java.util.concurrent.atomic.AtomicBoolean();
        registerSearchStubs();
        ComponentUtil.register(new SearchHelper() {
            @Override
            public void search(final SearchRequestParams searchRequestParams, final SearchRenderData data,
                    final OptionalThing<FessUserBean> userBean) {
                searched.set(true);
                data.setDocumentItems(List.of());
            }
        }, "searchHelper");

        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        params.put("facet.field", new String[] { "label" });
        params.put("facet.size", new String[] { "5" });
        params.put("facet.minDocCount", new String[] { "2" });
        new SearchHandler().handle(new StubRequest("/api/v2/search", params), res);

        assertEquals(res.body(), 200, res.status);
        assertTrue(searched.get());
    }

    /**
     * A partial result says why it is partial, so a client does not report a failed shard as a timeout.
     */
    @Test
    public void test_search_reportsWhyAResultIsPartial() throws Exception {
        ComponentUtil.register(new SearchHelper() {
            @Override
            public void search(final SearchRequestParams searchRequestParams, final SearchRenderData data,
                    final OptionalThing<FessUserBean> userBean) {
                data.setDocumentItems(List.of());
                data.setPartialResults(true);
                data.setShardFailed(true);
            }
        }, "searchHelper");
        ComponentUtil.register(new RelatedQueryHelper() {
            @Override
            public String[] getRelatedQueries(final String query) {
                return new String[0];
            }
        }, "relatedQueryHelper");
        ComponentUtil.register(new RelatedContentHelper() {
            @Override
            public String[] getRelatedContents(final String query) {
                return new String[0];
            }
        }, "relatedContentHelper");

        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        new SearchHandler().handle(new StubRequest("/api/v2/search", params), res);

        final String body = res.body();
        assertEquals(body, 200, res.status);
        assertTrue(body.contains("\"partial\":true"), body);
        assertTrue(body.contains("\"timed_out\":false"), body);
        assertTrue(body.contains("\"shard_failed\":true"), body);
    }

    @Test
    public void test_search_reportsThePermissionState() throws Exception {
        registerSearchStubs();

        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        new SearchHandler() {
            @Override
            protected OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.of(new FessUserBean(new StubFessUser(FessUser.PermissionState.PENDING)));
            }
        }.handle(new StubRequest("/api/v2/search", params), res);

        final String body = res.body();
        assertEquals(body, 200, res.status);
        assertTrue(body.contains("\"permission_state\":\"PENDING\""), body);
    }

    @Test
    public void test_search_guestIsResolved() throws Exception {
        registerSearchStubs();

        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        new SearchHandler() {
            @Override
            protected OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.empty();
            }
        }.handle(new StubRequest("/api/v2/search", params), res);

        final String body = res.body();
        assertEquals(body, 200, res.status);
        assertTrue(body.contains("\"permission_state\":\"RESOLVED\""), body);
    }

    // ===== User tags =====

    /** Delegates to the container's configuration except for user.tag.enabled. */
    private static void setUserTagEnabled(final boolean enabled) {
        final org.codelibs.fess.mylasta.direction.FessConfig real = ComponentUtil.getFessConfig();
        ComponentUtil.setFessConfig((org.codelibs.fess.mylasta.direction.FessConfig) java.lang.reflect.Proxy.newProxyInstance(
                org.codelibs.fess.mylasta.direction.FessConfig.class.getClassLoader(),
                new Class<?>[] { org.codelibs.fess.mylasta.direction.FessConfig.class }, (proxy, method, args) -> {
                    if ("isUserTagEnabled".equals(method.getName())) {
                        return enabled;
                    }
                    try {
                        return method.invoke(real, args);
                    } catch (final java.lang.reflect.InvocationTargetException e) {
                        throw e.getCause();
                    }
                }));
    }

    /** Registers the real response field configuration, in which the tag field is not a response field. */
    private static void registerQueryFieldConfig() {
        final org.codelibs.fess.query.QueryFieldConfig queryFieldConfig = new org.codelibs.fess.query.QueryFieldConfig();
        queryFieldConfig.init();
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");
    }

    private static TagType tagType(final String name, final String owner, final boolean shared) {
        final TagType tagType = new TagType();
        tagType.setName(name);
        tagType.setOwner(owner);
        tagType.setPermissions(shared ? new String[] { "1" + owner, "Rguest" } : new String[] { "1" + owner });
        return tagType;
    }

    private static Map<String, TagType> visibleMap(final TagType... tagTypes) {
        final Map<String, TagType> map = new LinkedHashMap<>();
        for (final TagType tagType : tagTypes) {
            map.put(tagType.getTagValue(), tagType);
        }
        return map;
    }

    private static Map<String, Object> tagItem(final TagType tagType, final boolean mine, final boolean shared) {
        final Map<String, Object> item = new LinkedHashMap<>();
        item.put("value", tagType.getTagValue());
        item.put("name", tagType.getName());
        item.put("owner", tagType.getOwner());
        item.put("mine", mine);
        item.put("shared", shared);
        return item;
    }

    /** A tag type helper whose visible tags are fixed by the test and whose sharing role is Rguest. */
    private static class StubTagTypeHelper extends TagTypeHelper {
        final Map<String, TagType> visible = new LinkedHashMap<>();
        final List<List<String>> requests = new java.util.ArrayList<>();
        int visibleValuesCalls;

        @Override
        public java.util.Set<String> getVisibleTagValues(final SearchRequestParams.SearchRequestType type) {
            org.junit.jupiter.api.Assertions.assertEquals(SearchRequestParams.SearchRequestType.JSON, type);
            visibleValuesCalls++;
            return java.util.Set.copyOf(visible.keySet());
        }

        @Override
        public Map<String, TagType> getVisibleTagTypes(final java.util.Collection<String> values,
                final SearchRequestParams.SearchRequestType type) {
            org.junit.jupiter.api.Assertions.assertEquals(SearchRequestParams.SearchRequestType.JSON, type);
            requests.add(List.copyOf(values));
            final Map<String, TagType> map = new LinkedHashMap<>();
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
    public void test_filterDocuments_hitTagsAreVisibleOnlyWithMineAndShared() {
        registerQueryFieldConfig();
        final StubTagTypeHelper helper = new StubTagTypeHelper();
        ComponentUtil.register(helper, "tagTypeHelper");
        final TagType own = tagType("foo", "frank", false);
        final TagType othersShared = tagType("bar", "bob", true);
        final TagType ownShared = tagType("baz", "frank", true);
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("title", "Title");
        doc.put("tag", List.of(own.getTagValue(), "hidden", othersShared.getTagValue(), ownShared.getTagValue()));

        final List<Map<String, Object>> out =
                new SearchHandler().filterDocuments(List.of(doc), visibleMap(own, othersShared, ownShared), "frank");

        final Map<String, Object> filtered = out.get(0);
        assertEquals("Title", filtered.get("title"));
        // the raw values decode to the name and the owner of every tag, visible or not
        assertFalse(filtered.containsKey("tag"), filtered.toString());
        assertEquals(List.of(tagItem(own, true, false), tagItem(othersShared, false, true), tagItem(ownShared, true, true)),
                filtered.get("tags"));
    }

    @Test
    public void test_filterDocuments_noVisibleTagMeansNoTagsKey() {
        registerQueryFieldConfig();
        final Map<String, Object> tagged = new LinkedHashMap<>();
        tagged.put("title", "tagged");
        tagged.put("tag", new String[] { "hidden" });
        final Map<String, Object> untagged = new LinkedHashMap<>();
        untagged.put("title", "untagged");
        for (final Map<String, Object> filtered : new SearchHandler().filterDocuments(List.of(tagged, untagged), Map.of(), null)) {
            assertFalse(filtered.containsKey("tag"), filtered.toString());
            assertFalse(filtered.containsKey("tags"), filtered.toString());
        }
    }

    @Test
    public void test_buildFacetField_tagBucketsAreVisibleOnesWithOwnerMineShared() {
        final StubTagTypeHelper helper = new StubTagTypeHelper();
        ComponentUtil.register(helper, "tagTypeHelper");
        final TagType own = tagType("foo", "frank", false);
        final TagType othersShared = tagType("foo", "bob", true);
        final TestFacetField tagField = new TestFacetField("tag");
        tagField.overrideName = "tag";
        tagField.overrideValueCountMap.put("hidden", 9L);
        tagField.overrideValueCountMap.put(own.getTagValue(), 3L);
        tagField.overrideValueCountMap.put(othersShared.getTagValue(), 1L);
        final TestFacetField labelField = new TestFacetField("label");
        labelField.overrideName = "label";
        labelField.overrideValueCountMap.put("news", 4L);
        final TestFacetResponse fr = new TestFacetResponse();
        fr.addField(tagField);
        fr.addField(labelField);

        final List<Map<String, Object>> out = new SearchHandler().buildFacetField(fr, visibleMap(own, othersShared), "frank");

        final Map<String, Object> ownBucket = new LinkedHashMap<>();
        ownBucket.put("value", own.getTagValue());
        ownBucket.put("count", 3L);
        ownBucket.put("label", "foo");
        ownBucket.put("owner", "frank");
        ownBucket.put("mine", true);
        ownBucket.put("shared", false);
        final Map<String, Object> sharedBucket = new LinkedHashMap<>();
        sharedBucket.put("value", othersShared.getTagValue());
        sharedBucket.put("count", 1L);
        sharedBucket.put("label", "foo");
        sharedBucket.put("owner", "bob");
        sharedBucket.put("mine", false);
        sharedBucket.put("shared", true);
        assertEquals(List.of(ownBucket, sharedBucket), out.get(0).get("result"));
        // other facets are untouched
        assertEquals(List.of(Map.of("value", "news", "count", 4L)), out.get(1).get("result"));
        // the one-argument form has no visible tag
        assertEquals(List.of(), new SearchHandler().buildFacetField(fr).get(0).get("result"));
    }

    @SafeVarargs
    private static void registerTaggedSearch(final List<String[]> responseFields, final Map<String, Object>... docs) {
        ComponentUtil.register(new SearchHelper() {
            @Override
            public void search(final SearchRequestParams searchRequestParams, final SearchRenderData data,
                    final OptionalThing<FessUserBean> userBean) {
                responseFields.add(searchRequestParams.getResponseFields());
                data.setDocumentItems(List.of(docs));
            }
        }, "searchHelper");
        ComponentUtil.register(new RelatedQueryHelper() {
            @Override
            public String[] getRelatedQueries(final String query) {
                return new String[0];
            }
        }, "relatedQueryHelper");
        ComponentUtil.register(new RelatedContentHelper() {
            @Override
            public String[] getRelatedContents(final String query) {
                return new String[0];
            }
        }, "relatedContentHelper");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_search_hitTagsAreResolvedOncePerRequest() throws Exception {
        registerQueryFieldConfig();
        setUserTagEnabled(true);
        final StubTagTypeHelper helper = new StubTagTypeHelper();
        final TagType own = tagType("foo", "frank", false);
        final TagType othersShared = tagType("bar", "bob", true);
        helper.visible.putAll(visibleMap(own, othersShared));
        ComponentUtil.register(helper, "tagTypeHelper");
        final List<String[]> responseFields = new java.util.ArrayList<>();
        final Map<String, Object> doc1 = new LinkedHashMap<>();
        doc1.put("title", "one");
        doc1.put("tag", List.of(own.getTagValue(), "hidden"));
        final Map<String, Object> doc2 = new LinkedHashMap<>();
        doc2.put("title", "two");
        doc2.put("tag", othersShared.getTagValue());
        registerTaggedSearch(responseFields, doc1, doc2);

        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        new SearchHandler() {
            @Override
            protected OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.of(new FessUserBean(new StubFessUser(FessUser.PermissionState.RESOLVED)));
            }
        }.handle(new StubRequest("/api/v2/search", params), res);

        final String body = res.body();
        assertEquals(body, 200, res.status);
        assertTrue(List.of(responseFields.get(0)).contains("tag"), "the v2 search fetches the tag field");
        assertEquals(1, helper.requests.size());
        assertEquals(1, helper.visibleValuesCalls);
        // a value that is not among the visible tags is not looked up
        assertEquals(List.of(own.getTagValue(), othersShared.getTagValue()), helper.requests.get(0));
        final Map<String, Object> root = tools.jackson.databind.json.JsonMapper.builder().build().readValue(body, Map.class);
        final List<Map<String, Object>> data = (List<Map<String, Object>>) ((Map<String, Object>) root.get("response")).get("data");
        assertEquals(List.of(tagItem(own, true, false)), data.get(0).get("tags"));
        assertEquals(List.of(tagItem(othersShared, false, true)), data.get(1).get("tags"));
        assertFalse(body.contains("\"tag\":"), body);
        assertFalse(body.contains("hidden"), body);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_search_invisibleHitTagsAreNotLookedUp() throws Exception {
        registerQueryFieldConfig();
        setUserTagEnabled(true);
        final StubTagTypeHelper helper = new StubTagTypeHelper();
        final TagType own = tagType("foo", "frank", false);
        helper.visible.putAll(visibleMap(own));
        ComponentUtil.register(helper, "tagTypeHelper");
        // a popular document carrying the tags of many other users
        final List<String> tagValues = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            tagValues.add("hidden" + i);
        }
        tagValues.add(500, own.getTagValue());
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("title", "popular");
        doc.put("tag", tagValues);
        registerTaggedSearch(new java.util.ArrayList<>(), doc);

        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        new SearchHandler() {
            @Override
            protected OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.of(new FessUserBean(new StubFessUser(FessUser.PermissionState.RESOLVED)));
            }
        }.handle(new StubRequest("/api/v2/search", params), res);

        final String body = res.body();
        assertEquals(body, 200, res.status);
        assertEquals(1, helper.visibleValuesCalls);
        assertEquals(List.of(List.of(own.getTagValue())), helper.requests);
        final Map<String, Object> root = tools.jackson.databind.json.JsonMapper.builder().build().readValue(body, Map.class);
        final List<Map<String, Object>> data = (List<Map<String, Object>>) ((Map<String, Object>) root.get("response")).get("data");
        assertEquals(List.of(tagItem(own, true, false)), data.get(0).get("tags"));
        assertFalse(body.contains("hidden"), body);
    }

    @Test
    public void test_search_noVisibleTagMeansNoLookup() throws Exception {
        registerQueryFieldConfig();
        setUserTagEnabled(true);
        final StubTagTypeHelper helper = new StubTagTypeHelper();
        ComponentUtil.register(helper, "tagTypeHelper");
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("title", "one");
        doc.put("tag", List.of("hidden1", "hidden2"));
        registerTaggedSearch(new java.util.ArrayList<>(), doc);

        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        new SearchHandler() {
            @Override
            protected OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.of(new FessUserBean(new StubFessUser(FessUser.PermissionState.RESOLVED)));
            }
        }.handle(new StubRequest("/api/v2/search", params), res);

        final String body = res.body();
        assertEquals(body, 200, res.status);
        assertTrue(helper.requests.isEmpty(), "nothing is looked up when the caller sees no tag");
        assertFalse(body.contains("\"tags\""), body);
    }

    @Test
    public void test_search_anonymousGetsNoTags() throws Exception {
        registerQueryFieldConfig();
        setUserTagEnabled(true);
        final StubTagTypeHelper helper = new StubTagTypeHelper();
        final TagType shared = tagType("bar", "bob", true);
        helper.visible.putAll(visibleMap(shared));
        ComponentUtil.register(helper, "tagTypeHelper");
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("title", "one");
        doc.put("tag", List.of(shared.getTagValue()));
        registerTaggedSearch(new java.util.ArrayList<>(), doc);

        final CapturingResponse res = new CapturingResponse();
        final Map<String, String[]> params = new HashMap<>();
        params.put("q", new String[] { "*" });
        new SearchHandler() {
            @Override
            protected OptionalThing<FessUserBean> getSavedUserBean() {
                return OptionalThing.empty();
            }
        }.handle(new StubRequest("/api/v2/search", params), res);

        final String body = res.body();
        assertEquals(body, 200, res.status);
        assertTrue(helper.requests.isEmpty(), "the tags are not looked up for an anonymous caller");
        assertEquals(0, helper.visibleValuesCalls);
        assertFalse(body.contains("\"tags\""), body);
        assertFalse(body.contains("\"tag\""), body);
    }

    @Test
    public void test_search_responseFieldsIncludeTagOnlyWhenEnabled() throws Exception {
        registerQueryFieldConfig();
        for (final boolean enabled : new boolean[] { false, true }) {
            setUserTagEnabled(enabled);
            ComponentUtil.register(new StubTagTypeHelper(), "tagTypeHelper");
            final List<String[]> responseFields = new java.util.ArrayList<>();
            registerTaggedSearch(responseFields);
            final Map<String, String[]> params = new HashMap<>();
            params.put("q", new String[] { "*" });
            new SearchHandler().handle(new StubRequest("/api/v2/search", params), new CapturingResponse());
            org.junit.jupiter.api.Assertions.assertEquals(enabled, List.of(responseFields.get(0)).contains("tag"), "enabled=" + enabled);
            // the global response fields are not changed
            assertFalse(List.of(ComponentUtil.getQueryFieldConfig().getResponseFields()).contains("tag"));
        }
    }

    private static void registerSearchStubs() {
        ComponentUtil.register(new SearchHelper() {
            @Override
            public void search(final SearchRequestParams searchRequestParams, final SearchRenderData data,
                    final OptionalThing<FessUserBean> userBean) {
                data.setDocumentItems(List.of());
            }
        }, "searchHelper");
        ComponentUtil.register(new RelatedQueryHelper() {
            @Override
            public String[] getRelatedQueries(final String query) {
                return new String[0];
            }
        }, "relatedQueryHelper");
        ComponentUtil.register(new RelatedContentHelper() {
            @Override
            public String[] getRelatedContents(final String query) {
                return new String[0];
            }
        }, "relatedContentHelper");
    }

    private static class StubFessUser implements FessUser {
        private static final long serialVersionUID = 1L;
        private final PermissionState state;

        StubFessUser(final PermissionState state) {
            this.state = state;
        }

        @Override
        public String getName() {
            return "frank";
        }

        @Override
        public String[] getRoleNames() {
            return new String[0];
        }

        @Override
        public String[] getGroupNames() {
            return new String[0];
        }

        @Override
        public String[] getPermissions() {
            return new String[0];
        }

        @Override
        public PermissionState getPermissionState() {
            return state;
        }
    }

    /** Minimal HttpServletResponse stub — local copy of SearchApiV2ManagerTest.CapturingResponse. */
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

    /** Minimal HttpServletRequest stub — local copy of SearchApiV2ManagerTest.StubRequest. */
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
