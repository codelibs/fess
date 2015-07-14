package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.ClickLogDbm;
import org.codelibs.fess.es.cbean.ClickLogCB;
import org.codelibs.fess.es.exentity.ClickLog;
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
public abstract class BsClickLogBhv extends AbstractBehavior<ClickLog, ClickLogCB> {

    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "search_log";
    }

    @Override
    public String asEsIndexType() {
        return "click_log";
    }

    @Override
    public String asEsSearchType() {
        return "click_log";
    }

    @Override
    public ClickLogDbm asDBMeta() {
        return ClickLogDbm.getInstance();
    }

    @Override
    protected <RESULT extends ClickLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setId(toString(source.get("id")));
            result.setRequestedTime(toLong(source.get("requestedTime")));
            result.setSearchLogId(toString(source.get("searchLogId")));
            result.setUrl(toString(source.get("url")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<ClickLogCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<ClickLog> selectEntity(CBCall<ClickLogCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<ClickLog> facadeSelectEntity(ClickLogCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends ClickLog> OptionalEntity<ENTITY> doSelectOptionalEntity(ClickLogCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public ClickLogCB newConditionBean() {
        return new ClickLogCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public ClickLog selectEntityWithDeletedCheck(CBCall<ClickLogCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<ClickLog> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<ClickLog> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends ClickLog> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected ClickLogCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends ClickLog> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends ClickLog> typeOfSelectedEntity() {
        return ClickLog.class;
    }

    @Override
    protected Class<ClickLog> typeOfHandlingEntity() {
        return ClickLog.class;
    }

    @Override
    protected Class<ClickLogCB> typeOfHandlingConditionBean() {
        return ClickLogCB.class;
    }

    public ListResultBean<ClickLog> selectList(CBCall<ClickLogCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<ClickLog> selectPage(CBCall<ClickLogCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<ClickLog>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<ClickLogCB> cbLambda, EntityRowHandler<ClickLog> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<ClickLogCB> cbLambda, EntityRowHandler<List<ClickLog>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(ClickLog entity) {
        doInsert(entity, null);
    }

    public void insert(ClickLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(ClickLog entity) {
        doUpdate(entity, null);
    }

    public void update(ClickLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(ClickLog entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(ClickLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(ClickLog entity) {
        doDelete(entity, null);
    }

    public void delete(ClickLog entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<ClickLogCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<ClickLog> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<ClickLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<ClickLog> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<ClickLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<ClickLog> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<ClickLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
