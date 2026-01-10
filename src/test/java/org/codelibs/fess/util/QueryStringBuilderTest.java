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
package org.codelibs.fess.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.unit.UnitFessTestCase;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;

public class QueryStringBuilderTest extends UnitFessTestCase {

    @Test
    public void test_query() {
        assertEquals("", getQuery("", new String[0], Collections.emptyMap(), Collections.emptyMap(), false));
    }

    @Test
    public void test_conditions_q() {
        final String k = "q";
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "" })));
        assertEquals("aaa", getAsQuery(Collections.singletonMap(k, new String[] { "aaa" })));
        assertEquals("aaa bbb", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb" })));
        assertEquals("aaa bbb ccc", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb", "ccc" })));
        assertEquals("aaa bbb", getAsQuery("111", Collections.singletonMap(k, new String[] { "aaa bbb" })));
    }

    @Test
    public void test_conditions_epq() {
        final String k = "epq";
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "" })));
        assertEquals("\"aaa\"", getAsQuery(Collections.singletonMap(k, new String[] { "aaa" })));
        assertEquals("\"aaa bbb\"", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb" })));
        assertEquals("\"aaa bbb\" \"ccc\"", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb", "ccc" })));
        assertEquals("\"a\\\"aa\"", getAsQuery(Collections.singletonMap(k, new String[] { "a\"aa" })));
        assertEquals("\"aaa bbb\"", getAsQuery("111", Collections.singletonMap(k, new String[] { "aaa bbb" })));
    }

    @Test
    public void test_conditions_oq() {
        final String k = "oq";
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "" })));
        assertEquals("aaa", getAsQuery(Collections.singletonMap(k, new String[] { "aaa" })));
        assertEquals("(aaa OR bbb)", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb" })));
        assertEquals("(aaa OR bbb) ccc", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb", "ccc" })));
        assertEquals("(aaa OR bbb) (ccc OR ddd)", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb", "ccc ddd" })));
        assertEquals("(aaa OR bbb)", getAsQuery("111", Collections.singletonMap(k, new String[] { "aaa bbb" })));
    }

    @Test
    public void test_conditions_eq() {
        final String k = "nq";
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "" })));
        assertEquals("NOT aaa", getAsQuery(Collections.singletonMap(k, new String[] { "aaa" })));
        assertEquals("NOT aaa NOT bbb", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb" })));
        assertEquals("NOT aaa NOT bbb NOT ccc", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb", "ccc" })));
        assertEquals("NOT aaa NOT bbb", getAsQuery("111", Collections.singletonMap(k, new String[] { "aaa bbb" })));
    }

    @Test
    public void test_escape() {
        assertEquals("\\/", getQuery("/", new String[0], Collections.emptyMap(), Collections.emptyMap(), true));
        assertEquals("aaa\\/bbb", getQuery("aaa/bbb", new String[0], Collections.emptyMap(), Collections.emptyMap(), true));
        assertEquals("\\\\", getQuery("\\", new String[0], Collections.emptyMap(), Collections.emptyMap(), true));
        assertEquals("\\\\\\\\", getQuery("\\\\", new String[0], Collections.emptyMap(), Collections.emptyMap(), true));
        assertEquals("aaa \\&\\& bbb", getQuery("aaa && bbb", new String[0], Collections.emptyMap(), Collections.emptyMap(), true));
        assertEquals("\\\\\\+\\-\\&\\&\\|\\|\\!\\(\\)\\{\\}\\[\\]\\^\\~\\*\\?\\;\\:\\/",
                getQuery("\\+-&&||!(){}[]^~*?;:/", new String[0], Collections.emptyMap(), Collections.emptyMap(), true));
    }

    private String getAsQuery(final String query, final Map<String, String[]> conditions) {
        return getQuery(query, new String[0], Collections.emptyMap(), conditions, false);
    }

    private String getAsQuery(final Map<String, String[]> conditions) {
        return getQuery("", new String[0], Collections.emptyMap(), conditions, false);
    }

    private String getQuery(final String query, final String[] extraQueries, final Map<String, String[]> fields,
            final Map<String, String[]> conditions, final boolean escape) {
        return new QueryStringBuilder().params(new SearchRequestParams() {

            @Override
            public String getQuery() {
                return query;
            }

            @Override
            public Map<String, String[]> getFields() {
                return fields;
            }

            @Override
            public Map<String, String[]> getConditions() {
                return conditions;
            }

            @Override
            public String[] getLanguages() {
                return null;
            }

            @Override
            public GeoInfo getGeoInfo() {
                return null;
            }

            @Override
            public FacetInfo getFacetInfo() {
                return null;
            }

            @Override
            public String getSort() {
                return null;
            }

            @Override
            public int getStartPosition() {
                return 0;
            }

            @Override
            public int getOffset() {
                return 0;
            }

            @Override
            public int getPageSize() {
                return 0;
            }

            @Override
            public String[] getExtraQueries() {
                return extraQueries;
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public SearchRequestType getType() {
                return null;
            }

            @Override
            public String getSimilarDocHash() {
                return null;
            }

            @Override
            public HighlightInfo getHighlightInfo() {
                return new HighlightInfo();
            }

            @Override
            public boolean hasConditionQuery() {
                return conditions != null && !conditions.isEmpty();
            }

        }).escape(escape).build();
    }

    // Additional test methods for improved coverage

    @Test
    public void test_conditions_filetype() {
        final String k = "filetype";
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "" })));
        assertEquals("filetype:\"pdf\"", getAsQuery(Collections.singletonMap(k, new String[] { "pdf" })));
        assertEquals("filetype:\"doc\"", getAsQuery(Collections.singletonMap(k, new String[] { " doc " })));
        assertEquals("filetype:\"pdf\" filetype:\"doc\"", getAsQuery(Collections.singletonMap(k, new String[] { "pdf", "doc" })));
    }

    @Test
    public void test_conditions_sitesearch() {
        final String k = "sitesearch";
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "" })));
        assertEquals("site:example.com", getAsQuery(Collections.singletonMap(k, new String[] { "example.com" })));
        assertEquals("site:test.org", getAsQuery(Collections.singletonMap(k, new String[] { " test.org " })));
        assertEquals("site:example.com site:test.org", getAsQuery(Collections.singletonMap(k, new String[] { "example.com", "test.org" })));
    }

    @Test
    public void test_conditions_timestamp() {
        final String k = "timestamp";
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "" })));
        assertEquals("timestamp:2023-01-01", getAsQuery(Collections.singletonMap(k, new String[] { "2023-01-01" })));
        assertEquals("timestamp:2023-12-31", getAsQuery(Collections.singletonMap(k, new String[] { " 2023-12-31 " })));
        assertEquals("timestamp:2023-01-01 timestamp:2023-12-31",
                getAsQuery(Collections.singletonMap(k, new String[] { "2023-01-01", "2023-12-31" })));
    }

    @Test
    public void test_conditions_occurrence() {
        final String k = "occt";
        assertEquals("allintitle:", getAsQuery(Collections.singletonMap(k, new String[] { "allintitle" })));
        assertEquals("allinurl:", getAsQuery(Collections.singletonMap(k, new String[] { "allinurl" })));
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "invalid" })));
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "" })));
    }

    @Test
    public void test_extraQueries() {
        assertEquals("test", getQuery("", new String[] { "test" }, Collections.emptyMap(), Collections.emptyMap(), false));
        assertEquals("test (query1 OR query2)",
                getQuery("", new String[] { "test", "query1 OR query2" }, Collections.emptyMap(), Collections.emptyMap(), false));
        assertEquals("(test || extra)",
                getQuery("", new String[] { "test || extra" }, Collections.emptyMap(), Collections.emptyMap(), false));
        assertEquals("query1 query2",
                getQuery("", new String[] { "query1", "query2" }, Collections.emptyMap(), Collections.emptyMap(), false));
    }

    @Test
    public void test_fields() {
        final Map<String, String[]> fields = Collections.singletonMap("title", new String[] { "test" });
        assertEquals("title:\"test\"", getQuery("", new String[0], fields, Collections.emptyMap(), false));

        final Map<String, String[]> multiFields = Collections.singletonMap("content", new String[] { "value1", "value2" });
        assertEquals("(content:\"value1\" OR content:\"value2\")", getQuery("", new String[0], multiFields, Collections.emptyMap(), false));

        final Map<String, String[]> nullFields = Collections.singletonMap("empty", null);
        assertEquals("", getQuery("", new String[0], nullFields, Collections.emptyMap(), false));
    }

    @Test
    public void test_sortField() {
        final String query = new QueryStringBuilder().params(createSimpleParams("test")).sortField("score").build();
        assertEquals("test sort:score", query);

        final String queryWithoutSort = new QueryStringBuilder().params(createSimpleParams("test")).build();
        assertEquals("test", queryWithoutSort);
    }

    @Test
    public void test_quote() {
        // Test quote functionality through the builder behavior
        assertEquals("test", getQuery("test", new String[0], Collections.emptyMap(), Collections.emptyMap(), false));
        assertEquals("hello world", getQuery("hello world", new String[0], Collections.emptyMap(), Collections.emptyMap(), false));
    }

    @Test
    public void test_escapeQuery() {
        // Test escaping functionality through escape parameter
        assertEquals("test", getQuery("test", new String[0], Collections.emptyMap(), Collections.emptyMap(), false));
        assertEquals("test", getQuery("test", new String[0], Collections.emptyMap(), Collections.emptyMap(), true));

        // Test specific characters that should be escaped
        assertEquals("test\\/query", getQuery("test/query", new String[0], Collections.emptyMap(), Collections.emptyMap(), true));
        assertEquals("\\\\", getQuery("\\", new String[0], Collections.emptyMap(), Collections.emptyMap(), true));
    }

    @Test
    public void test_isOccurrence() {
        // Test through occurrence conditions
        final String k = "occt";
        assertEquals("allintitle:", getAsQuery(Collections.singletonMap(k, new String[] { "allintitle" })));
        assertEquals("allinurl:", getAsQuery(Collections.singletonMap(k, new String[] { "allinurl" })));
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "invalid" })));
    }

    @Test
    public void test_escape_method() {
        // Test escape functionality through quote escaping in epq conditions
        final String k = "epq";
        assertEquals("\"a\\\"aa\"", getAsQuery(Collections.singletonMap(k, new String[] { "a\"aa" })));
    }

    @Test
    public void test_complexConditions() {
        final Map<String, String[]> conditions = new HashMap<>();
        conditions.put("q", new String[] { "search term" });
        conditions.put("epq", new String[] { "exact phrase" });
        conditions.put("oq", new String[] { "word1 word2" });
        conditions.put("nq", new String[] { "exclude" });
        conditions.put("filetype", new String[] { "pdf" });
        conditions.put("sitesearch", new String[] { "example.com" });

        final String result = getAsQuery(conditions);
        assertTrue(result.contains("search term"));
        assertTrue(result.contains("\"exact phrase\""));
        assertTrue(result.contains("(word1 OR word2)"));
        assertTrue(result.contains("NOT exclude"));
        assertTrue(result.contains("filetype:\"pdf\""));
        assertTrue(result.contains("site:example.com"));
    }

    @Test
    public void test_builderChaining() {
        QueryStringBuilder builder = new QueryStringBuilder();
        assertSame(builder, builder.params(createSimpleParams("test")));
        assertSame(builder, builder.sortField("score"));
        assertSame(builder, builder.escape(true));
    }

    @Test
    public void test_emptyAndNullInputs() {
        assertEquals("", getQuery(null, new String[0], Collections.emptyMap(), Collections.emptyMap(), false));
        assertEquals("", getQuery("", new String[0], Collections.emptyMap(), Collections.emptyMap(), false));
        assertEquals("", getQuery("   ", new String[0], Collections.emptyMap(), Collections.emptyMap(), false));

        assertEquals("", getQuery("", new String[] { "", "   " }, Collections.emptyMap(), Collections.emptyMap(), false));
        assertEquals("test", getQuery("", new String[] { "test", "", "   " }, Collections.emptyMap(), Collections.emptyMap(), false));
    }

    private SearchRequestParams createSimpleParams(final String query) {
        return new SearchRequestParams() {
            @Override
            public String getQuery() {
                return query;
            }

            @Override
            public Map<String, String[]> getFields() {
                return Collections.emptyMap();
            }

            @Override
            public Map<String, String[]> getConditions() {
                return Collections.emptyMap();
            }

            @Override
            public String[] getLanguages() {
                return null;
            }

            @Override
            public GeoInfo getGeoInfo() {
                return null;
            }

            @Override
            public FacetInfo getFacetInfo() {
                return null;
            }

            @Override
            public String getSort() {
                return null;
            }

            @Override
            public int getStartPosition() {
                return 0;
            }

            @Override
            public int getOffset() {
                return 0;
            }

            @Override
            public int getPageSize() {
                return 0;
            }

            @Override
            public String[] getExtraQueries() {
                return new String[0];
            }

            @Override
            public Object getAttribute(String name) {
                return null;
            }

            @Override
            public Locale getLocale() {
                return null;
            }

            @Override
            public SearchRequestType getType() {
                return null;
            }

            @Override
            public String getSimilarDocHash() {
                return null;
            }

            @Override
            public HighlightInfo getHighlightInfo() {
                return new HighlightInfo();
            }

            @Override
            public boolean hasConditionQuery() {
                return false;
            }
        };
    }
}
