package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.KeyMatchDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsKeyMatch extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public KeyMatchDbm asDBMeta() {
        return KeyMatchDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "key_match";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** boost */
    protected Float boost;

    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** id */
    protected String id;

    /** maxSize */
    protected Integer maxSize;

    /** query */
    protected String query;

    /** term */
    protected String term;

    /** updatedBy */
    protected String updatedBy;

    /** updatedTime */
    protected Long updatedTime;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Float getBoost() {
        return boost;
    }

    public void setBoost(Float value) {
        boost = value;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String value) {
        createdBy = value;
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

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer value) {
        maxSize = value;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String value) {
        query = value;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String value) {
        term = value;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String value) {
        updatedBy = value;
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
        if (boost != null) {
            sourceMap.put("boost", boost);
        }
        if (createdBy != null) {
            sourceMap.put("createdBy", createdBy);
        }
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (id != null) {
            sourceMap.put("id", id);
        }
        if (maxSize != null) {
            sourceMap.put("maxSize", maxSize);
        }
        if (query != null) {
            sourceMap.put("query", query);
        }
        if (term != null) {
            sourceMap.put("term", term);
        }
        if (updatedBy != null) {
            sourceMap.put("updatedBy", updatedBy);
        }
        if (updatedTime != null) {
            sourceMap.put("updatedTime", updatedTime);
        }
        return sourceMap;
    }
}
