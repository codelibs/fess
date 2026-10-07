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
package org.codelibs.fess.api;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.util.LogOnce;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Factory class for managing and retrieving web API managers.
 * This factory maintains a collection of web API managers and provides
 * functionality to find the appropriate manager for incoming requests.
 */
public class WebApiManagerFactory {

    private static final Logger logger = LogManager.getLogger(WebApiManagerFactory.class);

    /**
     * Path prefixes of the REST APIs that were split out of core, each paired with the plugin
     * that serves it: {@code /api/v1} is {@code fess-webapp-v1-api}, {@code /json} and
     * {@code /suggest} are {@code fess-webapp-classic-api}.
     */
    private static final String[][] LEGACY_API_PLUGINS =
            { { "/api/v1", "fess-webapp-v1-api" }, { "/json", "fess-webapp-classic-api" }, { "/suggest", "fess-webapp-classic-api" } };

    /** The plugins already reported as missing, so that each one is logged once rather than once per request. */
    private final LogOnce unservedLegacyApi = new LogOnce();

    /**
     * Default constructor.
     */
    public WebApiManagerFactory() {
    }

    /**
     * Array of registered web API managers.
     */
    protected WebApiManager[] webApiManagers = {};

    /**
     * Adds a web API manager to the factory.
     *
     * @param webApiManager The web API manager to add
     */
    public void add(final WebApiManager webApiManager) {
        if (logger.isDebugEnabled()) {
            logger.debug("Adding WebApiManager. class={}", webApiManager.getClass().getSimpleName());
        }
        final WebApiManager[] updated = Arrays.copyOf(webApiManagers, webApiManagers.length + 1);
        updated[webApiManagers.length] = webApiManager;
        webApiManagers = updated;
        if (logger.isDebugEnabled()) {
            logger.debug("WebApiManager added. totalManagers={}", webApiManagers.length);
        }
    }

    /**
     * Gets the appropriate web API manager for the given request.
     *
     * @param request The HTTP servlet request
     * @return The matching web API manager, or null if no match found
     */
    public WebApiManager get(final HttpServletRequest request) {
        final String servletPath = request.getServletPath();
        if (logger.isDebugEnabled()) {
            logger.debug("Looking for WebApiManager. servletPath={}, registeredManagers={}", servletPath, webApiManagers.length);
        }
        for (final WebApiManager webApiManager : webApiManagers) {
            if (webApiManager.matches(request)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("WebApiManager matched. servletPath={}, manager={}", servletPath,
                            webApiManager.getClass().getSimpleName());
                }
                return webApiManager;
            }
        }
        reportUnservedLegacyApi(servletPath);
        if (logger.isDebugEnabled()) {
            logger.debug("No WebApiManager matched. servletPath={}", servletPath);
        }
        return null;
    }

    /**
     * Reports that a request for a legacy API path reached no manager because the plugin that
     * serves that API is not installed.
     *
     * <p>The old REST APIs ({@code /api/v1/*}, {@code /json}, {@code /suggest}) ship as plugins,
     * so after an upgrade the request falls through {@code WebApiFilter} to LastaFlute and ends
     * as a plain 404 with nothing in the log to say why. This says it out loud, once per plugin
     * for the life of the JVM: the endpoint is anonymous, so a line per request would be a log a
     * client can fill, and the plugin name comes from a fixed table, so the set of keys is
     * bounded. Only the constant prefix and the plugin name are logged, never the request URI or
     * query string, because everything in the request is the caller's input. The response stays
     * the 404 it always was.</p>
     *
     * <p>A manager that serves the prefix but declined the request (for example
     * {@code web.api.json=false}) is an operator's setting, not a missing plugin, and says
     * nothing.</p>
     *
     * <p>WARN because the default log level is warn, so nothing below it is written on a default
     * install; not ERROR because ERROR is a notification trigger in Fess and a missing plugin is a
     * deployment state, not a fault at run time.</p>
     *
     * @param servletPath the servlet path of the request, may be null
     */
    protected void reportUnservedLegacyApi(final String servletPath) {
        if (servletPath == null) {
            return;
        }
        for (final String[] legacy : LEGACY_API_PLUGINS) {
            final String prefix = legacy[0];
            // the prefix itself or a sub-path of it, without building prefix + "/" on every request
            if (servletPath.startsWith(prefix) && (servletPath.length() == prefix.length() || servletPath.charAt(prefix.length()) == '/')) {
                // A served prefix is checked first so that it never uses up the plugin's key.
                if (!isServed(prefix)) {
                    unservedLegacyApi.warn(logger, legacy[1],
                            "No plugin serves the legacy API. pathPrefix={}, plugin={}. The legacy API ships as that plugin: "
                                    + "install it and restart Fess to serve this path. If it is already installed, check that its jar "
                                    + "is in the plugin directory and matches this Fess version. Until then every request to it is "
                                    + "answered with 404. This is reported once per plugin.",
                            prefix, legacy[1]);
                }
                return;
            }
        }
    }

    private boolean isServed(final String pathPrefix) {
        for (final WebApiManager webApiManager : webApiManagers) {
            if (webApiManager instanceof final BaseApiManager baseApiManager && pathPrefix.equals(baseApiManager.getPathPrefix())) {
                return true;
            }
        }
        return false;
    }

}
