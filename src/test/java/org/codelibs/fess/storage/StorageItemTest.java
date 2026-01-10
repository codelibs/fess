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
package org.codelibs.fess.storage;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.codelibs.fess.unit.UnitFessTestCase;

public class StorageItemTest extends UnitFessTestCase {

    public void test_constructorAndGetters() {
        final String name = "test-file.txt";
        final String path = "/documents/";
        final boolean directory = false;
        final long size = 1024L;
        final ZonedDateTime lastModified = ZonedDateTime.of(2025, 1, 15, 10, 30, 0, 0, ZoneId.systemDefault());
        final String encodedId = "dGVzdC1maWxlLnR4dA==";

        final StorageItem item = new StorageItem(name, path, directory, size, lastModified, encodedId);

        assertEquals(name, item.getName());
        assertEquals(path, item.getPath());
        assertFalse(item.isDirectory());
        assertEquals(size, item.getSize());
        assertEquals(lastModified, item.getLastModified());
        assertEquals(encodedId, item.getEncodedId());
    }

    public void test_directoryItem() {
        final String name = "documents";
        final String path = "/";
        final boolean directory = true;
        final long size = 0L;
        final String encodedId = "ZG9jdW1lbnRzLw==";

        final StorageItem item = new StorageItem(name, path, directory, size, null, encodedId);

        assertEquals(name, item.getName());
        assertEquals(path, item.getPath());
        assertTrue(item.isDirectory());
        assertEquals(0L, item.getSize());
        assertNull(item.getLastModified());
        assertEquals(encodedId, item.getEncodedId());
    }

    public void test_fileItem() {
        final ZonedDateTime now = ZonedDateTime.now();
        final StorageItem item = new StorageItem("report.pdf", "/reports/2025/", false, 2048576L, now, "cmVwb3J0LnBkZg==");

        assertEquals("report.pdf", item.getName());
        assertEquals("/reports/2025/", item.getPath());
        assertFalse(item.isDirectory());
        assertEquals(2048576L, item.getSize());
        assertEquals(now, item.getLastModified());
    }

    public void test_nullValues() {
        final StorageItem item = new StorageItem(null, null, false, 0L, null, null);

        assertNull(item.getName());
        assertNull(item.getPath());
        assertFalse(item.isDirectory());
        assertEquals(0L, item.getSize());
        assertNull(item.getLastModified());
        assertNull(item.getEncodedId());
    }

    public void test_emptyName() {
        final StorageItem item = new StorageItem("", "/path/", false, 100L, null, "");

        assertEquals("", item.getName());
        assertEquals("/path/", item.getPath());
        assertEquals("", item.getEncodedId());
    }

    public void test_largeFileSize() {
        final long largeSize = 10737418240L; // 10 GB
        final StorageItem item = new StorageItem("large-file.zip", "/backups/", false, largeSize, null, "bGFyZ2UtZmlsZS56aXA=");

        assertEquals(largeSize, item.getSize());
    }

    public void test_rootPath() {
        final StorageItem item = new StorageItem("root-file.txt", "", false, 512L, null, "cm9vdC1maWxlLnR4dA==");

        assertEquals("root-file.txt", item.getName());
        assertEquals("", item.getPath());
    }

    public void test_deeplyNestedPath() {
        final String deepPath = "/level1/level2/level3/level4/level5/";
        final StorageItem item = new StorageItem("nested.txt", deepPath, false, 256L, null, "bmVzdGVkLnR4dA==");

        assertEquals(deepPath, item.getPath());
    }

    public void test_specialCharactersInName() {
        final String specialName = "file with spaces & symbols!@#.txt";
        final StorageItem item = new StorageItem(specialName, "/special/", false, 100L, null, "c3BlY2lhbA==");

        assertEquals(specialName, item.getName());
    }

    public void test_unicodeInName() {
        final String unicodeName = "ファイル名.txt";
        final StorageItem item = new StorageItem(unicodeName, "/unicode/", false, 200L, null, "dW5pY29kZQ==");

        assertEquals(unicodeName, item.getName());
    }
}
