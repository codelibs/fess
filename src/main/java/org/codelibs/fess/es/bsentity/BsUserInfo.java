package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.UserInfoDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsUserInfo extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public UserInfoDbm asDBMeta() {
        return UserInfoDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "user_info";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** code */
    protected String code;

    /** createdTime */
    protected Long createdTime;

    /** id */
    protected String id;

    /** updatedTime */
    protected Long updatedTime;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        code = value;
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

    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long value) {
        updatedTime = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (code != null) {
            sourceMap.put("code", code);
        }
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (id != null) {
            sourceMap.put("id", id);
        }
        if (updatedTime != null) {
            sourceMap.put("updatedTime", updatedTime);
        }
        return sourceMap;
    }
}
