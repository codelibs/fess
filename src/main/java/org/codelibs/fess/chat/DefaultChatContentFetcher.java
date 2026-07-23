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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.embedding.EmbeddingClientManager;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

/**
 * Default {@link ChatContentFetcher}. Dispatches per document by
 * {@code content_length}: small documents are fetched in full (existing
 * behavior), large documents use query-relevant highlighted passages so the
 * relevant section reaches the LLM. Highlight snippets are normalized into the
 * {@code content} field so the downstream context builder is unchanged. A large
 * document with no extractable highlight passage falls back to its full content
 * truncated to the threshold; its short digest is never used as a passage.
 */
public class DefaultChatContentFetcher implements ChatContentFetcher {

    private static final Logger logger = LogManager.getLogger(DefaultChatContentFetcher.class);

    /** System property key for the number of top-ranked chunks selected for the RAG chat context. */
    protected static final String CHAT_TOP_K_PROPERTY = "content_chunker.chat.top_k";

    /** Default value of {@link #CHAT_TOP_K_PROPERTY}. */
    protected static final int DEFAULT_CHAT_TOP_K = 3;

    /**
     * Latches on the first time the embedding provider is found unavailable so the operator sees
     * a single WARN instead of one per chat request during a sustained outage. Subsequent
     * occurrences during the same outage degrade to DEBUG.
     *
     * <p>Unlike {@link org.codelibs.fess.filter.StaticThemeFilter}'s {@code warnOnce} convention
     * (a permanent one-time DI-readiness latch that never resets), this latch is reset once the
     * provider becomes available again -- embedding availability can flap over the process
     * lifetime (e.g. the provider comes back up), so a later, distinct outage still surfaces its
     * own WARN instead of silently degrading forever.</p>
     */
    private final AtomicBoolean embeddingUnavailableWarned = new AtomicBoolean(false);

    /** Per-document content resolution strategy. */
    protected enum Strategy {
        /** Fetch the full content (small documents / fallback). */
        FULL,
        /** Fetch query-relevant highlighted passages (large documents). */
        HIGHLIGHT;
    }

    /**
     * Default constructor.
     */
    public DefaultChatContentFetcher() {
        // Default constructor
    }

    @Override
    public List<Map<String, Object>> fetchContent(final ChatContentRequest request) {
        final List<String> docIds = request.getDocIds();
        if (docIds == null || docIds.isEmpty()) {
            return Collections.emptyList();
        }

        final Map<String, Long> sizeByDocId = buildSizeMap(request.getSearchResults());

        final List<String> fullIds = new ArrayList<>();
        final List<String> highlightIds = new ArrayList<>();
        for (final String docId : docIds) {
            if (decideStrategy(docId, sizeByDocId.get(docId), request) == Strategy.HIGHLIGHT) {
                highlightIds.add(docId);
            } else {
                fullIds.add(docId);
            }
        }

        final Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>();
        putByDocId(resultMap, fetchFullContent(fullIds));
        putByDocId(resultMap, fetchHighlightedContent(highlightIds, request.getQuery()));

        // Fallback for large documents that produced no highlight passage: fetch the full content
        // and truncate it to the threshold so the document still contributes a leading excerpt
        // without crowding out the answer context (its short digest is never used as a passage).
        // A chunked document (content_chunk_status=done/chunked) returned by the highlight search
        // is never in this list -- normalizeHighlightedDocs preserves it unconditionally so the
        // chunk-selection pass below can choose its content instead.
        final List<String> missingIds = highlightIds.stream().filter(id -> !resultMap.containsKey(id)).collect(Collectors.toList());
        if (!missingIds.isEmpty()) {
            final long maxLength = getFulltextThreshold();
            final List<Map<String, Object>> fallbackDocs = fetchFullContent(missingIds);
            fallbackDocs.forEach(doc -> {
                if (!isChunkedStatus(doc)) {
                    truncateContent(doc, maxLength);
                }
                // A chunked document is left raw here; applyChunkSelection() truncates it itself below.
            });
            putByDocId(resultMap, fallbackDocs);
        }

        enrichChunkedDocsWithHighlight(resultMap, request.getQuery(), highlightIds);
        applyChunkSelection(resultMap, request.getQuery());

        // Reorder to preserve original docIds (search-score) order.
        final List<Map<String, Object>> ordered = new ArrayList<>();
        for (final String docId : docIds) {
            final Map<String, Object> doc = resultMap.get(docId);
            if (doc != null) {
                ordered.add(doc);
            }
        }
        return ordered;
    }

