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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LlmChatResponseTest extends UnitFessTestCase {

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
        final LlmChatResponse response = new LlmChatResponse();
        assertNull(response.getContent());
        assertNull(response.getFinishReason());
        assertNull(response.getPromptTokens());
        assertNull(response.getCompletionTokens());
        assertNull(response.getTotalTokens());
        assertNull(response.getModel());
    }

    @Test
    public void test_constructorWithContent() {
        final LlmChatResponse response = new LlmChatResponse("Hello, World!");
        assertEquals("Hello, World!", response.getContent());
        assertNull(response.getFinishReason());
        assertNull(response.getPromptTokens());
        assertNull(response.getCompletionTokens());
        assertNull(response.getTotalTokens());
        assertNull(response.getModel());
    }

    @Test
    public void test_settersAndGetters() {
        final LlmChatResponse response = new LlmChatResponse();

        response.setContent("Response content");
        response.setFinishReason("stop");
        response.setPromptTokens(100);
        response.setCompletionTokens(50);
        response.setTotalTokens(150);
        response.setModel("gpt-4");

        assertEquals("Response content", response.getContent());
        assertEquals("stop", response.getFinishReason());
        assertEquals(Integer.valueOf(100), response.getPromptTokens());
        assertEquals(Integer.valueOf(50), response.getCompletionTokens());
        assertEquals(Integer.valueOf(150), response.getTotalTokens());
        assertEquals("gpt-4", response.getModel());
    }

    @Test
    public void test_setContent() {
        final LlmChatResponse response = new LlmChatResponse();
        response.setContent("Test content");
        assertEquals("Test content", response.getContent());

        response.setContent("Updated content");
        assertEquals("Updated content", response.getContent());
    }

    @Test
    public void test_setFinishReason() {
        final LlmChatResponse response = new LlmChatResponse();

        response.setFinishReason("stop");
        assertEquals("stop", response.getFinishReason());

        response.setFinishReason("length");
        assertEquals("length", response.getFinishReason());
    }

    @Test
    public void test_tokenCounts() {
        final LlmChatResponse response = new LlmChatResponse();

        response.setPromptTokens(250);
        response.setCompletionTokens(75);
        response.setTotalTokens(325);

        assertEquals(Integer.valueOf(250), response.getPromptTokens());
        assertEquals(Integer.valueOf(75), response.getCompletionTokens());
        assertEquals(Integer.valueOf(325), response.getTotalTokens());
    }

    @Test
    public void test_setModel() {
        final LlmChatResponse response = new LlmChatResponse();

        response.setModel("llama3");
        assertEquals("llama3", response.getModel());

        response.setModel("gpt-3.5-turbo");
        assertEquals("gpt-3.5-turbo", response.getModel());
    }

    @Test
    public void test_nullValues() {
        final LlmChatResponse response = new LlmChatResponse("Initial");
        response.setContent(null);
        response.setFinishReason(null);
        response.setPromptTokens(null);
        response.setCompletionTokens(null);
        response.setTotalTokens(null);
        response.setModel(null);

        assertNull(response.getContent());
        assertNull(response.getFinishReason());
        assertNull(response.getPromptTokens());
        assertNull(response.getCompletionTokens());
        assertNull(response.getTotalTokens());
        assertNull(response.getModel());
    }

    @Test
    public void test_zeroTokens() {
        final LlmChatResponse response = new LlmChatResponse();
        response.setPromptTokens(0);
        response.setCompletionTokens(0);
        response.setTotalTokens(0);

        assertEquals(Integer.valueOf(0), response.getPromptTokens());
        assertEquals(Integer.valueOf(0), response.getCompletionTokens());
        assertEquals(Integer.valueOf(0), response.getTotalTokens());
    }
}
