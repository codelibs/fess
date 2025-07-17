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
package org.codelibs.fess.helper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.exception.JobNotFoundException;
import org.codelibs.fess.exception.JobProcessingException;
import org.codelibs.fess.util.InputStreamThread;
import org.codelibs.fess.util.JobProcess;

import jakarta.annotation.PreDestroy;

/**
 * Helper class for managing system processes in Fess.
 * This class provides functionality to start, stop, and manage external processes
 * such as crawler processes, with proper resource cleanup and lifecycle management.
 */
public class ProcessHelper {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(ProcessHelper.class);

    /** Map of running processes indexed by session ID */
    protected final ConcurrentHashMap<String, JobProcess> runningProcessMap = new ConcurrentHashMap<>();

    /** Timeout in seconds for process destruction */
    protected int processDestroyTimeout = 10;

    /**
     * Default constructor for ProcessHelper.
     * Initializes the process management system with default timeout values.
     */
    public ProcessHelper() {
        // Default constructor
    }

    /**
     * Cleanup method called when the bean is destroyed.
     * Stops all running processes and cleans up resources.
     */
    @PreDestroy
    public void destroy() {
        for (final String sessionId : runningProcessMap.keySet()) {
            if (logger.isInfoEnabled()) {
                logger.info("Stopping process {}", sessionId);
            }
            if (destroyProcess(sessionId) == 0 && logger.isInfoEnabled()) {
                logger.info("Stopped process {}", sessionId);
            }
        }
    }

    /**
     * Starts a new process with the given session ID and command list.
     * Uses default buffer size and no output callback.
     *
     * @param sessionId unique identifier for the process session
     * @param cmdList list of command and arguments to execute
     * @param pbCall callback to configure the ProcessBuilder
     * @return JobProcess representing the started process
     */
    public JobProcess startProcess(final String sessionId, final List<String> cmdList, final Consumer<ProcessBuilder> pbCall) {
        return startProcess(sessionId, cmdList, pbCall, InputStreamThread.MAX_BUFFER_SIZE, null);
    }

    /**
     * Starts a new process with the given session ID, command list, buffer size, and output callback.
     * This method is synchronized to ensure thread safety when managing processes.
     *
     * @param sessionId unique identifier for the process session
     * @param cmdList list of command and arguments to execute
     * @param pbCall callback to configure the ProcessBuilder
     * @param bufferSize size of the buffer for process output
     * @param outputCallback callback to handle process output lines
     * @return JobProcess representing the started process
     * @throws JobProcessingException if the process cannot be started
     */
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

    /**
     * Destroys the process associated with the given session ID.
     *
     * @param sessionId unique identifier for the process session
     * @return exit code of the destroyed process, or -1 if the process was not found
     */
    public int destroyProcess(final String sessionId) {
        final JobProcess jobProcess = runningProcessMap.remove(sessionId);
        return destroyProcess(sessionId, jobProcess);
    }

    /**
     * Checks if any processes are currently running.
     *
     * @return true if at least one process is running, false otherwise
     */
    public boolean isProcessRunning() {
        return !runningProcessMap.isEmpty();
    }

    /**
     * Checks if the process with the given session ID is currently running.
     *
     * @param sessionId unique identifier for the process session
     * @return true if the process is running, false otherwise
     */
    public boolean isProcessRunning(final String sessionId) {
        final JobProcess jobProcess = runningProcessMap.get(sessionId);
        return jobProcess != null && jobProcess.getProcess().isAlive();
    }

    /**
     * Internal method to destroy a specific JobProcess.
     * Handles cleanup of streams, threads, and process termination.
     *
     * @param sessionId unique identifier for the process session
     * @param jobProcess the JobProcess to destroy
     * @return exit code of the destroyed process, or -1 if the process was null or could not be destroyed
     */
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

    /**
     * Gets the set of session IDs for all currently running processes.
     *
     * @return set of session IDs for running processes
     */
    public Set<String> getRunningSessionIdSet() {
        return runningProcessMap.keySet();
    }

    /**
     * Sets the timeout for process destruction.
     *
     * @param processDestroyTimeout timeout in seconds for process destruction
     */
    public void setProcessDestroyTimeout(final int processDestroyTimeout) {
        this.processDestroyTimeout = processDestroyTimeout;
    }

    /**
     * Sends a command to the process associated with the given session ID.
     *
     * @param sessionId unique identifier for the process session
     * @param command the command to send to the process
     * @throws JobNotFoundException if no process is found for the given session ID
     * @throws JobProcessingException if there's an error sending the command
     */
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
