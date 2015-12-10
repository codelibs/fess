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
package org.codelibs.fess.app.web.admin.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.util.LaRequestUtil;

/**
 * @author shinsuke
 * @author Shunji Makino
 */
public class AdminGeneralAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    protected DynamicProperties crawlerProperties;
    @Resource
    protected SystemHelper systemHelper;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameGeneral()));
        runtime.registerData("supportedSearchItems", getSupportedSearchItems());
        runtime.registerData("dayItems", getDayItems());
    }

    // ===================================================================================
    //

    @Execute
    public HtmlResponse index() {
        saveToken();
        return asHtml(path_AdminGeneral_AdminGeneralJsp).useForm(EditForm.class, setup -> {
            setup.setup(form -> {
                updateForm(form);
            });
        });
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_AdminGeneral_AdminGeneralJsp);
        });
        verifyToken(() -> {
            return asHtml(path_AdminGeneral_AdminGeneralJsp);
        });

        updateProperty(Constants.INCREMENTAL_CRAWLING_PROPERTY,
                form.incrementalCrawling != null && Constants.ON.equalsIgnoreCase(form.incrementalCrawling) ? Constants.TRUE
                        : Constants.FALSE);
        updateProperty(Constants.DAY_FOR_CLEANUP_PROPERTY, form.dayForCleanup.toString());
        updateProperty(Constants.CRAWLING_THREAD_COUNT_PROPERTY, form.crawlingThreadCount.toString());
        updateProperty(Constants.SEARCH_LOG_PROPERTY,
                form.searchLog != null && Constants.ON.equalsIgnoreCase(form.searchLog) ? Constants.TRUE : Constants.FALSE);
        updateProperty(Constants.USER_INFO_PROPERTY, form.userInfo != null && Constants.ON.equalsIgnoreCase(form.userInfo) ? Constants.TRUE
                : Constants.FALSE);
        updateProperty(Constants.USER_FAVORITE_PROPERTY,
                form.userFavorite != null && Constants.ON.equalsIgnoreCase(form.userFavorite) ? Constants.TRUE : Constants.FALSE);
        updateProperty(Constants.WEB_API_JSON_PROPERTY,
                form.webApiJson != null && Constants.ON.equalsIgnoreCase(form.webApiJson) ? Constants.TRUE : Constants.FALSE);
        updateProperty(Constants.DEFAULT_LABEL_VALUE_PROPERTY, form.defaultLabelValue);
        updateProperty(Constants.APPEND_QUERY_PARAMETER_PROPERTY,
                form.appendQueryParameter != null && Constants.ON.equalsIgnoreCase(form.appendQueryParameter) ? Constants.TRUE
                        : Constants.FALSE);
        updateProperty(Constants.SUPPORTED_SEARCH_FEATURE_PROPERTY, form.supportedSearch);
        updateProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY, form.ignoreFailureType);
        updateProperty(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY, form.failureCountThreshold.toString());
        updateProperty(Constants.WEB_API_HOT_SEARCH_WORD_PROPERTY,
                form.hotSearchWord != null && Constants.ON.equalsIgnoreCase(form.hotSearchWord) ? Constants.TRUE : Constants.FALSE);
        updateProperty(Constants.CSV_FILE_ENCODING_PROPERTY, form.csvFileEncoding);
        updateProperty(Constants.PURGE_SEARCH_LOG_DAY_PROPERTY, form.purgeSearchLogDay.toString());
        updateProperty(Constants.PURGE_JOB_LOG_DAY_PROPERTY, form.purgeJobLogDay.toString());
        updateProperty(Constants.PURGE_USER_INFO_DAY_PROPERTY, form.purgeUserInfoDay.toString());
        updateProperty(Constants.PURGE_BY_BOTS_PROPERTY, form.purgeByBots);
        updateProperty(Constants.NOTIFICATION_TO_PROPERTY, form.notificationTo);
        updateProperty(Constants.SUGGEST_SEARCH_LOG_PROPERTY,
                form.suggestSearchLog != null && Constants.ON.equalsIgnoreCase(form.suggestSearchLog) ? Constants.TRUE : Constants.FALSE);
        updateProperty(Constants.SUGGEST_DOCUMENTS_PROPERTY,
                form.suggestDocuments != null && Constants.ON.equalsIgnoreCase(form.suggestDocuments) ? Constants.TRUE : Constants.FALSE);
        updateProperty(Constants.PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY, form.purgeSuggestSearchLogDay.toString());

        crawlerProperties.store();
        saveInfo(messages -> messages.addSuccessUpdateCrawlerParams(GLOBAL));
        return redirect(getClass());
    }

    protected void updateForm(final EditForm form) {
        form.incrementalCrawling = crawlerProperties.getProperty(Constants.INCREMENTAL_CRAWLING_PROPERTY, Constants.TRUE);
        form.dayForCleanup = getPropertyAsInteger(Constants.DAY_FOR_CLEANUP_PROPERTY, Constants.DEFAULT_DAY_FOR_CLEANUP);
        form.crawlingThreadCount = Integer.parseInt(crawlerProperties.getProperty(Constants.CRAWLING_THREAD_COUNT_PROPERTY, "5"));
        form.searchLog = crawlerProperties.getProperty(Constants.SEARCH_LOG_PROPERTY, Constants.TRUE);
        form.userInfo = crawlerProperties.getProperty(Constants.USER_INFO_PROPERTY, Constants.TRUE);
        form.userFavorite = crawlerProperties.getProperty(Constants.USER_FAVORITE_PROPERTY, Constants.FALSE);
        form.webApiJson = crawlerProperties.getProperty(Constants.WEB_API_JSON_PROPERTY, Constants.TRUE);
        form.defaultLabelValue = crawlerProperties.getProperty(Constants.DEFAULT_LABEL_VALUE_PROPERTY, StringUtil.EMPTY);
        form.appendQueryParameter = crawlerProperties.getProperty(Constants.APPEND_QUERY_PARAMETER_PROPERTY, Constants.FALSE);
        form.supportedSearch = crawlerProperties.getProperty(Constants.SUPPORTED_SEARCH_FEATURE_PROPERTY, Constants.SUPPORTED_SEARCH_WEB);
        form.ignoreFailureType =
                crawlerProperties.getProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY, Constants.DEFAULT_IGNORE_FAILURE_TYPE);
        form.failureCountThreshold = getPropertyAsInteger(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY, Constants.DEFAULT_FAILURE_COUNT);
        form.hotSearchWord = crawlerProperties.getProperty(Constants.WEB_API_HOT_SEARCH_WORD_PROPERTY, Constants.TRUE);
        form.csvFileEncoding = crawlerProperties.getProperty(Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8);
        form.purgeSearchLogDay =
                Integer.parseInt(crawlerProperties.getProperty(Constants.PURGE_SEARCH_LOG_DAY_PROPERTY, Constants.DEFAULT_PURGE_DAY));
        form.purgeJobLogDay =
                Integer.parseInt(crawlerProperties.getProperty(Constants.PURGE_JOB_LOG_DAY_PROPERTY, Constants.DEFAULT_PURGE_DAY));
        form.purgeUserInfoDay =
                Integer.parseInt(crawlerProperties.getProperty(Constants.PURGE_USER_INFO_DAY_PROPERTY, Constants.DEFAULT_PURGE_DAY));
        form.purgeByBots = crawlerProperties.getProperty(Constants.PURGE_BY_BOTS_PROPERTY, Constants.DEFAULT_PURGE_BY_BOTS);
        form.notificationTo = crawlerProperties.getProperty(Constants.NOTIFICATION_TO_PROPERTY, StringUtil.EMPTY);
        form.suggestSearchLog = crawlerProperties.getProperty(Constants.SUGGEST_SEARCH_LOG_PROPERTY, Constants.TRUE);
        form.suggestDocuments = crawlerProperties.getProperty(Constants.SUGGEST_DOCUMENTS_PROPERTY, Constants.TRUE);
        form.purgeSuggestSearchLogDay =
                Integer.parseInt(crawlerProperties
                        .getProperty(Constants.PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY, Constants.DEFAULT_PURGE_DAY));
    }

    private void updateProperty(final String key, final String value) {
        crawlerProperties.setProperty(key, value == null ? StringUtil.EMPTY : value);
    }

    private Integer getPropertyAsInteger(final String key, final int defaultValue) {
        final String value = crawlerProperties.getProperty(Constants.CRAWLING_THREAD_COUNT_PROPERTY);
        if (value != null) {
            try {
                return Integer.valueOf(value);
            } catch (final NumberFormatException e) {
                // ignore
            }
        }
        return defaultValue;
    }

    private List<String> getDayItems() {
        final List<String> items = new ArrayList<String>();
        for (int i = 0; i < 32; i++) {
            items.add(Integer.valueOf(i).toString());
        }
        for (int i = 40; i < 370; i += 10) {
            items.add(Integer.valueOf(i).toString());
        }
        items.add(Integer.valueOf(365).toString());
        return items;
    }

    private List<Map<String, String>> getSupportedSearchItems() {
        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        list.add(createItem(
                ComponentUtil.getMessageManager().getMessage(LaRequestUtil.getRequest().getLocale(), "labels.supported_search_web"),
                Constants.SUPPORTED_SEARCH_WEB));
        list.add(createItem(
                ComponentUtil.getMessageManager().getMessage(LaRequestUtil.getRequest().getLocale(), "labels.supported_search_none"),
                Constants.SUPPORTED_SEARCH_NONE));
        return list;
    }

    private Map<String, String> createItem(final String label, final String value) {
        final Map<String, String> map = new HashMap<String, String>();
        map.put(Constants.ITEM_LABEL, label);
        map.put(Constants.ITEM_VALUE, value);
        return map;

    }

}
