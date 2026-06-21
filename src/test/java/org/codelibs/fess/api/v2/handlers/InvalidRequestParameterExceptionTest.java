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

public class InvalidRequestParameterExceptionTest extends UnitFessTestCase {

    @Test
    public void test_isRuntimeException() {
        final InvalidRequestParameterException e = new InvalidRequestParameterException("bad");
        assertTrue(e instanceof RuntimeException);
        assertEquals("bad", e.getMessage());
    }

    @Test
    public void test_pageSizeIsSubtype() {
        assertTrue(InvalidRequestParameterException.class.isAssignableFrom(V2JsonRequestParams.InvalidPageSizeException.class));
    }
}
