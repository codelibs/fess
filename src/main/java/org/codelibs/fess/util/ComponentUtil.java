/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import org.codelibs.core.crypto.CachedCipher;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.api.WebApiManagerFactory;
import org.codelibs.fess.crawler.entity.EsAccessResult;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.service.DataService;
import org.codelibs.fess.dict.DictionaryManager;
import org.codelibs.fess.ds.DataStoreFactory;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.helper.AdRoleHelper;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.DuplicateHostHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.HotSearchWordHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.IntervalControlHelper;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.KeyMatchHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.SambaHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SuggestHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.UserAgentHelper;
import org.codelibs.fess.helper.UserInfoHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.indexer.IndexUpdater;
import org.codelibs.fess.job.JobExecutor;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.lastaflute.core.message.MessageManager;
import org.lastaflute.di.core.SingletonLaContainer;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;

public final class ComponentUtil {
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

    private static final String JOB_HELPER = "jobHelper";

    private static final String DUPLICATE_HOST_HELPER = "duplicateHostHelper";

    private static final String PATH_MAPPING_HELPER = "pathMappingHelper";

    private static final String HOT_SEARCH_WORD_HELPER = "hotSearchWordHelper";

    private static final String CRAWLING_INFO_HELPER = "crawlingInfoHelper";

    private static final String CRAWLING_CONFIG_HELPER = "crawlingConfigHelper";

    private static final String SEARCH_LOG_HELPER = "searchLogHelper";

    private static final String LABEL_TYPE_HELPER = "labelTypeHelper";

    private static final String QUERY_HELPER = "queryHelper";

    private static final String SAMBA_HELPER = "sambaHelper";

    private static final String VIEW_HELPER = "viewHelper";

    private static final String SYSTEM_HELPER = "systemHelper";

    private static final String AD_ROLE_HELPER = "adRoleHelper";

    private static final String CRAWLER_PROPERTIES = "crawlerProperties";

    private static final String PROPERTIES_SUFFIX = "Properties";

    private static final String QUERY_RESPONSE_LIST = "queryResponseList";

    private static final String JOB_EXECUTOR_SUFFIX = "JobExecutor";

    private static final String KEY_MATCH_HELPER = "keyMatchHelper";

    private static final String INDEXING_HELPER = "indexingHelper";

    private static final String ELASTICSEARCH_CLIENT = FESS_ES_CLIENT;

    private ComponentUtil() {
    }

    public static CachedCipher getCipher(final String cipherName) {
        return SingletonLaContainer.getComponent(cipherName);
    }

    public static QueryResponseList getQueryResponseList() {
        return SingletonLaContainer.getComponent(QUERY_RESPONSE_LIST);
    }

    public static DynamicProperties getSolrGroupProperties(final String groupName) {
        return SingletonLaContainer.getComponent(groupName + PROPERTIES_SUFFIX);
    }

    public static DynamicProperties getCrawlerProperties() {
        return SingletonLaContainer.getComponent(CRAWLER_PROPERTIES);
    }

    public static SystemHelper getSystemHelper() {
        return SingletonLaContainer.getComponent(SYSTEM_HELPER);
    }

    public static ViewHelper getViewHelper() {
        return SingletonLaContainer.getComponent(VIEW_HELPER);
    }

    public static SambaHelper getSambaHelper() {
        return SingletonLaContainer.getComponent(SAMBA_HELPER);
    }

    public static QueryHelper getQueryHelper() {
        return SingletonLaContainer.getComponent(QUERY_HELPER);
    }

    public static LabelTypeHelper getLabelTypeHelper() {
        return SingletonLaContainer.getComponent(LABEL_TYPE_HELPER);
    }

    public static SearchLogHelper getSearchLogHelper() {
        return SingletonLaContainer.getComponent(SEARCH_LOG_HELPER);
    }

    public static CrawlingConfigHelper getCrawlingConfigHelper() {
        return SingletonLaContainer.getComponent(CRAWLING_CONFIG_HELPER);
    }

