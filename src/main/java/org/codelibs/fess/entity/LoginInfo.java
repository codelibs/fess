/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

package org.codelibs.fess.entity;

import org.codelibs.fess.util.ComponentUtil;

public class LoginInfo extends UserInfo {

    private static final long serialVersionUID = 1L;

    protected long updatedTime = System.currentTimeMillis();

    public void setUpdatedTime(final long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public boolean isAdministrator() {
        for (final String role : ComponentUtil.getSystemHelper().getAdminRoleSet()) {
            if (isUserInRole(role)) {
                return true;
            }
        }
        return false;
    }

}
