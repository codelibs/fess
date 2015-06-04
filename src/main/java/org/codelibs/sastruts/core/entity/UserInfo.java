/*
 * Copyright 2012 the CodeLibs Project and the Others.
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
package org.codelibs.sastruts.core.entity;

import java.io.Serializable;
import java.util.Set;

public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    protected Set<String> roleSet;

    public Set<String> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(Set<String> roleSet) {
        this.roleSet = roleSet;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public boolean isUserInRole(String role) {
        if (roleSet != null) {
            return roleSet.contains(role);
        }
        return false;
    }

    @Override
    public String toString() {
        return "UserInfo [username=" + username + ", roleSet=" + roleSet + "]";
    }
}
