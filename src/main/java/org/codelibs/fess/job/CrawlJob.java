/*
 * Copyright 2012-2021 CodeLibs Project and the Others.
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

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.config.exbhv.ScheduledJobBhv;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.exec.Crawler;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.InputStreamThread;
import org.codelibs.fess.util.JobProcess;
import org.codelibs.fess.util.ResourceUtil;

public class CrawlJob extends ExecJob {

    private static final Logger logger = LogManager.getLogger(CrawlJob.class);

    protected String namespace = Constants.CRAWLING_INFO_SYSTEM_NAME;

    protected String[] webConfigIds;

    protected String[] fileConfigIds;

    protected String[] dataConfigIds;

    protected int documentExpires = -2;

    public CrawlJob namespace(final String namespace) {
        this.namespace = namespace;
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

    @Override
    public String execute() {
        //   check # of crawler processes
        final int maxCrawlerProcesses = ComponentUtil.getFessConfig().getJobMaxCrawlerProcessesAsInteger();
        if (maxCrawlerProcesses > 0) {
            final int runningJobCount = getRunningJobCount();
            if (runningJobCount > maxCrawlerProcesses) {
                throw new JobProcessingException(
                        runningJobCount + " crawler processes are running. Max processes are " + maxCrawlerProcesses + ".");
            }
        }

        final StringBuilder resultBuf = new StringBuilder(100);
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
            jobExecutor.addShutdownListener(() -> ComponentUtil.getProcessHelper().destroyProcess(sessionId));
        }

        final TimeoutTask timeoutTask = createTimeoutTask();
        try {
            executeCrawler();
            ComponentUtil.getKeyMatchHelper().update();
        } catch (final JobProcessingException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobProcessingException("Failed to execute a crawl job.", e);
        } finally {
            if (timeoutTask != null && !timeoutTask.isCanceled()) {
                timeoutTask.cancel();
            }
        }

        return resultBuf.toString();

    }

    protected int getRunningJobCount() {
        final AtomicInteger counter = new AtomicInteger(0);
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        ComponentUtil.getComponent(ScheduledJobBhv.class).selectCursor(cb -> {
            cb.query().setAvailable_Equal(Constants.T);
            cb.query().setCrawler_Equal(Constants.T);
        }, scheduledJob -> {
            if (fessConfig.isSchedulerTarget(scheduledJob.getTarget())) {
                if (scheduledJob.isRunning()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("{} is running.", scheduledJob.getId());
                    }
                    counter.incrementAndGet();
                } else if (logger.isDebugEnabled()) {
                    logger.debug("{} is not running.", scheduledJob.getId());
                }
            }
        });
        return counter.get();
    }

    protected void executeCrawler() {
        final List<String> cmdList = new ArrayList<>();
        final String cpSeparator = SystemUtils.IS_OS_WINDOWS ? ";" : ":";
        final ServletContext servletContext = ComponentUtil.getComponent(ServletContext.class);
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final ProcessHelper processHelper = ComponentUtil.getProcessHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        cmdList.add(fessConfig.getJavaCommandPath());

        // -cp
        cmdList.add("-cp");
        final StringBuilder buf = new StringBuilder(100);
        ResourceUtil.getOverrideConfPath().ifPresent(p -> {
            buf.append(p);
            buf.append(cpSeparator);
        });
        final String confPath = System.getProperty(Constants.FESS_CONF_PATH);
        if (StringUtil.isNotBlank(confPath)) {
            buf.append(confPath);
            buf.append(cpSeparator);
        }
        // WEB-INF/env/crawler/resources
        buf.append("WEB-INF");
        buf.append(File.separator);
        buf.append("env");
        buf.append(File.separator);
        buf.append(getExecuteType());
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
        appendJarFile(cpSeparator, buf, new File(servletContext.getRealPath("/WEB-INF/lib")),
                "WEB-INF" + File.separator + "lib" + File.separator);
        // WEB-INF/env/crawler/lib
        appendJarFile(cpSeparator, buf, new File(servletContext.getRealPath("/WEB-INF/env/" + getExecuteType() + "/lib")),
                "WEB-INF" + File.separator + "env" + File.separator + getExecuteType() + File.separator + "lib" + File.separator);
        // WEB-INF/plugin
        appendJarFile(cpSeparator, buf, new File(servletContext.getRealPath("/WEB-INF/plugin")),
                "WEB-INF" + File.separator + "plugin" + File.separator);
        final File targetLibDir = new File(targetDir, "fess" + File.separator + "WEB-INF" + File.separator + "lib");
        if (targetLibDir.isDirectory()) {
            appendJarFile(cpSeparator, buf, targetLibDir, targetLibDir.getAbsolutePath() + File.separator);
        }
        cmdList.add(buf.toString());

        if (useLocalFesen) {
            final String httpAddress = System.getProperty(Constants.FESS_ES_HTTP_ADDRESS);
            if (StringUtil.isNotBlank(httpAddress)) {
                cmdList.add("-D" + Constants.FESS_ES_HTTP_ADDRESS + "=" + httpAddress);
            }
        }

        final String systemLastaEnv = System.getProperty("lasta.env");
        if (StringUtil.isNotBlank(systemLastaEnv)) {
            if (systemLastaEnv.equals("web")) {
                cmdList.add("-Dlasta.env=" + getExecuteType());
            } else {
                cmdList.add("-Dlasta.env=" + systemLastaEnv);
            }
        } else if (StringUtil.isNotBlank(lastaEnv)) {
            cmdList.add("-Dlasta.env=" + lastaEnv);
        } else {
            cmdList.add("-Dlasta.env=" + getExecuteType());
        }

        addSystemProperty(cmdList, Constants.FESS_CONF_PATH, null, null);
        cmdList.add("-Dfess." + getExecuteType() + ".process=true");
        cmdList.add("-Dfess.log.path=" + (logFilePath != null ? logFilePath : systemHelper.getLogFilePath()));
        addSystemProperty(cmdList, "fess.log.name", "fess-" + getExecuteType(), "-" + getExecuteType());
        if (logLevel == null) {
            addSystemProperty(cmdList, "fess.log.level", null, null);
        } else {
            cmdList.add("-Dfess.log.level=" + logLevel);
            if (logLevel.equalsIgnoreCase("debug")) {
                cmdList.add("-Dorg.apache.tika.service.error.warn=true");
            }
        }
        stream(fessConfig.getJvmCrawlerOptionsAsArray())
                .of(stream -> stream.filter(StringUtil::isNotBlank).forEach(value -> cmdList.add(value)));

        File ownTmpDir = null;
        final String tmpDir = System.getProperty("java.io.tmpdir");
        if (fessConfig.isUseOwnTmpDir() && StringUtil.isNotBlank(tmpDir)) {
            ownTmpDir = new File(tmpDir, "fessTmpDir_" + sessionId);
            if (ownTmpDir.mkdirs()) {
                cmdList.add("-Djava.io.tmpdir=" + ownTmpDir.getAbsolutePath());
                cmdList.add("-Dpdfbox.fontcache=" + ownTmpDir.getAbsolutePath());
            } else {
                ownTmpDir = null;
            }
        }

        cmdList.add(ComponentUtil.getThumbnailManager().getThumbnailPathOption());

        if (!jvmOptions.isEmpty()) {
            jvmOptions.stream().filter(StringUtil::isNotBlank).forEach(cmdList::add);
        }

        cmdList.add(Crawler.class.getCanonicalName());

        cmdList.add("--sessionId");
        cmdList.add(sessionId);
        cmdList.add("--name");
        cmdList.add(namespace);

        if (webConfigIds != null && webConfigIds.length > 0) {
            cmdList.add("-w");
            cmdList.add(StringUtils.join(webConfigIds, ','));
        }
        if (fileConfigIds != null && fileConfigIds.length > 0) {
            cmdList.add("-f");
            cmdList.add(StringUtils.join(fileConfigIds, ','));
        }
        if (dataConfigIds != null && dataConfigIds.length > 0) {
            cmdList.add("-d");
            cmdList.add(StringUtils.join(dataConfigIds, ','));
        }
        if (documentExpires >= -1) {
            cmdList.add("-e");
            cmdList.add(Integer.toString(documentExpires));
        }

        final File propFile = ComponentUtil.getSystemHelper().createTempFile(getExecuteType() + "_", ".properties");
        try {
            cmdList.add("-p");
            cmdList.add(propFile.getAbsolutePath());
            createSystemProperties(cmdList, propFile);

            final File baseDir = new File(servletContext.getRealPath("/WEB-INF")).getParentFile();

            if (logger.isInfoEnabled()) {
                logger.info("Crawler: \nDirectory={}\nOptions={}", baseDir, cmdList);
            }

            final JobProcess jobProcess = processHelper.startProcess(sessionId, cmdList, pb -> {
                pb.directory(baseDir);
                pb.redirectErrorStream(true);
            });

            final InputStreamThread it = jobProcess.getInputStreamThread();
            it.start();

            final Process currentProcess = jobProcess.getProcess();
            currentProcess.waitFor();
            it.join(5000);

            final int exitValue = currentProcess.exitValue();

            if (logger.isInfoEnabled()) {
                logger.info("Crawler: Exit Code={} - Process Output:\n{}", exitValue, it.getOutput());
            }
            if (exitValue != 0) {
                final StringBuilder out = new StringBuilder();
                if (processTimeout) {
                    out.append("Process is terminated due to ").append(timeout).append(" second exceeded.\n");
                }
                out.append("Exit Code: ").append(exitValue).append("\nOutput:\n").append(it.getOutput());
                throw new JobProcessingException(out.toString());
            }
        } catch (final JobProcessingException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobProcessingException("Crawler Process terminated.", e);
        } finally {
            try {
                processHelper.destroyProcess(sessionId);
            } finally {
                if (propFile != null && !propFile.delete()) {
                    logger.warn("Failed to delete {}.", propFile.getAbsolutePath());
                }
                deleteTempDir(ownTmpDir);
            }
        }
    }

    @Override
    protected String getExecuteType() {
        return Constants.EXECUTE_TYPE_CRAWLER;
    }

}
