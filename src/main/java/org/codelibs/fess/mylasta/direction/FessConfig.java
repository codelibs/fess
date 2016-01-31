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

import org.lastaflute.core.direction.exception.ConfigPropertyNotFoundException;

/**
 * @author FreeGen
 */
public interface FessConfig extends FessEnv, org.codelibs.fess.mylasta.direction.FessProp {

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

    /** The key of the configuration. e.g. default_crawler */
    String JOB_SYSTEM_JOB_IDS = "job.system.job.ids";

    /** The key of the configuration. e.g. Web Crawler - {0} */
    String JOB_TEMPLATE_TITLE_WEB = "job.template.title.web";

    /** The key of the configuration. e.g. File Crawler - {0} */
    String JOB_TEMPLATE_TITLE_FILE = "job.template.title.file";

    /** The key of the configuration. e.g. Data Crawler - {0} */
    String JOB_TEMPLATE_TITLE_DATA = "job.template.title.data";

    /** The key of the configuration. e.g. return container.getComponent("crawlJob").logLevel("info").sessionId("{3}").execute(executor, [{0}] as String[],[{1}] as String[],[{2}] as String[], ""); */
    String JOB_TEMPLATE_SCRIPT = "job.template.script";

    /** The key of the configuration. e.g. java */
    String JAVA_COMMAND_PATH = "java.command.path";

    /** The key of the configuration. e.g. UTF-8 */
    String PATH_ENCODING = "path.encoding";

    /** The key of the configuration. e.g. true */
    String USE_OWN_TMP_DIR = "use.own.tmp.dir";

    /** The key of the configuration. e.g. 4000 */
    String MAX_LOG_OUTPUT_LENGTH = "max.log.output.length";

    /** The key of the configuration. e.g. js */
    String SUPPORTED_UPLOADED_JS_EXTENTIONS = "supported.uploaded.js.extentions";

    /** The key of the configuration. e.g. css */
    String SUPPORTED_UPLOADED_CSS_EXTENTIONS = "supported.uploaded.css.extentions";

    /** The key of the configuration. e.g. jpg,jpeg,gif,png,swf */
    String SUPPORTED_UPLOADED_MEDIA_EXTENTIONS = "supported.uploaded.media.extentions";

    /** The key of the configuration. e.g. ar,bg,ca,da,de,el,en,es,eu,fa,fi,fr,ga,gl,hi,hu,hy,id,it,ja,lv,ko,nl,no,pt,ro,ru,sv,th,tr,zh_CN,zh_TW,zh */
    String SUPPORTED_LANGUAGES = "supported.languages";

    /** The key of the configuration. e.g. 50 */
    String CRAWLER_DOCUMENT_MAX_SITE_LENGTH = "crawler.document.max.site.length";

    /** The key of the configuration. e.g. UTF-8 */
    String CRAWLER_DOCUMENT_SITE_ENCODING = "crawler.document.site.encoding";

    /** The key of the configuration. e.g. unknown */
    String CRAWLER_DOCUMENT_UNKNOWN_HOSTNAME = "crawler.document.unknown.hostname";

    /** The key of the configuration. e.g. false */
    String CRAWLER_DOCUMENT_USE_SITE_ENCODING_ON_ENGLISH = "crawler.document.use.site.encoding.on.english";

    /** The key of the configuration. e.g. true */
    String CRAWLER_DOCUMENT_APPEND_DATA = "crawler.document.append.data";

    /** The key of the configuration. e.g. UTF-8 */
    String CRAWLER_CRAWLING_DATA_ENCODING = "crawler.crawling.data.encoding";

    /** The key of the configuration. e.g. resourceName,X-Parsed-By,Content-Encoding.*,Content-Type.* */
    String CRAWLER_METADATA_CONTENT_EXCLUDES = "crawler.metadata.content.excludes";

    /** The key of the configuration. e.g. title=title:string
    Title=title:string
    */
    String CRAWLER_METADATA_NAME_MAPPING = "crawler.metadata.name.mapping";

    /** The key of the configuration. e.g. //BODY */
    String CRAWLER_DOCUMENT_HTML_CONTENT_XPATH = "crawler.document.html.content.xpath";

    /** The key of the configuration. e.g. //HTML/@lang */
    String CRAWLER_DOCUMENT_HTML_LANG_XPATH = "crawler.document.html.lang.xpath";

    /** The key of the configuration. e.g. //META[@name='description']/@content */
    String CRAWLER_DOCUMENT_HTML_DIGEST_XPATH = "crawler.document.html.digest.xpath";

    /** The key of the configuration. e.g. //LINK[@rel='canonical']/@href */
    String CRAWLER_DOCUMENT_HTML_CANNONICAL_XPATH = "crawler.document.html.cannonical.xpath";

    /** The key of the configuration. e.g. noscript,script */
    String CRAWLER_DOCUMENT_HTML_PRUNED_TAGS = "crawler.document.html.pruned.tags";

    /** The key of the configuration. e.g. 200 */
    String CRAWLER_DOCUMENT_HTML_MAX_DIGEST_LENGTH = "crawler.document.html.max.digest.length";

