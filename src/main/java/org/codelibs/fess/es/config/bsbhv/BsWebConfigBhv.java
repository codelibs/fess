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
import org.codelibs.fess.es.config.bsentity.dbmeta.WebConfigDbm;
import org.codelibs.fess.es.config.cbean.WebConfigCB;
import org.codelibs.fess.es.config.exentity.WebConfig;
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
public abstract class BsWebConfigBhv extends EsAbstractBehavior<WebConfig, WebConfigCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.web_config";
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
            result.setAvailable(DfTypeUtil.toBoolean(source.get("available")));
            result.setBoost(DfTypeUtil.toFloat(source.get("boost")));
            result.setConfigParameter(DfTypeUtil.toString(source.get("configParameter")));
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setDepth(DfTypeUtil.toInteger(source.get("depth")));
            result.setDescription(DfTypeUtil.toString(source.get("description")));
            result.setExcludedDocUrls(DfTypeUtil.toString(source.get("excludedDocUrls")));
            result.setExcludedUrls(DfTypeUtil.toString(source.get("excludedUrls")));
            result.setIncludedDocUrls(DfTypeUtil.toString(source.get("includedDocUrls")));
            result.setIncludedUrls(DfTypeUtil.toString(source.get("includedUrls")));
            result.setIntervalTime(DfTypeUtil.toInteger(source.get("intervalTime")));
            result.setMaxAccessCount(DfTypeUtil.toLong(source.get("maxAccessCount")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setNumOfThread(DfTypeUtil.toInteger(source.get("numOfThread")));
            result.setPermissions(toStringArray(source.get("permissions")));
            result.setSortOrder(DfTypeUtil.toInteger(source.get("sortOrder")));
            result.setTimeToLive(DfTypeUtil.toInteger(source.get("timeToLive")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            result.setUrls(DfTypeUtil.toString(source.get("urls")));
            result.setUserAgent(DfTypeUtil.toString(source.get("userAgent")));
            result.setVirtualHosts(toStringArray(source.get("virtualHosts")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends WebConfig> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
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
        // #pending same?
        return (PagingResultBean<WebConfig>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<WebConfigCB> cbLambda, EntityRowHandler<WebConfig> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<WebConfigCB> cbLambda, EntityRowHandler<List<WebConfig>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(WebConfig entity) {
        doInsert(entity, null);
    }

    public void insert(WebConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(WebConfig entity) {
        doUpdate(entity, null);
    }

    public void update(WebConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(WebConfig entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(WebConfig entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(WebConfig entity) {
        doDelete(entity, null);
    }

    public void delete(WebConfig entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<WebConfigCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<WebConfig> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<WebConfig> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<WebConfig> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<WebConfig> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<WebConfig> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<WebConfig> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<WebConfig> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<WebConfig> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<WebConfig> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
