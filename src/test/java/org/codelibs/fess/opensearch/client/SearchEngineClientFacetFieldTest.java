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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.TagTypeHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exentity.TagType;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.opensearch.client.SearchEngineClient.SearchConditionBuilder;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.codelibs.fesen.opensearch.action.search.SearchAction;
import org.codelibs.fesen.opensearch.action.search.SearchRequestBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.AggregationBuilder;
import org.codelibs.fesen.opensearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

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

    private boolean userTagEnabled;

    private Set<String> visibleTagValues;

    private SearchRequestType resolvedType;

    private int resolveCount;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        queryFieldConfig = new QueryFieldConfig();
        // setFacetFields() repopulates the lookup set, so init() (and its long
        // list of index.field.* getters) is not needed here.
        queryFieldConfig.setFacetFields(new String[] { "label", "filetype", "tag" });
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");
        userTagEnabled = true;
        visibleTagValues = new LinkedHashSet<>();
        resolvedType = null;
        resolveCount = 0;
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isUserTagEnabled() {
                return userTagEnabled;
            }

            @Override
            public String getIndexFieldTag() {
                return "tag";
            }
        });
        ComponentUtil.register(new TagTypeHelper() {
            @Override
            public Set<String> getVisibleTagValues(final SearchRequestType type) {
                resolveCount++;
                resolvedType = type;
                return visibleTagValues;
            }

            @Override
            public Map<String, TagType> getVisibleTagTypes(final Collection<String> values, final SearchRequestType type) {
                throw new AssertionError("the facet must not look up tags one by one");
            }
        }, "tagTypeHelper");
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

    private SearchRequestBuilder buildTagFacet(final SearchRequestType type) {
        final FacetInfo facetInfo = new FacetInfo();
        facetInfo.field = new String[] { "label", "tag" };
        final SearchRequestBuilder searchRequestBuilder = newSearchRequestBuilder();
        SearchConditionBuilder.builder(searchRequestBuilder)
                .facetInfo(facetInfo)
                .searchRequestType(type)
                .buildFacet(null, queryFieldConfig, ComponentUtil.getFessConfig());
        return searchRequestBuilder;
    }

    private TermsAggregationBuilder findTagAggregation(final SearchRequestBuilder searchRequestBuilder) {
        for (final AggregationBuilder aggregation : searchRequestBuilder.request().source().aggregations().getAggregatorFactories()) {
            if (aggregation instanceof final TermsAggregationBuilder terms && "tag".equals(terms.field())) {
                return terms;
            }
        }
        return null;
    }

    @Test
    public void test_buildFacet_tag_includesOnlyVisibleTags() {
        final String own = TagType.toTagValue("foo", "alice");
        final String shared = TagType.toTagValue("foo", "bob");
        visibleTagValues.add(own);
        visibleTagValues.add(shared);

        final SearchRequestBuilder searchRequestBuilder = buildTagFacet(SearchRequestType.JSON);

        final TermsAggregationBuilder terms = findTagAggregation(searchRequestBuilder);
        assertNotNull(terms);
        assertNotNull(terms.includeExclude());
        final String json = searchRequestBuilder.request().source().toString().replaceAll("\\s", "");
        // the same name of two owners is two separate values, and only those two may become buckets
        assertTrue(json.contains("\"include\":[\"" + own + "\",\"" + shared + "\"]")
                || json.contains("\"include\":[\"" + shared + "\",\"" + own + "\"]"), json);
        assertEquals(SearchRequestType.JSON, resolvedType);
        assertEquals(1, resolveCount);
    }

    @Test
    public void test_buildFacet_tag_noVisibleTags_notAggregated() {
        // an anonymous caller sees no tag: the tag facet is left out, the others are kept, no error
        final SearchRequestBuilder searchRequestBuilder = buildTagFacet(SearchRequestType.SEARCH);
        assertNull(findTagAggregation(searchRequestBuilder));
        assertEquals(1, searchRequestBuilder.request().source().aggregations().count());
        assertEquals(SearchRequestType.SEARCH, resolvedType);
    }

    @Test
    public void test_buildFacet_tag_disabled_notAggregated() {
        userTagEnabled = false;
        visibleTagValues.add(TagType.toTagValue("foo", "alice"));
        final SearchRequestBuilder searchRequestBuilder = buildTagFacet(SearchRequestType.JSON);
        assertNull(findTagAggregation(searchRequestBuilder));
        assertEquals(0, resolveCount);
    }

    @Test
    public void test_buildFacet_tag_adminSearch_unrestricted() {
        final SearchRequestBuilder searchRequestBuilder = buildTagFacet(SearchRequestType.ADMIN_SEARCH);
        final TermsAggregationBuilder terms = findTagAggregation(searchRequestBuilder);
        assertNotNull(terms);
        assertNull(terms.includeExclude());
        assertEquals(0, resolveCount);
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
