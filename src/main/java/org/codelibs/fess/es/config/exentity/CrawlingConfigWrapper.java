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
package org.codelibs.fess.es.config.exentity;

import java.util.Map;
import java.util.function.Supplier;

import org.codelibs.fess.crawler.client.CrawlerClientFactory;

public class CrawlingConfigWrapper implements CrawlingConfig {

    private final CrawlingConfig crawlingConfig;

    public CrawlingConfigWrapper(final CrawlingConfig crawlingConfig) {
        this.crawlingConfig = crawlingConfig;
    }

    @Override
    public String getId() {
        return crawlingConfig.getId();
    }

    @Override
    public String getName() {
        return crawlingConfig.getName();
    }

    @Override
    public String[] getPermissions() {
        return crawlingConfig.getPermissions();
    }

    @Override
    public String[] getVirtualHosts() {
        return crawlingConfig.getVirtualHosts();
    }

    @Override
    public String getDocumentBoost() {
        return crawlingConfig.getDocumentBoost();
    }

    @Override
    public String getIndexingTarget(final String input) {
        return crawlingConfig.getIndexingTarget(input);
    }

    @Override
    public String getConfigId() {
        return crawlingConfig.getConfigId();
    }

    @Override
    public Integer getTimeToLive() {
        return crawlingConfig.getTimeToLive();
    }

    @Override
    public CrawlerClientFactory initializeClientFactory(final Supplier<CrawlerClientFactory> creator) {
        return crawlingConfig.initializeClientFactory(creator);
    }

    @Override
    public Map<String, String> getConfigParameterMap(final ConfigName name) {
        return crawlingConfig.getConfigParameterMap(name);
    }
}
