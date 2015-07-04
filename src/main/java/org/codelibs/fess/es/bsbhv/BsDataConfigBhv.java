package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.DataConfigDbm;
import org.codelibs.fess.es.cbean.DataConfigCB;
import org.codelibs.fess.es.exentity.DataConfig;
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
public abstract class BsDataConfigBhv extends AbstractBehavior<DataConfig, DataConfigCB> {

    @Override
    public String asTableDbName() {
        return "data_config";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public DataConfigDbm asDBMeta() {
        return DataConfigDbm.getInstance();
    }

    @Override
    protected <RESULT extends DataConfig> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setAvailable((Boolean) source.get("available"));
            result.setBoost((Float) source.get("boost"));
            result.setCreatedBy((String) source.get("createdBy"));
            result.setCreatedTime((Long) source.get("createdTime"));
            result.setHandlerName((String) source.get("handlerName"));
            result.setHandlerParameter((String) source.get("handlerParameter"));
            result.setHandlerScript((String) source.get("handlerScript"));
            result.setId((String) source.get("id"));
            result.setName((String) source.get("name"));
            result.setSortOrder((Integer) source.get("sortOrder"));
            result.setUpdatedBy((String) source.get("updatedBy"));
            result.setUpdatedTime((Long) source.get("updatedTime"));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<DataConfigCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<DataConfig> selectEntity(CBCall<DataConfigCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<DataConfig> facadeSelectEntity(DataConfigCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends DataConfig> OptionalEntity<ENTITY> doSelectOptionalEntity(DataConfigCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public DataConfigCB newConditionBean() {
        return new DataConfigCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public DataConfig selectEntityWithDeletedCheck(CBCall<DataConfigCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<DataConfig> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<DataConfig> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends DataConfig> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected DataConfigCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends DataConfig> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends DataConfig> typeOfSelectedEntity() {
        return DataConfig.class;
    }

    @Override
    protected Class<DataConfig> typeOfHandlingEntity() {
        return DataConfig.class;
    }

    @Override
    protected Class<DataConfigCB> typeOfHandlingConditionBean() {
        return DataConfigCB.class;
    }

    public ListResultBean<DataConfig> selectList(CBCall<DataConfigCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<DataConfig> selectPage(CBCall<DataConfigCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<DataConfig>) facadeSelectList(createCB(cbLambda));
    }

    public void insert(DataConfig entity) {
        doInsert(entity, null);
    }

    public void insert(DataConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(DataConfig entity) {
        doUpdate(entity, null);
    }

    public void update(DataConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(DataConfig entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(DataConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(DataConfig entity) {
        doDelete(entity, null);
    }

    public void delete(DataConfig entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    // TODO create, modify, remove
}
