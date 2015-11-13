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

import org.lastaflute.core.direction.exception.ConfigPropertyNotFoundException;

/**
 * @author FreeGen
 */
public interface FessConfig extends FessEnv {

    /** The key of the configuration. e.g. Fess */
    String DOMAIN_TITLE = "domain.title";

    /** The key of the configuration. e.g. false */
    String CRAWLER_DOCUMENT_CACHE_ENABLE = "crawler.document.cache.enable";

    /** The key of the configuration. e.g. favorite_count */
    String INDEX_FIELD_favorite_count = "index.field.favorite_count";

    /** The key of the configuration. e.g. click_count */
    String INDEX_FIELD_click_count = "index.field.click_count";

    /** The key of the configuration. e.g. config_id */
    String INDEX_FIELD_config_id = "index.field.config_id";

    /** The key of the configuration. e.g. expires */
    String INDEX_FIELD_EXPIRES = "index.field.expires";

    /** The key of the configuration. e.g. url */
    String INDEX_FIELD_URL = "index.field.url";

    /** The key of the configuration. e.g. doc_id */
    String INDEX_FIELD_doc_id = "index.field.doc_id";

    /** The key of the configuration. e.g. _id */
    String INDEX_FIELD_ID = "index.field.id";

    /** The key of the configuration. e.g. lang */
    String INDEX_FIELD_LANG = "index.field.lang";

    /** The key of the configuration. e.g. has_cache */
    String INDEX_FIELD_has_cache = "index.field.has_cache";

    /** The key of the configuration. e.g. last_modified */
    String INDEX_FIELD_last_modified = "index.field.last_modified";

    /** The key of the configuration. e.g. anchor */
    String INDEX_FIELD_ANCHOR = "index.field.anchor";

    /** The key of the configuration. e.g. segment */
    String INDEX_FIELD_SEGMENT = "index.field.segment";

    /** The key of the configuration. e.g. role */
    String INDEX_FIELD_ROLE = "index.field.role";

    /** The key of the configuration. e.g. boost */
    String INDEX_FIELD_BOOST = "index.field.boost";

    /** The key of the configuration. e.g. created */
    String INDEX_FIELD_CREATED = "index.field.created";

    /** The key of the configuration. e.g. label */
    String INDEX_FIELD_LABEL = "index.field.label";

    /** The key of the configuration. e.g. mimetype */
    String INDEX_FIELD_MIMETYPE = "index.field.mimetype";

    /** The key of the configuration. e.g. parent_id */
    String INDEX_FIELD_parent_id = "index.field.parent_id";

    /** The key of the configuration. e.g. content */
    String INDEX_FIELD_CONTENT = "index.field.content";

    /** The key of the configuration. e.g. cache */
    String INDEX_FIELD_CACHE = "index.field.cache";

    /** The key of the configuration. e.g. digest */
    String INDEX_FIELD_DIGEST = "index.field.digest";

    /** The key of the configuration. e.g. title */
    String INDEX_FIELD_TITLE = "index.field.title";

    /** The key of the configuration. e.g. host */
    String INDEX_FIELD_HOST = "index.field.host";

    /** The key of the configuration. e.g. site */
    String INDEX_FIELD_SITE = "index.field.site";

    /** The key of the configuration. e.g. content_length */
    String INDEX_FIELD_content_length = "index.field.content_length";

    /** The key of the configuration. e.g. filetype */
    String INDEX_FIELD_FILETYPE = "index.field.filetype";

    /** The key of the configuration. e.g. fess */
    String INDEX_DOCUMENT_INDEX = "index.document.index";

    /** The key of the configuration. e.g. doc */
    String INDEX_DOCUMENT_TYPE = "index.document.type";

    /** The key of the configuration. e.g. admin */
    String AUTHENTICATION_ADMIN_ROLES = "authentication.admin.roles";

    /** The key of the configuration. e.g. / */
    String COOKIE_DEFAULT_PATH = "cookie.default.path";

