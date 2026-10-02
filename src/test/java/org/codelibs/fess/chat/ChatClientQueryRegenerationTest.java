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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.chat.ChatClient.ChatSearchResult;
import org.codelibs.fess.llm.IntentDetectionResult;
import org.codelibs.fess.llm.LlmChatResponse;
import org.codelibs.fess.llm.LlmClientManager;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.llm.LlmStreamCallback;
import org.codelibs.fess.llm.RelevanceEvaluationResult;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Pins the query refinement loop of {@link ChatClient}: when a search finds nothing, or (in the
 * streaming chat) the model judges none of its hits relevant, the query is regenerated and the
 * search runs again, up to {@code rag.chat.query.regeneration.max.count} times.
 */
public class ChatClientQueryRegenerationTest extends UnitFessTestCase {

    private static final String FIRST_QUERY = "q0";

    /** The query each content fetch was made with, which selects the passages to evaluate. */
    private final List<String> fetchQueries = new ArrayList<>();

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        // An empty fetch makes fetchContentForAnswer fall back to the search-result maps.
        fetchQueries.clear();
        ComponentUtil.register((ChatContentFetcher) request -> {
            fetchQueries.add(request.getQuery());
            return Collections.emptyList();
        }, "chatContentFetcher");
    }

    private static Map<String, Object> doc(final String docId) {
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("doc_id", docId);
        m.put("title", "Title " + docId);
        m.put("url", "http://example.com/" + docId);
        m.put("url_link", "http://example.com/" + docId);
        m.put("content", "content of " + docId);
        m.put("content_description", "snippet of " + docId);
        return m;
    }

    /**
     * A client whose search engine and LLM are scripted: {@link #hits} maps a query to its hits,
     * {@link #relevantQueries} lists the queries whose hits the evaluation accepts, and
     * {@link #regeneratedQueries} is handed out by successive regenerateQuery calls.
     */
    private static class ScriptedChatClient extends ChatClient {
        final Map<String, List<Map<String, Object>>> hits = new HashMap<>();
        final List<String> relevantQueries = new ArrayList<>();
        final Deque<String> regeneratedQueries = new ArrayDeque<>();
        Integer maxRegenerations;
        String intentQuery = FIRST_QUERY;

        final List<String> searchedQueries = new ArrayList<>();
        final List<String> evaluatedQueries = new ArrayList<>();
        final List<String> regenerationCalls = new ArrayList<>();
        final List<String> events = new ArrayList<>();
        List<Map<String, Object>> answerDocs;
        boolean noResultsResponse;

        ScriptedChatClient() {
            chatSessionManager = new ChatSessionManager();
            llmClientManager = new LlmClientManager() {
                @Override
                public IntentDetectionResult detectIntent(final String userMessage, final List<LlmMessage> history) {
                    return IntentDetectionResult.search(intentQuery, "test");
                }

                @Override
                public RelevanceEvaluationResult evaluateResults(final String userMessage, final String query,
                        final List<Map<String, Object>> searchResults) {
                    evaluatedQueries.add(query);
                    if (!relevantQueries.contains(query)) {
                        return RelevanceEvaluationResult.noRelevantResults();
                    }
                    final List<String> ids = new ArrayList<>();
                    final List<Integer> indexes = new ArrayList<>();
                    for (int i = 0; i < searchResults.size(); i++) {
                        ids.add((String) searchResults.get(i).get("doc_id"));
                        indexes.add(i + 1);
                    }
                    return RelevanceEvaluationResult.withRelevantDocs(ids, indexes);
                }

                @Override
                public String regenerateQuery(final String userMessage, final String failedQuery, final String failureReason,
                        final List<LlmMessage> history) {
                    regenerationCalls.add(failedQuery + ":" + failureReason);
                    // AbstractLlmClient returns the failed query when the regeneration fails.
                    return regeneratedQueries.isEmpty() ? failedQuery : regeneratedQueries.poll();
                }

                @Override
                public void streamGenerateAnswer(final String userMessage, final List<Map<String, Object>> documents,
                        final List<LlmMessage> history, final LlmStreamCallback callback) {
                    answerDocs = documents;
                    callback.onChunk("answer", true);
                }

                @Override
                public void generateNoResultsResponse(final String userMessage, final List<LlmMessage> history,
                        final LlmStreamCallback callback) {
                    noResultsResponse = true;
                    callback.onChunk("no results", true);
                }

                @Override
                public LlmChatResponse generateAnswer(final String userMessage, final List<Map<String, Object>> documents,
                        final List<LlmMessage> history) {
                    answerDocs = documents;
                    return new LlmChatResponse("answer");
                }
            };
        }

        @Override
        protected ChatSearchResult searchDocuments(final String query, final Map<String, String[]> fields, final String[] extraQueries) {
            searchedQueries.add(query);
            return new ChatSearchResult(hits.getOrDefault(query, Collections.emptyList()), "qid-" + query, 1L);
        }

        @Override
        protected int getMaxQueryRegenerations() {
            return maxRegenerations != null ? maxRegenerations : super.getMaxQueryRegenerations();
        }

        ChatResult stream() {
            return streamChatEnhanced(null, "question", null, new ChatPhaseCallback() {
                @Override
                public void onPhaseStart(final String phase, final String message) {
                    events.add("start:" + phase);
                }

                @Override
                public void onPhaseComplete(final String phase) {
                    events.add("complete:" + phase);
                }

                @Override
                public void onChunk(final String content, final boolean done) {
                }

                @Override
                public void onFallback(final String phase, final String reason, final String originalQuery, final String newQuery) {
                    events.add("fallback:" + reason + ":" + originalQuery + "->" + newQuery);
                }

                @Override
                public void onError(final String phase, final String error) {
                    events.add("error:" + phase);
                }
            });
        }
    }

    // ===================================================================================
    //                                                                           Streaming
    //                                                                           =========

    @Test
    public void test_stream_refinesTheQueryUntilTheHitsAreRelevant() {
        final ScriptedChatClient client = new ScriptedChatClient();
        client.hits.put("q0", List.of(doc("a")));
        client.hits.put("q1", List.of(doc("b")));
        client.hits.put("q2", List.of(doc("c")));
        client.relevantQueries.add("q2");
        client.regeneratedQueries.addAll(List.of("q1", "q2"));

        final ChatResult result = client.stream();

        assertEquals(List.of("q0", "q1", "q2"), client.searchedQueries);
        // Each round is evaluated, and regenerated, from its own query -- not the first one.
        assertEquals(List.of("q0", "q1", "q2"), client.evaluatedQueries);
        assertEquals(List.of("q0:no_relevant_results", "q1:no_relevant_results"), client.regenerationCalls);
        assertFalse(client.noResultsResponse);
        assertEquals(1, result.getSources().size());
        assertEquals("c", result.getMessage().getSources().get(0).getDocId());
        assertEquals("q2", result.getMessage().getSearchQuery());
        assertEquals("c", client.answerDocs.get(0).get("doc_id"));
        assertEquals(List.of("q0", "q1", "q2"), fetchQueries);
        assertEquals(List.of("start:intent", "complete:intent", //
                "start:search", "complete:search", "start:evaluate", "complete:evaluate", "fallback:no_relevant_results:q0->q1", //
                "start:search", "complete:search", "start:evaluate", "complete:evaluate", "fallback:no_relevant_results:q1->q2", //
                "start:search", "complete:search", "start:evaluate", "complete:evaluate", //
                "start:fetch", "complete:fetch", "start:answer", "complete:answer"), client.events);
    }

    @Test
    public void test_stream_irrelevantHitsAfterAnEmptySearchAreRefinedFromTheCurrentQuery() {
        // No hits for q0, irrelevant hits for q1: the evaluation, the passage fetch and the next
        // regeneration all work from q1, the query that produced the hits.
        final ScriptedChatClient client = new ScriptedChatClient();
        client.hits.put("q1", List.of(doc("b")));
        client.hits.put("q2", List.of(doc("c")));
        client.relevantQueries.add("q2");
        client.regeneratedQueries.addAll(List.of("q1", "q2"));

        final ChatResult result = client.stream();

        assertEquals(List.of("q0", "q1", "q2"), client.searchedQueries);
        assertEquals(List.of("q1", "q2"), client.evaluatedQueries);
        assertEquals(List.of("q1", "q2"), fetchQueries);
        assertEquals(List.of("q0:no_results", "q1:no_relevant_results"), client.regenerationCalls);
        assertEquals("c", result.getMessage().getSources().get(0).getDocId());
    }

    @Test
    public void test_stream_retriesAfterAnEmptySearchThatFollowedAnIrrelevantOne() {
        // Irrelevant hits, then a refined query with no hits: the remaining attempt is still used.
        final ScriptedChatClient client = new ScriptedChatClient();
        client.hits.put("q0", List.of(doc("a")));
        client.hits.put("q2", List.of(doc("c")));
        client.relevantQueries.add("q2");
        client.regeneratedQueries.addAll(List.of("q1", "q2"));

        final ChatResult result = client.stream();

        assertEquals(List.of("q0", "q1", "q2"), client.searchedQueries);
        assertEquals(List.of("q0", "q2"), client.evaluatedQueries);
        assertEquals(List.of("q0:no_relevant_results", "q1:no_results"), client.regenerationCalls);
        assertEquals("c", result.getMessage().getSources().get(0).getDocId());
        assertEquals("q2", result.getMessage().getSearchQuery());
    }

    @Test
    public void test_stream_noHitsIsRetriedWithTheNoResultsReason() {
        final ScriptedChatClient client = new ScriptedChatClient();
        client.hits.put("q1", List.of(doc("b")));
        client.relevantQueries.add("q1");
        client.regeneratedQueries.add("q1");

        final ChatResult result = client.stream();

        assertEquals(List.of("q0", "q1"), client.searchedQueries);
        assertEquals(List.of("q1"), client.evaluatedQueries);
        assertEquals(List.of("q0:no_results"), client.regenerationCalls);
        assertEquals("b", result.getMessage().getSources().get(0).getDocId());
        // The phase protocol: the refined search is a second search phase after the fallback event.
        assertEquals(List.of("start:intent", "complete:intent", "start:search", "complete:search", "fallback:no_results:q0->q1",
                "start:search", "complete:search", "start:evaluate", "complete:evaluate", "start:fetch", "complete:fetch", "start:answer",
                "complete:answer"), client.events);
    }

    @Test
    public void test_stream_stopsAtTheRegenerationLimit() {
        final ScriptedChatClient client = new ScriptedChatClient();
        client.hits.put("q0", List.of(doc("a")));
        client.hits.put("q1", List.of(doc("b")));
        client.hits.put("q2", List.of(doc("c")));
        client.hits.put("q3", List.of(doc("d")));
        client.relevantQueries.add("q3");
        client.regeneratedQueries.addAll(List.of("q1", "q2", "q3"));

        final ChatResult result = client.stream();

        // The default limit is 2 regenerations, so q3 is never searched.
        assertEquals(List.of("q0", "q1", "q2"), client.searchedQueries);
        assertEquals(2, client.regenerationCalls.size());
        assertTrue(client.noResultsResponse);
        assertTrue(result.getSources().isEmpty());
        assertEquals("q2", result.getMessage().getSearchQuery());
    }

    @Test
    public void test_stream_honorsALargerLimit() {
        final ScriptedChatClient client = new ScriptedChatClient();
        client.maxRegenerations = 3;
        client.hits.put("q3", List.of(doc("d")));
        client.relevantQueries.add("q3");
        client.regeneratedQueries.addAll(List.of("q1", "q2", "q3"));

        final ChatResult result = client.stream();

        assertEquals(List.of("q0", "q1", "q2", "q3"), client.searchedQueries);
        assertEquals("d", result.getMessage().getSources().get(0).getDocId());
    }

    @Test
    public void test_stream_zeroLimitDisablesRegeneration() {
        final ScriptedChatClient client = new ScriptedChatClient();
        client.maxRegenerations = 0;
        client.hits.put("q0", List.of(doc("a")));
        client.regeneratedQueries.add("q1");

        final ChatResult result = client.stream();

        assertEquals(List.of("q0"), client.searchedQueries);
        assertTrue(client.regenerationCalls.isEmpty());
        assertTrue(client.noResultsResponse);
        assertTrue(result.getSources().isEmpty());
    }

    @Test
    public void test_stream_alreadyTriedQueryEndsTheLoop() {
        // q0 -> q1 -> q0 would only repeat the first search.
        final ScriptedChatClient client = new ScriptedChatClient();
        client.hits.put("q0", List.of(doc("a")));
        client.hits.put("q1", List.of(doc("b")));
        client.regeneratedQueries.addAll(List.of("q1", "q0"));

        client.stream();

        assertEquals(List.of("q0", "q1"), client.searchedQueries);
        assertEquals(2, client.regenerationCalls.size());
        assertTrue(client.noResultsResponse);
        // Only the accepted regeneration is announced.
        assertEquals(List.of("fallback:no_relevant_results:q0->q1"),
                client.events.stream().filter(e -> e.startsWith("fallback:")).toList());
    }

    @Test
    public void test_stream_firstQueryIsComparedTrimmed() {
        // The LLM client returns the failed query untrimmed when the regeneration fails; it must
        // still count as already searched.
        final ScriptedChatClient client = new ScriptedChatClient();
        client.intentQuery = "q0 ";
        client.regeneratedQueries.add("q0");

        client.stream();

        assertEquals(List.of("q0 "), client.searchedQueries);
        assertEquals(1, client.regenerationCalls.size());
        assertTrue(client.noResultsResponse);
        assertFalse(client.events.stream().anyMatch(e -> e.startsWith("fallback:")));
    }

    @Test
    public void test_stream_failedRegenerationEndsTheLoop() {
        // The LLM client returns the failed query itself when the regeneration fails.
        final ScriptedChatClient client = new ScriptedChatClient();

        client.stream();

        assertEquals(List.of("q0"), client.searchedQueries);
        assertEquals(List.of("q0:no_results"), client.regenerationCalls);
        assertTrue(client.noResultsResponse);
    }

    @Test
    public void test_stream_relevantFirstSearchDoesNotRegenerate() {
        final ScriptedChatClient client = new ScriptedChatClient();
        client.hits.put("q0", List.of(doc("a")));
        client.relevantQueries.add("q0");
        client.regeneratedQueries.add("q1");

        final ChatResult result = client.stream();

        assertEquals(List.of("q0"), client.searchedQueries);
        assertTrue(client.regenerationCalls.isEmpty());
        assertEquals("a", result.getMessage().getSources().get(0).getDocId());
        assertFalse(client.events.stream().anyMatch(e -> e.startsWith("fallback:")));
    }

    // ===================================================================================
    //                                                                       Non-streaming
    //                                                                       =============

    @Test
    public void test_chat_refinesTheQueryWhileTheSearchFindsNothing() {
        final ScriptedChatClient client = new ScriptedChatClient();
        client.hits.put("q2", List.of(doc("c")));
        client.regeneratedQueries.addAll(List.of("q1", "q2"));

        final ChatResult result = client.chat(null, "question", null);

        assertEquals(List.of("q0", "q1", "q2"), client.searchedQueries);
        assertEquals(List.of("q0:no_results", "q1:no_results"), client.regenerationCalls);
        // The non-streaming chat does not evaluate relevance.
        assertTrue(client.evaluatedQueries.isEmpty());
        assertEquals(1, result.getSources().size());
        assertEquals("q2", result.getMessage().getSearchQuery());
        assertEquals("c", client.answerDocs.get(0).get("doc_id"));
    }

    @Test
    public void test_chat_stopsAtTheRegenerationLimit() {
        final ScriptedChatClient client = new ScriptedChatClient();
        client.maxRegenerations = 1;
        client.hits.put("q2", List.of(doc("c")));
        client.regeneratedQueries.addAll(List.of("q1", "q2"));

        final ChatResult result = client.chat(null, "question", null);

        assertEquals(List.of("q0", "q1"), client.searchedQueries);
        assertTrue(result.getSources().isEmpty());
        assertEquals("q1", result.getMessage().getSearchQuery());
    }

    @Test
    public void test_chat_hitsDoNotRegenerate() {
        final ScriptedChatClient client = new ScriptedChatClient();
        client.hits.put("q0", List.of(doc("a")));
        client.regeneratedQueries.add("q1");

        client.chat(null, "question", null);

        assertEquals(List.of("q0"), client.searchedQueries);
        assertTrue(client.regenerationCalls.isEmpty());
    }

    // ===================================================================================
    //                                                                       Configuration
    //                                                                       =============

    @Test
    public void test_maxQueryRegenerations_defaultsToTwo() {
        assertEquals("rag.chat.query.regeneration.max.count", FessConfig.RAG_CHAT_QUERY_REGENERATION_MAX_COUNT);
        assertEquals(2, ComponentUtil.getFessConfig().getRagChatQueryRegenerationMaxCountAsInteger().intValue());
        assertEquals(2, new ChatClient().getMaxQueryRegenerations());
    }

    @Test
    public void test_maxQueryRegenerations_negativeMeansDisabled() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer getRagChatQueryRegenerationMaxCountAsInteger() {
                return -1;
            }
        });
        assertEquals(0, new ChatClient().getMaxQueryRegenerations());
    }

    @Test
    public void test_regenerateUntriedQuery_trimsAndRecordsTheQuery() {
        final ScriptedChatClient client = new ScriptedChatClient();
        client.regeneratedQueries.addAll(List.of("  q1  ", "q1", "   "));
        final Set<String> tried = new HashSet<>(Set.of("q0"));

        assertEquals("q1", client.regenerateUntriedQuery("question", "q0", "no_results", Collections.emptyList(), tried));
        assertEquals(Set.of("q0", "q1"), tried);
        assertNull(client.regenerateUntriedQuery("question", "q1", "no_results", Collections.emptyList(), tried));
        assertNull(client.regenerateUntriedQuery("question", "q1", "no_results", Collections.emptyList(), tried));
    }
}
