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
package org.codelibs.fess.ds.impl;

import java.util.Map;

import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.ds.IndexUpdateCallback;
import org.codelibs.fess.es.config.exentity.DataConfig;
import org.codelibs.fess.util.ComponentUtil;

public class EsListDataStoreImpl extends EsDataStoreImpl {

    @Override
    protected void storeData(final DataConfig dataConfig, final IndexUpdateCallback callback, final Map<String, String> paramMap,
            final Map<String, String> scriptMap, final Map<String, Object> defaultDataMap) {

        final CrawlerClientFactory crawlerClientFactory = ComponentUtil.getCrawlerClientFactory();
        dataConfig.initializeClientFactory(crawlerClientFactory);
        final FileListIndexUpdateCallbackImpl fileListIndexUpdateCallback =
                new FileListIndexUpdateCallbackImpl(callback, crawlerClientFactory);
        super.storeData(dataConfig, fileListIndexUpdateCallback, paramMap, scriptMap, defaultDataMap);
        fileListIndexUpdateCallback.commit();
    }

}
