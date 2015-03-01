/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.fess.api.WebApiManagerFactory;
import org.codelibs.fess.ds.DataStoreFactory;
import org.codelibs.fess.helper.AdRoleHelper;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingSessionHelper;
import org.codelibs.fess.helper.DatabaseHelper;
import org.codelibs.fess.helper.DocumentHelper;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.HotSearchWordHelper;
import org.codelibs.fess.helper.IndexingHelper;
import org.codelibs.fess.helper.IntervalControlHelper;
import org.codelibs.fess.helper.JobHelper;
import org.codelibs.fess.helper.KeyMatchHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.MailHelper;
import org.codelibs.fess.helper.OverlappingHostHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.QueryHelper;
import org.codelibs.fess.helper.SambaHelper;
import org.codelibs.fess.helper.SearchLogHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.helper.UserAgentHelper;
import org.codelibs.fess.helper.ViewHelper;
import org.codelibs.fess.job.JobExecutor;
import org.codelibs.fess.service.SearchService;
import org.codelibs.fess.solr.IndexUpdater;
import org.codelibs.robot.extractor.ExtractorFactory;
import org.codelibs.solr.lib.SolrGroupManager;
import org.seasar.framework.container.SingletonS2Container;

public final class ComponentUtil {
    private static final String USER_AGENT_NAME = "userAgentName";

    private static final String INDEX_UPDATER = "indexUpdater";

    private static final String DATABASE_HELPER = "databaseHelper";

    private static final String MAIL_HELPER = "mailHelper";

    private static final String FILE_TYPE_HELPER = "fileTypeHelper";

    private static final String EXTRACTOR_FACTORY = "extractorFactory";

    private static final String INTERVAL_CONTROL_HELPER = "intervalControlHelper";

    private static final String DATA_STORE_FACTORY = "dataStoreFactory";

    private static final String USER_AGENT_HELPER = "userAgentHelper";

    private static final String WEB_API_MANAGER_FACTORY = "webApiManagerFactory";

    private static final String DOCUMENT_HELPER = "documentHelper";

    private static final String JOB_HELPER = "jobHelper";

    private static final String OVERLAPPING_HOST_HELPER = "overlappingHostHelper";

    private static final String PATH_MAPPING_HELPER = "pathMappingHelper";

    private static final String HOT_SEARCH_WORD_HELPER = "hotSearchWordHelper";

    private static final String CRAWLING_SESSION_HELPER = "crawlingSessionHelper";

    private static final String CRAWLING_CONFIG_HELPER = "crawlingConfigHelper";

    private static final String SEARCH_LOG_HELPER = "searchLogHelper";

    private static final String LABEL_TYPE_HELPER = "labelTypeHelper";

    private static final String QUERY_HELPER = "queryHelper";

    private static final String SAMBA_HELPER = "sambaHelper";

    private static final String VIEW_HELPER = "viewHelper";

    private static final String SYSTEM_HELPER = "systemHelper";

    private static final String AD_ROLE_HELPER = "adRoleHelper";

    private static final String SOLR_GROUP_MANAGER = "solrGroupManager";

    private static final String CRAWLER_PROPERTIES = "crawlerProperties";

    private static final String PROPERTIES_SUFFIX = "Properties";

    private static final String QUERY_RESPONSE_LIST = "queryResponseList";

    private static final String JOB_EXECUTOR_SUFFIX = "JobExecutor";

    private static final String SEARCH_SERVICE = "searchService";

    private static final String KEY_MATCH_HELPER = "keyMatchHelper";

    private static final String INDEXING_HELPER = "indexingHelper";

    private static final String FIELD_HELPER = "fieldHelper";

    private ComponentUtil() {
    }

    public static CachedCipher getCipher(final String cipherName) {
        return SingletonS2Container.getComponent(cipherName);
    }

    public static QueryResponseList getQueryResponseList() {
        return SingletonS2Container.getComponent(QUERY_RESPONSE_LIST);
    }

    public static DynamicProperties getSolrGroupProperties(
            final String groupName) {
        return SingletonS2Container.getComponent(groupName + PROPERTIES_SUFFIX);
    }

    public static DynamicProperties getCrawlerProperties() {
        return SingletonS2Container.getComponent(CRAWLER_PROPERTIES);
    }

