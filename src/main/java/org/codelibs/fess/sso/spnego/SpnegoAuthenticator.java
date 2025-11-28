/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;

/**
 * SPNEGO (Security Provider Negotiation Protocol) authenticator implementation.
 *
 * This class provides Single Sign-On (SSO) authentication using the SPNEGO protocol,
 * which is commonly used for Kerberos-based authentication in Windows environments.
 * It handles the negotiation between client and server to establish a secure
 * authentication context without requiring users to explicitly enter credentials.
 *
 * The authenticator supports various configuration options including delegation,
 * basic authentication fallback, and localhost authentication bypass.
 */
public class SpnegoAuthenticator implements SsoAuthenticator {

    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(SpnegoAuthenticator.class);

    /** Configuration key for SPNEGO initialization status. */
    protected static final String SPNEGO_INITIALIZED = "spnego.initialized";

    /** Configuration key for directories to exclude from SPNEGO authentication. */
    protected static final String SPNEGO_EXCLUDE_DIRS = "spnego.exclude.dirs";

    /** Configuration key for enabling delegation in SPNEGO authentication. */
    protected static final String SPNEGO_ALLOW_DELEGATION = "spnego.allow.delegation";

    /** Configuration key for allowing localhost authentication bypass. */
    protected static final String SPNEGO_ALLOW_LOCALHOST = "spnego.allow.localhost";

    /** Configuration key for prompting NTLM authentication. */
    protected static final String SPNEGO_PROMPT_NTLM = "spnego.prompt.ntlm";

    /** Configuration key for allowing unsecure basic authentication. */
    protected static final String SPNEGO_ALLOW_UNSECURE_BASIC = "spnego.allow.unsecure.basic";

    /** Configuration key for allowing basic authentication. */
    protected static final String SPNEGO_ALLOW_BASIC = "spnego.allow.basic";

    /** Configuration key for pre-authentication password. */
    protected static final String SPNEGO_PREAUTH_PASSWORD = "spnego.preauth.password";

    /** Configuration key for pre-authentication username. */
    protected static final String SPNEGO_PREAUTH_USERNAME = "spnego.preauth.username";

    /** Configuration key for login server module name. */
    protected static final String SPNEGO_LOGIN_SERVER_MODULE = "spnego.login.server.module";

    /** Configuration key for login client module name. */
    protected static final String SPNEGO_LOGIN_CLIENT_MODULE = "spnego.login.client.module";

    /** Configuration key for Kerberos configuration file path. */
    protected static final String SPNEGO_KRB5_CONF = "spnego.krb5.conf";

    /** Configuration key for login configuration file path. */
    protected static final String SPNEGO_LOGIN_CONF = "spnego.login.conf";

    /** Configuration key for SPNEGO logger level. */
    protected static final String SPNEGO_LOGGER_LEVEL = "spnego.logger.level";

    /** The underlying SPNEGO authenticator instance. */
    protected org.codelibs.spnego.SpnegoAuthenticator authenticator = null;

    /**
     * Constructs a new SPNEGO authenticator.
     */
    public SpnegoAuthenticator() {
        // do nothing
    }

