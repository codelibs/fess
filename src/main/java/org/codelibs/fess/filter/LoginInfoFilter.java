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
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codelibs.fess.entity.LoginInfo;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.sastruts.core.SSCConstants;

// TODO refactoring...
public class LoginInfoFilter implements Filter {
    private long updateInterval = 60 * 60 * 1000L; // 1h

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        final String value = filterConfig.getInitParameter("updateInterval");
        if (value != null) {
            updateInterval = Long.parseLong(value);
        }
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
            ServletException {
        final HttpServletRequest hRequest = (HttpServletRequest) request;
        final HttpSession session = hRequest.getSession();
        LoginInfo loginInfo = (LoginInfo) session.getAttribute(SSCConstants.USER_INFO);
        if (loginInfo == null) {
            loginInfo = new LoginInfo();
            session.setAttribute(SSCConstants.USER_INFO, loginInfo);

            updateRoleList(hRequest, loginInfo);
        } else {
            final long now = System.currentTimeMillis();
            if (now - loginInfo.getUpdatedTime() > updateInterval) {
                updateRoleList(hRequest, loginInfo);
                loginInfo.setUpdatedTime(now);
            }
        }

        chain.doFilter(request, response);
    }

    private void updateRoleList(final HttpServletRequest hRequest, final LoginInfo loginInfo) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final Set<String> authenticatedRoleList = systemHelper.getAuthenticatedRoleSet();
        final Set<String> roleSet = new HashSet<>();
        for (final String role : authenticatedRoleList) {
            if (hRequest.isUserInRole(role)) {
                roleSet.add(role);
            }
        }
        loginInfo.setRoleSet(roleSet);
    }

    @Override
    public void destroy() {
        // nothing
    }

}
