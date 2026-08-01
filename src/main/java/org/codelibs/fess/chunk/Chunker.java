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
package org.codelibs.fess.chunk;

import java.util.List;

/**
 * Interface for content chunking strategies.
 * Implementations split a document's extracted text content into a list of
 * chunk strings suitable for independent embedding.
 *
 * Unlike {@link org.codelibs.fess.ingest.Ingester}, which runs every registered
 * instance in priority order, exactly one {@link Chunker} is active at a time,
 * selected by name via {@link ChunkerManager} — chunking strategies are not
 * cumulative.
 */
public interface Chunker {

    /**
     * Splits the given content into a list of chunk strings.
     *
     * @param content the document content to split
     * @return the list of chunks, in order; an empty list if content is blank
     */
    List<String> split(String content);

    /**
     * Splits the given content into at most {@code limit} chunk strings, stopping chunk
     * <em>production</em> once the limit is reached rather than producing every chunk and
     * discarding the excess.
     *
     * <p>The caller ({@link org.codelibs.fess.helper.ChunkVectorHelper}) only needs to know whether
     * a document exceeds {@code content_chunker.max_chunks_per_document}, so it asks for
     * {@code cap + 1} chunks: an oversized document is then detected from a
     * {@code cap + 1}-element result without ever materializing the full chunk list of a document
     * that is about to be marked {@code skipped} anyway. With the shipped defaults an unbounded
     * split of a pathological document can retain hundreds of megabytes of substrings across the
     * job's concurrent batches, against the chunk-indexer child JVM's small heap.</p>
     *
     * <p>The default implementation is a correctness-only fallback for third-party chunkers that
     * do not override it: it splits fully and then truncates, so the returned value is right but
     * the memory bound is not achieved. Implementations whose splitting is incremental (such as
     * {@link LengthChunker}) MUST override this to stop early.</p>
     *
     * @param content the document content to split
     * @param limit the maximum number of chunks to produce; a non-positive value yields an empty list
     * @return the first {@code limit} chunks, in order; an empty list if content is blank
     */
    default List<String> split(final String content, final int limit) {
        if (limit <= 0) {
            return List.of();
        }
        final List<String> chunks = split(content);
        if (chunks.size() <= limit) {
            return chunks;
        }
        return List.copyOf(chunks.subList(0, limit));
    }

    /**
     * Returns the name of this chunker (e.g. "length"), used for resolution
     * via the {@code content_chunker.chunker.name} system property.
     *
     * @return the chunker name
     */
    String getName();

    /**
     * Registers this chunker with the {@link ChunkerManager}.
     * Called via postConstruct.
     */
    void register();
}
