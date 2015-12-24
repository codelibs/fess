/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import java.util.Hashtable;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.entity.FessUser;

public class LdapUser implements FessUser {

    private static final long serialVersionUID = 1L;

    protected Hashtable<String, String> env;

    protected String name;

    public LdapUser(Hashtable<String, String> env, String name) {
        this.env = env;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getRoleNames() {
        // TODO
        return StringUtil.EMPTY_STRINGS;
    }

    @Override
    public String[] getGroupNames() {
        // TODO
        return StringUtil.EMPTY_STRINGS;
    }
}
