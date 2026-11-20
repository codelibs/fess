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
package org.codelibs.fess.validation;

import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class RegexPatternValidatorTest extends UnitFessTestCase {

    private static class Holder {
        @RegexPattern
        String patterns;

        @RegexPattern(RegexPattern.Syntax.CRAWL_FILTER)
        String filters;

        @RegexPattern(RegexPattern.Syntax.PLAIN)
        String single;

        @RegexPattern(RegexPattern.Syntax.LABEL_PATH)
        String labelPaths;

        @RegexPattern(RegexPattern.Syntax.FILE_DOC_PATH)
        String filePaths;
    }

    private RegexPatternValidator validatorOf(final String field) throws Exception {
        final RegexPatternValidator validator = new RegexPatternValidator();
        validator.initialize(Holder.class.getDeclaredField(field).getAnnotation(RegexPattern.class));
        return validator;
    }

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        ComponentUtil.register(new SystemHelper(), "systemHelper");
    }

    @Test
    public void test_blankIsValid() throws Exception {
        final RegexPatternValidator v = validatorOf("patterns");
        assertTrue(v.isValid(null, null));
        assertTrue(v.isValid("", null));
        assertTrue(v.isValid(" \n\t\n ", null));
    }

    @Test
    public void test_validPatterns() throws Exception {
        final RegexPatternValidator v = validatorOf("patterns");
        assertTrue(v.isValid("https?://example\\.com/.*", null));
        assertTrue(v.isValid("https://a/.*\r\n(?i).*\\.PDF$\n\nfile:/srv/.*", null));
        assertTrue(v.isValid(".*\\.(css|js|png)$", null));
    }

    @Test
    public void test_invalidPattern() throws Exception {
        final RegexPatternValidator v = validatorOf("patterns");
        assertFalse(v.isValid("https://a/(", null));
        assertFalse(v.isValid("https://a/.*\n[unclosed", null));
        assertFalse(v.isValid("*abc", null));
        assertFalse(v.isValid("a\r\nb{2,1}", null));
    }

    @Test
    public void test_commentLinesAreNotCompiled() throws Exception {
        final RegexPatternValidator v = validatorOf("patterns");
        assertTrue(v.isValid("# a comment with ( an open parenthesis\nhttps://a/.*", null));
        assertTrue(v.isValid("#DISABLE_URL_ENCODE\nhttps://a/%28.*", null));
    }

    @Test
    public void test_searchApplianceStylePrefixes() throws Exception {
        final RegexPatternValidator v = validatorOf("patterns");
        assertTrue(v.isValid("contains:a(b", null));
        assertTrue(v.isValid("regexp:https://a/.*", null));
        assertFalse(v.isValid("regexp:https://a/(", null));
    }

    @Test
    public void test_crawlFilter_isReadWithoutThePrefixesOfConfigPaths() throws Exception {
        final RegexPatternValidator v = validatorOf("filters");
        assertTrue(v.isValid("https?://example\\.com/.*\n# a comment (\n.*\\.pdf$", null));
        assertFalse(v.isValid("https://a/(", null));
        assertFalse(v.isValid("https://a/.*\n\n[unclosed", null));
        // "contains:" is resolved for the document filters only: as a crawl filter the line is the expression
        assertFalse(v.isValid("contains:a(b", null));
        assertTrue(v.isValid("regexp:https://a/.*", null));
    }

    @Test
    public void test_crawlFilter_commentLineBeforeALineThatIsNotEncoded() throws Exception {
        final RegexPatternValidator v = validatorOf("filters");
        assertTrue(v.isValid("#DISABLE_URL_ENCODE\nhttps://a/%28.*", null));
        assertFalse(v.isValid("#DISABLE_URL_ENCODE\nhttps://a/(", null));
        assertFalse(v.isValid("#DISABLE_URL_ENCODE\nhttps://a/.*\nhttps://b/(", null));
    }

    @Test
    public void test_singleExpressionIsCompiledAsItIs() throws Exception {
        final RegexPatternValidator v = validatorOf("single");
        assertTrue(v.isValid("/(.*)/", null));
        assertTrue(v.isValid("#(.*)", null));
        assertFalse(v.isValid("#(", null));
        assertFalse(v.isValid("(", null));
    }

    // ---- the validator and the code that reads the same text must agree --------------------------------------

    private static final String[] TEXTS = { //
            "https?://example\\.com/.*", //
            "https://a/(", //
            "# a (comment\nhttps://a/.*", //
            "#DISABLE_URL_ENCODE\nhttps://a/%28.*", //
            "#DISABLE_URL_ENCODE\nhttps://a/(", //
            "#DISABLE_URL_ENCODE\n# other comment (\nhttps://a/%28.*\nhttps://b/(", //
            "  https://a/.*  \r\n\r\n  https://b/[ab]+\n", //
            "https://a/[unclosed", //
            "https://a/\u65E5\u672C\u8A9E/.*", //
            "regexp:https://a/(", //
            "contains:a(b", //
            "(?i).*\\.PDF$\n.*\\.(css|js)$", //
            "a(\nb)", //
            "*abc", //
    };

    private static boolean compiles(final String pattern) {
        try {
            java.util.regex.Pattern.compile(pattern);
            return true;
        } catch (final java.util.regex.PatternSyntaxException e) {
            return false;
        }
    }

    @Test
    public void test_crawlFilter_agreesWithTheCrawlerReading() throws Exception {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final RegexPatternValidator v = validatorOf("filters");
        for (final String text : TEXTS) {
            final boolean consumed = org.codelibs.fess.helper.WebFsIndexHelper.toFilterPatterns(text, systemHelper)
                    .stream()
                    .allMatch(RegexPatternValidatorTest::compiles);
            assertTrue(consumed == v.isValid(text, null), text);
        }
    }

    @Test
    public void test_configPath_agreesWithTheDocumentFilterReading() throws Exception {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final RegexPatternValidator v = validatorOf("patterns");
        for (final String text : TEXTS) {
            boolean consumed = true;
            for (final String line : text.split("[\r\n]")) {
                final String normalized = systemHelper.normalizeConfigPath(line);
                if (!normalized.isBlank() && !compiles(normalized)) {
                    consumed = false;
                }
            }
            assertTrue(consumed == v.isValid(text, null), text);
        }
    }

    @Test
    public void test_labelPath_isCompiledAsOneJoinedExpression() throws Exception {
        final RegexPatternValidator v = validatorOf("labelPaths");
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        for (final String text : TEXTS) {
            // the label type reads its paths with LabelTypeHelper.LabelTypePattern
            boolean consumed = true;
            try {
                new org.codelibs.fess.helper.LabelTypeHelper.LabelTypePattern("v", text, null);
            } catch (final java.util.regex.PatternSyntaxException e) {
                consumed = false;
            }
            assertTrue(consumed == v.isValid(text, null), text);
        }
        // two lines that are not expressions of their own are one expression when joined
        assertTrue(v.isValid("a(\nb)", null));
        assertNotNull(systemHelper);
    }
}
