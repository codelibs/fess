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
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * FessTesseractOCRConfig is the default Tesseract OCR config of every TikaExtractor,
 * registered as the tesseractOCRConfig component. It applies the crawler.document.ocr.*
 * settings in FessConfig, and skips OCR unless crawler.document.ocr.enabled is true.
 * The tika.tesseract.config parameter of a crawl config takes precedence over it.
 */
public class FessTesseractOCRConfig extends TesseractOCRConfig {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(FessTesseractOCRConfig.class);

    /**
     * Default constructor for FessTesseractOCRConfig.
     */
    public FessTesseractOCRConfig() {
        // Default constructor
    }

    /**
     * Applies the OCR settings in FessConfig.
     */
    @PostConstruct
    public void init() {
        apply(ComponentUtil.getFessConfig());
    }

    /**
     * Applies the OCR settings in the given FessConfig.
     *
     * @param fessConfig the Fess configuration
     */
    protected void apply(final FessConfig fessConfig) {
        if (!fessConfig.isCrawlerDocumentOcrEnabled()) {
            setSkipOcr(true);
            return;
        }
        // Without this, Tesseract puts a space between CJK characters, e.g. "請 求 書".
        setPreserveInterwordSpacing(true);
        try {
            setLanguage(fessConfig.getCrawlerDocumentOcrLanguage());
            final Integer timeout = fessConfig.getCrawlerDocumentOcrTimeoutAsInteger();
            if (timeout != null) {
                setTimeoutSeconds(timeout);
            }
        } catch (final IllegalArgumentException e) {
            logger.warn("Invalid OCR settings, so OCR is disabled: language={}, timeout={}", fessConfig.getCrawlerDocumentOcrLanguage(),
                    fessConfig.getCrawlerDocumentOcrTimeout(), e);
            setSkipOcr(true);
        }
    }
}
