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
import org.codelibs.fess.es.config.bsentity.dbmeta.KeyMatchDbm;
import org.codelibs.fess.es.config.cbean.KeyMatchCB;
import org.codelibs.fess.es.config.exentity.KeyMatch;
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
public abstract class BsKeyMatchBhv extends EsAbstractBehavior<KeyMatch, KeyMatchCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.key_match";
    }

    @Override
    public String asEsIndexType() {
        return "key_match";
    }

    @Override
    public String asEsSearchType() {
        return "key_match";
    }

    @Override
    public KeyMatchDbm asDBMeta() {
        return KeyMatchDbm.getInstance();
    }

    @Override
    protected <RESULT extends KeyMatch> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setBoost(DfTypeUtil.toFloat(source.get("boost")));
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setMaxSize(DfTypeUtil.toInteger(source.get("maxSize")));
            result.setQuery(DfTypeUtil.toString(source.get("query")));
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

    protected <RESULT extends KeyMatch> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<KeyMatchCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<KeyMatch> selectEntity(CBCall<KeyMatchCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<KeyMatch> facadeSelectEntity(KeyMatchCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends KeyMatch> OptionalEntity<ENTITY> doSelectOptionalEntity(KeyMatchCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public KeyMatchCB newConditionBean() {
        return new KeyMatchCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public KeyMatch selectEntityWithDeletedCheck(CBCall<KeyMatchCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<KeyMatch> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<KeyMatch> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends KeyMatch> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected KeyMatchCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends KeyMatch> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends KeyMatch> typeOfSelectedEntity() {
        return KeyMatch.class;
    }

    @Override
    protected Class<KeyMatch> typeOfHandlingEntity() {
        return KeyMatch.class;
    }

    @Override
    protected Class<KeyMatchCB> typeOfHandlingConditionBean() {
        return KeyMatchCB.class;
    }

    public ListResultBean<KeyMatch> selectList(CBCall<KeyMatchCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<KeyMatch> selectPage(CBCall<KeyMatchCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<KeyMatch>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<KeyMatchCB> cbLambda, EntityRowHandler<KeyMatch> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<KeyMatchCB> cbLambda, EntityRowHandler<List<KeyMatch>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(KeyMatch entity) {
        doInsert(entity, null);
    }

    public void insert(KeyMatch entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(KeyMatch entity) {
        doUpdate(entity, null);
    }

    public void update(KeyMatch entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(KeyMatch entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(KeyMatch entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(KeyMatch entity) {
        doDelete(entity, null);
    }

    public void delete(KeyMatch entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<KeyMatchCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<KeyMatch> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<KeyMatch> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<KeyMatch> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<KeyMatch> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<KeyMatch> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<KeyMatch> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<KeyMatch> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<KeyMatch> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<KeyMatch> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
