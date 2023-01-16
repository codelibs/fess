/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
import java.util.Arrays;
import java.util.Enumeration;

import javax.annotation.PostConstruct;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.base.login.FessLoginAssist.LoginCredentialResolver;
import org.codelibs.fess.app.web.base.login.SpnegoCredential;
import org.codelibs.fess.exception.SsoLoginException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.sso.SsoAuthenticator;
import org.codelibs.fess.sso.SsoResponseType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.spnego.SpnegoFilterConfig;
import org.codelibs.spnego.SpnegoHttpFilter;
import org.codelibs.spnego.SpnegoHttpFilter.Constants;
import org.codelibs.spnego.SpnegoHttpServletResponse;
import org.codelibs.spnego.SpnegoPrincipal;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.servlet.filter.RequestLoggingFilter;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;

public class SpnegoAuthenticator implements SsoAuthenticator {

    private static final Logger logger = LogManager.getLogger(SpnegoAuthenticator.class);

    protected static final String SPNEGO_INITIALIZED = "spnego.initialized";
    protected static final String SPNEGO_EXCLUDE_DIRS = "spnego.exclude.dirs";
    protected static final String SPNEGO_ALLOW_DELEGATION = "spnego.allow.delegation";
    protected static final String SPNEGO_ALLOW_LOCALHOST = "spnego.allow.localhost";
    protected static final String SPNEGO_PROMPT_NTLM = "spnego.prompt.ntlm";
    protected static final String SPNEGO_ALLOW_UNSECURE_BASIC = "spnego.allow.unsecure.basic";
    protected static final String SPNEGO_ALLOW_BASIC = "spnego.allow.basic";
    protected static final String SPNEGO_PREAUTH_PASSWORD = "spnego.preauth.password";
    protected static final String SPNEGO_PREAUTH_USERNAME = "spnego.preauth.username";
    protected static final String SPNEGO_LOGIN_SERVER_MODULE = "spnego.login.server.module";
    protected static final String SPNEGO_LOGIN_CLIENT_MODULE = "spnego.login.client.module";
    protected static final String SPNEGO_KRB5_CONF = "spnego.krb5.conf";
    protected static final String SPNEGO_LOGIN_CONF = "spnego.login.conf";
    protected static final String SPNEGO_LOGGER_LEVEL = "spnego.logger.level";

