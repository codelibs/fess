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

    /** The key of the configuration. e.g. elasticsearch */
    String ELASTICSEARCH_CLUSTER_NAME = "elasticsearch.cluster.name";

    /** The key of the configuration. e.g. http://localhost:9201 */
    String ELASTICSEARCH_HTTP_URL = "elasticsearch.http.url";

    /** The key of the configuration. e.g. aes */
    String APP_CIPHER_ALGORISM = "app.cipher.algorism";

    /** The key of the configuration. e.g. __change_me__ */
    String APP_CIPHER_KEY = "app.cipher.key";

    /** The key of the configuration. e.g. sha256 */
    String APP_DIGEST_ALGORISM = "app.digest.algorism";

    /** The key of the configuration. e.g. -Djava.awt.headless=true
    -server
    -Xmx256m
    -XX:MaxMetaspaceSize=128m
    -XX:CompressedClassSpaceSize=32m
    -XX:-UseGCOverheadLimit
    -XX:+UseConcMarkSweepGC
    -XX:CMSInitiatingOccupancyFraction=75
    -XX:+UseParNewGC
    -XX:+UseTLAB
    -XX:+DisableExplicitGC */
    String JVM_CRAWLER_OPTIONS = "jvm.crawler.options";

    /** The key of the configuration. e.g. -Djava.awt.headless=true
    -server
    -Xmx256m
    -XX:MaxMetaspaceSize=128m
    -XX:CompressedClassSpaceSize=32m
    -XX:-UseGCOverheadLimit
    -XX:+UseConcMarkSweepGC
    -XX:CMSInitiatingOccupancyFraction=75
    -XX:+UseParNewGC
    -XX:+UseTLAB
    -XX:+DisableExplicitGC */
    String JVM_SUGGEST_OPTIONS = "jvm.suggest.options";

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

    /** The key of the configuration. e.g. timestamp */
    String INDEX_FIELD_TIMESTAMP = "index.field.timestamp";

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
    String INDEX_DOCUMENT_SEARCH_INDEX = "index.document.search.index";

    /** The key of the configuration. e.g. fess */
    String INDEX_DOCUMENT_UPDATE_INDEX = "index.document.update.index";

    /** The key of the configuration. e.g. doc */
    String INDEX_DOCUMENT_TYPE = "index.document.type";

    /** The key of the configuration. e.g. 1000 */
    String QUERY_MAX_LENGTH = "query.max.length";

    /** The key of the configuration. e.g. 1.6 */
    String QUERY_BOOST_TITLE = "query.boost.title";

    /** The key of the configuration. e.g. 2.0 */
    String QUERY_BOOST_TITLE_LANG = "query.boost.title.lang";

    /** The key of the configuration. e.g. 1.0 */
    String QUERY_BOOST_CONTENT = "query.boost.content";

    /** The key of the configuration. e.g. 1.3 */
    String QUERY_BOOST_CONTENT_LANG = "query.boost.content.lang";

    /** The key of the configuration. e.g. true */
    String ACL_AS_ROLE = "acl.as.role";

    /** The key of the configuration. e.g. admin */
    String AUTHENTICATION_ADMIN_ROLES = "authentication.admin.roles";

    /** The key of the configuration. e.g. guest */
    String SEARCH_DEFAULT_ROLES = "search.default.roles";

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

    /** The key of the configuration. e.g. 1000 */
    String PAGE_DOCBOOST_MAX_FETCH_SIZE = "page.docboost.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_KEYMATCH_MAX_FETCH_SIZE = "page.keymatch.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_LABELTYPE_MAX_FETCH_SIZE = "page.labeltype.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_ROLETYPE_MAX_FETCH_SIZE = "page.roletype.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_ROLE_MAX_FETCH_SIZE = "page.role.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_GROUP_MAX_FETCH_SIZE = "page.group.max.fetch.size";

    /** The key of the configuration. e.g. 0 */
    String PAGING_SEARCH_PAGE_START = "paging.search.page.start";

    /** The key of the configuration. e.g. 20 */
    String PAGING_SEARCH_PAGE_SIZE = "paging.search.page.size";

    /** The key of the configuration. e.g. 100 */
    String PAGING_SEARCH_PAGE_MAX_SIZE = "paging.search.page.max.size";

    /** The key of the configuration. e.g. Administrator */
    String MAIL_FROM_NAME = "mail.from.name";

    /** The key of the configuration. e.g. root@localhost */
    String MAIL_FROM_ADDRESS = "mail.from.address";

    /** The key of the configuration. e.g. http://fess.codelibs.org/{lang}/{version}/admin/ */
    String ONLINE_HELP_BASE_LINK = "online.help.base.link";

    /** The key of the configuration. e.g. failureurl */
    String ONLINE_HELP_NAME_FAILUREURL = "online.help.name.failureurl";

    /** The key of the configuration. e.g. elevateword */
    String ONLINE_HELP_NAME_ELEVATEWORD = "online.help.name.elevateword";

    /** The key of the configuration. e.g. reqheader */
    String ONLINE_HELP_NAME_REQHEADER = "online.help.name.reqheader";

    /** The key of the configuration. e.g. roletype */
    String ONLINE_HELP_NAME_ROLETYPE = "online.help.name.roletype";

    /** The key of the configuration. e.g. synonym */
    String ONLINE_HELP_NAME_DICT_SYNONYM = "online.help.name.dict.synonym";

    /** The key of the configuration. e.g. dict */
    String ONLINE_HELP_NAME_DICT = "online.help.name.dict";

    /** The key of the configuration. e.g. kuromoji */
    String ONLINE_HELP_NAME_DICT_KUROMOJI = "online.help.name.dict.kuromoji";

    /** The key of the configuration. e.g. webconfig */
    String ONLINE_HELP_NAME_WEBCONFIG = "online.help.name.webconfig";

    /** The key of the configuration. e.g. searchlist */
    String ONLINE_HELP_NAME_SEARCHLIST = "online.help.name.searchlist";

    /** The key of the configuration. e.g. log */
    String ONLINE_HELP_NAME_LOG = "online.help.name.log";

    /** The key of the configuration. e.g. general */
    String ONLINE_HELP_NAME_GENERAL = "online.help.name.general";

    /** The key of the configuration. e.g. role */
    String ONLINE_HELP_NAME_ROLE = "online.help.name.role";

    /** The key of the configuration. e.g. joblog */
    String ONLINE_HELP_NAME_JOBLOG = "online.help.name.joblog";

    /** The key of the configuration. e.g. keymatch */
    String ONLINE_HELP_NAME_KEYMATCH = "online.help.name.keymatch";

    /** The key of the configuration. e.g. wizard */
    String ONLINE_HELP_NAME_WIZARD = "online.help.name.wizard";

    /** The key of the configuration. e.g. badword */
    String ONLINE_HELP_NAME_BADWORD = "online.help.name.badword";

    /** The key of the configuration. e.g. pathmap */
    String ONLINE_HELP_NAME_PATHMAP = "online.help.name.pathmap";

    /** The key of the configuration. e.g. boostdoc */
    String ONLINE_HELP_NAME_BOOSTDOC = "online.help.name.boostdoc";

    /** The key of the configuration. e.g. dataconfig */
    String ONLINE_HELP_NAME_DATACONFIG = "online.help.name.dataconfig";

    /** The key of the configuration. e.g. systeminfo */
    String ONLINE_HELP_NAME_SYSTEMINFO = "online.help.name.systeminfo";

    /** The key of the configuration. e.g. user */
    String ONLINE_HELP_NAME_USER = "online.help.name.user";

    /** The key of the configuration. e.g. group */
    String ONLINE_HELP_NAME_GROUP = "online.help.name.group";

    /** The key of the configuration. e.g. design */
    String ONLINE_HELP_NAME_DESIGN = "online.help.name.design";

    /** The key of the configuration. e.g. dashboard */
    String ONLINE_HELP_NAME_DASHBOARD = "online.help.name.dashboard";

    /** The key of the configuration. e.g. webauth */
    String ONLINE_HELP_NAME_WEBAUTH = "online.help.name.webauth";

    /** The key of the configuration. e.g. fileconfig */
    String ONLINE_HELP_NAME_FILECONFIG = "online.help.name.fileconfig";

    /** The key of the configuration. e.g. fileauth */
    String ONLINE_HELP_NAME_FILEAUTH = "online.help.name.fileauth";

    /** The key of the configuration. e.g. labeltype */
    String ONLINE_HELP_NAME_LABELTYPE = "online.help.name.labeltype";

    /** The key of the configuration. e.g. duplicatehost */
    String ONLINE_HELP_NAME_DUPLICATEHOST = "online.help.name.duplicatehost";

    /** The key of the configuration. e.g. scheduler */
    String ONLINE_HELP_NAME_SCHEDULER = "online.help.name.scheduler";

    /** The key of the configuration. e.g. crawlinginfo */
    String ONLINE_HELP_NAME_CRAWLINGINFO = "online.help.name.crawlinginfo";

    /** The key of the configuration. e.g. 0 */
    String SUGGEST_POPULAR_WORD_SEED = "suggest.popular.word.seed";

    /** The key of the configuration. e.g. 10 */
    String SUGGEST_POPULAR_WORD_SIZE = "suggest.popular.word.size";

    /** The key of the configuration. e.g. 30 */
    String SUGGEST_POPULAR_WORD_WINDOW_SIZE = "suggest.popular.word.window.size";

    /** The key of the configuration. e.g. 1 */
    String SUGGEST_MIN_HIT_COUNT = "suggest.min.hit.count";

    /** The key of the configuration. e.g. _default */
    String SUGGEST_FIELD_CONTENTS = "suggest.field.contents";

    /** The key of the configuration. e.g. label */
    String SUGGEST_FIELD_TAGS = "suggest.field.tags";

    /** The key of the configuration. e.g. role */
    String SUGGEST_FIELD_ROLES = "suggest.field.roles";

    /** The key of the configuration. e.g. content,title */
    String SUGGEST_FIELD_INDEX_CONTENTS = "suggest.field.index.contents";

    /** The key of the configuration. e.g. 1 */
    String SUGGEST_UPDATE_REQUEST_INTERVAL = "suggest.update.request.interval";

    /** The key of the configuration. e.g. 1 */
    String SUGGEST_SOURCE_READER_SCROLL_SIZE = "suggest.source.reader.scroll.size";

    /** The key of the configuration. e.g. 1000 */
    String SUGGEST_POPULAR_WORD_CACHE_SIZE = "suggest.popular.word.cache.size";

    /** The key of the configuration. e.g. 60 */
    String SUGGEST_POPULAR_WORD_CACHE_EXPIRE = "suggest.popular.word.cache.expire";

    /** The key of the configuration. e.g. guest */
    String SUGGEST_ROLE_FILTERS = "suggest.role.filters";

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
     * Get the value for the key 'elasticsearch.cluster.name'. <br>
     * The value is, e.g. elasticsearch <br>
     * comment: Elasticsearch
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getElasticsearchClusterName();

    /**
     * Get the value for the key 'elasticsearch.http.url'. <br>
     * The value is, e.g. http://localhost:9201 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getElasticsearchHttpUrl();

    /**
     * Get the value for the key 'app.cipher.algorism'. <br>
     * The value is, e.g. aes <br>
     * comment: Cryptographer
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAppCipherAlgorism();

    /**
     * Get the value for the key 'app.cipher.key'. <br>
     * The value is, e.g. __change_me__ <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAppCipherKey();

    /**
     * Get the value for the key 'app.digest.algorism'. <br>
     * The value is, e.g. sha256 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAppDigestAlgorism();

    /**
     * Get the value for the key 'jvm.crawler.options'. <br>
     * The value is, e.g. -Djava.awt.headless=true
    -server
    -Xmx256m
    -XX:MaxMetaspaceSize=128m
    -XX:CompressedClassSpaceSize=32m
    -XX:-UseGCOverheadLimit
    -XX:+UseConcMarkSweepGC
    -XX:CMSInitiatingOccupancyFraction=75
    -XX:+UseParNewGC
    -XX:+UseTLAB
    -XX:+DisableExplicitGC <br>
     * comment: JVM options
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJvmCrawlerOptions();

    /**
     * Get the value for the key 'jvm.suggest.options'. <br>
     * The value is, e.g. -Djava.awt.headless=true
    -server
    -Xmx256m
    -XX:MaxMetaspaceSize=128m
    -XX:CompressedClassSpaceSize=32m
    -XX:-UseGCOverheadLimit
    -XX:+UseConcMarkSweepGC
    -XX:CMSInitiatingOccupancyFraction=75
    -XX:+UseParNewGC
    -XX:+UseTLAB
    -XX:+DisableExplicitGC <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJvmSuggestOptions();

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
     * Get the value for the key 'index.field.timestamp'. <br>
     * The value is, e.g. timestamp <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldTimestamp();

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
     * Get the value for the key 'index.document.search.index'. <br>
     * The value is, e.g. fess <br>
     * comment: document index
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentSearchIndex();

    /**
     * Get the value for the key 'index.document.update.index'. <br>
     * The value is, e.g. fess <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentUpdateIndex();

    /**
     * Get the value for the key 'index.document.type'. <br>
     * The value is, e.g. doc <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentType();

    /**
     * Get the value for the key 'query.max.length'. <br>
     * The value is, e.g. 1000 <br>
     * comment: query
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryMaxLength();

    /**
     * Get the value for the key 'query.max.length' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * comment: query
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryMaxLengthAsInteger();

    /**
     * Get the value for the key 'query.boost.title'. <br>
     * The value is, e.g. 1.6 <br>
     * comment: boost
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostTitle();

    /**
     * Get the value for the key 'query.boost.title' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 1.6 <br>
     * comment: boost
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostTitleAsDecimal();

    /**
     * Get the value for the key 'query.boost.title.lang'. <br>
     * The value is, e.g. 2.0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostTitleLang();

    /**
     * Get the value for the key 'query.boost.title.lang' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 2.0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostTitleLangAsDecimal();

    /**
     * Get the value for the key 'query.boost.content'. <br>
     * The value is, e.g. 1.0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostContent();

    /**
     * Get the value for the key 'query.boost.content' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 1.0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostContentAsDecimal();

    /**
     * Get the value for the key 'query.boost.content.lang'. <br>
     * The value is, e.g. 1.3 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostContentLang();

    /**
     * Get the value for the key 'query.boost.content.lang' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 1.3 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostContentLangAsDecimal();

    /**
     * Get the value for the key 'acl.as.role'. <br>
     * The value is, e.g. true <br>
     * comment: acl
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAclAsRole();

    /**
     * Is the property for the key 'acl.as.role' true? <br>
     * The value is, e.g. true <br>
     * comment: acl
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isAclAsRole();

    /**
     * Get the value for the key 'authentication.admin.roles'. <br>
     * The value is, e.g. admin <br>
     * comment: ------
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAuthenticationAdminRoles();

    /**
     * Get the value for the key 'search.default.roles'. <br>
     * The value is, e.g. guest <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSearchDefaultRoles();

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
     * Get the value for the key 'page.docboost.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * comment: max page size
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageDocboostMaxFetchSize();

    /**
     * Get the value for the key 'page.docboost.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * comment: max page size
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageDocboostMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.keymatch.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageKeymatchMaxFetchSize();

    /**
     * Get the value for the key 'page.keymatch.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageKeymatchMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.labeltype.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageLabeltypeMaxFetchSize();

    /**
     * Get the value for the key 'page.labeltype.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageLabeltypeMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.roletype.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageRoletypeMaxFetchSize();

    /**
     * Get the value for the key 'page.roletype.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageRoletypeMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.role.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageRoleMaxFetchSize();

    /**
     * Get the value for the key 'page.role.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageRoleMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.group.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageGroupMaxFetchSize();

    /**
     * Get the value for the key 'page.group.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageGroupMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'paging.search.page.start'. <br>
     * The value is, e.g. 0 <br>
     * comment: search page
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPagingSearchPageStart();

    /**
     * Get the value for the key 'paging.search.page.start' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * comment: search page
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPagingSearchPageStartAsInteger();

    /**
     * Get the value for the key 'paging.search.page.size'. <br>
     * The value is, e.g. 20 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPagingSearchPageSize();

    /**
     * Get the value for the key 'paging.search.page.size' as {@link Integer}. <br>
     * The value is, e.g. 20 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPagingSearchPageSizeAsInteger();

    /**
     * Get the value for the key 'paging.search.page.max.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPagingSearchPageMaxSize();

    /**
     * Get the value for the key 'paging.search.page.max.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPagingSearchPageMaxSizeAsInteger();

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
     * Get the value for the key 'online.help.base.link'. <br>
     * The value is, e.g. http://fess.codelibs.org/{lang}/{version}/admin/ <br>
     * comment: ------
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpBaseLink();

    /**
     * Get the value for the key 'online.help.name.failureurl'. <br>
     * The value is, e.g. failureurl <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameFailureurl();

    /**
     * Get the value for the key 'online.help.name.elevateword'. <br>
     * The value is, e.g. elevateword <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameElevateword();

    /**
     * Get the value for the key 'online.help.name.reqheader'. <br>
     * The value is, e.g. reqheader <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameReqheader();

    /**
     * Get the value for the key 'online.help.name.roletype'. <br>
     * The value is, e.g. roletype <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameRoletype();

    /**
     * Get the value for the key 'online.help.name.dict.synonym'. <br>
     * The value is, e.g. synonym <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDictSynonym();

    /**
     * Get the value for the key 'online.help.name.dict'. <br>
     * The value is, e.g. dict <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDict();

    /**
     * Get the value for the key 'online.help.name.dict.kuromoji'. <br>
     * The value is, e.g. kuromoji <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDictKuromoji();

    /**
     * Get the value for the key 'online.help.name.webconfig'. <br>
     * The value is, e.g. webconfig <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameWebconfig();

    /**
     * Get the value for the key 'online.help.name.searchlist'. <br>
     * The value is, e.g. searchlist <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameSearchlist();

    /**
     * Get the value for the key 'online.help.name.log'. <br>
     * The value is, e.g. log <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameLog();

    /**
     * Get the value for the key 'online.help.name.general'. <br>
     * The value is, e.g. general <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameGeneral();

    /**
     * Get the value for the key 'online.help.name.role'. <br>
     * The value is, e.g. role <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameRole();

    /**
     * Get the value for the key 'online.help.name.joblog'. <br>
     * The value is, e.g. joblog <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameJoblog();

    /**
     * Get the value for the key 'online.help.name.keymatch'. <br>
     * The value is, e.g. keymatch <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameKeymatch();

    /**
     * Get the value for the key 'online.help.name.wizard'. <br>
     * The value is, e.g. wizard <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameWizard();

    /**
     * Get the value for the key 'online.help.name.badword'. <br>
     * The value is, e.g. badword <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameBadword();

    /**
     * Get the value for the key 'online.help.name.pathmap'. <br>
     * The value is, e.g. pathmap <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNamePathmap();

    /**
     * Get the value for the key 'online.help.name.boostdoc'. <br>
     * The value is, e.g. boostdoc <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameBoostdoc();

    /**
     * Get the value for the key 'online.help.name.dataconfig'. <br>
     * The value is, e.g. dataconfig <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDataconfig();

    /**
     * Get the value for the key 'online.help.name.systeminfo'. <br>
     * The value is, e.g. systeminfo <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameSysteminfo();

    /**
     * Get the value for the key 'online.help.name.user'. <br>
     * The value is, e.g. user <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameUser();

    /**
     * Get the value for the key 'online.help.name.group'. <br>
     * The value is, e.g. group <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameGroup();

    /**
     * Get the value for the key 'online.help.name.design'. <br>
     * The value is, e.g. design <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDesign();

    /**
     * Get the value for the key 'online.help.name.dashboard'. <br>
     * The value is, e.g. dashboard <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDashboard();

    /**
     * Get the value for the key 'online.help.name.webauth'. <br>
     * The value is, e.g. webauth <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameWebauth();

    /**
     * Get the value for the key 'online.help.name.fileconfig'. <br>
     * The value is, e.g. fileconfig <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameFileconfig();

    /**
     * Get the value for the key 'online.help.name.fileauth'. <br>
     * The value is, e.g. fileauth <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameFileauth();

    /**
     * Get the value for the key 'online.help.name.labeltype'. <br>
     * The value is, e.g. labeltype <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameLabeltype();

    /**
     * Get the value for the key 'online.help.name.duplicatehost'. <br>
     * The value is, e.g. duplicatehost <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDuplicatehost();

    /**
     * Get the value for the key 'online.help.name.scheduler'. <br>
     * The value is, e.g. scheduler <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameScheduler();

    /**
     * Get the value for the key 'online.help.name.crawlinginfo'. <br>
     * The value is, e.g. crawlinginfo <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameCrawlinginfo();

    /**
     * Get the value for the key 'suggest.popular.word.seed'. <br>
     * The value is, e.g. 0 <br>
     * comment: ------
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestPopularWordSeed();

    /**
     * Get the value for the key 'suggest.popular.word.seed' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * comment: ------
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestPopularWordSeedAsInteger();

    /**
     * Get the value for the key 'suggest.popular.word.size'. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestPopularWordSize();

    /**
     * Get the value for the key 'suggest.popular.word.size' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestPopularWordSizeAsInteger();

    /**
     * Get the value for the key 'suggest.popular.word.window.size'. <br>
     * The value is, e.g. 30 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestPopularWordWindowSize();

    /**
     * Get the value for the key 'suggest.popular.word.window.size' as {@link Integer}. <br>
     * The value is, e.g. 30 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestPopularWordWindowSizeAsInteger();

    /**
     * Get the value for the key 'suggest.min.hit.count'. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestMinHitCount();

    /**
     * Get the value for the key 'suggest.min.hit.count' as {@link Integer}. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestMinHitCountAsInteger();

    /**
     * Get the value for the key 'suggest.field.contents'. <br>
     * The value is, e.g. _default <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestFieldContents();

    /**
     * Get the value for the key 'suggest.field.tags'. <br>
     * The value is, e.g. label <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestFieldTags();

    /**
     * Get the value for the key 'suggest.field.roles'. <br>
     * The value is, e.g. role <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestFieldRoles();

    /**
     * Get the value for the key 'suggest.field.index.contents'. <br>
     * The value is, e.g. content,title <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestFieldIndexContents();

    /**
     * Get the value for the key 'suggest.update.request.interval'. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestUpdateRequestInterval();

    /**
     * Get the value for the key 'suggest.update.request.interval' as {@link Integer}. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestUpdateRequestIntervalAsInteger();

    /**
     * Get the value for the key 'suggest.source.reader.scroll.size'. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestSourceReaderScrollSize();

    /**
     * Get the value for the key 'suggest.source.reader.scroll.size' as {@link Integer}. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestSourceReaderScrollSizeAsInteger();

    /**
     * Get the value for the key 'suggest.popular.word.cache.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestPopularWordCacheSize();

    /**
     * Get the value for the key 'suggest.popular.word.cache.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestPopularWordCacheSizeAsInteger();

    /**
     * Get the value for the key 'suggest.popular.word.cache.expire'. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestPopularWordCacheExpire();

    /**
     * Get the value for the key 'suggest.popular.word.cache.expire' as {@link Integer}. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestPopularWordCacheExpireAsInteger();

    /**
     * Get the value for the key 'suggest.role.filters'. <br>
     * The value is, e.g. guest <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestRoleFilters();

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

        public String getElasticsearchClusterName() {
            return get(FessConfig.ELASTICSEARCH_CLUSTER_NAME);
        }

        public String getElasticsearchHttpUrl() {
            return get(FessConfig.ELASTICSEARCH_HTTP_URL);
        }

        public String getAppCipherAlgorism() {
            return get(FessConfig.APP_CIPHER_ALGORISM);
        }

        public String getAppCipherKey() {
            return get(FessConfig.APP_CIPHER_KEY);
        }

        public String getAppDigestAlgorism() {
            return get(FessConfig.APP_DIGEST_ALGORISM);
        }

        public String getJvmCrawlerOptions() {
            return get(FessConfig.JVM_CRAWLER_OPTIONS);
        }

        public String getJvmSuggestOptions() {
            return get(FessConfig.JVM_SUGGEST_OPTIONS);
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

        public String getIndexFieldTimestamp() {
            return get(FessConfig.INDEX_FIELD_TIMESTAMP);
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

        public String getIndexDocumentSearchIndex() {
            return get(FessConfig.INDEX_DOCUMENT_SEARCH_INDEX);
        }

        public String getIndexDocumentUpdateIndex() {
            return get(FessConfig.INDEX_DOCUMENT_UPDATE_INDEX);
        }

        public String getIndexDocumentType() {
            return get(FessConfig.INDEX_DOCUMENT_TYPE);
        }

        public String getQueryMaxLength() {
            return get(FessConfig.QUERY_MAX_LENGTH);
        }

        public Integer getQueryMaxLengthAsInteger() {
            return getAsInteger(FessConfig.QUERY_MAX_LENGTH);
        }

        public String getQueryBoostTitle() {
            return get(FessConfig.QUERY_BOOST_TITLE);
        }

        public java.math.BigDecimal getQueryBoostTitleAsDecimal() {
            return getAsDecimal(FessConfig.QUERY_BOOST_TITLE);
        }

        public String getQueryBoostTitleLang() {
            return get(FessConfig.QUERY_BOOST_TITLE_LANG);
        }

        public java.math.BigDecimal getQueryBoostTitleLangAsDecimal() {
            return getAsDecimal(FessConfig.QUERY_BOOST_TITLE_LANG);
        }

        public String getQueryBoostContent() {
            return get(FessConfig.QUERY_BOOST_CONTENT);
        }

        public java.math.BigDecimal getQueryBoostContentAsDecimal() {
            return getAsDecimal(FessConfig.QUERY_BOOST_CONTENT);
        }

        public String getQueryBoostContentLang() {
            return get(FessConfig.QUERY_BOOST_CONTENT_LANG);
        }

        public java.math.BigDecimal getQueryBoostContentLangAsDecimal() {
            return getAsDecimal(FessConfig.QUERY_BOOST_CONTENT_LANG);
        }

        public String getAclAsRole() {
            return get(FessConfig.ACL_AS_ROLE);
        }

        public boolean isAclAsRole() {
            return is(FessConfig.ACL_AS_ROLE);
        }

        public String getAuthenticationAdminRoles() {
            return get(FessConfig.AUTHENTICATION_ADMIN_ROLES);
        }

        public String getSearchDefaultRoles() {
            return get(FessConfig.SEARCH_DEFAULT_ROLES);
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

        public String getPageDocboostMaxFetchSize() {
            return get(FessConfig.PAGE_DOCBOOST_MAX_FETCH_SIZE);
        }

        public Integer getPageDocboostMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_DOCBOOST_MAX_FETCH_SIZE);
        }

        public String getPageKeymatchMaxFetchSize() {
            return get(FessConfig.PAGE_KEYMATCH_MAX_FETCH_SIZE);
        }

        public Integer getPageKeymatchMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_KEYMATCH_MAX_FETCH_SIZE);
        }

        public String getPageLabeltypeMaxFetchSize() {
            return get(FessConfig.PAGE_LABELTYPE_MAX_FETCH_SIZE);
        }

        public Integer getPageLabeltypeMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_LABELTYPE_MAX_FETCH_SIZE);
        }

        public String getPageRoletypeMaxFetchSize() {
            return get(FessConfig.PAGE_ROLETYPE_MAX_FETCH_SIZE);
        }

        public Integer getPageRoletypeMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_ROLETYPE_MAX_FETCH_SIZE);
        }

        public String getPageRoleMaxFetchSize() {
            return get(FessConfig.PAGE_ROLE_MAX_FETCH_SIZE);
        }

        public Integer getPageRoleMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_ROLE_MAX_FETCH_SIZE);
        }

        public String getPageGroupMaxFetchSize() {
            return get(FessConfig.PAGE_GROUP_MAX_FETCH_SIZE);
        }

        public Integer getPageGroupMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_GROUP_MAX_FETCH_SIZE);
        }

        public String getPagingSearchPageStart() {
            return get(FessConfig.PAGING_SEARCH_PAGE_START);
        }

        public Integer getPagingSearchPageStartAsInteger() {
            return getAsInteger(FessConfig.PAGING_SEARCH_PAGE_START);
        }

        public String getPagingSearchPageSize() {
            return get(FessConfig.PAGING_SEARCH_PAGE_SIZE);
        }

        public Integer getPagingSearchPageSizeAsInteger() {
            return getAsInteger(FessConfig.PAGING_SEARCH_PAGE_SIZE);
        }

        public String getPagingSearchPageMaxSize() {
            return get(FessConfig.PAGING_SEARCH_PAGE_MAX_SIZE);
        }

        public Integer getPagingSearchPageMaxSizeAsInteger() {
            return getAsInteger(FessConfig.PAGING_SEARCH_PAGE_MAX_SIZE);
        }

        public String getMailFromName() {
            return get(FessConfig.MAIL_FROM_NAME);
        }

        public String getMailFromAddress() {
            return get(FessConfig.MAIL_FROM_ADDRESS);
        }

        public String getOnlineHelpBaseLink() {
            return get(FessConfig.ONLINE_HELP_BASE_LINK);
        }

        public String getOnlineHelpNameFailureurl() {
            return get(FessConfig.ONLINE_HELP_NAME_FAILUREURL);
        }

        public String getOnlineHelpNameElevateword() {
            return get(FessConfig.ONLINE_HELP_NAME_ELEVATEWORD);
        }

        public String getOnlineHelpNameReqheader() {
            return get(FessConfig.ONLINE_HELP_NAME_REQHEADER);
        }

        public String getOnlineHelpNameRoletype() {
            return get(FessConfig.ONLINE_HELP_NAME_ROLETYPE);
        }

        public String getOnlineHelpNameDictSynonym() {
            return get(FessConfig.ONLINE_HELP_NAME_DICT_SYNONYM);
        }

        public String getOnlineHelpNameDict() {
            return get(FessConfig.ONLINE_HELP_NAME_DICT);
        }

        public String getOnlineHelpNameDictKuromoji() {
            return get(FessConfig.ONLINE_HELP_NAME_DICT_KUROMOJI);
        }

        public String getOnlineHelpNameWebconfig() {
            return get(FessConfig.ONLINE_HELP_NAME_WEBCONFIG);
        }

        public String getOnlineHelpNameSearchlist() {
            return get(FessConfig.ONLINE_HELP_NAME_SEARCHLIST);
        }

        public String getOnlineHelpNameLog() {
            return get(FessConfig.ONLINE_HELP_NAME_LOG);
        }

        public String getOnlineHelpNameGeneral() {
            return get(FessConfig.ONLINE_HELP_NAME_GENERAL);
        }

        public String getOnlineHelpNameRole() {
            return get(FessConfig.ONLINE_HELP_NAME_ROLE);
        }

        public String getOnlineHelpNameJoblog() {
            return get(FessConfig.ONLINE_HELP_NAME_JOBLOG);
        }

        public String getOnlineHelpNameKeymatch() {
            return get(FessConfig.ONLINE_HELP_NAME_KEYMATCH);
        }

        public String getOnlineHelpNameWizard() {
            return get(FessConfig.ONLINE_HELP_NAME_WIZARD);
        }

        public String getOnlineHelpNameBadword() {
            return get(FessConfig.ONLINE_HELP_NAME_BADWORD);
        }

        public String getOnlineHelpNamePathmap() {
            return get(FessConfig.ONLINE_HELP_NAME_PATHMAP);
        }

        public String getOnlineHelpNameBoostdoc() {
            return get(FessConfig.ONLINE_HELP_NAME_BOOSTDOC);
        }

        public String getOnlineHelpNameDataconfig() {
            return get(FessConfig.ONLINE_HELP_NAME_DATACONFIG);
        }

        public String getOnlineHelpNameSysteminfo() {
            return get(FessConfig.ONLINE_HELP_NAME_SYSTEMINFO);
        }

        public String getOnlineHelpNameUser() {
            return get(FessConfig.ONLINE_HELP_NAME_USER);
        }

        public String getOnlineHelpNameGroup() {
            return get(FessConfig.ONLINE_HELP_NAME_GROUP);
        }

        public String getOnlineHelpNameDesign() {
            return get(FessConfig.ONLINE_HELP_NAME_DESIGN);
        }

        public String getOnlineHelpNameDashboard() {
            return get(FessConfig.ONLINE_HELP_NAME_DASHBOARD);
        }

        public String getOnlineHelpNameWebauth() {
            return get(FessConfig.ONLINE_HELP_NAME_WEBAUTH);
        }

        public String getOnlineHelpNameFileconfig() {
            return get(FessConfig.ONLINE_HELP_NAME_FILECONFIG);
        }

        public String getOnlineHelpNameFileauth() {
            return get(FessConfig.ONLINE_HELP_NAME_FILEAUTH);
        }

        public String getOnlineHelpNameLabeltype() {
            return get(FessConfig.ONLINE_HELP_NAME_LABELTYPE);
        }

        public String getOnlineHelpNameDuplicatehost() {
            return get(FessConfig.ONLINE_HELP_NAME_DUPLICATEHOST);
        }

        public String getOnlineHelpNameScheduler() {
            return get(FessConfig.ONLINE_HELP_NAME_SCHEDULER);
        }

        public String getOnlineHelpNameCrawlinginfo() {
            return get(FessConfig.ONLINE_HELP_NAME_CRAWLINGINFO);
        }

        public String getSuggestPopularWordSeed() {
            return get(FessConfig.SUGGEST_POPULAR_WORD_SEED);
        }

        public Integer getSuggestPopularWordSeedAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_POPULAR_WORD_SEED);
        }

        public String getSuggestPopularWordSize() {
            return get(FessConfig.SUGGEST_POPULAR_WORD_SIZE);
        }

        public Integer getSuggestPopularWordSizeAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_POPULAR_WORD_SIZE);
        }

        public String getSuggestPopularWordWindowSize() {
            return get(FessConfig.SUGGEST_POPULAR_WORD_WINDOW_SIZE);
        }

        public Integer getSuggestPopularWordWindowSizeAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_POPULAR_WORD_WINDOW_SIZE);
        }

        public String getSuggestMinHitCount() {
            return get(FessConfig.SUGGEST_MIN_HIT_COUNT);
        }

        public Integer getSuggestMinHitCountAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_MIN_HIT_COUNT);
        }

        public String getSuggestFieldContents() {
            return get(FessConfig.SUGGEST_FIELD_CONTENTS);
        }

        public String getSuggestFieldTags() {
            return get(FessConfig.SUGGEST_FIELD_TAGS);
        }

        public String getSuggestFieldRoles() {
            return get(FessConfig.SUGGEST_FIELD_ROLES);
        }

        public String getSuggestFieldIndexContents() {
            return get(FessConfig.SUGGEST_FIELD_INDEX_CONTENTS);
        }

        public String getSuggestUpdateRequestInterval() {
            return get(FessConfig.SUGGEST_UPDATE_REQUEST_INTERVAL);
        }

        public Integer getSuggestUpdateRequestIntervalAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_UPDATE_REQUEST_INTERVAL);
        }

        public String getSuggestSourceReaderScrollSize() {
            return get(FessConfig.SUGGEST_SOURCE_READER_SCROLL_SIZE);
        }

        public Integer getSuggestSourceReaderScrollSizeAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_SOURCE_READER_SCROLL_SIZE);
        }

        public String getSuggestPopularWordCacheSize() {
            return get(FessConfig.SUGGEST_POPULAR_WORD_CACHE_SIZE);
        }

        public Integer getSuggestPopularWordCacheSizeAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_POPULAR_WORD_CACHE_SIZE);
        }

        public String getSuggestPopularWordCacheExpire() {
            return get(FessConfig.SUGGEST_POPULAR_WORD_CACHE_EXPIRE);
        }

        public Integer getSuggestPopularWordCacheExpireAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_POPULAR_WORD_CACHE_EXPIRE);
        }

        public String getSuggestRoleFilters() {
            return get(FessConfig.SUGGEST_ROLE_FILTERS);
        }
    }
}
