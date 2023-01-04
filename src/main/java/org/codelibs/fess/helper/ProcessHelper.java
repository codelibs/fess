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
package org.codelibs.fess.helper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.PreDestroy;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.JobNotFoundException;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.util.InputStreamThread;
import org.codelibs.fess.util.JobProcess;

public class ProcessHelper {
    private static final Logger logger = LogManager.getLogger(ProcessHelper.class);

    protected final ConcurrentHashMap<String, JobProcess> runningProcessMap = new ConcurrentHashMap<>();

    protected int processDestroyTimeout = 10;

    @PreDestroy
    public void destroy() {
        for (final String sessionId : runningProcessMap.keySet()) {
            if (logger.isInfoEnabled()) {
                logger.info("Stopping process {}", sessionId);
            }
            if ((destroyProcess(sessionId) == 0) && logger.isInfoEnabled()) {
                logger.info("Stopped process {}", sessionId);
            }
        }
    }

    public JobProcess startProcess(final String sessionId, final List<String> cmdList, final Consumer<ProcessBuilder> pbCall) {
        return startProcess(sessionId, cmdList, pbCall, InputStreamThread.MAX_BUFFER_SIZE, null);
    }

    public synchronized JobProcess startProcess(final String sessionId, final List<String> cmdList, final Consumer<ProcessBuilder> pbCall,
            final int bufferSize, final Consumer<String> outputCallback) {
        final ProcessBuilder pb = new ProcessBuilder(cmdList);
        pbCall.accept(pb);
        destroyProcess(sessionId);
        JobProcess jobProcess;
        try {
            jobProcess = new JobProcess(pb.start(), bufferSize, outputCallback);
            destroyProcess(sessionId, runningProcessMap.putIfAbsent(sessionId, jobProcess));
            return jobProcess;
        } catch (final IOException e) {
            throw new JobProcessingException("Crawler Process terminated.", e);
        }
    }

    public int destroyProcess(final String sessionId) {
        final JobProcess jobProcess = runningProcessMap.remove(sessionId);
        return destroyProcess(sessionId, jobProcess);
    }

    public boolean isProcessRunning() {
        return !runningProcessMap.isEmpty();
    }

    public boolean isProcessRunning(final String sessionId) {
        final JobProcess jobProcess = runningProcessMap.get(sessionId);
        return jobProcess != null && jobProcess.getProcess().isAlive();
    }

    protected int destroyProcess(final String sessionId, final JobProcess jobProcess) {
        if (jobProcess != null) {
            final InputStreamThread ist = jobProcess.getInputStreamThread();
            try {
                ist.interrupt();
            } catch (final Exception e) {
                logger.warn("Could not interrupt a thread of an input stream.", e);
            }

            final CountDownLatch latch = new CountDownLatch(3);
            final Process process = jobProcess.getProcess();
            new Thread(() -> {
                try {
                    CloseableUtil.closeQuietly(process.getInputStream());
                } catch (final Exception e) {
                    logger.warn("Could not close a process input stream.", e);
                } finally {
                    latch.countDown();
                }
            }, "ProcessCloser-input-" + sessionId).start();
            new Thread(() -> {
                try {
                    CloseableUtil.closeQuietly(process.getErrorStream());
                } catch (final Exception e) {
                    logger.warn("Could not close a process error stream.", e);
                } finally {
                    latch.countDown();
                }
            }, "ProcessCloser-error-" + sessionId).start();
            new Thread(() -> {
                try {
                    CloseableUtil.closeQuietly(process.getOutputStream());
                } catch (final Exception e) {
                    logger.warn("Could not close a process output stream.", e);
                } finally {
                    latch.countDown();
                }
            }, "ProcessCloser-output-" + sessionId).start();

            try {
                latch.await(10, TimeUnit.SECONDS);
            } catch (final InterruptedException e) {
                logger.warn("Interrupted to wait a process.", e);
            }
            try {
                process.destroyForcibly().waitFor(processDestroyTimeout, TimeUnit.SECONDS);
                return process.exitValue();
            } catch (final Exception e) {
                logger.error("Could not destroy a process correctly.", e);
            }
        }
        return -1;
    }

    public Set<String> getRunningSessionIdSet() {
        return runningProcessMap.keySet();
    }

    public void setProcessDestroyTimeout(final int processDestroyTimeout) {
        this.processDestroyTimeout = processDestroyTimeout;
    }

    public void sendCommand(final String sessionId, final String command) {
        final JobProcess jobProcess = runningProcessMap.get(sessionId);
        if (jobProcess == null) {
            throw new JobNotFoundException("Job for " + sessionId + " is not found.");
        }
        try {
            final OutputStream out = jobProcess.getProcess().getOutputStream();
            IOUtils.write(command + "\n", out, Constants.CHARSET_UTF_8);
            out.flush();
        } catch (final IOException e) {
            throw new JobProcessingException(e);
        }
    }
}
