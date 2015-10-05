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

    /** key */
    protected String key;

    /** value */
    protected String value;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getCrawlingSessionId() {
        checkSpecifiedProperty("crawlingSessionId");
        return crawlingSessionId;
    }

    public void setCrawlingSessionId(String value) {
        registerModifiedProperty("crawlingSessionId");
        this.crawlingSessionId = value;
    }

    public Long getCreatedTime() {
        checkSpecifiedProperty("createdTime");
        return createdTime;
    }

    public void setCreatedTime(Long value) {
        registerModifiedProperty("createdTime");
        this.createdTime = value;
    }

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public String getKey() {
        checkSpecifiedProperty("key");
        return key;
    }

    public void setKey(String value) {
        registerModifiedProperty("key");
        this.key = value;
    }

    public String getValue() {
        checkSpecifiedProperty("value");
        return value;
    }

    public void setValue(String value) {
        registerModifiedProperty("value");
        this.value = value;
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
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
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
