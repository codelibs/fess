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
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codelibs.fess.api.WebApiManager;
import org.codelibs.fess.api.WebApiManagerFactory;
import org.codelibs.fess.util.ComponentUtil;

public class WebApiFilter implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // nothing
    }

    @Override
    public void destroy() {
        // nothing
    }

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
