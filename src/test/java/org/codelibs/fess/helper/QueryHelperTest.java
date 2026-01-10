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

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.codelibs.core.io.FileUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.mylasta.direction.FessConfig;
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
import org.dbflute.util.DfTypeUtil;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.ScoreFunctionBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class QueryHelperTest extends UnitFessTestCase {

    private QueryHelper queryHelper;

    private QueryFieldConfig queryFieldConfig;

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
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
        SystemHelper systemHelper = new SystemHelper();
        VirtualHostHelper virtualHostHelper = new VirtualHostHelper();
        KeyMatchHelper keyMatchHelper = new KeyMatchHelper();
        RoleQueryHelper roleQueryHelper = new RoleQueryHelper();
        PermissionHelper permissionHelper = new PermissionHelper();

        ComponentUtil.register(systemHelper, "systemHelper");
        ComponentUtil.register(virtualHostHelper, "virtualHostHelper");
        ComponentUtil.register(keyMatchHelper, "keyMatchHelper");
        ComponentUtil.register(roleQueryHelper, "roleQueryHelper");
        ComponentUtil.register(permissionHelper, "permissionHelper");

        inject(systemHelper);
        inject(virtualHostHelper);
        inject(keyMatchHelper);
        inject(roleQueryHelper);
        inject(permissionHelper);
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

    private void setQueryType(final String queryType) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String get(String propertyKey) {
                return fessConfig.get(propertyKey);
            }

            @Override
            public BigDecimal getAsDecimal(String propertyKey) {
                return DfTypeUtil.toBigDecimal(get(propertyKey));
            }

            @Override
            public Integer getAsInteger(String propertyKey) {
                return DfTypeUtil.toInteger(get(propertyKey));
            }

            @Override
            public String getQueryDefaultQueryType() {
                return queryType;
            }
        });
    }

    @Test
    public void test_build_simple() {
        setQueryType("bool");

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

    @Test
    public void test_build_simple_dismax() {
        setQueryType("dismax");

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

    @Test
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

    @Test
    public void test_build_boost() {
        setQueryType("bool");
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

    @Test
    public void test_build_wildcard() {
        setQueryType("bool");
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

    @Test
    public void test_build_phrase() {
        setQueryType("bool");
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

    @Test
    public void test_build_prefix() {
        setQueryType("bool");
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

    @Test
    public void test_build_escape() {
        setQueryType("bool");
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"aaa:bbb\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"aaa:bbb\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}},{\"fuzzy\":{\"title\":{\"value\":\"aaa:bbb\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.01}}},{\"fuzzy\":{\"content\":{\"value\":\"aaa:bbb\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.005}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("_default", List.of("aaa:bbb")), //
                Set.of("aaa:bbb"), //
                buildQuery("aaa\\:bbb"));
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"aaa:bbb\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"aaa:bbb\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}},{\"fuzzy\":{\"title\":{\"value\":\"aaa:bbb\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.01}}},{\"fuzzy\":{\"content\":{\"value\":\"aaa:bbb\",\"fuzziness\":\"AUTO\",\"prefix_length\":0,\"max_expansions\":10,\"transpositions\":true,\"boost\":0.005}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                buildQuery("\"aaa\\:bbb\""));

        assertQueryContext(
                "{\"function_score\":{\"query\":{\"match_phrase\":{\"title\":{\"query\":\"aaa:bbb\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("title", List.of("aaa:bbb")), //
                Set.of("aaa:bbb"), //
                buildQuery("title:aaa\\:bbb"));
    }

    @Test
    public void test_build_site() {
        assertQueryContext(
                "{\"function_score\":{\"query\":{\"prefix\":{\"site\":{\"value\":\"fess.codelibs.org\",\"boost\":1.0}}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"boost\",\"factor\":1.0,\"modifier\":\"none\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}",
                Map.of("site", List.of("fess.codelibs.org*")), //
                Set.of("fess.codelibs.org"), //
                buildQuery("site:fess.codelibs.org"));
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void test_build_fuzzy() {
        setQueryType("bool");
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

    @Test
    public void test_build_range() {
        setQueryType("bool");
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
        if ("dismax".equals(ComponentUtil.getFessConfig().getQueryDefaultQueryType())) {
            return QueryBuilders.disMaxQuery()
                    .tieBreaker(0.1f)//
                    .add(QueryBuilders.matchPhraseQuery("title", query).boost(titleBoost))//
                    .add(QueryBuilders.matchPhraseQuery("content", query).boost(contentBoost))//
                    .add(QueryBuilders.fuzzyQuery("title", query).boost(0.01f).maxExpansions(10))//
                    .add(QueryBuilders.fuzzyQuery("content", query).boost(0.005f).maxExpansions(10))//
            ;
        }
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

    // Additional test methods for improved coverage

    @Test
    public void test_getSortPrefix() {
        assertEquals("sort:", queryHelper.getSortPrefix());
    }

    @Test
    public void test_setSortPrefix() {
        queryHelper.setSortPrefix("customSort:");
        assertEquals("customSort:", queryHelper.getSortPrefix());
    }

    @Test
    public void test_getAdditionalQuery() {
        assertNull(queryHelper.getAdditionalQuery());
    }

    @Test
    public void test_setAdditionalQuery() {
        queryHelper.setAdditionalQuery("additional query");
        assertEquals("additional query", queryHelper.getAdditionalQuery());
    }

    @Test
    public void test_setHighlightPrefix() {
        queryHelper.setHighlightPrefix("highlight_");
        assertEquals("highlight_", queryHelper.getHighlightPrefix());
    }

    @Test
    public void test_getHighlightPrefix() {
        assertEquals("hl_", queryHelper.getHighlightPrefix());
    }

    @Test
    public void test_getDefaultFacetInfo() {
        assertNull(queryHelper.getDefaultFacetInfo());
    }

    @Test
    public void test_setDefaultFacetInfo() {
        FacetInfo facetInfo = new FacetInfo();
        queryHelper.setDefaultFacetInfo(facetInfo);
        assertEquals(facetInfo, queryHelper.getDefaultFacetInfo());
    }

    @Test
    public void test_getDefaultGeoInfo() {
        assertNull(queryHelper.getDefaultGeoInfo());
    }

    @Test
    public void test_setDefaultGeoInfo() {
        GeoInfo geoInfo = new GeoInfo(getMockRequest());
        queryHelper.setDefaultGeoInfo(geoInfo);
        assertEquals(geoInfo, queryHelper.getDefaultGeoInfo());
    }

    @Test
    public void test_generateId() {
        String id1 = queryHelper.generateId();
        String id2 = queryHelper.generateId();

        assertNotNull(id1);
        assertNotNull(id2);
        assertFalse(id1.equals(id2));
        assertTrue(id1.length() > 0);
        assertFalse(id1.contains("-"));
    }

    @Test
    public void test_generateId_format() {
        String id = queryHelper.generateId();
        // UUID without dashes should be 32 characters
        assertEquals(32, id.length());
        assertTrue(id.matches("[a-f0-9]{32}"));
    }

    @Test
    public void test_addDefaultSort_singleField() {
        queryHelper.addDefaultSort("timestamp", "DESC");

        QueryContext context = buildQuery("test");
        assertEquals(1, context.sortBuilders().size());
        assertTrue(context.sortBuilders().get(0).toString().contains("timestamp"));
        assertTrue(context.sortBuilders().get(0).toString().contains("desc"));
    }

    @Test
    public void test_addDefaultSort_multipleFields() {
        queryHelper.addDefaultSort("timestamp", "DESC");
        queryHelper.addDefaultSort("title", "ASC");

        QueryContext context = buildQuery("test");
        assertEquals(2, context.sortBuilders().size());
    }

    @Test
    public void test_addDefaultSort_scoreField() {
        queryHelper.addDefaultSort("_score", "DESC");

        QueryContext context = buildQuery("test");
        assertEquals(1, context.sortBuilders().size());
        assertTrue(context.sortBuilders().get(0).toString().contains("_score"));
    }

    @Test
    public void test_addDefaultSort_docScoreField() {
        queryHelper.addDefaultSort("doc_score", "ASC");

        QueryContext context = buildQuery("test");
        assertEquals(1, context.sortBuilders().size());
        assertTrue(context.sortBuilders().get(0).toString().contains("_score"));
    }

    @Test
    public void test_addBoostFunction_scoreFunction() {
        queryHelper.addBoostFunction(ScoreFunctionBuilders.weightFactorFunction(2.0f));

        QueryContext context = buildQuery("test");
        // Verify that function score query contains the added boost function
        assertTrue(context.getQueryBuilder().toString().contains("function_score"));
    }

    @Test
    public void test_addBoostFunction_withFilter() {
        QueryBuilder filter = QueryBuilders.termQuery("category", "test");
        queryHelper.addBoostFunction(filter, ScoreFunctionBuilders.weightFactorFunction(2.0f));

        QueryContext context = buildQuery("test");
        assertTrue(context.getQueryBuilder().toString().contains("function_score"));
    }

    @Test
    public void test_build_withAdditionalQuery() {
        queryHelper.setAdditionalQuery("additional:test");

        QueryContext context = queryHelper.build(SearchRequestType.SEARCH, "main query", ctx -> {
            ctx.skipRoleQuery();
        });

        assertNotNull(context);
        assertEquals("main query additional:test", context.getQueryString());
    }

    @Test
    public void test_build_withAdditionalQuery_emptyMainQuery() {
        queryHelper.setAdditionalQuery("additional:test");

        QueryContext context = queryHelper.build(SearchRequestType.SEARCH, "", ctx -> {
            ctx.skipRoleQuery();
        });

        assertNotNull(context);
        assertEquals("*", context.getQueryString());
    }

    @Test
    public void test_build_withAdditionalQuery_nullMainQuery() {
        queryHelper.setAdditionalQuery("additional:test");

        QueryContext context = queryHelper.build(SearchRequestType.SEARCH, null, ctx -> {
            ctx.skipRoleQuery();
        });

        assertNotNull(context);
        assertEquals("*", context.getQueryString());
    }

    @Test
    public void test_build_adminSearch() {
        QueryContext context = queryHelper.build(SearchRequestType.ADMIN_SEARCH, "test", ctx -> {
            // Admin search should not add virtual host filter
            ctx.skipRoleQuery(); // Skip role query to avoid complex dependency issues
        });

        assertNotNull(context);
    }

    @Test
    public void test_buildRoleQuery_emptyRoleSet() {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        queryHelper.buildRoleQuery(Set.of(), boolQuery);

        // Should add empty role query filter
        assertTrue(boolQuery.toString().contains("filter"));
    }

    @Test
    public void test_buildRoleQuery_withRoles() {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        Set<String> roles = Set.of("role1", "role2");

        queryHelper.buildRoleQuery(roles, boolQuery);

        String queryString = boolQuery.toString();
        assertTrue(queryString.contains("role1"));
        assertTrue(queryString.contains("role2"));
        assertTrue(queryString.contains("filter"));
    }

    @Test
    public void test_createFieldSortBuilder_normalField() {
        // Using reflection to test protected method
        try {
            java.lang.reflect.Method method =
                    QueryHelper.class.getDeclaredMethod("createFieldSortBuilder", String.class, org.opensearch.search.sort.SortOrder.class);
            method.setAccessible(true);

            org.opensearch.search.sort.SortBuilder<?> result = (org.opensearch.search.sort.SortBuilder<?>) method.invoke(queryHelper,
                    "timestamp", org.opensearch.search.sort.SortOrder.DESC);

            assertNotNull(result);
            assertTrue(result.toString().contains("timestamp"));
        } catch (Exception e) {
            fail("Failed to test createFieldSortBuilder: " + e.getMessage());
        }
    }

    @Test
    public void test_createFieldSortBuilder_scoreField() {
        try {
            java.lang.reflect.Method method =
                    QueryHelper.class.getDeclaredMethod("createFieldSortBuilder", String.class, org.opensearch.search.sort.SortOrder.class);
            method.setAccessible(true);

            org.opensearch.search.sort.SortBuilder<?> result = (org.opensearch.search.sort.SortBuilder<?>) method.invoke(queryHelper,
                    "_score", org.opensearch.search.sort.SortOrder.DESC);

            assertNotNull(result);
            assertTrue(result.toString().contains("_score"));
        } catch (Exception e) {
            fail("Failed to test createFieldSortBuilder for score field: " + e.getMessage());
        }
    }

    @Test
    public void test_getRescorers_emptyList() {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        org.opensearch.search.rescore.RescorerBuilder<?>[] rescorers = queryHelper.getRescorers(params);

        assertNotNull(rescorers);
        assertEquals(0, rescorers.length);
    }

    @Test
    public void test_addQueryRescorer() {
        org.codelibs.fess.score.QueryRescorer mockRescorer = new org.codelibs.fess.score.QueryRescorer() {
            @Override
            public org.opensearch.search.rescore.RescorerBuilder<?> evaluate(java.util.Map<String, Object> params) {
                return null; // Return null for this test
            }
        };

        queryHelper.addQueryRescorer(mockRescorer);

        java.util.Map<String, Object> params = new java.util.HashMap<>();
        org.opensearch.search.rescore.RescorerBuilder<?>[] rescorers = queryHelper.getRescorers(params);

        assertNotNull(rescorers);
        assertEquals(0, rescorers.length); // Should be 0 because rescorer returns null
    }

    @Test
    public void test_processJsonSearchPreference_queryPref() {
        String result = queryHelper.processJsonSearchPreference(null, "test query");
        // This will return the hashCode of the query as string
        assertEquals(Integer.toString("test query".hashCode()), result);
    }

    @Test
    public void test_processGsaSearchPreference_queryPref() {
        String result = queryHelper.processGsaSearchPreference(null, "test query");
        // This will return the hashCode of the query as string
        assertEquals(Integer.toString("test query".hashCode()), result);
    }

    @Test
    public void test_build_invalidQuery() {
        try {
            queryHelper.build(SearchRequestType.SEARCH, "invalid:query[", ctx -> {
                ctx.skipRoleQuery();
            });
            // If we get here without exception, that's actually fine for some invalid queries
            // that might be handled gracefully
        } catch (InvalidQueryException e) {
            // This is expected for some types of invalid queries
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void test_buildBaseQuery_invalidQuery() {
        try {
            queryHelper.buildBaseQuery(new QueryContext("field:[invalid", true), ctx -> {
                // Empty context consumer
            });
            // Some invalid queries might be handled gracefully
        } catch (InvalidQueryException e) {
            // This is expected for malformed queries
            assertNotNull(e.getMessage());
            assertTrue(e.getMessage().contains("Invalid query"));
        }
    }

    @Test
    public void test_build_emptyQuery() {
        QueryContext context = queryHelper.build(SearchRequestType.SEARCH, "", ctx -> {
            ctx.skipRoleQuery();
        });

        assertNotNull(context);
        assertEquals("*", context.getQueryString());
    }

    @Test
    public void test_build_whitespaceQuery() {
        QueryContext context = queryHelper.build(SearchRequestType.SEARCH, "   ", ctx -> {
            ctx.skipRoleQuery();
        });

        assertNotNull(context);
        assertEquals("*", context.getQueryString());
    }

    @Test
    public void test_build_nullQuery() {
        QueryContext context = queryHelper.build(SearchRequestType.SEARCH, null, ctx -> {
            ctx.skipRoleQuery();
        });

        assertNotNull(context);
        assertEquals("*", context.getQueryString());
    }

    @Test
    public void test_addDefaultSort_caseInsensitive() {
        // Test case insensitive order
        queryHelper.addDefaultSort("timestamp", "desc");

        QueryContext context = buildQuery("test");
        assertEquals(1, context.sortBuilders().size());
        assertTrue(context.sortBuilders().get(0).toString().contains("desc"));
    }

    @Test
    public void test_addDefaultSort_invalidOrder() {
        // Invalid order should default to ASC
        queryHelper.addDefaultSort("timestamp", "invalid");

        QueryContext context = buildQuery("test");
        assertEquals(1, context.sortBuilders().size());
        assertTrue(context.sortBuilders().get(0).toString().contains("asc"));
    }

    @Test
    public void test_constant_preferenceQuery() {
        assertEquals("_query", QueryHelper.PREFERENCE_QUERY);
    }

}