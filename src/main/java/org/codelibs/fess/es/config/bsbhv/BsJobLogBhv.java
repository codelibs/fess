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
import org.codelibs.fess.es.config.bsentity.dbmeta.JobLogDbm;
import org.codelibs.fess.es.config.cbean.JobLogCB;
import org.codelibs.fess.es.config.exentity.JobLog;
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
public abstract class BsJobLogBhv extends EsAbstractBehavior<JobLog, JobLogCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.job_log";
    }

    @Override
    public String asEsIndexType() {
        return "job_log";
    }

    @Override
    public String asEsSearchType() {
        return "job_log";
    }

    @Override
    public JobLogDbm asDBMeta() {
        return JobLogDbm.getInstance();
    }

    @Override
    protected <RESULT extends JobLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setEndTime(DfTypeUtil.toLong(source.get("endTime")));
            result.setJobName(DfTypeUtil.toString(source.get("jobName")));
            result.setJobStatus(DfTypeUtil.toString(source.get("jobStatus")));
            result.setLastUpdated(DfTypeUtil.toLong(source.get("lastUpdated")));
            result.setScriptData(DfTypeUtil.toString(source.get("scriptData")));
            result.setScriptResult(DfTypeUtil.toString(source.get("scriptResult")));
            result.setScriptType(DfTypeUtil.toString(source.get("scriptType")));
            result.setStartTime(DfTypeUtil.toLong(source.get("startTime")));
            result.setTarget(DfTypeUtil.toString(source.get("target")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends JobLog> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<JobLogCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<JobLog> selectEntity(CBCall<JobLogCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<JobLog> facadeSelectEntity(JobLogCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends JobLog> OptionalEntity<ENTITY> doSelectOptionalEntity(JobLogCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public JobLogCB newConditionBean() {
        return new JobLogCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public JobLog selectEntityWithDeletedCheck(CBCall<JobLogCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<JobLog> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<JobLog> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends JobLog> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected JobLogCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends JobLog> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends JobLog> typeOfSelectedEntity() {
        return JobLog.class;
    }

    @Override
    protected Class<JobLog> typeOfHandlingEntity() {
        return JobLog.class;
    }

    @Override
    protected Class<JobLogCB> typeOfHandlingConditionBean() {
        return JobLogCB.class;
    }

    public ListResultBean<JobLog> selectList(CBCall<JobLogCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<JobLog> selectPage(CBCall<JobLogCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<JobLog>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<JobLogCB> cbLambda, EntityRowHandler<JobLog> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<JobLogCB> cbLambda, EntityRowHandler<List<JobLog>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(JobLog entity) {
        doInsert(entity, null);
    }

    public void insert(JobLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(JobLog entity) {
        doUpdate(entity, null);
    }

    public void update(JobLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(JobLog entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(JobLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(JobLog entity) {
        doDelete(entity, null);
    }

    public void delete(JobLog entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<JobLogCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<JobLog> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<JobLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<JobLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<JobLog> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<JobLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<JobLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<JobLog> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<JobLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<JobLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
