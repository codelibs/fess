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
import java.util.Optional;
import java.util.TreeMap;

import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.llm.LlmClientManager;
import org.codelibs.fess.llm.LlmException;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.llm.LlmStreamCallback;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Pins {@code ChatClient#streamChatAboutDocument}: the answer comes from one document only, either
 * in a single pass ({@code generateSummaryResponse} on the document) or, when the document does not
 * fit the summary context budget, by summarizing its parts and answering from the part summaries.
 */
public class ChatClientDocumentChatTest extends UnitFessTestCase {

    private static final String TITLE = "T";

    private static final String URL = "http://example.com/doc1";

    /** Content budget with the test document: limit - title - url - the fixed overhead. */
    private static final int LIMIT = 600;

    private static final int PART_BUDGET = LIMIT - TITLE.length() - URL.length() - ChatClient.DOCUMENT_PROMPT_OVERHEAD_CHARS;

    @Override
    public void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new ViewHelper() {
            @Override
            public String getUrlLink(final Map<String, Object> document) {
                return "view-helper-link";
            }
        }, "viewHelper");
    }

    /** One summarizeDocumentPart call as the client made it. */
    private record PartCall(String userMessage, String title, String url, String partText, int partIndex, int partCount) {
    }

    /** One generateSummaryResponse call as the client made it. */
    private record AnswerCall(String userMessage, List<Map<String, Object>> documents, List<LlmMessage> history) {
    }

    /** Records every event the client reports. */
    private static class RecordingCallback implements ChatPhaseCallback {
        final List<String> events = new ArrayList<>();
        final StringBuilder chunks = new StringBuilder();
        String errorPhase;
        String errorCode;

        @Override
        public void onPhaseStart(final String phase, final String message) {
            events.add("start:" + phase + ":" + message);
        }

        @Override
        public void onPhaseComplete(final String phase) {
            events.add("complete:" + phase);
        }

        @Override
        public void onPhaseComplete(final String phase, final Map<String, Object> payload) {
            events.add("complete:" + phase + (payload.isEmpty() ? "" : ":" + new TreeMap<>(payload)));
        }

        @Override
        public void onChunk(final String content, final boolean done) {
            chunks.append(content);
        }

        @Override
        public void onError(final String phase, final String error) {
            errorPhase = phase;
            errorCode = error;
        }

        @Override
        public void onWarning(final String phase, final String code, final String detail) {
            events.add("warning:" + phase + ":" + code + ":" + detail);
        }
    }

    /** A client whose LLM layer and document fetch are stubbed and recorded. */
    private static class Fixture {
        final List<PartCall> partCalls = new ArrayList<>();
        final List<AnswerCall> answerCalls = new ArrayList<>();
        Map<String, Object> document;
        int maxParts = 10;
        int failOnPart = -1;
        String partSummary = null;
        final ChatClient client;

        Fixture(final Object content, final int contextMaxChars) {
            document = new LinkedHashMap<>();
            document.put("doc_id", "doc1");
            document.put("title", TITLE);
            document.put("url", URL);
            document.put("content", content);
            ComponentUtil.register(new ChatContentFetcher() {
                @Override
                public List<Map<String, Object>> fetchContent(final ChatContentRequest request) {
                    throw new AssertionError("a document chat must fetch the whole document, not select content by query");
                }

                @Override
                public Optional<Map<String, Object>> fetchWholeDocument(final String docId) {
                    return "doc1".equals(docId) ? Optional.ofNullable(document) : Optional.empty();
                }
            }, "chatContentFetcher");
            client = new ChatClient() {
                @Override
                protected int getDocumentMaxParts() {
                    return maxParts;
                }
            };
            client.chatSessionManager = new ChatSessionManager();
            client.llmClientManager = new LlmClientManager() {
                @Override
                public int getSummaryContextMaxChars() {
                    return contextMaxChars;
                }

                @Override
                public String summarizeDocumentPart(final String userMessage, final String title, final String url, final String partText,
                        final int partIndex, final int partCount, final int maxChars) {
                    partCalls.add(new PartCall(userMessage, title, url, partText, partIndex, partCount));
                    if (partIndex == failOnPart) {
                        throw new LlmException("part failed", LlmException.ERROR_TIMEOUT);
                    }
                    return partSummary != null ? partSummary : "summary" + partIndex;
                }

                @Override
                public void generateSummaryResponse(final String userMessage, final List<Map<String, Object>> documents,
                        final List<LlmMessage> history, final LlmStreamCallback callback) {
                    answerCalls.add(new AnswerCall(userMessage, documents, history));
                    callback.onChunk("the ", false);
                    callback.onChunk("answer", true);
                }
            };
        }

        ChatResult stream(final String sessionId, final RecordingCallback callback) {
            return client.streamChatAboutDocument(sessionId, "summarize it", "user1", "doc1", callback);
        }
    }

    private static String sentences(final int count) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("Sentence number ").append(i).append(" of the long document. ");
        }
        return sb.toString().trim();
    }

    // ===================================================================================
    //                                                                        single pass
    //                                                                        ===========

    @Test
    public void test_singlePass_answersFromTheWholeDocumentWithOneSummaryCall() {
        final Fixture f = new Fixture("short content", 10000);
        final RecordingCallback callback = new RecordingCallback();

        final ChatResult result = f.stream(null, callback);

        assertTrue(f.partCalls.isEmpty(), "no part may be summarized when the document fits");
        assertEquals(1, f.answerCalls.size());
        assertEquals("summarize it", f.answerCalls.get(0).userMessage());
        assertEquals(1, f.answerCalls.get(0).documents().size());
        assertSame(f.document, f.answerCalls.get(0).documents().get(0));
        assertEquals("the answer", callback.chunks.toString());
        assertEquals("the answer", result.getMessage().getContent());
        assertNotNull(result.getMessage().getHtmlContent());
        assertNull(callback.errorCode);
        // Phases: fetch, then answer -- existing phase names only, no search/intent/evaluate.
        assertEquals(List.of("start:fetch:Retrieving document content...", "complete:fetch", "start:answer:Generating response...",
                "complete:answer"), callback.events);
    }

    @Test
    public void test_singlePass_sourceIsTheDocumentWithoutGoUrl() {
        final Fixture f = new Fixture("short content", 10000);

        final ChatResult result = f.stream(null, new RecordingCallback());

        final List<ChatSource> sources = result.getMessage().getSources();
        assertEquals(1, sources.size());
        assertEquals("doc1", sources.get(0).getDocId());
        assertEquals(TITLE, sources.get(0).getTitle());
        assertEquals(URL, sources.get(0).getUrl());
        assertEquals("view-helper-link", sources.get(0).getUrlLink());
        assertNull(sources.get(0).getGoUrl(), "there is no search query id to build a go url from");
        assertEquals(1, result.getSources().size());
    }

    @Test
    public void test_singlePass_chunkedContentIsPassedUntouchedAndNotTruncated() {
        // 20 chunks of 400 chars = 8000+ chars: far beyond rag.chat.content.fulltext.max.length (3000).
        final List<String> chunks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            chunks.add(String.valueOf((char) ('a' + i)).repeat(400));
        }
        final Fixture f = new Fixture(new ArrayList<Object>(chunks), 100000);

        f.stream(null, new RecordingCallback());

        assertTrue(f.partCalls.isEmpty());
        assertEquals(1, f.answerCalls.size());
        assertEquals(chunks, f.answerCalls.get(0).documents().get(0).get("content"));
    }

    @Test
    public void test_singlePass_exactFitIsNotSplit() {
        final Fixture f = new Fixture("x".repeat(PART_BUDGET), LIMIT);

        f.stream(null, new RecordingCallback());

        assertTrue(f.partCalls.isEmpty());
        assertEquals(1, f.answerCalls.size());
    }

    @Test
    public void test_session_isReusedAndHistoryReachesTheAnswer() {
        final Fixture f = new Fixture("short content", 10000);

        final ChatResult first = f.stream(null, new RecordingCallback());
        final ChatResult second = f.stream(first.getSessionId(), new RecordingCallback());

        assertEquals(first.getSessionId(), second.getSessionId());
        assertTrue(f.answerCalls.get(0).history().isEmpty());
        assertFalse(f.answerCalls.get(1).history().isEmpty(), "the first turn must be in the history of the second");
    }

    @Test
    public void test_chatAboutDocument_returnsTheWholeAnswerAndOneSource() {
        final Fixture f = new Fixture("short content", 10000);

        final ChatResult result = f.client.chatAboutDocument(null, "summarize it", "user1", "doc1");

        assertEquals("the answer", result.getMessage().getContent());
        assertEquals(1, result.getMessage().getSources().size());
    }

    // ===================================================================================
    //                                                                         map-reduce
    //                                                                         ==========

    @Test
    public void test_longText_summarizesEveryPartThenAnswersOnceFromTheCombinedSummaries() {
        final String text = sentences(60);
        assertTrue(text.length() > PART_BUDGET * 3, "the test document must need several parts");
        final Fixture f = new Fixture(text, LIMIT);
        final RecordingCallback callback = new RecordingCallback();

        final ChatResult result = f.stream(null, callback);

        final int parts = f.partCalls.size();
        assertTrue(parts >= 2, "expected several parts but got " + parts);
        final StringBuilder rebuilt = new StringBuilder();
        for (int i = 0; i < parts; i++) {
            final PartCall call = f.partCalls.get(i);
            assertEquals(i + 1, call.partIndex());
            assertEquals(parts, call.partCount());
            assertEquals("summarize it", call.userMessage());
            assertEquals(TITLE, call.title());
            assertEquals(URL, call.url());
            assertTrue(call.partText().length() <= PART_BUDGET, "part " + i + " too long: " + call.partText().length());
            rebuilt.append(call.partText().replaceAll("\\s", ""));
        }
        assertEquals("every character of the document must reach a part", text.replaceAll("\\s", ""), rebuilt.toString());

        // Reduce: exactly one answer call, over a copy of the document whose content is the joined summaries.
        assertEquals(1, f.answerCalls.size());
        final AnswerCall answer = f.answerCalls.get(0);
        assertEquals("summarize it", answer.userMessage());
        final Map<String, Object> reduced = answer.documents().get(0);
        assertEquals("doc1", reduced.get("doc_id"));
        assertEquals(TITLE, reduced.get("title"));
        assertEquals(URL, reduced.get("url"));
        final StringBuilder expected = new StringBuilder();
        for (int i = 1; i <= parts; i++) {
            expected.append(i > 1 ? "\n\n" : "").append("Part ").append(i).append('/').append(parts).append(":\nsummary").append(i);
        }
        assertEquals(expected.toString(), reduced.get("content"));
        assertEquals("the original document must stay untouched", text, f.document.get("content"));

        assertEquals("the answer", result.getMessage().getContent());
        assertEquals(1, result.getMessage().getSources().size());
        assertEquals("doc1", result.getMessage().getSources().get(0).getDocId());
    }

    @Test
    public void test_longText_reportsProgressInTheFetchPhase() {
        final Fixture f = new Fixture(sentences(60), LIMIT);
        final RecordingCallback callback = new RecordingCallback();

        f.stream(null, callback);

        final int parts = f.partCalls.size();
        final List<String> expected = new ArrayList<>();
        expected.add("start:fetch:Retrieving document content...");
        expected.add("complete:fetch:{parts=" + parts + "}");
        for (int i = 1; i <= parts; i++) {
            expected.add("start:fetch:Summarizing document part " + i + " of " + parts + "...");
            expected.add("complete:fetch:{part=" + i + ", parts=" + parts + "}");
        }
        expected.add("start:answer:Generating response...");
        expected.add("complete:answer");
        assertEquals(expected, callback.events);
    }

    @Test
    public void test_longChunkedDocument_packsChunksIntoParts() {
        final List<Object> chunks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            chunks.add(String.valueOf((char) ('a' + i)).repeat(200));
        }
        final Fixture f = new Fixture(chunks, LIMIT);

        f.stream(null, new RecordingCallback());

        // Two 200-char chunks plus the separator fit a part; a third does not.
        assertEquals(5, f.partCalls.size());
        assertEquals("a".repeat(200) + "\n\n" + "b".repeat(200), f.partCalls.get(0).partText());
        assertEquals(1, f.answerCalls.size());
    }

    @Test
    public void test_longChunkedDocument_splitsAChunkLargerThanThePartBudget() {
        final List<Object> chunks = List.of("a".repeat(1000), "tail chunk");
        final Fixture f = new Fixture(new ArrayList<>(chunks), LIMIT);

        f.stream(null, new RecordingCallback());

        assertTrue(f.partCalls.size() >= 3, "the oversized chunk must be split: " + f.partCalls.size());
        for (final PartCall call : f.partCalls) {
            assertTrue(call.partText().length() <= PART_BUDGET);
        }
        assertEquals("tail chunk", f.partCalls.get(f.partCalls.size() - 1).partText());
    }

    @Test
    public void test_longDocument_overThePartCapUsesOnlyTheLeadingPartsAndWarns() {
        final String text = sentences(100);
        final Fixture f = new Fixture(text, LIMIT);
        f.maxParts = 2;
        final RecordingCallback callback = new RecordingCallback();

        f.stream(null, callback);

        assertEquals(2, f.partCalls.size());
        assertEquals(2, f.partCalls.get(0).partCount());
        assertEquals(2, f.partCalls.get(1).partCount());
        assertTrue(text.replaceAll("\\s", "").startsWith(f.partCalls.get(0).partText().replaceAll("\\s", "")));
        final String reduced = (String) f.answerCalls.get(0).documents().get(0).get("content");
        assertTrue(reduced.startsWith("Part 1/2:\nsummary1"), reduced);
        assertTrue(reduced.contains("Part 2/2:\nsummary2"), reduced);
        assertFalse(reduced.contains("Part 3"), reduced);
        assertEquals(1, callback.events.stream().filter(e -> e.startsWith("warning:")).count());
        assertTrue(callback.events.contains("warning:fetch:document_truncated:only the leading part of the document was used"),
                callback.events.toString());
        // The warning comes before the first part is summarized.
        assertTrue(
                callback.events.indexOf("warning:fetch:document_truncated:only the leading part of the document was used") < callback.events
                        .indexOf("start:fetch:Summarizing document part 1 of 2..."));
    }

    @Test
    public void test_longDocument_exactlyAtThePartCapDoesNotWarn() {
        final Fixture f = new Fixture(sentences(100), LIMIT);
        f.maxParts = 1000;
        f.stream(null, new RecordingCallback());
        final int needed = f.partCalls.size();

        final Fixture capped = new Fixture(sentences(100), LIMIT);
        capped.maxParts = needed;
        final RecordingCallback cappedCallback = new RecordingCallback();
        capped.stream(null, cappedCallback);

        assertEquals(needed, capped.partCalls.size());
        assertTrue(cappedCallback.events.stream().noneMatch(e -> e.startsWith("warning:")), cappedCallback.events.toString());
    }

    @Test
    public void test_longDocument_oversizedPartSummariesAreBoundedToTheBudget() {
        final Fixture f = new Fixture(sentences(100), LIMIT);
        f.partSummary = "s".repeat(5000);

        f.stream(null, new RecordingCallback());

        final String reduced = (String) f.answerCalls.get(0).documents().get(0).get("content");
        assertTrue(reduced.length() <= PART_BUDGET, "the combined summaries must fit the budget: " + reduced.length());
        assertTrue(reduced.contains("Part " + f.partCalls.size() + "/" + f.partCalls.size() + ":"), "the last part must survive");
    }

    @Test
    public void test_longDocument_aFailedPartSummaryFailsTheRequest() {
        final Fixture f = new Fixture(sentences(60), LIMIT);
        f.failOnPart = 2;
        final RecordingCallback callback = new RecordingCallback();

        final LlmException e = Assertions.assertThrows(LlmException.class, () -> f.stream(null, callback));

        assertEquals(LlmException.ERROR_TIMEOUT, e.getErrorCode());
        assertEquals("llm", callback.errorPhase);
        assertEquals(LlmException.ERROR_TIMEOUT, callback.errorCode);
        assertEquals(2, f.partCalls.size(), "no part after the failed one may be summarized");
        assertTrue(f.answerCalls.isEmpty(), "no answer may be generated from a partial set of summaries");
    }

    @Test
    public void test_whitespaceOnlyLongContentIsAnsweredWithoutSummaries() {
        final Fixture f = new Fixture(" ".repeat(5000), LIMIT);

        f.stream(null, new RecordingCallback());

        assertTrue(f.partCalls.isEmpty());
        assertEquals(1, f.answerCalls.size());
    }

    @Test
    public void test_documentMaxParts_defaultsToTen() {
        assertEquals("rag.chat.document.max.parts", org.codelibs.fess.mylasta.direction.FessConfig.RAG_CHAT_DOCUMENT_MAX_PARTS);
        assertEquals(10, ComponentUtil.getFessConfig().getRagChatDocumentMaxPartsAsInteger().intValue());
        assertEquals(10, new ChatClient().getDocumentMaxParts());
    }

    // ===================================================================================
    //                                                                            failures
    //                                                                            ========

    @Test
    public void test_missingDocumentFailsTheRequestWithoutCallingTheLlm() {
        final Fixture f = new Fixture("x", 10000);
        f.document = null;
        final RecordingCallback callback = new RecordingCallback();

        Assertions.assertThrows(IllegalStateException.class, () -> f.stream(null, callback));

        assertEquals("unknown", callback.errorCode);
        assertTrue(f.partCalls.isEmpty());
        assertTrue(f.answerCalls.isEmpty());
    }

    @Test
    public void test_answerFailureIsReportedAsLlmError() {
        final Fixture f = new Fixture("short", 10000);
        f.client.llmClientManager = new LlmClientManager() {
            @Override
            public int getSummaryContextMaxChars() {
                return 10000;
            }

            @Override
            public void generateSummaryResponse(final String userMessage, final List<Map<String, Object>> documents,
                    final List<LlmMessage> history, final LlmStreamCallback callback) {
                throw new LlmException("boom", LlmException.ERROR_SERVICE_UNAVAILABLE);
            }
        };
        final RecordingCallback callback = new RecordingCallback();

        Assertions.assertThrows(LlmException.class, () -> f.stream(null, callback));

        assertEquals("llm", callback.errorPhase);
        assertEquals(LlmException.ERROR_SERVICE_UNAVAILABLE, callback.errorCode);
        assertEquals(Collections.emptyList(), f.answerCalls);
    }
}
