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

import org.lastaflute.core.direction.exception.ConfigPropertyNotFoundException;

/**
 * @author FreeGen
 */
public interface FessConfig extends FessEnv, org.codelibs.fess.mylasta.direction.FessProp {

    /** The key of the configuration. e.g. Fess */
    String DOMAIN_TITLE = "domain.title";

    /** The key of the configuration. e.g. default */
    String search_engine_TYPE = "search_engine.type";

    /** The key of the configuration. e.g. http://localhost:9201 */
    String search_engine_HTTP_URL = "search_engine.http.url";

    /** The key of the configuration. e.g.  */
    String search_engine_HTTP_SSL_certificate_authorities = "search_engine.http.ssl.certificate_authorities";

    /** The key of the configuration. e.g.  */
    String search_engine_USERNAME = "search_engine.username";

    /** The key of the configuration. e.g.  */
    String search_engine_PASSWORD = "search_engine.password";

    /** The key of the configuration. e.g. 10000 */
    String search_engine_heartbeat_interval = "search_engine.heartbeat_interval";

    /** The key of the configuration. e.g. aes */
    String APP_CIPHER_ALGORISM = "app.cipher.algorism";

    /** The key of the configuration. e.g. ___change__me___ */
    String APP_CIPHER_KEY = "app.cipher.key";

    /** The key of the configuration. e.g. sha256 */
    String APP_DIGEST_ALGORISM = "app.digest.algorism";

    /** The key of the configuration. e.g. .*password|.*key|.*token|.*secret */
    String APP_ENCRYPT_PROPERTY_PATTERN = "app.encrypt.property.pattern";

    /** The key of the configuration. e.g.  */
    String APP_EXTENSION_NAMES = "app.extension.names";

    /** The key of the configuration. e.g.  */
    String APP_AUDIT_LOG_FORMAT = "app.audit.log.format";

    /** The key of the configuration. e.g. -Djava.awt.headless=true<br>
     * -Dfile.encoding=UTF-8<br>
     * -Djna.nosys=true<br>
     * -Djdk.io.permissionsUseCanonicalPath=true<br>
     * -Dhttp.maxConnections=20<br>
     * -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager<br>
     * -server<br>
     * -Xms128m<br>
     * -Xmx512m<br>
     * -XX:MaxMetaspaceSize=128m<br>
     * -XX:CompressedClassSpaceSize=32m<br>
     * -XX:-UseGCOverheadLimit<br>
     * -XX:+UseTLAB<br>
     * -XX:+DisableExplicitGC<br>
     * -XX:-HeapDumpOnOutOfMemoryError<br>
     * -XX:-OmitStackTraceInFastThrow<br>
     * -XX:+UnlockExperimentalVMOptions<br>
     * -XX:+UseG1GC<br>
     * -XX:InitiatingHeapOccupancyPercent=45<br>
     * -XX:G1HeapRegionSize=1m<br>
     * -XX:MaxGCPauseMillis=60000<br>
     * -XX:G1NewSizePercent=5<br>
     * -XX:G1MaxNewSizePercent=5<br>
     * -Djcifs.smb.client.responseTimeout=30000<br>
     * -Djcifs.smb.client.soTimeout=35000<br>
     * -Djcifs.smb.client.connTimeout=60000<br>
     * -Djcifs.smb.client.sessionTimeout=60000<br>
     * -Djcifs.smb1.smb.client.connTimeout=60000<br>
     * -Djcifs.smb1.smb.client.soTimeout=35000<br>
     * -Djcifs.smb1.smb.client.responseTimeout=30000<br>
     * -Dio.netty.noUnsafe=true<br>
     * -Dio.netty.noKeySetOptimization=true<br>
     * -Dio.netty.recycler.maxCapacityPerThread=0<br>
     * -Dlog4j.shutdownHookEnabled=false<br>
     * -Dlog4j2.formatMsgNoLookups=true<br>
     * -Dlog4j2.disable.jmx=true<br>
     * -Dlog4j.skipJansi=true<br>
     * -Dsun.java2d.cmm=sun.java2d.cmm.kcms.KcmsServiceProvider<br>
     * -Dorg.apache.pdfbox.rendering.UsePureJavaCMYKConversion=true<br>
     *  */
    String JVM_CRAWLER_OPTIONS = "jvm.crawler.options";

    /** The key of the configuration. e.g. -Djava.awt.headless=true<br>
     * -Dfile.encoding=UTF-8<br>
     * -Djna.nosys=true<br>
     * -Djdk.io.permissionsUseCanonicalPath=true<br>
     * -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager<br>
     * -server<br>
     * -Xms128m<br>
     * -Xmx256m<br>
     * -XX:MaxMetaspaceSize=128m<br>
     * -XX:CompressedClassSpaceSize=32m<br>
     * -XX:-UseGCOverheadLimit<br>
     * -XX:+UseTLAB<br>
     * -XX:+DisableExplicitGC<br>
     * -XX:-HeapDumpOnOutOfMemoryError<br>
     * -XX:+UnlockExperimentalVMOptions<br>
     * -XX:+UseG1GC<br>
     * -XX:InitiatingHeapOccupancyPercent=45<br>
     * -XX:G1HeapRegionSize=1m<br>
     * -XX:MaxGCPauseMillis=60000<br>
     * -XX:G1NewSizePercent=5<br>
     * -XX:G1MaxNewSizePercent=30<br>
     * -Dio.netty.noUnsafe=true<br>
     * -Dio.netty.noKeySetOptimization=true<br>
     * -Dio.netty.recycler.maxCapacityPerThread=0<br>
     * -Dlog4j.shutdownHookEnabled=false<br>
     * -Dlog4j2.disable.jmx=true<br>
     * -Dlog4j2.formatMsgNoLookups=true<br>
     * -Dlog4j.skipJansi=true<br>
     *  */
    String JVM_SUGGEST_OPTIONS = "jvm.suggest.options";

    /** The key of the configuration. e.g. -Djava.awt.headless=true<br>
     * -Dfile.encoding=UTF-8<br>
     * -Djna.nosys=true<br>
     * -Djdk.io.permissionsUseCanonicalPath=true<br>
     * -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager<br>
     * -server<br>
     * -Xms128m<br>
     * -Xmx256m<br>
     * -XX:MaxMetaspaceSize=128m<br>
     * -XX:CompressedClassSpaceSize=32m<br>
     * -XX:-UseGCOverheadLimit<br>
     * -XX:+UseTLAB<br>
     * -XX:+DisableExplicitGC<br>
     * -XX:-HeapDumpOnOutOfMemoryError<br>
     * -XX:-OmitStackTraceInFastThrow<br>
     * -XX:+UnlockExperimentalVMOptions<br>
     * -XX:+UseG1GC<br>
     * -XX:InitiatingHeapOccupancyPercent=45<br>
     * -XX:G1HeapRegionSize=4m<br>
     * -XX:MaxGCPauseMillis=60000<br>
     * -XX:G1NewSizePercent=5<br>
     * -XX:G1MaxNewSizePercent=50<br>
     * -Djcifs.smb.client.responseTimeout=30000<br>
     * -Djcifs.smb.client.soTimeout=35000<br>
     * -Djcifs.smb.client.connTimeout=60000<br>
     * -Djcifs.smb.client.sessionTimeout=60000<br>
     * -Djcifs.smb1.smb.client.connTimeout=60000<br>
     * -Djcifs.smb1.smb.client.soTimeout=35000<br>
     * -Djcifs.smb1.smb.client.responseTimeout=30000<br>
     * -Dio.netty.noUnsafe=true<br>
     * -Dio.netty.noKeySetOptimization=true<br>
     * -Dio.netty.recycler.maxCapacityPerThread=0<br>
     * -Dlog4j.shutdownHookEnabled=false<br>
     * -Dlog4j2.disable.jmx=true<br>
     * -Dlog4j2.formatMsgNoLookups=true<br>
     * -Dlog4j.skipJansi=true<br>
     * -Dsun.java2d.cmm=sun.java2d.cmm.kcms.KcmsServiceProvider<br>
     * -Dorg.apache.pdfbox.rendering.UsePureJavaCMYKConversion=true<br>
     *  */
    String JVM_THUMBNAIL_OPTIONS = "jvm.thumbnail.options";

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

    /** The key of the configuration. e.g. 0 */
    String JOB_MAX_CRAWLER_PROCESSES = "job.max.crawler.processes";

    /** The key of the configuration. e.g. groovy */
    String JOB_DEFAULT_SCRIPT = "job.default.script";

    /** The key of the configuration. e.g. 0 */
    String PROCESSORS = "processors";

    /** The key of the configuration. e.g. java */
    String JAVA_COMMAND_PATH = "java.command.path";

    /** The key of the configuration. e.g. python */
    String PYTHON_COMMAND_PATH = "python.command.path";

    /** The key of the configuration. e.g. UTF-8 */
    String PATH_ENCODING = "path.encoding";

    /** The key of the configuration. e.g. true */
    String USE_OWN_TMP_DIR = "use.own.tmp.dir";

    /** The key of the configuration. e.g. 4000 */
    String MAX_LOG_OUTPUT_LENGTH = "max.log.output.length";

    /** The key of the configuration. e.g. 50 */
    String ADAPTIVE_LOAD_CONTROL = "adaptive.load.control";

    /** The key of the configuration. e.g. js */
    String SUPPORTED_UPLOADED_JS_EXTENTIONS = "supported.uploaded.js.extentions";

    /** The key of the configuration. e.g. css */
    String SUPPORTED_UPLOADED_CSS_EXTENTIONS = "supported.uploaded.css.extentions";

    /** The key of the configuration. e.g. jpg,jpeg,gif,png,swf */
    String SUPPORTED_UPLOADED_MEDIA_EXTENTIONS = "supported.uploaded.media.extentions";

    /** The key of the configuration. e.g. license.properties */
    String SUPPORTED_UPLOADED_FILES = "supported.uploaded.files";

    /** The key of the configuration. e.g. ar,bg,bn,ca,ckb_IQ,cs,da,de,el,en_IE,en,es,et,eu,fa,fi,fr,gl,gu,he,hi,hr,hu,hy,id,it,ja,ko,lt,lv,mk,ml,nl,no,pa,pl,pt_BR,pt,ro,ru,si,sq,sv,ta,te,th,tl,tr,uk,ur,vi,zh_CN,zh_TW,zh */
    String SUPPORTED_LANGUAGES = "supported.languages";

    /** The key of the configuration. e.g. 60 */
    String API_ACCESS_TOKEN_LENGTH = "api.access.token.length";

    /** The key of the configuration. e.g. false */
    String API_ACCESS_TOKEN_REQUIRED = "api.access.token.required";

    /** The key of the configuration. e.g.  */
    String API_ACCESS_TOKEN_REQUEST_PARAMETER = "api.access.token.request.parameter";

    /** The key of the configuration. e.g. Radmin-api */
    String API_ADMIN_ACCESS_PERMISSIONS = "api.admin.access.permissions";

    /** The key of the configuration. e.g.  */
    String API_SEARCH_ACCEPT_REFERERS = "api.search.accept.referers";

    /** The key of the configuration. e.g. false */
    String API_SEARCH_SCROLL = "api.search.scroll";

    /** The key of the configuration. e.g.  */
    String API_JSON_RESPONSE_HEADERS = "api.json.response.headers";

    /** The key of the configuration. e.g. false */
    String API_JSON_RESPONSE_EXCEPTION_INCLUDED = "api.json.response.exception.included";

    /** The key of the configuration. e.g.  */
    String API_GSA_RESPONSE_HEADERS = "api.gsa.response.headers";

    /** The key of the configuration. e.g. false */
    String API_GSA_RESPONSE_EXCEPTION_INCLUDED = "api.gsa.response.exception.included";

    /** The key of the configuration. e.g.  */
    String API_DASHBOARD_RESPONSE_HEADERS = "api.dashboard.response.headers";

    /** The key of the configuration. e.g. * */
    String API_CORS_ALLOW_ORIGIN = "api.cors.allow.origin";

    /** The key of the configuration. e.g. GET, POST, OPTIONS, DELETE, PUT */
    String API_CORS_ALLOW_METHODS = "api.cors.allow.methods";

    /** The key of the configuration. e.g. 3600 */
    String API_CORS_MAX_AGE = "api.cors.max.age";

    /** The key of the configuration. e.g. Origin, Content-Type, Accept, Authorization, X-Requested-With */
    String API_CORS_ALLOW_HEADERS = "api.cors.allow.headers";

    /** The key of the configuration. e.g. true */
    String API_CORS_ALLOW_CREDENTIALS = "api.cors.allow.credentials";

    /** The key of the configuration. e.g. false */
    String API_JSONP_ENABLED = "api.jsonp.enabled";

    /** The key of the configuration. e.g. status,timed_out */
    String API_PING_search_engine_FIELDS = "api.ping.search_engine.fields";

    /** The key of the configuration. e.g.  */
    String VIRTUAL_HOST_HEADERS = "virtual.host.headers";

    /** The key of the configuration. e.g.  */
    String HTTP_PROXY_HOST = "http.proxy.host";

    /** The key of the configuration. e.g. 8080 */
    String HTTP_PROXY_PORT = "http.proxy.port";

    /** The key of the configuration. e.g.  */
    String HTTP_PROXY_USERNAME = "http.proxy.username";

    /** The key of the configuration. e.g.  */
    String HTTP_PROXY_PASSWORD = "http.proxy.password";

    /** The key of the configuration. e.g. 262144000 */
    String HTTP_FILEUPLOAD_MAX_SIZE = "http.fileupload.max.size";

    /** The key of the configuration. e.g. 262144 */
    String HTTP_FILEUPLOAD_THRESHOLD_SIZE = "http.fileupload.threshold.size";

    /** The key of the configuration. e.g. groovy */
    String CRAWLER_DEFAULT_SCRIPT = "crawler.default.script";

    /** The key of the configuration. e.g. 0 */
    String CRAWLER_HTTP_thread_pool_SIZE = "crawler.http.thread_pool.size";

    /** The key of the configuration. e.g. 100 */
    String CRAWLER_DOCUMENT_MAX_SITE_LENGTH = "crawler.document.max.site.length";

    /** The key of the configuration. e.g. UTF-8 */
    String CRAWLER_DOCUMENT_SITE_ENCODING = "crawler.document.site.encoding";

    /** The key of the configuration. e.g. unknown */
    String CRAWLER_DOCUMENT_UNKNOWN_HOSTNAME = "crawler.document.unknown.hostname";

    /** The key of the configuration. e.g. false */
    String CRAWLER_DOCUMENT_USE_SITE_ENCODING_ON_ENGLISH = "crawler.document.use.site.encoding.on.english";

    /** The key of the configuration. e.g. true */
    String CRAWLER_DOCUMENT_APPEND_DATA = "crawler.document.append.data";

    /** The key of the configuration. e.g. false */
    String CRAWLER_DOCUMENT_APPEND_FILENAME = "crawler.document.append.filename";

    /** The key of the configuration. e.g. 20 */
    String CRAWLER_DOCUMENT_MAX_ALPHANUM_TERM_SIZE = "crawler.document.max.alphanum.term.size";

    /** The key of the configuration. e.g. 10 */
    String CRAWLER_DOCUMENT_MAX_SYMBOL_TERM_SIZE = "crawler.document.max.symbol.term.size";

    /** The key of the configuration. e.g. false */
    String CRAWLER_DOCUMENT_DUPLICATE_TERM_REMOVED = "crawler.document.duplicate.term.removed";

    /** The key of the configuration. e.g. u0009u000Au000Bu000Cu000Du001Cu001Du001Eu001Fu0020u00A0u1680u180Eu2000u2001u2002u2003u2004u2005u2006u2007u2008u2009u200Au200Bu200Cu202Fu205Fu3000uFEFFuFFFDu00B6 */
    String CRAWLER_DOCUMENT_SPACE_CHARS = "crawler.document.space.chars";

    /** The key of the configuration. e.g. u002eu06d4u2e3cu3002 */
    String CRAWLER_DOCUMENT_FULLSTOP_CHARS = "crawler.document.fullstop.chars";

    /** The key of the configuration. e.g. UTF-8 */
    String CRAWLER_CRAWLING_DATA_ENCODING = "crawler.crawling.data.encoding";

    /** The key of the configuration. e.g. http,https */
    String CRAWLER_WEB_PROTOCOLS = "crawler.web.protocols";

    /** The key of the configuration. e.g. file,smb,smb1,ftp,storage */
    String CRAWLER_FILE_PROTOCOLS = "crawler.file.protocols";

    /** The key of the configuration. e.g. ^FESS_ENV_.* */
    String CRAWLER_DATA_ENV_PARAM_KEY_PATTERN = "crawler.data.env.param.key.pattern";

    /** The key of the configuration. e.g. false */
    String CRAWLER_IGNORE_ROBOTS_TXT = "crawler.ignore.robots.txt";

    /** The key of the configuration. e.g. false */
    String CRAWLER_IGNORE_ROBOTS_TAGS = "crawler.ignore.robots.tags";

    /** The key of the configuration. e.g. true */
    String CRAWLER_IGNORE_CONTENT_EXCEPTION = "crawler.ignore.content.exception";

    /** The key of the configuration. e.g. 404 */
    String CRAWLER_FAILURE_URL_STATUS_CODES = "crawler.failure.url.status.codes";

    /** The key of the configuration. e.g. 60 */
    String CRAWLER_SYSTEM_MONITOR_INTERVAL = "crawler.system.monitor.interval";

    /** The key of the configuration. e.g. true */
    String CRAWLER_HOTTHREAD_ignore_idle_threads = "crawler.hotthread.ignore_idle_threads";

    /** The key of the configuration. e.g. 500ms */
    String CRAWLER_HOTTHREAD_INTERVAL = "crawler.hotthread.interval";

    /** The key of the configuration. e.g. 10 */
    String CRAWLER_HOTTHREAD_SNAPSHOTS = "crawler.hotthread.snapshots";

    /** The key of the configuration. e.g. 3 */
    String CRAWLER_HOTTHREAD_THREADS = "crawler.hotthread.threads";

    /** The key of the configuration. e.g. 30s */
    String CRAWLER_HOTTHREAD_TIMEOUT = "crawler.hotthread.timeout";

    /** The key of the configuration. e.g. cpu */
    String CRAWLER_HOTTHREAD_TYPE = "crawler.hotthread.type";

    /** The key of the configuration. e.g. resourceName,X-Parsed-By,Content-Encoding.*,Content-Type.*,X-TIKA.* */
    String CRAWLER_METADATA_CONTENT_EXCLUDES = "crawler.metadata.content.excludes";

    /** The key of the configuration. e.g. title=title:string<br>
     * Title=title:string<br>
     * dc:title=title:string<br>
     *  */
    String CRAWLER_METADATA_NAME_MAPPING = "crawler.metadata.name.mapping";

    /** The key of the configuration. e.g. //BODY */
    String CRAWLER_DOCUMENT_HTML_CONTENT_XPATH = "crawler.document.html.content.xpath";

    /** The key of the configuration. e.g. //HTML/@lang */
    String CRAWLER_DOCUMENT_HTML_LANG_XPATH = "crawler.document.html.lang.xpath";

    /** The key of the configuration. e.g. //META[@name='description']/@content */
    String CRAWLER_DOCUMENT_HTML_DIGEST_XPATH = "crawler.document.html.digest.xpath";

    /** The key of the configuration. e.g. //LINK[@rel='canonical'][1]/@href */
    String CRAWLER_DOCUMENT_HTML_CANONICAL_XPATH = "crawler.document.html.canonical.xpath";

    /** The key of the configuration. e.g. noscript,script,style,header,footer,nav,a[rel=nofollow] */
    String CRAWLER_DOCUMENT_HTML_PRUNED_TAGS = "crawler.document.html.pruned.tags";

    /** The key of the configuration. e.g. 120 */
    String CRAWLER_DOCUMENT_HTML_MAX_DIGEST_LENGTH = "crawler.document.html.max.digest.length";

    /** The key of the configuration. e.g.  */
    String CRAWLER_DOCUMENT_HTML_DEFAULT_LANG = "crawler.document.html.default.lang";

    /** The key of the configuration. e.g.  */
    String CRAWLER_DOCUMENT_HTML_DEFAULT_INCLUDE_INDEX_PATTERNS = "crawler.document.html.default.include.index.patterns";

    /** The key of the configuration. e.g. (?i).*(css|js|jpeg|jpg|gif|png|bmp|wmv|xml|ico|exe) */
    String CRAWLER_DOCUMENT_HTML_DEFAULT_EXCLUDE_INDEX_PATTERNS = "crawler.document.html.default.exclude.index.patterns";

    /** The key of the configuration. e.g.  */
    String CRAWLER_DOCUMENT_HTML_DEFAULT_INCLUDE_SEARCH_PATTERNS = "crawler.document.html.default.include.search.patterns";

    /** The key of the configuration. e.g.  */
    String CRAWLER_DOCUMENT_HTML_DEFAULT_EXCLUDE_SEARCH_PATTERNS = "crawler.document.html.default.exclude.search.patterns";

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

    /** The key of the configuration. e.g.  */
    String CRAWLER_DOCUMENT_FILE_DEFAULT_INCLUDE_INDEX_PATTERNS = "crawler.document.file.default.include.index.patterns";

    /** The key of the configuration. e.g.  */
    String CRAWLER_DOCUMENT_FILE_DEFAULT_EXCLUDE_INDEX_PATTERNS = "crawler.document.file.default.exclude.index.patterns";

    /** The key of the configuration. e.g.  */
    String CRAWLER_DOCUMENT_FILE_DEFAULT_INCLUDE_SEARCH_PATTERNS = "crawler.document.file.default.include.search.patterns";

    /** The key of the configuration. e.g.  */
    String CRAWLER_DOCUMENT_FILE_DEFAULT_EXCLUDE_SEARCH_PATTERNS = "crawler.document.file.default.exclude.search.patterns";

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

    /** The key of the configuration. e.g. 3600 */
    String INDEXER_WEBFS_MAX_EMPTY_LIST_COUNT = "indexer.webfs.max.empty.list.count";

    /** The key of the configuration. e.g. 10000 */
    String INDEXER_WEBFS_UPDATE_INTERVAL = "indexer.webfs.update.interval";

    /** The key of the configuration. e.g. 10 */
    String INDEXER_WEBFS_MAX_DOCUMENT_CACHE_SIZE = "indexer.webfs.max.document.cache.size";

    /** The key of the configuration. e.g. 1048576 */
    String INDEXER_WEBFS_MAX_DOCUMENT_REQUEST_SIZE = "indexer.webfs.max.document.request.size";

    /** The key of the configuration. e.g. 10000 */
    String INDEXER_DATA_MAX_DOCUMENT_CACHE_SIZE = "indexer.data.max.document.cache.size";

    /** The key of the configuration. e.g. 1048576 */
    String INDEXER_DATA_MAX_DOCUMENT_REQUEST_SIZE = "indexer.data.max.document.request.size";

    /** The key of the configuration. e.g. 100 */
    String INDEXER_DATA_MAX_DELETE_CACHE_SIZE = "indexer.data.max.delete.cache.size";

    /** The key of the configuration. e.g. 10 */
    String INDEXER_DATA_MAX_REDIRECT_COUNT = "indexer.data.max.redirect.count";

    /** The key of the configuration. e.g. content,important_content,title */
    String INDEXER_LANGUAGE_FIELDS = "indexer.language.fields";

    /** The key of the configuration. e.g. 1000 */
    String INDEXER_LANGUAGE_DETECT_LENGTH = "indexer.language.detect.length";

    /** The key of the configuration. e.g. 10000 */
    String INDEXER_MAX_RESULT_WINDOW_SIZE = "indexer.max.result.window.size";

    /** The key of the configuration. e.g. 50000 */
    String INDEXER_MAX_SEARCH_DOC_SIZE = "indexer.max.search.doc.size";

    /** The key of the configuration. e.g. default */
    String INDEX_CODEC = "index.codec";

    /** The key of the configuration. e.g. 5 */
    String INDEX_number_of_shards = "index.number_of_shards";

    /** The key of the configuration. e.g. 0-1 */
    String INDEX_auto_expand_replicas = "index.auto_expand_replicas";

    /** The key of the configuration. e.g. SHA-512 */
    String INDEX_ID_DIGEST_ALGORITHM = "index.id.digest.algorithm";

    /** The key of the configuration. e.g. admin */
    String INDEX_USER_initial_password = "index.user.initial_password";

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

    /** The key of the configuration. e.g. _seq_no */
    String INDEX_FIELD_seq_no = "index.field.seq_no";

    /** The key of the configuration. e.g. _primary_term */
    String INDEX_FIELD_primary_term = "index.field.primary_term";

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

    /** The key of the configuration. e.g. content_minhash_bits */
    String INDEX_FIELD_content_minhash_bits = "index.field.content_minhash_bits";

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

    /** The key of the configuration. e.g. thumbnail */
    String INDEX_FIELD_THUMBNAIL = "index.field.thumbnail";

    /** The key of the configuration. e.g. virtual_host */
    String INDEX_FIELD_virtual_host = "index.field.virtual_host";

    /** The key of the configuration. e.g. content_title */
    String RESPONSE_FIELD_content_title = "response.field.content_title";

    /** The key of the configuration. e.g. content_description */
    String RESPONSE_FIELD_content_description = "response.field.content_description";

    /** The key of the configuration. e.g. url_link */
    String RESPONSE_FIELD_url_link = "response.field.url_link";

    /** The key of the configuration. e.g. site_path */
    String RESPONSE_FIELD_site_path = "response.field.site_path";

    /** The key of the configuration. e.g. 50 */
    String RESPONSE_MAX_TITLE_LENGTH = "response.max.title.length";

    /** The key of the configuration. e.g. 100 */
    String RESPONSE_MAX_SITE_PATH_LENGTH = "response.max.site.path.length";

    /** The key of the configuration. e.g. true */
    String RESPONSE_HIGHLIGHT_content_title_ENABLED = "response.highlight.content_title.enabled";

    /** The key of the configuration. e.g. application/pdf,text/plain */
    String RESPONSE_INLINE_MIMETYPES = "response.inline.mimetypes";

    /** The key of the configuration. e.g. fess.search */
    String INDEX_DOCUMENT_SEARCH_INDEX = "index.document.search.index";

    /** The key of the configuration. e.g. fess.update */
    String INDEX_DOCUMENT_UPDATE_INDEX = "index.document.update.index";

    /** The key of the configuration. e.g. fess */
    String INDEX_DOCUMENT_SUGGEST_INDEX = "index.document.suggest.index";

    /** The key of the configuration. e.g. fess_crawler */
    String INDEX_DOCUMENT_CRAWLER_INDEX = "index.document.crawler.index";

    /** The key of the configuration. e.g. 10 */
    String INDEX_DOCUMENT_CRAWLER_QUEUE_number_of_shards = "index.document.crawler.queue.number_of_shards";

    /** The key of the configuration. e.g. 10 */
    String INDEX_DOCUMENT_CRAWLER_DATA_number_of_shards = "index.document.crawler.data.number_of_shards";

    /** The key of the configuration. e.g. 10 */
    String INDEX_DOCUMENT_CRAWLER_FILTER_number_of_shards = "index.document.crawler.filter.number_of_shards";

    /** The key of the configuration. e.g. 1 */
    String INDEX_DOCUMENT_CRAWLER_QUEUE_number_of_replicas = "index.document.crawler.queue.number_of_replicas";

    /** The key of the configuration. e.g. 1 */
    String INDEX_DOCUMENT_CRAWLER_DATA_number_of_replicas = "index.document.crawler.data.number_of_replicas";

    /** The key of the configuration. e.g. 1 */
    String INDEX_DOCUMENT_CRAWLER_FILTER_number_of_replicas = "index.document.crawler.filter.number_of_replicas";

    /** The key of the configuration. e.g. fess_config */
    String INDEX_CONFIG_INDEX = "index.config.index";

    /** The key of the configuration. e.g. fess_user */
    String INDEX_USER_INDEX = "index.user.index";

    /** The key of the configuration. e.g. fess_log */
    String INDEX_LOG_INDEX = "index.log.index";

    /** The key of the configuration. e.g. lang,role,label,anchor,virtual_host */
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

    /** The key of the configuration. e.g. url,title,role,boost */
    String INDEX_ADMIN_REQUIRED_FIELDS = "index.admin.required.fields";

    /** The key of the configuration. e.g. 3m */
    String INDEX_SEARCH_TIMEOUT = "index.search.timeout";

    /** The key of the configuration. e.g. 3m */
    String INDEX_SCROLL_SEARCH_TIMEOUT = "index.scroll.search.timeout";

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

    /** The key of the configuration. e.g. text/html=html<br>
     * application/msword=word<br>
     * application/vnd.openxmlformats-officedocument.wordprocessingml.document=word<br>
     * application/vnd.ms-excel=excel<br>
     * application/vnd.ms-excel.sheet.2=excel<br>
     * application/vnd.ms-excel.sheet.3=excel<br>
     * application/vnd.ms-excel.sheet.4=excel<br>
     * application/vnd.ms-excel.workspace.3=excel<br>
     * application/vnd.ms-excel.workspace.4=excel<br>
     * application/vnd.openxmlformats-officedocument.spreadsheetml.sheet=excel<br>
     * application/vnd.ms-powerpoint=powerpoint<br>
     * application/vnd.openxmlformats-officedocument.presentationml.presentation=powerpoint<br>
     * application/vnd.oasis.opendocument.text=odt<br>
     * application/vnd.oasis.opendocument.spreadsheet=ods<br>
     * application/vnd.oasis.opendocument.presentation=odp<br>
     * application/pdf=pdf<br>
     * application/x-fictionbook+xml=fb2<br>
     * application/e-pub+zip=epub<br>
     * application/x-ibooks+zip=ibooks<br>
     * text/plain=txt<br>
     * application/rtf=rtf<br>
     * application/vnd.ms-htmlhelp=chm<br>
     * application/zip=zip<br>
     * application/x-7z-comressed=7z<br>
     * application/x-bzip=bz<br>
     * application/x-bzip2=bz2<br>
     * application/x-tar=tar<br>
     * application/x-rar-compressed=rar<br>
     * video/3gp=3gp<br>
     * video/3g2=3g2<br>
     * video/x-msvideo=avi<br>
     * video/x-flv=flv<br>
     * video/mpeg=mpeg<br>
     * video/mp4=mp4<br>
     * video/ogv=ogv<br>
     * video/quicktime=qt<br>
     * video/x-m4v=m4v<br>
     * audio/x-aif=aif<br>
     * audio/midi=midi<br>
     * audio/mpga=mpga<br>
     * audio/mp4=mp4a<br>
     * audio/ogg=oga<br>
     * audio/x-wav=wav<br>
     * image/webp=webp<br>
     * image/bmp=bmp<br>
     * image/x-icon=ico<br>
     * image/x-icon=ico<br>
     * image/png=png<br>
     * image/svg+xml=svg<br>
     * image/tiff=tiff<br>
     * image/jpeg=jpg<br>
     *  */
    String INDEX_FILETYPE = "index.filetype";

