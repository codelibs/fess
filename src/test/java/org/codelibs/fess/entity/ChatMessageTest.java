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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.entity.ChatMessage.ChatSource;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class ChatMessageTest extends UnitFessTestCase {

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
        final ChatMessage message = new ChatMessage();
        assertNull(message.getId());
        assertNull(message.getRole());
        assertNull(message.getContent());
        assertNotNull(message.getTimestamp());
        assertNotNull(message.getSources());
        assertTrue(message.getSources().isEmpty());
    }

    @Test
    public void test_constructorWithRoleAndContent() {
        final ChatMessage message = new ChatMessage("user", "Hello");
        assertNull(message.getId());
        assertEquals("user", message.getRole());
        assertEquals("Hello", message.getContent());
        assertNotNull(message.getTimestamp());
        assertNotNull(message.getSources());
    }

    @Test
    public void test_userMessage() {
        final ChatMessage message = ChatMessage.userMessage("Hello, World!");
        assertEquals(ChatMessage.ROLE_USER, message.getRole());
        assertEquals("Hello, World!", message.getContent());
        assertTrue(message.isUser());
        assertFalse(message.isAssistant());
    }

    @Test
    public void test_assistantMessage() {
        final ChatMessage message = ChatMessage.assistantMessage("Hi there!");
        assertEquals(ChatMessage.ROLE_ASSISTANT, message.getRole());
        assertEquals("Hi there!", message.getContent());
        assertTrue(message.isAssistant());
        assertFalse(message.isUser());
    }

    @Test
    public void test_settersAndGetters() {
        final ChatMessage message = new ChatMessage();
        final LocalDateTime timestamp = LocalDateTime.of(2025, 1, 1, 12, 0, 0);
        final List<ChatSource> sources = new ArrayList<>();

        message.setId("msg-123");
        message.setRole("assistant");
        message.setContent("Test content");
        message.setTimestamp(timestamp);
        message.setSources(sources);

        assertEquals("msg-123", message.getId());
        assertEquals("assistant", message.getRole());
        assertEquals("Test content", message.getContent());
        assertEquals(timestamp, message.getTimestamp());
        assertSame(sources, message.getSources());
    }

    @Test
    public void test_addSource() {
        final ChatMessage message = new ChatMessage();
        final ChatSource source = new ChatSource();
        source.setIndex(1);
        source.setTitle("Test Document");

        message.addSource(source);

        assertEquals(1, message.getSources().size());
        assertEquals("Test Document", message.getSources().get(0).getTitle());
    }

    @Test
    public void test_addSourceWithNullSources() {
        final ChatMessage message = new ChatMessage();
        message.setSources(null);

        final ChatSource source = new ChatSource();
        source.setTitle("New Source");

        message.addSource(source);

        assertNotNull(message.getSources());
        assertEquals(1, message.getSources().size());
    }

    @Test
    public void test_isUser() {
        final ChatMessage userMessage = new ChatMessage(ChatMessage.ROLE_USER, "test");
        final ChatMessage assistantMessage = new ChatMessage(ChatMessage.ROLE_ASSISTANT, "test");
        final ChatMessage unknownMessage = new ChatMessage("unknown", "test");

        assertTrue(userMessage.isUser());
        assertFalse(assistantMessage.isUser());
        assertFalse(unknownMessage.isUser());
    }

    @Test
    public void test_isAssistant() {
        final ChatMessage userMessage = new ChatMessage(ChatMessage.ROLE_USER, "test");
        final ChatMessage assistantMessage = new ChatMessage(ChatMessage.ROLE_ASSISTANT, "test");
        final ChatMessage unknownMessage = new ChatMessage("unknown", "test");

        assertFalse(userMessage.isAssistant());
        assertTrue(assistantMessage.isAssistant());
        assertFalse(unknownMessage.isAssistant());
    }

    @Test
    public void test_roleConstants() {
        assertEquals("user", ChatMessage.ROLE_USER);
        assertEquals("assistant", ChatMessage.ROLE_ASSISTANT);
    }

    @Test
    public void test_chatSourceDefaultConstructor() {
        final ChatSource source = new ChatSource();
        assertEquals(0, source.getIndex());
        assertNull(source.getTitle());
        assertNull(source.getUrl());
        assertNull(source.getDocId());
        assertNull(source.getSnippet());
    }

    @Test
    public void test_chatSourceFromMap() {
        final Map<String, Object> doc = new HashMap<>();
        doc.put("title", "Test Title");
        doc.put("url", "https://example.com");
        doc.put("doc_id", "doc-123");
        doc.put("content_description", "Test snippet");

        final ChatSource source = new ChatSource(5, doc);

        assertEquals(5, source.getIndex());
        assertEquals("Test Title", source.getTitle());
        assertEquals("https://example.com", source.getUrl());
        assertEquals("doc-123", source.getDocId());
        assertEquals("Test snippet", source.getSnippet());
    }

    @Test
    public void test_chatSourceFromMapWithNullValues() {
        final Map<String, Object> doc = new HashMap<>();

        final ChatSource source = new ChatSource(1, doc);

        assertEquals(1, source.getIndex());
        assertNull(source.getTitle());
        assertNull(source.getUrl());
        assertNull(source.getDocId());
        assertNull(source.getSnippet());
    }

    @Test
    public void test_chatSourceSetters() {
        final ChatSource source = new ChatSource();

        source.setIndex(10);
        source.setTitle("Document Title");
        source.setUrl("https://test.com");
        source.setDocId("id-456");
        source.setSnippet("A short snippet");

        assertEquals(10, source.getIndex());
        assertEquals("Document Title", source.getTitle());
        assertEquals("https://test.com", source.getUrl());
        assertEquals("id-456", source.getDocId());
        assertEquals("A short snippet", source.getSnippet());
    }

    @Test
    public void test_htmlContent_defaultValue() {
        final ChatMessage message = new ChatMessage();
        assertNull(message.getHtmlContent());
    }

    @Test
    public void test_htmlContent_setAndGet() {
        final ChatMessage message = new ChatMessage();
        final String htmlContent = "<p>This is <strong>HTML</strong> content</p>";

        message.setHtmlContent(htmlContent);

        assertEquals(htmlContent, message.getHtmlContent());
    }

    @Test
    public void test_htmlContent_withUserMessage() {
        final ChatMessage message = ChatMessage.userMessage("Hello");
        message.setHtmlContent("<p>Hello</p>");

        assertEquals("Hello", message.getContent());
        assertEquals("<p>Hello</p>", message.getHtmlContent());
    }

    @Test
    public void test_htmlContent_withAssistantMessage() {
        final ChatMessage message = ChatMessage.assistantMessage("# Title\n\nParagraph");
        message.setHtmlContent("<h1>Title</h1><p>Paragraph</p>");

        assertEquals("# Title\n\nParagraph", message.getContent());
        assertEquals("<h1>Title</h1><p>Paragraph</p>", message.getHtmlContent());
    }

    @Test
    public void test_htmlContent_setNull() {
        final ChatMessage message = new ChatMessage();
        message.setHtmlContent("<p>content</p>");
        message.setHtmlContent(null);

        assertNull(message.getHtmlContent());
    }

    @Test
    public void test_htmlContent_empty() {
        final ChatMessage message = new ChatMessage();
        message.setHtmlContent("");

        assertEquals("", message.getHtmlContent());
    }

    @Test
    public void test_htmlContent_withSpecialCharacters() {
        final ChatMessage message = new ChatMessage();
        final String htmlContent = "<p>Special chars: &amp; &lt; &gt; &quot;</p>";

        message.setHtmlContent(htmlContent);

        assertEquals(htmlContent, message.getHtmlContent());
    }

    @Test
    public void test_htmlContent_withUnicode() {
        final ChatMessage message = new ChatMessage();
        final String htmlContent = "<p>日本語テキスト</p>";

        message.setHtmlContent(htmlContent);

        assertEquals(htmlContent, message.getHtmlContent());
    }
}
