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
import org.codelibs.fess.es.config.bsentity.dbmeta.FileConfigDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsFileConfig extends EsAbstractEntity {

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

    /** configParameter */
    protected String configParameter;

    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** depth */
    protected Integer depth;

    /** description */
    protected String description;

    /** excludedDocPaths */
    protected String excludedDocPaths;

    /** excludedPaths */
    protected String excludedPaths;

    /** includedDocPaths */
    protected String includedDocPaths;

    /** includedPaths */
    protected String includedPaths;

    /** intervalTime */
    protected Integer intervalTime;

    /** maxAccessCount */
    protected Long maxAccessCount;

    /** name */
    protected String name;

    /** numOfThread */
    protected Integer numOfThread;

    /** paths */
    protected String paths;

    /** permissions */
    protected String[] permissions;

    /** sortOrder */
    protected Integer sortOrder;

    /** timeToLive */
    protected Integer timeToLive;

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
    public FileConfigDbm asDBMeta() {
        return FileConfigDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "file_config";
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
        if (configParameter != null) {
            addFieldToSource(sourceMap, "configParameter", configParameter);
        }
        if (createdBy != null) {
            addFieldToSource(sourceMap, "createdBy", createdBy);
        }
        if (createdTime != null) {
            addFieldToSource(sourceMap, "createdTime", createdTime);
        }
        if (depth != null) {
            addFieldToSource(sourceMap, "depth", depth);
        }
        if (description != null) {
            addFieldToSource(sourceMap, "description", description);
        }
        if (excludedDocPaths != null) {
            addFieldToSource(sourceMap, "excludedDocPaths", excludedDocPaths);
        }
        if (excludedPaths != null) {
            addFieldToSource(sourceMap, "excludedPaths", excludedPaths);
        }
        if (includedDocPaths != null) {
            addFieldToSource(sourceMap, "includedDocPaths", includedDocPaths);
        }
        if (includedPaths != null) {
            addFieldToSource(sourceMap, "includedPaths", includedPaths);
        }
        if (intervalTime != null) {
            addFieldToSource(sourceMap, "intervalTime", intervalTime);
        }
        if (maxAccessCount != null) {
            addFieldToSource(sourceMap, "maxAccessCount", maxAccessCount);
        }
        if (name != null) {
            addFieldToSource(sourceMap, "name", name);
        }
        if (numOfThread != null) {
            addFieldToSource(sourceMap, "numOfThread", numOfThread);
        }
        if (paths != null) {
            addFieldToSource(sourceMap, "paths", paths);
        }
        if (permissions != null) {
            addFieldToSource(sourceMap, "permissions", permissions);
        }
        if (sortOrder != null) {
            addFieldToSource(sourceMap, "sortOrder", sortOrder);
        }
        if (timeToLive != null) {
            addFieldToSource(sourceMap, "timeToLive", timeToLive);
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
        sb.append(dm).append(configParameter);
        sb.append(dm).append(createdBy);
        sb.append(dm).append(createdTime);
        sb.append(dm).append(depth);
        sb.append(dm).append(description);
        sb.append(dm).append(excludedDocPaths);
        sb.append(dm).append(excludedPaths);
        sb.append(dm).append(includedDocPaths);
        sb.append(dm).append(includedPaths);
        sb.append(dm).append(intervalTime);
        sb.append(dm).append(maxAccessCount);
        sb.append(dm).append(name);
        sb.append(dm).append(numOfThread);
        sb.append(dm).append(paths);
        sb.append(dm).append(permissions);
        sb.append(dm).append(sortOrder);
        sb.append(dm).append(timeToLive);
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

    public String getConfigParameter() {
        checkSpecifiedProperty("configParameter");
        return convertEmptyToNull(configParameter);
    }

    public void setConfigParameter(String value) {
        registerModifiedProperty("configParameter");
        this.configParameter = value;
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

    public Integer getDepth() {
        checkSpecifiedProperty("depth");
        return depth;
    }

    public void setDepth(Integer value) {
        registerModifiedProperty("depth");
        this.depth = value;
    }

    public String getDescription() {
        checkSpecifiedProperty("description");
        return convertEmptyToNull(description);
    }

    public void setDescription(String value) {
        registerModifiedProperty("description");
        this.description = value;
    }

    public String getExcludedDocPaths() {
        checkSpecifiedProperty("excludedDocPaths");
        return convertEmptyToNull(excludedDocPaths);
    }

    public void setExcludedDocPaths(String value) {
        registerModifiedProperty("excludedDocPaths");
        this.excludedDocPaths = value;
    }

    public String getExcludedPaths() {
        checkSpecifiedProperty("excludedPaths");
        return convertEmptyToNull(excludedPaths);
    }

    public void setExcludedPaths(String value) {
        registerModifiedProperty("excludedPaths");
        this.excludedPaths = value;
    }

    public String getIncludedDocPaths() {
        checkSpecifiedProperty("includedDocPaths");
        return convertEmptyToNull(includedDocPaths);
    }

    public void setIncludedDocPaths(String value) {
        registerModifiedProperty("includedDocPaths");
        this.includedDocPaths = value;
    }

    public String getIncludedPaths() {
        checkSpecifiedProperty("includedPaths");
        return convertEmptyToNull(includedPaths);
    }

    public void setIncludedPaths(String value) {
        registerModifiedProperty("includedPaths");
        this.includedPaths = value;
    }

    public Integer getIntervalTime() {
        checkSpecifiedProperty("intervalTime");
        return intervalTime;
    }

    public void setIntervalTime(Integer value) {
        registerModifiedProperty("intervalTime");
        this.intervalTime = value;
    }

    public Long getMaxAccessCount() {
        checkSpecifiedProperty("maxAccessCount");
        return maxAccessCount;
    }

    public void setMaxAccessCount(Long value) {
        registerModifiedProperty("maxAccessCount");
        this.maxAccessCount = value;
    }

    public String getName() {
        checkSpecifiedProperty("name");
        return convertEmptyToNull(name);
    }

    public void setName(String value) {
        registerModifiedProperty("name");
        this.name = value;
    }

    public Integer getNumOfThread() {
        checkSpecifiedProperty("numOfThread");
        return numOfThread;
    }

    public void setNumOfThread(Integer value) {
        registerModifiedProperty("numOfThread");
        this.numOfThread = value;
    }

    public String getPaths() {
        checkSpecifiedProperty("paths");
        return convertEmptyToNull(paths);
    }

    public void setPaths(String value) {
        registerModifiedProperty("paths");
        this.paths = value;
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

    public Integer getTimeToLive() {
        checkSpecifiedProperty("timeToLive");
        return timeToLive;
    }

    public void setTimeToLive(Integer value) {
        registerModifiedProperty("timeToLive");
        this.timeToLive = value;
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
