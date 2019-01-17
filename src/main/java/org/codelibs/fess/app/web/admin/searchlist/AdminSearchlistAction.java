/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.app.web.admin.searchlist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.net.URLUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.SearchService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.dbflute.optional.OptionalEntity;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.validation.VaMessenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * @author Keiichi Watanabe
 */
public class AdminSearchlistAction extends FessAdminAction {

    // ===================================================================================
    // Constant
    //
    private static final Logger logger = LoggerFactory.getLogger(AdminSearchlistAction.class);

    // ===================================================================================
    // Attribute
    // =========

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    protected QueryHelper queryHelper;

    @Resource
    protected SearchService searchService;

    @Resource
    protected HttpServletRequest request;

    public List<Map<String, Object>> documentItems;

    public String pageSize;

    public String currentPageNumber;

    public String allRecordCount;

    public String allPageCount;

    public boolean existNextPage;

    public boolean existPrevPage;

    public String currentStartRecordNumber;

    public String currentEndRecordNumber;

    public List<String> pageNumberList;

    public String execTime;

    // ===================================================================================
    // Hook
    // ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);

        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameSearchlist()));
    }

    // ===================================================================================
    // Search Execute
    // ==============
    @Execute
    public HtmlResponse index(final ListForm form) {
        saveToken();
        validate(form, messages -> {}, () -> asHtml(path_AdminError_AdminErrorJsp));
        return asListHtml();
    }

    protected HtmlResponse doSearch(final ListForm form) {
        validate(form, messages -> {}, () -> asListHtml());

        if (StringUtil.isBlank(form.q)) {
            // query matches on all documents.
            form.q = Constants.MATCHES_ALL_QUERY;
        }
        final WebRenderData renderData = new WebRenderData();
        form.initialize();
        request.setAttribute(Constants.SEARCH_LOG_ACCESS_TYPE, Constants.SEARCH_LOG_ACCESS_TYPE_ADMIN);
        try {
            searchService.search(form, renderData, getUserBean());
            return asListHtml().renderWith(data -> {
                renderData.register(data);
            });
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throwValidationError(e.getMessageCode(), () -> asListHtml());
        } catch (final ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throwValidationError(messages -> messages.addErrorsResultSizeExceeded(GLOBAL), () -> asListHtml());
        }

        throwValidationError(messages -> messages.addErrorsInvalidQueryUnknown(GLOBAL), () -> asListHtml());
        return null; // ignore
    }

    @Execute
    public HtmlResponse search(final ListForm form) {
        saveToken();
        return doSearch(form);
    }

    @Execute
    public HtmlResponse prev(final ListForm form) {
        saveToken();
        return doMove(form, -1);
    }

    @Execute
    public HtmlResponse next(final ListForm form) {
        saveToken();
        return doMove(form, 1);
    }

    @Execute
    public HtmlResponse move(final ListForm form) {
        saveToken();
        return doMove(form, 0);
    }

    protected HtmlResponse doMove(final ListForm form, final int move) {
        form.initialize();
        Integer pageNumber = form.pn;
        if (pageNumber != null && pageNumber > 0) {
            pageNumber = pageNumber + move;
            if (pageNumber < 1) {
                pageNumber = 1;
            }
            form.start = (pageNumber - 1) * form.getPageSize();
        }
        return doSearch(form);
    }

    // -----------------------------------------------------
    // Confirm
    // -------

    @Execute
    public HtmlResponse delete(final DeleteForm form) {
        validate(form, messages -> {}, () -> asListHtml());
        verifyToken(() -> asListHtml());
        final String docId = form.docId;
        try {
            final QueryBuilder query = QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId);
            fessEsClient.deleteByQuery(fessConfig.getIndexDocumentUpdateIndex(), fessConfig.getIndexDocumentType(), query);
            saveInfo(messages -> messages.addSuccessDeleteDocFromIndex(GLOBAL));
        } catch (final Exception e) {
            throwValidationError(messages -> messages.addErrorsFailedToDeleteDocInAdmin(GLOBAL), () -> asListHtml());
        }
        return asListHtml();
    }

    @Execute
    public HtmlResponse deleteall(final ListForm form) {
        validate(form, messages -> {}, () -> asListHtml());
        verifyToken(() -> asListHtml());
        try {
            searchService.deleteByQuery(request, form);
            saveInfo(messages -> messages.addSuccessDeleteDocFromIndex(GLOBAL));
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throwValidationError(e.getMessageCode(), () -> asListHtml());
        }
        return asListHtml();
    }

    @Execute
    public HtmlResponse createnew(final CreateForm form) {
        saveToken();
        form.initialize();
        form.crudMode = CrudMode.CREATE;
        getDoc(form).ifPresent(entity -> {
            form.doc = fessConfig.convertToEditableDoc(entity);
        });
        return asEditHtml();
    }

    @Execute
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, () -> asListHtml());
        getDoc(form).ifPresent(entity -> {
            form.doc = fessConfig.convertToEditableDoc(entity);
            form.id = (String) entity.remove(fessConfig.getIndexFieldId());
            form.version = (Long) entity.remove(fessConfig.getIndexFieldVersion());
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), () -> asListHtml());
        });
        saveToken();
        return asEditHtml();
    }

    @Execute
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, () -> asEditHtml());
        validateFields(form, v -> throwValidationError(v, () -> asEditHtml()));
        verifyToken(() -> asEditHtml());
        getDoc(form).ifPresent(
                entity -> {
                    try {
                        entity.putAll(fessConfig.convertToStorableDoc(form.doc));

                        final String newId = ComponentUtil.getCrawlingInfoHelper().generateId(entity);
                        entity.put(fessConfig.getIndexFieldId(), newId);

                        final String index = fessConfig.getIndexDocumentUpdateIndex();
                        final String type = fessConfig.getIndexDocumentType();
                        fessEsClient.store(index, type, entity);
                        saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
                    } catch (final Exception e) {
                        logger.error("Failed to add " + entity, e);
                        throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL, buildThrowableMessage(e)),
                                () -> asEditHtml());
                    }
                }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateInstance(GLOBAL), () -> asEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, () -> asEditHtml());
        validateFields(form, v -> throwValidationError(v, () -> asEditHtml()));
        verifyToken(() -> asEditHtml());
        getDoc(form).ifPresent(
                entity -> {
                    final String index = fessConfig.getIndexDocumentUpdateIndex();
                    final String type = fessConfig.getIndexDocumentType();
                    try {
                        entity.putAll(fessConfig.convertToStorableDoc(form.doc));

                        final String newId = ComponentUtil.getCrawlingInfoHelper().generateId(entity);
                        final String oldId = (String) entity.get(fessConfig.getIndexFieldId());
                        if (!newId.equals(oldId)) {
                            entity.put(fessConfig.getIndexFieldId(), newId);
                            final Long version = (Long) entity.remove(fessConfig.getIndexFieldVersion());
                            if (version != null && oldId != null) {
                                fessEsClient.delete(index, type, oldId, version);
                            }
                        }

                        fessEsClient.store(index, type, entity);
                        saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
                    } catch (final Exception e) {
                        logger.error("Failed to update " + entity, e);
                        throwValidationError(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, buildThrowableMessage(e)),
                                () -> asEditHtml());
                    }
                }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), () -> asEditHtml());
        });
        return redirectWith(getClass(), moreUrl("search").params("q", URLUtil.encode(form.q, Constants.UTF_8)));
    }

    // ===================================================================================
    //                                                                       Validation
    //                                                                           =========
    public static void validateFields(final CreateForm form, final Consumer<VaMessenger<FessMessages>> throwError) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();

        try {
            if (!fessConfig.validateIndexRequiredFields(form.doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexRequiredFields(form.doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyRequired(s, s)));
            }

            if (!fessConfig.validateIndexArrayFields(form.doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexArrayFields(form.doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyRequired(s, s)));
            }
            if (!fessConfig.validateIndexDateFields(form.doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexDateFields(form.doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyTypeDate(s, s)));
            }
            if (!fessConfig.validateIndexIntegerFields(form.doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexIntegerFields(form.doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyTypeInteger(s, s)));
            }
            if (!fessConfig.validateIndexLongFields(form.doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexLongFields(form.doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyTypeLong(s, s)));
            }
            if (!fessConfig.validateIndexFloatFields(form.doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexFloatFields(form.doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyTypeFloat(s, s)));
            }
            if (!fessConfig.validateIndexDoubleFields(form.doc)) {
                throwError.accept(messages -> fessConfig.invalidIndexDoubleFields(form.doc).stream().map(s -> "doc." + s)
                        .forEach(s -> messages.addErrorsPropertyTypeDouble(s, s)));
            }
        } catch (final Exception e) {
            throwError.accept(messages -> messages.addErrorsCrudFailedToUpdateCrudTable(GLOBAL, e.getMessage()));
        }
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========
    protected void verifyCrudMode(final int crudMode, final int expectedMode) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, () -> asListHtml());
        }
    }

    public static OptionalEntity<Map<String, Object>> getDoc(final CreateForm form) {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final FessEsClient fessEsClient = ComponentUtil.getFessEsClient();
        switch (form.crudMode) {
        case CrudMode.CREATE:
            final Map<String, Object> entity = new HashMap<>();
            entity.put(fessConfig.getIndexFieldDocId(), systemHelper.generateDocId(entity));
            return OptionalEntity.of(entity);
        case CrudMode.EDIT:
            final String docId;
            if (form.doc != null) {
                docId = (String) form.doc.get(fessConfig.getIndexFieldDocId());
            } else {
                docId = null;
            }
            if (StringUtil.isNotBlank(docId)) {
                return fessEsClient.getDocument(fessConfig.getIndexDocumentUpdateIndex(), fessConfig.getIndexDocumentType(), builder -> {
                    builder.setQuery(QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId));
                    return true;
                });
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminSearchlist_AdminSearchlistJsp);
    }

    private HtmlResponse asEditHtml() {
        return asHtml(path_AdminSearchlist_AdminSearchlistEditJsp);
    }

    protected static class WebRenderData extends SearchRenderData {

        public void register(final RenderData data) {
            RenderDataUtil.register(data, "queryId", queryId);
            RenderDataUtil.register(data, "documentItems", documentItems);
            RenderDataUtil.register(data, "facetResponse", facetResponse);
            RenderDataUtil.register(data, "appendHighlightParams", appendHighlightParams);
            RenderDataUtil.register(data, "execTime", execTime);
            RenderDataUtil.register(data, "pageSize", pageSize);
            RenderDataUtil.register(data, "currentPageNumber", currentPageNumber);
            RenderDataUtil.register(data, "allRecordCount", allRecordCount);
            RenderDataUtil.register(data, "allPageCount", allPageCount);
            RenderDataUtil.register(data, "existNextPage", existNextPage);
            RenderDataUtil.register(data, "existPrevPage", existPrevPage);
            RenderDataUtil.register(data, "currentStartRecordNumber", currentStartRecordNumber);
            RenderDataUtil.register(data, "currentEndRecordNumber", currentEndRecordNumber);
            RenderDataUtil.register(data, "pageNumberList", pageNumberList);
            RenderDataUtil.register(data, "partialResults", partialResults);
            RenderDataUtil.register(data, "queryTime", queryTime);
            RenderDataUtil.register(data, "searchQuery", searchQuery);
            RenderDataUtil.register(data, "requestedTime", requestedTime);
        }
    }
}
