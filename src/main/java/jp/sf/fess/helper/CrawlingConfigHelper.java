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

package jp.sf.fess.helper;

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

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;
import jp.sf.fess.db.exentity.CrawlingConfig;
import jp.sf.fess.helper.UserAgentHelper.UserAgentType;
import jp.sf.fess.service.DataCrawlingConfigService;
import jp.sf.fess.service.FileCrawlingConfigService;
import jp.sf.fess.service.WebCrawlingConfigService;

import org.apache.commons.io.IOUtils;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.Base64Util;
import org.seasar.robot.client.S2RobotClient;
import org.seasar.robot.client.S2RobotClientFactory;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.util.StreamUtil;
import org.seasar.struts.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlingConfigHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(CrawlingConfigHelper.class);

    protected final Map<String, CrawlingConfig> crawlingConfigMap = new ConcurrentHashMap<String, CrawlingConfig>();

    protected int count = 1;

    protected String configIdField = "cid_s_s";

    protected String urlField = "url";

    public String getUrlField() {
        return urlField;
    }

    public void setUrlField(final String urlField) {
        this.urlField = urlField;
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

    public String getConfigIdField() {
        return configIdField;
    }

    public void setConfigIdField(final String configIdField) {
        this.configIdField = configIdField;
    }

    public void writeContent(final Map<String, Object> doc) {
        final Object configIdObj = doc.get(configIdField);
        if (configIdObj == null || configIdObj.toString().length() < 2) {
            throw new FessSystemException("Invalid configId: " + configIdObj);
        }
        final String configType = configIdObj.toString().substring(0, 1);
        final String idStr = configIdObj.toString().substring(1);
        CrawlingConfig config = null;
        if (Constants.WEB_CONFIG_ID_PREFIX.equals(configType)) {
            final WebCrawlingConfigService webCrawlingConfigService = SingletonS2Container
                    .getComponent(WebCrawlingConfigService.class);
            config = webCrawlingConfigService.getWebCrawlingConfig(Long
                    .parseLong(idStr));
        } else if (Constants.FILE_CONFIG_ID_PREFIX.equals(configType)) {
            final FileCrawlingConfigService fileCrawlingConfigService = SingletonS2Container
                    .getComponent(FileCrawlingConfigService.class);
            config = fileCrawlingConfigService.getFileCrawlingConfig(Long
                    .parseLong(idStr));
        } else if (Constants.DATA_CONFIG_ID_PREFIX.equals(configType)) {
            final DataCrawlingConfigService dataCrawlingConfigService = SingletonS2Container
                    .getComponent(DataCrawlingConfigService.class);
            config = dataCrawlingConfigService.getDataCrawlingConfig(Long
                    .parseLong(idStr));
        }
        if (config == null) {
            throw new FessSystemException("No crawlingConfig: " + configIdObj);
        }
        final String url = (String) doc.get(urlField);
        final S2RobotClientFactory robotClientFactory = SingletonS2Container
                .getComponent(S2RobotClientFactory.class);
        config.initializeClientFactory(robotClientFactory);
        final S2RobotClient client = robotClientFactory.getClient(url);
        if (client == null) {
            throw new FessSystemException("No S2RobotClient: " + configIdObj
                    + ", url: " + url);
        }
        final ResponseData responseData = client.doGet(url);
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
            throw new FessSystemException(
                    "Failed to write a content. configId: " + configIdObj
                            + ", url: " + url, e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
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
        final UserAgentHelper userAgentHelper = SingletonS2Container
                .getComponent(UserAgentHelper.class);
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
                response.setHeader(
                        "Content-Disposition",
                        "attachment; filename=\"=?utf-8?B?"
                                + Base64Util.encode(name
                                        .getBytes(Constants.UTF_8)) + "?=\"");
                break;
            }
        } catch (final Exception e) {
            logger.warn("Failed to write a filename: " + responseData, e);
        }
    }

    protected void writeContentType(final HttpServletResponse response,
            final ResponseData responseData) {
        final String mimeType = responseData.getMimeType();
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
