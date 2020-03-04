/*
 * Copyright 2012-2020 CodeLibs Project and the Others.
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
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.thumbnail.ThumbnailManager;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocList;
import org.codelibs.fess.util.MemoryUtil;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class IndexingHelper {
    private static final Logger logger = LogManager.getLogger(IndexingHelper.class);

    protected int maxRetryCount = 5;

    protected int defaultRowSize = 100;

    protected long requestInterval = 500;

    public void sendDocuments(final FessEsClient fessEsClient, final DocList docList) {
        if (docList.isEmpty()) {
            return;
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.isResultCollapsed()) {
            docList.forEach(doc -> {
                doc.put("content_minhash", doc.get(fessConfig.getIndexFieldContent()));
            });
        }
        final long execTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("Sending {} documents to a server.", docList.size());
        }
        try {
            if (fessConfig.isThumbnailCrawlerEnabled()) {
                final ThumbnailManager thumbnailManager = ComponentUtil.getThumbnailManager();
                docList.stream().forEach(
                        doc -> {
                            if (!thumbnailManager.offer(doc)) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Removing {} from {}", doc.get(fessConfig.getIndexFieldThumbnail()),
                                            doc.get(fessConfig.getIndexFieldUrl()));
                                }
                                doc.remove(fessConfig.getIndexFieldThumbnail());
                            }
                        });
            }
            final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
            synchronized (fessEsClient) {
                deleteOldDocuments(fessEsClient, docList);
                fessEsClient.addAll(fessConfig.getIndexDocumentUpdateIndex(), docList, (doc, builder) -> {
                    final String configId = (String) doc.get(fessConfig.getIndexFieldConfigId());
                    crawlingConfigHelper.getPipeline(configId).ifPresent(s -> builder.setPipeline(s));
                });
            }
            if (logger.isInfoEnabled()) {
                if (docList.getContentSize() > 0) {
                    logger.info("Sent " + docList.size() + " docs (Doc:{process " + docList.getProcessingTime() + "ms, send "
                            + (System.currentTimeMillis() - execTime) + "ms, size "
                            + MemoryUtil.byteCountToDisplaySize(docList.getContentSize()) + "}, " + MemoryUtil.getMemoryUsageLog() + ")");
                } else {
                    logger.info("Sent " + docList.size() + " docs (Doc:{send " + (System.currentTimeMillis() - execTime) + "ms}, "
                            + MemoryUtil.getMemoryUsageLog() + ")");
                }
            }
        } finally {
            docList.clear();
        }
    }

    private void deleteOldDocuments(final FessEsClient fessEsClient, final DocList docList) {
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

            final QueryBuilder queryBuilder =
                    QueryBuilders.boolQuery()
                            .must(QueryBuilders.termQuery(fessConfig.getIndexFieldUrl(), inputDoc.get(fessConfig.getIndexFieldUrl())))
                            .filter(QueryBuilders.termQuery(fessConfig.getIndexFieldConfigId(), configIdValue));

            final List<Map<String, Object>> docs =
                    getDocumentListByQuery(fessEsClient, queryBuilder,
                            new String[] { fessConfig.getIndexFieldId(), fessConfig.getIndexFieldDocId() });
            for (final Map<String, Object> doc : docs) {
                final Object oldIdValue = doc.get(fessConfig.getIndexFieldId());
                if (!idValue.equals(oldIdValue) && oldIdValue != null) {
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
            fessEsClient.deleteByQuery(fessConfig.getIndexDocumentUpdateIndex(),
                    QueryBuilders.idsQuery().addIds(docIdList.stream().toArray(n -> new String[n])));

        }
    }

    public boolean updateDocument(final FessEsClient fessEsClient, final String id, final String field, final Object value) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessEsClient.update(fessConfig.getIndexDocumentUpdateIndex(), id, field, value);
    }

    public boolean deleteDocument(final FessEsClient fessEsClient, final String id) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessEsClient.delete(fessConfig.getIndexDocumentUpdateIndex(), id);
    }

    public long deleteDocumentByUrl(final FessEsClient fessEsClient, final String url) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessEsClient.deleteByQuery(fessConfig.getIndexDocumentUpdateIndex(),
                QueryBuilders.termQuery(fessConfig.getIndexFieldUrl(), url));
    }

    public long deleteDocumentsByDocId(final FessEsClient fessEsClient, final List<String> docIdList) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessEsClient.deleteByQuery(fessConfig.getIndexDocumentUpdateIndex(),
                QueryBuilders.idsQuery().addIds(docIdList.stream().toArray(n -> new String[n])));
    }

    public Map<String, Object> getDocument(final FessEsClient fessEsClient, final String id, final String[] fields) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return fessEsClient.getDocument(fessConfig.getIndexDocumentUpdateIndex(), builder -> {
            builder.setQuery(QueryBuilders.idsQuery().addIds(id));
            builder.setFetchSource(fields, null);
            return true;
        }).orElse(null);
    }

    public List<Map<String, Object>> getDocumentListByPrefixId(final FessEsClient fessEsClient, final String id, final String[] fields) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder queryBuilder = QueryBuilders.prefixQuery(fessConfig.getIndexFieldId(), id);
        return getDocumentListByQuery(fessEsClient, queryBuilder, fields);
    }

    public void deleteChildDocument(final FessEsClient fessEsClient, final String id) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        fessEsClient.deleteByQuery(fessConfig.getIndexDocumentUpdateIndex(),
                QueryBuilders.termQuery(fessConfig.getIndexFieldParentId(), id));
    }

    public List<Map<String, Object>> getChildDocumentList(final FessEsClient fessEsClient, final String id, final String[] fields) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final QueryBuilder queryBuilder = QueryBuilders.termQuery(fessConfig.getIndexFieldParentId(), id);
        return getDocumentListByQuery(fessEsClient, queryBuilder, fields);
    }

    protected List<Map<String, Object>> getDocumentListByQuery(final FessEsClient fessEsClient, final QueryBuilder queryBuilder,
            final String[] fields) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final SearchResponse countResponse =
                fessEsClient.prepareSearch(fessConfig.getIndexDocumentUpdateIndex()).setQuery(queryBuilder).setSize(0).execute()
                        .actionGet(fessConfig.getIndexSearchTimeout());
        final long numFound = countResponse.getHits().getTotalHits().value;
        // TODO max threshold

        return fessEsClient.getDocumentList(fessConfig.getIndexDocumentUpdateIndex(), requestBuilder -> {
            requestBuilder.setQuery(queryBuilder).setSize((int) numFound);
            if (fields != null) {
                requestBuilder.setFetchSource(fields, null);
            }
            return true;
        });

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
