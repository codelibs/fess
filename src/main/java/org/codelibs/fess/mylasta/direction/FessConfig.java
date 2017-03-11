/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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

    /** The key of the configuration. e.g. false */
    String ELASTICSEARCH_TRANSPORT_SNIFF = "elasticsearch.transport.sniff";

    /** The key of the configuration. e.g. 1m */
    String ELASTICSEARCH_TRANSPORT_ping_timeout = "elasticsearch.transport.ping_timeout";

    /** The key of the configuration. e.g. 5s */
    String ELASTICSEARCH_TRANSPORT_nodes_sampler_interval = "elasticsearch.transport.nodes_sampler_interval";

    /** The key of the configuration. e.g. aes */
    String APP_CIPHER_ALGORISM = "app.cipher.algorism";

    /** The key of the configuration. e.g. ___change__me___ */
    String APP_CIPHER_KEY = "app.cipher.key";

    /** The key of the configuration. e.g. sha256 */
    String APP_DIGEST_ALGORISM = "app.digest.algorism";

    /** The key of the configuration. e.g. -Djava.awt.headless=true
    -server
    -Xmx512m
    -XX:MaxMetaspaceSize=128m
    -XX:CompressedClassSpaceSize=32m
    -XX:-UseGCOverheadLimit
    -XX:+UseConcMarkSweepGC
    -XX:CMSInitiatingOccupancyFraction=75
    -XX:+UseParNewGC
    -XX:+UseTLAB
    -XX:+DisableExplicitGC
    -XX:+HeapDumpOnOutOfMemoryError
    -XX:-OmitStackTraceInFastThrow
    -Djcifs.smb.client.connTimeout=60000
    -Djcifs.smb.client.soTimeout=35000
    -Djcifs.smb.client.responseTimeout=30000
    -Dgroovy.use.classvalue=true
    */
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
    -XX:+DisableExplicitGC
    -XX:+HeapDumpOnOutOfMemoryError
    -Dgroovy.use.classvalue=true
    */
    String JVM_SUGGEST_OPTIONS = "jvm.suggest.options";

    /** The key of the configuration. e.g. default_crawler */
    String JOB_SYSTEM_JOB_IDS = "job.system.job.ids";

    /** The key of the configuration. e.g. Web Crawler - {0} */
    String JOB_TEMPLATE_TITLE_WEB = "job.template.title.web";

    /** The key of the configuration. e.g. File Crawler - {0} */
    String JOB_TEMPLATE_TITLE_FILE = "job.template.title.file";

    /** The key of the configuration. e.g. Data Crawler - {0} */
    String JOB_TEMPLATE_TITLE_DATA = "job.template.title.data";

    /** The key of the configuration. e.g. return container.getComponent("crawlJob").logLevel("info").sessionId("{3}").webConfigIds([{0}] as String[]).fileConfigIds([{1}] as String[]).dataConfigIds([{2}] as String[]).jobExecutor(executor).execute(); */
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

    /** The key of the configuration. e.g. license.properties */
    String SUPPORTED_UPLOADED_FILES = "supported.uploaded.files";

    /** The key of the configuration. e.g. ar,bg,ca,da,de,el,en,es,eu,fa,fi,fr,ga,gl,hi,hu,hy,id,it,ja,lv,ko,nl,no,pt,ro,ru,sv,th,tr,zh_CN,zh_TW,zh */
    String SUPPORTED_LANGUAGES = "supported.languages";

    /** The key of the configuration. e.g. 60 */
    String API_ACCESS_TOKEN_LENGTH = "api.access.token.length";

    /** The key of the configuration. e.g. false */
    String API_ACCESS_TOKEN_REQUIRED = "api.access.token.required";

    /** The key of the configuration. e.g. Radmin-api */
    String API_ADMIN_ACCESS_PERMISSIONS = "api.admin.access.permissions";

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

    /** The key of the configuration. e.g. 20 */
    String CRAWLER_DOCUMENT_MAX_ALPHANUM_TERM_SIZE = "crawler.document.max.alphanum.term.size";

    /** The key of the configuration. e.g. 10 */
    String CRAWLER_DOCUMENT_MAX_SYMBOL_TERM_SIZE = "crawler.document.max.symbol.term.size";

    /** The key of the configuration. e.g. false */
    String CRAWLER_DOCUMENT_DUPLICATE_TERM_REMOVED = "crawler.document.duplicate.term.removed";

    /** The key of the configuration. e.g. u0009u000Au000Bu000Cu000Du001Cu001Du001Eu001Fu0020u00A0u1680u180Eu2000u2001u2002u2003u2004u2005u2006u2007u2008u2009u200Au200Bu200Cu202Fu205Fu3000uFEFFuFFFDu00B6 */
    String CRAWLER_DOCUMENT_SPACE_CHARS = "crawler.document.space.chars";

    /** The key of the configuration. e.g. UTF-8 */
    String CRAWLER_CRAWLING_DATA_ENCODING = "crawler.crawling.data.encoding";

    /** The key of the configuration. e.g. http,https */
    String CRAWLER_WEB_PROTOCOLS = "crawler.web.protocols";

    /** The key of the configuration. e.g. file,smb,ftp */
    String CRAWLER_FILE_PROTOCOLS = "crawler.file.protocols";

    /** The key of the configuration. e.g. false */
    String CRAWLER_IGNORE_ROBOTS_TXT = "crawler.ignore.robots.txt";

    /** The key of the configuration. e.g. false */
    String CRAWLER_IGNORE_META_ROBOTS = "crawler.ignore.meta.robots";

    /** The key of the configuration. e.g. true */
    String CRAWLER_IGNORE_CONTENT_EXCEPTION = "crawler.ignore.content.exception";

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

    /** The key of the configuration. e.g. noscript,script,style */
    String CRAWLER_DOCUMENT_HTML_PRUNED_TAGS = "crawler.document.html.pruned.tags";

    /** The key of the configuration. e.g. 200 */
    String CRAWLER_DOCUMENT_HTML_MAX_DIGEST_LENGTH = "crawler.document.html.max.digest.length";

    /** The key of the configuration. e.g.  */
    String CRAWLER_DOCUMENT_FILE_NAME_ENCODING = "crawler.document.file.name.encoding";

    /** The key of the configuration. e.g. No title. */
    String CRAWLER_DOCUMENT_FILE_NO_TITLE_LABEL = "crawler.document.file.no.title.label";

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

    /** The key of the configuration. e.g.  */
    String CRAWLER_DOCUMENT_FILE_DEFAULT_LANG = "crawler.document.file.default.lang";

    /** The key of the configuration. e.g. true */
    String CRAWLER_DOCUMENT_CACHE_ENABLED = "crawler.document.cache.enabled";

    /** The key of the configuration. e.g. 2621440 */
    String CRAWLER_DOCUMENT_CACHE_MAX_SIZE = "crawler.document.cache.max.size";

    /** The key of the configuration. e.g. text/html */
    String CRAWLER_DOCUMENT_CACHE_SUPPORTED_MIMETYPES = "crawler.document.cache.supported.mimetypes";

    /** The key of the configuration. e.g. text/html */
    String CRAWLER_DOCUMENT_CACHE_HTML_MIMETYPES = "crawler.document.cache.html.mimetypes";

    /** The key of the configuration. e.g. true */
    String INDEXER_THREAD_DUMP_ENABLED = "indexer.thread.dump.enabled";

    /** The key of the configuration. e.g. 1000 */
    String INDEXER_UNPROCESSED_DOCUMENT_SIZE = "indexer.unprocessed.document.size";

    /** The key of the configuration. e.g. true */
    String INDEXER_CLICK_COUNT_ENABLED = "indexer.click.count.enabled";

    /** The key of the configuration. e.g. true */
    String INDEXER_FAVORITE_COUNT_ENABLED = "indexer.favorite.count.enabled";

    /** The key of the configuration. e.g. 5000 */
    String INDEXER_WEBFS_COMMIT_MARGIN_TIME = "indexer.webfs.commit.margin.time";

    /** The key of the configuration. e.g. 360 */
    String INDEXER_WEBFS_MAX_EMPTY_LIST_COUNT = "indexer.webfs.max.empty.list.count";

    /** The key of the configuration. e.g. 10000 */
    String INDEXER_WEBFS_UPDATE_INTERVAL = "indexer.webfs.update.interval";

    /** The key of the configuration. e.g. 100 */
    String INDEXER_WEBFS_MAX_DOCUMENT_CACHE_SIZE = "indexer.webfs.max.document.cache.size";

    /** The key of the configuration. e.g. 10485760 */
    String INDEXER_WEBFS_MAX_DOCUMENT_REQUEST_SIZE = "indexer.webfs.max.document.request.size";

    /** The key of the configuration. e.g. 5 */
    String INDEXER_DATA_MAX_DOCUMENT_CACHE_SIZE = "indexer.data.max.document.cache.size";

    /** The key of the configuration. e.g. 10485760 */
    String INDEXER_DATA_MAX_DOCUMENT_REQUEST_SIZE = "indexer.data.max.document.request.size";

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

    /** The key of the configuration. e.g. _version */
    String INDEX_FIELD_VERSION = "index.field.version";

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

    /** The key of the configuration. e.g. important_content */
    String INDEX_FIELD_important_content = "index.field.important_content";

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

    /** The key of the configuration. e.g. filename */
    String INDEX_FIELD_FILENAME = "index.field.filename";

    /** The key of the configuration. e.g. content_title */
    String RESPONSE_FIELD_content_title = "response.field.content_title";

    /** The key of the configuration. e.g. content_description */
    String RESPONSE_FIELD_content_description = "response.field.content_description";

    /** The key of the configuration. e.g. url_link */
    String RESPONSE_FIELD_url_link = "response.field.url_link";

    /** The key of the configuration. e.g. site_path */
    String RESPONSE_FIELD_site_path = "response.field.site_path";

    /** The key of the configuration. e.g. fess.search */
    String INDEX_DOCUMENT_SEARCH_INDEX = "index.document.search.index";

    /** The key of the configuration. e.g. fess.update */
    String INDEX_DOCUMENT_UPDATE_INDEX = "index.document.update.index";

    /** The key of the configuration. e.g. doc */
    String INDEX_DOCUMENT_TYPE = "index.document.type";

    /** The key of the configuration. e.g. fess */
    String INDEX_DOCUMENT_SUGGEST_INDEX = "index.document.suggest.index";

    /** The key of the configuration. e.g. .crawler */
    String INDEX_DOCUMENT_CRAWLER_INDEX = "index.document.crawler.index";

    /** The key of the configuration. e.g. lang,role,label,anchor */
    String INDEX_ADMIN_ARRAY_FIELDS = "index.admin.array.fields";

    /** The key of the configuration. e.g. expires,created,timestamp,last_modified */
    String INDEX_ADMIN_DATE_FIELDS = "index.admin.date.fields";

    /** The key of the configuration. e.g.  */
    String INDEX_ADMIN_INTEGER_FIELDS = "index.admin.integer.fields";

    /** The key of the configuration. e.g. content_length,favorite_count,click_count */
    String INDEX_ADMIN_LONG_FIELDS = "index.admin.long.fields";

    /** The key of the configuration. e.g. boost */
    String INDEX_ADMIN_FLOAT_FIELDS = "index.admin.float.fields";

    /** The key of the configuration. e.g.  */
    String INDEX_ADMIN_DOUBLE_FIELDS = "index.admin.double.fields";

    /** The key of the configuration. e.g. doc_id,url,title,role,boost */
    String INDEX_ADMIN_REQUIRED_FIELDS = "index.admin.required.fields";

    /** The key of the configuration. e.g. 3m */
    String INDEX_SEARCH_TIMEOUT = "index.search.timeout";

    /** The key of the configuration. e.g. 3m */
    String INDEX_SCROLL_SEARCH_TIMEOUT_TIMEOUT = "index.scroll.search.timeout.timeout";

    /** The key of the configuration. e.g. 3m */
    String INDEX_INDEX_TIMEOUT = "index.index.timeout";

    /** The key of the configuration. e.g. 3m */
    String INDEX_BULK_TIMEOUT = "index.bulk.timeout";

    /** The key of the configuration. e.g. 3m */
    String INDEX_DELETE_TIMEOUT = "index.delete.timeout";

    /** The key of the configuration. e.g. 10m */
    String INDEX_HEALTH_TIMEOUT = "index.health.timeout";

    /** The key of the configuration. e.g. 1m */
    String INDEX_INDICES_TIMEOUT = "index.indices.timeout";

    /** The key of the configuration. e.g. 1000 */
    String QUERY_MAX_LENGTH = "query.max.length";

    /** The key of the configuration. e.g. location */
    String QUERY_GEO_FIELDS = "query.geo.fields";

    /** The key of the configuration. e.g. browser_lang */
    String QUERY_BROWSER_LANG_PARAMETER_NAME = "query.browser.lang.parameter.name";

    /** The key of the configuration. e.g. true */
    String QUERY_REPLACE_TERM_WITH_PREFIX_QUERY = "query.replace.term.with.prefix.query";

    /** The key of the configuration. e.g. 50 */
    String QUERY_HIGHLIGHT_FRAGMENT_SIZE = "query.highlight.fragment.size";

    /** The key of the configuration. e.g. 5 */
    String QUERY_HIGHLIGHT_NUMBER_OF_FRAGMENTS = "query.highlight.number.of.fragments";

    /** The key of the configuration. e.g. fvh */
    String QUERY_HIGHLIGHT_TYPE = "query.highlight.type";

    /** The key of the configuration. e.g. 100000 */
    String QUERY_MAX_SEARCH_RESULT_OFFSET = "query.max.search.result.offset";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_RESPONSE_FIELDS = "query.additional.response.fields";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_API_RESPONSE_FIELDS = "query.additional.api.response.fields";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_CACHE_RESPONSE_FIELDS = "query.additional.cache.response.fields";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_HIGHLIGHTED_FIELDS = "query.additional.highlighted.fields";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_SEARCH_FIELDS = "query.additional.search.fields";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_FACET_FIELDS = "query.additional.facet.fields";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_SORT_FIELDS = "query.additional.sort.fields";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_NOT_ANALYZED_FIELDS = "query.additional.not.analyzed.fields";

    /** The key of the configuration. e.g.  */
    String QUERY_DEFAULT_LANGUAGES = "query.default.languages";

    /** The key of the configuration. e.g. ar=ar
    bg=bg
    bn=bn
    ca=ca
    cs=cs
    da=da
    de=de
    el=el
    en=en
    es=es
    et=et
    fa=fa
    fi=fi
    fr=fr
    gu=gu
    he=he
    hi=hi
    hr=hr
    hu=hu
    id=id
    it=it
    ja=ja
    ko=ko
    lt=lt
    lv=lv
    mk=mk
    ml=ml
    nl=nl
    no=no
    pa=pa
    pl=pl
    pt=pt
    ro=ro
    ru=ru
    si=si
    sq=sq
    sv=sv
    ta=ta
    te=te
    th=th
    tl=tl
    tr=tr
    uk=uk
    ur=ur
    vi=vi
    zh=zh-cn
    zh-cn=zh-cn
    zh-tw=zh-tw
    */
    String QUERY_LANGUAGE_MAPPING = "query.language.mapping";

    /** The key of the configuration. e.g. 0.2 */
    String QUERY_BOOST_TITLE = "query.boost.title";

    /** The key of the configuration. e.g. 1.0 */
    String QUERY_BOOST_TITLE_LANG = "query.boost.title.lang";

    /** The key of the configuration. e.g. 0.1 */
    String QUERY_BOOST_CONTENT = "query.boost.content";

    /** The key of the configuration. e.g. 0.5 */
    String QUERY_BOOST_CONTENT_LANG = "query.boost.content.lang";

    /** The key of the configuration. e.g. true */
    String SMB_ROLE_FROM_FILE = "smb.role.from.file";

    /** The key of the configuration. e.g. 1,2 */
    String SMB_AVAILABLE_SID_TYPES = "smb.available.sid.types";

    /** The key of the configuration. e.g. .fess_basic_config.bulk,.fess_config.bulk,.fess_user.bulk,system.properties */
    String INDEX_BACKUP_TARGETS = "index.backup.targets";

    /** The key of the configuration. e.g. 4000 */
    String FORM_ADMIN_MAX_INPUT_SIZE = "form.admin.max.input.size";

    /** The key of the configuration. e.g. admin */
    String AUTHENTICATION_ADMIN_USERS = "authentication.admin.users";

    /** The key of the configuration. e.g. admin */
    String AUTHENTICATION_ADMIN_ROLES = "authentication.admin.roles";

    /** The key of the configuration. e.g.  */
    String ROLE_SEARCH_DEFAULT_PERMISSIONS = "role.search.default.permissions";

    /** The key of the configuration. e.g. {role}guest */
    String ROLE_SEARCH_DEFAULT_DISPLAY_PERMISSIONS = "role.search.default.display.permissions";

    /** The key of the configuration. e.g. {role}guest */
    String ROLE_SEARCH_GUEST_PERMISSIONS = "role.search.guest.permissions";

    /** The key of the configuration. e.g. 1 */
    String ROLE_SEARCH_USER_PREFIX = "role.search.user.prefix";

    /** The key of the configuration. e.g. 2 */
    String ROLE_SEARCH_GROUP_PREFIX = "role.search.group.prefix";

    /** The key of the configuration. e.g. R */
    String ROLE_SEARCH_ROLE_PREFIX = "role.search.role.prefix";

    /** The key of the configuration. e.g. / */
    String COOKIE_DEFAULT_PATH = "cookie.default.path";

    /** The key of the configuration. e.g. 3600 */
    String COOKIE_DEFAULT_EXPIRE = "cookie.default.expire";

    /** The key of the configuration. e.g. 86400 */
    String COOKIE_ETERNAL_EXPIRE = "cookie.eternal.expire";

    /** The key of the configuration. e.g. FES */
    String COOKIE_REMEMBER_ME_HARBOR_KEY = "cookie.remember.me.harbor.key";

    /** The key of the configuration. e.g. 25 */
    String PAGING_PAGE_SIZE = "paging.page.size";

    /** The key of the configuration. e.g. 5 */
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
    String PAGE_USER_MAX_FETCH_SIZE = "page.user.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_ROLE_MAX_FETCH_SIZE = "page.role.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_GROUP_MAX_FETCH_SIZE = "page.group.max.fetch.size";

    /** The key of the configuration. e.g. 100 */
    String PAGE_CRAWLING_INFO_PARAM_MAX_FETCH_SIZE = "page.crawling.info.param.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_CRAWLING_INFO_MAX_FETCH_SIZE = "page.crawling.info.max.fetch.size";

    /** The key of the configuration. e.g. 100 */
    String PAGE_DATA_CONFIG_MAX_FETCH_SIZE = "page.data.config.max.fetch.size";

    /** The key of the configuration. e.g. 100 */
    String PAGE_WEB_CONFIG_MAX_FETCH_SIZE = "page.web.config.max.fetch.size";

    /** The key of the configuration. e.g. 100 */
    String PAGE_FILE_CONFIG_MAX_FETCH_SIZE = "page.file.config.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_DUPLICATE_HOST_MAX_FETCH_SIZE = "page.duplicate.host.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_FAILURE_URL_MAX_FETCH_SIZE = "page.failure.url.max.fetch.size";

    /** The key of the configuration. e.g. 100 */
    String PAGE_FAVORITE_LOG_MAX_FETCH_SIZE = "page.favorite.log.max.fetch.size";

    /** The key of the configuration. e.g. 100 */
    String PAGE_FILE_AUTH_MAX_FETCH_SIZE = "page.file.auth.max.fetch.size";

    /** The key of the configuration. e.g. 100 */
    String PAGE_WEB_AUTH_MAX_FETCH_SIZE = "page.web.auth.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_PATH_MAPPING_MAX_FETCH_SIZE = "page.path.mapping.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_REQUEST_HEADER_MAX_FETCH_SIZE = "page.request.header.max.fetch.size";

    /** The key of the configuration. e.g. 100 */
    String PAGE_SCHEDULED_JOB_MAX_FETCH_SIZE = "page.scheduled.job.max.fetch.size";

    /** The key of the configuration. e.g. 100 */
    String PAGE_SEARCH_FIELD_LOG_MAX_FETCH_SIZE = "page.search.field.log.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_ELEVATE_WORD_MAX_FETCH_SIZE = "page.elevate.word.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_BAD_WORD_MAX_FETCH_SIZE = "page.bad.word.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_DICTIONARY_MAX_FETCH_SIZE = "page.dictionary.max.fetch.size";

    /** The key of the configuration. e.g. 100 */
    String PAGE_THUMBNAIL_QUEUE_MAX_FETCH_SIZE = "page.thumbnail.queue.max.fetch.size";

    /** The key of the configuration. e.g. 0 */
    String PAGING_SEARCH_PAGE_START = "paging.search.page.start";

    /** The key of the configuration. e.g. 20 */
    String PAGING_SEARCH_PAGE_SIZE = "paging.search.page.size";

    /** The key of the configuration. e.g. 100 */
    String PAGING_SEARCH_PAGE_MAX_SIZE = "paging.search.page.max.size";

    /** The key of the configuration. e.g. true */
    String THUMBNAIL_HTML_PHANTOMJS_ENABLED = "thumbnail.html.phantomjs.enabled";

    /** The key of the configuration. e.g. 20000 */
    String THUMBNAIL_HTML_PHANTOMJS_MAX_HEIGHT = "thumbnail.html.phantomjs.max.height";

    /** The key of the configuration. e.g. 600000 */
    String THUMBNAIL_HTML_PHANTOMJS_KEEP_ALIVE = "thumbnail.html.phantomjs.keep.alive";

    /** The key of the configuration. e.g. 1200 */
    String THUMBNAIL_HTML_PHANTOMJS_WINDOW_WIDTH = "thumbnail.html.phantomjs.window.width";

    /** The key of the configuration. e.g. 800 */
    String THUMBNAIL_HTML_PHANTOMJS_WINDOW_HEIGHT = "thumbnail.html.phantomjs.window.height";

    /** The key of the configuration. e.g. 160 */
    String THUMBNAIL_HTML_PHANTOMJS_THUMBNAIL_WIDTH = "thumbnail.html.phantomjs.thumbnail.width";

    /** The key of the configuration. e.g. 160 */
    String THUMBNAIL_HTML_PHANTOMJS_THUMBNAIL_HEIGHT = "thumbnail.html.phantomjs.thumbnail.height";

    /** The key of the configuration. e.g. png */
    String THUMBNAIL_HTML_PHANTOMJS_FORMAT = "thumbnail.html.phantomjs.format";

    /** The key of the configuration. e.g. all */
    String THUMBNAIL_GENERATOR_TARGETS = "thumbnail.generator.targets";

    /** The key of the configuration. e.g. false */
    String THUMBNAIL_CRAWLER_ENABLED = "thumbnail.crawler.enabled";

    /** The key of the configuration. e.g. userCode */
    String USER_CODE_REQUEST_PARAMETER = "user.code.request.parameter";

    /** The key of the configuration. e.g. 20 */
    String USER_CODE_MIN_LENGTH = "user.code.min.length";

    /** The key of the configuration. e.g. 100 */
    String USER_CODE_MAX_LENGTH = "user.code.max.length";

    /** The key of the configuration. e.g. [a-zA-Z0-9_]+ */
    String USER_CODE_PATTERN = "user.code.pattern";

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

    /** The key of the configuration. e.g. 30 */
    String SCHEDULER_MONITOR_INTERVAL = "scheduler.monitor.interval";

    /** The key of the configuration. e.g. http://fess.codelibs.org/{lang}/{version}/admin/ */
    String ONLINE_HELP_BASE_LINK = "online.help.base.link";

    /** The key of the configuration. e.g. failureurl */
    String ONLINE_HELP_NAME_FAILUREURL = "online.help.name.failureurl";

    /** The key of the configuration. e.g. elevateword */
    String ONLINE_HELP_NAME_ELEVATEWORD = "online.help.name.elevateword";

    /** The key of the configuration. e.g. reqheader */
    String ONLINE_HELP_NAME_REQHEADER = "online.help.name.reqheader";

    /** The key of the configuration. e.g. synonym */
    String ONLINE_HELP_NAME_DICT_SYNONYM = "online.help.name.dict.synonym";

    /** The key of the configuration. e.g. dict */
    String ONLINE_HELP_NAME_DICT = "online.help.name.dict";

    /** The key of the configuration. e.g. kuromoji */
    String ONLINE_HELP_NAME_DICT_KUROMOJI = "online.help.name.dict.kuromoji";

    /** The key of the configuration. e.g. seunjeon */
    String ONLINE_HELP_NAME_DICT_SEUNJEON = "online.help.name.dict.seunjeon";

    /** The key of the configuration. e.g. protwords */
    String ONLINE_HELP_NAME_DICT_PROTWORDS = "online.help.name.dict.protwords";

    /** The key of the configuration. e.g. mapping */
    String ONLINE_HELP_NAME_DICT_MAPPING = "online.help.name.dict.mapping";

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

    /** The key of the configuration. e.g. upgrade */
    String ONLINE_HELP_NAME_UPGRADE = "online.help.name.upgrade";

    /** The key of the configuration. e.g. esreq */
    String ONLINE_HELP_NAME_ESREQ = "online.help.name.esreq";

    /** The key of the configuration. e.g. accesstoken */
    String ONLINE_HELP_NAME_ACCESSTOKEN = "online.help.name.accesstoken";

    /** The key of the configuration. e.g. suggest */
    String ONLINE_HELP_NAME_SUGGEST = "online.help.name.suggest";

    /** The key of the configuration. e.g. ja */
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

    /** The key of the configuration. e.g. 50% */
    String SUGGEST_UPDATE_CONTENTS_LIMIT_NUM_PERCENTAGE = "suggest.update.contents.limit.num.percentage";

    /** The key of the configuration. e.g. 10000 */
    String SUGGEST_UPDATE_CONTENTS_LIMIT_NUM = "suggest.update.contents.limit.num";

    /** The key of the configuration. e.g. 1 */
    String SUGGEST_SOURCE_READER_SCROLL_SIZE = "suggest.source.reader.scroll.size";

    /** The key of the configuration. e.g. 1000 */
    String SUGGEST_POPULAR_WORD_CACHE_SIZE = "suggest.popular.word.cache.size";

    /** The key of the configuration. e.g. 60 */
    String SUGGEST_POPULAR_WORD_CACHE_EXPIRE = "suggest.popular.word.cache.expire";

    /** The key of the configuration. e.g. {user}guest,{role}guest */
    String SUGGEST_SEARCH_LOG_PERMISSIONS = "suggest.search.log.permissions";

    /** The key of the configuration. e.g. false */
    String LDAP_ADMIN_ENABLED = "ldap.admin.enabled";

    /** The key of the configuration. e.g. uid=%s */
    String LDAP_ADMIN_USER_FILTER = "ldap.admin.user.filter";

    /** The key of the configuration. e.g. ou=People,dc=fess,dc=codelibs,dc=org */
    String LDAP_ADMIN_USER_BASE_DN = "ldap.admin.user.base.dn";

    /** The key of the configuration. e.g. organizationalPerson,top,person,inetOrgPerson */
    String LDAP_ADMIN_USER_OBJECT_CLASSES = "ldap.admin.user.object.classes";

    /** The key of the configuration. e.g. cn=%s */
    String LDAP_ADMIN_ROLE_FILTER = "ldap.admin.role.filter";

    /** The key of the configuration. e.g. ou=Role,dc=fess,dc=codelibs,dc=org */
    String LDAP_ADMIN_ROLE_BASE_DN = "ldap.admin.role.base.dn";

    /** The key of the configuration. e.g. groupOfNames */
    String LDAP_ADMIN_ROLE_OBJECT_CLASSES = "ldap.admin.role.object.classes";

    /** The key of the configuration. e.g. cn=%s */
    String LDAP_ADMIN_GROUP_FILTER = "ldap.admin.group.filter";

    /** The key of the configuration. e.g. ou=Group,dc=fess,dc=codelibs,dc=org */
    String LDAP_ADMIN_GROUP_BASE_DN = "ldap.admin.group.base.dn";

    /** The key of the configuration. e.g. groupOfNames */
    String LDAP_ADMIN_GROUP_OBJECT_CLASSES = "ldap.admin.group.object.classes";

    /** The key of the configuration. e.g. true */
    String LDAP_ADMIN_SYNC_PASSWORD = "ldap.admin.sync.password";

    /** The key of the configuration. e.g. -1 */
    String LDAP_MAX_USERNAME_LENGTH = "ldap.max.username.length";

    /** The key of the configuration. e.g. true */
    String LDAP_ROLE_SEARCH_USER_ENABLED = "ldap.role.search.user.enabled";

    /** The key of the configuration. e.g. true */
    String LDAP_ROLE_SEARCH_GROUP_ENABLED = "ldap.role.search.group.enabled";

    /** The key of the configuration. e.g. true */
    String LDAP_ROLE_SEARCH_ROLE_ENABLED = "ldap.role.search.role.enabled";

    /** The key of the configuration. e.g. sn */
    String LDAP_ATTR_SURNAME = "ldap.attr.surname";

    /** The key of the configuration. e.g. givenName */
    String LDAP_ATTR_GIVEN_NAME = "ldap.attr.givenName";

    /** The key of the configuration. e.g. employeeNumber */
    String LDAP_ATTR_EMPLOYEE_NUMBER = "ldap.attr.employeeNumber";

    /** The key of the configuration. e.g. mail */
    String LDAP_ATTR_MAIL = "ldap.attr.mail";

    /** The key of the configuration. e.g. telephoneNumber */
    String LDAP_ATTR_TELEPHONE_NUMBER = "ldap.attr.telephoneNumber";

    /** The key of the configuration. e.g. homePhone */
    String LDAP_ATTR_HOME_PHONE = "ldap.attr.homePhone";

    /** The key of the configuration. e.g. homePostalAddress */
    String LDAP_ATTR_HOME_POSTAL_ADDRESS = "ldap.attr.homePostalAddress";

    /** The key of the configuration. e.g. labeledURI */
    String LDAP_ATTR_LABELEDURI = "ldap.attr.labeledURI";

    /** The key of the configuration. e.g. roomNumber */
    String LDAP_ATTR_ROOM_NUMBER = "ldap.attr.roomNumber";

    /** The key of the configuration. e.g. description */
    String LDAP_ATTR_DESCRIPTION = "ldap.attr.description";

    /** The key of the configuration. e.g. title */
    String LDAP_ATTR_TITLE = "ldap.attr.title";

    /** The key of the configuration. e.g. pager */
    String LDAP_ATTR_PAGER = "ldap.attr.pager";

    /** The key of the configuration. e.g. street */
    String LDAP_ATTR_STREET = "ldap.attr.street";

    /** The key of the configuration. e.g. postalCode */
    String LDAP_ATTR_POSTAL_CODE = "ldap.attr.postalCode";

    /** The key of the configuration. e.g. physicalDeliveryOfficeName */
    String LDAP_ATTR_PHYSICAL_DELIVERY_OFFICE_NAME = "ldap.attr.physicalDeliveryOfficeName";

    /** The key of the configuration. e.g. destinationIndicator */
    String LDAP_ATTR_DESTINATION_INDICATOR = "ldap.attr.destinationIndicator";

    /** The key of the configuration. e.g. internationaliSDNNumber */
    String LDAP_ATTR_INTERNATIONALISDN_NUMBER = "ldap.attr.internationaliSDNNumber";

    /** The key of the configuration. e.g. st */
    String LDAP_ATTR_STATE = "ldap.attr.state";

    /** The key of the configuration. e.g. employeeType */
    String LDAP_ATTR_EMPLOYEE_TYPE = "ldap.attr.employeeType";

    /** The key of the configuration. e.g. facsimileTelephoneNumber */
    String LDAP_ATTR_FACSIMILE_TELEPHONE_NUMBER = "ldap.attr.facsimileTelephoneNumber";

    /** The key of the configuration. e.g. postOfficeBox */
    String LDAP_ATTR_POST_OFFICE_BOX = "ldap.attr.postOfficeBox";

    /** The key of the configuration. e.g. initials */
    String LDAP_ATTR_INITIALS = "ldap.attr.initials";

    /** The key of the configuration. e.g. carLicense */
    String LDAP_ATTR_CAR_LICENSE = "ldap.attr.carLicense";

    /** The key of the configuration. e.g. mobile */
    String LDAP_ATTR_MOBILE = "ldap.attr.mobile";

    /** The key of the configuration. e.g. postalAddress */
    String LDAP_ATTR_POSTAL_ADDRESS = "ldap.attr.postalAddress";

    /** The key of the configuration. e.g. l */
    String LDAP_ATTR_CITY = "ldap.attr.city";

    /** The key of the configuration. e.g. teletexTerminalIdentifier */
    String LDAP_ATTR_TELETEX_TERMINAL_IDENTIFIER = "ldap.attr.teletexTerminalIdentifier";

    /** The key of the configuration. e.g. x121Address */
    String LDAP_ATTR_X121_ADDRESS = "ldap.attr.x121Address";

    /** The key of the configuration. e.g. businessCategory */
    String LDAP_ATTR_BUSINESS_CATEGORY = "ldap.attr.businessCategory";

    /** The key of the configuration. e.g. registeredAddress */
    String LDAP_ATTR_REGISTERED_ADDRESS = "ldap.attr.registeredAddress";

    /** The key of the configuration. e.g. displayName */
    String LDAP_ATTR_DISPLAY_NAME = "ldap.attr.displayName";

    /** The key of the configuration. e.g. preferredLanguage */
    String LDAP_ATTR_PREFERRED_LANGUAGE = "ldap.attr.preferredLanguage";

    /** The key of the configuration. e.g. departmentNumber */
    String LDAP_ATTR_DEPARTMENT_NUMBER = "ldap.attr.departmentNumber";

    /** The key of the configuration. e.g. uidNumber */
    String LDAP_ATTR_UID_NUMBER = "ldap.attr.uidNumber";

    /** The key of the configuration. e.g. gidNumber */
    String LDAP_ATTR_GID_NUMBER = "ldap.attr.gidNumber";

    /** The key of the configuration. e.g. homeDirectory */
    String LDAP_ATTR_HOME_DIRECTORY = "ldap.attr.homeDirectory";

    /** The key of the configuration. e.g. none */
    String SSO_TYPE = "sso.type";

    /** The key of the configuration. e.g. 0 */
    String SPNEGO_LOGGER_LEVEL = "spnego.logger.level";

    /** The key of the configuration. e.g. krb5.conf */
    String SPNEGO_KRB5_CONF = "spnego.krb5.conf";

    /** The key of the configuration. e.g. auth_login.conf */
    String SPNEGO_LOGIN_CONF = "spnego.login.conf";

    /** The key of the configuration. e.g. username */
    String SPNEGO_PREAUTH_USERNAME = "spnego.preauth.username";

    /** The key of the configuration. e.g. password */
    String SPNEGO_PREAUTH_PASSWORD = "spnego.preauth.password";

    /** The key of the configuration. e.g. spnego-client */
    String SPNEGO_LOGIN_CLIENT_MODULE = "spnego.login.client.module";

    /** The key of the configuration. e.g. spnego-server */
    String SPNEGO_LOGIN_SERVER_MODULE = "spnego.login.server.module";

    /** The key of the configuration. e.g. true */
    String SPNEGO_ALLOW_BASIC = "spnego.allow.basic";

    /** The key of the configuration. e.g. true */
    String SPNEGO_ALLOW_UNSECURE_BASIC = "spnego.allow.unsecure.basic";

    /** The key of the configuration. e.g. true */
    String SPNEGO_PROMPT_NTLM = "spnego.prompt.ntlm";

    /** The key of the configuration. e.g. true */
    String SPNEGO_ALLOW_LOCALHOST = "spnego.allow.localhost";

    /** The key of the configuration. e.g. false */
    String SPNEGO_ALLOW_DELEGATION = "spnego.allow.delegation";

    /** The key of the configuration. e.g. __CLIENT_ID__ */
    String OIC_CLIENT_ID = "oic.client.id";

    /** The key of the configuration. e.g. __CLIENT_SECRET__ */
    String OIC_CLIENT_SECRET = "oic.client.secret";

    /** The key of the configuration. e.g. https://accounts.google.com/o/oauth2/auth */
    String OIC_AUTH_SERVER_URL = "oic.auth.server.url";

    /** The key of the configuration. e.g. http://localhost:8080/sso/ */
    String OIC_REDIRECT_URL = "oic.redirect.url";

    /** The key of the configuration. e.g. openid email */
    String OIC_SCOPE = "oic.scope";

    /** The key of the configuration. e.g. https://accounts.google.com/o/oauth2/token */
    String OIC_TOKEN_SERVER_URL = "oic.token.server.url";

    /** The key of the configuration. e.g. guest */
    String OIC_DEFAULT_ROLES = "oic.default.roles";

    /** The key of the configuration. e.g.  */
    String OIC_DEFAULT_GROUPS = "oic.default.groups";

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
     * Get the value for the key 'elasticsearch.transport.sniff'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getElasticsearchTransportSniff();

    /**
     * Is the property for the key 'elasticsearch.transport.sniff' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isElasticsearchTransportSniff();

    /**
     * Get the value for the key 'elasticsearch.transport.ping_timeout'. <br>
     * The value is, e.g. 1m <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getElasticsearchTransportPingTimeout();

    /**
     * Get the value for the key 'elasticsearch.transport.nodes_sampler_interval'. <br>
     * The value is, e.g. 5s <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getElasticsearchTransportNodesSamplerInterval();

    /**
     * Get the value for the key 'app.cipher.algorism'. <br>
     * The value is, e.g. aes <br>
     * comment: Cryptographer
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAppCipherAlgorism();

    /**
     * Get the value for the key 'app.cipher.key'. <br>
     * The value is, e.g. ___change__me___ <br>
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
    -Xmx512m
    -XX:MaxMetaspaceSize=128m
    -XX:CompressedClassSpaceSize=32m
    -XX:-UseGCOverheadLimit
    -XX:+UseConcMarkSweepGC
    -XX:CMSInitiatingOccupancyFraction=75
    -XX:+UseParNewGC
    -XX:+UseTLAB
    -XX:+DisableExplicitGC
    -XX:+HeapDumpOnOutOfMemoryError
    -XX:-OmitStackTraceInFastThrow
    -Djcifs.smb.client.connTimeout=60000
    -Djcifs.smb.client.soTimeout=35000
    -Djcifs.smb.client.responseTimeout=30000
    -Dgroovy.use.classvalue=true
    <br>
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
    -XX:+DisableExplicitGC
    -XX:+HeapDumpOnOutOfMemoryError
    -Dgroovy.use.classvalue=true
    <br>
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
     * The value is, e.g. return container.getComponent("crawlJob").logLevel("info").sessionId("{3}").webConfigIds([{0}] as String[]).fileConfigIds([{1}] as String[]).dataConfigIds([{2}] as String[]).jobExecutor(executor).execute(); <br>
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
     * Get the value for the key 'supported.uploaded.files'. <br>
     * The value is, e.g. license.properties <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSupportedUploadedFiles();

    /**
     * Get the value for the key 'supported.languages'. <br>
     * The value is, e.g. ar,bg,ca,da,de,el,en,es,eu,fa,fi,fr,ga,gl,hi,hu,hy,id,it,ja,lv,ko,nl,no,pt,ro,ru,sv,th,tr,zh_CN,zh_TW,zh <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSupportedLanguages();

    /**
     * Get the value for the key 'api.access.token.length'. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiAccessTokenLength();

    /**
     * Get the value for the key 'api.access.token.length' as {@link Integer}. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getApiAccessTokenLengthAsInteger();

    /**
     * Get the value for the key 'api.access.token.required'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiAccessTokenRequired();

    /**
     * Is the property for the key 'api.access.token.required' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isApiAccessTokenRequired();

    /**
     * Get the value for the key 'api.admin.access.permissions'. <br>
     * The value is, e.g. Radmin-api <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiAdminAccessPermissions();

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
     * Get the value for the key 'crawler.document.max.alphanum.term.size'. <br>
     * The value is, e.g. 20 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentMaxAlphanumTermSize();

    /**
     * Get the value for the key 'crawler.document.max.alphanum.term.size' as {@link Integer}. <br>
     * The value is, e.g. 20 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentMaxAlphanumTermSizeAsInteger();

    /**
     * Get the value for the key 'crawler.document.max.symbol.term.size'. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentMaxSymbolTermSize();

    /**
     * Get the value for the key 'crawler.document.max.symbol.term.size' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentMaxSymbolTermSizeAsInteger();

    /**
     * Get the value for the key 'crawler.document.duplicate.term.removed'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentDuplicateTermRemoved();

    /**
     * Is the property for the key 'crawler.document.duplicate.term.removed' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerDocumentDuplicateTermRemoved();

    /**
     * Get the value for the key 'crawler.document.space.chars'. <br>
     * The value is, e.g. u0009u000Au000Bu000Cu000Du001Cu001Du001Eu001Fu0020u00A0u1680u180Eu2000u2001u2002u2003u2004u2005u2006u2007u2008u2009u200Au200Bu200Cu202Fu205Fu3000uFEFFuFFFDu00B6 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentSpaceChars();

    /**
     * Get the value for the key 'crawler.crawling.data.encoding'. <br>
     * The value is, e.g. UTF-8 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerCrawlingDataEncoding();

    /**
     * Get the value for the key 'crawler.web.protocols'. <br>
     * The value is, e.g. http,https <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerWebProtocols();

    /**
     * Get the value for the key 'crawler.file.protocols'. <br>
     * The value is, e.g. file,smb,ftp <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerFileProtocols();

    /**
     * Get the value for the key 'crawler.ignore.robots.txt'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerIgnoreRobotsTxt();

    /**
     * Is the property for the key 'crawler.ignore.robots.txt' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerIgnoreRobotsTxt();

    /**
     * Get the value for the key 'crawler.ignore.meta.robots'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerIgnoreMetaRobots();

    /**
     * Is the property for the key 'crawler.ignore.meta.robots' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerIgnoreMetaRobots();

    /**
     * Get the value for the key 'crawler.ignore.content.exception'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerIgnoreContentException();

    /**
     * Is the property for the key 'crawler.ignore.content.exception' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerIgnoreContentException();

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
     * The value is, e.g. noscript,script,style <br>
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
     * Get the value for the key 'crawler.document.file.default.lang'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileDefaultLang();

    /**
     * Get the value for the key 'crawler.document.file.default.lang' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentFileDefaultLangAsInteger();

    /**
     * Get the value for the key 'crawler.document.cache.enabled'. <br>
     * The value is, e.g. true <br>
     * comment: cache
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentCacheEnabled();

    /**
     * Is the property for the key 'crawler.document.cache.enabled' true? <br>
     * The value is, e.g. true <br>
     * comment: cache
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerDocumentCacheEnabled();

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
     * Get the value for the key 'indexer.unprocessed.document.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerUnprocessedDocumentSize();

    /**
     * Get the value for the key 'indexer.unprocessed.document.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerUnprocessedDocumentSizeAsInteger();

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
     * The value is, e.g. 5000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerWebfsCommitMarginTime();

    /**
     * Get the value for the key 'indexer.webfs.commit.margin.time' as {@link Integer}. <br>
     * The value is, e.g. 5000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerWebfsCommitMarginTimeAsInteger();

    /**
     * Get the value for the key 'indexer.webfs.max.empty.list.count'. <br>
     * The value is, e.g. 360 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerWebfsMaxEmptyListCount();

    /**
     * Get the value for the key 'indexer.webfs.max.empty.list.count' as {@link Integer}. <br>
     * The value is, e.g. 360 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerWebfsMaxEmptyListCountAsInteger();

    /**
     * Get the value for the key 'indexer.webfs.update.interval'. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerWebfsUpdateInterval();

    /**
     * Get the value for the key 'indexer.webfs.update.interval' as {@link Integer}. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerWebfsUpdateIntervalAsInteger();

    /**
     * Get the value for the key 'indexer.webfs.max.document.cache.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerWebfsMaxDocumentCacheSize();

    /**
     * Get the value for the key 'indexer.webfs.max.document.cache.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerWebfsMaxDocumentCacheSizeAsInteger();

    /**
     * Get the value for the key 'indexer.webfs.max.document.request.size'. <br>
     * The value is, e.g. 10485760 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerWebfsMaxDocumentRequestSize();

    /**
     * Get the value for the key 'indexer.webfs.max.document.request.size' as {@link Integer}. <br>
     * The value is, e.g. 10485760 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerWebfsMaxDocumentRequestSizeAsInteger();

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
     * Get the value for the key 'indexer.data.max.document.request.size'. <br>
     * The value is, e.g. 10485760 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerDataMaxDocumentRequestSize();

    /**
     * Get the value for the key 'indexer.data.max.document.request.size' as {@link Integer}. <br>
     * The value is, e.g. 10485760 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerDataMaxDocumentRequestSizeAsInteger();

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
     * Get the value for the key 'index.field.version'. <br>
     * The value is, e.g. _version <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldVersion();

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
     * Get the value for the key 'index.field.important_content'. <br>
     * The value is, e.g. important_content <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldImportantContent();

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
     * Get the value for the key 'index.field.filename'. <br>
     * The value is, e.g. filename <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldFilename();

    /**
     * Get the value for the key 'response.field.content_title'. <br>
     * The value is, e.g. content_title <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getResponseFieldContentTitle();

    /**
     * Get the value for the key 'response.field.content_description'. <br>
     * The value is, e.g. content_description <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getResponseFieldContentDescription();

    /**
     * Get the value for the key 'response.field.url_link'. <br>
     * The value is, e.g. url_link <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getResponseFieldUrlLink();

    /**
     * Get the value for the key 'response.field.site_path'. <br>
     * The value is, e.g. site_path <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getResponseFieldSitePath();

    /**
     * Get the value for the key 'index.document.search.index'. <br>
     * The value is, e.g. fess.search <br>
     * comment: document index
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentSearchIndex();

    /**
     * Get the value for the key 'index.document.update.index'. <br>
     * The value is, e.g. fess.update <br>
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
     * Get the value for the key 'index.document.suggest.index'. <br>
     * The value is, e.g. fess <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentSuggestIndex();

    /**
     * Get the value for the key 'index.document.crawler.index'. <br>
     * The value is, e.g. .crawler <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentCrawlerIndex();

    /**
     * Get the value for the key 'index.admin.array.fields'. <br>
     * The value is, e.g. lang,role,label,anchor <br>
     * comment: doc management
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexAdminArrayFields();

    /**
     * Get the value for the key 'index.admin.date.fields'. <br>
     * The value is, e.g. expires,created,timestamp,last_modified <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexAdminDateFields();

    /**
     * Get the value for the key 'index.admin.integer.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexAdminIntegerFields();

    /**
     * Get the value for the key 'index.admin.integer.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexAdminIntegerFieldsAsInteger();

    /**
     * Get the value for the key 'index.admin.long.fields'. <br>
     * The value is, e.g. content_length,favorite_count,click_count <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexAdminLongFields();

    /**
     * Get the value for the key 'index.admin.float.fields'. <br>
     * The value is, e.g. boost <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexAdminFloatFields();

    /**
     * Get the value for the key 'index.admin.double.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexAdminDoubleFields();

    /**
     * Get the value for the key 'index.admin.double.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexAdminDoubleFieldsAsInteger();

    /**
     * Get the value for the key 'index.admin.required.fields'. <br>
     * The value is, e.g. doc_id,url,title,role,boost <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexAdminRequiredFields();

    /**
     * Get the value for the key 'index.search.timeout'. <br>
     * The value is, e.g. 3m <br>
     * comment: timeout
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexSearchTimeout();

    /**
     * Get the value for the key 'index.scroll.search.timeout.timeout'. <br>
     * The value is, e.g. 3m <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexScrollSearchTimeoutTimeout();

    /**
     * Get the value for the key 'index.index.timeout'. <br>
     * The value is, e.g. 3m <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexIndexTimeout();

    /**
     * Get the value for the key 'index.bulk.timeout'. <br>
     * The value is, e.g. 3m <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexBulkTimeout();

    /**
     * Get the value for the key 'index.delete.timeout'. <br>
     * The value is, e.g. 3m <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDeleteTimeout();

    /**
     * Get the value for the key 'index.health.timeout'. <br>
     * The value is, e.g. 10m <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexHealthTimeout();

    /**
     * Get the value for the key 'index.indices.timeout'. <br>
     * The value is, e.g. 1m <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexIndicesTimeout();

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
     * Get the value for the key 'query.geo.fields'. <br>
     * The value is, e.g. location <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryGeoFields();

    /**
     * Get the value for the key 'query.browser.lang.parameter.name'. <br>
     * The value is, e.g. browser_lang <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBrowserLangParameterName();

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
     * Get the value for the key 'query.highlight.fragment.size'. <br>
     * The value is, e.g. 50 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightFragmentSize();

    /**
     * Get the value for the key 'query.highlight.fragment.size' as {@link Integer}. <br>
     * The value is, e.g. 50 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryHighlightFragmentSizeAsInteger();

    /**
     * Get the value for the key 'query.highlight.number.of.fragments'. <br>
     * The value is, e.g. 5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightNumberOfFragments();

    /**
     * Get the value for the key 'query.highlight.number.of.fragments' as {@link Integer}. <br>
     * The value is, e.g. 5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryHighlightNumberOfFragmentsAsInteger();

    /**
     * Get the value for the key 'query.highlight.type'. <br>
     * The value is, e.g. fvh <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightType();

    /**
     * Get the value for the key 'query.max.search.result.offset'. <br>
     * The value is, e.g. 100000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryMaxSearchResultOffset();

    /**
     * Get the value for the key 'query.max.search.result.offset' as {@link Integer}. <br>
     * The value is, e.g. 100000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryMaxSearchResultOffsetAsInteger();

    /**
     * Get the value for the key 'query.additional.response.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryAdditionalResponseFields();

    /**
     * Get the value for the key 'query.additional.response.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryAdditionalResponseFieldsAsInteger();

    /**
     * Get the value for the key 'query.additional.api.response.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryAdditionalApiResponseFields();

    /**
     * Get the value for the key 'query.additional.api.response.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryAdditionalApiResponseFieldsAsInteger();

    /**
     * Get the value for the key 'query.additional.cache.response.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryAdditionalCacheResponseFields();

    /**
     * Get the value for the key 'query.additional.cache.response.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryAdditionalCacheResponseFieldsAsInteger();

    /**
     * Get the value for the key 'query.additional.highlighted.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryAdditionalHighlightedFields();

    /**
     * Get the value for the key 'query.additional.highlighted.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryAdditionalHighlightedFieldsAsInteger();

    /**
     * Get the value for the key 'query.additional.search.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryAdditionalSearchFields();

    /**
     * Get the value for the key 'query.additional.search.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryAdditionalSearchFieldsAsInteger();

    /**
     * Get the value for the key 'query.additional.facet.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryAdditionalFacetFields();

    /**
     * Get the value for the key 'query.additional.facet.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryAdditionalFacetFieldsAsInteger();

    /**
     * Get the value for the key 'query.additional.sort.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryAdditionalSortFields();

    /**
     * Get the value for the key 'query.additional.sort.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryAdditionalSortFieldsAsInteger();

    /**
     * Get the value for the key 'query.additional.not.analyzed.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryAdditionalNotAnalyzedFields();

    /**
     * Get the value for the key 'query.additional.not.analyzed.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryAdditionalNotAnalyzedFieldsAsInteger();

    /**
     * Get the value for the key 'query.default.languages'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryDefaultLanguages();

    /**
     * Get the value for the key 'query.default.languages' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryDefaultLanguagesAsInteger();

    /**
     * Get the value for the key 'query.language.mapping'. <br>
     * The value is, e.g. ar=ar
    bg=bg
    bn=bn
    ca=ca
    cs=cs
    da=da
    de=de
    el=el
    en=en
    es=es
    et=et
    fa=fa
    fi=fi
    fr=fr
    gu=gu
    he=he
    hi=hi
    hr=hr
    hu=hu
    id=id
    it=it
    ja=ja
    ko=ko
    lt=lt
    lv=lv
    mk=mk
    ml=ml
    nl=nl
    no=no
    pa=pa
    pl=pl
    pt=pt
    ro=ro
    ru=ru
    si=si
    sq=sq
    sv=sv
    ta=ta
    te=te
    th=th
    tl=tl
    tr=tr
    uk=uk
    ur=ur
    vi=vi
    zh=zh-cn
    zh-cn=zh-cn
    zh-tw=zh-tw
    <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryLanguageMapping();

    /**
     * Get the value for the key 'query.boost.title'. <br>
     * The value is, e.g. 0.2 <br>
     * comment: boost
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostTitle();

    /**
     * Get the value for the key 'query.boost.title' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 0.2 <br>
     * comment: boost
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostTitleAsDecimal();

    /**
     * Get the value for the key 'query.boost.title.lang'. <br>
     * The value is, e.g. 1.0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostTitleLang();

    /**
     * Get the value for the key 'query.boost.title.lang' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 1.0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostTitleLangAsDecimal();

    /**
     * Get the value for the key 'query.boost.content'. <br>
     * The value is, e.g. 0.1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostContent();

    /**
     * Get the value for the key 'query.boost.content' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 0.1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostContentAsDecimal();

    /**
     * Get the value for the key 'query.boost.content.lang'. <br>
     * The value is, e.g. 0.5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostContentLang();

    /**
     * Get the value for the key 'query.boost.content.lang' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 0.5 <br>
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
     * The value is, e.g. .fess_basic_config.bulk,.fess_config.bulk,.fess_user.bulk,system.properties <br>
     * comment: backup
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexBackupTargets();

    /**
     * Get the value for the key 'form.admin.max.input.size'. <br>
     * The value is, e.g. 4000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getFormAdminMaxInputSize();

    /**
     * Get the value for the key 'form.admin.max.input.size' as {@link Integer}. <br>
     * The value is, e.g. 4000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getFormAdminMaxInputSizeAsInteger();

    /**
     * Get the value for the key 'authentication.admin.users'. <br>
     * The value is, e.g. admin <br>
     * comment: ------
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAuthenticationAdminUsers();

    /**
     * Get the value for the key 'authentication.admin.roles'. <br>
     * The value is, e.g. admin <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAuthenticationAdminRoles();

    /**
     * Get the value for the key 'role.search.default.permissions'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getRoleSearchDefaultPermissions();

    /**
     * Get the value for the key 'role.search.default.permissions' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getRoleSearchDefaultPermissionsAsInteger();

    /**
     * Get the value for the key 'role.search.default.display.permissions'. <br>
     * The value is, e.g. {role}guest <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getRoleSearchDefaultDisplayPermissions();

    /**
     * Get the value for the key 'role.search.guest.permissions'. <br>
     * The value is, e.g. {role}guest <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getRoleSearchGuestPermissions();

    /**
     * Get the value for the key 'role.search.user.prefix'. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getRoleSearchUserPrefix();

    /**
     * Get the value for the key 'role.search.user.prefix' as {@link Integer}. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getRoleSearchUserPrefixAsInteger();

    /**
     * Get the value for the key 'role.search.group.prefix'. <br>
     * The value is, e.g. 2 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getRoleSearchGroupPrefix();

    /**
     * Get the value for the key 'role.search.group.prefix' as {@link Integer}. <br>
     * The value is, e.g. 2 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getRoleSearchGroupPrefixAsInteger();

    /**
     * Get the value for the key 'role.search.role.prefix'. <br>
     * The value is, e.g. R <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getRoleSearchRolePrefix();

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
     * The value is, e.g. 25 <br>
     * comment: The size of one page for paging
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPagingPageSize();

    /**
     * Get the value for the key 'paging.page.size' as {@link Integer}. <br>
     * The value is, e.g. 25 <br>
     * comment: The size of one page for paging
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPagingPageSizeAsInteger();

    /**
     * Get the value for the key 'paging.page.range.size'. <br>
     * The value is, e.g. 5 <br>
     * comment: The size of page range for paging
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPagingPageRangeSize();

    /**
     * Get the value for the key 'paging.page.range.size' as {@link Integer}. <br>
     * The value is, e.g. 5 <br>
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
     * Get the value for the key 'page.user.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageUserMaxFetchSize();

    /**
     * Get the value for the key 'page.user.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageUserMaxFetchSizeAsInteger();

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
     * Get the value for the key 'page.crawling.info.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageCrawlingInfoMaxFetchSize();

    /**
     * Get the value for the key 'page.crawling.info.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageCrawlingInfoMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.data.config.max.fetch.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageDataConfigMaxFetchSize();

    /**
     * Get the value for the key 'page.data.config.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageDataConfigMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.web.config.max.fetch.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageWebConfigMaxFetchSize();

    /**
     * Get the value for the key 'page.web.config.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageWebConfigMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.file.config.max.fetch.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageFileConfigMaxFetchSize();

    /**
     * Get the value for the key 'page.file.config.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageFileConfigMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.duplicate.host.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageDuplicateHostMaxFetchSize();

    /**
     * Get the value for the key 'page.duplicate.host.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageDuplicateHostMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.failure.url.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageFailureUrlMaxFetchSize();

    /**
     * Get the value for the key 'page.failure.url.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageFailureUrlMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.favorite.log.max.fetch.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageFavoriteLogMaxFetchSize();

    /**
     * Get the value for the key 'page.favorite.log.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageFavoriteLogMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.file.auth.max.fetch.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageFileAuthMaxFetchSize();

    /**
     * Get the value for the key 'page.file.auth.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageFileAuthMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.web.auth.max.fetch.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageWebAuthMaxFetchSize();

    /**
     * Get the value for the key 'page.web.auth.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageWebAuthMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.path.mapping.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPagePathMappingMaxFetchSize();

    /**
     * Get the value for the key 'page.path.mapping.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPagePathMappingMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.request.header.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageRequestHeaderMaxFetchSize();

    /**
     * Get the value for the key 'page.request.header.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageRequestHeaderMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.scheduled.job.max.fetch.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageScheduledJobMaxFetchSize();

    /**
     * Get the value for the key 'page.scheduled.job.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageScheduledJobMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.search.field.log.max.fetch.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageSearchFieldLogMaxFetchSize();

    /**
     * Get the value for the key 'page.search.field.log.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageSearchFieldLogMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.elevate.word.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageElevateWordMaxFetchSize();

    /**
     * Get the value for the key 'page.elevate.word.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageElevateWordMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.bad.word.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageBadWordMaxFetchSize();

    /**
     * Get the value for the key 'page.bad.word.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageBadWordMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.dictionary.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageDictionaryMaxFetchSize();

    /**
     * Get the value for the key 'page.dictionary.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageDictionaryMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.thumbnail.queue.max.fetch.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageThumbnailQueueMaxFetchSize();

    /**
     * Get the value for the key 'page.thumbnail.queue.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageThumbnailQueueMaxFetchSizeAsInteger();

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
     * Get the value for the key 'thumbnail.html.phantomjs.enabled'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlPhantomjsEnabled();

    /**
     * Is the property for the key 'thumbnail.html.phantomjs.enabled' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isThumbnailHtmlPhantomjsEnabled();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.max.height'. <br>
     * The value is, e.g. 20000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlPhantomjsMaxHeight();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.max.height' as {@link Integer}. <br>
     * The value is, e.g. 20000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getThumbnailHtmlPhantomjsMaxHeightAsInteger();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.keep.alive'. <br>
     * The value is, e.g. 600000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlPhantomjsKeepAlive();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.keep.alive' as {@link Integer}. <br>
     * The value is, e.g. 600000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getThumbnailHtmlPhantomjsKeepAliveAsInteger();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.window.width'. <br>
     * The value is, e.g. 1200 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlPhantomjsWindowWidth();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.window.width' as {@link Integer}. <br>
     * The value is, e.g. 1200 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getThumbnailHtmlPhantomjsWindowWidthAsInteger();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.window.height'. <br>
     * The value is, e.g. 800 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlPhantomjsWindowHeight();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.window.height' as {@link Integer}. <br>
     * The value is, e.g. 800 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getThumbnailHtmlPhantomjsWindowHeightAsInteger();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.thumbnail.width'. <br>
     * The value is, e.g. 160 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlPhantomjsThumbnailWidth();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.thumbnail.width' as {@link Integer}. <br>
     * The value is, e.g. 160 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getThumbnailHtmlPhantomjsThumbnailWidthAsInteger();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.thumbnail.height'. <br>
     * The value is, e.g. 160 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlPhantomjsThumbnailHeight();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.thumbnail.height' as {@link Integer}. <br>
     * The value is, e.g. 160 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getThumbnailHtmlPhantomjsThumbnailHeightAsInteger();

    /**
     * Get the value for the key 'thumbnail.html.phantomjs.format'. <br>
     * The value is, e.g. png <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlPhantomjsFormat();

    /**
     * Get the value for the key 'thumbnail.generator.targets'. <br>
     * The value is, e.g. all <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailGeneratorTargets();

    /**
     * Get the value for the key 'thumbnail.crawler.enabled'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailCrawlerEnabled();

    /**
     * Is the property for the key 'thumbnail.crawler.enabled' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isThumbnailCrawlerEnabled();

    /**
     * Get the value for the key 'user.code.request.parameter'. <br>
     * The value is, e.g. userCode <br>
     * comment: user
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getUserCodeRequestParameter();

    /**
     * Get the value for the key 'user.code.min.length'. <br>
     * The value is, e.g. 20 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getUserCodeMinLength();

    /**
     * Get the value for the key 'user.code.min.length' as {@link Integer}. <br>
     * The value is, e.g. 20 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getUserCodeMinLengthAsInteger();

    /**
     * Get the value for the key 'user.code.max.length'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getUserCodeMaxLength();

    /**
     * Get the value for the key 'user.code.max.length' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getUserCodeMaxLengthAsInteger();

    /**
     * Get the value for the key 'user.code.pattern'. <br>
     * The value is, e.g. [a-zA-Z0-9_]+ <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getUserCodePattern();

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
     * Get the value for the key 'scheduler.monitor.interval'. <br>
     * The value is, e.g. 30 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSchedulerMonitorInterval();

    /**
     * Get the value for the key 'scheduler.monitor.interval' as {@link Integer}. <br>
     * The value is, e.g. 30 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSchedulerMonitorIntervalAsInteger();

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
     * Get the value for the key 'online.help.name.dict.seunjeon'. <br>
     * The value is, e.g. seunjeon <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDictSeunjeon();

    /**
     * Get the value for the key 'online.help.name.dict.protwords'. <br>
     * The value is, e.g. protwords <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDictProtwords();

    /**
     * Get the value for the key 'online.help.name.dict.mapping'. <br>
     * The value is, e.g. mapping <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDictMapping();

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
     * Get the value for the key 'online.help.name.upgrade'. <br>
     * The value is, e.g. upgrade <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameUpgrade();

    /**
     * Get the value for the key 'online.help.name.esreq'. <br>
     * The value is, e.g. esreq <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameEsreq();

    /**
     * Get the value for the key 'online.help.name.accesstoken'. <br>
     * The value is, e.g. accesstoken <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameAccesstoken();

    /**
     * Get the value for the key 'online.help.name.suggest'. <br>
     * The value is, e.g. suggest <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameSuggest();

    /**
     * Get the value for the key 'online.help.supported.langs'. <br>
     * The value is, e.g. ja <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpSupportedLangs();

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
     * Get the value for the key 'suggest.update.contents.limit.num.percentage'. <br>
     * The value is, e.g. 50% <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestUpdateContentsLimitNumPercentage();

    /**
     * Get the value for the key 'suggest.update.contents.limit.num'. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestUpdateContentsLimitNum();

    /**
     * Get the value for the key 'suggest.update.contents.limit.num' as {@link Integer}. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestUpdateContentsLimitNumAsInteger();

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
     * Get the value for the key 'suggest.search.log.permissions'. <br>
     * The value is, e.g. {user}guest,{role}guest <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestSearchLogPermissions();

    /**
     * Get the value for the key 'ldap.admin.enabled'. <br>
     * The value is, e.g. false <br>
     * comment: ------
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAdminEnabled();

    /**
     * Is the property for the key 'ldap.admin.enabled' true? <br>
     * The value is, e.g. false <br>
     * comment: ------
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isLdapAdminEnabled();

    /**
     * Get the value for the key 'ldap.admin.user.filter'. <br>
     * The value is, e.g. uid=%s <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAdminUserFilter();

    /**
     * Get the value for the key 'ldap.admin.user.base.dn'. <br>
     * The value is, e.g. ou=People,dc=fess,dc=codelibs,dc=org <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAdminUserBaseDn();

    /**
     * Get the value for the key 'ldap.admin.user.object.classes'. <br>
     * The value is, e.g. organizationalPerson,top,person,inetOrgPerson <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAdminUserObjectClasses();

    /**
     * Get the value for the key 'ldap.admin.role.filter'. <br>
     * The value is, e.g. cn=%s <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAdminRoleFilter();

    /**
     * Get the value for the key 'ldap.admin.role.base.dn'. <br>
     * The value is, e.g. ou=Role,dc=fess,dc=codelibs,dc=org <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAdminRoleBaseDn();

    /**
     * Get the value for the key 'ldap.admin.role.object.classes'. <br>
     * The value is, e.g. groupOfNames <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAdminRoleObjectClasses();

    /**
     * Get the value for the key 'ldap.admin.group.filter'. <br>
     * The value is, e.g. cn=%s <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAdminGroupFilter();

    /**
     * Get the value for the key 'ldap.admin.group.base.dn'. <br>
     * The value is, e.g. ou=Group,dc=fess,dc=codelibs,dc=org <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAdminGroupBaseDn();

    /**
     * Get the value for the key 'ldap.admin.group.object.classes'. <br>
     * The value is, e.g. groupOfNames <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAdminGroupObjectClasses();

    /**
     * Get the value for the key 'ldap.admin.sync.password'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAdminSyncPassword();

    /**
     * Is the property for the key 'ldap.admin.sync.password' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isLdapAdminSyncPassword();

    /**
     * Get the value for the key 'ldap.max.username.length'. <br>
     * The value is, e.g. -1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapMaxUsernameLength();

    /**
     * Get the value for the key 'ldap.max.username.length' as {@link Integer}. <br>
     * The value is, e.g. -1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getLdapMaxUsernameLengthAsInteger();

    /**
     * Get the value for the key 'ldap.role.search.user.enabled'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapRoleSearchUserEnabled();

    /**
     * Is the property for the key 'ldap.role.search.user.enabled' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isLdapRoleSearchUserEnabled();

    /**
     * Get the value for the key 'ldap.role.search.group.enabled'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapRoleSearchGroupEnabled();

    /**
     * Is the property for the key 'ldap.role.search.group.enabled' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isLdapRoleSearchGroupEnabled();

    /**
     * Get the value for the key 'ldap.role.search.role.enabled'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapRoleSearchRoleEnabled();

    /**
     * Is the property for the key 'ldap.role.search.role.enabled' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isLdapRoleSearchRoleEnabled();

    /**
     * Get the value for the key 'ldap.attr.surname'. <br>
     * The value is, e.g. sn <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrSurname();

    /**
     * Get the value for the key 'ldap.attr.givenName'. <br>
     * The value is, e.g. givenName <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrGivenName();

    /**
     * Get the value for the key 'ldap.attr.employeeNumber'. <br>
     * The value is, e.g. employeeNumber <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrEmployeeNumber();

    /**
     * Get the value for the key 'ldap.attr.mail'. <br>
     * The value is, e.g. mail <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrMail();

    /**
     * Get the value for the key 'ldap.attr.telephoneNumber'. <br>
     * The value is, e.g. telephoneNumber <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrTelephoneNumber();

    /**
     * Get the value for the key 'ldap.attr.homePhone'. <br>
     * The value is, e.g. homePhone <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrHomePhone();

    /**
     * Get the value for the key 'ldap.attr.homePostalAddress'. <br>
     * The value is, e.g. homePostalAddress <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrHomePostalAddress();

    /**
     * Get the value for the key 'ldap.attr.labeledURI'. <br>
     * The value is, e.g. labeledURI <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrLabeleduri();

    /**
     * Get the value for the key 'ldap.attr.roomNumber'. <br>
     * The value is, e.g. roomNumber <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrRoomNumber();

    /**
     * Get the value for the key 'ldap.attr.description'. <br>
     * The value is, e.g. description <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrDescription();

    /**
     * Get the value for the key 'ldap.attr.title'. <br>
     * The value is, e.g. title <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrTitle();

    /**
     * Get the value for the key 'ldap.attr.pager'. <br>
     * The value is, e.g. pager <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrPager();

    /**
     * Get the value for the key 'ldap.attr.street'. <br>
     * The value is, e.g. street <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrStreet();

    /**
     * Get the value for the key 'ldap.attr.postalCode'. <br>
     * The value is, e.g. postalCode <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrPostalCode();

    /**
     * Get the value for the key 'ldap.attr.physicalDeliveryOfficeName'. <br>
     * The value is, e.g. physicalDeliveryOfficeName <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrPhysicalDeliveryOfficeName();

    /**
     * Get the value for the key 'ldap.attr.destinationIndicator'. <br>
     * The value is, e.g. destinationIndicator <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrDestinationIndicator();

    /**
     * Get the value for the key 'ldap.attr.internationaliSDNNumber'. <br>
     * The value is, e.g. internationaliSDNNumber <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrInternationalisdnNumber();

    /**
     * Get the value for the key 'ldap.attr.state'. <br>
     * The value is, e.g. st <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrState();

    /**
     * Get the value for the key 'ldap.attr.employeeType'. <br>
     * The value is, e.g. employeeType <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrEmployeeType();

    /**
     * Get the value for the key 'ldap.attr.facsimileTelephoneNumber'. <br>
     * The value is, e.g. facsimileTelephoneNumber <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrFacsimileTelephoneNumber();

    /**
     * Get the value for the key 'ldap.attr.postOfficeBox'. <br>
     * The value is, e.g. postOfficeBox <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrPostOfficeBox();

    /**
     * Get the value for the key 'ldap.attr.initials'. <br>
     * The value is, e.g. initials <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrInitials();

    /**
     * Get the value for the key 'ldap.attr.carLicense'. <br>
     * The value is, e.g. carLicense <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrCarLicense();

    /**
     * Get the value for the key 'ldap.attr.mobile'. <br>
     * The value is, e.g. mobile <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrMobile();

    /**
     * Get the value for the key 'ldap.attr.postalAddress'. <br>
     * The value is, e.g. postalAddress <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrPostalAddress();

    /**
     * Get the value for the key 'ldap.attr.city'. <br>
     * The value is, e.g. l <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrCity();

    /**
     * Get the value for the key 'ldap.attr.teletexTerminalIdentifier'. <br>
     * The value is, e.g. teletexTerminalIdentifier <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrTeletexTerminalIdentifier();

    /**
     * Get the value for the key 'ldap.attr.x121Address'. <br>
     * The value is, e.g. x121Address <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrX121Address();

    /**
     * Get the value for the key 'ldap.attr.businessCategory'. <br>
     * The value is, e.g. businessCategory <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrBusinessCategory();

    /**
     * Get the value for the key 'ldap.attr.registeredAddress'. <br>
     * The value is, e.g. registeredAddress <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrRegisteredAddress();

    /**
     * Get the value for the key 'ldap.attr.displayName'. <br>
     * The value is, e.g. displayName <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrDisplayName();

    /**
     * Get the value for the key 'ldap.attr.preferredLanguage'. <br>
     * The value is, e.g. preferredLanguage <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrPreferredLanguage();

    /**
     * Get the value for the key 'ldap.attr.departmentNumber'. <br>
     * The value is, e.g. departmentNumber <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrDepartmentNumber();

    /**
     * Get the value for the key 'ldap.attr.uidNumber'. <br>
     * The value is, e.g. uidNumber <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrUidNumber();

    /**
     * Get the value for the key 'ldap.attr.gidNumber'. <br>
     * The value is, e.g. gidNumber <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrGidNumber();

    /**
     * Get the value for the key 'ldap.attr.homeDirectory'. <br>
     * The value is, e.g. homeDirectory <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAttrHomeDirectory();

    /**
     * Get the value for the key 'sso.type'. <br>
     * The value is, e.g. none <br>
     * comment: ------
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSsoType();

    /**
     * Get the value for the key 'spnego.logger.level'. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSpnegoLoggerLevel();

    /**
     * Get the value for the key 'spnego.logger.level' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSpnegoLoggerLevelAsInteger();

    /**
     * Get the value for the key 'spnego.krb5.conf'. <br>
     * The value is, e.g. krb5.conf <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSpnegoKrb5Conf();

    /**
     * Get the value for the key 'spnego.login.conf'. <br>
     * The value is, e.g. auth_login.conf <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSpnegoLoginConf();

    /**
     * Get the value for the key 'spnego.preauth.username'. <br>
     * The value is, e.g. username <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSpnegoPreauthUsername();

    /**
     * Get the value for the key 'spnego.preauth.password'. <br>
     * The value is, e.g. password <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSpnegoPreauthPassword();

    /**
     * Get the value for the key 'spnego.login.client.module'. <br>
     * The value is, e.g. spnego-client <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSpnegoLoginClientModule();

    /**
     * Get the value for the key 'spnego.login.server.module'. <br>
     * The value is, e.g. spnego-server <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSpnegoLoginServerModule();

    /**
     * Get the value for the key 'spnego.allow.basic'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSpnegoAllowBasic();

    /**
     * Is the property for the key 'spnego.allow.basic' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isSpnegoAllowBasic();

    /**
     * Get the value for the key 'spnego.allow.unsecure.basic'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSpnegoAllowUnsecureBasic();

    /**
     * Is the property for the key 'spnego.allow.unsecure.basic' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isSpnegoAllowUnsecureBasic();

    /**
     * Get the value for the key 'spnego.prompt.ntlm'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSpnegoPromptNtlm();

    /**
     * Is the property for the key 'spnego.prompt.ntlm' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isSpnegoPromptNtlm();

    /**
     * Get the value for the key 'spnego.allow.localhost'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSpnegoAllowLocalhost();

    /**
     * Is the property for the key 'spnego.allow.localhost' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isSpnegoAllowLocalhost();

    /**
     * Get the value for the key 'spnego.allow.delegation'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSpnegoAllowDelegation();

    /**
     * Is the property for the key 'spnego.allow.delegation' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isSpnegoAllowDelegation();

    /**
     * Get the value for the key 'oic.client.id'. <br>
     * The value is, e.g. __CLIENT_ID__ <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOicClientId();

    /**
     * Get the value for the key 'oic.client.secret'. <br>
     * The value is, e.g. __CLIENT_SECRET__ <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOicClientSecret();

    /**
     * Get the value for the key 'oic.auth.server.url'. <br>
     * The value is, e.g. https://accounts.google.com/o/oauth2/auth <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOicAuthServerUrl();

    /**
     * Get the value for the key 'oic.redirect.url'. <br>
     * The value is, e.g. http://localhost:8080/sso/ <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOicRedirectUrl();

    /**
     * Get the value for the key 'oic.scope'. <br>
     * The value is, e.g. openid email <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOicScope();

    /**
     * Get the value for the key 'oic.token.server.url'. <br>
     * The value is, e.g. https://accounts.google.com/o/oauth2/token <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOicTokenServerUrl();

    /**
     * Get the value for the key 'oic.default.roles'. <br>
     * The value is, e.g. guest <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOicDefaultRoles();

    /**
     * Get the value for the key 'oic.default.groups'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOicDefaultGroups();

    /**
     * Get the value for the key 'oic.default.groups' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getOicDefaultGroupsAsInteger();

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

        public String getElasticsearchTransportSniff() {
            return get(FessConfig.ELASTICSEARCH_TRANSPORT_SNIFF);
        }

        public boolean isElasticsearchTransportSniff() {
            return is(FessConfig.ELASTICSEARCH_TRANSPORT_SNIFF);
        }

        public String getElasticsearchTransportPingTimeout() {
            return get(FessConfig.ELASTICSEARCH_TRANSPORT_ping_timeout);
        }

        public String getElasticsearchTransportNodesSamplerInterval() {
            return get(FessConfig.ELASTICSEARCH_TRANSPORT_nodes_sampler_interval);
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

        public String getSupportedUploadedFiles() {
            return get(FessConfig.SUPPORTED_UPLOADED_FILES);
        }

        public String getSupportedLanguages() {
            return get(FessConfig.SUPPORTED_LANGUAGES);
        }

        public String getApiAccessTokenLength() {
            return get(FessConfig.API_ACCESS_TOKEN_LENGTH);
        }

        public Integer getApiAccessTokenLengthAsInteger() {
            return getAsInteger(FessConfig.API_ACCESS_TOKEN_LENGTH);
        }

        public String getApiAccessTokenRequired() {
            return get(FessConfig.API_ACCESS_TOKEN_REQUIRED);
        }

        public boolean isApiAccessTokenRequired() {
            return is(FessConfig.API_ACCESS_TOKEN_REQUIRED);
        }

        public String getApiAdminAccessPermissions() {
            return get(FessConfig.API_ADMIN_ACCESS_PERMISSIONS);
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

        public String getCrawlerDocumentMaxAlphanumTermSize() {
            return get(FessConfig.CRAWLER_DOCUMENT_MAX_ALPHANUM_TERM_SIZE);
        }

        public Integer getCrawlerDocumentMaxAlphanumTermSizeAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_MAX_ALPHANUM_TERM_SIZE);
        }

        public String getCrawlerDocumentMaxSymbolTermSize() {
            return get(FessConfig.CRAWLER_DOCUMENT_MAX_SYMBOL_TERM_SIZE);
        }

        public Integer getCrawlerDocumentMaxSymbolTermSizeAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_MAX_SYMBOL_TERM_SIZE);
        }

        public String getCrawlerDocumentDuplicateTermRemoved() {
            return get(FessConfig.CRAWLER_DOCUMENT_DUPLICATE_TERM_REMOVED);
        }

        public boolean isCrawlerDocumentDuplicateTermRemoved() {
            return is(FessConfig.CRAWLER_DOCUMENT_DUPLICATE_TERM_REMOVED);
        }

        public String getCrawlerDocumentSpaceChars() {
            return get(FessConfig.CRAWLER_DOCUMENT_SPACE_CHARS);
        }

        public String getCrawlerCrawlingDataEncoding() {
            return get(FessConfig.CRAWLER_CRAWLING_DATA_ENCODING);
        }

        public String getCrawlerWebProtocols() {
            return get(FessConfig.CRAWLER_WEB_PROTOCOLS);
        }

        public String getCrawlerFileProtocols() {
            return get(FessConfig.CRAWLER_FILE_PROTOCOLS);
        }

        public String getCrawlerIgnoreRobotsTxt() {
            return get(FessConfig.CRAWLER_IGNORE_ROBOTS_TXT);
        }

        public boolean isCrawlerIgnoreRobotsTxt() {
            return is(FessConfig.CRAWLER_IGNORE_ROBOTS_TXT);
        }

        public String getCrawlerIgnoreMetaRobots() {
            return get(FessConfig.CRAWLER_IGNORE_META_ROBOTS);
        }

        public boolean isCrawlerIgnoreMetaRobots() {
            return is(FessConfig.CRAWLER_IGNORE_META_ROBOTS);
        }

        public String getCrawlerIgnoreContentException() {
            return get(FessConfig.CRAWLER_IGNORE_CONTENT_EXCEPTION);
        }

        public boolean isCrawlerIgnoreContentException() {
            return is(FessConfig.CRAWLER_IGNORE_CONTENT_EXCEPTION);
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

        public String getCrawlerDocumentFileDefaultLang() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_LANG);
        }

        public Integer getCrawlerDocumentFileDefaultLangAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_LANG);
        }

        public String getCrawlerDocumentCacheEnabled() {
            return get(FessConfig.CRAWLER_DOCUMENT_CACHE_ENABLED);
        }

        public boolean isCrawlerDocumentCacheEnabled() {
            return is(FessConfig.CRAWLER_DOCUMENT_CACHE_ENABLED);
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

        public String getIndexerUnprocessedDocumentSize() {
            return get(FessConfig.INDEXER_UNPROCESSED_DOCUMENT_SIZE);
        }

        public Integer getIndexerUnprocessedDocumentSizeAsInteger() {
            return getAsInteger(FessConfig.INDEXER_UNPROCESSED_DOCUMENT_SIZE);
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

        public String getIndexerWebfsMaxEmptyListCount() {
            return get(FessConfig.INDEXER_WEBFS_MAX_EMPTY_LIST_COUNT);
        }

        public Integer getIndexerWebfsMaxEmptyListCountAsInteger() {
            return getAsInteger(FessConfig.INDEXER_WEBFS_MAX_EMPTY_LIST_COUNT);
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

        public String getIndexerWebfsMaxDocumentRequestSize() {
            return get(FessConfig.INDEXER_WEBFS_MAX_DOCUMENT_REQUEST_SIZE);
        }

        public Integer getIndexerWebfsMaxDocumentRequestSizeAsInteger() {
            return getAsInteger(FessConfig.INDEXER_WEBFS_MAX_DOCUMENT_REQUEST_SIZE);
        }

        public String getIndexerDataMaxDocumentCacheSize() {
            return get(FessConfig.INDEXER_DATA_MAX_DOCUMENT_CACHE_SIZE);
        }

        public Integer getIndexerDataMaxDocumentCacheSizeAsInteger() {
            return getAsInteger(FessConfig.INDEXER_DATA_MAX_DOCUMENT_CACHE_SIZE);
        }

        public String getIndexerDataMaxDocumentRequestSize() {
            return get(FessConfig.INDEXER_DATA_MAX_DOCUMENT_REQUEST_SIZE);
        }

        public Integer getIndexerDataMaxDocumentRequestSizeAsInteger() {
            return getAsInteger(FessConfig.INDEXER_DATA_MAX_DOCUMENT_REQUEST_SIZE);
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

        public String getIndexFieldVersion() {
            return get(FessConfig.INDEX_FIELD_VERSION);
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

        public String getIndexFieldImportantContent() {
            return get(FessConfig.INDEX_FIELD_important_content);
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

        public String getIndexFieldFilename() {
            return get(FessConfig.INDEX_FIELD_FILENAME);
        }

        public String getResponseFieldContentTitle() {
            return get(FessConfig.RESPONSE_FIELD_content_title);
        }

        public String getResponseFieldContentDescription() {
            return get(FessConfig.RESPONSE_FIELD_content_description);
        }

        public String getResponseFieldUrlLink() {
            return get(FessConfig.RESPONSE_FIELD_url_link);
        }

        public String getResponseFieldSitePath() {
            return get(FessConfig.RESPONSE_FIELD_site_path);
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

        public String getIndexDocumentSuggestIndex() {
            return get(FessConfig.INDEX_DOCUMENT_SUGGEST_INDEX);
        }

        public String getIndexDocumentCrawlerIndex() {
            return get(FessConfig.INDEX_DOCUMENT_CRAWLER_INDEX);
        }

        public String getIndexAdminArrayFields() {
            return get(FessConfig.INDEX_ADMIN_ARRAY_FIELDS);
        }

        public String getIndexAdminDateFields() {
            return get(FessConfig.INDEX_ADMIN_DATE_FIELDS);
        }

        public String getIndexAdminIntegerFields() {
            return get(FessConfig.INDEX_ADMIN_INTEGER_FIELDS);
        }

        public Integer getIndexAdminIntegerFieldsAsInteger() {
            return getAsInteger(FessConfig.INDEX_ADMIN_INTEGER_FIELDS);
        }

        public String getIndexAdminLongFields() {
            return get(FessConfig.INDEX_ADMIN_LONG_FIELDS);
        }

        public String getIndexAdminFloatFields() {
            return get(FessConfig.INDEX_ADMIN_FLOAT_FIELDS);
        }

        public String getIndexAdminDoubleFields() {
            return get(FessConfig.INDEX_ADMIN_DOUBLE_FIELDS);
        }

        public Integer getIndexAdminDoubleFieldsAsInteger() {
            return getAsInteger(FessConfig.INDEX_ADMIN_DOUBLE_FIELDS);
        }

        public String getIndexAdminRequiredFields() {
            return get(FessConfig.INDEX_ADMIN_REQUIRED_FIELDS);
        }

        public String getIndexSearchTimeout() {
            return get(FessConfig.INDEX_SEARCH_TIMEOUT);
        }

        public String getIndexScrollSearchTimeoutTimeout() {
            return get(FessConfig.INDEX_SCROLL_SEARCH_TIMEOUT_TIMEOUT);
        }

        public String getIndexIndexTimeout() {
            return get(FessConfig.INDEX_INDEX_TIMEOUT);
        }

        public String getIndexBulkTimeout() {
            return get(FessConfig.INDEX_BULK_TIMEOUT);
        }

        public String getIndexDeleteTimeout() {
            return get(FessConfig.INDEX_DELETE_TIMEOUT);
        }

        public String getIndexHealthTimeout() {
            return get(FessConfig.INDEX_HEALTH_TIMEOUT);
        }

        public String getIndexIndicesTimeout() {
            return get(FessConfig.INDEX_INDICES_TIMEOUT);
        }

        public String getQueryMaxLength() {
            return get(FessConfig.QUERY_MAX_LENGTH);
        }

        public Integer getQueryMaxLengthAsInteger() {
            return getAsInteger(FessConfig.QUERY_MAX_LENGTH);
        }

        public String getQueryGeoFields() {
            return get(FessConfig.QUERY_GEO_FIELDS);
        }

        public String getQueryBrowserLangParameterName() {
            return get(FessConfig.QUERY_BROWSER_LANG_PARAMETER_NAME);
        }

        public String getQueryReplaceTermWithPrefixQuery() {
            return get(FessConfig.QUERY_REPLACE_TERM_WITH_PREFIX_QUERY);
        }

        public boolean isQueryReplaceTermWithPrefixQuery() {
            return is(FessConfig.QUERY_REPLACE_TERM_WITH_PREFIX_QUERY);
        }

        public String getQueryHighlightFragmentSize() {
            return get(FessConfig.QUERY_HIGHLIGHT_FRAGMENT_SIZE);
        }

        public Integer getQueryHighlightFragmentSizeAsInteger() {
            return getAsInteger(FessConfig.QUERY_HIGHLIGHT_FRAGMENT_SIZE);
        }

        public String getQueryHighlightNumberOfFragments() {
            return get(FessConfig.QUERY_HIGHLIGHT_NUMBER_OF_FRAGMENTS);
        }

        public Integer getQueryHighlightNumberOfFragmentsAsInteger() {
            return getAsInteger(FessConfig.QUERY_HIGHLIGHT_NUMBER_OF_FRAGMENTS);
        }

        public String getQueryHighlightType() {
            return get(FessConfig.QUERY_HIGHLIGHT_TYPE);
        }

        public String getQueryMaxSearchResultOffset() {
            return get(FessConfig.QUERY_MAX_SEARCH_RESULT_OFFSET);
        }

        public Integer getQueryMaxSearchResultOffsetAsInteger() {
            return getAsInteger(FessConfig.QUERY_MAX_SEARCH_RESULT_OFFSET);
        }

        public String getQueryAdditionalResponseFields() {
            return get(FessConfig.QUERY_ADDITIONAL_RESPONSE_FIELDS);
        }

        public Integer getQueryAdditionalResponseFieldsAsInteger() {
            return getAsInteger(FessConfig.QUERY_ADDITIONAL_RESPONSE_FIELDS);
        }

        public String getQueryAdditionalApiResponseFields() {
            return get(FessConfig.QUERY_ADDITIONAL_API_RESPONSE_FIELDS);
        }

        public Integer getQueryAdditionalApiResponseFieldsAsInteger() {
            return getAsInteger(FessConfig.QUERY_ADDITIONAL_API_RESPONSE_FIELDS);
        }

        public String getQueryAdditionalCacheResponseFields() {
            return get(FessConfig.QUERY_ADDITIONAL_CACHE_RESPONSE_FIELDS);
        }

        public Integer getQueryAdditionalCacheResponseFieldsAsInteger() {
            return getAsInteger(FessConfig.QUERY_ADDITIONAL_CACHE_RESPONSE_FIELDS);
        }

        public String getQueryAdditionalHighlightedFields() {
            return get(FessConfig.QUERY_ADDITIONAL_HIGHLIGHTED_FIELDS);
        }

        public Integer getQueryAdditionalHighlightedFieldsAsInteger() {
            return getAsInteger(FessConfig.QUERY_ADDITIONAL_HIGHLIGHTED_FIELDS);
        }

        public String getQueryAdditionalSearchFields() {
            return get(FessConfig.QUERY_ADDITIONAL_SEARCH_FIELDS);
        }

        public Integer getQueryAdditionalSearchFieldsAsInteger() {
            return getAsInteger(FessConfig.QUERY_ADDITIONAL_SEARCH_FIELDS);
        }

        public String getQueryAdditionalFacetFields() {
            return get(FessConfig.QUERY_ADDITIONAL_FACET_FIELDS);
        }

        public Integer getQueryAdditionalFacetFieldsAsInteger() {
            return getAsInteger(FessConfig.QUERY_ADDITIONAL_FACET_FIELDS);
        }

        public String getQueryAdditionalSortFields() {
            return get(FessConfig.QUERY_ADDITIONAL_SORT_FIELDS);
        }

        public Integer getQueryAdditionalSortFieldsAsInteger() {
            return getAsInteger(FessConfig.QUERY_ADDITIONAL_SORT_FIELDS);
        }

        public String getQueryAdditionalNotAnalyzedFields() {
            return get(FessConfig.QUERY_ADDITIONAL_NOT_ANALYZED_FIELDS);
        }

        public Integer getQueryAdditionalNotAnalyzedFieldsAsInteger() {
            return getAsInteger(FessConfig.QUERY_ADDITIONAL_NOT_ANALYZED_FIELDS);
        }

        public String getQueryDefaultLanguages() {
            return get(FessConfig.QUERY_DEFAULT_LANGUAGES);
        }

        public Integer getQueryDefaultLanguagesAsInteger() {
            return getAsInteger(FessConfig.QUERY_DEFAULT_LANGUAGES);
        }

        public String getQueryLanguageMapping() {
            return get(FessConfig.QUERY_LANGUAGE_MAPPING);
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

        public String getSmbAvailableSidTypes() {
            return get(FessConfig.SMB_AVAILABLE_SID_TYPES);
        }

        public Integer getSmbAvailableSidTypesAsInteger() {
            return getAsInteger(FessConfig.SMB_AVAILABLE_SID_TYPES);
        }

        public String getIndexBackupTargets() {
            return get(FessConfig.INDEX_BACKUP_TARGETS);
        }

        public String getFormAdminMaxInputSize() {
            return get(FessConfig.FORM_ADMIN_MAX_INPUT_SIZE);
        }

        public Integer getFormAdminMaxInputSizeAsInteger() {
            return getAsInteger(FessConfig.FORM_ADMIN_MAX_INPUT_SIZE);
        }

        public String getAuthenticationAdminUsers() {
            return get(FessConfig.AUTHENTICATION_ADMIN_USERS);
        }

        public String getAuthenticationAdminRoles() {
            return get(FessConfig.AUTHENTICATION_ADMIN_ROLES);
        }

        public String getRoleSearchDefaultPermissions() {
            return get(FessConfig.ROLE_SEARCH_DEFAULT_PERMISSIONS);
        }

        public Integer getRoleSearchDefaultPermissionsAsInteger() {
            return getAsInteger(FessConfig.ROLE_SEARCH_DEFAULT_PERMISSIONS);
        }

        public String getRoleSearchDefaultDisplayPermissions() {
            return get(FessConfig.ROLE_SEARCH_DEFAULT_DISPLAY_PERMISSIONS);
        }

        public String getRoleSearchGuestPermissions() {
            return get(FessConfig.ROLE_SEARCH_GUEST_PERMISSIONS);
        }

        public String getRoleSearchUserPrefix() {
            return get(FessConfig.ROLE_SEARCH_USER_PREFIX);
        }

        public Integer getRoleSearchUserPrefixAsInteger() {
            return getAsInteger(FessConfig.ROLE_SEARCH_USER_PREFIX);
        }

        public String getRoleSearchGroupPrefix() {
            return get(FessConfig.ROLE_SEARCH_GROUP_PREFIX);
        }

        public Integer getRoleSearchGroupPrefixAsInteger() {
            return getAsInteger(FessConfig.ROLE_SEARCH_GROUP_PREFIX);
        }

        public String getRoleSearchRolePrefix() {
            return get(FessConfig.ROLE_SEARCH_ROLE_PREFIX);
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

        public String getPageUserMaxFetchSize() {
            return get(FessConfig.PAGE_USER_MAX_FETCH_SIZE);
        }

        public Integer getPageUserMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_USER_MAX_FETCH_SIZE);
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

        public String getPageCrawlingInfoMaxFetchSize() {
            return get(FessConfig.PAGE_CRAWLING_INFO_MAX_FETCH_SIZE);
        }

        public Integer getPageCrawlingInfoMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_CRAWLING_INFO_MAX_FETCH_SIZE);
        }

        public String getPageDataConfigMaxFetchSize() {
            return get(FessConfig.PAGE_DATA_CONFIG_MAX_FETCH_SIZE);
        }

        public Integer getPageDataConfigMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_DATA_CONFIG_MAX_FETCH_SIZE);
        }

        public String getPageWebConfigMaxFetchSize() {
            return get(FessConfig.PAGE_WEB_CONFIG_MAX_FETCH_SIZE);
        }

        public Integer getPageWebConfigMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_WEB_CONFIG_MAX_FETCH_SIZE);
        }

        public String getPageFileConfigMaxFetchSize() {
            return get(FessConfig.PAGE_FILE_CONFIG_MAX_FETCH_SIZE);
        }

        public Integer getPageFileConfigMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_FILE_CONFIG_MAX_FETCH_SIZE);
        }

        public String getPageDuplicateHostMaxFetchSize() {
            return get(FessConfig.PAGE_DUPLICATE_HOST_MAX_FETCH_SIZE);
        }

        public Integer getPageDuplicateHostMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_DUPLICATE_HOST_MAX_FETCH_SIZE);
        }

        public String getPageFailureUrlMaxFetchSize() {
            return get(FessConfig.PAGE_FAILURE_URL_MAX_FETCH_SIZE);
        }

        public Integer getPageFailureUrlMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_FAILURE_URL_MAX_FETCH_SIZE);
        }

        public String getPageFavoriteLogMaxFetchSize() {
            return get(FessConfig.PAGE_FAVORITE_LOG_MAX_FETCH_SIZE);
        }

        public Integer getPageFavoriteLogMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_FAVORITE_LOG_MAX_FETCH_SIZE);
        }

        public String getPageFileAuthMaxFetchSize() {
            return get(FessConfig.PAGE_FILE_AUTH_MAX_FETCH_SIZE);
        }

        public Integer getPageFileAuthMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_FILE_AUTH_MAX_FETCH_SIZE);
        }

        public String getPageWebAuthMaxFetchSize() {
            return get(FessConfig.PAGE_WEB_AUTH_MAX_FETCH_SIZE);
        }

        public Integer getPageWebAuthMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_WEB_AUTH_MAX_FETCH_SIZE);
        }

        public String getPagePathMappingMaxFetchSize() {
            return get(FessConfig.PAGE_PATH_MAPPING_MAX_FETCH_SIZE);
        }

        public Integer getPagePathMappingMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_PATH_MAPPING_MAX_FETCH_SIZE);
        }

        public String getPageRequestHeaderMaxFetchSize() {
            return get(FessConfig.PAGE_REQUEST_HEADER_MAX_FETCH_SIZE);
        }

        public Integer getPageRequestHeaderMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_REQUEST_HEADER_MAX_FETCH_SIZE);
        }

        public String getPageScheduledJobMaxFetchSize() {
            return get(FessConfig.PAGE_SCHEDULED_JOB_MAX_FETCH_SIZE);
        }

        public Integer getPageScheduledJobMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_SCHEDULED_JOB_MAX_FETCH_SIZE);
        }

        public String getPageSearchFieldLogMaxFetchSize() {
            return get(FessConfig.PAGE_SEARCH_FIELD_LOG_MAX_FETCH_SIZE);
        }

        public Integer getPageSearchFieldLogMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_SEARCH_FIELD_LOG_MAX_FETCH_SIZE);
        }

        public String getPageElevateWordMaxFetchSize() {
            return get(FessConfig.PAGE_ELEVATE_WORD_MAX_FETCH_SIZE);
        }

        public Integer getPageElevateWordMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_ELEVATE_WORD_MAX_FETCH_SIZE);
        }

        public String getPageBadWordMaxFetchSize() {
            return get(FessConfig.PAGE_BAD_WORD_MAX_FETCH_SIZE);
        }

        public Integer getPageBadWordMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_BAD_WORD_MAX_FETCH_SIZE);
        }

        public String getPageDictionaryMaxFetchSize() {
            return get(FessConfig.PAGE_DICTIONARY_MAX_FETCH_SIZE);
        }

        public Integer getPageDictionaryMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_DICTIONARY_MAX_FETCH_SIZE);
        }

        public String getPageThumbnailQueueMaxFetchSize() {
            return get(FessConfig.PAGE_THUMBNAIL_QUEUE_MAX_FETCH_SIZE);
        }

        public Integer getPageThumbnailQueueMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_THUMBNAIL_QUEUE_MAX_FETCH_SIZE);
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

        public String getThumbnailHtmlPhantomjsEnabled() {
            return get(FessConfig.THUMBNAIL_HTML_PHANTOMJS_ENABLED);
        }

        public boolean isThumbnailHtmlPhantomjsEnabled() {
            return is(FessConfig.THUMBNAIL_HTML_PHANTOMJS_ENABLED);
        }

        public String getThumbnailHtmlPhantomjsMaxHeight() {
            return get(FessConfig.THUMBNAIL_HTML_PHANTOMJS_MAX_HEIGHT);
        }

        public Integer getThumbnailHtmlPhantomjsMaxHeightAsInteger() {
            return getAsInteger(FessConfig.THUMBNAIL_HTML_PHANTOMJS_MAX_HEIGHT);
        }

        public String getThumbnailHtmlPhantomjsKeepAlive() {
            return get(FessConfig.THUMBNAIL_HTML_PHANTOMJS_KEEP_ALIVE);
        }

        public Integer getThumbnailHtmlPhantomjsKeepAliveAsInteger() {
            return getAsInteger(FessConfig.THUMBNAIL_HTML_PHANTOMJS_KEEP_ALIVE);
        }

        public String getThumbnailHtmlPhantomjsWindowWidth() {
            return get(FessConfig.THUMBNAIL_HTML_PHANTOMJS_WINDOW_WIDTH);
        }

        public Integer getThumbnailHtmlPhantomjsWindowWidthAsInteger() {
            return getAsInteger(FessConfig.THUMBNAIL_HTML_PHANTOMJS_WINDOW_WIDTH);
        }

        public String getThumbnailHtmlPhantomjsWindowHeight() {
            return get(FessConfig.THUMBNAIL_HTML_PHANTOMJS_WINDOW_HEIGHT);
        }

        public Integer getThumbnailHtmlPhantomjsWindowHeightAsInteger() {
            return getAsInteger(FessConfig.THUMBNAIL_HTML_PHANTOMJS_WINDOW_HEIGHT);
        }

        public String getThumbnailHtmlPhantomjsThumbnailWidth() {
            return get(FessConfig.THUMBNAIL_HTML_PHANTOMJS_THUMBNAIL_WIDTH);
        }

        public Integer getThumbnailHtmlPhantomjsThumbnailWidthAsInteger() {
            return getAsInteger(FessConfig.THUMBNAIL_HTML_PHANTOMJS_THUMBNAIL_WIDTH);
        }

        public String getThumbnailHtmlPhantomjsThumbnailHeight() {
            return get(FessConfig.THUMBNAIL_HTML_PHANTOMJS_THUMBNAIL_HEIGHT);
        }

        public Integer getThumbnailHtmlPhantomjsThumbnailHeightAsInteger() {
            return getAsInteger(FessConfig.THUMBNAIL_HTML_PHANTOMJS_THUMBNAIL_HEIGHT);
        }

        public String getThumbnailHtmlPhantomjsFormat() {
            return get(FessConfig.THUMBNAIL_HTML_PHANTOMJS_FORMAT);
        }

        public String getThumbnailGeneratorTargets() {
            return get(FessConfig.THUMBNAIL_GENERATOR_TARGETS);
        }

        public String getThumbnailCrawlerEnabled() {
            return get(FessConfig.THUMBNAIL_CRAWLER_ENABLED);
        }

        public boolean isThumbnailCrawlerEnabled() {
            return is(FessConfig.THUMBNAIL_CRAWLER_ENABLED);
        }

        public String getUserCodeRequestParameter() {
            return get(FessConfig.USER_CODE_REQUEST_PARAMETER);
        }

        public String getUserCodeMinLength() {
            return get(FessConfig.USER_CODE_MIN_LENGTH);
        }

        public Integer getUserCodeMinLengthAsInteger() {
            return getAsInteger(FessConfig.USER_CODE_MIN_LENGTH);
        }

        public String getUserCodeMaxLength() {
            return get(FessConfig.USER_CODE_MAX_LENGTH);
        }

        public Integer getUserCodeMaxLengthAsInteger() {
            return getAsInteger(FessConfig.USER_CODE_MAX_LENGTH);
        }

        public String getUserCodePattern() {
            return get(FessConfig.USER_CODE_PATTERN);
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

        public String getSchedulerMonitorInterval() {
            return get(FessConfig.SCHEDULER_MONITOR_INTERVAL);
        }

        public Integer getSchedulerMonitorIntervalAsInteger() {
            return getAsInteger(FessConfig.SCHEDULER_MONITOR_INTERVAL);
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

        public String getOnlineHelpNameDictSynonym() {
            return get(FessConfig.ONLINE_HELP_NAME_DICT_SYNONYM);
        }

        public String getOnlineHelpNameDict() {
            return get(FessConfig.ONLINE_HELP_NAME_DICT);
        }

        public String getOnlineHelpNameDictKuromoji() {
            return get(FessConfig.ONLINE_HELP_NAME_DICT_KUROMOJI);
        }

        public String getOnlineHelpNameDictSeunjeon() {
            return get(FessConfig.ONLINE_HELP_NAME_DICT_SEUNJEON);
        }

        public String getOnlineHelpNameDictProtwords() {
            return get(FessConfig.ONLINE_HELP_NAME_DICT_PROTWORDS);
        }

        public String getOnlineHelpNameDictMapping() {
            return get(FessConfig.ONLINE_HELP_NAME_DICT_MAPPING);
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

        public String getOnlineHelpNameUpgrade() {
            return get(FessConfig.ONLINE_HELP_NAME_UPGRADE);
        }

        public String getOnlineHelpNameEsreq() {
            return get(FessConfig.ONLINE_HELP_NAME_ESREQ);
        }

        public String getOnlineHelpNameAccesstoken() {
            return get(FessConfig.ONLINE_HELP_NAME_ACCESSTOKEN);
        }

        public String getOnlineHelpNameSuggest() {
            return get(FessConfig.ONLINE_HELP_NAME_SUGGEST);
        }

        public String getOnlineHelpSupportedLangs() {
            return get(FessConfig.ONLINE_HELP_SUPPORTED_LANGS);
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

        public String getSuggestUpdateContentsLimitNumPercentage() {
            return get(FessConfig.SUGGEST_UPDATE_CONTENTS_LIMIT_NUM_PERCENTAGE);
        }

        public String getSuggestUpdateContentsLimitNum() {
            return get(FessConfig.SUGGEST_UPDATE_CONTENTS_LIMIT_NUM);
        }

        public Integer getSuggestUpdateContentsLimitNumAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_UPDATE_CONTENTS_LIMIT_NUM);
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

        public String getSuggestSearchLogPermissions() {
            return get(FessConfig.SUGGEST_SEARCH_LOG_PERMISSIONS);
        }

        public String getLdapAdminEnabled() {
            return get(FessConfig.LDAP_ADMIN_ENABLED);
        }

        public boolean isLdapAdminEnabled() {
            return is(FessConfig.LDAP_ADMIN_ENABLED);
        }

        public String getLdapAdminUserFilter() {
            return get(FessConfig.LDAP_ADMIN_USER_FILTER);
        }

        public String getLdapAdminUserBaseDn() {
            return get(FessConfig.LDAP_ADMIN_USER_BASE_DN);
        }

        public String getLdapAdminUserObjectClasses() {
            return get(FessConfig.LDAP_ADMIN_USER_OBJECT_CLASSES);
        }

        public String getLdapAdminRoleFilter() {
            return get(FessConfig.LDAP_ADMIN_ROLE_FILTER);
        }

        public String getLdapAdminRoleBaseDn() {
            return get(FessConfig.LDAP_ADMIN_ROLE_BASE_DN);
        }

        public String getLdapAdminRoleObjectClasses() {
            return get(FessConfig.LDAP_ADMIN_ROLE_OBJECT_CLASSES);
        }

        public String getLdapAdminGroupFilter() {
            return get(FessConfig.LDAP_ADMIN_GROUP_FILTER);
        }

        public String getLdapAdminGroupBaseDn() {
            return get(FessConfig.LDAP_ADMIN_GROUP_BASE_DN);
        }

        public String getLdapAdminGroupObjectClasses() {
            return get(FessConfig.LDAP_ADMIN_GROUP_OBJECT_CLASSES);
        }

        public String getLdapAdminSyncPassword() {
            return get(FessConfig.LDAP_ADMIN_SYNC_PASSWORD);
        }

        public boolean isLdapAdminSyncPassword() {
            return is(FessConfig.LDAP_ADMIN_SYNC_PASSWORD);
        }

        public String getLdapMaxUsernameLength() {
            return get(FessConfig.LDAP_MAX_USERNAME_LENGTH);
        }

        public Integer getLdapMaxUsernameLengthAsInteger() {
            return getAsInteger(FessConfig.LDAP_MAX_USERNAME_LENGTH);
        }

        public String getLdapRoleSearchUserEnabled() {
            return get(FessConfig.LDAP_ROLE_SEARCH_USER_ENABLED);
        }

        public boolean isLdapRoleSearchUserEnabled() {
            return is(FessConfig.LDAP_ROLE_SEARCH_USER_ENABLED);
        }

        public String getLdapRoleSearchGroupEnabled() {
            return get(FessConfig.LDAP_ROLE_SEARCH_GROUP_ENABLED);
        }

        public boolean isLdapRoleSearchGroupEnabled() {
            return is(FessConfig.LDAP_ROLE_SEARCH_GROUP_ENABLED);
        }

        public String getLdapRoleSearchRoleEnabled() {
            return get(FessConfig.LDAP_ROLE_SEARCH_ROLE_ENABLED);
        }

        public boolean isLdapRoleSearchRoleEnabled() {
            return is(FessConfig.LDAP_ROLE_SEARCH_ROLE_ENABLED);
        }

        public String getLdapAttrSurname() {
            return get(FessConfig.LDAP_ATTR_SURNAME);
        }

        public String getLdapAttrGivenName() {
            return get(FessConfig.LDAP_ATTR_GIVEN_NAME);
        }

        public String getLdapAttrEmployeeNumber() {
            return get(FessConfig.LDAP_ATTR_EMPLOYEE_NUMBER);
        }

        public String getLdapAttrMail() {
            return get(FessConfig.LDAP_ATTR_MAIL);
        }

        public String getLdapAttrTelephoneNumber() {
            return get(FessConfig.LDAP_ATTR_TELEPHONE_NUMBER);
        }

        public String getLdapAttrHomePhone() {
            return get(FessConfig.LDAP_ATTR_HOME_PHONE);
        }

        public String getLdapAttrHomePostalAddress() {
            return get(FessConfig.LDAP_ATTR_HOME_POSTAL_ADDRESS);
        }

        public String getLdapAttrLabeleduri() {
            return get(FessConfig.LDAP_ATTR_LABELEDURI);
        }

        public String getLdapAttrRoomNumber() {
            return get(FessConfig.LDAP_ATTR_ROOM_NUMBER);
        }

        public String getLdapAttrDescription() {
            return get(FessConfig.LDAP_ATTR_DESCRIPTION);
        }

        public String getLdapAttrTitle() {
            return get(FessConfig.LDAP_ATTR_TITLE);
        }

        public String getLdapAttrPager() {
            return get(FessConfig.LDAP_ATTR_PAGER);
        }

        public String getLdapAttrStreet() {
            return get(FessConfig.LDAP_ATTR_STREET);
        }

        public String getLdapAttrPostalCode() {
            return get(FessConfig.LDAP_ATTR_POSTAL_CODE);
        }

        public String getLdapAttrPhysicalDeliveryOfficeName() {
            return get(FessConfig.LDAP_ATTR_PHYSICAL_DELIVERY_OFFICE_NAME);
        }

        public String getLdapAttrDestinationIndicator() {
            return get(FessConfig.LDAP_ATTR_DESTINATION_INDICATOR);
        }

        public String getLdapAttrInternationalisdnNumber() {
            return get(FessConfig.LDAP_ATTR_INTERNATIONALISDN_NUMBER);
        }

        public String getLdapAttrState() {
            return get(FessConfig.LDAP_ATTR_STATE);
        }

        public String getLdapAttrEmployeeType() {
            return get(FessConfig.LDAP_ATTR_EMPLOYEE_TYPE);
        }

        public String getLdapAttrFacsimileTelephoneNumber() {
            return get(FessConfig.LDAP_ATTR_FACSIMILE_TELEPHONE_NUMBER);
        }

        public String getLdapAttrPostOfficeBox() {
            return get(FessConfig.LDAP_ATTR_POST_OFFICE_BOX);
        }

        public String getLdapAttrInitials() {
            return get(FessConfig.LDAP_ATTR_INITIALS);
        }

        public String getLdapAttrCarLicense() {
            return get(FessConfig.LDAP_ATTR_CAR_LICENSE);
        }

        public String getLdapAttrMobile() {
            return get(FessConfig.LDAP_ATTR_MOBILE);
        }

        public String getLdapAttrPostalAddress() {
            return get(FessConfig.LDAP_ATTR_POSTAL_ADDRESS);
        }

        public String getLdapAttrCity() {
            return get(FessConfig.LDAP_ATTR_CITY);
        }

        public String getLdapAttrTeletexTerminalIdentifier() {
            return get(FessConfig.LDAP_ATTR_TELETEX_TERMINAL_IDENTIFIER);
        }

        public String getLdapAttrX121Address() {
            return get(FessConfig.LDAP_ATTR_X121_ADDRESS);
        }

        public String getLdapAttrBusinessCategory() {
            return get(FessConfig.LDAP_ATTR_BUSINESS_CATEGORY);
        }

        public String getLdapAttrRegisteredAddress() {
            return get(FessConfig.LDAP_ATTR_REGISTERED_ADDRESS);
        }

        public String getLdapAttrDisplayName() {
            return get(FessConfig.LDAP_ATTR_DISPLAY_NAME);
        }

        public String getLdapAttrPreferredLanguage() {
            return get(FessConfig.LDAP_ATTR_PREFERRED_LANGUAGE);
        }

        public String getLdapAttrDepartmentNumber() {
            return get(FessConfig.LDAP_ATTR_DEPARTMENT_NUMBER);
        }

        public String getLdapAttrUidNumber() {
            return get(FessConfig.LDAP_ATTR_UID_NUMBER);
        }

        public String getLdapAttrGidNumber() {
            return get(FessConfig.LDAP_ATTR_GID_NUMBER);
        }

        public String getLdapAttrHomeDirectory() {
            return get(FessConfig.LDAP_ATTR_HOME_DIRECTORY);
        }

        public String getSsoType() {
            return get(FessConfig.SSO_TYPE);
        }

        public String getSpnegoLoggerLevel() {
            return get(FessConfig.SPNEGO_LOGGER_LEVEL);
        }

        public Integer getSpnegoLoggerLevelAsInteger() {
            return getAsInteger(FessConfig.SPNEGO_LOGGER_LEVEL);
        }

        public String getSpnegoKrb5Conf() {
            return get(FessConfig.SPNEGO_KRB5_CONF);
        }

        public String getSpnegoLoginConf() {
            return get(FessConfig.SPNEGO_LOGIN_CONF);
        }

        public String getSpnegoPreauthUsername() {
            return get(FessConfig.SPNEGO_PREAUTH_USERNAME);
        }

        public String getSpnegoPreauthPassword() {
            return get(FessConfig.SPNEGO_PREAUTH_PASSWORD);
        }

        public String getSpnegoLoginClientModule() {
            return get(FessConfig.SPNEGO_LOGIN_CLIENT_MODULE);
        }

        public String getSpnegoLoginServerModule() {
            return get(FessConfig.SPNEGO_LOGIN_SERVER_MODULE);
        }

        public String getSpnegoAllowBasic() {
            return get(FessConfig.SPNEGO_ALLOW_BASIC);
        }

        public boolean isSpnegoAllowBasic() {
            return is(FessConfig.SPNEGO_ALLOW_BASIC);
        }

        public String getSpnegoAllowUnsecureBasic() {
            return get(FessConfig.SPNEGO_ALLOW_UNSECURE_BASIC);
        }

        public boolean isSpnegoAllowUnsecureBasic() {
            return is(FessConfig.SPNEGO_ALLOW_UNSECURE_BASIC);
        }

        public String getSpnegoPromptNtlm() {
            return get(FessConfig.SPNEGO_PROMPT_NTLM);
        }

        public boolean isSpnegoPromptNtlm() {
            return is(FessConfig.SPNEGO_PROMPT_NTLM);
        }

        public String getSpnegoAllowLocalhost() {
            return get(FessConfig.SPNEGO_ALLOW_LOCALHOST);
        }

        public boolean isSpnegoAllowLocalhost() {
            return is(FessConfig.SPNEGO_ALLOW_LOCALHOST);
        }

        public String getSpnegoAllowDelegation() {
            return get(FessConfig.SPNEGO_ALLOW_DELEGATION);
        }

        public boolean isSpnegoAllowDelegation() {
            return is(FessConfig.SPNEGO_ALLOW_DELEGATION);
        }

        public String getOicClientId() {
            return get(FessConfig.OIC_CLIENT_ID);
        }

        public String getOicClientSecret() {
            return get(FessConfig.OIC_CLIENT_SECRET);
        }

        public String getOicAuthServerUrl() {
            return get(FessConfig.OIC_AUTH_SERVER_URL);
        }

        public String getOicRedirectUrl() {
            return get(FessConfig.OIC_REDIRECT_URL);
        }

        public String getOicScope() {
            return get(FessConfig.OIC_SCOPE);
        }

        public String getOicTokenServerUrl() {
            return get(FessConfig.OIC_TOKEN_SERVER_URL);
        }

        public String getOicDefaultRoles() {
            return get(FessConfig.OIC_DEFAULT_ROLES);
        }

        public String getOicDefaultGroups() {
            return get(FessConfig.OIC_DEFAULT_GROUPS);
        }

        public Integer getOicDefaultGroupsAsInteger() {
            return getAsInteger(FessConfig.OIC_DEFAULT_GROUPS);
        }
    }
}
