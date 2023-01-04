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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.config.exentity.ScheduledJob;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.job.LaJobRuntime;

public abstract class ExecJob {

    private static final Logger logger = LogManager.getLogger(ExecJob.class);

    protected JobExecutor jobExecutor;

    protected String sessionId;

    protected boolean useLocalFesen = true;

    protected String logFilePath;

    protected String logLevel;

    protected String logSuffix = StringUtil.EMPTY;

    protected List<String> jvmOptions = new ArrayList<>();

    protected String lastaEnv;

    protected int timeout = -1; // sec

    protected boolean processTimeout = false;

    public abstract String execute();

    protected abstract String getExecuteType();

    public String execute(final JobExecutor jobExecutor) {
        jobExecutor(jobExecutor);
        return execute();
    }

    public ExecJob jobExecutor(final JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
        return this;
    }

    public ExecJob sessionId(final String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public ExecJob logFilePath(final String logFilePath) {
        this.logFilePath = logFilePath;
        return this;
    }

    public ExecJob logLevel(final String logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public ExecJob logSuffix(final String logSuffix) {
        this.logSuffix = logSuffix.trim().replaceAll("\\s", "_");
        return this;
    }

    public ExecJob timeout(final int timeout) {
        this.timeout = timeout;
        return this;
    }

    public ExecJob useLocalFesen(final boolean useLocalFesen) {
        this.useLocalFesen = useLocalFesen;
        return this;
    }

    public ExecJob remoteDebug() {
        return jvmOptions("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=localhost:8000");
    }

    public ExecJob gcLogging() {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("-Xlog:gc*,gc+age=trace,safepoint:file=");
        if (logFilePath != null) {
            buf.append(logFilePath);
        } else {
            buf.append(ComponentUtil.getSystemHelper().getLogFilePath());
        }
        buf.append(File.separator);
        buf.append("gc-").append(getExecuteType()).append(".log");
        buf.append(":utctime,pid,tags:filecount=5,filesize=64m");
        return jvmOptions(buf.toString());
    }

    public ExecJob jvmOptions(final String... options) {
        Collections.addAll(this.jvmOptions, options);
        return this;
    }

    public ExecJob lastaEnv(final String env) {
        this.lastaEnv = env;
        return this;
    }

    protected void addSystemProperty(final List<String> cmdList, final String name, final String defaultValue, final String appendValue) {
        final String value = System.getProperty(name);
        if (value != null) {
            final StringBuilder buf = new StringBuilder();
            buf.append("-D").append(name).append("=").append(value);
            if (appendValue != null) {
                buf.append(appendValue);
            }
            cmdList.add(buf.toString());
        } else if (defaultValue != null) {
            cmdList.add("-D" + name + "=" + defaultValue);
        }
    }

    protected void addFessConfigProperties(final List<String> cmdList) {
        System.getProperties().keySet().stream().filter(k -> k != null && k.toString().startsWith(Constants.FESS_CONFIG_PREFIX))
                .forEach(k -> addSystemProperty(cmdList, k.toString(), null, null));
    }

    protected void addFessSystemProperties(final List<String> cmdList) {
        System.getProperties().keySet().stream().filter(k -> k != null && k.toString().startsWith(Constants.SYSTEM_PROP_PREFIX))
                .forEach(k -> addSystemProperty(cmdList, k.toString(), null, null));
    }

    protected void deleteTempDir(final File ownTmpDir) {
        if (ownTmpDir == null) {
            return;
        }
        if (!FileUtils.deleteQuietly(ownTmpDir)) {
            logger.warn("Could not delete a temp dir: {}", ownTmpDir.getAbsolutePath());
        }
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

    protected TimeoutTask createTimeoutTask() {
        if (timeout <= 0) {
            return null;
        }
        return TimeoutManager.getInstance().addTimeoutTarget(() -> {
            logger.warn("Process is terminated due to {} second exceeded.", timeout);
            ComponentUtil.getProcessHelper().destroyProcess(sessionId);
            processTimeout = true;
        }, timeout, false);
    }

    protected void createSystemProperties(final List<String> cmdList, final File propFile) {
        try (FileOutputStream out = new FileOutputStream(propFile)) {
            final Properties prop = new Properties();
            prop.putAll(ComponentUtil.getSystemProperties());
            final LaJobRuntime jobRuntime = ComponentUtil.getJobHelper().getJobRuntime();
            if (jobRuntime != null) {
                final ScheduledJob job = (ScheduledJob) jobRuntime.getParameterMap().get(Constants.SCHEDULED_JOB);
                if (job != null) {
                    prop.setProperty("job.runtime.id", job.getId());
                    prop.setProperty("job.runtime.name", job.getName());
                }
            }
            prop.store(out, cmdList.toString());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    protected String getLogName(final String logPrefix) {
        if (logSuffix.length() > 0) {
            return logPrefix + "-" + getExecuteType() + "-" + logSuffix;
        }
        return logPrefix + "-" + getExecuteType();
    }
}
