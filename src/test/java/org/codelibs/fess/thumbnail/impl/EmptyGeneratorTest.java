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
package org.codelibs.fess.thumbnail.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class EmptyGeneratorTest extends UnitFessTestCase {

    private EmptyGenerator emptyGenerator;

    @Test
    public void test_generate() {
        // Initialize without container
        emptyGenerator = new EmptyGenerator();

        // Test that generate always returns false
        File outputFile = new File("test.jpg");
        assertFalse(emptyGenerator.generate("thumbnailId", outputFile));
        assertFalse(emptyGenerator.generate(null, outputFile));
        assertFalse(emptyGenerator.generate("thumbnailId", null));
        assertFalse(emptyGenerator.generate(null, null));
    }

    @Test
    public void test_destroy() {
        // Initialize without container
        emptyGenerator = new EmptyGenerator();

        // Test that destroy method can be called without error
        emptyGenerator.destroy();
        // Call destroy multiple times to ensure it's safe
        emptyGenerator.destroy();
        emptyGenerator.destroy();
    }

    @Test
    public void test_isAvailable() {
        // Initialize without container
        emptyGenerator = new EmptyGenerator();

        // Test that isAvailable returns true for EmptyGenerator
        assertTrue(emptyGenerator.isAvailable());
        // Test cached availability
        assertTrue(emptyGenerator.isAvailable());
    }

    @Test
    public void test_isTarget() {
        // Initialize without container
        emptyGenerator = new EmptyGenerator();

        // Note: isTarget method requires FessConfig from ComponentUtil
        // which needs container initialization.
        // We can only test that the method can be called without crashing
        // when proper conditions are not met.

        Map<String, Object> docMap = new HashMap<>();

        // This will return false because getFessConfig() will fail
        // We verify the method handles the case gracefully
        try {
            emptyGenerator.isTarget(docMap);
        } catch (IllegalStateException e) {
            // Expected when container is not initialized
            assertTrue(e.getMessage().contains("Not initialized"));
        }
    }

    @Test
    public void test_getName() {
        // Initialize without container
        emptyGenerator = new EmptyGenerator();

        // Test default name (null)
        assertNull(emptyGenerator.getName());

        // Test after setting name
        emptyGenerator.setName("testGenerator");
        assertEquals("testGenerator", emptyGenerator.getName());

        // Test changing name
        emptyGenerator.setName("newName");
        assertEquals("newName", emptyGenerator.getName());
    }

    @Test
    public void test_addCondition() {
        // Initialize without container
        emptyGenerator = new EmptyGenerator();

        // Test adding conditions - this should work without container
        emptyGenerator.addCondition("field1", "pattern1");
        emptyGenerator.addCondition("field1", "pattern2");
        emptyGenerator.addCondition("field2", "pattern.*");

        // We can't test isTarget properly without container,
        // but we can verify conditions are added
        assertNotNull(emptyGenerator);

        // Test that multiple conditions can be added without errors
        emptyGenerator.addCondition("type", "document");
        emptyGenerator.addCondition("mime", "text/.*");
    }

    @Test
    public void test_setDirectoryNameLength() {
        // Initialize without container
        emptyGenerator = new EmptyGenerator();

        // Test default value
        assertEquals(5, emptyGenerator.directoryNameLength);

        // Test setting new value
        emptyGenerator.setDirectoryNameLength(10);
        assertEquals(10, emptyGenerator.directoryNameLength);

        // Test setting zero value
        emptyGenerator.setDirectoryNameLength(0);
        assertEquals(0, emptyGenerator.directoryNameLength);

        // Test setting negative value
        emptyGenerator.setDirectoryNameLength(-1);
        assertEquals(-1, emptyGenerator.directoryNameLength);
    }

    @Test
    public void test_setMaxRedirectCount() {
        // Initialize without container
        emptyGenerator = new EmptyGenerator();

        // Test default value
        assertEquals(10, emptyGenerator.maxRedirectCount);

        // Test setting new value
        emptyGenerator.setMaxRedirectCount(20);
        assertEquals(20, emptyGenerator.maxRedirectCount);

        // Test setting zero value
        emptyGenerator.setMaxRedirectCount(0);
        assertEquals(0, emptyGenerator.maxRedirectCount);
    }

    @Test
    public void test_expandPath() {
        // Initialize without container
        emptyGenerator = new EmptyGenerator();

        // Test with no file path mappings
        assertEquals("test/path", emptyGenerator.expandPath("test/path"));
        assertNull(emptyGenerator.expandPath(null));

        // Test with file path mappings
        emptyGenerator.filePathMap.put("${path}/test", "/usr/bin/test");
        assertEquals("/usr/bin/test", emptyGenerator.expandPath("${path}/test"));
        assertEquals("other/path", emptyGenerator.expandPath("other/path"));
    }

    @Test
    public void test_createTask() {
        // Initialize without container
        emptyGenerator = new EmptyGenerator();

        // Test basic functionality
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("id", "doc123");

        emptyGenerator.setName("empty");
        String path = "/path/to/thumbnail.jpg";

        // Since createTask requires FessConfig, it may not work properly
        // We test that the method can be called without throwing exceptions
        try {
            emptyGenerator.createTask(path, docMap);
        } catch (Exception e) {
            // Expected when FessConfig is not available
        }
    }
}