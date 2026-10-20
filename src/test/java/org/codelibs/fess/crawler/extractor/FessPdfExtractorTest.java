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

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.ocr.TesseractOCRParser;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.extractor.impl.TikaExtractor;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessPdfExtractorTest extends UnitFessTestCase {

    private final List<Path> tempDirs = new ArrayList<>();

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        tempDirs.forEach(dir -> FileUtils.deleteQuietly(dir.toFile()));
        super.tearDown(testInfo);
    }

    private static StandardCrawlerContainer createContainer() {
        return new StandardCrawlerContainer().singleton("tesseractOCRConfig", TesseractOCRConfig.class)
                .singleton("tikaExtractor", TikaExtractor.class);
    }

    private static class TestPdfExtractor extends FessPdfExtractor {
        private final boolean ocrAvailable;

        TestPdfExtractor(final StandardCrawlerContainer container, final boolean ocrAvailable) {
            crawlerContainer = container;
            this.ocrAvailable = ocrAvailable;
        }

        @Override
        protected boolean isOcrAvailable(final Parser parser, final TesseractOCRConfig config) {
            return ocrAvailable;
        }

        Extractor getFallbackExtractor() {
            return fallbackExtractor;
        }
    }

    @Test
    public void test_init_enabled() {
        ComponentUtil.setFessConfig(FessTesseractOCRConfigTest.createFessConfig(true, "eng", "120"));
        final StandardCrawlerContainer container = createContainer();
        final TestPdfExtractor extractor = new TestPdfExtractor(container, true);
        extractor.init();
        assertSame(container.getComponent("tikaExtractor"), extractor.getFallbackExtractor());
    }

    @Test
    public void test_init_enabled_unavailable() {
        ComponentUtil.setFessConfig(FessTesseractOCRConfigTest.createFessConfig(true, "eng", "120"));
        final TestPdfExtractor extractor = new TestPdfExtractor(createContainer(), false);
        extractor.init();
        assertNull(extractor.getFallbackExtractor());
    }

    @Test
    public void test_init_disabled() {
        // crawler.document.ocr.enabled is off, but a crawl config can turn OCR on, so the fallback is set
        ComponentUtil.setFessConfig(FessTesseractOCRConfigTest.createFessConfig(false, "eng", "120"));
        final StandardCrawlerContainer container = createContainer();
        final TestPdfExtractor extractor = new TestPdfExtractor(container, true);
        extractor.init();
        assertSame(container.getComponent("tikaExtractor"), extractor.getFallbackExtractor());
    }

    @Test
    public void test_init_disabled_unavailable() {
        ComponentUtil.setFessConfig(FessTesseractOCRConfigTest.createFessConfig(false, "eng", "120"));
        final TestPdfExtractor extractor = new TestPdfExtractor(createContainer(), false);
        extractor.init();
        assertNull(extractor.getFallbackExtractor());
    }

    /** A fallback extractor that counts its calls. */
    private static class CountingExtractor implements Extractor {
        int count;

        @Override
        public ExtractData getText(final InputStream in, final Map<String, String> params) {
            count++;
            return new ExtractData("ocr text");
        }
    }

    private static FessPdfExtractor createGateExtractor(final boolean skipOcr, final CountingExtractor fallback) {
        final FessPdfExtractor extractor = new FessPdfExtractor();
        extractor.tesseractOCRConfig = new TesseractOCRConfig();
        extractor.tesseractOCRConfig.setSkipOcr(skipOcr);
        extractor.setFallbackExtractor(fallback);
        return extractor;
    }

    private File createPdfFile() throws Exception {
        final File file = createTempDir("pdf").resolve("scan.pdf").toFile();
        Files.write(file.toPath(), new byte[] { 1 });
        return file;
    }

    @Test
    public void test_isOcrRequested() {
        final Map<String, String> perConfig = Map.of(TikaExtractor.TIKA_TESSERACT_CONFIG, "tesseract.properties");
        final Map<String, String> blank = Map.of(TikaExtractor.TIKA_TESSERACT_CONFIG, " ");

        final FessPdfExtractor skipped = createGateExtractor(true, new CountingExtractor());
        assertFalse(skipped.isOcrRequested(null));
        assertFalse(skipped.isOcrRequested(Map.of()));
        assertFalse(skipped.isOcrRequested(blank));
        assertTrue(skipped.isOcrRequested(perConfig));

        final FessPdfExtractor enabled = createGateExtractor(false, new CountingExtractor());
        assertTrue(enabled.isOcrRequested(null));
        assertTrue(enabled.isOcrRequested(Map.of()));
        assertTrue(enabled.isOcrRequested(perConfig));
    }

    @Test
    public void test_extractTextByFallback_globalOff() throws Exception {
        final CountingExtractor fallback = new CountingExtractor();
        final FessPdfExtractor extractor = createGateExtractor(true, fallback);
        assertEquals("", extractor.extractTextByFallback(createPdfFile(), Map.of(), ""));
        assertEquals(0, fallback.count);
    }

    @Test
    public void test_extractTextByFallback_globalOff_perConfig() throws Exception {
        final CountingExtractor fallback = new CountingExtractor();
        final FessPdfExtractor extractor = createGateExtractor(true, fallback);
        final Map<String, String> params = Map.of(TikaExtractor.TIKA_TESSERACT_CONFIG, "tesseract.properties");
        assertEquals("ocr text", extractor.extractTextByFallback(createPdfFile(), params, ""));
        assertEquals(1, fallback.count);
    }

    @Test
    public void test_extractTextByFallback_globalOn() throws Exception {
        final CountingExtractor fallback = new CountingExtractor();
        final FessPdfExtractor extractor = createGateExtractor(false, fallback);
        assertEquals("ocr text", extractor.extractTextByFallback(createPdfFile(), Map.of(), ""));
        assertEquals(1, fallback.count);
    }

    private Path createTempDir(final String prefix) throws Exception {
        final Path dir = Files.createTempDirectory(prefix);
        tempDirs.add(dir);
        return dir;
    }

    /** Creates a tesseract command that only answers the availability check, and returns its directory. */
    private Path createStubTesseract() throws Exception {
        Assumptions.assumeFalse(System.getProperty("os.name").startsWith("Windows"));
        final Path dir = createTempDir("tesseract");
        final Path command = dir.resolve("tesseract");
        Files.write(command, "#!/bin/sh\necho tesseract 5.3.0\nexit 0\n".getBytes(StandardCharsets.UTF_8));
        assertTrue(command.toFile().setExecutable(true));
        return dir;
    }

    private static TesseractOCRConfig createConfig(final Path tesseractDir, final boolean skipOcr) throws Exception {
        final TesseractOCRConfig config = new TesseractOCRConfig();
        config.setTesseractPath(tesseractDir.toString());
        config.setSkipOcr(skipOcr);
        return config;
    }

    /**
     * A parser tree with a Tesseract OCR parser that looks for the tesseract command in the config's path.
     * The OCR parser of the default parser looks it up in PATH once, so a stub command cannot be put in front of it.
     */
    private static Parser ocrParserTree(final TesseractOCRConfig config) throws Exception {
        return new CompositeParser(MediaTypeRegistry.getDefaultRegistry(), new TesseractOCRParser(config));
    }

    @Test
    public void test_isOcrAvailable_tesseractInstalled() throws Exception {
        final Path dir = createStubTesseract();
        final TesseractOCRConfig config = createConfig(dir, false);
        assertTrue(new FessPdfExtractor().isOcrAvailable(ocrParserTree(config), config));
    }

    @Test
    public void test_isOcrAvailable_skipOcr() throws Exception {
        final Path dir = createStubTesseract();
        final TesseractOCRConfig config = createConfig(dir, true);
        assertFalse(new FessPdfExtractor().isOcrAvailable(ocrParserTree(config), config));
    }

    @Test
    public void test_isOcrAvailable_tesseractNotInstalled() throws Exception {
        final Path dir = createTempDir("notesseract");
        final TesseractOCRConfig config = createConfig(dir, false);
        assertFalse(new FessPdfExtractor().isOcrAvailable(ocrParserTree(config), config));
    }

    @Test
    public void test_isOcrAvailable_tikaExtractorParser_skipOcr() throws Exception {
        final TikaExtractor tikaExtractor = new TikaExtractor();
        tikaExtractor.init();
        final TesseractOCRConfig config = createConfig(createStubTesseract(), true);
        assertFalse(new FessPdfExtractor().isOcrAvailable(tikaExtractor.getParser(), config));
    }

    @Test
    public void test_isOcrAvailable_noParser() {
        assertFalse(new FessPdfExtractor().isOcrAvailable(null, new TesseractOCRConfig()));
    }
}
