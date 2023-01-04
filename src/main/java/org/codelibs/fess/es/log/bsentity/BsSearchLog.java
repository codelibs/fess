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
package org.codelibs.fess.es.log.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.log.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.log.bsentity.dbmeta.SearchLogDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsSearchLog extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** accessType */
    protected String accessType;

    /** clientIp */
    protected String clientIp;

    /** hitCount */
    protected Long hitCount;

    /** hitCountRelation */
    protected String hitCountRelation;

    /** languages */
    protected String languages;

    /** queryId */
    protected String queryId;

    /** queryOffset */
    protected Integer queryOffset;

    /** queryPageSize */
    protected Integer queryPageSize;

    /** queryTime */
    protected Long queryTime;

    /** referer */
    protected String referer;

    /** requestedAt */
    protected LocalDateTime requestedAt;

    /** responseTime */
    protected Long responseTime;

    /** roles */
    protected String[] roles;

    /** searchWord */
    protected String searchWord;

    /** user */
    protected String user;

    /** userAgent */
    protected String userAgent;

    /** userInfoId */
    protected String userInfoId;

    /** userSessionId */
    protected String userSessionId;

    /** virtualHost */
    protected String virtualHost;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public SearchLogDbm asDBMeta() {
        return SearchLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "search_log";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (accessType != null) {
            addFieldToSource(sourceMap, "accessType", accessType);
        }
        if (clientIp != null) {
            addFieldToSource(sourceMap, "clientIp", clientIp);
        }
        if (hitCount != null) {
            addFieldToSource(sourceMap, "hitCount", hitCount);
        }
        if (hitCountRelation != null) {
            addFieldToSource(sourceMap, "hitCountRelation", hitCountRelation);
        }
        if (languages != null) {
            addFieldToSource(sourceMap, "languages", languages);
        }
        if (queryId != null) {
            addFieldToSource(sourceMap, "queryId", queryId);
        }
        if (queryOffset != null) {
            addFieldToSource(sourceMap, "queryOffset", queryOffset);
        }
        if (queryPageSize != null) {
            addFieldToSource(sourceMap, "queryPageSize", queryPageSize);
        }
        if (queryTime != null) {
            addFieldToSource(sourceMap, "queryTime", queryTime);
        }
        if (referer != null) {
            addFieldToSource(sourceMap, "referer", referer);
        }
        if (requestedAt != null) {
            addFieldToSource(sourceMap, "requestedAt", requestedAt);
        }
        if (responseTime != null) {
            addFieldToSource(sourceMap, "responseTime", responseTime);
        }
        if (roles != null) {
            addFieldToSource(sourceMap, "roles", roles);
        }
        if (searchWord != null) {
            addFieldToSource(sourceMap, "searchWord", searchWord);
        }
        if (user != null) {
            addFieldToSource(sourceMap, "user", user);
        }
        if (userAgent != null) {
            addFieldToSource(sourceMap, "userAgent", userAgent);
        }
        if (userInfoId != null) {
            addFieldToSource(sourceMap, "userInfoId", userInfoId);
        }
        if (userSessionId != null) {
            addFieldToSource(sourceMap, "userSessionId", userSessionId);
        }
        if (virtualHost != null) {
            addFieldToSource(sourceMap, "virtualHost", virtualHost);
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
        sb.append(dm).append(accessType);
        sb.append(dm).append(clientIp);
        sb.append(dm).append(hitCount);
        sb.append(dm).append(hitCountRelation);
        sb.append(dm).append(languages);
        sb.append(dm).append(queryId);
        sb.append(dm).append(queryOffset);
        sb.append(dm).append(queryPageSize);
        sb.append(dm).append(queryTime);
        sb.append(dm).append(referer);
        sb.append(dm).append(requestedAt);
        sb.append(dm).append(responseTime);
        sb.append(dm).append(roles);
        sb.append(dm).append(searchWord);
        sb.append(dm).append(user);
        sb.append(dm).append(userAgent);
        sb.append(dm).append(userInfoId);
        sb.append(dm).append(userSessionId);
        sb.append(dm).append(virtualHost);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getAccessType() {
        checkSpecifiedProperty("accessType");
        return convertEmptyToNull(accessType);
    }

    public void setAccessType(String value) {
        registerModifiedProperty("accessType");
        this.accessType = value;
    }

    public String getClientIp() {
        checkSpecifiedProperty("clientIp");
        return convertEmptyToNull(clientIp);
    }

    public void setClientIp(String value) {
        registerModifiedProperty("clientIp");
        this.clientIp = value;
    }

    public Long getHitCount() {
        checkSpecifiedProperty("hitCount");
        return hitCount;
    }

    public void setHitCount(Long value) {
        registerModifiedProperty("hitCount");
        this.hitCount = value;
    }

    public String getHitCountRelation() {
        checkSpecifiedProperty("hitCountRelation");
        return convertEmptyToNull(hitCountRelation);
    }

    public void setHitCountRelation(String value) {
        registerModifiedProperty("hitCountRelation");
        this.hitCountRelation = value;
    }

    public String getLanguages() {
        checkSpecifiedProperty("languages");
        return convertEmptyToNull(languages);
    }

    public void setLanguages(String value) {
        registerModifiedProperty("languages");
        this.languages = value;
    }

    public String getQueryId() {
        checkSpecifiedProperty("queryId");
        return convertEmptyToNull(queryId);
    }

    public void setQueryId(String value) {
        registerModifiedProperty("queryId");
        this.queryId = value;
    }

    public Integer getQueryOffset() {
        checkSpecifiedProperty("queryOffset");
        return queryOffset;
    }

    public void setQueryOffset(Integer value) {
        registerModifiedProperty("queryOffset");
        this.queryOffset = value;
    }

    public Integer getQueryPageSize() {
        checkSpecifiedProperty("queryPageSize");
        return queryPageSize;
    }

    public void setQueryPageSize(Integer value) {
        registerModifiedProperty("queryPageSize");
        this.queryPageSize = value;
    }

    public Long getQueryTime() {
        checkSpecifiedProperty("queryTime");
        return queryTime;
    }

    public void setQueryTime(Long value) {
        registerModifiedProperty("queryTime");
        this.queryTime = value;
    }

    public String getReferer() {
        checkSpecifiedProperty("referer");
        return convertEmptyToNull(referer);
    }

    public void setReferer(String value) {
        registerModifiedProperty("referer");
        this.referer = value;
    }

    public LocalDateTime getRequestedAt() {
        checkSpecifiedProperty("requestedAt");
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime value) {
        registerModifiedProperty("requestedAt");
        this.requestedAt = value;
    }

    public Long getResponseTime() {
        checkSpecifiedProperty("responseTime");
        return responseTime;
    }

    public void setResponseTime(Long value) {
        registerModifiedProperty("responseTime");
        this.responseTime = value;
    }

    public String[] getRoles() {
        checkSpecifiedProperty("roles");
        return roles;
    }

    public void setRoles(String[] value) {
        registerModifiedProperty("roles");
        this.roles = value;
    }

    public String getSearchWord() {
        checkSpecifiedProperty("searchWord");
        return convertEmptyToNull(searchWord);
    }

    public void setSearchWord(String value) {
        registerModifiedProperty("searchWord");
        this.searchWord = value;
    }

    public String getUser() {
        checkSpecifiedProperty("user");
        return convertEmptyToNull(user);
    }

    public void setUser(String value) {
        registerModifiedProperty("user");
        this.user = value;
    }

    public String getUserAgent() {
        checkSpecifiedProperty("userAgent");
        return convertEmptyToNull(userAgent);
    }

    public void setUserAgent(String value) {
        registerModifiedProperty("userAgent");
        this.userAgent = value;
    }

    public String getUserInfoId() {
        checkSpecifiedProperty("userInfoId");
        return convertEmptyToNull(userInfoId);
    }

    public void setUserInfoId(String value) {
        registerModifiedProperty("userInfoId");
        this.userInfoId = value;
    }

    public String getUserSessionId() {
        checkSpecifiedProperty("userSessionId");
        return convertEmptyToNull(userSessionId);
    }

    public void setUserSessionId(String value) {
        registerModifiedProperty("userSessionId");
        this.userSessionId = value;
    }

    public String getVirtualHost() {
        checkSpecifiedProperty("virtualHost");
        return convertEmptyToNull(virtualHost);
    }

    public void setVirtualHost(String value) {
        registerModifiedProperty("virtualHost");
        this.virtualHost = value;
    }
}
