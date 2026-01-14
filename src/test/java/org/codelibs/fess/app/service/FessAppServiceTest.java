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

    // Test subclass to access protected method
    private static class TestFessAppService extends FessAppService {
        public String callWrapQuery(final String query) {
            return wrapQuery(query);
        }
    }
}
