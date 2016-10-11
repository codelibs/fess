/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import org.codelibs.fess.crawler.client.CrawlerClientFactory;

public class CrawlingConfigWrapper implements CrawlingConfig {

    private CrawlingConfig crawlingConfig;

    public CrawlingConfigWrapper(final CrawlingConfig crawlingConfig) {
        this.crawlingConfig = crawlingConfig;
    }

    public String getId() {
        return crawlingConfig.getId();
    }

    public String getName() {
        return crawlingConfig.getName();
    }

    public String[] getPermissions() {
        return crawlingConfig.getPermissions();
    }

    public String[] getLabelTypeValues() {
        return crawlingConfig.getLabelTypeValues();
    }

    public String getDocumentBoost() {
        return crawlingConfig.getDocumentBoost();
    }

    public String getIndexingTarget(String input) {
        return crawlingConfig.getIndexingTarget(input);
    }

    public String getConfigId() {
        return crawlingConfig.getConfigId();
    }

    public Integer getTimeToLive() {
        return crawlingConfig.getTimeToLive();
    }

    public Map<String, Object> initializeClientFactory(CrawlerClientFactory crawlerClientFactory) {
        return crawlingConfig.initializeClientFactory(crawlerClientFactory);
    }

    public Map<String, String> getConfigParameterMap(ConfigName name) {
        return crawlingConfig.getConfigParameterMap(name);
    }
}
