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

import jp.sf.fess.db.allcommon.CDef;
import jp.sf.fess.db.allcommon.DBCurrent;
import jp.sf.fess.db.allcommon.DBFluteConfig;
import jp.sf.fess.db.exentity.SearchLog;
import jp.sf.fess.db.exentity.UserInfo;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of SEARCH_LOG. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class SearchLogDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final SearchLogDbm _instance = new SearchLogDbm();

    private SearchLogDbm() {
    }

    public static SearchLogDbm getInstance() {
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
        setupEpg(_epgMap, new EpgSearchWord(), "searchWord");
        setupEpg(_epgMap, new EpgRequestedTime(), "requestedTime");
        setupEpg(_epgMap, new EpgResponseTime(), "responseTime");
        setupEpg(_epgMap, new EpgHitCount(), "hitCount");
        setupEpg(_epgMap, new EpgQueryOffset(), "queryOffset");
        setupEpg(_epgMap, new EpgQueryPageSize(), "queryPageSize");
        setupEpg(_epgMap, new EpgUserAgent(), "userAgent");
        setupEpg(_epgMap, new EpgReferer(), "referer");
        setupEpg(_epgMap, new EpgClientIp(), "clientIp");
        setupEpg(_epgMap, new EpgUserSessionId(), "userSessionId");
        setupEpg(_epgMap, new EpgAccessType(), "accessType");
        setupEpg(_epgMap, new EpgUserId(), "userId");
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setId(ctl(vl));
        }
    }

    public static class EpgSearchWord implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getSearchWord();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setSearchWord((String) vl);
        }
    }

    public static class EpgRequestedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getRequestedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setRequestedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgResponseTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getResponseTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setResponseTime(cti(vl));
        }
    }

    public static class EpgHitCount implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getHitCount();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setHitCount(ctl(vl));
        }
    }

    public static class EpgQueryOffset implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getQueryOffset();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setQueryOffset(cti(vl));
        }
    }

    public static class EpgQueryPageSize implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getQueryPageSize();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setQueryPageSize(cti(vl));
        }
    }

    public static class EpgUserAgent implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getUserAgent();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setUserAgent((String) vl);
        }
    }

    public static class EpgReferer implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getReferer();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setReferer((String) vl);
        }
    }

    public static class EpgClientIp implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getClientIp();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setClientIp((String) vl);
        }
    }

    public static class EpgUserSessionId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getUserSessionId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setUserSessionId((String) vl);
        }
    }

    public static class EpgAccessType implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getAccessType();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setAccessType((String) vl);
        }
    }

    public static class EpgUserId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getUserId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setUserId(ctl(vl));
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
        setupEfpg(_efpgMap, new EfpgUserInfo(), "userInfo");
    }

    public class EfpgUserInfo implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SearchLog) et).getUserInfo();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SearchLog) et).setUserInfo((UserInfo) vl);
        }
    }

    @Override
    public PropertyGateway findForeignPropertyGateway(final String prop) {
        return doFindEfpg(_efpgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "SEARCH_LOG";

    protected final String _tablePropertyName = "searchLog";

    protected final TableSqlName _tableSqlName = new TableSqlName("SEARCH_LOG",
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_8609B14F_3D70_4BDE_8BE0_9559075768F5",
            false, null, null, null, "clickLogList,searchFieldLogList", null);

    protected final ColumnInfo _columnSearchWord = cci("SEARCH_WORD",
            "SEARCH_WORD", null, null, String.class, "searchWord", null, false,
            false, false, "VARCHAR", 1000, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnRequestedTime = cci("REQUESTED_TIME",
            "REQUESTED_TIME", null, null, java.sql.Timestamp.class,
            "requestedTime", null, false, false, true, "TIMESTAMP", 23, 10,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnResponseTime = cci("RESPONSE_TIME",
            "RESPONSE_TIME", null, null, Integer.class, "responseTime", null,
            false, false, true, "INTEGER", 10, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnHitCount = cci("HIT_COUNT", "HIT_COUNT",
            null, null, Long.class, "hitCount", null, false, false, true,
            "BIGINT", 19, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnQueryOffset = cci("QUERY_OFFSET",
            "QUERY_OFFSET", null, null, Integer.class, "queryOffset", null,
            false, false, true, "INTEGER", 10, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnQueryPageSize = cci("QUERY_PAGE_SIZE",
            "QUERY_PAGE_SIZE", null, null, Integer.class, "queryPageSize",
            null, false, false, true, "INTEGER", 10, 0, null, false, null,
            null, null, null, null);

    protected final ColumnInfo _columnUserAgent = cci("USER_AGENT",
            "USER_AGENT", null, null, String.class, "userAgent", null, false,
            false, false, "VARCHAR", 255, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnReferer = cci("REFERER", "REFERER", null,
            null, String.class, "referer", null, false, false, false,
            "VARCHAR", 1000, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnClientIp = cci("CLIENT_IP", "CLIENT_IP",
            null, null, String.class, "clientIp", null, false, false, false,
            "VARCHAR", 50, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnUserSessionId = cci("USER_SESSION_ID",
            "USER_SESSION_ID", null, null, String.class, "userSessionId", null,
            false, false, false, "VARCHAR", 100, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnAccessType = cci("ACCESS_TYPE",
            "ACCESS_TYPE", null, null, String.class, "accessType", null, false,
            false, true, "VARCHAR", 1, 0, null, false, null, null, null, null,
            CDef.DefMeta.AccessType);

    protected final ColumnInfo _columnUserId = cci("USER_ID", "USER_ID", null,
            null, Long.class, "userId", null, false, false, false, "BIGINT",
            19, 0, null, false, null, null, "userInfo", null, null);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * SEARCH_WORD: {IX, VARCHAR(1000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSearchWord() {
        return _columnSearchWord;
    }

    /**
     * REQUESTED_TIME: {IX+, NotNull, TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnRequestedTime() {
        return _columnRequestedTime;
    }

    /**
     * RESPONSE_TIME: {IX, NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnResponseTime() {
        return _columnResponseTime;
    }

    /**
     * HIT_COUNT: {IX, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnHitCount() {
        return _columnHitCount;
    }

    /**
     * QUERY_OFFSET: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnQueryOffset() {
        return _columnQueryOffset;
    }

    /**
     * QUERY_PAGE_SIZE: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnQueryPageSize() {
        return _columnQueryPageSize;
    }

    /**
     * USER_AGENT: {VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUserAgent() {
        return _columnUserAgent;
    }

    /**
     * REFERER: {VARCHAR(1000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnReferer() {
        return _columnReferer;
    }

    /**
     * CLIENT_IP: {VARCHAR(50)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnClientIp() {
        return _columnClientIp;
    }

    /**
     * USER_SESSION_ID: {VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUserSessionId() {
        return _columnUserSessionId;
    }

    /**
     * ACCESS_TYPE: {NotNull, VARCHAR(1), classification=AccessType}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnAccessType() {
        return _columnAccessType;
    }

    /**
     * USER_ID: {IX, BIGINT(19), FK to USER_INFO}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUserId() {
        return _columnUserId;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnSearchWord());
        ls.add(columnRequestedTime());
        ls.add(columnResponseTime());
        ls.add(columnHitCount());
        ls.add(columnQueryOffset());
        ls.add(columnQueryPageSize());
        ls.add(columnUserAgent());
        ls.add(columnReferer());
        ls.add(columnClientIp());
        ls.add(columnUserSessionId());
        ls.add(columnAccessType());
        ls.add(columnUserId());
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
     * USER_INFO by my USER_ID, named 'userInfo'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignUserInfo() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnUserId(),
                UserInfoDbm.getInstance().columnId());
        return cfi("CONSTRAINT_F2A", "userInfo", this,
                UserInfoDbm.getInstance(), mp, 0, null, false, false, false,
                false, null, null, false, "searchLogList");
    }

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------
    /**
     * CLICK_LOG by SEARCH_ID, named 'clickLogList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerClickLogList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                ClickLogDbm.getInstance().columnSearchId());
        return cri("CONSTRAINT_310", "clickLogList", this,
                ClickLogDbm.getInstance(), mp, false, "searchLog");
    }

    /**
     * SEARCH_FIELD_LOG by SEARCH_ID, named 'searchFieldLogList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerSearchFieldLogList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                SearchFieldLogDbm.getInstance().columnSearchId());
        return cri("CONSTRAINT_96", "searchFieldLogList", this,
                SearchFieldLogDbm.getInstance(), mp, false, "searchLog");
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
        return "jp.sf.fess.db.exentity.SearchLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.SearchLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.SearchLogBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<SearchLog> getEntityType() {
        return SearchLog.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public SearchLog newEntity() {
        return new SearchLog();
    }

    public SearchLog newMyEntity() {
        return new SearchLog();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((SearchLog) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((SearchLog) et, mp);
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
