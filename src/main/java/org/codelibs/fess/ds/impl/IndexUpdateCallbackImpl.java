/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.client.SearchClient;
import org.codelibs.fess.ds.IndexUpdateCallback;
import org.codelibs.fess.helper.CrawlingSessionHelper;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexUpdateCallbackImpl implements IndexUpdateCallback {
    private static final Logger logger = LoggerFactory.getLogger(IndexUpdateCallbackImpl.class);

    protected SearchClient searchClient;

    public int maxDocumentCacheSize = 5;

    public boolean clickCountEnabled = true;

    public boolean favoriteCountEnabled = true;

    protected volatile AtomicLong documentSize = new AtomicLong(0);

    protected volatile long executeTime = 0;

    final List<Map<String, Object>> docList = new ArrayList<>();

    private FieldHelper fieldHelper;

    @InitMethod
    public void init() {
        fieldHelper = ComponentUtil.getFieldHelper();
    }

    /* (non-Javadoc)
     * @see org.codelibs.fess.ds.impl.IndexUpdateCallback#store(java.util.Map)
     */
    @Override
    public synchronized boolean store(final Map<String, Object> dataMap) {
        final long startTime = System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            logger.debug("Adding " + dataMap);
        }

        //   required check
        final Object urlObj = dataMap.get(fieldHelper.urlField);
        if (urlObj == null) {
            throw new FessSystemException("url is null. dataMap=" + dataMap);
        }

        final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
        final CrawlingSessionHelper crawlingSessionHelper = ComponentUtil.getCrawlingSessionHelper();
        dataMap.put(fieldHelper.idField, crawlingSessionHelper.generateId(dataMap));

        updateDocument(dataMap);

        docList.add(dataMap);
        if (logger.isDebugEnabled()) {
            logger.debug("Added the document. " + "The number of a document cache is " + docList.size() + ".");
        }

        if (docList.size() >= maxDocumentCacheSize) {
            indexingHelper.sendDocuments(searchClient, docList);
        }
        documentSize.getAndIncrement();

        if (!docList.isEmpty()) {
            indexingHelper.sendDocuments(searchClient, docList);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("The number of an added document is " + documentSize.get() + ".");
        }

        executeTime += System.currentTimeMillis() - startTime;
        return true;
    }

    protected void updateDocument(final Map<String, Object> dataMap) {
        final String url = dataMap.get(fieldHelper.urlField).toString();

        if (clickCountEnabled) {
            addClickCountField(dataMap, url, fieldHelper.clickCountField);
        }

        if (favoriteCountEnabled) {
            addFavoriteCountField(dataMap, url, fieldHelper.favoriteCountField);
        }

        if (!dataMap.containsKey(fieldHelper.docIdField)) {
            final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
            dataMap.put(fieldHelper.docIdField, systemHelper.generateDocId(dataMap));
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

    @Override
    public SearchClient getElasticsearchClient() {
        return searchClient;
    }

    @Override
    public void setElasticsearchClient(final SearchClient searchClient) {
        this.searchClient = searchClient;
    }

}
