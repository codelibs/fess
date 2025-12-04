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
package org.codelibs.fess.thumbnail.impl;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.concurrent.CommonPoolUtil;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.InputStreamThread;

import jakarta.annotation.PostConstruct;

/**
 * Command-based thumbnail generator that executes external commands to create thumbnails.
 * Uses external tools through command execution to generate thumbnail images from documents.
 */
public class CommandGenerator extends BaseThumbnailGenerator {
    private static final Logger logger = LogManager.getLogger(CommandGenerator.class);

    /** List of command strings to execute for thumbnail generation. */
    protected List<String> commandList;

    /** Timeout for command execution in milliseconds. */
    protected long commandTimeout = 30 * 1000L;// 30sec

    /** Timeout for destroying processes in milliseconds. */
    protected long commandDestroyTimeout = 5 * 1000L;// 5sec

    /** Base directory for command execution. */
    protected File baseDir;

    /** Timer for managing process destruction. */
    private Timer destoryTimer;

    /**
     * Default constructor for CommandGenerator.
     */
    public CommandGenerator() {
        super();
    }

    /**
     * Initializes the command generator after construction.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing {}", this.getClass().getSimpleName());
        }
        if (baseDir == null) {
            baseDir = new File(System.getProperty("java.io.tmpdir"));
        }
        destoryTimer = new Timer("CommandGeneratorDestoryTimer-" + ComponentUtil.getSystemHelper().getCurrentTimeAsLong(), true);
        updateProperties();
    }

    /**
     * Updates timeout properties from system configuration.
     */
    protected void updateProperties() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String commandTimeoutStr = fessConfig.getSystemProperty("thumbnail.command.timeout");
        if (commandTimeoutStr != null) {
            commandTimeout = Long.parseLong(commandTimeoutStr);
        }
        final String commandDestroyTimeoutStr = fessConfig.getSystemProperty("thumbnail.command.destroy.timeout");
        if (commandDestroyTimeoutStr != null) {
            commandDestroyTimeout = Long.parseLong(commandDestroyTimeoutStr);
        }
    }

    /**
     * Destroys the command generator and cleanup resources.
     */
    @Override
    public void destroy() {
        destoryTimer.cancel();
        destoryTimer = null;
    }

    /**
     * Generates a thumbnail for the given ID and saves it to the output file.
     * @param thumbnailId The ID of the thumbnail to generate.
     * @param outputFile The file where the thumbnail will be saved.
     * @return True if thumbnail generation was successful, false otherwise.
     */
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
            logger.warn("Parent directory not found: {}", parentFile.getAbsolutePath());
            return false;
        }

        return process(thumbnailId, responseData -> {
            final String mimeType = responseData.getMimeType();
            final String extension = getExtensionFromMimeType(mimeType);
            final File tempFile = ComponentUtil.getSystemHelper().createTempFile("thumbnail_", extension);
            try {
                CopyUtil.copy(responseData.getResponseBody(), tempFile);

                final String tempPath = tempFile.getAbsolutePath();
                final String outputPath = outputFile.getAbsolutePath();
                final List<String> cmdList = new ArrayList<>();
                for (final String value : commandList) {
                    cmdList.add(expandPath(value.replace("${url}", tempPath)
                            .replace("${outputFile}", outputPath)
                            .replace("${mimetype}", mimeType != null ? mimeType : "")));
                }

                if (executeCommand(thumbnailId, cmdList) != 0) {
                    logger.warn("Failed to execute command for thumbnail ID: {}", thumbnailId);
                    if (outputFile.isFile() && !outputFile.delete()) {
                        logger.warn("Failed to delete output file: {}", outputFile.getAbsolutePath());
                    }
                    return false;
                }

                if (outputFile.isFile() && outputFile.length() == 0) {
                    logger.warn("Thumbnail file is empty: id={}", thumbnailId);
                    if (outputFile.delete()) {
                        logger.info("Deleted empty thumbnail file: {}", outputFile.getAbsolutePath());
                    }
                    updateThumbnailField(thumbnailId, StringUtil.EMPTY);
                    return false;
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("Thumbnail File: {}", outputPath);
                }
                return true;
            } catch (final Exception e) {
                logger.warn("Failed to process thumbnail: id={}", thumbnailId, e);
                updateThumbnailField(thumbnailId, StringUtil.EMPTY);
                return false;
            } finally {
                if (tempFile != null && !tempFile.delete()) {
                    logger.debug("Failed to delete temp file: {}", tempFile.getAbsolutePath());
                }
            }
        });

    }

    /**
     * Executes a command to generate a thumbnail using the specified command list.
     * <p>
     * This method starts a process with the given command list, manages its execution,
     * handles timeouts, and logs relevant information. It ensures the process is destroyed
     * if it exceeds the allowed execution time or becomes unresponsive. The method also
     * captures and logs the process output for debugging purposes.
     * </p>
     *
     * @param thumbnailId the identifier for the thumbnail being generated
     * @param cmdList the list of command arguments to execute
     * @return the exit code of the process if it finishes normally; -1 if the process fails or is terminated
     */
    protected int executeCommand(final String thumbnailId, final List<String> cmdList) {
        ProcessDestroyer task = null;
        Process p = null;
        InputStreamThread ist = null;
        try {
            final ProcessBuilder pb = new ProcessBuilder(cmdList);
            pb.directory(baseDir);
            pb.redirectErrorStream(true);

            if (logger.isDebugEnabled()) {
                logger.debug("Thumbnail Command: {}", cmdList);
            }

            p = pb.start();
            ist = new InputStreamThread(p.getInputStream(), Charset.defaultCharset(), 0, s -> {
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

            if (p.waitFor(commandTimeout + commandDestroyTimeout, TimeUnit.MILLISECONDS)) {
                if (task.isExecuted()) {
                    // Process was killed by the timer.
                    logger.warn("{} was timed out and destroyed.", getName());
                } else {
                    // Process finished normally.
                    final int exitValue = p.exitValue();
                    if (exitValue != 0) {
                        logger.warn("{} failed: exitCode={}, command={}", getName(), exitValue, commandList);
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("{} finished: exitCode={}", getName(), exitValue);
                    }
                    return exitValue;
                }
            } else {
                // This is a secondary timeout, a safety net.
                logger.warn("{} is unresponsive and could not be terminated within the safety timeout.", getName());
                if (!task.isExecuted()) {
                    task.run();
                }
            }
        } catch (final InterruptedException e) {
            logger.warn("Interrupted generating thumbnail: id={}, command={}", thumbnailId, cmdList, e);
            Thread.currentThread().interrupt();
        } catch (final Exception e) {
            logger.warn("Failed to generate thumbnail: id={}, command={}", thumbnailId, cmdList, e);
        } finally {
            if (task != null) {
                task.cancel();
            }
            if (p != null && p.isAlive()) {
                logger.warn("Process {} is still alive in finally block. Forcing destruction.", p);
                if (task == null) {
                    task = new ProcessDestroyer(p, ist, commandDestroyTimeout);
                }
                if (!task.isExecuted()) {
                    task.run();
                }
            }
            if (ist != null) {
                ist.interrupt();
            }
        }
        return -1; // Indicate failure
    }

    /**
     * Timer task for destroying processes that exceed their timeout.
     * Handles graceful and forceful termination of thumbnail generation processes.
     */
    protected static class ProcessDestroyer extends TimerTask {

        /** The process to monitor and destroy. */
        private final Process p;

        /** The input stream thread reading process output. */
        private final InputStreamThread ist;

        /** Flag indicating if the destroyer has been executed. */
        private final AtomicBoolean executed = new AtomicBoolean(false);

        /** Timeout for process destruction in milliseconds. */
        private final long timeout;

        /**
         * Constructor for ProcessDestroyer.
         * @param p The process to monitor.
         * @param ist The input stream thread.
         * @param timeout The destruction timeout.
         */
        public ProcessDestroyer(final Process p, final InputStreamThread ist, final long timeout) {
            this.p = p;
            this.ist = ist;
            this.timeout = timeout;
        }

        /**
         * Runs the process destroyer task to terminate the process.
         */
        @Override
        public void run() {
            if (!p.isAlive()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Process {} is not alive.", p);
                }
                return;
            }

            if (executed.compareAndSet(false, true)) {
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
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Process {} is already executed.", p);
                }
            }
        }

        /**
         * Checks if the destroyer has been executed.
         * @return True if executed, false otherwise.
         */
        public boolean isExecuted() {
            return executed.get();
        }
    }

    /**
     * Sets the list of commands to execute for thumbnail generation.
     * @param commandList The command list.
     */
    public void setCommandList(final List<String> commandList) {
        this.commandList = commandList;
    }

    /**
     * Sets the command execution timeout.
     * @param commandTimeout The timeout in milliseconds.
     */
    public void setCommandTimeout(final long commandTimeout) {
        this.commandTimeout = commandTimeout;
    }

    /**
     * Sets the base directory for command execution.
     * @param baseDir The base directory.
     */
    public void setBaseDir(final File baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Sets the timeout for destroying processes.
     * @param commandDestroyTimeout The destroy timeout in milliseconds.
     */
    public void setCommandDestroyTimeout(final long commandDestroyTimeout) {
        this.commandDestroyTimeout = commandDestroyTimeout;
    }

    /**
     * Gets file extension from MIME type for creating temp files with proper extensions.
     * This helps ImageMagick correctly identify file formats.
     * @param mimeType The MIME type of the content.
     * @return The file extension including the dot (e.g., ".gif"), or empty string if unknown.
     */
    protected String getExtensionFromMimeType(final String mimeType) {
        if (mimeType == null) {
            return "";
        }
        return switch (mimeType) {
        case "image/gif" -> ".gif";
        case "image/tiff" -> ".tiff";
        case "image/svg+xml" -> ".svg";
        case "image/jpeg" -> ".jpg";
        case "image/png" -> ".png";
        case "image/bmp", "image/x-windows-bmp", "image/x-ms-bmp" -> ".bmp";
        case "image/vnd.adobe.photoshop", "image/photoshop", "application/x-photoshop", "application/photoshop" -> ".psd";
        default -> "";
        };
    }

}
