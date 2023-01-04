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
package org.codelibs.fess.es.config.exentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.FileAuthenticationService;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.client.ftp.FtpAuthentication;
import org.codelibs.fess.crawler.client.smb.SmbAuthentication;
import org.codelibs.fess.es.config.bsentity.BsFileConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;

/**
 * @author FreeGen
 */
public class FileConfig extends BsFileConfig implements CrawlingConfig {

    private static final long serialVersionUID = 1L;

    protected volatile Pattern[] includedDocPathPatterns;

    protected volatile Pattern[] excludedDocPathPatterns;

    protected transient volatile Map<ConfigName, Map<String, String>> configParameterMap;

    protected CrawlerClientFactory crawlerClientFactory = null;

    public FileConfig() {
        setBoost(1.0f);
    }

    @Override
    public String getDocumentBoost() {
        return getBoost().toString();
    }

    @Override
    public String getIndexingTarget(final String input) {
        if (includedDocPathPatterns == null || excludedDocPathPatterns == null) {
            initDocPathPattern();
        }

        if (includedDocPathPatterns.length == 0 && excludedDocPathPatterns.length == 0) {
            return Constants.TRUE;
        }

        for (final Pattern pattern : includedDocPathPatterns) {
            if (pattern.matcher(input).matches()) {
                return Constants.TRUE;
            }
        }

        for (final Pattern pattern : excludedDocPathPatterns) {
            if (pattern.matcher(input).matches()) {
                return Constants.FALSE;
            }
        }

        return Constants.TRUE;

    }

