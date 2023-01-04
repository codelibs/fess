/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.es.config.bsentity.dbmeta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.config.exentity.JobLog;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

/**
 * @author ESFlute (using FreeGen)
 */
public class JobLogDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

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
    //                                                                       Current DBDef
    //                                                                       =============
    @Override
    public String getProjectName() {
        return null;
    }

    @Override
    public String getProjectPrefix() {
        return null;
    }

    @Override
    public String getGenerationGapBasePrefix() {
        return null;
    }

    @Override
    public DBDef getCurrentDBDef() {
        return null;
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
        setupEpg(_epgMap, et -> ((JobLog) et).getJobName(), (et, vl) -> ((JobLog) et).setJobName(DfTypeUtil.toString(vl)), "jobName");
        setupEpg(_epgMap, et -> ((JobLog) et).getJobStatus(), (et, vl) -> ((JobLog) et).setJobStatus(DfTypeUtil.toString(vl)), "jobStatus");
        setupEpg(_epgMap, et -> ((JobLog) et).getLastUpdated(), (et, vl) -> ((JobLog) et).setLastUpdated(DfTypeUtil.toLong(vl)),
                "lastUpdated");
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

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnEndTime = cci("endTime", "endTime", null, null, Long.class, "endTime", null, false, false, false,
            "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnJobName = cci("jobName", "jobName", null, null, String.class, "jobName", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnJobStatus = cci("jobStatus", "jobStatus", null, null, String.class, "jobStatus", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLastUpdated = cci("lastUpdated", "lastUpdated", null, null, Long.class, "lastUpdated", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnScriptData = cci("scriptData", "scriptData", null, null, String.class, "scriptData", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnScriptResult = cci("scriptResult", "scriptResult", null, null, String.class, "scriptResult", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnScriptType = cci("scriptType", "scriptType", null, null, String.class, "scriptType", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnStartTime = cci("startTime", "startTime", null, null, Long.class, "startTime", null, false, false,
            false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnTarget = cci("target", "target", null, null, String.class, "target", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnEndTime() {
        return _columnEndTime;
    }

    public ColumnInfo columnJobName() {
        return _columnJobName;
    }

    public ColumnInfo columnJobStatus() {
        return _columnJobStatus;
    }

    public ColumnInfo columnLastUpdated() {
        return _columnLastUpdated;
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
        ls.add(columnJobName());
        ls.add(columnJobStatus());
        ls.add(columnLastUpdated());
        ls.add(columnScriptData());
        ls.add(columnScriptResult());
        ls.add(columnScriptType());
        ls.add(columnStartTime());
        ls.add(columnTarget());
        return ls;
    }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    @Override
    public boolean hasPrimaryKey() {
        return false;
    }

    @Override
    public boolean hasCompoundPrimaryKey() {
        return false;
    }

    @Override
    protected UniqueInfo cpui() {
        return null;
    }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    @Override
    public String getEntityTypeName() {
        return "org.codelibs.fess.es.config.exentity.JobLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.JobLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.JobLogBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return JobLog.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new JobLog();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
    }

    @Override
    public void acceptAllColumnMap(Entity entity, Map<String, ? extends Object> allColumnMap) {
    }

    @Override
    public Map<String, Object> extractPrimaryKeyMap(Entity entity) {
        return null;
    }

    @Override
    public Map<String, Object> extractAllColumnMap(Entity entity) {
        return null;
    }
}
