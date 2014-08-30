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
import jp.sf.fess.db.exentity.CrawlingSessionInfo;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of CRAWLING_SESSION_INFO. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class CrawlingSessionInfoDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final CrawlingSessionInfoDbm _instance = new CrawlingSessionInfoDbm();

    private CrawlingSessionInfoDbm() {
    }

    public static CrawlingSessionInfoDbm getInstance() {
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
        setupEpg(_epgMap, new EpgCrawlingSessionId(), "crawlingSessionId");
        setupEpg(_epgMap, new EpgKey(), "key");
        setupEpg(_epgMap, new EpgValue(), "value");
        setupEpg(_epgMap, new EpgCreatedTime(), "createdTime");
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((CrawlingSessionInfo) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((CrawlingSessionInfo) et).setId(ctl(vl));
        }
    }

    public static class EpgCrawlingSessionId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((CrawlingSessionInfo) et).getCrawlingSessionId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((CrawlingSessionInfo) et).setCrawlingSessionId(ctl(vl));
        }
    }

    public static class EpgKey implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((CrawlingSessionInfo) et).getKey();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((CrawlingSessionInfo) et).setKey((String) vl);
        }
    }

    public static class EpgValue implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((CrawlingSessionInfo) et).getValue();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((CrawlingSessionInfo) et).setValue((String) vl);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((CrawlingSessionInfo) et).getCreatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((CrawlingSessionInfo) et).setCreatedTime((java.sql.Timestamp) vl);
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
        setupEfpg(_efpgMap, new EfpgCrawlingSession(), "crawlingSession");
    }

    public class EfpgCrawlingSession implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((CrawlingSessionInfo) et).getCrawlingSession();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((CrawlingSessionInfo) et).setCrawlingSession((CrawlingSession) vl);
        }
    }

    @Override
    public PropertyGateway findForeignPropertyGateway(final String prop) {
        return doFindEfpg(_efpgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "CRAWLING_SESSION_INFO";

    protected final String _tablePropertyName = "crawlingSessionInfo";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "CRAWLING_SESSION_INFO", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_E5E807B1_85D9_481E_9BF7_8CE454A49335",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnCrawlingSessionId = cci(
            "CRAWLING_SESSION_ID", "CRAWLING_SESSION_ID", null, null,
            Long.class, "crawlingSessionId", null, false, false, true,
            "BIGINT", 19, 0, null, false, null, null, "crawlingSession", null,
            null);

    protected final ColumnInfo _columnKey = cci("KEY", "KEY", null, null,
            String.class, "key", null, false, false, true, "VARCHAR", 20, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnValue = cci("VALUE", "VALUE", null, null,
            String.class, "value", null, false, false, true, "VARCHAR", 100, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnCreatedTime = cci("CREATED_TIME",
            "CREATED_TIME", null, null, java.sql.Timestamp.class,
            "createdTime", null, false, false, true, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * CRAWLING_SESSION_ID: {IX, NotNull, BIGINT(19), FK to CRAWLING_SESSION}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCrawlingSessionId() {
        return _columnCrawlingSessionId;
    }

    /**
     * KEY: {NotNull, VARCHAR(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnKey() {
        return _columnKey;
    }

    /**
     * VALUE: {NotNull, VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnValue() {
        return _columnValue;
    }

    /**
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnCrawlingSessionId());
        ls.add(columnKey());
        ls.add(columnValue());
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
    // cannot cache because it uses related DB meta instance while booting
    // (instead, cached by super's collection)
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    /**
     * CRAWLING_SESSION by my CRAWLING_SESSION_ID, named 'crawlingSession'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignCrawlingSession() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(
                columnCrawlingSessionId(), CrawlingSessionDbm.getInstance()
                        .columnId());
        return cfi("CONSTRAINT_B3A", "crawlingSession", this,
                CrawlingSessionDbm.getInstance(), mp, 0, null, false, false,
                false, false, null, null, false, "crawlingSessionInfoList");
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
        return "jp.sf.fess.db.exentity.CrawlingSessionInfo";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.CrawlingSessionInfoCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.CrawlingSessionInfoBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<CrawlingSessionInfo> getEntityType() {
        return CrawlingSessionInfo.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public CrawlingSessionInfo newEntity() {
        return new CrawlingSessionInfo();
    }

    public CrawlingSessionInfo newMyEntity() {
        return new CrawlingSessionInfo();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((CrawlingSessionInfo) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((CrawlingSessionInfo) et, mp);
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
