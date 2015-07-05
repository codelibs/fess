package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.SearchLogDbm;
import org.codelibs.fess.es.cbean.SearchLogCB;
import org.codelibs.fess.es.exentity.SearchLog;
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
public abstract class BsSearchLogBhv extends AbstractBehavior<SearchLog, SearchLogCB> {

    @Override
    public String asTableDbName() {
        return "search_log";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public SearchLogDbm asDBMeta() {
        return SearchLogDbm.getInstance();
    }

    @Override
    protected <RESULT extends SearchLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setAccessType(toString(source.get("accessType")));
            result.setClientIp(toString(source.get("clientIp")));
            result.setHitCount(toLong(source.get("hitCount")));
            result.setId(toString(source.get("id")));
            result.setQueryOffset(toInteger(source.get("queryOffset")));
            result.setQueryPageSize(toInteger(source.get("queryPageSize")));
            result.setReferer(toString(source.get("referer")));
            result.setRequestedTime(toLong(source.get("requestedTime")));
            result.setResponseTime(toInteger(source.get("responseTime")));
            result.setSearchWord(toString(source.get("searchWord")));
            result.setUserAgent(toString(source.get("userAgent")));
            result.setUserId(toLong(source.get("userId")));
            result.setUserSessionId(toString(source.get("userSessionId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<SearchLogCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<SearchLog> selectEntity(CBCall<SearchLogCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<SearchLog> facadeSelectEntity(SearchLogCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchLog> OptionalEntity<ENTITY> doSelectOptionalEntity(SearchLogCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public SearchLogCB newConditionBean() {
        return new SearchLogCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public SearchLog selectEntityWithDeletedCheck(CBCall<SearchLogCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<SearchLog> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<SearchLog> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchLog> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected SearchLogCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends SearchLog> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends SearchLog> typeOfSelectedEntity() {
        return SearchLog.class;
    }

    @Override
    protected Class<SearchLog> typeOfHandlingEntity() {
        return SearchLog.class;
    }

    @Override
    protected Class<SearchLogCB> typeOfHandlingConditionBean() {
        return SearchLogCB.class;
    }

    public ListResultBean<SearchLog> selectList(CBCall<SearchLogCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<SearchLog> selectPage(CBCall<SearchLogCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<SearchLog>) facadeSelectList(createCB(cbLambda));
    }

    public void insert(SearchLog entity) {
        doInsert(entity, null);
    }

    public void insert(SearchLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(SearchLog entity) {
        doUpdate(entity, null);
    }

    public void update(SearchLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(SearchLog entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(SearchLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(SearchLog entity) {
        doDelete(entity, null);
    }

    public void delete(SearchLog entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    // TODO create, modify, remove
}
