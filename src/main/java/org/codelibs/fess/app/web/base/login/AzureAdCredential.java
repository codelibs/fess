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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.sso.aad.AzureAdAuthenticator;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.login.credential.LoginCredential;

import org.codelibs.fess.sso.aad.AzureAdAuthenticator.AzureAuthenticationResult;

/**
 * Azure Active Directory credential implementation for Fess authentication.
 * Provides login credential functionality using Azure AD authentication results.
 */
public class AzureAdCredential implements LoginCredential, FessCredential {

    private static final Logger logger = LogManager.getLogger(AzureAdCredential.class);

    private final AzureAuthenticationResult authResult;

    /**
     * Constructs an Azure AD credential with the authentication result.
     * @param authResult The authentication result from Azure AD.
     */
    public AzureAdCredential(final AzureAuthenticationResult authResult) {
        this.authResult = authResult;
    }

    @Override
    public String getUserId() {
        final String idToken = authResult.getIdToken();
        if (StringUtil.isNotBlank(idToken)) {
            try {
                final Map<String, Object> claims = parseIdTokenClaims(idToken);
                final String oid = (String) claims.get("oid"); // Object ID
                final String sub = (String) claims.get("sub"); // Subject
                final String upn = (String) claims.get("upn"); // User Principal Name
                final String email = (String) claims.get("email");

                // Prefer oid (object ID) as it's most stable, fallback to sub, then upn, then email
                if (StringUtil.isNotBlank(oid)) {
                    return oid;
                } else if (StringUtil.isNotBlank(sub)) {
                    return sub;
                } else if (StringUtil.isNotBlank(upn)) {
                    return upn;
                } else if (StringUtil.isNotBlank(email)) {
                    return email;
                }
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse ID token for user ID", e);
                }
            }
        }
        // Fallback to a default value if ID token parsing fails
        return "azure-user-unknown";
    }

    @Override
    public String toString() {
        return "{" + getUserId() + "}";
    }

    /**
     * Parses ID token claims from JWT.
     * @param idToken The ID token string.
     * @return Map of claims from the ID token.
     */
    private Map<String, Object> parseIdTokenClaims(final String idToken) {
        try {
            final com.nimbusds.jwt.JWTClaimsSet claimsSet = com.nimbusds.jwt.JWTParser.parse(idToken).getJWTClaimsSet();
            return claimsSet.getClaims();
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse ID token claims", e);
            }
            return new java.util.HashMap<>();
        }
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
        protected AzureAuthenticationResult authResult;

        /**
         * Constructs an Azure AD user with the authentication result.
         * @param authResult The authentication result from Azure AD.
         */
        public AzureAdUser(final AzureAuthenticationResult authResult) {
            this.authResult = authResult;
            final AzureAdAuthenticator authenticator = ComponentUtil.getComponent(AzureAdAuthenticator.class);
            authenticator.updateMemberOf(this);
        }

        @Override
        public String getName() {
            final String idToken = authResult.getIdToken();
            if (StringUtil.isNotBlank(idToken)) {
                try {
                    final com.nimbusds.jwt.JWTClaimsSet claimsSet = com.nimbusds.jwt.JWTParser.parse(idToken).getJWTClaimsSet();
                    final Map<String, Object> claims = claimsSet.getClaims();

                    final String name = (String) claims.get("name");
                    final String preferredUsername = (String) claims.get("preferred_username");
                    final String givenName = (String) claims.get("given_name");
                    final String familyName = (String) claims.get("family_name");
                    final String upn = (String) claims.get("upn");
                    final String email = (String) claims.get("email");

                    // Prefer name, then preferred_username, then combination of given+family names
                    if (StringUtil.isNotBlank(name)) {
                        return name;
                    } else if (StringUtil.isNotBlank(preferredUsername)) {
                        return preferredUsername;
                    } else if (StringUtil.isNotBlank(givenName) && StringUtil.isNotBlank(familyName)) {
                        return givenName + " " + familyName;
                    } else if (StringUtil.isNotBlank(givenName)) {
                        return givenName;
                    } else if (StringUtil.isNotBlank(upn)) {
                        return upn;
                    } else if (StringUtil.isNotBlank(email)) {
                        return email;
                    }
                } catch (final Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to parse ID token for user name", e);
                    }
                }
            }
            // Fallback to a default value if ID token parsing fails
            return "Azure User";
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

                // Extract user information from ID token
                String uniqueId = "azure-unknown-id";
                String displayableId = "azure-unknown-user";

                final String idToken = authResult.getIdToken();
                if (StringUtil.isNotBlank(idToken)) {
                    try {
                        final com.nimbusds.jwt.JWTClaimsSet claimsSet = com.nimbusds.jwt.JWTParser.parse(idToken).getJWTClaimsSet();
                        final Map<String, Object> claims = claimsSet.getClaims();

                        final String oid = (String) claims.get("oid"); // Object ID
                        final String sub = (String) claims.get("sub"); // Subject
                        final String upn = (String) claims.get("upn"); // User Principal Name
                        final String preferredUsername = (String) claims.get("preferred_username");
                        final String email = (String) claims.get("email");
                        final String name = (String) claims.get("name");

                        // Set uniqueId (prefer oid, fallback to sub)
                        if (StringUtil.isNotBlank(oid)) {
                            uniqueId = oid;
                        } else if (StringUtil.isNotBlank(sub)) {
                            uniqueId = sub;
                        }

                        // Set displayableId (prefer UPN, then preferred_username, then email, then name)
                        if (StringUtil.isNotBlank(upn)) {
                            displayableId = upn;
                        } else if (StringUtil.isNotBlank(preferredUsername)) {
                            displayableId = preferredUsername;
                        } else if (StringUtil.isNotBlank(email)) {
                            displayableId = email;
                        } else if (StringUtil.isNotBlank(name)) {
                            displayableId = name;
                        }

                    } catch (final Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Failed to parse ID token for permissions", e);
                        }
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("uniqueId:{} displayableId:{}", uniqueId, displayableId);
                }
                permissionSet.add(systemHelper.getSearchRoleByUser(uniqueId));
                permissionSet.add(systemHelper.getSearchRoleByUser(displayableId));
                if (ComponentUtil.getFessConfig().isAzureAdUseDomainServices() && displayableId.indexOf('@') >= 0) {
                    final String[] values = displayableId.split("@");
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
            // Check if the current token is expired
            if (authResult.isExpired()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Access token is expired, refresh is needed but not supported with azure-identity library");
                }
                // azure-identity library doesn't support refresh token flow directly
                // Users will need to re-authenticate when tokens expire
                return false;
            }

            // Token is still valid, update group membership information
            try {
                final AzureAdAuthenticator authenticator = ComponentUtil.getComponent(AzureAdAuthenticator.class);
                authenticator.updateMemberOf(this);
                permissions = null; // Clear cached permissions to force re-calculation
                return true;
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to update group membership", e);
                }
                return false;
            }
        }

        /**
         * Gets the Azure AD authentication result.
         * @return The authentication result.
         */
        public AzureAuthenticationResult getAuthenticationResult() {
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
