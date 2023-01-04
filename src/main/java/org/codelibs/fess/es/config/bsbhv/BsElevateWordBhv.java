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
import org.codelibs.fess.es.config.bsentity.dbmeta.ElevateWordDbm;
import org.codelibs.fess.es.config.cbean.ElevateWordCB;
import org.codelibs.fess.es.config.exentity.ElevateWord;
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
public abstract class BsElevateWordBhv extends EsAbstractBehavior<ElevateWord, ElevateWordCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.elevate_word";
    }

    @Override
    public String asEsIndexType() {
        return "elevate_word";
    }

    @Override
    public String asEsSearchType() {
        return "elevate_word";
    }

    @Override
    public ElevateWordDbm asDBMeta() {
        return ElevateWordDbm.getInstance();
    }

    @Override
    protected <RESULT extends ElevateWord> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setBoost(DfTypeUtil.toFloat(source.get("boost")));
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setPermissions(toStringArray(source.get("permissions")));
            result.setReading(DfTypeUtil.toString(source.get("reading")));
            result.setSuggestWord(DfTypeUtil.toString(source.get("suggestWord")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends ElevateWord> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<ElevateWordCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<ElevateWord> selectEntity(CBCall<ElevateWordCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<ElevateWord> facadeSelectEntity(ElevateWordCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends ElevateWord> OptionalEntity<ENTITY> doSelectOptionalEntity(ElevateWordCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public ElevateWordCB newConditionBean() {
        return new ElevateWordCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public ElevateWord selectEntityWithDeletedCheck(CBCall<ElevateWordCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<ElevateWord> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<ElevateWord> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends ElevateWord> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected ElevateWordCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends ElevateWord> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends ElevateWord> typeOfSelectedEntity() {
        return ElevateWord.class;
    }

    @Override
    protected Class<ElevateWord> typeOfHandlingEntity() {
        return ElevateWord.class;
    }

    @Override
    protected Class<ElevateWordCB> typeOfHandlingConditionBean() {
        return ElevateWordCB.class;
    }

    public ListResultBean<ElevateWord> selectList(CBCall<ElevateWordCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<ElevateWord> selectPage(CBCall<ElevateWordCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<ElevateWord>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<ElevateWordCB> cbLambda, EntityRowHandler<ElevateWord> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<ElevateWordCB> cbLambda, EntityRowHandler<List<ElevateWord>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(ElevateWord entity) {
        doInsert(entity, null);
    }

    public void insert(ElevateWord entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(ElevateWord entity) {
        doUpdate(entity, null);
    }

    public void update(ElevateWord entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(ElevateWord entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(ElevateWord entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(ElevateWord entity) {
        doDelete(entity, null);
    }

    public void delete(ElevateWord entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<ElevateWordCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<ElevateWord> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<ElevateWord> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<ElevateWord> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<ElevateWord> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<ElevateWord> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<ElevateWord> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<ElevateWord> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<ElevateWord> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<ElevateWord> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
