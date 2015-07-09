package org.codelibs.fess.es.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.LabelToRoleDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsLabelToRole extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public LabelToRoleDbm asDBMeta() {
        return LabelToRoleDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "label_to_role";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** labelTypeId */
    protected String labelTypeId;

    /** roleTypeId */
    protected String roleTypeId;

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
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (labelTypeId != null) {
            sourceMap.put("labelTypeId", labelTypeId);
        }
        if (roleTypeId != null) {
            sourceMap.put("roleTypeId", roleTypeId);
        }
        return sourceMap;
    }
}
