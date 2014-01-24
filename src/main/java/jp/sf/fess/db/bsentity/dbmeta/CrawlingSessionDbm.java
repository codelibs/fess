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
import jp.sf.fess.db.exentity.CrawlingSession;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of CRAWLING_SESSION. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class CrawlingSessionDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final CrawlingSessionDbm _instance = new CrawlingSessionDbm();

    private CrawlingSessionDbm() {
    }

    public static CrawlingSessionDbm getInstance() {
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
        setupEpg(_epgMap, new EpgSessionId(), "sessionId");
        setupEpg(_epgMap, new EpgName(), "name");
        setupEpg(_epgMap, new EpgExpiredTime(), "expiredTime");
        setupEpg(_epgMap, new EpgCreatedTime(), "createdTime");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((CrawlingSession) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((CrawlingSession) e).setId(ctl(v));
        }
    }

    public static class EpgSessionId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((CrawlingSession) e).getSessionId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((CrawlingSession) e).setSessionId((String) v);
        }
    }

    public static class EpgName implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((CrawlingSession) e).getName();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((CrawlingSession) e).setName((String) v);
        }
    }

    public static class EpgExpiredTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((CrawlingSession) e).getExpiredTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((CrawlingSession) e).setExpiredTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((CrawlingSession) e).getCreatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((CrawlingSession) e).setCreatedTime((java.sql.Timestamp) v);
        }
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "CRAWLING_SESSION";

    protected final String _tablePropertyName = "crawlingSession";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "CRAWLING_SESSION", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_9AAE16DA_0823_450C_A0D8_8D9954DB2E8E",
            false, null, null, null, "crawlingSessionInfoList", null);

    protected final ColumnInfo _columnSessionId = cci("SESSION_ID",
            "SESSION_ID", null, null, true, "sessionId", String.class, false,
            false, "VARCHAR", 20, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnName = cci("NAME", "NAME", null, null,
            false, "name", String.class, false, false, "VARCHAR", 20, 0, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnExpiredTime = cci("EXPIRED_TIME",
            "EXPIRED_TIME", null, null, false, "expiredTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnCreatedTime = cci("CREATED_TIME",
            "CREATED_TIME", null, null, true, "createdTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnSessionId() {
        return _columnSessionId;
    }

    public ColumnInfo columnName() {
        return _columnName;
    }

    public ColumnInfo columnExpiredTime() {
        return _columnExpiredTime;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnSessionId());
        ls.add(columnName());
        ls.add(columnExpiredTime());
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

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------
    public ReferrerInfo referrerCrawlingSessionInfoList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                CrawlingSessionInfoDbm.getInstance().columnCrawlingSessionId());
        return cri("CONSTRAINT_B3A", "crawlingSessionInfoList", this,
                CrawlingSessionInfoDbm.getInstance(), map, false,
                "crawlingSession");
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
        return "jp.sf.fess.db.exentity.CrawlingSession";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.CrawlingSessionCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.CrawlingSessionBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<CrawlingSession> getEntityType() {
        return CrawlingSession.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public CrawlingSession newMyEntity() {
        return new CrawlingSession();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((CrawlingSession) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((CrawlingSession) e, m);
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