    /** The key of the configuration. e.g.  */
    String CRAWLER_DOCUMENT_FILE_NAME_ENCODING = "crawler.document.file.name.encoding";

    /** The key of the configuration. e.g. No title. */
    String CRAWLER_DOCUMENT_FILE_NO_TITLE_LABEL = "crawler.document.file.no.title.label";

    /** The key of the configuration. e.g. 10 */
    String CRAWLER_DOCUMENT_FILE_ABBREVIATION_MARGIN_LENGTH = "crawler.document.file.abbreviation.margin.length";

    /** The key of the configuration. e.g. false */
    String CRAWLER_DOCUMENT_FILE_IGNORE_EMPTY_CONTENT = "crawler.document.file.ignore.empty.content";

    /** The key of the configuration. e.g. 100 */
    String CRAWLER_DOCUMENT_FILE_MAX_TITLE_LENGTH = "crawler.document.file.max.title.length";

    /** The key of the configuration. e.g. 200 */
    String CRAWLER_DOCUMENT_FILE_MAX_DIGEST_LENGTH = "crawler.document.file.max.digest.length";

    /** The key of the configuration. e.g. true */
    String CRAWLER_DOCUMENT_FILE_APPEND_META_CONTENT = "crawler.document.file.append.meta.content";

    /** The key of the configuration. e.g. true */
    String CRAWLER_DOCUMENT_FILE_APPEND_BODY_CONTENT = "crawler.document.file.append.body.content";

    /** The key of the configuration. e.g. true */
    String CRAWLER_DOCUMENT_CACHE_ENABLE = "crawler.document.cache.enable";

    /** The key of the configuration. e.g. 2621440 */
    String CRAWLER_DOCUMENT_CACHE_MAX_SIZE = "crawler.document.cache.max.size";

    /** The key of the configuration. e.g. text/html */
    String CRAWLER_DOCUMENT_CACHE_SUPPORTED_MIMETYPES = "crawler.document.cache.supported.mimetypes";

    /** The key of the configuration. e.g. text/html */
    String CRAWLER_DOCUMENT_CACHE_HTML_MIMETYPES = "crawler.document.cache.html.mimetypes";

    /** The key of the configuration. e.g. true */
    String INDEXER_THREAD_DUMP_ENABLED = "indexer.thread.dump.enabled";

    /** The key of the configuration. e.g. true */
    String INDEXER_CLICK_COUNT_ENABLED = "indexer.click.count.enabled";

    /** The key of the configuration. e.g. true */
    String INDEXER_FAVORITE_COUNT_ENABLED = "indexer.favorite.count.enabled";

    /** The key of the configuration. e.g. 10000 */
    String INDEXER_WEBFS_COMMIT_MARGIN_TIME = "indexer.webfs.commit.margin.time";

    /** The key of the configuration. e.g. 60 */
    String INDEXER_WEBFS_MAX_EMPTY_LIST_CONUNT = "indexer.webfs.max.empty.list.conunt";

    /** The key of the configuration. e.g. 60000 */
    String INDEXER_WEBFS_UPDATE_INTERVAL = "indexer.webfs.update.interval";

    /** The key of the configuration. e.g. 5 */
    String INDEXER_WEBFS_MAX_DOCUMENT_CACHE_SIZE = "indexer.webfs.max.document.cache.size";

    /** The key of the configuration. e.g. 5 */
    String INDEXER_DATA_MAX_DOCUMENT_CACHE_SIZE = "indexer.data.max.document.cache.size";

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

    /** The key of the configuration. e.g. true */
    String QUERY_REPLACE_TERM_WITH_PREFIX_QUERY = "query.replace.term.with.prefix.query";

    /** The key of the configuration. e.g. 1.6 */
    String QUERY_BOOST_TITLE = "query.boost.title";

    /** The key of the configuration. e.g. 2.0 */
    String QUERY_BOOST_TITLE_LANG = "query.boost.title.lang";

    /** The key of the configuration. e.g. 1.0 */
    String QUERY_BOOST_CONTENT = "query.boost.content";

    /** The key of the configuration. e.g. 1.3 */
    String QUERY_BOOST_CONTENT_LANG = "query.boost.content.lang";

    /** The key of the configuration. e.g. true */
    String SMB_ROLE_FROM_FILE = "smb.role.from.file";

    /** The key of the configuration. e.g. true */
    String SMB_ROLE_AS_USER = "smb.role.as.user";

    /** The key of the configuration. e.g. true */
    String SMB_ROLE_AS_GROUP = "smb.role.as.group";

    /** The key of the configuration. e.g. 1,2 */
    String SMB_AVAILABLE_SID_TYPES = "smb.available.sid.types";

    /** The key of the configuration. e.g. .fess_config,.fess_user */
    String INDEX_BACKUP_TARGETS = "index.backup.targets";

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

    /** The key of the configuration. e.g. 100 */
    String PAGE_CRAWLING_INFO_PARAM_MAX_FETCH_SIZE = "page.crawling.info.param.max.fetch.size";

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

