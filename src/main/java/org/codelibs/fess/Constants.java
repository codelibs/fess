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
package org.codelibs.fess;

import java.util.TimeZone;
import java.util.regex.Pattern;

import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.lang.StringUtil;

public class Constants extends CoreLibConstants {
    public static final int MAJOR_VERSION = 10;

    public static final int MINOR_VERSION = 1;

    public static final String FESS_VERSION = String.valueOf(MAJOR_VERSION) + "." + String.valueOf(MINOR_VERSION);

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final int DEFAULT_ADMIN_PAGE_NUMBER = 1;

    public static final int DEFAULT_ADMIN_PAGE_SIZE = 25;

    public static final String WEB_API_VERSION = FESS_VERSION;

    public static final String TRUE = "true";

    public static final String FALSE = "false";

    public static final Boolean T = true;

    public static final Boolean F = false;

    public static final String ON = "on";

    public static final String READY = "ready";

    public static final String RUNNING = "running";

    public static final String DONE = "done";

    public static final String OK = "ok";

    public static final String FAIL = "fail";

    public static final String STOP = "stop";

    public static final String ITEM_LABEL = "label";

    public static final String ITEM_VALUE = "value";

    public static final String ITEM_NAME = "name";

    public static final String UTF_8 = "UTF-8";

    public static final String MS932 = "MS932";

    public static final String DEFAULT_CRON_EXPRESSION = "0 0 * * *";

    public static final String DEFAULT_SEARCH_LOG_CRON_EXPRESSION = "* * * * *";

    public static final String DEFAULT_DAILY_CRON_EXPRESSION = "0 0 * * *";

    public static final String DEFAULT_HOURLY_CRON_EXPRESSION = "0 * * * *";

    public static final int DEFAULT_INTERVAL_TIME_FOR_FS = 1000;

    public static final int DEFAULT_INTERVAL_TIME_FOR_WEB = 30000;

    public static final int DEFAULT_NUM_OF_THREAD_FOR_FS = 5;

    public static final int DEFAULT_NUM_OF_THREAD_FOR_WEB = 3;

    public static final long DEFAULT_CRAWLING_EXECUTION_INTERVAL = 5000L;

    // fess properties
    public static final String USER_INFO_PROPERTY = "user.info";

    public static final String USER_FAVORITE_PROPERTY = "user.favorite";

    public static final String SEARCH_LOG_PROPERTY = "search.log";

    public static final String APPEND_QUERY_PARAMETER_PROPERTY = "append.query.parameter";

    public static final String INCREMENTAL_CRAWLING_PROPERTY = "crawling.incremental";

    public static final String CRAWLING_THREAD_COUNT_PROPERTY = "crawling.thread.count";

    public static final String DAY_FOR_CLEANUP_PROPERTY = "day.for.cleanup";

    public static final String WEB_API_JSON_PROPERTY = "web.api.json";

    public static final String WEB_API_SUGGEST_PROPERTY = "web.api.suggest";

    public static final String WEB_API_GSA_PROPERTY = "web.api.gsa";

    public static final String WEB_API_POPULAR_WORD_PROPERTY = "web.api.popularword";

    public static final String WEB_DESIGN_EDITOR_PROPERTY = "design.editor";

    public static final String DEFAULT_LABEL_VALUE_PROPERTY = "label.value";

    public static final String DEFAULT_SORT_VALUE_PROPERTY = "sort.value";

    public static final String LOGIN_REQUIRED_PROPERTY = "login.required";

    public static final String IGNORE_FAILURE_TYPE_PROPERTY = "failure.ignoretype";

    public static final String FAILURE_COUNT_THRESHOLD_PROPERTY = "failure.countthreshold";

    public static final String CSV_FILE_ENCODING_PROPERTY = "csv.file.encoding";

    public static final String PURGE_SEARCH_LOG_DAY_PROPERTY = "purge.searchlog.day";

    public static final String PURGE_USER_INFO_DAY_PROPERTY = "purge.userinfo.day";

    public static final String PURGE_JOB_LOG_DAY_PROPERTY = "purge.joblog.day";

    public static final String PURGE_BY_BOTS_PROPERTY = "purge.by.bots";

    public static final String SEARCH_FILE_PROXY_PROPERTY = "search.file.proxy";

    public static final String NOTIFICATION_TO_PROPERTY = "notification.to";

    public static final String USE_BROWSER_LOCALE_FOR_SEARCH_PROPERTY = "search.use.browser.locale";

    public static final String SUGGEST_SEARCH_LOG_PROPERTY = "suggest.searchlog";

