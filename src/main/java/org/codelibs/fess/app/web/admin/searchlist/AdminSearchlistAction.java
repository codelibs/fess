/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.SystemHelper;
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
    private SystemHelper systemHelper;

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    protected QueryHelper queryHelper;

    @Resource
    protected JobHelper jobHelper;

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

        if (StringUtil.isBlank(form.query)) {
            // query matches on all documents.
            form.query = Constants.MATCHES_ALL_QUERY;
        }
        return asListHtml().renderWith(data -> {
            doSearchInternal(data, form);
        });
    }

    private void doSearchInternal(final RenderData data, final ListForm form) {
        form.initialize();
        try {
            final WebRenderData renderData = new WebRenderData(data);
            searchService.search(request, form, renderData);
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throwValidationError(e.getMessageCode(), () -> asListHtml());
        } catch (final ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throwValidationError(messages -> {
                messages.addErrorsResultSizeExceeded(GLOBAL);
            }, () -> asHtml(path_AdminError_AdminErrorJsp));
        }
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
        if (jobHelper.isCrawlProcessRunning()) {
            throwValidationError(messages -> messages.addErrorsCannotDeleteDocBecauseOfRunning(GLOBAL), () -> asListHtml());
        }
        try {
            final QueryBuilder query = QueryBuilders.termQuery(fessConfig.getIndexFieldDocId(), docId);
            fessEsClient.deleteByQuery(fessConfig.getIndexDocumentIndex(), fessConfig.getIndexDocumentType(), query);
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
        if (jobHelper.isCrawlProcessRunning()) {
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

    public boolean isSolrProcessRunning() {
        return jobHelper.isCrawlProcessRunning();
    }

    // ===================================================================================
    //                                                                              JSP
    //                                                                           =========

    private HtmlResponse asListHtml() {
        return asHtml(path_AdminSearchlist_AdminSearchlistJsp);
    }

    protected static class WebRenderData extends SearchRenderData {
        private final RenderData data;

        WebRenderData(final RenderData data) {
            this.data = data;
        }

        @Override
        public void setDocumentItems(final List<Map<String, Object>> documentItems) {
            data.register("documentItems", documentItems);
            super.setDocumentItems(documentItems);
        }

        @Override
        public void setExecTime(final String execTime) {
            data.register("execTime", execTime);
            super.setExecTime(execTime);
        }

        @Override
        public void setPageSize(final int pageSize) {
            data.register("pageSize", pageSize);
            super.setPageSize(pageSize);
        }

        @Override
        public void setCurrentPageNumber(final int currentPageNumber) {
            data.register("currentPageNumber", currentPageNumber);
            super.setCurrentPageNumber(currentPageNumber);
        }

        @Override
        public void setAllRecordCount(final long allRecordCount) {
            data.register("allRecordCount", allRecordCount);
            super.setAllRecordCount(allRecordCount);
        }

        @Override
        public void setAllPageCount(final int allPageCount) {
            data.register("allPageCount", allPageCount);
            super.setAllPageCount(allPageCount);
        }

        @Override
        public void setExistNextPage(final boolean existNextPage) {
            data.register("existNextPage", existNextPage);
            super.setExistNextPage(existNextPage);
        }

        @Override
        public void setExistPrevPage(final boolean existPrevPage) {
            data.register("existPrevPage", existPrevPage);
            super.setExistPrevPage(existPrevPage);
        }

        @Override
        public void setCurrentStartRecordNumber(final long currentStartRecordNumber) {
            data.register("currentStartRecordNumber", currentStartRecordNumber);
            super.setCurrentStartRecordNumber(currentStartRecordNumber);
        }

        @Override
        public void setCurrentEndRecordNumber(final long currentEndRecordNumber) {
            data.register("currentEndRecordNumber", currentEndRecordNumber);
            super.setCurrentEndRecordNumber(currentEndRecordNumber);
        }

        @Override
        public void setPageNumberList(final List<String> pageNumberList) {
            data.register("pageNumberList", pageNumberList);
            super.setPageNumberList(pageNumberList);
        }

        @Override
        public void setPartialResults(final boolean partialResults) {
            data.register("partialResults", partialResults);
            super.setPartialResults(partialResults);
        }

        @Override
        public void setQueryTime(final long queryTime) {
            data.register("queryTime", queryTime);
            super.setQueryTime(queryTime);
        }

        @Override
        public void setSearchQuery(final String searchQuery) {
            data.register("searchQuery", searchQuery);
            super.setSearchQuery(searchQuery);
        }
    }
}
