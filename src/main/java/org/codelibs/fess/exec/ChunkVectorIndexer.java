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
package org.codelibs.fess.exec;

import java.io.File;
import java.lang.management.ManagementFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.FesenClient;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.helper.ChunkVectorHelper;
import org.codelibs.fess.timer.LogNotificationTarget;
import org.codelibs.fess.timer.SystemMonitorTarget;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.SystemUtil;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.lastaflute.di.core.external.GenericExternalContext;
import org.lastaflute.di.core.external.GenericExternalContextComponentDefRegister;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;

/**
 * This class is a command-line application for processing documents pending
 * content-chunk and embedding-vector generation. It is launched as a separate
 * child JVM by {@link org.codelibs.fess.job.ChunkVectorJob} (the same pattern
 * {@link SuggestCreator} follows for the suggest index) and delegates the whole
 * processing run to {@link ChunkVectorHelper#executeChunkVectorProcessing()}.
 */
public class ChunkVectorIndexer {

    private static final Logger logger = LogManager.getLogger(ChunkVectorIndexer.class);

    /**
     * The system property key for the system monitor interval in seconds.
     * There is no chunk-specific FessConfig interval property; this JVM system
     * property (default 60) fills that role for the child process.
     */
    protected static final String SYSTEM_MONITOR_INTERVAL_PROPERTY = "fess.chunk.system.monitor.interval";

    /**
     * Constructs a new chunk vector indexer.
     */
    public ChunkVectorIndexer() {
        // do nothing
    }

    /**
     * A nested class for parsing command-line options.
     */
    protected static class Options {
        /** The session ID for the chunk vector indexing process. */
        @Option(name = "-s", aliases = "--sessionId", metaVar = "sessionId", usage = "Session ID")
        protected String sessionId;

        /** The name of the chunk vector indexer instance. */
        @Option(name = "-n", aliases = "--name", metaVar = "name", usage = "Name")
        protected String name;

        /** The path to the properties file for configuration. */
        @Option(name = "-p", aliases = "--properties", metaVar = "properties", usage = "Properties File")
        protected String propertiesPath;

        /**
         * Constructs a new Options object.
         */
        protected Options() {
            // nothing
        }

        @Override
        public String toString() {
            return "Options [sessionId=" + sessionId + ", name=" + name + ", propertiesPath=" + propertiesPath + "]";
        }
    }

