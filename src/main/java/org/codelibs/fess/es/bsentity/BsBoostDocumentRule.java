package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.BoostDocumentRuleDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsBoostDocumentRule extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public BoostDocumentRuleDbm asDBMeta() {
        return BoostDocumentRuleDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "boost_document_rule";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** boostExpr */
    protected String boostExpr;

    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** id */
    protected String id;

    /** sortOrder */
    protected Integer sortOrder;

    /** updatedBy */
    protected String updatedBy;

    /** updatedTime */
    protected Long updatedTime;

    /** urlExpr */
    protected String urlExpr;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getBoostExpr() {
        return boostExpr;
    }

    public void setBoostExpr(String value) {
        boostExpr = value;
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

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer value) {
        sortOrder = value;
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

    public String getUrlExpr() {
        return urlExpr;
    }

    public void setUrlExpr(String value) {
        urlExpr = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (boostExpr != null) {
            sourceMap.put("boostExpr", boostExpr);
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
        if (sortOrder != null) {
            sourceMap.put("sortOrder", sortOrder);
        }
        if (updatedBy != null) {
            sourceMap.put("updatedBy", updatedBy);
        }
        if (updatedTime != null) {
            sourceMap.put("updatedTime", updatedTime);
        }
        if (urlExpr != null) {
            sourceMap.put("urlExpr", urlExpr);
        }
        return sourceMap;
    }
}
