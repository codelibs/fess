/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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
import jp.sf.fess.db.exentity.ScheduledJob;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of SCHEDULED_JOB. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
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
        setupEpg(_epgMap, new EpgName(), "name");
        setupEpg(_epgMap, new EpgTarget(), "target");
        setupEpg(_epgMap, new EpgCronExpression(), "cronExpression");
        setupEpg(_epgMap, new EpgScriptType(), "scriptType");
        setupEpg(_epgMap, new EpgScriptData(), "scriptData");
        setupEpg(_epgMap, new EpgCrawler(), "crawler");
        setupEpg(_epgMap, new EpgJobLogging(), "jobLogging");
        setupEpg(_epgMap, new EpgAvailable(), "available");
        setupEpg(_epgMap, new EpgSortOrder(), "sortOrder");
        setupEpg(_epgMap, new EpgCreatedBy(), "createdBy");
        setupEpg(_epgMap, new EpgCreatedTime(), "createdTime");
        setupEpg(_epgMap, new EpgUpdatedBy(), "updatedBy");
        setupEpg(_epgMap, new EpgUpdatedTime(), "updatedTime");
        setupEpg(_epgMap, new EpgDeletedBy(), "deletedBy");
        setupEpg(_epgMap, new EpgDeletedTime(), "deletedTime");
        setupEpg(_epgMap, new EpgVersionNo(), "versionNo");
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setId(ctl(vl));
        }
    }

    public static class EpgName implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getName();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setName((String) vl);
        }
    }

    public static class EpgTarget implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getTarget();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setTarget((String) vl);
        }
    }

    public static class EpgCronExpression implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getCronExpression();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setCronExpression((String) vl);
        }
    }

    public static class EpgScriptType implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getScriptType();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setScriptType((String) vl);
        }
    }

    public static class EpgScriptData implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getScriptData();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setScriptData((String) vl);
        }
    }

    public static class EpgCrawler implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getCrawler();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setCrawler((String) vl);
        }
    }

    public static class EpgJobLogging implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getJobLogging();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setJobLogging((String) vl);
        }
    }

    public static class EpgAvailable implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getAvailable();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setAvailable((String) vl);
        }
    }

    public static class EpgSortOrder implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getSortOrder();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setSortOrder(cti(vl));
        }
    }

    public static class EpgCreatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getCreatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setCreatedBy((String) vl);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getCreatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setCreatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgUpdatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getUpdatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setUpdatedBy((String) vl);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getUpdatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setUpdatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgDeletedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getDeletedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setDeletedBy((String) vl);
        }
    }

    public static class EpgDeletedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getDeletedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setDeletedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgVersionNo implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ScheduledJob) et).getVersionNo();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ScheduledJob) et).setVersionNo(cti(vl));
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "SCHEDULED_JOB";

    protected final String _tablePropertyName = "scheduledJob";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "SCHEDULED_JOB", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_B6A4FF05_ABF5_4436_B82A_CC1845FCFBA1",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnName = cci("NAME", "NAME", null, null,
            String.class, "name", null, false, false, true, "VARCHAR", 100, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnTarget = cci("TARGET", "TARGET", null,
            null, String.class, "target", null, false, false, true, "VARCHAR",
            100, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnCronExpression = cci("CRON_EXPRESSION",
            "CRON_EXPRESSION", null, null, String.class, "cronExpression",
            null, false, false, true, "VARCHAR", 100, 0, null, false, null,
            null, null, null, null);

    protected final ColumnInfo _columnScriptType = cci("SCRIPT_TYPE",
            "SCRIPT_TYPE", null, null, String.class, "scriptType", null, false,
            false, true, "VARCHAR", 100, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnScriptData = cci("SCRIPT_DATA",
            "SCRIPT_DATA", null, null, String.class, "scriptData", null, false,
            false, false, "VARCHAR", 4000, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnCrawler = cci("CRAWLER", "CRAWLER", null,
            null, String.class, "crawler", null, false, false, true, "VARCHAR",
            1, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnJobLogging = cci("JOB_LOGGING",
            "JOB_LOGGING", null, null, String.class, "jobLogging", null, false,
            false, true, "VARCHAR", 1, 0, null, false, null, null, null, null,
            null);

    protected final ColumnInfo _columnAvailable = cci("AVAILABLE", "AVAILABLE",
            null, null, String.class, "available", null, false, false, true,
            "VARCHAR", 1, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnSortOrder = cci("SORT_ORDER",
            "SORT_ORDER", null, null, Integer.class, "sortOrder", null, false,
            false, true, "INTEGER", 10, 0, null, false, null, null, null, null,
            null);

    protected final ColumnInfo _columnCreatedBy = cci("CREATED_BY",
            "CREATED_BY", null, null, String.class, "createdBy", null, false,
            false, true, "VARCHAR", 255, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnCreatedTime = cci("CREATED_TIME",
            "CREATED_TIME", null, null, java.sql.Timestamp.class,
            "createdTime", null, false, false, true, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnUpdatedBy = cci("UPDATED_BY",
            "UPDATED_BY", null, null, String.class, "updatedBy", null, false,
            false, false, "VARCHAR", 255, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnUpdatedTime = cci("UPDATED_TIME",
            "UPDATED_TIME", null, null, java.sql.Timestamp.class,
            "updatedTime", null, false, false, false, "TIMESTAMP", 23, 10,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnDeletedBy = cci("DELETED_BY",
            "DELETED_BY", null, null, String.class, "deletedBy", null, false,
            false, false, "VARCHAR", 255, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnDeletedTime = cci("DELETED_TIME",
            "DELETED_TIME", null, null, java.sql.Timestamp.class,
            "deletedTime", null, false, false, false, "TIMESTAMP", 23, 10,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnVersionNo = cci("VERSION_NO",
            "VERSION_NO", null, null, Integer.class, "versionNo", null, false,
            false, true, "INTEGER", 10, 0, null, false,
            OptimisticLockType.VERSION_NO, null, null, null, null);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * NAME: {NotNull, VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnName() {
        return _columnName;
    }

    /**
     * TARGET: {NotNull, VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnTarget() {
        return _columnTarget;
    }

    /**
     * CRON_EXPRESSION: {NotNull, VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCronExpression() {
        return _columnCronExpression;
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
     * CRAWLER: {NotNull, VARCHAR(1)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCrawler() {
        return _columnCrawler;
    }

    /**
     * JOB_LOGGING: {NotNull, VARCHAR(1)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnJobLogging() {
        return _columnJobLogging;
    }

    /**
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnAvailable() {
        return _columnAvailable;
    }

    /**
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSortOrder() {
        return _columnSortOrder;
    }

    /**
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    /**
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    /**
     * UPDATED_BY: {VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    /**
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    /**
     * DELETED_BY: {VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnDeletedBy() {
        return _columnDeletedBy;
    }

    /**
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnDeletedTime() {
        return _columnDeletedTime;
    }

    /**
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnVersionNo() {
        return _columnVersionNo;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnName());
        ls.add(columnTarget());
        ls.add(columnCronExpression());
        ls.add(columnScriptType());
        ls.add(columnScriptData());
        ls.add(columnCrawler());
        ls.add(columnJobLogging());
        ls.add(columnAvailable());
        ls.add(columnSortOrder());
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        ls.add(columnDeletedBy());
        ls.add(columnDeletedTime());
        ls.add(columnVersionNo());
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

    @Override
    public boolean hasVersionNo() {
        return true;
    }

    @Override
    public ColumnInfo getVersionNoColumnInfo() {
        return _columnVersionNo;
    }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    @Override
    public String getEntityTypeName() {
        return "jp.sf.fess.db.exentity.ScheduledJob";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.ScheduledJobCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.ScheduledJobBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<ScheduledJob> getEntityType() {
        return ScheduledJob.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public ScheduledJob newEntity() {
        return new ScheduledJob();
    }

    public ScheduledJob newMyEntity() {
        return new ScheduledJob();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((ScheduledJob) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((ScheduledJob) et, mp);
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
