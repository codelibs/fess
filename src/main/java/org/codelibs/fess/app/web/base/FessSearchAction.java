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
package org.codelibs.fess.app.web.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.core.net.URLUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.SSCConstants;
import org.codelibs.fess.UnsupportedSearchException;
import org.codelibs.fess.client.FessEsClient;
import org.codelibs.fess.entity.LoginInfo;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.OpenSearchHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.RoleQueryHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.screenshot.ScreenShotManager;
import org.codelibs.fess.util.SearchParamMap;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.util.LaRequestUtil;

public abstract class FessSearchAction extends FessBaseAction {

    protected static final String LABEL_FIELD = "label";

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    protected ScreenShotManager screenShotManager;

    @Resource
    protected LabelTypeHelper labelTypeHelper;

    @Resource
    protected ViewHelper viewHelper;

    @Resource
    protected QueryHelper queryHelper;

    @Resource
    protected RoleQueryHelper roleQueryHelper;

    @Resource
    protected UserInfoHelper userInfoHelper;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected FieldHelper fieldHelper;

    @Resource
    protected OpenSearchHelper openSearchHelper;

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected HttpServletRequest request;

    protected boolean searchLogSupport;

    protected boolean favoriteSupport;

    @Override
    public ActionResponse hookBefore(ActionRuntime runtime) { // application may override
        searchLogSupport = Constants.TRUE.equals(crawlerProperties.getProperty(Constants.SEARCH_LOG_PROPERTY, Constants.TRUE));
        favoriteSupport = Constants.TRUE.equals(crawlerProperties.getProperty(Constants.USER_FAVORITE_PROPERTY, Constants.FALSE));
        runtime.registerData("searchLogSupport", searchLogSupport);
        runtime.registerData("favoriteSupport", favoriteSupport);
        return super.hookBefore(runtime);
    }

    @Override
    protected void setupHtmlData(ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("osddLink", openSearchHelper.hasOpenSearchFile());
        runtime.registerData("helpPage", viewHelper.getPagePath("common/help"));
    }

    protected void searchAvailable() {
        final String supportedSearch =
                crawlerProperties.getProperty(Constants.SUPPORTED_SEARCH_FEATURE_PROPERTY, Constants.SUPPORTED_SEARCH_WEB);
        if (Constants.SUPPORTED_SEARCH_NONE.equals(supportedSearch)) {
            throw new UnsupportedSearchException("A search is not supported: " + LaRequestUtil.getRequest().getRequestURL());
        }
    }

    protected List<Map<String, String>> buildLabelParams(RenderData data, SearchParamMap fields) {
        // label
        List<Map<String, String>> labelTypeItems = labelTypeHelper.getLabelTypeItemList();
        data.register("labelTypeItems", labelTypeItems);
        data.register("displayLabelTypeItems", labelTypeItems != null && !labelTypeItems.isEmpty());

        if (!labelTypeItems.isEmpty() && !fields.containsKey(FessSearchAction.LABEL_FIELD)) {
            final String defaultLabelValue = crawlerProperties.getProperty(Constants.DEFAULT_LABEL_VALUE_PROPERTY, StringUtil.EMPTY);
            if (StringUtil.isNotBlank(defaultLabelValue)) {
                final String[] values = defaultLabelValue.split("\n");
                if (values != null && values.length > 0) {
                    final List<String> list = new ArrayList<String>(values.length);
                    for (final String value : values) {
                        if (StringUtil.isNotBlank(value)) {
                            list.add(value);
                        }
                    }
                    if (!list.isEmpty()) {
                        fields.put(FessSearchAction.LABEL_FIELD, list.toArray(new String[list.size()]));
                    }
                }
            }
        }

        final Map<String, String> labelMap = new LinkedHashMap<String, String>();
        if (!labelTypeItems.isEmpty()) {
            for (final Map<String, String> map : labelTypeItems) {
                labelMap.put(map.get(Constants.ITEM_VALUE), map.get(Constants.ITEM_LABEL));
            }
        }
        request.setAttribute(Constants.LABEL_VALUE_MAP, labelMap);

        return labelTypeItems;
    }

    protected void buildUserParams(RenderData data) {

        Locale locale = request.getLocale();
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        data.register("langItems", systemHelper.getLanguageItems(locale));

        final HttpSession session = request.getSession(false);
        if (session != null) {
            final Object obj = session.getAttribute(SSCConstants.USER_INFO);
            if (obj instanceof LoginInfo) {
                final LoginInfo loginInfo = (LoginInfo) obj;
                data.register("username", loginInfo.getUsername());
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

}
