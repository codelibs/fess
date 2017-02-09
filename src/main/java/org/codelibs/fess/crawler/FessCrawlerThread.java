/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.smb.SmbClient;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.log.LogType;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.SambaHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcifs.smb.ACE;
import jcifs.smb.SID;

public class FessCrawlerThread extends CrawlerThread {
    private static final Logger logger = LoggerFactory.getLogger(FessCrawlerThread.class);

    @Override
    protected boolean isContentUpdated(final CrawlerClient client, final UrlQueue<?> urlQueue) {
        if (ComponentUtil.getFessConfig().isIncrementalCrawling()) {

            final long startTime = System.currentTimeMillis();

            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
            final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();
            final SambaHelper sambaHelper = ComponentUtil.getSambaHelper();
            final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
            final FessEsClient fessEsClient = ComponentUtil.getFessEsClient();

            final String url = urlQueue.getUrl();
            ResponseData responseData = null;
            try {
                final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(crawlerContext.getSessionId());
                final Map<String, Object> dataMap = new HashMap<>();
                dataMap.put(fessConfig.getIndexFieldUrl(), url);
                final List<String> roleTypeList = new ArrayList<>();
                stream(crawlingConfig.getPermissions()).of(stream -> stream.forEach(p -> roleTypeList.add(p)));
                if (url.startsWith("smb://")) {
                    if (url.endsWith("/")) {
                        // directory
                        return true;
                    }
                    if (fessConfig.isSmbRoleFromFile()) {
                        // head method
                        responseData = client.execute(RequestDataBuilder.newRequestData().head().url(url).build());
                        if (responseData == null) {
                            return true;
                        }

                        final ACE[] aces = (ACE[]) responseData.getMetaDataMap().get(SmbClient.SMB_ACCESS_CONTROL_ENTRIES);
                        if (aces != null) {
                            for (final ACE item : aces) {
                                final SID sid = item.getSID();
                                final String accountId = sambaHelper.getAccountId(sid);
                                if (accountId != null) {
                                    roleTypeList.add(accountId);
                                }
                            }
                            if (logger.isDebugEnabled()) {
                                logger.debug("smbUrl:" + responseData.getUrl() + " roleType:" + roleTypeList.toString());
                            }
                        }
                    }
                }
                dataMap.put(fessConfig.getIndexFieldRole(), roleTypeList);
                final String id = crawlingInfoHelper.generateId(dataMap);

                if (logger.isDebugEnabled()) {
                    logger.debug("Searching indexed document: " + id);
                }
                final Map<String, Object> document =
                        indexingHelper.getDocument(
                                fessEsClient,
                                id,
                                new String[] { fessConfig.getIndexFieldId(), fessConfig.getIndexFieldLastModified(),
                                        fessConfig.getIndexFieldAnchor(), fessConfig.getIndexFieldSegment(),
                                        fessConfig.getIndexFieldExpires(), fessConfig.getIndexFieldClickCount(),
                                        fessConfig.getIndexFieldFavoriteCount() });
                if (document == null) {
                    storeChildUrlsToQueue(urlQueue, getChildUrlSet(fessEsClient, id));
                    return true;
                }

                final Date expires = DocumentUtil.getValue(document, fessConfig.getIndexFieldExpires(), Date.class);
                if (expires != null && expires.getTime() < System.currentTimeMillis()) {
                    final Object idValue = document.get(fessConfig.getIndexFieldId());
                    if (idValue != null) {
                        indexingHelper.deleteDocument(fessEsClient, idValue.toString());
                    }
                    return true;
                }

                final Date lastModified = DocumentUtil.getValue(document, fessConfig.getIndexFieldLastModified(), Date.class);
                if (lastModified == null) {
                    return true;
                }
                urlQueue.setLastModified(lastModified.getTime());
                log(logHelper, LogType.CHECK_LAST_MODIFIED, crawlerContext, urlQueue);

                if (responseData == null) {
                    // head method
                    responseData = client.execute(RequestDataBuilder.newRequestData().head().url(url).build());
                    if (responseData == null) {
                        return true;
                    }
                }

                final int httpStatusCode = responseData.getHttpStatusCode();
                if (logger.isDebugEnabled()) {
                    logger.debug("Accessing document: " + url + ", status: " + httpStatusCode);
                }
                if (httpStatusCode == 404) {
                    storeChildUrlsToQueue(urlQueue, getAnchorSet(document.get(fessConfig.getIndexFieldAnchor())));
                    indexingHelper.deleteDocument(fessEsClient, id);
                    return false;
                } else if (responseData.getLastModified() == null) {
                    return true;
                } else if (responseData.getLastModified().getTime() <= lastModified.getTime() && httpStatusCode == 200) {

                    log(logHelper, LogType.NOT_MODIFIED, crawlerContext, urlQueue);

                    responseData.setExecutionTime(System.currentTimeMillis() - startTime);
                    responseData.setParentUrl(urlQueue.getParentUrl());
                    responseData.setSessionId(crawlerContext.getSessionId());
                    responseData.setHttpStatusCode(org.codelibs.fess.crawler.Constants.NOT_MODIFIED_STATUS);
                    processResponse(urlQueue, responseData);

                    storeChildUrlsToQueue(urlQueue, getAnchorSet(document.get(fessConfig.getIndexFieldAnchor())));

                    return false;
                }
            } finally {
                if (responseData != null) {
                    IOUtils.closeQuietly(responseData);
                }
            }
        }
        return true;
    }

    protected void storeChildUrlsToQueue(final UrlQueue<?> urlQueue, final Set<RequestData> childUrlSet) {
        if (childUrlSet != null) {
            // add an url
            try {
                storeChildUrls(childUrlSet.stream().filter(rd -> StringUtil.isNotBlank(rd.getUrl())).collect(Collectors.toSet()),
                        urlQueue.getUrl(), urlQueue.getDepth() != null ? urlQueue.getDepth() + 1 : 1);
            } catch (final Throwable t) {
                if (!ComponentUtil.available()) {
                    throw new ContainerNotAvailableException(t);
                }
                throw t;
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Set<RequestData> getAnchorSet(final Object obj) {
        List<String> anchorList;
        if (obj instanceof String) {
            anchorList = new ArrayList<>();
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

    protected Set<RequestData> getChildUrlSet(final FessEsClient fessEsClient, final String id) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final IndexingHelper indexingHelper = ComponentUtil.getIndexingHelper();
        final List<Map<String, Object>> docList =
                indexingHelper.getChildDocumentList(fessEsClient, id, new String[] { fessConfig.getIndexFieldUrl() });
        if (docList.isEmpty()) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Found documents: " + docList);
        }
        final Set<RequestData> urlSet = new HashSet<>(docList.size());
        for (final Map<String, Object> doc : docList) {
            final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
            if (StringUtil.isNotBlank(url)) {
                urlSet.add(RequestDataBuilder.newRequestData().get().url(url).build());
            }
        }
        return urlSet;
    }
}
