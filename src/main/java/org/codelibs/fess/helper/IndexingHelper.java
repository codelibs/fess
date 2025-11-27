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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.search.TotalHits;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.opensearch.client.SearchEngineClientException;
import org.codelibs.fess.thumbnail.ThumbnailManager;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocList;
import org.codelibs.fess.util.MemoryUtil;
import org.opensearch.action.admin.indices.refresh.RefreshResponse;
import org.opensearch.action.bulk.BulkItemResponse;
import org.opensearch.action.bulk.BulkItemResponse.Failure;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Helper class for indexing operations in the Fess search engine.
 * This class provides functionality for sending documents to the search engine,
 * managing document lifecycle operations (create, update, delete), and handling
 * thumbnail processing during indexing.
 *
 * <p>The IndexingHelper manages bulk operations, handles retries on failures,
 * and provides various query-based operations for document management.
 * It also integrates with the thumbnail generation system and handles
 * the cleanup of old documents during updates.</p>
 */
public class IndexingHelper {
    /** Logger for this class */
    private static final Logger logger = LogManager.getLogger(IndexingHelper.class);

    /** Maximum number of retry attempts for failed operations */
    protected int maxRetryCount = 5;

    /** Default number of rows to process in a single batch */
    protected int defaultRowSize = 100;

    /**
     * Default constructor for indexing helper.
     * Creates a new instance with default values.
     */
    public IndexingHelper() {
        // Default constructor
    }

    /** Interval between requests in milliseconds */
    protected long requestInterval = 500;

