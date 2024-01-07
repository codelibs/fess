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
package org.codelibs.fess.mylasta.direction;

import static org.codelibs.core.stream.StreamUtil.split;
import static org.codelibs.core.stream.StreamUtil.stream;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;

import org.codelibs.core.exception.ClassNotFoundRuntimeException;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.core.misc.Tuple4;
import org.codelibs.fess.Constants;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.taglib.FessFunctions;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.JvmUtil;
import org.codelibs.fess.util.PrunedTag;
import org.dbflute.optional.OptionalThing;
import org.dbflute.util.DfTypeUtil;
import org.lastaflute.job.LaJob;
import org.lastaflute.job.subsidiary.JobConcurrentExec;
import org.lastaflute.web.validation.RequiredValidator;
import org.lastaflute.web.validation.theme.typed.DoubleTypeValidator;
import org.lastaflute.web.validation.theme.typed.FloatTypeValidator;
import org.lastaflute.web.validation.theme.typed.IntegerTypeValidator;
import org.lastaflute.web.validation.theme.typed.LongTypeValidator;
import org.opensearch.search.sort.SortBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;

public interface FessProp {

    String API_PING_SEARCH_ENGINE_FIELD_SET = "apiPingSearchEngineFieldSet";

    String QUERY_HIGHLIGHT_TERMINAL_CHARS = "queryHighlightTerminalChars";

    String QUERY_HIGHLIGHT_BOUNDARY_CHARS = "queryHighlightBoundaryChars";

    String QUERY_TRACK_TOTAL_HITS_VALUE = "queryTrackTotalHitsValue";

    String CORS_ALLOW_ORIGIN = "CorsAllowOrigin";

    String API_DASHBOARD_RESPONSE_HEADER_LIST = "apiDashboardResponseHeaderList";

    String API_JSON_RESPONSE_HEADER_LIST = "apiJsonResponseHeaderList";

    String API_GSA_RESPONSE_HEADER_LIST = "apiGsaResponseHeaderList";

    String SMB_AVAILABLE_SID_TYPES = "smbAvailableSidTypes";

    String LOGGING_SEARCH_DOCS_FIELDS = "loggingSearchDocsFields";

    String API_SEARCH_ACCEPT_REFERERS = "apiSearchAcceptReferers";

    String QUERY_GSA_RESPONSE_FIELDS = "queryGsaResponseFields";

    String THUMBNAIL_HTML_IMAGE_EXCLUDE_EXTENSIONS = "ThumbnailHtmlImageExcludeExtensions";

    String VIRTUAL_HOST_VALUE = "VirtualHostValue";

    String QUERY_DEFAULT_LANGUAGES = "queryDefaultLanguages";

    String HTML_PROXY = "httpProxy";

    String CRAWLER_FAILURE_URL_STATUS_CODES = "crawlerFailureUrlStatusCodes";

    String VIRTUAL_HOST_HEADERS = "virtualHostHeaders";

    String QUERY_COLLAPSE_INNER_HITS_SORTS = "queryCollapseInnerHitsSorts";

    String USER_CODE_PATTERN = "userCodePattern";

    String API_ADMIN_ACCESS_PERMISSION_SET = "apiAdminAccessPermissionSet";

    String CRAWLER_DOCUMENT_SPACE_CHARS = "crawlerDocumentSpaceChars";

    String CRAWLER_DOCUMENT_SPACES = "crawlerDocumentSpaces";

    String CRAWLER_DOCUMENT_FULLSTOP_CHARS = "crawlerDocumentFullstopChars";

    String INDEX_ADMIN_ARRAY_FIELD_SET = "indexAdminArrayFieldSet";

    String INDEX_ADMIN_DATE_FIELD_SET = "indexAdminDateFieldSet";

    String INDEX_ADMIN_INTEGER_FIELD_SET = "indexAdminIntegerFieldSet";

    String INDEX_ADMIN_LONG_FIELD_SET = "indexAdminLongFieldSet";

    String INDEX_ADMIN_FLOAT_FIELD_SET = "indexAdminFloatFieldSet";

    String INDEX_ADMIN_DOUBLE_FIELD_SET = "indexAdminDoubleFieldSet";

    String AUTHENTICATION_ADMIN_ROLES = "authenticationAdminRoles";

    String SEARCH_GUEST_PERMISSION_LIST = "searchGuestPermissionList";

    String SUGGEST_SEARCH_LOG_PERMISSIONS = "suggestSearchLogPermissions";

    String GROUP_VALUE_PREFIX = "group:";

    String ROLE_VALUE_PREFIX = "role:";

    String APP_VALUES = "appValues";

    String DEFAULT_SORT_VALUES = "defaultSortValues";

    String DEFAULT_LABEL_VALUES = "defaultLabelValues";

    String VIRTUAL_HOST_VALUES = "virtualHostValues";

    String QUERY_LANGUAGE_MAPPING = "queryLanguageMapping";

    String CRAWLER_METADATA_NAME_MAPPING = "crawlerMetadataNameMapping";

    String CRAWLER_METADATA_CONTENT_EXCLUDES = "crawlerMetadataContentExcludes";

    Map<String, Object> propMap = new ConcurrentHashMap<>();

    //
    // system.properties
    //

    default void storeSystemProperties() {
        ComponentUtil.getSystemProperties().store();
    }

    default String getSystemProperty(final String key) {
        return ComponentUtil.getSystemProperties().getProperty(key, System.getProperty(Constants.SYSTEM_PROP_PREFIX + key));
    }

    default String getSystemProperty(final String key, final String defaultValue) {
        return ComponentUtil.getSystemProperties().getProperty(key, System.getProperty(Constants.SYSTEM_PROP_PREFIX + key, defaultValue));
    }

    default void setSystemProperty(final String key, final String value) {
        if (value != null) {
            ComponentUtil.getSystemProperties().setProperty(key, value);
        } else {
            ComponentUtil.getSystemProperties().remove(key);
        }
    }

    default boolean getSystemPropertyAsBoolean(final String key, final boolean defaultValue) {
        return Constants.TRUE.equalsIgnoreCase(getSystemProperty(key, defaultValue ? Constants.TRUE : Constants.FALSE));
    }

    default void setSystemPropertyAsBoolean(final String key, final boolean value) {
        setSystemProperty(key, value ? Constants.TRUE : Constants.FALSE);
    }

