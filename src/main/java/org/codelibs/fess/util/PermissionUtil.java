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
package org.codelibs.fess.util;

import java.util.Locale;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;

public class PermissionUtil {
    private static final String ROLE_PREFIX = "{role}";

    private static final String GROUP_PREFIX = "{group}";

    private static final String USER_PREFIX = "{user}";

    private PermissionUtil() {
        // nothing
    }

    public static String encode(final String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final String permission = value.trim();
        final String lower = permission.toLowerCase(Locale.ROOT);
        if (lower.startsWith(USER_PREFIX) && permission.length() > USER_PREFIX.length()) {
            return systemHelper.getSearchRoleByUser(permission.substring(USER_PREFIX.length()));
        } else if (lower.startsWith(GROUP_PREFIX) && permission.length() > GROUP_PREFIX.length()) {
            return systemHelper.getSearchRoleByGroup(permission.substring(GROUP_PREFIX.length()));
        } else if (lower.startsWith(ROLE_PREFIX) && permission.length() > ROLE_PREFIX.length()) {
            return systemHelper.getSearchRoleByRole(permission.substring(ROLE_PREFIX.length()));
        }
        return permission;
    }

    public static String decode(String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (value.startsWith(fessConfig.getRoleSearchUserPrefix()) && value.length() > 1) {
            return USER_PREFIX + value.substring(1);
        } else if (value.startsWith(fessConfig.getRoleSearchGroupPrefix()) && value.length() > 1) {
            return GROUP_PREFIX + value.substring(1);
        } else if (value.startsWith(fessConfig.getRoleSearchRolePrefix()) && value.length() > 1) {
            return ROLE_PREFIX + value.substring(1);
        }
        return value;
    }
}
