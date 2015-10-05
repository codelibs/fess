package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.WebConfigToLabelDbm;
import org.codelibs.fess.es.cbean.WebConfigToLabelCB;
import org.codelibs.fess.es.exentity.WebConfigToLabel;
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
public abstract class BsWebConfigToLabelBhv extends AbstractBehavior<WebConfigToLabel, WebConfigToLabelCB> {

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
        return "web_config_to_label";
    }

    @Override
    public String asEsSearchType() {
        return "web_config_to_label";
    }

    @Override
    public WebConfigToLabelDbm asDBMeta() {
        return WebConfigToLabelDbm.getInstance();
    }

    @Override
    protected <RESULT extends WebConfigToLabel> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setId(DfTypeUtil.toString(source.get("id")));
            result.setLabelTypeId(DfTypeUtil.toString(source.get("labelTypeId")));
            result.setWebConfigId(DfTypeUtil.toString(source.get("webConfigId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<WebConfigToLabelCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<WebConfigToLabel> selectEntity(CBCall<WebConfigToLabelCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<WebConfigToLabel> facadeSelectEntity(WebConfigToLabelCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebConfigToLabel> OptionalEntity<ENTITY> doSelectOptionalEntity(WebConfigToLabelCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public WebConfigToLabelCB newConditionBean() {
        return new WebConfigToLabelCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public WebConfigToLabel selectEntityWithDeletedCheck(CBCall<WebConfigToLabelCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<WebConfigToLabel> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<WebConfigToLabel> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebConfigToLabel> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected WebConfigToLabelCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends WebConfigToLabel> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends WebConfigToLabel> typeOfSelectedEntity() {
        return WebConfigToLabel.class;
    }

    @Override
    protected Class<WebConfigToLabel> typeOfHandlingEntity() {
        return WebConfigToLabel.class;
    }

    @Override
    protected Class<WebConfigToLabelCB> typeOfHandlingConditionBean() {
        return WebConfigToLabelCB.class;
    }

    public ListResultBean<WebConfigToLabel> selectList(CBCall<WebConfigToLabelCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<WebConfigToLabel> selectPage(CBCall<WebConfigToLabelCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<WebConfigToLabel>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<WebConfigToLabelCB> cbLambda, EntityRowHandler<WebConfigToLabel> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<WebConfigToLabelCB> cbLambda, EntityRowHandler<List<WebConfigToLabel>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(WebConfigToLabel entity) {
        doInsert(entity, null);
    }

    public void insert(WebConfigToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(WebConfigToLabel entity) {
        doUpdate(entity, null);
    }

    public void update(WebConfigToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(WebConfigToLabel entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(WebConfigToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(WebConfigToLabel entity) {
        doDelete(entity, null);
    }

    public void delete(WebConfigToLabel entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<WebConfigToLabelCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<WebConfigToLabel> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<WebConfigToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<WebConfigToLabel> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<WebConfigToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<WebConfigToLabel> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<WebConfigToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove

    @Override
    protected boolean isCompatibleBatchInsertDefaultEveryColumn() {
        return true;
    }
}
