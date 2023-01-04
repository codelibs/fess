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

import org.codelibs.fess.es.config.exentity.KeyMatch;
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
public class KeyMatchDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final KeyMatchDbm _instance = new KeyMatchDbm();

    private KeyMatchDbm() {
    }

    public static KeyMatchDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((KeyMatch) et).getBoost(), (et, vl) -> ((KeyMatch) et).setBoost(DfTypeUtil.toFloat(vl)), "boost");
        setupEpg(_epgMap, et -> ((KeyMatch) et).getCreatedBy(), (et, vl) -> ((KeyMatch) et).setCreatedBy(DfTypeUtil.toString(vl)),
                "createdBy");
        setupEpg(_epgMap, et -> ((KeyMatch) et).getCreatedTime(), (et, vl) -> ((KeyMatch) et).setCreatedTime(DfTypeUtil.toLong(vl)),
                "createdTime");
        setupEpg(_epgMap, et -> ((KeyMatch) et).getMaxSize(), (et, vl) -> ((KeyMatch) et).setMaxSize(DfTypeUtil.toInteger(vl)), "maxSize");
        setupEpg(_epgMap, et -> ((KeyMatch) et).getQuery(), (et, vl) -> ((KeyMatch) et).setQuery(DfTypeUtil.toString(vl)), "query");
        setupEpg(_epgMap, et -> ((KeyMatch) et).getTerm(), (et, vl) -> ((KeyMatch) et).setTerm(DfTypeUtil.toString(vl)), "term");
        setupEpg(_epgMap, et -> ((KeyMatch) et).getUpdatedBy(), (et, vl) -> ((KeyMatch) et).setUpdatedBy(DfTypeUtil.toString(vl)),
                "updatedBy");
        setupEpg(_epgMap, et -> ((KeyMatch) et).getUpdatedTime(), (et, vl) -> ((KeyMatch) et).setUpdatedTime(DfTypeUtil.toLong(vl)),
                "updatedTime");
        setupEpg(_epgMap, et -> ((KeyMatch) et).getVirtualHost(), (et, vl) -> ((KeyMatch) et).setVirtualHost(DfTypeUtil.toString(vl)),
                "virtualHost");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "key_match";
    protected final String _tableDispName = "key_match";
    protected final String _tablePropertyName = "KeyMatch";

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
    protected final ColumnInfo _columnBoost = cci("boost", "boost", null, null, Float.class, "boost", null, false, false, false, "Float", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedBy = cci("createdBy", "createdBy", null, null, String.class, "createdBy", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedTime = cci("createdTime", "createdTime", null, null, Long.class, "createdTime", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMaxSize = cci("maxSize", "maxSize", null, null, Integer.class, "maxSize", null, false, false, false,
            "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnQuery = cci("query", "query", null, null, String.class, "query", null, false, false, false, "keyword",
            0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnTerm = cci("term", "term", null, null, String.class, "term", null, false, false, false, "keyword", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedBy = cci("updatedBy", "updatedBy", null, null, String.class, "updatedBy", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedTime = cci("updatedTime", "updatedTime", null, null, Long.class, "updatedTime", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnVirtualHost = cci("virtualHost", "virtualHost", null, null, String.class, "virtualHost", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnBoost() {
        return _columnBoost;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnMaxSize() {
        return _columnMaxSize;
    }

    public ColumnInfo columnQuery() {
        return _columnQuery;
    }

    public ColumnInfo columnTerm() {
        return _columnTerm;
    }

    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    public ColumnInfo columnVirtualHost() {
        return _columnVirtualHost;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnBoost());
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnMaxSize());
        ls.add(columnQuery());
        ls.add(columnTerm());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
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
        return "org.codelibs.fess.es.config.exentity.KeyMatch";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.KeyMatchCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.KeyMatchBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return KeyMatch.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new KeyMatch();
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
