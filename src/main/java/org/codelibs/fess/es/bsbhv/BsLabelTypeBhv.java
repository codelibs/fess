package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.LabelTypeDbm;
import org.codelibs.fess.es.cbean.LabelTypeCB;
import org.codelibs.fess.es.exentity.LabelType;
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
public abstract class BsLabelTypeBhv extends AbstractBehavior<LabelType, LabelTypeCB> {

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
        return "label_type";
    }

    @Override
    public String asEsSearchType() {
        return "label_type";
    }

    @Override
    public LabelTypeDbm asDBMeta() {
        return LabelTypeDbm.getInstance();
    }

    @Override
    protected <RESULT extends LabelType> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(toString(source.get("createdBy")));
            result.setCreatedTime(toLong(source.get("createdTime")));
            result.setExcludedPaths(toString(source.get("excludedPaths")));
            result.setId(toString(source.get("id")));
            result.setIncludedPaths(toString(source.get("includedPaths")));
            result.setName(toString(source.get("name")));
            result.setSortOrder(toInteger(source.get("sortOrder")));
            result.setUpdatedBy(toString(source.get("updatedBy")));
            result.setUpdatedTime(toLong(source.get("updatedTime")));
            result.setValue(toString(source.get("value")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<LabelTypeCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<LabelType> selectEntity(CBCall<LabelTypeCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<LabelType> facadeSelectEntity(LabelTypeCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends LabelType> OptionalEntity<ENTITY> doSelectOptionalEntity(LabelTypeCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public LabelTypeCB newConditionBean() {
        return new LabelTypeCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public LabelType selectEntityWithDeletedCheck(CBCall<LabelTypeCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<LabelType> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<LabelType> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends LabelType> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected LabelTypeCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends LabelType> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends LabelType> typeOfSelectedEntity() {
        return LabelType.class;
    }

    @Override
    protected Class<LabelType> typeOfHandlingEntity() {
        return LabelType.class;
    }

    @Override
    protected Class<LabelTypeCB> typeOfHandlingConditionBean() {
        return LabelTypeCB.class;
    }

    public ListResultBean<LabelType> selectList(CBCall<LabelTypeCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<LabelType> selectPage(CBCall<LabelTypeCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<LabelType>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<LabelTypeCB> cbLambda, EntityRowHandler<LabelType> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<LabelTypeCB> cbLambda, EntityRowHandler<List<LabelType>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(LabelType entity) {
        doInsert(entity, null);
    }

    public void insert(LabelType entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(LabelType entity) {
        doUpdate(entity, null);
    }

    public void update(LabelType entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(LabelType entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(LabelType entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(LabelType entity) {
        doDelete(entity, null);
    }

    public void delete(LabelType entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<LabelTypeCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<LabelType> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<LabelType> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<LabelType> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<LabelType> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<LabelType> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<LabelType> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
