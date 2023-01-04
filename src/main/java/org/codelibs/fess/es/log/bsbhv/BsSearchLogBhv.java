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
package org.codelibs.fess.es.log.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.es.log.allcommon.EsAbstractBehavior;
import org.codelibs.fess.es.log.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.log.bsentity.dbmeta.SearchLogDbm;
import org.codelibs.fess.es.log.cbean.SearchLogCB;
import org.codelibs.fess.es.log.exentity.SearchLog;
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
public abstract class BsSearchLogBhv extends EsAbstractBehavior<SearchLog, SearchLogCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_log.search_log";
    }

    @Override
    public String asEsIndexType() {
        return "search_log";
    }

    @Override
    public String asEsSearchType() {
        return "search_log";
    }

    @Override
    public SearchLogDbm asDBMeta() {
        return SearchLogDbm.getInstance();
    }

    @Override
    protected <RESULT extends SearchLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setAccessType(DfTypeUtil.toString(source.get("accessType")));
            result.setClientIp(DfTypeUtil.toString(source.get("clientIp")));
            result.setHitCount(DfTypeUtil.toLong(source.get("hitCount")));
            result.setHitCountRelation(DfTypeUtil.toString(source.get("hitCountRelation")));
            result.setLanguages(DfTypeUtil.toString(source.get("languages")));
            result.setQueryId(DfTypeUtil.toString(source.get("queryId")));
            result.setQueryOffset(DfTypeUtil.toInteger(source.get("queryOffset")));
            result.setQueryPageSize(DfTypeUtil.toInteger(source.get("queryPageSize")));
            result.setQueryTime(DfTypeUtil.toLong(source.get("queryTime")));
            result.setReferer(DfTypeUtil.toString(source.get("referer")));
            result.setRequestedAt(toLocalDateTime(source.get("requestedAt")));
            result.setResponseTime(DfTypeUtil.toLong(source.get("responseTime")));
            result.setRoles(toStringArray(source.get("roles")));
            result.setSearchWord(DfTypeUtil.toString(source.get("searchWord")));
            result.setUser(DfTypeUtil.toString(source.get("user")));
            result.setUserAgent(DfTypeUtil.toString(source.get("userAgent")));
            result.setUserInfoId(DfTypeUtil.toString(source.get("userInfoId")));
            result.setUserSessionId(DfTypeUtil.toString(source.get("userSessionId")));
            result.setVirtualHost(DfTypeUtil.toString(source.get("virtualHost")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends SearchLog> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<SearchLogCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<SearchLog> selectEntity(CBCall<SearchLogCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<SearchLog> facadeSelectEntity(SearchLogCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchLog> OptionalEntity<ENTITY> doSelectOptionalEntity(SearchLogCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public SearchLogCB newConditionBean() {
        return new SearchLogCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public SearchLog selectEntityWithDeletedCheck(CBCall<SearchLogCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<SearchLog> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<SearchLog> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends SearchLog> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected SearchLogCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends SearchLog> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends SearchLog> typeOfSelectedEntity() {
        return SearchLog.class;
    }

    @Override
    protected Class<SearchLog> typeOfHandlingEntity() {
        return SearchLog.class;
    }

    @Override
    protected Class<SearchLogCB> typeOfHandlingConditionBean() {
        return SearchLogCB.class;
    }

    public ListResultBean<SearchLog> selectList(CBCall<SearchLogCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<SearchLog> selectPage(CBCall<SearchLogCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<SearchLog>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<SearchLogCB> cbLambda, EntityRowHandler<SearchLog> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<SearchLogCB> cbLambda, EntityRowHandler<List<SearchLog>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(SearchLog entity) {
        doInsert(entity, null);
    }

    public void insert(SearchLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(SearchLog entity) {
        doUpdate(entity, null);
    }

    public void update(SearchLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(SearchLog entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(SearchLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(SearchLog entity) {
        doDelete(entity, null);
    }

    public void delete(SearchLog entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<SearchLogCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<SearchLog> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<SearchLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<SearchLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<SearchLog> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<SearchLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<SearchLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<SearchLog> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<SearchLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<SearchLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
