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
import org.codelibs.fess.es.config.bsentity.dbmeta.CrawlingInfoParamDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsCrawlingInfoParam extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** crawlingInfoId */
    protected String crawlingInfoId;

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
    public CrawlingInfoParamDbm asDBMeta() {
        return CrawlingInfoParamDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "crawling_info_param";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (crawlingInfoId != null) {
            addFieldToSource(sourceMap, "crawlingInfoId", crawlingInfoId);
        }
        if (createdTime != null) {
            addFieldToSource(sourceMap, "createdTime", createdTime);
        }
        if (key != null) {
            addFieldToSource(sourceMap, "key", key);
        }
        if (value != null) {
            addFieldToSource(sourceMap, "value", value);
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
        sb.append(dm).append(crawlingInfoId);
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
    public String getCrawlingInfoId() {
        checkSpecifiedProperty("crawlingInfoId");
        return convertEmptyToNull(crawlingInfoId);
    }

    public void setCrawlingInfoId(String value) {
        registerModifiedProperty("crawlingInfoId");
        this.crawlingInfoId = value;
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
