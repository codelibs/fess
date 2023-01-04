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
package org.codelibs.fess.sso.saml;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.core.net.UuidUtil;
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
import org.dbflute.optional.OptionalEntity;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.authn.AuthnRequestParams;
import com.onelogin.saml2.logout.LogoutRequestParams;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;

public class SamlAuthenticator implements SsoAuthenticator {

    private static final Logger logger = LogManager.getLogger(SamlAuthenticator.class);

    protected static final String SAML_PREFIX = "saml.";

    protected static final String SAML_STATE = "SAML_STATE";

    private Map<String, Object> defaultSettings;

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        ComponentUtil.getSsoManager().register(this);

        defaultSettings = new HashMap<>();
        defaultSettings.put("onelogin.saml2.strict", "true");
        defaultSettings.put("onelogin.saml2.debug", "false");
        defaultSettings.put("onelogin.saml2.sp.entityid", "http://localhost:8080/sso/metadata");
        defaultSettings.put("onelogin.saml2.sp.assertion_consumer_service.url", "http://localhost:8080/sso/");
        defaultSettings.put("onelogin.saml2.sp.assertion_consumer_service.binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
        defaultSettings.put("onelogin.saml2.sp.single_logout_service.url", "http://localhost:8080/sso/logout");
        defaultSettings.put("onelogin.saml2.sp.single_logout_service.binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
        defaultSettings.put("onelogin.saml2.sp.nameidformat", "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
        defaultSettings.put("onelogin.saml2.sp.x509cert", "");
        defaultSettings.put("onelogin.saml2.sp.privatekey", "");
        defaultSettings.put("onelogin.saml2.idp.single_sign_on_service.binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
        defaultSettings.put("onelogin.saml2.idp.single_logout_service.response.url", "");
        defaultSettings.put("onelogin.saml2.idp.single_logout_service.binding", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
        defaultSettings.put("onelogin.saml2.security.nameid_encrypted", "false");
        defaultSettings.put("onelogin.saml2.security.authnrequest_signed", "false");
        defaultSettings.put("onelogin.saml2.security.logoutrequest_signed", "false");
        defaultSettings.put("onelogin.saml2.security.logoutresponse_signed", "false");
        defaultSettings.put("onelogin.saml2.security.want_messages_signed", "false");
        defaultSettings.put("onelogin.saml2.security.want_assertions_signed", "false");
        defaultSettings.put("onelogin.saml2.security.sign_metadata", "");
        defaultSettings.put("onelogin.saml2.security.want_assertions_encrypted", "false");
        defaultSettings.put("onelogin.saml2.security.want_nameid_encrypted", "false");
        defaultSettings.put("onelogin.saml2.security.requested_authncontext", "urn:oasis:names:tc:SAML:2.0:ac:classes:Password");
        defaultSettings.put("onelogin.saml2.security.onelogin.saml2.security.requested_authncontextcomparison", "exact");
        defaultSettings.put("onelogin.saml2.security.want_xml_validation", "true");
        defaultSettings.put("onelogin.saml2.security.signature_algorithm", "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        defaultSettings.put("onelogin.saml2.organization.name", "CodeLibs");
        defaultSettings.put("onelogin.saml2.organization.displayname", "Fess");
        defaultSettings.put("onelogin.saml2.organization.url", "https://fess.codelibs.org/");
        defaultSettings.put("onelogin.saml2.organization.lang", "");
        defaultSettings.put("onelogin.saml2.contacts.technical.given_name", "Technical Guy");
        defaultSettings.put("onelogin.saml2.contacts.technical.email_address", "technical@example.com");
        defaultSettings.put("onelogin.saml2.contacts.support.given_name", "Support Guy");
        defaultSettings.put("onelogin.saml2.contacts.support.email_address", "support@@example.com");
    }

    protected Saml2Settings getSettings() {
        final Map<String, Object> params = new HashMap<>(defaultSettings);
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        systemProperties.entrySet().stream().forEach(e -> {
            final String key = e.getKey().toString();
            if (!key.startsWith(SAML_PREFIX)) {
                return;
            }
            params.put("onelogin.saml2." + key.substring(SAML_PREFIX.length()), e.getValue());
        });
        return new SettingsBuilder().fromValues(params).build();
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
                final String sesState = (String) session.getAttribute(SAML_STATE);
                if (StringUtil.isNotBlank(sesState)) {
                    session.removeAttribute(SAML_STATE);
                    try {
                        final Auth auth = new Auth(getSettings(), request, response);
                        auth.processResponse();

                        if (!auth.isAuthenticated()) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Authentication is failed.");
                            }
                            return null;
                        }

                        final List<String> errors = auth.getErrors();
                        if (!errors.isEmpty()) {
                            logger.warn("{}", errors.stream().collect(Collectors.joining(", ")));
                            if (auth.isDebugActive() && StringUtil.isNotBlank(auth.getLastErrorReason())) {
                                logger.warn("Authentication Failure: {} - Reason: {}", errors.stream().collect(Collectors.joining(", ")),
                                        auth.getLastErrorReason());
                            } else {
                                logger.warn("Authentication Failure: {}", errors.stream().collect(Collectors.joining(", ")));
                            }
                            return null;
                        }

                        return createLoginCredential(request, response, auth);
                    } catch (final Exception e) {
                        logger.warn("Authentication is failed.", e);
                        return null;
                    }
                }
            }

            try {
                final Auth auth = new Auth(getSettings(), request, response);
                final AuthnRequestParams authnRequestParams = new AuthnRequestParams(false, false, true);
                final String loginUrl = auth.login(null, authnRequestParams, true);
                request.getSession().setAttribute(SAML_STATE, UuidUtil.create());
                return new ActionResponseCredential(() -> HtmlResponse.fromRedirectPathAsIs(loginUrl));
            } catch (final Exception e) {
                throw new SsoLoginException("Invalid SAML redirect URL.", e);
            }

        }).orElseGet(() -> null);
    }

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
                    final Auth auth = new Auth(getSettings(), request, response);
                    final LogoutRequestParams logoutRequestParams = new LogoutRequestParams(samlUser.getSessionIndex(), samlUser.getName(),
                            samlUser.getNameIdFormat(), samlUser.getNameidNameQualifier(), samlUser.getNameidSPNameQualifier());
                    return auth.logout(null, logoutRequestParams, true);
                } catch (final Exception e) {
                    logger.warn("Failed to logout from IdP: {}", samlUser, e);
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
                return new StreamResponse("metadata").contentType("application/xhtml+xml").stream(out -> {
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
        }).orElseThrow(() -> new SsoMessageException(
                messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, "Invalid state."),
                "Failed to process metadata.", new SsoProcessException("Invalid state.")));
    }

    protected ActionResponse getLogoutResponse() {
        LaRequestUtil.getOptionalRequest().map(request -> {
            if (logger.isDebugEnabled()) {
                logger.debug("Logging out with SAML Authenticator");
            }
            final HttpServletResponse response = LaResponseUtil.getResponse();
            try {
                final Auth auth = new Auth(getSettings(), request, response);
                auth.processSLO();
                final List<String> errors = auth.getErrors();
                if (errors.isEmpty()) {
                    throw new SsoMessageException(messages -> messages.addSuccessSsoLogout(UserMessages.GLOBAL_PROPERTY_KEY), "Logged out");
                }
                final String msg = errors.stream().collect(Collectors.joining(", "));
                throw new SsoMessageException(
                        messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, msg),
                        "Failed to log out.", new SsoProcessException(msg));
            } catch (final SsoMessageException e) {
                throw e;
            } catch (final Exception e) {
                throw new SsoMessageException(
                        messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, e.getMessage()),
                        "Failed to log out.", e);
            }
        }).orElseThrow(() -> new SsoMessageException(
                messages -> messages.addErrorsFailedToProcessSsoRequest(UserMessages.GLOBAL_PROPERTY_KEY, "Invalid state."),
                "Failed to log out.", new SsoProcessException("Invalid state.")));
        return null;
    }
}
