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
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
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

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setId(ctl(vl));
        }
    }

    public static class EpgName implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getName();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setName((String) vl);
        }
    }

    public static class EpgUrls implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getUrls();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setUrls((String) vl);
        }
    }

    public static class EpgIncludedUrls implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getIncludedUrls();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setIncludedUrls((String) vl);
        }
    }

    public static class EpgExcludedUrls implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getExcludedUrls();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setExcludedUrls((String) vl);
        }
    }

    public static class EpgIncludedDocUrls implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getIncludedDocUrls();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setIncludedDocUrls((String) vl);
        }
    }

    public static class EpgExcludedDocUrls implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getExcludedDocUrls();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setExcludedDocUrls((String) vl);
        }
    }

    public static class EpgConfigParameter implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getConfigParameter();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setConfigParameter((String) vl);
        }
    }

    public static class EpgDepth implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getDepth();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setDepth(cti(vl));
        }
    }

    public static class EpgMaxAccessCount implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getMaxAccessCount();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setMaxAccessCount(ctl(vl));
        }
    }

    public static class EpgUserAgent implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getUserAgent();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setUserAgent((String) vl);
        }
    }

    public static class EpgNumOfThread implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getNumOfThread();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setNumOfThread(cti(vl));
        }
    }

    public static class EpgIntervalTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getIntervalTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setIntervalTime(cti(vl));
        }
    }

    public static class EpgBoost implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getBoost();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setBoost(ctb(vl));
        }
    }

    public static class EpgAvailable implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getAvailable();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setAvailable((String) vl);
        }
    }

    public static class EpgSortOrder implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getSortOrder();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setSortOrder(cti(vl));
        }
    }

    public static class EpgCreatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getCreatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setCreatedBy((String) vl);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getCreatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setCreatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgUpdatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getUpdatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setUpdatedBy((String) vl);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getUpdatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setUpdatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgDeletedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getDeletedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setDeletedBy((String) vl);
        }
    }

    public static class EpgDeletedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getDeletedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setDeletedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgVersionNo implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((WebCrawlingConfig) et).getVersionNo();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((WebCrawlingConfig) et).setVersionNo(cti(vl));
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
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
            Long.class,
            "id",
            null,
            true,
            true,
            true,
            "BIGINT",
            19,
            0,
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_1FDCD552_C6C3_44C5_802A_A080B6B1037D",
            false,
            null,
            null,
            null,
            "requestHeaderList,webAuthenticationList,webConfigToLabelTypeMappingList,webConfigToRoleTypeMappingList",
            null);

    protected final ColumnInfo _columnName = cci("NAME", "NAME", null, null,
            String.class, "name", null, false, false, true, "VARCHAR", 200, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnUrls = cci("URLS", "URLS", null, null,
            String.class, "urls", null, false, false, true, "VARCHAR", 4000, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnIncludedUrls = cci("INCLUDED_URLS",
            "INCLUDED_URLS", null, null, String.class, "includedUrls", null,
            false, false, false, "VARCHAR", 4000, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnExcludedUrls = cci("EXCLUDED_URLS",
            "EXCLUDED_URLS", null, null, String.class, "excludedUrls", null,
            false, false, false, "VARCHAR", 4000, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnIncludedDocUrls = cci(
            "INCLUDED_DOC_URLS", "INCLUDED_DOC_URLS", null, null, String.class,
            "includedDocUrls", null, false, false, false, "VARCHAR", 4000, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnExcludedDocUrls = cci(
            "EXCLUDED_DOC_URLS", "EXCLUDED_DOC_URLS", null, null, String.class,
            "excludedDocUrls", null, false, false, false, "VARCHAR", 4000, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnConfigParameter = cci("CONFIG_PARAMETER",
            "CONFIG_PARAMETER", null, null, String.class, "configParameter",
            null, false, false, false, "VARCHAR", 4000, 0, null, false, null,
            null, null, null, null);

    protected final ColumnInfo _columnDepth = cci("DEPTH", "DEPTH", null, null,
            Integer.class, "depth", null, false, false, false, "INTEGER", 10,
            0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnMaxAccessCount = cci("MAX_ACCESS_COUNT",
            "MAX_ACCESS_COUNT", null, null, Long.class, "maxAccessCount", null,
            false, false, false, "BIGINT", 19, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnUserAgent = cci("USER_AGENT",
            "USER_AGENT", null, null, String.class, "userAgent", null, false,
            false, true, "VARCHAR", 200, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnNumOfThread = cci("NUM_OF_THREAD",
            "NUM_OF_THREAD", null, null, Integer.class, "numOfThread", null,
            false, false, true, "INTEGER", 10, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnIntervalTime = cci("INTERVAL_TIME",
            "INTERVAL_TIME", null, null, Integer.class, "intervalTime", null,
            false, false, true, "INTEGER", 10, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnBoost = cci("BOOST", "BOOST", null, null,
            java.math.BigDecimal.class, "boost", null, false, false, true,
            "DOUBLE", 17, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnAvailable = cci("AVAILABLE", "AVAILABLE",
            null, null, String.class, "available", null, false, false, true,
            "VARCHAR", 1, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnSortOrder = cci("SORT_ORDER",
            "SORT_ORDER", null, null, Integer.class, "sortOrder", null, false,
            false, true, "INTEGER", 10, 0, null, false, null, null, null, null,
            null);

    protected final ColumnInfo _columnCreatedBy = cci("CREATED_BY",
            "CREATED_BY", null, null, String.class, "createdBy", null, false,
            false, true, "VARCHAR", 255, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnCreatedTime = cci("CREATED_TIME",
            "CREATED_TIME", null, null, java.sql.Timestamp.class,
            "createdTime", null, false, false, true, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnUpdatedBy = cci("UPDATED_BY",
            "UPDATED_BY", null, null, String.class, "updatedBy", null, false,
            false, false, "VARCHAR", 255, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnUpdatedTime = cci("UPDATED_TIME",
            "UPDATED_TIME", null, null, java.sql.Timestamp.class,
            "updatedTime", null, false, false, false, "TIMESTAMP", 23, 10,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnDeletedBy = cci("DELETED_BY",
            "DELETED_BY", null, null, String.class, "deletedBy", null, false,
            false, false, "VARCHAR", 255, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnDeletedTime = cci("DELETED_TIME",
            "DELETED_TIME", null, null, java.sql.Timestamp.class,
            "deletedTime", null, false, false, false, "TIMESTAMP", 23, 10,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnVersionNo = cci("VERSION_NO",
            "VERSION_NO", null, null, Integer.class, "versionNo", null, false,
            false, true, "INTEGER", 10, 0, null, false,
            OptimisticLockType.VERSION_NO, null, null, null, null);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * NAME: {NotNull, VARCHAR(200)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnName() {
        return _columnName;
    }

    /**
     * URLS: {NotNull, VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUrls() {
        return _columnUrls;
    }

    /**
     * INCLUDED_URLS: {VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnIncludedUrls() {
        return _columnIncludedUrls;
    }

    /**
     * EXCLUDED_URLS: {VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnExcludedUrls() {
        return _columnExcludedUrls;
    }

    /**
     * INCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnIncludedDocUrls() {
        return _columnIncludedDocUrls;
    }

    /**
     * EXCLUDED_DOC_URLS: {VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnExcludedDocUrls() {
        return _columnExcludedDocUrls;
    }

    /**
     * CONFIG_PARAMETER: {VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnConfigParameter() {
        return _columnConfigParameter;
    }

    /**
     * DEPTH: {INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnDepth() {
        return _columnDepth;
    }

    /**
     * MAX_ACCESS_COUNT: {BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMaxAccessCount() {
        return _columnMaxAccessCount;
    }

    /**
     * USER_AGENT: {NotNull, VARCHAR(200)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUserAgent() {
        return _columnUserAgent;
    }

    /**
     * NUM_OF_THREAD: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnNumOfThread() {
        return _columnNumOfThread;
    }

    /**
     * INTERVAL_TIME: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnIntervalTime() {
        return _columnIntervalTime;
    }

    /**
     * BOOST: {NotNull, DOUBLE(17)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnBoost() {
        return _columnBoost;
    }

    /**
     * AVAILABLE: {NotNull, VARCHAR(1)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnAvailable() {
        return _columnAvailable;
    }

    /**
     * SORT_ORDER: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSortOrder() {
        return _columnSortOrder;
    }

    /**
     * CREATED_BY: {NotNull, VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    /**
     * CREATED_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    /**
     * UPDATED_BY: {VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    /**
     * UPDATED_TIME: {TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    /**
     * DELETED_BY: {VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnDeletedBy() {
        return _columnDeletedBy;
    }

    /**
     * DELETED_TIME: {TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnDeletedTime() {
        return _columnDeletedTime;
    }

    /**
     * VERSION_NO: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
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
    // cannot cache because it uses related DB meta instance while booting
    // (instead, cached by super's collection)
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------
    /**
     * REQUEST_HEADER by WEB_CRAWLING_CONFIG_ID, named 'requestHeaderList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerRequestHeaderList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                RequestHeaderDbm.getInstance().columnWebCrawlingConfigId());
        return cri("CONSTRAINT_A1", "requestHeaderList", this,
                RequestHeaderDbm.getInstance(), mp, false, "webCrawlingConfig");
    }

    /**
     * WEB_AUTHENTICATION by WEB_CRAWLING_CONFIG_ID, named 'webAuthenticationList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerWebAuthenticationList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                WebAuthenticationDbm.getInstance().columnWebCrawlingConfigId());
        return cri("CONSTRAINT_A31", "webAuthenticationList", this,
                WebAuthenticationDbm.getInstance(), mp, false,
                "webCrawlingConfig");
    }

    /**
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToLabelTypeMappingList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerWebConfigToLabelTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                WebConfigToLabelTypeMappingDbm.getInstance()
                        .columnWebConfigId());
        return cri("CONSTRAINT_6AC", "webConfigToLabelTypeMappingList", this,
                WebConfigToLabelTypeMappingDbm.getInstance(), mp, false,
                "webCrawlingConfig");
    }

    /**
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by WEB_CONFIG_ID, named 'webConfigToRoleTypeMappingList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerWebConfigToRoleTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                WebConfigToRoleTypeMappingDbm.getInstance().columnWebConfigId());
        return cri("CONSTRAINT_A17D", "webConfigToRoleTypeMappingList", this,
                WebConfigToRoleTypeMappingDbm.getInstance(), mp, false,
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
    public WebCrawlingConfig newEntity() {
        return new WebCrawlingConfig();
    }

    public WebCrawlingConfig newMyEntity() {
        return new WebCrawlingConfig();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((WebCrawlingConfig) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((WebCrawlingConfig) et, mp);
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
