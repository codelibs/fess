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
package org.codelibs.fess.cors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Default implementation of CORS (Cross-Origin Resource Sharing) handler.
 * This handler automatically registers itself for origins configured in the system
 * and applies standard CORS headers based on the application configuration.
 */
public class DefaultCorsHandler extends CorsHandler {

    private static final Logger logger = LogManager.getLogger(DefaultCorsHandler.class);

    /**
     * Creates a new instance of DefaultCorsHandler.
     * This constructor initializes the default CORS handler for applying
     * standard CORS headers based on application configuration.
     */
    public DefaultCorsHandler() {
        super();
    }

    /**
     * Registers this CORS handler with the factory for configured allowed origins.
     * This method is automatically called after bean initialization.
     */
    @PostConstruct
    public void register() {
        final CorsHandlerFactory factory = ComponentUtil.getCorsHandlerFactory();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final java.util.List<String> origins = fessConfig.getApiCorsAllowOriginList();
        origins.forEach(s -> factory.add(s, this));
        if (origins.contains(ALLOW_ORIGIN_ALL) && fessConfig.isApiCorsAllowCredentials()) {
            logger.warn("Credentials are NOT sent for the '*' entry in api.cors.allow.origin (a literal '*' is returned). "
                    + "Credentials are honored only for an exact match of an explicit Origin. "
                    + "To allow credentialed cross-origin access, set explicit origins in api.cors.allow.origin.");
        }
    }

    /**
     * Processes the CORS request by setting standard CORS headers on the response.
     * For an EXACT origin match the request Origin is reflected and credentials may be sent;
     * for a WILDCARD match a literal "*" is returned and credentials are never sent.
     *
     * @param origin the origin of the request
     * @param matchType how the origin matched the allow list
     * @param request the servlet request
     * @param response the servlet response to add CORS headers to
     */
    @Override
    public void process(final String origin, final CorsMatchType matchType, final ServletRequest request, final ServletResponse response) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (matchType == CorsMatchType.EXACT) {
            httpResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            if (fessConfig.isApiCorsAllowCredentials()) {
                httpResponse.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            }
        } else { // WILDCARD: never reflect, never send credentials
            httpResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, ALLOW_ORIGIN_ALL);
        }
        // Vary: Origin is emitted centrally by CorsFilter (avoid duplicates).
        httpResponse.setHeader(ACCESS_CONTROL_ALLOW_METHODS, fessConfig.getApiCorsAllowMethods());
        httpResponse.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, fessConfig.getApiCorsAllowHeaders());
        httpResponse.setHeader(ACCESS_CONTROL_MAX_AGE, fessConfig.getApiCorsMaxAge());
    }

}
