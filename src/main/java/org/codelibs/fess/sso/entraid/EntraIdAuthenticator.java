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
package org.codelibs.fess.sso.entraid;

import static org.codelibs.core.stream.StreamUtil.split;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.curl.Curl;
import org.codelibs.curl.CurlException;
import org.codelibs.curl.CurlRequest;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.base.login.EntraIdCredential;
import org.codelibs.fess.app.web.base.login.EntraIdCredential.EntraIdUser;
import org.codelibs.fess.app.web.base.login.FessLoginAssist.LoginCredentialResolver;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.exception.SsoLoginException;
import org.codelibs.fess.exception.SsoStateException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.sso.SsoAuthenticator;
import org.codelibs.fess.sso.SsoResponseType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.opensearch.runner.net.OpenSearchCurl;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.login.exception.LoginFailureException;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.util.LaRequestUtil;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.RefreshTokenParameters;
import com.microsoft.aad.msal4j.SilentParameters;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Microsoft Entra ID SSO authenticator implementation.
 * Handles OAuth2/OpenID Connect authentication flow with Entra ID.
 */
public class EntraIdAuthenticator implements SsoAuthenticator {

    private static final Logger logger = LogManager.getLogger(EntraIdAuthenticator.class);

    /**
     * Default constructor for EntraIdAuthenticator.
     */
    public EntraIdAuthenticator() {
        // Default constructor
    }

    // New configuration keys for Entra ID
    /** Configuration key for Entra ID state time-to-live. */
    protected static final String ENTRAID_STATE_TTL = "entraid.state.ttl";

    /** Default state time-to-live in seconds. */
    protected static final String DEFAULT_STATE_TTL = "3600";

    /**
     * Authority used when none is configured. Also used when the configured value is present but
     * blank: an empty authority would make {@link #getAuthUrl} build a scheme-less, and therefore
     * relative, redirect that sends the browser back into Fess instead of to Microsoft.
     */
    protected static final String DEFAULT_AUTHORITY = "https://login.microsoftonline.com/";

    /** Configuration key for Entra ID authority URL. */
    protected static final String ENTRAID_AUTHORITY = "entraid.authority";

    /** Configuration key for Entra ID tenant ID. */
    protected static final String ENTRAID_TENANT = "entraid.tenant";

    /** Configuration key for Entra ID client secret. */
    protected static final String ENTRAID_CLIENT_SECRET = "entraid.client.secret";

    /** Configuration key for Entra ID client ID. */
    protected static final String ENTRAID_CLIENT_ID = "entraid.client.id";

    /** Configuration key for Entra ID reply URL. */
    protected static final String ENTRAID_REPLY_URL = "entraid.reply.url";

    /** Configuration key for the OAuth2 response mode of the authorization request. */
    protected static final String ENTRAID_RESPONSE_MODE = "entraid.response.mode";

    /** Configuration key for Entra ID default groups. */
    protected static final String ENTRAID_DEFAULT_GROUPS = "entraid.default.groups";

    /** Configuration key for Entra ID default roles. */
    protected static final String ENTRAID_DEFAULT_ROLES = "entraid.default.roles";

    /**
     * Configuration key deciding what a login does when Microsoft Graph does not answer with a
     * membership list and the user has none yet. See {@link #isRequireMembership()}. There is no
     * {@code aad.require.membership} counterpart: the key is new, so no legacy value can exist.
     */
    protected static final String ENTRAID_REQUIRE_MEMBERSHIP = "entraid.require.membership";

    // Legacy configuration keys for backward compatibility (Azure AD)
    /** Legacy configuration key for Azure AD state time-to-live. */
    protected static final String AAD_STATE_TTL = "aad.state.ttl";

    /** Legacy configuration key for Azure AD authority URL. */
    protected static final String AAD_AUTHORITY = "aad.authority";

    /** Legacy configuration key for Azure AD tenant ID. */
    protected static final String AAD_TENANT = "aad.tenant";

    /** Legacy configuration key for Azure AD client secret. */
    protected static final String AAD_CLIENT_SECRET = "aad.client.secret";

    /** Legacy configuration key for Azure AD client ID. */
    protected static final String AAD_CLIENT_ID = "aad.client.id";

    /** Legacy configuration key for Azure AD reply URL. */
    protected static final String AAD_REPLY_URL = "aad.reply.url";

    /** Legacy configuration key for the OAuth2 response mode. */
    protected static final String AAD_RESPONSE_MODE = "aad.response.mode";

    /** Response mode that returns the authorization code in the callback query string. */
    protected static final String RESPONSE_MODE_QUERY = "query";

    /** Response mode that returns the authorization code in a form POST to the callback. */
    protected static final String RESPONSE_MODE_FORM_POST = "form_post";

    /** Legacy configuration key for Azure AD default groups. */
    protected static final String AAD_DEFAULT_GROUPS = "aad.default.groups";

    /** Legacy configuration key for Azure AD default roles. */
    protected static final String AAD_DEFAULT_ROLES = "aad.default.roles";

    /** Session attribute key for storing Entra ID states. */
    protected static final String STATES = "entraidStates";

    /** OAuth2 state parameter name. */
    protected static final String STATE = "state";

    /** OAuth2 error parameter name. */
    protected static final String ERROR = "error";

    /** OAuth2 error description parameter name. */
    protected static final String ERROR_DESCRIPTION = "error_description";

    /** OAuth2 error URI parameter name. */
    protected static final String ERROR_URI = "error_uri";

    /** OpenID Connect ID token parameter name. */
    protected static final String ID_TOKEN = "id_token";

    /** OAuth2 authorization code parameter name. */
    protected static final String CODE = "code";

    /** Microsoft Graph error code returned when the application lacks the required permission. */
    protected static final String PERMISSION_DENIED_ERROR_CODE = "Authorization_RequestDenied";

    /** HTTP status Microsoft Graph answers with while it is throttling the caller. */
    protected static final int HTTP_TOO_MANY_REQUESTS = 429;

    /** HTTP status Microsoft Graph answers with while it is temporarily unavailable. */
    protected static final int HTTP_SERVICE_UNAVAILABLE = 503;

    /** Backoff applied when a throttled response carries no usable {@code Retry-After} header. */
    protected static final long DEFAULT_GRAPH_THROTTLE_SECONDS = 60L;

    /**
     * Upper bound on the backoff. {@code Retry-After} is whatever the service says it is, and an
     * unreasonably large value would leave nested groups unresolved for the rest of the day.
     */
    protected static final long MAX_GRAPH_THROTTLE_SECONDS = 60L * 60L;

    /**
     * Scopes requested at the v2.0 authorization endpoint. msal4j already prepends
     * {@code openid profile offline_access} to the token request (its
     * {@code OAuthAuthorizationGrant.COMMON_SCOPES}), so naming them here as well means consent is
     * asked for the same set the token exchange goes on to request, rather than relying on the
     * app registration's static permissions happening to include them.
     */
    protected static final String V2_SCOPES = "openid profile offline_access https://graph.microsoft.com/.default";

    /**
     * Response parameters whose values are credentials. Their values are truncated before being
     * written to a debug log; every other parameter is logged verbatim so that a failed login can
     * still be diagnosed from {@code state}, {@code error} and {@code error_description}.
     */
    protected static final Set<String> SENSITIVE_PARAMS = Set.of(CODE, ID_TOKEN, "access_token", "refresh_token", "client_secret");

    /** Number of leading characters kept when a secret is written to a debug log. */
    protected static final int MASK_PREFIX_LENGTH = 8;

    /**
     * Truncates a secret so it can be correlated across log lines without being usable.
     * Null and empty values are passed through, because several call sites log a field that
     * the identity provider may not have sent at all.
     *
     * @param value The value to mask.
     * @return The masked value.
     */
    protected static String maskSecret(final String value) {
        if (StringUtil.isEmpty(value)) {
            return value;
        }
        return value.substring(0, Math.min(MASK_PREFIX_LENGTH, value.length())) + "***";
    }

    /**
     * Drops the query string from a URL before it is written to a debug log. The query string can
     * carry the authorization code, and every parameter it holds is already logged separately via
     * {@link #maskParams(Map)}.
     *
     * @param url The URL to strip.
     * @return The URL without its query string.
     */
    protected static String maskQueryString(final String url) {
        if (url == null) {
            return null;
        }
        final int index = url.indexOf('?');
        return index < 0 ? url : url.substring(0, index);
    }

    /**
     * Returns a copy of the response parameters with credential values masked, for logging.
     * The key set is preserved so the log still shows which artifacts the identity provider sent.
     *
     * @param params The response parameters.
     * @return A new map safe to write to a log.
     */
    protected static Map<String, List<String>> maskParams(final Map<String, List<String>> params) {
        final Map<String, List<String>> maskedParams = new LinkedHashMap<>();
        params.forEach((key, values) -> {
            if (key != null && SENSITIVE_PARAMS.contains(key.toLowerCase(Locale.ENGLISH))) {
                maskedParams.put(key, values.stream().map(EntraIdAuthenticator::maskSecret).collect(Collectors.toList()));
            } else {
                maskedParams.put(key, values);
            }
        });
        return maskedParams;
    }

    /** Timeout for token acquisition in milliseconds. */
    protected long acquisitionTimeout = 30 * 1000L;

    /** Cache for storing group information to reduce API calls. */
    protected Cache<String, Pair<String[], String[]>> groupCache;

