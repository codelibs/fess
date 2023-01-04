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
import org.codelibs.fess.es.config.bsentity.dbmeta.FailureUrlDbm;
import org.codelibs.fess.es.config.cbean.FailureUrlCB;
import org.codelibs.fess.es.config.exentity.FailureUrl;
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
public abstract class BsFailureUrlBhv extends EsAbstractBehavior<FailureUrl, FailureUrlCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.failure_url";
    }

    @Override
    public String asEsIndexType() {
        return "failure_url";
    }

    @Override
    public String asEsSearchType() {
        return "failure_url";
    }

    @Override
    public FailureUrlDbm asDBMeta() {
        return FailureUrlDbm.getInstance();
    }

    @Override
    protected <RESULT extends FailureUrl> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setConfigId(DfTypeUtil.toString(source.get("configId")));
            result.setErrorCount(DfTypeUtil.toInteger(source.get("errorCount")));
            result.setErrorLog(DfTypeUtil.toString(source.get("errorLog")));
            result.setErrorName(DfTypeUtil.toString(source.get("errorName")));
            result.setLastAccessTime(DfTypeUtil.toLong(source.get("lastAccessTime")));
            result.setThreadName(DfTypeUtil.toString(source.get("threadName")));
            result.setUrl(DfTypeUtil.toString(source.get("url")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends FailureUrl> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<FailureUrlCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<FailureUrl> selectEntity(CBCall<FailureUrlCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<FailureUrl> facadeSelectEntity(FailureUrlCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FailureUrl> OptionalEntity<ENTITY> doSelectOptionalEntity(FailureUrlCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public FailureUrlCB newConditionBean() {
        return new FailureUrlCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public FailureUrl selectEntityWithDeletedCheck(CBCall<FailureUrlCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<FailureUrl> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<FailureUrl> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends FailureUrl> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected FailureUrlCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends FailureUrl> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends FailureUrl> typeOfSelectedEntity() {
        return FailureUrl.class;
    }

    @Override
    protected Class<FailureUrl> typeOfHandlingEntity() {
        return FailureUrl.class;
    }

    @Override
    protected Class<FailureUrlCB> typeOfHandlingConditionBean() {
        return FailureUrlCB.class;
    }

    public ListResultBean<FailureUrl> selectList(CBCall<FailureUrlCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<FailureUrl> selectPage(CBCall<FailureUrlCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<FailureUrl>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<FailureUrlCB> cbLambda, EntityRowHandler<FailureUrl> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<FailureUrlCB> cbLambda, EntityRowHandler<List<FailureUrl>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(FailureUrl entity) {
        doInsert(entity, null);
    }

    public void insert(FailureUrl entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(FailureUrl entity) {
        doUpdate(entity, null);
    }

    public void update(FailureUrl entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(FailureUrl entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(FailureUrl entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(FailureUrl entity) {
        doDelete(entity, null);
    }

    public void delete(FailureUrl entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<FailureUrlCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<FailureUrl> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<FailureUrl> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<FailureUrl> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<FailureUrl> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<FailureUrl> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<FailureUrl> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<FailureUrl> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<FailureUrl> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<FailureUrl> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
