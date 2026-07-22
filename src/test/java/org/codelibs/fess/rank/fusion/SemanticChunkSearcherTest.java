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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SemanticChunkSearcherTest extends UnitFessTestCase {

    private SemanticChunkSearcher searcher;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        searcher = new SemanticChunkSearcher();
    }

    @Test
    public void test_isPlainQuery_plain() {
        assertTrue(searcher.isPlainQuery("fess"));
        assertTrue(searcher.isPlainQuery("systemd 自動起動"));
        assertTrue(searcher.isPlainQuery("how to install fess on linux"));
        assertTrue(searcher.isPlainQuery("Fessをサービスとして自動起動する方法"));
    }

    @Test
    public void test_isPlainQuery_syntax() {
        assertFalse(searcher.isPlainQuery("title:fess"));
        assertFalse(searcher.isPlainQuery("\"exact phrase\""));
        assertFalse(searcher.isPlainQuery("(fess OR opensearch)"));
        assertFalse(searcher.isPlainQuery("fess AND linux"));
        assertFalse(searcher.isPlainQuery("fess OR linux"));
        assertFalse(searcher.isPlainQuery("NOT fess"));
        assertFalse(searcher.isPlainQuery("+fess -windows"));
        assertFalse(searcher.isPlainQuery("fess -windows"));
        assertFalse(searcher.isPlainQuery("fes*"));
        assertFalse(searcher.isPlainQuery("fess~2"));
        assertFalse(searcher.isPlainQuery("timestamp:[now-1d TO now]"));
        assertFalse(searcher.isPlainQuery("content_length:{0 TO 100}"));
        assertFalse(searcher.isPlainQuery("a\\:b"));
        assertFalse(searcher.isPlainQuery("foo && bar"));
        assertFalse(searcher.isPlainQuery("foo || bar"));
    }

    @Test
    public void test_isPlainQuery_hyphenInsideWord() {
        // an in-word hyphen is not an exclusion operator
        assertTrue(searcher.isPlainQuery("fess-crawler"));
        assertTrue(searcher.isPlainQuery("real-time search"));
        assertTrue(searcher.isPlainQuery("10-20"));
        assertTrue(searcher.isPlainQuery("trailing -"));
    }

    @Test
    public void test_buildExactChunkQuery() {
        final String json = searcher.buildExactChunkQuery(new float[] { 0.1f, 0.2f }).toString().replaceAll("\\s", "");
        assertTrue(json.contains("\"nested\""));
        assertTrue(json.contains("\"path\":\"content_chunk_vector\""));
        assertTrue(json.contains("script_score"));
        assertTrue(json.contains("cosineSimilarity"));
        assertTrue(json.contains("content_chunk_vector.vector"));
        assertTrue(json.contains("\"score_mode\":\"max\""));
        // an unmapped content_chunk_vector (chunk job never ran) must yield no hits, not a query error
        assertTrue(json.contains("\"ignore_unmapped\":true"));
    }

    @Test
    public void test_emptyResult() {
        final SearchResult result = searcher.emptyResult();
        assertEquals(0, result.getAllRecordCount());
        assertTrue(result.getDocumentList().isEmpty());
    }

    @Test
    public void test_buildChunkVectorQuery_annShape() {
        final String json = searcher.buildKnnChunkQuery(new float[] { 0.1f, 0.2f }, new StubSearchRequestParams(0, 10))
                .toString()
                .replaceAll("\\s", "");
        assertTrue(json.contains("\"nested\""));
        assertTrue(json.contains("\"path\":\"content_chunk_vector\""));
        assertTrue(json.contains("\"knn\""));
        assertTrue(json.contains("\"content_chunk_vector.vector\""));
        assertTrue(json.contains("\"k\":100"));
        assertTrue(json.contains("\"ignore_unmapped\":true"));
        assertFalse(json.contains("script_score"), "ann mode must not use script_score: " + json);
    }

    @Test
    public void test_buildKnnChunkQuery_kGrowsWithWindow() {
        // a deep page must not request fewer neighbors than the window it has to fill
        final String json =
                searcher.buildKnnChunkQuery(new float[] { 0.1f }, new StubSearchRequestParams(190, 20)).toString().replaceAll("\\s", "");
        assertTrue(json.contains("\"k\":210"), "k must cover startPosition + pageSize: " + json);
    }

    @Test
    public void test_resolveEngineMinScore() {
        org.codelibs.fess.util.ComponentUtil.register(new org.codelibs.fess.helper.ChunkVectorHelper(),
                org.codelibs.fess.helper.ChunkVectorHelper.class.getCanonicalName());
        // exact mode: script emits cosine + 1.0
        assertTrue(Math.abs(searcher.resolveEngineMinScore(0.4f, false).get() - 1.4f) < 0.0001f);
        // ann mode with default lucene + cosinesimil: engine emits (1 + cosine) / 2
        assertTrue(Math.abs(searcher.resolveEngineMinScore(0.4f, true).get() - 0.7f) < 0.0001f);
    }

    private static class StubSearchRequestParams extends org.codelibs.fess.entity.SearchRequestParams {
        private final int start;
        private final int size;

        StubSearchRequestParams(final int start, final int size) {
            this.start = start;
            this.size = size;
        }

        @Override
        public String getQuery() {
            return null;
        }

        @Override
        public java.util.Map<String, String[]> getFields() {
            return java.util.Collections.emptyMap();
        }

        @Override
        public java.util.Map<String, String[]> getConditions() {
            return java.util.Collections.emptyMap();
        }

        @Override
        public String[] getLanguages() {
            return new String[0];
        }

        @Override
        public org.codelibs.fess.entity.GeoInfo getGeoInfo() {
            return null;
        }

        @Override
        public org.codelibs.fess.entity.FacetInfo getFacetInfo() {
            return null;
        }

        @Override
        public org.codelibs.fess.entity.HighlightInfo getHighlightInfo() {
            return null;
        }

        @Override
        public String getSort() {
            return null;
        }

        @Override
        public int getStartPosition() {
            return start;
        }

        @Override
        public int getPageSize() {
            return size;
        }

        @Override
        public int getOffset() {
            return 0;
        }

        @Override
        public String[] getExtraQueries() {
            return new String[0];
        }

        @Override
        public Object getAttribute(final String name) {
            return null;
        }

        @Override
        public java.util.Locale getLocale() {
            return java.util.Locale.ROOT;
        }

        @Override
        public SearchRequestType getType() {
            return SearchRequestType.SEARCH;
        }

        @Override
        public String getSimilarDocHash() {
            return null;
        }
    }
}
