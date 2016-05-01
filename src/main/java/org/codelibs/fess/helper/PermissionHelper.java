/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

public class PermissionHelper {
    protected String rolePrefix = "{role}";

    protected String groupPrefix = "{group}";

    protected String userPrefix = "{user}";

    public String encode(final String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String permission = value.trim();
        final String lower = permission.toLowerCase(Locale.ROOT);
        if (lower.startsWith(userPrefix) && permission.length() > userPrefix.length()) {
            return systemHelper.getSearchRoleByUser(permission.substring(userPrefix.length()));
        } else if (lower.startsWith(groupPrefix) && permission.length() > groupPrefix.length()) {
            return systemHelper.getSearchRoleByGroup(permission.substring(groupPrefix.length()));
        } else if (lower.startsWith(rolePrefix) && permission.length() > rolePrefix.length()) {
            return systemHelper.getSearchRoleByRole(permission.substring(rolePrefix.length()));
        }
        return permission;
    }

    public String decode(String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (value.startsWith(fessConfig.getRoleSearchUserPrefix()) && value.length() > 1) {
            return userPrefix + value.substring(1);
        } else if (value.startsWith(fessConfig.getRoleSearchGroupPrefix()) && value.length() > 1) {
            return groupPrefix + value.substring(1);
        } else if (value.startsWith(fessConfig.getRoleSearchRolePrefix()) && value.length() > 1) {
            return rolePrefix + value.substring(1);
        }
        return value;
    }

    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    public void setGroupPrefix(String groupPrefix) {
        this.groupPrefix = groupPrefix;
    }

    public void setUserPrefix(String userPrefix) {
        this.userPrefix = userPrefix;
    }
}
