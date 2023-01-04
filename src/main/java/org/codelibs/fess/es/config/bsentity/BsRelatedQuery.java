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
import org.codelibs.fess.es.config.bsentity.dbmeta.RelatedQueryDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsRelatedQuery extends EsAbstractEntity {

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

    /** queries */
    protected String[] queries;

    /** term */
    protected String term;

    /** updatedBy */
    protected String updatedBy;

    /** updatedTime */
    protected Long updatedTime;

    /** virtualHost */
    protected String virtualHost;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public RelatedQueryDbm asDBMeta() {
        return RelatedQueryDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "related_query";
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
        if (queries != null) {
            addFieldToSource(sourceMap, "queries", queries);
        }
        if (term != null) {
            addFieldToSource(sourceMap, "term", term);
        }
        if (updatedBy != null) {
            addFieldToSource(sourceMap, "updatedBy", updatedBy);
        }
        if (updatedTime != null) {
            addFieldToSource(sourceMap, "updatedTime", updatedTime);
        }
        if (virtualHost != null) {
            addFieldToSource(sourceMap, "virtualHost", virtualHost);
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
        sb.append(dm).append(queries);
        sb.append(dm).append(term);
        sb.append(dm).append(updatedBy);
        sb.append(dm).append(updatedTime);
        sb.append(dm).append(virtualHost);
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

    public String[] getQueries() {
        checkSpecifiedProperty("queries");
        return queries;
    }

    public void setQueries(String[] value) {
        registerModifiedProperty("queries");
        this.queries = value;
    }

    public String getTerm() {
        checkSpecifiedProperty("term");
        return convertEmptyToNull(term);
    }

    public void setTerm(String value) {
        registerModifiedProperty("term");
        this.term = value;
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

    public String getVirtualHost() {
        checkSpecifiedProperty("virtualHost");
        return convertEmptyToNull(virtualHost);
    }

    public void setVirtualHost(String value) {
        registerModifiedProperty("virtualHost");
        this.virtualHost = value;
    }
}
