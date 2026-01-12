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
package org.codelibs.fess.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class ChatSessionTest extends UnitFessTestCase {

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void test_defaultConstructor() {
        final ChatSession session = new ChatSession();
        assertNotNull(session.getSessionId());
        assertNull(session.getUserId());
        assertNotNull(session.getCreatedAt());
        assertNotNull(session.getLastAccessedAt());
        assertNotNull(session.getMessages());
        assertTrue(session.getMessages().isEmpty());
    }

    @Test
    public void test_constructorWithUserId() {
        final ChatSession session = new ChatSession("user-123");
        assertNotNull(session.getSessionId());
        assertEquals("user-123", session.getUserId());
        assertNotNull(session.getCreatedAt());
        assertNotNull(session.getLastAccessedAt());
        assertNotNull(session.getMessages());
    }

    @Test
    public void test_settersAndGetters() {
        final ChatSession session = new ChatSession();
        final LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 10, 0);
        final LocalDateTime lastAccessedAt = LocalDateTime.of(2025, 1, 1, 11, 0);
        final List<ChatMessage> messages = new ArrayList<>();

        session.setSessionId("session-456");
        session.setUserId("user-789");
        session.setCreatedAt(createdAt);
        session.setLastAccessedAt(lastAccessedAt);
        session.setMessages(messages);

        assertEquals("session-456", session.getSessionId());
        assertEquals("user-789", session.getUserId());
        assertEquals(createdAt, session.getCreatedAt());
        assertEquals(lastAccessedAt, session.getLastAccessedAt());
        assertSame(messages, session.getMessages());
    }

    @Test
    public void test_addMessage() {
        final ChatSession session = new ChatSession();
        final LocalDateTime beforeAdd = session.getLastAccessedAt();

        final ChatMessage message = ChatMessage.userMessage("Hello");
        session.addMessage(message);

        assertEquals(1, session.getMessages().size());
        assertEquals("Hello", session.getMessages().get(0).getContent());
        assertTrue(session.getLastAccessedAt().isAfter(beforeAdd) || session.getLastAccessedAt().isEqual(beforeAdd));
    }

    @Test
    public void test_addMessageWithNullMessages() {
        final ChatSession session = new ChatSession();
        session.setMessages(null);

        final ChatMessage message = ChatMessage.userMessage("Test");
        session.addMessage(message);

        assertNotNull(session.getMessages());
        assertEquals(1, session.getMessages().size());
    }

    @Test
    public void test_addUserMessage() {
        final ChatSession session = new ChatSession();
        session.addUserMessage("User message content");

        assertEquals(1, session.getMessages().size());
        assertEquals(ChatMessage.ROLE_USER, session.getMessages().get(0).getRole());
        assertEquals("User message content", session.getMessages().get(0).getContent());
    }

    @Test
    public void test_addAssistantMessage() {
        final ChatSession session = new ChatSession();
        session.addAssistantMessage("Assistant response");

        assertEquals(1, session.getMessages().size());
        assertEquals(ChatMessage.ROLE_ASSISTANT, session.getMessages().get(0).getRole());
        assertEquals("Assistant response", session.getMessages().get(0).getContent());
    }

    @Test
    public void test_touch() {
        final ChatSession session = new ChatSession();
        final LocalDateTime initialTime = session.getLastAccessedAt();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // ignore
        }

        session.touch();

        assertTrue(session.getLastAccessedAt().isAfter(initialTime) || session.getLastAccessedAt().isEqual(initialTime));
    }

    @Test
    public void test_getMessageCount() {
        final ChatSession session = new ChatSession();
        assertEquals(0, session.getMessageCount());

        session.addUserMessage("First");
        assertEquals(1, session.getMessageCount());

        session.addAssistantMessage("Second");
        assertEquals(2, session.getMessageCount());
    }

    @Test
    public void test_getMessageCountWithNullMessages() {
        final ChatSession session = new ChatSession();
        session.setMessages(null);
        assertEquals(0, session.getMessageCount());
    }

    @Test
    public void test_clearMessages() {
        final ChatSession session = new ChatSession();
        session.addUserMessage("Message 1");
        session.addAssistantMessage("Message 2");
        assertEquals(2, session.getMessageCount());

        final LocalDateTime beforeClear = session.getLastAccessedAt();
        session.clearMessages();

        assertEquals(0, session.getMessageCount());
        assertTrue(session.getLastAccessedAt().isAfter(beforeClear) || session.getLastAccessedAt().isEqual(beforeClear));
    }

    @Test
    public void test_clearMessagesWithNullMessages() {
        final ChatSession session = new ChatSession();
        session.setMessages(null);
        session.clearMessages();
        assertEquals(0, session.getMessageCount());
    }

    @Test
    public void test_trimHistory() {
        final ChatSession session = new ChatSession();
        for (int i = 0; i < 10; i++) {
            session.addUserMessage("Message " + i);
        }
        assertEquals(10, session.getMessageCount());

        session.trimHistory(5);

        assertEquals(5, session.getMessageCount());
        assertEquals("Message 5", session.getMessages().get(0).getContent());
        assertEquals("Message 9", session.getMessages().get(4).getContent());
    }

    @Test
    public void test_trimHistoryBelowMax() {
        final ChatSession session = new ChatSession();
        session.addUserMessage("Message 1");
        session.addUserMessage("Message 2");

        session.trimHistory(10);

        assertEquals(2, session.getMessageCount());
    }

    @Test
    public void test_trimHistoryWithNullMessages() {
        final ChatSession session = new ChatSession();
        session.setMessages(null);
        session.trimHistory(5);
        // Should not throw exception
        assertTrue(true);
    }

    @Test
    public void test_sessionIdIsUnique() {
        final ChatSession session1 = new ChatSession();
        final ChatSession session2 = new ChatSession();
        assertFalse(session1.getSessionId().equals(session2.getSessionId()));
    }
}
