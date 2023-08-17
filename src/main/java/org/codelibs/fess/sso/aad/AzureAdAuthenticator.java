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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;

public class AzureAdAuthenticator implements SsoAuthenticator {

    private static final Logger logger = LogManager.getLogger(AzureAdAuthenticator.class);

    protected static final String AZUREAD_STATE_TTL = "aad.state.ttl";

    protected static final String AZUREAD_AUTHORITY = "aad.authority";

    protected static final String AZUREAD_TENANT = "aad.tenant";

    protected static final String AZUREAD_CLIENT_SECRET = "aad.client.secret";

    protected static final String AZUREAD_CLIENT_ID = "aad.client.id";

    protected static final String AZUREAD_REPLY_URL = "aad.reply.url";

    protected static final String STATES = "aadStates";

    protected static final String STATE = "state";

    protected static final String ERROR = "error";

    protected static final String ERROR_DESCRIPTION = "error_description";

    protected static final String ERROR_URI = "error_uri";

    protected static final String ID_TOKEN = "id_token";

    protected static final String CODE = "code";

    protected long acquisitionTimeout = 30 * 1000L;

    protected Cache<String, Pair<String[], String[]>> groupCache;

    protected long groupCacheExpiry = 10 * 60L;

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

    protected String getAuthUrl(final HttpServletRequest request) {
        final String state = UuidUtil.create();
        final String nonce = UuidUtil.create();
        storeStateInSession(request.getSession(), state, nonce);
        final String authUrl = getAuthority() + getTenant()
                + "/oauth2/authorize?response_type=code&scope=directory.read.all&response_mode=form_post&redirect_uri="
                + URLEncoder.encode(getReplyUrl(request), Constants.UTF_8_CHARSET) + "&client_id=" + getClientId()
                + "&resource=https%3a%2f%2fgraph.microsoft.com" + "&state=" + state + "&nonce=" + nonce;
        if (logger.isDebugEnabled()) {
            logger.debug("redirect to: {}", authUrl);
        }
        return authUrl;

    }

    protected void storeStateInSession(final HttpSession session, final String state, final String nonce) {
        @SuppressWarnings("unchecked")
        Map<String, StateData> stateMap = (Map<String, StateData>) session.getAttribute(STATES);
        if (stateMap == null) {
            stateMap = new HashMap<>();
            session.setAttribute(STATES, stateMap);
        }
        final StateData stateData = new StateData(nonce, System.currentTimeMillis());
        if (logger.isDebugEnabled()) {
            logger.debug("store {} in session", stateData);
        }
        stateMap.put(state, stateData);
    }

