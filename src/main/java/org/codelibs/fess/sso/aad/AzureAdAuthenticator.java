/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.net.UuidUtil;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.base.login.AzureAdCredential;
import org.codelibs.fess.app.web.base.login.FessLoginAssist.LoginCredentialResolver;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.exception.SsoLoginException;
import org.codelibs.fess.sso.SsoAuthenticator;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.util.LaRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(AzureAdAuthenticator.class);

    protected static final String AZUREAD_STATE_TTL = "azuread.state.ttl";

    protected static final String AZUREAD_AUTHORITY = "azuread.authority";

    protected static final String AZUREAD_TENANT = "azuread.tenant";

    protected static final String AZUREAD_CLIENT_SECRET = "azuread.client.secret";

    protected static final String AZUREAD_CLIENT_ID = "azuread.client.id";

    protected static final String STATES = "aadStates";

    protected static final String STATE = "state";

    protected static final String ERROR = "error";

    protected static final String ERROR_DESCRIPTION = "error_description";

    protected static final String ERROR_URI = "error_uri";

    protected static final String ID_TOKEN = "id_token";

    protected static final String CODE = "code";

    protected long acquisitionTimeout = 30 * 1000L;

    @PostConstruct
    public void init() {
        ComponentUtil.getSsoManager().register(this);
    }

    @Override
    public LoginCredential getLoginCredential() {
        return LaRequestUtil.getOptionalRequest().map(request -> {
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
        final String authUrl =
                getAuthority() + getTenant()
                        + "/oauth2/authorize?response_type=code&scope=directory.read.all&response_mode=form_post&redirect_uri="
                        + URLEncoder.encode(request.getRequestURL().toString(), Constants.UTF_8_CHARSET) + "&client_id=" + getClientId()
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

        final Map<String, String> params = new HashMap<>();
        for (final Map.Entry<String, String[]> e : request.getParameterMap().entrySet()) {
            if (e.getValue().length > 0) {
                params.put(e.getKey(), e.getValue()[0]);
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("process authentication: url: {}, params: {}", urlBuf, params);
        }

        // validate that state in response equals to state in request
        final StateData stateData = validateState(request.getSession(), params.get(STATE));
        if (logger.isDebugEnabled()) {
            logger.debug("load {}", stateData);
        }

        final AuthenticationResponse authResponse = parseAuthenticationResponse(urlBuf.toString(), params);
        if (authResponse instanceof AuthenticationSuccessResponse) {
            final AuthenticationSuccessResponse oidcResponse = (AuthenticationSuccessResponse) authResponse;
            validateAuthRespMatchesCodeFlow(oidcResponse);
            final AuthenticationResult authData = getAccessToken(oidcResponse.getAuthorizationCode(), request.getRequestURL().toString());
            validateNonce(stateData, authData);

            return new AzureAdCredential(authData);
        } else {
            final AuthenticationErrorResponse oidcResponse = (AuthenticationErrorResponse) authResponse;
            throw new SsoLoginException(String.format("Request for auth code failed: %s - %s", oidcResponse.getErrorObject().getCode(),
                    oidcResponse.getErrorObject().getDescription()));
        }
    }

    protected AuthenticationResponse parseAuthenticationResponse(final String url, final Map<String, String> params) {
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

    public AuthenticationResult getAccessToken(String refreshToken) {
        final String authority = getAuthority() + getTenant() + "/";
        if (logger.isDebugEnabled()) {
            logger.debug("refreshToken: {}, authority: {}", refreshToken, authority);
        }
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            AuthenticationContext context = new AuthenticationContext(authority, true, service);
            Future<AuthenticationResult> future =
                    context.acquireTokenByRefreshToken(refreshToken, new ClientCredential(getClientId(), getClientSecret()), null, null);
            final AuthenticationResult result = future.get(acquisitionTimeout, TimeUnit.MILLISECONDS);
            if (result == null) {
                throw new SsoLoginException("authentication result was null");
            }
            return result;
        } catch (Exception e) {
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
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            return false;
        }
        final Map<String, String[]> params = request.getParameterMap();
        return params.containsKey(ERROR) || params.containsKey(ID_TOKEN) || params.containsKey(CODE);
    }

    protected class StateData {
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

    @Override
    public void resolveCredential(final LoginCredentialResolver resolver) {
        resolver.resolve(AzureAdCredential.class, credential -> {
            return OptionalEntity.of(credential.getUser());
        });
    }

    public void setAcquisitionTimeout(long acquisitionTimeout) {
        this.acquisitionTimeout = acquisitionTimeout;
    }
}
