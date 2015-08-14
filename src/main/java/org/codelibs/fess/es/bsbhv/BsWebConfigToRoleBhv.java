package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.WebConfigToRoleDbm;
import org.codelibs.fess.es.cbean.WebConfigToRoleCB;
import org.codelibs.fess.es.exentity.WebConfigToRole;
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
public abstract class BsWebConfigToRoleBhv extends AbstractBehavior<WebConfigToRole, WebConfigToRoleCB> {

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
        return "web_config_to_role";
    }

    @Override
    public String asEsSearchType() {
        return "web_config_to_role";
    }

    @Override
    public WebConfigToRoleDbm asDBMeta() {
        return WebConfigToRoleDbm.getInstance();
    }

    @Override
    protected <RESULT extends WebConfigToRole> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setRoleTypeId(DfTypeUtil.toString(source.get("roleTypeId")));
            result.setWebConfigId(DfTypeUtil.toString(source.get("webConfigId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<WebConfigToRoleCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<WebConfigToRole> selectEntity(CBCall<WebConfigToRoleCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<WebConfigToRole> facadeSelectEntity(WebConfigToRoleCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebConfigToRole> OptionalEntity<ENTITY> doSelectOptionalEntity(WebConfigToRoleCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public WebConfigToRoleCB newConditionBean() {
        return new WebConfigToRoleCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public WebConfigToRole selectEntityWithDeletedCheck(CBCall<WebConfigToRoleCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<WebConfigToRole> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<WebConfigToRole> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebConfigToRole> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected WebConfigToRoleCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends WebConfigToRole> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends WebConfigToRole> typeOfSelectedEntity() {
        return WebConfigToRole.class;
    }

    @Override
    protected Class<WebConfigToRole> typeOfHandlingEntity() {
        return WebConfigToRole.class;
    }

    @Override
    protected Class<WebConfigToRoleCB> typeOfHandlingConditionBean() {
        return WebConfigToRoleCB.class;
    }

    public ListResultBean<WebConfigToRole> selectList(CBCall<WebConfigToRoleCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<WebConfigToRole> selectPage(CBCall<WebConfigToRoleCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<WebConfigToRole>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<WebConfigToRoleCB> cbLambda, EntityRowHandler<WebConfigToRole> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<WebConfigToRoleCB> cbLambda, EntityRowHandler<List<WebConfigToRole>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(WebConfigToRole entity) {
        doInsert(entity, null);
    }

    public void insert(WebConfigToRole entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(WebConfigToRole entity) {
        doUpdate(entity, null);
    }

    public void update(WebConfigToRole entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(WebConfigToRole entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(WebConfigToRole entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(WebConfigToRole entity) {
        doDelete(entity, null);
    }

    public void delete(WebConfigToRole entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<WebConfigToRoleCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<WebConfigToRole> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<WebConfigToRole> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<WebConfigToRole> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<WebConfigToRole> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<WebConfigToRole> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<WebConfigToRole> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
