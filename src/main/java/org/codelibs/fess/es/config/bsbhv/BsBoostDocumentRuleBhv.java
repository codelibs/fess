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
import org.codelibs.fess.es.config.bsentity.dbmeta.BoostDocumentRuleDbm;
import org.codelibs.fess.es.config.cbean.BoostDocumentRuleCB;
import org.codelibs.fess.es.config.exentity.BoostDocumentRule;
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
public abstract class BsBoostDocumentRuleBhv extends EsAbstractBehavior<BoostDocumentRule, BoostDocumentRuleCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.boost_document_rule";
    }

    @Override
    public String asEsIndexType() {
        return "boost_document_rule";
    }

    @Override
    public String asEsSearchType() {
        return "boost_document_rule";
    }

    @Override
    public BoostDocumentRuleDbm asDBMeta() {
        return BoostDocumentRuleDbm.getInstance();
    }

    @Override
    protected <RESULT extends BoostDocumentRule> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setBoostExpr(DfTypeUtil.toString(source.get("boostExpr")));
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setSortOrder(DfTypeUtil.toInteger(source.get("sortOrder")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            result.setUrlExpr(DfTypeUtil.toString(source.get("urlExpr")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends BoostDocumentRule> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<BoostDocumentRuleCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<BoostDocumentRule> selectEntity(CBCall<BoostDocumentRuleCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<BoostDocumentRule> facadeSelectEntity(BoostDocumentRuleCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends BoostDocumentRule> OptionalEntity<ENTITY> doSelectOptionalEntity(BoostDocumentRuleCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public BoostDocumentRuleCB newConditionBean() {
        return new BoostDocumentRuleCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public BoostDocumentRule selectEntityWithDeletedCheck(CBCall<BoostDocumentRuleCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<BoostDocumentRule> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<BoostDocumentRule> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends BoostDocumentRule> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected BoostDocumentRuleCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends BoostDocumentRule> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends BoostDocumentRule> typeOfSelectedEntity() {
        return BoostDocumentRule.class;
    }

    @Override
    protected Class<BoostDocumentRule> typeOfHandlingEntity() {
        return BoostDocumentRule.class;
    }

    @Override
    protected Class<BoostDocumentRuleCB> typeOfHandlingConditionBean() {
        return BoostDocumentRuleCB.class;
    }

    public ListResultBean<BoostDocumentRule> selectList(CBCall<BoostDocumentRuleCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<BoostDocumentRule> selectPage(CBCall<BoostDocumentRuleCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<BoostDocumentRule>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<BoostDocumentRuleCB> cbLambda, EntityRowHandler<BoostDocumentRule> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<BoostDocumentRuleCB> cbLambda, EntityRowHandler<List<BoostDocumentRule>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(BoostDocumentRule entity) {
        doInsert(entity, null);
    }

    public void insert(BoostDocumentRule entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(BoostDocumentRule entity) {
        doUpdate(entity, null);
    }

    public void update(BoostDocumentRule entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(BoostDocumentRule entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(BoostDocumentRule entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(BoostDocumentRule entity) {
        doDelete(entity, null);
    }

    public void delete(BoostDocumentRule entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<BoostDocumentRuleCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<BoostDocumentRule> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<BoostDocumentRule> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<BoostDocumentRule> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<BoostDocumentRule> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<BoostDocumentRule> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<BoostDocumentRule> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<BoostDocumentRule> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<BoostDocumentRule> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<BoostDocumentRule> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
