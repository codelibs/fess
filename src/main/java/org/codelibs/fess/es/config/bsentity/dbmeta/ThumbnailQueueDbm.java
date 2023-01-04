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

import org.codelibs.fess.es.config.exentity.ThumbnailQueue;
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
public class ThumbnailQueueDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final ThumbnailQueueDbm _instance = new ThumbnailQueueDbm();

    private ThumbnailQueueDbm() {
    }

    public static ThumbnailQueueDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((ThumbnailQueue) et).getCreatedBy(),
                (et, vl) -> ((ThumbnailQueue) et).setCreatedBy(DfTypeUtil.toString(vl)), "createdBy");
        setupEpg(_epgMap, et -> ((ThumbnailQueue) et).getCreatedTime(),
                (et, vl) -> ((ThumbnailQueue) et).setCreatedTime(DfTypeUtil.toLong(vl)), "createdTime");
        setupEpg(_epgMap, et -> ((ThumbnailQueue) et).getGenerator(),
                (et, vl) -> ((ThumbnailQueue) et).setGenerator(DfTypeUtil.toString(vl)), "generator");
        setupEpg(_epgMap, et -> ((ThumbnailQueue) et).getPath(), (et, vl) -> ((ThumbnailQueue) et).setPath(DfTypeUtil.toString(vl)),
                "path");
        setupEpg(_epgMap, et -> ((ThumbnailQueue) et).getTarget(), (et, vl) -> ((ThumbnailQueue) et).setTarget(DfTypeUtil.toString(vl)),
                "target");
        setupEpg(_epgMap, et -> ((ThumbnailQueue) et).getThumbnailId(),
                (et, vl) -> ((ThumbnailQueue) et).setThumbnailId(DfTypeUtil.toString(vl)), "thumbnailId");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "thumbnail_queue";
    protected final String _tableDispName = "thumbnail_queue";
    protected final String _tablePropertyName = "ThumbnailQueue";

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
    protected final ColumnInfo _columnGenerator = cci("generator", "generator", null, null, String.class, "generator", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPath = cci("path", "path", null, null, String.class, "path", null, false, false, false, "keyword", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnTarget = cci("target", "target", null, null, String.class, "target", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnThumbnailId = cci("thumbnail_id", "thumbnail_id", null, null, String.class, "thumbnailId", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnCreatedTime() {
        return _columnCreatedTime;
    }

    public ColumnInfo columnGenerator() {
        return _columnGenerator;
    }

    public ColumnInfo columnPath() {
        return _columnPath;
    }

    public ColumnInfo columnTarget() {
        return _columnTarget;
    }

    public ColumnInfo columnThumbnailId() {
        return _columnThumbnailId;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnCreatedBy());
        ls.add(columnCreatedTime());
        ls.add(columnGenerator());
        ls.add(columnPath());
        ls.add(columnTarget());
        ls.add(columnThumbnailId());
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
        return "org.codelibs.fess.es.config.exentity.ThumbnailQueue";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.config.cbean.ThumbnailQueueCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.config.exbhv.ThumbnailQueueBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return ThumbnailQueue.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new ThumbnailQueue();
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
