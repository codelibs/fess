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
package org.codelibs.fess.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexingHelper {
    private static final Logger logger = LoggerFactory.getLogger(IndexingHelper.class);

    public int maxRetryCount = 5;

    public int defaultRowSize = 100;

    public long requestInterval = 500;

    public void sendDocuments(final FessEsClient fessEsClient, final List<Map<String, Object>> docList) {
        final long execTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("Sending " + docList.size() + " documents to a server.");
        }
        synchronized (fessEsClient) {
            deleteOldDocuments(fessEsClient, docList);
            final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
            fessEsClient.addAll(fieldHelper.docIndex, fieldHelper.docType, docList);
        }
        if (logger.isInfoEnabled()) {
            logger.info("Sent " + docList.size() + " docs (" + (System.currentTimeMillis() - execTime) + "ms)");
        }
        docList.clear();
    }

    private void deleteOldDocuments(final FessEsClient fessEsClient, final List<Map<String, Object>> docList) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();

        final List<String> docIdList = new ArrayList<>();
        for (final Map<String, Object> inputDoc : docList) {
            final Object idValue = inputDoc.get(fieldHelper.idField);
            if (idValue == null) {
                continue;
            }

            final Object configIdValue = inputDoc.get(fieldHelper.configIdField);
            if (configIdValue == null) {
                continue;
            }

            final QueryBuilder queryBuilder =
                    QueryBuilders.boolQuery().must(QueryBuilders.termQuery(fieldHelper.urlField, inputDoc.get(fieldHelper.urlField)))
                            .filter(QueryBuilders.termQuery(fieldHelper.configIdField, configIdValue));

            final List<Map<String, Object>> docs =
                    getDocumentListByQuery(fessEsClient, queryBuilder, new String[] { fieldHelper.idField, fieldHelper.docIdField });
            for (final Map<String, Object> doc : docs) {
                final Object oldIdValue = doc.get(fieldHelper.idField);
                if (!idValue.equals(oldIdValue) && oldIdValue != null) {
                    final Object oldDocIdValue = doc.get(fieldHelper.docIdField);
                    if (oldDocIdValue != null) {
                        docIdList.add(oldDocIdValue.toString());
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(queryBuilder.toString() + " => " + docs);
            }
        }
        if (!docIdList.isEmpty()) {
            fessEsClient.deleteByQuery(fieldHelper.docIndex, fieldHelper.docType,
                    QueryBuilders.idsQuery(fieldHelper.docType).ids(docIdList.stream().toArray(n -> new String[n])));

        }
    }

    public void deleteDocument(final FessEsClient fessEsClient, final String id) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        fessEsClient.delete(fieldHelper.docIndex, fieldHelper.docType, id, 0);
    }

    public void deleteDocumentsByDocId(final FessEsClient fessEsClient, final List<String> docIdList) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        fessEsClient.deleteByQuery(fieldHelper.docIndex, fieldHelper.docType,
                QueryBuilders.idsQuery(fieldHelper.docType).ids(docIdList.stream().toArray(n -> new String[n])));
    }

    public Map<String, Object> getDocument(final FessEsClient fessEsClient, final String id, final String[] fields) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        return fessEsClient.getDocument(fieldHelper.docIndex, fieldHelper.docType, id, requestBuilder -> {
            return true;
        }).orElseGet(() -> null);
    }

    public List<Map<String, Object>> getDocumentListByPrefixId(final FessEsClient fessEsClient, final String id, final String[] fields) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final QueryBuilder queryBuilder = QueryBuilders.prefixQuery(fieldHelper.idField, id);
        return getDocumentListByQuery(fessEsClient, queryBuilder, fields);
    }

    public void deleteChildDocument(final FessEsClient fessEsClient, final String id) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        fessEsClient.deleteByQuery(fieldHelper.docIndex, fieldHelper.docType, QueryBuilders.termQuery(fieldHelper.parentIdField, id));
    }

    public List<Map<String, Object>> getChildDocumentList(final FessEsClient fessEsClient, final String id, final String[] fields) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final QueryBuilder queryBuilder = QueryBuilders.termQuery(fieldHelper.parentIdField, id);
        return getDocumentListByQuery(fessEsClient, queryBuilder, fields);
    }

    protected List<Map<String, Object>> getDocumentListByQuery(final FessEsClient fessEsClient, final QueryBuilder queryBuilder,
            final String[] fields) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();

        final CountResponse countResponse =
                fessEsClient.prepareCount(fieldHelper.docIndex).setTypes(fieldHelper.docType).setQuery(queryBuilder).execute().actionGet();
        final long numFound = countResponse.getCount();
        // TODO max threshold

        return fessEsClient.getDocumentList(fieldHelper.docIndex, fieldHelper.docType, requestBuilder -> {
            requestBuilder.setQuery(queryBuilder).setSize((int) numFound);
            if (fields != null) {
                requestBuilder.addFields(fields);
            }
            return true;
        });

    }
}
