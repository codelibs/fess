package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.FileConfigToRoleDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsFileConfigToRole extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public FileConfigToRoleDbm asDBMeta() {
        return FileConfigToRoleDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "file_config_to_role";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** fileConfigId */
    protected String fileConfigId;

    /** roleTypeId */
    protected String roleTypeId;

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

    public String getRoleTypeId() {
        checkSpecifiedProperty("roleTypeId");
        return roleTypeId;
    }

    public void setRoleTypeId(String value) {
        registerModifiedProperty("roleTypeId");
        this.roleTypeId = value;
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
        if (roleTypeId != null) {
            sourceMap.put("roleTypeId", roleTypeId);
        }
        return sourceMap;
    }
}
