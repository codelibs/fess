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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
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
import org.codelibs.saml2.core.exception.SAMLException;
import org.codelibs.saml2.core.exception.ValidationException;
import org.codelibs.saml2.core.logout.LogoutRequest;
import org.codelibs.saml2.core.logout.LogoutRequestParams;
import org.codelibs.saml2.core.replay.InMemoryReplayCache;
import org.codelibs.saml2.core.replay.ReplayCache;
import org.codelibs.saml2.core.settings.Saml2Settings;
import org.codelibs.saml2.core.settings.SettingsBuilder;
import org.codelibs.saml2.core.util.Util;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;
import org.w3c.dom.Document;

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
 * <p>An assertion that carries the same attribute name on more than one element is refused, and
 * the login fails before any of the mapping above is reached. An IdP that emits one
 * {@code <Attribute>} element per value produces such an assertion -- Keycloak does unless the
 * {@code single} option of its role and group mappers is enabled -- and the repeats are accepted
 * and merged only with:</p>
 * <pre>
 * saml.security.allow_duplicated_attribute_name=true
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
 * accepts a LogoutRequest that nobody authenticated, and reaching it needs no knowledge of the
 * deployment: {@code Issuer} is optional in the SAML protocol schema and java-saml compares it
 * only when the element is present, so a LogoutRequest that omits it is never matched against the
 * IdP entity ID. A session logged in through SAML is protected by
 * {@link #isLogoutRequestForAnotherUser}, which refuses to end a session the LogoutRequest does
 * not name; what remains exposed is a session whose NameID the sender already knows, and any
 * session that did not come from SAML at all, because there is then no NameID to compare. The
 * impact is a forced logout, not account takeover. This is reported once as
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

    /** Upper bound on the length of a sender-supplied NameID embedded in a log message. */
    protected static final int MAX_LOGGED_NAME_ID_LENGTH = 64;

    /**
     * Upper bound on the length of a rejection reason embedded in a log message. Longer than a
     * NameID because the reason is a sentence that quotes what it objected to -- an entity ID, a
     * destination, an audience -- and truncating it to a NameID's length would cut off the part
     * that identifies the problem.
     */
    protected static final int MAX_LOGGED_FAILURE_REASON_LENGTH = 512;

    /**
     * Characters that must not be copied verbatim into a log message. {@code \p{Cntrl}} alone is
     * ASCII-only, so the Unicode break characters a log viewer still renders as a new line are
     * listed explicitly.
     */
    private static final Pattern LOG_UNSAFE_PATTERN = Pattern.compile("[\\p{Cntrl}\\u0085\\u2028\\u2029]");

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
     * The number of unanswered AuthnRequest IDs kept per session when {@code fess_sso++.xml}
     * leaves the cap alone, and the value {@link #setMaxRequestIds} falls back to when the
     * configured one is not positive. Ten abandoned logins in one session is already well past
     * anything a person does by hand.
     */
    protected static final int DEFAULT_MAX_REQUEST_IDS = 10;

    /**
     * Maximum number of unanswered AuthnRequest IDs kept per session. Every visit to
     * {@code /sso/} without a SAML response stores one, and {@code /sso/} is anonymous and
     * answers GET, so without a cap a page that embeds it as a sub-resource would grow the
     * session attribute without bound. It also bounds the number of candidates
     * {@link #processSamlResponse} tries.
     *
     * <p>Always positive when it is set through {@link #setMaxRequestIds}, which is the only path
     * a configured value takes; a subclass that assigns the field directly is on its own.</p>
     */
    protected int maxRequestIds = DEFAULT_MAX_REQUEST_IDS;

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
            // /sso/logout accepts a LogoutRequest that is not signed, so anyone who can lure a
            // logged-in user to a crafted URL can reach it; not even the IdP entity ID is needed,
            // since Issuer is optional in the protocol schema and java-saml compares it only when
            // it is present. isLogoutRequestForAnotherUser() keeps such a request from ending a
            // SAML session it does not name, but a session whose NameID the sender already knows,
            // and a session that did not come from SAML and therefore has no NameID to compare,
            // are still ended by it.
            warnings.add("unsigned_logoutrequest_accepted");
        }
        if (!warnings.equals(loggedSecurityWarnings.getAndSet(warnings)) && !warnings.isEmpty()) {
            logger.warn("Insecure SAML settings: {}. See the SAML SSO documentation for the recommended values.",
                    String.join(", ", warnings));
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
                        } catch (final SAMLException e) {
                            // The assertion consumer service is anonymous, and a SAMLResponse that
                            // cannot be decoded or parsed is refused before the pending
                            // AuthnRequest ID is consumed (see processSamlResponse), so the same
                            // request can be repeated for the whole TTL. A stack trace per attempt
                            // would let an unauthenticated client fill the log; SsoAction already
                            // makes this split for SsoStateException.
                            if (isDuplicatedAttributeName(e)) {
                                logDuplicatedAttributeName();
                            } else {
                                logger.warn("Authentication failed: {}", describeSamlFailure(e));
                            }
                            if (logger.isDebugEnabled()) {
                                logger.debug("Authentication failed.", e);
                            }
                            return null;
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
     * Renders a SAML failure as the single line that stands in for its stack trace at WARN.
     *
     * <p>The cause chain is rendered, not just {@code getMessage()}, because the exception a
     * malformed response produces is often the one that says the least: java-saml wraps a parse
     * failure as {@code XMLParsingException("Failed to load XML data.", cause)}, whose own
     * message names neither what failed nor where. Everything actionable lives further down the
     * chain, so dropping it would trade a noisy log for a useless one.</p>
     *
     * <p>Split out as its own method so that an extension can reshape or shorten this line
     * without having to re-implement the WARN/DEBUG split around it. The full stack trace stays
     * available at DEBUG either way.</p>
     *
     * @param throwable The failure to describe.
     * @return The chain rendered as {@code Type: message} entries joined by {@code " <- "},
     *         stopping at the first cause already seen so that a cyclic chain terminates.
     */
    protected String describeSamlFailure(final Throwable throwable) {
        final Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        final StringBuilder buf = new StringBuilder();
        for (Throwable current = throwable; current != null && seen.add(current); current = current.getCause()) {
            if (buf.length() > 0) {
                buf.append(" <- ");
            }
            buf.append(current.getClass().getSimpleName());
            if (StringUtil.isNotBlank(current.getMessage())) {
                buf.append(": ").append(current.getMessage());
            }
        }
        return buf.toString();
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
        final String errors = String.join(", ", lastAuth.getErrors());
        // The reason is reported whatever saml.debug is set to. getErrors() answers a category --
        // "invalid_response" for a bad signature, an expired assertion, a foreign audience and a
        // replay alike -- so on its own it tells an administrator only that the login failed. The
        // detail used to reach the log anyway because java-saml logged it at warn as well; it now
        // leaves that to whoever calls it, and getLastErrorReason() carries it either way.
        final String reason = lastAuth.getLastErrorReason();
        if (StringUtil.isNotBlank(reason)) {
            // The reason quotes the message it objected to -- an issuer, a destination, an
            // audience -- and this endpoint is anonymous, so the quoted part is a sender's own
            // input and is bounded and stripped of control characters like any other.
            logger.warn("Authentication Failure: {} - Reason: {}", errors, sanitizeForLog(reason, MAX_LOGGED_FAILURE_REASON_LENGTH));
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
        logger.warn("""
                Received a SAML response with no matching AuthnRequest ID in the session ({} pending).\
                 The assertion consumer service is a cross-site POST, which does not carry a SameSite=Lax cookie;\
                 see tomcat.sameSiteCookies in tomcat_config.properties.\
                 An IdP-initiated (unsolicited) response is rejected for the same reason.""", pendingCount);
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
        logger.warn("""
                Received a SAML response after all {} pending AuthnRequest ID(s) of the session had expired.\
                 The session cookie did reach this server, so this is not the SameSite case:\
                 the login took longer to finish at the IdP than {} allows, and starting it again resolves it.""", expiredCount,
                SAML_REQUEST_ID_TTL);
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
        logger.warn("""
                Received a SAML response after the session it belongs to had expired.\
                 The browser did return its session cookie, so this is not the SameSite case:\
                 the session, and with it the AuthnRequest ID it held, was discarded by the\
                 container's session timeout rather than by {}, so raising that value does not help.\
                 Starting the login again resolves it.""", SAML_REQUEST_ID_TTL);
    }

    /**
     * Returns whether the given failure is the library refusing an assertion that carries the same
     * attribute name more than once.
     *
     * <p>Matched on {@link ValidationException#DUPLICATED_ATTRIBUTE_NAME_FOUND} rather than on the
     * message, which is the library's to change.</p>
     *
     * @param e The failure that ended the login.
     * @return True if the assertion repeated an attribute name.
     */
    protected boolean isDuplicatedAttributeName(final SAMLException e) {
        return e instanceof final ValidationException ve && ve.getErrorCode() == ValidationException.DUPLICATED_ATTRIBUTE_NAME_FOUND;
    }

    /**
     * Logs the one line reported when the IdP put the same attribute name on more than one
     * element of the assertion.
     *
     * <p>Told apart from the generic failure line because the generic one -- "Found an Attribute
     * element with duplicated Name" -- names a fact about the XML and leaves an administrator with
     * nothing to change. The setting that accepts the repeats exists, but nothing in Fess mentions
     * it, so without this line the deployment is a dead end.</p>
     *
     * <p>It is worth its own line because of how it is reached rather than how rare it is: this
     * refusal happens in {@code Auth#processResponse} after {@code SamlResponse#isValid} has
     * already returned true, while the attributes are being read, so the signature, the
     * InResponseTo comparison and the replay check all passed. An administrator who is told only
     * that an assertion was refused reasonably suspects the certificate or the clock, none of
     * which is involved.</p>
     *
     * <p>Keycloak is named because it is listed as a supported IdP and produces this on a stock
     * configuration: its role and group mappers emit one {@code <Attribute>} element per value
     * unless their {@code single} option is enabled, and every Keycloak account carries several
     * default realm roles, so every login of every user fails. The failure does not depend on
     * Fess mapping those attributes -- a deployment that sets no
     * {@code saml.attribute.role.name} at all fails identically, because the refusal is in the
     * library, before Fess is given anything to map.</p>
     *
     * <p>The pending AuthnRequest ID is not consumed by this failure, so a login retried after the
     * IdP or Fess is reconfigured still has its ID to match against.</p>
     */
    protected void logDuplicatedAttributeName() {
        logger.warn("""
                The IdP repeated an attribute name in the SAML assertion, which is refused, so the login failed\
                 while the attributes were being read; the assertion itself passed validation and no group or\
                 role was mapped. An IdP that emits one <Attribute> element per value produces this: Keycloak\
                 does unless the "single" option of its role and group mappers is enabled, and every Keycloak\
                 account carries several default roles. Either aggregate each attribute into a single element\
                 at the IdP, or set {}=true in system.properties to accept the repeats and merge their values.""",
                SAML_PREFIX + "security.allow_duplicated_attribute_name");
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
                return (Map<String, Long>) stored;
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
     * <p>A value that is not positive is reported and replaced by
     * {@link #DEFAULT_MAX_REQUEST_IDS} rather than taken literally, for the same reason
     * {@link #getRequestIdTtl()} refuses one. Nothing fails as it is applied, but
     * {@link #getCandidateRequestIds} hands it to {@code limit()}: {@code 0} leaves
     * {@link #processSamlResponse} no candidate to try, so every SAML login in the deployment
     * fails and is reported by {@link #logUnmatchedSamlResponse} as the cookie problem it is not,
     * and a negative value makes {@code limit()} throw an {@link IllegalArgumentException} that
     * reaches the log only as "Authentication failed.". {@code 0} is not a far-fetched value to
     * write either -- it reads as "no limit" -- which is why it falls back rather than being
     * taken at its word. The warning names the property and the value. Refusing it here is
     * enough because this setter is the only path a configured value takes; a subclass that
     * assigns the field directly bypasses the check.</p>
     *
     * @param maxRequestIds The maximum number of AuthnRequest IDs. Only a positive value is
     *                      honoured.
     */
    public void setMaxRequestIds(final int maxRequestIds) {
        if (maxRequestIds <= 0) {
            logger.warn("maxRequestIds must be a positive number: {}. Using {}.", maxRequestIds, DEFAULT_MAX_REQUEST_IDS);
            this.maxRequestIds = DEFAULT_MAX_REQUEST_IDS;
            return;
        }
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
        final long defaultTtl = Long.parseLong(DEFAULT_REQUEST_ID_TTL);
        final String value = ComponentUtil.getFessConfig().getSystemProperty(SAML_REQUEST_ID_TTL);
        if (StringUtil.isBlank(value)) {
            return defaultTtl;
        }
        final long requestIdTtl;
        try {
            requestIdTtl = Long.parseLong(value.trim());
        } catch (final NumberFormatException e) {
            logger.warn("Invalid {}: {}. Using {} seconds.", SAML_REQUEST_ID_TTL, value, DEFAULT_REQUEST_ID_TTL);
            return defaultTtl;
        }
        if (requestIdTtl <= 0) {
            logger.warn("{} must be a positive number of seconds: {}. Using {} seconds.", SAML_REQUEST_ID_TTL, value,
                    DEFAULT_REQUEST_ID_TTL);
            return defaultTtl;
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
        if (user.getFessUser() instanceof final SamlUser samlUser) {
            return LaRequestUtil.getOptionalRequest().map(request -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("Logging out with SAML Authenticator");
                }
                final HttpServletResponse response = LaResponseUtil.getResponse();
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
     * Builds the exception used to report a failed SSO request to the user.
     *
     * @param action The action being performed, used as the log message.
     * @param msg The reason, shown to the user.
     * @param cause The underlying cause. An {@code SsoStateException} marks the failure as caused
     *        by the client, which {@code SsoAction} logs without a stack trace.
     * @return The exception to throw.
     */
    protected SsoMessageException processFailure(final String action, final String msg, final Exception cause) {
        return new SsoMessageException(messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, msg),
                action, cause);
    }

    /**
     * Builds the exception used to report a failed SSO request that has no underlying exception.
     *
     * @param action The action being performed, used as the log message.
     * @param msg The reason, shown to the user.
     * @return The exception to throw.
     */
    protected SsoMessageException processFailure(final String action, final String msg) {
        return processFailure(action, msg, new SsoProcessException(msg));
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
                    final String msg = String.join(", ", settingsErrors);
                    throw processFailure("Failed to process metadata.", msg);
                }
                final String metadata = settings.getSPMetadata();
                final List<String> errors = Saml2Settings.validateMetadata(metadata);
                if (!errors.isEmpty()) {
                    final String msg = String.join(", ", errors);
                    throw processFailure("Failed to process metadata.", msg);
                }
                return new StreamResponse("metadata.xml").contentType("application/samlmetadata+xml").stream(out -> {
                    try (final Writer writer = new OutputStreamWriter(out.stream(), Constants.UTF_8_CHARSET)) {
                        writer.write(metadata);
                    }
                });
            } catch (final SsoMessageException e) {
                throw e;
            } catch (final Exception e) {
                throw processFailure("Failed to process metadata.", e.getMessage(), e);
            }
        }).orElseThrow(() -> processFailure("Failed to process metadata.", "Invalid state."));
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
                    throw processFailure("Failed to log out.", msg, new SsoStateException(msg));
                }
                final Saml2Settings settings = getSettings();
                if (settings.getIdpSingleLogoutServiceResponseUrl() == null) {
                    final String msg = "IdP single logout service URL is not configured.";
                    throw processFailure("Failed to log out.", msg);
                }
                final Auth auth = new Auth(settings, request, response);
                // A LogoutRequest that names somebody else must not take this session with it, but
                // it is still answered with an ordinary LogoutResponse: an error would tell an
                // unauthenticated sender whether it guessed a live session, and would leave a
                // confused-but-legitimate IdP with no way of finishing its own logout.
                final boolean keepLocalSession = isLogoutRequestForAnotherUser(request, settings);
                // stay=true keeps java-saml from committing the servlet response itself
                final String redirectUrl = auth.processSLO(keepLocalSession, null, true);
                final List<String> errors = auth.getErrors();
                if (!errors.isEmpty()) {
                    // java-saml refused the message the sender supplied -- a replayed ID, a bad
                    // signature, a missing NameID, XML that will not parse. The endpoint is
                    // anonymous and, because SAML requires SameSite=none, reachable cross-site, so
                    // this is a rejected request rather than a fault: an SsoStateException gets it
                    // logged without a stack trace, the way getLoginCredential already treats a
                    // callback it did not start.
                    final String msg = String.join(", ", errors);
                    throw processFailure("Failed to log out.", msg, new SsoStateException(msg));
                }
                if (StringUtil.isNotBlank(redirectUrl)) {
                    // an IdP-initiated LogoutRequest: send our LogoutResponse back to the IdP
                    return HtmlResponse.fromRedirectPathAsIs(redirectUrl);
                }
                throw new SsoMessageException(messages -> messages.addSuccessSsoLogout(UserMessages.GLOBAL_PROPERTY_KEY), "Logged out");
            } catch (final SsoMessageException e) {
                throw e;
            } catch (final Exception e) {
                throw processFailure("Failed to log out.", e.getMessage(), e);
            }
        }).orElseThrow(() -> processFailure("Failed to log out.", "Invalid state."));
    }

    /**
     * Returns whether an IdP-initiated LogoutRequest names somebody other than the user this
     * session is logged in as, in which case the session must survive it.
     *
     * <p>{@code /sso/logout} is anonymous and, because SAML requires {@code SameSite=none}, is
     * reachable cross-site with the victim's session cookie attached. With the shipped default
     * {@code saml.security.want_messages_signed=false} java-saml accepts a LogoutRequest that
     * carries no signature, and every other check it makes is conditional on an attribute the
     * sender simply omits -- {@code NotOnOrAfter}, {@code Destination}, and even {@code Issuer},
     * which the protocol schema declares optional and whose absence therefore skips the entity ID
     * comparison as well. The NameID is the one element java-saml insists on, so it is the one
     * thing left worth checking, and comparing it with the session costs an attacker the guess.</p>
     *
     * <p>A LogoutResponse the IdP is answering ({@code SAMLResponse}) is left alone: it is the
     * reply to a LogoutRequest this SP itself sent, so it carries no NameID to compare, and
     * constructing a {@link LogoutRequest} from such a request would silently build a fresh
     * outgoing message rather than parse anything.</p>
     *
     * <p>Anything that is not a clear mismatch keeps the previous behaviour of ending the session:
     * no user logged in, a user who did not come from SAML, a message whose NameID cannot be read
     * at all. Those are properties of this deployment or of a message java-saml is about to reject
     * anyway, not values a sender chooses.</p>
     *
     * <p>A NameID that is read but empty is not one of them. It is the sender's own input, so
     * treating it as "cannot tell" would hand back exactly the bypass this method exists to close:
     * java-saml requires the {@code <saml:NameID>} element to be present but does not require it to
     * carry anything, so {@code <saml:NameID/>} parses, names nobody, and would end any session it
     * reached. It is therefore compared like any other value and, naming nobody, never matches. No
     * IdP is lost by this: one that ends a session says whose.</p>
     *
     * @param request The HTTP request carrying the SAML logout message.
     * @param settings The SAML settings, used to parse the LogoutRequest the way java-saml
     *            itself parses it a moment later.
     * @return true if the session must be kept because the LogoutRequest names another user.
     */
    protected boolean isLogoutRequestForAnotherUser(final HttpServletRequest request, final Saml2Settings settings) {
        if (StringUtil.isBlank(request.getParameter("SAMLRequest"))) {
            return false;
        }
        final String sessionNameId = getSessionSamlNameId();
        if (StringUtil.isBlank(sessionNameId)) {
            return false;
        }
        final String logoutRequestNameId = getLogoutRequestNameId(request, settings);
        if (logoutRequestNameId == null) {
            // the message could not be read at all; java-saml is about to fail on the same bytes
            return false;
        }
        if (isSameNameId(sessionNameId, logoutRequestNameId)) {
            return false;
        }
        logger.warn("The LogoutRequest names '{}' but this session is logged in as '{}', so it is answered without ending the session."
                + " If a legitimate single logout stopped working, compare the NameID the IdP puts in its assertion with the one it puts"
                + " in its LogoutRequest.", sanitizeForLog(logoutRequestNameId), sanitizeForLog(sessionNameId));
        return true;
    }

    /**
     * Bounds a NameID and strips its control characters so that it can be embedded in a log
     * message.
     *
     * <p>The NameID of the LogoutRequest reaches this log before anything has authenticated the
     * message -- that is the whole point of the check that reports it -- so a raw newline in it
     * would let an unauthenticated sender forge log lines. It is XML text content, so it can hold
     * one. {@code SpnegoAuthenticator} bounds the realm it logs for the same reason and in the
     * same way.</p>
     *
     * @param value The NameID to embed in a log message.
     * @return A value safe to embed in a log message.
     */
    protected static String sanitizeForLog(final String value) {
        return sanitizeForLog(value, MAX_LOGGED_NAME_ID_LENGTH);
    }

    /**
     * Bounds a value to {@code maxLength} and strips its control characters so that it can be
     * embedded in a log message.
     *
     * @param value The value to embed in a log message.
     * @param maxLength The number of characters to keep before truncating.
     * @return A value safe to embed in a log message.
     */
    protected static String sanitizeForLog(final String value, final int maxLength) {
        final String bounded = value.length() > maxLength ? value.substring(0, maxLength) + "..." : value;
        return LOG_UNSAFE_PATTERN.matcher(bounded).replaceAll("?");
    }

    /**
     * Returns the NameID this session was logged in with, or null when it did not come from SAML.
     *
     * <p>{@code SamlUser.getName()} is that NameID rather than a display name: it is what
     * {@link #logout(FessUserBean)} passes as the {@code nameId} of the LogoutRequest it sends,
     * so the IdP is expected to name the same value when the logout starts at its end.</p>
     *
     * @return The NameID of the session user, or null when nobody is logged in, when the user did
     *         not authenticate through SAML, or when the session cannot be reached at all.
     */
    protected String getSessionSamlNameId() {
        try {
            final FessUserBean userBean = getSavedUserBean().orElse(null);
            if (userBean != null && userBean.getFessUser() instanceof final SamlUser samlUser) {
                return samlUser.getName();
            }
        } catch (final Exception e) {
            // this endpoint has to keep working for a request that reaches it outside a login
            // scope, so being unable to look at the session means "cannot tell", not "fail"
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to read the session user.", e);
            }
        }
        return null;
    }

    /**
     * Returns the user bean held by the session.
     *
     * <p>Separate from {@link #getSessionSamlNameId()} so that a test can decide who is logged in
     * without standing up a login scope; {@code /api/v2} does the same with its own handlers.</p>
     *
     * @return The user bean of the session, empty when nobody is logged in.
     */
    protected OptionalThing<FessUserBean> getSavedUserBean() {
        return ComponentUtil.getComponent(FessLoginAssist.class).getSavedUserBean();
    }

    /**
     * Returns the NameID carried by the incoming LogoutRequest, or null when it cannot be read.
     *
     * <p>The message is decoded and parsed with java-saml rather than by hand. {@code /sso/logout}
     * is anonymous, so hand-parsing the base64 {@code SAMLRequest} here would put an XML parser --
     * and therefore an XXE surface -- in front of an unauthenticated sender, whereas
     * {@code Util.base64decodedInflated} and {@code Util.loadXML} are the hardened path the
     * library uses on the same bytes a moment later. The arguments mirror
     * {@code LogoutRequest.isValid()} exactly, including the allowed key transport algorithms, so
     * an encrypted NameID is read here under the same restrictions it would be read under a moment
     * later and this adds no decryption the message was not going to get anyway.</p>
     *
     * <p>It parses once. Constructing a {@link LogoutRequest} to reach the decoded XML would parse
     * it a second time, because that constructor loads the document itself and then discards it,
     * and a parse that fails is not free: java-saml logs the failure with its stack trace, so each
     * extra parse of a message that will not parse writes another ~90 lines to the log. This
     * endpoint is anonymous and, because SAML requires {@code SameSite=none}, reachable cross-site
     * with the victim's cookie attached, which is exactly when this method runs -- so the second
     * parse fell on the sessions an attacker targets.</p>
     *
     * <p>Nothing here is allowed to abort the logout. A malformed message, an {@code EncryptedID}
     * with no SP private key configured to open it, an unreadable NameID: all of them mean "cannot
     * tell", which {@link #isLogoutRequestForAnotherUser} turns back into the previous behaviour.
     * Parsing here touches no replay cache -- only {@code isValid()} registers a message ID -- so
     * reading the NameID does not make java-saml reject its own copy as a replay.</p>
     *
     * @param request The HTTP request carrying the LogoutRequest.
     * @param settings The SAML settings.
     * @return The NameID of the LogoutRequest, or null when it cannot be read.
     */
    protected String getLogoutRequestNameId(final HttpServletRequest request, final Saml2Settings settings) {
        final String logoutRequestMessage = request.getParameter("SAMLRequest");
        if (StringUtil.isBlank(logoutRequestMessage)) {
            // a LogoutResponse, or no SAML message at all: there is no LogoutRequest to read
            return null;
        }
        try {
            final Document document = Util.loadXML(Util.base64decodedInflated(logoutRequestMessage));
            if (document == null) {
                // Util.loadXML answers unparsable XML, and anything holding an ENTITY, with null
                return null;
            }
            return LogoutRequest.getNameId(document, settings.getSPkey(), settings.isTrimNameIds(),
                    settings.getAllowedKeyTransportAlgorithms());
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to read the NameID of the LogoutRequest.", e);
            }
            return null;
        }
    }

    /**
     * Returns whether two NameIDs identify the same user.
     *
     * <p>Deliberately more forgiving than {@code equals}, because the damage of the two comparisons
     * is not symmetric: a false match only leaves today's behaviour in place, while a false
     * mismatch breaks a legitimate single logout, which is silent and looks like the session simply
     * refusing to end.</p>
     *
     * <p>Both sides are trimmed. The NameID stored at login and the NameID of the LogoutRequest
     * are read from the text content of two different XML documents, and java-saml trims neither
     * unless {@code saml.parsing.trim_name_ids} is turned on, which Fess leaves off; an IdP that
     * pretty-prints one message and not the other would otherwise look like a different user.</p>
     *
     * <p>The comparison also ignores case. NameIDs that differ only in case are the same account
     * at every IdP that produces them -- an email address or a UPN -- and an IdP that normalises
     * case differently between its assertion and its LogoutRequest is a real deployment, not a
     * hypothetical one. It costs nothing to defend against: a sender who does not know the NameID
     * fails whatever the case, and one who does gains nothing from being allowed to change it.</p>
     *
     * @param sessionNameId The NameID this session was logged in with, not blank.
     * @param logoutRequestNameId The NameID carried by the LogoutRequest, never null but possibly
     *            blank, which the session NameID cannot be and so never matches.
     * @return true if both name the same user.
     */
    protected boolean isSameNameId(final String sessionNameId, final String logoutRequestNameId) {
        return sessionNameId.trim().equalsIgnoreCase(logoutRequestNameId.trim());
    }
}
