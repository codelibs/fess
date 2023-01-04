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
package org.codelibs.fess.es.config.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.config.allcommon.EsAbstractBehavior;
import org.codelibs.fess.es.config.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.config.bsentity.dbmeta.ThumbnailQueueDbm;
import org.codelibs.fess.es.config.cbean.ThumbnailQueueCB;
import org.codelibs.fess.es.config.exentity.ThumbnailQueue;
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
public abstract class BsThumbnailQueueBhv extends EsAbstractBehavior<ThumbnailQueue, ThumbnailQueueCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.thumbnail_queue";
    }

    @Override
    public String asEsIndexType() {
        return "thumbnail_queue";
    }

    @Override
    public String asEsSearchType() {
        return "thumbnail_queue";
    }

    @Override
    public ThumbnailQueueDbm asDBMeta() {
        return ThumbnailQueueDbm.getInstance();
    }

    @Override
    protected <RESULT extends ThumbnailQueue> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setGenerator(DfTypeUtil.toString(source.get("generator")));
            result.setPath(DfTypeUtil.toString(source.get("path")));
            result.setTarget(DfTypeUtil.toString(source.get("target")));
            result.setThumbnailId(DfTypeUtil.toString(source.get("thumbnail_id")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends ThumbnailQueue> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<ThumbnailQueueCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<ThumbnailQueue> selectEntity(CBCall<ThumbnailQueueCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<ThumbnailQueue> facadeSelectEntity(ThumbnailQueueCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends ThumbnailQueue> OptionalEntity<ENTITY> doSelectOptionalEntity(ThumbnailQueueCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public ThumbnailQueueCB newConditionBean() {
        return new ThumbnailQueueCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public ThumbnailQueue selectEntityWithDeletedCheck(CBCall<ThumbnailQueueCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<ThumbnailQueue> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<ThumbnailQueue> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends ThumbnailQueue> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected ThumbnailQueueCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends ThumbnailQueue> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends ThumbnailQueue> typeOfSelectedEntity() {
        return ThumbnailQueue.class;
    }

    @Override
    protected Class<ThumbnailQueue> typeOfHandlingEntity() {
        return ThumbnailQueue.class;
    }

    @Override
    protected Class<ThumbnailQueueCB> typeOfHandlingConditionBean() {
        return ThumbnailQueueCB.class;
    }

    public ListResultBean<ThumbnailQueue> selectList(CBCall<ThumbnailQueueCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<ThumbnailQueue> selectPage(CBCall<ThumbnailQueueCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<ThumbnailQueue>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<ThumbnailQueueCB> cbLambda, EntityRowHandler<ThumbnailQueue> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<ThumbnailQueueCB> cbLambda, EntityRowHandler<List<ThumbnailQueue>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(ThumbnailQueue entity) {
        doInsert(entity, null);
    }

    public void insert(ThumbnailQueue entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(ThumbnailQueue entity) {
        doUpdate(entity, null);
    }

    public void update(ThumbnailQueue entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(ThumbnailQueue entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(ThumbnailQueue entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(ThumbnailQueue entity) {
        doDelete(entity, null);
    }

    public void delete(ThumbnailQueue entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<ThumbnailQueueCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<ThumbnailQueue> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<ThumbnailQueue> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<ThumbnailQueue> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<ThumbnailQueue> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<ThumbnailQueue> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<ThumbnailQueue> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<ThumbnailQueue> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<ThumbnailQueue> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<ThumbnailQueue> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
