package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.SuggestBadWordDbm;
import org.codelibs.fess.es.cbean.SuggestBadWordCB;
import org.codelibs.fess.es.exentity.SuggestBadWord;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;

/**
 * @author FreeGen
 */
public abstract class BsSuggestBadWordBhv extends AbstractBehavior<SuggestBadWord, SuggestBadWordCB> {

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
        return "suggest_bad_word";
    }

    @Override
    public String asEsSearchType() {
        return "suggest_bad_word";
    }

    @Override
    public SuggestBadWordDbm asDBMeta() {
        return SuggestBadWordDbm.getInstance();
    }

    @Override
    protected <RESULT extends SuggestBadWord> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(toString(source.get("createdBy")));
            result.setCreatedTime(toLong(source.get("createdTime")));
            result.setId(toString(source.get("id")));
            result.setSuggestWord(toString(source.get("suggestWord")));
            result.setTargetLabel(toString(source.get("targetLabel")));
            result.setTargetRole(toString(source.get("targetRole")));
            result.setUpdatedBy(toString(source.get("updatedBy")));
            result.setUpdatedTime(toLong(source.get("updatedTime")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<SuggestBadWordCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<SuggestBadWord> selectEntity(CBCall<SuggestBadWordCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<SuggestBadWord> facadeSelectEntity(SuggestBadWordCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends SuggestBadWord> OptionalEntity<ENTITY> doSelectOptionalEntity(SuggestBadWordCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public SuggestBadWordCB newConditionBean() {
        return new SuggestBadWordCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public SuggestBadWord selectEntityWithDeletedCheck(CBCall<SuggestBadWordCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<SuggestBadWord> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<SuggestBadWord> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends SuggestBadWord> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected SuggestBadWordCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends SuggestBadWord> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends SuggestBadWord> typeOfSelectedEntity() {
        return SuggestBadWord.class;
    }

    @Override
    protected Class<SuggestBadWord> typeOfHandlingEntity() {
        return SuggestBadWord.class;
    }

    @Override
    protected Class<SuggestBadWordCB> typeOfHandlingConditionBean() {
        return SuggestBadWordCB.class;
    }

    public ListResultBean<SuggestBadWord> selectList(CBCall<SuggestBadWordCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<SuggestBadWord> selectPage(CBCall<SuggestBadWordCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<SuggestBadWord>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<SuggestBadWordCB> cbLambda, EntityRowHandler<SuggestBadWord> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<SuggestBadWordCB> cbLambda, EntityRowHandler<List<SuggestBadWord>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(SuggestBadWord entity) {
        doInsert(entity, null);
    }

    public void insert(SuggestBadWord entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(SuggestBadWord entity) {
        doUpdate(entity, null);
    }

    public void update(SuggestBadWord entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(SuggestBadWord entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(SuggestBadWord entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(SuggestBadWord entity) {
        doDelete(entity, null);
    }

    public void delete(SuggestBadWord entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<SuggestBadWordCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<SuggestBadWord> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<SuggestBadWord> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<SuggestBadWord> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<SuggestBadWord> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<SuggestBadWord> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<SuggestBadWord> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
