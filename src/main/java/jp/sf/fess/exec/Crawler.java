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

package jp.sf.fess.exec;

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
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;
import jp.sf.fess.db.allcommon.CDef;
import jp.sf.fess.helper.CrawlingSessionHelper;
import jp.sf.fess.helper.DataIndexHelper;
import jp.sf.fess.helper.DatabaseHelper;
import jp.sf.fess.helper.MailHelper;
import jp.sf.fess.helper.OverlappingHostHelper;
import jp.sf.fess.helper.PathMappingHelper;
import jp.sf.fess.helper.WebFsIndexHelper;
import jp.sf.fess.screenshot.ScreenShotManager;
import jp.sf.fess.service.CrawlingSessionService;
import jp.sf.fess.service.PathMappingService;
import jp.sf.fess.util.ResourceUtil;

import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.solr.lib.SolrGroup;
import org.codelibs.solr.lib.SolrGroupManager;
import org.codelibs.solr.lib.policy.QueryType;
import org.codelibs.solr.lib.policy.StatusPolicy;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.mobylet.core.launcher.MobyletLauncher;
import org.seasar.framework.container.ExternalContext;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.container.servlet.SingletonS2ContainerInitializer;
import org.seasar.framework.mock.servlet.MockHttpServletRequestImpl;
import org.seasar.framework.mock.servlet.MockHttpServletResponseImpl;
import org.seasar.framework.mock.servlet.MockServletContextImpl;
import org.seasar.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;

