/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import java.time.*;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.config.exentity.CrawlingSessionInfo;

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
public class CrawlingSessionInfoDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final CrawlingSessionInfoDbm _instance = new CrawlingSessionInfoDbm();

    private CrawlingSessionInfoDbm() {
    }

    public static CrawlingSessionInfoDbm getInstance() {
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
        setupEpg(_epgMap, et-> ((CrawlingSessionInfo)et).getCrawlingSessionId(),(et,vl)->((CrawlingSessionInfo) et).setCrawlingSessionId(DfTypeUtil.toString(vl)), "crawlingSessionId");
        setupEpg(_epgMap, et-> ((CrawlingSessionInfo)et).getCreatedTime(),(et,vl)->((CrawlingSessionInfo) et).setCreatedTime(DfTypeUtil.toLong(vl)), "createdTime");
        setupEpg(_epgMap, et-> ((CrawlingSessionInfo)et).getKey(),(et,vl)->((CrawlingSessionInfo) et).setKey(DfTypeUtil.toString(vl)), "key");
        setupEpg(_epgMap, et-> ((CrawlingSessionInfo)et).getValue(),(et,vl)->((CrawlingSessionInfo) et).setValue(DfTypeUtil.toString(vl)), "value");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "crawling_session_info";
    protected final String _tableDispName = "crawling_session_info";
    protected final String _tablePropertyName = "CrawlingSessionInfo";
    public String getTableDbName() { return _tableDbName; }
    @Override
    public String getTableDispName() { return _tableDispName; }
    @Override
    public String getTablePropertyName() { return _tablePropertyName; }
    @Override
    public TableSqlName getTableSqlName() { return null; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnCrawlingSessionId = cci("crawlingSessionId", "crawlingSessionId", null, null, String.class, "crawlingSessionId", null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedTime = cci("createdTime", "createdTime", null, null, Long.class, "createdTime", null, false, false, false, "Long", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnKey = cci("key", "key", null, null, String.class, "key", null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnValue = cci("value", "value", null, null, String.class, "value", null, false, false, false, "String", 0, 0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnCrawlingSessionId() { return _columnCrawlingSessionId; }
    public ColumnInfo columnCreatedTime() { return _columnCreatedTime; }
    public ColumnInfo columnKey() { return _columnKey; }
    public ColumnInfo columnValue() { return _columnValue; }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnCrawlingSessionId());
        ls.add(columnCreatedTime());
        ls.add(columnKey());
        ls.add(columnValue());
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
        return "org.codelibs.fess.es.config.exentity.CrawlingSessionInfo";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.CrawlingSessionInfoCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.CrawlingSessionInfoBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return CrawlingSessionInfo.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new CrawlingSessionInfo();
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

