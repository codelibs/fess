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
package org.codelibs.fess.job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.opensearch.config.exentity.ScheduledJob;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.exception.IORuntimeException;
import org.lastaflute.job.LaJobRuntime;

/**
 * Abstract base class for executable jobs in the Fess search engine.
 * This class provides common functionality for job execution including process management,
 * logging configuration, JVM options, and timeout handling.
 *
 * <p>Subclasses must implement the abstract methods to define specific job behavior
 * and execution type identification.</p>
 *
 * @version 1.0
 */
public abstract class ExecJob {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(ExecJob.class);

    /** The job executor responsible for running this job */
    protected JobExecutor jobExecutor;

    /** Unique session identifier for this job execution */
    protected String sessionId;

    /** Flag indicating whether to use local Fesen instance */
    protected boolean useLocalFesen = true;

    /** Path to the log file for this job execution */
    protected String logFilePath;

    /** Log level for this job execution */
    protected String logLevel;

    /** Suffix to append to log file names */
    protected String logSuffix = StringUtil.EMPTY;

    /** List of JVM options to apply when executing the job */
    protected List<String> jvmOptions = new ArrayList<>();

    /** Lasta environment configuration */
    protected String lastaEnv;

    /** Timeout in seconds for job execution (-1 means no timeout) */
    protected int timeout = -1; // sec

    /** Flag indicating whether the process has timed out */
    protected boolean processTimeout = false;

    /**
     * Default constructor for ExecJob.
     * Initializes default values for job configuration.
     */
    protected ExecJob() {
        // Default constructor
    }

    /**
     * Executes the job and returns the result as a string.
     * This method contains the main business logic for the specific job implementation.
     *
     * @return the execution result message or summary
     */
    public abstract String execute();

    /**
     * Returns the execution type identifier for this job.
     * This type is used for classpath construction, configuration, and logging purposes.
     *
     * @return the execution type (e.g., "crawler", "suggest", etc.)
     */
    protected abstract String getExecuteType();

    /**
     * Executes the job with the specified job executor.
     * This method sets the job executor and then calls the abstract execute method.
     *
     * @param jobExecutor the job executor to use for execution
     * @return the execution result message or summary
     */
    public String execute(final JobExecutor jobExecutor) {
        jobExecutor(jobExecutor);
        return execute();
    }

    /**
     * Sets the job executor for this job.
     *
     * @param jobExecutor the job executor to set
     * @return this ExecJob instance for method chaining
     */
    public ExecJob jobExecutor(final JobExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
        return this;
    }