    public static final String SUGGEST_DOCUMENTS_PROPERTY = "suggest.document";

    public static final String PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY = "purge.suggest.searchlog.day";

    public static final String HIGHLIGHT_QUERIES = "org.codelibs.fess.Queries";

    public static final String FIELD_LOGS = "org.codelibs.fess.FieldLogs";

    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final int DONE_STATUS = 9999;

    public static final String DEFAULT_IGNORE_FAILURE_TYPE = StringUtil.EMPTY;

    public static final Integer DEFAULT_FAILURE_COUNT = -1;

    public static final String DEFAULT_PURGE_DAY = "-1";

    public static final String DEFAULT_SUGGEST_PURGE_DAY = "30";

    public static final String DEFAULT_PURGE_BY_BOTS = "Crawler"//
            + ",crawler"//
            + ",Bot"//
            + ",bot"//
            + ",Slurp"//
            + ",Yeti"//
            + ",Baidu"//
            + ",Steeler"//
            + ",ichiro"//
            + ",hotpage"//
            + ",Feedfetcher"//
            + ",ia_archiver"//
            + ",Y!J-BRI"//
            + ",Google Desktop"//
            + ",Seznam"//
            + ",Tumblr"//
            + ",YandexBot"//
            + ",Chilkat"//
            + ",CloudFront"//
            + ",Mediapartners";

    public static final String DEFAULT_FROM_EMAIL = "Administrator <root@localhost>";

    // info map

    public static final String CRAWLER_STATUS = "CrawlerStatus";

    public static final String CRAWLER_START_TIME = "CrawlerStartTime";

    public static final String CRAWLER_END_TIME = "CrawlerEndTime";

    public static final String CRAWLER_EXEC_TIME = "CrawlerExecTime";

    public static final String WEB_FS_CRAWLER_START_TIME = "WebFsCrawlStartTime";

    public static final String WEB_FS_CRAWLER_END_TIME = "WebFsCrawlEndTime";

    public static final String DATA_CRAWLER_START_TIME = "DataCrawlStartTime";

    public static final String DATA_CRAWLER_END_TIME = "DataCrawlEndTime";

    public static final String WEB_FS_CRAWLING_EXEC_TIME = "WebFsCrawlExecTime";

    public static final String WEB_FS_INDEX_EXEC_TIME = "WebFsIndexExecTime";

    public static final String WEB_FS_INDEX_SIZE = "WebFsIndexSize";

    public static final String DATA_CRAWLING_EXEC_TIME = "DataCrawlExecTime";

    public static final String DATA_INDEX_EXEC_TIME = "DataIndexExecTime";

    public static final String DATA_INDEX_SIZE = "DataIndexSize";

    public static final String SESSION_ID = "sessionId";

    public static final String CRAWLING_INFO_ID = "crawlingInfoId";

    public static final String INDEXING_TARGET = "indexingTarget";

    public static final String NUM_OF_THREADS = "numOfThreads";

    public static final String BASIC = "BASIC";

    public static final String DIGEST = "DIGEST";

    public static final String NTLM = "NTLM";

    public static final String SAMBA = "SAMBA";

    public static final String[] RESERVED = { "+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "~", "*", "?", "\\", ";", ":",
            "/" };

    public static final Pattern LUCENE_FIELD_RESERVED_PATTERN = Pattern.compile("([+\\-!\\(\\){}\\[\\]^\"~\\\\:\\p{Zs}]|(&&)|(\\|\\|))"); // "*", "?",

    public static final Pattern LUCENE_RANGE_FIELD_RESERVED_PATTERN = Pattern.compile("([!\\(\\){}\\[\\]\"~\\\\:\\p{Zs}]|(&&)|(\\|\\|))");

    public static final String SEARCH_LOG_ACCESS_TYPE = "searchLogAccessType";

    public static final String SEARCH_LOG_ACCESS_TYPE_JSON = "json";

    public static final String SEARCH_LOG_ACCESS_TYPE_XML = "xml";

    public static final String SEARCH_LOG_ACCESS_TYPE_WEB = "web";

    public static final String SEARCH_LOG_ACCESS_TYPE_OTHER = "other";

    public static final String RESULTS_PER_PAGE = "resultsPerPage";

    public static final String USER_CODE = "userCode";

    public static final String SEARCH_FIELD_LOG_SEARCH_QUERY = "q";

    public static final String STATS_REPORT_TYPE = "reportType";

    public static final String RESULT_DOC_ID_CACHE = "resultDocIds";

    public static final String SCREEN_SHOT_PATH_CACHE = "screenShotPaths";

