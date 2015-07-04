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

    /** id */
    protected String id;

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

    public String getExcludedDocUrls() {
        return excludedDocUrls;
    }

    public void setExcludedDocUrls(String value) {
        excludedDocUrls = value;
    }

    public String getExcludedUrls() {
        return excludedUrls;
    }

    public void setExcludedUrls(String value) {
        excludedUrls = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getIncludedDocUrls() {
        return includedDocUrls;
    }

    public void setIncludedDocUrls(String value) {
        includedDocUrls = value;
    }

    public String getIncludedUrls() {
        return includedUrls;
    }

    public void setIncludedUrls(String value) {
        includedUrls = value;
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

    public String getUrls() {
        return urls;
    }

    public void setUrls(String value) {
        urls = value;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String value) {
        userAgent = value;
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
        if (id != null) {
            sourceMap.put("id", id);
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
