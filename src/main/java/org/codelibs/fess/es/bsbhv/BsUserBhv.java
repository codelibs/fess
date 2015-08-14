package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.UserDbm;
import org.codelibs.fess.es.cbean.UserCB;
import org.codelibs.fess.es.exentity.User;
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
public abstract class BsUserBhv extends AbstractBehavior<User, UserCB> {

    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return ".fess_user";
    }

    @Override
    public String asEsIndexType() {
        return "user";
    }

    @Override
    public String asEsSearchType() {
        return "user";
    }

    @Override
    public UserDbm asDBMeta() {
        return UserDbm.getInstance();
    }

    @Override
    protected <RESULT extends User> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setGroup(DfTypeUtil.toString(source.get("group")));
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setPassword(DfTypeUtil.toString(source.get("password")));
            result.setRole(DfTypeUtil.toString(source.get("role")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<UserCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<User> selectEntity(CBCall<UserCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<User> facadeSelectEntity(UserCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends User> OptionalEntity<ENTITY> doSelectOptionalEntity(UserCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public UserCB newConditionBean() {
        return new UserCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public User selectEntityWithDeletedCheck(CBCall<UserCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<User> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<User> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends User> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected UserCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends User> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends User> typeOfSelectedEntity() {
        return User.class;
    }

    @Override
    protected Class<User> typeOfHandlingEntity() {
        return User.class;
    }

    @Override
    protected Class<UserCB> typeOfHandlingConditionBean() {
        return UserCB.class;
    }

    public ListResultBean<User> selectList(CBCall<UserCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<User> selectPage(CBCall<UserCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<User>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<UserCB> cbLambda, EntityRowHandler<User> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<UserCB> cbLambda, EntityRowHandler<List<User>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(User entity) {
        doInsert(entity, null);
    }

    public void insert(User entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(User entity) {
        doUpdate(entity, null);
    }

    public void update(User entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(User entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(User entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(User entity) {
        doDelete(entity, null);
    }

    public void delete(User entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<UserCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<User> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<User> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<User> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<User> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<User> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<User> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
