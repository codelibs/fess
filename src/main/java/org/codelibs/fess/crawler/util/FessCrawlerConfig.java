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
package org.codelibs.fess.crawler.util;

import org.codelibs.fess.util.ComponentUtil;

/**
 * Fess-specific crawler configuration that extends OpenSearchCrawlerConfig.
 * This class provides configuration settings for the Fess crawler including
 * index names, shard counts, and replica counts for queue, data, and filter indices.
 */
public class FessCrawlerConfig extends OpenSearchCrawlerConfig {

    /**
     * Gets the name of the queue index for the crawler.
     *
     * @return the queue index name
     */
    @Override
    public String getQueueIndex() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerIndex() + ".queue";
    }

    /**
     * Gets the name of the data index for the crawler.
     *
     * @return the data index name
     */
    @Override
    public String getDataIndex() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerIndex() + ".data";
    }

    /**
     * Gets the name of the filter index for the crawler.
     *
     * @return the filter index name
     */
    @Override
    public String getFilterIndex() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerIndex() + ".filter";
    }

    /**
     * Gets the number of shards for the queue index.
     *
     * @return the number of queue shards
     */
    @Override
    public int getQueueShards() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerQueueNumberOfShardsAsInteger();
    }

    /**
     * Gets the number of shards for the data index.
     *
     * @return the number of data shards
     */
    @Override
    public int getDataShards() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerDataNumberOfShardsAsInteger();
    }

    /**
     * Gets the number of shards for the filter index.
     *
     * @return the number of filter shards
     */
    @Override
    public int getFilterShards() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerFilterNumberOfShardsAsInteger();
    }

    /**
     * Gets the number of replicas for the queue index.
     *
     * @return the number of queue replicas
     */
    @Override
    public int getQueueReplicas() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerQueueNumberOfReplicasAsInteger();
    }

    /**
     * Gets the number of replicas for the data index.
     *
     * @return the number of data replicas
     */
    @Override
    public int getDataReplicas() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerDataNumberOfReplicasAsInteger();
    }

    /**
     * Gets the number of replicas for the filter index.
     *
     * @return the number of filter replicas
     */
    @Override
    public int getFilterReplicas() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerFilterNumberOfReplicasAsInteger();
    }

}
