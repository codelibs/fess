/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.config.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.config.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.config.bsentity.dbmeta.CrawlingInfoDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsCrawlingInfo extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** createdTime */
    protected Long createdTime;

    /** expiredTime */
    protected Long expiredTime;

    /** name */
    protected String name;

    /** sessionId */
    protected String sessionId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public CrawlingInfoDbm asDBMeta() {
        return CrawlingInfoDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "crawling_info";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (createdTime != null) {
            addFieldToSource(sourceMap, "createdTime", createdTime);
        }
        if (expiredTime != null) {
            addFieldToSource(sourceMap, "expiredTime", expiredTime);
        }
        if (name != null) {
            addFieldToSource(sourceMap, "name", name);
        }
        if (sessionId != null) {
            addFieldToSource(sourceMap, "sessionId", sessionId);
        }
        return sourceMap;
    }

    protected void addFieldToSource(Map<String, Object> sourceMap, String field, Object value) {
        sourceMap.put(field, value);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(createdTime);
        sb.append(dm).append(expiredTime);
        sb.append(dm).append(name);
        sb.append(dm).append(sessionId);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

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

    public Long getExpiredTime() {
        checkSpecifiedProperty("expiredTime");
        return expiredTime;
    }

    public void setExpiredTime(Long value) {
        registerModifiedProperty("expiredTime");
        this.expiredTime = value;
    }

    public String getName() {
        checkSpecifiedProperty("name");
        return convertEmptyToNull(name);
    }

    public void setName(String value) {
        registerModifiedProperty("name");
        this.name = value;
    }

    public String getSessionId() {
        checkSpecifiedProperty("sessionId");
        return convertEmptyToNull(sessionId);
    }

    public void setSessionId(String value) {
        registerModifiedProperty("sessionId");
        this.sessionId = value;
    }
}
