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
import org.codelibs.fess.es.config.bsentity.dbmeta.DataConfigDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsDataConfig extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** available */
    protected Boolean available;

    /** boost */
    protected Float boost;

    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** description */
    protected String description;

    /** handlerName */
    protected String handlerName;

    /** handlerParameter */
    protected String handlerParameter;

    /** handlerScript */
    protected String handlerScript;

    /** name */
    protected String name;

    /** permissions */
    protected String[] permissions;

    /** sortOrder */
    protected Integer sortOrder;

    /** updatedBy */
    protected String updatedBy;

    /** updatedTime */
    protected Long updatedTime;

    /** virtualHosts */
    protected String[] virtualHosts;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public DataConfigDbm asDBMeta() {
        return DataConfigDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "data_config";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (available != null) {
            addFieldToSource(sourceMap, "available", available);
        }
        if (boost != null) {
            addFieldToSource(sourceMap, "boost", boost);
        }
        if (createdBy != null) {
            addFieldToSource(sourceMap, "createdBy", createdBy);
        }
        if (createdTime != null) {
            addFieldToSource(sourceMap, "createdTime", createdTime);
        }
        if (description != null) {
            addFieldToSource(sourceMap, "description", description);
        }
        if (handlerName != null) {
            addFieldToSource(sourceMap, "handlerName", handlerName);
        }
        if (handlerParameter != null) {
            addFieldToSource(sourceMap, "handlerParameter", handlerParameter);
        }
        if (handlerScript != null) {
            addFieldToSource(sourceMap, "handlerScript", handlerScript);
        }
        if (name != null) {
            addFieldToSource(sourceMap, "name", name);
        }
        if (permissions != null) {
            addFieldToSource(sourceMap, "permissions", permissions);
        }
        if (sortOrder != null) {
            addFieldToSource(sourceMap, "sortOrder", sortOrder);
        }
        if (updatedBy != null) {
            addFieldToSource(sourceMap, "updatedBy", updatedBy);
        }
        if (updatedTime != null) {
            addFieldToSource(sourceMap, "updatedTime", updatedTime);
        }
        if (virtualHosts != null) {
            addFieldToSource(sourceMap, "virtualHosts", virtualHosts);
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
        sb.append(dm).append(available);
        sb.append(dm).append(boost);
        sb.append(dm).append(createdBy);
        sb.append(dm).append(createdTime);
        sb.append(dm).append(description);
        sb.append(dm).append(handlerName);
        sb.append(dm).append(handlerParameter);
        sb.append(dm).append(handlerScript);
        sb.append(dm).append(name);
        sb.append(dm).append(permissions);
        sb.append(dm).append(sortOrder);
        sb.append(dm).append(updatedBy);
        sb.append(dm).append(updatedTime);
        sb.append(dm).append(virtualHosts);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Boolean getAvailable() {
        checkSpecifiedProperty("available");
        return available;
    }

    public void setAvailable(Boolean value) {
        registerModifiedProperty("available");
        this.available = value;
    }

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

    public String getDescription() {
        checkSpecifiedProperty("description");
        return convertEmptyToNull(description);
    }

    public void setDescription(String value) {
        registerModifiedProperty("description");
        this.description = value;
    }

    public String getHandlerName() {
        checkSpecifiedProperty("handlerName");
        return convertEmptyToNull(handlerName);
    }

    public void setHandlerName(String value) {
        registerModifiedProperty("handlerName");
        this.handlerName = value;
    }

    public String getHandlerParameter() {
        checkSpecifiedProperty("handlerParameter");
        return convertEmptyToNull(handlerParameter);
    }

    public void setHandlerParameter(String value) {
        registerModifiedProperty("handlerParameter");
        this.handlerParameter = value;
    }

    public String getHandlerScript() {
        checkSpecifiedProperty("handlerScript");
        return convertEmptyToNull(handlerScript);
    }

    public void setHandlerScript(String value) {
        registerModifiedProperty("handlerScript");
        this.handlerScript = value;
    }

    public String getName() {
        checkSpecifiedProperty("name");
        return convertEmptyToNull(name);
    }

    public void setName(String value) {
        registerModifiedProperty("name");
        this.name = value;
    }

    public String[] getPermissions() {
        checkSpecifiedProperty("permissions");
        return permissions;
    }

    public void setPermissions(String[] value) {
        registerModifiedProperty("permissions");
        this.permissions = value;
    }

    public Integer getSortOrder() {
        checkSpecifiedProperty("sortOrder");
        return sortOrder;
    }

    public void setSortOrder(Integer value) {
        registerModifiedProperty("sortOrder");
        this.sortOrder = value;
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

    public String[] getVirtualHosts() {
        checkSpecifiedProperty("virtualHosts");
        return virtualHosts;
    }

    public void setVirtualHosts(String[] value) {
        registerModifiedProperty("virtualHosts");
        this.virtualHosts = value;
    }
}
