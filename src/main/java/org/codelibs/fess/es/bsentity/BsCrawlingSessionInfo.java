package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.CrawlingSessionInfoDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsCrawlingSessionInfo extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public CrawlingSessionInfoDbm asDBMeta() {
        return CrawlingSessionInfoDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "crawling_session_info";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** crawlingSessionId */
    protected String crawlingSessionId;

    /** createdTime */
    protected Long createdTime;

    /** id */
    protected String id;

    /** key */
    protected String key;

    /** value */
    protected String value;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getCrawlingSessionId() {
        return crawlingSessionId;
    }

    public void setCrawlingSessionId(String value) {
        crawlingSessionId = value;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long value) {
        createdTime = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String value) {
        key = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        value = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (crawlingSessionId != null) {
            sourceMap.put("crawlingSessionId", crawlingSessionId);
        }
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (id != null) {
            sourceMap.put("id", id);
        }
        if (key != null) {
            sourceMap.put("key", key);
        }
        if (value != null) {
            sourceMap.put("value", value);
        }
        return sourceMap;
    }
}
