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
package org.codelibs.fess.util;

import java.nio.charset.StandardCharsets;

import org.codelibs.fess.Constants;
import org.codelibs.fess.unit.UnitFessTestCase;

import com.google.common.io.BaseEncoding;
import org.junit.jupiter.api.Test;

public class FacetResponseTest extends UnitFessTestCase {

    @Test
    public void test_base64_encoding_decoding() {
        // Test that base64 encoding and decoding works correctly
        String originalString = "test field with spaces";
        String encoded = BaseEncoding.base64().encode(originalString.getBytes(StandardCharsets.UTF_8));
        String decoded = new String(BaseEncoding.base64().decode(encoded), StandardCharsets.UTF_8);

        assertEquals(originalString, decoded);
    }

    @Test
    public void test_facet_constants() {
        // Test that the constants are available
        assertNotNull(Constants.FACET_FIELD_PREFIX);
        assertNotNull(Constants.FACET_QUERY_PREFIX);
        assertEquals("field:", Constants.FACET_FIELD_PREFIX);
        assertEquals("query:", Constants.FACET_QUERY_PREFIX);
    }

    @Test
    public void test_unicode_base64_encoding() {
        // Test that unicode strings are properly encoded/decoded
        String unicodeString = "日本語テスト";
        String encoded = BaseEncoding.base64().encode(unicodeString.getBytes(StandardCharsets.UTF_8));
        String decoded = new String(BaseEncoding.base64().decode(encoded), StandardCharsets.UTF_8);

        assertEquals(unicodeString, decoded);
    }

    @Test
    public void test_special_characters_base64_encoding() {
        // Test that special characters are properly encoded/decoded
        String specialString = "field-with.special_chars@domain.com";
        String encoded = BaseEncoding.base64().encode(specialString.getBytes(StandardCharsets.UTF_8));
        String decoded = new String(BaseEncoding.base64().decode(encoded), StandardCharsets.UTF_8);

        assertEquals(specialString, decoded);
    }

    @Test
    public void test_empty_string_base64_encoding() {
        // Test that empty strings are properly encoded/decoded
        String emptyString = "";
        String encoded = BaseEncoding.base64().encode(emptyString.getBytes(StandardCharsets.UTF_8));
        String decoded = new String(BaseEncoding.base64().decode(encoded), StandardCharsets.UTF_8);

        assertEquals(emptyString, decoded);
    }

    @Test
    public void test_facet_prefix_usage() {
        // Test that facet prefixes can be used correctly
        String fieldName = "category";
        String encodedFieldName = BaseEncoding.base64().encode(fieldName.getBytes(StandardCharsets.UTF_8));
        String fullFieldName = Constants.FACET_FIELD_PREFIX + encodedFieldName;

        assertTrue(fullFieldName.startsWith("field:"));
        assertTrue(fullFieldName.contains(encodedFieldName));

        String queryName = "title:test";
        String encodedQueryName = BaseEncoding.base64().encode(queryName.getBytes(StandardCharsets.UTF_8));
        String fullQueryName = Constants.FACET_QUERY_PREFIX + encodedQueryName;

        assertTrue(fullQueryName.startsWith("query:"));
        assertTrue(fullQueryName.contains(encodedQueryName));
    }

    @Test
    public void test_field_name_extraction() {
        // Test that field names can be extracted from encoded strings
        String originalFieldName = "author";
        String encodedFieldName = BaseEncoding.base64().encode(originalFieldName.getBytes(StandardCharsets.UTF_8));
        String fullFieldName = Constants.FACET_FIELD_PREFIX + encodedFieldName;

        // Extract the encoded part
        String extractedEncoded = fullFieldName.substring(Constants.FACET_FIELD_PREFIX.length());
        assertEquals(encodedFieldName, extractedEncoded);

        // Decode back to original
        String extractedFieldName = new String(BaseEncoding.base64().decode(extractedEncoded), StandardCharsets.UTF_8);
        assertEquals(originalFieldName, extractedFieldName);
    }

    @Test
    public void test_query_name_extraction() {
        // Test that query names can be extracted from encoded strings
        String originalQueryName = "content:important";
        String encodedQueryName = BaseEncoding.base64().encode(originalQueryName.getBytes(StandardCharsets.UTF_8));
        String fullQueryName = Constants.FACET_QUERY_PREFIX + encodedQueryName;

        // Extract the encoded part
        String extractedEncoded = fullQueryName.substring(Constants.FACET_QUERY_PREFIX.length());
        assertEquals(encodedQueryName, extractedEncoded);

        // Decode back to original
        String extractedQueryName = new String(BaseEncoding.base64().decode(extractedEncoded), StandardCharsets.UTF_8);
        assertEquals(originalQueryName, extractedQueryName);
    }

    @Test
    public void test_complex_field_names() {
        // Test various complex field names
        String[] fieldNames = { "simple_field", "field-with-dashes", "field.with.dots", "field_with_underscores", "fieldWithCamelCase",
                "field with spaces", "field@with#special!chars", "フィールド名", "字段名称", "필드명" };

        for (String fieldName : fieldNames) {
            String encoded = BaseEncoding.base64().encode(fieldName.getBytes(StandardCharsets.UTF_8));
            String decoded = new String(BaseEncoding.base64().decode(encoded), StandardCharsets.UTF_8);
            assertEquals("Failed for field: " + fieldName, fieldName, decoded);
        }
    }

    @Test
    public void test_complex_query_strings() {
        // Test various complex query strings
        String[] queryStrings = { "title:test", "content:\"quoted value\"", "field:value AND other:value", "field:value OR other:value",
                "field:[1 TO 100]", "field:value*", "field:?value", "field:~value", "field:value^2", "タイトル:テスト", "标题:测试", "제목:테스트" };

        for (String queryString : queryStrings) {
            String encoded = BaseEncoding.base64().encode(queryString.getBytes(StandardCharsets.UTF_8));
            String decoded = new String(BaseEncoding.base64().decode(encoded), StandardCharsets.UTF_8);
            assertEquals("Failed for query: " + queryString, queryString, decoded);
        }
    }
}