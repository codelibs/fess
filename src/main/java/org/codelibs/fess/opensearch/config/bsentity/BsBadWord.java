/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.opensearch.config.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity;
import org.codelibs.fess.opensearch.config.bsentity.dbmeta.BadWordDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsBadWord extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

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
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public BadWordDbm asDBMeta() {
        return BadWordDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "bad_word";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (createdBy != null) {
            addFieldToSource(sourceMap, "createdBy", createdBy);
        }
        if (createdTime != null) {
            addFieldToSource(sourceMap, "createdTime", createdTime);
        }
        if (suggestWord != null) {
            addFieldToSource(sourceMap, "suggestWord", suggestWord);
        }
        if (targetLabel != null) {
            addFieldToSource(sourceMap, "targetLabel", targetLabel);
        }
        if (targetRole != null) {
            addFieldToSource(sourceMap, "targetRole", targetRole);
        }
        if (updatedBy != null) {
            addFieldToSource(sourceMap, "updatedBy", updatedBy);
        }
        if (updatedTime != null) {
            addFieldToSource(sourceMap, "updatedTime", updatedTime);
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
        sb.append(dm).append(createdBy);
        sb.append(dm).append(createdTime);
        sb.append(dm).append(suggestWord);
        sb.append(dm).append(targetLabel);
        sb.append(dm).append(targetRole);
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

    public String getTargetRole() {
        checkSpecifiedProperty("targetRole");
        return convertEmptyToNull(targetRole);
    }

    public void setTargetRole(String value) {
        registerModifiedProperty("targetRole");
        this.targetRole = value;
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
