package org.codelibs.fess.es.bsentity;

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
    /** id */
    protected String id;

    /** labelTypeId */
    protected String labelTypeId;

    /** roleTypeId */
    protected String roleTypeId;

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

    public String getLabelTypeId() {
        return labelTypeId;
    }

    public void setLabelTypeId(String value) {
        labelTypeId = value;
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
        if (id != null) {
            sourceMap.put("id", id);
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
