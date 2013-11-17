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

package jp.sf.fess.db.exentity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jp.sf.fess.Constants;
import jp.sf.fess.db.bsentity.BsWebCrawlingConfig;
import jp.sf.fess.service.RequestHeaderService;
import jp.sf.fess.service.WebAuthenticationService;
import jp.sf.fess.util.ParameterUtil;

import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.client.S2RobotClientFactory;
import org.seasar.robot.client.http.Authentication;
import org.seasar.robot.client.http.HcHttpClient;

/**
 * The entity of WEB_CRAWLING_CONFIG.
 * <p>
 * You can implement your original methods here.
 * This class remains when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class WebCrawlingConfig extends BsWebCrawlingConfig implements
        CrawlingConfig {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    private String[] browserTypeIds;

    private String[] labelTypeIds;

    private String[] roleTypeIds;

    protected volatile Pattern[] includedDocUrlPatterns;

    protected volatile Pattern[] excludedDocUrlPatterns;

    public WebCrawlingConfig() {
        super();
        setBoost(BigDecimal.ONE);
    }

    /* (non-Javadoc)
     * @see jp.sf.fess.db.exentity.CrawlingConfig#getBrowserTypeIds()
     */
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
        final List<WebConfigToBrowserTypeMapping> list = getWebConfigToBrowserTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final WebConfigToBrowserTypeMapping mapping : list) {
                values.add(mapping.getBrowserType().getValue());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    /* (non-Javadoc)
     * @see jp.sf.fess.db.exentity.CrawlingConfig#getLabelTypeIds()
     */
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
        final List<WebConfigToLabelTypeMapping> list = getWebConfigToLabelTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final WebConfigToLabelTypeMapping mapping : list) {
                values.add(mapping.getLabelType().getValue());
            }
        }
        return values.toArray(new String[values.size()]);
    }

    /* (non-Javadoc)
     * @see jp.sf.fess.db.exentity.CrawlingConfig#getRoleTypeIds()
     */
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
        final List<WebConfigToRoleTypeMapping> list = getWebConfigToRoleTypeMappingList();
        if (list != null && !list.isEmpty()) {
            for (final WebConfigToRoleTypeMapping mapping : list) {
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
        if (includedDocUrlPatterns == null || excludedDocUrlPatterns == null) {
            initDocUrlPattern();
        }

        if (includedDocUrlPatterns.length == 0
                && excludedDocUrlPatterns.length == 0) {
            return Constants.TRUE;
        }

        for (final Pattern pattern : includedDocUrlPatterns) {
            if (pattern.matcher(input).matches()) {
                return Constants.TRUE;
            }
        }

        for (final Pattern pattern : excludedDocUrlPatterns) {
            if (pattern.matcher(input).matches()) {
                return Constants.FALSE;
            }
        }

        return Constants.TRUE;
    }

    protected synchronized void initDocUrlPattern() {

        if (includedDocUrlPatterns == null) {
            if (StringUtil.isNotBlank(getIncludedDocUrls())) {
                final List<Pattern> urlPatterList = new ArrayList<Pattern>();
                final String[] urls = getIncludedDocUrls().split("[\r\n]");
                for (final String u : urls) {
                    if (StringUtil.isNotBlank(u) && !u.trim().startsWith("#")) {
                        urlPatterList.add(Pattern.compile(u.trim()));
                    }
                }
                includedDocUrlPatterns = urlPatterList
                        .toArray(new Pattern[urlPatterList.size()]);
            } else {
                includedDocUrlPatterns = new Pattern[0];
            }
        }

        if (excludedDocUrlPatterns == null) {
            if (StringUtil.isNotBlank(getExcludedDocUrls())) {
                final List<Pattern> urlPatterList = new ArrayList<Pattern>();
                final String[] urls = getExcludedDocUrls().split("[\r\n]");
                for (final String u : urls) {
                    if (StringUtil.isNotBlank(u) && !u.trim().startsWith("#")) {
                        urlPatterList.add(Pattern.compile(u.trim()));
                    }
                }
                excludedDocUrlPatterns = urlPatterList
                        .toArray(new Pattern[urlPatterList.size()]);
            } else if (includedDocUrlPatterns.length > 0) {
                excludedDocUrlPatterns = new Pattern[] { Pattern.compile(".*") };
            } else {
                excludedDocUrlPatterns = new Pattern[0];
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
        return ConfigType.WEB.getConfigId(getId());
    }

    @Override
    public void initializeClientFactory(final S2RobotClientFactory clientFactory) {
        final WebAuthenticationService webAuthenticationService = SingletonS2Container
                .getComponent(WebAuthenticationService.class);
        final RequestHeaderService requestHeaderService = SingletonS2Container
                .getComponent(RequestHeaderService.class);

        // HttpClient Parameters
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        clientFactory.setInitParameterMap(paramMap);

        final String configParam = getConfigParameter();
        if (StringUtil.isNotBlank(configParam)) {
            ParameterUtil.loadConfigParams(paramMap, configParam);
        }

        final String userAgent = getUserAgent();
        if (StringUtil.isNotBlank(userAgent)) {
            paramMap.put(HcHttpClient.USER_AGENT_PROPERTY, userAgent);
        }

        final List<WebAuthentication> webAuthList = webAuthenticationService
                .getWebAuthenticationList(getId());
        final List<Authentication> basicAuthList = new ArrayList<Authentication>();
        for (final WebAuthentication webAuth : webAuthList) {
            basicAuthList.add(webAuth.getAuthentication());
        }
        paramMap.put(HcHttpClient.BASIC_AUTHENTICATIONS_PROPERTY,
                basicAuthList.toArray(new Authentication[basicAuthList.size()]));

        // request header
        final List<RequestHeader> requestHeaderList = requestHeaderService
                .getRequestHeaderList(getId());
        final List<org.seasar.robot.client.http.RequestHeader> rhList = new ArrayList<org.seasar.robot.client.http.RequestHeader>();
        for (final RequestHeader requestHeader : requestHeaderList) {
            rhList.add(requestHeader.getS2RobotRequestHeader());
        }
        paramMap.put(HcHttpClient.REQUERT_HEADERS_PROPERTY, rhList
                .toArray(new org.seasar.robot.client.http.RequestHeader[rhList
                        .size()]));

    }

}
