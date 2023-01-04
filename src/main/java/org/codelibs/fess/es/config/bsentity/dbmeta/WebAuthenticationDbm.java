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

import org.codelibs.fess.es.config.exentity.WebAuthentication;
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
public class WebAuthenticationDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final WebAuthenticationDbm _instance = new WebAuthenticationDbm();

    private WebAuthenticationDbm() {
    }

    public static WebAuthenticationDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getAuthRealm(),
                (et, vl) -> ((WebAuthentication) et).setAuthRealm(DfTypeUtil.toString(vl)), "authRealm");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getCreatedBy(),
                (et, vl) -> ((WebAuthentication) et).setCreatedBy(DfTypeUtil.toString(vl)), "createdBy");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getCreatedTime(),
                (et, vl) -> ((WebAuthentication) et).setCreatedTime(DfTypeUtil.toLong(vl)), "createdTime");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getHostname(),
                (et, vl) -> ((WebAuthentication) et).setHostname(DfTypeUtil.toString(vl)), "hostname");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getParameters(),
                (et, vl) -> ((WebAuthentication) et).setParameters(DfTypeUtil.toString(vl)), "parameters");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getPassword(),
                (et, vl) -> ((WebAuthentication) et).setPassword(DfTypeUtil.toString(vl)), "password");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getPort(), (et, vl) -> ((WebAuthentication) et).setPort(DfTypeUtil.toInteger(vl)),
                "port");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getProtocolScheme(),
                (et, vl) -> ((WebAuthentication) et).setProtocolScheme(DfTypeUtil.toString(vl)), "protocolScheme");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getUpdatedBy(),
                (et, vl) -> ((WebAuthentication) et).setUpdatedBy(DfTypeUtil.toString(vl)), "updatedBy");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getUpdatedTime(),
                (et, vl) -> ((WebAuthentication) et).setUpdatedTime(DfTypeUtil.toLong(vl)), "updatedTime");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getUsername(),
                (et, vl) -> ((WebAuthentication) et).setUsername(DfTypeUtil.toString(vl)), "username");
        setupEpg(_epgMap, et -> ((WebAuthentication) et).getWebConfigId(),
                (et, vl) -> ((WebAuthentication) et).setWebConfigId(DfTypeUtil.toString(vl)), "webConfigId");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "web_authentication";
    protected final String _tableDispName = "web_authentication";
    protected final String _tablePropertyName = "WebAuthentication";

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
    protected final ColumnInfo _columnAuthRealm = cci("authRealm", "authRealm", null, null, String.class, "authRealm", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedBy = cci("createdBy", "createdBy", null, null, String.class, "createdBy", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedTime = cci("createdTime", "createdTime", null, null, Long.class, "createdTime", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
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
    protected final ColumnInfo _columnWebConfigId = cci("webConfigId", "webConfigId", null, null, String.class, "webConfigId", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnAuthRealm() {
        return _columnAuthRealm;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
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

    public ColumnInfo columnWebConfigId() {
        return _columnWebConfigId;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnAuthRealm());
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnHostname());
        ls.add(columnParameters());
        ls.add(columnPassword());
        ls.add(columnPort());
        ls.add(columnProtocolScheme());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        ls.add(columnUsername());
        ls.add(columnWebConfigId());
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
        return "org.codelibs.fess.es.config.exentity.WebAuthentication";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.WebAuthenticationCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.WebAuthenticationBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return WebAuthentication.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new WebAuthentication();
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
