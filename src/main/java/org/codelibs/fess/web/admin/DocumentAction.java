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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.WebManagementHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.codelibs.solr.lib.SolrGroup;
import org.codelibs.solr.lib.SolrGroupManager;
import org.codelibs.solr.lib.SolrLibConstants;
import org.codelibs.solr.lib.policy.QueryType;
import org.codelibs.solr.lib.policy.impl.StatusPolicyImpl;
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
    protected DynamicProperties solrProperties;

    @Resource
    protected SolrGroupManager solrGroupManager;

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
        final Map<String, DynamicProperties> groupPropMap = new HashMap<String, DynamicProperties>();
        for (final String groupName : solrGroupManager.getSolrGroupNames()) {
            final DynamicProperties props = ComponentUtil.getSolrGroupProperties(groupName);
            if (props != null) {
                groupPropMap.put(groupName, props);
            }
        }

        final String[] serverNames = solrGroupManager.getSolrServerNames();
        for (final String name : serverNames) {
            final String[] names = name.split(":");
            if (names.length == 2) {
                final Map<String, String> map = new HashMap<String, String>(4);
                map.put("groupName", names[0]);
                map.put("serverName", names[1]);
                final DynamicProperties props = groupPropMap.get(names[0]);
                if (props != null) {
                    String status = props.getProperty(StatusPolicyImpl.STATUS_PREFIX + names[1]);
                    if (StringUtil.isBlank(status)) {
                        status = StatusPolicyImpl.ACTIVE;
                    }
                    map.put("status", status);
                    String index = props.getProperty(StatusPolicyImpl.INDEX_PREFIX + names[1]);
                    if (StringUtil.isBlank(index)) {
                        index = StatusPolicyImpl.READY;
                    }
                    map.put("index", index);
                } else {
                    map.put("status", StatusPolicyImpl.ACTIVE);
                    map.put("index", StatusPolicyImpl.READY);
                }
                documentForm.serverStatusList.add(map);
            }
        }

        suggestDocumentNums = getSuggestDocumentNum();

        // select group status
        documentForm.currentServerForSelect = solrProperties.getProperty(SolrLibConstants.SELECT_GROUP);
        final SolrGroup selectSolrGroup = solrGroupManager.getSolrGroup(documentForm.currentServerForSelect);
        if (selectSolrGroup != null && selectSolrGroup.isActive(QueryType.QUERY)) {
            documentForm.currentServerStatusForSelect = Constants.ACTIVE;
        } else {
            documentForm.currentServerStatusForSelect = Constants.INACTIVE;
        }

        // update group status
        documentForm.currentServerForUpdate = solrProperties.getProperty(SolrLibConstants.SELECT_GROUP);
        final SolrGroup updateSolrGroup = solrGroupManager.getSolrGroup(documentForm.currentServerForUpdate);
        if (updateSolrGroup != null && updateSolrGroup.isActive(QueryType.QUERY)) {
            documentForm.currentServerStatusForUpdate = Constants.ACTIVE;
        } else {
            documentForm.currentServerStatusForUpdate = Constants.INACTIVE;
        }

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
        if (jobHelper.isCrawlProcessRunning()) {
            throw new SSCActionMessagesException("errors.failed_to_start_solr_process_because_of_running");
        }
        final SolrGroup solrGroup = solrGroupManager.getSolrGroup(documentForm.groupName);
        if (solrGroup == null) {
            throw new SSCActionMessagesException("errors.failed_to_commit_solr_index");
        } else {
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!jobHelper.isCrawlProcessRunning()) {
                        final long execTime = System.currentTimeMillis();
                        try {
                            systemHelper.updateStatus(solrGroup, QueryType.ADD);
                            solrGroup.commit(true, true, false, true);
                            systemHelper.updateStatus(solrGroup, QueryType.COMMIT);
                            if (logger.isInfoEnabled()) {
                                logger.info("[EXEC TIME] index commit time: " + (System.currentTimeMillis() - execTime) + "ms");
                            }
                        } catch (final Exception e) {
                            logger.error("Failed to commit index.", e);
                        }
                    } else {
                        if (logger.isInfoEnabled()) {
                            logger.info("could not start index cleanup process" + " because of running solr process.");
                        }
                    }
                }
            });
            thread.start();
            SAStrutsUtil.addSessionMessage("success.commit_solr_index");
        }
        return showIndex(true);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String optimize() {
        if (jobHelper.isCrawlProcessRunning()) {
            throw new SSCActionMessagesException("errors.failed_to_start_solr_process_because_of_running");
        }
        final SolrGroup solrGroup = solrGroupManager.getSolrGroup(documentForm.groupName);
        if (solrGroup == null) {
            throw new SSCActionMessagesException("errors.failed_to_optimize_solr_index");
        } else {
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!jobHelper.isCrawlProcessRunning()) {
                        final long execTime = System.currentTimeMillis();
                        try {
                            systemHelper.updateStatus(solrGroup, QueryType.ADD);
                            solrGroup.optimize();
                            systemHelper.updateStatus(solrGroup, QueryType.OPTIMIZE);
                            if (logger.isInfoEnabled()) {
                                logger.info("[EXEC TIME] index optimize time: " + (System.currentTimeMillis() - execTime) + "ms");
                            }
                        } catch (final Exception e) {
                            logger.error("Failed to optimize index.", e);
                        }
                    } else {
                        if (logger.isInfoEnabled()) {
                            logger.info("could not start index cleanup process" + " because of running solr process.");
                        }
                    }
                }
            });
            thread.start();
            SAStrutsUtil.addSessionMessage("success.optimize_solr_index");
        }
        return showIndex(true);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String delete() {
        String deleteQuery;
        if ("*".equals(documentForm.sessionId)) {
            deleteQuery = "*:*";
        } else {
            deleteQuery = fieldHelper.segmentField + ":" + documentForm.sessionId;
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
        final String deleteUrl = documentForm.deleteUrl;
        final String deleteQuery = fieldHelper.urlField + ":\"" + deleteUrl + "\"";
        return deleteByQuery(deleteQuery);
    }

    private String deleteByQuery(final String deleteQuery) {
        if (jobHelper.isCrawlProcessRunning()) {
            throw new SSCActionMessagesException("errors.failed_to_start_solr_process_because_of_running");
        }
        final SolrGroup solrGroup = solrGroupManager.getSolrGroup(documentForm.groupName);
        if (solrGroup == null) {
            throw new SSCActionMessagesException("errors.failed_to_delete_solr_index");
        } else {
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!jobHelper.isCrawlProcessRunning()) {
                        final long execTime = System.currentTimeMillis();
                        try {
                            systemHelper.updateStatus(solrGroup, QueryType.DELETE);
                            solrGroup.deleteByQuery(deleteQuery);
                            solrGroup.commit(true, true, false, true);
                            systemHelper.updateStatus(solrGroup, QueryType.OPTIMIZE);
                            if (logger.isInfoEnabled()) {
                                logger.info("[EXEC TIME] index cleanup time: " + (System.currentTimeMillis() - execTime) + "ms");
                            }
                        } catch (final Exception e) {
                            logger.error("Failed to delete index (query=" + deleteQuery + ").", e);
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
        }
        return showIndex(true);
    }

    public List<Map<String, Object>> getGroupActionItems() {
        final List<Map<String, Object>> groupActionItems = new ArrayList<Map<String, Object>>();
        for (final String groupName : solrGroupManager.getSolrGroupNames()) {
            try {
                final Map<String, Object> map = new HashMap<String, Object>();
                map.put("groupName", groupName);
                final SessionIdList<Map<String, String>> sessionIdList = getSessionIdList(groupName);
                map.put("sessionIdItems", sessionIdList);
                map.put("totalCount", sessionIdList.getTotalCount());
                groupActionItems.add(map);
            } catch (final Exception e) {
                logger.info("could not get server groups.", e);
            }
        }
        return groupActionItems;
    }

    protected SessionIdList<Map<String, String>> getSessionIdList(final String groupName) {
        final SessionIdList<Map<String, String>> sessionIdList = new SessionIdList<Map<String, String>>();

        SolrGroup serverGroup;
        try {
            serverGroup = solrGroupManager.getSolrGroup(groupName);
        } catch (final Exception e) {
            if (logger.isInfoEnabled()) {
                logger.info(e.getMessage());
            }
            return sessionIdList;
        }

        final SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setFacet(true);
        query.addFacetField(fieldHelper.segmentField);
        query.addSort(fieldHelper.segmentField, ORDER.desc);

        final QueryResponse queryResponse = serverGroup.query(query);
        final List<FacetField> facets = queryResponse.getFacetFields();
        for (final FacetField facet : facets) {
            final List<FacetField.Count> facetEntries = facet.getValues();
            if (facetEntries != null) {
                for (final FacetField.Count fcount : facetEntries) {
                    final Map<String, String> map = new HashMap<String, String>(3);
                    map.put("label", fcount.getName() + " (" + fcount.getCount() + ")");
                    map.put("value", fcount.getName());
                    map.put("count", Long.toString(fcount.getCount()));
                    sessionIdList.add(map);
                    sessionIdList.addTotalCount(fcount.getCount());
                }
            }
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