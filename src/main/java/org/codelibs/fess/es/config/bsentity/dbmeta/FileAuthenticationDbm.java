/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.es.config.bsentity.dbmeta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.config.exentity.FileAuthentication;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.dbflute.util.DfTypeUtil;

/**
 * @author ESFlute (using FreeGen)
 */
public class FileAuthenticationDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

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
    public String getProjectName() {
        return null;
    }

    @Override
    public String getProjectPrefix() {
        return null;
    }

    @Override
    public String getGenerationGapBasePrefix() {
        return null;
    }

    @Override
    public DBDef getCurrentDBDef() {
        return null;
    }

    // ===================================================================================
    //                                                                    Property Gateway
    //                                                                    ================
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    {
        setupEpg(_epgMap, et -> ((FileAuthentication) et).getCreatedBy(),
                (et, vl) -> ((FileAuthentication) et).setCreatedBy(DfTypeUtil.toString(vl)), "createdBy");
        setupEpg(_epgMap, et -> ((FileAuthentication) et).getCreatedTime(),
                (et, vl) -> ((FileAuthentication) et).setCreatedTime(DfTypeUtil.toLong(vl)), "createdTime");
        setupEpg(_epgMap, et -> ((FileAuthentication) et).getFileConfigId(),
                (et, vl) -> ((FileAuthentication) et).setFileConfigId(DfTypeUtil.toString(vl)), "fileConfigId");
        setupEpg(_epgMap, et -> ((FileAuthentication) et).getHostname(),
                (et, vl) -> ((FileAuthentication) et).setHostname(DfTypeUtil.toString(vl)), "hostname");
        setupEpg(_epgMap, et -> ((FileAuthentication) et).getParameters(),
                (et, vl) -> ((FileAuthentication) et).setParameters(DfTypeUtil.toString(vl)), "parameters");
        setupEpg(_epgMap, et -> ((FileAuthentication) et).getPassword(),
                (et, vl) -> ((FileAuthentication) et).setPassword(DfTypeUtil.toString(vl)), "password");
        setupEpg(_epgMap, et -> ((FileAuthentication) et).getPort(),
                (et, vl) -> ((FileAuthentication) et).setPort(DfTypeUtil.toInteger(vl)), "port");
        setupEpg(_epgMap, et -> ((FileAuthentication) et).getProtocolScheme(),
                (et, vl) -> ((FileAuthentication) et).setProtocolScheme(DfTypeUtil.toString(vl)), "protocolScheme");
        setupEpg(_epgMap, et -> ((FileAuthentication) et).getUpdatedBy(),
                (et, vl) -> ((FileAuthentication) et).setUpdatedBy(DfTypeUtil.toString(vl)), "updatedBy");
        setupEpg(_epgMap, et -> ((FileAuthentication) et).getUpdatedTime(),
                (et, vl) -> ((FileAuthentication) et).setUpdatedTime(DfTypeUtil.toLong(vl)), "updatedTime");
        setupEpg(_epgMap, et -> ((FileAuthentication) et).getUsername(),
                (et, vl) -> ((FileAuthentication) et).setUsername(DfTypeUtil.toString(vl)), "username");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "file_authentication";
    protected final String _tableDispName = "file_authentication";
    protected final String _tablePropertyName = "FileAuthentication";

    public String getTableDbName() {
        return _tableDbName;
    }

    @Override
    public String getTableDispName() {
        return _tableDispName;
    }

    @Override
    public String getTablePropertyName() {
        return _tablePropertyName;
    }

    @Override
    public TableSqlName getTableSqlName() {
        return null;
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnCreatedBy = cci("createdBy", "createdBy", null, null, String.class, "createdBy", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedTime = cci("createdTime", "createdTime", null, null, Long.class, "createdTime", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnFileConfigId = cci("fileConfigId", "fileConfigId", null, null, String.class, "fileConfigId", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHostname = cci("hostname", "hostname", null, null, String.class, "hostname", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnParameters = cci("parameters", "parameters", null, null, String.class, "parameters", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPassword = cci("password", "password", null, null, String.class, "password", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPort = cci("port", "port", null, null, Integer.class, "port", null, false, false, false, "Integer", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnProtocolScheme = cci("protocolScheme", "protocolScheme", null, null, String.class, "protocolScheme",
            null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedBy = cci("updatedBy", "updatedBy", null, null, String.class, "updatedBy", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedTime = cci("updatedTime", "updatedTime", null, null, Long.class, "updatedTime", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUsername = cci("username", "username", null, null, String.class, "username", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnFileConfigId() {
        return _columnFileConfigId;
    }

    public ColumnInfo columnHostname() {
        return _columnHostname;
    }

    public ColumnInfo columnParameters() {
        return _columnParameters;
    }

    public ColumnInfo columnPassword() {
        return _columnPassword;
    }

    public ColumnInfo columnPort() {
        return _columnPort;
    }

    public ColumnInfo columnProtocolScheme() {
        return _columnProtocolScheme;
    }

    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    public ColumnInfo columnUsername() {
        return _columnUsername;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnFileConfigId());
        ls.add(columnHostname());
        ls.add(columnParameters());
        ls.add(columnPassword());
        ls.add(columnPort());
        ls.add(columnProtocolScheme());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        ls.add(columnUsername());
        return ls;
    }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    @Override
    public boolean hasPrimaryKey() {
        return false;
    }

    @Override
    public boolean hasCompoundPrimaryKey() {
        return false;
    }

    @Override
    protected UniqueInfo cpui() {
        return null;
    }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    @Override
    public String getEntityTypeName() {
        return "org.codelibs.fess.es.config.exentity.FileAuthentication";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.FileAuthenticationCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.FileAuthenticationBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return FileAuthentication.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new FileAuthentication();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
    }

    @Override
    public void acceptAllColumnMap(Entity entity, Map<String, ? extends Object> allColumnMap) {
    }

    @Override
    public Map<String, Object> extractPrimaryKeyMap(Entity entity) {
        return null;
    }

    @Override
    public Map<String, Object> extractAllColumnMap(Entity entity) {
        return null;
    }
}
