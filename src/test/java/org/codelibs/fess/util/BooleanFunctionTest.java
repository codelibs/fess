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
package org.codelibs.fess.util;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class BooleanFunctionTest extends UnitFessTestCase {

    @Test
    public void test_apply_string() {
        BooleanFunction<String> isNotEmpty = s -> s != null && !s.isEmpty();

        assertTrue(isNotEmpty.apply("test"));
        assertTrue(isNotEmpty.apply("a"));
        assertFalse(isNotEmpty.apply(""));
        assertFalse(isNotEmpty.apply(null));
    }

    @Test
    public void test_apply_integer() {
        BooleanFunction<Integer> isPositive = n -> n != null && n > 0;

        assertTrue(isPositive.apply(1));
        assertTrue(isPositive.apply(100));
        assertFalse(isPositive.apply(0));
        assertFalse(isPositive.apply(-1));
        assertFalse(isPositive.apply(null));
    }

    @Test
    public void test_apply_object() {
        BooleanFunction<Object> isNotNull = obj -> obj != null;

        assertTrue(isNotNull.apply("test"));
        assertTrue(isNotNull.apply(123));
        assertTrue(isNotNull.apply(new Object()));
        assertFalse(isNotNull.apply(null));
    }

    @Test
    public void test_apply_boolean() {
        BooleanFunction<Boolean> identity = b -> b != null && b;

        assertTrue(identity.apply(true));
        assertFalse(identity.apply(false));
        assertFalse(identity.apply(null));
    }

    @Test
    public void test_lambda_expression() {
        BooleanFunction<String> startsWithHello = s -> s != null && s.startsWith("hello");

        assertTrue(startsWithHello.apply("hello world"));
        assertTrue(startsWithHello.apply("hello"));
        assertFalse(startsWithHello.apply("hi world"));
        assertFalse(startsWithHello.apply(""));
        assertFalse(startsWithHello.apply(null));
    }

    @Test
    public void test_method_reference() {
        BooleanFunction<String> isEmpty = String::isEmpty;

        assertTrue(isEmpty.apply(""));
        assertFalse(isEmpty.apply("test"));
        assertFalse(isEmpty.apply("a"));
    }
}