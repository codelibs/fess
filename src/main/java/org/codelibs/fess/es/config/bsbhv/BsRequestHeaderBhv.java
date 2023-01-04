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
import org.codelibs.fess.es.config.bsentity.dbmeta.RequestHeaderDbm;
import org.codelibs.fess.es.config.cbean.RequestHeaderCB;
import org.codelibs.fess.es.config.exentity.RequestHeader;
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
public abstract class BsRequestHeaderBhv extends EsAbstractBehavior<RequestHeader, RequestHeaderCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.request_header";
    }

    @Override
    public String asEsIndexType() {
        return "request_header";
    }

    @Override
    public String asEsSearchType() {
        return "request_header";
    }

    @Override
    public RequestHeaderDbm asDBMeta() {
        return RequestHeaderDbm.getInstance();
    }

    @Override
    protected <RESULT extends RequestHeader> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            result.setValue(DfTypeUtil.toString(source.get("value")));
            result.setWebConfigId(DfTypeUtil.toString(source.get("webConfigId")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends RequestHeader> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<RequestHeaderCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<RequestHeader> selectEntity(CBCall<RequestHeaderCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<RequestHeader> facadeSelectEntity(RequestHeaderCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends RequestHeader> OptionalEntity<ENTITY> doSelectOptionalEntity(RequestHeaderCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public RequestHeaderCB newConditionBean() {
        return new RequestHeaderCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public RequestHeader selectEntityWithDeletedCheck(CBCall<RequestHeaderCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<RequestHeader> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<RequestHeader> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends RequestHeader> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected RequestHeaderCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends RequestHeader> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends RequestHeader> typeOfSelectedEntity() {
        return RequestHeader.class;
    }

    @Override
    protected Class<RequestHeader> typeOfHandlingEntity() {
        return RequestHeader.class;
    }

    @Override
    protected Class<RequestHeaderCB> typeOfHandlingConditionBean() {
        return RequestHeaderCB.class;
    }

    public ListResultBean<RequestHeader> selectList(CBCall<RequestHeaderCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<RequestHeader> selectPage(CBCall<RequestHeaderCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<RequestHeader>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<RequestHeaderCB> cbLambda, EntityRowHandler<RequestHeader> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<RequestHeaderCB> cbLambda, EntityRowHandler<List<RequestHeader>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(RequestHeader entity) {
        doInsert(entity, null);
    }

    public void insert(RequestHeader entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(RequestHeader entity) {
        doUpdate(entity, null);
    }

    public void update(RequestHeader entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(RequestHeader entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(RequestHeader entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(RequestHeader entity) {
        doDelete(entity, null);
    }

    public void delete(RequestHeader entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<RequestHeaderCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<RequestHeader> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<RequestHeader> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<RequestHeader> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<RequestHeader> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<RequestHeader> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<RequestHeader> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<RequestHeader> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<RequestHeader> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<RequestHeader> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
