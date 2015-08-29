package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbway.DBDef;

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
    public String getTableDbName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableDispName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTablePropertyName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TableSqlName getTableSqlName() {
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getConditionBeanTypeName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getBehaviorTypeName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity newEntity() {
        // TODO Auto-generated method stub
        return null;
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
