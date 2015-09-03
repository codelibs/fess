package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.LabelToRoleDbm;
import org.codelibs.fess.es.cbean.LabelToRoleCB;
import org.codelibs.fess.es.exentity.LabelToRole;
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
public abstract class BsLabelToRoleBhv extends AbstractBehavior<LabelToRole, LabelToRoleCB> {

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
        return "label_to_role";
    }

    @Override
    public String asEsSearchType() {
        return "label_to_role";
    }

    @Override
    public LabelToRoleDbm asDBMeta() {
        return LabelToRoleDbm.getInstance();
    }

    @Override
    protected <RESULT extends LabelToRole> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setLabelTypeId(DfTypeUtil.toString(source.get("labelTypeId")));
            result.setRoleTypeId(DfTypeUtil.toString(source.get("roleTypeId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<LabelToRoleCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<LabelToRole> selectEntity(CBCall<LabelToRoleCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<LabelToRole> facadeSelectEntity(LabelToRoleCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends LabelToRole> OptionalEntity<ENTITY> doSelectOptionalEntity(LabelToRoleCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public LabelToRoleCB newConditionBean() {
        return new LabelToRoleCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public LabelToRole selectEntityWithDeletedCheck(CBCall<LabelToRoleCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<LabelToRole> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<LabelToRole> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends LabelToRole> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected LabelToRoleCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends LabelToRole> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends LabelToRole> typeOfSelectedEntity() {
        return LabelToRole.class;
    }

    @Override
    protected Class<LabelToRole> typeOfHandlingEntity() {
        return LabelToRole.class;
    }

    @Override
    protected Class<LabelToRoleCB> typeOfHandlingConditionBean() {
        return LabelToRoleCB.class;
    }

    public ListResultBean<LabelToRole> selectList(CBCall<LabelToRoleCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<LabelToRole> selectPage(CBCall<LabelToRoleCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<LabelToRole>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<LabelToRoleCB> cbLambda, EntityRowHandler<LabelToRole> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<LabelToRoleCB> cbLambda, EntityRowHandler<List<LabelToRole>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(LabelToRole entity) {
        doInsert(entity, null);
    }

    public void insert(LabelToRole entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(LabelToRole entity) {
        doUpdate(entity, null);
    }

    public void update(LabelToRole entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(LabelToRole entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(LabelToRole entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(LabelToRole entity) {
        doDelete(entity, null);
    }

    public void delete(LabelToRole entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<LabelToRoleCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<LabelToRole> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<LabelToRole> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<LabelToRole> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<LabelToRole> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<LabelToRole> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<LabelToRole> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove

    @Override
    protected boolean isCompatibleBatchInsertDefaultEveryColumn() {
        return true;
    }
}
