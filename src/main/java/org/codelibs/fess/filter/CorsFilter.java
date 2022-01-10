/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public class CorsFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(CorsFilter.class);

    protected static final String OPTIONS = "OPTIONS";

    protected static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    protected static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    protected static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    protected static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

    protected static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    protected static final String WILDCARD = "*";

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String origin = httpRequest.getHeader("Origin");
        if (StringUtil.isBlank(origin)) {
            chain.doFilter(request, response);
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("HTTP Request: {}", httpRequest.getMethod());
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        final String allowOrigin = getAllowOrigin(fessConfig, origin);
        if (StringUtil.isNotBlank(allowOrigin)) {
            if (logger.isDebugEnabled()) {
                logger.debug("allowOrigin: {}", allowOrigin);
            }
            final HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowOrigin);
            httpResponse.addHeader(ACCESS_CONTROL_ALLOW_METHODS, fessConfig.getApiCorsAllowMethods());
            httpResponse.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, fessConfig.getApiCorsAllowHeaders());
            httpResponse.addHeader(ACCESS_CONTROL_MAX_AGE, fessConfig.getApiCorsMaxAge());
            httpResponse.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, fessConfig.getApiCorsAllowCredentials());

            if (OPTIONS.equals(httpRequest.getMethod())) {
                httpResponse.setStatus(HttpServletResponse.SC_ACCEPTED);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    protected String getAllowOrigin(final FessConfig fessConfig, final String origin) {
        final String allowOrigin = fessConfig.getApiCorsAllowOrigin();
        if (StringUtil.isBlank(allowOrigin)) {
            return StringUtil.EMPTY;
        }

        if (WILDCARD.equals(allowOrigin)) {
            return allowOrigin;
        }

        return fessConfig.getApiCorsAllowOriginList().stream().filter(s -> s.equals(origin)).findFirst().orElse(StringUtil.EMPTY);
    }

}