    public static CrawlingInfoHelper getCrawlingInfoHelper() {
        return SingletonLaContainer.getComponent(CRAWLING_INFO_HELPER);
    }

    public static HotSearchWordHelper getHotSearchWordHelper() {
        return SingletonLaContainer.getComponent(HOT_SEARCH_WORD_HELPER);
    }

    public static PathMappingHelper getPathMappingHelper() {
        return SingletonLaContainer.getComponent(PATH_MAPPING_HELPER);
    }

    public static DuplicateHostHelper getDuplicateHostHelper() {
        return SingletonLaContainer.getComponent(DUPLICATE_HOST_HELPER);
    }

    public static JobHelper getJobHelper() {
        return SingletonLaContainer.getComponent(JOB_HELPER);
    }

    public static WebApiManagerFactory getWebApiManagerFactory() {
        return SingletonLaContainer.getComponent(WEB_API_MANAGER_FACTORY);
    }

    public static UserAgentHelper getUserAgentHelper() {
        return SingletonLaContainer.getComponent(USER_AGENT_HELPER);
    }

    public static DataStoreFactory getDataStoreFactory() {
        return SingletonLaContainer.getComponent(DATA_STORE_FACTORY);
    }

    public static IntervalControlHelper getIntervalControlHelper() {
        return SingletonLaContainer.getComponent(INTERVAL_CONTROL_HELPER);
    }

    public static ExtractorFactory getExtractorFactory() {
        return SingletonLaContainer.getComponent(EXTRACTOR_FACTORY);
    }

    public static JobExecutor getJobExecutor(final String name) {
        return SingletonLaContainer.getComponent(name + JOB_EXECUTOR_SUFFIX);
    }

    public static FileTypeHelper getFileTypeHelper() {
        return SingletonLaContainer.getComponent(FILE_TYPE_HELPER);
    }

    public static AdRoleHelper getAdRoleHelper() {
        return SingletonLaContainer.getComponent(AD_ROLE_HELPER);
    }

    public static IndexUpdater getIndexUpdater() {
        return SingletonLaContainer.getComponent(INDEX_UPDATER);
    }

    public static String getUserAgentName() {
        return SingletonLaContainer.getComponent(USER_AGENT_NAME);
    }

    public static KeyMatchHelper getKeyMatchHelper() {
        return SingletonLaContainer.getComponent(KEY_MATCH_HELPER);
    }

    public static IndexingHelper getIndexingHelper() {
        return SingletonLaContainer.getComponent(INDEXING_HELPER);
    }

    public static UserInfoHelper getUserInfoHelper() {
        return SingletonLaContainer.getComponent(USER_INFO_HELPER);
    }

    public static FessEsClient getElasticsearchClient() {
        return SingletonLaContainer.getComponent(ELASTICSEARCH_CLIENT);
    }

    public static MessageManager getMessageManager() {
        return SingletonLaContainer.getComponent(MESSAGE_MANAGER);
    }

    public static DictionaryManager getDictionaryManager() {
        return SingletonLaContainer.getComponent(DICTIONARY_MANAGER);
    }

    public static DataService<EsAccessResult> getDataService() {
        return SingletonLaContainer.getComponent(DATA_SERVICE);
    }

    public static FessEsClient getFessEsClient() {
        return SingletonLaContainer.getComponent(FESS_ES_CLIENT);
    }

    public static FessConfig getFessConfig() {
        return SingletonLaContainer.getComponent(FessConfig.class);
    }

    public static <T> T getComponent(final Class<T> clazz) {
        return SingletonLaContainer.getComponent(clazz);
    }

    public static boolean hasQueryHelper() {
        return SingletonLaContainerFactory.getContainer().hasComponentDef(QUERY_HELPER);
    }

    public static SuggestHelper getSuggestHelper() {
        return getComponent(SuggestHelper.class);
    }
}
