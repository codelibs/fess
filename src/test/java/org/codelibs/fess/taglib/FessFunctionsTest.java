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
}
