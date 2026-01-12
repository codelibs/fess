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
package org.codelibs.fess.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.crypto.CachedCipher;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.api.WebApiManagerFactory;
import org.codelibs.fess.auth.AuthenticationManager;
import org.codelibs.fess.chat.ChatClient;
import org.codelibs.fess.chat.ChatSessionManager;
import org.codelibs.fess.cors.CorsHandlerFactory;
import org.codelibs.fess.crawler.client.CrawlerClientCreator;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.entity.OpenSearchAccessResult;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.ds.DataStoreFactory;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.helper.AccessTokenHelper;
import org.codelibs.fess.helper.ActivityHelper;
import org.codelibs.fess.helper.CrawlerStatsHelper;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.CurlHelper;
import org.codelibs.fess.helper.DocumentHelper;
import org.codelibs.fess.helper.DuplicateHostHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.IntervalControlHelper;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.KeyMatchHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.LanguageHelper;
import org.codelibs.fess.helper.NotificationHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.PluginHelper;
import org.codelibs.fess.helper.PopularWordHelper;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.helper.ProtocolHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.RateLimitHelper;
import org.codelibs.fess.helper.RelatedContentHelper;
import org.codelibs.fess.helper.RelatedQueryHelper;
import org.codelibs.fess.helper.RoleQueryHelper;
import org.codelibs.fess.helper.SambaHelper;
import org.codelibs.fess.helper.SearchHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.ThemeHelper;
import org.codelibs.fess.helper.UserAgentHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.helper.VirtualHostHelper;
import org.codelibs.fess.indexer.IndexUpdater;
import org.codelibs.fess.ingest.IngestFactory;
import org.codelibs.fess.job.JobExecutor;
import org.codelibs.fess.ldap.LdapManager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.direction.FessProp;
import org.codelibs.fess.opensearch.client.SearchEngineClient;
import org.codelibs.fess.query.QueryFieldConfig;
import org.codelibs.fess.query.QueryProcessor;
import org.codelibs.fess.query.parser.QueryParser;
import org.codelibs.fess.rank.fusion.RankFusionProcessor;
import org.codelibs.fess.script.ScriptEngineFactory;
import org.codelibs.fess.sso.SsoManager;
import org.codelibs.fess.thumbnail.ThumbnailManager;
import org.lastaflute.core.message.MessageManager;
import org.lastaflute.core.security.PrimaryCipher;
import org.lastaflute.di.core.SingletonLaContainer;
import org.lastaflute.di.core.exception.AutoBindingFailureException;
import org.lastaflute.di.core.exception.ComponentNotFoundException;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.lastaflute.di.core.smart.hot.HotdeployUtil;
import org.lastaflute.job.JobManager;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.servlet.request.ResponseManager;

/**
 * Utility class for accessing system components and dependencies.
 * Provides centralized access to various helpers, managers, and services throughout the Fess application.
 */
public final class ComponentUtil {

    private static final Logger logger = LogManager.getLogger(ComponentUtil.class);

    /** Component map for storing component instances. */
    private static Map<String, Object> componentMap = new HashMap<>();

    private static final String SCRIPT_ENGINE_FACTORY = "scriptEngineFactory";

    private static final String INGEST_FACTORY = "ingestFactory";

    private static final String NOTIFICATION_HELPER = "notificationHelper";

    private static final String SEARCH_HELPER = "searchHelper";

    private static final String THEME_HELPER = "themeHelper";

    private static final String PLUGIN_HELPER = "pluginHelper";

    private static final String LANGUAGE_HELPER = "languageHelper";

    private static final String CURL_HELPER = "curlHelper";

    private static final String QUERY_STRING_BUILDER = "queryStringBuilder";

    private static final String ACCESS_TOKEN_HELPER = "accessTokenHelper";

    private static final String RATE_LIMIT_HELPER = "rateLimitHelper";

    private static final String AUTHENTICATION_MANAGER = "authenticationManager";

    private static final String THUMBNAIL_MANAGER = "thumbnailManager";

    private static final String SSO_MANAGER = "ssoManager";

    private static final String PERMISSION_HELPER = "permissionHelper";

    private static final String QUERY_PARSER = "queryParser";

    private static final String DOCUMENT_HELPER = "documentHelper";

    private static final String ACTIVITY_HELPER = "activityHelper";

    private static final String LDAP_MANAGER = "ldapManager";

    private static final String ROLE_QUERY_HELPER = "roleQueryHelper";