    /** The key of the configuration. e.g. 3600 */
    String COOKIE_DEFAULT_EXPIRE = "cookie.default.expire";

    /** The key of the configuration. e.g. 86400 */
    String COOKIE_ETERNAL_EXPIRE = "cookie.eternal.expire";

    /** The key of the configuration. e.g. FES */
    String COOKIE_REMEMBER_ME_HARBOR_KEY = "cookie.remember.me.harbor.key";

    /** The key of the configuration. e.g. 4 */
    String PAGING_PAGE_SIZE = "paging.page.size";

    /** The key of the configuration. e.g. 3 */
    String PAGING_PAGE_RANGE_SIZE = "paging.page.range.size";

    /** The key of the configuration. e.g. true */
    String PAGING_PAGE_RANGE_FILL_LIMIT = "paging.page.range.fill.limit";

    /** The key of the configuration. e.g. 100 */
    String FORM_ROLE_LIST_SIZE = "form.role.list.size";

    /** The key of the configuration. e.g. 100 */
    String FORM_GROUP_LIST_SIZE = "form.group.list.size";

    /** The key of the configuration. e.g. Administrator */
    String MAIL_FROM_NAME = "mail.from.name";

    /** The key of the configuration. e.g. root@localhost */
    String MAIL_FROM_ADDRESS = "mail.from.address";

    /**
     * Get the value of property as {@link String}.
     * @param propertyKey The key of the property. (NotNull)
     * @return The value of found property. (NotNull: if not found, exception)
     * @throws ConfigPropertyNotFoundException When the property is not found.
     */
    String get(String propertyKey);

    /**
     * Is the property true?
     * @param propertyKey The key of the property which is boolean type. (NotNull)
     * @return The determination, true or false. (if not found, exception)
     * @throws ConfigPropertyNotFoundException When the property is not found.
     */
    boolean is(String propertyKey);

