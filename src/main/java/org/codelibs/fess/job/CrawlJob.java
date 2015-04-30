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

package org.codelibs.fess.job;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.codelibs.core.util.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.exec.Crawler;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.job.JobExecutor.ShutdownListener;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.InputStreamThread;
import org.codelibs.fess.util.JobProcess;
import org.seasar.framework.container.SingletonS2Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlJob {
    private static final Logger logger = LoggerFactory.getLogger(CrawlJob.class);

    protected JobExecutor jobExecutor;

    protected String sessionId;

    protected String namespace = Constants.CRAWLING_SESSION_SYSTEM_NAME;

    protected String[] webConfigIds;

    protected String[] fileConfigIds;

    protected String[] dataConfigIds;

    protected String operation;

    protected String logFilePath;

    protected int documentExpires = -2;

    protected int retryCountToDeleteTempDir = 10;

    protected long retryIntervalToDeleteTempDir = 5000;

    public CrawlJob jobExecutor(final JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
        return this;
    }

    public CrawlJob sessionId(final String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public CrawlJob namespace(final String namespace) {
        this.namespace = namespace;
        return this;
    }

    public CrawlJob operation(final String operation) {
        this.operation = operation;
        return this;
    }

    public CrawlJob logFilePath(final String logFilePath) {
        this.logFilePath = logFilePath;
        return this;
    }

    public CrawlJob documentExpires(final int documentExpires) {
        this.documentExpires = documentExpires;
        return this;
    }

    public CrawlJob webConfigIds(final String[] webConfigIds) {
        this.webConfigIds = webConfigIds;
        return this;
    }

    public CrawlJob fileConfigIds(final String[] fileConfigIds) {
        this.fileConfigIds = fileConfigIds;
        return this;
    }

    public CrawlJob dataConfigIds(final String[] dataConfigIds) {
        this.dataConfigIds = dataConfigIds;
        return this;
    }

    public void retryToDeleteTempDir(final int retryCount, final long retryInterval) {
        retryCountToDeleteTempDir = retryCount;
        retryIntervalToDeleteTempDir = retryInterval;
    }

    public String execute(final JobExecutor jobExecutor) {
        jobExecutor(jobExecutor);
        return execute();
    }

    public String execute(final JobExecutor jobExecutor, final String[] webConfigIds, final String[] fileConfigIds,
            final String[] dataConfigIds, final String operation) {
        jobExecutor(jobExecutor);
        operation(operation);
        webConfigIds(webConfigIds);
        fileConfigIds(fileConfigIds);
        dataConfigIds(dataConfigIds);
        return execute();

    }

    public String execute(final JobExecutor jobExecutor, final String sessionId, final String[] webConfigIds, final String[] fileConfigIds,
            final String[] dataConfigIds, final String operation) {
        jobExecutor(jobExecutor);
        operation(operation);
        webConfigIds(webConfigIds);
        fileConfigIds(fileConfigIds);
        dataConfigIds(dataConfigIds);
        sessionId(sessionId);
        return execute();
    }

    public String execute() {
        final StringBuilder resultBuf = new StringBuilder();
        final boolean runAll = webConfigIds == null && fileConfigIds == null && dataConfigIds == null;

        if (sessionId == null) { // create session id
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            sessionId = sdf.format(new Date());
        }
        resultBuf.append("Session Id: ").append(sessionId).append("\n");
        resultBuf.append("Web  Config Id:");
        if (webConfigIds == null) {
            if (runAll) {
                resultBuf.append(" ALL\n");
            } else {
                resultBuf.append(" NONE\n");
            }
        } else {
            for (final String id : webConfigIds) {
                resultBuf.append(' ').append(id);
            }
            resultBuf.append('\n');
        }
        resultBuf.append("File Config Id:");
        if (fileConfigIds == null) {
            if (runAll) {
                resultBuf.append(" ALL\n");
            } else {
                resultBuf.append(" NONE\n");
            }
        } else {
            for (final String id : fileConfigIds) {
                resultBuf.append(' ').append(id);
            }
            resultBuf.append('\n');
        }
        resultBuf.append("Data Config Id:");
        if (dataConfigIds == null) {
            if (runAll) {
                resultBuf.append(" ALL\n");
            } else {
                resultBuf.append(" NONE\n");
            }
        } else {
            for (final String id : dataConfigIds) {
                resultBuf.append(' ').append(id);
            }
            resultBuf.append('\n');
        }

        if (jobExecutor != null) {
            jobExecutor.addShutdownListener(new ShutdownListener() {
                @Override
                public void onShutdown() {
                    ComponentUtil.getJobHelper().destroyCrawlerProcess(sessionId);
                }
            });
        }

        try {
            executeCrawler();
            ComponentUtil.getKeyMatchHelper().update();
        } catch (final FessSystemException e) {
            throw e;
        } catch (final Exception e) {
            throw new FessSystemException("Failed to execute a crawl job.", e);
        }

        return resultBuf.toString();

    }

    protected void executeCrawler() {
        final List<String> crawlerCmdList = new ArrayList<String>();
        final String cpSeparator = SystemUtils.IS_OS_WINDOWS ? ";" : ":";
        final ServletContext servletContext = SingletonS2Container.getComponent(ServletContext.class);
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final JobHelper jobHelper = ComponentUtil.getJobHelper();

        crawlerCmdList.add(systemHelper.getJavaCommandPath());

        // -cp
        crawlerCmdList.add("-cp");
        final StringBuilder buf = new StringBuilder();
        // WEB-INF/cmd/resources
        buf.append("WEB-INF");
        buf.append(File.separator);
        buf.append("cmd");
        buf.append(File.separator);
        buf.append("resources");
        buf.append(cpSeparator);
        // WEB-INF/classes
        buf.append("WEB-INF");
        buf.append(File.separator);
        buf.append("classes");
        // WEB-INF/lib
        appendJarFile(cpSeparator, servletContext, buf, "/WEB-INF/lib", "WEB-INF" + File.separator + "lib" + File.separator);
        // WEB-INF/cmd/lib
        appendJarFile(cpSeparator, servletContext, buf, "/WEB-INF/cmd/lib", "WEB-INF" + File.separator + "cmd" + File.separator + "lib"
                + File.separator);
        crawlerCmdList.add(buf.toString());

        crawlerCmdList.add("-Dfess.crawler.process=true");
        crawlerCmdList.add("-Dsolr.solr.home=" + systemHelper.getSolrHome());
        if (logFilePath == null) {
            logFilePath = systemHelper.getLogFilePath();
        }
        crawlerCmdList.add("-Dfess.log.file=" + logFilePath);
        if (systemHelper.getCrawlerJavaOptions() != null) {
            for (final String value : systemHelper.getCrawlerJavaOptions()) {
                crawlerCmdList.add(value);
            }
        }

        File ownTmpDir = null;
        if (systemHelper.isUseOwnTmpDir()) {
            final String tmpDir = System.getProperty("java.io.tmpdir");
            if (StringUtil.isNotBlank(tmpDir)) {
                ownTmpDir = new File(tmpDir, "fessTmpDir_" + sessionId);
                if (ownTmpDir.mkdirs()) {
                    crawlerCmdList.add("-Djava.io.tmpdir=" + ownTmpDir.getAbsolutePath());
                } else {
                    ownTmpDir = null;
                }
            }
        }

        crawlerCmdList.add(Crawler.class.getCanonicalName());

        crawlerCmdList.add("--sessionId");
        crawlerCmdList.add(sessionId);
        crawlerCmdList.add("--name");
        crawlerCmdList.add(namespace);

        if (webConfigIds != null && webConfigIds.length > 0) {
            crawlerCmdList.add("-w");
            crawlerCmdList.add(StringUtils.join(webConfigIds, ','));
        }
        if (fileConfigIds != null && fileConfigIds.length > 0) {
            crawlerCmdList.add("-f");
            crawlerCmdList.add(StringUtils.join(fileConfigIds, ','));
        }
        if (dataConfigIds != null && dataConfigIds.length > 0) {
            crawlerCmdList.add("-d");
            crawlerCmdList.add(StringUtils.join(dataConfigIds, ','));
        }
        if (StringUtil.isNotBlank(operation)) {
            crawlerCmdList.add("-o");
            crawlerCmdList.add(operation);
        }
        if (documentExpires >= -1) {
            crawlerCmdList.add("-e");
            crawlerCmdList.add(Integer.toString(documentExpires));
        }

        final File baseDir = new File(servletContext.getRealPath("/"));

        if (logger.isInfoEnabled()) {
            logger.info("Crawler: \nDirectory=" + baseDir + "\nOptions=" + crawlerCmdList);
        }

        final ProcessBuilder pb = new ProcessBuilder(crawlerCmdList);
        pb.directory(baseDir);
        pb.redirectErrorStream(true);

        try {
            final JobProcess jobProcess = jobHelper.startCrawlerProcess(sessionId, pb);

            final InputStreamThread it = jobProcess.getInputStreamThread();
            it.start();

            final Process currentProcess = jobProcess.getProcess();
            currentProcess.waitFor();
            it.join(5000);

            final int exitValue = currentProcess.exitValue();

            if (logger.isInfoEnabled()) {
                logger.info("Crawler: Exit Code=" + exitValue + " - Crawler Process Output:\n" + it.getOutput());
            }
            if (exitValue != 0) {
                throw new FessSystemException("Exit Code: " + exitValue + "\nOutput:\n" + it.getOutput());
            }
        } catch (final FessSystemException e) {
            throw e;
        } catch (final InterruptedException e) {
            logger.warn("Crawler Process interrupted.");
        } catch (final Exception e) {
            throw new FessSystemException("Crawler Process terminated.", e);
        } finally {
            try {
                jobHelper.destroyCrawlerProcess(sessionId);
            } finally {
                deleteTempDir(ownTmpDir);
            }
        }
    }

    protected void deleteTempDir(final File ownTmpDir) {
        if (ownTmpDir == null) {
            return;
        }
        for (int i = 0; i < retryCountToDeleteTempDir; i++) {
            if (ownTmpDir.delete()) {
                return;
            }
            try {
                Thread.sleep(retryIntervalToDeleteTempDir);
            } catch (final InterruptedException e) {
                // ignore
            }
        }
        logger.warn("Could not delete a temp dir: " + ownTmpDir.getAbsolutePath());
    }

    protected void appendJarFile(final String cpSeparator, final ServletContext servletContext, final StringBuilder buf,
            final String libDirPath, final String basePath) {
        final File libDir = new File(servletContext.getRealPath(libDirPath));
        final File[] jarFiles = libDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.toLowerCase().endsWith(".jar");
            }
        });
        if (jarFiles != null) {
            for (final File file : jarFiles) {
                buf.append(cpSeparator);
                buf.append(basePath);
                buf.append(file.getName());
            }
        }
    }
}
