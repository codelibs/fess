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
package org.codelibs.fess;

import java.util.TimeZone;
import java.util.regex.Pattern;

import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.lang.StringUtil;

/**
 * Constants class that extends CoreLibConstants and contains application-wide constant values for the Fess search engine.
 * This class provides constants for system configuration, crawling and indexing defaults, user agent strings,
 * status values, field names, date/time formats, authentication types, and various reserved words and patterns.
 *
 * <p>Key constant categories include:</p>
 * <ul>
 * <li>System configuration property keys</li>
 * <li>Default values for crawling and indexing operations</li>
 * <li>User agent strings and patterns for web crawling</li>
 * <li>Status constants for job execution and system states</li>
 * <li>Field names and keys used throughout the application</li>
 * <li>Date/time format patterns</li>
 * <li>Authentication type identifiers</li>
 * <li>Reserved words and regex patterns for query processing</li>
 * </ul>
 */
public class Constants extends CoreLibConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * This class contains only static constants and should not be instantiated.
     */
    private Constants() {
        // Utility class - no instantiation
    }

    // ============================================================
    // System and UI Constants
    // ============================================================

    /** System line separator character sequence. */
    public static final String LINE_SEPARATOR = System.lineSeparator();

    /** Default page number for admin interface pagination. */
    public static final int DEFAULT_ADMIN_PAGE_NUMBER = 1;

    /** String constant representing boolean true value. */
    public static final String TRUE = "true";

    /** String constant representing boolean false value. */
    public static final String FALSE = "false";

    /** Boolean constant representing true value. */
    public static final Boolean T = true;

    /** Boolean constant representing false value. */
    public static final Boolean F = false;

    /** Constant for score field identifier. */
    public static final String SCORE = "score";

    /** Constant for searcher identifier. */
    public static final String SEARCHER = "searcher";

    /** Constant representing "on" state. */
    public static final String ON = "on";

    // ============================================================
    // Status Constants
    // ============================================================

    /** Status constant representing ready state. */
    public static final String READY = "ready";

    /** Status constant representing running state. */
    public static final String RUNNING = "running";

    /** Status constant representing done/completed state. */
    public static final String DONE = "done";

    /** Status constant representing successful operation. */
    public static final String OK = "ok";

    /** Status constant representing failed operation. */
    public static final String FAIL = "fail";

    /** Status constant representing stopped state. */
    public static final String STOP = "stop";

    /** Constant representing automatic mode. */
    public static final String AUTO = "auto";

    /** Constant representing no value or empty state. */
    public static final String NONE = "none";

    // ============================================================
    // Item/Field Identifiers
    // ============================================================

    /** Item property key for label field. */
    public static final String ITEM_LABEL = "label";

    /** Item property key for value field. */
    public static final String ITEM_VALUE = "value";

    /** Item property key for name field. */
    public static final String ITEM_NAME = "name";

    /** Character encoding constant for Japanese MS932. */
    public static final String MS932 = "MS932";

    // ============================================================
    // Default Configuration Values
    // ============================================================

    /** Default cron expression for general scheduling (daily at midnight). */
    public static final String DEFAULT_CRON_EXPRESSION = "0 0 * * *";

    /** Default cron expression for search log processing (every minute). */
    public static final String DEFAULT_SEARCH_LOG_CRON_EXPRESSION = "* * * * *";

    /** Default cron expression for daily tasks (daily at midnight). */
    public static final String DEFAULT_DAILY_CRON_EXPRESSION = "0 0 * * *";

    /** Default cron expression for hourly tasks (every hour). */
    public static final String DEFAULT_HOURLY_CRON_EXPRESSION = "0 * * * *";

    /** Default interval time in milliseconds for file system crawling. */
    public static final int DEFAULT_INTERVAL_TIME_FOR_FS = 1000;

    /** Default interval time in milliseconds for web crawling. */
    public static final int DEFAULT_INTERVAL_TIME_FOR_WEB = 10000;

    /** Default number of threads for file system crawling. */
    public static final int DEFAULT_NUM_OF_THREAD_FOR_FS = 5;

    /** Default number of threads for web crawling. */
    public static final int DEFAULT_NUM_OF_THREAD_FOR_WEB = 1;

    /** Default execution interval in milliseconds for crawling operations. */
    public static final long DEFAULT_CRAWLING_EXECUTION_INTERVAL = 5000L;

    // ============================================================
    // User Agent Configuration
    // ============================================================

    /** Prefix for Fess crawler user agent string. */
    public static final String CRAWLING_USER_AGENT_PREFIX = "Mozilla/5.0 (compatible; Fess/";

    /** Suffix for Fess crawler user agent string with bot information URL. */
    public static final String CRAWLING_USER_AGENT_SUFFIX = "; +http://fess.codelibs.org/bot.html)";

    /** Date format pattern for document index suffix generation. */
    public static final String DOCUMENT_INDEX_SUFFIX_PATTERN = "yyyyMMddHHmmssSSS";

    // ============================================================
    // System Property Keys
    // ============================================================

    /** Property key for user information configuration. */
    public static final String USER_INFO_PROPERTY = "user.info";

    /** Property key for user favorite functionality configuration. */
    public static final String USER_FAVORITE_PROPERTY = "user.favorite";

    /** Property key for search log configuration. */
    public static final String SEARCH_LOG_PROPERTY = "search.log";

    /** Property key for appending query parameters configuration. */
    public static final String APPEND_QUERY_PARAMETER_PROPERTY = "append.query.parameter";

    /** Property key for incremental crawling configuration. */
    public static final String INCREMENTAL_CRAWLING_PROPERTY = "crawling.incremental";

    /** Property key for crawling thread count configuration. */
    public static final String CRAWLING_THREAD_COUNT_PROPERTY = "crawling.thread.count";

    /** Property key for crawling user agent configuration. */
    public static final String CRAWLING_USER_AGENT_PROPERTY = "crawling.user.agent";

    /** Property key for cleanup day interval configuration. */
    public static final String DAY_FOR_CLEANUP_PROPERTY = "day.for.cleanup";

    // ============================================================
    // Web API Property Keys
    // ============================================================

    /** Property key for JSON web API configuration. */
    public static final String WEB_API_JSON_PROPERTY = "web.api.json";

    /** Property key for suggest web API configuration. */
    public static final String WEB_API_SUGGEST_PROPERTY = "web.api.suggest";

    /** Property key for GSA web API configuration. */
    public static final String WEB_API_GSA_PROPERTY = "web.api.gsa";

    /** Property key for popular word web API configuration. */
    public static final String WEB_API_POPULAR_WORD_PROPERTY = "web.api.popularword";

    /** Property key for system properties configuration. */
    public static final String APP_VALUE_PROPERTY = "system.properties";

    /** Property key for default label value configuration. */
    public static final String DEFAULT_LABEL_VALUE_PROPERTY = "label.value";

    /** Property key for default sort value configuration. */
    public static final String DEFAULT_SORT_VALUE_PROPERTY = "sort.value";

    /** Property key for virtual host value configuration. */
    public static final String VIRTUAL_HOST_VALUE_PROPERTY = "virtual.host.value";

    /** Property key for login requirement configuration. */
    public static final String LOGIN_REQUIRED_PROPERTY = "login.required";

    /** Property key for result collapse configuration. */
    public static final String RESULT_COLLAPSED_PROPERTY = "result.collapsed";

    /** Property key for login link enabled configuration. */
    public static final String LOGIN_LINK_ENALBED_PROPERTY = "login.link.enabled";

    /** Property key for thumbnail enabled configuration. */
    public static final String THUMBNAIL_ENALBED_PROPERTY = "thumbnail.enabled";

    /** Property key for failure type ignore configuration. */
    public static final String IGNORE_FAILURE_TYPE_PROPERTY = "failure.ignoretype";

    /** Property key for failure count threshold configuration. */
    public static final String FAILURE_COUNT_THRESHOLD_PROPERTY = "failure.countthreshold";

    /** Property key for CSV file encoding configuration. */
    public static final String CSV_FILE_ENCODING_PROPERTY = "csv.file.encoding";

    // ============================================================
    // Data Purge Property Keys
    // ============================================================

    /** Property key for search log purge day configuration. */
    public static final String PURGE_SEARCH_LOG_DAY_PROPERTY = "purge.searchlog.day";

    /** Property key for user info purge day configuration. */
    public static final String PURGE_USER_INFO_DAY_PROPERTY = "purge.userinfo.day";

    /** Property key for job log purge day configuration. */
    public static final String PURGE_JOB_LOG_DAY_PROPERTY = "purge.joblog.day";

    /** Property key for bot-based purge configuration. */
    public static final String PURGE_BY_BOTS_PROPERTY = "purge.by.bots";

    /** Property key for search file proxy configuration. */
    public static final String SEARCH_FILE_PROXY_PROPERTY = "search.file.proxy";

    // ============================================================
    // Notification Property Keys
    // ============================================================

    /** Property key for notification recipient configuration. */
    public static final String NOTIFICATION_TO_PROPERTY = "notification.to";

    /** Property key for Slack webhook URLs configuration. */
    public static final String SLACK_WEBHOOK_URLS_PROPERTY = "slack.webhook.urls";

    /** Property key for Google Chat webhook URLs configuration. */
    public static final String GOOGLE_CHAT_WEBHOOK_URLS_PROPERTY = "google.chat.webhook.urls";

    /** Property key for browser locale usage in search configuration. */
    public static final String USE_BROWSER_LOCALE_FOR_SEARCH_PROPERTY = "search.use.browser.locale";

    // ============================================================
    // Suggest and LTR Property Keys
    // ============================================================

    /** Property key for suggest search log configuration. */
    public static final String SUGGEST_SEARCH_LOG_PROPERTY = "suggest.searchlog";

    /** Property key for suggest documents configuration. */
    public static final String SUGGEST_DOCUMENTS_PROPERTY = "suggest.document";

    /** Property key for suggest search log purge day configuration. */
    public static final String PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY = "purge.suggest.searchlog.day";

    /** Property key for LTR model name configuration. */
    public static final String LTR_MODEL_NAME_PROPERTY = "ltr.model.name";

    /** Property key for LTR window size configuration. */
    public static final String LTR_WINDOW_SIZE_PROPERTY = "ltr.window.size";

    /** Property key for SSO type configuration. */
    public static final String SSO_TYPE_PROPERTY = "sso.type";

    // ============================================================
    // Request and Field Constants
    // ============================================================

    /** Request attribute key for storing queries. */
    public static final String REQUEST_QUERIES = "fess.Queries";

    /** Request attribute key for storing highlight queries. */
    public static final String HIGHLIGHT_QUERIES = "fess.HighlightQueries";

    /** Request attribute key for storing field logs. */
    public static final String FIELD_LOGS = "fess.FieldLogs";

    // ============================================================
    // Date/Time Format Constants
    // ============================================================

    /** Default datetime format pattern without timezone. */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /** ISO datetime format pattern with milliseconds and UTC timezone. */
    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /** Date optional time format identifier. */
    public static final String DATE_OPTIONAL_TIME = "date_optional_time";

    // ============================================================
    // Default Values and Status
    // ============================================================

    /** Status code representing completed/done state. */
    public static final int DONE_STATUS = 9999;

    /** Default value for ignore failure type (empty string). */
    public static final String DEFAULT_IGNORE_FAILURE_TYPE = StringUtil.EMPTY;

    /** Default failure count value (-1 indicates no limit). */
    public static final Integer DEFAULT_FAILURE_COUNT = -1;

    /** Default purge day value (-1 indicates no purging). */
    public static final String DEFAULT_PURGE_DAY = "-1";

    /** Default suggest purge day value (30 days). */
    public static final String DEFAULT_SUGGEST_PURGE_DAY = "30";

    /** Default list of bot user agents to purge from logs. */
    public static final String DEFAULT_PURGE_BY_BOTS =
            "Crawler,crawler,Bot,bot,Slurp,Yeti,Baidu,Steeler,ichiro,hotpage,Feedfetcher,ia_archiver,Y!J-BRI,Google Desktop,Seznam,Tumblr,YandexBot,Chilkat,CloudFront,Mediapartners,MSIE 6";

    /** Default from email address for notifications. */
    public static final String DEFAULT_FROM_EMAIL = "Administrator <root@localhost>";

    // ============================================================
    // Crawler Info Map Keys
    // ============================================================

    /** Info map key for crawler status information. */
    public static final String CRAWLER_STATUS = "CrawlerStatus";

    /** Info map key for crawler error information. */
    public static final String CRAWLER_ERRORS = "CrawlerErrors";

    /** Info map key for crawler start time. */
    public static final String CRAWLER_START_TIME = "CrawlerStartTime";

    /** Info map key for crawler end time. */
    public static final String CRAWLER_END_TIME = "CrawlerEndTime";

    /** Info map key for crawler execution time. */
    public static final String CRAWLER_EXEC_TIME = "CrawlerExecTime";

    /** Info map key for web/file system crawler start time. */
    public static final String WEB_FS_CRAWLER_START_TIME = "WebFsCrawlStartTime";

    /** Info map key for web/file system crawler end time. */
    public static final String WEB_FS_CRAWLER_END_TIME = "WebFsCrawlEndTime";

    /** Info map key for data crawler start time. */
    public static final String DATA_CRAWLER_START_TIME = "DataCrawlStartTime";

    /** Info map key for data crawler end time. */
    public static final String DATA_CRAWLER_END_TIME = "DataCrawlEndTime";

    /** Info map key for web/file system crawling execution time. */
    public static final String WEB_FS_CRAWLING_EXEC_TIME = "WebFsCrawlExecTime";

    /** Info map key for web/file system indexing execution time. */
    public static final String WEB_FS_INDEX_EXEC_TIME = "WebFsIndexExecTime";

    /** Info map key for web/file system index size. */
    public static final String WEB_FS_INDEX_SIZE = "WebFsIndexSize";

    /** Info map key for data crawling execution time. */
    public static final String DATA_CRAWLING_EXEC_TIME = "DataCrawlExecTime";

    /** Info map key for data indexing execution time. */
    public static final String DATA_INDEX_EXEC_TIME = "DataIndexExecTime";

    /** Info map key for data index size. */
    public static final String DATA_INDEX_SIZE = "DataIndexSize";

    /** Session identifier key. */
    public static final String SESSION_ID = "sessionId";

    /** Crawling information identifier key. */
    public static final String CRAWLING_INFO_ID = "crawlingInfoId";

    /** Indexing target identifier key. */
    public static final String INDEXING_TARGET = "indexingTarget";

    /** Number of threads configuration key. */
    public static final String NUM_OF_THREADS = "numOfThreads";

    // ============================================================
    // Authentication Type Constants
    // ============================================================

    /** Basic authentication type identifier. */
    public static final String BASIC = "BASIC";

    /** Digest authentication type identifier. */
    public static final String DIGEST = "DIGEST";

    /** NTLM authentication type identifier. */
    public static final String NTLM = "NTLM";

    /** Form-based authentication type identifier. */
    public static final String FORM = "FORM";

    /** Samba authentication type identifier. */
    public static final String SAMBA = "SAMBA";

    /** FTP authentication type identifier. */
    public static final String FTP = "FTP";

    // ============================================================
    // Query Reserved Characters and Patterns
    // ============================================================

    /** Array of reserved characters for query processing. */
    public static final String[] RESERVED =
            { "\\", "+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "~", "*", "?", ";", ":", "/" };

    /** Pattern for detecting reserved characters in Lucene field queries. */
    public static final Pattern LUCENE_FIELD_RESERVED_PATTERN = Pattern.compile("([+\\-!\\(\\){}\\[\\]^\"~\\\\:\\p{Zs}]|(&&)|(\\|\\|))"); // "*", "?",

    /** Pattern for detecting reserved characters in Lucene range field queries. */
    public static final Pattern LUCENE_RANGE_FIELD_RESERVED_PATTERN = Pattern.compile("([!\\(\\){}\\[\\]\"~\\\\:\\p{Zs}]|(&&)|(\\|\\|))");

    // ============================================================
    // Query and Search Constants
    // ============================================================

    /** Default query operator configuration key. */
    public static final String DEFAULT_QUERY_OPERATOR = "fess.DefaultQueryOperator";

    /** Search log access type field name. */
    public static final String SEARCH_LOG_ACCESS_TYPE = "searchLogAccessType";

    /** Search log access type for JSON API. */
    public static final String SEARCH_LOG_ACCESS_TYPE_JSON = "json";

    /** Search log access type for GSA API. */
    public static final String SEARCH_LOG_ACCESS_TYPE_GSA = "gsa";

    /** Search log access type for web interface. */
    public static final String SEARCH_LOG_ACCESS_TYPE_WEB = "web";

    /** Search log access type for admin interface. */
    public static final String SEARCH_LOG_ACCESS_TYPE_ADMIN = "admin";

    /** Search log access type for other interfaces. */
    public static final String SEARCH_LOG_ACCESS_TYPE_OTHER = "other";

    /** Results per page parameter name. */
    public static final String RESULTS_PER_PAGE = "resultsPerPage";

    /** User code parameter name. */
    public static final String USER_CODE = "userCode";

    /** Search query field name for logging. */
    public static final String SEARCH_FIELD_LOG_SEARCH_QUERY = "q";

    /** Statistics report type parameter name. */
    public static final String STATS_REPORT_TYPE = "reportType";

    /** Result document ID cache key. */
    public static final String RESULT_DOC_ID_CACHE = "resultDocIds";

    /** System name for crawling information. */
    public static final String CRAWLING_INFO_SYSTEM_NAME = "system";

    // ============================================================
    // View Parameters
    // ============================================================

    /** View parameter for facet query information. */
    public static final String FACET_QUERY = "fess.FacetQuery";

    /** View parameter for geo query information. */
    public static final String GEO_QUERY = "fess.GeoQuery";

    /** View parameter for facet form data. */
    public static final String FACET_FORM = "fess.FacetForm";

    /** View parameter for geo form data. */
    public static final String GEO_FORM = "fess.GeoForm";

    /** View parameter for label value mapping. */
    public static final String LABEL_VALUE_MAP = "fess.LabelValueMap";

    /** Query parameter name for main search query. */
    public static final String OPTION_QUERY_Q = "q";

    /** Query parameter name for constraint query. */
    public static final String OPTION_QUERY_CQ = "cq";

    /** Query parameter name for optional query. */
    public static final String OPTION_QUERY_OQ = "oq";

    /** Query parameter name for negative query. */
    public static final String OPTION_QUERY_NQ = "nq";

    // ============================================================
    // Job and Process Constants
    // ============================================================

    /** Scheduled job identifier. */
    public static final String SCHEDULED_JOB = "scheduledJob";

    /** Default job target value (all configurations). */
    public static final String DEFAULT_JOB_TARGET = "all";

    /** Default job script type (Groovy). */
    public static final String DEFAULT_JOB_SCRIPT_TYPE = "groovy";

    /** Exit code for successful operation. */
    public static final int EXIT_OK = 0;

    /** Exit code for failed operation. */
    public static final int EXIT_FAIL = 1;

    /** Document crawler format identifier. */
    public static final String DCF = "dcf";

    /** Constant representing all languages. */
    public static final String ALL_LANGUAGES = "all";

    /** Invalid numeric parameter value. */
    public static final String INVALID_NUMERIC_PARAMETER = "-1";

    // ============================================================
    // Facet and Query Constants
    // ============================================================

    /** Prefix for facet field identifiers. */
    public static final String FACET_FIELD_PREFIX = "field:";

    /** Prefix for facet query identifiers. */
    public static final String FACET_QUERY_PREFIX = "query:";

    /** Query that matches all documents. */
    public static final String MATCHES_ALL_QUERY = "*:*";

    /** Configuration key for search engine HTTP address. */
    public static final String FESS_SEARCH_ENGINE_HTTP_ADDRESS = "fess.search_engine.http_address";

    /** Default number of results per page. */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Default starting count for pagination. */
    public static final int DEFAULT_START_COUNT = 0;

    // ============================================================
    // Process Type Constants
    // ============================================================

    /** Process type for replace operations. */
    public static final String PROCESS_TYPE_REPLACE = "R";

    /** Process type for crawling operations. */
    public static final String PROCESS_TYPE_CRAWLING = "C";

    /** Process type for displaying operations. */
    public static final String PROCESS_TYPE_DISPLAYING = "D";

    /** Process type for both crawling and displaying operations. */
    public static final String PROCESS_TYPE_BOTH = "B";

    /** Number of milliseconds in one day. */
    public static final long ONE_DAY_IN_MILLIS = 24L * 60L * 60L * 1000L;

    /** Guest user identifier. */
    public static final String GUEST_USER = "guest";

    /** Conversion rule array for pager components. */
    public static final String[] PAGER_CONVERSION_RULE =
            { "allRecordCount", "pageSize", "currentPageNumber", "allPageCount", "existPrePage", "existNextPage" };

    // ============================================================
    // Crawler Types
    // ============================================================

    /** Web crawler type identifier. */
    public static final String WEB_CRAWLER_TYPE = "web_crawling";

    /** File system crawler type identifier. */
    public static final String FILE_CRAWLER_TYPE = "file_crawling";

    /** Data crawler type identifier. */
    public static final String DATA_CRAWLER_TYPE = "data_crawling";

    // ============================================================
    // Conversion Rules
    // ============================================================

    /** Common conversion rule for entity fields. */
    public static final String[] COMMON_CONVERSION_RULE = { "crudMode", "createdBy", "createdTime", "updatedBy", "updatedTime" };

    /** Common API conversion rule for CRUD operations. */
    public static final String[] COMMON_API_CONVERSION_RULE = { "crudMode" };

    // ============================================================
    // User and Authentication Constants
    // ============================================================

    /** User information session key. */
    public static final String USER_INFO = "LoginInfo";

    /** Search engine API access token key. */
    public static final String SEARCH_ENGINE_API_ACCESS_TOKEN = "searchEngineApiAccessToken";

    /** Default field name identifier. */
    public static final String DEFAULT_FIELD = "_default";

    /** Default number of days for cleanup operations. */
    public static final Integer DEFAULT_DAY_FOR_CLEANUP = 3;

    /** Configuration path property key. */
    public static final String FESS_CONF_PATH = "fess.conf.path";

    /** UTC timezone constant. */
    public static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");

    // ============================================================
    // LDAP Configuration Constants
    // ============================================================

    /** LDAP base DN configuration key. */
    public static final String LDAP_BASE_DN = "ldap.base.dn";

    /** LDAP security principal configuration key. */
    public static final String LDAP_SECURITY_PRINCIPAL = "ldap.security.principal";

    /** LDAP admin security principal configuration key. */
    public static final String LDAP_ADMIN_SECURITY_PRINCIPAL = "ldap.admin.security.principal";

    /** LDAP admin security credentials configuration key. */
    public static final String LDAP_ADMIN_SECURITY_CREDENTIALS = "ldap.admin.security.credentials";

    /** LDAP provider URL configuration key. */
    public static final String LDAP_PROVIDER_URL = "ldap.provider.url";

    /** LDAP security authentication configuration key. */
    public static final String LDAP_SECURITY_AUTHENTICATION = "ldap.security.authentication";

    /** LDAP initial context factory configuration key. */
    public static final String LDAP_INITIAL_CONTEXT_FACTORY = "ldap.initial.context.factory";

    /** LDAP account filter configuration key. */
    public static final String LDAP_ACCOUNT_FILTER = "ldap.account.filter";

    /** LDAP group filter configuration key. */
    public static final String LDAP_GROUP_FILTER = "ldap.group.filter";

    /** LDAP member of attribute configuration key. */
    public static final String LDAP_MEMBEROF_ATTRIBUTE = "ldap.memberof.attribute";

    // ============================================================
    // Notification Configuration
    // ============================================================

    /** Notification configuration for login page. */
    public static final String NOTIFICATION_LOGIN = "notification.login";

    /** Notification configuration for search top page. */
    public static final String NOTIFICATION_SEARCH_TOP = "notification.search.top";

    /** Notification configuration for advanced search page. */
    public static final String NOTIFICATION_ADVANCE_SEARCH = "notification.advance.search";

    // ============================================================
    // Storage Configuration
    // ============================================================

    /** Storage endpoint configuration key. */
    public static final String STORAGE_ENDPOINT = "storage.endpoint";

    /** Storage access key configuration key. */
    public static final String STORAGE_ACCESS_KEY = "storage.accesskey";

    /** Storage secret key configuration key. */
    public static final String STORAGE_SECRET_KEY = "storage.secretkey";

    /** Storage bucket configuration key. */
    public static final String STORAGE_BUCKET = "storage.bucket";

    // ============================================================
    // Mapping Type Constants
    // ============================================================

    /** Mapping type for array fields. */
    public static final String MAPPING_TYPE_ARRAY = "array";

    /** Mapping type for string fields. */
    public static final String MAPPING_TYPE_STRING = "string";

    /** Mapping type for long fields. */
    public static final String MAPPING_TYPE_LONG = "long";

    /** Mapping type for double fields. */
    public static final String MAPPING_TYPE_DOUBLE = "double";

    /** Mapping type for date fields. */
    public static final String MAPPING_TYPE_DATE = "date";

    /** Mapping type for PDF date fields. */
    public static final String MAPPING_TYPE_PDF_DATE = "pdf_date";

    // ============================================================
    // Request and Search Constants
    // ============================================================

    /** Paging query list parameter name. */
    public static final String PAGING_QUERY_LIST = "pagingQueryList";

    /** Request languages parameter name. */
    public static final String REQUEST_LANGUAGES = "requestLanguages";

    /** Request page size parameter name. */
    public static final String REQUEST_PAGE_SIZE = "requestPageSize";

    /** Search preference for local node. */
    public static final String SEARCH_PREFERENCE_LOCAL = "_local";

    /** GSA API version identifier. */
    public static final String GSA_API_VERSION = "3.2";

    /** Permissions field name. */
    public static final String PERMISSIONS = "permissions";

    /** Queries field name. */
    public static final String QUERIES = "queries";

    /** Virtual hosts field name. */
    public static final String VIRTUAL_HOSTS = "virtualHosts";

    /** Prefix for encrypted/ciphered values. */
    public static final String CIPHER_PREFIX = "{cipher}";

    /** System user identifier. */
    public static final String SYSTEM_USER = "system";

    /** Empty user ID placeholder. */
    public static final String EMPTY_USER_ID = "<empty>";

    /** Crawler process command for thread dump. */
    public static final String CRAWLER_PROCESS_COMMAND_THREAD_DUMP = "thread_dump";

    // ============================================================
    // Path and Configuration Constants
    // ============================================================

    /** Fess thumbnail path configuration key. */
    public static final String FESS_THUMBNAIL_PATH = "fess.thumbnail.path";

    /** Fess variable path configuration key. */
    public static final String FESS_VAR_PATH = "fess.var.path";

    /** Fess log level configuration key. */
    public static final String FESS_LOG_LEVEL = "fess.log.level";

    /** Track total hits configuration key. */
    public static final String TRACK_TOTAL_HITS = "track_total_hits";

    /** System property prefix for Fess configuration. */
    public static final String SYSTEM_PROP_PREFIX = "fess.system.";

    /** Configuration prefix for Fess settings. */
    public static final String FESS_CONFIG_PREFIX = "fess.config.";

    /** Xerces feature prefix for XML processing. */
    public static final String XERCES_FEATURE_PREFIX = "http://apache.org/xml/features/";

    /** Load external DTD feature name. */
    public static final String LOAD_EXTERNAL_DTD_FEATURE = "nonvalidating/load-external-dtd";

    // ============================================================
    // Search Engine Type Constants
    // ============================================================

    /** Cloud-based search engine type. */
    public static final String FESEN_TYPE_CLOUD = "cloud";

    /** AWS-based search engine type. */
    public static final String FESEN_TYPE_AWS = "aws";

    /** Search engine username configuration key. */
    public static final String FESEN_USERNAME = "fesen.username";

    /** Search engine password configuration key. */
    public static final String FESEN_PASSWORD = "fesen.password";

    // ============================================================
    // Execution Type Constants
    // ============================================================

    /** Execution type for crawler operations. */
    public static final String EXECUTE_TYPE_CRAWLER = "crawler";

    /** Execution type for thumbnail operations. */
    public static final String EXECUTE_TYPE_THUMBNAIL = "thumbnail";

    /** Execution type for Python script operations. */
    public static final String EXECUTE_TYPE_PYTHON = "python";

    /** Execution type for suggest operations. */
    public static final String EXECUTE_TYPE_SUGGEST = "suggest";

    /** Default script type (Groovy). */
    public static final String DEFAULT_SCRIPT = "groovy";

    // ============================================================
    // Text Fragment Constants
    // ============================================================

    /** Text fragments feature identifier. */
    public static final String TEXT_FRAGMENTS = "text_fragments";

    /** Text fragment type for query highlighting. */
    public static final String TEXT_FRAGMENT_TYPE_QUERY = "query";

    /** Text fragment type for result highlighting. */
    public static final String TEXT_FRAGMENT_TYPE_HIGHLIGHT = "highlight";

    /** Crawler statistics key identifier. */
    public static final String CRAWLER_STATS_KEY = "crawler.stats.key";

    // ============================================================
    // Web API Constants
    // ============================================================

    /** Request attribute key for storing API format type. */
    public static final String API_FORMAT_TYPE = "apiFormatType";

    /** Request attribute key for storing document ID in API requests. */
    public static final String API_DOC_ID_FIELD = "doc_id";

    /** JSON response field name for messages. */
    public static final String API_RESPONSE_MESSAGE = "message";

    /** JSON response field name for results. */
    public static final String API_RESPONSE_RESULT = "result";

    /** JSON response field name for error codes. */
    public static final String API_RESPONSE_ERROR_CODE = "error_code";

    /** JSON response field name for data. */
    public static final String API_RESPONSE_DATA = "data";

    /** JSON response field name for record count. */
    public static final String API_RESPONSE_RECORD_COUNT = "record_count";

    /** JSON response field name for query. */
    public static final String API_RESPONSE_QUERY = "q";

    /** JSON response field name for query ID. */
    public static final String API_RESPONSE_QUERY_ID = "query_id";

    /** MIME type for JSON. */
    public static final String MIME_TYPE_JSON = "application/json";

    /** MIME type for JSONP/JavaScript. */
    public static final String MIME_TYPE_JAVASCRIPT = "application/javascript";

    /** MIME type for NDJSON (newline-delimited JSON). */
    public static final String MIME_TYPE_NDJSON = "application/x-ndjson";

    /** MIME type for plain text. */
    public static final String MIME_TYPE_TEXT = "text/plain";

    /** API v1 path prefix. */
    public static final String API_V1_PREFIX = "/api/v1";

    /** Admin server path prefix for search engine API. */
    public static final String ADMIN_SERVER_PREFIX = "/admin/server_";

    /** Default number of suggestions to return in suggest API. */
    public static final int DEFAULT_SUGGEST_NUM = 10;

    /** Result value for created resources. */
    public static final String API_RESULT_CREATED = "created";

    /** Result value for updated resources. */
    public static final String API_RESULT_UPDATED = "updated";

    /** Result value for deleted resources. */
    public static final String API_RESULT_DELETED = "deleted";
}
