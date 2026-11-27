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
package org.codelibs.fess.app.service;

import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessAppServiceTest extends UnitFessTestCase {

    private TestFessAppService service;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        service = new TestFessAppService();
    }

    @Test
    public void test_wrapQuery_withoutWildcards() {
        assertEquals("*test*", service.callWrapQuery("test"));
    }

    @Test
    public void test_wrapQuery_withLeadingWildcard() {
        assertEquals("*test*", service.callWrapQuery("*test"));
    }

    @Test
    public void test_wrapQuery_withTrailingWildcard() {
        assertEquals("*test*", service.callWrapQuery("test*"));
    }

    @Test
    public void test_wrapQuery_withBothWildcards() {
        assertEquals("*test*", service.callWrapQuery("*test*"));
    }

    @Test
    public void test_wrapQuery_emptyString() {
        assertEquals("**", service.callWrapQuery(""));
    }

    @Test
    public void test_wrapQuery_singleWildcard() {
        assertEquals("*", service.callWrapQuery("*"));
    }

    @Test
    public void test_wrapQuery_withSpaces() {
        assertEquals("*test query*", service.callWrapQuery("test query"));
    }

    @Test
    public void test_wrapQuery_withSpecialCharacters() {
        assertEquals("*test@example.com*", service.callWrapQuery("test@example.com"));
    }

    @Test
    public void test_wrapQuery_multipleInternalWildcards() {
        assertEquals("*test*query*search*", service.callWrapQuery("test*query*search"));
    }

    @Test
    public void test_wrapQuery_longRandomTextIsKept() {
        final StringBuilder sb = new StringBuilder();
        final java.util.Random random = new java.util.Random(1);
        for (int i = 0; i < 5000; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }
        assertEquals("*" + sb + "*", service.callWrapQuery(sb.toString()));
    }

    @Test
    public void test_wrapQuery_repeatedTextUpToTheLimit() {
        final String text = "x".repeat(314);
        assertEquals("*" + text + "*", service.callWrapQuery(text));
    }

    @Test
    public void test_wrapQuery_tooComplexIsInvalidQuery() {
        // the search engine refuses these with "Determinizing automaton ... would require more than 10000 effort" (HTTP 500 "all shards failed" before)
        for (final String text : new String[] { "x".repeat(315), "x".repeat(5000), "a?".repeat(50), "abcdefghij".repeat(100) }) {
            try {
                service.callWrapQuery(text);
                fail("expected InvalidQueryException for a pattern of " + text.length() + " characters");
            } catch (final InvalidQueryException e) {
                assertNotNull(e.getMessageCode());
            }
        }
    }

    @Test
    public void test_checkWildcard_prefixPatternIsKept() {
        // a pattern without a leading wildcard is a prefix: it stays cheap at any length
        final String pattern = "x".repeat(5000) + "*";
        assertEquals(pattern, FessAppService.checkWildcard(pattern));
    }

    // Test subclass to access protected method
    private static class TestFessAppService extends FessAppService {
        public String callWrapQuery(final String query) {
            return wrapQuery(query);
        }
    }
}
