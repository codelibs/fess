package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.JobLogDbm;
import org.codelibs.fess.es.cbean.JobLogCB;
import org.codelibs.fess.es.exentity.JobLog;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;

/**
 * @author FreeGen
 */
public abstract class BsJobLogBhv extends AbstractBehavior<JobLog, JobLogCB> {

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
            result.setEndTime(toLong(source.get("endTime")));
            result.setId(toString(source.get("id")));
            result.setJobName(toString(source.get("jobName")));
            result.setJobStatus(toString(source.get("jobStatus")));
            result.setScriptData(toString(source.get("scriptData")));
            result.setScriptResult(toString(source.get("scriptResult")));
            result.setScriptType(toString(source.get("scriptType")));
            result.setStartTime(toLong(source.get("startTime")));
            result.setTarget(toString(source.get("target")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

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
        // TODO same?
        return (PagingResultBean<JobLog>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<JobLogCB> cbLambda, EntityRowHandler<JobLog> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<JobLogCB> cbLambda, EntityRowHandler<List<JobLog>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(JobLog entity) {
        doInsert(entity, null);
    }

    public void insert(JobLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(JobLog entity) {
        doUpdate(entity, null);
    }

    public void update(JobLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(JobLog entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(JobLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(JobLog entity) {
        doDelete(entity, null);
    }

    public void delete(JobLog entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<JobLogCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<JobLog> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<JobLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<JobLog> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<JobLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<JobLog> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<JobLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
