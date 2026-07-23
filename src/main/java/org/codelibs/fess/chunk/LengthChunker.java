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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Default {@link Chunker} implementation: fixed-length character windows,
 * with optional overlap between consecutive windows. Never splits a chunk
 * boundary in the middle of a UTF-16 surrogate pair (Java {@link String} is
 * UTF-16 internally; splitting between a high and low surrogate produces two
 * chunks each containing an unpaired, semantically invalid surrogate).
 */
public class LengthChunker implements Chunker {

    private static final Logger logger = LogManager.getLogger(LengthChunker.class);

    /** The name identifier for this chunker. */
    protected static final String NAME = "length";

    /** System property key for the chunk size in characters. */
    protected static final String CHUNK_SIZE_PROPERTY = "content_chunker.length.chunk_size";

    /** System property key for the overlap size in characters. */
    protected static final String OVERLAP_PROPERTY = "content_chunker.length.overlap";

    /** Fallback chunk size used when the configured value is not a positive integer. */
    protected static final int DEFAULT_CHUNK_SIZE = 800;

    /**
     * Minimum allowed chunk size. A chunk size of 1 can force the surrogate-pair
     * boundary adjustment (see {@link #split(String)}) to collide with the
     * defensive forward-progress fallback, re-landing the boundary on the split
     * it was meant to avoid and silently dropping the low surrogate. A chunk
     * size of 2 or more makes that collision mathematically unreachable.
     */
    protected static final int MIN_CHUNK_SIZE = 2;

    /**
     * Maximum allowed chunk size: a generous sanity ceiling, not a model/token
     * limit. It guards against a pathological configuration (e.g. an accidental
     * {@code content_chunker.length.chunk_size=100000000}) that would otherwise
     * allocate absurdly large chunk substrings and risk {@link OutOfMemoryError}.
     * The value is deliberately far larger than any realistic chunk size (typical
     * values are hundreds to a few thousand characters), so clamping to it never
     * affects a reasonable real configuration.
     */
    protected static final int MAX_CHUNK_SIZE = 100_000;

    /**
     * Default constructor.
     */
    public LengthChunker() {
        // Default constructor
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void register() {
        if (ComponentUtil.hasComponent("chunkerManager")) {
            ComponentUtil.getComponent(ChunkerManager.class).register(this);
        }
        warnOnOverlapSideEffect();
    }

    /**
     * Emits a one-time WARN at registration time when a positive overlap is
     * configured: the chunks are stored as the searchable {@code content} array,
     * so overlapped text is duplicated in the index -- inflating BM25 term
     * frequencies for terms in the overlapped regions and repeating those
     * regions in highlights. This is a deliberate trade-off (overlap preserves
     * context across chunk boundaries for embeddings), but operators should
     * enable it knowingly.
     */
    protected void warnOnOverlapSideEffect() {
        try {
            final int overlap = getOverlap();
            if (overlap > 0) {
                logger.warn(
                        "[Chunk] {}={} duplicates the overlapped text inside the searchable content array: "
                                + "BM25 term frequencies are inflated and highlights may repeat the overlapped regions.",
                        OVERLAP_PROPERTY, overlap);
            }
        } catch (final Exception e) {
            // Diagnostics only; never let a config-read failure break component registration.
            if (logger.isDebugEnabled()) {
                logger.debug("[Chunk] Skipping overlap diagnostic.", e);
            }
        }
    }

    @Override
    public List<String> split(final String content) {
        if (StringUtil.isBlank(content)) {
            return Collections.emptyList();
        }
        final int chunkSize = normalizeChunkSize(getChunkSize());
        final int overlap = normalizeOverlap(getOverlap(), chunkSize);
        final int length = content.length();
        final List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < length) {
            if (isMidSurrogatePair(content, start)) {
                start++;
            }
            if (start >= length) {
                break;
            }
            int end = Math.min(start + chunkSize, length);
            if (isMidSurrogatePair(content, end)) {
                end--;
            }
            if (end <= start) {
                // Defensive: guarantee forward progress even in pathological edge cases.
                end = start + 1;
            }
            chunks.add(content.substring(start, end));
            if (end >= length) {
                break;
            }
            int nextStart = end - overlap;
            if (nextStart <= start) {
                // The surrogate-pair adjustment consumed the overlap slack; fall back to
                // the actual chunk end so we never re-process the same position forever.
                nextStart = end;
            }
            start = nextStart;
        }
        return chunks;
    }

    /**
     * Returns true if {@code index} falls between a high surrogate at
     * {@code index - 1} and a low surrogate at {@code index}, i.e. splitting
     * the string at {@code index} would separate a surrogate pair.
     *
     * @param s the content being split
     * @param index the candidate boundary index
     * @return true if the boundary would split a surrogate pair
     */
    protected static boolean isMidSurrogatePair(final CharSequence s, final int index) {
        return index > 0 && index < s.length() && Character.isLowSurrogate(s.charAt(index))
                && Character.isHighSurrogate(s.charAt(index - 1));
    }

    /**
     * Gets the configured chunk size in characters.
     *
     * @return the value of {@code content_chunker.length.chunk_size} (default 800)
     */
    protected int getChunkSize() {
        return getConfigInt(CHUNK_SIZE_PROPERTY, DEFAULT_CHUNK_SIZE);
    }

    /**
     * Gets the configured overlap size in characters.
     *
     * @return the value of {@code content_chunker.length.overlap} (default 0)
     */
    protected int getOverlap() {
        return getConfigInt(OVERLAP_PROPERTY, 0);
    }

    private int getConfigInt(final String key, final int defaultValue) {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(key, null);
        if (value != null) {
            try {
                return Integer.parseInt(value.trim());
            } catch (final NumberFormatException e) {
                logger.warn("[Chunk] Invalid integer for {}: {}. Using default {}.", key, value, defaultValue);
            }
        }
        return defaultValue;
    }

    private int normalizeChunkSize(final int chunkSize) {
        if (chunkSize <= 0) {
            logger.warn("[Chunk] Invalid chunk_size={}, falling back to default {}", chunkSize, DEFAULT_CHUNK_SIZE);
            return DEFAULT_CHUNK_SIZE;
        }
        if (chunkSize < MIN_CHUNK_SIZE) {
            logger.warn("[Chunk] chunk_size={} is below the minimum of {} (required to avoid splitting UTF-16 surrogate pairs), "
                    + "clamping to {}", chunkSize, MIN_CHUNK_SIZE, MIN_CHUNK_SIZE);
            return MIN_CHUNK_SIZE;
        }
        if (chunkSize > MAX_CHUNK_SIZE) {
            logger.warn("[Chunk] chunk_size={} exceeds the maximum of {} (sanity ceiling guarding against absurd/OOM values), "
                    + "clamping to {}", chunkSize, MAX_CHUNK_SIZE, MAX_CHUNK_SIZE);
            return MAX_CHUNK_SIZE;
        }
        return chunkSize;
    }

    private int normalizeOverlap(final int overlap, final int chunkSize) {
        if (overlap < 0) {
            return 0;
        }
        if (overlap >= chunkSize) {
            logger.warn("[Chunk] overlap ({}) >= chunk_size ({}), clamping overlap to 0", overlap, chunkSize);
            return 0;
        }
        return overlap;
    }
}