    /** The key of the configuration. e.g.  */
    String SCHEDULER_TARGET_NAME = "scheduler.target.name";

    /** The key of the configuration. e.g. org.codelibs.fess.app.job.ScriptExecutorJob */
    String SCHEDULER_JOB_CLASS = "scheduler.job.class";

    /** The key of the configuration. e.g. QUIT */
    String SCHEDULER_CONCURRENT_EXEC_MODE = "scheduler.concurrent.exec.mode";

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

    /** The key of the configuration. e.g. backup */
    String ONLINE_HELP_NAME_BACKUP = "online.help.name.backup";

    /** The key of the configuration. e.g.  */
    String ONLINE_HELP_SUPPORTED_LANGS = "online.help.supported.langs";

    /** The key of the configuration. e.g. 0 */
    String SUGGEST_POPULAR_WORD_SEED = "suggest.popular.word.seed";

    /** The key of the configuration. e.g.  */
    String SUGGEST_POPULAR_WORD_TAGS = "suggest.popular.word.tags";

    /** The key of the configuration. e.g.  */
    String SUGGEST_POPULAR_WORD_FIELDS = "suggest.popular.word.fields";

    /** The key of the configuration. e.g.  */
    String SUGGEST_POPULAR_WORD_EXCLUDES = "suggest.popular.word.excludes";

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
     * Get the value for the key 'job.system.job.ids'. <br>
     * The value is, e.g. default_crawler <br>
     * comment: job
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJobSystemJobIds();

