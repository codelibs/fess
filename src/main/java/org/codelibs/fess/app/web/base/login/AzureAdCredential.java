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
package org.codelibs.fess.app.web.base.login;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.sso.aad.AzureAdAuthenticator;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.login.credential.LoginCredential;

import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;

/**
 * Azure Active Directory credential implementation for Fess authentication.
 * Provides login credential functionality using Azure AD authentication results.
 */
public class AzureAdCredential implements LoginCredential, FessCredential {

    private static final Logger logger = LogManager.getLogger(AzureAdCredential.class);

    private final IAuthenticationResult authResult;

    /**
     * Constructs an Azure AD credential with the authentication result.
     * @param authResult The authentication result from Azure AD.
     */
    public AzureAdCredential(final IAuthenticationResult authResult) {
        this.authResult = authResult;
    }

    @Override
    public String getUserId() {
        return authResult.account().username();
    }

    @Override
    public String toString() {
        return "{" + authResult.account().username() + "}";
    }

    /**
     * Gets the Azure AD user associated with this credential.
     * @return The Azure AD user instance.
     */
    public AzureAdUser getUser() {
        return new AzureAdUser(authResult);
    }

    /**
     * Azure AD user implementation providing user information and permissions.
     */
    public static class AzureAdUser implements FessUser {
        private static final long serialVersionUID = 1L;

        /** User's group memberships. */
        protected String[] groups;

        /** User's role assignments. */
        protected String[] roles;

        /** User's computed permissions. */
        protected String[] permissions;

        /** Azure AD authentication result. */
        protected IAuthenticationResult authResult;

        /**
         * Constructs an Azure AD user with the authentication result.
         * @param authResult The authentication result from Azure AD.
         */
        public AzureAdUser(final IAuthenticationResult authResult) {
            this.authResult = authResult;
            final AzureAdAuthenticator authenticator = ComponentUtil.getComponent(AzureAdAuthenticator.class);
            authenticator.updateMemberOf(this);
        }

        @Override
        public String getName() {
            return authResult.account().username();
        }

        @Override
        public String[] getRoleNames() {
            return roles;
        }

        @Override
        public String[] getGroupNames() {
            return groups;
        }

        @Override
        public String[] getPermissions() {
            if (permissions == null) {
                final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
                final Set<String> permissionSet = new HashSet<>();
                final IAccount account = authResult.account();
                final String homeAccountId = account.homeAccountId();
                final String username = account.username();
                if (logger.isDebugEnabled()) {
                    logger.debug("homeAccountId:{} username:{}", homeAccountId, username);
                }
                permissionSet.add(systemHelper.getSearchRoleByUser(homeAccountId));
                permissionSet.add(systemHelper.getSearchRoleByUser(username));
                if (ComponentUtil.getFessConfig().isAzureAdUseDomainServices() && username.indexOf('@') >= 0) {
                    final String[] values = username.split("@");
                    if (values.length > 1) {
                        permissionSet.add(systemHelper.getSearchRoleByUser(values[0]));
                    }
                }
                stream(groups).of(stream -> stream.forEach(s -> permissionSet.add(systemHelper.getSearchRoleByGroup(s))));
                stream(roles).of(stream -> stream.forEach(s -> permissionSet.add(systemHelper.getSearchRoleByRole(s))));
                permissions = permissionSet.stream().filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n]);
            }
            return permissions;
        }

        @Override
        public boolean refresh() {
            // MSAL4J handles token refresh internally through silent authentication
            // Check if token is still valid
            if (authResult.expiresOnDate().getTime() < ComponentUtil.getSystemHelper().getCurrentTimeAsLong()) {
                return false;
            }
            // For MSAL4J, we rely on silent token acquisition during next authentication request
            // This method returns true if token is still valid
            return true;
        }

        /**
         * Gets the Azure AD authentication result.
         * @return The authentication result.
         */
        public IAuthenticationResult getAuthenticationResult() {
            return authResult;
        }

        /**
         * Sets the user's group memberships.
         * @param groups Array of group names.
         */
        public void setGroups(final String[] groups) {
            this.groups = groups;
        }

        /**
         * Sets the user's role assignments.
         * @param roles Array of role names.
         */
        public void setRoles(final String[] roles) {
            this.roles = roles;
        }
    }
}
