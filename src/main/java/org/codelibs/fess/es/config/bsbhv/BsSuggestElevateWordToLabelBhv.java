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
import org.codelibs.fess.es.config.bsentity.dbmeta.SuggestElevateWordToLabelDbm;
import org.codelibs.fess.es.config.cbean.SuggestElevateWordToLabelCB;
import org.codelibs.fess.es.config.exentity.SuggestElevateWordToLabel;
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
public abstract class BsSuggestElevateWordToLabelBhv extends EsAbstractBehavior<SuggestElevateWordToLabel, SuggestElevateWordToLabelCB> {

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
        return "suggest_elevate_word_to_label";
    }

    @Override
    public String asEsSearchType() {
        return "suggest_elevate_word_to_label";
    }

    @Override
    public SuggestElevateWordToLabelDbm asDBMeta() {
        return SuggestElevateWordToLabelDbm.getInstance();
    }

    @Override
    protected <RESULT extends SuggestElevateWordToLabel> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setSuggestElevateWordId(DfTypeUtil.toString(source.get("suggestElevateWordId")));
            result.setLabelTypeId(DfTypeUtil.toString(source.get("labelTypeId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<SuggestElevateWordToLabelCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<SuggestElevateWordToLabel> selectEntity(CBCall<SuggestElevateWordToLabelCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<SuggestElevateWordToLabel> facadeSelectEntity(SuggestElevateWordToLabelCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends SuggestElevateWordToLabel> OptionalEntity<ENTITY> doSelectOptionalEntity(SuggestElevateWordToLabelCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public SuggestElevateWordToLabelCB newConditionBean() {
        return new SuggestElevateWordToLabelCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public SuggestElevateWordToLabel selectEntityWithDeletedCheck(CBCall<SuggestElevateWordToLabelCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<SuggestElevateWordToLabel> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<SuggestElevateWordToLabel> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends SuggestElevateWordToLabel> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected SuggestElevateWordToLabelCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends SuggestElevateWordToLabel> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends SuggestElevateWordToLabel> typeOfSelectedEntity() {
        return SuggestElevateWordToLabel.class;
    }

    @Override
    protected Class<SuggestElevateWordToLabel> typeOfHandlingEntity() {
        return SuggestElevateWordToLabel.class;
    }

    @Override
    protected Class<SuggestElevateWordToLabelCB> typeOfHandlingConditionBean() {
        return SuggestElevateWordToLabelCB.class;
    }

    public ListResultBean<SuggestElevateWordToLabel> selectList(CBCall<SuggestElevateWordToLabelCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<SuggestElevateWordToLabel> selectPage(CBCall<SuggestElevateWordToLabelCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<SuggestElevateWordToLabel>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<SuggestElevateWordToLabelCB> cbLambda, EntityRowHandler<SuggestElevateWordToLabel> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<SuggestElevateWordToLabelCB> cbLambda, EntityRowHandler<List<SuggestElevateWordToLabel>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda,typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(SuggestElevateWordToLabel entity) {
        doInsert(entity, null);
    }

    public void insert(SuggestElevateWordToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(SuggestElevateWordToLabel entity) {
        doUpdate(entity, null);
    }

    public void update(SuggestElevateWordToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(SuggestElevateWordToLabel entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(SuggestElevateWordToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(SuggestElevateWordToLabel entity) {
        doDelete(entity, null);
    }

    public void delete(SuggestElevateWordToLabel entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<SuggestElevateWordToLabelCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<SuggestElevateWordToLabel> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<SuggestElevateWordToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<SuggestElevateWordToLabel> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<SuggestElevateWordToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<SuggestElevateWordToLabel> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<SuggestElevateWordToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // #pending create, modify, remove
}

