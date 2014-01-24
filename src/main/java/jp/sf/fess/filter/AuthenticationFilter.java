/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.filter;

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
import javax.servlet.http.HttpServletResponse;

import jp.sf.fess.Constants;
import jp.sf.fess.crypto.FessCipher;
import jp.sf.fess.entity.LoginInfo;
import jp.sf.fess.util.ComponentUtil;

import org.seasar.framework.util.StringUtil;

public class AuthenticationFilter implements Filter {
    private static final String DEFAULT_CIPHER_NAME = "authenticationCipher";

    public List<Pattern> urlPatternList = new ArrayList<Pattern>();

    protected String cipherName;

    protected String loginPath;

    protected String adminRole;

    protected boolean useSecureLogin;

    @Override
    public void destroy() {
        urlPatternList = null;
        cipherName = null;
    }

    @Override
    public void doFilter(final ServletRequest request,
            final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;
        final String uri = req.getRequestURI();
        final FessCipher fessCipher = ComponentUtil.getCipher(cipherName);
        for (final Pattern pattern : urlPatternList) {
            final Matcher matcher = pattern.matcher(uri);
            if (matcher.matches()) {
                if (useSecureLogin) {
                    final String requestURL = req.getRequestURL().toString();
                    if (requestURL.startsWith("http:")) {
                        // redirect
                        res.sendRedirect(requestURL.replaceFirst("^http:",
                                "https:"));
                        return;
                    }
                }

                // require authentication
                boolean redirectLogin = false;
                final Object obj = req.getSession().getAttribute(
                        Constants.LOGIN_INFO);
                if (!(obj instanceof LoginInfo)) {
                    redirectLogin = true;
                } else {
                    final LoginInfo loginInfo = (LoginInfo) obj;
                    if (!loginInfo.isAdministrator()) {
                        redirectLogin = true;
                    }
                }
                if (redirectLogin) {
                    final StringBuilder buf = new StringBuilder(256);
                    buf.append(System.currentTimeMillis());
                    buf.append('|');
                    buf.append(req.getRequestURL());

                    String encoding = request.getCharacterEncoding();
                    if (encoding == null) {
                        encoding = Constants.UTF_8;
                    }

                    final StringBuilder urlBuf = new StringBuilder(1000);
                    urlBuf.append(res.encodeURL(loginPath));
                    urlBuf.append("?returnPath=");
                    urlBuf.append(URLEncoder.encode(
                            fessCipher.encryptoText(buf.toString()), encoding));

                    // redirect
                    res.sendRedirect(urlBuf.toString());
                    return;
                }
            }
        }

        request.setAttribute(Constants.AUTH_CIPHER, fessCipher);

        chain.doFilter(request, response);
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        String value = filterConfig.getInitParameter("urlPatterns");
        if (value != null) {
            final String[] urlPatterns = value.split(",");
            for (final String urlPattern : urlPatterns) {
                // TODO context name 
                urlPatternList.add(Pattern.compile(urlPattern.trim()));
            }
        }

        cipherName = filterConfig.getInitParameter("cipherName");
        if (StringUtil.isBlank(cipherName)) {
            cipherName = DEFAULT_CIPHER_NAME;
        }

        loginPath = filterConfig.getInitParameter("loginPath");
        if (StringUtil.isBlank(loginPath)) {
            loginPath = filterConfig.getServletContext().getContextPath()
                    + "/login/";
        }

        value = filterConfig.getInitParameter("useSecureLogin");
        if (StringUtil.isNotBlank(value)) {
            useSecureLogin = Boolean.parseBoolean(value);
        } else {
            useSecureLogin = false;
        }

    }

}
