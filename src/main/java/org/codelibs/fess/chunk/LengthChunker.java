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
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Default {@link Chunker} implementation: character windows of roughly
 * {@code content_chunker.length.chunk_size} characters, with optional overlap between
 * consecutive windows.
 *
 * <p>By default each cut moves to a suitable break found by {@link ChunkBoundaryFinder} within
 * the search window: the <em>nearest candidate of the highest tier present</em>, not simply the
 * nearest candidate. A line break or sentence end wins over any clause separator or space no
 * matter how much farther back it is, and those win over a script change. Only the boundary
 * moves; no character is dropped, so with the default {@code content_chunker.length.overlap=0}
 * concatenating the chunks still reproduces the content exactly. Set
 * {@code content_chunker.length.boundary.enabled=false} for the legacy fixed-length
 * behaviour.</p>
 *
 * <p>Because the forward search may overshoot, {@code chunk_size} is a target rather than a hard
 * ceiling: a chunk can reach {@code chunk_size + lookahead}. There is a second, independent
 * source of overshoot: {@link ChunkBoundaryFinder}'s grapheme-cluster escape is <em>not</em>
 * governed by {@code lookahead} and can fire even when {@code lookahead_percent=0}, so the worst
 * case reachable through this class is
 * {@code chunk_size + max(lookahead, 2 * ChunkBoundaryFinder.MAX_CLUSTER_ADJUST)} characters --
 * 840 at the shipped defaults ({@code chunk_size=800}, {@code lookahead_percent=5}). Setting
 * {@code lookahead_percent=0} alone does not lower this ceiling below
 * {@code chunk_size + 2 * ChunkBoundaryFinder.MAX_CLUSTER_ADJUST} (832 at the shipped defaults):
 * the cluster escape still runs whenever boundary search runs at all, which only requires
 * {@code lookback_percent > 0}. Boundary search runs at all only when {@code lookback} or
 * {@code lookahead} is positive (see {@link #split(String, int)}), so setting <em>both</em>
 * percentages to {@code 0} makes {@code chunk_size} an exact ceiling with no overshoot.
 * ({@link ChunkBoundaryFinder#findChunkEnd} documents one further {@code + 1}; that branch needs
 * {@code ideal == start + 1}, which {@link #MIN_CHUNK_SIZE} makes unreachable from here.)</p>
 *
 * <p>The backward search can undershoot symmetrically: because a cut may move earlier to reach a
 * boundary, a chunk can end up to {@code lookback} characters <em>shorter</em> than
 * {@code chunk_size} -- 160 characters at the shipped defaults ({@code chunk_size=800}, {@code
 * lookback_percent=20}). A document therefore yields more chunks than before: measured at +9% on
 * English prose and up to +25% in the worst case, which matters against
 * {@code content_chunker.max_chunks_per_document}. Never splits a chunk boundary in the middle of
 * a UTF-16 surrogate pair.</p>
 *
 * <p>When an overlap is configured, the restart point is snapped to a boundary too, which can
 * only move it <em>earlier</em> and so makes the effective overlap larger than the configured
 * value. The snap window is capped at the configured overlap (see {@link #split(String, int)}),
 * so the effective overlap never exceeds {@code 2 * overlap}.</p>
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
     * Minimum allowed chunk size: a sanity floor, not a correctness requirement. A chunk size of
     * 1 is nonsensical in practice, but it is no longer a correctness hazard -- the defensive
     * forward-progress fallback in {@link #split(String, int)} steps a whole code point
     * ({@code start + Character.charCount(content.codePointAt(start))}), so it can no longer
     * collide with the surrogate-pair boundary adjustment and silently drop a low surrogate.
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

    /** System property key toggling boundary-aware splitting. */
    protected static final String BOUNDARY_ENABLED_PROPERTY = "content_chunker.length.boundary.enabled";

    /** System property key for how far before the ideal cut a boundary may be searched, in percent of the chunk size. */
    protected static final String LOOKBACK_PERCENT_PROPERTY = "content_chunker.length.boundary.lookback_percent";

    /** System property key for how far after the ideal cut a sentence end may be searched, in percent of the chunk size. */
    protected static final String LOOKAHEAD_PERCENT_PROPERTY = "content_chunker.length.boundary.lookahead_percent";

    /** Boundary-aware splitting ships enabled; set the property to false for the legacy fixed-length behaviour. */
    protected static final boolean DEFAULT_BOUNDARY_ENABLED = true;

    /** Default backward search window, in percent of the chunk size. */
    protected static final int DEFAULT_LOOKBACK_PERCENT = 20;

    /** Default forward search window, in percent of the chunk size. */
    protected static final int DEFAULT_LOOKAHEAD_PERCENT = 5;

    /** Upper bound for the backward window: beyond half a chunk the chunks get pointlessly short. */
    protected static final int MAX_LOOKBACK_PERCENT = 50;

    /**
     * Upper bound for the forward window. The forward search is the only overshoot path this
     * setting governs -- {@link ChunkBoundaryFinder}'s grapheme-cluster escape can also push a
     * chunk past {@code chunk_size}, but it ignores {@code lookahead} entirely and fires even at
     * {@code lookahead_percent=0}, so it is not bounded by this cap. Within the range this setting
     * does govern, it caps how far past an embedding model's token budget a chunk can grow.
     */
    protected static final int MAX_LOOKAHEAD_PERCENT = 25;

    /** LastaDi component name of the boundary finder, defined in {@code fess_chunk.xml}. */
    protected static final String BOUNDARY_FINDER_COMPONENT = "chunkBoundaryFinder";

    /**
     * Used when {@link #BOUNDARY_FINDER_COMPONENT} is not registered (unit tests constructing a
     * chunker directly, or a container without {@code fess_chunk.xml}). Safe to share: the finder
     * holds no state.
     */
    private static final ChunkBoundaryFinder DEFAULT_BOUNDARY_FINDER = new ChunkBoundaryFinder();

    /**
     * Signature of the configuration problems most recently reported by
     * {@link #warnOnConfigProblem}.
     *
     * <p>Every setting is re-read on each {@code split} call, because {@code system.properties} is
     * live -- which means a misconfigured instance used to emit the same WARN once per crawled
     * document, millions of times over a large corpus. Reporting only when the signature changes
     * keeps a misconfiguration loud exactly once and still reports it again if the operator
     * changes it to something else that is also wrong. This holds the last problem
     * <em>reported</em>, not the last configuration seen, so a clean run in between does not
     * re-arm it -- the WARN describes a standing misconfiguration, and saying it once is the
     * point.</p>
     */
    private final AtomicReference<String> lastConfigProblem = new AtomicReference<>("");

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
        logBoundaryMode();
    }

    /**
     * Logs the effective boundary-search configuration once, at registration time, so that an
     * upgrade which silently turns {@code chunk_size} from a hard ceiling into a target leaves a
     * trace an operator can correlate an embedding-provider rejection against.
     */
    protected void logBoundaryMode() {
        try {
            if (!isBoundaryEnabled()) {
                logger.info("[Chunk] {}=false: chunks are cut at exactly {} characters.", BOUNDARY_ENABLED_PROPERTY,
                        normalizeChunkSize(getChunkSize()));
                return;
            }
            final int chunkSize = normalizeChunkSize(getChunkSize());
            final int lookback =
                    windowSize(chunkSize, normalizeBoundaryPercent(getLookbackPercent(), MAX_LOOKBACK_PERCENT, LOOKBACK_PERCENT_PROPERTY));
            final int lookahead = windowSize(chunkSize,
                    normalizeBoundaryPercent(getLookaheadPercent(), MAX_LOOKAHEAD_PERCENT, LOOKAHEAD_PERCENT_PROPERTY));
            logger.info(
                    "[Chunk] Boundary-aware splitting is enabled: {}={} is a target, not a ceiling. "
                            + "Chunks range from {} to {} characters (lookback={}, lookahead={}); "
                            + "expect more chunks per document than a fixed-length split, which counts against {}.",
                    CHUNK_SIZE_PROPERTY, chunkSize, chunkSize - lookback,
                    chunkSize + Math.max(lookahead, 2 * ChunkBoundaryFinder.MAX_CLUSTER_ADJUST), lookback, lookahead,
                    "content_chunker.max_chunks_per_document");
        } catch (final Exception e) {
            // Diagnostics only; never let a config-read failure break component registration.
            if (logger.isDebugEnabled()) {
                logger.debug("[Chunk] Skipping boundary-mode diagnostic.", e);
            }
        }
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
        return split(content, Integer.MAX_VALUE);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Overridden to stop <em>producing</em> substrings once {@code limit} chunks exist, rather
     * than inheriting {@link Chunker}'s split-everything-then-truncate default: an oversized
     * document that the caller is only going to mark {@code skipped} never materializes its full
     * chunk list.</p>
     */
    @Override
    public List<String> split(final String content, final int limit) {
        if (limit <= 0 || StringUtil.isBlank(content)) {
            return Collections.emptyList();
        }
        final int chunkSize = normalizeChunkSize(getChunkSize());
        final int overlap = normalizeOverlap(getOverlap(), chunkSize);
        final boolean boundaryEnabled = isBoundaryEnabled();
        final int lookback = boundaryEnabled
                ? windowSize(chunkSize, normalizeBoundaryPercent(getLookbackPercent(), MAX_LOOKBACK_PERCENT, LOOKBACK_PERCENT_PROPERTY))
                : 0;
        final int lookahead = boundaryEnabled
                ? windowSize(chunkSize, normalizeBoundaryPercent(getLookaheadPercent(), MAX_LOOKAHEAD_PERCENT, LOOKAHEAD_PERCENT_PROPERTY))
                : 0;
        // Resolved once per document, never cached on this singleton: see getBoundaryFinder().
        final ChunkBoundaryFinder finder = lookback > 0 || lookahead > 0 ? getBoundaryFinder() : null;
        final int length = content.length();
        // Pre-size to avoid repeated growth on large documents. limit may be Integer.MAX_VALUE,
        // so cap with long arithmetic before narrowing.
        final int stride = Math.max(1, chunkSize - overlap);
        final List<String> chunks = new ArrayList<>((int) Math.min(limit, (long) length / stride + 2L));
        int movedBack = 0;
        int movedForward = 0;
        int hardCut = 0;
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
            if (finder != null) {
                final int idealEnd = end;
                end = finder.findChunkEnd(content, start, end, length, lookback, lookahead);
                if (end < idealEnd) {
                    movedBack++;
                } else if (end > idealEnd) {
                    movedForward++;
                } else {
                    hardCut++;
                }
                if (isMidSurrogatePair(content, end)) {
                    // Defensive: a custom finder is not required to be code-point aligned.
                    end--;
                }
            }
            if (end <= start) {
                // Defensive: guarantee forward progress even in pathological edge cases. Step a
                // whole code point so a custom finder returning a mid-pair offset cannot make the
                // fallback strand a lone surrogate -- the loop-top adjustment would then skip its
                // low half, losing a character.
                end = start + Character.charCount(content.codePointAt(start));
            }
            chunks.add(content.substring(start, end));
            if (chunks.size() >= limit) {
                // Production bound: stop here instead of splitting the remainder only to have the
                // caller discard it. See Chunker#split(String, int).
                break;
            }
            if (end >= length) {
                break;
            }
            int nextStart = end - overlap;
            if (finder != null && overlap > 0) {
                // Cap the snap window at the configured overlap. snapOverlapStart only ever moves
                // the restart point earlier, so an uncapped `lookback` window (derived from
                // chunk_size, not from overlap) would let the effective overlap reach
                // overlap + lookback -- 165 for a configured 10 at the shipped defaults, silently
                // multiplying the index duplication warnOnOverlapSideEffect() exists to warn
                // about. Capped, the effective overlap can at most double.
                nextStart = finder.snapOverlapStart(content, start, nextStart, end, Math.min(lookback, overlap));
                if (isMidSurrogatePair(content, nextStart)) {
                    nextStart--;
                }
            }
            if (nextStart <= start) {
                // The surrogate-pair adjustment consumed the overlap slack; fall back to
                // the actual chunk end so we never re-process the same position forever.
                nextStart = end;
            }
            start = nextStart;
        }
        if (finder != null) {
            logBoundaryOutcome(chunks.size(), movedBack, movedForward, hardCut);
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
     * <p>Changing this value only affects documents that have not yet reached a terminal
     * {@code content_chunk_status}. A document already stored as a chunk array keeps its original
     * boundaries: nothing records which chunk size produced it, and
     * {@code ChunkVectorHelper#extractExistingChunks} deliberately reuses the stored array rather
     * than re-splitting it. Re-chunking an existing corpus therefore requires a recrawl -- which
     * replaces {@code _source} wholesale and so does pick up the new size.</p>
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

    /**
     * Returns whether each chunk boundary is moved to the nearest candidate of the highest tier
     * present in the search window -- a line break or sentence end beats any clause separator or
     * space however much farther back it is, and those beat a script change -- instead of being
     * cut at exactly {@code chunk_size} characters.
     *
     * @return the value of {@code content_chunker.length.boundary.enabled} (default true)
     */
    protected boolean isBoundaryEnabled() {
        return getConfigBoolean(BOUNDARY_ENABLED_PROPERTY, DEFAULT_BOUNDARY_ENABLED);
    }

    /**
     * Returns how far before the ideal cut a boundary may be searched, in percent of the chunk size.
     *
     * @return the value of {@code content_chunker.length.boundary.lookback_percent} (default 20)
     */
    protected int getLookbackPercent() {
        return getConfigInt(LOOKBACK_PERCENT_PROPERTY, DEFAULT_LOOKBACK_PERCENT);
    }

    /**
     * Returns how far after the ideal cut a sentence end may be searched, in percent of the chunk size.
     *
     * @return the value of {@code content_chunker.length.boundary.lookahead_percent} (default 5)
     */
    protected int getLookaheadPercent() {
        return getConfigInt(LOOKAHEAD_PERCENT_PROPERTY, DEFAULT_LOOKAHEAD_PERCENT);
    }

    /**
     * Resolves the boundary finder.
     *
     * <p>Deliberately not an {@code @Resource} field: unit tests construct this chunker with
     * {@code new}, where field injection never runs. Deliberately not cached either: a cached
     * reference would go stale across container re-creation. Callers resolve it once per
     * {@code split} call.</p>
     *
     * @return the registered {@code chunkBoundaryFinder} component, or a built-in fallback
     */
    protected ChunkBoundaryFinder getBoundaryFinder() {
        if (ComponentUtil.hasComponent(BOUNDARY_FINDER_COMPONENT)) {
            return ComponentUtil.getComponent(BOUNDARY_FINDER_COMPONENT);
        }
        return DEFAULT_BOUNDARY_FINDER;
    }

    /**
     * Reports a configuration problem, but only when it differs from the one reported last.
     *
     * @param signature identifies the problem; an unchanged signature is not reported again
     * @param format the log message pattern
     * @param args the log message arguments
     */
    protected void warnOnConfigProblem(final String signature, final String format, final Object... args) {
        if (!signature.equals(lastConfigProblem.getAndSet(signature))) {
            logger.warn(format, args);
        }
    }

    /**
     * Summarises, at DEBUG, how often boundary search actually moved a cut for this document.
     *
     * <p>Without this the feature is unfalsifiable in a running system: a corpus the character
     * tables do not cover -- a language whose sentence marks {@link ChunkBoundaryFinder} does not
     * know, or text with no punctuation at all -- silently gets 100% hard cuts and looks exactly
     * like the feature working. A high {@code hardCut} share is the signal to raise
     * {@code lookback_percent} or to extend the tables.</p>
     *
     * <p>Only the coarse moved/hard-cut split is reported, not a per-tier histogram: which tier
     * won is known inside {@link ChunkBoundaryFinder#findChunkEnd} alone, and widening its
     * signature to report it would change the {@code protected} seam a deployment subclasses.</p>
     *
     * @param chunks the number of chunks produced
     * @param movedBack how many cuts a backward tier pulled earlier
     * @param movedForward how many cuts the forward search or the cluster escape pushed later
     * @param hardCut how many cuts landed on the fixed-length offset because no tier had a candidate
     */
    protected void logBoundaryOutcome(final int chunks, final int movedBack, final int movedForward, final int hardCut) {
        if (logger.isDebugEnabled()) {
            logger.debug("[Chunk] Boundary outcome: chunks={} movedBack={} movedForward={} hardCut={} ({}% of cuts unmoved).", chunks,
                    movedBack, movedForward, hardCut, chunks == 0 ? 0 : 100 * hardCut / chunks);
        }
    }

    private boolean getConfigBoolean(final String key, final boolean defaultValue) {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(key, null);
        if (value != null) {
            final String trimmed = value.trim();
            if (Constants.TRUE.equalsIgnoreCase(trimmed)) {
                return true;
            }
            if (Constants.FALSE.equalsIgnoreCase(trimmed)) {
                return false;
            }
            warnOnConfigProblem(key + "=" + value, "[Chunk] Invalid boolean for {}: {}. Using default {}.", key, value, defaultValue);
        }
        return defaultValue;
    }

    private int normalizeBoundaryPercent(final int value, final int max, final String key) {
        if (value < 0) {
            warnOnConfigProblem(key + "=" + value, "[Chunk] {}={} is negative, treating it as 0 (that direction is not searched)", key,
                    value);
            return 0;
        }
        if (value > max) {
            warnOnConfigProblem(key + "=" + value, "[Chunk] {}={} exceeds the maximum of {}, clamping to {}", key, value, max, max);
            return max;
        }
        return value;
    }

    private int windowSize(final int chunkSize, final int percent) {
        if (percent <= 0) {
            return 0;
        }
        return Math.max(1, (int) ((long) chunkSize * percent / 100L));
    }

    private int getConfigInt(final String key, final int defaultValue) {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(key, null);
        if (value != null) {
            try {
                return Integer.parseInt(value.trim());
            } catch (final NumberFormatException e) {
                warnOnConfigProblem(key + "=" + value, "[Chunk] Invalid integer for {}: {}. Using default {}.", key, value, defaultValue);
            }
        }
        return defaultValue;
    }

    private int normalizeChunkSize(final int chunkSize) {
        if (chunkSize <= 0) {
            warnOnConfigProblem(CHUNK_SIZE_PROPERTY + "=" + chunkSize, "[Chunk] Invalid chunk_size={}, falling back to default {}",
                    chunkSize, DEFAULT_CHUNK_SIZE);
            return DEFAULT_CHUNK_SIZE;
        }
        if (chunkSize < MIN_CHUNK_SIZE) {
            warnOnConfigProblem(CHUNK_SIZE_PROPERTY + "=" + chunkSize,
                    "[Chunk] chunk_size={} is below the minimum of {} (a sanity floor), clamping to {}", chunkSize, MIN_CHUNK_SIZE,
                    MIN_CHUNK_SIZE);
            return MIN_CHUNK_SIZE;
        }
        if (chunkSize > MAX_CHUNK_SIZE) {
            warnOnConfigProblem(CHUNK_SIZE_PROPERTY + "=" + chunkSize,
                    "[Chunk] chunk_size={} exceeds the maximum of {} (sanity ceiling guarding against absurd/OOM values), "
                            + "clamping to {}",
                    chunkSize, MAX_CHUNK_SIZE, MAX_CHUNK_SIZE);
            return MAX_CHUNK_SIZE;
        }
        return chunkSize;
    }

    private int normalizeOverlap(final int overlap, final int chunkSize) {
        if (overlap < 0) {
            return 0;
        }
        if (overlap >= chunkSize) {
            warnOnConfigProblem(OVERLAP_PROPERTY + "=" + overlap + "/" + chunkSize,
                    "[Chunk] overlap ({}) >= chunk_size ({}), clamping overlap to 0", overlap, chunkSize);
            return 0;
        }
        return overlap;
    }
}
