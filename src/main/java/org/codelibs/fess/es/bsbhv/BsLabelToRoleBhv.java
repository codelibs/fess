package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.LabelToRoleDbm;
import org.codelibs.fess.es.cbean.LabelToRoleCB;
import org.codelibs.fess.es.exentity.LabelToRole;
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
public abstract class BsLabelToRoleBhv extends AbstractBehavior<LabelToRole, LabelToRoleCB> {

    @Override
    public String asTableDbName() {
        return "label_to_role";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public LabelToRoleDbm asDBMeta() {
        return LabelToRoleDbm.getInstance();
    }

    @Override
    protected <RESULT extends LabelToRole> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setId(toString(source.get("id")));
            result.setLabelTypeId(toString(source.get("labelTypeId")));
            result.setRoleTypeId(toString(source.get("roleTypeId")));
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

    // TODO create, modify, remove
}
