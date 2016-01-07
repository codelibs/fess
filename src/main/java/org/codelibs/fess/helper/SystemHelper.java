/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.RoleTypeService;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.es.config.exentity.RoleType;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.lastaflute.di.core.SingletonLaContainer;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.util.LaRequestUtil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ibm.icu.util.ULocale;

public class SystemHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Set<String> adminRoleSet = new HashSet<>();

    private final Map<String, String> designJspFileNameMap = new HashMap<String, String>();

    private final AtomicBoolean forceStop = new AtomicBoolean(false);

    protected LoadingCache<String, List<Map<String, String>>> langItemsCache;

    private String filterPathEncoding;

    private String[] supportedLanguages;

    @PostConstruct
    public void init() {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        filterPathEncoding = fessConfig.getPathEncoding();
        supportedLanguages = fessConfig.getSupportedLanguagesAsArray();
        langItemsCache =
                CacheBuilder.newBuilder().maximumSize(20).expireAfterAccess(1, TimeUnit.HOURS)
                        .build(new CacheLoader<String, List<Map<String, String>>>() {
                            @Override
                            public List<Map<String, String>> load(final String key) throws Exception {
                                final ULocale uLocale = new ULocale(key);
                                final Locale displayLocale = uLocale.toLocale();
                                final List<Map<String, String>> langItems = new ArrayList<>(supportedLanguages.length);
                                final String msg = ComponentUtil.getMessageManager().getMessage(displayLocale, "labels.allLanguages");
                                final Map<String, String> defaultMap = new HashMap<>(2);
                                defaultMap.put(Constants.ITEM_LABEL, msg);
                                defaultMap.put(Constants.ITEM_VALUE, "all");
                                langItems.add(defaultMap);

                                for (final String lang : supportedLanguages) {
                                    final Locale locale = LocaleUtils.toLocale(lang);
                                    final String label = locale.getDisplayName(displayLocale);
                                    final Map<String, String> map = new HashMap<>(2);
                                    map.put(Constants.ITEM_LABEL, label);
                                    map.put(Constants.ITEM_VALUE, lang);
                                    langItems.add(map);
                                }
                                return langItems;
                            }
                        });
    }

    public String getUsername() {
        final RequestManager requestManager = ComponentUtil.getRequestManager();
        return requestManager.findUserBean(FessUserBean.class).map(user -> {
            return user.getUserId();
        }).orElse(Constants.GUEST_USER);
    }

    public Date getCurrentTime() {
        return new Date();
    }

    public long getCurrentTimeAsLong() {
        return System.currentTimeMillis();
    }

    public LocalDateTime getCurrentTimeAsLocalDateTime() {
        return LocalDateTime.now();
    }

    public String getLogFilePath() {
        final String value = System.getProperty("fess.log.path");
        if (value != null) {
            return value;
        } else {
            final String userDir = System.getProperty("user.dir");
            final File targetDir = new File(userDir, "target");
            return new File(targetDir, "logs").getAbsolutePath();
        }
    }

    public String encodeUrlFilter(final String path) {
        if (filterPathEncoding == null || path == null) {
            return path;
        }

        try {
            final StringBuilder buf = new StringBuilder();
            for (int i = 0; i < path.length(); i++) {
                final char c = path.charAt(i);
                if (CharUtil.isUrlChar(c) || c == '^' || c == '{' || c == '}' || c == '|' || c == '\\') {
                    buf.append(c);
                } else {
                    buf.append(URLEncoder.encode(String.valueOf(c), filterPathEncoding));
                }
            }
            return buf.toString();
        } catch (final UnsupportedEncodingException e) {
            return path;
        }
    }

    public String getHelpLink(final String name) {
        final String url = ComponentUtil.getFessConfig().getOnlineHelpBaseLink() + name + "-guide.html";
        return LaRequestUtil
                .getOptionalRequest()
                .map(request -> {
                    final Locale locale = request.getLocale();
                    if (locale != null) {
                        final String lang = locale.getLanguage();
                        if (ComponentUtil.getFessConfig().isOnlineHelpSupportedLang(lang)) {
                            return url.replaceFirst("\\{lang\\}", lang).replaceFirst("\\{version\\}",
                                    Constants.MAJOR_VERSION + "." + Constants.MINOR_VERSION);
                        }
                    }
                    return getDefaultHelpLink(url);
                }).orElse(getDefaultHelpLink(url));
    }

    private String getDefaultHelpLink(final String url) {
        return url.replaceFirst("/\\{lang\\}/", "/").replaceFirst("\\{version\\}", Constants.MAJOR_VERSION + "." + Constants.MINOR_VERSION);
    }

    public void addDesignJspFileName(final String key, final String value) {
        designJspFileNameMap.put(key, value);
    }

    public String getDesignJspFileName(final String fileName) {
        return designJspFileNameMap.get(fileName);
    }

    public Set<String> getAdminRoleSet() {
        return adminRoleSet;
    }

    public void addAdminRoles(final Collection<String> adminRoles) {
        adminRoleSet.addAll(adminRoles);
    }

    public Set<String> getAuthenticatedRoleSet() {
        final RoleTypeService roleTypeService = SingletonLaContainer.getComponent(RoleTypeService.class);
        final List<RoleType> roleTypeList = roleTypeService.getRoleTypeList();

        final Set<String> roleList = new HashSet<>(roleTypeList.size() + adminRoleSet.size());
        for (final RoleType roleType : roleTypeList) {
            roleList.add(roleType.getValue());
        }

        // system roles
        roleList.addAll(adminRoleSet);

        return roleList;
    }

    public boolean isForceStop() {
        return forceStop.get();
    }

    public void setForceStop(final boolean b) {
        forceStop.set(true);
    }

    public String generateDocId(final Map<String, Object> map) {
        return UUID.randomUUID().toString().replace("-", StringUtil.EMPTY);
    }

    public String abbreviateLongText(final String str) {
        return StringUtils.abbreviate(str, ComponentUtil.getFessConfig().getMaxLogOutputLengthAsInteger().intValue());
    }

    public String normalizeLang(final String value) {
        if (StringUtil.isBlank(value)) {
            return null;
        }

        final String localeName = value.trim().toLowerCase(Locale.ENGLISH).replace("-", "_");

        for (final String supportedLang : supportedLanguages) {
            if (localeName.startsWith(supportedLang.toLowerCase(Locale.ENGLISH))) {
                return supportedLang;
            }
        }
        return null;
    }

    public List<Map<String, String>> getLanguageItems(final Locale locale) {
        try {
            final String localeStr = locale.toString();
            return langItemsCache.get(localeStr);
        } catch (final ExecutionException e) {
            final List<Map<String, String>> langItems = new ArrayList<>(supportedLanguages.length);
            final String msg = ComponentUtil.getMessageManager().getMessage(locale, "labels.allLanguages");
            final Map<String, String> defaultMap = new HashMap<>(2);
            defaultMap.put(Constants.ITEM_LABEL, msg);
            defaultMap.put(Constants.ITEM_VALUE, "all");
            langItems.add(defaultMap);
            return langItems;
        }
    }

}
