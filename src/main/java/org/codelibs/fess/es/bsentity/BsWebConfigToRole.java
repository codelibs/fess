package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.WebConfigToRoleDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsWebConfigToRole extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public WebConfigToRoleDbm asDBMeta() {
        return WebConfigToRoleDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "web_config_to_role";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** id */
    protected String id;

    /** roleTypeId */
    protected String roleTypeId;

    /** webConfigId */
    protected String webConfigId;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getId() {
        return id;
    }

    public void setId(String value) {
        id = value;
    }

    public String getRoleTypeId() {
        return roleTypeId;
    }

    public void setRoleTypeId(String value) {
        roleTypeId = value;
    }

    public String getWebConfigId() {
        return webConfigId;
    }

    public void setWebConfigId(String value) {
        webConfigId = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (id != null) {
            sourceMap.put("id", id);
        }
        if (roleTypeId != null) {
            sourceMap.put("roleTypeId", roleTypeId);
        }
        if (webConfigId != null) {
            sourceMap.put("webConfigId", webConfigId);
        }
        return sourceMap;
    }
}
