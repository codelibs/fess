package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.FileAuthenticationDbm;
import org.codelibs.fess.es.cbean.FileAuthenticationCB;
import org.codelibs.fess.es.exentity.FileAuthentication;
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
public abstract class BsFileAuthenticationBhv extends AbstractBehavior<FileAuthentication, FileAuthenticationCB> {

    @Override
    public String asTableDbName() {
        return "file_authentication";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public FileAuthenticationDbm asDBMeta() {
        return FileAuthenticationDbm.getInstance();
    }

    @Override
    protected <RESULT extends FileAuthentication> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(toString(source.get("createdBy")));
            result.setCreatedTime(toLong(source.get("createdTime")));
            result.setFileConfigId(toString(source.get("fileConfigId")));
            result.setHostname(toString(source.get("hostname")));
            result.setId(toString(source.get("id")));
            result.setParameters(toString(source.get("parameters")));
            result.setPassword(toString(source.get("password")));
            result.setPort(toInteger(source.get("port")));
            result.setProtocolScheme(toString(source.get("protocolScheme")));
            result.setUpdatedBy(toString(source.get("updatedBy")));
            result.setUpdatedTime(toLong(source.get("updatedTime")));
            result.setUsername(toString(source.get("username")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<FileAuthenticationCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<FileAuthentication> selectEntity(CBCall<FileAuthenticationCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<FileAuthentication> facadeSelectEntity(FileAuthenticationCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileAuthentication> OptionalEntity<ENTITY> doSelectOptionalEntity(FileAuthenticationCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public FileAuthenticationCB newConditionBean() {
        return new FileAuthenticationCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public FileAuthentication selectEntityWithDeletedCheck(CBCall<FileAuthenticationCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<FileAuthentication> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<FileAuthentication> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends FileAuthentication> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected FileAuthenticationCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends FileAuthentication> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends FileAuthentication> typeOfSelectedEntity() {
        return FileAuthentication.class;
    }

    @Override
    protected Class<FileAuthentication> typeOfHandlingEntity() {
        return FileAuthentication.class;
    }

    @Override
    protected Class<FileAuthenticationCB> typeOfHandlingConditionBean() {
        return FileAuthenticationCB.class;
    }

    public ListResultBean<FileAuthentication> selectList(CBCall<FileAuthenticationCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<FileAuthentication> selectPage(CBCall<FileAuthenticationCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<FileAuthentication>) facadeSelectList(createCB(cbLambda));
    }

    public void insert(FileAuthentication entity) {
        doInsert(entity, null);
    }

    public void insert(FileAuthentication entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(FileAuthentication entity) {
        doUpdate(entity, null);
    }

    public void update(FileAuthentication entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(FileAuthentication entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(FileAuthentication entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(FileAuthentication entity) {
        doDelete(entity, null);
    }

    public void delete(FileAuthentication entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    // TODO create, modify, remove
}