    private static final String SUGGEST_HELPER = "suggestHelper";

    private static final String SEARCH_ENGINE_CLIENT = "searchEngineClient";

    private static final String DICTIONARY_MANAGER = "dictionaryManager";

    private static final String DATA_SERVICE = "dataService";

    private static final String MESSAGE_MANAGER = "messageManager";

    private static final String INDEX_UPDATER = "indexUpdater";

    private static final String FILE_TYPE_HELPER = "fileTypeHelper";

    private static final String EXTRACTOR_FACTORY = "extractorFactory";

    private static final String INTERVAL_CONTROL_HELPER = "intervalControlHelper";

    private static final String DATA_STORE_FACTORY = "dataStoreFactory";

    private static final String USER_AGENT_HELPER = "userAgentHelper";

    private static final String USER_INFO_HELPER = "userInfoHelper";

    private static final String WEB_API_MANAGER_FACTORY = "webApiManagerFactory";

    private static final String PROCESS_HELPER = "processHelper";

    private static final String JOB_HELPER = "jobHelper";

    private static final String DUPLICATE_HOST_HELPER = "duplicateHostHelper";

    private static final String PATH_MAPPING_HELPER = "pathMappingHelper";

    private static final String POPULAR_WORD_HELPER = "popularWordHelper";

    private static final String CRAWLING_INFO_HELPER = "crawlingInfoHelper";

    private static final String CRAWLING_CONFIG_HELPER = "crawlingConfigHelper";

    private static final String SEARCH_LOG_HELPER = "searchLogHelper";

    private static final String LABEL_TYPE_HELPER = "labelTypeHelper";

    private static final String QUERY_HELPER = "queryHelper";

    private static final String QUERY_FIELD_CONFIG = "queryFieldConfig";

    private static final String QUERY_PROCESSOR = "queryProcessor";

    private static final String SAMBA_HELPER = "sambaHelper";

    private static final String VIEW_HELPER = "viewHelper";

    private static final String SYSTEM_HELPER = "systemHelper";

    private static final String CRAWLER_PROPERTIES = "systemProperties";

    private static final String JOB_EXECUTOR_SUFFIX = "JobExecutor";

    private static final String KEY_MATCH_HELPER = "keyMatchHelper";

    private static final String INDEXING_HELPER = "indexingHelper";

    private static final String VIRTUAL_HOST_HELPER = "virtualHostHelper";

    private static final String RELATED_CONTENT_HELPER = "relatedContentHelper";

    private static final String RELATED_QUERY_HELPER = "relatedQueryHelper";

    private static final String CRAWLER_STATS_HELPER = "crawlerStatsHelper";

    private static final String CORS_HANDLER_FACTORY = "corsHandlerFactory";

    private static final String RANK_FUSION_PROCESSOR = "rankFusionProcessor";

    private static final String PROTOCOL_HELPER = "protocolHelper";

    private static final String CHAT_SESSION_MANAGER = "chatSessionManager";

    private static final String CHAT_CLIENT = "chatClient";

    private static IndexingHelper indexingHelper;

    private static CrawlingConfigHelper crawlingConfigHelper;

    private static SystemHelper systemHelper;

    private static FessConfig fessConfig;

    /** List of initialization processes to run after container initialization. */
    private static List<Runnable> initProcesses = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private ComponentUtil() {
    }

    /**
     * Processes a runnable after container initialization.
     * @param process The process to run after container init.
     */
    public static void processAfterContainerInit(final Runnable process) {
        if (available()) {
            process.run();
        } else {
            initProcesses.add(process);
        }
    }

    /**
     * Executes all initialization processes.
     * @param action The action to perform on each initialization process.
     */
    public static void doInitProcesses(final Consumer<? super Runnable> action) {
        try {
            initProcesses.forEach(action);
        } finally {
            initProcesses.clear();
        }
    }

    /**
     * Gets a cached cipher by name.
     * @param cipherName The cipher name.
     * @return The cached cipher.
     */
    public static CachedCipher getCipher(final String cipherName) {
        return getComponent(cipherName);
    }

    /**
     * Gets the system properties.
     * @return The dynamic properties.
     */
    public static DynamicProperties getSystemProperties() {
        return getComponent(CRAWLER_PROPERTIES);
    }

