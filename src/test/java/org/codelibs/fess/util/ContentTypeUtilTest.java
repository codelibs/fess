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
package org.codelibs.fess.util;

import org.codelibs.fess.unit.UnitFessTestCase;

/**
 * Test class for ContentTypeUtil.
 * Tests content type detection from file paths and extensions.
 */
public class ContentTypeUtilTest extends UnitFessTestCase {

    public void test_getContentType_textFormats() {
        assertEquals("text/plain", ContentTypeUtil.getContentType("test.txt"));
        assertEquals("text/html;charset=utf-8", ContentTypeUtil.getContentType("index.html"));
        assertEquals("text/css", ContentTypeUtil.getContentType("style.css"));
        assertEquals("text/javascript", ContentTypeUtil.getContentType("script.js"));
        assertEquals("application/json", ContentTypeUtil.getContentType("data.json"));
        assertEquals("application/xml", ContentTypeUtil.getContentType("config.xml"));
    }

    public void test_getContentType_fontFormats() {
        assertEquals("application/vnd.ms-fontobject", ContentTypeUtil.getContentType("font.eot"));
        assertEquals("font/otf", ContentTypeUtil.getContentType("font.otf"));
        assertEquals("font/ttf", ContentTypeUtil.getContentType("font.ttf"));
        assertEquals("font/woff", ContentTypeUtil.getContentType("font.woff"));
        assertEquals("font/woff2", ContentTypeUtil.getContentType("font.woff2"));
    }

    public void test_getContentType_imageFormats() {
        assertEquals("image/vnd.microsoft.icon", ContentTypeUtil.getContentType("favicon.ico"));
        assertEquals("image/svg+xml", ContentTypeUtil.getContentType("logo.svg"));
        assertEquals("image/png", ContentTypeUtil.getContentType("image.png"));
        assertEquals("image/jpeg", ContentTypeUtil.getContentType("photo.jpg"));
        assertEquals("image/jpeg", ContentTypeUtil.getContentType("photo.jpeg"));
        assertEquals("image/gif", ContentTypeUtil.getContentType("animation.gif"));
        assertEquals("image/webp", ContentTypeUtil.getContentType("modern.webp"));
    }

    public void test_getContentType_documentFormats() {
        assertEquals("application/pdf", ContentTypeUtil.getContentType("document.pdf"));
        assertEquals("application/zip", ContentTypeUtil.getContentType("archive.zip"));
        assertEquals("application/x-tar", ContentTypeUtil.getContentType("archive.tar"));
        assertEquals("application/gzip", ContentTypeUtil.getContentType("archive.gz"));
    }

    public void test_getContentType_withPath() {
        assertEquals("text/javascript", ContentTypeUtil.getContentType("/path/to/script.js"));
        assertEquals("text/css", ContentTypeUtil.getContentType("/static/css/style.css"));
        assertEquals("image/png", ContentTypeUtil.getContentType("../images/logo.png"));
    }

    public void test_getContentType_caseInsensitive() {
        assertEquals("text/javascript", ContentTypeUtil.getContentType("SCRIPT.JS"));
        assertEquals("text/css", ContentTypeUtil.getContentType("Style.CSS"));
        assertEquals("image/png", ContentTypeUtil.getContentType("Image.PNG"));
    }

    public void test_getContentType_directoryPath() {
        assertEquals("text/html;charset=utf-8", ContentTypeUtil.getContentType("/path/to/directory/"));
        assertEquals("text/html;charset=utf-8", ContentTypeUtil.getContentType("folder/"));
    }

    public void test_getContentType_unknownExtension() {
        assertNull(ContentTypeUtil.getContentType("file.unknown"));
        assertNull(ContentTypeUtil.getContentType("test.xyz"));
    }

    public void test_getContentType_noExtension() {
        assertNull(ContentTypeUtil.getContentType("README"));
        assertNull(ContentTypeUtil.getContentType("LICENSE"));
        assertNull(ContentTypeUtil.getContentType("/path/to/file"));
    }

    public void test_getContentType_nullOrEmpty() {
        assertNull(ContentTypeUtil.getContentType(null));
        assertNull(ContentTypeUtil.getContentType(""));
        assertNull(ContentTypeUtil.getContentType("   "));
    }

    public void test_getContentType_withDefaultValue() {
        assertEquals("application/octet-stream",
            ContentTypeUtil.getContentType("file.unknown", "application/octet-stream"));
        assertEquals("text/javascript",
            ContentTypeUtil.getContentType("script.js", "application/octet-stream"));
        assertEquals("default/type",
            ContentTypeUtil.getContentType(null, "default/type"));
    }

    public void test_hasContentType() {
        assertTrue(ContentTypeUtil.hasContentType("test.txt"));
        assertTrue(ContentTypeUtil.hasContentType("style.css"));
        assertTrue(ContentTypeUtil.hasContentType("image.png"));

        assertFalse(ContentTypeUtil.hasContentType("file.unknown"));
        assertFalse(ContentTypeUtil.hasContentType("README"));
        assertFalse(ContentTypeUtil.hasContentType(null));
    }

    public void test_registerContentType() {
        // Test custom content type registration
        final String customExtension = ".custom";
        final String customContentType = "application/x-custom";

        // Before registration
        assertNull(ContentTypeUtil.getContentType("file.custom"));

        // Register custom type
        ContentTypeUtil.registerContentType(customExtension, customContentType);

        // After registration
        assertEquals(customContentType, ContentTypeUtil.getContentType("file.custom"));
        assertEquals(customContentType, ContentTypeUtil.getContentType("FILE.CUSTOM"));
    }

    public void test_registerContentType_caseInsensitive() {
        final String customExtension = ".MyExt";
        final String customContentType = "application/x-myext";

        ContentTypeUtil.registerContentType(customExtension, customContentType);

        // Should work with any case
        assertEquals(customContentType, ContentTypeUtil.getContentType("file.myext"));
        assertEquals(customContentType, ContentTypeUtil.getContentType("file.MYEXT"));
        assertEquals(customContentType, ContentTypeUtil.getContentType("file.MyExt"));
    }

    public void test_registerContentType_nullOrEmpty() {
        // Should not throw exceptions, just ignore
        ContentTypeUtil.registerContentType(null, "application/test");
        ContentTypeUtil.registerContentType("", "application/test");
        ContentTypeUtil.registerContentType("   ", "application/test");
        ContentTypeUtil.registerContentType(".ext", null);
        ContentTypeUtil.registerContentType(".ext", "");
        ContentTypeUtil.registerContentType(".ext", "   ");
    }

    public void test_threadSafety() throws Exception {
        // Test concurrent access to ContentTypeUtil
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final boolean[] results = new boolean[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    // Register custom type
                    ContentTypeUtil.registerContentType(".thread" + index, "application/thread" + index);

                    // Get content type
                    String result = ContentTypeUtil.getContentType("test.thread" + index);
                    results[index] = ("application/thread" + index).equals(result);
                } catch (Exception e) {
                    results[index] = false;
                }
            });
            threads[i].start();
        }

        // Wait for all threads
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify all threads succeeded
        for (boolean result : results) {
            assertTrue(result);
        }
    }
}
