/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.sso.spnego;

import java.io.File;
import java.util.Enumeration;

import javax.annotation.PostConstruct;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.codelibs.core.io.ResourceUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.base.login.SpnegoCredential;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.exception.SsoLoginException;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.sso.SsoAuthenticator;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.spnego.SpnegoFilterConfig;
import org.codelibs.spnego.SpnegoHttpFilter;
import org.codelibs.spnego.SpnegoHttpFilter.Constants;
import org.codelibs.spnego.SpnegoHttpServletResponse;
import org.codelibs.spnego.SpnegoPrincipal;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.servlet.filter.RequestLoggingFilter;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpnegoAuthenticator implements SsoAuthenticator {
    private static final Logger logger = LoggerFactory.getLogger(SpnegoAuthenticator.class);

    protected org.codelibs.spnego.SpnegoAuthenticator authenticator = null;

    @PostConstruct
    public void init() {
        if ("spnego".equals(ComponentUtil.getFessConfig().getSsoType())) {
            try {
                // set some System properties
                final SpnegoFilterConfig config = SpnegoFilterConfig.getInstance(new SpengoConfig());

                // pre-authenticate
                authenticator = new org.codelibs.spnego.SpnegoAuthenticator(config);
            } catch (final Exception e) {
                throw new FessSystemException("Failed to initialize SPNEGO.", e);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.codelibs.fess.sso.spnego.SsoAuthenticator#getLoginCredential()
     */
    @Override
    public LoginCredential getLoginCredential() {
        return LaRequestUtil
                .getOptionalRequest()
                .map(request -> {
                    final HttpServletResponse response = LaResponseUtil.getResponse();
                    final SpnegoHttpServletResponse spnegoResponse = new SpnegoHttpServletResponse(response);

                    // client/caller principal
                    final SpnegoPrincipal principal;
                    try {
                        principal = authenticator.authenticate(request, spnegoResponse);
                    } catch (final Exception e) {
                        final String msg = "HTTP Authorization Header=" + request.getHeader(Constants.AUTHZ_HEADER);
                        if (logger.isDebugEnabled()) {
                            logger.debug(msg);
                        }
                        throw new SsoLoginException(msg, e);
                    }

                    // context/auth loop not yet complete
                    if (spnegoResponse.isStatusSet()) {
                        return new ActionResponseCredential(() -> {
                            throw new RequestLoggingFilter.RequestClientErrorException("Your request is not authorized.",
                                    "401 Unauthorized", HttpServletResponse.SC_UNAUTHORIZED);
                        });
                    }

                    // assert
                    if (null == principal) {
                        final String msg = "Principal was null.";
                        if (logger.isDebugEnabled()) {
                            logger.debug(msg);
                        }
                        throw new SsoLoginException(msg);
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("principal=" + principal);
                    }

                    final String[] username = principal.getName().split("@", 2);
                    return new SpnegoCredential(username[0]);
                }).orElseGet(() -> null);

    }

    protected class SpengoConfig implements FilterConfig {

        protected FessConfig fessConfig = ComponentUtil.getFessConfig();

        @Override
        public String getFilterName() {
            return SpnegoAuthenticator.class.getName();
        }

        @Override
        public ServletContext getServletContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getInitParameter(final String name) {
            if (SpnegoHttpFilter.Constants.LOGGER_LEVEL.equals(name)) {
                if (StringUtil.isNotBlank(fessConfig.getSpnegoLoggerLevel())) {
                    return fessConfig.getSpnegoLoggerLevel();
                } else if (logger.isDebugEnabled()) {
                    return "3";
                } else if (logger.isInfoEnabled()) {
                    return "5";
                } else if (logger.isWarnEnabled()) {
                    return "6";
                } else if (logger.isErrorEnabled()) {
                    return "7";
                } else {
                    return "0";
                }
            } else if (SpnegoHttpFilter.Constants.LOGIN_CONF.equals(name)) {
                return getResourcePath(fessConfig.getSpnegoLoginConf());
            } else if (SpnegoHttpFilter.Constants.KRB5_CONF.equals(name)) {
                return getResourcePath(fessConfig.getSpnegoKrb5Conf());
            } else if (SpnegoHttpFilter.Constants.CLIENT_MODULE.equals(name)) {
                return fessConfig.getSpnegoLoginClientModule();
            } else if (SpnegoHttpFilter.Constants.SERVER_MODULE.equals(name)) {
                return fessConfig.getSpnegoLoginServerModule();
            } else if (SpnegoHttpFilter.Constants.PREAUTH_USERNAME.equals(name)) {
                return fessConfig.getSpnegoPreauthUsername();
            } else if (SpnegoHttpFilter.Constants.PREAUTH_PASSWORD.equals(name)) {
                return fessConfig.getSpnegoPreauthPassword();
            } else if (SpnegoHttpFilter.Constants.ALLOW_BASIC.equals(name)) {
                return fessConfig.getSpnegoAllowBasic();
            } else if (SpnegoHttpFilter.Constants.ALLOW_UNSEC_BASIC.equals(name)) {
                return fessConfig.getSpnegoAllowUnsecureBasic();
            } else if (SpnegoHttpFilter.Constants.PROMPT_NTLM.equals(name)) {
                return fessConfig.getSpnegoPromptNtlm();
            } else if (SpnegoHttpFilter.Constants.ALLOW_LOCALHOST.equals(name)) {
                return fessConfig.getSpnegoAllowLocalhost();
            } else if (SpnegoHttpFilter.Constants.ALLOW_DELEGATION.equals(name)) {
                return fessConfig.getSpnegoAllowDelegation();
            } else if (SpnegoHttpFilter.Constants.EXCLUDE_DIRS.equals(name)) {
                return fessConfig.getSpnegoExcludeDirs();
            }
            return null;
        }

        protected String getResourcePath(final String path) {
            final File file = ResourceUtil.getResourceAsFileNoException(path);
            if (file != null) {
                return file.getAbsolutePath();
            }
            return null;
        }

        @Override
        public Enumeration<String> getInitParameterNames() {
            throw new UnsupportedOperationException();
        }

    }
}
