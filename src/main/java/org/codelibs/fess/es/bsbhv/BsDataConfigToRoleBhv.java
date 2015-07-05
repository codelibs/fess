package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.DataConfigToRoleDbm;
import org.codelibs.fess.es.cbean.DataConfigToRoleCB;
import org.codelibs.fess.es.exentity.DataConfigToRole;
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
public abstract class BsDataConfigToRoleBhv extends AbstractBehavior<DataConfigToRole, DataConfigToRoleCB> {

    @Override
    public String asTableDbName() {
        return "data_config_to_role";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public DataConfigToRoleDbm asDBMeta() {
        return DataConfigToRoleDbm.getInstance();
    }

    @Override
    protected <RESULT extends DataConfigToRole> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setDataConfigId(toString(source.get("dataConfigId")));
            result.setId(toString(source.get("id")));
            result.setRoleTypeId(toString(source.get("roleTypeId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<DataConfigToRoleCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<DataConfigToRole> selectEntity(CBCall<DataConfigToRoleCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<DataConfigToRole> facadeSelectEntity(DataConfigToRoleCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends DataConfigToRole> OptionalEntity<ENTITY> doSelectOptionalEntity(DataConfigToRoleCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public DataConfigToRoleCB newConditionBean() {
        return new DataConfigToRoleCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public DataConfigToRole selectEntityWithDeletedCheck(CBCall<DataConfigToRoleCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<DataConfigToRole> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<DataConfigToRole> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends DataConfigToRole> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected DataConfigToRoleCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends DataConfigToRole> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends DataConfigToRole> typeOfSelectedEntity() {
        return DataConfigToRole.class;
    }

    @Override
    protected Class<DataConfigToRole> typeOfHandlingEntity() {
        return DataConfigToRole.class;
    }

    @Override
    protected Class<DataConfigToRoleCB> typeOfHandlingConditionBean() {
        return DataConfigToRoleCB.class;
    }

    public ListResultBean<DataConfigToRole> selectList(CBCall<DataConfigToRoleCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<DataConfigToRole> selectPage(CBCall<DataConfigToRoleCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<DataConfigToRole>) facadeSelectList(createCB(cbLambda));
    }

    public void insert(DataConfigToRole entity) {
        doInsert(entity, null);
    }

    public void insert(DataConfigToRole entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(DataConfigToRole entity) {
        doUpdate(entity, null);
    }

    public void update(DataConfigToRole entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(DataConfigToRole entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(DataConfigToRole entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(DataConfigToRole entity) {
        doDelete(entity, null);
    }

    public void delete(DataConfigToRole entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    // TODO create, modify, remove
}
