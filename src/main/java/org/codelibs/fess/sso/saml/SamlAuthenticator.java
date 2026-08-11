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
import java.util.ArrayList;
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
import org.codelibs.fess.exception.SsoStateException;
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
 * <p>{@code saml.security.want_messages_signed} matters in particular once
 * {@code saml.idp.single_logout_service.url} is configured. It defaults to {@code false} because
 * not every IdP signs its LogoutRequest, but while it is {@code false} the single logout service
 * accepts an unsigned LogoutRequest and does not compare its NameID with the session user, so
 * anyone who knows the IdP entity ID can end an authenticated session. This is reported once as
 * {@code unsigned_logoutrequest_accepted} in the insecure-settings warning.</p>
 *
 * <h2>Session Cookie Settings (Required)</h2>
 * <p>The IdP returns the assertion as a cross-site POST to the assertion consumer service.
 * A {@code SameSite=Lax} cookie is not sent on such a request, so the shipped default in
 * {@code tomcat_config.properties} has to be changed for SAML:</p>
 * <pre>
 * tomcat.sameSiteCookies = none
 * </pre>
 * <p>{@code none} is only accepted by browsers on a {@code Secure} cookie, so Fess must be
 * served over HTTPS.</p>
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
     * The security warnings reported the last time they were logged, so that a settings
     * rebuild does not repeat them until the settings change.
     */
    private final AtomicReference<List<String>> loggedSecurityWarnings = new AtomicReference<>();

    /**
     * The most recently built settings, paired with the parameters they were built from.
     *
     * @param params the parameters the settings were built from; values are always {@link String},
     *               never a {@link java.net.URL}, whose {@code equals} would resolve DNS
     * @param settings the settings built from {@code params}
     */
    private record CachedSettings(Map<String, Object> params, Saml2Settings settings) {
    }

    /**
     * The settings built for the parameters currently in effect.
     *
     * <p>Building is not cheap and is not silent: it re-parses the IdP certificate and the SP
     * private key, and {@code SettingsBuilder.build()} logs one line per security warning, so a
     * per-request build defeats the deduplication in {@link #logSecurityWarnings(Saml2Settings)}.</p>
     *
     * <p>An {@link AtomicReference} rather than a plain field because it is required for safe
     * publication: every field of {@link Saml2Settings} is non-final and non-volatile, so another
     * thread could otherwise observe a partially initialized instance.</p>
     */
    private final AtomicReference<CachedSettings> cachedSettings = new AtomicReference<>();

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
     * {@link #buildSettingsParams()}.</p>
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
     * Builds the parameters the SAML settings are built from.
     *
     * <p>The {@code saml.} system properties are applied without filtering out blank values:
     * {@code SettingsBuilder} already treats a blank value as absent, so a present-but-blank
     * property falls through to the library default. That is what lets
     * {@code saml.security.requested_authncontext=} suppress the {@code RequestedAuthnContext}
     * element, which is the documented way of not constraining the authentication method.</p>
     *
     * <p>The three SP endpoint URLs are the exception: they are computed from
     * {@code saml.sp.base.url} rather than defaulted by the library, so they are filled in only
     * when the matching property is absent or blank.</p>
     *
     * <p>Every value in the returned map is a {@link String}. A {@link java.net.URL} must never be
     * put in it, because the map is compared with {@code equals} and {@code URL.equals} resolves
     * DNS.</p>
     *
     * @return The parameters for {@link SettingsBuilder}.
     */
    protected Map<String, Object> buildSettingsParams() {
        final Map<String, Object> params = new HashMap<>(defaultSettings);
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        systemProperties.entrySet().stream().forEach(e -> {
            final String key = e.getKey().toString();
            if (!key.startsWith(SAML_PREFIX)) {
                return;
            }
            params.put("onelogin.saml2." + key.substring(SAML_PREFIX.length()), e.getValue());
        });
        // resolved here, not in createDefaultSettings(), because saml.sp.base.url can be changed
        // at runtime from the admin UI
        putComputedSpUrl(params, "onelogin.saml2.sp.entityid", "/sso/metadata");
        putComputedSpUrl(params, "onelogin.saml2.sp.assertion_consumer_service.url", "/sso/");
        putComputedSpUrl(params, "onelogin.saml2.sp.single_logout_service.url", "/sso/logout");
        return params;
    }

    /**
     * Derives an SP endpoint URL from {@code saml.sp.base.url} unless the corresponding property
     * already carries a usable value.
     *
     * <p>A present-but-blank property must not wipe out the derived URL: the library would treat
     * it as absent and leave the SP endpoint unset, which fails {@code checkSPSettings()}.</p>
     *
     * @param params The parameters being built.
     * @param key The {@code onelogin.saml2.} key to fill in.
     * @param path The path to append to the base URL.
     */
    protected void putComputedSpUrl(final Map<String, Object> params, final String key, final String path) {
        if (params.get(key) instanceof final String s && StringUtil.isNotBlank(s)) {
            return;
        }
        params.put(key, buildDefaultUrl(path));
    }

    /**
     * Gets the SAML settings.
     *
     * <p>The settings are cached and rebuilt only when the parameters change, because building
     * them re-parses the IdP certificate and the SP private key and logs the security warnings
     * again.</p>
     *
     * @return The SAML settings.
     */
    protected Saml2Settings getSettings() {
        final Map<String, Object> params = buildSettingsParams();
        final CachedSettings cached = cachedSettings.get();
        if (cached != null && cached.params().equals(params)) {
            return cached.settings();
        }
        // deliberately get()/set() rather than updateAndGet() or a synchronized block: the
        // mapping function of updateAndGet() may be retried, and building has logging side
        // effects that must not be repeated. Two threads building at once is harmless; both
        // produce equivalent settings and the last write wins.
        final Saml2Settings settings = new SettingsBuilder().fromValues(params).build();
        settings.setReplayCache(replayCache);
        logSecurityWarnings(settings);
        cachedSettings.set(new CachedSettings(params, settings));
        return settings;
    }

    /**
     * Logs the security warnings reported for the given settings.
     * The warnings are logged again only when they change, so that a settings rebuild does not
     * repeat them.
     *
     * @param settings The SAML settings.
     */
    protected void logSecurityWarnings(final Saml2Settings settings) {
        final List<String> warnings = new ArrayList<>(settings.getSecurityWarnings());
        if (settings.getIdpSingleLogoutServiceResponseUrl() != null && !settings.getWantMessagesSigned()) {
            // /sso/logout accepts a LogoutRequest that is not signed, and the NameID it carries is
            // never compared with the session user, so anyone who knows the IdP entity ID can end
            // an authenticated session by luring the user to a crafted URL.
            warnings.add("unsigned_logoutrequest_accepted");
        }
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

            if (containsSamlResponse(request)) {
                final HttpSession session = request.getSession(false);
                final String requestId = session == null ? null : (String) session.getAttribute(SAML_STATE);
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
                // The assertion arrived but the matching AuthnRequest ID is unreachable. Sending
                // another AuthnRequest would come straight back here in the same state, looping
                // forever, so fail once instead.
                logger.warn("Received a SAML response with no matching AuthnRequest ID in the session."
                        + " The assertion consumer service is a cross-site POST, which does not carry a SameSite=Lax cookie;"
                        + " see tomcat.sameSiteCookies in tomcat_config.properties."
                        + " An IdP-initiated (unsolicited) response is rejected for the same reason.");
                return null;
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
     * Returns whether the request carries a SAML response, which is what the IdP posts to the
     * assertion consumer service.
     *
     * <p>The session is deliberately not consulted here: it is the session cookie that goes
     * missing when the browser refuses to send it on the cross-site POST, and a callback that is
     * mistaken for a fresh visit is redirected back to the IdP forever.</p>
     *
     * <p>Only solicited responses are accepted. Fess binds every response to the ID of the
     * AuthnRequest it sent, so an unsolicited (IdP-initiated) response has nothing to match
     * against and is rejected rather than answered with a fresh AuthnRequest.</p>
     *
     * @param request The HTTP request.
     * @return true if the request carries a SAML response.
     */
    protected boolean containsSamlResponse(final HttpServletRequest request) {
        return StringUtil.isNotBlank(request.getParameter("SAMLResponse"));
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
     *
     * <p>The SP metadata is what the IdP is registered from, so it has to be obtainable before
     * any {@code saml.idp.*} property exists. Only the SP settings are therefore validated;
     * constructing an {@link Auth} here would validate the IdP settings in its constructor and
     * fail while they are still empty.</p>
     *
     * @return The metadata response.
     */
    protected ActionResponse getMetadataResponse() {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Accessing metadata with SAML Authenticator");
            }
            try {
                final Saml2Settings settings = getSettings();
                // checkSettings() with spValidationOnly is by definition checkSPSettings(), and
                // mutating the shared settings instance to say so would leak into every other
                // caller
                final List<String> settingsErrors = settings.checkSPSettings();
                if (!settingsErrors.isEmpty()) {
                    final String msg = settingsErrors.stream().collect(Collectors.joining(", "));
                    throw new SsoMessageException(
                            messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, msg),
                            "Failed to process metadata.", new SsoProcessException(msg));
                }
                final String metadata = settings.getSPMetadata();
                final List<String> errors = Saml2Settings.validateMetadata(metadata);
                if (!errors.isEmpty()) {
                    final String msg = errors.stream().collect(Collectors.joining(", "));
                    throw new SsoMessageException(
                            messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, msg),
                            "Failed to process metadata.", new SsoProcessException(msg));
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
     * Returns whether the request carries a SAML logout message, which is what the IdP sends to
     * the single logout service.
     *
     * <p>{@code /sso/logout} is reachable without authentication, so it also receives plain visits
     * that carry no SAML message at all. Those are not logout callbacks and must be rejected
     * before {@code Auth.processSLO} sees them, because it answers them with an exception whose
     * text describes the supported bindings rather than anything the visitor can act on.</p>
     *
     * @param request The HTTP request.
     * @return true if the request carries a SAML logout request or response.
     */
    protected boolean containsSamlLogoutMessage(final HttpServletRequest request) {
        return StringUtil.isNotBlank(request.getParameter("SAMLRequest")) || StringUtil.isNotBlank(request.getParameter("SAMLResponse"));
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
                if (!containsSamlLogoutMessage(request)) {
                    // an anonymous request that is not a logout callback: rejected, not a fault,
                    // so it carries an SsoStateException and is logged without a stack trace.
                    // Checked before the configuration guard below, because otherwise a
                    // deployment that leaves single logout unconfigured would answer the very
                    // same anonymous visit with a stack trace per request.
                    final String msg = "This endpoint expects a SAML logout message from the IdP.";
                    throw new SsoMessageException(
                            messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, msg),
                            "Failed to log out.", new SsoStateException(msg));
                }
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
