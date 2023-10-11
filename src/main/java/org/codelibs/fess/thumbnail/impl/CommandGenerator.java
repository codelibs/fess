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
package org.codelibs.fess.thumbnail.impl;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.concurrent.CommonPoolUtil;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.InputStreamThread;

public class CommandGenerator extends BaseThumbnailGenerator {
    private static final Logger logger = LogManager.getLogger(CommandGenerator.class);

    protected List<String> commandList;

    protected long commandTimeout = 30 * 1000L;// 30sec

    protected long commandDestroyTimeout = 5 * 1000L;// 5sec

    protected File baseDir;

    private Timer destoryTimer;

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        if (baseDir == null) {
            baseDir = new File(System.getProperty("java.io.tmpdir"));
        }
        destoryTimer = new Timer("CommandGeneratorDestoryTimer-" + System.currentTimeMillis(), true);
        updateProperties();
    }

    protected void updateProperties() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String commandTimeoutStr = fessConfig.getSystemProperty("thumbnail.command.timeout");
        if (commandTimeoutStr != null) {
            commandTimeout = Long.valueOf(commandTimeoutStr);
        }
        final String commandDestroyTimeoutStr = fessConfig.getSystemProperty("thumbnail.command.destroy.timeout");
        if (commandDestroyTimeoutStr != null) {
            commandDestroyTimeout = Long.valueOf(commandDestroyTimeoutStr);
        }
    }

    @Override
    public void destroy() {
        destoryTimer.cancel();
        destoryTimer = null;
    }

    @Override
    public boolean generate(final String thumbnailId, final File outputFile) {
        if (logger.isDebugEnabled()) {
            logger.debug("Generate Thumbnail: {}", thumbnailId);
        }

        if (outputFile.exists()) {
            if (logger.isDebugEnabled()) {
                logger.debug("The thumbnail file exists: {}", outputFile.getAbsolutePath());
            }
            return true;
        }

        final File parentFile = outputFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!parentFile.isDirectory()) {
            logger.warn("Not found: {}", parentFile.getAbsolutePath());
            return false;
        }

        return process(thumbnailId, responseData -> {
            final File tempFile = ComponentUtil.getSystemHelper().createTempFile("thumbnail_", "");
            try {
                CopyUtil.copy(responseData.getResponseBody(), tempFile);

                final String tempPath = tempFile.getAbsolutePath();
                final String outputPath = outputFile.getAbsolutePath();
                final List<String> cmdList = new ArrayList<>();
                for (final String value : commandList) {
                    cmdList.add(expandPath(value.replace("${url}", tempPath).replace("${outputFile}", outputPath)));
                }

                executeCommand(thumbnailId, cmdList);

                if (outputFile.isFile() && outputFile.length() == 0) {
                    logger.warn("Thumbnail File is empty. ID is {}", thumbnailId);
                    if (outputFile.delete()) {
                        logger.info("Deleted: {}", outputFile.getAbsolutePath());
                    }
                    updateThumbnailField(thumbnailId, StringUtil.EMPTY);
                    return false;
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("Thumbnail File: {}", outputPath);
                }
                return true;
            } catch (final Exception e) {
                logger.warn("Failed to process ", e);
                updateThumbnailField(thumbnailId, StringUtil.EMPTY);
                return false;
            } finally {
                if (tempFile != null && !tempFile.delete()) {
                    logger.debug("Failed to delete {}", tempFile.getAbsolutePath());
                }
            }
        });

    }

    protected void executeCommand(final String thumbnailId, final List<String> cmdList) {
        ProcessDestroyer task = null;
        try {
            final ProcessBuilder pb = new ProcessBuilder(cmdList);
            pb.directory(baseDir);
            pb.redirectErrorStream(true);

            if (logger.isDebugEnabled()) {
                logger.debug("Thumbnail Command: {}", cmdList);
            }

            final Process p = pb.start();

            final InputStreamThread ist = new InputStreamThread(p.getInputStream(), Charset.defaultCharset(), 0, s -> {
                if (logger.isDebugEnabled()) {
                    logger.debug(s);
                }
            });
            task = new ProcessDestroyer(p, ist, commandDestroyTimeout);
            destoryTimer.schedule(task, commandTimeout);
            ist.start();

            if (logger.isDebugEnabled()) {
                logger.debug("Waiting for {}.", getName());
            }
            if (!p.waitFor(commandTimeout + commandDestroyTimeout, TimeUnit.MILLISECONDS)) {
                logger.warn("Destroying {} because of the process timeout.", getName());
                p.destroy();
            }

            final int exitValue = p.exitValue();
            if (exitValue != 0) {
                logger.warn("{} is failed (exit code:{}, timeout:{}): {}", getName(), exitValue, task.isExecuted(), commandList);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("{} is finished with exit code {}.", getName(), exitValue);
            }
        } catch (final Exception e) {
            logger.warn("Failed to generate a thumbnail of {}: {}", thumbnailId, commandList, e);
        }
        if (task != null && !task.isExecuted()) {
            task.cancel();
        }
    }

    protected static class ProcessDestroyer extends TimerTask {

        private final Process p;

        private final InputStreamThread ist;

        private final AtomicBoolean executed = new AtomicBoolean(false);

        private final long timeout;

        public ProcessDestroyer(final Process p, final InputStreamThread ist, final long timeout) {
            this.p = p;
            this.ist = ist;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            if (!p.isAlive()) {
                return;
            }

            executed.set(true);

            if (logger.isDebugEnabled()) {
                logger.debug("Interrupting a stream thread.");
            }
            ist.interrupt();

            CommonPoolUtil.execute(() -> {
                try {
                    CloseableUtil.closeQuietly(p.getInputStream());
                } catch (final Exception e) {
                    logger.warn("Could not close a process input stream.", e);
                }
            });
            CommonPoolUtil.execute(() -> {
                try {
                    CloseableUtil.closeQuietly(p.getErrorStream());
                } catch (final Exception e) {
                    logger.warn("Could not close a process error stream.", e);
                }
            });
            CommonPoolUtil.execute(() -> {
                try {
                    CloseableUtil.closeQuietly(p.getOutputStream());
                } catch (final Exception e) {
                    logger.warn("Could not close a process output stream.", e);
                }
            });

            if (logger.isDebugEnabled()) {
                logger.debug("Terminating process {}.", p);
            }
            try {
                if (!p.destroyForcibly().waitFor(timeout, TimeUnit.MILLISECONDS)) {
                    logger.warn("Terminating process {} is timed out.", p);
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Terminated process {}.", p);
                }
            } catch (final Exception e) {
                logger.warn("Failed to stop destroyer.", e);
            }
        }

        public boolean isExecuted() {
            return executed.get();
        }
    }

    public void setCommandList(final List<String> commandList) {
        this.commandList = commandList;
    }

    public void setCommandTimeout(final long commandTimeout) {
        this.commandTimeout = commandTimeout;
    }

    public void setBaseDir(final File baseDir) {
        this.baseDir = baseDir;
    }

    public void setCommandDestroyTimeout(final long commandDestroyTimeout) {
        this.commandDestroyTimeout = commandDestroyTimeout;
    }

}
