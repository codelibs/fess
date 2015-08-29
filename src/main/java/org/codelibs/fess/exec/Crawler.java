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
import org.codelibs.fess.app.service.CrawlingSessionService;
import org.codelibs.fess.app.service.PathMappingService;
import org.codelibs.fess.client.FessEsClient;
import org.codelibs.fess.helper.CrawlingSessionHelper;
import org.codelibs.fess.helper.DataIndexHelper;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.MailHelper;
import org.codelibs.fess.helper.OverlappingHostHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.WebFsIndexHelper;
import org.codelibs.fess.screenshot.ScreenShotManager;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ResourceUtil;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.SingletonLaContainer;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;

public class Crawler implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    private static final String WEB_FS_CRAWLING_PROCESS = "WebFsCrawler";

    private static final String DATA_CRAWLING_PROCESS = "DataStoreCrawler";

    private static final String MAIL_TEMPLATE_NAME = "crawler";

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    protected WebFsIndexHelper webFsIndexHelper;

    @Resource
    protected DataIndexHelper dataIndexHelper;

    @Resource
    protected PathMappingService pathMappingService;

    @Resource
    protected CrawlingSessionService crawlingSessionService;

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected MailHelper mailHelper;

    public String notificationSubject = "[FESS] Crawler completed";

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

        int exitCode;
        try {
            SingletonLaContainerFactory.setConfigPath("app.xml");
            SingletonLaContainerFactory.init();

            final LaContainer container = SingletonLaContainerFactory.getContainer();

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

        final CrawlingSessionHelper crawlingSessionHelper = ComponentUtil.getCrawlingSessionHelper();
        final DynamicProperties crawlerProperties = ComponentUtil.getCrawlerProperties();

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
                logger.warn("Failed to create crawler properties file.", e);
            }
        }

        try {
            crawlingSessionHelper.store(options.sessionId, true);
            final String dayForCleanupStr;
            if (StringUtil.isNotBlank(options.expires)) {
                dayForCleanupStr = options.expires;
            } else {
                dayForCleanupStr = crawlerProperties.getProperty(Constants.DAY_FOR_CLEANUP_PROPERTY, "1");
            }
            int dayForCleanup = -1;
            try {
                dayForCleanup = Integer.parseInt(dayForCleanupStr);
            } catch (final NumberFormatException e) {}
            crawlingSessionHelper.updateParams(options.sessionId, options.name, dayForCleanup);
        } catch (final Exception e) {
            logger.warn("Failed to store crawling information.", e);
        }

        try {
            return crawler.doCrawl(options);
        } finally {
            try {
                crawlingSessionHelper.store(options.sessionId, false);
            } catch (final Exception e) {
                logger.warn("Failed to store crawling information.", e);
            }

            final Map<String, String> infoMap = crawlingSessionHelper.getInfoMap(options.sessionId);

            final StringBuilder buf = new StringBuilder(500);
            for (final Map.Entry<String, String> entry : infoMap.entrySet()) {
                if (buf.length() != 0) {
                    buf.append(',');
                }
                buf.append(entry.getKey()).append('=').append(entry.getValue());
            }
            if (buf.length() != 0) {
                logger.info("[CRAWL INFO] " + buf.toString());
            }

            // notification
            try {
                crawler.sendMail(infoMap);
            } catch (final Exception e) {
                logger.warn("Failed to send a mail.", e);
            }

        }
    }

    public void sendMail(final Map<String, String> infoMap) {
        final String toStrs = (String) crawlerProperties.get(Constants.NOTIFICATION_TO_PROPERTY);
        if (StringUtil.isNotBlank(toStrs)) {
            final String[] toAddresses = toStrs.split(",");
            final Map<String, Object> dataMap = new HashMap<String, Object>();
            for (final Map.Entry<String, String> entry : infoMap.entrySet()) {
                dataMap.put(StringUtil.decapitalize(entry.getKey()), entry.getValue());
            }

            if (Constants.T.equals(infoMap.get(Constants.CRAWLER_STATUS))) {
                dataMap.put("success", true);
            }
            try {
                dataMap.put("hostname", InetAddress.getLocalHost().getHostAddress());
            } catch (final UnknownHostException e) {
                // ignore
            }

            final FileTemplateLoader loader = new FileTemplateLoader(new File(ResourceUtil.getMailTemplatePath(StringUtil.EMPTY)));
            final Handlebars handlebars = new Handlebars(loader);

            try {
                final Template template = handlebars.compile(MAIL_TEMPLATE_NAME);
                final Context hbsContext = Context.newContext(dataMap);
                final String body = template.apply(hbsContext);

                mailHelper.send(toAddresses, notificationSubject, body);
            } catch (final Exception e) {
                logger.warn("Failed to send the notification.", e);
            }
        }
    }

    public int doCrawl(final Options options) {
        if (logger.isInfoEnabled()) {
            logger.info("Starting Crawler..");
        }

        final PathMappingHelper pathMappingHelper = ComponentUtil.getPathMappingHelper();

        final long totalTime = System.currentTimeMillis();

        final CrawlingSessionHelper crawlingSessionHelper = ComponentUtil.getCrawlingSessionHelper();
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();

        boolean completed = false;
        int exitCode = Constants.EXIT_OK;
        try {
            writeTimeToSessionInfo(crawlingSessionHelper, Constants.CRAWLER_START_TIME);

            // setup path mapping
            final List<String> ptList = new ArrayList<>();
            ptList.add(Constants.PROCESS_TYPE_CRAWLING);
            ptList.add(Constants.PROCESS_TYPE_BOTH);
            pathMappingHelper.setPathMappingList(options.sessionId, pathMappingService.getPathMappingList(ptList));

            // overlapping host
            try {
                final OverlappingHostHelper overlappingHostHelper = ComponentUtil.getOverlappingHostHelper();
                overlappingHostHelper.init();
            } catch (final Exception e) {
                logger.warn("Could not initialize overlappingHostHelper.", e);
            }

            // delete expired sessions
            crawlingSessionService.deleteSessionIdsBefore(options.sessionId, options.name, ComponentUtil.getSystemHelper()
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
                        writeTimeToSessionInfo(crawlingSessionHelper, Constants.WEB_FS_CRAWLER_START_TIME);
                        webFsIndexHelper.crawl(options.sessionId, webConfigIdList, fileConfigIdList);
                        writeTimeToSessionInfo(crawlingSessionHelper, Constants.WEB_FS_CRAWLER_END_TIME);
                    }, WEB_FS_CRAWLING_PROCESS);
                webFsCrawlerThread.start();
            }

            if (runAll || dataConfigIdList != null) {
                dataCrawlerThread = new Thread((Runnable) () -> {
                    // crawl data system
                        writeTimeToSessionInfo(crawlingSessionHelper, Constants.DATA_CRAWLER_START_TIME);
                        dataIndexHelper.crawl(options.sessionId, dataConfigIdList);
                        writeTimeToSessionInfo(crawlingSessionHelper, Constants.DATA_CRAWLER_END_TIME);
                    }, DATA_CRAWLING_PROCESS);
                dataCrawlerThread.start();
            }

            joinCrawlerThread(webFsCrawlerThread);
            joinCrawlerThread(dataCrawlerThread);

            // clean up
            final QueryBuilder queryBuilder =
                    QueryBuilders.boolQuery().must(QueryBuilders.rangeQuery(fieldHelper.expiresField).to(new Date()))
                            .mustNot(QueryBuilders.termQuery(fieldHelper.segmentField, options.sessionId));
            try {
                fessEsClient.deleteByQuery(fieldHelper.docIndex, fieldHelper.docType, queryBuilder);
            } catch (final Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Could not delete expired sessions: " + queryBuilder.toString(), e);
                }
                exitCode = Constants.EXIT_FAIL;
            }

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
            crawlingSessionHelper.putToInfoMap(Constants.CRAWLER_STATUS, completed ? Constants.T.toString() : Constants.F.toString());
            writeTimeToSessionInfo(crawlingSessionHelper, Constants.CRAWLER_END_TIME);
            crawlingSessionHelper.putToInfoMap(Constants.CRAWLER_EXEC_TIME, Long.toString(System.currentTimeMillis() - totalTime));

        }
    }

    private void writeTimeToSessionInfo(final CrawlingSessionHelper crawlingSessionHelper, final String key) {
        if (crawlingSessionHelper != null) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            crawlingSessionHelper.putToInfoMap(key, dateFormat.format(new Date()));
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
