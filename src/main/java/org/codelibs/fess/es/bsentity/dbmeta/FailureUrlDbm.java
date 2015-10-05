package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.exentity.FailureUrl;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

public class FailureUrlDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final FailureUrlDbm _instance = new FailureUrlDbm();

    private FailureUrlDbm() {
    }

    public static FailureUrlDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((FailureUrl) et).getConfigId(), (et, vl) -> ((FailureUrl) et).setConfigId(DfTypeUtil.toString(vl)),
                "configId");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getErrorCount(), (et, vl) -> ((FailureUrl) et).setErrorCount(DfTypeUtil.toInteger(vl)),
                "errorCount");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getErrorLog(), (et, vl) -> ((FailureUrl) et).setErrorLog(DfTypeUtil.toString(vl)),
                "errorLog");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getErrorName(), (et, vl) -> ((FailureUrl) et).setErrorName(DfTypeUtil.toString(vl)),
                "errorName");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getId(), (et, vl) -> ((FailureUrl) et).setId(DfTypeUtil.toString(vl)), "id");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getLastAccessTime(),
                (et, vl) -> ((FailureUrl) et).setLastAccessTime(DfTypeUtil.toLong(vl)), "lastAccessTime");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getThreadName(), (et, vl) -> ((FailureUrl) et).setThreadName(DfTypeUtil.toString(vl)),
                "threadName");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getUrl(), (et, vl) -> ((FailureUrl) et).setUrl(DfTypeUtil.toString(vl)), "url");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnConfigId = cci("configId", "configId", null, null, String.class, "configId", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnErrorCount = cci("errorCount", "errorCount", null, null, Integer.class, "errorCount", null, false,
            false, false, "Integer", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnErrorLog = cci("errorLog", "errorLog", null, null, String.class, "errorLog", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnErrorName = cci("errorName", "errorName", null, null, String.class, "errorName", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLastAccessTime = cci("lastAccessTime", "lastAccessTime", null, null, Long.class, "lastAccessTime",
            null, false, false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnThreadName = cci("threadName", "threadName", null, null, String.class, "threadName", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUrl = cci("url", "url", null, null, String.class, "url", null, false, false, false, "String", 0, 0,
            null, false, null, null, null, null, null, false);

    public ColumnInfo columnConfigId() {
        return _columnConfigId;
    }

    public ColumnInfo columnErrorCount() {
        return _columnErrorCount;
    }

    public ColumnInfo columnErrorLog() {
        return _columnErrorLog;
    }

    public ColumnInfo columnErrorName() {
        return _columnErrorName;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnLastAccessTime() {
        return _columnLastAccessTime;
    }

    public ColumnInfo columnThreadName() {
        return _columnThreadName;
    }

    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnConfigId());
        ls.add(columnErrorCount());
        ls.add(columnErrorLog());
        ls.add(columnErrorName());
        ls.add(columnId());
        ls.add(columnLastAccessTime());
        ls.add(columnThreadName());
        ls.add(columnUrl());
        return ls;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "failure_url";
    protected final String _tableDispName = "failure_url";
    protected final String _tablePropertyName = "FailureUrl";

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
        return "org.codelibs.fess.es.exentity.FailureUrl";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.cbean.FailureUrlCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.exbhv.FailureUrlBhv";
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return FailureUrl.class;
    }

    @Override
    public Entity newEntity() {
        return new FailureUrl();
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
