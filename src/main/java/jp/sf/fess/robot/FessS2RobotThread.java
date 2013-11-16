/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.robot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jcifs.smb.ACE;
import jcifs.smb.SID;
import jp.sf.fess.Constants;
import jp.sf.fess.db.exentity.CrawlingConfig;
import jp.sf.fess.helper.CrawlingConfigHelper;
import jp.sf.fess.helper.CrawlingSessionHelper;
import jp.sf.fess.helper.SambaHelper;
import jp.sf.fess.helper.SearchLogHelper;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.solr.lib.SolrGroup;
import org.codelibs.solr.lib.SolrGroupManager;
import org.codelibs.solr.lib.policy.QueryType;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.robot.S2RobotThread;
import org.seasar.robot.client.S2RobotClient;
import org.seasar.robot.client.smb.SmbClient;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.UrlQueue;
import org.seasar.robot.log.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FessS2RobotThread extends S2RobotThread {
    private static final Logger logger = LoggerFactory
            .getLogger(FessS2RobotThread.class);

    public int maxSolrQueryRetryCount = 5;

    public int childUrlSize = 10000;

    public String clickCountField = "clickCount_i";

    public String favoriteCountField = "favoriteCount_i";

    @Override
    protected boolean isContentUpdated(final S2RobotClient client,
            final UrlQueue urlQueue) {
        final DynamicProperties crawlerProperties = SingletonS2Container
                .getComponent("crawlerProperties");
        if (crawlerProperties.getProperty(Constants.DIFF_CRAWLING_PROPERTY,
                Constants.TRUE).equals(Constants.TRUE)) {

            log(logHelper, LogType.CHECK_LAST_MODIFIED, robotContext, urlQueue);
            final long startTime = System.currentTimeMillis();

            final CrawlingConfigHelper crawlingConfigHelper = SingletonS2Container
                    .getComponent(CrawlingConfigHelper.class);
            final CrawlingSessionHelper crawlingSessionHelper = SingletonS2Container
                    .getComponent(CrawlingSessionHelper.class);
            final SambaHelper sambaHelper = SingletonS2Container
                    .getComponent(SambaHelper.class);
            final boolean useAclAsRole = crawlerProperties.getProperty(
                    Constants.USE_ACL_AS_ROLE, Constants.FALSE).equals(
                    Constants.TRUE);
            final String expiresField = crawlingSessionHelper.getExpiresField();

            ResponseData responseData = null;
            try {
                //  head method
                responseData = client.doHead(urlQueue.getUrl());
                if (responseData == null) {
                    return true;
                }

                SolrDocumentList oldDocWithRoleList = null;
                final CrawlingConfig crawlingConfig = crawlingConfigHelper
                        .get(robotContext.getSessionId());
                final Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap.put("url", urlQueue.getUrl());
                final List<String> browserTypeList = new ArrayList<String>();
                for (final String browserType : crawlingConfig
                        .getBrowserTypeValues()) {
                    browserTypeList.add(browserType);
                }
                dataMap.put("type", browserTypeList);
                final List<String> roleTypeList = new ArrayList<String>();
                for (final String roleType : crawlingConfig.getRoleTypeValues()) {
                    roleTypeList.add(roleType);
                }
                if (useAclAsRole && responseData.getUrl().startsWith("smb://")) {
                    final String id = crawlingSessionHelper.generateId(dataMap);
                    oldDocWithRoleList = getSolrDocumentList(id, true,
                            expiresField);

                    final ACE[] aces = (ACE[]) responseData.getMetaDataMap()
                            .get(SmbClient.SMB_ACCESS_CONTROL_ENTRIES);
                    if (aces != null) {
                        for (final ACE item : aces) {
                            final SID sid = item.getSID();
                            roleTypeList.add(sambaHelper.getAccountId(sid));
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("smbUrl:" + responseData.getUrl()
                                    + " roleType:" + roleTypeList.toString());
                        }
                    }
                }
                dataMap.put("role", roleTypeList);
                final String id = crawlingSessionHelper.generateId(dataMap);

                final SolrDocumentList solrDocumentList = getSolrDocumentList(
                        id, false, expiresField);
                if (solrDocumentList == null) {
                    deleteSolrDocumentList(oldDocWithRoleList);
                    storeChildUrlsToQueue(urlQueue, getChildUrlSet(id));
                    return true;
                }

                if (solrDocumentList.size() > 1) {
                    // invalid state
                    deleteSolrDocumentList(oldDocWithRoleList);
                    deleteSolrDocumentList(solrDocumentList);
                    return true;
                }

                final SolrDocument solrDocument = solrDocumentList.get(0);
                final Date expires = (Date) solrDocument.get(expiresField);
                if (expires != null
                        && expires.getTime() < System.currentTimeMillis()) {
                    deleteSolrDocumentList(oldDocWithRoleList);
                    return true;
                }

                final Date lastModified = (Date) solrDocument
                        .get("lastModified");
                if (lastModified == null) {
                    deleteSolrDocumentList(oldDocWithRoleList);
                    return true;
                }

                final Integer clickCount = (Integer) solrDocument
                        .get(clickCountField);
                if (clickCount != null) {
                    final SearchLogHelper searchLogHelper = SingletonS2Container
                            .getComponent(SearchLogHelper.class);
                    final int count = searchLogHelper.getClickCount(urlQueue
                            .getUrl());
                    if (count != clickCount.intValue()) {
                        deleteSolrDocumentList(oldDocWithRoleList);
                        return true;
                    }
                }

                final Integer favoriteCount = (Integer) solrDocument
                        .get(favoriteCountField);
                if (favoriteCount != null) {
                    final SearchLogHelper searchLogHelper = SingletonS2Container
                            .getComponent(SearchLogHelper.class);
                    final long count = searchLogHelper
                            .getFavoriteCount(urlQueue.getUrl());
                    if (count != favoriteCount.longValue()) {
                        deleteSolrDocumentList(oldDocWithRoleList);
                        return true;
                    }
                }

                final int httpStatusCode = responseData.getHttpStatusCode();
                if (httpStatusCode == 404) {
                    deleteSolrDocument(id);
                    deleteSolrDocumentList(oldDocWithRoleList);
                    storeChildUrlsToQueue(urlQueue,
                            getAnchorSet(solrDocument.get("anchor")));
                    return false;
                } else if (responseData.getLastModified() == null) {
                    deleteSolrDocumentList(oldDocWithRoleList);
                    return true;
                } else if (responseData.getLastModified().getTime() <= lastModified
                        .getTime() && httpStatusCode == 200) {

                    log(logHelper, LogType.NOT_MODIFIED, robotContext, urlQueue);

                    responseData.setExecutionTime(System.currentTimeMillis()
                            - startTime);
                    responseData.setParentUrl(urlQueue.getParentUrl());
                    responseData.setSessionId(robotContext.getSessionId());
                    responseData
                            .setStatus(org.seasar.robot.Constants.NOT_MODIFIED_STATUS);
                    processResponse(urlQueue, responseData);

                    storeChildUrlsToQueue(urlQueue,
                            getAnchorSet(solrDocument.get("anchor")));

                    return false;
                }
            } finally {
                if (responseData != null) {
                    IOUtils.closeQuietly(responseData.getResponseBody());
                }
            }
        }
        return true;
    }

    protected void storeChildUrlsToQueue(final UrlQueue urlQueue,
            final Set<String> childUrlSet) {
        if (childUrlSet != null) {
            synchronized (robotContext.getAccessCountLock()) {
                //  add an url
                storeChildUrls(childUrlSet, urlQueue.getUrl(),
                        urlQueue.getDepth() != null ? urlQueue.getDepth() + 1
                                : 1);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Set<String> getAnchorSet(final Object obj) {
        List<String> anchorList;
        if (obj instanceof String) {
            anchorList = new ArrayList<String>();
            anchorList.add(obj.toString());
        } else if (obj instanceof List<?>) {
            anchorList = (List<String>) obj;
        } else {
            return null;
        }

        if (anchorList.isEmpty()) {
            return null;
        }

        final Set<String> childUrlSet = new LinkedHashSet<String>();
        for (final String anchor : anchorList) {
            childUrlSet.add(anchor);
        }
        return childUrlSet;
    }

    protected SolrDocumentList getSolrDocumentList(final String id,
            final boolean wildcard, final String expiresField) {
        final SolrGroupManager solrGroupManager = SingletonS2Container
                .getComponent(SolrGroupManager.class);
        final SolrGroup solrGroup = solrGroupManager
                .getSolrGroup(QueryType.ADD);
        final SolrQuery solrQuery = new SolrQuery();
        final StringBuilder queryBuf = new StringBuilder(200);
        if (wildcard) {
            queryBuf.append("{!prefix f=id}");
        } else {
            queryBuf.append("{!raw f=id}");
        }
        queryBuf.append(id);
        solrQuery.setQuery(queryBuf.toString());
        solrQuery.setFields("id", "lastModified", "anchor", "segment", "role",
                expiresField, clickCountField, favoriteCountField);
        for (int i = 0; i < maxSolrQueryRetryCount; i++) {
            try {
                final QueryResponse response = solrGroup.query(solrQuery);
                final SolrDocumentList docList = response.getResults();
                if (docList.isEmpty()) {
                    return null;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Found solr documents: " + docList);
                }
                return docList;
            } catch (final Exception e) {
                logger.info("Could not get a response from Solr."
                        + " It might be busy. " + "Retrying.. id:" + id
                        + ", cause: " + e.getMessage());
            }
            try {
                Thread.sleep(500);
            } catch (final InterruptedException e) {
            }
        }
        return null;
    }

    protected Set<String> getChildUrlSet(final String id) {
        final SolrGroupManager solrGroupManager = SingletonS2Container
                .getComponent(SolrGroupManager.class);
        final SolrGroup solrGroup = solrGroupManager
                .getSolrGroup(QueryType.ADD);
        final SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("{!raw f=parentId v=\"" + id + "\"}");
        solrQuery.setFields("url");
        solrQuery.setRows(childUrlSize);
        for (int i = 0; i < maxSolrQueryRetryCount; i++) {
            try {
                final QueryResponse response = solrGroup.query(solrQuery);
                final SolrDocumentList docList = response.getResults();
                if (docList.isEmpty()) {
                    return null;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Found solr documents: " + docList);
                }
                final Set<String> urlSet = new HashSet<String>(docList.size());
                for (final SolrDocument doc : docList) {
                    final Object obj = doc.get("url");
                    if (obj != null) {
                        urlSet.add(obj.toString());
                    }
                }
                return urlSet;
            } catch (final Exception e) {
                logger.info("Could not get a response from Solr."
                        + " It might be busy. " + "Retrying.. id:" + id
                        + ", cause: " + e.getMessage());
            }
            try {
                Thread.sleep(500);
            } catch (final InterruptedException e) {
            }
        }
        return null;
    }

    protected void deleteSolrDocument(final String id) {
        final SolrGroupManager solrGroupManager = SingletonS2Container
                .getComponent(SolrGroupManager.class);
        final SolrGroup solrGroup = solrGroupManager
                .getSolrGroup(QueryType.DELETE);
        final String query = "{!raw f=parentId v=\"" + id + "\"}";
        for (int i = 0; i < maxSolrQueryRetryCount; i++) {
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
                logger.info("Could not delete a document from Solr."
                        + " It might be busy. " + "Retrying.. id:" + id
                        + ", cause: " + e.getMessage());
                done = false;
            }
            if (done) {
                logger.info("Deleted from Solr: " + id);
                break;
            }
            try {
                Thread.sleep(500);
            } catch (final InterruptedException e) {
            }
        }
    }

    protected void deleteSolrDocumentList(
            final SolrDocumentList solrDocumentList) {
        if (solrDocumentList != null) {
            for (final SolrDocument solrDocument : solrDocumentList) {
                final Object idObj = solrDocument.get("id");
                if (idObj != null) {
                    deleteSolrDocument(idObj.toString());
                }
            }
        }
    }
}
