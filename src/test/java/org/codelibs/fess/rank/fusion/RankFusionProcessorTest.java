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
package org.codelibs.fess.rank.fusion;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.lucene.search.TotalHits.Relation;
import org.codelibs.fess.entity.FacetInfo;
import org.codelibs.fess.entity.GeoInfo;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.rank.fusion.SearchResult.SearchResultBuilder;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.QueryResponseList;
import org.dbflute.optional.OptionalThing;

public class RankFusionProcessorTest extends UnitFessTestCase {

    private static final String ID_FIELD = "_id";

    public void test_default_1000docs_10size() throws Exception {
        String query = "*";
        int allRecordCount = 1000;
        int pageSize = 10;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSeacher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount, list.getAllRecordCount());
                assertEquals(100, list.getAllPageCount());
                assertEquals(10, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(0, list.getOffset());
                assertEquals(10, list.getPageSize());
                assertEquals(0, list.getStart());
                assertEquals("0", list.get(0).get(ID_FIELD));
                assertEquals("9", list.get(9).get(ID_FIELD));
            } else {
                fail();
            }

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(10, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount, list.getAllRecordCount());
                assertEquals(100, list.getAllPageCount());
                assertEquals(20, list.getCurrentEndRecordNumber());
                assertEquals(2, list.getCurrentPageNumber());
                assertEquals(11, list.getCurrentStartRecordNumber());
                assertEquals(0, list.getOffset());
                assertEquals(10, list.getPageSize());
                assertEquals(10, list.getStart());
                assertEquals("10", list.get(0).get(ID_FIELD));
                assertEquals("19", list.get(9).get(ID_FIELD));
            } else {
                fail();
            }

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(990, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount, list.getAllRecordCount());
                assertEquals(100, list.getAllPageCount());
                assertEquals(1000, list.getCurrentEndRecordNumber());
                assertEquals(100, list.getCurrentPageNumber());
                assertEquals(991, list.getCurrentStartRecordNumber());
                assertEquals(0, list.getOffset());
                assertEquals(10, list.getPageSize());
                assertEquals(990, list.getStart());
                assertEquals("990", list.get(0).get(ID_FIELD));
                assertEquals("999", list.get(9).get(ID_FIELD));
            } else {
                fail();
            }
        }
    }

    public void test_1searcher10_45_45_1000docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 1000;
        int pageSize = 100;
        int offset = 45;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSeacher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(10, 45, 45));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount + offset, list.getAllRecordCount());
                assertEquals(11, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
                assertEquals("0", list.get(0).get(ID_FIELD));
                assertEquals("9", list.get(1).get(ID_FIELD));
                assertEquals("1", list.get(2).get(ID_FIELD));
                assertEquals("8", list.get(3).get(ID_FIELD));
                assertEquals("2", list.get(4).get(ID_FIELD));
                assertEquals("7", list.get(5).get(ID_FIELD));
                assertEquals("3", list.get(6).get(ID_FIELD));
                assertEquals("6", list.get(7).get(ID_FIELD));
                assertEquals("4", list.get(8).get(ID_FIELD));
                assertEquals("5", list.get(9).get(ID_FIELD));
            } else {
                fail();
            }

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, offset),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount + offset, list.getAllRecordCount());
                assertEquals(11, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
                assertEquals("0", list.get(0).get(ID_FIELD));
                assertEquals("9", list.get(1).get(ID_FIELD));
                assertEquals("1", list.get(2).get(ID_FIELD));
                assertEquals("8", list.get(3).get(ID_FIELD));
                assertEquals("2", list.get(4).get(ID_FIELD));
                assertEquals("7", list.get(5).get(ID_FIELD));
                assertEquals("3", list.get(6).get(ID_FIELD));
                assertEquals("6", list.get(7).get(ID_FIELD));
                assertEquals("4", list.get(8).get(ID_FIELD));
                assertEquals("5", list.get(9).get(ID_FIELD));
            } else {
                fail();
            }

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(100, pageSize, offset),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount + offset, list.getAllRecordCount());
                assertEquals(11, list.getAllPageCount());
                assertEquals(200, list.getCurrentEndRecordNumber());
                assertEquals(2, list.getCurrentPageNumber());
                assertEquals(101, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(100, list.getStart());
                assertEquals("55", list.get(0).get(ID_FIELD));
                assertEquals("154", list.get(99).get(ID_FIELD));
            } else {
                fail();
            }

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(900, pageSize, offset),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(allRecordCount + offset, list.getAllRecordCount());
                assertEquals(11, list.getAllPageCount());
                assertEquals(1000, list.getCurrentEndRecordNumber());
                assertEquals(10, list.getCurrentPageNumber());
                assertEquals(901, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(900, list.getStart());
                assertEquals("855", list.get(0).get(ID_FIELD));
                assertEquals("954", list.get(99).get(ID_FIELD));
            } else {
                fail();
            }

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(1000, pageSize, offset),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(offset, list.size());
                assertEquals(allRecordCount + offset, list.getAllRecordCount());
                assertEquals(11, list.getAllPageCount());
                assertEquals(1045, list.getCurrentEndRecordNumber());
                assertEquals(11, list.getCurrentPageNumber());
                assertEquals(1001, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(1000, list.getStart());
                assertEquals("955", list.get(0).get(ID_FIELD));
                assertEquals("999", list.get(44).get(ID_FIELD));
            } else {
                fail();
            }
        }
    }

    public void test_1searcher0_0docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 0;
        int pageSize = 100;
        int offset = 0;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSeacher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 0));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(0, list.size());
                assertEquals(0, list.getAllRecordCount());
                assertEquals(1, list.getAllPageCount());
                assertEquals(0, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(0, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    public void test_1searcher0_10docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 10;
        int pageSize = 100;
        int offset = 0;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSeacher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 0));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(10, list.size());
                assertEquals(10, list.getAllRecordCount());
                assertEquals(1, list.getAllPageCount());
                assertEquals(10, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    public void test_1searcher0_1000docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 1000;
        int pageSize = 100;
        int offset = 0;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSeacher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 0));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(1000, list.getAllRecordCount());
                assertEquals(10, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    public void test_1searcher10_0docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 0;
        int pageSize = 100;
        int offset = 10;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSeacher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(10, 0, 0));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(10, list.size());
                assertEquals(10, list.getAllRecordCount());
                assertEquals(1, list.getAllPageCount());
                assertEquals(10, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    public void test_1searcher10_10docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 10;
        int pageSize = 100;
        int offset = 0;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSeacher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(10, 0, 0));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(10, list.size());
                assertEquals(10, list.getAllRecordCount());
                assertEquals(1, list.getAllPageCount());
                assertEquals(10, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    public void test_1searcher10_1000docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 1000;
        int pageSize = 100;
        int offset = 0;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSeacher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(10, 0, 0));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(1000, list.getAllRecordCount());
                assertEquals(10, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    public void test_1searcher1000_0docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 0;
        int pageSize = 100;
        int offset = 100;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSeacher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 1000));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(100, list.getAllRecordCount());
                assertEquals(1, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    public void test_1searcher1000_10docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 10;
        int pageSize = 100;
        int offset = 90;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSeacher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 1000));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(100, list.getAllRecordCount());
                assertEquals(1, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    public void test_1searcher1000_1000docs_100size() throws Exception {
        String query = "*";
        int allRecordCount = 1000;
        int pageSize = 100;
        int offset = 50;
        try (RankFusionProcessor rankFusionProcessor = new RankFusionProcessor()) {
            rankFusionProcessor.setSeacher(new TestMainSearcher(allRecordCount));
            rankFusionProcessor.register(new TestSubSearcher(0, 0, 1000));
            rankFusionProcessor.init();

            if (rankFusionProcessor.search(query, new TestSearchRequestParams(0, pageSize, 0),
                    OptionalThing.empty()) instanceof QueryResponseList list) {
                assertEquals(pageSize, list.size());
                assertEquals(1050, list.getAllRecordCount());
                assertEquals(11, list.getAllPageCount());
                assertEquals(100, list.getCurrentEndRecordNumber());
                assertEquals(1, list.getCurrentPageNumber());
                assertEquals(1, list.getCurrentStartRecordNumber());
                assertEquals(offset, list.getOffset());
                assertEquals(100, list.getPageSize());
                assertEquals(0, list.getStart());
            } else {
                fail();
            }
        }
    }

    static class TestMainSearcher extends RankFusionSearcher {

        private long allRecordCount;

        TestMainSearcher(int allRecordCount) {
            this.allRecordCount = allRecordCount;
        }

        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            int start = params.getStartPosition();
            int size = params.getPageSize();
            SearchResultBuilder builder = SearchResult.create();
            for (int i = start; i < start + size && i < allRecordCount; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, Integer.toString(i));
                doc.put("score", 1.0f / (i + 1));
                builder.addDocument(doc);
            }
            builder.allRecordCount(allRecordCount);
            if (allRecordCount < 10000) {
                builder.allRecordCountRelation(Relation.EQUAL_TO.toString());
            }
            return builder.build();
        }
    }

    static class TestSubSearcher extends RankFusionSearcher {

        private int mainSize;
        private int inSize;
        private int outSize;

        TestSubSearcher(int mainSize, int inSize, int outSize) {
            this.mainSize = mainSize;
            this.inSize = inSize;
            this.outSize = outSize;
        }

        @Override
        protected SearchResult search(String query, SearchRequestParams params, OptionalThing<FessUserBean> userBean) {
            SearchResultBuilder builder = SearchResult.create();
            for (int i = 0; i < mainSize; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, Integer.toString(mainSize - i - 1));
                doc.put("score", 1.0f / (i + 2));
                builder.addDocument(doc);
            }
            for (int i = 100; i < inSize + 100; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, Integer.toString(i));
                doc.put("score", 1.0f / (mainSize + i + 2));
                builder.addDocument(doc);
            }
            for (int i = 200; i < outSize + 200; i++) {
                Map<String, Object> doc = new HashMap<>();
                doc.put(ID_FIELD, Integer.toString(i));
                doc.put("score", 1.0f / (mainSize + i + 3));
                builder.addDocument(doc);
            }
            builder.allRecordCount(mainSize + inSize + outSize);
            return builder.build();
        }
    }

    static class TestSearchRequestParams extends SearchRequestParams {

        private int startPosition;

        private int pageSize;

        private int offset;

        TestSearchRequestParams(int startPosition, int pageSize, int offset) {
            this.startPosition = startPosition;
            this.pageSize = pageSize;
            this.offset = offset;
        }

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
            return startPosition;
        }

        @Override
        public int getOffset() {
            return offset;
        }

        @Override
        public int getPageSize() {
            return pageSize;
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
