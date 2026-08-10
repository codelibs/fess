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

/**
 * Finds a "good" place to cut a chunk near an ideal character offset, instead of cutting
 * blindly at a fixed length.
 *
 * <p>A boundary is only ever <em>moved</em>: no character is dropped, so concatenating the
 * chunks still reproduces the original text exactly. This matters because
 * {@link org.codelibs.fess.helper.ChunkVectorHelper} stores the chunks as the searchable
 * {@code content} array.</p>
 *
 * <p>Registered as the LastaDi component {@code chunkBoundaryFinder} in {@code fess_chunk.xml}.
 * The class is deliberately non-final and its character predicates are {@code protected} so a
 * deployment can register a subclass that only swaps the character sets. Instances are shared
 * singletons used concurrently by {@code content_chunker.job.concurrency} job threads, so this
 * class must never hold mutable state.</p>
 */
public class ChunkBoundaryFinder {

    /** Zero-width joiner; splitting next to it breaks an emoji/grapheme sequence. */
    protected static final int ZWJ = 0x200D;

    /** Returned in place of an offset when a tier has no candidate. */
    private static final int NO_CANDIDATE = -1;

    /**
     * How far the hard-cut fallback may walk back (then forward) to leave a grapheme cluster
     * intact, in code points.
     *
     * <p>{@code protected} suggests a subclass can retune this budget by redeclaring the
     * constant, but it cannot: the sole reader, {@link #adjustToClusterStart}, is {@code private}
     * and binds this class's own constant at compile time -- {@code static} fields have no
     * virtual dispatch, so a subclass's redeclaration is simply never read. This budget is not
     * currently part of the swappable seam that {@link #isClusterContinuation} and the other
     * {@code protected} predicates form; kept {@code protected} rather than narrowed to
     * {@code private} to avoid an unrelated visibility change.</p>
     */
    protected static final int MAX_CLUSTER_ADJUST = 16;

    /**
     * Default constructor.
     */
    public ChunkBoundaryFinder() {
        // Default constructor
    }

