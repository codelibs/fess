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
package org.codelibs.fess.opensearch.config.exentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.client.ftp.FtpAuthentication;
import org.codelibs.fess.crawler.client.ftp.FtpClient;
import org.codelibs.fess.crawler.client.http.HcHttpClient;
import org.codelibs.fess.crawler.client.http.config.CredentialsConfig;
import org.codelibs.fess.crawler.client.http.config.CredentialsConfig.CredentialsType;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig;
import org.codelibs.fess.crawler.client.http.config.WebAuthenticationConfig.AuthSchemeType;
import org.codelibs.fess.crawler.client.smb.SmbAuthentication;
import org.codelibs.fess.crawler.client.smb.SmbClient;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.opensearch.config.bsentity.BsDataConfig;
import org.codelibs.fess.util.ParameterUtil;

/**
 * @author FreeGen
 */
public class DataConfig extends BsDataConfig implements CrawlingConfig {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(DataConfig.class);

    private static final String CRAWLER_WEB_PREFIX = "crawler.web.";

    private static final String CRAWLER_WEB_HEADER_PREFIX = CRAWLER_WEB_PREFIX + "header.";

    private static final String CRAWLER_WEB_AUTH = CRAWLER_WEB_PREFIX + "auth";

    private static final String CRAWLER_USERAGENT = "crawler.useragent";

    private static final String CRAWLER_PARAM_PREFIX = "crawler.param.";

    private static final Object CRAWLER_FILE_AUTH = "crawler.file.auth";

    protected Pattern[] includedDocPathPatterns;

    protected Pattern[] excludedDocPathPatterns;

    protected Map<String, String> handlerParameterMap;

    protected Map<String, String> handlerScriptMap;

    protected CrawlerClientFactory crawlerClientFactory = null;

    protected Map<ConfigName, Map<String, String>> configParameterMap;

    public DataConfig() {
        setBoost(1.0f);
    }

    @Override
    public String getDocumentBoost() {
        return getBoost().toString();
    }

    public String getBoostValue() {
        if (boost != null) {
            return boost.toString();
        }
        return null;
    }

