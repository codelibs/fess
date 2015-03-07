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

package org.codelibs.fess.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codelibs.fess.entity.LoginInfo;
import org.codelibs.fess.helper.AdRoleHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.sastruts.core.SSCConstants;
import org.seasar.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdLoginInfoFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AdLoginInfoFilter.class);

    private long updateInterval = 60 * 60 * 1000L; // 1h

    private boolean redirectLoginError;

    private boolean useTestUser;

    private String testUserName;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        redirectLoginError = "true".equalsIgnoreCase(filterConfig.getInitParameter("redirectLoginError"));

        final String value = filterConfig.getInitParameter("updateInterval");
        if (value != null) {
            updateInterval = Long.parseLong(value);
        }

        useTestUser = "true".equalsIgnoreCase(filterConfig.getInitParameter("useTestUser"));

        testUserName = filterConfig.getInitParameter("testUserName");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
            ServletException {
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest httpRequest = (HttpServletRequest) request;
            final HttpSession session = httpRequest.getSession();

            String userId = httpRequest.getRemoteUser();
            if (useTestUser) {
                userId = testUserName;
            }

            if (StringUtil.isEmpty(userId)) {
                final String servletPath = ((HttpServletRequest) request).getServletPath();
                if (redirectLoginError && "/index.do".equals(servletPath)) {
                    ((HttpServletResponse) response).sendRedirect(httpRequest.getContextPath() + "error/badRequest");
                    return;
                }
            }

            LoginInfo loginInfo = (LoginInfo) session.getAttribute(SSCConstants.USER_INFO);
            if (loginInfo == null) {
                loginInfo = new LoginInfo();
                loginInfo.setUsername(userId);
                updateRoleList(userId, loginInfo);
                session.setAttribute(SSCConstants.USER_INFO, loginInfo);
            } else {
                final long now = System.currentTimeMillis();
                if (now - loginInfo.getUpdatedTime() > updateInterval) {
                    loginInfo.setUsername(userId);
                    updateRoleList(userId, loginInfo);
                    loginInfo.setUpdatedTime(now);
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private void updateRoleList(final String userId, final LoginInfo loginInfo) {
        final AdRoleHelper adRoleHelper = ComponentUtil.getAdRoleHelper();
        final List<String> roleList = adRoleHelper.getRoleList(userId);
        final Set<String> roleSet = new HashSet<>();
        for (final String role : roleList) {
            roleSet.add(role);
        }
        loginInfo.setRoleSet(roleSet);
        if (logger.isDebugEnabled()) {
            logger.debug(loginInfo.toString());
        }
    }
}
