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
package org.codelibs.fess.app.web.admin.searchlog;

import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class AdminSearchlogActionTest extends UnitFessTestCase {

    @Test
    public void test_isValidTab() {
        assertTrue(AdminSearchlogAction.isValidTab("overview"));
        assertTrue(AdminSearchlogAction.isValidTab("queries"));
        assertTrue(AdminSearchlogAction.isValidTab("audience"));
        assertFalse(AdminSearchlogAction.isValidTab("logs"));
        assertFalse(AdminSearchlogAction.isValidTab("../x"));
        assertFalse(AdminSearchlogAction.isValidTab(null));
    }

    @Test
    public void test_toEmbeddedJson_escapes() {
        final String json = AdminSearchlogAction.toEmbeddedJson(Map.of("w", "</script><img src=x onerror=alert(1)>&'\u2028\u2029"));
        assertFalse(json.contains("<"));
        assertFalse(json.contains(">"));
        assertFalse(json.contains("&"));
        assertFalse(json.contains("'"));
        assertFalse(json.contains("\u2028"));
        assertFalse(json.contains("\u2029"));
        assertTrue(json.contains("\\u003c/script\\u003e"));
    }
}
