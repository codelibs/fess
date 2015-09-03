package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.RequestHeaderDbm;
import org.codelibs.fess.es.cbean.RequestHeaderCB;
import org.codelibs.fess.es.exentity.RequestHeader;
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
public abstract class BsRequestHeaderBhv extends AbstractBehavior<RequestHeader, RequestHeaderCB> {

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
        return "request_header";
    }

    @Override
    public String asEsSearchType() {
        return "request_header";
    }

    @Override
    public RequestHeaderDbm asDBMeta() {
        return RequestHeaderDbm.getInstance();
    }

    @Override
    protected <RESULT extends RequestHeader> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            result.setValue(DfTypeUtil.toString(source.get("value")));
            result.setWebConfigId(DfTypeUtil.toString(source.get("webConfigId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<RequestHeaderCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<RequestHeader> selectEntity(CBCall<RequestHeaderCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<RequestHeader> facadeSelectEntity(RequestHeaderCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends RequestHeader> OptionalEntity<ENTITY> doSelectOptionalEntity(RequestHeaderCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public RequestHeaderCB newConditionBean() {
        return new RequestHeaderCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public RequestHeader selectEntityWithDeletedCheck(CBCall<RequestHeaderCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<RequestHeader> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<RequestHeader> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends RequestHeader> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected RequestHeaderCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends RequestHeader> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends RequestHeader> typeOfSelectedEntity() {
        return RequestHeader.class;
    }

    @Override
    protected Class<RequestHeader> typeOfHandlingEntity() {
        return RequestHeader.class;
    }

    @Override
    protected Class<RequestHeaderCB> typeOfHandlingConditionBean() {
        return RequestHeaderCB.class;
    }

    public ListResultBean<RequestHeader> selectList(CBCall<RequestHeaderCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<RequestHeader> selectPage(CBCall<RequestHeaderCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<RequestHeader>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<RequestHeaderCB> cbLambda, EntityRowHandler<RequestHeader> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<RequestHeaderCB> cbLambda, EntityRowHandler<List<RequestHeader>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(RequestHeader entity) {
        doInsert(entity, null);
    }

    public void insert(RequestHeader entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(RequestHeader entity) {
        doUpdate(entity, null);
    }

    public void update(RequestHeader entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(RequestHeader entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(RequestHeader entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(RequestHeader entity) {
        doDelete(entity, null);
    }

    public void delete(RequestHeader entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<RequestHeaderCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<RequestHeader> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<RequestHeader> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<RequestHeader> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<RequestHeader> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<RequestHeader> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<RequestHeader> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove

    @Override
    protected boolean isCompatibleBatchInsertDefaultEveryColumn() {
        return true;
    }
}
