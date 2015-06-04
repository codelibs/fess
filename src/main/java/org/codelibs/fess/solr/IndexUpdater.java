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

package org.codelibs.fess.solr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.client.SearchClient;
import org.codelibs.fess.db.exbhv.ClickLogBhv;
import org.codelibs.fess.db.exbhv.FavoriteLogBhv;
import org.codelibs.fess.db.exbhv.pmbean.FavoriteUrlCountPmb;
import org.codelibs.fess.db.exentity.customize.FavoriteUrlCount;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.IntervalControlHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.robot.S2Robot;
import org.codelibs.robot.db.cbean.AccessResultCB;
import org.codelibs.robot.db.exbhv.AccessResultBhv;
import org.codelibs.robot.db.exbhv.AccessResultDataBhv;
import org.codelibs.robot.db.exentity.AccessResult;
import org.codelibs.robot.dbflute.cbean.PagingResultBean;
import org.codelibs.robot.entity.AccessResultData;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.service.UrlFilterService;
import org.codelibs.robot.service.UrlQueueService;
import org.codelibs.robot.transformer.Transformer;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexUpdater extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(IndexUpdater.class);

    protected List<String> sessionIdList;

    protected SearchClient searchClient;

    @Resource
    protected DataService dataService;

    @Resource
    protected UrlQueueService urlQueueService;

    @Resource
    protected UrlFilterService urlFilterService;

    @Resource
    protected AccessResultBhv accessResultBhv;

    @Resource
    protected AccessResultDataBhv accessResultDataBhv;

    @Resource
    protected ClickLogBhv clickLogBhv;

    @Resource
    protected FavoriteLogBhv favoriteLogBhv;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected FieldHelper fieldHelper;

    @Resource
    protected IndexingHelper indexingHelper;

    public int maxDocumentCacheSize = 5;

    public int maxInvalidDocumentSize = 100;

    protected boolean finishCrawling = false;

    public long updateInterval = 60000; // 1 min

    protected long executeTime;

    protected long documentSize;

    protected int maxSolrErrorCount = 0;

    protected int maxErrorCount = 2;

    protected int unprocessedDocumentSize = 100;

    protected List<String> finishedSessionIdList = new ArrayList<String>();

    public long commitMarginTime = 10000; // 10ms

    public int maxEmptyListCount = 60; // 1hour

    public boolean threadDump = false;

    public boolean clickCountEnabled = true;

    public boolean favoriteCountEnabled = true;

    private final List<BoostDocumentRule> boostRuleList = new ArrayList<BoostDocumentRule>();

    private final Map<String, Object> docValueMap = new HashMap<String, Object>();

    private List<S2Robot> s2RobotList;

    public IndexUpdater() {
        // nothing
    }

    public void addFinishedSessionId(final String sessionId) {
        synchronized (finishedSessionIdList) {
            finishedSessionIdList.add(sessionId);
        }
    }

    private void deleteBySessionId(final String sessionId) {
        try {
            urlFilterService.delete(sessionId);
        } catch (final Exception e) {
            logger.warn("Failed to delete url filters: " + sessionId, e);
        }
        try {
            urlQueueService.delete(sessionId);
        } catch (final Exception e) {
            logger.warn("Failed to delete url queues: " + sessionId, e);
        }
        try {
            dataService.delete(sessionId);
        } catch (final Exception e) {
            logger.warn("Failed to delete data: " + sessionId, e);
        }
    }

    @Override
    public void run() {
        if (dataService == null) {
            throw new FessSystemException("DataService is null.");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Starting indexUpdater.");
        }

        executeTime = 0;
        documentSize = 0;

        final IntervalControlHelper intervalControlHelper = ComponentUtil.getIntervalControlHelper();
        try {
            final AccessResultCB cb = new AccessResultCB();
            cb.setupSelect_AccessResultDataAsOne();
            cb.query().setSessionId_InScope(sessionIdList);
            cb.query().addOrderBy_CreateTime_Asc();
            cb.query().setStatus_Equal(org.codelibs.robot.Constants.OK_STATUS);
            if (maxDocumentCacheSize <= 0) {
                maxDocumentCacheSize = 1;
            }
            cb.fetchFirst(maxDocumentCacheSize);
            cb.fetchPage(1);

            final List<Map<String, Object>> docList = new ArrayList<>();
            final List<org.codelibs.robot.entity.AccessResult> accessResultList = new ArrayList<org.codelibs.robot.entity.AccessResult>();
            final List<org.codelibs.robot.db.exentity.AccessResultData> accessResultDataList =
                    new ArrayList<org.codelibs.robot.db.exentity.AccessResultData>();

            long updateTime = System.currentTimeMillis();
            int errorCount = 0;
            int emptyListCount = 0;
            while (!finishCrawling || !accessResultList.isEmpty()) {
                try {
                    final int sessionIdListSize = finishedSessionIdList.size();
                    intervalControlHelper.setCrawlerRunning(true);

                    updateTime = System.currentTimeMillis() - updateTime;

                    final long interval = updateInterval - updateTime;
                    if (interval > 0) {
                        // sleep
                        try {
                            Thread.sleep(interval); // 1 min (default)
                        } catch (final InterruptedException e) {
                            logger.warn("Interrupted index update.", e);
                        }
                    }

                    docList.clear();
                    accessResultList.clear();
                    accessResultDataList.clear();

                    intervalControlHelper.delayByRules();

                    if (logger.isDebugEnabled()) {
                        logger.debug("Processing documents in IndexUpdater queue.");
                    }

                    updateTime = System.currentTimeMillis();

                    PagingResultBean<AccessResult> arList = getAccessResultList(cb);
                    if (arList.isEmpty()) {
                        emptyListCount++;
                    } else {
                        emptyListCount = 0; // reset
                    }
                    while (!arList.isEmpty()) {
                        processAccessResults(docList, accessResultList, accessResultDataList, arList);

                        cleanupAccessResults(accessResultList, accessResultDataList);

                        if (logger.isDebugEnabled()) {
                            logger.debug("Getting documents in IndexUpdater queue.");
                        }
                        arList = getAccessResultList(cb);
                    }

                    if (!docList.isEmpty()) {
                        indexingHelper.sendDocuments(searchClient, docList);
                    }

                    synchronized (finishedSessionIdList) {
                        if (sessionIdListSize != 0 && sessionIdListSize == finishedSessionIdList.size()) {
                            cleanupFinishedSessionData();
                        }
                    }
                    executeTime += System.currentTimeMillis() - updateTime;

                    if (logger.isDebugEnabled()) {
                        logger.debug("Processed documents in IndexUpdater queue.");
                    }

                    // reset count
                    errorCount = 0;
                } catch (final Exception e) {
                    if (errorCount > maxErrorCount) {
                        throw e;
                    }
                    errorCount++;
                    logger.warn("Failed to access data. Retry to access.. " + errorCount, e);
                } finally {
                    if (systemHelper.isForceStop()) {
                        finishCrawling = true;
                        if (logger.isDebugEnabled()) {
                            logger.debug("Stopped indexUpdater.");
                        }
                    }
                }

                if (emptyListCount >= maxEmptyListCount) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Terminating indexUpdater. " + "emptyListCount is over " + maxEmptyListCount + ".");
                    }
                    // terminate crawling
                    finishCrawling = true;
                    forceStop();
                    if (threadDump) {
                        printThreadDump();
                    }

                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Finished indexUpdater.");
            }
        } catch (final Throwable t) { // NOPMD
            logger.error("IndexUpdater is terminated.", t);
            forceStop();
        } finally {
            intervalControlHelper.setCrawlerRunning(true);
        }

        if (logger.isInfoEnabled()) {
            logger.info("[EXEC TIME] index update time: " + executeTime + "ms");
        }

    }

    private void printThreadDump() {
        for (final Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            logger.info("Thread: " + entry.getKey());
            final StackTraceElement[] trace = entry.getValue();
            for (final StackTraceElement element : trace) {
                logger.info("\tat " + element);
            }
        }
    }

    private void processAccessResults(final List<Map<String, Object>> docList,
            final List<org.codelibs.robot.entity.AccessResult> accessResultList,
            final List<org.codelibs.robot.db.exentity.AccessResultData> accessResultDataList, final PagingResultBean<AccessResult> arList) {
        for (final AccessResult accessResult : arList) {
            if (logger.isDebugEnabled()) {
                logger.debug("Indexing " + accessResult.getUrl());
            }
            accessResult.setStatus(Constants.DONE_STATUS);
            accessResultList.add(accessResult);

            if (accessResult.getHttpStatusCode() != 200) {
                // invalid page
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipped. The response code is " + accessResult.getHttpStatusCode() + ".");
                }
                continue;
            }

            final AccessResultData accessResultData = accessResult.getAccessResultData();
            if (accessResultData != null) {
                accessResult.setAccessResultData(null);
                accessResultDataList.add((org.codelibs.robot.db.exentity.AccessResultData) accessResultData);
                try {
                    final Transformer transformer = SingletonS2Container.getComponent(accessResultData.getTransformerName());
                    if (transformer == null) {
                        // no transformer
                        logger.warn("No transformer: " + accessResultData.getTransformerName());
                        continue;
                    }
                    @SuppressWarnings("unchecked")
                    final Map<String, Object> map = (Map<String, Object>) transformer.getData(accessResultData);
                    if (map.isEmpty()) {
                        // no transformer
                        logger.warn("No data: " + accessResult.getUrl());
                        continue;
                    }

                    if (Constants.FALSE.equals(map.get(Constants.INDEXING_TARGET))) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Skipped. " + "This document is not a index target. ");
                        }
                        continue;
                    } else {
                        map.remove(Constants.INDEXING_TARGET);
                    }

                    updateDocument(map);

                    docList.add(map);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Added the document. " + "The number of a document cache is " + docList.size() + ".");
                    }

                    if (docList.size() >= maxDocumentCacheSize) {
                        indexingHelper.sendDocuments(searchClient, docList);
                    }
                    documentSize++;
                    if (logger.isDebugEnabled()) {
                        logger.debug("The number of an added document is " + documentSize + ".");
                    }
                } catch (final Exception e) {
                    logger.warn("Could not add a doc: " + accessResult.getUrl(), e);
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipped. No content. ");
                }
            }

        }
    }

    protected void updateDocument(final Map<String, Object> map) {
        if (clickCountEnabled) {
            addClickCountField(map);
        }

        if (favoriteCountEnabled) {
            addFavoriteCountField(map);
        }

        // default values
        for (final Map.Entry<String, Object> entry : docValueMap.entrySet()) {
            final String key = entry.getKey();
            final Object obj = map.get(key);
            if (obj == null) {
                map.put(key, entry.getValue());
            }
        }

        float documentBoost = 0.0f;
        for (final BoostDocumentRule rule : boostRuleList) {
            if (rule.match(map)) {
                documentBoost = rule.getValue(map);
                break;
            }
        }

        if (documentBoost > 0) {
            addBoostValue(map, documentBoost);
        }

        if (!map.containsKey(fieldHelper.docIdField)) {
            map.put(fieldHelper.docIdField, systemHelper.generateDocId(map));
        }
    }

    protected void addBoostValue(final Map<String, Object> map, final float documentBoost) {
        map.put(fieldHelper.boostField, documentBoost);
        if (logger.isDebugEnabled()) {
            logger.debug("Set a document boost (" + documentBoost + ").");
        }
    }

    protected void addClickCountField(final Map<String, Object> doc) {
        final String url = (String) doc.get(fieldHelper.urlField);
        if (StringUtil.isNotBlank(url)) {
            final int count = clickLogBhv.selectCount(cb -> {
                cb.query().setUrl_Equal(url);
            });
            doc.put(fieldHelper.clickCountField, count);
            if (logger.isDebugEnabled()) {
                logger.debug("Click Count: " + count + ", url: " + url);
            }
        }
    }

    protected void addFavoriteCountField(final Map<String, Object> map) {
        final String url = (String) map.get(fieldHelper.urlField);
        if (StringUtil.isNotBlank(url)) {
            final FavoriteUrlCountPmb pmb = new FavoriteUrlCountPmb();
            pmb.setUrl(url);
            final List<FavoriteUrlCount> list = favoriteLogBhv.outsideSql().selectList(pmb);

            long count = 0;
            if (!list.isEmpty()) {
                count = list.get(0).getCnt().longValue();
            }

            map.put(fieldHelper.favoriteCountField, count);
            if (logger.isDebugEnabled()) {
                logger.debug("Favorite Count: " + count + ", url: " + url);
            }
        }
    }

    private void cleanupAccessResults(final List<org.codelibs.robot.entity.AccessResult> accessResultList,
            final List<org.codelibs.robot.db.exentity.AccessResultData> accessResultDataList) {
        if (!accessResultList.isEmpty()) {
            final long execTime = System.currentTimeMillis();
            final int size = accessResultList.size();
            dataService.update(accessResultList);
            accessResultList.clear();
            if (logger.isDebugEnabled()) {
                logger.debug("Updated " + size + " access results. The execution time is " + (System.currentTimeMillis() - execTime)
                        + "ms.");
            }
        }

        if (!accessResultDataList.isEmpty()) {
            final long execTime = System.currentTimeMillis();
            final int size = accessResultDataList.size();
            // clean up content
            accessResultDataBhv.batchDelete(accessResultDataList);
            accessResultDataList.clear();
            if (logger.isDebugEnabled()) {
                logger.debug("Deleted " + size + " access result data. The execution time is " + (System.currentTimeMillis() - execTime)
                        + "ms.");
            }
        }
    }

    private PagingResultBean<AccessResult> getAccessResultList(final AccessResultCB cb) {
        final long execTime = System.currentTimeMillis();
        final PagingResultBean<AccessResult> arList = accessResultBhv.selectPage(cb);
        if (!arList.isEmpty()) {
            for (final AccessResult ar : arList.toArray(new AccessResult[arList.size()])) {
                if (ar.getCreateTime().getTime() > execTime - commitMarginTime) {
                    arList.remove(ar);
                }
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Processing " + arList.size() + "/" + arList.getAllRecordCount() + " docs (DB: "
                    + (System.currentTimeMillis() - execTime) + "ms)");
        }
        if (arList.getAllRecordCount() > unprocessedDocumentSize) {
            if (logger.isInfoEnabled()) {
                logger.info("Stopped all crawler threads. " + " You have " + arList.getAllRecordCount() + " (>" + unprocessedDocumentSize
                        + ") " + " unprocessed docs.");
            }
            final IntervalControlHelper intervalControlHelper = ComponentUtil.getIntervalControlHelper();
            intervalControlHelper.setCrawlerRunning(false);
        }
        return arList;
    }

    private void cleanupFinishedSessionData() {
        final long execTime = System.currentTimeMillis();
        // cleanup
        for (final String sessionId : finishedSessionIdList) {
            final long execTime2 = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("Deleting document data: " + sessionId);
            }
            deleteBySessionId(sessionId);
            if (logger.isDebugEnabled()) {
                logger.debug("Deleted " + sessionId + " documents. The execution time is " + (System.currentTimeMillis() - execTime2)
                        + "ms.");
            }
        }
        finishedSessionIdList.clear();

        if (logger.isInfoEnabled()) {
            logger.info("Deleted completed document data. " + "The execution time is " + (System.currentTimeMillis() - execTime) + "ms.");
        }
    }

    private void forceStop() {
        systemHelper.setForceStop(true);
        for (final S2Robot s2Robot : s2RobotList) {
            s2Robot.stop();
        }
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public List<String> getSessionIdList() {
        return sessionIdList;
    }

    public void setSessionIdList(final List<String> sessionIdList) {
        this.sessionIdList = sessionIdList;
    }

    public void setFinishCrawling(final boolean finishCrawling) {
        this.finishCrawling = finishCrawling;
    }

    public long getDocumentSize() {
        return documentSize;
    }

    @Binding(bindingType = BindingType.MAY)
    @Override
    public void setUncaughtExceptionHandler(final UncaughtExceptionHandler eh) {
        super.setUncaughtExceptionHandler(eh);
    }

    @Binding(bindingType = BindingType.MAY)
    public static void setDefaultUncaughtExceptionHandler(final UncaughtExceptionHandler eh) {
        Thread.setDefaultUncaughtExceptionHandler(eh);
    }

    public void setMaxSolrErrorCount(final int maxSolrErrorCount) {
        this.maxSolrErrorCount = maxSolrErrorCount;
    }

    public void setUnprocessedDocumentSize(final int unprocessedDocumentSize) {
        this.unprocessedDocumentSize = unprocessedDocumentSize;
    }

    public void addBoostDocumentRule(final BoostDocumentRule rule) {
        boostRuleList.add(rule);
    }

    public void addDefaultDocValue(final String fieldName, final Object value) {
        docValueMap.put(fieldName, value);
    }

    public void setS2RobotList(final List<S2Robot> s2RobotList) {
        this.s2RobotList = s2RobotList;
    }
}
