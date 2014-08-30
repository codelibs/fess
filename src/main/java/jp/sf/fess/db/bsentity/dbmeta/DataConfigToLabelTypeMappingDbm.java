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
import jp.sf.fess.db.exentity.DataConfigToLabelTypeMapping;
import jp.sf.fess.db.exentity.DataCrawlingConfig;
import jp.sf.fess.db.exentity.LabelType;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of DATA_CONFIG_TO_LABEL_TYPE_MAPPING. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class DataConfigToLabelTypeMappingDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final DataConfigToLabelTypeMappingDbm _instance = new DataConfigToLabelTypeMappingDbm();

    private DataConfigToLabelTypeMappingDbm() {
    }

    public static DataConfigToLabelTypeMappingDbm getInstance() {
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
        setupEpg(_epgMap, new EpgLabelTypeId(), "labelTypeId");
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((DataConfigToLabelTypeMapping) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((DataConfigToLabelTypeMapping) et).setId(ctl(vl));
        }
    }

    public static class EpgDataConfigId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((DataConfigToLabelTypeMapping) et).getDataConfigId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((DataConfigToLabelTypeMapping) et).setDataConfigId(ctl(vl));
        }
    }

    public static class EpgLabelTypeId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((DataConfigToLabelTypeMapping) et).getLabelTypeId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((DataConfigToLabelTypeMapping) et).setLabelTypeId(ctl(vl));
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
        setupEfpg(_efpgMap, new EfpgLabelType(), "labelType");
    }

    public class EfpgDataCrawlingConfig implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((DataConfigToLabelTypeMapping) et).getDataCrawlingConfig();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((DataConfigToLabelTypeMapping) et)
                    .setDataCrawlingConfig((DataCrawlingConfig) vl);
        }
    }

    public class EfpgLabelType implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((DataConfigToLabelTypeMapping) et).getLabelType();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((DataConfigToLabelTypeMapping) et).setLabelType((LabelType) vl);
        }
    }

    @Override
    public PropertyGateway findForeignPropertyGateway(final String prop) {
        return doFindEfpg(_efpgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "DATA_CONFIG_TO_LABEL_TYPE_MAPPING";

    protected final String _tablePropertyName = "dataConfigToLabelTypeMapping";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "DATA_CONFIG_TO_LABEL_TYPE_MAPPING", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_9598A6F0_57BF_424A_99E2_BC2B484B294E",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnDataConfigId = cci("DATA_CONFIG_ID",
            "DATA_CONFIG_ID", null, null, Long.class, "dataConfigId", null,
            false, false, true, "BIGINT", 19, 0, null, false, null, null,
            "dataCrawlingConfig", null, null);

    protected final ColumnInfo _columnLabelTypeId = cci("LABEL_TYPE_ID",
            "LABEL_TYPE_ID", null, null, Long.class, "labelTypeId", null,
            false, false, true, "BIGINT", 19, 0, null, false, null, null,
            "labelType", null, null);

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
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnLabelTypeId() {
        return _columnLabelTypeId;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnDataConfigId());
        ls.add(columnLabelTypeId());
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
        return cfi("CONSTRAINT_5CE", "dataCrawlingConfig", this,
                DataCrawlingConfigDbm.getInstance(), mp, 0, null, false, false,
                false, false, null, null, false,
                "dataConfigToLabelTypeMappingList");
    }

    /**
     * LABEL_TYPE by my LABEL_TYPE_ID, named 'labelType'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignLabelType() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(
                columnLabelTypeId(), LabelTypeDbm.getInstance().columnId());
        return cfi("CONSTRAINT_5CE1", "labelType", this,
                LabelTypeDbm.getInstance(), mp, 1, null, false, false, false,
                false, null, null, false, "dataConfigToLabelTypeMappingList");
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
        return "jp.sf.fess.db.exentity.DataConfigToLabelTypeMapping";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.DataConfigToLabelTypeMappingCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.DataConfigToLabelTypeMappingBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<DataConfigToLabelTypeMapping> getEntityType() {
        return DataConfigToLabelTypeMapping.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public DataConfigToLabelTypeMapping newEntity() {
        return new DataConfigToLabelTypeMapping();
    }

    public DataConfigToLabelTypeMapping newMyEntity() {
        return new DataConfigToLabelTypeMapping();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((DataConfigToLabelTypeMapping) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((DataConfigToLabelTypeMapping) et, mp);
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
