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
package org.codelibs.fess.helper;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.mylasta.action.FessMessages;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.GsaConfigParser;
import org.codelibs.fess.util.ParameterUtil;
import org.codelibs.fess.util.ResourceUtil;
import org.codelibs.fess.validation.FessActionValidator;
import org.lastaflute.core.message.supplier.UserMessagesCreator;
import org.lastaflute.web.TypicalAction;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.util.LaServletContextUtil;
import org.lastaflute.web.validation.ActionValidator;
import org.opensearch.monitor.os.OsProbe;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ibm.icu.util.ULocale;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Helper class for system-level operations and utilities.
 * This class provides methods for managing system properties, handling JSP files,
 * normalizing configurations, and other system-related tasks.
 */
public class SystemHelper {

    /**
     * Constructs a new system helper.
     */
    public SystemHelper() {
        // do nothing
    }

    private static final Logger logger = LogManager.getLogger(SystemHelper.class);

    /** A map of design JSP file names. */
    protected final Map<String, String> designJspFileNameMap = new LinkedHashMap<>();

    /** A flag to indicate if the system should be forcefully stopped. */
    protected final AtomicBoolean forceStop = new AtomicBoolean(false);

    /** A cache for language items. */
    protected LoadingCache<String, List<Map<String, String>>> langItemsCache;

    /** The encoding for filtering paths. */
    protected String filterPathEncoding;

    /** An array of supported language codes. */
    protected String[] supportedLanguages;

    /** A list of shutdown hooks to be executed on system shutdown. */
    protected List<Runnable> shutdownHookList = new ArrayList<>();

    /** The previous state of the cluster. */
    protected AtomicInteger previousClusterState = new AtomicInteger(0);

    /** The version of the Fess application. */
    protected String version;

    /** The major version number. */
    protected int majorVersion;

    /** The minor version number. */
    protected int minorVersion;

    /** The product version string. */
    protected String productVersion;

    /** The end-of-life timestamp for the application. */
    protected long eolTime;

    private short systemCpuPercent;

    private long systemCpuCheckTime;

    private long systemCpuCheckInterval = 1000L;

    /** A map of listeners for configuration updates. */
    protected Map<String, Supplier<String>> updateConfigListenerMap = new HashMap<>();

    /** A set of names of threads that are currently waiting. */
    protected Set<String> waitingThreadNames = Collections.synchronizedSet(new HashSet<>());

    /**
     * Initializes the SystemHelper.
     * This method sets up system properties, caches, and other initial configurations.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(2027, 4 - 1, 1); // EOL Date
        eolTime = cal.getTimeInMillis();
        if (isEoled()) {
            logger.error("Your system is out of support. See https://fess.codelibs.org/eol.html");
        }
        updateSystemProperties();
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        filterPathEncoding = fessConfig.getPathEncoding();
        supportedLanguages = fessConfig.getSupportedLanguagesAsArray();
        langItemsCache = CacheBuilder.newBuilder()
                .maximumSize(20)
                .expireAfterAccess(1, TimeUnit.HOURS)
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

        ComponentUtil.doInitProcesses(Runnable::run);

        parseProjectProperties(ResourceUtil.getProjectPropertiesFile());

        updateConfigListenerMap.put("Label", () -> Integer.toString(ComponentUtil.getLabelTypeHelper().load()));
        updateConfigListenerMap.put("PathMapping", () -> Integer.toString(ComponentUtil.getPathMappingHelper().load()));
        updateConfigListenerMap.put("RelatedContent", () -> Integer.toString(ComponentUtil.getRelatedContentHelper().load()));
        updateConfigListenerMap.put("RelatedQuery", () -> Integer.toString(ComponentUtil.getRelatedQueryHelper().load()));
        updateConfigListenerMap.put("KeyMatch", () -> Integer.toString(ComponentUtil.getKeyMatchHelper().load()));
    }

    /**
     * Parses project properties from the given path.
     *
     * @param propPath The path to the project properties file.
     * @throws FessSystemException if the properties file cannot be parsed.
     */
    protected void parseProjectProperties(final Path propPath) {
        try (final InputStream in = Files.newInputStream(propPath)) {
            final Properties prop = new Properties();
            prop.load(in);
            version = prop.getProperty("fess.version", "0.0.0");
            final String[] values = version.split("\\.");
            majorVersion = Integer.parseInt(values[0]);
            minorVersion = Integer.parseInt(values[1]);
            productVersion = majorVersion + "." + minorVersion;
            System.setProperty("fess.version", version);
            System.setProperty("fess.product.version", productVersion);
        } catch (final Exception e) {
            throw new FessSystemException("Failed to parse project.properties.", e);
        }
    }

