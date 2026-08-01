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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.codelibs.fess.chat.ChatClient.ChatResult;
import org.codelibs.fess.chat.ChatClient.ChatSearchResult;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.helper.ChatApiHelper;
import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.llm.IntentDetectionResult;
import org.codelibs.fess.llm.LlmChatResponse;
import org.codelibs.fess.llm.LlmClientManager;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Pins the source-field contract of the non-streaming {@code ChatClient#chat} path.
 *
 * <p>{@code content_description}/{@code content_title} are NOT index fields (they do not exist in
 * {@code fess_indices/fess/doc.json}); they are injected at render time by the rank-fusion searcher
 * from the highlight/digest fields. The answer-content fetch re-reads documents through
 * {@code SearchHelper#getDocumentListByDocIds}, which is a pure {@code _source} projection and
 * therefore can never return them. Building the API {@code sources[]} from the fetched maps
 * silently dropped {@code snippet} from {@code POST /api/v2/chat}; these tests hold the line that
 * the sources keep coming from the search-phase maps while the LLM answer context keeps coming
 * from the fetcher.</p>
 */
public class ChatClientSourceFieldsTest extends UnitFessTestCase {

    private static final String SNIPPET = "…matched <strong>install</strong> passage…";

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
    private static Map<String, Object> indexedSource() {
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("doc_id", "id1");
        m.put("title", "Install Guide");
        m.put("url", "http://example.com/install");
        m.put("content", "the full indexed body text");
        m.put("digest", "crawl-time digest text");
        m.put("content_length", 100L);
        return m;
    }

    /** A search-phase result map, as the rank-fusion searcher hands it to ChatClient. */
    private static Map<String, Object> searchResultDoc() {
        final Map<String, Object> m = new LinkedHashMap<>(indexedSource());
        m.put("content_title", "Install Guide");
        m.put("content_description", SNIPPET); // render-time only: never in _source
        m.put("url_link", "http://example.com/install");
        m.put("score", 3.5f);
        return m;
    }

    /**
     * A fetcher that reproduces the FULL strategy's real projection: it can only return keys that
     * exist in the stored {@code _source}, because {@code getDocumentListByDocIds} passes the field
     * list to {@code setFetchSource(fields, null)}.
     */
    private static class ProjectingFetcher implements ChatContentFetcher {
        @Override
        public List<Map<String, Object>> fetchContent(final ChatContentRequest request) {
            final List<Map<String, Object>> out = new ArrayList<>();
            for (final String id : request.getDocIds()) {
                final Map<String, Object> projected = new LinkedHashMap<>(indexedSource());
                projected.put("doc_id", id);
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
        List<Map<String, Object>> answerContextDocs;

        @Override
        protected ChatSearchResult searchDocuments(final String query, final Map<String, String[]> fields, final String[] extraQueries) {
            return new ChatSearchResult(searchDocs, "query-id-1", 4242L);
        }
    }

    private CapturingChatClient newChatClient(final List<Map<String, Object>> searchDocs) {
        final CapturingChatClient client = new CapturingChatClient();
        client.searchDocs = searchDocs;
        client.chatSessionManager = new ChatSessionManager();
        client.llmClientManager = new LlmClientManager() {
            @Override
            public IntentDetectionResult detectIntent(final String userMessage, final List<LlmMessage> history) {
                return IntentDetectionResult.search("install", "test");
            }

            @Override
            public LlmChatResponse generateAnswer(final String userMessage, final List<Map<String, Object>> documents,
                    final List<LlmMessage> history) {
                client.answerContextDocs = documents;
                return new LlmChatResponse("an answer");
            }
        };
        return client;
    }

    // ===================================================================================
    //                                                sources[] must keep render-only fields
    //                                                                           =========

    @Test
    public void test_chat_sourcesKeepSnippetWhileAnswerContextUsesFetchedContent() {
        ComponentUtil.register(new ProjectingFetcher(), "chatContentFetcher");
        final CapturingChatClient client = newChatClient(List.of(searchResultDoc()));

        final ChatResult result = client.chat(null, "how do I install?", null);

        // (1) The LLM answer context must still come from the fetcher (the point of the change).
        assertNotNull(client.answerContextDocs, "generateAnswer must have been called");
        assertEquals(1, client.answerContextDocs.size());
        assertEquals("the answer context must keep using the fetcher's (chunk-selected) content", "CHUNK-SELECTED CONTENT for id1",
                client.answerContextDocs.get(0).get("content"));

        // (2) The API sources must still carry the render-time-only fields.
        final List<ChatSource> sources = result.getMessage().getSources();
        assertEquals(1, sources.size());
        assertEquals("sources[].snippet comes from content_description (render-time only)", SNIPPET, sources.get(0).getSnippet());
        assertEquals("Install Guide", sources.get(0).getTitle());
        assertEquals("id1", sources.get(0).getDocId());
        assertNotNull(sources.get(0).getGoUrl(), "go_url must still be built from the search queryId/requestedTime");

        // (3) The wire shape: ChatApiHelper#putIfNotNull drops the key entirely when snippet is null.
        final Map<String, Object> wire = new ChatApiHelper().toSourceMaps(sources).get(0);
        assertTrue(wire.containsKey("snippet"), "POST /api/v2/chat sources[] must carry a snippet key");
        assertEquals(SNIPPET, wire.get("snippet"));
    }

    @Test
    public void test_chat_sourcesSurviveWhenFetcherReturnsNothing() {
        // fetchContentForAnswer degrades to the raw search results; the sources must be unaffected.
        ComponentUtil.register((ChatContentFetcher) request -> Collections.emptyList(), "chatContentFetcher");
        final CapturingChatClient client = newChatClient(List.of(searchResultDoc()));

        final ChatResult result = client.chat(null, "how do I install?", null);

        assertEquals("an empty fetch must degrade the answer context to the raw search results", "the full indexed body text",
                client.answerContextDocs.get(0).get("content"));
        assertEquals(SNIPPET, result.getMessage().getSources().get(0).getSnippet());
    }

    @Test
    public void test_chat_resultSourcesAreTheSearchPhaseMaps() {
        // ChatResult's third argument is the *sources* list (getSources()), so it must be the same
        // list the message's ChatSource objects were built from, not the answer-context list.
        ComponentUtil.register(new ProjectingFetcher(), "chatContentFetcher");
        final CapturingChatClient client = newChatClient(List.of(searchResultDoc()));

        final ChatResult result = client.chat(null, "how do I install?", null);

        assertEquals(1, result.getSources().size());
        assertEquals("ChatResult#getSources must expose the source maps, not the projected answer-context maps", SNIPPET,
                result.getSources().get(0).get("content_description"));
        Assertions.assertNotSame(client.answerContextDocs, result.getSources());
    }

    // ===================================================================================
    //                                             degrade catch must log the stack trace
    //                                                                           =========

    @Test
    public void test_fetchContentForAnswer_degradeWarnCarriesStackTrace() {
        // fetchFullContent/fetchHighlightedContent already catch and degrade internally, so this
        // catch only ever fires on a programming error -- where the stack trace is the whole
        // diagnostic. A two-placeholder/two-argument logger.warn() drops the Throwable.
        ComponentUtil.register((ChatContentFetcher) request -> {
            throw new IllegalStateException("boom");
        }, "chatContentFetcher");
        final CapturingChatClient client = newChatClient(List.of(searchResultDoc()));
        final List<Map<String, Object>> searchResults = List.of(searchResultDoc());

        final List<LogEvent> captured = new ArrayList<>();
        final String loggerName = ChatClient.class.getName();
        final LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        final Configuration cfg = ctx.getConfiguration();
        final LoggerConfig loggerCfg = cfg.getLoggerConfig(loggerName);
        final Level originalLevel = loggerCfg.getLevel();
        final AbstractAppender listAppender = new AbstractAppender("chat-client-degrade-appender", null,
                PatternLayout.createDefaultLayout(), true, Property.EMPTY_ARRAY) {
            @Override
            public void append(final LogEvent event) {
                if (event.getLevel().isMoreSpecificThan(Level.WARN)) {
                    captured.add(event.toImmutable());
                }
            }
        };
        listAppender.start();
        loggerCfg.addAppender(listAppender, Level.WARN, null);
        loggerCfg.setLevel(Level.WARN);
        ctx.updateLoggers();
        try {
            assertSame(searchResults, client.fetchContentForAnswer(searchResults, "install"),
                    "a fetcher failure must degrade to the raw search results");

            final LogEvent warn = captured.stream()
                    .filter(e -> loggerName.equals(e.getLoggerName()))
                    .filter(e -> e.getMessage().getFormattedMessage().contains("Failed to fetch answer content"))
                    .findFirst()
                    .orElse(null);
            assertNotNull(warn, "the degrade must be logged at WARN");
            assertNotNull(warn.getThrown(), "the degrade WARN must carry the Throwable; only a stack trace identifies the bug");
            assertEquals("boom", warn.getThrown().getMessage());
        } finally {
            loggerCfg.removeAppender("chat-client-degrade-appender");
            loggerCfg.setLevel(originalLevel);
            ctx.updateLoggers();
            listAppender.stop();
        }
    }
}
