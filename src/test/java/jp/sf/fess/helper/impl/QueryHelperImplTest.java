/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.helper.impl;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jp.sf.fess.Constants;
import jp.sf.fess.InvalidQueryException;
import jp.sf.fess.entity.SearchQuery;
import jp.sf.fess.helper.BrowserTypeHelper;
import jp.sf.fess.helper.RoleQueryHelper;
import jp.sf.fess.util.QueryUtil;
import jp.sf.fess.util.SearchParamMap;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.mock.servlet.MockHttpServletRequest;
import org.seasar.struts.util.RequestUtil;

public class QueryHelperImplTest extends S2TestCase {

    public QueryHelperImpl queryHelperImpl;

    @Override
    protected String getRootDicon() throws Throwable {
        return "jp/sf/fess/helper/query.dicon";
    }

    public void test_build() {
        assertEquals("", queryHelperImpl.buildQuery("").getQuery());

        assertEquals("title:QUERY OR content:QUERY", queryHelperImpl
                .buildQuery("QUERY").getQuery());
        assertEquals("title:QUERY OR content:QUERY", queryHelperImpl
                .buildQuery("QUERY ").getQuery());
        assertEquals("title:QUERY OR content:QUERY", queryHelperImpl
                .buildQuery(" QUERY").getQuery());

        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1 QUERY2").getQuery());
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1 QUERY2 ").getQuery());
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery(" QUERY1 QUERY2").getQuery());

        assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2",
                queryHelperImpl.buildQuery("\"QUERY1 QUERY2\"").getQuery());
        assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2",
                queryHelperImpl.buildQuery("\"QUERY1 QUERY2\" ").getQuery());
        assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2",
                queryHelperImpl.buildQuery(" \"QUERY1 QUERY2\"").getQuery());

        assertEquals(
                "(title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildQuery("\"QUERY1 QUERY2\" QUERY3")
                        .getQuery());

    }

    public void test_build_fullwidthSpace() {

        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1\u3000QUERY2").getQuery());
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1\u3000QUERY2\u3000")
                        .getQuery());
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("\u3000QUERY1\u3000QUERY2")
                        .getQuery());

        assertEquals(
                "title:QUERY1\\\u3000QUERY2 OR content:QUERY1\\\u3000QUERY2",
                queryHelperImpl.buildQuery("\"QUERY1\u3000QUERY2\"").getQuery());

        assertEquals(
                "(title:QUERY1\\\u3000QUERY2 OR content:QUERY1\\\u3000QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl
                        .buildQuery("\"QUERY1\u3000QUERY2\"\u3000QUERY3")
                        .getQuery());

    }

    public void test_build_prefix() {
        assertEquals("mimetype:QUERY1",
                queryHelperImpl.buildQuery("mimetype:QUERY1").getQuery());
        assertEquals("mimetype:QUERY1 AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("mimetype:QUERY1 QUERY2").getQuery());
        assertEquals(
                "mimetype:QUERY1 AND (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildQuery("mimetype:QUERY1 QUERY2 QUERY3")
                        .getQuery());
        assertEquals(
                "mimetype:QUERY1 AND host:QUERY2 AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl
                        .buildQuery("mimetype:QUERY1 host:QUERY2 QUERY3")
                        .getQuery());
        assertEquals(
                "mimetype:QUERY1\\ QUERY2 AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildQuery("mimetype:\"QUERY1 QUERY2\" QUERY3")
                        .getQuery());

    }

    public void test_build_prefix_unknown() {
        assertEquals("title:site\\: OR content:site\\:", queryHelperImpl
                .buildQuery("site:").getQuery());
        assertEquals("title:hoge\\:QUERY1 OR content:hoge\\:QUERY1",
                queryHelperImpl.buildQuery("hoge:QUERY1").getQuery());
        assertEquals(
                "(title:hoge\\:QUERY1 OR content:hoge\\:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("hoge:QUERY1 QUERY2").getQuery());
        assertEquals(
                "(title:hoge\\:QUERY1 OR content:hoge\\:QUERY1) AND (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildQuery("hoge:QUERY1 QUERY2 QUERY3")
                        .getQuery());
        assertEquals(
                "(title:hoge\\:QUERY1 OR content:hoge\\:QUERY1) AND host:QUERY2 AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildQuery("hoge:QUERY1 host:QUERY2 QUERY3")
                        .getQuery());
        assertEquals(
                "(title:hoge\\:QUERY1\\ QUERY2 OR content:hoge\\:QUERY1\\ QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildQuery("hoge:\"QUERY1 QUERY2\" QUERY3")
                        .getQuery());

    }

    public void test_build_browserType() {
        queryHelperImpl.browserTypeHelper = new BrowserTypeHelper() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getBrowserType() {
                return PC;
            }
        };

        assertEquals("", queryHelperImpl.build("", true).getQuery());

        assertEquals("title:QUERY OR content:QUERY",
                queryHelperImpl.build("QUERY", true).getQuery());
        assertEquals("type:pc", queryHelperImpl.build("QUERY", true)
                .getFilterQueries()[0]);

        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.build("QUERY1 QUERY2", true).getQuery());
        assertEquals("type:pc", queryHelperImpl.build("QUERY1 QUERY2", true)
                .getFilterQueries()[0]);

        queryHelperImpl.browserTypeHelper = new BrowserTypeHelper() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getBrowserType() {
                return DOCOMO;
            }
        };

        assertEquals("title:QUERY OR content:QUERY",
                queryHelperImpl.build("QUERY", true).getQuery());
        assertEquals("type:docomo", queryHelperImpl.build("QUERY", true)
                .getFilterQueries()[0]);

        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.build("QUERY1 QUERY2", true).getQuery());
        assertEquals("type:docomo", queryHelperImpl
                .build("QUERY1 QUERY2", true).getFilterQueries()[0]);
    }

    public void test_build_roleType() {
        queryHelperImpl.roleQueryHelper = new RoleQueryHelper() {
            @Override
            public Set<String> build() {
                final Set<String> list = new HashSet<>();
                list.add("guest");
                return list;
            }
        };

        assertEquals("", queryHelperImpl.build("", true).getQuery());

        assertEquals("title:QUERY OR content:QUERY",
                queryHelperImpl.build("QUERY", true).getQuery());
        assertEquals("role:guest", queryHelperImpl.build("QUERY", true)
                .getFilterQueries()[0]);

        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.build("QUERY1 QUERY2", true).getQuery());
        assertEquals("role:guest", queryHelperImpl.build("QUERY1 QUERY2", true)
                .getFilterQueries()[0]);

        queryHelperImpl.roleQueryHelper = new RoleQueryHelper() {
            @Override
            public Set<String> build() {
                final Set<String> list = new HashSet<>();
                list.add("guest");
                list.add("admin");
                return list;
            }
        };

        assertEquals("title:QUERY OR content:QUERY",
                queryHelperImpl.build("QUERY", true).getQuery());
        assertEquals("role:guest OR role:admin",
                queryHelperImpl.build("QUERY", true).getFilterQueries()[0]);

        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.build("QUERY1 QUERY2", true).getQuery());
        assertEquals(
                "role:guest OR role:admin",
                queryHelperImpl.build("QUERY1 QUERY2", true).getFilterQueries()[0]);
    }

    public void test_build_browserType_and_roleType() {
        queryHelperImpl.browserTypeHelper = new BrowserTypeHelper() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getBrowserType() {
                return PC;
            }
        };
        queryHelperImpl.roleQueryHelper = new RoleQueryHelper() {
            @Override
            public Set<String> build() {
                final Set<String> list = new HashSet<>();
                list.add("guest");
                list.add("admin");
                return list;
            }
        };

        assertEquals("", queryHelperImpl.build("", true).getQuery());

        assertEquals("title:QUERY OR content:QUERY",
                queryHelperImpl.build("QUERY", true).getQuery());
        assertEquals("type:pc", queryHelperImpl.build("QUERY", true)
                .getFilterQueries()[0]);
        assertEquals("role:guest OR role:admin",
                queryHelperImpl.build("QUERY", true).getFilterQueries()[1]);

        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.build("QUERY1 QUERY2", true).getQuery());
        assertEquals("type:pc", queryHelperImpl.build("QUERY1 QUERY2", true)
                .getFilterQueries()[0]);
        assertEquals(
                "role:guest OR role:admin",
                queryHelperImpl.build("QUERY1 QUERY2", true).getFilterQueries()[1]);
    }

    public void test_sortField() {
        String query;
        SearchQuery searchQuery;

        query = "";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("", searchQuery.getQuery());
        assertEquals(0, searchQuery.getSortFields().length);

        query = "sort:contentLength";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("", searchQuery.getQuery());
        assertEquals(1, searchQuery.getSortFields().length);
        assertEquals("contentLength", searchQuery.getSortFields()[0].getField());
        assertEquals(Constants.ASC, searchQuery.getSortFields()[0].getOrder());

        query = "sort:contentLength.desc";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("", searchQuery.getQuery());
        assertEquals(1, searchQuery.getSortFields().length);
        assertEquals("contentLength", searchQuery.getSortFields()[0].getField());
        assertEquals(Constants.DESC, searchQuery.getSortFields()[0].getOrder());

        query = "sort:contentLength.asc,lastModified";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("", searchQuery.getQuery());
        assertEquals(2, searchQuery.getSortFields().length);
        assertEquals("contentLength", searchQuery.getSortFields()[0].getField());
        assertEquals(Constants.ASC, searchQuery.getSortFields()[0].getOrder());
        assertEquals("lastModified", searchQuery.getSortFields()[1].getField());
        assertEquals(Constants.ASC, searchQuery.getSortFields()[1].getOrder());

        query = "QUERY sort:contentLength";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("title:QUERY OR content:QUERY", searchQuery.getQuery());
        assertEquals(1, searchQuery.getSortFields().length);
        assertEquals("contentLength", searchQuery.getSortFields()[0].getField());
        assertEquals(Constants.ASC, searchQuery.getSortFields()[0].getOrder());

        query = "QUERY sort:contentLength.desc";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("title:QUERY OR content:QUERY", searchQuery.getQuery());
        assertEquals(1, searchQuery.getSortFields().length);
        assertEquals("contentLength", searchQuery.getSortFields()[0].getField());
        assertEquals(Constants.DESC, searchQuery.getSortFields()[0].getOrder());

        query = "QUERY sort:contentLength.asc,lastModified";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("title:QUERY OR content:QUERY", searchQuery.getQuery());
        assertEquals(2, searchQuery.getSortFields().length);
        assertEquals("contentLength", searchQuery.getSortFields()[0].getField());
        assertEquals(Constants.ASC, searchQuery.getSortFields()[0].getOrder());
        assertEquals("lastModified", searchQuery.getSortFields()[1].getField());
        assertEquals(Constants.ASC, searchQuery.getSortFields()[1].getOrder());

        query = "QUERY mimetype:QUERY1 sort:contentLength";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("(title:QUERY OR content:QUERY) AND mimetype:QUERY1",
                searchQuery.getQuery());
        assertEquals(1, searchQuery.getSortFields().length);
        assertEquals("contentLength", searchQuery.getSortFields()[0].getField());
        assertEquals(Constants.ASC, searchQuery.getSortFields()[0].getOrder());

        query = "QUERY sort:contentLength.desc  mimetype:QUERY1";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("(title:QUERY OR content:QUERY) AND mimetype:QUERY1",
                searchQuery.getQuery());
        assertEquals(1, searchQuery.getSortFields().length);
        assertEquals("contentLength", searchQuery.getSortFields()[0].getField());
        assertEquals(Constants.DESC, searchQuery.getSortFields()[0].getOrder());

        query = "QUERY sort:contentLength.asc,lastModified mimetype:QUERY1";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("(title:QUERY OR content:QUERY) AND mimetype:QUERY1",
                searchQuery.getQuery());
        assertEquals(2, searchQuery.getSortFields().length);
        assertEquals("contentLength", searchQuery.getSortFields()[0].getField());
        assertEquals(Constants.ASC, searchQuery.getSortFields()[0].getOrder());
        assertEquals("lastModified", searchQuery.getSortFields()[1].getField());
        assertEquals(Constants.ASC, searchQuery.getSortFields()[1].getOrder());
    }

    public void test_sortField_invalid() {
        String query;
        SearchQuery searchQuery;

        query = "sort:hoge";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("", searchQuery.getQuery());
        assertEquals(0, searchQuery.getSortFields().length);

        query = "sort:contentLength.hoge";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("", searchQuery.getQuery());
        assertEquals(0, searchQuery.getSortFields().length);

        query = "sort:contentLength.asc,hoge";
        searchQuery = queryHelperImpl.buildQuery(query);
        assertEquals("", searchQuery.getQuery());
        assertEquals(1, searchQuery.getSortFields().length);
        assertEquals("contentLength", searchQuery.getSortFields()[0].getField());
        assertEquals(Constants.ASC, searchQuery.getSortFields()[0].getOrder());
    }

    public void test_wildcardSearches() {
        // *

        assertEquals("title:\\* OR content:\\*", queryHelperImpl
                .buildQuery("*").getQuery());
        assertEquals("title:QUERY* OR content:QUERY*", queryHelperImpl
                .buildQuery("QUERY* ").getQuery());
        assertEquals("title:Q*ERY OR content:Q*ERY", queryHelperImpl
                .buildQuery(" Q*ERY").getQuery());

        assertEquals(
                "(title:Q*ERY1 OR content:Q*ERY1) AND (title:Q*ERY2 OR content:Q*ERY2)",
                queryHelperImpl.buildQuery("Q*ERY1 Q*ERY2").getQuery());

        assertEquals("title:Q*ERY1\\ Q*ERY2 OR content:Q*ERY1\\ Q*ERY2",
                queryHelperImpl.buildQuery("\"Q*ERY1 Q*ERY2\"").getQuery());

        assertEquals(
                "(title:Q*ERY1\\ Q*ERY2 OR content:Q*ERY1\\ Q*ERY2) AND (title:Q*ERY3 OR content:Q*ERY3)",
                queryHelperImpl.buildQuery("\"Q*ERY1 Q*ERY2\" Q*ERY3")
                        .getQuery());

        assertEquals("mimetype:QUERY1*",
                queryHelperImpl.buildQuery("mimetype:QUERY1*").getQuery());
        assertEquals("mimetype:QUERY1* AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("mimetype:QUERY1* QUERY2")
                        .getQuery());

        // ?

        assertEquals("title:\\? OR content:\\?", queryHelperImpl
                .buildQuery("?").getQuery());
        assertEquals("title:QUERY? OR content:QUERY?", queryHelperImpl
                .buildQuery("QUERY? ").getQuery());
        assertEquals("title:Q?ERY OR content:Q?ERY", queryHelperImpl
                .buildQuery(" Q?ERY").getQuery());

        assertEquals(
                "(title:Q?ERY1 OR content:Q?ERY1) AND (title:Q?ERY2 OR content:Q?ERY2)",
                queryHelperImpl.buildQuery("Q?ERY1 Q?ERY2").getQuery());

        assertEquals("title:Q?ERY1\\ Q?ERY2 OR content:Q?ERY1\\ Q?ERY2",
                queryHelperImpl.buildQuery("\"Q?ERY1 Q?ERY2\"").getQuery());

        assertEquals(
                "(title:Q?ERY1\\ Q?ERY2 OR content:Q?ERY1\\ Q?ERY2) AND (title:Q?ERY3 OR content:Q?ERY3)",
                queryHelperImpl.buildQuery("\"Q?ERY1 Q?ERY2\" Q?ERY3")
                        .getQuery());

        assertEquals("mimetype:QUERY1?",
                queryHelperImpl.buildQuery("mimetype:QUERY1?").getQuery());
        assertEquals("mimetype:QUERY1? AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("mimetype:QUERY1? QUERY2")
                        .getQuery());
    }

    public void test_fuzzySearches() {
        // ~

        assertEquals("title:QUERY~ OR content:QUERY~", queryHelperImpl
                .buildQuery("QUERY~").getQuery());
        assertEquals(
                "(title:QUERY1~ OR content:QUERY1~) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1~ QUERY2").getQuery());
        assertEquals(
                "(title:QUERY1~ OR content:QUERY1~) AND (title:QUERY2~ OR content:QUERY2~)",
                queryHelperImpl.buildQuery("QUERY1~ QUERY2~").getQuery());

        assertEquals("mimetype:QUERY1~",
                queryHelperImpl.buildQuery("mimetype:QUERY1~").getQuery());
        assertEquals("mimetype:QUERY1~ AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("mimetype:QUERY1~ QUERY2")
                        .getQuery());

        assertEquals("title:QUERY1\\ QUERY2~ OR content:QUERY1\\ QUERY2~",
                queryHelperImpl.buildQuery("\"QUERY1 QUERY2\"~").getQuery());
        assertEquals("title:QUERY1~ OR content:QUERY1~", queryHelperImpl
                .buildQuery("\"QUERY1~\"").getQuery());

        // ~0.8

        assertEquals("title:QUERY~0.8 OR content:QUERY~0.8", queryHelperImpl
                .buildQuery("QUERY~0.8").getQuery());
        assertEquals(
                "(title:QUERY1~0.8 OR content:QUERY1~0.8) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1~0.8 QUERY2").getQuery());
        assertEquals(
                "(title:QUERY1~0.5 OR content:QUERY1~0.5) AND (title:QUERY2~0.8 OR content:QUERY2~0.8)",
                queryHelperImpl.buildQuery("QUERY1~0.5 QUERY2~0.8").getQuery());

        assertEquals("mimetype:QUERY1~0.8",
                queryHelperImpl.buildQuery("mimetype:QUERY1~0.8").getQuery());
        assertEquals(
                "mimetype:QUERY1~0.8 AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("mimetype:QUERY1~0.8 QUERY2")
                        .getQuery());

        assertEquals(
                "title:QUERY1\\ QUERY2~0.8 OR content:QUERY1\\ QUERY2~0.8",
                queryHelperImpl.buildQuery("\"QUERY1 QUERY2\"~0.8").getQuery());
        assertEquals("title:QUERY1~0.8 OR content:QUERY1~0.8", queryHelperImpl
                .buildQuery("\"QUERY1~0.8\"").getQuery());

        assertEquals("title:QUERY1~0.8 OR content:QUERY1~0.8", queryHelperImpl
                .buildQuery("\"QUERY1~0.8a\"").getQuery());
        assertEquals("title:QUERY1~ OR content:QUERY1~", queryHelperImpl
                .buildQuery("\"QUERY1~a\"").getQuery());

    }

    public void test_proximitySearches() {
        // ~10
        assertEquals("title:\"QUERY\"~10 OR content:\"QUERY\"~10",
                queryHelperImpl.buildQuery("QUERY~10").getQuery());
        assertEquals("title:\"QUERY\"~1 OR content:\"QUERY\"~1",
                queryHelperImpl.buildQuery("QUERY~1").getQuery());
        assertEquals("title:\"QUERY\"~5 OR content:\"QUERY\"~5",
                queryHelperImpl.buildQuery("QUERY~5.5").getQuery());
        assertEquals(
                "(title:\"QUERY1\"~10 OR content:\"QUERY1\"~10) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1~10 QUERY2").getQuery());
        assertEquals(
                "(title:\"QUERY1\"~5 OR content:\"QUERY1\"~5) AND (title:\"QUERY2\"~10 OR content:\"QUERY2\"~10)",
                queryHelperImpl.buildQuery("QUERY1~5 QUERY2~10").getQuery());

        assertEquals("mimetype:\"QUERY1\"~10",
                queryHelperImpl.buildQuery("mimetype:QUERY1~10").getQuery());
        assertEquals(
                "mimetype:\"QUERY1\"~10 AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("mimetype:QUERY1~10 QUERY2")
                        .getQuery());

        assertEquals(
                "title:\"QUERY1\\ QUERY2\"~10 OR content:\"QUERY1\\ QUERY2\"~10",
                queryHelperImpl.buildQuery("\"QUERY1 QUERY2\"~10").getQuery());
        assertEquals("title:\"QUERY1\"~10 OR content:\"QUERY1\"~10",
                queryHelperImpl.buildQuery("\"QUERY1~10\"").getQuery());

        assertEquals("title:\"QUERY1\"~10 OR content:\"QUERY1\"~10",
                queryHelperImpl.buildQuery("\"QUERY1~10a\"").getQuery());

    }

    public void test_rangeSearches() {
        String rangeQuery;
        // mod_date:[20020101 TO 20030101]
        assertEquals(
                "title:[20020101 TO 20030101] OR content:[20020101 TO 20030101]",
                queryHelperImpl.buildQuery("[20020101 TO 20030101]").getQuery());
        assertEquals("lastModified:[20020101 TO 20030101]", queryHelperImpl
                .buildQuery("lastModified:[20020101 TO 20030101]").getQuery());
        assertEquals(
                "(title:QUERY OR content:QUERY) AND lastModified:[20020101 TO 20030101]",
                queryHelperImpl.buildQuery(
                        "QUERY lastModified:[20020101 TO 20030101]").getQuery());
        assertEquals("title:{Aida TO Carmen} OR content:{Aida TO Carmen}",
                queryHelperImpl.buildQuery("{Aida TO Carmen}").getQuery());
        assertEquals("lastModified:{Aida TO Carmen}", queryHelperImpl
                .buildQuery("lastModified:{Aida TO Carmen}").getQuery());
        assertEquals(
                "(title:QUERY OR content:QUERY) AND title:{Aida TO Carmen}",
                queryHelperImpl.buildQuery("QUERY title:{Aida TO Carmen}")
                        .getQuery());
        assertEquals("lastModified:[20020101 TO abc]", queryHelperImpl
                .buildQuery("lastModified:[20020101 TO abc]").getQuery());
        assertEquals("lastModified:[abc TO 20020101]", queryHelperImpl
                .buildQuery("lastModified:[abc TO 20020101]").getQuery());
        assertEquals("lastModified:[20020101 TO *]", queryHelperImpl
                .buildQuery("lastModified:[20020101 TO *]").getQuery());
        assertEquals("lastModified:[* TO 20020101]", queryHelperImpl
                .buildQuery("lastModified:[* TO 20020101]").getQuery());

        rangeQuery = "(content:[1 TO 2] OR content:[3 TO 4]) AND (content:[5 TO 6] OR content:[7 TO 8])";
        assertEquals(rangeQuery, queryHelperImpl.buildQuery(rangeQuery)
                .getQuery());

        try {
            queryHelperImpl.buildQuery("lastModified:[20020101 TO]").getQuery();
            fail();
        } catch (final InvalidQueryException e) {
        }
        try {
            queryHelperImpl.buildQuery("lastModified:[TO 20030101]").getQuery();
            fail();
        } catch (final InvalidQueryException e) {
        }
        try {
            queryHelperImpl.buildQuery("lastModified:[20020101]").getQuery();
            fail();
        } catch (final InvalidQueryException e) {
        }
        try {
            queryHelperImpl.buildQuery("lastModified:[20030101]").getQuery();
            fail();
        } catch (final InvalidQueryException e) {
        }

        // mod_date:{20020101 TO 20030101}
        assertEquals(
                "title:{20020101 TO 20030101} OR content:{20020101 TO 20030101}",
                queryHelperImpl.buildQuery("{20020101 TO 20030101}").getQuery());
        assertEquals("lastModified:{20020101 TO 20030101}", queryHelperImpl
                .buildQuery("lastModified:{20020101 TO 20030101}").getQuery());
        assertEquals(
                "(title:QUERY OR content:QUERY) AND lastModified:{20020101 TO 20030101}",
                queryHelperImpl.buildQuery(
                        "QUERY lastModified:{20020101 TO 20030101}").getQuery());
        assertEquals("title:{Aida TO Carmen} OR content:{Aida TO Carmen}",
                queryHelperImpl.buildQuery("{Aida TO Carmen}").getQuery());
        assertEquals("lastModified:{Aida TO Carmen}", queryHelperImpl
                .buildQuery("lastModified:{Aida TO Carmen}").getQuery());
        assertEquals(
                "(title:QUERY OR content:QUERY) AND title:{Aida TO Carmen}",
                queryHelperImpl.buildQuery("QUERY title:{Aida TO Carmen}")
                        .getQuery());
        assertEquals("lastModified:{20020101 TO abc}", queryHelperImpl
                .buildQuery("lastModified:{20020101 TO abc}").getQuery());
        assertEquals("lastModified:{abc TO 20020101}", queryHelperImpl
                .buildQuery("lastModified:{abc TO 20020101}").getQuery());
        assertEquals("lastModified:{20020101 TO *}", queryHelperImpl
                .buildQuery("lastModified:{20020101 TO *}").getQuery());
        assertEquals("lastModified:{* TO 20020101}", queryHelperImpl
                .buildQuery("lastModified:{* TO 20020101}").getQuery());

        rangeQuery = "(content:{1 TO 2} OR content:{3 TO 4}) AND (content:{5 TO 6} OR content:{7 TO 8})";
        assertEquals(rangeQuery, queryHelperImpl.buildQuery(rangeQuery)
                .getQuery());

        try {
            queryHelperImpl.buildQuery("lastModified:{20020101 TO}").getQuery();
            fail();
        } catch (final InvalidQueryException e) {
        }
        try {
            queryHelperImpl.buildQuery("lastModified:{TO 20030101}").getQuery();
            fail();
        } catch (final InvalidQueryException e) {
        }
        try {
            queryHelperImpl.buildQuery("lastModified:{20020101}").getQuery();
            fail();
        } catch (final InvalidQueryException e) {
        }
        try {
            queryHelperImpl.buildQuery("lastModified:{20030101}").getQuery();
            fail();
        } catch (final InvalidQueryException e) {
        }

        rangeQuery = "(content:[1 TO 2] OR content:{3 TO 4}) AND (content:{5 TO 6} OR content:[7 TO 8])";
        assertEquals(rangeQuery, queryHelperImpl.buildQuery(rangeQuery)
                .getQuery());
    }

    public void test_boosting() {
        // ^1000 ""^1000
        assertEquals("title:QUERY^1000 OR content:QUERY^1000", queryHelperImpl
                .buildQuery("QUERY^1000").getQuery());
        assertEquals(
                "(title:QUERY1^1000 OR content:QUERY1^1000) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1^1000 QUERY2").getQuery());
        assertEquals(
                "(title:QUERY1^500 OR content:QUERY1^500) AND (title:QUERY2^1000 OR content:QUERY2^1000)",
                queryHelperImpl.buildQuery("QUERY1^500 QUERY2^1000").getQuery());

        assertEquals("mimetype:QUERY1^1000",
                queryHelperImpl.buildQuery("mimetype:QUERY1^1000").getQuery());
        assertEquals(
                "mimetype:QUERY1^1000 AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("mimetype:QUERY1^1000 QUERY2")
                        .getQuery());

        assertEquals(
                "title:QUERY1\\ QUERY2^1000 OR content:QUERY1\\ QUERY2^1000",
                queryHelperImpl.buildQuery("\"QUERY1 QUERY2\"^1000").getQuery());
        assertEquals("title:QUERY1^1000 OR content:QUERY1^1000",
                queryHelperImpl.buildQuery("\"QUERY1^1000\"").getQuery());
    }

    public void test_reserved() {
        for (int i = 0; i < Constants.RESERVED.length - 1; i++) {
            try {
                assertEquals("title:\\" + Constants.RESERVED[i]
                        + " OR content:\\" + Constants.RESERVED[i],
                        queryHelperImpl.buildQuery(Constants.RESERVED[i])
                                .getQuery());
            } catch (final InvalidQueryException e) {
                if (Constants.RESERVED[i].equals("\"")
                        && e.getMessageCode().equals(
                                "errors.invalid_query_quoted")) {
                    assertEquals(
                            "title:\\" + Constants.RESERVED[i]
                                    + " OR content:\\" + Constants.RESERVED[i],
                            queryHelperImpl.buildQuery(
                                    "\\" + Constants.RESERVED[i]).getQuery());
                    continue;
                } else if (Constants.RESERVED[i].equals("{")
                        && e.getMessageCode().equals(
                                "errors.invalid_query_curly_bracket")) {
                    assertEquals(
                            "title:\\" + Constants.RESERVED[i]
                                    + " OR content:\\" + Constants.RESERVED[i],
                            queryHelperImpl.buildQuery(
                                    "\\" + Constants.RESERVED[i]).getQuery());
                    continue;
                } else if (Constants.RESERVED[i].equals("[")
                        && e.getMessageCode().equals(
                                "errors.invalid_query_square_bracket")) {
                    assertEquals(
                            "title:\\" + Constants.RESERVED[i]
                                    + " OR content:\\" + Constants.RESERVED[i],
                            queryHelperImpl.buildQuery(
                                    "\\" + Constants.RESERVED[i]).getQuery());
                    continue;
                } else if (Constants.RESERVED[i].equals("(")
                        && e.getMessageCode().equals(
                                "errors.invalid_query_parenthesis")) {
                    assertEquals(
                            "title:\\" + Constants.RESERVED[i]
                                    + " OR content:\\" + Constants.RESERVED[i],
                            queryHelperImpl.buildQuery(
                                    "\\" + Constants.RESERVED[i]).getQuery());
                    continue;
                }
            }
        }
        assertEquals("title:\\: OR content:\\:", queryHelperImpl
                .buildQuery(":").getQuery());
    }

    public void test_or() {
        assertEquals("title:QUERY OR content:QUERY", queryHelperImpl
                .buildQuery("OR QUERY").getQuery());
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) OR (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1 OR QUERY2").getQuery());
        assertEquals("title:QUERY", queryHelperImpl
                .buildQuery("OR title:QUERY").getQuery());
        assertEquals("title:QUERY1 OR title:QUERY2", queryHelperImpl
                .buildQuery("title:QUERY1 OR title:QUERY2").getQuery());
        assertEquals("(title:QUERY1 OR content:QUERY1) OR title:QUERY2",
                queryHelperImpl.buildQuery("QUERY1 OR title:QUERY2").getQuery());
        assertEquals("mimetype:QUERY1 OR title:QUERY2", queryHelperImpl
                .buildQuery("mimetype:QUERY1 OR title:QUERY2").getQuery());
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) OR (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildQuery("QUERY1 OR QUERY2 QUERY3")
                        .getQuery());
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) OR (title:QUERY2 OR content:QUERY2) OR (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildQuery("QUERY1 OR QUERY2 OR QUERY3")
                        .getQuery());
    }

    public void test_and() {
        assertEquals("title:QUERY OR content:QUERY", queryHelperImpl
                .buildQuery("AND QUERY").getQuery());
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1 AND QUERY2").getQuery());
        assertEquals("title:QUERY",
                queryHelperImpl.buildQuery("AND title:QUERY").getQuery());
        assertEquals("title:QUERY1 AND title:QUERY2", queryHelperImpl
                .buildQuery("title:QUERY1 AND title:QUERY2").getQuery());
        assertEquals("(title:QUERY1 OR content:QUERY1) AND title:QUERY2",
                queryHelperImpl.buildQuery("QUERY1 AND title:QUERY2")
                        .getQuery());
        assertEquals("mimetype:QUERY1 AND title:QUERY2", queryHelperImpl
                .buildQuery("mimetype:QUERY1 AND title:QUERY2").getQuery());
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildQuery("QUERY1 AND QUERY2 QUERY3")
                        .getQuery());
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildQuery("QUERY1 AND QUERY2 AND QUERY3")
                        .getQuery());
    }

    public void test_not() {
        assertEquals("NOT (title:QUERY OR content:QUERY)", queryHelperImpl
                .buildQuery("NOT QUERY").getQuery());
        assertEquals("NOT title:QUERY",
                queryHelperImpl.buildQuery("NOT title:QUERY").getQuery());
        assertEquals(
                "(title:QUERY2 OR content:QUERY2) AND NOT (title:QUERY1 OR content:QUERY1)",
                queryHelperImpl.buildQuery("NOT QUERY1 QUERY2").getQuery());
        assertEquals(
                "(title:QUERY2 OR content:QUERY2) AND NOT (title:QUERY1 OR content:QUERY1)",
                queryHelperImpl.buildQuery("NOT QUERY1 OR QUERY2").getQuery());
        assertEquals(
                "NOT (title:QUERY1 OR content:QUERY1) AND NOT (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("NOT QUERY1 NOT QUERY2").getQuery());
        assertEquals(
                "NOT (title:QUERY1 OR content:QUERY1) AND NOT (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildQuery("NOT QUERY1 OR NOT QUERY2")
                        .getQuery());
        assertEquals(
                "(title:QUERY2 OR content:QUERY2) AND NOT (title:QUERY1 OR content:QUERY1) AND NOT (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildQuery("NOT QUERY1 QUERY2 NOT QUERY3")
                        .getQuery());
        assertEquals("NOT mimetype:QUERY",
                queryHelperImpl.buildQuery("NOT mimetype:QUERY").getQuery());
        assertEquals(
                "NOT mimetype:QUERY1 AND NOT title:QUERY2",
                queryHelperImpl.buildQuery(
                        "NOT mimetype:QUERY1 NOT title:QUERY2").getQuery());
        assertEquals(
                "(title:QUERY2 OR content:QUERY2) AND NOT mimetype:QUERY1",
                queryHelperImpl.buildQuery("NOT mimetype:QUERY1 QUERY2")
                        .getQuery());
        assertEquals(
                "mimetype:QUERY2 AND NOT (title:QUERY1 OR content:QUERY1)",
                queryHelperImpl.buildQuery("NOT QUERY1 mimetype:QUERY2")
                        .getQuery());

    }

    public void test_escapeValue() {
        final String[] targets = new String[] { "+", "-", "&&", "||", "!", "(",
                ")", "{", "}", "[", "]", "^", "\"", "~", ":", "\\", " ",
                "\u3000" };
        for (final String target : targets) {
            assertEquals("abc\\" + target + "123",
                    QueryUtil.escapeValue("abc" + target + "123"));
        }
        for (final String target : targets) {
            assertEquals("abc\\" + target,
                    QueryUtil.escapeValue("abc" + target));
        }
        for (final String target : targets) {
            assertEquals("\\" + target + "123",
                    QueryUtil.escapeValue(target + "123"));
        }
        for (final String target : targets) {
            assertEquals(
                    "abc\\" + target + "123\\" + target + "ABC",
                    QueryUtil.escapeValue("abc" + target + "123" + target
                            + "ABC"));
        }
    }

    public void test_escapeRangeValue() {
        final String[] targets = new String[] { "&&", "||", "!", "(", ")", "{",
                "}", "[", "]", "\"", "~", ":", "\\", " ", "\u3000" };
        for (final String target : targets) {
            assertEquals("abc\\" + target + "123",
                    QueryUtil.escapeValue("abc" + target + "123"));
        }
        for (final String target : targets) {
            assertEquals("abc\\" + target,
                    QueryUtil.escapeValue("abc" + target));
        }
        for (final String target : targets) {
            assertEquals("\\" + target + "123",
                    QueryUtil.escapeValue(target + "123"));
        }
        for (final String target : targets) {
            assertEquals(
                    "abc\\" + target + "123\\" + target + "ABC",
                    QueryUtil.escapeValue("abc" + target + "123" + target
                            + "ABC"));
        }
    }

    public void test_buildFacet() {
        assertEquals("", queryHelperImpl.buildFacetQuery(""));

        assertEquals("title:QUERY OR content:QUERY",
                queryHelperImpl.buildFacetQuery("QUERY"));
        assertEquals("title:QUERY OR content:QUERY",
                queryHelperImpl.buildFacetQuery("QUERY "));
        assertEquals("title:QUERY OR content:QUERY",
                queryHelperImpl.buildFacetQuery(" QUERY"));

        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("QUERY1 QUERY2"));
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("QUERY1 QUERY2 "));
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery(" QUERY1 QUERY2"));

        assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2",
                queryHelperImpl.buildFacetQuery("\"QUERY1 QUERY2\""));
        assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2",
                queryHelperImpl.buildFacetQuery("\"QUERY1 QUERY2\" "));
        assertEquals("title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2",
                queryHelperImpl.buildFacetQuery(" \"QUERY1 QUERY2\""));

        assertEquals(
                "(title:QUERY1\\ QUERY2 OR content:QUERY1\\ QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildFacetQuery("\"QUERY1 QUERY2\" QUERY3"));
    }

    public void test_buildFacet_fullwidthSpace() {

        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("QUERY1\u3000QUERY2"));
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("QUERY1\u3000QUERY2\u3000"));
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("\u3000QUERY1\u3000QUERY2"));

        assertEquals(
                "title:QUERY1\\\u3000QUERY2 OR content:QUERY1\\\u3000QUERY2",
                queryHelperImpl.buildFacetQuery("\"QUERY1\u3000QUERY2\""));

        assertEquals(
                "(title:QUERY1\\\u3000QUERY2 OR content:QUERY1\\\u3000QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl
                        .buildFacetQuery("\"QUERY1\u3000QUERY2\"\u3000QUERY3"));

    }

    public void test_buildFacet_prefix() {
        assertEquals("mimetype:QUERY1",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1"));
        assertEquals("mimetype:QUERY1 AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1 QUERY2"));
        assertEquals(
                "mimetype:QUERY1 AND (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl
                        .buildFacetQuery("mimetype:QUERY1 QUERY2 QUERY3"));
        assertEquals(
                "mimetype:QUERY1 AND host:QUERY2 AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl
                        .buildFacetQuery("mimetype:QUERY1 host:QUERY2 QUERY3"));
        assertEquals(
                "mimetype:QUERY1\\ QUERY2 AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl
                        .buildFacetQuery("mimetype:\"QUERY1 QUERY2\" QUERY3"));

    }

    public void test_buildFacet_prefix_unknown() {
        assertEquals("title:site\\: OR content:site\\:",
                queryHelperImpl.buildFacetQuery("site:"));
        assertEquals("title:hoge\\:QUERY1 OR content:hoge\\:QUERY1",
                queryHelperImpl.buildFacetQuery("hoge:QUERY1"));
        assertEquals(
                "(title:hoge\\:QUERY1 OR content:hoge\\:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("hoge:QUERY1 QUERY2"));
        assertEquals(
                "(title:hoge\\:QUERY1 OR content:hoge\\:QUERY1) AND (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildFacetQuery("hoge:QUERY1 QUERY2 QUERY3"));
        assertEquals(
                "(title:hoge\\:QUERY1 OR content:hoge\\:QUERY1) AND host:QUERY2 AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl
                        .buildFacetQuery("hoge:QUERY1 host:QUERY2 QUERY3"));
        assertEquals(
                "(title:hoge\\:QUERY1\\ QUERY2 OR content:hoge\\:QUERY1\\ QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl
                        .buildFacetQuery("hoge:\"QUERY1 QUERY2\" QUERY3"));

    }

    public void test_buildFacet_sortField() {
        String query;
        String searchQuery;

        query = "";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("", searchQuery);

        query = "sort:contentLength";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("", searchQuery);

        query = "sort:contentLength.desc";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("", searchQuery);

        query = "sort:contentLength.asc,lastModified";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("", searchQuery);

        query = "QUERY sort:contentLength";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("title:QUERY OR content:QUERY", searchQuery);

        query = "QUERY sort:contentLength.desc";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("title:QUERY OR content:QUERY", searchQuery);

        query = "QUERY sort:contentLength.asc,lastModified";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("title:QUERY OR content:QUERY", searchQuery);

        query = "QUERY mimetype:QUERY1 sort:contentLength";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("(title:QUERY OR content:QUERY) AND mimetype:QUERY1",
                searchQuery);

        query = "QUERY sort:contentLength.desc  mimetype:QUERY1";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("(title:QUERY OR content:QUERY) AND mimetype:QUERY1",
                searchQuery);

        query = "QUERY sort:contentLength.asc,lastModified mimetype:QUERY1";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("(title:QUERY OR content:QUERY) AND mimetype:QUERY1",
                searchQuery);
    }

    public void test_buildFacet_sortField_invalid() {
        String query;
        String searchQuery;

        query = "sort:hoge";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("", searchQuery);

        query = "sort:contentLength.hoge";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("", searchQuery);

        query = "sort:contentLength.asc,hoge";
        searchQuery = queryHelperImpl.buildFacetQuery(query);
        assertEquals("", searchQuery);
    }

    public void test_buildFacet_wildcardSearches() {
        // *

        assertEquals("title:\\* OR content:\\*",
                queryHelperImpl.buildFacetQuery("*"));
        assertEquals("title:QUERY* OR content:QUERY*",
                queryHelperImpl.buildFacetQuery("QUERY* "));
        assertEquals("title:Q*ERY OR content:Q*ERY",
                queryHelperImpl.buildFacetQuery(" Q*ERY"));

        assertEquals(
                "(title:Q*ERY1 OR content:Q*ERY1) AND (title:Q*ERY2 OR content:Q*ERY2)",
                queryHelperImpl.buildFacetQuery("Q*ERY1 Q*ERY2"));

        assertEquals("title:Q*ERY1\\ Q*ERY2 OR content:Q*ERY1\\ Q*ERY2",
                queryHelperImpl.buildFacetQuery("\"Q*ERY1 Q*ERY2\""));

        assertEquals(
                "(title:Q*ERY1\\ Q*ERY2 OR content:Q*ERY1\\ Q*ERY2) AND (title:Q*ERY3 OR content:Q*ERY3)",
                queryHelperImpl.buildFacetQuery("\"Q*ERY1 Q*ERY2\" Q*ERY3"));

        assertEquals("mimetype:QUERY1*",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1*"));
        assertEquals("mimetype:QUERY1* AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1* QUERY2"));

        assertEquals("title:\\*QUERY1 OR content:\\*QUERY1",
                queryHelperImpl.buildFacetQuery("*QUERY1"));
        assertEquals("title:\\*QUERY1* OR content:\\*QUERY1*",
                queryHelperImpl.buildFacetQuery("*QUERY1*"));

        // ?

        assertEquals("title:\\? OR content:\\?",
                queryHelperImpl.buildFacetQuery("?"));
        assertEquals("title:QUERY? OR content:QUERY?",
                queryHelperImpl.buildFacetQuery("QUERY? "));
        assertEquals("title:Q?ERY OR content:Q?ERY",
                queryHelperImpl.buildFacetQuery(" Q?ERY"));

        assertEquals(
                "(title:Q?ERY1 OR content:Q?ERY1) AND (title:Q?ERY2 OR content:Q?ERY2)",
                queryHelperImpl.buildFacetQuery("Q?ERY1 Q?ERY2"));

        assertEquals("title:Q?ERY1\\ Q?ERY2 OR content:Q?ERY1\\ Q?ERY2",
                queryHelperImpl.buildFacetQuery("\"Q?ERY1 Q?ERY2\""));

        assertEquals(
                "(title:Q?ERY1\\ Q?ERY2 OR content:Q?ERY1\\ Q?ERY2) AND (title:Q?ERY3 OR content:Q?ERY3)",
                queryHelperImpl.buildFacetQuery("\"Q?ERY1 Q?ERY2\" Q?ERY3"));

        assertEquals("mimetype:QUERY1?",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1?"));
        assertEquals("mimetype:QUERY1? AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1? QUERY2"));

        assertEquals("title:\\?QUERY1 OR content:\\?QUERY1",
                queryHelperImpl.buildFacetQuery("?QUERY1"));
        assertEquals("title:\\?QUERY1? OR content:\\?QUERY1?",
                queryHelperImpl.buildFacetQuery("?QUERY1?"));
    }

    public void test_buildFacet_fuzzySearches() {
        // ~

        assertEquals("title:QUERY~ OR content:QUERY~",
                queryHelperImpl.buildFacetQuery("QUERY~"));
        assertEquals(
                "(title:QUERY1~ OR content:QUERY1~) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("QUERY1~ QUERY2"));
        assertEquals(
                "(title:QUERY1~ OR content:QUERY1~) AND (title:QUERY2~ OR content:QUERY2~)",
                queryHelperImpl.buildFacetQuery("QUERY1~ QUERY2~"));

        assertEquals("mimetype:QUERY1~",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1~"));
        assertEquals("mimetype:QUERY1~ AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1~ QUERY2"));

        assertEquals("title:QUERY1\\ QUERY2~ OR content:QUERY1\\ QUERY2~",
                queryHelperImpl.buildFacetQuery("\"QUERY1 QUERY2\"~"));
        assertEquals("title:QUERY1~ OR content:QUERY1~",
                queryHelperImpl.buildFacetQuery("\"QUERY1~\""));

        // ~0.8

        assertEquals("title:QUERY~0.8 OR content:QUERY~0.8",
                queryHelperImpl.buildFacetQuery("QUERY~0.8"));
        assertEquals(
                "(title:QUERY1~0.8 OR content:QUERY1~0.8) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("QUERY1~0.8 QUERY2"));
        assertEquals(
                "(title:QUERY1~0.5 OR content:QUERY1~0.5) AND (title:QUERY2~0.8 OR content:QUERY2~0.8)",
                queryHelperImpl.buildFacetQuery("QUERY1~0.5 QUERY2~0.8"));

        assertEquals("mimetype:QUERY1~0.8",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1~0.8"));
        assertEquals(
                "mimetype:QUERY1~0.8 AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1~0.8 QUERY2"));

        assertEquals(
                "title:QUERY1\\ QUERY2~0.8 OR content:QUERY1\\ QUERY2~0.8",
                queryHelperImpl.buildFacetQuery("\"QUERY1 QUERY2\"~0.8"));
        assertEquals("title:QUERY1~0.8 OR content:QUERY1~0.8",
                queryHelperImpl.buildFacetQuery("\"QUERY1~0.8\""));

        assertEquals("title:QUERY1~0.8 OR content:QUERY1~0.8",
                queryHelperImpl.buildFacetQuery("\"QUERY1~0.8a\""));
        assertEquals("title:QUERY1~ OR content:QUERY1~",
                queryHelperImpl.buildFacetQuery("\"QUERY1~a\""));

    }

    public void test_buildFacet_proximitySearches() {
        // ~10
        assertEquals("title:\"QUERY\"~10 OR content:\"QUERY\"~10",
                queryHelperImpl.buildFacetQuery("QUERY~10"));
        assertEquals("title:\"QUERY\"~1 OR content:\"QUERY\"~1",
                queryHelperImpl.buildFacetQuery("QUERY~1"));
        assertEquals("title:\"QUERY\"~5 OR content:\"QUERY\"~5",
                queryHelperImpl.buildFacetQuery("QUERY~5.5"));
        assertEquals(
                "(title:\"QUERY1\"~10 OR content:\"QUERY1\"~10) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("QUERY1~10 QUERY2"));
        assertEquals(
                "(title:\"QUERY1\"~5 OR content:\"QUERY1\"~5) AND (title:\"QUERY2\"~10 OR content:\"QUERY2\"~10)",
                queryHelperImpl.buildFacetQuery("QUERY1~5 QUERY2~10"));

        assertEquals("mimetype:\"QUERY1\"~10",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1~10"));
        assertEquals(
                "mimetype:\"QUERY1\"~10 AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1~10 QUERY2"));

        assertEquals(
                "title:\"QUERY1\\ QUERY2\"~10 OR content:\"QUERY1\\ QUERY2\"~10",
                queryHelperImpl.buildFacetQuery("\"QUERY1 QUERY2\"~10"));
        assertEquals("title:\"QUERY1\"~10 OR content:\"QUERY1\"~10",
                queryHelperImpl.buildFacetQuery("\"QUERY1~10\""));

        assertEquals("title:\"QUERY1\"~10 OR content:\"QUERY1\"~10",
                queryHelperImpl.buildFacetQuery("\"QUERY1~10a\""));

    }

    public void test_buildFacet_rangeSearches() {
        // mod_date:[20020101 TO 20030101]
        assertEquals(
                "title:[20020101 TO 20030101] OR content:[20020101 TO 20030101]",
                queryHelperImpl.buildFacetQuery("[20020101 TO 20030101]"));
        assertEquals("lastModified:[20020101 TO 20030101]",
                queryHelperImpl
                        .buildFacetQuery("lastModified:[20020101 TO 20030101]"));
        assertEquals(
                "(title:QUERY OR content:QUERY) AND lastModified:[20020101 TO 20030101]",
                queryHelperImpl
                        .buildFacetQuery("QUERY lastModified:[20020101 TO 20030101]"));

        // TODO more..

        // title:{Aida TO Carmen}
        assertEquals("title:{Aida TO Carmen} OR content:{Aida TO Carmen}",
                queryHelperImpl.buildFacetQuery("{Aida TO Carmen}"));
        assertEquals("lastModified:{Aida TO Carmen}",
                queryHelperImpl
                        .buildFacetQuery("lastModified:{Aida TO Carmen}"));
        assertEquals(
                "(title:QUERY OR content:QUERY) AND title:{Aida TO Carmen}",
                queryHelperImpl.buildFacetQuery("QUERY title:{Aida TO Carmen}"));

        // TODO more..
    }

    public void test_buildFacet_boosting() {
        // ^1000 ""^1000
        assertEquals("title:QUERY^1000 OR content:QUERY^1000",
                queryHelperImpl.buildFacetQuery("QUERY^1000"));
        assertEquals(
                "(title:QUERY1^1000 OR content:QUERY1^1000) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("QUERY1^1000 QUERY2"));
        assertEquals(
                "(title:QUERY1^500 OR content:QUERY1^500) AND (title:QUERY2^1000 OR content:QUERY2^1000)",
                queryHelperImpl.buildFacetQuery("QUERY1^500 QUERY2^1000"));

        assertEquals("mimetype:QUERY1^1000",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1^1000"));
        assertEquals(
                "mimetype:QUERY1^1000 AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("mimetype:QUERY1^1000 QUERY2"));

        assertEquals(
                "title:QUERY1\\ QUERY2^1000 OR content:QUERY1\\ QUERY2^1000",
                queryHelperImpl.buildFacetQuery("\"QUERY1 QUERY2\"^1000"));
        assertEquals("title:QUERY1^1000 OR content:QUERY1^1000",
                queryHelperImpl.buildFacetQuery("\"QUERY1^1000\""));
    }

    public void test_buildFacet_reserved() {
        for (int i = 0; i < Constants.RESERVED.length - 1; i++) {
            try {
                assertEquals("title:\\" + Constants.RESERVED[i]
                        + " OR content:\\" + Constants.RESERVED[i],
                        queryHelperImpl.buildFacetQuery(Constants.RESERVED[i]));
            } catch (final InvalidQueryException e) {
                if (Constants.RESERVED[i].equals("\"")
                        && e.getMessageCode().equals(
                                "errors.invalid_query_quoted")) {
                    assertEquals(
                            "title:\\" + Constants.RESERVED[i]
                                    + " OR content:\\" + Constants.RESERVED[i],
                            queryHelperImpl.buildFacetQuery("\\"
                                    + Constants.RESERVED[i]));
                    continue;
                } else if (Constants.RESERVED[i].equals("{")
                        && e.getMessageCode().equals(
                                "errors.invalid_query_curly_bracket")) {
                    assertEquals(
                            "title:\\" + Constants.RESERVED[i]
                                    + " OR content:\\" + Constants.RESERVED[i],
                            queryHelperImpl.buildFacetQuery("\\"
                                    + Constants.RESERVED[i]));
                    continue;
                } else if (Constants.RESERVED[i].equals("[")
                        && e.getMessageCode().equals(
                                "errors.invalid_query_square_bracket")) {
                    assertEquals(
                            "title:\\" + Constants.RESERVED[i]
                                    + " OR content:\\" + Constants.RESERVED[i],
                            queryHelperImpl.buildFacetQuery("\\"
                                    + Constants.RESERVED[i]));
                    continue;
                } else if (Constants.RESERVED[i].equals("(")
                        && e.getMessageCode().equals(
                                "errors.invalid_query_parenthesis")) {
                    assertEquals(
                            "title:\\" + Constants.RESERVED[i]
                                    + " OR content:\\" + Constants.RESERVED[i],
                            queryHelperImpl.buildFacetQuery("\\"
                                    + Constants.RESERVED[i]));
                    continue;
                }
            }
        }
        assertEquals("title:\\: OR content:\\:",
                queryHelperImpl.buildFacetQuery(":"));
    }

    public void test_buildFacet_or() {
        assertEquals("title:QUERY OR content:QUERY",
                queryHelperImpl.buildFacetQuery("OR QUERY"));
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) OR (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("QUERY1 OR QUERY2"));
        assertEquals("title:QUERY",
                queryHelperImpl.buildFacetQuery("OR title:QUERY"));
        assertEquals("title:QUERY1 OR title:QUERY2",
                queryHelperImpl.buildFacetQuery("title:QUERY1 OR title:QUERY2"));
        assertEquals("(title:QUERY1 OR content:QUERY1) OR title:QUERY2",
                queryHelperImpl.buildFacetQuery("QUERY1 OR title:QUERY2"));
        assertEquals("mimetype:QUERY1 OR title:QUERY2",
                queryHelperImpl
                        .buildFacetQuery("mimetype:QUERY1 OR title:QUERY2"));
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) OR (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildFacetQuery("QUERY1 OR QUERY2 QUERY3"));
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) OR (title:QUERY2 OR content:QUERY2) OR (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildFacetQuery("QUERY1 OR QUERY2 OR QUERY3"));
    }

    public void test_buildFacet_and() {
        assertEquals("title:QUERY OR content:QUERY",
                queryHelperImpl.buildFacetQuery("AND QUERY"));
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("QUERY1 AND QUERY2"));
        assertEquals("title:QUERY",
                queryHelperImpl.buildFacetQuery("AND title:QUERY"));
        assertEquals("title:QUERY1 AND title:QUERY2",
                queryHelperImpl
                        .buildFacetQuery("title:QUERY1 AND title:QUERY2"));
        assertEquals("(title:QUERY1 OR content:QUERY1) AND title:QUERY2",
                queryHelperImpl.buildFacetQuery("QUERY1 AND title:QUERY2"));
        assertEquals("mimetype:QUERY1 AND title:QUERY2",
                queryHelperImpl
                        .buildFacetQuery("mimetype:QUERY1 AND title:QUERY2"));
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildFacetQuery("QUERY1 AND QUERY2 QUERY3"));
        assertEquals(
                "(title:QUERY1 OR content:QUERY1) AND (title:QUERY2 OR content:QUERY2) AND (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildFacetQuery("QUERY1 AND QUERY2 AND QUERY3"));
    }

    public void test_buildFacet_not() {
        assertEquals("NOT (title:QUERY OR content:QUERY)",
                queryHelperImpl.buildFacetQuery("NOT QUERY"));
        assertEquals("NOT title:QUERY",
                queryHelperImpl.buildFacetQuery("NOT title:QUERY"));
        assertEquals(
                "(title:QUERY2 OR content:QUERY2) AND NOT (title:QUERY1 OR content:QUERY1)",
                queryHelperImpl.buildFacetQuery("NOT QUERY1 QUERY2"));
        assertEquals(
                "(title:QUERY2 OR content:QUERY2) AND NOT (title:QUERY1 OR content:QUERY1)",
                queryHelperImpl.buildFacetQuery("NOT QUERY1 OR QUERY2"));
        assertEquals(
                "NOT (title:QUERY1 OR content:QUERY1) AND NOT (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("NOT QUERY1 NOT QUERY2"));
        assertEquals(
                "NOT (title:QUERY1 OR content:QUERY1) AND NOT (title:QUERY2 OR content:QUERY2)",
                queryHelperImpl.buildFacetQuery("NOT QUERY1 OR NOT QUERY2"));
        assertEquals(
                "(title:QUERY2 OR content:QUERY2) AND NOT (title:QUERY1 OR content:QUERY1) AND NOT (title:QUERY3 OR content:QUERY3)",
                queryHelperImpl.buildFacetQuery("NOT QUERY1 QUERY2 NOT QUERY3"));
        assertEquals("NOT mimetype:QUERY",
                queryHelperImpl.buildFacetQuery("NOT mimetype:QUERY"));
        assertEquals(
                "NOT mimetype:QUERY1 AND NOT title:QUERY2",
                queryHelperImpl
                        .buildFacetQuery("NOT mimetype:QUERY1 NOT title:QUERY2"));
        assertEquals(
                "(title:QUERY2 OR content:QUERY2) AND NOT mimetype:QUERY1",
                queryHelperImpl.buildFacetQuery("NOT mimetype:QUERY1 QUERY2"));
        assertEquals(
                "mimetype:QUERY2 AND NOT (title:QUERY1 OR content:QUERY1)",
                queryHelperImpl.buildFacetQuery("NOT QUERY1 mimetype:QUERY2"));

    }

    public void test_inner_query() {
        assertEquals(
                "(title:bbb OR content:bbb) AND (title:ccc OR content:ccc)",
                queryHelperImpl.buildQuery("(bbb ccc)").getQuery());
        assertEquals(
                "(title:bbb OR content:bbb) OR (title:ccc OR content:ccc)",
                queryHelperImpl.buildQuery("(bbb OR ccc)").getQuery());
        assertEquals(
                "(title:bbb OR content:bbb) AND (title:ccc OR content:ccc)",
                queryHelperImpl.buildQuery("(bbb AND ccc)").getQuery());

        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
                queryHelperImpl.buildQuery("aaa (bbb ccc)").getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
                queryHelperImpl.buildQuery("aaa (bbb AND ccc)").getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
                queryHelperImpl.buildQuery("aaa AND (bbb AND ccc)").getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
                queryHelperImpl.buildQuery("aaa OR (bbb AND ccc)").getQuery());

        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc))",
                queryHelperImpl.buildQuery("aaa (bbb OR ccc)").getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc))",
                queryHelperImpl.buildQuery("aaa AND (bbb OR ccc)").getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc))",
                queryHelperImpl.buildQuery("aaa OR (bbb OR ccc)").getQuery());

        assertEquals(
                "((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("(bbb ccc) ddd").getQuery());
        assertEquals(
                "((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("(bbb AND ccc) ddd").getQuery());
        assertEquals(
                "((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("(bbb AND ccc) AND ddd").getQuery());
        assertEquals(
                "((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("(bbb AND ccc) OR ddd").getQuery());

        assertEquals(
                "((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("(bbb OR ccc) ddd").getQuery());
        assertEquals(
                "((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("(bbb OR ccc) AND ddd").getQuery());
        assertEquals(
                "((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("(bbb OR ccc) OR ddd").getQuery());

        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("aaa (bbb ccc) ddd").getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("aaa (bbb AND ccc) ddd").getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("aaa AND (bbb AND ccc) AND ddd")
                        .getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("aaa OR (bbb AND ccc) OR ddd")
                        .getQuery());

        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("aaa (bbb OR ccc) ddd").getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("aaa AND (bbb OR ccc) AND ddd")
                        .getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("aaa OR (bbb OR ccc) OR ddd")
                        .getQuery());

        assertEquals(
                "(title:aaa OR content:aaa) AND (label:bbb AND (title:ccc OR content:ccc))",
                queryHelperImpl.buildQuery("aaa (label:bbb ccc)").getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) AND (label:bbb AND (title:ccc OR content:ccc))",
                queryHelperImpl.buildQuery("aaa (label:bbb AND ccc)")
                        .getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) AND (label:bbb OR (title:ccc OR content:ccc))",
                queryHelperImpl.buildQuery("aaa (label:bbb OR ccc)").getQuery());

        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND label:ccc)",
                queryHelperImpl.buildQuery("aaa (bbb label:ccc)").getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND label:ccc)",
                queryHelperImpl.buildQuery("aaa (bbb AND label:ccc)")
                        .getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR label:ccc)",
                queryHelperImpl.buildQuery("aaa (bbb OR label:ccc)").getQuery());

        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND NOT (title:ccc OR content:ccc))",
                queryHelperImpl.buildQuery("aaa (bbb NOT ccc)").getQuery());

    }

    public void test_more_inner_query() {
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND ((title:ccc OR content:ccc) AND (title:ddd OR content:ddd)))",
                queryHelperImpl.buildQuery("aaa (bbb (ccc ddd))").getQuery());
        assertEquals(
                "(((title:aaa OR content:aaa) AND (title:bbb OR content:bbb)) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildQuery("((aaa bbb) ccc) ddd").getQuery());
    }

    public void test_inner_facetQuery() {
        assertEquals(
                "(title:bbb OR content:bbb) AND (title:ccc OR content:ccc)",
                queryHelperImpl.buildFacetQuery("(bbb ccc)"));
        assertEquals(
                "(title:bbb OR content:bbb) OR (title:ccc OR content:ccc)",
                queryHelperImpl.buildFacetQuery("(bbb OR ccc)"));
        assertEquals(
                "(title:bbb OR content:bbb) AND (title:ccc OR content:ccc)",
                queryHelperImpl.buildFacetQuery("(bbb AND ccc)"));

        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
                queryHelperImpl.buildFacetQuery("aaa (bbb ccc)"));
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
                queryHelperImpl.buildFacetQuery("aaa (bbb AND ccc)"));
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
                queryHelperImpl.buildFacetQuery("aaa AND (bbb AND ccc)"));
        assertEquals(
                "(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc))",
                queryHelperImpl.buildFacetQuery("aaa OR (bbb AND ccc)"));

        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc))",
                queryHelperImpl.buildFacetQuery("aaa (bbb OR ccc)"));
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc))",
                queryHelperImpl.buildFacetQuery("aaa AND (bbb OR ccc)"));
        assertEquals(
                "(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc))",
                queryHelperImpl.buildFacetQuery("aaa OR (bbb OR ccc)"));

        assertEquals(
                "((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("(bbb ccc) ddd"));
        assertEquals(
                "((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("(bbb AND ccc) ddd"));
        assertEquals(
                "((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("(bbb AND ccc) AND ddd"));
        assertEquals(
                "((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("(bbb AND ccc) OR ddd"));

        assertEquals(
                "((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("(bbb OR ccc) ddd"));
        assertEquals(
                "((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("(bbb OR ccc) AND ddd"));
        assertEquals(
                "((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("(bbb OR ccc) OR ddd"));

        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("aaa (bbb ccc) ddd"));
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("aaa (bbb AND ccc) ddd"));
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl
                        .buildFacetQuery("aaa AND (bbb AND ccc) AND ddd"));
        assertEquals(
                "(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) AND (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("aaa OR (bbb AND ccc) OR ddd"));

        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("aaa (bbb OR ccc) ddd"));
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("aaa AND (bbb OR ccc) AND ddd"));
        assertEquals(
                "(title:aaa OR content:aaa) OR ((title:bbb OR content:bbb) OR (title:ccc OR content:ccc)) OR (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("aaa OR (bbb OR ccc) OR ddd"));

        assertEquals(
                "(title:aaa OR content:aaa) AND (label:bbb AND (title:ccc OR content:ccc))",
                queryHelperImpl.buildFacetQuery("aaa (label:bbb ccc)"));
        assertEquals(
                "(title:aaa OR content:aaa) AND (label:bbb AND (title:ccc OR content:ccc))",
                queryHelperImpl.buildFacetQuery("aaa (label:bbb AND ccc)"));
        assertEquals(
                "(title:aaa OR content:aaa) AND (label:bbb OR (title:ccc OR content:ccc))",
                queryHelperImpl.buildFacetQuery("aaa (label:bbb OR ccc)"));

        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND label:ccc)",
                queryHelperImpl.buildFacetQuery("aaa (bbb label:ccc)"));
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND label:ccc)",
                queryHelperImpl.buildFacetQuery("aaa (bbb AND label:ccc)"));
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) OR label:ccc)",
                queryHelperImpl.buildFacetQuery("aaa (bbb OR label:ccc)"));

        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND NOT (title:ccc OR content:ccc))",
                queryHelperImpl.buildFacetQuery("aaa (bbb NOT ccc)"));

    }

    public void test_more_inner_facetQuery() {
        assertEquals(
                "(title:aaa OR content:aaa) AND ((title:bbb OR content:bbb) AND ((title:ccc OR content:ccc) AND (title:ddd OR content:ddd)))",
                queryHelperImpl.buildFacetQuery("aaa (bbb (ccc ddd))"));
        assertEquals(
                "(((title:aaa OR content:aaa) AND (title:bbb OR content:bbb)) AND (title:ccc OR content:ccc)) AND (title:ddd OR content:ddd)",
                queryHelperImpl.buildFacetQuery("((aaa bbb) ccc) ddd"));
    }

    public void test_quote_query() {
        final String[] formats = { "%s", " %s ", "aaa AND %s", "aaa OR %s",
                "NOT %s", "%s bbb", "%s AND bbb", "%s OR bbb", "aaa AND (%s)",
                "(%s) AND bbb", "aaa AND (%s) AND bbb" };
        for (final String format : formats) {
            assertException(format, "\"", "errors.invalid_query_quoted");
            assertException(format, "a\"", "errors.invalid_query_quoted");
            assertException(format, "\"b", "errors.invalid_query_quoted");
            assertException(format, "a\"b", "errors.invalid_query_quoted");
            assertException(format, "content:\"", "errors.invalid_query_quoted");
            assertException(format, "content:\" ",
                    "errors.invalid_query_quoted");
            assertException(format, "content:a\"",
                    "errors.invalid_query_quoted");
            assertException(format, "content:\"b",
                    "errors.invalid_query_quoted");
            assertException(format, "content:a\"b",
                    "errors.invalid_query_quoted");
        }
    }

    public void test_curly_bracket_query() {
        final String[] formats = { "%s", " %s ", "aaa AND %s", "aaa OR %s",
                "NOT %s", "%s bbb", "%s AND bbb", "%s OR bbb", "aaa AND (%s)",
                "(%s) AND bbb", "aaa AND (%s) AND bbb" };
        for (final String format : formats) {
            assertException(format, "{", "errors.invalid_query_curly_bracket");
            assertException(format, "a{", "errors.invalid_query_curly_bracket");
            assertException(format, "{b", "errors.invalid_query_curly_bracket");
            assertException(format, "a{b", "errors.invalid_query_curly_bracket");
            assertException(format, "content:{",
                    "errors.invalid_query_curly_bracket");
            assertException(format, "content:{ ",
                    "errors.invalid_query_curly_bracket");
            assertException(format, "content:a{",
                    "errors.invalid_query_curly_bracket");
            assertException(format, "content:{b",
                    "errors.invalid_query_curly_bracket");
            assertException(format, "content:a{b",
                    "errors.invalid_query_curly_bracket");
        }
    }

    public void test_square_bracket_query() {
        final String[] formats = { "%s", " %s ", "aaa AND %s", "aaa OR %s",
                "NOT %s", "%s bbb", "%s AND bbb", "%s OR bbb", "aaa AND (%s)",
                "(%s) AND bbb", "aaa AND (%s) AND bbb" };
        for (final String format : formats) {
            assertException(format, "[", "errors.invalid_query_square_bracket");
            assertException(format, "a[", "errors.invalid_query_square_bracket");
            assertException(format, "[b", "errors.invalid_query_square_bracket");
            assertException(format, "a[b",
                    "errors.invalid_query_square_bracket");
            assertException(format, "content:[",
                    "errors.invalid_query_square_bracket");
            assertException(format, "content:[ ",
                    "errors.invalid_query_square_bracket");
            assertException(format, "content:a[",
                    "errors.invalid_query_square_bracket");
            assertException(format, "content:[b",
                    "errors.invalid_query_square_bracket");
            assertException(format, "content:a[b",
                    "errors.invalid_query_square_bracket");
        }
    }

    public void test_parenthesis_query() {
        final String[] formats = { "%s", " %s ", "aaa AND %s", "aaa OR %s",
                "NOT %s", "%s bbb", "%s AND bbb", "%s OR bbb", "aaa AND (%s)",
                "(%s) AND bbb", "aaa AND (%s) AND bbb" };
        for (final String format : formats) {
            assertException(format, "(", "errors.invalid_query_parenthesis");
            assertException(format, "a(", "errors.invalid_query_parenthesis");
            assertException(format, "(b", "errors.invalid_query_parenthesis");
            assertException(format, "a(b", "errors.invalid_query_parenthesis");
            assertException(format, "content:(",
                    "errors.invalid_query_parenthesis");
            assertException(format, "content:( ",
                    "errors.invalid_query_parenthesis");
            assertException(format, "content:a(",
                    "errors.invalid_query_parenthesis");
            assertException(format, "content:(b",
                    "errors.invalid_query_parenthesis");
            assertException(format, "content:a(b",
                    "errors.invalid_query_parenthesis");
        }
    }

    private void assertException(final String format, final String query,
            final String messageCode) {
        try {
            final String ret = queryHelperImpl.buildQuery(
                    String.format(format, query)).getQuery();
            fail("format: " + format + ", query: " + query + ", ret: " + ret);
        } catch (final InvalidQueryException e) {
            if (!messageCode.equals(e.getMessageCode())) {
                throw e;
            }
        }
    }

    public void test_quote_facetQuery() {
        final String[] formats = { "%s", " %s ", "aaa AND %s", "aaa OR %s",
                "NOT %s", "%s bbb", "%s AND bbb", "%s OR bbb", "aaa AND (%s)",
                "(%s) AND bbb", "aaa AND (%s) AND bbb" };
        for (final String format : formats) {
            assertExceptionOnFacetQuery(format, "\"",
                    "errors.invalid_query_quoted");
            assertExceptionOnFacetQuery(format, "a\"",
                    "errors.invalid_query_quoted");
            assertExceptionOnFacetQuery(format, "\"b",
                    "errors.invalid_query_quoted");
            assertExceptionOnFacetQuery(format, "a\"b",
                    "errors.invalid_query_quoted");
            assertExceptionOnFacetQuery(format, "content:\"",
                    "errors.invalid_query_quoted");
            assertExceptionOnFacetQuery(format, "content:\" ",
                    "errors.invalid_query_quoted");
            assertExceptionOnFacetQuery(format, "content:a\"",
                    "errors.invalid_query_quoted");
            assertExceptionOnFacetQuery(format, "content:\"b",
                    "errors.invalid_query_quoted");
            assertExceptionOnFacetQuery(format, "content:a\"b",
                    "errors.invalid_query_quoted");
        }
    }

    public void test_curly_bracket_facetQuery() {
        final String[] formats = { "%s", " %s ", "aaa AND %s", "aaa OR %s",
                "NOT %s", "%s bbb", "%s AND bbb", "%s OR bbb", "aaa AND (%s)",
                "(%s) AND bbb", "aaa AND (%s) AND bbb" };
        for (final String format : formats) {
            assertExceptionOnFacetQuery(format, "{",
                    "errors.invalid_query_curly_bracket");
            assertExceptionOnFacetQuery(format, "a{",
                    "errors.invalid_query_curly_bracket");
            assertExceptionOnFacetQuery(format, "{b",
                    "errors.invalid_query_curly_bracket");
            assertExceptionOnFacetQuery(format, "a{b",
                    "errors.invalid_query_curly_bracket");
            assertExceptionOnFacetQuery(format, "content:{",
                    "errors.invalid_query_curly_bracket");
            assertExceptionOnFacetQuery(format, "content:{ ",
                    "errors.invalid_query_curly_bracket");
            assertExceptionOnFacetQuery(format, "content:a{",
                    "errors.invalid_query_curly_bracket");
            assertExceptionOnFacetQuery(format, "content:{b",
                    "errors.invalid_query_curly_bracket");
            assertExceptionOnFacetQuery(format, "content:a{b",
                    "errors.invalid_query_curly_bracket");
        }
    }

    public void test_square_bracket_facetQuery() {
        final String[] formats = { "%s", " %s ", "aaa AND %s", "aaa OR %s",
                "NOT %s", "%s bbb", "%s AND bbb", "%s OR bbb", "aaa AND (%s)",
                "(%s) AND bbb", "aaa AND (%s) AND bbb" };
        for (final String format : formats) {
            assertExceptionOnFacetQuery(format, "[",
                    "errors.invalid_query_square_bracket");
            assertExceptionOnFacetQuery(format, "a[",
                    "errors.invalid_query_square_bracket");
            assertExceptionOnFacetQuery(format, "[b",
                    "errors.invalid_query_square_bracket");
            assertExceptionOnFacetQuery(format, "a[b",
                    "errors.invalid_query_square_bracket");
            assertExceptionOnFacetQuery(format, "content:[",
                    "errors.invalid_query_square_bracket");
            assertExceptionOnFacetQuery(format, "content:[ ",
                    "errors.invalid_query_square_bracket");
            assertExceptionOnFacetQuery(format, "content:a[",
                    "errors.invalid_query_square_bracket");
            assertExceptionOnFacetQuery(format, "content:[b",
                    "errors.invalid_query_square_bracket");
            assertExceptionOnFacetQuery(format, "content:a[b",
                    "errors.invalid_query_square_bracket");
        }
    }

    public void test_parenthesis_facetQuery() {
        final String[] formats = { "%s", " %s ", "aaa AND %s", "aaa OR %s",
                "NOT %s", "%s bbb", "%s AND bbb", "%s OR bbb", "aaa AND (%s)",
                "(%s) AND bbb", "aaa AND (%s) AND bbb" };
        for (final String format : formats) {
            assertExceptionOnFacetQuery(format, "(",
                    "errors.invalid_query_parenthesis");
            assertExceptionOnFacetQuery(format, "a(",
                    "errors.invalid_query_parenthesis");
            assertExceptionOnFacetQuery(format, "(b",
                    "errors.invalid_query_parenthesis");
            assertExceptionOnFacetQuery(format, "a(b",
                    "errors.invalid_query_parenthesis");
            assertExceptionOnFacetQuery(format, "content:(",
                    "errors.invalid_query_parenthesis");
            assertExceptionOnFacetQuery(format, "content:( ",
                    "errors.invalid_query_parenthesis");
            assertExceptionOnFacetQuery(format, "content:a(",
                    "errors.invalid_query_parenthesis");
            assertExceptionOnFacetQuery(format, "content:(b",
                    "errors.invalid_query_parenthesis");
            assertExceptionOnFacetQuery(format, "content:a(b",
                    "errors.invalid_query_parenthesis");
        }
    }

    public void test_buildContentQueryWithLang() {
        StringBuilder buf;

        buf = new StringBuilder();
        queryHelperImpl.buildContentQueryWithLang(buf, "aaa", null);
        assertEquals("(title:aaa OR content:aaa)", buf.toString());

        buf = new StringBuilder();
        queryHelperImpl.buildContentQueryWithLang(buf, "aaa", "ja");
        assertEquals("(title:aaa OR content:aaa OR content_ja:aaa)",
                buf.toString());

        buf = new StringBuilder();
        queryHelperImpl.buildContentQueryWithLang(buf, "aaa", "zh_CN");
        assertEquals("(title:aaa OR content:aaa OR content_zh_CN:aaa)",
                buf.toString());
    }

    public void test_getQueryLanguage() {
        assertNull(queryHelperImpl.getQueryLanguage());
        final MockHttpServletRequest request = (MockHttpServletRequest) RequestUtil
                .getRequest();
        request.setLocale(Locale.JAPAN);
        assertEquals("ja", queryHelperImpl.getQueryLanguage());
        request.setLocale(Locale.SIMPLIFIED_CHINESE);
        assertEquals("zh_CN", queryHelperImpl.getQueryLanguage());
        queryHelperImpl.addFieldLanguage("zh_CN", "cjk");
        assertEquals("cjk", queryHelperImpl.getQueryLanguage());
        request.setLocale(Locale.CHINESE);
        assertEquals("zh", queryHelperImpl.getQueryLanguage());
        request.setLocale(Locale.CANADA_FRENCH);
        assertEquals("fr", queryHelperImpl.getQueryLanguage());
        request.setLocale(Locale.ENGLISH);
        assertNull(queryHelperImpl.getQueryLanguage());
    }

    public void test_buildWithLang() {
        final MockHttpServletRequest request = (MockHttpServletRequest) RequestUtil
                .getRequest();
        request.setLocale(Locale.JAPAN);

        assertEquals("", queryHelperImpl.buildQuery("").getQuery());

        assertEquals("title:QUERY OR content:QUERY OR content_ja:QUERY",
                queryHelperImpl.buildQuery("QUERY").getQuery());
        assertEquals("title:QUERY OR content:QUERY OR content_ja:QUERY",
                queryHelperImpl.buildQuery("QUERY ").getQuery());
        assertEquals("title:QUERY OR content:QUERY OR content_ja:QUERY",
                queryHelperImpl.buildQuery(" QUERY").getQuery());

        assertEquals(
                "(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1) AND NOT (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1 NOT QUERY2").getQuery());

        assertEquals(
                "(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1) AND (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1 QUERY2").getQuery());
        assertEquals(
                "(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1) AND (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
                queryHelperImpl.buildQuery("QUERY1 QUERY2 ").getQuery());
        assertEquals(
                "(title:QUERY1 OR content:QUERY1 OR content_ja:QUERY1) AND (title:QUERY2 OR content:QUERY2 OR content_ja:QUERY2)",
                queryHelperImpl.buildQuery(" QUERY1 QUERY2").getQuery());

    }

    public void test_unbracketQuery() {
        assertEquals("", queryHelperImpl.unbracketQuery(""));
        assertEquals("", queryHelperImpl.unbracketQuery("()"));
        assertEquals("", queryHelperImpl.unbracketQuery("(())"));
        assertEquals("()()", queryHelperImpl.unbracketQuery("()()"));
        assertEquals("()()", queryHelperImpl.unbracketQuery("(()())"));
        assertEquals("()()()", queryHelperImpl.unbracketQuery("()()()"));
        assertEquals("()(()())", queryHelperImpl.unbracketQuery("()(()())"));

        assertEquals("(", queryHelperImpl.unbracketQuery("("));
        assertEquals(")", queryHelperImpl.unbracketQuery(")"));
        assertEquals("()(", queryHelperImpl.unbracketQuery("()("));
        assertEquals("())", queryHelperImpl.unbracketQuery("())"));
        assertEquals(")()", queryHelperImpl.unbracketQuery(")()"));
        assertEquals("(()", queryHelperImpl.unbracketQuery("(()"));

        assertEquals("\\(", queryHelperImpl.unbracketQuery("(\\()"));
        assertEquals("\\)", queryHelperImpl.unbracketQuery("(\\))"));
        assertEquals("(\\\\()", queryHelperImpl.unbracketQuery("(\\\\()"));
        assertEquals("(\\\\))", queryHelperImpl.unbracketQuery("(\\\\))"));
        assertEquals("\\\\\\(", queryHelperImpl.unbracketQuery("(\\\\\\()"));
        assertEquals("\\\\\\)", queryHelperImpl.unbracketQuery("(\\\\\\))"));
    }

    public void test_appendRangeQueryValue() {
        StringBuilder buf = new StringBuilder();
        queryHelperImpl.appendRangeQueryValue(buf, "[1 TO 2]", '[', ']');
        assertEquals("[1 TO 2]", buf.toString());

        buf = new StringBuilder();
        queryHelperImpl.appendRangeQueryValue(buf, "[1234 TO 2345]", '[', ']');
        assertEquals("[1234 TO 2345]", buf.toString());

        buf = new StringBuilder();
        queryHelperImpl.appendRangeQueryValue(buf, "[* TO 2345]", '[', ']');
        assertEquals("[* TO 2345]", buf.toString());

        buf = new StringBuilder();
        queryHelperImpl.appendRangeQueryValue(buf, "[1234 TO *]", '[', ']');
        assertEquals("[1234 TO *]", buf.toString());

        buf = new StringBuilder();

        try {
            queryHelperImpl.appendRangeQueryValue(buf, "[* TO *]", '[', ']');
            fail();
        } catch (final InvalidQueryException e) {
        }

        try {
            queryHelperImpl.appendRangeQueryValue(buf, "[1]", '[', ']');
            fail();
        } catch (final InvalidQueryException e) {
        }

        try {
            queryHelperImpl.appendRangeQueryValue(buf, "[1 TO]", '[', ']');
            fail();
        } catch (final InvalidQueryException e) {
        }

        try {
            queryHelperImpl.appendRangeQueryValue(buf, "[1 TO ]", '[', ']');
            fail();
        } catch (final InvalidQueryException e) {
        }

        try {
            queryHelperImpl.appendRangeQueryValue(buf, "[TO 1]", '[', ']');
            fail();
        } catch (final InvalidQueryException e) {
        }

        try {
            queryHelperImpl.appendRangeQueryValue(buf, "[ TO 1]", '[', ']');
            fail();
        } catch (final InvalidQueryException e) {
        }
    }

    public void test_inurl() {
        assertEquals("url:*QUERY*", queryHelperImpl.buildQuery("inurl:QUERY")
                .getQuery());
        assertEquals("url:*QUERY1* AND url:*QUERY2*", queryHelperImpl
                .buildQuery("inurl:QUERY1 inurl:QUERY2").getQuery());
        assertEquals(
                "(title:aaa OR content:aaa) AND url:*QUERY1* AND url:*QUERY2*",
                queryHelperImpl.buildQuery("aaa inurl:QUERY1 inurl:QUERY2")
                        .getQuery());
        assertEquals("url:*QUERY*",
                queryHelperImpl.buildQuery("inurl:\"QUERY\"").getQuery());
    }

    public void test_buildOptionQuery() {
        final SearchParamMap options = new SearchParamMap();

        assertEquals("", queryHelperImpl.buildOptionQuery(null));
        assertEquals("", queryHelperImpl.buildOptionQuery(options));

        // Q

        options.clear();
        options.put(Constants.OPTION_QUERY_Q, new String[] { "a" });
        assertEquals("a", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_Q, new String[] { "a b" });
        assertEquals("a b", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_Q, new String[] { "a b c" });
        assertEquals("a b c", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_Q, new String[] { "\"a b\"" });
        assertEquals("\"a b\"", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_Q, new String[] { "\"a b\" c" });
        assertEquals("\"a b\" c", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_Q,
                new String[] { "\"a b\" \"c d\"" });
        assertEquals("\"a b\" \"c d\"",
                queryHelperImpl.buildOptionQuery(options));

        // CQ

        options.clear();
        options.put(Constants.OPTION_QUERY_CQ, new String[] { "a" });
        assertEquals("\"a\"", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_CQ, new String[] { "a b" });
        assertEquals("\"a b\"", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_CQ, new String[] { "a b c" });
        assertEquals("\"a b c\"", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_CQ, new String[] { "\"a b\"" });
        assertEquals("\"a b\"", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_CQ, new String[] { "\"a b\" c" });
        assertEquals("\"a b\" \"c\"", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_CQ,
                new String[] { "\"a b\" \"c d\"" });
        assertEquals("\"a b\" \"c d\"",
                queryHelperImpl.buildOptionQuery(options));

        // OQ

        options.clear();
        options.put(Constants.OPTION_QUERY_OQ, new String[] { "a" });
        assertEquals("a", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_OQ, new String[] { "a b" });
        assertEquals("a OR b", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_OQ, new String[] { "a b c" });
        assertEquals("a OR b OR c", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_OQ, new String[] { "\"a b\"" });
        assertEquals("\"a b\"", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_OQ, new String[] { "\"a b\" c" });
        assertEquals("\"a b\" OR c", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_OQ,
                new String[] { "\"a b\" \"c d\"" });
        assertEquals("\"a b\" OR \"c d\"",
                queryHelperImpl.buildOptionQuery(options));

        // NQ

        options.clear();
        options.put(Constants.OPTION_QUERY_NQ, new String[] { "a" });
        assertEquals("NOT a", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_NQ, new String[] { "a b" });
        assertEquals("NOT a NOT b", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_NQ, new String[] { "a b c" });
        assertEquals("NOT a NOT b NOT c",
                queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_NQ, new String[] { "\"a b\"" });
        assertEquals("NOT \"a b\"", queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_NQ, new String[] { "\"a b\" c" });
        assertEquals("NOT \"a b\" NOT c",
                queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_NQ,
                new String[] { "\"a b\" \"c d\"" });
        assertEquals("NOT \"a b\" NOT \"c d\"",
                queryHelperImpl.buildOptionQuery(options));

        // combine

        options.clear();
        options.put(Constants.OPTION_QUERY_Q, new String[] { "a" });
        options.put(Constants.OPTION_QUERY_CQ, new String[] { "b" });
        options.put(Constants.OPTION_QUERY_OQ, new String[] { "c" });
        options.put(Constants.OPTION_QUERY_NQ, new String[] { "d" });
        assertEquals("a \"b\" c NOT d",
                queryHelperImpl.buildOptionQuery(options));

        options.clear();
        options.put(Constants.OPTION_QUERY_Q, new String[] { "a 1" });
        options.put(Constants.OPTION_QUERY_CQ, new String[] { "b 2" });
        options.put(Constants.OPTION_QUERY_OQ, new String[] { "c 3" });
        options.put(Constants.OPTION_QUERY_NQ, new String[] { "d 4" });
        assertEquals("a 1 \"b 2\" (c OR 3) NOT d NOT 4",
                queryHelperImpl.buildOptionQuery(options));
    }

    private void assertExceptionOnFacetQuery(final String format,
            final String query, final String messageCode) {
        try {
            final String ret = queryHelperImpl.buildFacetQuery(String.format(
                    format, query));
            fail("format: " + format + ", query: " + query + ", ret: " + ret);
        } catch (final InvalidQueryException e) {
            if (!messageCode.equals(e.getMessageCode())) {
                throw e;
            }
        }
    }

    public void test_getQueryParamMap() {
        assertEquals(0, queryHelperImpl.getQueryParamMap().size());

        Map<String, String[]> queryParamMap;

        queryHelperImpl.addQueryParam("aaa", new String[] { "111" });
        queryHelperImpl.addQueryParam("bbb", new String[] { "222", "$333" });
        queryParamMap = queryHelperImpl.getQueryParamMap();
        assertEquals(2, queryParamMap.size());
        assertEquals(1, queryParamMap.get("aaa").length);
        assertEquals("111", queryParamMap.get("aaa")[0]);
        assertEquals(2, queryParamMap.get("bbb").length);
        assertEquals("222", queryParamMap.get("bbb")[0]);
        assertEquals("", queryParamMap.get("bbb")[1]);

        getRequest().setParameter("333", "AAA");
        queryParamMap = queryHelperImpl.getQueryParamMap();
        assertEquals(2, queryParamMap.size());
        assertEquals(1, queryParamMap.get("aaa").length);
        assertEquals("111", queryParamMap.get("aaa")[0]);
        assertEquals(2, queryParamMap.get("bbb").length);
        assertEquals("222", queryParamMap.get("bbb")[0]);
        assertEquals("AAA", queryParamMap.get("bbb")[1]);

    }
}
