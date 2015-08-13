package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.PathMappingDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsPathMapping extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public PathMappingDbm asDBMeta() {
        return PathMappingDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "path_mapping";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** createdBy */
    protected String createdBy;

    /** createdTime */
    protected Long createdTime;

    /** processType */
    protected String processType;

    /** regex */
    protected String regex;

    /** replacement */
    protected String replacement;

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

    public String getProcessType() {
        checkSpecifiedProperty("processType");
        return processType;
    }

    public void setProcessType(String value) {
        registerModifiedProperty("processType");
        this.processType = value;
    }

    public String getRegex() {
        checkSpecifiedProperty("regex");
        return regex;
    }

    public void setRegex(String value) {
        registerModifiedProperty("regex");
        this.regex = value;
    }

    public String getReplacement() {
        checkSpecifiedProperty("replacement");
        return replacement;
    }

    public void setReplacement(String value) {
        registerModifiedProperty("replacement");
        this.replacement = value;
    }

    public Integer getSortOrder() {
        checkSpecifiedProperty("sortOrder");
        return sortOrder;
    }

    public void setSortOrder(Integer value) {
        registerModifiedProperty("sortOrder");
        this.sortOrder = value;
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
        if (createdBy != null) {
            sourceMap.put("createdBy", createdBy);
        }
        if (createdTime != null) {
            sourceMap.put("createdTime", createdTime);
        }
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (processType != null) {
            sourceMap.put("processType", processType);
        }
        if (regex != null) {
            sourceMap.put("regex", regex);
        }
        if (replacement != null) {
            sourceMap.put("replacement", replacement);
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
