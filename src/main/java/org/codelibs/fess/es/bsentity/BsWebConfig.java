package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.WebConfigDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsWebConfig extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public WebConfigDbm asDBMeta() {
        return WebConfigDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "web_config";
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

    /** excludedDocUrls */
    protected String excludedDocUrls;

    /** excludedUrls */
    protected String excludedUrls;

    /** includedDocUrls */
    protected String includedDocUrls;

    /** includedUrls */
    protected String includedUrls;

    /** intervalTime */
    protected Integer intervalTime;

    /** maxAccessCount */
    protected Long maxAccessCount;

    /** name */
    protected String name;

    /** numOfThread */
    protected Integer numOfThread;

    /** sortOrder */
    protected Integer sortOrder;

    /** updatedBy */
    protected String updatedBy;

    /** updatedTime */
    protected Long updatedTime;

    /** urls */
    protected String urls;

    /** userAgent */
    protected String userAgent;

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
        return configParameter;
    }

    public void setConfigParameter(String value) {
        registerModifiedProperty("configParameter");
        this.configParameter = value;
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

    public Integer getDepth() {
        checkSpecifiedProperty("depth");
        return depth;
    }

    public void setDepth(Integer value) {
        registerModifiedProperty("depth");
        this.depth = value;
    }

    public String getExcludedDocUrls() {
        checkSpecifiedProperty("excludedDocUrls");
        return excludedDocUrls;
    }

    public void setExcludedDocUrls(String value) {
        registerModifiedProperty("excludedDocUrls");
        this.excludedDocUrls = value;
    }

    public String getExcludedUrls() {
        checkSpecifiedProperty("excludedUrls");
        return excludedUrls;
    }

    public void setExcludedUrls(String value) {
        registerModifiedProperty("excludedUrls");
        this.excludedUrls = value;
    }

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public String getIncludedDocUrls() {
        checkSpecifiedProperty("includedDocUrls");
        return includedDocUrls;
    }

    public void setIncludedDocUrls(String value) {
        registerModifiedProperty("includedDocUrls");
        this.includedDocUrls = value;
    }

    public String getIncludedUrls() {
        checkSpecifiedProperty("includedUrls");
        return includedUrls;
    }

    public void setIncludedUrls(String value) {
        registerModifiedProperty("includedUrls");
        this.includedUrls = value;
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
        return name;
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

    public String getUrls() {
        checkSpecifiedProperty("urls");
        return urls;
    }

    public void setUrls(String value) {
        registerModifiedProperty("urls");
        this.urls = value;
    }

    public String getUserAgent() {
        checkSpecifiedProperty("userAgent");
        return userAgent;
    }

    public void setUserAgent(String value) {
        registerModifiedProperty("userAgent");
        this.userAgent = value;
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
        if (excludedDocUrls != null) {
            sourceMap.put("excludedDocUrls", excludedDocUrls);
        }
        if (excludedUrls != null) {
            sourceMap.put("excludedUrls", excludedUrls);
        }
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (includedDocUrls != null) {
            sourceMap.put("includedDocUrls", includedDocUrls);
        }
        if (includedUrls != null) {
            sourceMap.put("includedUrls", includedUrls);
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
        if (sortOrder != null) {
            sourceMap.put("sortOrder", sortOrder);
        }
        if (updatedBy != null) {
            sourceMap.put("updatedBy", updatedBy);
        }
        if (updatedTime != null) {
            sourceMap.put("updatedTime", updatedTime);
        }
        if (urls != null) {
            sourceMap.put("urls", urls);
        }
        if (userAgent != null) {
            sourceMap.put("userAgent", userAgent);
        }
        return sourceMap;
    }
}
