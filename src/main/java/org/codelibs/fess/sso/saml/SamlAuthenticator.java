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
package org.codelibs.fess.sso.saml;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.base.login.FessLoginAssist.LoginCredentialResolver;
import org.codelibs.fess.app.web.base.login.SamlCredential;
import org.codelibs.fess.app.web.base.login.SamlCredential.SamlUser;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.exception.SsoLoginException;
import org.codelibs.fess.exception.SsoMessageException;
import org.codelibs.fess.exception.SsoProcessException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.sso.SsoAuthenticator;
import org.codelibs.fess.sso.SsoResponseType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.saml2.Auth;
import org.codelibs.saml2.core.authn.AuthnRequestParams;
import org.codelibs.saml2.core.logout.LogoutRequestParams;
import org.codelibs.saml2.core.replay.InMemoryReplayCache;
import org.codelibs.saml2.core.replay.ReplayCache;
import org.codelibs.saml2.core.settings.Saml2Settings;
import org.codelibs.saml2.core.settings.SettingsBuilder;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Authenticator for SAML 2.0.
 *
 * <p>This authenticator enables Single Sign-On (SSO) using SAML 2.0 protocol
 * with Identity Providers such as Okta, Azure AD, OneLogin, etc.</p>
 *
 * <h2>Required Configuration</h2>
 * <p>Add the following properties to {@code system.properties}:</p>
 * <pre>
 * # Enable SAML SSO
 * sso.type=saml
 *
 * # Identity Provider settings (obtain from your IdP)
 * saml.idp.entityid=http://www.okta.com/xxxxx
 * saml.idp.single_sign_on_service.url=https://your-domain.okta.com/app/xxxxx/sso/saml
 * saml.idp.x509cert=MIIDqjCCApKgAwIBAgIGAYMwfYAwMA0G...
 * </pre>
 *
 * <h2>Service Provider URL Configuration</h2>
 * <p>By default, the SP URLs use {@code http://localhost:8080} as the base URL.
 * For production or when the IdP is configured with a different URL, you should
 * set one of the following:</p>
 *
 * <h3>Option 1: Set base URL (recommended for simplicity)</h3>
 * <pre>
 * # All SP URLs will be derived from this base URL
 * saml.sp.base.url=https://your-fess-server.example.com
 * </pre>
 *
 * <h3>Option 2: Set individual SP URLs</h3>
 * <pre>
 * # SP Entity ID (Audience URI in IdP)
 * saml.sp.entityid=https://your-fess-server.example.com/sso/metadata
 *
 * # Assertion Consumer Service URL
 * saml.sp.assertion_consumer_service.url=https://your-fess-server.example.com/sso/
 *
 * # Single Logout Service URL
 * saml.sp.single_logout_service.url=https://your-fess-server.example.com/sso/logout
 * </pre>
 *
 * <h2>Complete Configuration Example (Okta)</h2>
 * <pre>
 * sso.type=saml
 *
 * # IdP settings from Okta SAML setup instructions
 * saml.idp.entityid=http://www.okta.com/your-app-id
 * saml.idp.single_sign_on_service.url=https://your-domain.okta.com/app/your-app/your-app-id/sso/saml
 * saml.idp.x509cert=MIIDqjCCApKg... (your IdP certificate)
 *
 * # SP base URL (must match Audience URI configured in Okta)
 * saml.sp.base.url=https://your-fess-server.example.com
 * </pre>
 *
 * <h2>Optional Configuration</h2>
 * <pre>
 * # User attribute mapping
 * saml.attribute.group.name=groups
 * saml.attribute.role.name=roles
 *
 * # Default groups/roles for authenticated users
 * saml.default.groups=user
 * saml.default.roles=user
 * </pre>
 *
 * <h2>Security Settings (Production)</h2>
 * <p>For production environments, consider enabling these security features:</p>
 * <pre>
 * saml.security.authnrequest_signed=true
 * saml.security.want_messages_signed=true
 * saml.security.want_assertions_signed=true
 * </pre>
 *
 * @see <a href="https://fess.codelibs.org/">Fess Documentation</a>
 */
public class SamlAuthenticator implements SsoAuthenticator {

    /**
     * Constructor.
     */
    public SamlAuthenticator() {
        super();
    }

    private static final Logger logger = LogManager.getLogger(SamlAuthenticator.class);

    /**
     * The prefix for SAML properties.
     */
    protected static final String SAML_PREFIX = "saml.";

    /**
     * The session key holding the ID of the AuthnRequest sent to the IdP.
     * The value is compared with the InResponseTo of the SAML response.
     */
    protected static final String SAML_STATE = "SAML_STATE";

    /**
     * The property key for the SAML SP base URL.
     */
    protected static final String SAML_SP_BASE_URL = "saml.sp.base.url";

    private Map<String, Object> defaultSettings;

    /**
     * Cache of processed assertion IDs, used to reject replayed assertions.
     *
     * <p><b>Note:</b> the cache is held in memory by this instance and is not shared in a
     * multi-instance deployment. That is also why SAML SSO needs sticky sessions: the
     * assertion has to reach the instance whose session holds the matching AuthnRequest ID.
     * It is that ID check, not this cache, which rejects an assertion replayed to another
     * instance; the cache catches a repeated POST within a single session.</p>
     */
    private final ReplayCache replayCache = new InMemoryReplayCache();

    /**
     * The security warnings reported the last time they were logged, so that the
     * per-request settings build does not repeat them until the settings change.
     */
    private final AtomicReference<List<String>> loggedSecurityWarnings = new AtomicReference<>();

    /**
     * Initializes the SamlAuthenticator.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing {}", this.getClass().getSimpleName());
        }
        ComponentUtil.getSsoManager().register(this);
        defaultSettings = createDefaultSettings();
    }

    /**
     * Creates the settings that are applied before the {@code saml.} system properties.
     *
     * <p>NOTE: Many security settings are set to false for compatibility.
     * For production use, it is STRONGLY RECOMMENDED to enable security features:
     * {@code saml.security.authnrequest_signed}, {@code saml.security.want_messages_signed}
     * and {@code saml.security.want_assertions_signed}.</p>
     *
     * <p>The SP endpoint URLs are not included here because they are derived from
     * {@code saml.sp.base.url}, which can be changed at runtime. They are built by
     * {@link #getSettings()} on every call.</p>
     *
     * @return The default settings.
     */
    protected Map<String, Object> createDefaultSettings() {
        final Map<String, Object> settings = new HashMap<>();
        settings.put("onelogin.saml2.strict", "true");
        settings.put("onelogin.saml2.debug", "false");
        settings.put("onelogin.saml2.sp.assertion_consumer_service.binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
        settings.put("onelogin.saml2.sp.single_logout_service.binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
        settings.put("onelogin.saml2.sp.nameidformat", "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
        settings.put("onelogin.saml2.idp.single_sign_on_service.binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
        settings.put("onelogin.saml2.idp.single_logout_service.binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
        settings.put("onelogin.saml2.security.nameid_encrypted", "false");
        settings.put("onelogin.saml2.security.authnrequest_signed", "false");
        settings.put("onelogin.saml2.security.logoutrequest_signed", "false");
        settings.put("onelogin.saml2.security.logoutresponse_signed", "false");
        settings.put("onelogin.saml2.security.want_messages_signed", "false");
        settings.put("onelogin.saml2.security.want_assertions_signed", "false");
        settings.put("onelogin.saml2.security.want_assertions_encrypted", "false");
        settings.put("onelogin.saml2.security.want_nameid_encrypted", "false");
        settings.put("onelogin.saml2.security.requested_authncontext", "urn:oasis:names:tc:SAML:2.0:ac:classes:Password");
        settings.put("onelogin.saml2.security.requested_authncontextcomparison", "exact");
        settings.put("onelogin.saml2.security.want_xml_validation", "true");
        settings.put("onelogin.saml2.security.signature_algorithm", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        settings.put("onelogin.saml2.organization.name", "CodeLibs");
        settings.put("onelogin.saml2.organization.displayname", "Fess");
        settings.put("onelogin.saml2.organization.url", "https://fess.codelibs.org/");
        settings.put("onelogin.saml2.contacts.technical.given_name", "Technical Guy");
        settings.put("onelogin.saml2.contacts.technical.email_address", "technical@example.com");
        settings.put("onelogin.saml2.contacts.support.given_name", "Support Guy");
        settings.put("onelogin.saml2.contacts.support.email_address", "support@example.com");
        return settings;
    }

    /**
     * Builds a default URL for SAML endpoints.
     * Uses the configured base URL or defaults to http://localhost:8080 for compatibility
     * with common SAML IdP configurations.
     *
     * @param path the path to append to the base URL
     * @return the complete URL
     */
    protected String buildDefaultUrl(final String path) {
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        String baseUrl = systemProperties.getProperty(SAML_SP_BASE_URL);
        if (StringUtil.isBlank(baseUrl)) {
            baseUrl = "http://localhost:8080";
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + path;
    }

    /**
     * Gets the SAML settings.
     * @return The SAML settings.
     */
    protected Saml2Settings getSettings() {
        final Map<String, Object> params = new HashMap<>(defaultSettings);
        // built on every call because saml.sp.base.url can be changed at runtime
        params.put("onelogin.saml2.sp.entityid", buildDefaultUrl("/sso/metadata"));
        params.put("onelogin.saml2.sp.assertion_consumer_service.url", buildDefaultUrl("/sso/"));
        params.put("onelogin.saml2.sp.single_logout_service.url", buildDefaultUrl("/sso/logout"));
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        systemProperties.entrySet().stream().forEach(e -> {
            final String key = e.getKey().toString();
            if (!key.startsWith(SAML_PREFIX)) {
                return;
            }
            final Object value = e.getValue();
            if (value instanceof final String s && StringUtil.isBlank(s)) {
                // a blank property must not wipe out the default above
                return;
            }
            params.put("onelogin.saml2." + key.substring(SAML_PREFIX.length()), value);
        });
        final Saml2Settings settings = new SettingsBuilder().fromValues(params).build();
        settings.setReplayCache(replayCache);
        logSecurityWarnings(settings);
        return settings;
    }

    /**
     * Logs the security warnings reported for the given settings.
     * The settings are rebuilt on every request, so the warnings are logged
     * again only when they change.
     *
     * @param settings The SAML settings.
     */
    protected void logSecurityWarnings(final Saml2Settings settings) {
        final List<String> warnings = settings.getSecurityWarnings();
        if (!warnings.equals(loggedSecurityWarnings.getAndSet(warnings)) && !warnings.isEmpty()) {
            logger.warn("Insecure SAML settings: {}. See the SAML SSO documentation for the recommended values.",
                    warnings.stream().collect(Collectors.joining(", ")));
        }
    }

    @Override
    public LoginCredential getLoginCredential() {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Logging in with SAML Authenticator");
            }

            final HttpServletResponse response = LaResponseUtil.getResponse();

            final HttpSession session = request.getSession(false);
            if (session != null) {
                final String requestId = (String) session.getAttribute(SAML_STATE);
                if (StringUtil.isNotBlank(requestId)) {
                    session.removeAttribute(SAML_STATE);
                    try {
                        final Auth auth = new Auth(getSettings(), request, response);
                        auth.processResponse(requestId);

                        if (!auth.isAuthenticated()) {
                            final String errors = auth.getErrors().stream().collect(Collectors.joining(", "));
                            if (auth.isDebugActive() && StringUtil.isNotBlank(auth.getLastErrorReason())) {
                                logger.warn("Authentication Failure: {} - Reason: {}", errors, auth.getLastErrorReason());
                            } else {
                                logger.warn("Authentication Failure: {}", errors);
                            }
                            return null;
                        }

                        return createLoginCredential(request, response, auth);
                    } catch (final Exception e) {
                        logger.warn("Authentication failed.", e);
                        return null;
                    }
                }
            }

            try {
                final Auth auth = new Auth(getSettings(), request, response);
                final AuthnRequestParams authnRequestParams = new AuthnRequestParams(false, false, true);
                final String loginUrl = auth.login(null, authnRequestParams, true);
                request.getSession().setAttribute(SAML_STATE, auth.getLastRequestId());
                return new ActionResponseCredential(() -> HtmlResponse.fromRedirectPathAsIs(loginUrl));
            } catch (final Exception e) {
                throw new SsoLoginException("Invalid SAML redirect URL.", e);
            }

        }).orElse(null);
    }

    /**
     * Creates a login credential.
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param auth The SAML authentication.
     * @return The login credential.
     */
    protected LoginCredential createLoginCredential(final HttpServletRequest request, final HttpServletResponse response, final Auth auth) {
        final SamlCredential samlCredential = new SamlCredential(auth);
        if (logger.isDebugEnabled()) {
            logger.debug("SamlCredential: {}", samlCredential);
        }
        return samlCredential;
    }

    @Override
    public void resolveCredential(final LoginCredentialResolver resolver) {
        resolver.resolve(SamlCredential.class, credential -> OptionalEntity.of(credential.getUser()));
    }

    @Override
    public String logout(final FessUserBean user) {
        if (user.getFessUser() instanceof SamlUser) {
            return LaRequestUtil.getOptionalRequest().map(request -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("Logging out with SAML Authenticator");
                }
                final HttpServletResponse response = LaResponseUtil.getResponse();
                final SamlUser samlUser = (SamlUser) user.getFessUser();
                try {
                    final Saml2Settings settings = getSettings();
                    if (settings.getIdpSingleLogoutServiceUrl() == null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("IdP single logout service URL is not configured, skipping SLO for user: {}", samlUser);
                        }
                        return null;
                    }
                    final Auth auth = new Auth(settings, request, response);
                    final LogoutRequestParams logoutRequestParams = new LogoutRequestParams(samlUser.getSessionIndex(), samlUser.getName(),
                            samlUser.getNameIdFormat(), samlUser.getNameidNameQualifier(), samlUser.getNameidSPNameQualifier());
                    return auth.logout(null, logoutRequestParams, true);
                } catch (final Exception e) {
                    logger.warn("Failed to logout from IdP: name={}", samlUser.getName(), e);
                }
                return null;
            }).orElse(null);
        }
        return null;
    }

    @Override
    public ActionResponse getResponse(final SsoResponseType responseType) {
        return switch (responseType) {
        case METADATA -> getMetadataResponse();
        case LOGOUT -> getLogoutResponse();
        default -> null;
        };
    }

    /**
     * Gets the metadata response.
     * @return The metadata response.
     */
    protected ActionResponse getMetadataResponse() {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Accessing metadata with SAML Authenticator");
            }
            final HttpServletResponse response = LaResponseUtil.getResponse();
            try {
                final Auth auth = new Auth(getSettings(), request, response);
                final Saml2Settings settings = auth.getSettings();
                settings.setSPValidationOnly(true);
                final String metadata = settings.getSPMetadata();
                final List<String> errors = Saml2Settings.validateMetadata(metadata);
                if (!errors.isEmpty()) {
                    final String msg = errors.stream().collect(Collectors.joining(", "));
                    throw new SsoMessageException(
                            messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, msg),
                            "Failed to log out.", new SsoProcessException(msg));
                }
                return new StreamResponse("metadata.xml").contentType("application/samlmetadata+xml").stream(out -> {
                    try (final Writer writer = new OutputStreamWriter(out.stream(), Constants.UTF_8_CHARSET)) {
                        writer.write(metadata);
                    }
                });
            } catch (final SsoMessageException e) {
                throw e;
            } catch (final Exception e) {
                throw new SsoMessageException(
                        messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, e.getMessage()),
                        "Failed to process metadata.", e);
            }
        })
                .orElseThrow(() -> new SsoMessageException(
                        messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, "Invalid state."),
                        "Failed to process metadata.", new SsoProcessException("Invalid state.")));
    }

    /**
     * Gets the logout response.
     * @return The logout response.
     */
    protected ActionResponse getLogoutResponse() {
        return LaRequestUtil.getOptionalRequest().<ActionResponse> map(request -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Logging out with SAML Authenticator");
            }
            final HttpServletResponse response = LaResponseUtil.getResponse();
            try {
                final Saml2Settings settings = getSettings();
                if (settings.getIdpSingleLogoutServiceResponseUrl() == null) {
                    final String msg = "IdP single logout service URL is not configured.";
                    throw new SsoMessageException(
                            messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, msg),
                            "Failed to log out.", new SsoProcessException(msg));
                }
                final Auth auth = new Auth(settings, request, response);
                // stay=true keeps java-saml from committing the servlet response itself
                final String redirectUrl = auth.processSLO(false, null, true);
                final List<String> errors = auth.getErrors();
                if (!errors.isEmpty()) {
                    final String msg = errors.stream().collect(Collectors.joining(", "));
                    throw new SsoMessageException(
                            messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, msg),
                            "Failed to log out.", new SsoProcessException(msg));
                }
                if (StringUtil.isNotBlank(redirectUrl)) {
                    // an IdP-initiated LogoutRequest: send our LogoutResponse back to the IdP
                    return HtmlResponse.fromRedirectPathAsIs(redirectUrl);
                }
                throw new SsoMessageException(messages -> messages.addSuccessSsoLogout(UserMessages.GLOBAL_PROPERTY_KEY), "Logged out");
            } catch (final SsoMessageException e) {
                throw e;
            } catch (final Exception e) {
                throw new SsoMessageException(
                        messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, e.getMessage()),
                        "Failed to log out.", e);
            }
        })
                .orElseThrow(() -> new SsoMessageException(
                        messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, "Invalid state."),
                        "Failed to log out.", new SsoProcessException("Invalid state.")));
    }
}
