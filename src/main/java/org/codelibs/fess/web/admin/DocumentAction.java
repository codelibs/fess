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

package org.codelibs.fess.web.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import jp.sf.fess.suggest.SuggestConstants;
import jp.sf.fess.suggest.server.SuggestSolrServer;
import jp.sf.fess.suggest.service.SuggestService;

import org.codelibs.core.util.DynamicProperties;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.client.SearchClient;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.WebManagementHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.taglib.S2Functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentAction implements Serializable {
    private static final String SUGGEST_TYPE_ALL = "all";

    private static final String SUGGEST_TYPE_SEARCH_LOG = "searchLog";

    private static final String SUGGEST_TYPE_CONTENT = "content";

    private static final Logger logger = LoggerFactory.getLogger(DocumentAction.class);

    private static final long serialVersionUID = 1L;

    @ActionForm
    @Resource
    protected DocumentForm documentForm;

    @Resource
    protected SearchClient searchClient;

    @Resource
    protected WebManagementHelper webManagementHelper;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected FieldHelper fieldHelper;

    @Resource
    protected JobHelper jobHelper;

    @Resource
    protected SuggestService suggestService;

    public Map<String, Long> suggestDocumentNums;

    public String getHelpLink() {
        return systemHelper.getHelpLink("document");
    }

    protected String showIndex(final boolean redirect) {
        // TODO

        if (redirect) {
            return "index?redirect=true";
        } else {
            return "index.jsp";
        }
    }

    @Token(save = true, validate = false)
    @Execute(validator = false)
    public String index() {
        return showIndex(false);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String commit() {
        // TODO change to flush
        if (jobHelper.isCrawlProcessRunning()) {
            throw new SSCActionMessagesException("errors.failed_to_start_solr_process_because_of_running");
        }

        searchClient.flush();
        SAStrutsUtil.addSessionMessage("success.commit_solr_index");
        return showIndex(true);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String optimize() {
        // TODO change to optimize
        if (jobHelper.isCrawlProcessRunning()) {
            throw new SSCActionMessagesException("errors.failed_to_start_solr_process_because_of_running");
        }
        searchClient.optimize();
        SAStrutsUtil.addSessionMessage("success.optimize_solr_index");
        return showIndex(true);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String delete() {
        QueryBuilder deleteQuery;
        if ("*".equals(documentForm.sessionId)) {
            deleteQuery = QueryBuilders.matchAllQuery();
        } else {
            deleteQuery = QueryBuilders.termQuery(fieldHelper.segmentField, documentForm.sessionId);
        }
        return deleteByQuery(deleteQuery);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String confirmByUrl() {
        final String confirmQuery = fieldHelper.urlField + ":\"" + documentForm.deleteUrl + "\"";
        return "/admin/searchList/search?query=" + S2Functions.u(confirmQuery) + "&redirect=true";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String deleteByUrl() {
        return deleteByQuery(QueryBuilders.termQuery(fieldHelper.urlField, documentForm.deleteUrl));
    }

    private String deleteByQuery(final QueryBuilder queryBuilder) {
        if (jobHelper.isCrawlProcessRunning()) {
            throw new SSCActionMessagesException("errors.failed_to_start_solr_process_because_of_running");
        }

        searchClient.deleteByQuery(queryBuilder);
        SAStrutsUtil.addSessionMessage("success.delete_solr_index");
        return showIndex(true);
    }

    protected SessionIdList<Map<String, String>> getSessionIdList(final String groupName) {
        final SessionIdList<Map<String, String>> sessionIdList = new SessionIdList<Map<String, String>>();

        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        TermsBuilder termsBuilder =
                AggregationBuilders.terms(fieldHelper.segmentField).field(fieldHelper.segmentField).size(100).order(Order.count(false));

        SearchResponse response = searchClient.query(queryBuilder, termsBuilder, null);
        Terms terms = response.getAggregations().get(fieldHelper.segmentField);
        for (Bucket bucket : terms.getBuckets()) {
            final Map<String, String> map = new HashMap<String, String>(3);
            map.put("label", bucket.getKey() + " (" + bucket.getDocCount() + ")");
            map.put("value", bucket.getKey());
            map.put("count", Long.toString(bucket.getDocCount()));
            sessionIdList.add(map);
            sessionIdList.addTotalCount(bucket.getDocCount());
        }

        return sessionIdList;
    }

    public boolean isSolrProcessRunning() {
        return jobHelper.isCrawlProcessRunning();
    }

    public Set<String> getRunningSessionIdSet() {
        return jobHelper.getRunningSessionIdSet();
    }

    protected Map<String, Long> getSuggestDocumentNum() {
        final Map<String, Long> map = new HashMap<String, Long>();
        map.put(SUGGEST_TYPE_CONTENT, suggestService.getContentDocumentNum());
        map.put(SUGGEST_TYPE_SEARCH_LOG, suggestService.getSearchLogDocumentNum());
        map.put(SUGGEST_TYPE_ALL, suggestService.getDocumentNum());
        return map;
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String deleteSuggest() {
        final SuggestSolrServer suggestSolrServer = suggestService.getSuggestSolrServer();
        final String query;
        if (SUGGEST_TYPE_CONTENT.equals(documentForm.deleteSuggestType)) {
            query =
                    "*:* NOT " + SuggestConstants.SuggestFieldNames.SEGMENT + ":" + SuggestConstants.SEGMENT_ELEVATE + " NOT "
                            + SuggestConstants.SuggestFieldNames.SEGMENT + ":" + SuggestConstants.SEGMENT_QUERY;
        } else if (SUGGEST_TYPE_SEARCH_LOG.equals(documentForm.deleteSuggestType)) {
            query = SuggestConstants.SuggestFieldNames.SEGMENT + ":" + SuggestConstants.SEGMENT_QUERY;
        } else {
            query = "";
        }

        if (StringUtil.isNotBlank(query)) {
            // TODO
            /*
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!jobHelper.isCrawlProcessRunning()) {
                        final long execTime = System.currentTimeMillis();
                        try {
                            suggestSolrServer.deleteByQuery(query);
                            suggestSolrServer.commit();
                            if (logger.isInfoEnabled()) {
                                logger.info("[EXEC TIME] suggest index cleanup time: " + (System.currentTimeMillis() - execTime) + "ms");
                            }
                        } catch (final Exception e) {
                            logger.error("Failed to delete suggest index (query=" + query + ").", e);
                        }
                    } else {
                        if (logger.isInfoEnabled()) {
                            logger.info("could not start index cleanup process" + " because of running solr process.");
                        }
                    }
                }
            });
            thread.start();
            SAStrutsUtil.addSessionMessage("success.delete_solr_index");
            */
        }
        return showIndex(true);
    }

    private static class SessionIdList<E> extends ArrayList<E> {

        private static final long serialVersionUID = 1L;

        private long totalCount = 0;

        public void addTotalCount(final long count) {
            totalCount += count;
        }

        public long getTotalCount() {
            return totalCount;
        }

    }
}