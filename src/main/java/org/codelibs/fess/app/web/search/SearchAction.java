/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

import static org.codelibs.core.stream.StreamUtil.stream;

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
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.app.web.base.SearchForm;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.util.RenderDataUtil;
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
        if (viewHelper.isUseSession()) {
            LaRequestUtil.getOptionalRequest().ifPresent(request -> {
                final HttpSession session = request.getSession(false);
                if (session != null) {
                    session.setAttribute(Constants.RESULTS_PER_PAGE, form.num);
                }
            });
        }
        return doSearch(form);
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
        validate(form, messages -> {}, () -> asHtml(path_SearchJsp));
        if (isLoginRequired()) {
            return redirectToLogin();
        }

        if (viewHelper.isUseSession()) {
            final HttpSession session = request.getSession(false);
            if (session != null) {
                final Object resultsPerPage = session.getAttribute(Constants.RESULTS_PER_PAGE);
                if (resultsPerPage instanceof Integer) {
                    form.num = (Integer) resultsPerPage;
                }
            }
        }

        if (StringUtil.isBlank(form.q) && form.fields.isEmpty()) {
            // redirect to index page
            form.q = null;
            return redirectToRoot();
        }

        try {
            buildFormParams(form);
            form.lang = searchService.getLanguages(request, form);
            request.setAttribute(Constants.REQUEST_LANGUAGES, form.lang);
            request.setAttribute(Constants.REQUEST_QUERIES, form.q);
            final WebRenderData renderData = new WebRenderData();
            searchService.search(form, renderData, getUserBean());
            return asHtml(path_SearchJsp).renderWith(data -> {
                renderData.register(data);
                // favorite or thumbnail
                    if (favoriteSupport || thumbnailSupport) {
                        final String queryId = renderData.getQueryId();
                        final List<Map<String, Object>> documentItems = renderData.getDocumentItems();
                        userInfoHelper.storeQueryId(queryId, documentItems);
                        if (thumbnailSupport) {
                            thumbnailManager.storeRequest(queryId, documentItems);
                        }
                    }
                    RenderDataUtil.register(data, "displayQuery",
                            getDisplayQuery(form, labelTypeHelper.getLabelTypeItemList(SearchRequestType.SEARCH)));
                    createPagingQuery(form);
                });
        } catch (final InvalidQueryException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            saveError(e.getMessageCode());
            return redirectToRoot();
        } catch (final ResultOffsetExceededException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage(), e);
            }
            saveError(messages -> {
                messages.addErrorsResultSizeExceeded(GLOBAL);
            });
            return redirectToRoot();
        }
    }

    protected HtmlResponse doMove(final SearchForm form, final int move) {
        int start = fessConfig.getPagingSearchPageStartAsInteger();
        if (form.pn != null) {
            int pageNumber = form.pn;
            if (pageNumber > 0) {
                pageNumber = pageNumber + move;
                if (pageNumber < 1) {
                    pageNumber = 1;
                }
                start = (pageNumber - 1) * form.getPageSize();
            }
        }
        form.start = start;

        return doSearch(form);
    }

    protected String getDisplayQuery(final SearchForm form, final List<Map<String, String>> labelTypeItems) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append(form.q);
        if (!form.fields.isEmpty() && form.fields.containsKey(LABEL_FIELD)) {
            final String[] values = form.fields.get(LABEL_FIELD);
            final List<String> labelList = new ArrayList<>();
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

    protected void createPagingQuery(final SearchForm form) {
        final List<String> pagingQueryList = new ArrayList<>();
        if (form.ex_q != null) {
            stream(form.ex_q).of(
                    stream -> stream.filter(StringUtil::isNotBlank).distinct()
                            .forEach(q -> pagingQueryList.add("ex_q=" + LaFunctions.u(q))));
        }
        if (StringUtil.isNotBlank(form.sort)) {
            pagingQueryList.add("sort=" + LaFunctions.u(form.sort));
        }
        if (form.lang != null) {
            final Set<String> langSet = new HashSet<>();
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
                    pagingQueryList.add("&lang=" + LaFunctions.u(lang));
                }
            }
        }
        if (!form.fields.isEmpty()) {
            for (final Map.Entry<String, String[]> entry : form.fields.entrySet()) {
                final String[] values = entry.getValue();
                if (values != null) {
                    for (final String v : values) {
                        if (StringUtil.isNotBlank(v)) {
                            pagingQueryList.add("fields." + LaFunctions.u(entry.getKey()) + "=" + LaFunctions.u(v));
                        }
                    }
                }
            }
        }
        request.setAttribute(Constants.PAGING_QUERY_LIST, pagingQueryList);
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