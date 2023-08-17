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

import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.UserInfo;

public class AzureAdCredential implements LoginCredential, FessCredential {

    private static final Logger logger = LogManager.getLogger(AzureAdCredential.class);

    private final AuthenticationResult authResult;

    public AzureAdCredential(final AuthenticationResult authResult) {
        this.authResult = authResult;
    }

    @Override
    public String getUserId() {
        return authResult.getUserInfo().getDisplayableId();
    }

    @Override
    public String toString() {
        return "{" + authResult.getUserInfo().getDisplayableId() + "}";
    }

    public AzureAdUser getUser() {
        return new AzureAdUser(authResult);
    }

    public static class AzureAdUser implements FessUser {
        private static final long serialVersionUID = 1L;

        protected String[] groups;

        protected String[] roles;

        protected String[] permissions;

        protected AuthenticationResult authResult;

        public AzureAdUser(final AuthenticationResult authResult) {
            this.authResult = authResult;
            final AzureAdAuthenticator authenticator = ComponentUtil.getComponent(AzureAdAuthenticator.class);
            authenticator.updateMemberOf(this);
        }

        @Override
        public String getName() {
            return authResult.getUserInfo().getDisplayableId();
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
                final UserInfo userInfo = authResult.getUserInfo();
                final String uniqueId = userInfo.getUniqueId();
                final String displayableId = userInfo.getDisplayableId();
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
            if (authResult.getExpiresAfter() < System.currentTimeMillis()) {
                return false;
            }
            final AzureAdAuthenticator authenticator = ComponentUtil.getComponent(AzureAdAuthenticator.class);
            final String refreshToken = authResult.getRefreshToken();
            authResult = authenticator.getAccessToken(refreshToken);
            authenticator.updateMemberOf(this);
            permissions = null;
            return true;
        }

        public AuthenticationResult getAuthenticationResult() {
            return authResult;
        }

        public void setGroups(final String[] groups) {
            this.groups = groups;
        }

        public void setRoles(final String[] roles) {
            this.roles = roles;
        }
    }
}
