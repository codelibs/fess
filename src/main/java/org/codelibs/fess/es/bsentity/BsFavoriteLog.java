package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.FavoriteLogDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsFavoriteLog extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public FavoriteLogDbm asDBMeta() {
        return FavoriteLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "favorite_log";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** createdTime */
    protected Long createdTime;

    /** id */
    protected String id;

    /** url */
    protected String url;

    /** userInfoId */
    protected String userInfoId;

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

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        url = value;
    }

    public String getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(String value) {
        userInfoId = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (id != null) {
            sourceMap.put("id", id);
        }
        if (url != null) {
            sourceMap.put("url", url);
        }
        if (userInfoId != null) {
            sourceMap.put("userInfoId", userInfoId);
        }
        return sourceMap;
    }
}
