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
package org.codelibs.fess.app.web.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.net.URLUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.sso.SsoAction;
import org.codelibs.fess.entity.SearchRequestParams.SearchRequestType;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.OsddHelper;
import org.codelibs.fess.helper.PopularWordHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.RoleQueryHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.thumbnail.ThumbnailManager;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.login.LoginManager;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.next.HtmlNext;
import org.lastaflute.web.ruts.process.ActionRuntime;

public abstract class FessSearchAction extends FessBaseAction {

    protected static final String LABEL_FIELD = "label";

    @Resource
    protected SearchHelper searchHelper;

    @Resource
    protected ThumbnailManager thumbnailManager;

    @Resource
    protected LabelTypeHelper labelTypeHelper;

    @Resource
    protected QueryHelper queryHelper;

    @Resource
    protected QueryFieldConfig queryFieldConfig;

    @Resource
    protected RoleQueryHelper roleQueryHelper;

    @Resource
    protected UserInfoHelper userInfoHelper;

    @Resource
    protected OsddHelper osddHelper;

    @Resource
    protected PopularWordHelper popularWordHelper;

    @Resource
    protected HttpServletRequest request;

    protected boolean searchLogSupport;

    protected boolean favoriteSupport;

    protected boolean thumbnailSupport;

    @Override
    public ActionResponse hookBefore(final ActionRuntime runtime) { // application may override
        searchLogSupport = fessConfig.isSearchLog();
        favoriteSupport = fessConfig.isUserFavorite();
        thumbnailSupport = fessConfig.isThumbnailEnabled();
        runtime.registerData("searchLogSupport", searchLogSupport);
        runtime.registerData("favoriteSupport", favoriteSupport);
        runtime.registerData("thumbnailSupport", thumbnailSupport);
        if (fessConfig.isWebApiPopularWord()) {
            final List<String> tagList = new ArrayList<>();
            final String key = ComponentUtil.getVirtualHostHelper().getVirtualHostKey();
            if (StringUtil.isNotBlank(key)) {
                tagList.add(key);
            }
            runtime.registerData("popularWords", popularWordHelper.getWordList(SearchRequestType.SEARCH, null,
                    tagList.toArray(new String[tagList.size()]), null, null, null));
        }
        return super.hookBefore(runtime);
    }

    @Override
    protected OptionalThing<LoginManager> myLoginManager() {
        return OptionalThing.empty();
    }

    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        systemHelper.setupSearchHtmlData(this, runtime);

        runtime.registerData("osddLink", osddHelper.hasOpenSearchFile());
        runtime.registerData("clipboardCopyIcon", fessConfig.isClipboardCopyIconEnabled());

        final List<Map<String, String>> labelTypeItems = labelTypeHelper.getLabelTypeItemList(SearchRequestType.SEARCH,
                request.getLocale() == null ? Locale.ROOT : request.getLocale());
        runtime.registerData("labelTypeItems", labelTypeItems);
        runtime.registerData("displayLabelTypeItems", labelTypeItems != null && !labelTypeItems.isEmpty());

        Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        runtime.registerData("langItems", systemHelper.getLanguageItems(locale));
        final String username = systemHelper.getUsername();
        runtime.registerData("username", username);
        runtime.registerData("editableUser", fessLoginAssist.getSavedUserBean().map(FessUserBean::isEditable).orElse(false));
        runtime.registerData("adminUser", fessConfig.isAdminUser(username) || fessLoginAssist.getSavedUserBean()
                .map(user -> user.hasRoles(fessConfig.getAuthenticationAdminRolesAsArray())).orElse(false));

