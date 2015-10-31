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
import org.codelibs.fess.es.log.bsentity.dbmeta.ClickLogDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsClickLog extends EsAbstractEntity {

    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    @Override
    public ClickLogDbm asDBMeta() {
        return ClickLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "click_log";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** requestedTime */
    protected Long requestedTime;

    /** searchLogId */
    protected String searchLogId;

    /** url */
    protected String url;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public Long getRequestedTime() {
        checkSpecifiedProperty("requestedTime");
        return requestedTime;
    }

    public void setRequestedTime(Long value) {
        registerModifiedProperty("requestedTime");
        this.requestedTime = value;
    }

    public String getSearchLogId() {
        checkSpecifiedProperty("searchLogId");
        return searchLogId;
    }

    public void setSearchLogId(String value) {
        registerModifiedProperty("searchLogId");
        this.searchLogId = value;
    }

    public String getUrl() {
        checkSpecifiedProperty("url");
        return url;
    }

    public void setUrl(String value) {
        registerModifiedProperty("url");
        this.url = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (requestedTime != null) {
            sourceMap.put("requestedTime", requestedTime);
        }
        if (searchLogId != null) {
            sourceMap.put("searchLogId", searchLogId);
        }
        if (url != null) {
            sourceMap.put("url", url);
        }
        return sourceMap;
    }
}
