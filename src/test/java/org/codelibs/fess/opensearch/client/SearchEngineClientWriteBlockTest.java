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

    private static Map<String, Object> block(final String... levels) {
        return Map.of("description", "a block", "retryable", true, "levels", List.of(levels));
    }

    @Test
    public void test_floodStageBlockOnAnIndexIsAWriteBlock() {
        final Map<String, Object> state = Map.of("cluster_name", "c", "blocks",
                Map.of("indices", Map.of("fess_config.label_type", Map.of("12", block("write", "metadata_write")))));
        assertTrue(SearchEngineClient.hasWriteBlock(state));
    }

    @Test
    public void test_globalWriteBlockIsAWriteBlock() {
        final Map<String, Object> state = Map.of("blocks", Map.of("global", Map.of("2", block("read", "write", "metadata_write"))));
        assertTrue(SearchEngineClient.hasWriteBlock(state));
    }

    @Test
    public void test_noBlocksIsNotAWriteBlock() {
        assertFalse(SearchEngineClient.hasWriteBlock(Map.of("cluster_name", "c", "blocks", Map.of())));
        assertFalse(SearchEngineClient.hasWriteBlock(Map.of("cluster_name", "c")));
    }

    @Test
    public void test_blockWithoutWriteLevelIsNotAWriteBlock() {
        final Map<String, Object> state = Map.of("blocks", Map.of("indices", Map.of("fess.20260101", Map.of("9", block("metadata_read")))));
        assertFalse(SearchEngineClient.hasWriteBlock(state));
    }

    @Test
    public void test_closedIndexIsNotAWriteBlock() {
        final Map<String, Object> state =
                Map.of("blocks", Map.of("indices", Map.of("old_index", Map.of("4", block("read", "write", "metadata_write")))));
        assertFalse(SearchEngineClient.hasWriteBlock(state));
    }
}
