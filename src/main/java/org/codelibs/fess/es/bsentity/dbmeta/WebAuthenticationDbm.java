package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.exentity.WebAuthentication;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

public class WebAuthenticationDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final WebAuthenticationDbm _instance = new WebAuthenticationDbm();

    private WebAuthenticationDbm() {
    }

    public static WebAuthenticationDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getAuthRealm(),
                (et, vl) -> ((WebAuthentication) et).setAuthRealm(DfTypeUtil.toString(vl)), "authRealm");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getCreatedBy(),
                (et, vl) -> ((WebAuthentication) et).setCreatedBy(DfTypeUtil.toString(vl)), "createdBy");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getCreatedTime(),
                (et, vl) -> ((WebAuthentication) et).setCreatedTime(DfTypeUtil.toLong(vl)), "createdTime");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getHostname(),
                (et, vl) -> ((WebAuthentication) et).setHostname(DfTypeUtil.toString(vl)), "hostname");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getId(), (et, vl) -> ((WebAuthentication) et).setId(DfTypeUtil.toString(vl)), "id");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getParameters(),
                (et, vl) -> ((WebAuthentication) et).setParameters(DfTypeUtil.toString(vl)), "parameters");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getPassword(),
                (et, vl) -> ((WebAuthentication) et).setPassword(DfTypeUtil.toString(vl)), "password");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getPort(), (et, vl) -> ((WebAuthentication) et).setPort(DfTypeUtil.toInteger(vl)),
                "port");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getProtocolScheme(),
                (et, vl) -> ((WebAuthentication) et).setProtocolScheme(DfTypeUtil.toString(vl)), "protocolScheme");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getUpdatedBy(),
                (et, vl) -> ((WebAuthentication) et).setUpdatedBy(DfTypeUtil.toString(vl)), "updatedBy");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getUpdatedTime(),
                (et, vl) -> ((WebAuthentication) et).setUpdatedTime(DfTypeUtil.toLong(vl)), "updatedTime");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getUsername(),
                (et, vl) -> ((WebAuthentication) et).setUsername(DfTypeUtil.toString(vl)), "username");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getWebConfigId(),
                (et, vl) -> ((WebAuthentication) et).setWebConfigId(DfTypeUtil.toString(vl)), "webConfigId");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnAuthRealm = cci("authRealm", "authRealm", null, null, String.class, "authRealm", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedBy = cci("createdBy", "createdBy", null, null, String.class, "createdBy", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedTime = cci("createdTime", "createdTime", null, null, Long.class, "createdTime", null, false,
            false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHostname = cci("hostname", "hostname", null, null, String.class, "hostname", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnParameters = cci("parameters", "parameters", null, null, String.class, "parameters", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPassword = cci("password", "password", null, null, String.class, "password", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPort = cci("port", "port", null, null, Integer.class, "port", null, false, false, false, "Integer",
            0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnProtocolScheme = cci("protocolScheme", "protocolScheme", null, null, String.class, "protocolScheme",
            null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedBy = cci("updatedBy", "updatedBy", null, null, String.class, "updatedBy", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedTime = cci("updatedTime", "updatedTime", null, null, Long.class, "updatedTime", null, false,
            false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUsername = cci("username", "username", null, null, String.class, "username", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnWebConfigId = cci("webConfigId", "webConfigId", null, null, String.class, "webConfigId", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnAuthRealm() {
        return _columnAuthRealm;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnHostname() {
        return _columnHostname;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnParameters() {
        return _columnParameters;
    }

    public ColumnInfo columnPassword() {
        return _columnPassword;
    }

    public ColumnInfo columnPort() {
        return _columnPort;
    }

    public ColumnInfo columnProtocolScheme() {
        return _columnProtocolScheme;
    }

    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    public ColumnInfo columnUsername() {
        return _columnUsername;
    }

    public ColumnInfo columnWebConfigId() {
        return _columnWebConfigId;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnAuthRealm());
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnHostname());
        ls.add(columnId());
        ls.add(columnParameters());
        ls.add(columnPassword());
        ls.add(columnPort());
        ls.add(columnProtocolScheme());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        ls.add(columnUsername());
        ls.add(columnWebConfigId());
        return ls;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "web_authentication";
    protected final String _tableDispName = "web_authentication";
    protected final String _tablePropertyName = "WebAuthentication";

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
        return "org.codelibs.fess.es.exentity.WebAuthentication";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.cbean.WebAuthenticationCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.exbhv.WebAuthenticationBhv";
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return WebAuthentication.class;
    }

    @Override
    public Entity newEntity() {
        return new WebAuthentication();
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
