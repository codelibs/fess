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
import jp.sf.fess.db.exentity.ClickLog;
import jp.sf.fess.db.exentity.SearchLog;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of CLICK_LOG. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class ClickLogDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final ClickLogDbm _instance = new ClickLogDbm();

    private ClickLogDbm() {
    }

    public static ClickLogDbm getInstance() {
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
        setupEpg(_epgMap, new EpgSearchId(), "searchId");
        setupEpg(_epgMap, new EpgUrl(), "url");
        setupEpg(_epgMap, new EpgRequestedTime(), "requestedTime");
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ClickLog) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ClickLog) et).setId(ctl(vl));
        }
    }

    public static class EpgSearchId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ClickLog) et).getSearchId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ClickLog) et).setSearchId(ctl(vl));
        }
    }

    public static class EpgUrl implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ClickLog) et).getUrl();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ClickLog) et).setUrl((String) vl);
        }
    }

    public static class EpgRequestedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ClickLog) et).getRequestedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ClickLog) et).setRequestedTime((java.sql.Timestamp) vl);
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    protected final Map<String, PropertyGateway> _efpgMap = newHashMap();
    {
        setupEfpg(_efpgMap, new EfpgSearchLog(), "searchLog");
    }

    public class EfpgSearchLog implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((ClickLog) et).getSearchLog();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((ClickLog) et).setSearchLog((SearchLog) vl);
        }
    }

    @Override
    public PropertyGateway findForeignPropertyGateway(final String prop) {
        return doFindEfpg(_efpgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "CLICK_LOG";

    protected final String _tablePropertyName = "clickLog";

    protected final TableSqlName _tableSqlName = new TableSqlName("CLICK_LOG",
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_1C4461A0_8D02_4547_BE0E_3178BBF09289",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnSearchId = cci("SEARCH_ID", "SEARCH_ID",
            null, null, Long.class, "searchId", null, false, false, true,
            "BIGINT", 19, 0, null, false, null, null, "searchLog", null, null);

    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null,
            String.class, "url", null, false, false, true, "VARCHAR", 4000, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnRequestedTime = cci("REQUESTED_TIME",
            "REQUESTED_TIME", null, null, java.sql.Timestamp.class,
            "requestedTime", null, false, false, true, "TIMESTAMP", 23, 10,
            null, false, null, null, null, null, null);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * SEARCH_ID: {IX, NotNull, BIGINT(19), FK to SEARCH_LOG}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSearchId() {
        return _columnSearchId;
    }

    /**
     * URL: {IX, NotNull, VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    /**
     * REQUESTED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnRequestedTime() {
        return _columnRequestedTime;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnSearchId());
        ls.add(columnUrl());
        ls.add(columnRequestedTime());
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
    /**
     * SEARCH_LOG by my SEARCH_ID, named 'searchLog'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignSearchLog() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(
                columnSearchId(), SearchLogDbm.getInstance().columnId());
        return cfi("CONSTRAINT_310", "searchLog", this,
                SearchLogDbm.getInstance(), mp, 0, null, false, false, false,
                false, null, null, false, "clickLogList");
    }

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
        return "jp.sf.fess.db.exentity.ClickLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.ClickLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.ClickLogBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<ClickLog> getEntityType() {
        return ClickLog.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public ClickLog newEntity() {
        return new ClickLog();
    }

    public ClickLog newMyEntity() {
        return new ClickLog();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((ClickLog) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((ClickLog) et, mp);
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
