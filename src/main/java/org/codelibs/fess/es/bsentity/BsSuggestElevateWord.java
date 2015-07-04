package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.SuggestElevateWordDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsSuggestElevateWord extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public SuggestElevateWordDbm asDBMeta() {
        return SuggestElevateWordDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "suggest_elevate_word";
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

    /** reading */
    protected String reading;

    /** suggestWord */
    protected String suggestWord;

    /** targetLabel */
    protected String targetLabel;

    /** targetRole */
    protected String targetRole;

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

    public String getReading() {
        return reading;
    }

    public void setReading(String value) {
        reading = value;
    }

    public String getSuggestWord() {
        return suggestWord;
    }

    public void setSuggestWord(String value) {
        suggestWord = value;
    }

    public String getTargetLabel() {
        return targetLabel;
    }

    public void setTargetLabel(String value) {
        targetLabel = value;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String value) {
        targetRole = value;
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
        if (reading != null) {
            sourceMap.put("reading", reading);
        }
        if (suggestWord != null) {
            sourceMap.put("suggestWord", suggestWord);
        }
        if (targetLabel != null) {
            sourceMap.put("targetLabel", targetLabel);
        }
        if (targetRole != null) {
            sourceMap.put("targetRole", targetRole);
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
