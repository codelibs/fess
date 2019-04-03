/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.EsClient;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.timer.SystemMonitorTarget;
import org.codelibs.fess.util.ComponentUtil;
import org.elasticsearch.monitor.jvm.JvmInfo;
import org.elasticsearch.monitor.os.OsProbe;
import org.elasticsearch.monitor.process.ProcessProbe;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.lastaflute.di.core.external.GenericExternalContext;
import org.lastaflute.di.core.external.GenericExternalContextComponentDefRegister;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuggestCreator {

    private static final Logger logger = LoggerFactory.getLogger(SuggestCreator.class);

    @Resource
    public FessEsClient fessEsClient;

    protected static class Options {
        @Option(name = "-s", aliases = "--sessionId", metaVar = "sessionId", usage = "Session ID")
        protected String sessionId;

        @Option(name = "-n", aliases = "--name", metaVar = "name", usage = "Name")
        protected String name;

        @Option(name = "-p", aliases = "--properties", metaVar = "properties", usage = "Properties File")
        protected String propertiesPath;

        protected Options() {
            // nothing
        }

        @Override
        public String toString() {
            return "Options [sessionId=" + sessionId + ", name=" + name + ", propertiesPath=" + propertiesPath + "]";
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
            System.err.println("java " + SuggestCreator.class.getCanonicalName() + " [options...] arguments...");
            parser.printUsage(System.err);
            return;
        }

        if (logger.isDebugEnabled()) {
            try {
                ManagementFactory.getRuntimeMXBean().getInputArguments().stream().forEach(s -> logger.debug("Parameter: " + s));
                System.getProperties().entrySet().stream().forEach(e -> logger.debug("Property: " + e.getKey() + "=" + e.getValue()));
                System.getenv().entrySet().forEach(e -> logger.debug("Env: " + e.getKey() + "=" + e.getValue()));
                logger.debug("Option: " + options);
            } catch (final Exception e) {
                // ignore
            }
        }

        final String httpAddress = System.getProperty(Constants.FESS_ES_HTTP_ADDRESS);
        if (StringUtil.isNotBlank(httpAddress)) {
            System.setProperty(EsClient.HTTP_ADDRESS, httpAddress);
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

            systemMonitorTask =
                    TimeoutManager.getInstance().addTimeoutTarget(new SystemMonitorTarget(),
                            ComponentUtil.getFessConfig().getSuggestSystemMonitorIntervalAsInteger(), true);

            exitCode = process(options);
        } catch (final ContainerNotAvailableException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("SuggestCreator is stopped.", e);
            } else if (logger.isInfoEnabled()) {
                logger.info("SuggestCreator is stopped.");
            }
            exitCode = Constants.EXIT_FAIL;
        } catch (final Throwable t) {
            logger.error("Suggest creator does not work correctly.", t);
            exitCode = Constants.EXIT_FAIL;
        } finally {
            if (systemMonitorTask != null) {
                systemMonitorTask.cancel();
            }
            destroyContainer();
        }

        logger.info("Finished SuggestCreator.");
        System.exit(exitCode);
    }

    private static void destroyContainer() {
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
                final File propFile = File.createTempFile("suggest_", ".properties");
                if (propFile.delete() && logger.isDebugEnabled()) {
                    logger.debug("Deleted a temp file: " + propFile.getAbsolutePath());
                }
                systemProperties.reload(propFile.getAbsolutePath());
                propFile.deleteOnExit();
            } catch (final IOException e) {
                logger.warn("Failed to create system properties file.", e);
            }
        }

        final SuggestCreator creator = ComponentUtil.getComponent(SuggestCreator.class);
        final LocalDateTime startTime = LocalDateTime.now();
        int ret = creator.create();
        if (ret == 0) {
            ret = creator.purge(startTime);
        }
        return ret;
    }

    private int create() {
        if (!ComponentUtil.getFessConfig().isSuggestDocuments()) {
            logger.info("Skip create suggest document.");
            return 0;
        }

        logger.info("Start create suggest document.");

        final AtomicInteger result = new AtomicInteger(1);
        final CountDownLatch latch = new CountDownLatch(1);

        final SuggestHelper suggestHelper = ComponentUtil.getSuggestHelper();

        logger.info("Create update index.");
        suggestHelper.suggester().createNextIndex();

        logger.info("Store all bad words.");
        suggestHelper.storeAllBadWords(true);

        logger.info("Store all elevate words.");
        suggestHelper.storeAllElevateWords(true);

        logger.info("Parse words from indexed documents.");
        suggestHelper.indexFromDocuments(ret -> {
            logger.info("Success index from documents.");
            result.set(0);
            latch.countDown();
        }, t -> {
            logger.error("Failed to update suggest index.", t);
            latch.countDown();
        });

        try {
            latch.await();
        } catch (final InterruptedException ignore) {
            if (logger.isDebugEnabled()) {
                logger.debug("Interrupted.", ignore);
            }
        }

        logger.info("Store search logs.");
        suggestHelper.storeSearchLog();

        logger.info("Switch indices.");
        suggestHelper.suggester().switchIndex();

        logger.info("Remove old indices.");
        suggestHelper.suggester().removeDisableIndices();

        return result.get();
    }

    private int purge(final LocalDateTime time) {
        final SuggestHelper suggestHelper = ComponentUtil.getSuggestHelper();

        try {
            suggestHelper.purgeDocumentSuggest(time);
            final long cleanupDay = ComponentUtil.getFessConfig().getPurgeSuggestSearchLogDay();
            if (cleanupDay > 0) {
                suggestHelper.purgeSearchlogSuggest(time.minusDays(cleanupDay));
            }
            return 0;
        } catch (final Exception e) {
            logger.info("Purge error.", e);
            return 1;
        }
    }

}
