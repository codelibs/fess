/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.db.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import jp.sf.fess.db.allcommon.DBCurrent;
import jp.sf.fess.db.allcommon.DBFluteConfig;
import jp.sf.fess.db.exentity.JobLog;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of JOB_LOG. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
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
    //                                                                       Current DBDef
    //                                                                       =============
    @Override
    public DBDef getCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    // ===================================================================================
    //                                                                    Property Gateway
    //                                                                    ================
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    {
        setupEpg(_epgMap, new EpgId(), "id");
        setupEpg(_epgMap, new EpgJobName(), "jobName");
        setupEpg(_epgMap, new EpgJobStatus(), "jobStatus");
        setupEpg(_epgMap, new EpgTarget(), "target");
        setupEpg(_epgMap, new EpgScriptType(), "scriptType");
        setupEpg(_epgMap, new EpgScriptData(), "scriptData");
        setupEpg(_epgMap, new EpgScriptResult(), "scriptResult");
        setupEpg(_epgMap, new EpgStartTime(), "startTime");
        setupEpg(_epgMap, new EpgEndTime(), "endTime");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((JobLog) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((JobLog) e).setId(ctl(v));
        }
    }

    public static class EpgJobName implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((JobLog) e).getJobName();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((JobLog) e).setJobName((String) v);
        }
    }

    public static class EpgJobStatus implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((JobLog) e).getJobStatus();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((JobLog) e).setJobStatus((String) v);
        }
    }

    public static class EpgTarget implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((JobLog) e).getTarget();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((JobLog) e).setTarget((String) v);
        }
    }

    public static class EpgScriptType implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((JobLog) e).getScriptType();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((JobLog) e).setScriptType((String) v);
        }
    }

    public static class EpgScriptData implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((JobLog) e).getScriptData();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((JobLog) e).setScriptData((String) v);
        }
    }

    public static class EpgScriptResult implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((JobLog) e).getScriptResult();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((JobLog) e).setScriptResult((String) v);
        }
    }

    public static class EpgStartTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((JobLog) e).getStartTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((JobLog) e).setStartTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgEndTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((JobLog) e).getEndTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((JobLog) e).setEndTime((java.sql.Timestamp) v);
        }
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "JOB_LOG";

    protected final String _tablePropertyName = "jobLog";

    protected final TableSqlName _tableSqlName = new TableSqlName("JOB_LOG",
            _tableDbName);
    {
        _tableSqlName.xacceptFilter(DBFluteConfig.getInstance()
                .getTableSqlNameFilter());
    }

    @Override
    public String getTableDbName() {
        return _tableDbName;
    }

    @Override
    public String getTablePropertyName() {
        return _tablePropertyName;
    }

    @Override
    public TableSqlName getTableSqlName() {
        return _tableSqlName;
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci(
            "ID",
            "ID",
            null,
            null,
            true,
            "id",
            Long.class,
            true,
            true,
            "BIGINT",
            19,
            0,
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_A597AD31_D6B7_44D4_8A2C_D6B301347DAF",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnJobName = cci("JOB_NAME", "JOB_NAME",
            null, null, true, "jobName", String.class, false, false, "VARCHAR",
            100, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnJobStatus = cci("JOB_STATUS",
            "JOB_STATUS", null, null, true, "jobStatus", String.class, false,
            false, "VARCHAR", 10, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnTarget = cci("TARGET", "TARGET", null,
            null, true, "target", String.class, false, false, "VARCHAR", 100,
            0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnScriptType = cci("SCRIPT_TYPE",
            "SCRIPT_TYPE", null, null, true, "scriptType", String.class, false,
            false, "VARCHAR", 100, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnScriptData = cci("SCRIPT_DATA",
            "SCRIPT_DATA", null, null, false, "scriptData", String.class,
            false, false, "VARCHAR", 4000, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnScriptResult = cci("SCRIPT_RESULT",
            "SCRIPT_RESULT", null, null, false, "scriptResult", String.class,
            false, false, "VARCHAR", 4000, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnStartTime = cci("START_TIME",
            "START_TIME", null, null, true, "startTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnEndTime = cci("END_TIME", "END_TIME",
            null, null, true, "endTime", java.sql.Timestamp.class, false,
            false, "TIMESTAMP", 23, 10, null, false, null, null, null, null,
            null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnJobName() {
        return _columnJobName;
    }

    public ColumnInfo columnJobStatus() {
        return _columnJobStatus;
    }

    public ColumnInfo columnTarget() {
        return _columnTarget;
    }

    public ColumnInfo columnScriptType() {
        return _columnScriptType;
    }

    public ColumnInfo columnScriptData() {
        return _columnScriptData;
    }

    public ColumnInfo columnScriptResult() {
        return _columnScriptResult;
    }

    public ColumnInfo columnStartTime() {
        return _columnStartTime;
    }

    public ColumnInfo columnEndTime() {
        return _columnEndTime;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnJobName());
        ls.add(columnJobStatus());
        ls.add(columnTarget());
        ls.add(columnScriptType());
        ls.add(columnScriptData());
        ls.add(columnScriptResult());
        ls.add(columnStartTime());
        ls.add(columnEndTime());
        return ls;
    }

    {
        initializeInformationResource();
    }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    @Override
    protected UniqueInfo cpui() {
        return hpcpui(columnId());
    }

    @Override
    public boolean hasPrimaryKey() {
        return true;
    }

    @Override
    public boolean hasCompoundPrimaryKey() {
        return false;
    }

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============
    @Override
    public boolean hasIdentity() {
        return true;
    }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    @Override
    public String getEntityTypeName() {
        return "jp.sf.fess.db.exentity.JobLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.JobLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.JobLogBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<JobLog> getEntityType() {
        return JobLog.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public JobLog newMyEntity() {
        return new JobLog();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((JobLog) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((JobLog) e, m);
    }

    @Override
    public Map<String, Object> extractPrimaryKeyMap(final Entity e) {
        return doExtractPrimaryKeyMap(e);
    }

    @Override
    public Map<String, Object> extractAllColumnMap(final Entity e) {
        return doExtractAllColumnMap(e);
    }
}
