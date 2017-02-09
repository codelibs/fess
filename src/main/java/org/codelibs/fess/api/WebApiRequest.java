/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class WebApiRequest extends HttpServletRequestWrapper {
    protected String servletPath;

    public WebApiRequest(final HttpServletRequest request, final String servletPath) {
        super(request);
        this.servletPath = servletPath;
    }

    @Override
    public String getServletPath() {
        if (getQueryString() != null && getQueryString().indexOf("SAStruts.method") != -1) {
            return super.getServletPath();
        } else {
            return servletPath;
        }
    }

}
