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
package org.codelibs.fess.auth.chain;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.exception.InterruptedRuntimeException;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.exception.CommandExecutionException;
import org.codelibs.fess.opensearch.user.exentity.User;

/**
 * Authentication chain implementation that executes external commands for user operations.
 * Provides user management through command-line tool execution for password changes and user deletion.
 */
public class CommandChain implements AuthenticationChain {

    private static final Logger logger = LogManager.getLogger(CommandChain.class);

    /** Working directory for command execution. */
    protected File workingDirectory = null;

    /** Maximum number of output lines to capture. */
    protected int maxOutputLine = 1000;

    /** Command execution timeout in milliseconds. */
    protected long executionTimeout = 30L * 1000L; // 30sec

    /** Character encoding for command output. */
    protected String commandOutputEncoding = Charset.defaultCharset().displayName();

    /** Command array for user update operations. */
    protected String[] updateCommand;

    /** Command array for user deletion operations. */
    protected String[] deleteCommand;

    /** Array of target usernames for command execution. */
    protected String[] targetUsers;

    @Override
    public void update(final User user) {
        final String username = user.getName();
        final String password = user.getOriginalPassword();
        changePassword(username, password);
    }

    @Override
    public void delete(final User user) {
        final String username = user.getName();
        if (isTargetUser(username)) {
            executeCommand(deleteCommand, username, StringUtil.EMPTY);
        }
    }

    @Override
    public boolean changePassword(final String username, final String password) {
        if (isTargetUser(username) && StringUtil.isNotBlank(password)) {
            return executeCommand(updateCommand, username, password) == 0;
        }
        return true;
    }

    @Override
    public User load(final User user) {
        return user;
    }

    /**
     * Default constructor for CommandChain.
     */
    public CommandChain() {
        // Default constructor
    }

    /**
     * Checks if the given username is a target user for command execution.
     * @param username The username to check.
     * @return True if the user is a target user, false otherwise.
     */
    protected boolean isTargetUser(final String username) {
        if (targetUsers == null) {
            return true;
        }
        return stream(targetUsers).get(stream -> stream.anyMatch(s -> s.equals(username)));
    }

