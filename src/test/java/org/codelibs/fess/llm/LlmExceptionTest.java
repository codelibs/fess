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
package org.codelibs.fess.llm;

import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LlmExceptionTest extends UnitFessTestCase {

    @BeforeEach
    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void test_constructorWithMessage() {
        final LlmException exception = new LlmException("Error message");
        assertEquals("Error message", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    public void test_constructorWithMessageAndCause() {
        final RuntimeException cause = new RuntimeException("Root cause");
        final LlmException exception = new LlmException("Error occurred", cause);
        assertEquals("Error occurred", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    public void test_extendsFromFessSystemException() {
        final LlmException exception = new LlmException("Test");
        assertTrue(exception instanceof FessSystemException);
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void test_throwAndCatch() {
        boolean caught = false;
        try {
            throw new LlmException("Test exception");
        } catch (final LlmException e) {
            caught = true;
            assertEquals("Test exception", e.getMessage());
        }
        assertTrue(caught);
    }

    @Test
    public void test_catchAsFessSystemException() {
        boolean caught = false;
        try {
            throw new LlmException("Test exception");
        } catch (final FessSystemException e) {
            caught = true;
            assertEquals("Test exception", e.getMessage());
        }
        assertTrue(caught);
    }

    @Test
    public void test_catchAsRuntimeException() {
        boolean caught = false;
        try {
            throw new LlmException("Test exception");
        } catch (final RuntimeException e) {
            caught = true;
            assertEquals("Test exception", e.getMessage());
        }
        assertTrue(caught);
    }

    @Test
    public void test_nestedCause() {
        final IllegalArgumentException rootCause = new IllegalArgumentException("Invalid argument");
        final RuntimeException middleCause = new RuntimeException("Middle error", rootCause);
        final LlmException exception = new LlmException("Top level error", middleCause);

        assertEquals("Top level error", exception.getMessage());
        assertSame(middleCause, exception.getCause());
        assertSame(rootCause, exception.getCause().getCause());
    }

    @Test
    public void test_nullMessage() {
        final LlmException exception = new LlmException(null);
        assertNull(exception.getMessage());
    }

    @Test
    public void test_emptyMessage() {
        final LlmException exception = new LlmException("");
        assertEquals("", exception.getMessage());
    }
}
