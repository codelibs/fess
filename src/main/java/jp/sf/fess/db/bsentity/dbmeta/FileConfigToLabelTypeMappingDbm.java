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
import jp.sf.fess.db.exentity.FileConfigToLabelTypeMapping;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of FILE_CONFIG_TO_LABEL_TYPE_MAPPING. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class FileConfigToLabelTypeMappingDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final FileConfigToLabelTypeMappingDbm _instance = new FileConfigToLabelTypeMappingDbm();

    private FileConfigToLabelTypeMappingDbm() {
    }

    public static FileConfigToLabelTypeMappingDbm getInstance() {
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
        setupEpg(_epgMap, new EpgFileConfigId(), "fileConfigId");
        setupEpg(_epgMap, new EpgLabelTypeId(), "labelTypeId");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileConfigToLabelTypeMapping) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileConfigToLabelTypeMapping) e).setId(ctl(v));
        }
    }

    public static class EpgFileConfigId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileConfigToLabelTypeMapping) e).getFileConfigId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileConfigToLabelTypeMapping) e).setFileConfigId(ctl(v));
        }
    }

    public static class EpgLabelTypeId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileConfigToLabelTypeMapping) e).getLabelTypeId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileConfigToLabelTypeMapping) e).setLabelTypeId(ctl(v));
        }
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "FILE_CONFIG_TO_LABEL_TYPE_MAPPING";

    protected final String _tablePropertyName = "fileConfigToLabelTypeMapping";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "FILE_CONFIG_TO_LABEL_TYPE_MAPPING", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_637A8DAA_5001_42B4_A128_49EC888DD83B",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnFileConfigId = cci("FILE_CONFIG_ID",
            "FILE_CONFIG_ID", null, null, true, "fileConfigId", Long.class,
            false, false, "BIGINT", 19, 0, null, false, null, null,
            "fileCrawlingConfig", null, null);

    protected final ColumnInfo _columnLabelTypeId = cci("LABEL_TYPE_ID",
            "LABEL_TYPE_ID", null, null, true, "labelTypeId", Long.class,
            false, false, "BIGINT", 19, 0, null, false, null, null,
            "labelType", null, null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnFileConfigId() {
        return _columnFileConfigId;
    }

    public ColumnInfo columnLabelTypeId() {
        return _columnLabelTypeId;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnFileConfigId());
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
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    public ForeignInfo foreignLabelType() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(
                columnLabelTypeId(), LabelTypeDbm.getInstance().columnId());
        return cfi("CONSTRAINT_F57", "labelType", this,
                LabelTypeDbm.getInstance(), map, 0, false, false, false, false,
                null, null, false, "fileConfigToLabelTypeMappingList");
    }

    public ForeignInfo foreignFileCrawlingConfig() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(
                columnFileConfigId(), FileCrawlingConfigDbm.getInstance()
                        .columnId());
        return cfi("CONSTRAINT_F57F", "fileCrawlingConfig", this,
                FileCrawlingConfigDbm.getInstance(), map, 1, false, false,
                false, false, null, null, false,
                "fileConfigToLabelTypeMappingList");
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
        return "jp.sf.fess.db.exentity.FileConfigToLabelTypeMapping";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.FileConfigToLabelTypeMappingCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.FileConfigToLabelTypeMappingBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<FileConfigToLabelTypeMapping> getEntityType() {
        return FileConfigToLabelTypeMapping.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public FileConfigToLabelTypeMapping newMyEntity() {
        return new FileConfigToLabelTypeMapping();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((FileConfigToLabelTypeMapping) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((FileConfigToLabelTypeMapping) e, m);
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
