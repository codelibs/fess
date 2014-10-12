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
import jp.sf.fess.db.exentity.SearchFieldLog;
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
 * The DB meta of SEARCH_FIELD_LOG. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class SearchFieldLogDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final SearchFieldLogDbm _instance = new SearchFieldLogDbm();

    private SearchFieldLogDbm() {
    }

    public static SearchFieldLogDbm getInstance() {
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
        setupEpg(_epgMap, new EpgName(), "name");
        setupEpg(_epgMap, new EpgValue(), "value");
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchFieldLog) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchFieldLog) et).setId(ctl(vl));
        }
    }

    public static class EpgSearchId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchFieldLog) et).getSearchId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchFieldLog) et).setSearchId(ctl(vl));
        }
    }

    public static class EpgName implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchFieldLog) et).getName();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchFieldLog) et).setName((String) vl);
        }
    }

    public static class EpgValue implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchFieldLog) et).getValue();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchFieldLog) et).setValue((String) vl);
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
            return ((SearchFieldLog) et).getSearchLog();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchFieldLog) et).setSearchLog((SearchLog) vl);
        }
    }

    @Override
    public PropertyGateway findForeignPropertyGateway(final String prop) {
        return doFindEfpg(_efpgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "SEARCH_FIELD_LOG";

    protected final String _tablePropertyName = "searchFieldLog";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "SEARCH_FIELD_LOG", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_9073D7B3_DAA5_4428_9A25_73DFA7973FA0",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnSearchId = cci("SEARCH_ID", "SEARCH_ID",
            null, null, Long.class, "searchId", null, false, false, true,
            "BIGINT", 19, 0, null, false, null, null, "searchLog", null, null);

    protected final ColumnInfo _columnName = cci("NAME", "NAME", null, null,
            String.class, "name", null, false, false, true, "VARCHAR", 255, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnValue = cci("VALUE", "VALUE", null, null,
            String.class, "value", null, false, false, true, "VARCHAR", 1000,
            0, null, false, null, null, null, null, null);

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
     * NAME: {IX, NotNull, VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnName() {
        return _columnName;
    }

    /**
     * VALUE: {NotNull, VARCHAR(1000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnValue() {
        return _columnValue;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnSearchId());
        ls.add(columnName());
        ls.add(columnValue());
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
        return cfi("CONSTRAINT_96", "searchLog", this,
                SearchLogDbm.getInstance(), mp, 0, null, false, false, false,
                false, null, null, false, "searchFieldLogList");
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
        return "jp.sf.fess.db.exentity.SearchFieldLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.SearchFieldLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.SearchFieldLogBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<SearchFieldLog> getEntityType() {
        return SearchFieldLog.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public SearchFieldLog newEntity() {
        return new SearchFieldLog();
    }

    public SearchFieldLog newMyEntity() {
        return new SearchFieldLog();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((SearchFieldLog) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((SearchFieldLog) et, mp);
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
