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
package org.codelibs.fess.helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Base64Util;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.DataConfigService;
import org.codelibs.fess.app.service.FileConfigService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.entity.FacetQueryView;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigType;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.UserAgentHelper.UserAgentType;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.ResourceUtil;
import org.lastaflute.taglib.function.LaFunctions;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaResponseUtil;
import org.lastaflute.web.util.LaServletContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.ibm.icu.text.SimpleDateFormat;

public class ViewHelper {

    private static final String HL_CACHE = "hl_cache";

    private static final String QUERIES = "queries";

    private static final String CACHE_MSG = "cache_msg";

    private static final Pattern LOCAL_PATH_PATTERN = Pattern.compile("^file:/+[a-zA-Z]:");

    private static final Pattern SHARED_FOLDER_PATTERN = Pattern.compile("^file:/+[^/]\\.");

    private static final Logger logger = LoggerFactory.getLogger(ViewHelper.class);

    @Resource
    protected DynamicProperties systemProperties;

    @Resource
    protected PathMappingHelper pathMappingHelper;

    @Resource
    protected UserAgentHelper userAgentHelper;

    public int titleLength = 50;

    public int sitePathLength = 50;

    public boolean encodeUrlLink = false;

    public String urlLinkEncoding = Constants.UTF_8;

    public String[] highlightedFields = new String[] { "hl_content", "digest" };

    public String originalHighlightTagPre = "<em>";

    public String originalHighlightTagPost = "</em>";

    public String highlightTagPre = "<strong>";

    public String highlightTagPost = "</strong>";

    protected boolean useSession = true;

    private final Map<String, String> pageCacheMap = new ConcurrentHashMap<>();

    private final Map<String, String> initFacetParamMap = new HashMap<>();

    private final Map<String, String> initGeoParamMap = new HashMap<>();

    private final List<FacetQueryView> facetQueryViewList = new ArrayList<>();

    public String cacheTemplateName = "cache";

    private String escapedHighlightPre = null;

    private String escapedHighlightPost = null;

    @PostConstruct
    public void init() {
        escapedHighlightPre = LaFunctions.h(originalHighlightTagPre);
        escapedHighlightPost = LaFunctions.h(originalHighlightTagPost);
    }

    public String getContentTitle(final Map<String, Object> document) {
        final int size = titleLength;
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        String title = DocumentUtil.getValue(document, fessConfig.getIndexFieldTitle(), String.class);
        if (StringUtil.isBlank(title)) {
            title = DocumentUtil.getValue(document, fessConfig.getIndexFieldFilename(), String.class);
            if (StringUtil.isBlank(title)) {
                title = DocumentUtil.getValue(document, fessConfig.getIndexFieldUrl(), String.class);
            }
        }
        return StringUtils.abbreviate(title, size);
    }

    public String getContentDescription(final Map<String, Object> document) {
        for (final String field : highlightedFields) {
            final String text = DocumentUtil.getValue(document, field, String.class);
            if (StringUtil.isNotBlank(text)) {
                return escapeHighlight(text);
            }
        }

        return StringUtil.EMPTY;
    }

    protected String escapeHighlight(final String text) {
        return LaFunctions.h(text).replaceAll(escapedHighlightPre, highlightTagPre).replaceAll(escapedHighlightPost, highlightTagPost);
    }

    protected String removeHighlightTag(final String str) {
        return str.replaceAll(originalHighlightTagPre, StringUtil.EMPTY).replaceAll(originalHighlightTagPost, StringUtil.EMPTY);
    }

