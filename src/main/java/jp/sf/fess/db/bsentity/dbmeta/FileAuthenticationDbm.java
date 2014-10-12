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
import jp.sf.fess.db.exentity.FileAuthentication;
import jp.sf.fess.db.exentity.FileCrawlingConfig;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of FILE_AUTHENTICATION. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class FileAuthenticationDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final FileAuthenticationDbm _instance = new FileAuthenticationDbm();

    private FileAuthenticationDbm() {
    }

    public static FileAuthenticationDbm getInstance() {
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
        setupEpg(_epgMap, new EpgHostname(), "hostname");
        setupEpg(_epgMap, new EpgPort(), "port");
        setupEpg(_epgMap, new EpgProtocolScheme(), "protocolScheme");
        setupEpg(_epgMap, new EpgUsername(), "username");
        setupEpg(_epgMap, new EpgPassword(), "password");
        setupEpg(_epgMap, new EpgParameters(), "parameters");
        setupEpg(_epgMap, new EpgFileCrawlingConfigId(), "fileCrawlingConfigId");
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
            return ((FileAuthentication) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setId(ctl(vl));
        }
    }

    public static class EpgHostname implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getHostname();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setHostname((String) vl);
        }
    }

    public static class EpgPort implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getPort();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setPort(cti(vl));
        }
    }

    public static class EpgProtocolScheme implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getProtocolScheme();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setProtocolScheme((String) vl);
        }
    }

    public static class EpgUsername implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getUsername();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setUsername((String) vl);
        }
    }

    public static class EpgPassword implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getPassword();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setPassword((String) vl);
        }
    }

    public static class EpgParameters implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getParameters();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setParameters((String) vl);
        }
    }

    public static class EpgFileCrawlingConfigId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getFileCrawlingConfigId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setFileCrawlingConfigId(ctl(vl));
        }
    }

    public static class EpgCreatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getCreatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setCreatedBy((String) vl);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getCreatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setCreatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgUpdatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getUpdatedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setUpdatedBy((String) vl);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getUpdatedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setUpdatedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgDeletedBy implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getDeletedBy();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setDeletedBy((String) vl);
        }
    }

    public static class EpgDeletedTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getDeletedTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setDeletedTime((java.sql.Timestamp) vl);
        }
    }

    public static class EpgVersionNo implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getVersionNo();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et).setVersionNo(cti(vl));
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
    }

    public class EfpgFileCrawlingConfig implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((FileAuthentication) et).getFileCrawlingConfig();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((FileAuthentication) et)
                    .setFileCrawlingConfig((FileCrawlingConfig) vl);
        }
    }

    @Override
    public PropertyGateway findForeignPropertyGateway(final String prop) {
        return doFindEfpg(_efpgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "FILE_AUTHENTICATION";

    protected final String _tablePropertyName = "fileAuthentication";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "FILE_AUTHENTICATION", _tableDbName);
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
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_57E14B65_6683_4409_9BC0_4CCB7DFFB677",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnHostname = cci("HOSTNAME", "HOSTNAME",
            null, null, String.class, "hostname", null, false, false, false,
            "VARCHAR", 255, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnPort = cci("PORT", "PORT", null, null,
            Integer.class, "port", null, false, false, true, "INTEGER", 10, 0,
            null, false, null, null, null, null, null);

    protected final ColumnInfo _columnProtocolScheme = cci("PROTOCOL_SCHEME",
            "PROTOCOL_SCHEME", null, null, String.class, "protocolScheme",
            null, false, false, false, "VARCHAR", 10, 0, null, false, null,
            null, null, null, null);

    protected final ColumnInfo _columnUsername = cci("USERNAME", "USERNAME",
            null, null, String.class, "username", null, false, false, true,
            "VARCHAR", 100, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnPassword = cci("PASSWORD", "PASSWORD",
            null, null, String.class, "password", null, false, false, false,
            "VARCHAR", 100, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnParameters = cci("PARAMETERS",
            "PARAMETERS", null, null, String.class, "parameters", null, false,
            false, false, "VARCHAR", 1000, 0, null, false, null, null, null,
            null, null);

    protected final ColumnInfo _columnFileCrawlingConfigId = cci(
            "FILE_CRAWLING_CONFIG_ID", "FILE_CRAWLING_CONFIG_ID", null, null,
            Long.class, "fileCrawlingConfigId", null, false, false, true,
            "BIGINT", 19, 0, null, false, null, null, "fileCrawlingConfig",
            null, null);

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
     * HOSTNAME: {VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnHostname() {
        return _columnHostname;
    }

    /**
     * PORT: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnPort() {
        return _columnPort;
    }

    /**
     * PROTOCOL_SCHEME: {VARCHAR(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnProtocolScheme() {
        return _columnProtocolScheme;
    }

    /**
     * USERNAME: {NotNull, VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUsername() {
        return _columnUsername;
    }

    /**
     * PASSWORD: {VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnPassword() {
        return _columnPassword;
    }

    /**
     * PARAMETERS: {VARCHAR(1000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnParameters() {
        return _columnParameters;
    }

    /**
     * FILE_CRAWLING_CONFIG_ID: {IX, NotNull, BIGINT(19), FK to FILE_CRAWLING_CONFIG}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnFileCrawlingConfigId() {
        return _columnFileCrawlingConfigId;
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
        ls.add(columnHostname());
        ls.add(columnPort());
        ls.add(columnProtocolScheme());
        ls.add(columnUsername());
        ls.add(columnPassword());
        ls.add(columnParameters());
        ls.add(columnFileCrawlingConfigId());
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
    /**
     * FILE_CRAWLING_CONFIG by my FILE_CRAWLING_CONFIG_ID, named 'fileCrawlingConfig'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignFileCrawlingConfig() {
        final Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(
                columnFileCrawlingConfigId(), FileCrawlingConfigDbm
                        .getInstance().columnId());
        return cfi("CONSTRAINT_F90", "fileCrawlingConfig", this,
                FileCrawlingConfigDbm.getInstance(), mp, 0, null, false, false,
                false, false, null, null, false, "fileAuthenticationList");
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
        return "jp.sf.fess.db.exentity.FileAuthentication";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "jp.sf.fess.db.cbean.FileAuthenticationCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "jp.sf.fess.db.exbhv.FileAuthenticationBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<FileAuthentication> getEntityType() {
        return FileAuthentication.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public FileAuthentication newEntity() {
        return new FileAuthentication();
    }

    public FileAuthentication newMyEntity() {
        return new FileAuthentication();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((FileAuthentication) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((FileAuthentication) et, mp);
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
