/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.seasar.framework.util.StringUtil;

public class LoginInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String username;

    protected boolean administrator = false;

    protected List<String> roleList = new ArrayList<String>();

    protected long updatedTime = System.currentTimeMillis();

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(final boolean administrator) {
        this.administrator = administrator;
    }

    public void addRole(final String role) {
        if (StringUtil.isNotBlank(role)) {
            roleList.add(role);
        }
    }

    public void setRoleList(final List<String> roleList) {
        this.roleList = roleList;
    }

    public List<String> getRoleList() {
        return roleList;
    }

    public void setUpdatedTime(final long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }
}
