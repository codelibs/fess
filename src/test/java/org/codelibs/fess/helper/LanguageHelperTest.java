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
import java.util.Map;

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.script.Script;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LanguageHelperTest extends UnitFessTestCase {

    private LanguageHelper languageHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        languageHelper = new LanguageHelper();
        languageHelper.langFields = new String[] { "title", "content" };
        languageHelper.supportedLanguages = new String[] { "ja", "en", "zh", "ko" };
        languageHelper.maxTextLength = 1000;

        // Setup mock FessConfig
        ComponentUtil.setFessConfig(new MockFessConfig());
    }

    @Test
    public void test_createScript() {
        Map<String, Object> doc = new HashMap<>();
        assertEquals("aaa", languageHelper.createScript(doc, "aaa").getIdOrCode());

        doc.put("lang", "ja");
        assertEquals("aaa;ctx._source.title_ja=ctx._source.title;ctx._source.content_ja=ctx._source.content",
                languageHelper.createScript(doc, "aaa").getIdOrCode());
    }

    @Test
    public void test_getReindexScriptSource() {
        assertEquals(
                "if(ctx._source.lang!=null){ctx._source['title_'+ctx._source.lang]=ctx._source.title;ctx._source['content_'+ctx._source.lang]=ctx._source.content}",
                languageHelper.getReindexScriptSource());
    }

    @Test
    public void test_init() {
        try {
            languageHelper.init();
            // Should not throw exception
        } catch (Exception e) {
            fail("init() should not throw an exception: " + e.getMessage());
        }
    }

    @Test
    public void test_updateDocument_withExistingLang() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("lang", "ja");
        doc.put("title", "タイトル");
        doc.put("content", "コンテンツ");

        languageHelper.updateDocument(doc);

        assertEquals("ja", doc.get("lang"));
        assertEquals("タイトル", doc.get("title_ja"));
        assertEquals("コンテンツ", doc.get("content_ja"));
    }

    @Test
    public void test_updateDocument_noLanguageDetected() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", "xyz");
        doc.put("content", "xyz");

        // Without a real detector, this will throw NullPointerException
        try {
            languageHelper.updateDocument(doc);
            fail("Should throw NullPointerException without detector");
        } catch (NullPointerException e) {
            // Expected since we don't have a detector
        }
    }

    @Test
    public void test_updateDocument_emptyDoc() {
        Map<String, Object> doc = new HashMap<>();

        languageHelper.updateDocument(doc);

        // Should not add any language fields
        assertNull(doc.get("lang"));
        assertEquals(0, doc.size());
    }

    @Test
    public void test_updateDocument_skipExistingLangFields() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("lang", "ja");
        doc.put("title", "タイトル");
        doc.put("title_ja", "既存のタイトル"); // Should not be overwritten
        doc.put("content", "コンテンツ");

        languageHelper.updateDocument(doc);

        assertEquals("ja", doc.get("lang"));
        assertEquals("既存のタイトル", doc.get("title_ja")); // Should remain unchanged
        assertEquals("コンテンツ", doc.get("content_ja"));
    }

    @Test
    public void test_detectLanguage_blank() {
        assertNull(languageHelper.detectLanguage(null));
        assertNull(languageHelper.detectLanguage(""));
        assertNull(languageHelper.detectLanguage("   "));
    }

    @Test
    public void test_detectLanguage_withoutDetector() {
        // Without a real detector, this would cause NullPointerException
        try {
            languageHelper.detectLanguage("This is English text");
            fail("Should throw NullPointerException without detector");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    public void test_getDetectText_shortText() {
        String text = "Short text";
        String result = languageHelper.getDetectText(text);
        assertEquals("Short text", result);
    }

    @Test
    public void test_getDetectText_longText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1100; i++) {
            sb.append("a");
        }
        String longText = sb.toString();

        String result = languageHelper.getDetectText(longText);
        assertEquals(1000, result.length());
    }

    @Test
    public void test_getDetectText_whitespaceNormalization() {
        String text = "Text   with\tmultiple\n\r  spaces";
        String result = languageHelper.getDetectText(text);
        assertEquals("Text with multiple spaces", result);
    }

    @Test
    public void test_getSupportedLanguage_supported() {
        assertEquals("ja", languageHelper.getSupportedLanguage("ja"));
        assertEquals("en", languageHelper.getSupportedLanguage("en"));
        assertEquals("zh", languageHelper.getSupportedLanguage("zh"));
        assertEquals("ko", languageHelper.getSupportedLanguage("ko"));
    }

    @Test
    public void test_getSupportedLanguage_unsupported() {
        assertNull(languageHelper.getSupportedLanguage("fr"));
        assertNull(languageHelper.getSupportedLanguage("de"));
        assertNull(languageHelper.getSupportedLanguage("unknown"));
    }

    @Test
    public void test_getSupportedLanguage_blank() {
        assertNull(languageHelper.getSupportedLanguage(null));
        assertNull(languageHelper.getSupportedLanguage(""));
        assertNull(languageHelper.getSupportedLanguage("   "));
    }

    @Test
    public void test_setDetector() {
        // Test with null detector
        languageHelper.setDetector(null);
        assertNull(languageHelper.detector);
    }

    @Test
    public void test_createScript_withBlankLanguage() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("lang", "");

        Script result = languageHelper.createScript(doc, "aaa");
        assertEquals("aaa", result.getIdOrCode());
    }

    @Test
    public void test_createScript_withNullLanguage() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("lang", null);

        Script result = languageHelper.createScript(doc, "aaa");
        assertEquals("aaa", result.getIdOrCode());
    }

    @Test
    public void test_createScript_multipleFields() {
        languageHelper.langFields = new String[] { "title", "content", "description" };

        Map<String, Object> doc = new HashMap<>();
        doc.put("lang", "ja");

        Script result = languageHelper.createScript(doc, "test");
        assertEquals(
                "test;ctx._source.title_ja=ctx._source.title;ctx._source.content_ja=ctx._source.content;ctx._source.description_ja=ctx._source.description",
                result.getIdOrCode());
    }

    @Test
    public void test_getReindexScriptSource_singleField() {
        languageHelper.langFields = new String[] { "title" };

        String result = languageHelper.getReindexScriptSource();
        assertEquals("if(ctx._source.lang!=null){ctx._source['title_'+ctx._source.lang]=ctx._source.title}", result);
    }

    @Test
    public void test_getReindexScriptSource_multipleFields() {
        languageHelper.langFields = new String[] { "title", "content", "description" };

        String result = languageHelper.getReindexScriptSource();
        assertEquals(
                "if(ctx._source.lang!=null){ctx._source['title_'+ctx._source.lang]=ctx._source.title;ctx._source['content_'+ctx._source.lang]=ctx._source.content;ctx._source['description_'+ctx._source.lang]=ctx._source.description}",
                result);
    }

    @Test
    public void test_updateDocument_withDifferentFieldTypes() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("lang", "ja");
        doc.put("title", "タイトル");
        doc.put("content", 123); // Non-string content
        doc.put("url", "http://example.com"); // Field not in langFields

        languageHelper.updateDocument(doc);

        assertEquals("ja", doc.get("lang"));
        assertEquals("タイトル", doc.get("title_ja"));
        assertEquals(123, doc.get("content_ja"));
        assertNull(doc.get("url_ja")); // url is not in langFields
    }

    @Test
    public void test_updateDocument_withNullLangFields() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("lang", "ja");
        doc.put("title", null);
        doc.put("content", "コンテンツ");

        languageHelper.updateDocument(doc);

        assertEquals("ja", doc.get("lang"));
        assertNull(doc.get("title_ja"));
        assertEquals("コンテンツ", doc.get("content_ja"));
    }

    @Test
    public void test_updateDocument_withoutLangFields() {
        languageHelper.langFields = new String[] { "description" }; // Different field

        Map<String, Object> doc = new HashMap<>();
        doc.put("lang", "ja");
        doc.put("title", "タイトル");
        doc.put("content", "コンテンツ");

        languageHelper.updateDocument(doc);

        assertEquals("ja", doc.get("lang"));
        assertNull(doc.get("title_ja")); // title is not in langFields
        assertNull(doc.get("content_ja")); // content is not in langFields
    }

    @Test
    public void test_getSupportedLanguage_caseInsensitive() {
        // Test that method is case sensitive (as per implementation)
        assertNull(languageHelper.getSupportedLanguage("JA"));
        assertNull(languageHelper.getSupportedLanguage("EN"));
        assertEquals("ja", languageHelper.getSupportedLanguage("ja"));
        assertEquals("en", languageHelper.getSupportedLanguage("en"));
    }

    @Test
    public void test_updateDocument_emptyLangFields() {
        languageHelper.langFields = new String[] {};

        Map<String, Object> doc = new HashMap<>();
        doc.put("lang", "ja");
        doc.put("title", "タイトル");
        doc.put("content", "コンテンツ");

        languageHelper.updateDocument(doc);

        assertEquals("ja", doc.get("lang"));
        // No fields should be added since langFields is empty
        assertNull(doc.get("title_ja"));
        assertNull(doc.get("content_ja"));
    }

    @Test
    public void test_createScript_noLangFields() {
        languageHelper.langFields = new String[] {};

        Map<String, Object> doc = new HashMap<>();
        doc.put("lang", "ja");

        Script result = languageHelper.createScript(doc, "test");
        assertEquals("test", result.getIdOrCode());
    }

    @Test
    public void test_getReindexScriptSource_noLangFields() {
        languageHelper.langFields = new String[] {};

        String result = languageHelper.getReindexScriptSource();
        assertEquals("if(ctx._source.lang!=null){}", result);
    }

    @Test
    public void test_updateDocument_partialFieldsPresent() {
        Map<String, Object> doc = new HashMap<>();
        doc.put("lang", "ja");
        doc.put("title", "タイトル");
        // content field is missing

        languageHelper.updateDocument(doc);

        assertEquals("ja", doc.get("lang"));
        assertEquals("タイトル", doc.get("title_ja"));
        assertNull(doc.get("content_ja")); // content field was not present
    }

    @Test
    public void test_getSupportedLanguage_withWhitespace() {
        // Test that whitespace is not trimmed (as per implementation)
        assertNull(languageHelper.getSupportedLanguage(" ja "));
        assertNull(languageHelper.getSupportedLanguage("ja "));
        assertNull(languageHelper.getSupportedLanguage(" ja"));
    }

    // Mock classes
    private static class MockFessConfig extends FessConfig.SimpleImpl {
        private static final long serialVersionUID = 1L;

        @Override
        public String[] getIndexerLanguageFieldsAsArray() {
            return new String[] { "title", "content" };
        }

        @Override
        public String[] getSupportedLanguagesAsArray() {
            return new String[] { "ja", "en", "zh", "ko" };
        }

        @Override
        public Integer getIndexerLanguageDetectLengthAsInteger() {
            return 1000;
        }

        @Override
        public String getIndexFieldLang() {
            return "lang";
        }
    }
}