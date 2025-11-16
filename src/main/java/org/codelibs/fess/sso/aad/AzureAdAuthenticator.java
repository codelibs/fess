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
package org.codelibs.fess.sso.aad;

import static org.codelibs.core.stream.StreamUtil.split;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.core.net.UuidUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.curl.Curl;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.base.login.AzureAdCredential;
import org.codelibs.fess.app.web.base.login.AzureAdCredential.AzureAdUser;
import org.codelibs.fess.app.web.base.login.FessLoginAssist.LoginCredentialResolver;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.exception.SsoLoginException;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.sso.SsoAuthenticator;
import org.codelibs.fess.sso.SsoResponseType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.opensearch.runner.net.OpenSearchCurl;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.util.LaRequestUtil;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
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
 * Azure Active Directory (Azure AD) SSO authenticator implementation.
 * Handles OAuth2/OpenID Connect authentication flow with Azure AD.
 */
public class AzureAdAuthenticator implements SsoAuthenticator {

    private static final Logger logger = LogManager.getLogger(AzureAdAuthenticator.class);

    /**
     * Default constructor for AzureAdAuthenticator.
     */
    public AzureAdAuthenticator() {
        // Default constructor
    }

    /** Configuration key for Azure AD state time-to-live. */
    protected static final String AZUREAD_STATE_TTL = "aad.state.ttl";

    /** Configuration key for Azure AD authority URL. */
    protected static final String AZUREAD_AUTHORITY = "aad.authority";

    /** Configuration key for Azure AD tenant ID. */
    protected static final String AZUREAD_TENANT = "aad.tenant";

    /** Configuration key for Azure AD client secret. */
    protected static final String AZUREAD_CLIENT_SECRET = "aad.client.secret";

    /** Configuration key for Azure AD client ID. */
    protected static final String AZUREAD_CLIENT_ID = "aad.client.id";

    /** Configuration key for Azure AD reply URL. */
    protected static final String AZUREAD_REPLY_URL = "aad.reply.url";

    /** Session attribute key for storing Azure AD states. */
    protected static final String STATES = "aadStates";

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

    /** Timeout for token acquisition in milliseconds. */
    protected long acquisitionTimeout = 30 * 1000L;

    /** Cache for storing group information to reduce API calls. */
    protected Cache<String, Pair<String[], String[]>> groupCache;

    /** Group cache expiry time in seconds. */
    protected long groupCacheExpiry = 10 * 60L;

    /** HTTP request timeout in milliseconds. */
    protected int httpRequestTimeout = 10 * 1000;

    /** Maximum depth for processing nested groups to prevent infinite loops. */
    protected int maxGroupDepth = 10;

    /** Use V2 endpoint. */
    protected boolean useV2Endpoint = true;

