package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.EventLogDbm;
import org.codelibs.fess.es.cbean.EventLogCB;
import org.codelibs.fess.es.exentity.EventLog;
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
 * @author FreeGen
 */
public abstract class BsEventLogBhv extends AbstractBehavior<EventLog, EventLogCB> {

    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_log";
    }

    @Override
    public String asEsIndexType() {
        return "event_log";
    }

    @Override
    public String asEsSearchType() {
        return "event_log";
    }

    @Override
    public EventLogDbm asDBMeta() {
        return EventLogDbm.getInstance();
    }

    @Override
    protected <RESULT extends EventLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedAt(DfTypeUtil.toLocalDateTime(source.get("createdAt")));
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setEventType(DfTypeUtil.toString(source.get("eventType")));
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setMessage(DfTypeUtil.toString(source.get("message")));
            result.setPath(DfTypeUtil.toString(source.get("path")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<EventLogCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<EventLog> selectEntity(CBCall<EventLogCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<EventLog> facadeSelectEntity(EventLogCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends EventLog> OptionalEntity<ENTITY> doSelectOptionalEntity(EventLogCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public EventLogCB newConditionBean() {
        return new EventLogCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public EventLog selectEntityWithDeletedCheck(CBCall<EventLogCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<EventLog> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<EventLog> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends EventLog> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected EventLogCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends EventLog> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends EventLog> typeOfSelectedEntity() {
        return EventLog.class;
    }

    @Override
    protected Class<EventLog> typeOfHandlingEntity() {
        return EventLog.class;
    }

    @Override
    protected Class<EventLogCB> typeOfHandlingConditionBean() {
        return EventLogCB.class;
    }

    public ListResultBean<EventLog> selectList(CBCall<EventLogCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<EventLog> selectPage(CBCall<EventLogCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<EventLog>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<EventLogCB> cbLambda, EntityRowHandler<EventLog> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<EventLogCB> cbLambda, EntityRowHandler<List<EventLog>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(EventLog entity) {
        doInsert(entity, null);
    }

    public void insert(EventLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(EventLog entity) {
        doUpdate(entity, null);
    }

    public void update(EventLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(EventLog entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(EventLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(EventLog entity) {
        doDelete(entity, null);
    }

    public void delete(EventLog entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<EventLogCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<EventLog> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<EventLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<EventLog> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<EventLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<EventLog> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<EventLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
