/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.opensearch.user.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.opensearch.user.allcommon.EsAbstractBehavior;
import org.codelibs.fess.opensearch.user.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.opensearch.user.bsentity.dbmeta.PasskeyDbm;
import org.codelibs.fess.opensearch.user.cbean.PasskeyCB;
import org.codelibs.fess.opensearch.user.exentity.Passkey;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.util.DfTypeUtil;
import org.codelibs.fesen.opensearch.action.bulk.BulkRequestBuilder;
import org.codelibs.fesen.opensearch.action.delete.DeleteRequestBuilder;
import org.codelibs.fesen.opensearch.action.index.IndexRequestBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsPasskeyBhv extends EsAbstractBehavior<Passkey, PasskeyCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_user.passkey";
    }

    @Override
    public String asEsIndexType() {
        return "passkey";
    }

    @Override
    public String asEsSearchType() {
        return "passkey";
    }

    @Override
    public PasskeyDbm asDBMeta() {
        return PasskeyDbm.getInstance();
    }

    @Override
    protected <RESULT extends Passkey> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setBackupEligible(DfTypeUtil.toBoolean(source.get("backupEligible")));
            result.setBackupState(DfTypeUtil.toBoolean(source.get("backupState")));
            result.setCreatedAt(DfTypeUtil.toLong(source.get("createdAt")));
            result.setLabel(DfTypeUtil.toString(source.get("label")));
            result.setLastUsedAt(DfTypeUtil.toLong(source.get("lastUsedAt")));
            result.setPublicKey(DfTypeUtil.toString(source.get("publicKey")));
            result.setSignCount(DfTypeUtil.toLong(source.get("signCount")));
            result.setTransports(toStringArray(source.get("transports")));
            result.setUserHandle(DfTypeUtil.toString(source.get("userHandle")));
            result.setUserName(DfTypeUtil.toString(source.get("userName")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends Passkey> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<PasskeyCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<Passkey> selectEntity(CBCall<PasskeyCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<Passkey> facadeSelectEntity(PasskeyCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends Passkey> OptionalEntity<ENTITY> doSelectOptionalEntity(PasskeyCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public PasskeyCB newConditionBean() {
        return new PasskeyCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public Passkey selectEntityWithDeletedCheck(CBCall<PasskeyCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<Passkey> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<Passkey> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends Passkey> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected PasskeyCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends Passkey> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends Passkey> typeOfSelectedEntity() {
        return Passkey.class;
    }

    @Override
    protected Class<Passkey> typeOfHandlingEntity() {
        return Passkey.class;
    }

    @Override
    protected Class<PasskeyCB> typeOfHandlingConditionBean() {
        return PasskeyCB.class;
    }

    public ListResultBean<Passkey> selectList(CBCall<PasskeyCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<Passkey> selectPage(CBCall<PasskeyCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<Passkey>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<PasskeyCB> cbLambda, EntityRowHandler<Passkey> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<PasskeyCB> cbLambda, EntityRowHandler<List<Passkey>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(Passkey entity) {
        doInsert(entity, null);
    }

    public void insert(Passkey entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(Passkey entity) {
        doUpdate(entity, null);
    }

    public void update(Passkey entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(Passkey entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(Passkey entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(Passkey entity) {
        doDelete(entity, null);
    }

    public void delete(Passkey entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<PasskeyCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<Passkey> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<Passkey> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<Passkey> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<Passkey> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<Passkey> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<Passkey> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<Passkey> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<Passkey> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<Passkey> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
