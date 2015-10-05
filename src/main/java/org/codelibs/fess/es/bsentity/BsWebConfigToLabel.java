package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.WebConfigToLabelDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsWebConfigToLabel extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public WebConfigToLabelDbm asDBMeta() {
        return WebConfigToLabelDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "web_config_to_label";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** labelTypeId */
    protected String labelTypeId;

    /** webConfigId */
    protected String webConfigId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
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

    public String getWebConfigId() {
        checkSpecifiedProperty("webConfigId");
        return webConfigId;
    }

    public void setWebConfigId(String value) {
        registerModifiedProperty("webConfigId");
        this.webConfigId = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (labelTypeId != null) {
            sourceMap.put("labelTypeId", labelTypeId);
        }
        if (webConfigId != null) {
            sourceMap.put("webConfigId", webConfigId);
        }
        return sourceMap;
    }
}
