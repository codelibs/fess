package org.codelibs.fess.es.bsbhv;

import java.util.Map;

import org.codelibs.fess.es.bsentity.AbstractEntity;
import org.codelibs.fess.es.bsentity.AbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.bsentity.dbmeta.CrawlingSessionInfoDbm;
import org.codelibs.fess.es.cbean.CrawlingSessionInfoCB;
import org.codelibs.fess.es.exentity.CrawlingSessionInfo;
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
public abstract class BsCrawlingSessionInfoBhv extends AbstractBehavior<CrawlingSessionInfo, CrawlingSessionInfoCB> {

    @Override
    public String asTableDbName() {
        return "crawling_session_info";
    }

    @Override
    protected String asIndexEsName() {
        return ".fess_config";
    }

    @Override
    public CrawlingSessionInfoDbm asDBMeta() {
        return CrawlingSessionInfoDbm.getInstance();
    }

    @Override
    protected <RESULT extends CrawlingSessionInfo> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCrawlingSessionId(toString(source.get("crawlingSessionId")));
            result.setCreatedTime(toLong(source.get("createdTime")));
            result.setId(toString(source.get("id")));
            result.setKey(toString(source.get("key")));
            result.setValue(toString(source.get("value")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    public int selectCount(CBCall<CrawlingSessionInfoCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<CrawlingSessionInfo> selectEntity(CBCall<CrawlingSessionInfoCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<CrawlingSessionInfo> facadeSelectEntity(CrawlingSessionInfoCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends CrawlingSessionInfo> OptionalEntity<ENTITY> doSelectOptionalEntity(CrawlingSessionInfoCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public CrawlingSessionInfoCB newConditionBean() {
        return new CrawlingSessionInfoCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public CrawlingSessionInfo selectEntityWithDeletedCheck(CBCall<CrawlingSessionInfoCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<CrawlingSessionInfo> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<CrawlingSessionInfo> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends CrawlingSessionInfo> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected CrawlingSessionInfoCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends CrawlingSessionInfo> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends CrawlingSessionInfo> typeOfSelectedEntity() {
        return CrawlingSessionInfo.class;
    }

    @Override
    protected Class<CrawlingSessionInfo> typeOfHandlingEntity() {
        return CrawlingSessionInfo.class;
    }

    @Override
    protected Class<CrawlingSessionInfoCB> typeOfHandlingConditionBean() {
        return CrawlingSessionInfoCB.class;
    }

    public ListResultBean<CrawlingSessionInfo> selectList(CBCall<CrawlingSessionInfoCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<CrawlingSessionInfo> selectPage(CBCall<CrawlingSessionInfoCB> cbLambda) {
        // TODO same?
        return (PagingResultBean<CrawlingSessionInfo>) facadeSelectList(createCB(cbLambda));
    }

    public void insert(CrawlingSessionInfo entity) {
        doInsert(entity, null);
    }

    public void insert(CrawlingSessionInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(CrawlingSessionInfo entity) {
        doUpdate(entity, null);
    }

    public void update(CrawlingSessionInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(CrawlingSessionInfo entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(CrawlingSessionInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(CrawlingSessionInfo entity) {
        doDelete(entity, null);
    }

    public void delete(CrawlingSessionInfo entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof AbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    // TODO create, modify, remove
}
