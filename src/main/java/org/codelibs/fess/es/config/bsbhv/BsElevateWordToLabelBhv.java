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
import org.codelibs.fess.es.config.bsentity.dbmeta.ElevateWordToLabelDbm;
import org.codelibs.fess.es.config.cbean.ElevateWordToLabelCB;
import org.codelibs.fess.es.config.exentity.ElevateWordToLabel;
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
public abstract class BsElevateWordToLabelBhv extends EsAbstractBehavior<ElevateWordToLabel, ElevateWordToLabelCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.elevate_word_to_label";
    }

    @Override
    public String asEsIndexType() {
        return "elevate_word_to_label";
    }

    @Override
    public String asEsSearchType() {
        return "elevate_word_to_label";
    }

    @Override
    public ElevateWordToLabelDbm asDBMeta() {
        return ElevateWordToLabelDbm.getInstance();
    }

    @Override
    protected <RESULT extends ElevateWordToLabel> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setElevateWordId(DfTypeUtil.toString(source.get("elevateWordId")));
            result.setLabelTypeId(DfTypeUtil.toString(source.get("labelTypeId")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends ElevateWordToLabel> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<ElevateWordToLabelCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<ElevateWordToLabel> selectEntity(CBCall<ElevateWordToLabelCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<ElevateWordToLabel> facadeSelectEntity(ElevateWordToLabelCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends ElevateWordToLabel> OptionalEntity<ENTITY> doSelectOptionalEntity(ElevateWordToLabelCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public ElevateWordToLabelCB newConditionBean() {
        return new ElevateWordToLabelCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public ElevateWordToLabel selectEntityWithDeletedCheck(CBCall<ElevateWordToLabelCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<ElevateWordToLabel> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<ElevateWordToLabel> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends ElevateWordToLabel> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected ElevateWordToLabelCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends ElevateWordToLabel> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends ElevateWordToLabel> typeOfSelectedEntity() {
        return ElevateWordToLabel.class;
    }

    @Override
    protected Class<ElevateWordToLabel> typeOfHandlingEntity() {
        return ElevateWordToLabel.class;
    }

    @Override
    protected Class<ElevateWordToLabelCB> typeOfHandlingConditionBean() {
        return ElevateWordToLabelCB.class;
    }

    public ListResultBean<ElevateWordToLabel> selectList(CBCall<ElevateWordToLabelCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<ElevateWordToLabel> selectPage(CBCall<ElevateWordToLabelCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<ElevateWordToLabel>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<ElevateWordToLabelCB> cbLambda, EntityRowHandler<ElevateWordToLabel> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<ElevateWordToLabelCB> cbLambda, EntityRowHandler<List<ElevateWordToLabel>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(ElevateWordToLabel entity) {
        doInsert(entity, null);
    }

    public void insert(ElevateWordToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(ElevateWordToLabel entity) {
        doUpdate(entity, null);
    }

    public void update(ElevateWordToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(ElevateWordToLabel entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(ElevateWordToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(ElevateWordToLabel entity) {
        doDelete(entity, null);
    }

    public void delete(ElevateWordToLabel entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<ElevateWordToLabelCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<ElevateWordToLabel> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<ElevateWordToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<ElevateWordToLabel> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<ElevateWordToLabel> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<ElevateWordToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<ElevateWordToLabel> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<ElevateWordToLabel> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<ElevateWordToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<ElevateWordToLabel> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
