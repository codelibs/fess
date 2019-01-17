/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.exec.SuggestCreator;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.InputStreamThread;
import org.codelibs.fess.util.JobProcess;
import org.codelibs.fess.util.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuggestJob extends ExecJob {

    private static final Logger logger = LoggerFactory.getLogger(SuggestJob.class);

    @Deprecated
    public String execute(final JobExecutor jobExecutor) {
        jobExecutor(jobExecutor);
        return execute();
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

        try {
            executeSuggestCreator();
        } catch (final Exception e) {
            logger.error("Failed to create suggest data.", e);
            resultBuf.append(e.getMessage()).append("\n");
        }

        return resultBuf.toString();

    }

    protected void executeSuggestCreator() {
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
        // WEB-INF/env/suggest/resources
        buf.append("WEB-INF");
        buf.append(File.separator);
        buf.append("env");
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
        appendJarFile(cpSeparator, buf, new File(servletContext.getRealPath("/WEB-INF/lib")), "WEB-INF" + File.separator + "lib"
                + File.separator);
        // WEB-INF/env/suggest/lib
        appendJarFile(cpSeparator, buf, new File(servletContext.getRealPath("/WEB-INF/env/suggest/lib")), "WEB-INF" + File.separator
                + "env" + File.separator + "suggest" + File.separator + "lib" + File.separator);
        final File targetLibDir = new File(targetDir, "fess" + File.separator + "WEB-INF" + File.separator + "lib");
        if (targetLibDir.isDirectory()) {
            appendJarFile(cpSeparator, buf, targetLibDir, targetLibDir.getAbsolutePath() + File.separator);
        }
        cmdList.add(buf.toString());

        if (useLocalElasticsearch) {
            final String httpAddress = System.getProperty(Constants.FESS_ES_HTTP_ADDRESS);
            if (StringUtil.isNotBlank(httpAddress)) {
                cmdList.add("-D" + Constants.FESS_ES_HTTP_ADDRESS + "=" + httpAddress);
            }
        }

        final String systemLastaEnv = System.getProperty("lasta.env");
        if (StringUtil.isNotBlank(systemLastaEnv)) {
            if (systemLastaEnv.equals("web")) {
                cmdList.add("-Dlasta.env=suggest");
            } else {
                cmdList.add("-Dlasta.env=" + systemLastaEnv);
            }
        } else if (StringUtil.isNotBlank(lastaEnv)) {
            cmdList.add("-Dlasta.env=" + lastaEnv);
        }

        addSystemProperty(cmdList, Constants.FESS_CONF_PATH, null, null);
        cmdList.add("-Dfess.suggest.process=true");
        if (logFilePath == null) {
            final String value = System.getProperty("fess.log.path");
            logFilePath = value != null ? value : new File(targetDir, "logs").getAbsolutePath();
        }
        cmdList.add("-Dfess.log.path=" + logFilePath);
        addSystemProperty(cmdList, "fess.log.name", "fess-suggest", "-suggest");
        if (logLevel == null) {
            addSystemProperty(cmdList, "fess.log.level", null, null);
        } else {
            cmdList.add("-Dfess.log.level=" + logLevel);
        }
        stream(fessConfig.getJvmSuggestOptionsAsArray()).of(
                stream -> stream.filter(StringUtil::isNotBlank).forEach(value -> cmdList.add(value)));

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

        if (StringUtil.isNotBlank(jvmOptions)) {
            split(jvmOptions, " ").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(s -> cmdList.add(s)));
        }

        cmdList.add(SuggestCreator.class.getCanonicalName());

        cmdList.add("--sessionId");
        cmdList.add(sessionId);

        File propFile = null;
        try {
            cmdList.add("-p");
            propFile = File.createTempFile("suggest_", ".properties");
            cmdList.add(propFile.getAbsolutePath());
            try (FileOutputStream out = new FileOutputStream(propFile)) {
                final Properties prop = new Properties();
                prop.putAll(ComponentUtil.getSystemProperties());
                prop.store(out, cmdList.toString());
            }

            final File baseDir = new File(servletContext.getRealPath("/WEB-INF")).getParentFile();

            if (logger.isInfoEnabled()) {
                logger.info("SuggestCreator: \nDirectory=" + baseDir + "\nOptions=" + cmdList);
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
                logger.info("SuggestCreator: Exit Code=" + exitValue + " - SuggestCreator Process Output:\n" + it.getOutput());
            }
            if (exitValue != 0) {
                throw new FessSystemException("Exit Code: " + exitValue + "\nOutput:\n" + it.getOutput());
            }
            ComponentUtil.getPopularWordHelper().clearCache();
        } catch (final FessSystemException e) {
            throw e;
        } catch (final InterruptedException e) {
            logger.warn("SuggestCreator Process interrupted.");
        } catch (final Exception e) {
            throw new FessSystemException("SuggestCreator Process terminated.", e);
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

}
