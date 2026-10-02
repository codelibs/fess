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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.codelibs.fesen.opensearch.action.search.SearchAction;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;

/**
 * Tests that the doc-id lookups of {@link SearchHelper} apply extra filter clauses next to the role
 * filter, and are unchanged without them.
 */
public class SearchHelperFilterQueryTest extends UnitFessTestCase {

    private final List<String> capturedQueries = new ArrayList<>();

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new QueryHelper() {
            @Override
            public void processSearchPreference(final SearchRequestBuilder searchRequestBuilder, final OptionalThing<FessUserBean> userBean,
                    final String query) {
                // no preference in this test
            }
        }, "queryHelper");
        ComponentUtil.register(new RoleQueryHelper() {
            @Override
            public Set<String> build(final SearchRequestType searchRequestType) {
                return Set.of("Rguest");
            }
        }, "roleQueryHelper");
        ComponentUtil.register(new SearchEngineClient() {
            @Override
            public OptionalEntity<Map<String, Object>> getDocument(final String index,
                    final SearchCondition<SearchRequestBuilder> condition) {
                capture(this, condition);
                return OptionalEntity.empty();
            }

            @Override
            public List<Map<String, Object>> getDocumentList(final String index, final SearchCondition<SearchRequestBuilder> condition) {
                capture(this, condition);
                return Collections.emptyList();
            }
        }, "searchEngineClient");
    }

    private void capture(final SearchEngineClient client, final SearchEngineClient.SearchCondition<SearchRequestBuilder> condition) {
        final SearchRequestBuilder builder = new SearchRequestBuilder(client, SearchAction.INSTANCE);
        assertTrue(condition.build(builder));
        capturedQueries.add(builder.request().source().query().toString().replaceAll("\\s", ""));
    }

    private static final String LABEL_FILTER = "\"terms\":{\"label\":[\"public\"]";

    private static List<QueryBuilder> labelFilter() {
        return List.of(QueryBuilders.termsQuery("label", "public"));
    }

    @Test
    public void test_getDocumentByDocId_withFilterQueries() {
        new SearchHelper().getDocumentByDocId("doc1", new String[] { "doc_id" }, OptionalThing.empty(), labelFilter());
        assertEquals(1, capturedQueries.size());
        final String json = capturedQueries.get(0);
        assertTrue(json.contains(LABEL_FILTER), json);
        assertTrue(json.contains("Rguest"), "the role filter must stay: " + json);
    }

    @Test
    public void test_getDocumentByDocId_withoutFilterQueries_unchanged() {
        new SearchHelper().getDocumentByDocId("doc1", new String[] { "doc_id" }, OptionalThing.empty());
        assertEquals(1, capturedQueries.size());
        final String json = capturedQueries.get(0);
        assertFalse(json.contains("\"terms\":{\"label\""), json);
        assertTrue(json.contains("Rguest"), json);
    }

    @Test
    public void test_getDocumentListByDocIds_withFilterQueries() {
        new SearchHelper().getDocumentListByDocIds(new String[] { "doc1", "doc2" }, new String[] { "doc_id" }, OptionalThing.empty(),
                SearchRequestType.JSON, labelFilter());
        assertEquals(1, capturedQueries.size());
        final String json = capturedQueries.get(0);
        assertTrue(json.contains(LABEL_FILTER), json);
        assertTrue(json.contains("Rguest"), json);
    }

    @Test
    public void test_getDocumentListByDocIds_withoutFilterQueries_unchanged() {
        new SearchHelper().getDocumentListByDocIds(new String[] { "doc1" }, new String[] { "doc_id" }, OptionalThing.empty(),
                SearchRequestType.JSON);
        assertEquals(1, capturedQueries.size());
        assertFalse(capturedQueries.get(0).contains("\"terms\":{\"label\""), capturedQueries.get(0));
    }
}
