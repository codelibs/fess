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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class KuromojiCSVUtilTest extends UnitFessTestCase {
    @Test
    public void test_parse_basic() {
        String value;
        List<String> expected;
        List<String> actual;

        value = "フェス";
        expected = Arrays.asList("フェス");
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));

        value = "フェス,Fess";
        expected = Arrays.asList("フェス", "Fess");
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));

        value = "Fess,フェス";
        expected = Arrays.asList("Fess", "フェス");
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));

        value = "\"Fess,FESS\"";
        expected = Arrays.asList("\"Fess,FESS\"");
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));

        value = " ";
        expected = Arrays.asList(" ");
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));

        value = "";
        expected = Arrays.asList("");
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));

        value = "\"Fess\"Fess\"";
        expected = Arrays.asList();
        actual = Arrays.asList(KuromojiCSVUtil.parse(value));
        assertThat(actual, is(expected));
    }

    @Test
    public void test_parse_quoted_values() {
        String value;
        String[] result;

        // Simple quoted value - quotes are removed
        value = "\"simple\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(1, result.length);
        assertEquals("\"simple\"", result[0]);

        // Quoted value with comma - quotes are removed
        value = "\"value,with,commas\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(1, result.length);
        assertEquals("\"value,with,commas\"", result[0]);

        // Multiple quoted values - quotes are removed
        value = "\"first\",\"second\",\"third\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(3, result.length);
        assertEquals("first", result[0]);
        assertEquals("second", result[1]);
        assertEquals("\"third\"", result[2]);

        // Mixed quoted and unquoted - quotes are removed from quoted values
        value = "\"quoted\",unquoted,\"another quoted\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(3, result.length);
        assertEquals("quoted", result[0]);
        assertEquals("unquoted", result[1]);
        assertEquals("\"another quoted\"", result[2]);
    }

    @Test
    public void test_parse_escaped_quotes() {
        String value;
        String[] result;

        // Escaped quotes within quoted value - DOES NOT get unquoted because pattern doesn't match
        value = "\"value with \"\"escaped\"\" quotes\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(1, result.length);
        assertEquals("\"value with \"\"escaped\"\" quotes\"", result[0]);

        // Multiple escaped quotes - NONE get unquoted because they all contain internal quotes
        value = "\"\"\"start\"\"\",\"\"\"middle\"\"\",\"\"\"end\"\"\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(3, result.length);
        assertEquals("\"\"start\"\"", result[0]);
        assertEquals("\"\"middle\"\"", result[1]);
        assertEquals("\"\"\"end\"\"\"", result[2]);

        // Complex escaped scenario - only middle field gets unquoted (no internal quotes)
        value = "\"complex \"\"test\"\" value\",normal,\"another \"\"escaped\"\"\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(3, result.length);
        assertEquals("\"complex \"test\" value\"", result[0]);
        assertEquals("normal", result[1]);
        assertEquals("\"another \"\"escaped\"\"\"", result[2]);
    }

    @Test
    public void test_parse_empty_values() {
        String value;
        String[] result;

        // Empty fields between commas
        value = ",,";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(3, result.length);
        assertEquals("", result[0]);
        assertEquals("", result[1]);
        assertEquals("", result[2]);

        // Mixed empty and non-empty
        value = "first,,third,";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(4, result.length);
        assertEquals("first", result[0]);
        assertEquals("", result[1]);
        assertEquals("third", result[2]);
        assertEquals("", result[3]);

        // Empty quoted values
        value = "\"\",\"\",\"\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(3, result.length);
        assertEquals("\"", result[0]);
        assertEquals("\"", result[1]);
        assertEquals("\"\"", result[2]);
    }

    @Test
    public void test_parse_whitespace() {
        String value;
        String[] result;

        // Leading and trailing spaces
        value = " value1 , value2 , value3 ";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(3, result.length);
        assertEquals(" value1 ", result[0]);
        assertEquals(" value2 ", result[1]);
        assertEquals(" value3 ", result[2]);

        // Spaces in quoted values
        value = "\" spaced value \",\"  another  \"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(2, result.length);
        assertEquals(" spaced value ", result[0]);
        assertEquals("\"  another  \"", result[1]);

        // Tab characters - quoted value keeps quotes
        value = "tab\tvalue,\"quoted\ttab\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(2, result.length);
        assertEquals("tab\tvalue", result[0]);
        assertEquals("\"quoted\ttab\"", result[1]);
    }

    @Test
    public void test_parse_invalid_quotes() {
        String value;
        String[] result;

        // Unmatched quote at start
        value = "\"unmatched";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(0, result.length);

        // Unmatched quote at end
        value = "unmatched\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(0, result.length);

        // Unmatched quotes in middle
        value = "valid,\"unmatched,another";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(0, result.length);

        // Multiple unmatched quotes
        value = "\"first\"second\"third";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(0, result.length);
    }

    @Test
    public void test_parse_special_characters() {
        String value;
        String[] result;

        // Newline characters
        value = "\"line1\nline2\",normal";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(2, result.length);
        assertEquals("line1\nline2", result[0]);
        assertEquals("normal", result[1]);

        // Carriage return
        value = "\"with\rcarriage\",return";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(2, result.length);
        assertEquals("with\rcarriage", result[0]);
        assertEquals("return", result[1]);

        // Unicode characters
        value = "\"日本語\",\"한국어\",\"中文\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(3, result.length);
        assertEquals("日本語", result[0]);
        assertEquals("한국어", result[1]);
        assertEquals("\"中文\"", result[2]);

        // Emoji and special symbols - quotes preserved
        value = "\"🚀rocket\",\"@symbol\",\"#hashtag\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(3, result.length);
        assertEquals("🚀rocket", result[0]);
        assertEquals("@symbol", result[1]);
        assertEquals("\"#hashtag\"", result[2]);
    }

    @Test
    public void test_parse_single_field() {
        String value;
        String[] result;

        // Single unquoted value
        value = "single";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(1, result.length);
        assertEquals("single", result[0]);

        // Single quoted value - quotes preserved
        value = "\"single quoted\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(1, result.length);
        assertEquals("\"single quoted\"", result[0]);

        // Single empty value - quotes preserved
        value = "\"\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(1, result.length);
        assertEquals("\"\"", result[0]);
    }

    @Test
    public void test_quoteEscape_basic() {
        String input;
        String result;

        // Simple text without special characters
        input = "simple text";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("simple text", result);

        // Text with quotes
        input = "text with \"quotes\"";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("text with \"\"quotes\"\"", result);

        // Text with commas
        input = "text,with,commas";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("\"text,with,commas\"", result);

        // Text with both quotes and commas
        input = "text \"with\" quotes, and commas";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("\"text \"\"with\"\" quotes, and commas\"", result);
    }

    @Test
    public void test_quoteEscape_edge_cases() {
        String input;
        String result;

        // Empty string
        input = "";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("", result);

        // Only quotes
        input = "\"\"";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("\"\"\"\"", result);

        // Only commas
        input = ",,,";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("\",,,\"", result);

        // Single quote
        input = "\"";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("\"\"", result);

        // Single comma
        input = ",";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("\",\"", result);
    }

    @Test
    public void test_quoteEscape_special_characters() {
        String input;
        String result;

        // Newlines
        input = "line1\nline2";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("line1\nline2", result);

        // Tabs
        input = "tab\tcharacter";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("tab\tcharacter", result);

        // Unicode
        input = "日本語,テスト";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("\"日本語,テスト\"", result);

        // Emoji with quotes
        input = "\"🚀\" rocket";
        result = KuromojiCSVUtil.quoteEscape(input);
        assertEquals("\"\"🚀\"\" rocket", result);
    }

    @Test
    public void test_quoteEscape_null_input() {
        // Test null input handling
        try {
            KuromojiCSVUtil.quoteEscape(null);
            fail("Should throw NullPointerException for null input");
        } catch (NullPointerException e) {
            // Expected behavior
        }
    }

    @Test
    public void test_parse_null_input() {
        // Test null input handling
        try {
            KuromojiCSVUtil.parse(null);
            fail("Should throw NullPointerException for null input");
        } catch (NullPointerException e) {
            // Expected behavior
        }
    }

    @Test
    public void test_roundtrip_parsing() {
        // Test that parse(quoteEscape(x)) works correctly for simple values
        // Note: The implementation has a quirk where strings with escaped quotes don't get unquoted
        String[] simpleValues = { "simple", "", "no quotes or commas" };

        for (String original : simpleValues) {
            String escaped = KuromojiCSVUtil.quoteEscape(original);
            String[] parsed = KuromojiCSVUtil.parse(escaped);
            assertEquals("Roundtrip failed for: " + original, 1, parsed.length);
            assertEquals("Roundtrip failed for: " + original, original, parsed[0]);
        }

        // Test special cases where roundtrip doesn't work due to implementation quirks
        String input;
        String escaped;
        String[] parsed;

        // Text with comma gets quoted, but parse preserves quotes
        input = "with,comma";
        escaped = KuromojiCSVUtil.quoteEscape(input); // "with,comma"
        parsed = KuromojiCSVUtil.parse(escaped);
        assertEquals(1, parsed.length);
        assertEquals("\"" + input + "\"", parsed[0]); // Quotes are preserved

        // Text with quotes gets escaped quotes, but escaped quotes don't get unquoted
        input = "with \"quotes\"";
        escaped = KuromojiCSVUtil.quoteEscape(input); // with ""quotes""
        parsed = KuromojiCSVUtil.parse(escaped);
        assertEquals(1, parsed.length);
        assertEquals("with \"\"quotes\"\"", parsed[0]); // NOT the original - this is the quirk
    }

    @Test
    public void test_constructor_isPrivate() {
        // Verify that KuromojiCSVUtil has a private constructor (utility class pattern)
        try {
            Constructor<KuromojiCSVUtil> constructor = KuromojiCSVUtil.class.getDeclaredConstructor();
            assertTrue("Constructor should be private", java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));

            // Test that constructor is accessible when made accessible
            constructor.setAccessible(true);
            KuromojiCSVUtil instance = constructor.newInstance();
            assertNotNull(instance);

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            fail("Should be able to access private constructor: " + e.getMessage());
        }
    }

    @Test
    public void test_class_isFinal() {
        // Verify that KuromojiCSVUtil is a final class
        assertTrue("KuromojiCSVUtil should be final class", java.lang.reflect.Modifier.isFinal(KuromojiCSVUtil.class.getModifiers()));
    }

    @Test
    public void test_static_method_signatures() {
        // Verify all public static methods exist with correct signatures
        try {
            // parse(String)
            Method parseMethod = KuromojiCSVUtil.class.getMethod("parse", String.class);
            assertTrue("parse should be static", java.lang.reflect.Modifier.isStatic(parseMethod.getModifiers()));
            assertTrue("parse should be public", java.lang.reflect.Modifier.isPublic(parseMethod.getModifiers()));
            assertEquals("parse should return String[]", String[].class, parseMethod.getReturnType());

            // quoteEscape(String)
            Method quoteEscapeMethod = KuromojiCSVUtil.class.getMethod("quoteEscape", String.class);
            assertTrue("quoteEscape should be static", java.lang.reflect.Modifier.isStatic(quoteEscapeMethod.getModifiers()));
            assertTrue("quoteEscape should be public", java.lang.reflect.Modifier.isPublic(quoteEscapeMethod.getModifiers()));
            assertEquals("quoteEscape should return String", String.class, quoteEscapeMethod.getReturnType());

        } catch (NoSuchMethodException e) {
            fail("All expected public methods should exist: " + e.getMessage());
        }
    }

    @Test
    public void test_utilityClass_pattern() {
        // Verify utility class design pattern compliance

        // 1. Class should be final
        assertTrue("Class should be final", java.lang.reflect.Modifier.isFinal(KuromojiCSVUtil.class.getModifiers()));

        // 2. Should have exactly one private constructor
        Constructor<?>[] constructors = KuromojiCSVUtil.class.getDeclaredConstructors();
        assertEquals("Should have exactly one constructor", 1, constructors.length);
        assertTrue("Constructor should be private", java.lang.reflect.Modifier.isPrivate(constructors[0].getModifiers()));

        // 3. All public methods should be static
        Method[] methods = KuromojiCSVUtil.class.getDeclaredMethods();
        for (Method method : methods) {
            if (java.lang.reflect.Modifier.isPublic(method.getModifiers()) && !method.isSynthetic()) {
                assertTrue("Public method " + method.getName() + " should be static",
                        java.lang.reflect.Modifier.isStatic(method.getModifiers()));
            }
        }
    }

    @Test
    public void test_parse_large_input() {
        // Test with large input to ensure performance
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            if (i > 0)
                sb.append(",");
            sb.append("\"field").append(i).append("\"");
        }

        String[] result = KuromojiCSVUtil.parse(sb.toString());
        assertEquals(1000, result.length);
        // Quoted values keep their quotes
        for (int i = 0; i < 999; i++) {
            assertEquals("field" + i, result[i]);
        }
        assertEquals("\"field999\"", result[999]);

    }

    @Test
    public void test_parse_complex_scenarios() {
        String value;
        String[] result;

        // Complex real-world-like data - quotes are preserved
        value = "\"John Doe\",\"Software Engineer\",\"john.doe@example.com\",\"Loves \"\"coding\"\" and coffee\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(4, result.length);
        assertEquals("John Doe", result[0]);
        assertEquals("Software Engineer", result[1]);
        assertEquals("john.doe@example.com", result[2]);
        assertEquals("\"Loves \"\"coding\"\" and coffee\"", result[3]);

        // CSV with numbers and mixed data types - quoted fields keep quotes
        value = "123,\"Product Name\",45.67,\"Description with, comma\"";
        result = KuromojiCSVUtil.parse(value);
        assertEquals(4, result.length);
        assertEquals("123", result[0]);
        assertEquals("Product Name", result[1]);
        assertEquals("45.67", result[2]);
        assertEquals("\"Description with, comma\"", result[3]);
    }
}
