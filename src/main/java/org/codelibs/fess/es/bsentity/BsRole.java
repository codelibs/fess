package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.RoleDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsRole extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public RoleDbm asDBMeta() {
        return RoleDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "role";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** name */
    protected String name;

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

    public String getName() {
        checkSpecifiedProperty("name");
        return name;
    }

    public void setName(String value) {
        registerModifiedProperty("name");
        this.name = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (name != null) {
            sourceMap.put("name", name);
        }
        return sourceMap;
    }
}
