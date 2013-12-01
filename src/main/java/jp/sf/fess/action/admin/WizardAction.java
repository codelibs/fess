/*
 * Copyright 2009-2013 the Fess Project and the Others.
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
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import jp.sf.fess.Constants;
import jp.sf.fess.crud.util.SAStrutsUtil;
import jp.sf.fess.db.exentity.BrowserType;
import jp.sf.fess.db.exentity.FileCrawlingConfig;
import jp.sf.fess.db.exentity.ScheduledJob;
import jp.sf.fess.db.exentity.WebCrawlingConfig;
import jp.sf.fess.form.admin.WizardForm;
import jp.sf.fess.helper.JobHelper;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.job.TriggeredJob;
import jp.sf.fess.service.BrowserTypeService;
import jp.sf.fess.service.FileCrawlingConfigService;
import jp.sf.fess.service.ScheduledJobService;
import jp.sf.fess.service.WebCrawlingConfigService;

import org.apache.commons.lang.StringUtils;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.sastruts.core.annotation.Token;
import org.codelibs.sastruts.core.exception.SSCActionMessagesException;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.util.CharUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WizardAction implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(WizardAction.class);

    @ActionForm
    @Resource
    protected WizardForm wizardForm;

    @Resource
    protected DynamicProperties crawlerProperties;

    @Resource
    protected WebCrawlingConfigService webCrawlingConfigService;

    @Resource
    protected FileCrawlingConfigService fileCrawlingConfigService;

    @Resource
    protected BrowserTypeService browserTypeService;

    @Resource
    protected SystemHelper systemHelper;

    @Resource
    protected JobHelper jobHelper;

    public String getHelpLink() {
        return systemHelper.getHelpLink("wizard");
    }

    @Execute(validator = false)
    public String index() {
        return "index.jsp";
    }

    @Token(save = true, validate = false)
    @Execute(validator = false)
    public String crawlingConfigForm() {
        return "crawlingConfig.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "crawlingConfigForm")
    public String crawlingConfig() {
        final String name = crawlingConfigInternal(
                wizardForm.crawlingConfigName, wizardForm.crawlingConfigPath);
        SAStrutsUtil.addSessionMessage(
                "success.create_crawling_config_at_wizard", name);
        return "crawlingConfigForm?redirect=true";
    }

    @Token(save = false, validate = true)
    @Execute(validator = true, input = "crawlingConfigForm")
    public String crawlingConfigNext() {
        final String name = crawlingConfigInternal(
                wizardForm.crawlingConfigName, wizardForm.crawlingConfigPath);
        SAStrutsUtil.addSessionMessage(
                "success.create_crawling_config_at_wizard", name);
        return "startCrawlingForm?redirect=true";
    }

    protected String crawlingConfigInternal(final String crawlingConfigName,
            final String crawlingConfigPath) {

        String configName = crawlingConfigName;
        String configPath = crawlingConfigPath.trim();
        if (StringUtil.isBlank(configName)) {
            configName = StringUtils.abbreviate(configPath, 30);
        }

        // normalize
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < configPath.length(); i++) {
            final char c = configPath.charAt(i);
            if (c == '\\') {
                buf.append('/');
            } else if (CharUtil.isUrlChar(c)) {
                buf.append(c);
            } else {
                try {
                    buf.append(URLEncoder.encode(String.valueOf(c),
                            Constants.UTF_8));
                } catch (final UnsupportedEncodingException e) {
                }
            }
        }
        configPath = convertCrawlingPath(buf.toString());

        final String username = systemHelper.getUsername();
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        final List<BrowserType> browserTypeList = browserTypeService
                .getBrowserTypeList();
        final List<String> browserTypeIdList = new ArrayList<String>();
        for (final BrowserType browserType : browserTypeList) {
            browserTypeIdList.add(browserType.getId().toString());
        }

        try {
            if (isWebCrawlingPath(configPath)) {
                // web
                final WebCrawlingConfig wConfig = new WebCrawlingConfig();
                wConfig.setAvailable(Constants.T);
                wConfig.setBoost(BigDecimal.ONE);
                wConfig.setCreatedBy(username);
                wConfig.setCreatedTime(now);
                if (StringUtil.isNotBlank(wizardForm.depth)) {
                    wConfig.setDepth(Integer.parseInt(wizardForm.depth));
                }
                wConfig.setExcludedDocUrls(getDefaultString(
                        "default.config.web.excludedDocUrls",
                        Constants.EMPTY_STRING));
                wConfig.setExcludedUrls(getDefaultString(
                        "default.config.web.excludedUrls",
                        Constants.EMPTY_STRING));
                wConfig.setIncludedDocUrls(getDefaultString(
                        "default.config.web.includedDocUrls",
                        Constants.EMPTY_STRING));
                wConfig.setIncludedUrls(getDefaultString(
                        "default.config.web.includedUrls",
                        Constants.EMPTY_STRING));
                wConfig.setIntervalTime(getDefaultInteger(
                        "default.config.web.intervalTime",
                        Constants.DEFAULT_INTERVAL_TIME_FOR_WEB));
                if (StringUtil.isNotBlank(wizardForm.maxAccessCount)) {
                    wConfig.setMaxAccessCount(Long
                            .parseLong(wizardForm.maxAccessCount));
                }
                wConfig.setName(configName);
                wConfig.setNumOfThread(getDefaultInteger(
                        "default.config.web.numOfThread",
                        Constants.DEFAULT_NUM_OF_THREAD_FOR_WEB));
                wConfig.setSortOrder(getDefaultInteger(
                        "default.config.web.sortOrder", 1));
                wConfig.setUpdatedBy(username);
                wConfig.setUpdatedTime(now);
                wConfig.setUrls(configPath);
                wConfig.setUserAgent(getDefaultString(
                        "default.config.web.userAgent",
                        (String) SingletonS2Container
                                .getComponent("userAgentName")));

                if (!browserTypeIdList.isEmpty()) {
                    wConfig.setBrowserTypeIds(browserTypeIdList
                            .toArray(new String[browserTypeIdList.size()]));
                }

                webCrawlingConfigService.store(wConfig);

            } else {
                // file
                final FileCrawlingConfig fConfig = new FileCrawlingConfig();
                fConfig.setAvailable(Constants.T);
                fConfig.setBoost(BigDecimal.ONE);
                fConfig.setCreatedBy(username);
                fConfig.setCreatedTime(now);
                if (StringUtil.isNotBlank(wizardForm.depth)) {
                    fConfig.setDepth(Integer.parseInt(wizardForm.depth));
                }
                fConfig.setExcludedDocPaths(getDefaultString(
                        "default.config.file.excludedDocPaths",
                        Constants.EMPTY_STRING));
                fConfig.setExcludedPaths(getDefaultString(
                        "default.config.file.excludedPaths",
                        Constants.EMPTY_STRING));
                fConfig.setIncludedDocPaths(getDefaultString(
                        "default.config.file.includedDocPaths",
                        Constants.EMPTY_STRING));
                fConfig.setIncludedPaths(getDefaultString(
                        "default.config.file.includedPaths",
                        Constants.EMPTY_STRING));
                fConfig.setIntervalTime(getDefaultInteger(
                        "default.config.file.intervalTime",
                        Constants.DEFAULT_INTERVAL_TIME_FOR_FS));
                if (StringUtil.isNotBlank(wizardForm.maxAccessCount)) {
                    fConfig.setMaxAccessCount(Long
                            .parseLong(wizardForm.maxAccessCount));
                }
                fConfig.setName(configName);
                fConfig.setNumOfThread(getDefaultInteger(
                        "default.config.file.numOfThread",
                        Constants.DEFAULT_NUM_OF_THREAD_FOR_FS));
                fConfig.setSortOrder(getDefaultInteger(
                        "default.config.file.sortOrder", 1));
                fConfig.setUpdatedBy(username);
                fConfig.setUpdatedTime(now);
                fConfig.setPaths(configPath);

                if (!browserTypeIdList.isEmpty()) {
                    fConfig.setBrowserTypeIds(browserTypeIdList
                            .toArray(new String[browserTypeIdList.size()]));
                }

                fileCrawlingConfigService.store(fConfig);
            }
            return configName;
        } catch (final Exception e) {
            logger.error("Failed to create crawling config: "
                    + wizardForm.crawlingConfigPath, e);
            throw new SSCActionMessagesException(e,
                    "errors.failed_to_create_crawling_config_at_wizard",
                    wizardForm.crawlingConfigPath);
        }
    }

    protected Integer getDefaultInteger(final String key,
            final Integer defaultValue) {
        final String value = crawlerProperties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (final NumberFormatException e) {
            }
        }
        return defaultValue;
    }

    protected Long getDefaultLong(final String key, final Long defaultValue) {
        final String value = crawlerProperties.getProperty(key);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (final NumberFormatException e) {
            }
        }
        return defaultValue;
    }

    protected String getDefaultString(final String key,
            final String defaultValue) {
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
        if (path.startsWith("http:") || path.startsWith("https:")
                || path.startsWith("smb:")) {
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

    @Token(save = true, validate = false)
    @Execute(validator = false)
    public String startCrawlingForm() {
        return "startCrawling.jsp";
    }

    @Token(save = false, validate = true)
    @Execute(validator = false)
    public String startCrawling() {
        if (!jobHelper.isCrawlProcessRunning()) {
            final ScheduledJobService scheduledJobService = SingletonS2Container
                    .getComponent(ScheduledJobService.class);
            final List<ScheduledJob> scheduledJobList = scheduledJobService
                    .getCrawloerJobList();
            for (final ScheduledJob scheduledJob : scheduledJobList) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new TriggeredJob().execute(scheduledJob);
                    }
                }).start();
            }
            SAStrutsUtil.addSessionMessage("success.start_crawl_process");
        } else {
            SAStrutsUtil
                    .addSessionMessage("success.failed_to_start_crawl_process");
        }
        return "../system/index?redirect=true";
    }
}