/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.codelibs.fess.ds.IndexUpdateCallback;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexUpdateCallbackImpl implements IndexUpdateCallback {
    private static final Logger logger = LoggerFactory.getLogger(IndexUpdateCallbackImpl.class);

    protected volatile AtomicLong documentSize = new AtomicLong(0);

    protected volatile long executeTime = 0;

    final List<Map<String, Object>> docList = new ArrayList<>();

    /* (non-Javadoc)
     * @see org.codelibs.fess.ds.impl.IndexUpdateCallback#store(java.util.Map)
     */
    @Override
    public synchronized boolean store(final Map<String, Object> dataMap) {
        final long startTime = System.currentTimeMillis();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final FessEsClient fessEsClient = ComponentUtil.getElasticsearchClient();

        if (logger.isDebugEnabled()) {
            logger.debug("Adding " + dataMap);
        }

        //   required check
        final Object urlObj = dataMap.get(fessConfig.getIndexFieldUrl());
        if (urlObj == null) {
            throw new FessSystemException("url is null. dataMap=" + dataMap);
        }

        final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();
        dataMap.put(fessConfig.getIndexFieldId(), crawlingInfoHelper.generateId(dataMap));

        final String url = dataMap.get(fessConfig.getIndexFieldUrl()).toString();

        if (fessConfig.getIndexerClickCountEnabledAsBoolean()) {
            addClickCountField(dataMap, url, fessConfig.getIndexFieldClickCount());
        }

        if (fessConfig.getIndexerFavoriteCountEnabledAsBoolean()) {
            addFavoriteCountField(dataMap, url, fessConfig.getIndexFieldFavoriteCount());
        }

        if (!dataMap.containsKey(fessConfig.getIndexFieldDocId())) {
            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            dataMap.put(fessConfig.getIndexFieldDocId(), systemHelper.generateDocId(dataMap));
        }

        docList.add(dataMap);
        if (logger.isDebugEnabled()) {
            logger.debug("Added the document. " + "The number of a document cache is " + docList.size() + ".");
        }

        if (docList.size() >= fessConfig.getIndexerDataMaxDocumentCacheSizeAsInteger().intValue()) {
            indexingHelper.sendDocuments(fessEsClient, docList);
        }
        documentSize.getAndIncrement();

        if (logger.isDebugEnabled()) {
            logger.debug("The number of an added document is " + documentSize.get() + ".");
        }

        executeTime += System.currentTimeMillis() - startTime;
        return true;
    }

    @Override
    public void commit() {
        if (!docList.isEmpty()) {
            final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
            final FessEsClient fessEsClient = ComponentUtil.getElasticsearchClient();
            indexingHelper.sendDocuments(fessEsClient, docList);
        }
    }

    protected void addClickCountField(final Map<String, Object> doc, final String url, final String clickCountField) {
        final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();
        final int count = searchLogHelper.getClickCount(url);
        doc.put(clickCountField, count);
        if (logger.isDebugEnabled()) {
            logger.debug("Click Count: " + count + ", url: " + url);
        }
    }

    protected void addFavoriteCountField(final Map<String, Object> doc, final String url, final String favoriteCountField) {
        final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();
        final long count = searchLogHelper.getFavoriteCount(url);
        doc.put(favoriteCountField, count);
        if (logger.isDebugEnabled()) {
            logger.debug("Favorite Count: " + count + ", url: " + url);
        }
    }

    @Override
    public long getDocumentSize() {
        return documentSize.get();
    }

    @Override
    public long getExecuteTime() {
        return executeTime;
    }

}
