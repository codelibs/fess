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
package org.codelibs.fess.crawler.processor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.processor.impl.DefaultResponseProcessor;
import org.codelibs.fess.ingest.IngestFactory;
import org.codelibs.fess.ingest.Ingester;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;

/**
 * Response processor implementation for the Fess search engine.
 * This processor extends DefaultResponseProcessor to provide additional
 * processing capabilities through the ingest framework, allowing for
 * custom data transformation and enrichment during the crawling process.
 *
 * <p>It supports pluggable ingesters that can modify the result data
 * before it is stored in the search index.</p>
 */
public class FessResponseProcessor extends DefaultResponseProcessor {
    /**
     * Default constructor.
     */
    public FessResponseProcessor() {
        super();
    }

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(FessResponseProcessor.class);

    /** Factory for creating and managing ingesters */
    private IngestFactory ingestFactory = null;

    /**
     * Initializes the processor after dependency injection.
     * Sets up the ingest factory if available in the component system.
     */
    @PostConstruct
    public void init() {
        if (ComponentUtil.hasIngestFactory()) {
            ingestFactory = ComponentUtil.getIngestFactory();
        }
    }

    /**
     * Creates an access result after processing the response data through ingesters.
     *
     * @param responseData the response data from the crawled resource
     * @param resultData the result data to be processed
     * @return the access result containing the processed data
     */
    @Override
    protected AccessResult<?> createAccessResult(final ResponseData responseData, final ResultData resultData) {
        return super.createAccessResult(responseData, ingest(responseData, resultData));
    }

    /**
     * Processes the result data through all available ingesters.
     * Each ingester can transform and enrich the data before it is indexed.
     *
     * @param responseData the response data from the crawled resource
     * @param resultData the result data to be processed
     * @return the processed result data after all ingesters have been applied
     */
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
