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
package org.codelibs.fess.job;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.SystemUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.exec.SuggestCreator;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.InputStreamThread;
import org.codelibs.fess.util.JobProcess;
import org.lastaflute.di.core.SingletonLaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuggestJob {
    private static final Logger logger = LoggerFactory.getLogger(SuggestJob.class);

    protected JobExecutor jobExecutor;

    protected String sessionId;

    protected boolean useLocaleElasticsearch = true;

    protected String logFilePath;

    protected int retryCountToDeleteTempDir = 10;

    protected long retryIntervalToDeleteTempDir = 5000;

    public SuggestJob jobExecutor(final JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
        return this;
    }

    public SuggestJob sessionId(final String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String execute(final JobExecutor jobExecutor) {
        jobExecutor(jobExecutor);
        return execute();
    }

    public String execute() {
        final StringBuilder resultBuf = new StringBuilder();

        if (sessionId == null) { // create session id
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            sessionId = sdf.format(new Date());
        }
        resultBuf.append("Session Id: ").append(sessionId).append("\n");
        if (jobExecutor != null) {
            jobExecutor.addShutdownListener(() -> ComponentUtil.getJobHelper().destroyCrawlerProcess(sessionId));
        }

        try {
            executeSuggestCreator();
        } catch (final FessSystemException e) {
            throw e;
        } catch (final Exception e) {
            throw new FessSystemException("Failed to execute a crawl job.", e);
        }

        return resultBuf.toString();

    }

    protected void executeSuggestCreator() {
        final List<String> suggestCreatorCmdList = new ArrayList<>();
        final String cpSeparator = SystemUtils.IS_OS_WINDOWS ? ";" : ":";
        final ServletContext servletContext = SingletonLaContainer.getComponent(ServletContext.class);
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final JobHelper jobHelper = ComponentUtil.getJobHelper();

        suggestCreatorCmdList.add(systemHelper.getJavaCommandPath());

        // -cp
        suggestCreatorCmdList.add("-cp");
        final StringBuilder buf = new StringBuilder();
        // WEB-INF/suggest/resources
        buf.append("WEB-INF");
        buf.append(File.separator);
        buf.append("suggest");
        buf.append(File.separator);
        buf.append("resources");
        buf.append(cpSeparator);
        // WEB-INF/classes
        buf.append("WEB-INF");
        buf.append(File.separator);
        buf.append("classes");
        // target/classes
        final String userDir = System.getProperty("user.dir");
        final File targetDir = new File(userDir, "target");
        final File targetClassesDir = new File(targetDir, "classes");
        if (targetClassesDir.isDirectory()) {
            buf.append(cpSeparator);
            buf.append(targetClassesDir.getAbsolutePath());
        }
        // WEB-INF/lib
        appendJarFile(cpSeparator, buf, new File(servletContext.getRealPath("/WEB-INF/lib")), "WEB-INF/lib" + File.separator);
        // WEB-INF/crawler/lib
        appendJarFile(cpSeparator, buf, new File(servletContext.getRealPath("/WEB-INF/suggest/lib")), "WEB-INF/suggest" + File.separator
                + "lib" + File.separator);
        final File targetLibDir = new File(targetDir, "fess" + File.separator + "WEB-INF" + File.separator + "lib");
        if (targetLibDir.isDirectory()) {
            appendJarFile(cpSeparator, buf, targetLibDir, targetLibDir.getAbsolutePath() + File.separator);
        }
        suggestCreatorCmdList.add(buf.toString());

        if (useLocaleElasticsearch) {
            final String transportAddresses = System.getProperty(Constants.FESS_ES_TRANSPORT_ADDRESSES);
            if (StringUtil.isNotBlank(transportAddresses)) {
                suggestCreatorCmdList.add("-D" + Constants.FESS_ES_TRANSPORT_ADDRESSES + "=" + transportAddresses);
            }
            final String clusterName = System.getProperty(Constants.FESS_ES_CLUSTER_NAME);
            if (StringUtil.isNotBlank(clusterName)) {
                suggestCreatorCmdList.add("-D" + Constants.FESS_ES_CLUSTER_NAME + "=" + clusterName);
            }
        }

        suggestCreatorCmdList.add("-Dfess.suggest.process=true");
        if (logFilePath == null) {
            final String value = System.getProperty("fess.log.path");
            logFilePath = value != null ? value : new File(targetDir, "logs").getAbsolutePath();
        }
        suggestCreatorCmdList.add("-Dfess.log.path=" + logFilePath);
        addSystemProperty(suggestCreatorCmdList, "lasta.env", null, null);
        addSystemProperty(suggestCreatorCmdList, "fess.log.name", "fess-suggest", "-suggest");
        addSystemProperty(suggestCreatorCmdList, "fess.log.level", null, null);
        if (systemHelper.getCrawlerJavaOptions() != null) {
            for (final String value : systemHelper.getCrawlerJavaOptions()) {
                suggestCreatorCmdList.add(value);
            }
        }

        File ownTmpDir = null;
        if (systemHelper.isUseOwnTmpDir()) {
            final String tmpDir = System.getProperty("java.io.tmpdir");
            if (StringUtil.isNotBlank(tmpDir)) {
                ownTmpDir = new File(tmpDir, "fessTmpDir_" + sessionId);
                if (ownTmpDir.mkdirs()) {
                    suggestCreatorCmdList.add("-Djava.io.tmpdir=" + ownTmpDir.getAbsolutePath());
                } else {
                    ownTmpDir = null;
                }
            }
        }

        suggestCreatorCmdList.add(SuggestCreator.class.getCanonicalName());

        suggestCreatorCmdList.add("--sessionId");
        suggestCreatorCmdList.add(sessionId);

        final File baseDir = new File(servletContext.getRealPath("/WEB-INF")).getParentFile();

        if (logger.isInfoEnabled()) {
            logger.info("SuggestCreator: \nDirectory=" + baseDir + "\nOptions=" + suggestCreatorCmdList);
        }

        final ProcessBuilder pb = new ProcessBuilder(suggestCreatorCmdList);
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
                logger.info("SuggestCreator: Exit Code=" + exitValue + " - SuggestCreator Process Output:\n" + it.getOutput());
            }
            if (exitValue != 0) {
                throw new FessSystemException("Exit Code: " + exitValue + "\nOutput:\n" + it.getOutput());
            }
        } catch (final FessSystemException e) {
            throw e;
        } catch (final InterruptedException e) {
            logger.warn("SuggestCreator Process interrupted.");
        } catch (final Exception e) {
            throw new FessSystemException("SuggestCreator Process terminated.", e);
        } finally {
            try {
                jobHelper.destroyCrawlerProcess(sessionId);
            } finally {
                deleteTempDir(ownTmpDir);
            }
        }
    }

    private void addSystemProperty(final List<String> crawlerCmdList, final String name, final String defaultValue, final String appendValue) {
        final String value = System.getProperty(name);
        if (value != null) {
            final StringBuilder buf = new StringBuilder();
            buf.append("-D").append(name).append("=").append(value);
            if (appendValue != null) {
                buf.append(appendValue);
            }
            crawlerCmdList.add(buf.toString());
        } else if (defaultValue != null) {
            crawlerCmdList.add("-D" + name + "=" + defaultValue);
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

    protected void appendJarFile(final String cpSeparator, final StringBuilder buf, final File libDir, final String basePath) {
        final File[] jarFiles = libDir.listFiles((FilenameFilter) (dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (jarFiles != null) {
            for (final File file : jarFiles) {
                buf.append(cpSeparator);
                buf.append(basePath);
                buf.append(file.getName());
            }
        }
    }
}