    /** The key of the configuration. e.g. 100 */
    String INDEX_REINDEX_SIZE = "index.reindex.size";

    /** The key of the configuration. e.g. {"source":{"index":"__SOURCE_INDEX__","size":__SIZE__},"dest":{"index":"__DEST_INDEX__"},"script":{"source":"__SCRIPT_SOURCE__"}} */
    String INDEX_REINDEX_BODY = "index.reindex.body";

    /** The key of the configuration. e.g. adaptive */
    String INDEX_REINDEX_requests_per_second = "index.reindex.requests_per_second";

    /** The key of the configuration. e.g. false */
    String INDEX_REINDEX_REFRESH = "index.reindex.refresh";

    /** The key of the configuration. e.g. 1m */
    String INDEX_REINDEX_TIMEOUT = "index.reindex.timeout";

    /** The key of the configuration. e.g. 5m */
    String INDEX_REINDEX_SCROLL = "index.reindex.scroll";

    /** The key of the configuration. e.g.  */
    String INDEX_REINDEX_max_docs = "index.reindex.max_docs";

    /** The key of the configuration. e.g. 1000 */
    String QUERY_MAX_LENGTH = "query.max.length";

    /** The key of the configuration. e.g. 10000 */
    String QUERY_TIMEOUT = "query.timeout";

    /** The key of the configuration. e.g. true */
    String QUERY_TIMEOUT_LOGGING = "query.timeout.logging";

    /** The key of the configuration. e.g. 10000 */
    String QUERY_TRACK_TOTAL_HITS = "query.track.total.hits";

    /** The key of the configuration. e.g. location */
    String QUERY_GEO_FIELDS = "query.geo.fields";

    /** The key of the configuration. e.g. browser_lang */
    String QUERY_BROWSER_LANG_PARAMETER_NAME = "query.browser.lang.parameter.name";

    /** The key of the configuration. e.g. true */
    String QUERY_REPLACE_TERM_WITH_PREFIX_QUERY = "query.replace.term.with.prefix.query";

    /** The key of the configuration. e.g. u0021u002Cu002Eu003Fu0589u061Fu06D4u0700u0701u0702u0964u104Au104Bu1362u1367u1368u166Eu1803u1809u203Cu203Du2047u2048u2049u3002uFE52uFE57uFF01uFF0EuFF1FuFF61 */
    String QUERY_HIGHLIGHT_TERMINAL_CHARS = "query.highlight.terminal.chars";

    /** The key of the configuration. e.g. 60 */
    String QUERY_HIGHLIGHT_FRAGMENT_SIZE = "query.highlight.fragment.size";

    /** The key of the configuration. e.g. 2 */
    String QUERY_HIGHLIGHT_NUMBER_OF_FRAGMENTS = "query.highlight.number.of.fragments";

    /** The key of the configuration. e.g. fvh */
    String QUERY_HIGHLIGHT_TYPE = "query.highlight.type";

    /** The key of the configuration. e.g. &lt;strong&gt; */
    String QUERY_HIGHLIGHT_TAG_PRE = "query.highlight.tag.pre";

    /** The key of the configuration. e.g. &lt;/strong&gt; */
    String QUERY_HIGHLIGHT_TAG_POST = "query.highlight.tag.post";

    /** The key of the configuration. e.g. u0009u000Au0013u0020 */
    String QUERY_HIGHLIGHT_BOUNDARY_CHARS = "query.highlight.boundary.chars";

    /** The key of the configuration. e.g. 20 */
    String QUERY_HIGHLIGHT_BOUNDARY_MAX_SCAN = "query.highlight.boundary.max.scan";

    /** The key of the configuration. e.g. chars */
    String QUERY_HIGHLIGHT_BOUNDARY_SCANNER = "query.highlight.boundary.scanner";

    /** The key of the configuration. e.g. default */
    String QUERY_HIGHLIGHT_ENCODER = "query.highlight.encoder";

    /** The key of the configuration. e.g. false */
    String QUERY_HIGHLIGHT_FORCE_SOURCE = "query.highlight.force.source";

    /** The key of the configuration. e.g. span */
    String QUERY_HIGHLIGHT_FRAGMENTER = "query.highlight.fragmenter";

    /** The key of the configuration. e.g. -1 */
    String QUERY_HIGHLIGHT_FRAGMENT_OFFSET = "query.highlight.fragment.offset";

    /** The key of the configuration. e.g. 0 */
    String QUERY_HIGHLIGHT_NO_MATCH_SIZE = "query.highlight.no.match.size";

    /** The key of the configuration. e.g. score */
    String QUERY_HIGHLIGHT_ORDER = "query.highlight.order";

    /** The key of the configuration. e.g. 256 */
    String QUERY_HIGHLIGHT_PHRASE_LIMIT = "query.highlight.phrase.limit";

    /** The key of the configuration. e.g. hl_content,digest */
    String QUERY_HIGHLIGHT_CONTENT_DESCRIPTION_FIELDS = "query.highlight.content.description.fields";

    /** The key of the configuration. e.g. true */
    String QUERY_HIGHLIGHT_BOUNDARY_POSITION_DETECT = "query.highlight.boundary.position.detect";

    /** The key of the configuration. e.g. query */
    String QUERY_HIGHLIGHT_TEXT_FRAGMENT_TYPE = "query.highlight.text.fragment.type";

    /** The key of the configuration. e.g. 3 */
    String QUERY_HIGHLIGHT_TEXT_FRAGMENT_SIZE = "query.highlight.text.fragment.size";

    /** The key of the configuration. e.g. 5 */
    String QUERY_HIGHLIGHT_TEXT_FRAGMENT_PREFIX_LENGTH = "query.highlight.text.fragment.prefix.length";

    /** The key of the configuration. e.g. 5 */
    String QUERY_HIGHLIGHT_TEXT_FRAGMENT_SUFFIX_LENGTH = "query.highlight.text.fragment.suffix.length";

    /** The key of the configuration. e.g. 100000 */
    String QUERY_MAX_SEARCH_RESULT_OFFSET = "query.max.search.result.offset";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_DEFAULT_FIELDS = "query.additional.default.fields";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_RESPONSE_FIELDS = "query.additional.response.fields";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_API_RESPONSE_FIELDS = "query.additional.api.response.fields";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_SCROLL_RESPONSE_FIELDS = "query.additional.scroll.response.fields";

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
    String QUERY_ADDITIONAL_ANALYZED_FIELDS = "query.additional.analyzed.fields";

    /** The key of the configuration. e.g.  */
    String QUERY_ADDITIONAL_NOT_ANALYZED_FIELDS = "query.additional.not.analyzed.fields";

    /** The key of the configuration. e.g. UE,U,T,RK,S,LANG */
    String QUERY_GSA_RESPONSE_FIELDS = "query.gsa.response.fields";

    /** The key of the configuration. e.g. en */
    String QUERY_GSA_DEFAULT_LANG = "query.gsa.default.lang";

    /** The key of the configuration. e.g.  */
    String QUERY_GSA_DEFAULT_SORT = "query.gsa.default.sort";

    /** The key of the configuration. e.g. MT_ */
    String QUERY_GSA_META_PREFIX = "query.gsa.meta.prefix";

    /** The key of the configuration. e.g. charset */
    String QUERY_GSA_INDEX_FIELD_CHARSET = "query.gsa.index.field.charset";

    /** The key of the configuration. e.g. content_type */
    String QUERY_GSA_INDEX_FIELD_content_type_ = "query.gsa.index.field.content_type.";

    /** The key of the configuration. e.g. 4 */
    String QUERY_COLLAPSE_MAX_CONCURRENT_GROUP_RESULTS = "query.collapse.max.concurrent.group.results";

    /** The key of the configuration. e.g. similar_docs */
    String QUERY_COLLAPSE_INNER_HITS_NAME = "query.collapse.inner.hits.name";

    /** The key of the configuration. e.g. 0 */
    String QUERY_COLLAPSE_INNER_HITS_SIZE = "query.collapse.inner.hits.size";

    /** The key of the configuration. e.g.  */
    String QUERY_COLLAPSE_INNER_HITS_SORTS = "query.collapse.inner.hits.sorts";

    /** The key of the configuration. e.g.  */
    String QUERY_DEFAULT_LANGUAGES = "query.default.languages";

    /** The key of the configuration. e.g. _query */
    String QUERY_JSON_DEFAULT_PREFERENCE = "query.json.default.preference";

    /** The key of the configuration. e.g. _query */
    String QUERY_GSA_DEFAULT_PREFERENCE = "query.gsa.default.preference";

    /** The key of the configuration. e.g. ar=ar<br>
     * bg=bg<br>
     * bn=bn<br>
     * ca=ca<br>
     * ckb-iq=ckb-iq<br>
     * ckb_IQ=ckb-iq<br>
     * cs=cs<br>
     * da=da<br>
     * de=de<br>
     * el=el<br>
     * en=en<br>
     * en-ie=en-ie<br>
     * en_IE=en-ie<br>
     * es=es<br>
     * et=et<br>
     * eu=eu<br>
     * fa=fa<br>
     * fi=fi<br>
     * fr=fr<br>
     * gl=gl<br>
     * gu=gu<br>
     * he=he<br>
     * hi=hi<br>
     * hr=hr<br>
     * hu=hu<br>
     * hy=hy<br>
     * id=id<br>
     * it=it<br>
     * ja=ja<br>
     * ko=ko<br>
     * lt=lt<br>
     * lv=lv<br>
     * mk=mk<br>
     * ml=ml<br>
     * nl=nl<br>
     * no=no<br>
     * pa=pa<br>
     * pl=pl<br>
     * pt=pt<br>
     * pt-br=pt-br<br>
     * pt_BR=pt-br<br>
     * ro=ro<br>
     * ru=ru<br>
     * si=si<br>
     * sq=sq<br>
     * sv=sv<br>
     * ta=ta<br>
     * te=te<br>
     * th=th<br>
     * tl=tl<br>
     * tr=tr<br>
     * uk=uk<br>
     * ur=ur<br>
     * vi=vi<br>
     * zh-cn=zh-cn<br>
     * zh_CN=zh-cn<br>
     * zh-tw=zh-tw<br>
     * zh_TW=zh-tw<br>
     * zh=zh<br>
     *  */
    String QUERY_LANGUAGE_MAPPING = "query.language.mapping";

    /** The key of the configuration. e.g. 0.5 */
    String QUERY_BOOST_TITLE = "query.boost.title";

    /** The key of the configuration. e.g. 1.0 */
    String QUERY_BOOST_TITLE_LANG = "query.boost.title.lang";

    /** The key of the configuration. e.g. 0.05 */
    String QUERY_BOOST_CONTENT = "query.boost.content";

    /** The key of the configuration. e.g. 0.1 */
    String QUERY_BOOST_CONTENT_LANG = "query.boost.content.lang";

    /** The key of the configuration. e.g. -1.0 */
    String QUERY_BOOST_important_content = "query.boost.important_content";

    /** The key of the configuration. e.g. -1.0 */
    String QUERY_BOOST_important_content_LANG = "query.boost.important_content.lang";

    /** The key of the configuration. e.g. 4 */
    String QUERY_BOOST_FUZZY_MIN_LENGTH = "query.boost.fuzzy.min.length";

    /** The key of the configuration. e.g. 0.01 */
    String QUERY_BOOST_FUZZY_TITLE = "query.boost.fuzzy.title";

    /** The key of the configuration. e.g. AUTO */
    String QUERY_BOOST_FUZZY_TITLE_FUZZINESS = "query.boost.fuzzy.title.fuzziness";

    /** The key of the configuration. e.g. 10 */
    String QUERY_BOOST_FUZZY_TITLE_EXPANSIONS = "query.boost.fuzzy.title.expansions";

    /** The key of the configuration. e.g. 0 */
    String QUERY_BOOST_FUZZY_TITLE_prefix_length = "query.boost.fuzzy.title.prefix_length";

    /** The key of the configuration. e.g. true */
    String QUERY_BOOST_FUZZY_TITLE_TRANSPOSITIONS = "query.boost.fuzzy.title.transpositions";

    /** The key of the configuration. e.g. 0.005 */
    String QUERY_BOOST_FUZZY_CONTENT = "query.boost.fuzzy.content";

    /** The key of the configuration. e.g. AUTO */
    String QUERY_BOOST_FUZZY_CONTENT_FUZZINESS = "query.boost.fuzzy.content.fuzziness";

    /** The key of the configuration. e.g. 10 */
    String QUERY_BOOST_FUZZY_CONTENT_EXPANSIONS = "query.boost.fuzzy.content.expansions";

    /** The key of the configuration. e.g. 0 */
    String QUERY_BOOST_FUZZY_CONTENT_prefix_length = "query.boost.fuzzy.content.prefix_length";

    /** The key of the configuration. e.g. true */
    String QUERY_BOOST_FUZZY_CONTENT_TRANSPOSITIONS = "query.boost.fuzzy.content.transpositions";

    /** The key of the configuration. e.g. 50 */
    String QUERY_PREFIX_EXPANSIONS = "query.prefix.expansions";

    /** The key of the configuration. e.g. 0 */
    String QUERY_PREFIX_SLOP = "query.prefix.slop";

    /** The key of the configuration. e.g. 0 */
    String QUERY_FUZZY_prefix_length = "query.fuzzy.prefix_length";

    /** The key of the configuration. e.g. 50 */
    String QUERY_FUZZY_EXPANSIONS = "query.fuzzy.expansions";

    /** The key of the configuration. e.g. true */
    String QUERY_FUZZY_TRANSPOSITIONS = "query.fuzzy.transpositions";

    /** The key of the configuration. e.g. label */
    String QUERY_FACET_FIELDS = "query.facet.fields";

    /** The key of the configuration. e.g. 100 */
    String QUERY_FACET_FIELDS_SIZE = "query.facet.fields.size";

    /** The key of the configuration. e.g. 1 */
    String QUERY_FACET_FIELDS_min_doc_count = "query.facet.fields.min_doc_count";

    /** The key of the configuration. e.g. count.desc */
    String QUERY_FACET_FIELDS_SORT = "query.facet.fields.sort";

    /** The key of the configuration. e.g.  */
    String QUERY_FACET_FIELDS_MISSING = "query.facet.fields.missing";

    /** The key of the configuration. e.g. labels.facet_timestamp_title:labels.facet_timestamp_1day=timestamp:[now/d-1d TO *]	labels.facet_timestamp_1week=timestamp:[now/d-7d TO *]	labels.facet_timestamp_1month=timestamp:[now/d-1M TO *]	labels.facet_timestamp_1year=timestamp:[now/d-1y TO *]<br>
     * labels.facet_contentLength_title:labels.facet_contentLength_10k=content_length:[0 TO 9999]	labels.facet_contentLength_10kto100k=content_length:[10000 TO 99999]	labels.facet_contentLength_100kto500k=content_length:[100000 TO 499999]	labels.facet_contentLength_500kto1m=content_length:[500000 TO 999999]	labels.facet_contentLength_1m=content_length:[1000000 TO *]<br>
     * labels.facet_filetype_title:labels.facet_filetype_html=filetype:html	labels.facet_filetype_word=filetype:word	labels.facet_filetype_excel=filetype:excel	labels.facet_filetype_powerpoint=filetype:powerpoint	labels.facet_filetype_odt=filetype:odt	labels.facet_filetype_ods=filetype:ods	labels.facet_filetype_odp=filetype:odp	labels.facet_filetype_pdf=filetype:pdf	labels.facet_filetype_txt=filetype:txt	labels.facet_filetype_others=filetype:others<br>
     *  */
    String QUERY_FACET_QUERIES = "query.facet.queries";

    /** The key of the configuration. e.g. 200 */
    String RANK_FUSION_window_size = "rank.fusion.window_size";

    /** The key of the configuration. e.g. 20 */
    String RANK_FUSION_rank_constant = "rank.fusion.rank_constant";

    /** The key of the configuration. e.g. -1 */
    String RANK_FUSION_THREADS = "rank.fusion.threads";

    /** The key of the configuration. e.g. rf_score */
    String RANK_FUSION_score_field = "rank.fusion.score_field";

    /** The key of the configuration. e.g. true */
    String SMB_ROLE_FROM_FILE = "smb.role.from.file";

    /** The key of the configuration. e.g. 1,2,4:2,5:1 */
    String SMB_AVAILABLE_SID_TYPES = "smb.available.sid.types";

    /** The key of the configuration. e.g. true */
    String FILE_ROLE_FROM_FILE = "file.role.from.file";

    /** The key of the configuration. e.g. true */
    String FTP_ROLE_FROM_FILE = "ftp.role.from.file";

    /** The key of the configuration. e.g. fess_basic_config.bulk,fess_config.bulk,fess_user.bulk,system.properties,fess.json,doc.json */
    String INDEX_BACKUP_TARGETS = "index.backup.targets";

    /** The key of the configuration. e.g. click_log.ndjson,favorite_log.ndjson,search_log.ndjson,user_info.ndjson */
    String INDEX_BACKUP_LOG_TARGETS = "index.backup.log.targets";

    /** The key of the configuration. e.g. true */
    String LOGGING_SEARCH_DOCS_ENABLED = "logging.search.docs.enabled";

    /** The key of the configuration. e.g. filetype,created,click_count,title,doc_id,url,score,site,filename,host,digest,boost,mimetype,favorite_count,_id,lang,last_modified,content_length,timestamp */
    String LOGGING_SEARCH_DOCS_FIELDS = "logging.search.docs.fields";

    /** The key of the configuration. e.g. true */
    String LOGGING_SEARCH_USE_LOGFILE = "logging.search.use.logfile";

    /** The key of the configuration. e.g. org.codelibs,org.dbflute,org.lastaflute */
    String LOGGING_APP_PACKAGES = "logging.app.packages";

    /** The key of the configuration. e.g. 10000 */
    String FORM_ADMIN_MAX_INPUT_SIZE = "form.admin.max.input.size";

    /** The key of the configuration. e.g. false */
    String FORM_ADMIN_LABEL_IN_CONFIG_ENABLED = "form.admin.label.in.config.enabled";

    /** The key of the configuration. e.g. __TEMPLATE__ */
    String FORM_ADMIN_DEFAULT_TEMPLATE_NAME = "form.admin.default.template.name";

    /** The key of the configuration. e.g. true */
    String OSDD_LINK_ENABLED = "osdd.link.enabled";

    /** The key of the configuration. e.g. true */
    String CLIPBOARD_COPY_ICON_ENABLED = "clipboard.copy.icon.enabled";

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

    /** The key of the configuration. e.g. D */
    String ROLE_SEARCH_DENIED_PREFIX = "role.search.denied.prefix";

    /** The key of the configuration. e.g. / */
    String COOKIE_DEFAULT_PATH = "cookie.default.path";

    /** The key of the configuration. e.g. 3600 */
    String COOKIE_DEFAULT_EXPIRE = "cookie.default.expire";

    /** The key of the configuration. e.g. cookie */
    String SESSION_TRACKING_MODES = "session.tracking.modes";

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

    /** The key of the configuration. e.g. 1000 */
    String PAGE_ELEVATE_WORD_MAX_FETCH_SIZE = "page.elevate.word.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_BAD_WORD_MAX_FETCH_SIZE = "page.bad.word.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_DICTIONARY_MAX_FETCH_SIZE = "page.dictionary.max.fetch.size";

    /** The key of the configuration. e.g. 5000 */
    String PAGE_RELATEDCONTENT_MAX_FETCH_SIZE = "page.relatedcontent.max.fetch.size";

    /** The key of the configuration. e.g. 5000 */
    String PAGE_RELATEDQUERY_MAX_FETCH_SIZE = "page.relatedquery.max.fetch.size";

    /** The key of the configuration. e.g. 100 */
    String PAGE_THUMBNAIL_QUEUE_MAX_FETCH_SIZE = "page.thumbnail.queue.max.fetch.size";

    /** The key of the configuration. e.g. 100 */
    String PAGE_THUMBNAIL_PURGE_MAX_FETCH_SIZE = "page.thumbnail.purge.max.fetch.size";

    /** The key of the configuration. e.g. 1000 */
    String PAGE_SCORE_BOOSTER_MAX_FETCH_SIZE = "page.score.booster.max.fetch.size";

    /** The key of the configuration. e.g. 10000 */
    String PAGE_SEARCHLOG_MAX_FETCH_SIZE = "page.searchlog.max.fetch.size";

    /** The key of the configuration. e.g. true */
    String PAGE_SEARCHLIST_TRACK_TOTAL_HITS = "page.searchlist.track.total.hits";

    /** The key of the configuration. e.g. 0 */
    String PAGING_SEARCH_PAGE_START = "paging.search.page.start";

    /** The key of the configuration. e.g. 10 */
    String PAGING_SEARCH_PAGE_SIZE = "paging.search.page.size";

    /** The key of the configuration. e.g. 100 */
    String PAGING_SEARCH_PAGE_MAX_SIZE = "paging.search.page.max.size";

    /** The key of the configuration. e.g. -1 */
    String SEARCHLOG_AGG_SHARD_SIZE = "searchlog.agg.shard.size";

    /** The key of the configuration. e.g.  */
    String SEARCHLOG_REQUEST_HEADERS = "searchlog.request.headers";

    /** The key of the configuration. e.g. 100 */
    String THUMBNAIL_HTML_IMAGE_MIN_WIDTH = "thumbnail.html.image.min.width";

    /** The key of the configuration. e.g. 100 */
    String THUMBNAIL_HTML_IMAGE_MIN_HEIGHT = "thumbnail.html.image.min.height";

    /** The key of the configuration. e.g. 3.0 */
    String THUMBNAIL_HTML_IMAGE_MAX_ASPECT_RATIO = "thumbnail.html.image.max.aspect.ratio";

    /** The key of the configuration. e.g. 100 */
    String THUMBNAIL_HTML_IMAGE_THUMBNAIL_WIDTH = "thumbnail.html.image.thumbnail.width";

    /** The key of the configuration. e.g. 100 */
    String THUMBNAIL_HTML_IMAGE_THUMBNAIL_HEIGHT = "thumbnail.html.image.thumbnail.height";

    /** The key of the configuration. e.g. png */
    String THUMBNAIL_HTML_IMAGE_FORMAT = "thumbnail.html.image.format";

    /** The key of the configuration. e.g. //IMG */
    String THUMBNAIL_HTML_IMAGE_XPATH = "thumbnail.html.image.xpath";

    /** The key of the configuration. e.g. svg,html,css,js */
    String THUMBNAIL_HTML_IMAGE_EXCLUDE_EXTENSIONS = "thumbnail.html.image.exclude.extensions";

    /** The key of the configuration. e.g. 0 */
    String THUMBNAIL_GENERATOR_INTERVAL = "thumbnail.generator.interval";

    /** The key of the configuration. e.g. all */
    String THUMBNAIL_GENERATOR_TARGETS = "thumbnail.generator.targets";

    /** The key of the configuration. e.g. true */
    String THUMBNAIL_CRAWLER_ENABLED = "thumbnail.crawler.enabled";

    /** The key of the configuration. e.g. 60 */
    String THUMBNAIL_SYSTEM_MONITOR_INTERVAL = "thumbnail.system.monitor.interval";

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
    String MAIL_HOSTNAME = "mail.hostname";

    /** The key of the configuration. e.g.  */
    String SCHEDULER_TARGET_NAME = "scheduler.target.name";

    /** The key of the configuration. e.g. org.codelibs.fess.app.job.ScriptExecutorJob */
    String SCHEDULER_JOB_CLASS = "scheduler.job.class";

    /** The key of the configuration. e.g. QUIT */
    String SCHEDULER_CONCURRENT_EXEC_MODE = "scheduler.concurrent.exec.mode";

    /** The key of the configuration. e.g. 30 */
    String SCHEDULER_MONITOR_INTERVAL = "scheduler.monitor.interval";

    /** The key of the configuration. e.g. https://fess.codelibs.org/{lang}/{version}/admin/ */
    String ONLINE_HELP_BASE_LINK = "online.help.base.link";

    /** The key of the configuration. e.g. https://fess.codelibs.org/{lang}/{version}/install/install.html */
    String ONLINE_HELP_INSTALLATION = "online.help.installation";

    /** The key of the configuration. e.g. https://fess.codelibs.org/{lang}/eol.html */
    String ONLINE_HELP_EOL = "online.help.eol";

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

    /** The key of the configuration. e.g. protwords */
    String ONLINE_HELP_NAME_DICT_PROTWORDS = "online.help.name.dict.protwords";

    /** The key of the configuration. e.g. stopwords */
    String ONLINE_HELP_NAME_DICT_STOPWORDS = "online.help.name.dict.stopwords";

    /** The key of the configuration. e.g. stemmeroverride */
    String ONLINE_HELP_NAME_DICT_STEMMEROVERRIDE = "online.help.name.dict.stemmeroverride";

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

    /** The key of the configuration. e.g. relatedquery */
    String ONLINE_HELP_NAME_RELATEDQUERY = "online.help.name.relatedquery";

    /** The key of the configuration. e.g. relatedcontent */
    String ONLINE_HELP_NAME_RELATEDCONTENT = "online.help.name.relatedcontent";

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

    /** The key of the configuration. e.g. searchlog */
    String ONLINE_HELP_NAME_SEARCHLOG = "online.help.name.searchlog";

    /** The key of the configuration. e.g. maintenance */
    String ONLINE_HELP_NAME_MAINTENANCE = "online.help.name.maintenance";

    /** The key of the configuration. e.g. plugin */
    String ONLINE_HELP_NAME_PLUGIN = "online.help.name.plugin";

    /** The key of the configuration. e.g. storage */
    String ONLINE_HELP_NAME_STORAGE = "online.help.name.storage";

    /** The key of the configuration. e.g. ja */
    String ONLINE_HELP_SUPPORTED_LANGS = "online.help.supported.langs";

    /** The key of the configuration. e.g. https://discuss.codelibs.org/c/Fess{lang}/ */
    String FORUM_LINK = "forum.link";

    /** The key of the configuration. e.g. en,ja */
    String FORUM_SUPPORTED_LANGS = "forum.supported.langs";

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

    /** The key of the configuration. e.g. 10 */
    String SUGGEST_POPULAR_WORD_QUERY_FREQ = "suggest.popular.word.query.freq";

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

    /** The key of the configuration. e.g. 0 */
    String SUGGEST_UPDATE_REQUEST_INTERVAL = "suggest.update.request.interval";

    /** The key of the configuration. e.g. 2 */
    String SUGGEST_UPDATE_DOC_PER_REQUEST = "suggest.update.doc.per.request";

    /** The key of the configuration. e.g. 50% */
    String SUGGEST_UPDATE_CONTENTS_LIMIT_NUM_PERCENTAGE = "suggest.update.contents.limit.num.percentage";

    /** The key of the configuration. e.g. 10000 */
    String SUGGEST_UPDATE_CONTENTS_LIMIT_NUM = "suggest.update.contents.limit.num";

    /** The key of the configuration. e.g. 50000 */
    String SUGGEST_UPDATE_CONTENTS_LIMIT_DOC_SIZE = "suggest.update.contents.limit.doc.size";

    /** The key of the configuration. e.g. 1 */
    String SUGGEST_SOURCE_READER_SCROLL_SIZE = "suggest.source.reader.scroll.size";

    /** The key of the configuration. e.g. 1000 */
    String SUGGEST_POPULAR_WORD_CACHE_SIZE = "suggest.popular.word.cache.size";

    /** The key of the configuration. e.g. 60 */
    String SUGGEST_POPULAR_WORD_CACHE_EXPIRE = "suggest.popular.word.cache.expire";

    /** The key of the configuration. e.g. {user}guest,{role}guest */
    String SUGGEST_SEARCH_LOG_PERMISSIONS = "suggest.search.log.permissions";

    /** The key of the configuration. e.g. 60 */
    String SUGGEST_SYSTEM_MONITOR_INTERVAL = "suggest.system.monitor.interval";

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

    /** The key of the configuration. e.g. true */
    String LDAP_AUTH_VALIDATION = "ldap.auth.validation";

    /** The key of the configuration. e.g. -1 */
    String LDAP_MAX_USERNAME_LENGTH = "ldap.max.username.length";

    /** The key of the configuration. e.g. true */
    String LDAP_IGNORE_NETBIOS_NAME = "ldap.ignore.netbios.name";

    /** The key of the configuration. e.g. false */
    String LDAP_GROUP_NAME_WITH_UNDERSCORES = "ldap.group.name.with.underscores";

    /** The key of the configuration. e.g. false */
    String LDAP_LOWERCASE_PERMISSION_NAME = "ldap.lowercase.permission.name";

    /** The key of the configuration. e.g. true */
    String LDAP_ALLOW_EMPTY_PERMISSION = "ldap.allow.empty.permission";

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
    String LDAP_ATTR_LABELED_U_R_I = "ldap.attr.labeledURI";

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
    String LDAP_ATTR_INTERNATIONALI_S_D_N_NUMBER = "ldap.attr.internationaliSDNNumber";

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

    /** The key of the configuration. e.g. https://repo.maven.apache.org/maven2/org/codelibs/fess/,https://fess.codelibs.org/plugin/artifacts.yaml */
    String PLUGIN_REPOSITORIES = "plugin.repositories";

    /** The key of the configuration. e.g.  */
    String PLUGIN_VERSION_FILTER = "plugin.version.filter";

    /** The key of the configuration. e.g. 1000 */
    String STORAGE_MAX_ITEMS_IN_PAGE = "storage.max.items.in.page";

    /** The key of the configuration. e.g. admin */
    String PASSWORD_INVALID_ADMIN_PASSWORDS = "password.invalid.admin.passwords";

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
     * Get the value for the key 'search_engine.type'. <br>
     * The value is, e.g. default <br>
     * comment: Search Engine
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSearchEngineType();

