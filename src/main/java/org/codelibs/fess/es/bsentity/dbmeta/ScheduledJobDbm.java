package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.exentity.ScheduledJob;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

public class ScheduledJobDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final ScheduledJobDbm _instance = new ScheduledJobDbm();

    private ScheduledJobDbm() {
    }

    public static ScheduledJobDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getAvailable(), (et, vl) -> ((ScheduledJob) et).setAvailable(DfTypeUtil.toBoolean(vl)),
                "available");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getCrawler(), (et, vl) -> ((ScheduledJob) et).setCrawler(DfTypeUtil.toBoolean(vl)),
                "crawler");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getCreatedBy(), (et, vl) -> ((ScheduledJob) et).setCreatedBy(DfTypeUtil.toString(vl)),
                "createdBy");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getCreatedTime(),
                (et, vl) -> ((ScheduledJob) et).setCreatedTime(DfTypeUtil.toLong(vl)), "createdTime");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getCronExpression(),
                (et, vl) -> ((ScheduledJob) et).setCronExpression(DfTypeUtil.toString(vl)), "cronExpression");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getId(), (et, vl) -> ((ScheduledJob) et).setId(DfTypeUtil.toString(vl)), "id");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getJobLogging(),
                (et, vl) -> ((ScheduledJob) et).setJobLogging(DfTypeUtil.toBoolean(vl)), "jobLogging");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getName(), (et, vl) -> ((ScheduledJob) et).setName(DfTypeUtil.toString(vl)), "name");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getScriptData(),
                (et, vl) -> ((ScheduledJob) et).setScriptData(DfTypeUtil.toString(vl)), "scriptData");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getScriptType(),
                (et, vl) -> ((ScheduledJob) et).setScriptType(DfTypeUtil.toString(vl)), "scriptType");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getSortOrder(), (et, vl) -> ((ScheduledJob) et).setSortOrder(DfTypeUtil.toInteger(vl)),
                "sortOrder");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getTarget(), (et, vl) -> ((ScheduledJob) et).setTarget(DfTypeUtil.toString(vl)),
                "target");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getUpdatedBy(), (et, vl) -> ((ScheduledJob) et).setUpdatedBy(DfTypeUtil.toString(vl)),
                "updatedBy");
        setupEpg(_epgMap, et -> ((ScheduledJob) et).getUpdatedTime(),
                (et, vl) -> ((ScheduledJob) et).setUpdatedTime(DfTypeUtil.toLong(vl)), "updatedTime");
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
    protected final ColumnInfo _columnCrawler = cci("crawler", "crawler", null, null, Boolean.class, "crawler", null, false, false, false,
            "Boolean", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedBy = cci("createdBy", "createdBy", null, null, String.class, "createdBy", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedTime = cci("createdTime", "createdTime", null, null, Long.class, "createdTime", null, false,
            false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCronExpression = cci("cronExpression", "cronExpression", null, null, String.class, "cronExpression",
            null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnJobLogging = cci("jobLogging", "jobLogging", null, null, Boolean.class, "jobLogging", null, false,
            false, false, "Boolean", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnName = cci("name", "name", null, null, String.class, "name", null, false, false, false, "String", 0,
            0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnScriptData = cci("scriptData", "scriptData", null, null, String.class, "scriptData", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnScriptType = cci("scriptType", "scriptType", null, null, String.class, "scriptType", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSortOrder = cci("sortOrder", "sortOrder", null, null, Integer.class, "sortOrder", null, false, false,
            false, "Integer", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnTarget = cci("target", "target", null, null, String.class, "target", null, false, false, false,
            "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedBy = cci("updatedBy", "updatedBy", null, null, String.class, "updatedBy", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedTime = cci("updatedTime", "updatedTime", null, null, Long.class, "updatedTime", null, false,
            false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnAvailable() {
        return _columnAvailable;
    }

    public ColumnInfo columnCrawler() {
        return _columnCrawler;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnCronExpression() {
        return _columnCronExpression;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnJobLogging() {
        return _columnJobLogging;
    }

    public ColumnInfo columnName() {
        return _columnName;
    }

    public ColumnInfo columnScriptData() {
        return _columnScriptData;
    }

    public ColumnInfo columnScriptType() {
        return _columnScriptType;
    }

    public ColumnInfo columnSortOrder() {
        return _columnSortOrder;
    }

    public ColumnInfo columnTarget() {
        return _columnTarget;
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
        ls.add(columnCrawler());
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnCronExpression());
        ls.add(columnId());
        ls.add(columnJobLogging());
        ls.add(columnName());
        ls.add(columnScriptData());
        ls.add(columnScriptType());
        ls.add(columnSortOrder());
        ls.add(columnTarget());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        return ls;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "scheduled_job";
    protected final String _tableDispName = "scheduled_job";
    protected final String _tablePropertyName = "ScheduledJob";

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
        return "org.codelibs.fess.es.exentity.ScheduledJob";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.cbean.ScheduledJobCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.exbhv.ScheduledJobBhv";
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return ScheduledJob.class;
    }

    @Override
    public Entity newEntity() {
        return new ScheduledJob();
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