    /**
     * Destroys the SystemHelper and executes shutdown hooks.
     */
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

    /**
     * Gets the username of the current user.
     *
     * @return The username, or "guest" if not logged in.
     */
    public String getUsername() {
        return getRequestManager().findUserBean(FessUserBean.class).map(FessUserBean::getUserId).orElse(Constants.GUEST_USER);
    }

    /**
     * Gets the request manager.
     *
     * @return The request manager.
     */
    protected RequestManager getRequestManager() {
        return ComponentUtil.getRequestManager();
    }

    /**
     * Gets the current time as a Date object.
     *
     * @return The current time.
     */
    public Date getCurrentTime() {
        return new Date(getCurrentTimeAsLong());
    }

    /**
     * Gets the current time in milliseconds.
     *
     * @return The current time in milliseconds.
     */
    public long getCurrentTimeAsLong() {
        return System.currentTimeMillis();
    }

    /**
     * Gets the current time as a LocalDateTime object.
     *
     * @return The current time.
     */
    public LocalDateTime getCurrentTimeAsLocalDateTime() {
        final Instant instant = Instant.ofEpochMilli(getCurrentTimeAsLong());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Gets the path to the log file.
     *
     * @return The log file path.
     */
    public String getLogFilePath() {
        final String value = System.getProperty("fess.log.path");
        if (value != null) {
            return value;
        }
        final String userDir = System.getProperty("user.dir");
        final File targetDir = new File(userDir, "target");
        return new File(targetDir, "logs").getAbsolutePath();
    }

    /**
     * Encodes a URL path for filtering.
     *
     * @param path The path to encode.
     * @return The encoded path.
     */
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

    /**
     * Normalizes a configuration path.
     *
     * @param path The path to normalize.
     * @return The normalized path.
     */
    public String normalizeConfigPath(final String path) {

        if (StringUtil.isBlank(path)) {
            return StringUtils.EMPTY;
        }

        final String p = path.trim();
        if (p.startsWith("#")) {
            return StringUtils.EMPTY;
        }

        if (p.startsWith(GsaConfigParser.CONTAINS)) {
            return ".*" + Pattern.quote(p.substring(GsaConfigParser.CONTAINS.length())) + ".*";
        }

        if (p.startsWith(GsaConfigParser.REGEXP)) {
            return p.substring(GsaConfigParser.REGEXP.length());
        }

        if (p.startsWith(GsaConfigParser.REGEXP_CASE)) {
            return p.substring(GsaConfigParser.REGEXP_CASE.length());
        }

        if (p.startsWith(GsaConfigParser.REGEXP_IGNORE_CASE)) {
            return "(?i)" + p.substring(GsaConfigParser.REGEXP_IGNORE_CASE.length());
        }

        return p;
    }

    /**
     * Gets the link to the Fess forum.
     *
     * @return The forum link.
     */
    public String getForumLink() {
        final String url = ComponentUtil.getFessConfig().getForumLink();
        if (StringUtil.isBlank(url)) {
            return null;
        }
        String target = null;
        final Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        if (locale != null) {
            final String lang = locale.getLanguage();
            if (ComponentUtil.getFessConfig().isOnlineHelpSupportedLang(lang)) {
                target = lang.toUpperCase(Locale.ROOT);
            }
        }
        return url.replaceFirst("\\{lang\\}", target == null ? "EN" : target);
    }

    /**
     * Gets the help link for a specific page.
     *
     * @param name The name of the help page.
     * @return The help link.
     */
    public String getHelpLink(final String name) {
        final String url = ComponentUtil.getFessConfig().getOnlineHelpBaseLink() + name + "-guide.html";
        return getHelpUrl(url);
    }

    /**
     * Gets the help URL for a given base URL.
     *
     * @param url The base URL.
     * @return The localized help URL.
     */
    protected String getHelpUrl(final String url) {
        final Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        if (locale != null) {
            final String lang = locale.getLanguage();
            if (ComponentUtil.getFessConfig().isOnlineHelpSupportedLang(lang)) {
                return url.replaceFirst("\\{lang\\}", lang).replaceFirst("\\{version\\}", majorVersion + "." + minorVersion);
            }
        }
        return getDefaultHelpLink(url);
    }

    /**
     * Gets the default help link for a given URL.
     *
     * @param url The URL.
     * @return The default help link.
     */
    protected String getDefaultHelpLink(final String url) {
        return url.replaceFirst("/\\{lang\\}/", "/").replaceFirst("\\{version\\}", majorVersion + "." + minorVersion);
    }

    /**
     * Adds a design JSP file name to the map.
     *
     * @param key   The key for the JSP file.
     * @param value The file name.
     */
    public void addDesignJspFileName(final String key, final String value) {
        designJspFileNameMap.put(key, value);
    }

    /**
     * Gets the design JSP file name for a given key.
     *
     * @param fileName The key for the JSP file.
     * @return The file name.
     */
    public String getDesignJspFileName(final String fileName) {
        return designJspFileNameMap.get(fileName);
    }

    /**
     * Gets an array of design JSP file names.
     *
     * @return An array of pairs of keys and file names.
     */
    @SuppressWarnings("unchecked")
    public Pair<String, String>[] getDesignJspFileNames() {
        return designJspFileNameMap.entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue())).toArray(n -> new Pair[n]);
    }

    /**
     * Refreshes the design JSP files for all virtual hosts.
     *
     * @return A list of paths to the refreshed files.
     */
    public List<Path> refreshDesignJspFiles() {
        final List<Path> fileList = new ArrayList<>();
        stream(ComponentUtil.getVirtualHostHelper().getVirtualHostPaths())
                .of(stream -> stream.filter(s -> s != null && !"/".equals(s)).forEach(key -> {
                    designJspFileNameMap.entrySet().stream().forEach(e -> {
                        final File jspFile = getDesignJspFile("/WEB-INF/view" + key + "/" + e.getValue());
                        if (!jspFile.exists()) {
                            jspFile.getParentFile().mkdirs();
                            final File baseJspFile = getDesignJspFile("/WEB-INF/view/" + e.getValue());
                            try {
                                final Path jspPath = jspFile.toPath();
                                Files.copy(baseJspFile.toPath(), jspPath);
                                fileList.add(jspPath);
                            } catch (final IOException ex) {
                                logger.warn("Could not copy from {} to {}", baseJspFile.getAbsolutePath(), jspFile.getAbsolutePath(), ex);
                            }
                        }
                    });
                }));
        return fileList;
    }

    /**
     * Gets a design JSP file for a given path.
     *
     * @param path The path to the JSP file.
     * @return The JSP file.
     */
    protected File getDesignJspFile(final String path) {
        return new File(LaServletContextUtil.getServletContext().getRealPath(path));
    }

    /**
     * Checks if the system is in a force-stop state.
     *
     * @return true if the system is force-stopping, false otherwise.
     */
    public boolean isForceStop() {
        return forceStop.get();
    }

    /**
     * Sets the force-stop state of the system.
     *
     * @param b true to force-stop the system.
     */
    public void setForceStop(final boolean b) {
        forceStop.set(b);
    }

    /**
     * Generates a document ID.
     *
     * @param map A map of data for the document.
     * @return A unique document ID.
     */
    public String generateDocId(final Map<String, Object> map) {
        return UUID.randomUUID().toString().replace("-", StringUtil.EMPTY);
    }

    /**
     * Abbreviates a long text string.
     *
     * @param str The string to abbreviate.
     * @return The abbreviated string.
     */
    public String abbreviateLongText(final String str) {
        return StringUtils.abbreviate(str, ComponentUtil.getFessConfig().getMaxLogOutputLengthAsInteger());
    }

    /**
     * Normalizes an HTML language string.
     *
     * @param value The language string to normalize.
     * @return The normalized language string.
     */
    public String normalizeHtmlLang(final String value) {
        final String defaultLang = ComponentUtil.getFessConfig().getCrawlerDocumentHtmlDefaultLang();
        if (StringUtil.isNotBlank(defaultLang)) {
            return defaultLang;
        }

        return normalizeLang(value);
    }

    /**
     * Normalizes a language string.
     *
     * @param value The language string to normalize.
     * @return The normalized language string.
     */
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

    /**
     * Gets a list of language items for a given locale.
     *
     * @param locale The locale.
     * @return A list of language items.
     */
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

    /**
     * Adds a shutdown hook.
     *
     * @param hook The shutdown hook to add.
     */
    public void addShutdownHook(final Runnable hook) {
        shutdownHookList.add(hook);
    }

    /**
     * Gets the hostname of the local machine.
     *
     * @return The hostname.
     */
    public String getHostname() {
        final Map<String, String> env = getEnvMap();
        if (env.containsKey("COMPUTERNAME")) {
            return env.get("COMPUTERNAME");
        }
        if (env.containsKey("HOSTNAME")) {
            return env.get("HOSTNAME");
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (final UnknownHostException e) {
            logger.debug("Unknown hostname.", e);
        }
        return "Unknown";
    }

    /**
     * Sets up administrative HTML data for a given action.
     *
     * @param action  The action to set up data for.
     * @param runtime The action runtime.
     */
    public void setupAdminHtmlData(final TypicalAction action, final ActionRuntime runtime) {
        runtime.registerData("developmentMode", ComponentUtil.getSearchEngineClient().isEmbedded());
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String installationLink = fessConfig.getOnlineHelpInstallation();
        runtime.registerData("installationLink", getHelpUrl(installationLink));
        runtime.registerData("storageEnabled",
                StringUtil.isNotBlank(fessConfig.getStorageEndpoint()) && StringUtil.isNotBlank(fessConfig.getStorageBucket()));
        final boolean eoled = isEoled();
        runtime.registerData("eoled", eoled);
        if (eoled) {
            final String eolLink = fessConfig.getOnlineHelpEol();
            runtime.registerData("eolLink", getHelpUrl(eolLink));
        }
    }

    /**
     * Sets up search HTML data for a given action.
     *
     * @param action  The action to set up data for.
     * @param runtime The action runtime.
     */
    public void setupSearchHtmlData(final TypicalAction action, final ActionRuntime runtime) {
        runtime.registerData("developmentMode", ComponentUtil.getSearchEngineClient().isEmbedded());
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final String installationLink = fessConfig.getOnlineHelpInstallation();
        runtime.registerData("installationLink", getHelpUrl(installationLink));
        final boolean eoled = isEoled();
        runtime.registerData("eoled", eoled);
        if (eoled) {
            final String eolLink = fessConfig.getOnlineHelpEol();
            runtime.registerData("eolLink", getHelpUrl(eolLink));
        }
    }

    /**
     * Checks if the application has reached its end-of-life.
     *
     * @return true if the application is EOL, false otherwise.
     */
    protected boolean isEoled() {
        return getCurrentTimeAsLong() > eolTime;
    }

    /**
     * Checks if a permission string is a user permission.
     *
     * @param permission The permission string.
     * @return true if it is a user permission, false otherwise.
     */
    public boolean isUserPermission(final String permission) {
        if (StringUtil.isNotBlank(permission)) {
            return permission.startsWith(ComponentUtil.getFessConfig().getRoleSearchUserPrefix());
        }
        return false;
    }

    /**
     * Gets the search role for a given user.
     *
     * @param name The username.
     * @return The search role.
     */
    public String getSearchRoleByUser(final String name) {
        return createSearchRole(ComponentUtil.getFessConfig().getRoleSearchUserPrefix(), name);
    }

    /**
     * Gets the search role for a given group.
     *
     * @param name The group name.
     * @return The search role.
     */
    public String getSearchRoleByGroup(final String name) {
        return createSearchRole(ComponentUtil.getFessConfig().getRoleSearchGroupPrefix(), name);
    }

    /**
     * Gets the search role for a given role.
     *
     * @param name The role name.
     * @return The search role.
     */
    public String getSearchRoleByRole(final String name) {
        return createSearchRole(ComponentUtil.getFessConfig().getRoleSearchRolePrefix(), name);
    }

    /**
     * Creates a search role string.
     *
     * @param type The type of the role.
     * @param name The name of the role.
     * @return The search role string.
     */
    protected String createSearchRole(final String type, final String name) {
        final String value = type + ComponentUtil.getFessConfig().getCanonicalLdapName(name);
        if (logger.isDebugEnabled()) {
            logger.debug("Search Role: {}:{}={}", type, name, value);
        }
        return value;
    }

    /**
     * Reloads the application configuration.
     */
    public void reloadConfiguration() {
        reloadConfiguration(true);
    }

    /**
     * Reloads the application configuration.
     *
     * @param resetJobs true to reset scheduled jobs.
     */
    public void reloadConfiguration(final boolean resetJobs) {
        ComponentUtil.getSearchEngineClient().refresh();

        ComponentUtil.getSuggestHelper().init();
        ComponentUtil.getPopularWordHelper().init();

        ComponentUtil.getLabelTypeHelper().update();
        ComponentUtil.getPathMappingHelper().update();
        ComponentUtil.getRelatedContentHelper().update();
        ComponentUtil.getRelatedQueryHelper().update();
        ComponentUtil.getKeyMatchHelper().update();

        ComponentUtil.getLdapManager().updateConfig();
        if (resetJobs) {
            ComponentUtil.getJobManager().reboot();
        }
        updateSystemProperties();

        ComponentUtil.getRankFusionProcessor().update();
    }

    /**
     * Updates system properties from the application configuration.
     */
    public void updateSystemProperties() {
        final String value = ComponentUtil.getFessConfig().getAppValue();
        if (logger.isDebugEnabled()) {
            logger.debug("system.properties: {}", value);
        }
        if (StringUtil.isNotBlank(value)) {
            ParameterUtil.parse(ParameterUtil.encrypt(value)).entrySet().stream().filter(e -> {
                final String key = e.getKey();
                if (StringUtil.isBlank(key)) {
                    return false;
                }
                if (key.startsWith("fess.")) {
                    return true;
                }
                return System.getProperty(key) == null;
            }).forEach(e -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("system.properties: setProperty({}, {})", e.getKey(), e.getValue());
                }
                System.setProperty(e.getKey(), e.getValue());
            });
        }
    }

    /**
     * Updates the application configuration.
     *
     * @return A string containing the results of the update.
     */
    public String updateConfiguration() {
        final StringBuilder buf = new StringBuilder();
        updateConfigListenerMap.entrySet().stream().forEach(e -> {
            buf.append(e.getKey()).append(": ");
            try {
                buf.append(e.getValue().get());
            } catch (final Exception ex) {
                logger.warn("Failed to process {} task.", e.getKey(), ex);
                buf.append(ex.getMessage());
            }
            buf.append('\n');
        });
        return buf.toString();
    }

    /**
     * Adds a listener for configuration updates.
     *
     * @param name     The name of the listener.
     * @param listener The listener to add.
     */
    public void addUpdateConfigListener(final String name, final Supplier<String> listener) {
        updateConfigListenerMap.put(name, listener);
    }

    /**
     * Checks if the cluster state has changed.
     *
     * @param status The current cluster status.
     * @return true if the state has changed, false otherwise.
     */
    public boolean isChangedClusterState(final int status) {
        return previousClusterState.getAndSet(status) != status;
    }

    /**
     * Creates a new action validator.
     *
     * @param requestManager  The request manager.
     * @param messagesCreator The messages creator.
     * @param runtimeGroups   The runtime groups.
     * @return A new action validator.
     */
    public ActionValidator<FessMessages> createValidator(final RequestManager requestManager,
            final UserMessagesCreator<FessMessages> messagesCreator, final Class<?>[] runtimeGroups) {
        return new FessActionValidator<>(requestManager, messagesCreator, runtimeGroups);
    }

    /**
     * Gets the redirect response to the login page.
     *
     * @param response The original response.
     * @return The redirect response.
     */
    public HtmlResponse getRedirectResponseToLogin(final HtmlResponse response) {
        return response;
    }

    /**
     * Gets the redirect response to the root page.
     *
     * @param response The original response.
     * @return The redirect response.
     */
    public HtmlResponse getRedirectResponseToRoot(final HtmlResponse response) {
        return response;
    }

    /**
     * Sets the log level for the application.
     *
     * @param level The log level to set.
     */
    public void setLogLevel(final String level) {
        final Level logLevel = Level.toLevel(level, Level.WARN);
        System.setProperty(Constants.FESS_LOG_LEVEL, logLevel.toString());
        split(ComponentUtil.getFessConfig().getLoggingAppPackages(), ",")
                .of(stream -> stream.map(String::trim).filter(StringUtil::isNotEmpty).forEach(s -> Configurator.setLevel(s, logLevel)));
    }

    /**
     * Gets the current log level.
     *
     * @return The current log level.
     */
    public String getLogLevel() {
        return System.getProperty(Constants.FESS_LOG_LEVEL, Level.WARN.toString());
    }

    /**
     * Creates a temporary file.
     *
     * @param prefix The prefix for the file name.
     * @param suffix The suffix for the file name.
     * @return The created temporary file.
     * @throws IORuntimeException if the file cannot be created.
     */
    public File createTempFile(final String prefix, final String suffix) {
        try {
            final File file = File.createTempFile(prefix, suffix);
            file.setReadable(false, false);
            file.setReadable(true, true);
            file.setWritable(false, false);
            file.setWritable(true, true);
            if (logger.isDebugEnabled()) {
                logger.debug("Create {} as a temp file.", file.getAbsolutePath());
            }
            return file;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Calibrates the CPU load.
     *
     * @return true if the CPU load is within the acceptable range, false otherwise.
     */
    public boolean calibrateCpuLoad() {
        return calibrateCpuLoad(0L);
    }

    /**
     * Calibrates the CPU load with a timeout.
     *
     * @param timeoutInMillis The timeout in milliseconds.
     * @return true if the CPU load is within the acceptable range, false otherwise.
     */
    public boolean calibrateCpuLoad(final long timeoutInMillis) {
        final short percent = ComponentUtil.getFessConfig().getAdaptiveLoadControlAsInteger().shortValue();
        if (percent <= 0) {
            return true;
        }
        short current = getSystemCpuPercent();
        if (current < percent) {
            return true;
        }
        final long startTime = getCurrentTimeAsLong();
        final String threadName = Thread.currentThread().getName();
        try {
            waitingThreadNames.add(threadName);
            while (current >= percent) {
                if (timeoutInMillis > 0 && getCurrentTimeAsLong() - startTime > timeoutInMillis) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Cpu Load {}% is greater than {}%. {} waiting thread(s). {} thread is timed out.", current, percent,
                                waitingThreadNames.size(), threadName);
                    }
                    return false;
                }
                if (logger.isInfoEnabled()) {
                    logger.info("Cpu Load {}% is greater than {}%. {} waiting thread(s).", current, percent, waitingThreadNames.size());
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Waiting threads: {}", waitingThreadNames);
                }
                ThreadUtil.sleep(systemCpuCheckInterval);
                current = getSystemCpuPercent();
            }
        } finally {
            waitingThreadNames.remove(threadName);
        }
        return true;
    }

    /**
     * Waits for all waiting threads to complete.
     */
    public void waitForNoWaitingThreads() {
        int count = waitingThreadNames.size();
        while (count > 0) {
            if (logger.isInfoEnabled()) {
                logger.info("{} waiting thread(s).", count);
            }
            ThreadUtil.sleep(systemCpuCheckInterval);
            count = waitingThreadNames.size();
        }
    }

    /**
     * Gets the system CPU usage percentage.
     *
     * @return The system CPU usage percentage.
     */
    protected short getSystemCpuPercent() {
        final long now = getCurrentTimeAsLong();
        if (now - systemCpuCheckTime > systemCpuCheckInterval) {
            synchronized (this) {
                if (now - systemCpuCheckTime > systemCpuCheckInterval) {
                    try {
                        final OsProbe osProbe = OsProbe.getInstance();
                        systemCpuPercent = osProbe.getSystemCpuPercent();
                        if (logger.isDebugEnabled()) {
                            logger.debug("Updated System Cpu {}%", systemCpuPercent);
                        }
                    } catch (final Exception e) {
                        logger.warn("Failed to get SystemCpuPercent.", e);
                        return 0;
                    }
                    systemCpuCheckTime = now;
                }
            }
        }
        return systemCpuPercent;
    }

    /**
     * Gets a map of filtered environment variables.
     *
     * @param keyPattern The pattern to filter environment variable keys.
     * @return A map of filtered environment variables.
     */
    public Map<String, String> getFilteredEnvMap(final String keyPattern) {
        final Pattern pattern = Pattern.compile(keyPattern);
        return getEnvMap().entrySet().stream().filter(e -> {
            final String key = e.getKey();
            if (StringUtil.isBlank(key)) {
                return false;
            }
            return pattern.matcher(key).matches();
        }).collect(Collectors.toMap(Entry<String, String>::getKey, Entry<String, String>::getValue));
    }

    /**
     * Gets a map of environment variables.
     *
     * @return A map of environment variables.
     */
    protected Map<String, String> getEnvMap() {
        return System.getenv();
    }

    /**
     * Gets the version of the Fess application.
     *
     * @return The version string.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the major version number.
     *
     * @return The major version number.
     */
    public int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Gets the minor version number.
     *
     * @return The minor version number.
     */
    public int getMinorVersion() {
        return minorVersion;
    }

    /**
     * Gets the product version string.
     *
     * @return The product version string.
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * Sets the interval for checking the system CPU load.
     *
     * @param systemCpuCheckInterval The interval in milliseconds.
     */
    public void setSystemCpuCheckInterval(final long systemCpuCheckInterval) {
        this.systemCpuCheckInterval = systemCpuCheckInterval;
    }
}
