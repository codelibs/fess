/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.opensearch.log.bsentity.dbmeta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.opensearch.log.exentity.ChatLog;
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
public class ChatLogDbm extends AbstractDBMeta {

    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final ChatLogDbm _instance = new ChatLogDbm();

    private ChatLogDbm() {
    }

    public static ChatLogDbm getInstance() {
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
        setupEpg(_epgMap, et -> ((ChatLog) et).getAccessType(), (et, vl) -> ((ChatLog) et).setAccessType(DfTypeUtil.toString(vl)),
                "accessType");
        setupEpg(_epgMap, et -> ((ChatLog) et).getChatSessionId(), (et, vl) -> ((ChatLog) et).setChatSessionId(DfTypeUtil.toString(vl)),
                "chatSessionId");
        setupEpg(_epgMap, et -> ((ChatLog) et).getChatType(), (et, vl) -> ((ChatLog) et).setChatType(DfTypeUtil.toString(vl)), "chatType");
        setupEpg(_epgMap, et -> ((ChatLog) et).getCompletionTokens(), (et, vl) -> ((ChatLog) et).setCompletionTokens(DfTypeUtil.toLong(vl)),
                "completionTokens");
        setupEpg(_epgMap, et -> ((ChatLog) et).getErrorCode(), (et, vl) -> ((ChatLog) et).setErrorCode(DfTypeUtil.toString(vl)),
                "errorCode");
        setupEpg(_epgMap, et -> ((ChatLog) et).getIntent(), (et, vl) -> ((ChatLog) et).setIntent(DfTypeUtil.toString(vl)), "intent");
        setupEpg(_epgMap, et -> ((ChatLog) et).getLlmCalls(), (et, vl) -> ((ChatLog) et).setLlmCalls(DfTypeUtil.toInteger(vl)), "llmCalls");
        setupEpg(_epgMap, et -> ((ChatLog) et).getLlmName(), (et, vl) -> ((ChatLog) et).setLlmName(DfTypeUtil.toString(vl)), "llmName");
        setupEpg(_epgMap, et -> ((ChatLog) et).getModel(), (et, vl) -> ((ChatLog) et).setModel(DfTypeUtil.toString(vl)), "model");
        setupEpg(_epgMap, et -> ((ChatLog) et).getPromptTokens(), (et, vl) -> ((ChatLog) et).setPromptTokens(DfTypeUtil.toLong(vl)),
                "promptTokens");
        setupEpg(_epgMap, et -> ((ChatLog) et).getRequestedAt(), (et, vl) -> ((ChatLog) et).setRequestedAt(DfTypeUtil.toLocalDateTime(vl)),
                "requestedAt");
        setupEpg(_epgMap, et -> ((ChatLog) et).getResponseTime(), (et, vl) -> ((ChatLog) et).setResponseTime(DfTypeUtil.toLong(vl)),
                "responseTime");
        setupEpg(_epgMap, et -> ((ChatLog) et).getRoles(), (et, vl) -> ((ChatLog) et).setRoles((String[]) vl), "roles");
        setupEpg(_epgMap, et -> ((ChatLog) et).getSearchQueryId(), (et, vl) -> ((ChatLog) et).setSearchQueryId(DfTypeUtil.toString(vl)),
                "searchQueryId");
        setupEpg(_epgMap, et -> ((ChatLog) et).getSourceCount(), (et, vl) -> ((ChatLog) et).setSourceCount(DfTypeUtil.toInteger(vl)),
                "sourceCount");
        setupEpg(_epgMap, et -> ((ChatLog) et).getStatus(), (et, vl) -> ((ChatLog) et).setStatus(DfTypeUtil.toString(vl)), "status");
        setupEpg(_epgMap, et -> ((ChatLog) et).getTotalTokens(), (et, vl) -> ((ChatLog) et).setTotalTokens(DfTypeUtil.toLong(vl)),
                "totalTokens");
        setupEpg(_epgMap, et -> ((ChatLog) et).getUser(), (et, vl) -> ((ChatLog) et).setUser(DfTypeUtil.toString(vl)), "user");
        setupEpg(_epgMap, et -> ((ChatLog) et).getUserSessionId(), (et, vl) -> ((ChatLog) et).setUserSessionId(DfTypeUtil.toString(vl)),
                "userSessionId");
        setupEpg(_epgMap, et -> ((ChatLog) et).getVirtualHost(), (et, vl) -> ((ChatLog) et).setVirtualHost(DfTypeUtil.toString(vl)),
                "virtualHost");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "chat_log";
    protected final String _tableDispName = "chat_log";
    protected final String _tablePropertyName = "ChatLog";

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
    protected final ColumnInfo _columnChatSessionId = cci("chatSessionId", "chatSessionId", null, null, String.class, "chatSessionId", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnChatType = cci("chatType", "chatType", null, null, String.class, "chatType", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCompletionTokens = cci("completionTokens", "completionTokens", null, null, Long.class,
            "completionTokens", null, false, false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnErrorCode = cci("errorCode", "errorCode", null, null, String.class, "errorCode", null, false, false,
            false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnIntent = cci("intent", "intent", null, null, String.class, "intent", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLlmCalls = cci("llmCalls", "llmCalls", null, null, Integer.class, "llmCalls", null, false, false,
            false, "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLlmName = cci("llmName", "llmName", null, null, String.class, "llmName", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnModel = cci("model", "model", null, null, String.class, "model", null, false, false, false, "keyword",
            0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnPromptTokens = cci("promptTokens", "promptTokens", null, null, Long.class, "promptTokens", null,
            false, false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnRequestedAt = cci("requestedAt", "requestedAt", null, null, LocalDateTime.class, "requestedAt", null,
            false, false, false, "LocalDateTime", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnResponseTime = cci("responseTime", "responseTime", null, null, Long.class, "responseTime", null,
            false, false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnRoles = cci("roles", "roles", null, null, String[].class, "roles", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSearchQueryId = cci("searchQueryId", "searchQueryId", null, null, String.class, "searchQueryId", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnSourceCount = cci("sourceCount", "sourceCount", null, null, Integer.class, "sourceCount", null, false,
            false, false, "Integer", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnStatus = cci("status", "status", null, null, String.class, "status", null, false, false, false,
            "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnTotalTokens = cci("totalTokens", "totalTokens", null, null, Long.class, "totalTokens", null, false,
            false, false, "Long", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUser = cci("user", "user", null, null, String.class, "user", null, false, false, false, "keyword", 0,
            0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUserSessionId = cci("userSessionId", "userSessionId", null, null, String.class, "userSessionId", null,
            false, false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnVirtualHost = cci("virtualHost", "virtualHost", null, null, String.class, "virtualHost", null, false,
            false, false, "keyword", 0, 0, null, null, false, null, null, null, null, null, false);

    public ColumnInfo columnAccessType() {
        return _columnAccessType;
    }

    public ColumnInfo columnChatSessionId() {
        return _columnChatSessionId;
    }

    public ColumnInfo columnChatType() {
        return _columnChatType;
    }

    public ColumnInfo columnCompletionTokens() {
        return _columnCompletionTokens;
    }

    public ColumnInfo columnErrorCode() {
        return _columnErrorCode;
    }

    public ColumnInfo columnIntent() {
        return _columnIntent;
    }

    public ColumnInfo columnLlmCalls() {
        return _columnLlmCalls;
    }

    public ColumnInfo columnLlmName() {
        return _columnLlmName;
    }

    public ColumnInfo columnModel() {
        return _columnModel;
    }

    public ColumnInfo columnPromptTokens() {
        return _columnPromptTokens;
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

    public ColumnInfo columnSearchQueryId() {
        return _columnSearchQueryId;
    }

    public ColumnInfo columnSourceCount() {
        return _columnSourceCount;
    }

    public ColumnInfo columnStatus() {
        return _columnStatus;
    }

    public ColumnInfo columnTotalTokens() {
        return _columnTotalTokens;
    }

    public ColumnInfo columnUser() {
        return _columnUser;
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
        ls.add(columnChatSessionId());
        ls.add(columnChatType());
        ls.add(columnCompletionTokens());
        ls.add(columnErrorCode());
        ls.add(columnIntent());
        ls.add(columnLlmCalls());
        ls.add(columnLlmName());
        ls.add(columnModel());
        ls.add(columnPromptTokens());
        ls.add(columnRequestedAt());
        ls.add(columnResponseTime());
        ls.add(columnRoles());
        ls.add(columnSearchQueryId());
        ls.add(columnSourceCount());
        ls.add(columnStatus());
        ls.add(columnTotalTokens());
        ls.add(columnUser());
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
        return "org.codelibs.fess.opensearch.log.exentity.ChatLog";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.fess.opensearch.log.cbean.ChatLogCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.fess.opensearch.log.exbhv.ChatLogBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<? extends Entity> getEntityType() {
        return ChatLog.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return new ChatLog();
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
