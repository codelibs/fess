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
import org.codelibs.fess.es.config.bsentity.dbmeta.JobLogDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsJobLog extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** endTime */
    protected Long endTime;

    /** jobName */
    protected String jobName;

    /** jobStatus */
    protected String jobStatus;

    /** lastUpdated */
    protected Long lastUpdated;

    /** scriptData */
    protected String scriptData;

    /** scriptResult */
    protected String scriptResult;

    /** scriptType */
    protected String scriptType;

    /** startTime */
    protected Long startTime;

    /** target */
    protected String target;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public JobLogDbm asDBMeta() {
        return JobLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "job_log";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (endTime != null) {
            addFieldToSource(sourceMap, "endTime", endTime);
        }
        if (jobName != null) {
            addFieldToSource(sourceMap, "jobName", jobName);
        }
        if (jobStatus != null) {
            addFieldToSource(sourceMap, "jobStatus", jobStatus);
        }
        if (lastUpdated != null) {
            addFieldToSource(sourceMap, "lastUpdated", lastUpdated);
        }
        if (scriptData != null) {
            addFieldToSource(sourceMap, "scriptData", scriptData);
        }
        if (scriptResult != null) {
            addFieldToSource(sourceMap, "scriptResult", scriptResult);
        }
        if (scriptType != null) {
            addFieldToSource(sourceMap, "scriptType", scriptType);
        }
        if (startTime != null) {
            addFieldToSource(sourceMap, "startTime", startTime);
        }
        if (target != null) {
            addFieldToSource(sourceMap, "target", target);
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
        sb.append(dm).append(endTime);
        sb.append(dm).append(jobName);
        sb.append(dm).append(jobStatus);
        sb.append(dm).append(lastUpdated);
        sb.append(dm).append(scriptData);
        sb.append(dm).append(scriptResult);
        sb.append(dm).append(scriptType);
        sb.append(dm).append(startTime);
        sb.append(dm).append(target);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Long getEndTime() {
        checkSpecifiedProperty("endTime");
        return endTime;
    }

    public void setEndTime(Long value) {
        registerModifiedProperty("endTime");
        this.endTime = value;
    }

    public String getJobName() {
        checkSpecifiedProperty("jobName");
        return convertEmptyToNull(jobName);
    }

    public void setJobName(String value) {
        registerModifiedProperty("jobName");
        this.jobName = value;
    }

    public String getJobStatus() {
        checkSpecifiedProperty("jobStatus");
        return convertEmptyToNull(jobStatus);
    }

    public void setJobStatus(String value) {
        registerModifiedProperty("jobStatus");
        this.jobStatus = value;
    }

    public Long getLastUpdated() {
        checkSpecifiedProperty("lastUpdated");
        return lastUpdated;
    }

    public void setLastUpdated(Long value) {
        registerModifiedProperty("lastUpdated");
        this.lastUpdated = value;
    }

    public String getScriptData() {
        checkSpecifiedProperty("scriptData");
        return convertEmptyToNull(scriptData);
    }

    public void setScriptData(String value) {
        registerModifiedProperty("scriptData");
        this.scriptData = value;
    }

    public String getScriptResult() {
        checkSpecifiedProperty("scriptResult");
        return convertEmptyToNull(scriptResult);
    }

    public void setScriptResult(String value) {
        registerModifiedProperty("scriptResult");
        this.scriptResult = value;
    }

    public String getScriptType() {
        checkSpecifiedProperty("scriptType");
        return convertEmptyToNull(scriptType);
    }

    public void setScriptType(String value) {
        registerModifiedProperty("scriptType");
        this.scriptType = value;
    }

    public Long getStartTime() {
        checkSpecifiedProperty("startTime");
        return startTime;
    }

    public void setStartTime(Long value) {
        registerModifiedProperty("startTime");
        this.startTime = value;
    }

    public String getTarget() {
        checkSpecifiedProperty("target");
        return convertEmptyToNull(target);
    }

    public void setTarget(String value) {
        registerModifiedProperty("target");
        this.target = value;
    }
}