    /**
     * Returns the offset at which the current chunk should end.
     *
     * <p>Walks backward from {@code ideal} looking for a STRONG boundary first, then a WEAK or
     * SCRIPT one. When no STRONG boundary is found behind {@code ideal}, a forward search looks
     * for a sentence end or line break just ahead; that candidate beats a backward WEAK or
     * SCRIPT one. Failing all of that, the offset is hard-cut at {@code ideal} and adjusted --
     * backward first, then forward if backward finds nothing -- to avoid splitting a grapheme
     * cluster; see {@link #adjustToClusterStart}.</p>
     *
     * <p>No path returns an offset that splits a grapheme cluster: a tier candidate that would
     * strand a combining mark or a joiner half is rejected during the scan and the search
     * continues, so the protection is not limited to the hard-cut path.</p>
     *
     * <p>The returned offset may exceed {@code ideal} by at most
     * {@code max(lookahead, 2 * MAX_CLUSTER_ADJUST) + 1} UTF-16 units. The forward sentence
     * search and the grapheme-cluster escape are <em>mutually exclusive</em>, not additive: this
     * method returns as soon as the forward search succeeds, so the escape only ever runs when
     * the forward search found nothing, and their two overshoot budgets never stack. The
     * trailing {@code + 1} comes from {@link #alignToCodePoint}, which can move the aligned
     * offset one UTF-16 unit past {@code ideal} before either search runs, to step off the
     * inside of a surrogate pair. The cluster escape is <em>not</em> governed by
     * {@code lookahead} and fires even when {@code lookahead} is {@code 0}: returning an offset
     * that splits a grapheme cluster -- an emoji sequence rendering as mismatched halves across
     * two chunks -- defeats the purpose of this search, so the escape is allowed to overshoot
     * where the other tiers are not; a UTF-16 surrogate pair itself is never split regardless of
     * how far either search has to move.</p>
     *
     * <p>The {@code start < result <= limitEnd} guarantee below holds for in-contract arguments
     * ({@code 0 <= start < ideal <= limitEnd == text.length()}). Out-of-contract arguments --
     * e.g. {@code ideal} at or below {@code start}, or {@code limitEnd} past the text -- are not
     * rejected or normalized back onto the contract; the result is only clamped as far as the
     * internal {@code bound}/{@code origin} defensive clamps reach, and can land at or below
     * {@code start} or past {@code limitEnd} in those cases. This is unlike the sibling
     * {@link #snapOverlapStart}, which degrades any out-of-contract shape to {@code end}.</p>
     *
     * @param text the full content being split
     * @param start the offset at which the current chunk starts
     * @param ideal the offset the fixed-length rule would cut at
     * @param limitEnd the highest offset any chunk may end at (the content length)
     * @param lookback how many characters before {@code ideal} may be searched; 0 disables it
     * @param lookahead how many characters after {@code ideal} may be searched; 0 disables it
     * @return the chunk end offset; always {@code start < result <= limitEnd} for in-contract
     *         arguments
     */
    public int findChunkEnd(final CharSequence text, final int start, final int ideal, final int limitEnd, final int lookback,
            final int lookahead) {
        // Clamp defensively: limitEnd is contractually the content length, but this is a public
        // component method and an oversized bound must not turn into an index-out-of-bounds.
        final int bound = Math.min(limitEnd, text.length());
        // Mirror of the bound clamp: start is contractually non-negative, but a public component
        // method must not turn an out-of-contract argument into an index-out-of-bounds.
        final int origin = Math.max(start, 0);
        final int base = alignToCodePoint(text, origin, ideal, bound);
        if (base >= bound || base <= origin) {
            return base;
        }
        int fallback = NO_CANDIDATE;
        if (lookback > 0) {
            final long packed = searchBackward(text, origin, base, bound, lookback);
            final int strong = unpackStrong(packed);
            if (strong != NO_CANDIDATE) {
                return strong;
            }
            fallback = unpackWeakOrScript(packed);
        }
        if (lookahead > 0) {
            final int forward = searchForward(text, base, bound, lookahead);
            if (forward != NO_CANDIDATE) {
                return forward;
            }
        }
        if (fallback != NO_CANDIDATE) {
            return fallback;
        }
        return adjustToClusterStart(text, base, origin, bound);
    }

    /**
     * Returns the offset at which the next chunk should start when an overlap is configured.
     *
     * <p>Walks backward from {@code ideal} looking for a STRONG boundary first, then a WEAK or
     * SCRIPT one, exactly like the backward half of {@link #findChunkEnd}. There is no forward
     * search: moving the overlap start forward would skip text that the previous chunk already
     * ended before, so a candidate is only ever accepted if it stays within {@code (start, end]}.
     * When no candidate qualifies, the aligned {@code ideal} offset is returned unchanged.</p>
     *
     * <p>The caller must pass {@code start < end}; given that, the result satisfies
     * {@code start < result <= end}. Any other argument shape -- {@code ideal <= start}, or
     * {@code ideal} past {@code end} or the text -- is out of contract and degrades to
     * {@code end} (equivalently: the overlap is dropped for that step) rather than throwing or
     * handing back an offset the caller would have to repair itself.</p>
     *
     * @param text the full content being split
     * @param start the offset at which the current chunk starts
     * @param ideal the offset the fixed overlap rule would restart at
     * @param end the offset at which the current chunk ends
     * @param lookback how many characters before {@code ideal} may be searched; 0 disables it
     * @return the next start offset; always {@code start < result <= end}
     */
    public int snapOverlapStart(final CharSequence text, final int start, final int ideal, final int end, final int lookback) {
        // The same pair of defensive clamps findChunkEnd applies, with `end` in place of the
        // content length: the overlap start may never move past the chunk it overlaps.
        final int bound = Math.min(end, text.length());
        final int origin = Math.max(start, 0);
        final int base = alignToCodePoint(text, origin, ideal, bound);
        if (base <= origin || base > bound) {
            // Out of contract: the ideal offset makes no forward progress, or lies past the chunk
            // it overlaps. No valid overlap start exists, so restart at the chunk end -- i.e. drop
            // the overlap for this step rather than returning an offset the caller must repair.
            return bound;
        }
        if (lookback <= 0 || base == origin + 1) {
            return base;
        }
        // The scan's limitEnd is `bound`, and there is no forward search here: moving the overlap
        // start forward would skip text.
        final long packed = searchBackward(text, origin, base, bound, lookback);
        final int strong = unpackStrong(packed);
        if (strong != NO_CANDIDATE && strong > origin && strong <= bound) {
            return strong;
        }
        final int weakOrScript = unpackWeakOrScript(packed);
        if (weakOrScript != NO_CANDIDATE && weakOrScript > origin && weakOrScript <= bound) {
            return weakOrScript;
        }
        return base;
    }

