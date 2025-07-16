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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.saml2.Auth;
import org.lastaflute.web.login.credential.LoginCredential;

/**
 * Credential for SAML authentication.
 */
public class SamlCredential implements LoginCredential, FessCredential {

    private final Map<String, List<String>> attributes;

    private final String nameId;

    private final String nameIdFormat;

    private final String sessionIndex;

    private final String nameidNameQualifier;

    private final String nameidSPNameQualifier;

    /**
     * Constructor.
     * @param auth The SAML authentication.
     */
    public SamlCredential(final Auth auth) {
        attributes = auth.getAttributes();
        nameId = auth.getNameId();
        nameIdFormat = auth.getNameIdFormat();
        sessionIndex = auth.getSessionIndex();
        nameidNameQualifier = auth.getNameIdNameQualifier();
        nameidSPNameQualifier = auth.getNameIdSPNameQualifier();
    }

    @Override
    public String toString() {
        return "{" + getUserId() + "}";
    }

    @Override
    public String getUserId() {
        return nameId;
    }

    /**
     * Gets the SAML user.
     * @return The SAML user.
     */
    public SamlUser getUser() {
        return new SamlUser(nameId, sessionIndex, nameIdFormat, nameidNameQualifier, nameidSPNameQualifier, getDefaultGroupsAsArray(),
                getDefaultRolesAsArray());
    }

    /**
     * Gets the default groups as an array.
     * @return The default groups as an array.
     */
    protected String[] getDefaultGroupsAsArray() {
        final List<String> list = new ArrayList<>();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String key = fessConfig.getSystemProperty("saml.attribute.group.name", "memberOf");
        if (StringUtil.isNotBlank(key)) {
            final List<String> nameList = attributes.get(key);
            if (nameList != null) {
                list.addAll(nameList);
            }
        }
        final String value = fessConfig.getSystemProperty("saml.default.groups");
        if (StringUtil.isNotBlank(value)) {
            split(value, ",").of(stream -> stream.forEach(list::add));
        }
        return list.stream().filter(StringUtil::isNotBlank).map(String::trim).toArray(n -> new String[n]);
    }

    /**
     * Gets the default roles as an array.
     * @return The default roles as an array.
     */
    protected String[] getDefaultRolesAsArray() {
        final List<String> list = new ArrayList<>();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String key = fessConfig.getSystemProperty("saml.attribute.role.name");
        if (StringUtil.isNotBlank(key)) {
            final List<String> nameList = attributes.get(key);
            if (nameList != null) {
                list.addAll(nameList);
            }
        }
        final String value = fessConfig.getSystemProperty("saml.default.roles");
        if (StringUtil.isNotBlank(value)) {
            split(value, ",").of(stream -> stream.forEach(list::add));
        }
        return list.stream().filter(StringUtil::isNotBlank).map(String::trim).toArray(n -> new String[n]);
    }

    /**
     * Represents a SAML user.
     */
    public static class SamlUser implements FessUser {

        private static final long serialVersionUID = 1L;

        /**
         * The groups of the user.
         */
        protected String[] groups;

        /**
         * The roles of the user.
         */
        protected String[] roles;

        /**
         * The permissions of the user.
         */
        protected String[] permissions;

        /**
         * The name ID of the user.
         */
        protected String nameId;

        /**
         * The session index of the user.
         */
        protected String sessionIndex;

        /**
         * The name ID format of the user.
         */
        protected String nameIdFormat;

        /**
         * The name ID name qualifier of the user.
         */
        protected String nameidNameQualifier;

        /**
         * The name ID SP name qualifier of the user.
         */
        protected String nameidSPNameQualifier;

        /**
         * Constructor.
         * @param nameId The name ID.
         * @param sessionIndex The session index.
         * @param nameIdFormat The name ID format.
         * @param nameidNameQualifier The name ID name qualifier.
         * @param nameidSPNameQualifier The name ID SP name qualifier.
         * @param groups The groups.
         * @param roles The roles.
         */
        public SamlUser(final String nameId, final String sessionIndex, final String nameIdFormat, final String nameidNameQualifier,
                final String nameidSPNameQualifier, final String[] groups, final String[] roles) {
            this.nameId = nameId;
            this.sessionIndex = sessionIndex;
            this.nameIdFormat = nameIdFormat;
            this.nameidNameQualifier = nameidNameQualifier;
            this.nameidSPNameQualifier = nameidSPNameQualifier;
            this.groups = groups;
            this.roles = roles;
        }

        @Override
        public String getName() {
            return nameId;
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
                permissionSet.add(systemHelper.getSearchRoleByUser(nameId));
                stream(groups).of(stream -> stream.forEach(s -> permissionSet.add(systemHelper.getSearchRoleByGroup(s))));
                stream(roles).of(stream -> stream.forEach(s -> permissionSet.add(systemHelper.getSearchRoleByRole(s))));
                permissions = permissionSet.toArray(new String[permissionSet.size()]);
            }
            return permissions;
        }

        /**
         * Gets the session index.
         * @return The session index.
         */
        public String getSessionIndex() {
            return sessionIndex;
        }

        /**
         * Gets the name ID format.
         * @return The name ID format.
         */
        public String getNameIdFormat() {
            return nameIdFormat;
        }

        /**
         * Gets the name ID name qualifier.
         * @return The name ID name qualifier.
         */
        public String getNameidNameQualifier() {
            return nameidNameQualifier;
        }

        /**
         * Gets the name ID SP name qualifier.
         * @return The name ID SP name qualifier.
         */
        public String getNameidSPNameQualifier() {
            return nameidSPNameQualifier;
        }

        @Override
        public String toString() {
            return "SamlUser [groups=" + Arrays.toString(groups) + ", roles=" + Arrays.toString(roles) + ", permissions="
                    + Arrays.toString(permissions) + ", nameId=" + nameId + ", sessionIndex=" + sessionIndex + ", nameIdFormat="
                    + nameIdFormat + ", nameidNameQualifier=" + nameidNameQualifier + ", nameidSPNameQualifier=" + nameidSPNameQualifier
                    + "]";
        }

    }
}
