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
import org.codelibs.fess.es.config.bsentity.dbmeta.DataConfigDbm;
import org.codelibs.fess.es.config.cbean.DataConfigCB;
import org.codelibs.fess.es.config.exentity.DataConfig;
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
public abstract class BsDataConfigBhv extends EsAbstractBehavior<DataConfig, DataConfigCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.data_config";
    }

    @Override
    public String asEsIndexType() {
        return "data_config";
    }

    @Override
    public String asEsSearchType() {
        return "data_config";
    }

    @Override
    public DataConfigDbm asDBMeta() {
        return DataConfigDbm.getInstance();
    }

    @Override
    protected <RESULT extends DataConfig> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setAvailable(DfTypeUtil.toBoolean(source.get("available")));
            result.setBoost(DfTypeUtil.toFloat(source.get("boost")));
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setDescription(DfTypeUtil.toString(source.get("description")));
            result.setHandlerName(DfTypeUtil.toString(source.get("handlerName")));
            result.setHandlerParameter(DfTypeUtil.toString(source.get("handlerParameter")));
            result.setHandlerScript(DfTypeUtil.toString(source.get("handlerScript")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setPermissions(toStringArray(source.get("permissions")));
            result.setSortOrder(DfTypeUtil.toInteger(source.get("sortOrder")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            result.setVirtualHosts(toStringArray(source.get("virtualHosts")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends DataConfig> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<DataConfigCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<DataConfig> selectEntity(CBCall<DataConfigCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<DataConfig> facadeSelectEntity(DataConfigCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends DataConfig> OptionalEntity<ENTITY> doSelectOptionalEntity(DataConfigCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public DataConfigCB newConditionBean() {
        return new DataConfigCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public DataConfig selectEntityWithDeletedCheck(CBCall<DataConfigCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<DataConfig> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<DataConfig> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends DataConfig> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected DataConfigCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends DataConfig> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends DataConfig> typeOfSelectedEntity() {
        return DataConfig.class;
    }

    @Override
    protected Class<DataConfig> typeOfHandlingEntity() {
        return DataConfig.class;
    }

    @Override
    protected Class<DataConfigCB> typeOfHandlingConditionBean() {
        return DataConfigCB.class;
    }

    public ListResultBean<DataConfig> selectList(CBCall<DataConfigCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<DataConfig> selectPage(CBCall<DataConfigCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<DataConfig>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<DataConfigCB> cbLambda, EntityRowHandler<DataConfig> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<DataConfigCB> cbLambda, EntityRowHandler<List<DataConfig>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(DataConfig entity) {
        doInsert(entity, null);
    }

    public void insert(DataConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(DataConfig entity) {
        doUpdate(entity, null);
    }

    public void update(DataConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(DataConfig entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(DataConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(DataConfig entity) {
        doDelete(entity, null);
    }

    public void delete(DataConfig entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<DataConfigCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<DataConfig> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<DataConfig> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<DataConfig> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<DataConfig> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<DataConfig> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<DataConfig> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<DataConfig> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<DataConfig> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<DataConfig> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
