/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
import org.codelibs.fess.crawler.service.impl.OpenSearchDataService;
import org.codelibs.fess.crawler.service.impl.OpenSearchUrlFilterService;
import org.codelibs.fess.crawler.service.impl.OpenSearchUrlQueueService;
import org.codelibs.fess.indexer.IndexUpdater;
import org.codelibs.fess.opensearch.config.exbhv.BoostDocumentRuleBhv;
import org.codelibs.fess.opensearch.config.exentity.BoostDocumentRule;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.Param.Config;
import org.codelibs.fess.opensearch.config.exentity.FileConfig;
import org.codelibs.fess.opensearch.config.exentity.WebConfig;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Helper class for web and file system crawling and indexing operations.
 * Manages the crawling process for both web configurations and file configurations,
 * coordinating multiple crawler threads and handling indexing operations.
 */
public class WebFsIndexHelper {

    /**
     * Default constructor.
     */
    public WebFsIndexHelper() {
        // Default constructor
    }

    private static final Logger logger = LogManager.getLogger(WebFsIndexHelper.class);

    private static final String DISABLE_URL_ENCODE = "#DISABLE_URL_ENCODE";

    /**
     * Maximum number of URLs to access during crawling.
     */
    protected long maxAccessCount = Long.MAX_VALUE;

    /**
     * Interval time in milliseconds between crawling executions.
     */
    protected long crawlingExecutionInterval = Constants.DEFAULT_CRAWLING_EXECUTION_INTERVAL;

    /**
     * Thread priority for index updater operations.
     */
    protected int indexUpdaterPriority = Thread.MAX_PRIORITY;

    /**
     * Thread priority for crawler operations.
     */
    protected int crawlerPriority = Thread.NORM_PRIORITY;

    /**
     * Synchronized list of active crawlers.
     */
    protected final List<Crawler> crawlerList = Collections.synchronizedList(new ArrayList<>());

    /**
     * Initiates crawling for specified web and file configurations.
     *
     * @param sessionId The session ID for this crawling operation
     * @param webConfigIdList List of web configuration IDs to crawl, null for all
     * @param fileConfigIdList List of file configuration IDs to crawl, null for all
     */
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

