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
package org.codelibs.fess.opensearch.log.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.opensearch.log.allcommon.EsAbstractBehavior;
import org.codelibs.fess.opensearch.log.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.opensearch.log.bsentity.dbmeta.ChatLogDbm;
import org.codelibs.fess.opensearch.log.cbean.ChatLogCB;
import org.codelibs.fess.opensearch.log.exentity.ChatLog;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.util.DfTypeUtil;
import org.codelibs.fesen.opensearch.action.bulk.BulkRequestBuilder;
import org.codelibs.fesen.opensearch.action.delete.DeleteRequestBuilder;
import org.codelibs.fesen.opensearch.action.index.IndexRequestBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsChatLogBhv extends EsAbstractBehavior<ChatLog, ChatLogCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_log.chat_log";
    }

    @Override
    public String asEsIndexType() {
        return "chat_log";
    }

    @Override
    public String asEsSearchType() {
        return "chat_log";
    }

    @Override
    public ChatLogDbm asDBMeta() {
        return ChatLogDbm.getInstance();
    }

    @Override
    protected <RESULT extends ChatLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setAccessType(DfTypeUtil.toString(source.get("accessType")));
            result.setChatSessionId(DfTypeUtil.toString(source.get("chatSessionId")));
            result.setChatType(DfTypeUtil.toString(source.get("chatType")));
            result.setCompletionTokens(DfTypeUtil.toLong(source.get("completionTokens")));
            result.setErrorCode(DfTypeUtil.toString(source.get("errorCode")));
            result.setIntent(DfTypeUtil.toString(source.get("intent")));
            result.setLlmCalls(DfTypeUtil.toInteger(source.get("llmCalls")));
            result.setLlmName(DfTypeUtil.toString(source.get("llmName")));
            result.setModel(DfTypeUtil.toString(source.get("model")));
            result.setPromptTokens(DfTypeUtil.toLong(source.get("promptTokens")));
            result.setRequestedAt(toLocalDateTime(source.get("requestedAt")));
            result.setResponseTime(DfTypeUtil.toLong(source.get("responseTime")));
            result.setRoles(toStringArray(source.get("roles")));
            result.setSearchQueryId(DfTypeUtil.toString(source.get("searchQueryId")));
            result.setSourceCount(DfTypeUtil.toInteger(source.get("sourceCount")));
            result.setStatus(DfTypeUtil.toString(source.get("status")));
            result.setTotalTokens(DfTypeUtil.toLong(source.get("totalTokens")));
            result.setUser(DfTypeUtil.toString(source.get("user")));
            result.setUserSessionId(DfTypeUtil.toString(source.get("userSessionId")));
            result.setVirtualHost(DfTypeUtil.toString(source.get("virtualHost")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends ChatLog> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<ChatLogCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<ChatLog> selectEntity(CBCall<ChatLogCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<ChatLog> facadeSelectEntity(ChatLogCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends ChatLog> OptionalEntity<ENTITY> doSelectOptionalEntity(ChatLogCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public ChatLogCB newConditionBean() {
        return new ChatLogCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public ChatLog selectEntityWithDeletedCheck(CBCall<ChatLogCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<ChatLog> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<ChatLog> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends ChatLog> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected ChatLogCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends ChatLog> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends ChatLog> typeOfSelectedEntity() {
        return ChatLog.class;
    }

    @Override
    protected Class<ChatLog> typeOfHandlingEntity() {
        return ChatLog.class;
    }

    @Override
    protected Class<ChatLogCB> typeOfHandlingConditionBean() {
        return ChatLogCB.class;
    }

    public ListResultBean<ChatLog> selectList(CBCall<ChatLogCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<ChatLog> selectPage(CBCall<ChatLogCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<ChatLog>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<ChatLogCB> cbLambda, EntityRowHandler<ChatLog> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<ChatLogCB> cbLambda, EntityRowHandler<List<ChatLog>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(ChatLog entity) {
        doInsert(entity, null);
    }

    public void insert(ChatLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(ChatLog entity) {
        doUpdate(entity, null);
    }

    public void update(ChatLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(ChatLog entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(ChatLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(ChatLog entity) {
        doDelete(entity, null);
    }

    public void delete(ChatLog entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<ChatLogCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<ChatLog> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<ChatLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<ChatLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<ChatLog> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<ChatLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<ChatLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<ChatLog> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<ChatLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<ChatLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