    /**
     * Gets the system helper component.
     * @return The system helper.
     */
    public static SystemHelper getSystemHelper() {
        if (systemHelper == null || HotdeployUtil.isHotdeploy()) {
            systemHelper = getComponent(SYSTEM_HELPER);
        }
        return systemHelper;
    }

    /**
     * Gets the view helper component.
     * @return The view helper.
     */
    public static ViewHelper getViewHelper() {
        return getComponent(VIEW_HELPER);
    }

    /**
     * Gets the Samba helper component.
     * @return The Samba helper.
     */
    public static SambaHelper getSambaHelper() {
        return getComponent(SAMBA_HELPER);
    }

    /**
     * Gets the query helper component.
     * @return The query helper.
     */
    public static QueryHelper getQueryHelper() {
        return getComponent(QUERY_HELPER);
    }

    /**
     * Gets the query field configuration.
     * @return The query field config.
     */
    public static QueryFieldConfig getQueryFieldConfig() {
        return getComponent(QUERY_FIELD_CONFIG);
    }

    /**
     * Gets the query processor component.
     * @return The query processor.
     */
    public static QueryProcessor getQueryProcessor() {
        return getComponent(QUERY_PROCESSOR);
    }

    /**
     * Gets the label type helper component.
     * @return The label type helper.
     */
    public static LabelTypeHelper getLabelTypeHelper() {
        return getComponent(LABEL_TYPE_HELPER);
    }

    /**
     * Gets the search log helper component.
     * @return The search log helper.
     */
    public static SearchLogHelper getSearchLogHelper() {
        return getComponent(SEARCH_LOG_HELPER);
    }

    /**
     * Gets the crawling configuration helper component.
     * @return The crawling config helper.
     */
    public static CrawlingConfigHelper getCrawlingConfigHelper() {
        if (crawlingConfigHelper == null || HotdeployUtil.isHotdeploy()) {
            crawlingConfigHelper = getComponent(CRAWLING_CONFIG_HELPER);
        }
        return crawlingConfigHelper;
    }

    /**
     * Gets the crawling info helper component.
     * @return The crawling info helper.
     */
    public static CrawlingInfoHelper getCrawlingInfoHelper() {
        return getComponent(CRAWLING_INFO_HELPER);
    }

    /**
     * Gets the popular word helper component.
     * @return The popular word helper.
     */
    public static PopularWordHelper getPopularWordHelper() {
        return getComponent(POPULAR_WORD_HELPER);
    }

    /**
     * Gets the path mapping helper component.
     * @return The path mapping helper.
     */
    public static PathMappingHelper getPathMappingHelper() {
        return getComponent(PATH_MAPPING_HELPER);
    }

    /**
     * Gets the duplicate host helper component.
     * @return The duplicate host helper.
     */
    public static DuplicateHostHelper getDuplicateHostHelper() {
        return getComponent(DUPLICATE_HOST_HELPER);
    }

    /**
     * Gets the process helper component.
     * @return The process helper.
     */
    public static ProcessHelper getProcessHelper() {
        return getComponent(PROCESS_HELPER);
    }

    /**
     * Gets the job helper component.
     * @return The job helper.
     */
    public static JobHelper getJobHelper() {
        return getComponent(JOB_HELPER);
    }

    /**
     * Gets the web API manager factory component.
     * @return The web API manager factory.
     */
    public static WebApiManagerFactory getWebApiManagerFactory() {
        return getComponent(WEB_API_MANAGER_FACTORY);
    }

    /**
     * Gets the user agent helper component.
     * @return The user agent helper.
     */
    public static UserAgentHelper getUserAgentHelper() {
        return getComponent(USER_AGENT_HELPER);
    }

    /**
     * Gets the data store factory component.
     * @return The data store factory.
     */
    public static DataStoreFactory getDataStoreFactory() {
        return getComponent(DATA_STORE_FACTORY);
    }

    /**
     * Gets the interval control helper component.
     * @return The interval control helper.
     */
    public static IntervalControlHelper getIntervalControlHelper() {
        return getComponent(INTERVAL_CONTROL_HELPER);
    }

    /**
     * Gets the extractor factory component.
     * @return The extractor factory.
     */
    public static ExtractorFactory getExtractorFactory() {
        return getComponent(EXTRACTOR_FACTORY);
    }

    /**
     * Gets a job executor by name.
     * @param name The name of the job executor.
     * @return The job executor instance.
     */
    public static JobExecutor getJobExecutor(final String name) {
        if (name.endsWith(JOB_EXECUTOR_SUFFIX)) {
            return getComponent(name);
        }
        return getComponent("script" + JOB_EXECUTOR_SUFFIX);
    }

