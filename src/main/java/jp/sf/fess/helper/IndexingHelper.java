/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.helper;

import java.util.ArrayList;
import java.util.List;

import jp.sf.fess.util.ComponentUtil;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.codelibs.solr.lib.SolrGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexingHelper {
    private static final Logger logger = LoggerFactory
            .getLogger(IndexingHelper.class);

    public int maxRetryCount = 5;

    public int defaultRowSize = 100;

    public long requestInterval = 500;

    public void sendDocuments(final SolrGroup solrGroup,
            final List<SolrInputDocument> docList) {
        final long execTime = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("Sending " + docList.size()
                    + " documents to a server.");
        }
        synchronized (solrGroup) {
            deleteOldDocuments(solrGroup, docList);
            solrGroup.add(docList);
        }
        if (logger.isInfoEnabled()) {
            logger.info("Sent " + docList.size() + " docs (Solr: "
                    + (System.currentTimeMillis() - execTime) + "ms)");
        }
        docList.clear();
    }

    private void deleteOldDocuments(final SolrGroup solrGroup,
            final List<SolrInputDocument> docList) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();

        final List<String> ids = new ArrayList<String>();
        final StringBuilder q = new StringBuilder(1000);
        final StringBuilder fq = new StringBuilder(100);
        for (final SolrInputDocument inputDoc : docList) {
            final Object idValue = inputDoc.getFieldValue(fieldHelper.idField);
            if (idValue == null) {
                continue;
            }

            final Object configIdValue = inputDoc
                    .getFieldValue(fieldHelper.configIdField);
            if (configIdValue == null) {
                continue;
            }

            q.setLength(0);
            q.append(fieldHelper.urlField).append(":\"");
            q.append(ClientUtils.escapeQueryChars((String) inputDoc
                    .getFieldValue(fieldHelper.urlField)));
            q.append('"');

            fq.setLength(0);
            fq.append(fieldHelper.configIdField).append(':');
            fq.append(configIdValue.toString());

            final SolrDocumentList docs = getSolrDocumentList(solrGroup,
                    fq.toString(), q.toString(),
                    new String[] { fieldHelper.idField });
            for (final SolrDocument doc : docs) {
                final Object oldIdValue = doc
                        .getFieldValue(fieldHelper.idField);
                if (!idValue.equals(oldIdValue) && oldIdValue != null) {
                    ids.add(oldIdValue.toString());
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug(q + " in " + fq + " => " + docs);
            }
        }
        if (!ids.isEmpty()) {
            for (final String id : ids) {
                deleteDocument(solrGroup, id);
            }
        }
    }

    public SolrDocumentList getSolrDocumentList(final SolrGroup solrGroup,
            final String fq, final String q, final String[] fields) {
        return getSolrDocumentList(solrGroup, fq, q, fields, defaultRowSize);
    }

    protected SolrDocumentList getSolrDocumentList(final SolrGroup solrGroup,
            final String fq, final String q, final String[] fields,
            final int row) {
        final SolrQuery sq = new SolrQuery();
        if (fq != null) {
            sq.setFilterQueries(fq);
        }
        sq.setQuery(q);
        if (fields != null) {
            sq.setFields(fields);
        }
        sq.setRows(row);
        final SolrDocumentList docList = solrGroup.query(sq).getResults();
        if (docList.getNumFound() < row) {
            return docList;
        }
        return getSolrDocumentList(solrGroup, fq, q, fields,
                (int) docList.getNumFound());
    }

    public void deleteDocument(final SolrGroup solrGroup, final String id) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final String query = "{!raw f=" + fieldHelper.idField + "}" + id;
        for (int i = 0; i < maxRetryCount; i++) {
            boolean done = true;
            try {
                for (final UpdateResponse response : solrGroup
                        .deleteByQuery(query)) {
                    if (response.getStatus() != 200) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Failed to delete: " + response);
                        }
                        done = false;
                    }
                }
            } catch (final Exception e) {
                logger.warn("Could not delete a document from Solr."
                        + " It might be busy. " + "Retrying.. id:" + id
                        + ", cause: " + e.getMessage());
                done = false;
            }
            if (done) {
                logger.info("Deleted from Solr: " + id);
                break;
            }
            try {
                Thread.sleep(requestInterval);
            } catch (final InterruptedException e) {
            }
        }
    }

    public SolrDocument getSolrDocument(final SolrGroup solrGroup,
            final String id, final String[] fields) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();

        final SolrQuery solrQuery = new SolrQuery();
        final StringBuilder queryBuf = new StringBuilder(200);
        queryBuf.append("{!raw f=").append(fieldHelper.idField).append("}");
        queryBuf.append(id);
        solrQuery.setQuery(queryBuf.toString());
        if (fields != null) {
            solrQuery.setFields(fields);
        }
        final QueryResponse response = solrGroup.query(solrQuery);
        final SolrDocumentList docList = response.getResults();
        if (docList.isEmpty()) {
            return null;
        }
        if (docList.size() > 1) {
            logger.error("Invalid multiple docs for " + id);
            for (final SolrDocument doc : docList) {
                final Object idValue = doc.getFieldValue(fieldHelper.idField);
                if (idValue != null) {
                    deleteDocument(solrGroup, idValue.toString());
                }
            }
            return null;
        }
        return docList.get(0);
    }

    public SolrDocumentList getSolrDocumentListByPrefixId(
            final SolrGroup solrGroup, final String id, final String[] fields) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final SolrQuery solrQuery = new SolrQuery();
        final StringBuilder queryBuf = new StringBuilder(200);
        queryBuf.append("{!prefix f=").append(fieldHelper.idField).append("}");
        queryBuf.append(id);
        solrQuery.setQuery(queryBuf.toString());
        if (fields != null) {
            solrQuery.setFields(fields);
        }
        final QueryResponse response = solrGroup.query(solrQuery);
        final SolrDocumentList docList = response.getResults();
        if (docList.isEmpty()) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Found solr documents: " + docList);
        }
        return docList;
    }

    public void deleteChildSolrDocument(final SolrGroup solrGroup,
            final String id) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final String query = "{!raw f=" + fieldHelper.parentIdField + " v=\""
                + id + "\"}";
        for (final UpdateResponse response : solrGroup.deleteByQuery(query)) {
            if (response.getStatus() != 200) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to delete: " + response);
                }
            }
        }
    }

    public SolrDocumentList getChildSolrDocumentList(final SolrGroup solrGroup,
            final String id, final String[] fields) {
        return getChildSolrDocumentList(solrGroup, id, fields, defaultRowSize);
    }

    protected SolrDocumentList getChildSolrDocumentList(
            final SolrGroup solrGroup, final String id, final String[] fields,
            final int row) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("{!raw f=" + fieldHelper.parentIdField + " v=\""
                + id + "\"}");
        if (fields != null) {
            solrQuery.setFields(fields);
        }
        solrQuery.setRows(row);
        final SolrDocumentList docList = solrGroup.query(solrQuery)
                .getResults();
        if (docList.getNumFound() < row) {
            return docList;
        }
        return getChildSolrDocumentList(solrGroup, id, fields,
                (int) docList.getNumFound());
    }
}