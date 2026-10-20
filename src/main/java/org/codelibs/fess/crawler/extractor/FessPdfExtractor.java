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
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.enricher.ContentEnrichers;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.extractor.impl.PdfExtractor;
import org.codelibs.fess.crawler.extractor.impl.TikaExtractor;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * FessPdfExtractor extends PdfExtractor to OCR scanned PDFs.
 * When Tesseract OCR is available, a PDF from which PDFBox extracts no text is extracted again by tikaExtractor,
 * which runs Tesseract OCR on the rendered pages. The retry runs when crawler.document.ocr.enabled is true,
 * or when the crawl config gives tika.tesseract.config, which also turns OCR on for images.
 */
public class FessPdfExtractor extends PdfExtractor {

    private static final Logger logger = LogManager.getLogger(FessPdfExtractor.class);

    /** The default OCR config, which crawler.document.ocr.* settings are applied to. */
    protected TesseractOCRConfig tesseractOCRConfig;

    /**
     * Default constructor for FessPdfExtractor.
     */
    public FessPdfExtractor() {
        // Default constructor
    }

    /**
     * Sets tikaExtractor as the fallback extractor if Tesseract OCR is available.
     */
    @PostConstruct
    public void init() {
        final boolean enabled = ComponentUtil.getFessConfig().isCrawlerDocumentOcrEnabled();
        tesseractOCRConfig = crawlerContainer.getComponent("tesseractOCRConfig");
        final TikaExtractor tikaExtractor = crawlerContainer.getComponent("tikaExtractor");
        // crawler.document.ocr.enabled may be off while a crawl config turns OCR on, so the fallback is set
        // whenever Tesseract can run, and extractTextByFallback decides for each PDF.
        if (isOcrAvailable(tikaExtractor.getParser(), new TesseractOCRConfig())) {
            setFallbackExtractor(tikaExtractor);
        }
        if (enabled) {
            if (isOcrAvailable(tikaExtractor.getParser(), tesseractOCRConfig)) {
                logger.info("OCR is enabled: language={}, timeout={}s", tesseractOCRConfig.getLanguage(),
                        tesseractOCRConfig.getTimeoutMillis() / 1000);
            } else {
                logger.warn("OCR is enabled, but Tesseract OCR is not available. "
                        + "Check that the tesseract command is installed and that the OCR settings are valid.");
            }
        }
    }

    @Override
    protected String extractTextByFallback(final File file, final Map<String, String> params, final String content) {
        if (!isOcrRequested(params)) {
            if (logger.isDebugEnabled()) {
                logger.debug("OCR is not enabled for this crawl, so the PDF has no text: url={}",
                        params != null ? params.get(ExtractData.URL) : null);
            }
            return content;
        }
        return super.extractTextByFallback(file, params, content);
    }

    /**
     * Checks if OCR is turned on for a PDF. It is on when the crawl config gives tika.tesseract.config,
     * as the images of the crawl config are OCR'd then, and otherwise when the default OCR config does not skip OCR.
     *
     * @param params the extraction parameters
     * @return true if OCR is on
     */
    protected boolean isOcrRequested(final Map<String, String> params) {
        if (params != null && StringUtil.isNotBlank(params.get(TikaExtractor.TIKA_TESSERACT_CONFIG))) {
            return true;
        }
        return tesseractOCRConfig != null && !tesseractOCRConfig.isSkipOcr();
    }

    /**
     * Checks if the Tika parser runs Tesseract OCR on the images of a PDF with the given config.
     * Tika 4 no longer lists the OCR in the supported types of the parser, so this asks whether
     * a text recognizer in the parser tree takes the image type.
     *
     * @param parser the Tika parser of tikaExtractor, which holds the OCR parser; OCR is not available if null
     * @param config the Tesseract OCR config
     * @return true if OCR is available
     */
    protected boolean isOcrAvailable(final Parser parser, final TesseractOCRConfig config) {
        if (parser == null) {
            return false;
        }
        final ParseContext context = new ParseContext();
        context.set(Parser.class, parser);
        context.set(TesseractOCRConfig.class, config);
        return ContentEnrichers.hasTextRecognizer(null, MediaType.image("png"), new Metadata(), context);
    }
}
