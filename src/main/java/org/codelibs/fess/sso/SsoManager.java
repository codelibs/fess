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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 */
public class SsoManager {
    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(SsoManager.class);

    /** List of registered SSO authenticators. */
    protected final List<SsoAuthenticator> authenticatorList = new ArrayList<>();

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
        if (available()) {
            final SsoAuthenticator authenticator = getAuthenticator();
            if (authenticator != null) {
                return authenticator.getLoginCredential();
            }
        }
        return null;
    }

    /**
     * Gets the appropriate response for the specified SSO response type.
     *
     * @param responseType The type of SSO response required (e.g., METADATA, LOGOUT)
     * @return The action response from the SSO authenticator, or null if SSO is not available
     */
    public ActionResponse getResponse(final SsoResponseType responseType) {
        if (available()) {
            final SsoAuthenticator authenticator = getAuthenticator();
            if (authenticator != null) {
                return authenticator.getResponse(responseType);
            }
        }
        return null;
    }

    /**
     * Performs logout operations for the specified user using SSO.
     *
     * @param user The user to logout
     * @return The logout URL from the SSO authenticator, or null if SSO is not available
     */
    public String logout(final FessUserBean user) {
        if (available()) {
            final SsoAuthenticator authenticator = getAuthenticator();
            if (authenticator != null) {
                return authenticator.logout(user);
            }
        }
        return null;
    }

    /**
     * Gets the SSO authenticator instance for the configured SSO type.
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
        return null;
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
        return authenticatorList.toArray(new SsoAuthenticator[authenticatorList.size()]);
    }

    /**
     * Registers an SSO authenticator with this manager.
     *
     * @param authenticator The SSO authenticator to register
     */
    public void register(final SsoAuthenticator authenticator) {
        if (logger.isInfoEnabled()) {
            logger.info("Load {}", authenticator.getClass().getSimpleName());
        }
        authenticatorList.add(authenticator);
    }
}
