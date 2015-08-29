package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbway.DBDef;

public class WebConfigDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final WebConfigDbm _instance = new WebConfigDbm();

    private WebConfigDbm() {
    }

    public static WebConfigDbm getInstance() {
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
    protected final ColumnInfo _columnExcludedDocUrls = cci("excludedDocUrls", "excludedDocUrls", null, null, String.class,
            "excludedDocUrls", null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnExcludedUrls = cci("excludedUrls", "excludedUrls", null, null, String.class, "excludedUrls", null,
            false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIncludedDocUrls = cci("includedDocUrls", "includedDocUrls", null, null, String.class,
            "includedDocUrls", null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIncludedUrls = cci("includedUrls", "includedUrls", null, null, String.class, "includedUrls", null,
            false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIntervalTime = cci("intervalTime", "intervalTime", null, null, Integer.class, "intervalTime", null,
            false, false, false, "Integer", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMaxAccessCount = cci("maxAccessCount", "maxAccessCount", null, null, Long.class, "maxAccessCount",
            null, false, false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnName = cci("name", "name", null, null, String.class, "name", null, false, false, false, "String", 0,
            0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnNumOfThread = cci("numOfThread", "numOfThread", null, null, Integer.class, "numOfThread", null,
            false, false, false, "Integer", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSortOrder = cci("sortOrder", "sortOrder", null, null, Integer.class, "sortOrder", null, false, false,
            false, "Integer", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedBy = cci("updatedBy", "updatedBy", null, null, String.class, "updatedBy", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedTime = cci("updatedTime", "updatedTime", null, null, Long.class, "updatedTime", null, false,
            false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUrls = cci("urls", "urls", null, null, String.class, "urls", null, false, false, false, "String", 0,
            0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserAgent = cci("userAgent", "userAgent", null, null, String.class, "userAgent", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);

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

    public ColumnInfo columnExcludedDocUrls() {
        return _columnExcludedDocUrls;
    }

    public ColumnInfo columnExcludedUrls() {
        return _columnExcludedUrls;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnIncludedDocUrls() {
        return _columnIncludedDocUrls;
    }

    public ColumnInfo columnIncludedUrls() {
        return _columnIncludedUrls;
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

    public ColumnInfo columnSortOrder() {
        return _columnSortOrder;
    }

    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    public ColumnInfo columnUrls() {
        return _columnUrls;
    }

    public ColumnInfo columnUserAgent() {
        return _columnUserAgent;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnAvailable());
        ls.add(columnBoost());
        ls.add(columnConfigParameter());
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnDepth());
        ls.add(columnExcludedDocUrls());
        ls.add(columnExcludedUrls());
        ls.add(columnId());
        ls.add(columnIncludedDocUrls());
        ls.add(columnIncludedUrls());
        ls.add(columnIntervalTime());
        ls.add(columnMaxAccessCount());
        ls.add(columnName());
        ls.add(columnNumOfThread());
        ls.add(columnSortOrder());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        ls.add(columnUrls());
        ls.add(columnUserAgent());
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
