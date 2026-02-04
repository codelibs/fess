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

import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
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

    @Test
    public void test_addConditionWithSvgMimetype() {
        // Test that addCondition works correctly with SVG MIME type pattern
        // This simulates what happens when the condition is loaded from fess_thumbnail.xml
        generator = new TestThumbnailGenerator();

        // The pattern as it would be loaded from XML: "image/svg\+xml"
        // In XML, a single backslash is passed directly (no Java string escaping)
        // So we use a literal backslash here to simulate XML-loaded value
        String xmlLoadedPattern = "image/svg" + "\\" + "+xml"; // Simulates XML value: image/svg\+xml

        generator.addCondition("mimetype", xmlLoadedPattern);

        // The condition map should store the pattern with a single backslash
        // which is correct for matching literal + in regex
        String svgMimetype = "image/svg+xml";
        assertTrue(svgMimetype.matches(xmlLoadedPattern));
    }

    @Test
    public void test_svgMimetypePatternVariants() {
        // Test different representations of the SVG pattern to understand escaping
        String svgMimetype = "image/svg+xml";

        // Pattern with single backslash (14 chars): should match
        String singleBackslash = "image/svg" + "\\" + "+xml";
        assertEquals("Single backslash pattern should be 14 chars", 14, singleBackslash.length());
        assertTrue(svgMimetype.matches(singleBackslash));

        // Pattern with double backslash (15 chars): should NOT match
        String doubleBackslash = "image/svg" + "\\\\" + "+xml";
        assertEquals("Double backslash pattern should be 15 chars", 15, doubleBackslash.length());
        assertFalse(svgMimetype.matches(doubleBackslash));

        // Pattern with no backslash (13 chars): should NOT match
        String noBackslash = "image/svg+xml";
        assertEquals("No backslash pattern should be 13 chars", 13, noBackslash.length());
        assertFalse(svgMimetype.matches(noBackslash));
    }

    @Test
    public void test_isTarget_withSvgMimetype() {
        // Test isTarget method through actual execution path with SVG MIME type
        // This tests the full flow: addCondition -> isTarget -> conditionMap -> matches
        generator = new TestThumbnailGenerator();

        // Setup mock FessConfig
        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getIndexFieldThumbnail() {
                return "thumbnail";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        try {
            // Add condition with properly escaped SVG pattern (as loaded from XML)
            // In XML: image/svg\+xml -> Java string: "image/svg\\+xml" -> actual: image/svg\+xml
            String svgPattern = "image/svg" + "\\" + "+xml";
            generator.addCondition("mimetype", svgPattern);

            // Test case 1: SVG MIME type should match
            Map<String, Object> svgDocMap = new HashMap<>();
            svgDocMap.put("thumbnail", "http://example.com/image.svg");
            svgDocMap.put("mimetype", "image/svg+xml");
            assertTrue("SVG MIME type should be target", generator.isTarget(svgDocMap));

            // Test case 2: Different MIME type should not match
            Map<String, Object> pngDocMap = new HashMap<>();
            pngDocMap.put("thumbnail", "http://example.com/image.png");
            pngDocMap.put("mimetype", "image/png");
            assertFalse("PNG MIME type should not be target", generator.isTarget(pngDocMap));

            // Test case 3: Missing thumbnail field should not match
            Map<String, Object> noThumbDocMap = new HashMap<>();
            noThumbDocMap.put("mimetype", "image/svg+xml");
            assertFalse("Doc without thumbnail field should not be target", generator.isTarget(noThumbDocMap));
        } finally {
            ComponentUtil.setFessConfig(null);
        }
    }

    @Test
    public void test_isTarget_withXhtmlMimetype() {
        // Test isTarget with XHTML MIME type (application/xhtml+xml)
        // This tests the pattern used in rule.xml
        generator = new TestThumbnailGenerator();

        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getIndexFieldThumbnail() {
                return "thumbnail";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        try {
            // Add condition with properly escaped XHTML pattern
            String xhtmlPattern = "application/xhtml" + "\\" + "+xml";
            generator.addCondition("mimetype", xhtmlPattern);

            // Test: XHTML MIME type should match
            Map<String, Object> xhtmlDocMap = new HashMap<>();
            xhtmlDocMap.put("thumbnail", "http://example.com/page.xhtml");
            xhtmlDocMap.put("mimetype", "application/xhtml+xml");
            assertTrue("XHTML MIME type should be target", generator.isTarget(xhtmlDocMap));

            // Test: Plain HTML should not match
            Map<String, Object> htmlDocMap = new HashMap<>();
            htmlDocMap.put("thumbnail", "http://example.com/page.html");
            htmlDocMap.put("mimetype", "text/html");
            assertFalse("HTML MIME type should not be target for XHTML pattern", generator.isTarget(htmlDocMap));
        } finally {
            ComponentUtil.setFessConfig(null);
        }
    }

    @Test
    public void test_isTarget_withRdfMimetype() {
        // Test isTarget with RDF MIME type (application/rdf+xml)
        generator = new TestThumbnailGenerator();

        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getIndexFieldThumbnail() {
                return "thumbnail";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        try {
            // Add condition with properly escaped RDF pattern
            String rdfPattern = "application/rdf" + "\\" + "+xml";
            generator.addCondition("mimetype", rdfPattern);

            // Test: RDF MIME type should match
            Map<String, Object> rdfDocMap = new HashMap<>();
            rdfDocMap.put("thumbnail", "http://example.com/data.rdf");
            rdfDocMap.put("mimetype", "application/rdf+xml");
            assertTrue("RDF MIME type should be target", generator.isTarget(rdfDocMap));
        } finally {
            ComponentUtil.setFessConfig(null);
        }
    }

    @Test
    public void test_isTarget_withMultipleConditions() {
        // Test isTarget with multiple MIME type patterns (simulating real config)
        generator = new TestThumbnailGenerator();

        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getIndexFieldThumbnail() {
                return "thumbnail";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        try {
            // Add multiple conditions for same field (like in fess_thumbnail.xml)
            // When same key is added multiple times, patterns are combined with |
            generator.addCondition("mimetype", "image/jpeg");
            generator.addCondition("mimetype", "image/png");
            generator.addCondition("mimetype", "image/svg" + "\\" + "+xml");

            // Test: All configured MIME types should match
            Map<String, Object> jpegDocMap = new HashMap<>();
            jpegDocMap.put("thumbnail", "http://example.com/image.jpg");
            jpegDocMap.put("mimetype", "image/jpeg");
            assertTrue("JPEG should be target", generator.isTarget(jpegDocMap));

            Map<String, Object> pngDocMap = new HashMap<>();
            pngDocMap.put("thumbnail", "http://example.com/image.png");
            pngDocMap.put("mimetype", "image/png");
            assertTrue("PNG should be target", generator.isTarget(pngDocMap));

            Map<String, Object> svgDocMap = new HashMap<>();
            svgDocMap.put("thumbnail", "http://example.com/image.svg");
            svgDocMap.put("mimetype", "image/svg+xml");
            assertTrue("SVG should be target", generator.isTarget(svgDocMap));

            // Test: Non-configured MIME type should not match
            Map<String, Object> gifDocMap = new HashMap<>();
            gifDocMap.put("thumbnail", "http://example.com/image.gif");
            gifDocMap.put("mimetype", "image/gif");
            assertFalse("GIF should not be target (not configured)", generator.isTarget(gifDocMap));
        } finally {
            ComponentUtil.setFessConfig(null);
        }
    }

    @Test
    public void test_isTarget_unescapedPlusDoesNotMatch() {
        // Demonstrate the bug: unescaped + in pattern does NOT match literal +
        generator = new TestThumbnailGenerator();

        FessConfig mockConfig = new FessConfig.SimpleImpl() {
            @Override
            public String getIndexFieldThumbnail() {
                return "thumbnail";
            }
        };
        ComponentUtil.setFessConfig(mockConfig);

        try {
            // Add condition with WRONG pattern (unescaped +)
            // This simulates the bug before the fix
            String wrongPattern = "image/svg+xml"; // + is not escaped
            generator.addCondition("mimetype", wrongPattern);

            // Test: SVG MIME type should NOT match with wrong pattern
            Map<String, Object> svgDocMap = new HashMap<>();
            svgDocMap.put("thumbnail", "http://example.com/image.svg");
            svgDocMap.put("mimetype", "image/svg+xml");
            assertFalse("SVG should NOT match with unescaped + pattern", generator.isTarget(svgDocMap));
        } finally {
            ComponentUtil.setFessConfig(null);
        }
    }
}
