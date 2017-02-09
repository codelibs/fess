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
package org.codelibs.fess.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.codelibs.core.crypto.CachedCipher;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.api.WebApiManagerFactory;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.entity.EsAccessResult;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.ds.DataStoreFactory;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.exception.ContainerNotAvailableException;
import org.codelibs.fess.helper.ActivityHelper;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.DocumentHelper;
import org.codelibs.fess.helper.DuplicateHostHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.IntervalControlHelper;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.KeyMatchHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.PopularWordHelper;
import org.codelibs.fess.helper.ProcessHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.RoleQueryHelper;
import org.codelibs.fess.helper.SambaHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.UserAgentHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.indexer.IndexUpdater;
import org.codelibs.fess.job.JobExecutor;
import org.codelibs.fess.ldap.LdapManager;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.mylasta.direction.FessProp;
import org.codelibs.fess.sso.SsoManager;
import org.codelibs.fess.thumbnail.ThumbnailManager;
import org.lastaflute.core.message.MessageManager;
import org.lastaflute.core.security.PrimaryCipher;
import org.lastaflute.di.core.SingletonLaContainer;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;
import org.lastaflute.di.core.smart.hot.HotdeployUtil;
import org.lastaflute.job.JobManager;
import org.lastaflute.web.servlet.request.RequestManager;
import org.lastaflute.web.servlet.request.ResponseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ComponentUtil {

    private static final Logger logger = LoggerFactory.getLogger(ComponentUtil.class);

    private static final String THUMBNAIL_MANAGER = "thumbnailManager";

    private static final String SSO_MANAGER = "ssoManager";

    private static final String PERMISSION_HELPER = "permissionHelper";

    private static final String QUERY_PARSER = "queryParser";

    private static final String DOCUMENT_HELPER = "documentHelper";

    private static final String ACTIVITY_HELPER = "activityHelper";

    private static final String LDAP_MANAGER = "ldapManager";

    private static final String ROLE_QUERY_HELPER = "roleQueryHelper";

    private static final String SUGGEST_HELPER = "suggestHelper";

    private static final String FESS_ES_CLIENT = "fessEsClient";

    private static final String DICTIONARY_MANAGER = "dictionaryManager";

    private static final String DATA_SERVICE = "dataService";

    private static final String MESSAGE_MANAGER = "messageManager";

    private static final String USER_AGENT_NAME = "userAgentName";

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

    private static final String SAMBA_HELPER = "sambaHelper";

    private static final String VIEW_HELPER = "viewHelper";

    private static final String SYSTEM_HELPER = "systemHelper";

    private static final String CRAWLER_PROPERTIES = "systemProperties";

    private static final String QUERY_RESPONSE_LIST = "queryResponseList";

    private static final String JOB_EXECUTOR_SUFFIX = "JobExecutor";

    private static final String KEY_MATCH_HELPER = "keyMatchHelper";

    private static final String INDEXING_HELPER = "indexingHelper";

    private static IndexingHelper indexingHelper;

    private static CrawlingConfigHelper crawlingConfigHelper;

    private static SystemHelper systemHelper;

    private static FessConfig fessConfig;

    private static List<Runnable> initProcesses = new ArrayList<>();

    private ComponentUtil() {
    }

    public static void processAfterContainerInit(final Runnable process) {
        if (available()) {
            process.run();
        } else {
            initProcesses.add(process);
        }
    }

    public static void doInitProcesses(final Consumer<? super Runnable> action) {
        try {
            initProcesses.forEach(action);
        } finally {
            initProcesses.clear();
        }
    }

    public static CachedCipher getCipher(final String cipherName) {
        return getComponent(cipherName);
    }

    public static QueryResponseList getQueryResponseList() {
        return getComponent(QUERY_RESPONSE_LIST);
    }

    public static DynamicProperties getSystemProperties() {
        return getComponent(CRAWLER_PROPERTIES);
    }

    public static SystemHelper getSystemHelper() {
        if (systemHelper == null || HotdeployUtil.isHotdeploy()) {
            systemHelper = getComponent(SYSTEM_HELPER);
        }
        return systemHelper;
    }

    public static ViewHelper getViewHelper() {
        return getComponent(VIEW_HELPER);
    }

    public static SambaHelper getSambaHelper() {
        return getComponent(SAMBA_HELPER);
    }

    public static QueryHelper getQueryHelper() {
        return getComponent(QUERY_HELPER);
    }

    public static LabelTypeHelper getLabelTypeHelper() {
        return getComponent(LABEL_TYPE_HELPER);
    }

    public static SearchLogHelper getSearchLogHelper() {
        return getComponent(SEARCH_LOG_HELPER);
    }

    public static CrawlingConfigHelper getCrawlingConfigHelper() {
        if (crawlingConfigHelper == null || HotdeployUtil.isHotdeploy()) {
            crawlingConfigHelper = getComponent(CRAWLING_CONFIG_HELPER);
        }
        return crawlingConfigHelper;
    }

    public static CrawlingInfoHelper getCrawlingInfoHelper() {
        return getComponent(CRAWLING_INFO_HELPER);
    }

    public static PopularWordHelper getPopularWordHelper() {
        return getComponent(POPULAR_WORD_HELPER);
    }

    public static PathMappingHelper getPathMappingHelper() {
        return getComponent(PATH_MAPPING_HELPER);
    }

    public static DuplicateHostHelper getDuplicateHostHelper() {
        return getComponent(DUPLICATE_HOST_HELPER);
    }

    public static ProcessHelper getProcessHelper() {
        return getComponent(PROCESS_HELPER);
    }

    public static JobHelper getJobHelper() {
        return getComponent(JOB_HELPER);
    }

    public static WebApiManagerFactory getWebApiManagerFactory() {
        return getComponent(WEB_API_MANAGER_FACTORY);
    }

    public static UserAgentHelper getUserAgentHelper() {
        return getComponent(USER_AGENT_HELPER);
    }

    public static DataStoreFactory getDataStoreFactory() {
        return getComponent(DATA_STORE_FACTORY);
    }

    public static IntervalControlHelper getIntervalControlHelper() {
        return getComponent(INTERVAL_CONTROL_HELPER);
    }

    public static ExtractorFactory getExtractorFactory() {
        return getComponent(EXTRACTOR_FACTORY);
    }

    public static JobExecutor getJobExecutor(final String name) {
        return getComponent(name + JOB_EXECUTOR_SUFFIX);
    }

    public static FileTypeHelper getFileTypeHelper() {
        return getComponent(FILE_TYPE_HELPER);
    }

    public static IndexUpdater getIndexUpdater() {
        return getComponent(INDEX_UPDATER);
    }

    public static String getUserAgentName() {
        return getComponent(USER_AGENT_NAME);
    }

    public static KeyMatchHelper getKeyMatchHelper() {
        return getComponent(KEY_MATCH_HELPER);
    }

    public static IndexingHelper getIndexingHelper() {
        if (indexingHelper == null || HotdeployUtil.isHotdeploy()) {
            indexingHelper = getComponent(INDEXING_HELPER);
        }
        return indexingHelper;
    }

    public static UserInfoHelper getUserInfoHelper() {
        return getComponent(USER_INFO_HELPER);
    }

    public static MessageManager getMessageManager() {
        return getComponent(MESSAGE_MANAGER);
    }

    public static DictionaryManager getDictionaryManager() {
        return getComponent(DICTIONARY_MANAGER);
    }

    public static DataService<EsAccessResult> getDataService() {
        return getComponent(DATA_SERVICE);
    }

    public static FessEsClient getFessEsClient() {
        return getComponent(FESS_ES_CLIENT);
    }

    public static FessConfig getFessConfig() {
        if (fessConfig != null) {
            return fessConfig;
        }
        return getComponent(FessConfig.class);
    }

    public static SuggestHelper getSuggestHelper() {
        return getComponent(SUGGEST_HELPER);
    }

    public static RoleQueryHelper getRoleQueryHelper() {
        return getComponent(ROLE_QUERY_HELPER);
    }

    public static LdapManager getLdapManager() {
        return getComponent(LDAP_MANAGER);
    }

    public static ActivityHelper getActivityHelper() {
        return getComponent(ACTIVITY_HELPER);
    }

    public static RequestManager getRequestManager() {
        return getComponent(RequestManager.class);
    }

    public static ResponseManager getResponseManager() {
        return getComponent(ResponseManager.class);
    }

    public static JobManager getJobManager() {
        return getComponent(JobManager.class);
    }

    public static DocumentHelper getDocumentHelper() {
        return getComponent(DOCUMENT_HELPER);
    }

    public static QueryParser getQueryParser() {
        return getComponent(QUERY_PARSER);
    }

    public static PermissionHelper getPermissionHelper() {
        return getComponent(PERMISSION_HELPER);
    }

    public static SsoManager getSsoManager() {
        return getComponent(SSO_MANAGER);
    }

    public static ThumbnailManager getThumbnailManager() {
        return getComponent(THUMBNAIL_MANAGER);
    }

    public static PrimaryCipher getPrimaryCipher() {
        return getComponent(PrimaryCipher.class);
    }

    public static CrawlerClientFactory getCrawlerClientFactory() {
        return getComponent(CrawlerClientFactory.class);
    }

    public static <T> T getComponent(final Class<T> clazz) {
        try {
            return SingletonLaContainer.getComponent(clazz);
        } catch (final NullPointerException e) {
            if (logger.isDebugEnabled()) {
                throw new ContainerNotAvailableException(clazz.getCanonicalName(), e);
            } else {
                throw new ContainerNotAvailableException(clazz.getCanonicalName());
            }
        }
    }

    public static <T> T getComponent(final String componentName) {
        try {
            return SingletonLaContainer.getComponent(componentName);
        } catch (final NullPointerException e) {
            if (logger.isDebugEnabled()) {
                throw new ContainerNotAvailableException(componentName, e);
            } else {
                throw new ContainerNotAvailableException(componentName);
            }
        }
    }

    public static boolean hasQueryHelper() {
        return SingletonLaContainerFactory.getContainer().hasComponentDef(QUERY_HELPER);
    }

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
            FessProp.propMap.clear();
        }
    }

}
