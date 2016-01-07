/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
import org.codelibs.fess.es.config.allcommon.EsAbstractEntity;
import org.codelibs.fess.es.config.allcommon.EsAbstractEntity.RequestOptionCall;
import org.codelibs.fess.es.config.bsentity.dbmeta.WebConfigToLabelDbm;
import org.codelibs.fess.es.config.cbean.WebConfigToLabelCB;
import org.codelibs.fess.es.config.exentity.WebConfigToLabel;
import org.dbflute.Entity;
import org.dbflute.bhv.readable.CBCall;
import org.dbflute.bhv.readable.EntityRowHandler;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.result.ListResultBean;
import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.exception.IllegalBehaviorStateException;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.util.DfTypeUtil;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;

/**
 * @author ESFlute (using FreeGen)
 */
public abstract class BsWebConfigToLabelBhv extends EsAbstractBehavior<WebConfigToLabel, WebConfigToLabelCB> {

    // ===================================================================================
    //                                                                    Control Override
    //                                                                    ================
    @Override
    public String asTableDbName() {
        return asEsIndexType();
    }

    @Override
    protected String asEsIndex() {
        return ".fess_config";
    }

    @Override
    public String asEsIndexType() {
        return "web_config_to_label";
    }

    @Override
    public String asEsSearchType() {
        return "web_config_to_label";
    }

    @Override
    public WebConfigToLabelDbm asDBMeta() {
        return WebConfigToLabelDbm.getInstance();
    }

