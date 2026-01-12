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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class ChatIntentTest extends UnitFessTestCase {

    @Test
    public void test_values() {
        ChatIntent[] values = ChatIntent.values();
        assertEquals(4, values.length);

        assertEquals(ChatIntent.SEARCH, values[0]);
        assertEquals(ChatIntent.SUMMARY, values[1]);
        assertEquals(ChatIntent.FAQ, values[2]);
        assertEquals(ChatIntent.CHAT, values[3]);
    }

    @Test
    public void test_getValue() {
        assertEquals("search", ChatIntent.SEARCH.getValue());
        assertEquals("summary", ChatIntent.SUMMARY.getValue());
        assertEquals("faq", ChatIntent.FAQ.getValue());
        assertEquals("chat", ChatIntent.CHAT.getValue());
    }

    @Test
    public void test_fromValue_search() {
        assertEquals(ChatIntent.SEARCH, ChatIntent.fromValue("search"));
        assertEquals(ChatIntent.SEARCH, ChatIntent.fromValue("SEARCH"));
        assertEquals(ChatIntent.SEARCH, ChatIntent.fromValue("Search"));
    }

    @Test
    public void test_fromValue_summary() {
        assertEquals(ChatIntent.SUMMARY, ChatIntent.fromValue("summary"));
        assertEquals(ChatIntent.SUMMARY, ChatIntent.fromValue("SUMMARY"));
        assertEquals(ChatIntent.SUMMARY, ChatIntent.fromValue("Summary"));
    }

    @Test
    public void test_fromValue_faq() {
        assertEquals(ChatIntent.FAQ, ChatIntent.fromValue("faq"));
        assertEquals(ChatIntent.FAQ, ChatIntent.fromValue("FAQ"));
        assertEquals(ChatIntent.FAQ, ChatIntent.fromValue("Faq"));
    }

    @Test
    public void test_fromValue_chat() {
        assertEquals(ChatIntent.CHAT, ChatIntent.fromValue("chat"));
        assertEquals(ChatIntent.CHAT, ChatIntent.fromValue("CHAT"));
        assertEquals(ChatIntent.CHAT, ChatIntent.fromValue("Chat"));
    }

    @Test
    public void test_fromValue_null() {
        // Defaults to CHAT when value is null
        assertEquals(ChatIntent.CHAT, ChatIntent.fromValue(null));
    }

    @Test
    public void test_fromValue_empty() {
        // Defaults to CHAT when value is empty or whitespace
        assertEquals(ChatIntent.CHAT, ChatIntent.fromValue(""));
        assertEquals(ChatIntent.CHAT, ChatIntent.fromValue("   "));
    }

    @Test
    public void test_fromValue_unknown() {
        // Defaults to CHAT for unknown values
        assertEquals(ChatIntent.CHAT, ChatIntent.fromValue("unknown"));
        assertEquals(ChatIntent.CHAT, ChatIntent.fromValue("invalid"));
        assertEquals(ChatIntent.CHAT, ChatIntent.fromValue("xyz"));
    }

    @Test
    public void test_valueOf() {
        assertEquals(ChatIntent.SEARCH, ChatIntent.valueOf("SEARCH"));
        assertEquals(ChatIntent.SUMMARY, ChatIntent.valueOf("SUMMARY"));
        assertEquals(ChatIntent.FAQ, ChatIntent.valueOf("FAQ"));
        assertEquals(ChatIntent.CHAT, ChatIntent.valueOf("CHAT"));
    }

    @Test
    public void test_toString() {
        assertNotNull(ChatIntent.SEARCH.toString());
        assertNotNull(ChatIntent.SUMMARY.toString());
        assertNotNull(ChatIntent.FAQ.toString());
        assertNotNull(ChatIntent.CHAT.toString());
    }
}
