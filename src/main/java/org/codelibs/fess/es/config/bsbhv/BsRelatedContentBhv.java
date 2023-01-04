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
import org.codelibs.fess.es.config.bsentity.dbmeta.RelatedContentDbm;
import org.codelibs.fess.es.config.cbean.RelatedContentCB;
import org.codelibs.fess.es.config.exentity.RelatedContent;
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
public abstract class BsRelatedContentBhv extends EsAbstractBehavior<RelatedContent, RelatedContentCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.related_content";
    }

    @Override
    public String asEsIndexType() {
        return "related_content";
    }

    @Override
    public String asEsSearchType() {
        return "related_content";
    }

    @Override
    public RelatedContentDbm asDBMeta() {
        return RelatedContentDbm.getInstance();
    }

    @Override
    protected <RESULT extends RelatedContent> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setContent(DfTypeUtil.toString(source.get("content")));
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setSortOrder(DfTypeUtil.toInteger(source.get("sortOrder")));
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

    protected <RESULT extends RelatedContent> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<RelatedContentCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<RelatedContent> selectEntity(CBCall<RelatedContentCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<RelatedContent> facadeSelectEntity(RelatedContentCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends RelatedContent> OptionalEntity<ENTITY> doSelectOptionalEntity(RelatedContentCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public RelatedContentCB newConditionBean() {
        return new RelatedContentCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public RelatedContent selectEntityWithDeletedCheck(CBCall<RelatedContentCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<RelatedContent> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<RelatedContent> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends RelatedContent> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected RelatedContentCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends RelatedContent> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends RelatedContent> typeOfSelectedEntity() {
        return RelatedContent.class;
    }

    @Override
    protected Class<RelatedContent> typeOfHandlingEntity() {
        return RelatedContent.class;
    }

    @Override
    protected Class<RelatedContentCB> typeOfHandlingConditionBean() {
        return RelatedContentCB.class;
    }

    public ListResultBean<RelatedContent> selectList(CBCall<RelatedContentCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<RelatedContent> selectPage(CBCall<RelatedContentCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<RelatedContent>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<RelatedContentCB> cbLambda, EntityRowHandler<RelatedContent> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<RelatedContentCB> cbLambda, EntityRowHandler<List<RelatedContent>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(RelatedContent entity) {
        doInsert(entity, null);
    }

    public void insert(RelatedContent entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(RelatedContent entity) {
        doUpdate(entity, null);
    }

    public void update(RelatedContent entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(RelatedContent entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(RelatedContent entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(RelatedContent entity) {
        doDelete(entity, null);
    }

    public void delete(RelatedContent entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<RelatedContentCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<RelatedContent> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<RelatedContent> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<RelatedContent> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<RelatedContent> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<RelatedContent> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<RelatedContent> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<RelatedContent> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<RelatedContent> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<RelatedContent> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
