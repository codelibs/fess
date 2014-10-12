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
import jp.sf.fess.db.exentity.UserInfo;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of USER_INFO. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class UserInfoDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final UserInfoDbm _instance = new UserInfoDbm();

    private UserInfoDbm() {
    }

    public static UserInfoDbm getInstance() {
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
        setupEpg(_epgMap, new EpgCode(), "code");
        setupEpg(_epgMap, new EpgCreatedTime(), "createdTime");
        setupEpg(_epgMap, new EpgUpdatedTime(), "updatedTime");
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((UserInfo) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((UserInfo) et).setId(ctl(vl));
        }
    }

    public static class EpgCode implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((UserInfo) et).getCode();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((UserInfo) et).setCode((String) vl);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((UserInfo) et).getCreatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((UserInfo) et).setCreatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((UserInfo) et).getUpdatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((UserInfo) et).setUpdatedTime((java.sql.Timestamp) vl);
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "USER_INFO";

    protected final String _tablePropertyName = "userInfo";

    protected final TableSqlName _tableSqlName = new TableSqlName("USER_INFO",
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_73943943_5D67_4C52_BD23_8FAE169D4F5E",
            false, null, null, null, "favoriteLogList,searchLogList", null);

    protected final ColumnInfo _columnCode = cci("CODE", "CODE", null, null,
            String.class, "code", null, false, false, true, "VARCHAR", 1000, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnCreatedTime = cci("CREATED_TIME",
            "CREATED_TIME", null, null, java.sql.Timestamp.class,
            "createdTime", null, false, false, true, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnUpdatedTime = cci("UPDATED_TIME",
            "UPDATED_TIME", null, null, java.sql.Timestamp.class,
            "updatedTime", null, false, false, true, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * CODE: {NotNull, VARCHAR(1000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCode() {
        return _columnCode;
    }

    /**
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    /**
     * UPDATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnCode());
        ls.add(columnCreatedTime());
        ls.add(columnUpdatedTime());
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
    /**
     * FAVORITE_LOG by USER_ID, named 'favoriteLogList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerFavoriteLogList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                FavoriteLogDbm.getInstance().columnUserId());
        return cri("CONSTRAINT_A98", "favoriteLogList", this,
                FavoriteLogDbm.getInstance(), mp, false, "userInfo");
    }

    /**
     * SEARCH_LOG by USER_ID, named 'searchLogList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerSearchLogList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                SearchLogDbm.getInstance().columnUserId());
        return cri("CONSTRAINT_F2A", "searchLogList", this,
                SearchLogDbm.getInstance(), mp, false, "userInfo");
    }

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
        return "jp.sf.fess.db.exentity.UserInfo";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.UserInfoCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.UserInfoBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<UserInfo> getEntityType() {
        return UserInfo.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public UserInfo newEntity() {
        return new UserInfo();
    }

    public UserInfo newMyEntity() {
        return new UserInfo();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((UserInfo) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((UserInfo) et, mp);
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
