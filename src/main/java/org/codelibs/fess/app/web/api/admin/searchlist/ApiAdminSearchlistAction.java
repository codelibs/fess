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
package org.codelibs.fess.app.web.api.admin.searchlist;

import static org.codelibs.fess.app.web.admin.searchlist.AdminSearchlistAction.getDoc;
import static org.codelibs.fess.app.web.admin.searchlist.AdminSearchlistAction.validateFields;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.api.ApiResult;
import org.codelibs.fess.app.web.api.ApiResult.ApiDeleteResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiDocResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiDocsResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiResponse;
import org.codelibs.fess.app.web.api.ApiResult.ApiUpdateResponse;
import org.codelibs.fess.app.web.api.ApiResult.Status;
import org.codelibs.fess.app.web.api.admin.FessApiAdminAction;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.es.client.SearchEngineClient;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.JsonResponse;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * @author shinsuke
 */
public class ApiAdminSearchlistAction extends FessApiAdminAction {

    // ===================================================================================
    // Constant
    //
    private static final Logger logger = LogManager.getLogger(ApiAdminSearchlistAction.class);

    // ===================================================================================
    // Attribute
    // =========
    @Resource
    protected SearchHelper searchHelper;

    @Resource
    protected SearchEngineClient searchEngineClient;

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    // GET /api/admin/searchlist/docs
    // POST /api/admin/searchlist/docs
    @Execute
    public JsonResponse<ApiResult> docs(final SearchBody body) {
        validateApi(body, messages -> {});

        if (StringUtil.isBlank(body.q)) {
            // query matches on all documents.
            body.q = Constants.MATCHES_ALL_QUERY;
        }

        final SearchRenderData renderData = new SearchRenderData();
        body.initialize();
        try {
            searchHelper.search(body, renderData, getUserBean());
            return asJson(new ApiDocsResponse().renderData(renderData).status(Status.OK).result());
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid query: {}", body.q, e);
            }
            throwValidationErrorApi(e.getMessageCode());
        } catch (final ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid offset: {}", body.offset, e);
            }
            throwValidationErrorApi(messages -> messages.addErrorsResultSizeExceeded(GLOBAL));
        }

        throwValidationErrorApi(messages -> messages.addErrorsInvalidQueryUnknown(GLOBAL));
        return null; // ignore
    }

    // GET /api/admin/searchlist/doc/{doc_id}
    @Execute
    public JsonResponse<ApiResult> get$doc(final String id) {
        return asJson(new ApiDocResponse().doc(searchEngineClient.getDocument(fessConfig.getIndexDocumentUpdateIndex(), builder -> {
            builder.setQuery(QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), id));
            return true;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id));
            return null;
        })).status(Status.OK).result());
    }

    // PUT /api/admin/searchlist/doc
    @Execute
    public JsonResponse<ApiResult> put$doc(final CreateBody body) {
        validateApi(body, messages -> {});
        if (body.doc == null) {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, "doc is required"));
        }
        validateFields(body, this::throwValidationErrorApi);
        body.crudMode = CrudMode.CREATE;
        final Map<String, Object> doc = getDoc(body).map(entity -> {
            try {
                entity.putAll(fessConfig.convertToStorableDoc(body.doc));

                final String newId = ComponentUtil.getCrawlingInfoHelper().generateId(entity);
                entity.put(fessConfig.getIndexFieldId(), newId);

                final String index = fessConfig.getIndexDocumentUpdateIndex();
                searchEngineClient.store(index, entity);
                saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to add {}", entity, e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL));
            return null;
        });
        return asJson(
                new ApiUpdateResponse().id(doc.get(fessConfig.getIndexFieldDocId()).toString()).created(true).status(Status.OK).result());
    }

    // POST /api/admin/searchlist/doc
    @Execute
    public JsonResponse<ApiResult> post$doc(final EditBody body) {
        validateApi(body, messages -> {});
        if (body.doc == null) {
            throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, "doc is required"));
        }
        validateFields(body, this::throwValidationErrorApi);
        body.crudMode = CrudMode.EDIT;
        final Map<String, Object> doc = getDoc(body).map(entity -> {
            final String index = fessConfig.getIndexDocumentUpdateIndex();
            try {
                entity.putAll(fessConfig.convertToStorableDoc(body.doc));

                final String newId = ComponentUtil.getCrawlingInfoHelper().generateId(entity);
                final String oldId = (String) entity.get(fessConfig.getIndexFieldId());
                if (!newId.equals(oldId)) {
                    entity.put(fessConfig.getIndexFieldId(), newId);
                    entity.remove(fessConfig.getIndexFieldVersion());
                    final Number seqNo = (Number) entity.remove(fessConfig.getIndexFieldSeqNo());
                    final Number primaryTerm = (Number) entity.remove(fessConfig.getIndexFieldPrimaryTerm());
                    if (seqNo != null && primaryTerm != null && oldId != null) {
                        searchEngineClient.delete(index, oldId, seqNo, primaryTerm);
                    }
                }

                searchEngineClient.store(index, entity);
                saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
            } catch (final Exception e) {
                logger.warn("Failed to update {}", entity, e);
                throwValidationErrorApi(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)));
            }
            return entity;
        }).orElseGet(() -> {
            throwValidationErrorApi(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, body.doc.toString()));
            return null;
        });
        return asJson(
                new ApiUpdateResponse().id(doc.get(fessConfig.getIndexFieldDocId()).toString()).created(false).status(Status.OK).result());
    }

    // DELETE /api/admin/searchlist/doc/{doc_id}
    @Execute
    public JsonResponse<ApiResult> delete$doc(final String id) {
        try {
            final QueryBuilder query = QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), id);
            searchEngineClient.deleteByQuery(fessConfig.getIndexDocumentUpdateIndex(), query);
            saveInfo(messages -> messages.addSuccessDeleteDocFromIndex(GLOBAL));
        } catch (final Exception e) {
            logger.warn("Failed to process a request.", e);
            throwValidationErrorApi(messages -> messages.addErrorsFailedToDeleteDocInAdmin(GLOBAL));
        }
        return asJson(new ApiResponse().status(Status.OK).result());
    }

    // DELETE /api/admin/searchlist/query
    @Execute
    public JsonResponse<ApiResult> delete$query(final SearchBody body) {
        validateApi(body, messages -> {});

        if (StringUtil.isBlank(body.q)) {
            throwValidationErrorApi(messages -> messages.addErrorsInvalidQueryUnknown(GLOBAL));
        }
        try {
            final long count = searchHelper.deleteByQuery(request, body);
            return asJson(new ApiDeleteResponse().count(count).status(Status.OK).result());
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throwValidationErrorApi(e.getMessageCode());
        } catch (final ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throwValidationErrorApi(messages -> messages.addErrorsResultSizeExceeded(GLOBAL));
        }

        throwValidationErrorApi(messages -> messages.addErrorsInvalidQueryUnknown(GLOBAL));
        return null; // ignore
    }
}
