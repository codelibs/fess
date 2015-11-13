/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import org.apache.lucene.queryparser.classic.ParseException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;

public class QueryHelperTest extends UnitFessTestCase {

    private QueryHelper queryHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queryHelper = new QueryHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        registerMockInstance(fessConfig);
        registerMockInstance(new SystemHelper());
        inject(queryHelper);
        queryHelper.init();
    }

    public void test_dummy() throws ParseException {
        System.out.println(queryHelper);
    }

    //    public void test_build() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("", queryHelper.buildQuery("").getQuery());
    //
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildQuery("QUERY").getQuery());
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildQuery("QUERY ").getQuery());
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildQuery(" QUERY").getQuery());
    //
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1 QUERY2").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1 QUERY2 ").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery(" QUERY1 QUERY2").getQuery());
    //
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2", queryHelper.buildQuery("\"QUERY1 QUERY2\"").getQuery());
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2", queryHelper.buildQuery("\"QUERY1 QUERY2\" ").getQuery());
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2", queryHelper.buildQuery(" \"QUERY1 QUERY2\"").getQuery());
    //
    //            assertEquals("(title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2) " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildQuery("\"QUERY1 QUERY2\" QUERY3").getQuery());
    //        }
    //    }
    //
    //    public void test_build_fullwidthSpace() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1\u3000QUERY2").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1\u3000QUERY2\u3000").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("\u3000QUERY1\u3000QUERY2").getQuery());
    //
    //            assertEquals("title:QUERY1\\\u3000QUERY2 OR content:QUERY1\\\u3000QUERY2",
    //                    queryHelper.buildQuery("\"QUERY1\u3000QUERY2\"").getQuery());
    //
    //            assertEquals("(title:QUERY1\\\u3000QUERY2 OR content:QUERY1\\\u3000QUERY2) " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildQuery("\"QUERY1\u3000QUERY2\"\u3000QUERY3").getQuery());
    //        }
    //    }
    //
    //    public void test_build_prefix() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("mimetype:QUERY1", queryHelper.buildQuery("mimetype:QUERY1").getQuery());
    //            assertEquals("mimetype:QUERY1 " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("mimetype:QUERY1 QUERY2").getQuery());
    //            assertEquals("mimetype:QUERY1 " + op + " (title:QUERY2 OR content:QUERY2) " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildQuery("mimetype:QUERY1 QUERY2 QUERY3").getQuery());
    //            assertEquals("mimetype:QUERY1 " + op + " host:QUERY2 " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildQuery("mimetype:QUERY1 host:QUERY2 QUERY3").getQuery());
    //            assertEquals("mimetype:QUERY1\\ QUERY2 " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildQuery("mimetype:\"QUERY1 QUERY2\" QUERY3").getQuery());
    //        }
    //    }
    //
    //    public void test_build_prefix_unknown() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("title:site\\: OR content:site\\:", queryHelper.buildQuery("site:").getQuery());
    //            assertEquals("title:hoge\\:QUERY1 OR content:hoge\\:QUERY1", queryHelper.buildQuery("hoge:QUERY1").getQuery());
    //            assertEquals("(title:hoge\\:QUERY1 OR content:hoge\\:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("hoge:QUERY1 QUERY2").getQuery());
    //            assertEquals("(title:hoge\\:QUERY1 OR content:hoge\\:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2) " + op
    //                    + " (title:QUERY3 OR content:QUERY3)", queryHelper.buildQuery("hoge:QUERY1 QUERY2 QUERY3").getQuery());
    //            assertEquals(
    //                    "(title:hoge\\:QUERY1 OR content:hoge\\:QUERY1) " + op + " host:QUERY2 " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildQuery("hoge:QUERY1 host:QUERY2 QUERY3").getQuery());
    //            assertEquals("(title:hoge\\:QUERY1\\ QUERY2 OR content:hoge\\:QUERY1\\ QUERY2) " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildQuery("hoge:\"QUERY1 QUERY2\" QUERY3").getQuery());
    //        }
    //    }
    //
    //    public void test_build_roleType() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            queryHelper.roleQueryHelper = new RoleQueryHelper() {
    //                @Override
    //                public Set<String> build() {
    //                    final Set<String> list = new LinkedHashSet<>();
    //                    list.add("guest");
    //                    return list;
    //                }
    //            };
    //
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("", queryHelper.build("", true).getQuery());
    //
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.build("QUERY", true).getQuery());
    //            assertEquals("role:guest", queryHelper.build("QUERY", true).getFilterQueries()[0]);
    //
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.build("QUERY1 QUERY2", true).getQuery());
    //            assertEquals("role:guest", queryHelper.build("QUERY1 QUERY2", true).getFilterQueries()[0]);
    //
    //            queryHelper.roleQueryHelper = new RoleQueryHelper() {
    //                @Override
    //                public Set<String> build() {
    //                    final Set<String> list = new LinkedHashSet<>();
    //                    list.add("guest");
    //                    list.add("admin");
    //                    return list;
    //                }
    //            };
    //
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.build("QUERY", true).getQuery());
    //            assertEquals("role:guest OR role:admin", queryHelper.build("QUERY", true).getFilterQueries()[0]);
    //
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.build("QUERY1 QUERY2", true).getQuery());
    //            assertEquals("role:guest OR role:admin", queryHelper.build("QUERY1 QUERY2", true).getFilterQueries()[0]);
    //        }
    //    }
    //
    //    public void test_sortField() {
    //        String query;
    //        QueryContext queryContext;
    //
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            query = "";
    //            queryContext = queryHelper.buildQuery(query);
    //            assertEquals("", queryContext.getQuery());
    //            assertEquals(0, queryContext.getSortFields().length);
    //
    //            query = "sort:content_length";
    //            queryContext = queryHelper.buildQuery(query);
    //            assertEquals("", queryContext.getQuery());
    //            assertEquals(1, queryContext.getSortFields().length);
    //            assertEquals("content_length", queryContext.getSortFields()[0].getField());
    //            assertEquals(Constants.ASC, queryContext.getSortFields()[0].getOrder());
    //
    //            query = "sort:content_length.desc";
    //            queryContext = queryHelper.buildQuery(query);
    //            assertEquals("", queryContext.getQuery());
    //            assertEquals(1, queryContext.getSortFields().length);
    //            assertEquals("content_length", queryContext.getSortFields()[0].getField());
    //            assertEquals(Constants.DESC, queryContext.getSortFields()[0].getOrder());
    //
    //            query = "sort:content_length.asc,last_modified";
    //            queryContext = queryHelper.buildQuery(query);
    //            assertEquals("", queryContext.getQuery());
    //            assertEquals(2, queryContext.getSortFields().length);
    //            assertEquals("content_length", queryContext.getSortFields()[0].getField());
    //            assertEquals(Constants.ASC, queryContext.getSortFields()[0].getOrder());
    //            assertEquals("last_modified", queryContext.getSortFields()[1].getField());
    //            assertEquals(Constants.ASC, queryContext.getSortFields()[1].getOrder());
    //
    //            query = "QUERY sort:content_length";
    //            queryContext = queryHelper.buildQuery(query);
    //            assertEquals("title:QUERY OR content:QUERY", queryContext.getQuery());
    //            assertEquals(1, queryContext.getSortFields().length);
    //            assertEquals("content_length", queryContext.getSortFields()[0].getField());
    //            assertEquals(Constants.ASC, queryContext.getSortFields()[0].getOrder());
    //
    //            query = "QUERY sort:content_length.desc";
    //            queryContext = queryHelper.buildQuery(query);
    //            assertEquals("title:QUERY OR content:QUERY", queryContext.getQuery());
    //            assertEquals(1, queryContext.getSortFields().length);
    //            assertEquals("content_length", queryContext.getSortFields()[0].getField());
    //            assertEquals(Constants.DESC, queryContext.getSortFields()[0].getOrder());
    //
    //            query = "QUERY sort:content_length.asc,last_modified";
    //            queryContext = queryHelper.buildQuery(query);
    //            assertEquals("title:QUERY OR content:QUERY", queryContext.getQuery());
    //            assertEquals(2, queryContext.getSortFields().length);
    //            assertEquals("content_length", queryContext.getSortFields()[0].getField());
    //            assertEquals(Constants.ASC, queryContext.getSortFields()[0].getOrder());
    //            assertEquals("last_modified", queryContext.getSortFields()[1].getField());
    //            assertEquals(Constants.ASC, queryContext.getSortFields()[1].getOrder());
    //
    //            query = "QUERY mimetype:QUERY1 sort:content_length";
    //            queryContext = queryHelper.buildQuery(query);
    //            assertEquals("(title:QUERY OR content:QUERY) " + op + " mimetype:QUERY1", queryContext.getQuery());
    //            assertEquals(1, queryContext.getSortFields().length);
    //            assertEquals("content_length", queryContext.getSortFields()[0].getField());
    //            assertEquals(Constants.ASC, queryContext.getSortFields()[0].getOrder());
    //
    //            query = "QUERY sort:content_length.desc  mimetype:QUERY1";
    //            queryContext = queryHelper.buildQuery(query);
    //            assertEquals("(title:QUERY OR content:QUERY) " + op + " mimetype:QUERY1", queryContext.getQuery());
    //            assertEquals(1, queryContext.getSortFields().length);
    //            assertEquals("content_length", queryContext.getSortFields()[0].getField());
    //            assertEquals(Constants.DESC, queryContext.getSortFields()[0].getOrder());
    //
    //            query = "QUERY sort:content_length.asc,last_modified mimetype:QUERY1";
    //            queryContext = queryHelper.buildQuery(query);
    //            assertEquals("(title:QUERY OR content:QUERY) " + op + " mimetype:QUERY1", queryContext.getQuery());
    //            assertEquals(2, queryContext.getSortFields().length);
    //            assertEquals("content_length", queryContext.getSortFields()[0].getField());
    //            assertEquals(Constants.ASC, queryContext.getSortFields()[0].getOrder());
    //            assertEquals("last_modified", queryContext.getSortFields()[1].getField());
    //            assertEquals(Constants.ASC, queryContext.getSortFields()[1].getOrder());
    //        }
    //    }
    //
    //    public void test_sortField_invalid() {
    //        String query;
    //        QueryContext queryContext;
    //
    //        query = "sort:hoge";
    //        queryContext = queryHelper.buildQuery(query);
    //        assertEquals("", queryContext.getQuery());
    //        assertEquals(0, queryContext.getSortFields().length);
    //
    //        query = "sort:content_length.hoge";
    //        queryContext = queryHelper.buildQuery(query);
    //        assertEquals("", queryContext.getQuery());
    //        assertEquals(0, queryContext.getSortFields().length);
    //
    //        query = "sort:content_length.asc,hoge";
    //        queryContext = queryHelper.buildQuery(query);
    //        assertEquals("", queryContext.getQuery());
    //        assertEquals(1, queryContext.getSortFields().length);
    //        assertEquals("content_length", queryContext.getSortFields()[0].getField());
    //        assertEquals(Constants.ASC, queryContext.getSortFields()[0].getOrder());
    //    }
    //
    //    public void test_wildcardSearches() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            // *
    //
    //            assertEquals("title:\\* OR content:\\*", queryHelper.buildQuery("*").getQuery());
    //            assertEquals("title:QUERY* OR content:QUERY*", queryHelper.buildQuery("QUERY* ").getQuery());
    //            assertEquals("title:Q*ERY OR content:Q*ERY", queryHelper.buildQuery(" Q*ERY").getQuery());
    //
    //            assertEquals("(title:Q*ERY1 OR content:Q*ERY1) " + op + " (title:Q*ERY2 OR content:Q*ERY2)",
    //                    queryHelper.buildQuery("Q*ERY1 Q*ERY2").getQuery());
    //
    //            assertEquals("title:Q*ERY1\\ Q*ERY2 OR content:Q*ERY1\\ Q*ERY2", queryHelper.buildQuery("\"Q*ERY1 Q*ERY2\"").getQuery());
    //
    //            assertEquals("(title:Q*ERY1\\ Q*ERY2 OR content:Q*ERY1\\ Q*ERY2) " + op + " (title:Q*ERY3 OR content:Q*ERY3)",
    //                    queryHelper.buildQuery("\"Q*ERY1 Q*ERY2\" Q*ERY3").getQuery());
    //
    //            assertEquals("mimetype:QUERY1*", queryHelper.buildQuery("mimetype:QUERY1*").getQuery());
    //            assertEquals("mimetype:QUERY1* " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("mimetype:QUERY1* QUERY2").getQuery());
    //
    //            // ?
    //
    //            assertEquals("title:\\? OR content:\\?", queryHelper.buildQuery("?").getQuery());
    //            assertEquals("title:QUERY? OR content:QUERY?", queryHelper.buildQuery("QUERY? ").getQuery());
    //            assertEquals("title:Q?ERY OR content:Q?ERY", queryHelper.buildQuery(" Q?ERY").getQuery());
    //
    //            assertEquals("(title:Q?ERY1 OR content:Q?ERY1) " + op + " (title:Q?ERY2 OR content:Q?ERY2)",
    //                    queryHelper.buildQuery("Q?ERY1 Q?ERY2").getQuery());
    //
    //            assertEquals("title:Q?ERY1\\ Q?ERY2 OR content:Q?ERY1\\ Q?ERY2", queryHelper.buildQuery("\"Q?ERY1 Q?ERY2\"").getQuery());
    //
    //            assertEquals("(title:Q?ERY1\\ Q?ERY2 OR content:Q?ERY1\\ Q?ERY2) " + op + " (title:Q?ERY3 OR content:Q?ERY3)",
    //                    queryHelper.buildQuery("\"Q?ERY1 Q?ERY2\" Q?ERY3").getQuery());
    //
    //            assertEquals("mimetype:QUERY1?", queryHelper.buildQuery("mimetype:QUERY1?").getQuery());
    //            assertEquals("mimetype:QUERY1? " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("mimetype:QUERY1? QUERY2").getQuery());
    //        }
    //    }
    //
    //    public void test_fuzzySearches() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            // ~
    //
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildQuery("QUERY~").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1~ QUERY2").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1~ QUERY2~").getQuery());
    //
    //            assertEquals("mimetype:QUERY1~", queryHelper.buildQuery("mimetype:QUERY1~").getQuery());
    //            assertEquals("mimetype:QUERY1~ " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("mimetype:QUERY1~ QUERY2").getQuery());
    //
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2", queryHelper.buildQuery("\"QUERY1 QUERY2\"~").getQuery());
    //            assertEquals("title:QUERY1 OR content:QUERY1", queryHelper.buildQuery("\"QUERY1~\"").getQuery());
    //
    //            // ~0.8
    //
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildQuery("QUERY~0.8").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1~0.8 QUERY2").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1~0.5 QUERY2~0.8").getQuery());
    //
    //            assertEquals("mimetype:QUERY1~0.8", queryHelper.buildQuery("mimetype:QUERY1~0.8").getQuery());
    //            assertEquals("mimetype:QUERY1~0.8 " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("mimetype:QUERY1~0.8 QUERY2").getQuery());
    //
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2", queryHelper.buildQuery("\"QUERY1 QUERY2\"~0.8").getQuery());
    //            assertEquals("title:QUERY1 OR content:QUERY1", queryHelper.buildQuery("\"QUERY1~0.8\"").getQuery());
    //
    //            assertEquals("title:QUERY1 OR content:QUERY1", queryHelper.buildQuery("\"QUERY1~0.8a\"").getQuery());
    //            assertEquals("title:QUERY1 OR content:QUERY1", queryHelper.buildQuery("\"QUERY1~a\"").getQuery());
    //        }
    //
    //        getMockRequest().setLocale(Locale.JAPANESE);
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            // ~
    //
    //            assertEquals("title:QUERY OR content:QUERY OR content_ja:QUERY~", queryHelper.buildQuery("QUERY~").getQuery());
    //            assertEquals(
    //                    "(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~) " + op
    //                            + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1~ QUERY2").getQuery());
    //            assertEquals(
    //                    "(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~) " + op
    //                            + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2~)",
    //                    queryHelper.buildQuery("QUERY1~ QUERY2~").getQuery());
    //
    //            assertEquals("mimetype:QUERY1~", queryHelper.buildQuery("mimetype:QUERY1~").getQuery());
    //            assertEquals("mimetype:QUERY1~ " + op + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
    //                    queryHelper.buildQuery("mimetype:QUERY1~ QUERY2").getQuery());
    //
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2 OR content_ja:QUERY1\\ QUERY2~",
    //                    queryHelper.buildQuery("\"QUERY1 QUERY2\"~").getQuery());
    //            assertEquals("title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~", queryHelper.buildQuery("\"QUERY1~\"").getQuery());
    //
    //            // ~0.8
    //
    //            assertEquals("title:QUERY OR content:QUERY OR content_ja:QUERY~0.8", queryHelper.buildQuery("QUERY~0.8").getQuery());
    //            assertEquals(
    //                    "(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~0.8) " + op
    //                            + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1~0.8 QUERY2").getQuery());
    //            assertEquals(
    //                    "(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~0.5) " + op
    //                            + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2~0.8)",
    //                    queryHelper.buildQuery("QUERY1~0.5 QUERY2~0.8").getQuery());
    //
    //            assertEquals("mimetype:QUERY1~0.8", queryHelper.buildQuery("mimetype:QUERY1~0.8").getQuery());
    //            assertEquals("mimetype:QUERY1~0.8 " + op + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
    //                    queryHelper.buildQuery("mimetype:QUERY1~0.8 QUERY2").getQuery());
    //
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2 OR content_ja:QUERY1\\ QUERY2~0.8",
    //                    queryHelper.buildQuery("\"QUERY1 QUERY2\"~0.8").getQuery());
    //            assertEquals("title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~0.8", queryHelper.buildQuery("\"QUERY1~0.8\"").getQuery());
    //
    //            assertEquals("title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~0.8", queryHelper.buildQuery("\"QUERY1~0.8a\"").getQuery());
    //            assertEquals("title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~", queryHelper.buildQuery("\"QUERY1~a\"").getQuery());
    //        }
    //    }
    //
    //    public void test_proximitySearches() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            // ~10
    //            assertEquals("title:\"QUERY\"~10 OR content:\"QUERY\"~10", queryHelper.buildQuery("QUERY~10").getQuery());
    //            assertEquals("title:\"QUERY\"~1 OR content:\"QUERY\"~1", queryHelper.buildQuery("QUERY~1").getQuery());
    //            assertEquals("title:\"QUERY\"~5 OR content:\"QUERY\"~5", queryHelper.buildQuery("QUERY~5.5").getQuery());
    //            assertEquals("(title:\"QUERY1\"~10 OR content:\"QUERY1\"~10) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1~10 QUERY2").getQuery());
    //            assertEquals("(title:\"QUERY1\"~5 OR content:\"QUERY1\"~5) " + op + " (title:\"QUERY2\"~10 OR content:\"QUERY2\"~10)",
    //                    queryHelper.buildQuery("QUERY1~5 QUERY2~10").getQuery());
    //
    //            assertEquals("mimetype:\"QUERY1\"~10", queryHelper.buildQuery("mimetype:QUERY1~10").getQuery());
    //            assertEquals("mimetype:\"QUERY1\"~10 " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("mimetype:QUERY1~10 QUERY2").getQuery());
    //
    //            assertEquals("title:\"QUERY1\\ QUERY2\"~10 OR content:\"QUERY1\\ QUERY2\"~10",
    //                    queryHelper.buildQuery("\"QUERY1 QUERY2\"~10").getQuery());
    //            assertEquals("title:\"QUERY1\"~10 OR content:\"QUERY1\"~10", queryHelper.buildQuery("\"QUERY1~10\"").getQuery());
    //
    //            assertEquals("title:\"QUERY1\"~10 OR content:\"QUERY1\"~10", queryHelper.buildQuery("\"QUERY1~10a\"").getQuery());
    //        }
    //    }
    //
    //    public void test_rangeSearches() {
    //        String rangeQuery;
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            // mod_date:[20020101 TO 20030101]
    //            assertEquals("title:[20020101 TO 20030101] OR content:[20020101 TO 20030101]",
    //                    queryHelper.buildQuery("[20020101 TO 20030101]").getQuery());
    //            assertEquals("last_modified:[20020101 TO 20030101]", queryHelper.buildQuery("last_modified:[20020101 TO 20030101]").getQuery());
    //            assertEquals("(title:QUERY OR content:QUERY) " + op + " last_modified:[20020101 TO 20030101]",
    //                    queryHelper.buildQuery("QUERY last_modified:[20020101 TO 20030101]").getQuery());
    //            assertEquals("title:{Aida TO Carmen} OR content:{Aida TO Carmen}", queryHelper.buildQuery("{Aida TO Carmen}").getQuery());
    //            assertEquals("last_modified:{Aida TO Carmen}", queryHelper.buildQuery("last_modified:{Aida TO Carmen}").getQuery());
    //            assertEquals("(title:QUERY OR content:QUERY) " + op + " title:{Aida TO Carmen}",
    //                    queryHelper.buildQuery("QUERY title:{Aida TO Carmen}").getQuery());
    //            assertEquals("last_modified:[20020101 TO abc]", queryHelper.buildQuery("last_modified:[20020101 TO abc]").getQuery());
    //            assertEquals("last_modified:[abc TO 20020101]", queryHelper.buildQuery("last_modified:[abc TO 20020101]").getQuery());
    //            assertEquals("last_modified:[20020101 TO *]", queryHelper.buildQuery("last_modified:[20020101 TO *]").getQuery());
    //            assertEquals("last_modified:[* TO 20020101]", queryHelper.buildQuery("last_modified:[* TO 20020101]").getQuery());
    //
    //            rangeQuery = "(content:[1 TO 2] OR content:[3 TO 4]) " + op + " (content:[5 TO 6] OR content:[7 TO 8])";
    //            assertEquals(rangeQuery, queryHelper.buildQuery(rangeQuery).getQuery());
    //
    //            try {
    //                queryHelper.buildQuery("last_modified:[20020101 TO]").getQuery();
    //                fail();
    //            } catch (final InvalidQueryException e) {}
    //            try {
    //                queryHelper.buildQuery("last_modified:[TO 20030101]").getQuery();
    //                fail();
    //            } catch (final InvalidQueryException e) {}
    //            try {
    //                queryHelper.buildQuery("last_modified:[20020101]").getQuery();
    //                fail();
    //            } catch (final InvalidQueryException e) {}
    //            try {
    //                queryHelper.buildQuery("last_modified:[20030101]").getQuery();
    //                fail();
    //            } catch (final InvalidQueryException e) {}
    //
    //            // mod_date:{20020101 TO 20030101}
    //            assertEquals("title:{20020101 TO 20030101} OR content:{20020101 TO 20030101}",
    //                    queryHelper.buildQuery("{20020101 TO 20030101}").getQuery());
    //            assertEquals("last_modified:{20020101 TO 20030101}", queryHelper.buildQuery("last_modified:{20020101 TO 20030101}").getQuery());
    //            assertEquals("(title:QUERY OR content:QUERY) " + op + " last_modified:{20020101 TO 20030101}",
    //                    queryHelper.buildQuery("QUERY last_modified:{20020101 TO 20030101}").getQuery());
    //            assertEquals("title:{Aida TO Carmen} OR content:{Aida TO Carmen}", queryHelper.buildQuery("{Aida TO Carmen}").getQuery());
    //            assertEquals("last_modified:{Aida TO Carmen}", queryHelper.buildQuery("last_modified:{Aida TO Carmen}").getQuery());
    //            assertEquals("(title:QUERY OR content:QUERY) " + op + " title:{Aida TO Carmen}",
    //                    queryHelper.buildQuery("QUERY title:{Aida TO Carmen}").getQuery());
    //            assertEquals("last_modified:{20020101 TO abc}", queryHelper.buildQuery("last_modified:{20020101 TO abc}").getQuery());
    //            assertEquals("last_modified:{abc TO 20020101}", queryHelper.buildQuery("last_modified:{abc TO 20020101}").getQuery());
    //            assertEquals("last_modified:{20020101 TO *}", queryHelper.buildQuery("last_modified:{20020101 TO *}").getQuery());
    //            assertEquals("last_modified:{* TO 20020101}", queryHelper.buildQuery("last_modified:{* TO 20020101}").getQuery());
    //
    //            rangeQuery = "(content:{1 TO 2} OR content:{3 TO 4}) " + op + " (content:{5 TO 6} OR content:{7 TO 8})";
    //            assertEquals(rangeQuery, queryHelper.buildQuery(rangeQuery).getQuery());
    //
    //            try {
    //                queryHelper.buildQuery("last_modified:{20020101 TO}").getQuery();
    //                fail();
    //            } catch (final InvalidQueryException e) {}
    //            try {
    //                queryHelper.buildQuery("last_modified:{TO 20030101}").getQuery();
    //                fail();
    //            } catch (final InvalidQueryException e) {}
    //            try {
    //                queryHelper.buildQuery("last_modified:{20020101}").getQuery();
    //                fail();
    //            } catch (final InvalidQueryException e) {}
    //            try {
    //                queryHelper.buildQuery("last_modified:{20030101}").getQuery();
    //                fail();
    //            } catch (final InvalidQueryException e) {}
    //
    //            rangeQuery = "(content:[1 TO 2] OR content:{3 TO 4}) " + op + " (content:{5 TO 6} OR content:[7 TO 8])";
    //            assertEquals(rangeQuery, queryHelper.buildQuery(rangeQuery).getQuery());
    //        }
    //    }
    //
    //    public void test_boosting() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            // ^1000 ""^1000
    //            assertEquals("title:QUERY^1000 OR content:QUERY^1000", queryHelper.buildQuery("QUERY^1000").getQuery());
    //            assertEquals("(title:QUERY1^1000 OR content:QUERY1^1000) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1^1000 QUERY2").getQuery());
    //            assertEquals("(title:QUERY1^500 OR content:QUERY1^500) " + op + " (title:QUERY2^1000 OR content:QUERY2^1000)",
    //                    queryHelper.buildQuery("QUERY1^500 QUERY2^1000").getQuery());
    //
    //            assertEquals("mimetype:QUERY1^1000", queryHelper.buildQuery("mimetype:QUERY1^1000").getQuery());
    //            assertEquals("mimetype:QUERY1^1000 " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("mimetype:QUERY1^1000 QUERY2").getQuery());
    //
    //            assertEquals("title:QUERY1\\ QUERY2^1000 OR content:QUERY1\\ QUERY2^1000",
    //                    queryHelper.buildQuery("\"QUERY1 QUERY2\"^1000").getQuery());
    //            assertEquals("title:QUERY1^1000 OR content:QUERY1^1000", queryHelper.buildQuery("\"QUERY1^1000\"").getQuery());
    //        }
    //    }
    //
    //    public void test_reserved() {
    //        for (int i = 0; i < Constants.RESERVED.length - 1; i++) {
    //            try {
    //                assertEquals("title:\\" + Constants.RESERVED[i] + " OR content:\\" + Constants.RESERVED[i],
    //                        queryHelper.buildQuery(Constants.RESERVED[i]).getQuery());
    //            } catch (final InvalidQueryException e) {
    //                if (Constants.RESERVED[i].equals("\"") && e.getMessageCode().equals("errors.invalid_query_quoted")) {
    //                    assertEquals("title:\\" + Constants.RESERVED[i] + " OR content:\\" + Constants.RESERVED[i],
    //                            queryHelper.buildQuery("\\" + Constants.RESERVED[i]).getQuery());
    //                    continue;
    //                } else if (Constants.RESERVED[i].equals("{") && e.getMessageCode().equals("errors.invalid_query_curly_bracket")) {
    //                    assertEquals("title:\\" + Constants.RESERVED[i] + " OR content:\\" + Constants.RESERVED[i],
    //                            queryHelper.buildQuery("\\" + Constants.RESERVED[i]).getQuery());
    //                    continue;
    //                } else if (Constants.RESERVED[i].equals("[") && e.getMessageCode().equals("errors.invalid_query_square_bracket")) {
    //                    assertEquals("title:\\" + Constants.RESERVED[i] + " OR content:\\" + Constants.RESERVED[i],
    //                            queryHelper.buildQuery("\\" + Constants.RESERVED[i]).getQuery());
    //                    continue;
    //                } else if (Constants.RESERVED[i].equals("(") && e.getMessageCode().equals("errors.invalid_query_parenthesis")) {
    //                    assertEquals("title:\\" + Constants.RESERVED[i] + " OR content:\\" + Constants.RESERVED[i],
    //                            queryHelper.buildQuery("\\" + Constants.RESERVED[i]).getQuery());
    //                    continue;
    //                }
    //            }
    //        }
    //        assertEquals("title:\\: OR content:\\:", queryHelper.buildQuery(":").getQuery());
    //    }
    //
    //    public void test_or() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildQuery("OR QUERY").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) OR (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1 OR QUERY2").getQuery());
    //            assertEquals("title:QUERY", queryHelper.buildQuery("OR title:QUERY").getQuery());
    //            assertEquals("title:QUERY1 OR title:QUERY2", queryHelper.buildQuery("title:QUERY1 OR title:QUERY2").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) OR title:QUERY2", queryHelper.buildQuery("QUERY1 OR title:QUERY2").getQuery());
    //            assertEquals("mimetype:QUERY1 OR title:QUERY2", queryHelper.buildQuery("mimetype:QUERY1 OR title:QUERY2").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) OR (title:QUERY2 OR content:QUERY2) " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildQuery("QUERY1 OR QUERY2 QUERY3").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) OR (title:QUERY2 OR content:QUERY2) OR (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildQuery("QUERY1 OR QUERY2 OR QUERY3").getQuery());
    //        }
    //    }
    //
    //    public void test_and() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildQuery("AND QUERY").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1 AND QUERY2").getQuery());
    //            assertEquals("title:QUERY", queryHelper.buildQuery("AND title:QUERY").getQuery());
    //            assertEquals("title:QUERY1 AND title:QUERY2", queryHelper.buildQuery("title:QUERY1 AND title:QUERY2").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) AND title:QUERY2", queryHelper.buildQuery("QUERY1 AND title:QUERY2").getQuery());
    //            assertEquals("mimetype:QUERY1 AND title:QUERY2", queryHelper.buildQuery("mimetype:QUERY1 AND title:QUERY2").getQuery());
    //            assertEquals(
    //                    "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2) " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildQuery("QUERY1 AND QUERY2 QUERY3").getQuery());
    //            assertEquals(
    //                    "(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildQuery("QUERY1 QUERY2 AND QUERY3").getQuery());
    //            assertEquals("(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildQuery("QUERY1 AND QUERY2 AND QUERY3").getQuery());
    //        }
    //    }
    //
    //    public void test_not() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("NOT (title:QUERY OR content:QUERY)", queryHelper.buildQuery("NOT QUERY").getQuery());
    //            assertEquals("NOT title:QUERY", queryHelper.buildQuery("NOT title:QUERY").getQuery());
    //            assertEquals("(title:QUERY2 OR content:QUERY2) " + op + " NOT (title:QUERY1 OR content:QUERY1)",
    //                    queryHelper.buildQuery("NOT QUERY1 QUERY2").getQuery());
    //            assertEquals("(title:QUERY2 OR content:QUERY2) " + op + " NOT (title:QUERY1 OR content:QUERY1)",
    //                    queryHelper.buildQuery("NOT QUERY1 OR QUERY2").getQuery());
    //            assertEquals("NOT (title:QUERY1 OR content:QUERY1) " + op + " NOT (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("NOT QUERY1 NOT QUERY2").getQuery());
    //            assertEquals("NOT (title:QUERY1 OR content:QUERY1) " + op + " NOT (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildQuery("NOT QUERY1 OR NOT QUERY2").getQuery());
    //            assertEquals("(title:QUERY2 OR content:QUERY2) " + op + " NOT (title:QUERY1 OR content:QUERY1) " + op
    //                    + " NOT (title:QUERY3 OR content:QUERY3)", queryHelper.buildQuery("NOT QUERY1 QUERY2 NOT QUERY3").getQuery());
    //            assertEquals("NOT mimetype:QUERY", queryHelper.buildQuery("NOT mimetype:QUERY").getQuery());
    //            assertEquals("NOT mimetype:QUERY1 " + op + " NOT title:QUERY2",
    //                    queryHelper.buildQuery("NOT mimetype:QUERY1 NOT title:QUERY2").getQuery());
    //            assertEquals("(title:QUERY2 OR content:QUERY2) " + op + " NOT mimetype:QUERY1",
    //                    queryHelper.buildQuery("NOT mimetype:QUERY1 QUERY2").getQuery());
    //            assertEquals("mimetype:QUERY2 " + op + " NOT (title:QUERY1 OR content:QUERY1)",
    //                    queryHelper.buildQuery("NOT QUERY1 mimetype:QUERY2").getQuery());
    //        }
    //    }
    //
    //    public void test_escapeValue() {
    //        final String[] targets =
    //                new String[] { "+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", ":", "\\", " ", "\u3000" };
    //        for (final String target : targets) {
    //            assertEquals("abc\\" + target + "123", QueryUtil.escapeValue("abc" + target + "123"));
    //        }
    //        for (final String target : targets) {
    //            assertEquals("abc\\" + target, QueryUtil.escapeValue("abc" + target));
    //        }
    //        for (final String target : targets) {
    //            assertEquals("\\" + target + "123", QueryUtil.escapeValue(target + "123"));
    //        }
    //        for (final String target : targets) {
    //            assertEquals("abc\\" + target + "123\\" + target + "ABC", QueryUtil.escapeValue("abc" + target + "123" + target + "ABC"));
    //        }
    //    }
    //
    //    public void test_escapeRangeValue() {
    //        final String[] targets = new String[] { "&&", "||", "!", "(", ")", "{", "}", "[", "]", "\"", "~", ":", "\\", " ", "\u3000" };
    //        for (final String target : targets) {
    //            assertEquals("abc\\" + target + "123", QueryUtil.escapeValue("abc" + target + "123"));
    //        }
    //        for (final String target : targets) {
    //            assertEquals("abc\\" + target, QueryUtil.escapeValue("abc" + target));
    //        }
    //        for (final String target : targets) {
    //            assertEquals("\\" + target + "123", QueryUtil.escapeValue(target + "123"));
    //        }
    //        for (final String target : targets) {
    //            assertEquals("abc\\" + target + "123\\" + target + "ABC", QueryUtil.escapeValue("abc" + target + "123" + target + "ABC"));
    //        }
    //    }
    //
    //    public void test_buildFacet() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("", queryHelper.buildFacetQuery(""));
    //
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildFacetQuery("QUERY"));
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildFacetQuery("QUERY "));
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildFacetQuery(" QUERY"));
    //
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("QUERY1 QUERY2"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("QUERY1 QUERY2 "));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery(" QUERY1 QUERY2"));
    //
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2", queryHelper.buildFacetQuery("\"QUERY1 QUERY2\""));
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2", queryHelper.buildFacetQuery("\"QUERY1 QUERY2\" "));
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2", queryHelper.buildFacetQuery(" \"QUERY1 QUERY2\""));
    //
    //            assertEquals("(title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2) " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildFacetQuery("\"QUERY1 QUERY2\" QUERY3"));
    //        }
    //    }
    //
    //    public void test_buildFacet_fullwidthSpace() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("QUERY1\u3000QUERY2"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("QUERY1\u3000QUERY2\u3000"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("\u3000QUERY1\u3000QUERY2"));
    //
    //            assertEquals("title:QUERY1\\\u3000QUERY2 OR content:QUERY1\\\u3000QUERY2",
    //                    queryHelper.buildFacetQuery("\"QUERY1\u3000QUERY2\""));
    //
    //            assertEquals("(title:QUERY1\\\u3000QUERY2 OR content:QUERY1\\\u3000QUERY2) " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildFacetQuery("\"QUERY1\u3000QUERY2\"\u3000QUERY3"));
    //        }
    //    }
    //
    //    public void test_buildFacet_prefix() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("mimetype:QUERY1", queryHelper.buildFacetQuery("mimetype:QUERY1"));
    //            assertEquals("mimetype:QUERY1 " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("mimetype:QUERY1 QUERY2"));
    //            assertEquals("mimetype:QUERY1 " + op + " (title:QUERY2 OR content:QUERY2) " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildFacetQuery("mimetype:QUERY1 QUERY2 QUERY3"));
    //            assertEquals("mimetype:QUERY1 " + op + " host:QUERY2 " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildFacetQuery("mimetype:QUERY1 host:QUERY2 QUERY3"));
    //            assertEquals("mimetype:QUERY1\\ QUERY2 " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildFacetQuery("mimetype:\"QUERY1 QUERY2\" QUERY3"));
    //        }
    //    }
    //
    //    public void test_buildFacet_prefix_unknown() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("title:site\\: OR content:site\\:", queryHelper.buildFacetQuery("site:"));
    //            assertEquals("title:hoge\\:QUERY1 OR content:hoge\\:QUERY1", queryHelper.buildFacetQuery("hoge:QUERY1"));
    //            assertEquals("(title:hoge\\:QUERY1 OR content:hoge\\:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("hoge:QUERY1 QUERY2"));
    //            assertEquals("(title:hoge\\:QUERY1 OR content:hoge\\:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2) " + op
    //                    + " (title:QUERY3 OR content:QUERY3)", queryHelper.buildFacetQuery("hoge:QUERY1 QUERY2 QUERY3"));
    //            assertEquals(
    //                    "(title:hoge\\:QUERY1 OR content:hoge\\:QUERY1) " + op + " host:QUERY2 " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildFacetQuery("hoge:QUERY1 host:QUERY2 QUERY3"));
    //            assertEquals("(title:hoge\\:QUERY1\\ QUERY2 OR content:hoge\\:QUERY1\\ QUERY2) " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildFacetQuery("hoge:\"QUERY1 QUERY2\" QUERY3"));
    //        }
    //    }
    //
    //    public void test_buildFacet_sortField() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            String query;
    //            String searchQuery;
    //
    //            query = "";
    //            searchQuery = queryHelper.buildFacetQuery(query);
    //            assertEquals("", searchQuery);
    //
    //            query = "sort:content_length";
    //            searchQuery = queryHelper.buildFacetQuery(query);
    //            assertEquals("", searchQuery);
    //
    //            query = "sort:content_length.desc";
    //            searchQuery = queryHelper.buildFacetQuery(query);
    //            assertEquals("", searchQuery);
    //
    //            query = "sort:content_length.asc,last_modified";
    //            searchQuery = queryHelper.buildFacetQuery(query);
    //            assertEquals("", searchQuery);
    //
    //            query = "QUERY sort:content_length";
    //            searchQuery = queryHelper.buildFacetQuery(query);
    //            assertEquals("title:QUERY OR content:QUERY", searchQuery);
    //
    //            query = "QUERY sort:content_length.desc";
    //            searchQuery = queryHelper.buildFacetQuery(query);
    //            assertEquals("title:QUERY OR content:QUERY", searchQuery);
    //
    //            query = "QUERY sort:content_length.asc,last_modified";
    //            searchQuery = queryHelper.buildFacetQuery(query);
    //            assertEquals("title:QUERY OR content:QUERY", searchQuery);
    //
    //            query = "QUERY mimetype:QUERY1 sort:content_length";
    //            searchQuery = queryHelper.buildFacetQuery(query);
    //            assertEquals("(title:QUERY OR content:QUERY) " + op + " mimetype:QUERY1", searchQuery);
    //
    //            query = "QUERY sort:content_length.desc  mimetype:QUERY1";
    //            searchQuery = queryHelper.buildFacetQuery(query);
    //            assertEquals("(title:QUERY OR content:QUERY) " + op + " mimetype:QUERY1", searchQuery);
    //
    //            query = "QUERY sort:content_length.asc,last_modified mimetype:QUERY1";
    //            searchQuery = queryHelper.buildFacetQuery(query);
    //            assertEquals("(title:QUERY OR content:QUERY) " + op + " mimetype:QUERY1", searchQuery);
    //        }
    //    }
    //
    //    public void test_buildFacet_sortField_invalid() {
    //        String query;
    //        String searchQuery;
    //
    //        query = "sort:hoge";
    //        searchQuery = queryHelper.buildFacetQuery(query);
    //        assertEquals("", searchQuery);
    //
    //        query = "sort:content_length.hoge";
    //        searchQuery = queryHelper.buildFacetQuery(query);
    //        assertEquals("", searchQuery);
    //
    //        query = "sort:content_length.asc,hoge";
    //        searchQuery = queryHelper.buildFacetQuery(query);
    //        assertEquals("", searchQuery);
    //    }
    //
    //    public void test_buildFacet_wildcardSearches() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            // *
    //
    //            assertEquals("title:\\* OR content:\\*", queryHelper.buildFacetQuery("*"));
    //            assertEquals("title:QUERY* OR content:QUERY*", queryHelper.buildFacetQuery("QUERY* "));
    //            assertEquals("title:Q*ERY OR content:Q*ERY", queryHelper.buildFacetQuery(" Q*ERY"));
    //
    //            assertEquals("(title:Q*ERY1 OR content:Q*ERY1) " + op + " (title:Q*ERY2 OR content:Q*ERY2)",
    //                    queryHelper.buildFacetQuery("Q*ERY1 Q*ERY2"));
    //
    //            assertEquals("title:Q*ERY1\\ Q*ERY2 OR content:Q*ERY1\\ Q*ERY2", queryHelper.buildFacetQuery("\"Q*ERY1 Q*ERY2\""));
    //
    //            assertEquals("(title:Q*ERY1\\ Q*ERY2 OR content:Q*ERY1\\ Q*ERY2) " + op + " (title:Q*ERY3 OR content:Q*ERY3)",
    //                    queryHelper.buildFacetQuery("\"Q*ERY1 Q*ERY2\" Q*ERY3"));
    //
    //            assertEquals("mimetype:QUERY1*", queryHelper.buildFacetQuery("mimetype:QUERY1*"));
    //            assertEquals("mimetype:QUERY1* " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("mimetype:QUERY1* QUERY2"));
    //
    //            assertEquals("title:\\*QUERY1 OR content:\\*QUERY1", queryHelper.buildFacetQuery("*QUERY1"));
    //            assertEquals("title:\\*QUERY1* OR content:\\*QUERY1*", queryHelper.buildFacetQuery("*QUERY1*"));
    //
    //            // ?
    //
    //            assertEquals("title:\\? OR content:\\?", queryHelper.buildFacetQuery("?"));
    //            assertEquals("title:QUERY? OR content:QUERY?", queryHelper.buildFacetQuery("QUERY? "));
    //            assertEquals("title:Q?ERY OR content:Q?ERY", queryHelper.buildFacetQuery(" Q?ERY"));
    //
    //            assertEquals("(title:Q?ERY1 OR content:Q?ERY1) " + op + " (title:Q?ERY2 OR content:Q?ERY2)",
    //                    queryHelper.buildFacetQuery("Q?ERY1 Q?ERY2"));
    //
    //            assertEquals("title:Q?ERY1\\ Q?ERY2 OR content:Q?ERY1\\ Q?ERY2", queryHelper.buildFacetQuery("\"Q?ERY1 Q?ERY2\""));
    //
    //            assertEquals("(title:Q?ERY1\\ Q?ERY2 OR content:Q?ERY1\\ Q?ERY2) " + op + " (title:Q?ERY3 OR content:Q?ERY3)",
    //                    queryHelper.buildFacetQuery("\"Q?ERY1 Q?ERY2\" Q?ERY3"));
    //
    //            assertEquals("mimetype:QUERY1?", queryHelper.buildFacetQuery("mimetype:QUERY1?"));
    //            assertEquals("mimetype:QUERY1? " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("mimetype:QUERY1? QUERY2"));
    //
    //            assertEquals("title:\\?QUERY1 OR content:\\?QUERY1", queryHelper.buildFacetQuery("?QUERY1"));
    //            assertEquals("title:\\?QUERY1? OR content:\\?QUERY1?", queryHelper.buildFacetQuery("?QUERY1?"));
    //        }
    //    }
    //
    //    public void test_buildFacet_fuzzySearches() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            // ~
    //
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildFacetQuery("QUERY~"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("QUERY1~ QUERY2"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("QUERY1~ QUERY2~"));
    //
    //            assertEquals("mimetype:QUERY1~", queryHelper.buildFacetQuery("mimetype:QUERY1~"));
    //            assertEquals("mimetype:QUERY1~ " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("mimetype:QUERY1~ QUERY2"));
    //
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2", queryHelper.buildFacetQuery("\"QUERY1 QUERY2\"~"));
    //            assertEquals("title:QUERY1 OR content:QUERY1", queryHelper.buildFacetQuery("\"QUERY1~\""));
    //
    //            // ~0.8
    //
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildFacetQuery("QUERY~0.8"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("QUERY1~0.8 QUERY2"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("QUERY1~0.5 QUERY2~0.8"));
    //
    //            assertEquals("mimetype:QUERY1~0.8", queryHelper.buildFacetQuery("mimetype:QUERY1~0.8"));
    //            assertEquals("mimetype:QUERY1~0.8 " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("mimetype:QUERY1~0.8 QUERY2"));
    //
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2", queryHelper.buildFacetQuery("\"QUERY1 QUERY2\"~0.8"));
    //            assertEquals("title:QUERY1 OR content:QUERY1", queryHelper.buildFacetQuery("\"QUERY1~0.8\""));
    //
    //            assertEquals("title:QUERY1 OR content:QUERY1", queryHelper.buildFacetQuery("\"QUERY1~0.8a\""));
    //            assertEquals("title:QUERY1 OR content:QUERY1", queryHelper.buildFacetQuery("\"QUERY1~a\""));
    //        }
    //
    //        getMockRequest().setLocale(Locale.JAPANESE);
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            // ~
    //
    //            assertEquals("title:QUERY OR content:QUERY OR content_ja:QUERY~", queryHelper.buildFacetQuery("QUERY~"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~) " + op
    //                    + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)", queryHelper.buildFacetQuery("QUERY1~ QUERY2"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~) " + op
    //                    + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2~)", queryHelper.buildFacetQuery("QUERY1~ QUERY2~"));
    //
    //            assertEquals("mimetype:QUERY1~", queryHelper.buildFacetQuery("mimetype:QUERY1~"));
    //            assertEquals("mimetype:QUERY1~ " + op + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
    //                    queryHelper.buildFacetQuery("mimetype:QUERY1~ QUERY2"));
    //
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2 OR content_ja:QUERY1\\ QUERY2~",
    //                    queryHelper.buildFacetQuery("\"QUERY1 QUERY2\"~"));
    //            assertEquals("title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~", queryHelper.buildFacetQuery("\"QUERY1~\""));
    //
    //            // ~0.8
    //
    //            assertEquals("title:QUERY OR content:QUERY OR content_ja:QUERY~0.8", queryHelper.buildFacetQuery("QUERY~0.8"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~0.8) " + op
    //                    + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)", queryHelper.buildFacetQuery("QUERY1~0.8 QUERY2"));
    //            assertEquals(
    //                    "(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~0.5) " + op
    //                            + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2~0.8)",
    //                    queryHelper.buildFacetQuery("QUERY1~0.5 QUERY2~0.8"));
    //
    //            assertEquals("mimetype:QUERY1~0.8", queryHelper.buildFacetQuery("mimetype:QUERY1~0.8"));
    //            assertEquals("mimetype:QUERY1~0.8 " + op + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
    //                    queryHelper.buildFacetQuery("mimetype:QUERY1~0.8 QUERY2"));
    //
    //            assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2 OR content_ja:QUERY1\\ QUERY2~0.8",
    //                    queryHelper.buildFacetQuery("\"QUERY1 QUERY2\"~0.8"));
    //            assertEquals("title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~0.8", queryHelper.buildFacetQuery("\"QUERY1~0.8\""));
    //
    //            assertEquals("title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~0.8", queryHelper.buildFacetQuery("\"QUERY1~0.8a\""));
    //            assertEquals("title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1~", queryHelper.buildFacetQuery("\"QUERY1~a\""));
    //        }
    //    }
    //
    //    public void test_buildFacet_proximitySearches() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            // ~10
    //            assertEquals("title:\"QUERY\"~10 OR content:\"QUERY\"~10", queryHelper.buildFacetQuery("QUERY~10"));
    //            assertEquals("title:\"QUERY\"~1 OR content:\"QUERY\"~1", queryHelper.buildFacetQuery("QUERY~1"));
    //            assertEquals("title:\"QUERY\"~5 OR content:\"QUERY\"~5", queryHelper.buildFacetQuery("QUERY~5.5"));
    //            assertEquals("(title:\"QUERY1\"~10 OR content:\"QUERY1\"~10) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("QUERY1~10 QUERY2"));
    //            assertEquals("(title:\"QUERY1\"~5 OR content:\"QUERY1\"~5) " + op + " (title:\"QUERY2\"~10 OR content:\"QUERY2\"~10)",
    //                    queryHelper.buildFacetQuery("QUERY1~5 QUERY2~10"));
    //
    //            assertEquals("mimetype:\"QUERY1\"~10", queryHelper.buildFacetQuery("mimetype:QUERY1~10"));
    //            assertEquals("mimetype:\"QUERY1\"~10 " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("mimetype:QUERY1~10 QUERY2"));
    //
    //            assertEquals("title:\"QUERY1\\ QUERY2\"~10 OR content:\"QUERY1\\ QUERY2\"~10",
    //                    queryHelper.buildFacetQuery("\"QUERY1 QUERY2\"~10"));
    //            assertEquals("title:\"QUERY1\"~10 OR content:\"QUERY1\"~10", queryHelper.buildFacetQuery("\"QUERY1~10\""));
    //
    //            assertEquals("title:\"QUERY1\"~10 OR content:\"QUERY1\"~10", queryHelper.buildFacetQuery("\"QUERY1~10a\""));
    //        }
    //    }
    //
    //    public void test_buildFacet_rangeSearches() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            // mod_date:[20020101 TO 20030101]
    //            assertEquals("title:[20020101 TO 20030101] OR content:[20020101 TO 20030101]",
    //                    queryHelper.buildFacetQuery("[20020101 TO 20030101]"));
    //            assertEquals("last_modified:[20020101 TO 20030101]", queryHelper.buildFacetQuery("last_modified:[20020101 TO 20030101]"));
    //            assertEquals("(title:QUERY OR content:QUERY) " + op + " last_modified:[20020101 TO 20030101]",
    //                    queryHelper.buildFacetQuery("QUERY last_modified:[20020101 TO 20030101]"));
    //
    //            // TODO more..
    //
    //            // title:{Aida TO Carmen}
    //            assertEquals("title:{Aida TO Carmen} OR content:{Aida TO Carmen}", queryHelper.buildFacetQuery("{Aida TO Carmen}"));
    //            assertEquals("last_modified:{Aida TO Carmen}", queryHelper.buildFacetQuery("last_modified:{Aida TO Carmen}"));
    //            assertEquals("(title:QUERY OR content:QUERY) " + op + " title:{Aida TO Carmen}",
    //                    queryHelper.buildFacetQuery("QUERY title:{Aida TO Carmen}"));
    //
    //            // TODO more..
    //        }
    //    }
    //
    //    public void test_buildFacet_boosting() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            // ^1000 ""^1000
    //            assertEquals("title:QUERY^1000 OR content:QUERY^1000", queryHelper.buildFacetQuery("QUERY^1000"));
    //            assertEquals("(title:QUERY1^1000 OR content:QUERY1^1000) " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("QUERY1^1000 QUERY2"));
    //            assertEquals("(title:QUERY1^500 OR content:QUERY1^500) " + op + " (title:QUERY2^1000 OR content:QUERY2^1000)",
    //                    queryHelper.buildFacetQuery("QUERY1^500 QUERY2^1000"));
    //
    //            assertEquals("mimetype:QUERY1^1000", queryHelper.buildFacetQuery("mimetype:QUERY1^1000"));
    //            assertEquals("mimetype:QUERY1^1000 " + op + " (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("mimetype:QUERY1^1000 QUERY2"));
    //
    //            assertEquals("title:QUERY1\\ QUERY2^1000 OR content:QUERY1\\ QUERY2^1000",
    //                    queryHelper.buildFacetQuery("\"QUERY1 QUERY2\"^1000"));
    //            assertEquals("title:QUERY1^1000 OR content:QUERY1^1000", queryHelper.buildFacetQuery("\"QUERY1^1000\""));
    //        }
    //    }
    //
    //    public void test_buildFacet_reserved() {
    //        for (int i = 0; i < Constants.RESERVED.length - 1; i++) {
    //            try {
    //                assertEquals("title:\\" + Constants.RESERVED[i] + " OR content:\\" + Constants.RESERVED[i],
    //                        queryHelper.buildFacetQuery(Constants.RESERVED[i]));
    //            } catch (final InvalidQueryException e) {
    //                if (Constants.RESERVED[i].equals("\"") && e.getMessageCode().equals("errors.invalid_query_quoted")) {
    //                    assertEquals("title:\\" + Constants.RESERVED[i] + " OR content:\\" + Constants.RESERVED[i],
    //                            queryHelper.buildFacetQuery("\\" + Constants.RESERVED[i]));
    //                    continue;
    //                } else if (Constants.RESERVED[i].equals("{") && e.getMessageCode().equals("errors.invalid_query_curly_bracket")) {
    //                    assertEquals("title:\\" + Constants.RESERVED[i] + " OR content:\\" + Constants.RESERVED[i],
    //                            queryHelper.buildFacetQuery("\\" + Constants.RESERVED[i]));
    //                    continue;
    //                } else if (Constants.RESERVED[i].equals("[") && e.getMessageCode().equals("errors.invalid_query_square_bracket")) {
    //                    assertEquals("title:\\" + Constants.RESERVED[i] + " OR content:\\" + Constants.RESERVED[i],
    //                            queryHelper.buildFacetQuery("\\" + Constants.RESERVED[i]));
    //                    continue;
    //                } else if (Constants.RESERVED[i].equals("(") && e.getMessageCode().equals("errors.invalid_query_parenthesis")) {
    //                    assertEquals("title:\\" + Constants.RESERVED[i] + " OR content:\\" + Constants.RESERVED[i],
    //                            queryHelper.buildFacetQuery("\\" + Constants.RESERVED[i]));
    //                    continue;
    //                }
    //            }
    //        }
    //        assertEquals("title:\\: OR content:\\:", queryHelper.buildFacetQuery(":"));
    //    }
    //
    //    public void test_buildFacet_or() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildFacetQuery("OR QUERY"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) OR (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("QUERY1 OR QUERY2"));
    //            assertEquals("title:QUERY", queryHelper.buildFacetQuery("OR title:QUERY"));
    //            assertEquals("title:QUERY1 OR title:QUERY2", queryHelper.buildFacetQuery("title:QUERY1 OR title:QUERY2"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) OR title:QUERY2", queryHelper.buildFacetQuery("QUERY1 OR title:QUERY2"));
    //            assertEquals("mimetype:QUERY1 OR title:QUERY2", queryHelper.buildFacetQuery("mimetype:QUERY1 OR title:QUERY2"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) OR (title:QUERY2 OR content:QUERY2) " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildFacetQuery("QUERY1 OR QUERY2 QUERY3"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) OR (title:QUERY2 OR content:QUERY2) OR (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildFacetQuery("QUERY1 OR QUERY2 OR QUERY3"));
    //        }
    //    }
    //
    //    public void test_buildFacet_and() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("title:QUERY OR content:QUERY", queryHelper.buildFacetQuery("AND QUERY"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("QUERY1 AND QUERY2"));
    //            assertEquals("title:QUERY", queryHelper.buildFacetQuery("AND title:QUERY"));
    //            assertEquals("title:QUERY1 AND title:QUERY2", queryHelper.buildFacetQuery("title:QUERY1 AND title:QUERY2"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) AND title:QUERY2", queryHelper.buildFacetQuery("QUERY1 AND title:QUERY2"));
    //            assertEquals("mimetype:QUERY1 AND title:QUERY2", queryHelper.buildFacetQuery("mimetype:QUERY1 AND title:QUERY2"));
    //            assertEquals(
    //                    "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2) " + op + " (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildFacetQuery("QUERY1 AND QUERY2 QUERY3"));
    //            assertEquals(
    //                    "(title:QUERY1 OR content:QUERY1) " + op + " (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildFacetQuery("QUERY1 QUERY2 AND QUERY3"));
    //            assertEquals("(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
    //                    queryHelper.buildFacetQuery("QUERY1 AND QUERY2 AND QUERY3"));
    //        }
    //    }
    //
    //    public void test_buildFacet_not() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("NOT (title:QUERY OR content:QUERY)", queryHelper.buildFacetQuery("NOT QUERY"));
    //            assertEquals("NOT title:QUERY", queryHelper.buildFacetQuery("NOT title:QUERY"));
    //            assertEquals("(title:QUERY2 OR content:QUERY2) " + op + " NOT (title:QUERY1 OR content:QUERY1)",
    //                    queryHelper.buildFacetQuery("NOT QUERY1 QUERY2"));
    //            assertEquals("(title:QUERY2 OR content:QUERY2) " + op + " NOT (title:QUERY1 OR content:QUERY1)",
    //                    queryHelper.buildFacetQuery("NOT QUERY1 OR QUERY2"));
    //            assertEquals("NOT (title:QUERY1 OR content:QUERY1) " + op + " NOT (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("NOT QUERY1 NOT QUERY2"));
    //            assertEquals("NOT (title:QUERY1 OR content:QUERY1) " + op + " NOT (title:QUERY2 OR content:QUERY2)",
    //                    queryHelper.buildFacetQuery("NOT QUERY1 OR NOT QUERY2"));
    //            assertEquals("(title:QUERY2 OR content:QUERY2) " + op + " NOT (title:QUERY1 OR content:QUERY1) " + op
    //                    + " NOT (title:QUERY3 OR content:QUERY3)", queryHelper.buildFacetQuery("NOT QUERY1 QUERY2 NOT QUERY3"));
    //            assertEquals("NOT mimetype:QUERY", queryHelper.buildFacetQuery("NOT mimetype:QUERY"));
    //            assertEquals("NOT mimetype:QUERY1 " + op + " NOT title:QUERY2",
    //                    queryHelper.buildFacetQuery("NOT mimetype:QUERY1 NOT title:QUERY2"));
    //            assertEquals("(title:QUERY2 OR content:QUERY2) " + op + " NOT mimetype:QUERY1",
    //                    queryHelper.buildFacetQuery("NOT mimetype:QUERY1 QUERY2"));
    //            assertEquals("mimetype:QUERY2 " + op + " NOT (title:QUERY1 OR content:QUERY1)",
    //                    queryHelper.buildFacetQuery("NOT QUERY1 mimetype:QUERY2"));
    //        }
    //    }
    //
    //    public void test_inner_query() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("(title:bbb OR content:bbb) " + op + " (title:ccc OR content:ccc)",
    //                    queryHelper.buildQuery("(bbb ccc)").getQuery());
    //            assertEquals("(title:bbb OR content:bbb) OR (title:ccc OR content:ccc)", queryHelper.buildQuery("(bbb OR ccc)").getQuery());
    //            assertEquals("(title:bbb OR content:bbb) AND (title:ccc OR content:ccc)", queryHelper.buildQuery("(bbb AND ccc)").getQuery());
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) " + op + " (title:ccc OR content:ccc))",
    //                    queryHelper.buildQuery("aaa (bbb ccc)").getQuery());
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
    //                    queryHelper.buildQuery("aaa (bbb AND ccc)").getQuery());
    //            assertEquals("(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
    //                    queryHelper.buildQuery("aaa AND (bbb AND ccc)").getQuery());
    //            assertEquals("(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
    //                    queryHelper.buildQuery("aaa OR (bbb AND ccc)").getQuery());
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc))",
    //                    queryHelper.buildQuery("aaa (bbb OR ccc)").getQuery());
    //            assertEquals("(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc))",
    //                    queryHelper.buildQuery("aaa AND (bbb OR ccc)").getQuery());
    //            assertEquals("(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc))",
    //                    queryHelper.buildQuery("aaa OR (bbb OR ccc)").getQuery());
    //
    //            assertEquals("((title:bbb OR content:bbb) " + op + " (title:ccc OR content:ccc)) " + op + " (title:ddd OR content:ddd)",
    //                    queryHelper.buildQuery("(bbb ccc) ddd").getQuery());
    //            assertEquals("((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) " + op + " (title:ddd OR content:ddd)",
    //                    queryHelper.buildQuery("(bbb AND ccc) ddd").getQuery());
    //            assertEquals("((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
    //                    queryHelper.buildQuery("(bbb AND ccc) AND ddd").getQuery());
    //            assertEquals("((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
    //                    queryHelper.buildQuery("(bbb AND ccc) OR ddd").getQuery());
    //
    //            assertEquals("((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) " + op + " (title:ddd OR content:ddd)",
    //                    queryHelper.buildQuery("(bbb OR ccc) ddd").getQuery());
    //            assertEquals("((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
    //                    queryHelper.buildQuery("(bbb OR ccc) AND ddd").getQuery());
    //            assertEquals("((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
    //                    queryHelper.buildQuery("(bbb OR ccc) OR ddd").getQuery());
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) " + op + " (title:ccc OR content:ccc)) " + op
    //                    + " (title:ddd OR content:ddd)", queryHelper.buildQuery("aaa (bbb ccc) ddd").getQuery());
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) " + op
    //                    + " (title:ddd OR content:ddd)", queryHelper.buildQuery("aaa (bbb AND ccc) ddd").getQuery());
    //            assertEquals(
    //                    "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
    //                    queryHelper.buildQuery("aaa AND (bbb AND ccc) AND ddd").getQuery());
    //            assertEquals(
    //                    "(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
    //                    queryHelper.buildQuery("aaa OR (bbb AND ccc) OR ddd").getQuery());
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) " + op
    //                    + " (title:ddd OR content:ddd)", queryHelper.buildQuery("aaa (bbb OR ccc) ddd").getQuery());
    //            assertEquals(
    //                    "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
    //                    queryHelper.buildQuery("aaa AND (bbb OR ccc) AND ddd").getQuery());
    //            assertEquals(
    //                    "(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
    //                    queryHelper.buildQuery("aaa OR (bbb OR ccc) OR ddd").getQuery());
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " (label:bbb " + op + " (title:ccc OR content:ccc))",
    //                    queryHelper.buildQuery("aaa (label:bbb ccc)").getQuery());
    //            assertEquals("(title:aaa OR content:aaa) " + op + " (label:bbb AND (title:ccc OR content:ccc))",
    //                    queryHelper.buildQuery("aaa (label:bbb AND ccc)").getQuery());
    //            assertEquals("(title:aaa OR content:aaa) " + op + " (label:bbb OR (title:ccc OR content:ccc))",
    //                    queryHelper.buildQuery("aaa (label:bbb OR ccc)").getQuery());
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) " + op + " label:ccc)",
    //                    queryHelper.buildQuery("aaa (bbb label:ccc)").getQuery());
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) AND label:ccc)",
    //                    queryHelper.buildQuery("aaa (bbb AND label:ccc)").getQuery());
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) OR label:ccc)",
    //                    queryHelper.buildQuery("aaa (bbb OR label:ccc)").getQuery());
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) " + op + " NOT (title:ccc OR content:ccc))",
    //                    queryHelper.buildQuery("aaa (bbb NOT ccc)").getQuery());
    //        }
    //    }
    //
    //    public void test_more_inner_query() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) " + op + " ((title:ccc OR content:ccc) " + op
    //                    + " (title:ddd OR content:ddd)))", queryHelper.buildQuery("aaa (bbb (ccc ddd))").getQuery());
    //            assertEquals("(((title:aaa OR content:aaa) " + op + " (title:bbb OR content:bbb)) " + op + " (title:ccc OR content:ccc)) " + op
    //                    + " (title:ddd OR content:ddd)", queryHelper.buildQuery("((aaa bbb) ccc) ddd").getQuery());
    //        }
    //    }
    //
    //    public void test_inner_facetQuery() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("(title:bbb OR content:bbb) " + op + " (title:ccc OR content:ccc)", queryHelper.buildFacetQuery("(bbb ccc)"));
    //            assertEquals("(title:bbb OR content:bbb) OR (title:ccc OR content:ccc)", queryHelper.buildFacetQuery("(bbb OR ccc)"));
    //            assertEquals("(title:bbb OR content:bbb) AND (title:ccc OR content:ccc)", queryHelper.buildFacetQuery("(bbb AND ccc)"));
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) " + op + " (title:ccc OR content:ccc))",
    //                    queryHelper.buildFacetQuery("aaa (bbb ccc)"));
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
    //                    queryHelper.buildFacetQuery("aaa (bbb AND ccc)"));
    //            assertEquals("(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
    //                    queryHelper.buildFacetQuery("aaa AND (bbb AND ccc)"));
    //            assertEquals("(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
    //                    queryHelper.buildFacetQuery("aaa OR (bbb AND ccc)"));
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc))",
    //                    queryHelper.buildFacetQuery("aaa (bbb OR ccc)"));
    //            assertEquals("(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc))",
    //                    queryHelper.buildFacetQuery("aaa AND (bbb OR ccc)"));
    //            assertEquals("(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc))",
    //                    queryHelper.buildFacetQuery("aaa OR (bbb OR ccc)"));
    //
    //            assertEquals("((title:bbb OR content:bbb) " + op + " (title:ccc OR content:ccc)) " + op + " (title:ddd OR content:ddd)",
    //                    queryHelper.buildFacetQuery("(bbb ccc) ddd"));
    //            assertEquals("((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) " + op + " (title:ddd OR content:ddd)",
    //                    queryHelper.buildFacetQuery("(bbb AND ccc) ddd"));
    //            assertEquals("((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
    //                    queryHelper.buildFacetQuery("(bbb AND ccc) AND ddd"));
    //            assertEquals("((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
    //                    queryHelper.buildFacetQuery("(bbb AND ccc) OR ddd"));
    //
    //            assertEquals("((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) " + op + " (title:ddd OR content:ddd)",
    //                    queryHelper.buildFacetQuery("(bbb OR ccc) ddd"));
    //            assertEquals("((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
    //                    queryHelper.buildFacetQuery("(bbb OR ccc) AND ddd"));
    //            assertEquals("((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
    //                    queryHelper.buildFacetQuery("(bbb OR ccc) OR ddd"));
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) " + op + " (title:ccc OR content:ccc)) " + op
    //                    + " (title:ddd OR content:ddd)", queryHelper.buildFacetQuery("aaa (bbb ccc) ddd"));
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) " + op
    //                    + " (title:ddd OR content:ddd)", queryHelper.buildFacetQuery("aaa (bbb AND ccc) ddd"));
    //            assertEquals(
    //                    "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
    //                    queryHelper.buildFacetQuery("aaa AND (bbb AND ccc) AND ddd"));
    //            assertEquals(
    //                    "(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
    //                    queryHelper.buildFacetQuery("aaa OR (bbb AND ccc) OR ddd"));
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) " + op
    //                    + " (title:ddd OR content:ddd)", queryHelper.buildFacetQuery("aaa (bbb OR ccc) ddd"));
    //            assertEquals(
    //                    "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
    //                    queryHelper.buildFacetQuery("aaa AND (bbb OR ccc) AND ddd"));
    //            assertEquals(
    //                    "(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
    //                    queryHelper.buildFacetQuery("aaa OR (bbb OR ccc) OR ddd"));
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " (label:bbb " + op + " (title:ccc OR content:ccc))",
    //                    queryHelper.buildFacetQuery("aaa (label:bbb ccc)"));
    //            assertEquals("(title:aaa OR content:aaa) " + op + " (label:bbb AND (title:ccc OR content:ccc))",
    //                    queryHelper.buildFacetQuery("aaa (label:bbb AND ccc)"));
    //            assertEquals("(title:aaa OR content:aaa) " + op + " (label:bbb OR (title:ccc OR content:ccc))",
    //                    queryHelper.buildFacetQuery("aaa (label:bbb OR ccc)"));
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) " + op + " label:ccc)",
    //                    queryHelper.buildFacetQuery("aaa (bbb label:ccc)"));
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) AND label:ccc)",
    //                    queryHelper.buildFacetQuery("aaa (bbb AND label:ccc)"));
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) OR label:ccc)",
    //                    queryHelper.buildFacetQuery("aaa (bbb OR label:ccc)"));
    //
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) " + op + " NOT (title:ccc OR content:ccc))",
    //                    queryHelper.buildFacetQuery("aaa (bbb NOT ccc)"));
    //        }
    //    }
    //
    //    public void test_more_inner_facetQuery() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("(title:aaa OR content:aaa) " + op + " ((title:bbb OR content:bbb) " + op + " ((title:ccc OR content:ccc) " + op
    //                    + " (title:ddd OR content:ddd)))", queryHelper.buildFacetQuery("aaa (bbb (ccc ddd))"));
    //            assertEquals("(((title:aaa OR content:aaa) " + op + " (title:bbb OR content:bbb)) " + op + " (title:ccc OR content:ccc)) " + op
    //                    + " (title:ddd OR content:ddd)", queryHelper.buildFacetQuery("((aaa bbb) ccc) ddd"));
    //        }
    //    }
    //
    //    public void test_quote_query() {
    //        final String[] formats =
    //                { "%s", " %s ", "aaa %s", "aaa OR %s", "NOT %s", "%s bbb", "%s bbb", "%s OR bbb", "aaa (%s)", "(%s) bbb", "aaa (%s) bbb" };
    //        for (final String format : formats) {
    //            assertException(format, "\"", "errors.invalid_query_quoted");
    //            assertException(format, "a\"", "errors.invalid_query_quoted");
    //            assertException(format, "\"b", "errors.invalid_query_quoted");
    //            assertException(format, "a\"b", "errors.invalid_query_quoted");
    //            assertException(format, "content:\"", "errors.invalid_query_quoted");
    //            assertException(format, "content:\" ", "errors.invalid_query_quoted");
    //            assertException(format, "content:a\"", "errors.invalid_query_quoted");
    //            assertException(format, "content:\"b", "errors.invalid_query_quoted");
    //            assertException(format, "content:a\"b", "errors.invalid_query_quoted");
    //        }
    //    }
    //
    //    public void test_curly_bracket_query() {
    //        queryHelper = new QueryHelper();
    //        inject(new FieldHelper());
    //        inject(new SystemHelper());
    //        inject(queryHelper);
    //        final String[] formats =
    //                { "%s", " %s ", "aaa %s", "aaa OR %s", "NOT %s", "%s bbb", "%s bbb", "%s OR bbb", "aaa (%s)", "(%s) bbb", "aaa (%s) bbb" };
    //        for (final String format : formats) {
    //            assertException(format, "{", "errors.invalid_query_curly_bracket");
    //            assertException(format, "a{", "errors.invalid_query_curly_bracket");
    //            assertException(format, "{b", "errors.invalid_query_curly_bracket");
    //            assertException(format, "a{b", "errors.invalid_query_curly_bracket");
    //            assertException(format, "content:{", "errors.invalid_query_curly_bracket");
    //            assertException(format, "content:{ ", "errors.invalid_query_curly_bracket");
    //            assertException(format, "content:a{", "errors.invalid_query_curly_bracket");
    //            assertException(format, "content:{b", "errors.invalid_query_curly_bracket");
    //            assertException(format, "content:a{b", "errors.invalid_query_curly_bracket");
    //        }
    //    }
    //
    //    public void test_square_bracket_query() {
    //        final String[] formats =
    //                { "%s", " %s ", "aaa %s", "aaa OR %s", "NOT %s", "%s bbb", "%s bbb", "%s OR bbb", "aaa (%s)", "(%s) bbb", "aaa (%s) bbb" };
    //        for (final String format : formats) {
    //            assertException(format, "[", "errors.invalid_query_square_bracket");
    //            assertException(format, "a[", "errors.invalid_query_square_bracket");
    //            assertException(format, "[b", "errors.invalid_query_square_bracket");
    //            assertException(format, "a[b", "errors.invalid_query_square_bracket");
    //            assertException(format, "content:[", "errors.invalid_query_square_bracket");
    //            assertException(format, "content:[ ", "errors.invalid_query_square_bracket");
    //            assertException(format, "content:a[", "errors.invalid_query_square_bracket");
    //            assertException(format, "content:[b", "errors.invalid_query_square_bracket");
    //            assertException(format, "content:a[b", "errors.invalid_query_square_bracket");
    //        }
    //    }
    //
    //    public void test_parenthesis_query() {
    //        final String[] formats =
    //                { "%s", " %s ", "aaa %s", "aaa OR %s", "NOT %s", "%s bbb", "%s bbb", "%s OR bbb", "aaa (%s)", "(%s) bbb", "aaa (%s) bbb" };
    //        for (final String format : formats) {
    //            assertException(format, "(", "errors.invalid_query_parenthesis");
    //            assertException(format, "a(", "errors.invalid_query_parenthesis");
    //            assertException(format, "(b", "errors.invalid_query_parenthesis");
    //            assertException(format, "a(b", "errors.invalid_query_parenthesis");
    //            assertException(format, "content:(", "errors.invalid_query_parenthesis");
    //            assertException(format, "content:( ", "errors.invalid_query_parenthesis");
    //            assertException(format, "content:a(", "errors.invalid_query_parenthesis");
    //            assertException(format, "content:(b", "errors.invalid_query_parenthesis");
    //            assertException(format, "content:a(b", "errors.invalid_query_parenthesis");
    //        }
    //    }
    //
    //    private void assertException(final String format, final String query, final String messageCode) {
    //        try {
    //            final String ret = queryHelper.buildQuery(String.format(format, query)).getQuery();
    //            fail("format: " + format + ", query: " + query + ", ret: " + ret);
    //        } catch (final InvalidQueryException e) {
    //            FessMessages messages = new FessMessages();
    //            e.getMessageCode().message(messages);
    //            if (!messages.hasMessageOf(ActionMessages.GLOBAL_PROPERTY_KEY, messageCode)) {
    //                throw e;
    //            }
    //        }
    //    }
    //
    //    public void test_quote_facetQuery() {
    //        final String[] formats =
    //                { "%s", " %s ", "aaa %s", "aaa OR %s", "NOT %s", "%s bbb", "%s bbb", "%s OR bbb", "aaa (%s)", "(%s) bbb", "aaa (%s) bbb" };
    //        for (final String format : formats) {
    //            assertExceptionOnFacetQuery(format, "\"", "errors.invalid_query_quoted");
    //            assertExceptionOnFacetQuery(format, "a\"", "errors.invalid_query_quoted");
    //            assertExceptionOnFacetQuery(format, "\"b", "errors.invalid_query_quoted");
    //            assertExceptionOnFacetQuery(format, "a\"b", "errors.invalid_query_quoted");
    //            assertExceptionOnFacetQuery(format, "content:\"", "errors.invalid_query_quoted");
    //            assertExceptionOnFacetQuery(format, "content:\" ", "errors.invalid_query_quoted");
    //            assertExceptionOnFacetQuery(format, "content:a\"", "errors.invalid_query_quoted");
    //            assertExceptionOnFacetQuery(format, "content:\"b", "errors.invalid_query_quoted");
    //            assertExceptionOnFacetQuery(format, "content:a\"b", "errors.invalid_query_quoted");
    //        }
    //    }
    //
    //    public void test_curly_bracket_facetQuery() {
    //        final String[] formats =
    //                { "%s", " %s ", "aaa %s", "aaa OR %s", "NOT %s", "%s bbb", "%s bbb", "%s OR bbb", "aaa (%s)", "(%s) bbb", "aaa (%s) bbb" };
    //        for (final String format : formats) {
    //            assertExceptionOnFacetQuery(format, "{", "errors.invalid_query_curly_bracket");
    //            assertExceptionOnFacetQuery(format, "a{", "errors.invalid_query_curly_bracket");
    //            assertExceptionOnFacetQuery(format, "{b", "errors.invalid_query_curly_bracket");
    //            assertExceptionOnFacetQuery(format, "a{b", "errors.invalid_query_curly_bracket");
    //            assertExceptionOnFacetQuery(format, "content:{", "errors.invalid_query_curly_bracket");
    //            assertExceptionOnFacetQuery(format, "content:{ ", "errors.invalid_query_curly_bracket");
    //            assertExceptionOnFacetQuery(format, "content:a{", "errors.invalid_query_curly_bracket");
    //            assertExceptionOnFacetQuery(format, "content:{b", "errors.invalid_query_curly_bracket");
    //            assertExceptionOnFacetQuery(format, "content:a{b", "errors.invalid_query_curly_bracket");
    //        }
    //    }
    //
    //    public void test_square_bracket_facetQuery() {
    //        final String[] formats =
    //                { "%s", " %s ", "aaa %s", "aaa OR %s", "NOT %s", "%s bbb", "%s bbb", "%s OR bbb", "aaa (%s)", "(%s) bbb", "aaa (%s) bbb" };
    //        for (final String format : formats) {
    //            assertExceptionOnFacetQuery(format, "[", "errors.invalid_query_square_bracket");
    //            assertExceptionOnFacetQuery(format, "a[", "errors.invalid_query_square_bracket");
    //            assertExceptionOnFacetQuery(format, "[b", "errors.invalid_query_square_bracket");
    //            assertExceptionOnFacetQuery(format, "a[b", "errors.invalid_query_square_bracket");
    //            assertExceptionOnFacetQuery(format, "content:[", "errors.invalid_query_square_bracket");
    //            assertExceptionOnFacetQuery(format, "content:[ ", "errors.invalid_query_square_bracket");
    //            assertExceptionOnFacetQuery(format, "content:a[", "errors.invalid_query_square_bracket");
    //            assertExceptionOnFacetQuery(format, "content:[b", "errors.invalid_query_square_bracket");
    //            assertExceptionOnFacetQuery(format, "content:a[b", "errors.invalid_query_square_bracket");
    //        }
    //    }
    //
    //    public void test_parenthesis_facetQuery() {
    //        final String[] formats =
    //                { "%s", " %s ", "aaa %s", "aaa OR %s", "NOT %s", "%s bbb", "%s bbb", "%s OR bbb", "aaa (%s)", "(%s) bbb", "aaa (%s) bbb" };
    //        for (final String format : formats) {
    //            assertExceptionOnFacetQuery(format, "(", "errors.invalid_query_parenthesis");
    //            assertExceptionOnFacetQuery(format, "a(", "errors.invalid_query_parenthesis");
    //            assertExceptionOnFacetQuery(format, "(b", "errors.invalid_query_parenthesis");
    //            assertExceptionOnFacetQuery(format, "a(b", "errors.invalid_query_parenthesis");
    //            assertExceptionOnFacetQuery(format, "content:(", "errors.invalid_query_parenthesis");
    //            assertExceptionOnFacetQuery(format, "content:( ", "errors.invalid_query_parenthesis");
    //            assertExceptionOnFacetQuery(format, "content:a(", "errors.invalid_query_parenthesis");
    //            assertExceptionOnFacetQuery(format, "content:(b", "errors.invalid_query_parenthesis");
    //            assertExceptionOnFacetQuery(format, "content:a(b", "errors.invalid_query_parenthesis");
    //        }
    //    }
    //
    //    public void test_buildContentQueryWithLang() {
    //        StringBuilder buf;
    //
    //        buf = new StringBuilder();
    //        queryHelper.buildContentQueryWithLang(buf, "aaa", null);
    //        assertEquals("(title:aaa OR content:aaa)", buf.toString());
    //
    //        buf = new StringBuilder();
    //        queryHelper.buildContentQueryWithLang(buf, "aaa", "ja");
    //        assertEquals("(title:aaa OR content:aaa OR content_ja:aaa)", buf.toString());
    //
    //        buf = new StringBuilder();
    //        queryHelper.buildContentQueryWithLang(buf, "aaa", "zh_CN");
    //        assertEquals("(title:aaa OR content:aaa OR content_zh_CN:aaa)", buf.toString());
    //    }
    //
    //    public void test_getQueryLanguage() {
    //        assertNull(queryHelper.getQueryLanguage());
    //        final MockletHttpServletRequest request = getMockRequest();
    //        request.setLocale(Locale.JAPAN);
    //        assertEquals("ja", queryHelper.getQueryLanguage());
    //        request.setLocale(Locale.SIMPLIFIED_CHINESE);
    //        assertEquals("zh_CN", queryHelper.getQueryLanguage());
    //        queryHelper.addFieldLanguage("zh_CN", "cjk");
    //        assertEquals("cjk", queryHelper.getQueryLanguage());
    //        request.setLocale(Locale.CHINESE);
    //        assertEquals("zh", queryHelper.getQueryLanguage());
    //        request.setLocale(Locale.CANADA_FRENCH);
    //        assertEquals("fr", queryHelper.getQueryLanguage());
    //        request.setLocale(new Locale("aa"));
    //        assertNull(queryHelper.getQueryLanguage());
    //    }
    //
    //    public void test_buildWithLang() {
    //        final MockletHttpServletRequest request = getMockRequest();
    //        request.setLocale(Locale.JAPAN);
    //
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("", queryHelper.buildQuery("").getQuery());
    //
    //            assertEquals("title:QUERY OR content:QUERY OR content_ja:QUERY", queryHelper.buildQuery("QUERY").getQuery());
    //            assertEquals("title:QUERY OR content:QUERY OR content_ja:QUERY", queryHelper.buildQuery("QUERY ").getQuery());
    //            assertEquals("title:QUERY OR content:QUERY OR content_ja:QUERY", queryHelper.buildQuery(" QUERY").getQuery());
    //
    //            assertEquals(
    //                    "(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1) " + op
    //                            + " NOT (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1 NOT QUERY2").getQuery());
    //
    //            assertEquals("(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1) " + op
    //                    + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)", queryHelper.buildQuery("QUERY1 QUERY2").getQuery());
    //            assertEquals(
    //                    "(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1) " + op
    //                            + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
    //                    queryHelper.buildQuery("QUERY1 QUERY2 ").getQuery());
    //            assertEquals(
    //                    "(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1) " + op
    //                            + " (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
    //                    queryHelper.buildQuery(" QUERY1 QUERY2").getQuery());
    //        }
    //    }
    //
    //    public void test_unbracketQuery() {
    //        assertEquals("", queryHelper.unbracketQuery(""));
    //        assertEquals("", queryHelper.unbracketQuery("()"));
    //        assertEquals("", queryHelper.unbracketQuery("(())"));
    //        assertEquals("()()", queryHelper.unbracketQuery("()()"));
    //        assertEquals("()()", queryHelper.unbracketQuery("(()())"));
    //        assertEquals("()()()", queryHelper.unbracketQuery("()()()"));
    //        assertEquals("()(()())", queryHelper.unbracketQuery("()(()())"));
    //
    //        assertEquals("(", queryHelper.unbracketQuery("("));
    //        assertEquals(")", queryHelper.unbracketQuery(")"));
    //        assertEquals("()(", queryHelper.unbracketQuery("()("));
    //        assertEquals("())", queryHelper.unbracketQuery("())"));
    //        assertEquals(")()", queryHelper.unbracketQuery(")()"));
    //        assertEquals("(()", queryHelper.unbracketQuery("(()"));
    //
    //        assertEquals("\\(", queryHelper.unbracketQuery("(\\()"));
    //        assertEquals("\\)", queryHelper.unbracketQuery("(\\))"));
    //        assertEquals("(\\\\()", queryHelper.unbracketQuery("(\\\\()"));
    //        assertEquals("(\\\\))", queryHelper.unbracketQuery("(\\\\))"));
    //        assertEquals("\\\\\\(", queryHelper.unbracketQuery("(\\\\\\()"));
    //        assertEquals("\\\\\\)", queryHelper.unbracketQuery("(\\\\\\))"));
    //    }
    //
    //    public void test_appendRangeQueryValue() {
    //        StringBuilder buf = new StringBuilder();
    //        queryHelper.appendRangeQueryValue(buf, "[1 TO 2]", '[', ']');
    //        assertEquals("[1 TO 2]", buf.toString());
    //
    //        buf = new StringBuilder();
    //        queryHelper.appendRangeQueryValue(buf, "[1234 TO 2345]", '[', ']');
    //        assertEquals("[1234 TO 2345]", buf.toString());
    //
    //        buf = new StringBuilder();
    //        queryHelper.appendRangeQueryValue(buf, "[* TO 2345]", '[', ']');
    //        assertEquals("[* TO 2345]", buf.toString());
    //
    //        buf = new StringBuilder();
    //        queryHelper.appendRangeQueryValue(buf, "[1234 TO *]", '[', ']');
    //        assertEquals("[1234 TO *]", buf.toString());
    //
    //        buf = new StringBuilder();
    //
    //        try {
    //            queryHelper.appendRangeQueryValue(buf, "[* TO *]", '[', ']');
    //            fail();
    //        } catch (final InvalidQueryException e) {}
    //
    //        try {
    //            queryHelper.appendRangeQueryValue(buf, "[1]", '[', ']');
    //            fail();
    //        } catch (final InvalidQueryException e) {}
    //
    //        try {
    //            queryHelper.appendRangeQueryValue(buf, "[1 TO]", '[', ']');
    //            fail();
    //        } catch (final InvalidQueryException e) {}
    //
    //        try {
    //            queryHelper.appendRangeQueryValue(buf, "[1 TO ]", '[', ']');
    //            fail();
    //        } catch (final InvalidQueryException e) {}
    //
    //        try {
    //            queryHelper.appendRangeQueryValue(buf, "[TO 1]", '[', ']');
    //            fail();
    //        } catch (final InvalidQueryException e) {}
    //
    //        try {
    //            queryHelper.appendRangeQueryValue(buf, "[ TO 1]", '[', ']');
    //            fail();
    //        } catch (final InvalidQueryException e) {}
    //    }
    //
    //    public void test_inurl() {
    //        for (final String op : new String[] { "AND", "OR" }) {
    //            getMockRequest().setAttribute(Constants.DEFAULT_OPERATOR, op);
    //            assertEquals("url:*QUERY*", queryHelper.buildQuery("inurl:QUERY").getQuery());
    //            assertEquals("url:*QUERY1* " + op + " url:*QUERY2*", queryHelper.buildQuery("inurl:QUERY1 inurl:QUERY2").getQuery());
    //            assertEquals("(title:aaa OR content:aaa) " + op + " url:*QUERY1* " + op + " url:*QUERY2*",
    //                    queryHelper.buildQuery("aaa inurl:QUERY1 inurl:QUERY2").getQuery());
    //            assertEquals("url:*QUERY*", queryHelper.buildQuery("inurl:\"QUERY\"").getQuery());
    //        }
    //    }
    //
    //    public void test_buildOptionQuery() {
    //        final Map<String, String[]> options = new HashMap<>();
    //
    //        assertEquals("", queryHelper.buildOptionQuery(null));
    //        assertEquals("", queryHelper.buildOptionQuery(options));
    //
    //        // Q
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_Q, new String[] { "a" });
    //        assertEquals("a", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_Q, new String[] { "a b" });
    //        assertEquals("a b", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_Q, new String[] { "a b c" });
    //        assertEquals("a b c", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_Q, new String[] { "\"a b\"" });
    //        assertEquals("\"a b\"", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_Q, new String[] { "\"a b\" c" });
    //        assertEquals("\"a b\" c", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_Q, new String[] { "\"a b\" \"c d\"" });
    //        assertEquals("\"a b\" \"c d\"", queryHelper.buildOptionQuery(options));
    //
    //        // CQ
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_CQ, new String[] { "a" });
    //        assertEquals("\"a\"", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_CQ, new String[] { "a b" });
    //        assertEquals("\"a b\"", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_CQ, new String[] { "a b c" });
    //        assertEquals("\"a b c\"", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_CQ, new String[] { "\"a b\"" });
    //        assertEquals("\"a b\"", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_CQ, new String[] { "\"a b\" c" });
    //        assertEquals("\"a b\" \"c\"", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_CQ, new String[] { "\"a b\" \"c d\"" });
    //        assertEquals("\"a b\" \"c d\"", queryHelper.buildOptionQuery(options));
    //
    //        // OQ
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_OQ, new String[] { "a" });
    //        assertEquals("a", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_OQ, new String[] { "a b" });
    //        assertEquals("a OR b", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_OQ, new String[] { "a b c" });
    //        assertEquals("a OR b OR c", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_OQ, new String[] { "\"a b\"" });
    //        assertEquals("\"a b\"", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_OQ, new String[] { "\"a b\" c" });
    //        assertEquals("\"a b\" OR c", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_OQ, new String[] { "\"a b\" \"c d\"" });
    //        assertEquals("\"a b\" OR \"c d\"", queryHelper.buildOptionQuery(options));
    //
    //        // NQ
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_NQ, new String[] { "a" });
    //        assertEquals("NOT a", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_NQ, new String[] { "a b" });
    //        assertEquals("NOT a NOT b", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_NQ, new String[] { "a b c" });
    //        assertEquals("NOT a NOT b NOT c", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_NQ, new String[] { "\"a b\"" });
    //        assertEquals("NOT \"a b\"", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_NQ, new String[] { "\"a b\" c" });
    //        assertEquals("NOT \"a b\" NOT c", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_NQ, new String[] { "\"a b\" \"c d\"" });
    //        assertEquals("NOT \"a b\" NOT \"c d\"", queryHelper.buildOptionQuery(options));
    //
    //        // combine
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_Q, new String[] { "a" });
    //        options.put(Constants.OPTION_QUERY_CQ, new String[] { "b" });
    //        options.put(Constants.OPTION_QUERY_OQ, new String[] { "c" });
    //        options.put(Constants.OPTION_QUERY_NQ, new String[] { "d" });
    //        assertEquals("a \"b\" c NOT d", queryHelper.buildOptionQuery(options));
    //
    //        options.clear();
    //        options.put(Constants.OPTION_QUERY_Q, new String[] { "a 1" });
    //        options.put(Constants.OPTION_QUERY_CQ, new String[] { "b 2" });
    //        options.put(Constants.OPTION_QUERY_OQ, new String[] { "c 3" });
    //        options.put(Constants.OPTION_QUERY_NQ, new String[] { "d 4" });
    //        assertEquals("a 1 \"b 2\" (c OR 3) NOT d NOT 4", queryHelper.buildOptionQuery(options));
    //    }
    //
    //    private void assertExceptionOnFacetQuery(final String format, final String query, final String messageCode) {
    //        try {
    //            final String ret = queryHelper.buildFacetQuery(String.format(format, query));
    //            fail("format: " + format + ", query: " + query + ", ret: " + ret);
    //        } catch (final InvalidQueryException e) {
    //            FessMessages messages = new FessMessages();
    //            e.getMessageCode().message(messages);
    //            if (!messages.hasMessageOf(ActionMessages.GLOBAL_PROPERTY_KEY, messageCode)) {
    //                throw e;
    //            }
    //        }
    //    }
    //
    //    public void test_getQueryParamMap() {
    //        assertEquals(0, queryHelper.getQueryParamMap().size());
    //
    //        Map<String, String[]> queryParamMap;
    //
    //        queryHelper.addQueryParam("aaa", new String[] { "111" });
    //        queryHelper.addQueryParam("bbb", new String[] { "222", "$333" });
    //        queryParamMap = queryHelper.getQueryParamMap();
    //        assertEquals(2, queryParamMap.size());
    //        assertEquals(1, queryParamMap.get("aaa").length);
    //        assertEquals("111", queryParamMap.get("aaa")[0]);
    //        assertEquals(2, queryParamMap.get("bbb").length);
    //        assertEquals("222", queryParamMap.get("bbb")[0]);
    //        assertEquals("", queryParamMap.get("bbb")[1]);
    //
    //        getMockRequest().setParameter("333", "AAA");
    //        queryParamMap = queryHelper.getQueryParamMap();
    //        assertEquals(2, queryParamMap.size());
    //        assertEquals(1, queryParamMap.get("aaa").length);
    //        assertEquals("111", queryParamMap.get("aaa")[0]);
    //        assertEquals(2, queryParamMap.get("bbb").length);
    //        assertEquals("222", queryParamMap.get("bbb")[0]);
    //        assertEquals("AAA", queryParamMap.get("bbb")[1]);
    //
    //    }

}