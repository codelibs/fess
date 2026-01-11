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
package org.codelibs.fess.llm;

import java.util.ArrayList;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LlmChatRequestTest extends UnitFessTestCase {

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
        final LlmChatRequest request = new LlmChatRequest();
        assertNotNull(request.getMessages());
        assertTrue(request.getMessages().isEmpty());
        assertNull(request.getModel());
        assertNull(request.getMaxTokens());
        assertNull(request.getTemperature());
        assertFalse(request.isStream());
    }

    @Test
    public void test_addMessage() {
        final LlmChatRequest request = new LlmChatRequest();
        final LlmMessage message = LlmMessage.user("Test message");

        final LlmChatRequest result = request.addMessage(message);

        assertSame(request, result);
        assertEquals(1, request.getMessages().size());
        assertEquals("Test message", request.getMessages().get(0).getContent());
    }

    @Test
    public void test_addSystemMessage() {
        final LlmChatRequest request = new LlmChatRequest();

        final LlmChatRequest result = request.addSystemMessage("System prompt");

        assertSame(request, result);
        assertEquals(1, request.getMessages().size());
        assertEquals(LlmMessage.ROLE_SYSTEM, request.getMessages().get(0).getRole());
        assertEquals("System prompt", request.getMessages().get(0).getContent());
    }

    @Test
    public void test_addUserMessage() {
        final LlmChatRequest request = new LlmChatRequest();

        final LlmChatRequest result = request.addUserMessage("User question");

        assertSame(request, result);
        assertEquals(1, request.getMessages().size());
        assertEquals(LlmMessage.ROLE_USER, request.getMessages().get(0).getRole());
        assertEquals("User question", request.getMessages().get(0).getContent());
    }

    @Test
    public void test_addAssistantMessage() {
        final LlmChatRequest request = new LlmChatRequest();

        final LlmChatRequest result = request.addAssistantMessage("Assistant response");

        assertSame(request, result);
        assertEquals(1, request.getMessages().size());
        assertEquals(LlmMessage.ROLE_ASSISTANT, request.getMessages().get(0).getRole());
        assertEquals("Assistant response", request.getMessages().get(0).getContent());
    }

    @Test
    public void test_setMessages() {
        final LlmChatRequest request = new LlmChatRequest();
        final List<LlmMessage> messages = new ArrayList<>();
        messages.add(LlmMessage.user("Message 1"));
        messages.add(LlmMessage.assistant("Message 2"));

        request.setMessages(messages);

        assertSame(messages, request.getMessages());
        assertEquals(2, request.getMessages().size());
    }

    @Test
    public void test_setModel() {
        final LlmChatRequest request = new LlmChatRequest();

        final LlmChatRequest result = request.setModel("gpt-4");

        assertSame(request, result);
        assertEquals("gpt-4", request.getModel());
    }

    @Test
    public void test_setMaxTokens() {
        final LlmChatRequest request = new LlmChatRequest();

        final LlmChatRequest result = request.setMaxTokens(1000);

        assertSame(request, result);
        assertEquals(Integer.valueOf(1000), request.getMaxTokens());
    }

    @Test
    public void test_setTemperature() {
        final LlmChatRequest request = new LlmChatRequest();

        final LlmChatRequest result = request.setTemperature(0.7);

        assertSame(request, result);
        assertEquals(Double.valueOf(0.7), request.getTemperature());
    }

    @Test
    public void test_setStream() {
        final LlmChatRequest request = new LlmChatRequest();

        final LlmChatRequest result = request.setStream(true);

        assertSame(request, result);
        assertTrue(request.isStream());
    }

    @Test
    public void test_fluentBuilding() {
        final LlmChatRequest request = new LlmChatRequest()
                .addSystemMessage("You are a helpful assistant")
                .addUserMessage("Hello!")
                .addAssistantMessage("Hi there!")
                .addUserMessage("How are you?")
                .setModel("llama3")
                .setMaxTokens(500)
                .setTemperature(0.5)
                .setStream(false);

        assertEquals(4, request.getMessages().size());
        assertEquals("llama3", request.getModel());
        assertEquals(Integer.valueOf(500), request.getMaxTokens());
        assertEquals(Double.valueOf(0.5), request.getTemperature());
        assertFalse(request.isStream());
    }

    @Test
    public void test_multipleMessages() {
        final LlmChatRequest request = new LlmChatRequest();
        for (int i = 0; i < 5; i++) {
            request.addUserMessage("User " + i);
            request.addAssistantMessage("Assistant " + i);
        }

        assertEquals(10, request.getMessages().size());
    }
}
