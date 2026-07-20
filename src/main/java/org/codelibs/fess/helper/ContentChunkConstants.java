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

/**
 * System-property keys, document field names, status values, and defaults
 * shared across the content-chunk classes.
 */
public final class ContentChunkConstants {

    private ContentChunkConstants() {
        // constants only
    }

    // ----- System property keys -----

    /** Global feature toggle. */
    public static final String ENABLED = "content_chunker.enabled";

    /** Active {@code Chunker} name. */
    public static final String CHUNKER_NAME = "content_chunker.chunker.name";

    /** Active {@code EmbeddingClient} name. */
    public static final String EMBEDDING_NAME = "content_chunker.embedding.name";

    /** Required vector dimension of the configured embedding provider. */
    public static final String EMBEDDING_DIMENSION = "content_chunker.embedding.dimension";

    /** {@code LengthChunker} chunk size in characters. */
    public static final String LENGTH_CHUNK_SIZE = "content_chunker.length.chunk_size";

    /** {@code LengthChunker} overlap in characters. */
    public static final String LENGTH_OVERLAP = "content_chunker.length.overlap";

    /** Maximum per-document retry count before marking a document permanently failed. */
    public static final String JOB_MAX_RETRY = "content_chunker.job.max_retry";

    /**
     * Bounded concurrency for the indexing job's embedding calls. Since the batching change in
     * {@code ChunkVectorJob}, this bounds the number of concurrently in-flight <em>batches</em>
     * (each up to {@link #JOB_BULK_SIZE} documents), not individual documents.
     */
    public static final String JOB_CONCURRENCY = "content_chunker.job.concurrency";

    /** Number of top-ranked chunks selected for the RAG chat context per document. */
    public static final String CHAT_TOP_K = "content_chunker.chat.top_k";

    /** Number of documents whose chunks are grouped into a single {@code embedDocuments()} call. */
    public static final String JOB_BULK_SIZE = "content_chunker.job.bulk_size";

    /**
     * Maximum number of pending document IDs collected (and therefore processed) in a single job
     * run. Bounds the memory the scroll phase holds, so a large-corpus first run cannot OOM by
     * materializing the entire pending-ID set at once. Documents beyond this cap keep
     * {@code content_chunk_status} absent and are picked up by the next (idempotent) scheduled run.
     */
    public static final String JOB_MAX_DOCUMENTS_PER_RUN = "content_chunker.job.max_documents_per_run";

    /** Maximum number of chunks a single document may produce before it is marked terminally skipped (its content is left intact for keyword search) instead of embedded/stored as one oversized nested document. */
    public static final String MAX_CHUNKS_PER_DOCUMENT = "content_chunker.max_chunks_per_document";

    // ----- Document field names -----

    /** Nested field holding one entry per chunk vector. */
    public static final String CONTENT_CHUNK_VECTOR_FIELD = "content_chunk_vector";

    /** Keyword field: absent = unprocessed, "done", or "failed". */
    public static final String CONTENT_CHUNK_STATUS_FIELD = "content_chunk_status";

    /** Integer field: incremented on each per-document processing failure. */
    public static final String CONTENT_CHUNK_RETRY_COUNT_FIELD = "content_chunk_retry_count";

    /** Sub-field of {@link #CONTENT_CHUNK_VECTOR_FIELD} holding the knn_vector value. */
    public static final String VECTOR_SUBFIELD = "vector";

    // ----- Status values -----

    /** {@link #CONTENT_CHUNK_STATUS_FIELD} value: processed successfully. */
    public static final String STATUS_DONE = "done";

    /** {@link #CONTENT_CHUNK_STATUS_FIELD} value: permanently failed (retry budget exhausted). */
    public static final String STATUS_FAILED = "failed";

    /** {@link #CONTENT_CHUNK_STATUS_FIELD} value: intentionally not chunked (blank/no-chunk content, or chunk count over {@link #MAX_CHUNKS_PER_DOCUMENT}). Terminal, but distinct from "done" so the RAG read path does not treat the document as chunked. */
    public static final String STATUS_SKIPPED = "skipped";

    // ----- Defaults -----

    /** Default value of {@link #JOB_MAX_RETRY}. */
    public static final int DEFAULT_JOB_MAX_RETRY = 3;

    /** Default value of {@link #JOB_CONCURRENCY}. */
    public static final int DEFAULT_JOB_CONCURRENCY = 2;

    /** Default value of {@link #JOB_BULK_SIZE}. */
    public static final int DEFAULT_JOB_BULK_SIZE = 20;

    /** Default value of {@link #JOB_MAX_DOCUMENTS_PER_RUN}. */
    public static final int DEFAULT_JOB_MAX_DOCUMENTS_PER_RUN = 10000;

    /** Default value of {@link #CHAT_TOP_K}. */
    public static final int DEFAULT_CHAT_TOP_K = 3;

    /** Default value of {@link #MAX_CHUNKS_PER_DOCUMENT}. */
    public static final int DEFAULT_MAX_CHUNKS_PER_DOCUMENT = 1000;
}
