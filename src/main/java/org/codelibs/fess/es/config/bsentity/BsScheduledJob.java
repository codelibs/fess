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
import org.codelibs.fess.es.config.bsentity.dbmeta.ScheduledJobDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsScheduledJob extends EsAbstractEntity {

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

    /** crawler */
    protected Boolean crawler;

    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** cronExpression */
    protected String cronExpression;

    /** jobLogging */
    protected Boolean jobLogging;

    /** name */
    protected String name;

    /** scriptData */
    protected String scriptData;

    /** scriptType */
    protected String scriptType;

    /** sortOrder */
    protected Integer sortOrder;

    /** target */
    protected String target;

    /** updatedBy */
    protected String updatedBy;

    /** updatedTime */
    protected Long updatedTime;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public ScheduledJobDbm asDBMeta() {
        return ScheduledJobDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "scheduled_job";
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
        if (crawler != null) {
            addFieldToSource(sourceMap, "crawler", crawler);
        }
        if (createdBy != null) {
            addFieldToSource(sourceMap, "createdBy", createdBy);
        }
        if (createdTime != null) {
            addFieldToSource(sourceMap, "createdTime", createdTime);
        }
        if (cronExpression != null) {
            addFieldToSource(sourceMap, "cronExpression", cronExpression);
        }
        if (jobLogging != null) {
            addFieldToSource(sourceMap, "jobLogging", jobLogging);
        }
        if (name != null) {
            addFieldToSource(sourceMap, "name", name);
        }
        if (scriptData != null) {
            addFieldToSource(sourceMap, "scriptData", scriptData);
        }
        if (scriptType != null) {
            addFieldToSource(sourceMap, "scriptType", scriptType);
        }
        if (sortOrder != null) {
            addFieldToSource(sourceMap, "sortOrder", sortOrder);
        }
        if (target != null) {
            addFieldToSource(sourceMap, "target", target);
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
        sb.append(dm).append(available);
        sb.append(dm).append(crawler);
        sb.append(dm).append(createdBy);
        sb.append(dm).append(createdTime);
        sb.append(dm).append(cronExpression);
        sb.append(dm).append(jobLogging);
        sb.append(dm).append(name);
        sb.append(dm).append(scriptData);
        sb.append(dm).append(scriptType);
        sb.append(dm).append(sortOrder);
        sb.append(dm).append(target);
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

    public Boolean getCrawler() {
        checkSpecifiedProperty("crawler");
        return crawler;
    }

    public void setCrawler(Boolean value) {
        registerModifiedProperty("crawler");
        this.crawler = value;
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

    public String getCronExpression() {
        checkSpecifiedProperty("cronExpression");
        return convertEmptyToNull(cronExpression);
    }

    public void setCronExpression(String value) {
        registerModifiedProperty("cronExpression");
        this.cronExpression = value;
    }

    public Boolean getJobLogging() {
        checkSpecifiedProperty("jobLogging");
        return jobLogging;
    }

    public void setJobLogging(Boolean value) {
        registerModifiedProperty("jobLogging");
        this.jobLogging = value;
    }

    public String getName() {
        checkSpecifiedProperty("name");
        return convertEmptyToNull(name);
    }

    public void setName(String value) {
        registerModifiedProperty("name");
        this.name = value;
    }

    public String getScriptData() {
        checkSpecifiedProperty("scriptData");
        return convertEmptyToNull(scriptData);
    }

    public void setScriptData(String value) {
        registerModifiedProperty("scriptData");
        this.scriptData = value;
    }

    public String getScriptType() {
        checkSpecifiedProperty("scriptType");
        return convertEmptyToNull(scriptType);
    }

    public void setScriptType(String value) {
        registerModifiedProperty("scriptType");
        this.scriptType = value;
    }

    public Integer getSortOrder() {
        checkSpecifiedProperty("sortOrder");
        return sortOrder;
    }

    public void setSortOrder(Integer value) {
        registerModifiedProperty("sortOrder");
        this.sortOrder = value;
    }

    public String getTarget() {
        checkSpecifiedProperty("target");
        return convertEmptyToNull(target);
    }

    public void setTarget(String value) {
        registerModifiedProperty("target");
        this.target = value;
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
