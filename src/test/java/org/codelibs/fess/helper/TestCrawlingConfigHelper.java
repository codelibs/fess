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
package org.codelibs.fess.helper;

import java.util.Collections;
import java.util.List;

import org.codelibs.fess.opensearch.config.exentity.DataConfig;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;

/**
 * Test stub implementation of CrawlingConfigHelper for unit testing.
 * Provides minimal functionality to support test scenarios.
 */
public class TestCrawlingConfigHelper extends CrawlingConfigHelper {

    // Configurable return values for tests
    private List<String> excludedUrlList = null;

    @Override
    public List<WebConfig> getWebConfigListByIds(List<String> webConfigIdList) {
        return Collections.emptyList();
    }

    @Override
    public List<FileConfig> getFileConfigListByIds(List<String> fileConfigIdList) {
        return Collections.emptyList();
    }

    @Override
    public List<DataConfig> getDataConfigListByIds(List<String> configIdList) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getExcludedUrlList(String configId) {
        return excludedUrlList;
    }

    @Override
    public String store(String sessionId, WebConfig webConfig) {
        return sessionId + "-web";
    }

    @Override
    public String store(String sessionId, FileConfig fileConfig) {
        return sessionId + "-file";
    }

    @Override
    public String store(String sessionId, DataConfig dataConfig) {
        return sessionId + "-data";
    }

    @Override
    public void remove(String sessionId) {
        // No-op for testing
    }

    // Test helper methods to configure behavior
    public void setExcludedUrlList(List<String> excludedUrlList) {
        this.excludedUrlList = excludedUrlList;
    }
}