    protected org.codelibs.spnego.SpnegoAuthenticator authenticator = null;

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        ComponentUtil.getSsoManager().register(this);
    }

    protected synchronized org.codelibs.spnego.SpnegoAuthenticator getAuthenticator() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (authenticator != null && fessConfig.getSystemPropertyAsBoolean(SPNEGO_INITIALIZED, false)) {
            return authenticator;
        }
        try {
            // set some System properties
            final SpnegoFilterConfig config = SpnegoFilterConfig.getInstance(new SpengoConfig());

            // pre-authenticate
            authenticator = new org.codelibs.spnego.SpnegoAuthenticator(config);

            fessConfig.setSystemPropertyAsBoolean(SPNEGO_INITIALIZED, true);
            fessConfig.storeSystemProperties();
            return authenticator;
        } catch (final Exception e) {
            throw new SsoLoginException("Failed to initialize SPNEGO.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.codelibs.fess.sso.spnego.SsoAuthenticator#getLoginCredential()
     */
    @Override
    public LoginCredential getLoginCredential() {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Logging in with SPNEGO Authenticator");
            }
            final HttpServletResponse response = LaResponseUtil.getResponse();
            final SpnegoHttpServletResponse spnegoResponse = new SpnegoHttpServletResponse(response);

            // client/caller principal
            final SpnegoPrincipal principal;
            try {
                principal = getAuthenticator().authenticate(request, spnegoResponse);
                if (logger.isDebugEnabled()) {
                    logger.debug("principal: {}", principal);
                }
            } catch (final Exception e) {
                final String msg = "Failed to process Authorization Header: " + request.getHeader(Constants.AUTHZ_HEADER);
                if (logger.isDebugEnabled()) {
                    logger.debug(msg);
                }
                throw new SsoLoginException(e.getMessage() + " " + msg, e);
            }

            // context/auth loop not yet complete
            final boolean status = spnegoResponse.isStatusSet();
            if (logger.isDebugEnabled()) {
                logger.debug("isStatusSet: {}", status);
            }
            if (status) {
                return new ActionResponseCredential(() -> {
                    throw new RequestLoggingFilter.RequestClientErrorException("Your request is not authorized.", "401 Unauthorized",
                            HttpServletResponse.SC_UNAUTHORIZED);
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
                logger.debug("principal={}", principal);
            }

            final String[] username = principal.getName().split("@", 2);
            if (logger.isDebugEnabled()) {
                logger.debug("username: {}", Arrays.toString(username));
            }
            return new SpnegoCredential(username[0]);
        }).orElseGet(() -> null);

    }

    protected static class SpengoConfig implements FilterConfig {

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
                final String logLevel = getProperty(SPNEGO_LOGGER_LEVEL, StringUtil.EMPTY);
                if (StringUtil.isNotBlank(logLevel)) {
                    return logLevel;
                }
                if (logger.isDebugEnabled()) {
                    return "3";
                }
                if (logger.isInfoEnabled()) {
                    return "5";
                }
                if (logger.isWarnEnabled()) {
                    return "6";
                }
                if (logger.isErrorEnabled()) {
                    return "7";
                }
                return "0";
            }
            if (SpnegoHttpFilter.Constants.LOGIN_CONF.equals(name)) {
                return getResourcePath(getProperty(SPNEGO_LOGIN_CONF, "auth_login.conf"));
            }
            if (SpnegoHttpFilter.Constants.KRB5_CONF.equals(name)) {
                return getResourcePath(getProperty(SPNEGO_KRB5_CONF, "krb5.conf"));
            }
            if (SpnegoHttpFilter.Constants.CLIENT_MODULE.equals(name)) {
                return getProperty(SPNEGO_LOGIN_CLIENT_MODULE, "spnego-client");
            }
            if (SpnegoHttpFilter.Constants.SERVER_MODULE.equals(name)) {
                return getProperty(SPNEGO_LOGIN_SERVER_MODULE, "spnego-server");
            }
            if (SpnegoHttpFilter.Constants.PREAUTH_USERNAME.equals(name)) {
                return getProperty(SPNEGO_PREAUTH_USERNAME, "username");
            }
            if (SpnegoHttpFilter.Constants.PREAUTH_PASSWORD.equals(name)) {
                return getProperty(SPNEGO_PREAUTH_PASSWORD, "password");
            }
            if (SpnegoHttpFilter.Constants.ALLOW_BASIC.equals(name)) {
                return getProperty(SPNEGO_ALLOW_BASIC, "true");
            }
            if (SpnegoHttpFilter.Constants.ALLOW_UNSEC_BASIC.equals(name)) {
                return getProperty(SPNEGO_ALLOW_UNSECURE_BASIC, "true");
            }
            if (SpnegoHttpFilter.Constants.PROMPT_NTLM.equals(name)) {
                return getProperty(SPNEGO_PROMPT_NTLM, "true");
            }
            if (SpnegoHttpFilter.Constants.ALLOW_LOCALHOST.equals(name)) {
                return getProperty(SPNEGO_ALLOW_LOCALHOST, "true");
            }
            if (SpnegoHttpFilter.Constants.ALLOW_DELEGATION.equals(name)) {
                return getProperty(SPNEGO_ALLOW_DELEGATION, "false");
            }
            if (SpnegoHttpFilter.Constants.EXCLUDE_DIRS.equals(name)) {
                return getProperty(SPNEGO_EXCLUDE_DIRS, StringUtil.EMPTY);
            }
            return null;
        }

        protected String getProperty(final String key, final String defaultValue) {
            return ComponentUtil.getSystemProperties().getProperty(key, defaultValue);
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

    @Override
    public void resolveCredential(final LoginCredentialResolver resolver) {
        resolver.resolve(SpnegoCredential.class, credential -> {
            final String username = credential.getUserId();
            if (!ComponentUtil.getFessConfig().isAdminUser(username)) {
                return ComponentUtil.getLdapManager().login(username);
            }
            return OptionalEntity.empty();
        });
    }

    @Override
    public ActionResponse getResponse(final SsoResponseType responseType) {
        return null;
    }

    @Override
    public String logout(final FessUserBean user) {
        return null;
    }

}
