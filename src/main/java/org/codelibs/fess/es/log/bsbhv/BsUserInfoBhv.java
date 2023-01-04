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
import org.codelibs.fess.es.log.bsentity.dbmeta.UserInfoDbm;
import org.codelibs.fess.es.log.cbean.UserInfoCB;
import org.codelibs.fess.es.log.exentity.UserInfo;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.opensearch.action.bulk.BulkRequestBuilder;
import org.opensearch.action.delete.DeleteRequestBuilder;
import org.opensearch.action.index.IndexRequestBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsUserInfoBhv extends EsAbstractBehavior<UserInfo, UserInfoCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_log.user_info";
    }

    @Override
    public String asEsIndexType() {
        return "user_info";
    }

    @Override
    public String asEsSearchType() {
        return "user_info";
    }

    @Override
    public UserInfoDbm asDBMeta() {
        return UserInfoDbm.getInstance();
    }

    @Override
    protected <RESULT extends UserInfo> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedAt(toLocalDateTime(source.get("createdAt")));
            result.setUpdatedAt(toLocalDateTime(source.get("updatedAt")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends UserInfo> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<UserInfoCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<UserInfo> selectEntity(CBCall<UserInfoCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<UserInfo> facadeSelectEntity(UserInfoCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends UserInfo> OptionalEntity<ENTITY> doSelectOptionalEntity(UserInfoCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public UserInfoCB newConditionBean() {
        return new UserInfoCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public UserInfo selectEntityWithDeletedCheck(CBCall<UserInfoCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<UserInfo> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<UserInfo> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends UserInfo> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected UserInfoCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends UserInfo> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends UserInfo> typeOfSelectedEntity() {
        return UserInfo.class;
    }

    @Override
    protected Class<UserInfo> typeOfHandlingEntity() {
        return UserInfo.class;
    }

    @Override
    protected Class<UserInfoCB> typeOfHandlingConditionBean() {
        return UserInfoCB.class;
    }

    public ListResultBean<UserInfo> selectList(CBCall<UserInfoCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<UserInfo> selectPage(CBCall<UserInfoCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<UserInfo>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<UserInfoCB> cbLambda, EntityRowHandler<UserInfo> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<UserInfoCB> cbLambda, EntityRowHandler<List<UserInfo>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(UserInfo entity) {
        doInsert(entity, null);
    }

    public void insert(UserInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(UserInfo entity) {
        doUpdate(entity, null);
    }

    public void update(UserInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(UserInfo entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(UserInfo entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(UserInfo entity) {
        doDelete(entity, null);
    }

    public void delete(UserInfo entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<UserInfoCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<UserInfo> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<UserInfo> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<UserInfo> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<UserInfo> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<UserInfo> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<UserInfo> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<UserInfo> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<UserInfo> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<UserInfo> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
