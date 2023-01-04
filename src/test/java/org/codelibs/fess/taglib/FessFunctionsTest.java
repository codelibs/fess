/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

public class FessFunctionsTest extends UnitFessTestCase {
    public void test_formatNumber() {
        assertEquals("0", FessFunctions.formatNumber(0, "###,###"));
        assertEquals("1,000", FessFunctions.formatNumber(1000, "###,###"));
        assertEquals("1,000,000", FessFunctions.formatNumber(1000000, "###,###"));
    }

    public void test_formatFileSize() {
        assertEquals("0", FessFunctions.formatFileSize(0));
        assertEquals("1000", FessFunctions.formatFileSize(1000));
        assertEquals("976.6K", FessFunctions.formatFileSize(1000000));
        assertEquals("953.7M", FessFunctions.formatFileSize(1000000000));
        assertEquals("931.3G", FessFunctions.formatFileSize(1000000000000L));
        assertEquals("909.5T", FessFunctions.formatFileSize(1000000000000000L));
    }

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

    public void test_formatCode() {
        String code;
        String value;

        code = "";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint\"></pre>", value);

        code = "aaa";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint\">aaa</pre>", value);

        code = "aaa\nbbb";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint\">aaa\nbbb</pre>", value);

        code = "aaa\nbbb\nccc";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint\">aaa\nbbb\nccc</pre>", value);

        code = "L10:aaa";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint linenums:10\">aaa</pre>", value);

        code = "L10:aaa\nL11:bbb";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint linenums:10\">aaa\nbbb</pre>", value);

        code = "L10:aaa\nL11:bbb\nL12:ccc";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint linenums:10\">aaa\nbbb\nccc</pre>", value);

        code = "aaa\nL11:bbb\nL12:ccc";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint linenums:11\">bbb\nccc</pre>", value);

        code = "L10:aaa\nL11:bbb\nL12:ccc...";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint linenums:10\">aaa\nbbb</pre>", value);

        code = "aaa\nL11:bbb\nL12:ccc...";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint linenums:11\">bbb</pre>", value);

        code = "aaa\nL10:";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint\">aaa</pre>", value);

        code = "aaa\nL10:\nL11:ccc...";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint\">aaa\n\nccc...</pre>", value);

        code = "aaa\nL10:\nL11:ccc";
        value = FessFunctions.formatCode("L", "prettyprint", "text/plain", code);
        assertEquals("<pre class=\"prettyprint linenums:10\">\nccc</pre>", value);
    }

    public void test_maskEmail() {
        String value;

        value = FessFunctions.maskEmail(null);
        assertEquals("", value);

        value = FessFunctions.maskEmail("");
        assertEquals("", value);

        value = FessFunctions.maskEmail("aaa bbb ccc");
        assertEquals("aaa bbb ccc", value);

        value = FessFunctions.maskEmail("aaa@bbb.ccc");
        assertEquals("******@****.***", value);

        value = FessFunctions.maskEmail("111.aaa@bbb.ccc");
        assertEquals("******@****.***", value);

        value = FessFunctions.maskEmail("111 aaa+@bbb.ccc 222");
        assertEquals("111 ******@****.*** 222", value);

        value = FessFunctions.maskEmail("あaaa@bbb.ccc１");
        assertEquals("あ******@****.***１", value);

        value = FessFunctions.maskEmail("<aaa@bbb.ccc>");
        assertEquals("<******@****.***>", value);
    }
}
