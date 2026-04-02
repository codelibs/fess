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
package org.codelibs.fess.mylasta.direction.sponsor;

import java.lang.reflect.Field;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import jakarta.servlet.ServletContext;

public class FessMultipartRequestHandlerTest extends UnitFessTestCase {

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        super.tearDown(testInfo);
    }

    @Test
    public void test_contextTempdirKey_equalsJakartaConstant() throws Exception {
        final Field field = FessMultipartRequestHandler.class.getDeclaredField("CONTEXT_TEMPDIR_KEY");
        field.setAccessible(true);
        final String value = (String) field.get(null);
        assertEquals(ServletContext.TEMPDIR, value);
    }

    @Test
    public void test_contextTempdirKey_isJakartaNamespace() throws Exception {
        final Field field = FessMultipartRequestHandler.class.getDeclaredField("CONTEXT_TEMPDIR_KEY");
        field.setAccessible(true);
        final String value = (String) field.get(null);
        assertEquals("jakarta.servlet.context.tempdir", value);
        assertFalse(value.startsWith("javax."));
    }

    @Test
    public void test_jakartaServletContextTempdir_value() {
        assertEquals("jakarta.servlet.context.tempdir", ServletContext.TEMPDIR);
    }

    @Test
    public void test_javaIoTmpdirKey() throws Exception {
        final Field field = FessMultipartRequestHandler.class.getDeclaredField("JAVA_IO_TMPDIR_KEY");
        field.setAccessible(true);
        final String value = (String) field.get(null);
        assertEquals("java.io.tmpdir", value);
    }

    @Test
    public void test_instantiation() {
        final FessMultipartRequestHandler handler = new FessMultipartRequestHandler();
        assertNotNull(handler);
    }
}
