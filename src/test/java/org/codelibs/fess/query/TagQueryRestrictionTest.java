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
package org.codelibs.fess.query;

import java.util.Locale;
import java.util.Set;

import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.MatchNoneQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.TermQueryBuilder;
import org.junit.jupiter.api.Test;

/**
 * A condition on the {@code tag} field matches only the tags that the caller can see.
 */
public class TagQueryRestrictionTest extends QueryTestBase {

    private static final String VISIBLE = "a1b2c3d4visible";

    private static final String HIDDEN = "e5f6a7b8hidden";

    private int resolveCount;

    private SearchRequestType resolvedType;

    @Override
    protected void setUpChild() throws Exception {
        new TermQueryCommand().register();
        new PhraseQueryCommand().register();
        new WildcardQueryCommand().register();
        new PrefixQueryCommand().register();
        new FuzzyQueryCommand().register();
        new TermRangeQueryCommand().register();
        new BooleanQueryCommand().register();
        new BoostQueryCommand().register();
        resolveCount = 0;
        resolvedType = null;
        ComponentUtil.register(new LabelTypeHelper() {
            @Override
            public Set<String> getTagValueSet(final SearchRequestType searchRequestType, final Locale requestLocale) {
                resolveCount++;
                resolvedType = searchRequestType;
                return Set.of(VISIBLE);
            }
        }, "labelTypeHelper");
    }

    private QueryBuilder build(final String query, final SearchRequestType searchRequestType) {
        final QueryContext context = new QueryContext(query, false);
        context.setSearchRequestType(searchRequestType);
        return queryProcessor.execute(context, ComponentUtil.getQueryParser().parse(query), 1.0f);
    }

    private QueryBuilder build(final String query) {
        return build(query, SearchRequestType.JSON);
    }

    private void assertTermQuery(final QueryBuilder builder, final String value) {
        assertTrue(builder instanceof TermQueryBuilder, "expected a term query: " + builder);
        final TermQueryBuilder termQuery = (TermQueryBuilder) builder;
        assertEquals("tag", termQuery.fieldName());
        assertEquals(value, termQuery.value());
    }

    private void assertMatchNone(final QueryBuilder builder) {
        assertTrue(builder instanceof MatchNoneQueryBuilder, "expected a query matching nothing: " + builder);
    }

    @Test
    public void test_fieldsTag_visible() {
        // fields.tag=<value> is turned into tag:"<value>"
        assertTermQuery(build("tag:\"" + VISIBLE + "\""), VISIBLE);
    }

    @Test
    public void test_fieldsTag_hidden() {
        assertMatchNone(build("tag:\"" + HIDDEN + "\""));
    }

    @Test
    public void test_queryTag_visible() {
        assertTermQuery(build("tag:" + VISIBLE), VISIBLE);
    }

    @Test
    public void test_queryTag_hidden() {
        assertMatchNone(build("tag:" + HIDDEN));
    }

    @Test
    public void test_queryTag_hiddenWithKeyword() {
        final QueryBuilder builder = build("report tag:" + HIDDEN);
        assertTrue(builder instanceof BoolQueryBuilder, builder.toString());
        assertTrue(builder.toString().contains("match_none"), builder.toString());
        assertFalse(builder.toString().contains(HIDDEN), builder.toString());
    }

    @Test
    public void test_queryTag_patterns() {
        assertMatchNone(build("tag:e5f6*"));
        assertMatchNone(build("tag:e5f6?7b8hidden"));
        assertMatchNone(build("tag:" + HIDDEN + "~"));
        assertMatchNone(build("tag:[a TO z]"));
        assertMatchNone(build("tag:\"" + VISIBLE + " " + HIDDEN + "\""));
    }

    @Test
    public void test_queryTag_negatedHidden() {
        final QueryBuilder builder = build("-tag:" + HIDDEN);
        assertTrue(builder instanceof BoolQueryBuilder, builder.toString());
        final BoolQueryBuilder boolQuery = (BoolQueryBuilder) builder;
        assertEquals(1, boolQuery.mustNot().size());
        assertMatchNone(boolQuery.mustNot().get(0));
    }

    @Test
    public void test_queryTag_resolvedOnce() {
        build("tag:" + VISIBLE + " OR tag:" + HIDDEN + " OR tag:\"" + VISIBLE + "\"");
        assertEquals(1, resolveCount);
        assertEquals(SearchRequestType.JSON, resolvedType);
    }

    @Test
    public void test_queryTag_unknownRequestType() {
        assertMatchNone(build("tag:" + HIDDEN, null));
        assertEquals(SearchRequestType.JSON, resolvedType);
    }

    @Test
    public void test_queryTag_adminSearch() {
        assertTermQuery(build("tag:" + HIDDEN, SearchRequestType.ADMIN_SEARCH), HIDDEN);
        assertEquals(0, resolveCount);
    }

    @Test
    public void test_otherField_notResolved() {
        final QueryBuilder builder = build("title:" + HIDDEN);
        assertFalse(builder instanceof MatchNoneQueryBuilder, builder.toString());
        assertEquals(0, resolveCount);
    }

    @Test
    public void test_queryTag_resolutionFails() {
        ComponentUtil.register(new LabelTypeHelper() {
            @Override
            public Set<String> getTagValueSet(final SearchRequestType searchRequestType, final Locale requestLocale) {
                throw new IllegalStateException("not available");
            }
        }, "labelTypeHelper");
        assertMatchNone(build("tag:" + VISIBLE));
    }
}
