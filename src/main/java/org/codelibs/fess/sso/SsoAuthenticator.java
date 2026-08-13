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

import org.codelibs.fess.app.web.base.login.FessLoginAssist.LoginCredentialResolver;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.lastaflute.web.login.credential.LoginCredential;
import org.lastaflute.web.response.ActionResponse;

/**
 * Interface for SSO (Single Sign-On) authenticator implementations.
 *
 * This interface defines the contract for SSO authentication providers that can be
 * integrated with Fess. Implementations handle specific SSO protocols like SAML,
 * OAuth, SPNEGO, or other authentication mechanisms. Each authenticator is responsible
 * for obtaining login credentials, resolving user information, and managing SSO
 * lifecycle operations like logout and metadata exchange.
 *
 * An implementation has to satisfy two separate conventions to be reachable:
 * <ul>
 * <li>It must be registered in DI under the name {@code <sso.type> + "Authenticator"}, because
 * {@link SsoManager#getAuthenticator()} resolves the active provider by that name.</li>
 * <li>It must call {@link SsoManager#register(SsoAuthenticator)} from its initialization method, or
 * {@code FessLoginAssist} will never invoke its {@link #resolveCredential(LoginCredentialResolver)}
 * and a logged-in user will not be resolved.</li>
 * </ul>
 */
public interface SsoAuthenticator {

    /**
     * Gets the login credential for SSO authentication.
     * @return The login credential.
     */
    LoginCredential getLoginCredential();

    /**
     * Resolves credential using the provided resolver.
     * @param resolver The login credential resolver.
     */
    void resolveCredential(LoginCredentialResolver resolver);

    /**
     * Gets the action response for the specified SSO response type.
     *
     * Only a protocol with its own metadata or single-logout endpoint implements this; the default
     * reports that this provider does not participate in the operation, which {@code SsoAction}
     * answers with a 400.
     *
     * @param responseType The type of SSO response required.
     * @return The action response, or null if this provider has no response for the type.
     */
    default ActionResponse getResponse(final SsoResponseType responseType) {
        return null;
    }

    /**
     * Performs logout for the specified user.
     *
     * The default reports that this provider has no single-logout endpoint, leaving the caller to
     * end the local session only.
     *
     * @param user The user to logout.
     * @return The logout URL, or null if not applicable.
     */
    default String logout(final FessUserBean user) {
        return null;
    }

}