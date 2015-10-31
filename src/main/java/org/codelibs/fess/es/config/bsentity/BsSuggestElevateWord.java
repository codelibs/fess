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
import org.codelibs.fess.es.config.bsentity.dbmeta.SuggestElevateWordDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsSuggestElevateWord extends EsAbstractEntity {

    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    @Override
    public SuggestElevateWordDbm asDBMeta() {
        return SuggestElevateWordDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "suggest_elevate_word";
    }

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

    /** targetRole */
    protected String targetRole;

    /** updatedBy */
    protected String updatedBy;

    /** updatedTime */
    protected Long updatedTime;

    // [Referrers] *comment only

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
        return createdBy;
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

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public String getReading() {
        checkSpecifiedProperty("reading");
        return reading;
    }

    public void setReading(String value) {
        registerModifiedProperty("reading");
        this.reading = value;
    }

    public String getSuggestWord() {
        checkSpecifiedProperty("suggestWord");
        return suggestWord;
    }

    public void setSuggestWord(String value) {
        registerModifiedProperty("suggestWord");
        this.suggestWord = value;
    }

    public String getTargetLabel() {
        checkSpecifiedProperty("targetLabel");
        return targetLabel;
    }

    public void setTargetLabel(String value) {
        registerModifiedProperty("targetLabel");
        this.targetLabel = value;
    }

    public String getTargetRole() {
        checkSpecifiedProperty("targetRole");
        return targetRole;
    }

    public void setTargetRole(String value) {
        registerModifiedProperty("targetRole");
        this.targetRole = value;
    }

    public String getUpdatedBy() {
        checkSpecifiedProperty("updatedBy");
        return updatedBy;
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
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
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
        if (targetRole != null) {
            sourceMap.put("targetRole", targetRole);
        }
        if (updatedBy != null) {
            sourceMap.put("updatedBy", updatedBy);
        }
        if (updatedTime != null) {
            sourceMap.put("updatedTime", updatedTime);
        }
        return sourceMap;
    }
}
