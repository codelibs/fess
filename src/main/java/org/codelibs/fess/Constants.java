/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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

    public static final String LINE_SEPARATOR = System.lineSeparator();

    public static final int DEFAULT_ADMIN_PAGE_NUMBER = 1;

    public static final String TRUE = "true";

    public static final String FALSE = "false";

    public static final Boolean T = true;

    public static final Boolean F = false;

    public static final String SCORE = "score";

    public static final String SEARCHER = "searcher";

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

    public static final String MS932 = "MS932";

    public static final String DEFAULT_CRON_EXPRESSION = "0 0 * * *";

    public static final String DEFAULT_SEARCH_LOG_CRON_EXPRESSION = "* * * * *";

    public static final String DEFAULT_DAILY_CRON_EXPRESSION = "0 0 * * *";

    public static final String DEFAULT_HOURLY_CRON_EXPRESSION = "0 * * * *";

    public static final int DEFAULT_INTERVAL_TIME_FOR_FS = 1000;

    public static final int DEFAULT_INTERVAL_TIME_FOR_WEB = 10000;

    public static final int DEFAULT_NUM_OF_THREAD_FOR_FS = 5;

    public static final int DEFAULT_NUM_OF_THREAD_FOR_WEB = 1;

    public static final long DEFAULT_CRAWLING_EXECUTION_INTERVAL = 5000L;

    public static final String CRAWLING_USER_AGENT_PREFIX = "Mozilla/5.0 (compatible; Fess/";

    public static final String CRAWLING_USER_AGENT_SUFFIX = "; +http://fess.codelibs.org/bot.html)";

    // fess properties
    public static final String USER_INFO_PROPERTY = "user.info";

    public static final String USER_FAVORITE_PROPERTY = "user.favorite";

    public static final String SEARCH_LOG_PROPERTY = "search.log";

    public static final String APPEND_QUERY_PARAMETER_PROPERTY = "append.query.parameter";

    public static final String INCREMENTAL_CRAWLING_PROPERTY = "crawling.incremental";

    public static final String CRAWLING_THREAD_COUNT_PROPERTY = "crawling.thread.count";

    public static final String CRAWLING_USER_AGENT_PROPERTY = "crawling.user.agent";

    public static final String DAY_FOR_CLEANUP_PROPERTY = "day.for.cleanup";

    public static final String WEB_API_JSON_PROPERTY = "web.api.json";

    public static final String WEB_API_SUGGEST_PROPERTY = "web.api.suggest";

    public static final String WEB_API_GSA_PROPERTY = "web.api.gsa";

    public static final String WEB_API_POPULAR_WORD_PROPERTY = "web.api.popularword";

    public static final String APP_VALUE_PROPERTY = "system.properties";

    public static final String DEFAULT_LABEL_VALUE_PROPERTY = "label.value";

    public static final String DEFAULT_SORT_VALUE_PROPERTY = "sort.value";

    public static final String VIRTUAL_HOST_VALUE_PROPERTY = "virtual.host.value";

    public static final String LOGIN_REQUIRED_PROPERTY = "login.required";

    public static final String RESULT_COLLAPSED_PROPERTY = "result.collapsed";

    public static final String LOGIN_LINK_ENALBED_PROPERTY = "login.link.enabled";

    public static final String THUMBNAIL_ENALBED_PROPERTY = "thumbnail.enabled";

    public static final String IGNORE_FAILURE_TYPE_PROPERTY = "failure.ignoretype";

    public static final String FAILURE_COUNT_THRESHOLD_PROPERTY = "failure.countthreshold";

    public static final String CSV_FILE_ENCODING_PROPERTY = "csv.file.encoding";

    public static final String PURGE_SEARCH_LOG_DAY_PROPERTY = "purge.searchlog.day";

    public static final String PURGE_USER_INFO_DAY_PROPERTY = "purge.userinfo.day";

    public static final String PURGE_JOB_LOG_DAY_PROPERTY = "purge.joblog.day";

    public static final String PURGE_BY_BOTS_PROPERTY = "purge.by.bots";

    public static final String SEARCH_FILE_PROXY_PROPERTY = "search.file.proxy";

    public static final String NOTIFICATION_TO_PROPERTY = "notification.to";

    public static final String SLACK_WEBHOOK_URLS_PROPERTY = "slack.webhook.urls";

    public static final String GOOGLE_CHAT_WEBHOOK_URLS_PROPERTY = "google.chat.webhook.urls";

    public static final String USE_BROWSER_LOCALE_FOR_SEARCH_PROPERTY = "search.use.browser.locale";

    public static final String SUGGEST_SEARCH_LOG_PROPERTY = "suggest.searchlog";

    public static final String SUGGEST_DOCUMENTS_PROPERTY = "suggest.document";

    public static final String PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY = "purge.suggest.searchlog.day";

    public static final String LTR_MODEL_NAME_PROPERTY = "ltr.model.name";

    public static final String LTR_WINDOW_SIZE_PROPERTY = "ltr.window.size";

    public static final String REQUEST_QUERIES = "fess.Queries";

    public static final String HIGHLIGHT_QUERIES = "fess.HighlightQueries";

    public static final String FIELD_LOGS = "fess.FieldLogs";

    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String DATE_OPTIONAL_TIME = "date_optional_time";

    public static final int DONE_STATUS = 9999;

    public static final String DEFAULT_IGNORE_FAILURE_TYPE = StringUtil.EMPTY;

    public static final Integer DEFAULT_FAILURE_COUNT = -1;

    public static final String DEFAULT_PURGE_DAY = "-1";

    public static final String DEFAULT_SUGGEST_PURGE_DAY = "30";

    public static final String DEFAULT_PURGE_BY_BOTS =
            "Crawler,crawler,Bot,bot,Slurp,Yeti,Baidu,Steeler,ichiro,hotpage,Feedfetcher,ia_archiver,Y!J-BRI,Google Desktop,Seznam,Tumblr,YandexBot,Chilkat,CloudFront,Mediapartners,MSIE 6";

    public static final String DEFAULT_FROM_EMAIL = "Administrator <root@localhost>";

    // info map

    public static final String CRAWLER_STATUS = "CrawlerStatus";

    public static final String CRAWLER_ERRORS = "CrawlerErrors";

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

    public static final String FORM = "FORM";

    public static final String SAMBA = "SAMBA";

    public static final String FTP = "FTP";

    public static final String[] RESERVED =
            { "\\", "+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "~", "*", "?", ";", ":", "/" };

    public static final Pattern LUCENE_FIELD_RESERVED_PATTERN = Pattern.compile("([+\\-!\\(\\){}\\[\\]^\"~\\\\:\\p{Zs}]|(&&)|(\\|\\|))"); // "*", "?",

    public static final Pattern LUCENE_RANGE_FIELD_RESERVED_PATTERN = Pattern.compile("([!\\(\\){}\\[\\]\"~\\\\:\\p{Zs}]|(&&)|(\\|\\|))");

    public static final String SEARCH_LOG_ACCESS_TYPE = "searchLogAccessType";

    public static final String SEARCH_LOG_ACCESS_TYPE_JSON = "json";

    public static final String SEARCH_LOG_ACCESS_TYPE_GSA = "gsa";

    public static final String SEARCH_LOG_ACCESS_TYPE_WEB = "web";

    public static final String SEARCH_LOG_ACCESS_TYPE_ADMIN = "admin";

    public static final String SEARCH_LOG_ACCESS_TYPE_OTHER = "other";

    public static final String RESULTS_PER_PAGE = "resultsPerPage";

    public static final String USER_CODE = "userCode";

    public static final String SEARCH_FIELD_LOG_SEARCH_QUERY = "q";

    public static final String STATS_REPORT_TYPE = "reportType";

    public static final String RESULT_DOC_ID_CACHE = "resultDocIds";

    public static final String CRAWLING_INFO_SYSTEM_NAME = "system";

    // view parameters

    public static final String FACET_QUERY = "fess.FacetQuery";

    public static final String GEO_QUERY = "fess.GeoQuery";

    public static final String FACET_FORM = "fess.FacetForm";

    public static final String GEO_FORM = "fess.GeoForm";

    public static final String LABEL_VALUE_MAP = "fess.LabelValueMap";

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

    @Deprecated
    public static final String FESS_ES_HTTP_ADDRESS = "fess.es.http_address";

    public static final String FESS_SEARCH_ENGINE_HTTP_ADDRESS = "fess.search_engine.http_address";

    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final int DEFAULT_START_COUNT = 0;

    public static final String PROCESS_TYPE_REPLACE = "R";

    public static final String PROCESS_TYPE_CRAWLING = "C";

    public static final String PROCESS_TYPE_DISPLAYING = "D";

    public static final String PROCESS_TYPE_BOTH = "B";

    public static final long ONE_DAY_IN_MILLIS = 24L * 60L * 60L * 1000L;

    public static final String GUEST_USER = "guest";

    public static final String[] PAGER_CONVERSION_RULE =
            { "allRecordCount", "pageSize", "currentPageNumber", "allPageCount", "existPrePage", "existNextPage" };

    // crawler types
    public static final String WEB_CRAWLER_TYPE = "web_crawling";

    public static final String FILE_CRAWLER_TYPE = "file_crawling";

    public static final String DATA_CRAWLER_TYPE = "data_crawling";

    public static final String[] COMMON_CONVERSION_RULE = { "crudMode", "createdBy", "createdTime", "updatedBy", "updatedTime" };

    public static final String[] COMMON_API_CONVERSION_RULE = { "crudMode" };

    public static final String USER_INFO = "LoginInfo";

    public static final String SEARCH_ENGINE_API_ACCESS_TOKEN = "searchEngineApiAccessToken";

    public static final String DEFAULT_FIELD = "_default";

    public static final Integer DEFAULT_DAY_FOR_CLEANUP = 3;

    public static final String FESS_CONF_PATH = "fess.conf.path";

    public static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");

    public static final String LDAP_BASE_DN = "ldap.base.dn";

    public static final String LDAP_SECURITY_PRINCIPAL = "ldap.security.principal";

    public static final String LDAP_ADMIN_SECURITY_PRINCIPAL = "ldap.admin.security.principal";

    public static final String LDAP_ADMIN_SECURITY_CREDENTIALS = "ldap.admin.security.credentials";

    public static final String LDAP_PROVIDER_URL = "ldap.provider.url";

    public static final String LDAP_SECURITY_AUTHENTICATION = "ldap.security.authentication";

    public static final String LDAP_INITIAL_CONTEXT_FACTORY = "ldap.initial.context.factory";

    public static final String LDAP_ACCOUNT_FILTER = "ldap.account.filter";

    public static final String LDAP_GROUP_FILTER = "ldap.group.filter";

    public static final String LDAP_MEMBEROF_ATTRIBUTE = "ldap.memberof.attribute";

    public static final String NOTIFICATION_LOGIN = "notification.login";

    public static final String NOTIFICATION_SEARCH_TOP = "notification.search.top";

    public static final String NOTIFICATION_ADVANCE_SEARCH = "notification.advance.search";

    public static final String STORAGE_ENDPOINT = "storage.endpoint";

    public static final String STORAGE_ACCESS_KEY = "storage.accesskey";

    public static final String STORAGE_SECRET_KEY = "storage.secretkey";

    public static final String STORAGE_BUCKET = "storage.bucket";

    public static final String MAPPING_TYPE_ARRAY = "array";

    public static final String MAPPING_TYPE_STRING = "string";

    public static final String MAPPING_TYPE_LONG = "long";

    public static final String MAPPING_TYPE_DOUBLE = "double";

    public static final String MAPPING_TYPE_DATE = "date";

    public static final String MAPPING_TYPE_PDF_DATE = "pdf_date";

    public static final String PAGING_QUERY_LIST = "pagingQueryList";

    public static final String REQUEST_LANGUAGES = "requestLanguages";

    public static final String REQUEST_PAGE_SIZE = "requestPageSize";

    public static final String SEARCH_PREFERENCE_LOCAL = "_local";

    public static final String GSA_API_VERSION = "3.2";

    public static final String PERMISSIONS = "permissions";

    public static final String QUERIES = "queries";

    public static final String VIRTUAL_HOSTS = "virtualHosts";

    public static final String CIPHER_PREFIX = "{cipher}";

    public static final String SYSTEM_USER = "system";

    public static final String EMPTY_USER_ID = "<empty>";

    public static final String CRAWLER_PROCESS_COMMAND_THREAD_DUMP = "thread_dump";

    public static final String FESS_THUMBNAIL_PATH = "fess.thumbnail.path";

    public static final String FESS_VAR_PATH = "fess.var.path";

    public static final String FESS_LOG_LEVEL = "fess.log.level";

    public static final String TRACK_TOTAL_HITS = "track_total_hits";

    public static final String SYSTEM_PROP_PREFIX = "fess.system.";

    public static final String FESS_CONFIG_PREFIX = "fess.config.";

    public static final String XERCES_FEATURE_PREFIX = "http://apache.org/xml/features/";

    public static final String LOAD_EXTERNAL_DTD_FEATURE = "nonvalidating/load-external-dtd";

    public static final String FESEN_TYPE_CLOUD = "cloud";

    public static final String FESEN_TYPE_AWS = "aws";

    public static final String FESEN_USERNAME = "fesen.username";

    public static final String FESEN_PASSWORD = "fesen.password";

    public static final String EXECUTE_TYPE_CRAWLER = "crawler";

    public static final String EXECUTE_TYPE_THUMBNAIL = "thumbnail";

    public static final String EXECUTE_TYPE_PYTHON = "python";

    public static final String EXECUTE_TYPE_SUGGEST = "suggest";

    public static final String DEFAULT_SCRIPT = "groovy";

    public static final String TEXT_FRAGMENTS = "text_fragments";

    public static final String TEXT_FRAGMENT_TYPE_QUERY = "query";

    public static final String TEXT_FRAGMENT_TYPE_HIGHLIGHT = "highlight";

    public static final String CRAWLER_STATS_KEY = "crawler.stats.key";
}
