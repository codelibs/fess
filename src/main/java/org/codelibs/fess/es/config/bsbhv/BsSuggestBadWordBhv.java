/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
import org.codelibs.fess.es.config.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.config.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.config.bsentity.dbmeta.SuggestBadWordDbm;
import org.codelibs.fess.es.config.cbean.SuggestBadWordCB;
import org.codelibs.fess.es.config.exentity.SuggestBadWord;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.util.DfTypeUtil;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsSuggestBadWordBhv extends EsAbstractBehavior<SuggestBadWord, SuggestBadWordCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return ".fess_config";
    }

    @Override
    public String asEsIndexType() {
        return "suggest_bad_word";
    }

    @Override
    public String asEsSearchType() {
        return "suggest_bad_word";
    }

    @Override
    public SuggestBadWordDbm asDBMeta() {
        return SuggestBadWordDbm.getInstance();
    }

    @Override
    protected <RESULT extends SuggestBadWord> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setSuggestWord(DfTypeUtil.toString(source.get("suggestWord")));
            result.setTargetLabel(DfTypeUtil.toString(source.get("targetLabel")));
            result.setTargetRole(DfTypeUtil.toString(source.get("targetRole")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<SuggestBadWordCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<SuggestBadWord> selectEntity(CBCall<SuggestBadWordCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<SuggestBadWord> facadeSelectEntity(SuggestBadWordCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends SuggestBadWord> OptionalEntity<ENTITY> doSelectOptionalEntity(SuggestBadWordCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public SuggestBadWordCB newConditionBean() {
        return new SuggestBadWordCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public SuggestBadWord selectEntityWithDeletedCheck(CBCall<SuggestBadWordCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<SuggestBadWord> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<SuggestBadWord> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends SuggestBadWord> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected SuggestBadWordCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends SuggestBadWord> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends SuggestBadWord> typeOfSelectedEntity() {
        return SuggestBadWord.class;
    }

    @Override
    protected Class<SuggestBadWord> typeOfHandlingEntity() {
        return SuggestBadWord.class;
    }

    @Override
    protected Class<SuggestBadWordCB> typeOfHandlingConditionBean() {
        return SuggestBadWordCB.class;
    }

    public ListResultBean<SuggestBadWord> selectList(CBCall<SuggestBadWordCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<SuggestBadWord> selectPage(CBCall<SuggestBadWordCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<SuggestBadWord>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<SuggestBadWordCB> cbLambda, EntityRowHandler<SuggestBadWord> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<SuggestBadWordCB> cbLambda, EntityRowHandler<List<SuggestBadWord>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(SuggestBadWord entity) {
        doInsert(entity, null);
    }

    public void insert(SuggestBadWord entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(SuggestBadWord entity) {
        doUpdate(entity, null);
    }

    public void update(SuggestBadWord entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(SuggestBadWord entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(SuggestBadWord entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(SuggestBadWord entity) {
        doDelete(entity, null);
    }

    public void delete(SuggestBadWord entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<SuggestBadWordCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<SuggestBadWord> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<SuggestBadWord> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<SuggestBadWord> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<SuggestBadWord> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<SuggestBadWord> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<SuggestBadWord> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // #pending create, modify, remove
}
