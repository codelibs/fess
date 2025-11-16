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
package org.codelibs.fess.sso.oic;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.net.UuidUtil;
import org.codelibs.fess.app.web.base.login.ActionResponseCredential;
import org.codelibs.fess.app.web.base.login.FessLoginAssist.LoginCredentialResolver;
import org.codelibs.fess.app.web.base.login.OpenIdConnectCredential;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.sso.SsoAuthenticator;
import org.codelibs.fess.sso.SsoResponseType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.IpAddressUtil;
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.util.LaRequestUtil;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.api.client.json.gson.GsonFactory;
import com.google.common.io.BaseEncoding;
import com.google.common.io.BaseEncoding.DecodingException;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * OpenID Connect authenticator for SSO integration.
 */
public class OpenIdConnectAuthenticator implements SsoAuthenticator {

    /**
     * Default constructor.
     */
    public OpenIdConnectAuthenticator() {
        // Default constructor
    }

    private static final Logger logger = LogManager.getLogger(OpenIdConnectAuthenticator.class);

    private static final BaseEncoding BASE64_DECODER = BaseEncoding.base64().withSeparator("\n", 64);

    private static final BaseEncoding BASE64URL_DECODER = BaseEncoding.base64Url().withSeparator("\n", 64);

    /** Configuration key for OpenID Connect authorization server URL. */
    protected static final String OIC_AUTH_SERVER_URL = "oic.auth.server.url";

    /** Configuration key for OpenID Connect client ID. */
    protected static final String OIC_CLIENT_ID = "oic.client.id";

    /** Configuration key for OpenID Connect scope. */
    protected static final String OIC_SCOPE = "oic.scope";

    /** Configuration key for OpenID Connect redirect URL. */
    protected static final String OIC_REDIRECT_URL = "oic.redirect.url";

    /** Configuration key for OpenID Connect token server URL. */
    protected static final String OIC_TOKEN_SERVER_URL = "oic.token.server.url";

    /** Configuration key for OpenID Connect client secret. */
    protected static final String OIC_CLIENT_SECRET = "oic.client.secret";

    /** Session key for OpenID Connect state parameter. */
    protected static final String OIC_STATE = "OIC_STATE";

    /** HTTP transport for OpenID Connect requests. */
    protected final HttpTransport httpTransport = new NetHttpTransport();

    /** JSON factory for OpenID Connect response parsing. */
    protected final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    /** HTTP request timeout in milliseconds. */
    protected int httpRequestTimeout = 30 * 1000;

    /**
     * Initializes the OpenID Connect authenticator.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        ComponentUtil.getSsoManager().register(this);
    }

    @Override
    public LoginCredential getLoginCredential() {
        return LaRequestUtil.getOptionalRequest().map(request -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Logging in with OpenID Connect Authenticator");
            }
            final HttpSession session = request.getSession(false);
            if (session != null) {
                final String sesState = (String) session.getAttribute(OIC_STATE);
                if (StringUtil.isNotBlank(sesState)) {
                    session.removeAttribute(OIC_STATE);
                    final String code = request.getParameter("code");
                    final String reqState = request.getParameter("state");
                    if (logger.isDebugEnabled()) {
                        logger.debug("code: {}, state(request): {}, state(session): {}", code, reqState, sesState);
                    }
                    if (sesState.equals(reqState) && StringUtil.isNotBlank(code)) {
                        return processCallback(request, code);
                    }
                }
            }

            return new ActionResponseCredential(() -> HtmlResponse.fromRedirectPathAsIs(getAuthUrl(request)));
        }).orElse(null);
    }

    /**
     * Gets the authorization URL for OpenID Connect.
     *
     * @param request the HTTP servlet request
     * @return the authorization URL
     */
    protected String getAuthUrl(final HttpServletRequest request) {
        final String state = UuidUtil.create();
        request.getSession().setAttribute(OIC_STATE, state);
        return new AuthorizationCodeRequestUrl(getOicAuthServerUrl(), getOicClientId())//
                .setScopes(Arrays.asList(getOicScope()))//
                .setResponseTypes(Arrays.asList("code"))//
                .setRedirectUri(getOicRedirectUrl())//
                .setState(state)//
                .build();
    }

