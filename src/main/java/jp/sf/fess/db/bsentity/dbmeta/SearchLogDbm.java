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

import jp.sf.fess.db.allcommon.CDef;
import jp.sf.fess.db.allcommon.DBCurrent;
import jp.sf.fess.db.allcommon.DBFluteConfig;
import jp.sf.fess.db.exentity.SearchLog;

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

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setId(ctl(v));
        }
    }

    public static class EpgSearchWord implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getSearchWord();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setSearchWord((String) v);
        }
    }

    public static class EpgRequestedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getRequestedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setRequestedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgResponseTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getResponseTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setResponseTime(cti(v));
        }
    }

    public static class EpgHitCount implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getHitCount();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setHitCount(ctl(v));
        }
    }

    public static class EpgQueryOffset implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getQueryOffset();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setQueryOffset(cti(v));
        }
    }

    public static class EpgQueryPageSize implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getQueryPageSize();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setQueryPageSize(cti(v));
        }
    }

    public static class EpgUserAgent implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getUserAgent();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setUserAgent((String) v);
        }
    }

    public static class EpgReferer implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getReferer();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setReferer((String) v);
        }
    }

    public static class EpgClientIp implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getClientIp();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setClientIp((String) v);
        }
    }

    public static class EpgUserSessionId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getUserSessionId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setUserSessionId((String) v);
        }
    }

    public static class EpgAccessType implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getAccessType();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setAccessType((String) v);
        }
    }

    public static class EpgUserId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((SearchLog) e).getUserId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((SearchLog) e).setUserId(ctl(v));
        }
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
            true,
            "id",
            Long.class,
            true,
            true,
            "BIGINT",
            19,
            0,
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_380A14A7_E02A_40AA_B0B1_825DFB0AB844",
            false, null, null, null, "clickLogList,searchFieldLogList", null);

    protected final ColumnInfo _columnSearchWord = cci("SEARCH_WORD",
            "SEARCH_WORD", null, null, false, "searchWord", String.class,
            false, false, "VARCHAR", 1000, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnRequestedTime = cci("REQUESTED_TIME",
            "REQUESTED_TIME", null, null, true, "requestedTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnResponseTime = cci("RESPONSE_TIME",
            "RESPONSE_TIME", null, null, true, "responseTime", Integer.class,
            false, false, "INTEGER", 10, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnHitCount = cci("HIT_COUNT", "HIT_COUNT",
            null, null, true, "hitCount", Long.class, false, false, "BIGINT",
            19, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnQueryOffset = cci("QUERY_OFFSET",
            "QUERY_OFFSET", null, null, true, "queryOffset", Integer.class,
            false, false, "INTEGER", 10, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnQueryPageSize = cci("QUERY_PAGE_SIZE",
            "QUERY_PAGE_SIZE", null, null, true, "queryPageSize",
            Integer.class, false, false, "INTEGER", 10, 0, null, false, null,
            null, null, null, null);

    protected final ColumnInfo _columnUserAgent = cci("USER_AGENT",
            "USER_AGENT", null, null, false, "userAgent", String.class, false,
            false, "VARCHAR", 255, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnReferer = cci("REFERER", "REFERER", null,
            null, false, "referer", String.class, false, false, "VARCHAR",
            1000, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnClientIp = cci("CLIENT_IP", "CLIENT_IP",
            null, null, false, "clientIp", String.class, false, false,
            "VARCHAR", 50, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnUserSessionId = cci("USER_SESSION_ID",
            "USER_SESSION_ID", null, null, false, "userSessionId",
            String.class, false, false, "VARCHAR", 100, 0, null, false, null,
            null, null, null, null);

    protected final ColumnInfo _columnAccessType = cci("ACCESS_TYPE",
            "ACCESS_TYPE", null, null, true, "accessType", String.class, false,
            false, "VARCHAR", 1, 0, null, false, null, null, null, null,
            CDef.DefMeta.AccessType);

    protected final ColumnInfo _columnUserId = cci("USER_ID", "USER_ID", null,
            null, false, "userId", Long.class, false, false, "BIGINT", 19, 0,
            null, false, null, null, "userInfo", null, null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnSearchWord() {
        return _columnSearchWord;
    }

    public ColumnInfo columnRequestedTime() {
        return _columnRequestedTime;
    }

    public ColumnInfo columnResponseTime() {
        return _columnResponseTime;
    }

    public ColumnInfo columnHitCount() {
        return _columnHitCount;
    }

    public ColumnInfo columnQueryOffset() {
        return _columnQueryOffset;
    }

    public ColumnInfo columnQueryPageSize() {
        return _columnQueryPageSize;
    }

    public ColumnInfo columnUserAgent() {
        return _columnUserAgent;
    }

    public ColumnInfo columnReferer() {
        return _columnReferer;
    }

    public ColumnInfo columnClientIp() {
        return _columnClientIp;
    }

    public ColumnInfo columnUserSessionId() {
        return _columnUserSessionId;
    }

    public ColumnInfo columnAccessType() {
        return _columnAccessType;
    }

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
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    public ForeignInfo foreignUserInfo() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(
                columnUserId(), UserInfoDbm.getInstance().columnId());
        return cfi("CONSTRAINT_F2A", "userInfo", this,
                UserInfoDbm.getInstance(), map, 0, false, false, false, false,
                null, null, false, "searchLogList");
    }

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------
    public ReferrerInfo referrerClickLogList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                ClickLogDbm.getInstance().columnSearchId());
        return cri("CONSTRAINT_310", "clickLogList", this,
                ClickLogDbm.getInstance(), map, false, "searchLog");
    }

    public ReferrerInfo referrerSearchFieldLogList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                SearchFieldLogDbm.getInstance().columnSearchId());
        return cri("CONSTRAINT_96", "searchFieldLogList", this,
                SearchFieldLogDbm.getInstance(), map, false, "searchLog");
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
    public Entity newEntity() {
        return newMyEntity();
    }

    public SearchLog newMyEntity() {
        return new SearchLog();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((SearchLog) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((SearchLog) e, m);
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
