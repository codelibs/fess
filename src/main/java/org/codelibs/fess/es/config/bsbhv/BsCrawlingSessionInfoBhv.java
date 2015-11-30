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
import org.codelibs.fess.es.config.bsentity.dbmeta.CrawlingSessionInfoDbm;
import org.codelibs.fess.es.config.cbean.CrawlingSessionInfoCB;
import org.codelibs.fess.es.config.exentity.CrawlingSessionInfo;
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
public abstract class BsCrawlingSessionInfoBhv extends EsAbstractBehavior<CrawlingSessionInfo, CrawlingSessionInfoCB> {

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
        return "crawling_session_info";
    }

    @Override
    public String asEsSearchType() {
        return "crawling_session_info";
    }

    @Override
    public CrawlingSessionInfoDbm asDBMeta() {
        return CrawlingSessionInfoDbm.getInstance();
    }

    @Override
    protected <RESULT extends CrawlingSessionInfo> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCrawlingSessionId(DfTypeUtil.toString(source.get("crawlingSessionId")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setKey(DfTypeUtil.toString(source.get("key")));
            result.setValue(DfTypeUtil.toString(source.get("value")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
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
        // #pending same?
        return (PagingResultBean<CrawlingSessionInfo>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<CrawlingSessionInfoCB> cbLambda, EntityRowHandler<CrawlingSessionInfo> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<CrawlingSessionInfoCB> cbLambda, EntityRowHandler<List<CrawlingSessionInfo>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda,typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(CrawlingSessionInfo entity) {
        doInsert(entity, null);
    }

    public void insert(CrawlingSessionInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(CrawlingSessionInfo entity) {
        doUpdate(entity, null);
    }

    public void update(CrawlingSessionInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(CrawlingSessionInfo entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(CrawlingSessionInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(CrawlingSessionInfo entity) {
        doDelete(entity, null);
    }

    public void delete(CrawlingSessionInfo entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<CrawlingSessionInfoCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<CrawlingSessionInfo> list) {
        return batchInsert(list, null);
    }

    public int[] batchInsert(List<CrawlingSessionInfo> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchInsert(new BulkList<>(list, call), null);
    }

    public int[] batchUpdate(List<CrawlingSessionInfo> list) {
        return batchUpdate(list, null);
    }

    public int[] batchUpdate(List<CrawlingSessionInfo> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchUpdate(new BulkList<>(list, call), null);
    }

    public int[] batchDelete(List<CrawlingSessionInfo> list) {
        return batchDelete(list, null);
    }

    public int[] batchDelete(List<CrawlingSessionInfo> list, RequestOptionCall<BulkRequestBuilder> call) {
        return doBatchDelete(new BulkList<>(list, call), null);
    }

    // #pending create, modify, remove
}

