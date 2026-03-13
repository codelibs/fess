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
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "full");
        assertEquals("Full response text here.", result);
    }

    @Test
    public void test_buildAssistantHistoryContent_none() {
        final ChatMessage msg = ChatMessage.assistantMessage("Any content");
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "none");
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

        final String result = chatClient.testBuildAssistantHistoryContent(msg, "source_titles");
        assertEquals("Response text\n[Referenced documents: Installation Guide, Quick Start]", result);
    }

    @Test
    public void test_buildAssistantHistoryContent_sourceTitles_withoutSources() {
        final ChatMessage msg = ChatMessage.assistantMessage("Response without sources");
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "source_titles");
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

        final String result = chatClient.testBuildAssistantHistoryContent(msg, "source_titles_and_urls");
        assertEquals("[References: Install Guide (http://example.com/install)]", result);
    }

    @Test
    public void test_buildAssistantHistoryContent_truncated() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.max.chars".equals(key)) {
                    return "20";
                }
                return defaultValue;
            }
        });

        final ChatMessage msg = ChatMessage.assistantMessage("This is a long response that should be truncated.");
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "truncated");
        assertEquals("This is a long respo...", result);
    }

    @Test
    public void test_buildAssistantHistoryContent_truncated_shortContent() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.max.chars".equals(key)) {
                    return "500";
                }
                return defaultValue;
            }
        });

        final ChatMessage msg = ChatMessage.assistantMessage("Short");
        final String result = chatClient.testBuildAssistantHistoryContent(msg, "truncated");
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

        final List<LlmMessage> history = chatClient.testExtractHistory(session);
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

        final List<LlmMessage> history = chatClient.testExtractHistory(session);
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

        final List<LlmMessage> history = chatClient.testExtractHistory(session);
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

        final List<LlmMessage> history = chatClient.testExtractHistory(session);
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

        final List<LlmMessage> history = chatClient.testExtractHistory(session);
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

        final List<LlmMessage> history = chatClient.testExtractHistory(session);
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

        final List<LlmMessage> history = chatClient.testExtractHistory(session);
        assertEquals(2, history.size());
        assertEquals("assistant", history.get(1).getRole());
        assertEquals("[References: Guide (http://example.com/guide)]", history.get(1).getContent());
    }

    @Test
    public void test_extractHistory_truncatedMode() {
        ComponentUtil.setFessConfig(new FessConfig.SimpleImpl() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getOrDefault(final String key, final String defaultValue) {
                if ("rag.chat.history.assistant.content".equals(key)) {
                    return "truncated";
                }
                if ("rag.chat.history.assistant.max.chars".equals(key)) {
                    return "10";
                }
                return defaultValue;
            }
        });

        final ChatSession session = new ChatSession();
        session.addUserMessage("Q1");
        session.addAssistantMessage("This is a very long response text that should be truncated");

        final List<LlmMessage> history = chatClient.testExtractHistory(session);
        assertEquals(2, history.size());
        assertEquals("assistant", history.get(1).getRole());
        assertEquals("This is a ...", history.get(1).getContent());
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

    // ========== Testable subclass ==========

    static class TestableChatClient extends ChatClient {

        private boolean searchDocumentsCalled = false;

        String testBuildAssistantHistoryContent(final ChatMessage msg, final String mode) {
            return buildAssistantHistoryContent(msg, mode);
        }

        List<LlmMessage> testExtractHistory(final ChatSession session) {
            return extractHistory(session);
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

        @Override
        protected List<Map<String, Object>> searchDocuments(final String query) {
            searchDocumentsCalled = true;
            return Collections.emptyList();
        }

        boolean wasSearchDocumentsCalled() {
            return searchDocumentsCalled;
        }

        void resetSearchDocumentsCalled() {
            searchDocumentsCalled = false;
        }
    }
}
