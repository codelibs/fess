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
package org.codelibs.fess.app.web.admin.wizard;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.es.config.exentity.FileConfig;
import org.codelibs.fess.es.config.exentity.ScheduledJob;
import org.codelibs.fess.es.config.exentity.WebConfig;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.job.JobManager;
import org.lastaflute.job.key.LaJobUnique;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;

public class AdminWizardAction extends FessAdminAction {

    public static final String ROLE = "admin-wizard";

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LogManager.getLogger(AdminWizardAction.class);

    // ===================================================================================
    //                                                                           Attribute
    //
    @Resource
    protected DynamicProperties systemProperties;

    @Resource
    protected WebConfigService webConfigService;

    @Resource
    protected FileConfigService fileConfigService;

    @Resource
    protected ProcessHelper processHelper;

    @Resource
    protected ScheduledJobService scheduledJobService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameWizard()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        return asIndexHtml();
    }

    private HtmlResponse asIndexHtml() {
        return asHtml(path_AdminWizard_AdminWizardJsp).useForm(IndexForm.class);
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse crawlingConfigForm() {
        saveToken();
        return asHtml(path_AdminWizard_AdminWizardConfigJsp).useForm(CrawlingConfigForm.class);
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse crawlingConfig(final CrawlingConfigForm form) {
        validate(form, messages -> {}, () -> asHtml(path_AdminWizard_AdminWizardConfigJsp));
        verifyTokenKeep(this::asIndexHtml);
        final String name = crawlingConfigInternal(form);
        saveInfo(messages -> messages.addSuccessCreateCrawlingConfigAtWizard(GLOBAL, name));
        return redirectWith(getClass(), moreUrl("crawlingConfigForm"));
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse crawlingConfigNext(final CrawlingConfigForm form) {
        validate(form, messages -> {}, () -> asHtml(path_AdminWizard_AdminWizardConfigJsp));
        verifyToken(this::asIndexHtml);
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
                wConfig.setUserAgent(getDefaultString("default.config.web.userAgent", fessConfig.getUserAgentName()));
                wConfig.setPermissions(ComponentUtil.getFessConfig().getSearchDefaultDisplayEncodedPermissions());

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
                fConfig.setPermissions(ComponentUtil.getFessConfig().getSearchDefaultDisplayEncodedPermissions());

                fileConfigService.store(fConfig);
            }
            return configName;
        } catch (final Exception e) {
            logger.warn("Failed to create crawling config: {}", form.crawlingConfigPath, e);
            throwValidationError(messages -> messages.addErrorsFailedToCreateCrawlingConfigAtWizard(GLOBAL),
                    () -> asHtml(path_AdminWizard_AdminWizardConfigJsp));
            return null;
        }
    }

    protected Integer getDefaultInteger(final String key, final Integer defaultValue) {
        final String value = systemProperties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (final NumberFormatException e) {}
        }
        return defaultValue;
    }

    protected Long getDefaultLong(final String key, final Long defaultValue) {
        final String value = systemProperties.getProperty(key);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (final NumberFormatException e) {}
        }
        return defaultValue;
    }

    protected String getDefaultString(final String key, final String defaultValue) {
        final String value = systemProperties.getProperty(key);
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
        if (path.startsWith("http:") || path.startsWith("https:") || path.startsWith("smb:") || path.startsWith("smb1:")
                || path.startsWith("ftp:") || path.startsWith("storage:")) {
            return path;
        }

        if (path.startsWith("www.")) {
            return "http://" + path;
        }

        if (path.startsWith("//")) {
            return "file://" + path;
        }
        if (path.startsWith("/")) {
            return "file:" + path;
        }
        if (!path.startsWith("file:")) {
            return "file:/" + path.replace('\\', '/');
        }
        return path;
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse startCrawlingForm() {
        saveToken();
        return asHtml(path_AdminWizard_AdminWizardStartJsp).useForm(StartCrawlingForm.class);
    }

    @Execute
    @Secured({ ROLE })
    public HtmlResponse startCrawling(final StartCrawlingForm form) {
        verifyToken(this::asIndexHtml);
        if (!processHelper.isProcessRunning()) {
            final List<ScheduledJob> scheduledJobList = scheduledJobService.getCrawlerJobList();
            final JobManager jobManager = ComponentUtil.getJobManager();
            for (final ScheduledJob scheduledJob : scheduledJobList) {
                jobManager.findJobByUniqueOf(LaJobUnique.of(scheduledJob.getId())).ifPresent(job -> {
                    job.launchNow();
                });
            }
            saveInfo(messages -> messages.addSuccessStartCrawlProcess(GLOBAL));
        } else {
            saveError(messages -> messages.addErrorsFailedToStartCrawlProcess(GLOBAL));
        }
        return redirect(AdminWizardAction.class);
    }
}
