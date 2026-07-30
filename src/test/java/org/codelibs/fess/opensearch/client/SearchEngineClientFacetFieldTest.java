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
package org.codelibs.fess.opensearch.client;

import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchConditionBuilder;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.opensearch.action.search.SearchAction;
import org.opensearch.action.search.SearchRequestBuilder;

/**
 * Verifies that a facet field outside the allowlist is reported as an invalid
 * query rather than being swallowed into an empty result set.
 *
 * <p>A facet field is validated against {@code query.additional.facet.fields}
 * only, never against the index mapping. When a theme or client requests a
 * field the deployment forgot to allowlist, the failure used to surface as a
 * {@code SearchQueryException}, which {@code RankFusionProcessor} catches
 * generically and turns into zero documents plus a WARN - so every search
 * silently returned nothing. It must be an {@link InvalidQueryException} so
 * the search action and the API managers report it to the caller, the same way
 * an unsupported sort field is reported.</p>
 */
public class SearchEngineClientFacetFieldTest extends UnitFessTestCase {

    private QueryFieldConfig queryFieldConfig;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        queryFieldConfig = new QueryFieldConfig();
        // setFacetFields() repopulates the lookup set, so init() (and its long
        // list of index.field.* getters) is not needed here.
        queryFieldConfig.setFacetFields(new String[] { "label", "filetype" });
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");
    }

    @Test
    public void test_buildFacet_unsupportedField() {
        final FacetInfo facetInfo = new FacetInfo();
        facetInfo.field = new String[] { "repository" };

        try {
            buildFacet(facetInfo);
            fail("InvalidQueryException should be thrown for an unsupported facet field.");
        } catch (final InvalidQueryException e) {
            assertNotNull(e.getMessageCode());
            assertTrue(e.getMessage().contains("repository"));
        }
    }

    @Test
    public void test_buildFacet_unsupportedFieldAfterSupportedField() {
        final FacetInfo facetInfo = new FacetInfo();
        facetInfo.field = new String[] { "label", "repository" };

        try {
            buildFacet(facetInfo);
            fail("InvalidQueryException should be thrown for an unsupported facet field.");
        } catch (final InvalidQueryException e) {
            assertTrue(e.getMessage().contains("repository"));
        }
    }

    @Test
    public void test_buildFacet_supportedFields() {
        final FacetInfo facetInfo = new FacetInfo();
        facetInfo.field = new String[] { "label", "filetype" };

        final SearchRequestBuilder searchRequestBuilder = newSearchRequestBuilder();
        SearchConditionBuilder.builder(searchRequestBuilder)
                .facetInfo(facetInfo)
                .buildFacet(null, queryFieldConfig, ComponentUtil.getFessConfig());

        assertEquals(2, searchRequestBuilder.request().source().aggregations().count());
    }

    private void buildFacet(final FacetInfo facetInfo) {
        SearchConditionBuilder.builder(newSearchRequestBuilder())
                .facetInfo(facetInfo)
                .buildFacet(null, queryFieldConfig, ComponentUtil.getFessConfig());
    }

    private SearchRequestBuilder newSearchRequestBuilder() {
        return new SearchRequestBuilder(new SearchEngineClient(), SearchAction.INSTANCE);
    }
}
