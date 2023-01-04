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
import org.codelibs.fess.es.config.bsentity.dbmeta.ScheduledJobDbm;
import org.codelibs.fess.es.config.cbean.ScheduledJobCB;
import org.codelibs.fess.es.config.exentity.ScheduledJob;
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
public abstract class BsScheduledJobBhv extends EsAbstractBehavior<ScheduledJob, ScheduledJobCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.scheduled_job";
    }

    @Override
    public String asEsIndexType() {
        return "scheduled_job";
    }

    @Override
    public String asEsSearchType() {
        return "scheduled_job";
    }

    @Override
    public ScheduledJobDbm asDBMeta() {
        return ScheduledJobDbm.getInstance();
    }

    @Override
    protected <RESULT extends ScheduledJob> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setAvailable(DfTypeUtil.toBoolean(source.get("available")));
            result.setCrawler(DfTypeUtil.toBoolean(source.get("crawler")));
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setCronExpression(DfTypeUtil.toString(source.get("cronExpression")));
            result.setJobLogging(DfTypeUtil.toBoolean(source.get("jobLogging")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setScriptData(DfTypeUtil.toString(source.get("scriptData")));
            result.setScriptType(DfTypeUtil.toString(source.get("scriptType")));
            result.setSortOrder(DfTypeUtil.toInteger(source.get("sortOrder")));
            result.setTarget(DfTypeUtil.toString(source.get("target")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends ScheduledJob> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<ScheduledJobCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<ScheduledJob> selectEntity(CBCall<ScheduledJobCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<ScheduledJob> facadeSelectEntity(ScheduledJobCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends ScheduledJob> OptionalEntity<ENTITY> doSelectOptionalEntity(ScheduledJobCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public ScheduledJobCB newConditionBean() {
        return new ScheduledJobCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public ScheduledJob selectEntityWithDeletedCheck(CBCall<ScheduledJobCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<ScheduledJob> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<ScheduledJob> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends ScheduledJob> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected ScheduledJobCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends ScheduledJob> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends ScheduledJob> typeOfSelectedEntity() {
        return ScheduledJob.class;
    }

    @Override
    protected Class<ScheduledJob> typeOfHandlingEntity() {
        return ScheduledJob.class;
    }

    @Override
    protected Class<ScheduledJobCB> typeOfHandlingConditionBean() {
        return ScheduledJobCB.class;
    }

    public ListResultBean<ScheduledJob> selectList(CBCall<ScheduledJobCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<ScheduledJob> selectPage(CBCall<ScheduledJobCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<ScheduledJob>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<ScheduledJobCB> cbLambda, EntityRowHandler<ScheduledJob> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<ScheduledJobCB> cbLambda, EntityRowHandler<List<ScheduledJob>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(ScheduledJob entity) {
        doInsert(entity, null);
    }

    public void insert(ScheduledJob entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(ScheduledJob entity) {
        doUpdate(entity, null);
    }

    public void update(ScheduledJob entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(ScheduledJob entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(ScheduledJob entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(ScheduledJob entity) {
        doDelete(entity, null);
    }

    public void delete(ScheduledJob entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<ScheduledJobCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<ScheduledJob> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<ScheduledJob> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<ScheduledJob> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<ScheduledJob> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<ScheduledJob> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<ScheduledJob> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<ScheduledJob> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<ScheduledJob> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<ScheduledJob> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
