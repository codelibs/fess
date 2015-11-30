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
package org.codelibs.fess.es.log.bsentity.dbmeta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.log.exentity.EventLog;
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
public class EventLogDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final EventLogDbm _instance = new EventLogDbm();

    private EventLogDbm() {
    }

    public static EventLogDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((EventLog) et).getCreatedAt(), (et, vl) -> ((EventLog) et).setCreatedAt(DfTypeUtil.toLocalDateTime(vl)),
                "createdAt");
        setupEpg(_epgMap, et -> ((EventLog) et).getCreatedBy(), (et, vl) -> ((EventLog) et).setCreatedBy(DfTypeUtil.toString(vl)),
                "createdBy");
        setupEpg(_epgMap, et -> ((EventLog) et).getEventType(), (et, vl) -> ((EventLog) et).setEventType(DfTypeUtil.toString(vl)),
                "eventType");
        setupEpg(_epgMap, et -> ((EventLog) et).getMessage(), (et, vl) -> ((EventLog) et).setMessage(DfTypeUtil.toString(vl)), "message");
        setupEpg(_epgMap, et -> ((EventLog) et).getPath(), (et, vl) -> ((EventLog) et).setPath(DfTypeUtil.toString(vl)), "path");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "event_log";
    protected final String _tableDispName = "event_log";
    protected final String _tablePropertyName = "EventLog";

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
            false, false, "LocalDateTime", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreatedBy = cci("createdBy", "createdBy", null, null, String.class, "createdBy", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnEventType = cci("eventType", "eventType", null, null, String.class, "eventType", null, false, false,
            false, "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMessage = cci("message", "message", null, null, String.class, "message", null, false, false, false,
            "String", 0, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPath = cci("path", "path", null, null, String.class, "path", null, false, false, false, "String", 0,
            0, null, false, null, null, null, null, null, false);

    public ColumnInfo columnCreatedAt() {
        return _columnCreatedAt;
    }

    public ColumnInfo columnCreatedBy() {
        return _columnCreatedBy;
    }

    public ColumnInfo columnEventType() {
        return _columnEventType;
    }

    public ColumnInfo columnMessage() {
        return _columnMessage;
    }

    public ColumnInfo columnPath() {
        return _columnPath;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnCreatedAt());
        ls.add(columnCreatedBy());
        ls.add(columnEventType());
        ls.add(columnMessage());
        ls.add(columnPath());
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
        return "org.codelibs.fess.es.log.exentity.EventLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.es.log.cbean.EventLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.es.log.exbhv.EventLogBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return EventLog.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new EventLog();
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
