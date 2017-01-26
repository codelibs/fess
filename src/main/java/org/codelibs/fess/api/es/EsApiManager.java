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
package org.codelibs.fess.api.es;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.elasticsearch.runner.net.Curl.Method;
import org.codelibs.elasticsearch.runner.net.CurlRequest;
import org.codelibs.fess.Constants;
import org.codelibs.fess.api.BaseApiManager;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.exception.WebApiException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ResourceUtil;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.servlet.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsApiManager extends BaseApiManager {
    private static final String ADMIN_SERVER = "/admin/server_";

    private static final Logger logger = LoggerFactory.getLogger(EsApiManager.class);

    protected String[] acceptedRoles = new String[] { "admin" };

    public EsApiManager() {
        setPathPrefix(ADMIN_SERVER);
    }

    @Override
    public boolean matches(final HttpServletRequest request) {
        final String servletPath = request.getServletPath();
        if (servletPath.startsWith(pathPrefix)) {
            final RequestManager requestManager = ComponentUtil.getRequestManager();
            return requestManager.findUserBean(FessUserBean.class).map(user -> user.hasRoles(acceptedRoles)).orElse(Boolean.FALSE);
        }
        return false;
    }

    @Override
    public void process(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException,
            ServletException {
        try {
            getSessionManager().getAttribute(Constants.ES_API_ACCESS_TOKEN, String.class).ifPresent(token -> {
                final String servletPath = request.getServletPath();
                final String pathPrefix = ADMIN_SERVER + token;
                if (!servletPath.startsWith(pathPrefix)) {
                    throw new WebApiException(HttpServletResponse.SC_FORBIDDEN, "Invalid access token.");
                }
                final String path;
                final String value = servletPath.substring(pathPrefix.length());
                if (!value.startsWith("/")) {
                    path = "/" + value;
                } else {
                    path = value;
                }
                processRequest(request, response, path);
            }).orElse(() -> {
                throw new WebApiException(HttpServletResponse.SC_FORBIDDEN, "Invalid session.");
            });
        } catch (final WebApiException e) {
            logger.debug("Web API access error. ", e);
            e.sendError(response);
        }
    }

    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response, final String path) {
        if (StringUtil.isNotBlank(path)) {
            final String lowerPath = path.toLowerCase(Locale.ROOT);
            if (lowerPath.endsWith(".html")) {
                response.setContentType("text/html;charset=utf-8");
            } else if (lowerPath.endsWith(".txt")) {
                response.setContentType("text/plain");
            } else if (lowerPath.endsWith(".css")) {
                response.setContentType("text/css");
            }
        }

        if (path.startsWith("/_plugin/") || path.equals("/_plugin")) {
            processPluginRequest(request, response, path.replaceFirst("^/_plugin", StringUtil.EMPTY));
            return;
        }

        final Method httpMethod = Method.valueOf(request.getMethod().toUpperCase(Locale.ROOT));
        final CurlRequest curlRequest = new CurlRequest(httpMethod, ResourceUtil.getElasticsearchHttpUrl() + path);

        request.getParameterMap().entrySet().stream().forEach(entry -> {
            if (entry.getValue().length > 1) {
                curlRequest.param(entry.getKey(), String.join(",", entry.getValue()));
            } else if (entry.getValue().length == 1) {
                curlRequest.param(entry.getKey(), entry.getValue()[0]);
            }
        });
        curlRequest.onConnect((req, con) -> {
            con.setDoOutput(true);
            if (httpMethod != Method.GET) {
                try (ServletInputStream in = request.getInputStream(); OutputStream out = con.getOutputStream()) {
                    CopyUtil.copy(in, out);
                } catch (final IOException e) {
                    throw new WebApiException(HttpServletResponse.SC_BAD_REQUEST, e);
                }
            }
        }).execute(con -> {
            try (InputStream in = con.getInputStream(); ServletOutputStream out = response.getOutputStream()) {
                response.setStatus(con.getResponseCode());
                CopyUtil.copy(in, out);
            } catch (final ClientAbortException e) {
                logger.debug("Client aborts this request.", e);
            } catch (final Exception e) {
                if (e.getCause() instanceof ClientAbortException) {
                    logger.debug("Client aborts this request.", e);
                } else {
                    try (InputStream err = con.getErrorStream()) {
                        logger.error(new String(InputStreamUtil.getBytes(err), Constants.CHARSET_UTF_8));
                    } catch (final IOException e1) {
                        // ignore
            }
            throw new WebApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
    }
}       );
    }

    private void processPluginRequest(final HttpServletRequest request, final HttpServletResponse response, final String path) {
        Path filePath = ResourceUtil.getSitePath(path.replaceAll("\\.\\.+", StringUtil.EMPTY).replaceAll("/+", "/").split("/"));
        if (Files.isDirectory(filePath)) {
            filePath = filePath.resolve("index.html");
        }
        if (Files.exists(filePath)) {
            try (InputStream in = Files.newInputStream(filePath); ServletOutputStream out = response.getOutputStream()) {
                response.setStatus(HttpServletResponse.SC_OK);
                CopyUtil.copy(in, out);
            } catch (final ClientAbortException e) {
                logger.debug("Client aborts this request.", e);
            } catch (final IOException e) {
                logger.error("Failed to read " + path + " from " + filePath);
                throw new WebApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        } else {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, path + " is not found.");
            } catch (final ClientAbortException e) {
                logger.debug("Client aborts this request.", e);
            } catch (final IOException e) {
                logger.error("Failed to read " + path + " from " + filePath);
                throw new WebApiException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            }
        }
    }

    public void setAcceptedRoles(final String[] acceptedRoles) {
        this.acceptedRoles = acceptedRoles;
    }

    public String getServerPath() {
        return getSessionManager().getAttribute(Constants.ES_API_ACCESS_TOKEN, String.class).map(token -> ADMIN_SERVER + token)
                .orElseThrow(() -> new FessSystemException("Cannot create an access token."));
    }

    public void saveToken() {
        getSessionManager().setAttribute(Constants.ES_API_ACCESS_TOKEN, UUID.randomUUID().toString().replace("-", ""));
    }

    private SessionManager getSessionManager() {
        return ComponentUtil.getComponent(SessionManager.class);
    }
}
