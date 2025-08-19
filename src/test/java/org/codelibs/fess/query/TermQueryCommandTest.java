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

import java.math.BigDecimal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.util.DfTypeUtil;
import org.opensearch.index.query.MatchPhraseQueryBuilder;
import org.opensearch.index.query.PrefixQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.query.WildcardQueryBuilder;
import org.opensearch.search.sort.SortBuilder;

public class TermQueryCommandTest extends QueryTestBase {
    private static final Logger logger = LogManager.getLogger(TermQueryCommandTest.class);

    private TermQueryCommand queryCommand;

    @Override
    protected void setUpChild() throws Exception {
        queryCommand = new TermQueryCommand();
        queryCommand.register();
    }

    private void setQueryType(final String queryType) {
        // Re-create the base FessConfig with the new query type
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            private final FessConfig baseConfig = createBaseFessConfig();

            @Override
            public String getQueryDefaultQueryType() {
                return queryType;
            }

            // Delegate all other methods to base config
            @Override
            public String get(String key) {
                return baseConfig.get(key);
            }

            @Override
            public String getIndexFieldTitle() {
                return baseConfig.getIndexFieldTitle();
            }

            @Override
            public String getIndexFieldContent() {
                return baseConfig.getIndexFieldContent();
            }

            @Override
            public String getIndexFieldUrl() {
                return baseConfig.getIndexFieldUrl();
            }

            @Override
            public String getIndexFieldSite() {
                return baseConfig.getIndexFieldSite();
            }

            @Override
            public String getIndexFieldTimestamp() {
                return baseConfig.getIndexFieldTimestamp();
            }

            @Override
            public String getIndexFieldId() {
                return baseConfig.getIndexFieldId();
            }

            @Override
            public String getIndexFieldDocId() {
                return baseConfig.getIndexFieldDocId();
            }

            @Override
            public String getQueryDefaultLanguages() {
                return baseConfig.getQueryDefaultLanguages();
            }

            @Override
            public String getQueryLanguageMapping() {
                return baseConfig.getQueryLanguageMapping();
            }

            @Override
            public String getQueryAdditionalDefaultFields() {
                return baseConfig.getQueryAdditionalDefaultFields();
            }

            @Override
            public String getQueryAdditionalSearchFields() {
                return baseConfig.getQueryAdditionalSearchFields();
            }

            @Override
            public String getQueryAdditionalSortFields() {
                return baseConfig.getQueryAdditionalSortFields();
            }

            @Override
            public BigDecimal getQueryBoostTitleAsDecimal() {
                return baseConfig.getQueryBoostTitleAsDecimal();
            }

            @Override
            public BigDecimal getQueryBoostContentAsDecimal() {
                return baseConfig.getQueryBoostContentAsDecimal();
            }

            @Override
            public BigDecimal getQueryBoostFuzzyTitleAsDecimal() {
                return baseConfig.getQueryBoostFuzzyTitleAsDecimal();
            }

            @Override
            public BigDecimal getQueryBoostFuzzyContentAsDecimal() {
                return baseConfig.getQueryBoostFuzzyContentAsDecimal();
            }

            @Override
            public BigDecimal getQueryDismaxTieBreakerAsDecimal() {
                return baseConfig.getQueryDismaxTieBreakerAsDecimal();
            }

            @Override
            public BigDecimal getQueryBoostImportantContentAsDecimal() {
                return baseConfig.getQueryBoostImportantContentAsDecimal();
            }

            @Override
            public String getIndexFieldBoost() {
                return baseConfig.getIndexFieldBoost();
            }

            @Override
            public String getIndexFieldContentLength() {
                return baseConfig.getIndexFieldContentLength();
            }

            @Override
            public String getIndexFieldHost() {
                return baseConfig.getIndexFieldHost();
            }

            @Override
            public String getIndexFieldLastModified() {
                return baseConfig.getIndexFieldLastModified();
            }

            @Override
            public String getIndexFieldMimetype() {
                return baseConfig.getIndexFieldMimetype();
            }

            @Override
            public String getIndexFieldFiletype() {
                return baseConfig.getIndexFieldFiletype();
            }

            @Override
            public String getIndexFieldFilename() {
                return baseConfig.getIndexFieldFilename();
            }

            @Override
            public String getIndexFieldCreated() {
                return baseConfig.getIndexFieldCreated();
            }

            @Override
            public String getIndexFieldDigest() {
                return baseConfig.getIndexFieldDigest();
            }

            @Override
            public String getIndexFieldThumbnail() {
                return baseConfig.getIndexFieldThumbnail();
            }

            @Override
            public String getIndexFieldClickCount() {
                return baseConfig.getIndexFieldClickCount();
            }

            @Override
            public String getIndexFieldFavoriteCount() {
                return baseConfig.getIndexFieldFavoriteCount();
            }

            @Override
            public String getIndexFieldConfigId() {
                return baseConfig.getIndexFieldConfigId();
            }

            @Override
            public String getIndexFieldLang() {
                return baseConfig.getIndexFieldLang();
            }

            @Override
            public String getIndexFieldHasCache() {
                return baseConfig.getIndexFieldHasCache();
            }

            @Override
            public String getIndexFieldCache() {
                return baseConfig.getIndexFieldCache();
            }

            @Override
            public String getIndexFieldLabel() {
                return baseConfig.getIndexFieldLabel();
            }

            @Override
            public String getIndexFieldSegment() {
                return baseConfig.getIndexFieldSegment();
            }

            @Override
            public String[] getQueryAdditionalResponseFields(String... fields) {
                return baseConfig.getQueryAdditionalResponseFields(fields);
            }

            @Override
            public String[] getQueryAdditionalScrollResponseFields(String... fields) {
                return baseConfig.getQueryAdditionalScrollResponseFields(fields);
            }

            @Override
            public String[] getQueryAdditionalCacheResponseFields(String... fields) {
                return baseConfig.getQueryAdditionalCacheResponseFields(fields);
            }

            @Override
            public String[] getQueryAdditionalHighlightedFields(String... fields) {
                return baseConfig.getQueryAdditionalHighlightedFields(fields);
            }

            @Override
            public String[] getQueryAdditionalSearchFields(String... fields) {
                return baseConfig.getQueryAdditionalSearchFields(fields);
            }

            @Override
            public String[] getQueryAdditionalFacetFields(String... fields) {
                return baseConfig.getQueryAdditionalFacetFields(fields);
            }

            @Override
            public String[] getQueryAdditionalSortFields(String... fields) {
                return baseConfig.getQueryAdditionalSortFields(fields);
            }

            @Override
            public String[] getQueryAdditionalApiResponseFields(String... fields) {
                return baseConfig.getQueryAdditionalApiResponseFields(fields);
            }

            @Override
            public String getIndexFieldAnchor() {
                return baseConfig.getIndexFieldAnchor();
            }

            @Override
            public String getIndexFieldExpires() {
                return baseConfig.getIndexFieldExpires();
            }

            @Override
            public String getIndexFieldParentId() {
                return baseConfig.getIndexFieldParentId();
            }

            @Override
            public String getIndexFieldPrimaryTerm() {
                return baseConfig.getIndexFieldPrimaryTerm();
            }

            @Override
            public String getIndexFieldRole() {
                return baseConfig.getIndexFieldRole();
            }

            @Override
            public String getIndexFieldSeqNo() {
                return baseConfig.getIndexFieldSeqNo();
            }

            @Override
            public String getIndexFieldVersion() {
                return baseConfig.getIndexFieldVersion();
            }

            @Override
            public String getIndexFieldImportantContent() {
                return baseConfig.getIndexFieldImportantContent();
            }

            @Override
            public String[] getQueryAdditionalNotAnalyzedFields(String... fields) {
                return baseConfig.getQueryAdditionalNotAnalyzedFields(fields);
            }

            @Override
            public String getQueryAdditionalAnalyzedFields() {
                return baseConfig.getQueryAdditionalAnalyzedFields();
            }

            @Override
            public String getResponseFieldContentDescription() {
                return baseConfig.getResponseFieldContentDescription();
            }

            @Override
            public String getResponseFieldContentTitle() {
                return baseConfig.getResponseFieldContentTitle();
            }

            @Override
            public String getResponseFieldSitePath() {
                return baseConfig.getResponseFieldSitePath();
            }

            @Override
            public String getResponseFieldUrlLink() {
                return baseConfig.getResponseFieldUrlLink();
            }

            @Override
            public BigDecimal getQueryBoostImportantContentLangAsDecimal() {
                return baseConfig.getQueryBoostImportantContentLangAsDecimal();
            }

            @Override
            public String getQueryBoostImportantContentLang() {
                return baseConfig.getQueryBoostImportantContentLang();
            }

            @Override
            public String getQueryBoostFuzzyMinLength() {
                return baseConfig.getQueryBoostFuzzyMinLength();
            }

            @Override
            public Integer getQueryBoostFuzzyMinLengthAsInteger() {
                return baseConfig.getQueryBoostFuzzyMinLengthAsInteger();
            }

            @Override
            public String getQueryBoostFuzzyTitlePrefixLength() {
                return baseConfig.getQueryBoostFuzzyTitlePrefixLength();
            }

            @Override
            public Integer getQueryBoostFuzzyTitlePrefixLengthAsInteger() {
                return baseConfig.getQueryBoostFuzzyTitlePrefixLengthAsInteger();
            }

            @Override
            public String getQueryBoostFuzzyTitleTranspositions() {
                return baseConfig.getQueryBoostFuzzyTitleTranspositions();
            }

            @Override
            public String getQueryBoostFuzzyContentPrefixLength() {
                return baseConfig.getQueryBoostFuzzyContentPrefixLength();
            }

            @Override
            public Integer getQueryBoostFuzzyContentPrefixLengthAsInteger() {
                return baseConfig.getQueryBoostFuzzyContentPrefixLengthAsInteger();
            }

            @Override
            public String getQueryBoostFuzzyContentTranspositions() {
                return baseConfig.getQueryBoostFuzzyContentTranspositions();
            }

            @Override
            public String getQueryBoostFuzzyTitleFuzziness() {
                return baseConfig.getQueryBoostFuzzyTitleFuzziness();
            }

            @Override
            public String getQueryBoostFuzzyContentFuzziness() {
                return baseConfig.getQueryBoostFuzzyContentFuzziness();
            }

            @Override
            public String getQueryBoostFuzzyTitleExpansions() {
                return baseConfig.getQueryBoostFuzzyTitleExpansions();
            }

            @Override
            public Integer getQueryBoostFuzzyTitleExpansionsAsInteger() {
                return baseConfig.getQueryBoostFuzzyTitleExpansionsAsInteger();
            }

            @Override
            public String getQueryBoostFuzzyContentExpansions() {
                return baseConfig.getQueryBoostFuzzyContentExpansions();
            }

            @Override
            public Integer getQueryBoostFuzzyContentExpansionsAsInteger() {
                return baseConfig.getQueryBoostFuzzyContentExpansionsAsInteger();
            }
        });
    }

    public void test_convertTermQuery() throws Exception {
        setQueryType("bool");
        assertQueryBuilder(DefaultQueryBuilder.class,
                "{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}",
                "aaa");
        assertQueryBuilder(MatchPhraseQueryBuilder.class,
                "{\"match_phrase\":{\"title\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}}", //
                "title:aaa");
        assertQueryBuilder(MatchPhraseQueryBuilder.class,
                "{\"match_phrase\":{\"content\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}}", //
                "content:aaa");
        assertQueryBuilder(DefaultQueryBuilder.class,
                "{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"xxx:aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"xxx:aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}},{\"fuzzy\":{\"title\":{\"value\":\"xxx:aaa\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.01}}},{\"fuzzy\":{\"content\":{\"value\":\"xxx:aaa\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.005}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}",
                "xxx:aaa");
        assertQueryBuilder(WildcardQueryBuilder.class, //
                "{\"wildcard\":{\"url\":{\"wildcard\":\"*aaa*\",\"boost\":1.0}}}", //
                "inurl:aaa");
        assertQueryBuilder(TermQueryBuilder.class, //
                "{\"term\":{\"url\":{\"value\":\"aaa\",\"boost\":1.0}}}", //
                "url:aaa");
        assertQueryBuilder(PrefixQueryBuilder.class, //
                "{\"prefix\":{\"site\":{\"value\":\"aaa\",\"boost\":1.0}}}", //
                "site:aaa");

        assertQueryBuilder("{\"timestamp\":{\"order\":\"asc\"}}", "sort:timestamp");
        assertQueryBuilder("{\"timestamp\":{\"order\":\"asc\"}}", "sort:timestamp.asc");
        assertQueryBuilder("{\"timestamp\":{\"order\":\"desc\"}}", "sort:timestamp.desc");

        try {
            assertQueryBuilder("", "sort:xxx");
            fail();
        } catch (InvalidQueryException e) {
            // nothing
        }
    }

    public void test_convertTermQuery_dismax() throws Exception {
        setQueryType("dismax");
        assertQueryBuilder(DefaultQueryBuilder.class,
                "{\"dis_max\":{\"tie_breaker\":0.1,\"queries\":[{\"match_phrase\":{\"title\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}}],\"boost\":1.0}}",
                "aaa");
        assertQueryBuilder(MatchPhraseQueryBuilder.class,
                "{\"match_phrase\":{\"title\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}}", //
                "title:aaa");
        assertQueryBuilder(MatchPhraseQueryBuilder.class,
                "{\"match_phrase\":{\"content\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}}", //
                "content:aaa");
        assertQueryBuilder(DefaultQueryBuilder.class,
                "{\"dis_max\":{\"tie_breaker\":0.1,\"queries\":[{\"match_phrase\":{\"title\":{\"query\":\"xxx:aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"xxx:aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}},{\"fuzzy\":{\"title\":{\"value\":\"xxx:aaa\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.01}}},{\"fuzzy\":{\"content\":{\"value\":\"xxx:aaa\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.005}}}],\"boost\":1.0}}",
                "xxx:aaa");
        assertQueryBuilder(WildcardQueryBuilder.class, //
                "{\"wildcard\":{\"url\":{\"wildcard\":\"*aaa*\",\"boost\":1.0}}}", //
                "inurl:aaa");
        assertQueryBuilder(TermQueryBuilder.class, //
                "{\"term\":{\"url\":{\"value\":\"aaa\",\"boost\":1.0}}}", //
                "url:aaa");
        assertQueryBuilder(PrefixQueryBuilder.class, //
                "{\"prefix\":{\"site\":{\"value\":\"aaa\",\"boost\":1.0}}}", //
                "site:aaa");

        assertQueryBuilder("{\"timestamp\":{\"order\":\"asc\"}}", "sort:timestamp");
        assertQueryBuilder("{\"timestamp\":{\"order\":\"asc\"}}", "sort:timestamp.asc");
        assertQueryBuilder("{\"timestamp\":{\"order\":\"desc\"}}", "sort:timestamp.desc");

        try {
            assertQueryBuilder("", "sort:xxx");
            fail();
        } catch (InvalidQueryException e) {
            // nothing
        }
    }

    private void assertQueryBuilder(final String expect, final String text) throws Exception {
        QueryContext queryContext = assertQueryBuilder(null, null, text);
        List<SortBuilder<?>> sortBuilders = queryContext.sortBuilders();
        assertEquals(1, sortBuilders.size());
        logger.info("{} => {}", text, sortBuilders.get(0).toString());
        assertEquals(expect, sortBuilders.get(0).toString().replaceAll("[\s\n]", ""));
    }

    private QueryContext assertQueryBuilder(final Class<?> expectedClass, final String expectedQuery, final String text) throws Exception {
        final QueryContext queryContext = new QueryContext(text, false);
        final Query query = ComponentUtil.getQueryParser().parse(queryContext.getQueryString());
        QueryBuilder builder = queryCommand.convertTermQuery(queryContext, (TermQuery) query, 1.0f);
        if (text.startsWith("sort:")) {
            assertNull(builder);
        } else {
            logger.info("{} => {}", text, builder.toString());
            assertEquals(expectedClass, builder.getClass());
            assertEquals(expectedQuery, builder.toString().replaceAll("[\s\n]", ""));
        }
        return queryContext;
    }
}
