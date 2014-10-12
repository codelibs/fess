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
import jp.sf.fess.db.exentity.RoleType;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of ROLE_TYPE. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class RoleTypeDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final RoleTypeDbm _instance = new RoleTypeDbm();

    private RoleTypeDbm() {
    }

    public static RoleTypeDbm getInstance() {
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
            return ((RoleType) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((RoleType) et).setId(ctl(vl));
        }
    }

    public static class EpgName implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((RoleType) et).getName();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((RoleType) et).setName((String) vl);
        }
    }

    public static class EpgValue implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((RoleType) et).getValue();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((RoleType) et).setValue((String) vl);
        }
    }

    public static class EpgSortOrder implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((RoleType) et).getSortOrder();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((RoleType) et).setSortOrder(cti(vl));
        }
    }

    public static class EpgCreatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((RoleType) et).getCreatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((RoleType) et).setCreatedBy((String) vl);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((RoleType) et).getCreatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((RoleType) et).setCreatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgUpdatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((RoleType) et).getUpdatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((RoleType) et).setUpdatedBy((String) vl);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((RoleType) et).getUpdatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((RoleType) et).setUpdatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgDeletedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((RoleType) et).getDeletedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((RoleType) et).setDeletedBy((String) vl);
        }
    }

    public static class EpgDeletedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((RoleType) et).getDeletedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((RoleType) et).setDeletedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgVersionNo implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((RoleType) et).getVersionNo();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((RoleType) et).setVersionNo(cti(vl));
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "ROLE_TYPE";

    protected final String _tablePropertyName = "roleType";

    protected final TableSqlName _tableSqlName = new TableSqlName("ROLE_TYPE",
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_98A73057_DD05_44E8_BE5A_EB50044424D0",
            false,
            null,
            null,
            null,
            "dataConfigToRoleTypeMappingList,fileConfigToRoleTypeMappingList,labelTypeToRoleTypeMappingList,webConfigToRoleTypeMappingList",
            null);

    protected final ColumnInfo _columnName = cci("NAME", "NAME", null, null,
            String.class, "name", null, false, false, true, "VARCHAR", 100, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnValue = cci("VALUE", "VALUE", null, null,
            String.class, "value", null, false, false, true, "VARCHAR", 20, 0,
            null, false, null, null, null, null, null);

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
     * DATA_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'dataConfigToRoleTypeMappingList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerDataConfigToRoleTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                DataConfigToRoleTypeMappingDbm.getInstance().columnRoleTypeId());
        return cri("CONSTRAINT_E31", "dataConfigToRoleTypeMappingList", this,
                DataConfigToRoleTypeMappingDbm.getInstance(), mp, false,
                "roleType");
    }

    /**
     * FILE_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'fileConfigToRoleTypeMappingList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerFileConfigToRoleTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                FileConfigToRoleTypeMappingDbm.getInstance().columnRoleTypeId());
        return cri("CONSTRAINT_3A9", "fileConfigToRoleTypeMappingList", this,
                FileConfigToRoleTypeMappingDbm.getInstance(), mp, false,
                "roleType");
    }

    /**
     * LABEL_TYPE_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'labelTypeToRoleTypeMappingList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerLabelTypeToRoleTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                LabelTypeToRoleTypeMappingDbm.getInstance().columnRoleTypeId());
        return cri("CONSTRAINT_2C8", "labelTypeToRoleTypeMappingList", this,
                LabelTypeToRoleTypeMappingDbm.getInstance(), mp, false,
                "roleType");
    }

    /**
     * WEB_CONFIG_TO_ROLE_TYPE_MAPPING by ROLE_TYPE_ID, named 'webConfigToRoleTypeMappingList'.
     * @return The information object of referrer property. (NotNull)
     */
    public ReferrerInfo referrerWebConfigToRoleTypeMappingList() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(),
                WebConfigToRoleTypeMappingDbm.getInstance().columnRoleTypeId());
        return cri("CONSTRAINT_A17D5", "webConfigToRoleTypeMappingList", this,
                WebConfigToRoleTypeMappingDbm.getInstance(), mp, false,
                "roleType");
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
        return "jp.sf.fess.db.exentity.RoleType";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.RoleTypeCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.RoleTypeBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<RoleType> getEntityType() {
        return RoleType.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public RoleType newEntity() {
        return new RoleType();
    }

    public RoleType newMyEntity() {
        return new RoleType();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((RoleType) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((RoleType) et, mp);
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
