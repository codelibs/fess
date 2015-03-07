/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.servlet;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat7ConfigServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(Tomcat7ConfigServlet.class);

    private static final long serialVersionUID = 1L;

    @Override
    public void destroy() {
        if (logger.isInfoEnabled()) {
            logger.info("Removing HTTP connection manager...");
        }
        shutdownCommonsHttpClient();
    }

    private void shutdownCommonsHttpClient() {
        try {
            final Class<?> clazz = Class.forName("org.apache.commons.httpclient.MultiThreadedHttpConnectionManager");
            final Method method = clazz.getMethod("shutdownAll", null);
            method.invoke(null, null);
        } catch (final ClassNotFoundException e) {
            // ignore
        } catch (final Exception e) {
            logger.warn("Could not shutdown Commons HttpClient.", e);
        }
    }

}
