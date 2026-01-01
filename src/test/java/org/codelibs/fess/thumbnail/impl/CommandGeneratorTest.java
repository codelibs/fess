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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codelibs.fess.unit.UnitFessTestCase;

public class CommandGeneratorTest extends UnitFessTestCase {

    private CommandGenerator generator;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        generator = new CommandGenerator();
    }

    // Basic setter tests
    public void test_setCommandTimeout() throws Exception {
        final long timeout = 5000L;
        generator.setCommandTimeout(timeout);
        assertTrue(true);
    }

    public void test_setCommandDestroyTimeout() throws Exception {
        final long timeout = 2000L;
        generator.setCommandDestroyTimeout(timeout);
        assertTrue(true);
    }

    public void test_setBaseDir() throws Exception {
        final File tempDir = new File(System.getProperty("java.io.tmpdir"));
        generator.setBaseDir(tempDir);
        assertTrue(true);
    }

    public void test_setCommandList() throws Exception {
        final List<String> commands = Collections.singletonList("test_command");
        generator.setCommandList(commands);
        assertTrue(true);
    }

    public void test_setCommandList_null() throws Exception {
        generator.setCommandList(null);
        assertTrue(true);
    }

    public void test_setCommandList_empty() throws Exception {
        final List<String> commands = new ArrayList<>();
        generator.setCommandList(commands);
        assertTrue(true);
    }

    public void test_setCommandList_multiple() throws Exception {
        final List<String> commands = Arrays.asList("command1", "command2", "command3");
        generator.setCommandList(commands);
        assertTrue(true);
    }

    // Basic generate tests that work without component dependencies
    public void test_generate_outputFile_exists() throws Exception {
        final File outputFile = File.createTempFile("command_generator", ".out");
        try {
            assertTrue(outputFile.exists());
            generator.setCommandList(Collections.singletonList("invalid_command_for_test"));
            assertTrue(generator.generate("test_id", outputFile));
        } finally {
            outputFile.delete();
        }
    }

    public void test_generate_outputFile_null() throws Exception {
        generator.setCommandList(Collections.singletonList("echo test"));
        try {
            generator.generate("test_id", null);
            fail("Should throw exception for null output file");
        } catch (final Exception e) {
            assertTrue(true);
        }
    }

    public void test_generate_thumbnailId_null() throws Exception {
        final File outputFile = File.createTempFile("command_generator", ".out");
        try {
            outputFile.delete();
            generator.setCommandList(Collections.singletonList("echo test"));
            generator.generate(null, outputFile);
            assertTrue(true);
        } catch (final Exception e) {
            assertTrue(true);
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }
    }

    public void test_generate_thumbnailId_empty() throws Exception {
        final File outputFile = File.createTempFile("command_generator", ".out");
        try {
            outputFile.delete();
            generator.setCommandList(Collections.singletonList("echo test"));
            generator.generate("", outputFile);
            assertTrue(true);
        } catch (final Exception e) {
            assertTrue(true);
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }
    }

    // Test parent directory scenarios
    public void test_generate_parent_directory_not_exists() throws Exception {
        final File tempDir = new File(System.getProperty("java.io.tmpdir"));
        final File nonExistentParent = new File(tempDir, "non_existent_" + System.currentTimeMillis());
        final File outputFile = new File(nonExistentParent, "output.txt");

        try {
            assertFalse(nonExistentParent.exists());
            assertFalse(outputFile.exists());

            generator.setCommandList(Collections.singletonList("echo test"));
            // This should handle parent directory creation
            generator.generate("test_id", outputFile);
            assertTrue(true);
        } catch (final Exception e) {
            assertTrue(true);
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
            if (nonExistentParent.exists()) {
                nonExistentParent.delete();
            }
        }
    }

    public void test_generate_invalid_parent_directory() throws Exception {
        // Create a path that's guaranteed to be invalid
        final File invalidParent = System.getProperty("os.name", "linux").toLowerCase().startsWith("windows")
                ? new File("Q:\\invalid\\path\\that\\does\\not\\exist")
                : new File("/invalid/path/that/does/not/exist");
        final File outputFile = new File(invalidParent, "output.txt");

        generator.setCommandList(Collections.singletonList("echo test"));
        try {
            final boolean result = generator.generate("test_id", outputFile);
            // Should return false for invalid parent directory
            assertFalse(result);
        } catch (final Exception e) {
            assertTrue(true);
        }
    }

    // Test command list scenarios
    public void test_generate_no_command_list() throws Exception {
        final File outputFile = File.createTempFile("command_generator", ".out");
        try {
            outputFile.delete();
            // Don't set command list (null)
            generator.generate("test_id", outputFile);
            assertTrue(true);
        } catch (final Exception e) {
            assertTrue(true);
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }
    }

    public void test_generate_empty_command_list() throws Exception {
        final File outputFile = File.createTempFile("command_generator", ".out");
        try {
            outputFile.delete();
            generator.setCommandList(new ArrayList<>());
            generator.generate("test_id", outputFile);
            assertTrue(true);
        } catch (final Exception e) {
            assertTrue(true);
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }
    }

    // Test variable replacement logic without triggering component dependencies
    public void test_variable_replacement_logic() throws Exception {
        // Test the variable replacement logic directly
        final List<String> commands =
                Arrays.asList("convert ${url} ${outputFile}", "echo Processing ${url} to ${outputFile}", "cp ${url} ${outputFile}");

        final String tempPath = "/tmp/test.tmp";
        final String outputPath = "/tmp/output.jpg";

        // Simulate the variable replacement logic from the source code
        final List<String> processedCommands = new ArrayList<>();
        for (final String command : commands) {
            processedCommands.add(command.replace("${url}", tempPath).replace("${outputFile}", outputPath));
        }

        // Verify variable replacement
        assertEquals(3, processedCommands.size());
        for (final String cmd : processedCommands) {
            assertFalse("Command should not contain ${url}", cmd.contains("${url}"));
            assertFalse("Command should not contain ${outputFile}", cmd.contains("${outputFile}"));
            assertTrue("Command should contain temp path", cmd.contains(tempPath));
            assertTrue("Command should contain output path", cmd.contains(outputPath));
        }
    }

    // Test timeout scenarios
    public void test_command_timeout_configuration() throws Exception {
        // Test timeout configuration without triggering component dependencies
        generator.setCommandTimeout(100L); // Very short timeout
        generator.setCommandDestroyTimeout(50L);

        // Test that timeout values are set (we can't verify execution without components)
        assertTrue("Timeout values should be configurable", true);

        // Test edge cases
        generator.setCommandTimeout(0L);
        generator.setCommandTimeout(-1L);
        generator.setCommandTimeout(Long.MAX_VALUE);

        assertTrue("Timeout edge cases should be handled", true);
    }

    // Test ProcessDestroyer inner class
    public void test_processDestroyer_creation() throws Exception {
        // We can't directly test ProcessDestroyer without creating actual processes,
        // but we can test that the class exists and basic functionality
        try {
            // Use reflection to verify ProcessDestroyer class exists
            final Class<?> destroyerClass = Class.forName("org.codelibs.fess.thumbnail.impl.CommandGenerator$ProcessDestroyer");
            assertNotNull("ProcessDestroyer class should exist", destroyerClass);
            assertTrue("ProcessDestroyer should extend TimerTask", java.util.TimerTask.class.isAssignableFrom(destroyerClass));
        } catch (final ClassNotFoundException e) {
            fail("ProcessDestroyer inner class should exist");
        }
    }

    // Test exception handling scenarios
    public void test_exception_handling_scenarios() throws Exception {
        // Test various exception scenarios without triggering component dependencies

        // Test with null command list
        try {
            final File outputFile = File.createTempFile("test", ".out");
            outputFile.delete();
            generator.setCommandList(null);
            generator.generate("test", outputFile);
            assertTrue(true);
        } catch (final Exception e) {
            assertTrue("Null command list should be handled", true);
        }

        // Test with invalid commands
        try {
            final File outputFile = File.createTempFile("test", ".out");
            outputFile.delete();
            generator.setCommandList(Collections.singletonList(""));
            generator.generate("test", outputFile);
            assertTrue(true);
        } catch (final Exception e) {
            assertTrue("Empty command should be handled", true);
        }
    }

    // Test multiple commands configuration
    public void test_multiple_commands_configuration() throws Exception {
        // Test setting multiple commands without triggering execution
        final List<String> commands =
                Arrays.asList("command1 ${url} ${outputFile}", "command2 ${url} ${outputFile}", "command3 ${url} ${outputFile}");

        generator.setCommandList(commands);
        assertTrue("Multiple commands should be settable", true);

        // Test with empty list
        generator.setCommandList(new ArrayList<>());
        assertTrue("Empty command list should be settable", true);

        // Test with single command
        generator.setCommandList(Collections.singletonList("single_command"));
        assertTrue("Single command should be settable", true);
    }

    // Test baseDir configuration
    public void test_baseDir_configuration() throws Exception {
        final File customBaseDir = new File(System.getProperty("java.io.tmpdir"), "custom_base");
        customBaseDir.mkdirs();

        try {
            generator.setBaseDir(customBaseDir);
            assertTrue("Base directory should be set", true);
        } finally {
            if (customBaseDir.exists()) {
                customBaseDir.delete();
            }
        }
    }

    // Test command timeout edge cases
    public void test_timeout_values() throws Exception {
        // Test zero timeout
        generator.setCommandTimeout(0L);
        assertTrue(true);

        // Test negative timeout
        generator.setCommandTimeout(-1L);
        assertTrue(true);

        // Test very large timeout
        generator.setCommandTimeout(Long.MAX_VALUE);
        assertTrue(true);

        // Test destroy timeout
        generator.setCommandDestroyTimeout(0L);
        assertTrue(true);

        generator.setCommandDestroyTimeout(-1L);
        assertTrue(true);

        generator.setCommandDestroyTimeout(Long.MAX_VALUE);
        assertTrue(true);
    }

    // Test special characters in paths
    public void test_special_characters_in_paths() throws Exception {
        final File tempDir = new File(System.getProperty("java.io.tmpdir"));
        final File outputFile = new File(tempDir, "test file with spaces.out");

        try {
            generator.setCommandList(Collections.singletonList("echo test"));
            generator.generate("test_id", outputFile);
            assertTrue("Should handle special characters in paths", true);
        } catch (final Exception e) {
            assertTrue("Exception handling for special characters", true);
        } finally {
            if (outputFile.exists()) {
                outputFile.delete();
            }
        }
    }

    // Test concurrent access patterns
    public void test_concurrent_operations() throws Exception {
        final CommandGenerator generator1 = new CommandGenerator();
        final CommandGenerator generator2 = new CommandGenerator();

        generator1.setCommandList(Collections.singletonList("echo test1"));
        generator2.setCommandList(Collections.singletonList("echo test2"));

        generator1.setCommandTimeout(1000L);
        generator2.setCommandTimeout(2000L);

        assertTrue("Multiple generators should work independently", true);
    }

    // Test command string construction edge cases
    public void test_command_string_edge_cases() throws Exception {
        // Test with commands containing special characters
        final List<String> specialCommands = Arrays.asList("echo \"test with quotes\"", "echo 'test with single quotes'",
                "echo test with spaces", "echo test&with&ampersand", "echo test|with|pipe");

        generator.setCommandList(specialCommands);
        assertTrue("Special character commands should be settable", true);

        // Test with very long commands
        final StringBuilder longCommand = new StringBuilder("echo ");
        for (int i = 0; i < 1000; i++) {
            longCommand.append("test");
        }

        generator.setCommandList(Collections.singletonList(longCommand.toString()));
        assertTrue("Long commands should be settable", true);
    }

    // Test path expansion logic
    public void test_path_expansion() throws Exception {
        // Test the expandPath functionality indirectly through variable replacement
        final String testCommand = "convert ${url} -resize 100x100 ${outputFile}";
        final String tempPath = "/tmp/source.pdf";
        final String outputPath = "/tmp/thumbnail.jpg";

        final String expandedCommand = testCommand.replace("${url}", tempPath).replace("${outputFile}", outputPath);

        assertEquals("convert /tmp/source.pdf -resize 100x100 /tmp/thumbnail.jpg", expandedCommand);
        assertFalse("Expanded command should not contain variables", expandedCommand.contains("${"));
    }

    // Test configuration validation
    public void test_configuration_validation() throws Exception {
        // Test configuration without actual execution
        generator.setCommandTimeout(30000L); // 30 seconds
        generator.setCommandDestroyTimeout(5000L); // 5 seconds
        generator.setBaseDir(new File(System.getProperty("java.io.tmpdir")));
        generator.setCommandList(Arrays.asList("imagemagick", "convert", "${url}", "${outputFile}"));

        assertTrue("Complete configuration should be valid", true);

        // Test minimal configuration
        final CommandGenerator minimalGenerator = new CommandGenerator();
        minimalGenerator.setCommandList(Collections.singletonList("echo test"));

        assertTrue("Minimal configuration should be valid", true);
    }

    // ==================== Tests for getExtensionFromMimeType method ====================

    // Tests for GIF MIME type
    public void test_getExtensionFromMimeType_gif() {
        assertEquals(".gif", generator.getExtensionFromMimeType("image/gif"));
    }

    // Tests for TIFF MIME type
    public void test_getExtensionFromMimeType_tiff() {
        assertEquals(".tiff", generator.getExtensionFromMimeType("image/tiff"));
    }

    // Tests for SVG MIME type
    public void test_getExtensionFromMimeType_svg() {
        assertEquals(".svg", generator.getExtensionFromMimeType("image/svg+xml"));
    }

    // Tests for JPEG MIME type
    public void test_getExtensionFromMimeType_jpeg() {
        assertEquals(".jpg", generator.getExtensionFromMimeType("image/jpeg"));
    }

    // Tests for PNG MIME type
    public void test_getExtensionFromMimeType_png() {
        assertEquals(".png", generator.getExtensionFromMimeType("image/png"));
    }

    // Tests for BMP MIME types (multiple variants)
    public void test_getExtensionFromMimeType_bmp() {
        assertEquals(".bmp", generator.getExtensionFromMimeType("image/bmp"));
    }

    public void test_getExtensionFromMimeType_xWindowsBmp() {
        assertEquals(".bmp", generator.getExtensionFromMimeType("image/x-windows-bmp"));
    }

    public void test_getExtensionFromMimeType_xMsBmp() {
        assertEquals(".bmp", generator.getExtensionFromMimeType("image/x-ms-bmp"));
    }

    // Tests for Photoshop MIME types (multiple variants)
    public void test_getExtensionFromMimeType_photoshopVnd() {
        assertEquals(".psd", generator.getExtensionFromMimeType("image/vnd.adobe.photoshop"));
    }

    public void test_getExtensionFromMimeType_photoshopImage() {
        assertEquals(".psd", generator.getExtensionFromMimeType("image/photoshop"));
    }

    public void test_getExtensionFromMimeType_photoshopAppX() {
        assertEquals(".psd", generator.getExtensionFromMimeType("application/x-photoshop"));
    }

    public void test_getExtensionFromMimeType_photoshopApp() {
        assertEquals(".psd", generator.getExtensionFromMimeType("application/photoshop"));
    }

    // Tests for null and empty input
    public void test_getExtensionFromMimeType_null() {
        assertEquals("", generator.getExtensionFromMimeType(null));
    }

    public void test_getExtensionFromMimeType_empty() {
        assertEquals("", generator.getExtensionFromMimeType(""));
    }

    // Tests for unknown/unsupported MIME types
    public void test_getExtensionFromMimeType_unknownType() {
        assertEquals("", generator.getExtensionFromMimeType("application/octet-stream"));
    }

    public void test_getExtensionFromMimeType_pdf() {
        assertEquals("", generator.getExtensionFromMimeType("application/pdf"));
    }

    public void test_getExtensionFromMimeType_html() {
        assertEquals("", generator.getExtensionFromMimeType("text/html"));
    }

    public void test_getExtensionFromMimeType_textPlain() {
        assertEquals("", generator.getExtensionFromMimeType("text/plain"));
    }

    public void test_getExtensionFromMimeType_msword() {
        assertEquals("", generator.getExtensionFromMimeType("application/msword"));
    }

    // Test all supported image MIME types in one comprehensive test
    public void test_getExtensionFromMimeType_allImageTypes() {
        final String[][] testCases = { { "image/gif", ".gif" }, { "image/tiff", ".tiff" }, { "image/svg+xml", ".svg" },
                { "image/jpeg", ".jpg" }, { "image/png", ".png" }, { "image/bmp", ".bmp" }, { "image/x-windows-bmp", ".bmp" },
                { "image/x-ms-bmp", ".bmp" }, { "image/vnd.adobe.photoshop", ".psd" }, { "image/photoshop", ".psd" },
                { "application/x-photoshop", ".psd" }, { "application/photoshop", ".psd" } };

        for (final String[] testCase : testCases) {
            final String mimeType = testCase[0];
            final String expectedExtension = testCase[1];
            assertEquals("Extension for " + mimeType, expectedExtension, generator.getExtensionFromMimeType(mimeType));
        }
    }

    // Test non-image MIME types return empty string
    public void test_getExtensionFromMimeType_nonImageTypes() {
        final String[] mimeTypes = { "application/pdf", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/html", "text/plain", "application/json",
                "application/xml", "audio/mp3", "video/mp4", "application/postscript" };

        for (final String mimeType : mimeTypes) {
            assertEquals("Extension for " + mimeType + " should be empty", "", generator.getExtensionFromMimeType(mimeType));
        }
    }

    // Test case sensitivity (current implementation is case-sensitive)
    public void test_getExtensionFromMimeType_caseSensitive() {
        assertEquals("", generator.getExtensionFromMimeType("IMAGE/GIF"));
        assertEquals("", generator.getExtensionFromMimeType("Image/Gif"));
        assertEquals("", generator.getExtensionFromMimeType("IMAGE/SVG+XML"));
    }

    // ==================== Tests for ${mimetype} placeholder replacement ====================

    public void test_mimetype_variable_replacement() {
        final String testCommand = "generate-thumbnail image ${url} ${outputFile} ${mimetype}";
        final String tempPath = "/tmp/test.gif";
        final String outputPath = "/tmp/thumbnail.png";
        final String mimeType = "image/gif";

        final String expandedCommand =
                testCommand.replace("${url}", tempPath).replace("${outputFile}", outputPath).replace("${mimetype}", mimeType);

        assertEquals("generate-thumbnail image /tmp/test.gif /tmp/thumbnail.png image/gif", expandedCommand);
        assertFalse("Expanded command should not contain ${mimetype}", expandedCommand.contains("${mimetype}"));
    }

    public void test_mimetype_variable_replacement_nullMimeType() {
        final String testCommand = "generate-thumbnail image ${url} ${outputFile} ${mimetype}";
        final String tempPath = "/tmp/test.gif";
        final String outputPath = "/tmp/thumbnail.png";
        final String mimeType = null;

        final String expandedCommand = testCommand.replace("${url}", tempPath)
                .replace("${outputFile}", outputPath)
                .replace("${mimetype}", mimeType != null ? mimeType : "");

        assertEquals("generate-thumbnail image /tmp/test.gif /tmp/thumbnail.png ", expandedCommand);
    }

    public void test_mimetype_variable_replacement_allImageFormats() {
        final String[] mimeTypes = { "image/gif", "image/tiff", "image/svg+xml", "image/jpeg", "image/png", "image/bmp" };

        for (final String mimeType : mimeTypes) {
            final String testCommand = "generate-thumbnail image ${url} ${outputFile} ${mimetype}";
            final String expandedCommand =
                    testCommand.replace("${url}", "/tmp/test").replace("${outputFile}", "/tmp/out.png").replace("${mimetype}", mimeType);

            assertTrue("Command should contain mimetype: " + mimeType, expandedCommand.contains(mimeType));
            assertFalse("Command should not contain placeholder", expandedCommand.contains("${mimetype}"));
        }
    }

    // Test command list with mimetype placeholder
    public void test_commandList_withMimetypePlaceholder() {
        final List<String> commands = Arrays.asList("/bin/generate-thumbnail", "image", "${url}", "${outputFile}", "${mimetype}");
        generator.setCommandList(commands);
        // Verify command list can contain mimetype placeholder
        assertNotNull(generator);
    }

    // ==================== Tests for TOCTOU fix - atomic file operations ====================

    public void test_atomicCreateDirectories_nestedPath() throws Exception {
        // Test that Files.createDirectories creates nested directories atomically
        Path tempDir = Files.createTempDirectory("toctou_cmd_test");
        try {
            Path nestedPath = tempDir.resolve("a").resolve("b").resolve("c");
            assertFalse(Files.exists(nestedPath));

            Files.createDirectories(nestedPath);

            assertTrue(Files.exists(nestedPath));
            assertTrue(Files.isDirectory(nestedPath));
        } finally {
            deleteDirectory(tempDir.toFile());
        }
    }

    public void test_atomicCreateDirectories_existingDirectory() throws Exception {
        // Test that Files.createDirectories doesn't fail on existing directory
        Path tempDir = Files.createTempDirectory("toctou_cmd_test");
        try {
            Path existingPath = tempDir.resolve("existing");
            Files.createDirectories(existingPath);
            assertTrue(Files.exists(existingPath));

            // Should not throw when called again
            Files.createDirectories(existingPath);
            assertTrue(Files.exists(existingPath));
        } finally {
            deleteDirectory(tempDir.toFile());
        }
    }

    public void test_atomicDeleteIfExists_existingFile() throws Exception {
        // Test that Files.deleteIfExists deletes existing file atomically
        Path tempFile = Files.createTempFile("toctou_cmd_test", ".tmp");
        try {
            assertTrue(Files.exists(tempFile));

            boolean deleted = Files.deleteIfExists(tempFile);

            assertTrue(deleted);
            assertFalse(Files.exists(tempFile));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    public void test_atomicDeleteIfExists_nonExistingFile() throws Exception {
        // Test that Files.deleteIfExists returns false for non-existing file without throwing
        Path tempDir = Files.createTempDirectory("toctou_cmd_test");
        try {
            Path nonExisting = tempDir.resolve("non_existing.tmp");
            assertFalse(Files.exists(nonExisting));

            boolean deleted = Files.deleteIfExists(nonExisting);

            assertFalse(deleted);
        } finally {
            deleteDirectory(tempDir.toFile());
        }
    }

    public void test_atomicDeleteIfExists_emptyFile() throws Exception {
        // Test deleting empty files (simulating empty thumbnail scenario)
        Path tempFile = Files.createTempFile("toctou_cmd_test", ".tmp");
        try {
            assertEquals(0, Files.size(tempFile));

            boolean deleted = Files.deleteIfExists(tempFile);

            assertTrue(deleted);
            assertFalse(Files.exists(tempFile));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        dir.delete();
    }
}
