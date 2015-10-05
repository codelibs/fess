package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.exentity.RequestHeader;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

public class RequestHeaderDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final RequestHeaderDbm _instance = new RequestHeaderDbm();

    private RequestHeaderDbm() {
    }

    public static RequestHeaderDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((RequestHeader) et).getCreatedBy(),
                (et, vl) -> ((RequestHeader) et).setCreatedBy(DfTypeUtil.toString(vl)), "createdBy");
        setupEpg(_epgMap, et -> ((RequestHeader) et).getCreatedTime(),
                (et, vl) -> ((RequestHeader) et).setCreatedTime(DfTypeUtil.toLong(vl)), "createdTime");
        setupEpg(_epgMap, et -> ((RequestHeader) et).getId(), (et, vl) -> ((RequestHeader) et).setId(DfTypeUtil.toString(vl)), "id");
        setupEpg(_epgMap, et -> ((RequestHeader) et).getName(), (et, vl) -> ((RequestHeader) et).setName(DfTypeUtil.toString(vl)), "name");
        setupEpg(_epgMap, et -> ((RequestHeader) et).getUpdatedBy(),
                (et, vl) -> ((RequestHeader) et).setUpdatedBy(DfTypeUtil.toString(vl)), "updatedBy");
        setupEpg(_epgMap, et -> ((RequestHeader) et).getUpdatedTime(),
                (et, vl) -> ((RequestHeader) et).setUpdatedTime(DfTypeUtil.toLong(vl)), "updatedTime");
        setupEpg(_epgMap, et -> ((RequestHeader) et).getValue(), (et, vl) -> ((RequestHeader) et).setValue(DfTypeUtil.toString(vl)),
                "value");
        setupEpg(_epgMap, et -> ((RequestHeader) et).getWebConfigId(),
                (et, vl) -> ((RequestHeader) et).setWebConfigId(DfTypeUtil.toString(vl)), "webConfigId");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnCreatedBy = cci("createdBy", "createdBy", null, null, String.class, "createdBy", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedTime = cci("createdTime", "createdTime", null, null, Long.class, "createdTime", null, false,
            false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnName = cci("name", "name", null, null, String.class, "name", null, false, false, false, "String", 0,
            0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedBy = cci("updatedBy", "updatedBy", null, null, String.class, "updatedBy", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedTime = cci("updatedTime", "updatedTime", null, null, Long.class, "updatedTime", null, false,
            false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnValue = cci("value", "value", null, null, String.class, "value", null, false, false, false, "String",
            0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnWebConfigId = cci("webConfigId", "webConfigId", null, null, String.class, "webConfigId", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnName() {
        return _columnName;
    }

    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    public ColumnInfo columnValue() {
        return _columnValue;
    }

    public ColumnInfo columnWebConfigId() {
        return _columnWebConfigId;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnId());
        ls.add(columnName());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        ls.add(columnValue());
        ls.add(columnWebConfigId());
        return ls;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "request_header";
    protected final String _tableDispName = "request_header";
    protected final String _tablePropertyName = "RequestHeader";

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
        return "org.codelibs.fess.es.exentity.RequestHeader";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.cbean.RequestHeaderCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.exbhv.RequestHeaderBhv";
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return RequestHeader.class;
    }

    @Override
    public Entity newEntity() {
        return new RequestHeader();
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
