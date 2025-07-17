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
package org.codelibs.fess;

// DO NOT DEPEND OTHER JARs

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.http.CookieProcessorBase;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.tomcat.valve.SuppressErrorReportValve;
import org.codelibs.fess.tomcat.webresources.FessWebResourceRoot;
import org.dbflute.tomcat.TomcatBoot;
import org.dbflute.tomcat.logging.BootLogger;
import org.dbflute.tomcat.props.BootPropsTranslator;

/**
 * Main boot class for the Fess search engine application.
 * This class extends TomcatBoot to provide Fess-specific Tomcat server configuration
 * and initialization, including SSL setup, context path handling, and resource management.
 *
 * <p>The class handles system property configuration for paths, ports, and other
 * Fess-specific settings during application startup.</p>
 *
 * @since 1.0
 */
public class FessBoot extends TomcatBoot {

    /** Configuration file name for logging properties */
    private static final String LOGGING_PROPERTIES = "logging.properties";

    /** System property key for Fess context path configuration */
    private static final String FESS_CONTEXT_PATH = "fess.context.path";

    /** System property key for Fess port configuration */
    private static final String FESS_PORT = "fess.port";

    /** System property key for Fess temporary directory path */
    private static final String FESS_TEMP_PATH = "fess.temp.path";

    /** System property key for Fess variable directory path */
    private static final String FESS_VAR_PATH = "fess.var.path";

    /** System property key for Fess web application path */
    private static final String FESS_WEBAPP_PATH = "fess.webapp.path";

    /** System property key for Java temporary directory */
    private static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    /** System property key for Tomcat configuration path */
    private static final String TOMCAT_CONFIG_PATH = "tomcat.config.path";

    /**
     * Constructs a new FessBoot instance with the specified port and context path.
     *
     * @param port the port number for the Tomcat server
     * @param contextPath the context path for the web application
     */
    public FessBoot(final int port, final String contextPath) {
        super(port, contextPath);
    }

    /**
     * Prepares and returns the web application path.
     * Checks for the fess.webapp.path system property first, then falls back to the parent implementation.
     *
     * @return the web application path
     */
    @Override
    protected String prepareWebappPath() {
        final String value = System.getProperty(FESS_WEBAPP_PATH);
        if (value != null) {
            return value;
        }
        return super.prepareWebappPath();
    }

    /**
     * Returns the directory path for temporary mark files.
     *
     * @return the absolute path to the fessboot directory in the system temp directory
     */
    @Override
    protected String getMarkDir() {
        return new File(System.getProperty(JAVA_IO_TMPDIR), "fessboot").getAbsolutePath();
    }

    // ===================================================================================
    //                                                                        main
    //                                                                        ============

    /**
     * Main method to start the Fess application.
     * Sets up system properties, configures Tomcat, and starts the server.
     *
     * @param args command line arguments (not used)
     */
    public static void main(final String[] args) {
        // update java.io.tmpdir
        final String tempPath = System.getProperty(FESS_TEMP_PATH);
        if (tempPath != null) {
            System.setProperty(JAVA_IO_TMPDIR, tempPath);
        }

        final TomcatBoot tomcatBoot = new FessBoot(getPort(), getContextPath()) //
                .useTldDetect(); // for JSP
        final String varPath = System.getProperty(FESS_VAR_PATH);
        if (varPath != null) {
            tomcatBoot.atBaseDir(new File(varPath, "webapp").getAbsolutePath());
        } else if (tempPath != null) {
            tomcatBoot.atBaseDir(new File(tempPath, "webapp").getAbsolutePath());
        }
        final String tomcatConfigPath = getTomcatConfigPath();
        if (tomcatConfigPath != null) {
            tomcatBoot.configure(tomcatConfigPath); // e.g. URIEncoding
        }
        tomcatBoot.logging(LOGGING_PROPERTIES, op -> {
            op.ignoreNoFile();
            String fessLogPath = System.getProperty("fess.log.path");
            if (fessLogPath == null) {
                fessLogPath = "../../logs";
            }
            op.replace("fess.log.path", fessLogPath.replace("\\", "/"));
        }).asYouLikeIt(resource -> {
            final Host host = resource.getHost();
            if (host instanceof final StandardHost standardHost) {
                standardHost.setErrorReportValveClass(SuppressErrorReportValve.class.getName());
            }
        }).useTldDetect(jarName -> (jarName.contains("jstl") || jarName.contains("lasta-taglib"))).asDevelopment(isNoneEnv()).bootAwait();
    }