    /**
     * Get the value for the key 'domain.title'. <br>
     * The value is, e.g. Fess <br>
     * comment: The title of domain the application for logging
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getDomainTitle();

    /**
     * Get the value for the key 'crawler.document.cache.enable'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentCacheEnable();

    /**
     * Is the property for the key 'crawler.document.cache.enable' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerDocumentCacheEnable();

    /**
     * Get the value for the key 'index.field.favorite_count'. <br>
     * The value is, e.g. favorite_count <br>
     * comment: field names
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldFavoriteCount();

    /**
     * Get the value for the key 'index.field.click_count'. <br>
     * The value is, e.g. click_count <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldClickCount();

    /**
     * Get the value for the key 'index.field.config_id'. <br>
     * The value is, e.g. config_id <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldConfigId();

    /**
     * Get the value for the key 'index.field.expires'. <br>
     * The value is, e.g. expires <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldExpires();

    /**
     * Get the value for the key 'index.field.url'. <br>
     * The value is, e.g. url <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldUrl();

    /**
     * Get the value for the key 'index.field.doc_id'. <br>
     * The value is, e.g. doc_id <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldDocId();

    /**
     * Get the value for the key 'index.field.id'. <br>
     * The value is, e.g. _id <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldId();

    /**
     * Get the value for the key 'index.field.lang'. <br>
     * The value is, e.g. lang <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldLang();

    /**
     * Get the value for the key 'index.field.has_cache'. <br>
     * The value is, e.g. has_cache <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldHasCache();

    /**
     * Get the value for the key 'index.field.last_modified'. <br>
     * The value is, e.g. last_modified <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldLastModified();

    /**
     * Get the value for the key 'index.field.anchor'. <br>
     * The value is, e.g. anchor <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldAnchor();

    /**
     * Get the value for the key 'index.field.segment'. <br>
     * The value is, e.g. segment <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldSegment();

    /**
     * Get the value for the key 'index.field.role'. <br>
     * The value is, e.g. role <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldRole();

    /**
     * Get the value for the key 'index.field.boost'. <br>
     * The value is, e.g. boost <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldBoost();

    /**
     * Get the value for the key 'index.field.created'. <br>
     * The value is, e.g. created <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldCreated();

    /**
     * Get the value for the key 'index.field.label'. <br>
     * The value is, e.g. label <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldLabel();

    /**
     * Get the value for the key 'index.field.mimetype'. <br>
     * The value is, e.g. mimetype <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldMimetype();

    /**
     * Get the value for the key 'index.field.parent_id'. <br>
     * The value is, e.g. parent_id <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldParentId();

    /**
     * Get the value for the key 'index.field.content'. <br>
     * The value is, e.g. content <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldContent();

    /**
     * Get the value for the key 'index.field.cache'. <br>
     * The value is, e.g. cache <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldCache();

    /**
     * Get the value for the key 'index.field.digest'. <br>
     * The value is, e.g. digest <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldDigest();

    /**
     * Get the value for the key 'index.field.title'. <br>
     * The value is, e.g. title <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldTitle();

    /**
     * Get the value for the key 'index.field.host'. <br>
     * The value is, e.g. host <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldHost();

    /**
     * Get the value for the key 'index.field.site'. <br>
     * The value is, e.g. site <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldSite();

    /**
     * Get the value for the key 'index.field.content_length'. <br>
     * The value is, e.g. content_length <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldContentLength();

    /**
     * Get the value for the key 'index.field.filetype'. <br>
     * The value is, e.g. filetype <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldFiletype();

    /**
     * Get the value for the key 'index.document.index'. <br>
     * The value is, e.g. fess <br>
     * comment: document index
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentIndex();

    /**
     * Get the value for the key 'index.document.type'. <br>
     * The value is, e.g. doc <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentType();

    /**
     * Get the value for the key 'authentication.admin.roles'. <br>
     * The value is, e.g. admin <br>
     * comment: ------
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAuthenticationAdminRoles();

    /**
     * Get the value for the key 'cookie.default.path'. <br>
     * The value is, e.g. / <br>
     * comment: The default path of cookie (basically '/' if no context path)
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCookieDefaultPath();

    /**
     * Get the value for the key 'cookie.default.expire'. <br>
     * The value is, e.g. 3600 <br>
     * comment: The default expire of cookie in seconds e.g. 31556926: one year, 86400: one day
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCookieDefaultExpire();

    /**
     * Get the value for the key 'cookie.default.expire' as {@link Integer}. <br>
     * The value is, e.g. 3600 <br>
     * comment: The default expire of cookie in seconds e.g. 31556926: one year, 86400: one day
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCookieDefaultExpireAsInteger();

    /**
     * Get the value for the key 'cookie.eternal.expire'. <br>
     * The value is, e.g. 86400 <br>
     * comment: The eternal expire of cookie in seconds e.g. 315360000: ten year, 86400: one day
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCookieEternalExpire();

    /**
     * Get the value for the key 'cookie.eternal.expire' as {@link Integer}. <br>
     * The value is, e.g. 86400 <br>
     * comment: The eternal expire of cookie in seconds e.g. 315360000: ten year, 86400: one day
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCookieEternalExpireAsInteger();

    /**
     * Get the value for the key 'cookie.remember.me.harbor.key'. <br>
     * The value is, e.g. FES <br>
     * comment: The cookie key of remember-me for Fess
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCookieRememberMeHarborKey();

    /**
     * Get the value for the key 'paging.page.size'. <br>
     * The value is, e.g. 4 <br>
     * comment: The size of one page for paging
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPagingPageSize();

    /**
     * Get the value for the key 'paging.page.size' as {@link Integer}. <br>
     * The value is, e.g. 4 <br>
     * comment: The size of one page for paging
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPagingPageSizeAsInteger();

    /**
     * Get the value for the key 'paging.page.range.size'. <br>
     * The value is, e.g. 3 <br>
     * comment: The size of page range for paging
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPagingPageRangeSize();

    /**
     * Get the value for the key 'paging.page.range.size' as {@link Integer}. <br>
     * The value is, e.g. 3 <br>
     * comment: The size of page range for paging
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPagingPageRangeSizeAsInteger();

    /**
     * Get the value for the key 'paging.page.range.fill.limit'. <br>
     * The value is, e.g. true <br>
     * comment: The option 'fillLimit' of page range for paging
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPagingPageRangeFillLimit();

    /**
     * Is the property for the key 'paging.page.range.fill.limit' true? <br>
     * The value is, e.g. true <br>
     * comment: The option 'fillLimit' of page range for paging
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isPagingPageRangeFillLimit();

    /**
     * Get the value for the key 'form.role.list.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getFormRoleListSize();

    /**
     * Get the value for the key 'form.role.list.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getFormRoleListSizeAsInteger();

    /**
     * Get the value for the key 'form.group.list.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getFormGroupListSize();

    /**
     * Get the value for the key 'form.group.list.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getFormGroupListSizeAsInteger();

    /**
     * Get the value for the key 'mail.from.name'. <br>
     * The value is, e.g. Administrator <br>
     * comment: From
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getMailFromName();

    /**
     * Get the value for the key 'mail.from.address'. <br>
     * The value is, e.g. root@localhost <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getMailFromAddress();

    /**
     * The simple implementation for configuration.
     * @author FreeGen
     */
    public static class SimpleImpl extends FessEnv.SimpleImpl implements FessConfig {

