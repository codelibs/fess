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
import org.codelibs.fess.helper.RateLimitHelper;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter for rate limiting to protect against bot attacks and excessive requests.
 * Tracks request counts per IP address and blocks excessive requests based on configurable thresholds.
 */
public class RateLimitFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(RateLimitFilter.class);

    /**
     * Creates a new instance of RateLimitFilter.
     */
    public RateLimitFilter() {
        // Default constructor
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (!ComponentUtil.available()) {
            chain.doFilter(request, response);
            return;
        }

        final RateLimitHelper rateLimitHelper = ComponentUtil.getRateLimitHelper();

        if (!rateLimitHelper.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final String clientIp = rateLimitHelper.getClientIp(httpRequest);

        if (logger.isDebugEnabled()) {
            logger.debug("Rate limit check: clientIp={}, path={}", clientIp, httpRequest.getRequestURI());
        }

        // Check if IP is blocked
        if (rateLimitHelper.isBlocked(clientIp)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Request blocked: clientIp={}", clientIp);
            }
            sendBlockedResponse(httpResponse);
            return;
        }

        // Check rate limit
        if (!rateLimitHelper.allowRequest(clientIp)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Rate limit exceeded: clientIp={}", clientIp);
            }
            sendRateLimitResponse(httpResponse, rateLimitHelper.getRetryAfterSeconds());
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Send a 403 Forbidden response for blocked IPs.
     * @param response the HTTP response
     * @throws IOException if an I/O error occurs
     */
    protected void sendBlockedResponse(final HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"ip_blocked\",\"message\":\"Access denied.\"}");
    }

    /**
     * Send a 429 Too Many Requests response.
     * @param response the HTTP response
     * @param retryAfterSeconds the Retry-After header value
     * @throws IOException if an I/O error occurs
     */
    protected void sendRateLimitResponse(final HttpServletResponse response, final int retryAfterSeconds) throws IOException {
        response.setStatus(429); // Too Many Requests
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.getWriter()
                .write("{\"error\":\"rate_limit_exceeded\",\"message\":\"Too many requests. Please retry after " + retryAfterSeconds
                        + " seconds.\",\"retry_after\":" + retryAfterSeconds + "}");
    }
}
