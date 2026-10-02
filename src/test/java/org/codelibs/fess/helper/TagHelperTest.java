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
package org.codelibs.fess.helper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class TagHelperTest extends UnitFessTestCase {

    private TagHelper tagHelper;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        tagHelper = new TagHelper() {
            @Override
            public int getNameMaxLength() {
                return 10;
            }
        };
    }

    @Test
    public void test_toValue() {
        assertEquals("general:proposal", tagHelper.toValue("general", "proposal"));
    }

    @Test
    public void test_getTypeAndName() {
        assertEquals("general", tagHelper.getType("general:proposal"));
        assertEquals("proposal", tagHelper.getName("general:proposal"));
        // only the first separator splits, so a name can contain ':'
        assertEquals("general", tagHelper.getType("general:a:b"));
        assertEquals("a:b", tagHelper.getName("general:a:b"));

        assertNull(tagHelper.getType(null));
        assertNull(tagHelper.getType("general"));
        assertNull(tagHelper.getType(":proposal"));
        assertNull(tagHelper.getType("general:"));
        // a label value has only alphanumerics and underscores
        assertNull(tagHelper.getType("gen-eral:proposal"));
        assertNull(tagHelper.getName("general"));
    }

    @Test
    public void test_normalizeName() {
        assertEquals("proposal", tagHelper.normalizeName("proposal"));
        assertEquals("a b", tagHelper.normalizeName("  a \t\n b  "));
        // NFKC: full-width alphanumerics and half-width katakana
        assertEquals("ABC", tagHelper.normalizeName("ＡＢＣ"));
        assertEquals("カタカナ", tagHelper.normalizeName("ｶﾀｶﾅ"));
        assertEquals("1234567890", tagHelper.normalizeName("1234567890"));
        assertEquals("提案書", tagHelper.normalizeName("提案書"));

        assertNull(tagHelper.normalizeName(null));
        assertNull(tagHelper.normalizeName(""));
        assertNull(tagHelper.normalizeName("   "));
        assertNull(tagHelper.normalizeName("12345678901"));
        assertNull(tagHelper.normalizeName("a\"b"));
        assertNull(tagHelper.normalizeName("a\\b"));
        assertNull(tagHelper.normalizeName("a\u0000b"));
        assertNull(tagHelper.normalizeName("a\u007fb"));
        assertNull(tagHelper.normalizeName("a\u200bb"));
        assertNull(tagHelper.normalizeName("a\u202eb"));
    }

    @Test
    public void test_normalizeName_countsCodePoints() {
        // 10 characters outside the BMP are 20 chars in UTF-16 but within the limit of 10
        final String name = "😀".repeat(10);
        assertEquals(name, tagHelper.normalizeName(name));
        assertNull(tagHelper.normalizeName("😀".repeat(11)));
    }

    @Test
    public void test_isVisible() {
        final Set<String> typeSet = Set.of("general");
        assertTrue(tagHelper.isVisible("general:a", typeSet));
        assertFalse(tagHelper.isVisible("sales:a", typeSet));
        assertFalse(tagHelper.isVisible("general", typeSet));
        assertFalse(tagHelper.isVisible(null, typeSet));
        assertFalse(tagHelper.isVisible("general:a", Set.of()));
    }

    @Test
    public void test_toTagItems() {
        final List<Map<String, Object>> items =
                tagHelper.toTagItems(List.of("general:a", "sales:b", "general:a", "invalid", "general:c"), Set.of("general"));
        assertEquals(2, items.size());
        assertEquals("general:a", items.get(0).get("value"));
        assertEquals("general", items.get(0).get("type"));
        assertEquals("a", items.get(0).get("name"));
        assertEquals("general:c", items.get(1).get("value"));

        assertTrue(tagHelper.toTagItems(null, Set.of("general")).isEmpty());
    }

    @Test
    public void test_putTagFields() {
        final Map<String, Long> countMap = new LinkedHashMap<>();
        countMap.put("general:a", 3L);
        countMap.put("general:b", 1L);
        final Map<String, Object> doc = new HashMap<>();
        tagHelper.putTagFields(doc, countMap);
        Assertions.assertArrayEquals(new String[] { "general:a", "general:b" }, (String[]) doc.get("tag"));
        assertEquals(4L, doc.get("tag_count"));

        // tags that a crawler or a data store set are replaced, and an untagged document has no tag field
        doc.put("tag", new String[] { "other:x" });
        tagHelper.putTagFields(doc, new LinkedHashMap<>());
        assertFalse(doc.containsKey("tag"));
        assertEquals(0L, doc.get("tag_count"));
    }

    @Test
    public void test_createId() {
        final String id = tagHelper.createId("alice", "http://example.com/", "general:a");
        assertEquals(64, id.length());
        assertEquals(id, tagHelper.createId("alice", "http://example.com/", "general:a"));
        Assertions.assertNotEquals(id, tagHelper.createId("bob", "http://example.com/", "general:a"));
        Assertions.assertNotEquals(id, tagHelper.createId("alice", "http://example.com/x", "general:a"));
        Assertions.assertNotEquals(id, tagHelper.createId("alice", "http://example.com/", "general:b"));
    }
}
