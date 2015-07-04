package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.CrawlingSessionDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsCrawlingSession extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public CrawlingSessionDbm asDBMeta() {
        return CrawlingSessionDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "crawling_session";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** createdTime */
    protected Long createdTime;

    /** expiredTime */
    protected Long expiredTime;

    /** id */
    protected String id;

    /** name */
    protected String name;

    /** sessionId */
    protected String sessionId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long value) {
        createdTime = value;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Long value) {
        expiredTime = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String value) {
        sessionId = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (expiredTime != null) {
            sourceMap.put("expiredTime", expiredTime);
        }
        if (id != null) {
            sourceMap.put("id", id);
        }
        if (name != null) {
            sourceMap.put("name", name);
        }
        if (sessionId != null) {
            sourceMap.put("sessionId", sessionId);
        }
        return sourceMap;
    }
}
