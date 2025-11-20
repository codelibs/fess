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
package org.codelibs.fess.rank.fusion;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.dbflute.optional.OptionalThing;

/**
 * Tests for RankFusionSearcher abstract base class.
 */
public class RankFusionSearcherTest extends UnitFessTestCase {

    /**
     * Test getName() method derives name from class name.
     */
    public void test_getNameFromClassName() {
        final TestSearcher searcher = new TestSearcher();
        assertEquals("test", searcher.getName());
    }

    /**
     * Test getName() removes "Searcher" suffix.
     */
    public void test_getNameRemovesSearcherSuffix() {
        final CustomSearcher searcher = new CustomSearcher();
        assertEquals("custom", searcher.getName());
    }

    /**
     * Test getName() converts to lowercase.
     */
    public void test_getNameLowercase() {
        final MyCustomSearcher searcher = new MyCustomSearcher();
        // Should be "my_custom" (decamelized and lowercased)
        String name = searcher.getName();
        assertNotNull(name);
        assertTrue(name.equals("my_custom") || name.equals("mycustom"));
    }

    /**
     * Test getName() is cached (lazily initialized).
     */
    public void test_getNameCached() {
        final TestSearcher searcher = new TestSearcher();
        final String name1 = searcher.getName();
        final String name2 = searcher.getName();
        assertSame(name1, name2); // Should be same instance
    }

    /**
     * Test searcher with simple class name.
     */
    public void test_simpleClassNameSearcher() {
        final SimpleSearcher searcher = new SimpleSearcher();
        assertEquals("simple", searcher.getName());
    }

    /**
     * Test searcher extending another searcher.
     */
    public void test_extendedSearcher() {
        final ExtendedTestSearcher searcher = new ExtendedTestSearcher();
        // Name should be based on actual class, not parent
        assertEquals("extended_test", searcher.getName());
    }

    /**
     * Test that search method must be implemented.
     */
    public void test_searchMethodAbstract() {
        final TestSearcher searcher = new TestSearcher();
        // Should be able to call search
        final SearchResult result = searcher.search("test", new TestSearchRequestParams(), OptionalThing.empty());
        assertNotNull(result);
    }

    /**
     * Simple test searcher implementation.
     */
    static class TestSearcher extends RankFusionSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            return SearchResult.create().build();
        }
    }

    /**
     * Custom searcher implementation.
     */
    static class CustomSearcher extends RankFusionSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            return SearchResult.create().build();
        }
    }

    /**
     * Multi-word class name searcher.
     */
    static class MyCustomSearcher extends RankFusionSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            return SearchResult.create().build();
        }
    }

    /**
     * Simple searcher for testing.
     */
    static class SimpleSearcher extends RankFusionSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            return SearchResult.create().build();
        }
    }

    /**
     * Extended searcher for testing inheritance.
     */
    static class ExtendedTestSearcher extends TestSearcher {
        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            return SearchResult.create().allRecordCount(100).build();
        }
    }

    /**
     * Test search request parameters for testing.
     */
    static class TestSearchRequestParams extends SearchRequestParams {
        @Override
        public String getQuery() {
            return null;
        }

        @Override
        public Map<String, String[]> getFields() {
            return null;
        }

        @Override
        public Map<String, String[]> getConditions() {
            return null;
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
        public HighlightInfo getHighlightInfo() {
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
            return 10;
        }

        @Override
        public String[] getExtraQueries() {
            return null;
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
    }
}