    /**
     * Sends a list of documents to the search engine for indexing.
     * This method handles thumbnail processing, deletes old documents with the same URL,
     * and performs bulk indexing operations with proper error handling.
     *
     * @param searchEngineClient the search engine client to use for indexing
     * @param docList the list of documents to be indexed
     * @throws SearchEngineClientException if the bulk indexing operation fails
     */
    public void sendDocuments(final SearchEngineClient searchEngineClient, final DocList docList) {
        if (docList.isEmpty()) {
            return;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final long execTime = systemHelper.getCurrentTimeAsLong();
        if (logger.isDebugEnabled()) {
            logger.debug("Sending {} documents to a server.", docList.size());
        }
        try {
            if (fessConfig.isThumbnailCrawlerEnabled()) {
                final ThumbnailManager thumbnailManager = ComponentUtil.getThumbnailManager();
                final String thumbnailField = fessConfig.getIndexFieldThumbnail();
                docList.stream().forEach(doc -> {
                    if (!thumbnailManager.offer(doc)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Removing {}={} from doc[{}]", thumbnailField, doc.get(thumbnailField),
                                    doc.get(fessConfig.getIndexFieldUrl()));
                        }
                        doc.remove(thumbnailField);
                    }
                });
            }
            final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
            synchronized (searchEngineClient) {
                final long deletedDocCount = deleteOldDocuments(searchEngineClient, docList);
                if (logger.isDebugEnabled()) {
                    logger.debug("Deleted {} stale documents", deletedDocCount);
                }
                final BulkResponse response =
                        searchEngineClient.addAll(fessConfig.getIndexDocumentUpdateIndex(), docList, (doc, builder) -> {
                            final String configId = (String) doc.get(fessConfig.getIndexFieldConfigId());
                            crawlingConfigHelper.getPipeline(configId).ifPresent(s -> builder.setPipeline(s));
                        });
                if (response.hasFailures()) {
                    if (logger.isDebugEnabled()) {
                        final BulkItemResponse[] items = response.getItems();
                        if (docList.size() == items.length) {
                            for (int i = 0; i < docList.size(); i++) {
                                final BulkItemResponse resp = items[i];
                                if (resp.isFailed() && resp.getFailure() != null) {
                                    final Map<String, Object> req = docList.get(i);
                                    final Failure failure = resp.getFailure();
                                    logger.debug("Failed Request: {}\n=>{}", req, failure.getMessage());
                                }
                            }
                        }
                    }
                    throw new SearchEngineClientException(response.buildFailureMessage());
                }
            }
            if (logger.isInfoEnabled()) {
                if (docList.getContentSize() > 0) {
                    logger.info("Sent {} documents (process={}ms, send={}ms, size={}, {})", docList.size(), docList.getProcessingTime(),
                            systemHelper.getCurrentTimeAsLong() - execTime, MemoryUtil.byteCountToDisplaySize(docList.getContentSize()),
                            MemoryUtil.getMemoryUsageLog());
                } else {
                    logger.info("Sent {} documents (send={}ms, {})", docList.size(), systemHelper.getCurrentTimeAsLong() - execTime,
                            MemoryUtil.getMemoryUsageLog());
                }
            }
        } finally {
            docList.clear();
        }
    }

    /**
     * Deletes old documents that have the same URL but different document IDs
     * as the documents in the provided list. This prevents duplicate documents
     * from accumulating in the index.
     *
     * @param searchEngineClient the search engine client to use for deletion
     * @param docList the list of new documents to check against
     * @return the number of old documents that were deleted
     */
    protected long deleteOldDocuments(final SearchEngineClient searchEngineClient, final DocList docList) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final List<String> docIdList = new ArrayList<>();
        for (final Map<String, Object> inputDoc : docList) {
            final Object idValue = inputDoc.get(fessConfig.getIndexFieldId());
            if (idValue == null) {
                continue;
            }

            final Object configIdValue = inputDoc.get(fessConfig.getIndexFieldConfigId());
            if (configIdValue == null) {
                continue;
            }

            final QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery(fessConfig.getIndexFieldUrl(), inputDoc.get(fessConfig.getIndexFieldUrl())))
                    .filter(QueryBuilders.termQuery(fessConfig.getIndexFieldConfigId(), configIdValue));

            final List<Map<String, Object>> docs = getDocumentListByQuery(searchEngineClient, queryBuilder,
                    new String[] { fessConfig.getIndexFieldId(), fessConfig.getIndexFieldDocId() });
            for (final Map<String, Object> doc : docs) {
                final Object oldIdValue = doc.get(fessConfig.getIndexFieldId());
                if (oldIdValue != null && !idValue.equals(oldIdValue)) {
                    final Object oldDocIdValue = doc.get(fessConfig.getIndexFieldDocId());
                    if (oldDocIdValue != null) {
                        docIdList.add(oldDocIdValue.toString());
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("{} => {}", queryBuilder, docs);
            }
        }
        if (!docIdList.isEmpty()) {
            return deleteDocumentByQuery(searchEngineClient, fessConfig.getIndexDocumentUpdateIndex(),
                    QueryBuilders.termsQuery(fessConfig.getIndexFieldDocId(), docIdList.stream().toArray(n -> new String[n])));
        }
        return 0L;
    }

    /**
     * Updates a specific field of a document in the search index.
     *
     * @param searchEngineClient the search engine client to use for the update
     * @param id the document ID to update
     * @param field the field name to update
     * @param value the new value for the field
     * @return true if the update was successful, false otherwise
     */
    public boolean updateDocument(final SearchEngineClient searchEngineClient, final String id, final String field, final Object value) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return searchEngineClient.update(fessConfig.getIndexDocumentUpdateIndex(), id, field, value);
    }

    /**
     * Deletes a document from the search index by its ID.
     *
     * @param searchEngineClient the search engine client to use for deletion
     * @param id the document ID to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteDocument(final SearchEngineClient searchEngineClient, final String id) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return searchEngineClient.delete(fessConfig.getIndexDocumentUpdateIndex(), id);
    }

    /**
     * Deletes all documents that match the specified URL.
     *
     * @param searchEngineClient the search engine client to use for deletion
     * @param url the URL to match for document deletion
     * @return the number of documents that were deleted
     */
    public long deleteDocumentByUrl(final SearchEngineClient searchEngineClient, final String url) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return deleteDocumentByQuery(searchEngineClient, fessConfig.getIndexDocumentUpdateIndex(),
                QueryBuilders.termQuery(fessConfig.getIndexFieldUrl(), url));
    }

    /**
     * Deletes all documents that match the specified document IDs.
     *
     * @param searchEngineClient the search engine client to use for deletion
     * @param docIdList the list of document IDs to delete
     * @return the number of documents that were deleted
     */
    public long deleteDocumentsByDocId(final SearchEngineClient searchEngineClient, final List<String> docIdList) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return deleteDocumentByQuery(searchEngineClient, fessConfig.getIndexDocumentUpdateIndex(),
                QueryBuilders.termsQuery(fessConfig.getIndexFieldDocId(), docIdList.stream().toArray(n -> new String[n])));
    }

    /**
     * Deletes all documents that match the specified query from the default update index.
     *
     * @param searchEngineClient the search engine client to use for deletion
     * @param queryBuilder the query to match documents for deletion
     * @return the number of documents that were deleted
     */
    public long deleteDocumentByQuery(final SearchEngineClient searchEngineClient, final QueryBuilder queryBuilder) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return deleteDocumentByQuery(searchEngineClient, fessConfig.getIndexDocumentUpdateIndex(), queryBuilder);
    }

    /**
     * Deletes all documents that match the specified query from the specified index.
     *
     * @param searchEngineClient the search engine client to use for deletion
     * @param index the index name to delete documents from
     * @param queryBuilder the query to match documents for deletion
     * @return the number of documents that were deleted
     */
    protected long deleteDocumentByQuery(final SearchEngineClient searchEngineClient, final String index, final QueryBuilder queryBuilder) {
        return searchEngineClient.deleteByQuery(index, queryBuilder);
    }

    /**
     * Retrieves a document from the search index by its ID.
     *
     * @param searchEngineClient the search engine client to use for retrieval
     * @param id the document ID to retrieve
     * @param fields the fields to include in the response (null for all fields)
     * @return the document as a map of field names to values, or null if not found
     */
    public Map<String, Object> getDocument(final SearchEngineClient searchEngineClient, final String id, final String[] fields) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return searchEngineClient.getDocument(fessConfig.getIndexDocumentUpdateIndex(), builder -> {
            builder.setQuery(QueryBuilders.idsQuery().addIds(id));
            builder.setFetchSource(fields, null);
            return true;
        }).orElse(null);
    }

    /**
     * Retrieves a list of documents whose IDs start with the specified prefix.
     *
     * @param searchEngineClient the search engine client to use for retrieval
     * @param id the ID prefix to match documents
     * @param fields the fields to include in the response (null for all fields)
     * @return a list of documents that match the prefix
     */
    public List<Map<String, Object>> getDocumentListByPrefixId(final SearchEngineClient searchEngineClient, final String id,
            final String[] fields) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder queryBuilder = QueryBuilders.prefixQuery(fessConfig.getIndexFieldId(), id);
        return getDocumentListByQuery(searchEngineClient, queryBuilder, fields);
    }

    /**
     * Deletes all child documents that belong to the specified parent document.
     *
     * @param searchEngineClient the search engine client to use for deletion
     * @param id the parent document ID whose children should be deleted
     * @return the number of child documents that were deleted
     */
    public long deleteChildDocument(final SearchEngineClient searchEngineClient, final String id) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return searchEngineClient.deleteByQuery(fessConfig.getIndexDocumentUpdateIndex(),
                QueryBuilders.termQuery(fessConfig.getIndexFieldParentId(), id));
    }

    /**
     * Retrieves all child documents that belong to the specified parent document.
     *
     * @param searchEngineClient the search engine client to use for retrieval
     * @param id the parent document ID whose children should be retrieved
     * @param fields the fields to include in the response (null for all fields)
     * @return a list of child documents
     */
    public List<Map<String, Object>> getChildDocumentList(final SearchEngineClient searchEngineClient, final String id,
            final String[] fields) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder queryBuilder = QueryBuilders.termQuery(fessConfig.getIndexFieldParentId(), id);
        return getDocumentListByQuery(searchEngineClient, queryBuilder, fields);
    }

    /**
     * Retrieves a list of documents that match the specified query.
     * This method handles large result sets by using scroll search when necessary
     * and enforces maximum document size limits.
     *
     * @param searchEngineClient the search engine client to use for retrieval
     * @param queryBuilder the query to match documents
     * @param fields the fields to include in the response (null for all fields)
     * @return a list of documents that match the query
     */
    protected List<Map<String, Object>> getDocumentListByQuery(final SearchEngineClient searchEngineClient, final QueryBuilder queryBuilder,
            final String[] fields) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final long numFound = getDocumentSizeByQuery(searchEngineClient, queryBuilder, fessConfig);
        final long maxSearchDocSize = fessConfig.getIndexerMaxSearchDocSizeAsInteger().longValue();
        final boolean exceeded = numFound > maxSearchDocSize;
        if (exceeded) {
            logger.warn("Max search document size exceeded: found={}, limit={}. query={}", numFound,
                    fessConfig.getIndexerMaxSearchDocSize(), queryBuilder);
        }

        if (numFound > fessConfig.getIndexerMaxResultWindowSizeAsInteger().longValue()) {
            final List<Map<String, Object>> entityList = new ArrayList<>(Long.valueOf(numFound).intValue());
            searchEngineClient.scrollSearch(fessConfig.getIndexDocumentUpdateIndex(), requestBuilder -> {
                requestBuilder.setQuery(queryBuilder).setSize((int) numFound);
                if (fields != null) {
                    requestBuilder.setFetchSource(fields, null);
                }
                return true;
            }, entity -> {
                entityList.add(entity);
                return entityList.size() <= (exceeded ? maxSearchDocSize : numFound);
            });
            return entityList;
        }
        return searchEngineClient.getDocumentList(fessConfig.getIndexDocumentUpdateIndex(), requestBuilder -> {
            requestBuilder.setQuery(queryBuilder).setSize((int) numFound);
            if (fields != null) {
                requestBuilder.setFetchSource(fields, null);
            }
            return true;
        });
    }

    /**
     * Gets the total number of documents that match the specified query.
     *
     * @param searchEngineClient the search engine client to use for the count
     * @param queryBuilder the query to count documents for
     * @param fessConfig the Fess configuration
     * @return the number of documents that match the query
     */
    protected long getDocumentSizeByQuery(final SearchEngineClient searchEngineClient, final QueryBuilder queryBuilder,
            final FessConfig fessConfig) {
        final SearchResponse countResponse = searchEngineClient.prepareSearch(fessConfig.getIndexDocumentUpdateIndex())
                .setQuery(queryBuilder)
                .setSize(0)
                .setTrackTotalHits(true)
                .execute()
                .actionGet(fessConfig.getIndexSearchTimeout());
        final TotalHits totalHits = countResponse.getHits().getTotalHits();
        if (totalHits != null) {
            return totalHits.value();
        }
        return 0;
    }

    /**
     * Deletes all documents associated with the specified session ID.
     *
     * @param sessionId the session ID to delete documents for
     * @return the number of documents that were deleted
     */
    public long deleteBySessionId(final String sessionId) {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String index = fessConfig.getIndexDocumentUpdateIndex();
        return deleteBySessionId(searchEngineClient, index, sessionId);
    }

    /**
     * Deletes all documents associated with the specified session ID from the given index.
     *
     * @param searchEngineClient the search engine client to use for deletion
     * @param index the index name to delete documents from
     * @param sessionId the session ID to delete documents for
     * @return the number of documents that were deleted
     */
    public long deleteBySessionId(final SearchEngineClient searchEngineClient, final String index, final String sessionId) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder queryBuilder = QueryBuilders.termQuery(fessConfig.getIndexFieldSegment(), sessionId);
        return deleteByQueryBuilder(searchEngineClient, index, queryBuilder);
    }

    /**
     * Deletes all documents associated with the specified configuration ID.
     *
     * @param configId the configuration ID to delete documents for
     * @return the number of documents that were deleted
     */
    public long deleteByConfigId(final String configId) {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String index = fessConfig.getIndexDocumentUpdateIndex();
        return deleteByConfigId(searchEngineClient, index, configId);
    }

    /**
     * Deletes all documents associated with the specified configuration ID from the given index.
     *
     * @param searchEngineClient the search engine client to use for deletion
     * @param index the index name to delete documents from
     * @param configId the configuration ID to delete documents for
     * @return the number of documents that were deleted
     */
    public long deleteByConfigId(final SearchEngineClient searchEngineClient, final String index, final String configId) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder queryBuilder = QueryBuilders.termQuery(fessConfig.getIndexFieldConfigId(), configId);
        return deleteByQueryBuilder(searchEngineClient, index, queryBuilder);
    }

    /**
     * Deletes all documents associated with the specified virtual host.
     *
     * @param virtualHost the virtual host to delete documents for
     * @return the number of documents that were deleted
     */
    public long deleteByVirtualHost(final String virtualHost) {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String index = fessConfig.getIndexDocumentUpdateIndex();
        return deleteByVirtualHost(searchEngineClient, index, virtualHost);
    }

    /**
     * Deletes all documents associated with the specified virtual host from the given index.
     *
     * @param searchEngineClient the search engine client to use for deletion
     * @param index the index name to delete documents from
     * @param virtualHost the virtual host to delete documents for
     * @return the number of documents that were deleted
     */
    public long deleteByVirtualHost(final SearchEngineClient searchEngineClient, final String index, final String virtualHost) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder queryBuilder = QueryBuilders.termQuery(fessConfig.getIndexFieldVirtualHost(), virtualHost);
        return deleteByQueryBuilder(searchEngineClient, index, queryBuilder);
    }

    /**
     * Deletes documents using the specified query builder and refreshes the index.
     * This method first refreshes the index, then performs the deletion, and logs the result.
     *
     * @param searchEngineClient the search engine client to use for deletion
     * @param index the index name to delete documents from
     * @param queryBuilder the query to match documents for deletion
     * @return the number of documents that were deleted
     */
    protected long deleteByQueryBuilder(final SearchEngineClient searchEngineClient, final String index, final QueryBuilder queryBuilder) {
        refreshIndex(searchEngineClient, index);
        final long numOfDeleted = searchEngineClient.deleteByQuery(index, queryBuilder);
        if (logger.isDebugEnabled()) {
            logger.debug("Deleted {} stale documents.", numOfDeleted);
        }
        return numOfDeleted;
    }

    /**
     * Refreshes the specified index to make recent changes visible for search.
     * This operation ensures that all pending writes are persisted and searchable.
     *
     * @param searchEngineClient the search engine client to use for refresh
     * @param index the index name to refresh
     * @return the refresh status code
     */
    protected int refreshIndex(final SearchEngineClient searchEngineClient, final String index) {
        final RefreshResponse response = searchEngineClient.admin().indices().prepareRefresh(index).execute().actionGet();
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] refresh status: {} ({}/{}/{})", index, response.getStatus(), response.getTotalShards(),
                    response.getSuccessfulShards(), response.getFailedShards());
        }
        return response.getStatus().getStatus();
    }

    /**
     * Calculates the memory size of a document data map.
     * This is useful for monitoring memory usage during indexing operations.
     *
     * @param dataMap the document data as a map of field names to values
     * @return the estimated memory size in bytes
     */
    public long calculateDocumentSize(final Map<String, Object> dataMap) {
        return MemoryUtil.sizeOf(dataMap);
    }

    /**
     * Sets the maximum number of retry attempts for failed operations.
     *
     * @param maxRetryCount the maximum retry count
     */
    public void setMaxRetryCount(final int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    /**
     * Sets the default number of rows to process in a single batch.
     *
     * @param defaultRowSize the default row size
     */
    public void setDefaultRowSize(final int defaultRowSize) {
        this.defaultRowSize = defaultRowSize;
    }

    /**
     * Sets the interval between requests in milliseconds.
     *
     * @param requestInterval the request interval in milliseconds
     */
    public void setRequestInterval(final long requestInterval) {
        this.requestInterval = requestInterval;
    }

}
