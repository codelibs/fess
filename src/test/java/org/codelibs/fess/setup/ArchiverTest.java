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
package org.codelibs.fess.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ArchiverTest {

    @TempDir
    Path tempDir;

    private Path writeZip(final String name, final String... entries) throws Exception {
        final Path zip = tempDir.resolve(name);
        try (OutputStream out = Files.newOutputStream(zip); ZipOutputStream zos = new ZipOutputStream(out)) {
            for (final String entry : entries) {
                zos.putNextEntry(new ZipEntry(entry));
                if (!entry.endsWith("/")) {
                    zos.write(("content of " + entry).getBytes(StandardCharsets.UTF_8));
                }
                zos.closeEntry();
            }
        }
        return zip;
    }

    @Test
    public void test_extract_writesZipEntries() throws Exception {
        final Path zip = writeZip("a.zip", "opensearch-3.8.0/", "opensearch-3.8.0/bin/opensearch");
        final Path dest = tempDir.resolve("out");
        Archiver.extract(zip, dest);
        final Path extracted = dest.resolve("opensearch-3.8.0").resolve("bin").resolve("opensearch");
        assertTrue(Files.isRegularFile(extracted));
        assertEquals("content of opensearch-3.8.0/bin/opensearch", Files.readString(extracted));
    }

    @Test
    public void test_extract_createsParentDirectoriesForNestedEntries() throws Exception {
        final Path zip = writeZip("a.zip", "a/b/c/deep.txt");
        final Path dest = tempDir.resolve("out");
        Archiver.extract(zip, dest);
        assertTrue(Files.isRegularFile(dest.resolve("a").resolve("b").resolve("c").resolve("deep.txt")));
    }

    @Test
    public void test_extract_rejectsPathTraversal() throws Exception {
        final Path zip = writeZip("evil.zip", "../escaped.txt");
        final Path dest = tempDir.resolve("out");
        final SetupException e = assertThrows(SetupException.class, () -> Archiver.extract(zip, dest));
        assertTrue(e.getMessage().contains("escaped.txt"), e.getMessage());
        assertFalse(Files.exists(tempDir.resolve("escaped.txt")), "nothing may be written outside the destination");
    }

    @Test
    public void test_extract_rejectsAbsolutePathEntry() throws Exception {
        final Path zip = writeZip("evil2.zip", "/tmp/absolute.txt");
        final Path dest = tempDir.resolve("out");
        assertThrows(SetupException.class, () -> Archiver.extract(zip, dest));
    }

    @Test
    public void test_extract_unknownFormat() throws Exception {
        final Path file = tempDir.resolve("payload.rar");
        Files.writeString(file, "x");
        final SetupException e = assertThrows(SetupException.class, () -> Archiver.extract(file, tempDir.resolve("out")));
        assertTrue(e.getMessage().contains("payload.rar"), e.getMessage());
    }
}
