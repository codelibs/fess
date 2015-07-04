package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.FileConfigToLabelDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsFileConfigToLabel extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public FileConfigToLabelDbm asDBMeta() {
        return FileConfigToLabelDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "file_config_to_label";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** fileConfigId */
    protected String fileConfigId;

    /** id */
    protected String id;

    /** labelTypeId */
    protected String labelTypeId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getFileConfigId() {
        return fileConfigId;
    }

    public void setFileConfigId(String value) {
        fileConfigId = value;
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
        if (fileConfigId != null) {
            sourceMap.put("fileConfigId", fileConfigId);
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
