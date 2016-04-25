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
package org.codelibs.fess.mylasta.direction;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.servlet.http.HttpSession;

import org.codelibs.core.exception.ClassNotFoundRuntimeException;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.Constants;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;
import org.dbflute.optional.OptionalThing;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.lastaflute.job.LaJob;
import org.lastaflute.job.subsidiary.ConcurrentExec;
import org.lastaflute.web.util.LaRequestUtil;

public interface FessProp {

    public static final String GROUP_VALUE_PREFIX = "group:";

    public static final String ROLE_VALUE_PREFIX = "role:";

    public static final String DEFAULT_SORT_VALUES = "defaultSortValues";

    public static final String DEFAULT_LABEL_VALUES = "defaultLabelValues";

    public static final String QUERY_LANGUAGE_MAPPING = "queryLanguageMapping";

    public static final String CRAWLER_METADATA_NAME_MAPPING = "crawlerMetadataNameMapping";

    public static final String CRAWLER_METADATA_CONTENT_EXCLUDES = "crawlerMetadataContentExcludes";

    public static final Map<String, Object> propMap = new ConcurrentHashMap<>();

    //
    // system.properties
    //

    public default void storeSystemProperties() {
        ComponentUtil.getSystemProperties().store();
    }

    public default String getSystemProperty(final String key) {
        return ComponentUtil.getSystemProperties().getProperty(key);
    }

    public default String getSystemProperty(final String key, final String defaultValue) {
        return ComponentUtil.getSystemProperties().getProperty(key, defaultValue);
    }

    public default void setSystemProperty(final String key, final String value) {
        if (value != null) {
            ComponentUtil.getSystemProperties().setProperty(key, value);
        } else {
            ComponentUtil.getSystemProperties().remove(key);
        }
    }

    public default boolean getSystemPropertyAsBoolean(final String key, final boolean defaultValue) {
        return Constants.TRUE.equalsIgnoreCase(getSystemProperty(key, defaultValue ? Constants.TRUE : Constants.FALSE));
    }

    public default void setSystemPropertyAsBoolean(final String key, final boolean value) {
        setSystemProperty(key, value ? Constants.TRUE : Constants.FALSE);
    }

    public default int getSystemPropertyAsInt(final String key, final int defaultValue) {
        final String value = getSystemProperty(key);
        if (value != null) {
            try {
                return Integer.valueOf(value);
            } catch (final NumberFormatException e) {
                // ignore
            }
        }
        return defaultValue;
    }

    public default void setSystemPropertyAsInt(final String key, final int value) {
        setSystemProperty(key, Integer.toString(value));
    }

