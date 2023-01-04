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
import org.codelibs.fess.es.config.bsentity.dbmeta.RelatedQueryDbm;
import org.codelibs.fess.es.config.cbean.RelatedQueryCB;
import org.codelibs.fess.es.config.exentity.RelatedQuery;
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
public abstract class BsRelatedQueryBhv extends EsAbstractBehavior<RelatedQuery, RelatedQueryCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.related_query";
    }

    @Override
    public String asEsIndexType() {
        return "related_query";
    }

    @Override
    public String asEsSearchType() {
        return "related_query";
    }

    @Override
    public RelatedQueryDbm asDBMeta() {
        return RelatedQueryDbm.getInstance();
    }

    @Override
    protected <RESULT extends RelatedQuery> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setQueries(toStringArray(source.get("queries")));
            result.setTerm(DfTypeUtil.toString(source.get("term")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            result.setVirtualHost(DfTypeUtil.toString(source.get("virtualHost")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends RelatedQuery> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<RelatedQueryCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<RelatedQuery> selectEntity(CBCall<RelatedQueryCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<RelatedQuery> facadeSelectEntity(RelatedQueryCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends RelatedQuery> OptionalEntity<ENTITY> doSelectOptionalEntity(RelatedQueryCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public RelatedQueryCB newConditionBean() {
        return new RelatedQueryCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public RelatedQuery selectEntityWithDeletedCheck(CBCall<RelatedQueryCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<RelatedQuery> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<RelatedQuery> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends RelatedQuery> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected RelatedQueryCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends RelatedQuery> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends RelatedQuery> typeOfSelectedEntity() {
        return RelatedQuery.class;
    }

    @Override
    protected Class<RelatedQuery> typeOfHandlingEntity() {
        return RelatedQuery.class;
    }

    @Override
    protected Class<RelatedQueryCB> typeOfHandlingConditionBean() {
        return RelatedQueryCB.class;
    }

    public ListResultBean<RelatedQuery> selectList(CBCall<RelatedQueryCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<RelatedQuery> selectPage(CBCall<RelatedQueryCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<RelatedQuery>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<RelatedQueryCB> cbLambda, EntityRowHandler<RelatedQuery> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<RelatedQueryCB> cbLambda, EntityRowHandler<List<RelatedQuery>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(RelatedQuery entity) {
        doInsert(entity, null);
    }

    public void insert(RelatedQuery entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(RelatedQuery entity) {
        doUpdate(entity, null);
    }

    public void update(RelatedQuery entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(RelatedQuery entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(RelatedQuery entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(RelatedQuery entity) {
        doDelete(entity, null);
    }

    public void delete(RelatedQuery entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<RelatedQueryCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<RelatedQuery> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<RelatedQuery> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<RelatedQuery> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<RelatedQuery> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<RelatedQuery> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<RelatedQuery> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<RelatedQuery> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<RelatedQuery> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<RelatedQuery> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
