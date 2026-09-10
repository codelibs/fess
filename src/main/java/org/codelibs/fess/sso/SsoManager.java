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
package org.codelibs.fess.sso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;

/**
 * Manager class for coordinating SSO (Single Sign-On) authentication operations.
 *
 * This class serves as the central coordinator for SSO authentication in Fess.
 * It manages registered SSO authenticators, determines when SSO is available,
 * and delegates authentication operations to the appropriate SSO provider based
 * on the current configuration.
 *
 * <p>No authenticator ships in the distribution: each one is a fess-sso-* plugin that contributes
 * its {@code <sso.type>Authenticator} component through {@code fess_sso++.xml}. What this class
 * owns is the mapping from an {@code sso.type} value to that component name, so a plugin needs no
 * change here to be reachable.</p>
 */
public class SsoManager {
    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(SsoManager.class);

    /** List of registered SSO authenticators. */
    protected final List<SsoAuthenticator> authenticatorList = new ArrayList<>();

    /**
     * The {@code sso.type} values already reported as unserved, so that each one is logged once
     * rather than once per request.
     *
     * <p>{@code /sso/} is anonymous, and the same miss is hit on every visit to it, so a warning
     * per attempt is a log an unauthenticated client can fill. The set is bounded by the number of
     * distinct values the setting has held for the life of the JVM, which is one.</p>
     */
    protected final Set<String> unservedSsoTypes = ConcurrentHashMap.newKeySet();

    /**
     * Default constructor for creating a new SsoManager instance.
     */
    public SsoManager() {
        // Default constructor
    }

    /**
     * Checks whether SSO authentication is available and configured.
     *
     * @return true if SSO is configured and available, false otherwise
     */
    public boolean available() {
        final String ssoType = getSsoType();
        if (logger.isDebugEnabled()) {
            logger.debug("sso.type: {}", ssoType);
        }
        return !Constants.NONE.equals(ssoType);
    }

    /**
     * Attempts to obtain login credentials using the configured SSO authenticator.
     *
     * @return The login credential from SSO authentication, or null if SSO is not available
     *         or no credential could be obtained
     */
    public LoginCredential getLoginCredential() {
        return withAuthenticator(SsoAuthenticator::getLoginCredential);
    }

    /**
     * Gets the appropriate response for the specified SSO response type.
     *
     * @param responseType The type of SSO response required (e.g., METADATA, LOGOUT)
     * @return The action response from the SSO authenticator, or null if SSO is not available
     */
    public ActionResponse getResponse(final SsoResponseType responseType) {
        return withAuthenticator(authenticator -> authenticator.getResponse(responseType));
    }

    /**
     * Performs logout operations for the specified user using SSO.
     *
     * @param user The user to logout
     * @return The logout URL from the SSO authenticator, or null if SSO is not available
     */
    public String logout(final FessUserBean user) {
        return withAuthenticator(authenticator -> authenticator.logout(user));
    }

    /**
     * Applies the given operation to the configured SSO authenticator.
     *
     * @param <T> The result type of the operation.
     * @param operation The operation to apply.
     * @return The result of the operation, or null if SSO is unavailable or no authenticator is
     *         registered for the configured type.
     */
    protected <T> T withAuthenticator(final Function<SsoAuthenticator, T> operation) {
        if (!available()) {
            return null;
        }
        final SsoAuthenticator authenticator = getAuthenticator();
        return authenticator != null ? operation.apply(authenticator) : null;
    }

    /**
     * Gets the SSO authenticator instance for the configured SSO type.
     *
     * <p>The type is resolved to the component name {@code <sso.type>Authenticator} rather than to
     * a class this package knows about, which is what lets the authenticators ship as plugins: a
     * type core has never heard of reaches a plugin that registers that name. A type whose plugin
     * is not installed is therefore a configuration error rather than a missing class, and is
     * reported by {@link #reportUnservedSsoType}.</p>
     *
     * @return The SSO authenticator instance, or null if not found
     */
    protected SsoAuthenticator getAuthenticator() {
        String ssoType = getSsoType();
        // Backward compatibility: map legacy "aad" (Azure AD) to "entraid" (Entra ID)
        if ("aad".equals(ssoType)) {
            ssoType = "entraid";
        }
        final String name = ssoType + "Authenticator";
        if (ComponentUtil.hasComponent(name)) {
            return ComponentUtil.getComponent(name);
        }
        reportUnservedSsoType(ssoType, name);
        return null;
    }

    /**
     * Reports that no component serves the configured {@code sso.type}.
     *
     * <p>Every caller of {@link #getAuthenticator()} answers null when this happens, and
     * {@code SsoAction.index()} turns that null into {@code errors.sso_login_error} and a redirect
     * to the login page. Without this the whole symptom is a {@code GET /sso/} that answers 302 to
     * {@code /login/}, with nothing in the log at any level to say why -- the configuration is
     * complete and correct, and the piece that is missing is a plugin. That became the ordinary
     * upgrade path when the authenticators were split out of core, so it is said out loud.</p>
     *
     * <p>WARN rather than ERROR: in Fess an ERROR line is a notification trigger, and this is a
     * deployment that has not finished installing rather than a fault at run time. Blank and
     * {@code none} are the states of a deployment that does not use SSO at all and say nothing --
     * {@code /sso/} is reachable whether or not it is configured.</p>
     *
     * @param ssoType the configured type, after the legacy {@code aad} mapping
     * @param componentName the component name it resolved to
     */
    protected void reportUnservedSsoType(final String ssoType, final String componentName) {
        if (StringUtil.isBlank(ssoType) || Constants.NONE.equals(ssoType)) {
            return;
        }
        if (logger.isWarnEnabled() && unservedSsoTypes.add(ssoType)) {
            logger.warn("No SSO authenticator is registered as {} for sso.type={}. Every authenticator ships as a fess-sso-* plugin: "
                    + "install fess-sso-saml for saml, fess-sso-spnego for spnego, fess-sso-entraid for entraid "
                    + "(or the legacy aad), or fess-sso-oidc for oic. Until then every request to /sso/ is redirected "
                    + "back to the login page.", componentName, ssoType);
        }
    }

    /**
     * Gets the configured SSO type from the system configuration.
     *
     * @return The SSO type string from configuration
     */
    protected String getSsoType() {
        return ComponentUtil.getFessConfig().getSsoType();
    }

    /**
     * Gets all registered SSO authenticators.
     *
     * @return Array of all registered SSO authenticators
     */
    public SsoAuthenticator[] getAuthenticators() {
        return authenticatorList.toArray(new SsoAuthenticator[0]);
    }

    /**
     * Registers an SSO authenticator with this manager.
     *
     * @param authenticator The SSO authenticator to register
     */
    public void register(final SsoAuthenticator authenticator) {
        if (logger.isInfoEnabled()) {
            logger.info("Loaded SsoAuthenticator: {}", authenticator.getClass().getSimpleName());
        }
        authenticatorList.add(authenticator);
    }
}
