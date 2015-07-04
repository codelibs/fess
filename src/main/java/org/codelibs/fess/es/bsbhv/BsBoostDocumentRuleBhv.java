package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.BoostDocumentRuleDbm;
import org.codelibs.fess.es.cbean.BoostDocumentRuleCB;
import org.codelibs.fess.es.exentity.BoostDocumentRule;
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
public abstract class BsBoostDocumentRuleBhv extends AbstractBehavior<BoostDocumentRule, BoostDocumentRuleCB> {

    @Override
    public String asTableDbName() {
        return "boost_document_rule";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public BoostDocumentRuleDbm asDBMeta() {
        return BoostDocumentRuleDbm.getInstance();
    }

    @Override
    protected <RESULT extends BoostDocumentRule> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setBoostExpr((String) source.get("boostExpr"));
            result.setCreatedBy((String) source.get("createdBy"));
            result.setCreatedTime((Long) source.get("createdTime"));
            result.setId((String) source.get("id"));
            result.setSortOrder((Integer) source.get("sortOrder"));
            result.setUpdatedBy((String) source.get("updatedBy"));
            result.setUpdatedTime((Long) source.get("updatedTime"));
            result.setUrlExpr((String) source.get("urlExpr"));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<BoostDocumentRuleCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<BoostDocumentRule> selectEntity(CBCall<BoostDocumentRuleCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<BoostDocumentRule> facadeSelectEntity(BoostDocumentRuleCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends BoostDocumentRule> OptionalEntity<ENTITY> doSelectOptionalEntity(BoostDocumentRuleCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public BoostDocumentRuleCB newConditionBean() {
        return new BoostDocumentRuleCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public BoostDocumentRule selectEntityWithDeletedCheck(CBCall<BoostDocumentRuleCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<BoostDocumentRule> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<BoostDocumentRule> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends BoostDocumentRule> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected BoostDocumentRuleCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends BoostDocumentRule> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends BoostDocumentRule> typeOfSelectedEntity() {
        return BoostDocumentRule.class;
    }

    @Override
    protected Class<BoostDocumentRule> typeOfHandlingEntity() {
        return BoostDocumentRule.class;
    }

    @Override
    protected Class<BoostDocumentRuleCB> typeOfHandlingConditionBean() {
        return BoostDocumentRuleCB.class;
    }

    public ListResultBean<BoostDocumentRule> selectList(CBCall<BoostDocumentRuleCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<BoostDocumentRule> selectPage(CBCall<BoostDocumentRuleCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<BoostDocumentRule>) facadeSelectList(createCB(cbLambda));
    }

    public void insert(BoostDocumentRule entity) {
        doInsert(entity, null);
    }

    public void insert(BoostDocumentRule entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(BoostDocumentRule entity) {
        doUpdate(entity, null);
    }

    public void update(BoostDocumentRule entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(BoostDocumentRule entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(BoostDocumentRule entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(BoostDocumentRule entity) {
        doDelete(entity, null);
    }

    public void delete(BoostDocumentRule entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    // TODO create, modify, remove
}
