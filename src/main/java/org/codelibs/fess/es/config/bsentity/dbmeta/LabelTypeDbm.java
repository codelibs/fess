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

import org.codelibs.fess.es.config.exentity.LabelType;
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
public class LabelTypeDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final LabelTypeDbm _instance = new LabelTypeDbm();

    private LabelTypeDbm() {
    }

    public static LabelTypeDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((LabelType) et).getCreatedBy(), (et, vl) -> ((LabelType) et).setCreatedBy(DfTypeUtil.toString(vl)),
                "createdBy");
        setupEpg(_epgMap, et -> ((LabelType) et).getCreatedTime(), (et, vl) -> ((LabelType) et).setCreatedTime(DfTypeUtil.toLong(vl)),
                "createdTime");
        setupEpg(_epgMap, et -> ((LabelType) et).getExcludedPaths(), (et, vl) -> ((LabelType) et).setExcludedPaths(DfTypeUtil.toString(vl)),
                "excludedPaths");
        setupEpg(_epgMap, et -> ((LabelType) et).getIncludedPaths(), (et, vl) -> ((LabelType) et).setIncludedPaths(DfTypeUtil.toString(vl)),
                "includedPaths");
        setupEpg(_epgMap, et -> ((LabelType) et).getName(), (et, vl) -> ((LabelType) et).setName(DfTypeUtil.toString(vl)), "name");
        setupEpg(_epgMap, et -> ((LabelType) et).getPermissions(), (et, vl) -> ((LabelType) et).setPermissions((String[]) vl),
                "permissions");
        setupEpg(_epgMap, et -> ((LabelType) et).getSortOrder(), (et, vl) -> ((LabelType) et).setSortOrder(DfTypeUtil.toInteger(vl)),
                "sortOrder");
        setupEpg(_epgMap, et -> ((LabelType) et).getUpdatedBy(), (et, vl) -> ((LabelType) et).setUpdatedBy(DfTypeUtil.toString(vl)),
                "updatedBy");
        setupEpg(_epgMap, et -> ((LabelType) et).getUpdatedTime(), (et, vl) -> ((LabelType) et).setUpdatedTime(DfTypeUtil.toLong(vl)),
                "updatedTime");
        setupEpg(_epgMap, et -> ((LabelType) et).getValue(), (et, vl) -> ((LabelType) et).setValue(DfTypeUtil.toString(vl)), "value");
        setupEpg(_epgMap, et -> ((LabelType) et).getVirtualHost(), (et, vl) -> ((LabelType) et).setVirtualHost(DfTypeUtil.toString(vl)),
                "virtualHost");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "label_type";
    protected final String _tableDispName = "label_type";
    protected final String _tablePropertyName = "LabelType";

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
    protected final ColumnInfo _columnExcludedPaths = cci("excludedPaths", "excludedPaths", null, null, String.class, "excludedPaths", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIncludedPaths = cci("includedPaths", "includedPaths", null, null, String.class, "includedPaths", null,
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
    protected final ColumnInfo _columnValue = cci("value", "value", null, null, String.class, "value", null, false, false, false, "keyword",
            0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnVirtualHost = cci("virtualHost", "virtualHost", null, null, String.class, "virtualHost", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnExcludedPaths() {
        return _columnExcludedPaths;
    }

    public ColumnInfo columnIncludedPaths() {
        return _columnIncludedPaths;
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

    public ColumnInfo columnValue() {
        return _columnValue;
    }

    public ColumnInfo columnVirtualHost() {
        return _columnVirtualHost;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnExcludedPaths());
        ls.add(columnIncludedPaths());
        ls.add(columnName());
        ls.add(columnPermissions());
        ls.add(columnSortOrder());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        ls.add(columnValue());
        ls.add(columnVirtualHost());
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
        return "org.codelibs.fess.es.config.exentity.LabelType";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.LabelTypeCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.LabelTypeBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return LabelType.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new LabelType();
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
