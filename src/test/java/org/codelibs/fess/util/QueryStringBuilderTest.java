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
package org.codelibs.fess.util;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.unit.UnitFessTestCase;

public class QueryStringBuilderTest extends UnitFessTestCase {

    public void test_query() {
        assertEquals("", getQuery("", new String[0], Collections.emptyMap(), Collections.emptyMap(), false));
    }

    public void test_conditions_q() {
        final String k = "q";
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "" })));
        assertEquals("aaa", getAsQuery(Collections.singletonMap(k, new String[] { "aaa" })));
        assertEquals("aaa bbb", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb" })));
        assertEquals("aaa bbb ccc", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb", "ccc" })));
        assertEquals("aaa bbb", getAsQuery("111", Collections.singletonMap(k, new String[] { "aaa bbb" })));
    }

    public void test_conditions_epq() {
        final String k = "epq";
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "" })));
        assertEquals("\"aaa\"", getAsQuery(Collections.singletonMap(k, new String[] { "aaa" })));
        assertEquals("\"aaa bbb\"", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb" })));
        assertEquals("\"aaa bbb\" \"ccc\"", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb", "ccc" })));
        assertEquals("\"a\\\"aa\"", getAsQuery(Collections.singletonMap(k, new String[] { "a\"aa" })));
        assertEquals("\"aaa bbb\"", getAsQuery("111", Collections.singletonMap(k, new String[] { "aaa bbb" })));
    }

    public void test_conditions_oq() {
        final String k = "oq";
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "" })));
        assertEquals("aaa", getAsQuery(Collections.singletonMap(k, new String[] { "aaa" })));
        assertEquals("(aaa OR bbb)", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb" })));
        assertEquals("(aaa OR bbb) ccc", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb", "ccc" })));
        assertEquals("(aaa OR bbb) (ccc OR ddd)", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb", "ccc ddd" })));
        assertEquals("(aaa OR bbb)", getAsQuery("111", Collections.singletonMap(k, new String[] { "aaa bbb" })));
    }

    public void test_conditions_eq() {
        final String k = "nq";
        assertEquals("", getAsQuery(Collections.singletonMap(k, new String[] { "" })));
        assertEquals("NOT aaa", getAsQuery(Collections.singletonMap(k, new String[] { "aaa" })));
        assertEquals("NOT aaa NOT bbb", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb" })));
        assertEquals("NOT aaa NOT bbb NOT ccc", getAsQuery(Collections.singletonMap(k, new String[] { "aaa bbb", "ccc" })));
        assertEquals("NOT aaa NOT bbb", getAsQuery("111", Collections.singletonMap(k, new String[] { "aaa bbb" })));
    }

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

        }).escape(escape).build();
    }
}
