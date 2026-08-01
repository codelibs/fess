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

import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.chat.ChatClient.ChatSearchResult;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.helper.ChatApiHelper;
import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.llm.IntentDetectionResult;
import org.codelibs.fess.llm.LlmClientManager;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.llm.LlmStreamCallback;
import org.codelibs.fess.llm.RelevanceEvaluationResult;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Pins the source-field contract of the STREAMING {@code ChatClient#streamChatEnhanced} path --
 * the sibling of {@link ChatClientSourceFieldsTest}, which pins the same contract for the
 * non-streaming {@code chat()} path.
 *
 * <p>{@code content_description}/{@code content_title} are NOT index fields (they do not exist in
 * {@code fess_indices/fess/doc.json}); they are injected at render time by the rank-fusion searcher
 * from the highlight/digest fields. The fetch phase re-reads documents through
 * {@code SearchHelper#getDocumentListByDocIds} (a pure {@code _source} projection) or through a
 * doc_id-restricted highlight search, so those keys can never come back on the fetched maps.
 * Publishing the fetched maps as the API {@code sources[]} therefore silently dropped
 * {@code snippet} (and {@code title} for a document whose {@code title} is blank) from
 * {@code POST /api/v2/chat/stream}.</p>
 *
 * <p>The fix keeps the two lists separate inside {@code ChatClient}: the fetched maps stay the LLM
 * answer context (chunk-selected content included), while {@code sources[]} is resolved back to the
 * search-phase maps for the very same documents. No {@link ChatContentFetcher} contract change is
 * involved -- these tests assert both halves so a "just use the search results everywhere" revert,
 * which would undo the chunk-selection feature, fails here.</p>
 */
public class ChatClientStreamSourceFieldsTest extends UnitFessTestCase {

    private static final String SNIPPET = "…matched <strong>install</strong> passage…";

    private static final String SNIPPET2 = "…matched <strong>upgrade</strong> passage…";

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        // DefaultChatContentFetcher#isContentChunkerEnabled resolves ChunkVectorHelper as a
        // component; the minimal unit-test DI set does not wire it.
        ComponentUtil.register(new ChunkVectorHelper(), ChunkVectorHelper.class.getCanonicalName());
        // populateUrlLink() falls through to ViewHelper for any doc without url_link -- which is
        // exactly the projected (fetched) map shape, so this must be wired for both variants.
        ComponentUtil.register(new ViewHelper() {
            @Override
            public String getUrlLink(final Map<String, Object> document) {
                return "view-helper-link";
            }
        }, "viewHelper");
    }

    /** Only fields that actually exist in {@code fess_indices/fess/doc.json}. */
    private static Map<String, Object> indexedSource(final String docId, final String title) {
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("doc_id", docId);
        m.put("title", title);
        m.put("url", "http://example.com/" + docId);
        m.put("content", "the full indexed body text of " + docId);
        m.put("digest", "crawl-time digest text");
        m.put("content_length", 100L);
        return m;
    }

    /** A search-phase result map, as the rank-fusion searcher hands it to ChatClient. */
    private static Map<String, Object> searchResultDoc(final String docId, final String title, final String snippet) {
        final Map<String, Object> m = new LinkedHashMap<>(indexedSource(docId, title));
        m.put("content_title", "Install Guide"); // render-time only: never in _source
        m.put("content_description", snippet); // render-time only: never in _source
        m.put("url_link", "http://example.com/" + docId);
        m.put("score", 3.5f);
        return m;
    }

    private static Map<String, Object> searchResultDoc() {
        return searchResultDoc("id1", "Install Guide", SNIPPET);
    }

    /**
     * A fetcher that reproduces the FULL strategy's real projection: it can only return keys that
     * exist in the stored {@code _source}, because {@code getDocumentListByDocIds} passes the field
     * list to {@code setFetchSource(fields, null)}. Its {@code content} stands in for the
     * chunk-selected passage the real fetcher produces.
     */
    private static class ProjectingFetcher implements ChatContentFetcher {
        @Override
        public List<Map<String, Object>> fetchContent(final ChatContentRequest request) {
            final List<Map<String, Object>> out = new ArrayList<>();
            for (final String id : request.getDocIds()) {
                final Map<String, Object> projected = new LinkedHashMap<>(indexedSource(id, "Install Guide"));
                projected.put("content", "CHUNK-SELECTED CONTENT for " + id);
                projected.put("_id", id);
                out.add(projected);
            }
            return out;
        }
    }

    /** Captures the document list handed to the LLM and stubs out the search engine. */
    private static class CapturingChatClient extends ChatClient {
        List<Map<String, Object>> searchDocs = Collections.emptyList();
        List<Map<String, Object>> urlDocs = Collections.emptyList();
        List<Map<String, Object>> answerContextDocs;

        @Override
        protected ChatSearchResult searchDocuments(final String query, final Map<String, String[]> fields, final String[] extraQueries) {
            return new ChatSearchResult(searchDocs, "query-id-1", 4242L);
        }

        @Override
        protected ChatSearchResult searchByUrl(final String url) {
            return new ChatSearchResult(urlDocs, "url-query-id-1", 8484L);
        }
    }

    /**
     * Builds a client for the SEARCH intent whose evaluation phase reports {@code relevantDocIds}
     * as the relevant subset of the search results.
     */
    private CapturingChatClient newSearchChatClient(final List<Map<String, Object>> searchDocs, final List<String> relevantDocIds) {
        final CapturingChatClient client = new CapturingChatClient();
        client.searchDocs = searchDocs;
        client.chatSessionManager = new ChatSessionManager();
        client.llmClientManager = new LlmClientManager() {
            @Override
            public IntentDetectionResult detectIntent(final String userMessage, final List<LlmMessage> history) {
                return IntentDetectionResult.search("install", "test");
            }

            @Override
            public RelevanceEvaluationResult evaluateResults(final String userMessage, final String query,
                    final List<Map<String, Object>> searchResults) {
                final List<Integer> indexes = new ArrayList<>();
                for (int i = 0; i < relevantDocIds.size(); i++) {
                    indexes.add(i);
                }
                return RelevanceEvaluationResult.withRelevantDocs(relevantDocIds, indexes);
            }

            @Override
            public void streamGenerateAnswer(final String userMessage, final List<Map<String, Object>> documents,
                    final List<LlmMessage> history, final LlmStreamCallback callback) {
                client.answerContextDocs = documents;
                callback.onChunk("an answer", true);
            }
        };
        return client;
    }

    /** Builds a client for the SUMMARY intent (search-by-URL then fetch). */
    private CapturingChatClient newSummaryChatClient(final List<Map<String, Object>> urlDocs) {
        final CapturingChatClient client = new CapturingChatClient();
        client.urlDocs = urlDocs;
        client.chatSessionManager = new ChatSessionManager();
        client.llmClientManager = new LlmClientManager() {
            @Override
            public IntentDetectionResult detectIntent(final String userMessage, final List<LlmMessage> history) {
                return IntentDetectionResult.summary("http://example.com/id1", "test");
            }

            @Override
            public void generateSummaryResponse(final String userMessage, final List<Map<String, Object>> documents,
                    final List<LlmMessage> history, final LlmStreamCallback callback) {
                client.answerContextDocs = documents;
                callback.onChunk("a summary", true);
            }
        };
        return client;
    }

    private static ChatResult stream(final CapturingChatClient client, final String message) {
        return client.streamChatEnhanced(null, message, null, ChatPhaseCallback.noOp());
    }

    // ===================================================================================
    //                                          SEARCH intent: sources[] keep render fields
    //                                                                           =========

    @Test
    public void test_streamChatEnhanced_sourcesKeepSnippetWhileAnswerContextUsesFetchedContent() {
        ComponentUtil.register(new ProjectingFetcher(), "chatContentFetcher");
        final CapturingChatClient client = newSearchChatClient(List.of(searchResultDoc()), List.of("id1"));

        final ChatResult result = stream(client, "how do I install?");

        // (1) The LLM answer context must still come from the fetcher (the chunk-selection feature).
        assertNotNull(client.answerContextDocs, "streamGenerateAnswer must have been called");
        assertEquals(1, client.answerContextDocs.size());
        assertEquals("the answer context must keep using the fetcher's (chunk-selected) content", "CHUNK-SELECTED CONTENT for id1",
                client.answerContextDocs.get(0).get("content"));

        // (2) The streamed sources must still carry the render-time-only fields.
        final List<ChatSource> sources = result.getMessage().getSources();
        assertEquals(1, sources.size());
        assertEquals("sources[].snippet comes from content_description (render-time only)", SNIPPET, sources.get(0).getSnippet());
        assertEquals("Install Guide", sources.get(0).getTitle());
        assertEquals("id1", sources.get(0).getDocId());
        assertNotNull(sources.get(0).getGoUrl(), "go_url must still be built from the search queryId/requestedTime");

        // (3) The wire shape: ChatApiHelper#putIfNotNull drops the key entirely when snippet is null.
        final Map<String, Object> wire = new ChatApiHelper().toSourceMaps(sources).get(0);
        assertTrue(wire.containsKey("snippet"), "POST /api/v2/chat/stream sources[] must carry a snippet key");
        assertEquals(SNIPPET, wire.get("snippet"));
    }

    @Test
    public void test_streamChatEnhanced_blankTitleFallsBackToContentTitle() {
        // ChatSource falls back title <- content_title, another render-time-only field: a document
        // whose indexed title is blank comes out completely untitled from the projected maps.
        ComponentUtil.register(new ProjectingFetcher() {
            @Override
            public List<Map<String, Object>> fetchContent(final ChatContentRequest request) {
                final List<Map<String, Object>> docs = super.fetchContent(request);
                docs.forEach(doc -> doc.put("title", ""));
                return docs;
            }
        }, "chatContentFetcher");
        final CapturingChatClient client = newSearchChatClient(List.of(searchResultDoc("id1", "", SNIPPET)), List.of("id1"));

        final ChatResult result = stream(client, "how do I install?");

        final List<ChatSource> sources = result.getMessage().getSources();
        assertEquals(1, sources.size());
        assertEquals("sources[].title must fall back to content_title (render-time only)", "Install Guide", sources.get(0).getTitle());
    }

    @Test
    public void test_streamChatEnhanced_sourcesKeepTheRelevantSubsetOnly() {
        // The sources must stay the documents the fetch phase actually resolved -- the relevant
        // subset -- not every search hit. A "just publish searchResults" fix would list id1 too.
        ComponentUtil.register(new ProjectingFetcher(), "chatContentFetcher");
        final CapturingChatClient client =
                newSearchChatClient(List.of(searchResultDoc(), searchResultDoc("id2", "Upgrade Guide", SNIPPET2)), List.of("id2"));

        final ChatResult result = stream(client, "how do I upgrade?");

        final List<ChatSource> sources = result.getMessage().getSources();
        assertEquals("only the relevant documents may be published as sources", 1, sources.size());
        assertEquals("id2", sources.get(0).getDocId());
        assertEquals(SNIPPET2, sources.get(0).getSnippet());
        assertEquals("Upgrade Guide", sources.get(0).getTitle());
        assertEquals("CHUNK-SELECTED CONTENT for id2", client.answerContextDocs.get(0).get("content"));
    }

    @Test
    public void test_streamChatEnhanced_unmatchedFetchedDocKeepsTheFetchedMap() {
        // Defensive: a fetched doc_id with no search-phase counterpart (the evaluation phase is
        // LLM-driven and could name an id outside the result set) must still be published, using
        // the fetched map exactly as before.
        ComponentUtil.register(new ProjectingFetcher(), "chatContentFetcher");
        final CapturingChatClient client = newSearchChatClient(List.of(searchResultDoc()), List.of("ghost"));

        final ChatResult result = stream(client, "how do I install?");

        final List<ChatSource> sources = result.getMessage().getSources();
        assertEquals(1, sources.size());
        assertEquals("ghost", sources.get(0).getDocId());
        assertNull(sources.get(0).getSnippet(), "an unmatched doc has no render-time snippet to restore");
    }

    @Test
    public void test_streamChatEnhanced_emptyFetchKeepsSourcesEmpty() {
        // An empty fetch result must not start publishing the raw search results as sources:
        // the source set stays exactly what the fetch phase resolved (previous behavior).
        ComponentUtil.register((ChatContentFetcher) request -> Collections.emptyList(), "chatContentFetcher");
        final CapturingChatClient client = newSearchChatClient(List.of(searchResultDoc()), List.of("id1"));

        final ChatResult result = stream(client, "how do I install?");

        assertTrue(result.getSources().isEmpty(), "an empty fetch must leave the sources empty");
        assertTrue(result.getMessage().getSources().isEmpty(), "an empty fetch must leave the message sources empty");
    }

    // ===================================================================================
    //                                        SUMMARY intent: sources[] keep render fields
    //                                                                           =========

    @Test
    public void test_streamChatEnhanced_summarySourcesKeepSnippet() {
        ComponentUtil.register(new ProjectingFetcher(), "chatContentFetcher");
        final CapturingChatClient client = newSummaryChatClient(List.of(searchResultDoc()));

        final ChatResult result = stream(client, "summarize http://example.com/id1");

        assertNotNull(client.answerContextDocs, "generateSummaryResponse must have been called");
        assertEquals("the summary context must keep using the fetcher's content", "CHUNK-SELECTED CONTENT for id1",
                client.answerContextDocs.get(0).get("content"));

        final List<ChatSource> sources = result.getMessage().getSources();
        assertEquals(1, sources.size());
        assertEquals("the SUMMARY path's sources[].snippet comes from content_description", SNIPPET, sources.get(0).getSnippet());
        assertEquals("id1", sources.get(0).getDocId());
        assertNotNull(sources.get(0).getGoUrl(), "go_url must still be built from the url-search queryId/requestedTime");
    }
}
