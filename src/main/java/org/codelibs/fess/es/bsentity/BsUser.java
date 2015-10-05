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
    /** groups */
    protected String[] groups;

    /** name */
    protected String name;

    /** password */
    protected String password;

    /** roles */
    protected String[] roles;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String[] getGroups() {
        checkSpecifiedProperty("groups");
        return groups;
    }

    public void setGroups(String[] value) {
        registerModifiedProperty("groups");
        this.groups = value;
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

    public String[] getRoles() {
        checkSpecifiedProperty("roles");
        return roles;
    }

    public void setRoles(String[] value) {
        registerModifiedProperty("roles");
        this.roles = value;
    }

    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (groups != null) {
            sourceMap.put("groups", groups);
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
        if (roles != null) {
            sourceMap.put("roles", roles);
        }
        return sourceMap;
    }
}
