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

package org.codelibs.fess.helper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.db.exentity.CrawlingConfig;
import org.codelibs.fess.db.exentity.CrawlingConfig.ConfigType;
import org.codelibs.fess.helper.UserAgentHelper.UserAgentType;
import org.codelibs.fess.service.DataCrawlingConfigService;
import org.codelibs.fess.service.FileCrawlingConfigService;
import org.codelibs.fess.service.WebCrawlingConfigService;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.robot.builder.RequestDataBuilder;
import org.codelibs.robot.client.S2RobotClient;
import org.codelibs.robot.client.S2RobotClientFactory;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.util.StreamUtil;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.Base64Util;
import org.seasar.struts.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlingConfigHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(CrawlingConfigHelper.class);

    protected final Map<String, CrawlingConfig> crawlingConfigMap = new ConcurrentHashMap<String, CrawlingConfig>();

    protected int count = 1;

    public ConfigType getConfigType(final String configId) {
        if (configId == null || configId.length() < 2) {
            return null;
        }
        final String configType = configId.substring(0, 1);
        if (ConfigType.WEB.getTypePrefix().equals(configType)) {
            return ConfigType.WEB;
        } else if (ConfigType.FILE.getTypePrefix().equals(configType)) {
            return ConfigType.FILE;
        } else if (ConfigType.DATA.getTypePrefix().equals(configType)) {
            return ConfigType.DATA;
        }
        return null;
    }

    protected Long getId(final String configId) {
        if (configId == null || configId.length() < 2) {
            return null;
        }
        try {
            final String idStr = configId.substring(1);
            return Long.parseLong(idStr);
        } catch (final NumberFormatException e) {
            // ignore
        }
        return null;
    }

    public CrawlingConfig getCrawlingConfig(final String configId) {
        final ConfigType configType = getConfigType(configId);
        if (configType == null) {
            return null;
        }
        final Long id = getId(configId);
        if (id == null) {
            return null;
        }
        switch (configType) {
            case WEB:
                final WebCrawlingConfigService webCrawlingConfigService = SingletonS2Container
                        .getComponent(WebCrawlingConfigService.class);
                return webCrawlingConfigService.getWebCrawlingConfig(id);
            case FILE:
                final FileCrawlingConfigService fileCrawlingConfigService = SingletonS2Container
                        .getComponent(FileCrawlingConfigService.class);
                return fileCrawlingConfigService.getFileCrawlingConfig(id);
            case DATA:
                final DataCrawlingConfigService dataCrawlingConfigService = SingletonS2Container
                        .getComponent(DataCrawlingConfigService.class);
                return dataCrawlingConfigService.getDataCrawlingConfig(id);
            default:
                return null;
        }
    }

    public synchronized String store(final String sessionId,
            final CrawlingConfig crawlingConfig) {
        final String sessionCountId = sessionId + "-" + count;
        crawlingConfigMap.put(sessionCountId, crawlingConfig);
        count++;
        return sessionCountId;
    }

    public void remove(final String sessionId) {
        crawlingConfigMap.remove(sessionId);
    }

    public CrawlingConfig get(final String sessionId) {
        return crawlingConfigMap.get(sessionId);
    }

    public void writeContent(final Map<String, Object> doc) {
        if (logger.isDebugEnabled()) {
            logger.debug("writing the content of: " + doc);
        }
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final Object configIdObj = doc.get(fieldHelper.configIdField);
        if (configIdObj == null) {
            throw new FessSystemException("configId is null.");
        }
        final String configId = configIdObj.toString();
        if (configId.length() < 2) {
            throw new FessSystemException("Invalid configId: " + configIdObj);
        }
        final ConfigType configType = getConfigType(configId);
        CrawlingConfig config = null;
        if (logger.isDebugEnabled()) {
            logger.debug("configType: " + configType + ", configId: "
                    + configId);
        }
        if (ConfigType.WEB == configType) {
            final WebCrawlingConfigService webCrawlingConfigService = SingletonS2Container
                    .getComponent(WebCrawlingConfigService.class);
            config = webCrawlingConfigService
                    .getWebCrawlingConfig(getId(configId));
        } else if (ConfigType.FILE == configType) {
            final FileCrawlingConfigService fileCrawlingConfigService = SingletonS2Container
                    .getComponent(FileCrawlingConfigService.class);
            config = fileCrawlingConfigService
                    .getFileCrawlingConfig(getId(configId));
        } else if (ConfigType.DATA == configType) {
            final DataCrawlingConfigService dataCrawlingConfigService = SingletonS2Container
                    .getComponent(DataCrawlingConfigService.class);
            config = dataCrawlingConfigService
                    .getDataCrawlingConfig(getId(configId));
        }
        if (config == null) {
            throw new FessSystemException("No crawlingConfig: " + configIdObj);
        }
        final String url = (String) doc.get(fieldHelper.urlField);
        final S2RobotClientFactory robotClientFactory = SingletonS2Container
                .getComponent(S2RobotClientFactory.class);
        config.initializeClientFactory(robotClientFactory);
        final S2RobotClient client = robotClientFactory.getClient(url);
        if (client == null) {
            throw new FessSystemException("No S2RobotClient: " + configIdObj
                    + ", url: " + url);
        }
        final ResponseData responseData = client.execute(RequestDataBuilder
                .newRequestData().get().url(url).build());
        final HttpServletResponse response = ResponseUtil.getResponse();
        writeFileName(response, responseData);
        writeContentType(response, responseData);
        writeNoCache(response, responseData);
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new BufferedInputStream(responseData.getResponseBody());
            os = new BufferedOutputStream(response.getOutputStream());
            StreamUtil.drain(is, os);
            os.flush();
        } catch (final IOException e) {
            if (!"ClientAbortException".equals(e.getClass().getSimpleName())) {
                throw new FessSystemException(
                        "Failed to write a content. configId: " + configIdObj
                                + ", url: " + url, e);
            }
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Finished to write " + url);
        }
    }

    protected void writeNoCache(final HttpServletResponse response,
            final ResponseData responseData) {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "Thu, 01 Dec 1994 16:00:00 GMT");
    }

    protected void writeFileName(final HttpServletResponse response,
            final ResponseData responseData) {
        final UserAgentHelper userAgentHelper = ComponentUtil
                .getUserAgentHelper();
        final UserAgentType userAgentType = userAgentHelper.getUserAgentType();
        String charset = responseData.getCharSet();
        if (charset == null) {
            charset = Constants.UTF_8;
        }
        final String name;
        final String url = responseData.getUrl();
        final int pos = url.lastIndexOf('/');
        try {
            if (pos >= 0 && pos + 1 < url.length()) {
                name = URLDecoder.decode(url.substring(pos + 1), charset);
            } else {
                name = URLDecoder.decode(url, charset);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("userAgentType: " + userAgentType + ", charset: "
                        + charset + ", name: " + name);
            }

            switch (userAgentType) {
                case IE:
                    response.setHeader(
                            "Content-Disposition",
                            "attachment; filename=\""
                                    + URLEncoder.encode(name, Constants.UTF_8)
                                    + "\"");
                    break;
                case OPERA:
                    response.setHeader(
                            "Content-Disposition",
                            "attachment; filename*=utf-8'ja'"
                                    + URLEncoder.encode(name, Constants.UTF_8));
                    break;
                case SAFARI:
                    response.setHeader("Content-Disposition",
                            "attachment; filename=\"" + name + "\"");
                    break;
                case CHROME:
                case FIREFOX:
                case OTHER:
                default:
                    response.setHeader(
                            "Content-Disposition",
                            "attachment; filename=\"=?utf-8?B?"
                                    + Base64Util.encode(name
                                            .getBytes(Constants.UTF_8))
                                    + "?=\"");
                    break;
            }
        } catch (final Exception e) {
            logger.warn("Failed to write a filename: " + responseData, e);
        }
    }

    protected void writeContentType(final HttpServletResponse response,
            final ResponseData responseData) {
        final String mimeType = responseData.getMimeType();
        if (logger.isDebugEnabled()) {
            logger.debug("mimeType: " + mimeType);
        }
        if (mimeType == null) {
            return;
        }
        if (mimeType.startsWith("text/")) {
            final String charset = response.getCharacterEncoding();
            if (charset != null) {
                response.setContentType(mimeType + "; charset=" + charset);
                return;
            }
        }
        response.setContentType(mimeType);
    }

}
