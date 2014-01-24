/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.db.exentity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jp.sf.fess.Constants;
import jp.sf.fess.db.bsentity.BsDataCrawlingConfig;
import jp.sf.fess.util.ParameterUtil;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.auth.NTLMScheme;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.client.S2RobotClientFactory;
import org.seasar.robot.client.http.Authentication;
import org.seasar.robot.client.http.HcHttpClient;
import org.seasar.robot.client.http.impl.AuthenticationImpl;
import org.seasar.robot.client.http.ntlm.JcifsEngine;
import org.seasar.robot.client.smb.SmbAuthentication;
import org.seasar.robot.client.smb.SmbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The entity of DATA_CRAWLING_CONFIG.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class DataCrawlingConfig extends BsDataCrawlingConfig implements
        CrawlingConfig {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(DataCrawlingConfig.class);

    private static final String S2ROBOT_WEB_HEADER_PREFIX = "s2robot.web.header.";

    private static final String S2ROBOT_WEB_AUTH = "s2robot.web.auth";

    private static final String S2ROBOT_USERAGENT = "s2robot.useragent";

    private static final String S2ROBOT_PARAM_PREFIX = "s2robot.param.";

    private static final Object S2ROBOT_FILE_AUTH = "s2robot.file.auth";

    private String[] browserTypeIds;

    private String[] labelTypeIds;

    private String[] roleTypeIds;

    protected Pattern[] includedDocPathPatterns;

    protected Pattern[] excludedDocPathPatterns;

    private Map<String, String> handlerParameterMap;

    private Map<String, String> handlerScriptMap;

    public DataCrawlingConfig() {
        super();
        setBoost(BigDecimal.ONE);
    }

    public String[] getBrowserTypeIds() {
        if (browserTypeIds == null) {
            return StringUtil.EMPTY_STRINGS;
        }
        return browserTypeIds;
    }

    public void setBrowserTypeIds(final String[] browserTypeIds) {
        this.browserTypeIds = browserTypeIds;
    }

    @Override
    public String[] getBrowserTypeValues() {
        final List<String> values = new ArrayList<String>();
        final List<DataConfigToBrowserTypeMapping> list = getDataConfigToBrowserTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final DataConfigToBrowserTypeMapping mapping : list) {
                values.add(mapping.getBrowserType().getValue());
            }
        }
        return values.toArray(new String[values.size()]);
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

    @Override
    public String[] getLabelTypeValues() {
        final List<String> values = new ArrayList<String>();
        final List<DataConfigToLabelTypeMapping> list = getDataConfigToLabelTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final DataConfigToLabelTypeMapping mapping : list) {
                values.add(mapping.getLabelType().getValue());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    public String[] getRoleTypeIds() {
        if (roleTypeIds == null) {
            return StringUtil.EMPTY_STRINGS;
        }
        return roleTypeIds;
    }

    public void setRoleTypeIds(final String[] roleTypeIds) {
        this.roleTypeIds = roleTypeIds;
    }

    @Override
    public String[] getRoleTypeValues() {
        final List<String> values = new ArrayList<String>();
        final List<DataConfigToRoleTypeMapping> list = getDataConfigToRoleTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final DataConfigToRoleTypeMapping mapping : list) {
                values.add(mapping.getRoleType().getValue());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    @Override
    public String getDocumentBoost() {
        return Float.valueOf(getBoost().floatValue()).toString();
    }

    public String getBoostValue() {
        if (_boost != null) {
            return Integer.toString(_boost.intValue());
        }
        return null;
    }

    public void setBoostValue(final String value) {
        if (value != null) {
            try {
                _boost = new BigDecimal(value);
            } catch (final Exception e) {
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
    public void initializeClientFactory(
            final S2RobotClientFactory robotClientFactory) {
        final Map<String, String> paramMap = getHandlerParameterMap();

        final Map<String, Object> factoryParamMap = new HashMap<String, Object>();
        robotClientFactory.setInitParameterMap(factoryParamMap);

        // parameters
        for (final Map.Entry<String, String> entry : paramMap.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith(S2ROBOT_PARAM_PREFIX)) {
                factoryParamMap.put(
                        key.substring(S2ROBOT_PARAM_PREFIX.length()),
                        entry.getValue());
            }
        }

        // user agent
        final String userAgent = paramMap.get(S2ROBOT_USERAGENT);
        if (StringUtil.isNotBlank(userAgent)) {
            factoryParamMap.put(HcHttpClient.USER_AGENT_PROPERTY, userAgent);
        }

        // web auth
        final String webAuthStr = paramMap.get(S2ROBOT_WEB_AUTH);
        if (StringUtil.isNotBlank(webAuthStr)) {
            final String[] webAuthNames = webAuthStr.split(",");
            final List<Authentication> basicAuthList = new ArrayList<Authentication>();
            for (final String webAuthName : webAuthNames) {
                final String scheme = paramMap.get(S2ROBOT_WEB_AUTH + "."
                        + webAuthName + ".scheme");
                final String hostname = paramMap.get(S2ROBOT_WEB_AUTH + "."
                        + webAuthName + ".host");
                final String port = paramMap.get(S2ROBOT_WEB_AUTH + "."
                        + webAuthName + ".port");
                final String realm = paramMap.get(S2ROBOT_WEB_AUTH + "."
                        + webAuthName + ".realm");
                final String username = paramMap.get(S2ROBOT_WEB_AUTH + "."
                        + webAuthName + ".username");
                final String password = paramMap.get(S2ROBOT_WEB_AUTH + "."
                        + webAuthName + ".password");

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
                    if (StringUtil.isBlank(scheme)
                            || Constants.NTLM.equals(scheme)) {
                        s = AuthScope.ANY_SCHEME;
                    }
                    authScope = new AuthScope(hostname, p, r, s);
                }

                Credentials credentials;
                if (Constants.NTLM.equals(scheme)) {
                    final String workstation = paramMap.get(S2ROBOT_WEB_AUTH
                            + "." + webAuthName + ".workstation");
                    final String domain = paramMap.get(S2ROBOT_WEB_AUTH + "."
                            + webAuthName + ".domain");
                    credentials = new NTCredentials(username,
                            password == null ? "" : password,
                            workstation == null ? "" : workstation,
                            domain == null ? "" : domain);
                } else {
                    credentials = new UsernamePasswordCredentials(username,
                            password == null ? "" : password);
                }

                basicAuthList.add(new AuthenticationImpl(authScope,
                        credentials, authScheme));
            }
            factoryParamMap.put(HcHttpClient.BASIC_AUTHENTICATIONS_PROPERTY,
                    basicAuthList.toArray(new Authentication[basicAuthList
                            .size()]));
        }

        // request header
        final List<org.seasar.robot.client.http.RequestHeader> rhList = new ArrayList<org.seasar.robot.client.http.RequestHeader>();
        int count = 1;
        String headerName = paramMap.get(S2ROBOT_WEB_HEADER_PREFIX + count
                + ".name");
        while (StringUtil.isNotBlank(headerName)) {
            final String headerValue = paramMap.get(S2ROBOT_WEB_HEADER_PREFIX
                    + count + ".value");
            rhList.add(new org.seasar.robot.client.http.RequestHeader(
                    headerName, headerValue));
            count++;
            headerName = paramMap.get(S2ROBOT_WEB_HEADER_PREFIX + count
                    + ".name");
        }
        if (!rhList.isEmpty()) {
            factoryParamMap
                    .put(HcHttpClient.REQUERT_HEADERS_PROPERTY,
                            rhList.toArray(new org.seasar.robot.client.http.RequestHeader[rhList
                                    .size()]));
        }

        // file auth
        final String fileAuthStr = paramMap.get(S2ROBOT_FILE_AUTH);
        if (StringUtil.isNotBlank(fileAuthStr)) {
            final String[] fileAuthNames = fileAuthStr.split(",");
            final List<SmbAuthentication> smbAuthList = new ArrayList<SmbAuthentication>();
            for (final String fileAuthName : fileAuthNames) {
                final String scheme = paramMap.get(S2ROBOT_FILE_AUTH + "."
                        + fileAuthName + ".scheme");
                if (Constants.SAMBA.equals(scheme)) {
                    final String domain = paramMap.get(S2ROBOT_FILE_AUTH + "."
                            + fileAuthName + ".domain");
                    final String hostname = paramMap.get(S2ROBOT_FILE_AUTH
                            + "." + fileAuthName + ".host");
                    final String port = paramMap.get(S2ROBOT_FILE_AUTH + "."
                            + fileAuthName + ".port");
                    final String username = paramMap.get(S2ROBOT_FILE_AUTH
                            + "." + fileAuthName + ".username");
                    final String password = paramMap.get(S2ROBOT_FILE_AUTH
                            + "." + fileAuthName + ".password");

                    if (StringUtil.isEmpty(username)) {
                        logger.warn("username is empty. fileAuth:"
                                + fileAuthName);
                        continue;
                    }

                    final SmbAuthentication smbAuth = new SmbAuthentication();
                    smbAuth.setDomain(domain == null ? "" : domain);
                    smbAuth.setServer(hostname);
                    if (StringUtil.isNotBlank(port)) {
                        try {
                            smbAuth.setPort(Integer.parseInt(port));
                        } catch (final NumberFormatException e) {
                            logger.warn("Failed to parse " + port, e);
                        }
                    }
                    smbAuth.setUsername(username);
                    smbAuth.setPassword(password == null ? "" : password);
                    smbAuthList.add(smbAuth);
                }
            }
            if (!smbAuthList.isEmpty()) {
                factoryParamMap.put(SmbClient.SMB_AUTHENTICATIONS_PROPERTY,
                        smbAuthList.toArray(new SmbAuthentication[smbAuthList
                                .size()]));
            }
        }

    }
}
