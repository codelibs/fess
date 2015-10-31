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
import org.codelibs.fess.es.config.bsentity.dbmeta.FileConfigToLabelDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsFileConfigToLabel extends EsAbstractEntity {

    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    @Override
    public FileConfigToLabelDbm asDBMeta() {
        return FileConfigToLabelDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "file_config_to_label";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** fileConfigId */
    protected String fileConfigId;

    /** labelTypeId */
    protected String labelTypeId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getFileConfigId() {
        checkSpecifiedProperty("fileConfigId");
        return fileConfigId;
    }

    public void setFileConfigId(String value) {
        registerModifiedProperty("fileConfigId");
        this.fileConfigId = value;
    }

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public String getLabelTypeId() {
        checkSpecifiedProperty("labelTypeId");
        return labelTypeId;
    }

    public void setLabelTypeId(String value) {
        registerModifiedProperty("labelTypeId");
        this.labelTypeId = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (fileConfigId != null) {
            sourceMap.put("fileConfigId", fileConfigId);
        }
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (labelTypeId != null) {
            sourceMap.put("labelTypeId", labelTypeId);
        }
        return sourceMap;
    }
}