    /**
     * Sets the session ID for this job execution.
     *
     * @param sessionId the unique session identifier
     * @return this ExecJob instance for method chaining
     */
    public ExecJob sessionId(final String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    /**
     * Sets the log file path for this job execution.
     *
     * @param logFilePath the path to the log file
     * @return this ExecJob instance for method chaining
     */
    public ExecJob logFilePath(final String logFilePath) {
        this.logFilePath = logFilePath;
        return this;
    }

    /**
     * Sets the log level for this job execution.
     *
     * @param logLevel the log level (e.g., DEBUG, INFO, WARN, ERROR)
     * @return this ExecJob instance for method chaining
     */
    public ExecJob logLevel(final String logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    /**
     * Sets the log suffix for this job execution.
     * The suffix is trimmed and whitespace is replaced with underscores.
     *
     * @param logSuffix the suffix to append to log file names
     * @return this ExecJob instance for method chaining
     */
    public ExecJob logSuffix(final String logSuffix) {
        this.logSuffix = logSuffix.trim().replaceAll("\\s", "_");
        return this;
    }

    /**
     * Sets the timeout for this job execution.
     *
     * @param timeout the timeout in seconds (-1 for no timeout)
     * @return this ExecJob instance for method chaining
     */
    public ExecJob timeout(final int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Sets whether to use local Fesen instance.
     *
     * @param useLocalFesen true to use local Fesen, false otherwise
     * @return this ExecJob instance for method chaining
     */
    public ExecJob useLocalFesen(final boolean useLocalFesen) {
        this.useLocalFesen = useLocalFesen;
        return this;
    }

    /**
     * Enables remote debugging for this job execution.
     * Adds JVM options for remote debugging on localhost:8000.
     *
     * @return this ExecJob instance for method chaining
     */
    public ExecJob remoteDebug() {
        return jvmOptions("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=localhost:8000");
    }

    /**
     * Enables garbage collection logging for this job execution.
     * Configures JVM options for comprehensive GC logging with rotation.
     *
     * @return this ExecJob instance for method chaining
     */
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

    /**
     * Adds JVM options for this job execution.
     *
     * @param options the JVM options to add
     * @return this ExecJob instance for method chaining
     */
    public ExecJob jvmOptions(final String... options) {
        Collections.addAll(jvmOptions, options);
        return this;
    }

    /**
     * Sets the Lasta environment configuration.
     *
     * @param env the Lasta environment string
     * @return this ExecJob instance for method chaining
     */
    public ExecJob lastaEnv(final String env) {
        lastaEnv = env;
        return this;
    }

    /**
     * Adds a system property to the command list.
     * If the property exists in the system, it uses that value with optional append value.
     * Otherwise, it uses the default value if provided.
     *
     * @param cmdList the command list to add the property to
     * @param name the property name
     * @param defaultValue the default value to use if property doesn't exist
     * @param appendValue the value to append to the property value
     */
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

    /**
     * Adds all Fess configuration properties to the command list.
     * Properties starting with the Fess config prefix are included.
     *
     * @param cmdList the command list to add properties to
     */
    protected void addFessConfigProperties(final List<String> cmdList) {
        System.getProperties().keySet().stream().filter(k -> k != null && k.toString().startsWith(Constants.FESS_CONFIG_PREFIX))
                .forEach(k -> addSystemProperty(cmdList, k.toString(), null, null));
    }

    /**
     * Adds all Fess system properties to the command list.
     * Properties starting with the Fess system property prefix are included.
     *
     * @param cmdList the command list to add properties to
     */
    protected void addFessSystemProperties(final List<String> cmdList) {
        System.getProperties().keySet().stream().filter(k -> k != null && k.toString().startsWith(Constants.SYSTEM_PROP_PREFIX))
                .forEach(k -> addSystemProperty(cmdList, k.toString(), null, null));
    }

    /**
     * Adds custom system properties that match the given regex pattern to the command list.
     *
     * @param cmdList the command list to add properties to
     * @param regex the regular expression pattern to match property names
     */
    protected void addFessCustomSystemProperties(final List<String> cmdList, final String regex) {
        if (StringUtil.isNotBlank(regex)) {
            final Pattern pattern = Pattern.compile(regex);
            System.getProperties().keySet().stream().filter(k -> k != null && pattern.matcher(k.toString()).matches())
                    .forEach(k -> addSystemProperty(cmdList, k.toString(), null, null));
        }
    }

    /**
     * Deletes the specified temporary directory.
     * Logs a warning if the directory cannot be deleted.
     *
     * @param ownTmpDir the temporary directory to delete
     */
    protected void deleteTempDir(final File ownTmpDir) {
        if (ownTmpDir == null) {
            return;
        }
        if (!FileUtils.deleteQuietly(ownTmpDir)) {
            logger.warn("Could not delete a temp dir: {}", ownTmpDir.getAbsolutePath());
        }
    }

    /**
     * Appends JAR files from the specified directory to the classpath buffer.
     *
     * @param cpSeparator the classpath separator to use
     * @param buf the StringBuilder to append to
     * @param libDir the directory containing JAR files
     * @param basePath the base path to prepend to JAR file names
     */
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

    /**
     * Creates a timeout task for this job execution.
     * If timeout is not set or is &lt;= 0, returns null.
     *
     * @return the timeout task, or null if no timeout is configured
     */
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

    /**
     * Creates and stores system properties to the specified file.
     * Includes system properties and job runtime information if available.
     *
     * @param cmdList the command list (used as comment in properties file)
     * @param propFile the file to store properties to
     * @throws IORuntimeException if an I/O error occurs
     */
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

    /**
     * Generates a log name using the specified prefix, execution type, and suffix.
     *
     * @param logPrefix the prefix for the log name
     * @return the generated log name
     */
    protected String getLogName(final String logPrefix) {
        if (logSuffix.length() > 0) {
            return logPrefix + "-" + getExecuteType() + "-" + logSuffix;
        }
        return logPrefix + "-" + getExecuteType();
    }
}
