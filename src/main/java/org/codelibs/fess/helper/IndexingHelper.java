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

package org.codelibs.fess.helper;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.client.SearchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexingHelper {
    private static final Logger logger = LoggerFactory.getLogger(IndexingHelper.class);

    public int maxRetryCount = 5;

    public int defaultRowSize = 100;

    public long requestInterval = 500;

    public void sendDocuments(final SearchClient searchClient, final List<Map<String, Object>> docList) {
        final long execTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("Sending " + docList.size() + " documents to a server.");
        }
        synchronized (searchClient) {
            deleteOldDocuments(searchClient, docList);
            searchClient.addAll(docList);
        }
        if (logger.isInfoEnabled()) {
            logger.info("Sent " + docList.size() + " docs (Solr: " + (System.currentTimeMillis() - execTime) + "ms)");
        }
        docList.clear();
    }

    private void deleteOldDocuments(final SearchClient searchClient, final List<Map<String, Object>> docList) {
        // TODO move searchClient
        //        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        //
        //        final List<String> docIdList = new ArrayList<String>();
        //        final StringBuilder q = new StringBuilder(1000);
        //        final StringBuilder fq = new StringBuilder(100);
        //        for (final Map<String, Object> inputDoc : docList) {
        //            final Object idValue = inputDoc.get(fieldHelper.idField);
        //            if (idValue == null) {
        //                continue;
        //            }
        //
        //            final Object configIdValue = inputDoc.get(fieldHelper.configIdField);
        //            if (configIdValue == null) {
        //                continue;
        //            }
        //
        //            q.setLength(0);
        //            q.append(fieldHelper.urlField).append(":\"");
        //            q.append(QueryUtil.escapeValue((String) inputDoc.get(fieldHelper.urlField)));
        //            q.append('"');
        //
        //            fq.setLength(0);
        //            fq.append(fieldHelper.configIdField).append(':');
        //            fq.append(configIdValue.toString());
        //
        //            final List<Map<String, Object>> docs =
        //                    getSolrDocumentList(searchClient, fq.toString(), q.toString(), new String[] { fieldHelper.idField,
        //                            fieldHelper.docIdField });
        //            for (final SolrDocument doc : docs) {
        //                final Object oldIdValue = doc.getFieldValue(fieldHelper.idField);
        //                if (!idValue.equals(oldIdValue) && oldIdValue != null) {
        //                    final Object oldDocIdValue = doc.getFieldValue(fieldHelper.docIdField);
        //                    if (oldDocIdValue != null) {
        //                        docIdList.add(oldDocIdValue.toString());
        //                    }
        //                }
        //            }
        //            if (logger.isDebugEnabled()) {
        //                logger.debug(q + " in " + fq + " => " + docs);
        //            }
        //        }
        //        if (!docIdList.isEmpty()) {
        //            deleteDocumentsByDocId(searchClient, docIdList);
        //        }
    }

    public List<Map<String, Object>> getSolrDocumentList(final SearchClient searchClient, final String fq, final String q,
            final String[] fields) {
        return getSolrDocumentList(searchClient, fq, q, fields, defaultRowSize);
    }

    protected List<Map<String, Object>> getSolrDocumentList(final SearchClient searchClient, final String fq, final String q,
            final String[] fields, final int row) {
        // TODO move searchClient
        //        final SolrQuery sq = new SolrQuery();
        //        if (fq != null) {
        //            sq.setFilterQueries(fq);
        //        }
        //        sq.setQuery(q);
        //        if (fields != null) {
        //            sq.setFields(fields);
        //        }
        //        sq.setRows(row);
        //        e.
        //        final List<Map<String,Object>> docList = searchClient.query(sq).getResults();
        //        if (docList.getNumFound() <= row) {
        //            return docList;
        //        }
        //        return getSolrDocumentList(searchClient, fq, q, fields, (int) docList.getNumFound());
        return null;
    }

    public void deleteDocument(final SearchClient searchClient, final String id) {
        // TODO move searchClient
        //        final long start = System.currentTimeMillis();
        //        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        //        final String query = "{!raw f=" + fieldHelper.idField + "}" + id;
        //        for (int i = 0; i < maxRetryCount; i++) {
        //            boolean done = true;
        //            try {
        //                for (final UpdateResponse response : searchClient.deleteByQuery(query)) {
        //                    if (response.getStatus() != 0) {
        //                        logger.warn("Failed to delete: " + response);
        //                        done = false;
        //                    }
        //                }
        //            } catch (final Exception e) {
        //                logger.warn("Could not delete a document from Solr." + " It might be busy. " + "Retrying.. id:" + id + ", cause: "
        //                        + e.getMessage());
        //                done = false;
        //            }
        //            if (done) {
        //                if (logger.isDebugEnabled()) {
        //                    logger.info("Deleted 1 doc (Solr: " + (System.currentTimeMillis() - start) + "ms) => id:" + id);
        //                } else {
        //                    logger.info("Deleted 1 doc (Solr: " + (System.currentTimeMillis() - start) + "ms)");
        //                }
        //                return;
        //            }
        //            try {
        //                Thread.sleep(requestInterval);
        //            } catch (final InterruptedException e) {}
        //        }
    }

    public void deleteDocumentsByDocId(final SearchClient searchClient, final List<String> docIdList) {
        // TODO move searchClient
        //        final long start = System.currentTimeMillis();
        //        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        //        final StringBuilder buf = new StringBuilder(500);
        //        for (final String docId : docIdList) {
        //            if (buf.length() != 0) {
        //                buf.append(" OR ");
        //            }
        //            buf.append(fieldHelper.docIdField).append(':').append(docId);
        //        }
        //        final String query = buf.toString();
        //        for (int i = 0; i < maxRetryCount; i++) {
        //            boolean done = true;
        //            try {
        //                for (final UpdateResponse response : searchClient.deleteByQuery(query)) {
        //                    if (response.getStatus() != 0) {
        //                        logger.warn("Failed to delete: " + response);
        //                        done = false;
        //                    }
        //                }
        //            } catch (final Exception e) {
        //                logger.warn("Could not delete a document from Solr." + " It might be busy. " + "Retrying.. id:" + docIdList + ", cause: "
        //                        + e.getMessage());
        //                done = false;
        //            }
        //            if (done) {
        //                if (logger.isDebugEnabled()) {
        //                    logger.info("Deleted " + docIdList.size() + " docs (Solr: " + (System.currentTimeMillis() - start) + "ms) => docId:"
        //                            + docIdList);
        //                } else {
        //                    logger.info("Deleted " + docIdList.size() + " docs (Solr: " + (System.currentTimeMillis() - start) + "ms)");
        //                }
        //                return;
        //            }
        //            try {
        //                Thread.sleep(requestInterval);
        //            } catch (final InterruptedException e) {}
        //        }
    }

    public Map<String, Object> getSolrDocument(final SearchClient searchClient, final String id, final String[] fields) {
        // TODO move searchClient
        //        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        //
        //        final SolrQuery solrQuery = new SolrQuery();
        //        final StringBuilder queryBuf = new StringBuilder(500);
        //        queryBuf.append("{!raw f=").append(fieldHelper.idField).append("}");
        //        queryBuf.append(id);
        //        solrQuery.setQuery(queryBuf.toString());
        //        if (fields != null) {
        //            solrQuery.setFields(fields);
        //        }
        //        final QueryResponse response = searchClient.query(solrQuery);
        //        final List<Map<String, Object>> docList = response.getResults();
        //        if (docList.isEmpty()) {
        //            return null;
        //        }
        //        if (docList.size() > 1) {
        //            logger.error("Invalid multiple docs for " + id);
        //            for (final SolrDocument doc : docList) {
        //                final Object idValue = doc.getFieldValue(fieldHelper.idField);
        //                if (idValue != null) {
        //                    deleteDocument(searchClient, idValue.toString());
        //                }
        //            }
        //            return null;
        //        }
        //        return docList.get(0);
        return null;
    }

    public List<Map<String, Object>> getSolrDocumentListByPrefixId(final SearchClient searchClient, final String id, final String[] fields) {
        // TODO move searchClient
        //        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        //        final SolrQuery solrQuery = new SolrQuery();
        //        final StringBuilder queryBuf = new StringBuilder(500);
        //        queryBuf.append("{!prefix f=").append(fieldHelper.idField).append("}");
        //        queryBuf.append(id);
        //        solrQuery.setQuery(queryBuf.toString());
        //        if (fields != null) {
        //            solrQuery.setFields(fields);
        //        }
        //        final QueryResponse response = searchClient.query(solrQuery);
        //        final List<Map<String, Object>> docList = response.getResults();
        //        if (docList.isEmpty()) {
        //            return null;
        //        }
        //        if (logger.isDebugEnabled()) {
        //            logger.debug("Found solr documents: " + docList);
        //        }
        //        return docList;
        return null;
    }

    public void deleteChildSolrDocument(final SearchClient searchClient, final String id) {
        // TODO move searchClient
        //        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        //        final String query = "{!raw f=" + fieldHelper.parentIdField + " v=\"" + id + "\"}";
        //        for (final UpdateResponse response : searchClient.deleteByQuery(query)) {
        //            if (response.getStatus() != 0) {
        //                logger.warn("Failed to delete: " + response);
        //            }
        //        }
    }

    public List<Map<String, Object>> getChildSolrDocumentList(final SearchClient searchClient, final String id, final String[] fields) {
        return getChildSolrDocumentList(searchClient, id, fields, defaultRowSize);
    }

    protected List<Map<String, Object>> getChildSolrDocumentList(final SearchClient searchClient, final String id, final String[] fields,
            final int row) {
        // TODO move searchClient
        //        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        //        final SolrQuery solrQuery = new SolrQuery();
        //        solrQuery.setQuery("{!raw f=" + fieldHelper.parentIdField + " v=\"" + id + "\"}");
        //        if (fields != null) {
        //            solrQuery.setFields(fields);
        //        }
        //        solrQuery.setRows(row);
        //        final List<Map<String,Object>> docList = searchClient.query(solrQuery).getResults();
        //        if (docList.getNumFound() <= row) {
        //            return docList;
        //        }
        //        return getChildSolrDocumentList(searchClient, id, fields, (int) docList.getNumFound());
        return null;
    }
}