    /** Group cache expiry time in seconds. */
    protected long groupCacheExpiry = 10 * 60L;

    /**
     * Maximum number of groups kept in {@link #groupCache}. The cache is keyed by group id, so a
     * tenant with many groups would otherwise grow it without bound until every entry expired.
     */
    protected int maxGroupCacheSize = 10000;

    /** Maximum depth for processing nested groups to prevent infinite loops. */
    protected int maxGroupDepth = 10;

    /**
     * Connection timeout for Microsoft Graph requests in milliseconds. curl4j leaves this unset,
     * which means an unbounded wait, and the direct-membership lookup runs on the login thread.
     */
    protected int graphConnectTimeout = 10 * 1000;

    /** Read timeout for Microsoft Graph requests in milliseconds. See {@link #graphConnectTimeout}. */
    protected int graphReadTimeout = 30 * 1000;

    /**
     * Maximum number of unfinished authorization attempts kept per session. Each redirect to the
     * authorization endpoint stores one; without a cap, a client that keeps starting logins
     * without finishing one grows the session attribute without bound.
     */
    protected int maxStates = 10;

    /**
     * Point in time, as epoch milliseconds, until which Microsoft Graph asked us to stop calling
     * it. Zero means it never did. Read on the login path, written from whichever thread was
     * throttled, hence volatile.
     */
    protected volatile long graphThrottledUntil;

    /**
     * Shared MSAL4J client application together with the configuration it was built from.
     *
     * <p>A single reference is what makes the pair consistent: with the application and its key in
     * two separate fields, a reader interleaved between the two writes pairs the old application
     * with the new key and hands back the stale one indefinitely.
     */
    protected volatile ClientApplicationHolder clientApplicationHolder;

    /**
     * Initializes the Entra ID authenticator.
     * Registers this authenticator with the SSO manager and sets up group cache.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing {}", this.getClass().getSimpleName());
        }
        ComponentUtil.getSsoManager().register(this);
        groupCache = createGroupCache();
    }

    /**
     * Builds the parent group cache. Both bounds matter: the expiry keeps a group whose
     * membership changed from being served forever, and the size keeps a large tenant from
     * holding every group it ever resolved until the expiry comes round.
     *
     * @return The cache.
     */
    protected Cache<String, Pair<String[], String[]>> createGroupCache() {
        return CacheBuilder.newBuilder().maximumSize(maxGroupCacheSize).expireAfterWrite(groupCacheExpiry, TimeUnit.SECONDS).build();
    }

    @Override
    public LoginCredential getLoginCredential() {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Logging in with Entra ID Authenticator");
            }
            final HttpSession session = request.getSession(false);
            if (containsAuthenticationData(request)) {
                if (session != null) {
                    try {
                        return processAuthenticationData(request);
                    } catch (final SsoLoginException e) {
                        throw e;
                    } catch (final Exception e) {
                        // Wrapped rather than returned as null so SsoAction logs it at WARN and
                        // shows the SSO error message, the same as it already does for the other
                        // authenticators. Swallowing it here left a failed login invisible unless
                        // DEBUG logging happened to be on.
                        throw new SsoLoginException("Failed to process a login request on Entra ID.", e);
                    }
                }
                if (!hasExpiredSession(request)) {
                    // No session, and no session id came back either: the browser is not returning
                    // the cookie at all (form_post with SameSite=Lax or Strict, or cookies off).
                    // Redirecting would send the user straight back here in the same state, so
                    // this is where the loop has to stop. Returning null makes SsoAction show the
                    // SSO error message and fall back to the local login form.
                    logger.warn("Received an Entra ID authentication response without a session."
                            + " The session cookie was not sent back with the callback request."
                            + " See tomcat.sameSiteCookies in tomcat_config.properties.");
                    return null;
                }
                // The browser did return a session id and the container rejected it, so cookies
                // demonstrably work and the session merely expired while the user was at
                // Microsoft. Start the login again rather than dropping them on a login form that
                // has no SSO link. This cannot loop: getAuthUrl creates a fresh session, so the
                // next callback either finds it or arrives with no session id at all, which is
                // the branch above.
                if (logger.isDebugEnabled()) {
                    logger.debug("The session of an Entra ID callback had expired. Restarting the login.");
                }
            }

