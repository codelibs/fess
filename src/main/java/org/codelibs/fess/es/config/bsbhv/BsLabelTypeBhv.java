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
import org.codelibs.fess.es.config.bsentity.dbmeta.LabelTypeDbm;
import org.codelibs.fess.es.config.cbean.LabelTypeCB;
import org.codelibs.fess.es.config.exentity.LabelType;
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
public abstract class BsLabelTypeBhv extends EsAbstractBehavior<LabelType, LabelTypeCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.label_type";
    }

    @Override
    public String asEsIndexType() {
        return "label_type";
    }

    @Override
    public String asEsSearchType() {
        return "label_type";
    }

    @Override
    public LabelTypeDbm asDBMeta() {
        return LabelTypeDbm.getInstance();
    }

    @Override
    protected <RESULT extends LabelType> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setExcludedPaths(DfTypeUtil.toString(source.get("excludedPaths")));
            result.setIncludedPaths(DfTypeUtil.toString(source.get("includedPaths")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setPermissions(toStringArray(source.get("permissions")));
            result.setSortOrder(DfTypeUtil.toInteger(source.get("sortOrder")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            result.setValue(DfTypeUtil.toString(source.get("value")));
            result.setVirtualHost(DfTypeUtil.toString(source.get("virtualHost")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends LabelType> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<LabelTypeCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<LabelType> selectEntity(CBCall<LabelTypeCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<LabelType> facadeSelectEntity(LabelTypeCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends LabelType> OptionalEntity<ENTITY> doSelectOptionalEntity(LabelTypeCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public LabelTypeCB newConditionBean() {
        return new LabelTypeCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public LabelType selectEntityWithDeletedCheck(CBCall<LabelTypeCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<LabelType> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<LabelType> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends LabelType> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected LabelTypeCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends LabelType> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends LabelType> typeOfSelectedEntity() {
        return LabelType.class;
    }

    @Override
    protected Class<LabelType> typeOfHandlingEntity() {
        return LabelType.class;
    }

    @Override
    protected Class<LabelTypeCB> typeOfHandlingConditionBean() {
        return LabelTypeCB.class;
    }

    public ListResultBean<LabelType> selectList(CBCall<LabelTypeCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<LabelType> selectPage(CBCall<LabelTypeCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<LabelType>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<LabelTypeCB> cbLambda, EntityRowHandler<LabelType> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<LabelTypeCB> cbLambda, EntityRowHandler<List<LabelType>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(LabelType entity) {
        doInsert(entity, null);
    }

    public void insert(LabelType entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(LabelType entity) {
        doUpdate(entity, null);
    }

    public void update(LabelType entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(LabelType entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(LabelType entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(LabelType entity) {
        doDelete(entity, null);
    }

    public void delete(LabelType entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<LabelTypeCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<LabelType> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<LabelType> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<LabelType> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<LabelType> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<LabelType> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<LabelType> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<LabelType> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<LabelType> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<LabelType> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
