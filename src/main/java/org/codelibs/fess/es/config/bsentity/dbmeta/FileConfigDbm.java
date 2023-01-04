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

import org.codelibs.fess.es.config.exentity.FileConfig;
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
public class FileConfigDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final FileConfigDbm _instance = new FileConfigDbm();

    private FileConfigDbm() {
    }

    public static FileConfigDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((FileConfig) et).getAvailable(), (et, vl) -> ((FileConfig) et).setAvailable(DfTypeUtil.toBoolean(vl)),
                "available");
        setupEpg(_epgMap, et -> ((FileConfig) et).getBoost(), (et, vl) -> ((FileConfig) et).setBoost(DfTypeUtil.toFloat(vl)), "boost");
        setupEpg(_epgMap, et -> ((FileConfig) et).getConfigParameter(),
                (et, vl) -> ((FileConfig) et).setConfigParameter(DfTypeUtil.toString(vl)), "configParameter");
        setupEpg(_epgMap, et -> ((FileConfig) et).getCreatedBy(), (et, vl) -> ((FileConfig) et).setCreatedBy(DfTypeUtil.toString(vl)),
                "createdBy");
        setupEpg(_epgMap, et -> ((FileConfig) et).getCreatedTime(), (et, vl) -> ((FileConfig) et).setCreatedTime(DfTypeUtil.toLong(vl)),
                "createdTime");
        setupEpg(_epgMap, et -> ((FileConfig) et).getDepth(), (et, vl) -> ((FileConfig) et).setDepth(DfTypeUtil.toInteger(vl)), "depth");
        setupEpg(_epgMap, et -> ((FileConfig) et).getDescription(), (et, vl) -> ((FileConfig) et).setDescription(DfTypeUtil.toString(vl)),
                "description");
        setupEpg(_epgMap, et -> ((FileConfig) et).getExcludedDocPaths(),
                (et, vl) -> ((FileConfig) et).setExcludedDocPaths(DfTypeUtil.toString(vl)), "excludedDocPaths");
        setupEpg(_epgMap, et -> ((FileConfig) et).getExcludedPaths(),
                (et, vl) -> ((FileConfig) et).setExcludedPaths(DfTypeUtil.toString(vl)), "excludedPaths");
        setupEpg(_epgMap, et -> ((FileConfig) et).getIncludedDocPaths(),
                (et, vl) -> ((FileConfig) et).setIncludedDocPaths(DfTypeUtil.toString(vl)), "includedDocPaths");
        setupEpg(_epgMap, et -> ((FileConfig) et).getIncludedPaths(),
                (et, vl) -> ((FileConfig) et).setIncludedPaths(DfTypeUtil.toString(vl)), "includedPaths");
        setupEpg(_epgMap, et -> ((FileConfig) et).getIntervalTime(),
                (et, vl) -> ((FileConfig) et).setIntervalTime(DfTypeUtil.toInteger(vl)), "intervalTime");
        setupEpg(_epgMap, et -> ((FileConfig) et).getMaxAccessCount(),
                (et, vl) -> ((FileConfig) et).setMaxAccessCount(DfTypeUtil.toLong(vl)), "maxAccessCount");
        setupEpg(_epgMap, et -> ((FileConfig) et).getName(), (et, vl) -> ((FileConfig) et).setName(DfTypeUtil.toString(vl)), "name");
        setupEpg(_epgMap, et -> ((FileConfig) et).getNumOfThread(), (et, vl) -> ((FileConfig) et).setNumOfThread(DfTypeUtil.toInteger(vl)),
                "numOfThread");
        setupEpg(_epgMap, et -> ((FileConfig) et).getPaths(), (et, vl) -> ((FileConfig) et).setPaths(DfTypeUtil.toString(vl)), "paths");
        setupEpg(_epgMap, et -> ((FileConfig) et).getPermissions(), (et, vl) -> ((FileConfig) et).setPermissions((String[]) vl),
                "permissions");
        setupEpg(_epgMap, et -> ((FileConfig) et).getSortOrder(), (et, vl) -> ((FileConfig) et).setSortOrder(DfTypeUtil.toInteger(vl)),
                "sortOrder");
        setupEpg(_epgMap, et -> ((FileConfig) et).getTimeToLive(), (et, vl) -> ((FileConfig) et).setTimeToLive(DfTypeUtil.toInteger(vl)),
                "timeToLive");
        setupEpg(_epgMap, et -> ((FileConfig) et).getUpdatedBy(), (et, vl) -> ((FileConfig) et).setUpdatedBy(DfTypeUtil.toString(vl)),
                "updatedBy");
        setupEpg(_epgMap, et -> ((FileConfig) et).getUpdatedTime(), (et, vl) -> ((FileConfig) et).setUpdatedTime(DfTypeUtil.toLong(vl)),
                "updatedTime");
        setupEpg(_epgMap, et -> ((FileConfig) et).getVirtualHosts(), (et, vl) -> ((FileConfig) et).setVirtualHosts((String[]) vl),
                "virtualHosts");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "file_config";
    protected final String _tableDispName = "file_config";
    protected final String _tablePropertyName = "FileConfig";

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
    protected final ColumnInfo _columnConfigParameter = cci("configParameter", "configParameter", null, null, String.class,
            "configParameter", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedBy = cci("createdBy", "createdBy", null, null, String.class, "createdBy", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedTime = cci("createdTime", "createdTime", null, null, Long.class, "createdTime", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnDepth = cci("depth", "depth", null, null, Integer.class, "depth", null, false, false, false,
            "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnDescription = cci("description", "description", null, null, String.class, "description", null, false,
            false, false, "text", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnExcludedDocPaths = cci("excludedDocPaths", "excludedDocPaths", null, null, String.class,
            "excludedDocPaths", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnExcludedPaths = cci("excludedPaths", "excludedPaths", null, null, String.class, "excludedPaths", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIncludedDocPaths = cci("includedDocPaths", "includedDocPaths", null, null, String.class,
            "includedDocPaths", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIncludedPaths = cci("includedPaths", "includedPaths", null, null, String.class, "includedPaths", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIntervalTime = cci("intervalTime", "intervalTime", null, null, Integer.class, "intervalTime", null,
            false, false, false, "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMaxAccessCount = cci("maxAccessCount", "maxAccessCount", null, null, Long.class, "maxAccessCount",
            null, false, false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnName = cci("name", "name", null, null, String.class, "name", null, false, false, false, "keyword", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnNumOfThread = cci("numOfThread", "numOfThread", null, null, Integer.class, "numOfThread", null, false,
            false, false, "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPaths = cci("paths", "paths", null, null, String.class, "paths", null, false, false, false, "keyword",
            0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPermissions = cci("permissions", "permissions", null, null, String[].class, "permissions", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSortOrder = cci("sortOrder", "sortOrder", null, null, Integer.class, "sortOrder", null, false, false,
            false, "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnTimeToLive = cci("timeToLive", "timeToLive", null, null, Integer.class, "timeToLive", null, false,
            false, false, "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
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

    public ColumnInfo columnConfigParameter() {
        return _columnConfigParameter;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnDepth() {
        return _columnDepth;
    }

    public ColumnInfo columnDescription() {
        return _columnDescription;
    }

    public ColumnInfo columnExcludedDocPaths() {
        return _columnExcludedDocPaths;
    }

    public ColumnInfo columnExcludedPaths() {
        return _columnExcludedPaths;
    }

    public ColumnInfo columnIncludedDocPaths() {
        return _columnIncludedDocPaths;
    }

    public ColumnInfo columnIncludedPaths() {
        return _columnIncludedPaths;
    }

    public ColumnInfo columnIntervalTime() {
        return _columnIntervalTime;
    }

    public ColumnInfo columnMaxAccessCount() {
        return _columnMaxAccessCount;
    }

    public ColumnInfo columnName() {
        return _columnName;
    }

    public ColumnInfo columnNumOfThread() {
        return _columnNumOfThread;
    }

    public ColumnInfo columnPaths() {
        return _columnPaths;
    }

    public ColumnInfo columnPermissions() {
        return _columnPermissions;
    }

    public ColumnInfo columnSortOrder() {
        return _columnSortOrder;
    }

    public ColumnInfo columnTimeToLive() {
        return _columnTimeToLive;
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
        ls.add(columnConfigParameter());
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnDepth());
        ls.add(columnDescription());
        ls.add(columnExcludedDocPaths());
        ls.add(columnExcludedPaths());
        ls.add(columnIncludedDocPaths());
        ls.add(columnIncludedPaths());
        ls.add(columnIntervalTime());
        ls.add(columnMaxAccessCount());
        ls.add(columnName());
        ls.add(columnNumOfThread());
        ls.add(columnPaths());
        ls.add(columnPermissions());
        ls.add(columnSortOrder());
        ls.add(columnTimeToLive());
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
        return "org.codelibs.fess.es.config.exentity.FileConfig";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.FileConfigCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.FileConfigBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return FileConfig.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new FileConfig();
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
