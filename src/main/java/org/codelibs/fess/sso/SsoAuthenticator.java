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
     * @param responseType The type of SSO response required.
     * @return The action response.
     */
    ActionResponse getResponse(SsoResponseType responseType);

    /**
     * Performs logout for the specified user.
     * @param user The user to logout.
     * @return The logout URL or null if not applicable.
     */
    String logout(FessUserBean user);

}