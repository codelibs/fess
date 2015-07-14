package org.codelibs.fess.es.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.WebConfigDbm;
import org.codelibs.fess.es.cbean.WebConfigCB;
import org.codelibs.fess.es.exentity.WebConfig;
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
public abstract class BsWebConfigBhv extends AbstractBehavior<WebConfig, WebConfigCB> {

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
        return "web_config";
    }

    @Override
    public String asEsSearchType() {
        return "web_config";
    }

    @Override
    public WebConfigDbm asDBMeta() {
        return WebConfigDbm.getInstance();
    }

    @Override
    protected <RESULT extends WebConfig> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setAvailable(toBoolean(source.get("available")));
            result.setBoost(toFloat(source.get("boost")));
            result.setConfigParameter(toString(source.get("configParameter")));
            result.setCreatedBy(toString(source.get("createdBy")));
            result.setCreatedTime(toLong(source.get("createdTime")));
            result.setDepth(toInteger(source.get("depth")));
            result.setExcludedDocUrls(toString(source.get("excludedDocUrls")));
            result.setExcludedUrls(toString(source.get("excludedUrls")));
            result.setId(toString(source.get("id")));
            result.setIncludedDocUrls(toString(source.get("includedDocUrls")));
            result.setIncludedUrls(toString(source.get("includedUrls")));
            result.setIntervalTime(toInteger(source.get("intervalTime")));
            result.setMaxAccessCount(toLong(source.get("maxAccessCount")));
            result.setName(toString(source.get("name")));
            result.setNumOfThread(toInteger(source.get("numOfThread")));
            result.setSortOrder(toInteger(source.get("sortOrder")));
            result.setUpdatedBy(toString(source.get("updatedBy")));
            result.setUpdatedTime(toLong(source.get("updatedTime")));
            result.setUrls(toString(source.get("urls")));
            result.setUserAgent(toString(source.get("userAgent")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<WebConfigCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<WebConfig> selectEntity(CBCall<WebConfigCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<WebConfig> facadeSelectEntity(WebConfigCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebConfig> OptionalEntity<ENTITY> doSelectOptionalEntity(WebConfigCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public WebConfigCB newConditionBean() {
        return new WebConfigCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public WebConfig selectEntityWithDeletedCheck(CBCall<WebConfigCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<WebConfig> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<WebConfig> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebConfig> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected WebConfigCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends WebConfig> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends WebConfig> typeOfSelectedEntity() {
        return WebConfig.class;
    }

    @Override
    protected Class<WebConfig> typeOfHandlingEntity() {
        return WebConfig.class;
    }

    @Override
    protected Class<WebConfigCB> typeOfHandlingConditionBean() {
        return WebConfigCB.class;
    }

    public ListResultBean<WebConfig> selectList(CBCall<WebConfigCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<WebConfig> selectPage(CBCall<WebConfigCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<WebConfig>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<WebConfigCB> cbLambda, EntityRowHandler<WebConfig> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<WebConfigCB> cbLambda, EntityRowHandler<List<WebConfig>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    public void insert(WebConfig entity) {
        doInsert(entity, null);
    }

    public void insert(WebConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(WebConfig entity) {
        doUpdate(entity, null);
    }

    public void update(WebConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(WebConfig entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(WebConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(WebConfig entity) {
        doDelete(entity, null);
    }

    public void delete(WebConfig entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<WebConfigCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<WebConfig> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<WebConfig> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<WebConfig> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<WebConfig> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<WebConfig> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<WebConfig> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // TODO create, modify, remove
}
