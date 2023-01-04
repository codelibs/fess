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
import org.codelibs.fess.es.config.bsentity.dbmeta.ElevateWordToLabelDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsElevateWordToLabel extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** elevateWordId */
    protected String elevateWordId;

    /** labelTypeId */
    protected String labelTypeId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public ElevateWordToLabelDbm asDBMeta() {
        return ElevateWordToLabelDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "elevate_word_to_label";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (elevateWordId != null) {
            addFieldToSource(sourceMap, "elevateWordId", elevateWordId);
        }
        if (labelTypeId != null) {
            addFieldToSource(sourceMap, "labelTypeId", labelTypeId);
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
        sb.append(dm).append(elevateWordId);
        sb.append(dm).append(labelTypeId);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getElevateWordId() {
        checkSpecifiedProperty("elevateWordId");
        return convertEmptyToNull(elevateWordId);
    }

    public void setElevateWordId(String value) {
        registerModifiedProperty("elevateWordId");
        this.elevateWordId = value;
    }

    public String getLabelTypeId() {
        checkSpecifiedProperty("labelTypeId");
        return convertEmptyToNull(labelTypeId);
    }

    public void setLabelTypeId(String value) {
        registerModifiedProperty("labelTypeId");
        this.labelTypeId = value;
    }
}
