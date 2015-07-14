package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.CrawlingSessionDbm;
import org.codelibs.fess.es.cbean.CrawlingSessionCB;
import org.codelibs.fess.es.exentity.CrawlingSession;
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
public abstract class BsCrawlingSessionBhv extends AbstractBehavior<CrawlingSession, CrawlingSessionCB> {

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
        return "crawling_session";
    }

    @Override
    public String asEsSearchType() {
        return "crawling_session";
    }

    @Override
    public CrawlingSessionDbm asDBMeta() {
        return CrawlingSessionDbm.getInstance();
    }

    @Override
    protected <RESULT extends CrawlingSession> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedTime(toLong(source.get("createdTime")));
            result.setExpiredTime(toLong(source.get("expiredTime")));
            result.setId(toString(source.get("id")));
            result.setName(toString(source.get("name")));
            result.setSessionId(toString(source.get("sessionId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<CrawlingSessionCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<CrawlingSession> selectEntity(CBCall<CrawlingSessionCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<CrawlingSession> facadeSelectEntity(CrawlingSessionCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends CrawlingSession> OptionalEntity<ENTITY> doSelectOptionalEntity(CrawlingSessionCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public CrawlingSessionCB newConditionBean() {
        return new CrawlingSessionCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public CrawlingSession selectEntityWithDeletedCheck(CBCall<CrawlingSessionCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<CrawlingSession> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<CrawlingSession> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends CrawlingSession> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected CrawlingSessionCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends CrawlingSession> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends CrawlingSession> typeOfSelectedEntity() {
        return CrawlingSession.class;
    }

    @Override
    protected Class<CrawlingSession> typeOfHandlingEntity() {
        return CrawlingSession.class;
    }

    @Override
    protected Class<CrawlingSessionCB> typeOfHandlingConditionBean() {
        return CrawlingSessionCB.class;
    }

    public ListResultBean<CrawlingSession> selectList(CBCall<CrawlingSessionCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<CrawlingSession> selectPage(CBCall<CrawlingSessionCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<CrawlingSession>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<CrawlingSessionCB> cbLambda, EntityRowHandler<CrawlingSession> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<CrawlingSessionCB> cbLambda, EntityRowHandler<List<CrawlingSession>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(CrawlingSession entity) {
        doInsert(entity, null);
    }

    public void insert(CrawlingSession entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(CrawlingSession entity) {
        doUpdate(entity, null);
    }

    public void update(CrawlingSession entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(CrawlingSession entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(CrawlingSession entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(CrawlingSession entity) {
        doDelete(entity, null);
    }

    public void delete(CrawlingSession entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<CrawlingSessionCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<CrawlingSession> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<CrawlingSession> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<CrawlingSession> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<CrawlingSession> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<CrawlingSession> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<CrawlingSession> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
