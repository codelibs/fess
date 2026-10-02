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
package org.codelibs.fess.opensearch.log.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.opensearch.log.allcommon.EsAbstractBehavior;
import org.codelibs.fess.opensearch.log.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.opensearch.log.bsentity.dbmeta.TagLogDbm;
import org.codelibs.fess.opensearch.log.cbean.TagLogCB;
import org.codelibs.fess.opensearch.log.exentity.TagLog;
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
public abstract class BsTagLogBhv extends EsAbstractBehavior<TagLog, TagLogCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_log.tag_log";
    }

    @Override
    public String asEsIndexType() {
        return "tag_log";
    }

    @Override
    public String asEsSearchType() {
        return "tag_log";
    }

    @Override
    public TagLogDbm asDBMeta() {
        return TagLogDbm.getInstance();
    }

    @Override
    protected <RESULT extends TagLog> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedAt(toLocalDateTime(source.get("createdAt")));
            result.setDocId(DfTypeUtil.toString(source.get("docId")));
            result.setTag(DfTypeUtil.toString(source.get("tag")));
            result.setUrl(DfTypeUtil.toString(source.get("url")));
            result.setUser(DfTypeUtil.toString(source.get("user")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends TagLog> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<TagLogCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<TagLog> selectEntity(CBCall<TagLogCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<TagLog> facadeSelectEntity(TagLogCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends TagLog> OptionalEntity<ENTITY> doSelectOptionalEntity(TagLogCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public TagLogCB newConditionBean() {
        return new TagLogCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public TagLog selectEntityWithDeletedCheck(CBCall<TagLogCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<TagLog> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<TagLog> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends TagLog> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected TagLogCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends TagLog> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends TagLog> typeOfSelectedEntity() {
        return TagLog.class;
    }

    @Override
    protected Class<TagLog> typeOfHandlingEntity() {
        return TagLog.class;
    }

    @Override
    protected Class<TagLogCB> typeOfHandlingConditionBean() {
        return TagLogCB.class;
    }

    public ListResultBean<TagLog> selectList(CBCall<TagLogCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<TagLog> selectPage(CBCall<TagLogCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<TagLog>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<TagLogCB> cbLambda, EntityRowHandler<TagLog> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<TagLogCB> cbLambda, EntityRowHandler<List<TagLog>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(TagLog entity) {
        doInsert(entity, null);
    }

    public void insert(TagLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(TagLog entity) {
        doUpdate(entity, null);
    }

    public void update(TagLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(TagLog entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(TagLog entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(TagLog entity) {
        doDelete(entity, null);
    }

    public void delete(TagLog entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<TagLogCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<TagLog> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<TagLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<TagLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<TagLog> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<TagLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<TagLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<TagLog> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<TagLog> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<TagLog> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
