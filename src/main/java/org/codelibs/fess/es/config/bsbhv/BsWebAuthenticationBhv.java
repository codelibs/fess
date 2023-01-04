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
import org.codelibs.fess.es.config.bsentity.dbmeta.WebAuthenticationDbm;
import org.codelibs.fess.es.config.cbean.WebAuthenticationCB;
import org.codelibs.fess.es.config.exentity.WebAuthentication;
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
public abstract class BsWebAuthenticationBhv extends EsAbstractBehavior<WebAuthentication, WebAuthenticationCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.web_authentication";
    }

    @Override
    public String asEsIndexType() {
        return "web_authentication";
    }

    @Override
    public String asEsSearchType() {
        return "web_authentication";
    }

    @Override
    public WebAuthenticationDbm asDBMeta() {
        return WebAuthenticationDbm.getInstance();
    }

    @Override
    protected <RESULT extends WebAuthentication> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setAuthRealm(DfTypeUtil.toString(source.get("authRealm")));
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setHostname(DfTypeUtil.toString(source.get("hostname")));
            result.setParameters(DfTypeUtil.toString(source.get("parameters")));
            result.setPassword(DfTypeUtil.toString(source.get("password")));
            result.setPort(DfTypeUtil.toInteger(source.get("port")));
            result.setProtocolScheme(DfTypeUtil.toString(source.get("protocolScheme")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            result.setUsername(DfTypeUtil.toString(source.get("username")));
            result.setWebConfigId(DfTypeUtil.toString(source.get("webConfigId")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends WebAuthentication> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<WebAuthenticationCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<WebAuthentication> selectEntity(CBCall<WebAuthenticationCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<WebAuthentication> facadeSelectEntity(WebAuthenticationCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebAuthentication> OptionalEntity<ENTITY> doSelectOptionalEntity(WebAuthenticationCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public WebAuthenticationCB newConditionBean() {
        return new WebAuthenticationCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public WebAuthentication selectEntityWithDeletedCheck(CBCall<WebAuthenticationCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<WebAuthentication> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<WebAuthentication> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebAuthentication> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected WebAuthenticationCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends WebAuthentication> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends WebAuthentication> typeOfSelectedEntity() {
        return WebAuthentication.class;
    }

    @Override
    protected Class<WebAuthentication> typeOfHandlingEntity() {
        return WebAuthentication.class;
    }

    @Override
    protected Class<WebAuthenticationCB> typeOfHandlingConditionBean() {
        return WebAuthenticationCB.class;
    }

    public ListResultBean<WebAuthentication> selectList(CBCall<WebAuthenticationCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<WebAuthentication> selectPage(CBCall<WebAuthenticationCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<WebAuthentication>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<WebAuthenticationCB> cbLambda, EntityRowHandler<WebAuthentication> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<WebAuthenticationCB> cbLambda, EntityRowHandler<List<WebAuthentication>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(WebAuthentication entity) {
        doInsert(entity, null);
    }

    public void insert(WebAuthentication entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(WebAuthentication entity) {
        doUpdate(entity, null);
    }

    public void update(WebAuthentication entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(WebAuthentication entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(WebAuthentication entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(WebAuthentication entity) {
        doDelete(entity, null);
    }

    public void delete(WebAuthentication entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<WebAuthenticationCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<WebAuthentication> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<WebAuthentication> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<WebAuthentication> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<WebAuthentication> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<WebAuthentication> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<WebAuthentication> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<WebAuthentication> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<WebAuthentication> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<WebAuthentication> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
