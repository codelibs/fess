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
    /** id */
    protected String id;

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
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public Long getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(Long value) {
        requestedTime = value;
    }

    public String getSearchLogId() {
        return searchLogId;
    }

    public void setSearchLogId(String value) {
        searchLogId = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        url = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (id != null) {
            sourceMap.put("id", id);
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
