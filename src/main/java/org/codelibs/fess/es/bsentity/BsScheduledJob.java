package org.codelibs.fess.es.bsentity;

import java.time.LocalDateTime;
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
        return createdBy;
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
        return cronExpression;
    }

    public void setCronExpression(String value) {
        registerModifiedProperty("cronExpression");
        this.cronExpression = value;
    }

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
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
        return name;
    }

    public void setName(String value) {
        registerModifiedProperty("name");
        this.name = value;
    }

    public String getScriptData() {
        checkSpecifiedProperty("scriptData");
        return scriptData;
    }

    public void setScriptData(String value) {
        registerModifiedProperty("scriptData");
        this.scriptData = value;
    }

    public String getScriptType() {
        checkSpecifiedProperty("scriptType");
        return scriptType;
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
        return target;
    }

    public void setTarget(String value) {
        registerModifiedProperty("target");
        this.target = value;
    }

    public String getUpdatedBy() {
        checkSpecifiedProperty("updatedBy");
        return updatedBy;
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
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
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
