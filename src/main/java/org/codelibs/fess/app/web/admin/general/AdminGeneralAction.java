/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.llm.LlmClient;
import org.codelibs.fess.llm.LlmClientManager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.mail.TestmailPostcard;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.core.mail.Postbox;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;

/**
 * Admin action for General settings.
 *
 */
public class AdminGeneralAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminGeneralAction() {
        super();
    }

    /** The role name for general settings administration. */
    public static final String ROLE = "admin-general";

    private static final String DUMMY_PASSWORD = "**********";

    private static final Logger logger = LogManager.getLogger(AdminGeneralAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** System properties for configuration management. */
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
        final boolean ragEnabled = isRagSectionVisible();
        runtime.registerData("ragEnabled", ragEnabled);
        if (ragEnabled) {
            runtime.registerData("ragLlmNameItems", getRagLlmNameItems());
        }
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //

    /**
     * Displays the general settings index page.
     *
     * @return HTML response for the general settings page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        saveToken();
        return asHtml(path_AdminGeneral_AdminGeneralJsp).useForm(EditForm.class, setup -> {
            setup.setup(form -> {
                updateForm(fessConfig, form);
            });
        });
    }

    /**
     * Sends a test mail using the provided notification settings.
     *
     * @param form the mail form containing notification settings
     * @return HTML response after sending test mail
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse sendmail(final MailForm form) {
        validate(form, messages -> {}, () -> asHtml(path_AdminGeneral_AdminGeneralJsp));

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

    /**
     * Updates the general system configuration settings.
     *
     * @param form the edit form containing updated settings
     * @return HTML response after updating settings
     */
    @Execute
    @Secured({ ROLE })
    public HtmlResponse update(final EditForm form) {
        validate(form, messages -> {}, () -> asHtml(path_AdminGeneral_AdminGeneralJsp));
        verifyToken(() -> asHtml(path_AdminGeneral_AdminGeneralJsp));

        updateConfig(fessConfig, form);
        saveInfo(messages -> messages.addSuccessUpdateCrawlerParams(GLOBAL));
        return redirect(getClass());
    }

    /**
     * Updates the Fess configuration with values from the form.
     *
     * @param fessConfig the Fess configuration to update
     * @param form the form containing new configuration values
     */
    public static void updateConfig(final FessConfig fessConfig, final EditForm form) {
        fessConfig.setLoginRequired(isCheckboxEnabled(form.loginRequired));
        fessConfig.setResultCollapsed(isCheckboxEnabled(form.resultCollapsed));
        fessConfig.setLoginLinkEnabled(isCheckboxEnabled(form.loginLink));
        fessConfig.setThumbnailEnabled(isCheckboxEnabled(form.thumbnail));
        fessConfig.setIncrementalCrawling(isCheckboxEnabled(form.incrementalCrawling));
        fessConfig.setDayForCleanup(form.dayForCleanup);
        fessConfig.setCrawlingThreadCount(form.crawlingThreadCount);
        fessConfig.setUserAgentName(form.crawlingUserAgent);
        fessConfig.setSearchLog(isCheckboxEnabled(form.searchLog));
        fessConfig.setUserInfo(isCheckboxEnabled(form.userInfo));
        fessConfig.setUserFavorite(isCheckboxEnabled(form.userFavorite));
        fessConfig.setWebApiJson(isCheckboxEnabled(form.webApiJson));
        fessConfig.setSearchFileProxy(isCheckboxEnabled(form.searchFileProxy));
        fessConfig.setUseBrowserLocaleForSearch(isCheckboxEnabled(form.searchUseBrowserLocale));
        fessConfig.setSsoType(form.ssoType);
        fessConfig.setAppValue(form.appValue);
        fessConfig.setDefaultLabelValue(form.defaultLabelValue);
        fessConfig.setDefaultSortValue(form.defaultSortValue);
        fessConfig.setVirtualHostValue(form.virtualHostValue);
        fessConfig.setAppendQueryParameter(isCheckboxEnabled(form.appendQueryParameter));
        fessConfig.setIgnoreFailureType(form.ignoreFailureType);
        fessConfig.setFailureCountThreshold(form.failureCountThreshold);
        fessConfig.setWebApiPopularWord(isCheckboxEnabled(form.popularWord));
        fessConfig.setCsvFileEncoding(form.csvFileEncoding);
        fessConfig.setPurgeSearchLogDay(form.purgeSearchLogDay);
        fessConfig.setPurgeJobLogDay(form.purgeJobLogDay);
        fessConfig.setPurgeUserInfoDay(form.purgeUserInfoDay);
        fessConfig.setPurgeByBots(form.purgeByBots);
        fessConfig.setNotificationTo(form.notificationTo);
        fessConfig.setSuggestSearchLog(isCheckboxEnabled(form.suggestSearchLog));
        fessConfig.setSuggestDocuments(isCheckboxEnabled(form.suggestDocuments));
        fessConfig.setPurgeSuggestSearchLogDay(form.purgeSuggestSearchLogDay);
        fessConfig.setLdapProviderUrl(form.ldapProviderUrl);
        fessConfig.setLdapSecurityPrincipal(form.ldapSecurityPrincipal);
        fessConfig.setLdapAdminSecurityPrincipal(form.ldapAdminSecurityPrincipal);
        if (form.ldapAdminSecurityCredentials != null && StringUtil.isNotBlank(form.ldapAdminSecurityCredentials.replace("*", " "))) {
            fessConfig.setLdapAdminSecurityCredentials(form.ldapAdminSecurityCredentials);
        }
        fessConfig.setLdapBaseDn(form.ldapBaseDn);
        fessConfig.setLdapAccountFilter(form.ldapAccountFilter);
        fessConfig.setLdapGroupFilter(form.ldapGroupFilter);
        fessConfig.setLdapMemberofAttribute(form.ldapMemberofAttribute);
        fessConfig.setLdapSecurityAuthentication(form.ldapSecurityAuthentication);
        fessConfig.setLdapInitialContextFactory(form.ldapInitialContextFactory);
        fessConfig.setNotificationLogin(form.notificationLogin);
        fessConfig.setNotificationSearchTop(form.notificationSearchTop);
        fessConfig.setNotificationAdvanceSearch(form.notificationAdvanceSearch);
        fessConfig.setSlackWebhookUrls(form.slackWebhookUrls);
        fessConfig.setGoogleChatWebhookUrls(form.googleChatWebhookUrls);
        fessConfig.setLogNotificationEnabled(isCheckboxEnabled(form.logNotificationEnabled));
        fessConfig.setStorageEndpoint(form.storageEndpoint);
        if (form.storageAccessKey != null && StringUtil.isNotBlank(form.storageAccessKey.replace("*", " "))) {
            fessConfig.setStorageAccessKey(form.storageAccessKey);
        }
        if (form.storageSecretKey != null && StringUtil.isNotBlank(form.storageSecretKey.replace("*", " "))) {
            fessConfig.setStorageSecretKey(form.storageSecretKey);
        }
        fessConfig.setStorageBucket(form.storageBucket);
        fessConfig.setStorageType(form.storageType);
        fessConfig.setStorageRegion(form.storageRegion);
        fessConfig.setStorageProjectId(form.storageProjectId);
        fessConfig.setStorageCredentialsPath(form.storageCredentialsPath);
        if (form.ragLlmName != null && isValidRagLlmName(form.ragLlmName)) {
            fessConfig.setRagLlmName(form.ragLlmName);
        }

        // OpenID Connect
        if (form.oicClientId != null && StringUtil.isNotBlank(form.oicClientId.replace("*", " "))) {
            fessConfig.setSystemProperty("oic.client.id", form.oicClientId);
        }
        if (form.oicClientSecret != null && StringUtil.isNotBlank(form.oicClientSecret.replace("*", " "))) {
            fessConfig.setSystemProperty("oic.client.secret", form.oicClientSecret);
        }
        fessConfig.setSystemProperty("oic.auth.server.url", form.oicAuthServerUrl);
        fessConfig.setSystemProperty("oic.token.server.url", form.oicTokenServerUrl);
        fessConfig.setSystemProperty("oic.redirect.url", form.oicRedirectUrl);
        fessConfig.setSystemProperty("oic.scope", form.oicScope);
        fessConfig.setSystemProperty("oic.base.url", form.oicBaseUrl);
        fessConfig.setSystemProperty("oic.default.groups", form.oicDefaultGroups);
        fessConfig.setSystemProperty("oic.default.roles", form.oicDefaultRoles);

        // SAML
        fessConfig.setSystemProperty("saml.sp.base.url", form.samlSpBaseUrl);
        fessConfig.setSystemProperty("saml.attribute.group.name", form.samlAttributeGroupName);
        fessConfig.setSystemProperty("saml.attribute.role.name", form.samlAttributeRoleName);
        fessConfig.setSystemProperty("saml.default.groups", form.samlDefaultGroups);
        fessConfig.setSystemProperty("saml.default.roles", form.samlDefaultRoles);

        // SPNEGO
        fessConfig.setSystemProperty("spnego.krb5.conf", form.spnegoKrb5Conf);
        fessConfig.setSystemProperty("spnego.login.conf", form.spnegoLoginConf);
        fessConfig.setSystemProperty("spnego.login.client.module", form.spnegoLoginClientModule);
        fessConfig.setSystemProperty("spnego.login.server.module", form.spnegoLoginServerModule);
        fessConfig.setSystemProperty("spnego.preauth.username", form.spnegoPreauthUsername);
        if (form.spnegoPreauthPassword != null && StringUtil.isNotBlank(form.spnegoPreauthPassword.replace("*", " "))) {
            fessConfig.setSystemProperty("spnego.preauth.password", form.spnegoPreauthPassword);
        }
        fessConfig.setSystemProperty("spnego.allow.basic", String.valueOf(isCheckboxEnabled(form.spnegoAllowBasic)));
        fessConfig.setSystemProperty("spnego.allow.unsecure.basic", String.valueOf(isCheckboxEnabled(form.spnegoAllowUnsecureBasic)));
        fessConfig.setSystemProperty("spnego.prompt.ntlm", String.valueOf(isCheckboxEnabled(form.spnegoPromptNtlm)));
        fessConfig.setSystemProperty("spnego.allow.localhost", String.valueOf(isCheckboxEnabled(form.spnegoAllowLocalhost)));
        fessConfig.setSystemProperty("spnego.allow.delegation", String.valueOf(isCheckboxEnabled(form.spnegoAllowDelegation)));
        fessConfig.setSystemProperty("spnego.exclude.dirs", form.spnegoExcludeDirs);

        // Entra ID
        if (form.entraidClientId != null && StringUtil.isNotBlank(form.entraidClientId.replace("*", " "))) {
            fessConfig.setSystemProperty("entraid.client.id", form.entraidClientId);
        }
        if (form.entraidClientSecret != null && StringUtil.isNotBlank(form.entraidClientSecret.replace("*", " "))) {
            fessConfig.setSystemProperty("entraid.client.secret", form.entraidClientSecret);
        }
        fessConfig.setSystemProperty("entraid.tenant", form.entraidTenant);
        fessConfig.setSystemProperty("entraid.authority", form.entraidAuthority);
        fessConfig.setSystemProperty("entraid.reply.url", form.entraidReplyUrl);
        fessConfig.setSystemProperty("entraid.state.ttl", form.entraidStateTtl);
        fessConfig.setSystemProperty("entraid.default.groups", form.entraidDefaultGroups);
        fessConfig.setSystemProperty("entraid.default.roles", form.entraidDefaultRoles);
        fessConfig.setSystemProperty("entraid.permission.fields", form.entraidPermissionFields);
        fessConfig.setSystemProperty("entraid.use.ds", String.valueOf(isCheckboxEnabled(form.entraidUseDs)));

        fessConfig.storeSystemProperties();
        ComponentUtil.getLdapManager().updateConfig();
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        systemHelper.refreshDesignJspFiles();
        systemHelper.updateSystemProperties();

        if (StringUtil.isNotBlank(form.logLevel)) {
            systemHelper.setLogLevel(form.logLevel);
        }
        if (StringUtil.isNotBlank(form.llmLogLevel)) {
            systemHelper.setLlmLogLevel(form.llmLogLevel);
        }
    }

    /**
     * Updates the form with current configuration values.
     *
     * @param fessConfig the current Fess configuration
     * @param form the form to populate with configuration values
     */
    public static void updateForm(final FessConfig fessConfig, final EditForm form) {
        form.loginRequired = fessConfig.isLoginRequired() ? Constants.TRUE : Constants.FALSE;
        form.resultCollapsed = fessConfig.isResultCollapsed() ? Constants.TRUE : Constants.FALSE;
        form.loginLink = fessConfig.isLoginLinkEnabled() ? Constants.TRUE : Constants.FALSE;
        form.thumbnail = fessConfig.isThumbnailEnabled() ? Constants.TRUE : Constants.FALSE;
        form.incrementalCrawling = fessConfig.isIncrementalCrawling() ? Constants.TRUE : Constants.FALSE;
        form.dayForCleanup = fessConfig.getDayForCleanup();
        form.crawlingThreadCount = fessConfig.getCrawlingThreadCount();
        form.crawlingUserAgent = fessConfig.getUserAgentName();
        form.searchLog = fessConfig.isSearchLog() ? Constants.TRUE : Constants.FALSE;
        form.userInfo = fessConfig.isUserInfo() ? Constants.TRUE : Constants.FALSE;
        form.userFavorite = fessConfig.isUserFavorite() ? Constants.TRUE : Constants.FALSE;
        form.webApiJson = fessConfig.isWebApiJson() ? Constants.TRUE : Constants.FALSE;
        form.searchFileProxy = fessConfig.isSearchFileProxyEnabled() ? Constants.TRUE : Constants.FALSE;
        form.searchUseBrowserLocale = fessConfig.isBrowserLocaleForSearchUsed() ? Constants.TRUE : Constants.FALSE;
        form.ssoType = fessConfig.getSsoType();
        form.appValue = fessConfig.getAppValue();
        form.defaultLabelValue = fessConfig.getDefaultLabelValue();
        form.defaultSortValue = fessConfig.getDefaultSortValue();
        form.virtualHostValue = fessConfig.getVirtualHostValue();
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
        form.ldapAdminSecurityCredentials =
                StringUtil.isNotBlank(fessConfig.getLdapAdminSecurityCredentials()) ? DUMMY_PASSWORD : StringUtil.EMPTY;
        form.ldapBaseDn = fessConfig.getLdapBaseDn();
        form.ldapAccountFilter = fessConfig.getLdapAccountFilter();
        form.ldapGroupFilter = fessConfig.getLdapGroupFilter();
        form.ldapMemberofAttribute = fessConfig.getLdapMemberofAttribute();
        form.ldapSecurityAuthentication = fessConfig.getLdapSecurityAuthentication();
        form.ldapInitialContextFactory = fessConfig.getLdapInitialContextFactory();
        form.notificationLogin = fessConfig.getNotificationLogin();
        form.notificationSearchTop = fessConfig.getNotificationSearchTop();
        form.notificationAdvanceSearch = fessConfig.getNotificationAdvanceSearch();
        form.slackWebhookUrls = fessConfig.getSlackWebhookUrls();
        form.googleChatWebhookUrls = fessConfig.getGoogleChatWebhookUrls();
        form.logNotificationEnabled = fessConfig.isLogNotificationEnabled() ? Constants.TRUE : Constants.FALSE;
        form.storageEndpoint = fessConfig.getStorageEndpoint();
        form.storageAccessKey = StringUtil.isNotBlank(fessConfig.getStorageAccessKey()) ? DUMMY_PASSWORD : StringUtil.EMPTY;
        form.storageSecretKey = StringUtil.isNotBlank(fessConfig.getStorageSecretKey()) ? DUMMY_PASSWORD : StringUtil.EMPTY;
        form.storageBucket = fessConfig.getStorageBucket();
        form.storageType = fessConfig.getStorageType();
        form.storageRegion = fessConfig.getStorageRegion();
        form.storageProjectId = fessConfig.getStorageProjectId();
        form.storageCredentialsPath = fessConfig.getStorageCredentialsPath();
        form.ragLlmName = fessConfig.getRagLlmName();
        form.llmLogLevel = ComponentUtil.getSystemHelper().getLlmLogLevel().toUpperCase();
        form.logLevel = ComponentUtil.getSystemHelper().getLogLevel().toUpperCase();

        // OpenID Connect
        form.oicClientId = StringUtil.isNotBlank(fessConfig.getSystemProperty("oic.client.id")) ? DUMMY_PASSWORD : StringUtil.EMPTY;
        form.oicClientSecret = StringUtil.isNotBlank(fessConfig.getSystemProperty("oic.client.secret")) ? DUMMY_PASSWORD : StringUtil.EMPTY;
        form.oicAuthServerUrl = fessConfig.getSystemProperty("oic.auth.server.url", "https://accounts.google.com/o/oauth2/auth");
        form.oicTokenServerUrl = fessConfig.getSystemProperty("oic.token.server.url", "https://accounts.google.com/o/oauth2/token");
        form.oicRedirectUrl = fessConfig.getSystemProperty("oic.redirect.url", StringUtil.EMPTY);
        form.oicScope = fessConfig.getSystemProperty("oic.scope", StringUtil.EMPTY);
        form.oicBaseUrl = fessConfig.getSystemProperty("oic.base.url", "http://localhost:8080");
        form.oicDefaultGroups = fessConfig.getSystemProperty("oic.default.groups", StringUtil.EMPTY);
        form.oicDefaultRoles = fessConfig.getSystemProperty("oic.default.roles", StringUtil.EMPTY);

        // SAML
        form.samlSpBaseUrl = fessConfig.getSystemProperty("saml.sp.base.url", "http://localhost:8080");
        form.samlAttributeGroupName = fessConfig.getSystemProperty("saml.attribute.group.name", "memberOf");
        form.samlAttributeRoleName = fessConfig.getSystemProperty("saml.attribute.role.name", StringUtil.EMPTY);
        form.samlDefaultGroups = fessConfig.getSystemProperty("saml.default.groups", StringUtil.EMPTY);
        form.samlDefaultRoles = fessConfig.getSystemProperty("saml.default.roles", StringUtil.EMPTY);

        // SPNEGO
        form.spnegoKrb5Conf = fessConfig.getSystemProperty("spnego.krb5.conf", "krb5.conf");
        form.spnegoLoginConf = fessConfig.getSystemProperty("spnego.login.conf", "auth_login.conf");
        form.spnegoLoginClientModule = fessConfig.getSystemProperty("spnego.login.client.module", "spnego-client");
        form.spnegoLoginServerModule = fessConfig.getSystemProperty("spnego.login.server.module", "spnego-server");
        form.spnegoPreauthUsername = fessConfig.getSystemProperty("spnego.preauth.username", StringUtil.EMPTY);
        form.spnegoPreauthPassword =
                StringUtil.isNotBlank(fessConfig.getSystemProperty("spnego.preauth.password")) ? DUMMY_PASSWORD : StringUtil.EMPTY;
        form.spnegoAllowBasic =
                Constants.TRUE.equalsIgnoreCase(fessConfig.getSystemProperty("spnego.allow.basic", Constants.TRUE)) ? Constants.TRUE
                        : Constants.FALSE;
        form.spnegoAllowUnsecureBasic =
                Constants.TRUE.equalsIgnoreCase(fessConfig.getSystemProperty("spnego.allow.unsecure.basic", Constants.TRUE))
                        ? Constants.TRUE
                        : Constants.FALSE;
        form.spnegoPromptNtlm =
                Constants.TRUE.equalsIgnoreCase(fessConfig.getSystemProperty("spnego.prompt.ntlm", Constants.TRUE)) ? Constants.TRUE
                        : Constants.FALSE;
        form.spnegoAllowLocalhost =
                Constants.TRUE.equalsIgnoreCase(fessConfig.getSystemProperty("spnego.allow.localhost", Constants.TRUE)) ? Constants.TRUE
                        : Constants.FALSE;
        form.spnegoAllowDelegation =
                Constants.TRUE.equalsIgnoreCase(fessConfig.getSystemProperty("spnego.allow.delegation", Constants.FALSE)) ? Constants.TRUE
                        : Constants.FALSE;
        form.spnegoExcludeDirs = fessConfig.getSystemProperty("spnego.exclude.dirs", StringUtil.EMPTY);

        // Entra ID
        form.entraidClientId = StringUtil.isNotBlank(fessConfig.getSystemProperty("entraid.client.id")) ? DUMMY_PASSWORD : StringUtil.EMPTY;
        form.entraidClientSecret =
                StringUtil.isNotBlank(fessConfig.getSystemProperty("entraid.client.secret")) ? DUMMY_PASSWORD : StringUtil.EMPTY;
        form.entraidTenant = fessConfig.getSystemProperty("entraid.tenant", StringUtil.EMPTY);
        form.entraidAuthority = fessConfig.getSystemProperty("entraid.authority", "https://login.microsoftonline.com/");
        form.entraidReplyUrl = fessConfig.getSystemProperty("entraid.reply.url", StringUtil.EMPTY);
        form.entraidStateTtl = fessConfig.getSystemProperty("entraid.state.ttl", "3600");
        form.entraidDefaultGroups = fessConfig.getSystemProperty("entraid.default.groups", StringUtil.EMPTY);
        form.entraidDefaultRoles = fessConfig.getSystemProperty("entraid.default.roles", StringUtil.EMPTY);
        form.entraidPermissionFields = fessConfig.getSystemProperty("entraid.permission.fields", "mail");
        form.entraidUseDs = Constants.TRUE.equalsIgnoreCase(fessConfig.getSystemProperty("entraid.use.ds", Constants.TRUE)) ? Constants.TRUE
                : Constants.FALSE;
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

    private boolean isRagSectionVisible() {
        if (!fessConfig.isRagChatEnabled()) {
            return false;
        }
        if (!ComponentUtil.hasComponent("llmClientManager")) {
            return false;
        }
        final LlmClientManager llmClientManager = ComponentUtil.getComponent("llmClientManager");
        return llmClientManager.getClients().length > 0;
    }

    private List<Map<String, String>> getRagLlmNameItems() {
        final List<Map<String, String>> itemList = new ArrayList<>();
        final LlmClientManager llmClientManager = ComponentUtil.getComponent("llmClientManager");
        for (final LlmClient client : llmClientManager.getClients()) {
            final Map<String, String> map = new HashMap<>();
            map.put(Constants.ITEM_LABEL, client.getName());
            map.put(Constants.ITEM_VALUE, client.getName());
            itemList.add(map);
        }
        return itemList;
    }

    private static boolean isValidRagLlmName(final String name) {
        if (!ComponentUtil.getFessConfig().isRagChatEnabled()) {
            return false;
        }
        if (!ComponentUtil.hasComponent("llmClientManager")) {
            return false;
        }
        final LlmClientManager llmClientManager = ComponentUtil.getComponent("llmClientManager");
        for (final LlmClient client : llmClientManager.getClients()) {
            if (name.equals(client.getName())) {
                return true;
            }
        }
        return false;
    }

}
