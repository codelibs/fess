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
package org.codelibs.fess.es.log.bsentity.dbmeta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.log.exentity.ClickLog;
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
public class ClickLogDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final ClickLogDbm _instance = new ClickLogDbm();

    private ClickLogDbm() {
    }

    public static ClickLogDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((ClickLog) et).getUrlId(), (et, vl) -> ((ClickLog) et).setUrlId(DfTypeUtil.toString(vl)), "urlId");
        setupEpg(_epgMap, et -> ((ClickLog) et).getDocId(), (et, vl) -> ((ClickLog) et).setDocId(DfTypeUtil.toString(vl)), "docId");
        setupEpg(_epgMap, et -> ((ClickLog) et).getOrder(), (et, vl) -> ((ClickLog) et).setOrder(DfTypeUtil.toInteger(vl)), "order");
        setupEpg(_epgMap, et -> ((ClickLog) et).getQueryId(), (et, vl) -> ((ClickLog) et).setQueryId(DfTypeUtil.toString(vl)), "queryId");
        setupEpg(_epgMap, et -> ((ClickLog) et).getQueryRequestedAt(),
                (et, vl) -> ((ClickLog) et).setQueryRequestedAt(DfTypeUtil.toLocalDateTime(vl)), "queryRequestedAt");
        setupEpg(_epgMap, et -> ((ClickLog) et).getRequestedAt(),
                (et, vl) -> ((ClickLog) et).setRequestedAt(DfTypeUtil.toLocalDateTime(vl)), "requestedAt");
        setupEpg(_epgMap, et -> ((ClickLog) et).getUrl(), (et, vl) -> ((ClickLog) et).setUrl(DfTypeUtil.toString(vl)), "url");
        setupEpg(_epgMap, et -> ((ClickLog) et).getUserSessionId(), (et, vl) -> ((ClickLog) et).setUserSessionId(DfTypeUtil.toString(vl)),
                "userSessionId");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "click_log";
    protected final String _tableDispName = "click_log";
    protected final String _tablePropertyName = "ClickLog";

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
    protected final ColumnInfo _columnUrlId = cci("urlId", "urlId", null, null, String.class, "urlId", null, false, false, false, "keyword",
            0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnDocId = cci("docId", "docId", null, null, String.class, "docId", null, false, false, false, "keyword",
            0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnOrder = cci("order", "order", null, null, Integer.class, "order", null, false, false, false,
            "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnQueryId = cci("queryId", "queryId", null, null, String.class, "queryId", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnQueryRequestedAt = cci("queryRequestedAt", "queryRequestedAt", null, null, LocalDateTime.class,
            "queryRequestedAt", null, false, false, false, "LocalDateTime", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnRequestedAt = cci("requestedAt", "requestedAt", null, null, LocalDateTime.class, "requestedAt", null,
            false, false, false, "LocalDateTime", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUrl = cci("url", "url", null, null, String.class, "url", null, false, false, false, "keyword", 0, 0,
            null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserSessionId = cci("userSessionId", "userSessionId", null, null, String.class, "userSessionId", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnUrlId() {
        return _columnUrlId;
    }

    public ColumnInfo columnDocId() {
        return _columnDocId;
    }

    public ColumnInfo columnOrder() {
        return _columnOrder;
    }

    public ColumnInfo columnQueryId() {
        return _columnQueryId;
    }

    public ColumnInfo columnQueryRequestedAt() {
        return _columnQueryRequestedAt;
    }

    public ColumnInfo columnRequestedAt() {
        return _columnRequestedAt;
    }

    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    public ColumnInfo columnUserSessionId() {
        return _columnUserSessionId;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnUrlId());
        ls.add(columnDocId());
        ls.add(columnOrder());
        ls.add(columnQueryId());
        ls.add(columnQueryRequestedAt());
        ls.add(columnRequestedAt());
        ls.add(columnUrl());
        ls.add(columnUserSessionId());
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
        return "org.codelibs.fess.es.log.exentity.ClickLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.log.cbean.ClickLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.log.exbhv.ClickLogBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return ClickLog.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new ClickLog();
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
