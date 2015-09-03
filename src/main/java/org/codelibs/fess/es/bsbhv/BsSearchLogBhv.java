package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.SearchLogDbm;
import org.codelibs.fess.es.cbean.SearchLogCB;
import org.codelibs.fess.es.exentity.SearchLog;
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
public abstract class BsSearchLogBhv extends AbstractBehavior<SearchLog, SearchLogCB> {

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
        return "search_log";
    }

    @Override
    public String asEsSearchType() {
        return "search_log";
    }

    @Override
    public SearchLogDbm asDBMeta() {
        return SearchLogDbm.getInstance();
    }

    @Override
    protected <RESULT extends SearchLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setAccessType(DfTypeUtil.toString(source.get("accessType")));
            result.setClientIp(DfTypeUtil.toString(source.get("clientIp")));
            result.setHitCount(DfTypeUtil.toLong(source.get("hitCount")));
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setQueryOffset(DfTypeUtil.toInteger(source.get("queryOffset")));
            result.setQueryPageSize(DfTypeUtil.toInteger(source.get("queryPageSize")));
            result.setReferer(DfTypeUtil.toString(source.get("referer")));
            result.setRequestedTime(DfTypeUtil.toLong(source.get("requestedTime")));
            result.setResponseTime(DfTypeUtil.toInteger(source.get("responseTime")));
            result.setSearchWord(DfTypeUtil.toString(source.get("searchWord")));
            result.setUserAgent(DfTypeUtil.toString(source.get("userAgent")));
            result.setUserInfoId(DfTypeUtil.toString(source.get("userInfoId")));
            result.setUserSessionId(DfTypeUtil.toString(source.get("userSessionId")));
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

    public void selectCursor(CBCall<SearchLogCB> cbLambda, EntityRowHandler<SearchLog> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<SearchLogCB> cbLambda, EntityRowHandler<List<SearchLog>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
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

    public int queryDelete(CBCall<SearchLogCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<SearchLog> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<SearchLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<SearchLog> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<SearchLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<SearchLog> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<SearchLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove

    @Override
    protected boolean isCompatibleBatchInsertDefaultEveryColumn() {
        return true;
    }
}