    /**
     * Selects the most relevant chunk(s) for every chunked document
     * ({@code content_chunk_status=done} or {@code =chunked}) present in
     * {@code resultMap}, replacing its {@code content} field via the
     * semantic-first, keyword-fallback, whole-array-final-fallback chain.
     * Non-chunked documents are left untouched.
     *
     * <p>The chat query is embedded at most once here (not once per document),
     * and only when at least one chunked document actually carries usable chunk
     * vectors -- a vector-less batch (e.g. every doc is {@code status=chunked},
     * written when embedding is configured off) can only select by keyword, so
     * embedding the query for it would be a wasted provider round-trip.</p>
     *
     * @param resultMap the fetched documents, keyed by doc_id (mutated in place)
     * @param query the chat query (may be null/blank)
     */
    protected void applyChunkSelection(final Map<String, Map<String, Object>> resultMap, final String query) {
        // Defense-in-depth: the real gate is that content_chunk_vector/content_chunk_status are
        // never fetched in the first place when chunking is disabled (see buildContentFields() /
        // AnswerHighlightSearchParams#getResponseFields()). This explicit check keeps a stale
        // content_chunk_status left over from when chunking was previously enabled from being
        // used even if that field-omission mechanism is ever refactored.
        if (!isContentChunkerEnabled()) {
            return;
        }
        final List<Map<String, Object>> chunkedDocs =
                resultMap.values().stream().filter(this::isChunkedStatus).collect(Collectors.toList());
        if (chunkedDocs.isEmpty()) {
            return;
        }
        final float[] queryVector = chunkedDocs.stream().anyMatch(this::hasUsableChunkVectors) ? resolveQueryVector(query) : null;
        for (final Map<String, Object> doc : chunkedDocs) {
            selectRelevantChunks(doc, query, queryVector);
        }
    }

    /**
     * Checks whether a document is a chunked document -- one whose {@code content} field holds a
     * chunk array rather than a raw string. Matches both chunk statuses: {@link Constants#DONE}
     * (chunks with vectors) and {@link Constants#CHUNKED} (chunks written without vectors, e.g.
     * when embedding is configured off). Statuses {@code skipped}/{@code fail} -- and an absent
     * status -- mean the {@code content} field is a raw string, so they are not chunked here.
     *
     * @param doc the document map
     * @return true if the document carries a chunk-array {@code content}
     */
    protected boolean isChunkedStatus(final Map<String, Object> doc) {
        final Object status = doc.get(Constants.CONTENT_CHUNK_STATUS_FIELD);
        return Constants.DONE.equals(status) || Constants.CHUNKED.equals(status);
    }

