package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.ClickLogDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsClickLog extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public ClickLogDbm asDBMeta() {
        return ClickLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "click_log";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** requestedTime */
    protected Long requestedTime;

    /** searchLogId */
    protected String searchLogId;

    /** url */
    protected String url;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public Long getRequestedTime() {
        checkSpecifiedProperty("requestedTime");
        return requestedTime;
    }

    public void setRequestedTime(Long value) {
        registerModifiedProperty("requestedTime");
        this.requestedTime = value;
    }

    public String getSearchLogId() {
        checkSpecifiedProperty("searchLogId");
        return searchLogId;
    }

    public void setSearchLogId(String value) {
        registerModifiedProperty("searchLogId");
        this.searchLogId = value;
    }

    public String getUrl() {
        checkSpecifiedProperty("url");
        return url;
    }

    public void setUrl(String value) {
        registerModifiedProperty("url");
        this.url = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (requestedTime != null) {
            sourceMap.put("requestedTime", requestedTime);
        }
        if (searchLogId != null) {
            sourceMap.put("searchLogId", searchLogId);
        }
        if (url != null) {
            sourceMap.put("url", url);
        }
        return sourceMap;
    }
}
