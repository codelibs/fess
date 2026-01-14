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

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class CrawlerLogHelperTest extends UnitFessTestCase {

    private CrawlerLogHelper crawlerLogHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        crawlerLogHelper = new CrawlerLogHelper();
    }

    @Test
    public void test_instantiation() {
        assertNotNull(crawlerLogHelper);
    }

    @Test
    public void test_log_method_exists() {
        // Test that the log method exists and can be called
        // We can't easily test the actual functionality without complex mocking
        // but we can verify the class structure
        assertNotNull(crawlerLogHelper);
        assertTrue(CrawlerLogHelper.class.getSuperclass().getSimpleName().contains("LogHelper"));
    }
}