    /**
     * Initializes the Azure AD authenticator.
     * Registers this authenticator with the SSO manager and sets up group cache.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        ComponentUtil.getSsoManager().register(this);
        groupCache = CacheBuilder.newBuilder().expireAfterWrite(groupCacheExpiry, TimeUnit.SECONDS).build();
    }

    @Override
    public LoginCredential getLoginCredential() {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Logging in with Azure AD Authenticator");
            }
            final HttpSession session = request.getSession(false);
            if (session != null && containsAuthenticationData(request)) {
                try {
                    return processAuthenticationData(request);
                } catch (final Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to process a login request on AzureAD.", e);
                    }
                }
                return null;
            }

            return new ActionResponseCredential(() -> HtmlResponse.fromRedirectPathAsIs(getAuthUrl(request)));
        }).orElse(null);
    }

    /**
     * Generates the Azure AD authorization URL for the authentication request.
     * @param request The HTTP servlet request.
     * @return The authorization URL to redirect the user to.
     */
    protected String getAuthUrl(final HttpServletRequest request) {
        final String state = UuidUtil.create();
        final String nonce = UuidUtil.create();
        storeStateInSession(request.getSession(), state, nonce);
        final String authUrl;

        if (useV2Endpoint) {
            // v2.0 endpoint with MSAL4J (recommended)
            authUrl = getAuthority() + getTenant()
                    + "/oauth2/v2.0/authorize?response_type=code&scope=https://graph.microsoft.com/.default&response_mode=form_post&redirect_uri="
                    + URLEncoder.encode(getReplyUrl(request), Constants.UTF_8_CHARSET) + "&client_id=" + getClientId() + "&state=" + state
                    + "&nonce=" + nonce;
        } else {
            // v1.0 endpoint for backward compatibility
            authUrl = getAuthority() + getTenant()
                    + "/oauth2/authorize?response_type=code&scope=directory.read.all&response_mode=form_post&redirect_uri="
                    + URLEncoder.encode(getReplyUrl(request), Constants.UTF_8_CHARSET) + "&client_id=" + getClientId()
                    + "&resource=https%3a%2f%2fgraph.microsoft.com" + "&state=" + state + "&nonce=" + nonce;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("redirect to: {} (using {} endpoint)", authUrl, useV2Endpoint ? "v2.0" : "v1.0");
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
        @SuppressWarnings("unchecked")
        Map<String, StateData> stateMap = (Map<String, StateData>) session.getAttribute(STATES);
        if (stateMap == null) {
            stateMap = new HashMap<>();
            session.setAttribute(STATES, stateMap);
        }
        final StateData stateData = new StateData(nonce, ComponentUtil.getSystemHelper().getCurrentTimeAsLong());
        if (logger.isDebugEnabled()) {
            logger.debug("store {} in session", stateData);
        }
        stateMap.put(state, stateData);
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
            logger.debug("process authentication: url: {}, params: {}", urlBuf, params);
        }

        // validate that state in response equals to state in request
        final StateData stateData = validateState(request.getSession(), params.containsKey(STATE) ? params.get(STATE).get(0) : null);
        if (logger.isDebugEnabled()) {
            logger.debug("load {}", stateData);
        }

        final AuthenticationResponse authResponse = parseAuthenticationResponse(urlBuf.toString(), params);
        if (authResponse instanceof final AuthenticationSuccessResponse oidcResponse) {
            validateAuthRespMatchesCodeFlow(oidcResponse);
            final IAuthenticationResult authData = getAccessToken(oidcResponse.getAuthorizationCode(), getReplyUrl(request));
            validateNonce(stateData, authData);

            return new AzureAdCredential(authData);
        }
        final AuthenticationErrorResponse oidcResponse = (AuthenticationErrorResponse) authResponse;
        throw new SsoLoginException(String.format("Request for auth code failed: %s - %s", oidcResponse.getErrorObject().getCode(),
                oidcResponse.getErrorObject().getDescription()));
    }

