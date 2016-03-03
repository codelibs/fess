/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.mail.TestmailPostcard;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;
import org.lastaflute.core.mail.Postbox;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * @author Shunji Makino
 */
public class AdminGeneralAction extends FessAdminAction {

    private static final Logger logger = LoggerFactory.getLogger(AdminGeneralAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    protected DynamicProperties systemProperties;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameGeneral()));
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
    public HtmlResponse sendmail(final MailForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_AdminGeneral_AdminGeneralJsp);
        });

        final String[] toAddresses = form.notificationTo.split(",");
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("hostname", systemHelper.getHostname());

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Postbox postbox = ComponentUtil.getComponent(Postbox.class);
        try {
            TestmailPostcard.droppedInto(postbox, postcard -> {
                postcard.setFrom(fessConfig.getMailFromAddress(), fessConfig.getMailFromName());
                postcard.addReplyTo(fessConfig.getMailReturnPath());
                StreamUtil.of(toAddresses).forEach(address -> {
                    postcard.addTo(address);
                });
                BeanUtil.copyMapToBean(dataMap, postcard);
            });
            saveInfo(messages -> messages.addSuccessSendTestmail(GLOBAL));
            updateProperty(Constants.NOTIFICATION_TO_PROPERTY, form.notificationTo);
            systemProperties.store();
        } catch (final Exception e) {
            logger.warn("Failed to send a test mail.", e);
            saveError(messages -> messages.addErrorsFailedToSendTestmail(GLOBAL));
        }

        return redirectByParam(AdminGeneralAction.class, "notificationTo", form.notificationTo);
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_AdminGeneral_AdminGeneralJsp);
        });
        verifyToken(() -> {
            return asHtml(path_AdminGeneral_AdminGeneralJsp);
        });

        fessConfig.setLoginRequired(Constants.ON.equalsIgnoreCase(form.loginRequired));
//boolean updateProperty(Constants.INCREMENTAL_CRAWLING_PROPERTY, getCheckboxValue(form.incrementalCrawling));
        fessConfig.setIncrementalCrawling(Constants.ON.equalsIgnoreCase(form.incrementalCrawling));
//int     updateProperty(Constants.DAY_FOR_CLEANUP_PROPERTY, form.dayForCleanup.toString());
        fessConfig.setDayForCleanup(form.dayForCleanup);
//        updateProperty(Constants.CRAWLING_THREAD_COUNT_PROPERTY, form.crawlingThreadCount.toString());
        fessConfig.setCrawlingThreadCount(form.crawlingThreadCount);
//        updateProperty(Constants.SEARCH_LOG_PROPERTY, getCheckboxValue(form.searchLog));
        fessConfig.setSearchLog(Constants.ON.equalsIgnoreCase(form.searchLog));
//        updateProperty(Constants.USER_INFO_PROPERTY, getCheckboxValue(form.userInfo));
        fessConfig.setUserInfo(Constants.ON.equalsIgnoreCase(form.userInfo));
//        updateProperty(Constants.USER_FAVORITE_PROPERTY, getCheckboxValue(form.userFavorite));
        fessConfig.setUserFavorite(Constants.ON.equalsIgnoreCase(form.userFavorite));
//        updateProperty(Constants.WEB_API_JSON_PROPERTY, getCheckboxValue(form.webApiJson));
        fessConfig.setWebApiJson(Constants.ON.equalsIgnoreCase(form.webApiJson));
        fessConfig.setDefaultLabelValue(form.defaultLabelValue);
        fessConfig.setDefaultSortValue(form.defaultSortValue);
//        updateProperty(Constants.APPEND_QUERY_PARAMETER_PROPERTY, getCheckboxValue(form.appendQueryParameter));
        fessConfig.setAppendQueryParameter(Constants.ON.equalsIgnoreCase(form.appendQueryParameter));
//String  updateProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY, form.ignoreFailureType);
        fessConfig.setIgnoreFailureType(form.ignoreFailureType);
//        updateProperty(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY, form.failureCountThreshold.toString());
        fessConfig.setFailureCountThreshold(form.failureCountThreshold);
        fessConfig.setWebApiPopularWord(Constants.ON.equalsIgnoreCase(form.popularWord));
//        updateProperty(Constants.CSV_FILE_ENCODING_PROPERTY, form.csvFileEncoding);
        fessConfig.setCsvFileEncoding(form.csvFileEncoding);
//        updateProperty(Constants.PURGE_SEARCH_LOG_DAY_PROPERTY, form.purgeSearchLogDay.toString());
        fessConfig.setPurgeSearchLogDay(form.purgeSearchLogDay);
//int     updateProperty(Constants.PURGE_JOB_LOG_DAY_PROPERTY, form.purgeJobLogDay.toString());
        fessConfig.setPurgeJobLogDay(form.purgeJobLogDay);
