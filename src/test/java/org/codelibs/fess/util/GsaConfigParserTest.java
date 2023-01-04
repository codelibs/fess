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
package org.codelibs.fess.util;

import java.io.IOException;
import java.io.InputStream;

import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.es.config.exentity.LabelType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.xml.sax.InputSource;

public class GsaConfigParserTest extends UnitFessTestCase {

    public void test_parse() throws IOException {
        GsaConfigParser parser = new GsaConfigParser();
        try (InputStream is = ResourceUtil.getResourceAsStream("data/gsaconfig.xml")) {
            parser.parse(new InputSource(is));
        }
        parser.getWebConfig().ifPresent(c -> {
            System.out.println(c.toString());
        }).orElse(() -> fail());
        parser.getFileConfig().ifPresent(c -> {
            System.out.println(c.toString());
        }).orElse(() -> fail());
        LabelType[] labelTypes = parser.getLabelTypes();
        assertEquals(3, labelTypes.length);
    }

    public void test_escape() {
        // https://www.google.com/support/enterprise/static/gsa/docs/admin/70/gsa_doc_set/admin_crawl/url_patterns.html#1076127
        assertEscapePattern("", "# Test");
        assertEscapePattern(".*\\Q!/\\E.*", "!/");
        assertEscapePattern("\\Qindex.html\\E", "index.html");
        assertEscapePattern("^\\Qhttp://\\E.*", "^http://");
        assertEscapePattern(".*\\Qindex.html\\E$", "index.html$");
        assertEscapePattern("^\\Qhttp://www.codelibs.org/page.html\\E$", "^http://www.codelibs.org/page.html$");
        assertEscapePattern("\\Qhttp://www.codelibs.org/\\E.*", "http://www.codelibs.org/");
        assertEscapePattern("\\Qsmb://server/test/\\E.*", "smb://server/test/");
        assertEscapePattern(".*\\Q?\\E.*", "contains:?");
        assertEscapePattern(".*\\Q\001\\E.*", "contains:\001");
        assertEscapePattern("(?i).*\\.exe$", "regexpIgnoreCase:\\.exe$");
        assertEscapePattern("(?i)index.html", "regexpIgnoreCase:index.html");
        assertEscapePattern(".*\\.exe$", "regexp:\\.exe$");
        assertEscapePattern("index.html", "regexp:index.html");
    }

    private void assertEscapePattern(String expect, String value) {
        GsaConfigParser parser = new GsaConfigParser();
        assertEquals(expect, parser.getFilterPath(value));
    }

}
