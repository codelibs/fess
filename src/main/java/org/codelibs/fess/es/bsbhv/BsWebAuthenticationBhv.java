package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.WebAuthenticationDbm;
import org.codelibs.fess.es.cbean.WebAuthenticationCB;
import org.codelibs.fess.es.exentity.WebAuthentication;
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
public abstract class BsWebAuthenticationBhv extends AbstractBehavior<WebAuthentication, WebAuthenticationCB> {

    @Override
    public String asTableDbName() {
        return "web_authentication";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public WebAuthenticationDbm asDBMeta() {
        return WebAuthenticationDbm.getInstance();
    }

    @Override
    protected <RESULT extends WebAuthentication> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setAuthRealm(toString(source.get("authRealm")));
            result.setCreatedBy(toString(source.get("createdBy")));
            result.setCreatedTime(toLong(source.get("createdTime")));
            result.setHostname(toString(source.get("hostname")));
            result.setId(toString(source.get("id")));
            result.setParameters(toString(source.get("parameters")));
            result.setPassword(toString(source.get("password")));
            result.setPort(toInteger(source.get("port")));
            result.setProtocolScheme(toString(source.get("protocolScheme")));
            result.setUpdatedBy(toString(source.get("updatedBy")));
            result.setUpdatedTime(toLong(source.get("updatedTime")));
            result.setUsername(toString(source.get("username")));
            result.setWebConfigId(toString(source.get("webConfigId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<WebAuthenticationCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<WebAuthentication> selectEntity(CBCall<WebAuthenticationCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<WebAuthentication> facadeSelectEntity(WebAuthenticationCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebAuthentication> OptionalEntity<ENTITY> doSelectOptionalEntity(WebAuthenticationCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public WebAuthenticationCB newConditionBean() {
        return new WebAuthenticationCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public WebAuthentication selectEntityWithDeletedCheck(CBCall<WebAuthenticationCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<WebAuthentication> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<WebAuthentication> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebAuthentication> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected WebAuthenticationCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends WebAuthentication> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends WebAuthentication> typeOfSelectedEntity() {
        return WebAuthentication.class;
    }

    @Override
    protected Class<WebAuthentication> typeOfHandlingEntity() {
        return WebAuthentication.class;
    }

    @Override
    protected Class<WebAuthenticationCB> typeOfHandlingConditionBean() {
        return WebAuthenticationCB.class;
    }

    public ListResultBean<WebAuthentication> selectList(CBCall<WebAuthenticationCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<WebAuthentication> selectPage(CBCall<WebAuthenticationCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<WebAuthentication>) facadeSelectList(createCB(cbLambda));
    }

    public void insert(WebAuthentication entity) {
        doInsert(entity, null);
    }

    public void insert(WebAuthentication entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(WebAuthentication entity) {
        doUpdate(entity, null);
    }

    public void update(WebAuthentication entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(WebAuthentication entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(WebAuthentication entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(WebAuthentication entity) {
        doDelete(entity, null);
    }

    public void delete(WebAuthentication entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    // TODO create, modify, remove
}
