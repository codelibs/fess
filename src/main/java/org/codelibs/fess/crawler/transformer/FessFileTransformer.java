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
package org.codelibs.fess.crawler.transformer;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public class FessFileTransformer extends AbstractFessFileTransformer {
    private static final Logger logger = LogManager.getLogger(FessFileTransformer.class);

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        fessConfig = ComponentUtil.getFessConfig();
    }

    @Override
    public FessConfig getFessConfig() {
        return fessConfig;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

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
