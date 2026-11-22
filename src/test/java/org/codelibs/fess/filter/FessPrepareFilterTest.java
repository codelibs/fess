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
package org.codelibs.fess.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FessPrepareFilterTest {

    @TempDir
    Path tempDir;

    @Test
    public void test_duplicatedPlugins_namesThePluginAndBothJars() throws Exception {
        Files.writeString(tempDir.resolve("fess-script-ognl-15.8.0.jar"), "x");
        Files.writeString(tempDir.resolve("fess-script-ognl-15.9.0.jar"), "x");
        Files.writeString(tempDir.resolve("fess-ds-git-15.9.0.jar"), "x");

        final List<String> messages = FessPrepareFilter.duplicatedPlugins(tempDir);
        assertEquals(1, messages.size(), messages.toString());
        final String message = messages.get(0);
        assertTrue(message.contains("fess-script-ognl"), message);
        assertTrue(message.contains("fess-script-ognl-15.8.0.jar"), message);
        assertTrue(message.contains("fess-script-ognl-15.9.0.jar"), message);
        assertTrue(!message.contains("fess-ds-git"), message);
    }

    @Test
    public void test_duplicatedPlugins_isEmptyWhenEachPluginIsInstalledOnce() throws Exception {
        Files.writeString(tempDir.resolve("fess-webapp-mcp-15.9.0.jar"), "x");
        Files.writeString(tempDir.resolve("fess-webapp-mcp-client-15.9.0.jar"), "x");
        assertEquals(List.of(), FessPrepareFilter.duplicatedPlugins(tempDir));
    }

    @Test
    public void test_duplicatedPlugins_ignoresJarsThatAreNotPlugins() throws Exception {
        Files.writeString(tempDir.resolve("some-library-1.0.jar"), "x");
        Files.writeString(tempDir.resolve("some-library-2.0.jar"), "x");
        assertEquals(List.of(), FessPrepareFilter.duplicatedPlugins(tempDir));
    }

    @Test
    public void test_duplicatedPlugins_isEmptyWhenTheDirectoryIsAbsent() {
        assertEquals(List.of(), FessPrepareFilter.duplicatedPlugins(tempDir.resolve("none")));
    }

    private static void writeJar(final Path file) throws Exception {
        try (java.util.zip.ZipOutputStream out = new java.util.zip.ZipOutputStream(Files.newOutputStream(file))) {
            out.putNextEntry(new java.util.zip.ZipEntry("META-INF/MANIFEST.MF"));
            out.write("Manifest-Version: 1.0\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            out.closeEntry();
        }
    }

    @Test
    public void test_unreadablePlugins_namesEachJarThatIsNotAZip() throws Exception {
        Files.write(tempDir.resolve("fess-ds-zero-15.10.0.jar"), new byte[0]);
        Files.writeString(tempDir.resolve("fess-ds-text-15.10.0.jar"), "this is text");
        writeJar(tempDir.resolve("fess-ds-good-15.10.0.jar"));
        final Path truncated = tempDir.resolve("fess-ds-trunc-15.10.0.jar");
        writeJar(truncated);
        final byte[] bytes = Files.readAllBytes(truncated);
        Files.write(truncated, java.util.Arrays.copyOf(bytes, bytes.length / 2));

        final List<String> messages = FessPrepareFilter.unreadablePlugins(tempDir);
        assertEquals(3, messages.size(), messages.toString());
        assertTrue(messages.get(0).contains("fess-ds-text-15.10.0.jar"), messages.toString());
        assertTrue(messages.get(0).contains("fess-setup remove plugin fess-ds-text"), messages.get(0));
        assertTrue(messages.get(1).contains("fess-ds-trunc-15.10.0.jar"), messages.toString());
        assertTrue(messages.get(2).contains("fess-ds-zero-15.10.0.jar"), messages.toString());
        assertTrue(messages.stream().noneMatch(m -> m.contains("fess-ds-good")), messages.toString());
    }

    @Test
    public void test_unreadablePlugins_ignoresFilesThatAreNotJars() throws Exception {
        Files.writeString(tempDir.resolve("readme.txt"), "x");
        writeJar(tempDir.resolve("fess-ds-good-15.10.0.jar"));
        assertEquals(List.of(), FessPrepareFilter.unreadablePlugins(tempDir));
    }

    @Test
    public void test_unreadablePlugins_isEmptyWhenTheDirectoryIsAbsent() {
        assertEquals(List.of(), FessPrepareFilter.unreadablePlugins(tempDir.resolve("none")));
    }
}
