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
package org.codelibs.fess.es.config.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.config.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.config.bsentity.dbmeta.ElevateWordDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsElevateWord extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** boost */
    protected Float boost;

    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** reading */
    protected String reading;

    /** suggestWord */
    protected String suggestWord;

    /** targetLabel */
    protected String targetLabel;

    /** permissions */
    protected String permissions;

    /** updatedBy */
    protected String updatedBy;

    /** updatedTime */
    protected Long updatedTime;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public ElevateWordDbm asDBMeta() {
        return ElevateWordDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "elevate_word";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (boost != null) {
            sourceMap.put("boost", boost);
        }
        if (createdBy != null) {
            sourceMap.put("createdBy", createdBy);
        }
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (reading != null) {
            sourceMap.put("reading", reading);
        }
        if (suggestWord != null) {
            sourceMap.put("suggestWord", suggestWord);
        }
        if (targetLabel != null) {
            sourceMap.put("targetLabel", targetLabel);
        }
        if (permissions != null) {
            sourceMap.put("permissions", permissions);
        }
        if (updatedBy != null) {
            sourceMap.put("updatedBy", updatedBy);
        }
        if (updatedTime != null) {
            sourceMap.put("updatedTime", updatedTime);
        }
        return sourceMap;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(boost);
        sb.append(dm).append(createdBy);
        sb.append(dm).append(createdTime);
        sb.append(dm).append(reading);
        sb.append(dm).append(suggestWord);
        sb.append(dm).append(targetLabel);
        sb.append(dm).append(permissions);
        sb.append(dm).append(updatedBy);
        sb.append(dm).append(updatedTime);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Float getBoost() {
        checkSpecifiedProperty("boost");
        return boost;
    }

    public void setBoost(Float value) {
        registerModifiedProperty("boost");
        this.boost = value;
    }

    public String getCreatedBy() {
        checkSpecifiedProperty("createdBy");
        return convertEmptyToNull(createdBy);
    }

    public void setCreatedBy(String value) {
        registerModifiedProperty("createdBy");
        this.createdBy = value;
    }

    public Long getCreatedTime() {
        checkSpecifiedProperty("createdTime");
        return createdTime;
    }

    public void setCreatedTime(Long value) {
        registerModifiedProperty("createdTime");
        this.createdTime = value;
    }

    public String getReading() {
        checkSpecifiedProperty("reading");
        return convertEmptyToNull(reading);
    }

    public void setReading(String value) {
        registerModifiedProperty("reading");
        this.reading = value;
    }

    public String getSuggestWord() {
        checkSpecifiedProperty("suggestWord");
        return convertEmptyToNull(suggestWord);
    }

    public void setSuggestWord(String value) {
        registerModifiedProperty("suggestWord");
        this.suggestWord = value;
    }

    public String getTargetLabel() {
        checkSpecifiedProperty("targetLabel");
        return convertEmptyToNull(targetLabel);
    }

    public void setTargetLabel(String value) {
        registerModifiedProperty("targetLabel");
        this.targetLabel = value;
    }

    public String getPermissions() {
        checkSpecifiedProperty("permissions");
        return convertEmptyToNull(permissions);
    }

    public void setPermissions(String value) {
        registerModifiedProperty("permissions");
        this.permissions = value;
    }

    public String getUpdatedBy() {
        checkSpecifiedProperty("updatedBy");
        return convertEmptyToNull(updatedBy);
    }

    public void setUpdatedBy(String value) {
        registerModifiedProperty("updatedBy");
        this.updatedBy = value;
    }

    public Long getUpdatedTime() {
        checkSpecifiedProperty("updatedTime");
        return updatedTime;
    }

    public void setUpdatedTime(Long value) {
        registerModifiedProperty("updatedTime");
        this.updatedTime = value;
    }
}
