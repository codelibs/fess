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
import org.codelibs.fess.es.config.bsentity.dbmeta.CrawlingInfoParamDbm;
import org.codelibs.fess.es.config.cbean.CrawlingInfoParamCB;
import org.codelibs.fess.es.config.exentity.CrawlingInfoParam;
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
public abstract class BsCrawlingInfoParamBhv extends EsAbstractBehavior<CrawlingInfoParam, CrawlingInfoParamCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.crawling_info_param";
    }

    @Override
    public String asEsIndexType() {
        return "crawling_info_param";
    }

    @Override
    public String asEsSearchType() {
        return "crawling_info_param";
    }

    @Override
    public CrawlingInfoParamDbm asDBMeta() {
        return CrawlingInfoParamDbm.getInstance();
    }

    @Override
    protected <RESULT extends CrawlingInfoParam> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCrawlingInfoId(DfTypeUtil.toString(source.get("crawlingInfoId")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setKey(DfTypeUtil.toString(source.get("key")));
            result.setValue(DfTypeUtil.toString(source.get("value")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends CrawlingInfoParam> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<CrawlingInfoParamCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<CrawlingInfoParam> selectEntity(CBCall<CrawlingInfoParamCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<CrawlingInfoParam> facadeSelectEntity(CrawlingInfoParamCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends CrawlingInfoParam> OptionalEntity<ENTITY> doSelectOptionalEntity(CrawlingInfoParamCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public CrawlingInfoParamCB newConditionBean() {
        return new CrawlingInfoParamCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public CrawlingInfoParam selectEntityWithDeletedCheck(CBCall<CrawlingInfoParamCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<CrawlingInfoParam> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<CrawlingInfoParam> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends CrawlingInfoParam> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected CrawlingInfoParamCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends CrawlingInfoParam> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends CrawlingInfoParam> typeOfSelectedEntity() {
        return CrawlingInfoParam.class;
    }

    @Override
    protected Class<CrawlingInfoParam> typeOfHandlingEntity() {
        return CrawlingInfoParam.class;
    }

    @Override
    protected Class<CrawlingInfoParamCB> typeOfHandlingConditionBean() {
        return CrawlingInfoParamCB.class;
    }

    public ListResultBean<CrawlingInfoParam> selectList(CBCall<CrawlingInfoParamCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<CrawlingInfoParam> selectPage(CBCall<CrawlingInfoParamCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<CrawlingInfoParam>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<CrawlingInfoParamCB> cbLambda, EntityRowHandler<CrawlingInfoParam> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<CrawlingInfoParamCB> cbLambda, EntityRowHandler<List<CrawlingInfoParam>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(CrawlingInfoParam entity) {
        doInsert(entity, null);
    }

    public void insert(CrawlingInfoParam entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(CrawlingInfoParam entity) {
        doUpdate(entity, null);
    }

    public void update(CrawlingInfoParam entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(CrawlingInfoParam entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(CrawlingInfoParam entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(CrawlingInfoParam entity) {
        doDelete(entity, null);
    }

    public void delete(CrawlingInfoParam entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<CrawlingInfoParamCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<CrawlingInfoParam> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<CrawlingInfoParam> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<CrawlingInfoParam> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<CrawlingInfoParam> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<CrawlingInfoParam> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<CrawlingInfoParam> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<CrawlingInfoParam> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<CrawlingInfoParam> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<CrawlingInfoParam> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
