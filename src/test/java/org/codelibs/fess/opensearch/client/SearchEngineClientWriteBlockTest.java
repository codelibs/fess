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
package org.codelibs.fess.opensearch.client;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Reads the {@code blocks} of a {@code GET /_cluster/state/blocks} response the way the health endpoint needs it.
 */
public class SearchEngineClientWriteBlockTest extends UnitFessTestCase {

    private static final java.util.function.Predicate<String> ANY = name -> true;

    private static Map<String, Object> block(final String... levels) {
        return Map.of("description", "a block", "retryable", true, "levels", List.of(levels));
    }

    @Test
    public void test_floodStageBlockOnAnIndexIsAWriteBlock() {
        final Map<String, Object> state = Map.of("cluster_name", "c", "blocks",
                Map.of("indices", Map.of("fess_config.label_type", Map.of("12", block("write", "metadata_write")))));
        assertTrue(SearchEngineClient.hasWriteBlock(state, ANY));
    }

    @Test
    public void test_globalWriteBlockIsAWriteBlock() {
        final Map<String, Object> state = Map.of("blocks", Map.of("global", Map.of("2", block("read", "write", "metadata_write"))));
        assertTrue(SearchEngineClient.hasWriteBlock(state, ANY));
    }

    @Test
    public void test_noBlocksIsNotAWriteBlock() {
        assertFalse(SearchEngineClient.hasWriteBlock(Map.of("cluster_name", "c", "blocks", Map.of()), ANY));
        assertFalse(SearchEngineClient.hasWriteBlock(Map.of("cluster_name", "c"), ANY));
    }

    @Test
    public void test_blockWithoutWriteLevelIsNotAWriteBlock() {
        final Map<String, Object> state = Map.of("blocks", Map.of("indices", Map.of("fess.20260101", Map.of("9", block("metadata_read")))));
        assertFalse(SearchEngineClient.hasWriteBlock(state, ANY));
    }

    @Test
    public void test_closedIndexIsNotAWriteBlock() {
        final Map<String, Object> state =
                Map.of("blocks", Map.of("indices", Map.of("old_index", Map.of("4", block("read", "write", "metadata_write")))));
        assertFalse(SearchEngineClient.hasWriteBlock(state, ANY));
    }

    /**
     * Another application may share the cluster and keep an index read-only on purpose (a lifecycle policy):
     * it must not keep Fess from being healthy.
     */
    @Test
    public void test_writeBlockOnAnIndexOfAnotherApplicationIsNotCounted() {
        final Map<String, Object> state = Map.of("blocks", Map.of("indices", Map.of("logs-2026.01",
                Map.of("5", block("write", "metadata_write")), "fess_config.label_type", Map.of("9", block("read")))));
        final SearchEngineClient client = new SearchEngineClient();
        assertFalse(SearchEngineClient.hasWriteBlock(state, client::isFessIndex));
    }

    @Test
    public void test_writeBlockOnAnIndexOfFessIsCounted() {
        final SearchEngineClient client = new SearchEngineClient();
        for (final String index : List.of("fess.20260101", "fess_config.web_config", "fess_user.user", "fess_log.search_log",
                "fess_crawler.queue", "fess", "fess.suggest.20260101", "fess_suggest", "fess_suggest_analyzer", "fess_suggest_array.fess",
                "configsync")) {
            final Map<String, Object> state = Map.of("blocks", Map.of("indices", Map.of(index, Map.of("12", block("write")))));
            assertTrue(index, SearchEngineClient.hasWriteBlock(state, client::isFessIndex));
        }
    }

    @Test
    public void test_isFessIndex_doesNotTakeAnIndexThatOnlyStartsWithTheSameLetters() {
        final SearchEngineClient client = new SearchEngineClient();
        assertFalse(client.isFessIndex("fessionable"));
        assertFalse(client.isFessIndex("my_fess.20260101"));
        assertFalse(client.isFessIndex("logs-2026.01"));
    }

    @Test
    public void test_globalWriteBlockStillCounts() {
        final SearchEngineClient client = new SearchEngineClient();
        final Map<String, Object> state = Map.of("blocks", Map.of("global", Map.of("2", block("write"))));
        assertTrue(SearchEngineClient.hasWriteBlock(state, client::isFessIndex));
    }

    /**
     * The document index is created as fess.&lt;timestamp&gt; even when the aliases are called something else
     * (fess01.search, fess01.update), so a block on it counts however the aliases are named.
     */
    @Test
    public void test_theDocumentIndexCountsWhateverTheAliasesAreCalled() {
        final SearchEngineClient client = new SearchEngineClient();
        assertTrue(client.isFessIndex("fess.20260101120000000"));
        assertTrue(client.isFessIndex("fess.suggest.20260101120000000"));
    }

    /**
     * Every index that Fess creates from its index definitions (src/main/resources/fess_indices) is one of Fess.
     */
    @Test
    public void test_everyIndexOfTheDefinitionsIsAnIndexOfFess() {
        final SearchEngineClient client = new SearchEngineClient();
        final String[] files = new java.io.File("src/main/resources/fess_indices").list((dir, name) -> name.endsWith(".json"));
        assertTrue(files != null && files.length > 30);
        for (final String file : files) {
            final String index = file.substring(0, file.length() - ".json".length());
            assertTrue(index, client.isFessIndex(index));
        }
    }
}
