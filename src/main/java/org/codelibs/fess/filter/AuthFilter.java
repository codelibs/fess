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
package org.codelibs.fess.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.crypto.CachedCipher;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.entity.UserInfo;
import org.codelibs.fess.util.ActivityUtil;
import org.lastaflute.di.core.SingletonLaContainer;

/**
 * A filter implementation to process a container based authentication.
 *
 * @author shinsuke
 *
 */
public class AuthFilter implements Filter {

    private static final String DEFAULT_LOGIN_PATH = "/login/";

    private static final String HTTPS = "https:";

    private static final String HTTP = "http:";

    public List<Pattern> urlPatternList = new ArrayList<Pattern>();

    protected String cipherName;

    protected String loginPath;

    protected boolean useSecureLogin;

    protected String returnPathName;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        String value = filterConfig.getInitParameter("urlPatterns");
        if (value != null) {
            final String[] urlPatterns = value.split(",");
            for (final String urlPattern : urlPatterns) {
                urlPatternList.add(Pattern.compile(urlPattern.trim()));
            }
        }

        cipherName = filterConfig.getInitParameter("cipherName");
        if (StringUtil.isBlank(cipherName)) {
            cipherName = "authCipher";
        }

        loginPath = filterConfig.getInitParameter("loginPath");

        value = filterConfig.getInitParameter("useSecureLogin");
        if (StringUtil.isNotBlank(value)) {
            useSecureLogin = Boolean.parseBoolean(value);
        } else {
            useSecureLogin = false;
        }

        returnPathName = filterConfig.getInitParameter("returnPathName");
        if (StringUtil.isBlank(returnPathName)) {
            returnPathName = "returnPath";
        }
    }

    @Override
    public void destroy() {
        urlPatternList = null;
        cipherName = null;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
            ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;
        final String uri = req.getRequestURI();
        final CachedCipher cipher = getCachedCipher();
        for (final Pattern pattern : urlPatternList) {
            final Matcher matcher = pattern.matcher(uri);
            if (matcher.matches()) {
                final String requestURL = req.getRequestURL().toString();
                if (useSecureLogin) {
                    if (requestURL.startsWith(HTTP)) {
                        // redirect
                        res.sendRedirect(requestURL.replaceFirst(HTTP, HTTPS));
                        return;
                    }
                }

                // require authentication
                final UserInfo userInfo = getUserInfo(req);
                if (userInfo != null) {
                    ActivityUtil.access(userInfo.getUsername(), req);
                    chain.doFilter(new AuthHttpServletRequest(req, userInfo), response);
                } else {
                    final StringBuilder buf = new StringBuilder(256);
                    buf.append(System.currentTimeMillis());
                    buf.append('|');
                    buf.append(requestURL);

                    String encoding = request.getCharacterEncoding();
                    if (encoding == null) {
                        encoding = CoreLibConstants.UTF_8;
                    }

                    final StringBuilder urlBuf = new StringBuilder(1000);
                    if (StringUtil.isBlank(loginPath)) {
                        final String contextPath = req.getContextPath();
                        if (contextPath != null) {
                            urlBuf.append(contextPath);
                        }
                        urlBuf.append(DEFAULT_LOGIN_PATH);
                    } else {
                        urlBuf.append(res.encodeURL(loginPath));
                    }
                    urlBuf.append('?').append(returnPathName).append('=');
                    urlBuf.append(URLEncoder.encode(cipher.encryptoText(buf.toString()), encoding));

                    // redirect
                    res.sendRedirect(urlBuf.toString());
                }
                return;
            }
        }

        chain.doFilter(request, response);
    }

    protected UserInfo getUserInfo(final HttpServletRequest req) {
        final Object obj = req.getSession().getAttribute(Constants.USER_INFO);
        if (obj instanceof UserInfo) {
            return (UserInfo) obj;
        }
        return null;
    }

    protected CachedCipher getCachedCipher() {
        return SingletonLaContainer.getComponent(cipherName);
    }

    protected static class AuthHttpServletRequest extends HttpServletRequestWrapper {
        protected UserInfo userInfo;

        protected AuthHttpServletRequest(final HttpServletRequest request, final UserInfo userInfo) {
            super(request);
            this.userInfo = userInfo;
        }

        @Override
        public String getRemoteUser() {
            return userInfo.getUsername();
        }

        @Override
        public boolean isUserInRole(final String role) {
            return userInfo.isUserInRole(role);
        }

    }
}
