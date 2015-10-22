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

package org.codelibs.fess.app.web.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.SearchService;
import org.codelibs.fess.app.web.RootAction;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.util.FacetResponse;
import org.lastaflute.taglib.function.LaFunctions;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.util.LaRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchAction extends FessSearchAction {

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LoggerFactory.getLogger(SearchAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //
    @Resource
    protected SearchService searchService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SearchForm form) {
        return search(form);
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        final HtmlResponse response = doSearch(form);
        if (viewHelper.isUseSession()) {
            LaRequestUtil.getOptionalRequest().ifPresent(request -> {
                final HttpSession session = request.getSession(false);
                if (session != null) {
                    session.setAttribute(Constants.RESULTS_PER_PAGE, form.num);
                }
            });
        }
        return response;
    }

    @Execute
    public HtmlResponse prev(final SearchForm form) {
        return doMove(form, -1);
    }

    @Execute
    public HtmlResponse next(final SearchForm form) {
        return doMove(form, 1);
    }

    @Execute
    public HtmlResponse move(final SearchForm form) {
        return doMove(form, 0);
    }

    protected HtmlResponse doSearch(final SearchForm form) {
        searchAvailable();

        if (viewHelper.isUseSession()) {
            final HttpSession session = request.getSession(false);
            if (session != null) {
                final Object resultsPerPage = session.getAttribute(Constants.RESULTS_PER_PAGE);
                if (resultsPerPage != null) {
                    form.num = resultsPerPage.toString();
                }
            }
        }

        if (StringUtil.isBlank(form.query)) {
            try {
                final String optionQuery = queryHelper.buildOptionQuery(form.options);
                form.query = optionQuery;
            } catch (final InvalidQueryException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug(e.getMessage(), e);
                }
                throwValidationError(e.getMessageCode(), () -> asHtml(path_ErrorJsp));
            }
        }

        if (StringUtil.isBlank(form.query) && form.fields.isEmpty()) {
            // redirect to index page
            form.query = null;
            return redirect(RootAction.class);
        }

        return asHtml(path_SearchJsp).renderWith(data -> {
            updateSearchParams(form);
            buildLabelParams(form.fields);
            form.lang = searchService.getLanguages(request, form);
            try {
                final WebRenderData renderData = new WebRenderData(data);
                searchService.search(request, form, renderData);
                form.rt = Long.toString(renderData.getRequestedTime());
                // favorite or screenshot
                if (favoriteSupport || screenShotManager != null) {
                    final String searchQuery = renderData.getSearchQuery();
                    final List<Map<String, Object>> documentItems = renderData.getDocumentItems();
                    form.queryId = userInfoHelper.generateQueryId(searchQuery, documentItems);
                    if (screenShotManager != null) {
                        screenShotManager.storeRequest(form.queryId, documentItems);
                        data.register("screenShotSupport", true);
                    }
                }
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
            data.register("displayQuery", getDisplayQuery(form, labelTypeHelper.getLabelTypeItemList()));
            data.register("pagingQuery", getPagingQuery(form));
        });
    }

    protected HtmlResponse doMove(final SearchForm form, final int move) {
        int start = queryHelper.getDefaultStart();
        if (StringUtil.isNotBlank(form.pn)) {
            try {
                int pageNumber = Integer.parseInt(form.pn);
                if (pageNumber > 0) {
                    pageNumber = pageNumber + move;
                    if (pageNumber < 1) {
                        pageNumber = 1;
                    }
                    start = (pageNumber - 1) * form.getPageSize();
                }
            } catch (final NumberFormatException e) {
                // ignore
            }
        }
        form.start = String.valueOf(start);

        return doSearch(form);
    }

    protected void updateSearchParams(final SearchForm form) {
        if (form.facet == null) {
            form.facet = queryHelper.getDefaultFacetInfo();
        }

        if (form.geo == null) {
            form.geo = queryHelper.getDefaultGeoInfo();
        }
    }

    protected String getDisplayQuery(final SearchForm form, final List<Map<String, String>> labelTypeItems) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append(form.query);
        if (!form.fields.isEmpty() && form.fields.containsKey(LABEL_FIELD)) {
            final String[] values = form.fields.get(LABEL_FIELD);
            final List<String> labelList = new ArrayList<String>();
            if (values != null) {
                for (final String v : values) {
                    labelList.add(v);
                }
            }
            for (final String labelTypeValue : labelList) {
                for (final Map<String, String> map : labelTypeItems) {
                    if (map.get(Constants.ITEM_VALUE).equals(labelTypeValue)) {
                        buf.append(' ');
                        buf.append(map.get(Constants.ITEM_LABEL));
                        break;
                    }
                }
            }
        }
        return buf.toString();
    }

    protected String getPagingQuery(final SearchForm form) {
        final StringBuilder buf = new StringBuilder(200);
        if (form.additional != null) {
            searchService.appendAdditionalQuery(form.additional, additional -> {
                buf.append("&additional=").append(LaFunctions.u(additional));
            });
        }
        if (StringUtil.isNotBlank(form.sort)) {
            buf.append("&sort=").append(LaFunctions.u(form.sort));
        }
        if (StringUtil.isNotBlank(form.op)) {
            buf.append("&op=").append(LaFunctions.u(form.op));
        }
        if (form.lang != null) {
            final Set<String> langSet = new HashSet<String>();
            for (final String lang : form.lang) {
                if (StringUtil.isNotBlank(lang) && lang.length() < 1000) {
                    if (Constants.ALL_LANGUAGES.equals(lang)) {
                        langSet.clear();
                        break;
                    }
                    final String normalizeLang = systemHelper.normalizeLang(lang);
                    if (normalizeLang != null) {
                        langSet.add(normalizeLang);
                    }
                }
            }
            if (!langSet.isEmpty()) {
                for (final String lang : langSet) {
                    buf.append("&lang=").append(LaFunctions.u(lang));
                }
            }
        }
        if (!form.fields.isEmpty()) {
            for (final Map.Entry<String, String[]> entry : form.fields.entrySet()) {
                final String[] values = entry.getValue();
                if (values != null) {
                    for (final String v : values) {
                        if (StringUtil.isNotBlank(v)) {
                            buf.append("&fields.").append(LaFunctions.u(entry.getKey())).append('=').append(LaFunctions.u(v));
                        }
                    }
                }
            }
        }

        return buf.toString();
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
        public void setFacetResponse(final FacetResponse facetResponse) {
            data.register("facetResponse", facetResponse);
            super.setFacetResponse(facetResponse);
        }

        @Override
        public void setAppendHighlightParams(final String appendHighlightParams) {
            data.register("appendHighlightParams", appendHighlightParams);
            super.setAppendHighlightParams(appendHighlightParams);
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