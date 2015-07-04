package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.DataConfigDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsDataConfig extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public DataConfigDbm asDBMeta() {
        return DataConfigDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "data_config";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** available */
    protected Boolean available;

    /** boost */
    protected Float boost;

    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** handlerName */
    protected String handlerName;

    /** handlerParameter */
    protected String handlerParameter;

    /** handlerScript */
    protected String handlerScript;

    /** id */
    protected String id;

    /** name */
    protected String name;

    /** sortOrder */
    protected Integer sortOrder;

    /** updatedBy */
    protected String updatedBy;

    /** updatedTime */
    protected Long updatedTime;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean value) {
        available = value;
    }

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

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String value) {
        handlerName = value;
    }

    public String getHandlerParameter() {
        return handlerParameter;
    }

    public void setHandlerParameter(String value) {
        handlerParameter = value;
    }

    public String getHandlerScript() {
        return handlerScript;
    }

    public void setHandlerScript(String value) {
        handlerScript = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
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

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (available != null) {
            sourceMap.put("available", available);
        }
        if (boost != null) {
            sourceMap.put("boost", boost);
        }
        if (createdBy != null) {
            sourceMap.put("createdBy", createdBy);
        }
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (handlerName != null) {
            sourceMap.put("handlerName", handlerName);
        }
        if (handlerParameter != null) {
            sourceMap.put("handlerParameter", handlerParameter);
        }
        if (handlerScript != null) {
            sourceMap.put("handlerScript", handlerScript);
        }
        if (id != null) {
            sourceMap.put("id", id);
        }
        if (name != null) {
            sourceMap.put("name", name);
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
        return sourceMap;
    }
}
