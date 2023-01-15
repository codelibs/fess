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
package org.codelibs.fess.job;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.exec.ThumbnailGenerator;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.InputStreamThread;
import org.codelibs.fess.util.JobProcess;
import org.codelibs.fess.util.ResourceUtil;
import org.codelibs.fess.util.SystemUtil;

public class GenerateThumbnailJob extends ExecJob {
    static final Logger logger = LogManager.getLogger(GenerateThumbnailJob.class);

    protected int numOfThreads = 1;

    protected boolean cleanup = false;

    public GenerateThumbnailJob numOfThreads(final int numOfThreads) {
        this.numOfThreads = numOfThreads;
        return this;
    }

    public GenerateThumbnailJob cleanup() {
        this.cleanup = true;
        return this;
    }

    @Override
    public String execute() {
        final StringBuilder resultBuf = new StringBuilder();

        if (sessionId == null) { // create session id
            sessionId = RandomStringUtils.randomAlphabetic(15);
        }
        resultBuf.append("Session Id: ").append(sessionId).append("\n");
        if (jobExecutor != null) {
            jobExecutor.addShutdownListener(() -> ComponentUtil.getProcessHelper().destroyProcess(sessionId));
        }

        final TimeoutTask timeoutTask = createTimeoutTask();
        try {
            executeThumbnailGenerator();
        } catch (final Exception e) {
            logger.warn("Failed to generate thumbnails.", e);
            resultBuf.append(e.getMessage()).append("\n");
        } finally {
            if (timeoutTask != null && !timeoutTask.isCanceled()) {
                timeoutTask.cancel();
            }
        }

        return resultBuf.toString();

    }

    protected void executeThumbnailGenerator() {
        final List<String> cmdList = new ArrayList<>();
        final String cpSeparator = SystemUtils.IS_OS_WINDOWS ? ";" : ":";
        final ServletContext servletContext = ComponentUtil.getComponent(ServletContext.class);
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
        // WEB-INF/env/thumbnail/resources
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
        // WEB-INF/env/thumbnail/lib
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
            final String httpAddress = SystemUtil.getSearchEngineHttpAddress();
            if (StringUtil.isNotBlank(httpAddress)) {
                cmdList.add("-D" + Constants.FESS_SEARCH_ENGINE_HTTP_ADDRESS + "=" + httpAddress);
            }
        }

        final String systemLastaEnv = System.getProperty("lasta.env");
        if (StringUtil.isNotBlank(systemLastaEnv)) {
            if ("web".equals(systemLastaEnv)) {
                cmdList.add("-Dlasta.env=" + getExecuteType());
            } else {
                cmdList.add("-Dlasta.env=" + systemLastaEnv);
            }
        } else if (StringUtil.isNotBlank(lastaEnv)) {
            cmdList.add("-Dlasta.env=" + lastaEnv);
        } else {
            cmdList.add("-Dlasta.env=" + getExecuteType());
        }

        addFessConfigProperties(cmdList);
        addFessSystemProperties(cmdList);
        addSystemProperty(cmdList, Constants.FESS_CONF_PATH, null, null);
        cmdList.add("-Dfess." + getExecuteType() + ".process=true");
        if (logFilePath == null) {
            final String value = System.getProperty("fess.log.path");
            logFilePath = value != null ? value : new File(targetDir, "logs").getAbsolutePath();
        }
        cmdList.add("-Dfess.log.path=" + logFilePath);
        addSystemProperty(cmdList, Constants.FESS_VAR_PATH, null, null);
        addSystemProperty(cmdList, Constants.FESS_THUMBNAIL_PATH, null, null);
        addSystemProperty(cmdList, "fess.log.name", getLogName("fess"), getLogName(StringUtil.EMPTY));
        if (logLevel != null) {
            cmdList.add("-Dfess.log.level=" + logLevel);
        }
        stream(fessConfig.getJvmThumbnailOptionsAsArray())
                .of(stream -> stream.filter(StringUtil::isNotBlank).forEach(value -> cmdList.add(value)));

        File ownTmpDir = null;
        final String tmpDir = System.getProperty("java.io.tmpdir");
        if (fessConfig.isUseOwnTmpDir() && StringUtil.isNotBlank(tmpDir)) {
            ownTmpDir = new File(tmpDir, "fessTmpDir_" + sessionId);
            if (ownTmpDir.mkdirs()) {
                cmdList.add("-Djava.io.tmpdir=" + ownTmpDir.getAbsolutePath());
            } else {
                ownTmpDir = null;
            }
        }

        if (!jvmOptions.isEmpty()) {
            jvmOptions.stream().filter(StringUtil::isNotBlank).forEach(cmdList::add);
        }

        cmdList.add(ThumbnailGenerator.class.getCanonicalName());

        cmdList.add("--sessionId");
        cmdList.add(sessionId);
        cmdList.add("--numOfThreads");
        cmdList.add(Integer.toString(numOfThreads));
        if (cleanup) {
            cmdList.add("--cleanup");
        }

        final File propFile = ComponentUtil.getSystemHelper().createTempFile(getExecuteType() + "_", ".properties");
        try {
            cmdList.add("-p");
            cmdList.add(propFile.getAbsolutePath());
            createSystemProperties(cmdList, propFile);

            final File baseDir = new File(servletContext.getRealPath("/WEB-INF")).getParentFile();

            if (logger.isInfoEnabled()) {
                logger.info("ThumbnailGenerator: \nDirectory={}\nOptions={}", baseDir, cmdList);
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
                logger.info("ThumbnailGenerator: Exit Code={} - Process Output:\n{}", exitValue, it.getOutput());
            }
            if (exitValue != 0) {
                final StringBuilder out = new StringBuilder();
                if (processTimeout) {
                    out.append("Process is terminated due to ").append(timeout).append(" second exceeded.\n");
                }
                out.append("Exit Code: ").append(exitValue).append("\nOutput:\n").append(it.getOutput());
                throw new JobProcessingException(out.toString());
            }
            ComponentUtil.getPopularWordHelper().clearCache();
        } catch (final JobProcessingException e) {
            throw e;
        } catch (final Exception e) {
            throw new JobProcessingException("ThumbnailGenerator Process terminated.", e);
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
        return Constants.EXECUTE_TYPE_THUMBNAIL;
    }
}
