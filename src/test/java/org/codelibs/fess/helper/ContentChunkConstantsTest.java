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
package org.codelibs.fess.helper;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ContentChunkConstantsTest extends UnitFessTestCase {

    @Test
    public void test_systemPropertyKeys() {
        assertEquals("content_chunker.enabled", ContentChunkConstants.ENABLED);
        assertEquals("content_chunker.chunker.name", ContentChunkConstants.CHUNKER_NAME);
        assertEquals("content_chunker.embedding.name", ContentChunkConstants.EMBEDDING_NAME);
        assertEquals("content_chunker.embedding.dimension", ContentChunkConstants.EMBEDDING_DIMENSION);
        assertEquals("content_chunker.length.chunk_size", ContentChunkConstants.LENGTH_CHUNK_SIZE);
        assertEquals("content_chunker.length.overlap", ContentChunkConstants.LENGTH_OVERLAP);
        assertEquals("content_chunker.job.max_retry", ContentChunkConstants.JOB_MAX_RETRY);
        assertEquals("content_chunker.job.concurrency", ContentChunkConstants.JOB_CONCURRENCY);
        assertEquals("content_chunker.job.bulk_size", ContentChunkConstants.JOB_BULK_SIZE);
    }

    @Test
    public void test_documentFieldNames() {
        assertEquals("content_chunk_vector", ContentChunkConstants.CONTENT_CHUNK_VECTOR_FIELD);
        assertEquals("content_chunk_status", ContentChunkConstants.CONTENT_CHUNK_STATUS_FIELD);
        assertEquals("content_chunk_retry_count", ContentChunkConstants.CONTENT_CHUNK_RETRY_COUNT_FIELD);
        assertEquals("vector", ContentChunkConstants.VECTOR_SUBFIELD);
    }

    @Test
    public void test_statusValuesAndDefaults() {
        assertEquals("done", ContentChunkConstants.STATUS_DONE);
        assertEquals("failed", ContentChunkConstants.STATUS_FAILED);
        assertEquals(3, ContentChunkConstants.DEFAULT_JOB_MAX_RETRY);
        assertEquals(2, ContentChunkConstants.DEFAULT_JOB_CONCURRENCY);
        assertEquals(20, ContentChunkConstants.DEFAULT_JOB_BULK_SIZE);
    }
}