    /**
     * Get the value for the key 'job.template.title.web'. <br>
     * The value is, e.g. Web Crawler - {0} <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJobTemplateTitleWeb();

    /**
     * Get the value for the key 'job.template.title.file'. <br>
     * The value is, e.g. File Crawler - {0} <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJobTemplateTitleFile();

    /**
     * Get the value for the key 'job.template.title.data'. <br>
     * The value is, e.g. Data Crawler - {0} <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJobTemplateTitleData();

    /**
     * Get the value for the key 'job.template.script'. <br>
     * The value is, e.g. return container.getComponent("crawlJob").logLevel("info").sessionId("{3}").execute(executor, [{0}] as String[],[{1}] as String[],[{2}] as String[], ""); <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJobTemplateScript();

    /**
     * Get the value for the key 'java.command.path'. <br>
     * The value is, e.g. java <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJavaCommandPath();

    /**
     * Get the value for the key 'path.encoding'. <br>
     * The value is, e.g. UTF-8 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPathEncoding();

    /**
     * Get the value for the key 'use.own.tmp.dir'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getUseOwnTmpDir();

    /**
     * Is the property for the key 'use.own.tmp.dir' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isUseOwnTmpDir();

    /**
     * Get the value for the key 'max.log.output.length'. <br>
     * The value is, e.g. 4000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getMaxLogOutputLength();

    /**
     * Get the value for the key 'max.log.output.length' as {@link Integer}. <br>
     * The value is, e.g. 4000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getMaxLogOutputLengthAsInteger();

    /**
     * Get the value for the key 'supported.uploaded.js.extentions'. <br>
     * The value is, e.g. js <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSupportedUploadedJsExtentions();

    /**
     * Get the value for the key 'supported.uploaded.css.extentions'. <br>
     * The value is, e.g. css <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSupportedUploadedCssExtentions();

    /**
     * Get the value for the key 'supported.uploaded.media.extentions'. <br>
     * The value is, e.g. jpg,jpeg,gif,png,swf <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSupportedUploadedMediaExtentions();

    /**
     * Get the value for the key 'supported.languages'. <br>
     * The value is, e.g. ar,bg,ca,da,de,el,en,es,eu,fa,fi,fr,ga,gl,hi,hu,hy,id,it,ja,lv,ko,nl,no,pt,ro,ru,sv,th,tr,zh_CN,zh_TW,zh <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSupportedLanguages();

    /**
     * Get the value for the key 'crawler.document.max.site.length'. <br>
     * The value is, e.g. 50 <br>
     * comment: common
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentMaxSiteLength();

    /**
     * Get the value for the key 'crawler.document.max.site.length' as {@link Integer}. <br>
     * The value is, e.g. 50 <br>
     * comment: common
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentMaxSiteLengthAsInteger();

    /**
     * Get the value for the key 'crawler.document.site.encoding'. <br>
     * The value is, e.g. UTF-8 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentSiteEncoding();

    /**
     * Get the value for the key 'crawler.document.unknown.hostname'. <br>
     * The value is, e.g. unknown <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentUnknownHostname();

    /**
     * Get the value for the key 'crawler.document.use.site.encoding.on.english'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentUseSiteEncodingOnEnglish();

    /**
     * Is the property for the key 'crawler.document.use.site.encoding.on.english' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerDocumentUseSiteEncodingOnEnglish();

    /**
     * Get the value for the key 'crawler.document.append.data'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentAppendData();

    /**
     * Is the property for the key 'crawler.document.append.data' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerDocumentAppendData();

    /**
     * Get the value for the key 'crawler.crawling.data.encoding'. <br>
     * The value is, e.g. UTF-8 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerCrawlingDataEncoding();

    /**
     * Get the value for the key 'crawler.metadata.content.excludes'. <br>
     * The value is, e.g. resourceName,X-Parsed-By,Content-Encoding.*,Content-Type.* <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerMetadataContentExcludes();

    /**
     * Get the value for the key 'crawler.metadata.name.mapping'. <br>
     * The value is, e.g. title=title:string
    Title=title:string
    <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerMetadataNameMapping();

    /**
     * Get the value for the key 'crawler.document.html.content.xpath'. <br>
     * The value is, e.g. //BODY <br>
     * comment: html
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlContentXpath();

    /**
     * Get the value for the key 'crawler.document.html.lang.xpath'. <br>
     * The value is, e.g. //HTML/@lang <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlLangXpath();

    /**
     * Get the value for the key 'crawler.document.html.digest.xpath'. <br>
     * The value is, e.g. //META[@name='description']/@content <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlDigestXpath();

    /**
     * Get the value for the key 'crawler.document.html.cannonical.xpath'. <br>
     * The value is, e.g. //LINK[@rel='canonical']/@href <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlCannonicalXpath();

    /**
     * Get the value for the key 'crawler.document.html.pruned.tags'. <br>
     * The value is, e.g. noscript,script <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlPrunedTags();

    /**
     * Get the value for the key 'crawler.document.html.max.digest.length'. <br>
     * The value is, e.g. 200 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlMaxDigestLength();

    /**
     * Get the value for the key 'crawler.document.html.max.digest.length' as {@link Integer}. <br>
     * The value is, e.g. 200 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentHtmlMaxDigestLengthAsInteger();

    /**
     * Get the value for the key 'crawler.document.file.name.encoding'. <br>
     * The value is, e.g.  <br>
     * comment: file
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileNameEncoding();

    /**
     * Get the value for the key 'crawler.document.file.name.encoding' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * comment: file
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentFileNameEncodingAsInteger();

    /**
     * Get the value for the key 'crawler.document.file.no.title.label'. <br>
     * The value is, e.g. No title. <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileNoTitleLabel();

    /**
     * Get the value for the key 'crawler.document.file.abbreviation.margin.length'. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileAbbreviationMarginLength();

    /**
     * Get the value for the key 'crawler.document.file.abbreviation.margin.length' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentFileAbbreviationMarginLengthAsInteger();

    /**
     * Get the value for the key 'crawler.document.file.ignore.empty.content'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileIgnoreEmptyContent();

    /**
     * Is the property for the key 'crawler.document.file.ignore.empty.content' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerDocumentFileIgnoreEmptyContent();

    /**
     * Get the value for the key 'crawler.document.file.max.title.length'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileMaxTitleLength();

    /**
     * Get the value for the key 'crawler.document.file.max.title.length' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentFileMaxTitleLengthAsInteger();

    /**
     * Get the value for the key 'crawler.document.file.max.digest.length'. <br>
     * The value is, e.g. 200 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileMaxDigestLength();

    /**
     * Get the value for the key 'crawler.document.file.max.digest.length' as {@link Integer}. <br>
     * The value is, e.g. 200 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentFileMaxDigestLengthAsInteger();

    /**
     * Get the value for the key 'crawler.document.file.append.meta.content'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileAppendMetaContent();

    /**
     * Is the property for the key 'crawler.document.file.append.meta.content' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerDocumentFileAppendMetaContent();

    /**
     * Get the value for the key 'crawler.document.file.append.body.content'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileAppendBodyContent();

    /**
     * Is the property for the key 'crawler.document.file.append.body.content' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerDocumentFileAppendBodyContent();

    /**
     * Get the value for the key 'crawler.document.cache.enable'. <br>
     * The value is, e.g. true <br>
     * comment: cache
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentCacheEnable();

    /**
     * Is the property for the key 'crawler.document.cache.enable' true? <br>
     * The value is, e.g. true <br>
     * comment: cache
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerDocumentCacheEnable();

    /**
     * Get the value for the key 'crawler.document.cache.max.size'. <br>
     * The value is, e.g. 2621440 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentCacheMaxSize();

    /**
     * Get the value for the key 'crawler.document.cache.max.size' as {@link Integer}. <br>
     * The value is, e.g. 2621440 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentCacheMaxSizeAsInteger();

    /**
     * Get the value for the key 'crawler.document.cache.supported.mimetypes'. <br>
     * The value is, e.g. text/html <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentCacheSupportedMimetypes();

    /**
     * Get the value for the key 'crawler.document.cache.html.mimetypes'. <br>
     * The value is, e.g. text/html <br>
     * comment: ,text/plain,application/xml,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-powerpoint,application/vnd.openxmlformats-officedocument.presentationml.presentation
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentCacheHtmlMimetypes();

    /**
     * Get the value for the key 'indexer.thread.dump.enabled'. <br>
     * The value is, e.g. true <br>
     * comment: indexer
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerThreadDumpEnabled();

    /**
     * Is the property for the key 'indexer.thread.dump.enabled' true? <br>
     * The value is, e.g. true <br>
     * comment: indexer
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isIndexerThreadDumpEnabled();

    /**
     * Get the value for the key 'indexer.click.count.enabled'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerClickCountEnabled();

    /**
     * Is the property for the key 'indexer.click.count.enabled' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isIndexerClickCountEnabled();

    /**
     * Get the value for the key 'indexer.favorite.count.enabled'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerFavoriteCountEnabled();

    /**
     * Is the property for the key 'indexer.favorite.count.enabled' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isIndexerFavoriteCountEnabled();

    /**
     * Get the value for the key 'indexer.webfs.commit.margin.time'. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerWebfsCommitMarginTime();

    /**
     * Get the value for the key 'indexer.webfs.commit.margin.time' as {@link Integer}. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerWebfsCommitMarginTimeAsInteger();

    /**
     * Get the value for the key 'indexer.webfs.max.empty.list.conunt'. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerWebfsMaxEmptyListConunt();

    /**
     * Get the value for the key 'indexer.webfs.max.empty.list.conunt' as {@link Integer}. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerWebfsMaxEmptyListConuntAsInteger();

    /**
     * Get the value for the key 'indexer.webfs.update.interval'. <br>
     * The value is, e.g. 60000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerWebfsUpdateInterval();

    /**
     * Get the value for the key 'indexer.webfs.update.interval' as {@link Integer}. <br>
     * The value is, e.g. 60000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerWebfsUpdateIntervalAsInteger();

    /**
     * Get the value for the key 'indexer.webfs.max.document.cache.size'. <br>
     * The value is, e.g. 5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerWebfsMaxDocumentCacheSize();

    /**
     * Get the value for the key 'indexer.webfs.max.document.cache.size' as {@link Integer}. <br>
     * The value is, e.g. 5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerWebfsMaxDocumentCacheSizeAsInteger();

    /**
     * Get the value for the key 'indexer.data.max.document.cache.size'. <br>
     * The value is, e.g. 5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerDataMaxDocumentCacheSize();

    /**
     * Get the value for the key 'indexer.data.max.document.cache.size' as {@link Integer}. <br>
     * The value is, e.g. 5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerDataMaxDocumentCacheSizeAsInteger();

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
     * Get the value for the key 'query.replace.term.with.prefix.query'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryReplaceTermWithPrefixQuery();

    /**
     * Is the property for the key 'query.replace.term.with.prefix.query' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isQueryReplaceTermWithPrefixQuery();

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
     * Get the value for the key 'smb.role.from.file'. <br>
     * The value is, e.g. true <br>
     * comment: acl
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSmbRoleFromFile();

    /**
     * Is the property for the key 'smb.role.from.file' true? <br>
     * The value is, e.g. true <br>
     * comment: acl
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isSmbRoleFromFile();

    /**
     * Get the value for the key 'smb.role.as.user'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSmbRoleAsUser();

    /**
     * Is the property for the key 'smb.role.as.user' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isSmbRoleAsUser();

    /**
     * Get the value for the key 'smb.role.as.group'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSmbRoleAsGroup();

    /**
     * Is the property for the key 'smb.role.as.group' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isSmbRoleAsGroup();

    /**
     * Get the value for the key 'smb.available.sid.types'. <br>
     * The value is, e.g. 1,2 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSmbAvailableSidTypes();

    /**
     * Get the value for the key 'smb.available.sid.types' as {@link Integer}. <br>
     * The value is, e.g. 1,2 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSmbAvailableSidTypesAsInteger();

    /**
     * Get the value for the key 'index.backup.targets'. <br>
     * The value is, e.g. .fess_config,.fess_user <br>
     * comment: backup
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexBackupTargets();

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
     * comment: fetch page size
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageDocboostMaxFetchSize();

    /**
     * Get the value for the key 'page.docboost.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * comment: fetch page size
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
     * Get the value for the key 'page.crawling.info.param.max.fetch.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageCrawlingInfoParamMaxFetchSize();

    /**
     * Get the value for the key 'page.crawling.info.param.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageCrawlingInfoParamMaxFetchSizeAsInteger();

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
     * Get the value for the key 'scheduler.target.name'. <br>
     * The value is, e.g.  <br>
     * comment: ------
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSchedulerTargetName();

    /**
     * Get the value for the key 'scheduler.target.name' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * comment: ------
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSchedulerTargetNameAsInteger();

    /**
     * Get the value for the key 'scheduler.job.class'. <br>
     * The value is, e.g. org.codelibs.fess.app.job.ScriptExecutorJob <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSchedulerJobClass();

    /**
     * Get the value for the key 'scheduler.concurrent.exec.mode'. <br>
     * The value is, e.g. QUIT <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSchedulerConcurrentExecMode();

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
     * Get the value for the key 'online.help.name.backup'. <br>
     * The value is, e.g. backup <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameBackup();

    /**
     * Get the value for the key 'online.help.supported.langs'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpSupportedLangs();

    /**
     * Get the value for the key 'online.help.supported.langs' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getOnlineHelpSupportedLangsAsInteger();

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
     * Get the value for the key 'suggest.popular.word.tags'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestPopularWordTags();

    /**
     * Get the value for the key 'suggest.popular.word.tags' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestPopularWordTagsAsInteger();

    /**
     * Get the value for the key 'suggest.popular.word.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestPopularWordFields();

    /**
     * Get the value for the key 'suggest.popular.word.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestPopularWordFieldsAsInteger();

    /**
     * Get the value for the key 'suggest.popular.word.excludes'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestPopularWordExcludes();

    /**
     * Get the value for the key 'suggest.popular.word.excludes' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestPopularWordExcludesAsInteger();

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

        public String getJobSystemJobIds() {
            return get(FessConfig.JOB_SYSTEM_JOB_IDS);
        }

        public String getJobTemplateTitleWeb() {
            return get(FessConfig.JOB_TEMPLATE_TITLE_WEB);
        }

        public String getJobTemplateTitleFile() {
            return get(FessConfig.JOB_TEMPLATE_TITLE_FILE);
        }

        public String getJobTemplateTitleData() {
            return get(FessConfig.JOB_TEMPLATE_TITLE_DATA);
        }

        public String getJobTemplateScript() {
            return get(FessConfig.JOB_TEMPLATE_SCRIPT);
        }

        public String getJavaCommandPath() {
            return get(FessConfig.JAVA_COMMAND_PATH);
        }

        public String getPathEncoding() {
            return get(FessConfig.PATH_ENCODING);
        }

        public String getUseOwnTmpDir() {
            return get(FessConfig.USE_OWN_TMP_DIR);
        }

        public boolean isUseOwnTmpDir() {
            return is(FessConfig.USE_OWN_TMP_DIR);
        }

        public String getMaxLogOutputLength() {
            return get(FessConfig.MAX_LOG_OUTPUT_LENGTH);
        }

        public Integer getMaxLogOutputLengthAsInteger() {
            return getAsInteger(FessConfig.MAX_LOG_OUTPUT_LENGTH);
        }

        public String getSupportedUploadedJsExtentions() {
            return get(FessConfig.SUPPORTED_UPLOADED_JS_EXTENTIONS);
        }

        public String getSupportedUploadedCssExtentions() {
            return get(FessConfig.SUPPORTED_UPLOADED_CSS_EXTENTIONS);
        }

        public String getSupportedUploadedMediaExtentions() {
            return get(FessConfig.SUPPORTED_UPLOADED_MEDIA_EXTENTIONS);
        }

        public String getSupportedLanguages() {
            return get(FessConfig.SUPPORTED_LANGUAGES);
        }

        public String getCrawlerDocumentMaxSiteLength() {
            return get(FessConfig.CRAWLER_DOCUMENT_MAX_SITE_LENGTH);
        }

        public Integer getCrawlerDocumentMaxSiteLengthAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_MAX_SITE_LENGTH);
        }

        public String getCrawlerDocumentSiteEncoding() {
            return get(FessConfig.CRAWLER_DOCUMENT_SITE_ENCODING);
        }

        public String getCrawlerDocumentUnknownHostname() {
            return get(FessConfig.CRAWLER_DOCUMENT_UNKNOWN_HOSTNAME);
        }

        public String getCrawlerDocumentUseSiteEncodingOnEnglish() {
            return get(FessConfig.CRAWLER_DOCUMENT_USE_SITE_ENCODING_ON_ENGLISH);
        }

        public boolean isCrawlerDocumentUseSiteEncodingOnEnglish() {
            return is(FessConfig.CRAWLER_DOCUMENT_USE_SITE_ENCODING_ON_ENGLISH);
        }

        public String getCrawlerDocumentAppendData() {
            return get(FessConfig.CRAWLER_DOCUMENT_APPEND_DATA);
        }

        public boolean isCrawlerDocumentAppendData() {
            return is(FessConfig.CRAWLER_DOCUMENT_APPEND_DATA);
        }

        public String getCrawlerCrawlingDataEncoding() {
            return get(FessConfig.CRAWLER_CRAWLING_DATA_ENCODING);
        }

        public String getCrawlerMetadataContentExcludes() {
            return get(FessConfig.CRAWLER_METADATA_CONTENT_EXCLUDES);
        }

        public String getCrawlerMetadataNameMapping() {
            return get(FessConfig.CRAWLER_METADATA_NAME_MAPPING);
        }

        public String getCrawlerDocumentHtmlContentXpath() {
            return get(FessConfig.CRAWLER_DOCUMENT_HTML_CONTENT_XPATH);
        }

        public String getCrawlerDocumentHtmlLangXpath() {
            return get(FessConfig.CRAWLER_DOCUMENT_HTML_LANG_XPATH);
        }

        public String getCrawlerDocumentHtmlDigestXpath() {
            return get(FessConfig.CRAWLER_DOCUMENT_HTML_DIGEST_XPATH);
        }

        public String getCrawlerDocumentHtmlCannonicalXpath() {
            return get(FessConfig.CRAWLER_DOCUMENT_HTML_CANNONICAL_XPATH);
        }

        public String getCrawlerDocumentHtmlPrunedTags() {
            return get(FessConfig.CRAWLER_DOCUMENT_HTML_PRUNED_TAGS);
        }

        public String getCrawlerDocumentHtmlMaxDigestLength() {
            return get(FessConfig.CRAWLER_DOCUMENT_HTML_MAX_DIGEST_LENGTH);
        }

        public Integer getCrawlerDocumentHtmlMaxDigestLengthAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_HTML_MAX_DIGEST_LENGTH);
        }

        public String getCrawlerDocumentFileNameEncoding() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_NAME_ENCODING);
        }

        public Integer getCrawlerDocumentFileNameEncodingAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_FILE_NAME_ENCODING);
        }

        public String getCrawlerDocumentFileNoTitleLabel() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_NO_TITLE_LABEL);
        }

        public String getCrawlerDocumentFileAbbreviationMarginLength() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_ABBREVIATION_MARGIN_LENGTH);
        }

        public Integer getCrawlerDocumentFileAbbreviationMarginLengthAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_FILE_ABBREVIATION_MARGIN_LENGTH);
        }

        public String getCrawlerDocumentFileIgnoreEmptyContent() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_IGNORE_EMPTY_CONTENT);
        }

        public boolean isCrawlerDocumentFileIgnoreEmptyContent() {
            return is(FessConfig.CRAWLER_DOCUMENT_FILE_IGNORE_EMPTY_CONTENT);
        }

        public String getCrawlerDocumentFileMaxTitleLength() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_MAX_TITLE_LENGTH);
        }

        public Integer getCrawlerDocumentFileMaxTitleLengthAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_FILE_MAX_TITLE_LENGTH);
        }

        public String getCrawlerDocumentFileMaxDigestLength() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_MAX_DIGEST_LENGTH);
        }

        public Integer getCrawlerDocumentFileMaxDigestLengthAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_FILE_MAX_DIGEST_LENGTH);
        }

        public String getCrawlerDocumentFileAppendMetaContent() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_APPEND_META_CONTENT);
        }

        public boolean isCrawlerDocumentFileAppendMetaContent() {
            return is(FessConfig.CRAWLER_DOCUMENT_FILE_APPEND_META_CONTENT);
        }

        public String getCrawlerDocumentFileAppendBodyContent() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_APPEND_BODY_CONTENT);
        }

        public boolean isCrawlerDocumentFileAppendBodyContent() {
            return is(FessConfig.CRAWLER_DOCUMENT_FILE_APPEND_BODY_CONTENT);
        }

        public String getCrawlerDocumentCacheEnable() {
            return get(FessConfig.CRAWLER_DOCUMENT_CACHE_ENABLE);
        }

        public boolean isCrawlerDocumentCacheEnable() {
            return is(FessConfig.CRAWLER_DOCUMENT_CACHE_ENABLE);
        }

        public String getCrawlerDocumentCacheMaxSize() {
            return get(FessConfig.CRAWLER_DOCUMENT_CACHE_MAX_SIZE);
        }

        public Integer getCrawlerDocumentCacheMaxSizeAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_CACHE_MAX_SIZE);
        }

        public String getCrawlerDocumentCacheSupportedMimetypes() {
            return get(FessConfig.CRAWLER_DOCUMENT_CACHE_SUPPORTED_MIMETYPES);
        }

        public String getCrawlerDocumentCacheHtmlMimetypes() {
            return get(FessConfig.CRAWLER_DOCUMENT_CACHE_HTML_MIMETYPES);
        }

        public String getIndexerThreadDumpEnabled() {
            return get(FessConfig.INDEXER_THREAD_DUMP_ENABLED);
        }

        public boolean isIndexerThreadDumpEnabled() {
            return is(FessConfig.INDEXER_THREAD_DUMP_ENABLED);
        }

        public String getIndexerClickCountEnabled() {
            return get(FessConfig.INDEXER_CLICK_COUNT_ENABLED);
        }

        public boolean isIndexerClickCountEnabled() {
            return is(FessConfig.INDEXER_CLICK_COUNT_ENABLED);
        }

        public String getIndexerFavoriteCountEnabled() {
            return get(FessConfig.INDEXER_FAVORITE_COUNT_ENABLED);
        }

        public boolean isIndexerFavoriteCountEnabled() {
            return is(FessConfig.INDEXER_FAVORITE_COUNT_ENABLED);
        }

        public String getIndexerWebfsCommitMarginTime() {
            return get(FessConfig.INDEXER_WEBFS_COMMIT_MARGIN_TIME);
        }

        public Integer getIndexerWebfsCommitMarginTimeAsInteger() {
            return getAsInteger(FessConfig.INDEXER_WEBFS_COMMIT_MARGIN_TIME);
        }

        public String getIndexerWebfsMaxEmptyListConunt() {
            return get(FessConfig.INDEXER_WEBFS_MAX_EMPTY_LIST_CONUNT);
        }

        public Integer getIndexerWebfsMaxEmptyListConuntAsInteger() {
            return getAsInteger(FessConfig.INDEXER_WEBFS_MAX_EMPTY_LIST_CONUNT);
        }

        public String getIndexerWebfsUpdateInterval() {
            return get(FessConfig.INDEXER_WEBFS_UPDATE_INTERVAL);
        }

        public Integer getIndexerWebfsUpdateIntervalAsInteger() {
            return getAsInteger(FessConfig.INDEXER_WEBFS_UPDATE_INTERVAL);
        }

        public String getIndexerWebfsMaxDocumentCacheSize() {
            return get(FessConfig.INDEXER_WEBFS_MAX_DOCUMENT_CACHE_SIZE);
        }

        public Integer getIndexerWebfsMaxDocumentCacheSizeAsInteger() {
            return getAsInteger(FessConfig.INDEXER_WEBFS_MAX_DOCUMENT_CACHE_SIZE);
        }

        public String getIndexerDataMaxDocumentCacheSize() {
            return get(FessConfig.INDEXER_DATA_MAX_DOCUMENT_CACHE_SIZE);
        }

        public Integer getIndexerDataMaxDocumentCacheSizeAsInteger() {
            return getAsInteger(FessConfig.INDEXER_DATA_MAX_DOCUMENT_CACHE_SIZE);
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

        public String getQueryReplaceTermWithPrefixQuery() {
            return get(FessConfig.QUERY_REPLACE_TERM_WITH_PREFIX_QUERY);
        }

        public boolean isQueryReplaceTermWithPrefixQuery() {
            return is(FessConfig.QUERY_REPLACE_TERM_WITH_PREFIX_QUERY);
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

        public String getSmbRoleFromFile() {
            return get(FessConfig.SMB_ROLE_FROM_FILE);
        }

        public boolean isSmbRoleFromFile() {
            return is(FessConfig.SMB_ROLE_FROM_FILE);
        }

        public String getSmbRoleAsUser() {
            return get(FessConfig.SMB_ROLE_AS_USER);
        }

        public boolean isSmbRoleAsUser() {
            return is(FessConfig.SMB_ROLE_AS_USER);
        }

        public String getSmbRoleAsGroup() {
            return get(FessConfig.SMB_ROLE_AS_GROUP);
        }

        public boolean isSmbRoleAsGroup() {
            return is(FessConfig.SMB_ROLE_AS_GROUP);
        }

        public String getSmbAvailableSidTypes() {
            return get(FessConfig.SMB_AVAILABLE_SID_TYPES);
        }

        public Integer getSmbAvailableSidTypesAsInteger() {
            return getAsInteger(FessConfig.SMB_AVAILABLE_SID_TYPES);
        }

        public String getIndexBackupTargets() {
            return get(FessConfig.INDEX_BACKUP_TARGETS);
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

        public String getPageCrawlingInfoParamMaxFetchSize() {
            return get(FessConfig.PAGE_CRAWLING_INFO_PARAM_MAX_FETCH_SIZE);
        }

        public Integer getPageCrawlingInfoParamMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_CRAWLING_INFO_PARAM_MAX_FETCH_SIZE);
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

        public String getSchedulerTargetName() {
            return get(FessConfig.SCHEDULER_TARGET_NAME);
        }

        public Integer getSchedulerTargetNameAsInteger() {
            return getAsInteger(FessConfig.SCHEDULER_TARGET_NAME);
        }

        public String getSchedulerJobClass() {
            return get(FessConfig.SCHEDULER_JOB_CLASS);
        }

        public String getSchedulerConcurrentExecMode() {
            return get(FessConfig.SCHEDULER_CONCURRENT_EXEC_MODE);
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

        public String getOnlineHelpNameBackup() {
            return get(FessConfig.ONLINE_HELP_NAME_BACKUP);
        }

        public String getOnlineHelpSupportedLangs() {
            return get(FessConfig.ONLINE_HELP_SUPPORTED_LANGS);
        }

        public Integer getOnlineHelpSupportedLangsAsInteger() {
            return getAsInteger(FessConfig.ONLINE_HELP_SUPPORTED_LANGS);
        }

        public String getSuggestPopularWordSeed() {
            return get(FessConfig.SUGGEST_POPULAR_WORD_SEED);
        }

        public Integer getSuggestPopularWordSeedAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_POPULAR_WORD_SEED);
        }

        public String getSuggestPopularWordTags() {
            return get(FessConfig.SUGGEST_POPULAR_WORD_TAGS);
        }

        public Integer getSuggestPopularWordTagsAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_POPULAR_WORD_TAGS);
        }

        public String getSuggestPopularWordFields() {
            return get(FessConfig.SUGGEST_POPULAR_WORD_FIELDS);
        }

        public Integer getSuggestPopularWordFieldsAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_POPULAR_WORD_FIELDS);
        }

        public String getSuggestPopularWordExcludes() {
            return get(FessConfig.SUGGEST_POPULAR_WORD_EXCLUDES);
        }

        public Integer getSuggestPopularWordExcludesAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_POPULAR_WORD_EXCLUDES);
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
