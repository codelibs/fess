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
import jp.sf.fess.db.exentity.DataConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.DataCrawlingConfig;
import jp.sf.fess.db.exentity.RoleType;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of DATA_CONFIG_TO_ROLE_TYPE_MAPPING. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class DataConfigToRoleTypeMappingDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final DataConfigToRoleTypeMappingDbm _instance = new DataConfigToRoleTypeMappingDbm();

    private DataConfigToRoleTypeMappingDbm() {
    }

    public static DataConfigToRoleTypeMappingDbm getInstance() {
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
        setupEpg(_epgMap, new EpgDataConfigId(), "dataConfigId");
        setupEpg(_epgMap, new EpgRoleTypeId(), "roleTypeId");
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((DataConfigToRoleTypeMapping) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((DataConfigToRoleTypeMapping) et).setId(ctl(vl));
        }
    }

    public static class EpgDataConfigId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((DataConfigToRoleTypeMapping) et).getDataConfigId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((DataConfigToRoleTypeMapping) et).setDataConfigId(ctl(vl));
        }
    }

    public static class EpgRoleTypeId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((DataConfigToRoleTypeMapping) et).getRoleTypeId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((DataConfigToRoleTypeMapping) et).setRoleTypeId(ctl(vl));
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
        setupEfpg(_efpgMap, new EfpgDataCrawlingConfig(), "dataCrawlingConfig");
        setupEfpg(_efpgMap, new EfpgRoleType(), "roleType");
    }

    public class EfpgDataCrawlingConfig implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((DataConfigToRoleTypeMapping) et).getDataCrawlingConfig();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((DataConfigToRoleTypeMapping) et)
                    .setDataCrawlingConfig((DataCrawlingConfig) vl);
        }
    }

    public class EfpgRoleType implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((DataConfigToRoleTypeMapping) et).getRoleType();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((DataConfigToRoleTypeMapping) et).setRoleType((RoleType) vl);
        }
    }

    @Override
    public PropertyGateway findForeignPropertyGateway(final String prop) {
        return doFindEfpg(_efpgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "DATA_CONFIG_TO_ROLE_TYPE_MAPPING";

    protected final String _tablePropertyName = "dataConfigToRoleTypeMapping";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "DATA_CONFIG_TO_ROLE_TYPE_MAPPING", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_E34A2367_6704_437E_9EE0_B5E3E3C8331D",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnDataConfigId = cci("DATA_CONFIG_ID",
            "DATA_CONFIG_ID", null, null, Long.class, "dataConfigId", null,
            false, false, true, "BIGINT", 19, 0, null, false, null, null,
            "dataCrawlingConfig", null, null);

    protected final ColumnInfo _columnRoleTypeId = cci("ROLE_TYPE_ID",
            "ROLE_TYPE_ID", null, null, Long.class, "roleTypeId", null, false,
            false, true, "BIGINT", 19, 0, null, false, null, null, "roleType",
            null, null);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * DATA_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to DATA_CRAWLING_CONFIG}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnDataConfigId() {
        return _columnDataConfigId;
    }

    /**
     * ROLE_TYPE_ID: {IX, NotNull, BIGINT(19), FK to ROLE_TYPE}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnRoleTypeId() {
        return _columnRoleTypeId;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnDataConfigId());
        ls.add(columnRoleTypeId());
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
     * DATA_CRAWLING_CONFIG by my DATA_CONFIG_ID, named 'dataCrawlingConfig'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignDataCrawlingConfig() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(
                columnDataConfigId(), DataCrawlingConfigDbm.getInstance()
                        .columnId());
        return cfi("CONSTRAINT_E3", "dataCrawlingConfig", this,
                DataCrawlingConfigDbm.getInstance(), mp, 0, null, false, false,
                false, false, null, null, false,
                "dataConfigToRoleTypeMappingList");
    }

    /**
     * ROLE_TYPE by my ROLE_TYPE_ID, named 'roleType'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignRoleType() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(
                columnRoleTypeId(), RoleTypeDbm.getInstance().columnId());
        return cfi("CONSTRAINT_E31", "roleType", this,
                RoleTypeDbm.getInstance(), mp, 1, null, false, false, false,
                false, null, null, false, "dataConfigToRoleTypeMappingList");
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
        return "jp.sf.fess.db.exentity.DataConfigToRoleTypeMapping";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.DataConfigToRoleTypeMappingCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.DataConfigToRoleTypeMappingBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<DataConfigToRoleTypeMapping> getEntityType() {
        return DataConfigToRoleTypeMapping.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public DataConfigToRoleTypeMapping newEntity() {
        return new DataConfigToRoleTypeMapping();
    }

    public DataConfigToRoleTypeMapping newMyEntity() {
        return new DataConfigToRoleTypeMapping();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((DataConfigToRoleTypeMapping) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((DataConfigToRoleTypeMapping) et, mp);
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
