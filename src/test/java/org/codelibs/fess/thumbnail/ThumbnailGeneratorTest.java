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
package org.codelibs.fess.thumbnail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.unit.UnitFessTestCase;

public class ThumbnailGeneratorTest extends UnitFessTestCase {

    private ThumbnailGenerator thumbnailGenerator;
    private File tempOutputFile;
    private Path tempDir;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        tempDir = Files.createTempDirectory("thumbnail-test");
        tempOutputFile = Files.createTempFile(tempDir, "thumbnail", ".png").toFile();

        // Create a test implementation of ThumbnailGenerator
        thumbnailGenerator = new TestThumbnailGenerator();
    }

    @Override
    public void tearDown() throws Exception {
        // Clean up temp files
        if (tempOutputFile != null && tempOutputFile.exists()) {
            tempOutputFile.delete();
        }
        if (tempDir != null) {
            Files.walk(tempDir).sorted((a, b) -> b.compareTo(a)).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    // Ignore cleanup errors
                }
            });
        }
        super.tearDown();
    }

    public void test_getName() {
        // Test getting the generator name
        assertEquals("TestGenerator", thumbnailGenerator.getName());
    }

    public void test_generate_withValidThumbnailId() {
        // Test successful thumbnail generation
        String thumbnailId = "test-thumbnail-001";
        assertTrue(thumbnailGenerator.generate(thumbnailId, tempOutputFile));
        assertTrue(tempOutputFile.exists());
    }

    public void test_generate_withNullThumbnailId() {
        // Test generation with null thumbnail ID
        assertFalse(thumbnailGenerator.generate(null, tempOutputFile));
    }

    public void test_generate_withEmptyThumbnailId() {
        // Test generation with empty thumbnail ID
        assertFalse(thumbnailGenerator.generate("", tempOutputFile));
    }

    public void test_generate_withNullOutputFile() {
        // Test generation with null output file
        String thumbnailId = "test-thumbnail-002";
        assertFalse(thumbnailGenerator.generate(thumbnailId, null));
    }

    public void test_generate_withInvalidOutputPath() throws IOException {
        // Test generation with non-writable output file
        File readOnlyDir = Files.createTempDirectory(tempDir, "readonly").toFile();
        readOnlyDir.setWritable(false);
        File invalidOutputFile = new File(readOnlyDir, "thumbnail.png");

        String thumbnailId = "test-thumbnail-003";
        assertFalse(thumbnailGenerator.generate(thumbnailId, invalidOutputFile));

        // Cleanup
        readOnlyDir.setWritable(true);
    }

    public void test_isTarget_withSupportedMimeType() {
        // Test with supported document type
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("mimetype", "application/pdf");
        assertTrue(thumbnailGenerator.isTarget(docMap));
    }

    public void test_isTarget_withUnsupportedMimeType() {
        // Test with unsupported document type
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("mimetype", "application/unknown");
        assertFalse(thumbnailGenerator.isTarget(docMap));
    }

    public void test_isTarget_withNullDocMap() {
        // Test with null document map
        assertFalse(thumbnailGenerator.isTarget(null));
    }

    public void test_isTarget_withEmptyDocMap() {
        // Test with empty document map
        Map<String, Object> docMap = new HashMap<>();
        assertFalse(thumbnailGenerator.isTarget(docMap));
    }

    public void test_isTarget_withNullMimeType() {
        // Test with null mimetype value
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("mimetype", null);
        assertFalse(thumbnailGenerator.isTarget(docMap));
    }

    public void test_isAvailable() {
        // Test availability check
        assertTrue(thumbnailGenerator.isAvailable());
    }

    public void test_destroy() {
        // Test destroy method - should not throw exception
        thumbnailGenerator.destroy();
        // After destroy, generator should not be available
        assertFalse(thumbnailGenerator.isAvailable());
    }

    public void test_createTask_withValidParams() {
        // Test task creation with valid parameters
        String path = "/path/to/document.pdf";
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("mimetype", "application/pdf");
        docMap.put("title", "Test Document");

        Tuple3<String, String, String> task = thumbnailGenerator.createTask(path, docMap);
        assertNotNull(task);
        assertNotNull(task.getValue1());
        assertNotNull(task.getValue2());
        assertNotNull(task.getValue3());
        assertEquals("task-id", task.getValue1());
        assertEquals(path, task.getValue2());
        assertEquals("application/pdf", task.getValue3());
    }

    public void test_createTask_withNullPath() {
        // Test task creation with null path
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("mimetype", "application/pdf");

        Tuple3<String, String, String> task = thumbnailGenerator.createTask(null, docMap);
        assertNull(task);
    }

    public void test_createTask_withEmptyPath() {
        // Test task creation with empty path
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("mimetype", "application/pdf");

        Tuple3<String, String, String> task = thumbnailGenerator.createTask("", docMap);
        assertNull(task);
    }

    public void test_createTask_withNullDocMap() {
        // Test task creation with null document map
        String path = "/path/to/document.pdf";

        Tuple3<String, String, String> task = thumbnailGenerator.createTask(path, null);
        assertNull(task);
    }

    public void test_createTask_withEmptyDocMap() {
        // Test task creation with empty document map
        String path = "/path/to/document.pdf";
        Map<String, Object> docMap = new HashMap<>();

        Tuple3<String, String, String> task = thumbnailGenerator.createTask(path, docMap);
        assertNull(task);
    }

    public void test_multipleGenerations() {
        // Test multiple thumbnail generations
        for (int i = 0; i < 5; i++) {
            String thumbnailId = "test-thumbnail-" + i;
            File outputFile = new File(tempDir.toFile(), "thumbnail-" + i + ".png");
            assertTrue(thumbnailGenerator.generate(thumbnailId, outputFile));
            assertTrue(outputFile.exists());
        }
    }

    public void test_isTarget_withVariousMimeTypes() {
        // Test with various MIME types
        String[] supportedTypes = { "application/pdf", "image/jpeg", "image/png" };
        String[] unsupportedTypes = { "text/plain", "application/xml", "audio/mp3" };

        for (String mimeType : supportedTypes) {
            Map<String, Object> docMap = new HashMap<>();
            docMap.put("mimetype", mimeType);
            assertTrue("Should support " + mimeType, thumbnailGenerator.isTarget(docMap));
        }

        for (String mimeType : unsupportedTypes) {
            Map<String, Object> docMap = new HashMap<>();
            docMap.put("mimetype", mimeType);
            assertFalse("Should not support " + mimeType, thumbnailGenerator.isTarget(docMap));
        }
    }

    public void test_concurrentOperations() throws InterruptedException {
        // Test thread safety with concurrent operations
        final int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        final boolean[] results = new boolean[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                String thumbnailId = "concurrent-" + index;
                File outputFile = new File(tempDir.toFile(), "concurrent-" + index + ".png");
                results[index] = thumbnailGenerator.generate(thumbnailId, outputFile);
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        for (boolean result : results) {
            assertTrue(result);
        }
    }

    /**
     * Test implementation of ThumbnailGenerator interface
     */
    private static class TestThumbnailGenerator implements ThumbnailGenerator {

        private boolean available = true;

        @Override
        public String getName() {
            return "TestGenerator";
        }

        @Override
        public boolean generate(String thumbnailId, File outputFile) {
            // Validate inputs
            if (thumbnailId == null || thumbnailId.isEmpty() || outputFile == null) {
                return false;
            }

            // Check if parent directory is writable
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.canWrite()) {
                return false;
            }

            // Simulate thumbnail generation
            try {
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                // Write some dummy content to simulate actual thumbnail
                Files.write(outputFile.toPath(), ("thumbnail-" + thumbnailId).getBytes());
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        public boolean isTarget(Map<String, Object> docMap) {
            if (docMap == null || docMap.isEmpty()) {
                return false;
            }

            Object mimeType = docMap.get("mimetype");
            if (mimeType == null) {
                return false;
            }

            String mimeTypeStr = mimeType.toString();
            // Support PDF and common image formats
            return "application/pdf".equals(mimeTypeStr) || "image/jpeg".equals(mimeTypeStr) || "image/png".equals(mimeTypeStr);
        }

        @Override
        public boolean isAvailable() {
            return available;
        }

        @Override
        public void destroy() {
            available = false;
        }

        @Override
        public Tuple3<String, String, String> createTask(String path, Map<String, Object> docMap) {
            // Validate inputs
            if (path == null || path.isEmpty() || docMap == null || docMap.isEmpty()) {
                return null;
            }

            Object mimeType = docMap.get("mimetype");
            if (mimeType == null) {
                return null;
            }

            // Create a task tuple
            return new Tuple3<>("task-id", path, mimeType.toString());
        }
    }
}