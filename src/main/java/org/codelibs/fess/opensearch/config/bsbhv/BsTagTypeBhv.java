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
package org.codelibs.fess.opensearch.config.bsbhv;

import java.util.List;
import java.util.Map;

import org.codelibs.fess.opensearch.config.allcommon.EsAbstractBehavior;
import org.codelibs.fess.opensearch.config.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.opensearch.config.bsentity.dbmeta.TagTypeDbm;
import org.codelibs.fess.opensearch.config.cbean.TagTypeCB;
import org.codelibs.fess.opensearch.config.exentity.TagType;
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
public abstract class BsTagTypeBhv extends EsAbstractBehavior<TagType, TagTypeCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return "fess_config.tag_type";
    }

    @Override
    public String asEsIndexType() {
        return "tag_type";
    }

    @Override
    public String asEsSearchType() {
        return "tag_type";
    }

    @Override
    public TagTypeDbm asDBMeta() {
        return TagTypeDbm.getInstance();
    }

    @Override
    protected <RESULT extends TagType> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setCreatedBy(DfTypeUtil.toString(source.get("createdBy")));
            result.setCreatedTime(DfTypeUtil.toLong(source.get("createdTime")));
            result.setName(DfTypeUtil.toString(source.get("name")));
            result.setOwner(DfTypeUtil.toString(source.get("owner")));
            result.setPaths(toStringArray(source.get("paths")));
            result.setPermissions(toStringArray(source.get("permissions")));
            result.setSortOrder(DfTypeUtil.toInteger(source.get("sortOrder")));
            result.setUpdatedBy(DfTypeUtil.toString(source.get("updatedBy")));
            result.setUpdatedTime(DfTypeUtil.toLong(source.get("updatedTime")));
            result.setVirtualHost(DfTypeUtil.toString(source.get("virtualHost")));
            return updateEntity(source, result);
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    protected <RESULT extends TagType> RESULT updateEntity(Map<String, Object> source, RESULT result) {
        return result;
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<TagTypeCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<TagType> selectEntity(CBCall<TagTypeCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<TagType> facadeSelectEntity(TagTypeCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends TagType> OptionalEntity<ENTITY> doSelectOptionalEntity(TagTypeCB cb, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public TagTypeCB newConditionBean() {
        return new TagTypeCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public TagType selectEntityWithDeletedCheck(CBCall<TagTypeCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<TagType> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<TagType> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends TagType> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected TagTypeCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends TagType> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends TagType> typeOfSelectedEntity() {
        return TagType.class;
    }

    @Override
    protected Class<TagType> typeOfHandlingEntity() {
        return TagType.class;
    }

    @Override
    protected Class<TagTypeCB> typeOfHandlingConditionBean() {
        return TagTypeCB.class;
    }

    public ListResultBean<TagType> selectList(CBCall<TagTypeCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<TagType> selectPage(CBCall<TagTypeCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<TagType>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<TagTypeCB> cbLambda, EntityRowHandler<TagType> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<TagTypeCB> cbLambda, EntityRowHandler<List<TagType>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(TagType entity) {
        doInsert(entity, null);
    }

    public void insert(TagType entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsert(entity, null);
    }

    public void update(TagType entity) {
        doUpdate(entity, null);
    }

    public void update(TagType entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doUpdate(entity, null);
    }

    public void insertOrUpdate(TagType entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(TagType entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        entity.asDocMeta().indexOption(opLambda);
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(TagType entity) {
        doDelete(entity, null);
    }

    public void delete(TagType entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        entity.asDocMeta().deleteOption(opLambda);
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<TagTypeCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<TagType> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<TagType> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<TagType> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<TagType> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<TagType> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<TagType> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<TagType> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<TagType> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<TagType> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
