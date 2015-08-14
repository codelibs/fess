package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.KeyMatchDbm;
import org.codelibs.fess.es.cbean.KeyMatchCB;
import org.codelibs.fess.es.exentity.KeyMatch;
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
public abstract class BsKeyMatchBhv extends AbstractBehavior<KeyMatch, KeyMatchCB> {

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
        return "key_match";
    }

    @Override
    public String asEsSearchType() {
        return "key_match";
    }

    @Override
    public KeyMatchDbm asDBMeta() {
        return KeyMatchDbm.getInstance();
    }

    @Override
    protected <RESULT extends KeyMatch> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setBoost(DfTypeUtil.toFloat(source.get("boost")));
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setMaxSize(DfTypeUtil.toInteger(source.get("maxSize")));
            result.setQuery(DfTypeUtil.toString(source.get("query")));
            result.setTerm(DfTypeUtil.toString(source.get("term")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<KeyMatchCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<KeyMatch> selectEntity(CBCall<KeyMatchCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<KeyMatch> facadeSelectEntity(KeyMatchCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends KeyMatch> OptionalEntity<ENTITY> doSelectOptionalEntity(KeyMatchCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public KeyMatchCB newConditionBean() {
        return new KeyMatchCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public KeyMatch selectEntityWithDeletedCheck(CBCall<KeyMatchCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<KeyMatch> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<KeyMatch> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends KeyMatch> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected KeyMatchCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends KeyMatch> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends KeyMatch> typeOfSelectedEntity() {
        return KeyMatch.class;
    }

    @Override
    protected Class<KeyMatch> typeOfHandlingEntity() {
        return KeyMatch.class;
    }

    @Override
    protected Class<KeyMatchCB> typeOfHandlingConditionBean() {
        return KeyMatchCB.class;
    }

    public ListResultBean<KeyMatch> selectList(CBCall<KeyMatchCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<KeyMatch> selectPage(CBCall<KeyMatchCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<KeyMatch>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<KeyMatchCB> cbLambda, EntityRowHandler<KeyMatch> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<KeyMatchCB> cbLambda, EntityRowHandler<List<KeyMatch>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(KeyMatch entity) {
        doInsert(entity, null);
    }

    public void insert(KeyMatch entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(KeyMatch entity) {
        doUpdate(entity, null);
    }

    public void update(KeyMatch entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(KeyMatch entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(KeyMatch entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(KeyMatch entity) {
        doDelete(entity, null);
    }

    public void delete(KeyMatch entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<KeyMatchCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<KeyMatch> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<KeyMatch> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<KeyMatch> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<KeyMatch> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<KeyMatch> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<KeyMatch> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