    public static SolrGroupManager getSolrGroupManager() {
        return SingletonS2Container.getComponent(SOLR_GROUP_MANAGER);
    }

    public static SystemHelper getSystemHelper() {
        return SingletonS2Container.getComponent(SYSTEM_HELPER);
    }

    public static ViewHelper getViewHelper() {
        return SingletonS2Container.getComponent(VIEW_HELPER);
    }

    public static SambaHelper getSambaHelper() {
        return SingletonS2Container.getComponent(SAMBA_HELPER);
    }

    public static QueryHelper getQueryHelper() {
        return SingletonS2Container.getComponent(QUERY_HELPER);
    }

    public static LabelTypeHelper getLabelTypeHelper() {
        return SingletonS2Container.getComponent(LABEL_TYPE_HELPER);
    }

    public static SearchLogHelper getSearchLogHelper() {
        return SingletonS2Container.getComponent(SEARCH_LOG_HELPER);
    }

    public static CrawlingConfigHelper getCrawlingConfigHelper() {
        return SingletonS2Container.getComponent(CRAWLING_CONFIG_HELPER);
    }

    public static CrawlingSessionHelper getCrawlingSessionHelper() {
        return SingletonS2Container.getComponent(CRAWLING_SESSION_HELPER);
    }

    public static HotSearchWordHelper getHotSearchWordHelper() {
        return SingletonS2Container.getComponent(HOT_SEARCH_WORD_HELPER);
    }

    public static PathMappingHelper getPathMappingHelper() {
        return SingletonS2Container.getComponent(PATH_MAPPING_HELPER);
    }

    public static OverlappingHostHelper getOverlappingHostHelper() {
        return SingletonS2Container.getComponent(OVERLAPPING_HOST_HELPER);
    }

    public static JobHelper getJobHelper() {
        return SingletonS2Container.getComponent(JOB_HELPER);
    }

    public static DocumentHelper getDocumentHelper() {
        return SingletonS2Container.getComponent(DOCUMENT_HELPER);
    }

    public static WebApiManagerFactory getWebApiManagerFactory() {
        return SingletonS2Container.getComponent(WEB_API_MANAGER_FACTORY);
    }

    public static UserAgentHelper getUserAgentHelper() {
        return SingletonS2Container.getComponent(USER_AGENT_HELPER);
    }

    public static DataStoreFactory getDataStoreFactory() {
        return SingletonS2Container.getComponent(DATA_STORE_FACTORY);
    }

    public static IntervalControlHelper getIntervalControlHelper() {
        return SingletonS2Container.getComponent(INTERVAL_CONTROL_HELPER);
    }

    public static ExtractorFactory getExtractorFactory() {
        return SingletonS2Container.getComponent(EXTRACTOR_FACTORY);
    }

    public static JobExecutor getJobExecutor(final String name) {
        return SingletonS2Container.getComponent(name + JOB_EXECUTOR_SUFFIX);
    }

    public static MailHelper getMailHelper() {
        return SingletonS2Container.getComponent(MAIL_HELPER);
    }

    public static FileTypeHelper getFileTypeHelper() {
        return SingletonS2Container.getComponent(FILE_TYPE_HELPER);
    }

    public static DatabaseHelper getDatabaseHelper() {
        return SingletonS2Container.getComponent(DATABASE_HELPER);
    }

    public static AdRoleHelper getAdRoleHelper() {
        return SingletonS2Container.getComponent(AD_ROLE_HELPER);
    }

    public static IndexUpdater getIndexUpdater() {
        return SingletonS2Container.getComponent(INDEX_UPDATER);
    }

    public static String getUserAgentName() {
        return SingletonS2Container.getComponent(USER_AGENT_NAME);
    }

    public static SearchService getSearchService() {
        return SingletonS2Container.getComponent(SEARCH_SERVICE);
    }

    public static KeyMatchHelper getKeyMatchHelper() {
        return SingletonS2Container.getComponent(KEY_MATCH_HELPER);
    }

    public static IndexingHelper getIndexingHelper() {
        return SingletonS2Container.getComponent(INDEXING_HELPER);
    }

    public static FieldHelper getFieldHelper() {
        return SingletonS2Container.getComponent(FIELD_HELPER);
    }

    public static <T> T getComponent(Class<T> clazz) {
        return SingletonS2Container.getComponent(clazz);
    }
}
