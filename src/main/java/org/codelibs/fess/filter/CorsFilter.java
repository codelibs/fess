/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.cors.CorsHandler;
import org.codelibs.fess.cors.CorsHandlerFactory;
import org.codelibs.fess.util.ComponentUtil;

public class CorsFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(CorsFilter.class);

    protected static final String OPTIONS = "OPTIONS";

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String origin = httpRequest.getHeader("Origin");
        if (StringUtil.isNotBlank(origin)) {
            if (logger.isDebugEnabled()) {
                logger.debug("HTTP Request: {}", httpRequest.getMethod());
            }
            final CorsHandlerFactory factory = ComponentUtil.getCorsHandlerFactory();
            final CorsHandler handler = factory.get(origin);
            if (handler != null) {
                handler.process(origin, request, response);

                if (OPTIONS.equals(httpRequest.getMethod())) {
                    final HttpServletResponse httpResponse = (HttpServletResponse) response;
                    httpResponse.setStatus(HttpServletResponse.SC_ACCEPTED);
                    return;
                }
            } else if (logger.isDebugEnabled()) {
                logger.debug("No CorsHandler for {}", origin);
            }
        }

        chain.doFilter(request, response);
    }

}
