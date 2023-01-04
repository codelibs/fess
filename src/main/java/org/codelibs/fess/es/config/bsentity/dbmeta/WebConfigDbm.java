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

import org.codelibs.fess.es.config.exentity.WebConfig;
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
public class WebConfigDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final WebConfigDbm _instance = new WebConfigDbm();

    private WebConfigDbm() {
    }

    public static WebConfigDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((WebConfig) et).getAvailable(), (et, vl) -> ((WebConfig) et).setAvailable(DfTypeUtil.toBoolean(vl)),
                "available");
        setupEpg(_epgMap, et -> ((WebConfig) et).getBoost(), (et, vl) -> ((WebConfig) et).setBoost(DfTypeUtil.toFloat(vl)), "boost");
        setupEpg(_epgMap, et -> ((WebConfig) et).getConfigParameter(),
                (et, vl) -> ((WebConfig) et).setConfigParameter(DfTypeUtil.toString(vl)), "configParameter");
        setupEpg(_epgMap, et -> ((WebConfig) et).getCreatedBy(), (et, vl) -> ((WebConfig) et).setCreatedBy(DfTypeUtil.toString(vl)),
                "createdBy");
        setupEpg(_epgMap, et -> ((WebConfig) et).getCreatedTime(), (et, vl) -> ((WebConfig) et).setCreatedTime(DfTypeUtil.toLong(vl)),
                "createdTime");
        setupEpg(_epgMap, et -> ((WebConfig) et).getDepth(), (et, vl) -> ((WebConfig) et).setDepth(DfTypeUtil.toInteger(vl)), "depth");
        setupEpg(_epgMap, et -> ((WebConfig) et).getDescription(), (et, vl) -> ((WebConfig) et).setDescription(DfTypeUtil.toString(vl)),
                "description");
        setupEpg(_epgMap, et -> ((WebConfig) et).getExcludedDocUrls(),
                (et, vl) -> ((WebConfig) et).setExcludedDocUrls(DfTypeUtil.toString(vl)), "excludedDocUrls");
        setupEpg(_epgMap, et -> ((WebConfig) et).getExcludedUrls(), (et, vl) -> ((WebConfig) et).setExcludedUrls(DfTypeUtil.toString(vl)),
                "excludedUrls");
        setupEpg(_epgMap, et -> ((WebConfig) et).getIncludedDocUrls(),
                (et, vl) -> ((WebConfig) et).setIncludedDocUrls(DfTypeUtil.toString(vl)), "includedDocUrls");
        setupEpg(_epgMap, et -> ((WebConfig) et).getIncludedUrls(), (et, vl) -> ((WebConfig) et).setIncludedUrls(DfTypeUtil.toString(vl)),
                "includedUrls");
        setupEpg(_epgMap, et -> ((WebConfig) et).getIntervalTime(), (et, vl) -> ((WebConfig) et).setIntervalTime(DfTypeUtil.toInteger(vl)),
                "intervalTime");
        setupEpg(_epgMap, et -> ((WebConfig) et).getMaxAccessCount(), (et, vl) -> ((WebConfig) et).setMaxAccessCount(DfTypeUtil.toLong(vl)),
                "maxAccessCount");
        setupEpg(_epgMap, et -> ((WebConfig) et).getName(), (et, vl) -> ((WebConfig) et).setName(DfTypeUtil.toString(vl)), "name");
        setupEpg(_epgMap, et -> ((WebConfig) et).getNumOfThread(), (et, vl) -> ((WebConfig) et).setNumOfThread(DfTypeUtil.toInteger(vl)),
                "numOfThread");
        setupEpg(_epgMap, et -> ((WebConfig) et).getPermissions(), (et, vl) -> ((WebConfig) et).setPermissions((String[]) vl),
                "permissions");
        setupEpg(_epgMap, et -> ((WebConfig) et).getSortOrder(), (et, vl) -> ((WebConfig) et).setSortOrder(DfTypeUtil.toInteger(vl)),
                "sortOrder");
        setupEpg(_epgMap, et -> ((WebConfig) et).getTimeToLive(), (et, vl) -> ((WebConfig) et).setTimeToLive(DfTypeUtil.toInteger(vl)),
                "timeToLive");
        setupEpg(_epgMap, et -> ((WebConfig) et).getUpdatedBy(), (et, vl) -> ((WebConfig) et).setUpdatedBy(DfTypeUtil.toString(vl)),
                "updatedBy");
        setupEpg(_epgMap, et -> ((WebConfig) et).getUpdatedTime(), (et, vl) -> ((WebConfig) et).setUpdatedTime(DfTypeUtil.toLong(vl)),
                "updatedTime");
        setupEpg(_epgMap, et -> ((WebConfig) et).getUrls(), (et, vl) -> ((WebConfig) et).setUrls(DfTypeUtil.toString(vl)), "urls");
        setupEpg(_epgMap, et -> ((WebConfig) et).getUserAgent(), (et, vl) -> ((WebConfig) et).setUserAgent(DfTypeUtil.toString(vl)),
                "userAgent");
        setupEpg(_epgMap, et -> ((WebConfig) et).getVirtualHosts(), (et, vl) -> ((WebConfig) et).setVirtualHosts((String[]) vl),
                "virtualHosts");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "web_config";
    protected final String _tableDispName = "web_config";
    protected final String _tablePropertyName = "WebConfig";

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
    protected final ColumnInfo _columnExcludedDocUrls = cci("excludedDocUrls", "excludedDocUrls", null, null, String.class,
            "excludedDocUrls", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnExcludedUrls = cci("excludedUrls", "excludedUrls", null, null, String.class, "excludedUrls", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIncludedDocUrls = cci("includedDocUrls", "includedDocUrls", null, null, String.class,
            "includedDocUrls", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIncludedUrls = cci("includedUrls", "includedUrls", null, null, String.class, "includedUrls", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIntervalTime = cci("intervalTime", "intervalTime", null, null, Integer.class, "intervalTime", null,
            false, false, false, "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMaxAccessCount = cci("maxAccessCount", "maxAccessCount", null, null, Long.class, "maxAccessCount",
            null, false, false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnName = cci("name", "name", null, null, String.class, "name", null, false, false, false, "keyword", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnNumOfThread = cci("numOfThread", "numOfThread", null, null, Integer.class, "numOfThread", null, false,
            false, false, "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
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
    protected final ColumnInfo _columnUrls = cci("urls", "urls", null, null, String.class, "urls", null, false, false, false, "keyword", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserAgent = cci("userAgent", "userAgent", null, null, String.class, "userAgent", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
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

    public ColumnInfo columnExcludedDocUrls() {
        return _columnExcludedDocUrls;
    }

    public ColumnInfo columnExcludedUrls() {
        return _columnExcludedUrls;
    }

    public ColumnInfo columnIncludedDocUrls() {
        return _columnIncludedDocUrls;
    }

    public ColumnInfo columnIncludedUrls() {
        return _columnIncludedUrls;
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

    public ColumnInfo columnUrls() {
        return _columnUrls;
    }

    public ColumnInfo columnUserAgent() {
        return _columnUserAgent;
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
        ls.add(columnExcludedDocUrls());
        ls.add(columnExcludedUrls());
        ls.add(columnIncludedDocUrls());
        ls.add(columnIncludedUrls());
        ls.add(columnIntervalTime());
        ls.add(columnMaxAccessCount());
        ls.add(columnName());
        ls.add(columnNumOfThread());
        ls.add(columnPermissions());
        ls.add(columnSortOrder());
        ls.add(columnTimeToLive());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
        ls.add(columnUrls());
        ls.add(columnUserAgent());
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
        return "org.codelibs.fess.es.config.exentity.WebConfig";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.WebConfigCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.WebConfigBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return WebConfig.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new WebConfig();
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
