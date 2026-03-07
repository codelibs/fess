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

    // ========== Testable subclass ==========

    static class TestableChatClient extends ChatClient {

        String testBuildAssistantHistoryContent(final ChatMessage msg, final String mode) {
            return buildAssistantHistoryContent(msg, mode);
        }

        List<LlmMessage> testExtractHistory(final ChatSession session) {
            return extractHistory(session);
        }
    }
}
