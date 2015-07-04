package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.OverlappingHostDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsOverlappingHost extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public OverlappingHostDbm asDBMeta() {
        return OverlappingHostDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "overlapping_host";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** id */
    protected String id;

    /** overlappingName */
    protected String overlappingName;

    /** regularName */
    protected String regularName;

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

    public String getOverlappingName() {
        return overlappingName;
    }

    public void setOverlappingName(String value) {
        overlappingName = value;
    }

    public String getRegularName() {
        return regularName;
    }

    public void setRegularName(String value) {
        regularName = value;
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
        if (createdBy != null) {
            sourceMap.put("createdBy", createdBy);
        }
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (id != null) {
            sourceMap.put("id", id);
        }
        if (overlappingName != null) {
            sourceMap.put("overlappingName", overlappingName);
        }
        if (regularName != null) {
            sourceMap.put("regularName", regularName);
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
