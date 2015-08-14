package org.codelibs.fess.es.bsentity;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.es.bsentity.dbmeta.UserDbm;

/**
 * ${table.comment}
 * @author FreeGen
 */
public class BsUser extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Override
    public UserDbm asDBMeta() {
        return UserDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "user";
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** group */
    protected String group;

    /** name */
    protected String name;

    /** password */
    protected String password;

    /** role */
    protected String role;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getGroup() {
        checkSpecifiedProperty("group");
        return group;
    }

    public void setGroup(String value) {
        registerModifiedProperty("group");
        this.group = value;
    }

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

    public String getPassword() {
        checkSpecifiedProperty("password");
        return password;
    }

    public void setPassword(String value) {
        registerModifiedProperty("password");
        this.password = value;
    }

    public String getRole() {
        checkSpecifiedProperty("role");
        return role;
    }

    public void setRole(String value) {
        registerModifiedProperty("role");
        this.role = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (group != null) {
            sourceMap.put("group", group);
        }
        if (asDocMeta().id() != null) {
            sourceMap.put("id", asDocMeta().id());
        }
        if (name != null) {
            sourceMap.put("name", name);
        }
        if (password != null) {
            sourceMap.put("password", password);
        }
        if (role != null) {
            sourceMap.put("role", role);
        }
        return sourceMap;
    }
}
