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

import org.codelibs.fess.es.config.exentity.FailureUrl;
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
public class FailureUrlDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final FailureUrlDbm _instance = new FailureUrlDbm();

    private FailureUrlDbm() {
    }

    public static FailureUrlDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((FailureUrl) et).getConfigId(), (et, vl) -> ((FailureUrl) et).setConfigId(DfTypeUtil.toString(vl)),
                "configId");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getErrorCount(), (et, vl) -> ((FailureUrl) et).setErrorCount(DfTypeUtil.toInteger(vl)),
                "errorCount");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getErrorLog(), (et, vl) -> ((FailureUrl) et).setErrorLog(DfTypeUtil.toString(vl)),
                "errorLog");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getErrorName(), (et, vl) -> ((FailureUrl) et).setErrorName(DfTypeUtil.toString(vl)),
                "errorName");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getLastAccessTime(),
                (et, vl) -> ((FailureUrl) et).setLastAccessTime(DfTypeUtil.toLong(vl)), "lastAccessTime");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getThreadName(), (et, vl) -> ((FailureUrl) et).setThreadName(DfTypeUtil.toString(vl)),
                "threadName");
        setupEpg(_epgMap, et -> ((FailureUrl) et).getUrl(), (et, vl) -> ((FailureUrl) et).setUrl(DfTypeUtil.toString(vl)), "url");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "failure_url";
    protected final String _tableDispName = "failure_url";
    protected final String _tablePropertyName = "FailureUrl";

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
    protected final ColumnInfo _columnConfigId = cci("configId", "configId", null, null, String.class, "configId", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnErrorCount = cci("errorCount", "errorCount", null, null, Integer.class, "errorCount", null, false,
            false, false, "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnErrorLog = cci("errorLog", "errorLog", null, null, String.class, "errorLog", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnErrorName = cci("errorName", "errorName", null, null, String.class, "errorName", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLastAccessTime = cci("lastAccessTime", "lastAccessTime", null, null, Long.class, "lastAccessTime",
            null, false, false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnThreadName = cci("threadName", "threadName", null, null, String.class, "threadName", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUrl = cci("url", "url", null, null, String.class, "url", null, false, false, false, "keyword", 0, 0,
            null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnConfigId() {
        return _columnConfigId;
    }

    public ColumnInfo columnErrorCount() {
        return _columnErrorCount;
    }

    public ColumnInfo columnErrorLog() {
        return _columnErrorLog;
    }

    public ColumnInfo columnErrorName() {
        return _columnErrorName;
    }

    public ColumnInfo columnLastAccessTime() {
        return _columnLastAccessTime;
    }

    public ColumnInfo columnThreadName() {
        return _columnThreadName;
    }

    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnConfigId());
        ls.add(columnErrorCount());
        ls.add(columnErrorLog());
        ls.add(columnErrorName());
        ls.add(columnLastAccessTime());
        ls.add(columnThreadName());
        ls.add(columnUrl());
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
        return "org.codelibs.fess.es.config.exentity.FailureUrl";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.FailureUrlCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.FailureUrlBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return FailureUrl.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new FailureUrl();
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