    protected synchronized void initDocPathPattern() {
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();

        if (includedDocPathPatterns == null) {
            if (StringUtil.isNotBlank(getIncludedDocPaths())) {
                final List<Pattern> pathPatterList = new ArrayList<>();
                final String[] paths = getIncludedDocPaths().split("[\r\n]");
                for (final String u : paths) {
                    final String v = systemHelper.normalizeConfigPath(u);
                    if (StringUtil.isNotBlank(v)) {
                        pathPatterList.add(Pattern.compile(systemHelper.encodeUrlFilter(v)));
                    }
                }
                includedDocPathPatterns = pathPatterList.toArray(new Pattern[pathPatterList.size()]);
            } else {
                includedDocPathPatterns = new Pattern[0];
            }
        }

        if (excludedDocPathPatterns == null) {
            if (StringUtil.isNotBlank(getExcludedDocPaths())) {
                final List<Pattern> pathPatterList = new ArrayList<>();
                final String[] paths = getExcludedDocPaths().split("[\r\n]");
                for (final String u : paths) {
                    final String v = systemHelper.normalizeConfigPath(u);
                    if (StringUtil.isNotBlank(v)) {
                        pathPatterList.add(Pattern.compile(systemHelper.encodeUrlFilter(v)));
                    }
                }
                excludedDocPathPatterns = pathPatterList.toArray(new Pattern[pathPatterList.size()]);
            } else if (includedDocPathPatterns.length > 0) {
                excludedDocPathPatterns = new Pattern[] { Pattern.compile(".*") };
            } else {
                excludedDocPathPatterns = new Pattern[0];
            }
        }
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
            } catch (final Exception e) {}
        }
    }

    @Override
    public String getConfigId() {
        return ConfigType.FILE.getConfigId(getId());
    }

    @Override
    public CrawlerClientFactory initializeClientFactory(final Supplier<CrawlerClientFactory> creator) {
        if (crawlerClientFactory != null) {
            return crawlerClientFactory;
        }
        final CrawlerClientFactory factory = creator.get();

        final FileAuthenticationService fileAuthenticationService = ComponentUtil.getComponent(FileAuthenticationService.class);

        //  Parameters
        final Map<String, Object> paramMap = new HashMap<>();
        factory.setInitParameterMap(paramMap);

        final Map<String, String> clientConfigMap = getConfigParameterMap(ConfigName.CLIENT);
        if (clientConfigMap != null) {
            paramMap.putAll(clientConfigMap);
        }

        // auth params
        final List<FileAuthentication> fileAuthList = fileAuthenticationService.getFileAuthenticationList(getId());
        final List<SmbAuthentication> smbAuthList = new ArrayList<>();
        final List<org.codelibs.fess.crawler.client.smb1.SmbAuthentication> smb1AuthList = new ArrayList<>();
        final List<FtpAuthentication> ftpAuthList = new ArrayList<>();
        for (final FileAuthentication fileAuth : fileAuthList) {
            if (Constants.SAMBA.equals(fileAuth.getProtocolScheme())) {
                final SmbAuthentication smbAuth = new SmbAuthentication();
                final Map<String, String> map = ParameterUtil.parse(fileAuth.getParameters());
                final String domain = map.get("domain");
                smbAuth.setDomain(domain == null ? StringUtil.EMPTY : domain);
                smbAuth.setServer(fileAuth.getHostname());
                smbAuth.setPort(fileAuth.getPort() == null ? -1 : fileAuth.getPort());
                smbAuth.setUsername(fileAuth.getUsername());
                smbAuth.setPassword(fileAuth.getPassword());
                smbAuthList.add(smbAuth);

                final org.codelibs.fess.crawler.client.smb1.SmbAuthentication smb1Auth =
                        new org.codelibs.fess.crawler.client.smb1.SmbAuthentication();
                smb1Auth.setDomain(domain == null ? StringUtil.EMPTY : domain);
                smb1Auth.setServer(fileAuth.getHostname());
                smb1Auth.setPort(fileAuth.getPort() == null ? -1 : fileAuth.getPort());
                smb1Auth.setUsername(fileAuth.getUsername());
                smb1Auth.setPassword(fileAuth.getPassword());
                smb1AuthList.add(smb1Auth);
            } else if (Constants.FTP.equals(fileAuth.getProtocolScheme())) {
                final FtpAuthentication ftpAuth = new FtpAuthentication();
                ftpAuth.setServer(fileAuth.getHostname());
                ftpAuth.setPort(fileAuth.getPort() == null ? -1 : fileAuth.getPort());
                ftpAuth.setUsername(fileAuth.getUsername());
                ftpAuth.setPassword(fileAuth.getPassword());
                ftpAuthList.add(ftpAuth);
            }
        }
        paramMap.put(Param.Client.SMB_AUTHENTICATIONS, smbAuthList.toArray(new SmbAuthentication[smbAuthList.size()]));
        paramMap.put(Param.Client.SMB1_AUTHENTICATIONS,
                smb1AuthList.toArray(new org.codelibs.fess.crawler.client.smb1.SmbAuthentication[smb1AuthList.size()]));
        paramMap.put(Param.Client.FTP_AUTHENTICATIONS, ftpAuthList.toArray(new FtpAuthentication[ftpAuthList.size()]));

        crawlerClientFactory = factory;
        return factory;
    }

    @Override
    public Map<String, String> getConfigParameterMap(final ConfigName name) {
        if (configParameterMap == null) {
            configParameterMap = ParameterUtil.createConfigParameterMap(getConfigParameter());
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
    public String toString() {
        return "FileConfig [available=" + available + ", boost=" + boost + ", configParameter=" + configParameter + ", createdBy="
                + createdBy + ", createdTime=" + createdTime + ", depth=" + depth + ", excludedDocPaths=" + excludedDocPaths
                + ", excludedPaths=" + excludedPaths + ", includedDocPaths=" + includedDocPaths + ", includedPaths=" + includedPaths
                + ", intervalTime=" + intervalTime + ", timeToLive=" + timeToLive + ", maxAccessCount=" + maxAccessCount + ", name=" + name
                + ", numOfThread=" + numOfThread + ", paths=" + paths + ", permissions=" + Arrays.toString(permissions) + ", sortOrder="
                + sortOrder + ", updatedBy=" + updatedBy + ", updatedTime=" + updatedTime + "]";
    }
}
