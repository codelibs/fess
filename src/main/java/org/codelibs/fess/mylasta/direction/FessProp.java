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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.codelibs.core.exception.ClassNotFoundRuntimeException;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.Constants;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;
import org.lastaflute.job.LaJob;
import org.lastaflute.job.subsidiary.ConcurrentExec;

public interface FessProp {

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
        ComponentUtil.getSystemProperties().setProperty(key, value);
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

    public default void setLoginRequired(final boolean value) {
        setSystemPropertyAsBoolean(Constants.LOGIN_REQUIRED_PROPERTY, value);
    }

    public default boolean isLoginRequired() {
        return getSystemPropertyAsBoolean(Constants.LOGIN_REQUIRED_PROPERTY, false);
    }

    public default void setWebApiPopularWord(final boolean value) {
        setSystemPropertyAsBoolean(Constants.WEB_API_POPULAR_WORD_PROPERTY, value);
    }

    public default boolean isWebApiPopularWord() {
        return getSystemPropertyAsBoolean(Constants.WEB_API_POPULAR_WORD_PROPERTY, true);
    }

    public default String getLdapInitialContextFactory() {
        return getSystemProperty(Constants.LDAP_INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    }

    public default void setLdapInitialContextFactory(final String value) {
        setSystemProperty(Constants.LDAP_INITIAL_CONTEXT_FACTORY, value);
    }

    public default String getLdapSecurityAuthentication() {
        return getSystemProperty(Constants.LDAP_SECURITY_AUTHENTICATION, "simple");
    }

    public default void setLdapSecurityAuthentication(final String value) {
        setSystemProperty(Constants.LDAP_SECURITY_AUTHENTICATION, value);
    }

    public default String getLdapProviderUrl() {
        return getSystemProperty(Constants.LDAP_PROVIDER_URL);
    }

    public default void setLdapProviderUrl(final String value) {
        setSystemProperty(Constants.LDAP_PROVIDER_URL, value);
    }

    public default String getLdapSecurityPrincipal(final String username) {
        return String.format(getSystemProperty(Constants.LDAP_SECURITY_PRINCIPAL, StringUtil.EMPTY), username);
    }

    public default String getLdapSecurityPrincipal() {
        return getSystemProperty(Constants.LDAP_SECURITY_PRINCIPAL);
    }

    public default void setLdapSecurityPrincipal(final String value) {
        setSystemProperty(Constants.LDAP_SECURITY_PRINCIPAL, value);
    }

    public default String getLdapBaseDn() {
        return getSystemProperty(Constants.LDAP_BASE_DN);
    }

    public default void setLdapBaseDn(final String value) {
        setSystemProperty(Constants.LDAP_BASE_DN, value);
    }

    public default String getLdapAccountFilter() {
        return getSystemProperty(Constants.LDAP_ACCOUNT_FILTER);
    }

    public default void setLdapAccountFilter(final String value) {
        setSystemProperty(Constants.LDAP_ACCOUNT_FILTER, value);
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

    public default boolean isSystemJobId(String id) {
        if (StringUtil.isBlank(getJobSystemJobIds())) {
            return false;
        }
        return StreamUtil.of(getJobSystemJobIds().split(",")).anyMatch(s -> s.equals(id));
    }

    String getSmbAvailableSidTypes();

    public default boolean isAvailableSmbSidType(int sidType) {
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

    public default boolean isOnlineHelpSupportedLang(String lang) {
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

    public default String getJobTemplateTitle(String type) {
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
            Class<? extends LaJob> clazz = (Class<? extends LaJob>) Class.forName(getSchedulerJobClass());
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundRuntimeException(e);
        }
    }

    String getSchedulerConcurrentExecMode();

    public default ConcurrentExec getSchedulerConcurrentExecModeAsEnum() {
        return ConcurrentExec.valueOf(getSchedulerConcurrentExecMode());
    }

    String getCrawlerMetadataContentExcludes();

    public default boolean isCrawlerMetadataContentIncluded(String name) {
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

    public default Pair<String, String> getCrawlerMetadataNameMapping(String name) {
        @SuppressWarnings("unchecked")
        Map<String, Pair<String, String>> params = (Map<String, Pair<String, String>>) propMap.get(CRAWLER_METADATA_NAME_MAPPING);
        if (params == null) {
            params = StreamUtil.of(getCrawlerMetadataNameMapping().split("\n")).filter(v -> StringUtil.isNotBlank(v)).map(v -> {
                String[] values = v.split("=");
                if (values.length == 2) {
                    String[] subValues = values[1].split(":");
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
}
