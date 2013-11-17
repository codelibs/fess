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

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FailureUrl) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FailureUrl) e).setId(ctl(v));
        }
    }

    public static class EpgUrl implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FailureUrl) e).getUrl();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FailureUrl) e).setUrl((String) v);
        }
    }

    public static class EpgThreadName implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FailureUrl) e).getThreadName();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FailureUrl) e).setThreadName((String) v);
        }
    }

    public static class EpgErrorName implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FailureUrl) e).getErrorName();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FailureUrl) e).setErrorName((String) v);
        }
    }

    public static class EpgErrorLog implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FailureUrl) e).getErrorLog();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FailureUrl) e).setErrorLog((String) v);
        }
    }

    public static class EpgErrorCount implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FailureUrl) e).getErrorCount();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FailureUrl) e).setErrorCount(cti(v));
        }
    }

    public static class EpgLastAccessTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FailureUrl) e).getLastAccessTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FailureUrl) e).setLastAccessTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgConfigId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FailureUrl) e).getConfigId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FailureUrl) e).setConfigId((String) v);
        }
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
            true,
            "id",
            Long.class,
            true,
            true,
            "BIGINT",
            19,
            0,
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_7B2DAF5F_D6A4_4685_9369_BB3D040D4CE8",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null, true,
            "url", String.class, false, false, "VARCHAR", 4000, 0, null, false,
            null, null, null, null, null);

    protected final ColumnInfo _columnThreadName = cci("THREAD_NAME",
            "THREAD_NAME", null, null, true, "threadName", String.class, false,
            false, "VARCHAR", 30, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnErrorName = cci("ERROR_NAME",
            "ERROR_NAME", null, null, false, "errorName", String.class, false,
            false, "VARCHAR", 255, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnErrorLog = cci("ERROR_LOG", "ERROR_LOG",
            null, null, false, "errorLog", String.class, false, false,
            "VARCHAR", 4000, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnErrorCount = cci("ERROR_COUNT",
            "ERROR_COUNT", null, null, true, "errorCount", Integer.class,
            false, false, "INTEGER", 10, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnLastAccessTime = cci("LAST_ACCESS_TIME",
            "LAST_ACCESS_TIME", null, null, true, "lastAccessTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnConfigId = cci("CONFIG_ID", "CONFIG_ID",
            null, null, false, "configId", String.class, false, false,
            "VARCHAR", 100, 0, null, false, null, null, null, null, null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    public ColumnInfo columnThreadName() {
        return _columnThreadName;
    }

    public ColumnInfo columnErrorName() {
        return _columnErrorName;
    }

    public ColumnInfo columnErrorLog() {
        return _columnErrorLog;
    }

    public ColumnInfo columnErrorCount() {
        return _columnErrorCount;
    }

    public ColumnInfo columnLastAccessTime() {
        return _columnLastAccessTime;
    }

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
    public Entity newEntity() {
        return newMyEntity();
    }

    public FailureUrl newMyEntity() {
        return new FailureUrl();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((FailureUrl) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((FailureUrl) e, m);
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
