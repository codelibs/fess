package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.exentity.JobLog;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

public class JobLogDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final JobLogDbm _instance = new JobLogDbm();

    private JobLogDbm() {
    }

    public static JobLogDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((JobLog) et).getEndTime(), (et, vl) -> ((JobLog) et).setEndTime(DfTypeUtil.toLong(vl)), "endTime");
        setupEpg(_epgMap, et -> ((JobLog) et).getId(), (et, vl) -> ((JobLog) et).setId(DfTypeUtil.toString(vl)), "id");
        setupEpg(_epgMap, et -> ((JobLog) et).getJobName(), (et, vl) -> ((JobLog) et).setJobName(DfTypeUtil.toString(vl)), "jobName");
        setupEpg(_epgMap, et -> ((JobLog) et).getJobStatus(), (et, vl) -> ((JobLog) et).setJobStatus(DfTypeUtil.toString(vl)), "jobStatus");
        setupEpg(_epgMap, et -> ((JobLog) et).getScriptData(), (et, vl) -> ((JobLog) et).setScriptData(DfTypeUtil.toString(vl)),
                "scriptData");
        setupEpg(_epgMap, et -> ((JobLog) et).getScriptResult(), (et, vl) -> ((JobLog) et).setScriptResult(DfTypeUtil.toString(vl)),
                "scriptResult");
        setupEpg(_epgMap, et -> ((JobLog) et).getScriptType(), (et, vl) -> ((JobLog) et).setScriptType(DfTypeUtil.toString(vl)),
                "scriptType");
        setupEpg(_epgMap, et -> ((JobLog) et).getStartTime(), (et, vl) -> ((JobLog) et).setStartTime(DfTypeUtil.toLong(vl)), "startTime");
        setupEpg(_epgMap, et -> ((JobLog) et).getTarget(), (et, vl) -> ((JobLog) et).setTarget(DfTypeUtil.toString(vl)), "target");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnEndTime = cci("endTime", "endTime", null, null, Long.class, "endTime", null, false, false, false,
            "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnId = cci("id", "id", null, null, String.class, "id", null, false, false, false, "String", 0, 0, null,
            false, null, null, null, null, null, false);
    protected final ColumnInfo _columnJobName = cci("jobName", "jobName", null, null, String.class, "jobName", null, false, false, false,
            "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnJobStatus = cci("jobStatus", "jobStatus", null, null, String.class, "jobStatus", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnScriptData = cci("scriptData", "scriptData", null, null, String.class, "scriptData", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnScriptResult = cci("scriptResult", "scriptResult", null, null, String.class, "scriptResult", null,
            false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnScriptType = cci("scriptType", "scriptType", null, null, String.class, "scriptType", null, false,
            false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnStartTime = cci("startTime", "startTime", null, null, Long.class, "startTime", null, false, false,
            false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnTarget = cci("target", "target", null, null, String.class, "target", null, false, false, false,
            "String", 0, 0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnEndTime() {
        return _columnEndTime;
    }

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnJobName() {
        return _columnJobName;
    }

    public ColumnInfo columnJobStatus() {
        return _columnJobStatus;
    }

    public ColumnInfo columnScriptData() {
        return _columnScriptData;
    }

    public ColumnInfo columnScriptResult() {
        return _columnScriptResult;
    }

    public ColumnInfo columnScriptType() {
        return _columnScriptType;
    }

    public ColumnInfo columnStartTime() {
        return _columnStartTime;
    }

    public ColumnInfo columnTarget() {
        return _columnTarget;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnEndTime());
        ls.add(columnId());
        ls.add(columnJobName());
        ls.add(columnJobStatus());
        ls.add(columnScriptData());
        ls.add(columnScriptResult());
        ls.add(columnScriptType());
        ls.add(columnStartTime());
        ls.add(columnTarget());
        return ls;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "job_log";
    protected final String _tableDispName = "job_log";
    protected final String _tablePropertyName = "JobLog";

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
        return "org.codelibs.fess.es.exentity.JobLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.cbean.JobLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.exbhv.JobLogBhv";
    }

    @Override
    public Class<? extends Entity> getEntityType() {
        return JobLog.class;
    }

    @Override
    public Entity newEntity() {
        return new JobLog();
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
