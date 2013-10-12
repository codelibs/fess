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
import jp.sf.fess.db.exentity.WebCrawlingConfig;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of WEB_CRAWLING_CONFIG. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class WebCrawlingConfigDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final WebCrawlingConfigDbm _instance = new WebCrawlingConfigDbm();

    private WebCrawlingConfigDbm() {
    }

    public static WebCrawlingConfigDbm getInstance() {
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
        setupEpg(_epgMap, new EpgName(), "name");
        setupEpg(_epgMap, new EpgUrls(), "urls");
        setupEpg(_epgMap, new EpgIncludedUrls(), "includedUrls");
        setupEpg(_epgMap, new EpgExcludedUrls(), "excludedUrls");
        setupEpg(_epgMap, new EpgIncludedDocUrls(), "includedDocUrls");
        setupEpg(_epgMap, new EpgExcludedDocUrls(), "excludedDocUrls");
        setupEpg(_epgMap, new EpgConfigParameter(), "configParameter");
        setupEpg(_epgMap, new EpgDepth(), "depth");
        setupEpg(_epgMap, new EpgMaxAccessCount(), "maxAccessCount");
        setupEpg(_epgMap, new EpgUserAgent(), "userAgent");
        setupEpg(_epgMap, new EpgNumOfThread(), "numOfThread");
        setupEpg(_epgMap, new EpgIntervalTime(), "intervalTime");
        setupEpg(_epgMap, new EpgBoost(), "boost");
        setupEpg(_epgMap, new EpgAvailable(), "available");
        setupEpg(_epgMap, new EpgSortOrder(), "sortOrder");
        setupEpg(_epgMap, new EpgCreatedBy(), "createdBy");
        setupEpg(_epgMap, new EpgCreatedTime(), "createdTime");
        setupEpg(_epgMap, new EpgUpdatedBy(), "updatedBy");
        setupEpg(_epgMap, new EpgUpdatedTime(), "updatedTime");
        setupEpg(_epgMap, new EpgDeletedBy(), "deletedBy");
        setupEpg(_epgMap, new EpgDeletedTime(), "deletedTime");
        setupEpg(_epgMap, new EpgVersionNo(), "versionNo");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setId(ctl(v));
        }
    }

    public static class EpgName implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getName();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setName((String) v);
        }
    }

    public static class EpgUrls implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getUrls();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setUrls((String) v);
        }
    }

    public static class EpgIncludedUrls implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getIncludedUrls();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setIncludedUrls((String) v);
        }
    }

    public static class EpgExcludedUrls implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getExcludedUrls();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setExcludedUrls((String) v);
        }
    }

    public static class EpgIncludedDocUrls implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getIncludedDocUrls();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setIncludedDocUrls((String) v);
        }
    }

    public static class EpgExcludedDocUrls implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getExcludedDocUrls();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setExcludedDocUrls((String) v);
        }
    }

    public static class EpgConfigParameter implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getConfigParameter();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setConfigParameter((String) v);
        }
    }

    public static class EpgDepth implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getDepth();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setDepth(cti(v));
        }
    }

    public static class EpgMaxAccessCount implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getMaxAccessCount();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setMaxAccessCount(ctl(v));
        }
    }

    public static class EpgUserAgent implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getUserAgent();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setUserAgent((String) v);
        }
    }

    public static class EpgNumOfThread implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getNumOfThread();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setNumOfThread(cti(v));
        }
    }

    public static class EpgIntervalTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getIntervalTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setIntervalTime(cti(v));
        }
    }

    public static class EpgBoost implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getBoost();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setBoost(ctb(v));
        }
    }

    public static class EpgAvailable implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getAvailable();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setAvailable((String) v);
        }
    }

    public static class EpgSortOrder implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getSortOrder();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setSortOrder(cti(v));
        }
    }

    public static class EpgCreatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getCreatedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setCreatedBy((String) v);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getCreatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setCreatedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgUpdatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getUpdatedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setUpdatedBy((String) v);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getUpdatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setUpdatedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgDeletedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getDeletedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setDeletedBy((String) v);
        }
    }

    public static class EpgDeletedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getDeletedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setDeletedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgVersionNo implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((WebCrawlingConfig) e).getVersionNo();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((WebCrawlingConfig) e).setVersionNo(cti(v));
        }
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "WEB_CRAWLING_CONFIG";

    protected final String _tablePropertyName = "webCrawlingConfig";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "WEB_CRAWLING_CONFIG", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_A2A25811_03ED_49AF_9DA3_3BF45CE3AED3",
            false,
            null,
            null,
            null,
            "failureUrlList,requestHeaderList,webAuthenticationList,webConfigToBrowserTypeMappingList,webConfigToLabelTypeMappingList,webConfigToRoleTypeMappingList",
            null);

    protected final ColumnInfo _columnName = cci("NAME", "NAME", null, null,
            true, "name", String.class, false, false, "VARCHAR", 200, 0, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnUrls = cci("URLS", "URLS", null, null,
            true, "urls", String.class, false, false, "VARCHAR", 4000, 0, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnIncludedUrls = cci("INCLUDED_URLS",
            "INCLUDED_URLS", null, null, false, "includedUrls", String.class,
            false, false, "VARCHAR", 4000, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnExcludedUrls = cci("EXCLUDED_URLS",
            "EXCLUDED_URLS", null, null, false, "excludedUrls", String.class,
            false, false, "VARCHAR", 4000, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnIncludedDocUrls = cci(
            "INCLUDED_DOC_URLS", "INCLUDED_DOC_URLS", null, null, false,
            "includedDocUrls", String.class, false, false, "VARCHAR", 4000, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnExcludedDocUrls = cci(
            "EXCLUDED_DOC_URLS", "EXCLUDED_DOC_URLS", null, null, false,
            "excludedDocUrls", String.class, false, false, "VARCHAR", 4000, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnConfigParameter = cci("CONFIG_PARAMETER",
            "CONFIG_PARAMETER", null, null, false, "configParameter",
            String.class, false, false, "VARCHAR", 4000, 0, null, false, null,
            null, null, null, null);

    protected final ColumnInfo _columnDepth = cci("DEPTH", "DEPTH", null, null,
            false, "depth", Integer.class, false, false, "INTEGER", 10, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnMaxAccessCount = cci("MAX_ACCESS_COUNT",
            "MAX_ACCESS_COUNT", null, null, false, "maxAccessCount",
            Long.class, false, false, "BIGINT", 19, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnUserAgent = cci("USER_AGENT",
            "USER_AGENT", null, null, true, "userAgent", String.class, false,
            false, "VARCHAR", 200, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnNumOfThread = cci("NUM_OF_THREAD",
            "NUM_OF_THREAD", null, null, true, "numOfThread", Integer.class,
            false, false, "INTEGER", 10, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnIntervalTime = cci("INTERVAL_TIME",
            "INTERVAL_TIME", null, null, true, "intervalTime", Integer.class,
            false, false, "INTEGER", 10, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnBoost = cci("BOOST", "BOOST", null, null,
            true, "boost", java.math.BigDecimal.class, false, false, "DOUBLE",
            17, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnAvailable = cci("AVAILABLE", "AVAILABLE",
            null, null, true, "available", String.class, false, false,
            "VARCHAR", 1, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnSortOrder = cci("SORT_ORDER",
            "SORT_ORDER", null, null, true, "sortOrder", Integer.class, false,
            false, "INTEGER", 10, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnCreatedBy = cci("CREATED_BY",
            "CREATED_BY", null, null, true, "createdBy", String.class, false,
            false, "VARCHAR", 255, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnCreatedTime = cci("CREATED_TIME",
            "CREATED_TIME", null, null, true, "createdTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnUpdatedBy = cci("UPDATED_BY",
            "UPDATED_BY", null, null, false, "updatedBy", String.class, false,
            false, "VARCHAR", 255, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnUpdatedTime = cci("UPDATED_TIME",
            "UPDATED_TIME", null, null, false, "updatedTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnDeletedBy = cci("DELETED_BY",
            "DELETED_BY", null, null, false, "deletedBy", String.class, false,
            false, "VARCHAR", 255, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnDeletedTime = cci("DELETED_TIME",
            "DELETED_TIME", null, null, false, "deletedTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnVersionNo = cci("VERSION_NO",
            "VERSION_NO", null, null, true, "versionNo", Integer.class, false,
            false, "INTEGER", 10, 0, null, false,
            OptimisticLockType.VERSION_NO, null, null, null, null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnName() {
        return _columnName;
    }

    public ColumnInfo columnUrls() {
        return _columnUrls;
    }

    public ColumnInfo columnIncludedUrls() {
        return _columnIncludedUrls;
    }

    public ColumnInfo columnExcludedUrls() {
        return _columnExcludedUrls;
    }

    public ColumnInfo columnIncludedDocUrls() {
        return _columnIncludedDocUrls;
    }

    public ColumnInfo columnExcludedDocUrls() {
        return _columnExcludedDocUrls;
    }

    public ColumnInfo columnConfigParameter() {
        return _columnConfigParameter;
    }

    public ColumnInfo columnDepth() {
        return _columnDepth;
    }

    public ColumnInfo columnMaxAccessCount() {
        return _columnMaxAccessCount;
    }

    public ColumnInfo columnUserAgent() {
        return _columnUserAgent;
    }

    public ColumnInfo columnNumOfThread() {
        return _columnNumOfThread;
    }

    public ColumnInfo columnIntervalTime() {
        return _columnIntervalTime;
    }

    public ColumnInfo columnBoost() {
        return _columnBoost;
    }

    public ColumnInfo columnAvailable() {
        return _columnAvailable;
    }

    public ColumnInfo columnSortOrder() {
        return _columnSortOrder;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    public ColumnInfo columnDeletedBy() {
        return _columnDeletedBy;
    }

    public ColumnInfo columnDeletedTime() {
        return _columnDeletedTime;
    }

    public ColumnInfo columnVersionNo() {
        return _columnVersionNo;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnName());
        ls.add(columnUrls());
        ls.add(columnIncludedUrls());
        ls.add(columnExcludedUrls());
        ls.add(columnIncludedDocUrls());
        ls.add(columnExcludedDocUrls());
        ls.add(columnConfigParameter());
        ls.add(columnDepth());
        ls.add(columnMaxAccessCount());
        ls.add(columnUserAgent());
        ls.add(columnNumOfThread());
        ls.add(columnIntervalTime());
        ls.add(columnBoost());
        ls.add(columnAvailable());
        ls.add(columnSortOrder());
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        ls.add(columnDeletedBy());
        ls.add(columnDeletedTime());
        ls.add(columnVersionNo());
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
    public ReferrerInfo referrerFailureUrlList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                FailureUrlDbm.getInstance().columnWebConfigId());
        return cri("CONSTRAINT_FBE3", "failureUrlList", this,
                FailureUrlDbm.getInstance(), map, false, "webCrawlingConfig");
    }

    public ReferrerInfo referrerRequestHeaderList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                RequestHeaderDbm.getInstance().columnWebCrawlingConfigId());
        return cri("CONSTRAINT_A1", "requestHeaderList", this,
                RequestHeaderDbm.getInstance(), map, false, "webCrawlingConfig");
    }

    public ReferrerInfo referrerWebAuthenticationList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                WebAuthenticationDbm.getInstance().columnWebCrawlingConfigId());
        return cri("CONSTRAINT_A31", "webAuthenticationList", this,
                WebAuthenticationDbm.getInstance(), map, false,
                "webCrawlingConfig");
    }

    public ReferrerInfo referrerWebConfigToBrowserTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                WebConfigToBrowserTypeMappingDbm.getInstance()
                        .columnWebConfigId());
        return cri("CONSTRAINT_27", "webConfigToBrowserTypeMappingList", this,
                WebConfigToBrowserTypeMappingDbm.getInstance(), map, false,
                "webCrawlingConfig");
    }

    public ReferrerInfo referrerWebConfigToLabelTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                WebConfigToLabelTypeMappingDbm.getInstance()
                        .columnWebConfigId());
        return cri("CONSTRAINT_6AC", "webConfigToLabelTypeMappingList", this,
                WebConfigToLabelTypeMappingDbm.getInstance(), map, false,
                "webCrawlingConfig");
    }

    public ReferrerInfo referrerWebConfigToRoleTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                WebConfigToRoleTypeMappingDbm.getInstance().columnWebConfigId());
        return cri("CONSTRAINT_A17D", "webConfigToRoleTypeMappingList", this,
                WebConfigToRoleTypeMappingDbm.getInstance(), map, false,
                "webCrawlingConfig");
    }

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============
    @Override
    public boolean hasIdentity() {
        return true;
    }

    @Override
    public boolean hasVersionNo() {
        return true;
    }

    @Override
    public ColumnInfo getVersionNoColumnInfo() {
        return _columnVersionNo;
    }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    @Override
    public String getEntityTypeName() {
        return "jp.sf.fess.db.exentity.WebCrawlingConfig";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.WebCrawlingConfigCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.WebCrawlingConfigBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<WebCrawlingConfig> getEntityType() {
        return WebCrawlingConfig.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public WebCrawlingConfig newMyEntity() {
        return new WebCrawlingConfig();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((WebCrawlingConfig) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((WebCrawlingConfig) e, m);
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
