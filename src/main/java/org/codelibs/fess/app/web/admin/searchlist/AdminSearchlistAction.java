/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Token;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.client.FessEsClient.SearchConditionBuilder;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.QueryResponseList;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.lastaflute.taglib.function.LaFunctions;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.util.LaRequestUtil;

/**
 * @author codelibs
 * @author Keiichi Watanabe
 */
public class AdminSearchlistAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
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
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);

        runtime.registerData("helpLink", systemHelper.getHelpLink("searchList"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
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

        final int offset = Integer.parseInt(form.start);
        final int size = Integer.parseInt(form.num);

        try {
            documentItems =
                    fessEsClient.getDocumentList(fieldHelper.docIndex, fieldHelper.docType, searchRequestBuilder -> {
                        return SearchConditionBuilder.builder(searchRequestBuilder).administrativeAccess().offset(offset).size(size)
                                .responseFields(queryHelper.getResponseFields()).build();
                    });
        } catch (final InvalidQueryException e) {
            // TODO Log
        } catch (final ResultOffsetExceededException e) {
            // TODO Log
        }
        final QueryResponseList queryResponseList = (QueryResponseList) documentItems;
        final NumberFormat nf = NumberFormat.getInstance(LaRequestUtil.getRequest().getLocale());
        nf.setMaximumIntegerDigits(2);
        nf.setMaximumFractionDigits(2);
        try {
            nf.format((double) queryResponseList.getExecTime() / 1000);
        } catch (final Exception e) {}

        copyBeanToBean(documentItems, this, option -> option.include("pageSize", "currentPageNumber", "allRecordCount", "allPageCount",
                "existNextPage", "existPrevPage", "currentStartRecordNumber", "currentEndRecordNumber", "pageNumberList"));
    }

    //@Execute(validator = false)
    public HtmlResponse search(final SearchListForm form) {
        return doSearch(form);
    }

    //@Execute(validator = false)
    public HtmlResponse prev(final SearchListForm form) {
        return doMove(form, -1);
    }

    //@Execute(validator = false)
    public HtmlResponse next(final SearchListForm form) {
        return doMove(form, 1);
    }

    //@Execute(validator = false)
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
    //                                               Confirm
    //                                               -------
    @Execute
    public HtmlResponse confirmDelete(final SearchListForm form) {
        return asHtml(path_AdminSearchlist_ConfirmDeleteJsp);
    }

    @Token(save = false, validate = true)
    //@Execute(validator = true, input = "index")
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

}
