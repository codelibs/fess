package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.SearchLogDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsSearchLog extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public SearchLogDbm asDBMeta() {
        return SearchLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "search_log";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** accessType */
    protected String accessType;

    /** clientIp */
    protected String clientIp;

    /** hitCount */
    protected Long hitCount;

    /** id */
    protected String id;

    /** queryOffset */
    protected Integer queryOffset;

    /** queryPageSize */
    protected Integer queryPageSize;

    /** referer */
    protected String referer;

    /** requestedTime */
    protected Long requestedTime;

    /** responseTime */
    protected Integer responseTime;

    /** searchWord */
    protected String searchWord;

    /** userAgent */
    protected String userAgent;

    /** userId */
    protected Long userId;

    /** userSessionId */
    protected String userSessionId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String value) {
        accessType = value;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String value) {
        clientIp = value;
    }

    public Long getHitCount() {
        return hitCount;
    }

    public void setHitCount(Long value) {
        hitCount = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public Integer getQueryOffset() {
        return queryOffset;
    }

    public void setQueryOffset(Integer value) {
        queryOffset = value;
    }

    public Integer getQueryPageSize() {
        return queryPageSize;
    }

    public void setQueryPageSize(Integer value) {
        queryPageSize = value;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String value) {
        referer = value;
    }

    public Long getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(Long value) {
        requestedTime = value;
    }

    public Integer getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Integer value) {
        responseTime = value;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String value) {
        searchWord = value;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String value) {
        userAgent = value;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long value) {
        userId = value;
    }

    public String getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(String value) {
        userSessionId = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (accessType != null) {
            sourceMap.put("accessType", accessType);
        }
        if (clientIp != null) {
            sourceMap.put("clientIp", clientIp);
        }
        if (hitCount != null) {
            sourceMap.put("hitCount", hitCount);
        }
        if (id != null) {
            sourceMap.put("id", id);
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
        if (requestedTime != null) {
            sourceMap.put("requestedTime", requestedTime);
        }
        if (responseTime != null) {
            sourceMap.put("responseTime", responseTime);
        }
        if (searchWord != null) {
            sourceMap.put("searchWord", searchWord);
        }
        if (userAgent != null) {
            sourceMap.put("userAgent", userAgent);
        }
        if (userId != null) {
            sourceMap.put("userId", userId);
        }
        if (userSessionId != null) {
            sourceMap.put("userSessionId", userSessionId);
        }
        return sourceMap;
    }
}
