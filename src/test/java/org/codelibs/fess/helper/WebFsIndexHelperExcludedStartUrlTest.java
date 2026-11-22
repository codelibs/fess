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

import java.util.List;

import org.codelibs.fess.unit.LogCapturingAppender;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class WebFsIndexHelperExcludedStartUrlTest extends UnitFessTestCase {

    // the exclusion the admin screen proposes for a new web crawl configuration
    private static final String DEFAULT_EXCLUDE = "(?i).*(css|js|jpeg|jpg|gif|png|bmp|wmv|xml|ico|exe)";

    private List<String> warn(final String kind, final String url, final String... patterns) {
        final LogCapturingAppender appender = LogCapturingAppender.attach(WebFsIndexHelper.class);
        try {
            new WebFsIndexHelper().warnExcludedStartUrl("site1", kind, url, List.of(patterns));
            return appender.warnings();
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_warnExcludedStartUrl_namesTheConfigTheUrlAndThePattern() {
        final List<String> warnings = warn("URL", "https://example.com/sitemap.xml", DEFAULT_EXCLUDE);
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("[site1]"));
        assertTrue(warnings.get(0).contains("https://example.com/sitemap.xml"));
        assertTrue(warnings.get(0).contains(DEFAULT_EXCLUDE));
    }

    @Test
    public void test_warnExcludedStartUrl_namesTheFirstMatchingPatternOnly() {
        final List<String> warnings = warn("URL", "https://example.com/a.xml", "https://other.example/.*", ".*\\.xml", ".*a\\.xml");
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains(".*\\.xml"));
        assertFalse(warnings.get(0).contains("other.example"));
    }

    @Test
    public void test_warnExcludedStartUrl_silentWhenNoPatternMatches() {
        assertTrue(warn("URL", "https://example.com/index.html", DEFAULT_EXCLUDE).isEmpty());
        assertTrue(warn("URL", "https://example.com/index.html").isEmpty());
    }

    @Test
    public void test_warnExcludedStartUrl_matchesTheWholeUrlLikeTheCrawler() {
        // the crawler uses Matcher#matches, so a pattern that only matches a part does not exclude
        assertTrue(warn("URL", "https://example.com/a.xml", "a\\.xml").isEmpty());
    }

    @Test
    public void test_warnExcludedStartUrl_ignoresAPatternThatDoesNotCompile() {
        final List<String> warnings = warn("URL", "https://example.com/a.xml", "(unclosed", ".*xml");
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains(".*xml"));
    }

    @Test
    public void test_warnExcludedStartUrl_forAFilePath() {
        final List<String> warnings = warn("path", "file:/data/docs/index.xml", ".*\\.xml");
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("start path file:/data/docs/index.xml"));
        assertTrue(warnings.get(0).contains("excluded path pattern"));
    }

    @Test
    public void test_warnExcludedStartUrl_doesNotLogCredentials() {
        final List<String> warnings = warn("URL", "http://user:s3cret@example.com/a.xml", ".*xml", ".*");
        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("http://example.com/a.xml"));
        assertFalse(warnings.get(0).contains("s3cret"));
    }
}
