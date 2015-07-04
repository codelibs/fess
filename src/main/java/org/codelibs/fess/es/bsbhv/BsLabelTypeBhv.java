package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.LabelTypeDbm;
import org.codelibs.fess.es.cbean.LabelTypeCB;
import org.codelibs.fess.es.exentity.LabelType;
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
public abstract class BsLabelTypeBhv extends AbstractBehavior<LabelType, LabelTypeCB> {

    @Override
    public String asTableDbName() {
        return "label_type";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public LabelTypeDbm asDBMeta() {
        return LabelTypeDbm.getInstance();
    }

    @Override
    protected <RESULT extends LabelType> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy((String) source.get("createdBy"));
            result.setCreatedTime((Long) source.get("createdTime"));
            result.setExcludedPaths((String) source.get("excludedPaths"));
            result.setId((String) source.get("id"));
            result.setIncludedPaths((String) source.get("includedPaths"));
            result.setName((String) source.get("name"));
            result.setSortOrder((Integer) source.get("sortOrder"));
            result.setUpdatedBy((String) source.get("updatedBy"));
            result.setUpdatedTime((Long) source.get("updatedTime"));
            result.setValue((String) source.get("value"));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<LabelTypeCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<LabelType> selectEntity(CBCall<LabelTypeCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<LabelType> facadeSelectEntity(LabelTypeCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends LabelType> OptionalEntity<ENTITY> doSelectOptionalEntity(LabelTypeCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public LabelTypeCB newConditionBean() {
        return new LabelTypeCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public LabelType selectEntityWithDeletedCheck(CBCall<LabelTypeCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<LabelType> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<LabelType> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends LabelType> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected LabelTypeCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends LabelType> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends LabelType> typeOfSelectedEntity() {
        return LabelType.class;
    }

    @Override
    protected Class<LabelType> typeOfHandlingEntity() {
        return LabelType.class;
    }

    @Override
    protected Class<LabelTypeCB> typeOfHandlingConditionBean() {
        return LabelTypeCB.class;
    }

    public ListResultBean<LabelType> selectList(CBCall<LabelTypeCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<LabelType> selectPage(CBCall<LabelTypeCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<LabelType>) facadeSelectList(createCB(cbLambda));
    }

    public void insert(LabelType entity) {
        doInsert(entity, null);
    }

    public void insert(LabelType entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(LabelType entity) {
        doUpdate(entity, null);
    }

    public void update(LabelType entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(LabelType entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(LabelType entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(LabelType entity) {
        doDelete(entity, null);
    }

    public void delete(LabelType entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    // TODO create, modify, remove
}
