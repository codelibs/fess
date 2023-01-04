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
package org.codelibs.fess.query;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.query.parser.QueryParser;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MatchPhraseQueryBuilder;
import org.opensearch.index.query.PrefixQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.query.WildcardQueryBuilder;
import org.opensearch.search.sort.SortBuilder;

public class TermQueryCommandTest extends UnitFessTestCase {
    private static final Logger logger = LogManager.getLogger(TermQueryCommandTest.class);

    private TermQueryCommand queryCommand;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        QueryFieldConfig queryFieldConfig = new QueryFieldConfig();
        queryFieldConfig.init();
        ComponentUtil.register(queryFieldConfig, "queryFieldConfig");

        QueryParser queryParser = new QueryParser();
        queryParser.init();
        ComponentUtil.register(queryParser, "queryParser");

        queryCommand = new TermQueryCommand();
    }

    @Override
    public void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    public void test_convertTermQuery() throws Exception {
        assertQueryBuilder(BoolQueryBuilder.class,
                "{\"bool\":{\"should\":[{\"match_phrase\":{\"title\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.5}}},{\"match_phrase\":{\"content\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":0.05}}}],\"adjust_pure_negative\":true,\"boost\":1.0}}",
                "aaa");
        assertQueryBuilder(MatchPhraseQueryBuilder.class,
                "{\"match_phrase\":{\"title\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}}", //
                "title:aaa");
        assertQueryBuilder(MatchPhraseQueryBuilder.class,
                "{\"match_phrase\":{\"content\":{\"query\":\"aaa\",\"slop\":0,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}}", //
                "content:aaa");
        assertQueryBuilder(BoolQueryBuilder.class,
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