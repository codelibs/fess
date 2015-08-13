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

    /** name */
    protected String name;

    /** sessionId */
    protected String sessionId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Long getCreatedTime() {
        checkSpecifiedProperty("createdTime");
        return createdTime;
    }

    public void setCreatedTime(Long value) {
        registerModifiedProperty("createdTime");
        this.createdTime = value;
    }

    public Long getExpiredTime() {
        checkSpecifiedProperty("expiredTime");
        return expiredTime;
    }

    public void setExpiredTime(Long value) {
        registerModifiedProperty("expiredTime");
        this.expiredTime = value;
    }

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public String getName() {
        checkSpecifiedProperty("name");
        return name;
    }

    public void setName(String value) {
        registerModifiedProperty("name");
        this.name = value;
    }

    public String getSessionId() {
        checkSpecifiedProperty("sessionId");
        return sessionId;
    }

    public void setSessionId(String value) {
        registerModifiedProperty("sessionId");
        this.sessionId = value;
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
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
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
