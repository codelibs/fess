package org.codelibs.fess.es.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.JobLogDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsJobLog extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public JobLogDbm asDBMeta() {
        return JobLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "job_log";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** endTime */
    protected Long endTime;

    /** jobName */
    protected String jobName;

    /** jobStatus */
    protected String jobStatus;

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

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public String getJobName() {
        checkSpecifiedProperty("jobName");
        return jobName;
    }

    public void setJobName(String value) {
        registerModifiedProperty("jobName");
        this.jobName = value;
    }

    public String getJobStatus() {
        checkSpecifiedProperty("jobStatus");
        return jobStatus;
    }

    public void setJobStatus(String value) {
        registerModifiedProperty("jobStatus");
        this.jobStatus = value;
    }

    public String getScriptData() {
        checkSpecifiedProperty("scriptData");
        return scriptData;
    }

    public void setScriptData(String value) {
        registerModifiedProperty("scriptData");
        this.scriptData = value;
    }

    public String getScriptResult() {
        checkSpecifiedProperty("scriptResult");
        return scriptResult;
    }

    public void setScriptResult(String value) {
        registerModifiedProperty("scriptResult");
        this.scriptResult = value;
    }

    public String getScriptType() {
        checkSpecifiedProperty("scriptType");
        return scriptType;
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
        return target;
    }

    public void setTarget(String value) {
        registerModifiedProperty("target");
        this.target = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (endTime != null) {
            sourceMap.put("endTime", endTime);
        }
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (jobName != null) {
            sourceMap.put("jobName", jobName);
        }
        if (jobStatus != null) {
            sourceMap.put("jobStatus", jobStatus);
        }
        if (scriptData != null) {
            sourceMap.put("scriptData", scriptData);
        }
        if (scriptResult != null) {
            sourceMap.put("scriptResult", scriptResult);
        }
        if (scriptType != null) {
            sourceMap.put("scriptType", scriptType);
        }
        if (startTime != null) {
            sourceMap.put("startTime", startTime);
        }
        if (target != null) {
            sourceMap.put("target", target);
        }
        return sourceMap;
    }
}
