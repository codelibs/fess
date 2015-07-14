package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.FavoriteLogDbm;
import org.codelibs.fess.es.cbean.FavoriteLogCB;
import org.codelibs.fess.es.exentity.FavoriteLog;
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
public abstract class BsFavoriteLogBhv extends AbstractBehavior<FavoriteLog, FavoriteLogCB> {

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
        return "favorite_log";
    }

    @Override
    public String asEsSearchType() {
        return "favorite_log";
    }

    @Override
    public FavoriteLogDbm asDBMeta() {
        return FavoriteLogDbm.getInstance();
    }

    @Override
    protected <RESULT extends FavoriteLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedTime(toLong(source.get("createdTime")));
            result.setId(toString(source.get("id")));
            result.setUrl(toString(source.get("url")));
            result.setUserInfoId(toString(source.get("userInfoId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<FavoriteLogCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<FavoriteLog> selectEntity(CBCall<FavoriteLogCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<FavoriteLog> facadeSelectEntity(FavoriteLogCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FavoriteLog> OptionalEntity<ENTITY> doSelectOptionalEntity(FavoriteLogCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public FavoriteLogCB newConditionBean() {
        return new FavoriteLogCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public FavoriteLog selectEntityWithDeletedCheck(CBCall<FavoriteLogCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<FavoriteLog> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<FavoriteLog> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends FavoriteLog> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected FavoriteLogCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends FavoriteLog> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends FavoriteLog> typeOfSelectedEntity() {
        return FavoriteLog.class;
    }

    @Override
    protected Class<FavoriteLog> typeOfHandlingEntity() {
        return FavoriteLog.class;
    }

    @Override
    protected Class<FavoriteLogCB> typeOfHandlingConditionBean() {
        return FavoriteLogCB.class;
    }

    public ListResultBean<FavoriteLog> selectList(CBCall<FavoriteLogCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<FavoriteLog> selectPage(CBCall<FavoriteLogCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<FavoriteLog>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<FavoriteLogCB> cbLambda, EntityRowHandler<FavoriteLog> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<FavoriteLogCB> cbLambda, EntityRowHandler<List<FavoriteLog>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(FavoriteLog entity) {
        doInsert(entity, null);
    }

    public void insert(FavoriteLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(FavoriteLog entity) {
        doUpdate(entity, null);
    }

    public void update(FavoriteLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(FavoriteLog entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(FavoriteLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(FavoriteLog entity) {
        doDelete(entity, null);
    }

    public void delete(FavoriteLog entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<FavoriteLogCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<FavoriteLog> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<FavoriteLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<FavoriteLog> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<FavoriteLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<FavoriteLog> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<FavoriteLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
