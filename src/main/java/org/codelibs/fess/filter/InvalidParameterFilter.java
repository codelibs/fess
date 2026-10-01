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
import org.apache.tomcat.util.http.InvalidParameterException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Answers a request whose parameters cannot be decoded with the status Tomcat assigns to that
 * failure, instead of a 500.
 *
 * <p>Tomcat decodes the query string (and a form-encoded body) when a parameter is first read,
 * and an invalid percent-encoding ({@code ?q=%ff}, {@code ?q=%}) makes that first read throw
 * {@link InvalidParameterException}. Which code reads first differs per URL: LastaFlute reads
 * parameters while it routes a request and binds its form, before any action method runs, and a
 * handler may read them anywhere. None of that code treats the exception as the caller's mistake,
 * so the request ended as a 500 that was logged as a server error. Reading the parameters once
 * here turns the failure into the client error it is, whichever code would have read them
 * first.</p>
 *
 * <p>The status is the exception's own {@link InvalidParameterException#getErrorCode()}: 400 for
 * an undecodable parameter, 413 when a form body exceeds the container's size limit. It is sent
 * with {@code sendError}, so the container's error page answers exactly as it does for any other
 * 400: the active theme's error page for a browser, a one-line {@code text/plain} body for an
 * API client.</p>
 *
 * <p>{@code web.xml} maps this filter after {@code webApiFilter}. A request that a
 * {@link org.codelibs.fess.api.WebApiManager} serves never reaches it, so an API keeps its own
 * error format; {@code SearchApiV2Manager} reads the parameters first itself for that reason.</p>
 */
public class InvalidParameterFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(InvalidParameterFilter.class);

    /**
     * Creates a new instance of InvalidParameterFilter.
     */
    public InvalidParameterFilter() {
        // Default constructor
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        try {
            // The parse is cached, so a valid request pays for it here instead of later.
            request.getParameterNames();
        } catch (final InvalidParameterException e) {
            // A scanner or a mistyped link can send these in bulk, so a rejection stays at debug.
            if (logger.isDebugEnabled()) {
                logger.debug("Rejected a request with undecodable parameters: status={}", e.getErrorCode(), e);
            }
            if (response instanceof final HttpServletResponse res && !res.isCommitted()) {
                res.sendError(e.getErrorCode());
            }
            return;
        }
        chain.doFilter(request, response);
    }

}
