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
package org.codelibs.fess.ldap;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.Hashtable;

import org.apache.commons.lang3.ArrayUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public class LdapUser implements FessUser {

    private static final long serialVersionUID = 1L;

    protected Hashtable<String, String> env;

    protected String name;

    protected String[] permissions = null;

    public LdapUser(final Hashtable<String, String> env, final String name) {
        this.env = env;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getPermissions() {
        if (permissions == null) {
            final FessConfig fessConfig = ComponentUtil.getFessConfig();
            final String baseDn = fessConfig.getLdapBaseDn();
            final String accountFilter = fessConfig.getLdapAccountFilter();
            final String groupFilter = fessConfig.getLdapGroupFilter();
            if (StringUtil.isNotBlank(baseDn) && StringUtil.isNotBlank(accountFilter)) {
                permissions =
                        ArrayUtils.addAll(ComponentUtil.getLdapManager().getRoles(this, baseDn, accountFilter, groupFilter),
                                fessConfig.getRoleSearchUserPrefix() + getName());
            } else {
                permissions = StringUtil.EMPTY_STRINGS;
            }
        }
        return permissions;
    }

    @Override
    public String[] getRoleNames() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return stream(getPermissions()).get(
                stream -> stream.filter(s -> s.startsWith(fessConfig.getRoleSearchRolePrefix())).map(s -> s.substring(1))
                        .toArray(n -> new String[n]));
    }

    @Override
    public String[] getGroupNames() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        return stream(getPermissions()).get(
                stream -> stream.filter(s -> s.startsWith(fessConfig.getRoleSearchGroupPrefix())).map(s -> s.substring(1))
                        .toArray(n -> new String[n]));
    }

    public Hashtable<String, String> getEnvironment() {
        return env;
    }

    @Override
    public boolean isEditable() {
        return ComponentUtil.getFessConfig().isLdapAdminEnabled(name);
    }

}
