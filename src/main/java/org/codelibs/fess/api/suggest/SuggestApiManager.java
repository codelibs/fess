/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.api.suggest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codelibs.fess.api.BaseApiManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuggestApiManager extends BaseApiManager {
    private static final Logger logger = LoggerFactory.getLogger(SuggestApiManager.class);

    public SuggestApiManager() {
        setPathPrefix("/suggest");
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        return false; // TODO remove
        //        final String servletPath = request.getServletPath();
        //        return servletPath.startsWith(pathPrefix);
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException,
            ServletException {
        throw new UnsupportedOperationException("TODO");
    }

}
