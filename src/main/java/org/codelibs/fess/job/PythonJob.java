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
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.InputStreamThread;
import org.codelibs.fess.util.JobProcess;
import org.codelibs.fess.util.SystemUtil;

public class PythonJob extends ExecJob {
    static final Logger logger = LogManager.getLogger(PythonJob.class);

    protected String filename;

    protected List<String> argList = new ArrayList<>();

    public PythonJob filename(final String filename) {
        this.filename = filename;
        return this;
    }

    public PythonJob arg(final String value) {
        argList.add(value);
        return this;
    }

    public PythonJob args(final String... values) {
        stream(values).of(stream -> stream.forEach(argList::add));
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
            executePython();
        } catch (final Exception e) {
            logger.warn("Failed to run python command.", e);
            resultBuf.append(e.getMessage()).append("\n");
        } finally {
            if (timeoutTask != null && !timeoutTask.isCanceled()) {
                timeoutTask.cancel();
            }
        }

        return resultBuf.toString();

    }

    protected void executePython() {
        final List<String> cmdList = new ArrayList<>();
        final ServletContext servletContext = ComponentUtil.getComponent(ServletContext.class);
        final ProcessHelper processHelper = ComponentUtil.getProcessHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        if (StringUtil.isBlank(filename)) {
            throw new JobProcessingException("Python script is not specified.");
        }

        cmdList.add(fessConfig.getPythonCommandPath());

        cmdList.add(getPyFilePath());

        cmdList.addAll(argList);

        try {

            final File baseDir = new File(servletContext.getRealPath("/WEB-INF")).getParentFile();

            if (logger.isInfoEnabled()) {
                logger.info("Python: \nDirectory={}\nOptions={}", baseDir, cmdList);
            }

            final JobProcess jobProcess = processHelper.startProcess(sessionId, cmdList, pb -> {
                pb.directory(baseDir);
                pb.redirectErrorStream(true);
                final Map<String, String> environment = pb.environment();
                environment.put("SESSION_ID", sessionId);
                environment.put("OPENSEARCH_URL", SystemUtil.getSearchEngineHttpAddress());
            });

            final InputStreamThread it = jobProcess.getInputStreamThread();
            it.start();

            final Process currentProcess = jobProcess.getProcess();
            currentProcess.waitFor();
            it.join(5000);

            final int exitValue = currentProcess.exitValue();

            if (logger.isInfoEnabled()) {
                logger.info("Python: Exit Code={} - Process Output:\n{}", exitValue, it.getOutput());
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
            throw new JobProcessingException("Python Process terminated.", e);
        } finally {
            processHelper.destroyProcess(sessionId);

        }
    }

    protected String getPyFilePath() {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("WEB-INF");
        buf.append(File.separator);
        buf.append("env");
        buf.append(File.separator);
        buf.append(getExecuteType());
        buf.append(File.separator);
        buf.append("resources");
        buf.append(File.separator);
        buf.append(filename.replaceAll("\\.\\.+", ""));
        return buf.toString();
    }

    @Override
    protected String getExecuteType() {
        return Constants.EXECUTE_TYPE_PYTHON;
    }
}
