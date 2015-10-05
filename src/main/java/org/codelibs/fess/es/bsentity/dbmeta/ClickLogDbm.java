package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.exentity.ClickLog;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

public class ClickLogDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final ClickLogDbm _instance = new ClickLogDbm();

    private ClickLogDbm() {
    }

    public static ClickLogDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((ClickLog) et).getId(), (et, vl) -> ((ClickLog) et).setId(DfTypeUtil.toString(vl)), "id");
        setupEpg(_epgMap, et -> ((ClickLog) et).getRequestedTime(), (et, vl) -> ((ClickLog) et).setRequestedTime(DfTypeUtil.toLong(vl)),
                "requestedTime");
        setupEpg(_epgMap, et -> ((ClickLog) et).getSearchLogId(), (et, vl) -> ((ClickLog) et).setSearchLogId(DfTypeUtil.toString(vl)),
                "searchLogId");
        setupEpg(_epgMap, et -> ((ClickLog) et).getUrl(), (et, vl) -> ((ClickLog) et).setUrl(DfTypeUtil.toString(vl)), "url");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnRequestedTime = cci("requestedTime", "requestedTime", null, null, Long.class, "requestedTime", null,
            false, false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSearchLogId = cci("searchLogId", "searchLogId", null, null, String.class, "searchLogId", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUrl = cci("url", "url", null, null, String.class, "url", null, false, false, false, "String", 0, 0,
            null, false, null, null, null, null, null, false);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnRequestedTime() {
        return _columnRequestedTime;
    }

    public ColumnInfo columnSearchLogId() {
        return _columnSearchLogId;
    }

    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnRequestedTime());
        ls.add(columnSearchLogId());
        ls.add(columnUrl());
        return ls;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "click_log";
    protected final String _tableDispName = "click_log";
    protected final String _tablePropertyName = "ClickLog";

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
        return "org.codelibs.fess.es.exentity.ClickLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.cbean.ClickLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.exbhv.ClickLogBhv";
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return ClickLog.class;
    }

    @Override
    public Entity newEntity() {
        return new ClickLog();
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
