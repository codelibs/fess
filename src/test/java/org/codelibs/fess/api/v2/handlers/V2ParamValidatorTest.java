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

public class V2ParamValidatorTest extends UnitFessTestCase {

    @Test
    public void test_checkMaxLength_ok() {
        assertEquals("abc", V2ParamValidator.checkMaxLength("abc", 3, "q"));
        assertNull(V2ParamValidator.checkMaxLength(null, 3, "q"));
    }

    @Test
    public void test_checkMaxLength_over() {
        try {
            V2ParamValidator.checkMaxLength("abcd", 3, "q");
            fail();
        } catch (final InvalidRequestParameterException e) {
            assertTrue(e.getMessage().contains("q"));
        }
    }

    @Test
    public void test_checkArray_items_over() {
        try {
            V2ParamValidator.checkArray(new String[] { "a", "b", "c" }, 2, 10, "ex_q");
            fail();
        } catch (final InvalidRequestParameterException e) {
            assertTrue(e.getMessage().contains("ex_q"));
        }
    }

    @Test
    public void test_checkArray_elementLength_over() {
        try {
            V2ParamValidator.checkArray(new String[] { "abcdef" }, 10, 3, "ex_q");
            fail();
        } catch (final InvalidRequestParameterException e) {
            // expected
        }
    }

    @Test
    public void test_checkArray_null_ok() {
        assertNull(V2ParamValidator.checkArray(null, 2, 10, "ex_q"));
    }
}
