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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Service;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.http.CookieProcessorBase;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.codelibs.fess.tomcat.session.FessSessionFilter;
import org.codelibs.fess.tomcat.session.FessSessionManager;
import org.codelibs.fess.tomcat.session.FessSessionValve;
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

    /** Environment variable that selects the HTTP session store ({@code opensearch}); unset keeps Tomcat's in-memory sessions */
    protected static final String FESS_SESSION_STORE = "FESS_SESSION_STORE";

    /** Key of the tomcat configuration file that sets the compression of the responses ({@code on} or {@code off}) */
    protected static final String COMPRESSION_KEY = "tomcat.compression";

    /** Key of the tomcat configuration file that adds types to the compressed responses (comma-separated) */
    protected static final String COMPRESSION_MIME_KEY = "tomcat.compression.mime";

    /**
     * The types of the responses that are compressed by default: the static files only. HTML and JSON are left out
     * because the admin screens put a session token in the page and reflect the request, so the size of a compressed
     * response can reveal the token; add them with {@code tomcat.compression.mime} when that does not matter.
     */
    protected static final String COMPRESSIBLE_MIME_TYPES = "text/css,text/javascript,application/javascript,image/svg+xml";

    /** The name of the filter that saves the sessions before a response is committed */
    protected static final String SESSION_FILTER_NAME = "fessSessionFilter";

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
     * Starts Tomcat and exits the process when the web application or a connector did not start.
     *
     * <p>Tomcat does not fail when a web application fails to initialize (for example, when
     * the search engine cannot be reached at startup): the context is left in the failed
     * state, the connector stays open and every request is answered with 404. Nor does it
     * fail when a connector cannot bind its port (for example, when another Fess already
     * uses it): the error is only logged and the process keeps running, with its scheduler
     * jobs, without serving anything. Nothing would ever start either again, so leave it to
     * the launcher (a service manager, a container restart policy or the operator) to notice
     * the failure and retry.</p>
     *
     * @return the URI Tomcat is serving
     */
    @Override
    protected URI startServer() {
        final URI uri = super.startServer();
        final List<Integer> failedPorts = findUnavailableConnectors(server.getService());
        if (!failedPorts.isEmpty()) {
            abortStartupOnPorts(failedPorts);
        }
        final List<String> failedContexts = findUnavailableContexts(server.getHost());
        if (!failedContexts.isEmpty()) {
            abortStartup(failedContexts);
        }
        return uri;
    }

    /**
     * Finds the web applications of a host that are not available after Tomcat has started.
     *
     * @param host the host that holds the web applications
     * @return the context paths of the web applications that are not available ("/" for the root context)
     */
    protected static List<String> findUnavailableContexts(final Container host) {
        final List<String> paths = new ArrayList<>();
        for (final Container child : host.findChildren()) {
            if (child instanceof final Context context && !context.getState().isAvailable()) {
                paths.add(context.getPath() == null || context.getPath().isEmpty() ? "/" : context.getPath());
            }
        }
        return paths;
    }

    /**
     * Finds the connectors of a service that are not available after Tomcat has started.
     *
     * @param service the service that holds the connectors
     * @return the ports of the connectors that are not available
     */
    protected static List<Integer> findUnavailableConnectors(final Service service) {
        final List<Integer> ports = new ArrayList<>();
        for (final Connector connector : service.findConnectors()) {
            if (!connector.getState().isAvailable()) {
                ports.add(connector.getPort());
            }
        }
        return ports;
    }

    /**
     * Reports that a connector did not start and terminates the JVM.
     *
     * @param ports the ports of the connectors that did not start
     */
    protected void abortStartupOnPorts(final List<Integer> ports) {
        reportAbort("Fess aborted the startup because the port " + ports
                + " could not be opened. Another process may already use it (see also the console output).");
        exit(1);
    }

    /**
     * Reports that the web application did not start and terminates the JVM.
     *
     * @param contextPaths the context paths of the web applications that did not start
     */
    protected void abortStartup(final List<String> contextPaths) {
        reportAbort("Fess aborted the startup because the web application " + contextPaths
                + " failed to initialize. The cause is in logs/fess.log (see also the console output).");
        exit(1);
    }

    /**
     * Reports why the startup is aborted on the standard error and in the server log.
     *
     * <p>The standard error is discarded when Fess is started as a service (an init script runs it in the
     * background with its output sent to /dev/null), so the reason is also logged at the highest level of the
     * logging that runs here: the java.util.logging configured by {@code logging.properties}, which writes
     * {@code server_*.log} in the log directory. Log4j is not used because it is not on this class path (the
     * web application has it); {@code fess.log} is written by the web application.</p>
     *
     * <p>The logger is looked up here, not kept in a static field: this class is loaded before
     * {@code main} points java.util.logging at {@code logging.properties}.</p>
     *
     * @param message the reason
     */
    protected void reportAbort(final String message) {
        System.err.println(message);
        Logger.getLogger(FessBoot.class.getName()).severe(message);
    }

    /**
     * Terminates the JVM.
     *
     * @param status the exit status
     */
    protected void exit(final int status) {
        System.exit(status);
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
        return "";
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
            contextPath = contextPath.replaceAll("/+$", "");
        }
        final Context context = (Context) server.getHost().findChild(contextPath);
        if (context != null) {
            context.setResources(new FessWebResourceRoot(context));
            context.setCookieProcessor(new Rfc6265CookieProcessor());
            setupSessionStore(context, System.getenv(FESS_SESSION_STORE));
        }
    }

    /**
     * Applies the connector settings Fess sets itself, after those of the tomcat configuration file.
     */
    @Override
    protected void setupServerConfigIfNeeds() {
        super.setupServerConfigIfNeeds();
        setupCompression(server.getConnector(), configProps);
    }

    /**
     * Turns on the compression of the static text responses (scripts, styles, SVG) of 1 KB or more.
     * Pages and JSON carry the session token or reflect the request, so they stay as they are unless
     * {@code tomcat.compression.mime} in the tomcat configuration file adds their types. Binary content
     * (images, downloads) and event streams are not in the list either.
     * {@code tomcat.compression=off} turns the compression off, for a deployment whose reverse proxy compresses already.
     *
     * @param connector the HTTP connector
     * @param props the tomcat configuration, or {@code null} when there is none
     */
    protected static void setupCompression(final Connector connector, final Properties props) {
        final String compression = props == null ? null : props.getProperty(COMPRESSION_KEY);
        connector.setProperty("compression", compression == null || compression.isBlank() ? "on" : compression.trim());
        connector.setProperty("compressionMinSize", "1024");
        final String extra = props == null ? null : props.getProperty(COMPRESSION_MIME_KEY);
        connector.setProperty("compressibleMimeType",
                extra == null || extra.isBlank() ? COMPRESSIBLE_MIME_TYPES : COMPRESSIBLE_MIME_TYPES + "," + extra.trim());
    }

    /**
     * Switches the context to the OpenSearch-backed HTTP session store when requested.
     * Nothing changes for an unset, empty or {@code memory} value; an unknown value is reported and treated as {@code memory}.
     *
     * @param context the web application context
     * @param sessionStore the value of {@link #FESS_SESSION_STORE}, or {@code null}
     */
    protected static void setupSessionStore(final Context context, final String sessionStore) {
        if (sessionStore == null || sessionStore.isBlank()) {
            return;
        }
        final String value = sessionStore.trim();
        if ("opensearch".equalsIgnoreCase(value)) {
            final FessSessionManager manager = new FessSessionManager();
            context.setManager(manager);
            context.getPipeline().addValve(new FessSessionValve());
            // saves the sessions before a response is committed, which the valve cannot do (see FessSessionFilter)
            final FilterDef filterDef = new FilterDef();
            filterDef.setFilterName(SESSION_FILTER_NAME);
            filterDef.setFilter(new FessSessionFilter(manager));
            filterDef.setAsyncSupported("true");
            context.addFilterDef(filterDef);
            final FilterMap filterMap = new FilterMap();
            filterMap.setFilterName(SESSION_FILTER_NAME);
            filterMap.addURLPattern("/*");
            context.addFilterMapBefore(filterMap);
        } else if (!"memory".equalsIgnoreCase(value)) {
            System.err.println("Unknown " + FESS_SESSION_STORE + " value: " + value + ". Sessions are kept in memory.");
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