//        updateProperty(Constants.PURGE_USER_INFO_DAY_PROPERTY, form.purgeUserInfoDay.toString());
        fessConfig.setPurgeUserInfoDay(form.purgeUserInfoDay);
//String  updateProperty(Constants.PURGE_BY_BOTS_PROPERTY, form.purgeByBots);
        fessConfig.setPurgeByBots(form.purgeByBots);
//        updateProperty(Constants.NOTIFICATION_TO_PROPERTY, form.notificationTo);
        fessConfig.setNotificationTo(form.notificationTo);
////s       updateProperty(Constants.SUGGEST_SEARCH_LOG_PROPERTY, getCheckboxValue(form.suggestSearchLog));
//        fessConfig.setSuggestSearchLog(form.suggestSearchLog);
////s       updateProperty(Constants.SUGGEST_DOCUMENTS_PROPERTY, getCheckboxValue(form.suggestDocuments));
//        fessConfig.setSuggestDocuments(form.suggestDocuments);
////i       updateProperty(Constants.PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY, form.purgeSuggestSearchLogDay.toString());
//        fessConfig.setPurgeSuggestSearchLogDay(form.purgeSuggestSearchLogDay);
////s       updateProperty(Constants.LDAP_PROVIDER_URL, form.ldapProviderUrl);
//        fessConfig.setLdapProviderUrl(form.ldapProviderUrl);
////s       updateProperty(Constants.LDAP_SECURITY_PRINCIPAL, form.ldapProviderUrl);
//        fessConfig.setLdapSecurityPrincipal(form.ldapSecurityPrincipal);
////s       updateProperty(Constants.LDAP_BASE_DN, form.ldapBaseDn);
//        fessConfig.setLdapBaseDn(form.ldapBaseDn);
////s       updateProperty(Constants.LDAP_ACCOUNT_FILTER, form.ldapAccountFilter);
//        fessConfig.setLdapAccountFilter(form.ldapAccountFilter);
////s       updateProperty(Constants.NOTIFICATION_LOGIN, form.notificationLogin);
//        fessConfig.setNotificationLogin(form.notificationLogin);
////s       updateProperty(Constants.NOTIFICATION_SEARCH_TOP, form.notificationSearchTop);
//        fessConfig.setNotificationSearchTop(form.notificationSearchTop);

        fessConfig.storeSystemProperties();
        saveInfo(messages -> messages.addSuccessUpdateCrawlerParams(GLOBAL));
        return redirect(getClass());
    }

    private String getCheckboxValue(final String value) {
        return Constants.ON.equalsIgnoreCase(value) ? Constants.TRUE : Constants.FALSE;
    }

    protected void updateForm(final EditForm form) {
        form.loginRequired = fessConfig.isLoginRequired() ? Constants.TRUE : Constants.FALSE;
//boolean form.incrementalCrawling = systemProperties.getProperty(Constants.INCREMENTAL_CRAWLING_PROPERTY, Constants.TRUE);
        form.incrementalCrawling = fessConfig.isIncrementalCrawling() ? Constants.TRUE : Constants.FALSE;
//int     form.dayForCleanup = getPropertyAsInteger(Constants.DAY_FOR_CLEANUP_PROPERTY, Constants.DEFAULT_DAY_FOR_CLEANUP);
        form.dayForCleanup = fessConfig.getDayForCleanup();
//        form.crawlingThreadCount = getPropertyAsInteger(Constants.CRAWLING_THREAD_COUNT_PROPERTY, 5);
        form.crawlingThreadCount = fessConfig.getCrawlingThreadCount();
//        form.searchLog = systemProperties.getProperty(Constants.SEARCH_LOG_PROPERTY, Constants.TRUE);
        form.searchLog = fessConfig.isSearchLog() ? Constants.TRUE : Constants.FALSE;
//        form.userInfo = systemProperties.getProperty(Constants.USER_INFO_PROPERTY, Constants.TRUE);
        form.userInfo = fessConfig.isUserInfo() ? Constants.TRUE : Constants.FALSE;
//        form.userFavorite = systemProperties.getProperty(Constants.USER_FAVORITE_PROPERTY, Constants.FALSE);
        form.userFavorite = fessConfig.isUserFavorite() ? Constants.TRUE : Constants.FALSE;
//        form.webApiJson = systemProperties.getProperty(Constants.WEB_API_JSON_PROPERTY, Constants.TRUE);
        form.webApiJson = fessConfig.isWebApiJson() ? Constants.TRUE : Constants.FALSE;
        form.defaultLabelValue = fessConfig.getDefaultLabelValue();
        form.defaultSortValue = fessConfig.getDefaultSortValue();
//        form.appendQueryParameter = systemProperties.getProperty(Constants.APPEND_QUERY_PARAMETER_PROPERTY, Constants.FALSE);
        form.appendQueryParameter = fessConfig.isAppendQueryParameter() ? Constants.TRUE : Constants.FALSE;
//String  form.ignoreFailureType = systemProperties.getProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY, Constants.DEFAULT_IGNORE_FAILURE_TYPE);
        form.ignoreFailureType = fessConfig.getIgnoreFailureType();
//        form.failureCountThreshold = getPropertyAsInteger(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY, Constants.DEFAULT_FAILURE_COUNT);
        form.failureCountThreshold = fessConfig.getFailureCountThreshold();
        form.popularWord = fessConfig.isWebApiPopularWord() ? Constants.TRUE : Constants.FALSE;
//        form.csvFileEncoding = systemProperties.getProperty(Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8);
        form.csvFileEncoding = fessConfig.getCsvFileEncoding();
//        form.purgeSearchLogDay = Integer.parseInt(systemProperties.getProperty(Constants.PURGE_SEARCH_LOG_DAY_PROPERTY, Constants.DEFAULT_PURGE_DAY));
        form.purgeSearchLogDay = fessConfig.getPurgeSearchLogDay();
//int     form.purgeJobLogDay = Integer.parseInt(systemProperties.getProperty(Constants.PURGE_JOB_LOG_DAY_PROPERTY, Constants.DEFAULT_PURGE_DAY));
        form.purgeJobLogDay = fessConfig.getPurgeJobLogDay();
//        form.purgeUserInfoDay = Integer.parseInt(systemProperties.getProperty(Constants.PURGE_USER_INFO_DAY_PROPERTY, Constants.DEFAULT_PURGE_DAY));
        form.purgeUserInfoDay = fessConfig.getPurgeUserInfoDay();
//String  form.purgeByBots = systemProperties.getProperty(Constants.PURGE_BY_BOTS_PROPERTY, Constants.DEFAULT_PURGE_BY_BOTS);
        form.purgeByBots = fessConfig.getPurgeByBots();
//        form.notificationTo = systemProperties.getProperty(Constants.NOTIFICATION_TO_PROPERTY, StringUtil.EMPTY);
        form.notificationTo = fessConfig.getNotificationTo();
////s       form.suggestSearchLog = systemProperties.getProperty(Constants.SUGGEST_SEARCH_LOG_PROPERTY, Constants.TRUE);
//        form.suggestSearchLog = fessConfig.getSuggestSearchLog();
////s       form.suggestDocuments = systemProperties.getProperty(Constants.SUGGEST_DOCUMENTS_PROPERTY, Constants.TRUE);
//        form.suggestDocuments = fessConfig.getSuggestDocuments();
////i       form.purgeSuggestSearchLogDay = Integer.parseInt(systemProperties.getProperty(Constants.PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY, Constants.DEFAULT_SUGGEST_PURGE_DAY));
//        form.purgeSuggestSearchLogDay = fessConfig.getPurgeSuggestSearchLogDay();
////s       form.ldapProviderUrl = systemProperties.getProperty(Constants.LDAP_PROVIDER_URL, StringUtil.EMPTY);
//        form.ldapProviderUrl = fessConfig.getLdapProviderUrl();
////s       form.ldapSecurityPrincipal = systemProperties.getProperty(Constants.LDAP_SECURITY_PRINCIPAL, StringUtil.EMPTY);
//        form.ldapSecurityPrincipal = fessConfig.getLdapSecurityPrincipal();
////s       form.ldapBaseDn = systemProperties.getProperty(Constants.LDAP_BASE_DN, StringUtil.EMPTY);
//        form.ldapBaseDn = fessConfig.getLdapBaseDn();
////s       form.ldapAccountFilter = systemProperties.getProperty(Constants.LDAP_ACCOUNT_FILTER, StringUtil.EMPTY);
//        form.ldapAccountFilter = fessConfig.getLdapAccountFilter();
////s       form.notificationLogin = systemProperties.getProperty(Constants.NOTIFICATION_LOGIN, StringUtil.EMPTY);
//        form.notificationLogin = fessConfig.getNotificationLogin();
////s       form.notificationSearchTop = systemProperties.getProperty(Constants.NOTIFICATION_SEARCH_TOP, StringUtil.EMPTY);
//        form.notificationSearchTop = fessConfig.getNotificationSearchTop();
    }

    private void updateProperty(final String key, final String value) {
        systemProperties.setProperty(key, value == null ? StringUtil.EMPTY : value);
    }

    private Integer getPropertyAsInteger(final String key, final int defaultValue) {
        final String value = systemProperties.getProperty(key);
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

}
