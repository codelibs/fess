package org.codelibs.fess.es.bsentity;

import java.time.LocalDateTime;
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

    /** labelTypeId */
    protected String labelTypeId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getFileConfigId() {
        checkSpecifiedProperty("fileConfigId");
        return fileConfigId;
    }

    public void setFileConfigId(String value) {
        registerModifiedProperty("fileConfigId");
        this.fileConfigId = value;
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
        if (fileConfigId != null) {
            sourceMap.put("fileConfigId", fileConfigId);
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
