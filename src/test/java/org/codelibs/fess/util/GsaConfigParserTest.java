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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.exception.GsaConfigException;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.opensearch.config.exentity.LabelType;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class GsaConfigParserTest extends UnitFessTestCase {

    private static final Logger logger = LogManager.getLogger(GsaConfigParserTest.class);

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ComponentUtil.register(new SystemHelper(), "systemHelper");
    }

    public void test_parse() throws IOException {
        GsaConfigParser parser = new GsaConfigParser();
        try (InputStream is = ResourceUtil.getResourceAsStream("data/gsaconfig.xml")) {
            parser.parse(new InputSource(is));
        }
        parser.getWebConfig().ifPresent(c -> {
            logger.debug(c.toString());
        }).orElse(() -> fail());
        parser.getFileConfig().ifPresent(c -> {
            logger.debug(c.toString());
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

    public void test_parseFilterPaths_web() {
        GsaConfigParser parser = new GsaConfigParser();

        assertEquals("\\Qhttps://example.com\\E.*", parser.parseFilterPaths("https://example.com", true, false));
        assertEquals("\\Qfile://test\\E.*", parser.parseFilterPaths("file://test", true, false));
        assertEquals("\\Qtest\\E", parser.parseFilterPaths("test", true, false));
        assertEquals("", parser.parseFilterPaths("# comment\n\n  ", true, false));
    }

    public void test_parseFilterPaths_file() {
        GsaConfigParser parser = new GsaConfigParser();

        assertEquals("\\Qfile://test\\E.*", parser.parseFilterPaths("file://test", false, true));
        assertEquals("\\Qhttps://example.com\\E.*", parser.parseFilterPaths("https://example.com", false, true));
        assertEquals("\\Qtest\\E", parser.parseFilterPaths("test", false, true));
    }

    public void test_escape_advanced() {
        GsaConfigParser parser = new GsaConfigParser();

        assertEquals("", parser.escape("# comment"));
        assertEquals("^\\Qtest\\E$", parser.escape("^test$"));
        assertEquals("^\\Qtest\\E", parser.escape("^test"));
        assertEquals("\\Qtest\\E$", parser.escape("test$"));
        assertEquals("\\Qtest\\E", parser.escape("test"));
    }

    public void test_unescape() {
        GsaConfigParser parser = new GsaConfigParser();

        assertEquals("test\\", parser.unescape("test\\\\"));
        assertEquals("test", parser.unescape("test"));
        assertEquals("\\", parser.unescape("\\\\"));
    }

    public void test_appendFilterPath() {
        GsaConfigParser parser = new GsaConfigParser();
        StringBuilder buf = new StringBuilder();

        assertEquals("", parser.appendFileterPath(buf, ""));
        assertEquals("", parser.appendFileterPath(buf, null));
        assertEquals("", parser.appendFileterPath(buf, "   "));

        assertEquals("^test.*", parser.appendFileterPath(new StringBuilder(), "^test"));
        assertEquals("^test$", parser.appendFileterPath(new StringBuilder(), "^test$"));
        assertEquals(".*test$", parser.appendFileterPath(new StringBuilder(), "test$"));
        assertEquals(".*test/\\E.*", parser.appendFileterPath(new StringBuilder(), "test/\\E"));
        assertEquals("test", parser.appendFileterPath(new StringBuilder(), "test"));
    }

    public void test_getFilterPath_regexpCase() {
        GsaConfigParser parser = new GsaConfigParser();

        assertEquals(".*\\.exe$", parser.getFilterPath("regexpCase:\\.exe$"));
        assertEquals("test", parser.getFilterPath("regexpCase:test"));
    }

    public void test_setProtocols() {
        GsaConfigParser parser = new GsaConfigParser();

        String[] customWebProtocols = { "http:", "https:", "ftp:" };
        String[] customFileProtocols = { "file:", "smb:" };

        parser.setWebProtocols(customWebProtocols);
        parser.setFileProtocols(customFileProtocols);

        assertEquals("\\Qftp://test\\E.*", parser.parseFilterPaths("ftp://test", true, false));
        assertEquals("\\Qftp://test\\E.*", parser.parseFilterPaths("ftp://test", false, true));
    }

    public void test_parseWithInvalidFormat() {
        GsaConfigParser parser = new GsaConfigParser();
        String invalidXml = "<?xml version=\"1.0\"?><invalid><test/></invalid>";

        try {
            parser.parse(new InputSource(new StringReader(invalidXml)));
            fail("Should throw GsaConfigException");
        } catch (GsaConfigException e) {
            assertEquals("Failed to parse XML file.", e.getMessage());
        }
    }

    public void test_parseWithMalformedXml() {
        GsaConfigParser parser = new GsaConfigParser();
        String malformedXml = "<?xml version=\"1.0\"?><eef><unclosed>";

        try {
            parser.parse(new InputSource(new StringReader(malformedXml)));
            fail("Should throw GsaConfigException");
        } catch (GsaConfigException e) {
            assertEquals("Failed to parse XML file.", e.getMessage());
        }
    }

    public void test_parseWithUserAgent() {
        GsaConfigParser parser = new GsaConfigParser();
        String xmlWithUserAgent = "<?xml version=\"1.0\"?>" + "<eef><config><globalparams>" + "<user_agent>custom-agent</user_agent>"
                + "<start_urls>https://example.com</start_urls>" + "</globalparams></config></eef>";

        parser.parse(new InputSource(new StringReader(xmlWithUserAgent)));

        parser.getWebConfig().ifPresent(config -> {
            assertEquals("custom-agent", config.getUserAgent());
        }).orElse(() -> fail("WebConfig should be present"));
    }

    public void test_parseMinimalValid() {
        GsaConfigParser parser = new GsaConfigParser();
        String minimalXml = "<?xml version=\"1.0\"?>" + "<eef><config><globalparams>" + "<start_urls>https://example.com</start_urls>"
                + "</globalparams></config></eef>";

        parser.parse(new InputSource(new StringReader(minimalXml)));

        assertTrue(parser.getWebConfig().isPresent());
        assertFalse(parser.getFileConfig().isPresent());
        assertEquals(0, parser.getLabelTypes().length);
    }

    public void test_parseWithFileUrls() {
        GsaConfigParser parser = new GsaConfigParser();
        String xmlWithFileUrls = "<?xml version=\"1.0\"?>" + "<eef><config><globalparams>"
                + "<start_urls>file:///test\nsmb://server/share</start_urls>" + "</globalparams></config></eef>";

        parser.parse(new InputSource(new StringReader(xmlWithFileUrls)));

        assertFalse(parser.getWebConfig().isPresent());
        assertTrue(parser.getFileConfig().isPresent());

        parser.getFileConfig().ifPresent(config -> {
            assertTrue(config.getPaths().contains("file:///test"));
            assertTrue(config.getPaths().contains("smb://server/share"));
        });
    }

    public void test_parseWithMixedUrls() {
        GsaConfigParser parser = new GsaConfigParser();
        String xmlWithMixedUrls = "<?xml version=\"1.0\"?>" + "<eef><config><globalparams>"
                + "<start_urls>https://example.com\nfile:///test</start_urls>" + "</globalparams></config></eef>";

        parser.parse(new InputSource(new StringReader(xmlWithMixedUrls)));

        assertTrue(parser.getWebConfig().isPresent());
        assertTrue(parser.getFileConfig().isPresent());

        parser.getWebConfig().ifPresent(config -> {
            assertTrue(config.getUrls().contains("https://example.com"));
            assertFalse(config.getUrls().contains("file:///test"));
        });

        parser.getFileConfig().ifPresent(config -> {
            assertTrue(config.getPaths().contains("file:///test"));
            assertFalse(config.getPaths().contains("https://example.com"));
        });
    }

    public void test_parseWithCollectionFilters() {
        GsaConfigParser parser = new GsaConfigParser();
        String xmlWithCollection = "<?xml version=\"1.0\"?>" + "<eef><config>" + "<collections><collection Name=\"test\">"
                + "<good_urls>https://example.com</good_urls>" + "<bad_urls>https://example.com/admin</bad_urls>"
                + "</collection></collections>" + "<globalparams><start_urls>https://example.com</start_urls></globalparams>"
                + "</config></eef>";

        parser.parse(new InputSource(new StringReader(xmlWithCollection)));

        LabelType[] labels = parser.getLabelTypes();
        assertEquals(1, labels.length);
        assertEquals("test", labels[0].getName());
        assertEquals("test", labels[0].getValue());
        assertNotNull(labels[0].getIncludedPaths());
        assertNotNull(labels[0].getExcludedPaths());
    }

    public void test_toString() {
        GsaConfigParser parser = new GsaConfigParser();
        String result = parser.toString();
        assertTrue(result.contains("GsaConfigParser"));
        assertTrue(result.contains("labelList"));
        assertTrue(result.contains("webConfig"));
        assertTrue(result.contains("fileConfig"));
    }

    public void test_getSAXHandlerMethods() throws SAXException {
        GsaConfigParser parser = new GsaConfigParser();

        parser.startDocument();

        char[] chars = "test".toCharArray();
        parser.characters(chars, 0, chars.length);

        parser.endDocument();
    }

}
