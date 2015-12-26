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
package org.codelibs.fess.mylasta.direction;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.StreamUtil;

public interface FessProp {
    public default String getProperty(String key) {
        return ComponentUtil.getCrawlerProperties().getProperty(key);
    }

    public default String getProperty(String key, String defaultValue) {
        return ComponentUtil.getCrawlerProperties().getProperty(key, defaultValue);
    }

    public default void setLoginRequired(boolean value) {
        ComponentUtil.getCrawlerProperties().setProperty(Constants.LOGIN_REQUIRED_PROPERTY, value ? Constants.TRUE : Constants.FALSE);
    }

    public default boolean isLoginRequired() {
        return Constants.TRUE.equalsIgnoreCase(ComponentUtil.getCrawlerProperties().getProperty(Constants.LOGIN_REQUIRED_PROPERTY,
                Constants.FALSE));
    }

    public default String getLdapInitialContextFactory() {
        return getProperty(Constants.LDAP_INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    }

    public default String getLdapSecurityAuthentication() {
        return getProperty(Constants.LDAP_SECURITY_AUTHENTICATION, "simple");
    }

    public default String getLdapProviderUrl() {
        return getProperty(Constants.LDAP_PROVIDER_URL);
    }

    public default String getLdapSecurityPrincipal(String username) {
        return String.format(getProperty(Constants.LDAP_SECURITY_PRINCIPAL, StringUtil.EMPTY), username);
    }

    public default String getLdapBaseDn() {
        return getProperty(Constants.LDAP_BASE_DN);
    }

    public default String getLdapAccountFilter() {
        return getProperty(Constants.LDAP_ACCOUNT_FILTER);
    }

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

    public default boolean isHtmlMimetypeForCache(String mimetype) {
        String[] mimetypes = getCrawlerDocumentCacheHtmlMimetypes().split(",");
        if (mimetypes.length == 1 && StringUtil.isBlank(mimetypes[0])) {
            return true;
        }
        return StreamUtil.of(mimetypes).anyMatch(s -> s.equalsIgnoreCase(mimetype));
    }

    String getCrawlerDocumentCacheSupportedMimetypes();

    public default boolean isSupportedDocumentCacheMimetypes(String mimetype) {
        String[] mimetypes = getCrawlerDocumentCacheSupportedMimetypes().split(",");
        if (mimetypes.length == 1 && StringUtil.isBlank(mimetypes[0])) {
            return true;
        }
        return StreamUtil.of(mimetypes).anyMatch(s -> s.equalsIgnoreCase(mimetype));
    }

}