        runtime.registerData("pageLoginLink", fessConfig.isLoginLinkEnabled());
    }

    // ===================================================================================
    //                                                                             Helpers
    //                                                                           =========
    protected boolean isLoginRequired() {
        if (fessConfig.isLoginRequired() && !fessLoginAssist.getSavedUserBean().isPresent()) {
            return true;
        }
        return false;
    }

    protected void buildFormParams(final SearchForm form) {

        final HttpSession session = request.getSession(false);
        if (session != null) {
            final Object resultsPerPage = session.getAttribute(Constants.RESULTS_PER_PAGE);
            if (resultsPerPage instanceof Integer) {
                form.num = (Integer) resultsPerPage;
            }
        }

        // label
        final List<Map<String, String>> labelTypeItems = labelTypeHelper.getLabelTypeItemList(SearchRequestType.SEARCH,
                request.getLocale() == null ? Locale.ROOT : request.getLocale());

        if (!labelTypeItems.isEmpty() && !form.fields.containsKey(FessSearchAction.LABEL_FIELD)) {
            final String[] defaultLabelValues = fessConfig.getDefaultLabelValues(getUserBean());
            if (defaultLabelValues.length > 0) {
                form.fields.put(FessSearchAction.LABEL_FIELD, defaultLabelValues);
            }
        }

        final Map<String, String> labelMap = new LinkedHashMap<>();
        if (!labelTypeItems.isEmpty()) {
            for (final Map<String, String> map : labelTypeItems) {
                labelMap.put(map.get(Constants.ITEM_VALUE), map.get(Constants.ITEM_LABEL));
            }
        }
        request.setAttribute(Constants.LABEL_VALUE_MAP, labelMap);

        // sort
        if (StringUtil.isBlank(form.sort)) {
            final String[] defaultSortValues = fessConfig.getDefaultSortValues(getUserBean());
            if (defaultSortValues.length == 1) {
                form.sort = defaultSortValues[0];
            } else if (defaultSortValues.length >= 2) {
                final StringBuilder sortValueSb = new StringBuilder();
                final Set<String> sortFieldNames = new HashSet<>();
                for (final String defaultSortValue : defaultSortValues) {
                    for (final String singleValue : defaultSortValue.split(",")) {
                        final String sortFieldName = singleValue.split("\\.")[0];
                        if (!sortFieldNames.contains(sortFieldName)) {
                            sortFieldNames.add(sortFieldName);
                            if (sortValueSb.length() > 0) {
                                sortValueSb.append(",");
                            }
                            sortValueSb.append(singleValue);
                        }
                    }
                }
                form.sort = sortValueSb.toString();
            }
        }
    }

    protected void buildInitParams() {
        buildInitParamMap(viewHelper.getInitFacetParamMap(), Constants.FACET_QUERY, Constants.FACET_FORM);
        buildInitParamMap(viewHelper.getInitGeoParamMap(), Constants.GEO_QUERY, Constants.GEO_FORM);
    }

    protected void buildInitParamMap(final Map<String, String> paramMap, final String queryKey, final String formKey) {
        if (!paramMap.isEmpty()) {
            final StringBuilder queryBuf = new StringBuilder(100);
            final StringBuilder formBuf = new StringBuilder(100);
            for (final Map.Entry<String, String> entry : paramMap.entrySet()) {
                queryBuf.append('&');
                queryBuf.append(URLUtil.encode(entry.getValue(), Constants.UTF_8));
                queryBuf.append('=');
                queryBuf.append(URLUtil.encode(entry.getKey(), Constants.UTF_8));
                formBuf.append("<input type=\"hidden\" name=\"");
                formBuf.append(StringEscapeUtils.escapeHtml4(entry.getValue()));
                formBuf.append("\" value=\"");
                formBuf.append(StringEscapeUtils.escapeHtml4(entry.getKey()));
                formBuf.append("\"/>");
            }
            request.setAttribute(queryKey, queryBuf.toString());
            request.setAttribute(formKey, formBuf.toString());
        }
    }

    protected HtmlResponse redirectToLogin() {
        return systemHelper.getRedirectResponseToLogin(redirect(SsoAction.class));
    }

    protected HtmlResponse redirectToRoot() {
        return systemHelper.getRedirectResponseToRoot(newHtmlResponseAsRedirect("/"));
    }

    protected HtmlNext virtualHost(final HtmlNext path) {
        return ComponentUtil.getVirtualHostHelper().getVirtualHostPath(path);
    }
}
