/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.es.config.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.config.allcommon.EsAbstractBehavior;
import org.codelibs.fess.es.config.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.config.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.config.bsentity.dbmeta.CrawlingSessionDbm;
import org.codelibs.fess.es.config.cbean.CrawlingSessionCB;
import org.codelibs.fess.es.config.exentity.CrawlingSession;
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
 * @author ESFlute (using FreeGen)
 */
public abstract class BsCrawlingSessionBhv extends EsAbstractBehavior<CrawlingSession, CrawlingSessionCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
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
        return "crawling_session";
    }

    @Override
    public String asEsSearchType() {
        return "crawling_session";
    }

    @Override
    public CrawlingSessionDbm asDBMeta() {
        return CrawlingSessionDbm.getInstance();
    }

    @Override
    protected <RESULT extends CrawlingSession> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setExpiredTime(DfTypeUtil.toLong(source.get("expiredTime")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setSessionId(DfTypeUtil.toString(source.get("sessionId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<CrawlingSessionCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<CrawlingSession> selectEntity(CBCall<CrawlingSessionCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<CrawlingSession> facadeSelectEntity(CrawlingSessionCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends CrawlingSession> OptionalEntity<ENTITY> doSelectOptionalEntity(CrawlingSessionCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public CrawlingSessionCB newConditionBean() {
        return new CrawlingSessionCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public CrawlingSession selectEntityWithDeletedCheck(CBCall<CrawlingSessionCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<CrawlingSession> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<CrawlingSession> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends CrawlingSession> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected CrawlingSessionCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends CrawlingSession> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends CrawlingSession> typeOfSelectedEntity() {
        return CrawlingSession.class;
    }

    @Override
    protected Class<CrawlingSession> typeOfHandlingEntity() {
        return CrawlingSession.class;
    }

    @Override
    protected Class<CrawlingSessionCB> typeOfHandlingConditionBean() {
        return CrawlingSessionCB.class;
    }

    public ListResultBean<CrawlingSession> selectList(CBCall<CrawlingSessionCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<CrawlingSession> selectPage(CBCall<CrawlingSessionCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<CrawlingSession>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<CrawlingSessionCB> cbLambda, EntityRowHandler<CrawlingSession> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<CrawlingSessionCB> cbLambda, EntityRowHandler<List<CrawlingSession>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(CrawlingSession entity) {
        doInsert(entity, null);
    }

    public void insert(CrawlingSession entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(CrawlingSession entity) {
        doUpdate(entity, null);
    }

    public void update(CrawlingSession entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(CrawlingSession entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(CrawlingSession entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(CrawlingSession entity) {
        doDelete(entity, null);
    }

    public void delete(CrawlingSession entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<CrawlingSessionCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<CrawlingSession> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<CrawlingSession> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<CrawlingSession> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<CrawlingSession> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<CrawlingSession> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<CrawlingSession> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // #pending create, modify, remove
}
