package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.DataConfigToRoleDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsDataConfigToRole extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public DataConfigToRoleDbm asDBMeta() {
        return DataConfigToRoleDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "data_config_to_role";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** dataConfigId */
    protected String dataConfigId;

    /** id */
    protected String id;

    /** roleTypeId */
    protected String roleTypeId;

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

    public String getRoleTypeId() {
        return roleTypeId;
    }

    public void setRoleTypeId(String value) {
        roleTypeId = value;
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
        if (roleTypeId != null) {
            sourceMap.put("roleTypeId", roleTypeId);
        }
        return sourceMap;
    }
}
