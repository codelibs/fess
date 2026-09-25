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
package org.codelibs.fess.exception;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class UserRoleLoginExceptionTest extends UnitFessTestCase {

    @Test
    public void test_isARuntimeException() {
        assertTrue(new UserRoleLoginException() instanceof RuntimeException);
    }

    @Test
    public void test_carriesNoStackTrace() {
        // It is thrown on every denied admin request, so filling a stack trace would be wasted work.
        final UserRoleLoginException exception = new UserRoleLoginException();
        assertNull(exception.fillInStackTrace());
        assertEquals(0, exception.getStackTrace().length);
    }
}
