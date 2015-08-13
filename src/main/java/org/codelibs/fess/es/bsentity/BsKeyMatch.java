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
        checkSpecifiedProperty("boost");
        return boost;
    }

    public void setBoost(Float value) {
        registerModifiedProperty("boost");
        this.boost = value;
    }

    public String getCreatedBy() {
        checkSpecifiedProperty("createdBy");
        return createdBy;
    }

    public void setCreatedBy(String value) {
        registerModifiedProperty("createdBy");
        this.createdBy = value;
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

    public Integer getMaxSize() {
        checkSpecifiedProperty("maxSize");
        return maxSize;
    }

    public void setMaxSize(Integer value) {
        registerModifiedProperty("maxSize");
        this.maxSize = value;
    }

    public String getQuery() {
        checkSpecifiedProperty("query");
        return query;
    }

    public void setQuery(String value) {
        registerModifiedProperty("query");
        this.query = value;
    }

    public String getTerm() {
        checkSpecifiedProperty("term");
        return term;
    }

    public void setTerm(String value) {
        registerModifiedProperty("term");
        this.term = value;
    }

    public String getUpdatedBy() {
        checkSpecifiedProperty("updatedBy");
        return updatedBy;
    }

    public void setUpdatedBy(String value) {
        registerModifiedProperty("updatedBy");
        this.updatedBy = value;
    }

    public Long getUpdatedTime() {
        checkSpecifiedProperty("updatedTime");
        return updatedTime;
    }

    public void setUpdatedTime(Long value) {
        registerModifiedProperty("updatedTime");
        this.updatedTime = value;
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
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
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
