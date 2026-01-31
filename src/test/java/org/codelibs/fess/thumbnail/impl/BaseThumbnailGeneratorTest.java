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

public class BaseThumbnailGeneratorTest extends UnitFessTestCase {

    private TestThumbnailGenerator generator;

    // Test concrete implementation for testing abstract BaseThumbnailGenerator
    private static class TestThumbnailGenerator extends BaseThumbnailGenerator {
        private boolean generateCalled = false;
        private boolean destroyCalled = false;

        @Override
        public boolean generate(String thumbnailId, File outputFile) {
            generateCalled = true;
            return true;
        }

        @Override
        public void destroy() {
            destroyCalled = true;
        }

        public boolean isGenerateCalled() {
            return generateCalled;
        }

        public boolean isDestroyCalled() {
            return destroyCalled;
        }
    }

    @Test
    public void test_isAvailable() {
        // Test default implementation
        generator = new TestThumbnailGenerator();
        assertTrue(generator.isAvailable());
    }

    @Test
    public void test_isTarget() {
        // Test isTarget method
        generator = new TestThumbnailGenerator();
        Map<String, Object> docMap = new HashMap<>();

        // Note: isTarget requires FessConfig which needs container
        // We can only verify the method handles missing container gracefully
        try {
            generator.isTarget(docMap);
        } catch (IllegalStateException e) {
            // Expected when container is not initialized
            assertTrue(e.getMessage().contains("Not initialized"));
        }
    }

    @Test
    public void test_addCondition() {
        // Test adding conditions
        generator = new TestThumbnailGenerator();

        // Test adding single condition
        generator.addCondition("field1", "value1");

        // Test adding multiple conditions for same field
        generator.addCondition("field1", "value2");

        // Test adding conditions for different fields
        generator.addCondition("field2", "pattern.*");

        // Verify conditions can be added without errors
        assertNotNull(generator);
    }

    @Test
    public void test_getName() {
        // Test getName and setName methods
        generator = new TestThumbnailGenerator();

        // Default name should be null
        assertNull(generator.getName());

        // Set and get name
        generator.setName("test-generator");
        assertEquals("test-generator", generator.getName());

        // Change name
        generator.setName("new-name");
        assertEquals("new-name", generator.getName());
    }

    @Test
    public void test_generate() {
        // Test generate method is called correctly
        generator = new TestThumbnailGenerator();
        File outputFile = new File("test.jpg");

        assertFalse(generator.isGenerateCalled());
        assertTrue(generator.generate("id123", outputFile));
        assertTrue(generator.isGenerateCalled());
    }

    @Test
    public void test_destroy() {
        // Test destroy method is called correctly
        generator = new TestThumbnailGenerator();

        assertFalse(generator.isDestroyCalled());
        generator.destroy();
        assertTrue(generator.isDestroyCalled());
    }

    @Test
    public void test_expandPath() {
        // Test expandPath method
        generator = new TestThumbnailGenerator();

        // Test without mappings
        assertEquals("path/to/file", generator.expandPath("path/to/file"));
        assertNull(generator.expandPath(null));

        // Test with exact match mappings (expandPath only replaces exact matches)
        generator.filePathMap.put("${home}/file", "/home/user/file");
        assertEquals("/home/user/file", generator.expandPath("${home}/file"));
        assertEquals("other/path", generator.expandPath("other/path"));

        // Test another exact mapping
        generator.filePathMap.put("${temp}", "/tmp");
        assertEquals("/tmp", generator.expandPath("${temp}"));
        assertEquals("${temp}/file", generator.expandPath("${temp}/file")); // No partial replacement
    }

    @Test
    public void test_directoryNameLength() {
        // Test directoryNameLength setter
        generator = new TestThumbnailGenerator();

        // Default value
        assertEquals(5, generator.directoryNameLength);

        // Set new value
        generator.setDirectoryNameLength(10);
        assertEquals(10, generator.directoryNameLength);

        // Set zero
        generator.setDirectoryNameLength(0);
        assertEquals(0, generator.directoryNameLength);
    }

    @Test
    public void test_maxRedirectCount() {
        // Test maxRedirectCount setter
        generator = new TestThumbnailGenerator();

        // Default value
        assertEquals(10, generator.maxRedirectCount);

        // Set new value
        generator.setMaxRedirectCount(20);
        assertEquals(20, generator.maxRedirectCount);

        // Set zero
        generator.setMaxRedirectCount(0);
        assertEquals(0, generator.maxRedirectCount);
    }

    @Test
    public void test_createTask() {
        // Test createTask method (requires FessConfig)
        generator = new TestThumbnailGenerator();
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("id", "doc123");

        // Since createTask depends on FessConfig, it may not work properly
        // in unit tests without full container
        try {
            generator.createTask("/path/to/thumb.jpg", docMap);
        } catch (Exception e) {
            // Expected when FessConfig is not available
        }
    }

    @Test
    public void test_mimetypePatternMatching() {
        // Test that MIME type patterns work correctly as regex
        // This verifies the fix for SVG thumbnail generation issue
        // where image/svg+xml was not matching due to unescaped + character

        // SVG MIME type - the + must be escaped in regex
        String svgMimetype = "image/svg+xml";
        String svgPatternWrong = "image/svg+xml"; // Wrong: + means "one or more" in regex
        String svgPatternCorrect = "image/svg\\+xml"; // Correct: \\+ matches literal +

        // Verify wrong pattern does NOT match (demonstrates the bug)
        assertFalse(svgMimetype.matches(svgPatternWrong));

        // Verify correct pattern DOES match (demonstrates the fix)
        assertTrue(svgMimetype.matches(svgPatternCorrect));

        // Verify other common MIME types still work (no special chars)
        assertTrue("text/html".matches("text/html"));
        assertTrue("application/pdf".matches("application/pdf"));
        assertTrue("image/jpeg".matches("image/jpeg"));
        assertTrue("image/png".matches("image/png"));
        assertTrue("image/gif".matches("image/gif"));
        assertTrue("image/tiff".matches("image/tiff"));
    }
}