    public default String[] getDefaultSortValues(final OptionalThing<FessUserBean> userBean) {
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) propMap.get(DEFAULT_SORT_VALUES);
        if (map == null) {
            String value = getSystemProperty(Constants.DEFAULT_SORT_VALUE_PROPERTY);
            if (StringUtil.isBlank(value)) {
                map = Collections.emptyMap();
            } else {
                final Set<String> keySet = new HashSet<>();
                map = StreamUtil.of(value.split("\n")).filter(s -> StringUtil.isNotBlank(s)).map(s -> {
                    final String[] pair = s.split("=");
                    if (pair.length == 1) {
                        return new Pair<>(StringUtil.EMPTY, pair[0].trim());
                    } else if (pair.length == 2) {
                        String sortValue = pair[1].trim();
                        if (StringUtil.isBlank(sortValue) || "score".equals(sortValue)) {
                            sortValue = "score.desc";
                        }
                        return new Pair<>(pair[0].trim(), sortValue);
                    }
                    return null;
                }).filter(o -> o != null && keySet.add(o.getFirst())).collect(Collectors.toMap(Pair::getFirst, d -> d.getSecond()));
            }
            propMap.put(DEFAULT_SORT_VALUES, map);
        }
        return map
                .entrySet()
                .stream()
                .map(e -> {
                    final String key = e.getKey();
                    if (StringUtil.isEmpty(key)) {
                        return e.getValue();
                    }
                    if (userBean.map(
                            user -> StreamUtil.of(user.getRoles()).anyMatch(s -> key.equals(ROLE_VALUE_PREFIX + s))
                                    || StreamUtil.of(user.getGroups()).anyMatch(s -> key.equals(GROUP_VALUE_PREFIX + s))).orElse(false)) {
                        return e.getValue();
                    }
                    return null;
                }).filter(s -> StringUtil.isNotBlank(s)).toArray(n -> new String[n]);
    }

    public default void setDefaultSortValue(final String value) {
        setSystemProperty(Constants.DEFAULT_SORT_VALUE_PROPERTY, value);
        propMap.remove(DEFAULT_SORT_VALUES);
    }

    public default String getDefaultSortValue() {
        return getSystemProperty(Constants.DEFAULT_SORT_VALUE_PROPERTY, StringUtil.EMPTY);
    }

    public default String[] getDefaultLabelValues(final OptionalThing<FessUserBean> userBean) {
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) propMap.get(DEFAULT_LABEL_VALUES);
        if (map == null) {
            String value = getSystemProperty(Constants.DEFAULT_LABEL_VALUE_PROPERTY);
            if (StringUtil.isBlank(value)) {
                map = Collections.emptyMap();
            } else {
                final Set<String> keySet = new HashSet<>();
                map = StreamUtil.of(value.split("\n")).filter(s -> StringUtil.isNotBlank(s)).map(s -> {
                    final String[] pair = s.split("=");
                    if (pair.length == 1) {
                        return new Pair<>(StringUtil.EMPTY, pair[0].trim());
                    } else if (pair.length == 2) {
                        return new Pair<>(pair[0].trim(), pair[1].trim());
                    }
                    return null;
                }).filter(o -> o != null && keySet.add(o.getFirst())).collect(Collectors.toMap(Pair::getFirst, d -> d.getSecond()));
            }
            propMap.put(DEFAULT_LABEL_VALUES, map);
        }
        return map
                .entrySet()
                .stream()
                .map(e -> {
                    final String key = e.getKey();
                    if (StringUtil.isEmpty(key)) {
                        return e.getValue();
                    }
                    if (userBean.map(
                            user -> StreamUtil.of(user.getRoles()).anyMatch(s -> key.equals(ROLE_VALUE_PREFIX + s))
                                    || StreamUtil.of(user.getGroups()).anyMatch(s -> key.equals(GROUP_VALUE_PREFIX + s))).orElse(false)) {
                        return e.getValue();
                    }
                    return null;
                }).filter(s -> StringUtil.isNotBlank(s)).toArray(n -> new String[n]);
    }

    public default void setDefaultLabelValue(final String value) {
        setSystemProperty(Constants.DEFAULT_LABEL_VALUE_PROPERTY, value);
        propMap.remove(DEFAULT_LABEL_VALUES);
    }

    public default String getDefaultLabelValue() {
        return getSystemProperty(Constants.DEFAULT_LABEL_VALUE_PROPERTY, StringUtil.EMPTY);
    }

    public default void setLoginRequired(final boolean value) {
        setSystemPropertyAsBoolean(Constants.LOGIN_REQUIRED_PROPERTY, value);
    }

    public default boolean isLoginRequired() {
        return getSystemPropertyAsBoolean(Constants.LOGIN_REQUIRED_PROPERTY, false);
    }

    public default void setIncrementalCrawling(final boolean value) {
        setSystemPropertyAsBoolean(Constants.INCREMENTAL_CRAWLING_PROPERTY, value);
    }

    public default boolean isIncrementalCrawling() {
        return getSystemPropertyAsBoolean(Constants.INCREMENTAL_CRAWLING_PROPERTY, true);
    }

    public default void setDayForCleanup(final int value) {
        setSystemPropertyAsInt(Constants.DAY_FOR_CLEANUP_PROPERTY, value);
    }

    public default int getDayForCleanup() {
        return getSystemPropertyAsInt(Constants.DAY_FOR_CLEANUP_PROPERTY, Constants.DEFAULT_DAY_FOR_CLEANUP);
    }

    public default void setCrawlingThreadCount(final int value) {
        setSystemPropertyAsInt(Constants.CRAWLING_THREAD_COUNT_PROPERTY, value);
    }

    public default int getCrawlingThreadCount() {
        return getSystemPropertyAsInt(Constants.CRAWLING_THREAD_COUNT_PROPERTY, 5);
    }

    public default void setSearchLog(final boolean value) {
        setSystemPropertyAsBoolean(Constants.SEARCH_LOG_PROPERTY, value);
    }

    public default boolean isSearchLog() {
        return getSystemPropertyAsBoolean(Constants.SEARCH_LOG_PROPERTY, false);
    }

    public default void setUserInfo(final boolean value) {
        setSystemPropertyAsBoolean(Constants.USER_INFO_PROPERTY, value);
    }

    public default boolean isUserInfo() {
        return getSystemPropertyAsBoolean(Constants.USER_INFO_PROPERTY, false);
    }

    public default void setUserFavorite(final boolean value) {
        setSystemPropertyAsBoolean(Constants.USER_FAVORITE_PROPERTY, value);
    }

    public default boolean isUserFavorite() {
        return getSystemPropertyAsBoolean(Constants.USER_FAVORITE_PROPERTY, false);
    }

    public default void setWebApiJson(final boolean value) {
        setSystemPropertyAsBoolean(Constants.WEB_API_JSON_PROPERTY, value);
    }

    public default boolean isWebApiJson() {
        return getSystemPropertyAsBoolean(Constants.WEB_API_JSON_PROPERTY, false);
    }

    public default boolean isWebApiGsa() {
        return getSystemPropertyAsBoolean(Constants.WEB_API_GSA_PROPERTY, false);
    }

    public default void setAppendQueryParameter(final boolean value) {
        setSystemPropertyAsBoolean(Constants.APPEND_QUERY_PARAMETER_PROPERTY, value);
    }

    public default boolean isAppendQueryParameter() {
        return getSystemPropertyAsBoolean(Constants.APPEND_QUERY_PARAMETER_PROPERTY, false);
    }

    public default void setIgnoreFailureType(final String value) {
        setSystemProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY, value);
    }

    public default String getIgnoreFailureType() {
        return getSystemProperty(Constants.IGNORE_FAILURE_TYPE_PROPERTY, Constants.DEFAULT_IGNORE_FAILURE_TYPE);
    }

    public default void setFailureCountThreshold(final int value) {
        setSystemPropertyAsInt(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY, value);
    }

    public default int getFailureCountThreshold() {
        return getSystemPropertyAsInt(Constants.FAILURE_COUNT_THRESHOLD_PROPERTY, Constants.DEFAULT_FAILURE_COUNT);
    }

    public default void setWebApiPopularWord(final boolean value) {
        setSystemPropertyAsBoolean(Constants.WEB_API_POPULAR_WORD_PROPERTY, value);
    }

    public default boolean isWebApiPopularWord() {
        return getSystemPropertyAsBoolean(Constants.WEB_API_POPULAR_WORD_PROPERTY, true);
    }

    public default void setCsvFileEncoding(final String value) {
        setSystemProperty(Constants.CSV_FILE_ENCODING_PROPERTY, value);
    }

    public default String getCsvFileEncoding() {
        return getSystemProperty(Constants.CSV_FILE_ENCODING_PROPERTY, Constants.UTF_8);
    }

    public default void setPurgeSearchLogDay(final int value) {
        setSystemPropertyAsInt(Constants.PURGE_SEARCH_LOG_DAY_PROPERTY, value);
    }

    public default int getPurgeSearchLogDay() {
        return getSystemPropertyAsInt(Constants.PURGE_SEARCH_LOG_DAY_PROPERTY, Integer.parseInt(Constants.DEFAULT_PURGE_DAY));
    }

    public default void setPurgeJobLogDay(final int value) {
        setSystemPropertyAsInt(Constants.PURGE_JOB_LOG_DAY_PROPERTY, value);
    }

    public default int getPurgeJobLogDay() {
        return getSystemPropertyAsInt(Constants.PURGE_JOB_LOG_DAY_PROPERTY, Integer.parseInt(Constants.DEFAULT_PURGE_DAY));
    }

    public default void setPurgeUserInfoDay(final int value) {
        setSystemPropertyAsInt(Constants.PURGE_USER_INFO_DAY_PROPERTY, value);
    }

    public default int getPurgeUserInfoDay() {
        return getSystemPropertyAsInt(Constants.PURGE_USER_INFO_DAY_PROPERTY, Integer.parseInt(Constants.DEFAULT_PURGE_DAY));
    }

    public default void setPurgeByBots(final String value) {
        setSystemProperty(Constants.PURGE_BY_BOTS_PROPERTY, value);
    }

    public default String getPurgeByBots() {
        return getSystemProperty(Constants.PURGE_BY_BOTS_PROPERTY, Constants.DEFAULT_PURGE_BY_BOTS);
    }

    public default void setNotificationTo(final String value) {
        setSystemProperty(Constants.NOTIFICATION_TO_PROPERTY, value);
    }

    public default String getNotificationTo() {
        return getSystemProperty(Constants.NOTIFICATION_TO_PROPERTY, StringUtil.EMPTY);
    }

    public default void setSuggestSearchLog(final boolean value) {
        setSystemPropertyAsBoolean(Constants.SUGGEST_SEARCH_LOG_PROPERTY, value);
    }

    public default boolean isSuggestSearchLog() {
        return getSystemPropertyAsBoolean(Constants.SUGGEST_SEARCH_LOG_PROPERTY, true);
    }

    public default void setSuggestDocuments(final boolean value) {
        setSystemPropertyAsBoolean(Constants.SUGGEST_DOCUMENTS_PROPERTY, value);
    }

    public default boolean isSuggestDocuments() {
        return getSystemPropertyAsBoolean(Constants.SUGGEST_DOCUMENTS_PROPERTY, true);
    }

    public default void setPurgeSuggestSearchLogDay(final int value) {
        setSystemPropertyAsInt(Constants.PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY, value);
    }

    public default int getPurgeSuggestSearchLogDay() {
        return getSystemPropertyAsInt(Constants.PURGE_SUGGEST_SEARCH_LOG_DAY_PROPERTY,
                Integer.parseInt(Constants.DEFAULT_SUGGEST_PURGE_DAY));
    }

    public default void setLdapInitialContextFactory(final String value) {
        setSystemProperty(Constants.LDAP_INITIAL_CONTEXT_FACTORY, value);
    }

    public default String getLdapInitialContextFactory() {
        return getSystemProperty(Constants.LDAP_INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    }

    public default void setLdapSecurityAuthentication(final String value) {
        setSystemProperty(Constants.LDAP_SECURITY_AUTHENTICATION, value);
    }

    public default String getLdapSecurityAuthentication() {
        return getSystemProperty(Constants.LDAP_SECURITY_AUTHENTICATION, "simple");
    }

    public default void setLdapProviderUrl(final String value) {
        setSystemProperty(Constants.LDAP_PROVIDER_URL, value);
    }

    public default String getLdapProviderUrl() {
        return getSystemProperty(Constants.LDAP_PROVIDER_URL);
    }

    public default void setLdapSecurityPrincipal(final String value) {
        setSystemProperty(Constants.LDAP_SECURITY_PRINCIPAL, value);
    }

    Integer getLdapMaxUsernameLengthAsInteger();

    public default String getLdapSecurityPrincipal(final String username) {
        final String value;
        final int maxLength = getLdapMaxUsernameLengthAsInteger().intValue();
        if (username == null) {
            value = StringUtil.EMPTY;
        } else if (maxLength >= 0 && username.length() > maxLength) {
            value = username.substring(0, maxLength);
        } else {
            value = username;
        }
        return String.format(getSystemProperty(Constants.LDAP_SECURITY_PRINCIPAL, StringUtil.EMPTY), value);
    }

    public default String getLdapSecurityPrincipal() {
        return getSystemProperty(Constants.LDAP_SECURITY_PRINCIPAL);
    }

    public default void setLdapBaseDn(final String value) {
        setSystemProperty(Constants.LDAP_BASE_DN, value);
    }

    public default String getLdapBaseDn() {
        return getSystemProperty(Constants.LDAP_BASE_DN);
    }

    public default void setLdapAccountFilter(final String value) {
        setSystemProperty(Constants.LDAP_ACCOUNT_FILTER, value);
    }

    public default String getLdapAccountFilter() {
        return getSystemProperty(Constants.LDAP_ACCOUNT_FILTER);
    }

    public default void setNotificationLogin(final String value) {
        setSystemProperty(Constants.NOTIFICATION_LOGIN, value);
    }

    public default String getNotificationLogin() {
        return getSystemProperty(Constants.NOTIFICATION_LOGIN, StringUtil.EMPTY);
    }

    public default void setNotificationSearchTop(final String value) {
        setSystemProperty(Constants.NOTIFICATION_SEARCH_TOP, value);
    }

    public default String getNotificationSearchTop() {
        return getSystemProperty(Constants.NOTIFICATION_SEARCH_TOP, StringUtil.EMPTY);
    }

    //
    // fess_*.properties
    //

    String getAuthenticationAdminRoles();

    public default String[] getAuthenticationAdminRolesAsArray() {
        return getAuthenticationAdminRoles().split(",");
    }

    String getJvmCrawlerOptions();

    public default String[] getJvmCrawlerOptionsAsArray() {
        return getJvmCrawlerOptions().split("\n");
    }

    String getJvmSuggestOptions();

    public default String[] getJvmSuggestOptionsAsArray() {
        return getJvmSuggestOptions().split("\n");
    }

    String getCrawlerDocumentHtmlPrunedTags();

    public default String[] getCrawlerDocumentHtmlPrunedTagsAsArray() {
        return getCrawlerDocumentHtmlPrunedTags().split(",");
    }

    String getCrawlerDocumentCacheHtmlMimetypes();

    public default boolean isHtmlMimetypeForCache(final String mimetype) {
        final String[] mimetypes = getCrawlerDocumentCacheHtmlMimetypes().split(",");
        if (mimetypes.length == 1 && StringUtil.isBlank(mimetypes[0])) {
            return true;
        }
        return StreamUtil.of(mimetypes).anyMatch(s -> s.equalsIgnoreCase(mimetype));
    }

    String getCrawlerDocumentCacheSupportedMimetypes();

    public default boolean isSupportedDocumentCacheMimetypes(final String mimetype) {
        final String[] mimetypes = getCrawlerDocumentCacheSupportedMimetypes().split(",");
        if (mimetypes.length == 1 && StringUtil.isBlank(mimetypes[0])) {
            return true;
        }
        return StreamUtil.of(mimetypes).anyMatch(s -> s.equalsIgnoreCase(mimetype));
    }

    String getIndexerClickCountEnabled();

    public default boolean getIndexerClickCountEnabledAsBoolean() {
        return Constants.TRUE.equalsIgnoreCase(getIndexerClickCountEnabled());
    }

    String getIndexerFavoriteCountEnabled();

    public default boolean getIndexerFavoriteCountEnabledAsBoolean() {
        return Constants.TRUE.equalsIgnoreCase(getIndexerFavoriteCountEnabled());
    }

    String getIndexerThreadDumpEnabled();

    public default boolean getIndexerThreadDumpEnabledAsBoolean() {
        return Constants.TRUE.equalsIgnoreCase(getIndexerThreadDumpEnabled());
    }

    String getIndexBackupTargets();

    public default String[] getIndexBackupTargetsAsArray() {
        return getIndexBackupTargets().split(",");
    }

    String getJobSystemJobIds();

    public default boolean isSystemJobId(final String id) {
        if (StringUtil.isBlank(getJobSystemJobIds())) {
            return false;
        }
        return StreamUtil.of(getJobSystemJobIds().split(",")).anyMatch(s -> s.equals(id));
    }

    String getSmbAvailableSidTypes();

    public default boolean isAvailableSmbSidType(final int sidType) {
        if (StringUtil.isBlank(getSmbAvailableSidTypes())) {
            return false;
        }
        final String value = Integer.toString(sidType);
        return StreamUtil.of(getSmbAvailableSidTypes().split(",")).anyMatch(s -> {
            return s.equals(value);
        });
    }

    String getSupportedLanguages();

    public default String[] getSupportedLanguagesAsArray() {
        return StreamUtil.of(getSupportedLanguages().split(",")).filter(s -> StringUtil.isNotBlank(s)).toArray(n -> new String[n]);
    }

    String getOnlineHelpSupportedLangs();

    public default boolean isOnlineHelpSupportedLang(final String lang) {
        if (StringUtil.isBlank(getOnlineHelpSupportedLangs())) {
            return false;
        }
        return StreamUtil.of(getOnlineHelpSupportedLangs().split(",")).filter(s -> StringUtil.isNotBlank(s)).anyMatch(s -> s.equals(lang));
    }

    String getSupportedUploadedJsExtentions();

    public default String[] getSupportedUploadedJsExtentionsAsArray() {
        return StreamUtil.of(getSupportedUploadedJsExtentions().split(",")).filter(s -> StringUtil.isNotBlank(s))
                .toArray(n -> new String[n]);
    }

    String getSupportedUploadedCssExtentions();

    public default String[] getSupportedUploadedCssExtentionsAsArray() {
        return StreamUtil.of(getSupportedUploadedCssExtentions().split(",")).filter(s -> StringUtil.isNotBlank(s))
                .toArray(n -> new String[n]);
    }

    String getSupportedUploadedMediaExtentions();

    public default String[] getSupportedUploadedMediaExtentionsAsArray() {
        return StreamUtil.of(getSupportedUploadedMediaExtentions().split(",")).filter(s -> StringUtil.isNotBlank(s))
                .toArray(n -> new String[n]);
    }

    String getJobTemplateTitleWeb();

    String getJobTemplateTitleFile();

    String getJobTemplateTitleData();

    public default String getJobTemplateTitle(final String type) {
        if (Constants.WEB_CRAWLER_TYPE.equals(type)) {
            return getJobTemplateTitleWeb();
        } else if (Constants.FILE_CRAWLER_TYPE.equals(type)) {
            return getJobTemplateTitleFile();
        } else if (Constants.DATA_CRAWLER_TYPE.equals(type)) {
            return getJobTemplateTitleData();
        }
        return "None";
    }

    String getSchedulerJobClass();

    public default Class<? extends LaJob> getSchedulerJobClassAsClass() {
        try {
            @SuppressWarnings("unchecked")
            final Class<? extends LaJob> clazz = (Class<? extends LaJob>) Class.forName(getSchedulerJobClass());
            return clazz;
        } catch (final ClassNotFoundException e) {
            throw new ClassNotFoundRuntimeException(e);
        }
    }

    String getSchedulerConcurrentExecMode();

    public default ConcurrentExec getSchedulerConcurrentExecModeAsEnum() {
        return ConcurrentExec.valueOf(getSchedulerConcurrentExecMode());
    }

    String getCrawlerMetadataContentExcludes();

    public default boolean isCrawlerMetadataContentIncluded(final String name) {
        Pattern[] patterns = (Pattern[]) propMap.get(CRAWLER_METADATA_CONTENT_EXCLUDES);
        if (patterns == null) {
            patterns =
                    StreamUtil.of(getCrawlerMetadataContentExcludes().split(",")).filter(v -> StringUtil.isNotBlank(v))
                            .map(v -> Pattern.compile(v)).toArray(n -> new Pattern[n]);
            propMap.put(CRAWLER_METADATA_CONTENT_EXCLUDES, patterns);
        }
        return !StreamUtil.of(patterns).anyMatch(p -> p.matcher(name).matches());
    }

    String getCrawlerMetadataNameMapping();

    public default Pair<String, String> getCrawlerMetadataNameMapping(final String name) {
        @SuppressWarnings("unchecked")
        Map<String, Pair<String, String>> params = (Map<String, Pair<String, String>>) propMap.get(CRAWLER_METADATA_NAME_MAPPING);
        if (params == null) {
            params = StreamUtil.of(getCrawlerMetadataNameMapping().split("\n")).filter(v -> StringUtil.isNotBlank(v)).map(v -> {
                final String[] values = v.split("=");
                if (values.length == 2) {
                    final String[] subValues = values[1].split(":");
                    if (subValues.length == 2) {
                        return new Tuple3<String, String, String>(values[0], subValues[0], subValues[1]);
                    } else {
                        return new Tuple3<String, String, String>(values[0], values[1], Constants.MAPPING_TYPE_ARRAY);
                    }
                }
                return null;
            }).collect(Collectors.toMap(Tuple3::getValue1, d -> new Pair<>(d.getValue2(), d.getValue3())));
            propMap.put(CRAWLER_METADATA_NAME_MAPPING, params);
        }
        return params.get(name);
    }

    String getSuggestPopularWordFields();

    public default String[] getSuggestPopularWordFieldsAsArray() {
        return StreamUtil.of(getSuggestPopularWordFields().split("\n")).filter(s -> StringUtil.isNotBlank(s)).toArray(n -> new String[n]);
    }

    String getSuggestPopularWordTags();

    public default String[] getSuggestPopularWordTagsAsArray() {
        return StreamUtil.of(getSuggestPopularWordTags().split("\n")).filter(s -> StringUtil.isNotBlank(s)).toArray(n -> new String[n]);
    }

    String getSuggestPopularWordExcludes();

    public default String[] getSuggestPopularWordExcludesAsArray() {
        return StreamUtil.of(getSuggestPopularWordExcludes().split("\n")).filter(s -> StringUtil.isNotBlank(s)).toArray(n -> new String[n]);
    }

    String getQueryReplaceTermWithPrefixQuery();

    public default boolean getQueryReplaceTermWithPrefixQueryAsBoolean() {
        return Boolean.valueOf(getQueryReplaceTermWithPrefixQuery());
    }

    String getQueryDefaultLanguages();

    String getQueryLanguageMapping();

    public default String[] getQueryLanguages(final Enumeration<Locale> locales, final String[] requestLangs) {
        if (StringUtil.isNotBlank(getQueryDefaultLanguages())) {
            String[] langs = (String[]) propMap.get("queryDefaultLanguages");
            if (langs == null) {
                langs = StreamUtil.of(getQueryDefaultLanguages().split(",")).map(s -> s.trim()).toArray(n -> new String[n]);
                propMap.put("queryDefaultLanguages", langs);

            }
            return langs;
        }

        if (requestLangs != null && requestLangs.length != 0) {
            return requestLangs;
        }

        if (locales == null) {
            return StringUtil.EMPTY_STRINGS;
        }

        @SuppressWarnings("unchecked")
        Map<String, String> params = (Map<String, String>) propMap.get(QUERY_LANGUAGE_MAPPING);
        if (params == null) {
            params = StreamUtil.of(getQueryLanguageMapping().split("\n")).filter(v -> StringUtil.isNotBlank(v)).map(v -> {
                final String[] values = v.split("=");
                if (values.length == 2) {
                    return new Pair<String, String>(values[0], values[1]);
                }
                return null;
            }).collect(Collectors.toMap(Pair::getFirst, d -> d.getSecond()));
            propMap.put(QUERY_LANGUAGE_MAPPING, params);
        }

        final Map<String, String> mapping = params;
        return Collections.list(locales).stream().map(locale -> {
            final String language = locale.getLanguage();
            final String country = locale.getCountry();
            if (StringUtil.isNotBlank(language)) {
                if (StringUtil.isNotBlank(country)) {
                    final String lang = language.toLowerCase(Locale.ROOT) + "-" + country.toLowerCase(Locale.ROOT);
                    if (mapping.containsKey(lang)) {
                        return mapping.get(lang);
                    }
                }
                if (mapping.containsKey(language)) {
                    return mapping.get(language);
                }
            }
            return null;
        }).filter(l -> l != null).distinct().toArray(n -> new String[n]);
    }

    String getSupportedUploadedFiles();

    public default boolean isSupportedUploadedFile(final String name) {
        return StreamUtil.of(getSuggestPopularWordExcludes().split(",")).filter(s -> StringUtil.isNotBlank(s))
                .anyMatch(s -> s.equals(name));
    }

    String getLdapAdminUserObjectClasses();

    public default Attribute getLdapAdminUserObjectClassAttribute() {
        final Attribute oc = new BasicAttribute("objectClass");
        StreamUtil.of(getLdapAdminUserObjectClasses().split(",")).filter(s -> StringUtil.isNotBlank(s)).forEach(s -> oc.add(s.trim()));
        return oc;
    }

    String getLdapAdminUserFilter();

    public default String getLdapAdminUserFilter(final String name) {
        return String.format(getLdapAdminUserFilter(), name);
    }

    String getLdapAdminUserBaseDn();

    public default String getLdapAdminUserSecurityPrincipal(final String name) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append(String.format(getLdapAdminUserFilter(), name));
        if (StringUtil.isNotBlank(getLdapAdminUserBaseDn())) {
            buf.append(',').append(getLdapAdminUserBaseDn());
        }
        return buf.toString();
    }

    String getLdapAdminRoleObjectClasses();

    public default Attribute getLdapAdminRoleObjectClassAttribute() {
        final Attribute oc = new BasicAttribute("objectClass");
        StreamUtil.of(getLdapAdminRoleObjectClasses().split(",")).filter(s -> StringUtil.isNotBlank(s)).forEach(s -> oc.add(s.trim()));
        return oc;
    }

    String getLdapAdminRoleFilter();

    public default String getLdapAdminRoleFilter(final String name) {
        return String.format(getLdapAdminRoleFilter(), name);
    }

    String getLdapAdminRoleBaseDn();

    public default String getLdapAdminRoleSecurityPrincipal(final String name) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append(String.format(getLdapAdminRoleFilter(), name));
        if (StringUtil.isNotBlank(getLdapAdminRoleBaseDn())) {
            buf.append(',').append(getLdapAdminRoleBaseDn());
        }
        return buf.toString();
    }

    String getLdapAdminGroupObjectClasses();

    public default Attribute getLdapAdminGroupObjectClassAttribute() {
        final Attribute oc = new BasicAttribute("objectClass");
        StreamUtil.of(getLdapAdminGroupObjectClasses().split(",")).filter(s -> StringUtil.isNotBlank(s)).forEach(s -> oc.add(s.trim()));
        return oc;
    }

    String getLdapAdminGroupFilter();

    public default String getLdapAdminGroupFilter(final String name) {
        return String.format(getLdapAdminGroupFilter(), name);
    }

    String getLdapAdminGroupBaseDn();

    public default String getLdapAdminGroupSecurityPrincipal(final String name) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append(String.format(getLdapAdminGroupFilter(), name));
        if (StringUtil.isNotBlank(getLdapAdminGroupBaseDn())) {
            buf.append(',').append(getLdapAdminGroupBaseDn());
        }
        return buf.toString();
    }

    String getAuthenticationAdminUsers();

    public default boolean isAdminUser(final String username) {
        return StreamUtil.of(getAuthenticationAdminUsers().split(",")).anyMatch(s -> s.equals(username));
    }

    boolean isLdapAdminEnabled();

    public default boolean isLdapAdminEnabled(final String username) {
        if (isAdminUser(username)) {
            return false;
        }
        return isLdapAdminEnabled();
    }

    String getCrawlerWebProtocols();

    public default String[] getCrawlerWebProtocolsAsArray() {
        return StreamUtil.of(getCrawlerWebProtocols().split(",")).filter(s -> StringUtil.isNotBlank(s)).map(s -> s.trim() + ":")
                .toArray(n -> new String[n]);
    }

    public default boolean isValidCrawlerWebProtocol(final String url) {
        return StreamUtil.of(getCrawlerWebProtocolsAsArray()).anyMatch(s -> url.startsWith(s));
    }

    String getCrawlerFileProtocols();

    public default String[] getCrawlerFileProtocolsAsArray() {
        return StreamUtil.of(getCrawlerFileProtocols().split(",")).filter(s -> StringUtil.isNotBlank(s)).map(s -> s.trim() + ":")
                .toArray(n -> new String[n]);
    }

    public default boolean isValidCrawlerFileProtocol(final String url) {
        return StreamUtil.of(getCrawlerFileProtocolsAsArray()).anyMatch(s -> url.startsWith(s));
    }

    public default void processSearchPreference(SearchRequestBuilder searchRequestBuilder, OptionalThing<FessUserBean> userBean) {
        userBean.map(user -> {
            if (user.hasRoles(getAuthenticationAdminRolesAsArray())) {
                return Constants.SEARCH_PREFERENCE_PRIMARY;
            }
            return user.getUserId();
        }).ifPresent(p -> searchRequestBuilder.setPreference(p)).orElse(() -> LaRequestUtil.getOptionalRequest().map(r -> {
            HttpSession session = r.getSession(false);
            if (session != null) {
                return session.getId();
            }
            final String preference = r.getParameter("preference");
            if (preference != null) {
                return Integer.toString(preference.hashCode());
            }
            return null;
        }).ifPresent(p -> searchRequestBuilder.setPreference(p)));
    }

}
