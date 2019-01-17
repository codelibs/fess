/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import java.io.File;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.ext.ExtendableQueryParser;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;

public class QueryHelperTest extends UnitFessTestCase {

    private QueryHelper queryHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queryHelper = new QueryHelper() {
            protected QueryParser getQueryParser() {
                ExtendableQueryParser queryParser = new ExtendableQueryParser(Constants.DEFAULT_FIELD, new WhitespaceAnalyzer());
                queryParser.setAllowLeadingWildcard(true);
                queryParser.setDefaultOperator(QueryParser.Operator.AND);
                return queryParser;
            }
        };
        File file = File.createTempFile("test", ".properties");
        file.deleteOnExit();
        FileUtil.writeBytes(file.getAbsolutePath(), "ldap.security.principal=%s@fess.codelibs.local".getBytes("UTF-8"));
        DynamicProperties systemProps = new DynamicProperties(file);
        ComponentUtil.register(systemProps, "systemProperties");
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        ComponentUtil.register(new VirtualHostHelper(), "virtualHostHelper");
        ComponentUtil.register(new KeyMatchHelper(), "keyMatchHelper");
        inject(queryHelper);
        queryHelper.init();
    }

    public void test_build() {
        float titleBoost = 0.01f;
        float contentBoost = 0.005f;

        assertQuery(functionScoreQuery(simpleQuery("QUERY", titleBoost, contentBoost)), buildQuery("QUERY"));
        assertQuery(functionScoreQuery(simpleQuery("QUERY", titleBoost, contentBoost)), buildQuery(" QUERY"));
        assertQuery(functionScoreQuery(simpleQuery("QUERY", titleBoost, contentBoost)), buildQuery("QUERY "));

        assertQuery(
                functionScoreQuery(andQuery(simpleQuery("QUERY1", titleBoost, contentBoost),
                        simpleQuery("QUERY2", titleBoost, contentBoost))), buildQuery("QUERY1 QUERY2"));
        assertQuery(
                functionScoreQuery(andQuery(simpleQuery("QUERY1", titleBoost, contentBoost),
                        simpleQuery("QUERY2", titleBoost, contentBoost))), buildQuery("QUERY1 AND QUERY2"));

        assertQuery(
                functionScoreQuery(orQuery(simpleQuery("QUERY1", titleBoost, contentBoost), simpleQuery("QUERY2", titleBoost, contentBoost))),
                buildQuery("QUERY1 OR QUERY2"));

        assertQueryBuilder("test", "", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("test", "test", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("test", "a", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("test", "あ", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("test", "ア", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("test", "亜", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("test", "아", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("title", "test", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("title", "a", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("title", "あ", PrefixQueryBuilder.class);
        assertQueryBuilder("title", "ああ", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("title", "ア", PrefixQueryBuilder.class);
        assertQueryBuilder("title", "アア", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("title", "亜", PrefixQueryBuilder.class);
        assertQueryBuilder("title", "亜亜", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("title", "아", PrefixQueryBuilder.class);
        assertQueryBuilder("title", "아아", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("content", "test", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("content", "a", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("content", "あ", PrefixQueryBuilder.class);
        assertQueryBuilder("content", "ああ", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("content", "ア", PrefixQueryBuilder.class);
        assertQueryBuilder("content", "アア", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("content", "亜", PrefixQueryBuilder.class);
        assertQueryBuilder("content", "亜亜", MatchPhraseQueryBuilder.class);
        assertQueryBuilder("content", "아", PrefixQueryBuilder.class);
        assertQueryBuilder("content", "아아", MatchPhraseQueryBuilder.class);

        assertEquals(
                "{\"function_score\":{\"query\":{\"prefix\":{\"site\":{\"value\":\"fess.codelibs.org\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                buildQuery("site:fess.codelibs.org").toString().replaceAll("\\s", ""));

        assertEquals(
                "{\"function_score\":{\"query\":{\"wildcard\":{\"title\":{\"wildcard\":\"*\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                buildQuery("allintitle:").toString().replaceAll("\\s", ""));
        assertEquals(
                "{\"function_score\":{\"query\":{\"match_phrase\":{\"title\":{\"query\":\"test\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                buildQuery("allintitle:test").toString().replaceAll("\\s", ""));
        assertEquals(
                "{\"function_score\":{\"query\":{\"match_phrase\":{\"title\":{\"query\":\"test\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                buildQuery("allintitle: test").toString().replaceAll("\\s", ""));
        assertEquals(
                "{\"function_score\":{\"query\":{\"bool\":{\"must\":[{\"match_phrase\":{\"title\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}},{\"match_phrase\":{\"title\":{\"query\":\"bbb\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                buildQuery("allintitle: aaa bbb").toString().replaceAll("\\s", ""));

        assertEquals(
                "{\"function_score\":{\"query\":{\"wildcard\":{\"url\":{\"wildcard\":\"*\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                buildQuery("allinurl:").toString().replaceAll("\\s", ""));
        assertEquals(
                "{\"function_score\":{\"query\":{\"wildcard\":{\"url\":{\"wildcard\":\"*test*\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                buildQuery("allinurl:test").toString().replaceAll("\\s", ""));
        assertEquals(
                "{\"function_score\":{\"query\":{\"wildcard\":{\"url\":{\"wildcard\":\"*test*\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                buildQuery("allinurl: test").toString().replaceAll("\\s", ""));
        assertEquals(
                "{\"function_score\":{\"query\":{\"bool\":{\"must\":[{\"wildcard\":{\"url\":{\"wildcard\":\"*aaa*\",\"boost\":1.0}}},{\"wildcard\":{\"url\":{\"wildcard\":\"*bbb*\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                buildQuery("allinurl: aaa bbb").toString().replaceAll("\\s", ""));
    }

    private void assertQueryBuilder(String field, String value, Class<?> clazz) {
        QueryBuilder queryBuilder = queryHelper.buildMatchPhraseQuery(field, value);
        assertEquals(clazz, queryBuilder.getClass());
    }

    private QueryBuilder andQuery(QueryBuilder... queries) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        for (QueryBuilder query : queries) {
            boolQuery.must(query);
        }
        return boolQuery;
    }

    private QueryBuilder orQuery(QueryBuilder... queries) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        for (QueryBuilder query : queries) {
            boolQuery.should(query);
        }
        return boolQuery;
    }

    private QueryBuilder simpleQuery(String query, float titleBoost, float contentBoost) {
        return QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery("title", query).boost(titleBoost))
                .should(QueryBuilders.matchPhraseQuery("content", query).boost(contentBoost));
    }

    private QueryBuilder functionScoreQuery(QueryBuilder queryBuilder) {
        return QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.fieldValueFactorFunction("boost"));
    }

    private void assertQuery(QueryBuilder query1, QueryBuilder query2) {
        assertEquals(query1.toString(), query2.toString());
    }

    private QueryBuilder buildQuery(String query) {
        return queryHelper.build(SearchRequestType.SEARCH, query, context -> {
            context.skipRoleQuery();
        }).getQueryBuilder();
    }

}