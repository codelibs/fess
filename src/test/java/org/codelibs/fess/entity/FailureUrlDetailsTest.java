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
package org.codelibs.fess.entity;

import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class FailureUrlDetailsTest extends UnitFessTestCase {

    @Test
    public void test_normalizeMimeType_keepsOnlyTheLowerCaseMediaType() {
        assertEquals("application/pdf", FailureUrlDetails.normalizeMimeType("Application/PDF"));
        assertEquals("text/html", FailureUrlDetails.normalizeMimeType(" text/html ; charset=UTF-8"));
        assertEquals("image/svg+xml", FailureUrlDetails.normalizeMimeType("image/svg+xml"));
        assertEquals("application/vnd.ms-excel", FailureUrlDetails.normalizeMimeType("application/vnd.ms-excel"));
    }

    @Test
    public void test_normalizeMimeType_dropsWhatIsNotAMediaType() {
        assertNull(FailureUrlDetails.normalizeMimeType(null));
        assertNull(FailureUrlDetails.normalizeMimeType(""));
        assertNull(FailureUrlDetails.normalizeMimeType("  ;charset=UTF-8"));
        assertNull(FailureUrlDetails.normalizeMimeType("pdf"));
        // a spreadsheet would read these as a formula, and a browser as markup
        assertNull(FailureUrlDetails.normalizeMimeType("=cmd/x"));
        assertNull(FailureUrlDetails.normalizeMimeType("+a/b"));
        assertNull(FailureUrlDetails.normalizeMimeType("@a/b"));
        assertNull(FailureUrlDetails.normalizeMimeType("-a/b"));
        assertNull(FailureUrlDetails.normalizeMimeType("text/<script>"));
        assertNull(FailureUrlDetails.normalizeMimeType("text/html\r\nX-Injected: 1"));
    }

    @Test
    public void test_normalizeMimeType_capsTheLength() {
        final String atLimit = "a/" + "b".repeat(FailureUrlDetails.MAX_MIME_TYPE_LENGTH - 2);
        assertEquals(FailureUrlDetails.MAX_MIME_TYPE_LENGTH, atLimit.length());
        assertEquals(atLimit, FailureUrlDetails.normalizeMimeType(atLimit));
        assertNull(FailureUrlDetails.normalizeMimeType(atLimit + "b"));
        assertNull(FailureUrlDetails.normalizeMimeType("a/" + "b".repeat(100000)));
    }

    @Test
    public void test_constructor_normalizesTheUnknownValues() {
        final FailureUrlDetails unknown = new FailureUrlDetails(-1L, "bad", 0);
        assertNull(unknown.contentLength());
        assertNull(unknown.mimeType());
        assertNull(unknown.httpStatusCode());
        final FailureUrlDetails known = new FailureUrlDetails(0L, "text/plain", 304);
        assertEquals(Long.valueOf(0L), known.contentLength());
        assertEquals(Integer.valueOf(304), known.httpStatusCode());
    }

    @Test
    public void test_of_takesTheResponse() {
        assertNull(FailureUrlDetails.of(null));
        final ResponseData responseData = new ResponseData();
        // nothing is known of a response that has not been filled in
        final FailureUrlDetails empty = FailureUrlDetails.of(responseData);
        assertNull(empty.contentLength());
        assertNull(empty.mimeType());
        assertNull(empty.httpStatusCode());
        responseData.setContentLength(10L);
        responseData.setMimeType("text/csv; charset=Shift_JIS");
        responseData.setHttpStatusCode(200);
        responseData.setUrl("https:docs.example.com-a.csv");
        final FailureUrlDetails details = FailureUrlDetails.of(responseData);
        assertEquals(Long.valueOf(10L), details.contentLength());
        assertEquals("text/csv", details.mimeType());
        assertEquals(Integer.valueOf(200), details.httpStatusCode());
    }

    @Test
    public void test_of_keepsTheStatusOnlyOfAnHttpResponse() {
        final ResponseData responseData = new ResponseData();
        responseData.setHttpStatusCode(200);
        responseData.setContentLength(10L);
        // the file, SMB, FTP and storage clients set 200 for every document they can read
        for (final String url : new String[] { "file:srv-a.pdf", "smb:host-a.pdf", "ftp:host-a.pdf", "s3:bucket-a.pdf", "gcs:bucket-a.pdf",
                null }) {
            responseData.setUrl(url);
            final FailureUrlDetails details = FailureUrlDetails.of(responseData);
            assertNull(details.httpStatusCode());
            assertEquals(Long.valueOf(10L), details.contentLength());
        }
        responseData.setUrl("HTTP:docs.example.com-a.html");
        assertEquals(Integer.valueOf(200), FailureUrlDetails.of(responseData).httpStatusCode());
        responseData.setUrl("https:docs.example.com-a.html");
        assertEquals(Integer.valueOf(200), FailureUrlDetails.of(responseData).httpStatusCode());
    }

    @Test
    public void test_ofMaxLengthMessage() {
        final FailureUrlDetails details =
                FailureUrlDetails.ofMaxLengthMessage("The content length (2048 byte) is over 1024 byte. The url is x");
        assertEquals(Long.valueOf(2048L), details.contentLength());
        assertEquals(Long.valueOf(1L), FailureUrlDetails.ofMaxLengthMessage("Content length (1 bytes) exceeds").contentLength());
        assertNull(FailureUrlDetails.ofMaxLengthMessage(null));
        assertNull(FailureUrlDetails.ofMaxLengthMessage("Connection timed out"));
        assertNull(FailureUrlDetails.ofMaxLengthMessage("The content length (abc byte)"));
    }
}
