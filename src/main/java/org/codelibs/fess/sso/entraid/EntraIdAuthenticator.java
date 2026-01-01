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
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.curl.Curl;
import org.codelibs.curl.CurlResponse;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.base.login.EntraIdCredential;
import org.codelibs.fess.app.web.base.login.EntraIdCredential.EntraIdUser;
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
import org.dbflute.optional.OptionalThing;
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

    /** Configuration key for Entra ID default groups. */
    protected static final String ENTRAID_DEFAULT_GROUPS = "entraid.default.groups";

    /** Configuration key for Entra ID default roles. */
    protected static final String ENTRAID_DEFAULT_ROLES = "entraid.default.roles";

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

    /** Timeout for token acquisition in milliseconds. */
    protected long acquisitionTimeout = 30 * 1000L;

    /** Cache for storing group information to reduce API calls. */
    protected Cache<String, Pair<String[], String[]>> groupCache;

    /** Group cache expiry time in seconds. */
    protected long groupCacheExpiry = 10 * 60L;

    /** Maximum depth for processing nested groups to prevent infinite loops. */
    protected int maxGroupDepth = 10;

    /** Use V2 endpoint. */
    protected boolean useV2Endpoint = true;

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
        groupCache = CacheBuilder.newBuilder().expireAfterWrite(groupCacheExpiry, TimeUnit.SECONDS).build();
    }

    @Override
    public LoginCredential getLoginCredential() {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Logging in with Entra ID Authenticator");
            }
            final HttpSession session = request.getSession(false);
            if (session != null && containsAuthenticationData(request)) {
                try {
                    return processAuthenticationData(request);
                } catch (final Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to process a login request on Entra ID.", e);
                    }
                }
                return null;
            }

            return new ActionResponseCredential(() -> HtmlResponse.fromRedirectPathAsIs(getAuthUrl(request)));
        }).orElse(null);
    }

    /**
     * Generates the Entra ID authorization URL for the authentication request.
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
            logger.debug("Storing state in session: {}", stateData);
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
        try {
            final JWTClaimsSet claimsSet = JWTParser.parse(idToken).getJWTClaimsSet();
            if (claimsSet == null) {
                throw new SsoLoginException("could not validate nonce");
            }

            final String nonce = (String) claimsSet.getClaim("nonce");
            if (logger.isDebugEnabled()) {
                logger.debug("nonce={}", nonce);
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
            logger.debug("authority={}", authority);
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
     * @param authorizationCode The authorization code received from Entra ID.
     * @param currentUri The current URI for the redirect.
     * @return The authentication result containing the access token.
     */
    protected IAuthenticationResult getAccessToken(final AuthorizationCode authorizationCode, final String currentUri) {
        final String authority = getAuthority() + getTenant() + "/";
        final String authCode = authorizationCode.getValue();
        if (logger.isDebugEnabled()) {
            logger.debug("authority={}, uri={}", authority, currentUri);
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
     * @param user The Entra ID user whose tokens need to be refreshed.
     * @return The new authentication result, or null if silent refresh failed.
     */
    public IAuthenticationResult refreshTokenSilently(final EntraIdCredential.EntraIdUser user) {
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
                            logger.debug("Removing old state: {}", s);
                        }
                        states.remove(s);
                    });
            final StateData stateData = states.get(state);
            if (stateData != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Restoring state from session: {}", stateData);
                }
                states.remove(state);
                return stateData;
            }
        }
        return null;
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
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        final Map<String, String[]> params = request.getParameterMap();
        if (logger.isDebugEnabled()) {
            logger.debug("params={}", params);
        }
        return params.containsKey(ERROR) || params.containsKey(ID_TOKEN) || params.containsKey(CODE);
    }

    /**
     * Updates the user's group and role membership information with lazy loading for parent groups.
     * Direct groups are retrieved synchronously, while parent groups are fetched asynchronously
     * to avoid login delays when users have many nested group memberships.
     * @param user The Entra ID user to update.
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
        processDirectMemberOf(user, groupList, roleList, groupIdsForParentLookup, "https://graph.microsoft.com/v1.0/me/memberOf");

        if (logger.isDebugEnabled()) {
            logger.debug("[updateMemberOf] Direct groups retrieved. Total groups: {}, Total roles: {}, Group IDs for parent lookup: {}",
                    groupList.size(), roleList.size(), groupIdsForParentLookup.size());
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
     * Processes member-of information from Microsoft Graph API.
     * @param user The Entra ID user.
     * @param groupList The list to add group names to.
     * @param roleList The list to add role names to.
     * @param url The Microsoft Graph API URL.
     */
    protected void processMemberOf(final EntraIdUser user, final List<String> groupList, final List<String> roleList, final String url) {
        if (logger.isDebugEnabled()) {
            logger.debug("url={}", url);
        }
        try (CurlResponse response = Curl.get(url)
                .header("Authorization", "Bearer " + user.getAuthenticationResult().accessToken())
                .header("Accept", "application/json")
                .execute()) {
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
                        } else if (memberType.contains("role")) {
                            roleList.add(id);
                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Unknown @odata.type: {}", memberOf);
                            }
                            groupList.add(id);
                        }
                        processParentGroup(user, groupList, roleList, id);
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
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Unknown @odata.type: {}", memberOf);
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
            logger.warn("Failed to access groups/roles in Entra ID.", e);
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
     */
    protected void processDirectMemberOf(final EntraIdUser user, final List<String> groupList, final List<String> roleList,
            final List<String> groupIdsForParentLookup, final String url) {
        if (logger.isDebugEnabled()) {
            logger.debug("[processDirectMemberOf] Fetching direct memberships from URL: {}", url);
        }
        try (CurlResponse response = Curl.get(url)
                .header("Authorization", "Bearer " + user.getAuthenticationResult().accessToken())
                .header("Accept", "application/json")
                .execute()) {
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
                    processDirectMemberOf(user, groupList, roleList, groupIdsForParentLookup, nextLink);
                }
            } else if (contentMap.containsKey("error")) {
                logger.warn("Failed to access groups/roles: {}", contentMap);
            }
        } catch (final IOException e) {
            logger.warn("Failed to access groups/roles in Entra ID.", e);
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
     * Retrieves parent group information for the specified group ID.
     * @param user The Entra ID user.
     * @param id The group ID to get parent information for.
     * @return A pair containing group names and role names.
     */
    protected Pair<String[], String[]> getParentGroup(final EntraIdUser user, final String id) {
        return getParentGroup(user, id, 0);
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
        try {
            return groupCache.get(id, () -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("[getParentGroup] Loading parent groups for id: {} into cache", id);
                }
                final List<String> groupList = new ArrayList<>();
                final List<String> roleList = new ArrayList<>();
                final String url = "https://graph.microsoft.com/v1.0/groups/" + id + "/getMemberGroups";
                if (logger.isDebugEnabled()) {
                    logger.debug("[getParentGroup] Calling API: {}", url);
                }
                try (CurlResponse response = Curl.post(url)
                        .header("Authorization", "Bearer " + user.getAuthenticationResult().accessToken())
                        .header("Accept", "application/json")
                        .header("Content-type", "application/json")
                        .body("{\"securityEnabledOnly\":false}")
                        .execute()) {
                    final Map<String, Object> contentMap = response.getContent(OpenSearchCurl.jsonParser());
                    if (logger.isDebugEnabled()) {
                        logger.debug("[getParentGroup] Response for id {}: {}", id, contentMap);
                    }
                    if (contentMap.containsKey("value")) {
                        final String[] values = DocumentUtil.getValue(contentMap, "value", String[].class);
                        if (values != null) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("[getParentGroup] Found {} parent group IDs for id: {}", values.length, id);
                            }
                            for (final String value : values) {
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
                        }
                    } else if (contentMap.containsKey("error")) {
                        @SuppressWarnings("unchecked")
                        final Map<String, Object> errorMap = (Map<String, Object>) contentMap.get("error");
                        if ("Request_ResourceNotFound".equals(errorMap.get("code"))) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("[getParentGroup] Resource not found for id {}: {}", id, contentMap);
                            }
                        } else {
                            logger.warn("Failed to access parent groups for id {}: {}", id, contentMap);
                        }
                    }
                } catch (final IOException e) {
                    logger.warn("Failed to access groups/roles in Entra ID for id: {}", id, e);
                }
                final Pair<String[], String[]> result = new Pair<>(groupList.stream().distinct().toArray(n1 -> new String[n1]),
                        roleList.stream().distinct().toArray(n2 -> new String[n2]));
                if (logger.isDebugEnabled()) {
                    logger.debug("[getParentGroup] Cached result for id {}: {} groups, {} roles", id, result.getFirst().length,
                            result.getSecond().length);
                }
                return result;
            });
        } catch (final ExecutionException e) {
            logger.warn("Failed to process group cache for id: {}", id, e);
            return new Pair<>(StringUtil.EMPTY_STRINGS, StringUtil.EMPTY_STRINGS);
        }
    }

    /**
     * Processes individual group information.
     * @param user The Entra ID user.
     * @param groupList The list to add group names to.
     * @param roleList The list to add role names to.
     * @param id The group ID to process.
     */
    protected void processGroup(final EntraIdUser user, final List<String> groupList, final List<String> roleList, final String id) {
        if (logger.isDebugEnabled()) {
            logger.debug("[processGroup] Processing group info for id: {}", id);
        }
        final String url = "https://graph.microsoft.com/v1.0/groups/" + id;
        if (logger.isDebugEnabled()) {
            logger.debug("[processGroup] Fetching from url: {}", url);
        }
        try (CurlResponse response = Curl.get(url)
                .header("Authorization", "Bearer " + user.getAuthenticationResult().accessToken())
                .header("Accept", "application/json")
                .execute()) {
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
        } catch (final IOException e) {
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
            value = ComponentUtil.getFessConfig().getSystemProperty(AAD_AUTHORITY, "https://login.microsoftonline.com/");
        }
        return value;
    }

    /**
     * Gets the state time-to-live from configuration.
     * Uses new entraid.state.ttl key with fallback to legacy aad.state.ttl.
     * @return The state TTL in milliseconds.
     */
    protected long getStateTtl() {
        String value = ComponentUtil.getFessConfig().getSystemProperty(ENTRAID_STATE_TTL);
        if (StringUtil.isBlank(value)) {
            value = ComponentUtil.getFessConfig().getSystemProperty(AAD_STATE_TTL, "3600");
        }
        return Long.parseLong(value);
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
