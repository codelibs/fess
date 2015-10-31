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
package org.codelibs.fess.es.log.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.log.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.log.bsentity.dbmeta.FavoriteLogDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsFavoriteLog extends EsAbstractEntity {

    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    @Override
    public FavoriteLogDbm asDBMeta() {
        return FavoriteLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "favorite_log";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** createdTime */
    protected Long createdTime;

    /** url */
    protected String url;

    /** userInfoId */
    protected String userInfoId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Long getCreatedTime() {
        checkSpecifiedProperty("createdTime");
        return createdTime;
    }

    public void setCreatedTime(Long value) {
        registerModifiedProperty("createdTime");
        this.createdTime = value;
    }

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public String getUrl() {
        checkSpecifiedProperty("url");
        return url;
    }

    public void setUrl(String value) {
        registerModifiedProperty("url");
        this.url = value;
    }

    public String getUserInfoId() {
        checkSpecifiedProperty("userInfoId");
        return userInfoId;
    }

    public void setUserInfoId(String value) {
        registerModifiedProperty("userInfoId");
        this.userInfoId = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (url != null) {
            sourceMap.put("url", url);
        }
        if (userInfoId != null) {
            sourceMap.put("userInfoId", userInfoId);
        }
        return sourceMap;
    }
}
