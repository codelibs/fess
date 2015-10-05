package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.DataConfigToLabelDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsDataConfigToLabel extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public DataConfigToLabelDbm asDBMeta() {
        return DataConfigToLabelDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "data_config_to_label";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** dataConfigId */
    protected String dataConfigId;

    /** labelTypeId */
    protected String labelTypeId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getDataConfigId() {
        checkSpecifiedProperty("dataConfigId");
        return dataConfigId;
    }

    public void setDataConfigId(String value) {
        registerModifiedProperty("dataConfigId");
        this.dataConfigId = value;
    }

    public String getId() {
        checkSpecifiedProperty("id");
        return asDocMeta().id();
    }

    public void setId(String value) {
        registerModifiedProperty("id");
        asDocMeta().id(value);
    }

    public String getLabelTypeId() {
        checkSpecifiedProperty("labelTypeId");
        return labelTypeId;
    }

    public void setLabelTypeId(String value) {
        registerModifiedProperty("labelTypeId");
        this.labelTypeId = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (dataConfigId != null) {
            sourceMap.put("dataConfigId", dataConfigId);
        }
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (labelTypeId != null) {
            sourceMap.put("labelTypeId", labelTypeId);
        }
        return sourceMap;
    }
}
