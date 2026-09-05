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
import org.lastaflute.web.servlet.filter.RequestLoggingFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Reports every request that ends as a 500 with a single WARN line.
 *
 * <p>{@code web.xml} switches {@code errorLogging} off on {@code lastaShowbaseFilter}, so
 * {@link RequestLoggingFilter} writes its multi-line server-error dump at debug on the
 * {@code org.lastaflute} logger, and it neither rethrows the exception nor lets the container
 * see it. With the shipped log level that dump is dropped, and a 500 leaves no trace anywhere.
 * This filter restores a minimum: what failed, where, and why, on one line.</p>
 *
 * <p>The exception itself is only reachable through
 * {@link RequestLoggingFilter#setServerErrorHandlerOnThread(RequestLoggingFilter.RequestServerErrorHandler)},
 * whose callback the logging filter invokes from {@code sendInternalServerError} — that is, on
 * the 500 path only. Client errors ({@code RequestClientErrorException}, e.g. 404) go through a
 * separate callback and deliberate redirects never raise at all, so neither reaches this class.
 * The handler is per-thread, hence this wrapper: {@code web.xml} maps it just ahead of
 * {@code lastaShowbaseFilter} so that it runs outside the logging filter, and it installs the
 * callback for the duration of the request.</p>
 *
 * <p>The registration lives in {@code web.xml} rather than in
 * {@code FessFwAssistantDirector#prepareWebDirection}, which is where a LastaFlute application
 * would normally put it with {@code FwWebDirection#directServletFilter(filter, false)}. The
 * assistant director is named by {@code lastaflute_director.xml}, which the crawler, thumbnail,
 * suggest and chunk child processes also read: their classpath is
 * {@code WEB-INF/classes} plus {@code WEB-INF/lib} plus {@code WEB-INF/env/<type>/lib} and
 * carries no servlet API, so a servlet type reachable from that class fails its verification
 * and kills every child process at DI container startup.</p>
 */
public class ServerErrorLoggingFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(ServerErrorLoggingFilter.class);

    /**
     * Creates a new instance of ServerErrorLoggingFilter.
     */
    public ServerErrorLoggingFilter() {
        // Default constructor
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        RequestLoggingFilter.setServerErrorHandlerOnThread(this::logServerError);
        try {
            chain.doFilter(request, response);
        } finally {
            // RequestLoggingFilter clears the handler itself for routed requests, but not on the
            // static-resource path, so do not leave it attached to a pooled container thread.
            RequestLoggingFilter.setServerErrorHandlerOnThread(null);
        }
    }

    /**
     * Writes the one line an operator needs to find out that a 500 happened.
     *
     * <p>The stack trace stays at debug on purpose: an operator running with the shipped level
     * gets the method, the path, the exception class and its message, and nothing else. The
     * query string is left out because it carries caller-supplied values.</p>
     *
     * @param request the request that failed
     * @param response the response, possibly already committed
     * @param cause the exception that became the 500
     */
    protected void logServerError(final HttpServletRequest request, final HttpServletResponse response, final Throwable cause) {
        // The throwable is passed as its class name and message, never as the trailing argument,
        // so that log4j2 does not append a stack trace to this WARN line.
        logger.warn("Server error: {} {} [{}: {}]", request.getMethod(), request.getRequestURI(), cause.getClass().getName(),
                cause.getMessage());
        if (logger.isDebugEnabled()) {
            logger.debug("Server error: {} {}", request.getMethod(), request.getRequestURI(), cause);
        }
    }

}
