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
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author codelibs
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
    protected FieldHelper fieldHelper;

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

        runtime.registerData("helpLink", systemHelper.getHelpLink("searchList"));
    }

    // ===================================================================================
    // Search Execute
    // ==============
    @Execute
    public HtmlResponse index(final SearchListForm form) {
        return asHtml(path_AdminSearchlist_IndexJsp);
    }

    protected HtmlResponse doSearch(final SearchListForm form) {

        if (StringUtil.isBlank(form.query)) {
            // redirect to index page
            form.query = null;
            return redirect(getClass());
        }
        return asHtml(path_AdminSearchlist_IndexJsp).renderWith(data -> {
            doSearchInternal(data, form);
        });
    }

    private void doSearchInternal(final RenderData data, final SearchListForm form) {
        // init pager
        if (StringUtil.isBlank(form.start)) {
            form.start = String.valueOf(Constants.DEFAULT_START_COUNT);
        } else {
            try {
                Long.parseLong(form.start);
            } catch (final NumberFormatException e) {
                form.start = String.valueOf(Constants.DEFAULT_START_COUNT);
            }
        }
        if (StringUtil.isBlank(form.num)) {
            form.num = String.valueOf(Constants.DEFAULT_PAGE_SIZE);
        } else {
            try {
                final int num = Integer.parseInt(form.num);
                if (num > 100) {
                    // max page size
                    form.num = "100";
                }
            } catch (final NumberFormatException e) {
                form.num = String.valueOf(Constants.DEFAULT_PAGE_SIZE);
            }
        }

        try {
            final WebRenderData renderData = new WebRenderData(data);
            searchService.search(request, form, renderData);
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throwValidationError(e.getMessageCode(), () -> asHtml(path_ErrorJsp));
        } catch (final ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throwValidationError(messages -> {
                messages.addErrorsResultSizeExceeded(GLOBAL);
            }, () -> asHtml(path_ErrorJsp));
        }
    }

    @Execute
    public HtmlResponse search(final SearchListForm form) {
        return doSearch(form);
    }

    @Execute
    public HtmlResponse prev(final SearchListForm form) {
        return doMove(form, -1);
    }

    @Execute
    public HtmlResponse next(final SearchListForm form) {
        return doMove(form, 1);
    }

    @Execute
    public HtmlResponse move(final SearchListForm form) {
        return doMove(form, 0);
    }

    protected HtmlResponse doMove(final SearchListForm form, final int move) {
        int size = Constants.DEFAULT_PAGE_SIZE;
        if (StringUtil.isBlank(form.num)) {
            form.num = String.valueOf(Constants.DEFAULT_PAGE_SIZE);
        } else {
            try {
                size = Integer.parseInt(form.num);
            } catch (final NumberFormatException e) {
                form.num = String.valueOf(Constants.DEFAULT_PAGE_SIZE);
            }
        }

        if (StringUtil.isBlank(form.pn)) {
            form.start = String.valueOf(Constants.DEFAULT_START_COUNT);
        } else {
            Integer pageNumber = Integer.parseInt(form.pn);
            if (pageNumber != null && pageNumber > 0) {
                pageNumber = pageNumber + move;
                if (pageNumber < 1) {
                    pageNumber = 1;
                }
                form.start = String.valueOf((pageNumber - 1) * size);
            } else {
                form.start = String.valueOf(Constants.DEFAULT_START_COUNT);
            }
        }

        return doSearch(form);
    }

    // -----------------------------------------------------
    // Confirm
    // -------
    @Execute
    public HtmlResponse confirmDelete(final SearchListForm form) {
        return asHtml(path_AdminSearchlist_ConfirmDeleteJsp);
    }

    @Execute
    public HtmlResponse delete(final SearchListForm form) {
        return deleteByQuery(form);
    }

    private HtmlResponse deleteByQuery(final SearchListForm form) {
        final String docId = form.docId;
        if (jobHelper.isCrawlProcessRunning()) {
            // TODO Error
        }
        final Thread thread = new Thread(() -> {
            if (!jobHelper.isCrawlProcessRunning()) {
                System.currentTimeMillis();
                try {
                    final QueryBuilder query = QueryBuilders.termQuery(fieldHelper.docIdField, docId);
                    fessEsClient.deleteByQuery(fieldHelper.docIndex, fieldHelper.docType, query);
                } catch (final Exception e) {
                    // TODO Log
            }
        }
    }   );
        thread.start();
        saveInfo(messages -> messages.addSuccessDeleteSolrIndex(GLOBAL));
        return redirectWith(getClass(), moreUrl("search").params("query", form.query));
    }

    public boolean isSolrProcessRunning() {
        return jobHelper.isCrawlProcessRunning();
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
