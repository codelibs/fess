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
package org.codelibs.fess.chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits the content of a long document into parts that each fit a character budget, so every part
 * can be summarized by a separate LLM request.
 *
 * <p>A chunked document keeps its chunk boundaries: consecutive chunks are packed into a part, and
 * only a chunk longer than the budget is split further. A plain text is cut at the last paragraph
 * break, else the last sentence end, else the last whitespace before the budget, in the second half
 * of the part; without any such boundary there it is cut at the budget.</p>
 */
public final class DocumentPartSplitter {

    /** The separator placed between chunks when they are joined into one text. */
    public static final String CHUNK_SEPARATOR = "\n\n";

    /** Sentence-ending characters that end a sentence wherever they appear (CJK punctuation has no following space). */
    private static final String SENTENCE_END_CHARS = "。．！？";

    /** Sentence-ending characters that end a sentence only when whitespace follows. */
    private static final String SPACED_SENTENCE_END_CHARS = ".!?";

    private DocumentPartSplitter() {
        // utility class
    }

    /**
     * Packs consecutive chunks into parts of at most {@code maxChars} characters, joined with
     * {@link #CHUNK_SEPARATOR}. A chunk longer than {@code maxChars} is split with
     * {@link #splitText(String, int)} and its pieces form parts of their own. Blank chunks are skipped.
     *
     * @param chunks the chunk texts, in document order
     * @param maxChars the maximum length of a part; at least 1
     * @return the parts in document order; empty if there is no text
     */
    public static List<String> splitChunks(final List<String> chunks, final int maxChars) {
        checkMaxChars(maxChars);
        final List<String> parts = new ArrayList<>();
        final StringBuilder current = new StringBuilder();
        for (final String chunk : chunks) {
            if (chunk == null || chunk.isBlank()) {
                continue;
            }
            if (chunk.length() > maxChars) {
                flush(parts, current);
                parts.addAll(splitText(chunk, maxChars));
                continue;
            }
            if (current.length() > 0 && current.length() + CHUNK_SEPARATOR.length() + chunk.length() > maxChars) {
                flush(parts, current);
            }
            if (current.length() > 0) {
                current.append(CHUNK_SEPARATOR);
            }
            current.append(chunk);
        }
        flush(parts, current);
        return parts;
    }

    /**
     * Splits a text into parts of at most {@code maxChars} characters. Whitespace around a cut is
     * dropped, so a text that fits is returned as one part without its surrounding whitespace.
     *
     * @param text the text
     * @param maxChars the maximum length of a part; at least 1
     * @return the parts in document order; empty if the text is null or blank
     */
    public static List<String> splitText(final String text, final int maxChars) {
        checkMaxChars(maxChars);
        final List<String> parts = new ArrayList<>();
        if (text == null) {
            return parts;
        }
        final int length = text.length();
        int start = skipWhitespace(text, 0);
        while (start < length) {
            if (length - start <= maxChars) {
                addPart(parts, text.substring(start));
                break;
            }
            final int end = findCut(text, start, maxChars);
            addPart(parts, text.substring(start, end));
            start = skipWhitespace(text, end);
        }
        return parts;
    }

    private static void checkMaxChars(final int maxChars) {
        if (maxChars < 1) {
            throw new IllegalArgumentException("maxChars must be positive: " + maxChars);
        }
    }

    private static void flush(final List<String> parts, final StringBuilder current) {
        if (current.length() > 0) {
            parts.add(current.toString());
            current.setLength(0);
        }
    }

    private static void addPart(final List<String> parts, final String part) {
        final String stripped = part.stripTrailing();
        if (!stripped.isEmpty()) {
            parts.add(stripped);
        }
    }

    private static int skipWhitespace(final String text, final int from) {
        int i = from;
        while (i < text.length() && Character.isWhitespace(text.charAt(i))) {
            i++;
        }
        return i;
    }

    /**
     * Finds where to end the part starting at {@code start}; the text beyond {@code start + maxChars}
     * does not fit, so the result is in {@code (start, start + maxChars]}.
     */
    private static int findCut(final String text, final int start, final int maxChars) {
        final int limit = start + maxChars;
        // Do not cut before the middle of the part: a boundary near its start would waste most of the budget.
        final int minEnd = start + Math.max(1, maxChars / 2);
        for (int end = limit; end >= minEnd; end--) {
            if (text.charAt(end - 1) == '\n' || text.charAt(end) == '\n') {
                return end;
            }
        }
        for (int end = limit; end >= minEnd; end--) {
            if (isSentenceEnd(text, end - 1)) {
                return end;
            }
        }
        for (int end = limit; end >= minEnd; end--) {
            if (Character.isWhitespace(text.charAt(end - 1)) || Character.isWhitespace(text.charAt(end))) {
                return end;
            }
        }
        // Hard cut, but never between the two halves of a surrogate pair.
        if (limit - 1 > start && Character.isHighSurrogate(text.charAt(limit - 1)) && Character.isLowSurrogate(text.charAt(limit))) {
            return limit - 1;
        }
        return limit;
    }

    /** {@code index + 1} is always a valid index here: a cut is only searched when text remains beyond the part. */
    private static boolean isSentenceEnd(final String text, final int index) {
        final char c = text.charAt(index);
        if (SENTENCE_END_CHARS.indexOf(c) >= 0) {
            return true;
        }
        return SPACED_SENTENCE_END_CHARS.indexOf(c) >= 0 && Character.isWhitespace(text.charAt(index + 1));
    }
}
