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

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.lastaflute.web.login.credential.LoginCredential;

/**
 * OpenID Connect credential implementation.
 */
public class OpenIdConnectCredential implements LoginCredential, FessCredential {

    private final Map<String, Object> attributes;

    /**
     * Creates a new OpenID Connect credential.
     *
     * @param attributes the attributes from OpenID Connect provider
     */
    public OpenIdConnectCredential(final Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "{" + getUserId() + "}";
    }

    @Override
    public String getUserId() {
        return DocumentUtil.getValue(attributes, "email", String.class);
    }

    /**
     * Gets the user groups.
     *
     * @return the user groups
     */
    public String[] getUserGroups() {
        String[] userGroups = DocumentUtil.getValue(attributes, "groups", String[].class);
        if (userGroups == null) {
            userGroups = getDefaultGroupsAsArray();
        }
        return userGroups;
    }

    /**
     * Gets the OpenID Connect user.
     *
     * @return the OpenID Connect user
     */
    public OpenIdUser getUser() {
        return new OpenIdUser(getUserId(), getUserGroups(), getDefaultRolesAsArray());
    }

    /**
     * Gets the default groups as an array.
     *
     * @return the default groups
     */
    protected static String[] getDefaultGroupsAsArray() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty("oic.default.groups");
        if (StringUtil.isBlank(value)) {
            return StringUtil.EMPTY_STRINGS;
        }
        return split(value, ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).toArray(n -> new String[n]));
    }

    /**
     * Gets the default roles as an array.
     *
     * @return the default roles
     */
    protected static String[] getDefaultRolesAsArray() {
        final String value = ComponentUtil.getFessConfig().getSystemProperty("oic.default.roles");
        if (StringUtil.isBlank(value)) {
            return StringUtil.EMPTY_STRINGS;
        }
        return split(value, ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).toArray(n -> new String[n]));
    }

    /**
     * OpenID Connect user implementation.
     */
    public static class OpenIdUser implements FessUser {

        private static final long serialVersionUID = 1L;

        /** The user name. */
        protected final String name;

        /** The user groups. */
        protected String[] groups;

        /** The user roles. */
        protected String[] roles;

        /** The user permissions. */
        protected String[] permissions;

        /**
         * Creates a new OpenID Connect user.
         *
         * @param name the user name
         * @param groups the user groups
         * @param roles the user roles
         */
        protected OpenIdUser(final String name, final String[] groups, final String[] roles) {
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

    }
}
