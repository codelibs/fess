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

package jp.sf.fess.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.h2.tools.Server;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class H2ConfigServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory
            .getLogger(H2ConfigServlet.class);

    private static final long serialVersionUID = 1L;

    protected Server server = null;

    @Override
    public void init() throws ServletException {
        final List<String> argList = new ArrayList<String>();
        String value;

        try {
            argList.add("-baseDir");
            value = getServletConfig().getInitParameter("baseDir");
            if (value != null) {
                argList.add(value);
            } else {
                argList.add(getServletContext().getRealPath("/WEB-INF/db/"));
            }

            argList.add("-tcp");

            value = getServletConfig().getInitParameter("tcpAllowOthers");
            if (value != null && "true".equalsIgnoreCase(value)) {
                argList.add("-tcpAllowOthers");
            }

            value = getServletConfig().getInitParameter("tcpPort");
            if (value != null) {
                argList.add("-tcpPort");
                argList.add(value);
            }

            value = getServletConfig().getInitParameter("tcpSSL");
            if (value != null && "true".equalsIgnoreCase(value)) {
                argList.add("-tcpSSL");
            }

            value = getServletConfig().getInitParameter("tcpPassword");
            if (value != null) {
                argList.add("-tcpPassword");
                argList.add(value);
            }

            if (logger.isInfoEnabled()) {
                logger.info("Starting H2 server...");
            }
            server = Server.createTcpServer(
                    argList.toArray(new String[argList.size()])).start();
        } catch (final Exception e) {
            throw new ServletException("Could not start Fess Config DB.", e);
        }
    }

    @Override
    public void destroy() {
        if (System.getProperty("java.specification.version").equals("1.7")) {
            DisposableUtil.add(new Disposable() {
                @Override
                public void dispose() {
                    if (server != null) {
                        server.stop();
                    }
                }
            });
        } else {
            if (server != null) {
                server.stop();
            }
        }
    }

}
