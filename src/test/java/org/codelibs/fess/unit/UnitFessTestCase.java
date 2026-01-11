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
package org.codelibs.fess.unit;

import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.utflute.lastaflute.WebContainerTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

public abstract class UnitFessTestCase extends WebContainerTestCase {
    @Override
    protected String prepareConfigFile() {
        return "test_app.xml";
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown();
    }

    @Override
    protected boolean isAssertionEqualsNumberInsensitive() {
        return true;
    }

    // ===== Assert methods for JUnit 5 compatibility =====

    // fail with message
    protected void fail(String message) {
        Assertions.fail(message);
    }

    // assertTrue - JUnit 4 style (message first)
    protected void assertTrue(String message, boolean condition) {
        Assertions.assertTrue(condition, message);
    }

    // assertTrue - JUnit 5 style (message last)
    protected void assertTrue(boolean condition, String message) {
        Assertions.assertTrue(condition, message);
    }

    // assertFalse - JUnit 4 style (message first)
    protected void assertFalse(String message, boolean condition) {
        Assertions.assertFalse(condition, message);
    }

    // assertFalse - JUnit 5 style (message last)
    protected void assertFalse(boolean condition, String message) {
        Assertions.assertFalse(condition, message);
    }

    // assertNull - JUnit 5 style only (to avoid ambiguity with String actual)
    protected void assertNull(Object actual, String message) {
        Assertions.assertNull(actual, message);
    }

    // assertNotNull - JUnit 5 style only (to avoid ambiguity with String actual)
    protected void assertNotNull(Object actual, String message) {
        Assertions.assertNotNull(actual, message);
    }

    // assertEquals - JUnit 4 style (message first) - Object version
    protected void assertEquals(String message, Object expected, Object actual) {
        Assertions.assertEquals(expected, actual, message);
    }

    // assertEquals - JUnit 4 style (message first) - int version
    protected void assertEquals(String message, int expected, int actual) {
        Assertions.assertEquals(expected, actual, message);
    }

    // assertSame - JUnit 5 style (no message)
    protected void assertSame(Object expected, Object actual) {
        Assertions.assertSame(expected, actual);
    }

    // assertSame - JUnit 5 style (with message)
    protected void assertSame(Object expected, Object actual, String message) {
        Assertions.assertSame(expected, actual, message);
    }
}
