package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.OverlappingHostDbm;
import org.codelibs.fess.es.cbean.OverlappingHostCB;
import org.codelibs.fess.es.exentity.OverlappingHost;
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
public abstract class BsOverlappingHostBhv extends AbstractBehavior<OverlappingHost, OverlappingHostCB> {

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
        return "overlapping_host";
    }

    @Override
    public String asEsSearchType() {
        return "overlapping_host";
    }

    @Override
    public OverlappingHostDbm asDBMeta() {
        return OverlappingHostDbm.getInstance();
    }

    @Override
    protected <RESULT extends OverlappingHost> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setOverlappingName(DfTypeUtil.toString(source.get("overlappingName")));
            result.setRegularName(DfTypeUtil.toString(source.get("regularName")));
            result.setSortOrder(DfTypeUtil.toInteger(source.get("sortOrder")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<OverlappingHostCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<OverlappingHost> selectEntity(CBCall<OverlappingHostCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<OverlappingHost> facadeSelectEntity(OverlappingHostCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends OverlappingHost> OptionalEntity<ENTITY> doSelectOptionalEntity(OverlappingHostCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public OverlappingHostCB newConditionBean() {
        return new OverlappingHostCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public OverlappingHost selectEntityWithDeletedCheck(CBCall<OverlappingHostCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<OverlappingHost> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<OverlappingHost> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends OverlappingHost> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected OverlappingHostCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends OverlappingHost> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends OverlappingHost> typeOfSelectedEntity() {
        return OverlappingHost.class;
    }

    @Override
    protected Class<OverlappingHost> typeOfHandlingEntity() {
        return OverlappingHost.class;
    }

    @Override
    protected Class<OverlappingHostCB> typeOfHandlingConditionBean() {
        return OverlappingHostCB.class;
    }

    public ListResultBean<OverlappingHost> selectList(CBCall<OverlappingHostCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<OverlappingHost> selectPage(CBCall<OverlappingHostCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<OverlappingHost>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<OverlappingHostCB> cbLambda, EntityRowHandler<OverlappingHost> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<OverlappingHostCB> cbLambda, EntityRowHandler<List<OverlappingHost>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(OverlappingHost entity) {
        doInsert(entity, null);
    }

    public void insert(OverlappingHost entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(OverlappingHost entity) {
        doUpdate(entity, null);
    }

    public void update(OverlappingHost entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(OverlappingHost entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(OverlappingHost entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(OverlappingHost entity) {
        doDelete(entity, null);
    }

    public void delete(OverlappingHost entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<OverlappingHostCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<OverlappingHost> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<OverlappingHost> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<OverlappingHost> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<OverlappingHost> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<OverlappingHost> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<OverlappingHost> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove

    @Override
    protected boolean isCompatibleBatchInsertDefaultEveryColumn() {
        return true;
    }
}