    /**
     * Parses the authentication response from Azure AD.
     * @param url The response URL.
     * @param params The response parameters.
     * @return The parsed authentication response.
     */
    protected AuthenticationResponse parseAuthenticationResponse(final String url, final Map<String, List<String>> params) {
        if (logger.isDebugEnabled()) {
            logger.debug("Parse: {} : {}", url, params);
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
            logger.debug("idToken: {}", idToken);
        }
        try {
            final JWTClaimsSet claimsSet = JWTParser.parse(idToken).getJWTClaimsSet();
            if (claimsSet == null) {
                throw new SsoLoginException("could not validate nonce");
            }

            final String nonce = (String) claimsSet.getClaim("nonce");
            if (logger.isDebugEnabled()) {
                logger.debug("nonce: {}", nonce);
            }
            if (StringUtils.isEmpty(nonce) || !nonce.equals(stateData.getNonce())) {
                throw new SsoLoginException("could not validate nonce");
            }
        } catch (final SsoLoginException e) {
            throw e;
        } catch (final Exception e) {
            throw new SsoLoginException("could not validate nonce", e);
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
            logger.debug("refreshToken: {}, authority: {}", refreshToken, authority);
        }
        try {
            final ConfidentialClientApplication app = ConfidentialClientApplication
                    .builder(getClientId(), com.microsoft.aad.msal4j.ClientCredentialFactory.createFromSecret(getClientSecret()))
                    .authority(authority)
                    .build();

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
     * @param authorizationCode The authorization code received from Azure AD.
     * @param currentUri The current URI for the redirect.
     * @return The authentication result containing the access token.
     */
    protected IAuthenticationResult getAccessToken(final AuthorizationCode authorizationCode, final String currentUri) {
        final String authority = getAuthority() + getTenant() + "/";
        final String authCode = authorizationCode.getValue();
        if (logger.isDebugEnabled()) {
            logger.debug("authCode: {}, authority: {}, uri: {}", authCode, authority, currentUri);
        }
        try {
            final ConfidentialClientApplication app = ConfidentialClientApplication
                    .builder(getClientId(), com.microsoft.aad.msal4j.ClientCredentialFactory.createFromSecret(getClientSecret()))
                    .authority(authority)
                    .build();

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
     * @param user The Azure AD user whose tokens need to be refreshed.
     * @return The new authentication result, or null if silent refresh failed.
     */
    public IAuthenticationResult refreshTokenSilently(final AzureAdCredential.AzureAdUser user) {
        final String authority = getAuthority() + getTenant() + "/";
        try {
            final ConfidentialClientApplication app = ConfidentialClientApplication
                    .builder(getClientId(), com.microsoft.aad.msal4j.ClientCredentialFactory.createFromSecret(getClientSecret()))
                    .authority(authority)
                    .build();

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
        throw new SsoLoginException("could not validate state");
    }

    /**
     * Removes and returns state data from the HTTP session.
     * @param session The HTTP session.
     * @param state The state parameter to remove.
     * @return The removed state data or null if not found.
     */
    protected StateData removeStateFromSession(final HttpSession session, final String state) {
        @SuppressWarnings("unchecked")
        final Map<String, StateData> states = (Map<String, StateData>) session.getAttribute(STATES);
        if (states != null) {
            final long now = ComponentUtil.getSystemHelper().getCurrentTimeAsLong();
            states.entrySet()
                    .stream()
                    .filter(e -> (now - e.getValue().getExpiration()) / 1000L > getStateTtl())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList())
                    .forEach(s -> {
                        if (logger.isDebugEnabled()) {
                            logger.debug("remove old state: {}", s);
                        }
                        states.remove(s);
                    });
            final StateData stateData = states.get(state);
            if (stateData != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("restore {} from session", stateData);
                }
                states.remove(state);
                return stateData;
            }
        }
        return null;
    }

    /**
     * Checks if the request contains authentication data from Azure AD.
     * @param request The HTTP servlet request to check.
     * @return True if authentication data is present, false otherwise.
     */
    protected boolean containsAuthenticationData(final HttpServletRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("HTTP Method: {}", request.getMethod());
        }
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        final Map<String, String[]> params = request.getParameterMap();
        if (logger.isDebugEnabled()) {
            logger.debug("params: {}", params);
        }
        return params.containsKey(ERROR) || params.containsKey(ID_TOKEN) || params.containsKey(CODE);
    }

    /**
     * Updates the user's group and role membership information.
     * @param user The Azure AD user to update.
     */
    public void updateMemberOf(final AzureAdUser user) {
        final List<String> groupList = new ArrayList<>();
        final List<String> roleList = new ArrayList<>();
        groupList.addAll(getDefaultGroupList());
        roleList.addAll(getDefaultRoleList());
        processMemberOf(user, groupList, roleList, "https://graph.microsoft.com/v1.0/me/memberOf");
        user.setGroups(groupList.stream().distinct().toArray(n -> new String[n]));
        user.setRoles(roleList.stream().distinct().toArray(n -> new String[n]));
    }

    /**
     * Processes member-of information from Microsoft Graph API.
     * @param user The Azure AD user.
     * @param groupList The list to add group names to.
     * @param roleList The list to add role names to.
     * @param url The Microsoft Graph API URL.
     */
    protected void processMemberOf(final AzureAdUser user, final List<String> groupList, final List<String> roleList, final String url) {
        if (logger.isDebugEnabled()) {
            logger.debug("url: {}", url);
        }
        try (CurlResponse response = Curl.get(url)
                .header("Authorization", "Bearer " + user.getAuthenticationResult().accessToken())
                .header("Accept", "application/json")
                .timeout(httpRequestTimeout)
                .execute()) {
            final Map<String, Object> contentMap = response.getContent(OpenSearchCurl.jsonParser());
            if (logger.isDebugEnabled()) {
                logger.debug("response: {}", contentMap);
            }
            if (contentMap.containsKey("value")) {
                @SuppressWarnings("unchecked")
                final List<Map<String, Object>> memberOfList = (List<Map<String, Object>>) contentMap.get("value");
                final FessConfig fessConfig = ComponentUtil.getFessConfig();
                for (final Map<String, Object> memberOf : memberOfList) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("member: {}", memberOf);
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
                        } else if (memberType.contains("role")) {
                            roleList.add(id);
                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("unknown @odata.type: {}", memberOf);
                            }
                            groupList.add(id);
                        }
                        processParentGroup(user, groupList, roleList, id);
                    } else {
                        logger.warn("id is empty: {}", memberOf);
                    }
                    final String[] names = fessConfig.getAzureAdPermissionFields();
                    final boolean useDomainServices = fessConfig.isAzureAdUseDomainServices();
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
                                if (logger.isDebugEnabled()) {
                                    logger.debug("unknown @odata.type: {}", memberOf);
                                }
                                addGroupOrRoleName(groupList, value, useDomainServices);
                            }
                        } else if (logger.isDebugEnabled()) {
                            logger.debug("{} is empty: {}", name, memberOf);
                        }
                    }
                }
                final String nextLink = (String) contentMap.get("@odata.nextLink");
                if (StringUtil.isNotBlank(nextLink)) {
                    processMemberOf(user, groupList, roleList, nextLink);
                }
            } else if (contentMap.containsKey("error")) {
                logger.warn("Failed to access groups/roles: {}", contentMap);
            }
        } catch (final IOException e) {
            logger.warn("Failed to access groups/roles in AzureAD.", e);
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
     * Processes parent group information for nested groups.
     * @param user The Azure AD user.
     * @param groupList The list to add group names to.
     * @param roleList The list to add role names to.
     * @param id The group ID to process.
     */
    protected void processParentGroup(final AzureAdUser user, final List<String> groupList, final List<String> roleList, final String id) {
        processParentGroup(user, groupList, roleList, id, 0);
    }

    /**
     * Processes parent group information for nested groups with depth tracking.
     * @param user The Azure AD user.
     * @param groupList The list to add group names to.
     * @param roleList The list to add role names to.
     * @param id The group ID to process.
     * @param depth The current recursion depth.
     */
    protected void processParentGroup(final AzureAdUser user, final List<String> groupList, final List<String> roleList, final String id,
            final int depth) {
        if (depth >= maxGroupDepth) {
            if (logger.isDebugEnabled()) {
                logger.debug("Maximum group depth {} reached for group {}", maxGroupDepth, id);
            }
            return;
        }
        final Pair<String[], String[]> groupsAndRoles = getParentGroup(user, id, depth);
        StreamUtil.stream(groupsAndRoles.getFirst()).of(stream -> stream.forEach(groupList::add));
        StreamUtil.stream(groupsAndRoles.getSecond()).of(stream -> stream.forEach(roleList::add));
    }

    /**
     * Retrieves parent group information for the specified group ID.
     * @param user The Azure AD user.
     * @param id The group ID to get parent information for.
     * @return A pair containing group names and role names.
     */
    protected Pair<String[], String[]> getParentGroup(final AzureAdUser user, final String id) {
        return getParentGroup(user, id, 0);
    }

    /**
     * Retrieves parent group information for the specified group ID with depth tracking.
     * @param user The Azure AD user.
     * @param id The group ID to get parent information for.
     * @param depth The current recursion depth.
     * @return A pair containing group names and role names.
     */
    protected Pair<String[], String[]> getParentGroup(final AzureAdUser user, final String id, final int depth) {
        if (depth >= maxGroupDepth) {
            if (logger.isDebugEnabled()) {
                logger.debug("Maximum group depth {} reached for group {}", maxGroupDepth, id);
            }
            return new Pair<>(StringUtil.EMPTY_STRINGS, StringUtil.EMPTY_STRINGS);
        }
        try {
            return groupCache.get(id, () -> {
                final List<String> groupList = new ArrayList<>();
                final List<String> roleList = new ArrayList<>();
                final String url = "https://graph.microsoft.com/v1.0/groups/" + id + "/getMemberGroups";
                if (logger.isDebugEnabled()) {
                    logger.debug("url: {}", url);
                }
                try (CurlResponse response = Curl.post(url)
                        .header("Authorization", "Bearer " + user.getAuthenticationResult().accessToken())
                        .header("Accept", "application/json")
                        .header("Content-type", "application/json")
                        .body("{\"securityEnabledOnly\":false}")
                        .timeout(httpRequestTimeout)
                        .execute()) {
                    final Map<String, Object> contentMap = response.getContent(OpenSearchCurl.jsonParser());
                    if (logger.isDebugEnabled()) {
                        logger.debug("response: {}", contentMap);
                    }
                    if (contentMap.containsKey("value")) {
                        final String[] values = DocumentUtil.getValue(contentMap, "value", String[].class);
                        if (values != null) {
                            for (final String value : values) {
                                processGroup(user, groupList, roleList, value);
                                if (!groupList.contains(value) && !roleList.contains(value)) {
                                    final Pair<String[], String[]> groupsAndRoles = getParentGroup(user, value, depth + 1);
                                    StreamUtil.stream(groupsAndRoles.getFirst()).of(stream1 -> stream1.forEach(groupList::add));
                                    StreamUtil.stream(groupsAndRoles.getSecond()).of(stream2 -> stream2.forEach(roleList::add));
                                }
                            }
                        }
                    } else if (contentMap.containsKey("error")) {
                        @SuppressWarnings("unchecked")
                        final Map<String, Object> errorMap = (Map<String, Object>) contentMap.get("error");
                        if ("Request_ResourceNotFound".equals(errorMap.get("code"))) {
                            logger.debug("Failed to access parent groups: {}", contentMap);
                        } else {
                            logger.warn("Failed to access parent groups: {}", contentMap);
                        }
                    }
                } catch (final IOException e) {
                    logger.warn("Failed to access groups/roles in AzureAD.", e);
                }
                return new Pair<>(groupList.stream().distinct().toArray(n1 -> new String[n1]),
                        roleList.stream().distinct().toArray(n2 -> new String[n2]));
            });
        } catch (final ExecutionException e) {
            logger.warn("Failed to process a group cache.", e);
            return new Pair<>(StringUtil.EMPTY_STRINGS, StringUtil.EMPTY_STRINGS);
        }
    }

    /**
     * Processes individual group information.
     * @param user The Azure AD user.
     * @param groupList The list to add group names to.
     * @param roleList The list to add role names to.
     * @param id The group ID to process.
     */
    protected void processGroup(final AzureAdUser user, final List<String> groupList, final List<String> roleList, final String id) {
        final String url = "https://graph.microsoft.com/v1.0/groups/" + id;
        if (logger.isDebugEnabled()) {
            logger.debug("url: {}", url);
        }
        try (CurlResponse response = Curl.get(url)
                .header("Authorization", "Bearer " + user.getAuthenticationResult().accessToken())
                .header("Accept", "application/json")
                .timeout(httpRequestTimeout)
                .execute()) {
            final Map<String, Object> contentMap = response.getContent(OpenSearchCurl.jsonParser());
            if (logger.isDebugEnabled()) {
                logger.debug("response: {}", contentMap);
            }
            groupList.add(id);
            if (contentMap.containsKey("error")) {
                logger.warn("Failed to access parent groups: {}", contentMap);
            } else {
                final FessConfig fessConfig = ComponentUtil.getFessConfig();
                final String[] names = fessConfig.getAzureAdPermissionFields();
                for (final String name : names) {
                    final String value = (String) contentMap.get(name);
                    if (StringUtil.isNotBlank(value)) {
                        groupList.add(value);
                    } else if (logger.isDebugEnabled()) {
                        logger.debug("{} is empty: {}", name, id);
                    }
                }
            }
        } catch (final IOException e) {
            logger.warn("Failed to access groups/roles in AzureAD.", e);
        }
    }

    /**
     * Gets the default group list for users.
     * @return The default group list.
     */
    protected List<String> getDefaultGroupList() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty("aad.default.groups");
        if (StringUtil.isBlank(value)) {
            return Collections.emptyList();
        }
        return split(value, ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toList()));
    }

    /**
     * Gets the default role list for users.
     * @return The default role list.
     */
    protected List<String> getDefaultRoleList() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty("aad.default.roles");
        if (StringUtil.isBlank(value)) {
            return Collections.emptyList();
        }
        return split(value, ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toList()));
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
     * Gets the Azure AD client ID from configuration.
     * @return The client ID.
     */
    protected String getClientId() {
        return ComponentUtil.getFessConfig().getSystemProperty(AZUREAD_CLIENT_ID, StringUtil.EMPTY);
    }

    /**
     * Gets the Azure AD client secret from configuration.
     * @return The client secret.
     */
    protected String getClientSecret() {
        return ComponentUtil.getFessConfig().getSystemProperty(AZUREAD_CLIENT_SECRET, StringUtil.EMPTY);
    }

    /**
     * Gets the Azure AD tenant ID from configuration.
     * @return The tenant ID.
     */
    protected String getTenant() {
        return ComponentUtil.getFessConfig().getSystemProperty(AZUREAD_TENANT, StringUtil.EMPTY);
    }

    /**
     * Gets the Azure AD authority URL from configuration.
     * @return The authority URL.
     */
    protected String getAuthority() {
        return ComponentUtil.getFessConfig().getSystemProperty(AZUREAD_AUTHORITY, "https://login.microsoftonline.com/");
    }

    /**
     * Gets the state time-to-live from configuration.
     * @return The state TTL in milliseconds.
     */
    protected long getStateTtl() {
        return Long.parseLong(ComponentUtil.getFessConfig().getSystemProperty(AZUREAD_STATE_TTL, "3600"));
    }

    /**
     * Gets the reply URL for Azure AD authentication.
     * @param request The HTTP servlet request.
     * @return The reply URL.
     */
    protected String getReplyUrl(final HttpServletRequest request) {
        final String value = ComponentUtil.getFessConfig().getSystemProperty(AZUREAD_REPLY_URL, StringUtil.EMPTY);
        if (StringUtil.isNotBlank(value)) {
            return value;
        }
        return request.getRequestURL().toString();
    }

    @Override
    public void resolveCredential(final LoginCredentialResolver resolver) {
        resolver.resolve(AzureAdCredential.class, credential -> OptionalEntity.of(credential.getUser()));
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
     * Sets the HTTP request timeout.
     * @param httpRequestTimeout The HTTP request timeout in milliseconds.
     */
    public void setHttpRequestTimeout(final int httpRequestTimeout) {
        this.httpRequestTimeout = httpRequestTimeout;
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
        return null;
    }

    /**
     * Enable to use V2 endpoint.
     * @param useV2Endpoint true if using V2 endpoint.
     */
    public void setUseV2Endpoint(final boolean useV2Endpoint) {
        this.useV2Endpoint = useV2Endpoint;
    }
}
