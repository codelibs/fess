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
package org.codelibs.fess.ds.callback;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.entity.DataStoreParams;
import org.codelibs.fess.exception.DataStoreException;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.ingest.IngestFactory;
import org.codelibs.fess.ingest.Ingester;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocList;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.MemoryUtil;

import jakarta.annotation.PostConstruct;

/**
 * Implementation of IndexUpdateCallback for handling document indexing operations.
 * This class manages the process of updating the search index with documents from
 * data stores, including bulk operations, document transformation, and error handling.
 */
public class IndexUpdateCallbackImpl implements IndexUpdateCallback {
    private static final Logger logger = LogManager.getLogger(IndexUpdateCallbackImpl.class);

    /**
     * Default constructor for index update callback implementation.
     * Creates a new instance with default values.
     */
    public IndexUpdateCallbackImpl() {
        // Default constructor
    }

    /** Atomic counter for the number of documents processed */
    protected AtomicLong documentSize = new AtomicLong(0);

    /** Total execution time for all operations */
    protected volatile long executeTime = 0;

    /** List of documents waiting to be indexed */
    protected final DocList docList = new DocList();

    /** Maximum size of document requests in bytes */
    protected long maxDocumentRequestSize;

    /** Maximum number of documents to cache before indexing */
    protected int maxDocumentCacheSize;

    /** Factory for creating ingesters to process documents */
    private IngestFactory ingestFactory = null;

    /**
     * Initializes the callback implementation after dependency injection.
     * Sets up configuration values and initializes the ingest factory if available.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        maxDocumentRequestSize = Long.parseLong(ComponentUtil.getFessConfig().getIndexerDataMaxDocumentRequestSize());
        maxDocumentCacheSize = ComponentUtil.getFessConfig().getIndexerDataMaxDocumentCacheSizeAsInteger();
        if (ComponentUtil.hasIngestFactory()) {
            ingestFactory = ComponentUtil.getIngestFactory();
        }
    }

    /**
     * Stores a document in the index after processing and validation.
     * Handles document transformation, field addition, and batched indexing.
     *
     * @param paramMap the data store parameters
     * @param dataMap the document data to store
     * @throws DataStoreException if required fields are missing or other errors occur
     */
    @Override
    public void store(final DataStoreParams paramMap, final Map<String, Object> dataMap) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        systemHelper.calibrateCpuLoad();

        final long startTime = systemHelper.getCurrentTimeAsLong();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();

        if (logger.isDebugEnabled()) {
            logger.debug("Adding {}", dataMap);
        }

        //   required check
        final Object urlObj = dataMap.get(fessConfig.getIndexFieldUrl());
        if (urlObj == null) {
            final Object configId = dataMap.get(fessConfig.getIndexFieldConfigId());
            throw new DataStoreException("URL field is null in dataMap. Cannot index document without a URL. configId: " + configId);
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

        final Set<String> matchedLabelSet = ComponentUtil.getLabelTypeHelper().getMatchedLabelValueSet(url);
        if (!matchedLabelSet.isEmpty()) {
            final Set<String> newLabelSet = new HashSet<>();
            final String[] oldLabels = DocumentUtil.getValue(dataMap, fessConfig.getIndexFieldLabel(), String[].class);
            StreamUtil.stream(oldLabels).of(stream -> stream.forEach(newLabelSet::add));
            matchedLabelSet.stream().forEach(newLabelSet::add);
            dataMap.put(fessConfig.getIndexFieldLabel(), newLabelSet.toArray(new String[newLabelSet.size()]));
        }

        if (!dataMap.containsKey(fessConfig.getIndexFieldDocId())) {
            dataMap.put(fessConfig.getIndexFieldDocId(), systemHelper.generateDocId(dataMap));
        }

        ComponentUtil.getLanguageHelper().updateDocument(dataMap);

        synchronized (docList) {
            docList.add(ingest(paramMap, dataMap));
            final long contentSize = indexingHelper.calculateDocumentSize(dataMap);
            docList.addContentSize(contentSize);
            final long processingTime = systemHelper.getCurrentTimeAsLong() - startTime;
            docList.addProcessingTime(processingTime);
            if (logger.isDebugEnabled()) {
                logger.debug("Added the document({}, {}ms). The number of a document cache is {}.",
                        MemoryUtil.byteCountToDisplaySize(contentSize), processingTime, docList.size());
            }

            if (docList.getContentSize() >= maxDocumentRequestSize || docList.size() >= maxDocumentCacheSize) {
                indexingHelper.sendDocuments(searchEngineClient, docList);
            }
            executeTime += processingTime;
        }

        documentSize.getAndIncrement();

        if (logger.isDebugEnabled()) {
            logger.debug("The number of an added document is {}.", documentSize.get());
        }

    }

    /**
     * Processes a document through the ingest pipeline.
     * Applies all available ingesters to transform the document data.
     *
     * @param paramMap the data store parameters
     * @param dataMap the document data to process
     * @return the processed document data
     */
    protected Map<String, Object> ingest(final DataStoreParams paramMap, final Map<String, Object> dataMap) {
        if (ingestFactory == null) {
            return dataMap;
        }
        Map<String, Object> target = dataMap;
        for (final Ingester ingester : ingestFactory.getIngesters()) {
            try {
                target = ingester.process(target, paramMap);
            } catch (final Exception e) {
                logger.warn("Failed to process Ingest[{}]", ingester.getClass().getSimpleName(), e);
            }
        }
        return target;
    }

    /**
     * Commits any remaining documents in the cache to the index.
     * This method ensures all pending documents are processed.
     */
    @Override
    public void commit() {
        synchronized (docList) {
            if (!docList.isEmpty()) {
                final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
                final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
                indexingHelper.sendDocuments(searchEngineClient, docList);
            }
        }
    }

    /**
     * Adds click count information to the document.
     *
     * @param doc the document to update
     * @param url the URL to get click count for
     * @param clickCountField the field name to store click count
     */
    protected void addClickCountField(final Map<String, Object> doc, final String url, final String clickCountField) {
        final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();
        final int count = searchLogHelper.getClickCount(url);
        doc.put(clickCountField, count);
        if (logger.isDebugEnabled()) {
            logger.debug("Click Count: {}, url: {}", count, url);
        }
    }

    /**
     * Adds favorite count information to the document.
     *
     * @param doc the document to update
     * @param url the URL to get favorite count for
     * @param favoriteCountField the field name to store favorite count
     */
    protected void addFavoriteCountField(final Map<String, Object> doc, final String url, final String favoriteCountField) {
        final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();
        final long count = searchLogHelper.getFavoriteCount(url);
        doc.put(favoriteCountField, count);
        if (logger.isDebugEnabled()) {
            logger.debug("Favorite Count: {}, url: {}", count, url);
        }
    }

    /**
     * Returns the total number of documents processed.
     *
     * @return the number of documents processed
     */
    @Override
    public long getDocumentSize() {
        return documentSize.get();
    }

    /**
     * Returns the total execution time for all operations.
     *
     * @return the total execution time in milliseconds
     */
    @Override
    public long getExecuteTime() {
        return executeTime;
    }

}
