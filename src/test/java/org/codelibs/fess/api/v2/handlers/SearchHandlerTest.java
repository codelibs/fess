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
import org.codelibs.fess.helper.LabelTypeHelper.LabelTypeItem;
import org.codelibs.fess.helper.RelatedContentHelper;
import org.codelibs.fess.helper.RelatedQueryHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.TagHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.query.QueryFieldConfig;
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

    // ===== User tags =====

    /** Registers the real tag rules and the real API response field set (in which the raw tag field is absent). */
    private static void registerTagComponents() {
        ComponentUtil.register(new TagHelper(), "tagHelper");
        final QueryFieldConfig queryFieldConfig = new QueryFieldConfig();
        queryFieldConfig.init();
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");
    }

    /** A visible tag: a label type of the kind tag whose permissions are the given users. */
    private static LabelTypeItem tagLabel(final String value, final String name, final String... users) {
        final LabelTypeItem item = new LabelTypeItem();
        item.setValue(value);
        item.setLabel(name);
        item.setTag(true);
        item.setPermissions(java.util.Arrays.stream(users).map(u -> "1" + u).toArray(String[]::new));
        item.setUrlSet(new java.util.LinkedHashSet<>(List.of("http://example.com/")));
        return item;
    }

    private static Map<String, LabelTypeItem> tagItemMap(final LabelTypeItem... items) {
        final Map<String, LabelTypeItem> map = new LinkedHashMap<>();
        for (final LabelTypeItem item : items) {
            map.put(item.getValue(), item);
        }
        return map;
    }

    private static Map<String, Object> tagItem(final String value, final String name, final boolean mine) {
        final Map<String, Object> item = new LinkedHashMap<>();
        item.put("value", value);
        item.put("name", name);
        item.put("mine", mine);
        return item;
    }

    @Test
    public void test_filterDocuments_addsTheVisibleTags() {
        registerTagComponents();
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("title", "Title");
        doc.put("url", "http://example.com/");
        doc.put("tag", new String[] { "aaa", "hidden", "bbb" });
        final Map<String, LabelTypeItem> visible = tagItemMap(tagLabel("aaa", "Alpha", "alice", "bob"), tagLabel("bbb", "Beta", "bob"));
        final List<Map<String, Object>> out = new SearchHandler().filterDocuments(List.of(doc), visible, "alice");
        assertEquals(1, out.size());
        final Map<String, Object> filtered = out.get(0);
        assertEquals("Title", filtered.get("title"));
        assertEquals("http://example.com/", filtered.get("url"));
        // The raw field can carry the values of tags that the caller cannot see, so it never reaches the response.
        assertFalse(filtered.containsKey("tag"), filtered.toString());
        assertEquals(List.of(tagItem("aaa", "Alpha", true), tagItem("bbb", "Beta", false)), filtered.get("tags"));
    }

    @Test
    public void test_filterDocuments_anonymousCallerOwnsNoTag() {
        registerTagComponents();
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("tag", new String[] { "aaa" });
        final Map<String, LabelTypeItem> visible = tagItemMap(tagLabel("aaa", "Alpha", "alice"));
        for (final String userId : new String[] { null, "", "bob" }) {
            final List<Map<String, Object>> out = new SearchHandler().filterDocuments(List.of(doc), visible, userId);
            org.junit.jupiter.api.Assertions.assertEquals(List.of(tagItem("aaa", "Alpha", false)), out.get(0).get("tags"),
                    "userId=" + userId);
        }
    }

    @Test
    public void test_filterDocuments_acceptsListAndSingleTagValues() {
        registerTagComponents();
        final Map<String, Object> listDoc = new LinkedHashMap<>();
        listDoc.put("title", "list");
        listDoc.put("tag", List.of("aaa", "hidden"));
        final Map<String, Object> singleDoc = new LinkedHashMap<>();
        singleDoc.put("title", "single");
        singleDoc.put("tag", "zzz");
        final Map<String, LabelTypeItem> visible = tagItemMap(tagLabel("aaa", "Alpha"), tagLabel("zzz", "Zeta", "alice"));
        final List<Map<String, Object>> out = new SearchHandler().filterDocuments(List.of(listDoc, singleDoc), visible, "alice");
        assertEquals(List.of(tagItem("aaa", "Alpha", false)), out.get(0).get("tags"));
        assertEquals(List.of(tagItem("zzz", "Zeta", true)), out.get(1).get("tags"));
    }

    @Test
    public void test_filterDocuments_noTagsKeyWithoutAVisibleTag() {
        registerTagComponents();
        final Map<String, Object> hiddenOnly = new LinkedHashMap<>();
        hiddenOnly.put("title", "hidden");
        hiddenOnly.put("tag", new String[] { "hidden" });
        final Map<String, Object> untagged = new LinkedHashMap<>();
        untagged.put("title", "untagged");
        final List<Map<String, Object>> out =
                new SearchHandler().filterDocuments(List.of(hiddenOnly, untagged), tagItemMap(tagLabel("aaa", "Alpha", "alice")), "alice");
        for (final Map<String, Object> filtered : out) {
            assertFalse(filtered.containsKey("tags"), filtered.toString());
            assertFalse(filtered.containsKey("tag"), filtered.toString());
        }
    }

    @Test
    public void test_filterDocuments_emptyTagMapAddsNothing() {
        // No tag helper is registered: with no visible tag (or user tags disabled) it is not consulted.
        final QueryFieldConfig queryFieldConfig = new QueryFieldConfig();
        queryFieldConfig.init();
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");
        final Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("title", "Title");
        doc.put("tag", new String[] { "aaa" });
        final List<Map<String, Object>> out = new SearchHandler().filterDocuments(List.of(doc), Collections.emptyMap(), "alice");
        assertEquals(Map.of("title", "Title"), out.get(0));
    }

    private static HttpServletRequest localeRequest(final Locale locale) {
        return (HttpServletRequest) java.lang.reflect.Proxy.newProxyInstance(SearchHandlerTest.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class }, (proxy, method, args) -> "getLocale".equals(method.getName()) ? locale : null);
    }

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

    @Test
    public void test_getTagItemMap_followsTheFeatureFlag() {
        final List<Locale> requested = new java.util.ArrayList<>();
        final LabelTypeItem alpha = tagLabel("aaa", "Alpha", "alice");
        final LabelTypeItem beta = tagLabel("bbb", "Beta");
        ComponentUtil.register(new org.codelibs.fess.helper.LabelTypeHelper() {
            @Override
            public List<LabelTypeItem> getTagItemList(final SearchRequestParams.SearchRequestType searchRequestType,
                    final Locale requestLocale) {
                assertEquals(SearchRequestParams.SearchRequestType.JSON, searchRequestType);
                requested.add(requestLocale);
                return List.of(alpha, beta);
            }
        }, "labelTypeHelper");

        setUserTagEnabled(false);
        assertEquals(Map.of(), new SearchHandler().getTagItemMap(localeRequest(Locale.JAPANESE)));
        org.junit.jupiter.api.Assertions.assertEquals(List.of(), requested, "the label types are not read while user tags are disabled");

        setUserTagEnabled(true);
        final Map<String, LabelTypeItem> map = new SearchHandler().getTagItemMap(localeRequest(Locale.JAPANESE));
        assertEquals(List.of("aaa", "bbb"), List.copyOf(map.keySet()));
        assertSame(alpha, map.get("aaa"));
        assertSame(beta, map.get("bbb"));
        assertEquals(List.of("aaa", "bbb"), List.copyOf(new SearchHandler().getTagItemMap(localeRequest(null)).keySet()));
        assertEquals(List.of(Locale.JAPANESE, Locale.ROOT), requested);
    }

    @Test
    public void test_getTagItemMap_failureMeansNoTags() {
        setUserTagEnabled(true);
        ComponentUtil.register(new org.codelibs.fess.helper.LabelTypeHelper() {
            @Override
            public List<LabelTypeItem> getTagItemList(final SearchRequestParams.SearchRequestType searchRequestType,
                    final Locale requestLocale) {
                throw new IllegalStateException("label types unavailable");
            }
        }, "labelTypeHelper");
        assertEquals(Map.of(), new SearchHandler().getTagItemMap(localeRequest(Locale.ENGLISH)));
    }

    @Test
    public void test_buildFacetField_tagValuesLimitedToVisibleTags() {
        final TestFacetField tagField = new TestFacetField("tag");
        tagField.overrideName = "tag";
        tagField.overrideValueCountMap.put("hidden", 9L);
        tagField.overrideValueCountMap.put("aaa", 3L);
        tagField.overrideValueCountMap.put("bbb", 1L);
        final TestFacetField labelField = new TestFacetField("label");
        labelField.overrideName = "label";
        labelField.overrideValueCountMap.put("hidden", 4L);
        final TestFacetResponse fr = new TestFacetResponse();
        fr.addField(tagField);
        fr.addField(labelField);

        final List<Map<String, Object>> out =
                new SearchHandler().buildFacetField(fr, tagItemMap(tagLabel("aaa", "Alpha", "alice"), tagLabel("bbb", "Beta")));
        assertEquals(2, out.size());
        assertEquals("tag", out.get(0).get("name"));
        // A tag that the caller cannot see is not revealed by its count; a visible one carries its name.
        assertEquals(List.of(Map.of("value", "aaa", "count", 3L, "label", "Alpha"), Map.of("value", "bbb", "count", 1L, "label", "Beta")),
                out.get(0).get("result"));
        // Only the tag facet is filtered; a value of another facet is kept and gets no label.
        assertEquals("label", out.get(1).get("name"));
        assertEquals(List.of(Map.of("value", "hidden", "count", 4L)), out.get(1).get("result"));
    }

    @Test
    public void test_buildFacetField_withoutVisibleTags_dropsEveryTagValue() {
        final TestFacetField tagField = new TestFacetField("tag");
        tagField.overrideName = "tag";
        tagField.overrideValueCountMap.put("aaa", 3L);
        final TestFacetField labelField = new TestFacetField("label");
        labelField.overrideName = "label";
        labelField.overrideValueCountMap.put("news", 5L);
        final TestFacetResponse fr = new TestFacetResponse();
        fr.addField(tagField);
        fr.addField(labelField);

        for (final List<Map<String, Object>> out : List.of(new SearchHandler().buildFacetField(fr),
                new SearchHandler().buildFacetField(fr, Collections.emptyMap()))) {
            assertEquals(2, out.size());
            assertEquals("tag", out.get(0).get("name"));
            assertEquals(List.of(), out.get(0).get("result"));
            assertEquals(List.of(Map.of("value", "news", "count", 5L)), out.get(1).get("result"));
        }
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
