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

import org.codelibs.fess.es.config.exentity.AccessToken;
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
public class AccessTokenDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final AccessTokenDbm _instance = new AccessTokenDbm();

    private AccessTokenDbm() {
    }

    public static AccessTokenDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((AccessToken) et).getCreatedBy(), (et, vl) -> ((AccessToken) et).setCreatedBy(DfTypeUtil.toString(vl)),
                "createdBy");
        setupEpg(_epgMap, et -> ((AccessToken) et).getCreatedTime(), (et, vl) -> ((AccessToken) et).setCreatedTime(DfTypeUtil.toLong(vl)),
                "createdTime");
        setupEpg(_epgMap, et -> ((AccessToken) et).getExpiredTime(), (et, vl) -> ((AccessToken) et).setExpiredTime(DfTypeUtil.toLong(vl)),
                "expiredTime");
        setupEpg(_epgMap, et -> ((AccessToken) et).getName(), (et, vl) -> ((AccessToken) et).setName(DfTypeUtil.toString(vl)), "name");
        setupEpg(_epgMap, et -> ((AccessToken) et).getParameterName(),
                (et, vl) -> ((AccessToken) et).setParameterName(DfTypeUtil.toString(vl)), "parameterName");
        setupEpg(_epgMap, et -> ((AccessToken) et).getPermissions(), (et, vl) -> ((AccessToken) et).setPermissions((String[]) vl),
                "permissions");
        setupEpg(_epgMap, et -> ((AccessToken) et).getToken(), (et, vl) -> ((AccessToken) et).setToken(DfTypeUtil.toString(vl)), "token");
        setupEpg(_epgMap, et -> ((AccessToken) et).getUpdatedBy(), (et, vl) -> ((AccessToken) et).setUpdatedBy(DfTypeUtil.toString(vl)),
                "updatedBy");
        setupEpg(_epgMap, et -> ((AccessToken) et).getUpdatedTime(), (et, vl) -> ((AccessToken) et).setUpdatedTime(DfTypeUtil.toLong(vl)),
                "updatedTime");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "access_token";
    protected final String _tableDispName = "access_token";
    protected final String _tablePropertyName = "AccessToken";

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
    protected final ColumnInfo _columnExpiredTime = cci("expiredTime", "expiredTime", null, null, Long.class, "expiredTime", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnName = cci("name", "name", null, null, String.class, "name", null, false, false, false, "keyword", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnParameterName = cci("parameter_name", "parameter_name", null, null, String.class, "parameterName",
            null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPermissions = cci("permissions", "permissions", null, null, String[].class, "permissions", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnToken = cci("token", "token", null, null, String.class, "token", null, false, false, false, "keyword",
            0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedBy = cci("updatedBy", "updatedBy", null, null, String.class, "updatedBy", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedTime = cci("updatedTime", "updatedTime", null, null, Long.class, "updatedTime", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnExpiredTime() {
        return _columnExpiredTime;
    }

    public ColumnInfo columnName() {
        return _columnName;
    }

    public ColumnInfo columnParameterName() {
        return _columnParameterName;
    }

    public ColumnInfo columnPermissions() {
        return _columnPermissions;
    }

    public ColumnInfo columnToken() {
        return _columnToken;
    }

    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnExpiredTime());
        ls.add(columnName());
        ls.add(columnParameterName());
        ls.add(columnPermissions());
        ls.add(columnToken());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
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
        return "org.codelibs.fess.es.config.exentity.AccessToken";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.AccessTokenCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.AccessTokenBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return AccessToken.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new AccessToken();
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
