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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LlmMessageTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    @Test
    public void test_defaultConstructor() {
        final LlmMessage message = new LlmMessage();
        assertNull(message.getRole());
        assertNull(message.getContent());
    }

    @Test
    public void test_constructorWithRoleAndContent() {
        final LlmMessage message = new LlmMessage("system", "You are a helpful assistant.");
        assertEquals("system", message.getRole());
        assertEquals("You are a helpful assistant.", message.getContent());
    }

    @Test
    public void test_systemFactory() {
        final LlmMessage message = LlmMessage.system("System prompt");
        assertEquals(LlmMessage.ROLE_SYSTEM, message.getRole());
        assertEquals("System prompt", message.getContent());
    }

    @Test
    public void test_userFactory() {
        final LlmMessage message = LlmMessage.user("User question");
        assertEquals(LlmMessage.ROLE_USER, message.getRole());
        assertEquals("User question", message.getContent());
    }

    @Test
    public void test_assistantFactory() {
        final LlmMessage message = LlmMessage.assistant("Assistant response");
        assertEquals(LlmMessage.ROLE_ASSISTANT, message.getRole());
        assertEquals("Assistant response", message.getContent());
    }

    @Test
    public void test_settersAndGetters() {
        final LlmMessage message = new LlmMessage();

        message.setRole("user");
        message.setContent("Test content");

        assertEquals("user", message.getRole());
        assertEquals("Test content", message.getContent());
    }

    @Test
    public void test_roleConstants() {
        assertEquals("system", LlmMessage.ROLE_SYSTEM);
        assertEquals("user", LlmMessage.ROLE_USER);
        assertEquals("assistant", LlmMessage.ROLE_ASSISTANT);
    }

    @Test
    public void test_toStringShortContent() {
        final LlmMessage message = new LlmMessage("user", "Short content");
        final String str = message.toString();
        assertTrue(str.contains("role='user'"));
        assertTrue(str.contains("content='Short content'"));
    }

    @Test
    public void test_toStringLongContent() {
        final StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longContent.append("A");
        }
        final LlmMessage message = new LlmMessage("user", longContent.toString());
        final String str = message.toString();
        assertTrue(str.contains("..."));
        assertTrue(str.length() < longContent.length() + 50);
    }

    @Test
    public void test_toStringNullContent() {
        final LlmMessage message = new LlmMessage("user", null);
        final String str = message.toString();
        assertTrue(str.contains("content='null'"));
    }
}
