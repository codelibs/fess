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
package org.codelibs.fess.es.config.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.config.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.config.bsentity.dbmeta.CrawlingSessionInfoDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsCrawlingSessionInfo extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** crawlingSessionId */
    protected String crawlingSessionId;

    /** createdTime */
    protected Long createdTime;

    /** key */
    protected String key;

    /** value */
    protected String value;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public CrawlingSessionInfoDbm asDBMeta() {
        return CrawlingSessionInfoDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "crawling_session_info";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (crawlingSessionId != null) {
            sourceMap.put("crawlingSessionId", crawlingSessionId);
        }
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (key != null) {
            sourceMap.put("key", key);
        }
        if (value != null) {
            sourceMap.put("value", value);
        }
        return sourceMap;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(crawlingSessionId);
        sb.append(dm).append(createdTime);
        sb.append(dm).append(key);
        sb.append(dm).append(value);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getCrawlingSessionId() {
        checkSpecifiedProperty("crawlingSessionId");
        return convertEmptyToNull(crawlingSessionId);
    }

    public void setCrawlingSessionId(String value) {
        registerModifiedProperty("crawlingSessionId");
        this.crawlingSessionId = value;
    }

    public Long getCreatedTime() {
        checkSpecifiedProperty("createdTime");
        return createdTime;
    }

    public void setCreatedTime(Long value) {
        registerModifiedProperty("createdTime");
        this.createdTime = value;
    }

    public String getKey() {
        checkSpecifiedProperty("key");
        return convertEmptyToNull(key);
    }

    public void setKey(String value) {
        registerModifiedProperty("key");
        this.key = value;
    }

    public String getValue() {
        checkSpecifiedProperty("value");
        return convertEmptyToNull(value);
    }

    public void setValue(String value) {
        registerModifiedProperty("value");
        this.value = value;
    }
}
