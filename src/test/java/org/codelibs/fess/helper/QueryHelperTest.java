/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.codelibs.core.io.FileUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.query.BooleanQueryCommand;
import org.codelibs.fess.query.BoostQueryCommand;
import org.codelibs.fess.query.FuzzyQueryCommand;
import org.codelibs.fess.query.MatchAllQueryCommand;
import org.codelibs.fess.query.PhraseQueryCommand;
import org.codelibs.fess.query.PrefixQueryCommand;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.query.QueryProcessor;
import org.codelibs.fess.query.TermQueryCommand;
import org.codelibs.fess.query.TermRangeQueryCommand;
import org.codelibs.fess.query.WildcardQueryCommand;
import org.codelibs.fess.query.parser.QueryParser;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.ScoreFunctionBuilders;

public class QueryHelperTest extends UnitFessTestCase {

    private QueryHelper queryHelper;

    private QueryFieldConfig queryFieldConfig;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queryHelper = new QueryHelper() {
            protected QueryParser getQueryParser() {
                QueryParser queryParser = new QueryParser();
                queryParser.init();
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
        queryFieldConfig = new QueryFieldConfig();
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");
        queryFieldConfig.init();
        QueryProcessor queryProcessor = new QueryProcessor();
        ComponentUtil.register(queryProcessor, "queryProcessor");
        queryProcessor.init();
        new BooleanQueryCommand().register();
        new BoostQueryCommand().register();
        new FuzzyQueryCommand().register();
        new MatchAllQueryCommand().register();
        new PhraseQueryCommand().register();
        new PrefixQueryCommand().register();
        new TermQueryCommand().register();
        new TermRangeQueryCommand().register();
        new WildcardQueryCommand().register();
    }

    public void test_build_simple() {
        float titleBoost = 0.5f;
        float contentBoost = 0.05f;

        assertQuery(functionScoreQuery(simpleQuery("QUERY", titleBoost, contentBoost)), //
                Map.of("_default", List.of("QUERY")), //
                Set.of("QUERY"), //
                buildQuery("QUERY"));
        assertQuery(functionScoreQuery(simpleQuery("QUERY", titleBoost, contentBoost)), //
                Map.of("_default", List.of("QUERY")), //
                Set.of("QUERY"), //
                buildQuery(" QUERY"));
        assertQuery(functionScoreQuery(simpleQuery("QUERY", titleBoost, contentBoost)), //
                Map.of("_default", List.of("QUERY")), //
                Set.of("QUERY"), //
                buildQuery("QUERY "));
    }

    public void test_build_multiple() {
        float titleBoost = 0.5f;
        float contentBoost = 0.05f;

        assertQuery(
                functionScoreQuery(
                        andQuery(simpleQuery("QUERY1", titleBoost, contentBoost), simpleQuery("QUERY2", titleBoost, contentBoost))),
                Map.of("_default", List.of("QUERY1", "QUERY2")), //
                Set.of("QUERY1", "QUERY2"), //
                buildQuery("QUERY1 QUERY2"));
        assertQuery(
                functionScoreQuery(
                        andQuery(simpleQuery("QUERY1", titleBoost, contentBoost), simpleQuery("QUERY2", titleBoost, contentBoost))),
                Map.of("_default", List.of("QUERY1", "QUERY2")), //
                Set.of("QUERY1", "QUERY2"), //
                buildQuery("QUERY1 AND QUERY2"));

        assertQuery(
                functionScoreQuery(
                        orQuery(simpleQuery("QUERY1", titleBoost, contentBoost), simpleQuery("QUERY2", titleBoost, contentBoost))),
                Map.of("_default", List.of("QUERY1", "QUERY2")), //
                Set.of("QUERY1", "QUERY2"), //
                buildQuery("QUERY1 OR QUERY2"));

    }

    public void test_build_boost() {
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"QUERY1\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":5.0}}},{\"match_phrase\":{\"content\":{\"query\":\"QUERY1\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"fuzzy\":{\"title\":{\"value\":\"QUERY1\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.01}}},{\"fuzzy\":{\"content\":{\"value\":\"QUERY1\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.005}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("QUERY1")), //
                Set.of("QUERY1"), //
                buildQuery("QUERY1^10"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"must\":[{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"QUERY1\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":5.0}}},{\"match_phrase\":{\"content\":{\"query\":\"QUERY1\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"fuzzy\":{\"title\":{\"value\":\"QUERY1\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.01}}},{\"fuzzy\":{\"content\":{\"value\":\"QUERY1\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.005}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"QUERY2\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":2.5}}},{\"match_phrase\":{\"content\":{\"query\":\"QUERY2\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.25}}},{\"fuzzy\":{\"title\":{\"value\":\"QUERY2\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.01}}},{\"fuzzy\":{\"content\":{\"value\":\"QUERY2\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.005}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("QUERY1", "QUERY2")), //
                Set.of("QUERY1", "QUERY2"), //
                buildQuery("QUERY1^10 QUERY2^5"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"match_phrase\":{\"title\":{\"query\":\"QUERY1\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":10.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("title", List.of("QUERY1")), //
                Set.of("QUERY1"), //
                buildQuery("title:QUERY1^10"));
    }

    public void test_build_wildcard() {
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"wildcard\":{\"title\":{\"wildcard\":\"*\",\"boost\":0.5}}},{\"wildcard\":{\"content\":{\"wildcard\":\"*\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("*")), //
                Set.of(), //
                buildQuery("*"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"wildcard\":{\"title\":{\"wildcard\":\"*\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("title", List.of("*")), //
                Set.of(), //
                buildQuery("title:*"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"wildcard\":{\"title\":{\"wildcard\":\"*aaa*\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("title", List.of("*aaa*")), //
                Set.of("aaa"), //
                buildQuery("title:*aaa*"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"wildcard\":{\"title\":{\"wildcard\":\"*aaa:*\",\"boost\":0.5}}},{\"wildcard\":{\"content\":{\"wildcard\":\"*aaa:*\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("*aaa:*")), //
                Set.of("aaa:"), //
                buildQuery("aaa:*"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"wildcard\":{\"title\":{\"wildcard\":\"*aaa*\",\"boost\":0.5}}},{\"wildcard\":{\"content\":{\"wildcard\":\"*aaa*\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("*aaa*")), //
                Set.of("aaa"), //
                buildQuery("*aaa*"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"wildcard\":{\"title\":{\"wildcard\":\"*aaa:*bbb*\",\"boost\":0.5}}},{\"wildcard\":{\"content\":{\"wildcard\":\"*aaa:*bbb*\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("*aaa:*bbb*")), //
                Set.of("aaa:*bbb"), //
                buildQuery("aaa:*bbb*"));
    }

    public void test_build_phrase() {
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"QUERY1QUERY2\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"QUERY1QUERY2\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("QUERY1 QUERY2")), //
                Set.of("QUERY1", "QUERY2"), //
                buildQuery("\"QUERY1 QUERY2\""));
        assertQueryContext(buildQuery("\"QUERY1 QUERY2\"").getQueryBuilder().toString().replaceAll("\\s", ""),
                buildQuery("aaa:\"QUERY1 QUERY2\""));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"match_phrase\":{\"title\":{\"query\":\"QUERY1QUERY2\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("title", List.of("QUERY1 QUERY2")), //
                Set.of("QUERY1", "QUERY2"), //
                buildQuery("title:\"QUERY1 QUERY2\""));
    }

    public void test_build_prefix() {
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase_prefix\":{\"title\":{\"query\":\"query\",\"slop\":0,\"max_expansions\":50,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase_prefix\":{\"content\":{\"query\":\"query\",\"slop\":0,\"max_expansions\":50,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("QUERY*")), //
                Set.of("QUERY"), //
                buildQuery("QUERY*"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"match_phrase_prefix\":{\"title\":{\"query\":\"query\",\"slop\":0,\"max_expansions\":50,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("title", List.of("QUERY*")), //
                Set.of("QUERY"), //
                buildQuery("title:QUERY*"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"prefix\":{\"url\":{\"value\":\"query\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("url", List.of("QUERY*")), //
                Set.of("QUERY"), //
                buildQuery("url:QUERY*"));
        assertQueryContext(buildQuery("url:QUERY*").getQueryBuilder().toString().replaceAll("\\s", ""), buildQuery("url:\"QUERY*\""));

    }

    public void test_build_escape() {
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"aaa:bbb\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"aaa:bbb\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}},{\"fuzzy\":{\"title\":{\"value\":\"aaa:bbb\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.01}}},{\"fuzzy\":{\"content\":{\"value\":\"aaa:bbb\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.005}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("aaa:bbb")), //
                Set.of("aaa:bbb"), //
                buildQuery("aaa\\:bbb"));
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"aaa:bbb\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"aaa:bbb\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                buildQuery("\"aaa\\:bbb\""));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"match_phrase\":{\"title\":{\"query\":\"aaa:bbb\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("title", List.of("aaa:bbb")), //
                Set.of("aaa:bbb"), //
                buildQuery("title:aaa\\:bbb"));
    }

    public void test_build_site() {
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"prefix\":{\"site\":{\"value\":\"fess.codelibs.org\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("site", List.of("fess.codelibs.org*")), //
                Set.of("fess.codelibs.org"), //
                buildQuery("site:fess.codelibs.org"));
    }

    public void test_build_allintitle() {
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"wildcard\":{\"title\":{\"wildcard\":\"*\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("title", List.of("*")), //
                Set.of(), //
                buildQuery("allintitle:"));
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"match_phrase\":{\"title\":{\"query\":\"test\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("title", List.of("test")), //
                Set.of("test"), //
                buildQuery("allintitle:test"));
        assertQueryContext(buildQuery("allintitle:test").getQueryBuilder().toString().replaceAll("\\s", ""),
                buildQuery("allintitle: test"));
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"must\":[{\"match_phrase\":{\"title\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}},{\"match_phrase\":{\"title\":{\"query\":\"bbb\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("title", List.of("aaa", "bbb")), //
                Set.of("aaa", "bbb"), //
                buildQuery("allintitle: aaa bbb"));
    }

    public void test_build_allinurl() {
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"wildcard\":{\"url\":{\"wildcard\":\"*\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("url", List.of("*")), //
                Set.of(), //
                buildQuery("allinurl:"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"wildcard\":{\"url\":{\"wildcard\":\"*test*\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("url", List.of("*test*")), //
                Set.of("test"), //
                buildQuery("allinurl:test"));
        assertQueryContext(buildQuery("allinurl:test").getQueryBuilder().toString().replaceAll("\\s", ""), buildQuery("allinurl: test"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"must\":[{\"wildcard\":{\"url\":{\"wildcard\":\"*aaa*\",\"boost\":1.0}}},{\"wildcard\":{\"url\":{\"wildcard\":\"*bbb*\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("url", List.of("*aaa*", "*bbb*")), //
                Set.of("aaa", "bbb"), //
                buildQuery("allinurl: aaa bbb"));
    }

    public void test_build_sort() {
        String query =
                "{\"function_score\":{\"query\":{\"match_all\":{\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}";
        assertQueryContext(query, Map.of(), //
                Set.of(), //
                "{\"timestamp\":{\"order\":\"asc\"}}", //
                buildQuery("sort:timestamp"));
        assertQueryContext(query, Map.of(), //
                Set.of(), //
                "{\"timestamp\":{\"order\":\"asc\"}}", //
                buildQuery("sort:timestamp.asc"));
        assertQueryContext(query, Map.of(), //
                Set.of(), //
                "{\"timestamp\":{\"order\":\"desc\"}}", //
                buildQuery("sort:timestamp.desc"));

        assertQueryContext(query, Map.of(), //
                Set.of(), //
                "{\"timestamp\":{\"order\":\"desc\"}}{\"last_modified\":{\"order\":\"asc\"}}", //
                buildQuery("sort:timestamp.desc sort:last_modified"));
        assertQueryContext(query, Map.of(), //
                Set.of(), //
                "{\"timestamp\":{\"order\":\"desc\"}}{\"last_modified\":{\"order\":\"desc\"}}", //
                buildQuery("sort:timestamp.desc sort:last_modified.desc"));

        try {
            buildQuery("sort:timestamp.xxx");
            fail();
        } catch (InvalidQueryException e) {
            // ok
        }

        try {
            buildQuery("sort:aaa");
            fail();
        } catch (InvalidQueryException e) {
            // ok
        }
    }

    public void test_build_fuzzy() {
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"fuzzy\":{\"title\":{\"value\":\"QUERY1\",\"fuzziness\":\"2\",\"prefix_length\":0,\"max_expansions\":50,\"transpositions\":true,\"boost\":0.5}}},{\"fuzzy\":{\"content\":{\"value\":\"QUERY1\",\"fuzziness\":\"2\",\"prefix_length\":0,\"max_expansions\":50,\"transpositions\":true,\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("QUERY1")), //
                Set.of("QUERY1"), //
                buildQuery("QUERY1~0.5"));
        assertQuery(buildQuery("QUERY1~0.5").getQueryBuilder(), //
                buildQuery("QUERY1~"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"fuzzy\":{\"title\":{\"value\":\"QUERY1\",\"fuzziness\":\"2\",\"prefix_length\":0,\"max_expansions\":50,\"transpositions\":true,\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("title", List.of("QUERY1")), //
                Set.of("QUERY1"), //
                buildQuery("title:QUERY1~0.5"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"fuzzy\":{\"title\":{\"value\":\"aaa:QUERY1\",\"fuzziness\":\"2\",\"prefix_length\":0,\"max_expansions\":50,\"transpositions\":true,\"boost\":0.5}}},{\"fuzzy\":{\"content\":{\"value\":\"aaa:QUERY1\",\"fuzziness\":\"2\",\"prefix_length\":0,\"max_expansions\":50,\"transpositions\":true,\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("aaa:QUERY1")), //
                Set.of("aaa:QUERY1"), //
                buildQuery("aaa:QUERY1~0.5"));
    }

    public void test_build_range() {
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"[aaaTObbb]\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"[aaaTObbb]\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("[aaa TO bbb]")), //
                Set.of("[aaa TO bbb]"), //
                buildQuery("[aaa TO bbb]"));
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"[*TObbb]\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"[*TObbb]\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("[* TO bbb]")), //
                Set.of("[* TO bbb]"), //
                buildQuery("[* TO bbb]"));
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"[aaaTO*]\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"[aaaTO*]\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("[aaa TO *]")), //
                Set.of("[aaa TO *]"), //
                buildQuery("[aaa TO *]"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"{aaaTObbb}\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"{aaaTObbb}\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("{aaa TO bbb}")), //
                Set.of("{aaa TO bbb}"), //
                buildQuery("{aaa TO bbb}"));
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"{*TObbb}\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"{*TObbb}\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("{* TO bbb}")), //
                Set.of("{* TO bbb}"), //
                buildQuery("{* TO bbb}"));
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"{aaaTO*}\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"{aaaTO*}\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("{aaa TO *}")), //
                Set.of("{aaa TO *}"), //
                buildQuery("{aaa TO *}"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"range\":{\"timestamp\":{\"from\":\"0\",\"to\":\"100\",\"include_lower\":true,\"include_upper\":true,\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("timestamp", List.of("[0 TO 100]")), //
                Set.of(), //
                buildQuery("timestamp:[0 TO 100]"));
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"range\":{\"timestamp\":{\"from\":null,\"to\":\"100\",\"include_lower\":true,\"include_upper\":true,\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("timestamp", List.of("[* TO 100]")), //
                Set.of(), //
                buildQuery("timestamp:[* TO 100]"));
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"range\":{\"timestamp\":{\"from\":\"0\",\"to\":null,\"include_lower\":true,\"include_upper\":true,\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("timestamp", List.of("[0 TO *]")), //
                Set.of(), //
                buildQuery("timestamp:[0 TO *]"));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"range\":{\"timestamp\":{\"from\":\"0\",\"to\":\"100\",\"include_lower\":false,\"include_upper\":false,\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("timestamp", List.of("{0 TO 100}")), //
                Set.of(), //
                buildQuery("timestamp:{0 TO 100}"));
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"range\":{\"timestamp\":{\"from\":null,\"to\":\"100\",\"include_lower\":true,\"include_upper\":false,\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("timestamp", List.of("{* TO 100}")), //
                Set.of(), //
                buildQuery("timestamp:{* TO 100}"));
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"range\":{\"timestamp\":{\"from\":\"0\",\"to\":null,\"include_lower\":false,\"include_upper\":true,\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("timestamp", List.of("{0 TO *}")), //
                Set.of(), //
                buildQuery("timestamp:{0 TO *}"));
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
        return QueryBuilders.boolQuery()//
                .should(QueryBuilders.matchPhraseQuery("title", query).boost(titleBoost))//
                .should(QueryBuilders.matchPhraseQuery("content", query).boost(contentBoost))//
                .should(QueryBuilders.fuzzyQuery("title", query).boost(0.01f).maxExpansions(10))//
                .should(QueryBuilders.fuzzyQuery("content", query).boost(0.005f).maxExpansions(10))//
        ;
    }

    private QueryBuilder functionScoreQuery(QueryBuilder queryBuilder) {
        return QueryBuilders.functionScoreQuery(queryBuilder, ScoreFunctionBuilders.fieldValueFactorFunction("boost"));
    }

    private void assertQuery(QueryBuilder query1, QueryContext context) {
        assertQuery(query1, null, null, context);
    }

    private void assertQuery(QueryBuilder query1, Map<String, List<String>> fieldLogMap, Set<String> highlightedQuerySet,
            QueryContext context) {
        assertEquals(query1.toString(), context.getQueryBuilder().toString());
        assertFieldLogs(fieldLogMap);
        assertHighlightedQueries(highlightedQuerySet);
    }

    private void assertQueryContext(String query, QueryContext context) {
        assertQueryContext(query, null, null, context);
    }

    private void assertQueryContext(String query, Map<String, List<String>> fieldLogMap, Set<String> highlightedQuerySet,
            QueryContext context) {
        assertQueryContext(query, fieldLogMap, highlightedQuerySet, null, context);
    }

    private void assertQueryContext(String query, Map<String, List<String>> fieldLogMap, Set<String> highlightedQuerySet, String sorts,
            QueryContext context) {
        assertEquals(query, context.getQueryBuilder().toString().replaceAll("\\s", ""));
        assertFieldLogs(fieldLogMap);
        assertHighlightedQueries(highlightedQuerySet);
        if (sorts != null) {
            assertEquals(sorts, context.sortBuilders().stream().map(s -> s.toString().replaceAll("\\s", "")).collect(Collectors.joining()));
        }
    }

    private void assertHighlightedQueries(Set<String> highlightedQuerySet) {
        @SuppressWarnings("unchecked")
        Set<String> set = (Set<String>) getMockRequest().getAttribute(Constants.HIGHLIGHT_QUERIES);
        if (highlightedQuerySet != null) {
            assertEquals(highlightedQuerySet.stream().sorted().collect(Collectors.joining("\n")),
                    set.stream().sorted().collect(Collectors.joining("\n")));
        }
        set.clear();
    }

    private void assertFieldLogs(Map<String, List<String>> fieldLogMap) {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> map = (Map<String, List<String>>) getMockRequest().getAttribute(Constants.FIELD_LOGS);
        if (fieldLogMap != null) {
            assertEquals(fieldLogMap, map);
        }
        map.clear();
    }

    private QueryContext buildQuery(String query) {
        return queryHelper.build(SearchRequestType.SEARCH, query, context -> {
            context.skipRoleQuery();
        });
    }

}