    /**
     * Initializes the SPNEGO authenticator and registers it with the SSO manager.
     * This method is called automatically after dependency injection is complete.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing {}", this.getClass().getSimpleName());
        }
        ComponentUtil.getSsoManager().register(this);
    }

    /**
     * Gets or creates the SPNEGO authenticator instance.
     *
     * This method implements lazy initialization with synchronization to ensure
     * the authenticator is only created once. It configures the authenticator
     * with the appropriate SPNEGO settings and marks initialization as complete.
     *
     * @return The configured SPNEGO authenticator instance
     * @throws SsoLoginException if SPNEGO initialization fails
     */
    protected synchronized org.codelibs.spnego.SpnegoAuthenticator getAuthenticator() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (authenticator != null && fessConfig.getSystemPropertyAsBoolean(SPNEGO_INITIALIZED, false)) {
            return authenticator;
        }
        try {
            // set some System properties
            final SpnegoFilterConfig config = SpnegoFilterConfig.getInstance(new SpnegoConfig());

            // pre-authenticate
            authenticator = new org.codelibs.spnego.SpnegoAuthenticator(config);

            fessConfig.setSystemPropertyAsBoolean(SPNEGO_INITIALIZED, true);
            fessConfig.storeSystemProperties();
            return authenticator;
        } catch (final Exception e) {
            throw new SsoLoginException("Failed to initialize SPNEGO.", e);
        }
    }

    /**
     * Attempts to obtain login credentials using SPNEGO authentication.
     *
     * This method processes the HTTP request to extract and validate SPNEGO
     * authentication tokens. It handles the SPNEGO handshake process and
     * extracts the user principal from successful authentication.
     *
     * @return The login credential containing the authenticated username,
     *         an ActionResponseCredential for authentication challenges,
     *         or null if no authentication information is available
     * @throws SsoLoginException if SPNEGO authentication fails
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
                    logger.debug("principal={}", principal);
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
                logger.debug("isStatusSet={}", status);
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
                logger.debug("username={}", Arrays.toString(username));
            }
            return new SpnegoCredential(username[0]);
        }).orElse(null);

    }

    /**
     * SPNEGO filter configuration implementation.
     *
     * This inner class provides configuration parameters for the SPNEGO filter,
     * mapping system properties to SPNEGO configuration values. It handles
     * various authentication settings including Kerberos configuration,
     * authentication modules, and security options.
     */
    protected static class SpnegoConfig implements FilterConfig {

        /**
         * Constructs a new SPNEGO filter configuration.
         */
        public SpnegoConfig() {
            // do nothing
        }

        /**
         * Gets the filter name for this SPNEGO configuration.
         *
         * @return The fully qualified class name of SpnegoAuthenticator
         */
        @Override
        public String getFilterName() {
            return SpnegoAuthenticator.class.getName();
        }

        /**
         * Gets the servlet context. This operation is not supported.
         *
         * @return Never returns, always throws UnsupportedOperationException
         * @throws UnsupportedOperationException Always thrown as this operation is not supported
         */
        @Override
        public ServletContext getServletContext() {
            throw new UnsupportedOperationException("getServletContext() is not supported in SpnegoFilterConfig");
        }

        /**
         * Gets the initialization parameter value for the given parameter name.
         *
         * This method maps SPNEGO configuration parameter names to their corresponding
         * values from system properties or default values. It handles various
         * authentication and security settings for SPNEGO.
         *
         * @param name The name of the initialization parameter
         * @return The parameter value, or null if not found
         */
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
                // SECURITY NOTE: Basic authentication is enabled by default for compatibility.
                // For production, consider setting spnego.allow.basic to false.
                return getProperty(SPNEGO_ALLOW_BASIC, "true");
            }
            if (SpnegoHttpFilter.Constants.ALLOW_UNSEC_BASIC.equals(name)) {
                // SECURITY WARNING: Unsecure basic authentication is enabled by default.
                // This sends credentials in Base64 encoding over potentially unencrypted connections.
                // For production, it is STRONGLY RECOMMENDED to set spnego.allow.unsecure.basic to false
                // and use HTTPS or more secure authentication methods.
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

        /**
         * Gets a system property value with a default fallback.
         *
         * @param key The property key to look up
         * @param defaultValue The default value to return if the property is not set
         * @return The property value or the default value
         */
        protected String getProperty(final String key, final String defaultValue) {
            return ComponentUtil.getSystemProperties().getProperty(key, defaultValue);
        }

        /**
         * Resolves a resource path to an absolute file path.
         *
         * @param path The resource path to resolve
         * @return The absolute file path of the resource, or null if not found
         */
        protected String getResourcePath(final String path) {
            final File file = ResourceUtil.getResourceAsFileNoException(path);
            if (file != null) {
                return file.getAbsolutePath();
            }
            return null;
        }

        /**
         * Gets the names of all initialization parameters. This operation is not supported.
         *
         * @return Never returns, always throws UnsupportedOperationException
         * @throws UnsupportedOperationException Always thrown as this operation is not supported
         */
        @Override
        public Enumeration<String> getInitParameterNames() {
            throw new UnsupportedOperationException("getInitParameterNames() is not supported in SpnegoFilterConfig");
        }

    }

    /**
     * Resolves the SPNEGO credential to a user entity.
     *
     * This method handles the resolution of SPNEGO credentials by checking
     * if the user is an admin user or needs to be authenticated through LDAP.
     *
     * @param resolver The credential resolver to use for user lookup
     */
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

    /**
     * Gets the action response for the specified SSO response type.
     *
     * SPNEGO authentication typically doesn't require special response handling
     * for metadata or logout operations, so this method returns null.
     *
     * @param responseType The type of SSO response requested
     * @return Always returns null for SPNEGO authentication
     */
    @Override
    public ActionResponse getResponse(final SsoResponseType responseType) {
        return null;
    }

    /**
     * Performs logout for the specified user.
     *
     * SPNEGO authentication relies on the underlying Kerberos infrastructure
     * for session management, so no specific logout URL is provided.
     *
     * @param user The user to logout
     * @return Always returns null as SPNEGO doesn't provide a logout URL
     */
    @Override
    public String logout(final FessUserBean user) {
        return null;
    }

}
