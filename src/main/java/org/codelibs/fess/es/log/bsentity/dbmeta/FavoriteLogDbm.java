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

import org.codelibs.fess.es.log.exentity.FavoriteLog;
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
public class FavoriteLogDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final FavoriteLogDbm _instance = new FavoriteLogDbm();

    private FavoriteLogDbm() {
    }

    public static FavoriteLogDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((FavoriteLog) et).getCreatedAt(),
                (et, vl) -> ((FavoriteLog) et).setCreatedAt(DfTypeUtil.toLocalDateTime(vl)), "createdAt");
        setupEpg(_epgMap, et -> ((FavoriteLog) et).getDocId(), (et, vl) -> ((FavoriteLog) et).setDocId(DfTypeUtil.toString(vl)), "docId");
        setupEpg(_epgMap, et -> ((FavoriteLog) et).getQueryId(), (et, vl) -> ((FavoriteLog) et).setQueryId(DfTypeUtil.toString(vl)),
                "queryId");
        setupEpg(_epgMap, et -> ((FavoriteLog) et).getUrl(), (et, vl) -> ((FavoriteLog) et).setUrl(DfTypeUtil.toString(vl)), "url");
        setupEpg(_epgMap, et -> ((FavoriteLog) et).getUserInfoId(), (et, vl) -> ((FavoriteLog) et).setUserInfoId(DfTypeUtil.toString(vl)),
                "userInfoId");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "favorite_log";
    protected final String _tableDispName = "favorite_log";
    protected final String _tablePropertyName = "FavoriteLog";

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
    protected final ColumnInfo _columnCreatedAt = cci("createdAt", "createdAt", null, null, LocalDateTime.class, "createdAt", null, false,
            false, false, "LocalDateTime", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnDocId = cci("docId", "docId", null, null, String.class, "docId", null, false, false, false, "keyword",
            0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnQueryId = cci("queryId", "queryId", null, null, String.class, "queryId", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUrl = cci("url", "url", null, null, String.class, "url", null, false, false, false, "keyword", 0, 0,
            null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserInfoId = cci("userInfoId", "userInfoId", null, null, String.class, "userInfoId", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnCreatedAt() {
        return _columnCreatedAt;
    }

    public ColumnInfo columnDocId() {
        return _columnDocId;
    }

    public ColumnInfo columnQueryId() {
        return _columnQueryId;
    }

    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    public ColumnInfo columnUserInfoId() {
        return _columnUserInfoId;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnCreatedAt());
        ls.add(columnDocId());
        ls.add(columnQueryId());
        ls.add(columnUrl());
        ls.add(columnUserInfoId());
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
        return "org.codelibs.fess.es.log.exentity.FavoriteLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.log.cbean.FavoriteLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.log.exbhv.FavoriteLogBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return FavoriteLog.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new FavoriteLog();
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