        /** The serial version UID for object serialization. (Default) */
        private static final long serialVersionUID = 1L;

        public String getDomainTitle() {
            return get(FessConfig.DOMAIN_TITLE);
        }

        public String getCrawlerDocumentCacheEnable() {
            return get(FessConfig.CRAWLER_DOCUMENT_CACHE_ENABLE);
        }

        public boolean isCrawlerDocumentCacheEnable() {
            return is(FessConfig.CRAWLER_DOCUMENT_CACHE_ENABLE);
        }

        public String getIndexFieldFavoriteCount() {
            return get(FessConfig.INDEX_FIELD_favorite_count);
        }

        public String getIndexFieldClickCount() {
            return get(FessConfig.INDEX_FIELD_click_count);
        }

        public String getIndexFieldConfigId() {
            return get(FessConfig.INDEX_FIELD_config_id);
        }

        public String getIndexFieldExpires() {
            return get(FessConfig.INDEX_FIELD_EXPIRES);
        }

        public String getIndexFieldUrl() {
            return get(FessConfig.INDEX_FIELD_URL);
        }

        public String getIndexFieldDocId() {
            return get(FessConfig.INDEX_FIELD_doc_id);
        }

        public String getIndexFieldId() {
            return get(FessConfig.INDEX_FIELD_ID);
        }

        public String getIndexFieldLang() {
            return get(FessConfig.INDEX_FIELD_LANG);
        }

        public String getIndexFieldHasCache() {
            return get(FessConfig.INDEX_FIELD_has_cache);
        }

        public String getIndexFieldLastModified() {
            return get(FessConfig.INDEX_FIELD_last_modified);
        }

        public String getIndexFieldAnchor() {
            return get(FessConfig.INDEX_FIELD_ANCHOR);
        }

        public String getIndexFieldSegment() {
            return get(FessConfig.INDEX_FIELD_SEGMENT);
        }

        public String getIndexFieldRole() {
            return get(FessConfig.INDEX_FIELD_ROLE);
        }

        public String getIndexFieldBoost() {
            return get(FessConfig.INDEX_FIELD_BOOST);
        }

        public String getIndexFieldCreated() {
            return get(FessConfig.INDEX_FIELD_CREATED);
        }

        public String getIndexFieldLabel() {
            return get(FessConfig.INDEX_FIELD_LABEL);
        }

