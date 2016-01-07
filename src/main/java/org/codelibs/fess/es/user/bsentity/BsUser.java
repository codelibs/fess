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
package org.codelibs.fess.es.user.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.user.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.user.bsentity.dbmeta.UserDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsUser extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** groups */
    protected String[] groups;

    /** name */
    protected String name;

    /** password */
    protected String password;

    /** roles */
    protected String[] roles;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public UserDbm asDBMeta() {
        return UserDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "user";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (groups != null) {
            sourceMap.put("groups", groups);
        }
        if (name != null) {
            sourceMap.put("name", name);
        }
        if (password != null) {
            sourceMap.put("password", password);
        }
        if (roles != null) {
            sourceMap.put("roles", roles);
        }
        return sourceMap;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(groups);
        sb.append(dm).append(name);
        sb.append(dm).append(password);
        sb.append(dm).append(roles);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String[] getGroups() {
        checkSpecifiedProperty("groups");
        return groups;
    }

    public void setGroups(String[] value) {
        registerModifiedProperty("groups");
        this.groups = value;
    }

    public String getName() {
        checkSpecifiedProperty("name");
        return convertEmptyToNull(name);
    }

    public void setName(String value) {
        registerModifiedProperty("name");
        this.name = value;
    }

    public String getPassword() {
        checkSpecifiedProperty("password");
        return convertEmptyToNull(password);
    }

    public void setPassword(String value) {
        registerModifiedProperty("password");
        this.password = value;
    }

    public String[] getRoles() {
        checkSpecifiedProperty("roles");
        return roles;
    }

    public void setRoles(String[] value) {
        registerModifiedProperty("roles");
        this.roles = value;
    }
}