    /**
     * Get the value for the key 'search_engine.http.url'. <br>
     * The value is, e.g. http://localhost:9201 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSearchEngineHttpUrl();

    /**
     * Get the value for the key 'search_engine.http.ssl.certificate_authorities'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSearchEngineHttpSslCertificateAuthorities();

    /**
     * Get the value for the key 'search_engine.http.ssl.certificate_authorities' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSearchEngineHttpSslCertificateAuthoritiesAsInteger();

    /**
     * Get the value for the key 'search_engine.username'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSearchEngineUsername();

    /**
     * Get the value for the key 'search_engine.username' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSearchEngineUsernameAsInteger();

    /**
     * Get the value for the key 'search_engine.password'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSearchEnginePassword();

    /**
     * Get the value for the key 'search_engine.password' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSearchEnginePasswordAsInteger();

    /**
     * Get the value for the key 'search_engine.heartbeat_interval'. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSearchEngineHeartbeatInterval();

    /**
     * Get the value for the key 'search_engine.heartbeat_interval' as {@link Integer}. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSearchEngineHeartbeatIntervalAsInteger();

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
     * Get the value for the key 'app.encrypt.property.pattern'. <br>
     * The value is, e.g. .*password|.*key|.*token|.*secret <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAppEncryptPropertyPattern();

    /**
     * Get the value for the key 'app.extension.names'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAppExtensionNames();

    /**
     * Get the value for the key 'app.extension.names' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getAppExtensionNamesAsInteger();

    /**
     * Get the value for the key 'app.audit.log.format'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAppAuditLogFormat();

    /**
     * Get the value for the key 'app.audit.log.format' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getAppAuditLogFormatAsInteger();

    /**
     * Get the value for the key 'jvm.crawler.options'. <br>
     * The value is, e.g. -Djava.awt.headless=true<br>
     * -Dfile.encoding=UTF-8<br>
     * -Djna.nosys=true<br>
     * -Djdk.io.permissionsUseCanonicalPath=true<br>
     * -Dhttp.maxConnections=20<br>
     * -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager<br>
     * -server<br>
     * -Xms128m<br>
     * -Xmx512m<br>
     * -XX:MaxMetaspaceSize=128m<br>
     * -XX:CompressedClassSpaceSize=32m<br>
     * -XX:-UseGCOverheadLimit<br>
     * -XX:+UseTLAB<br>
     * -XX:+DisableExplicitGC<br>
     * -XX:-HeapDumpOnOutOfMemoryError<br>
     * -XX:-OmitStackTraceInFastThrow<br>
     * -XX:+UnlockExperimentalVMOptions<br>
     * -XX:+UseG1GC<br>
     * -XX:InitiatingHeapOccupancyPercent=45<br>
     * -XX:G1HeapRegionSize=1m<br>
     * -XX:MaxGCPauseMillis=60000<br>
     * -XX:G1NewSizePercent=5<br>
     * -XX:G1MaxNewSizePercent=5<br>
     * -Djcifs.smb.client.responseTimeout=30000<br>
     * -Djcifs.smb.client.soTimeout=35000<br>
     * -Djcifs.smb.client.connTimeout=60000<br>
     * -Djcifs.smb.client.sessionTimeout=60000<br>
     * -Djcifs.smb1.smb.client.connTimeout=60000<br>
     * -Djcifs.smb1.smb.client.soTimeout=35000<br>
     * -Djcifs.smb1.smb.client.responseTimeout=30000<br>
     * -Dio.netty.noUnsafe=true<br>
     * -Dio.netty.noKeySetOptimization=true<br>
     * -Dio.netty.recycler.maxCapacityPerThread=0<br>
     * -Dlog4j.shutdownHookEnabled=false<br>
     * -Dlog4j2.formatMsgNoLookups=true<br>
     * -Dlog4j2.disable.jmx=true<br>
     * -Dlog4j.skipJansi=true<br>
     * -Dsun.java2d.cmm=sun.java2d.cmm.kcms.KcmsServiceProvider<br>
     * -Dorg.apache.pdfbox.rendering.UsePureJavaCMYKConversion=true<br>
     *  <br>
     * comment: JVM options
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJvmCrawlerOptions();

    /**
     * Get the value for the key 'jvm.suggest.options'. <br>
     * The value is, e.g. -Djava.awt.headless=true<br>
     * -Dfile.encoding=UTF-8<br>
     * -Djna.nosys=true<br>
     * -Djdk.io.permissionsUseCanonicalPath=true<br>
     * -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager<br>
     * -server<br>
     * -Xms128m<br>
     * -Xmx256m<br>
     * -XX:MaxMetaspaceSize=128m<br>
     * -XX:CompressedClassSpaceSize=32m<br>
     * -XX:-UseGCOverheadLimit<br>
     * -XX:+UseTLAB<br>
     * -XX:+DisableExplicitGC<br>
     * -XX:-HeapDumpOnOutOfMemoryError<br>
     * -XX:+UnlockExperimentalVMOptions<br>
     * -XX:+UseG1GC<br>
     * -XX:InitiatingHeapOccupancyPercent=45<br>
     * -XX:G1HeapRegionSize=1m<br>
     * -XX:MaxGCPauseMillis=60000<br>
     * -XX:G1NewSizePercent=5<br>
     * -XX:G1MaxNewSizePercent=30<br>
     * -Dio.netty.noUnsafe=true<br>
     * -Dio.netty.noKeySetOptimization=true<br>
     * -Dio.netty.recycler.maxCapacityPerThread=0<br>
     * -Dlog4j.shutdownHookEnabled=false<br>
     * -Dlog4j2.disable.jmx=true<br>
     * -Dlog4j2.formatMsgNoLookups=true<br>
     * -Dlog4j.skipJansi=true<br>
     *  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJvmSuggestOptions();

    /**
     * Get the value for the key 'jvm.thumbnail.options'. <br>
     * The value is, e.g. -Djava.awt.headless=true<br>
     * -Dfile.encoding=UTF-8<br>
     * -Djna.nosys=true<br>
     * -Djdk.io.permissionsUseCanonicalPath=true<br>
     * -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager<br>
     * -server<br>
     * -Xms128m<br>
     * -Xmx256m<br>
     * -XX:MaxMetaspaceSize=128m<br>
     * -XX:CompressedClassSpaceSize=32m<br>
     * -XX:-UseGCOverheadLimit<br>
     * -XX:+UseTLAB<br>
     * -XX:+DisableExplicitGC<br>
     * -XX:-HeapDumpOnOutOfMemoryError<br>
     * -XX:-OmitStackTraceInFastThrow<br>
     * -XX:+UnlockExperimentalVMOptions<br>
     * -XX:+UseG1GC<br>
     * -XX:InitiatingHeapOccupancyPercent=45<br>
     * -XX:G1HeapRegionSize=4m<br>
     * -XX:MaxGCPauseMillis=60000<br>
     * -XX:G1NewSizePercent=5<br>
     * -XX:G1MaxNewSizePercent=50<br>
     * -Djcifs.smb.client.responseTimeout=30000<br>
     * -Djcifs.smb.client.soTimeout=35000<br>
     * -Djcifs.smb.client.connTimeout=60000<br>
     * -Djcifs.smb.client.sessionTimeout=60000<br>
     * -Djcifs.smb1.smb.client.connTimeout=60000<br>
     * -Djcifs.smb1.smb.client.soTimeout=35000<br>
     * -Djcifs.smb1.smb.client.responseTimeout=30000<br>
     * -Dio.netty.noUnsafe=true<br>
     * -Dio.netty.noKeySetOptimization=true<br>
     * -Dio.netty.recycler.maxCapacityPerThread=0<br>
     * -Dlog4j.shutdownHookEnabled=false<br>
     * -Dlog4j2.disable.jmx=true<br>
     * -Dlog4j2.formatMsgNoLookups=true<br>
     * -Dlog4j.skipJansi=true<br>
     * -Dsun.java2d.cmm=sun.java2d.cmm.kcms.KcmsServiceProvider<br>
     * -Dorg.apache.pdfbox.rendering.UsePureJavaCMYKConversion=true<br>
     *  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJvmThumbnailOptions();

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
     * Get the value for the key 'job.max.crawler.processes'. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJobMaxCrawlerProcesses();

    /**
     * Get the value for the key 'job.max.crawler.processes' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getJobMaxCrawlerProcessesAsInteger();

    /**
     * Get the value for the key 'job.default.script'. <br>
     * The value is, e.g. groovy <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJobDefaultScript();

    /**
     * Get the value for the key 'processors'. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getProcessors();

    /**
     * Get the value for the key 'processors' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getProcessorsAsInteger();

    /**
     * Get the value for the key 'java.command.path'. <br>
     * The value is, e.g. java <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getJavaCommandPath();

    /**
     * Get the value for the key 'python.command.path'. <br>
     * The value is, e.g. python <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPythonCommandPath();

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
     * Get the value for the key 'adaptive.load.control'. <br>
     * The value is, e.g. 50 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getAdaptiveLoadControl();

    /**
     * Get the value for the key 'adaptive.load.control' as {@link Integer}. <br>
     * The value is, e.g. 50 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getAdaptiveLoadControlAsInteger();

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
     * The value is, e.g. ar,bg,bn,ca,ckb_IQ,cs,da,de,el,en_IE,en,es,et,eu,fa,fi,fr,gl,gu,he,hi,hr,hu,hy,id,it,ja,ko,lt,lv,mk,ml,nl,no,pa,pl,pt_BR,pt,ro,ru,si,sq,sv,ta,te,th,tl,tr,uk,ur,vi,zh_CN,zh_TW,zh <br>
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
     * Get the value for the key 'api.access.token.request.parameter'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiAccessTokenRequestParameter();

    /**
     * Get the value for the key 'api.access.token.request.parameter' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getApiAccessTokenRequestParameterAsInteger();

    /**
     * Get the value for the key 'api.admin.access.permissions'. <br>
     * The value is, e.g. Radmin-api <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiAdminAccessPermissions();

    /**
     * Get the value for the key 'api.search.accept.referers'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiSearchAcceptReferers();

    /**
     * Get the value for the key 'api.search.accept.referers' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getApiSearchAcceptReferersAsInteger();

    /**
     * Get the value for the key 'api.search.scroll'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiSearchScroll();

    /**
     * Is the property for the key 'api.search.scroll' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isApiSearchScroll();

    /**
     * Get the value for the key 'api.json.response.headers'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiJsonResponseHeaders();

    /**
     * Get the value for the key 'api.json.response.headers' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getApiJsonResponseHeadersAsInteger();

    /**
     * Get the value for the key 'api.json.response.exception.included'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiJsonResponseExceptionIncluded();

    /**
     * Is the property for the key 'api.json.response.exception.included' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isApiJsonResponseExceptionIncluded();

    /**
     * Get the value for the key 'api.gsa.response.headers'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiGsaResponseHeaders();

    /**
     * Get the value for the key 'api.gsa.response.headers' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getApiGsaResponseHeadersAsInteger();

    /**
     * Get the value for the key 'api.gsa.response.exception.included'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiGsaResponseExceptionIncluded();

    /**
     * Is the property for the key 'api.gsa.response.exception.included' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isApiGsaResponseExceptionIncluded();

    /**
     * Get the value for the key 'api.dashboard.response.headers'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiDashboardResponseHeaders();

    /**
     * Get the value for the key 'api.dashboard.response.headers' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getApiDashboardResponseHeadersAsInteger();

    /**
     * Get the value for the key 'api.cors.allow.origin'. <br>
     * The value is, e.g. * <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiCorsAllowOrigin();

    /**
     * Get the value for the key 'api.cors.allow.methods'. <br>
     * The value is, e.g. GET, POST, OPTIONS, DELETE, PUT <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiCorsAllowMethods();

    /**
     * Get the value for the key 'api.cors.max.age'. <br>
     * The value is, e.g. 3600 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiCorsMaxAge();

    /**
     * Get the value for the key 'api.cors.max.age' as {@link Integer}. <br>
     * The value is, e.g. 3600 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getApiCorsMaxAgeAsInteger();

    /**
     * Get the value for the key 'api.cors.allow.headers'. <br>
     * The value is, e.g. Origin, Content-Type, Accept, Authorization, X-Requested-With <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiCorsAllowHeaders();

    /**
     * Get the value for the key 'api.cors.allow.credentials'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiCorsAllowCredentials();

    /**
     * Is the property for the key 'api.cors.allow.credentials' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isApiCorsAllowCredentials();

    /**
     * Get the value for the key 'api.jsonp.enabled'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiJsonpEnabled();

    /**
     * Is the property for the key 'api.jsonp.enabled' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isApiJsonpEnabled();

    /**
     * Get the value for the key 'api.ping.search_engine.fields'. <br>
     * The value is, e.g. status,timed_out <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getApiPingSearchEngineFields();

    /**
     * Get the value for the key 'virtual.host.headers'. <br>
     * The value is, e.g.  <br>
     * comment: Virtual Host: Host:fess.codelibs.org=fess
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getVirtualHostHeaders();

    /**
     * Get the value for the key 'virtual.host.headers' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * comment: Virtual Host: Host:fess.codelibs.org=fess
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getVirtualHostHeadersAsInteger();

    /**
     * Get the value for the key 'http.proxy.host'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getHttpProxyHost();

    /**
     * Get the value for the key 'http.proxy.host' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getHttpProxyHostAsInteger();

    /**
     * Get the value for the key 'http.proxy.port'. <br>
     * The value is, e.g. 8080 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getHttpProxyPort();

    /**
     * Get the value for the key 'http.proxy.port' as {@link Integer}. <br>
     * The value is, e.g. 8080 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getHttpProxyPortAsInteger();

    /**
     * Get the value for the key 'http.proxy.username'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getHttpProxyUsername();

    /**
     * Get the value for the key 'http.proxy.username' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getHttpProxyUsernameAsInteger();

    /**
     * Get the value for the key 'http.proxy.password'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getHttpProxyPassword();

    /**
     * Get the value for the key 'http.proxy.password' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getHttpProxyPasswordAsInteger();

    /**
     * Get the value for the key 'http.fileupload.max.size'. <br>
     * The value is, e.g. 262144000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getHttpFileuploadMaxSize();

    /**
     * Get the value for the key 'http.fileupload.max.size' as {@link Integer}. <br>
     * The value is, e.g. 262144000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getHttpFileuploadMaxSizeAsInteger();

    /**
     * Get the value for the key 'http.fileupload.threshold.size'. <br>
     * The value is, e.g. 262144 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getHttpFileuploadThresholdSize();

    /**
     * Get the value for the key 'http.fileupload.threshold.size' as {@link Integer}. <br>
     * The value is, e.g. 262144 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getHttpFileuploadThresholdSizeAsInteger();

    /**
     * Get the value for the key 'crawler.default.script'. <br>
     * The value is, e.g. groovy <br>
     * comment: common
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDefaultScript();

    /**
     * Get the value for the key 'crawler.http.thread_pool.size'. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerHttpThreadPoolSize();

    /**
     * Get the value for the key 'crawler.http.thread_pool.size' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerHttpThreadPoolSizeAsInteger();

    /**
     * Get the value for the key 'crawler.document.max.site.length'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentMaxSiteLength();

    /**
     * Get the value for the key 'crawler.document.max.site.length' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
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
     * Get the value for the key 'crawler.document.append.filename'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentAppendFilename();

    /**
     * Is the property for the key 'crawler.document.append.filename' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerDocumentAppendFilename();

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
     * Get the value for the key 'crawler.document.fullstop.chars'. <br>
     * The value is, e.g. u002eu06d4u2e3cu3002 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFullstopChars();

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
     * The value is, e.g. file,smb,smb1,ftp,storage <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerFileProtocols();

    /**
     * Get the value for the key 'crawler.data.env.param.key.pattern'. <br>
     * The value is, e.g. ^FESS_ENV_.* <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDataEnvParamKeyPattern();

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
     * Get the value for the key 'crawler.ignore.robots.tags'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerIgnoreRobotsTags();

    /**
     * Is the property for the key 'crawler.ignore.robots.tags' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerIgnoreRobotsTags();

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
     * Get the value for the key 'crawler.failure.url.status.codes'. <br>
     * The value is, e.g. 404 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerFailureUrlStatusCodes();

    /**
     * Get the value for the key 'crawler.failure.url.status.codes' as {@link Integer}. <br>
     * The value is, e.g. 404 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerFailureUrlStatusCodesAsInteger();

    /**
     * Get the value for the key 'crawler.system.monitor.interval'. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerSystemMonitorInterval();

    /**
     * Get the value for the key 'crawler.system.monitor.interval' as {@link Integer}. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerSystemMonitorIntervalAsInteger();

    /**
     * Get the value for the key 'crawler.hotthread.ignore_idle_threads'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerHotthreadIgnoreIdleThreads();

    /**
     * Is the property for the key 'crawler.hotthread.ignore_idle_threads' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isCrawlerHotthreadIgnoreIdleThreads();

    /**
     * Get the value for the key 'crawler.hotthread.interval'. <br>
     * The value is, e.g. 500ms <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerHotthreadInterval();

    /**
     * Get the value for the key 'crawler.hotthread.snapshots'. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerHotthreadSnapshots();

    /**
     * Get the value for the key 'crawler.hotthread.snapshots' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerHotthreadSnapshotsAsInteger();

    /**
     * Get the value for the key 'crawler.hotthread.threads'. <br>
     * The value is, e.g. 3 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerHotthreadThreads();

    /**
     * Get the value for the key 'crawler.hotthread.threads' as {@link Integer}. <br>
     * The value is, e.g. 3 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerHotthreadThreadsAsInteger();

    /**
     * Get the value for the key 'crawler.hotthread.timeout'. <br>
     * The value is, e.g. 30s <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerHotthreadTimeout();

    /**
     * Get the value for the key 'crawler.hotthread.type'. <br>
     * The value is, e.g. cpu <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerHotthreadType();

    /**
     * Get the value for the key 'crawler.metadata.content.excludes'. <br>
     * The value is, e.g. resourceName,X-Parsed-By,Content-Encoding.*,Content-Type.*,X-TIKA.* <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerMetadataContentExcludes();

    /**
     * Get the value for the key 'crawler.metadata.name.mapping'. <br>
     * The value is, e.g. title=title:string<br>
     * Title=title:string<br>
     * dc:title=title:string<br>
     *  <br>
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
     * Get the value for the key 'crawler.document.html.canonical.xpath'. <br>
     * The value is, e.g. //LINK[@rel='canonical'][1]/@href <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlCanonicalXpath();

    /**
     * Get the value for the key 'crawler.document.html.pruned.tags'. <br>
     * The value is, e.g. noscript,script,style,header,footer,nav,a[rel=nofollow] <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlPrunedTags();

    /**
     * Get the value for the key 'crawler.document.html.max.digest.length'. <br>
     * The value is, e.g. 120 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlMaxDigestLength();

    /**
     * Get the value for the key 'crawler.document.html.max.digest.length' as {@link Integer}. <br>
     * The value is, e.g. 120 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentHtmlMaxDigestLengthAsInteger();

    /**
     * Get the value for the key 'crawler.document.html.default.lang'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlDefaultLang();

    /**
     * Get the value for the key 'crawler.document.html.default.lang' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentHtmlDefaultLangAsInteger();

    /**
     * Get the value for the key 'crawler.document.html.default.include.index.patterns'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlDefaultIncludeIndexPatterns();

    /**
     * Get the value for the key 'crawler.document.html.default.include.index.patterns' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentHtmlDefaultIncludeIndexPatternsAsInteger();

    /**
     * Get the value for the key 'crawler.document.html.default.exclude.index.patterns'. <br>
     * The value is, e.g. (?i).*(css|js|jpeg|jpg|gif|png|bmp|wmv|xml|ico|exe) <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlDefaultExcludeIndexPatterns();

    /**
     * Get the value for the key 'crawler.document.html.default.include.search.patterns'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlDefaultIncludeSearchPatterns();

    /**
     * Get the value for the key 'crawler.document.html.default.include.search.patterns' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentHtmlDefaultIncludeSearchPatternsAsInteger();

    /**
     * Get the value for the key 'crawler.document.html.default.exclude.search.patterns'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentHtmlDefaultExcludeSearchPatterns();

    /**
     * Get the value for the key 'crawler.document.html.default.exclude.search.patterns' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentHtmlDefaultExcludeSearchPatternsAsInteger();

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
     * Get the value for the key 'crawler.document.file.default.include.index.patterns'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileDefaultIncludeIndexPatterns();

    /**
     * Get the value for the key 'crawler.document.file.default.include.index.patterns' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentFileDefaultIncludeIndexPatternsAsInteger();

    /**
     * Get the value for the key 'crawler.document.file.default.exclude.index.patterns'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileDefaultExcludeIndexPatterns();

    /**
     * Get the value for the key 'crawler.document.file.default.exclude.index.patterns' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentFileDefaultExcludeIndexPatternsAsInteger();

    /**
     * Get the value for the key 'crawler.document.file.default.include.search.patterns'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileDefaultIncludeSearchPatterns();

    /**
     * Get the value for the key 'crawler.document.file.default.include.search.patterns' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentFileDefaultIncludeSearchPatternsAsInteger();

    /**
     * Get the value for the key 'crawler.document.file.default.exclude.search.patterns'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getCrawlerDocumentFileDefaultExcludeSearchPatterns();

    /**
     * Get the value for the key 'crawler.document.file.default.exclude.search.patterns' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getCrawlerDocumentFileDefaultExcludeSearchPatternsAsInteger();

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
     * The value is, e.g. 3600 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerWebfsMaxEmptyListCount();

    /**
     * Get the value for the key 'indexer.webfs.max.empty.list.count' as {@link Integer}. <br>
     * The value is, e.g. 3600 <br>
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
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerWebfsMaxDocumentCacheSize();

    /**
     * Get the value for the key 'indexer.webfs.max.document.cache.size' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerWebfsMaxDocumentCacheSizeAsInteger();

    /**
     * Get the value for the key 'indexer.webfs.max.document.request.size'. <br>
     * The value is, e.g. 1048576 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerWebfsMaxDocumentRequestSize();

    /**
     * Get the value for the key 'indexer.webfs.max.document.request.size' as {@link Integer}. <br>
     * The value is, e.g. 1048576 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerWebfsMaxDocumentRequestSizeAsInteger();

    /**
     * Get the value for the key 'indexer.data.max.document.cache.size'. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerDataMaxDocumentCacheSize();

    /**
     * Get the value for the key 'indexer.data.max.document.cache.size' as {@link Integer}. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerDataMaxDocumentCacheSizeAsInteger();

    /**
     * Get the value for the key 'indexer.data.max.document.request.size'. <br>
     * The value is, e.g. 1048576 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerDataMaxDocumentRequestSize();

    /**
     * Get the value for the key 'indexer.data.max.document.request.size' as {@link Integer}. <br>
     * The value is, e.g. 1048576 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerDataMaxDocumentRequestSizeAsInteger();

    /**
     * Get the value for the key 'indexer.data.max.delete.cache.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerDataMaxDeleteCacheSize();

    /**
     * Get the value for the key 'indexer.data.max.delete.cache.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerDataMaxDeleteCacheSizeAsInteger();

    /**
     * Get the value for the key 'indexer.data.max.redirect.count'. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerDataMaxRedirectCount();

    /**
     * Get the value for the key 'indexer.data.max.redirect.count' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerDataMaxRedirectCountAsInteger();

    /**
     * Get the value for the key 'indexer.language.fields'. <br>
     * The value is, e.g. content,important_content,title <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerLanguageFields();

    /**
     * Get the value for the key 'indexer.language.detect.length'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerLanguageDetectLength();

    /**
     * Get the value for the key 'indexer.language.detect.length' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerLanguageDetectLengthAsInteger();

    /**
     * Get the value for the key 'indexer.max.result.window.size'. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerMaxResultWindowSize();

    /**
     * Get the value for the key 'indexer.max.result.window.size' as {@link Integer}. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerMaxResultWindowSizeAsInteger();

    /**
     * Get the value for the key 'indexer.max.search.doc.size'. <br>
     * The value is, e.g. 50000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexerMaxSearchDocSize();

    /**
     * Get the value for the key 'indexer.max.search.doc.size' as {@link Integer}. <br>
     * The value is, e.g. 50000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexerMaxSearchDocSizeAsInteger();

    /**
     * Get the value for the key 'index.codec'. <br>
     * The value is, e.g. default <br>
     * comment: index setting
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexCodec();

    /**
     * Get the value for the key 'index.number_of_shards'. <br>
     * The value is, e.g. 5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexNumberOfShards();

    /**
     * Get the value for the key 'index.number_of_shards' as {@link Integer}. <br>
     * The value is, e.g. 5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexNumberOfShardsAsInteger();

    /**
     * Get the value for the key 'index.auto_expand_replicas'. <br>
     * The value is, e.g. 0-1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexAutoExpandReplicas();

    /**
     * Get the value for the key 'index.id.digest.algorithm'. <br>
     * The value is, e.g. SHA-512 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexIdDigestAlgorithm();

    /**
     * Get the value for the key 'index.user.initial_password'. <br>
     * The value is, e.g. admin <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexUserInitialPassword();

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
     * Get the value for the key 'index.field.seq_no'. <br>
     * The value is, e.g. _seq_no <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldSeqNo();

    /**
     * Get the value for the key 'index.field.primary_term'. <br>
     * The value is, e.g. _primary_term <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldPrimaryTerm();

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
     * Get the value for the key 'index.field.content_minhash_bits'. <br>
     * The value is, e.g. content_minhash_bits <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldContentMinhashBits();

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
     * Get the value for the key 'index.field.thumbnail'. <br>
     * The value is, e.g. thumbnail <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldThumbnail();

    /**
     * Get the value for the key 'index.field.virtual_host'. <br>
     * The value is, e.g. virtual_host <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFieldVirtualHost();

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
     * Get the value for the key 'response.max.title.length'. <br>
     * The value is, e.g. 50 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getResponseMaxTitleLength();

    /**
     * Get the value for the key 'response.max.title.length' as {@link Integer}. <br>
     * The value is, e.g. 50 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getResponseMaxTitleLengthAsInteger();

    /**
     * Get the value for the key 'response.max.site.path.length'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getResponseMaxSitePathLength();

    /**
     * Get the value for the key 'response.max.site.path.length' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getResponseMaxSitePathLengthAsInteger();

    /**
     * Get the value for the key 'response.highlight.content_title.enabled'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getResponseHighlightContentTitleEnabled();

    /**
     * Is the property for the key 'response.highlight.content_title.enabled' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isResponseHighlightContentTitleEnabled();

    /**
     * Get the value for the key 'response.inline.mimetypes'. <br>
     * The value is, e.g. application/pdf,text/plain <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getResponseInlineMimetypes();

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
     * Get the value for the key 'index.document.suggest.index'. <br>
     * The value is, e.g. fess <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentSuggestIndex();

    /**
     * Get the value for the key 'index.document.crawler.index'. <br>
     * The value is, e.g. fess_crawler <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentCrawlerIndex();

    /**
     * Get the value for the key 'index.document.crawler.queue.number_of_shards'. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentCrawlerQueueNumberOfShards();

    /**
     * Get the value for the key 'index.document.crawler.queue.number_of_shards' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexDocumentCrawlerQueueNumberOfShardsAsInteger();

    /**
     * Get the value for the key 'index.document.crawler.data.number_of_shards'. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentCrawlerDataNumberOfShards();

    /**
     * Get the value for the key 'index.document.crawler.data.number_of_shards' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexDocumentCrawlerDataNumberOfShardsAsInteger();

    /**
     * Get the value for the key 'index.document.crawler.filter.number_of_shards'. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentCrawlerFilterNumberOfShards();

    /**
     * Get the value for the key 'index.document.crawler.filter.number_of_shards' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexDocumentCrawlerFilterNumberOfShardsAsInteger();

    /**
     * Get the value for the key 'index.document.crawler.queue.number_of_replicas'. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentCrawlerQueueNumberOfReplicas();

    /**
     * Get the value for the key 'index.document.crawler.queue.number_of_replicas' as {@link Integer}. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexDocumentCrawlerQueueNumberOfReplicasAsInteger();

    /**
     * Get the value for the key 'index.document.crawler.data.number_of_replicas'. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentCrawlerDataNumberOfReplicas();

    /**
     * Get the value for the key 'index.document.crawler.data.number_of_replicas' as {@link Integer}. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexDocumentCrawlerDataNumberOfReplicasAsInteger();

    /**
     * Get the value for the key 'index.document.crawler.filter.number_of_replicas'. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexDocumentCrawlerFilterNumberOfReplicas();

    /**
     * Get the value for the key 'index.document.crawler.filter.number_of_replicas' as {@link Integer}. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexDocumentCrawlerFilterNumberOfReplicasAsInteger();

    /**
     * Get the value for the key 'index.config.index'. <br>
     * The value is, e.g. fess_config <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexConfigIndex();

    /**
     * Get the value for the key 'index.user.index'. <br>
     * The value is, e.g. fess_user <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexUserIndex();

    /**
     * Get the value for the key 'index.log.index'. <br>
     * The value is, e.g. fess_log <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexLogIndex();

    /**
     * Get the value for the key 'index.admin.array.fields'. <br>
     * The value is, e.g. lang,role,label,anchor,virtual_host <br>
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
     * The value is, e.g. url,title,role,boost <br>
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
     * Get the value for the key 'index.scroll.search.timeout'. <br>
     * The value is, e.g. 3m <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexScrollSearchTimeout();

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
     * Get the value for the key 'index.filetype'. <br>
     * The value is, e.g. text/html=html<br>
     * application/msword=word<br>
     * application/vnd.openxmlformats-officedocument.wordprocessingml.document=word<br>
     * application/vnd.ms-excel=excel<br>
     * application/vnd.ms-excel.sheet.2=excel<br>
     * application/vnd.ms-excel.sheet.3=excel<br>
     * application/vnd.ms-excel.sheet.4=excel<br>
     * application/vnd.ms-excel.workspace.3=excel<br>
     * application/vnd.ms-excel.workspace.4=excel<br>
     * application/vnd.openxmlformats-officedocument.spreadsheetml.sheet=excel<br>
     * application/vnd.ms-powerpoint=powerpoint<br>
     * application/vnd.openxmlformats-officedocument.presentationml.presentation=powerpoint<br>
     * application/vnd.oasis.opendocument.text=odt<br>
     * application/vnd.oasis.opendocument.spreadsheet=ods<br>
     * application/vnd.oasis.opendocument.presentation=odp<br>
     * application/pdf=pdf<br>
     * application/x-fictionbook+xml=fb2<br>
     * application/e-pub+zip=epub<br>
     * application/x-ibooks+zip=ibooks<br>
     * text/plain=txt<br>
     * application/rtf=rtf<br>
     * application/vnd.ms-htmlhelp=chm<br>
     * application/zip=zip<br>
     * application/x-7z-comressed=7z<br>
     * application/x-bzip=bz<br>
     * application/x-bzip2=bz2<br>
     * application/x-tar=tar<br>
     * application/x-rar-compressed=rar<br>
     * video/3gp=3gp<br>
     * video/3g2=3g2<br>
     * video/x-msvideo=avi<br>
     * video/x-flv=flv<br>
     * video/mpeg=mpeg<br>
     * video/mp4=mp4<br>
     * video/ogv=ogv<br>
     * video/quicktime=qt<br>
     * video/x-m4v=m4v<br>
     * audio/x-aif=aif<br>
     * audio/midi=midi<br>
     * audio/mpga=mpga<br>
     * audio/mp4=mp4a<br>
     * audio/ogg=oga<br>
     * audio/x-wav=wav<br>
     * image/webp=webp<br>
     * image/bmp=bmp<br>
     * image/x-icon=ico<br>
     * image/x-icon=ico<br>
     * image/png=png<br>
     * image/svg+xml=svg<br>
     * image/tiff=tiff<br>
     * image/jpeg=jpg<br>
     *  <br>
     * comment: filetype
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexFiletype();

    /**
     * Get the value for the key 'index.reindex.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexReindexSize();

    /**
     * Get the value for the key 'index.reindex.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexReindexSizeAsInteger();

    /**
     * Get the value for the key 'index.reindex.body'. <br>
     * The value is, e.g. {"source":{"index":"__SOURCE_INDEX__","size":__SIZE__},"dest":{"index":"__DEST_INDEX__"},"script":{"source":"__SCRIPT_SOURCE__"}} <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexReindexBody();

    /**
     * Get the value for the key 'index.reindex.requests_per_second'. <br>
     * The value is, e.g. adaptive <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexReindexRequestsPerSecond();

    /**
     * Get the value for the key 'index.reindex.refresh'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexReindexRefresh();

    /**
     * Is the property for the key 'index.reindex.refresh' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isIndexReindexRefresh();

    /**
     * Get the value for the key 'index.reindex.timeout'. <br>
     * The value is, e.g. 1m <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexReindexTimeout();

    /**
     * Get the value for the key 'index.reindex.scroll'. <br>
     * The value is, e.g. 5m <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexReindexScroll();

    /**
     * Get the value for the key 'index.reindex.max_docs'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexReindexMaxDocs();

    /**
     * Get the value for the key 'index.reindex.max_docs' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getIndexReindexMaxDocsAsInteger();

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
     * Get the value for the key 'query.timeout'. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryTimeout();

    /**
     * Get the value for the key 'query.timeout' as {@link Integer}. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryTimeoutAsInteger();

    /**
     * Get the value for the key 'query.timeout.logging'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryTimeoutLogging();

    /**
     * Is the property for the key 'query.timeout.logging' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isQueryTimeoutLogging();

    /**
     * Get the value for the key 'query.track.total.hits'. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryTrackTotalHits();

    /**
     * Get the value for the key 'query.track.total.hits' as {@link Integer}. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryTrackTotalHitsAsInteger();

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
     * Get the value for the key 'query.highlight.terminal.chars'. <br>
     * The value is, e.g. u0021u002Cu002Eu003Fu0589u061Fu06D4u0700u0701u0702u0964u104Au104Bu1362u1367u1368u166Eu1803u1809u203Cu203Du2047u2048u2049u3002uFE52uFE57uFF01uFF0EuFF1FuFF61 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightTerminalChars();

    /**
     * Get the value for the key 'query.highlight.fragment.size'. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightFragmentSize();

    /**
     * Get the value for the key 'query.highlight.fragment.size' as {@link Integer}. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryHighlightFragmentSizeAsInteger();

    /**
     * Get the value for the key 'query.highlight.number.of.fragments'. <br>
     * The value is, e.g. 2 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightNumberOfFragments();

    /**
     * Get the value for the key 'query.highlight.number.of.fragments' as {@link Integer}. <br>
     * The value is, e.g. 2 <br>
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
     * Get the value for the key 'query.highlight.tag.pre'. <br>
     * The value is, e.g. &lt;strong&gt; <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightTagPre();

    /**
     * Get the value for the key 'query.highlight.tag.post'. <br>
     * The value is, e.g. &lt;/strong&gt; <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightTagPost();

    /**
     * Get the value for the key 'query.highlight.boundary.chars'. <br>
     * The value is, e.g. u0009u000Au0013u0020 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightBoundaryChars();

    /**
     * Get the value for the key 'query.highlight.boundary.max.scan'. <br>
     * The value is, e.g. 20 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightBoundaryMaxScan();

    /**
     * Get the value for the key 'query.highlight.boundary.max.scan' as {@link Integer}. <br>
     * The value is, e.g. 20 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryHighlightBoundaryMaxScanAsInteger();

    /**
     * Get the value for the key 'query.highlight.boundary.scanner'. <br>
     * The value is, e.g. chars <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightBoundaryScanner();

    /**
     * Get the value for the key 'query.highlight.encoder'. <br>
     * The value is, e.g. default <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightEncoder();

    /**
     * Get the value for the key 'query.highlight.force.source'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightForceSource();

    /**
     * Is the property for the key 'query.highlight.force.source' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isQueryHighlightForceSource();

    /**
     * Get the value for the key 'query.highlight.fragmenter'. <br>
     * The value is, e.g. span <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightFragmenter();

    /**
     * Get the value for the key 'query.highlight.fragment.offset'. <br>
     * The value is, e.g. -1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightFragmentOffset();

    /**
     * Get the value for the key 'query.highlight.fragment.offset' as {@link Integer}. <br>
     * The value is, e.g. -1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryHighlightFragmentOffsetAsInteger();

    /**
     * Get the value for the key 'query.highlight.no.match.size'. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightNoMatchSize();

    /**
     * Get the value for the key 'query.highlight.no.match.size' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryHighlightNoMatchSizeAsInteger();

    /**
     * Get the value for the key 'query.highlight.order'. <br>
     * The value is, e.g. score <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightOrder();

    /**
     * Get the value for the key 'query.highlight.phrase.limit'. <br>
     * The value is, e.g. 256 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightPhraseLimit();

    /**
     * Get the value for the key 'query.highlight.phrase.limit' as {@link Integer}. <br>
     * The value is, e.g. 256 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryHighlightPhraseLimitAsInteger();

    /**
     * Get the value for the key 'query.highlight.content.description.fields'. <br>
     * The value is, e.g. hl_content,digest <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightContentDescriptionFields();

    /**
     * Get the value for the key 'query.highlight.boundary.position.detect'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightBoundaryPositionDetect();

    /**
     * Is the property for the key 'query.highlight.boundary.position.detect' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isQueryHighlightBoundaryPositionDetect();

    /**
     * Get the value for the key 'query.highlight.text.fragment.type'. <br>
     * The value is, e.g. query <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightTextFragmentType();

    /**
     * Get the value for the key 'query.highlight.text.fragment.size'. <br>
     * The value is, e.g. 3 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightTextFragmentSize();

    /**
     * Get the value for the key 'query.highlight.text.fragment.size' as {@link Integer}. <br>
     * The value is, e.g. 3 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryHighlightTextFragmentSizeAsInteger();

    /**
     * Get the value for the key 'query.highlight.text.fragment.prefix.length'. <br>
     * The value is, e.g. 5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightTextFragmentPrefixLength();

    /**
     * Get the value for the key 'query.highlight.text.fragment.prefix.length' as {@link Integer}. <br>
     * The value is, e.g. 5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryHighlightTextFragmentPrefixLengthAsInteger();

    /**
     * Get the value for the key 'query.highlight.text.fragment.suffix.length'. <br>
     * The value is, e.g. 5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryHighlightTextFragmentSuffixLength();

    /**
     * Get the value for the key 'query.highlight.text.fragment.suffix.length' as {@link Integer}. <br>
     * The value is, e.g. 5 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryHighlightTextFragmentSuffixLengthAsInteger();

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
     * Get the value for the key 'query.additional.default.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryAdditionalDefaultFields();

    /**
     * Get the value for the key 'query.additional.default.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryAdditionalDefaultFieldsAsInteger();

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
     * Get the value for the key 'query.additional.scroll.response.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryAdditionalScrollResponseFields();

    /**
     * Get the value for the key 'query.additional.scroll.response.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryAdditionalScrollResponseFieldsAsInteger();

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
     * Get the value for the key 'query.additional.analyzed.fields'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryAdditionalAnalyzedFields();

    /**
     * Get the value for the key 'query.additional.analyzed.fields' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryAdditionalAnalyzedFieldsAsInteger();

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
     * Get the value for the key 'query.gsa.response.fields'. <br>
     * The value is, e.g. UE,U,T,RK,S,LANG <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryGsaResponseFields();

    /**
     * Get the value for the key 'query.gsa.default.lang'. <br>
     * The value is, e.g. en <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryGsaDefaultLang();

    /**
     * Get the value for the key 'query.gsa.default.sort'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryGsaDefaultSort();

    /**
     * Get the value for the key 'query.gsa.default.sort' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryGsaDefaultSortAsInteger();

    /**
     * Get the value for the key 'query.gsa.meta.prefix'. <br>
     * The value is, e.g. MT_ <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryGsaMetaPrefix();

    /**
     * Get the value for the key 'query.gsa.index.field.charset'. <br>
     * The value is, e.g. charset <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryGsaIndexFieldCharset();

    /**
     * Get the value for the key 'query.gsa.index.field.content_type.'. <br>
     * The value is, e.g. content_type <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryGsaIndexFieldContentType();

    /**
     * Get the value for the key 'query.collapse.max.concurrent.group.results'. <br>
     * The value is, e.g. 4 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryCollapseMaxConcurrentGroupResults();

    /**
     * Get the value for the key 'query.collapse.max.concurrent.group.results' as {@link Integer}. <br>
     * The value is, e.g. 4 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryCollapseMaxConcurrentGroupResultsAsInteger();

    /**
     * Get the value for the key 'query.collapse.inner.hits.name'. <br>
     * The value is, e.g. similar_docs <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryCollapseInnerHitsName();

    /**
     * Get the value for the key 'query.collapse.inner.hits.size'. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryCollapseInnerHitsSize();

    /**
     * Get the value for the key 'query.collapse.inner.hits.size' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryCollapseInnerHitsSizeAsInteger();

    /**
     * Get the value for the key 'query.collapse.inner.hits.sorts'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryCollapseInnerHitsSorts();

    /**
     * Get the value for the key 'query.collapse.inner.hits.sorts' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryCollapseInnerHitsSortsAsInteger();

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
     * Get the value for the key 'query.json.default.preference'. <br>
     * The value is, e.g. _query <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryJsonDefaultPreference();

    /**
     * Get the value for the key 'query.gsa.default.preference'. <br>
     * The value is, e.g. _query <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryGsaDefaultPreference();

    /**
     * Get the value for the key 'query.language.mapping'. <br>
     * The value is, e.g. ar=ar<br>
     * bg=bg<br>
     * bn=bn<br>
     * ca=ca<br>
     * ckb-iq=ckb-iq<br>
     * ckb_IQ=ckb-iq<br>
     * cs=cs<br>
     * da=da<br>
     * de=de<br>
     * el=el<br>
     * en=en<br>
     * en-ie=en-ie<br>
     * en_IE=en-ie<br>
     * es=es<br>
     * et=et<br>
     * eu=eu<br>
     * fa=fa<br>
     * fi=fi<br>
     * fr=fr<br>
     * gl=gl<br>
     * gu=gu<br>
     * he=he<br>
     * hi=hi<br>
     * hr=hr<br>
     * hu=hu<br>
     * hy=hy<br>
     * id=id<br>
     * it=it<br>
     * ja=ja<br>
     * ko=ko<br>
     * lt=lt<br>
     * lv=lv<br>
     * mk=mk<br>
     * ml=ml<br>
     * nl=nl<br>
     * no=no<br>
     * pa=pa<br>
     * pl=pl<br>
     * pt=pt<br>
     * pt-br=pt-br<br>
     * pt_BR=pt-br<br>
     * ro=ro<br>
     * ru=ru<br>
     * si=si<br>
     * sq=sq<br>
     * sv=sv<br>
     * ta=ta<br>
     * te=te<br>
     * th=th<br>
     * tl=tl<br>
     * tr=tr<br>
     * uk=uk<br>
     * ur=ur<br>
     * vi=vi<br>
     * zh-cn=zh-cn<br>
     * zh_CN=zh-cn<br>
     * zh-tw=zh-tw<br>
     * zh_TW=zh-tw<br>
     * zh=zh<br>
     *  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryLanguageMapping();

    /**
     * Get the value for the key 'query.boost.title'. <br>
     * The value is, e.g. 0.5 <br>
     * comment: boost
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostTitle();

    /**
     * Get the value for the key 'query.boost.title' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 0.5 <br>
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
     * The value is, e.g. 0.05 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostContent();

    /**
     * Get the value for the key 'query.boost.content' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 0.05 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostContentAsDecimal();

    /**
     * Get the value for the key 'query.boost.content.lang'. <br>
     * The value is, e.g. 0.1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostContentLang();

    /**
     * Get the value for the key 'query.boost.content.lang' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 0.1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostContentLangAsDecimal();

    /**
     * Get the value for the key 'query.boost.important_content'. <br>
     * The value is, e.g. -1.0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostImportantContent();

    /**
     * Get the value for the key 'query.boost.important_content' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. -1.0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostImportantContentAsDecimal();

    /**
     * Get the value for the key 'query.boost.important_content.lang'. <br>
     * The value is, e.g. -1.0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostImportantContentLang();

    /**
     * Get the value for the key 'query.boost.important_content.lang' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. -1.0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostImportantContentLangAsDecimal();

    /**
     * Get the value for the key 'query.boost.fuzzy.min.length'. <br>
     * The value is, e.g. 4 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostFuzzyMinLength();

    /**
     * Get the value for the key 'query.boost.fuzzy.min.length' as {@link Integer}. <br>
     * The value is, e.g. 4 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryBoostFuzzyMinLengthAsInteger();

    /**
     * Get the value for the key 'query.boost.fuzzy.title'. <br>
     * The value is, e.g. 0.01 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostFuzzyTitle();

    /**
     * Get the value for the key 'query.boost.fuzzy.title' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 0.01 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostFuzzyTitleAsDecimal();

    /**
     * Get the value for the key 'query.boost.fuzzy.title.fuzziness'. <br>
     * The value is, e.g. AUTO <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostFuzzyTitleFuzziness();

    /**
     * Get the value for the key 'query.boost.fuzzy.title.expansions'. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostFuzzyTitleExpansions();

    /**
     * Get the value for the key 'query.boost.fuzzy.title.expansions' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryBoostFuzzyTitleExpansionsAsInteger();

    /**
     * Get the value for the key 'query.boost.fuzzy.title.prefix_length'. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostFuzzyTitlePrefixLength();

    /**
     * Get the value for the key 'query.boost.fuzzy.title.prefix_length' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryBoostFuzzyTitlePrefixLengthAsInteger();

    /**
     * Get the value for the key 'query.boost.fuzzy.title.transpositions'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostFuzzyTitleTranspositions();

    /**
     * Is the property for the key 'query.boost.fuzzy.title.transpositions' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isQueryBoostFuzzyTitleTranspositions();

    /**
     * Get the value for the key 'query.boost.fuzzy.content'. <br>
     * The value is, e.g. 0.005 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostFuzzyContent();

    /**
     * Get the value for the key 'query.boost.fuzzy.content' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 0.005 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getQueryBoostFuzzyContentAsDecimal();

    /**
     * Get the value for the key 'query.boost.fuzzy.content.fuzziness'. <br>
     * The value is, e.g. AUTO <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostFuzzyContentFuzziness();

    /**
     * Get the value for the key 'query.boost.fuzzy.content.expansions'. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostFuzzyContentExpansions();

    /**
     * Get the value for the key 'query.boost.fuzzy.content.expansions' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryBoostFuzzyContentExpansionsAsInteger();

    /**
     * Get the value for the key 'query.boost.fuzzy.content.prefix_length'. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostFuzzyContentPrefixLength();

    /**
     * Get the value for the key 'query.boost.fuzzy.content.prefix_length' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryBoostFuzzyContentPrefixLengthAsInteger();

    /**
     * Get the value for the key 'query.boost.fuzzy.content.transpositions'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryBoostFuzzyContentTranspositions();

    /**
     * Is the property for the key 'query.boost.fuzzy.content.transpositions' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isQueryBoostFuzzyContentTranspositions();

    /**
     * Get the value for the key 'query.prefix.expansions'. <br>
     * The value is, e.g. 50 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryPrefixExpansions();

    /**
     * Get the value for the key 'query.prefix.expansions' as {@link Integer}. <br>
     * The value is, e.g. 50 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryPrefixExpansionsAsInteger();

    /**
     * Get the value for the key 'query.prefix.slop'. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryPrefixSlop();

    /**
     * Get the value for the key 'query.prefix.slop' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryPrefixSlopAsInteger();

    /**
     * Get the value for the key 'query.fuzzy.prefix_length'. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryFuzzyPrefixLength();

    /**
     * Get the value for the key 'query.fuzzy.prefix_length' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryFuzzyPrefixLengthAsInteger();

    /**
     * Get the value for the key 'query.fuzzy.expansions'. <br>
     * The value is, e.g. 50 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryFuzzyExpansions();

    /**
     * Get the value for the key 'query.fuzzy.expansions' as {@link Integer}. <br>
     * The value is, e.g. 50 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryFuzzyExpansionsAsInteger();

    /**
     * Get the value for the key 'query.fuzzy.transpositions'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryFuzzyTranspositions();

    /**
     * Is the property for the key 'query.fuzzy.transpositions' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isQueryFuzzyTranspositions();

    /**
     * Get the value for the key 'query.facet.fields'. <br>
     * The value is, e.g. label <br>
     * comment: facet
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryFacetFields();

    /**
     * Get the value for the key 'query.facet.fields.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryFacetFieldsSize();

    /**
     * Get the value for the key 'query.facet.fields.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryFacetFieldsSizeAsInteger();

    /**
     * Get the value for the key 'query.facet.fields.min_doc_count'. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryFacetFieldsMinDocCount();

    /**
     * Get the value for the key 'query.facet.fields.min_doc_count' as {@link Integer}. <br>
     * The value is, e.g. 1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryFacetFieldsMinDocCountAsInteger();

    /**
     * Get the value for the key 'query.facet.fields.sort'. <br>
     * The value is, e.g. count.desc <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryFacetFieldsSort();

    /**
     * Get the value for the key 'query.facet.fields.missing'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryFacetFieldsMissing();

    /**
     * Get the value for the key 'query.facet.fields.missing' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getQueryFacetFieldsMissingAsInteger();

    /**
     * Get the value for the key 'query.facet.queries'. <br>
     * The value is, e.g. labels.facet_timestamp_title:labels.facet_timestamp_1day=timestamp:[now/d-1d TO *]	labels.facet_timestamp_1week=timestamp:[now/d-7d TO *]	labels.facet_timestamp_1month=timestamp:[now/d-1M TO *]	labels.facet_timestamp_1year=timestamp:[now/d-1y TO *]<br>
     * labels.facet_contentLength_title:labels.facet_contentLength_10k=content_length:[0 TO 9999]	labels.facet_contentLength_10kto100k=content_length:[10000 TO 99999]	labels.facet_contentLength_100kto500k=content_length:[100000 TO 499999]	labels.facet_contentLength_500kto1m=content_length:[500000 TO 999999]	labels.facet_contentLength_1m=content_length:[1000000 TO *]<br>
     * labels.facet_filetype_title:labels.facet_filetype_html=filetype:html	labels.facet_filetype_word=filetype:word	labels.facet_filetype_excel=filetype:excel	labels.facet_filetype_powerpoint=filetype:powerpoint	labels.facet_filetype_odt=filetype:odt	labels.facet_filetype_ods=filetype:ods	labels.facet_filetype_odp=filetype:odp	labels.facet_filetype_pdf=filetype:pdf	labels.facet_filetype_txt=filetype:txt	labels.facet_filetype_others=filetype:others<br>
     *  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getQueryFacetQueries();

    /**
     * Get the value for the key 'rank.fusion.window_size'. <br>
     * The value is, e.g. 200 <br>
     * comment: ranking
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getRankFusionWindowSize();

    /**
     * Get the value for the key 'rank.fusion.window_size' as {@link Integer}. <br>
     * The value is, e.g. 200 <br>
     * comment: ranking
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getRankFusionWindowSizeAsInteger();

    /**
     * Get the value for the key 'rank.fusion.rank_constant'. <br>
     * The value is, e.g. 20 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getRankFusionRankConstant();

    /**
     * Get the value for the key 'rank.fusion.rank_constant' as {@link Integer}. <br>
     * The value is, e.g. 20 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getRankFusionRankConstantAsInteger();

    /**
     * Get the value for the key 'rank.fusion.threads'. <br>
     * The value is, e.g. -1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getRankFusionThreads();

    /**
     * Get the value for the key 'rank.fusion.threads' as {@link Integer}. <br>
     * The value is, e.g. -1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getRankFusionThreadsAsInteger();

    /**
     * Get the value for the key 'rank.fusion.score_field'. <br>
     * The value is, e.g. rf_score <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getRankFusionScoreField();

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
     * The value is, e.g. 1,2,4:2,5:1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSmbAvailableSidTypes();

    /**
     * Get the value for the key 'file.role.from.file'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getFileRoleFromFile();

    /**
     * Is the property for the key 'file.role.from.file' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isFileRoleFromFile();

    /**
     * Get the value for the key 'ftp.role.from.file'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getFtpRoleFromFile();

    /**
     * Is the property for the key 'ftp.role.from.file' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isFtpRoleFromFile();

    /**
     * Get the value for the key 'index.backup.targets'. <br>
     * The value is, e.g. fess_basic_config.bulk,fess_config.bulk,fess_user.bulk,system.properties,fess.json,doc.json <br>
     * comment: backup
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexBackupTargets();

    /**
     * Get the value for the key 'index.backup.log.targets'. <br>
     * The value is, e.g. click_log.ndjson,favorite_log.ndjson,search_log.ndjson,user_info.ndjson <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getIndexBackupLogTargets();

    /**
     * Get the value for the key 'logging.search.docs.enabled'. <br>
     * The value is, e.g. true <br>
     * comment: logging
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLoggingSearchDocsEnabled();

    /**
     * Is the property for the key 'logging.search.docs.enabled' true? <br>
     * The value is, e.g. true <br>
     * comment: logging
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isLoggingSearchDocsEnabled();

    /**
     * Get the value for the key 'logging.search.docs.fields'. <br>
     * The value is, e.g. filetype,created,click_count,title,doc_id,url,score,site,filename,host,digest,boost,mimetype,favorite_count,_id,lang,last_modified,content_length,timestamp <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLoggingSearchDocsFields();

    /**
     * Get the value for the key 'logging.search.use.logfile'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLoggingSearchUseLogfile();

    /**
     * Is the property for the key 'logging.search.use.logfile' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isLoggingSearchUseLogfile();

    /**
     * Get the value for the key 'logging.app.packages'. <br>
     * The value is, e.g. org.codelibs,org.dbflute,org.lastaflute <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLoggingAppPackages();

    /**
     * Get the value for the key 'form.admin.max.input.size'. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getFormAdminMaxInputSize();

    /**
     * Get the value for the key 'form.admin.max.input.size' as {@link Integer}. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getFormAdminMaxInputSizeAsInteger();

    /**
     * Get the value for the key 'form.admin.label.in.config.enabled'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getFormAdminLabelInConfigEnabled();

    /**
     * Is the property for the key 'form.admin.label.in.config.enabled' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isFormAdminLabelInConfigEnabled();

    /**
     * Get the value for the key 'form.admin.default.template.name'. <br>
     * The value is, e.g. __TEMPLATE__ <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getFormAdminDefaultTemplateName();

    /**
     * Get the value for the key 'osdd.link.enabled'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOsddLinkEnabled();

    /**
     * Is the property for the key 'osdd.link.enabled' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isOsddLinkEnabled();

    /**
     * Get the value for the key 'clipboard.copy.icon.enabled'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getClipboardCopyIconEnabled();

    /**
     * Is the property for the key 'clipboard.copy.icon.enabled' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isClipboardCopyIconEnabled();

    /**
     * Get the value for the key 'authentication.admin.users'. <br>
     * The value is, e.g. admin <br>
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
     * Get the value for the key 'role.search.denied.prefix'. <br>
     * The value is, e.g. D <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getRoleSearchDeniedPrefix();

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
     * Get the value for the key 'session.tracking.modes'. <br>
     * The value is, e.g. cookie <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSessionTrackingModes();

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
     * Get the value for the key 'page.relatedcontent.max.fetch.size'. <br>
     * The value is, e.g. 5000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageRelatedcontentMaxFetchSize();

    /**
     * Get the value for the key 'page.relatedcontent.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 5000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageRelatedcontentMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.relatedquery.max.fetch.size'. <br>
     * The value is, e.g. 5000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageRelatedqueryMaxFetchSize();

    /**
     * Get the value for the key 'page.relatedquery.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 5000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageRelatedqueryMaxFetchSizeAsInteger();

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
     * Get the value for the key 'page.thumbnail.purge.max.fetch.size'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageThumbnailPurgeMaxFetchSize();

    /**
     * Get the value for the key 'page.thumbnail.purge.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageThumbnailPurgeMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.score.booster.max.fetch.size'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageScoreBoosterMaxFetchSize();

    /**
     * Get the value for the key 'page.score.booster.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageScoreBoosterMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.searchlog.max.fetch.size'. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageSearchlogMaxFetchSize();

    /**
     * Get the value for the key 'page.searchlog.max.fetch.size' as {@link Integer}. <br>
     * The value is, e.g. 10000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPageSearchlogMaxFetchSizeAsInteger();

    /**
     * Get the value for the key 'page.searchlist.track.total.hits'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPageSearchlistTrackTotalHits();

    /**
     * Is the property for the key 'page.searchlist.track.total.hits' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isPageSearchlistTrackTotalHits();

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
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPagingSearchPageSize();

    /**
     * Get the value for the key 'paging.search.page.size' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
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
     * Get the value for the key 'searchlog.agg.shard.size'. <br>
     * The value is, e.g. -1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSearchlogAggShardSize();

    /**
     * Get the value for the key 'searchlog.agg.shard.size' as {@link Integer}. <br>
     * The value is, e.g. -1 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSearchlogAggShardSizeAsInteger();

    /**
     * Get the value for the key 'searchlog.request.headers'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSearchlogRequestHeaders();

    /**
     * Get the value for the key 'searchlog.request.headers' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSearchlogRequestHeadersAsInteger();

    /**
     * Get the value for the key 'thumbnail.html.image.min.width'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlImageMinWidth();

    /**
     * Get the value for the key 'thumbnail.html.image.min.width' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getThumbnailHtmlImageMinWidthAsInteger();

    /**
     * Get the value for the key 'thumbnail.html.image.min.height'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlImageMinHeight();

    /**
     * Get the value for the key 'thumbnail.html.image.min.height' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getThumbnailHtmlImageMinHeightAsInteger();

    /**
     * Get the value for the key 'thumbnail.html.image.max.aspect.ratio'. <br>
     * The value is, e.g. 3.0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlImageMaxAspectRatio();

    /**
     * Get the value for the key 'thumbnail.html.image.max.aspect.ratio' as {@link java.math.BigDecimal}. <br>
     * The value is, e.g. 3.0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not decimal.
     */
    java.math.BigDecimal getThumbnailHtmlImageMaxAspectRatioAsDecimal();

