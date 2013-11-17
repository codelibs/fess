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
import jp.sf.fess.db.exentity.DataCrawlingConfig;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of DATA_CRAWLING_CONFIG. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class DataCrawlingConfigDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final DataCrawlingConfigDbm _instance = new DataCrawlingConfigDbm();

    private DataCrawlingConfigDbm() {
    }

    public static DataCrawlingConfigDbm getInstance() {
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
        setupEpg(_epgMap, new EpgHandlerName(), "handlerName");
        setupEpg(_epgMap, new EpgHandlerParameter(), "handlerParameter");
        setupEpg(_epgMap, new EpgHandlerScript(), "handlerScript");
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
            return ((DataCrawlingConfig) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setId(ctl(v));
        }
    }

    public static class EpgName implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getName();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setName((String) v);
        }
    }

    public static class EpgHandlerName implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getHandlerName();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setHandlerName((String) v);
        }
    }

    public static class EpgHandlerParameter implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getHandlerParameter();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setHandlerParameter((String) v);
        }
    }

    public static class EpgHandlerScript implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getHandlerScript();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setHandlerScript((String) v);
        }
    }

    public static class EpgBoost implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getBoost();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setBoost(ctb(v));
        }
    }

    public static class EpgAvailable implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getAvailable();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setAvailable((String) v);
        }
    }

    public static class EpgSortOrder implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getSortOrder();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setSortOrder(cti(v));
        }
    }

    public static class EpgCreatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getCreatedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setCreatedBy((String) v);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getCreatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setCreatedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgUpdatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getUpdatedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setUpdatedBy((String) v);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getUpdatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setUpdatedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgDeletedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getDeletedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setDeletedBy((String) v);
        }
    }

    public static class EpgDeletedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getDeletedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setDeletedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgVersionNo implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataCrawlingConfig) e).getVersionNo();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataCrawlingConfig) e).setVersionNo(cti(v));
        }
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "DATA_CRAWLING_CONFIG";

    protected final String _tablePropertyName = "dataCrawlingConfig";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "DATA_CRAWLING_CONFIG", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_D4F0A908_257C_465E_9D16_46C7676E0EE9",
            false,
            null,
            null,
            null,
            "dataConfigToBrowserTypeMappingList,dataConfigToLabelTypeMappingList,dataConfigToRoleTypeMappingList",
            null);

    protected final ColumnInfo _columnName = cci("NAME", "NAME", null, null,
            true, "name", String.class, false, false, "VARCHAR", 200, 0, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnHandlerName = cci("HANDLER_NAME",
            "HANDLER_NAME", null, null, true, "handlerName", String.class,
            false, false, "VARCHAR", 200, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnHandlerParameter = cci(
            "HANDLER_PARAMETER", "HANDLER_PARAMETER", null, null, false,
            "handlerParameter", String.class, false, false, "VARCHAR", 4000, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnHandlerScript = cci("HANDLER_SCRIPT",
            "HANDLER_SCRIPT", null, null, false, "handlerScript", String.class,
            false, false, "VARCHAR", 4000, 0, null, false, null, null, null,
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

    public ColumnInfo columnHandlerName() {
        return _columnHandlerName;
    }

    public ColumnInfo columnHandlerParameter() {
        return _columnHandlerParameter;
    }

    public ColumnInfo columnHandlerScript() {
        return _columnHandlerScript;
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
        ls.add(columnHandlerName());
        ls.add(columnHandlerParameter());
        ls.add(columnHandlerScript());
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
    public ReferrerInfo referrerDataConfigToBrowserTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                DataConfigToBrowserTypeMappingDbm.getInstance()
                        .columnDataConfigId());
        return cri("CONSTRAINT_FA09", "dataConfigToBrowserTypeMappingList",
                this, DataConfigToBrowserTypeMappingDbm.getInstance(), map,
                false, "dataCrawlingConfig");
    }

    public ReferrerInfo referrerDataConfigToLabelTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                DataConfigToLabelTypeMappingDbm.getInstance()
                        .columnDataConfigId());
        return cri("CONSTRAINT_5CE", "dataConfigToLabelTypeMappingList", this,
                DataConfigToLabelTypeMappingDbm.getInstance(), map, false,
                "dataCrawlingConfig");
    }

    public ReferrerInfo referrerDataConfigToRoleTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                DataConfigToRoleTypeMappingDbm.getInstance()
                        .columnDataConfigId());
        return cri("CONSTRAINT_E3", "dataConfigToRoleTypeMappingList", this,
                DataConfigToRoleTypeMappingDbm.getInstance(), map, false,
                "dataCrawlingConfig");
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
        return "jp.sf.fess.db.exentity.DataCrawlingConfig";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.DataCrawlingConfigCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.DataCrawlingConfigBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<DataCrawlingConfig> getEntityType() {
        return DataCrawlingConfig.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public DataCrawlingConfig newMyEntity() {
        return new DataCrawlingConfig();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((DataCrawlingConfig) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((DataCrawlingConfig) e, m);
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
