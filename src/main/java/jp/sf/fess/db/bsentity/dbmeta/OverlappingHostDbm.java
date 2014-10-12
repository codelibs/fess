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
import jp.sf.fess.db.exentity.OverlappingHost;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of OVERLAPPING_HOST. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class OverlappingHostDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final OverlappingHostDbm _instance = new OverlappingHostDbm();

    private OverlappingHostDbm() {
    }

    public static OverlappingHostDbm getInstance() {
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
        setupEpg(_epgMap, new EpgRegularName(), "regularName");
        setupEpg(_epgMap, new EpgOverlappingName(), "overlappingName");
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
            return ((OverlappingHost) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((OverlappingHost) et).setId(ctl(vl));
        }
    }

    public static class EpgRegularName implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((OverlappingHost) et).getRegularName();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((OverlappingHost) et).setRegularName((String) vl);
        }
    }

    public static class EpgOverlappingName implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((OverlappingHost) et).getOverlappingName();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((OverlappingHost) et).setOverlappingName((String) vl);
        }
    }

    public static class EpgSortOrder implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((OverlappingHost) et).getSortOrder();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((OverlappingHost) et).setSortOrder(cti(vl));
        }
    }

    public static class EpgCreatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((OverlappingHost) et).getCreatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((OverlappingHost) et).setCreatedBy((String) vl);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((OverlappingHost) et).getCreatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((OverlappingHost) et).setCreatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgUpdatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((OverlappingHost) et).getUpdatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((OverlappingHost) et).setUpdatedBy((String) vl);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((OverlappingHost) et).getUpdatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((OverlappingHost) et).setUpdatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgDeletedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((OverlappingHost) et).getDeletedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((OverlappingHost) et).setDeletedBy((String) vl);
        }
    }

    public static class EpgDeletedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((OverlappingHost) et).getDeletedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((OverlappingHost) et).setDeletedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgVersionNo implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((OverlappingHost) et).getVersionNo();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((OverlappingHost) et).setVersionNo(cti(vl));
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "OVERLAPPING_HOST";

    protected final String _tablePropertyName = "overlappingHost";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "OVERLAPPING_HOST", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_7831BA07_C376_450B_BFBA_69716B6395E1",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnRegularName = cci("REGULAR_NAME",
            "REGULAR_NAME", null, null, String.class, "regularName", null,
            false, false, true, "VARCHAR", 1000, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnOverlappingName = cci("OVERLAPPING_NAME",
            "OVERLAPPING_NAME", null, null, String.class, "overlappingName",
            null, false, false, true, "VARCHAR", 1000, 0, null, false, null,
            null, null, null, null);

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
     * REGULAR_NAME: {IX+, NotNull, VARCHAR(1000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnRegularName() {
        return _columnRegularName;
    }

    /**
     * OVERLAPPING_NAME: {NotNull, VARCHAR(1000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnOverlappingName() {
        return _columnOverlappingName;
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
        ls.add(columnRegularName());
        ls.add(columnOverlappingName());
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
        return "jp.sf.fess.db.exentity.OverlappingHost";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.OverlappingHostCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.OverlappingHostBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<OverlappingHost> getEntityType() {
        return OverlappingHost.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public OverlappingHost newEntity() {
        return new OverlappingHost();
    }

    public OverlappingHost newMyEntity() {
        return new OverlappingHost();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((OverlappingHost) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((OverlappingHost) et, mp);
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