    /**
     * Pulls an offset off the inside of a UTF-16 surrogate pair so the whole scan can step by
     * code points.
     *
     * <p>The offset normally moves back to the start of the pair. When moving back would reach
     * {@code start} — leaving the caller no forward progress — it moves forward past the pair
     * instead, provided that stays within {@code upperBound}.</p>
     *
     * @param text the full content being split
     * @param start the offset at which the current chunk starts
     * @param index the offset to align
     * @param upperBound the highest offset the result may take
     * @return the aligned offset
     */
    private int alignToCodePoint(final CharSequence text, final int start, final int index, final int upperBound) {
        if (index <= start || index >= text.length() || !Character.isLowSurrogate(text.charAt(index))
                || !Character.isHighSurrogate(text.charAt(index - 1))) {
            return index;
        }
        if (index > start + 1) {
            return index - 1;
        }
        return index + 1 <= upperBound ? index + 1 : index;
    }

    /**
     * Single reverse pass over the lookback window.
     *
     * <p>Walking backward one code point at a time, a run of skippable code points (spaces and
     * closing marks) is accumulated; when the run ends, the character in front of it decides
     * which tier the run's end position belongs to. Doing it in one pass keeps the cost O(W)
     * instead of the O(W^2) that re-scanning the run at every candidate would cost.</p>
     *
     * <p>Both tiers are returned because the caller must be able to tell "a strong boundary was
     * found" from "only a weak one was": the forward search only runs when there is no strong
     * boundary behind the ideal offset.</p>
     *
     * @param text the full content being split
     * @param start the offset at which the current chunk starts
     * @param ideal the offset the fixed-length rule would cut at
     * @param limitEnd the highest offset any chunk may end at
     * @param lookback how many characters before {@code ideal} may be searched
     * @return the strong candidate in the high 32 bits and the weak-or-script candidate in the
     *         low 32 bits; either is {@code NO_CANDIDATE} when that tier found nothing
     */
    private long searchBackward(final CharSequence text, final int start, final int ideal, final int limitEnd, final int lookback) {
        final int floor = Math.max(start + 1, ideal - lookback);
        int weak = NO_CANDIDATE;
        int script = NO_CANDIDATE;
        int runEnd = NO_CANDIDATE;
        // The two code points bracketing runEnd, carried so the cluster guard below costs no
        // extra decoding: both are already in hand at the moment the run starts.
        int runEndCp = -1;
        int runEndBeforeCp = -1;
        boolean runHasNewline = false;
        boolean runHasSpace = false;
        boolean runHasClosing = false;
        int b = ideal;
        // codePointAt(text, b) is always the code point that codePointBefore() decoded as
        // "before" on the previous iteration (the cursor moved back by exactly its charCount()),
        // so it is carried forward here instead of being decoded again every iteration.
        int after = b < limitEnd ? Character.codePointAt(text, b) : -1;
        while (b >= floor) {
            final int before = Character.codePointBefore(text, b);
            final int beforeLen = Character.charCount(before);
            if (isSkippableAfterBoundary(before)) {
                if (runEnd == NO_CANDIDATE) {
                    runEnd = b;
                    runEndCp = after;
                    runEndBeforeCp = before;
                }
                runHasNewline |= isNewline(before);
                runHasSpace |= isBreakableSpace(before);
                runHasClosing |= isClosingBracket(before);
                b -= beforeLen;
                after = before;
                continue;
            }
            final int candidate = runEnd == NO_CANDIDATE ? b : runEnd;
            final boolean spaceSatisfied = runHasSpace || candidate >= limitEnd;
            // A tier candidate is no more allowed to split a grapheme cluster than the hard cut
            // is; an offset that strands a combining mark or a joiner half is rejected and the
            // scan simply keeps looking. Without this the boundary search could split a cluster
            // the fixed-length cut it replaces had left intact.
            final boolean candidateSafe = candidate >= limitEnd
                    || !splitsCluster(runEnd == NO_CANDIDATE ? after : runEndCp, runEnd == NO_CANDIDATE ? before : runEndBeforeCp);
            final boolean bSafe = b >= limitEnd || !splitsCluster(after, before);
            // `after` is the code point immediately following `before`, so this is exactly the
            // "punctuation sits between digits" test -- a run of spaces in between means the
            // punctuation is not a decimal or thousands separator.
            final boolean blockedByDigit = isPunctuationRequiringNonDigit(before) && after >= 0 && Character.isDigit(after);
            if (candidateSafe && !blockedByDigit
                    && (runHasNewline || isSentenceTerminator(before) && (!isAsciiPunctuationRequiringSpace(before) || spaceSatisfied))) {
                return pack(candidate, weak != NO_CANDIDATE ? weak : script);
            }
            if (weak == NO_CANDIDATE) {
                if (candidateSafe && (runHasSpace || runHasClosing
                        || isClauseSeparator(before) && (!isAsciiPunctuationRequiringSpace(before) || spaceSatisfied) && !blockedByDigit)) {
                    weak = candidate;
                } else if (b < limitEnd && bSafe && isOpeningBracket(after)) {
                    weak = b;
                }
            }
            // Once a WEAK candidate exists this tier can never win (WEAK beats SCRIPT), so
            // isScriptBoundary() -- a binary search over the Unicode block table -- must not run.
            if (script == NO_CANDIDATE && weak == NO_CANDIDATE && b < limitEnd && bSafe && isScriptBoundary(before, after)) {
                script = b;
            }
            runEnd = NO_CANDIDATE;
            runHasNewline = false;
            runHasSpace = false;
            runHasClosing = false;
            b -= beforeLen;
            after = before;
        }
        if (runEnd != NO_CANDIDATE) {
            // The window ran out inside a skippable run. The run-based rules (a newline in the
            // run makes it STRONG; any space or closing mark makes it WEAK) do not depend on the
            // character in front of the run, and runEnd is >= floor, so this candidate is valid
            // and must not be discarded just because the window ended mid-run.
            final boolean runEndSafe = runEnd >= limitEnd || !splitsCluster(runEndCp, runEndBeforeCp);
            if (runEndSafe) {
                if (runHasNewline) {
                    return pack(runEnd, weak != NO_CANDIDATE ? weak : script);
                }
                if (weak == NO_CANDIDATE && (runHasSpace || runHasClosing)) {
                    weak = runEnd;
                }
            }
        }
        return pack(NO_CANDIDATE, weak != NO_CANDIDATE ? weak : script);
    }

