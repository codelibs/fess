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
import jp.sf.fess.db.exentity.FavoriteLog;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of FAVORITE_LOG. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class FavoriteLogDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final FavoriteLogDbm _instance = new FavoriteLogDbm();

    private FavoriteLogDbm() {
    }

    public static FavoriteLogDbm getInstance() {
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
        setupEpg(_epgMap, new EpgUserId(), "userId");
        setupEpg(_epgMap, new EpgUrl(), "url");
        setupEpg(_epgMap, new EpgCreatedTime(), "createdTime");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FavoriteLog) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FavoriteLog) e).setId(ctl(v));
        }
    }

    public static class EpgUserId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FavoriteLog) e).getUserId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FavoriteLog) e).setUserId(ctl(v));
        }
    }

    public static class EpgUrl implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FavoriteLog) e).getUrl();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FavoriteLog) e).setUrl((String) v);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FavoriteLog) e).getCreatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FavoriteLog) e).setCreatedTime((java.sql.Timestamp) v);
        }
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "FAVORITE_LOG";

    protected final String _tablePropertyName = "favoriteLog";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "FAVORITE_LOG", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_FBA5677B_CE15_44E2_AE7C_6F1081DEA432",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnUserId = cci("USER_ID", "USER_ID", null,
            null, true, "userId", Long.class, false, false, "BIGINT", 19, 0,
            null, false, null, null, "userInfo", null, null);

    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null, true,
            "url", String.class, false, false, "VARCHAR", 4000, 0, null, false,
            null, null, null, null, null);

    protected final ColumnInfo _columnCreatedTime = cci("CREATED_TIME",
            "CREATED_TIME", null, null, true, "createdTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnUserId() {
        return _columnUserId;
    }

    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnUserId());
        ls.add(columnUrl());
        ls.add(columnCreatedTime());
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
    public ForeignInfo foreignUserInfo() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(
                columnUserId(), UserInfoDbm.getInstance().columnId());
        return cfi("CONSTRAINT_A98", "userInfo", this,
                UserInfoDbm.getInstance(), map, 0, false, false, false, false,
                null, null, false, "favoriteLogList");
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
        return "jp.sf.fess.db.exentity.FavoriteLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.FavoriteLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.FavoriteLogBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<FavoriteLog> getEntityType() {
        return FavoriteLog.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public FavoriteLog newMyEntity() {
        return new FavoriteLog();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((FavoriteLog) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((FavoriteLog) e, m);
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
