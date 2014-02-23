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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jp.sf.fess.Constants;
import jp.sf.fess.db.bsentity.BsFileCrawlingConfig;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.service.FileAuthenticationService;
import jp.sf.fess.util.ComponentUtil;
import jp.sf.fess.util.ParameterUtil;

import org.codelibs.core.util.StringUtil;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.robot.client.S2RobotClientFactory;
import org.seasar.robot.client.smb.SmbAuthentication;
import org.seasar.robot.client.smb.SmbClient;

/**
 * The entity of FILE_CRAWLING_CONFIG.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class FileCrawlingConfig extends BsFileCrawlingConfig implements
        CrawlingConfig {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    private String[] browserTypeIds;

    private String[] labelTypeIds;

    private String[] roleTypeIds;

    protected volatile Pattern[] includedDocPathPatterns;

    protected volatile Pattern[] excludedDocPathPatterns;

    protected volatile Map<ConfigName, Map<String, String>> configParameterMap;

    public FileCrawlingConfig() {
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
        final List<FileConfigToBrowserTypeMapping> list = getFileConfigToBrowserTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final FileConfigToBrowserTypeMapping mapping : list) {
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
        final List<FileConfigToLabelTypeMapping> list = getFileConfigToLabelTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final FileConfigToLabelTypeMapping mapping : list) {
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
        final List<FileConfigToRoleTypeMapping> list = getFileConfigToRoleTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final FileConfigToRoleTypeMapping mapping : list) {
                values.add(mapping.getRoleType().getValue());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    @Override
    public String getDocumentBoost() {
        return Float.valueOf(getBoost().floatValue()).toString();
    }

    @Override
    public String getIndexingTarget(final String input) {
        if (includedDocPathPatterns == null || excludedDocPathPatterns == null) {
            initDocPathPattern();
        }

        if (includedDocPathPatterns.length == 0
                && excludedDocPathPatterns.length == 0) {
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
                final List<Pattern> pathPatterList = new ArrayList<Pattern>();
                final String[] paths = getIncludedDocPaths().split("[\r\n]");
                for (final String u : paths) {
                    if (StringUtil.isNotBlank(u) && !u.trim().startsWith("#")) {
                        pathPatterList.add(Pattern.compile(systemHelper
                                .encodeUrlFilter(u.trim())));
                    }
                }
                includedDocPathPatterns = pathPatterList
                        .toArray(new Pattern[pathPatterList.size()]);
            } else {
                includedDocPathPatterns = new Pattern[0];
            }
        }

        if (excludedDocPathPatterns == null) {
            if (StringUtil.isNotBlank(getExcludedDocPaths())) {
                final List<Pattern> pathPatterList = new ArrayList<Pattern>();
                final String[] paths = getExcludedDocPaths().split("[\r\n]");
                for (final String u : paths) {
                    if (StringUtil.isNotBlank(u) && !u.trim().startsWith("#")) {
                        pathPatterList.add(Pattern.compile(systemHelper
                                .encodeUrlFilter(u.trim())));
                    }
                }
                excludedDocPathPatterns = pathPatterList
                        .toArray(new Pattern[pathPatterList.size()]);
            } else if (includedDocPathPatterns.length > 0) {
                excludedDocPathPatterns = new Pattern[] { Pattern.compile(".*") };
            } else {
                excludedDocPathPatterns = new Pattern[0];
            }
        }
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
    public String getConfigId() {
        return ConfigType.FILE.getConfigId(getId());
    }

    @Override
    public void initializeClientFactory(final S2RobotClientFactory clientFactory) {
        final FileAuthenticationService fileAuthenticationService = SingletonS2Container
                .getComponent(FileAuthenticationService.class);

        //  Parameters
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        clientFactory.setInitParameterMap(paramMap);

        final Map<String, String> clientConfigMap = getConfigParameterMap(ConfigName.CLIENT);
        if (clientConfigMap != null) {
            paramMap.putAll(clientConfigMap);
        }

        // auth params
        final List<FileAuthentication> fileAuthList = fileAuthenticationService
                .getFileAuthenticationList(getId());
        final List<SmbAuthentication> smbAuthList = new ArrayList<SmbAuthentication>();
        for (final FileAuthentication fileAuth : fileAuthList) {
            if (Constants.SAMBA.equals(fileAuth.getProtocolScheme())) {
                final SmbAuthentication smbAuth = new SmbAuthentication();
                final Map<String, String> map = ParameterUtil.parse(fileAuth
                        .getParameters());
                final String domain = map.get("domain");
                smbAuth.setDomain(domain == null ? StringUtil.EMPTY : domain);
                smbAuth.setServer(fileAuth.getHostname());
                smbAuth.setPort(fileAuth.getPort());
                smbAuth.setUsername(fileAuth.getUsername());
                smbAuth.setPassword(fileAuth.getPassword());
                smbAuthList.add(smbAuth);
            }
        }
        paramMap.put(SmbClient.SMB_AUTHENTICATIONS_PROPERTY,
                smbAuthList.toArray(new SmbAuthentication[smbAuthList.size()]));

    }

    @Override
    public Map<String, String> getConfigParameterMap(final ConfigName name) {
        if (configParameterMap == null) {
            final Map<ConfigName, Map<String, String>> map = new HashMap<>();
            final Map<String, String> clientConfigMap = new HashMap<>();
            final Map<String, String> xpathConfigMap = new HashMap<>();
            final Map<String, String> scriptConfigMap = new HashMap<>();
            map.put(ConfigName.CLIENT, clientConfigMap);
            map.put(ConfigName.XPATH, xpathConfigMap);
            map.put(ConfigName.SCRIPT, scriptConfigMap);
            for (final Map.Entry<String, String> entry : ParameterUtil.parse(
                    getConfigParameter()).entrySet()) {
                final String key = entry.getKey();
                if (key.startsWith(CLIENT_PREFIX)) {
                    clientConfigMap.put(key.substring(CLIENT_PREFIX.length()),
                            entry.getValue());
                } else if (key.startsWith(XPATH_PREFIX)) {
                    xpathConfigMap.put(key.substring(XPATH_PREFIX.length()),
                            entry.getValue());
                } else if (key.startsWith(SCRIPT_PREFIX)) {
                    scriptConfigMap.put(key.substring(SCRIPT_PREFIX.length()),
                            entry.getValue());
                }
            }

            configParameterMap = map;
        }

        final Map<String, String> configMap = configParameterMap.get(name);
        if (configMap == null) {
            return Collections.emptyMap();
        }
        return configMap;
    }
}
