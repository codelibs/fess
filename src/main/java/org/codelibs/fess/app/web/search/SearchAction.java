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
package org.codelibs.fess.app.web.search;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessSearchAction;
import org.codelibs.fess.app.web.base.SearchForm;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.exception.InvalidQueryException;
import org.codelibs.fess.exception.ResultOffsetExceededException;
import org.codelibs.fess.helper.RelatedContentHelper;
import org.codelibs.fess.helper.RelatedQueryHelper;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.taglib.function.LaFunctions;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.util.LaRequestUtil;

public class SearchAction extends FessSearchAction {

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LogManager.getLogger(SearchAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //

    @Resource
    protected RelatedContentHelper relatedContentHelper;

    @Resource
    protected RelatedQueryHelper relatedQueryHelper;

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
    public HtmlResponse advance(final SearchForm form) {
        if (isLoginRequired()) {
            return redirectToLogin();
        }
        validate(form, messages -> {}, () -> asHtml(virtualHost(path_IndexJsp)).renderWith(data -> {
            buildInitParams();
            RenderDataUtil.register(data, "notification", fessConfig.getNotificationSearchTop());
        }));
        if (!form.hasConditionQuery()) {
            if (StringUtil.isNotBlank(form.q)) {
                form.as.put("q", new String[] { form.q });
            } else {
                // TODO set default?
            }
        }
        return asHtml(virtualHost(path_AdvanceJsp)).renderWith(data -> {
            buildInitParams();
            RenderDataUtil.register(data, "notification", fessConfig.getNotificationAdvanceSearch());
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        if (viewHelper.isUseSession()) {
            LaRequestUtil.getOptionalRequest().ifPresent(request -> {
                final HttpSession session = request.getSession(false);
                if (session != null && form.num != null) {
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
        validate(form, messages -> {}, () -> asHtml(virtualHost(path_SearchJsp)));
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

        if (StringUtil.isBlank(form.q) && form.fields.isEmpty() && !form.hasConditionQuery()) {
            // redirect to index page
            form.q = null;
            return redirectToRoot();
        }

        try {
            buildFormParams(form);
            form.lang = searchHelper.getLanguages(request, form);
            final WebRenderData renderData = new WebRenderData();
            searchHelper.search(form, renderData, getUserBean());
            return asHtml(virtualHost(path_SearchJsp)).renderWith(data -> {
                if (form.hasConditionQuery()) {
                    form.q = renderData.getSearchQuery();
                }
                renderData.register(data);
                RenderDataUtil.register(data, "displayQuery", getDisplayQuery(form, labelTypeHelper
                        .getLabelTypeItemList(SearchRequestType.SEARCH, request.getLocale() == null ? Locale.ROOT : request.getLocale())));
                createPagingQuery(form);
                final String[] relatedContents = relatedContentHelper.getRelatedContents(form.getQuery());
                RenderDataUtil.register(data, "relatedContents", relatedContents);
                final String[] relatedQueries = relatedQueryHelper.getRelatedQueries(form.getQuery());
                if (relatedQueries.length > 0) {
                    RenderDataUtil.register(data, "relatedQueries", relatedQueries);
                }
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
                Collections.addAll(labelList, values);
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
            stream(form.ex_q).of(stream -> stream.filter(StringUtil::isNotBlank).distinct()
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
        form.fields.entrySet().stream().filter(e -> e.getValue() != null).forEach(e -> {
            final String key = LaFunctions.u(e.getKey());
            stream(e.getValue()).of(stream -> stream.filter(StringUtil::isNotBlank)
                    .forEach(s -> pagingQueryList.add("fields." + key + "=" + LaFunctions.u(s))));
        });
        form.as.entrySet().stream().filter(e -> e.getValue() != null).forEach(e -> {
            final String key = LaFunctions.u(e.getKey());
            stream(e.getValue()).of(stream -> stream.filter(StringUtil::isNotBlank)
                    .forEach(s -> pagingQueryList.add("as." + key + "=" + LaFunctions.u(s))));
        });
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
            RenderDataUtil.register(data, "allRecordCountRelation", allRecordCountRelation);
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