    public static final String CRAWLING_INFO_SYSTEM_NAME = "system";

    // view parameters

    public static final String FACET_QUERY = "org.codelibs.fess.tag.FacetQuery";

    public static final String GEO_QUERY = "org.codelibs.fess.tag.GeoQuery";

    public static final String FACET_FORM = "org.codelibs.fess.tag.FacetForm";

    public static final String GEO_FORM = "org.codelibs.fess.tag.GeoForm";

    public static final String LABEL_VALUE_MAP = "org.codelibs.fess.LabelValueMap";

    public static final String OPTION_QUERY_Q = "q";

    public static final String OPTION_QUERY_CQ = "cq";

    public static final String OPTION_QUERY_OQ = "oq";

    public static final String OPTION_QUERY_NQ = "nq";

    // job

    public static final String SCHEDULED_JOB = "scheduledJob";

    public static final String DEFAULT_JOB_TARGET = "all";

    public static final String DEFAULT_JOB_SCRIPT_TYPE = "groovy";

    public static final int EXIT_OK = 0;

    public static final int EXIT_FAIL = 1;

    public static final String DCF = "dcf";

    public static final String ALL_LANGUAGES = "all";

    public static final String INVALID_NUMERIC_PARAMETER = "-1";

    public static final String FACET_FIELD_PREFIX = "field:";

    public static final String FACET_QUERY_PREFIX = "query:";

    public static final String MATCHES_ALL_QUERY = "*:*";

    public static final String FESS_ES_TRANSPORT_ADDRESSES = "fess.es.transport_addresses";

    public static final String FESS_ES_CLUSTER_NAME = "fess.es.cluster_name";

    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final int DEFAULT_START_COUNT = 0;

    public static final String PROCESS_TYPE_CRAWLING = "C";

    public static final String PROCESS_TYPE_DISPLAYING = "D";

    public static final String PROCESS_TYPE_BOTH = "B";

    public static final long ONE_DAY_IN_MILLIS = 24L * 60L * 60L * 1000L;

    public static final String GUEST_USER = "guest";

    public static final String[] PAGER_CONVERSION_RULE = { "allRecordCount", "pageSize", "currentPageNumber", "allPageCount",
            "existPrePage", "existNextPage" };

    // crawler types
    public static final String WEB_CRAWLER_TYPE = "web_crawling";

    public static final String FILE_CRAWLER_TYPE = "file_crawling";

    public static final String DATA_CRAWLER_TYPE = "data_crawling";

    // TODO remove searchParams
    public static final String[] COMMON_CONVERSION_RULE = new String[] { "searchParams", "crudMode", "createdBy", "createdTime",
            "updatedBy", "updatedTime" };

    public static final String USER_INFO = "LoginInfo";

    public static final String ES_API_ACCESS_TOKEN = "esApiAccessToken";

    public static final String ADMIN_PACKAGE = "org.codelibs.fess.app.web.admin";

    public static final String DEFAULT_FIELD = "_default";

    public static final Integer DEFAULT_DAY_FOR_CLEANUP = 3;

    public static final String FESS_CONF_PATH = "fess.conf.path";

    public static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");

    public static final String LDAP_BASE_DN = "ldap.base.dn";

    public static final String LDAP_SECURITY_PRINCIPAL = "ldap.security.principal";

    public static final String LDAP_PROVIDER_URL = "ldap.provider.url";

    public static final String LDAP_SECURITY_AUTHENTICATION = "ldap.security.authentication";

    public static final String LDAP_INITIAL_CONTEXT_FACTORY = "ldap.initial.context.factory";

    public static final String LDAP_ACCOUNT_FILTER = "ldap.account.filter";

    public static final String NOTIFICATION_LOGIN = "notification.login";

    public static final String NOTIFICATION_SEARCH_TOP = "notification.search.top";

    public static final String MAPPING_TYPE_ARRAY = "array";

    public static final String MAPPING_TYPE_STRING = "string";

    public static final String MAPPING_TYPE_LONG = "long";

    public static final String MAPPING_TYPE_DOUBLE = "double";

    public static final String PAGING_QUERY_LIST = "pagingQueryList";

    public static final String REQUEST_LANGUAGES = "requestLanguages";

    public static final String SEARCH_PREFERENCE_PRIMARY = "_primary";

    public static final String CONFIG_IGNORE_FAILURE_URLS = "ignore.failureUrls";

    public static final String CONFIG_CLEANUP_FILTERS = "cleanup.urlFilters";

    public static final String CONFIG_CLEANUP_ALL = "cleanup.all";

    public static final String GSA_API_VERSION = "3.2";
}
