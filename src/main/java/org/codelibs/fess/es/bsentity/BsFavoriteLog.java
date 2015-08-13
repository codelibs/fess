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

    /** url */
    protected String url;

    /** userInfoId */
    protected String userInfoId;

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

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public String getUrl() {
        checkSpecifiedProperty("url");
        return url;
    }

    public void setUrl(String value) {
        registerModifiedProperty("url");
        this.url = value;
    }

    public String getUserInfoId() {
        checkSpecifiedProperty("userInfoId");
        return userInfoId;
    }

    public void setUserInfoId(String value) {
        registerModifiedProperty("userInfoId");
        this.userInfoId = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
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
