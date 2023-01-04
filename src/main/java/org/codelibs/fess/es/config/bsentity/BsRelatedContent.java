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
import org.codelibs.fess.es.config.bsentity.dbmeta.RelatedContentDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsRelatedContent extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** content */
    protected String content;

    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** sortOrder */
    protected Integer sortOrder;

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
    public RelatedContentDbm asDBMeta() {
        return RelatedContentDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "related_content";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (content != null) {
            addFieldToSource(sourceMap, "content", content);
        }
        if (createdBy != null) {
            addFieldToSource(sourceMap, "createdBy", createdBy);
        }
        if (createdTime != null) {
            addFieldToSource(sourceMap, "createdTime", createdTime);
        }
        if (sortOrder != null) {
            addFieldToSource(sourceMap, "sortOrder", sortOrder);
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
        sb.append(dm).append(content);
        sb.append(dm).append(createdBy);
        sb.append(dm).append(createdTime);
        sb.append(dm).append(sortOrder);
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
    public String getContent() {
        checkSpecifiedProperty("content");
        return convertEmptyToNull(content);
    }

    public void setContent(String value) {
        registerModifiedProperty("content");
        this.content = value;
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

    public Integer getSortOrder() {
        checkSpecifiedProperty("sortOrder");
        return sortOrder;
    }

    public void setSortOrder(Integer value) {
        registerModifiedProperty("sortOrder");
        this.sortOrder = value;
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
