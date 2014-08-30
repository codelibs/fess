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
import jp.sf.fess.db.exentity.FileConfigToRoleTypeMapping;
import jp.sf.fess.db.exentity.FileCrawlingConfig;
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
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    {
        setupEpg(_epgMap, new EpgId(), "id");
        setupEpg(_epgMap, new EpgFileConfigId(), "fileConfigId");
        setupEpg(_epgMap, new EpgRoleTypeId(), "roleTypeId");
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileConfigToRoleTypeMapping) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileConfigToRoleTypeMapping) et).setId(ctl(vl));
        }
    }

    public static class EpgFileConfigId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileConfigToRoleTypeMapping) et).getFileConfigId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileConfigToRoleTypeMapping) et).setFileConfigId(ctl(vl));
        }
    }

    public static class EpgRoleTypeId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileConfigToRoleTypeMapping) et).getRoleTypeId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileConfigToRoleTypeMapping) et).setRoleTypeId(ctl(vl));
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
        setupEfpg(_efpgMap, new EfpgFileCrawlingConfig(), "fileCrawlingConfig");
        setupEfpg(_efpgMap, new EfpgRoleType(), "roleType");
    }

    public class EfpgFileCrawlingConfig implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileConfigToRoleTypeMapping) et).getFileCrawlingConfig();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileConfigToRoleTypeMapping) et)
                    .setFileCrawlingConfig((FileCrawlingConfig) vl);
        }
    }

    public class EfpgRoleType implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileConfigToRoleTypeMapping) et).getRoleType();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileConfigToRoleTypeMapping) et).setRoleType((RoleType) vl);
        }
    }

    @Override
    public PropertyGateway findForeignPropertyGateway(final String prop) {
        return doFindEfpg(_efpgMap, prop);
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
            Long.class,
            "id",
            null,
            true,
            true,
            true,
            "BIGINT",
            19,
            0,
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_753BD317_B84F_44B6_804F_A15B41D531BD",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnFileConfigId = cci("FILE_CONFIG_ID",
            "FILE_CONFIG_ID", null, null, Long.class, "fileConfigId", null,
            false, false, true, "BIGINT", 19, 0, null, false, null, null,
            "fileCrawlingConfig", null, null);

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
     * FILE_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnFileConfigId() {
        return _columnFileConfigId;
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
    // cannot cache because it uses related DB meta instance while booting
    // (instead, cached by super's collection)
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    /**
     * FILE_CRAWLING_CONFIG by my FILE_CONFIG_ID, named 'fileCrawlingConfig'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignFileCrawlingConfig() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(
                columnFileConfigId(), FileCrawlingConfigDbm.getInstance()
                        .columnId());
        return cfi("CONSTRAINT_3A", "fileCrawlingConfig", this,
                FileCrawlingConfigDbm.getInstance(), mp, 0, null, false, false,
                false, false, null, null, false,
                "fileConfigToRoleTypeMappingList");
    }

    /**
     * ROLE_TYPE by my ROLE_TYPE_ID, named 'roleType'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignRoleType() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(
                columnRoleTypeId(), RoleTypeDbm.getInstance().columnId());
        return cfi("CONSTRAINT_3A9", "roleType", this,
                RoleTypeDbm.getInstance(), mp, 1, null, false, false, false,
                false, null, null, false, "fileConfigToRoleTypeMappingList");
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
    public FileConfigToRoleTypeMapping newEntity() {
        return new FileConfigToRoleTypeMapping();
    }

    public FileConfigToRoleTypeMapping newMyEntity() {
        return new FileConfigToRoleTypeMapping();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((FileConfigToRoleTypeMapping) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((FileConfigToRoleTypeMapping) et, mp);
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
