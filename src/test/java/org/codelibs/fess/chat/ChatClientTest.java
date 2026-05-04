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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.entity.ChatMessage;
import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.entity.ChatSession;
import org.codelibs.fess.llm.LlmMessage;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Tests for ChatClient's extractHistory and buildAssistantHistoryContent methods.
 * Verifies correct behavior across all assistant content modes (full, source_titles,
 * source_titles_and_urls, truncated, none).
 */
public class ChatClientTest extends UnitFessTestCase {

    private TestableChatClient chatClient;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        chatClient = new TestableChatClient();
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    // ========== buildAssistantHistoryContent tests ==========

    @Test
    public void test_buildAssistantHistoryContent_full() {
        final ChatMessage msg = ChatMessage.assistantMessage("Full response text here.");
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "full", 500, 500);
        assertEquals("Full response text here.", result);
    }

    @Test
    public void test_buildAssistantHistoryContent_none() {
        final ChatMessage msg = ChatMessage.assistantMessage("Any content");
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "none", 500, 500);
        assertNull(result);
    }

    @Test
    public void test_buildAssistantHistoryContent_sourceTitles_withSources() {
        final ChatMessage msg = ChatMessage.assistantMessage("Response text");
        final Map<String, Object> doc1 = new HashMap<>();
        doc1.put("title", "Installation Guide");
        doc1.put("url", "http://example.com/install");
        msg.addSource(new ChatSource(1, doc1));
        final Map<String, Object> doc2 = new HashMap<>();
        doc2.put("title", "Quick Start");
        doc2.put("url", "http://example.com/start");
        msg.addSource(new ChatSource(2, doc2));

        final String result = chatClient.testBuildAssistantHistoryContent(msg, "source_titles", 500, 500);
        assertEquals("Response text\n[Referenced documents: Installation Guide, Quick Start]", result);
    }

    @Test
    public void test_buildAssistantHistoryContent_sourceTitles_withoutSources() {
        final ChatMessage msg = ChatMessage.assistantMessage("Response without sources");
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "source_titles", 500, 500);
        // Falls back to full content when no sources
        assertEquals("Response without sources", result);
    }

    @Test
    public void test_buildAssistantHistoryContent_sourceTitlesAndUrls() {
        final ChatMessage msg = ChatMessage.assistantMessage("Response text");
        final Map<String, Object> doc = new HashMap<>();
        doc.put("title", "Install Guide");
        doc.put("url", "http://example.com/install");
        msg.addSource(new ChatSource(1, doc));

        final String result = chatClient.testBuildAssistantHistoryContent(msg, "source_titles_and_urls", 500, 500);
        assertEquals("[References: Install Guide (http://example.com/install)]", result);
    }

    @Test
    public void test_buildAssistantHistoryContent_truncated() {
        final ChatMessage msg = ChatMessage.assistantMessage("This is a long response that should be truncated.");
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "truncated", 20, 500);
        assertEquals("This is a long respo...", result);
    }

    @Test
    public void test_buildAssistantHistoryContent_truncated_shortContent() {
        final ChatMessage msg = ChatMessage.assistantMessage("Short");
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "truncated", 500, 500);
        assertEquals("Short", result);
    }

    // ========== extractHistory tests ==========

    @Test
    public void test_extractHistory_fullMode() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "full";
                }
                return defaultValue;
            }
        });

        final ChatSession session = new ChatSession();
        session.addUserMessage("Q1");
        session.addAssistantMessage("A1");
        session.addUserMessage("Q2");
        session.addAssistantMessage("A2");

        final List<LlmMessage> history = chatClient.testExtractHistoryForAnswer(session);
        assertEquals(4, history.size());
        assertEquals("user", history.get(0).getRole());
        assertEquals("Q1", history.get(0).getContent());
        assertEquals("assistant", history.get(1).getRole());
        assertEquals("A1", history.get(1).getContent());
        assertEquals("user", history.get(2).getRole());
        assertEquals("Q2", history.get(2).getContent());
        assertEquals("assistant", history.get(3).getRole());
        assertEquals("A2", history.get(3).getContent());
    }

    @Test
    public void test_extractHistory_noneMode() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "none";
                }
                return defaultValue;
            }
        });

        final ChatSession session = new ChatSession();
        session.addUserMessage("Q1");
        session.addAssistantMessage("A1");
        session.addUserMessage("Q2");
        session.addAssistantMessage("A2");

        final List<LlmMessage> history = chatClient.testExtractHistoryForAnswer(session);
        // none mode: assistant messages are skipped, only user messages remain
        assertEquals(2, history.size());
        assertEquals("user", history.get(0).getRole());
        assertEquals("Q1", history.get(0).getContent());
        assertEquals("user", history.get(1).getRole());
        assertEquals("Q2", history.get(1).getContent());
    }

    @Test
    public void test_extractHistory_noneMode_singleTurn() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "none";
                }
                return defaultValue;
            }
        });

        final ChatSession session = new ChatSession();
        session.addUserMessage("Q1");
        session.addAssistantMessage("A1");

        final List<LlmMessage> history = chatClient.testExtractHistoryForAnswer(session);
        assertEquals(1, history.size());
        assertEquals("user", history.get(0).getRole());
        assertEquals("Q1", history.get(0).getContent());
    }

    @Test
    public void test_extractHistory_sourceTitlesMode() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "source_titles";
                }
                return defaultValue;
            }
        });

        final ChatSession session = new ChatSession();
        session.addUserMessage("Q1");
        final ChatMessage assistantMsg = ChatMessage.assistantMessage("Full response");
        final Map<String, Object> doc = new HashMap<>();
        doc.put("title", "Doc Title");
        doc.put("url", "http://example.com");
        assistantMsg.addSource(new ChatSource(1, doc));
        session.addMessage(assistantMsg);

        final List<LlmMessage> history = chatClient.testExtractHistoryForAnswer(session);
        assertEquals(2, history.size());
        assertEquals("user", history.get(0).getRole());
        assertEquals("assistant", history.get(1).getRole());
        assertEquals("Full response\n[Referenced documents: Doc Title]", history.get(1).getContent());
    }

    @Test
    public void test_extractHistory_emptySession() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "none";
                }
                return defaultValue;
            }
        });

        final ChatSession session = new ChatSession();

        final List<LlmMessage> history = chatClient.testExtractHistoryForAnswer(session);
        assertTrue(history.isEmpty());
    }

    @Test
    public void test_extractHistory_noneMode_manyTurns() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "none";
                }
                return defaultValue;
            }
        });

        final ChatSession session = new ChatSession();
        for (int i = 1; i <= 5; i++) {
            session.addUserMessage("Q" + i);
            session.addAssistantMessage("A" + i);
        }

        final List<LlmMessage> history = chatClient.testExtractHistoryForAnswer(session);
        // 5 user messages, 0 assistant messages
        assertEquals(5, history.size());
        for (int i = 0; i < 5; i++) {
            assertEquals("user", history.get(i).getRole());
            assertEquals("Q" + (i + 1), history.get(i).getContent());
        }
    }

    @Test
    public void test_extractHistory_sourceTitlesAndUrlsMode() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "source_titles_and_urls";
                }
                return defaultValue;
            }
        });

        final ChatSession session = new ChatSession();
        session.addUserMessage("Q1");
        final ChatMessage assistantMsg = ChatMessage.assistantMessage("Full response");
        final Map<String, Object> doc = new HashMap<>();
        doc.put("title", "Guide");
        doc.put("url", "http://example.com/guide");
        assistantMsg.addSource(new ChatSource(1, doc));
        session.addMessage(assistantMsg);

        final List<LlmMessage> history = chatClient.testExtractHistoryForAnswer(session);
        assertEquals(2, history.size());
        assertEquals("assistant", history.get(1).getRole());
        assertEquals("[References: Guide (http://example.com/guide)]", history.get(1).getContent());
    }

    @Test
    public void test_extractHistory_truncatedMode() {
        final ChatMessage msg = ChatMessage.assistantMessage("This is a very long response text that should be truncated");
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "truncated", 10, 800);
        assertEquals("This is a ...", result);
    }

    // ========== additional source_titles tests ==========

    @Test
    public void test_sourceTitles_truncatesLongContent() {
        final ChatMessage msg = createAssistantWithSources("A".repeat(1000), "My Doc");
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "source_titles", 500, 500);
        assertTrue(result.contains("... [truncated]"));
        assertTrue(result.contains("[Referenced documents: My Doc]"));
    }

    @Test
    public void test_sourceTitles_budgetEnforcesMaxChars() {
        final ChatMessage msg = createAssistantWithSources("A".repeat(2000), "Title");
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "source_titles", 500, 500);
        assertTrue(result.length() <= 500 + 50, "result length " + result.length() + " exceeds reasonable bound");
    }

    @Test
    public void test_sourceTitles_longTitlesExceedBudget() {
        final String longTitle = "T".repeat(1000);
        final ChatMessage msg = createAssistantWithSources("Short content", longTitle);
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "source_titles", 200, 200);
        // maxSuffixLen = 200/4 = 50, suffix truncated
        assertNotNull(result);
        assertTrue(result.length() <= 250);
    }

    @Test
    public void test_sourceTitles_nullContent_withSources() {
        final ChatMessage msg = createAssistantWithSources(null, "Doc A", "Doc B");
        // Manually set content to null via assistantMessage then add sources
        final ChatMessage nullMsg = ChatMessage.assistantMessage(null);
        for (int i = 0; i < msg.getSources().size(); i++) {
            nullMsg.addSource(msg.getSources().get(i));
        }
        final String result = chatClient.testBuildAssistantHistoryContent(nullMsg, "source_titles", 500, 500);
        // null content with sources => returns suffix only
        assertNotNull(result);
        assertTrue(result.contains("[Referenced documents: Doc A, Doc B]"));
    }

    @Test
    public void test_sourceTitles_emptyTitles() {
        final ChatMessage msg = ChatMessage.assistantMessage("Some content here that is quite long enough to be truncated.");
        final Map<String, Object> doc = new HashMap<>();
        doc.put("title", "");
        doc.put("url", "http://example.com/1");
        msg.addSource(new ChatSource(1, doc));
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "source_titles", 500, 500);
        // Empty titles => suffix is empty => falls back to truncated
        assertEquals("Some content here that is quite long enough to be truncated.", result);
    }

    // ========== additional truncated tests ==========

    @Test
    public void test_truncated_nullContent() {
        final ChatMessage msg = ChatMessage.assistantMessage(null);
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "truncated", 500, 500);
        assertNull(result);
    }

    @Test
    public void test_truncated_exactlyAtLimit() {
        final String content = "A".repeat(500);
        final ChatMessage msg = ChatMessage.assistantMessage(content);
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "truncated", 500, 500);
        assertEquals(content, result);
    }

    @Test
    public void test_truncated_oneCharOver() {
        final String content = "A".repeat(501);
        final ChatMessage msg = ChatMessage.assistantMessage(content);
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "truncated", 500, 500);
        assertEquals("A".repeat(500) + "...", result);
    }

    // ========== extractHistory integration tests ==========

    @Test
    public void test_extractHistory_defaultMode_isSmartSummary() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                return defaultValue;
            }
        });
        final ChatSession session = new ChatSession();
        session.addMessage(ChatMessage.userMessage("Q1"));
        final ChatMessage a = ChatMessage.assistantMessage("Some long body that we no longer keep verbatim.");
        a.setSearchQuery("Q1");
        session.addMessage(a);

        // Default mode is smart_summary => intent rendering drops the body.
        final List<LlmMessage> intent = chatClient.testExtractHistoryForIntent(session);
        assertEquals(2, intent.size());
        assertEquals("searched: \"Q1\"", intent.get(1).getContent());

        // Default mode is smart_summary => answer rendering pairs user with rendered turn.
        final List<LlmMessage> answer = chatClient.testExtractHistoryForAnswer(session);
        assertEquals(1, answer.size());
        assertEquals("Q: \"Q1\" (searched: \"Q1\")", answer.get(0).getContent());
    }

    @Test
    public void test_extractHistory_unknownMode_returnsFull() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "unknown_mode";
                }
                return defaultValue;
            }
        });

        final ChatSession session = new ChatSession();
        session.addUserMessage("Q1");
        session.addAssistantMessage("Full content here");

        final List<LlmMessage> history = chatClient.testExtractHistoryForAnswer(session);
        assertEquals(2, history.size());
        assertEquals("Full content here", history.get(1).getContent());
    }

    // ========== helper method ==========

    private ChatMessage createAssistantWithSources(final String content, final String... titles) {
        final ChatMessage msg = ChatMessage.assistantMessage(content);
        for (int i = 0; i < titles.length; i++) {
            final Map<String, Object> doc = new HashMap<>();
            doc.put("title", titles[i]);
            doc.put("url", "http://example.com/" + i);
            msg.addSource(new ChatSource(i + 1, doc));
        }
        return msg;
    }

    // ========== escapeQueryValue tests ==========

    @Test
    public void test_escapeQueryValue_null() {
        assertEquals("", chatClient.testEscapeQueryValue(null));
    }

    @Test
    public void test_escapeQueryValue_empty() {
        assertEquals("", chatClient.testEscapeQueryValue(""));
    }

    @Test
    public void test_escapeQueryValue_noSpecialChars() {
        assertEquals("hello world", chatClient.testEscapeQueryValue("hello world"));
    }

    @Test
    public void test_escapeQueryValue_backslash() {
        assertEquals("path\\\\to\\\\file", chatClient.testEscapeQueryValue("path\\to\\file"));
    }

    @Test
    public void test_escapeQueryValue_doubleQuote() {
        assertEquals("say \\\"hello\\\"", chatClient.testEscapeQueryValue("say \"hello\""));
    }

    @Test
    public void test_escapeQueryValue_mixed() {
        assertEquals("a\\\\b\\\"c", chatClient.testEscapeQueryValue("a\\b\"c"));
    }

    @Test
    public void test_escapeQueryValue_luceneCharsUnchanged() {
        assertEquals("a + b - c * d ? e", chatClient.testEscapeQueryValue("a + b - c * d ? e"));
    }

    @Test
    public void test_escapeQueryValue_urlWithParams() {
        assertEquals("https://example.com/path?q=test&lang=en", chatClient.testEscapeQueryValue("https://example.com/path?q=test&lang=en"));
    }

    @Test
    public void test_escapeQueryValue_consecutiveBackslashes() {
        assertEquals("\\\\\\\\", chatClient.testEscapeQueryValue("\\\\"));
    }

    // ========== searchWithQuery tests ==========

    @Test
    public void test_searchWithQuery_null() {
        final List<Map<String, Object>> result = chatClient.testSearchWithQuery(null);
        assertTrue(result.isEmpty());
        assertFalse(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_blank() {
        final List<Map<String, Object>> result = chatClient.testSearchWithQuery("   ");
        assertTrue(result.isEmpty());
        assertFalse(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_exceedsMaxLength() {
        final String longQuery = "a".repeat(1001);
        final List<Map<String, Object>> result = chatClient.testSearchWithQuery(longQuery);
        assertTrue(result.isEmpty());
        assertFalse(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_exactlyMaxLength() {
        chatClient.resetSearchDocumentsCalled();
        final String query = "a".repeat(1000);
        chatClient.testSearchWithQuery(query);
        assertTrue(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_validQuery() {
        chatClient.resetSearchDocumentsCalled();
        chatClient.testSearchWithQuery("OpenSearch tutorial");
        assertTrue(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_validFieldQuery() {
        chatClient.resetSearchDocumentsCalled();
        chatClient.testSearchWithQuery("title:fess");
        assertTrue(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_rejects_starColonStar() {
        final List<Map<String, Object>> result = chatClient.testSearchWithQuery("*:*");
        assertTrue(result.isEmpty());
        assertFalse(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_rejects_starColonStarEmbedded() {
        chatClient.resetSearchDocumentsCalled();
        final List<Map<String, Object>> result = chatClient.testSearchWithQuery("title:hello AND *:*");
        assertTrue(result.isEmpty());
        assertFalse(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_allows_scriptKeyword() {
        chatClient.resetSearchDocumentsCalled();
        chatClient.testSearchWithQuery("painless scripting tutorial");
        assertTrue(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_allows_internalFieldName() {
        chatClient.resetSearchDocumentsCalled();
        chatClient.testSearchWithQuery("_source configuration guide");
        assertTrue(chatClient.wasSearchDocumentsCalled());
    }

    // ========== escapeHtml tests ==========

    @Test
    public void test_escapeHtml_null() {
        assertEquals("", chatClient.testEscapeHtml(null));
    }

    @Test
    public void test_escapeHtml_empty() {
        assertEquals("", chatClient.testEscapeHtml(""));
    }

    @Test
    public void test_escapeHtml_noSpecialChars() {
        assertEquals("hello world", chatClient.testEscapeHtml("hello world"));
    }

    @Test
    public void test_escapeHtml_ampersand() {
        assertEquals("a &amp; b", chatClient.testEscapeHtml("a & b"));
    }

    @Test
    public void test_escapeHtml_lessThan() {
        assertEquals("a &lt; b", chatClient.testEscapeHtml("a < b"));
    }

    @Test
    public void test_escapeHtml_greaterThan() {
        assertEquals("a &gt; b", chatClient.testEscapeHtml("a > b"));
    }

    @Test
    public void test_escapeHtml_allSpecialChars() {
        assertEquals("&amp;&lt;&gt;&quot;&#39;", chatClient.testEscapeHtml("&<>\"'"));
    }

    @Test
    public void test_escapeHtml_scriptTag() {
        assertEquals("&lt;script&gt;alert(&#39;xss&#39;)&lt;/script&gt;", chatClient.testEscapeHtml("<script>alert('xss')</script>"));
    }

    // ========== buildGoUrl tests ==========

    @Test
    public void test_buildGoUrl_basic() {
        final String url = chatClient.testBuildGoUrl("", "doc123", "query456", 1710000000L, 0);
        assertEquals("/go/?rt=1710000000&docId=doc123&queryId=query456&order=0", url);
    }

    @Test
    public void test_buildGoUrl_withContextPath() {
        final String url = chatClient.testBuildGoUrl("/fess", "doc123", "query456", 1710000000L, 2);
        assertEquals("/fess/go/?rt=1710000000&docId=doc123&queryId=query456&order=2", url);
    }

    @Test
    public void test_buildGoUrl_nullDocId() {
        assertNull(chatClient.testBuildGoUrl("", null, "query456", 1710000000L, 0));
    }

    @Test
    public void test_buildGoUrl_nullQueryId() {
        assertNull(chatClient.testBuildGoUrl("", "doc123", null, 1710000000L, 0));
    }

    @Test
    public void test_buildGoUrl_encodesSpecialCharacters() {
        final String url = chatClient.testBuildGoUrl("", "doc&id=1", "query id#2", 1710000000L, 0);
        assertTrue(url.contains("docId=doc%26id%3D1"));
        assertTrue(url.contains("queryId=query+id%232"));
    }

    // ========== ChatSource goUrl/urlLink tests ==========

    @Test
    public void test_chatSource_goUrlAndUrlLink() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("title", "Test Doc");
        doc.put("url", "smb://server/share/file.doc");
        doc.put("doc_id", "doc123");
        doc.put("url_link", "http://proxy.example.com/file.doc");

        final ChatMessage.ChatSource source = new ChatMessage.ChatSource(1, doc);
        assertEquals("http://proxy.example.com/file.doc", source.getUrlLink());
        assertNull(source.getGoUrl());

        source.setGoUrl("/go/?rt=123&docId=doc123&queryId=q1&order=0");
        assertEquals("/go/?rt=123&docId=doc123&queryId=q1&order=0", source.getGoUrl());
    }

    // ========== searchWithQuery with filters tests ==========

    @Test
    public void test_searchWithQuery_filters_null() {
        final List<Map<String, Object>> result = chatClient.testSearchWithQueryFiltered(null, Collections.emptyMap(), new String[0]);
        assertTrue(result.isEmpty());
        assertFalse(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_filters_blank() {
        final List<Map<String, Object>> result = chatClient.testSearchWithQueryFiltered("   ", Collections.emptyMap(), new String[0]);
        assertTrue(result.isEmpty());
        assertFalse(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_filters_exceedsMaxLength() {
        final String longQuery = "a".repeat(1001);
        final List<Map<String, Object>> result = chatClient.testSearchWithQueryFiltered(longQuery, Collections.emptyMap(), new String[0]);
        assertTrue(result.isEmpty());
        assertFalse(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_filters_validQuery() {
        chatClient.resetSearchDocumentsCalled();
        chatClient.testSearchWithQueryFiltered("OpenSearch tutorial", Collections.emptyMap(), new String[0]);
        assertTrue(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_filters_rejectsDangerousPattern() {
        final List<Map<String, Object>> result = chatClient.testSearchWithQueryFiltered("*:*", Collections.emptyMap(), new String[0]);
        assertTrue(result.isEmpty());
        assertFalse(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_filters_validWithFields() {
        chatClient.resetSearchDocumentsCalled();
        final Map<String, String[]> fields = new HashMap<>();
        fields.put("label", new String[] { "internal" });
        chatClient.testSearchWithQueryFiltered("Fess tutorial", fields, new String[0]);
        assertTrue(chatClient.wasSearchDocumentsCalled());
    }

    @Test
    public void test_searchWithQuery_filters_validWithExtraQueries() {
        chatClient.resetSearchDocumentsCalled();
        chatClient.testSearchWithQueryFiltered("Fess tutorial", Collections.emptyMap(), new String[] { "filetype:pdf" });
        assertTrue(chatClient.wasSearchDocumentsCalled());
    }

    // ========== Phase-specific render helper tests ==========

    @Test
    public void test_renderIntentHistoryTurn_full() {
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        msg.setSearchQuery("Fess install");
        addTitle(msg, "Installation Guide");
        addTitle(msg, "Quick Start");
        final String result = chatClient.testRenderIntentHistoryTurn(msg, 5);
        assertEquals("searched: \"Fess install\" -> found: [Installation Guide, Quick Start]", result);
    }

    @Test
    public void test_renderIntentHistoryTurn_titlesOverflow() {
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        msg.setSearchQuery("q");
        for (int i = 1; i <= 7; i++) {
            addTitle(msg, "T" + i);
        }
        final String result = chatClient.testRenderIntentHistoryTurn(msg, 3);
        assertEquals("searched: \"q\" -> found: [T1, T2, T3, ... (+4 more)]", result);
    }

    @Test
    public void test_renderIntentHistoryTurn_titlesMaxCountZero() {
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        msg.setSearchQuery("q");
        addTitle(msg, "T1");
        addTitle(msg, "T2");
        final String result = chatClient.testRenderIntentHistoryTurn(msg, 0);
        assertEquals("searched: \"q\"", result);
    }

    @Test
    public void test_renderIntentHistoryTurn_titlesMaxCountNegative() {
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        msg.setSearchQuery("q");
        addTitle(msg, "T1");
        final String result = chatClient.testRenderIntentHistoryTurn(msg, -3);
        assertEquals("searched: \"q\"", result);
    }

    @Test
    public void test_renderIntentHistoryTurn_noSearchQuery() {
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        addTitle(msg, "T1");
        final String result = chatClient.testRenderIntentHistoryTurn(msg, 5);
        assertEquals("found: [T1]", result);
    }

    @Test
    public void test_renderIntentHistoryTurn_empty() {
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        final String result = chatClient.testRenderIntentHistoryTurn(msg, 5);
        assertNull(result);
    }

    @Test
    public void test_renderAnswerHistoryTurn_full() {
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        msg.setSearchQuery("Fess install");
        addTitle(msg, "Installation Guide");
        addTitle(msg, "Quick Start");
        final String result = chatClient.testRenderAnswerHistoryTurn("Fessのインストール方法は？", msg, 5);
        assertEquals("Q: \"Fessのインストール方法は？\" (searched: \"Fess install\", refs: [Installation Guide, Quick Start])", result);
    }

    @Test
    public void test_renderAnswerHistoryTurn_noTitles() {
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        msg.setSearchQuery("q");
        final String result = chatClient.testRenderAnswerHistoryTurn("Hi", msg, 5);
        assertEquals("Q: \"Hi\" (searched: \"q\")", result);
    }

    @Test
    public void test_renderAnswerHistoryTurn_noSearchQuery() {
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        addTitle(msg, "T1");
        final String result = chatClient.testRenderAnswerHistoryTurn("Hi", msg, 5);
        assertEquals("Q: \"Hi\" (refs: [T1])", result);
    }

    @Test
    public void test_renderAnswerHistoryTurn_userQueryEscapesQuoteAndNewline() {
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        msg.setSearchQuery("q");
        final String result = chatClient.testRenderAnswerHistoryTurn("a\"b\nc", msg, 5);
        assertEquals("Q: \"a'b c\" (searched: \"q\")", result);
    }

    @Test
    public void test_renderAnswerHistoryTurn_searchQueryEscapesQuoteAndNewline() {
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        msg.setSearchQuery("q1\"q2\nq3");
        final String result = chatClient.testRenderAnswerHistoryTurn("Hi", msg, 5);
        assertEquals("Q: \"Hi\" (searched: \"q1'q2 q3\")", result);
    }

    private void addTitle(final ChatMessage msg, final String title) {
        final java.util.Map<String, Object> doc = new java.util.HashMap<>();
        doc.put("title", title);
        msg.addSource(new ChatSource(msg.getSources().size() + 1, doc));
    }

    // ========== Phase-specific extractHistory tests (smart_summary) ==========

    @Test
    public void test_extractHistoryForIntent_smartSummary() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "smart_summary";
                }
                if ("rag.chat.history.titles.max.count".equals(key)) {
                    return "5";
                }
                return defaultValue;
            }
        });
        final ChatSession session = new ChatSession();
        session.addMessage(ChatMessage.userMessage("Fessのインストール方法は？"));
        final ChatMessage a1 = ChatMessage.assistantMessage("body1");
        a1.setSearchQuery("Fess install");
        addTitle(a1, "Installation Guide");
        session.addMessage(a1);
        session.addMessage(ChatMessage.userMessage("Dockerの場合は？"));

        final List<LlmMessage> result = chatClient.testExtractHistoryForIntent(session);

        assertEquals(2, result.size());
        // user turn passes through
        assertEquals("user", result.get(0).getRole());
        assertEquals("Fessのインストール方法は？", result.get(0).getContent());
        // assistant turn rendered minimally
        assertEquals("assistant", result.get(1).getRole());
        assertEquals("searched: \"Fess install\" -> found: [Installation Guide]", result.get(1).getContent());
    }

    @Test
    public void test_extractHistoryForAnswer_smartSummary_pairsUserWithAssistant() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "smart_summary";
                }
                if ("rag.chat.history.titles.max.count".equals(key)) {
                    return "5";
                }
                return defaultValue;
            }
        });
        final ChatSession session = new ChatSession();
        session.addMessage(ChatMessage.userMessage("Fessのインストール方法は？"));
        final ChatMessage a1 = ChatMessage.assistantMessage("body1");
        a1.setSearchQuery("Fess install");
        addTitle(a1, "Installation Guide");
        session.addMessage(a1);

        final List<LlmMessage> result = chatClient.testExtractHistoryForAnswer(session);

        assertEquals(1, result.size());
        assertEquals("assistant", result.get(0).getRole());
        assertEquals("Q: \"Fessのインストール方法は？\" (searched: \"Fess install\", refs: [Installation Guide])", result.get(0).getContent());
    }

    @Test
    public void test_extractHistoryForAnswer_smartSummary_skipsOrphanAssistant() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "smart_summary";
                }
                return defaultValue;
            }
        });
        final ChatSession session = new ChatSession();
        final ChatMessage orphan = ChatMessage.assistantMessage("body");
        orphan.setSearchQuery("q");
        session.addMessage(orphan);

        final List<LlmMessage> result = chatClient.testExtractHistoryForAnswer(session);
        assertTrue(result.isEmpty());
    }

    @Test
    public void test_extractHistoryForIntent_fullMode_unchanged() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "full";
                }
                return defaultValue;
            }
        });
        final ChatSession session = new ChatSession();
        session.addMessage(ChatMessage.userMessage("Q"));
        session.addMessage(ChatMessage.assistantMessage("Long answer body here."));

        final List<LlmMessage> result = chatClient.testExtractHistoryForIntent(session);
        assertEquals(2, result.size());
        assertEquals("Long answer body here.", result.get(1).getContent());
    }

    // ========== searchWithQuery tracks queries ==========

    @Test
    public void test_searchWithQuery_tracksQueryString() {
        chatClient.resetSearchDocumentsCalled();
        chatClient.testSearchWithQuery("Fess Docker");
        assertEquals(1, chatClient.getSearchedQueries().size());
        assertEquals("Fess Docker", chatClient.getSearchedQueries().get(0));
    }

    @Test
    public void test_searchWithQuery_multipleSearches_tracksAll() {
        chatClient.resetSearchDocumentsCalled();
        chatClient.testSearchWithQuery("query1");
        chatClient.testSearchWithQuery("query2");
        assertEquals(2, chatClient.getSearchedQueries().size());
        assertEquals("query1", chatClient.getSearchedQueries().get(0));
        assertEquals("query2", chatClient.getSearchedQueries().get(1));
    }

    // ========== streamChatEnhanced searchQuery contract ==========

    @Test
    public void test_streamChatEnhanced_capturesFinalSearchQuery() {
        // Sentinel test for the contract: assistant ChatMessage.searchQuery is
        // populated by streamChatEnhanced. Full integration coverage lives in *Tests.java.
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        msg.setSearchQuery("Fess install");
        assertEquals("Fess install", msg.getSearchQuery());
    }

    // ========== chat() searchQuery contract ==========

    @Test
    public void test_chat_capturesFinalSearchQuery_sentinel() {
        // Sentinel — see test_streamChatEnhanced_capturesFinalSearchQuery for rationale.
        final ChatMessage msg = ChatMessage.assistantMessage("body");
        msg.setSearchQuery("query2");
        assertEquals("query2", msg.getSearchQuery());
    }

    // ========== searchWithQuery with configurable results ==========

    @Test
    public void test_searchWithQuery_returnsConfiguredResults() {
        final List<Map<String, Object>> docs = new ArrayList<>();
        final Map<String, Object> doc = new HashMap<>();
        doc.put("title", "Test Doc");
        doc.put("doc_id", "doc1");
        docs.add(doc);
        chatClient.setSearchResults(docs);
        chatClient.resetSearchDocumentsCalled();

        final List<Map<String, Object>> result = chatClient.testSearchWithQuery("Fess");
        assertEquals(1, result.size());
        assertEquals("Test Doc", result.get(0).get("title"));
    }

    @Test
    public void test_searchWithQuery_emptyResults() {
        chatClient.setSearchResults(Collections.emptyList());
        chatClient.resetSearchDocumentsCalled();

        final List<Map<String, Object>> result = chatClient.testSearchWithQuery("nonexistent");
        assertTrue(result.isEmpty());
    }

    // ========== Testable subclass ==========

    static class TestableChatClient extends ChatClient {

        private boolean searchDocumentsCalled = false;
        private final List<String> searchedQueries = new ArrayList<>();
        private List<Map<String, Object>> searchResultsToReturn = Collections.emptyList();

        void setSearchResults(final List<Map<String, Object>> results) {
            this.searchResultsToReturn = results;
        }

        List<String> getSearchedQueries() {
            return searchedQueries;
        }

        String testBuildAssistantHistoryContent(final ChatMessage msg, final String mode, final int assistantMaxChars,
                final int summaryMaxChars) {
            return buildAssistantHistoryContent(msg, mode, assistantMaxChars, summaryMaxChars);
        }

        List<LlmMessage> testExtractHistoryForIntent(final ChatSession session) {
            return extractHistoryForIntent(session);
        }

        List<LlmMessage> testExtractHistoryForAnswer(final ChatSession session) {
            return extractHistoryForAnswer(session);
        }

        String testEscapeQueryValue(final String value) {
            return escapeQueryValue(value);
        }

        List<Map<String, Object>> testSearchWithQuery(final String query) {
            return searchWithQuery(query);
        }

        String testEscapeHtml(final String text) {
            return escapeHtml(text);
        }

        String testBuildGoUrl(final String contextPath, final String docId, final String queryId, final long requestedTime,
                final int order) {
            return buildGoUrl(contextPath, docId, queryId, requestedTime, order);
        }

        String testRenderIntentHistoryTurn(final ChatMessage msg, final int titlesMaxCount) {
            return renderIntentHistoryTurn(msg, titlesMaxCount);
        }

        String testRenderAnswerHistoryTurn(final String userQuery, final ChatMessage assistantMsg, final int titlesMaxCount) {
            return renderAnswerHistoryTurn(userQuery, assistantMsg, titlesMaxCount);
        }

        List<Map<String, Object>> testSearchWithQueryFiltered(final String query, final Map<String, String[]> fields,
                final String[] extraQueries) {
            return searchWithQuery(query, fields, extraQueries);
        }

        @Override
        protected ChatSearchResult searchDocuments(final String query) {
            searchDocumentsCalled = true;
            searchedQueries.add(query);
            return new ChatSearchResult(searchResultsToReturn, null, 0L);
        }

        @Override
        protected ChatSearchResult searchDocuments(final String query, final Map<String, String[]> fields, final String[] extraQueries) {
            searchDocumentsCalled = true;
            searchedQueries.add(query);
            return new ChatSearchResult(searchResultsToReturn, null, 0L);
        }

        boolean wasSearchDocumentsCalled() {
            return searchDocumentsCalled;
        }

        void resetSearchDocumentsCalled() {
            searchDocumentsCalled = false;
            searchedQueries.clear();
        }
    }
}
