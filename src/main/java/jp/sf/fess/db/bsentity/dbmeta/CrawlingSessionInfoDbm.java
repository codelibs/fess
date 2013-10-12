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
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    {
        setupEpg(_epgMap, new EpgId(), "id");
        setupEpg(_epgMap, new EpgCrawlingSessionId(), "crawlingSessionId");
        setupEpg(_epgMap, new EpgKey(), "key");
        setupEpg(_epgMap, new EpgValue(), "value");
        setupEpg(_epgMap, new EpgCreatedTime(), "createdTime");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((CrawlingSessionInfo) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((CrawlingSessionInfo) e).setId(ctl(v));
        }
    }

    public static class EpgCrawlingSessionId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((CrawlingSessionInfo) e).getCrawlingSessionId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((CrawlingSessionInfo) e).setCrawlingSessionId(ctl(v));
        }
    }

    public static class EpgKey implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((CrawlingSessionInfo) e).getKey();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((CrawlingSessionInfo) e).setKey((String) v);
        }
    }

    public static class EpgValue implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((CrawlingSessionInfo) e).getValue();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((CrawlingSessionInfo) e).setValue((String) v);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((CrawlingSessionInfo) e).getCreatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((CrawlingSessionInfo) e).setCreatedTime((java.sql.Timestamp) v);
        }
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
            true,
            "id",
            Long.class,
            true,
            true,
            "BIGINT",
            19,
            0,
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_C4E8F6CF_7747_4C35_820B_0A2E3DF42FB4",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnCrawlingSessionId = cci(
            "CRAWLING_SESSION_ID", "CRAWLING_SESSION_ID", null, null, true,
            "crawlingSessionId", Long.class, false, false, "BIGINT", 19, 0,
            null, false, null, null, "crawlingSession", null, null);

    protected final ColumnInfo _columnKey = cci("KEY", "KEY", null, null, true,
            "key", String.class, false, false, "VARCHAR", 20, 0, null, false,
            null, null, null, null, null);

    protected final ColumnInfo _columnValue = cci("VALUE", "VALUE", null, null,
            true, "value", String.class, false, false, "VARCHAR", 100, 0, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnCreatedTime = cci("CREATED_TIME",
            "CREATED_TIME", null, null, true, "createdTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnCrawlingSessionId() {
        return _columnCrawlingSessionId;
    }

    public ColumnInfo columnKey() {
        return _columnKey;
    }

    public ColumnInfo columnValue() {
        return _columnValue;
    }

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
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    public ForeignInfo foreignCrawlingSession() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(
                columnCrawlingSessionId(), CrawlingSessionDbm.getInstance()
                        .columnId());
        return cfi("CONSTRAINT_B3A", "crawlingSession", this,
                CrawlingSessionDbm.getInstance(), map, 0, false, false, false,
                false, null, null, false, "crawlingSessionInfoList");
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
    public Entity newEntity() {
        return newMyEntity();
    }

    public CrawlingSessionInfo newMyEntity() {
        return new CrawlingSessionInfo();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((CrawlingSessionInfo) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((CrawlingSessionInfo) e, m);
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
