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
import org.lastaflute.web.login.credential.LoginCredential;

import com.onelogin.saml2.Auth;

public class SamlCredential implements LoginCredential, FessCredential {

    private final Map<String, List<String>> attributes;

    private final String nameId;

    private final String nameIdFormat;

    private final String sessionIndex;

    private final String nameidNameQualifier;

    private final String nameidSPNameQualifier;

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

    public SamlUser getUser() {
        return new SamlUser(nameId, sessionIndex, nameIdFormat, nameidNameQualifier, nameidSPNameQualifier, getDefaultGroupsAsArray(),
                getDefaultRolesAsArray());
    }

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

    public static class SamlUser implements FessUser {

        private static final long serialVersionUID = 1L;

        protected String[] groups;

        protected String[] roles;

        protected String[] permissions;

        protected String nameId;

        protected String sessionIndex;

        protected String nameIdFormat;

        protected String nameidNameQualifier;

        protected String nameidSPNameQualifier;

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

        public String getSessionIndex() {
            return sessionIndex;
        }

        public String getNameIdFormat() {
            return nameIdFormat;
        }

        public String getNameidNameQualifier() {
            return nameidNameQualifier;
        }

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
