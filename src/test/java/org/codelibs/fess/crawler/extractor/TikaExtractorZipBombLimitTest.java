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
package org.codelibs.fess.crawler.extractor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.impl.TikaExtractor;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Checks the zip bomb limits shipped in {@code crawler/extractor+tikaExtractor.xml}: an ordinary archive of text
 * is extracted even when it expands to more than {@code maxUncompressionSize}, and a real zip bomb is still rejected.
 */
public class TikaExtractorZipBombLimitTest extends UnitFessTestCase {

    private static final String CONFIG_PATH = "crawler/extractor+tikaExtractor.xml";

    private static final String MARKER = "zipmarkerword";

    private long maxCompressionRatio;

    private long maxUncompressionSize;

    private TikaExtractor createExtractor() throws Exception {
        readShippedLimits();
        final StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("mimeTypeHelper", MimeTypeHelperImpl.class).singleton("tikaExtractor", TikaExtractor.class);
        final TikaExtractor extractor = container.getComponent("tikaExtractor");
        extractor.setMaxCompressionRatio(maxCompressionRatio);
        extractor.setMaxUncompressionSize(maxUncompressionSize);
        return extractor;
    }

    private void readShippedLimits() throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(CONFIG_PATH)) {
            assertNotNull(in);
            final NodeList properties = factory.newDocumentBuilder().parse(in).getElementsByTagName("property");
            for (int i = 0; i < properties.getLength(); i++) {
                final Element property = (Element) properties.item(i);
                final String value = property.getTextContent().trim();
                switch (property.getAttribute("name")) {
                case "maxCompressionRatio" -> maxCompressionRatio = Long.parseLong(value);
                case "maxUncompressionSize" -> maxUncompressionSize = Long.parseLong(value);
                default -> {
                }
                }
            }
        }
        assertTrue(maxCompressionRatio > 0);
        assertTrue(maxUncompressionSize > 0);
    }

    /** Random words from a vocabulary of the given size: the smaller it is, the better the text compresses. */
    private static byte[] createText(final int vocabularySize, final int length) {
        final Random random = new Random(42);
        final String[] words = new String[vocabularySize];
        for (int i = 0; i < words.length; i++) {
            final StringBuilder word = new StringBuilder();
            for (int j = random.nextInt(6) + 3; j > 0; j--) {
                word.append((char) ('a' + random.nextInt(26)));
            }
            words[i] = word.toString();
        }
        final StringBuilder text = new StringBuilder(length + 64).append(MARKER).append(' ');
        while (text.length() < length) {
            text.append(words[random.nextInt(words.length)]).append(random.nextInt(5) == 0 ? ".\n" : " ");
        }
        return text.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private static byte[] zip(final byte[] content) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream out = new ZipOutputStream(bytes)) {
            out.putNextEntry(new ZipEntry("data.txt"));
            out.write(content);
            out.closeEntry();
        }
        return bytes.toByteArray();
    }

    private void assertTextZipIsExtracted(final int vocabularySize, final double minRatio, final double maxRatio) throws Exception {
        final TikaExtractor extractor = createExtractor();
        final byte[] text = createText(vocabularySize, (int) maxUncompressionSize + 2_000_000);
        final byte[] zip = zip(text);
        final double ratio = (double) text.length / zip.length;
        assertTrue(text.length > maxUncompressionSize);
        assertTrue(ratio >= minRatio && ratio <= maxRatio, "unexpected ratio of the test data: " + ratio);
        assertTrue(ratio > 2, "the test data has to be above the old limit of 2: " + ratio);

        final ExtractData data = extractor.getText(new ByteArrayInputStream(zip), null);
        assertTrue(data.getContent().contains(MARKER));
    }

    @Test
    public void test_shippedLimits_allowOrdinaryTextRatio() throws Exception {
        readShippedLimits();
        assertTrue(maxCompressionRatio >= 20, "ordinary text archives compress by tens: " + maxCompressionRatio);
    }

    @Test
    public void test_textZip_ratioAboutTwo_isExtracted() throws Exception {
        assertTextZipIsExtracted(4_000, 2.0, 3.5);
    }

    @Test
    public void test_textZip_ratioAboutTen_isExtracted() throws Exception {
        assertTextZipIsExtracted(20, 6.0, 15.0);
    }

    @Test
    public void test_zipBomb_isRejected() throws Exception {
        final TikaExtractor extractor = createExtractor();
        final byte[] zip = zip(new byte[(int) (maxUncompressionSize * 5)]);
        assertTrue((double) (maxUncompressionSize * 5) / zip.length > maxCompressionRatio * 2);
        try {
            extractor.getText(new ByteArrayInputStream(zip), null);
            fail();
        } catch (final ExtractException e) {
            assertTrue(e.getMessage().contains("reason=zipBombDetected"), e.getMessage());
        }
    }

    @Test
    public void test_zipBomb_withTextEntryAboveLimitIsRejected() throws Exception {
        final TikaExtractor extractor = createExtractor();
        // a highly repetitive text expands far beyond any ordinary ratio
        final StringBuilder text = new StringBuilder();
        while (text.length() < maxUncompressionSize * 5) {
            text.append("all work and no play makes jack a dull boy\n");
        }
        final byte[] zip = zip(text.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        assertTrue((double) text.length() / zip.length > maxCompressionRatio * 2);
        try {
            extractor.getText(new ByteArrayInputStream(zip), null);
            fail();
        } catch (final ExtractException e) {
            assertTrue(e.getMessage().contains("reason=zipBombDetected"), e.getMessage());
        }
    }
}
