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

package org.codelibs.fess.web;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codelibs.core.crypto.CachedCipher;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.entity.LoginInfo;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.sastruts.core.SSCConstants;
import org.codelibs.sastruts.core.util.ActivityUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(LoginAction.class);

    private static final long serialVersionUID = 1L;

    @ActionForm
    @Resource
    protected LoginForm loginForm;

    @Resource
    protected SystemHelper systemHelper;

    @Execute(validator = false, input = "../index")
    public String index() {
        final HttpServletRequest request = LaRequestUtil.getRequest();
        final HttpSession session = request.getSession();
        // check login session
        final Object obj = session.getAttribute(SSCConstants.USER_INFO);
        if (obj instanceof LoginInfo) {
            final LoginInfo loginInfo = (LoginInfo) obj;
            if (loginInfo.isAdministrator()) {
                redirect(getAdminRootPath());
                return null;
            } else {
                return "logout.jsp";
            }
        }

        if ("logout".equals(loginForm.type)) {
            if (logger.isInfoEnabled()) {
                logger.info("Invalidated session. The username is " + request.getRemoteUser());
            }
            session.invalidate();
        }

        String returnPath;
        if (StringUtil.isNotBlank(loginForm.returnPath)) {
            final CachedCipher cipher = ComponentUtil.getCipher(Constants.AUTH_CIPHER);
            if (cipher == null) {
                throw new FessSystemException("A cipher for authentication is null. Please check a filter setting.");
            }
            final String value = cipher.decryptoText(loginForm.returnPath);
            final int idx = value.indexOf('|');
            if (idx >= 0) {
                returnPath = value.substring(idx + 1);
                LaRequestUtil.getRequest().getSession().setAttribute(Constants.RETURN_PATH, returnPath);
            } else {
                // invalid returnPath
                LaRequestUtil.getRequest().getSession().removeAttribute(Constants.RETURN_PATH);
            }
        } else {
            LaRequestUtil.getRequest().getSession().removeAttribute(Constants.RETURN_PATH);
        }

        return "login?redirect=true";
    }

    @Execute(validator = false, input = "../index")
    public String login() {
        final HttpServletRequest request = LaRequestUtil.getRequest();
        final HttpSession oldSession = request.getSession();

        final Map<String, Object> sessionObjMap = new HashMap<String, Object>();
        final Enumeration<String> e = oldSession.getAttributeNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            sessionObjMap.put(name, oldSession.getAttribute(name));
        }
        oldSession.invalidate();

        sessionObjMap.remove(Globals.MESSAGE_KEY);

        final HttpSession session = request.getSession();
        for (final Map.Entry<String, Object> entry : sessionObjMap.entrySet()) {
            session.setAttribute(entry.getKey(), entry.getValue());
        }

        // create user info
        final LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUsername(request.getRemoteUser());
        session.setAttribute(SSCConstants.USER_INFO, loginInfo);

        String returnPath;
        final Set<String> authenticatedRoleList = systemHelper.getAuthenticatedRoleSet();
        final Set<String> roleSet = new HashSet<>();
        for (final String role : authenticatedRoleList) {
            if (request.isUserInRole(role)) {
                roleSet.add(role);
            }
        }
        loginInfo.setRoleSet(roleSet);

        if (loginInfo.isAdministrator()) {
            ActivityUtil.login(request.getRemoteUser(), request);

            returnPath = (String) session.getAttribute(Constants.RETURN_PATH);
            if (returnPath != null) {
                session.removeAttribute(Constants.RETURN_PATH);
            } else {
                // admin page
                returnPath = getAdminRootPath();
            }
        } else {
            if (!loginInfo.getRoleSet().isEmpty()) {
                ActivityUtil.login(request.getRemoteUser(), request);
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("Login Failure: " + request.getRemoteUser() + " does not have authenticated roles.");
                }
                // logout
                session.invalidate();
            }
            returnPath = LaRequestUtil.getRequest().getContextPath();
        }

        redirect(returnPath);

        return null;
    }

    private void redirect(final String returnPath) {
        final HttpServletResponse response = LaResponseUtil.getResponse();
        try {
            response.sendRedirect(response.encodeURL(returnPath));
        } catch (final IOException e) {
            throw new FessSystemException("Failed to redirect to " + returnPath, e);
        }
    }

    private String getAdminRootPath() {
        String returnPath = LaRequestUtil.getRequest().getContextPath();
        if (StringUtil.isEmpty(returnPath) || "/".equals(returnPath)) {
            returnPath = "/admin";
        } else {
            returnPath = returnPath + "/admin";
        }
        return returnPath;
    }

    @Execute(validator = false, input = "../index")
    public String logout() {
        final HttpServletRequest request = LaRequestUtil.getRequest();
        ActivityUtil.logout(request.getRemoteUser(), request);

        final HttpSession session = request.getSession();
        session.invalidate();

        return "login?redirect=true";
    }
}