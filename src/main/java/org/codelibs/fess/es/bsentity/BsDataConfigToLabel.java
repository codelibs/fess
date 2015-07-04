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

    /** id */
    protected String id;

    /** labelTypeId */
    protected String labelTypeId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getDataConfigId() {
        return dataConfigId;
    }

    public void setDataConfigId(String value) {
        dataConfigId = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getLabelTypeId() {
        return labelTypeId;
    }

    public void setLabelTypeId(String value) {
        labelTypeId = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (dataConfigId != null) {
            sourceMap.put("dataConfigId", dataConfigId);
        }
        if (id != null) {
            sourceMap.put("id", id);
        }
        if (labelTypeId != null) {
            sourceMap.put("labelTypeId", labelTypeId);
        }
        return sourceMap;
    }
}
