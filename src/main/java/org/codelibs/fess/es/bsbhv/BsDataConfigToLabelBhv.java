package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.DataConfigToLabelDbm;
import org.codelibs.fess.es.cbean.DataConfigToLabelCB;
import org.codelibs.fess.es.exentity.DataConfigToLabel;
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
public abstract class BsDataConfigToLabelBhv extends AbstractBehavior<DataConfigToLabel, DataConfigToLabelCB> {

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
        return "data_config_to_label";
    }

    @Override
    public String asEsSearchType() {
        return "data_config_to_label";
    }

    @Override
    public DataConfigToLabelDbm asDBMeta() {
        return DataConfigToLabelDbm.getInstance();
    }

    @Override
    protected <RESULT extends DataConfigToLabel> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setDataConfigId(DfTypeUtil.toString(source.get("dataConfigId")));
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setLabelTypeId(DfTypeUtil.toString(source.get("labelTypeId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<DataConfigToLabelCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<DataConfigToLabel> selectEntity(CBCall<DataConfigToLabelCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<DataConfigToLabel> facadeSelectEntity(DataConfigToLabelCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends DataConfigToLabel> OptionalEntity<ENTITY> doSelectOptionalEntity(DataConfigToLabelCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public DataConfigToLabelCB newConditionBean() {
        return new DataConfigToLabelCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public DataConfigToLabel selectEntityWithDeletedCheck(CBCall<DataConfigToLabelCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<DataConfigToLabel> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<DataConfigToLabel> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends DataConfigToLabel> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected DataConfigToLabelCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends DataConfigToLabel> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends DataConfigToLabel> typeOfSelectedEntity() {
        return DataConfigToLabel.class;
    }

    @Override
    protected Class<DataConfigToLabel> typeOfHandlingEntity() {
        return DataConfigToLabel.class;
    }

    @Override
    protected Class<DataConfigToLabelCB> typeOfHandlingConditionBean() {
        return DataConfigToLabelCB.class;
    }

    public ListResultBean<DataConfigToLabel> selectList(CBCall<DataConfigToLabelCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<DataConfigToLabel> selectPage(CBCall<DataConfigToLabelCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<DataConfigToLabel>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<DataConfigToLabelCB> cbLambda, EntityRowHandler<DataConfigToLabel> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<DataConfigToLabelCB> cbLambda, EntityRowHandler<List<DataConfigToLabel>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(DataConfigToLabel entity) {
        doInsert(entity, null);
    }

    public void insert(DataConfigToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(DataConfigToLabel entity) {
        doUpdate(entity, null);
    }

    public void update(DataConfigToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(DataConfigToLabel entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(DataConfigToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(DataConfigToLabel entity) {
        doDelete(entity, null);
    }

    public void delete(DataConfigToLabel entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<DataConfigToLabelCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<DataConfigToLabel> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<DataConfigToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<DataConfigToLabel> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<DataConfigToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<DataConfigToLabel> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<DataConfigToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
