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
import jp.sf.fess.db.exentity.FileCrawlingConfig;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of FILE_CRAWLING_CONFIG. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class FileCrawlingConfigDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final FileCrawlingConfigDbm _instance = new FileCrawlingConfigDbm();

    private FileCrawlingConfigDbm() {
    }

    public static FileCrawlingConfigDbm getInstance() {
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
        setupEpg(_epgMap, new EpgPaths(), "paths");
        setupEpg(_epgMap, new EpgIncludedPaths(), "includedPaths");
        setupEpg(_epgMap, new EpgExcludedPaths(), "excludedPaths");
        setupEpg(_epgMap, new EpgIncludedDocPaths(), "includedDocPaths");
        setupEpg(_epgMap, new EpgExcludedDocPaths(), "excludedDocPaths");
        setupEpg(_epgMap, new EpgConfigParameter(), "configParameter");
        setupEpg(_epgMap, new EpgDepth(), "depth");
        setupEpg(_epgMap, new EpgMaxAccessCount(), "maxAccessCount");
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
            return ((FileCrawlingConfig) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setId(ctl(v));
        }
    }

    public static class EpgName implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getName();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setName((String) v);
        }
    }

    public static class EpgPaths implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getPaths();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setPaths((String) v);
        }
    }

    public static class EpgIncludedPaths implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getIncludedPaths();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setIncludedPaths((String) v);
        }
    }

    public static class EpgExcludedPaths implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getExcludedPaths();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setExcludedPaths((String) v);
        }
    }

    public static class EpgIncludedDocPaths implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getIncludedDocPaths();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setIncludedDocPaths((String) v);
        }
    }

    public static class EpgExcludedDocPaths implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getExcludedDocPaths();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setExcludedDocPaths((String) v);
        }
    }

    public static class EpgConfigParameter implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getConfigParameter();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setConfigParameter((String) v);
        }
    }

    public static class EpgDepth implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getDepth();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setDepth(cti(v));
        }
    }

    public static class EpgMaxAccessCount implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getMaxAccessCount();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setMaxAccessCount(ctl(v));
        }
    }

    public static class EpgNumOfThread implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getNumOfThread();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setNumOfThread(cti(v));
        }
    }

    public static class EpgIntervalTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getIntervalTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setIntervalTime(cti(v));
        }
    }

    public static class EpgBoost implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getBoost();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setBoost(ctb(v));
        }
    }

    public static class EpgAvailable implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getAvailable();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setAvailable((String) v);
        }
    }

    public static class EpgSortOrder implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getSortOrder();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setSortOrder(cti(v));
        }
    }

    public static class EpgCreatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getCreatedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setCreatedBy((String) v);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getCreatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setCreatedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgUpdatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getUpdatedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setUpdatedBy((String) v);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getUpdatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setUpdatedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgDeletedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getDeletedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setDeletedBy((String) v);
        }
    }

    public static class EpgDeletedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getDeletedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setDeletedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgVersionNo implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileCrawlingConfig) e).getVersionNo();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileCrawlingConfig) e).setVersionNo(cti(v));
        }
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "FILE_CRAWLING_CONFIG";

    protected final String _tablePropertyName = "fileCrawlingConfig";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "FILE_CRAWLING_CONFIG", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_BE942DC5_22D5_4C59_9BD6_5A2FFADE870C",
            false,
            null,
            null,
            null,
            "failureUrlList,fileAuthenticationList,fileConfigToBrowserTypeMappingList,fileConfigToLabelTypeMappingList,fileConfigToRoleTypeMappingList",
            null);

    protected final ColumnInfo _columnName = cci("NAME", "NAME", null, null,
            true, "name", String.class, false, false, "VARCHAR", 200, 0, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnPaths = cci("PATHS", "PATHS", null, null,
            true, "paths", String.class, false, false, "VARCHAR", 4000, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnIncludedPaths = cci("INCLUDED_PATHS",
            "INCLUDED_PATHS", null, null, false, "includedPaths", String.class,
            false, false, "VARCHAR", 4000, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnExcludedPaths = cci("EXCLUDED_PATHS",
            "EXCLUDED_PATHS", null, null, false, "excludedPaths", String.class,
            false, false, "VARCHAR", 4000, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnIncludedDocPaths = cci(
            "INCLUDED_DOC_PATHS", "INCLUDED_DOC_PATHS", null, null, false,
            "includedDocPaths", String.class, false, false, "VARCHAR", 4000, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnExcludedDocPaths = cci(
            "EXCLUDED_DOC_PATHS", "EXCLUDED_DOC_PATHS", null, null, false,
            "excludedDocPaths", String.class, false, false, "VARCHAR", 4000, 0,
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

    public ColumnInfo columnPaths() {
        return _columnPaths;
    }

    public ColumnInfo columnIncludedPaths() {
        return _columnIncludedPaths;
    }

    public ColumnInfo columnExcludedPaths() {
        return _columnExcludedPaths;
    }

    public ColumnInfo columnIncludedDocPaths() {
        return _columnIncludedDocPaths;
    }

    public ColumnInfo columnExcludedDocPaths() {
        return _columnExcludedDocPaths;
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
        ls.add(columnPaths());
        ls.add(columnIncludedPaths());
        ls.add(columnExcludedPaths());
        ls.add(columnIncludedDocPaths());
        ls.add(columnExcludedDocPaths());
        ls.add(columnConfigParameter());
        ls.add(columnDepth());
        ls.add(columnMaxAccessCount());
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
                FailureUrlDbm.getInstance().columnFileConfigId());
        return cri("CONSTRAINT_FBE", "failureUrlList", this,
                FailureUrlDbm.getInstance(), map, false, "fileCrawlingConfig");
    }

    public ReferrerInfo referrerFileAuthenticationList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                FileAuthenticationDbm.getInstance()
                        .columnFileCrawlingConfigId());
        return cri("CONSTRAINT_F90", "fileAuthenticationList", this,
                FileAuthenticationDbm.getInstance(), map, false,
                "fileCrawlingConfig");
    }

    public ReferrerInfo referrerFileConfigToBrowserTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                FileConfigToBrowserTypeMappingDbm.getInstance()
                        .columnFileConfigId());
        return cri("CONSTRAINT_F85", "fileConfigToBrowserTypeMappingList",
                this, FileConfigToBrowserTypeMappingDbm.getInstance(), map,
                false, "fileCrawlingConfig");
    }

    public ReferrerInfo referrerFileConfigToLabelTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                FileConfigToLabelTypeMappingDbm.getInstance()
                        .columnFileConfigId());
        return cri("CONSTRAINT_F57F", "fileConfigToLabelTypeMappingList", this,
                FileConfigToLabelTypeMappingDbm.getInstance(), map, false,
                "fileCrawlingConfig");
    }

    public ReferrerInfo referrerFileConfigToRoleTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                FileConfigToRoleTypeMappingDbm.getInstance()
                        .columnFileConfigId());
        return cri("CONSTRAINT_3A", "fileConfigToRoleTypeMappingList", this,
                FileConfigToRoleTypeMappingDbm.getInstance(), map, false,
                "fileCrawlingConfig");
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
        return "jp.sf.fess.db.exentity.FileCrawlingConfig";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.FileCrawlingConfigCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.FileCrawlingConfigBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<FileCrawlingConfig> getEntityType() {
        return FileCrawlingConfig.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public FileCrawlingConfig newMyEntity() {
        return new FileCrawlingConfig();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((FileCrawlingConfig) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((FileCrawlingConfig) e, m);
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
