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

import org.codelibs.fess.es.log.exentity.SearchLog;
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
public class SearchLogDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final SearchLogDbm _instance = new SearchLogDbm();

    private SearchLogDbm() {
    }

    public static SearchLogDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((SearchLog) et).getAccessType(), (et, vl) -> ((SearchLog) et).setAccessType(DfTypeUtil.toString(vl)),
                "accessType");
        setupEpg(_epgMap, et -> ((SearchLog) et).getClientIp(), (et, vl) -> ((SearchLog) et).setClientIp(DfTypeUtil.toString(vl)),
                "clientIp");
        setupEpg(_epgMap, et -> ((SearchLog) et).getHitCount(), (et, vl) -> ((SearchLog) et).setHitCount(DfTypeUtil.toLong(vl)),
                "hitCount");
        setupEpg(_epgMap, et -> ((SearchLog) et).getHitCountRelation(),
                (et, vl) -> ((SearchLog) et).setHitCountRelation(DfTypeUtil.toString(vl)), "hitCountRelation");
        setupEpg(_epgMap, et -> ((SearchLog) et).getLanguages(), (et, vl) -> ((SearchLog) et).setLanguages(DfTypeUtil.toString(vl)),
                "languages");
        setupEpg(_epgMap, et -> ((SearchLog) et).getQueryId(), (et, vl) -> ((SearchLog) et).setQueryId(DfTypeUtil.toString(vl)), "queryId");
        setupEpg(_epgMap, et -> ((SearchLog) et).getQueryOffset(), (et, vl) -> ((SearchLog) et).setQueryOffset(DfTypeUtil.toInteger(vl)),
                "queryOffset");
        setupEpg(_epgMap, et -> ((SearchLog) et).getQueryPageSize(),
                (et, vl) -> ((SearchLog) et).setQueryPageSize(DfTypeUtil.toInteger(vl)), "queryPageSize");
        setupEpg(_epgMap, et -> ((SearchLog) et).getQueryTime(), (et, vl) -> ((SearchLog) et).setQueryTime(DfTypeUtil.toLong(vl)),
                "queryTime");
        setupEpg(_epgMap, et -> ((SearchLog) et).getReferer(), (et, vl) -> ((SearchLog) et).setReferer(DfTypeUtil.toString(vl)), "referer");
        setupEpg(_epgMap, et -> ((SearchLog) et).getRequestedAt(),
                (et, vl) -> ((SearchLog) et).setRequestedAt(DfTypeUtil.toLocalDateTime(vl)), "requestedAt");
        setupEpg(_epgMap, et -> ((SearchLog) et).getResponseTime(), (et, vl) -> ((SearchLog) et).setResponseTime(DfTypeUtil.toLong(vl)),
                "responseTime");
        setupEpg(_epgMap, et -> ((SearchLog) et).getRoles(), (et, vl) -> ((SearchLog) et).setRoles((String[]) vl), "roles");
        setupEpg(_epgMap, et -> ((SearchLog) et).getSearchWord(), (et, vl) -> ((SearchLog) et).setSearchWord(DfTypeUtil.toString(vl)),
                "searchWord");
        setupEpg(_epgMap, et -> ((SearchLog) et).getUser(), (et, vl) -> ((SearchLog) et).setUser(DfTypeUtil.toString(vl)), "user");
        setupEpg(_epgMap, et -> ((SearchLog) et).getUserAgent(), (et, vl) -> ((SearchLog) et).setUserAgent(DfTypeUtil.toString(vl)),
                "userAgent");
        setupEpg(_epgMap, et -> ((SearchLog) et).getUserInfoId(), (et, vl) -> ((SearchLog) et).setUserInfoId(DfTypeUtil.toString(vl)),
                "userInfoId");
        setupEpg(_epgMap, et -> ((SearchLog) et).getUserSessionId(), (et, vl) -> ((SearchLog) et).setUserSessionId(DfTypeUtil.toString(vl)),
                "userSessionId");
        setupEpg(_epgMap, et -> ((SearchLog) et).getVirtualHost(), (et, vl) -> ((SearchLog) et).setVirtualHost(DfTypeUtil.toString(vl)),
                "virtualHost");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "search_log";
    protected final String _tableDispName = "search_log";
    protected final String _tablePropertyName = "SearchLog";

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
    protected final ColumnInfo _columnAccessType = cci("accessType", "accessType", null, null, String.class, "accessType", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnClientIp = cci("clientIp", "clientIp", null, null, String.class, "clientIp", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHitCount = cci("hitCount", "hitCount", null, null, Long.class, "hitCount", null, false, false, false,
            "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHitCountRelation = cci("hitCountRelation", "hitCountRelation", null, null, String.class,
            "hitCountRelation", null, false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLanguages = cci("languages", "languages", null, null, String.class, "languages", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnQueryId = cci("queryId", "queryId", null, null, String.class, "queryId", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnQueryOffset = cci("queryOffset", "queryOffset", null, null, Integer.class, "queryOffset", null, false,
            false, false, "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnQueryPageSize = cci("queryPageSize", "queryPageSize", null, null, Integer.class, "queryPageSize",
            null, false, false, false, "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnQueryTime = cci("queryTime", "queryTime", null, null, Long.class, "queryTime", null, false, false,
            false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnReferer = cci("referer", "referer", null, null, String.class, "referer", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnRequestedAt = cci("requestedAt", "requestedAt", null, null, LocalDateTime.class, "requestedAt", null,
            false, false, false, "LocalDateTime", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnResponseTime = cci("responseTime", "responseTime", null, null, Long.class, "responseTime", null,
            false, false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnRoles = cci("roles", "roles", null, null, String[].class, "roles", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSearchWord = cci("searchWord", "searchWord", null, null, String.class, "searchWord", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUser = cci("user", "user", null, null, String.class, "user", null, false, false, false, "keyword", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserAgent = cci("userAgent", "userAgent", null, null, String.class, "userAgent", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserInfoId = cci("userInfoId", "userInfoId", null, null, String.class, "userInfoId", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserSessionId = cci("userSessionId", "userSessionId", null, null, String.class, "userSessionId", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnVirtualHost = cci("virtualHost", "virtualHost", null, null, String.class, "virtualHost", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnAccessType() {
        return _columnAccessType;
    }

    public ColumnInfo columnClientIp() {
        return _columnClientIp;
    }

    public ColumnInfo columnHitCount() {
        return _columnHitCount;
    }

    public ColumnInfo columnHitCountRelation() {
        return _columnHitCountRelation;
    }

    public ColumnInfo columnLanguages() {
        return _columnLanguages;
    }

    public ColumnInfo columnQueryId() {
        return _columnQueryId;
    }

    public ColumnInfo columnQueryOffset() {
        return _columnQueryOffset;
    }

    public ColumnInfo columnQueryPageSize() {
        return _columnQueryPageSize;
    }

    public ColumnInfo columnQueryTime() {
        return _columnQueryTime;
    }

    public ColumnInfo columnReferer() {
        return _columnReferer;
    }

    public ColumnInfo columnRequestedAt() {
        return _columnRequestedAt;
    }

    public ColumnInfo columnResponseTime() {
        return _columnResponseTime;
    }

    public ColumnInfo columnRoles() {
        return _columnRoles;
    }

    public ColumnInfo columnSearchWord() {
        return _columnSearchWord;
    }

    public ColumnInfo columnUser() {
        return _columnUser;
    }

    public ColumnInfo columnUserAgent() {
        return _columnUserAgent;
    }

    public ColumnInfo columnUserInfoId() {
        return _columnUserInfoId;
    }

    public ColumnInfo columnUserSessionId() {
        return _columnUserSessionId;
    }

    public ColumnInfo columnVirtualHost() {
        return _columnVirtualHost;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnAccessType());
        ls.add(columnClientIp());
        ls.add(columnHitCount());
        ls.add(columnHitCountRelation());
        ls.add(columnLanguages());
        ls.add(columnQueryId());
        ls.add(columnQueryOffset());
        ls.add(columnQueryPageSize());
        ls.add(columnQueryTime());
        ls.add(columnReferer());
        ls.add(columnRequestedAt());
        ls.add(columnResponseTime());
        ls.add(columnRoles());
        ls.add(columnSearchWord());
        ls.add(columnUser());
        ls.add(columnUserAgent());
        ls.add(columnUserInfoId());
        ls.add(columnUserSessionId());
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
        return "org.codelibs.fess.es.log.exentity.SearchLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.log.cbean.SearchLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.log.exbhv.SearchLogBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return SearchLog.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new SearchLog();
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
