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

import org.codelibs.fess.es.config.exentity.ElevateWord;
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
public class ElevateWordDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final ElevateWordDbm _instance = new ElevateWordDbm();

    private ElevateWordDbm() {
    }

    public static ElevateWordDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((ElevateWord) et).getBoost(), (et, vl) -> ((ElevateWord) et).setBoost(DfTypeUtil.toFloat(vl)), "boost");
        setupEpg(_epgMap, et -> ((ElevateWord) et).getCreatedBy(), (et, vl) -> ((ElevateWord) et).setCreatedBy(DfTypeUtil.toString(vl)),
                "createdBy");
        setupEpg(_epgMap, et -> ((ElevateWord) et).getCreatedTime(), (et, vl) -> ((ElevateWord) et).setCreatedTime(DfTypeUtil.toLong(vl)),
                "createdTime");
        setupEpg(_epgMap, et -> ((ElevateWord) et).getPermissions(), (et, vl) -> ((ElevateWord) et).setPermissions((String[]) vl),
                "permissions");
        setupEpg(_epgMap, et -> ((ElevateWord) et).getReading(), (et, vl) -> ((ElevateWord) et).setReading(DfTypeUtil.toString(vl)),
                "reading");
        setupEpg(_epgMap, et -> ((ElevateWord) et).getSuggestWord(), (et, vl) -> ((ElevateWord) et).setSuggestWord(DfTypeUtil.toString(vl)),
                "suggestWord");
        setupEpg(_epgMap, et -> ((ElevateWord) et).getUpdatedBy(), (et, vl) -> ((ElevateWord) et).setUpdatedBy(DfTypeUtil.toString(vl)),
                "updatedBy");
        setupEpg(_epgMap, et -> ((ElevateWord) et).getUpdatedTime(), (et, vl) -> ((ElevateWord) et).setUpdatedTime(DfTypeUtil.toLong(vl)),
                "updatedTime");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "elevate_word";
    protected final String _tableDispName = "elevate_word";
    protected final String _tablePropertyName = "ElevateWord";

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
    protected final ColumnInfo _columnPermissions = cci("permissions", "permissions", null, null, String[].class, "permissions", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnReading = cci("reading", "reading", null, null, String.class, "reading", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSuggestWord = cci("suggestWord", "suggestWord", null, null, String.class, "suggestWord", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedBy = cci("updatedBy", "updatedBy", null, null, String.class, "updatedBy", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUpdatedTime = cci("updatedTime", "updatedTime", null, null, Long.class, "updatedTime", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnBoost() {
        return _columnBoost;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnPermissions() {
        return _columnPermissions;
    }

    public ColumnInfo columnReading() {
        return _columnReading;
    }

    public ColumnInfo columnSuggestWord() {
        return _columnSuggestWord;
    }

    public ColumnInfo columnUpdatedBy() {
        return _columnUpdatedBy;
    }

    public ColumnInfo columnUpdatedTime() {
        return _columnUpdatedTime;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnBoost());
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnPermissions());
        ls.add(columnReading());
        ls.add(columnSuggestWord());
        ls.add(columnUpdatedBy());
        ls.add(columnUpdatedTime());
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
        return "org.codelibs.fess.es.config.exentity.ElevateWord";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.ElevateWordCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.ElevateWordBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return ElevateWord.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new ElevateWord();
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
