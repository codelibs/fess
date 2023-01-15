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
package org.codelibs.fess.exec;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.FesenClient;
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.timer.SystemMonitorTarget;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.SystemUtil;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.lastaflute.di.core.external.GenericExternalContext;
import org.lastaflute.di.core.external.GenericExternalContextComponentDefRegister;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.opensearch.monitor.jvm.JvmInfo;
import org.opensearch.monitor.os.OsProbe;
import org.opensearch.monitor.process.ProcessProbe;

public class ThumbnailGenerator {

    private static final Logger logger = LogManager.getLogger(ThumbnailGenerator.class);

    @Resource
    public SearchEngineClient searchEngineClient;

    protected static class Options {
        @Option(name = "-s", aliases = "--sessionId", metaVar = "sessionId", usage = "Session ID")
        protected String sessionId;

        @Option(name = "-n", aliases = "--name", metaVar = "name", usage = "Name")
        protected String name;

        @Option(name = "-p", aliases = "--properties", metaVar = "properties", usage = "Properties File")
        protected String propertiesPath;

        @Option(name = "-t", aliases = "--numOfThreads", metaVar = "numOfThreads", usage = "The number of threads")
        protected int numOfThreads = 1;

        @Option(name = "-c", aliases = "--cleanup", usage = "Clean-Up mode")
        protected boolean cleanup;

        protected Options() {
            // nothing
        }

        @Override
        public String toString() {
            return "Options [sessionId=" + sessionId + ", name=" + name + ", propertiesPath=" + propertiesPath + ", numOfThreads="
                    + numOfThreads + "]";
        }

    }

    static void initializeProbes() {
        // Force probes to be loaded
        ProcessProbe.getInstance();
        OsProbe.getInstance();
        JvmInfo.jvmInfo();
    }

    public static void main(final String[] args) {
        final Options options = new Options();

        final CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch (final CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java " + ThumbnailGenerator.class.getCanonicalName() + " [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }

        if (logger.isDebugEnabled()) {
            try {
                ManagementFactory.getRuntimeMXBean().getInputArguments().stream().forEach(s -> logger.debug("Parameter: {}", s));
                System.getProperties().entrySet().stream().forEach(e -> logger.debug("Property: {}={}", e.getKey(), e.getValue()));
                System.getenv().entrySet().forEach(e -> logger.debug("Env: {}={}", e.getKey(), e.getValue()));
                logger.debug("Option: {}", options);
            } catch (final Exception e) {
                // ignore
            }
        }

        final String httpAddress = SystemUtil.getSearchEngineHttpAddress();
        if (StringUtil.isNotBlank(httpAddress)) {
            System.setProperty(FesenClient.HTTP_ADDRESS, httpAddress);
        }

        TimeoutTask systemMonitorTask = null;
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
                        logger.debug("Destroying LaContainer..");
                    }
                    destroyContainer();
                }
            };
            Runtime.getRuntime().addShutdownHook(shutdownCallback);

            systemMonitorTask = TimeoutManager.getInstance().addTimeoutTarget(new SystemMonitorTarget(),
                    ComponentUtil.getFessConfig().getThumbnailSystemMonitorIntervalAsInteger(), true);

            final int totalCount = process(options);
            if (totalCount != 0) {
                logger.info("Created {} thumbnail files.", totalCount);
            } else {
                logger.info("No new thumbnails found.");
            }
            exitCode = 0;
        } catch (final ContainerNotAvailableException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("ThumbnailGenerator is stopped.", e);
            } else if (logger.isInfoEnabled()) {
                logger.info("ThumbnailGenerator is stopped.");
            }
            exitCode = Constants.EXIT_FAIL;
        } catch (final Throwable t) {
            logger.error("ThumbnailGenerator does not work correctly.", t);
            exitCode = Constants.EXIT_FAIL;
        } finally {
            if (systemMonitorTask != null) {
                systemMonitorTask.cancel();
            }
            destroyContainer();
        }

        System.exit(exitCode);
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
                final File propFile = ComponentUtil.getSystemHelper().createTempFile("thumbnail_", ".properties");
                if (propFile.delete() && logger.isDebugEnabled()) {
                    logger.debug("Deleted a temp file: {}", propFile.getAbsolutePath());
                }
                systemProperties.reload(propFile.getAbsolutePath());
                propFile.deleteOnExit();
            } catch (final Exception e) {
                logger.warn("Failed to create system properties file.", e);
            }
        }

        int totalCount = 0;
        int count = 1;
        final ExecutorService executorService = Executors.newFixedThreadPool(options.numOfThreads);
        try {
            while (count != 0) {
                count = ComponentUtil.getThumbnailManager().generate(executorService, options.cleanup);
                totalCount += count;
            }
            executorService.shutdown();
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Interrupted.", e);
            }
        } finally {
            executorService.shutdownNow();
        }
        return totalCount;
    }
}
