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
package org.codelibs.fess.crawler.transformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * File transformer implementation for the Fess search engine.
 * This transformer handles file-based document transformation and content extraction
 * using the Fess file transformation process with support for various file types.
 *
 * <p>It extends AbstractFessFileTransformer to provide specialized file processing
 * capabilities using the appropriate extractor for each file type.</p>
 */
public class FessFileTransformer extends AbstractFessFileTransformer {
    /**
     * Default constructor.
     */
    public FessFileTransformer() {
        super();
    }

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(FessFileTransformer.class);

    /**
     * Initializes the transformer after dependency injection.
     * Sets up the Fess configuration and data serializer components.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        fessConfig = ComponentUtil.getFessConfig();
        dataSerializer = ComponentUtil.getComponent("dataSerializer");
    }

    /**
     * Gets the Fess configuration instance.
     *
     * @return the Fess configuration
     */
    @Override
    public FessConfig getFessConfig() {
        return fessConfig;
    }

    /**
     * Gets the logger instance for this transformer.
     *
     * @return the logger instance
     */
    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * Gets the appropriate extractor for the given response data.
     * Selects an extractor based on the MIME type of the file.
     *
     * @param responseData the response data containing the file to extract
     * @return the extractor instance for processing the file
     * @throws FessSystemException if no suitable extractor factory can be found
     */
    @Override
    protected Extractor getExtractor(final ResponseData responseData) {
        final ExtractorFactory extractorFactory = ComponentUtil.getExtractorFactory();
        if (extractorFactory == null) {
            throw new FessSystemException("Could not find extractorFactory.");
        }
        final Extractor extractor = extractorFactory.getExtractor(responseData.getMimeType());
        if (logger.isDebugEnabled()) {
            logger.debug("url={}, extractor={}", responseData.getUrl(), extractor);
        }
        return extractor;
    }
}
