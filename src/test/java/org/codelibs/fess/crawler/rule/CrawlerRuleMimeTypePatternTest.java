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
package org.codelibs.fess.crawler.rule;

import java.util.regex.Pattern;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

/**
 * Test class for MIME type patterns defined in crawler/rule.xml.
 * Verifies that regex patterns correctly match MIME types with special characters.
 *
 * This test ensures that the + character in MIME types like application/xhtml+xml
 * is properly escaped in regex patterns to match literally.
 */
public class CrawlerRuleMimeTypePatternTest extends UnitFessTestCase {

    // Pattern from webFileRule in rule.xml
    // Note: In XML, single backslash is used. In Java string, we need to double it.
    private static final String WEB_FILE_RULE_PATTERN = "(application/xml" //
            + "|application/xhtml\\+xml" //
            + "|application/rdf\\+xml" //
            + "|application/pdf" //
            + "|application/x-freemind" //
            + "|text/xml" //
            + "|text/xml-external-parsed-entity)";

    // Pattern from fsFileRule in rule.xml
    private static final String FS_FILE_RULE_PATTERN = "(application/xml" //
            + "|application/xhtml\\+xml" //
            + "|application/rdf\\+xml" //
            + "|application/pdf" //
            + "|application/x-freemind" //
            + "|application/lha" //
            + "|application/x-lha" //
            + "|application/x-lha-compressed" //
            + "|text/xml" //
            + "|text/xml-external-parsed-entity" //
            + "|text/html)";

    // HTML rule pattern from webHtmlRule in rule.xml
    private static final String WEB_HTML_RULE_PATTERN = "text/html";

    @Test
    public void test_webFileRulePattern_xhtmlXml() {
        // Test that application/xhtml+xml is correctly matched
        Pattern pattern = Pattern.compile(WEB_FILE_RULE_PATTERN);
        assertTrue(pattern.matcher("application/xhtml+xml").matches());
    }

    @Test
    public void test_webFileRulePattern_rdfXml() {
        // Test that application/rdf+xml is correctly matched
        Pattern pattern = Pattern.compile(WEB_FILE_RULE_PATTERN);
        assertTrue(pattern.matcher("application/rdf+xml").matches());
    }

    @Test
    public void test_webFileRulePattern_applicationXml() {
        Pattern pattern = Pattern.compile(WEB_FILE_RULE_PATTERN);
        assertTrue(pattern.matcher("application/xml").matches());
    }

    @Test
    public void test_webFileRulePattern_applicationPdf() {
        Pattern pattern = Pattern.compile(WEB_FILE_RULE_PATTERN);
        assertTrue(pattern.matcher("application/pdf").matches());
    }

    @Test
    public void test_webFileRulePattern_textXml() {
        Pattern pattern = Pattern.compile(WEB_FILE_RULE_PATTERN);
        assertTrue(pattern.matcher("text/xml").matches());
    }

    @Test
    public void test_webFileRulePattern_freemind() {
        Pattern pattern = Pattern.compile(WEB_FILE_RULE_PATTERN);
        assertTrue(pattern.matcher("application/x-freemind").matches());
    }

    @Test
    public void test_webFileRulePattern_doesNotMatchTextHtml() {
        // text/html should NOT match webFileRule (it's for webHtmlRule)
        Pattern pattern = Pattern.compile(WEB_FILE_RULE_PATTERN);
        assertFalse(pattern.matcher("text/html").matches());
    }

    @Test
    public void test_webFileRulePattern_doesNotMatchImageTypes() {
        Pattern pattern = Pattern.compile(WEB_FILE_RULE_PATTERN);
        assertFalse(pattern.matcher("image/jpeg").matches());
        assertFalse(pattern.matcher("image/png").matches());
        assertFalse(pattern.matcher("image/gif").matches());
    }

    @Test
    public void test_fsFileRulePattern_xhtmlXml() {
        // Test that application/xhtml+xml is correctly matched
        Pattern pattern = Pattern.compile(FS_FILE_RULE_PATTERN);
        assertTrue(pattern.matcher("application/xhtml+xml").matches());
    }

    @Test
    public void test_fsFileRulePattern_rdfXml() {
        // Test that application/rdf+xml is correctly matched
        Pattern pattern = Pattern.compile(FS_FILE_RULE_PATTERN);
        assertTrue(pattern.matcher("application/rdf+xml").matches());
    }

