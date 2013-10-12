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
import jp.sf.fess.db.exentity.DataConfigToBrowserTypeMapping;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of DATA_CONFIG_TO_BROWSER_TYPE_MAPPING. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class DataConfigToBrowserTypeMappingDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final DataConfigToBrowserTypeMappingDbm _instance = new DataConfigToBrowserTypeMappingDbm();

    private DataConfigToBrowserTypeMappingDbm() {
    }

    public static DataConfigToBrowserTypeMappingDbm getInstance() {
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
        setupEpg(_epgMap, new EpgDataConfigId(), "dataConfigId");
        setupEpg(_epgMap, new EpgBrowserTypeId(), "browserTypeId");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataConfigToBrowserTypeMapping) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataConfigToBrowserTypeMapping) e).setId(ctl(v));
        }
    }

    public static class EpgDataConfigId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataConfigToBrowserTypeMapping) e).getDataConfigId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataConfigToBrowserTypeMapping) e).setDataConfigId(ctl(v));
        }
    }

    public static class EpgBrowserTypeId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((DataConfigToBrowserTypeMapping) e).getBrowserTypeId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((DataConfigToBrowserTypeMapping) e).setBrowserTypeId(ctl(v));
        }
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "DATA_CONFIG_TO_BROWSER_TYPE_MAPPING";

    protected final String _tablePropertyName = "dataConfigToBrowserTypeMapping";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "DATA_CONFIG_TO_BROWSER_TYPE_MAPPING", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_2B76FBE0_224F_4AD1_85FD_41B4C9AEDC31",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnDataConfigId = cci("DATA_CONFIG_ID",
            "DATA_CONFIG_ID", null, null, true, "dataConfigId", Long.class,
            false, false, "BIGINT", 19, 0, null, false, null, null,
            "dataCrawlingConfig", null, null);

    protected final ColumnInfo _columnBrowserTypeId = cci("BROWSER_TYPE_ID",
            "BROWSER_TYPE_ID", null, null, true, "browserTypeId", Long.class,
            false, false, "BIGINT", 19, 0, null, false, null, null,
            "browserType", null, null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnDataConfigId() {
        return _columnDataConfigId;
    }

    public ColumnInfo columnBrowserTypeId() {
        return _columnBrowserTypeId;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnDataConfigId());
        ls.add(columnBrowserTypeId());
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
    public ForeignInfo foreignDataCrawlingConfig() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(
                columnDataConfigId(), DataCrawlingConfigDbm.getInstance()
                        .columnId());
        return cfi("CONSTRAINT_FA09", "dataCrawlingConfig", this,
                DataCrawlingConfigDbm.getInstance(), map, 0, false, false,
                false, false, null, null, false,
                "dataConfigToBrowserTypeMappingList");
    }

    public ForeignInfo foreignBrowserType() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(
                columnBrowserTypeId(), BrowserTypeDbm.getInstance().columnId());
        return cfi("CONSTRAINT_FA09C", "browserType", this,
                BrowserTypeDbm.getInstance(), map, 1, false, false, false,
                false, null, null, false, "dataConfigToBrowserTypeMappingList");
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
        return "jp.sf.fess.db.exentity.DataConfigToBrowserTypeMapping";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.DataConfigToBrowserTypeMappingCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.DataConfigToBrowserTypeMappingBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<DataConfigToBrowserTypeMapping> getEntityType() {
        return DataConfigToBrowserTypeMapping.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public DataConfigToBrowserTypeMapping newMyEntity() {
        return new DataConfigToBrowserTypeMapping();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((DataConfigToBrowserTypeMapping) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((DataConfigToBrowserTypeMapping) e, m);
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
