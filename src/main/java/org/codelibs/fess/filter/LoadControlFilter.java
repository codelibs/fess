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
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter for CPU load-based request control.
 * Returns HTTP 429 (Too Many Requests) when CPU usage exceeds configurable thresholds.
 * Web and API requests have independent threshold settings.
 */
public class LoadControlFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(LoadControlFilter.class);

    private static final int RETRY_AFTER_SECONDS = 60;

    private static final Set<String> STATIC_EXTENSIONS =
            Arrays.stream(new String[] { ".css", ".js", ".png", ".jpg", ".gif", ".ico", ".svg", ".woff", ".woff2", ".ttf", ".eot" })
                    .collect(Collectors.toSet());

    /**
     * Creates a new instance of LoadControlFilter.
     */
    public LoadControlFilter() {
        // Default constructor
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (!ComponentUtil.available()) {
            chain.doFilter(request, response);
            return;
        }

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if (isExcludedPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        final boolean isApiPath = path.startsWith("/api/");
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final int threshold = isApiPath ? fessConfig.getApiLoadControlAsInteger() : fessConfig.getWebLoadControlAsInteger();

        if (threshold >= 100) {
            chain.doFilter(request, response);
            return;
        }

        final short cpuPercent = ComponentUtil.getSystemHelper().getSearchEngineCpuPercent();

        if (cpuPercent < threshold) {
            chain.doFilter(request, response);
            return;
        }

        if (logger.isInfoEnabled()) {
            logger.info("Rejecting request due to high CPU load: path={}, cpu={}%, threshold={}%", path, cpuPercent, threshold);
        }

        if (isApiPath) {
            sendApiResponse(httpResponse);
        } else {
            httpResponse.sendError(429);
        }
    }

    /**
     * Checks if the given path should be excluded from load control.
     * @param path the request path
     * @return true if the path should be excluded
     */
    protected boolean isExcludedPath(final String path) {
        if (path.startsWith("/admin") || path.startsWith("/error") || path.startsWith("/login")) {
            return true;
        }
        final int dotIndex = path.lastIndexOf('.');
        if (dotIndex >= 0) {
            final String extension = path.substring(dotIndex);
            return STATIC_EXTENSIONS.contains(extension);
        }
        return false;
    }

    /**
     * Sends a 429 JSON response for API requests.
     * @param response the HTTP response
     * @throws IOException if an I/O error occurs
     */
    protected void sendApiResponse(final HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Retry-After", String.valueOf(RETRY_AFTER_SECONDS));
        response.getWriter()
                .write("{\"response\":{\"status\":9,\"message\":\"Server is busy. Please retry after " + RETRY_AFTER_SECONDS
                        + " seconds.\",\"retry_after\":" + RETRY_AFTER_SECONDS + "}}");
    }
}
