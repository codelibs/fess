/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
import org.codelibs.fess.es.config.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.config.bsentity.dbmeta.CrawlingInfoDbm;
import org.codelibs.fess.es.config.cbean.CrawlingInfoCB;
import org.codelibs.fess.es.config.exentity.CrawlingInfo;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.util.DfTypeUtil;
import org.opensearch.action.bulk.BulkRequestBuilder;
import org.opensearch.action.delete.DeleteRequestBuilder;
import org.opensearch.action.index.IndexRequestBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsCrawlingInfoBhv extends EsAbstractBehavior<CrawlingInfo, CrawlingInfoCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.crawling_info";
    }

    @Override
    public String asEsIndexType() {
        return "crawling_info";
    }

    @Override
    public String asEsSearchType() {
        return "crawling_info";
    }

    @Override
    public CrawlingInfoDbm asDBMeta() {
        return CrawlingInfoDbm.getInstance();
    }

    @Override
    protected <RESULT extends CrawlingInfo> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setExpiredTime(DfTypeUtil.toLong(source.get("expiredTime")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setSessionId(DfTypeUtil.toString(source.get("sessionId")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends CrawlingInfo> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<CrawlingInfoCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<CrawlingInfo> selectEntity(CBCall<CrawlingInfoCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<CrawlingInfo> facadeSelectEntity(CrawlingInfoCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends CrawlingInfo> OptionalEntity<ENTITY> doSelectOptionalEntity(CrawlingInfoCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public CrawlingInfoCB newConditionBean() {
        return new CrawlingInfoCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public CrawlingInfo selectEntityWithDeletedCheck(CBCall<CrawlingInfoCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<CrawlingInfo> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<CrawlingInfo> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends CrawlingInfo> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected CrawlingInfoCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends CrawlingInfo> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends CrawlingInfo> typeOfSelectedEntity() {
        return CrawlingInfo.class;
    }

    @Override
    protected Class<CrawlingInfo> typeOfHandlingEntity() {
        return CrawlingInfo.class;
    }

    @Override
    protected Class<CrawlingInfoCB> typeOfHandlingConditionBean() {
        return CrawlingInfoCB.class;
    }

    public ListResultBean<CrawlingInfo> selectList(CBCall<CrawlingInfoCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<CrawlingInfo> selectPage(CBCall<CrawlingInfoCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<CrawlingInfo>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<CrawlingInfoCB> cbLambda, EntityRowHandler<CrawlingInfo> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<CrawlingInfoCB> cbLambda, EntityRowHandler<List<CrawlingInfo>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(CrawlingInfo entity) {
        doInsert(entity, null);
    }

    public void insert(CrawlingInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(CrawlingInfo entity) {
        doUpdate(entity, null);
    }

    public void update(CrawlingInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(CrawlingInfo entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(CrawlingInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(CrawlingInfo entity) {
        doDelete(entity, null);
    }

    public void delete(CrawlingInfo entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<CrawlingInfoCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<CrawlingInfo> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<CrawlingInfo> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<CrawlingInfo> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<CrawlingInfo> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<CrawlingInfo> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<CrawlingInfo> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<CrawlingInfo> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<CrawlingInfo> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<CrawlingInfo> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