        public String getIndexFieldMimetype() {
            return get(FessConfig.INDEX_FIELD_MIMETYPE);
        }

        public String getIndexFieldParentId() {
            return get(FessConfig.INDEX_FIELD_parent_id);
        }

        public String getIndexFieldContent() {
            return get(FessConfig.INDEX_FIELD_CONTENT);
        }

        public String getIndexFieldCache() {
            return get(FessConfig.INDEX_FIELD_CACHE);
        }

        public String getIndexFieldDigest() {
            return get(FessConfig.INDEX_FIELD_DIGEST);
        }

        public String getIndexFieldTitle() {
            return get(FessConfig.INDEX_FIELD_TITLE);
        }

        public String getIndexFieldHost() {
            return get(FessConfig.INDEX_FIELD_HOST);
        }

        public String getIndexFieldSite() {
            return get(FessConfig.INDEX_FIELD_SITE);
        }

        public String getIndexFieldContentLength() {
            return get(FessConfig.INDEX_FIELD_content_length);
        }

        public String getIndexFieldFiletype() {
            return get(FessConfig.INDEX_FIELD_FILETYPE);
        }

        public String getIndexDocumentIndex() {
            return get(FessConfig.INDEX_DOCUMENT_INDEX);
        }

        public String getIndexDocumentType() {
            return get(FessConfig.INDEX_DOCUMENT_TYPE);
        }

        public String getAuthenticationAdminRoles() {
            return get(FessConfig.AUTHENTICATION_ADMIN_ROLES);
        }

        public String getCookieDefaultPath() {
            return get(FessConfig.COOKIE_DEFAULT_PATH);
        }

        public String getCookieDefaultExpire() {
            return get(FessConfig.COOKIE_DEFAULT_EXPIRE);
        }

        public Integer getCookieDefaultExpireAsInteger() {
            return getAsInteger(FessConfig.COOKIE_DEFAULT_EXPIRE);
        }

        public String getCookieEternalExpire() {
            return get(FessConfig.COOKIE_ETERNAL_EXPIRE);
        }

        public Integer getCookieEternalExpireAsInteger() {
            return getAsInteger(FessConfig.COOKIE_ETERNAL_EXPIRE);
        }

        public String getCookieRememberMeHarborKey() {
            return get(FessConfig.COOKIE_REMEMBER_ME_HARBOR_KEY);
        }

        public String getPagingPageSize() {
            return get(FessConfig.PAGING_PAGE_SIZE);
        }

        public Integer getPagingPageSizeAsInteger() {
            return getAsInteger(FessConfig.PAGING_PAGE_SIZE);
        }

        public String getPagingPageRangeSize() {
            return get(FessConfig.PAGING_PAGE_RANGE_SIZE);
        }

        public Integer getPagingPageRangeSizeAsInteger() {
            return getAsInteger(FessConfig.PAGING_PAGE_RANGE_SIZE);
        }

        public String getPagingPageRangeFillLimit() {
            return get(FessConfig.PAGING_PAGE_RANGE_FILL_LIMIT);
        }

        public boolean isPagingPageRangeFillLimit() {
            return is(FessConfig.PAGING_PAGE_RANGE_FILL_LIMIT);
        }

        public String getFormRoleListSize() {
            return get(FessConfig.FORM_ROLE_LIST_SIZE);
        }

        public Integer getFormRoleListSizeAsInteger() {
            return getAsInteger(FessConfig.FORM_ROLE_LIST_SIZE);
        }

        public String getFormGroupListSize() {
            return get(FessConfig.FORM_GROUP_LIST_SIZE);
        }

        public Integer getFormGroupListSizeAsInteger() {
            return getAsInteger(FessConfig.FORM_GROUP_LIST_SIZE);
        }

        public String getMailFromName() {
            return get(FessConfig.MAIL_FROM_NAME);
        }

        public String getMailFromAddress() {
            return get(FessConfig.MAIL_FROM_ADDRESS);
        }
    }
}
