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

public class WebFsIndexHelperUserInfoTest extends UnitFessTestCase {

    @Test
    public void test_warnUserInfo_warnsWithoutTheCredentials() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(WebFsIndexHelper.class);
        try {
            new WebFsIndexHelper().warnUserInfo("http://user:s3cret@example.com/start.html");
            final List<String> warnings = appender.warnings();
            assertEquals(1, warnings.size());
            assertTrue(warnings.get(0).contains("http://example.com/start.html"));
            assertTrue(warnings.get(0).contains("Web Authentication"));
            assertFalse(warnings.get(0).contains("s3cret"));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_warnUserInfo_hidesAPasswordWithSpecialCharacters() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(WebFsIndexHelper.class);
        try {
            // the userinfo may hold characters that a URL should not: an "@", a space, a ":"
            new WebFsIndexHelper().warnUserInfo("http://user:p@ss:w@rd@example.com/start.html");
            final List<String> warnings = appender.warnings();
            assertEquals(1, warnings.size());
            assertTrue(warnings.get(0).contains("http://example.com/start.html"));
            assertFalse(warnings.get(0).contains("ss:w"));
            assertFalse(warnings.get(0).contains("rd@"));
            assertFalse(warnings.get(0).contains("user:"));
        } finally {
            appender.detach();
        }
    }

    @Test
    public void test_warnUserInfo_silentForOtherUrls() {
        final LogCapturingAppender appender = LogCapturingAppender.attach(WebFsIndexHelper.class);
        try {
            final WebFsIndexHelper helper = new WebFsIndexHelper();
            helper.warnUserInfo("http://example.com/start.html");
            helper.warnUserInfo("http://example.com:8080/a@b");
            assertTrue(appender.warnings().isEmpty());
        } finally {
            appender.detach();
        }
    }
}
