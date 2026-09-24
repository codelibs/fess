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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.QueryContext;
import org.codelibs.fess.query.parser.QueryParser;
import org.codelibs.fesen.opensearch.index.query.BoolQueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilder;
import org.codelibs.fesen.opensearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;

public class MarkedQueryCommandTest extends QueryTestBase {

    @Override
    protected void setUpChild() throws Exception {
        new TermQueryCommand().register();
        new BooleanQueryCommand().register();
        new BoostQueryCommand().register();
        new MarkedQueryCommand().register();
    }

    /** Marks a negated bare term whose text is one of the given words, like a "!g" bang. */
    static class NegatedWordMarker extends QueryMarker {
        private final Set<String> words;

        NegatedWordMarker(final int priority, final String... words) {
            this.priority = priority;
            this.words = Set.of(words);
        }

        @Override
        public boolean matches(final Occur occur, final Query query) {
            return occur == Occur.MUST_NOT && query instanceof final TermQuery termQuery
                    && Constants.DEFAULT_FIELD.equals(termQuery.getTerm().field()) && words.contains(termQuery.getTerm().text());
        }
    }

    private QueryParser parser(final QueryMarker... markers) {
        final QueryParser queryParser = new QueryParser();
        queryParser.init();
        for (final QueryMarker marker : markers) {
            queryParser.addMarker(marker);
        }
        return queryParser;
    }

    @Test
    public void test_parse_withoutMarkers() {
        final Query query = parser().parse("airplane !g");
        assertEquals(0, MarkedQuery.collect(query).size());
        assertEquals("+_default:airplane -_default:g", query.toString());
    }

    @Test
    public void test_parse_marksNegatedTerm() {
        final NegatedWordMarker marker = new NegatedWordMarker(99, "g");
        final Query query = parser(marker).parse("airplane !g");
        final List<BooleanClause> clauses = ((BooleanQuery) query).clauses();
        assertEquals(TermQuery.class, clauses.get(0).query().getClass());
        final MarkedQuery marked = (MarkedQuery) clauses.get(1).query();
        assertSame(marker, marked.getMarker());
        assertEquals(Occur.MUST_NOT, marked.getOccur());
        assertEquals("_default:g", marked.getQuery().toString());
        // prints as the original query
        assertEquals("+_default:airplane -_default:g", query.toString());
    }

    @Test
    public void test_parse_minusAndNotAreTheSameClause() {
        final QueryParser queryParser = parser(new NegatedWordMarker(99, "g"));
        assertEquals(1, MarkedQuery.collect(queryParser.parse("airplane -g")).size());
        assertEquals(1, MarkedQuery.collect(queryParser.parse("airplane NOT g")).size());
    }

    @Test
    public void test_parse_occurIsChecked() {
        final QueryParser queryParser = parser(new NegatedWordMarker(99, "g"));
        assertEquals(0, MarkedQuery.collect(queryParser.parse("airplane g")).size());
        assertEquals(0, MarkedQuery.collect(queryParser.parse("g")).size());
        assertEquals(0, MarkedQuery.collect(queryParser.parse("airplane !w")).size());
    }

    @Test
    public void test_parse_boostKeepsWrapping() {
        final Query query = parser(new NegatedWordMarker(99, "g")).parse("airplane !g^2");
        final BoostQuery boostQuery = (BoostQuery) ((BooleanQuery) query).clauses().get(1).query();
        assertEquals(2.0f, boostQuery.getBoost());
        assertEquals(MarkedQuery.class, boostQuery.getQuery().getClass());
    }

    @Test
    public void test_parse_priorityDecidesTheMarker() {
        final NegatedWordMarker low = new NegatedWordMarker(10, "g");
        final NegatedWordMarker high = new NegatedWordMarker(5, "g");
        final QueryParser queryParser = parser(low, high);
        assertEquals(List.of(high, low), queryParser.getMarkers());
        final List<MarkedQuery> marked = MarkedQuery.collect(queryParser.parse("airplane !g"));
        assertEquals(1, marked.size());
        assertSame(high, marked.get(0).getMarker());
    }

    @Test
    public void test_addMarker_concurrentWithParse() throws Exception {
        final QueryParser queryParser = parser();
        final int perThread = 500;
        final ExecutorService executor = Executors.newFixedThreadPool(3);
        try {
            final List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < perThread; j++) {
                        queryParser.addMarker(new NegatedWordMarker(j, "g"));
                    }
                }));
            }
            futures.add(executor.submit(() -> {
                for (int j = 0; j < perThread; j++) {
                    final List<MarkedQuery> marked = MarkedQuery.collect(queryParser.parse("airplane !g"));
                    assertTrue(marked.size() <= 1);
                }
            }));
            for (final Future<?> future : futures) {
                future.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
        }
        assertEquals(perThread * 2, queryParser.getMarkers().size());
        assertEquals(1, MarkedQuery.collect(queryParser.parse("airplane !g")).size());
    }

    @Test
    public void test_collect_inQueryOrder() {
        final List<MarkedQuery> marked = MarkedQuery.collect(parser(new NegatedWordMarker(99, "g", "w")).parse("!w airplane (foo !g)"));
        assertEquals(2, marked.size());
        assertEquals("_default:w", marked.get(0).getQuery().toString());
        assertEquals("_default:g", marked.get(1).getQuery().toString());
    }

    @Test
    public void test_execute_defaultDropsClause() {
        final QueryParser queryParser = parser(new NegatedWordMarker(99, "g"));
        final QueryContext context = new QueryContext("airplane !g", false);
        final QueryBuilder builder = queryProcessor.execute(context, queryParser.parse("airplane !g"), 1.0f);
        final BoolQueryBuilder boolQuery = (BoolQueryBuilder) builder;
        assertEquals(1, boolQuery.must().size());
        assertEquals(0, boolQuery.mustNot().size());

        assertNull(queryProcessor.execute(context, queryParser.parse("!g"), 1.0f));
    }

    @Test
    public void test_execute_markerConvertsClause() {
        final QueryParser queryParser = parser(new NegatedWordMarker(99, "g") {
            @Override
            public QueryBuilder execute(final QueryContext context, final MarkedQuery query, final float boost) {
                return QueryBuilders.termQuery("label", "x");
            }
        });
        final QueryContext context = new QueryContext("airplane !g", false);
        final BoolQueryBuilder boolQuery = (BoolQueryBuilder) queryProcessor.execute(context, queryParser.parse("airplane !g"), 1.0f);
        assertEquals(1, boolQuery.mustNot().size());
        assertEquals(QueryBuilders.termQuery("label", "x"), boolQuery.mustNot().get(0));
    }

    @Test
    public void test_markedQuery_equality() {
        final NegatedWordMarker marker = new NegatedWordMarker(99, "g");
        final TermQuery termQuery = new TermQuery(new Term(Constants.DEFAULT_FIELD, "g"));
        final MarkedQuery a = new MarkedQuery(marker, Occur.MUST_NOT, termQuery);
        assertEquals(a, new MarkedQuery(marker, Occur.MUST_NOT, termQuery));
        assertEquals(a.hashCode(), new MarkedQuery(marker, Occur.MUST_NOT, termQuery).hashCode());
        assertFalse(a.equals(new MarkedQuery(marker, Occur.MUST, termQuery)));
        assertFalse(a.equals(new MarkedQuery(new NegatedWordMarker(99, "g"), Occur.MUST_NOT, termQuery)));
    }
}
