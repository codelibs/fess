package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.exentity.DataConfig;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

public class DataConfigDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final DataConfigDbm _instance = new DataConfigDbm();

    private DataConfigDbm() {
    }

    public static DataConfigDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((DataConfig) et).getAvailable(), (et, vl) -> ((DataConfig) et).setAvailable(DfTypeUtil.toBoolean(vl)),
                "available");
        setupEpg(_epgMap, et -> ((DataConfig) et).getBoost(), (et, vl) -> ((DataConfig) et).setBoost(DfTypeUtil.toFloat(vl)), "boost");
        setupEpg(_epgMap, et -> ((DataConfig) et).getCreatedBy(), (et, vl) -> ((DataConfig) et).setCreatedBy(DfTypeUtil.toString(vl)),
                "createdBy");
        setupEpg(_epgMap, et -> ((DataConfig) et).getCreatedTime(), (et, vl) -> ((DataConfig) et).setCreatedTime(DfTypeUtil.toLong(vl)),
                "createdTime");
        setupEpg(_epgMap, et -> ((DataConfig) et).getHandlerName(), (et, vl) -> ((DataConfig) et).setHandlerName(DfTypeUtil.toString(vl)),
                "handlerName");
        setupEpg(_epgMap, et -> ((DataConfig) et).getHandlerParameter(),
                (et, vl) -> ((DataConfig) et).setHandlerParameter(DfTypeUtil.toString(vl)), "handlerParameter");
        setupEpg(_epgMap, et -> ((DataConfig) et).getHandlerScript(),
                (et, vl) -> ((DataConfig) et).setHandlerScript(DfTypeUtil.toString(vl)), "handlerScript");
        setupEpg(_epgMap, et -> ((DataConfig) et).getId(), (et, vl) -> ((DataConfig) et).setId(DfTypeUtil.toString(vl)), "id");
        setupEpg(_epgMap, et -> ((DataConfig) et).getName(), (et, vl) -> ((DataConfig) et).setName(DfTypeUtil.toString(vl)), "name");
        setupEpg(_epgMap, et -> ((DataConfig) et).getSortOrder(), (et, vl) -> ((DataConfig) et).setSortOrder(DfTypeUtil.toInteger(vl)),
                "sortOrder");
        setupEpg(_epgMap, et -> ((DataConfig) et).getUpdatedBy(), (et, vl) -> ((DataConfig) et).setUpdatedBy(DfTypeUtil.toString(vl)),
                "updatedBy");
        setupEpg(_epgMap, et -> ((DataConfig) et).getUpdatedTime(), (et, vl) -> ((DataConfig) et).setUpdatedTime(DfTypeUtil.toLong(vl)),
                "updatedTime");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnAvailable = cci("available", "available", null, null, Boolean.class, "available", null, false, false,
            false, "Boolean", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnBoost = cci("boost", "boost", null, null, Float.class, "boost", null, false, false, false, "Float",
            0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedBy = cci("createdBy", "createdBy", null, null, String.class, "createdBy", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedTime = cci("createdTime", "createdTime", null, null, Long.class, "createdTime", null, false,
            false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHandlerName = cci("handlerName", "handlerName", null, null, String.class, "handlerName", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHandlerParameter = cci("handlerParameter", "handlerParameter", null, null, String.class,
            "handlerParameter", null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHandlerScript = cci("handlerScript", "handlerScript", null, null, String.class, "handlerScript",
            null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnName = cci("name", "name", null, null, String.class, "name", null, false, false, false, "String", 0,
            0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSortOrder = cci("sortOrder", "sortOrder", null, null, Integer.class, "sortOrder", null, false, false,
            false, "Integer", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedBy = cci("updatedBy", "updatedBy", null, null, String.class, "updatedBy", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedTime = cci("updatedTime", "updatedTime", null, null, Long.class, "updatedTime", null, false,
            false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnAvailable() {
        return _columnAvailable;
    }

    public ColumnInfo columnBoost() {
        return _columnBoost;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnHandlerName() {
        return _columnHandlerName;
    }

    public ColumnInfo columnHandlerParameter() {
        return _columnHandlerParameter;
    }

    public ColumnInfo columnHandlerScript() {
        return _columnHandlerScript;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnName() {
        return _columnName;
    }

    public ColumnInfo columnSortOrder() {
        return _columnSortOrder;
    }

    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnAvailable());
        ls.add(columnBoost());
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnHandlerName());
        ls.add(columnHandlerParameter());
        ls.add(columnHandlerScript());
        ls.add(columnId());
        ls.add(columnName());
        ls.add(columnSortOrder());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        return ls;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "data_config";
    protected final String _tableDispName = "data_config";
    protected final String _tablePropertyName = "DataConfig";

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
        return "org.codelibs.fess.es.exentity.DataConfig";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.cbean.DataConfigCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.exbhv.DataConfigBhv";
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return DataConfig.class;
    }

    @Override
    public Entity newEntity() {
        return new DataConfig();
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
