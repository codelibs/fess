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
package org.codelibs.fess.opensearch.log.bsentity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.opensearch.log.allcommon.EsAbstractEntity;
import org.codelibs.fess.opensearch.log.bsentity.dbmeta.ChatLogDbm;

/**
 * ${table.comment}
 * @author ESFlute (using FreeGen)
 */
public class BsChatLog extends EsAbstractEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;
    protected static final Class<?> suppressUnusedImportLocalDateTime = LocalDateTime.class;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** accessType */
    protected String accessType;

    /** chatSessionId */
    protected String chatSessionId;

    /** chatType */
    protected String chatType;

    /** completionTokens */
    protected Long completionTokens;

    /** errorCode */
    protected String errorCode;

    /** intent */
    protected String intent;

    /** llmCalls */
    protected Integer llmCalls;

    /** llmName */
    protected String llmName;

    /** model */
    protected String model;

    /** promptTokens */
    protected Long promptTokens;

    /** requestedAt */
    protected LocalDateTime requestedAt;

    /** responseTime */
    protected Long responseTime;

    /** roles */
    protected String[] roles;

    /** searchQueryId */
    protected String searchQueryId;

    /** sourceCount */
    protected Integer sourceCount;

    /** status */
    protected String status;

    /** totalTokens */
    protected Long totalTokens;

    /** user */
    protected String user;

    /** userSessionId */
    protected String userSessionId;

    /** virtualHost */
    protected String virtualHost;

    // [Referrers] *comment only

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    public ChatLogDbm asDBMeta() {
        return ChatLogDbm.getInstance();
    }

    @Override
    public String asTableDbName() {
        return "chat_log";
    }

    // ===================================================================================
    //                                                                              Source
    //                                                                              ======
    @Override
    public Map<String, Object> toSource() {
        Map<String, Object> sourceMap = new HashMap<>();
        if (accessType != null) {
            addFieldToSource(sourceMap, "accessType", accessType);
        }
        if (chatSessionId != null) {
            addFieldToSource(sourceMap, "chatSessionId", chatSessionId);
        }
        if (chatType != null) {
            addFieldToSource(sourceMap, "chatType", chatType);
        }
        if (completionTokens != null) {
            addFieldToSource(sourceMap, "completionTokens", completionTokens);
        }
        if (errorCode != null) {
            addFieldToSource(sourceMap, "errorCode", errorCode);
        }
        if (intent != null) {
            addFieldToSource(sourceMap, "intent", intent);
        }
        if (llmCalls != null) {
            addFieldToSource(sourceMap, "llmCalls", llmCalls);
        }
        if (llmName != null) {
            addFieldToSource(sourceMap, "llmName", llmName);
        }
        if (model != null) {
            addFieldToSource(sourceMap, "model", model);
        }
        if (promptTokens != null) {
            addFieldToSource(sourceMap, "promptTokens", promptTokens);
        }
        if (requestedAt != null) {
            addFieldToSource(sourceMap, "requestedAt", requestedAt);
        }
        if (responseTime != null) {
            addFieldToSource(sourceMap, "responseTime", responseTime);
        }
        if (roles != null) {
            addFieldToSource(sourceMap, "roles", roles);
        }
        if (searchQueryId != null) {
            addFieldToSource(sourceMap, "searchQueryId", searchQueryId);
        }
        if (sourceCount != null) {
            addFieldToSource(sourceMap, "sourceCount", sourceCount);
        }
        if (status != null) {
            addFieldToSource(sourceMap, "status", status);
        }
        if (totalTokens != null) {
            addFieldToSource(sourceMap, "totalTokens", totalTokens);
        }
        if (user != null) {
            addFieldToSource(sourceMap, "user", user);
        }
        if (userSessionId != null) {
            addFieldToSource(sourceMap, "userSessionId", userSessionId);
        }
        if (virtualHost != null) {
            addFieldToSource(sourceMap, "virtualHost", virtualHost);
        }
        return sourceMap;
    }

    protected void addFieldToSource(Map<String, Object> sourceMap, String field, Object value) {
        sourceMap.put(field, value);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(accessType);
        sb.append(dm).append(chatSessionId);
        sb.append(dm).append(chatType);
        sb.append(dm).append(completionTokens);
        sb.append(dm).append(errorCode);
        sb.append(dm).append(intent);
        sb.append(dm).append(llmCalls);
        sb.append(dm).append(llmName);
        sb.append(dm).append(model);
        sb.append(dm).append(promptTokens);
        sb.append(dm).append(requestedAt);
        sb.append(dm).append(responseTime);
        sb.append(dm).append(roles);
        sb.append(dm).append(searchQueryId);
        sb.append(dm).append(sourceCount);
        sb.append(dm).append(status);
        sb.append(dm).append(totalTokens);
        sb.append(dm).append(user);
        sb.append(dm).append(userSessionId);
        sb.append(dm).append(virtualHost);
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getAccessType() {
        checkSpecifiedProperty("accessType");
        return convertEmptyToNull(accessType);
    }

    public void setAccessType(String value) {
        registerModifiedProperty("accessType");
        this.accessType = value;
    }

    public String getChatSessionId() {
        checkSpecifiedProperty("chatSessionId");
        return convertEmptyToNull(chatSessionId);
    }

    public void setChatSessionId(String value) {
        registerModifiedProperty("chatSessionId");
        this.chatSessionId = value;
    }

    public String getChatType() {
        checkSpecifiedProperty("chatType");
        return convertEmptyToNull(chatType);
    }

    public void setChatType(String value) {
        registerModifiedProperty("chatType");
        this.chatType = value;
    }

    public Long getCompletionTokens() {
        checkSpecifiedProperty("completionTokens");
        return completionTokens;
    }

    public void setCompletionTokens(Long value) {
        registerModifiedProperty("completionTokens");
        this.completionTokens = value;
    }

    public String getErrorCode() {
        checkSpecifiedProperty("errorCode");
        return convertEmptyToNull(errorCode);
    }

    public void setErrorCode(String value) {
        registerModifiedProperty("errorCode");
        this.errorCode = value;
    }

    public String getIntent() {
        checkSpecifiedProperty("intent");
        return convertEmptyToNull(intent);
    }

    public void setIntent(String value) {
        registerModifiedProperty("intent");
        this.intent = value;
    }

    public Integer getLlmCalls() {
        checkSpecifiedProperty("llmCalls");
        return llmCalls;
    }

    public void setLlmCalls(Integer value) {
        registerModifiedProperty("llmCalls");
        this.llmCalls = value;
    }

    public String getLlmName() {
        checkSpecifiedProperty("llmName");
        return convertEmptyToNull(llmName);
    }

    public void setLlmName(String value) {
        registerModifiedProperty("llmName");
        this.llmName = value;
    }

    public String getModel() {
        checkSpecifiedProperty("model");
        return convertEmptyToNull(model);
    }

    public void setModel(String value) {
        registerModifiedProperty("model");
        this.model = value;
    }

    public Long getPromptTokens() {
        checkSpecifiedProperty("promptTokens");
        return promptTokens;
    }

    public void setPromptTokens(Long value) {
        registerModifiedProperty("promptTokens");
        this.promptTokens = value;
    }

    public LocalDateTime getRequestedAt() {
        checkSpecifiedProperty("requestedAt");
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime value) {
        registerModifiedProperty("requestedAt");
        this.requestedAt = value;
    }

    public Long getResponseTime() {
        checkSpecifiedProperty("responseTime");
        return responseTime;
    }

    public void setResponseTime(Long value) {
        registerModifiedProperty("responseTime");
        this.responseTime = value;
    }

    public String[] getRoles() {
        checkSpecifiedProperty("roles");
        return roles;
    }

    public void setRoles(String[] value) {
        registerModifiedProperty("roles");
        this.roles = value;
    }

    public String getSearchQueryId() {
        checkSpecifiedProperty("searchQueryId");
        return convertEmptyToNull(searchQueryId);
    }

    public void setSearchQueryId(String value) {
        registerModifiedProperty("searchQueryId");
        this.searchQueryId = value;
    }

    public Integer getSourceCount() {
        checkSpecifiedProperty("sourceCount");
        return sourceCount;
    }

    public void setSourceCount(Integer value) {
        registerModifiedProperty("sourceCount");
        this.sourceCount = value;
    }

    public String getStatus() {
        checkSpecifiedProperty("status");
        return convertEmptyToNull(status);
    }

    public void setStatus(String value) {
        registerModifiedProperty("status");
        this.status = value;
    }

    public Long getTotalTokens() {
        checkSpecifiedProperty("totalTokens");
        return totalTokens;
    }

    public void setTotalTokens(Long value) {
        registerModifiedProperty("totalTokens");
        this.totalTokens = value;
    }

    public String getUser() {
        checkSpecifiedProperty("user");
        return convertEmptyToNull(user);
    }

    public void setUser(String value) {
        registerModifiedProperty("user");
        this.user = value;
    }

    public String getUserSessionId() {
        checkSpecifiedProperty("userSessionId");
        return convertEmptyToNull(userSessionId);
    }

    public void setUserSessionId(String value) {
        registerModifiedProperty("userSessionId");
        this.userSessionId = value;
    }

    public String getVirtualHost() {
        checkSpecifiedProperty("virtualHost");
        return convertEmptyToNull(virtualHost);
    }

    public void setVirtualHost(String value) {
        registerModifiedProperty("virtualHost");
        this.virtualHost = value;
    }
}