    /**
     * Returns true if placing a boundary between the two given adjacent code points would split a
     * grapheme cluster.
     *
     * @param at the code point at the candidate offset, or a negative value when there is none
     * @param before the code point immediately before the candidate offset
     * @return true if the boundary must not be placed there
     */
    private boolean splitsCluster(final int at, final int before) {
        return at >= 0 && (isClusterContinuation(at) || isClusterJoiner(before));
    }

    /**
     * {@link #splitsCluster} for an offset whose bracketing code points are not already in hand.
     *
     * @param text the full content being split
     * @param index the candidate offset
     * @param limitEnd the highest offset any chunk may end at
     * @return true if the boundary must not be placed there
     */
    private boolean splitsClusterAt(final CharSequence text, final int index, final int limitEnd) {
        if (index <= 0 || index >= limitEnd) {
            return false;
        }
        return splitsCluster(Character.codePointAt(text, index), Character.codePointBefore(text, index));
    }

    private long pack(final int strong, final int weakOrScript) {
        return (long) strong << 32 | weakOrScript & 0xFFFFFFFFL;
    }

    private int unpackStrong(final long packed) {
        return (int) (packed >> 32);
    }

    private int unpackWeakOrScript(final long packed) {
        return (int) packed;
    }

    /**
     * Scans forward from the ideal offset for a sentence end or a line break.
     *
     * <p>Only STRONG boundaries are looked for ahead: overshooting the configured chunk size is
     * acceptable to keep a sentence whole, but not to land on a comma or a space.</p>
     *
     * @param text the full content being split
     * @param ideal the offset the fixed-length rule would cut at
     * @param limitEnd the highest offset any chunk may end at
     * @param lookahead how many characters after {@code ideal} may be searched
     * @return the forward candidate, or {@code -1} if there is none
     */
    private int searchForward(final CharSequence text, final int ideal, final int limitEnd, final int lookahead) {
        final int ceiling = Math.min(limitEnd, ideal + lookahead);
        int i = ideal;
        while (i < ceiling) {
            final int cp = Character.codePointAt(text, i);
            final int cpLen = Character.charCount(cp);
            if (isNewline(cp) || isSentenceTerminator(cp)) {
                int p = i + cpLen;
                boolean sawSpace = false;
                while (p < ceiling) {
                    final int next = Character.codePointAt(text, p);
                    if (!isSkippableAfterBoundary(next)) {
                        break;
                    }
                    sawSpace |= isBreakableSpace(next);
                    p += Character.charCount(next);
                }
                final boolean spaceSatisfied = sawSpace || p >= limitEnd;
                final int afterCp = i + cpLen < limitEnd ? Character.codePointAt(text, i + cpLen) : -1;
                final boolean blockedByDigit = isPunctuationRequiringNonDigit(cp) && afterCp >= 0 && Character.isDigit(afterCp);
                if ((isNewline(cp) || !isAsciiPunctuationRequiringSpace(cp) || spaceSatisfied) && !blockedByDigit && p <= ceiling
                        && !splitsClusterAt(text, p, limitEnd)) {
                    return p;
                }
            }
            i += cpLen;
        }
        return NO_CANDIDATE;
    }

