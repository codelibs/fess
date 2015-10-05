package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.exentity.FileConfig;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

public class FileConfigDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final FileConfigDbm _instance = new FileConfigDbm();

    private FileConfigDbm() {
    }

    public static FileConfigDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((FileConfig) et).getAvailable(), (et, vl) -> ((FileConfig) et).setAvailable(DfTypeUtil.toBoolean(vl)),
                "available");
        setupEpg(_epgMap, et -> ((FileConfig) et).getBoost(), (et, vl) -> ((FileConfig) et).setBoost(DfTypeUtil.toFloat(vl)), "boost");
        setupEpg(_epgMap, et -> ((FileConfig) et).getConfigParameter(),
                (et, vl) -> ((FileConfig) et).setConfigParameter(DfTypeUtil.toString(vl)), "configParameter");
        setupEpg(_epgMap, et -> ((FileConfig) et).getCreatedBy(), (et, vl) -> ((FileConfig) et).setCreatedBy(DfTypeUtil.toString(vl)),
                "createdBy");
        setupEpg(_epgMap, et -> ((FileConfig) et).getCreatedTime(), (et, vl) -> ((FileConfig) et).setCreatedTime(DfTypeUtil.toLong(vl)),
                "createdTime");
        setupEpg(_epgMap, et -> ((FileConfig) et).getDepth(), (et, vl) -> ((FileConfig) et).setDepth(DfTypeUtil.toInteger(vl)), "depth");
        setupEpg(_epgMap, et -> ((FileConfig) et).getExcludedDocPaths(),
                (et, vl) -> ((FileConfig) et).setExcludedDocPaths(DfTypeUtil.toString(vl)), "excludedDocPaths");
        setupEpg(_epgMap, et -> ((FileConfig) et).getExcludedPaths(),
                (et, vl) -> ((FileConfig) et).setExcludedPaths(DfTypeUtil.toString(vl)), "excludedPaths");
        setupEpg(_epgMap, et -> ((FileConfig) et).getId(), (et, vl) -> ((FileConfig) et).setId(DfTypeUtil.toString(vl)), "id");
        setupEpg(_epgMap, et -> ((FileConfig) et).getIncludedDocPaths(),
                (et, vl) -> ((FileConfig) et).setIncludedDocPaths(DfTypeUtil.toString(vl)), "includedDocPaths");
        setupEpg(_epgMap, et -> ((FileConfig) et).getIncludedPaths(),
                (et, vl) -> ((FileConfig) et).setIncludedPaths(DfTypeUtil.toString(vl)), "includedPaths");
        setupEpg(_epgMap, et -> ((FileConfig) et).getIntervalTime(),
                (et, vl) -> ((FileConfig) et).setIntervalTime(DfTypeUtil.toInteger(vl)), "intervalTime");
        setupEpg(_epgMap, et -> ((FileConfig) et).getMaxAccessCount(),
                (et, vl) -> ((FileConfig) et).setMaxAccessCount(DfTypeUtil.toLong(vl)), "maxAccessCount");
        setupEpg(_epgMap, et -> ((FileConfig) et).getName(), (et, vl) -> ((FileConfig) et).setName(DfTypeUtil.toString(vl)), "name");
        setupEpg(_epgMap, et -> ((FileConfig) et).getNumOfThread(), (et, vl) -> ((FileConfig) et).setNumOfThread(DfTypeUtil.toInteger(vl)),
                "numOfThread");
        setupEpg(_epgMap, et -> ((FileConfig) et).getPaths(), (et, vl) -> ((FileConfig) et).setPaths(DfTypeUtil.toString(vl)), "paths");
        setupEpg(_epgMap, et -> ((FileConfig) et).getSortOrder(), (et, vl) -> ((FileConfig) et).setSortOrder(DfTypeUtil.toInteger(vl)),
                "sortOrder");
        setupEpg(_epgMap, et -> ((FileConfig) et).getUpdatedBy(), (et, vl) -> ((FileConfig) et).setUpdatedBy(DfTypeUtil.toString(vl)),
                "updatedBy");
        setupEpg(_epgMap, et -> ((FileConfig) et).getUpdatedTime(), (et, vl) -> ((FileConfig) et).setUpdatedTime(DfTypeUtil.toLong(vl)),
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
    protected final ColumnInfo _columnConfigParameter = cci("configParameter", "configParameter", null, null, String.class,
            "configParameter", null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedBy = cci("createdBy", "createdBy", null, null, String.class, "createdBy", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedTime = cci("createdTime", "createdTime", null, null, Long.class, "createdTime", null, false,
            false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnDepth = cci("depth", "depth", null, null, Integer.class, "depth", null, false, false, false,
            "Integer", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnExcludedDocPaths = cci("excludedDocPaths", "excludedDocPaths", null, null, String.class,
            "excludedDocPaths", null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnExcludedPaths = cci("excludedPaths", "excludedPaths", null, null, String.class, "excludedPaths",
            null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIncludedDocPaths = cci("includedDocPaths", "includedDocPaths", null, null, String.class,
            "includedDocPaths", null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIncludedPaths = cci("includedPaths", "includedPaths", null, null, String.class, "includedPaths",
            null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIntervalTime = cci("intervalTime", "intervalTime", null, null, Integer.class, "intervalTime", null,
            false, false, false, "Integer", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMaxAccessCount = cci("maxAccessCount", "maxAccessCount", null, null, Long.class, "maxAccessCount",
            null, false, false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnName = cci("name", "name", null, null, String.class, "name", null, false, false, false, "String", 0,
            0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnNumOfThread = cci("numOfThread", "numOfThread", null, null, Integer.class, "numOfThread", null,
            false, false, false, "Integer", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPaths = cci("paths", "paths", null, null, String.class, "paths", null, false, false, false, "String",
            0, 0, null, false, null, null, null, null, null, false);
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

    public ColumnInfo columnConfigParameter() {
        return _columnConfigParameter;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnDepth() {
        return _columnDepth;
    }

    public ColumnInfo columnExcludedDocPaths() {
        return _columnExcludedDocPaths;
    }

    public ColumnInfo columnExcludedPaths() {
        return _columnExcludedPaths;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnIncludedDocPaths() {
        return _columnIncludedDocPaths;
    }

    public ColumnInfo columnIncludedPaths() {
        return _columnIncludedPaths;
    }

    public ColumnInfo columnIntervalTime() {
        return _columnIntervalTime;
    }

    public ColumnInfo columnMaxAccessCount() {
        return _columnMaxAccessCount;
    }

    public ColumnInfo columnName() {
        return _columnName;
    }

    public ColumnInfo columnNumOfThread() {
        return _columnNumOfThread;
    }

    public ColumnInfo columnPaths() {
        return _columnPaths;
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
        ls.add(columnConfigParameter());
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnDepth());
        ls.add(columnExcludedDocPaths());
        ls.add(columnExcludedPaths());
        ls.add(columnId());
        ls.add(columnIncludedDocPaths());
        ls.add(columnIncludedPaths());
        ls.add(columnIntervalTime());
        ls.add(columnMaxAccessCount());
        ls.add(columnName());
        ls.add(columnNumOfThread());
        ls.add(columnPaths());
        ls.add(columnSortOrder());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        return ls;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "file_config";
    protected final String _tableDispName = "file_config";
    protected final String _tablePropertyName = "FileConfig";

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
        return "org.codelibs.fess.es.exentity.FileConfig";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.cbean.FileConfigCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.exbhv.FileConfigBhv";
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return FileConfig.class;
    }

    @Override
    public Entity newEntity() {
        return new FileConfig();
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
