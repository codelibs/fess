/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.time.*;
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

    /** user */
    protected String user;

    /** roles */
    protected String[] roles;

    /** queryId */
    protected String queryId;

    /** clientIp */
    protected String clientIp;

    /** hitCount */
    protected Long hitCount;

    /** queryOffset */
    protected Integer queryOffset;

    /** queryPageSize */
    protected Integer queryPageSize;

    /** referer */
    protected String referer;

    /** requestedAt */
    protected LocalDateTime requestedAt;

    /** responseTime */
    protected Long responseTime;

    /** queryTime */
    protected Long queryTime;

    /** searchWord */
    protected String searchWord;

    /** userAgent */
    protected String userAgent;

    /** userInfoId */
    protected String userInfoId;

    /** userSessionId */
    protected String userSessionId;

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
            sourceMap.put("accessType", accessType);
        }
        if (user != null) {
            sourceMap.put("user", user);
        }
        if (roles != null) {
            sourceMap.put("roles", roles);
        }
        if (queryId != null) {
            sourceMap.put("queryId", queryId);
        }
        if (clientIp != null) {
            sourceMap.put("clientIp", clientIp);
        }
        if (hitCount != null) {
            sourceMap.put("hitCount", hitCount);
        }
        if (queryOffset != null) {
            sourceMap.put("queryOffset", queryOffset);
        }
        if (queryPageSize != null) {
            sourceMap.put("queryPageSize", queryPageSize);
        }
        if (referer != null) {
            sourceMap.put("referer", referer);
        }
        if (requestedAt != null) {
            sourceMap.put("requestedAt", requestedAt);
        }
        if (responseTime != null) {
            sourceMap.put("responseTime", responseTime);
        }
        if (queryTime != null) {
            sourceMap.put("queryTime", queryTime);
        }
        if (searchWord != null) {
            sourceMap.put("searchWord", searchWord);
        }
        if (userAgent != null) {
            sourceMap.put("userAgent", userAgent);
        }
        if (userInfoId != null) {
            sourceMap.put("userInfoId", userInfoId);
        }
        if (userSessionId != null) {
            sourceMap.put("userSessionId", userSessionId);
        }
        return sourceMap;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(accessType);
        sb.append(dm).append(user);
        sb.append(dm).append(roles);
        sb.append(dm).append(queryId);
        sb.append(dm).append(clientIp);
        sb.append(dm).append(hitCount);
        sb.append(dm).append(queryOffset);
        sb.append(dm).append(queryPageSize);
        sb.append(dm).append(referer);
        sb.append(dm).append(requestedAt);
        sb.append(dm).append(responseTime);
        sb.append(dm).append(queryTime);
        sb.append(dm).append(searchWord);
        sb.append(dm).append(userAgent);
        sb.append(dm).append(userInfoId);
        sb.append(dm).append(userSessionId);
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

    public String getUser() {
        checkSpecifiedProperty("user");
        return convertEmptyToNull(user);
    }

    public void setUser(String value) {
        registerModifiedProperty("user");
        this.user = value;
    }

    public String[] getRoles() {
        checkSpecifiedProperty("roles");
        return roles;
    }

    public void setRoles(String[] value) {
        registerModifiedProperty("roles");
        this.roles = value;
    }

    public String getQueryId() {
        checkSpecifiedProperty("queryId");
        return convertEmptyToNull(queryId);
    }

    public void setQueryId(String value) {
        registerModifiedProperty("queryId");
        this.queryId = value;
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

    public Long getQueryTime() {
        checkSpecifiedProperty("queryTime");
        return queryTime;
    }

    public void setQueryTime(Long value) {
        registerModifiedProperty("queryTime");
        this.queryTime = value;
    }

    public String getSearchWord() {
        checkSpecifiedProperty("searchWord");
        return convertEmptyToNull(searchWord);
    }

    public void setSearchWord(String value) {
        registerModifiedProperty("searchWord");
        this.searchWord = value;
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
}
