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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import org.codelibs.saml2.core.exception.ValidationException;
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
 * anyone who can lure a logged-in user to a crafted URL can end that session. The IdP entity ID
 * is not needed either: {@code Issuer} is optional in the SAML protocol schema and java-saml
 * compares it only when the element is present, so a LogoutRequest that omits it skips the
 * comparison altogether. The impact is a forced logout, not account takeover. This is reported as
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
     * The session key holding the IDs of the AuthnRequests sent to the IdP that have not been
     * answered yet. Each ID is compared with the InResponseTo of the SAML response.
     *
     * <p>The value is a {@code Map<String, Long>} of AuthnRequest ID to the time it was created,
     * not a single ID: a browser with several tabs open sends one AuthnRequest per tab, and a
     * single slot means the second overwrites the first, after which the first assertion to come
     * back consumes the slot, fails the InResponseTo comparison and takes both logins down with
     * it. {@code FessSearchAction} redirects every unauthenticated page hit to {@code /sso/}, so
     * that happens as soon as a session expires with more than one tab open.</p>
     *
     * <p>The key is unchanged from the release that stored a bare {@link String} here, so that an
     * existing session keeps working across an upgrade; see {@link #getRequestIdMap(HttpSession)}
     * for how such a value is migrated.</p>
     */
    protected static final String SAML_STATE = "SAML_STATE";

    /**
     * The property key for the SAML SP base URL.
     */
    protected static final String SAML_SP_BASE_URL = "saml.sp.base.url";

    /**
     * The property key for how long, in seconds, an unanswered AuthnRequest ID stays usable. Only
     * a positive value is honoured; see {@link #getRequestIdTtl()}.
     */
    protected static final String SAML_REQUEST_ID_TTL = "saml.request.id.ttl";

    /**
     * The time-to-live applied to an unanswered AuthnRequest ID when
     * {@link #SAML_REQUEST_ID_TTL} is absent, blank, not a number, or not positive. One hour, the
     * same default {@code EntraIdAuthenticator} uses for an OpenID Connect state, and comfortably
     * longer than any interactive login at an IdP.
     */
    protected static final String DEFAULT_REQUEST_ID_TTL = "3600";

    /**
     * Maximum number of unanswered AuthnRequest IDs kept per session. Every visit to
     * {@code /sso/} without a SAML response stores one, and {@code /sso/} is anonymous and
     * answers GET, so without a cap a page that embeds it as a sub-resource would grow the
     * session attribute without bound. It also bounds the number of candidates
     * {@link #processSamlResponse} tries.
     */
    protected int maxRequestIds = 10;

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
            // never compared with the session user, so anyone can end an authenticated session by
            // luring the user to a crafted URL. Not even the IdP entity ID is needed: Issuer is
            // optional in the protocol schema and java-saml compares it only when it is present.
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
                // counted here rather than inside removeExpiredRequestIds so that the pruning
                // itself keeps the signature it ships with; see logUnmatchedSamlResponseAfterExpiry
                int expiredCount = 0;
                if (session != null) {
                    final Map<String, Long> requestIdMap = getRequestIdMap(session);
                    final int pendingCount = requestIdMap.size();
                    removeExpiredRequestIds(requestIdMap);
                    if (!requestIdMap.isEmpty()) {
                        try {
                            return processSamlResponse(request, response, requestIdMap);
                        } catch (final Exception e) {
                            logger.warn("Authentication failed.", e);
                            return null;
                        }
                    }
                    // pruning is the only thing this thread removed, so an emptied map that was not
                    // empty a moment ago says how many logins ran out of time. A concurrent request
                    // of the same session can consume or evict an entry too, which would make this
                    // an over-count; it only shapes a log line, and the response was unanswerable
                    // by this thread either way.
                    expiredCount = pendingCount;
                }
                // No session at all, or one that holds nothing still answerable: the assertion
                // cannot be tied to an AuthnRequest this server sent, so it is refused rather
                // than answered with another one.
                if (expiredCount > 0) {
                    logUnmatchedSamlResponseAfterExpiry(expiredCount);
                } else if (hasExpiredSession(request)) {
                    logUnmatchedSamlResponseAfterSessionExpiry();
                } else {
                    logUnmatchedSamlResponse(0);
                }
                return null;
            }

            try {
                final Auth auth = new Auth(getSettings(), request, response);
                final AuthnRequestParams authnRequestParams = new AuthnRequestParams(false, false, true);
                final String loginUrl = auth.login(null, authnRequestParams, true);
                storeRequestIdInSession(request.getSession(), auth.getLastRequestId());
                return new ActionResponseCredential(() -> HtmlResponse.fromRedirectPathAsIs(loginUrl));
            } catch (final Exception e) {
                throw new SsoLoginException("Invalid SAML redirect URL.", e);
            }

        }).orElse(null);
    }

    /**
     * Answers a SAML response with the pending AuthnRequest it names.
     *
     * <p>{@code Auth.processResponse} takes exactly one AuthnRequest ID and compares it with the
     * InResponseTo of the response, and java-saml 3.1.1 exposes no supported way of reading that
     * InResponseTo beforehand: {@code SamlResponse} has no getter for it, and the only other
     * route would be to base64-decode and parse the {@code SAMLResponse} parameter here, which
     * would add an XML parsing surface -- and therefore an XXE surface -- to an endpoint that is
     * anonymous by design. So the candidates are tried one at a time instead.</p>
     *
     * <p>That is cheap and, more importantly, safe, because of where the comparison sits in
     * {@code SamlResponse.isValid}: it runs before signature validation and before the assertion
     * ID is registered with the replay cache, so a candidate the response does not name fails
     * fast and leaves no trace behind. A candidate that fails for any other reason has already
     * passed the comparison, so there is nothing left to try and the loop stops there; that is
     * also what keeps the reported error the real one rather than the InResponseTo mismatch of
     * whichever candidate happened to be tried last.</p>
     *
     * <p>Only a response that authenticates consumes its AuthnRequest ID. Consuming it on failure
     * as well would look tidier, but telling "named this ID and was then rejected" apart from
     * "was rejected before the ID was even looked at" means enumerating java-saml's internal
     * ordering of checks, and getting that wrong hands anyone who can reach the assertion
     * consumer service -- a cross-site POST, since SAML requires {@code SameSite=none} -- a way
     * to burn a pending login per request. The TTL and {@link #maxRequestIds} bound the map
     * instead.</p>
     *
     * <p>With {@code saml.strict=false} the library skips the InResponseTo comparison entirely,
     * so the first candidate either authenticates or fails for a real reason and no further
     * candidate is tried. That matches the library's contract: without strict mode there is no
     * InResponseTo binding to match against.</p>
     *
     * @param request The HTTP request carrying the SAML response.
     * @param response The HTTP response.
     * @param requestIdMap The pending AuthnRequest IDs of the session, already pruned of expired
     *            entries. The matching entry is removed from it on success.
     * @return The login credential, or null when the response is not accepted.
     */
    protected LoginCredential processSamlResponse(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, Long> requestIdMap) {
        Auth lastAuth = null;
        for (final String requestId : getCandidateRequestIds(requestIdMap)) {
            final Auth auth = createAuth(request, response);
            auth.processResponse(requestId);
            if (auth.isAuthenticated()) {
                requestIdMap.remove(requestId);
                return createLoginCredential(request, response, auth);
            }
            lastAuth = auth;
            if (!isInResponseToMismatch(auth)) {
                break;
            }
        }
        if (lastAuth == null || isInResponseToMismatch(lastAuth)) {
            logUnmatchedSamlResponse(requestIdMap.size());
            return null;
        }
        final String errors = lastAuth.getErrors().stream().collect(Collectors.joining(", "));
        if (lastAuth.isDebugActive() && StringUtil.isNotBlank(lastAuth.getLastErrorReason())) {
            logger.warn("Authentication Failure: {} - Reason: {}", errors, lastAuth.getLastErrorReason());
        } else {
            logger.warn("Authentication Failure: {}", errors);
        }
        return null;
    }

    /**
     * Creates the {@link Auth} that processes one candidate AuthnRequest ID.
     *
     * <p>A fresh instance per candidate is required, not an optimisation left undone:
     * {@code Auth} accumulates its errors, its authenticated flag and its last validation
     * exception across calls, so reusing one would report the first candidate's InResponseTo
     * mismatch alongside whatever the matching candidate produced.</p>
     *
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @return A new SAML authentication object.
     */
    protected Auth createAuth(final HttpServletRequest request, final HttpServletResponse response) {
        return new Auth(getSettings(), request, response);
    }

    /**
     * Returns the pending AuthnRequest IDs to try, most recently created first.
     *
     * <p>Most recent first because the overwhelmingly common case is a single login, whose ID is
     * the newest one; every earlier entry is a tab or a visit the user abandoned. The list is
     * capped at {@link #maxRequestIds} even though the map is already bounded on write, so that
     * lowering the cap at runtime takes effect on the very next response rather than only once
     * the surplus entries have been evicted.</p>
     *
     * @param requestIdMap The pending AuthnRequest IDs of the session.
     * @return The AuthnRequest IDs to try, in the order they are tried.
     */
    protected List<String> getCandidateRequestIds(final Map<String, Long> requestIdMap) {
        return requestIdMap.entrySet()
                .stream()
                .sorted(Comparator.comparingLong((final Map.Entry<String, Long> e) -> e.getValue()).reversed())
                .limit(maxRequestIds)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Returns whether the given attempt failed only because the response names a different
     * AuthnRequest, which is the one failure that says nothing about the response itself and is
     * therefore worth retrying with the next candidate.
     *
     * @param auth The attempt that did not authenticate.
     * @return true if the response's InResponseTo did not match the candidate that was tried.
     */
    protected boolean isInResponseToMismatch(final Auth auth) {
        return auth.getLastValidationException() instanceof final ValidationException e
                && e.getErrorCode() == ValidationException.WRONG_INRESPONSETO;
    }

    /**
     * Logs the one line reported when a SAML response cannot be tied to an AuthnRequest this
     * server sent.
     *
     * <p>It is a warning and not a redirect back to the IdP on purpose: answering an unmatched
     * assertion with a fresh AuthnRequest sends the browser to an IdP that is already
     * authenticated, which posts the same kind of unmatched assertion straight back, and the
     * loop only ends when the browser gives up.</p>
     *
     * <p>The SameSite guidance below is what this case usually is, but only because the two ways
     * a login can instead have run out of time are reported elsewhere: an ID pruned by
     * {@link #SAML_REQUEST_ID_TTL} by {@link #logUnmatchedSamlResponseAfterExpiry(int)}, and a
     * session the container has already discarded by
     * {@link #logUnmatchedSamlResponseAfterSessionExpiry()}. Without that split every expired
     * login would read as a cookie misconfiguration, since neither leaves anything pending and
     * both therefore reach this line with a pending count of zero as well.</p>
     *
     * @param pendingCount How many pending AuthnRequest IDs the session held, so that a log can
     *            tell a missing session cookie apart from a response that simply matched none of
     *            several live logins.
     */
    protected void logUnmatchedSamlResponse(final int pendingCount) {
        logger.warn("Received a SAML response with no matching AuthnRequest ID in the session ({} pending)."
                + " The assertion consumer service is a cross-site POST, which does not carry a SameSite=Lax cookie;"
                + " see tomcat.sameSiteCookies in tomcat_config.properties."
                + " An IdP-initiated (unsolicited) response is rejected for the same reason.", pendingCount);
    }

    /**
     * Logs the one line reported when a SAML response arrives after every AuthnRequest ID its
     * session held had passed {@link #SAML_REQUEST_ID_TTL}.
     *
     * <p>Kept apart from {@link #logUnmatchedSamlResponse(int)} because that line names
     * {@code tomcat.sameSiteCookies} as the cause, and here the session cookie demonstrably did
     * arrive: the session was found and it did hold pending IDs until this request pruned them.
     * Sending an operator whose cookie settings are already correct off to change them is what
     * the split avoids; the only line that told the two apart was the {@code debug} one in
     * {@link #removeExpiredRequestIds(Map)}, which is below the level a shipped Fess logs at.</p>
     *
     * <p>This is an ordinary event rather than a misconfiguration -- a user who starts a login and
     * finishes it at the IdP later than the TTL allows reaches it, and simply starting the login
     * again succeeds -- so it is worth telling apart from the cases that need an administrator.</p>
     *
     * <p>An extension that overrides {@link #logUnmatchedSamlResponse(int)} to reshape or suppress
     * that warning wants to override this one as well: until this method existed, the expiry case
     * was reported by that one with a pending count of zero.</p>
     *
     * @param expiredCount How many pending AuthnRequest IDs had expired, that is, how many logins
     *            the session still had in flight before the TTL removed them.
     */
    protected void logUnmatchedSamlResponseAfterExpiry(final int expiredCount) {
        logger.warn(
                "Received a SAML response after all {} pending AuthnRequest ID(s) of the session had expired."
                        + " The session cookie did reach this server, so this is not the SameSite case:"
                        + " the login took longer to finish at the IdP than {} allows, and starting it again resolves it.",
                expiredCount, SAML_REQUEST_ID_TTL);
    }

    /**
     * Logs the one line reported when a SAML response arrives with a session id the container no
     * longer recognises, so the session that held the AuthnRequest ID is gone rather than merely
     * empty.
     *
     * <p>This, not {@link #logUnmatchedSamlResponseAfterExpiry(int)}, is what a login left too
     * long at the IdP actually reaches on a stock Fess, because the session runs out first: the
     * AuthnRequest ID is kept for {@link #DEFAULT_REQUEST_ID_TTL} seconds, an hour, while
     * {@code WEB-INF/web.xml} sets no {@code session-timeout} and nothing calls
     * {@code setMaxInactiveInterval}, which leaves the servlet container's own default of thirty
     * minutes. The session is therefore discarded, IDs and all, some half an hour before any of
     * those IDs can expire, and the response comes back to a {@code getSession(false)} that
     * returns null. Without this line that lands on {@link #logUnmatchedSamlResponse(int)} and is
     * reported as a {@code SameSite} cookie problem -- the very misdiagnosis the expiry line was
     * added to prevent, in the one case that occurs in practice.</p>
     *
     * <p>The two are told apart by {@link #hasExpiredSession(HttpServletRequest)}: a browser that
     * is not sending the session cookie sends no session id at all, so a request that does carry
     * one demonstrably kept the cookie and lost only the session behind it.</p>
     *
     * <p>Like the TTL case this is an ordinary event rather than a misconfiguration, and starting
     * the login again resolves it. It names the container's session timeout instead of
     * {@link #SAML_REQUEST_ID_TTL} on purpose, since raising the TTL cannot extend a session that
     * is already the shorter of the two.</p>
     *
     * <p>An extension that overrides {@link #logUnmatchedSamlResponse(int)} to reshape or suppress
     * that warning wants to override this one as well: until this method existed, this case was
     * reported by that one with a pending count of zero.</p>
     */
    protected void logUnmatchedSamlResponseAfterSessionExpiry() {
        logger.warn("Received a SAML response after the session it belongs to had expired."
                + " The browser did return its session cookie, so this is not the SameSite case:"
                + " the session, and with it the AuthnRequest ID it held, was discarded by the"
                + " container's session timeout rather than by {}, so raising that value does not help."
                + " Starting the login again resolves it.", SAML_REQUEST_ID_TTL);
    }

    /**
     * Returns whether the request carries a session id that the container no longer recognises.
     *
     * <p>This is what tells an expired session apart from a browser that is not sending the
     * cookie: a request with no session id at all cannot have lost one. Named after the
     * {@code EntraIdAuthenticator} method that answers the same question, so that the two
     * authenticators stay recognisable to each other.</p>
     *
     * <p>Unlike there, the answer only chooses a log line here. An unmatched SAML response is
     * refused either way, because restarting the login by redirecting to an IdP that is already
     * authenticated would only bring the same unmatched assertion straight back.</p>
     *
     * @param request The HTTP servlet request.
     * @return True if a session id was sent and it is no longer valid.
     */
    protected boolean hasExpiredSession(final HttpServletRequest request) {
        return request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid();
    }

    /**
     * Records the ID of an AuthnRequest that has just been sent to the IdP, pruning the entries
     * that can no longer be answered first.
     *
     * @param session The HTTP session.
     * @param requestId The ID of the AuthnRequest sent to the IdP.
     */
    protected void storeRequestIdInSession(final HttpSession session, final String requestId) {
        final Map<String, Long> requestIdMap = getRequestIdMap(session);
        removeExpiredRequestIds(requestIdMap);
        removeOldestRequestIds(requestIdMap, maxRequestIds - 1);
        if (logger.isDebugEnabled()) {
            logger.debug("Storing AuthnRequest ID in session: {}", requestId);
        }
        requestIdMap.put(requestId, ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
    }

    /**
     * Returns the per-session map of unanswered AuthnRequest IDs, creating it if needed.
     *
     * <p>The map is concurrent, and the create is synchronized on the session, because the tabs
     * that make several logins possible in the first place can also start them at the same
     * moment: a plain {@code HashMap} created twice loses the ID one of them has to match
     * later.</p>
     *
     * <p>Anything else found under the key is replaced rather than cast. A session that predates
     * this change holds a bare {@link String} there, and blindly casting it would end the login
     * with a {@link ClassCastException} rather than a message; that single ID is carried over so
     * a login already in flight across the upgrade can still complete.</p>
     *
     * @param session The HTTP session.
     * @return The AuthnRequest ID map held by the session, keyed by ID and valued with the time
     *         the ID was created.
     */
    protected Map<String, Long> getRequestIdMap(final HttpSession session) {
        synchronized (session) {
            final Object stored = session.getAttribute(SAML_STATE);
            if (stored instanceof ConcurrentHashMap) {
                @SuppressWarnings("unchecked")
                final Map<String, Long> requestIdMap = (Map<String, Long>) stored;
                return requestIdMap;
            }
            final Map<String, Long> concurrentMap = new ConcurrentHashMap<>();
            if (stored instanceof final String requestId && StringUtil.isNotBlank(requestId)) {
                concurrentMap.put(requestId, ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
            }
            session.setAttribute(SAML_STATE, concurrentMap);
            return concurrentMap;
        }
    }

    /**
     * Drops the AuthnRequest IDs that are older than the configured TTL.
     *
     * @param requestIdMap The AuthnRequest ID map to prune.
     */
    protected void removeExpiredRequestIds(final Map<String, Long> requestIdMap) {
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        final long requestIdTtl = getRequestIdTtl();
        requestIdMap.entrySet()
                .stream()
                .filter(e -> (now - e.getValue()) / 1000L > requestIdTtl)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .forEach(requestId -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Removing expired AuthnRequest ID: {}", requestId);
                    }
                    requestIdMap.remove(requestId);
                });
    }

    /**
     * Drops the least recently created AuthnRequest IDs until at most {@code limit} remain. An
     * AuthnRequest that is never answered does not expire before the TTL, so this is what bounds
     * the map for a client that keeps starting logins.
     *
     * @param requestIdMap The AuthnRequest ID map to prune.
     * @param limit The number of AuthnRequest IDs to keep.
     */
    protected void removeOldestRequestIds(final Map<String, Long> requestIdMap, final int limit) {
        if (requestIdMap.size() <= limit) {
            return;
        }
        requestIdMap.entrySet()
                .stream()
                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                .limit((long) requestIdMap.size() - limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .forEach(requestId -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Removing surplus AuthnRequest ID: {}", requestId);
                    }
                    requestIdMap.remove(requestId);
                });
    }

    /**
     * Sets the maximum number of unanswered AuthnRequest IDs kept per session.
     *
     * @param maxRequestIds The maximum number of AuthnRequest IDs.
     */
    public void setMaxRequestIds(final int maxRequestIds) {
        this.maxRequestIds = maxRequestIds;
    }

    /**
     * Gets how long an unanswered AuthnRequest ID stays usable.
     *
     * <p>A value that is not a number is reported once per read rather than thrown, because the
     * alternative is a login that dies with a {@code NumberFormatException} nobody can act
     * on.</p>
     *
     * <p>A value that is not positive is treated the same way, and for the same reason. It parses,
     * so nothing would fail here, but {@link #removeExpiredRequestIds} compares
     * {@code (now - created) / 1000} against it: {@code 0} drops an AuthnRequest ID one second
     * after it was issued and a negative value drops it at once, so no IdP round trip could ever
     * complete and every SAML login in the deployment would fail. {@code 0} is not a far-fetched
     * value to write either -- it reads as "no expiry", and elsewhere in Fess that is what it
     * means -- which is why it falls back rather than being taken literally. The warning names the
     * property and the value, and is worded differently from the one above so that a log says
     * which of the two mistakes was made.</p>
     *
     * @return The TTL in seconds, always positive. {@link #removeExpiredRequestIds} compares it
     *         against an elapsed time that has already been divided by 1000.
     */
    protected long getRequestIdTtl() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(SAML_REQUEST_ID_TTL);
        if (StringUtil.isBlank(value)) {
            return Long.parseLong(DEFAULT_REQUEST_ID_TTL);
        }
        final long requestIdTtl;
        try {
            requestIdTtl = Long.parseLong(value.trim());
        } catch (final NumberFormatException e) {
            logger.warn("Invalid {}: {}. Using {} seconds.", SAML_REQUEST_ID_TTL, value, DEFAULT_REQUEST_ID_TTL);
            return Long.parseLong(DEFAULT_REQUEST_ID_TTL);
        }
        if (requestIdTtl <= 0) {
            logger.warn("{} must be a positive number of seconds: {}. Using {} seconds.", SAML_REQUEST_ID_TTL, value,
                    DEFAULT_REQUEST_ID_TTL);
            return Long.parseLong(DEFAULT_REQUEST_ID_TTL);
        }
        return requestIdTtl;
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