    /**
     * Gets the file type helper component.
     * @return The file type helper.
     */
    public static FileTypeHelper getFileTypeHelper() {
        return getComponent(FILE_TYPE_HELPER);
    }

    /**
     * Gets the index updater component.
     * @return The index updater.
     */
    public static IndexUpdater getIndexUpdater() {
        return getComponent(INDEX_UPDATER);
    }

    /**
     * Gets the key match helper component.
     * @return The key match helper.
     */
    public static KeyMatchHelper getKeyMatchHelper() {
        return getComponent(KEY_MATCH_HELPER);
    }

    /**
     * Gets the indexing helper component.
     * @return The indexing helper.
     */
    public static IndexingHelper getIndexingHelper() {
        if (indexingHelper == null || HotdeployUtil.isHotdeploy()) {
            indexingHelper = getComponent(INDEXING_HELPER);
        }
        return indexingHelper;
    }

    /**
     * Gets the user info helper component.
     * @return The user info helper.
     */
    public static UserInfoHelper getUserInfoHelper() {
        return getComponent(USER_INFO_HELPER);
    }

    /**
     * Gets the message manager component.
     * @return The message manager.
     */
    public static MessageManager getMessageManager() {
        return getComponent(MESSAGE_MANAGER);
    }

    /**
     * Gets the dictionary manager component.
     * @return The dictionary manager.
     */
    public static DictionaryManager getDictionaryManager() {
        return getComponent(DICTIONARY_MANAGER);
    }

    /**
     * Gets the data service component.
     * @return The data service.
     */
    public static DataService<OpenSearchAccessResult> getDataService() {
        return getComponent(DATA_SERVICE);
    }

    /**
     * Gets the search engine client component.
     * @return The search engine client.
     */
    public static SearchEngineClient getSearchEngineClient() {
        return getComponent(SEARCH_ENGINE_CLIENT);
    }

    /**
     * Gets the Fess configuration component.
     * @return The Fess config.
     */
    public static FessConfig getFessConfig() {
        if (fessConfig != null) {
            return fessConfig;
        }
        fessConfig = getComponent(FessConfig.class);
        return fessConfig;
    }

    /**
     * Gets the suggest helper component.
     * @return The suggest helper.
     */
    public static SuggestHelper getSuggestHelper() {
        return getComponent(SUGGEST_HELPER);
    }

    /**
     * Gets the role query helper component.
     * @return The role query helper.
     */
    public static RoleQueryHelper getRoleQueryHelper() {
        return getComponent(ROLE_QUERY_HELPER);
    }

    /**
     * Gets the LDAP manager component.
     * @return The LDAP manager.
     */
    public static LdapManager getLdapManager() {
        return getComponent(LDAP_MANAGER);
    }

    /**
     * Gets the activity helper component.
     * @return The activity helper.
     */
    public static ActivityHelper getActivityHelper() {
        return getComponent(ACTIVITY_HELPER);
    }

    /**
     * Gets the request manager component.
     * @return The request manager.
     */
    public static RequestManager getRequestManager() {
        return getComponent(RequestManager.class);
    }

    /**
     * Gets the response manager component.
     * @return The response manager.
     */
    public static ResponseManager getResponseManager() {
        return getComponent(ResponseManager.class);
    }

    /**
     * Gets the job manager component.
     * @return The job manager.
     */
    public static JobManager getJobManager() {
        return getComponent(JobManager.class);
    }

    /**
     * Gets the document helper component.
     * @return The document helper.
     */
    public static DocumentHelper getDocumentHelper() {
        return getComponent(DOCUMENT_HELPER);
    }

    /**
     * Gets the query parser component.
     * @return The query parser.
     */
    public static QueryParser getQueryParser() {
        return getComponent(QUERY_PARSER);
    }

    /**
     * Gets the permission helper component.
     * @return The permission helper.
     */
    public static PermissionHelper getPermissionHelper() {
        return getComponent(PERMISSION_HELPER);
    }

    /**
     * Gets the SSO manager component.
     * @return The SSO manager.
     */
    public static SsoManager getSsoManager() {
        return getComponent(SSO_MANAGER);
    }

    /**
     * Gets the thumbnail manager component.
     * @return The thumbnail manager.
     */
    public static ThumbnailManager getThumbnailManager() {
        return getComponent(THUMBNAIL_MANAGER);
    }

