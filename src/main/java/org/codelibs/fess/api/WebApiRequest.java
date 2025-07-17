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
package org.codelibs.fess.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * Wrapper for HTTP servlet requests in web API context.
 * This class extends HttpServletRequestWrapper to provide custom servlet path handling
 * for web API requests.
 */
public class WebApiRequest extends HttpServletRequestWrapper {
    /**
     * The custom servlet path for this web API request.
     */
    protected String servletPath;

    /**
     * Constructs a WebApiRequest with the specified request and servlet path.
     *
     * @param request The original HTTP servlet request
     * @param servletPath The custom servlet path for this web API request
     */
    public WebApiRequest(final HttpServletRequest request, final String servletPath) {
        super(request);
        this.servletPath = servletPath;
    }

    /**
     * Gets the servlet path for this request.
     * Returns the custom servlet path unless the query string contains SAStruts.method.
     *
     * @return The servlet path
     */
    @Override
    public String getServletPath() {
        if (getQueryString() != null && getQueryString().indexOf("SAStruts.method") != -1) {
            return super.getServletPath();
        }
        return servletPath;
    }

}