    /**
     * Shuts down the Fess application.
     *
     * @param args command line arguments (not used)
     */
    public static void shutdown(final String[] args) {
        System.exit(0);
    }

    /**
     * Checks if the lasta.env system property is not set.
     *
     * @return true if lasta.env is not set, false otherwise
     */
    private static boolean isNoneEnv() {
        return System.getProperty("lasta.env") == null;
    }

    /**
     * Gets the port number for the Tomcat server from system properties.
     *
     * @return the port number (default 8080 if not specified)
     */
    protected static int getPort() {
        final String value = System.getProperty(FESS_PORT);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return 8080;
    }

    /**
     * Gets the context path for the web application from system properties.
     *
     * @return the context path (empty string if not specified or if set to "/")
     */
    protected static String getContextPath() {
        final String value = System.getProperty(FESS_CONTEXT_PATH);
        if (value != null && !"/".equals(value)) {
            return value;
        }
        return StringUtil.EMPTY;
    }

    /**
     * Gets the Tomcat configuration path from system properties.
     *
     * @return the Tomcat configuration path, or null if not specified
     */
    protected static String getTomcatConfigPath() {
        return System.getProperty(TOMCAT_CONFIG_PATH);
    }

    /**
     * Sets up the web application context with Fess-specific configurations.
     * Configures the web resource root and cookie processor for the context.
     */
    @Override
    protected void setupWebappContext() {
        super.setupWebappContext();
        String contextPath = getContextPath();
        if (contextPath.length() > 0 && contextPath.endsWith("/")) {
            contextPath = contextPath.replaceAll("/+$", StringUtil.EMPTY);
        }
        final Context context = (Context) server.getHost().findChild(contextPath);
        if (context != null) {
            context.setResources(new FessWebResourceRoot(context));
            context.setCookieProcessor(new Rfc6265CookieProcessor());
        }
    }

    /**
     * Creates a Fess-specific boot properties translator.
     *
     * @return a new FessBootPropsTranslator instance
     */
    @Override
    protected BootPropsTranslator createBootPropsTranslator() {
        return new FessBootPropsTranslator();
    }

    /**
     * Fess-specific implementation of BootPropsTranslator.
     * Handles SSL configuration and cookie settings for the Tomcat server.
     */
    static class FessBootPropsTranslator extends BootPropsTranslator {
        /**
         * Sets up server configuration if needed, including SSL and cookie settings.
         *
         * @param logger the boot logger for logging configuration messages
         * @param server the Tomcat server instance
         * @param connector the Tomcat connector
         * @param props the configuration properties
         * @param readConfigList the list of read configuration items
         */
        @Override
        public void setupServerConfigIfNeeds(final BootLogger logger, final Tomcat server, final Connector connector,
                final Properties props, final List<String> readConfigList) {
            if (props == null) {
                return;
            }
            super.setupServerConfigIfNeeds(logger, server, connector, props, readConfigList);
            doSetupServerConfig(logger, props, "SSLEnabled", value -> {
                if ("true".equalsIgnoreCase(value)) {
                    connector.setProperty("SSLEnabled", "true");
                    final SSLHostConfig sslHostConfig = new SSLHostConfig();
                    sslHostConfig.setHostName("_default_");
                    final SSLHostConfigCertificate certificate =
                            new SSLHostConfigCertificate(sslHostConfig, SSLHostConfigCertificate.Type.UNDEFINED);
                    doSetupServerConfig(logger, props, "certificateKeystoreFile", v -> certificate.setCertificateKeystoreFile(v));
                    doSetupServerConfig(logger, props, "certificateKeystorePassword", v -> certificate.setCertificateKeystorePassword(v));
                    doSetupServerConfig(logger, props, "certificateKeyAlias", v -> certificate.setCertificateKeyAlias(v));
                    doSetupServerConfig(logger, props, "sslProtocol", v -> sslHostConfig.setSslProtocol(v));
                    doSetupServerConfig(logger, props, "enabledProtocols", v -> sslHostConfig.setEnabledProtocols(v.trim().split(",")));
                    sslHostConfig.addCertificate(certificate);
                    connector.addSslHostConfig(sslHostConfig);

                }
            });
            doSetupServerConfig(logger, props, "sameSiteCookies", value -> {
                for (final Container container : server.getHost().findChildren()) {
                    if (container instanceof final Context context
                            && context.getCookieProcessor() instanceof final CookieProcessorBase cookieProcessor) {
                        cookieProcessor.setSameSiteCookies(value);
                    }
                }
            });
        }
    }
}
