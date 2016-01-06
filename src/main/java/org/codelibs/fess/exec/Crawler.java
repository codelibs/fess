/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.exec;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.CrawlingInfoService;
import org.codelibs.fess.app.service.PathMappingService;
import org.codelibs.fess.crawler.client.EsClient;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.DataIndexHelper;
import org.codelibs.fess.helper.DuplicateHostHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.WebFsIndexHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.mail.CrawlerPostcard;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.lastaflute.core.mail.Postbox;
import org.lastaflute.di.core.SingletonLaContainer;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Crawler implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    private static final String WEB_FS_CRAWLING_PROCESS = "WebFsCrawler";

    private static final String DATA_CRAWLING_PROCESS = "DataStoreCrawler";

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    protected WebFsIndexHelper webFsIndexHelper;

    @Resource
    protected DataIndexHelper dataIndexHelper;

    @Resource
    protected PathMappingService pathMappingService;

    @Resource
    protected CrawlingInfoService crawlingInfoService;

    @Resource
    protected DynamicProperties crawlerProperties;

    protected static class Options {

        @Option(name = "-s", aliases = "--sessionId", metaVar = "sessionId", usage = "Session ID")
        protected String sessionId;

        @Option(name = "-n", aliases = "--name", metaVar = "name", usage = "Name")
        protected String name;

        @Option(name = "-w", aliases = "--webConfigIds", metaVar = "webConfigIds", usage = "Web Config IDs")
        protected String webConfigIds;

        @Option(name = "-f", aliases = "--fileConfigIds", metaVar = "fileConfigIds", usage = "File Config IDs")
        protected String fileConfigIds;

        @Option(name = "-d", aliases = "--dataConfigIds", metaVar = "dataConfigIds", usage = "Data Config IDs")
        protected String dataConfigIds;

        @Option(name = "-p", aliases = "--properties", metaVar = "properties", usage = "Properties File")
        protected String propertiesPath;

        @Option(name = "-e", aliases = "--expires", metaVar = "expires", usage = "Expires for documents")
        protected String expires;

        protected Options() {
            // noghing
        }

        protected List<String> getWebConfigIdList() {
            if (StringUtil.isNotBlank(webConfigIds)) {
                final String[] values = webConfigIds.split(",");
                return createConfigIdList(values);
            }
            return null;
        }

        protected List<String> getFileConfigIdList() {
            if (StringUtil.isNotBlank(fileConfigIds)) {
                final String[] values = fileConfigIds.split(",");
                return createConfigIdList(values);
            }
            return null;
        }

        protected List<String> getDataConfigIdList() {
            if (StringUtil.isNotBlank(dataConfigIds)) {
                final String[] values = dataConfigIds.split(",");
                return createConfigIdList(values);
            }
            return null;
        }

        private static List<String> createConfigIdList(final String[] values) {
            final List<String> idList = new ArrayList<>();
            for (final String value : values) {
                idList.add(value);
            }
            return idList;
        }

    }

    public static void main(final String[] args) {
        final Options options = new Options();

        final CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch (final CmdLineException e) {
            System.err.println(e.getMessage()); // NOPMD
            System.err.println("java " + Crawler.class.getCanonicalName() // NOPMD
                    + " [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }

        final String transportAddresses = System.getProperty(Constants.FESS_ES_TRANSPORT_ADDRESSES);
        if (StringUtil.isNotBlank(transportAddresses)) {
            System.setProperty(EsClient.TRANSPORT_ADDRESSES, transportAddresses);
        }
        final String clusterName = System.getProperty(Constants.FESS_ES_CLUSTER_NAME);
        if (StringUtil.isNotBlank(clusterName)) {
            System.setProperty(EsClient.CLUSTER_NAME, clusterName);
        }

        int exitCode;
        try {
            SingletonLaContainerFactory.setConfigPath("app.xml");
            SingletonLaContainerFactory.init();

            final Thread shutdownCallback = new Thread("ShutdownHook") {
                @Override
                public void run() {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Destroying LaContainer..");
                    }
                    SingletonLaContainerFactory.destroy();
                }
            };
            Runtime.getRuntime().addShutdownHook(shutdownCallback);

            exitCode = process(options);
        } catch (final Throwable t) { // NOPMD
            logger.error("Crawler does not work correctly.", t);
            exitCode = Constants.EXIT_FAIL;
        } finally {
            SingletonLaContainerFactory.destroy();
        }

        if (exitCode != Constants.EXIT_OK) {
            System.exit(exitCode);
        }
    }

    private static int process(final Options options) {
        final Crawler crawler = SingletonLaContainer.getComponent(Crawler.class);

        if (StringUtil.isBlank(options.sessionId)) {
            // use a default session id
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            options.sessionId = sdf.format(new Date());
        }

        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();
        final DynamicProperties crawlerProperties = ComponentUtil.getSystemProperties();

        if (StringUtil.isNotBlank(options.propertiesPath)) {
            crawlerProperties.reload(options.propertiesPath);
        } else {
            try {
                final File propFile = File.createTempFile("crawler_", ".properties");
                if (propFile.delete() && logger.isDebugEnabled()) {
                    logger.debug("Deleted a temp file: " + propFile.getAbsolutePath());
                }
                crawlerProperties.reload(propFile.getAbsolutePath());
                propFile.deleteOnExit(); // NOSONAR
            } catch (final IOException e) {
                logger.warn("Failed to create system properties file.", e);
            }
        }

        try {
            crawlingInfoHelper.store(options.sessionId, true);
            final String dayForCleanupStr;
            if (StringUtil.isNotBlank(options.expires)) {
                dayForCleanupStr = options.expires;
            } else {
                dayForCleanupStr =
                        crawlerProperties.getProperty(Constants.DAY_FOR_CLEANUP_PROPERTY, Constants.DEFAULT_DAY_FOR_CLEANUP.toString());
            }
            int dayForCleanup = -1;
            try {
                dayForCleanup = Integer.parseInt(dayForCleanupStr);
            } catch (final NumberFormatException e) {}
            crawlingInfoHelper.updateParams(options.sessionId, options.name, dayForCleanup);
        } catch (final Exception e) {
            logger.warn("Failed to store crawling information.", e);
        }

        try {
            return crawler.doCrawl(options);
        } finally {
            try {
                crawlingInfoHelper.store(options.sessionId, false);
            } catch (final Exception e) {
                logger.warn("Failed to store crawling information.", e);
            }

            final Map<String, String> infoMap = crawlingInfoHelper.getInfoMap(options.sessionId);

            final StringBuilder buf = new StringBuilder(500);
            for (final Map.Entry<String, String> entry : infoMap.entrySet()) {
                if (buf.length() != 0) {
                    buf.append(',');
                }
                buf.append(entry.getKey()).append('=').append(entry.getValue());
            }
            logger.info("[CRAWL INFO] " + buf.toString());

            // notification
            try {
                crawler.sendMail(infoMap);
            } catch (final Exception e) {
                logger.warn("Failed to send a mail.", e);
            }

        }
    }

    protected void sendMail(final Map<String, String> infoMap) {
        final String toStrs = (String) crawlerProperties.get(Constants.NOTIFICATION_TO_PROPERTY);
        if (StringUtil.isNotBlank(toStrs)) {
            final String[] toAddresses = toStrs.split(",");
            final Map<String, String> dataMap = new HashMap<>();
            for (final Map.Entry<String, String> entry : infoMap.entrySet()) {
                dataMap.put(StringUtil.decapitalize(entry.getKey()), entry.getValue());
            }

            try {
                dataMap.put("hostname", InetAddress.getLocalHost().getHostAddress());
            } catch (final UnknownHostException e) {
                // ignore
            }

            logger.debug("\ninfoMap: {}\ndataMap: {}", infoMap, dataMap);

            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final Postbox postbox = ComponentUtil.getComponent(Postbox.class);
            CrawlerPostcard.droppedInto(postbox, postcard -> {
                postcard.setFrom(fessConfig.getMailFromAddress(), fessConfig.getMailFromName());
                postcard.addReplyTo(fessConfig.getMailReturnPath());
                StreamUtil.of(toAddresses).forEach(address -> {
                    postcard.addTo(address);
                });
                postcard.setCrawlerEndTime(getValueFromMap(dataMap, "crawlerEndTime", StringUtil.EMPTY));
                postcard.setCrawlerExecTime(getValueFromMap(dataMap, "crawlerExecTime", "0"));
                postcard.setCrawlerStartTime(getValueFromMap(dataMap, "crawlerStartTime", StringUtil.EMPTY));
                postcard.setDataCrawlEndTime(getValueFromMap(dataMap, "dataCrawlEndTime", StringUtil.EMPTY));
                postcard.setDataCrawlExecTime(getValueFromMap(dataMap, "dataCrawlExecTime", "0"));
                postcard.setDataCrawlStartTime(getValueFromMap(dataMap, "dataCrawlStartTime", StringUtil.EMPTY));
                postcard.setDataFsIndexSize(getValueFromMap(dataMap, "dataFsIndexSize", "0"));
                postcard.setDataIndexExecTime(getValueFromMap(dataMap, "dataIndexExecTime", "0"));
                postcard.setHostname(getValueFromMap(dataMap, "hostname", StringUtil.EMPTY));
                postcard.setWebFsCrawlEndTime(getValueFromMap(dataMap, "webFsCrawlEndTime", StringUtil.EMPTY));
                postcard.setWebFsCrawlExecTime(getValueFromMap(dataMap, "webFsCrawlExecTime", "0"));
                postcard.setWebFsCrawlStartTime(getValueFromMap(dataMap, "webFsCrawlStartTime", StringUtil.EMPTY));
                postcard.setWebFsIndexExecTime(getValueFromMap(dataMap, "webFsIndexExecTime", "0"));
                postcard.setWebFsIndexSize(getValueFromMap(dataMap, "webFsIndexSize", "0"));
                if (Constants.TRUE.equalsIgnoreCase(infoMap.get(Constants.CRAWLER_STATUS))) {
                    postcard.setStatus(Constants.OK);
                } else {
                    postcard.setStatus(Constants.FAIL);
                }
            });
        }
    }

    private String getValueFromMap(Map<String, String> dataMap, String key, String defaultValue) {
        String value = dataMap.get(key);
        if (StringUtil.isBlank(value)) {
            return defaultValue;
        }
        return value;
    }

    public int doCrawl(final Options options) {
        if (logger.isInfoEnabled()) {
            logger.info("Starting Crawler..");
        }

        final PathMappingHelper pathMappingHelper = ComponentUtil.getPathMappingHelper();

        final long totalTime = System.currentTimeMillis();

        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();

        boolean completed = false;
        final int exitCode = Constants.EXIT_OK;
        try {
            writeTimeToSessionInfo(crawlingInfoHelper, Constants.CRAWLER_START_TIME);

            // setup path mapping
            final List<String> ptList = new ArrayList<>();
            ptList.add(Constants.PROCESS_TYPE_CRAWLING);
            ptList.add(Constants.PROCESS_TYPE_BOTH);
            pathMappingHelper.setPathMappingList(options.sessionId, pathMappingService.getPathMappingList(ptList));

            // duplicate host
            try {
                final DuplicateHostHelper duplicateHostHelper = ComponentUtil.getDuplicateHostHelper();
                duplicateHostHelper.init();
            } catch (final Exception e) {
                logger.warn("Could not initialize duplicateHostHelper.", e);
            }

            // delete expired sessions
            crawlingInfoService.deleteSessionIdsBefore(options.sessionId, options.name, ComponentUtil.getSystemHelper()
                    .getCurrentTimeAsLong());

            final List<String> webConfigIdList = options.getWebConfigIdList();
            final List<String> fileConfigIdList = options.getFileConfigIdList();
            final List<String> dataConfigIdList = options.getDataConfigIdList();
            final boolean runAll = webConfigIdList == null && fileConfigIdList == null && dataConfigIdList == null;

            Thread webFsCrawlerThread = null;
            Thread dataCrawlerThread = null;

            if (runAll || webConfigIdList != null || fileConfigIdList != null) {
                webFsCrawlerThread = new Thread((Runnable) () -> {
                    // crawl web
                        writeTimeToSessionInfo(crawlingInfoHelper, Constants.WEB_FS_CRAWLER_START_TIME);
                        webFsIndexHelper.crawl(options.sessionId, webConfigIdList, fileConfigIdList);
                        writeTimeToSessionInfo(crawlingInfoHelper, Constants.WEB_FS_CRAWLER_END_TIME);
                    }, WEB_FS_CRAWLING_PROCESS);
                webFsCrawlerThread.start();
            }

            if (runAll || dataConfigIdList != null) {
                dataCrawlerThread = new Thread((Runnable) () -> {
                    // crawl data system
                        writeTimeToSessionInfo(crawlingInfoHelper, Constants.DATA_CRAWLER_START_TIME);
                        dataIndexHelper.crawl(options.sessionId, dataConfigIdList);
                        writeTimeToSessionInfo(crawlingInfoHelper, Constants.DATA_CRAWLER_END_TIME);
                    }, DATA_CRAWLING_PROCESS);
                dataCrawlerThread.start();
            }

            joinCrawlerThread(webFsCrawlerThread);
            joinCrawlerThread(dataCrawlerThread);

            if (logger.isInfoEnabled()) {
                logger.info("Finished Crawler");
            }
            completed = true;

            return exitCode;
        } catch (final Throwable t) { // NOPMD
            logger.warn("An exception occurs on the crawl task.", t);
            return Constants.EXIT_FAIL;
        } finally {
            pathMappingHelper.removePathMappingList(options.sessionId);
            crawlingInfoHelper.putToInfoMap(Constants.CRAWLER_STATUS, completed ? Constants.T.toString() : Constants.F.toString());
            writeTimeToSessionInfo(crawlingInfoHelper, Constants.CRAWLER_END_TIME);
            crawlingInfoHelper.putToInfoMap(Constants.CRAWLER_EXEC_TIME, Long.toString(System.currentTimeMillis() - totalTime));

        }
    }

    private void writeTimeToSessionInfo(final CrawlingInfoHelper crawlingInfoHelper, final String key) {
        if (crawlingInfoHelper != null) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            crawlingInfoHelper.putToInfoMap(key, dateFormat.format(new Date()));
        }
    }

    private void joinCrawlerThread(final Thread crawlerThread) {
        if (crawlerThread != null) {
            try {
                crawlerThread.join();
            } catch (final Exception e) {
                logger.info("Interrupted a crawling process: " + crawlerThread.getName());
            }
        }
    }
}
