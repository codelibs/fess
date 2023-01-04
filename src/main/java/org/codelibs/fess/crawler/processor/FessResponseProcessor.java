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
package org.codelibs.fess.crawler.processor;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.processor.impl.DefaultResponseProcessor;
import org.codelibs.fess.ingest.IngestFactory;
import org.codelibs.fess.ingest.Ingester;
import org.codelibs.fess.util.ComponentUtil;

public class FessResponseProcessor extends DefaultResponseProcessor {
    private static final Logger logger = LogManager.getLogger(FessResponseProcessor.class);

    private IngestFactory ingestFactory = null;

    @PostConstruct
    public void init() {
        if (ComponentUtil.hasIngestFactory()) {
            ingestFactory = ComponentUtil.getIngestFactory();
        }
    }

    @Override
    protected AccessResult<?> createAccessResult(final ResponseData responseData, final ResultData resultData) {
        return super.createAccessResult(responseData, ingest(responseData, resultData));
    }

    private ResultData ingest(final ResponseData responseData, final ResultData resultData) {
        if (ingestFactory == null) {
            return resultData;
        }
        ResultData target = resultData;
        for (final Ingester ingester : ingestFactory.getIngesters()) {
            try {
                target = ingester.process(target, responseData);
            } catch (final Exception e) {
                logger.warn("Failed to process Ingest[{}]", ingester.getClass().getSimpleName(), e);
            }
        }
        return target;
    }
}