    /**
     * Gets the authentication manager component.
     * @return The authentication manager.
     */
    public static AuthenticationManager getAuthenticationManager() {
        return getComponent(AUTHENTICATION_MANAGER);
    }

    /**
     * Gets the primary cipher component.
     * @return The primary cipher.
     */
    public static PrimaryCipher getPrimaryCipher() {
        return getComponent(PrimaryCipher.class);
    }

    /**
     * Gets the crawler client factory component.
     * @return The crawler client factory.
     */
    public static CrawlerClientFactory getCrawlerClientFactory() {
        return getComponent(CrawlerClientFactory.class);
    }

    /**
     * Gets the crawler client creator component.
     * @return The crawler client creator.
     */
    public static CrawlerClientCreator getCrawlerClientCreator() {
        return getComponent(CrawlerClientCreator.class);
    }

    /**
     * Gets the related query helper component.
     * @return The related query helper.
     */
    public static RelatedQueryHelper getRelatedQueryHelper() {
        return getComponent(RELATED_QUERY_HELPER);
    }

    /**
     * Gets the related content helper component.
     * @return The related content helper.
     */
    public static RelatedContentHelper getRelatedContentHelper() {
        return getComponent(RELATED_CONTENT_HELPER);
    }

    /**
     * Gets the virtual host helper component.
     * @return The virtual host helper.
     */
    public static VirtualHostHelper getVirtualHostHelper() {
        return getComponent(VIRTUAL_HOST_HELPER);
    }

    /**
     * Gets the access token helper component.
     * @return The access token helper.
     */
    public static AccessTokenHelper getAccessTokenHelper() {
        return getComponent(ACCESS_TOKEN_HELPER);
    }

    /**
     * Gets the rate limit helper component.
     * @return The rate limit helper.
     */
    public static RateLimitHelper getRateLimitHelper() {
        return getComponent(RATE_LIMIT_HELPER);
    }

    /**
     * Gets the query string builder component.
     * @return The query string builder.
     */
    public static QueryStringBuilder getQueryStringBuilder() {
        return getComponent(QUERY_STRING_BUILDER);
    }

    /**
     * Gets the curl helper component.
     * @return The curl helper.
     */
    public static CurlHelper getCurlHelper() {
        return getComponent(CURL_HELPER);
    }

    /**
     * Gets the language helper component.
     * @return The language helper.
     */
    public static LanguageHelper getLanguageHelper() {
        return getComponent(LANGUAGE_HELPER);
    }

    /**
     * Gets the plugin helper component.
     * @return The plugin helper.
     */
    public static PluginHelper getPluginHelper() {
        return getComponent(PLUGIN_HELPER);
    }

    /**
     * Gets the theme helper component.
     * @return The theme helper.
     */
    public static ThemeHelper getThemeHelper() {
        return getComponent(THEME_HELPER);
    }

    /**
     * Gets the search helper component.
     * @return The search helper.
     */
    public static SearchHelper getSearchHelper() {
        return getComponent(SEARCH_HELPER);
    }

    /**
     * Gets the notification helper component.
     * @return The notification helper.
     */
    public static NotificationHelper getNotificationHelper() {
        return getComponent(NOTIFICATION_HELPER);
    }

    /**
     * Gets the ingest factory component.
     * @return The ingest factory.
     */
    public static IngestFactory getIngestFactory() {
        return getComponent(INGEST_FACTORY);
    }

    /**
     * Gets the script engine factory component.
     * @return The script engine factory.
     */
    public static ScriptEngineFactory getScriptEngineFactory() {
        return getComponent(SCRIPT_ENGINE_FACTORY);
    }

    /**
     * Gets the crawler stats helper component.
     * @return The crawler stats helper.
     */
    public static CrawlerStatsHelper getCrawlerStatsHelper() {
        return getComponent(CRAWLER_STATS_HELPER);
    }

    /**
     * Gets the CORS handler factory component.
     * @return The CORS handler factory.
     */
    public static CorsHandlerFactory getCorsHandlerFactory() {
        return getComponent(CORS_HANDLER_FACTORY);
    }

    /**
     * Gets the rank fusion processor component.
     * @return The rank fusion processor.
     */
    public static RankFusionProcessor getRankFusionProcessor() {
        return getComponent(RANK_FUSION_PROCESSOR);
    }