    /**
     * Walks the hard-cut offset off a grapheme cluster so the boundary does not split it.
     *
     * <p>Three phases, tried in order:</p>
     * <ol>
     * <li>Walk backward from {@code index}, at most {@link #MAX_CLUSTER_ADJUST} code points and
     * never below {@code start + 1}, and return the first position that is safe -- one where
     * {@link Character#codePointAt} is not a cluster continuation and
     * {@link Character#codePointBefore} is not {@link #ZWJ}.</li>
     * <li>If backward found nothing (it exhausted its budget or ran into {@code start} -- for
     * example a ZWJ-joined emoji sequence that begins exactly at {@code start}, which backward
     * can never clear no matter how far the window extends), walk forward from {@code index}
     * instead, at most {@link #MAX_CLUSTER_ADJUST} code points, stepping by
     * {@link Character#charCount} so the cursor is always code-point aligned. This is what
     * guarantees a UTF-16 surrogate pair is never split, even when {@code upperBound} itself
     * falls inside one: the walk only ever stops exactly on {@code upperBound} or on the start
     * of a code point, never in between.</li>
     * <li>If neither walk found a safe position, return {@code index} unchanged -- still
     * code-point aligned, just not guaranteed cluster-safe.</li>
     * </ol>
     *
     * @param text the full content being split
     * @param index the hard-cut offset
     * @param start the offset at which the current chunk starts; the result stays above it
     * @param upperBound the highest offset the result may take
     * @return the adjusted offset, or {@code index} if no safe adjustment exists in either
     *         direction
     */
    private int adjustToClusterStart(final CharSequence text, final int index, final int start, final int upperBound) {
        // `start` is guaranteed non-negative here: findChunkEnd is this method's only caller and
        // already clamps it (see `origin` there), so codePointBefore(text, b) below can never be
        // reached with a non-positive `b`.
        int b = index;
        for (int i = 0; i < MAX_CLUSTER_ADJUST && b > start; i++) {
            final int at = Character.codePointAt(text, b);
            final int before = Character.codePointBefore(text, b);
            if (!isClusterContinuation(at) && !isClusterJoiner(before)) {
                return b;
            }
            b -= Character.charCount(before);
        }
        // Backward found nothing safe without going through `start`. Walk forward from the
        // original hard-cut offset instead, stepping by charCount() so the cursor is always
        // code-point aligned -- a surrogate pair can then never be split, even when
        // `upperBound` itself falls inside one.
        int f = index;
        int cp = Character.codePointAt(text, f);
        for (int i = 0; i < MAX_CLUSTER_ADJUST && f < upperBound; i++) {
            final int cpLen = Character.charCount(cp);
            if (f + cpLen > upperBound) {
                // The next aligned step would overshoot upperBound; nothing more can be tried.
                break;
            }
            f += cpLen;
            if (f >= upperBound) {
                // Landed exactly on upperBound: always code-point aligned by construction, and
                // there is no character at-or-after it in this chunk left to split.
                return f;
            }
            final int next = Character.codePointAt(text, f);
            if (!isClusterContinuation(next) && !isClusterJoiner(cp)) {
                return f;
            }
            cp = next;
        }
        return index;
    }