            validateConfiguration();
            return new ActionResponseCredential(() -> HtmlResponse.fromRedirectPathAsIs(getAuthUrl(request)));
        }).orElse(null);
    }

    /**
     * Returns whether the request carries a session id that the container no longer recognises.
     *
     * <p>This is what tells an expired session apart from a browser that is not sending the
     * cookie: a request with no session id at all cannot have lost one.
     *
     * @param request The HTTP servlet request.
     * @return True if a session id was sent and it is no longer valid.
     */
    protected boolean hasExpiredSession(final HttpServletRequest request) {
        return request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid();
    }

    /**
     * Fails the login when Entra ID cannot possibly answer for want of configuration.
     *
     * <p>Without this an unconfigured server redirects to
     * {@code https://login.microsoftonline.com//oauth2/v2.0/authorize?...&amp;client_id=} and logs
     * nothing, so the administrator sees only a Microsoft error page. It is thrown from here
     * rather than from the {@link ActionResponseCredential} supplier because {@code SsoAction}
     * executes that supplier outside the block that catches {@link SsoLoginException}, and it is
     * not checked in {@code init()} because {@code fess_sso++.xml} registers every authenticator
     * unconditionally -- including when {@code sso.type} selects another one.
     */
    protected void validateConfiguration() {
        final List<String> missing = new ArrayList<>();
        if (StringUtil.isBlank(getTenant())) {
            missing.add(ENTRAID_TENANT);
        }
        if (StringUtil.isBlank(getClientId())) {
            missing.add(ENTRAID_CLIENT_ID);
        }
        if (StringUtil.isBlank(getClientSecret())) {
            missing.add(ENTRAID_CLIENT_SECRET);
        }
        if (!missing.isEmpty()) {
            throw new SsoLoginException("Entra ID is not configured. The following settings are empty: " + String.join(", ", missing));
        }
    }

    /**
     * Generates the Entra ID authorization URL for the authentication request.
     * @param request The HTTP servlet request.
     * @return The authorization URL to redirect the user to.
     */
    protected String getAuthUrl(final HttpServletRequest request) {
        // UUID.randomUUID is backed by SecureRandom and varies in 122 bits. The state is the
        // only thing standing between a login and a forged callback (RFC 6749 section 10.12), and
        // org.codelibs.core.net.UuidUtil, which this used to call, keeps the first 16 hex
        // characters constant for the life of the JVM and varies under 32 bits per call.
        final String state = UUID.randomUUID().toString();
        final String nonce = UUID.randomUUID().toString();
        storeStateInSession(request.getSession(), state, nonce);

        final String responseMode = getResponseMode();
        final String authUrl = getAuthority() + getTenant() + "/oauth2/v2.0/authorize?response_type=code&scope="
                + URLEncoder.encode(V2_SCOPES, Constants.UTF_8_CHARSET) + "&response_mode=" + responseMode + "&redirect_uri="
                + URLEncoder.encode(getReplyUrl(request), Constants.UTF_8_CHARSET) + "&client_id=" + getClientId() + "&state=" + state
                + "&nonce=" + nonce;
        if (logger.isDebugEnabled()) {
            logger.debug("redirect to: {}", authUrl);
        }
        return authUrl;

    }

    /**
     * Stores state and nonce information in the HTTP session.
     * @param session The HTTP session.
     * @param state The OAuth2 state parameter.
     * @param nonce The OpenID Connect nonce parameter.
     */
    protected void storeStateInSession(final HttpSession session, final String state, final String nonce) {
        final Map<String, StateData> stateMap = getStateMap(session);
        removeExpiredStates(stateMap);
        removeOldestStates(stateMap, maxStates - 1);
        final StateData stateData = new StateData(nonce, ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
        if (logger.isDebugEnabled()) {
            logger.debug("Storing state in session: {}", stateData);
        }
        stateMap.put(state, stateData);
    }

    /**
     * Returns the per-session map of pending authorization attempts, creating it if needed.
     * The map is concurrent, and the create is synchronized on the session, because a user can
     * have several login attempts in flight at once -- a plain HashMap created twice loses the
     * state one of them has to validate later.
     *
     * @param session The HTTP session.
     * @return The state map held by the session.
     */
    protected Map<String, StateData> getStateMap(final HttpSession session) {
        synchronized (session) {
            @SuppressWarnings("unchecked")
            final Map<String, StateData> stateMap = (Map<String, StateData>) session.getAttribute(STATES);
            if (stateMap instanceof ConcurrentHashMap) {
                return stateMap;
            }
            // Either absent, or a plain HashMap left by a session that predates this change.
            final Map<String, StateData> concurrentMap = new ConcurrentHashMap<>();
            if (stateMap != null) {
                concurrentMap.putAll(stateMap);
            }
            session.setAttribute(STATES, concurrentMap);
            return concurrentMap;
        }
    }

    /**
     * Drops states that are older than the configured TTL.
     *
     * @param stateMap The state map to prune.
     */
    protected void removeExpiredStates(final Map<String, StateData> stateMap) {
        final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
        final long stateTtl = getStateTtl();
        stateMap.entrySet()
                .stream()
                .filter(e -> (now - e.getValue().getExpiration()) / 1000L > stateTtl)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .forEach(s -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Removing old state: {}", s);
                    }
                    stateMap.remove(s);
                });
    }

    /**
     * Drops the least recently created states until at most {@code limit} remain. Unfinished
     * attempts never expire on their own before the TTL, so this is what bounds the map for a
     * client that keeps starting logins.
     *
     * @param stateMap The state map to prune.
     * @param limit The number of states to keep.
     */
    protected void removeOldestStates(final Map<String, StateData> stateMap, final int limit) {
        if (stateMap.size() <= limit) {
            return;
        }
        stateMap.entrySet()
                .stream()
                .sorted(Comparator.comparingLong(e -> e.getValue().getExpiration()))
                .limit((long) stateMap.size() - limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .forEach(s -> {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Removing surplus state: {}", s);
                    }
                    stateMap.remove(s);
                });
    }

    /**
     * Sets the maximum number of pending authorization attempts kept per session.
     * @param maxStates The maximum number of states.
     */
    public void setMaxStates(final int maxStates) {
        this.maxStates = maxStates;
    }

    /**
     * Processes authentication data from the OAuth2 callback.
     * @param request The HTTP servlet request containing authentication data.
     * @return The login credential or null if processing fails.
     */
    protected LoginCredential processAuthenticationData(final HttpServletRequest request) {
        final StringBuilder urlBuf = new StringBuilder(request.getRequestURL());
        final String queryStr = request.getQueryString();
        if (queryStr != null) {
            urlBuf.append('?').append(queryStr);
        }

        final Map<String, List<String>> params = new HashMap<>();
        for (final Map.Entry<String, String[]> e : request.getParameterMap().entrySet()) {
            if (e.getValue().length > 0) {
                params.put(e.getKey(), Arrays.asList(e.getValue()));
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("process authentication: url: {}, params: {}", request.getRequestURL(), maskParams(params));
        }

        // validate that state in response equals to state in request
        final StateData stateData = validateState(request.getSession(), params.containsKey(STATE) ? params.get(STATE).get(0) : null);
        if (logger.isDebugEnabled()) {
            logger.debug("Loading state: {}", stateData);
        }

        final AuthenticationResponse authResponse = parseAuthenticationResponse(urlBuf.toString(), params);
        if (authResponse instanceof final AuthenticationSuccessResponse oidcResponse) {
            validateAuthRespMatchesCodeFlow(oidcResponse);
            final IAuthenticationResult authData = getAccessToken(oidcResponse.getAuthorizationCode(), getReplyUrl(request));
            validateNonce(stateData, authData);

            return new EntraIdCredential(authData);
        }
        final AuthenticationErrorResponse oidcResponse = (AuthenticationErrorResponse) authResponse;
        throw new SsoLoginException(String.format("Request for auth code failed: %s - %s", oidcResponse.getErrorObject().getCode(),
                oidcResponse.getErrorObject().getDescription()));
    }

    /**
     * Parses the authentication response from Entra ID.
     * @param url The response URL.
     * @param params The response parameters.
     * @return The parsed authentication response.
     */
    protected AuthenticationResponse parseAuthenticationResponse(final String url, final Map<String, List<String>> params) {
        if (logger.isDebugEnabled()) {
            logger.debug("Parse: {} : {}", maskQueryString(url), maskParams(params));
        }
        try {
            return AuthenticationResponseParser.parse(new URI(url), params);
        } catch (final Exception e) {
            throw new SsoLoginException("Failed to parse an authentication response.", e);
        }
    }

    /**
     * Validates the nonce in the authentication result.
     * @param stateData The stored state data containing the expected nonce.
     * @param authData The authentication result containing the actual nonce.
     */
    protected void validateNonce(final StateData stateData, final IAuthenticationResult authData) {
        final String idToken = authData.idToken();
        if (logger.isDebugEnabled()) {
            logger.debug("idToken={}", maskSecret(idToken));
        }
        try {
            final JWTClaimsSet claimsSet = JWTParser.parse(idToken).getJWTClaimsSet();
            if (claimsSet == null) {
                throw new SsoStateException("could not validate nonce");
            }

            final String nonce = (String) claimsSet.getClaim("nonce");
            if (logger.isDebugEnabled()) {
                logger.debug("nonce={}", nonce);
            }
            if (StringUtils.isEmpty(nonce) || !nonce.equals(stateData.getNonce())) {
                throw new SsoStateException("could not validate nonce");
            }
        } catch (final SsoLoginException e) {
            throw e;
        } catch (final Exception e) {
            // Not an SsoStateException: this is only reachable once the authorization code was
            // redeemed, so an unparsable or unreadable ID token is a fault worth a stack trace,
            // not a callback someone sent us.
            throw new SsoLoginException("could not validate nonce", e);
        }
    }

    /**
     * Returns the shared MSAL4J client application.
     *
     * <p>Each {@link ConfidentialClientApplication} owns its own in-memory token cache, and
     * {@code acquireTokenSilently} throws {@code NO_TOKEN_IN_CACHE} on a miss. Building one per
     * call therefore made silent refresh impossible: the tokens acquired at login went into an
     * instance that was thrown away immediately afterwards. One instance per authenticator keeps
     * them reachable. MSAL4J documents the application as thread safe and meant to be reused.
     *
     * <p>The instance is rebuilt when the client id, secret or tenant changes, because all three
     * are editable from the admin screen while Fess is running.
     *
     * @return The client application.
     */
    protected ConfidentialClientApplication getClientApplication() {
        final ClientApplicationHolder current = clientApplicationHolder;
        final String key = buildClientApplicationKey();
        if (current != null && current.getKey().equals(key)) {
            return current.getApplication();
        }
        synchronized (this) {
            // The four settings are read again, and the key recomputed, inside the monitor. They
            // are four independent reads of mutable configuration, so a key built on the fast path
            // can mix values from before and after an admin save; publishing an application built
            // from that mixture would leave it in place indefinitely.
            final String currentKey = buildClientApplicationKey();
            final ClientApplicationHolder holder = clientApplicationHolder;
            if (holder != null && holder.getKey().equals(currentKey)) {
                return holder.getApplication();
            }
            final String clientId = getClientId();
            final String clientSecret = getClientSecret();
            final String authority = getAuthority() + getTenant() + "/";
            if (logger.isDebugEnabled()) {
                logger.debug("Building a client application for authority={}", authority);
            }
            try {
                final ConfidentialClientApplication application = ConfidentialClientApplication
                        .builder(clientId, com.microsoft.aad.msal4j.ClientCredentialFactory.createFromSecret(clientSecret))
                        .authority(authority)
                        .build();
                clientApplicationHolder =
                        new ClientApplicationHolder(buildClientApplicationKey(clientId, clientSecret, authority), application);
                return application;
            } catch (final Exception e) {
                throw new SsoLoginException("Failed to build an Entra ID client application.", e);
            }
        }
    }

    /**
     * Reads the configuration the client application depends on and reduces it to a key.
     *
     * @return The key.
     */
    protected String buildClientApplicationKey() {
        return buildClientApplicationKey(getClientId(), getClientSecret(), getAuthority() + getTenant() + "/");
    }

    /**
     * Reduces the configuration the client application depends on to a key.
     *
     * @param clientId The client id.
     * @param clientSecret The client secret.
     * @param authority The authority URL.
     * @return The key.
     */
    protected String buildClientApplicationKey(final String clientId, final String clientSecret, final String authority) {
        // The secret is reduced to a hash so the key can never carry it into a log or a heap dump
        // label; a collision would only mean the application is not rebuilt after a secret change.
        return authority + '\n' + clientId + '\n' + clientSecret.hashCode();
    }

    /**
     * A client application and the configuration key it was built from, published together so a
     * reader can never see one without the other.
     */
    protected static final class ClientApplicationHolder {
        private final String key;
        private final ConfidentialClientApplication application;

        /**
         * Constructs a holder.
         *
         * @param key The configuration key.
         * @param application The application built from it.
         */
        public ClientApplicationHolder(final String key, final ConfidentialClientApplication application) {
            this.key = key;
            this.application = application;
        }

        /**
         * Gets the configuration key.
         *
         * @return The key.
         */
        public String getKey() {
            return key;
        }

        /**
         * Gets the client application.
         *
         * @return The application.
         */
        public ConfidentialClientApplication getApplication() {
            return application;
        }
    }

    /**
     * Obtains an access token using a refresh token.
     * @param refreshToken The refresh token to use for token acquisition.
     * @return The authentication result containing the access token.
     */
    public IAuthenticationResult getAccessToken(final String refreshToken) {
        final String authority = getAuthority() + getTenant() + "/";
        if (logger.isDebugEnabled()) {
            logger.debug("refreshToken={}, authority={}", maskSecret(refreshToken), authority);
        }
        try {
            final ConfidentialClientApplication app = getClientApplication();

            final RefreshTokenParameters parameters =
                    RefreshTokenParameters.builder(Collections.singleton("https://graph.microsoft.com/.default"), refreshToken).build();

            final IAuthenticationResult result = app.acquireToken(parameters).get(acquisitionTimeout, TimeUnit.MILLISECONDS);
            if (result == null) {
                throw new SsoLoginException("authentication result was null");
            }
            return result;
        } catch (final Exception e) {
            throw new SsoLoginException("Failed to get a token.", e);
        }
    }

    /**
     * Obtains an access token using an authorization code.
     * @param authorizationCode The authorization code received from Entra ID.
     * @param currentUri The current URI for the redirect.
     * @return The authentication result containing the access token.
     */
    protected IAuthenticationResult getAccessToken(final AuthorizationCode authorizationCode, final String currentUri) {
        final String authority = getAuthority() + getTenant() + "/";
        final String authCode = authorizationCode.getValue();
        if (logger.isDebugEnabled()) {
            logger.debug("authCode={}, authority={}, uri={}", maskSecret(authCode), authority, currentUri);
        }
        try {
            final ConfidentialClientApplication app = getClientApplication();

            final AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(authCode, new URI(currentUri))
                    .scopes(Collections.singleton("https://graph.microsoft.com/.default"))
                    .build();

            final IAuthenticationResult result = app.acquireToken(parameters).get(acquisitionTimeout, TimeUnit.MILLISECONDS);
            if (result == null) {
                throw new SsoLoginException("authentication result was null");
            }
            return result;
        } catch (final Exception e) {
            throw new SsoLoginException("Failed to get a token.", e);
        }
    }

    /**
     * Attempts to refresh tokens silently using the MSAL4J silent authentication flow.
     * @param user The Entra ID user whose tokens need to be refreshed.
     * @return The new authentication result, or null if silent refresh failed.
     */
    public IAuthenticationResult refreshTokenSilently(final EntraIdCredential.EntraIdUser user) {
        try {
            final ConfidentialClientApplication app = getClientApplication();

            final SilentParameters parameters = SilentParameters
                    .builder(Collections.singleton("https://graph.microsoft.com/.default"), user.getAuthenticationResult().account())
                    .build();

            final IAuthenticationResult result = app.acquireTokenSilently(parameters).get(acquisitionTimeout, TimeUnit.MILLISECONDS);
            if (logger.isDebugEnabled()) {
                logger.debug("Silent token acquisition successful");
            }
            return result;
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Silent token acquisition failed: {}", e.getMessage());
            }
            return null;
        }
    }

    /**
     * Validates that the authentication response matches the authorization code flow.
     * @param oidcResponse The OpenID Connect authentication success response.
     */
    protected void validateAuthRespMatchesCodeFlow(final AuthenticationSuccessResponse oidcResponse) {
        if (oidcResponse.getIDToken() != null || oidcResponse.getAccessToken() != null || oidcResponse.getAuthorizationCode() == null) {
            throw new SsoLoginException("unexpected set of artifacts received");
        }
    }

    /**
     * Validates the OAuth2 state parameter.
     * @param session The HTTP session containing stored state data.
     * @param state The state parameter to validate.
     * @return The validated state data.
     */
    protected StateData validateState(final HttpSession session, final String state) {
        if (StringUtils.isNotEmpty(state)) {
            final StateData stateDataInSession = removeStateFromSession(session, state);
            if (stateDataInSession != null) {
                return stateDataInSession;
            }
        }
        throw new SsoStateException("could not validate state");
    }

    /**
     * Removes and returns state data from the HTTP session.
     * @param session The HTTP session.
     * @param state The state parameter to remove.
     * @return The removed state data or null if not found.
     */
    protected StateData removeStateFromSession(final HttpSession session, final String state) {
        final Map<String, StateData> states = getStateMap(session);
        removeExpiredStates(states);
        final StateData stateData = states.remove(state);
        if (stateData != null && logger.isDebugEnabled()) {
            logger.debug("Restoring state from session: {}", stateData);
        }
        return stateData;
    }

    /**
     * Checks if the request contains authentication data from Entra ID.
     * @param request The HTTP servlet request to check.
     * @return True if authentication data is present, false otherwise.
     */
    protected boolean containsAuthenticationData(final HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("HTTP Method: {}", request.getMethod());
        }
        // The authorization response arrives as a GET in query mode and as a POST in form_post
        // mode; both are accepted because entraid.response.mode selects between them, and a login
        // already in flight when that setting changes still has to complete.
        final String method = request.getMethod();
        if (!"GET".equalsIgnoreCase(method) && !"POST".equalsIgnoreCase(method)) {
            return false;
        }
        final Map<String, String[]> params = request.getParameterMap();
        if (logger.isDebugEnabled()) {
            logger.debug("params={}", params.keySet());
        }
        return params.containsKey(ERROR) || params.containsKey(ID_TOKEN) || params.containsKey(CODE);
    }

    /**
     * Applies the headers and timeouts every Microsoft Graph request needs.
     *
     * @param request The request to configure.
     * @param accessToken The bearer token to authenticate with.
     * @return The configured request.
     */
    protected CurlRequest createGraphRequest(final CurlRequest request, final String accessToken) {
        return request.header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .timeout(graphConnectTimeout, graphReadTimeout);
    }

    /**
     * Sets the connection timeout for Microsoft Graph requests.
     * @param graphConnectTimeout The timeout in milliseconds.
     */
    public void setGraphConnectTimeout(final int graphConnectTimeout) {
        this.graphConnectTimeout = graphConnectTimeout;
    }

    /**
     * Sets the read timeout for Microsoft Graph requests.
     * @param graphReadTimeout The timeout in milliseconds.
     */
    public void setGraphReadTimeout(final int graphReadTimeout) {
        this.graphReadTimeout = graphReadTimeout;
    }

    /**
     * Updates the user's group and role membership information with lazy loading for parent groups.
     * Direct groups are retrieved synchronously, while parent groups are fetched asynchronously
     * to avoid login delays when users have many nested group memberships.
     *
     * <p>When Microsoft Graph does not answer with a membership list, the memberships already on
     * the user are kept; if there are none yet -- which is the case at login -- the login either
     * completes with whatever was collected plus the configured defaults, or is failed, according
     * to {@link #isRequireMembership()}.
     *
     * @param user The Entra ID user to update.
     * @throws LoginFailureException If the first membership lookup for this user failed and
     *         {@code entraid.require.membership} is enabled.
     */
    public void updateMemberOf(final EntraIdUser user) {
        if (logger.isDebugEnabled()) {
            logger.debug("[updateMemberOf] Starting for user: {}", user.getName());
        }

        final List<String> groupList = new ArrayList<>();
        final List<String> roleList = new ArrayList<>();
        final List<String> groupIdsForParentLookup = new ArrayList<>();

        final List<String> defaultGroups = getDefaultGroupList();
        final List<String> defaultRoles = getDefaultRoleList();
        groupList.addAll(defaultGroups);
        roleList.addAll(defaultRoles);

        if (logger.isDebugEnabled()) {
            logger.debug("[updateMemberOf] Default groups: {}, Default roles: {}", defaultGroups, defaultRoles);
        }

        // Retrieve direct groups synchronously (parent group lookup is deferred)
        final boolean resolved =
                processDirectMemberOf(user, groupList, roleList, groupIdsForParentLookup, "https://graph.microsoft.com/v1.0/me/memberOf");

        if (logger.isDebugEnabled()) {
            logger.debug("[updateMemberOf] Direct groups retrieved. Total groups: {}, Total roles: {}, Group IDs for parent lookup: {}",
                    groupList.size(), roleList.size(), groupIdsForParentLookup.size());
        }

        if (!resolved) {
            // Microsoft Graph did not answer with a membership list -- an expired token, a
            // throttled tenant, a revoked permission.
            if (user.getGroupNames() != null) {
                // Refresh path. Writing what we have would replace the memberships this user
                // logged in with by the configured defaults alone, silently taking away their
                // search permissions until some later call happens to succeed.
                logger.warn("Failed to resolve the Entra ID memberships of {}. Keeping the ones already resolved.", user.getName());
                return;
            }
            // First resolution, so there is nothing to keep.
            if (isRequireMembership()) {
                // Handing out a session carrying the configured defaults alone looks like a
                // successful login and quietly truncates every search result for its whole
                // lifetime, and refresh() does not try again until the token is nearly expired.
                // A deployment that would rather have no session at all asks for this.
                //
                // LoginFailureException specifically: this runs inside the resolver that
                // FessLoginAssist passes to TypicalLoginAssist.findLoginUser, which calls it
                // directly without wrapping anything, and SsoAction only catches
                // LoginFailureException around fessLoginAssist.loginRedirect(). Any other type
                // escapes to the generic error page instead of the standard SSO error message.
                throw new LoginFailureException("Failed to resolve the Entra ID memberships of " + user.getName() + ".");
            }
            // Otherwise degrade rather than refuse, which is what 15.7 did. A throttled tenant, a
            // Graph outage or a permission that was never granted would otherwise refuse every
            // login in the tenant for as long as the condition lasts. The lists are seeded with
            // the configured defaults before the lookup, so what is written below is always a
            // superset of them.
            logger.warn(
                    "Failed to resolve the Entra ID memberships of {}. Continuing with the memberships collected so far"
                            + " and the configured defaults. Set {} = true to fail the login instead.",
                    user.getName(), ENTRAID_REQUIRE_MEMBERSHIP);
        }

        // Set initial groups
        user.setGroups(groupList.stream().distinct().toArray(n -> new String[n]));
        user.setRoles(roleList.stream().distinct().toArray(n -> new String[n]));

        if (logger.isDebugEnabled()) {
            logger.debug("[updateMemberOf] Initial groups/roles set for user: {}. Groups: {}, Roles: {}", user.getName(),
                    Arrays.toString(user.getGroupNames()), Arrays.toString(user.getRoleNames()));
        }

        // Schedule lazy loading of parent groups
        if (!groupIdsForParentLookup.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("[updateMemberOf] Scheduling parent group lookup for {} group IDs: {}", groupIdsForParentLookup.size(),
                        groupIdsForParentLookup);
            }
            scheduleParentGroupLookup(user, new ArrayList<>(groupList), new ArrayList<>(roleList), groupIdsForParentLookup);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("[updateMemberOf] No parent group lookup needed (no group IDs to process)");
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[updateMemberOf] Completed for user: {}", user.getName());
        }
    }

    /**
     * Adds a group or role name to the specified list.
     * @param list The list to add the group or role name to.
     * @param value The group or role name value.
     * @param useDomainServices Whether to use domain services for group resolution.
     */
    protected void addGroupOrRoleName(final List<String> list, final String value, final boolean useDomainServices) {
        list.add(value);
        if (useDomainServices && value.indexOf('@') >= 0) {
            final String[] values = value.split("@");
            if (values.length > 1) {
                list.add(values[0]);
            }
        }
    }

    /**
     * Processes direct member-of information from Microsoft Graph API without parent group lookup.
     * This method retrieves only direct group memberships and collects group IDs for later
     * asynchronous parent group lookup.
     * @param user The Entra ID user.
     * @param groupList The list to add group names to.
     * @param roleList The list to add role names to.
     * @param groupIdsForParentLookup The list to collect group IDs for later parent lookup.
     * @param url The Microsoft Graph API URL.
     * @return True if Microsoft Graph answered with a membership list, false if it reported an
     *         error or could not be read. When this is false the lists hold whatever was collected
     *         before the failure, and {@link #updateMemberOf} discards them: it keeps the
     *         memberships the user already had, or fails the login when there are none yet.
     */
    protected boolean processDirectMemberOf(final EntraIdUser user, final List<String> groupList, final List<String> roleList,
            final List<String> groupIdsForParentLookup, final String url) {
        if (logger.isDebugEnabled()) {
            logger.debug("[processDirectMemberOf] Fetching direct memberships from URL: {}", url);
        }
        try (CurlResponse response = createGraphRequest(Curl.get(url), user.getAuthenticationResult().accessToken()).execute()) {
            // Before the body, for the same reason as in getMemberGroupIds: a throttled reply is
            // not required to be JSON, and the parser throws CurlException when it is not. The
            // lookup itself is still attempted while throttled -- a login has to try -- but
            // recording the backoff here is what keeps the asynchronous parent group walk from
            // hammering a Graph that already asked us to wait.
            applyGraphThrottle(response);
            final Map<String, Object> contentMap = response.getContent(OpenSearchCurl.jsonParser());
            if (logger.isDebugEnabled()) {
                logger.debug("response={}", contentMap);
            }
            if (contentMap.containsKey("value")) {
                @SuppressWarnings("unchecked")
                final List<Map<String, Object>> memberOfList = (List<Map<String, Object>>) contentMap.get("value");
                final FessConfig fessConfig = ComponentUtil.getFessConfig();
                for (final Map<String, Object> memberOf : memberOfList) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("member={}", memberOf);
                    }
                    String memberType = (String) memberOf.get("@odata.type");
                    if (memberType == null) {
                        logger.warn("@odata.type is null: {}", memberOf);
                        continue;
                    }
                    memberType = memberType.toLowerCase(Locale.ENGLISH);
                    final String id = (String) memberOf.get("id");
                    if (StringUtil.isNotBlank(id)) {
                        if (memberType.contains("group")) {
                            groupList.add(id);
                            // Collect group ID for parent lookup (deferred)
                            groupIdsForParentLookup.add(id);
                            if (logger.isDebugEnabled()) {
                                logger.debug("[processDirectMemberOf] Added group ID: {} (will lookup parent groups later)", id);
                            }
                        } else if (memberType.contains("role")) {
                            roleList.add(id);
                            if (logger.isDebugEnabled()) {
                                logger.debug("[processDirectMemberOf] Added role ID: {}", id);
                            }
                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("[processDirectMemberOf] Unknown @odata.type: {}, treating as group", memberOf);
                            }
                            groupList.add(id);
                            groupIdsForParentLookup.add(id);
                        }
                    } else {
                        logger.warn("id is empty: {}", memberOf);
                    }
                    final String[] names = fessConfig.getEntraIdPermissionFields();
                    final boolean useDomainServices = fessConfig.isEntraIdUseDomainServices();
                    for (final String name : names) {
                        final String value = (String) memberOf.get(name);
                        if (StringUtil.isNotBlank(value)) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("{} is a member of {}", name, value);
                            }
                            if (memberType.contains("group")) {
                                addGroupOrRoleName(groupList, value, useDomainServices);
                            } else if (memberType.contains("role")) {
                                addGroupOrRoleName(roleList, value, useDomainServices);
                            } else {
                                addGroupOrRoleName(groupList, value, useDomainServices);
                            }
                        } else if (logger.isDebugEnabled()) {
                            logger.debug("{} is empty: {}", name, memberOf);
                        }
                    }
                }
                final String nextLink = (String) contentMap.get("@odata.nextLink");
                if (StringUtil.isNotBlank(nextLink)) {
                    return processDirectMemberOf(user, groupList, roleList, groupIdsForParentLookup, nextLink);
                }
                return true;
            }
            if (contentMap.containsKey("error")) {
                logger.warn("Failed to access groups/roles: {}", contentMap);
            } else {
                logger.warn("Unexpected response while accessing groups/roles: {}", contentMap);
            }
            return false;
        } catch (final IOException | RuntimeException e) {
            // Every unchecked failure has to take this path too, not just curl4j's CurlException:
            // a body whose "value" is not an array of objects throws ClassCastException from the
            // cast above, which used to escape updateMemberOf and the EntraIdUser constructor and
            // land on the generic error page rather than on the controlled outcome the caller
            // chooses. Nothing thrown from inside this method has to propagate --
            // LoginFailureException is raised by updateMemberOf, after this returns.
            logger.warn("Failed to access groups/roles in Entra ID.", e);
            return false;
        }
    }

    /**
     * Schedules asynchronous parent group lookup using TimeoutManager.
     * This method defers the retrieval of nested group information to avoid login delays.
     * @param user The Entra ID user.
     * @param initialGroups The initial group list to be updated.
     * @param initialRoles The initial role list to be updated.
     * @param groupIds The list of group IDs to lookup parent groups for.
     */
    protected void scheduleParentGroupLookup(final EntraIdUser user, final List<String> initialGroups, final List<String> initialRoles,
            final List<String> groupIds) {
        if (logger.isDebugEnabled()) {
            logger.debug("[scheduleParentGroupLookup] Scheduling async parent group lookup for user: {}, groupIds count: {}",
                    user.getName(), groupIds.size());
        }
        TimeoutManager.getInstance().addTimeoutTarget(() -> {
            if (logger.isDebugEnabled()) {
                logger.debug("[scheduleParentGroupLookup] Async task started for user: {}", user.getName());
            }
            final long startTime = System.currentTimeMillis();
            try {
                final List<String> updatedGroups = new ArrayList<>(initialGroups);
                final List<String> updatedRoles = new ArrayList<>(initialRoles);

                if (logger.isDebugEnabled()) {
                    logger.debug("[scheduleParentGroupLookup] Processing {} group IDs for parent lookup", groupIds.size());
                }

                int processedCount = 0;
                for (final String groupId : groupIds) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[scheduleParentGroupLookup] Processing parent groups for groupId: {} ({}/{})", groupId,
                                ++processedCount, groupIds.size());
                    }
                    processParentGroup(user, updatedGroups, updatedRoles, groupId);
                }

                // Update groups/roles
                final String[] finalGroups = updatedGroups.stream().distinct().toArray(n -> new String[n]);
                final String[] finalRoles = updatedRoles.stream().distinct().toArray(n -> new String[n]);
                user.setGroups(finalGroups);
                user.setRoles(finalRoles);

                // Reset permissions to force recalculation
                user.resetPermissions();

                final long elapsedTime = System.currentTimeMillis() - startTime;
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "[scheduleParentGroupLookup] Async task completed for user: {}. Final groups: {}, Final roles: {}, Elapsed time: {}ms",
                            user.getName(), finalGroups.length, finalRoles.length, elapsedTime);
                    logger.debug("[scheduleParentGroupLookup] Final groups for user {}: {}", user.getName(), Arrays.toString(finalGroups));
                    logger.debug("[scheduleParentGroupLookup] Final roles for user {}: {}", user.getName(), Arrays.toString(finalRoles));
                }

                // Update session information
                if (logger.isDebugEnabled()) {
                    logger.debug("[scheduleParentGroupLookup] Notifying permission change for user: {}", user.getName());
                }
                ComponentUtil.getActivityHelper().permissionChanged(OptionalThing.of(new FessUserBean(user)));
            } catch (final Exception e) {
                final long elapsedTime = System.currentTimeMillis() - startTime;
                logger.warn("Failed to process parent groups asynchronously for user: {} after {}ms", user.getName(), elapsedTime, e);
            }
        }, 0, false);
    }

    /**
     * Processes parent group information for nested groups.
     * @param user The Entra ID user.
     * @param groupList The list to add group names to.
     * @param roleList The list to add role names to.
     * @param id The group ID to process.
     */
    protected void processParentGroup(final EntraIdUser user, final List<String> groupList, final List<String> roleList, final String id) {
        processParentGroup(user, groupList, roleList, id, 0);
    }

    /**
     * Processes parent group information for nested groups with depth tracking.
     * @param user The Entra ID user.
     * @param groupList The list to add group names to.
     * @param roleList The list to add role names to.
     * @param id The group ID to process.
     * @param depth The current recursion depth.
     */
    protected void processParentGroup(final EntraIdUser user, final List<String> groupList, final List<String> roleList, final String id,
            final int depth) {
        if (logger.isDebugEnabled()) {
            logger.debug("[processParentGroup] Processing parent groups for id: {}, depth: {}/{}", id, depth, maxGroupDepth);
        }
        if (depth >= maxGroupDepth) {
            if (logger.isDebugEnabled()) {
                logger.debug("[processParentGroup] Maximum group depth {} reached for group {}", maxGroupDepth, id);
            }
            return;
        }
        final Pair<String[], String[]> groupsAndRoles = getParentGroup(user, id, depth);
        StreamUtil.stream(groupsAndRoles.getFirst()).of(stream -> stream.forEach(groupList::add));
        StreamUtil.stream(groupsAndRoles.getSecond()).of(stream -> stream.forEach(roleList::add));
        if (logger.isDebugEnabled()) {
            logger.debug("[processParentGroup] Completed for id: {}, depth: {}, added groups: {}, added roles: {}", id, depth,
                    groupsAndRoles.getFirst().length, groupsAndRoles.getSecond().length);
        }
    }

    /**
     * Retrieves parent group information for the specified group ID with depth tracking.
     * @param user The Entra ID user.
     * @param id The group ID to get parent information for.
     * @param depth The current recursion depth.
     * @return A pair containing group names and role names.
     */
    protected Pair<String[], String[]> getParentGroup(final EntraIdUser user, final String id, final int depth) {
        if (logger.isDebugEnabled()) {
            logger.debug("[getParentGroup] Getting parent groups for id: {}, depth: {}", id, depth);
        }
        if (depth >= maxGroupDepth) {
            if (logger.isDebugEnabled()) {
                logger.debug("[getParentGroup] Maximum group depth {} reached for group {}", maxGroupDepth, id);
            }
            return new Pair<>(StringUtil.EMPTY_STRINGS, StringUtil.EMPTY_STRINGS);
        }
        // Check if cached
        final Pair<String[], String[]> cachedResult = groupCache.getIfPresent(id);
        if (cachedResult != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("[getParentGroup] Cache HIT for id: {}, groups: {}, roles: {}", id, cachedResult.getFirst().length,
                        cachedResult.getSecond().length);
            }
            return cachedResult;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("[getParentGroup] Cache MISS for id: {}, fetching from API", id);
        }
        if (isGraphThrottled()) {
            // Microsoft Graph asked us to back off, so walking it would fail once per group on
            // every login until the tenant recovered. The empty pair is deliberately not written
            // to groupCache: caching it would keep the parent group permissions away for the whole
            // cache TTL even once the throttle had lapsed, which is what #3223 removed.
            if (logger.isDebugEnabled()) {
                logger.debug("[getParentGroup] Skipping the lookup for id {} while Microsoft Graph is throttling.", id);
            }
            return new Pair<>(StringUtil.EMPTY_STRINGS, StringUtil.EMPTY_STRINGS);
        }
        try {
            return groupCache.get(id, () -> loadParentGroup(user, id, depth));
        } catch (final ExecutionException | UncheckedExecutionException e) {
            // A loader that throws leaves nothing in the cache, which is the point: a throttled or
            // briefly unreachable Graph must not pin an empty result for the whole cache TTL.
            // UncheckedExecutionException matters because the Graph JSON parser throws
            // CurlException, a RuntimeException, on a non-JSON error body.
            if (isGraphThrottled()) {
                // The reason is already stated by the single WARN that set the throttle; a stack
                // trace per group would bury it.
                logger.warn("Failed to process group cache for id {} while Microsoft Graph is throttling: {}", id, e.getMessage());
            } else {
                logger.warn("Failed to process group cache for id: {}", id, e);
            }
            return new Pair<>(StringUtil.EMPTY_STRINGS, StringUtil.EMPTY_STRINGS);
        }
    }

    /**
     * Returns whether Microsoft Graph is still inside the backoff a throttled response asked for.
     *
     * @return True while the parent group walk has to be skipped.
     */
    protected boolean isGraphThrottled() {
        final long until = graphThrottledUntil;
        // The clock is read only once a throttle has actually been recorded, so the ordinary path
        // does not depend on the system helper at all.
        return until > 0L && ComponentUtil.getSystemHelper().getCurrentTimeAsLong() < until;
    }

    /**
     * Records the backoff a throttled Microsoft Graph response asked for, if it is one.
     *
     * <p>Nothing is cached in response to it. 15.7 cached an empty result for the cache TTL, and
     * #3223 removed that because it silently took the parent group permissions away for ten
     * minutes. An explicit backoff does the job the negative cache was reaching for -- the next
     * logins skip the walk instead of re-issuing one failing request, and one stack trace, per
     * group -- without outliving the condition that caused it.
     *
     * @param response The response to inspect.
     */
    protected void applyGraphThrottle(final CurlResponse response) {
        // curl4j does not throw on a non-2xx response, it hands back the error stream, so the
        // status code is the only place a 429 is visible.
        final int statusCode = response.getHttpStatusCode();
        if (statusCode != HTTP_TOO_MANY_REQUESTS && statusCode != HTTP_SERVICE_UNAVAILABLE) {
            return;
        }
        final long seconds = parseRetryAfterSeconds(response.getHeaderValue("Retry-After"));
        final long until = ComponentUtil.getSystemHelper().getCurrentTimeAsLong() + seconds * 1000L;
        if (until > graphThrottledUntil) {
            graphThrottledUntil = until;
            // One line per throttling episode: every group after this one short-circuits in
            // getParentGroup without reaching Graph, so nothing repeats it per group.
            logger.warn("Microsoft Graph returned {} for a group membership lookup."
                    + " Nested groups are not resolved for the next {} seconds.", statusCode, seconds);
        }
    }

    /**
     * Reads a {@code Retry-After} header as a number of seconds.
     *
     * @param value The header value, which may be null.
     * @return The backoff in seconds, always positive and bounded by
     *         {@link #MAX_GRAPH_THROTTLE_SECONDS}.
     */
    protected long parseRetryAfterSeconds(final String value) {
        if (StringUtil.isNotBlank(value)) {
            try {
                final long seconds = Long.parseLong(value.trim());
                if (seconds > 0L) {
                    return Math.min(seconds, MAX_GRAPH_THROTTLE_SECONDS);
                }
            } catch (final NumberFormatException e) {
                // RFC 9110 also allows an HTTP-date here. Microsoft Graph sends delay-seconds, so
                // rather than parse a format we never see, fall back to the default.
                if (logger.isDebugEnabled()) {
                    logger.debug("Retry-After is not a number of seconds: {}", value);
                }
            }
        }
        return DEFAULT_GRAPH_THROTTLE_SECONDS;
    }

    /**
     * Walks the parent groups of the specified group. Any failure is thrown rather than turned
     * into an empty result, so a caller that caches this never stores a transient failure.
     *
     * @param user The Entra ID user.
     * @param id The group ID to get parent information for.
     * @param depth The current recursion depth.
     * @return A pair containing group names and role names.
     * @throws IOException If Microsoft Graph could not be reached or returned an error.
     */
    protected Pair<String[], String[]> loadParentGroup(final EntraIdUser user, final String id, final int depth) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("[getParentGroup] Loading parent groups for id: {}", id);
        }
        final List<String> groupList = new ArrayList<>();
        final List<String> roleList = new ArrayList<>();
        for (final String value : getMemberGroupIds(user, id)) {
            if (logger.isDebugEnabled()) {
                logger.debug("[getParentGroup] Processing parent group id: {} for group: {}", value, id);
            }
            processGroup(user, groupList, roleList, value);
            if (!groupList.contains(value) && !roleList.contains(value)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[getParentGroup] Recursively getting parent groups for: {}", value);
                }
                final Pair<String[], String[]> groupsAndRoles = getParentGroup(user, value, depth + 1);
                StreamUtil.stream(groupsAndRoles.getFirst()).of(stream1 -> stream1.forEach(groupList::add));
                StreamUtil.stream(groupsAndRoles.getSecond()).of(stream2 -> stream2.forEach(roleList::add));
            }
        }
        final Pair<String[], String[]> result = new Pair<>(groupList.stream().distinct().toArray(n1 -> new String[n1]),
                roleList.stream().distinct().toArray(n2 -> new String[n2]));
        if (logger.isDebugEnabled()) {
            logger.debug("[getParentGroup] Result for id {}: {} groups, {} roles", id, result.getFirst().length, result.getSecond().length);
        }
        return result;
    }

    /**
     * Asks Microsoft Graph which groups the specified group is a member of.
     *
     * <p>Two error codes are real answers rather than failures and come back as an empty array so
     * that the caller can cache them: {@code Request_ResourceNotFound}, because the group does not
     * exist, and {@code Authorization_RequestDenied}, because a Graph permission that was never
     * granted will not appear within the cache TTL -- throwing on it left nothing cached and made
     * every login re-issue one failing request, and one stack trace, per group. Everything else is
     * thrown, so a transient failure is never mistaken for "this group has no parents".
     *
     * <p>A throttled reply is recorded by {@link #applyGraphThrottle} before the body is looked
     * at, so the groups after this one skip the walk instead of each producing their own failure.
     *
     * @param user The Entra ID user.
     * @param id The group ID to get parent information for.
     * @return The parent group IDs, never null.
     * @throws IOException If Microsoft Graph could not be reached or returned an error.
     */
    protected String[] getMemberGroupIds(final EntraIdUser user, final String id) throws IOException {
        return getMemberGroupIds(user, id, "https://graph.microsoft.com/v1.0/groups/" + id + "/getMemberGroups");
    }

    /**
     * Asks Microsoft Graph, at the specified URL, which groups the specified group is a member of.
     * The URL is a parameter so that this, like {@link #processDirectMemberOf} and
     * {@link #processGroup}, can be pointed at a stub rather than at Microsoft Graph.
     *
     * @param user The Entra ID user.
     * @param id The group ID to get parent information for.
     * @param url The Microsoft Graph URL to post the request to.
     * @return The parent group IDs, never null.
     * @throws IOException If Microsoft Graph could not be reached or returned an error.
     */
    protected String[] getMemberGroupIds(final EntraIdUser user, final String id, final String url) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("[getParentGroup] Calling API: {}", url);
        }
        try (CurlResponse response =
                createGraphRequest(Curl.post(url), user.getAuthenticationResult().accessToken()).header("Content-type", "application/json")
                        .body("{\"securityEnabledOnly\":false}")
                        .execute()) {
            // Before the body: a throttled reply is not required to be JSON, and the parser throws
            // CurlException when it is not.
            applyGraphThrottle(response);
            final Map<String, Object> contentMap = response.getContent(OpenSearchCurl.jsonParser());
            if (logger.isDebugEnabled()) {
                logger.debug("[getParentGroup] Response for id {}: {}", id, contentMap);
            }
            return toMemberGroupIds(contentMap, id);
        }
    }

    /**
     * Classifies a {@code getMemberGroups} response body. See {@link #getMemberGroupIds} for which
     * error codes count as an answer and which are failures.
     *
     * @param contentMap The parsed response body.
     * @param id The group ID the response is for.
     * @return The parent group IDs, never null.
     * @throws IOException If the body reports a failure rather than an answer.
     */
    protected String[] toMemberGroupIds(final Map<String, Object> contentMap, final String id) throws IOException {
        if (contentMap.containsKey("value")) {
            final String[] values = DocumentUtil.getValue(contentMap, "value", String[].class);
            return values != null ? values : StringUtil.EMPTY_STRINGS;
        }
        if (contentMap.containsKey("error")) {
            if (contentMap.get("error") instanceof final Map<?, ?> errorMap) {
                final Object code = errorMap.get("code");
                if ("Request_ResourceNotFound".equals(code)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("[getParentGroup] Resource not found for id {}: {}", id, contentMap);
                    }
                    return StringUtil.EMPTY_STRINGS;
                }
                if (PERMISSION_DENIED_ERROR_CODE.equals(code)) {
                    logger.warn("Not allowed to read the parent groups of {}. Grant the Entra ID application"
                            + " GroupMember.Read.All to resolve nested groups. {}", id, contentMap);
                    return StringUtil.EMPTY_STRINGS;
                }
            }
            throw new IOException("Failed to access parent groups for id " + id + ": " + contentMap);
        }
        return StringUtil.EMPTY_STRINGS;
    }

    /**
     * Processes individual group information.
     * @param user The Entra ID user.
     * @param groupList The list to add group names to.
     * @param roleList The list to add role names to.
     * @param id The group ID to process.
     */
    protected void processGroup(final EntraIdUser user, final List<String> groupList, final List<String> roleList, final String id) {
        processGroup(user, groupList, roleList, id, "https://graph.microsoft.com/v1.0/groups/" + id);
    }

    /**
     * Processes individual group information read from the specified URL. The URL is a parameter
     * so that this, like {@link #processDirectMemberOf}, can be pointed at a stub rather than at
     * Microsoft Graph.
     *
     * @param user The Entra ID user.
     * @param groupList The list to add group names to.
     * @param roleList The list to add role names to.
     * @param id The group ID to process.
     * @param url The Microsoft Graph URL to read the group from.
     */
    protected void processGroup(final EntraIdUser user, final List<String> groupList, final List<String> roleList, final String id,
            final String url) {
        if (logger.isDebugEnabled()) {
            logger.debug("[processGroup] Processing group info for id: {} from url: {}", id, url);
        }
        try (CurlResponse response = createGraphRequest(Curl.get(url), user.getAuthenticationResult().accessToken()).execute()) {
            // Before the body, for the same reason as in getMemberGroupIds and
            // processDirectMemberOf: a throttled reply is not required to be JSON, and the parser
            // throws CurlException when it is not. This was the one Graph call in the class that
            // did not record the backoff, and it is the one most likely to meet a 429 first: the
            // parent group walk calls it once per member id, whereas getMemberGroupIds is called
            // once per group. Worse, a 429 whose body *is* JSON leaves the walk believing it had
            // an answer -- groupList.add(id) below runs on every non-throwing path -- so nothing
            // else on that path ever reached Graph to notice the throttling.
            applyGraphThrottle(response);
            final Map<String, Object> contentMap = response.getContent(OpenSearchCurl.jsonParser());
            if (logger.isDebugEnabled()) {
                logger.debug("[processGroup] Response for id {}: {}", id, contentMap);
            }
            groupList.add(id);
            if (contentMap.containsKey("error")) {
                logger.warn("Failed to access group info: {}", contentMap);
            } else {
                final FessConfig fessConfig = ComponentUtil.getFessConfig();
                final String[] names = fessConfig.getEntraIdPermissionFields();
                final int initialSize = groupList.size();
                for (final String name : names) {
                    final String value = (String) contentMap.get(name);
                    if (StringUtil.isNotBlank(value)) {
                        groupList.add(value);
                        if (logger.isDebugEnabled()) {
                            logger.debug("[processGroup] Added {} value: {} for group id: {}", name, value, id);
                        }
                    } else if (logger.isDebugEnabled()) {
                        logger.debug("[processGroup] {} is empty for group id: {}", name, id);
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("[processGroup] Completed for id: {}, added {} entries", id, groupList.size() - initialSize);
                }
            }
        } catch (final IOException | CurlException e) {
            // See processDirectMemberOf: curl4j's transport failure is the unchecked CurlException.
            logger.warn("Failed to access groups/roles in Entra ID for id: {}", id, e);
        }
    }

    /**
     * Gets the default group list for users.
     * Uses new entraid.default.groups key with fallback to legacy aad.default.groups.
     * @return The default group list.
     */
    protected List<String> getDefaultGroupList() {
        String value = ComponentUtil.getFessConfig().getSystemProperty(ENTRAID_DEFAULT_GROUPS);
        if (StringUtil.isBlank(value)) {
            value = ComponentUtil.getFessConfig().getSystemProperty(AAD_DEFAULT_GROUPS);
        }
        if (StringUtil.isBlank(value)) {
            return Collections.emptyList();
        }
        return split(value, ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toList()));
    }

    /**
     * Gets the default role list for users.
     * Uses new entraid.default.roles key with fallback to legacy aad.default.roles.
     * @return The default role list.
     */
    protected List<String> getDefaultRoleList() {
        String value = ComponentUtil.getFessConfig().getSystemProperty(ENTRAID_DEFAULT_ROLES);
        if (StringUtil.isBlank(value)) {
            value = ComponentUtil.getFessConfig().getSystemProperty(AAD_DEFAULT_ROLES);
        }
        if (StringUtil.isBlank(value)) {
            return Collections.emptyList();
        }
        return split(value, ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toList()));
    }

    /**
     * Returns whether a login must be refused when the first membership lookup does not answer.
     *
     * <p>Defaults to false, which is what 15.7 did: a throttled tenant, a Graph outage or a
     * {@code GroupMember.Read.All} permission that was never granted degrades the login to the
     * memberships collected so far plus the configured defaults, rather than refusing every login
     * in the tenant for as long as the condition lasts. A deployment that would rather hand out no
     * session at all than an under-permissioned one sets {@code entraid.require.membership} to
     * {@code true}.
     *
     * @return True to fail the login, false to complete it with the configured defaults.
     */
    protected boolean isRequireMembership() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(ENTRAID_REQUIRE_MEMBERSHIP);
        if (StringUtil.isBlank(value)) {
            // Absent, or present but empty. getSystemProperty substitutes a default only when the
            // key is absent, and the admin screen stores an empty string rather than removing a
            // key, so both have to be mapped onto the default here rather than left to
            // Boolean.parseBoolean.
            return false;
        }
        return Boolean.parseBoolean(value.trim());
    }

    /**
     * Represents state data stored during the OAuth2 authentication flow.
     */
    protected static class StateData {
        private final String nonce;
        private final long expiration;

        /**
         * Constructs StateData with nonce and expiration.
         * @param nonce The nonce value.
         * @param expiration The expiration timestamp.
         */
        public StateData(final String nonce, final long expiration) {
            this.nonce = nonce;
            this.expiration = expiration;
        }

        /**
         * Gets the nonce value.
         * @return The nonce.
         */
        public String getNonce() {
            return nonce;
        }

        /**
         * Gets the expiration timestamp.
         * @return The expiration timestamp.
         */
        public long getExpiration() {
            return expiration;
        }

        @Override
        public String toString() {
            return "StateData [nonce=" + nonce + ", expiration=" + expiration + "]";
        }
    }

    /**
     * Gets the Entra ID client ID from configuration.
     * Uses new entraid.client.id key with fallback to legacy aad.client.id.
     * @return The client ID.
     */
    protected String getClientId() {
        String value = ComponentUtil.getFessConfig().getSystemProperty(ENTRAID_CLIENT_ID);
        if (StringUtil.isBlank(value)) {
            value = ComponentUtil.getFessConfig().getSystemProperty(AAD_CLIENT_ID, StringUtil.EMPTY);
        }
        return value;
    }

    /**
     * Gets the Entra ID client secret from configuration.
     * Uses new entraid.client.secret key with fallback to legacy aad.client.secret.
     * @return The client secret.
     */
    protected String getClientSecret() {
        String value = ComponentUtil.getFessConfig().getSystemProperty(ENTRAID_CLIENT_SECRET);
        if (StringUtil.isBlank(value)) {
            value = ComponentUtil.getFessConfig().getSystemProperty(AAD_CLIENT_SECRET, StringUtil.EMPTY);
        }
        return value;
    }

    /**
     * Gets the Entra ID tenant ID from configuration.
     * Uses new entraid.tenant key with fallback to legacy aad.tenant.
     * @return The tenant ID.
     */
    protected String getTenant() {
        String value = ComponentUtil.getFessConfig().getSystemProperty(ENTRAID_TENANT);
        if (StringUtil.isBlank(value)) {
            value = ComponentUtil.getFessConfig().getSystemProperty(AAD_TENANT, StringUtil.EMPTY);
        }
        return value;
    }

    /**
     * Gets the Entra ID authority URL from configuration.
     * Uses new entraid.authority key with fallback to legacy aad.authority.
     * @return The authority URL.
     */
    protected String getAuthority() {
        String value = ComponentUtil.getFessConfig().getSystemProperty(ENTRAID_AUTHORITY);
        if (StringUtil.isBlank(value)) {
            value = ComponentUtil.getFessConfig().getSystemProperty(AAD_AUTHORITY, DEFAULT_AUTHORITY);
        }
        if (StringUtil.isBlank(value)) {
            // getSystemProperty only falls back to the default when the key is absent, so a legacy
            // aad.authority that is present but empty arrives here as "". Left alone, getAuthUrl
            // would build a scheme-less URL and redirect the browser back inside Fess.
            return DEFAULT_AUTHORITY;
        }
        return value;
    }

    /**
     * Gets the state time-to-live from configuration.
     * Uses new entraid.state.ttl key with fallback to legacy aad.state.ttl.
     * @return The state TTL in seconds. removeExpiredStates compares it against an elapsed time
     *         that has already been divided by 1000.
     */
    protected long getStateTtl() {
        String value = ComponentUtil.getFessConfig().getSystemProperty(ENTRAID_STATE_TTL);
        if (StringUtil.isBlank(value)) {
            value = ComponentUtil.getFessConfig().getSystemProperty(AAD_STATE_TTL, DEFAULT_STATE_TTL);
        }
        try {
            return Long.parseLong(value.trim());
        } catch (final NumberFormatException e) {
            logger.warn("Invalid {}: {}. Using {} seconds.", ENTRAID_STATE_TTL, value, DEFAULT_STATE_TTL);
            return Long.parseLong(DEFAULT_STATE_TTL);
        }
    }

    /**
     * Gets the reply URL for Entra ID authentication.
     * Uses new entraid.reply.url key with fallback to legacy aad.reply.url.
     * @param request The HTTP servlet request.
     * @return The reply URL.
     */
    protected String getReplyUrl(final HttpServletRequest request) {
        String value = ComponentUtil.getFessConfig().getSystemProperty(ENTRAID_REPLY_URL);
        if (StringUtil.isBlank(value)) {
            value = ComponentUtil.getFessConfig().getSystemProperty(AAD_REPLY_URL, StringUtil.EMPTY);
        }
        if (StringUtil.isNotBlank(value)) {
            return value;
        }
        return request.getRequestURL().toString();
    }

    /**
     * Gets the OAuth2 response mode to ask the authorization endpoint for.
     *
     * <p>Defaults to {@code query}. Fess ships {@code tomcat.sameSiteCookies = lax}, and a Lax
     * cookie is not sent on the cross-site POST that {@code form_post} produces, so a form_post
     * callback arrives without JSESSIONID and the login loops. A deployment that sets
     * {@code tomcat.sameSiteCookies = none} can select {@code form_post} to keep the
     * authorization code out of the callback URL, and therefore out of browser history and any
     * front-end proxy log.
     *
     * @return Either {@code query} or {@code form_post}.
     */
    protected String getResponseMode() {
        String value = ComponentUtil.getFessConfig().getSystemProperty(ENTRAID_RESPONSE_MODE);
        if (StringUtil.isBlank(value)) {
            value = ComponentUtil.getFessConfig().getSystemProperty(AAD_RESPONSE_MODE, RESPONSE_MODE_QUERY);
        }
        if (StringUtil.isBlank(value)) {
            return RESPONSE_MODE_QUERY;
        }
        value = value.trim();
        if (RESPONSE_MODE_QUERY.equals(value) || RESPONSE_MODE_FORM_POST.equals(value)) {
            return value;
        }
        logger.warn("Invalid {}: {}. Using {}.", ENTRAID_RESPONSE_MODE, value, RESPONSE_MODE_QUERY);
        return RESPONSE_MODE_QUERY;
    }

    @Override
    public void resolveCredential(final LoginCredentialResolver resolver) {
        resolver.resolve(EntraIdCredential.class, credential -> OptionalEntity.of(credential.getUser()));
    }

    /**
     * Sets the token acquisition timeout.
     * @param acquisitionTimeout The timeout in milliseconds.
     */
    public void setAcquisitionTimeout(final long acquisitionTimeout) {
        this.acquisitionTimeout = acquisitionTimeout;
    }

    /**
     * Sets the group cache expiry time.
     * @param groupCacheExpiry The cache expiry time in seconds.
     */
    public void setGroupCacheExpiry(final long groupCacheExpiry) {
        this.groupCacheExpiry = groupCacheExpiry;
    }

    /**
     * Sets the maximum number of groups kept in the parent group cache.
     * @param maxGroupCacheSize The maximum number of cached groups.
     */
    public void setMaxGroupCacheSize(final int maxGroupCacheSize) {
        this.maxGroupCacheSize = maxGroupCacheSize;
    }

    /**
     * Sets the maximum group depth for nested group processing.
     * @param maxGroupDepth The maximum depth for nested groups.
     */
    public void setMaxGroupDepth(final int maxGroupDepth) {
        this.maxGroupDepth = maxGroupDepth;
    }

    @Override
    public ActionResponse getResponse(final SsoResponseType responseType) {
        return null;
    }

    @Override
    public String logout(final FessUserBean user) {
        // The client application is shared for the whole server so that its token cache survives
        // between a login and its refresh. MSAL4J's TokenCache is a set of unbounded LinkedHashMaps
        // with no eviction, and removeAccount() is the only way anything leaves it, so a user who
        // logs out has to be dropped explicitly or they stay resident until the JVM restarts.
        if (user.getFessUser() instanceof final EntraIdUser entraIdUser) {
            final IAuthenticationResult authResult = entraIdUser.getAuthenticationResult();
            if (authResult != null && authResult.account() != null) {
                removeAccount(authResult.account());
            }
        }
        // Null keeps the existing behaviour: Fess does not sign the user out at Entra ID.
        return null;
    }

    /**
     * Drops an account's tokens from the shared client application's cache.
     *
     * @param account The account to evict.
     */
    protected void removeAccount(final IAccount account) {
        try {
            getClientApplication().removeAccount(account).join();
            if (logger.isDebugEnabled()) {
                logger.debug("Removed an account from the token cache.");
            }
        } catch (final Exception e) {
            // Logging out must not fail because the cache could not be pruned.
            logger.warn("Failed to remove an account from the Entra ID token cache.", e);
        }
    }

    /**
     * Kept only so an out-of-tree {@code fess_sso+entraidAuthenticator.xml} that still sets this
     * property keeps loading. The value is ignored.
     *
     * <p>The v1.0 endpoint is not supported. msal4j hardcodes {@code oauth2/v2.0/token} as the
     * token endpoint of an AAD authority and offers no v1 alternative, so an authorization code
     * minted at {@code /oauth2/authorize} could never be redeemed -- the v1.0 branch this used to
     * select produced a login that always failed.
     *
     * @param useV2Endpoint Ignored.
     * @deprecated The v1.0 endpoint is unsupported; the authorization request is always v2.0.
     */
    @Deprecated
    public void setUseV2Endpoint(final boolean useV2Endpoint) {
        if (!useV2Endpoint) {
            logger.warn("useV2Endpoint=false is ignored. The Entra ID v1.0 endpoint is not supported:"
                    + " msal4j only redeems authorization codes at the v2.0 token endpoint.");
        }
    }
}
