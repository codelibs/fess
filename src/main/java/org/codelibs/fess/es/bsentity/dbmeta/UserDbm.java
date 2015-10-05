package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.exentity.User;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

public class UserDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final UserDbm _instance = new UserDbm();

    private UserDbm() {
    }

    public static UserDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                    Property Gateway
    //                                                                    ================
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    {
        setupEpg(_epgMap, et -> ((User) et).getGroups(), (et, vl) -> ((User) et).setGroups((String[]) vl), "groups");
        setupEpg(_epgMap, et -> ((User) et).getId(), (et, vl) -> ((User) et).setId(DfTypeUtil.toString(vl)), "id");
        setupEpg(_epgMap, et -> ((User) et).getName(), (et, vl) -> ((User) et).setName(DfTypeUtil.toString(vl)), "name");
        setupEpg(_epgMap, et -> ((User) et).getPassword(), (et, vl) -> ((User) et).setPassword(DfTypeUtil.toString(vl)), "password");
        setupEpg(_epgMap, et -> ((User) et).getRoles(), (et, vl) -> ((User) et).setRoles((String[]) vl), "roles");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnGroups = cci("groups", "groups", null, null, String[].class, "groups", null, false, false, false,
            "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnName = cci("name", "name", null, null, String.class, "name", null, false, false, false, "String", 0,
            0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPassword = cci("password", "password", null, null, String.class, "password", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnRoles = cci("roles", "roles", null, null, String[].class, "roles", null, false, false, false,
            "String", 0, 0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnGroups() {
        return _columnGroups;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnName() {
        return _columnName;
    }

    public ColumnInfo columnPassword() {
        return _columnPassword;
    }

    public ColumnInfo columnRoles() {
        return _columnRoles;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnGroups());
        ls.add(columnId());
        ls.add(columnName());
        ls.add(columnPassword());
        ls.add(columnRoles());
        return ls;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "user";
    protected final String _tableDispName = "user";
    protected final String _tablePropertyName = "User";

    public String getTableDbName() {
        return _tableDbName;
    }

    @Override
    public String getTableDispName() {
        return _tableDispName;
    }

    @Override
    public String getTablePropertyName() {
        return _tablePropertyName;
    }

    @Override
    public TableSqlName getTableSqlName() {
        return null;
    }

    @Override
    public String getProjectName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProjectPrefix() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getGenerationGapBasePrefix() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DBDef getCurrentDBDef() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasPrimaryKey() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasCompoundPrimaryKey() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getEntityTypeName() {
        return "org.codelibs.fess.es.exentity.User";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.cbean.UserCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.exbhv.UserBhv";
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return User.class;
    }

    @Override
    public Entity newEntity() {
        return new User();
    }

    @Override
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
        // TODO Auto-generated method stub

    }

    @Override
    public void acceptAllColumnMap(Entity entity, Map<String, ? extends Object> allColumnMap) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<String, Object> extractPrimaryKeyMap(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Object> extractAllColumnMap(Entity entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected UniqueInfo cpui() {
        // TODO Auto-generated method stub
        return null;
    }

}
