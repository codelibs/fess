package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.ScheduledJobDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsScheduledJob extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public ScheduledJobDbm asDBMeta() {
        return ScheduledJobDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "scheduled_job";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** available */
    protected Boolean available;

    /** crawler */
    protected String crawler;

    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** cronExpression */
    protected String cronExpression;

    /** id */
    protected String id;

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
    //                                                                            Accessor
    //                                                                            ========
    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean value) {
        available = value;
    }

    public String getCrawler() {
        return crawler;
    }

    public void setCrawler(String value) {
        crawler = value;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String value) {
        createdBy = value;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long value) {
        createdTime = value;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String value) {
        cronExpression = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public Boolean getJobLogging() {
        return jobLogging;
    }

    public void setJobLogging(Boolean value) {
        jobLogging = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getScriptData() {
        return scriptData;
    }

    public void setScriptData(String value) {
        scriptData = value;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String value) {
        scriptType = value;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer value) {
        sortOrder = value;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String value) {
        target = value;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String value) {
        updatedBy = value;
    }

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long value) {
        updatedTime = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (available != null) {
            sourceMap.put("available", available);
        }
        if (crawler != null) {
            sourceMap.put("crawler", crawler);
        }
        if (createdBy != null) {
            sourceMap.put("createdBy", createdBy);
        }
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (cronExpression != null) {
            sourceMap.put("cronExpression", cronExpression);
        }
        if (id != null) {
            sourceMap.put("id", id);
        }
        if (jobLogging != null) {
            sourceMap.put("jobLogging", jobLogging);
        }
        if (name != null) {
            sourceMap.put("name", name);
        }
        if (scriptData != null) {
            sourceMap.put("scriptData", scriptData);
        }
        if (scriptType != null) {
            sourceMap.put("scriptType", scriptType);
        }
        if (sortOrder != null) {
            sourceMap.put("sortOrder", sortOrder);
        }
        if (target != null) {
            sourceMap.put("target", target);
        }
        if (updatedBy != null) {
            sourceMap.put("updatedBy", updatedBy);
        }
        if (updatedTime != null) {
            sourceMap.put("updatedTime", updatedTime);
        }
        return sourceMap;
    }
}