    /**
     * Get the value for the key 'thumbnail.html.image.thumbnail.width'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlImageThumbnailWidth();

    /**
     * Get the value for the key 'thumbnail.html.image.thumbnail.width' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getThumbnailHtmlImageThumbnailWidthAsInteger();

    /**
     * Get the value for the key 'thumbnail.html.image.thumbnail.height'. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlImageThumbnailHeight();

    /**
     * Get the value for the key 'thumbnail.html.image.thumbnail.height' as {@link Integer}. <br>
     * The value is, e.g. 100 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getThumbnailHtmlImageThumbnailHeightAsInteger();

    /**
     * Get the value for the key 'thumbnail.html.image.format'. <br>
     * The value is, e.g. png <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlImageFormat();

    /**
     * Get the value for the key 'thumbnail.html.image.xpath'. <br>
     * The value is, e.g. //IMG <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlImageXpath();

    /**
     * Get the value for the key 'thumbnail.html.image.exclude.extensions'. <br>
     * The value is, e.g. svg,html,css,js <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailHtmlImageExcludeExtensions();

    /**
     * Get the value for the key 'thumbnail.generator.interval'. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailGeneratorInterval();

    /**
     * Get the value for the key 'thumbnail.generator.interval' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getThumbnailGeneratorIntervalAsInteger();

    /**
     * Get the value for the key 'thumbnail.generator.targets'. <br>
     * The value is, e.g. all <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailGeneratorTargets();

    /**
     * Get the value for the key 'thumbnail.crawler.enabled'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailCrawlerEnabled();

    /**
     * Is the property for the key 'thumbnail.crawler.enabled' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isThumbnailCrawlerEnabled();

    /**
     * Get the value for the key 'thumbnail.system.monitor.interval'. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getThumbnailSystemMonitorInterval();

    /**
     * Get the value for the key 'thumbnail.system.monitor.interval' as {@link Integer}. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getThumbnailSystemMonitorIntervalAsInteger();

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
     * Get the value for the key 'mail.hostname'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getMailHostname();

    /**
     * Get the value for the key 'mail.hostname' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getMailHostnameAsInteger();

    /**
     * Get the value for the key 'scheduler.target.name'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSchedulerTargetName();

    /**
     * Get the value for the key 'scheduler.target.name' as {@link Integer}. <br>
     * The value is, e.g.  <br>
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
     * The value is, e.g. https://fess.codelibs.org/{lang}/{version}/admin/ <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpBaseLink();

    /**
     * Get the value for the key 'online.help.installation'. <br>
     * The value is, e.g. https://fess.codelibs.org/{lang}/{version}/install/install.html <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpInstallation();

    /**
     * Get the value for the key 'online.help.eol'. <br>
     * The value is, e.g. https://fess.codelibs.org/{lang}/eol.html <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpEol();

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
     * Get the value for the key 'online.help.name.dict.protwords'. <br>
     * The value is, e.g. protwords <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDictProtwords();

    /**
     * Get the value for the key 'online.help.name.dict.stopwords'. <br>
     * The value is, e.g. stopwords <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDictStopwords();

    /**
     * Get the value for the key 'online.help.name.dict.stemmeroverride'. <br>
     * The value is, e.g. stemmeroverride <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameDictStemmeroverride();

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
     * Get the value for the key 'online.help.name.relatedquery'. <br>
     * The value is, e.g. relatedquery <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameRelatedquery();

    /**
     * Get the value for the key 'online.help.name.relatedcontent'. <br>
     * The value is, e.g. relatedcontent <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameRelatedcontent();

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
     * Get the value for the key 'online.help.name.searchlog'. <br>
     * The value is, e.g. searchlog <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameSearchlog();

    /**
     * Get the value for the key 'online.help.name.maintenance'. <br>
     * The value is, e.g. maintenance <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameMaintenance();

    /**
     * Get the value for the key 'online.help.name.plugin'. <br>
     * The value is, e.g. plugin <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNamePlugin();

    /**
     * Get the value for the key 'online.help.name.storage'. <br>
     * The value is, e.g. storage <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpNameStorage();

    /**
     * Get the value for the key 'online.help.supported.langs'. <br>
     * The value is, e.g. ja <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getOnlineHelpSupportedLangs();

    /**
     * Get the value for the key 'forum.link'. <br>
     * The value is, e.g. https://discuss.codelibs.org/c/Fess{lang}/ <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getForumLink();

    /**
     * Get the value for the key 'forum.supported.langs'. <br>
     * The value is, e.g. en,ja <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getForumSupportedLangs();

    /**
     * Get the value for the key 'suggest.popular.word.seed'. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestPopularWordSeed();

    /**
     * Get the value for the key 'suggest.popular.word.seed' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
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
     * Get the value for the key 'suggest.popular.word.query.freq'. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestPopularWordQueryFreq();

    /**
     * Get the value for the key 'suggest.popular.word.query.freq' as {@link Integer}. <br>
     * The value is, e.g. 10 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestPopularWordQueryFreqAsInteger();

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
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestUpdateRequestInterval();

    /**
     * Get the value for the key 'suggest.update.request.interval' as {@link Integer}. <br>
     * The value is, e.g. 0 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestUpdateRequestIntervalAsInteger();

    /**
     * Get the value for the key 'suggest.update.doc.per.request'. <br>
     * The value is, e.g. 2 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestUpdateDocPerRequest();

    /**
     * Get the value for the key 'suggest.update.doc.per.request' as {@link Integer}. <br>
     * The value is, e.g. 2 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestUpdateDocPerRequestAsInteger();

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
     * Get the value for the key 'suggest.update.contents.limit.doc.size'. <br>
     * The value is, e.g. 50000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestUpdateContentsLimitDocSize();

    /**
     * Get the value for the key 'suggest.update.contents.limit.doc.size' as {@link Integer}. <br>
     * The value is, e.g. 50000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestUpdateContentsLimitDocSizeAsInteger();

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
     * Get the value for the key 'suggest.system.monitor.interval'. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getSuggestSystemMonitorInterval();

    /**
     * Get the value for the key 'suggest.system.monitor.interval' as {@link Integer}. <br>
     * The value is, e.g. 60 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getSuggestSystemMonitorIntervalAsInteger();

    /**
     * Get the value for the key 'ldap.admin.enabled'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAdminEnabled();

    /**
     * Is the property for the key 'ldap.admin.enabled' true? <br>
     * The value is, e.g. false <br>
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
     * Get the value for the key 'ldap.auth.validation'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAuthValidation();

    /**
     * Is the property for the key 'ldap.auth.validation' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isLdapAuthValidation();

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
     * Get the value for the key 'ldap.ignore.netbios.name'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapIgnoreNetbiosName();

    /**
     * Is the property for the key 'ldap.ignore.netbios.name' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isLdapIgnoreNetbiosName();

    /**
     * Get the value for the key 'ldap.group.name.with.underscores'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapGroupNameWithUnderscores();

    /**
     * Is the property for the key 'ldap.group.name.with.underscores' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isLdapGroupNameWithUnderscores();

    /**
     * Get the value for the key 'ldap.lowercase.permission.name'. <br>
     * The value is, e.g. false <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapLowercasePermissionName();

    /**
     * Is the property for the key 'ldap.lowercase.permission.name' true? <br>
     * The value is, e.g. false <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isLdapLowercasePermissionName();

    /**
     * Get the value for the key 'ldap.allow.empty.permission'. <br>
     * The value is, e.g. true <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLdapAllowEmptyPermission();

    /**
     * Is the property for the key 'ldap.allow.empty.permission' true? <br>
     * The value is, e.g. true <br>
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isLdapAllowEmptyPermission();

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
    String getLdapAttrLabeledURI();

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
    String getLdapAttrInternationaliSDNNumber();

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
     * Get the value for the key 'plugin.repositories'. <br>
     * The value is, e.g. https://repo.maven.apache.org/maven2/org/codelibs/fess/,https://fess.codelibs.org/plugin/artifacts.yaml <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPluginRepositories();

    /**
     * Get the value for the key 'plugin.version.filter'. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPluginVersionFilter();

    /**
     * Get the value for the key 'plugin.version.filter' as {@link Integer}. <br>
     * The value is, e.g.  <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getPluginVersionFilterAsInteger();

    /**
     * Get the value for the key 'storage.max.items.in.page'. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getStorageMaxItemsInPage();

    /**
     * Get the value for the key 'storage.max.items.in.page' as {@link Integer}. <br>
     * The value is, e.g. 1000 <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not integer.
     */
    Integer getStorageMaxItemsInPageAsInteger();

