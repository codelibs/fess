/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.action.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.form.admin.CrawlForm;
import jp.sf.fess.helper.SystemHelper;

import org.codelibs.core.util.DynamicProperties;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.seasar.struts.util.MessageResourcesUtil;
import org.seasar.struts.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlAction implements Serializable {
    private static final Logger logger = LoggerFactory
            .getLogger(CrawlAction.class);

    private static final long serialVersionUID = 1L;

    @ActionForm
    @Resource
    protected CrawlForm crawlForm;

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected SystemHelper systemHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("crawl");
    }

    protected String showIndex(final boolean redirect) {
        crawlForm.diffCrawling = crawlerProperties.getProperty(
                Constants.DIFF_CRAWLING_PROPERTY, Constants.TRUE);
        crawlForm.useAclAsRole = crawlerProperties.getProperty(
                Constants.USE_ACL_AS_ROLE, Constants.FALSE);
        crawlForm.serverRotation = crawlerProperties.getProperty(
                Constants.SERVER_ROTATION_PROPERTY, Constants.FALSE);
        crawlForm.dayForCleanup = crawlerProperties.getProperty(
                Constants.DAY_FOR_CLEANUP_PROPERTY, "1");
        crawlForm.commitPerCount = crawlerProperties.getProperty(
                Constants.COMMIT_PER_COUNT_PROPERTY,
                Long.toString(Constants.DEFAULT_COMMIT_PER_COUNT));
        crawlForm.crawlingThreadCount = crawlerProperties.getProperty(
                Constants.CRAWLING_THREAD_COUNT_PROPERTY, "5");
        crawlForm.mobileTranscoder = crawlerProperties.getProperty(
                Constants.MOBILE_TRANSCODER_PROPERTY, Constants.EMPTY_STRING);
        crawlForm.searchLog = crawlerProperties.getProperty(
                Constants.SEARCH_LOG_PROPERTY, Constants.TRUE);
        crawlForm.userInfo = crawlerProperties.getProperty(
                Constants.USER_INFO_PROPERTY, Constants.TRUE);
        crawlForm.userFavorite = crawlerProperties.getProperty(
                Constants.USER_FAVORITE_PROPERTY, Constants.FALSE);
        crawlForm.webApiXml = crawlerProperties.getProperty(
                Constants.WEB_API_XML_PROPERTY, Constants.TRUE);
        crawlForm.webApiJson = crawlerProperties.getProperty(
                Constants.WEB_API_JSON_PROPERTY, Constants.TRUE);
        crawlForm.defaultLabelValue = crawlerProperties.getProperty(
                Constants.DEFAULT_LABEL_VALUE_PROPERTY, "");
        crawlForm.appendQueryParameter = crawlerProperties.getProperty(
                Constants.APPEND_QUERY_PARAMETER_PROPERTY, Constants.FALSE);
        crawlForm.supportedSearch = crawlerProperties.getProperty(
                Constants.SUPPORTED_SEARCH_FEATURE_PROPERTY,
                Constants.SUPPORTED_SEARCH_WEB_MOBILE);
        crawlForm.ignoreFailureType = crawlerProperties.getProperty(
                Constants.IGNORE_FAILURE_TYPE_PROPERTY,
                Constants.DEFAULT_IGNORE_FAILURE_TYPE);
        crawlForm.failureCountThreshold = crawlerProperties.getProperty(
                Constants.FAILURE_COUNT_THRESHOLD_PROPERTY,
                Constants.DEFAULT_FAILURE_COUNT);
        crawlForm.hotSearchWord = crawlerProperties.getProperty(
                Constants.WEB_API_HOT_SEARCH_WORD_PROPERTY, Constants.TRUE);
        crawlForm.csvFileEncoding = crawlerProperties.getProperty(
                Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8);
        crawlForm.purgeSearchLogDay = crawlerProperties.getProperty(
                Constants.PURGE_SEARCH_LOG_DAY_PROPERTY,
                Constants.DEFAULT_PURGE_DAY);
        crawlForm.purgeJobLogDay = crawlerProperties.getProperty(
                Constants.PURGE_JOB_LOG_DAY_PROPERTY,
                Constants.DEFAULT_PURGE_DAY);
        crawlForm.purgeUserInfoDay = crawlerProperties.getProperty(
                Constants.PURGE_USER_INFO_DAY_PROPERTY,
                Constants.DEFAULT_PURGE_DAY);
        crawlForm.purgeByBots = crawlerProperties.getProperty(
                Constants.PURGE_BY_BOTS_PROPERTY,
                Constants.DEFAULT_PURGE_BY_BOTS);
        crawlForm.notificationTo = crawlerProperties.getProperty(
                Constants.NOTIFICATION_TO_PROPERTY, Constants.EMPTY_STRING);
        if (redirect) {
            return "index?redirect=true";
        } else {
            return "index.jsp";
        }
    }

    @Token(save = true, validate = false)
    @Execute(validator = false)
    public String index() {
        return showIndex(false);
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "index.jsp")
    public String update() {
        crawlerProperties
                .setProperty(
                        Constants.DIFF_CRAWLING_PROPERTY,
                        crawlForm.diffCrawling != null
                                && Constants.ON
                                        .equalsIgnoreCase(crawlForm.diffCrawling) ? Constants.TRUE
                                : Constants.FALSE);
        crawlerProperties
                .setProperty(
                        Constants.USE_ACL_AS_ROLE,
                        crawlForm.useAclAsRole != null
                                && Constants.ON
                                        .equalsIgnoreCase(crawlForm.useAclAsRole) ? Constants.TRUE
                                : Constants.FALSE);
        crawlerProperties
                .setProperty(
                        Constants.SERVER_ROTATION_PROPERTY,
                        crawlForm.serverRotation != null
                                && Constants.ON
                                        .equalsIgnoreCase(crawlForm.serverRotation) ? Constants.TRUE
                                : Constants.FALSE);
        crawlerProperties.setProperty(Constants.DAY_FOR_CLEANUP_PROPERTY,
                crawlForm.dayForCleanup);
        crawlerProperties.setProperty(Constants.COMMIT_PER_COUNT_PROPERTY,
                crawlForm.commitPerCount);
        crawlerProperties.setProperty(Constants.CRAWLING_THREAD_COUNT_PROPERTY,
                crawlForm.crawlingThreadCount);
        crawlerProperties.setProperty(Constants.MOBILE_TRANSCODER_PROPERTY,
                crawlForm.mobileTranscoder);
        crawlerProperties
                .setProperty(
                        Constants.SEARCH_LOG_PROPERTY,
                        crawlForm.searchLog != null
                                && Constants.ON
                                        .equalsIgnoreCase(crawlForm.searchLog) ? Constants.TRUE
                                : Constants.FALSE);
        crawlerProperties
                .setProperty(
                        Constants.USER_INFO_PROPERTY,
                        crawlForm.userInfo != null
                                && Constants.ON
                                        .equalsIgnoreCase(crawlForm.userInfo) ? Constants.TRUE
                                : Constants.FALSE);
        crawlerProperties
                .setProperty(
                        Constants.USER_FAVORITE_PROPERTY,
                        crawlForm.userFavorite != null
                                && Constants.ON
                                        .equalsIgnoreCase(crawlForm.userFavorite) ? Constants.TRUE
                                : Constants.FALSE);
        crawlerProperties
                .setProperty(
                        Constants.WEB_API_XML_PROPERTY,
                        crawlForm.webApiXml != null
                                && Constants.ON
                                        .equalsIgnoreCase(crawlForm.webApiXml) ? Constants.TRUE
                                : Constants.FALSE);
        crawlerProperties
                .setProperty(
                        Constants.WEB_API_JSON_PROPERTY,
                        crawlForm.webApiJson != null
                                && Constants.ON
                                        .equalsIgnoreCase(crawlForm.webApiJson) ? Constants.TRUE
                                : Constants.FALSE);
        crawlerProperties.setProperty(Constants.DEFAULT_LABEL_VALUE_PROPERTY,
                crawlForm.defaultLabelValue);
        crawlerProperties
                .setProperty(
                        Constants.APPEND_QUERY_PARAMETER_PROPERTY,
                        crawlForm.appendQueryParameter != null
                                && Constants.ON
                                        .equalsIgnoreCase(crawlForm.appendQueryParameter) ? Constants.TRUE
                                : Constants.FALSE);
        crawlerProperties.setProperty(
                Constants.SUPPORTED_SEARCH_FEATURE_PROPERTY,
                crawlForm.supportedSearch);
        crawlerProperties.setProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY,
                crawlForm.ignoreFailureType);
        crawlerProperties.setProperty(
                Constants.FAILURE_COUNT_THRESHOLD_PROPERTY,
                crawlForm.failureCountThreshold);
        crawlerProperties
                .setProperty(
                        Constants.WEB_API_HOT_SEARCH_WORD_PROPERTY,
                        crawlForm.hotSearchWord != null
                                && Constants.ON
                                        .equalsIgnoreCase(crawlForm.hotSearchWord) ? Constants.TRUE
                                : Constants.FALSE);
        crawlerProperties.setProperty(Constants.CSV_FILE_ENCODING_PROPERTY,
                crawlForm.csvFileEncoding);
        crawlerProperties.setProperty(Constants.PURGE_SEARCH_LOG_DAY_PROPERTY,
                crawlForm.purgeSearchLogDay);
        crawlerProperties.setProperty(Constants.PURGE_JOB_LOG_DAY_PROPERTY,
                crawlForm.purgeJobLogDay);
        crawlerProperties.setProperty(Constants.PURGE_USER_INFO_DAY_PROPERTY,
                crawlForm.purgeUserInfoDay);
        crawlerProperties.setProperty(Constants.PURGE_BY_BOTS_PROPERTY,
                crawlForm.purgeByBots);
        crawlerProperties.setProperty(Constants.NOTIFICATION_TO_PROPERTY,
                crawlForm.notificationTo);
        try {
            crawlerProperties.store();
            SAStrutsUtil.addSessionMessage("success.update_crawler_params");
            return showIndex(true);
        } catch (final Exception e) {
            logger.error("Failed to update crawler parameters.", e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_update_crawler_params", e);
        }
    }

    public List<String> getDayItems() {
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

    public List<Map<String, String>> getMobileTranscoderItems() {
        final List<Map<String, String>> mobileTranscoderList = new ArrayList<Map<String, String>>();
        mobileTranscoderList.add(createItem("-", Constants.EMPTY_STRING));
        mobileTranscoderList.add(createItem(MessageResourcesUtil.getMessage(
                RequestUtil.getRequest().getLocale(),
                "labels.mobile_transcoder_google"),
                Constants.GOOGLE_MOBILE_TRANSCODER));
        return mobileTranscoderList;
    }

    public List<Map<String, String>> getSupportedSearchItems() {
        final List<Map<String, String>> mobileTranscoderList = new ArrayList<Map<String, String>>();
        mobileTranscoderList.add(createItem(MessageResourcesUtil.getMessage(
                RequestUtil.getRequest().getLocale(),
                "labels.supported_search_web_mobile"),
                Constants.SUPPORTED_SEARCH_WEB_MOBILE));
        mobileTranscoderList
                .add(createItem(MessageResourcesUtil.getMessage(RequestUtil
                        .getRequest().getLocale(),
                        "labels.supported_search_web"),
                        Constants.SUPPORTED_SEARCH_WEB));
        mobileTranscoderList.add(createItem(MessageResourcesUtil.getMessage(
                RequestUtil.getRequest().getLocale(),
                "labels.supported_search_mobile"),
                Constants.SUPPORTED_SEARCH_MOBILE));
        mobileTranscoderList.add(createItem(MessageResourcesUtil.getMessage(
                RequestUtil.getRequest().getLocale(),
                "labels.supported_search_none"),
                Constants.SUPPORTED_SEARCH_NONE));
        return mobileTranscoderList;
    }

    private Map<String, String> createItem(final String label,
            final String value) {
        final Map<String, String> map = new HashMap<String, String>();
        map.put(Constants.ITEM_LABEL, label);
        map.put(Constants.ITEM_VALUE, value);
        return map;

    }
}