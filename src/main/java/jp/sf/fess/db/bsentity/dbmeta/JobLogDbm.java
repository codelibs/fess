/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
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

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((JobLog) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((JobLog) et).setId(ctl(vl));
        }
    }

    public static class EpgJobName implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((JobLog) et).getJobName();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((JobLog) et).setJobName((String) vl);
        }
    }

    public static class EpgJobStatus implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((JobLog) et).getJobStatus();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((JobLog) et).setJobStatus((String) vl);
        }
    }

    public static class EpgTarget implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((JobLog) et).getTarget();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((JobLog) et).setTarget((String) vl);
        }
    }

    public static class EpgScriptType implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((JobLog) et).getScriptType();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((JobLog) et).setScriptType((String) vl);
        }
    }

    public static class EpgScriptData implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((JobLog) et).getScriptData();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((JobLog) et).setScriptData((String) vl);
        }
    }

    public static class EpgScriptResult implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((JobLog) et).getScriptResult();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((JobLog) et).setScriptResult((String) vl);
        }
    }

    public static class EpgStartTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((JobLog) et).getStartTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((JobLog) et).setStartTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgEndTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((JobLog) et).getEndTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((JobLog) et).setEndTime((java.sql.Timestamp) vl);
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
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
            Long.class,
            "id",
            null,
            true,
            true,
            true,
            "BIGINT",
            19,
            0,
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_18428598_57ED_471A_8B9C_911274CEE79E",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnJobName = cci("JOB_NAME", "JOB_NAME",
            null, null, String.class, "jobName", null, false, false, true,
            "VARCHAR", 100, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnJobStatus = cci("JOB_STATUS",
            "JOB_STATUS", null, null, String.class, "jobStatus", null, false,
            false, true, "VARCHAR", 10, 0, null, false, null, null, null, null,
            null);

    protected final ColumnInfo _columnTarget = cci("TARGET", "TARGET", null,
            null, String.class, "target", null, false, false, true, "VARCHAR",
            100, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnScriptType = cci("SCRIPT_TYPE",
            "SCRIPT_TYPE", null, null, String.class, "scriptType", null, false,
            false, true, "VARCHAR", 100, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnScriptData = cci("SCRIPT_DATA",
            "SCRIPT_DATA", null, null, String.class, "scriptData", null, false,
            false, false, "VARCHAR", 4000, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnScriptResult = cci("SCRIPT_RESULT",
            "SCRIPT_RESULT", null, null, String.class, "scriptResult", null,
            false, false, false, "VARCHAR", 4000, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnStartTime = cci("START_TIME",
            "START_TIME", null, null, java.sql.Timestamp.class, "startTime",
            null, false, false, true, "TIMESTAMP", 23, 10, null, false, null,
            null, null, null, null);

    protected final ColumnInfo _columnEndTime = cci("END_TIME", "END_TIME",
            null, null, java.sql.Timestamp.class, "endTime", null, false,
            false, false, "TIMESTAMP", 23, 10, null, false, null, null, null,
            null, null);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * JOB_NAME: {NotNull, VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnJobName() {
        return _columnJobName;
    }

    /**
     * JOB_STATUS: {NotNull, VARCHAR(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnJobStatus() {
        return _columnJobStatus;
    }

    /**
     * TARGET: {NotNull, VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnTarget() {
        return _columnTarget;
    }

    /**
     * SCRIPT_TYPE: {NotNull, VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnScriptType() {
        return _columnScriptType;
    }

    /**
     * SCRIPT_DATA: {VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnScriptData() {
        return _columnScriptData;
    }

    /**
     * SCRIPT_RESULT: {VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnScriptResult() {
        return _columnScriptResult;
    }

    /**
     * START_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnStartTime() {
        return _columnStartTime;
    }

    /**
     * END_TIME: {TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
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
    // cannot cache because it uses related DB meta instance while booting
    // (instead, cached by super's collection)
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
    public JobLog newEntity() {
        return new JobLog();
    }

    public JobLog newMyEntity() {
        return new JobLog();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((JobLog) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((JobLog) et, mp);
    }

    @Override
    public Map<String, Object> extractPrimaryKeyMap(final Entity et) {
        return doExtractPrimaryKeyMap(et);
    }

    @Override
    public Map<String, Object> extractAllColumnMap(final Entity et) {
        return doExtractAllColumnMap(et);
    }
}
