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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.base.login.FessLoginAssist.LoginCredentialResolver;
import org.codelibs.fess.app.web.base.login.SpnegoCredential;
import org.codelibs.fess.exception.SsoLoginException;
import org.codelibs.fess.exception.SsoStateException;
import org.codelibs.fess.mylasta.action.FessUserBean;
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
import jakarta.annotation.PreDestroy;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
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

    /** Configuration key for enabling delegation in SPNEGO authentication. */
    protected static final String SPNEGO_ALLOW_DELEGATION = "spnego.allow.delegation";

    /** Configuration key for the comma-separated list of additionally allowed Kerberos realms. */
    protected static final String SPNEGO_ALLOWED_REALMS = "spnego.allowed.realms";

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

    /** Upper bound on the length of a client-supplied value embedded in a log message. */
    protected static final int MAX_LOGGED_REALM_LENGTH = 64;

    /**
     * Characters that must not be copied verbatim into a log message. {@code \p{Cntrl}} alone is
     * ASCII-only, so the Unicode break characters a log viewer still renders as a new line are
     * listed explicitly.
     */
    private static final Pattern LOG_UNSAFE_PATTERN = Pattern.compile("[\\p{Cntrl}\\u0085\\u2028\\u2029]");

    /** The underlying SPNEGO authenticator instance. */
    protected volatile org.codelibs.spnego.SpnegoAuthenticator authenticator = null;

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
     * Releases the SPNEGO server credentials and login context on shutdown.
     */
    @PreDestroy
    public synchronized void destroy() {
        if (authenticator != null) {
            try {
                authenticator.dispose();
            } catch (final Exception e) {
                logger.warn("Failed to dispose SPNEGO authenticator.", e);
            } finally {
                authenticator = null;
            }
        }
    }

    /**
     * Gets or creates the SPNEGO authenticator instance.
     *
     * This method implements lazy initialization with synchronization to ensure
     * the authenticator is only created once per JVM. Because the underlying
     * SpnegoFilterConfig is a JVM-wide singleton, the configuration is cached for
     * the lifetime of the process and a Fess restart is required to apply changes.
     *
     * @return The configured SPNEGO authenticator instance
     * @throws SsoLoginException if SPNEGO initialization fails
     */
    protected org.codelibs.spnego.SpnegoAuthenticator getAuthenticator() {
        final org.codelibs.spnego.SpnegoAuthenticator current = authenticator;
        if (current != null) {
            return current;
        }
        synchronized (this) {
            if (authenticator != null) {
                return authenticator;
            }
            try {
                // NOTE: The underlying SpnegoFilterConfig is a JVM-wide singleton, so the SPNEGO
                // configuration is effectively cached for the lifetime of the process. Changes to the
                // spnego.* settings therefore require a Fess restart to take effect.
                final SpnegoConfig spnegoConfig = new SpnegoConfig();
                final SpnegoFilterConfig config = SpnegoFilterConfig.getInstance(spnegoConfig);
                authenticator = new org.codelibs.spnego.SpnegoAuthenticator(config);
                // Warn only once initialization has succeeded. A failed attempt leaves authenticator
                // null and is retried on the next login, so warning before this point repeats the
                // same message for every attempt, and the settings cannot matter until SPNEGO runs.
                warnInsecureSettings(spnegoConfig);
                return authenticator;
            } catch (final Exception e) {
                throw new SsoLoginException("Failed to initialize SPNEGO.", e);
            }
        }
    }

    /**
     * Logs a warning for security-sensitive settings that are effectively enabled.
     *
     * The coded defaults for these settings are secure, but they only apply when the key is absent
     * from the system properties. An instance that stored the old, permissive values before the
     * defaults were hardened keeps using them silently, so surface them at initialization time.
     *
     * @param config the resolved SPNEGO configuration
     */
    protected void warnInsecureSettings(final SpnegoConfig config) {
        if (Boolean.parseBoolean(config.getInitParameter(Constants.ALLOW_LOCALHOST))) {
            logger.warn("spnego.allow.localhost=true: same-host requests are authenticated as the server OS user "
                    + "without any Kerberos verification. Set it to false unless you fully understand the risk.");
        }
        if (Boolean.parseBoolean(config.getInitParameter(Constants.ALLOW_BASIC))
                && Boolean.parseBoolean(config.getInitParameter(Constants.ALLOW_UNSEC_BASIC))) {
            logger.warn(
                    "spnego.allow.unsecure.basic=true: basic credentials may be sent over plain HTTP. " + "Set it to false and use HTTPS.");
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

            // The Basic path destroys the realm before a principal exists, so it has to be checked
            // here, against the request. Doing it before authenticating also keeps a rejected realm
            // from causing an AS-REQ to a foreign KDC.
            rejectDisallowedBasicRealm(request);

            // client/caller principal
            final SpnegoPrincipal principal;
            try {
                principal = getAuthenticator().authenticate(request, spnegoResponse);
                if (logger.isDebugEnabled()) {
                    logger.debug("principal={}", principal);
                }
            } catch (final Exception e) {
                final String msg = "Failed to process Authorization Header: " + maskAuthzHeader(request.getHeader(Constants.AUTHZ_HEADER));
                if (logger.isDebugEnabled()) {
                    logger.debug(msg);
                }
                // The library reports why the handshake failed; keep it, but not every exception
                // carries a message and "null <msg>" helps nobody diagnose an SSO failure.
                final String detail = e.getMessage();
                final String reason = detail == null ? msg : detail + " " + msg;
                if (e instanceof UnsupportedOperationException) {
                    // The library raises this type only for a header it refuses to even try: a
                    // scheme that is neither Negotiate nor Basic, a Basic header carrying no token,
                    // Basic while basic authentication is not supported, or an NTLM token it cannot
                    // downgrade. The client decides every one of those, and /sso is anonymous, so a
                    // stack trace per attempt would let an unauthenticated client fill the log. It
                    // is not only an abuse path either: basic support is off unless the request is
                    // secure, so a TLS-terminating proxy that leaves isSecure() false sends
                    // ordinary browser traffic down here. An initialization fault cannot arrive as
                    // this type, because getAuthenticator() wraps every one of them in a plain
                    // SsoLoginException, which keeps its stack trace.
                    throw new SsoStateException(reason, e);
                }
                throw new SsoLoginException(reason, e);
            }

            // context/auth loop not yet complete
            final boolean status = spnegoResponse.isStatusSet();
            if (logger.isDebugEnabled()) {
                logger.debug("isStatusSet={}", status);
            }
            if (status) {
                // The library has already written and flushed the 401 with its WWW-Authenticate header,
                // so this exception only unwinds the action. Log it at debug level to keep the normal
                // SPNEGO handshake out of the application log.
                return new ActionResponseCredential(() -> {
                    throw new RequestLoggingFilter.RequestClientErrorException("Your request is not authorized.", "401 Unauthorized",
                            HttpServletResponse.SC_UNAUTHORIZED).asLogging(RequestLoggingFilter.DelicateErrorLoggingLevel.DEBUG);
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

            final String[] username = principal.getName().split("@", 2);
            if (logger.isDebugEnabled()) {
                logger.debug("username={}", Arrays.toString(username));
            }
            if (username.length == 2 && StringUtil.isNotBlank(username[1]) && !isAllowedRealm(username[1])) {
                // A refused realm is a rejected request, not a fault: /sso is anonymous, so a stack
                // trace per attempt would let an unauthenticated client fill the log.
                throw new SsoStateException(realmRejectedMessage(username[1]));
            }
            return new SpnegoCredential(username[0]);
        }).orElse(null);

    }

    /**
     * Rejects a Basic authentication attempt whose user name names a Kerberos realm that is not
     * allowed.
     *
     * The SPNEGO handshake carries the client's real realm in the principal, so it can be validated
     * after the fact. Basic authentication cannot: the library authenticates the name the user
     * typed but then builds the principal from the <em>server</em> realm, and KerberosPrincipal
     * collapses the resulting two-realm name back to that server realm. By the time a principal
     * exists the typed realm is gone, which would leave the allow list unable to govern this path.
     *
     * @param request the current request
     * @throws SsoStateException if the realm named in the header is not allowed
     */
    protected void rejectDisallowedBasicRealm(final HttpServletRequest request) {
        final String realm = getBasicRealm(request.getHeader(Constants.AUTHZ_HEADER));
        if (realm != null && !isAllowedRealm(realm)) {
            // A refused realm is a rejected request, not a fault: /sso is anonymous, so a stack
            // trace per attempt would let an unauthenticated client fill the log.
            throw new SsoStateException(realmRejectedMessage(realm));
        }
    }

    /**
     * Builds the message reported when a Kerberos realm is refused.
     *
     * @param realm the rejected realm
     * @return the message, with the realm sanitized for logging
     */
    protected static String realmRejectedMessage(final String realm) {
        return "Kerberos realm is not allowed: realm=" + sanitizeForLog(realm) + ". Add it to " + SPNEGO_ALLOWED_REALMS
                + " to accept logins from this realm.";
    }

    /**
     * Extracts the Kerberos realm from the user name of a Basic {@code Authorization} header.
     *
     * Only the user name half of the decoded token is inspected. The password is never returned and
     * never logged.
     *
     * @param authzHeader the raw Authorization header value (may be null)
     * @return the realm the client typed, or null when the header is not Basic, cannot be decoded,
     *         or names no realm
     */
    protected static String getBasicRealm(final String authzHeader) {
        if (authzHeader == null) {
            return null;
        }
        // The scheme is separated from the token exactly the way SpnegoProvider#parseAuthHeader
        // separates it: the scheme is matched case-insensitively at offset 0, any run of whitespace
        // after it is skipped, and the trimmed remainder is the token. Diverging from that -- by
        // splitting on a literal space, for instance -- leaves headers the library still
        // authenticates (a tab as the separator, or no separator at all) resolving to no realm
        // here, which silently reopens the spnego.allowed.realms bypass this check exists to close.
        final int schemeLength = Constants.BASIC_HEADER.length();
        if (authzHeader.length() < schemeLength || !authzHeader.regionMatches(true, 0, Constants.BASIC_HEADER, 0, schemeLength)) {
            return null;
        }
        int index = schemeLength;
        while (index < authzHeader.length() && Character.isWhitespace(authzHeader.charAt(index))) {
            index++;
        }
        if (index >= authzHeader.length()) {
            return null;
        }
        final String token = authzHeader.substring(index).trim();
        if (token.isEmpty()) {
            return null;
        }
        final byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(token);
        } catch (final IllegalArgumentException e) {
            // A malformed token is the library's to reject; do not turn it into a realm failure.
            return null;
        }
        final String credentials = new String(decoded, StandardCharsets.UTF_8);
        final int colon = credentials.indexOf(':');
        final String user = colon < 0 ? credentials : credentials.substring(0, colon);
        // The library drops a NetBIOS "DOMAIN\" prefix before authenticating, so mirror it here.
        final String name = user.substring(user.indexOf('\\') + 1);
        // Kerberos reads the realm after the last '@', not the first: KerberosPrincipal collapses
        // "alice@a@PARTNER.EXAMPLE" to name "alice@PARTNER.EXAMPLE" in realm "PARTNER.EXAMPLE", and
        // the library hands the typed name straight to the login module, so PARTNER.EXAMPLE is the
        // realm an AS-REQ would actually reach. Splitting on the first '@' names a realm that
        // exists nowhere and that the allow list can therefore only refuse.
        final int at = name.lastIndexOf('@');
        // A name ending in '@' names an empty realm, which KerberosPrincipal rejects outright, so
        // there is nothing here for the allow list to decide.
        if (at < 0 || at == name.length() - 1) {
            return null;
        }
        return name.substring(at + 1);
    }

    /**
     * Bounds a client-supplied value and strips its control characters.
     *
     * A realm refused on the Basic path comes straight from an unauthenticated request and is
     * written to the application log, so a raw newline would let a client forge log lines.
     *
     * @param value the client-supplied value
     * @return a value safe to embed in a log message
     */
    protected static String sanitizeForLog(final String value) {
        final String bounded = value.length() > MAX_LOGGED_REALM_LENGTH ? value.substring(0, MAX_LOGGED_REALM_LENGTH) + "..." : value;
        return LOG_UNSAFE_PATTERN.matcher(bounded).replaceAll("?");
    }

    /**
     * Masks an Authorization header so that only its authentication scheme remains.
     *
     * The credential part must never reach the log: a Basic token carries the user name and the
     * password, and even a short prefix of it decodes back to readable characters. The scheme that
     * survives is still client-controlled, so it is bounded and sanitized like any other value
     * taken from the request. The scheme ends at the first whitespace, matching how the library and
     * {@link #getBasicRealm(String)} split the header.
     *
     * @param authzHeader the raw Authorization header value (may be null)
     * @return the scheme followed by a mask, or "null" when the header is absent
     */
    protected static String maskAuthzHeader(final String authzHeader) {
        if (authzHeader == null) {
            return "null";
        }
        int index = 0;
        while (index < authzHeader.length() && !Character.isWhitespace(authzHeader.charAt(index))) {
            index++;
        }
        // Nothing before the first whitespace, or no whitespace at all, means no scheme can be
        // named without echoing part of the credential.
        if (index == 0 || index == authzHeader.length()) {
            return "***";
        }
        return sanitizeForLog(authzHeader.substring(0, index)) + " ***";
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
            throw new UnsupportedOperationException("getServletContext() is not supported in SpnegoConfig");
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
                    if (isSupportedLoggerLevel(logLevel)) {
                        return logLevel;
                    }
                    logger.warn("Invalid spnego.logger.level (must be 0-7): {}. Falling back to auto-detection.", logLevel);
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
                // The library maps every unknown level (including "0") to INFO, so "7" (SEVERE) is
                // the quietest setting it actually understands.
                return "7";
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
                // Empty by default so that keytab-based server login is used when the server login
                // module is configured for it (the library only uses a keytab when both preauth
                // username and password are empty).
                return getProperty(SPNEGO_PREAUTH_USERNAME, StringUtil.EMPTY);
            }
            if (SpnegoHttpFilter.Constants.PREAUTH_PASSWORD.equals(name)) {
                return getProperty(SPNEGO_PREAUTH_PASSWORD, StringUtil.EMPTY);
            }
            if (SpnegoHttpFilter.Constants.ALLOW_BASIC.equals(name)) {
                // SECURITY NOTE: Basic authentication is enabled by default for compatibility.
                // For production, consider setting spnego.allow.basic to false.
                return getProperty(SPNEGO_ALLOW_BASIC, "true");
            }
            if (SpnegoHttpFilter.Constants.ALLOW_UNSEC_BASIC.equals(name)) {
                // SECURITY: unsecure basic authentication is disabled by default so that basic
                // credentials are never offered over plain HTTP. When false, basic auth is only
                // offered over HTTPS. Enable only if you fully understand the risk.
                return getProperty(SPNEGO_ALLOW_UNSECURE_BASIC, "false");
            }
            if (SpnegoHttpFilter.Constants.PROMPT_NTLM.equals(name)) {
                return getProperty(SPNEGO_PROMPT_NTLM, "true");
            }
            if (SpnegoHttpFilter.Constants.ALLOW_LOCALHOST.equals(name)) {
                // SECURITY: localhost bypass is disabled by default. When enabled, the spnego library
                // authenticates same-host requests as the server OS user without Kerberos verification,
                // which is unsafe behind a same-host reverse proxy. Opt in explicitly if required.
                return getProperty(SPNEGO_ALLOW_LOCALHOST, "false");
            }
            if (SpnegoHttpFilter.Constants.ALLOW_DELEGATION.equals(name)) {
                return getProperty(SPNEGO_ALLOW_DELEGATION, "false");
            }
            // NOTE: spnego.exclude.dirs is deliberately not mapped. Only SpnegoHttpFilter consumes it,
            // and Fess calls SpnegoAuthenticator#authenticate directly instead of installing that
            // filter, so honoring the key here would advertise an exclusion that never happens.
            return null;
        }

        /**
         * Determines whether a configured logger level is one the SPNEGO library can consume.
         *
         * The library parses the value with {@link Integer#parseInt(String)}, so a value that only
         * looks numeric still fails initialization once it overflows an int. Anything it does not
         * recognize is mapped to INFO, which makes the documented 0-7 range the useful bound.
         *
         * @param value The configured logger level (not blank)
         * @return true if the value can be handed to the library
         */
        protected static boolean isSupportedLoggerLevel(final String value) {
            try {
                final int level = Integer.parseInt(value);
                return level >= 0 && level <= 7;
            } catch (final NumberFormatException e) {
                return false;
            }
        }

        /**
         * Gets a system property value with a default fallback.
         *
         * A blank value is treated as unset. The admin screen writes every spnego.* key on save, so
         * clearing an input field stores an empty string rather than removing the key, and passing
         * that empty string down to the library turns a simple misconfiguration into an opaque
         * initialization failure.
         *
         * @param key The property key to look up
         * @param defaultValue The default value to return if the property is not set or blank
         * @return The property value or the default value
         */
        protected String getProperty(final String key, final String defaultValue) {
            final String value = ComponentUtil.getSystemProperties().getProperty(key);
            if (StringUtil.isBlank(value)) {
                return defaultValue;
            }
            return value;
        }

        /**
         * Resolves a resource path to an absolute file path.
         *
         * @param path The resource path to resolve
         * @return The resolved absolute file path of the resource
         * @throws SsoLoginException if the file cannot be found
         */
        protected String getResourcePath(final String path) {
            final File file = ResourceUtil.getResourceAsFileNoException(path);
            if (file != null) {
                return file.getAbsolutePath();
            }
            throw new SsoLoginException("SPNEGO configuration file not found: " + path);
        }

        /**
         * Gets the names of all initialization parameters. This operation is not supported.
         *
         * @return Never returns, always throws UnsupportedOperationException
         * @throws UnsupportedOperationException Always thrown as this operation is not supported
         */
        @Override
        public Enumeration<String> getInitParameterNames() {
            throw new UnsupportedOperationException("getInitParameterNames() is not supported in SpnegoConfig");
        }

    }

    /**
     * Determines whether the given Kerberos realm is permitted to log in.
     * The server's own realm is always allowed.
     *
     * @param realm the Kerberos realm extracted from the client principal
     * @return true if the realm is allowed
     */
    protected boolean isAllowedRealm(final String realm) {
        return isAllowedRealm(realm, getAuthenticator().getServerRealm());
    }

    /**
     * Determines whether the given Kerberos realm is permitted, considering the server realm and
     * the comma-separated {@code spnego.allowed.realms} system property (for intentional cross-realm
     * trust setups). When neither the server realm nor an allow list can be determined, the realm is
     * accepted to preserve backward compatibility (a warning is logged).
     *
     * @param realm the Kerberos realm extracted from the client principal
     * @param serverRealm the Kerberos realm of the SPNEGO server principal (may be blank)
     * @return true if the realm is allowed
     */
    protected boolean isAllowedRealm(final String realm, final String serverRealm) {
        final Set<String> allowedRealms = new HashSet<>();
        if (StringUtil.isNotBlank(serverRealm)) {
            allowedRealms.add(serverRealm);
        }
        final String configured = ComponentUtil.getSystemProperties().getProperty(SPNEGO_ALLOWED_REALMS, StringUtil.EMPTY);
        if (StringUtil.isNotBlank(configured)) {
            for (final String r : configured.split(",")) {
                if (StringUtil.isNotBlank(r)) {
                    allowedRealms.add(r.trim());
                }
            }
        }
        if (allowedRealms.isEmpty()) {
            logger.warn("No allowed Kerberos realm could be determined; accepting realm={} without validation.", sanitizeForLog(realm));
            return true;
        }
        return allowedRealms.stream().anyMatch(r -> r.equalsIgnoreCase(realm));
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
