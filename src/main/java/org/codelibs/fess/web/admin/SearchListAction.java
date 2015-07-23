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

package org.codelibs.fess.web.admin;

import java.beans.Beans;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.InvalidQueryException;
import org.codelibs.fess.ResultOffsetExceededException;
import org.codelibs.fess.client.FessEsClient;
import org.codelibs.fess.client.FessEsClient.SearchConditionBuilder;
import org.codelibs.fess.crud.util.SAStrutsUtil;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.QueryResponseList;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.lastaflute.web.Execute;
import org.lastaflute.web.util.LaRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchListAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(SearchListAction.class);

    private static final long serialVersionUID = 1L;

    @ActionForm
    @Resource
    protected SearchListForm searchListForm;

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected HttpServletRequest request;

    @Resource
    protected SystemHelper systemHelper;

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

    public String getHelpLink() {
        return systemHelper.getHelpLink("searchList");
    }

    @Execute(validator = false)
    public String index() {
        return "index.jsp";
    }

    protected String doSearch() {

        if (StringUtil.isBlank(searchListForm.query)) {
            // redirect to index page
            searchListForm.query = null;
            return "index?redirect=true";
        }

        doSearchInternal();

        return "index.jsp";
    }

    private String doSearchInternal() {
        final String query = searchListForm.query;

        // init pager
        if (StringUtil.isBlank(searchListForm.start)) {
            searchListForm.start = String.valueOf(Constants.DEFAULT_START_COUNT);
        } else {
            try {
                Long.parseLong(searchListForm.start);
            } catch (final NumberFormatException e) {
                searchListForm.start = String.valueOf(Constants.DEFAULT_START_COUNT);
            }
        }
        if (StringUtil.isBlank(searchListForm.num)) {
            searchListForm.num = String.valueOf(Constants.DEFAULT_PAGE_SIZE);
        } else {
            try {
                final int num = Integer.parseInt(searchListForm.num);
                if (num > 100) {
                    // max page size
                    searchListForm.num = "100";
                }
            } catch (final NumberFormatException e) {
                searchListForm.num = String.valueOf(Constants.DEFAULT_PAGE_SIZE);
            }
        }

        final int offset = Integer.parseInt(searchListForm.start);
        final int size = Integer.parseInt(searchListForm.num);
        try {
            documentItems =
                    fessEsClient.getDocumentList(fieldHelper.docIndex, fieldHelper.docType, searchRequestBuilder -> {
                        return SearchConditionBuilder.builder(searchRequestBuilder).administrativeAccess().offset(offset).size(size)
                                .responseFields(queryHelper.getResponseFields()).build();
                    });
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throw new SSCActionMessagesException(e, e.getMessageCode());
        } catch (final ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            throw new SSCActionMessagesException(e, "errors.result_size_exceeded");
        }
        final QueryResponseList queryResponseList = (QueryResponseList) documentItems;
        final NumberFormat nf = NumberFormat.getInstance(LaRequestUtil.getRequest().getLocale());
        nf.setMaximumIntegerDigits(2);
        nf.setMaximumFractionDigits(2);
        try {
            execTime = nf.format((double) queryResponseList.getExecTime() / 1000);
        } catch (final Exception e) {}

        Beans.copy(documentItems, this)
                .includes("pageSize", "currentPageNumber", "allRecordCount", "allPageCount", "existNextPage", "existPrevPage",
                        "currentStartRecordNumber", "currentEndRecordNumber", "pageNumberList").execute();

        return query;
    }

    @Execute(validator = false)
    public String search() {
        return doSearch();
    }

    @Execute(validator = false)
    public String prev() {
        return doMove(-1);
    }

    @Execute(validator = false)
    public String next() {
        return doMove(1);
    }

    @Execute(validator = false)
    public String move() {
        return doMove(0);
    }

    protected String doMove(final int move) {
        int size = Constants.DEFAULT_PAGE_SIZE;
        if (StringUtil.isBlank(searchListForm.num)) {
            searchListForm.num = String.valueOf(Constants.DEFAULT_PAGE_SIZE);
        } else {
            try {
                size = Integer.parseInt(searchListForm.num);
            } catch (final NumberFormatException e) {
                searchListForm.num = String.valueOf(Constants.DEFAULT_PAGE_SIZE);
            }
        }

        if (StringUtil.isBlank(searchListForm.pn)) {
            searchListForm.start = String.valueOf(Constants.DEFAULT_START_COUNT);
        } else {
            Integer pageNumber = Integer.parseInt(searchListForm.pn);
            if (pageNumber != null && pageNumber > 0) {
                pageNumber = pageNumber + move;
                if (pageNumber < 1) {
                    pageNumber = 1;
                }
                searchListForm.start = String.valueOf((pageNumber - 1) * size);
            } else {
                searchListForm.start = String.valueOf(Constants.DEFAULT_START_COUNT);
            }
        }

        return doSearch();
    }

    @Token(save = true, validate = false)
    @Execute(validator = true, input = "index")
    public String confirmDelete() {
        return "confirmDelete.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index")
    public String delete() {
        return deleteByQuery(searchListForm.docId);
    }

    private String deleteByQuery(final String docId) {
        if (jobHelper.isCrawlProcessRunning()) {
            throw new SSCActionMessagesException("errors.failed_to_start_solr_process_because_of_running");
        }
        final Thread thread = new Thread(() -> {
            if (!jobHelper.isCrawlProcessRunning()) {
                final long time = System.currentTimeMillis();
                try {
                    final QueryBuilder query = QueryBuilders.termQuery(fieldHelper.docIdField, docId);
                    fessEsClient.deleteByQuery(fieldHelper.docIndex, fieldHelper.docType, query);
                    if (logger.isInfoEnabled()) {
                        logger.info("[EXEC TIME] index cleanup time: " + (System.currentTimeMillis() - time) + "ms");
                    }
                } catch (final Exception e) {
                    logger.error("Failed to delete index (query=" + fieldHelper.docIdField + ":" + docId + ").", e);
                }
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("could not start index cleanup process" + " because of running solr process.");
                }
            }
        });
        thread.start();
        SAStrutsUtil.addSessionMessage("success.delete_solr_index");
        return "search?query=" + LaFunctions.u(searchListForm.query) + "&redirect=true";
    }

    public boolean isSolrProcessRunning() {
        return jobHelper.isCrawlProcessRunning();
    }
}