    /**
     * Returns true if the code point is a break opportunity that behaves like a space.
     *
     * <p>Includes the code points {@link Character#isWhitespace(int)} reports as
     * <em>non</em>-whitespace but that Fess's extracted text treats as separators
     * (see {@code crawler.document.space.chars}).</p>
     *
     * @param cp the code point
     * @return true if the code point is a breakable space
     */
    protected boolean isBreakableSpace(final int cp) {
        if (cp == ' ') {
            // By far the most common code point in extracted text: check it before anything else.
            return true;
        }
        if (Character.isWhitespace(cp)) {
            return true;
        }
        switch (cp) {
        case 0x0085: // NEXT LINE (NEL) -- Character.isWhitespace() rejects it
        case 0x00A0: // NO-BREAK SPACE
        case 0x2007: // FIGURE SPACE
        case 0x202F: // NARROW NO-BREAK SPACE
        case 0x200B: // ZERO WIDTH SPACE
        case 0x200C: // ZERO WIDTH NON-JOINER
        case 0x2060: // WORD JOINER
        case 0xFEFF: // ZERO WIDTH NO-BREAK SPACE / BOM
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true if the code point ends a line.
     *
     * @param cp the code point
     * @return true if the code point is a line separator
     */
    protected boolean isNewline(final int cp) {
        return cp == '\n' || cp == '\r' || cp == 0x0085 || cp == 0x2028 || cp == 0x2029;
    }

    /**
     * Returns true if the code point ends a sentence.
     *
     * @param cp the code point
     * @return true if the code point is a sentence terminator
     */
    protected boolean isSentenceTerminator(final int cp) {
        switch (cp) {
        case '.':
        case '!':
        case '?':
        case 0x3002: // 。
        case 0xFF0E: // ．
        case 0xFF61: // ｡
        case 0xFF01: // ！
        case 0xFF1F: // ？
        case 0x2025: // ‥
        case 0x2026: // …
        case 0x203C: // ‼
        case 0x2047: // ⁇
        case 0x2048: // ⁈
        case 0x2049: // ⁉
        case 0x06D4: // Arabic full stop
        case 0x1362: // Ethiopic full stop
        case 0x2E3C: // stenographic full stop
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true if the code point separates clauses within a sentence.
     *
     * @param cp the code point
     * @return true if the code point is a clause separator
     */
    protected boolean isClauseSeparator(final int cp) {
        switch (cp) {
        case ',':
        case ';':
        case ':':
        case '-':
        case 0x3001: // 、
        case 0xFF0C: // ，
        case 0xFF1B: // ；
        case 0xFF1A: // ：
        case 0xFF64: // ､
        case 0xFF65: // ･
        case 0x30FB: // ・
        case 0x2010: // ‐
        case 0x2013: // –
        case 0x2014: // —
        case 0x2015: // ―
        case 0x301C: // 〜
        case 0xFF5E: // ～
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true if the code point only counts as a boundary when whitespace follows it.
     *
     * <p>ASCII {@code . , ; :} are ambiguous inside numbers and abbreviations
     * ({@code 3.14}, {@code 1,234}); requiring a following space rejects those without needing a
     * separate digit test. The fullwidth forms cannot use this rule -- CJK sentences carry no
     * trailing space -- and are handled by {@link #isPunctuationRequiringNonDigit} instead.</p>
     *
     * @param cp the code point
     * @return true if a following space is required
     */
    protected boolean isAsciiPunctuationRequiringSpace(final int cp) {
        return cp == '.' || cp == ',' || cp == ';' || cp == ':';
    }

    /**
     * Returns true if the code point only counts as a boundary when the code point immediately
     * following it is not a digit.
     *
     * <p>This is the counterpart of {@link #isAsciiPunctuationRequiringSpace} for the forms that
     * rule cannot cover. The fullwidth stop, comma and colon are decimal points, thousands
     * separators and time separators in ordinary Japanese typography ({@code １．５倍},
     * {@code １，２３４}, {@code １０：３０}), so they cannot simply be trusted -- but they cannot be
     * made to require a following space either, because CJK sentences are not written with one
     * (the JIS {@code ，．} convention would stop working). Hyphens get the same treatment so
     * that ISO dates and version strings ({@code 2026-08-09}, {@code UTF-8}) survive; a hyphen
     * inside a word ({@code well-known}) remains a boundary, which is the ordinary line-break
     * behaviour for hyphenated compounds.</p>
     *
     * @param cp the code point
     * @return true if a following digit disqualifies this code point as a boundary
     */
    protected boolean isPunctuationRequiringNonDigit(final int cp) {
        switch (cp) {
        case '-':
        case 0x2010: // ‐
        case 0xFF0E: // ．
        case 0xFF0C: // ，
        case 0xFF1A: // ：
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true if the code point closes a bracket or a quotation.
     *
     * @param cp the code point
     * @return true if the code point is a closing mark
     */
    protected boolean isClosingBracket(final int cp) {
        switch (cp) {
        case ')':
        case ']':
        case '}':
        case '"':
        case '\'':
        case 0xFF09: // ）
        case 0xFF3D: // ］
        case 0xFF5D: // ｝
        case 0x300D: // 」
        case 0x300F: // 』
        case 0x3011: // 】
        case 0x3015: // 〕
        case 0x300B: // 》
        case 0x3009: // 〉
        case 0x2019: // ’
        case 0x201D: // ”
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true if the code point opens a bracket or a quotation.
     *
     * @param cp the code point
     * @return true if the code point is an opening mark
     */
    protected boolean isOpeningBracket(final int cp) {
        switch (cp) {
        case '(':
        case '[':
        case '{':
        case 0xFF08: // （
        case 0xFF3B: // ［
        case 0xFF5B: // ｛
        case 0x300C: // 「
        case 0x300E: // 『
        case 0x3010: // 【
        case 0x3014: // 〔
        case 0x300A: // 《
        case 0x3008: // 〈
        case 0x2018: // ‘
        case 0x201C: // “
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true if the code point may be swallowed into the preceding chunk when it trails a
     * separator (spaces and closing marks).
     *
     * @param cp the code point
     * @return true if the code point belongs to a trailing run
     */
    protected boolean isSkippableAfterBoundary(final int cp) {
        return isBreakableSpace(cp) || isClosingBracket(cp);
    }

    /**
     * Returns true if two adjacent code points belong to different writing systems.
     *
     * <p>{@code COMMON} / {@code INHERITED} / {@code UNKNOWN} are excluded so that digits,
     * punctuation and emoji do not register as script changes — those are handled by the
     * other tiers.</p>
     *
     * @param before the code point before the candidate boundary
     * @param after the code point at the candidate boundary
     * @return true if the boundary sits on a script change
     */
    protected boolean isScriptBoundary(final int before, final int after) {
        if (before < 0x80 && after < 0x80) {
            // Fast path: ASCII never changes script in a way this tier cares about, and
            // UnicodeScript.of() is a binary search over the Unicode block table -- an order of
            // magnitude more expensive than the switch-based predicates around it.
            return false;
        }
        final Character.UnicodeScript b = Character.UnicodeScript.of(before);
        if (isNeutralScript(b)) {
            return false;
        }
        final Character.UnicodeScript a = Character.UnicodeScript.of(after);
        if (isNeutralScript(a)) {
            return false;
        }
        return b != a;
    }

    private boolean isNeutralScript(final Character.UnicodeScript script) {
        return script == Character.UnicodeScript.COMMON || script == Character.UnicodeScript.INHERITED
                || script == Character.UnicodeScript.UNKNOWN;
    }

    /**
     * Returns true if the code point continues the grapheme cluster started by the preceding
     * code point, so a boundary must not be placed in front of it.
     *
     * <p>Covers combining marks, variation selectors and the zero-width joiner. Regional
     * indicator pairs (flag emoji) are deliberately <em>not</em> covered: splitting is lossless,
     * so a flag merely renders as two letters across the chunk boundary.</p>
     *
     * @param cp the code point
     * @return true if a boundary must not be placed before this code point
     */
    protected boolean isClusterContinuation(final int cp) {
        if (cp == ZWJ) {
            return true;
        }
        if (cp >= 0xFE00 && cp <= 0xFE0F || cp >= 0xE0100 && cp <= 0xE01EF) {
            return true;
        }
        final int type = Character.getType(cp);
        return type == Character.NON_SPACING_MARK || type == Character.COMBINING_SPACING_MARK || type == Character.ENCLOSING_MARK;
    }

    /**
     * Returns true if the code point, when it appears immediately before a candidate boundary,
     * joins that boundary's code point into the same grapheme cluster -- so a boundary must not
     * be placed right after it either.
     *
     * <p>This is the backward-looking counterpart of {@link #isClusterContinuation}, which asks
     * the same question about the code point <em>at</em> the candidate boundary. {@link
     * #adjustToClusterStart} tests both directions, so both must be overridable for a subclass
     * that redefines cluster joining to take full effect: overriding only
     * {@link #isClusterContinuation} would leave this direction on the built-in zero-width-joiner
     * rule.</p>
     *
     * @param cp the code point immediately before the candidate boundary
     * @return true if a boundary must not be placed right after this code point
     */
    protected boolean isClusterJoiner(final int cp) {
        return cp == ZWJ;
    }
}
