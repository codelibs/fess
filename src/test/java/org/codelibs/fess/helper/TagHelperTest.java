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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.codelibs.fess.app.service.LabelTypeService;
import org.codelibs.fess.helper.TagHelper.AddResult;
import org.codelibs.fess.opensearch.config.exentity.LabelType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class TagHelperTest extends UnitFessTestCase {

    private TagHelper tagHelper;

    private final List<LabelType> store = new ArrayList<>();

    private final List<LabelType> deleted = new ArrayList<>();

    private int urlTagCount;

    private int otherLabelCount;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new SystemHelper(), "systemHelper");
        ComponentUtil.register(new VirtualHostHelper() {
            @Override
            public String getVirtualHostKey() {
                return "";
            }
        }, "virtualHostHelper");
        final LabelTypeService labelTypeService = new LabelTypeService() {
            @Override
            public OptionalEntity<LabelType> getLabelTypeByValue(final String value) {
                return store.stream()
                        .filter(l -> value.equals(l.getValue()))
                        .findFirst()
                        .map(OptionalEntity::of)
                        .orElseGet(OptionalEntity::empty);
            }

            @Override
            public List<LabelType> getLabelTypeList() {
                final List<LabelType> list = new ArrayList<>(store);
                for (int i = 0; i < otherLabelCount; i++) {
                    list.add(new LabelType());
                }
                return list;
            }

            @Override
            public void store(final LabelType labelType) {
                if (!store.contains(labelType)) {
                    store.add(labelType);
                }
            }

            @Override
            public void delete(final LabelType labelType) {
                store.remove(labelType);
                deleted.add(labelType);
            }
        };
        tagHelper = new TagHelper() {
            @Override
            public int getNameMaxLength() {
                return 10;
            }

            @Override
            protected int countTags(final String url) {
                return urlTagCount;
            }

            @Override
            protected int getMaxDocumentTags() {
                return 2;
            }

            @Override
            protected LabelTypeService getLabelTypeService() {
                return labelTypeService;
            }
        };
    }

    @Test
    public void test_toValue() {
        final String value = tagHelper.toValue("提案書");
        assertEquals(64, value.length());
        // a valid label value for the admin form
        assertTrue(value.matches("^[a-zA-Z0-9_]+$"));
        assertEquals(value, tagHelper.toValue("提案書"));
        Assertions.assertNotEquals(value, tagHelper.toValue("提案"));
    }

    @Test
    public void test_normalizeName() {
        assertEquals("proposal", tagHelper.normalizeName("proposal"));
        assertEquals("a b", tagHelper.normalizeName("  a \t\n b  "));
        // NFKC: full-width alphanumerics and half-width katakana
        assertEquals("ABC", tagHelper.normalizeName("ＡＢＣ"));
        assertEquals("カタカナ", tagHelper.normalizeName("ｶﾀｶﾅ"));
        assertEquals("1234567890", tagHelper.normalizeName("1234567890"));
        assertEquals("a\"b\\c", tagHelper.normalizeName("a\"b\\c"));

        assertNull(tagHelper.normalizeName(null));
        assertNull(tagHelper.normalizeName(""));
        assertNull(tagHelper.normalizeName("   "));
        assertNull(tagHelper.normalizeName("12345678901"));
        assertNull(tagHelper.normalizeName("a\u0000b"));
        assertNull(tagHelper.normalizeName("a\u007fb"));
        assertNull(tagHelper.normalizeName("a​b"));
        assertNull(tagHelper.normalizeName("a‮b"));
    }

    @Test
    public void test_normalizeName_countsCodePoints() {
        final String name = "😀".repeat(10);
        assertEquals(name, tagHelper.normalizeName(name));
        assertNull(tagHelper.normalizeName("😀".repeat(11)));
    }

    @Test
    public void test_addTag_createsTagVisibleToTheUser() {
        assertEquals(AddResult.ADDED, tagHelper.addTag("alice", "http://example.com/a", "提案書"));

        assertEquals(1, store.size());
        final LabelType tag = store.get(0);
        assertEquals("提案書", tag.getName());
        assertEquals(tagHelper.toValue("提案書"), tag.getValue());
        assertTrue(tag.isTagKind());
        assertEquals("http://example.com/a", tag.getIncludedPaths());
        Assertions.assertArrayEquals(new String[] { "1alice" }, tag.getPermissions());
        assertEquals("alice", tag.getCreatedBy());
    }

    @Test
    public void test_addTag_existingTag() {
        tagHelper.addTag("alice", "http://example.com/a", "提案書");

        // the same user again
        assertEquals(AddResult.ALREADY_ADDED, tagHelper.addTag("alice", "http://example.com/a", "提案書"));
        // another URL
        assertEquals(AddResult.ADDED, tagHelper.addTag("alice", "http://example.com/b", "提案書"));
        // another user on a tagged URL joins the permissions
        assertEquals(AddResult.ADDED, tagHelper.addTag("bob", "http://example.com/a", "提案書"));

        assertEquals(1, store.size());
        final LabelType tag = store.get(0);
        assertEquals(Set.of("http://example.com/a", "http://example.com/b"), LabelTypeHelper.toUrlSet(tag.getIncludedPaths()));
        Assertions.assertArrayEquals(new String[] { "1alice", "1bob" }, tag.getPermissions());
    }

    @Test
    public void test_addTag_keepsOtherPermissions() {
        final LabelType tag = new LabelType();
        tag.setName("提案書");
        tag.setValue(tagHelper.toValue("提案書"));
        tag.setKind(LabelType.KIND_TAG);
        tag.setIncludedPaths("http://example.com/a");
        tag.setPermissions(new String[] { "Rsales" });
        store.add(tag);

        assertEquals(AddResult.ADDED, tagHelper.addTag("alice", "http://example.com/a", "提案書"));
        Assertions.assertArrayEquals(new String[] { "Rsales", "1alice" }, tag.getPermissions());
    }

    @Test
    public void test_addTag_limits() {
        urlTagCount = 2;
        assertEquals(AddResult.TOO_MANY_TAGS, tagHelper.addTag("alice", "http://example.com/a", "a"));
        assertTrue(store.isEmpty());

        urlTagCount = 0;
        // page.labeltype.max.fetch.size: label types beyond it would not be loaded
        otherLabelCount = 1000;
        assertEquals(AddResult.TOO_MANY_LABELS, tagHelper.addTag("alice", "http://example.com/a", "a"));
        assertTrue(store.isEmpty());
    }

    @Test
    public void test_addTag_labelWithTheValue() {
        final LabelType label = new LabelType();
        label.setValue(tagHelper.toValue("a"));
        store.add(label);
        try {
            tagHelper.addTag("alice", "http://example.com/a", "a");
            fail();
        } catch (final IllegalStateException e) {
            // a plain label is never changed
        }
    }

    @Test
    public void test_removeTag() {
        tagHelper.addTag("alice", "http://example.com/a", "提案書");
        tagHelper.addTag("bob", "http://example.com/b", "提案書");
        final String value = tagHelper.toValue("提案書");

        // bob is left
        assertFalse(tagHelper.removeTag("alice", value));
        Assertions.assertArrayEquals(new String[] { "1bob" }, store.get(0).getPermissions());
        // not in the permissions
        assertFalse(tagHelper.removeTag("alice", value));
        // no permission is left
        assertTrue(tagHelper.removeTag("bob", value));
        assertTrue(store.isEmpty());
        assertEquals(1, deleted.size());
        // unknown tag
        assertFalse(tagHelper.removeTag("bob", value));
    }

    @Test
    public void test_removeTag_keepsTagWithGroupOrRole() {
        tagHelper.addTag("alice", "http://example.com/a", "提案書");
        final LabelType tag = store.get(0);
        tag.setPermissions(new String[] { "1alice", "2dev" });

        assertFalse(tagHelper.removeTag("alice", tag.getValue()));
        Assertions.assertArrayEquals(new String[] { "2dev" }, tag.getPermissions());
        assertTrue(deleted.isEmpty());
    }

    @Test
    public void test_isMine() {
        final LabelTypeHelper.LabelTypeItem item = new LabelTypeHelper.LabelTypeItem();
        item.setPermissions(new String[] { "1alice", "Rguest" });
        assertTrue(tagHelper.isMine(item, "alice"));
        assertFalse(tagHelper.isMine(item, "bob"));
        assertFalse(tagHelper.isMine(item, null));
    }
}
