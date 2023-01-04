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

import org.codelibs.fess.es.config.exentity.DataConfig;
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
public class DataConfigDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final DataConfigDbm _instance = new DataConfigDbm();

    private DataConfigDbm() {
    }

    public static DataConfigDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((DataConfig) et).getAvailable(), (et, vl) -> ((DataConfig) et).setAvailable(DfTypeUtil.toBoolean(vl)),
                "available");
        setupEpg(_epgMap, et -> ((DataConfig) et).getBoost(), (et, vl) -> ((DataConfig) et).setBoost(DfTypeUtil.toFloat(vl)), "boost");
        setupEpg(_epgMap, et -> ((DataConfig) et).getCreatedBy(), (et, vl) -> ((DataConfig) et).setCreatedBy(DfTypeUtil.toString(vl)),
                "createdBy");
        setupEpg(_epgMap, et -> ((DataConfig) et).getCreatedTime(), (et, vl) -> ((DataConfig) et).setCreatedTime(DfTypeUtil.toLong(vl)),
                "createdTime");
        setupEpg(_epgMap, et -> ((DataConfig) et).getDescription(), (et, vl) -> ((DataConfig) et).setDescription(DfTypeUtil.toString(vl)),
                "description");
        setupEpg(_epgMap, et -> ((DataConfig) et).getHandlerName(), (et, vl) -> ((DataConfig) et).setHandlerName(DfTypeUtil.toString(vl)),
                "handlerName");
        setupEpg(_epgMap, et -> ((DataConfig) et).getHandlerParameter(),
                (et, vl) -> ((DataConfig) et).setHandlerParameter(DfTypeUtil.toString(vl)), "handlerParameter");
        setupEpg(_epgMap, et -> ((DataConfig) et).getHandlerScript(),
                (et, vl) -> ((DataConfig) et).setHandlerScript(DfTypeUtil.toString(vl)), "handlerScript");
        setupEpg(_epgMap, et -> ((DataConfig) et).getName(), (et, vl) -> ((DataConfig) et).setName(DfTypeUtil.toString(vl)), "name");
        setupEpg(_epgMap, et -> ((DataConfig) et).getPermissions(), (et, vl) -> ((DataConfig) et).setPermissions((String[]) vl),
                "permissions");
        setupEpg(_epgMap, et -> ((DataConfig) et).getSortOrder(), (et, vl) -> ((DataConfig) et).setSortOrder(DfTypeUtil.toInteger(vl)),
                "sortOrder");
        setupEpg(_epgMap, et -> ((DataConfig) et).getUpdatedBy(), (et, vl) -> ((DataConfig) et).setUpdatedBy(DfTypeUtil.toString(vl)),
                "updatedBy");
        setupEpg(_epgMap, et -> ((DataConfig) et).getUpdatedTime(), (et, vl) -> ((DataConfig) et).setUpdatedTime(DfTypeUtil.toLong(vl)),
                "updatedTime");
        setupEpg(_epgMap, et -> ((DataConfig) et).getVirtualHosts(), (et, vl) -> ((DataConfig) et).setVirtualHosts((String[]) vl),
                "virtualHosts");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "data_config";
    protected final String _tableDispName = "data_config";
    protected final String _tablePropertyName = "DataConfig";

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
    protected final ColumnInfo _columnAvailable = cci("available", "available", null, null, Boolean.class, "available", null, false, false,
            false, "Boolean", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnBoost = cci("boost", "boost", null, null, Float.class, "boost", null, false, false, false, "Float", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedBy = cci("createdBy", "createdBy", null, null, String.class, "createdBy", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedTime = cci("createdTime", "createdTime", null, null, Long.class, "createdTime", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnDescription = cci("description", "description", null, null, String.class, "description", null, false,
            false, false, "text", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHandlerName = cci("handlerName", "handlerName", null, null, String.class, "handlerName", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHandlerParameter = cci("handlerParameter", "handlerParameter", null, null, String.class,
            "handlerParameter", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHandlerScript = cci("handlerScript", "handlerScript", null, null, String.class, "handlerScript", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnName = cci("name", "name", null, null, String.class, "name", null, false, false, false, "keyword", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPermissions = cci("permissions", "permissions", null, null, String[].class, "permissions", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSortOrder = cci("sortOrder", "sortOrder", null, null, Integer.class, "sortOrder", null, false, false,
            false, "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedBy = cci("updatedBy", "updatedBy", null, null, String.class, "updatedBy", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedTime = cci("updatedTime", "updatedTime", null, null, Long.class, "updatedTime", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnVirtualHosts = cci("virtualHosts", "virtualHosts", null, null, String[].class, "virtualHosts", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnAvailable() {
        return _columnAvailable;
    }

    public ColumnInfo columnBoost() {
        return _columnBoost;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnDescription() {
        return _columnDescription;
    }

    public ColumnInfo columnHandlerName() {
        return _columnHandlerName;
    }

    public ColumnInfo columnHandlerParameter() {
        return _columnHandlerParameter;
    }

    public ColumnInfo columnHandlerScript() {
        return _columnHandlerScript;
    }

    public ColumnInfo columnName() {
        return _columnName;
    }

    public ColumnInfo columnPermissions() {
        return _columnPermissions;
    }

    public ColumnInfo columnSortOrder() {
        return _columnSortOrder;
    }

    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    public ColumnInfo columnVirtualHosts() {
        return _columnVirtualHosts;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnAvailable());
        ls.add(columnBoost());
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnDescription());
        ls.add(columnHandlerName());
        ls.add(columnHandlerParameter());
        ls.add(columnHandlerScript());
        ls.add(columnName());
        ls.add(columnPermissions());
        ls.add(columnSortOrder());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        ls.add(columnVirtualHosts());
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
        return "org.codelibs.fess.es.config.exentity.DataConfig";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.DataConfigCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.DataConfigBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return DataConfig.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new DataConfig();
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