    /**
     * Executes an external command with the given parameters.
     * @param commands The command array to execute.
     * @param username The username parameter for the command.
     * @param password The password parameter for the command.
     * @return The exit code of the executed command.
     */
    protected int executeCommand(final String[] commands, final String username, final String password) {
        if (commands == null || commands.length == 0) {
            throw new CommandExecutionException("command is empty.");
        }

        // Log command template with masked password for security
        if (logger.isDebugEnabled()) {
            final String commandStr = stream(commands).get(stream -> stream.map(s -> {
                if ("$PASSWORD".equals(s)) {
                    return "***MASKED***";
                }
                if ("$USERNAME".equals(s)) {
                    return username;
                }
                return s;
            }).collect(java.util.stream.Collectors.joining(" ")));
            logger.debug("Executing command for user: username={}, command={}", username, commandStr);
        }

        final String[] cmds = stream(commands).get(stream -> stream.map(s -> {
            if ("$USERNAME".equals(s)) {
                return username;
            }
            if ("$PASSWORD".equals(s)) {
                return password;
            }
            return s;
        }).toArray(n -> new String[n]));
        final ProcessBuilder pb = new ProcessBuilder(cmds);
        if (workingDirectory != null) {
            pb.directory(workingDirectory);
        }
        pb.redirectErrorStream(true);

        Process currentProcess = null;
        MonitorThread mt = null;
        try {
            currentProcess = pb.start();

            // monitoring
            mt = new MonitorThread(currentProcess, executionTimeout);
            mt.start();

            final InputStreamThread it = new InputStreamThread(currentProcess.getInputStream(), commandOutputEncoding, maxOutputLine);
            it.start();

            currentProcess.waitFor();
            it.join(5000);

            if (mt.isTeminated()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Command execution timeout for user: username={}", username);
                }
                throw new CommandExecutionException("The command execution is timeout for user: " + username);
            }

            final int exitValue = currentProcess.exitValue();

            if (logger.isInfoEnabled()) {
                logger.info("Command execution completed for user: username={}, exitCode={}", username, exitValue);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Process output:\n{}", it.getOutput());
            }
            if (exitValue == 143 && mt.isTeminated()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Command execution timeout (exit 143) for user: username={}", username);
                }
                throw new CommandExecutionException("The command execution is timeout for user: " + username);
            }
            return exitValue;
        } catch (final CrawlerSystemException e) {
            throw e;
        } catch (final InterruptedException e) {
            if (mt != null && mt.isTeminated()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Command execution interrupted due to timeout for user: username={}", username, e);
                }
                throw new CommandExecutionException("The command execution is timeout for user: " + username, e);
            }
            throw new InterruptedRuntimeException(e);
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Command execution failed for user: username={}, error={}", username, e.getMessage(), e);
            }
            throw new CommandExecutionException("Process terminated for user: " + username, e);
        } finally {
            if (mt != null) {
                mt.setFinished(true);
                try {
                    mt.interrupt();
                } catch (final Exception e) {
                    // ignore
                }
            }
            if (currentProcess != null) {
                try {
                    currentProcess.destroy();
                } catch (final Exception e) {
                    // ignore
                }
            }
            currentProcess = null;

        }
    }

    /**
     * Monitor thread that handles process timeout and termination.
     * This thread sleeps for the specified timeout duration and terminates the process if it hasn't finished.
     */
    protected static class MonitorThread extends Thread {
        /** The process to monitor. */
        private final Process process;

        /** The timeout duration in milliseconds. */
        private final long timeout;

        /** Flag indicating if the process has finished. */
        private boolean finished = false;

        /** Flag indicating if the process has been terminated. */
        private boolean teminated = false;

        /**
         * Constructor for MonitorThread.
         * @param process The process to monitor.
         * @param timeout The timeout duration in milliseconds.
         */
        public MonitorThread(final Process process, final long timeout) {
            this.process = process;
            this.timeout = timeout;
        }

        /**
         * Runs the monitor thread, sleeping for the timeout duration and terminating the process if needed.
         */
        @Override
        public void run() {
            ThreadUtil.sleepQuietly(timeout);

            if (!finished) {
                try {
                    process.destroy();
                    teminated = true;
                } catch (final Exception e) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Could not kill the subprocess.", e);
                    }
                }
            }
        }

        /**
         * Sets the finished flag to indicate whether the process has completed.
         * @param finished True if the process has finished, false otherwise.
         */
        public void setFinished(final boolean finished) {
            this.finished = finished;
        }

        /**
         * Checks if the process has been terminated due to timeout.
         * @return True if the process was terminated, false otherwise.
         */
        public boolean isTeminated() {
            return teminated;
        }
    }

    /**
     * Thread that reads input stream data and buffers it for later retrieval.
     * Captures output from command execution with configurable line buffering.
     */
    protected static class InputStreamThread extends Thread {

        /** Buffered reader for input stream. */
        private BufferedReader br;

        /** List to store captured output lines. */
        private final List<String> list = new LinkedList<>();

        /** Maximum number of lines to buffer. */
        private final int maxLineBuffer;

        /**
         * Constructor for InputStreamThread.
         * @param is The input stream to read from.
         * @param charset The character encoding to use.
         * @param maxOutputLineBuffer The maximum number of lines to buffer.
         */
        public InputStreamThread(final InputStream is, final String charset, final int maxOutputLineBuffer) {
            try {
                br = new BufferedReader(new InputStreamReader(is, charset));
            } catch (final UnsupportedEncodingException e) {
                br = new BufferedReader(new InputStreamReader(is, Constants.UTF_8_CHARSET));
            }
            maxLineBuffer = maxOutputLineBuffer;
        }

        /**
         * Runs the input stream thread, reading lines and buffering them.
         */
        @Override
        public void run() {
            for (;;) {
                try {
                    final String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug(line);
                    }
                    list.add(line);
                    if (list.size() > maxLineBuffer) {
                        list.remove(0);
                    }
                } catch (final IOException e) {
                    throw new CrawlerSystemException(e);
                }
            }
        }

        /**
         * Gets the captured output as a single string.
         * @return The captured output with newlines.
         */
        public String getOutput() {
            final StringBuilder buf = new StringBuilder(100);
            for (final String value : list) {
                buf.append(value).append("\n");
            }
            return buf.toString();
        }

    }

    /**
     * Sets the working directory for command execution.
     * @param workingDirectory The working directory.
     */
    public void setWorkingDirectory(final File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    /**
     * Sets the maximum number of output lines to capture.
     * @param maxOutputLine The maximum output line count.
     */
    public void setMaxOutputLine(final int maxOutputLine) {
        this.maxOutputLine = maxOutputLine;
    }

    /**
     * Sets the command execution timeout.
     * @param executionTimeout The execution timeout in milliseconds.
     */
    public void setExecutionTimeout(final long executionTimeout) {
        this.executionTimeout = executionTimeout;
    }

    /**
     * Sets the character encoding for command output.
     * @param commandOutputEncoding The character encoding.
     */
    public void setCommandOutputEncoding(final String commandOutputEncoding) {
        this.commandOutputEncoding = commandOutputEncoding;
    }

    /**
     * Sets the command array for user update operations.
     * @param updateCommand The update command array.
     */
    public void setUpdateCommand(final String[] updateCommand) {
        this.updateCommand = updateCommand;
    }

    /**
     * Sets the command array for user deletion operations.
     * @param deleteCommand The delete command array.
     */
    public void setDeleteCommand(final String[] deleteCommand) {
        this.deleteCommand = deleteCommand;
    }

    /**
     * Sets the array of target usernames for command execution.
     * @param targetUsers The target users array.
     */
    public void setTargetUsers(final String[] targetUsers) {
        this.targetUsers = targetUsers;
    }

}
