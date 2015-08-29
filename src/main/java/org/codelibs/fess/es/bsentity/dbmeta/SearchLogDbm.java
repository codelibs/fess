package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbway.DBDef;

public class SearchLogDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final SearchLogDbm _instance = new SearchLogDbm();

    private SearchLogDbm() {
    }

    public static SearchLogDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnAccessType = cci("accessType", "accessType", null, null, String.class, "accessType", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnClientIp = cci("clientIp", "clientIp", null, null, String.class, "clientIp", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHitCount = cci("hitCount", "hitCount", null, null, Long.class, "hitCount", null, false, false, false,
            "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnQueryOffset = cci("queryOffset", "queryOffset", null, null, Integer.class, "queryOffset", null,
            false, false, false, "Integer", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnQueryPageSize = cci("queryPageSize", "queryPageSize", null, null, Integer.class, "queryPageSize",
            null, false, false, false, "Integer", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnReferer = cci("referer", "referer", null, null, String.class, "referer", null, false, false, false,
            "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnRequestedTime = cci("requestedTime", "requestedTime", null, null, Long.class, "requestedTime", null,
            false, false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnResponseTime = cci("responseTime", "responseTime", null, null, Integer.class, "responseTime", null,
            false, false, false, "Integer", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSearchWord = cci("searchWord", "searchWord", null, null, String.class, "searchWord", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserAgent = cci("userAgent", "userAgent", null, null, String.class, "userAgent", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserInfoId = cci("userInfoId", "userInfoId", null, null, String.class, "userInfoId", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserSessionId = cci("userSessionId", "userSessionId", null, null, String.class, "userSessionId",
            null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnAccessType() {
        return _columnAccessType;
    }

    public ColumnInfo columnClientIp() {
        return _columnClientIp;
    }

    public ColumnInfo columnHitCount() {
        return _columnHitCount;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnQueryOffset() {
        return _columnQueryOffset;
    }

    public ColumnInfo columnQueryPageSize() {
        return _columnQueryPageSize;
    }

    public ColumnInfo columnReferer() {
        return _columnReferer;
    }

    public ColumnInfo columnRequestedTime() {
        return _columnRequestedTime;
    }

    public ColumnInfo columnResponseTime() {
        return _columnResponseTime;
    }

    public ColumnInfo columnSearchWord() {
        return _columnSearchWord;
    }

    public ColumnInfo columnUserAgent() {
        return _columnUserAgent;
    }

    public ColumnInfo columnUserInfoId() {
        return _columnUserInfoId;
    }

    public ColumnInfo columnUserSessionId() {
        return _columnUserSessionId;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnAccessType());
        ls.add(columnClientIp());
        ls.add(columnHitCount());
        ls.add(columnId());
        ls.add(columnQueryOffset());
        ls.add(columnQueryPageSize());
        ls.add(columnReferer());
        ls.add(columnRequestedTime());
        ls.add(columnResponseTime());
        ls.add(columnSearchWord());
        ls.add(columnUserAgent());
        ls.add(columnUserInfoId());
        ls.add(columnUserSessionId());
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
