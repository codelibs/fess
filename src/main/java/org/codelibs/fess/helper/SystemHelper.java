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
package org.codelibs.fess.helper;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.validation.FessActionValidator;
import org.lastaflute.core.message.supplier.UserMessagesCreator;
import org.lastaflute.web.TypicalAction;
import org.lastaflute.web.response.next.HtmlNext;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.util.LaServletContextUtil;
import org.lastaflute.web.validation.ActionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ibm.icu.util.ULocale;

public class SystemHelper {
    private static final Logger logger = LoggerFactory.getLogger(SystemHelper.class);

    protected final Map<String, String> designJspFileNameMap = new LinkedHashMap<>();

    protected final AtomicBoolean forceStop = new AtomicBoolean(false);

    protected LoadingCache<String, List<Map<String, String>>> langItemsCache;

    protected String filterPathEncoding;

    protected String[] supportedLanguages;

    protected List<Runnable> shutdownHookList = new ArrayList<>();

    protected Random random = new SecureRandom();

    protected AtomicInteger previousClusterState = new AtomicInteger(0);

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

        ComponentUtil.doInitProcesses(p -> p.run());
    }

    @PreDestroy
    public void destroy() {
        shutdownHookList.forEach(action -> {
            try {
                action.run();
            } catch (final Exception e) {
                logger.warn("Failed to process shutdown task.", e);
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
            final StringBuilder buf = new StringBuilder(path.length() + 100);
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
        return getHelpUrl(url);
    }

    protected String getHelpUrl(final String url) {
        final Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        if (locale != null) {
            final String lang = locale.getLanguage();
            if (ComponentUtil.getFessConfig().isOnlineHelpSupportedLang(lang)) {
                return url.replaceFirst("\\{lang\\}", lang).replaceFirst("\\{version\\}",
                        Constants.MAJOR_VERSION + "." + Constants.MINOR_VERSION);
            }
        }
        return getDefaultHelpLink(url);
    }

    protected String getDefaultHelpLink(final String url) {
        return url.replaceFirst("/\\{lang\\}/", "/").replaceFirst("\\{version\\}", Constants.MAJOR_VERSION + "." + Constants.MINOR_VERSION);
    }

    public void addDesignJspFileName(final String key, final String value) {
        designJspFileNameMap.put(key, value);
    }

    public String getDesignJspFileName(final String fileName) {
        return designJspFileNameMap.get(fileName);
    }

    @SuppressWarnings("unchecked")
    public Pair<String, String>[] getDesignJspFileNames() {
        return designJspFileNameMap.entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue())).toArray(n -> new Pair[n]);
    }

    public void refreshDesignJspFiles() {
        final ServletContext servletContext = LaServletContextUtil.getServletContext();
        stream(ComponentUtil.getFessConfig().getVirtualHostPaths()).of(
                stream -> stream.filter(s -> s != null && !s.equals("/")).forEach(
                        key -> {
                            designJspFileNameMap
                                    .entrySet()
                                    .stream()
                                    .forEach(
                                            e -> {
                                                final File jspFile =
                                                        new File(servletContext.getRealPath("/WEB-INF/view" + key + "/" + e.getValue()));
                                                if (!jspFile.exists()) {
                                                    jspFile.getParentFile().mkdirs();
                                                    final File baseJspFile =
                                                            new File(servletContext.getRealPath("/WEB-INF/view/" + e.getValue()));
                                                    try {
                                                        Files.copy(baseJspFile.toPath(), jspFile.toPath());
                                                    } catch (final IOException ex) {
                                                        logger.warn("Could not copy from " + baseJspFile.getAbsolutePath() + " to "
                                                                + jspFile.getAbsolutePath(), ex);
                                                    }
                                                }
                                            });
                        }));
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

    public void sleep(final int sec) {
        try {
            Thread.sleep(sec * 1000L);
        } catch (final InterruptedException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Interrupted.", e);
            }
        }
    }

    public void addShutdownHook(final Runnable hook) {
        shutdownHookList.add(hook);
    }

    public String getHostname() {
        final Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME")) {
            return env.get("COMPUTERNAME");
        } else if (env.containsKey("HOSTNAME")) {
            return env.get("HOSTNAME");
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (final UnknownHostException e) {
            logger.debug("Unknown hostname.", e);
        }
        return "Unknown";
    }

    public void setupAdminHtmlData(final TypicalAction action, final ActionRuntime runtime) {
        runtime.registerData("developmentMode", ComponentUtil.getFessEsClient().isEmbedded());
        final String url = ComponentUtil.getFessConfig().getOnlineHelpInstallation();
        runtime.registerData("installationLink", getHelpUrl(url));
    }

    public String getSearchRoleByUser(final String name) {
        return createSearchRole(ComponentUtil.getFessConfig().getRoleSearchUserPrefix(), name);
    }

    public String getSearchRoleByGroup(final String name) {
        return createSearchRole(ComponentUtil.getFessConfig().getRoleSearchGroupPrefix(), name);
    }

    public String getSearchRoleByRole(final String name) {
        return createSearchRole(ComponentUtil.getFessConfig().getRoleSearchRolePrefix(), name);
    }

    protected String createSearchRole(final String type, final String name) {
        return type + name;
    }

    public void reloadConfiguration() {
        ComponentUtil.getFessEsClient().refresh();
        ComponentUtil.getLabelTypeHelper().init();
        ComponentUtil.getPathMappingHelper().init();
        ComponentUtil.getSuggestHelper().init();
        ComponentUtil.getPopularWordHelper().init();
        ComponentUtil.getJobManager().reboot();
        ComponentUtil.getLdapManager().updateConfig();
        ComponentUtil.getRelatedContentHelper().update();
        ComponentUtil.getRelatedQueryHelper().update();
    }

    public String updateConfiguration() {
        final StringBuilder buf = new StringBuilder();
        buf.append("Label: ").append(ComponentUtil.getLabelTypeHelper().update()).append("\n");
        buf.append("PathMapping: ").append(ComponentUtil.getPathMappingHelper().update()).append("\n");
        buf.append("RelatedContent: ").append(ComponentUtil.getRelatedContentHelper().update()).append("\n");
        buf.append("RelatedQuery: ").append(ComponentUtil.getRelatedQueryHelper().update()).append("\n");
        return buf.toString();
    }

    public String generateAccessToken() {
        return RandomStringUtils.random(ComponentUtil.getFessConfig().getApiAccessTokenLengthAsInteger().intValue(), 0, 0, true, true,
                null, random);
    }

    public void setRandom(final Random random) {
        this.random = random;
    }

    public boolean isChangedClusterState(final int status) {
        return previousClusterState.getAndSet(status) != status;
    }

    public ActionValidator<FessMessages> createValidator(final RequestManager requestManager,
            final UserMessagesCreator<FessMessages> messagesCreator, final Class<?>[] runtimeGroups) {
        return new FessActionValidator<>(requestManager, messagesCreator, runtimeGroups);
    }

    public String getVirtualHostBasePath(final String s, final HtmlNext page) {
        return StringUtil.isBlank(s) ? StringUtil.EMPTY : "/" + s;
    }

}
