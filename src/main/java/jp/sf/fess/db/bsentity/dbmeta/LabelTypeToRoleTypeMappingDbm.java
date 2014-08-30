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
import jp.sf.fess.db.exentity.LabelTypeToRoleTypeMapping;
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
 * The DB meta of LABEL_TYPE_TO_ROLE_TYPE_MAPPING. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class LabelTypeToRoleTypeMappingDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final LabelTypeToRoleTypeMappingDbm _instance = new LabelTypeToRoleTypeMappingDbm();

    private LabelTypeToRoleTypeMappingDbm() {
    }

    public static LabelTypeToRoleTypeMappingDbm getInstance() {
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
        setupEpg(_epgMap, new EpgLabelTypeId(), "labelTypeId");
        setupEpg(_epgMap, new EpgRoleTypeId(), "roleTypeId");
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelTypeToRoleTypeMapping) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelTypeToRoleTypeMapping) et).setId(ctl(vl));
        }
    }

    public static class EpgLabelTypeId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelTypeToRoleTypeMapping) et).getLabelTypeId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelTypeToRoleTypeMapping) et).setLabelTypeId(ctl(vl));
        }
    }

    public static class EpgRoleTypeId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelTypeToRoleTypeMapping) et).getRoleTypeId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelTypeToRoleTypeMapping) et).setRoleTypeId(ctl(vl));
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
        setupEfpg(_efpgMap, new EfpgLabelType(), "labelType");
        setupEfpg(_efpgMap, new EfpgRoleType(), "roleType");
    }

    public class EfpgLabelType implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelTypeToRoleTypeMapping) et).getLabelType();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelTypeToRoleTypeMapping) et).setLabelType((LabelType) vl);
        }
    }

    public class EfpgRoleType implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((LabelTypeToRoleTypeMapping) et).getRoleType();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((LabelTypeToRoleTypeMapping) et).setRoleType((RoleType) vl);
        }
    }

    @Override
    public PropertyGateway findForeignPropertyGateway(final String prop) {
        return doFindEfpg(_efpgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "LABEL_TYPE_TO_ROLE_TYPE_MAPPING";

    protected final String _tablePropertyName = "labelTypeToRoleTypeMapping";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "LABEL_TYPE_TO_ROLE_TYPE_MAPPING", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_EBC6D553_E53B_4789_8B97_BA3ECC49A013",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnLabelTypeId = cci("LABEL_TYPE_ID",
            "LABEL_TYPE_ID", null, null, Long.class, "labelTypeId", null,
            false, false, true, "BIGINT", 19, 0, null, false, null, null,
            "labelType", null, null);

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
     * LABEL_TYPE_ID: {IX, NotNull, BIGINT(19), FK to LABEL_TYPE}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnLabelTypeId() {
        return _columnLabelTypeId;
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
        ls.add(columnLabelTypeId());
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
     * LABEL_TYPE by my LABEL_TYPE_ID, named 'labelType'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignLabelType() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(
                columnLabelTypeId(), LabelTypeDbm.getInstance().columnId());
        return cfi("CONSTRAINT_2C", "labelType", this,
                LabelTypeDbm.getInstance(), mp, 0, null, false, false, false,
                false, null, null, false, "labelTypeToRoleTypeMappingList");
    }

    /**
     * ROLE_TYPE by my ROLE_TYPE_ID, named 'roleType'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignRoleType() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(
                columnRoleTypeId(), RoleTypeDbm.getInstance().columnId());
        return cfi("CONSTRAINT_2C8", "roleType", this,
                RoleTypeDbm.getInstance(), mp, 1, null, false, false, false,
                false, null, null, false, "labelTypeToRoleTypeMappingList");
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
        return "jp.sf.fess.db.exentity.LabelTypeToRoleTypeMapping";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.LabelTypeToRoleTypeMappingCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.LabelTypeToRoleTypeMappingBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<LabelTypeToRoleTypeMapping> getEntityType() {
        return LabelTypeToRoleTypeMapping.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public LabelTypeToRoleTypeMapping newEntity() {
        return new LabelTypeToRoleTypeMapping();
    }

    public LabelTypeToRoleTypeMapping newMyEntity() {
        return new LabelTypeToRoleTypeMapping();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((LabelTypeToRoleTypeMapping) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((LabelTypeToRoleTypeMapping) et, mp);
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
