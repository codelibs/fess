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
        fessConfig.getApiCorsAllowOriginList().forEach(s -> factory.add(s, this));
    }

    /**
     * Processes the CORS request by adding standard CORS headers to the response.
     * Headers include allowed origin, methods, headers, max age, and credentials setting.
     *
     * @param origin the origin of the request
     * @param request the servlet request
     * @param response the servlet response to add CORS headers to
     */
    @Override
    public void process(final String origin, final ServletRequest request, final ServletResponse response) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        httpResponse.addHeader(ACCESS_CONTROL_ALLOW_METHODS, fessConfig.getApiCorsAllowMethods());
        httpResponse.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, fessConfig.getApiCorsAllowHeaders());
        httpResponse.addHeader(ACCESS_CONTROL_MAX_AGE, fessConfig.getApiCorsMaxAge());
        httpResponse.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, fessConfig.getApiCorsAllowCredentials());
    }

}