    /**
     * Get the value for the key 'password.invalid.admin.passwords'. <br>
     * The value is, e.g. admin <br>
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getPasswordInvalidAdminPasswords();

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

        public String getSearchEngineType() {
            return get(FessConfig.search_engine_TYPE);
        }

        public String getSearchEngineHttpUrl() {
            return get(FessConfig.search_engine_HTTP_URL);
        }

        public String getSearchEngineHttpSslCertificateAuthorities() {
            return get(FessConfig.search_engine_HTTP_SSL_certificate_authorities);
        }

        public Integer getSearchEngineHttpSslCertificateAuthoritiesAsInteger() {
            return getAsInteger(FessConfig.search_engine_HTTP_SSL_certificate_authorities);
        }

        public String getSearchEngineUsername() {
            return get(FessConfig.search_engine_USERNAME);
        }

        public Integer getSearchEngineUsernameAsInteger() {
            return getAsInteger(FessConfig.search_engine_USERNAME);
        }

        public String getSearchEnginePassword() {
            return get(FessConfig.search_engine_PASSWORD);
        }

        public Integer getSearchEnginePasswordAsInteger() {
            return getAsInteger(FessConfig.search_engine_PASSWORD);
        }

        public String getSearchEngineHeartbeatInterval() {
            return get(FessConfig.search_engine_heartbeat_interval);
        }

        public Integer getSearchEngineHeartbeatIntervalAsInteger() {
            return getAsInteger(FessConfig.search_engine_heartbeat_interval);
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

        public String getAppEncryptPropertyPattern() {
            return get(FessConfig.APP_ENCRYPT_PROPERTY_PATTERN);
        }

        public String getAppExtensionNames() {
            return get(FessConfig.APP_EXTENSION_NAMES);
        }

        public Integer getAppExtensionNamesAsInteger() {
            return getAsInteger(FessConfig.APP_EXTENSION_NAMES);
        }

        public String getAppAuditLogFormat() {
            return get(FessConfig.APP_AUDIT_LOG_FORMAT);
        }

        public Integer getAppAuditLogFormatAsInteger() {
            return getAsInteger(FessConfig.APP_AUDIT_LOG_FORMAT);
        }

        public String getJvmCrawlerOptions() {
            return get(FessConfig.JVM_CRAWLER_OPTIONS);
        }

        public String getJvmSuggestOptions() {
            return get(FessConfig.JVM_SUGGEST_OPTIONS);
        }

        public String getJvmThumbnailOptions() {
            return get(FessConfig.JVM_THUMBNAIL_OPTIONS);
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

        public String getJobMaxCrawlerProcesses() {
            return get(FessConfig.JOB_MAX_CRAWLER_PROCESSES);
        }

        public Integer getJobMaxCrawlerProcessesAsInteger() {
            return getAsInteger(FessConfig.JOB_MAX_CRAWLER_PROCESSES);
        }

        public String getJobDefaultScript() {
            return get(FessConfig.JOB_DEFAULT_SCRIPT);
        }

        public String getProcessors() {
            return get(FessConfig.PROCESSORS);
        }

        public Integer getProcessorsAsInteger() {
            return getAsInteger(FessConfig.PROCESSORS);
        }

        public String getJavaCommandPath() {
            return get(FessConfig.JAVA_COMMAND_PATH);
        }

        public String getPythonCommandPath() {
            return get(FessConfig.PYTHON_COMMAND_PATH);
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

        public String getAdaptiveLoadControl() {
            return get(FessConfig.ADAPTIVE_LOAD_CONTROL);
        }

        public Integer getAdaptiveLoadControlAsInteger() {
            return getAsInteger(FessConfig.ADAPTIVE_LOAD_CONTROL);
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

        public String getApiAccessTokenRequestParameter() {
            return get(FessConfig.API_ACCESS_TOKEN_REQUEST_PARAMETER);
        }

        public Integer getApiAccessTokenRequestParameterAsInteger() {
            return getAsInteger(FessConfig.API_ACCESS_TOKEN_REQUEST_PARAMETER);
        }

        public String getApiAdminAccessPermissions() {
            return get(FessConfig.API_ADMIN_ACCESS_PERMISSIONS);
        }

        public String getApiSearchAcceptReferers() {
            return get(FessConfig.API_SEARCH_ACCEPT_REFERERS);
        }

        public Integer getApiSearchAcceptReferersAsInteger() {
            return getAsInteger(FessConfig.API_SEARCH_ACCEPT_REFERERS);
        }

        public String getApiSearchScroll() {
            return get(FessConfig.API_SEARCH_SCROLL);
        }

        public boolean isApiSearchScroll() {
            return is(FessConfig.API_SEARCH_SCROLL);
        }

        public String getApiJsonResponseHeaders() {
            return get(FessConfig.API_JSON_RESPONSE_HEADERS);
        }

        public Integer getApiJsonResponseHeadersAsInteger() {
            return getAsInteger(FessConfig.API_JSON_RESPONSE_HEADERS);
        }

        public String getApiJsonResponseExceptionIncluded() {
            return get(FessConfig.API_JSON_RESPONSE_EXCEPTION_INCLUDED);
        }

        public boolean isApiJsonResponseExceptionIncluded() {
            return is(FessConfig.API_JSON_RESPONSE_EXCEPTION_INCLUDED);
        }

        public String getApiGsaResponseHeaders() {
            return get(FessConfig.API_GSA_RESPONSE_HEADERS);
        }

        public Integer getApiGsaResponseHeadersAsInteger() {
            return getAsInteger(FessConfig.API_GSA_RESPONSE_HEADERS);
        }

        public String getApiGsaResponseExceptionIncluded() {
            return get(FessConfig.API_GSA_RESPONSE_EXCEPTION_INCLUDED);
        }

        public boolean isApiGsaResponseExceptionIncluded() {
            return is(FessConfig.API_GSA_RESPONSE_EXCEPTION_INCLUDED);
        }

        public String getApiDashboardResponseHeaders() {
            return get(FessConfig.API_DASHBOARD_RESPONSE_HEADERS);
        }

        public Integer getApiDashboardResponseHeadersAsInteger() {
            return getAsInteger(FessConfig.API_DASHBOARD_RESPONSE_HEADERS);
        }

        public String getApiCorsAllowOrigin() {
            return get(FessConfig.API_CORS_ALLOW_ORIGIN);
        }

        public String getApiCorsAllowMethods() {
            return get(FessConfig.API_CORS_ALLOW_METHODS);
        }

        public String getApiCorsMaxAge() {
            return get(FessConfig.API_CORS_MAX_AGE);
        }

        public Integer getApiCorsMaxAgeAsInteger() {
            return getAsInteger(FessConfig.API_CORS_MAX_AGE);
        }

        public String getApiCorsAllowHeaders() {
            return get(FessConfig.API_CORS_ALLOW_HEADERS);
        }

        public String getApiCorsAllowCredentials() {
            return get(FessConfig.API_CORS_ALLOW_CREDENTIALS);
        }

        public boolean isApiCorsAllowCredentials() {
            return is(FessConfig.API_CORS_ALLOW_CREDENTIALS);
        }

        public String getApiJsonpEnabled() {
            return get(FessConfig.API_JSONP_ENABLED);
        }

        public boolean isApiJsonpEnabled() {
            return is(FessConfig.API_JSONP_ENABLED);
        }

        public String getApiPingSearchEngineFields() {
            return get(FessConfig.API_PING_search_engine_FIELDS);
        }

        public String getVirtualHostHeaders() {
            return get(FessConfig.VIRTUAL_HOST_HEADERS);
        }

        public Integer getVirtualHostHeadersAsInteger() {
            return getAsInteger(FessConfig.VIRTUAL_HOST_HEADERS);
        }

        public String getHttpProxyHost() {
            return get(FessConfig.HTTP_PROXY_HOST);
        }

        public Integer getHttpProxyHostAsInteger() {
            return getAsInteger(FessConfig.HTTP_PROXY_HOST);
        }

        public String getHttpProxyPort() {
            return get(FessConfig.HTTP_PROXY_PORT);
        }

        public Integer getHttpProxyPortAsInteger() {
            return getAsInteger(FessConfig.HTTP_PROXY_PORT);
        }

        public String getHttpProxyUsername() {
            return get(FessConfig.HTTP_PROXY_USERNAME);
        }

        public Integer getHttpProxyUsernameAsInteger() {
            return getAsInteger(FessConfig.HTTP_PROXY_USERNAME);
        }

        public String getHttpProxyPassword() {
            return get(FessConfig.HTTP_PROXY_PASSWORD);
        }

        public Integer getHttpProxyPasswordAsInteger() {
            return getAsInteger(FessConfig.HTTP_PROXY_PASSWORD);
        }

        public String getHttpFileuploadMaxSize() {
            return get(FessConfig.HTTP_FILEUPLOAD_MAX_SIZE);
        }

        public Integer getHttpFileuploadMaxSizeAsInteger() {
            return getAsInteger(FessConfig.HTTP_FILEUPLOAD_MAX_SIZE);
        }

        public String getHttpFileuploadThresholdSize() {
            return get(FessConfig.HTTP_FILEUPLOAD_THRESHOLD_SIZE);
        }

        public Integer getHttpFileuploadThresholdSizeAsInteger() {
            return getAsInteger(FessConfig.HTTP_FILEUPLOAD_THRESHOLD_SIZE);
        }

        public String getCrawlerDefaultScript() {
            return get(FessConfig.CRAWLER_DEFAULT_SCRIPT);
        }

        public String getCrawlerHttpThreadPoolSize() {
            return get(FessConfig.CRAWLER_HTTP_thread_pool_SIZE);
        }

        public Integer getCrawlerHttpThreadPoolSizeAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_HTTP_thread_pool_SIZE);
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

        public String getCrawlerDocumentAppendFilename() {
            return get(FessConfig.CRAWLER_DOCUMENT_APPEND_FILENAME);
        }

        public boolean isCrawlerDocumentAppendFilename() {
            return is(FessConfig.CRAWLER_DOCUMENT_APPEND_FILENAME);
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

        public String getCrawlerDocumentFullstopChars() {
            return get(FessConfig.CRAWLER_DOCUMENT_FULLSTOP_CHARS);
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

        public String getCrawlerDataEnvParamKeyPattern() {
            return get(FessConfig.CRAWLER_DATA_ENV_PARAM_KEY_PATTERN);
        }

        public String getCrawlerIgnoreRobotsTxt() {
            return get(FessConfig.CRAWLER_IGNORE_ROBOTS_TXT);
        }

        public boolean isCrawlerIgnoreRobotsTxt() {
            return is(FessConfig.CRAWLER_IGNORE_ROBOTS_TXT);
        }

        public String getCrawlerIgnoreRobotsTags() {
            return get(FessConfig.CRAWLER_IGNORE_ROBOTS_TAGS);
        }

        public boolean isCrawlerIgnoreRobotsTags() {
            return is(FessConfig.CRAWLER_IGNORE_ROBOTS_TAGS);
        }

        public String getCrawlerIgnoreContentException() {
            return get(FessConfig.CRAWLER_IGNORE_CONTENT_EXCEPTION);
        }

        public boolean isCrawlerIgnoreContentException() {
            return is(FessConfig.CRAWLER_IGNORE_CONTENT_EXCEPTION);
        }

        public String getCrawlerFailureUrlStatusCodes() {
            return get(FessConfig.CRAWLER_FAILURE_URL_STATUS_CODES);
        }

        public Integer getCrawlerFailureUrlStatusCodesAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_FAILURE_URL_STATUS_CODES);
        }

        public String getCrawlerSystemMonitorInterval() {
            return get(FessConfig.CRAWLER_SYSTEM_MONITOR_INTERVAL);
        }

        public Integer getCrawlerSystemMonitorIntervalAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_SYSTEM_MONITOR_INTERVAL);
        }

        public String getCrawlerHotthreadIgnoreIdleThreads() {
            return get(FessConfig.CRAWLER_HOTTHREAD_ignore_idle_threads);
        }

        public boolean isCrawlerHotthreadIgnoreIdleThreads() {
            return is(FessConfig.CRAWLER_HOTTHREAD_ignore_idle_threads);
        }

        public String getCrawlerHotthreadInterval() {
            return get(FessConfig.CRAWLER_HOTTHREAD_INTERVAL);
        }

        public String getCrawlerHotthreadSnapshots() {
            return get(FessConfig.CRAWLER_HOTTHREAD_SNAPSHOTS);
        }

        public Integer getCrawlerHotthreadSnapshotsAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_HOTTHREAD_SNAPSHOTS);
        }

        public String getCrawlerHotthreadThreads() {
            return get(FessConfig.CRAWLER_HOTTHREAD_THREADS);
        }

        public Integer getCrawlerHotthreadThreadsAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_HOTTHREAD_THREADS);
        }

        public String getCrawlerHotthreadTimeout() {
            return get(FessConfig.CRAWLER_HOTTHREAD_TIMEOUT);
        }

        public String getCrawlerHotthreadType() {
            return get(FessConfig.CRAWLER_HOTTHREAD_TYPE);
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

        public String getCrawlerDocumentHtmlCanonicalXpath() {
            return get(FessConfig.CRAWLER_DOCUMENT_HTML_CANONICAL_XPATH);
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

        public String getCrawlerDocumentHtmlDefaultLang() {
            return get(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_LANG);
        }

        public Integer getCrawlerDocumentHtmlDefaultLangAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_LANG);
        }

        public String getCrawlerDocumentHtmlDefaultIncludeIndexPatterns() {
            return get(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_INCLUDE_INDEX_PATTERNS);
        }

        public Integer getCrawlerDocumentHtmlDefaultIncludeIndexPatternsAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_INCLUDE_INDEX_PATTERNS);
        }

        public String getCrawlerDocumentHtmlDefaultExcludeIndexPatterns() {
            return get(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_EXCLUDE_INDEX_PATTERNS);
        }

        public String getCrawlerDocumentHtmlDefaultIncludeSearchPatterns() {
            return get(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_INCLUDE_SEARCH_PATTERNS);
        }

        public Integer getCrawlerDocumentHtmlDefaultIncludeSearchPatternsAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_INCLUDE_SEARCH_PATTERNS);
        }

        public String getCrawlerDocumentHtmlDefaultExcludeSearchPatterns() {
            return get(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_EXCLUDE_SEARCH_PATTERNS);
        }

        public Integer getCrawlerDocumentHtmlDefaultExcludeSearchPatternsAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_EXCLUDE_SEARCH_PATTERNS);
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

        public String getCrawlerDocumentFileDefaultIncludeIndexPatterns() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_INCLUDE_INDEX_PATTERNS);
        }

        public Integer getCrawlerDocumentFileDefaultIncludeIndexPatternsAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_INCLUDE_INDEX_PATTERNS);
        }

        public String getCrawlerDocumentFileDefaultExcludeIndexPatterns() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_EXCLUDE_INDEX_PATTERNS);
        }

        public Integer getCrawlerDocumentFileDefaultExcludeIndexPatternsAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_EXCLUDE_INDEX_PATTERNS);
        }

        public String getCrawlerDocumentFileDefaultIncludeSearchPatterns() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_INCLUDE_SEARCH_PATTERNS);
        }

        public Integer getCrawlerDocumentFileDefaultIncludeSearchPatternsAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_INCLUDE_SEARCH_PATTERNS);
        }

        public String getCrawlerDocumentFileDefaultExcludeSearchPatterns() {
            return get(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_EXCLUDE_SEARCH_PATTERNS);
        }

        public Integer getCrawlerDocumentFileDefaultExcludeSearchPatternsAsInteger() {
            return getAsInteger(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_EXCLUDE_SEARCH_PATTERNS);
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

        public String getIndexerDataMaxDeleteCacheSize() {
            return get(FessConfig.INDEXER_DATA_MAX_DELETE_CACHE_SIZE);
        }

        public Integer getIndexerDataMaxDeleteCacheSizeAsInteger() {
            return getAsInteger(FessConfig.INDEXER_DATA_MAX_DELETE_CACHE_SIZE);
        }

        public String getIndexerDataMaxRedirectCount() {
            return get(FessConfig.INDEXER_DATA_MAX_REDIRECT_COUNT);
        }

        public Integer getIndexerDataMaxRedirectCountAsInteger() {
            return getAsInteger(FessConfig.INDEXER_DATA_MAX_REDIRECT_COUNT);
        }

        public String getIndexerLanguageFields() {
            return get(FessConfig.INDEXER_LANGUAGE_FIELDS);
        }

        public String getIndexerLanguageDetectLength() {
            return get(FessConfig.INDEXER_LANGUAGE_DETECT_LENGTH);
        }

        public Integer getIndexerLanguageDetectLengthAsInteger() {
            return getAsInteger(FessConfig.INDEXER_LANGUAGE_DETECT_LENGTH);
        }

        public String getIndexerMaxResultWindowSize() {
            return get(FessConfig.INDEXER_MAX_RESULT_WINDOW_SIZE);
        }

        public Integer getIndexerMaxResultWindowSizeAsInteger() {
            return getAsInteger(FessConfig.INDEXER_MAX_RESULT_WINDOW_SIZE);
        }

        public String getIndexerMaxSearchDocSize() {
            return get(FessConfig.INDEXER_MAX_SEARCH_DOC_SIZE);
        }

        public Integer getIndexerMaxSearchDocSizeAsInteger() {
            return getAsInteger(FessConfig.INDEXER_MAX_SEARCH_DOC_SIZE);
        }

        public String getIndexCodec() {
            return get(FessConfig.INDEX_CODEC);
        }

        public String getIndexNumberOfShards() {
            return get(FessConfig.INDEX_number_of_shards);
        }

        public Integer getIndexNumberOfShardsAsInteger() {
            return getAsInteger(FessConfig.INDEX_number_of_shards);
        }

        public String getIndexAutoExpandReplicas() {
            return get(FessConfig.INDEX_auto_expand_replicas);
        }

        public String getIndexIdDigestAlgorithm() {
            return get(FessConfig.INDEX_ID_DIGEST_ALGORITHM);
        }

        public String getIndexUserInitialPassword() {
            return get(FessConfig.INDEX_USER_initial_password);
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

        public String getIndexFieldSeqNo() {
            return get(FessConfig.INDEX_FIELD_seq_no);
        }

        public String getIndexFieldPrimaryTerm() {
            return get(FessConfig.INDEX_FIELD_primary_term);
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

        public String getIndexFieldContentMinhashBits() {
            return get(FessConfig.INDEX_FIELD_content_minhash_bits);
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

        public String getIndexFieldThumbnail() {
            return get(FessConfig.INDEX_FIELD_THUMBNAIL);
        }

        public String getIndexFieldVirtualHost() {
            return get(FessConfig.INDEX_FIELD_virtual_host);
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

        public String getResponseMaxTitleLength() {
            return get(FessConfig.RESPONSE_MAX_TITLE_LENGTH);
        }

        public Integer getResponseMaxTitleLengthAsInteger() {
            return getAsInteger(FessConfig.RESPONSE_MAX_TITLE_LENGTH);
        }

        public String getResponseMaxSitePathLength() {
            return get(FessConfig.RESPONSE_MAX_SITE_PATH_LENGTH);
        }

        public Integer getResponseMaxSitePathLengthAsInteger() {
            return getAsInteger(FessConfig.RESPONSE_MAX_SITE_PATH_LENGTH);
        }

        public String getResponseHighlightContentTitleEnabled() {
            return get(FessConfig.RESPONSE_HIGHLIGHT_content_title_ENABLED);
        }

        public boolean isResponseHighlightContentTitleEnabled() {
            return is(FessConfig.RESPONSE_HIGHLIGHT_content_title_ENABLED);
        }

        public String getResponseInlineMimetypes() {
            return get(FessConfig.RESPONSE_INLINE_MIMETYPES);
        }

        public String getIndexDocumentSearchIndex() {
            return get(FessConfig.INDEX_DOCUMENT_SEARCH_INDEX);
        }

        public String getIndexDocumentUpdateIndex() {
            return get(FessConfig.INDEX_DOCUMENT_UPDATE_INDEX);
        }

        public String getIndexDocumentSuggestIndex() {
            return get(FessConfig.INDEX_DOCUMENT_SUGGEST_INDEX);
        }

        public String getIndexDocumentCrawlerIndex() {
            return get(FessConfig.INDEX_DOCUMENT_CRAWLER_INDEX);
        }

        public String getIndexDocumentCrawlerQueueNumberOfShards() {
            return get(FessConfig.INDEX_DOCUMENT_CRAWLER_QUEUE_number_of_shards);
        }

        public Integer getIndexDocumentCrawlerQueueNumberOfShardsAsInteger() {
            return getAsInteger(FessConfig.INDEX_DOCUMENT_CRAWLER_QUEUE_number_of_shards);
        }

        public String getIndexDocumentCrawlerDataNumberOfShards() {
            return get(FessConfig.INDEX_DOCUMENT_CRAWLER_DATA_number_of_shards);
        }

        public Integer getIndexDocumentCrawlerDataNumberOfShardsAsInteger() {
            return getAsInteger(FessConfig.INDEX_DOCUMENT_CRAWLER_DATA_number_of_shards);
        }

        public String getIndexDocumentCrawlerFilterNumberOfShards() {
            return get(FessConfig.INDEX_DOCUMENT_CRAWLER_FILTER_number_of_shards);
        }

        public Integer getIndexDocumentCrawlerFilterNumberOfShardsAsInteger() {
            return getAsInteger(FessConfig.INDEX_DOCUMENT_CRAWLER_FILTER_number_of_shards);
        }

        public String getIndexDocumentCrawlerQueueNumberOfReplicas() {
            return get(FessConfig.INDEX_DOCUMENT_CRAWLER_QUEUE_number_of_replicas);
        }

        public Integer getIndexDocumentCrawlerQueueNumberOfReplicasAsInteger() {
            return getAsInteger(FessConfig.INDEX_DOCUMENT_CRAWLER_QUEUE_number_of_replicas);
        }

        public String getIndexDocumentCrawlerDataNumberOfReplicas() {
            return get(FessConfig.INDEX_DOCUMENT_CRAWLER_DATA_number_of_replicas);
        }

        public Integer getIndexDocumentCrawlerDataNumberOfReplicasAsInteger() {
            return getAsInteger(FessConfig.INDEX_DOCUMENT_CRAWLER_DATA_number_of_replicas);
        }

        public String getIndexDocumentCrawlerFilterNumberOfReplicas() {
            return get(FessConfig.INDEX_DOCUMENT_CRAWLER_FILTER_number_of_replicas);
        }

        public Integer getIndexDocumentCrawlerFilterNumberOfReplicasAsInteger() {
            return getAsInteger(FessConfig.INDEX_DOCUMENT_CRAWLER_FILTER_number_of_replicas);
        }

        public String getIndexConfigIndex() {
            return get(FessConfig.INDEX_CONFIG_INDEX);
        }

        public String getIndexUserIndex() {
            return get(FessConfig.INDEX_USER_INDEX);
        }

        public String getIndexLogIndex() {
            return get(FessConfig.INDEX_LOG_INDEX);
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

        public String getIndexScrollSearchTimeout() {
            return get(FessConfig.INDEX_SCROLL_SEARCH_TIMEOUT);
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

        public String getIndexFiletype() {
            return get(FessConfig.INDEX_FILETYPE);
        }

        public String getIndexReindexSize() {
            return get(FessConfig.INDEX_REINDEX_SIZE);
        }

        public Integer getIndexReindexSizeAsInteger() {
            return getAsInteger(FessConfig.INDEX_REINDEX_SIZE);
        }

        public String getIndexReindexBody() {
            return get(FessConfig.INDEX_REINDEX_BODY);
        }

        public String getIndexReindexRequestsPerSecond() {
            return get(FessConfig.INDEX_REINDEX_requests_per_second);
        }

        public String getIndexReindexRefresh() {
            return get(FessConfig.INDEX_REINDEX_REFRESH);
        }

        public boolean isIndexReindexRefresh() {
            return is(FessConfig.INDEX_REINDEX_REFRESH);
        }

        public String getIndexReindexTimeout() {
            return get(FessConfig.INDEX_REINDEX_TIMEOUT);
        }

        public String getIndexReindexScroll() {
            return get(FessConfig.INDEX_REINDEX_SCROLL);
        }

        public String getIndexReindexMaxDocs() {
            return get(FessConfig.INDEX_REINDEX_max_docs);
        }

        public Integer getIndexReindexMaxDocsAsInteger() {
            return getAsInteger(FessConfig.INDEX_REINDEX_max_docs);
        }

        public String getQueryMaxLength() {
            return get(FessConfig.QUERY_MAX_LENGTH);
        }

        public Integer getQueryMaxLengthAsInteger() {
            return getAsInteger(FessConfig.QUERY_MAX_LENGTH);
        }

        public String getQueryTimeout() {
            return get(FessConfig.QUERY_TIMEOUT);
        }

        public Integer getQueryTimeoutAsInteger() {
            return getAsInteger(FessConfig.QUERY_TIMEOUT);
        }

        public String getQueryTimeoutLogging() {
            return get(FessConfig.QUERY_TIMEOUT_LOGGING);
        }

        public boolean isQueryTimeoutLogging() {
            return is(FessConfig.QUERY_TIMEOUT_LOGGING);
        }

        public String getQueryTrackTotalHits() {
            return get(FessConfig.QUERY_TRACK_TOTAL_HITS);
        }

        public Integer getQueryTrackTotalHitsAsInteger() {
            return getAsInteger(FessConfig.QUERY_TRACK_TOTAL_HITS);
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

        public String getQueryHighlightTerminalChars() {
            return get(FessConfig.QUERY_HIGHLIGHT_TERMINAL_CHARS);
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

        public String getQueryHighlightTagPre() {
            return get(FessConfig.QUERY_HIGHLIGHT_TAG_PRE);
        }

        public String getQueryHighlightTagPost() {
            return get(FessConfig.QUERY_HIGHLIGHT_TAG_POST);
        }

        public String getQueryHighlightBoundaryChars() {
            return get(FessConfig.QUERY_HIGHLIGHT_BOUNDARY_CHARS);
        }

        public String getQueryHighlightBoundaryMaxScan() {
            return get(FessConfig.QUERY_HIGHLIGHT_BOUNDARY_MAX_SCAN);
        }

        public Integer getQueryHighlightBoundaryMaxScanAsInteger() {
            return getAsInteger(FessConfig.QUERY_HIGHLIGHT_BOUNDARY_MAX_SCAN);
        }

        public String getQueryHighlightBoundaryScanner() {
            return get(FessConfig.QUERY_HIGHLIGHT_BOUNDARY_SCANNER);
        }

        public String getQueryHighlightEncoder() {
            return get(FessConfig.QUERY_HIGHLIGHT_ENCODER);
        }

        public String getQueryHighlightForceSource() {
            return get(FessConfig.QUERY_HIGHLIGHT_FORCE_SOURCE);
        }

        public boolean isQueryHighlightForceSource() {
            return is(FessConfig.QUERY_HIGHLIGHT_FORCE_SOURCE);
        }

        public String getQueryHighlightFragmenter() {
            return get(FessConfig.QUERY_HIGHLIGHT_FRAGMENTER);
        }

        public String getQueryHighlightFragmentOffset() {
            return get(FessConfig.QUERY_HIGHLIGHT_FRAGMENT_OFFSET);
        }

        public Integer getQueryHighlightFragmentOffsetAsInteger() {
            return getAsInteger(FessConfig.QUERY_HIGHLIGHT_FRAGMENT_OFFSET);
        }

        public String getQueryHighlightNoMatchSize() {
            return get(FessConfig.QUERY_HIGHLIGHT_NO_MATCH_SIZE);
        }

        public Integer getQueryHighlightNoMatchSizeAsInteger() {
            return getAsInteger(FessConfig.QUERY_HIGHLIGHT_NO_MATCH_SIZE);
        }

        public String getQueryHighlightOrder() {
            return get(FessConfig.QUERY_HIGHLIGHT_ORDER);
        }

        public String getQueryHighlightPhraseLimit() {
            return get(FessConfig.QUERY_HIGHLIGHT_PHRASE_LIMIT);
        }

        public Integer getQueryHighlightPhraseLimitAsInteger() {
            return getAsInteger(FessConfig.QUERY_HIGHLIGHT_PHRASE_LIMIT);
        }

        public String getQueryHighlightContentDescriptionFields() {
            return get(FessConfig.QUERY_HIGHLIGHT_CONTENT_DESCRIPTION_FIELDS);
        }

        public String getQueryHighlightBoundaryPositionDetect() {
            return get(FessConfig.QUERY_HIGHLIGHT_BOUNDARY_POSITION_DETECT);
        }

        public boolean isQueryHighlightBoundaryPositionDetect() {
            return is(FessConfig.QUERY_HIGHLIGHT_BOUNDARY_POSITION_DETECT);
        }

        public String getQueryHighlightTextFragmentType() {
            return get(FessConfig.QUERY_HIGHLIGHT_TEXT_FRAGMENT_TYPE);
        }

        public String getQueryHighlightTextFragmentSize() {
            return get(FessConfig.QUERY_HIGHLIGHT_TEXT_FRAGMENT_SIZE);
        }

        public Integer getQueryHighlightTextFragmentSizeAsInteger() {
            return getAsInteger(FessConfig.QUERY_HIGHLIGHT_TEXT_FRAGMENT_SIZE);
        }

        public String getQueryHighlightTextFragmentPrefixLength() {
            return get(FessConfig.QUERY_HIGHLIGHT_TEXT_FRAGMENT_PREFIX_LENGTH);
        }

        public Integer getQueryHighlightTextFragmentPrefixLengthAsInteger() {
            return getAsInteger(FessConfig.QUERY_HIGHLIGHT_TEXT_FRAGMENT_PREFIX_LENGTH);
        }

        public String getQueryHighlightTextFragmentSuffixLength() {
            return get(FessConfig.QUERY_HIGHLIGHT_TEXT_FRAGMENT_SUFFIX_LENGTH);
        }

        public Integer getQueryHighlightTextFragmentSuffixLengthAsInteger() {
            return getAsInteger(FessConfig.QUERY_HIGHLIGHT_TEXT_FRAGMENT_SUFFIX_LENGTH);
        }

        public String getQueryMaxSearchResultOffset() {
            return get(FessConfig.QUERY_MAX_SEARCH_RESULT_OFFSET);
        }

        public Integer getQueryMaxSearchResultOffsetAsInteger() {
            return getAsInteger(FessConfig.QUERY_MAX_SEARCH_RESULT_OFFSET);
        }

        public String getQueryAdditionalDefaultFields() {
            return get(FessConfig.QUERY_ADDITIONAL_DEFAULT_FIELDS);
        }

        public Integer getQueryAdditionalDefaultFieldsAsInteger() {
            return getAsInteger(FessConfig.QUERY_ADDITIONAL_DEFAULT_FIELDS);
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

        public String getQueryAdditionalScrollResponseFields() {
            return get(FessConfig.QUERY_ADDITIONAL_SCROLL_RESPONSE_FIELDS);
        }

        public Integer getQueryAdditionalScrollResponseFieldsAsInteger() {
            return getAsInteger(FessConfig.QUERY_ADDITIONAL_SCROLL_RESPONSE_FIELDS);
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

        public String getQueryAdditionalAnalyzedFields() {
            return get(FessConfig.QUERY_ADDITIONAL_ANALYZED_FIELDS);
        }

        public Integer getQueryAdditionalAnalyzedFieldsAsInteger() {
            return getAsInteger(FessConfig.QUERY_ADDITIONAL_ANALYZED_FIELDS);
        }

        public String getQueryAdditionalNotAnalyzedFields() {
            return get(FessConfig.QUERY_ADDITIONAL_NOT_ANALYZED_FIELDS);
        }

        public Integer getQueryAdditionalNotAnalyzedFieldsAsInteger() {
            return getAsInteger(FessConfig.QUERY_ADDITIONAL_NOT_ANALYZED_FIELDS);
        }

        public String getQueryGsaResponseFields() {
            return get(FessConfig.QUERY_GSA_RESPONSE_FIELDS);
        }

        public String getQueryGsaDefaultLang() {
            return get(FessConfig.QUERY_GSA_DEFAULT_LANG);
        }

        public String getQueryGsaDefaultSort() {
            return get(FessConfig.QUERY_GSA_DEFAULT_SORT);
        }

        public Integer getQueryGsaDefaultSortAsInteger() {
            return getAsInteger(FessConfig.QUERY_GSA_DEFAULT_SORT);
        }

        public String getQueryGsaMetaPrefix() {
            return get(FessConfig.QUERY_GSA_META_PREFIX);
        }

        public String getQueryGsaIndexFieldCharset() {
            return get(FessConfig.QUERY_GSA_INDEX_FIELD_CHARSET);
        }

        public String getQueryGsaIndexFieldContentType() {
            return get(FessConfig.QUERY_GSA_INDEX_FIELD_content_type_);
        }

        public String getQueryCollapseMaxConcurrentGroupResults() {
            return get(FessConfig.QUERY_COLLAPSE_MAX_CONCURRENT_GROUP_RESULTS);
        }

        public Integer getQueryCollapseMaxConcurrentGroupResultsAsInteger() {
            return getAsInteger(FessConfig.QUERY_COLLAPSE_MAX_CONCURRENT_GROUP_RESULTS);
        }

        public String getQueryCollapseInnerHitsName() {
            return get(FessConfig.QUERY_COLLAPSE_INNER_HITS_NAME);
        }

        public String getQueryCollapseInnerHitsSize() {
            return get(FessConfig.QUERY_COLLAPSE_INNER_HITS_SIZE);
        }

        public Integer getQueryCollapseInnerHitsSizeAsInteger() {
            return getAsInteger(FessConfig.QUERY_COLLAPSE_INNER_HITS_SIZE);
        }

        public String getQueryCollapseInnerHitsSorts() {
            return get(FessConfig.QUERY_COLLAPSE_INNER_HITS_SORTS);
        }

        public Integer getQueryCollapseInnerHitsSortsAsInteger() {
            return getAsInteger(FessConfig.QUERY_COLLAPSE_INNER_HITS_SORTS);
        }

        public String getQueryDefaultLanguages() {
            return get(FessConfig.QUERY_DEFAULT_LANGUAGES);
        }

        public Integer getQueryDefaultLanguagesAsInteger() {
            return getAsInteger(FessConfig.QUERY_DEFAULT_LANGUAGES);
        }

        public String getQueryJsonDefaultPreference() {
            return get(FessConfig.QUERY_JSON_DEFAULT_PREFERENCE);
        }

        public String getQueryGsaDefaultPreference() {
            return get(FessConfig.QUERY_GSA_DEFAULT_PREFERENCE);
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

        public String getQueryBoostImportantContent() {
            return get(FessConfig.QUERY_BOOST_important_content);
        }

        public java.math.BigDecimal getQueryBoostImportantContentAsDecimal() {
            return getAsDecimal(FessConfig.QUERY_BOOST_important_content);
        }

        public String getQueryBoostImportantContentLang() {
            return get(FessConfig.QUERY_BOOST_important_content_LANG);
        }

        public java.math.BigDecimal getQueryBoostImportantContentLangAsDecimal() {
            return getAsDecimal(FessConfig.QUERY_BOOST_important_content_LANG);
        }

        public String getQueryBoostFuzzyMinLength() {
            return get(FessConfig.QUERY_BOOST_FUZZY_MIN_LENGTH);
        }

        public Integer getQueryBoostFuzzyMinLengthAsInteger() {
            return getAsInteger(FessConfig.QUERY_BOOST_FUZZY_MIN_LENGTH);
        }

        public String getQueryBoostFuzzyTitle() {
            return get(FessConfig.QUERY_BOOST_FUZZY_TITLE);
        }

        public java.math.BigDecimal getQueryBoostFuzzyTitleAsDecimal() {
            return getAsDecimal(FessConfig.QUERY_BOOST_FUZZY_TITLE);
        }

        public String getQueryBoostFuzzyTitleFuzziness() {
            return get(FessConfig.QUERY_BOOST_FUZZY_TITLE_FUZZINESS);
        }

        public String getQueryBoostFuzzyTitleExpansions() {
            return get(FessConfig.QUERY_BOOST_FUZZY_TITLE_EXPANSIONS);
        }

        public Integer getQueryBoostFuzzyTitleExpansionsAsInteger() {
            return getAsInteger(FessConfig.QUERY_BOOST_FUZZY_TITLE_EXPANSIONS);
        }

        public String getQueryBoostFuzzyTitlePrefixLength() {
            return get(FessConfig.QUERY_BOOST_FUZZY_TITLE_prefix_length);
        }

        public Integer getQueryBoostFuzzyTitlePrefixLengthAsInteger() {
            return getAsInteger(FessConfig.QUERY_BOOST_FUZZY_TITLE_prefix_length);
        }

        public String getQueryBoostFuzzyTitleTranspositions() {
            return get(FessConfig.QUERY_BOOST_FUZZY_TITLE_TRANSPOSITIONS);
        }

        public boolean isQueryBoostFuzzyTitleTranspositions() {
            return is(FessConfig.QUERY_BOOST_FUZZY_TITLE_TRANSPOSITIONS);
        }

        public String getQueryBoostFuzzyContent() {
            return get(FessConfig.QUERY_BOOST_FUZZY_CONTENT);
        }

        public java.math.BigDecimal getQueryBoostFuzzyContentAsDecimal() {
            return getAsDecimal(FessConfig.QUERY_BOOST_FUZZY_CONTENT);
        }

        public String getQueryBoostFuzzyContentFuzziness() {
            return get(FessConfig.QUERY_BOOST_FUZZY_CONTENT_FUZZINESS);
        }

        public String getQueryBoostFuzzyContentExpansions() {
            return get(FessConfig.QUERY_BOOST_FUZZY_CONTENT_EXPANSIONS);
        }

        public Integer getQueryBoostFuzzyContentExpansionsAsInteger() {
            return getAsInteger(FessConfig.QUERY_BOOST_FUZZY_CONTENT_EXPANSIONS);
        }

        public String getQueryBoostFuzzyContentPrefixLength() {
            return get(FessConfig.QUERY_BOOST_FUZZY_CONTENT_prefix_length);
        }

        public Integer getQueryBoostFuzzyContentPrefixLengthAsInteger() {
            return getAsInteger(FessConfig.QUERY_BOOST_FUZZY_CONTENT_prefix_length);
        }

        public String getQueryBoostFuzzyContentTranspositions() {
            return get(FessConfig.QUERY_BOOST_FUZZY_CONTENT_TRANSPOSITIONS);
        }

        public boolean isQueryBoostFuzzyContentTranspositions() {
            return is(FessConfig.QUERY_BOOST_FUZZY_CONTENT_TRANSPOSITIONS);
        }

        public String getQueryPrefixExpansions() {
            return get(FessConfig.QUERY_PREFIX_EXPANSIONS);
        }

        public Integer getQueryPrefixExpansionsAsInteger() {
            return getAsInteger(FessConfig.QUERY_PREFIX_EXPANSIONS);
        }

        public String getQueryPrefixSlop() {
            return get(FessConfig.QUERY_PREFIX_SLOP);
        }

        public Integer getQueryPrefixSlopAsInteger() {
            return getAsInteger(FessConfig.QUERY_PREFIX_SLOP);
        }

        public String getQueryFuzzyPrefixLength() {
            return get(FessConfig.QUERY_FUZZY_prefix_length);
        }

        public Integer getQueryFuzzyPrefixLengthAsInteger() {
            return getAsInteger(FessConfig.QUERY_FUZZY_prefix_length);
        }

        public String getQueryFuzzyExpansions() {
            return get(FessConfig.QUERY_FUZZY_EXPANSIONS);
        }

        public Integer getQueryFuzzyExpansionsAsInteger() {
            return getAsInteger(FessConfig.QUERY_FUZZY_EXPANSIONS);
        }

        public String getQueryFuzzyTranspositions() {
            return get(FessConfig.QUERY_FUZZY_TRANSPOSITIONS);
        }

        public boolean isQueryFuzzyTranspositions() {
            return is(FessConfig.QUERY_FUZZY_TRANSPOSITIONS);
        }

        public String getQueryFacetFields() {
            return get(FessConfig.QUERY_FACET_FIELDS);
        }

        public String getQueryFacetFieldsSize() {
            return get(FessConfig.QUERY_FACET_FIELDS_SIZE);
        }

        public Integer getQueryFacetFieldsSizeAsInteger() {
            return getAsInteger(FessConfig.QUERY_FACET_FIELDS_SIZE);
        }

        public String getQueryFacetFieldsMinDocCount() {
            return get(FessConfig.QUERY_FACET_FIELDS_min_doc_count);
        }

        public Integer getQueryFacetFieldsMinDocCountAsInteger() {
            return getAsInteger(FessConfig.QUERY_FACET_FIELDS_min_doc_count);
        }

        public String getQueryFacetFieldsSort() {
            return get(FessConfig.QUERY_FACET_FIELDS_SORT);
        }

        public String getQueryFacetFieldsMissing() {
            return get(FessConfig.QUERY_FACET_FIELDS_MISSING);
        }

        public Integer getQueryFacetFieldsMissingAsInteger() {
            return getAsInteger(FessConfig.QUERY_FACET_FIELDS_MISSING);
        }

        public String getQueryFacetQueries() {
            return get(FessConfig.QUERY_FACET_QUERIES);
        }

        public String getRankFusionWindowSize() {
            return get(FessConfig.RANK_FUSION_window_size);
        }

        public Integer getRankFusionWindowSizeAsInteger() {
            return getAsInteger(FessConfig.RANK_FUSION_window_size);
        }

        public String getRankFusionRankConstant() {
            return get(FessConfig.RANK_FUSION_rank_constant);
        }

        public Integer getRankFusionRankConstantAsInteger() {
            return getAsInteger(FessConfig.RANK_FUSION_rank_constant);
        }

        public String getRankFusionThreads() {
            return get(FessConfig.RANK_FUSION_THREADS);
        }

        public Integer getRankFusionThreadsAsInteger() {
            return getAsInteger(FessConfig.RANK_FUSION_THREADS);
        }

        public String getRankFusionScoreField() {
            return get(FessConfig.RANK_FUSION_score_field);
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

        public String getFileRoleFromFile() {
            return get(FessConfig.FILE_ROLE_FROM_FILE);
        }

        public boolean isFileRoleFromFile() {
            return is(FessConfig.FILE_ROLE_FROM_FILE);
        }

        public String getFtpRoleFromFile() {
            return get(FessConfig.FTP_ROLE_FROM_FILE);
        }

        public boolean isFtpRoleFromFile() {
            return is(FessConfig.FTP_ROLE_FROM_FILE);
        }

        public String getIndexBackupTargets() {
            return get(FessConfig.INDEX_BACKUP_TARGETS);
        }

        public String getIndexBackupLogTargets() {
            return get(FessConfig.INDEX_BACKUP_LOG_TARGETS);
        }

        public String getLoggingSearchDocsEnabled() {
            return get(FessConfig.LOGGING_SEARCH_DOCS_ENABLED);
        }

        public boolean isLoggingSearchDocsEnabled() {
            return is(FessConfig.LOGGING_SEARCH_DOCS_ENABLED);
        }

        public String getLoggingSearchDocsFields() {
            return get(FessConfig.LOGGING_SEARCH_DOCS_FIELDS);
        }

        public String getLoggingSearchUseLogfile() {
            return get(FessConfig.LOGGING_SEARCH_USE_LOGFILE);
        }

        public boolean isLoggingSearchUseLogfile() {
            return is(FessConfig.LOGGING_SEARCH_USE_LOGFILE);
        }

        public String getLoggingAppPackages() {
            return get(FessConfig.LOGGING_APP_PACKAGES);
        }

        public String getFormAdminMaxInputSize() {
            return get(FessConfig.FORM_ADMIN_MAX_INPUT_SIZE);
        }

        public Integer getFormAdminMaxInputSizeAsInteger() {
            return getAsInteger(FessConfig.FORM_ADMIN_MAX_INPUT_SIZE);
        }

        public String getFormAdminLabelInConfigEnabled() {
            return get(FessConfig.FORM_ADMIN_LABEL_IN_CONFIG_ENABLED);
        }

        public boolean isFormAdminLabelInConfigEnabled() {
            return is(FessConfig.FORM_ADMIN_LABEL_IN_CONFIG_ENABLED);
        }

        public String getFormAdminDefaultTemplateName() {
            return get(FessConfig.FORM_ADMIN_DEFAULT_TEMPLATE_NAME);
        }

        public String getOsddLinkEnabled() {
            return get(FessConfig.OSDD_LINK_ENABLED);
        }

        public boolean isOsddLinkEnabled() {
            return is(FessConfig.OSDD_LINK_ENABLED);
        }

        public String getClipboardCopyIconEnabled() {
            return get(FessConfig.CLIPBOARD_COPY_ICON_ENABLED);
        }

        public boolean isClipboardCopyIconEnabled() {
            return is(FessConfig.CLIPBOARD_COPY_ICON_ENABLED);
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

        public String getRoleSearchDeniedPrefix() {
            return get(FessConfig.ROLE_SEARCH_DENIED_PREFIX);
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

        public String getSessionTrackingModes() {
            return get(FessConfig.SESSION_TRACKING_MODES);
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

        public String getPageRelatedcontentMaxFetchSize() {
            return get(FessConfig.PAGE_RELATEDCONTENT_MAX_FETCH_SIZE);
        }

        public Integer getPageRelatedcontentMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_RELATEDCONTENT_MAX_FETCH_SIZE);
        }

        public String getPageRelatedqueryMaxFetchSize() {
            return get(FessConfig.PAGE_RELATEDQUERY_MAX_FETCH_SIZE);
        }

        public Integer getPageRelatedqueryMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_RELATEDQUERY_MAX_FETCH_SIZE);
        }

        public String getPageThumbnailQueueMaxFetchSize() {
            return get(FessConfig.PAGE_THUMBNAIL_QUEUE_MAX_FETCH_SIZE);
        }

        public Integer getPageThumbnailQueueMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_THUMBNAIL_QUEUE_MAX_FETCH_SIZE);
        }

        public String getPageThumbnailPurgeMaxFetchSize() {
            return get(FessConfig.PAGE_THUMBNAIL_PURGE_MAX_FETCH_SIZE);
        }

        public Integer getPageThumbnailPurgeMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_THUMBNAIL_PURGE_MAX_FETCH_SIZE);
        }

        public String getPageScoreBoosterMaxFetchSize() {
            return get(FessConfig.PAGE_SCORE_BOOSTER_MAX_FETCH_SIZE);
        }

        public Integer getPageScoreBoosterMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_SCORE_BOOSTER_MAX_FETCH_SIZE);
        }

        public String getPageSearchlogMaxFetchSize() {
            return get(FessConfig.PAGE_SEARCHLOG_MAX_FETCH_SIZE);
        }

        public Integer getPageSearchlogMaxFetchSizeAsInteger() {
            return getAsInteger(FessConfig.PAGE_SEARCHLOG_MAX_FETCH_SIZE);
        }

        public String getPageSearchlistTrackTotalHits() {
            return get(FessConfig.PAGE_SEARCHLIST_TRACK_TOTAL_HITS);
        }

        public boolean isPageSearchlistTrackTotalHits() {
            return is(FessConfig.PAGE_SEARCHLIST_TRACK_TOTAL_HITS);
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

        public String getSearchlogAggShardSize() {
            return get(FessConfig.SEARCHLOG_AGG_SHARD_SIZE);
        }

        public Integer getSearchlogAggShardSizeAsInteger() {
            return getAsInteger(FessConfig.SEARCHLOG_AGG_SHARD_SIZE);
        }

        public String getSearchlogRequestHeaders() {
            return get(FessConfig.SEARCHLOG_REQUEST_HEADERS);
        }

        public Integer getSearchlogRequestHeadersAsInteger() {
            return getAsInteger(FessConfig.SEARCHLOG_REQUEST_HEADERS);
        }

        public String getThumbnailHtmlImageMinWidth() {
            return get(FessConfig.THUMBNAIL_HTML_IMAGE_MIN_WIDTH);
        }

        public Integer getThumbnailHtmlImageMinWidthAsInteger() {
            return getAsInteger(FessConfig.THUMBNAIL_HTML_IMAGE_MIN_WIDTH);
        }

        public String getThumbnailHtmlImageMinHeight() {
            return get(FessConfig.THUMBNAIL_HTML_IMAGE_MIN_HEIGHT);
        }

        public Integer getThumbnailHtmlImageMinHeightAsInteger() {
            return getAsInteger(FessConfig.THUMBNAIL_HTML_IMAGE_MIN_HEIGHT);
        }

        public String getThumbnailHtmlImageMaxAspectRatio() {
            return get(FessConfig.THUMBNAIL_HTML_IMAGE_MAX_ASPECT_RATIO);
        }

        public java.math.BigDecimal getThumbnailHtmlImageMaxAspectRatioAsDecimal() {
            return getAsDecimal(FessConfig.THUMBNAIL_HTML_IMAGE_MAX_ASPECT_RATIO);
        }

        public String getThumbnailHtmlImageThumbnailWidth() {
            return get(FessConfig.THUMBNAIL_HTML_IMAGE_THUMBNAIL_WIDTH);
        }

        public Integer getThumbnailHtmlImageThumbnailWidthAsInteger() {
            return getAsInteger(FessConfig.THUMBNAIL_HTML_IMAGE_THUMBNAIL_WIDTH);
        }

        public String getThumbnailHtmlImageThumbnailHeight() {
            return get(FessConfig.THUMBNAIL_HTML_IMAGE_THUMBNAIL_HEIGHT);
        }

        public Integer getThumbnailHtmlImageThumbnailHeightAsInteger() {
            return getAsInteger(FessConfig.THUMBNAIL_HTML_IMAGE_THUMBNAIL_HEIGHT);
        }

        public String getThumbnailHtmlImageFormat() {
            return get(FessConfig.THUMBNAIL_HTML_IMAGE_FORMAT);
        }

        public String getThumbnailHtmlImageXpath() {
            return get(FessConfig.THUMBNAIL_HTML_IMAGE_XPATH);
        }

        public String getThumbnailHtmlImageExcludeExtensions() {
            return get(FessConfig.THUMBNAIL_HTML_IMAGE_EXCLUDE_EXTENSIONS);
        }

        public String getThumbnailGeneratorInterval() {
            return get(FessConfig.THUMBNAIL_GENERATOR_INTERVAL);
        }

        public Integer getThumbnailGeneratorIntervalAsInteger() {
            return getAsInteger(FessConfig.THUMBNAIL_GENERATOR_INTERVAL);
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

        public String getThumbnailSystemMonitorInterval() {
            return get(FessConfig.THUMBNAIL_SYSTEM_MONITOR_INTERVAL);
        }

        public Integer getThumbnailSystemMonitorIntervalAsInteger() {
            return getAsInteger(FessConfig.THUMBNAIL_SYSTEM_MONITOR_INTERVAL);
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

        public String getMailHostname() {
            return get(FessConfig.MAIL_HOSTNAME);
        }

        public Integer getMailHostnameAsInteger() {
            return getAsInteger(FessConfig.MAIL_HOSTNAME);
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

        public String getOnlineHelpInstallation() {
            return get(FessConfig.ONLINE_HELP_INSTALLATION);
        }

        public String getOnlineHelpEol() {
            return get(FessConfig.ONLINE_HELP_EOL);
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

        public String getOnlineHelpNameDictProtwords() {
            return get(FessConfig.ONLINE_HELP_NAME_DICT_PROTWORDS);
        }

        public String getOnlineHelpNameDictStopwords() {
            return get(FessConfig.ONLINE_HELP_NAME_DICT_STOPWORDS);
        }

        public String getOnlineHelpNameDictStemmeroverride() {
            return get(FessConfig.ONLINE_HELP_NAME_DICT_STEMMEROVERRIDE);
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

        public String getOnlineHelpNameRelatedquery() {
            return get(FessConfig.ONLINE_HELP_NAME_RELATEDQUERY);
        }

        public String getOnlineHelpNameRelatedcontent() {
            return get(FessConfig.ONLINE_HELP_NAME_RELATEDCONTENT);
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

        public String getOnlineHelpNameSearchlog() {
            return get(FessConfig.ONLINE_HELP_NAME_SEARCHLOG);
        }

        public String getOnlineHelpNameMaintenance() {
            return get(FessConfig.ONLINE_HELP_NAME_MAINTENANCE);
        }

        public String getOnlineHelpNamePlugin() {
            return get(FessConfig.ONLINE_HELP_NAME_PLUGIN);
        }

        public String getOnlineHelpNameStorage() {
            return get(FessConfig.ONLINE_HELP_NAME_STORAGE);
        }

        public String getOnlineHelpSupportedLangs() {
            return get(FessConfig.ONLINE_HELP_SUPPORTED_LANGS);
        }

        public String getForumLink() {
            return get(FessConfig.FORUM_LINK);
        }

        public String getForumSupportedLangs() {
            return get(FessConfig.FORUM_SUPPORTED_LANGS);
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

        public String getSuggestPopularWordQueryFreq() {
            return get(FessConfig.SUGGEST_POPULAR_WORD_QUERY_FREQ);
        }

        public Integer getSuggestPopularWordQueryFreqAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_POPULAR_WORD_QUERY_FREQ);
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

        public String getSuggestUpdateDocPerRequest() {
            return get(FessConfig.SUGGEST_UPDATE_DOC_PER_REQUEST);
        }

        public Integer getSuggestUpdateDocPerRequestAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_UPDATE_DOC_PER_REQUEST);
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

        public String getSuggestUpdateContentsLimitDocSize() {
            return get(FessConfig.SUGGEST_UPDATE_CONTENTS_LIMIT_DOC_SIZE);
        }

        public Integer getSuggestUpdateContentsLimitDocSizeAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_UPDATE_CONTENTS_LIMIT_DOC_SIZE);
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

        public String getSuggestSystemMonitorInterval() {
            return get(FessConfig.SUGGEST_SYSTEM_MONITOR_INTERVAL);
        }

        public Integer getSuggestSystemMonitorIntervalAsInteger() {
            return getAsInteger(FessConfig.SUGGEST_SYSTEM_MONITOR_INTERVAL);
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

        public String getLdapAuthValidation() {
            return get(FessConfig.LDAP_AUTH_VALIDATION);
        }

        public boolean isLdapAuthValidation() {
            return is(FessConfig.LDAP_AUTH_VALIDATION);
        }

        public String getLdapMaxUsernameLength() {
            return get(FessConfig.LDAP_MAX_USERNAME_LENGTH);
        }

        public Integer getLdapMaxUsernameLengthAsInteger() {
            return getAsInteger(FessConfig.LDAP_MAX_USERNAME_LENGTH);
        }

        public String getLdapIgnoreNetbiosName() {
            return get(FessConfig.LDAP_IGNORE_NETBIOS_NAME);
        }

        public boolean isLdapIgnoreNetbiosName() {
            return is(FessConfig.LDAP_IGNORE_NETBIOS_NAME);
        }

        public String getLdapGroupNameWithUnderscores() {
            return get(FessConfig.LDAP_GROUP_NAME_WITH_UNDERSCORES);
        }

        public boolean isLdapGroupNameWithUnderscores() {
            return is(FessConfig.LDAP_GROUP_NAME_WITH_UNDERSCORES);
        }

        public String getLdapLowercasePermissionName() {
            return get(FessConfig.LDAP_LOWERCASE_PERMISSION_NAME);
        }

        public boolean isLdapLowercasePermissionName() {
            return is(FessConfig.LDAP_LOWERCASE_PERMISSION_NAME);
        }

        public String getLdapAllowEmptyPermission() {
            return get(FessConfig.LDAP_ALLOW_EMPTY_PERMISSION);
        }

        public boolean isLdapAllowEmptyPermission() {
            return is(FessConfig.LDAP_ALLOW_EMPTY_PERMISSION);
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

        public String getLdapAttrLabeledURI() {
            return get(FessConfig.LDAP_ATTR_LABELED_U_R_I);
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

        public String getLdapAttrInternationaliSDNNumber() {
            return get(FessConfig.LDAP_ATTR_INTERNATIONALI_S_D_N_NUMBER);
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

        public String getPluginRepositories() {
            return get(FessConfig.PLUGIN_REPOSITORIES);
        }

        public String getPluginVersionFilter() {
            return get(FessConfig.PLUGIN_VERSION_FILTER);
        }

        public Integer getPluginVersionFilterAsInteger() {
            return getAsInteger(FessConfig.PLUGIN_VERSION_FILTER);
        }

        public String getStorageMaxItemsInPage() {
            return get(FessConfig.STORAGE_MAX_ITEMS_IN_PAGE);
        }

        public Integer getStorageMaxItemsInPageAsInteger() {
            return getAsInteger(FessConfig.STORAGE_MAX_ITEMS_IN_PAGE);
        }

        public String getPasswordInvalidAdminPasswords() {
            return get(FessConfig.PASSWORD_INVALID_ADMIN_PASSWORDS);
        }

        @Override
        protected java.util.Map<String, String> prepareGeneratedDefaultMap() {
            java.util.Map<String, String> defaultMap = super.prepareGeneratedDefaultMap();
            defaultMap.put(FessConfig.DOMAIN_TITLE, "Fess");
            defaultMap.put(FessConfig.search_engine_TYPE, "default");
            defaultMap.put(FessConfig.search_engine_HTTP_URL, "http://localhost:9201");
            defaultMap.put(FessConfig.search_engine_HTTP_SSL_certificate_authorities, "");
            defaultMap.put(FessConfig.search_engine_USERNAME, "");
            defaultMap.put(FessConfig.search_engine_PASSWORD, "");
            defaultMap.put(FessConfig.search_engine_heartbeat_interval, "10000");
            defaultMap.put(FessConfig.APP_CIPHER_ALGORISM, "aes");
            defaultMap.put(FessConfig.APP_CIPHER_KEY, "___change__me___");
            defaultMap.put(FessConfig.APP_DIGEST_ALGORISM, "sha256");
            defaultMap.put(FessConfig.APP_ENCRYPT_PROPERTY_PATTERN, ".*password|.*key|.*token|.*secret");
            defaultMap.put(FessConfig.APP_EXTENSION_NAMES, "");
            defaultMap.put(FessConfig.APP_AUDIT_LOG_FORMAT, "");
            defaultMap.put(FessConfig.JVM_CRAWLER_OPTIONS,
                    "-Djava.awt.headless=true\n-Dfile.encoding=UTF-8\n-Djna.nosys=true\n-Djdk.io.permissionsUseCanonicalPath=true\n-Dhttp.maxConnections=20\n-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager\n-server\n-Xms128m\n-Xmx512m\n-XX:MaxMetaspaceSize=128m\n-XX:CompressedClassSpaceSize=32m\n-XX:-UseGCOverheadLimit\n-XX:+UseTLAB\n-XX:+DisableExplicitGC\n-XX:-HeapDumpOnOutOfMemoryError\n-XX:-OmitStackTraceInFastThrow\n-XX:+UnlockExperimentalVMOptions\n-XX:+UseG1GC\n-XX:InitiatingHeapOccupancyPercent=45\n-XX:G1HeapRegionSize=1m\n-XX:MaxGCPauseMillis=60000\n-XX:G1NewSizePercent=5\n-XX:G1MaxNewSizePercent=5\n-Djcifs.smb.client.responseTimeout=30000\n-Djcifs.smb.client.soTimeout=35000\n-Djcifs.smb.client.connTimeout=60000\n-Djcifs.smb.client.sessionTimeout=60000\n-Djcifs.smb1.smb.client.connTimeout=60000\n-Djcifs.smb1.smb.client.soTimeout=35000\n-Djcifs.smb1.smb.client.responseTimeout=30000\n-Dio.netty.noUnsafe=true\n-Dio.netty.noKeySetOptimization=true\n-Dio.netty.recycler.maxCapacityPerThread=0\n-Dlog4j.shutdownHookEnabled=false\n-Dlog4j2.formatMsgNoLookups=true\n-Dlog4j2.disable.jmx=true\n-Dlog4j.skipJansi=true\n-Dsun.java2d.cmm=sun.java2d.cmm.kcms.KcmsServiceProvider\n-Dorg.apache.pdfbox.rendering.UsePureJavaCMYKConversion=true\n");
            defaultMap.put(FessConfig.JVM_SUGGEST_OPTIONS,
                    "-Djava.awt.headless=true\n-Dfile.encoding=UTF-8\n-Djna.nosys=true\n-Djdk.io.permissionsUseCanonicalPath=true\n-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager\n-server\n-Xms128m\n-Xmx256m\n-XX:MaxMetaspaceSize=128m\n-XX:CompressedClassSpaceSize=32m\n-XX:-UseGCOverheadLimit\n-XX:+UseTLAB\n-XX:+DisableExplicitGC\n-XX:-HeapDumpOnOutOfMemoryError\n-XX:+UnlockExperimentalVMOptions\n-XX:+UseG1GC\n-XX:InitiatingHeapOccupancyPercent=45\n-XX:G1HeapRegionSize=1m\n-XX:MaxGCPauseMillis=60000\n-XX:G1NewSizePercent=5\n-XX:G1MaxNewSizePercent=30\n-Dio.netty.noUnsafe=true\n-Dio.netty.noKeySetOptimization=true\n-Dio.netty.recycler.maxCapacityPerThread=0\n-Dlog4j.shutdownHookEnabled=false\n-Dlog4j2.disable.jmx=true\n-Dlog4j2.formatMsgNoLookups=true\n-Dlog4j.skipJansi=true\n");
            defaultMap.put(FessConfig.JVM_THUMBNAIL_OPTIONS,
                    "-Djava.awt.headless=true\n-Dfile.encoding=UTF-8\n-Djna.nosys=true\n-Djdk.io.permissionsUseCanonicalPath=true\n-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager\n-server\n-Xms128m\n-Xmx256m\n-XX:MaxMetaspaceSize=128m\n-XX:CompressedClassSpaceSize=32m\n-XX:-UseGCOverheadLimit\n-XX:+UseTLAB\n-XX:+DisableExplicitGC\n-XX:-HeapDumpOnOutOfMemoryError\n-XX:-OmitStackTraceInFastThrow\n-XX:+UnlockExperimentalVMOptions\n-XX:+UseG1GC\n-XX:InitiatingHeapOccupancyPercent=45\n-XX:G1HeapRegionSize=4m\n-XX:MaxGCPauseMillis=60000\n-XX:G1NewSizePercent=5\n-XX:G1MaxNewSizePercent=50\n-Djcifs.smb.client.responseTimeout=30000\n-Djcifs.smb.client.soTimeout=35000\n-Djcifs.smb.client.connTimeout=60000\n-Djcifs.smb.client.sessionTimeout=60000\n-Djcifs.smb1.smb.client.connTimeout=60000\n-Djcifs.smb1.smb.client.soTimeout=35000\n-Djcifs.smb1.smb.client.responseTimeout=30000\n-Dio.netty.noUnsafe=true\n-Dio.netty.noKeySetOptimization=true\n-Dio.netty.recycler.maxCapacityPerThread=0\n-Dlog4j.shutdownHookEnabled=false\n-Dlog4j2.disable.jmx=true\n-Dlog4j2.formatMsgNoLookups=true\n-Dlog4j.skipJansi=true\n-Dsun.java2d.cmm=sun.java2d.cmm.kcms.KcmsServiceProvider\n-Dorg.apache.pdfbox.rendering.UsePureJavaCMYKConversion=true\n");
            defaultMap.put(FessConfig.JOB_SYSTEM_JOB_IDS, "default_crawler");
            defaultMap.put(FessConfig.JOB_TEMPLATE_TITLE_WEB, "Web Crawler - {0}");
            defaultMap.put(FessConfig.JOB_TEMPLATE_TITLE_FILE, "File Crawler - {0}");
            defaultMap.put(FessConfig.JOB_TEMPLATE_TITLE_DATA, "Data Crawler - {0}");
            defaultMap.put(FessConfig.JOB_TEMPLATE_SCRIPT,
                    "return container.getComponent(\"crawlJob\").logLevel(\"info\").sessionId(\"{3}\").webConfigIds([{0}] as String[]).fileConfigIds([{1}] as String[]).dataConfigIds([{2}] as String[]).jobExecutor(executor).execute();");
            defaultMap.put(FessConfig.JOB_MAX_CRAWLER_PROCESSES, "0");
            defaultMap.put(FessConfig.JOB_DEFAULT_SCRIPT, "groovy");
            defaultMap.put(FessConfig.PROCESSORS, "0");
            defaultMap.put(FessConfig.JAVA_COMMAND_PATH, "java");
            defaultMap.put(FessConfig.PYTHON_COMMAND_PATH, "python");
            defaultMap.put(FessConfig.PATH_ENCODING, "UTF-8");
            defaultMap.put(FessConfig.USE_OWN_TMP_DIR, "true");
            defaultMap.put(FessConfig.MAX_LOG_OUTPUT_LENGTH, "4000");
            defaultMap.put(FessConfig.ADAPTIVE_LOAD_CONTROL, "50");
            defaultMap.put(FessConfig.SUPPORTED_UPLOADED_JS_EXTENTIONS, "js");
            defaultMap.put(FessConfig.SUPPORTED_UPLOADED_CSS_EXTENTIONS, "css");
            defaultMap.put(FessConfig.SUPPORTED_UPLOADED_MEDIA_EXTENTIONS, "jpg,jpeg,gif,png,swf");
            defaultMap.put(FessConfig.SUPPORTED_UPLOADED_FILES, "license.properties");
            defaultMap.put(FessConfig.SUPPORTED_LANGUAGES,
                    "ar,bg,bn,ca,ckb_IQ,cs,da,de,el,en_IE,en,es,et,eu,fa,fi,fr,gl,gu,he,hi,hr,hu,hy,id,it,ja,ko,lt,lv,mk,ml,nl,no,pa,pl,pt_BR,pt,ro,ru,si,sq,sv,ta,te,th,tl,tr,uk,ur,vi,zh_CN,zh_TW,zh");
            defaultMap.put(FessConfig.API_ACCESS_TOKEN_LENGTH, "60");
            defaultMap.put(FessConfig.API_ACCESS_TOKEN_REQUIRED, "false");
            defaultMap.put(FessConfig.API_ACCESS_TOKEN_REQUEST_PARAMETER, "");
            defaultMap.put(FessConfig.API_ADMIN_ACCESS_PERMISSIONS, "Radmin-api");
            defaultMap.put(FessConfig.API_SEARCH_ACCEPT_REFERERS, "");
            defaultMap.put(FessConfig.API_SEARCH_SCROLL, "false");
            defaultMap.put(FessConfig.API_JSON_RESPONSE_HEADERS, "");
            defaultMap.put(FessConfig.API_JSON_RESPONSE_EXCEPTION_INCLUDED, "false");
            defaultMap.put(FessConfig.API_GSA_RESPONSE_HEADERS, "");
            defaultMap.put(FessConfig.API_GSA_RESPONSE_EXCEPTION_INCLUDED, "false");
            defaultMap.put(FessConfig.API_DASHBOARD_RESPONSE_HEADERS, "");
            defaultMap.put(FessConfig.API_CORS_ALLOW_ORIGIN, "*");
            defaultMap.put(FessConfig.API_CORS_ALLOW_METHODS, "GET, POST, OPTIONS, DELETE, PUT");
            defaultMap.put(FessConfig.API_CORS_MAX_AGE, "3600");
            defaultMap.put(FessConfig.API_CORS_ALLOW_HEADERS, "Origin, Content-Type, Accept, Authorization, X-Requested-With");
            defaultMap.put(FessConfig.API_CORS_ALLOW_CREDENTIALS, "true");
            defaultMap.put(FessConfig.API_JSONP_ENABLED, "false");
            defaultMap.put(FessConfig.API_PING_search_engine_FIELDS, "status,timed_out");
            defaultMap.put(FessConfig.VIRTUAL_HOST_HEADERS, "");
            defaultMap.put(FessConfig.HTTP_PROXY_HOST, "");
            defaultMap.put(FessConfig.HTTP_PROXY_PORT, "8080");
            defaultMap.put(FessConfig.HTTP_PROXY_USERNAME, "");
            defaultMap.put(FessConfig.HTTP_PROXY_PASSWORD, "");
            defaultMap.put(FessConfig.HTTP_FILEUPLOAD_MAX_SIZE, "262144000");
            defaultMap.put(FessConfig.HTTP_FILEUPLOAD_THRESHOLD_SIZE, "262144");
            defaultMap.put(FessConfig.CRAWLER_DEFAULT_SCRIPT, "groovy");
            defaultMap.put(FessConfig.CRAWLER_HTTP_thread_pool_SIZE, "0");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_MAX_SITE_LENGTH, "100");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_SITE_ENCODING, "UTF-8");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_UNKNOWN_HOSTNAME, "unknown");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_USE_SITE_ENCODING_ON_ENGLISH, "false");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_APPEND_DATA, "true");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_APPEND_FILENAME, "false");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_MAX_ALPHANUM_TERM_SIZE, "20");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_MAX_SYMBOL_TERM_SIZE, "10");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_DUPLICATE_TERM_REMOVED, "false");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_SPACE_CHARS,
                    "u0009u000Au000Bu000Cu000Du001Cu001Du001Eu001Fu0020u00A0u1680u180Eu2000u2001u2002u2003u2004u2005u2006u2007u2008u2009u200Au200Bu200Cu202Fu205Fu3000uFEFFuFFFDu00B6");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FULLSTOP_CHARS, "u002eu06d4u2e3cu3002");
            defaultMap.put(FessConfig.CRAWLER_CRAWLING_DATA_ENCODING, "UTF-8");
            defaultMap.put(FessConfig.CRAWLER_WEB_PROTOCOLS, "http,https");
            defaultMap.put(FessConfig.CRAWLER_FILE_PROTOCOLS, "file,smb,smb1,ftp,storage");
            defaultMap.put(FessConfig.CRAWLER_DATA_ENV_PARAM_KEY_PATTERN, "^FESS_ENV_.*");
            defaultMap.put(FessConfig.CRAWLER_IGNORE_ROBOTS_TXT, "false");
            defaultMap.put(FessConfig.CRAWLER_IGNORE_ROBOTS_TAGS, "false");
            defaultMap.put(FessConfig.CRAWLER_IGNORE_CONTENT_EXCEPTION, "true");
            defaultMap.put(FessConfig.CRAWLER_FAILURE_URL_STATUS_CODES, "404");
            defaultMap.put(FessConfig.CRAWLER_SYSTEM_MONITOR_INTERVAL, "60");
            defaultMap.put(FessConfig.CRAWLER_HOTTHREAD_ignore_idle_threads, "true");
            defaultMap.put(FessConfig.CRAWLER_HOTTHREAD_INTERVAL, "500ms");
            defaultMap.put(FessConfig.CRAWLER_HOTTHREAD_SNAPSHOTS, "10");
            defaultMap.put(FessConfig.CRAWLER_HOTTHREAD_THREADS, "3");
            defaultMap.put(FessConfig.CRAWLER_HOTTHREAD_TIMEOUT, "30s");
            defaultMap.put(FessConfig.CRAWLER_HOTTHREAD_TYPE, "cpu");
            defaultMap.put(FessConfig.CRAWLER_METADATA_CONTENT_EXCLUDES,
                    "resourceName,X-Parsed-By,Content-Encoding.*,Content-Type.*,X-TIKA.*");
            defaultMap.put(FessConfig.CRAWLER_METADATA_NAME_MAPPING, "title=title:string\nTitle=title:string\ndc:title=title:string\n");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_HTML_CONTENT_XPATH, "//BODY");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_HTML_LANG_XPATH, "//HTML/@lang");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_HTML_DIGEST_XPATH, "//META[@name='description']/@content");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_HTML_CANONICAL_XPATH, "//LINK[@rel='canonical'][1]/@href");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_HTML_PRUNED_TAGS, "noscript,script,style,header,footer,nav,a[rel=nofollow]");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_HTML_MAX_DIGEST_LENGTH, "120");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_LANG, "");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_INCLUDE_INDEX_PATTERNS, "");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_EXCLUDE_INDEX_PATTERNS,
                    "(?i).*(css|js|jpeg|jpg|gif|png|bmp|wmv|xml|ico|exe)");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_INCLUDE_SEARCH_PATTERNS, "");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_HTML_DEFAULT_EXCLUDE_SEARCH_PATTERNS, "");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FILE_NAME_ENCODING, "");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FILE_NO_TITLE_LABEL, "No title.");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FILE_IGNORE_EMPTY_CONTENT, "false");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FILE_MAX_TITLE_LENGTH, "100");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FILE_MAX_DIGEST_LENGTH, "200");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FILE_APPEND_META_CONTENT, "true");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FILE_APPEND_BODY_CONTENT, "true");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_LANG, "");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_INCLUDE_INDEX_PATTERNS, "");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_EXCLUDE_INDEX_PATTERNS, "");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_INCLUDE_SEARCH_PATTERNS, "");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_FILE_DEFAULT_EXCLUDE_SEARCH_PATTERNS, "");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_CACHE_ENABLED, "true");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_CACHE_MAX_SIZE, "2621440");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_CACHE_SUPPORTED_MIMETYPES, "text/html");
            defaultMap.put(FessConfig.CRAWLER_DOCUMENT_CACHE_HTML_MIMETYPES, "text/html");
            defaultMap.put(FessConfig.INDEXER_THREAD_DUMP_ENABLED, "true");
            defaultMap.put(FessConfig.INDEXER_UNPROCESSED_DOCUMENT_SIZE, "1000");
            defaultMap.put(FessConfig.INDEXER_CLICK_COUNT_ENABLED, "true");
            defaultMap.put(FessConfig.INDEXER_FAVORITE_COUNT_ENABLED, "true");
            defaultMap.put(FessConfig.INDEXER_WEBFS_COMMIT_MARGIN_TIME, "5000");
            defaultMap.put(FessConfig.INDEXER_WEBFS_MAX_EMPTY_LIST_COUNT, "3600");
            defaultMap.put(FessConfig.INDEXER_WEBFS_UPDATE_INTERVAL, "10000");
            defaultMap.put(FessConfig.INDEXER_WEBFS_MAX_DOCUMENT_CACHE_SIZE, "10");
            defaultMap.put(FessConfig.INDEXER_WEBFS_MAX_DOCUMENT_REQUEST_SIZE, "1048576");
            defaultMap.put(FessConfig.INDEXER_DATA_MAX_DOCUMENT_CACHE_SIZE, "10000");
            defaultMap.put(FessConfig.INDEXER_DATA_MAX_DOCUMENT_REQUEST_SIZE, "1048576");
            defaultMap.put(FessConfig.INDEXER_DATA_MAX_DELETE_CACHE_SIZE, "100");
            defaultMap.put(FessConfig.INDEXER_DATA_MAX_REDIRECT_COUNT, "10");
            defaultMap.put(FessConfig.INDEXER_LANGUAGE_FIELDS, "content,important_content,title");
            defaultMap.put(FessConfig.INDEXER_LANGUAGE_DETECT_LENGTH, "1000");
            defaultMap.put(FessConfig.INDEXER_MAX_RESULT_WINDOW_SIZE, "10000");
            defaultMap.put(FessConfig.INDEXER_MAX_SEARCH_DOC_SIZE, "50000");
            defaultMap.put(FessConfig.INDEX_CODEC, "default");
            defaultMap.put(FessConfig.INDEX_number_of_shards, "5");
            defaultMap.put(FessConfig.INDEX_auto_expand_replicas, "0-1");
            defaultMap.put(FessConfig.INDEX_ID_DIGEST_ALGORITHM, "SHA-512");
            defaultMap.put(FessConfig.INDEX_USER_initial_password, "admin");
            defaultMap.put(FessConfig.INDEX_FIELD_favorite_count, "favorite_count");
            defaultMap.put(FessConfig.INDEX_FIELD_click_count, "click_count");
            defaultMap.put(FessConfig.INDEX_FIELD_config_id, "config_id");
            defaultMap.put(FessConfig.INDEX_FIELD_EXPIRES, "expires");
            defaultMap.put(FessConfig.INDEX_FIELD_URL, "url");
            defaultMap.put(FessConfig.INDEX_FIELD_doc_id, "doc_id");
            defaultMap.put(FessConfig.INDEX_FIELD_ID, "_id");
            defaultMap.put(FessConfig.INDEX_FIELD_VERSION, "_version");
            defaultMap.put(FessConfig.INDEX_FIELD_seq_no, "_seq_no");
            defaultMap.put(FessConfig.INDEX_FIELD_primary_term, "_primary_term");
            defaultMap.put(FessConfig.INDEX_FIELD_LANG, "lang");
            defaultMap.put(FessConfig.INDEX_FIELD_has_cache, "has_cache");
            defaultMap.put(FessConfig.INDEX_FIELD_last_modified, "last_modified");
            defaultMap.put(FessConfig.INDEX_FIELD_ANCHOR, "anchor");
            defaultMap.put(FessConfig.INDEX_FIELD_SEGMENT, "segment");
            defaultMap.put(FessConfig.INDEX_FIELD_ROLE, "role");
            defaultMap.put(FessConfig.INDEX_FIELD_BOOST, "boost");
            defaultMap.put(FessConfig.INDEX_FIELD_CREATED, "created");
            defaultMap.put(FessConfig.INDEX_FIELD_TIMESTAMP, "timestamp");
            defaultMap.put(FessConfig.INDEX_FIELD_LABEL, "label");
            defaultMap.put(FessConfig.INDEX_FIELD_MIMETYPE, "mimetype");
            defaultMap.put(FessConfig.INDEX_FIELD_parent_id, "parent_id");
            defaultMap.put(FessConfig.INDEX_FIELD_important_content, "important_content");
            defaultMap.put(FessConfig.INDEX_FIELD_CONTENT, "content");
            defaultMap.put(FessConfig.INDEX_FIELD_content_minhash_bits, "content_minhash_bits");
            defaultMap.put(FessConfig.INDEX_FIELD_CACHE, "cache");
            defaultMap.put(FessConfig.INDEX_FIELD_DIGEST, "digest");
            defaultMap.put(FessConfig.INDEX_FIELD_TITLE, "title");
            defaultMap.put(FessConfig.INDEX_FIELD_HOST, "host");
            defaultMap.put(FessConfig.INDEX_FIELD_SITE, "site");
            defaultMap.put(FessConfig.INDEX_FIELD_content_length, "content_length");
            defaultMap.put(FessConfig.INDEX_FIELD_FILETYPE, "filetype");
            defaultMap.put(FessConfig.INDEX_FIELD_FILENAME, "filename");
            defaultMap.put(FessConfig.INDEX_FIELD_THUMBNAIL, "thumbnail");
            defaultMap.put(FessConfig.INDEX_FIELD_virtual_host, "virtual_host");
            defaultMap.put(FessConfig.RESPONSE_FIELD_content_title, "content_title");
            defaultMap.put(FessConfig.RESPONSE_FIELD_content_description, "content_description");
            defaultMap.put(FessConfig.RESPONSE_FIELD_url_link, "url_link");
            defaultMap.put(FessConfig.RESPONSE_FIELD_site_path, "site_path");
            defaultMap.put(FessConfig.RESPONSE_MAX_TITLE_LENGTH, "50");
            defaultMap.put(FessConfig.RESPONSE_MAX_SITE_PATH_LENGTH, "100");
            defaultMap.put(FessConfig.RESPONSE_HIGHLIGHT_content_title_ENABLED, "true");
            defaultMap.put(FessConfig.RESPONSE_INLINE_MIMETYPES, "application/pdf,text/plain");
            defaultMap.put(FessConfig.INDEX_DOCUMENT_SEARCH_INDEX, "fess.search");
            defaultMap.put(FessConfig.INDEX_DOCUMENT_UPDATE_INDEX, "fess.update");
            defaultMap.put(FessConfig.INDEX_DOCUMENT_SUGGEST_INDEX, "fess");
            defaultMap.put(FessConfig.INDEX_DOCUMENT_CRAWLER_INDEX, "fess_crawler");
            defaultMap.put(FessConfig.INDEX_DOCUMENT_CRAWLER_QUEUE_number_of_shards, "10");
            defaultMap.put(FessConfig.INDEX_DOCUMENT_CRAWLER_DATA_number_of_shards, "10");
            defaultMap.put(FessConfig.INDEX_DOCUMENT_CRAWLER_FILTER_number_of_shards, "10");
            defaultMap.put(FessConfig.INDEX_DOCUMENT_CRAWLER_QUEUE_number_of_replicas, "1");
            defaultMap.put(FessConfig.INDEX_DOCUMENT_CRAWLER_DATA_number_of_replicas, "1");
            defaultMap.put(FessConfig.INDEX_DOCUMENT_CRAWLER_FILTER_number_of_replicas, "1");
            defaultMap.put(FessConfig.INDEX_CONFIG_INDEX, "fess_config");
            defaultMap.put(FessConfig.INDEX_USER_INDEX, "fess_user");
            defaultMap.put(FessConfig.INDEX_LOG_INDEX, "fess_log");
            defaultMap.put(FessConfig.INDEX_ADMIN_ARRAY_FIELDS, "lang,role,label,anchor,virtual_host");
            defaultMap.put(FessConfig.INDEX_ADMIN_DATE_FIELDS, "expires,created,timestamp,last_modified");
            defaultMap.put(FessConfig.INDEX_ADMIN_INTEGER_FIELDS, "");
            defaultMap.put(FessConfig.INDEX_ADMIN_LONG_FIELDS, "content_length,favorite_count,click_count");
            defaultMap.put(FessConfig.INDEX_ADMIN_FLOAT_FIELDS, "boost");
            defaultMap.put(FessConfig.INDEX_ADMIN_DOUBLE_FIELDS, "");
            defaultMap.put(FessConfig.INDEX_ADMIN_REQUIRED_FIELDS, "url,title,role,boost");
            defaultMap.put(FessConfig.INDEX_SEARCH_TIMEOUT, "3m");
            defaultMap.put(FessConfig.INDEX_SCROLL_SEARCH_TIMEOUT, "3m");
            defaultMap.put(FessConfig.INDEX_INDEX_TIMEOUT, "3m");
            defaultMap.put(FessConfig.INDEX_BULK_TIMEOUT, "3m");
            defaultMap.put(FessConfig.INDEX_DELETE_TIMEOUT, "3m");
            defaultMap.put(FessConfig.INDEX_HEALTH_TIMEOUT, "10m");
            defaultMap.put(FessConfig.INDEX_INDICES_TIMEOUT, "1m");
            defaultMap.put(FessConfig.INDEX_FILETYPE,
                    "text/html=html\napplication/msword=word\napplication/vnd.openxmlformats-officedocument.wordprocessingml.document=word\napplication/vnd.ms-excel=excel\napplication/vnd.ms-excel.sheet.2=excel\napplication/vnd.ms-excel.sheet.3=excel\napplication/vnd.ms-excel.sheet.4=excel\napplication/vnd.ms-excel.workspace.3=excel\napplication/vnd.ms-excel.workspace.4=excel\napplication/vnd.openxmlformats-officedocument.spreadsheetml.sheet=excel\napplication/vnd.ms-powerpoint=powerpoint\napplication/vnd.openxmlformats-officedocument.presentationml.presentation=powerpoint\napplication/vnd.oasis.opendocument.text=odt\napplication/vnd.oasis.opendocument.spreadsheet=ods\napplication/vnd.oasis.opendocument.presentation=odp\napplication/pdf=pdf\napplication/x-fictionbook+xml=fb2\napplication/e-pub+zip=epub\napplication/x-ibooks+zip=ibooks\ntext/plain=txt\napplication/rtf=rtf\napplication/vnd.ms-htmlhelp=chm\napplication/zip=zip\napplication/x-7z-comressed=7z\napplication/x-bzip=bz\napplication/x-bzip2=bz2\napplication/x-tar=tar\napplication/x-rar-compressed=rar\nvideo/3gp=3gp\nvideo/3g2=3g2\nvideo/x-msvideo=avi\nvideo/x-flv=flv\nvideo/mpeg=mpeg\nvideo/mp4=mp4\nvideo/ogv=ogv\nvideo/quicktime=qt\nvideo/x-m4v=m4v\naudio/x-aif=aif\naudio/midi=midi\naudio/mpga=mpga\naudio/mp4=mp4a\naudio/ogg=oga\naudio/x-wav=wav\nimage/webp=webp\nimage/bmp=bmp\nimage/x-icon=ico\nimage/x-icon=ico\nimage/png=png\nimage/svg+xml=svg\nimage/tiff=tiff\nimage/jpeg=jpg\n");
            defaultMap.put(FessConfig.INDEX_REINDEX_SIZE, "100");
            defaultMap.put(FessConfig.INDEX_REINDEX_BODY,
                    "{\"source\":{\"index\":\"__SOURCE_INDEX__\",\"size\":__SIZE__},\"dest\":{\"index\":\"__DEST_INDEX__\"},\"script\":{\"source\":\"__SCRIPT_SOURCE__\"}}");
            defaultMap.put(FessConfig.INDEX_REINDEX_requests_per_second, "adaptive");
            defaultMap.put(FessConfig.INDEX_REINDEX_REFRESH, "false");
            defaultMap.put(FessConfig.INDEX_REINDEX_TIMEOUT, "1m");
            defaultMap.put(FessConfig.INDEX_REINDEX_SCROLL, "5m");
            defaultMap.put(FessConfig.INDEX_REINDEX_max_docs, "");
            defaultMap.put(FessConfig.QUERY_MAX_LENGTH, "1000");
            defaultMap.put(FessConfig.QUERY_TIMEOUT, "10000");
            defaultMap.put(FessConfig.QUERY_TIMEOUT_LOGGING, "true");
            defaultMap.put(FessConfig.QUERY_TRACK_TOTAL_HITS, "10000");
            defaultMap.put(FessConfig.QUERY_GEO_FIELDS, "location");
            defaultMap.put(FessConfig.QUERY_BROWSER_LANG_PARAMETER_NAME, "browser_lang");
            defaultMap.put(FessConfig.QUERY_REPLACE_TERM_WITH_PREFIX_QUERY, "true");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_TERMINAL_CHARS,
                    "u0021u002Cu002Eu003Fu0589u061Fu06D4u0700u0701u0702u0964u104Au104Bu1362u1367u1368u166Eu1803u1809u203Cu203Du2047u2048u2049u3002uFE52uFE57uFF01uFF0EuFF1FuFF61");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_FRAGMENT_SIZE, "60");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_NUMBER_OF_FRAGMENTS, "2");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_TYPE, "fvh");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_TAG_PRE, "<strong>");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_TAG_POST, "</strong>");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_BOUNDARY_CHARS, "u0009u000Au0013u0020");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_BOUNDARY_MAX_SCAN, "20");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_BOUNDARY_SCANNER, "chars");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_ENCODER, "default");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_FORCE_SOURCE, "false");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_FRAGMENTER, "span");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_FRAGMENT_OFFSET, "-1");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_NO_MATCH_SIZE, "0");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_ORDER, "score");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_PHRASE_LIMIT, "256");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_CONTENT_DESCRIPTION_FIELDS, "hl_content,digest");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_BOUNDARY_POSITION_DETECT, "true");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_TEXT_FRAGMENT_TYPE, "query");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_TEXT_FRAGMENT_SIZE, "3");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_TEXT_FRAGMENT_PREFIX_LENGTH, "5");
            defaultMap.put(FessConfig.QUERY_HIGHLIGHT_TEXT_FRAGMENT_SUFFIX_LENGTH, "5");
            defaultMap.put(FessConfig.QUERY_MAX_SEARCH_RESULT_OFFSET, "100000");
            defaultMap.put(FessConfig.QUERY_ADDITIONAL_DEFAULT_FIELDS, "");
            defaultMap.put(FessConfig.QUERY_ADDITIONAL_RESPONSE_FIELDS, "");
            defaultMap.put(FessConfig.QUERY_ADDITIONAL_API_RESPONSE_FIELDS, "");
            defaultMap.put(FessConfig.QUERY_ADDITIONAL_SCROLL_RESPONSE_FIELDS, "");
            defaultMap.put(FessConfig.QUERY_ADDITIONAL_CACHE_RESPONSE_FIELDS, "");
            defaultMap.put(FessConfig.QUERY_ADDITIONAL_HIGHLIGHTED_FIELDS, "");
            defaultMap.put(FessConfig.QUERY_ADDITIONAL_SEARCH_FIELDS, "");
            defaultMap.put(FessConfig.QUERY_ADDITIONAL_FACET_FIELDS, "");
            defaultMap.put(FessConfig.QUERY_ADDITIONAL_SORT_FIELDS, "");
            defaultMap.put(FessConfig.QUERY_ADDITIONAL_ANALYZED_FIELDS, "");
            defaultMap.put(FessConfig.QUERY_ADDITIONAL_NOT_ANALYZED_FIELDS, "");
            defaultMap.put(FessConfig.QUERY_GSA_RESPONSE_FIELDS, "UE,U,T,RK,S,LANG");
            defaultMap.put(FessConfig.QUERY_GSA_DEFAULT_LANG, "en");
            defaultMap.put(FessConfig.QUERY_GSA_DEFAULT_SORT, "");
            defaultMap.put(FessConfig.QUERY_GSA_META_PREFIX, "MT_");
            defaultMap.put(FessConfig.QUERY_GSA_INDEX_FIELD_CHARSET, "charset");
            defaultMap.put(FessConfig.QUERY_GSA_INDEX_FIELD_content_type_, "content_type");
            defaultMap.put(FessConfig.QUERY_COLLAPSE_MAX_CONCURRENT_GROUP_RESULTS, "4");
            defaultMap.put(FessConfig.QUERY_COLLAPSE_INNER_HITS_NAME, "similar_docs");
            defaultMap.put(FessConfig.QUERY_COLLAPSE_INNER_HITS_SIZE, "0");
            defaultMap.put(FessConfig.QUERY_COLLAPSE_INNER_HITS_SORTS, "");
            defaultMap.put(FessConfig.QUERY_DEFAULT_LANGUAGES, "");
            defaultMap.put(FessConfig.QUERY_JSON_DEFAULT_PREFERENCE, "_query");
            defaultMap.put(FessConfig.QUERY_GSA_DEFAULT_PREFERENCE, "_query");
            defaultMap.put(FessConfig.QUERY_LANGUAGE_MAPPING,
                    "ar=ar\nbg=bg\nbn=bn\nca=ca\nckb-iq=ckb-iq\nckb_IQ=ckb-iq\ncs=cs\nda=da\nde=de\nel=el\nen=en\nen-ie=en-ie\nen_IE=en-ie\nes=es\net=et\neu=eu\nfa=fa\nfi=fi\nfr=fr\ngl=gl\ngu=gu\nhe=he\nhi=hi\nhr=hr\nhu=hu\nhy=hy\nid=id\nit=it\nja=ja\nko=ko\nlt=lt\nlv=lv\nmk=mk\nml=ml\nnl=nl\nno=no\npa=pa\npl=pl\npt=pt\npt-br=pt-br\npt_BR=pt-br\nro=ro\nru=ru\nsi=si\nsq=sq\nsv=sv\nta=ta\nte=te\nth=th\ntl=tl\ntr=tr\nuk=uk\nur=ur\nvi=vi\nzh-cn=zh-cn\nzh_CN=zh-cn\nzh-tw=zh-tw\nzh_TW=zh-tw\nzh=zh\n");
            defaultMap.put(FessConfig.QUERY_BOOST_TITLE, "0.5");
            defaultMap.put(FessConfig.QUERY_BOOST_TITLE_LANG, "1.0");
            defaultMap.put(FessConfig.QUERY_BOOST_CONTENT, "0.05");
            defaultMap.put(FessConfig.QUERY_BOOST_CONTENT_LANG, "0.1");
            defaultMap.put(FessConfig.QUERY_BOOST_important_content, "-1.0");
            defaultMap.put(FessConfig.QUERY_BOOST_important_content_LANG, "-1.0");
            defaultMap.put(FessConfig.QUERY_BOOST_FUZZY_MIN_LENGTH, "4");
            defaultMap.put(FessConfig.QUERY_BOOST_FUZZY_TITLE, "0.01");
            defaultMap.put(FessConfig.QUERY_BOOST_FUZZY_TITLE_FUZZINESS, "AUTO");
            defaultMap.put(FessConfig.QUERY_BOOST_FUZZY_TITLE_EXPANSIONS, "10");
            defaultMap.put(FessConfig.QUERY_BOOST_FUZZY_TITLE_prefix_length, "0");
            defaultMap.put(FessConfig.QUERY_BOOST_FUZZY_TITLE_TRANSPOSITIONS, "true");
            defaultMap.put(FessConfig.QUERY_BOOST_FUZZY_CONTENT, "0.005");
            defaultMap.put(FessConfig.QUERY_BOOST_FUZZY_CONTENT_FUZZINESS, "AUTO");
            defaultMap.put(FessConfig.QUERY_BOOST_FUZZY_CONTENT_EXPANSIONS, "10");
            defaultMap.put(FessConfig.QUERY_BOOST_FUZZY_CONTENT_prefix_length, "0");
            defaultMap.put(FessConfig.QUERY_BOOST_FUZZY_CONTENT_TRANSPOSITIONS, "true");
            defaultMap.put(FessConfig.QUERY_PREFIX_EXPANSIONS, "50");
            defaultMap.put(FessConfig.QUERY_PREFIX_SLOP, "0");
            defaultMap.put(FessConfig.QUERY_FUZZY_prefix_length, "0");
            defaultMap.put(FessConfig.QUERY_FUZZY_EXPANSIONS, "50");
            defaultMap.put(FessConfig.QUERY_FUZZY_TRANSPOSITIONS, "true");
            defaultMap.put(FessConfig.QUERY_FACET_FIELDS, "label");
            defaultMap.put(FessConfig.QUERY_FACET_FIELDS_SIZE, "100");
            defaultMap.put(FessConfig.QUERY_FACET_FIELDS_min_doc_count, "1");
            defaultMap.put(FessConfig.QUERY_FACET_FIELDS_SORT, "count.desc");
            defaultMap.put(FessConfig.QUERY_FACET_FIELDS_MISSING, "");
            defaultMap.put(FessConfig.QUERY_FACET_QUERIES,
                    "labels.facet_timestamp_title:labels.facet_timestamp_1day=timestamp:[now/d-1d TO *]\tlabels.facet_timestamp_1week=timestamp:[now/d-7d TO *]\tlabels.facet_timestamp_1month=timestamp:[now/d-1M TO *]\tlabels.facet_timestamp_1year=timestamp:[now/d-1y TO *]\nlabels.facet_contentLength_title:labels.facet_contentLength_10k=content_length:[0 TO 9999]\tlabels.facet_contentLength_10kto100k=content_length:[10000 TO 99999]\tlabels.facet_contentLength_100kto500k=content_length:[100000 TO 499999]\tlabels.facet_contentLength_500kto1m=content_length:[500000 TO 999999]\tlabels.facet_contentLength_1m=content_length:[1000000 TO *]\nlabels.facet_filetype_title:labels.facet_filetype_html=filetype:html\tlabels.facet_filetype_word=filetype:word\tlabels.facet_filetype_excel=filetype:excel\tlabels.facet_filetype_powerpoint=filetype:powerpoint\tlabels.facet_filetype_odt=filetype:odt\tlabels.facet_filetype_ods=filetype:ods\tlabels.facet_filetype_odp=filetype:odp\tlabels.facet_filetype_pdf=filetype:pdf\tlabels.facet_filetype_txt=filetype:txt\tlabels.facet_filetype_others=filetype:others\n");
            defaultMap.put(FessConfig.RANK_FUSION_window_size, "200");
            defaultMap.put(FessConfig.RANK_FUSION_rank_constant, "20");
            defaultMap.put(FessConfig.RANK_FUSION_THREADS, "-1");
            defaultMap.put(FessConfig.RANK_FUSION_score_field, "rf_score");
            defaultMap.put(FessConfig.SMB_ROLE_FROM_FILE, "true");
            defaultMap.put(FessConfig.SMB_AVAILABLE_SID_TYPES, "1,2,4:2,5:1");
            defaultMap.put(FessConfig.FILE_ROLE_FROM_FILE, "true");
            defaultMap.put(FessConfig.FTP_ROLE_FROM_FILE, "true");
            defaultMap.put(FessConfig.INDEX_BACKUP_TARGETS,
                    "fess_basic_config.bulk,fess_config.bulk,fess_user.bulk,system.properties,fess.json,doc.json");
            defaultMap.put(FessConfig.INDEX_BACKUP_LOG_TARGETS, "click_log.ndjson,favorite_log.ndjson,search_log.ndjson,user_info.ndjson");
            defaultMap.put(FessConfig.LOGGING_SEARCH_DOCS_ENABLED, "true");
            defaultMap.put(FessConfig.LOGGING_SEARCH_DOCS_FIELDS,
                    "filetype,created,click_count,title,doc_id,url,score,site,filename,host,digest,boost,mimetype,favorite_count,_id,lang,last_modified,content_length,timestamp");
            defaultMap.put(FessConfig.LOGGING_SEARCH_USE_LOGFILE, "true");
            defaultMap.put(FessConfig.LOGGING_APP_PACKAGES, "org.codelibs,org.dbflute,org.lastaflute");
            defaultMap.put(FessConfig.FORM_ADMIN_MAX_INPUT_SIZE, "10000");
            defaultMap.put(FessConfig.FORM_ADMIN_LABEL_IN_CONFIG_ENABLED, "false");
            defaultMap.put(FessConfig.FORM_ADMIN_DEFAULT_TEMPLATE_NAME, "__TEMPLATE__");
            defaultMap.put(FessConfig.OSDD_LINK_ENABLED, "true");
            defaultMap.put(FessConfig.CLIPBOARD_COPY_ICON_ENABLED, "true");
            defaultMap.put(FessConfig.AUTHENTICATION_ADMIN_USERS, "admin");
            defaultMap.put(FessConfig.AUTHENTICATION_ADMIN_ROLES, "admin");
            defaultMap.put(FessConfig.ROLE_SEARCH_DEFAULT_PERMISSIONS, "");
            defaultMap.put(FessConfig.ROLE_SEARCH_DEFAULT_DISPLAY_PERMISSIONS, "{role}guest");
            defaultMap.put(FessConfig.ROLE_SEARCH_GUEST_PERMISSIONS, "{role}guest");
            defaultMap.put(FessConfig.ROLE_SEARCH_USER_PREFIX, "1");
            defaultMap.put(FessConfig.ROLE_SEARCH_GROUP_PREFIX, "2");
            defaultMap.put(FessConfig.ROLE_SEARCH_ROLE_PREFIX, "R");
            defaultMap.put(FessConfig.ROLE_SEARCH_DENIED_PREFIX, "D");
            defaultMap.put(FessConfig.COOKIE_DEFAULT_PATH, "/");
            defaultMap.put(FessConfig.COOKIE_DEFAULT_EXPIRE, "3600");
            defaultMap.put(FessConfig.SESSION_TRACKING_MODES, "cookie");
            defaultMap.put(FessConfig.PAGING_PAGE_SIZE, "25");
            defaultMap.put(FessConfig.PAGING_PAGE_RANGE_SIZE, "5");
            defaultMap.put(FessConfig.PAGING_PAGE_RANGE_FILL_LIMIT, "true");
            defaultMap.put(FessConfig.PAGE_DOCBOOST_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_KEYMATCH_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_LABELTYPE_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_ROLETYPE_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_USER_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_ROLE_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_GROUP_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_CRAWLING_INFO_PARAM_MAX_FETCH_SIZE, "100");
            defaultMap.put(FessConfig.PAGE_CRAWLING_INFO_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_DATA_CONFIG_MAX_FETCH_SIZE, "100");
            defaultMap.put(FessConfig.PAGE_WEB_CONFIG_MAX_FETCH_SIZE, "100");
            defaultMap.put(FessConfig.PAGE_FILE_CONFIG_MAX_FETCH_SIZE, "100");
            defaultMap.put(FessConfig.PAGE_DUPLICATE_HOST_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_FAILURE_URL_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_FAVORITE_LOG_MAX_FETCH_SIZE, "100");
            defaultMap.put(FessConfig.PAGE_FILE_AUTH_MAX_FETCH_SIZE, "100");
            defaultMap.put(FessConfig.PAGE_WEB_AUTH_MAX_FETCH_SIZE, "100");
            defaultMap.put(FessConfig.PAGE_PATH_MAPPING_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_REQUEST_HEADER_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_SCHEDULED_JOB_MAX_FETCH_SIZE, "100");
            defaultMap.put(FessConfig.PAGE_ELEVATE_WORD_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_BAD_WORD_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_DICTIONARY_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_RELATEDCONTENT_MAX_FETCH_SIZE, "5000");
            defaultMap.put(FessConfig.PAGE_RELATEDQUERY_MAX_FETCH_SIZE, "5000");
            defaultMap.put(FessConfig.PAGE_THUMBNAIL_QUEUE_MAX_FETCH_SIZE, "100");
            defaultMap.put(FessConfig.PAGE_THUMBNAIL_PURGE_MAX_FETCH_SIZE, "100");
            defaultMap.put(FessConfig.PAGE_SCORE_BOOSTER_MAX_FETCH_SIZE, "1000");
            defaultMap.put(FessConfig.PAGE_SEARCHLOG_MAX_FETCH_SIZE, "10000");
            defaultMap.put(FessConfig.PAGE_SEARCHLIST_TRACK_TOTAL_HITS, "true");
            defaultMap.put(FessConfig.PAGING_SEARCH_PAGE_START, "0");
            defaultMap.put(FessConfig.PAGING_SEARCH_PAGE_SIZE, "10");
            defaultMap.put(FessConfig.PAGING_SEARCH_PAGE_MAX_SIZE, "100");
            defaultMap.put(FessConfig.SEARCHLOG_AGG_SHARD_SIZE, "-1");
            defaultMap.put(FessConfig.SEARCHLOG_REQUEST_HEADERS, "");
            defaultMap.put(FessConfig.THUMBNAIL_HTML_IMAGE_MIN_WIDTH, "100");
            defaultMap.put(FessConfig.THUMBNAIL_HTML_IMAGE_MIN_HEIGHT, "100");
            defaultMap.put(FessConfig.THUMBNAIL_HTML_IMAGE_MAX_ASPECT_RATIO, "3.0");
            defaultMap.put(FessConfig.THUMBNAIL_HTML_IMAGE_THUMBNAIL_WIDTH, "100");
            defaultMap.put(FessConfig.THUMBNAIL_HTML_IMAGE_THUMBNAIL_HEIGHT, "100");
            defaultMap.put(FessConfig.THUMBNAIL_HTML_IMAGE_FORMAT, "png");
            defaultMap.put(FessConfig.THUMBNAIL_HTML_IMAGE_XPATH, "//IMG");
            defaultMap.put(FessConfig.THUMBNAIL_HTML_IMAGE_EXCLUDE_EXTENSIONS, "svg,html,css,js");
            defaultMap.put(FessConfig.THUMBNAIL_GENERATOR_INTERVAL, "0");
            defaultMap.put(FessConfig.THUMBNAIL_GENERATOR_TARGETS, "all");
            defaultMap.put(FessConfig.THUMBNAIL_CRAWLER_ENABLED, "true");
            defaultMap.put(FessConfig.THUMBNAIL_SYSTEM_MONITOR_INTERVAL, "60");
            defaultMap.put(FessConfig.USER_CODE_REQUEST_PARAMETER, "userCode");
            defaultMap.put(FessConfig.USER_CODE_MIN_LENGTH, "20");
            defaultMap.put(FessConfig.USER_CODE_MAX_LENGTH, "100");
            defaultMap.put(FessConfig.USER_CODE_PATTERN, "[a-zA-Z0-9_]+");
            defaultMap.put(FessConfig.MAIL_FROM_NAME, "Administrator");
            defaultMap.put(FessConfig.MAIL_FROM_ADDRESS, "root@localhost");
            defaultMap.put(FessConfig.MAIL_HOSTNAME, "");
            defaultMap.put(FessConfig.SCHEDULER_TARGET_NAME, "");
            defaultMap.put(FessConfig.SCHEDULER_JOB_CLASS, "org.codelibs.fess.app.job.ScriptExecutorJob");
            defaultMap.put(FessConfig.SCHEDULER_CONCURRENT_EXEC_MODE, "QUIT");
            defaultMap.put(FessConfig.SCHEDULER_MONITOR_INTERVAL, "30");
            defaultMap.put(FessConfig.ONLINE_HELP_BASE_LINK, "https://fess.codelibs.org/{lang}/{version}/admin/");
            defaultMap.put(FessConfig.ONLINE_HELP_INSTALLATION, "https://fess.codelibs.org/{lang}/{version}/install/install.html");
            defaultMap.put(FessConfig.ONLINE_HELP_EOL, "https://fess.codelibs.org/{lang}/eol.html");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_FAILUREURL, "failureurl");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_ELEVATEWORD, "elevateword");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_REQHEADER, "reqheader");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_DICT_SYNONYM, "synonym");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_DICT, "dict");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_DICT_KUROMOJI, "kuromoji");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_DICT_PROTWORDS, "protwords");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_DICT_STOPWORDS, "stopwords");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_DICT_STEMMEROVERRIDE, "stemmeroverride");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_DICT_MAPPING, "mapping");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_WEBCONFIG, "webconfig");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_SEARCHLIST, "searchlist");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_LOG, "log");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_GENERAL, "general");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_ROLE, "role");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_JOBLOG, "joblog");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_KEYMATCH, "keymatch");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_RELATEDQUERY, "relatedquery");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_RELATEDCONTENT, "relatedcontent");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_WIZARD, "wizard");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_BADWORD, "badword");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_PATHMAP, "pathmap");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_BOOSTDOC, "boostdoc");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_DATACONFIG, "dataconfig");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_SYSTEMINFO, "systeminfo");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_USER, "user");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_GROUP, "group");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_DESIGN, "design");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_DASHBOARD, "dashboard");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_WEBAUTH, "webauth");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_FILECONFIG, "fileconfig");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_FILEAUTH, "fileauth");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_LABELTYPE, "labeltype");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_DUPLICATEHOST, "duplicatehost");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_SCHEDULER, "scheduler");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_CRAWLINGINFO, "crawlinginfo");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_BACKUP, "backup");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_UPGRADE, "upgrade");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_ESREQ, "esreq");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_ACCESSTOKEN, "accesstoken");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_SUGGEST, "suggest");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_SEARCHLOG, "searchlog");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_MAINTENANCE, "maintenance");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_PLUGIN, "plugin");
            defaultMap.put(FessConfig.ONLINE_HELP_NAME_STORAGE, "storage");
            defaultMap.put(FessConfig.ONLINE_HELP_SUPPORTED_LANGS, "ja");
            defaultMap.put(FessConfig.FORUM_LINK, "https://discuss.codelibs.org/c/Fess{lang}/");
            defaultMap.put(FessConfig.FORUM_SUPPORTED_LANGS, "en,ja");
            defaultMap.put(FessConfig.SUGGEST_POPULAR_WORD_SEED, "0");
            defaultMap.put(FessConfig.SUGGEST_POPULAR_WORD_TAGS, "");
            defaultMap.put(FessConfig.SUGGEST_POPULAR_WORD_FIELDS, "");
            defaultMap.put(FessConfig.SUGGEST_POPULAR_WORD_EXCLUDES, "");
            defaultMap.put(FessConfig.SUGGEST_POPULAR_WORD_SIZE, "10");
            defaultMap.put(FessConfig.SUGGEST_POPULAR_WORD_WINDOW_SIZE, "30");
            defaultMap.put(FessConfig.SUGGEST_POPULAR_WORD_QUERY_FREQ, "10");
            defaultMap.put(FessConfig.SUGGEST_MIN_HIT_COUNT, "1");
            defaultMap.put(FessConfig.SUGGEST_FIELD_CONTENTS, "_default");
            defaultMap.put(FessConfig.SUGGEST_FIELD_TAGS, "label");
            defaultMap.put(FessConfig.SUGGEST_FIELD_ROLES, "role");
            defaultMap.put(FessConfig.SUGGEST_FIELD_INDEX_CONTENTS, "content,title");
            defaultMap.put(FessConfig.SUGGEST_UPDATE_REQUEST_INTERVAL, "0");
            defaultMap.put(FessConfig.SUGGEST_UPDATE_DOC_PER_REQUEST, "2");
            defaultMap.put(FessConfig.SUGGEST_UPDATE_CONTENTS_LIMIT_NUM_PERCENTAGE, "50%");
            defaultMap.put(FessConfig.SUGGEST_UPDATE_CONTENTS_LIMIT_NUM, "10000");
            defaultMap.put(FessConfig.SUGGEST_UPDATE_CONTENTS_LIMIT_DOC_SIZE, "50000");
            defaultMap.put(FessConfig.SUGGEST_SOURCE_READER_SCROLL_SIZE, "1");
            defaultMap.put(FessConfig.SUGGEST_POPULAR_WORD_CACHE_SIZE, "1000");
            defaultMap.put(FessConfig.SUGGEST_POPULAR_WORD_CACHE_EXPIRE, "60");
            defaultMap.put(FessConfig.SUGGEST_SEARCH_LOG_PERMISSIONS, "{user}guest,{role}guest");
            defaultMap.put(FessConfig.SUGGEST_SYSTEM_MONITOR_INTERVAL, "60");
            defaultMap.put(FessConfig.LDAP_ADMIN_ENABLED, "false");
            defaultMap.put(FessConfig.LDAP_ADMIN_USER_FILTER, "uid=%s");
            defaultMap.put(FessConfig.LDAP_ADMIN_USER_BASE_DN, "ou=People,dc=fess,dc=codelibs,dc=org");
            defaultMap.put(FessConfig.LDAP_ADMIN_USER_OBJECT_CLASSES, "organizationalPerson,top,person,inetOrgPerson");
            defaultMap.put(FessConfig.LDAP_ADMIN_ROLE_FILTER, "cn=%s");
            defaultMap.put(FessConfig.LDAP_ADMIN_ROLE_BASE_DN, "ou=Role,dc=fess,dc=codelibs,dc=org");
            defaultMap.put(FessConfig.LDAP_ADMIN_ROLE_OBJECT_CLASSES, "groupOfNames");
            defaultMap.put(FessConfig.LDAP_ADMIN_GROUP_FILTER, "cn=%s");
            defaultMap.put(FessConfig.LDAP_ADMIN_GROUP_BASE_DN, "ou=Group,dc=fess,dc=codelibs,dc=org");
            defaultMap.put(FessConfig.LDAP_ADMIN_GROUP_OBJECT_CLASSES, "groupOfNames");
            defaultMap.put(FessConfig.LDAP_ADMIN_SYNC_PASSWORD, "true");
            defaultMap.put(FessConfig.LDAP_AUTH_VALIDATION, "true");
            defaultMap.put(FessConfig.LDAP_MAX_USERNAME_LENGTH, "-1");
            defaultMap.put(FessConfig.LDAP_IGNORE_NETBIOS_NAME, "true");
            defaultMap.put(FessConfig.LDAP_GROUP_NAME_WITH_UNDERSCORES, "false");
            defaultMap.put(FessConfig.LDAP_LOWERCASE_PERMISSION_NAME, "false");
            defaultMap.put(FessConfig.LDAP_ALLOW_EMPTY_PERMISSION, "true");
            defaultMap.put(FessConfig.LDAP_ROLE_SEARCH_USER_ENABLED, "true");
            defaultMap.put(FessConfig.LDAP_ROLE_SEARCH_GROUP_ENABLED, "true");
            defaultMap.put(FessConfig.LDAP_ROLE_SEARCH_ROLE_ENABLED, "true");
            defaultMap.put(FessConfig.LDAP_ATTR_SURNAME, "sn");
            defaultMap.put(FessConfig.LDAP_ATTR_GIVEN_NAME, "givenName");
            defaultMap.put(FessConfig.LDAP_ATTR_EMPLOYEE_NUMBER, "employeeNumber");
            defaultMap.put(FessConfig.LDAP_ATTR_MAIL, "mail");
            defaultMap.put(FessConfig.LDAP_ATTR_TELEPHONE_NUMBER, "telephoneNumber");
            defaultMap.put(FessConfig.LDAP_ATTR_HOME_PHONE, "homePhone");
            defaultMap.put(FessConfig.LDAP_ATTR_HOME_POSTAL_ADDRESS, "homePostalAddress");
            defaultMap.put(FessConfig.LDAP_ATTR_LABELED_U_R_I, "labeledURI");
            defaultMap.put(FessConfig.LDAP_ATTR_ROOM_NUMBER, "roomNumber");
            defaultMap.put(FessConfig.LDAP_ATTR_DESCRIPTION, "description");
            defaultMap.put(FessConfig.LDAP_ATTR_TITLE, "title");
            defaultMap.put(FessConfig.LDAP_ATTR_PAGER, "pager");
            defaultMap.put(FessConfig.LDAP_ATTR_STREET, "street");
            defaultMap.put(FessConfig.LDAP_ATTR_POSTAL_CODE, "postalCode");
            defaultMap.put(FessConfig.LDAP_ATTR_PHYSICAL_DELIVERY_OFFICE_NAME, "physicalDeliveryOfficeName");
            defaultMap.put(FessConfig.LDAP_ATTR_DESTINATION_INDICATOR, "destinationIndicator");
            defaultMap.put(FessConfig.LDAP_ATTR_INTERNATIONALI_S_D_N_NUMBER, "internationaliSDNNumber");
            defaultMap.put(FessConfig.LDAP_ATTR_STATE, "st");
            defaultMap.put(FessConfig.LDAP_ATTR_EMPLOYEE_TYPE, "employeeType");
            defaultMap.put(FessConfig.LDAP_ATTR_FACSIMILE_TELEPHONE_NUMBER, "facsimileTelephoneNumber");
            defaultMap.put(FessConfig.LDAP_ATTR_POST_OFFICE_BOX, "postOfficeBox");
            defaultMap.put(FessConfig.LDAP_ATTR_INITIALS, "initials");
            defaultMap.put(FessConfig.LDAP_ATTR_CAR_LICENSE, "carLicense");
            defaultMap.put(FessConfig.LDAP_ATTR_MOBILE, "mobile");
            defaultMap.put(FessConfig.LDAP_ATTR_POSTAL_ADDRESS, "postalAddress");
            defaultMap.put(FessConfig.LDAP_ATTR_CITY, "l");
            defaultMap.put(FessConfig.LDAP_ATTR_TELETEX_TERMINAL_IDENTIFIER, "teletexTerminalIdentifier");
            defaultMap.put(FessConfig.LDAP_ATTR_X121_ADDRESS, "x121Address");
            defaultMap.put(FessConfig.LDAP_ATTR_BUSINESS_CATEGORY, "businessCategory");
            defaultMap.put(FessConfig.LDAP_ATTR_REGISTERED_ADDRESS, "registeredAddress");
            defaultMap.put(FessConfig.LDAP_ATTR_DISPLAY_NAME, "displayName");
            defaultMap.put(FessConfig.LDAP_ATTR_PREFERRED_LANGUAGE, "preferredLanguage");
            defaultMap.put(FessConfig.LDAP_ATTR_DEPARTMENT_NUMBER, "departmentNumber");
            defaultMap.put(FessConfig.LDAP_ATTR_UID_NUMBER, "uidNumber");
            defaultMap.put(FessConfig.LDAP_ATTR_GID_NUMBER, "gidNumber");
            defaultMap.put(FessConfig.LDAP_ATTR_HOME_DIRECTORY, "homeDirectory");
            defaultMap.put(FessConfig.PLUGIN_REPOSITORIES,
                    "https://repo.maven.apache.org/maven2/org/codelibs/fess/,https://fess.codelibs.org/plugin/artifacts.yaml");
            defaultMap.put(FessConfig.PLUGIN_VERSION_FILTER, "");
            defaultMap.put(FessConfig.STORAGE_MAX_ITEMS_IN_PAGE, "1000");
            defaultMap.put(FessConfig.PASSWORD_INVALID_ADMIN_PASSWORDS, "admin");
            defaultMap.put(FessConfig.lasta_di_SMART_DEPLOY_MODE, "warm");
            defaultMap.put(FessConfig.DEVELOPMENT_HERE, "true");
            defaultMap.put(FessConfig.ENVIRONMENT_TITLE, "Local Development");
            defaultMap.put(FessConfig.FRAMEWORK_DEBUG, "false");
            defaultMap.put(FessConfig.TIME_ADJUST_TIME_MILLIS, "0");
            defaultMap.put(FessConfig.MAIL_SEND_MOCK, "true");
            defaultMap.put(FessConfig.MAIL_SMTP_SERVER_MAIN_HOST_AND_PORT, "localhost:25");
            defaultMap.put(FessConfig.MAIL_SUBJECT_TEST_PREFIX, "[Test]");
            defaultMap.put(FessConfig.MAIL_RETURN_PATH, "root@localhost");
            return defaultMap;
        }
    }
}
