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

package jp.sf.fess.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.db.exentity.FileCrawlingConfig;
import jp.sf.fess.db.exentity.WebCrawlingConfig;
import jp.sf.fess.interval.FessIntervalController;
import jp.sf.fess.service.FailureUrlService;
import jp.sf.fess.service.FileAuthenticationService;
import jp.sf.fess.service.FileCrawlingConfigService;
import jp.sf.fess.service.WebCrawlingConfigService;
import jp.sf.fess.solr.IndexUpdater;

import org.codelibs.core.util.DynamicProperties;
import org.codelibs.solr.lib.SolrGroup;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.S2Robot;
import org.seasar.robot.S2RobotContext;
import org.seasar.robot.service.DataService;
import org.seasar.robot.service.UrlFilterService;
import org.seasar.robot.service.UrlQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebFsIndexHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(WebFsIndexHelper.class);

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
    protected CrawlingConfigHelper crawlingConfigHelper;

    public long maxAccessCount = 100000;

    public long crawlingExecutionInterval = Constants.DEFAULT_CRAWLING_EXECUTION_INTERVAL;

    public int indexUpdaterPriority = Thread.MAX_PRIORITY;

    public int crawlerPriority = Thread.NORM_PRIORITY;

    private final List<S2Robot> s2RobotList = Collections
            .synchronizedList(new ArrayList<S2Robot>());

    public void crawl(final String sessionId, final SolrGroup solrGroup) {
        final List<WebCrawlingConfig> webConfigList = webCrawlingConfigService
                .getAllWebCrawlingConfigList();
        final List<FileCrawlingConfig> fileConfigList = fileCrawlingConfigService
                .getAllFileCrawlingConfigList();

        if (webConfigList.isEmpty() && fileConfigList.isEmpty()) {
            // nothing
            if (logger.isInfoEnabled()) {
                logger.info("No crawling target urls.");
            }
            return;
        }

        crawl(sessionId, solrGroup, webConfigList, fileConfigList);
    }

    public void crawl(final String sessionId, final List<Long> webConfigIdList,
            final List<Long> fileConfigIdList, final SolrGroup solrGroup) {
        final List<WebCrawlingConfig> webConfigList = webCrawlingConfigService
                .getWebCrawlingConfigListByIds(webConfigIdList);
        final List<FileCrawlingConfig> fileConfigList = fileCrawlingConfigService
                .getFileCrawlingConfigListByIds(fileConfigIdList);

        if (webConfigList.isEmpty() && fileConfigList.isEmpty()) {
            // nothing
            if (logger.isInfoEnabled()) {
                logger.info("No crawling target urls.");
            }
            return;
        }

        crawl(sessionId, solrGroup, webConfigList, fileConfigList);
    }

    protected void crawl(final String sessionId, final SolrGroup solrGroup,
            final List<WebCrawlingConfig> webConfigList,
            final List<FileCrawlingConfig> fileConfigList) {
        int multiprocessCrawlingCount = 5;
        String value = crawlerProperties.getProperty(
                Constants.CRAWLING_THREAD_COUNT_PROPERTY, "5");
        try {
            multiprocessCrawlingCount = Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            // NOP
        }

        long commitPerCount = Constants.DEFAULT_COMMIT_PER_COUNT;
        value = crawlerProperties.getProperty(
                Constants.COMMIT_PER_COUNT_PROPERTY,
                Long.toString(Constants.DEFAULT_COMMIT_PER_COUNT));
        try {
            commitPerCount = Long.parseLong(value);
        } catch (final NumberFormatException e) {
            // NOP
        }

        final SystemHelper systemHelper = SingletonS2Container
                .getComponent("systemHelper");

        final long startTime = System.currentTimeMillis();

        final List<String> sessionIdList = new ArrayList<String>();
        s2RobotList.clear();
        final List<String> s2RobotStatusList = new ArrayList<String>();
        // Web
        for (final WebCrawlingConfig webCrawlingConfig : webConfigList) {
            final String sid = crawlingConfigHelper.store(sessionId,
                    webCrawlingConfig);

            // create s2robot
            final S2Robot s2Robot = SingletonS2Container
                    .getComponent(S2Robot.class);
            s2Robot.setSessionId(sid);
            sessionIdList.add(sid);

            final String urlsStr = webCrawlingConfig.getUrls();
            if (StringUtil.isBlank(urlsStr)) {
                logger.warn("No target urls. Skipped");
                break;
            }

            // interval time
            final int intervalTime = webCrawlingConfig.getIntervalTime() != null ? webCrawlingConfig
                    .getIntervalTime()
                    : Constants.DEFAULT_INTERVAL_TIME_FOR_WEB;
            ((FessIntervalController) s2Robot.getIntervalController())
                    .setDelayMillisForWaitingNewUrl(intervalTime);

            final String includedUrlsStr = webCrawlingConfig.getIncludedUrls() != null ? webCrawlingConfig
                    .getIncludedUrls() : "";
            final String excludedUrlsStr = webCrawlingConfig.getExcludedUrls() != null ? webCrawlingConfig
                    .getExcludedUrls() : "";

            // num of threads
            final S2RobotContext robotContext = s2Robot.getRobotContext();
            final int numOfThread = webCrawlingConfig.getNumOfThread() != null ? webCrawlingConfig
                    .getNumOfThread() : Constants.DEFAULT_NUM_OF_THREAD_FOR_WEB;
            robotContext.setNumOfThread(numOfThread);

            // depth
            final int depth = webCrawlingConfig.getDepth() != null ? webCrawlingConfig
                    .getDepth() : -1;
            robotContext.setMaxDepth(depth);

            // max count
            final long maxCount = webCrawlingConfig.getMaxAccessCount() != null ? webCrawlingConfig
                    .getMaxAccessCount() : maxAccessCount;
            robotContext.setMaxAccessCount(maxCount);

            webCrawlingConfig.initializeClientFactory(s2Robot
                    .getClientFactory());

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
            final List<String> excludedUrlList = failureUrlService
                    .getExcludedUrlList(webCrawlingConfig.getId(), null);
            if (excludedUrlList != null) {
                for (final String u : excludedUrlList) {
                    if (StringUtil.isNotBlank(u)) {
                        final String urlValue = u.trim();
                        s2Robot.addExcludeFilter(urlValue);
                        if (logger.isInfoEnabled()) {
                            logger.info("Excluded URL from failures: "
                                    + urlValue);
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
            final String sid = crawlingConfigHelper.store(sessionId,
                    fileCrawlingConfig);

            // create s2robot
            final S2Robot s2Robot = SingletonS2Container
                    .getComponent(S2Robot.class);
            s2Robot.setSessionId(sid);
            sessionIdList.add(sid);

            final String pathsStr = fileCrawlingConfig.getPaths();
            if (StringUtil.isBlank(pathsStr)) {
                logger.warn("No target uris. Skipped");
                break;
            }

            final int intervalTime = fileCrawlingConfig.getIntervalTime() != null ? fileCrawlingConfig
                    .getIntervalTime() : Constants.DEFAULT_INTERVAL_TIME_FOR_FS;
            ((FessIntervalController) s2Robot.getIntervalController())
                    .setDelayMillisForWaitingNewUrl(intervalTime);

            final String includedPathsStr = fileCrawlingConfig
                    .getIncludedPaths() != null ? fileCrawlingConfig
                    .getIncludedPaths() : "";
            final String excludedPathsStr = fileCrawlingConfig
                    .getExcludedPaths() != null ? fileCrawlingConfig
                    .getExcludedPaths() : "";

            // num of threads
            final S2RobotContext robotContext = s2Robot.getRobotContext();
            final int numOfThread = fileCrawlingConfig.getNumOfThread() != null ? fileCrawlingConfig
                    .getNumOfThread() : Constants.DEFAULT_NUM_OF_THREAD_FOR_FS;
            robotContext.setNumOfThread(numOfThread);

            // depth
            final int depth = fileCrawlingConfig.getDepth() != null ? fileCrawlingConfig
                    .getDepth() : -1;
            robotContext.setMaxDepth(depth);

            // max count
            final long maxCount = fileCrawlingConfig.getMaxAccessCount() != null ? fileCrawlingConfig
                    .getMaxAccessCount() : maxAccessCount;
            robotContext.setMaxAccessCount(maxCount);

            fileCrawlingConfig.initializeClientFactory(s2Robot
                    .getClientFactory());

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
            final String[] includedPaths = includedPathsStr.split("[\r\n]");
            for (final String u : includedPaths) {
                if (StringUtil.isNotBlank(u)) {
                    final String urlValue = u.trim();
                    if (!urlValue.startsWith("#")) {
                        s2Robot.addIncludeFilter(systemHelper
                                .encodeUrlFilter(urlValue));
                        if (logger.isInfoEnabled()) {
                            logger.info("Included Path: " + urlValue);
                        }
                    }
                }
            }

            // set excluded paths
            final String[] excludedPaths = excludedPathsStr.split("[\r\n]");
            for (final String u : excludedPaths) {
                if (StringUtil.isNotBlank(u)) {
                    final String urlValue = u.trim();
                    if (!urlValue.startsWith("#")) {
                        s2Robot.addExcludeFilter(systemHelper
                                .encodeUrlFilter(urlValue));
                        if (logger.isInfoEnabled()) {
                            logger.info("Excluded Path: " + urlValue);
                        }
                    }
                }
            }

            // failure url
            final List<String> excludedUrlList = failureUrlService
                    .getExcludedUrlList(null, fileCrawlingConfig.getId());
            if (excludedUrlList != null) {
                for (final String u : excludedUrlList) {
                    if (StringUtil.isNotBlank(u)) {
                        final String urlValue = u.trim();
                        s2Robot.addExcludeFilter(urlValue);
                        if (logger.isInfoEnabled()) {
                            logger.info("Excluded Path from failures: "
                                    + urlValue);
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
        final IndexUpdater indexUpdater = SingletonS2Container
                .getComponent("indexUpdater");
        indexUpdater.setName("IndexUpdater");
        indexUpdater.setPriority(indexUpdaterPriority);
        indexUpdater.setSessionIdList(sessionIdList);
        indexUpdater.setSolrGroup(solrGroup);
        indexUpdater.setDaemon(true);
        indexUpdater.setCommitPerCount(commitPerCount);
        indexUpdater.setS2RobotList(s2RobotList);
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
                if (!s2RobotList.get(i).getRobotContext().isRunning()
                        && s2RobotStatusList.get(i).equals(Constants.RUNNING)) {
                    s2RobotList.get(i).awaitTermination();
                    s2RobotStatusList.set(i, Constants.DONE);
                    final String sid = s2RobotList.get(i).getRobotContext()
                            .getSessionId();
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
                if (!s2RobotList.get(i).getRobotContext().isRunning()
                        && !s2RobotStatusList.get(i).equals(Constants.DONE)) {
                    s2RobotStatusList.set(i, Constants.DONE);
                    final String sid = s2RobotList.get(i).getRobotContext()
                            .getSessionId();
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
        final CrawlingSessionHelper crawlingSessionHelper = SingletonS2Container
                .getComponent("crawlingSessionHelper");

        final long execTime = System.currentTimeMillis() - startTime;
        crawlingSessionHelper.putToInfoMap(Constants.WEB_FS_CRAWLING_EXEC_TIME,
                Long.toString(execTime));
        if (logger.isInfoEnabled()) {
            logger.info("[EXEC TIME] crawling time: " + execTime + "ms");
        }

        indexUpdater.setFinishCrawling(true);
        try {
            indexUpdater.join();
        } catch (final InterruptedException e) {
            logger.warn("Interrupted index update.", e);
        }

        crawlingSessionHelper.putToInfoMap(Constants.WEB_FS_INDEX_EXEC_TIME,
                Long.toString(indexUpdater.getExecuteTime()));
        crawlingSessionHelper.putToInfoMap(Constants.WEB_FS_INDEX_SIZE,
                Long.toString(indexUpdater.getDocumentSize()));

        for (final String sid : sessionIdList) {
            // remove config
            crawlingConfigHelper.remove(sid);
        }

        // clear url filter
        final UrlFilterService urlFilterService = SingletonS2Container
                .getComponent(UrlFilterService.class);
        urlFilterService.deleteAll();

        // clear queue
        final UrlQueueService urlQueueService = SingletonS2Container
                .getComponent(UrlQueueService.class);
        urlQueueService.deleteAll();

        // clear
        final DataService dataService = SingletonS2Container
                .getComponent(DataService.class);
        dataService.deleteAll();

    }

}
