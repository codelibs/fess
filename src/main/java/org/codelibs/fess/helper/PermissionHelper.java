/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.helper;

import java.util.Locale;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public class PermissionHelper {
    protected String rolePrefix = "{role}";

    protected String groupPrefix = "{group}";

    protected String userPrefix = "{user}";

    @Resource
    protected SystemHelper systemHelper;

    public String encode(final String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        final String permission = value.trim();
        final String lower = permission.toLowerCase(Locale.ROOT);
        if (lower.startsWith(userPrefix)) {
            if (permission.length() > userPrefix.length()) {
                return systemHelper.getSearchRoleByUser(permission.substring(userPrefix.length()));
            } else {
                return null;
            }
        } else if (lower.startsWith(groupPrefix)) {
            if (permission.length() > groupPrefix.length()) {
                return systemHelper.getSearchRoleByGroup(permission.substring(groupPrefix.length()));
            } else {
                return null;
            }
        } else if (lower.startsWith(rolePrefix)) {
            if (permission.length() > rolePrefix.length()) {
                return systemHelper.getSearchRoleByRole(permission.substring(rolePrefix.length()));
            } else {
                return null;
            }
        }
        return permission;
    }

    public String decode(final String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (value.startsWith(fessConfig.getRoleSearchUserPrefix()) && value.length() > fessConfig.getRoleSearchUserPrefix().length()) {
            return userPrefix + value.substring(fessConfig.getRoleSearchUserPrefix().length());
        } else if (value.startsWith(fessConfig.getRoleSearchGroupPrefix())
                && value.length() > fessConfig.getRoleSearchGroupPrefix().length()) {
            return groupPrefix + value.substring(fessConfig.getRoleSearchGroupPrefix().length());
        } else if (value.startsWith(fessConfig.getRoleSearchRolePrefix()) && value.length() > fessConfig.getRoleSearchRolePrefix().length()) {
            return rolePrefix + value.substring(fessConfig.getRoleSearchRolePrefix().length());
        }
        return value;
    }

    public void setRolePrefix(final String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    public void setGroupPrefix(final String groupPrefix) {
        this.groupPrefix = groupPrefix;
    }

    public void setUserPrefix(final String userPrefix) {
        this.userPrefix = userPrefix;
    }
}
