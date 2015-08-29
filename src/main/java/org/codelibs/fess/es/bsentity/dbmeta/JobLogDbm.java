package org.codelibs.fess.es.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbway.DBDef;

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
