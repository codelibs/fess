/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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
import jp.sf.fess.db.exentity.SuggestElevateWord;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of SUGGEST_ELEVATE_WORD. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class SuggestElevateWordDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final SuggestElevateWordDbm _instance = new SuggestElevateWordDbm();

    private SuggestElevateWordDbm() {
    }

    public static SuggestElevateWordDbm getInstance() {
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
        setupEpg(_epgMap, new EpgSuggestWord(), "suggestWord");
        setupEpg(_epgMap, new EpgReading(), "reading");
        setupEpg(_epgMap, new EpgTargetRole(), "targetRole");
        setupEpg(_epgMap, new EpgTargetLabel(), "targetLabel");
        setupEpg(_epgMap, new EpgBoost(), "boost");
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
            return ((SuggestElevateWord) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setId(ctl(vl));
        }
    }

    public static class EpgSuggestWord implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SuggestElevateWord) et).getSuggestWord();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setSuggestWord((String) vl);
        }
    }

    public static class EpgReading implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SuggestElevateWord) et).getReading();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setReading((String) vl);
        }
    }

    public static class EpgTargetRole implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SuggestElevateWord) et).getTargetRole();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setTargetRole((String) vl);
        }
    }

    public static class EpgTargetLabel implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SuggestElevateWord) et).getTargetLabel();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setTargetLabel((String) vl);
        }
    }

    public static class EpgBoost implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SuggestElevateWord) et).getBoost();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setBoost(ctb(vl));
        }
    }

    public static class EpgCreatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SuggestElevateWord) et).getCreatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setCreatedBy((String) vl);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SuggestElevateWord) et).getCreatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setCreatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgUpdatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SuggestElevateWord) et).getUpdatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setUpdatedBy((String) vl);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SuggestElevateWord) et).getUpdatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setUpdatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgDeletedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SuggestElevateWord) et).getDeletedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setDeletedBy((String) vl);
        }
    }

    public static class EpgDeletedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SuggestElevateWord) et).getDeletedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setDeletedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgVersionNo implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((SuggestElevateWord) et).getVersionNo();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((SuggestElevateWord) et).setVersionNo(cti(vl));
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "SUGGEST_ELEVATE_WORD";

    protected final String _tablePropertyName = "suggestElevateWord";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "SUGGEST_ELEVATE_WORD", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_278A6A28_36F4_4786_9C84_87D820521EDC",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnSuggestWord = cci("SUGGEST_WORD",
            "SUGGEST_WORD", null, null, String.class, "suggestWord", null,
            false, false, true, "VARCHAR", 255, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnReading = cci("READING", "READING", null,
            null, String.class, "reading", null, false, false, false,
            "VARCHAR", 255, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnTargetRole = cci("TARGET_ROLE",
            "TARGET_ROLE", null, null, String.class, "targetRole", null, false,
            false, false, "VARCHAR", 255, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnTargetLabel = cci("TARGET_LABEL",
            "TARGET_LABEL", null, null, String.class, "targetLabel", null,
            false, false, false, "VARCHAR", 255, 0, null, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnBoost = cci("BOOST", "BOOST", null, null,
            java.math.BigDecimal.class, "boost", null, false, false, true,
            "DOUBLE", 17, 0, null, false, null, null, null, null, null);

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
     * SUGGEST_WORD: {NotNull, VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSuggestWord() {
        return _columnSuggestWord;
    }

    /**
     * READING: {VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnReading() {
        return _columnReading;
    }

    /**
     * TARGET_ROLE: {VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnTargetRole() {
        return _columnTargetRole;
    }

    /**
     * TARGET_LABEL: {VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnTargetLabel() {
        return _columnTargetLabel;
    }

    /**
     * BOOST: {NotNull, DOUBLE(17)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnBoost() {
        return _columnBoost;
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
        ls.add(columnSuggestWord());
        ls.add(columnReading());
        ls.add(columnTargetRole());
        ls.add(columnTargetLabel());
        ls.add(columnBoost());
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
        return "jp.sf.fess.db.exentity.SuggestElevateWord";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.SuggestElevateWordCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.SuggestElevateWordBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<SuggestElevateWord> getEntityType() {
        return SuggestElevateWord.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public SuggestElevateWord newEntity() {
        return new SuggestElevateWord();
    }

    public SuggestElevateWord newMyEntity() {
        return new SuggestElevateWord();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((SuggestElevateWord) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((SuggestElevateWord) et, mp);
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
