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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.db.exentity.FileCrawlingConfig;
import org.codelibs.fess.db.exentity.WebCrawlingConfig;
import org.codelibs.fess.entity.BoostDocumentRule;
import org.codelibs.fess.interval.FessIntervalController;
import org.codelibs.fess.service.BoostDocumentRuleService;
import org.codelibs.fess.service.FailureUrlService;
import org.codelibs.fess.service.FileAuthenticationService;
import org.codelibs.fess.service.FileCrawlingConfigService;
import org.codelibs.fess.service.WebCrawlingConfigService;
import org.codelibs.fess.solr.IndexUpdater;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.robot.S2Robot;
import org.codelibs.robot.S2RobotContext;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.service.UrlFilterService;
import org.codelibs.robot.service.UrlQueueService;
import org.seasar.framework.container.SingletonS2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebFsIndexHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(WebFsIndexHelper.class);

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    public WebCrawlingConfigService webCrawlingConfigService;

    @Resource
    protected FileCrawlingConfigService fileCrawlingConfigService;

    @Resource
    protected FileAuthenticationService fileAuthenticationService;

    @Resource
    public FailureUrlService failureUrlService;

    @Resource
    protected BoostDocumentRuleService boostDocumentRuleService;

    @Resource
    protected CrawlingConfigHelper crawlingConfigHelper;

    public long maxAccessCount = 100000;

    public long crawlingExecutionInterval = Constants.DEFAULT_CRAWLING_EXECUTION_INTERVAL;

    public int indexUpdaterPriority = Thread.MAX_PRIORITY;

    public int crawlerPriority = Thread.NORM_PRIORITY;

    private final List<S2Robot> s2RobotList = Collections.synchronizedList(new ArrayList<S2Robot>());

    // needed?
    @Deprecated
    public void crawl(final String sessionId) {
        final List<WebCrawlingConfig> webConfigList = webCrawlingConfigService.getAllWebCrawlingConfigList();
        final List<FileCrawlingConfig> fileConfigList = fileCrawlingConfigService.getAllFileCrawlingConfigList();

        if (webConfigList.isEmpty() && fileConfigList.isEmpty()) {
            // nothing
            if (logger.isInfoEnabled()) {
                logger.info("No crawling target urls.");
            }
            return;
        }

        doCrawl(sessionId, webConfigList, fileConfigList);
    }

    public void crawl(final String sessionId, final List<Long> webConfigIdList, final List<Long> fileConfigIdList) {
        final boolean runAll = webConfigIdList == null && fileConfigIdList == null;
        final List<WebCrawlingConfig> webConfigList;
        if (runAll || webConfigIdList != null) {
            webConfigList = webCrawlingConfigService.getWebCrawlingConfigListByIds(webConfigIdList);
        } else {
            webConfigList = Collections.emptyList();
        }
        final List<FileCrawlingConfig> fileConfigList;
        if (runAll || fileConfigIdList != null) {
            fileConfigList = fileCrawlingConfigService.getFileCrawlingConfigListByIds(fileConfigIdList);
        } else {
            fileConfigList = Collections.emptyList();
        }

        if (webConfigList.isEmpty() && fileConfigList.isEmpty()) {
            // nothing
            if (logger.isInfoEnabled()) {
                logger.info("No crawling target urls.");
            }
            return;
        }

        doCrawl(sessionId, webConfigList, fileConfigList);
    }

    protected void doCrawl(final String sessionId, final List<WebCrawlingConfig> webConfigList,
            final List<FileCrawlingConfig> fileConfigList) {
        int multiprocessCrawlingCount = 5;
        final String value = crawlerProperties.getProperty(Constants.CRAWLING_THREAD_COUNT_PROPERTY, "5");
        try {
            multiprocessCrawlingCount = Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            // NOP
        }

        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();

        final long startTime = System.currentTimeMillis();

        final List<String> sessionIdList = new ArrayList<String>();
        s2RobotList.clear();
        final List<String> s2RobotStatusList = new ArrayList<String>();
        // Web
        for (final WebCrawlingConfig webCrawlingConfig : webConfigList) {
            final String sid = crawlingConfigHelper.store(sessionId, webCrawlingConfig);

            // create s2robot
            final S2Robot s2Robot = SingletonS2Container.getComponent(S2Robot.class);
            s2Robot.setSessionId(sid);
            sessionIdList.add(sid);

            final String urlsStr = webCrawlingConfig.getUrls();
            if (StringUtil.isBlank(urlsStr)) {
                logger.warn("No target urls. Skipped");
                break;
            }

            // interval time
            final int intervalTime =
                    webCrawlingConfig.getIntervalTime() != null ? webCrawlingConfig.getIntervalTime()
                            : Constants.DEFAULT_INTERVAL_TIME_FOR_WEB;
            ((FessIntervalController) s2Robot.getIntervalController()).setDelayMillisForWaitingNewUrl(intervalTime);

            final String includedUrlsStr =
                    webCrawlingConfig.getIncludedUrls() != null ? webCrawlingConfig.getIncludedUrls() : StringUtil.EMPTY;
            final String excludedUrlsStr =
                    webCrawlingConfig.getExcludedUrls() != null ? webCrawlingConfig.getExcludedUrls() : StringUtil.EMPTY;

            // num of threads
            final S2RobotContext robotContext = s2Robot.getRobotContext();
            final int numOfThread =
                    webCrawlingConfig.getNumOfThread() != null ? webCrawlingConfig.getNumOfThread()
                            : Constants.DEFAULT_NUM_OF_THREAD_FOR_WEB;
            robotContext.setNumOfThread(numOfThread);

            // depth
            final int depth = webCrawlingConfig.getDepth() != null ? webCrawlingConfig.getDepth() : -1;
            robotContext.setMaxDepth(depth);

            // max count
            final long maxCount = webCrawlingConfig.getMaxAccessCount() != null ? webCrawlingConfig.getMaxAccessCount() : maxAccessCount;
            robotContext.setMaxAccessCount(maxCount);

            webCrawlingConfig.initializeClientFactory(s2Robot.getClientFactory());

            // set urls
            final String[] urls = urlsStr.split("[\r\n]");
            for (final String u : urls) {
                if (StringUtil.isNotBlank(u)) {
                    final String urlValue = u.trim();
                    if (!urlValue.startsWith("#")) {
                        s2Robot.addUrl(urlValue);
                        if (logger.isInfoEnabled()) {
                            logger.info("Target URL: " + urlValue);
                        }
                    }
                }
            }

            // set included urls
            final String[] includedUrls = includedUrlsStr.split("[\r\n]");
            for (final String u : includedUrls) {
                if (StringUtil.isNotBlank(u)) {
                    final String urlValue = u.trim();
                    if (!urlValue.startsWith("#")) {
                        s2Robot.addIncludeFilter(urlValue);
                        if (logger.isInfoEnabled()) {
                            logger.info("Included URL: " + urlValue);
                        }
                    }
                }
            }

            // set excluded urls
            final String[] excludedUrls = excludedUrlsStr.split("[\r\n]");
            for (final String u : excludedUrls) {
                if (StringUtil.isNotBlank(u)) {
                    final String urlValue = u.trim();
                    if (!urlValue.startsWith("#")) {
                        s2Robot.addExcludeFilter(urlValue);
                        if (logger.isInfoEnabled()) {
                            logger.info("Excluded URL: " + urlValue);
                        }
                    }
                }
            }

            // failure url
            final List<String> excludedUrlList = failureUrlService.getExcludedUrlList(webCrawlingConfig.getConfigId());
            if (excludedUrlList != null) {
                for (final String u : excludedUrlList) {
                    if (StringUtil.isNotBlank(u)) {
                        final String urlValue = u.trim();
                        s2Robot.addExcludeFilter(urlValue);
                        if (logger.isInfoEnabled()) {
                            logger.info("Excluded URL from failures: " + urlValue);
                        }
                    }
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Crawling " + urlsStr);
            }

            s2Robot.setBackground(true);
            s2Robot.setThreadPriority(crawlerPriority);

            s2RobotList.add(s2Robot);
            s2RobotStatusList.add(Constants.READY);
        }

        // File
        for (final FileCrawlingConfig fileCrawlingConfig : fileConfigList) {
            final String sid = crawlingConfigHelper.store(sessionId, fileCrawlingConfig);

            // create s2robot
            final S2Robot s2Robot = SingletonS2Container.getComponent(S2Robot.class);
            s2Robot.setSessionId(sid);
            sessionIdList.add(sid);

            final String pathsStr = fileCrawlingConfig.getPaths();
            if (StringUtil.isBlank(pathsStr)) {
                logger.warn("No target uris. Skipped");
                break;
            }

            final int intervalTime =
                    fileCrawlingConfig.getIntervalTime() != null ? fileCrawlingConfig.getIntervalTime()
                            : Constants.DEFAULT_INTERVAL_TIME_FOR_FS;
            ((FessIntervalController) s2Robot.getIntervalController()).setDelayMillisForWaitingNewUrl(intervalTime);

            final String includedPathsStr =
                    fileCrawlingConfig.getIncludedPaths() != null ? fileCrawlingConfig.getIncludedPaths() : StringUtil.EMPTY;
            final String excludedPathsStr =
                    fileCrawlingConfig.getExcludedPaths() != null ? fileCrawlingConfig.getExcludedPaths() : StringUtil.EMPTY;

            // num of threads
            final S2RobotContext robotContext = s2Robot.getRobotContext();
            final int numOfThread =
                    fileCrawlingConfig.getNumOfThread() != null ? fileCrawlingConfig.getNumOfThread()
                            : Constants.DEFAULT_NUM_OF_THREAD_FOR_FS;
            robotContext.setNumOfThread(numOfThread);

            // depth
            final int depth = fileCrawlingConfig.getDepth() != null ? fileCrawlingConfig.getDepth() : -1;
            robotContext.setMaxDepth(depth);

            // max count
            final long maxCount = fileCrawlingConfig.getMaxAccessCount() != null ? fileCrawlingConfig.getMaxAccessCount() : maxAccessCount;
            robotContext.setMaxAccessCount(maxCount);

            fileCrawlingConfig.initializeClientFactory(s2Robot.getClientFactory());

            // set paths
            final String[] paths = pathsStr.split("[\r\n]");
            for (String u : paths) {
                if (StringUtil.isNotBlank(u)) {
                    u = u.trim();
                    if (!u.startsWith("#")) {
                        if (!u.startsWith("file:") && !u.startsWith("smb:")) {
                            if (u.startsWith("/")) {
                                u = "file:" + u;
                            } else {
                                u = "file:/" + u;
                            }
                        }
                        s2Robot.addUrl(u);
                        if (logger.isInfoEnabled()) {
                            logger.info("Target Path: " + u);
                        }
                    }
                }
            }

            // set included paths
            boolean urlEncodeDisabled = false;
            final String[] includedPaths = includedPathsStr.split("[\r\n]");
            for (final String u : includedPaths) {
                if (StringUtil.isNotBlank(u)) {
                    final String line = u.trim();
                    if (!line.startsWith("#")) {
                        final String urlValue;
                        if (urlEncodeDisabled) {
                            urlValue = line;
                            urlEncodeDisabled = false;
                        } else {
                            urlValue = systemHelper.encodeUrlFilter(line);
                        }
                        s2Robot.addIncludeFilter(urlValue);
                        if (logger.isInfoEnabled()) {
                            logger.info("Included Path: " + urlValue);
                        }
                    } else if (line.startsWith("#DISABLE_URL_ENCODE")) {
                        urlEncodeDisabled = true;
                    }
                }
            }

            // set excluded paths
            urlEncodeDisabled = false;
            final String[] excludedPaths = excludedPathsStr.split("[\r\n]");
            for (final String u : excludedPaths) {
                if (StringUtil.isNotBlank(u)) {
                    final String line = u.trim();
                    if (!line.startsWith("#")) {
                        final String urlValue;
                        if (urlEncodeDisabled) {
                            urlValue = line;
                            urlEncodeDisabled = false;
                        } else {
                            urlValue = systemHelper.encodeUrlFilter(line);
                        }
                        s2Robot.addExcludeFilter(urlValue);
                        if (logger.isInfoEnabled()) {
                            logger.info("Excluded Path: " + urlValue);
                        }
                    } else if (line.startsWith("#DISABLE_URL_ENCODE")) {
                        urlEncodeDisabled = true;
                    }
                }
            }

            // failure url
            final List<String> excludedUrlList = failureUrlService.getExcludedUrlList(fileCrawlingConfig.getConfigId());
            if (excludedUrlList != null) {
                for (final String u : excludedUrlList) {
                    if (StringUtil.isNotBlank(u)) {
                        final String urlValue = u.trim();
                        s2Robot.addExcludeFilter(urlValue);
                        if (logger.isInfoEnabled()) {
                            logger.info("Excluded Path from failures: " + urlValue);
                        }
                    }
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Crawling " + pathsStr);
            }

            s2Robot.setBackground(true);
            s2Robot.setThreadPriority(crawlerPriority);

            s2RobotList.add(s2Robot);
            s2RobotStatusList.add(Constants.READY);
        }

        // run index update
        final IndexUpdater indexUpdater = ComponentUtil.getIndexUpdater();
        indexUpdater.setName("IndexUpdater");
        indexUpdater.setPriority(indexUpdaterPriority);
        indexUpdater.setSessionIdList(sessionIdList);
        indexUpdater.setDaemon(true);
        indexUpdater.setS2RobotList(s2RobotList);
        for (final BoostDocumentRule rule : boostDocumentRuleService.getAvailableBoostDocumentRuleList()) {
            indexUpdater.addBoostDocumentRule(new org.codelibs.fess.solr.BoostDocumentRule(rule));
        }
        indexUpdater.start();

        int startedCrawlerNum = 0;
        int activeCrawlerNum = 0;
        while (startedCrawlerNum < s2RobotList.size()) {
            // Force to stop crawl
            if (systemHelper.isForceStop()) {
                for (final S2Robot s2Robot : s2RobotList) {
                    s2Robot.stop();
                }
                break;
            }

            if (activeCrawlerNum < multiprocessCrawlingCount) {
                // start crawling
                s2RobotList.get(startedCrawlerNum).execute();
                s2RobotStatusList.set(startedCrawlerNum, Constants.RUNNING);
                startedCrawlerNum++;
                activeCrawlerNum++;
                try {
                    Thread.sleep(crawlingExecutionInterval);
                } catch (final InterruptedException e) {
                    // NOP
                }
                continue;
            }

            // check status
            for (int i = 0; i < startedCrawlerNum; i++) {
                if (!s2RobotList.get(i).getRobotContext().isRunning() && s2RobotStatusList.get(i).equals(Constants.RUNNING)) {
                    s2RobotList.get(i).awaitTermination();
                    s2RobotStatusList.set(i, Constants.DONE);
                    final String sid = s2RobotList.get(i).getRobotContext().getSessionId();
                    indexUpdater.addFinishedSessionId(sid);
                    activeCrawlerNum--;
                }
            }
            try {
                Thread.sleep(crawlingExecutionInterval);
            } catch (final InterruptedException e) {
                // NOP
            }
        }

        boolean finishedAll = false;
        while (!finishedAll) {
            finishedAll = true;
            for (int i = 0; i < s2RobotList.size(); i++) {
                s2RobotList.get(i).awaitTermination(crawlingExecutionInterval);
                if (!s2RobotList.get(i).getRobotContext().isRunning() && !s2RobotStatusList.get(i).equals(Constants.DONE)) {
                    s2RobotStatusList.set(i, Constants.DONE);
                    final String sid = s2RobotList.get(i).getRobotContext().getSessionId();
                    indexUpdater.addFinishedSessionId(sid);
                }
                if (!s2RobotStatusList.get(i).equals(Constants.DONE)) {
                    finishedAll = false;
                }
            }
        }
        s2RobotList.clear();
        s2RobotStatusList.clear();

        // put cralwing info
        final CrawlingSessionHelper crawlingSessionHelper = ComponentUtil.getCrawlingSessionHelper();

        final long execTime = System.currentTimeMillis() - startTime;
        crawlingSessionHelper.putToInfoMap(Constants.WEB_FS_CRAWLING_EXEC_TIME, Long.toString(execTime));
        if (logger.isInfoEnabled()) {
            logger.info("[EXEC TIME] crawling time: " + execTime + "ms");
        }

        indexUpdater.setFinishCrawling(true);
        try {
            indexUpdater.join();
        } catch (final InterruptedException e) {
            logger.warn("Interrupted index update.", e);
        }

        crawlingSessionHelper.putToInfoMap(Constants.WEB_FS_INDEX_EXEC_TIME, Long.toString(indexUpdater.getExecuteTime()));
        crawlingSessionHelper.putToInfoMap(Constants.WEB_FS_INDEX_SIZE, Long.toString(indexUpdater.getDocumentSize()));

        for (final String sid : sessionIdList) {
            // remove config
            crawlingConfigHelper.remove(sid);
        }

        // clear url filter
        final UrlFilterService urlFilterService = SingletonS2Container.getComponent(UrlFilterService.class);
        urlFilterService.deleteAll();

        // clear queue
        final UrlQueueService urlQueueService = SingletonS2Container.getComponent(UrlQueueService.class);
        urlQueueService.deleteAll();

        // clear
        final DataService dataService = SingletonS2Container.getComponent(DataService.class);
        dataService.deleteAll();

    }

}
