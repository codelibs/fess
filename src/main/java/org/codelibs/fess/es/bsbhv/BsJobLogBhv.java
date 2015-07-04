package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.JobLogDbm;
import org.codelibs.fess.es.cbean.JobLogCB;
import org.codelibs.fess.es.exentity.JobLog;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;

/**
 * @author FreeGen
 */
public abstract class BsJobLogBhv extends AbstractBehavior<JobLog, JobLogCB> {

    @Override
    public String asTableDbName() {
        return "job_log";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public JobLogDbm asDBMeta() {
        return JobLogDbm.getInstance();
    }

    @Override
    protected <RESULT extends JobLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setEndTime((Long) source.get("endTime"));
            result.setId((String) source.get("id"));
            result.setJobName((String) source.get("jobName"));
            result.setJobStatus((String) source.get("jobStatus"));
            result.setScriptData((String) source.get("scriptData"));
            result.setScriptResult((String) source.get("scriptResult"));
            result.setScriptType((String) source.get("scriptType"));
            result.setStartTime((Long) source.get("startTime"));
            result.setTarget((String) source.get("target"));
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

    // TODO create, modify, remove
}