    /**
     * The main method for the ChunkVectorIndexer application.
     *
     * @param args The command-line arguments.
     */
    public static void main(final String[] args) {
        final Options options = new Options();
        final CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch (final CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java " + ChunkVectorIndexer.class.getCanonicalName() + " [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }

        if (logger.isDebugEnabled()) {
            try {
                ManagementFactory.getRuntimeMXBean().getInputArguments().stream().forEach(s -> logger.debug("Parameter: {}", s));
                System.getProperties()
                        .entrySet()
                        .stream()
                        .forEach(e -> logger.debug("Property: {}={}", e.getKey(),
                                SystemUtil.maskSensitiveValue(String.valueOf(e.getKey()), String.valueOf(e.getValue()))));
                System.getenv()
                        .entrySet()
                        .forEach(e -> logger.debug("Env: {}={}", e.getKey(), SystemUtil.maskSensitiveValue(e.getKey(), e.getValue())));
                logger.debug("Options: options={}", options);
            } catch (final Exception e) {
                // ignore
            }
        }

        final String httpAddress = SystemUtil.getSearchEngineHttpAddress();
        if (StringUtil.isNotBlank(httpAddress)) {
            System.setProperty(FesenClient.HTTP_ADDRESS, httpAddress);
        }

        TimeoutTask systemMonitorTask = null;
        TimeoutTask logNotificationTask = null;
        LogNotificationTarget logNotificationTarget = null;
        int exitCode;
        try {
            SingletonLaContainerFactory.setConfigPath("app.xml");
            SingletonLaContainerFactory.setExternalContext(new GenericExternalContext());
            SingletonLaContainerFactory.setExternalContextComponentDefRegister(new GenericExternalContextComponentDefRegister());
            SingletonLaContainerFactory.init();

            final Thread shutdownCallback = new Thread("ShutdownHook") {
                @Override
                public void run() {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Destroying LaContainer...");
                    }
                    destroyContainer();
                }
            };
            Runtime.getRuntime().addShutdownHook(shutdownCallback);

            systemMonitorTask = TimeoutManager.getInstance().addTimeoutTarget(new SystemMonitorTarget(), getSystemMonitorInterval(), true);

            if (ComponentUtil.getFessConfig().isLogNotificationEnabled()) {
                logNotificationTarget = new LogNotificationTarget();
                logNotificationTask = TimeoutManager.getInstance()
                        .addTimeoutTarget(logNotificationTarget, ComponentUtil.getFessConfig().getLogNotificationFlushIntervalAsInteger(),
                                true);
            }

            exitCode = process(options);
        } catch (final ContainerNotAvailableException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("ChunkVectorIndexer is stopped.", e);
            } else if (logger.isInfoEnabled()) {
                logger.info("ChunkVectorIndexer is stopped.");
            }
            exitCode = Constants.EXIT_FAIL;
        } catch (final Throwable t) {
            logger.error("ChunkVectorIndexer terminated unexpectedly.", t);
            exitCode = Constants.EXIT_FAIL;
        } finally {
            if (systemMonitorTask != null) {
                systemMonitorTask.cancel();
            }
            if (logNotificationTask != null) {
                logNotificationTask.cancel();
            }
            if (logNotificationTarget != null) {
                logNotificationTarget.flush();
            }
            destroyContainer();
        }

        logger.info("Finished ChunkVectorIndexer.");
        System.exit(exitCode);
    }

    /**
     * Returns the system monitor interval in seconds, read from the
     * {@code fess.chunk.system.monitor.interval} JVM system property (default 60).
     *
     * @return the system monitor interval in seconds
     */
    protected static int getSystemMonitorInterval() {
        return Integer.getInteger(SYSTEM_MONITOR_INTERVAL_PROPERTY, 60);
    }

    private static void destroyContainer() {
        TimeoutManager.getInstance().stop();
        synchronized (SingletonLaContainerFactory.class) {
            SingletonLaContainerFactory.destroy();
        }
    }

    private static int process(final Options options) {
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();

        if (StringUtil.isNotBlank(options.propertiesPath)) {
            systemProperties.reload(options.propertiesPath);
        } else {
            try {
                final File propFile = ComponentUtil.getSystemHelper().createTempFile("chunk_", ".properties");
                if (propFile.delete() && logger.isDebugEnabled()) {
                    logger.debug("Deleted temp file: path={}", propFile.getAbsolutePath());
                }
                systemProperties.reload(propFile.getAbsolutePath());
                propFile.deleteOnExit();
            } catch (final Exception e) {
                logger.warn("Failed to create system properties file.", e);
            }
        }

        final ChunkVectorIndexer indexer = ComponentUtil.getComponent(ChunkVectorIndexer.class);
        return indexer.index();
    }

    private int index() {
        // Same exit-code contract as SuggestCreator#create(): a genuine run failure (an exception
        // out of the helper, e.g. the pending-document scroll failing) exits non-zero so the parent
        // ChunkVectorJob throws JobProcessingException; intentional skips (chunker disabled,
        // transient provider outage, dimension mismatch, mapping not ready) and normal runs --
        // including runs with per-document failures -- return a summary message and exit 0.
        try {
            final String summary = ComponentUtil.getComponent(ChunkVectorHelper.class).executeChunkVectorProcessing();
            logger.info("Chunk vector processing result: {}", summary);
            return Constants.EXIT_OK;
        } catch (final Exception e) {
            logger.error("Chunk vector processing failed.", e);
            return Constants.EXIT_FAIL;
        }
    }

}
