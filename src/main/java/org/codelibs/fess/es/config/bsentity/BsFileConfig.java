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

import java.time.*;
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

    /** timeToLive */
    protected Integer timeToLive;

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

    /** updatedBy */
    protected String updatedBy;

    /** updatedTime */
    protected Long updatedTime;

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
            sourceMap.put("available", available);
        }
        if (boost != null) {
            sourceMap.put("boost", boost);
        }
        if (configParameter != null) {
            sourceMap.put("configParameter", configParameter);
        }
        if (createdBy != null) {
            sourceMap.put("createdBy", createdBy);
        }
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (depth != null) {
            sourceMap.put("depth", depth);
        }
        if (excludedDocPaths != null) {
            sourceMap.put("excludedDocPaths", excludedDocPaths);
        }
        if (excludedPaths != null) {
            sourceMap.put("excludedPaths", excludedPaths);
        }
        if (includedDocPaths != null) {
            sourceMap.put("includedDocPaths", includedDocPaths);
        }
        if (includedPaths != null) {
            sourceMap.put("includedPaths", includedPaths);
        }
        if (intervalTime != null) {
            sourceMap.put("intervalTime", intervalTime);
        }
        if (timeToLive != null) {
            sourceMap.put("timeToLive", timeToLive);
        }
        if (maxAccessCount != null) {
            sourceMap.put("maxAccessCount", maxAccessCount);
        }
        if (name != null) {
            sourceMap.put("name", name);
        }
        if (numOfThread != null) {
            sourceMap.put("numOfThread", numOfThread);
        }
        if (paths != null) {
            sourceMap.put("paths", paths);
        }
        if (permissions != null) {
            sourceMap.put("permissions", permissions);
        }
        if (sortOrder != null) {
            sourceMap.put("sortOrder", sortOrder);
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
        sb.append(dm).append(available);
        sb.append(dm).append(boost);
        sb.append(dm).append(configParameter);
        sb.append(dm).append(createdBy);
        sb.append(dm).append(createdTime);
        sb.append(dm).append(depth);
        sb.append(dm).append(excludedDocPaths);
        sb.append(dm).append(excludedPaths);
        sb.append(dm).append(includedDocPaths);
        sb.append(dm).append(includedPaths);
        sb.append(dm).append(intervalTime);
        sb.append(dm).append(timeToLive);
        sb.append(dm).append(maxAccessCount);
        sb.append(dm).append(name);
        sb.append(dm).append(numOfThread);
        sb.append(dm).append(paths);
        sb.append(dm).append(permissions);
        sb.append(dm).append(sortOrder);
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

    public Integer getTimeToLive() {
        checkSpecifiedProperty("timeToLive");
        return timeToLive;
    }

    public void setTimeToLive(Integer value) {
        registerModifiedProperty("timeToLive");
        this.timeToLive = value;
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
