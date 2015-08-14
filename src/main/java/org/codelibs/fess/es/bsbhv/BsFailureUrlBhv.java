package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.FailureUrlDbm;
import org.codelibs.fess.es.cbean.FailureUrlCB;
import org.codelibs.fess.es.exentity.FailureUrl;
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
public abstract class BsFailureUrlBhv extends AbstractBehavior<FailureUrl, FailureUrlCB> {

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
        return "failure_url";
    }

    @Override
    public String asEsSearchType() {
        return "failure_url";
    }

    @Override
    public FailureUrlDbm asDBMeta() {
        return FailureUrlDbm.getInstance();
    }

    @Override
    protected <RESULT extends FailureUrl> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setConfigId(DfTypeUtil.toString(source.get("configId")));
            result.setErrorCount(DfTypeUtil.toInteger(source.get("errorCount")));
            result.setErrorLog(DfTypeUtil.toString(source.get("errorLog")));
            result.setErrorName(DfTypeUtil.toString(source.get("errorName")));
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setLastAccessTime(DfTypeUtil.toLong(source.get("lastAccessTime")));
            result.setThreadName(DfTypeUtil.toString(source.get("threadName")));
            result.setUrl(DfTypeUtil.toString(source.get("url")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<FailureUrlCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<FailureUrl> selectEntity(CBCall<FailureUrlCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<FailureUrl> facadeSelectEntity(FailureUrlCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FailureUrl> OptionalEntity<ENTITY> doSelectOptionalEntity(FailureUrlCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public FailureUrlCB newConditionBean() {
        return new FailureUrlCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public FailureUrl selectEntityWithDeletedCheck(CBCall<FailureUrlCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<FailureUrl> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<FailureUrl> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends FailureUrl> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected FailureUrlCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends FailureUrl> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends FailureUrl> typeOfSelectedEntity() {
        return FailureUrl.class;
    }

    @Override
    protected Class<FailureUrl> typeOfHandlingEntity() {
        return FailureUrl.class;
    }

    @Override
    protected Class<FailureUrlCB> typeOfHandlingConditionBean() {
        return FailureUrlCB.class;
    }

    public ListResultBean<FailureUrl> selectList(CBCall<FailureUrlCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<FailureUrl> selectPage(CBCall<FailureUrlCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<FailureUrl>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<FailureUrlCB> cbLambda, EntityRowHandler<FailureUrl> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<FailureUrlCB> cbLambda, EntityRowHandler<List<FailureUrl>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(FailureUrl entity) {
        doInsert(entity, null);
    }

    public void insert(FailureUrl entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(FailureUrl entity) {
        doUpdate(entity, null);
    }

    public void update(FailureUrl entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(FailureUrl entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(FailureUrl entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(FailureUrl entity) {
        doDelete(entity, null);
    }

    public void delete(FailureUrl entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<FailureUrlCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<FailureUrl> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<FailureUrl> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<FailureUrl> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<FailureUrl> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<FailureUrl> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<FailureUrl> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
