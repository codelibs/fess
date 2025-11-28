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
import org.codelibs.fess.sso.entraid.EntraIdAuthenticator;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.login.credential.LoginCredential;

import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;

/**
 * Microsoft Entra ID credential implementation for Fess authentication.
 * Provides login credential functionality using Entra ID authentication results.
 */
public class EntraIdCredential implements LoginCredential, FessCredential {

    private static final Logger logger = LogManager.getLogger(EntraIdCredential.class);

    private final IAuthenticationResult authResult;

    /**
     * Constructs an Entra ID credential with the authentication result.
     * @param authResult The authentication result from Entra ID.
     */
    public EntraIdCredential(final IAuthenticationResult authResult) {
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
     * Gets the Entra ID user associated with this credential.
     * @return The Entra ID user instance.
     */
    public EntraIdUser getUser() {
        return new EntraIdUser(authResult);
    }

    /**
     * Entra ID user implementation providing user information and permissions.
     */
    public static class EntraIdUser implements FessUser {
        private static final long serialVersionUID = 1L;

        /** User's group memberships. */
        protected volatile String[] groups;

        /** User's role assignments. */
        protected volatile String[] roles;

        /** User's computed permissions. */
        protected volatile String[] permissions;

        /** Entra ID authentication result. */
        protected IAuthenticationResult authResult;

        /**
         * Constructs an Entra ID user with the authentication result.
         * @param authResult The authentication result from Entra ID.
         */
        public EntraIdUser(final IAuthenticationResult authResult) {
            this.authResult = authResult;
            final EntraIdAuthenticator authenticator = ComponentUtil.getComponent(EntraIdAuthenticator.class);
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
                    logger.debug("homeAccountId={}, username={}", homeAccountId, username);
                }
                permissionSet.add(systemHelper.getSearchRoleByUser(homeAccountId));
                permissionSet.add(systemHelper.getSearchRoleByUser(username));
                if (ComponentUtil.getFessConfig().isEntraIdUseDomainServices() && username.indexOf('@') >= 0) {
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
            // Check if token is still valid by comparing absolute timestamps
            final long tokenExpiryTime = authResult.expiresOnDate().getTime(); // milliseconds since epoch
            final long currentTime = ComponentUtil.getSystemHelper().getCurrentTimeAsLong(); // milliseconds since epoch
            if (tokenExpiryTime < currentTime) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Token expired: expiryTime={}, currentTime={}", tokenExpiryTime, currentTime);
                }
                return false;
            }
            // Attempt to refresh token using MSAL4J silent authentication
            try {
                final EntraIdAuthenticator authenticator = ComponentUtil.getComponent(EntraIdAuthenticator.class);
                final IAuthenticationResult newResult = authenticator.refreshTokenSilently(this);
                if (newResult != null) {
                    authResult = newResult;
                    authenticator.updateMemberOf(this);
                    permissions = null;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Token refreshed successfully via silent authentication");
                    }
                    return true;
                }
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Silent token refresh failed: {}", e.getMessage());
                }
            }
            // For MSAL4J, if silent refresh fails, return true if token is still valid
            // Actual refresh will happen during next authentication request
            return true;
        }

        /**
         * Gets the Entra ID authentication result.
         * @return The authentication result.
         */
        public IAuthenticationResult getAuthenticationResult() {
            return authResult;
        }

        /**
         * Sets the user's group memberships.
         * @param groups Array of group names.
         */
        public synchronized void setGroups(final String[] groups) {
            this.groups = groups;
        }

        /**
         * Sets the user's role assignments.
         * @param roles Array of role names.
         */
        public synchronized void setRoles(final String[] roles) {
            this.roles = roles;
        }

        /**
         * Resets permissions to force recalculation on next getPermissions() call.
         * This is called after asynchronous parent group lookup completes.
         */
        public void resetPermissions() {
            this.permissions = null;
        }
    }
}
