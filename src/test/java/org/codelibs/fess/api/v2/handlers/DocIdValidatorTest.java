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
package org.codelibs.fess.api.v2.handlers;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class DocIdValidatorTest extends UnitFessTestCase {

    @Test
    public void test_valid_uuid() {
        assertTrue(new DocIdValidator().isValid("0123456789abcdef0123456789abcdef"));
    }

    @Test
    public void test_valid_512() {
        assertTrue(new DocIdValidator().isValid("a".repeat(512)));
    }

    @Test
    public void test_invalid_513() {
        assertFalse(new DocIdValidator().isValid("a".repeat(513)));
    }

    @Test
    public void test_invalid_empty() {
        assertFalse(new DocIdValidator().isValid(""));
    }

    @Test
    public void test_invalid_char() {
        assertFalse(new DocIdValidator().isValid("a/b"));
    }
}
