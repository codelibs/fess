package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.SearchFieldLogDbm;
import org.codelibs.fess.es.cbean.SearchFieldLogCB;
import org.codelibs.fess.es.exentity.SearchFieldLog;
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
public abstract class BsSearchFieldLogBhv extends AbstractBehavior<SearchFieldLog, SearchFieldLogCB> {

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
        return "search_field_log";
    }

    @Override
    public String asEsSearchType() {
        return "search_field_log";
    }

    @Override
    public SearchFieldLogDbm asDBMeta() {
        return SearchFieldLogDbm.getInstance();
    }

    @Override
    protected <RESULT extends SearchFieldLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setSearchLogId(DfTypeUtil.toString(source.get("searchLogId")));
            result.setValue(DfTypeUtil.toString(source.get("value")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<SearchFieldLogCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<SearchFieldLog> selectEntity(CBCall<SearchFieldLogCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<SearchFieldLog> facadeSelectEntity(SearchFieldLogCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchFieldLog> OptionalEntity<ENTITY> doSelectOptionalEntity(SearchFieldLogCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public SearchFieldLogCB newConditionBean() {
        return new SearchFieldLogCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public SearchFieldLog selectEntityWithDeletedCheck(CBCall<SearchFieldLogCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<SearchFieldLog> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<SearchFieldLog> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchFieldLog> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected SearchFieldLogCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends SearchFieldLog> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends SearchFieldLog> typeOfSelectedEntity() {
        return SearchFieldLog.class;
    }

    @Override
    protected Class<SearchFieldLog> typeOfHandlingEntity() {
        return SearchFieldLog.class;
    }

    @Override
    protected Class<SearchFieldLogCB> typeOfHandlingConditionBean() {
        return SearchFieldLogCB.class;
    }

    public ListResultBean<SearchFieldLog> selectList(CBCall<SearchFieldLogCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<SearchFieldLog> selectPage(CBCall<SearchFieldLogCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<SearchFieldLog>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<SearchFieldLogCB> cbLambda, EntityRowHandler<SearchFieldLog> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<SearchFieldLogCB> cbLambda, EntityRowHandler<List<SearchFieldLog>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(SearchFieldLog entity) {
        doInsert(entity, null);
    }

    public void insert(SearchFieldLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(SearchFieldLog entity) {
        doUpdate(entity, null);
    }

    public void update(SearchFieldLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(SearchFieldLog entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(SearchFieldLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(SearchFieldLog entity) {
        doDelete(entity, null);
    }

    public void delete(SearchFieldLog entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<SearchFieldLogCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<SearchFieldLog> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<SearchFieldLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<SearchFieldLog> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<SearchFieldLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<SearchFieldLog> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<SearchFieldLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
