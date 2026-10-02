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
package org.codelibs.fess.opensearch.log.exbhv;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.opensearch.log.exentity.ChatLog;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ChatLogBhvTest extends UnitFessTestCase {

    @Test
    public void test_toSource() {
        final LocalDateTime requestedAt = LocalDateTime.of(2026, 9, 30, 12, 34, 56);
        final ChatLog chatLog = newChatLog(requestedAt);

        final Map<String, Object> source = chatLog.toSource();
        assertEquals(DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.of(requestedAt, ZoneId.systemDefault())),
                source.get("requestedAt"));
        assertEquals("taro", source.get("user"));
        assertEquals("code-1", source.get("userSessionId"));
        assertEquals(List.of("Ruser"), List.of((String[]) source.get("roles")));
        assertEquals("stream", source.get("accessType"));
        assertEquals("chat", source.get("chatType"));
        assertEquals("sess-1", source.get("chatSessionId"));
        assertEquals("q-1", source.get("searchQueryId"));
        assertEquals("search", source.get("intent"));
        assertEquals("success", source.get("status"));
        assertEquals("my-llm", source.get("llmName"));
        assertEquals("model-a", source.get("model"));
        assertEquals(3, source.get("llmCalls"));
        assertEquals(100L, source.get("promptTokens"));
        assertEquals(20L, source.get("completionTokens"));
        assertEquals(120L, source.get("totalTokens"));
        assertEquals(1500L, source.get("responseTime"));
        assertEquals(4, source.get("sourceCount"));
        assertEquals("", source.get("virtualHost"));
        // unset values are not written
        assertFalse(source.containsKey("errorCode"));
        assertEquals(19, source.size());
        assertEquals("chat", chatLog.getEventType());
    }

    @Test
    public void test_toSource_withoutUsage() {
        final ChatLog chatLog = new ChatLog();
        chatLog.setStatus(ChatLog.STATUS_ERROR);
        chatLog.setErrorCode("timeout");
        chatLog.setLlmCalls(0);
        final Map<String, Object> source = chatLog.toSource();
        assertEquals(Map.of("status", "error", "errorCode", "timeout", "llmCalls", 0), source);
    }

    @Test
    public void test_roundTrip() {
        final LocalDateTime requestedAt = LocalDateTime.of(2026, 9, 30, 12, 34, 56);
        final ChatLog restored = new TestChatLogBhv().create(newChatLog(requestedAt).toSource());
        assertEquals(requestedAt, restored.getRequestedAt());
        assertEquals("taro", restored.getUser());
        assertEquals(List.of("Ruser"), List.of(restored.getRoles()));
        assertEquals(Integer.valueOf(3), restored.getLlmCalls());
        assertEquals(Long.valueOf(120L), restored.getTotalTokens());
        assertEquals(Integer.valueOf(4), restored.getSourceCount());
        assertEquals("model-a", restored.getModel());
        assertNull(restored.getErrorCode());
    }

    private static ChatLog newChatLog(final LocalDateTime requestedAt) {
        final ChatLog chatLog = new ChatLog();
        chatLog.setRequestedAt(requestedAt);
        chatLog.setUser("taro");
        chatLog.setUserSessionId("code-1");
        chatLog.setRoles(new String[] { "Ruser" });
        chatLog.setVirtualHost("");
        chatLog.setAccessType(ChatLog.ACCESS_TYPE_STREAM);
        chatLog.setChatType(ChatLog.CHAT_TYPE_CHAT);
        chatLog.setChatSessionId("sess-1");
        chatLog.setSearchQueryId("q-1");
        chatLog.setIntent("search");
        chatLog.setStatus(ChatLog.STATUS_SUCCESS);
        chatLog.setLlmName("my-llm");
        chatLog.setModel("model-a");
        chatLog.setLlmCalls(3);
        chatLog.setPromptTokens(100L);
        chatLog.setCompletionTokens(20L);
        chatLog.setTotalTokens(120L);
        chatLog.setResponseTime(1500L);
        chatLog.setSourceCount(4);
        return chatLog;
    }

    private static class TestChatLogBhv extends ChatLogBhv {
        ChatLog create(final Map<String, Object> source) {
            return createEntity(source, ChatLog.class);
        }
    }
}
