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
import jp.sf.fess.db.exentity.FailureUrl;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of FAILURE_URL. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class FailureUrlDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final FailureUrlDbm _instance = new FailureUrlDbm();

    private FailureUrlDbm() {
    }

    public static FailureUrlDbm getInstance() {
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
        setupEpg(_epgMap, new EpgUrl(), "url");
        setupEpg(_epgMap, new EpgThreadName(), "threadName");
        setupEpg(_epgMap, new EpgErrorName(), "errorName");
        setupEpg(_epgMap, new EpgErrorLog(), "errorLog");
        setupEpg(_epgMap, new EpgErrorCount(), "errorCount");
        setupEpg(_epgMap, new EpgLastAccessTime(), "lastAccessTime");
        setupEpg(_epgMap, new EpgConfigId(), "configId");
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FailureUrl) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FailureUrl) et).setId(ctl(vl));
        }
    }

    public static class EpgUrl implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FailureUrl) et).getUrl();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FailureUrl) et).setUrl((String) vl);
        }
    }

    public static class EpgThreadName implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FailureUrl) et).getThreadName();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FailureUrl) et).setThreadName((String) vl);
        }
    }

    public static class EpgErrorName implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FailureUrl) et).getErrorName();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FailureUrl) et).setErrorName((String) vl);
        }
    }

    public static class EpgErrorLog implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FailureUrl) et).getErrorLog();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FailureUrl) et).setErrorLog((String) vl);
        }
    }

    public static class EpgErrorCount implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FailureUrl) et).getErrorCount();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FailureUrl) et).setErrorCount(cti(vl));
        }
    }

    public static class EpgLastAccessTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FailureUrl) et).getLastAccessTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FailureUrl) et).setLastAccessTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgConfigId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FailureUrl) et).getConfigId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FailureUrl) et).setConfigId((String) vl);
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "FAILURE_URL";

    protected final String _tablePropertyName = "failureUrl";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "FAILURE_URL", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_275DAA65_E8B1_48D7_AC90_CAC87877E583",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null,
            String.class, "url", null, false, false, true, "VARCHAR", 4000, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnThreadName = cci("THREAD_NAME",
            "THREAD_NAME", null, null, String.class, "threadName", null, false,
            false, true, "VARCHAR", 30, 0, null, false, null, null, null, null,
            null);

    protected final ColumnInfo _columnErrorName = cci("ERROR_NAME",
            "ERROR_NAME", null, null, String.class, "errorName", null, false,
            false, false, "VARCHAR", 255, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnErrorLog = cci("ERROR_LOG", "ERROR_LOG",
            null, null, String.class, "errorLog", null, false, false, false,
            "VARCHAR", 4000, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnErrorCount = cci("ERROR_COUNT",
            "ERROR_COUNT", null, null, Integer.class, "errorCount", null,
            false, false, true, "INTEGER", 10, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnLastAccessTime = cci("LAST_ACCESS_TIME",
            "LAST_ACCESS_TIME", null, null, java.sql.Timestamp.class,
            "lastAccessTime", null, false, false, true, "TIMESTAMP", 23, 10,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnConfigId = cci("CONFIG_ID", "CONFIG_ID",
            null, null, String.class, "configId", null, false, false, false,
            "VARCHAR", 100, 0, null, false, null, null, null, null, null);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * URL: {IX+, NotNull, VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    /**
     * THREAD_NAME: {NotNull, VARCHAR(30)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnThreadName() {
        return _columnThreadName;
    }

    /**
     * ERROR_NAME: {VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnErrorName() {
        return _columnErrorName;
    }

    /**
     * ERROR_LOG: {VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnErrorLog() {
        return _columnErrorLog;
    }

    /**
     * ERROR_COUNT: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnErrorCount() {
        return _columnErrorCount;
    }

    /**
     * LAST_ACCESS_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnLastAccessTime() {
        return _columnLastAccessTime;
    }

    /**
     * CONFIG_ID: {IX, VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnConfigId() {
        return _columnConfigId;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnUrl());
        ls.add(columnThreadName());
        ls.add(columnErrorName());
        ls.add(columnErrorLog());
        ls.add(columnErrorCount());
        ls.add(columnLastAccessTime());
        ls.add(columnConfigId());
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
        return "jp.sf.fess.db.exentity.FailureUrl";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.FailureUrlCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.FailureUrlBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<FailureUrl> getEntityType() {
        return FailureUrl.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public FailureUrl newEntity() {
        return new FailureUrl();
    }

    public FailureUrl newMyEntity() {
        return new FailureUrl();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((FailureUrl) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((FailureUrl) et, mp);
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
