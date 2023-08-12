/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import static org.codelibs.core.stream.StreamUtil.split;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.Crawler;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.CrawlerStatus;
import org.codelibs.fess.crawler.interval.FessIntervalController;
import org.codelibs.fess.crawler.service.impl.EsDataService;
import org.codelibs.fess.crawler.service.impl.EsUrlFilterService;
import org.codelibs.fess.crawler.service.impl.EsUrlQueueService;
import org.codelibs.fess.es.config.exbhv.BoostDocumentRuleBhv;
import org.codelibs.fess.es.config.exentity.BoostDocumentRule;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.Param.Config;
import org.codelibs.fess.es.config.exentity.FileConfig;
import org.codelibs.fess.es.config.exentity.WebConfig;
import org.codelibs.fess.indexer.IndexUpdater;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public class WebFsIndexHelper {

    private static final Logger logger = LogManager.getLogger(WebFsIndexHelper.class);

    private static final String DISABLE_URL_ENCODE = "#DISABLE_URL_ENCODE";

    protected long maxAccessCount = Long.MAX_VALUE;

    protected long crawlingExecutionInterval = Constants.DEFAULT_CRAWLING_EXECUTION_INTERVAL;

    protected int indexUpdaterPriority = Thread.MAX_PRIORITY;

    protected int crawlerPriority = Thread.NORM_PRIORITY;

    protected final List<Crawler> crawlerList = Collections.synchronizedList(new ArrayList<>());

    public void crawl(final String sessionId, final List<String> webConfigIdList, final List<String> fileConfigIdList) {
        final boolean runAll = webConfigIdList == null && fileConfigIdList == null;
        final List<WebConfig> webConfigList;
        if (runAll || webConfigIdList != null) {
            webConfigList = ComponentUtil.getCrawlingConfigHelper().getWebConfigListByIds(webConfigIdList);
        } else {
            webConfigList = Collections.emptyList();
        }
        final List<FileConfig> fileConfigList;
        if (runAll || fileConfigIdList != null) {
            fileConfigList = ComponentUtil.getCrawlingConfigHelper().getFileConfigListByIds(fileConfigIdList);
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

    protected void doCrawl(final String sessionId, final List<WebConfig> webConfigList, final List<FileConfig> fileConfigList) {
        final int multiprocessCrawlingCount = ComponentUtil.getFessConfig().getCrawlingThreadCount();

        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final long startTime = System.currentTimeMillis();

        final List<String> sessionIdList = new ArrayList<>();
        crawlerList.clear();
        final List<String> crawlerStatusList = new ArrayList<>();
        // Web
        for (final WebConfig webConfig : webConfigList) {
            final String sid = ComponentUtil.getCrawlingConfigHelper().store(sessionId, webConfig);

            // create crawler
            final Crawler crawler = ComponentUtil.getComponent(Crawler.class);
            crawler.setSessionId(sid);
            sessionIdList.add(sid);

            final String urlsStr = webConfig.getUrls();
            if (StringUtil.isBlank(urlsStr)) {
                logger.warn("No target urls. Skipped");
                break;
            }

            // interval time
            final int intervalTime =
                    webConfig.getIntervalTime() != null ? webConfig.getIntervalTime() : Constants.DEFAULT_INTERVAL_TIME_FOR_WEB;
            ((FessIntervalController) crawler.getIntervalController()).setDelayMillisForWaitingNewUrl(intervalTime);

            final String includedUrlsStr = webConfig.getIncludedUrls() != null ? webConfig.getIncludedUrls() : StringUtil.EMPTY;
            final String excludedUrlsStr = webConfig.getExcludedUrls() != null ? webConfig.getExcludedUrls() : StringUtil.EMPTY;

            // num of threads
            final CrawlerContext crawlerContext = crawler.getCrawlerContext();
            final int numOfThread =
                    webConfig.getNumOfThread() != null ? webConfig.getNumOfThread() : Constants.DEFAULT_NUM_OF_THREAD_FOR_WEB;
            crawlerContext.setNumOfThread(numOfThread);

            // depth
            final int depth = webConfig.getDepth() != null ? webConfig.getDepth() : -1;
            crawlerContext.setMaxDepth(depth);

            // max count
            final long maxCount = webConfig.getMaxAccessCount() != null ? webConfig.getMaxAccessCount() : maxAccessCount;
            crawlerContext.setMaxAccessCount(maxCount);

            webConfig.initializeClientFactory(() -> crawler.getClientFactory());
            final Map<String, String> configParamMap = webConfig.getConfigParameterMap(ConfigName.CONFIG);

            if (Constants.TRUE.equalsIgnoreCase(configParamMap.get(Config.CLEANUP_ALL))) {
                deleteCrawlData(sid);
            } else if (Constants.TRUE.equalsIgnoreCase(configParamMap.get(Config.CLEANUP_URL_FILTERS))) {
                final EsUrlFilterService urlFilterService = ComponentUtil.getComponent(EsUrlFilterService.class);
                try {
                    urlFilterService.delete(sid);
                } catch (final Exception e) {
                    logger.warn("Failed to delete url filters for {}", sid);
                }
            }

            final DuplicateHostHelper duplicateHostHelper = ComponentUtil.getDuplicateHostHelper();

            // set urls
            split(urlsStr, "[\r\n]").of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).distinct().forEach(urlValue -> {
                if (!urlValue.startsWith("#") && fessConfig.isValidCrawlerWebProtocol(urlValue)) {
                    final String u = duplicateHostHelper.convert(urlValue);
                    crawler.addUrl(u);
                    if (logger.isInfoEnabled()) {
                        logger.info("Target URL: {}", u);
                    }
                }
            }));

            // set included urls
            final AtomicBoolean urlEncodeDisabled = new AtomicBoolean(false);
            split(includedUrlsStr, "[\r\n]").of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(line -> {
                if (!line.startsWith("#")) {
                    final String urlValue;
                    if (urlEncodeDisabled.get()) {
                        urlValue = line;
                        urlEncodeDisabled.set(false);
                    } else {
                        urlValue = systemHelper.encodeUrlFilter(line);
                    }
                    crawler.addIncludeFilter(urlValue);
                    if (logger.isInfoEnabled()) {
                        logger.info("Included URL: {}", urlValue);
                    }
                } else if (line.startsWith(DISABLE_URL_ENCODE)) {
                    urlEncodeDisabled.set(true);
                }
            }));

            // set excluded urls
            urlEncodeDisabled.set(false);
            split(excludedUrlsStr, "[\r\n]").of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(line -> {
                if (!line.startsWith("#")) {
                    final String urlValue;
                    if (urlEncodeDisabled.get()) {
                        urlValue = line;
                        urlEncodeDisabled.set(false);
                    } else {
                        urlValue = systemHelper.encodeUrlFilter(line);
                    }
                    crawler.addExcludeFilter(urlValue);
                    if (logger.isInfoEnabled()) {
                        logger.info("Excluded URL: {}", urlValue);
                    }
                } else if (line.startsWith(DISABLE_URL_ENCODE)) {
                    urlEncodeDisabled.set(true);
                }
            }));

            // failure url
            final List<String> excludedUrlList = ComponentUtil.getCrawlingConfigHelper().getExcludedUrlList(webConfig.getConfigId());
            if (excludedUrlList != null) {
                excludedUrlList.stream().filter(StringUtil::isNotBlank).map(String::trim).distinct().forEach(u -> {
                    final String urlValue = Pattern.quote(u);
                    crawler.addExcludeFilter(urlValue);
                    if (logger.isInfoEnabled()) {
                        logger.info("Excluded URL from failures: {}", urlValue);
                    }
                });
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Crawling {}", urlsStr);
            }

            crawler.setBackground(true);
            crawler.setThreadPriority(crawlerPriority);

            crawlerList.add(crawler);
            crawlerStatusList.add(Constants.READY);
        }

        // File
        for (final FileConfig fileConfig : fileConfigList) {
            final String sid = ComponentUtil.getCrawlingConfigHelper().store(sessionId, fileConfig);

            // create crawler
            final Crawler crawler = ComponentUtil.getComponent(Crawler.class);
            crawler.setSessionId(sid);
            sessionIdList.add(sid);

            final String pathsStr = fileConfig.getPaths();
            if (StringUtil.isBlank(pathsStr)) {
                logger.warn("No target uris. Skipped");
                break;
            }

            final int intervalTime =
                    fileConfig.getIntervalTime() != null ? fileConfig.getIntervalTime() : Constants.DEFAULT_INTERVAL_TIME_FOR_FS;
            ((FessIntervalController) crawler.getIntervalController()).setDelayMillisForWaitingNewUrl(intervalTime);

            final String includedPathsStr = fileConfig.getIncludedPaths() != null ? fileConfig.getIncludedPaths() : StringUtil.EMPTY;
            final String excludedPathsStr = fileConfig.getExcludedPaths() != null ? fileConfig.getExcludedPaths() : StringUtil.EMPTY;

            // num of threads
            final CrawlerContext crawlerContext = crawler.getCrawlerContext();
            final int numOfThread =
                    fileConfig.getNumOfThread() != null ? fileConfig.getNumOfThread() : Constants.DEFAULT_NUM_OF_THREAD_FOR_FS;
            crawlerContext.setNumOfThread(numOfThread);

            // depth
            final int depth = fileConfig.getDepth() != null ? fileConfig.getDepth() : -1;
            crawlerContext.setMaxDepth(depth);

            // max count
            final long maxCount = fileConfig.getMaxAccessCount() != null ? fileConfig.getMaxAccessCount() : maxAccessCount;
            crawlerContext.setMaxAccessCount(maxCount);

            fileConfig.initializeClientFactory(() -> crawler.getClientFactory());
            final Map<String, String> configParamMap = fileConfig.getConfigParameterMap(ConfigName.CONFIG);

            if (Constants.TRUE.equalsIgnoreCase(configParamMap.get(Config.CLEANUP_ALL))) {
                deleteCrawlData(sid);
            } else if (Constants.TRUE.equalsIgnoreCase(configParamMap.get(Config.CLEANUP_URL_FILTERS))) {
                final EsUrlFilterService urlFilterService = ComponentUtil.getComponent(EsUrlFilterService.class);
                try {
                    urlFilterService.delete(sid);
                } catch (final Exception e) {
                    logger.warn("Failed to delete url filters for {}", sid);
                }
            }

            // set paths
            split(pathsStr, "[\r\n]").of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).distinct().forEach(urlValue -> {
                if (!urlValue.startsWith("#")) {
                    final String u;
                    if (!fessConfig.isValidCrawlerFileProtocol(urlValue)) {
                        if (urlValue.startsWith("/")) {
                            u = "file:" + urlValue;
                        } else {
                            u = "file:/" + urlValue;
                        }
                    } else {
                        u = urlValue;
                    }
                    crawler.addUrl(u);
                    if (logger.isInfoEnabled()) {
                        logger.info("Target Path: {}", u);
                    }
                }
            }));

            // set included paths
            final AtomicBoolean urlEncodeDisabled = new AtomicBoolean(false);
            split(includedPathsStr, "[\r\n]").of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(line -> {
                if (!line.startsWith("#")) {
                    final String urlValue;
                    if (urlEncodeDisabled.get()) {
                        urlValue = line;
                        urlEncodeDisabled.set(false);
                    } else {
                        urlValue = systemHelper.encodeUrlFilter(line);
                    }
                    crawler.addIncludeFilter(urlValue);
                    if (logger.isInfoEnabled()) {
                        logger.info("Included Path: {}", urlValue);
                    }
                } else if (line.startsWith(DISABLE_URL_ENCODE)) {
                    urlEncodeDisabled.set(true);
                }
            }));

            // set excluded paths
            urlEncodeDisabled.set(false);
            split(excludedPathsStr, "[\r\n]").of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(line -> {
                if (!line.startsWith("#")) {
                    final String urlValue;
                    if (urlEncodeDisabled.get()) {
                        urlValue = line;
                        urlEncodeDisabled.set(false);
                    } else {
                        urlValue = systemHelper.encodeUrlFilter(line);
                    }
                    crawler.addExcludeFilter(urlValue);
                    if (logger.isInfoEnabled()) {
                        logger.info("Excluded Path: {}", urlValue);
                    }
                } else if (line.startsWith(DISABLE_URL_ENCODE)) {
                    urlEncodeDisabled.set(true);
                }
            }));

            // failure url
            final List<String> excludedUrlList = ComponentUtil.getCrawlingConfigHelper().getExcludedUrlList(fileConfig.getConfigId());
            if (excludedUrlList != null) {
                excludedUrlList.stream().filter(StringUtil::isNotBlank).map(String::trim).distinct().forEach(u -> {
                    final String urlValue = Pattern.quote(u);
                    crawler.addExcludeFilter(urlValue);
                    if (logger.isInfoEnabled()) {
                        logger.info("Excluded Path from failures: {}", urlValue);
                    }
                });
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Crawling {}", pathsStr);
            }

            crawler.setBackground(true);
            crawler.setThreadPriority(crawlerPriority);

            crawlerList.add(crawler);
            crawlerStatusList.add(Constants.READY);
        }

        // run index update
        final IndexUpdater indexUpdater = ComponentUtil.getIndexUpdater();
        indexUpdater.setName("IndexUpdater");
        indexUpdater.setPriority(indexUpdaterPriority);
        indexUpdater.setSessionIdList(sessionIdList);
        indexUpdater.setDaemon(true);
        indexUpdater.setCrawlerList(crawlerList);
        getAvailableBoostDocumentRuleList().forEach(rule -> {
            indexUpdater.addDocBoostMatcher(new org.codelibs.fess.indexer.DocBoostMatcher(rule));
        });
        indexUpdater.start();

        int startedCrawlerNum = 0;
        int activeCrawlerNum = 0;
        try {
            while (startedCrawlerNum < crawlerList.size()) {
                // Force to stop crawl
                if (systemHelper.isForceStop()) {
                    for (final Crawler crawler : crawlerList) {
                        crawler.stop();
                    }
                    break;
                }

                if (activeCrawlerNum < multiprocessCrawlingCount) {
                    // start crawling
                    crawlerList.get(startedCrawlerNum).execute();
                    crawlerStatusList.set(startedCrawlerNum, Constants.RUNNING);
                    startedCrawlerNum++;
                    activeCrawlerNum++;
                    ThreadUtil.sleep(crawlingExecutionInterval);
                    continue;
                }

                // check status
                for (int i = 0; i < startedCrawlerNum; i++) {
                    if (crawlerList.get(i).getCrawlerContext().getStatus() == CrawlerStatus.DONE
                            && Constants.RUNNING.equals(crawlerStatusList.get(i))) {
                        crawlerList.get(i).awaitTermination();
                        crawlerStatusList.set(i, Constants.DONE);
                        final String sid = crawlerList.get(i).getCrawlerContext().getSessionId();
                        indexUpdater.addFinishedSessionId(sid);
                        activeCrawlerNum--;
                    }
                }
                ThreadUtil.sleep(crawlingExecutionInterval);
            }

            boolean finishedAll = false;
            while (!finishedAll) {
                finishedAll = true;
                for (int i = 0; i < crawlerList.size(); i++) {
                    final Crawler crawler = crawlerList.get(i);
                    crawler.awaitTermination(crawlingExecutionInterval);
                    if (crawler.getCrawlerContext().getStatus() == CrawlerStatus.DONE && !Constants.DONE.equals(crawlerStatusList.get(i))) {
                        crawlerStatusList.set(i, Constants.DONE);
                        final String sid = crawler.getCrawlerContext().getSessionId();
                        indexUpdater.addFinishedSessionId(sid);
                        try {
                            crawler.close();
                        } catch (final Exception e) {
                            logger.warn("Failed to close the crawler.", e);
                        }
                    }
                    if (!Constants.DONE.equals(crawlerStatusList.get(i))) {
                        finishedAll = false;
                    }
                }
            }
        } finally {
            crawlerList.forEach(crawler -> {
                try {
                    crawler.close();
                } catch (final Exception e) {
                    logger.warn("Failed to close the crawler.", e);
                }
            });
        }
        crawlerList.clear();
        crawlerStatusList.clear();

        // put cralwing info
        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();

        final long execTime = System.currentTimeMillis() - startTime;
        crawlingInfoHelper.putToInfoMap(Constants.WEB_FS_CRAWLING_EXEC_TIME, Long.toString(execTime));
        if (logger.isInfoEnabled()) {
            logger.info("[EXEC TIME] crawling time: {}ms", execTime);
        }

        indexUpdater.setFinishCrawling(true);
        try {
            indexUpdater.join();
        } catch (final InterruptedException e) {
            logger.warn("Interrupted index update.", e);
        }

        crawlingInfoHelper.putToInfoMap(Constants.WEB_FS_INDEX_EXEC_TIME, Long.toString(indexUpdater.getExecuteTime()));
        crawlingInfoHelper.putToInfoMap(Constants.WEB_FS_INDEX_SIZE, Long.toString(indexUpdater.getDocumentSize()));

        if (systemHelper.isForceStop()) {
            return;
        }

        for (final String sid : sessionIdList) {
            // remove config
            ComponentUtil.getCrawlingConfigHelper().remove(sid);
            deleteCrawlData(sid);
        }
    }

    protected List<BoostDocumentRule> getAvailableBoostDocumentRuleList() {
        return ComponentUtil.getComponent(BoostDocumentRuleBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_SortOrder_Asc();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageDocboostMaxFetchSizeAsInteger());
        });
    }

    protected void deleteCrawlData(final String sid) {
        final EsUrlFilterService urlFilterService = ComponentUtil.getComponent(EsUrlFilterService.class);
        final EsUrlQueueService urlQueueService = ComponentUtil.getComponent(EsUrlQueueService.class);
        final EsDataService dataService = ComponentUtil.getComponent(EsDataService.class);

        try {
            // clear url filter
            urlFilterService.delete(sid);
        } catch (final Exception e) {
            logger.warn("Failed to delete UrlFilter for {}", sid, e);
        }

        try {
            // clear queue
            urlQueueService.clearCache();
            urlQueueService.delete(sid);
        } catch (final Exception e) {
            logger.warn("Failed to delete UrlQueue for {}", sid, e);
        }

        try {
            // clear
            dataService.delete(sid);
        } catch (final Exception e) {
            logger.warn("Failed to delete AccessResult for {}", sid, e);
        }
    }

    public void setMaxAccessCount(final long maxAccessCount) {
        this.maxAccessCount = maxAccessCount;
    }

    public void setCrawlingExecutionInterval(final long crawlingExecutionInterval) {
        this.crawlingExecutionInterval = crawlingExecutionInterval;
    }

    public void setIndexUpdaterPriority(final int indexUpdaterPriority) {
        this.indexUpdaterPriority = indexUpdaterPriority;
    }

    public void setCrawlerPriority(final int crawlerPriority) {
        this.crawlerPriority = crawlerPriority;
    }

}