    public void setBoostValue(final String value) {
        if (value != null) {
            try {
                boost = Float.parseFloat(value);
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Invalid boost value, keeping current: value={}, error={}", value, e.getMessage());
                }
            }
        }
    }

    @Override
    public String getIndexingTarget(final String input) {
        // always return true
        return Constants.TRUE;
    }

    @Override
    public String getConfigId() {
        return ConfigType.DATA.getConfigId(getId());
    }

    public Map<String, String> getHandlerParameterMap() {
        if (handlerParameterMap == null) {
            handlerParameterMap = ParameterUtil.parse(getHandlerParameter());
        }
        return handlerParameterMap;
    }

    public Map<String, String> getHandlerScriptMap() {
        if (handlerScriptMap == null) {
            handlerScriptMap = ParameterUtil.parse(getHandlerScript());
        }
        return handlerScriptMap;
    }

    @Override
    public CrawlerClientFactory initializeClientFactory(final Supplier<CrawlerClientFactory> creator) {
        if (crawlerClientFactory != null) {
            return crawlerClientFactory;
        }
        final CrawlerClientFactory factory = creator.get();

        final Map<String, String> paramMap = getHandlerParameterMap();

        final Map<String, Object> factoryParamMap = new HashMap<>();
        factory.setInitParameterMap(factoryParamMap);

        // parameters
        for (final Map.Entry<String, String> entry : paramMap.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith(CRAWLER_PARAM_PREFIX)) {
                factoryParamMap.put(key.substring(CRAWLER_PARAM_PREFIX.length()), entry.getValue());
            }
        }

        // user agent
        final String userAgent = paramMap.get(CRAWLER_USERAGENT);
        if (StringUtil.isNotBlank(userAgent)) {
            factoryParamMap.put(HcHttpClient.USER_AGENT_PROPERTY, userAgent);
        }

        // web auth
        final String webAuthStr = paramMap.get(CRAWLER_WEB_AUTH);
        if (StringUtil.isNotBlank(webAuthStr)) {
            final String[] webAuthNames = webAuthStr.split(",");
            final List<WebAuthenticationConfig> authConfigList = new ArrayList<>();
            for (final String webAuthName : webAuthNames) {
                authConfigList.add(createWebAuthConfig(paramMap, webAuthName));
            }
            factoryParamMap.put(HcHttpClient.AUTHENTICATIONS_PROPERTY,
                    authConfigList.toArray(new WebAuthenticationConfig[authConfigList.size()]));
        }

        // request header
        final List<org.codelibs.fess.crawler.client.http.RequestHeader> rhList = new ArrayList<>();
        int count = 1;
        String headerName = paramMap.get(CRAWLER_WEB_HEADER_PREFIX + count + ".name");
        while (StringUtil.isNotBlank(headerName)) {
            final String headerValue = paramMap.get(CRAWLER_WEB_HEADER_PREFIX + count + ".value");
            rhList.add(new org.codelibs.fess.crawler.client.http.RequestHeader(headerName, headerValue));
            count++;
            headerName = paramMap.get(CRAWLER_WEB_HEADER_PREFIX + count + ".name");
        }
        if (!rhList.isEmpty()) {
            factoryParamMap.put(HcHttpClient.REQUEST_HEADERS_PROPERTY,
                    rhList.toArray(new org.codelibs.fess.crawler.client.http.RequestHeader[rhList.size()]));
        }

        // proxy credentials
        final String proxyHost = paramMap.get(CRAWLER_WEB_PREFIX + "proxyHost");
        final String proxyPort = paramMap.get(CRAWLER_WEB_PREFIX + "proxyPort");
        if (StringUtil.isNotBlank(proxyHost) && StringUtil.isNotBlank(proxyPort)) {
            factoryParamMap.put(HcHttpClient.PROXY_HOST_PROPERTY, proxyHost);
            factoryParamMap.put(HcHttpClient.PROXY_PORT_PROPERTY, proxyPort);
            final String proxyUsername = paramMap.get(CRAWLER_WEB_PREFIX + "proxyUsername");
            final String proxyPassword = paramMap.get(CRAWLER_WEB_PREFIX + "proxyPassword");
            if (proxyUsername != null && proxyPassword != null) {
                factoryParamMap.put(HcHttpClient.PROXY_CREDENTIALS_PROPERTY, new UsernamePasswordCredentials(proxyUsername, proxyPassword));
            }
        } else {
            initializeDefaultHttpProxy(factoryParamMap);
        }

        // file auth
        final String fileAuthStr = paramMap.get(CRAWLER_FILE_AUTH);
        if (StringUtil.isNotBlank(fileAuthStr)) {
            final String[] fileAuthNames = fileAuthStr.split(",");
            final List<SmbAuthentication> smbAuthList = new ArrayList<>();
            final List<org.codelibs.fess.crawler.client.smb1.SmbAuthentication> smb1AuthList = new ArrayList<>();
            final List<FtpAuthentication> ftpAuthList = new ArrayList<>();
            for (final String fileAuthName : fileAuthNames) {
                final String scheme = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".scheme");
                if (Constants.SAMBA.equals(scheme)) {
                    final String domain = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".domain");
                    final String hostname = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".host");
                    final String port = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".port");
                    final String username = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".username");
                    final String password = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".password");

                    if (StringUtil.isEmpty(username)) {
                        logger.warn("username is empty. fileAuth:{}", fileAuthName);
                        continue;
                    }

                    final SmbAuthentication smbAuth = new SmbAuthentication();
                    smbAuth.setDomain(domain == null ? StringUtil.EMPTY : domain);
                    smbAuth.setServer(hostname);
                    if (StringUtil.isNotBlank(port)) {
                        try {
                            smbAuth.setPort(Integer.parseInt(port));
                        } catch (final NumberFormatException e) {
                            logger.warn("Failed to parse {}", port, e);
                        }
                    }
                    smbAuth.setUsername(username);
                    smbAuth.setPassword(password == null ? StringUtil.EMPTY : password);
                    smbAuthList.add(smbAuth);

                    final org.codelibs.fess.crawler.client.smb1.SmbAuthentication smb1Auth =
                            new org.codelibs.fess.crawler.client.smb1.SmbAuthentication();
                    smb1Auth.setDomain(domain == null ? StringUtil.EMPTY : domain);
                    smb1Auth.setServer(hostname);
                    if (StringUtil.isNotBlank(port)) {
                        try {
                            smb1Auth.setPort(Integer.parseInt(port));
                        } catch (final NumberFormatException e) {
                            logger.warn("Failed to parse {}", port, e);
                        }
                    }
                    smb1Auth.setUsername(username);
                    smb1Auth.setPassword(password == null ? StringUtil.EMPTY : password);
                    smb1AuthList.add(smb1Auth);
                } else if (Constants.FTP.equals(scheme)) {
                    final String hostname = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".host");
                    final String port = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".port");
                    final String username = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".username");
                    final String password = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".password");

                    if (StringUtil.isEmpty(username)) {
                        logger.warn("username is empty. fileAuth:{}", fileAuthName);
                        continue;
                    }

                    final FtpAuthentication ftpAuth = new FtpAuthentication();
                    ftpAuth.setServer(hostname);
                    if (StringUtil.isNotBlank(port)) {
                        try {
                            ftpAuth.setPort(Integer.parseInt(port));
                        } catch (final NumberFormatException e) {
                            logger.warn("Failed to parse {}", port, e);
                        }
                    }
                    ftpAuth.setUsername(username);
                    ftpAuth.setPassword(password == null ? StringUtil.EMPTY : password);
                    ftpAuthList.add(ftpAuth);
                }
            }
            if (!smbAuthList.isEmpty()) {
                factoryParamMap.put(SmbClient.SMB_AUTHENTICATIONS_PROPERTY, smbAuthList.toArray(new SmbAuthentication[smbAuthList.size()]));
            }
            if (!smb1AuthList.isEmpty()) {
                factoryParamMap.put(org.codelibs.fess.crawler.client.smb1.SmbClient.SMB_AUTHENTICATIONS_PROPERTY,
                        smb1AuthList.toArray(new org.codelibs.fess.crawler.client.smb1.SmbAuthentication[smb1AuthList.size()]));
            }
            if (!ftpAuthList.isEmpty()) {
                factoryParamMap.put(FtpClient.FTP_AUTHENTICATIONS_PROPERTY, ftpAuthList.toArray(new FtpAuthentication[ftpAuthList.size()]));
            }
        }

        crawlerClientFactory = factory;
        return factory;
    }

    private WebAuthenticationConfig createWebAuthConfig(final Map<String, String> paramMap, final String webAuthName) {
        final String prefix = CRAWLER_WEB_AUTH + "." + webAuthName + ".";
        final String scheme = paramMap.get(prefix + "scheme");

        final String username = paramMap.get(prefix + "username");
        if (StringUtil.isEmpty(username)) {
            throw new CrawlerSystemException("username is empty. webAuth:" + webAuthName);
        }

        final WebAuthenticationConfig config = new WebAuthenticationConfig();

        // host/port/realm: only set if not blank (null means "any" - AuthScope.ANY equivalent)
        final String host = paramMap.get(prefix + "host");
        if (StringUtil.isNotBlank(host)) {
            config.setHost(host);
        }

        final String port = paramMap.get(prefix + "port");
        if (StringUtil.isNotBlank(port)) {
            try {
                config.setPort(Integer.parseInt(port));
            } catch (final NumberFormatException e) {
                logger.warn("Failed to parse port: {}", port, e);
            }
        }

        final String realm = paramMap.get(prefix + "realm");
        if (StringUtil.isNotBlank(realm)) {
            config.setRealm(realm);
        }

        // AuthSchemeType
        if (Constants.BASIC.equals(scheme)) {
            config.setAuthSchemeType(AuthSchemeType.BASIC);
        } else if (Constants.DIGEST.equals(scheme)) {
            config.setAuthSchemeType(AuthSchemeType.DIGEST);
        } else if (Constants.NTLM.equals(scheme)) {
            config.setAuthSchemeType(AuthSchemeType.NTLM);
            // Pass jcifs.* properties via formParameters for NTLM configuration
            final Map<String, String> jcifsParams = paramMap.entrySet()
                    .stream()
                    .filter(e -> e.getKey().startsWith("jcifs."))
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            if (!jcifsParams.isEmpty()) {
                config.setNtlmParameters(jcifsParams);
            }
        } else if (Constants.FORM.equals(scheme)) {
            config.setAuthSchemeType(AuthSchemeType.FORM);
            final Map<String, String> formParams = paramMap.entrySet()
                    .stream()
                    .filter(e -> e.getKey().startsWith(prefix))
                    .collect(Collectors.toMap(e -> e.getKey().substring(prefix.length()), Entry::getValue));
            config.setFormParameters(formParams);
        }

        // Credentials
        final CredentialsConfig credentials = new CredentialsConfig();
        credentials.setUsername(username);
        final String password = paramMap.get(prefix + "password");
        credentials.setPassword(password == null ? StringUtil.EMPTY : password);
        if (Constants.NTLM.equals(scheme)) {
            credentials.setType(CredentialsType.NTLM);
            credentials.setDomain(paramMap.get(prefix + "domain"));
            credentials.setWorkstation(paramMap.get(prefix + "workstation"));
        }
        config.setCredentials(credentials);

        return config;
    }

    @Override
    public Map<String, String> getConfigParameterMap(final ConfigName name) {
        if (configParameterMap == null) {
            configParameterMap = ParameterUtil.createConfigParameterMap(getHandlerParameter());
        }

        final Map<String, String> configMap = configParameterMap.get(name);
        if (configMap == null) {
            return Collections.emptyMap();
        }
        return configMap;
    }

    @Override
    public String getId() {
        return asDocMeta().id();
    }

    public void setId(final String id) {
        asDocMeta().id(id);
    }

    public Long getVersionNo() {
        return asDocMeta().version();
    }

    public void setVersionNo(final Long version) {
        asDocMeta().version(version);
    }

    @Override
    public Integer getTimeToLive() {
        String value = getHandlerParameterMap().get("time_to_live");
        if (StringUtil.isBlank(value)) {
            value = getHandlerParameterMap().get("timeToLive"); // TODO remove
            if (StringUtil.isBlank(value)) {
                return null;
            }
            logger.warn("timeToLive is deprecated. Please use time_to_live.");
        }
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid format: {}", value, e);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "DataConfig [available=" + available + ", boost=" + boost + ", createdBy=" + createdBy + ", createdTime=" + createdTime
                + ", handlerName=" + handlerName + ", handlerParameter=" + handlerParameter + ", handlerScript=" + handlerScript + ", name="
                + name + ", permissions=" + Arrays.toString(permissions) + ", sortOrder=" + sortOrder + ", updatedBy=" + updatedBy
                + ", updatedTime=" + updatedTime + "]";
    }
}