    @Test
    public void test_fsFileRulePattern_textHtml() {
        // fsFileRule includes text/html (unlike webFileRule)
        Pattern pattern = Pattern.compile(FS_FILE_RULE_PATTERN);
        assertTrue(pattern.matcher("text/html").matches());
    }

    @Test
    public void test_fsFileRulePattern_lhaTypes() {
        Pattern pattern = Pattern.compile(FS_FILE_RULE_PATTERN);
        assertTrue(pattern.matcher("application/lha").matches());
        assertTrue(pattern.matcher("application/x-lha").matches());
        assertTrue(pattern.matcher("application/x-lha-compressed").matches());
    }

    @Test
    public void test_fsFileRulePattern_xmlTypes() {
        Pattern pattern = Pattern.compile(FS_FILE_RULE_PATTERN);
        assertTrue(pattern.matcher("application/xml").matches());
        assertTrue(pattern.matcher("text/xml").matches());
        assertTrue(pattern.matcher("text/xml-external-parsed-entity").matches());
    }

    @Test
    public void test_webHtmlRulePattern() {
        Pattern pattern = Pattern.compile(WEB_HTML_RULE_PATTERN);
        assertTrue(pattern.matcher("text/html").matches());
        assertFalse(pattern.matcher("application/xml").matches());
    }

    @Test
    public void test_unescapedPlusDoesNotMatch() {
        // Demonstrate the bug when + is not escaped
        // In regex, + means "one or more" of the previous character
        String wrongPattern = "(application/xhtml+xml|application/rdf+xml)";
        Pattern pattern = Pattern.compile(wrongPattern);

        // Without escaping, "application/xhtml+xml" does NOT match
        // because the pattern expects "one or more 'l'" followed by "xml"
        assertFalse(pattern.matcher("application/xhtml+xml").matches());
        assertFalse(pattern.matcher("application/rdf+xml").matches());
    }

    @Test
    public void test_escapedPlusDoesMatch() {
        // Demonstrate the fix when + is properly escaped
        String correctPattern = "(application/xhtml\\+xml|application/rdf\\+xml)";
        Pattern pattern = Pattern.compile(correctPattern);

        // With escaping, "application/xhtml+xml" matches correctly
        assertTrue(pattern.matcher("application/xhtml+xml").matches());
        assertTrue(pattern.matcher("application/rdf+xml").matches());
    }

    @Test
    public void test_mimeTypeWithPlusCharacterRequiresEscaping() {
        // Test various MIME types that contain + character
        String[] mimeTypesWithPlus = { //
                "application/xhtml+xml", //
                "application/rdf+xml", //
                "application/soap+xml", //
                "application/atom+xml", //
                "image/svg+xml" //
        };

        for (String mimeType : mimeTypesWithPlus) {
            // Create proper escaped pattern
            String escapedPattern = mimeType.replace("+", "\\+");

            // Verify escaped pattern matches
            assertTrue("Escaped pattern should match: " + mimeType, Pattern.compile(escapedPattern).matcher(mimeType).matches());

            // Verify unescaped pattern does NOT match
            assertFalse("Unescaped pattern should NOT match: " + mimeType, Pattern.compile(mimeType).matcher(mimeType).matches());
        }
    }

    @Test
    public void test_mimeTypeWithoutPlusCharacterWorksWithoutEscaping() {
        // Test MIME types without + character (no escaping needed)
        String[] mimeTypesWithoutPlus = { //
                "text/html", //
                "text/plain", //
                "application/pdf", //
                "application/xml", //
                "image/jpeg", //
                "image/png" //
        };

        for (String mimeType : mimeTypesWithoutPlus) {
            // Pattern without escaping should match
            assertTrue("Pattern should match: " + mimeType, Pattern.compile(mimeType).matcher(mimeType).matches());
        }
    }

    @Test
    public void test_patternCompilationIsValid() {
        // Ensure all patterns compile without error
        assertNotNull(Pattern.compile(WEB_FILE_RULE_PATTERN));
        assertNotNull(Pattern.compile(FS_FILE_RULE_PATTERN));
        assertNotNull(Pattern.compile(WEB_HTML_RULE_PATTERN));
    }
}
