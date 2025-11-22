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

    /**
     * Cached FessConfig instance for performance optimization.
     */
    protected FessConfig fessConfig;

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
     * This method is automatically called after bean initialization and caches
     * the FessConfig instance for better performance.
     */
    @PostConstruct
    public void register() {
        final CorsHandlerFactory factory = ComponentUtil.getCorsHandlerFactory();
        fessConfig = ComponentUtil.getFessConfig();
        fessConfig.getApiCorsAllowOriginList().forEach(s -> factory.add(s, this));
    }

    /**
     * Processes the CORS request by adding standard CORS headers to the response.
     * Headers include allowed origin, methods, headers, max age, credentials setting,
     * expose headers, and vary header for proper caching.
     *
     * @param origin the origin of the request
     * @param request the servlet request
     * @param response the servlet response to add CORS headers to
     */
    @Override
    public void process(final String origin, final ServletRequest request, final ServletResponse response) {
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Add standard CORS headers
        httpResponse.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        httpResponse.addHeader(ACCESS_CONTROL_ALLOW_METHODS, fessConfig.getApiCorsAllowMethods());
        httpResponse.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, fessConfig.getApiCorsAllowHeaders());
        httpResponse.addHeader(ACCESS_CONTROL_MAX_AGE, fessConfig.getApiCorsMaxAge());

        // Add credentials header only if explicitly set to true (CORS spec compliance)
        final String allowCredentials = fessConfig.getApiCorsAllowCredentials();
        if ("true".equalsIgnoreCase(allowCredentials)) {
            httpResponse.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }

        // Add expose headers if configured
        final String exposeHeaders = fessConfig.getApiCorsExposeHeaders();
        if (exposeHeaders != null && !exposeHeaders.isEmpty()) {
            httpResponse.addHeader(ACCESS_CONTROL_EXPOSE_HEADERS, exposeHeaders);
        }

        // Add Vary header for proper caching
        httpResponse.addHeader(VARY, ORIGIN);

        // Handle private network access request if present
        if (request instanceof jakarta.servlet.http.HttpServletRequest) {
            final jakarta.servlet.http.HttpServletRequest httpRequest = (jakarta.servlet.http.HttpServletRequest) request;
            final String privateNetworkRequest = httpRequest.getHeader(ACCESS_CONTROL_REQUEST_PRIVATE_NETWORK);
            if ("true".equalsIgnoreCase(privateNetworkRequest)) {
                httpResponse.addHeader(ACCESS_CONTROL_ALLOW_PRIVATE_NETWORK, "true");
            }
        }
    }

}
