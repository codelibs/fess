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
package org.codelibs.fess.app.web.admin.searchlist;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.SearchService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.util.RenderDataUtil;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;
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
    protected ProcessHelper processHelper;

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
        runtime.registerData("isProcessRunning", processHelper.isProcessRunning());
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
        try {
            searchService.search(request, form, renderData, getUserBean());
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
        verifyToken(() -> asListHtml());
        validate(form, messages -> {}, () -> asListHtml());
        final String docId = form.docId;
        if (processHelper.isProcessRunning()) {
            throwValidationError(messages -> messages.addErrorsCannotDeleteDocBecauseOfRunning(GLOBAL), () -> asListHtml());
        }
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
        verifyToken(() -> asListHtml());
        validate(form, messages -> {}, () -> asListHtml());
        if (processHelper.isProcessRunning()) {
            throwValidationError(messages -> messages.addErrorsCannotDeleteDocBecauseOfRunning(GLOBAL), () -> asListHtml());
        }
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

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminSearchlist_AdminSearchlistJsp);
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
