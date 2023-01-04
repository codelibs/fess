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
import org.codelibs.fess.es.user.exentity.User;
import org.codelibs.fess.exception.CommandExecutionException;

public class CommandChain implements AuthenticationChain {

    private static final Logger logger = LogManager.getLogger(CommandChain.class);

    protected File workingDirectory = null;

    protected int maxOutputLine = 1000;

    protected long executionTimeout = 30L * 1000L; // 30sec

    protected String commandOutputEncoding = Charset.defaultCharset().displayName();

    protected String[] updateCommand;

    protected String[] deleteCommand;

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

    protected boolean isTargetUser(final String username) {
        if (targetUsers == null) {
            return true;
        }
        return stream(targetUsers).get(stream -> stream.anyMatch(s -> s.equals(username)));
    }

    protected int executeCommand(final String[] commands, final String username, final String password) {
        if (commands == null || commands.length == 0) {
            throw new CommandExecutionException("command is empty.");
        }

        if (logger.isInfoEnabled()) {
            logger.info("Command: {}", String.join(" ", commands));
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
                throw new CommandExecutionException("The command execution is timeout: " + String.join(" ", commands));
            }

            final int exitValue = currentProcess.exitValue();

            if (logger.isInfoEnabled()) {
                logger.info("Exit Code: {} - Process Output:\n{}", exitValue, it.getOutput());
            }
            if (exitValue == 143 && mt.isTeminated()) {
                throw new CommandExecutionException("The command execution is timeout: " + String.join(" ", commands));
            }
            return exitValue;
        } catch (final CrawlerSystemException e) {
            throw e;
        } catch (final InterruptedException e) {
            if (mt != null && mt.isTeminated()) {
                throw new CommandExecutionException("The command execution is timeout: " + String.join(" ", commands), e);
            }
            throw new InterruptedRuntimeException(e);
        } catch (final Exception e) {
            throw new CommandExecutionException("Process terminated.", e);
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

    protected static class MonitorThread extends Thread {
        private final Process process;

        private final long timeout;

        private boolean finished = false;

        private boolean teminated = false;

        public MonitorThread(final Process process, final long timeout) {
            this.process = process;
            this.timeout = timeout;
        }

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
         * @param finished
         *            The finished to set.
         */
        public void setFinished(final boolean finished) {
            this.finished = finished;
        }

        /**
         * @return Returns the teminated.
         */
        public boolean isTeminated() {
            return teminated;
        }
    }

    protected static class InputStreamThread extends Thread {

        private BufferedReader br;

        private final List<String> list = new LinkedList<>();

        private final int maxLineBuffer;

        public InputStreamThread(final InputStream is, final String charset, final int maxOutputLineBuffer) {
            try {
                br = new BufferedReader(new InputStreamReader(is, charset));
            } catch (final UnsupportedEncodingException e) {
                br = new BufferedReader(new InputStreamReader(is, Constants.UTF_8_CHARSET));
            }
            maxLineBuffer = maxOutputLineBuffer;
        }

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

        public String getOutput() {
            final StringBuilder buf = new StringBuilder(100);
            for (final String value : list) {
                buf.append(value).append("\n");
            }
            return buf.toString();
        }

    }

    public void setWorkingDirectory(final File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public void setMaxOutputLine(final int maxOutputLine) {
        this.maxOutputLine = maxOutputLine;
    }

    public void setExecutionTimeout(final long executionTimeout) {
        this.executionTimeout = executionTimeout;
    }

    public void setCommandOutputEncoding(final String commandOutputEncoding) {
        this.commandOutputEncoding = commandOutputEncoding;
    }

    public void setUpdateCommand(final String[] updateCommand) {
        this.updateCommand = updateCommand;
    }

    public void setDeleteCommand(final String[] deleteCommand) {
        this.deleteCommand = deleteCommand;
    }

    public void setTargetUsers(final String[] targetUsers) {
        this.targetUsers = targetUsers;
    }

}
