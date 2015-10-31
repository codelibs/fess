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
import org.codelibs.fess.es.log.bsentity.dbmeta.SearchFieldLogDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsSearchFieldLog extends EsAbstractEntity {

    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    @Override
    public SearchFieldLogDbm asDBMeta() {
        return SearchFieldLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "search_field_log";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** name */
    protected String name;

    /** searchLogId */
    protected String searchLogId;

    /** value */
    protected String value;

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

    public String getName() {
        checkSpecifiedProperty("name");
        return name;
    }

    public void setName(String value) {
        registerModifiedProperty("name");
        this.name = value;
    }

    public String getSearchLogId() {
        checkSpecifiedProperty("searchLogId");
        return searchLogId;
    }

    public void setSearchLogId(String value) {
        registerModifiedProperty("searchLogId");
        this.searchLogId = value;
    }

    public String getValue() {
        checkSpecifiedProperty("value");
        return value;
    }

    public void setValue(String value) {
        registerModifiedProperty("value");
        this.value = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (name != null) {
            sourceMap.put("name", name);
        }
        if (searchLogId != null) {
            sourceMap.put("searchLogId", searchLogId);
        }
        if (value != null) {
            sourceMap.put("value", value);
        }
        return sourceMap;
    }
}