public class Crawler implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    private static final String WEB_CRAWLING_PROCESS = "Web Crawling Process";

    private static final String DATA_CRAWLING_PROCESS = "Data Crawling Process";

    private static final String MAIL_TEMPLATE_NAME = "crawler";

    @Resource
    protected SolrGroupManager solrGroupManager;

    @Binding(bindingType = BindingType.MAY)
    @Resource
    protected ScreenShotManager screenShotManager;

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

        @Option(name = "-o", aliases = "--operation", metaVar = "operation", usage = "Opration when crawlwer is finised")
        protected String operation;

        protected Options() {
            // noghing
        }

        protected List<Long> getWebConfigIdList() {
            if (StringUtil.isNotBlank(webConfigIds)) {
                final String[] values = webConfigIds.split(",");
                return createConfigIdList(values);
            }
            return null;
        }

        protected List<Long> getFileConfigIdList() {
            if (StringUtil.isNotBlank(fileConfigIds)) {
                final String[] values = fileConfigIds.split(",");
                return createConfigIdList(values);
            }
            return null;
        }

        protected List<Long> getDataConfigIdList() {
            if (StringUtil.isNotBlank(dataConfigIds)) {
                final String[] values = dataConfigIds.split(",");
                return createConfigIdList(values);
            }
            return null;
        }

        private static List<Long> createConfigIdList(final String[] values) {
            final List<Long> idList = new ArrayList<Long>();
            for (final String value : values) {
                final long id = Long.valueOf(value);
                if (id > 0) {
                    idList.add(id);
                }
            }
            return idList;
        }

        public boolean isOptimize() {
            return Constants.OPTIMIZE.equalsIgnoreCase(operation);
        }

        public boolean isCommit() {
            return Constants.COMMIT.equalsIgnoreCase(operation);
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

        final ServletContext servletContext = new MockServletContextImpl(
                "/fess");
        final HttpServletRequest request = new MockHttpServletRequestImpl(
                servletContext, "/crawler");
        final HttpServletResponse response = new MockHttpServletResponseImpl(
                request);
        final SingletonS2ContainerInitializer initializer = new SingletonS2ContainerInitializer();
        initializer.setConfigPath("app.dicon");
        initializer.setApplication(servletContext);
        initializer.initialize();

        final S2Container container = SingletonS2ContainerFactory
                .getContainer();
        final ExternalContext externalContext = container.getExternalContext();
        externalContext.setRequest(request);
        externalContext.setResponse(response);

        final Thread shutdownCallback = new Thread("ShutdownHook") {
            @Override
            public void run() {
                if (logger.isDebugEnabled()) {
                    logger.debug("Destroying S2Container..");
                }
                SingletonS2ContainerFactory.destroy();
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownCallback);

        int exitCode;
        try {
            exitCode = process(options);
        } catch (final Throwable t) {
            logger.error("Crawler does not work correctly.", t);
            exitCode = Constants.EXIT_FAIL;
        } finally {
            SingletonS2ContainerFactory.destroy();
        }

        if (exitCode != Constants.EXIT_OK) {
            System.exit(exitCode);
        }
    }

    private static int process(final Options options) {
        // initialize mobylet
        MobyletLauncher.launch();

        final Crawler crawler = SingletonS2Container
                .getComponent(Crawler.class);

        final DatabaseHelper databaseHelper = SingletonS2Container
                .getComponent("databaseHelper");
        databaseHelper.optimize();

        if (StringUtil.isBlank(options.sessionId)) {
            // use a default session id
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            options.sessionId = sdf.format(new Date());
        }

        final CrawlingSessionHelper crawlingSessionHelper = SingletonS2Container
                .getComponent("crawlingSessionHelper");
        final DynamicProperties crawlerProperties = SingletonS2Container
                .getComponent("crawlerProperties");

        if (StringUtil.isNotBlank(options.propertiesPath)) {
            crawlerProperties.reload(options.propertiesPath);
        } else {
            try {
                final File propFile = File.createTempFile("crawler_",
                        ".properties");
                if (propFile.delete() && logger.isDebugEnabled()) {
                    logger.debug("Deleted a temp file: "
                            + propFile.getAbsolutePath());
                }
                crawlerProperties.reload(propFile.getAbsolutePath());
                propFile.deleteOnExit(); // NOSONAR
            } catch (final IOException e) {
                logger.warn("Failed to create crawler properties file.", e);
            }
        }

        try {
            crawlingSessionHelper.store(options.sessionId);
            final String dayForCleanupStr = crawlerProperties.getProperty(
                    Constants.DAY_FOR_CLEANUP_PROPERTY, "1");
            int dayForCleanup = -1;
            try {
                dayForCleanup = Integer.parseInt(dayForCleanupStr);
            } catch (final NumberFormatException e) {
            }
            crawlingSessionHelper.updateParams(options.sessionId, options.name,
                    dayForCleanup);
        } catch (final Exception e) {
            logger.warn("Failed to store crawling information.", e);
        }

        try {
            return crawler.doCrawl(options);
        } finally {
            try {
                crawlingSessionHelper.store(options.sessionId);
            } catch (final Exception e) {
                logger.warn("Failed to store crawling information.", e);
            }
            databaseHelper.optimize();

            // notification
            try {
                crawler.sendMail(crawlingSessionHelper
                        .getInfoMap(options.sessionId));
            } catch (final Exception e) {
                logger.warn("Failed to send a mail.", e);
            }

        }
    }

    public void sendMail(final Map<String, String> infoMap) {
        final String toStrs = (String) crawlerProperties
                .get(Constants.NOTIFICATION_TO_PROPERTY);
        if (StringUtil.isNotBlank(toStrs)) {
            final String[] toAddresses = toStrs.split(",");
            final Map<String, Object> dataMap = new HashMap<String, Object>();
            for (final Map.Entry<String, String> entry : infoMap.entrySet()) {
                dataMap.put(StringUtil.decapitalize(entry.getKey()),
                        entry.getValue());
            }

            if (Constants.T.equals(infoMap.get(Constants.CRAWLER_STATUS))) {
                dataMap.put("success", true);
            }
            try {
                dataMap.put("hostname", InetAddress.getLocalHost()
                        .getHostAddress());
            } catch (final UnknownHostException e) {
                // ignore
            }

            final FileTemplateLoader loader = new FileTemplateLoader(new File(
                    ResourceUtil.getMailTemplatePath("")));
            final Handlebars handlebars = new Handlebars(loader);

            try {
                final Template template = handlebars
                        .compile(MAIL_TEMPLATE_NAME);
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

        final PathMappingHelper pathMappingHelper = SingletonS2Container
                .getComponent("pathMappingHelper");

        final long totalTime = System.currentTimeMillis();

        final CrawlingSessionHelper crawlingSessionHelper = SingletonS2Container
                .getComponent("crawlingSessionHelper");

        boolean completed = false;
        int exitCode = Constants.EXIT_OK;
        try {
            writeTimeToSessionInfo(crawlingSessionHelper,
                    Constants.CRAWLER_START_TIME);

            final SolrGroup updateSolrGroup = solrGroupManager
                    .getSolrGroup(QueryType.ADD);
            if (!updateSolrGroup.isActive(QueryType.ADD)) {
                throw new FessSystemException("SolrGroup "
                        + updateSolrGroup.getGroupName() + " is not available.");
            }

            // setup path mapping
            final List<CDef.ProcessType> ptList = new ArrayList<CDef.ProcessType>();
            ptList.add(CDef.ProcessType.Crawling);
            ptList.add(CDef.ProcessType.Both);
            pathMappingHelper.setPathMappingList(options.sessionId,
                    pathMappingService.getPathMappingList(ptList));

            // overlapping host
            try {
                final OverlappingHostHelper overlappingHostHelper = SingletonS2Container
                        .getComponent("overlappingHostHelper");
                overlappingHostHelper.init();
            } catch (final Exception e) {
                logger.warn("Could not initialize overlappingHostHelper.", e);
            }

            // delete expired sessions
            crawlingSessionService.deleteSessionIdsBefore(options.sessionId,
                    options.name, new Date());

            // expired session ids
            final List<Map<String, String>> sessionIdInfoList = crawlingSessionHelper
                    .getSessionIdList(updateSolrGroup);
            for (final Map<String, String> sessionIdInfoMap : sessionIdInfoList) {
                final String sid = sessionIdInfoMap
                        .get(CrawlingSessionHelper.FACET_SEGMENT_KEY);
                if (crawlingSessionService.get(sid) == null) {
                    crawlingSessionHelper.addExpiredSessions(sid);
                }
            }
            if (logger.isInfoEnabled()) {
                logger.info("Expired Session Ids: "
                        + crawlingSessionHelper.getExpiredSessionIdSet());
            }

            final List<Long> webConfigIdList = options.getWebConfigIdList();
            final List<Long> fileConfigIdList = options.getFileConfigIdList();
            final List<Long> dataConfigIdList = options.getDataConfigIdList();
            final boolean runAll = webConfigIdList == null
                    && fileConfigIdList == null && dataConfigIdList == null;

            Thread webFsCrawlerThread = null;
            Thread dataCrawlerThread = null;

            if (runAll || webConfigIdList != null || fileConfigIdList != null) {
                webFsCrawlerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // crawl web
                        writeTimeToSessionInfo(crawlingSessionHelper,
                                Constants.WEB_FS_CRAWLER_START_TIME);
                        webFsIndexHelper.crawl(options.sessionId,
                                webConfigIdList, fileConfigIdList,
                                updateSolrGroup);
                        writeTimeToSessionInfo(crawlingSessionHelper,
                                Constants.WEB_FS_CRAWLER_END_TIME);
                    }
                }, WEB_CRAWLING_PROCESS);
                webFsCrawlerThread.start();
            }

            if (runAll || dataConfigIdList != null) {
                dataCrawlerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // crawl data system
                        writeTimeToSessionInfo(crawlingSessionHelper,
                                Constants.DATA_CRAWLER_START_TIME);
                        dataIndexHelper.crawl(options.sessionId,
                                dataConfigIdList, updateSolrGroup);
                        writeTimeToSessionInfo(crawlingSessionHelper,
                                Constants.DATA_CRAWLER_END_TIME);
                    }
                }, DATA_CRAWLING_PROCESS);
                dataCrawlerThread.start();
            }

            joinCrawlerThread(webFsCrawlerThread);
            joinCrawlerThread(dataCrawlerThread);

            // clean up
            try {
                final Set<String> expiredSessionIdSet = crawlingSessionHelper
                        .getExpiredSessionIdSet();
                for (final String expiredSessionId : expiredSessionIdSet) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Deleted segment:" + expiredSessionId
                                + " in " + updateSolrGroup.getGroupName());
                    }
                    updateSolrGroup
                            .deleteByQuery("segment:" + expiredSessionId);
                    if (screenShotManager != null) {
                        screenShotManager.delete(expiredSessionId);
                    }
                }
            } catch (final Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Could not delete expired sessions in "
                            + updateSolrGroup.getGroupName(), e);
                }
                exitCode = Constants.EXIT_FAIL;
            }

            if (options.isCommit()) {
                commit(crawlingSessionHelper, updateSolrGroup);
            } else if (options.isOptimize()) {
                optimize(crawlingSessionHelper, updateSolrGroup);
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("No index commit.");
                }
            }

            final String serverRotationStr = crawlerProperties.getProperty(
                    Constants.SERVER_ROTATION_PROPERTY, Constants.FALSE);
            if (Constants.TRUE.equalsIgnoreCase(serverRotationStr)) {
                // apply
                solrGroupManager.applyNewSolrGroup();
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
            crawlingSessionHelper.putToInfoMap(Constants.CRAWLER_STATUS,
                    completed ? Constants.T : Constants.F);
            writeTimeToSessionInfo(crawlingSessionHelper,
                    Constants.CRAWLER_END_TIME);
            crawlingSessionHelper.putToInfoMap(Constants.CRAWLER_EXEC_TIME,
                    Long.toString(System.currentTimeMillis() - totalTime));

        }
    }

    protected void optimize(final CrawlingSessionHelper crawlingSessionHelper,
            final SolrGroup solrGroup) {
        writeTimeToSessionInfo(crawlingSessionHelper,
                Constants.OPTIMIZE_START_TIME);
        long startTime = System.currentTimeMillis();
        solrGroup.optimize();
        startTime = System.currentTimeMillis() - startTime;
        writeTimeToSessionInfo(crawlingSessionHelper,
                Constants.OPTIMIZE_END_TIME);
        if (crawlingSessionHelper != null) {
            crawlingSessionHelper.putToInfoMap(Constants.OPTIMIZE_EXEC_TIME,
                    Long.toString(startTime));
        }

        // update status
        final StatusPolicy statusPolicy = solrGroup.getStatusPolicy();
        for (final String serverName : solrGroup.getServerNames()) {
            if (statusPolicy.isActive(QueryType.OPTIMIZE, serverName)) {
                statusPolicy.activate(QueryType.OPTIMIZE, serverName);
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("[EXEC TIME] index optimize time: " + startTime + "ms");
        }
    }

    protected void commit(final CrawlingSessionHelper crawlingSessionHelper,
            final SolrGroup solrGroup) {
        writeTimeToSessionInfo(crawlingSessionHelper,
                Constants.COMMIT_START_TIME);
        long startTime = System.currentTimeMillis();
        solrGroup.commit(true, true, false, true);
        startTime = System.currentTimeMillis() - startTime;
        writeTimeToSessionInfo(crawlingSessionHelper, Constants.COMMIT_END_TIME);
        if (crawlingSessionHelper != null) {
            crawlingSessionHelper.putToInfoMap(Constants.COMMIT_EXEC_TIME,
                    Long.toString(startTime));
        }

        // update status
        final StatusPolicy statusPolicy = solrGroup.getStatusPolicy();
        for (final String serverName : solrGroup.getServerNames()) {
            if (statusPolicy.isActive(QueryType.COMMIT, serverName)) {
                statusPolicy.activate(QueryType.COMMIT, serverName);
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("[EXEC TIME] index commit time: " + startTime + "ms");
        }
    }

    private void writeTimeToSessionInfo(
            final CrawlingSessionHelper crawlingSessionHelper, final String key) {
        if (crawlingSessionHelper != null) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(
                    CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            crawlingSessionHelper.putToInfoMap(key,
                    dateFormat.format(new Date()));
        }
    }

    private void joinCrawlerThread(final Thread crawlerThread) {
        if (crawlerThread != null) {
            try {
                crawlerThread.join();
            } catch (final Exception e) {
                logger.info("Interrupted a crawling process: "
                        + crawlerThread.getName());
            }
        }
    }
}