    /**
     * Checks whether a chunked document carries at least one chunk vector usable for semantic
     * selection (the same entry shape {@link #selectBySemanticSimilarity} consumes). False for a
     * {@code status=chunked} document (no vector field) and, defensively, for a {@code status=done}
     * document whose vector field is missing or malformed.
     *
     * @param doc the document map
     * @return true if at least one usable chunk vector is present
     */
    protected boolean hasUsableChunkVectors(final Map<String, Object> doc) {
        final Object vectorFieldValue = doc.get(Constants.CONTENT_CHUNK_VECTOR_FIELD);
        if (!(vectorFieldValue instanceof List<?>)) {
            return false;
        }
        for (final Object entry : (List<?>) vectorFieldValue) {
            if (toFloatArray(extractVectorSubfield(entry)) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fetches the highlight snippet for chunked documents that cannot use semantic selection --
     * no usable chunk vectors (typically {@code content_chunk_status=chunked}, written when
     * embedding is configured off) -- and have no {@code hl_content} yet, so
     * {@link #selectByHighlightMatch} still has a snippet to select chunks with on the FULL
     * (small-document) path. One additional doc_id-restricted highlight search covers all such
     * documents; only the raw highlight field is copied onto the already-fetched document maps --
     * their {@code content} (the chunk array) is never replaced here.
     *
     * <p>Documents in {@code alreadyHighlightedIds} had their highlight opportunity in the primary
     * highlight fetch (a chunked document returned there is preserved with its {@code hl_content}
     * by {@link #normalizeHighlightedDocs}; one missing from its results entirely would miss an
     * identical re-query too), so they are excluded. Skipped entirely for a blank query (nothing
     * to highlight against -- the summary path falls through to the whole-array truncate) and when
     * content chunking is disabled (zero extra cost for non-chunking deployments).</p>
     *
     * @param resultMap the fetched documents, keyed by doc_id (mutated in place)
     * @param query the chat query (may be null/blank)
     * @param alreadyHighlightedIds doc ids already covered by the primary highlight fetch
     */
    protected void enrichChunkedDocsWithHighlight(final Map<String, Map<String, Object>> resultMap, final String query,
            final List<String> alreadyHighlightedIds) {
        if (StringUtil.isBlank(query) || !isContentChunkerEnabled()) {
            return;
        }
        final String highlightField = getHighlightContentField();
        final List<String> targetIds = resultMap.entrySet().stream().filter(e -> {
            final Map<String, Object> doc = e.getValue();
            final Object snippet = doc.get(highlightField);
            return isChunkedStatus(doc) && doc.get("content") instanceof List<?> && !hasUsableChunkVectors(doc)
                    && !(snippet instanceof String && StringUtil.isNotBlank((String) snippet))
                    && !alreadyHighlightedIds.contains(e.getKey());
        }).map(Map.Entry::getKey).collect(Collectors.toList());
        if (targetIds.isEmpty()) {
            return;
        }
        for (final Map<String, Object> hlDoc : fetchHighlightedContent(targetIds, query)) {
            final Object docId = hlDoc.get("doc_id");
            final Object snippet = hlDoc.get(highlightField);
            if (docId instanceof String && snippet instanceof String && StringUtil.isNotBlank((String) snippet)) {
                final Map<String, Object> target = resultMap.get(docId);
                if (target != null) {
                    target.put(highlightField, snippet);
                }
            }
        }
    }

    /**
     * Embeds the chat query for semantic chunk selection, tolerating any
     * failure (embedding client unavailable, provider error, timeout) by
     * falling back to {@code null} so callers fall through to the
     * keyword/full-content fallback chain.
     *
     * @param query the chat query (may be null/blank)
     * @return the query embedding vector, or null if unavailable/blank/failed
     */
    protected float[] resolveQueryVector(final String query) {
        if (StringUtil.isBlank(query)) {
            return null;
        }
        try {
            final EmbeddingClientManager manager = getEmbeddingClientManager();
            if (!manager.available()) {
                warnEmbeddingUnavailableOnce();
                return null;
            }
            embeddingUnavailableWarned.set(false);
            return manager.embedQuery(query);
        } catch (final Exception e) {
            logger.warn("[RAG] Failed to embed chat query for chunk selection; falling back to keyword/full content.", e);
            return null;
        }
    }

    /**
     * Logs a WARN the first time the embedding provider is found unavailable in a given outage,
     * then degrades subsequent occurrences during the same outage to DEBUG so a persistently
     * unavailable provider does not spam a WARN on every chat request.
     */
    private void warnEmbeddingUnavailableOnce() {
        if (embeddingUnavailableWarned.compareAndSet(false, true)) {
            logger.warn("[RAG] Embedding client unavailable; falling back to keyword/full content for chunk selection "
                    + "until availability is restored.");
        } else if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Embedding client still unavailable; falling back to keyword/full content for chunk selection.");
        }
    }

    /**
     * Gets the {@link EmbeddingClientManager}. Overridable seam for tests.
     *
     * @return the embedding client manager
     */
    protected EmbeddingClientManager getEmbeddingClientManager() {
        return ComponentUtil.getComponent(EmbeddingClientManager.class);
    }

    /**
     * Selects the most relevant chunk(s) of one chunked document for the LLM
     * context and truncates the result to the fulltext threshold as a final
     * context-budget guard.
     *
     * <p>Fallback chain: (1) semantic similarity against {@code queryVector}, if
     * available; (2) keyword/highlight-fragment matching against the query, if
     * there is a query to match; (3) the whole chunk array (existing array-safe
     * {@link #truncateContent} behavior). A blank query explicitly skips step 2
     * (there is no query to highlight against) rather than relying on step 2 to
     * incidentally find nothing.</p>
     *
     * @param doc the document map (mutated in place); must have
     *            {@code content_chunk_status=done} or {@code =chunked}
     * @param query the chat query (may be null/blank)
     * @param queryVector the query embedding vector, or null if unavailable
     */
    protected void selectRelevantChunks(final Map<String, Object> doc, final String query, final float[] queryVector) {
        final List<String> chunks = toStringChunks(doc.get("content"));
        if (!chunks.isEmpty()) {
            String selected = null;
            if (queryVector != null) {
                selected = selectBySemanticSimilarity(chunks, doc, queryVector);
            }
            if (selected == null && StringUtil.isNotBlank(query)) {
                selected = selectByHighlightMatch(chunks, doc);
            }
            if (selected != null) {
                doc.put("content", selected);
            }
            // else: leave "content" as the original chunk List; truncateContent below joins+truncates
            // it as the final fallback (step 3).
        }
        truncateContent(doc, getFulltextThreshold());
    }

    /**
     * Ranks a chunked document's chunks by cosine similarity to {@code queryVector}
     * and joins the top {@link #getChatTopK()} chunks (fewer if the document has
     * fewer chunks), in their original array order.
     *
     * @param chunks the document's chunk texts
     * @param doc the document map (read-only here)
     * @param queryVector the query embedding vector
     * @return the joined selected chunk text, or null if no usable chunk vectors were found
     */
    protected String selectBySemanticSimilarity(final List<String> chunks, final Map<String, Object> doc, final float[] queryVector) {
        final Object vectorFieldValue = doc.get(Constants.CONTENT_CHUNK_VECTOR_FIELD);
        if (!(vectorFieldValue instanceof List<?>)) {
            return null;
        }
        final List<?> vectorEntries = (List<?>) vectorFieldValue;
        final int n = Math.min(chunks.size(), vectorEntries.size());
        final List<Map.Entry<Integer, Double>> scored = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            final float[] chunkVector = toFloatArray(extractVectorSubfield(vectorEntries.get(i)));
            if (chunkVector != null) {
                scored.add(Map.entry(i, cosineSimilarity(queryVector, chunkVector)));
            }
        }
        if (scored.isEmpty()) {
            return null;
        }
        scored.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        final int topK = Math.max(1, getChatTopK());
        final Set<Integer> selectedIndexes =
                scored.stream().limit(topK).map(Map.Entry::getKey).collect(Collectors.toCollection(TreeSet::new));
        if (logger.isDebugEnabled()) {
            logger.debug("[RAG] Selected chunks by semantic similarity. docId={}, selectedIndexes={}, scores={}",
                    doc.get(ComponentUtil.getFessConfig().getIndexFieldDocId()), selectedIndexes,
                    scored.stream()
                            .limit(topK)
                            .map(e -> e.getKey() + ":" + String.format(Locale.ROOT, "%.4f", e.getValue()))
                            .collect(Collectors.joining(",")));
        }
        return selectedIndexes.stream().map(chunks::get).collect(Collectors.joining("\n\n"));
    }

    /**
     * Extracts the {@link ChunkVectorHelper#VECTOR_SUBFIELD} value from one
     * {@code content_chunk_vector} entry.
     *
     * @param entry one {@code content_chunk_vector} list entry
     * @return the raw vector sub-field value, or null if {@code entry} is not a map
     */
    protected Object extractVectorSubfield(final Object entry) {
        if (entry instanceof Map<?, ?>) {
            return ((Map<?, ?>) entry).get(ChunkVectorHelper.VECTOR_SUBFIELD);
        }
        return null;
    }

    /**
     * Converts a vector value to a {@code float[]}. Handles both a {@code float[]}
     * (e.g. a directly-injected test double) and a {@code List} of {@link Number}
     * (the shape a {@code knn_vector} nested field actually deserializes to from
     * an OpenSearch {@code _source}, e.g. {@code List<Double>}).
     *
     * @param value the raw vector sub-field value
     * @return the vector as a float[], or null if the value is not a usable vector shape
     */
    protected float[] toFloatArray(final Object value) {
        if (value instanceof float[]) {
            return (float[]) value;
        }
        if (value instanceof List<?>) {
            final List<?> list = (List<?>) value;
            final float[] result = new float[list.size()];
            for (int i = 0; i < list.size(); i++) {
                final Object element = list.get(i);
                if (!(element instanceof Number)) {
                    return null;
                }
                result[i] = ((Number) element).floatValue();
            }
            return result;
        }
        return null;
    }

    /**
     * Computes the cosine similarity between two vectors. Returns {@code 0.0}
     * (rather than a numerically-valid-looking but semantically meaningless
     * score) if the two vectors have different lengths -- a stored chunk
     * vector is never comparable to a query vector embedded by a different
     * (or differently-configured) model, so it must score like any other
     * non-matching chunk rather than being silently truncated to the shorter
     * length. Also returns {@code 0.0} (rather than dividing by zero /
     * producing {@code NaN}) if either vector has a zero L2 norm, and also
     * {@code 0.0} if the result would otherwise be {@code NaN} -- e.g. a
     * corrupted stored embedding containing a {@code NaN}/{@code Infinity}
     * component. A raw {@code NaN} score must never reach the caller's
     * {@code Double.compare}-based top-K sort: {@code NaN} compares
     * inconsistently there, which could let a corrupted-vector chunk always
     * win (or always lose) ranking instead of being scored like any other
     * non-matching chunk.
     *
     * @param a the first vector
     * @param b the second vector
     * @return the cosine similarity in [-1.0, 1.0], or 0.0 for a length mismatch, a zero-norm
     *         vector, or a NaN result
     */
    private static double cosineSimilarity(final float[] a, final float[] b) {
        if (a.length != b.length) {
            return 0.0;
        }
        final int n = a.length;
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < n; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        final double similarity = dot / (Math.sqrt(normA) * Math.sqrt(normB));
        return Double.isNaN(similarity) ? 0.0 : similarity;
    }

    /**
     * Gets the number of top-ranked chunks selected for the RAG chat context.
     *
     * @return the value of {@code content_chunker.chat.top_k} (default 3)
     */
    protected int getChatTopK() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(CHAT_TOP_K_PROPERTY, null);
        if (value != null) {
            try {
                return Integer.parseInt(value.trim());
            } catch (final NumberFormatException e) {
                logger.warn("[RAG] Invalid integer for {}: {}", CHAT_TOP_K_PROPERTY, value);
            }
        }
        return DEFAULT_CHAT_TOP_K;
    }

    /**
     * Selects chunks whose text contains a highlight fragment matched against
     * the query, joined in original array order. This is the keyword-based
     * fallback used when semantic selection is unavailable or found no usable
     * vectors.
     *
     * @param chunks the document's chunk texts
     * @param doc the document map; must carry the raw highlight field (i.e. not
     *            yet normalized into {@code content})
     * @return the joined matched chunk text, or null if no fragment matched any chunk
     */
    protected String selectByHighlightMatch(final List<String> chunks, final Map<String, Object> doc) {
        final Object snippetValue = doc.get(getHighlightContentField());
        if (!(snippetValue instanceof String) || StringUtil.isBlank((String) snippetValue)) {
            return null;
        }
        final Set<Integer> matched = new TreeSet<>();
        for (final String rawFragment : HIGHLIGHT_FRAGMENT_SEPARATOR_PATTERN.split((String) snippetValue)) {
            final String fragment = stripHighlightTags(rawFragment).trim();
            if (StringUtil.isBlank(fragment)) {
                continue;
            }
            for (int i = 0; i < chunks.size(); i++) {
                if (chunks.get(i).contains(fragment)) {
                    matched.add(i);
                }
            }
        }
        if (matched.isEmpty()) {
            return null;
        }
        return matched.stream().map(chunks::get).collect(Collectors.joining("\n\n"));
    }

    /**
     * Matches the separator {@code ViewHelper#createHighlightText} joins multiple highlight
     * fragments with (its private {@code ELLIPSIS} constant, always exactly 3 dots) -- as a run
     * of <b>3 or more</b> consecutive dots, not an exact 3-dot literal.
     *
     * <p>When a fragment's own text ends with a sentence-terminating {@code .} (the ASCII entry
     * of {@code FessConfig#getCrawlerDocumentFullstopChars}), joining it with the 3-dot separator
     * produces a run of 4+ consecutive dots. Splitting on an exact 3-dot literal only consumes the
     * first 3 of those, leaving a stray leading {@code .} glued onto the next recovered fragment;
     * that stray dot then breaks the fragment's {@code contains()} match against its chunk text,
     * silently dropping the chunk from selection even though OpenSearch's highlighter matched it.
     * A genuine inter-fragment boundary is always a run of at least 3 dots (the separator itself,
     * plus any trailing/leading dots contributed by the adjoining fragments), so {@code \.{3,}}
     * matches every real boundary -- greedily consuming the whole run, including any leading dots
     * of the next fragment -- while never matching inside a fragment's own legitimate short dot
     * sequences (e.g. {@code "1..5"}, {@code "Loading.."}), which a looser {@code \.{2,}} would
     * incorrectly split on. The other three configured fullstop characters ({@code u06d4}
     * Arabic full stop, {@code u2e3c} stenographic full stop, {@code u3002} ideographic full
     * stop) are not dots, so they never collide with the separator and need no special handling.
     */
    private static final Pattern HIGHLIGHT_FRAGMENT_SEPARATOR_PATTERN = Pattern.compile("\\.{3,}");

    /**
     * Pre-tag {@link AnswerHighlightSearchParams#getHighlightInfo()} configures for
     * this query path -- confirmed empty (not {@code <em>}/{@code </em>}), so
     * stripping is a no-op today; kept keyed off the actual configured value
     * rather than an assumed tag so it stays correct if that ever changes.
     */
    private static final String HIGHLIGHT_PRE_TAG = StringUtil.EMPTY;

    /** Post-tag counterpart of {@link #HIGHLIGHT_PRE_TAG}. */
    private static final String HIGHLIGHT_POST_TAG = StringUtil.EMPTY;

    /**
     * Strips the configured highlight pre/post tags from a fragment.
     *
     * @param fragment the raw highlight fragment
     * @return the fragment with highlight tags stripped (a no-op today; see {@link #HIGHLIGHT_PRE_TAG})
     */
    protected String stripHighlightTags(final String fragment) {
        String result = fragment;
        if (StringUtil.isNotBlank(HIGHLIGHT_PRE_TAG)) {
            result = result.replace(HIGHLIGHT_PRE_TAG, "");
        }
        if (StringUtil.isNotBlank(HIGHLIGHT_POST_TAG)) {
            result = result.replace(HIGHLIGHT_POST_TAG, "");
        }
        return result;
    }

    /**
     * Converts a {@code content} field value into a chunk-text list. Only a
     * {@code List} value (the shape a chunked document's {@code content}
     * actually has) is usable; anything else (should not occur for a
     * chunked-status document) yields an empty list so
     * callers leave the document untouched.
     *
     * @param value the raw {@code content} field value
     * @return the chunk texts, or an empty list if not a List
     */
    protected List<String> toStringChunks(final Object value) {
        if (!(value instanceof List<?>)) {
            return Collections.emptyList();
        }
        return ((List<?>) value).stream().map(String::valueOf).collect(Collectors.toList());
    }

    /**
     * Checks if content chunking is enabled, delegating to
     * {@link ChunkVectorHelper#isContentChunkerEnabled()} so the read path shares the
     * ingestion pipeline's single config lookup instead of re-reading the property.
     *
     * @return the value of {@code content_chunker.enabled} (default false)
     */
    protected boolean isContentChunkerEnabled() {
        return ComponentUtil.getComponent(ChunkVectorHelper.class).isContentChunkerEnabled();
    }

    /**
     * Decides the fetch strategy for a document.
     *
     * @param docId the document id
     * @param contentLength the indexed content length (null if unknown)
     * @param request the fetch request
     * @return {@link Strategy#HIGHLIGHT} for large documents, otherwise {@link Strategy#FULL}
     */
    protected Strategy decideStrategy(final String docId, final Long contentLength, final ChatContentRequest request) {
        if (StringUtil.isBlank(request.getQuery())) {
            return Strategy.FULL; // no query to highlight with
        }
        if (contentLength == null) {
            return Strategy.FULL; // unknown size (e.g. summary path) -> full, no regression
        }
        return contentLength.longValue() > getFulltextThreshold() ? Strategy.HIGHLIGHT : Strategy.FULL;
    }

    /**
     * Returns the content_length threshold above which highlighted passages are used.
     *
     * @return the threshold in characters
     */
    protected long getFulltextThreshold() {
        return ComponentUtil.getFessConfig().getRagChatContentFulltextMaxLengthAsInteger().longValue();
    }

    /**
     * Truncates the {@code content} field of a document to at most {@code maxLength}
     * characters, keeping the leading portion. Used as the fallback for a large
     * document that produced no highlight passage so it still contributes a leading
     * excerpt without crowding out the answer context.
     *
     * <p>A {@code List} value (chunked document content) is first joined into a
     * single string (chunks separated by a blank line, so boundaries read as
     * paragraph breaks) and then truncated with the same length check as the
     * {@code String} case, so chunked documents are still bounded by this
     * context-window budget.</p>
     *
     * @param doc the document map (modified in place)
     * @param maxLength the maximum content length in characters
     */
    protected void truncateContent(final Map<String, Object> doc, final long maxLength) {
        final Object value = doc.get("content");
        String text = null;
        if (value instanceof String) {
            text = (String) value;
        } else if (value instanceof List<?>) {
            text = ((List<?>) value).stream().map(String::valueOf).collect(Collectors.joining("\n\n"));
        }
        if (text != null && text.length() > maxLength) {
            doc.put("content", text.substring(0, (int) maxLength));
        }
    }

    /**
     * Builds a doc_id -&gt; content_length map from the search-phase results.
     *
     * @param searchResults the search-phase result maps (may be null)
     * @return the size map (empty when searchResults is null/empty)
     */
    protected Map<String, Long> buildSizeMap(final List<Map<String, Object>> searchResults) {
        final Map<String, Long> map = new LinkedHashMap<>();
        if (searchResults == null) {
            return map;
        }
        for (final Map<String, Object> doc : searchResults) {
            final String docId = (String) doc.get("doc_id");
            if (docId != null) {
                map.put(docId, toLong(doc.get("content_length")));
            }
        }
        return map;
    }

    /**
     * Converts a value to Long, tolerating Number and numeric String.
     *
     * @param value the value
     * @return the Long value, or null if not convertible
     */
    protected Long toLong(final Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String && StringUtil.isNotBlank((String) value)) {
            try {
                return Long.valueOf((String) value);
            } catch (final NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Fetches full content for the given document ids via doc_id lookup (no highlighting).
     *
     * @param docIds the document ids
     * @return document maps (unordered)
     */
    protected List<Map<String, Object>> fetchFullContent(final List<String> docIds) {
        if (docIds.isEmpty()) {
            return Collections.emptyList();
        }
        final long startTime = System.currentTimeMillis();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String[] fields = buildContentFields(fessConfig);
        try {
            return ComponentUtil.getSearchHelper()
                    .getDocumentListByDocIds(docIds.toArray(new String[0]), fields, OptionalThing.empty(),
                            SearchRequestParams.SearchRequestType.JSON);
        } catch (final Exception e) {
            logger.warn("[RAG] Failed to fetch full content for docIds={}. error={}, elapsedTime={}ms", docIds, e.getMessage(),
                    System.currentTimeMillis() - startTime);
            return Collections.emptyList();
        }
    }

    /**
     * Builds the field list for {@link #fetchFullContent}, appending
     * {@code content_chunk_vector}/{@code content_chunk_status} to the
     * operator-configured {@code rag.chat.content.fields} so a chunked
     * document's chunk-selection data is always retrievable, without
     * requiring the operator to know to add them. A no-op appendage when
     * content chunking is disabled, so a non-chunking deployment's field list
     * is completely unchanged.
     *
     * @param fessConfig the fess config
     * @return the field list to fetch
     */
    protected String[] buildContentFields(final FessConfig fessConfig) {
        final String[] configured = fessConfig.getRagChatContentFields().split(",");
        if (!isContentChunkerEnabled()) {
            return configured;
        }
        final Set<String> fieldSet = new LinkedHashSet<>(Arrays.asList(configured));
        fieldSet.add(Constants.CONTENT_CHUNK_VECTOR_FIELD);
        fieldSet.add(Constants.CONTENT_CHUNK_STATUS_FIELD);
        return fieldSet.toArray(new String[0]);
    }

    /**
     * Fetches query-relevant highlighted passages for large documents via a
     * doc_id-restricted highlighted search, normalizing the snippet into the
     * {@code content} field. Returns empty on failure (callers fall back to full).
     *
     * @param docIds the large-document ids
     * @param query the highlight query
     * @return document maps with {@code content} set to the highlighted snippet
     */
    protected List<Map<String, Object>> fetchHighlightedContent(final List<String> docIds, final String query) {
        if (docIds.isEmpty()) {
            return Collections.emptyList();
        }
        final long startTime = System.currentTimeMillis();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        try {
            final String docIdQuery =
                    docIds.stream().map(id -> fessConfig.getIndexFieldDocId() + ":" + id).collect(Collectors.joining(" OR ", "(", ")"));
            final SearchRenderData data = new SearchRenderData();
            final AnswerHighlightSearchParams params =
                    new AnswerHighlightSearchParams(query, docIds.size(), fessConfig, new String[] { docIdQuery });
            ComponentUtil.getSearchHelper().search(params, data, OptionalThing.empty());

            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> docs = (List<Map<String, Object>>) data.getDocumentItems();
            if (docs == null) {
                return Collections.emptyList();
            }
            return normalizeHighlightedDocs(docs);
        } catch (final Exception e) {
            logger.warn("[RAG] Failed to fetch highlighted content for docIds={}. Falling back to full. error={}, elapsedTime={}ms", docIds,
                    e.getMessage(), System.currentTimeMillis() - startTime);
            return Collections.emptyList();
        }
    }

    /**
     * Normalizes highlighted search results: copies the highlight passage into the
     * {@code content} field, and drops documents that produced no highlight passage
     * so the caller can fall back to their truncated full content.
     *
     * <p>The passage is read from the raw highlight field ({@code hl_content}), not
     * from {@code content_description}. {@code content_description} is built by
     * {@code ViewHelper#getContentDescription} from {@code hl_content,digest} and
     * therefore falls back to the short crawl-time {@code digest} when there is no
     * highlight match; relying on it would mask the "no passage" case and send the
     * digest to the LLM instead of the truncated full-content fallback. Reading the
     * highlight field directly keeps that case detectable.</p>
     *
     * @param docs the highlighted search result maps
     * @return maps that produced a highlight passage only, each with {@code content} set to it
     */
    protected List<Map<String, Object>> normalizeHighlightedDocs(final List<Map<String, Object>> docs) {
        final String highlightField = getHighlightContentField();
        final List<Map<String, Object>> normalized = new ArrayList<>();
        for (final Map<String, Object> doc : docs) {
            if (isChunkedStatus(doc)) {
                // Chunked document: preserve its raw content/vector/highlight fields untouched --
                // never overwrite "content" with the snippet, never drop on a highlight miss. The
                // chunk-selection pass in fetchContent() decides its final content via the
                // semantic/keyword/full fallback chain instead of this FULL-vs-HIGHLIGHT dispatch.
                normalized.add(doc);
                continue;
            }
            final String snippet = (String) doc.get(highlightField);
            if (StringUtil.isNotBlank(snippet)) {
                doc.put("content", snippet); // normalize: buildContext is content-first
                normalized.add(doc);
            }
            // no highlight passage -> drop so the caller falls back to truncated full content
        }
        return normalized;
    }

    /**
     * Returns the document-map key that holds the highlight passage for the content
     * field (the highlight prefix joined with the content field name, e.g.
     * {@code hl_content}).
     *
     * @return the highlight content field key
     */
    protected String getHighlightContentField() {
        return ComponentUtil.getQueryHelper().getHighlightPrefix() + ComponentUtil.getFessConfig().getIndexFieldContent();
    }

    private void putByDocId(final Map<String, Map<String, Object>> target, final List<Map<String, Object>> docs) {
        for (final Map<String, Object> doc : docs) {
            final String docId = (String) doc.get("doc_id");
            if (docId != null) {
                target.put(docId, doc);
            }
        }
    }

    /**
     * Chat search params restricted to specific doc ids, using the answer-specific
     * highlight settings ({@code rag.chat.answer.highlight.*}) instead of the
     * display-oriented {@code rag.chat.highlight.*}.
     */
    protected static class AnswerHighlightSearchParams extends ChatClient.ChatSearchRequestParams {
        private final int fragmentSize;
        private final int numOfFragments;

        /**
         * Creates answer-highlight search params.
         *
         * @param query the highlight query
         * @param pageSize the page size (number of large documents)
         * @param fessConfig the configuration
         * @param extraQueries the doc_id restriction clause
         */
        public AnswerHighlightSearchParams(final String query, final int pageSize, final FessConfig fessConfig,
                final String[] extraQueries) {
            super(query, pageSize, fessConfig, Collections.emptyMap(), extraQueries);
            // Resolve once; getHighlightInfo() may be called repeatedly during search execution.
            this.fragmentSize = fessConfig.getRagChatAnswerHighlightFragmentSizeAsInteger();
            this.numOfFragments = fessConfig.getRagChatAnswerHighlightNumberOfFragmentsAsInteger();
        }

        @Override
        public HighlightInfo getHighlightInfo() {
            return new HighlightInfo().fragmentSize(fragmentSize)
                    .numOfFragments(numOfFragments)
                    .preTags(StringUtil.EMPTY)
                    .postTags(StringUtil.EMPTY);
        }

        /**
         * Appends {@code content}/{@code content_chunk_vector}/{@code content_chunk_status}
         * to the standard response fields so a chunked large document's chunk-selection
         * data is retrievable from this highlight search too. A no-op appendage when
         * content chunking is disabled, so a non-chunking deployment's highlight-search
         * {@code _source} filter (and its fetch cost) is completely unchanged -- otherwise
         * this would needlessly pull full content for every large document, defeating the
         * highlight path's purpose. Returns a new array; never mutates the shared
         * {@link org.codelibs.fess.query.QueryFieldConfig} array returned by the super call.
         *
         * <p><b>Known tradeoff:</b> this is an all-or-nothing toggle keyed on
         * {@code content_chunker.enabled}, not on whether a given batch of large documents
         * is actually chunked. In a mixed corpus (chunking enabled, but only some documents
         * have been chunk-processed), every large document in the batch pays the cost of
         * fetching full {@code content} here, even the non-chunked ones whose
         * {@code content_chunk_status} will come back unset -- for those,
         * {@link #normalizeHighlightedDocs} discards the fetched {@code content} in favor of
         * the {@code hl_content} snippet, so the fetch was wasted. Avoiding this would require
         * knowing which docIds are chunked <em>before</em> issuing this search (e.g. a
         * cheap {@code content_chunk_status}-only pre-check, or restructuring into a two-phase
         * fetch), which isn't done today: {@code docIds} here come from {@link #decideStrategy}
         * sizing alone, with no chunk-status lookup beforehand. This is accepted as a
         * documented tradeoff rather than a bug -- it only over-fetches for large, not-yet-chunked
         * documents, and only while content chunking is enabled.</p>
         */
        @Override
        public String[] getResponseFields() {
            final String[] base = super.getResponseFields();
            if (!ComponentUtil.getComponent(ChunkVectorHelper.class).isContentChunkerEnabled()) {
                return base;
            }
            final Set<String> fieldSet = new LinkedHashSet<>(Arrays.asList(base));
            fieldSet.add(ComponentUtil.getFessConfig().getIndexFieldContent());
            fieldSet.add(Constants.CONTENT_CHUNK_VECTOR_FIELD);
            fieldSet.add(Constants.CONTENT_CHUNK_STATUS_FIELD);
            return fieldSet.toArray(new String[0]);
        }
    }
}
