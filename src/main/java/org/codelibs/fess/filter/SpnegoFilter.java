/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.exception.ServletRuntimeException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.spnego.SpnegoHttpFilter;
import org.codelibs.spnego.SpnegoHttpServletRequest;

/**
 * SpnegoFilter supports Integrated Windows Authentication(SSO).
 * 
 * @author shinsuke
 */
public class SpnegoFilter extends SpnegoHttpFilter {

    public void init(final FilterConfig filterConfig) throws ServletException {
        ComponentUtil.processAfterContainerInit(() -> {
            if (ComponentUtil.getFessConfig().isSsoEnabled()) {
                initAuthenticator(filterConfig);
            }
        });
    }

    private void initAuthenticator(final FilterConfig filterConfig) {
        try {
            super.init(new FilterConfig() {

                @Override
                public ServletContext getServletContext() {
                    return filterConfig.getServletContext();
                }

                @Override
                public Enumeration<String> getInitParameterNames() {
                    return filterConfig.getInitParameterNames();
                }

                @Override
                public String getInitParameter(String name) {
                    if (Constants.KRB5_CONF.equals(name)) {
                        final String krb5Conf = ComponentUtil.getFessConfig().getSpnegoKrb5Conf();
                        if (StringUtil.isNotBlank(krb5Conf)) {
                            return krb5Conf;
                        }
                    } else if (Constants.LOGIN_CONF.equals(name)) {
                        final String loginConf = ComponentUtil.getFessConfig().getSpnegoLoginConf();
                        if (StringUtil.isNotBlank(loginConf)) {
                            return loginConf;
                        }
                    } else if (Constants.PREAUTH_USERNAME.equals(name)) {
                        final String username = ComponentUtil.getFessConfig().getSpnegoPreauthUsername();
                        if (StringUtil.isNotBlank(username)) {
                            return username;
                        }
                    } else if (Constants.PREAUTH_PASSWORD.equals(name)) {
                        final String password = ComponentUtil.getFessConfig().getSpnegoPreauthPassword();
                        if (StringUtil.isNotBlank(password)) {
                            return password;
                        }
                    }
                    return filterConfig.getInitParameter(name);
                }

                @Override
                public String getFilterName() {
                    return filterConfig.getFilterName();
                }
            });
        } catch (final ServletException e) {
            throw new FessSystemException("Initialization failed.", e);
        }
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
            ServletException {
        try {
            ComponentUtil.getRequestManager().findUserBean(FessUserBean.class).ifPresent(u -> {
                doFilter(() -> chain.doFilter(request, response));
            }).orElse(() -> {
                if (ComponentUtil.getFessConfig().isSsoEnabled()) {
                    doFilter(() -> SpnegoFilter.super.doFilter(request, response, chain));
                } else {
                    doFilter(() -> chain.doFilter(request, response));
                }
            });
        } catch (final IORuntimeException e) {
            throw (IOException) e.getCause();
        } catch (final ServletRuntimeException e) {
            throw (ServletException) e.getCause();
        }
    }

    @Override
    protected void processRequest(final SpnegoHttpServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (StringUtil.isNotBlank(request.getRemoteUser())) {
            // TODO save path and parameters into session
            RequestDispatcher dispatcher = request.getRequestDispatcher(ComponentUtil.getFessConfig().getSsoLoginPath());
            dispatcher.forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    protected void doFilter(DoFilterCallback callback) {
        try {
            callback.run();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } catch (ServletException e) {
            throw new ServletRuntimeException(e);
        }
    }

    interface DoFilterCallback {

        void run() throws IOException, ServletException;
    }
}
