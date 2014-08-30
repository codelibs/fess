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
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
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

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setId(ctl(vl));
        }
    }

    public static class EpgName implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getName();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setName((String) vl);
        }
    }

    public static class EpgValue implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getValue();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setValue((String) vl);
        }
    }

    public static class EpgIncludedPaths implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getIncludedPaths();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setIncludedPaths((String) vl);
        }
    }

    public static class EpgExcludedPaths implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getExcludedPaths();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setExcludedPaths((String) vl);
        }
    }

    public static class EpgSortOrder implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getSortOrder();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setSortOrder(cti(vl));
        }
    }

    public static class EpgCreatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getCreatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setCreatedBy((String) vl);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getCreatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setCreatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgUpdatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getUpdatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setUpdatedBy((String) vl);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getUpdatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setUpdatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgDeletedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getDeletedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setDeletedBy((String) vl);
        }
    }

    public static class EpgDeletedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getDeletedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setDeletedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgVersionNo implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelType) et).getVersionNo();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelType) et).setVersionNo(cti(vl));
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
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
            Long.class,
            "id",
            null,
            true,
            true,
            true,
            "BIGINT",
            19,
            0,
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_39EA4F20_1968_4E93_9F48_EDD8DD9A25B2",
            false,
            null,
            null,
            null,
            "dataConfigToLabelTypeMappingList,fileConfigToLabelTypeMappingList,labelTypeToRoleTypeMappingList,webConfigToLabelTypeMappingList",
            null);

    protected final ColumnInfo _columnName = cci("NAME", "NAME", null, null,
            String.class, "name", null, false, false, true, "VARCHAR", 100, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnValue = cci("VALUE", "VALUE", null, null,
            String.class, "value", null, false, false, true, "VARCHAR", 20, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnIncludedPaths = cci("INCLUDED_PATHS",
            "INCLUDED_PATHS", null, null, String.class, "includedPaths", null,
            false, false, false, "VARCHAR", 4000, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnExcludedPaths = cci("EXCLUDED_PATHS",
            "EXCLUDED_PATHS", null, null, String.class, "excludedPaths", null,
            false, false, false, "VARCHAR", 4000, 0, null, false, null, null,
            null, null, null);

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
     * NAME: {NotNull, VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnName() {
        return _columnName;
    }

    /**
     * VALUE: {NotNull, VARCHAR(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnValue() {
        return _columnValue;
    }

    /**
     * INCLUDED_PATHS: {VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnIncludedPaths() {
        return _columnIncludedPaths;
    }

    /**
     * EXCLUDED_PATHS: {VARCHAR(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnExcludedPaths() {
        return _columnExcludedPaths;
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
    // cannot cache because it uses related DB meta instance while booting
    // (instead, cached by super's collection)
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------
    /**
     * DATA_CONFIG_TO_LABEL_TYPE_MAPPING by LABEL_TYPE_ID, named 'dataConfigToLabelTypeMappingList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerDataConfigToLabelTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                DataConfigToLabelTypeMappingDbm.getInstance()
                        .columnLabelTypeId());
        return cri("CONSTRAINT_5CE1", "dataConfigToLabelTypeMappingList", this,
                DataConfigToLabelTypeMappingDbm.getInstance(), mp, false,
                "labelType");
    }

    /**
     * FILE_CONFIG_TO_LABEL_TYPE_MAPPING by LABEL_TYPE_ID, named 'fileConfigToLabelTypeMappingList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerFileConfigToLabelTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                FileConfigToLabelTypeMappingDbm.getInstance()
                        .columnLabelTypeId());
        return cri("CONSTRAINT_F57", "fileConfigToLabelTypeMappingList", this,
                FileConfigToLabelTypeMappingDbm.getInstance(), mp, false,
                "labelType");
    }

    /**
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by LABEL_TYPE_ID, named 'labelTypeToRoleTypeMappingList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerLabelTypeToRoleTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                LabelTypeToRoleTypeMappingDbm.getInstance().columnLabelTypeId());
        return cri("CONSTRAINT_2C", "labelTypeToRoleTypeMappingList", this,
                LabelTypeToRoleTypeMappingDbm.getInstance(), mp, false,
                "labelType");
    }

    /**
     * WEB_CONFIG_TO_LABEL_TYPE_MAPPING by LABEL_TYPE_ID, named 'webConfigToLabelTypeMappingList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerWebConfigToLabelTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                WebConfigToLabelTypeMappingDbm.getInstance()
                        .columnLabelTypeId());
        return cri("CONSTRAINT_6A", "webConfigToLabelTypeMappingList", this,
                WebConfigToLabelTypeMappingDbm.getInstance(), mp, false,
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
    public LabelType newEntity() {
        return new LabelType();
    }

    public LabelType newMyEntity() {
        return new LabelType();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((LabelType) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((LabelType) et, mp);
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
