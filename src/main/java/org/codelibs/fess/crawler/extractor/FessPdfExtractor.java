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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.codelibs.fess.crawler.extractor.impl.PdfExtractor;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * FessPdfExtractor extends PdfExtractor to OCR scanned PDFs.
 * When crawler.document.ocr.enabled is true and Tesseract OCR is available,
 * a PDF from which PDFBox extracts no text is extracted again by tikaExtractor,
 * which runs Tesseract OCR on the rendered pages.
 */
public class FessPdfExtractor extends PdfExtractor {

    private static final Logger logger = LogManager.getLogger(FessPdfExtractor.class);

    /**
     * Default constructor for FessPdfExtractor.
     */
    public FessPdfExtractor() {
        // Default constructor
    }

    /**
     * Sets tikaExtractor as the fallback extractor if OCR is enabled and available.
     */
    @PostConstruct
    public void init() {
        if (!ComponentUtil.getFessConfig().isCrawlerDocumentOcrEnabled()) {
            return;
        }
        final TesseractOCRConfig config = crawlerContainer.getComponent("tesseractOCRConfig");
        if (isOcrAvailable(crawlerContainer.getComponent("tikaConfig"), config)) {
            setFallbackExtractor(crawlerContainer.getComponent("tikaExtractor"));
            logger.info("OCR is enabled: language={}, timeout={}s", config.getLanguage(), config.getTimeoutSeconds());
        } else {
            logger.warn("OCR is enabled, but Tesseract OCR is not available. "
                    + "Check that the tesseract command is installed, that the OCR settings are valid, "
                    + "and that tika.xml does not exclude TesseractOCRParser.");
        }
    }

    /**
     * Checks if Tika runs Tesseract OCR with the given config.
     *
     * @param tikaConfig the Tika config
     * @param config the Tesseract OCR config
     * @return true if OCR is available
     */
    protected boolean isOcrAvailable(final TikaConfig tikaConfig, final TesseractOCRConfig config) {
        final ParseContext context = new ParseContext();
        context.set(TesseractOCRConfig.class, config);
        return tikaConfig.getParser().getSupportedTypes(context).contains(MediaType.image("ocr-png"));
    }
}
