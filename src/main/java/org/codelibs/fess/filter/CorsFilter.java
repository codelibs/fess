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
package org.codelibs.fess.filter;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.cors.CorsHandlerFactory;
import org.codelibs.fess.cors.CorsResolution;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter for handling Cross-Origin Resource Sharing (CORS) requests.
 * Processes CORS headers and handles preflight OPTIONS requests.
 */
public class CorsFilter implements Filter {

    /**
     * Creates a new instance of CorsFilter.
     */
    public CorsFilter() {
        // Default constructor
    }

    private static final Logger logger = LogManager.getLogger(CorsFilter.class);

    /**
     * HTTP OPTIONS method constant used for CORS preflight requests.
     */
    protected static final String OPTIONS = "OPTIONS";

    /** Header name read to detect a genuine CORS preflight. */
    protected static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";

    /** {@code Vary} response header name. */
    protected static final String VARY = "Vary";

    /** {@code Origin} value appended to {@code Vary}. */
    protected static final String ORIGIN = "Origin";

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String origin = httpRequest.getHeader(ORIGIN);
        if (StringUtil.isBlank(origin)) {
            chain.doFilter(request, response);
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("HTTP Request: method={}", httpRequest.getMethod());
        }
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        // Append Origin to Vary for every origin-bearing request (cache-poisoning safe; single REQUEST dispatch).
        httpResponse.addHeader(VARY, ORIGIN);

        final boolean preflight =
                OPTIONS.equals(httpRequest.getMethod()) && StringUtil.isNotBlank(httpRequest.getHeader(ACCESS_CONTROL_REQUEST_METHOD));

        final CorsHandlerFactory factory = ComponentUtil.getCorsHandlerFactory();
        final CorsResolution resolution = factory.resolve(origin);
        if (resolution != null) {
            resolution.getHandler().process(origin, resolution.getMatchType(), request, response);
            if (preflight) {
                httpResponse.setStatus(HttpServletResponse.SC_ACCEPTED);
                return;
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("No CorsHandler: origin={}", origin);
            }
            if (preflight) {
                // Disallowed genuine preflight: short-circuit with no CORS headers so the browser blocks it.
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        chain.doFilter(request, response);
    }

}
