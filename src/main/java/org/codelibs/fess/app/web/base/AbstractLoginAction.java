/*
 * Copyright 2012 the CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.base;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codelibs.core.crypto.CachedCipher;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.SSCConstants;
import org.codelibs.fess.entity.UserInfo;
import org.codelibs.fess.exception.LoginException;
import org.codelibs.fess.struts.form.AbstractLoginForm;
import org.codelibs.fess.util.ActivityUtil;
import org.lastaflute.web.LastaWebKey;
import org.lastaflute.web.util.LaResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLoginAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(AbstractLoginAction.class);

    private static final long serialVersionUID = 1L;

    @Resource
    protected HttpServletRequest request;

    @Resource
    protected CachedCipher authCipher;

    protected String doIndex(final AbstractLoginForm form) {
        HttpSession session = request.getSession(false);
        // check login session
        final Object obj = session == null ? null : session.getAttribute(SSCConstants.USER_INFO);
        if (obj instanceof UserInfo) {
            redirect(getAuthRootPath());
            return null;
        }

        String params = null;
        if ("forbidden".equals(form.type)) {
            // invalid user
            if (logger.isInfoEnabled()) {
                logger.info("ISSC0001", new Object[] { request.getRemoteUser() });
            }
            if (session != null) {
                session = invalidateSession(session);
            }
            params = "msgs=error.login_error";
        }

        if (session == null) {
            session = request.getSession();
        }

        String path;
        if (StringUtil.isNotBlank(form.returnPath)) {
            final String value = authCipher.decryptoText(form.returnPath);
            final int idx = value.indexOf('|');
            if (idx >= 0) {
                path = value.substring(idx + 1);
                session.setAttribute(SSCConstants.RETURN_PATH, path);
            } else {
                // invalid returnPathName
                session.removeAttribute(SSCConstants.RETURN_PATH);
            }
        } else {
            session.removeAttribute(SSCConstants.RETURN_PATH);
        }

        return getLoginPath(params);
    }

    protected String doLogin(final AbstractLoginForm form) {
        final HttpSession oldSession = request.getSession();

        final HttpSession session = invalidateSession(oldSession);

        session.removeAttribute(LastaWebKey.ACTION_INFO_KEY);

        // create user info
        final UserInfo loginInfo = new UserInfo();
        loginInfo.setUsername(request.getRemoteUser());
        session.setAttribute(SSCConstants.USER_INFO, loginInfo);

        String returnPath = (String) session.getAttribute(SSCConstants.RETURN_PATH);
        if (returnPath != null) {
            session.removeAttribute(SSCConstants.RETURN_PATH);
        } else {
            // admin page
            returnPath = getAuthRootPath();
        }

        ActivityUtil.login(loginInfo.getUsername(), request);

        redirect(returnPath);

        return null;
    }

    private HttpSession invalidateSession(final HttpSession oldSession) {
        final Map<String, Object> sessionObjMap = new HashMap<String, Object>();
        @SuppressWarnings("unchecked")
        final Enumeration<String> e = oldSession.getAttributeNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            sessionObjMap.put(name, oldSession.getAttribute(name));
        }
        oldSession.invalidate();

        final HttpSession session = request.getSession();
        for (final Map.Entry<String, Object> entry : sessionObjMap.entrySet()) {
            session.setAttribute(entry.getKey(), entry.getValue());
        }
        return session;
    }

    protected String doLogout(final AbstractLoginForm form) {
        ActivityUtil.login(request.getRemoteUser(), request);

        final HttpSession session = request.getSession();
        session.invalidate();

        return getLoginPath(null);
    }

    protected String getDefaultPath() {
        return "/index?redirect=true";
    }

    protected String getLoginPath(final String params) {
        final StringBuilder buf = new StringBuilder();
        buf.append("login?");
        if (params != null && params.length() > 0) {
            buf.append(params).append('&');
        }
        buf.append("redirect=true");
        return buf.toString();
    }

    protected String getAuthRootPath() {
        final String contextPath = request.getContextPath();
        if (StringUtil.isEmpty(contextPath) || "/".equals(contextPath)) {
            return "/admin/";
        } else {
            return contextPath + "/admin/";
        }
    }

    protected void redirect(final String returnPath) {
        final HttpServletResponse response = LaResponseUtil.getResponse();
        try {
            response.sendRedirect(response.encodeURL(returnPath));
        } catch (final IOException e) {
            throw new LoginException("ESSC0002", new Object[] { returnPath }, e);
        }
    }
}
