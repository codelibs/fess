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
package org.codelibs.fess.app.web.admin.wizard;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.admin.dashboard.AdminDashboardAction;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.es.config.exentity.FileConfig;
import org.codelibs.fess.es.config.exentity.ScheduledJob;
import org.codelibs.fess.es.config.exentity.WebConfig;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.job.TriggeredJob;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminWizardAction extends FessAdminAction {

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LoggerFactory.getLogger(AdminWizardAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //
    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected WebConfigService webConfigService;

    @Resource
    protected FileConfigService fileConfigService;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected JobHelper jobHelper;

    @Resource
    protected ScheduledJobService scheduledJobService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink("wizard"));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    @Execute
    public HtmlResponse index() {
        return asHtml(path_AdminWizard_IndexJsp).useForm(IndexForm.class);
    }

    @Execute
    //(token = TxToken.SAVE)
    public HtmlResponse crawlingConfigForm() {
        return asHtml(path_AdminWizard_CrawlingConfigJsp).useForm(CrawlingConfigForm.class);
    }

    @Execute
    //(token = TxToken.VALIDATE)
    public HtmlResponse crawlingConfig(final CrawlingConfigForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_AdminWizard_CrawlingConfigJsp);
        });
        final String name = crawlingConfigInternal(form);
        saveInfo(messages -> messages.addSuccessCreateCrawlingConfigAtWizard(GLOBAL, name));
        return redirectWith(getClass(), moreUrl("crawlingConfigForm"));
    }

    @Execute
    //(token = TxToken.VALIDATE)
    public HtmlResponse crawlingConfigNext(final CrawlingConfigForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_AdminWizard_CrawlingConfigJsp);
        });
        final String name = crawlingConfigInternal(form);
        saveInfo(messages -> messages.addSuccessCreateCrawlingConfigAtWizard(GLOBAL, name));
        return redirectWith(getClass(), moreUrl("startCrawlingForm"));
    }

    protected String crawlingConfigInternal(final CrawlingConfigForm form) {

        String configName = form.crawlingConfigName;
        String configPath = form.crawlingConfigPath.trim();
        if (StringUtil.isBlank(configName)) {
            configName = StringUtils.abbreviate(configPath, 30);
        }

        // normalize
        final StringBuilder buf = new StringBuilder(1000);
        for (int i = 0; i < configPath.length(); i++) {
            final char c = configPath.charAt(i);
            if (c == '\\') {
                buf.append('/');
            } else if (c == ' ') {
                buf.append("%20");
            } else if (CharUtil.isUrlChar(c)) {
                buf.append(c);
            } else {
                try {
                    buf.append(URLEncoder.encode(String.valueOf(c), Constants.UTF_8));
                } catch (final UnsupportedEncodingException e) {}
            }
        }
        configPath = convertCrawlingPath(buf.toString());

        final String username = systemHelper.getUsername();
        final long now = systemHelper.getCurrentTimeAsLong();

        try {
            if (isWebCrawlingPath(configPath)) {
                // web
                final WebConfig wConfig = new WebConfig();
                wConfig.setAvailable(Constants.T);
                wConfig.setBoost(1.0f);
                wConfig.setCreatedBy(username);
                wConfig.setCreatedTime(now);
                if (form.depth != null) {
                    wConfig.setDepth(form.depth);
                }
                wConfig.setExcludedDocUrls(getDefaultString("default.config.web.excludedDocUrls", StringUtil.EMPTY));
                wConfig.setExcludedUrls(getDefaultString("default.config.web.excludedUrls", StringUtil.EMPTY));
                wConfig.setIncludedDocUrls(getDefaultString("default.config.web.includedDocUrls", StringUtil.EMPTY));
                wConfig.setIncludedUrls(getDefaultString("default.config.web.includedUrls", StringUtil.EMPTY));
                wConfig.setIntervalTime(getDefaultInteger("default.config.web.intervalTime", Constants.DEFAULT_INTERVAL_TIME_FOR_WEB));
                if (form.maxAccessCount != null) {
                    wConfig.setMaxAccessCount(form.maxAccessCount);
                }
                wConfig.setName(configName);
                wConfig.setNumOfThread(getDefaultInteger("default.config.web.numOfThread", Constants.DEFAULT_NUM_OF_THREAD_FOR_WEB));
                wConfig.setSortOrder(getDefaultInteger("default.config.web.sortOrder", 1));
                wConfig.setUpdatedBy(username);
                wConfig.setUpdatedTime(now);
                wConfig.setUrls(configPath);
                wConfig.setUserAgent(getDefaultString("default.config.web.userAgent", ComponentUtil.getUserAgentName()));

                webConfigService.store(wConfig);

            } else {
                // file
                final FileConfig fConfig = new FileConfig();
                fConfig.setAvailable(Constants.T);
                fConfig.setBoost(1.0f);
                fConfig.setCreatedBy(username);
                fConfig.setCreatedTime(now);
                if (form.depth != null) {
                    fConfig.setDepth(form.depth);
                }
                fConfig.setExcludedDocPaths(getDefaultString("default.config.file.excludedDocPaths", StringUtil.EMPTY));
                fConfig.setExcludedPaths(getDefaultString("default.config.file.excludedPaths", StringUtil.EMPTY));
                fConfig.setIncludedDocPaths(getDefaultString("default.config.file.includedDocPaths", StringUtil.EMPTY));
                fConfig.setIncludedPaths(getDefaultString("default.config.file.includedPaths", StringUtil.EMPTY));
                fConfig.setIntervalTime(getDefaultInteger("default.config.file.intervalTime", Constants.DEFAULT_INTERVAL_TIME_FOR_FS));
                if (form.maxAccessCount != null) {
                    fConfig.setMaxAccessCount(form.maxAccessCount);
                }
                fConfig.setName(configName);
                fConfig.setNumOfThread(getDefaultInteger("default.config.file.numOfThread", Constants.DEFAULT_NUM_OF_THREAD_FOR_FS));
                fConfig.setSortOrder(getDefaultInteger("default.config.file.sortOrder", 1));
                fConfig.setUpdatedBy(username);
                fConfig.setUpdatedTime(now);
                fConfig.setPaths(configPath);

                fileConfigService.store(fConfig);
            }
            return configName;
        } catch (final Exception e) {
            logger.error("Failed to create crawling config: " + form.crawlingConfigPath, e);
            throwValidationError(messages -> messages.addErrorsFailedToCreateCrawlingConfigAtWizard(GLOBAL), () -> {
                return asHtml(path_AdminWizard_CrawlingConfigJsp);
            });
            return null;
        }
    }

    protected Integer getDefaultInteger(final String key, final Integer defaultValue) {
        final String value = crawlerProperties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (final NumberFormatException e) {}
        }
        return defaultValue;
    }

    protected Long getDefaultLong(final String key, final Long defaultValue) {
        final String value = crawlerProperties.getProperty(key);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (final NumberFormatException e) {}
        }
        return defaultValue;
    }

    protected String getDefaultString(final String key, final String defaultValue) {
        final String value = crawlerProperties.getProperty(key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    protected boolean isWebCrawlingPath(final String path) {
        if (path.startsWith("http:") || path.startsWith("https:")) {
            return true;
        }

        return false;
    }

    protected String convertCrawlingPath(final String path) {
        if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("smb:")) {
            return path;
        }

        if (path.startsWith("www.")) {
            return "http://" + path;
        }

        if (path.startsWith("//")) {
            return "file://" + path;
        } else if (path.startsWith("/")) {
            return "file:" + path;
        } else if (!path.startsWith("file:")) {
            return "file:/" + path.replace('\\', '/');
        }
        return path;
    }

    @Execute
    //(token = TxToken.SAVE)
    public HtmlResponse startCrawlingForm() {
        return asHtml(path_AdminWizard_StartCrawlingJsp).useForm(StartCrawlingForm.class);
    }

    @Execute
    //(token = TxToken.VALIDATE)
    public HtmlResponse startCrawling(final StartCrawlingForm form) {
        if (!jobHelper.isCrawlProcessRunning()) {
            final List<ScheduledJob> scheduledJobList = scheduledJobService.getCrawlerJobList();
            for (final ScheduledJob scheduledJob : scheduledJobList) {
                new Thread(() -> new TriggeredJob().execute(scheduledJob)).start();
            }
            saveInfo(messages -> messages.addSuccessStartCrawlProcess(GLOBAL));
        } else {
            saveError(messages -> messages.addErrorsFailedToStartCrawlProcess(GLOBAL));
        }
        return redirect(AdminDashboardAction.class);
    }
}