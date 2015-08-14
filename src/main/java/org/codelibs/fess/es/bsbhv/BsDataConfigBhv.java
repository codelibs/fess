package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.DataConfigDbm;
import org.codelibs.fess.es.cbean.DataConfigCB;
import org.codelibs.fess.es.exentity.DataConfig;
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
public abstract class BsDataConfigBhv extends AbstractBehavior<DataConfig, DataConfigCB> {

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
        return "data_config";
    }

    @Override
    public String asEsSearchType() {
        return "data_config";
    }

    @Override
    public DataConfigDbm asDBMeta() {
        return DataConfigDbm.getInstance();
    }

    @Override
    protected <RESULT extends DataConfig> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setAvailable(DfTypeUtil.toBoolean(source.get("available")));
            result.setBoost(DfTypeUtil.toFloat(source.get("boost")));
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setHandlerName(DfTypeUtil.toString(source.get("handlerName")));
            result.setHandlerParameter(DfTypeUtil.toString(source.get("handlerParameter")));
            result.setHandlerScript(DfTypeUtil.toString(source.get("handlerScript")));
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setSortOrder(DfTypeUtil.toInteger(source.get("sortOrder")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<DataConfigCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<DataConfig> selectEntity(CBCall<DataConfigCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<DataConfig> facadeSelectEntity(DataConfigCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends DataConfig> OptionalEntity<ENTITY> doSelectOptionalEntity(DataConfigCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public DataConfigCB newConditionBean() {
        return new DataConfigCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public DataConfig selectEntityWithDeletedCheck(CBCall<DataConfigCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<DataConfig> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<DataConfig> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends DataConfig> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected DataConfigCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends DataConfig> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends DataConfig> typeOfSelectedEntity() {
        return DataConfig.class;
    }

    @Override
    protected Class<DataConfig> typeOfHandlingEntity() {
        return DataConfig.class;
    }

    @Override
    protected Class<DataConfigCB> typeOfHandlingConditionBean() {
        return DataConfigCB.class;
    }

    public ListResultBean<DataConfig> selectList(CBCall<DataConfigCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<DataConfig> selectPage(CBCall<DataConfigCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<DataConfig>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<DataConfigCB> cbLambda, EntityRowHandler<DataConfig> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<DataConfigCB> cbLambda, EntityRowHandler<List<DataConfig>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(DataConfig entity) {
        doInsert(entity, null);
    }

    public void insert(DataConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(DataConfig entity) {
        doUpdate(entity, null);
    }

    public void update(DataConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(DataConfig entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(DataConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(DataConfig entity) {
        doDelete(entity, null);
    }

    public void delete(DataConfig entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<DataConfigCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<DataConfig> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<DataConfig> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<DataConfig> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<DataConfig> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<DataConfig> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<DataConfig> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
