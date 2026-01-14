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

import java.util.Arrays;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FileTypeHelperTest extends UnitFessTestCase {

    private FileTypeHelper fileTypeHelper;

    @Override
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        fileTypeHelper = new FileTypeHelper();
    }

    @Test
    public void test_init() {
        try {
            fileTypeHelper.init();
        } catch (Exception e) {
            fail("init() should not throw an exception: " + e.getMessage());
        }
    }

    @Test
    public void test_add() {
        fileTypeHelper.add("application/pdf", "pdf");
        assertEquals("pdf", fileTypeHelper.get("application/pdf"));

        fileTypeHelper.add("text/plain", "text");
        assertEquals("text", fileTypeHelper.get("text/plain"));

        fileTypeHelper.add("image/jpeg", "image");
        assertEquals("image", fileTypeHelper.get("image/jpeg"));
    }

    @Test
    public void test_get() {
        fileTypeHelper.add("application/pdf", "pdf");
        fileTypeHelper.add("text/plain", "text");

        assertEquals("pdf", fileTypeHelper.get("application/pdf"));
        assertEquals("text", fileTypeHelper.get("text/plain"));

        // Test non-existent mimetype returns default value
        assertEquals("others", fileTypeHelper.get("non/existent"));
    }

    @Test
    public void test_get_withNullMimetype() {
        assertEquals("others", fileTypeHelper.get(null));
    }

    @Test
    public void test_get_withEmptyMimetype() {
        assertEquals("others", fileTypeHelper.get(""));
        assertEquals("others", fileTypeHelper.get(" "));
    }

    @Test
    public void test_get_withBlankFiletype() {
        fileTypeHelper.add("test/blank", "");
        assertEquals("others", fileTypeHelper.get("test/blank"));

        fileTypeHelper.add("test/null", null);
        assertEquals("others", fileTypeHelper.get("test/null"));
    }

    @Test
    public void test_getDefaultValue() {
        assertEquals("others", fileTypeHelper.getDefaultValue());
    }

    @Test
    public void test_setDefaultValue() {
        fileTypeHelper.setDefaultValue("unknown");
        assertEquals("unknown", fileTypeHelper.getDefaultValue());

        // Test that get() returns new default value
        assertEquals("unknown", fileTypeHelper.get("non/existent"));
    }

    @Test
    public void test_setDefaultValue_withNull() {
        fileTypeHelper.setDefaultValue(null);
        assertNull(fileTypeHelper.getDefaultValue());
        assertNull(fileTypeHelper.get("non/existent"));
    }

    @Test
    public void test_setDefaultValue_withEmpty() {
        fileTypeHelper.setDefaultValue("");
        assertEquals("", fileTypeHelper.getDefaultValue());
        assertEquals("", fileTypeHelper.get("non/existent"));
    }

    @Test
    public void test_getTypes() {
        String[] types = fileTypeHelper.getTypes();
        assertNotNull(types);
        assertEquals(0, types.length);

        fileTypeHelper.add("application/pdf", "pdf");
        fileTypeHelper.add("text/plain", "text");
        fileTypeHelper.add("image/jpeg", "image");

        types = fileTypeHelper.getTypes();
        assertEquals(3, types.length);

        Arrays.sort(types);
        assertEquals("image", types[0]);
        assertEquals("pdf", types[1]);
        assertEquals("text", types[2]);
    }

    @Test
    public void test_getTypes_withDuplicates() {
        fileTypeHelper.add("application/pdf", "document");
        fileTypeHelper.add("application/msword", "document");
        fileTypeHelper.add("text/plain", "text");

        String[] types = fileTypeHelper.getTypes();
        assertEquals(2, types.length);

        Arrays.sort(types);
        assertEquals("document", types[0]);
        assertEquals("text", types[1]);
    }

    @Test
    public void test_getTypes_empty() {
        String[] types = fileTypeHelper.getTypes();
        assertNotNull(types);
        assertEquals(0, types.length);
    }

    @Test
    public void test_add_overwrite() {
        fileTypeHelper.add("application/pdf", "pdf");
        assertEquals("pdf", fileTypeHelper.get("application/pdf"));

        fileTypeHelper.add("application/pdf", "document");
        assertEquals("document", fileTypeHelper.get("application/pdf"));
    }

    @Test
    public void test_add_withNullMimetype() {
        fileTypeHelper.add(null, "test");
        assertEquals("test", fileTypeHelper.get(null));
    }

    @Test
    public void test_add_withEmptyMimetype() {
        fileTypeHelper.add("", "empty");
        assertEquals("empty", fileTypeHelper.get(""));
    }

    @Test
    public void test_add_withNullFiletype() {
        fileTypeHelper.add("test/null", null);
        assertEquals("others", fileTypeHelper.get("test/null"));
    }

    @Test
    public void test_add_withEmptyFiletype() {
        fileTypeHelper.add("test/empty", "");
        assertEquals("others", fileTypeHelper.get("test/empty"));
    }
}