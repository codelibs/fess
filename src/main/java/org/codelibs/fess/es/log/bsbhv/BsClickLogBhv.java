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
package org.codelibs.fess.es.log.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.log.allcommon.EsAbstractBehavior;
import org.codelibs.fess.es.log.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.log.bsentity.dbmeta.ClickLogDbm;
import org.codelibs.fess.es.log.cbean.ClickLogCB;
import org.codelibs.fess.es.log.exentity.ClickLog;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.util.DfTypeUtil;
import org.opensearch.action.bulk.BulkRequestBuilder;
import org.opensearch.action.delete.DeleteRequestBuilder;
import org.opensearch.action.index.IndexRequestBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsClickLogBhv extends EsAbstractBehavior<ClickLog, ClickLogCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_log.click_log";
    }

    @Override
    public String asEsIndexType() {
        return "click_log";
    }

    @Override
    public String asEsSearchType() {
        return "click_log";
    }

    @Override
    public ClickLogDbm asDBMeta() {
        return ClickLogDbm.getInstance();
    }

    @Override
    protected <RESULT extends ClickLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setUrlId(DfTypeUtil.toString(source.get("urlId")));
            result.setDocId(DfTypeUtil.toString(source.get("docId")));
            result.setOrder(DfTypeUtil.toInteger(source.get("order")));
            result.setQueryId(DfTypeUtil.toString(source.get("queryId")));
            result.setQueryRequestedAt(toLocalDateTime(source.get("queryRequestedAt")));
            result.setRequestedAt(toLocalDateTime(source.get("requestedAt")));
            result.setUrl(DfTypeUtil.toString(source.get("url")));
            result.setUserSessionId(DfTypeUtil.toString(source.get("userSessionId")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends ClickLog> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<ClickLogCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<ClickLog> selectEntity(CBCall<ClickLogCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<ClickLog> facadeSelectEntity(ClickLogCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends ClickLog> OptionalEntity<ENTITY> doSelectOptionalEntity(ClickLogCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public ClickLogCB newConditionBean() {
        return new ClickLogCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public ClickLog selectEntityWithDeletedCheck(CBCall<ClickLogCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<ClickLog> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<ClickLog> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends ClickLog> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected ClickLogCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends ClickLog> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends ClickLog> typeOfSelectedEntity() {
        return ClickLog.class;
    }

    @Override
    protected Class<ClickLog> typeOfHandlingEntity() {
        return ClickLog.class;
    }

    @Override
    protected Class<ClickLogCB> typeOfHandlingConditionBean() {
        return ClickLogCB.class;
    }

    public ListResultBean<ClickLog> selectList(CBCall<ClickLogCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<ClickLog> selectPage(CBCall<ClickLogCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<ClickLog>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<ClickLogCB> cbLambda, EntityRowHandler<ClickLog> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<ClickLogCB> cbLambda, EntityRowHandler<List<ClickLog>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(ClickLog entity) {
        doInsert(entity, null);
    }

    public void insert(ClickLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(ClickLog entity) {
        doUpdate(entity, null);
    }

    public void update(ClickLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(ClickLog entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(ClickLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(ClickLog entity) {
        doDelete(entity, null);
    }

    public void delete(ClickLog entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<ClickLogCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<ClickLog> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<ClickLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<ClickLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<ClickLog> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<ClickLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<ClickLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<ClickLog> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<ClickLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<ClickLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