    default int getSystemPropertyAsInt(final String key, final int defaultValue) {
        final String value = getSystemProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (final NumberFormatException e) {
                // ignore
            }
        }
        return defaultValue;
    }

    default void setSystemPropertyAsInt(final String key, final int value) {
        setSystemProperty(key, Integer.toString(value));
    }

    default boolean isSearchFileProxyEnabled() {
        return getSystemPropertyAsBoolean(Constants.SEARCH_FILE_PROXY_PROPERTY, true);
    }

    default boolean isBrowserLocaleForSearchUsed() {
        return getSystemPropertyAsBoolean(Constants.USE_BROWSER_LOCALE_FOR_SEARCH_PROPERTY, false);
    }

    default String[] getDefaultSortValues(final OptionalThing<FessUserBean> userBean) {
        @SuppressWarnings("unchecked")
        List<Pair<String, String>> list = (List<Pair<String, String>>) propMap.get(DEFAULT_SORT_VALUES);
        if (list == null) {
            final String value = getSystemProperty(Constants.DEFAULT_SORT_VALUE_PROPERTY);
            if (StringUtil.isBlank(value)) {
                list = Collections.emptyList();
            } else {
                final Set<String> keySet = new HashSet<>();
                list = split(value, "\n").get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> {
                    final String[] pair = s.split("=");
                    if (pair.length == 1) {
                        return new Pair<>(StringUtil.EMPTY, pair[0].trim());
                    }
                    if (pair.length == 2) {
                        String sortValue = pair[1].trim();
                        if (StringUtil.isBlank(sortValue) || "score".equals(sortValue)) {
                            sortValue = "score.desc";
                        }
                        return new Pair<>(pair[0].trim(), sortValue);
                    }
                    return null;
                }).filter(o -> o != null && keySet.add(o.getFirst())).collect(Collectors.toList()));
            }
            propMap.put(DEFAULT_SORT_VALUES, list);
        }
        return list.stream().map(p -> {
            final String key = p.getFirst();
            if (StringUtil.isEmpty(key) || userBean
                    .map(user -> stream(user.getRoles()).get(stream -> stream.anyMatch(s -> (ROLE_VALUE_PREFIX + s).equals(key)))
                            || stream(user.getGroups()).get(stream -> stream.anyMatch(s -> (GROUP_VALUE_PREFIX + s).equals(key))))
                    .orElse(false)) {
                return p.getSecond();
            }
            return null;
        }).filter(StringUtil::isNotBlank).toArray(n -> new String[n]);
    }

    default void setDefaultSortValue(final String value) {
        setSystemProperty(Constants.DEFAULT_SORT_VALUE_PROPERTY, value);
        propMap.remove(DEFAULT_SORT_VALUES);
    }

    default String getDefaultSortValue() {
        return getSystemProperty(Constants.DEFAULT_SORT_VALUE_PROPERTY, StringUtil.EMPTY);
    }

    default String[] getDefaultLabelValues(final OptionalThing<FessUserBean> userBean) {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> map = (Map<String, List<String>>) propMap.get(DEFAULT_LABEL_VALUES);
        if (map == null) {
            final String value = getSystemProperty(Constants.DEFAULT_LABEL_VALUE_PROPERTY);
            if (StringUtil.isBlank(value)) {
                map = Collections.emptyMap();
            } else {
                final Set<String> keySet = new HashSet<>();
                map = split(value, "\n").get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> {
                    final String[] pair = s.split("=");
                    if (pair.length == 1) {
                        return new Pair<>(StringUtil.EMPTY, pair[0].trim());
                    }
                    if (pair.length == 2) {
                        return new Pair<>(pair[0].trim(), pair[1].trim());
                    }
                    return null;
                }).filter(o -> o != null && keySet.add(o.getFirst())).collect(HashMap<String, List<String>>::new,
                        (m, d) -> m.put(d.getFirst(), Arrays.asList(d.getSecond().split(","))), HashMap::putAll));
            }
            propMap.put(DEFAULT_LABEL_VALUES, map);
        }
        return map.entrySet().stream().flatMap(e -> {
            final String key = e.getKey();
            if (StringUtil.isEmpty(key) || userBean
                    .map(user -> stream(user.getRoles()).get(stream -> stream.anyMatch(s -> (ROLE_VALUE_PREFIX + s).equals(key)))
                            || stream(user.getGroups()).get(stream -> stream.anyMatch(s -> (GROUP_VALUE_PREFIX + s).equals(key))))
                    .orElse(false)) {
                return e.getValue().stream();
            }
            return Stream.empty();
        }).filter(StringUtil::isNotBlank).toArray(n -> new String[n]);
    }

    default void setAppValue(final String value) {
        setSystemProperty(Constants.APP_VALUE_PROPERTY, value);
        propMap.remove(APP_VALUES);
    }

    default String getAppValue() {
        return getSystemProperty(Constants.APP_VALUE_PROPERTY, StringUtil.EMPTY);
    }

    default void setDefaultLabelValue(final String value) {
        setSystemProperty(Constants.DEFAULT_LABEL_VALUE_PROPERTY, value);
        propMap.remove(DEFAULT_LABEL_VALUES);
    }

    default String getDefaultLabelValue() {
        return getSystemProperty(Constants.DEFAULT_LABEL_VALUE_PROPERTY, StringUtil.EMPTY);
    }

    default void setVirtualHostValue(final String value) {
        setSystemProperty(Constants.VIRTUAL_HOST_VALUE_PROPERTY, value);
        propMap.remove(VIRTUAL_HOST_HEADERS);
    }

    default String getVirtualHostValue() {
        return getSystemProperty(Constants.VIRTUAL_HOST_VALUE_PROPERTY, getVirtualHostHeaders());
    }

    default void setLoginRequired(final boolean value) {
        setSystemPropertyAsBoolean(Constants.LOGIN_REQUIRED_PROPERTY, value);
    }

    default boolean isLoginRequired() {
        return getSystemPropertyAsBoolean(Constants.LOGIN_REQUIRED_PROPERTY, false);
    }

    default void setResultCollapsed(final boolean value) {
        setSystemPropertyAsBoolean(Constants.RESULT_COLLAPSED_PROPERTY, value);
    }

    default boolean isResultCollapsed() {
        return switch (getFesenType()) {
        case Constants.FESEN_TYPE_CLOUD, Constants.FESEN_TYPE_AWS -> false;
        default -> getSystemPropertyAsBoolean(Constants.RESULT_COLLAPSED_PROPERTY, false);
        };
    }

    default void setLoginLinkEnabled(final boolean value) {
        setSystemPropertyAsBoolean(Constants.LOGIN_LINK_ENALBED_PROPERTY, value);
    }

    default boolean isLoginLinkEnabled() {
        return getSystemPropertyAsBoolean(Constants.LOGIN_LINK_ENALBED_PROPERTY, true);
    }

    default void setThumbnailEnabled(final boolean value) {
        setSystemPropertyAsBoolean(Constants.THUMBNAIL_ENALBED_PROPERTY, value);
    }

    default boolean isThumbnailEnabled() {
        return getSystemPropertyAsBoolean(Constants.THUMBNAIL_ENALBED_PROPERTY, false);
    }

    default void setIncrementalCrawling(final boolean value) {
        setSystemPropertyAsBoolean(Constants.INCREMENTAL_CRAWLING_PROPERTY, value);
    }

    default boolean isIncrementalCrawling() {
        return getSystemPropertyAsBoolean(Constants.INCREMENTAL_CRAWLING_PROPERTY, true);
    }

    default void setDayForCleanup(final int value) {
        setSystemPropertyAsInt(Constants.DAY_FOR_CLEANUP_PROPERTY, value);
    }

    default int getDayForCleanup() {
        return getSystemPropertyAsInt(Constants.DAY_FOR_CLEANUP_PROPERTY, Constants.DEFAULT_DAY_FOR_CLEANUP);
    }

    default void setCrawlingThreadCount(final int value) {
        setSystemPropertyAsInt(Constants.CRAWLING_THREAD_COUNT_PROPERTY, value);
    }

    default int getCrawlingThreadCount() {
        return getSystemPropertyAsInt(Constants.CRAWLING_THREAD_COUNT_PROPERTY, 5);
    }

    default void setSearchLog(final boolean value) {
        setSystemPropertyAsBoolean(Constants.SEARCH_LOG_PROPERTY, value);
    }

    default boolean isSearchLog() {
        return getSystemPropertyAsBoolean(Constants.SEARCH_LOG_PROPERTY, true);
    }

    default void setUserInfo(final boolean value) {
        setSystemPropertyAsBoolean(Constants.USER_INFO_PROPERTY, value);
    }

    default boolean isUserInfo() {
        return getSystemPropertyAsBoolean(Constants.USER_INFO_PROPERTY, true);
    }

    default void setUserFavorite(final boolean value) {
        setSystemPropertyAsBoolean(Constants.USER_FAVORITE_PROPERTY, value);
    }

    default boolean isUserFavorite() {
        return getSystemPropertyAsBoolean(Constants.USER_FAVORITE_PROPERTY, false);
    }

    default void setWebApiJson(final boolean value) {
        setSystemPropertyAsBoolean(Constants.WEB_API_JSON_PROPERTY, value);
    }

    default boolean isWebApiJson() {
        return getSystemPropertyAsBoolean(Constants.WEB_API_JSON_PROPERTY, true);
    }

    default boolean isWebApiGsa() {
        return getSystemPropertyAsBoolean(Constants.WEB_API_GSA_PROPERTY, false);
    }

    default void setAppendQueryParameter(final boolean value) {
        setSystemPropertyAsBoolean(Constants.APPEND_QUERY_PARAMETER_PROPERTY, value);
    }

    default boolean isAppendQueryParameter() {
        return getSystemPropertyAsBoolean(Constants.APPEND_QUERY_PARAMETER_PROPERTY, false);
    }

    default void setIgnoreFailureType(final String value) {
        setSystemProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY, value);
    }

    default String getIgnoreFailureType() {
        return getSystemProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY, Constants.DEFAULT_IGNORE_FAILURE_TYPE);
    }

    default void setFailureCountThreshold(final int value) {
        setSystemPropertyAsInt(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY, value);
    }

    default int getFailureCountThreshold() {
        return getSystemPropertyAsInt(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY, Constants.DEFAULT_FAILURE_COUNT);
    }

    default void setWebApiPopularWord(final boolean value) {
        setSystemPropertyAsBoolean(Constants.WEB_API_POPULAR_WORD_PROPERTY, value);
    }

    default boolean isWebApiPopularWord() {
        return getSystemPropertyAsBoolean(Constants.WEB_API_POPULAR_WORD_PROPERTY, true);
    }

    default void setCsvFileEncoding(final String value) {
        setSystemProperty(Constants.CSV_FILE_ENCODING_PROPERTY, value);
    }

    default String getCsvFileEncoding() {
        return getSystemProperty(Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8);
    }

    default void setPurgeSearchLogDay(final int value) {
        setSystemPropertyAsInt(Constants.PURGE_SEARCH_LOG_DAY_PROPERTY, value);
    }

    default int getPurgeSearchLogDay() {
        return getSystemPropertyAsInt(Constants.PURGE_SEARCH_LOG_DAY_PROPERTY, DfTypeUtil.toInteger(Constants.DEFAULT_PURGE_DAY));
    }

    default void setPurgeJobLogDay(final int value) {
        setSystemPropertyAsInt(Constants.PURGE_JOB_LOG_DAY_PROPERTY, value);
    }

    default int getPurgeJobLogDay() {
        return getSystemPropertyAsInt(Constants.PURGE_JOB_LOG_DAY_PROPERTY, DfTypeUtil.toInteger(Constants.DEFAULT_PURGE_DAY));
    }

    default void setPurgeUserInfoDay(final int value) {
        setSystemPropertyAsInt(Constants.PURGE_USER_INFO_DAY_PROPERTY, value);
    }

    default int getPurgeUserInfoDay() {
        return getSystemPropertyAsInt(Constants.PURGE_USER_INFO_DAY_PROPERTY, DfTypeUtil.toInteger(Constants.DEFAULT_PURGE_DAY));
    }

    default void setPurgeByBots(final String value) {
        setSystemProperty(Constants.PURGE_BY_BOTS_PROPERTY, value);
    }

    default String getPurgeByBots() {
        return getSystemProperty(Constants.PURGE_BY_BOTS_PROPERTY, Constants.DEFAULT_PURGE_BY_BOTS);
    }

    default boolean hasNotification() {
        return StringUtil.isNotBlank(getNotificationTo()) || StringUtil.isNotBlank(getSlackWebhookUrls());
    }

    default void setSlackWebhookUrls(final String value) {
        setSystemProperty(Constants.SLACK_WEBHOOK_URLS_PROPERTY, value);
    }

    default String getSlackWebhookUrls() {
        return getSystemProperty(Constants.SLACK_WEBHOOK_URLS_PROPERTY, StringUtil.EMPTY);
    }

    default void setGoogleChatWebhookUrls(final String value) {
        setSystemProperty(Constants.GOOGLE_CHAT_WEBHOOK_URLS_PROPERTY, value);
    }

    default String getGoogleChatWebhookUrls() {
        return getSystemProperty(Constants.GOOGLE_CHAT_WEBHOOK_URLS_PROPERTY, StringUtil.EMPTY);
    }

    default void setNotificationTo(final String value) {
        setSystemProperty(Constants.NOTIFICATION_TO_PROPERTY, value);
    }

    default String getNotificationTo() {
        return getSystemProperty(Constants.NOTIFICATION_TO_PROPERTY, StringUtil.EMPTY);
    }

    default void setSuggestSearchLog(final boolean value) {
        setSystemPropertyAsBoolean(Constants.SUGGEST_SEARCH_LOG_PROPERTY, value);
    }

    default boolean isSuggestSearchLog() {
        return getSystemPropertyAsBoolean(Constants.SUGGEST_SEARCH_LOG_PROPERTY, true);
    }

    default void setSuggestDocuments(final boolean value) {
        setSystemPropertyAsBoolean(Constants.SUGGEST_DOCUMENTS_PROPERTY, value);
    }

    default boolean isSuggestDocuments() {
        return getSystemPropertyAsBoolean(Constants.SUGGEST_DOCUMENTS_PROPERTY, true);
    }

    default void setPurgeSuggestSearchLogDay(final int value) {
        setSystemPropertyAsInt(Constants.PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY, value);
    }

    default int getPurgeSuggestSearchLogDay() {
        return getSystemPropertyAsInt(Constants.PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY,
                DfTypeUtil.toInteger(Constants.DEFAULT_SUGGEST_PURGE_DAY));
    }

    default void setLdapInitialContextFactory(final String value) {
        setSystemProperty(Constants.LDAP_INITIAL_CONTEXT_FACTORY, value);
    }

    default String getLdapInitialContextFactory() {
        return getSystemProperty(Constants.LDAP_INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    }

    default void setLdapSecurityAuthentication(final String value) {
        setSystemProperty(Constants.LDAP_SECURITY_AUTHENTICATION, value);
    }

    default String getLdapSecurityAuthentication() {
        return getSystemProperty(Constants.LDAP_SECURITY_AUTHENTICATION, "simple");
    }

    default void setLdapProviderUrl(final String value) {
        setSystemProperty(Constants.LDAP_PROVIDER_URL, value);
    }

    default String getLdapProviderUrl() {
        return getSystemProperty(Constants.LDAP_PROVIDER_URL);
    }

    default void setLdapSecurityPrincipal(final String value) {
        setSystemProperty(Constants.LDAP_SECURITY_PRINCIPAL, value);
    }

    default String getLdapMemberofAttribute() {
        return getSystemProperty(Constants.LDAP_MEMBEROF_ATTRIBUTE, "memberOf");
    }

    default void setLdapMemberofAttribute(final String value) {
        setSystemProperty(Constants.LDAP_MEMBEROF_ATTRIBUTE, value);
    }

    default void setStorageEndpoint(final String value) {
        setSystemProperty(Constants.STORAGE_ENDPOINT, value);
    }

    default String getStorageEndpoint() {
        return getSystemProperty(Constants.STORAGE_ENDPOINT, StringUtil.EMPTY);
    }

    default void setStorageAccessKey(final String value) {
        setSystemProperty(Constants.STORAGE_ACCESS_KEY, value);
    }

    default String getStorageAccessKey() {
        return getSystemProperty(Constants.STORAGE_ACCESS_KEY, StringUtil.EMPTY);
    }

    default void setStorageSecretKey(final String value) {
        setSystemProperty(Constants.STORAGE_SECRET_KEY, value);
    }

    default String getStorageSecretKey() {
        return getSystemProperty(Constants.STORAGE_SECRET_KEY, StringUtil.EMPTY);
    }

    default void setStorageBucket(final String value) {
        setSystemProperty(Constants.STORAGE_BUCKET, value);
    }

    default String getStorageBucket() {
        return getSystemProperty(Constants.STORAGE_BUCKET, StringUtil.EMPTY);
    }

    Integer getLdapMaxUsernameLengthAsInteger();

    default String getLdapSecurityPrincipal(final String username) {
        final String value;
        final int maxLength = getLdapMaxUsernameLengthAsInteger();
        if (username == null) {
            value = StringUtil.EMPTY;
        } else if (maxLength >= 0 && username.length() > maxLength) {
            value = username.substring(0, maxLength);
        } else {
            value = username;
        }
        return String.format(getSystemProperty(Constants.LDAP_SECURITY_PRINCIPAL, StringUtil.EMPTY), value);
    }

    default String getLdapSecurityPrincipal() {
        return getSystemProperty(Constants.LDAP_SECURITY_PRINCIPAL);
    }

    default void setLdapAdminSecurityPrincipal(final String value) {
        setSystemProperty(Constants.LDAP_ADMIN_SECURITY_PRINCIPAL, value);
    }

    default String getLdapAdminSecurityPrincipal() {
        return getSystemProperty(Constants.LDAP_ADMIN_SECURITY_PRINCIPAL);
    }

    default void setLdapAdminSecurityCredentials(final String value) {
        setSystemProperty(Constants.LDAP_ADMIN_SECURITY_CREDENTIALS,
                Constants.CIPHER_PREFIX + ComponentUtil.getPrimaryCipher().encrypt(value));
    }

    default String getLdapAdminSecurityCredentials() {
        final String value = getSystemProperty(Constants.LDAP_ADMIN_SECURITY_CREDENTIALS);
        if (StringUtil.isNotBlank(value) && value.startsWith(Constants.CIPHER_PREFIX)) {
            return ComponentUtil.getPrimaryCipher().decrypt(value.substring(Constants.CIPHER_PREFIX.length()));
        }
        return value;
    }

    default void setLdapBaseDn(final String value) {
        setSystemProperty(Constants.LDAP_BASE_DN, value);
    }

    default String getLdapBaseDn() {
        return getSystemProperty(Constants.LDAP_BASE_DN);
    }

    default void setLdapAccountFilter(final String value) {
        setSystemProperty(Constants.LDAP_ACCOUNT_FILTER, value);
    }

    default String getLdapAccountFilter() {
        return getSystemProperty(Constants.LDAP_ACCOUNT_FILTER);
    }

    default void setLdapGroupFilter(final String value) {
        setSystemProperty(Constants.LDAP_GROUP_FILTER, value);
    }

    default String getLdapGroupFilter() {
        return getSystemProperty(Constants.LDAP_GROUP_FILTER, StringUtil.EMPTY);
    }

    default void setNotificationLogin(final String value) {
        setSystemProperty(Constants.NOTIFICATION_LOGIN, value);
    }

    default String getNotificationLogin() {
        return getSystemProperty(Constants.NOTIFICATION_LOGIN, StringUtil.EMPTY);
    }

    default String getNotificationAdvanceSearch() {
        return getSystemProperty(Constants.NOTIFICATION_ADVANCE_SEARCH, StringUtil.EMPTY);
    }

    default void setNotificationSearchTop(final String value) {
        setSystemProperty(Constants.NOTIFICATION_SEARCH_TOP, value);
    }

    default String getNotificationSearchTop() {
        return getSystemProperty(Constants.NOTIFICATION_SEARCH_TOP, StringUtil.EMPTY);
    }

    default String getUserAgentName() {
        return getSystemProperty(Constants.CRAWLING_USER_AGENT_PROPERTY, Constants.CRAWLING_USER_AGENT_PREFIX
                + ComponentUtil.getSystemHelper().getProductVersion() + Constants.CRAWLING_USER_AGENT_SUFFIX);
    }

    default void setLtrModelName(final String value) {
        setSystemProperty(Constants.LTR_MODEL_NAME_PROPERTY, value);
    }

    default String getLtrModelName() {
        return getSystemProperty(Constants.LTR_MODEL_NAME_PROPERTY, StringUtil.EMPTY);
    }

    default void setLtrWindowSize(final int value) {
        setSystemPropertyAsInt(Constants.LTR_WINDOW_SIZE_PROPERTY, value);
    }

    default int getLtrWindowSize() {
        return getSystemPropertyAsInt(Constants.LTR_WINDOW_SIZE_PROPERTY, 100);
    }

    default String[] getAzureAdPermissionFields() {
        return split(getSystemProperty("aad.permission.fields", "mail"), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).toArray(n -> new String[n]));
    }

    default boolean isAzureAdUseDomainServices() {
        return Constants.TRUE.equalsIgnoreCase(getSystemProperty("aad.use.ds", "true"));
    }

    //
    // fess_*.properties
    //

    String getAuthenticationAdminRoles();

    default String[] getAuthenticationAdminRolesAsArray() {
        String[] roles = (String[]) propMap.get(AUTHENTICATION_ADMIN_ROLES);
        if (roles == null) {
            roles = getAuthenticationAdminRoles().split(",");
            propMap.put(AUTHENTICATION_ADMIN_ROLES, roles);
        }
        return roles;
    }

    String getJvmCrawlerOptions();

    default String[] getJvmCrawlerOptionsAsArray() {
        return JvmUtil.filterJvmOptions(getJvmCrawlerOptions().split("\n"));
    }

    String getJvmSuggestOptions();

    default String[] getJvmSuggestOptionsAsArray() {
        return JvmUtil.filterJvmOptions(getJvmSuggestOptions().split("\n"));
    }

    String getJvmThumbnailOptions();

    default String[] getJvmThumbnailOptionsAsArray() {
        return JvmUtil.filterJvmOptions(getJvmThumbnailOptions().split("\n"));
    }

    String getCrawlerDocumentHtmlPrunedTags();

    default PrunedTag[] getCrawlerDocumentHtmlPrunedTagsAsArray() {
        PrunedTag[] tags = (PrunedTag[]) propMap.get("crawlerDocumentHtmlPrunedTags");
        if (tags == null) {
            tags = PrunedTag.parse(getCrawlerDocumentHtmlPrunedTags());
            propMap.put("crawlerDocumentHtmlPrunedTags", tags);
        }
        return tags;
    }

    String getCrawlerDocumentCacheHtmlMimetypes();

    default boolean isHtmlMimetypeForCache(final String mimetype) {
        final String[] mimetypes = getCrawlerDocumentCacheHtmlMimetypes().split(",");
        if (mimetypes.length == 1 && StringUtil.isBlank(mimetypes[0])) {
            return true;
        }
        return stream(mimetypes).get(stream -> stream.anyMatch(s -> s.equalsIgnoreCase(mimetype)));
    }

    String getCrawlerDocumentCacheSupportedMimetypes();

    default boolean isSupportedDocumentCacheMimetypes(final String mimetype) {
        final String[] mimetypes = getCrawlerDocumentCacheSupportedMimetypes().split(",");
        if (mimetypes.length == 1 && StringUtil.isBlank(mimetypes[0])) {
            return true;
        }
        return stream(mimetypes).get(stream -> stream.anyMatch(s -> s.equalsIgnoreCase(mimetype)));
    }

    String getIndexerClickCountEnabled();

    default boolean getIndexerClickCountEnabledAsBoolean() {
        return Constants.TRUE.equalsIgnoreCase(getIndexerClickCountEnabled());
    }

    String getIndexerFavoriteCountEnabled();

    default boolean getIndexerFavoriteCountEnabledAsBoolean() {
        return Constants.TRUE.equalsIgnoreCase(getIndexerFavoriteCountEnabled());
    }

    String getApiAccessTokenRequired();

    default boolean getApiAccessTokenRequiredAsBoolean() {
        return Constants.TRUE.equalsIgnoreCase(getApiAccessTokenRequired());
    }

    String getIndexerThreadDumpEnabled();

    default boolean getIndexerThreadDumpEnabledAsBoolean() {
        return Constants.TRUE.equalsIgnoreCase(getIndexerThreadDumpEnabled());
    }

    String getIndexBackupTargets();

    String getIndexBackupLogTargets();

    default String[] getIndexBackupAllTargets() {
        return split(getIndexBackupTargets() + "," + getIndexBackupLogTargets(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).toArray(n -> new String[n]));
    }

    String getJobSystemJobIds();

    default boolean isSystemJobId(final String id) {
        if (StringUtil.isBlank(getJobSystemJobIds())) {
            return false;
        }
        return split(getJobSystemJobIds(), ",").get(stream -> stream.anyMatch(s -> s.equals(id)));
    }

    String getSmbAvailableSidTypes();

    default Integer getAvailableSmbSidType(final int sidType) {
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> params = (Map<Integer, Integer>) propMap.get(SMB_AVAILABLE_SID_TYPES);
        if (params == null) {
            params = split(getSmbAvailableSidTypes(), ",").get(stream -> stream.map(s -> {
                final String[] v = s.split(":");
                if (v.length == 1) {
                    final int x = DfTypeUtil.toInteger(v[0].trim());
                    return new Pair<>(x, x);
                }
                if (v.length == 2) {
                    final int x = DfTypeUtil.toInteger(v[0].trim());
                    final int y = DfTypeUtil.toInteger(v[1].trim());
                    return new Pair<>(x, y);
                }
                return null;
            }).filter(v -> v != null).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            propMap.put(SMB_AVAILABLE_SID_TYPES, params);
        }
        return params.get(sidType);
    }

    String getSupportedLanguages();

    default String[] getSupportedLanguagesAsArray() {
        return split(getSupportedLanguages(), ",").get(stream -> stream.filter(StringUtil::isNotBlank).toArray(n -> new String[n]));
    }

    String getOnlineHelpSupportedLangs();

    default boolean isOnlineHelpSupportedLang(final String lang) {
        if (StringUtil.isBlank(getOnlineHelpSupportedLangs())) {
            return false;
        }
        return split(getOnlineHelpSupportedLangs(), ",").get(stream -> stream.filter(StringUtil::isNotBlank).anyMatch(s -> s.equals(lang)));
    }

    String getForumSupportedLangs();

    default boolean isForumSupportedLang(final String lang) {
        if (StringUtil.isBlank(getForumSupportedLangs())) {
            return false;
        }
        return split(getForumSupportedLangs(), ",").get(stream -> stream.filter(StringUtil::isNotBlank).anyMatch(s -> s.equals(lang)));
    }

    String getSupportedUploadedJsExtentions();

    default String[] getSupportedUploadedJsExtentionsAsArray() {
        return split(getSupportedUploadedJsExtentions(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).toArray(n -> new String[n]));
    }

    String getSupportedUploadedCssExtentions();

    default String[] getSupportedUploadedCssExtentionsAsArray() {
        return split(getSupportedUploadedCssExtentions(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).toArray(n -> new String[n]));
    }

    String getSupportedUploadedMediaExtentions();

    default String[] getSupportedUploadedMediaExtentionsAsArray() {
        return split(getSupportedUploadedMediaExtentions(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).toArray(n -> new String[n]));
    }

    String getJobTemplateTitleWeb();

    String getJobTemplateTitleFile();

    String getJobTemplateTitleData();

    default String getJobTemplateTitle(final String type) {
        if (Constants.WEB_CRAWLER_TYPE.equals(type)) {
            return getJobTemplateTitleWeb();
        }
        if (Constants.FILE_CRAWLER_TYPE.equals(type)) {
            return getJobTemplateTitleFile();
        }
        if (Constants.DATA_CRAWLER_TYPE.equals(type)) {
            return getJobTemplateTitleData();
        }
        return "None";
    }

    String getSchedulerJobClass();

    default Class<? extends LaJob> getSchedulerJobClassAsClass() {
        try {
            return (Class<? extends LaJob>) Class.forName(getSchedulerJobClass());
        } catch (final ClassNotFoundException e) {
            throw new ClassNotFoundRuntimeException(e);
        }
    }

    String getSchedulerConcurrentExecMode();

    default JobConcurrentExec getSchedulerConcurrentExecModeAsEnum() {
        return JobConcurrentExec.valueOf(getSchedulerConcurrentExecMode());
    }

    String getCrawlerMetadataContentExcludes();

    default boolean isCrawlerMetadataContentIncluded(final String name) {
        Pattern[] patterns = (Pattern[]) propMap.get(CRAWLER_METADATA_CONTENT_EXCLUDES);
        if (patterns == null) {
            patterns = split(getCrawlerMetadataContentExcludes(), ",")
                    .get(stream -> stream.filter(StringUtil::isNotBlank).map(Pattern::compile).toArray(n -> new Pattern[n]));
            propMap.put(CRAWLER_METADATA_CONTENT_EXCLUDES, patterns);
        }
        return !stream(patterns).get(stream -> stream.anyMatch(p -> p.matcher(name).matches()));
    }

    String getCrawlerMetadataNameMapping();

    default Tuple3<String, String, String> getCrawlerMetadataNameMapping(final String name) {
        @SuppressWarnings("unchecked")
        Map<String, Tuple3<String, String, String>> params =
                (Map<String, Tuple3<String, String, String>>) propMap.get(CRAWLER_METADATA_NAME_MAPPING);
        if (params == null) {
            params = split(getCrawlerMetadataNameMapping(), "\n").get(stream -> stream.filter(StringUtil::isNotBlank).map(v -> {
                final String[] values = v.split("=");
                if (values.length == 2) {
                    final String[] subValues = values[1].split(":", 3);
                    if (subValues.length == 3) {
                        return new Tuple4<>(values[0], subValues[0], subValues[1], subValues[2]);
                    }
                    if (subValues.length == 2) {
                        return new Tuple4<>(values[0], subValues[0], subValues[1], StringUtil.EMPTY);
                    }
                    return new Tuple4<>(values[0], values[1], Constants.MAPPING_TYPE_ARRAY, StringUtil.EMPTY);
                }
                return null;
            }).collect(Collectors.toMap(Tuple4::getValue1, d -> new Tuple3<>(d.getValue2(), d.getValue3(), d.getValue4()))));
            propMap.put(CRAWLER_METADATA_NAME_MAPPING, params);
        }
        return params.get(name);
    }

    String getSuggestPopularWordFields();

    default String[] getSuggestPopularWordFieldsAsArray() {
        return split(getSuggestPopularWordFields(), "\n").get(stream -> stream.filter(StringUtil::isNotBlank).toArray(n -> new String[n]));
    }

    String getSuggestPopularWordTags();

    default String[] getSuggestPopularWordTagsAsArray() {
        return split(getSuggestPopularWordTags(), "\n").get(stream -> stream.filter(StringUtil::isNotBlank).toArray(n -> new String[n]));
    }

    String getSuggestPopularWordExcludes();

    default String[] getSuggestPopularWordExcludesAsArray() {
        return split(getSuggestPopularWordExcludes(), "\n")
                .get(stream -> stream.filter(StringUtil::isNotBlank).toArray(n -> new String[n]));
    }

    String getQueryReplaceTermWithPrefixQuery();

    default boolean getQueryReplaceTermWithPrefixQueryAsBoolean() {
        return Boolean.parseBoolean(getQueryReplaceTermWithPrefixQuery());
    }

    String getQueryDefaultLanguages();

    String getQueryLanguageMapping();

    default Map<String, String> getQueryLanguageMappingMap() {
        @SuppressWarnings("unchecked")
        Map<String, String> params = (Map<String, String>) propMap.get(QUERY_LANGUAGE_MAPPING);
        if (params == null) {
            params = stream(getQueryLanguageMapping().split("\n")).get(stream -> stream.filter(StringUtil::isNotBlank).map(v -> {
                final String[] values = v.split("=");
                if (values.length == 2) {
                    return new Pair<>(values[0], values[1]);
                }
                return null;
            }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            propMap.put(QUERY_LANGUAGE_MAPPING, params);
        }
        return params;
    }

    default String[] normalizeQueryLanguages(final String[] langs) {
        final Map<String, String> mapping = getQueryLanguageMappingMap();
        return stream(langs).get(stream -> stream.map(s -> {
            if (StringUtil.isBlank(s)) {
                return null;
            }
            final String lang1 = mapping.get(s);
            if (lang1 != null) {
                return lang1;
            }
            return mapping.get(s.split("[\\-_]")[0]);
        }).filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n]));
    }

    default String[] getQueryLanguages(final Enumeration<Locale> locales, final String[] requestLangs) {
        // requestLangs > default > browser
        if (StringUtil.isNotBlank(getQueryDefaultLanguages())) {
            String[] langs = (String[]) propMap.get(QUERY_DEFAULT_LANGUAGES);
            if (langs == null) {
                langs = split(getQueryDefaultLanguages(), ",").get(stream -> stream.map(String::trim).toArray(n -> new String[n]));
                propMap.put(QUERY_DEFAULT_LANGUAGES, langs);

            }
            return normalizeQueryLanguages(langs);
        }

        if (requestLangs != null && requestLangs.length != 0) {
            return normalizeQueryLanguages(requestLangs);
        }

        if (locales == null) {
            return StringUtil.EMPTY_STRINGS;
        }

        return normalizeQueryLanguages(Collections.list(locales).stream().map(locale -> {
            final String language = locale.getLanguage();
            final String country = locale.getCountry();
            if (StringUtil.isNotBlank(language)) {
                if (StringUtil.isNotBlank(country)) {
                    return language.toLowerCase(Locale.ROOT) + "-" + country.toLowerCase(Locale.ROOT);
                }
                return language.toLowerCase(Locale.ROOT);
            }
            return null;
        }).toArray(n -> new String[n]));
    }

    default Locale getQueryLocaleFromName(final String name) {
        if (name == null) {
            return Locale.ROOT;
        }
        final String value = name.toLowerCase(Locale.ROOT);
        final Map<String, String> mapping = getQueryLanguageMappingMap();
        for (final String key : mapping.keySet()) {
            if (value.endsWith("_" + key.toLowerCase(Locale.ROOT))) {
                final String[] values = key.split("_");
                switch (values.length) {
                case 1:
                    return new Locale(values[0]);
                case 2:
                    return new Locale(values[0], values[1]);
                case 3:
                    return new Locale(values[0], values[1], values[2]);
                default:
                    break;
                }
            }
        }
        return Locale.ROOT;
    }

    String getSupportedUploadedFiles();

    default boolean isSupportedUploadedFile(final String name) {
        return split(getSuggestPopularWordExcludes(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).anyMatch(s -> s.equals(name)));
    }

    String getLdapAdminUserObjectClasses();

    default Attribute getLdapAdminUserObjectClassAttribute() {
        final Attribute oc = new BasicAttribute("objectClass");
        split(getLdapAdminUserObjectClasses(), ",").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(s -> oc.add(s.trim())));
        return oc;
    }

    String getLdapAdminUserFilter();

    default String getLdapAdminUserFilter(final String name) {
        return String.format(getLdapAdminUserFilter(), name);
    }

    String getLdapAdminUserBaseDn();

    default String getLdapAdminUserSecurityPrincipal(final String name) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append(String.format(getLdapAdminUserFilter(), name));
        if (StringUtil.isNotBlank(getLdapAdminUserBaseDn())) {
            buf.append(',').append(getLdapAdminUserBaseDn());
        }
        return buf.toString();
    }

    String getLdapAdminRoleObjectClasses();

    default Attribute getLdapAdminRoleObjectClassAttribute() {
        final Attribute oc = new BasicAttribute("objectClass");
        split(getLdapAdminRoleObjectClasses(), ",").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(s -> oc.add(s.trim())));
        return oc;
    }

    String getLdapAdminRoleFilter();

    default String getLdapAdminRoleFilter(final String name) {
        return String.format(getLdapAdminRoleFilter(), name);
    }

    String getLdapAdminRoleBaseDn();

    default String getLdapAdminRoleSecurityPrincipal(final String name) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append(String.format(getLdapAdminRoleFilter(), name));
        if (StringUtil.isNotBlank(getLdapAdminRoleBaseDn())) {
            buf.append(',').append(getLdapAdminRoleBaseDn());
        }
        return buf.toString();
    }

    String getLdapAdminGroupObjectClasses();

    default Attribute getLdapAdminGroupObjectClassAttribute() {
        final Attribute oc = new BasicAttribute("objectClass");
        split(getLdapAdminGroupObjectClasses(), ",").of(stream -> stream.filter(StringUtil::isNotBlank).forEach(s -> oc.add(s.trim())));
        return oc;
    }

    String getLdapAdminGroupFilter();

    default String getLdapAdminGroupFilter(final String name) {
        return String.format(getLdapAdminGroupFilter(), name);
    }

    String getLdapAdminGroupBaseDn();

    default String getLdapAdminGroupSecurityPrincipal(final String name) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append(String.format(getLdapAdminGroupFilter(), name));
        if (StringUtil.isNotBlank(getLdapAdminGroupBaseDn())) {
            buf.append(',').append(getLdapAdminGroupBaseDn());
        }
        return buf.toString();
    }

    String getAuthenticationAdminUsers();

    default boolean isAdminUser(final String username) {
        return split(getAuthenticationAdminUsers(), ",").get(stream -> stream.anyMatch(s -> s.equals(username)));
    }

    boolean isLdapAdminEnabled();

    default boolean isLdapAdminEnabled(final String username) {
        if (isAdminUser(username)) {
            return false;
        }
        return isLdapAdminEnabled();
    }

    String getCrawlerWebProtocols();

    default String[] getCrawlerWebProtocolsAsArray() {
        return split(getCrawlerWebProtocols(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> s.trim() + ":").toArray(n -> new String[n]));
    }

    default boolean isValidCrawlerWebProtocol(final String url) {
        return stream(getCrawlerWebProtocolsAsArray()).get(stream -> stream.anyMatch(s -> url.startsWith(s)));
    }

    String getCrawlerFileProtocols();

    default String[] getCrawlerFileProtocolsAsArray() {
        return split(getCrawlerFileProtocols(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> s.trim() + ":").toArray(n -> new String[n]));
    }

    default boolean isValidCrawlerFileProtocol(final String url) {
        return stream(getCrawlerFileProtocolsAsArray()).get(stream -> stream.anyMatch(s -> url.startsWith(s)));
    }

    String getRoleSearchDefaultPermissions();

    default String[] getSearchDefaultPermissionsAsArray() {
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
        return split(getRoleSearchDefaultPermissions(), ",").get(stream -> stream.map(p -> permissionHelper.encode(p))
                .filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n]));
    }

    String getRoleSearchDefaultDisplayPermissions();

    default String[] getSearchDefaultDisplayEncodedPermissions() {
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
        return split(getRoleSearchDefaultDisplayPermissions(), ",").get(stream -> stream.map(p -> permissionHelper.encode(p))
                .filter(StringUtil::isNotBlank).distinct().toArray(n -> new String[n]));
    }

    default String getSearchDefaultDisplayPermission() {
        return split(getRoleSearchDefaultDisplayPermissions(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).distinct().collect(Collectors.joining("\n")));
    }

    String getQueryGeoFields();

    default String[] getQueryGeoFieldsAsArray() {
        return split(getQueryGeoFields(), ",")
                .get(stream -> stream.map(String::trim).filter(StringUtil::isNotBlank).toArray(n -> new String[n]));
    }

    String getSuggestSearchLogPermissions();

    default boolean isValidSearchLogPermissions(final String[] permissions) {
        if (permissions == null) {
            return false;
        }
        @SuppressWarnings("unchecked")
        List<String> validPermissionList = (List<String>) propMap.get(SUGGEST_SEARCH_LOG_PERMISSIONS);
        if (validPermissionList == null) {
            final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
            validPermissionList = split(getSuggestSearchLogPermissions(), ",")
                    .get(stream -> stream.map(s -> permissionHelper.encode(s)).filter(StringUtil::isNotBlank).collect(Collectors.toList()));
            propMap.put(SUGGEST_SEARCH_LOG_PERMISSIONS, validPermissionList);
        }
        final List<String> list = validPermissionList;
        return stream(permissions).get(stream -> stream.allMatch(v -> list.contains(v)));
    }

    String getRoleSearchUserPrefix();

    String getRoleSearchGuestPermissions();

    default List<String> getSearchGuestPermissionList() {
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) propMap.get(SEARCH_GUEST_PERMISSION_LIST);
        if (list == null) {
            final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();
            list = split(getRoleSearchGuestPermissions(), ",")
                    .get(stream -> stream.map(s -> permissionHelper.encode(s)).filter(StringUtil::isNotBlank).collect(Collectors.toList()));
            list.add(getRoleSearchUserPrefix() + Constants.GUEST_USER);
            propMap.put(SEARCH_GUEST_PERMISSION_LIST, list);
        }
        return list;
    }

    String getIndexAdminArrayFields();

    default Set<String> getIndexAdminArrayFieldSet() {
        @SuppressWarnings("unchecked")
        Set<String> fieldSet = (Set<String>) propMap.get(INDEX_ADMIN_ARRAY_FIELD_SET);
        if (fieldSet == null) {
            fieldSet = split(getIndexAdminArrayFields(), ",")
                    .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toSet()));
            propMap.put(INDEX_ADMIN_ARRAY_FIELD_SET, fieldSet);
        }
        return fieldSet;
    }

    default boolean validateIndexArrayFields(final Map<String, Object> source) {
        return invalidIndexArrayFields(source).isEmpty();
    }

    default List<String> invalidIndexArrayFields(final Map<String, Object> source) {
        // TODO always returns empty list
        return split(getIndexAdminArrayFields(), ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim)
                .filter(s -> isNonEmptyValue(source.get(s))).filter(s -> false) // TODO
                .collect(Collectors.toList()));
    }

    String getIndexAdminDateFields();

    default Set<String> getIndexAdminDateFieldSet() {
        @SuppressWarnings("unchecked")
        Set<String> fieldSet = (Set<String>) propMap.get(INDEX_ADMIN_DATE_FIELD_SET);
        if (fieldSet == null) {
            fieldSet = split(getIndexAdminDateFields(), ",")
                    .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toSet()));
            propMap.put(INDEX_ADMIN_DATE_FIELD_SET, fieldSet);
        }
        return fieldSet;
    }

    default boolean validateIndexDateFields(final Map<String, Object> source) {
        return invalidIndexDateFields(source).isEmpty();
    }

    default List<String> invalidIndexDateFields(final Map<String, Object> source) {
        return split(getIndexAdminDateFields(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).filter(s -> isNonEmptyValue(source.get(s)))
                        .filter(s -> !validateDateTimeString(source.get(s))).collect(Collectors.toList()));
    }

    default boolean validateDateTimeString(final Object obj) {
        if (FessFunctions.parseDate(obj.toString()) != null) {
            return true;
        }
        return false;
    }

    String getIndexAdminIntegerFields();

    default Set<String> getIndexAdminIntegerFieldSet() {
        @SuppressWarnings("unchecked")
        Set<String> fieldSet = (Set<String>) propMap.get(INDEX_ADMIN_INTEGER_FIELD_SET);
        if (fieldSet == null) {
            fieldSet = split(getIndexAdminIntegerFields(), ",")
                    .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toSet()));
            propMap.put(INDEX_ADMIN_INTEGER_FIELD_SET, fieldSet);
        }
        return fieldSet;
    }

    default boolean validateIndexIntegerFields(final Map<String, Object> source) {
        return invalidIndexIntegerFields(source).isEmpty();
    }

    default List<String> invalidIndexIntegerFields(final Map<String, Object> source) {
        final IntegerTypeValidator integerValidator = new IntegerTypeValidator();
        return split(getIndexAdminIntegerFields(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).filter(s -> isNonEmptyValue(source.get(s)))
                        .filter(s -> !integerValidator.isValid(source.get(s).toString(), null)).collect(Collectors.toList()));
    }

    String getIndexAdminLongFields();

    default Set<String> getIndexAdminLongFieldSet() {
        @SuppressWarnings("unchecked")
        Set<String> fieldSet = (Set<String>) propMap.get(INDEX_ADMIN_LONG_FIELD_SET);
        if (fieldSet == null) {
            fieldSet = split(getIndexAdminLongFields(), ",")
                    .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toSet()));
            propMap.put(INDEX_ADMIN_LONG_FIELD_SET, fieldSet);
        }
        return fieldSet;
    }

    default boolean validateIndexLongFields(final Map<String, Object> source) {
        return invalidIndexLongFields(source).isEmpty();
    }

    default List<String> invalidIndexLongFields(final Map<String, Object> source) {
        final LongTypeValidator longValidator = new LongTypeValidator();
        return split(getIndexAdminLongFields(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).filter(s -> isNonEmptyValue(source.get(s)))
                        .filter(s -> !longValidator.isValid(source.get(s).toString(), null)).collect(Collectors.toList()));
    }

    String getIndexAdminFloatFields();

    default Set<String> getIndexAdminFloatFieldSet() {
        @SuppressWarnings("unchecked")
        Set<String> fieldSet = (Set<String>) propMap.get(INDEX_ADMIN_FLOAT_FIELD_SET);
        if (fieldSet == null) {
            fieldSet = split(getIndexAdminFloatFields(), ",")
                    .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toSet()));
            propMap.put(INDEX_ADMIN_FLOAT_FIELD_SET, fieldSet);
        }
        return fieldSet;
    }

    default boolean validateIndexFloatFields(final Map<String, Object> source) {
        return invalidIndexFloatFields(source).isEmpty();
    }

    default List<String> invalidIndexFloatFields(final Map<String, Object> source) {
        final FloatTypeValidator floatValidator = new FloatTypeValidator();
        return split(getIndexAdminFloatFields(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).filter(s -> isNonEmptyValue(source.get(s)))
                        .filter(s -> !floatValidator.isValid(source.get(s).toString(), null)).collect(Collectors.toList()));
    }

    String getIndexAdminDoubleFields();

    default Set<String> getIndexAdminDoubleFieldSet() {
        @SuppressWarnings("unchecked")
        Set<String> fieldSet = (Set<String>) propMap.get(INDEX_ADMIN_DOUBLE_FIELD_SET);
        if (fieldSet == null) {
            fieldSet = split(getIndexAdminDoubleFields(), ",")
                    .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toSet()));
            propMap.put(INDEX_ADMIN_DOUBLE_FIELD_SET, fieldSet);
        }
        return fieldSet;
    }

    default boolean validateIndexDoubleFields(final Map<String, Object> source) {
        return invalidIndexDoubleFields(source).isEmpty();
    }

    default List<String> invalidIndexDoubleFields(final Map<String, Object> source) {
        final DoubleTypeValidator doubleValidator = new DoubleTypeValidator();
        return split(getIndexAdminDoubleFields(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).filter(s -> isNonEmptyValue(source.get(s)))
                        .filter(s -> !doubleValidator.isValid(source.get(s).toString(), null)).collect(Collectors.toList()));
    }

    default Map<String, Object> convertToEditableDoc(final Map<String, Object> source) {

        final Set<String> arrayFieldSet = getIndexAdminArrayFieldSet();
        final Set<String> dateFieldSet = getIndexAdminDateFieldSet();
        final Set<String> integerFieldSet = getIndexAdminIntegerFieldSet();
        final Set<String> longFieldSet = getIndexAdminLongFieldSet();
        final Set<String> floatFieldSet = getIndexAdminFloatFieldSet();
        final Set<String> doubleFieldSet = getIndexAdminDoubleFieldSet();

        return source.entrySet().stream().map(e -> {
            final String key = e.getKey();
            Object value = e.getValue();
            if (value == null) {
                value = StringUtil.EMPTY;
            }
            if (value instanceof String || value == null) {
                return new Pair<>(key, value);
            }
            if (arrayFieldSet.contains(key)) {
                if (value instanceof String[]) {
                    value = stream((String[]) value).get(stream -> stream.collect(Collectors.joining("\n")));
                } else if (value instanceof List) {
                    @SuppressWarnings("unchecked")
                    final List<String> list = (List<String>) value;
                    value = list.stream().collect(Collectors.joining("\n"));
                }
            } else if (dateFieldSet.contains(key)) {
                value = FessFunctions.formatDate((Date) value);
            } else if (integerFieldSet.contains(key) || longFieldSet.contains(key) || floatFieldSet.contains(key)
                    || doubleFieldSet.contains(key)) {
                value = value.toString();
            }
            return new Pair<>(key, value);
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    default Map<String, Object> convertToStorableDoc(final Map<String, Object> source) {

        final Set<String> arrayFieldSet = getIndexAdminArrayFieldSet();
        final Set<String> dateFieldSet = getIndexAdminDateFieldSet();
        final Set<String> integerFieldSet = getIndexAdminIntegerFieldSet();
        final Set<String> longFieldSet = getIndexAdminLongFieldSet();
        final Set<String> floatFieldSet = getIndexAdminFloatFieldSet();
        final Set<String> doubleFieldSet = getIndexAdminDoubleFieldSet();

        return source.entrySet().stream().filter(e -> isNonEmptyValue(e.getValue())).map(e -> {
            final String key = e.getKey();
            Object value = e.getValue();
            if (arrayFieldSet.contains(key)) {
                value = split(value.toString(), "\n")
                        .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toList()));
            } else if (dateFieldSet.contains(key)) {
                // TODO time zone
                value = FessFunctions.parseDate(value.toString());
            } else if (integerFieldSet.contains(key)) {
                value = DfTypeUtil.toInteger(value.toString());
            } else if (longFieldSet.contains(key)) {
                value = DfTypeUtil.toLong(value.toString());
            } else if (floatFieldSet.contains(key)) {
                value = DfTypeUtil.toFloat(value.toString());
            } else if (doubleFieldSet.contains(key)) {
                value = DfTypeUtil.toDouble(value.toString());
            }
            return new Pair<>(key, value);
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    String getIndexAdminRequiredFields();

    default boolean validateIndexRequiredFields(final Map<String, Object> source) {
        return invalidIndexRequiredFields(source).isEmpty();
    }

    default List<String> invalidIndexRequiredFields(final Map<String, Object> source) {
        final RequiredValidator requiredValidator = new RequiredValidator();
        return split(getIndexAdminRequiredFields(), ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim)
                .filter(s -> !requiredValidator.isValid(source.get(s), null)).collect(Collectors.toList()));
    }

    default boolean isNonEmptyValue(final Object value) {
        final RequiredValidator requiredValidator = new RequiredValidator();
        return requiredValidator.isValid(value, null);
    }

    String getCrawlerDocumentSpaceChars();

    default int[] getCrawlerDocumentSpaceCharsAsArray() {
        return getCrawlerDocumentCharsAsArray(CRAWLER_DOCUMENT_SPACE_CHARS, getCrawlerDocumentSpaceChars());
    }

    default String[] getCrawlerDocumentSpaces() {
        String[] spaces = (String[]) propMap.get(CRAWLER_DOCUMENT_SPACES);
        if (spaces == null) {
            spaces = Arrays.stream(getCrawlerDocumentSpaceCharsAsArray()).mapToObj(Character::toString).toArray(n -> new String[n]);
            propMap.put(CRAWLER_DOCUMENT_SPACES, spaces);
        }
        return spaces;
    }

    default int[] getCrawlerDocumentCharsAsArray(final String key, final String spaceStr) {
        int[] spaceChars = (int[]) propMap.get(key);
        if (spaceChars == null) {
            if (spaceStr.startsWith("u")) {
                spaceChars = split(spaceStr, "u")
                        .get(stream -> stream.filter(StringUtil::isNotBlank).mapToInt(s -> Integer.parseInt(s, 16)).toArray());
            } else {
                // backward compatibility
                final int length = spaceStr.length();
                spaceChars = new int[length];
                for (int i = 0; i < length; i++) {
                    spaceChars[i] = spaceStr.codePointAt(i);
                }
            }
            propMap.put(key, spaceChars);
        }
        return spaceChars;
    }

    String getCrawlerDocumentFullstopChars();

    default boolean endsWithFullstop(final String s) {
        if (StringUtil.isBlank(s)) {
            return false;
        }
        for (final int i : getCrawlerDocumentFullstopCharsAsArray()) {
            if (s.endsWith(String.valueOf(i))) {
                return true;
            }
        }
        return false;
    }

    default int[] getCrawlerDocumentFullstopCharsAsArray() {
        return getCrawlerDocumentCharsAsArray(CRAWLER_DOCUMENT_FULLSTOP_CHARS, getCrawlerDocumentFullstopChars());
    }

    String getQueryAdditionalResponseFields();

    default String[] getQueryAdditionalResponseFields(final String... fields) {
        final List<String> list = new ArrayList<>(fields.length + 10);
        stream(fields).of(stream -> stream.forEach(list::add));
        split(getQueryAdditionalResponseFields(), ",")
                .of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(list::add));
        return list.toArray(new String[list.size()]);
    }

    String getQueryAdditionalScrollResponseFields();

    default String[] getQueryAdditionalScrollResponseFields(final String... fields) {
        final List<String> list = new ArrayList<>(fields.length + 10);
        stream(fields).of(stream -> stream.forEach(list::add));
        split(getQueryAdditionalScrollResponseFields(), ",")
                .of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(list::add));
        return list.toArray(new String[list.size()]);
    }

    String getQueryAdditionalCacheResponseFields();

    default String[] getQueryAdditionalCacheResponseFields(final String... fields) {
        final List<String> list = new ArrayList<>(fields.length + 10);
        stream(fields).of(stream -> stream.forEach(list::add));
        split(getQueryAdditionalCacheResponseFields(), ",")
                .of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(list::add));
        return list.toArray(new String[list.size()]);
    }

    String getQueryAdditionalHighlightedFields();

    default String[] getQueryAdditionalHighlightedFields(final String... fields) {
        final List<String> list = new ArrayList<>(fields.length + 10);
        stream(fields).of(stream -> stream.forEach(list::add));
        split(getQueryAdditionalHighlightedFields(), ",")
                .of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(list::add));
        return list.toArray(new String[list.size()]);
    }

    String getQueryAdditionalSearchFields();

    default String[] getQueryAdditionalSearchFields(final String... fields) {
        final List<String> list = new ArrayList<>(fields.length + 10);
        stream(fields).of(stream -> stream.forEach(list::add));
        split(getQueryAdditionalSearchFields(), ",")
                .of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(list::add));
        return list.toArray(new String[list.size()]);
    }

    String getQueryAdditionalFacetFields();

    default String[] getQueryAdditionalFacetFields(final String... fields) {
        final List<String> list = new ArrayList<>(fields.length + 10);
        stream(fields).of(stream -> stream.forEach(list::add));
        split(getQueryAdditionalFacetFields(), ",")
                .of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(list::add));
        return list.toArray(new String[list.size()]);
    }

    String getQueryAdditionalSortFields();

    default String[] getQueryAdditionalSortFields(final String... fields) {
        final List<String> list = new ArrayList<>(fields.length + 10);
        stream(fields).of(stream -> stream.forEach(list::add));
        split(getQueryAdditionalSortFields(), ",").of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(list::add));
        return list.toArray(new String[list.size()]);
    }

    String getQueryAdditionalApiResponseFields();

    default String[] getQueryAdditionalApiResponseFields(final String... fields) {
        final List<String> list = new ArrayList<>(fields.length + 10);
        stream(fields).of(stream -> stream.forEach(list::add));
        split(getQueryAdditionalApiResponseFields(), ",")
                .of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(list::add));
        return list.toArray(new String[list.size()]);
    }

    String getQueryAdditionalNotAnalyzedFields();

    default String[] getQueryAdditionalNotAnalyzedFields(final String... fields) {
        final List<String> list = new ArrayList<>(fields.length + 10);
        stream(fields).of(stream -> stream.forEach(list::add));
        split(getQueryAdditionalNotAnalyzedFields(), ",")
                .of(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).forEach(list::add));
        return list.toArray(new String[list.size()]);
    }

    String getThumbnailGeneratorTargets();

    default String[] getThumbnailGeneratorTargetsAsArray() {
        return getThumbnailGeneratorTargets().split(",");

    }

    String getApiAdminAccessPermissions();

    default Set<String> getApiAdminAccessPermissionSet() {
        @SuppressWarnings("unchecked")
        Set<String> fieldSet = (Set<String>) propMap.get(API_ADMIN_ACCESS_PERMISSION_SET);
        if (fieldSet == null) {
            fieldSet = split(getApiAdminAccessPermissions(), ",")
                    .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toSet()));
            propMap.put(API_ADMIN_ACCESS_PERMISSION_SET, fieldSet);
        }
        return fieldSet;
    }

    default boolean isApiAdminAccessAllowed(final Set<String> accessPermissions) {
        return getApiAdminAccessPermissionSet().stream().anyMatch(s -> accessPermissions.contains(s));
    }

    String getUserCodePattern();

    Integer getUserCodeMinLengthAsInteger();

    Integer getUserCodeMaxLengthAsInteger();

    default boolean isValidUserCode(final String userCode) {
        if (userCode == null) {
            return false;
        }

        final int length = userCode.length();
        if (getUserCodeMinLengthAsInteger().intValue() > length || getUserCodeMaxLengthAsInteger().intValue() < length) {
            return false;
        }

        Pattern pattern = (Pattern) propMap.get(USER_CODE_PATTERN);
        if (pattern == null) {
            pattern = Pattern.compile(getUserCodePattern());
            propMap.put(USER_CODE_PATTERN, pattern);
        }
        return pattern.matcher(userCode).matches();
    }

    String getQueryCollapseInnerHitsSorts();

    @SuppressWarnings("rawtypes")
    default OptionalThing<SortBuilder[]> getQueryCollapseInnerHitsSortBuilders() {
        @SuppressWarnings("unchecked")
        OptionalThing<SortBuilder[]> ot = (OptionalThing<SortBuilder[]>) propMap.get(QUERY_COLLAPSE_INNER_HITS_SORTS);
        if (ot == null) {
            final String sorts = getQueryCollapseInnerHitsSorts();
            if (StringUtil.isBlank(sorts)) {
                ot = OptionalThing.empty();
            } else {
                final SortBuilder[] sortBuilders = split(sorts, ",").get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> {
                    final String[] values = s.split(":");
                    if (values.length > 1) {
                        return SortBuilders.fieldSort(values[0]).order("desc".equalsIgnoreCase(values[0]) ? SortOrder.DESC : SortOrder.ASC);
                    }
                    return SortBuilders.fieldSort(values[0]).order(SortOrder.ASC);
                }).toArray(n -> new SortBuilder[n]));
                ot = OptionalThing.of(sortBuilders);
            }
            propMap.put(QUERY_COLLAPSE_INNER_HITS_SORTS, ot);
        }
        return ot;
    }

    String getVirtualHostHeaders();

    default String getVirtualHostHeaderValue() {
        final String value = getVirtualHostValue();
        if (StringUtil.isNotBlank(value)) {
            return value;
        }
        return getVirtualHostHeaders();
    }

    @SuppressWarnings("unchecked")
    default Tuple3<String, String, String>[] getVirtualHosts() {
        Tuple3<String, String, String>[] hosts = (Tuple3<String, String, String>[]) propMap.get(VIRTUAL_HOST_HEADERS);
        if (hosts == null) {
            hosts = split(getVirtualHostHeaderValue(), "\n").get(stream -> stream.map(s -> {
                final String[] v1 = s.split("=");
                if (v1.length == 2) {
                    final String[] v2 = v1[0].split(":", 2);
                    if (v2.length == 2) {
                        return new Tuple3<>(v2[0].trim(), v2[1].trim(), v1[1].replaceAll("[^a-zA-Z0-9_]", StringUtil.EMPTY).trim());
                    }
                }
                return null;
            }).filter(v -> {
                if (v == null) {
                    return false;
                }
                if ("admin".equalsIgnoreCase(v.getValue3()) || "common".equalsIgnoreCase(v.getValue3())
                        || "error".equalsIgnoreCase(v.getValue3()) || "login".equalsIgnoreCase(v.getValue3())
                        || "profile".equalsIgnoreCase(v.getValue3())) {
                    return false;
                }
                return true;
            }).toArray(n -> new Tuple3[n]));
            propMap.put(VIRTUAL_HOST_HEADERS, hosts);
        }
        return hosts;
    }

    String getCrawlerFailureUrlStatusCodes();

    default boolean isCrawlerFailureUrlStatusCodes(final int code) {
        int[] codes = (int[]) propMap.get(CRAWLER_FAILURE_URL_STATUS_CODES);
        if (codes == null) {
            codes = split(getCrawlerFailureUrlStatusCodes(), ",")
                    .get(stream -> stream.filter(StringUtil::isNotBlank).mapToInt(Integer::parseInt).toArray());
            propMap.put(CRAWLER_FAILURE_URL_STATUS_CODES, codes);
        }
        for (final int v : codes) {
            if (v == code) {
                return true;
            }
        }
        return false;
    }

    Integer getThumbnailHtmlImageMinWidthAsInteger();

    Integer getThumbnailHtmlImageMinHeightAsInteger();

    java.math.BigDecimal getThumbnailHtmlImageMaxAspectRatioAsDecimal();

    default boolean validateThumbnailSize(final int width, final int height) {
        if (width <= 0 || height <= 0 || width < getThumbnailHtmlImageMinWidthAsInteger().intValue()
                || height < getThumbnailHtmlImageMinHeightAsInteger().intValue()) {
            return false;
        }

        final float ratio = getThumbnailHtmlImageMaxAspectRatioAsDecimal().floatValue();
        if (((float) width) / ((float) height) > ratio || ((float) height) / ((float) width) > ratio) {
            return false;
        }

        return true;
    }

    String getHttpProxyHost();

    Integer getHttpProxyPortAsInteger();

    String getHttpProxyUsername();

    String getHttpProxyPassword();

    default Proxy getHttpProxy() {
        Proxy proxy = (Proxy) propMap.get(HTML_PROXY);
        if (proxy == null) {
            if (StringUtil.isNotBlank(getHttpProxyHost()) && getHttpProxyPortAsInteger() != null) {
                final SocketAddress addr = new InetSocketAddress(getHttpProxyHost(), getHttpProxyPortAsInteger());
                proxy = new Proxy(Type.HTTP, addr);
                if (StringUtil.isNotBlank(getHttpProxyUsername())) {
                    Authenticator.setDefault(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(getHttpProxyUsername(), getHttpProxyPassword().toCharArray());
                        }
                    });
                }
            } else {
                proxy = Proxy.NO_PROXY;
            }
            propMap.put(HTML_PROXY, proxy);
        }
        return proxy;
    }

    String getThumbnailHtmlImageExcludeExtensions();

    default boolean isThumbnailHtmlImageUrl(final String url) {
        if (StringUtil.isBlank(url)) {
            return false;
        }

        String[] excludeExtensions = (String[]) propMap.get(THUMBNAIL_HTML_IMAGE_EXCLUDE_EXTENSIONS);
        if (excludeExtensions == null) {
            excludeExtensions = split(getThumbnailHtmlImageExcludeExtensions(), ",").get(stream -> stream
                    .map(s -> s.toLowerCase(Locale.ROOT).trim()).filter(StringUtil::isNotBlank).toArray(n -> new String[n]));
            propMap.put(THUMBNAIL_HTML_IMAGE_EXCLUDE_EXTENSIONS, excludeExtensions);
        }

        final String u = url.toLowerCase(Locale.ROOT);
        return !stream(excludeExtensions).get(stream -> stream.anyMatch(s -> u.endsWith(s)));
    }

    String getQueryGsaResponseFields();

    default boolean isGsaResponseFields(final String name) {
        @SuppressWarnings("unchecked")
        Set<String> gsaResponseFieldSet = (Set<String>) propMap.get(QUERY_GSA_RESPONSE_FIELDS);
        if (gsaResponseFieldSet == null) {
            gsaResponseFieldSet = split(getQueryGsaResponseFields(), ",").get(stream -> stream.map(s -> s.toLowerCase(Locale.ROOT).trim())
                    .filter(StringUtil::isNotBlank).collect(Collectors.toSet()));
            propMap.put(QUERY_GSA_RESPONSE_FIELDS, gsaResponseFieldSet);
        }
        return gsaResponseFieldSet.contains(name.toLowerCase(Locale.ROOT));
    }

    String getApiSearchAcceptReferers();

    default boolean isAcceptedSearchReferer(final String referer) {
        Pattern[] patterns = (Pattern[]) propMap.get(API_SEARCH_ACCEPT_REFERERS);
        if (patterns == null) {
            final String refs = getApiSearchAcceptReferers();
            if (StringUtil.isBlank(refs)) {
                patterns = new Pattern[0];
            } else {
                patterns = split(refs, "\n").get(
                        stream -> stream.filter(StringUtil::isNotBlank).map(s -> Pattern.compile(s.trim())).toArray(n -> new Pattern[n]));
            }
            propMap.put(API_SEARCH_ACCEPT_REFERERS, patterns);
        }
        if (patterns.length == 0) {
            return true;
        }

        if (referer == null) {
            return false;
        }
        return Arrays.stream(patterns).anyMatch(p -> p.matcher(referer).matches());
    }

    String getQueryHighlightContentDescriptionFields();

    default String[] getQueryHighlightContentDescriptionFieldsAsArray() {
        return split(getQueryHighlightContentDescriptionFields(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).toArray(n -> new String[n]));
    }

    boolean isLdapIgnoreNetbiosName();

    default String getCanonicalLdapName(final String name) {
        if (isLdapIgnoreNetbiosName()) {
            final String[] values = name.split("\\\\");
            if (values.length == 0) {
                return null;
            }
            if (values.length == 1) {
                return values[0];
            }
            return String.join("\\", Arrays.copyOfRange(values, 1, values.length));
        }
        return name;
    }

    String getLoggingSearchDocsFields();

    default String[] getLoggingSearchDocsFieldsAsArray() {
        String[] fields = (String[]) propMap.get(LOGGING_SEARCH_DOCS_FIELDS);
        if (fields == null) {
            fields = split(getLoggingSearchDocsFields(), ",").get(stream -> stream.map(String::trim).toArray(n -> new String[n]));
            propMap.put(LOGGING_SEARCH_DOCS_FIELDS, fields);
        }
        return fields;
    }

    String getSchedulerTargetName();

    default boolean isSchedulerTarget(final String target) {
        if (StringUtil.isBlank(target)) {
            return true;
        }

        final String myName = getSchedulerTargetName();

        final String[] targets = target.split(",");
        for (String name : targets) {
            name = name.trim();
            if (Constants.DEFAULT_JOB_TARGET.equalsIgnoreCase(name) || StringUtil.isNotBlank(myName) && myName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    String getApiGsaResponseHeaders();

    default List<Pair<String, String>> getApiGsaResponseHeaderList() {
        @SuppressWarnings("unchecked")
        List<Pair<String, String>> list = (List<Pair<String, String>>) propMap.get(API_GSA_RESPONSE_HEADER_LIST);
        if (list == null) {
            list = split(getApiGsaResponseHeaders(), "\n").get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> {
                final String[] values = s.split(":", 2);
                if (values.length == 2) {
                    return new Pair<>(values[0], values[1]);
                }
                return new Pair<>(values[0], StringUtil.EMPTY);
            }).collect(Collectors.toList()));
            propMap.put(API_GSA_RESPONSE_HEADER_LIST, list);
        }
        return list;
    }

    String getApiJsonResponseHeaders();

    default List<Pair<String, String>> getApiJsonResponseHeaderList() {
        @SuppressWarnings("unchecked")
        List<Pair<String, String>> list = (List<Pair<String, String>>) propMap.get(API_JSON_RESPONSE_HEADER_LIST);
        if (list == null) {
            list = split(getApiJsonResponseHeaders(), "\n").get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> {
                final String[] values = s.split(":", 2);
                if (values.length == 2) {
                    return new Pair<>(values[0], values[1]);
                }
                return new Pair<>(values[0], StringUtil.EMPTY);
            }).collect(Collectors.toList()));
            propMap.put(API_JSON_RESPONSE_HEADER_LIST, list);
        }
        return list;
    }

    String getApiDashboardResponseHeaders();

    default List<Pair<String, String>> getApiDashboardResponseHeaderList() {
        @SuppressWarnings("unchecked")
        List<Pair<String, String>> list = (List<Pair<String, String>>) propMap.get(API_DASHBOARD_RESPONSE_HEADER_LIST);
        if (list == null) {
            list = split(getApiDashboardResponseHeaders(), "\n").get(stream -> stream.filter(StringUtil::isNotBlank).map(s -> {
                final String[] values = s.split(":", 2);
                if (values.length == 2) {
                    return new Pair<>(values[0], values[1]);
                }
                return new Pair<>(values[0], StringUtil.EMPTY);
            }).collect(Collectors.toList()));
            propMap.put(API_DASHBOARD_RESPONSE_HEADER_LIST, list);
        }
        return list;
    }

    String getApiCorsAllowOrigin();

    default List<String> getApiCorsAllowOriginList() {
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) propMap.get(CORS_ALLOW_ORIGIN);
        if (list == null) {
            list = split(getApiCorsAllowOrigin(), "\n")
                    .get(stream -> stream.map(String::trim).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
            propMap.put(CORS_ALLOW_ORIGIN, list);
        }
        return list;
    }

    String getIndexerLanguageFields();

    default String[] getIndexerLanguageFieldsAsArray() {
        return split(getIndexerLanguageFields(), ",").get(stream -> stream.map(String::trim).toArray(n -> new String[n]));

    }

    String getSessionTrackingModes();

    default Set<String> getSessionTrackingModesAsSet() {
        return split(getSessionTrackingModes(), ",")
                .get(stream -> stream.map(s -> s.trim().toUpperCase(Locale.ENGLISH)).collect(Collectors.toSet()));
    }

    String getQueryTrackTotalHits();

    default Object getQueryTrackTotalHitsValue() {
        Object value = propMap.get(QUERY_TRACK_TOTAL_HITS_VALUE);
        if (value == null) {
            final String v = getQueryTrackTotalHits();
            if (Constants.TRUE.equalsIgnoreCase(v)) {
                value = Boolean.TRUE;
            } else if (Constants.FALSE.equalsIgnoreCase(v)) {
                value = Boolean.FALSE;
            } else {
                try {
                    value = DfTypeUtil.toInteger(v);
                } catch (final NumberFormatException e) {
                    value = StringUtil.EMPTY;
                }
            }
            propMap.put(QUERY_TRACK_TOTAL_HITS_VALUE, value);
        }
        return value;
    }

    Integer getProcessorsAsInteger();

    default int availableProcessors() {
        final int num = getProcessorsAsInteger();
        if (num > 0) {
            return num;
        }
        return Runtime.getRuntime().availableProcessors();
    }

    Integer getCrawlerHttpThreadPoolSizeAsInteger();

    default int getCrawlerHttpProcessors() {
        final int num = getCrawlerHttpThreadPoolSizeAsInteger();
        if (num > 0) {
            return num;
        }
        return Runtime.getRuntime().availableProcessors();
    }

    String getPluginVersionFilter();

    default boolean isTargetPluginVersion(final String version) {
        final Pattern pattern;
        if (StringUtil.isBlank(getPluginVersionFilter())) {
            pattern = Pattern.compile("^" + Pattern.quote(ComponentUtil.getSystemHelper().getProductVersion()) + ".*");
        } else {
            pattern = Pattern.compile(getPluginVersionFilter());
        }
        return pattern.matcher(version).matches();
    }

    String getQueryHighlightBoundaryChars();

    default char[] getQueryHighlightBoundaryCharsAsArray() {
        final int[] values = getCrawlerDocumentCharsAsArray(QUERY_HIGHLIGHT_BOUNDARY_CHARS, getQueryHighlightBoundaryChars());
        final char[] chars = new char[values.length];
        for (int i = 0; i < values.length; i++) {
            chars[i] = (char) values[i];
        }
        return chars;
    }

    String getQueryHighlightTerminalChars();

    default int[] getQueryHighlightTerminalCharsAsArray() {
        return getCrawlerDocumentCharsAsArray(QUERY_HIGHLIGHT_TERMINAL_CHARS, getQueryHighlightTerminalChars());
    }

    String getHttpFileuploadMaxSize();

    default Long getHttpFileuploadMaxSizeAsLong() {
        final String value = getHttpFileuploadMaxSize();
        return value != null ? DfTypeUtil.toLong(value) : null;
    }

    String getHttpFileuploadThresholdSize();

    default Long getHttpFileuploadThresholdSizeAsLong() {
        final String value = getHttpFileuploadThresholdSize();
        return value != null ? DfTypeUtil.toLong(value) : null;
    }

    String getPasswordInvalidAdminPasswords();

    default boolean isValidAdminPassword(final String password) {
        return !split(getPasswordInvalidAdminPasswords(), "\n")
                .get(stream -> stream.map(String::trim).filter(StringUtil::isNotEmpty).anyMatch(s -> s.equals(password)));
    }

    String getSearchlogRequestHeaders();

    default String[] getSearchlogRequestHeadersAsArray() {
        return split(getSearchlogRequestHeaders(), ",")
                .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).toArray(n -> new String[n]));
    }

    String getApiPingSearchEngineFields();

    default Set<String> getApiPingEsFieldSet() {
        Set<String> value = (Set<String>) propMap.get(API_PING_SEARCH_ENGINE_FIELD_SET);
        if (value == null) {
            value = split(getApiPingSearchEngineFields(), ",")
                    .get(stream -> stream.filter(StringUtil::isNotBlank).map(String::trim).collect(Collectors.toSet()));
            propMap.put(API_PING_SEARCH_ENGINE_FIELD_SET, value);
        }
        return value;
    }

    String get(String propertyKey);

    String getSearchEngineType();

    default String getFesenType() {
        final String value = getSearchEngineType();
        if (value != null) {
            return value;
        }
        return get("elasticsearch.type");
    }

    String getSearchEngineHttpUrl();

    default String getFesenHttpUrl() {
        final String value = getSearchEngineHttpUrl();
        if (value != null) {
            return value;
        }
        return get("elasticsearch.http.url");
    }

    String getSearchEngineHttpSslCertificateAuthorities();

    default String getFesenHttpSslCertificateAuthorities() {
        final String value = getSearchEngineHttpSslCertificateAuthorities();
        if (value != null) {
            return value;
        }
        return get("elasticsearch.http.ssl.certificate_authorities");
    }

    String getSearchEngineUsername();

    default String getFesenUsername() {
        final String value = getSearchEngineUsername();
        if (value != null) {
            return value;
        }
        return get("elasticsearch.username");
    }

    String getSearchEnginePassword();

    default String getFesenPassword() {
        final String value = getSearchEnginePassword();
        if (value != null) {
            return value;
        }
        return get("elasticsearch.password");
    }

    Integer getAsInteger(String propertyKey);

    Integer getSearchEngineHeartbeatIntervalAsInteger();

    default long getFesenHeartbeatInterval() {
        Integer value = getSearchEngineHeartbeatIntervalAsInteger();
        if (value != null) {
            return value.longValue();
        }
        value = getAsInteger("elasticsearch.heartbeat_interval");
        if (value != null) {
            return value.longValue();
        }
        return 10000L;
    }
}