    /**
     * Decodes a Base64 string to bytes.
     *
     * @param base64String the Base64 string to decode
     * @return the decoded bytes, or null if input is null
     */
    protected byte[] decodeBase64(String base64String) {
        if (base64String == null) {
            return null;
        }
        try {
            return BASE64_DECODER.decode(base64String);
        } catch (IllegalArgumentException e) {
            if (e.getCause() instanceof DecodingException) {
                return BASE64URL_DECODER.decode(base64String.trim());
            }
            throw e;
        }
    }

    /**
     * Processes the callback from OpenID Connect provider.
     *
     * @param request the HTTP servlet request
     * @param code the authorization code
     * @return the login credential
     */
    protected LoginCredential processCallback(final HttpServletRequest request, final String code) {
        try {
            final TokenResponse tr = getTokenUrl(code);

            final String[] jwt = ((String) tr.get("id_token")).split("\\.");
            final String jwtHeader = new String(decodeBase64(jwt[0]), Constants.UTF_8_CHARSET);
            final String jwtClaim = new String(decodeBase64(jwt[1]), Constants.UTF_8_CHARSET);
            final String jwtSignature = new String(decodeBase64(jwt[2]), Constants.UTF_8_CHARSET);

            if (logger.isDebugEnabled()) {
                logger.debug("jwtHeader: {}", jwtHeader);
                logger.debug("jwtClaim: {}", jwtClaim);
                logger.debug("jwtSignature: {}", jwtSignature);
            }

            // SECURITY WARNING: JWT signature validation is not implemented.
            // This is a critical security vulnerability. The ID token should be validated
            // to ensure it was issued by the expected OpenID Connect provider and has not been tampered with.
            // TODO: Implement JWT signature validation using the provider's public key

            final Map<String, Object> attributes = new HashMap<>();
            attributes.put("accesstoken", tr.getAccessToken());
            attributes.put("refreshtoken", tr.getRefreshToken() == null ? "null" : tr.getRefreshToken());
            attributes.put("tokentype", tr.getTokenType());
            attributes.put("expire", tr.getExpiresInSeconds());
            attributes.put("jwtheader", jwtHeader);
            attributes.put("jwtclaim", jwtClaim);
            attributes.put("jwtsignature", jwtSignature);

            if (logger.isDebugEnabled()) {
                logger.debug("attribute: {}", attributes);
            }
            parseJwtClaim(jwtClaim, attributes);

            return new OpenIdConnectCredential(attributes);
        } catch (final IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to process callbacked request.", e);
            }
        }
        return null;
    }

    /**
     * Parses the JWT claim and extracts attributes.
     *
     * @param jwtClaim the JWT claim string
     * @param attributes the attributes map to populate
     * @throws IOException if an I/O error occurs
     */
    protected void parseJwtClaim(final String jwtClaim, final Map<String, Object> attributes) throws IOException {
        try (final JsonParser jsonParser = jsonFactory.createJsonParser(jwtClaim)) {
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                final String name = jsonParser.getCurrentName();
                if (name != null) {
                    jsonParser.nextToken();

                    if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
                        // Handle array type
                        attributes.put(name, parseArray(jsonParser));
                    } else if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                        // Handle nested object type
                        attributes.put(name, parseObject(jsonParser));
                    } else {
                        // Handle primitive types (string, number, boolean, etc.)
                        attributes.put(name, parsePrimitive(jsonParser));
                    }
                }
            }
        }
    }

    /**
     * Parses primitive values from JSON parser.
     *
     * @param jsonParser the JSON parser
     * @return the parsed primitive value
     * @throws IOException if an I/O error occurs
     */
    protected Object parsePrimitive(final JsonParser jsonParser) throws IOException {
        final JsonToken token = jsonParser.getCurrentToken();
        return switch (token) {
        case VALUE_STRING -> jsonParser.getText();
        case VALUE_NUMBER_INT -> jsonParser.getLongValue();
        case VALUE_NUMBER_FLOAT -> jsonParser.getDoubleValue();
        case VALUE_TRUE -> true;
        case VALUE_FALSE -> false;
        case VALUE_NULL -> null;
        default -> null; // Or throw an exception if unexpected token
        };
    }

    /**
     * Parses array values from JSON parser.
     *
     * @param jsonParser the JSON parser
     * @return the parsed array as a list
     * @throws IOException if an I/O error occurs
     */
    protected Object parseArray(final JsonParser jsonParser) throws IOException {
        final List<Object> list = new ArrayList<>();
        while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                list.add(parseObject(jsonParser));
            } else if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
                list.add(parseArray(jsonParser)); // Nested array
            } else {
                list.add(parsePrimitive(jsonParser));
            }
        }

        return list;
    }

    /**
     * Parses object values from JSON parser.
     *
     * @param jsonParser the JSON parser
     * @return the parsed object as a map
     * @throws IOException if an I/O error occurs
     */
    protected Map<String, Object> parseObject(final JsonParser jsonParser) throws IOException {
        final Map<String, Object> nestedMap = new HashMap<>();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            final String fieldName = jsonParser.getCurrentName();
            if (fieldName != null) {
                jsonParser.nextToken(); // Move to the value of the current field

                if (jsonParser.getCurrentToken() == JsonToken.START_ARRAY) {
                    nestedMap.put(fieldName, parseArray(jsonParser));
                } else if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                    nestedMap.put(fieldName, parseObject(jsonParser));
                } else {
                    nestedMap.put(fieldName, parsePrimitive(jsonParser));
                }
            }
        }
        return nestedMap;
    }

    /**
     * Gets the token response from the OpenID Connect provider.
     *
     * @param code the authorization code
     * @return the token response
     * @throws IOException if an I/O error occurs
     */
    protected TokenResponse getTokenUrl(final String code) throws IOException {
        final AuthorizationCodeTokenRequest request =
                new AuthorizationCodeTokenRequest(httpTransport, jsonFactory, new GenericUrl(getOicTokenServerUrl()), code)//
                        .setGrantType("authorization_code")//
                        .setRedirectUri(getOicRedirectUrl())//
                        .set("client_id", getOicClientId())//
                        .set("client_secret", getOicClientSecret());
        request.setConnectTimeout(httpRequestTimeout);
        request.setReadTimeout(httpRequestTimeout);
        return request.execute();
    }

    /**
     * Gets the OpenID Connect client secret.
     *
     * @return the client secret
     */
    protected String getOicClientSecret() {
        return ComponentUtil.getSystemProperties().getProperty(OIC_CLIENT_SECRET, StringUtil.EMPTY);
    }

    /**
     * Gets the OpenID Connect token server URL.
     *
     * @return the token server URL
     */
    protected String getOicTokenServerUrl() {
        return ComponentUtil.getSystemProperties().getProperty(OIC_TOKEN_SERVER_URL, "https://accounts.google.com/o/oauth2/token");
    }

    /**
     * Gets the OpenID Connect redirect URL.
     *
     * @return the redirect URL
     */
    protected String getOicRedirectUrl() {
        return ComponentUtil.getSystemProperties().getProperty(OIC_REDIRECT_URL, buildDefaultRedirectUrl());
    }

    /**
     * Builds a default redirect URL for OpenID Connect based on the environment.
     * Automatically handles IPv6 addresses by wrapping them in brackets.
     *
     * @return the default redirect URL with proper IPv6 handling
     */
    protected String buildDefaultRedirectUrl() {
        try {
            final InetAddress localhost = InetAddress.getByName("localhost");
            return IpAddressUtil.buildUrl("http", localhost, 8080, "/sso/");
        } catch (final UnknownHostException e) {
            // Fallback to hardcoded localhost if resolution fails
            return "http://localhost:8080/sso/";
        }
    }

    /**
     * Gets the OpenID Connect scope.
     *
     * @return the scope
     */
    protected String getOicScope() {
        return ComponentUtil.getSystemProperties().getProperty(OIC_SCOPE, StringUtil.EMPTY);
    }

    /**
     * Gets the OpenID Connect client ID.
     *
     * @return the client ID
     */
    protected String getOicClientId() {
        return ComponentUtil.getSystemProperties().getProperty(OIC_CLIENT_ID, StringUtil.EMPTY);
    }

    /**
     * Gets the OpenID Connect authorization server URL.
     *
     * @return the authorization server URL
     */
    protected String getOicAuthServerUrl() {
        return ComponentUtil.getSystemProperties().getProperty(OIC_AUTH_SERVER_URL, "https://accounts.google.com/o/oauth2/auth");
    }

    @Override
    public void resolveCredential(final LoginCredentialResolver resolver) {
        resolver.resolve(OpenIdConnectCredential.class, credential -> OptionalEntity.of(credential.getUser()));
    }

    /**
     * Sets the HTTP request timeout.
     *
     * @param httpRequestTimeout the HTTP request timeout in milliseconds
     */
    public void setHttpRequestTimeout(final int httpRequestTimeout) {
        this.httpRequestTimeout = httpRequestTimeout;
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
