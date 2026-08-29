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
package org.codelibs.fess.crawler.service;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.order.UrlQueueOrder;
import org.codelibs.fess.crawler.service.impl.OpenSearchUrlQueueService;
import org.codelibs.fess.crawler.util.OpenSearchCrawlerConfig;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Fess-specific URL queue service that selects the fetch order named by the
 * {@code crawl.order} crawling config parameter.
 */
public class FessUrlQueueService extends OpenSearchUrlQueueService {

    private static final Logger logger = LogManager.getLogger(FessUrlQueueService.class);

    /** Aliases from the crawl.order values that shipped before the orders became components. */
    protected static final Map<String, String> LEGACY_ORDER_NAMES =
            Map.of("sequential", "sequentialUrlQueueOrder", "random", "randomUrlQueueOrder");

    /**
     * Constructs a new FessUrlQueueService with the specified crawler configuration.
     *
     * @param crawlerConfig the OpenSearch crawler configuration
     */
    public FessUrlQueueService(final OpenSearchCrawlerConfig crawlerConfig) {
        super(crawlerConfig);
    }

    /**
     * Returns the configured {@code crawl.order} value for the session.
     *
     * @param sessionId the crawling session identifier
     * @return the configured value, or {@literal null} when it is not set
     */
    protected String getConfiguredCrawlOrder(final String sessionId) {
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(sessionId);
        final Map<String, String> configParams = crawlingConfig.getConfigParameterMap(ConfigName.CONFIG);
        return configParams.get(CrawlingConfig.Param.Config.CRAWL_ORDER);
    }

    @Override
    protected UrlQueueOrder getUrlQueueOrder(final String sessionId) {
        final String configured = getConfiguredCrawlOrder(sessionId);
        if (StringUtil.isBlank(configured)) {
            return super.getUrlQueueOrder(sessionId);
        }
        final String name = LEGACY_ORDER_NAMES.getOrDefault(configured, configured);
        try {
            final Object component = ComponentUtil.getComponent(name);
            if (component instanceof UrlQueueOrder) {
                return (UrlQueueOrder) component;
            }
            logger.warn("Component {} is not a UrlQueueOrder. Falling back to the default order.", name);
        } catch (final Exception e) {
            logger.warn("Invalid crawl order specified: {}. Falling back to the default order.", configured);
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to resolve crawl order component: {}", name, e);
            }
        }
        return super.getUrlQueueOrder(sessionId);
    }
}
