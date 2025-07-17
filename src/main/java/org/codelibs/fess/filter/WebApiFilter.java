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

import org.codelibs.fess.api.WebApiManager;
import org.codelibs.fess.api.WebApiManagerFactory;
import org.codelibs.fess.util.ComponentUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet filter for processing web API requests.
 * This filter intercepts HTTP requests and delegates processing to appropriate web API managers.
 */
public class WebApiFilter implements Filter {

    /**
     * Default constructor.
     */
    public WebApiFilter() {
        // Default constructor
    }

    /**
     * Initializes the web API filter.
     *
     * @param filterConfig The filter configuration
     * @throws ServletException If initialization fails
     */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // nothing
    }

    /**
     * Destroys the web API filter and cleans up resources.
     */
    @Override
    public void destroy() {
        // nothing
    }

    /**
     * Filters HTTP requests and processes them through appropriate web API managers.
     *
     * @param request The servlet request
     * @param response The servlet response
     * @param chain The filter chain
     * @throws IOException If an I/O error occurs
     * @throws ServletException If a servlet error occurs
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final WebApiManagerFactory webApiManagerFactory = ComponentUtil.getWebApiManagerFactory();
        final WebApiManager webApiManager = webApiManagerFactory.get((HttpServletRequest) request);
        if (webApiManager == null) {
            chain.doFilter(request, response);
        } else {
            webApiManager.process((HttpServletRequest) request, (HttpServletResponse) response, chain);
        }
    }

}
