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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;

/**
 * Default {@link ChatContentFetcher}. Dispatches per document by
 * {@code content_length}: small documents are fetched in full (existing
 * behavior), large documents use query-relevant highlighted passages so the
 * relevant section reaches the LLM. Highlight snippets are normalized into the
 * {@code content} field so the downstream context builder is unchanged.
 */
public class DefaultChatContentFetcher implements ChatContentFetcher {

    private static final Logger logger = LogManager.getLogger(DefaultChatContentFetcher.class);

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

        // Reconcile: any requested doc still missing (e.g. highlight had no match) -> full fetch fallback.
        final List<String> missingIds = docIds.stream().filter(id -> !resultMap.containsKey(id)).collect(Collectors.toList());
        if (!missingIds.isEmpty()) {
            putByDocId(resultMap, fetchFullContent(missingIds));
        }

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
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return Long.parseLong(fessConfig.getOrDefault("rag.chat.content.fulltext.max.length", "3000"));
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
        final String[] fields = fessConfig.getRagChatContentFields().split(",");
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
     * Normalizes highlighted search results: copies the highlight snippet into the
     * {@code content} field, and drops documents whose snippet is blank so the
     * caller's reconciliation step re-fetches them with full content (the highlight
     * search response does not include the full content field).
     *
     * @param docs the highlighted search result maps
     * @return maps with non-blank snippets only, each with {@code content} set to the snippet
     */
    protected List<Map<String, Object>> normalizeHighlightedDocs(final List<Map<String, Object>> docs) {
        final List<Map<String, Object>> normalized = new ArrayList<>();
        for (final Map<String, Object> doc : docs) {
            final String snippet = (String) doc.get("content_description");
            if (StringUtil.isNotBlank(snippet)) {
                doc.put("content", snippet); // normalize: buildContext is content-first
                normalized.add(doc);
            }
            // blank snippet -> omit so reconciliation re-fetches full content
        }
        return normalized;
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
        private final FessConfig fessConfig;

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
            this.fessConfig = fessConfig;
        }

        @Override
        public HighlightInfo getHighlightInfo() {
            return new HighlightInfo()
                    .fragmentSize(Integer.parseInt(fessConfig.getOrDefault("rag.chat.answer.highlight.fragment.size", "1000")))
                    .numOfFragments(Integer.parseInt(fessConfig.getOrDefault("rag.chat.answer.highlight.number.of.fragments", "5")))
                    .preTags(StringUtil.EMPTY)
                    .postTags(StringUtil.EMPTY);
        }
    }
}
