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

public class FessCrawlerConfig extends OpenSearchCrawlerConfig {

    @Override
    public String getQueueIndex() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerIndex() + ".queue";
    }

    @Override
    public String getDataIndex() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerIndex() + ".data";
    }

    @Override
    public String getFilterIndex() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerIndex() + ".filter";
    }

    @Override
    public int getQueueShards() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerQueueNumberOfShardsAsInteger();
    }

    @Override
    public int getDataShards() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerDataNumberOfShardsAsInteger();
    }

    @Override
    public int getFilterShards() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerFilterNumberOfShardsAsInteger();
    }

    @Override
    public int getQueueReplicas() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerQueueNumberOfReplicasAsInteger();
    }

    @Override
    public int getDataReplicas() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerDataNumberOfReplicasAsInteger();
    }

    @Override
    public int getFilterReplicas() {
        return ComponentUtil.getFessConfig().getIndexDocumentCrawlerFilterNumberOfReplicasAsInteger();
    }

}