    public String getUrlLink(final Map<String, Object> document) {
        // file protocol
        String url = DocumentUtil.getValue(document, "url", String.class);

        if (url == null) {
            // TODO should redirect to a invalid page?
            return "#";
        }

        final boolean isFileUrl = url.startsWith("smb:") || url.startsWith("ftp:");

        // replacing url with mapping data
        url = pathMappingHelper.replaceUrl(url);

        if (url.startsWith("smb:")) {
            url = url.replace("smb:", "file:");
        } else if (url.startsWith("ftp:")) {
            url = url.replace("ftp:", "file:");
        }

        if (url.startsWith("http:") && isFileUrl) {
            final StringBuilder buf = new StringBuilder(url.length() + 100);
            for (final char c : url.toCharArray()) {
                if (CharUtil.isUrlChar(c)) {
                    buf.append(c);
                } else {
                    try {
                        buf.append(URLEncoder.encode(String.valueOf(c), urlLinkEncoding));
                    } catch (final UnsupportedEncodingException e) {
                        // NOP
                    }
                }
            }
            url = buf.toString();
        } else if (url.startsWith("file:")) {

            final int pos = url.indexOf(':', 5);
            final boolean isLocalFile = pos > 0 && pos < 12;

            final UserAgentType ua = userAgentHelper.getUserAgentType();
            switch (ua) {
            case IE:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", systemProperties.getProperty("file.protocol.winlocal.ie", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", systemProperties.getProperty("file.protocol.ie", "file://"));
                }
                break;
            case FIREFOX:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", systemProperties.getProperty("file.protocol.winlocal.firefox", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", systemProperties.getProperty("file.protocol.firefox", "file://///"));
                }
                break;
            case CHROME:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", systemProperties.getProperty("file.protocol.winlocal.chrome", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", systemProperties.getProperty("file.protocol.chrome", "file://"));
                }
                break;
            case SAFARI:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", systemProperties.getProperty("file.protocol.winlocal.safari", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", systemProperties.getProperty("file.protocol.safari", "file:////"));
                }
                break;
            case OPERA:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", systemProperties.getProperty("file.protocol.winlocal.opera", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", systemProperties.getProperty("file.protocol.opera", "file://"));
                }
                break;
            default:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", systemProperties.getProperty("file.protocol.winlocal.other", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", systemProperties.getProperty("file.protocol.other", "file://"));
                }
                break;
            }

            if (encodeUrlLink) {
                return appendQueryParameter(document, url);
            } else {
                final String urlLink = appendQueryParameter(document, url).replace("+", "%2B");
                final String[] values = urlLink.split("#");
                final StringBuilder buf = new StringBuilder(urlLink.length());
                try {
                    buf.append(URLDecoder.decode(values[0], urlLinkEncoding));
                    for (int i = 1; i < values.length - 1; i++) {
                        buf.append('#').append(URLDecoder.decode(values[i], urlLinkEncoding));
                    }
                } catch (final Exception e) {
                    throw new FessSystemException("Unsupported encoding: " + urlLinkEncoding, e);
                }
                if (values.length > 1) {
                    buf.append('#').append(values[values.length - 1]);
                }
                return buf.toString();
            }
        }

        return appendQueryParameter(document, url);
    }

    protected String appendQueryParameter(final Map<String, Object> document, final String url) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.isAppendQueryParameter()) {
            final String mimetype = DocumentUtil.getValue(document, fessConfig.getIndexFieldMimetype(), String.class);
            if (StringUtil.isNotBlank(mimetype)) {
                if ("application/pdf".equals(mimetype)) {
                    return appendPDFSearchWord(url);
                } else {
                    // TODO others..
                    return url;
                }
            }
        }
        return url;
    }

    protected String appendPDFSearchWord(final String url) {
        final String queries = (String) LaRequestUtil.getRequest().getAttribute(Constants.REQUEST_QUERIES);
        if (queries != null) {
            try {
                final StringBuilder buf = new StringBuilder(url.length() + 100);
                buf.append(url).append("#search=%22");
                buf.append(URLEncoder.encode(queries.trim(), Constants.UTF_8));
                buf.append("%22");
                return buf.toString();
            } catch (final UnsupportedEncodingException e) {
                logger.warn("Unsupported encoding.", e);
            }
        }
        return url;
    }

    public String getPagePath(final String page) {
        final Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        final String lang = locale.getLanguage();
        final String country = locale.getCountry();

        final String pathLC = getLocalizedPagePath(page, lang, country);
        final String pLC = pageCacheMap.get(pathLC);
        if (pLC != null) {
            return pLC;
        }
        if (existsPage(pathLC)) {
            pageCacheMap.put(pathLC, pathLC);
            return pathLC;
        }

        final String pathL = getLocalizedPagePath(page, lang, null);
        final String pL = pageCacheMap.get(pathL);
        if (pL != null) {
            return pL;
        }
        if (existsPage(pathL)) {
            pageCacheMap.put(pathLC, pathL);
            return pathL;
        }

        final String path = getLocalizedPagePath(page, null, null);
        final String p = pageCacheMap.get(path);
        if (p != null) {
            return p;
        }
        if (existsPage(path)) {
            pageCacheMap.put(pathLC, path);
            return path;
        }

        return "index.jsp";
    }

    private String getLocalizedPagePath(final String page, final String lang, final String country) {
        final StringBuilder buf = new StringBuilder(100);
        buf.append("/WEB-INF/view/").append(page);
        if (StringUtil.isNotBlank(lang)) {
            buf.append('_').append(lang);
            if (StringUtil.isNotBlank(country)) {
                buf.append('_').append(country);
            }
        }
        buf.append(".jsp");
        return buf.toString();
    }

    private boolean existsPage(final String path) {
        final String realPath = LaServletContextUtil.getServletContext().getRealPath(path);
        final File file = new File(realPath);
        return file.isFile();
    }

    public String createCacheContent(final Map<String, Object> doc, final String[] queries) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final FileTemplateLoader loader = new FileTemplateLoader(ResourceUtil.getViewTemplatePath().toFile());
        final Handlebars handlebars = new Handlebars(loader);

        Locale locale = ComponentUtil.getRequestManager().getUserLocale();
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
        if (url == null) {
            url = ComponentUtil.getMessageManager().getMessage(locale, "labels.search_unknown");
        }
        doc.put(fessConfig.getResponseFieldUrlLink(), getUrlLink(doc));
        String createdStr;
        final Date created = DocumentUtil.getValue(doc, fessConfig.getIndexFieldCreated(), Date.class);
        if (created != null) {
            final SimpleDateFormat sdf = new SimpleDateFormat(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            createdStr = sdf.format(created);
        } else {
            createdStr = ComponentUtil.getMessageManager().getMessage(locale, "labels.search_unknown");
        }
        doc.put(CACHE_MSG, ComponentUtil.getMessageManager()
                .getMessage(locale, "labels.search_cache_msg", new Object[] { url, createdStr }));

        doc.put(QUERIES, queries);

        String cache = DocumentUtil.getValue(doc, fessConfig.getIndexFieldCache(), String.class);
        if (cache != null) {
            final String mimetype = DocumentUtil.getValue(doc, fessConfig.getIndexFieldMimetype(), String.class);
            if (!ComponentUtil.getFessConfig().isHtmlMimetypeForCache(mimetype)) {
                cache = StringEscapeUtils.escapeHtml4(cache);
            }
            cache = pathMappingHelper.replaceUrls(cache);
            if (queries != null && queries.length > 0) {
                doc.put(HL_CACHE, replaceHighlightQueries(cache, queries));
            } else {
                doc.put(HL_CACHE, cache);
            }
        } else {
            doc.put(fessConfig.getIndexFieldCache(), StringUtil.EMPTY);
            doc.put(HL_CACHE, StringUtil.EMPTY);
        }

        try {
            final Template template = handlebars.compile(cacheTemplateName);
            final Context hbsContext = Context.newContext(doc);
            return template.apply(hbsContext);
        } catch (final Exception e) {
            logger.warn("Failed to create a cache response.", e);
        }

        return null;
    }

    protected String replaceHighlightQueries(final String cache, final String[] queries) {
        final StringBuffer buf = new StringBuffer(cache.length() + 100);
        final StringBuffer segBuf = new StringBuffer(1000);
        final Pattern p = Pattern.compile("<[^>]+>");
        final Matcher m = p.matcher(cache);
        final String[] regexQueries = new String[queries.length];
        final String[] hlQueries = new String[queries.length];
        for (int i = 0; i < queries.length; i++) {
            regexQueries[i] = Pattern.quote(queries[i]);
            hlQueries[i] = highlightTagPre + queries[i] + highlightTagPost;
        }
        while (m.find()) {
            segBuf.setLength(0);
            m.appendReplacement(segBuf, StringUtil.EMPTY);
            String segment = segBuf.toString();
            for (int i = 0; i < queries.length; i++) {
                segment = Pattern.compile(regexQueries[i], Pattern.CASE_INSENSITIVE).matcher(segment).replaceAll(hlQueries[i]);
            }
            buf.append(segment);
            buf.append(m.group(0));
        }
        segBuf.setLength(0);
        m.appendTail(segBuf);
        String segment = segBuf.toString();
        for (int i = 0; i < queries.length; i++) {
            segment = Pattern.compile(regexQueries[i], Pattern.CASE_INSENSITIVE).matcher(segment).replaceAll(hlQueries[i]);
        }
        buf.append(segment);
        return buf.toString();
    }

    public Object getSitePath(final Map<String, Object> docMap) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final Object urlLink = docMap.get(fessConfig.getResponseFieldUrlLink());
        if (urlLink != null) {
            final String returnUrl;
            final String url = urlLink.toString();
            if (LOCAL_PATH_PATTERN.matcher(url).find() || SHARED_FOLDER_PATTERN.matcher(url).find()) {
                returnUrl = url.replaceFirst("^file:/+", "");
            } else if (url.startsWith("file:")) {
                returnUrl = url.replaceFirst("^file:/+", "/");
            } else {
                returnUrl = url.replaceFirst("^[a-zA-Z0-9]*:/+", "");
            }
            return StringUtils.abbreviate(returnUrl, sitePathLength);
        }
        return null;
    }

    public StreamResponse asContentResponse(final Map<String, Object> doc) {
        if (logger.isDebugEnabled()) {
            logger.debug("writing the content of: " + doc);
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        final String configId = DocumentUtil.getValue(doc, fessConfig.getIndexFieldConfigId(), String.class);
        if (configId == null) {
            throw new FessSystemException("configId is null.");
        }
        if (configId.length() < 2) {
            throw new FessSystemException("Invalid configId: " + configId);
        }
        final ConfigType configType = crawlingConfigHelper.getConfigType(configId);
        CrawlingConfig config = null;
        if (logger.isDebugEnabled()) {
            logger.debug("configType: " + configType + ", configId: " + configId);
        }
        if (ConfigType.WEB == configType) {
            final WebConfigService webConfigService = ComponentUtil.getComponent(WebConfigService.class);
            config = webConfigService.getWebConfig(crawlingConfigHelper.getId(configId)).get();
        } else if (ConfigType.FILE == configType) {
            final FileConfigService fileConfigService = ComponentUtil.getComponent(FileConfigService.class);
            config = fileConfigService.getFileConfig(crawlingConfigHelper.getId(configId)).get();
        } else if (ConfigType.DATA == configType) {
            final DataConfigService dataConfigService = ComponentUtil.getComponent(DataConfigService.class);
            config = dataConfigService.getDataConfig(crawlingConfigHelper.getId(configId)).get();
        }
        if (config == null) {
            throw new FessSystemException("No crawlingConfig: " + configId);
        }
        final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
        final CrawlerClientFactory crawlerClientFactory = ComponentUtil.getComponent(CrawlerClientFactory.class);
        config.initializeClientFactory(crawlerClientFactory);
        final CrawlerClient client = crawlerClientFactory.getClient(url);
        if (client == null) {
            throw new FessSystemException("No CrawlerClient: " + configId + ", url: " + url);
        }
        return writeContent(configId, url, client);
    }

    protected StreamResponse writeContent(final String configId, final String url, final CrawlerClient client) {
        final ResponseData responseData = client.execute(RequestDataBuilder.newRequestData().get().url(url).build());
        final StreamResponse response = new StreamResponse(StringUtil.EMPTY);
        writeFileName(response, responseData);
        writeContentType(response, responseData);
        writeNoCache(response, responseData);
        if (responseData.getHttpStatusCode() == 404) {
            response.httpStatus(responseData.getHttpStatusCode());
            return response;
        }
        response.stream(out -> {
            try (final InputStream is = new BufferedInputStream(responseData.getResponseBody())) {
                out.write(is);
            } catch (final IOException e) {
                if (!(e.getCause() instanceof ClientAbortException)) {
                    throw new FessSystemException("Failed to write a content. configId: " + configId + ", url: " + url, e);
                }
            } finally {
                responseData.close();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Finished to write " + url);
            }
        });
        return response;
    }

    protected void writeNoCache(final StreamResponse response, final ResponseData responseData) {
        response.header("Pragma", "no-cache");
        response.header("Cache-Control", "no-cache");
        response.header("Expires", "Thu, 01 Dec 1994 16:00:00 GMT");
    }

    protected void writeFileName(final StreamResponse response, final ResponseData responseData) {
        final UserAgentHelper userAgentHelper = ComponentUtil.getUserAgentHelper();
        final UserAgentType userAgentType = userAgentHelper.getUserAgentType();
        String charset = responseData.getCharSet();
        if (charset == null) {
            charset = Constants.UTF_8;
        }
        final String name;
        final String url = responseData.getUrl();
        final int pos = url.lastIndexOf('/');
        try {
            if (pos >= 0 && pos + 1 < url.length()) {
                name = URLDecoder.decode(url.substring(pos + 1), charset);
            } else {
                name = URLDecoder.decode(url, charset);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("userAgentType: " + userAgentType + ", charset: " + charset + ", name: " + name);
            }

            switch (userAgentType) {
            case IE:
                response.header("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(name, Constants.UTF_8) + "\"");
                break;
            case OPERA:
                response.header("Content-Disposition", "attachment; filename*=utf-8'ja'" + URLEncoder.encode(name, Constants.UTF_8));
                break;
            case SAFARI:
                response.header("Content-Disposition", "attachment; filename=\"" + name + "\"");
                break;
            case CHROME:
            case FIREFOX:
            case OTHER:
            default:
                response.header("Content-Disposition",
                        "attachment; filename=\"=?utf-8?B?" + Base64Util.encode(name.getBytes(Constants.UTF_8)) + "?=\"");
                break;
            }
        } catch (final Exception e) {
            logger.warn("Failed to write a filename: " + responseData, e);
        }
    }

    protected void writeContentType(final StreamResponse response, final ResponseData responseData) {
        final String mimeType = responseData.getMimeType();
        if (logger.isDebugEnabled()) {
            logger.debug("mimeType: " + mimeType);
        }
        if (mimeType == null) {
            response.contentTypeOctetStream();
            return;
        }
        if (mimeType.startsWith("text/")) {
            final String charset = LaResponseUtil.getResponse().getCharacterEncoding();
            if (charset != null) {
                response.contentType(mimeType + "; charset=" + charset);
                return;
            }
        }
        response.contentType(mimeType);
    }

    public String getClientIp(final HttpServletRequest request) {
        final String value = request.getHeader("x-forwarded-for");
        if (StringUtil.isNotBlank(value)) {
            return value;
        } else {
            return request.getRemoteAddr();
        }
    }

    public boolean isUseSession() {
        return useSession;
    }

    public void setUseSession(final boolean useSession) {
        this.useSession = useSession;
    }

    public void addInitFacetParam(final String key, final String value) {
        initFacetParamMap.put(value, key);
    }

    public Map<String, String> getInitFacetParamMap() {
        return initFacetParamMap;
    }

    public void addInitGeoParam(final String key, final String value) {
        initGeoParamMap.put(value, key);
    }

    public Map<String, String> getInitGeoParamMap() {
        return initGeoParamMap;
    }

    public void addFacetQueryView(final FacetQueryView facetQueryView) {
        facetQueryViewList.add(facetQueryView);
    }

    public List<FacetQueryView> getFacetQueryViewList() {
        return facetQueryViewList;
    }

}
