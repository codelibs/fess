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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for JsonResponseUtil.
 * Tests JSON response building and formatting utilities.
 */
public class JsonResponseUtilTest extends UnitFessTestCase {

    public void test_success() {
        Map<String, Object> response = JsonResponseUtil.success("test data");

        assertNotNull(response);
        assertEquals("test data", response.get("data"));
    }

    public void test_success_withMap() {
        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");

        Map<String, Object> response = JsonResponseUtil.success(data);

        assertNotNull(response);
        assertEquals(data, response.get("data"));
    }

    public void test_success_withNull() {
        Map<String, Object> response = JsonResponseUtil.success(null);

        assertNotNull(response);
        assertNull(response.get("data"));
    }

    public void test_error_withMessage() {
        Map<String, Object> response = JsonResponseUtil.error("Error message");

        assertNotNull(response);
        assertEquals("Error message", response.get("message"));
    }

    public void test_error_withErrorCodeAndMessage() {
        Map<String, Object> response = JsonResponseUtil.error("ERR001", "Error occurred");

        assertNotNull(response);
        assertEquals("ERR001", response.get("error_code"));
        assertEquals("Error occurred", response.get("message"));
    }

    public void test_escapeCallbackName_valid() {
        assertEquals("/**/callback", JsonResponseUtil.escapeCallbackName("callback"));
        assertEquals("/**/myCallback", JsonResponseUtil.escapeCallbackName("myCallback"));
        assertEquals("/**/jQuery123", JsonResponseUtil.escapeCallbackName("jQuery123"));
        assertEquals("/**/_callback", JsonResponseUtil.escapeCallbackName("_callback"));
        assertEquals("/**/$callback", JsonResponseUtil.escapeCallbackName("$callback"));
    }

    public void test_escapeCallbackName_withInvalidCharacters() {
        // Should remove all invalid characters
        assertEquals("/**/callback", JsonResponseUtil.escapeCallbackName("call-back"));
        assertEquals("/**/callback", JsonResponseUtil.escapeCallbackName("call back"));
        assertEquals("/**/callback", JsonResponseUtil.escapeCallbackName("call@back"));
        assertEquals("/**/callback", JsonResponseUtil.escapeCallbackName("call#back"));
    }

    public void test_escapeCallbackName_noDots() {
        // Dots should be removed for security (prevents prototype pollution)
        assertEquals("/**/jQuerycallback", JsonResponseUtil.escapeCallbackName("jQuery.callback"));
        assertEquals("/**/windowdocument", JsonResponseUtil.escapeCallbackName("window.document"));
        assertEquals("/**/aabbcc", JsonResponseUtil.escapeCallbackName("aa.bb.cc"));
    }

    public void test_escapeCallbackName_withSpecialCharacters() {
        assertEquals("/**/callback$", JsonResponseUtil.escapeCallbackName("callback!@#$%^&*()"));
        assertEquals("/**/abc123XYZ", JsonResponseUtil.escapeCallbackName("abc123!XYZ"));
        assertEquals("/**/test_fnscript", JsonResponseUtil.escapeCallbackName("test_fn<script>"));
    }

    public void test_escapeCallbackName_null() {
        assertNull(JsonResponseUtil.escapeCallbackName(null));
    }

    public void test_escapeCallbackName_empty() {
        assertEquals("/**/", JsonResponseUtil.escapeCallbackName(""));
    }

    public void test_escapeCallbackName_onlyInvalidCharacters() {
        assertEquals("/**/$", JsonResponseUtil.escapeCallbackName("!@#$%^&*()"));
        assertEquals("/**/", JsonResponseUtil.escapeCallbackName("---"));
    }

    public void test_toJson_simpleObject() throws JsonProcessingException {
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");

        String json = JsonResponseUtil.toJson(data);

        assertNotNull(json);
        assertTrue(json.contains("\"key\""));
        assertTrue(json.contains("\"value\""));
    }

    public void test_toJson_nestedObject() throws JsonProcessingException {
        Map<String, Object> data = new HashMap<>();
        data.put("string", "value");
        data.put("number", 123);
        data.put("boolean", true);
        data.put("array", Arrays.asList("a", "b", "c"));

        String json = JsonResponseUtil.toJson(data);

        assertNotNull(json);
        assertTrue(json.contains("\"string\""));
        assertTrue(json.contains("\"number\""));
        assertTrue(json.contains("\"boolean\""));
        assertTrue(json.contains("\"array\""));
    }

    public void test_toJson_null() throws JsonProcessingException {
        String json = JsonResponseUtil.toJson(null);
        assertEquals("null", json);
    }

    public void test_getObjectMapper() {
        ObjectMapper mapper = JsonResponseUtil.getObjectMapper();

        assertNotNull(mapper);
        assertSame(mapper, JsonResponseUtil.getObjectMapper()); // Should return same instance
    }

    public void test_objectMapper_configuration() {
        ObjectMapper mapper = JsonResponseUtil.getObjectMapper();

        // Verify ObjectMapper is configured correctly
        assertFalse(mapper.getSerializationConfig().isEnabled(
            com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
        assertFalse(mapper.getSerializationConfig().isEnabled(
            com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS));
    }

    public void test_escapeCallbackName_alphanumericOnly() {
        // Test that only alphanumeric, underscore, and dollar are allowed
        String result = JsonResponseUtil.escapeCallbackName("abcABC123_$");
        assertEquals("/**/abcABC123_$", result);

        // Verify dots are removed
        result = JsonResponseUtil.escapeCallbackName("abc.def");
        assertEquals("/**/abcdef", result);

        // Verify other special chars are removed
        result = JsonResponseUtil.escapeCallbackName("abc-def+ghi");
        assertEquals("/**/abcdefghi", result);
    }

    public void test_escapeCallbackName_preventPrototypePollution() {
        // Common prototype pollution attack vectors
        String[] attacks = {
            "__proto__",
            "constructor.prototype",
            "Object.prototype",
            "Array.prototype"
        };

        for (String attack : attacks) {
            String result = JsonResponseUtil.escapeCallbackName(attack);
            // Dots should be removed
            assertFalse("Callback name should not contain dots: " + result, result.contains("."));
        }
    }
}