    /**
     * Gets the protocol helper component.
     * @return The protocol helper.
     */
    public static ProtocolHelper getProtocolHelper() {
        return getComponent(PROTOCOL_HELPER);
    }

    /**
     * Gets the chat session manager component.
     * @return The chat session manager.
     */
    public static ChatSessionManager getChatSessionManager() {
        return getComponent(CHAT_SESSION_MANAGER);
    }

    /**
     * Gets the chat client component.
     * @return The chat client.
     */
    public static ChatClient getChatClient() {
        return getComponent(CHAT_CLIENT);
    }

    /**
     * Gets a component by its class type.
     * @param <T> The type of the component.
     * @param clazz The class of the component to retrieve.
     * @return The component instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getComponent(final Class<T> clazz) {
        try {
            return SingletonLaContainer.getComponent(clazz);
        } catch (final NullPointerException e) {
            if (logger.isDebugEnabled()) {
                throw new ContainerNotAvailableException(clazz.getCanonicalName(), e);
            }
            throw new ContainerNotAvailableException(clazz.getCanonicalName());
        } catch (final ComponentNotFoundException | AutoBindingFailureException e) {
            if (componentMap.containsKey(clazz.getCanonicalName())) {
                return (T) componentMap.get(clazz.getCanonicalName());
            }
            throw e;
        }
    }

    /**
     * Gets a component by its name.
     * @param <T> The type of the component.
     * @param componentName The name of the component to retrieve.
     * @return The component instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getComponent(final String componentName) {
        try {
            return SingletonLaContainer.getComponent(componentName);
        } catch (final NullPointerException e) {
            if (logger.isDebugEnabled()) {
                throw new ContainerNotAvailableException(componentName, e);
            }
            throw new ContainerNotAvailableException(componentName);
        } catch (final ComponentNotFoundException | AutoBindingFailureException e) {
            if (componentMap.containsKey(componentName)) {
                return (T) componentMap.get(componentName);
            }
            throw e;
        }
    }

    /**
     * Checks if a component is available.
     * @param componentKey The key of the component to check.
     * @return True if the component is available, false otherwise.
     */
    public static boolean hasComponent(final String componentKey) {
        if (SingletonLaContainerFactory.getContainer().hasComponentDef(componentKey)) {
            return true;
        }
        return componentMap.containsKey(componentKey);
    }

    /**
     * Checks if query parser is available.
     * @return True if query parser is available, false otherwise.
     */
    public static boolean hasQueryParser() {
        return hasComponent(QUERY_PARSER);
    }

    /**
     * Checks if view helper is available.
     * @return True if view helper is available, false otherwise.
     */
    public static boolean hasViewHelper() {
        return hasComponent(VIEW_HELPER);
    }

    /**
     * Checks if query helper is available.
     * @return True if query helper is available, false otherwise.
     */
    public static boolean hasQueryHelper() {
        return hasComponent(QUERY_HELPER);
    }

    /**
     * Checks if popular word helper is available.
     * @return True if popular word helper is available, false otherwise.
     */
    public static boolean hasPopularWordHelper() {
        return hasComponent(POPULAR_WORD_HELPER);
    }

    /**
     * Checks if related query helper is available.
     * @return True if related query helper is available, false otherwise.
     */
    public static boolean hasRelatedQueryHelper() {
        return hasComponent(RELATED_QUERY_HELPER);
    }

    /**
     * Checks if ingest factory is available.
     * @return True if ingest factory is available, false otherwise.
     */
    public static boolean hasIngestFactory() {
        return hasComponent(INGEST_FACTORY);
    }

    /**
     * Checks if the container is available.
     * @return True if the container is available, false otherwise.
     */
    public static boolean available() {
        try {
            return SingletonLaContainer.getComponent(SYSTEM_HELPER) != null;
        } catch (final Exception e) {
            // ignore
        }
        return false;
    }

    /**
     * For test purpose only.
     *
     * @param fessConfig fessConfig instance
     */
    public static void setFessConfig(final FessConfig fessConfig) {
        ComponentUtil.fessConfig = fessConfig;
        if (fessConfig == null) {
            systemHelper = null;
            FessProp.propMap.clear();
            componentMap.clear();
        }
    }

    /**
     * Registers a component instance with a name.
     * @param instance The component instance to register.
     * @param name The name to register the component under.
     */
    public static void register(final Object instance, final String name) {
        componentMap.put(name, instance);
    }

}
