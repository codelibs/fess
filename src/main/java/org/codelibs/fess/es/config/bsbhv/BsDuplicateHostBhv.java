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
import org.codelibs.fess.es.config.bsentity.dbmeta.DuplicateHostDbm;
import org.codelibs.fess.es.config.cbean.DuplicateHostCB;
import org.codelibs.fess.es.config.exentity.DuplicateHost;
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
public abstract class BsDuplicateHostBhv extends EsAbstractBehavior<DuplicateHost, DuplicateHostCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.duplicate_host";
    }

    @Override
    public String asEsIndexType() {
        return "duplicate_host";
    }

    @Override
    public String asEsSearchType() {
        return "duplicate_host";
    }

    @Override
    public DuplicateHostDbm asDBMeta() {
        return DuplicateHostDbm.getInstance();
    }

    @Override
    protected <RESULT extends DuplicateHost> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setDuplicateHostName(DfTypeUtil.toString(source.get("duplicateHostName")));
            result.setRegularName(DfTypeUtil.toString(source.get("regularName")));
            result.setSortOrder(DfTypeUtil.toInteger(source.get("sortOrder")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends DuplicateHost> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<DuplicateHostCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<DuplicateHost> selectEntity(CBCall<DuplicateHostCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<DuplicateHost> facadeSelectEntity(DuplicateHostCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends DuplicateHost> OptionalEntity<ENTITY> doSelectOptionalEntity(DuplicateHostCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public DuplicateHostCB newConditionBean() {
        return new DuplicateHostCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public DuplicateHost selectEntityWithDeletedCheck(CBCall<DuplicateHostCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<DuplicateHost> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<DuplicateHost> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends DuplicateHost> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected DuplicateHostCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends DuplicateHost> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends DuplicateHost> typeOfSelectedEntity() {
        return DuplicateHost.class;
    }

    @Override
    protected Class<DuplicateHost> typeOfHandlingEntity() {
        return DuplicateHost.class;
    }

    @Override
    protected Class<DuplicateHostCB> typeOfHandlingConditionBean() {
        return DuplicateHostCB.class;
    }

    public ListResultBean<DuplicateHost> selectList(CBCall<DuplicateHostCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<DuplicateHost> selectPage(CBCall<DuplicateHostCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<DuplicateHost>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<DuplicateHostCB> cbLambda, EntityRowHandler<DuplicateHost> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<DuplicateHostCB> cbLambda, EntityRowHandler<List<DuplicateHost>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(DuplicateHost entity) {
        doInsert(entity, null);
    }

    public void insert(DuplicateHost entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(DuplicateHost entity) {
        doUpdate(entity, null);
    }

    public void update(DuplicateHost entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(DuplicateHost entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(DuplicateHost entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(DuplicateHost entity) {
        doDelete(entity, null);
    }

    public void delete(DuplicateHost entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<DuplicateHostCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<DuplicateHost> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<DuplicateHost> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<DuplicateHost> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<DuplicateHost> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<DuplicateHost> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<DuplicateHost> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<DuplicateHost> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<DuplicateHost> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<DuplicateHost> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
