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
import jp.sf.fess.db.exentity.FileConfigToRoleTypeMapping;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of FILE_CONFIG_TO_ROLE_TYPE_MAPPING. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class FileConfigToRoleTypeMappingDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final FileConfigToRoleTypeMappingDbm _instance = new FileConfigToRoleTypeMappingDbm();

    private FileConfigToRoleTypeMappingDbm() {
    }

    public static FileConfigToRoleTypeMappingDbm getInstance() {
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
        setupEpg(_epgMap, new EpgRoleTypeId(), "roleTypeId");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileConfigToRoleTypeMapping) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileConfigToRoleTypeMapping) e).setId(ctl(v));
        }
    }

    public static class EpgFileConfigId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileConfigToRoleTypeMapping) e).getFileConfigId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileConfigToRoleTypeMapping) e).setFileConfigId(ctl(v));
        }
    }

    public static class EpgRoleTypeId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileConfigToRoleTypeMapping) e).getRoleTypeId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileConfigToRoleTypeMapping) e).setRoleTypeId(ctl(v));
        }
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "FILE_CONFIG_TO_ROLE_TYPE_MAPPING";

    protected final String _tablePropertyName = "fileConfigToRoleTypeMapping";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "FILE_CONFIG_TO_ROLE_TYPE_MAPPING", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_3378BA5C_C67A_4A90_9235_4D81E1B7081A",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnFileConfigId = cci("FILE_CONFIG_ID",
            "FILE_CONFIG_ID", null, null, true, "fileConfigId", Long.class,
            false, false, "BIGINT", 19, 0, null, false, null, null,
            "fileCrawlingConfig", null, null);

    protected final ColumnInfo _columnRoleTypeId = cci("ROLE_TYPE_ID",
            "ROLE_TYPE_ID", null, null, true, "roleTypeId", Long.class, false,
            false, "BIGINT", 19, 0, null, false, null, null, "roleType", null,
            null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnFileConfigId() {
        return _columnFileConfigId;
    }

    public ColumnInfo columnRoleTypeId() {
        return _columnRoleTypeId;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnFileConfigId());
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
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    public ForeignInfo foreignFileCrawlingConfig() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(
                columnFileConfigId(), FileCrawlingConfigDbm.getInstance()
                        .columnId());
        return cfi("CONSTRAINT_3A", "fileCrawlingConfig", this,
                FileCrawlingConfigDbm.getInstance(), map, 0, false, false,
                false, false, null, null, false,
                "fileConfigToRoleTypeMappingList");
    }

    public ForeignInfo foreignRoleType() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(
                columnRoleTypeId(), RoleTypeDbm.getInstance().columnId());
        return cfi("CONSTRAINT_3A9", "roleType", this,
                RoleTypeDbm.getInstance(), map, 1, false, false, false, false,
                null, null, false, "fileConfigToRoleTypeMappingList");
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
        return "jp.sf.fess.db.exentity.FileConfigToRoleTypeMapping";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.FileConfigToRoleTypeMappingCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.FileConfigToRoleTypeMappingBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<FileConfigToRoleTypeMapping> getEntityType() {
        return FileConfigToRoleTypeMapping.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public FileConfigToRoleTypeMapping newMyEntity() {
        return new FileConfigToRoleTypeMapping();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((FileConfigToRoleTypeMapping) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((FileConfigToRoleTypeMapping) e, m);
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
