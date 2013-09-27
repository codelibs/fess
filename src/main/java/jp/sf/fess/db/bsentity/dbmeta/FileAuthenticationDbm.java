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
import jp.sf.fess.db.exentity.FileAuthentication;

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

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setId(ctl(v));
        }
    }

    public static class EpgHostname implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getHostname();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setHostname((String) v);
        }
    }

    public static class EpgPort implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getPort();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setPort(cti(v));
        }
    }

    public static class EpgProtocolScheme implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getProtocolScheme();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setProtocolScheme((String) v);
        }
    }

    public static class EpgUsername implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getUsername();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setUsername((String) v);
        }
    }

    public static class EpgPassword implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getPassword();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setPassword((String) v);
        }
    }

    public static class EpgParameters implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getParameters();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setParameters((String) v);
        }
    }

    public static class EpgFileCrawlingConfigId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getFileCrawlingConfigId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setFileCrawlingConfigId(ctl(v));
        }
    }

    public static class EpgCreatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getCreatedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setCreatedBy((String) v);
        }
    }

    public static class EpgCreatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getCreatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setCreatedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgUpdatedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getUpdatedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setUpdatedBy((String) v);
        }
    }

    public static class EpgUpdatedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getUpdatedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setUpdatedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgDeletedBy implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getDeletedBy();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setDeletedBy((String) v);
        }
    }

    public static class EpgDeletedTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getDeletedTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setDeletedTime((java.sql.Timestamp) v);
        }
    }

    public static class EpgVersionNo implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((FileAuthentication) e).getVersionNo();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((FileAuthentication) e).setVersionNo(cti(v));
        }
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
            true,
            "id",
            Long.class,
            true,
            true,
            "BIGINT",
            19,
            0,
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_2D07A622_2874_4837_B16D_87D02816E51F",
            false, null, null, null, null, null);

    protected final ColumnInfo _columnHostname = cci("HOSTNAME", "HOSTNAME",
            null, null, false, "hostname", String.class, false, false,
            "VARCHAR", 255, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnPort = cci("PORT", "PORT", null, null,
            true, "port", Integer.class, false, false, "INTEGER", 10, 0, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnProtocolScheme = cci("PROTOCOL_SCHEME",
            "PROTOCOL_SCHEME", null, null, false, "protocolScheme",
            String.class, false, false, "VARCHAR", 10, 0, null, false, null,
            null, null, null, null);

    protected final ColumnInfo _columnUsername = cci("USERNAME", "USERNAME",
            null, null, true, "username", String.class, false, false,
            "VARCHAR", 100, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnPassword = cci("PASSWORD", "PASSWORD",
            null, null, false, "password", String.class, false, false,
            "VARCHAR", 100, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnParameters = cci("PARAMETERS",
            "PARAMETERS", null, null, false, "parameters", String.class, false,
            false, "VARCHAR", 1000, 0, null, false, null, null, null, null,
            null);

    protected final ColumnInfo _columnFileCrawlingConfigId = cci(
            "FILE_CRAWLING_CONFIG_ID", "FILE_CRAWLING_CONFIG_ID", null, null,
            true, "fileCrawlingConfigId", Long.class, false, false, "BIGINT",
            19, 0, null, false, null, null, "fileCrawlingConfig", null, null);

    protected final ColumnInfo _columnCreatedBy = cci("CREATED_BY",
            "CREATED_BY", null, null, true, "createdBy", String.class, false,
            false, "VARCHAR", 255, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnCreatedTime = cci("CREATED_TIME",
            "CREATED_TIME", null, null, true, "createdTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnUpdatedBy = cci("UPDATED_BY",
            "UPDATED_BY", null, null, false, "updatedBy", String.class, false,
            false, "VARCHAR", 255, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnUpdatedTime = cci("UPDATED_TIME",
            "UPDATED_TIME", null, null, false, "updatedTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnDeletedBy = cci("DELETED_BY",
            "DELETED_BY", null, null, false, "deletedBy", String.class, false,
            false, "VARCHAR", 255, 0, null, false, null, null, null, null, null);

    protected final ColumnInfo _columnDeletedTime = cci("DELETED_TIME",
            "DELETED_TIME", null, null, false, "deletedTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, null,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnVersionNo = cci("VERSION_NO",
            "VERSION_NO", null, null, true, "versionNo", Integer.class, false,
            false, "INTEGER", 10, 0, null, false,
            OptimisticLockType.VERSION_NO, null, null, null, null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnHostname() {
        return _columnHostname;
    }

    public ColumnInfo columnPort() {
        return _columnPort;
    }

    public ColumnInfo columnProtocolScheme() {
        return _columnProtocolScheme;
    }

    public ColumnInfo columnUsername() {
        return _columnUsername;
    }

    public ColumnInfo columnPassword() {
        return _columnPassword;
    }

    public ColumnInfo columnParameters() {
        return _columnParameters;
    }

    public ColumnInfo columnFileCrawlingConfigId() {
        return _columnFileCrawlingConfigId;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    public ColumnInfo columnDeletedBy() {
        return _columnDeletedBy;
    }

    public ColumnInfo columnDeletedTime() {
        return _columnDeletedTime;
    }

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
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    public ForeignInfo foreignFileCrawlingConfig() {
        final Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(
                columnFileCrawlingConfigId(), FileCrawlingConfigDbm
                        .getInstance().columnId());
        return cfi("CONSTRAINT_F90", "fileCrawlingConfig", this,
                FileCrawlingConfigDbm.getInstance(), map, 0, false, false,
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
    public Entity newEntity() {
        return newMyEntity();
    }

    public FileAuthentication newMyEntity() {
        return new FileAuthentication();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((FileAuthentication) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((FileAuthentication) e, m);
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
