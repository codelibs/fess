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

import org.apache.tika.config.TikaConfig;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.extractor.impl.TikaExtractor;
import org.codelibs.fess.unit.UnitFessTestCase;
import org.codelibs.fess.util.ComponentUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class FessPdfExtractorTest extends UnitFessTestCase {

    @Override
    protected void tearDown(TestInfo testInfo) throws Exception {
        ComponentUtil.setFessConfig(null);
        super.tearDown(testInfo);
    }

    private static StandardCrawlerContainer createContainer() {
        return new StandardCrawlerContainer().singleton("tikaConfig", TikaConfig.class)
                .singleton("tesseractOCRConfig", TesseractOCRConfig.class)
                .singleton("tikaExtractor", TikaExtractor.class);
    }

    private static class TestPdfExtractor extends FessPdfExtractor {
        private final boolean ocrAvailable;

        TestPdfExtractor(final StandardCrawlerContainer container, final boolean ocrAvailable) {
            crawlerContainer = container;
            this.ocrAvailable = ocrAvailable;
        }

        @Override
        protected boolean isOcrAvailable(final TikaConfig tikaConfig, final TesseractOCRConfig config) {
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
        ComponentUtil.setFessConfig(FessTesseractOCRConfigTest.createFessConfig(false, "eng", "120"));
        final TestPdfExtractor extractor = new TestPdfExtractor(new StandardCrawlerContainer(), true);
        extractor.init();
        assertNull(extractor.getFallbackExtractor());
    }

    @Test
    public void test_isOcrAvailable_skipOcr() throws Exception {
        final TesseractOCRConfig config = new TesseractOCRConfig();
        config.setSkipOcr(true);
        assertFalse(new FessPdfExtractor().isOcrAvailable(TikaConfig.getDefaultConfig(), config));
    }
}
