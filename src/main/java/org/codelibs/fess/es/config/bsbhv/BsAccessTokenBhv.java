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
import org.codelibs.fess.es.config.bsentity.dbmeta.AccessTokenDbm;
import org.codelibs.fess.es.config.cbean.AccessTokenCB;
import org.codelibs.fess.es.config.exentity.AccessToken;
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
public abstract class BsAccessTokenBhv extends EsAbstractBehavior<AccessToken, AccessTokenCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.access_token";
    }

    @Override
    public String asEsIndexType() {
        return "access_token";
    }

    @Override
    public String asEsSearchType() {
        return "access_token";
    }

    @Override
    public AccessTokenDbm asDBMeta() {
        return AccessTokenDbm.getInstance();
    }

    @Override
    protected <RESULT extends AccessToken> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setExpiredTime(DfTypeUtil.toLong(source.get("expiredTime")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setParameterName(DfTypeUtil.toString(source.get("parameter_name")));
            result.setPermissions(toStringArray(source.get("permissions")));
            result.setToken(DfTypeUtil.toString(source.get("token")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends AccessToken> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<AccessTokenCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<AccessToken> selectEntity(CBCall<AccessTokenCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<AccessToken> facadeSelectEntity(AccessTokenCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends AccessToken> OptionalEntity<ENTITY> doSelectOptionalEntity(AccessTokenCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public AccessTokenCB newConditionBean() {
        return new AccessTokenCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public AccessToken selectEntityWithDeletedCheck(CBCall<AccessTokenCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<AccessToken> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<AccessToken> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends AccessToken> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected AccessTokenCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends AccessToken> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends AccessToken> typeOfSelectedEntity() {
        return AccessToken.class;
    }

    @Override
    protected Class<AccessToken> typeOfHandlingEntity() {
        return AccessToken.class;
    }

    @Override
    protected Class<AccessTokenCB> typeOfHandlingConditionBean() {
        return AccessTokenCB.class;
    }

    public ListResultBean<AccessToken> selectList(CBCall<AccessTokenCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<AccessToken> selectPage(CBCall<AccessTokenCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<AccessToken>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<AccessTokenCB> cbLambda, EntityRowHandler<AccessToken> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<AccessTokenCB> cbLambda, EntityRowHandler<List<AccessToken>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(AccessToken entity) {
        doInsert(entity, null);
    }

    public void insert(AccessToken entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(AccessToken entity) {
        doUpdate(entity, null);
    }

    public void update(AccessToken entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(AccessToken entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(AccessToken entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(AccessToken entity) {
        doDelete(entity, null);
    }

    public void delete(AccessToken entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<AccessTokenCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<AccessToken> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<AccessToken> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<AccessToken> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<AccessToken> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<AccessToken> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<AccessToken> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<AccessToken> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<AccessToken> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<AccessToken> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