    protected LoginCredential processAuthenticationData(final HttpServletRequest request) {
        final StringBuffer urlBuf = request.getRequestURL();
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
            final AuthenticationResult authData = getAccessToken(oidcResponse.getAuthorizationCode(), getReplyUrl(request));
            validateNonce(stateData, authData);

            return new AzureAdCredential(authData);
        }
        final AuthenticationErrorResponse oidcResponse = (AuthenticationErrorResponse) authResponse;
        throw new SsoLoginException(String.format("Request for auth code failed: %s - %s", oidcResponse.getErrorObject().getCode(),
                oidcResponse.getErrorObject().getDescription()));
    }

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

    protected void validateNonce(final StateData stateData, final AuthenticationResult authData) {
        final String idToken = authData.getIdToken();
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

    public AuthenticationResult getAccessToken(final String refreshToken) {
        final String authority = getAuthority() + getTenant() + "/";
        if (logger.isDebugEnabled()) {
            logger.debug("refreshToken: {}, authority: {}", refreshToken, authority);
        }
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            final AuthenticationContext context = new AuthenticationContext(authority, true, service);
            final Future<AuthenticationResult> future =
                    context.acquireTokenByRefreshToken(refreshToken, new ClientCredential(getClientId(), getClientSecret()), null, null);
            final AuthenticationResult result = future.get(acquisitionTimeout, TimeUnit.MILLISECONDS);
            if (result == null) {
                throw new SsoLoginException("authentication result was null");
            }
            return result;
        } catch (final Exception e) {
            throw new SsoLoginException("Failed to get a token.", e);
        } finally {
            if (service != null) {
                service.shutdown();
            }
        }
    }

    protected AuthenticationResult getAccessToken(final AuthorizationCode authorizationCode, final String currentUri) {
        final String authority = getAuthority() + getTenant() + "/";
        final String authCode = authorizationCode.getValue();
        if (logger.isDebugEnabled()) {
            logger.debug("authCode: {}, authority: {}, uri: {}", authCode, authority, currentUri);
        }
        final ClientCredential credential = new ClientCredential(getClientId(), getClientSecret());
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            final AuthenticationContext context = new AuthenticationContext(authority, true, service);
            final Future<AuthenticationResult> future =
                    context.acquireTokenByAuthorizationCode(authCode, new URI(currentUri), credential, null);
            final AuthenticationResult result = future.get(acquisitionTimeout, TimeUnit.MILLISECONDS);
            if (result == null) {
                throw new SsoLoginException("authentication result was null");
            }
            return result;
        } catch (final Exception e) {
            throw new SsoLoginException("Failed to get a token.", e);
        } finally {
            if (service != null) {
                service.shutdown();
            }
        }
    }

    protected void validateAuthRespMatchesCodeFlow(final AuthenticationSuccessResponse oidcResponse) {
        if (oidcResponse.getIDToken() != null || oidcResponse.getAccessToken() != null || oidcResponse.getAuthorizationCode() == null) {
            throw new SsoLoginException("unexpected set of artifacts received");
        }
    }

    protected StateData validateState(final HttpSession session, final String state) {
        if (StringUtils.isNotEmpty(state)) {
            final StateData stateDataInSession = removeStateFromSession(session, state);
            if (stateDataInSession != null) {
                return stateDataInSession;
            }
        }
        throw new SsoLoginException("could not validate state");
    }

    protected StateData removeStateFromSession(final HttpSession session, final String state) {
        @SuppressWarnings("unchecked")
        final Map<String, StateData> states = (Map<String, StateData>) session.getAttribute(STATES);
        if (states != null) {
            final long now = System.currentTimeMillis();
            states.entrySet().stream().filter(e -> (now - e.getValue().getExpiration()) / 1000L > getStateTtl()).map(Map.Entry::getKey)
                    .collect(Collectors.toList()).forEach(s -> {
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

    public void updateMemberOf(final AzureAdUser user) {
        final List<String> groupList = new ArrayList<>();
        final List<String> roleList = new ArrayList<>();
        groupList.addAll(getDefaultGroupList());
        roleList.addAll(getDefaultRoleList());
        processMemberOf(user, groupList, roleList, "https://graph.microsoft.com/v1.0/me/memberOf");
        user.setGroups(groupList.stream().distinct().toArray(n -> new String[n]));
        user.setRoles(roleList.stream().distinct().toArray(n -> new String[n]));
    }

    protected void processMemberOf(final AzureAdUser user, final List<String> groupList, final List<String> roleList, final String url) {
        if (logger.isDebugEnabled()) {
            logger.debug("url: {}", url);
        }
        try (CurlResponse response = Curl.get(url).header("Authorization", "Bearer " + user.getAuthenticationResult().getAccessToken())
                .header("Accept", "application/json").execute()) {
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

    protected void addGroupOrRoleName(final List<String> list, final String value, final boolean useDomainServices) {
        list.add(value);
        if (useDomainServices && value.indexOf('@') >= 0) {
            final String[] values = value.split("@");
            if (values.length > 1) {
                list.add(values[0]);
            }
        }
    }

    protected void processParentGroup(final AzureAdUser user, final List<String> groupList, final List<String> roleList, final String id) {
        final Pair<String[], String[]> groupsAndRoles = getParentGroup(user, id);
        StreamUtil.stream(groupsAndRoles.getFirst()).of(stream -> stream.forEach(groupList::add));
        StreamUtil.stream(groupsAndRoles.getSecond()).of(stream -> stream.forEach(roleList::add));
    }

    protected Pair<String[], String[]> getParentGroup(final AzureAdUser user, final String id) {
        try {
            return groupCache.get(id, () -> {
                final List<String> groupList = new ArrayList<>();
                final List<String> roleList = new ArrayList<>();
                final String url = "https://graph.microsoft.com/v1.0/groups/" + id + "/getMemberGroups";
                if (logger.isDebugEnabled()) {
                    logger.debug("url: {}", url);
                }
                try (CurlResponse response =
                        Curl.post(url).header("Authorization", "Bearer " + user.getAuthenticationResult().getAccessToken())
                                .header("Accept", "application/json").header("Content-type", "application/json")
                                .body("{\"securityEnabledOnly\":false}").execute()) {
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
                                    final Pair<String[], String[]> groupsAndRoles = getParentGroup(user, value);
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

    protected void processGroup(final AzureAdUser user, final List<String> groupList, final List<String> roleList, final String id) {
        final String url = "https://graph.microsoft.com/v1.0/groups/" + id;
        if (logger.isDebugEnabled()) {
            logger.debug("url: {}", url);
        }
        try (CurlResponse response = Curl.get(url).header("Authorization", "Bearer " + user.getAuthenticationResult().getAccessToken())
                .header("Accept", "application/json").execute()) {
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

    protected List<String> getDefaultGroupList() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty("aad.default.groups");
        if (StringUtil.isBlank(value)) {
            return Collections.emptyList();
        }
        return split(value, ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toList()));
    }

    protected List<String> getDefaultRoleList() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty("aad.default.roles");
        if (StringUtil.isBlank(value)) {
            return Collections.emptyList();
        }
        return split(value, ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toList()));
    }

    protected static class StateData {
        private final String nonce;
        private final long expiration;

        public StateData(final String nonce, final long expiration) {
            this.nonce = nonce;
            this.expiration = expiration;
        }

        public String getNonce() {
            return nonce;
        }

        public long getExpiration() {
            return expiration;
        }

        @Override
        public String toString() {
            return "StateData [nonce=" + nonce + ", expiration=" + expiration + "]";
        }
    }

    protected String getClientId() {
        return ComponentUtil.getFessConfig().getSystemProperty(AZUREAD_CLIENT_ID, StringUtil.EMPTY);
    }

    protected String getClientSecret() {
        return ComponentUtil.getFessConfig().getSystemProperty(AZUREAD_CLIENT_SECRET, StringUtil.EMPTY);
    }

    protected String getTenant() {
        return ComponentUtil.getFessConfig().getSystemProperty(AZUREAD_TENANT, StringUtil.EMPTY);
    }

    protected String getAuthority() {
        return ComponentUtil.getFessConfig().getSystemProperty(AZUREAD_AUTHORITY, "https://login.microsoftonline.com/");
    }

    protected long getStateTtl() {
        return Long.parseLong(ComponentUtil.getFessConfig().getSystemProperty(AZUREAD_STATE_TTL, "3600"));
    }

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

    public void setAcquisitionTimeout(final long acquisitionTimeout) {
        this.acquisitionTimeout = acquisitionTimeout;
    }

    public void setGroupCacheExpiry(final long groupCacheExpiry) {
        this.groupCacheExpiry = groupCacheExpiry;
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
