package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.SuggestElevateWordDbm;
import org.codelibs.fess.es.cbean.SuggestElevateWordCB;
import org.codelibs.fess.es.exentity.SuggestElevateWord;
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
public abstract class BsSuggestElevateWordBhv extends AbstractBehavior<SuggestElevateWord, SuggestElevateWordCB> {

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
        return "suggest_elevate_word";
    }

    @Override
    public String asEsSearchType() {
        return "suggest_elevate_word";
    }

    @Override
    public SuggestElevateWordDbm asDBMeta() {
        return SuggestElevateWordDbm.getInstance();
    }

    @Override
    protected <RESULT extends SuggestElevateWord> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setBoost(DfTypeUtil.toFloat(source.get("boost")));
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setReading(DfTypeUtil.toString(source.get("reading")));
            result.setSuggestWord(DfTypeUtil.toString(source.get("suggestWord")));
            result.setTargetLabel(DfTypeUtil.toString(source.get("targetLabel")));
            result.setTargetRole(DfTypeUtil.toString(source.get("targetRole")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<SuggestElevateWordCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<SuggestElevateWord> selectEntity(CBCall<SuggestElevateWordCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<SuggestElevateWord> facadeSelectEntity(SuggestElevateWordCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends SuggestElevateWord> OptionalEntity<ENTITY> doSelectOptionalEntity(SuggestElevateWordCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public SuggestElevateWordCB newConditionBean() {
        return new SuggestElevateWordCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public SuggestElevateWord selectEntityWithDeletedCheck(CBCall<SuggestElevateWordCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<SuggestElevateWord> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<SuggestElevateWord> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends SuggestElevateWord> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected SuggestElevateWordCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends SuggestElevateWord> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends SuggestElevateWord> typeOfSelectedEntity() {
        return SuggestElevateWord.class;
    }

    @Override
    protected Class<SuggestElevateWord> typeOfHandlingEntity() {
        return SuggestElevateWord.class;
    }

    @Override
    protected Class<SuggestElevateWordCB> typeOfHandlingConditionBean() {
        return SuggestElevateWordCB.class;
    }

    public ListResultBean<SuggestElevateWord> selectList(CBCall<SuggestElevateWordCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<SuggestElevateWord> selectPage(CBCall<SuggestElevateWordCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<SuggestElevateWord>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<SuggestElevateWordCB> cbLambda, EntityRowHandler<SuggestElevateWord> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<SuggestElevateWordCB> cbLambda, EntityRowHandler<List<SuggestElevateWord>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(SuggestElevateWord entity) {
        doInsert(entity, null);
    }

    public void insert(SuggestElevateWord entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(SuggestElevateWord entity) {
        doUpdate(entity, null);
    }

    public void update(SuggestElevateWord entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(SuggestElevateWord entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(SuggestElevateWord entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(SuggestElevateWord entity) {
        doDelete(entity, null);
    }

    public void delete(SuggestElevateWord entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<SuggestElevateWordCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<SuggestElevateWord> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<SuggestElevateWord> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<SuggestElevateWord> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<SuggestElevateWord> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<SuggestElevateWord> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<SuggestElevateWord> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
