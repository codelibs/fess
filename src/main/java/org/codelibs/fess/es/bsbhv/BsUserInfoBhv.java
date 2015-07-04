package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.UserInfoDbm;
import org.codelibs.fess.es.cbean.UserInfoCB;
import org.codelibs.fess.es.exentity.UserInfo;
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
public abstract class BsUserInfoBhv extends AbstractBehavior<UserInfo, UserInfoCB> {

    @Override
    public String asTableDbName() {
        return "user_info";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public UserInfoDbm asDBMeta() {
        return UserInfoDbm.getInstance();
    }

    @Override
    protected <RESULT extends UserInfo> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCode((String) source.get("code"));
            result.setCreatedTime((Long) source.get("createdTime"));
            result.setId((String) source.get("id"));
            result.setUpdatedTime((Long) source.get("updatedTime"));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<UserInfoCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<UserInfo> selectEntity(CBCall<UserInfoCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<UserInfo> facadeSelectEntity(UserInfoCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends UserInfo> OptionalEntity<ENTITY> doSelectOptionalEntity(UserInfoCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public UserInfoCB newConditionBean() {
        return new UserInfoCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public UserInfo selectEntityWithDeletedCheck(CBCall<UserInfoCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<UserInfo> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<UserInfo> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends UserInfo> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected UserInfoCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends UserInfo> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends UserInfo> typeOfSelectedEntity() {
        return UserInfo.class;
    }

    @Override
    protected Class<UserInfo> typeOfHandlingEntity() {
        return UserInfo.class;
    }

    @Override
    protected Class<UserInfoCB> typeOfHandlingConditionBean() {
        return UserInfoCB.class;
    }

    public ListResultBean<UserInfo> selectList(CBCall<UserInfoCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<UserInfo> selectPage(CBCall<UserInfoCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<UserInfo>) facadeSelectList(createCB(cbLambda));
    }

    public void insert(UserInfo entity) {
        doInsert(entity, null);
    }

    public void insert(UserInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(UserInfo entity) {
        doUpdate(entity, null);
    }

    public void update(UserInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(UserInfo entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(UserInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(UserInfo entity) {
        doDelete(entity, null);
    }

    public void delete(UserInfo entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    // TODO create, modify, remove
}
