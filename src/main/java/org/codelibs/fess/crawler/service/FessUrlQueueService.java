/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
import org.codelibs.fess.crawler.entity.EsUrlQueue;
import org.codelibs.fess.crawler.service.impl.EsUrlQueueService;
import org.codelibs.fess.crawler.util.EsCrawlerConfig;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.RandomScoreFunctionBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;

public class FessUrlQueueService extends EsUrlQueueService {
    private static final Logger logger = LogManager.getLogger(FessUrlQueueService.class);

    public FessUrlQueueService(final EsCrawlerConfig crawlerConfig) {
        super(crawlerConfig);
    }

    @Override
    protected List<EsUrlQueue> fetchUrlQueueList(final String sessionId) {
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(sessionId);
        final Map<String, String> configParams = crawlingConfig.getConfigParameterMap(ConfigName.CONFIG);
        final String crawlOrder = configParams.getOrDefault(CrawlingConfig.Param.Config.CRAWL_ORDER, "sequential");
        if ("random".equals(crawlOrder)) {
            return getList(EsUrlQueue.class, sessionId,
                    QueryBuilders.functionScoreQuery(QueryBuilders.matchAllQuery(),
                            new FunctionScoreQueryBuilder.FilterFunctionBuilder[] { new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                    new RandomScoreFunctionBuilder().seed(sessionId.hashCode())) }),
                    0, pollingFetchSize, SortBuilders.scoreSort().order(SortOrder.ASC));
        } else if (!"sequential".equals(crawlOrder)) {
            logger.warn("Invalid crawl order specified: {}. Falling back to sequential.", crawlOrder);
        }
        return getList(EsUrlQueue.class, sessionId, null, 0, pollingFetchSize, SortBuilders.fieldSort(CREATE_TIME).order(SortOrder.ASC));
    }
}
