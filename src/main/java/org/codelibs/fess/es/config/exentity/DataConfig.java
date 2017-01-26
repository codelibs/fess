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
package org.codelibs.fess.es.config.exentity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.auth.NTLMScheme;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.client.ftp.FtpAuthentication;
import org.codelibs.fess.crawler.client.ftp.FtpClient;
import org.codelibs.fess.crawler.client.http.Authentication;
import org.codelibs.fess.crawler.client.http.HcHttpClient;
import org.codelibs.fess.crawler.client.http.impl.AuthenticationImpl;
import org.codelibs.fess.crawler.client.http.ntlm.JcifsEngine;
import org.codelibs.fess.crawler.client.smb.SmbAuthentication;
import org.codelibs.fess.crawler.client.smb.SmbClient;
import org.codelibs.fess.es.config.bsentity.BsDataConfig;
import org.codelibs.fess.es.config.exbhv.DataConfigToLabelBhv;
import org.codelibs.fess.es.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ParameterUtil;
import org.dbflute.cbean.result.ListResultBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author FreeGen
 */
public class DataConfig extends BsDataConfig implements CrawlingConfig {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DataConfig.class);

    private static final String CRAWLER_WEB_HEADER_PREFIX = "crawler.web.header.";

    private static final String CRAWLER_WEB_AUTH = "crawler.web.auth";

    private static final String CRAWLER_USERAGENT = "crawler.useragent";

    private static final String CRAWLER_PARAM_PREFIX = "crawler.param.";

    private static final Object CRAWLER_FILE_AUTH = "crawler.file.auth";

    private String[] labelTypeIds;

    protected Pattern[] includedDocPathPatterns;

    protected Pattern[] excludedDocPathPatterns;

    private Map<String, String> handlerParameterMap;

    private Map<String, String> handlerScriptMap;

    private volatile List<LabelType> labelTypeList;

    public DataConfig() {
        super();
        setBoost(1.0f);
    }

    public String[] getLabelTypeIds() {
        if (labelTypeIds == null) {
            return StringUtil.EMPTY_STRINGS;
        }
        return labelTypeIds;
    }

    public void setLabelTypeIds(final String[] labelTypeIds) {
        this.labelTypeIds = labelTypeIds;
    }

    public List<LabelType> getLabelTypeList() {
        if (labelTypeList == null) {
            synchronized (this) {
                if (labelTypeList == null) {
                    final FessConfig fessConfig = ComponentUtil.getFessConfig();
                    final DataConfigToLabelBhv dataConfigToLabelBhv = ComponentUtil.getComponent(DataConfigToLabelBhv.class);
                    final ListResultBean<DataConfigToLabel> mappingList = dataConfigToLabelBhv.selectList(cb -> {
                        cb.query().setDataConfigId_Equal(getId());
                        cb.specify().columnLabelTypeId();
                        cb.paging(fessConfig.getPageLabeltypeMaxFetchSizeAsInteger().intValue(), 1);
                    });
                    final List<String> labelIdList = new ArrayList<>();
                    for (final DataConfigToLabel mapping : mappingList) {
                        labelIdList.add(mapping.getLabelTypeId());
                    }
                    final LabelTypeBhv labelTypeBhv = ComponentUtil.getComponent(LabelTypeBhv.class);
                    labelTypeList = labelIdList.isEmpty() ? Collections.emptyList() : labelTypeBhv.selectList(cb -> {
                        cb.query().setId_InScope(labelIdList);
                        cb.query().addOrderBy_SortOrder_Asc();
                        cb.fetchFirst(fessConfig.getPageLabeltypeMaxFetchSizeAsInteger());
                    });
                }
            }
        }
        return labelTypeList;
    }

    @Override
    public String[] getLabelTypeValues() {
        final List<LabelType> list = getLabelTypeList();
        final List<String> labelValueList = new ArrayList<>(list.size());
        for (final LabelType labelType : list) {
            labelValueList.add(labelType.getValue());
        }
        return labelValueList.toArray(new String[labelValueList.size()]);
    }

    @Override
    public String getDocumentBoost() {
        return Float.valueOf(getBoost().floatValue()).toString();
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
    public Map<String, Object> initializeClientFactory(final CrawlerClientFactory crawlerClientFactory) {
        final Map<String, String> paramMap = getHandlerParameterMap();

        final Map<String, Object> factoryParamMap = new HashMap<>();
        crawlerClientFactory.setInitParameterMap(factoryParamMap);

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
            final List<Authentication> basicAuthList = new ArrayList<>();
            for (final String webAuthName : webAuthNames) {
                final String scheme = paramMap.get(CRAWLER_WEB_AUTH + "." + webAuthName + ".scheme");
                final String hostname = paramMap.get(CRAWLER_WEB_AUTH + "." + webAuthName + ".host");
                final String port = paramMap.get(CRAWLER_WEB_AUTH + "." + webAuthName + ".port");
                final String realm = paramMap.get(CRAWLER_WEB_AUTH + "." + webAuthName + ".realm");
                final String username = paramMap.get(CRAWLER_WEB_AUTH + "." + webAuthName + ".username");
                final String password = paramMap.get(CRAWLER_WEB_AUTH + "." + webAuthName + ".password");

                if (StringUtil.isEmpty(username)) {
                    logger.warn("username is empty. webAuth:" + webAuthName);
                    continue;
                }

                AuthScheme authScheme = null;
                if (Constants.BASIC.equals(scheme)) {
                    authScheme = new BasicScheme();
                } else if (Constants.DIGEST.equals(scheme)) {
                    authScheme = new DigestScheme();
                } else if (Constants.NTLM.equals(scheme)) {
                    authScheme = new NTLMScheme(new JcifsEngine());
                }
                // TODO FORM

                AuthScope authScope;
                if (StringUtil.isBlank(hostname)) {
                    authScope = AuthScope.ANY;
                } else {
                    int p = AuthScope.ANY_PORT;
                    if (StringUtil.isNotBlank(port)) {
                        try {
                            p = Integer.parseInt(port);
                        } catch (final NumberFormatException e) {
                            logger.warn("Failed to parse " + port, e);
                        }
                    }

                    String r = realm;
                    if (StringUtil.isBlank(realm)) {
                        r = AuthScope.ANY_REALM;
                    }

                    String s = scheme;
                    if (StringUtil.isBlank(scheme) || Constants.NTLM.equals(scheme)) {
                        s = AuthScope.ANY_SCHEME;
                    }
                    authScope = new AuthScope(hostname, p, r, s);
                }

                Credentials credentials;
                if (Constants.NTLM.equals(scheme)) {
                    final String workstation = paramMap.get(CRAWLER_WEB_AUTH + "." + webAuthName + ".workstation");
                    final String domain = paramMap.get(CRAWLER_WEB_AUTH + "." + webAuthName + ".domain");
                    credentials =
                            new NTCredentials(username, password == null ? StringUtil.EMPTY : password,
                                    workstation == null ? StringUtil.EMPTY : workstation, domain == null ? StringUtil.EMPTY : domain);
                } else {
                    credentials = new UsernamePasswordCredentials(username, password == null ? StringUtil.EMPTY : password);
                }

                basicAuthList.add(new AuthenticationImpl(authScope, credentials, authScheme));
            }
            factoryParamMap.put(HcHttpClient.BASIC_AUTHENTICATIONS_PROPERTY,
                    basicAuthList.toArray(new Authentication[basicAuthList.size()]));
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
            factoryParamMap.put(HcHttpClient.REQUERT_HEADERS_PROPERTY,
                    rhList.toArray(new org.codelibs.fess.crawler.client.http.RequestHeader[rhList.size()]));
        }

        // file auth
        final String fileAuthStr = paramMap.get(CRAWLER_FILE_AUTH);
        if (StringUtil.isNotBlank(fileAuthStr)) {
            final String[] fileAuthNames = fileAuthStr.split(",");
            final List<SmbAuthentication> smbAuthList = new ArrayList<>();
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
                        logger.warn("username is empty. fileAuth:" + fileAuthName);
                        continue;
                    }

                    final SmbAuthentication smbAuth = new SmbAuthentication();
                    smbAuth.setDomain(domain == null ? StringUtil.EMPTY : domain);
                    smbAuth.setServer(hostname);
                    if (StringUtil.isNotBlank(port)) {
                        try {
                            smbAuth.setPort(Integer.parseInt(port));
                        } catch (final NumberFormatException e) {
                            logger.warn("Failed to parse " + port, e);
                        }
                    }
                    smbAuth.setUsername(username);
                    smbAuth.setPassword(password == null ? StringUtil.EMPTY : password);
                    smbAuthList.add(smbAuth);
                } else if (Constants.FTP.equals(scheme)) {
                    final String hostname = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".host");
                    final String port = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".port");
                    final String username = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".username");
                    final String password = paramMap.get(CRAWLER_FILE_AUTH + "." + fileAuthName + ".password");

                    if (StringUtil.isEmpty(username)) {
                        logger.warn("username is empty. fileAuth:" + fileAuthName);
                        continue;
                    }

                    final FtpAuthentication ftpAuth = new FtpAuthentication();
                    ftpAuth.setServer(hostname);
                    if (StringUtil.isNotBlank(port)) {
                        try {
                            ftpAuth.setPort(Integer.parseInt(port));
                        } catch (final NumberFormatException e) {
                            logger.warn("Failed to parse " + port, e);
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
            if (!ftpAuthList.isEmpty()) {
                factoryParamMap.put(FtpClient.FTP_AUTHENTICATIONS_PROPERTY, ftpAuthList.toArray(new FtpAuthentication[ftpAuthList.size()]));
            }
        }

        return factoryParamMap;
    }

    @Override
    public Map<String, String> getConfigParameterMap(final ConfigName name) {
        return Collections.emptyMap();
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
        final String value = getHandlerParameterMap().get("timeToLive");
        if (StringUtil.isBlank(value)) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid format: " + value, e);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "DataConfig [available=" + available + ", boost=" + boost + ", createdBy=" + createdBy + ", createdTime=" + createdTime
                + ", handlerName=" + handlerName + ", handlerParameter=" + handlerParameter + ", handlerScript=" + handlerScript
                + ", name=" + name + ", permissions=" + Arrays.toString(permissions) + ", sortOrder=" + sortOrder + ", updatedBy="
                + updatedBy + ", updatedTime=" + updatedTime + "]";
    }

}
