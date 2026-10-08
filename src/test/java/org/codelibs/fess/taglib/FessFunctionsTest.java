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
package org.codelibs.fess.taglib;

import java.util.Date;

import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;

public class FessFunctionsTest extends UnitFessTestCase {
    @Test
    public void test_parseDate() {
        Date date;

        date = FessFunctions.parseDate("");
        assertNull(date);

        date = FessFunctions.parseDate("2004-04-01T12:34:56.123Z");
        assertEquals("2004-04-01T12:34:56.123Z", FessFunctions.formatDate(date));

        date = FessFunctions.parseDate("2004-04-01T12:34:56Z");
        assertEquals("2004-04-01T12:34:56.000Z", FessFunctions.formatDate(date));

        date = FessFunctions.parseDate("2004-04-01T12:34Z");
        assertEquals("2004-04-01T12:34:00.000Z", FessFunctions.formatDate(date));

        date = FessFunctions.parseDate("2004-04-01");
        assertEquals("2004-04-01T00:00:00.000Z", FessFunctions.formatDate(date));

        date = FessFunctions.parseDate("2004-04-01T12:34:56.123+09:00");
        assertEquals("2004-04-01T03:34:56.123Z", FessFunctions.formatDate(date));

        date = FessFunctions.parseDate("D:20040401033456-05'00'", "pdf_date");
        assertEquals("2004-04-01T08:34:56.000Z", FessFunctions.formatDate(date));
    }

    @Test
    public void test_escapeJs() {
        assertEquals("", FessFunctions.escapeJs(null));
        assertEquals("", FessFunctions.escapeJs(""));
        assertEquals("plain text", FessFunctions.escapeJs("plain text"));
        assertEquals("It\\'s currently busy.", FessFunctions.escapeJs("It's currently busy."));
        assertEquals("say \\\"hi\\\"", FessFunctions.escapeJs("say \"hi\""));
        assertEquals("a\\\\b", FessFunctions.escapeJs("a\\b"));
        assertEquals("line1\\nline2", FessFunctions.escapeJs("line1\nline2"));
        assertEquals("L\\'authentification a \\u00E9chou\\u00E9.", FessFunctions.escapeJs("L'authentification a échoué."));

        // Forward slash must be escaped so a label can never close the surrounding inline <script> block.
        assertEquals("a\\/b", FessFunctions.escapeJs("a/b"));
        final String breakout = FessFunctions.escapeJs("</script><script>alert(1)</script>");
        assertEquals("<\\/script><script>alert(1)<\\/script>", breakout);
        assertFalse("escapeJs output must not contain a literal </script>", breakout.contains("</script>"));

        // Control characters and NUL get unicode-escaped, never passed through raw.
        assertEquals("\\u0000", FessFunctions.escapeJs("\0"));
        assertEquals("a\\tb\\rc", FessFunctions.escapeJs("a\tb\rc"));

        // Supplementary code points (here a robot emoji, U+1F916) are emitted as escaped surrogate pairs.
        assertEquals("\\uD83E\\uDD16", FessFunctions.escapeJs("🤖"));
    }

    @Test
    public void test_safeHref() {
        // Schemes Fess crawls, and relative URLs, are kept as they are.
        assertEquals("http://example.com/a?b=c#d", FessFunctions.safeHref("http://example.com/a?b=c#d"));
        assertEquals("https://example.com/", FessFunctions.safeHref("https://example.com/"));
        assertEquals("HTTP://example.com/", FessFunctions.safeHref("HTTP://example.com/"));
        assertEquals("ftp://example.com/a.txt", FessFunctions.safeHref("ftp://example.com/a.txt"));
        assertEquals("file:///srv/docs/a.txt", FessFunctions.safeHref("file:///srv/docs/a.txt"));
        assertEquals("smb://host/share/a.txt", FessFunctions.safeHref("smb://host/share/a.txt"));
        assertEquals("smb1://host/share/a.txt", FessFunctions.safeHref("smb1://host/share/a.txt"));
        assertEquals("s3://bucket/key", FessFunctions.safeHref("s3://bucket/key"));
        assertEquals("gcs://bucket/key", FessFunctions.safeHref("gcs://bucket/key"));
        assertEquals("storage://bucket/key", FessFunctions.safeHref("storage://bucket/key"));
        assertEquals("/path/to/doc.html", FessFunctions.safeHref("/path/to/doc.html"));
        assertEquals("doc.html?a=b:c", FessFunctions.safeHref("doc.html?a=b:c"));
        assertEquals("//example.com/doc.html", FessFunctions.safeHref("//example.com/doc.html"));
        assertEquals("#not-found-abc", FessFunctions.safeHref("#not-found-abc"));

        // Any other scheme becomes "#".
        assertEquals("#", FessFunctions.safeHref("javascript:window.x=1"));
        assertEquals("#", FessFunctions.safeHref("JavaScript:window.x=1"));
        assertEquals("#", FessFunctions.safeHref("data:text/html,<p>a</p>"));
        assertEquals("#", FessFunctions.safeHref("vbscript:msgbox(1)"));
        assertEquals("#", FessFunctions.safeHref("blob:http://example.com/1"));
        assertEquals("#", FessFunctions.safeHref("mailto:a@example.com"));

        // The scheme is read the way a browser reads it.
        assertEquals("#", FessFunctions.safeHref("java\tscript:window.x=1"));
        assertEquals("#", FessFunctions.safeHref("java\nscript:window.x=1"));
        assertEquals("#", FessFunctions.safeHref("jav\r\nascript:window.x=1"));
        assertEquals("#", FessFunctions.safeHref(" javascript:window.x=1"));
        assertEquals("#", FessFunctions.safeHref("\u0001javascript:window.x=1"));
        assertEquals("#", FessFunctions.safeHref("\t\n javascript:window.x=1"));
        assertEquals("#", FessFunctions.safeHref("htt\tp-x://example.com/"));

        // A colon that is not the end of a scheme does not make the URL unsafe.
        assertEquals("a b:c", FessFunctions.safeHref("a b:c"));
        assertEquals("1http:x", FessFunctions.safeHref("1http:x"));

        // Nothing to link to.
        assertEquals("#", FessFunctions.safeHref(null));
        assertEquals("#", FessFunctions.safeHref(""));
        assertEquals("#", FessFunctions.safeHref("   "));
    }
}