    /**
     * Performs the actual crawling operation for the provided configurations.
     *
     * @param sessionId The session ID for this crawling operation
     * @param webConfigList List of web configurations to crawl
     * @param fileConfigList List of file configurations to crawl
     */
    protected void doCrawl(final String sessionId, final List<WebConfig> webConfigList, final List<FileConfig> fileConfigList) {
        final int multiprocessCrawlingCount = ComponentUtil.getFessConfig().getCrawlingThreadCount();

        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final ProtocolHelper protocolHelper = ComponentUtil.getProtocolHelper();

        final long startTime = systemHelper.getCurrentTimeAsLong();

        final List<String> sessionIdList = new ArrayList<>();
        crawlerList.clear();
        final List<String> crawlerStatusList = new ArrayList<>();

        // Web
        for (final WebConfig webConfig : webConfigList) {
            setupWebCrawler(sessionId, webConfig, sessionIdList, crawlerStatusList, systemHelper, protocolHelper);
        }

        // File
        for (final FileConfig fileConfig : fileConfigList) {
            setupFileCrawler(sessionId, fileConfig, sessionIdList, crawlerStatusList, systemHelper, protocolHelper);
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

        // Execute and monitor crawlers
        executeCrawlers(crawlerStatusList, multiprocessCrawlingCount, systemHelper, indexUpdater);

        // put crawling info
        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();

        final long execTime = systemHelper.getCurrentTimeAsLong() - startTime;
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

    /**
     * Gets the list of available boost document rules.
     *
     * @return List of boost document rules that are currently available
     */
    protected List<BoostDocumentRule> getAvailableBoostDocumentRuleList() {
        return ComponentUtil.getComponent(BoostDocumentRuleBhv.class).selectList(cb -> {
            cb.query().matchAll();
            cb.query().addOrderBy_SortOrder_Asc();
            cb.fetchFirst(ComponentUtil.getFessConfig().getPageDocboostMaxFetchSizeAsInteger());
        });
    }

    /**
     * Deletes crawl data for the specified session ID.
     *
     * @param sid The session ID whose crawl data should be deleted
     */
    protected void deleteCrawlData(final String sid) {
        final OpenSearchUrlFilterService urlFilterService = ComponentUtil.getComponent(OpenSearchUrlFilterService.class);
        final OpenSearchUrlQueueService urlQueueService = ComponentUtil.getComponent(OpenSearchUrlQueueService.class);
        final OpenSearchDataService dataService = ComponentUtil.getComponent(OpenSearchDataService.class);

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

    /**
     * Sets the maximum number of URLs to access during crawling.
     *
     * @param maxAccessCount The maximum access count
     */
    public void setMaxAccessCount(final long maxAccessCount) {
        this.maxAccessCount = maxAccessCount;
    }

    /**
     * Sets the interval time between crawling executions.
     *
     * @param crawlingExecutionInterval The crawling execution interval in milliseconds
     */
    public void setCrawlingExecutionInterval(final long crawlingExecutionInterval) {
        this.crawlingExecutionInterval = crawlingExecutionInterval;
    }

    /**
     * Sets the thread priority for index updater operations.
     *
     * @param indexUpdaterPriority The index updater thread priority
     */
    public void setIndexUpdaterPriority(final int indexUpdaterPriority) {
        this.indexUpdaterPriority = indexUpdaterPriority;
    }

    /**
     * Sets the thread priority for crawler operations.
     *
     * @param crawlerPriority The crawler thread priority
     */
    public void setCrawlerPriority(final int crawlerPriority) {
        this.crawlerPriority = crawlerPriority;
    }

    /**
     * Executes and monitors multiple crawlers.
     *
     * @param crawlerStatusList List of crawler statuses
     * @param multiprocessCrawlingCount Maximum number of concurrent crawlers
     * @param systemHelper The system helper
     * @param indexUpdater The index updater
     */
    protected void executeCrawlers(final List<String> crawlerStatusList, final int multiprocessCrawlingCount,
            final SystemHelper systemHelper, final IndexUpdater indexUpdater) {
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
    }

    /**
     * Sets up a web crawler for the given web configuration.
     *
     * @param sessionId The session ID for this crawling operation
     * @param webConfig The web configuration to process
     * @param sessionIdList List to add the session ID to
     * @param crawlerStatusList List to add the crawler status to
     * @param systemHelper The system helper
     * @param protocolHelper The protocol helper
     */
    protected void setupWebCrawler(final String sessionId, final WebConfig webConfig, final List<String> sessionIdList,
            final List<String> crawlerStatusList, final SystemHelper systemHelper, final ProtocolHelper protocolHelper) {
        final String urlsStr = webConfig.getUrls();
        if (StringUtil.isBlank(urlsStr)) {
            logger.warn("No target urls for web config {}. Skipped", webConfig.getName());
            return;
        }

        final String sid = ComponentUtil.getCrawlingConfigHelper().store(sessionId, webConfig);

        // create crawler
        final Crawler crawler = ComponentUtil.getComponent(Crawler.class);
        crawler.setSessionId(sid);
        sessionIdList.add(sid);

        final String includedUrlsStr = webConfig.getIncludedUrls() != null ? webConfig.getIncludedUrls() : StringUtil.EMPTY;
        final String excludedUrlsStr = webConfig.getExcludedUrls() != null ? webConfig.getExcludedUrls() : StringUtil.EMPTY;

        // Configure crawler settings
        final int intervalTime =
                webConfig.getIntervalTime() != null ? webConfig.getIntervalTime() : Constants.DEFAULT_INTERVAL_TIME_FOR_WEB;
        final int numOfThread =
                webConfig.getNumOfThread() != null ? webConfig.getNumOfThread() : Constants.DEFAULT_NUM_OF_THREAD_FOR_WEB;
        final int depth = webConfig.getDepth() != null ? webConfig.getDepth() : -1;
        final long maxCount = webConfig.getMaxAccessCount() != null ? webConfig.getMaxAccessCount() : maxAccessCount;
        configureCrawler(crawler, intervalTime, numOfThread, depth, maxCount);

        webConfig.initializeClientFactory(() -> crawler.getClientFactory());
        final Map<String, String> configParamMap = webConfig.getConfigParameterMap(ConfigName.CONFIG);

        // Handle cleanup configuration
        handleCleanupConfig(sid, configParamMap);

        final DuplicateHostHelper duplicateHostHelper = ComponentUtil.getDuplicateHostHelper();

        // set urls
        split(urlsStr, "[\r\n]").of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).distinct().forEach(urlValue -> {
            if (!urlValue.startsWith("#") && protocolHelper.isValidWebProtocol(urlValue)) {
                final String u = duplicateHostHelper.convert(urlValue);
                crawler.addUrl(u);
                if (logger.isInfoEnabled()) {
                    logger.info("Target URL: {}", u);
                }
            }
        }));

        // set included urls
        processFilters(includedUrlsStr, crawler, systemHelper, true, "URL");

        // set excluded urls
        processFilters(excludedUrlsStr, crawler, systemHelper, false, "URL");

        // failure url
        addFailureExclusionFilters(crawler, webConfig.getConfigId(), "URL");

        if (logger.isDebugEnabled()) {
            logger.debug("Crawling {}", urlsStr);
        }

        crawlerList.add(crawler);
        crawlerStatusList.add(Constants.READY);
    }

    /**
     * Sets up a file system crawler for the given file configuration.
     *
     * @param sessionId The session ID for this crawling operation
     * @param fileConfig The file configuration to process
     * @param sessionIdList List to add the session ID to
     * @param crawlerStatusList List to add the crawler status to
     * @param systemHelper The system helper
     * @param protocolHelper The protocol helper
     */
    protected void setupFileCrawler(final String sessionId, final FileConfig fileConfig, final List<String> sessionIdList,
            final List<String> crawlerStatusList, final SystemHelper systemHelper, final ProtocolHelper protocolHelper) {
        final String pathsStr = fileConfig.getPaths();
        if (StringUtil.isBlank(pathsStr)) {
            logger.warn("No target paths for file config {}. Skipped", fileConfig.getName());
            return;
        }

        final String sid = ComponentUtil.getCrawlingConfigHelper().store(sessionId, fileConfig);

        // create crawler
        final Crawler crawler = ComponentUtil.getComponent(Crawler.class);
        crawler.setSessionId(sid);
        sessionIdList.add(sid);

        final String includedPathsStr = fileConfig.getIncludedPaths() != null ? fileConfig.getIncludedPaths() : StringUtil.EMPTY;
        final String excludedPathsStr = fileConfig.getExcludedPaths() != null ? fileConfig.getExcludedPaths() : StringUtil.EMPTY;

        // Configure crawler settings
        final int intervalTime =
                fileConfig.getIntervalTime() != null ? fileConfig.getIntervalTime() : Constants.DEFAULT_INTERVAL_TIME_FOR_FS;
        final int numOfThread =
                fileConfig.getNumOfThread() != null ? fileConfig.getNumOfThread() : Constants.DEFAULT_NUM_OF_THREAD_FOR_FS;
        final int depth = fileConfig.getDepth() != null ? fileConfig.getDepth() : -1;
        final long maxCount = fileConfig.getMaxAccessCount() != null ? fileConfig.getMaxAccessCount() : maxAccessCount;
        configureCrawler(crawler, intervalTime, numOfThread, depth, maxCount);

        fileConfig.initializeClientFactory(() -> crawler.getClientFactory());
        final Map<String, String> configParamMap = fileConfig.getConfigParameterMap(ConfigName.CONFIG);

        // Handle cleanup configuration
        handleCleanupConfig(sid, configParamMap);

        // set paths
        split(pathsStr, "[\r\n]").of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).distinct().forEach(urlValue -> {
            if (!urlValue.startsWith("#")) {
                final String u;
                if (!protocolHelper.isValidFileProtocol(urlValue)) {
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
        processFilters(includedPathsStr, crawler, systemHelper, true, "Path");

        // set excluded paths
        processFilters(excludedPathsStr, crawler, systemHelper, false, "Path");

        // failure url
        addFailureExclusionFilters(crawler, fileConfig.getConfigId(), "Path");

        if (logger.isDebugEnabled()) {
            logger.debug("Crawling {}", pathsStr);
        }

        crawlerList.add(crawler);
        crawlerStatusList.add(Constants.READY);
    }

    /**
     * Configures basic crawler settings.
     *
     * @param crawler The crawler to configure
     * @param intervalTime The interval time in milliseconds
     * @param numOfThread The number of threads
     * @param depth The maximum depth (-1 for unlimited)
     * @param maxCount The maximum access count
     */
    protected void configureCrawler(final Crawler crawler, final int intervalTime, final int numOfThread, final int depth,
            final long maxCount) {
        // interval time
        ((FessIntervalController) crawler.getIntervalController()).setDelayMillisForWaitingNewUrl(intervalTime);

        // num of threads
        final CrawlerContext crawlerContext = crawler.getCrawlerContext();
        crawlerContext.setNumOfThread(numOfThread);

        // depth
        crawlerContext.setMaxDepth(depth);

        // max count
        crawlerContext.setMaxAccessCount(maxCount);

        crawler.setBackground(true);
        crawler.setThreadPriority(crawlerPriority);
    }

    /**
     * Handles cleanup configuration based on config parameters.
     *
     * @param sid The session ID
     * @param configParamMap The configuration parameter map
     */
    protected void handleCleanupConfig(final String sid, final Map<String, String> configParamMap) {
        if (Constants.TRUE.equalsIgnoreCase(configParamMap.get(Config.CLEANUP_ALL))) {
            deleteCrawlData(sid);
        } else if (Constants.TRUE.equalsIgnoreCase(configParamMap.get(Config.CLEANUP_URL_FILTERS))) {
            final OpenSearchUrlFilterService urlFilterService = ComponentUtil.getComponent(OpenSearchUrlFilterService.class);
            try {
                urlFilterService.delete(sid);
            } catch (final Exception e) {
                logger.warn("Failed to delete url filters for {}", sid, e);
            }
        }
    }

    /**
     * Processes filter lines and adds them to the crawler.
     * Handles URL encoding and the #DISABLE_URL_ENCODE directive.
     *
     * @param filterStr The filter string containing newline-separated filters
     * @param crawler The crawler to add filters to
     * @param systemHelper The system helper for URL encoding
     * @param isIncludeFilter True if adding include filters, false for exclude filters
     * @param filterType Description of filter type for logging (e.g., "URL", "Path")
     */
    protected void processFilters(final String filterStr, final Crawler crawler, final SystemHelper systemHelper,
            final boolean isIncludeFilter, final String filterType) {
        if (StringUtil.isBlank(filterStr)) {
            return;
        }

        final AtomicBoolean urlEncodeDisabled = new AtomicBoolean(false);
        split(filterStr, "[\r\n]").of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(line -> {
            if (line.startsWith(DISABLE_URL_ENCODE)) {
                urlEncodeDisabled.set(true);
            } else if (!line.startsWith("#")) {
                final String filterValue;
                if (urlEncodeDisabled.get()) {
                    filterValue = line;
                    urlEncodeDisabled.set(false);
                } else {
                    filterValue = systemHelper.encodeUrlFilter(line);
                }

                if (isIncludeFilter) {
                    crawler.addIncludeFilter(filterValue);
                } else {
                    crawler.addExcludeFilter(filterValue);
                }

                if (logger.isInfoEnabled()) {
                    final String action = isIncludeFilter ? "Included" : "Excluded";
                    logger.info("{} {}: {}", action, filterType, filterValue);
                }
            }
        }));
    }

    /**
     * Adds failure URL/Path exclusion filters to the crawler.
     *
     * @param crawler The crawler to add filters to
     * @param configId The configuration ID to get excluded URLs for
     * @param filterType Description of filter type for logging (e.g., "URL", "Path")
     */
    protected void addFailureExclusionFilters(final Crawler crawler, final String configId, final String filterType) {
        final List<String> excludedUrlList = ComponentUtil.getCrawlingConfigHelper().getExcludedUrlList(configId);
        if (excludedUrlList != null) {
            excludedUrlList.stream().filter(StringUtil::isNotBlank).map(String::trim).distinct().forEach(u -> {
                final String filterValue = Pattern.quote(u);
                crawler.addExcludeFilter(filterValue);
                if (logger.isInfoEnabled()) {
                    logger.info("Excluded {} from failures: {}", filterType, filterValue);
                }
            });
        }
    }

}
