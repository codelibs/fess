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
package org.codelibs.fess.app.web.admin.general;

import static org.codelibs.core.stream.StreamUtil.stream;

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

    private static final String DUMMY_PASSWORD = "**********";

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
                updateForm(fessConfig, form);
            });
        });
    }

    @Execute
    public HtmlResponse sendmail(final MailForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_AdminGeneral_AdminGeneralJsp);
        });

        final String[] toAddresses = form.notificationTo.split(",");
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("hostname", systemHelper.getHostname());

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Postbox postbox = ComponentUtil.getComponent(Postbox.class);
        try {
            TestmailPostcard.droppedInto(postbox, postcard -> {
                postcard.setFrom(fessConfig.getMailFromAddress(), fessConfig.getMailFromName());
                postcard.addReplyTo(fessConfig.getMailReturnPath());
                stream(toAddresses).of(stream -> stream.forEach(address -> {
                    postcard.addTo(address);
                }));
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

        updateConfig(fessConfig, form);
        saveInfo(messages -> messages.addSuccessUpdateCrawlerParams(GLOBAL));
        return redirect(getClass());
    }

    public static void updateConfig(final FessConfig fessConfig, final EditForm form) {
        if (form.loginRequired != null) {
            fessConfig.setLoginRequired(Constants.ON.equalsIgnoreCase(form.loginRequired));
        }
        if (form.loginLink != null) {
            fessConfig.setLoginLinkEnabled(Constants.ON.equalsIgnoreCase(form.loginLink));
        }
        if (form.thumbnail != null) {
            fessConfig.setThumbnailEnabled(Constants.ON.equalsIgnoreCase(form.thumbnail));
        }
        if (form.incrementalCrawling != null) {
            fessConfig.setIncrementalCrawling(Constants.ON.equalsIgnoreCase(form.incrementalCrawling));
        }
        if (form.dayForCleanup != null) {
            fessConfig.setDayForCleanup(form.dayForCleanup);
        }
        if (form.crawlingThreadCount != null) {
            fessConfig.setCrawlingThreadCount(form.crawlingThreadCount);
        }
        if (form.searchLog != null) {
            fessConfig.setSearchLog(Constants.ON.equalsIgnoreCase(form.searchLog));
        }
        if (form.userInfo != null) {
            fessConfig.setUserInfo(Constants.ON.equalsIgnoreCase(form.userInfo));
        }
        if (form.userFavorite != null) {
            fessConfig.setUserFavorite(Constants.ON.equalsIgnoreCase(form.userFavorite));
        }
        if (form.webApiJson != null) {
            fessConfig.setWebApiJson(Constants.ON.equalsIgnoreCase(form.webApiJson));
        }
        if (form.defaultLabelValue != null) {
            fessConfig.setDefaultLabelValue(form.defaultLabelValue);
        }
        if (form.defaultSortValue != null) {
            fessConfig.setDefaultSortValue(form.defaultSortValue);
        }
        if (form.appendQueryParameter != null) {
            fessConfig.setAppendQueryParameter(Constants.ON.equalsIgnoreCase(form.appendQueryParameter));
        }
        if (form.ignoreFailureType != null) {
            fessConfig.setIgnoreFailureType(form.ignoreFailureType);
        }
        if (form.failureCountThreshold != null) {
            fessConfig.setFailureCountThreshold(form.failureCountThreshold);
        }
        if (form.popularWord != null) {
            fessConfig.setWebApiPopularWord(Constants.ON.equalsIgnoreCase(form.popularWord));
        }
        if (form.csvFileEncoding != null) {
            fessConfig.setCsvFileEncoding(form.csvFileEncoding);
        }
        if (form.purgeSearchLogDay != null) {
            fessConfig.setPurgeSearchLogDay(form.purgeSearchLogDay);
        }
        if (form.purgeJobLogDay != null) {
            fessConfig.setPurgeJobLogDay(form.purgeJobLogDay);
        }
        if (form.purgeUserInfoDay != null) {
            fessConfig.setPurgeUserInfoDay(form.purgeUserInfoDay);
        }
        if (form.purgeByBots != null) {
            fessConfig.setPurgeByBots(form.purgeByBots);
        }
        if (form.notificationTo != null) {
            fessConfig.setNotificationTo(form.notificationTo);
        }
        if (form.suggestSearchLog != null) {
            fessConfig.setSuggestSearchLog(Constants.ON.equalsIgnoreCase(form.suggestSearchLog));
        }
        if (form.suggestDocuments != null) {
            fessConfig.setSuggestDocuments(Constants.ON.equalsIgnoreCase(form.suggestDocuments));
        }
        if (form.purgeSuggestSearchLogDay != null) {
            fessConfig.setPurgeSuggestSearchLogDay(form.purgeSuggestSearchLogDay);
        }
        if (form.ldapProviderUrl != null) {
            fessConfig.setLdapProviderUrl(form.ldapProviderUrl);
        }
        if (form.ldapSecurityPrincipal != null) {
            fessConfig.setLdapSecurityPrincipal(form.ldapSecurityPrincipal);
        }
        if (form.ldapAdminSecurityPrincipal != null) {
            fessConfig.setLdapAdminSecurityPrincipal(form.ldapAdminSecurityPrincipal);
        }
        if (form.ldapAdminSecurityCredentials != null && form.ldapAdminSecurityCredentials != null
                && StringUtil.isNotBlank(form.ldapAdminSecurityCredentials.replace("*", " "))) {
            fessConfig.setLdapAdminSecurityCredentials(form.ldapAdminSecurityCredentials);
        }
        if (form.ldapBaseDn != null) {
            fessConfig.setLdapBaseDn(form.ldapBaseDn);
        }
        if (form.ldapAccountFilter != null) {
            fessConfig.setLdapAccountFilter(form.ldapAccountFilter);
        }
        if (form.ldapMemberofAttribute != null) {
            fessConfig.setLdapMemberofAttribute(form.ldapMemberofAttribute);
        }
        if (form.notificationLogin != null) {
            fessConfig.setNotificationLogin(form.notificationLogin);
        }
        if (form.notificationSearchTop != null) {
            fessConfig.setNotificationSearchTop(form.notificationSearchTop);
        }

        fessConfig.storeSystemProperties();
        ComponentUtil.getLdapManager().updateConfig();
    }

    public static void updateForm(final FessConfig fessConfig, final EditForm form) {
        form.loginRequired = fessConfig.isLoginRequired() ? Constants.TRUE : Constants.FALSE;
        form.loginLink = fessConfig.isLoginLinkEnabled() ? Constants.TRUE : Constants.FALSE;
        form.thumbnail = fessConfig.isThumbnailEnabled() ? Constants.TRUE : Constants.FALSE;
        form.incrementalCrawling = fessConfig.isIncrementalCrawling() ? Constants.TRUE : Constants.FALSE;
        form.dayForCleanup = fessConfig.getDayForCleanup();
        form.crawlingThreadCount = fessConfig.getCrawlingThreadCount();
        form.searchLog = fessConfig.isSearchLog() ? Constants.TRUE : Constants.FALSE;
        form.userInfo = fessConfig.isUserInfo() ? Constants.TRUE : Constants.FALSE;
        form.userFavorite = fessConfig.isUserFavorite() ? Constants.TRUE : Constants.FALSE;
        form.webApiJson = fessConfig.isWebApiJson() ? Constants.TRUE : Constants.FALSE;
        form.defaultLabelValue = fessConfig.getDefaultLabelValue();
        form.defaultSortValue = fessConfig.getDefaultSortValue();
        form.appendQueryParameter = fessConfig.isAppendQueryParameter() ? Constants.TRUE : Constants.FALSE;
        form.ignoreFailureType = fessConfig.getIgnoreFailureType();
        form.failureCountThreshold = fessConfig.getFailureCountThreshold();
        form.popularWord = fessConfig.isWebApiPopularWord() ? Constants.TRUE : Constants.FALSE;
        form.csvFileEncoding = fessConfig.getCsvFileEncoding();
        form.purgeSearchLogDay = fessConfig.getPurgeSearchLogDay();
        form.purgeJobLogDay = fessConfig.getPurgeJobLogDay();
        form.purgeUserInfoDay = fessConfig.getPurgeUserInfoDay();
        form.purgeByBots = fessConfig.getPurgeByBots();
        form.notificationTo = fessConfig.getNotificationTo();
        form.suggestSearchLog = fessConfig.isSuggestSearchLog() ? Constants.TRUE : Constants.FALSE;
        form.suggestDocuments = fessConfig.isSuggestDocuments() ? Constants.TRUE : Constants.FALSE;
        form.purgeSuggestSearchLogDay = fessConfig.getPurgeSuggestSearchLogDay();
        form.ldapProviderUrl = fessConfig.getLdapProviderUrl();
        form.ldapSecurityPrincipal = fessConfig.getLdapSecurityPrincipal();
        form.ldapAdminSecurityPrincipal = fessConfig.getLdapAdminSecurityPrincipal();
        form.ldapAdminSecurityCredentials = DUMMY_PASSWORD;//fessConfig.getLdapAdminSecurityCredentials();
        form.ldapBaseDn = fessConfig.getLdapBaseDn();
        form.ldapAccountFilter = fessConfig.getLdapAccountFilter();
        form.ldapMemberofAttribute = fessConfig.getLdapMemberofAttribute();
        form.notificationLogin = fessConfig.getNotificationLogin();
        form.notificationSearchTop = fessConfig.getNotificationSearchTop();
    }

    private void updateProperty(final String key, final String value) {
        systemProperties.setProperty(key, value == null ? StringUtil.EMPTY : value);
    }

    private List<String> getDayItems() {
        final List<String> items = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            items.add(Integer.toString(i));
        }
        for (int i = 40; i < 370; i += 10) {
            items.add(Integer.toString(i));
        }
        items.add(Integer.toString(365));
        return items;
    }

}
