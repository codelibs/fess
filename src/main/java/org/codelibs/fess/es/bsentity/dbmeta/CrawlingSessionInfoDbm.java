package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.exentity.CrawlingSessionInfo;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

public class CrawlingSessionInfoDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final CrawlingSessionInfoDbm _instance = new CrawlingSessionInfoDbm();

    private CrawlingSessionInfoDbm() {
    }

    public static CrawlingSessionInfoDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((CrawlingSessionInfo) et).getCrawlingSessionId(),
                (et, vl) -> ((CrawlingSessionInfo) et).setCrawlingSessionId(DfTypeUtil.toString(vl)), "crawlingSessionId");
        setupEpg(_epgMap, et -> ((CrawlingSessionInfo) et).getCreatedTime(),
                (et, vl) -> ((CrawlingSessionInfo) et).setCreatedTime(DfTypeUtil.toLong(vl)), "createdTime");
        setupEpg(_epgMap, et -> ((CrawlingSessionInfo) et).getId(), (et, vl) -> ((CrawlingSessionInfo) et).setId(DfTypeUtil.toString(vl)),
                "id");
        setupEpg(_epgMap, et -> ((CrawlingSessionInfo) et).getKey(),
                (et, vl) -> ((CrawlingSessionInfo) et).setKey(DfTypeUtil.toString(vl)), "key");
        setupEpg(_epgMap, et -> ((CrawlingSessionInfo) et).getValue(),
                (et, vl) -> ((CrawlingSessionInfo) et).setValue(DfTypeUtil.toString(vl)), "value");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnCrawlingSessionId = cci("crawlingSessionId", "crawlingSessionId", null, null, String.class,
            "crawlingSessionId", null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedTime = cci("createdTime", "createdTime", null, null, Long.class, "createdTime", null, false,
            false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnKey = cci("key", "key", null, null, String.class, "key", null, false, false, false, "String", 0, 0,
            null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnValue = cci("value", "value", null, null, String.class, "value", null, false, false, false, "String",
            0, 0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnCrawlingSessionId() {
        return _columnCrawlingSessionId;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnKey() {
        return _columnKey;
    }

    public ColumnInfo columnValue() {
        return _columnValue;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnCrawlingSessionId());
        ls.add(columnCreatedTime());
        ls.add(columnId());
        ls.add(columnKey());
        ls.add(columnValue());
        return ls;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "crawling_session_info";
    protected final String _tableDispName = "crawling_session_info";
    protected final String _tablePropertyName = "CrawlingSessionInfo";

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
        return "org.codelibs.fess.es.exentity.CrawlingSessionInfo";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.cbean.CrawlingSessionInfoCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.exbhv.CrawlingSessionInfoBhv";
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return CrawlingSessionInfo.class;
    }

    @Override
    public Entity newEntity() {
        return new CrawlingSessionInfo();
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
