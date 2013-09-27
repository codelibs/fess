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
import jp.sf.fess.db.exentity.LabelType;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of LABEL_TYPE. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class LabelTypeDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final LabelTypeDbm _instance = new LabelTypeDbm();

    private LabelTypeDbm() {
    }

    public static LabelTypeDbm getInstance() {
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
        setupEpg(_epgMap, new EpgValue(), "value");
        setupEpg(_epgMap, new EpgIncludedPaths(), "includedPaths");
        setupEpg(_epgMap, new EpgExcludedPaths(), "excludedPaths");
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
            return ((LabelType) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setId(ctl(v));
        }
    }

    public static class EpgName implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((LabelType) e).getName();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setName((String) v);
        }
    }

    public static class EpgValue implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((LabelType) e).getValue();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setValue((String) v);
        }
    }

    public static class EpgIncludedPaths implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((LabelType) e).getIncludedPaths();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setIncludedPaths((String) v);
        }
    }

    public static class EpgExcludedPaths implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((LabelType) e).getExcludedPaths();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setExcludedPaths((String) v);
        }
    }

    public static class EpgSortOrder implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((LabelType) e).getSortOrder();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setSortOrder(cti(v));
        }
    }

    public static class EpgCreatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((LabelType) e).getCreatedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setCreatedBy((String) v);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((LabelType) e).getCreatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setCreatedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgUpdatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((LabelType) e).getUpdatedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setUpdatedBy((String) v);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((LabelType) e).getUpdatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setUpdatedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgDeletedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((LabelType) e).getDeletedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setDeletedBy((String) v);
        }
    }

    public static class EpgDeletedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((LabelType) e).getDeletedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setDeletedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgVersionNo implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((LabelType) e).getVersionNo();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((LabelType) e).setVersionNo(cti(v));
        }
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "LABEL_TYPE";

    protected final String _tablePropertyName = "labelType";

    protected final TableSqlName _tableSqlName = new TableSqlName("LABEL_TYPE",
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_ABD3A922_F562_4CF2_88C4_697DDDA4D94F",
            false,
            null,
            null,
            null,
            "dataConfigToLabelTypeMappingList,fileConfigToLabelTypeMappingList,labelTypeToRoleTypeMappingList,webConfigToLabelTypeMappingList",
            null);

    protected final ColumnInfo _columnName = cci("NAME", "NAME", null, null,
            true, "name", String.class, false, false, "VARCHAR", 100, 0, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnValue = cci("VALUE", "VALUE", null, null,
            true, "value", String.class, false, false, "VARCHAR", 20, 0, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnIncludedPaths = cci("INCLUDED_PATHS",
            "INCLUDED_PATHS", null, null, false, "includedPaths", String.class,
            false, false, "VARCHAR", 4000, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnExcludedPaths = cci("EXCLUDED_PATHS",
            "EXCLUDED_PATHS", null, null, false, "excludedPaths", String.class,
            false, false, "VARCHAR", 4000, 0, null, false, null, null, null,
            null, null);

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

    public ColumnInfo columnValue() {
        return _columnValue;
    }

    public ColumnInfo columnIncludedPaths() {
        return _columnIncludedPaths;
    }

    public ColumnInfo columnExcludedPaths() {
        return _columnExcludedPaths;
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
        ls.add(columnValue());
        ls.add(columnIncludedPaths());
        ls.add(columnExcludedPaths());
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
    public ReferrerInfo referrerDataConfigToLabelTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                DataConfigToLabelTypeMappingDbm.getInstance()
                        .columnLabelTypeId());
        return cri("CONSTRAINT_5CE1", "dataConfigToLabelTypeMappingList", this,
                DataConfigToLabelTypeMappingDbm.getInstance(), map, false,
                "labelType");
    }

    public ReferrerInfo referrerFileConfigToLabelTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                FileConfigToLabelTypeMappingDbm.getInstance()
                        .columnLabelTypeId());
        return cri("CONSTRAINT_F57", "fileConfigToLabelTypeMappingList", this,
                FileConfigToLabelTypeMappingDbm.getInstance(), map, false,
                "labelType");
    }

    public ReferrerInfo referrerLabelTypeToRoleTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                LabelTypeToRoleTypeMappingDbm.getInstance().columnLabelTypeId());
        return cri("CONSTRAINT_2C8", "labelTypeToRoleTypeMappingList", this,
                LabelTypeToRoleTypeMappingDbm.getInstance(), map, false,
                "labelType");
    }

    public ReferrerInfo referrerWebConfigToLabelTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                WebConfigToLabelTypeMappingDbm.getInstance()
                        .columnLabelTypeId());
        return cri("CONSTRAINT_6A", "webConfigToLabelTypeMappingList", this,
                WebConfigToLabelTypeMappingDbm.getInstance(), map, false,
                "labelType");
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
        return "jp.sf.fess.db.exentity.LabelType";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.LabelTypeCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.LabelTypeBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<LabelType> getEntityType() {
        return LabelType.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public LabelType newMyEntity() {
        return new LabelType();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((LabelType) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((LabelType) e, m);
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
