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

public class ChatIntentTest extends UnitFessTestCase {

    @Test
    public void test_values() {
        ChatIntent[] values = ChatIntent.values();
        assertEquals(4, values.length);

        assertEquals(ChatIntent.SEARCH, values[0]);
        assertEquals(ChatIntent.SUMMARY, values[1]);
        assertEquals(ChatIntent.FAQ, values[2]);
        assertEquals(ChatIntent.UNCLEAR, values[3]);
    }

    @Test
    public void test_getValue() {
        assertEquals("search", ChatIntent.SEARCH.getValue());
        assertEquals("summary", ChatIntent.SUMMARY.getValue());
        assertEquals("faq", ChatIntent.FAQ.getValue());
        assertEquals("unclear", ChatIntent.UNCLEAR.getValue());
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
    public void test_fromValue_unclear() {
        assertEquals(ChatIntent.UNCLEAR, ChatIntent.fromValue("unclear"));
        assertEquals(ChatIntent.UNCLEAR, ChatIntent.fromValue("UNCLEAR"));
        assertEquals(ChatIntent.UNCLEAR, ChatIntent.fromValue("Unclear"));
    }

    @Test
    public void test_fromValue_null() {
        // Defaults to UNCLEAR when value is null
        assertEquals(ChatIntent.UNCLEAR, ChatIntent.fromValue(null));
    }

    @Test
    public void test_fromValue_empty() {
        // Defaults to UNCLEAR when value is empty or whitespace
        assertEquals(ChatIntent.UNCLEAR, ChatIntent.fromValue(""));
        assertEquals(ChatIntent.UNCLEAR, ChatIntent.fromValue("   "));
    }

    @Test
    public void test_fromValue_unknown() {
        // Defaults to UNCLEAR for unknown values
        assertEquals(ChatIntent.UNCLEAR, ChatIntent.fromValue("unknown"));
        assertEquals(ChatIntent.UNCLEAR, ChatIntent.fromValue("invalid"));
        assertEquals(ChatIntent.UNCLEAR, ChatIntent.fromValue("xyz"));
    }

    @Test
    public void test_valueOf() {
        assertEquals(ChatIntent.SEARCH, ChatIntent.valueOf("SEARCH"));
        assertEquals(ChatIntent.SUMMARY, ChatIntent.valueOf("SUMMARY"));
        assertEquals(ChatIntent.FAQ, ChatIntent.valueOf("FAQ"));
        assertEquals(ChatIntent.UNCLEAR, ChatIntent.valueOf("UNCLEAR"));
    }

    @Test
    public void test_toString() {
        assertNotNull(ChatIntent.SEARCH.toString());
        assertNotNull(ChatIntent.SUMMARY.toString());
        assertNotNull(ChatIntent.FAQ.toString());
        assertNotNull(ChatIntent.UNCLEAR.toString());
    }
}
