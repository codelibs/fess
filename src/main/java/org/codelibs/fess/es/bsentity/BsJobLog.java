package org.codelibs.fess.es.bsentity;

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

    /** id */
    protected String id;

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
        return endTime;
    }

    public void setEndTime(Long value) {
        endTime = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String value) {
        jobName = value;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String value) {
        jobStatus = value;
    }

    public String getScriptData() {
        return scriptData;
    }

    public void setScriptData(String value) {
        scriptData = value;
    }

    public String getScriptResult() {
        return scriptResult;
    }

    public void setScriptResult(String value) {
        scriptResult = value;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String value) {
        scriptType = value;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long value) {
        startTime = value;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String value) {
        target = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (endTime != null) {
            sourceMap.put("endTime", endTime);
        }
        if (id != null) {
            sourceMap.put("id", id);
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
