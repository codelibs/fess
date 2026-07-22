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
    public void test_buildChunkVectorQuery() {
        final String json = searcher.buildChunkVectorQuery(new float[] { 0.1f, 0.2f }).toString().replaceAll("\\s", "");
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
}
