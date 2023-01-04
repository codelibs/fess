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
import org.codelibs.fess.es.config.bsentity.dbmeta.WebConfigDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsWebConfig extends EsAbstractEntity {

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

    /** description */
    protected String description;

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

    /** permissions */
    protected String[] permissions;

    /** sortOrder */
    protected Integer sortOrder;

    /** timeToLive */
    protected Integer timeToLive;

    /** updatedBy */
    protected String updatedBy;

    /** updatedTime */
    protected Long updatedTime;

    /** urls */
    protected String urls;

    /** userAgent */
    protected String userAgent;

    /** virtualHosts */
    protected String[] virtualHosts;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public WebConfigDbm asDBMeta() {
        return WebConfigDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "web_config";
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
        if (boost != null) {
            addFieldToSource(sourceMap, "boost", boost);
        }
        if (configParameter != null) {
            addFieldToSource(sourceMap, "configParameter", configParameter);
        }
        if (createdBy != null) {
            addFieldToSource(sourceMap, "createdBy", createdBy);
        }
        if (createdTime != null) {
            addFieldToSource(sourceMap, "createdTime", createdTime);
        }
        if (depth != null) {
            addFieldToSource(sourceMap, "depth", depth);
        }
        if (description != null) {
            addFieldToSource(sourceMap, "description", description);
        }
        if (excludedDocUrls != null) {
            addFieldToSource(sourceMap, "excludedDocUrls", excludedDocUrls);
        }
        if (excludedUrls != null) {
            addFieldToSource(sourceMap, "excludedUrls", excludedUrls);
        }
        if (includedDocUrls != null) {
            addFieldToSource(sourceMap, "includedDocUrls", includedDocUrls);
        }
        if (includedUrls != null) {
            addFieldToSource(sourceMap, "includedUrls", includedUrls);
        }
        if (intervalTime != null) {
            addFieldToSource(sourceMap, "intervalTime", intervalTime);
        }
        if (maxAccessCount != null) {
            addFieldToSource(sourceMap, "maxAccessCount", maxAccessCount);
        }
        if (name != null) {
            addFieldToSource(sourceMap, "name", name);
        }
        if (numOfThread != null) {
            addFieldToSource(sourceMap, "numOfThread", numOfThread);
        }
        if (permissions != null) {
            addFieldToSource(sourceMap, "permissions", permissions);
        }
        if (sortOrder != null) {
            addFieldToSource(sourceMap, "sortOrder", sortOrder);
        }
        if (timeToLive != null) {
            addFieldToSource(sourceMap, "timeToLive", timeToLive);
        }
        if (updatedBy != null) {
            addFieldToSource(sourceMap, "updatedBy", updatedBy);
        }
        if (updatedTime != null) {
            addFieldToSource(sourceMap, "updatedTime", updatedTime);
        }
        if (urls != null) {
            addFieldToSource(sourceMap, "urls", urls);
        }
        if (userAgent != null) {
            addFieldToSource(sourceMap, "userAgent", userAgent);
        }
        if (virtualHosts != null) {
            addFieldToSource(sourceMap, "virtualHosts", virtualHosts);
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
        sb.append(dm).append(boost);
        sb.append(dm).append(configParameter);
        sb.append(dm).append(createdBy);
        sb.append(dm).append(createdTime);
        sb.append(dm).append(depth);
        sb.append(dm).append(description);
        sb.append(dm).append(excludedDocUrls);
        sb.append(dm).append(excludedUrls);
        sb.append(dm).append(includedDocUrls);
        sb.append(dm).append(includedUrls);
        sb.append(dm).append(intervalTime);
        sb.append(dm).append(maxAccessCount);
        sb.append(dm).append(name);
        sb.append(dm).append(numOfThread);
        sb.append(dm).append(permissions);
        sb.append(dm).append(sortOrder);
        sb.append(dm).append(timeToLive);
        sb.append(dm).append(updatedBy);
        sb.append(dm).append(updatedTime);
        sb.append(dm).append(urls);
        sb.append(dm).append(userAgent);
        sb.append(dm).append(virtualHosts);
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

    public String getDescription() {
        checkSpecifiedProperty("description");
        return convertEmptyToNull(description);
    }

    public void setDescription(String value) {
        registerModifiedProperty("description");
        this.description = value;
    }

    public String getExcludedDocUrls() {
        checkSpecifiedProperty("excludedDocUrls");
        return convertEmptyToNull(excludedDocUrls);
    }

    public void setExcludedDocUrls(String value) {
        registerModifiedProperty("excludedDocUrls");
        this.excludedDocUrls = value;
    }

    public String getExcludedUrls() {
        checkSpecifiedProperty("excludedUrls");
        return convertEmptyToNull(excludedUrls);
    }

    public void setExcludedUrls(String value) {
        registerModifiedProperty("excludedUrls");
        this.excludedUrls = value;
    }

    public String getIncludedDocUrls() {
        checkSpecifiedProperty("includedDocUrls");
        return convertEmptyToNull(includedDocUrls);
    }

    public void setIncludedDocUrls(String value) {
        registerModifiedProperty("includedDocUrls");
        this.includedDocUrls = value;
    }

    public String getIncludedUrls() {
        checkSpecifiedProperty("includedUrls");
        return convertEmptyToNull(includedUrls);
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

    public Integer getTimeToLive() {
        checkSpecifiedProperty("timeToLive");
        return timeToLive;
    }

    public void setTimeToLive(Integer value) {
        registerModifiedProperty("timeToLive");
        this.timeToLive = value;
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

    public String getUrls() {
        checkSpecifiedProperty("urls");
        return convertEmptyToNull(urls);
    }

    public void setUrls(String value) {
        registerModifiedProperty("urls");
        this.urls = value;
    }

    public String getUserAgent() {
        checkSpecifiedProperty("userAgent");
        return convertEmptyToNull(userAgent);
    }

    public void setUserAgent(String value) {
        registerModifiedProperty("userAgent");
        this.userAgent = value;
    }

    public String[] getVirtualHosts() {
        checkSpecifiedProperty("virtualHosts");
        return virtualHosts;
    }

    public void setVirtualHosts(String[] value) {
        registerModifiedProperty("virtualHosts");
        this.virtualHosts = value;
    }
}