    @Override
    protected <RESULT extends WebConfigToLabel> RESULT createEntity(Map<String, Object> source, Class<? extends RESULT> entityType) {
        try {
            final RESULT result = entityType.newInstance();
            result.setLabelTypeId(DfTypeUtil.toString(source.get("labelTypeId")));
            result.setWebConfigId(DfTypeUtil.toString(source.get("webConfigId")));
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            final String msg = "Cannot create a new instance: " + entityType.getName();
            throw new IllegalBehaviorStateException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    public int selectCount(CBCall<WebConfigToLabelCB> cbLambda) {
        return facadeSelectCount(createCB(cbLambda));
    }

    public OptionalEntity<WebConfigToLabel> selectEntity(CBCall<WebConfigToLabelCB> cbLambda) {
        return facadeSelectEntity(createCB(cbLambda));
    }

    protected OptionalEntity<WebConfigToLabel> facadeSelectEntity(WebConfigToLabelCB cb) {
        return doSelectOptionalEntity(cb, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebConfigToLabel> OptionalEntity<ENTITY> doSelectOptionalEntity(WebConfigToLabelCB cb,
            Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectEntity(cb, tp), cb);
    }

    @Override
    public WebConfigToLabelCB newConditionBean() {
        return new WebConfigToLabelCB();
    }

    @Override
    protected Entity doReadEntity(ConditionBean cb) {
        return facadeSelectEntity(downcast(cb)).orElse(null);
    }

    public WebConfigToLabel selectEntityWithDeletedCheck(CBCall<WebConfigToLabelCB> cbLambda) {
        return facadeSelectEntityWithDeletedCheck(createCB(cbLambda));
    }

    public OptionalEntity<WebConfigToLabel> selectByPK(String id) {
        return facadeSelectByPK(id);
    }

    protected OptionalEntity<WebConfigToLabel> facadeSelectByPK(String id) {
        return doSelectOptionalByPK(id, typeOfSelectedEntity());
    }

    protected <ENTITY extends WebConfigToLabel> ENTITY doSelectByPK(String id, Class<? extends ENTITY> tp) {
        return doSelectEntity(xprepareCBAsPK(id), tp);
    }

    protected WebConfigToLabelCB xprepareCBAsPK(String id) {
        assertObjectNotNull("id", id);
        return newConditionBean().acceptPK(id);
    }

    protected <ENTITY extends WebConfigToLabel> OptionalEntity<ENTITY> doSelectOptionalByPK(String id, Class<? extends ENTITY> tp) {
        return createOptionalEntity(doSelectByPK(id, tp), id);
    }

    @Override
    protected Class<? extends WebConfigToLabel> typeOfSelectedEntity() {
        return WebConfigToLabel.class;
    }

    @Override
    protected Class<WebConfigToLabel> typeOfHandlingEntity() {
        return WebConfigToLabel.class;
    }

    @Override
    protected Class<WebConfigToLabelCB> typeOfHandlingConditionBean() {
        return WebConfigToLabelCB.class;
    }

    public ListResultBean<WebConfigToLabel> selectList(CBCall<WebConfigToLabelCB> cbLambda) {
        return facadeSelectList(createCB(cbLambda));
    }

    public PagingResultBean<WebConfigToLabel> selectPage(CBCall<WebConfigToLabelCB> cbLambda) {
        // #pending same?
        return (PagingResultBean<WebConfigToLabel>) facadeSelectList(createCB(cbLambda));
    }

    public void selectCursor(CBCall<WebConfigToLabelCB> cbLambda, EntityRowHandler<WebConfigToLabel> entityLambda) {
        facadeSelectCursor(createCB(cbLambda), entityLambda);
    }

    public void selectBulk(CBCall<WebConfigToLabelCB> cbLambda, EntityRowHandler<List<WebConfigToLabel>> entityLambda) {
        delegateSelectBulk(createCB(cbLambda), entityLambda, typeOfSelectedEntity());
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    public void insert(WebConfigToLabel entity) {
        doInsert(entity, null);
    }

    public void insert(WebConfigToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsert(entity, null);
    }

    public void update(WebConfigToLabel entity) {
        doUpdate(entity, null);
    }

    public void update(WebConfigToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doUpdate(entity, null);
    }

    public void insertOrUpdate(WebConfigToLabel entity) {
        doInsertOrUpdate(entity, null, null);
    }

    public void insertOrUpdate(WebConfigToLabel entity, RequestOptionCall<IndexRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().indexOption(opLambda);
        }
        doInsertOrUpdate(entity, null, null);
    }

    public void delete(WebConfigToLabel entity) {
        doDelete(entity, null);
    }

    public void delete(WebConfigToLabel entity, RequestOptionCall<DeleteRequestBuilder> opLambda) {
        if (entity instanceof EsAbstractEntity) {
            entity.asDocMeta().deleteOption(opLambda);
        }
        doDelete(entity, null);
    }

    public int queryDelete(CBCall<WebConfigToLabelCB> cbLambda) {
        return doQueryDelete(createCB(cbLambda), null);
    }

    public int[] batchInsert(List<WebConfigToLabel> list) {
        return batchInsert(list, null, null);
    }

    public int[] batchInsert(List<WebConfigToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchInsert(list, call, null);
    }

    public int[] batchInsert(List<WebConfigToLabel> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchInsert(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchUpdate(List<WebConfigToLabel> list) {
        return batchUpdate(list, null, null);
    }

    public int[] batchUpdate(List<WebConfigToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchUpdate(list, call, null);
    }

    public int[] batchUpdate(List<WebConfigToLabel> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchUpdate(new BulkList<>(list, call, entityCall), null);
    }

    public int[] batchDelete(List<WebConfigToLabel> list) {
        return batchDelete(list, null, null);
    }

    public int[] batchDelete(List<WebConfigToLabel> list, RequestOptionCall<BulkRequestBuilder> call) {
        return batchDelete(list, call, null);
    }

    public int[] batchDelete(List<WebConfigToLabel> list, RequestOptionCall<BulkRequestBuilder> call,
            RequestOptionCall<IndexRequestBuilder> entityCall) {
        return doBatchDelete(new BulkList<>(list, call, entityCall), null);
    }

    // #pending create, modify, remove
}
