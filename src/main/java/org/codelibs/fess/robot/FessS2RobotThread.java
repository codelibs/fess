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

package org.codelibs.fess.robot;

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

import org.apache.commons.io.IOUtils;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.client.SearchClient;
import org.codelibs.fess.db.exentity.CrawlingConfig;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingSessionHelper;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.SambaHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.robot.S2RobotThread;
import org.codelibs.robot.builder.RequestDataBuilder;
import org.codelibs.robot.client.S2RobotClient;
import org.codelibs.robot.client.smb.SmbClient;
import org.codelibs.robot.entity.RequestData;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.entity.UrlQueue;
import org.codelibs.robot.log.LogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FessS2RobotThread extends S2RobotThread {
    private static final Logger logger = LoggerFactory.getLogger(FessS2RobotThread.class);

    @Override
    protected boolean isContentUpdated(final S2RobotClient client, final UrlQueue urlQueue) {
        final DynamicProperties crawlerProperties = ComponentUtil.getCrawlerProperties();
        if (crawlerProperties.getProperty(Constants.DIFF_CRAWLING_PROPERTY, Constants.TRUE).equals(Constants.TRUE)) {

            log(logHelper, LogType.CHECK_LAST_MODIFIED, robotContext, urlQueue);
            final long startTime = System.currentTimeMillis();

            final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
            final CrawlingSessionHelper crawlingSessionHelper = ComponentUtil.getCrawlingSessionHelper();
            final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
            final SambaHelper sambaHelper = ComponentUtil.getSambaHelper();
            final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
            final SearchClient searchClient = ComponentUtil.getElasticsearchClient();
            final boolean useAclAsRole = crawlerProperties.getProperty(Constants.USE_ACL_AS_ROLE, Constants.FALSE).equals(Constants.TRUE);

            final String url = urlQueue.getUrl();
            ResponseData responseData = null;
            try {
                final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(robotContext.getSessionId());
                final Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap.put(fieldHelper.urlField, url);
                final List<String> roleTypeList = new ArrayList<String>();
                for (final String roleType : crawlingConfig.getRoleTypeValues()) {
                    roleTypeList.add(roleType);
                }
                if (useAclAsRole && url.startsWith("smb://")) {
                    //  head method
                    responseData = client.execute(RequestDataBuilder.newRequestData().head().url(url).build());
                    if (responseData == null) {
                        return true;
                    }

                    final ACE[] aces = (ACE[]) responseData.getMetaDataMap().get(SmbClient.SMB_ACCESS_CONTROL_ENTRIES);
                    if (aces != null) {
                        for (final ACE item : aces) {
                            final SID sid = item.getSID();
                            roleTypeList.add(sambaHelper.getAccountId(sid));
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("smbUrl:" + responseData.getUrl() + " roleType:" + roleTypeList.toString());
                        }
                    }
                }
                dataMap.put(fieldHelper.roleField, roleTypeList);
                final String id = crawlingSessionHelper.generateId(dataMap);

                final Map<String, Object> solrDocument =
                        indexingHelper.getSolrDocument(searchClient, id, new String[] { fieldHelper.idField, fieldHelper.lastModifiedField,
                                fieldHelper.anchorField, fieldHelper.segmentField, fieldHelper.expiresField, fieldHelper.clickCountField,
                                fieldHelper.favoriteCountField });
                if (solrDocument == null) {
                    storeChildUrlsToQueue(urlQueue, getChildUrlSet(searchClient, id));
                    return true;
                }

                final Date expires = (Date) solrDocument.get(fieldHelper.expiresField);
                if (expires != null && expires.getTime() < System.currentTimeMillis()) {
                    final Object idValue = solrDocument.get(fieldHelper.idField);
                    if (idValue != null) {
                        indexingHelper.deleteDocument(searchClient, idValue.toString());
                    }
                    return true;
                }

                final Date lastModified = (Date) solrDocument.get(fieldHelper.lastModifiedField);
                if (lastModified == null) {
                    return true;
                }

                final Integer clickCount = (Integer) solrDocument.get(fieldHelper.clickCountField);
                if (clickCount != null) {
                    final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();
                    final int count = searchLogHelper.getClickCount(url);
                    if (count != clickCount.intValue()) {
                        return true;
                    }
                }

                final Integer favoriteCount = (Integer) solrDocument.get(fieldHelper.favoriteCountField);
                if (favoriteCount != null) {
                    final SearchLogHelper searchLogHelper = ComponentUtil.getSearchLogHelper();
                    final long count = searchLogHelper.getFavoriteCount(url);
                    if (count != favoriteCount.longValue()) {
                        return true;
                    }
                }

                if (responseData == null) {
                    //  head method
                    responseData = client.execute(RequestDataBuilder.newRequestData().head().url(url).build());
                    if (responseData == null) {
                        return true;
                    }
                }

                final int httpStatusCode = responseData.getHttpStatusCode();
                if (httpStatusCode == 404) {
                    storeChildUrlsToQueue(urlQueue, getAnchorSet(solrDocument.get(fieldHelper.anchorField)));
                    indexingHelper.deleteDocument(searchClient, id);
                    return false;
                } else if (responseData.getLastModified() == null) {
                    return true;
                } else if (responseData.getLastModified().getTime() <= lastModified.getTime() && httpStatusCode == 200) {

                    log(logHelper, LogType.NOT_MODIFIED, robotContext, urlQueue);

                    responseData.setExecutionTime(System.currentTimeMillis() - startTime);
                    responseData.setParentUrl(urlQueue.getParentUrl());
                    responseData.setSessionId(robotContext.getSessionId());
                    responseData.setHttpStatusCode(org.codelibs.robot.Constants.NOT_MODIFIED_STATUS);
                    processResponse(urlQueue, responseData);

                    storeChildUrlsToQueue(urlQueue, getAnchorSet(solrDocument.get(fieldHelper.anchorField)));

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

    protected void storeChildUrlsToQueue(final UrlQueue urlQueue, final Set<RequestData> childUrlSet) {
        if (childUrlSet != null) {
            synchronized (robotContext.getAccessCountLock()) {
                //  add an url
                storeChildUrls(childUrlSet, urlQueue.getUrl(), urlQueue.getDepth() != null ? urlQueue.getDepth() + 1 : 1);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Set<RequestData> getAnchorSet(final Object obj) {
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

        final Set<RequestData> childUrlSet = new LinkedHashSet<>();
        for (final String anchor : anchorList) {
            childUrlSet.add(RequestDataBuilder.newRequestData().get().url(anchor).build());
        }
        return childUrlSet;
    }

    protected Set<RequestData> getChildUrlSet(final SearchClient searchClient, final String id) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
        final List<Map<String, Object>> docList =
                indexingHelper.getChildSolrDocumentList(searchClient, id, new String[] { fieldHelper.urlField });
        if (docList.isEmpty()) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Found solr documents: " + docList);
        }
        final Set<RequestData> urlSet = new HashSet<>(docList.size());
        for (final Map<String, Object> doc : docList) {
            final Object obj = doc.get(fieldHelper.urlField);
            if (obj != null) {
                urlSet.add(RequestDataBuilder.newRequestData().get().url(obj.toString()).build());
            }
        }
        return urlSet;
    }

}
