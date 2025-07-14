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

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.OpenSearchUrlQueue;
import org.codelibs.fess.crawler.service.impl.OpenSearchUrlQueueService;
import org.codelibs.fess.crawler.util.OpenSearchCrawlerConfig;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.RandomScoreFunctionBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;

/**
 * Fess-specific URL queue service that extends OpenSearch URL queue functionality.
 * This service provides customized URL fetching strategies including sequential and random ordering
 * based on crawling configuration parameters.
 */
public class FessUrlQueueService extends OpenSearchUrlQueueService {

    private static final Logger logger = LogManager.getLogger(FessUrlQueueService.class);

    protected static final String ORDER_SEQUENTIAL = "sequential";

    protected static final String ORDER_RANDOM = "random";

    /**
     * Constructs a new FessUrlQueueService with the specified crawler configuration.
     *
     * @param crawlerConfig the OpenSearch crawler configuration
     */
    public FessUrlQueueService(final OpenSearchCrawlerConfig crawlerConfig) {
        super(crawlerConfig);
    }

    /**
     * Fetches URL queue list for the specified session with configurable ordering strategy.
     * Supports sequential (default) and random ordering based on crawling configuration.
     *
     * @param sessionId the crawling session identifier
     * @return list of URL queue entries for processing
     */
    @Override
    protected List<OpenSearchUrlQueue> fetchUrlQueueList(final String sessionId) {
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(sessionId);
        final Map<String, String> configParams = crawlingConfig.getConfigParameterMap(ConfigName.CONFIG);
        final String crawlOrder = configParams.getOrDefault(CrawlingConfig.Param.Config.CRAWL_ORDER, ORDER_SEQUENTIAL);
        if (ORDER_RANDOM.equals(crawlOrder)) {
            return getList(OpenSearchUrlQueue.class, sessionId,
                    QueryBuilders.functionScoreQuery(QueryBuilders.matchAllQuery(),
                            new FunctionScoreQueryBuilder.FilterFunctionBuilder[] { new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                    new RandomScoreFunctionBuilder().seed(sessionId.hashCode())) }),
                    0, pollingFetchSize, SortBuilders.scoreSort().order(SortOrder.DESC));
        } else if (!ORDER_SEQUENTIAL.equals(crawlOrder)) {
            logger.warn("Invalid crawl order specified: {}. Falling back to sequential.", crawlOrder);
        }
        return super.fetchUrlQueueList(sessionId);
    }
}
