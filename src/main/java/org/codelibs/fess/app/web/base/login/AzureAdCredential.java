/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.HashSet;
import java.util.Set;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.login.credential.LoginCredential;

import com.microsoft.aad.adal4j.AuthenticationResult;

public class AzureAdCredential implements LoginCredential, FessCredential {

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

    public User getUser() {
        return new User(authResult.getUserInfo().getDisplayableId(), getDefaultGroupsAsArray(), getDefaultRolesAsArray());
    }

    protected static String[] getDefaultGroupsAsArray() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty("azuread.default.groups");
        if (StringUtil.isBlank(value)) {
            return StringUtil.EMPTY_STRINGS;
        } else {
            return split(value, ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> s.trim()).toArray(n -> new String[n]));
        }
    }

    protected static String[] getDefaultRolesAsArray() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty("azuread.default.roles");
        if (StringUtil.isBlank(value)) {
            return StringUtil.EMPTY_STRINGS;
        } else {
            return split(value, ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> s.trim()).toArray(n -> new String[n]));
        }
    }

    public static class User implements FessUser {
        private static final long serialVersionUID = 1L;

        protected final String name;

        protected String[] groups;

        protected String[] roles;

        protected String[] permissions;

        protected User(final String name, final String[] groups, final String[] roles) {
            this.name = name;
            this.groups = groups;
            this.roles = roles;
        }

        @Override
        public String getName() {
            return name;
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
                permissionSet.add(systemHelper.getSearchRoleByUser(name));
                stream(groups).of(stream -> stream.forEach(s -> permissionSet.add(systemHelper.getSearchRoleByGroup(s))));
                stream(roles).of(stream -> stream.forEach(s -> permissionSet.add(systemHelper.getSearchRoleByRole(s))));
                permissions = permissionSet.toArray(new String[permissionSet.size()]);
            }
            return permissions;
        }

        @Override
        public boolean isEditable() {
            return false;
        }

    }
}
