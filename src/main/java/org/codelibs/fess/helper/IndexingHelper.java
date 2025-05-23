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

public class IndexingHelper {
    private static final Logger logger = LogManager.getLogger(IndexingHelper.class);

    protected int maxRetryCount = 5;

    protected int defaultRowSize = 100;

    protected long requestInterval = 500;

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
                    logger.debug("Deleted {} old docs", deletedDocCount);
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
                    logger.info("Sent {} docs (Doc:{process {}ms, send {}ms, size {}}, {})", docList.size(), docList.getProcessingTime(),
                            systemHelper.getCurrentTimeAsLong() - execTime, MemoryUtil.byteCountToDisplaySize(docList.getContentSize()),
                            MemoryUtil.getMemoryUsageLog());
                } else {
                    logger.info("Sent {}  docs (Doc:{send {}ms}, {})", docList.size(), systemHelper.getCurrentTimeAsLong() - execTime,
                            MemoryUtil.getMemoryUsageLog());
                }
            }
        } finally {
            docList.clear();
        }
    }

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

    public boolean updateDocument(final SearchEngineClient searchEngineClient, final String id, final String field, final Object value) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return searchEngineClient.update(fessConfig.getIndexDocumentUpdateIndex(), id, field, value);
    }

    public boolean deleteDocument(final SearchEngineClient searchEngineClient, final String id) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return searchEngineClient.delete(fessConfig.getIndexDocumentUpdateIndex(), id);
    }

    public long deleteDocumentByUrl(final SearchEngineClient searchEngineClient, final String url) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return deleteDocumentByQuery(searchEngineClient, fessConfig.getIndexDocumentUpdateIndex(),
                QueryBuilders.termQuery(fessConfig.getIndexFieldUrl(), url));
    }

    public long deleteDocumentsByDocId(final SearchEngineClient searchEngineClient, final List<String> docIdList) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return deleteDocumentByQuery(searchEngineClient, fessConfig.getIndexDocumentUpdateIndex(),
                QueryBuilders.termsQuery(fessConfig.getIndexFieldDocId(), docIdList.stream().toArray(n -> new String[n])));
    }

    public long deleteDocumentByQuery(final SearchEngineClient searchEngineClient, final QueryBuilder queryBuilder) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return deleteDocumentByQuery(searchEngineClient, fessConfig.getIndexDocumentUpdateIndex(), queryBuilder);
    }

    protected long deleteDocumentByQuery(final SearchEngineClient searchEngineClient, final String index, final QueryBuilder queryBuilder) {
        return searchEngineClient.deleteByQuery(index, queryBuilder);
    }

    public Map<String, Object> getDocument(final SearchEngineClient searchEngineClient, final String id, final String[] fields) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return searchEngineClient.getDocument(fessConfig.getIndexDocumentUpdateIndex(), builder -> {
            builder.setQuery(QueryBuilders.idsQuery().addIds(id));
            builder.setFetchSource(fields, null);
            return true;
        }).orElse(null);
    }

    public List<Map<String, Object>> getDocumentListByPrefixId(final SearchEngineClient searchEngineClient, final String id,
            final String[] fields) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder queryBuilder = QueryBuilders.prefixQuery(fessConfig.getIndexFieldId(), id);
        return getDocumentListByQuery(searchEngineClient, queryBuilder, fields);
    }

    public long deleteChildDocument(final SearchEngineClient searchEngineClient, final String id) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return searchEngineClient.deleteByQuery(fessConfig.getIndexDocumentUpdateIndex(),
                QueryBuilders.termQuery(fessConfig.getIndexFieldParentId(), id));
    }

    public List<Map<String, Object>> getChildDocumentList(final SearchEngineClient searchEngineClient, final String id,
            final String[] fields) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder queryBuilder = QueryBuilders.termQuery(fessConfig.getIndexFieldParentId(), id);
        return getDocumentListByQuery(searchEngineClient, queryBuilder, fields);
    }

    protected List<Map<String, Object>> getDocumentListByQuery(final SearchEngineClient searchEngineClient, final QueryBuilder queryBuilder,
            final String[] fields) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final long numFound = getDocumentSizeByQuery(searchEngineClient, queryBuilder, fessConfig);
        final long maxSearchDocSize = fessConfig.getIndexerMaxSearchDocSizeAsInteger().longValue();
        final boolean exceeded = numFound > maxSearchDocSize;
        if (exceeded) {
            logger.warn("Max document size is exceeded({}>{}): {}", numFound, fessConfig.getIndexerMaxSearchDocSize(), queryBuilder);
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

    protected long getDocumentSizeByQuery(final SearchEngineClient searchEngineClient, final QueryBuilder queryBuilder,
            final FessConfig fessConfig) {
        final SearchResponse countResponse = searchEngineClient.prepareSearch(fessConfig.getIndexDocumentUpdateIndex())
                .setQuery(queryBuilder).setSize(0).setTrackTotalHits(true).execute().actionGet(fessConfig.getIndexSearchTimeout());
        final TotalHits totalHits = countResponse.getHits().getTotalHits();
        if (totalHits != null) {
            return totalHits.value();
        }
        return 0;
    }

    public long deleteBySessionId(final String sessionId) {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String index = fessConfig.getIndexDocumentUpdateIndex();
        return deleteBySessionId(searchEngineClient, index, sessionId);
    }

    public long deleteBySessionId(final SearchEngineClient searchEngineClient, final String index, final String sessionId) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder queryBuilder = QueryBuilders.termQuery(fessConfig.getIndexFieldSegment(), sessionId);
        return deleteByQueryBuilder(searchEngineClient, index, queryBuilder);
    }

    public long deleteByConfigId(final String configId) {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String index = fessConfig.getIndexDocumentUpdateIndex();
        return deleteByConfigId(searchEngineClient, index, configId);
    }

    public long deleteByConfigId(final SearchEngineClient searchEngineClient, final String index, final String configId) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder queryBuilder = QueryBuilders.termQuery(fessConfig.getIndexFieldConfigId(), configId);
        return deleteByQueryBuilder(searchEngineClient, index, queryBuilder);
    }

    public long deleteByVirtualHost(final String virtualHost) {
        final SearchEngineClient searchEngineClient = ComponentUtil.getSearchEngineClient();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String index = fessConfig.getIndexDocumentUpdateIndex();
        return deleteByVirtualHost(searchEngineClient, index, virtualHost);
    }

    public long deleteByVirtualHost(final SearchEngineClient searchEngineClient, final String index, final String virtualHost) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder queryBuilder = QueryBuilders.termQuery(fessConfig.getIndexFieldVirtualHost(), virtualHost);
        return deleteByQueryBuilder(searchEngineClient, index, queryBuilder);
    }

    protected long deleteByQueryBuilder(final SearchEngineClient searchEngineClient, final String index, final QueryBuilder queryBuilder) {
        refreshIndex(searchEngineClient, index);
        final long numOfDeleted = searchEngineClient.deleteByQuery(index, queryBuilder);
        if (logger.isDebugEnabled()) {
            logger.debug("Deleted {} old docs.", numOfDeleted);
        }
        return numOfDeleted;
    }

    protected int refreshIndex(final SearchEngineClient searchEngineClient, final String index) {
        final RefreshResponse response = searchEngineClient.admin().indices().prepareRefresh(index).execute().actionGet();
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] refresh status: {} ({}/{}/{})", index, response.getStatus(), response.getTotalShards(),
                    response.getSuccessfulShards(), response.getFailedShards());
        }
        return response.getStatus().getStatus();
    }

    public long calculateDocumentSize(final Map<String, Object> dataMap) {
        return MemoryUtil.sizeOf(dataMap);
    }

    public void setMaxRetryCount(final int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public void setDefaultRowSize(final int defaultRowSize) {
        this.defaultRowSize = defaultRowSize;
    }

    public void setRequestInterval(final long requestInterval) {
        this.requestInterval = requestInterval;
    }

}
