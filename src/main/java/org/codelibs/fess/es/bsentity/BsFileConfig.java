package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.FileConfigDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsFileConfig extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public FileConfigDbm asDBMeta() {
        return FileConfigDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "file_config";
    }

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

    /** id */
    protected String id;

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

    /** sortOrder */
    protected Integer sortOrder;

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

    public Float getBoost() {
        return boost;
    }

    public void setBoost(Float value) {
        boost = value;
    }

    public String getConfigParameter() {
        return configParameter;
    }

    public void setConfigParameter(String value) {
        configParameter = value;
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

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer value) {
        depth = value;
    }

    public String getExcludedDocPaths() {
        return excludedDocPaths;
    }

    public void setExcludedDocPaths(String value) {
        excludedDocPaths = value;
    }

    public String getExcludedPaths() {
        return excludedPaths;
    }

    public void setExcludedPaths(String value) {
        excludedPaths = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getIncludedDocPaths() {
        return includedDocPaths;
    }

    public void setIncludedDocPaths(String value) {
        includedDocPaths = value;
    }

    public String getIncludedPaths() {
        return includedPaths;
    }

    public void setIncludedPaths(String value) {
        includedPaths = value;
    }

    public Integer getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Integer value) {
        intervalTime = value;
    }

    public Long getMaxAccessCount() {
        return maxAccessCount;
    }

    public void setMaxAccessCount(Long value) {
        maxAccessCount = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public Integer getNumOfThread() {
        return numOfThread;
    }

    public void setNumOfThread(Integer value) {
        numOfThread = value;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String value) {
        paths = value;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer value) {
        sortOrder = value;
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
        if (id != null) {
            sourceMap.put("id", id);
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
}
