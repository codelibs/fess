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
package org.codelibs.fess.helper;

import static org.codelibs.core.stream.StreamUtil.split;

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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.app.web.base.SearchForm;
import org.codelibs.fess.app.web.base.login.FessLoginAssist;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.CrawlerClient;
import org.codelibs.fess.crawler.client.CrawlerClientFactory;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.util.CharUtil;
import org.codelibs.fess.entity.FacetQueryView;
import org.codelibs.fess.entity.HighlightInfo;
import org.codelibs.fess.entity.SearchRenderData;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.exception.FessSystemException;
import org.codelibs.fess.helper.UserAgentHelper.UserAgentType;
import org.codelibs.fess.mylasta.action.FessUserBean;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.DocumentUtil;
import org.codelibs.fess.util.FacetResponse;
import org.codelibs.fess.util.ResourceUtil;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.taglib.function.LaFunctions;
import org.lastaflute.web.response.ActionResponse;
import org.lastaflute.web.response.StreamResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaServletContextUtil;
import org.opensearch.core.common.text.Text;
import org.opensearch.search.fetch.subphase.highlight.HighlightField;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.ibm.icu.text.SimpleDateFormat;

public class ViewHelper {

    private static final Logger logger = LogManager.getLogger(ViewHelper.class);

    protected static final String SCREEN_WIDTH = "screen_width";

    protected static final int TABLET_WIDTH = 768;

    protected static final String CONTENT_DISPOSITION = "Content-Disposition";

    protected static final String HL_CACHE = "hl_cache";

    protected static final String QUERIES = "queries";

    protected static final String CACHE_MSG = "cache_msg";

    protected static final Pattern LOCAL_PATH_PATTERN = Pattern.compile("^file:/+[a-zA-Z]:");

    protected static final Pattern SHARED_FOLDER_PATTERN = Pattern.compile("^file:/+[^/]\\.");

    protected static final String ELLIPSIS = "...";

    protected boolean encodeUrlLink = false;

    protected String urlLinkEncoding = Constants.UTF_8;

    protected String[] highlightedFields;

    protected String originalHighlightTagPre = "<em>";

    protected String originalHighlightTagPost = "</em>";

    protected String highlightTagPre;

    protected String highlightTagPost;

    protected boolean useSession = true;

    protected final Map<String, String> pageCacheMap = new ConcurrentHashMap<>();

    protected final Map<String, String> initFacetParamMap = new HashMap<>();

    protected final Map<String, String> initGeoParamMap = new HashMap<>();

    protected final List<FacetQueryView> facetQueryViewList = new ArrayList<>();

    protected String cacheTemplateName = "cache";

    protected String escapedHighlightPre = null;

    protected String escapedHighlightPost = null;

    protected Set<Integer> highlightTerminalCharSet = new HashSet<>();

    protected ActionHook actionHook = new ActionHook();

    protected final Set<String> inlineMimeTypeSet = new HashSet<>();

    protected Cache<String, FacetResponse> facetCache;

    protected long facetCacheDuration = 60 * 10L; // 10min

    protected int textFragmentPrefixLength;

    protected int textFragmentSuffixLength;

    protected int textFragmentSize;

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        escapedHighlightPre = LaFunctions.h(originalHighlightTagPre);
        escapedHighlightPost = LaFunctions.h(originalHighlightTagPost);
        highlightTagPre = fessConfig.getQueryHighlightTagPre();
        highlightTagPost = fessConfig.getQueryHighlightTagPost();
        highlightedFields = fessConfig.getQueryHighlightContentDescriptionFieldsAsArray();
        for (final int v : fessConfig.getQueryHighlightTerminalCharsAsArray()) {
            highlightTerminalCharSet.add(v);
        }
        try {
            final ServletContext servletContext = ComponentUtil.getComponent(ServletContext.class);
            servletContext.setSessionTrackingModes(
                    fessConfig.getSessionTrackingModesAsSet().stream().map(SessionTrackingMode::valueOf).collect(Collectors.toSet()));
        } catch (final Throwable t) {
            logger.warn("Failed to set SessionTrackingMode.", t);
        }

        split(fessConfig.getQueryFacetQueries(), "\n").of(stream -> stream.map(String::trim).filter(StringUtil::isNotEmpty).forEach(s -> {
            final String[] values = StringUtils.split(s, ":", 2);
            if (values.length != 2) {
                return;
            }
            final FacetQueryView facetQueryView = new FacetQueryView();
            facetQueryView.setTitle(values[0]);
            split(values[1], "\t").of(subStream -> subStream.map(String::trim).filter(StringUtil::isNotEmpty).forEach(v -> {
                final String[] facet = StringUtils.split(v, "=", 2);
                if (facet.length == 2) {
                    facetQueryView.addQuery(facet[0], facet[1]);
                }
            }));
            facetQueryView.init();
            facetQueryViewList.add(facetQueryView);
            if (logger.isDebugEnabled()) {
                logger.debug("loaded {}", facetQueryView);
            }
        }));

        facetCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(facetCacheDuration, TimeUnit.SECONDS).build();

        textFragmentPrefixLength = fessConfig.getQueryHighlightTextFragmentPrefixLengthAsInteger();
        textFragmentSuffixLength = fessConfig.getQueryHighlightTextFragmentSuffixLengthAsInteger();
        textFragmentSize = fessConfig.getQueryHighlightTextFragmentSizeAsInteger();

        split(fessConfig.getResponseInlineMimetypes(), ",")
                .of(stream -> stream.map(String::trim).filter(StringUtil::isNotEmpty).forEach(inlineMimeTypeSet::add));
    }

    public String getContentTitle(final Map<String, Object> document) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        String title = DocumentUtil.getValue(document, fessConfig.getIndexFieldTitle(), String.class);
        if (StringUtil.isBlank(title)) {
            title = DocumentUtil.getValue(document, fessConfig.getIndexFieldFilename(), String.class);
            if (StringUtil.isBlank(title)) {
                title = DocumentUtil.getValue(document, fessConfig.getIndexFieldUrl(), String.class);
            }
        }
        final int size = fessConfig.getResponseMaxTitleLengthAsInteger();
        if (size > -1) {
            title = StringUtils.abbreviate(title, size);
        }
        final String value = LaFunctions.h(title);
        if (!fessConfig.isResponseHighlightContentTitleEnabled()) {
            return value;
        }
        return getQuerySet().map(querySet -> {
            final String pattern = querySet.stream().map(LaFunctions::h).map(Pattern::quote).collect(Collectors.joining("|"));
            if (StringUtil.isBlank(pattern)) {
                return null;
            }
            final Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(value);
            final StringBuffer buf = new StringBuffer(value.length() + 100);
            while (matcher.find()) {
                matcher.appendReplacement(buf, Matcher.quoteReplacement(highlightTagPre + matcher.group(0) + highlightTagPost));
            }
            matcher.appendTail(buf);
            return buf.toString();
        }).orElse(value);
    }

    protected OptionalThing<Set<String>> getQuerySet() {
        return LaRequestUtil.getOptionalRequest().map(req -> ((Set<String>) req.getAttribute(Constants.HIGHLIGHT_QUERIES)))
                .filter(s -> s != null);
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
        final String escaped = LaFunctions.h(text);
        final String value;
        if (ComponentUtil.getFessConfig().isQueryHighlightBoundaryPositionDetect()) {
            int pos = escaped.indexOf(escapedHighlightPre);
            while (pos >= 0) {
                final int c = escaped.codePointAt(pos);
                if (Character.isISOControl(c) || highlightTerminalCharSet.contains(c)) {
                    break;
                }
                pos--;
            }

            value = escaped.substring(pos + 1);
        } else {
            value = escaped;
        }
        return value.replaceAll(escapedHighlightPre, highlightTagPre).replaceAll(escapedHighlightPost, highlightTagPost);
    }

    protected String removeHighlightTag(final String str) {
        return str.replaceAll(originalHighlightTagPre, StringUtil.EMPTY).replaceAll(originalHighlightTagPost, StringUtil.EMPTY);
    }

    public HighlightInfo createHighlightInfo() {
        return LaRequestUtil.getOptionalRequest().map(req -> {
            final HighlightInfo highlightInfo = new HighlightInfo();
            final String widthStr = req.getParameter(SCREEN_WIDTH);
            if (StringUtil.isNotBlank(widthStr)) {
                final int width = Integer.parseInt(widthStr);
                updateHighlightInfo(highlightInfo, width);
                final HttpSession session = req.getSession(false);
                if (session != null) {
                    session.setAttribute(SCREEN_WIDTH, width);
                }
            } else {
                final HttpSession session = req.getSession(false);
                if (session != null) {
                    final Integer width = (Integer) session.getAttribute(SCREEN_WIDTH);
                    if (width != null) {
                        updateHighlightInfo(highlightInfo, width);
                    }
                }
            }
            return highlightInfo;
        }).orElse(new HighlightInfo());
    }

    protected void updateHighlightInfo(final HighlightInfo highlightInfo, final int width) {
        if (width < TABLET_WIDTH) {
            float ratio = ((float) width) / ((float) TABLET_WIDTH);
            if (ratio < 0.5) {
                ratio = 0.5f;
            }
            highlightInfo.fragmentSize((int) (highlightInfo.getFragmentSize() * ratio));
        }
    }

    public String getUrlLink(final Map<String, Object> document) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        String url = DocumentUtil.getValue(document, fessConfig.getIndexFieldUrl(), String.class);

        if (StringUtil.isBlank(url)) {
            return "#not-found-" + DocumentUtil.getValue(document, fessConfig.getIndexFieldDocId(), String.class);
        }

        final boolean isSmbUrl = url.startsWith("smb:") || url.startsWith("smb1:");
        final boolean isFtpUrl = url.startsWith("ftp:");
        final boolean isSmbOrFtpUrl = isSmbUrl || isFtpUrl;

        // replacing url with mapping data
        url = ComponentUtil.getPathMappingHelper().replaceUrl(url);

        final boolean isHttpUrl = url.startsWith("http:") || url.startsWith("https:");

        if (isSmbUrl) {
            url = url.replace("smb:", "file:");
            url = url.replace("smb1:", "file:");
        }

        if (isHttpUrl && isSmbOrFtpUrl) {
            //  smb/ftp->http
            // encode
            final StringBuilder buf = new StringBuilder(url.length() + 100);
            for (final char c : url.toCharArray()) {
                if (CharUtil.isUrlChar(c)) {
                    buf.append(c);
                } else {
                    try {
                        buf.append(URLEncoder.encode(String.valueOf(c), urlLinkEncoding));
                    } catch (final UnsupportedEncodingException e) {
                        buf.append(c);
                    }
                }
            }
            url = buf.toString();
        } else if (url.startsWith("file:")) {
            // file, smb/ftp->http
            url = updateFileProtocol(url);

            if (encodeUrlLink) {
                return appendQueryParameter(document, url);
            }

            // decode
            if (!isSmbOrFtpUrl) {
                // file
                try {
                    url = URLDecoder.decode(url.replace("+", "%2B"), urlLinkEncoding);
                } catch (final Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.warn("Failed to decode {}", url, e);
                    }
                }
            }
        }
        // http, ftp
        // nothing

        return appendQueryParameter(document, url);
    }

    protected String updateFileProtocol(String url) {
        final int pos = url.indexOf(':', 5);
        final boolean isLocalFile = pos > 0 && pos < 12;

        final UserAgentType ua = ComponentUtil.getUserAgentHelper().getUserAgentType();
        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
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
        return url;
    }

    protected String appendQueryParameter(final Map<String, Object> document, final String url) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.isAppendQueryParameter()) {
            if (url.indexOf('#') >= 0) {
                return url;
            }

            final String mimetype = DocumentUtil.getValue(document, fessConfig.getIndexFieldMimetype(), String.class);
            if (StringUtil.isNotBlank(mimetype)) {
                switch (mimetype) {
                case "text/html":
                    return appendHTMLSearchWord(document, url);
                case "application/pdf":
                    return appendPDFSearchWord(document, url);
                default:
                    break;
                }
            }
        }
        return url;
    }

    protected String appendHTMLSearchWord(final Map<String, Object> document, final String url) {
        final TextFragment[] textFragments = (TextFragment[]) document.get(Constants.TEXT_FRAGMENTS);
        if (textFragments != null) {
            final StringBuilder buf = new StringBuilder(1000);
            buf.append(url).append("#:~:");
            for (int i = 0; i < textFragmentSize && i < textFragments.length; i++) {
                buf.append(textFragments[i].toURLString()).append('&');
            }
            return buf.toString();
        }
        return url;
    }

    protected String appendPDFSearchWord(final Map<String, Object> document, final String url) {
        return LaRequestUtil.getOptionalRequest().map(req -> (String) req.getAttribute(Constants.REQUEST_QUERIES)).map(queries -> {
            try {
                final StringBuilder buf = new StringBuilder(url.length() + 100);
                buf.append(url).append("#search=%22");
                buf.append(URLEncoder.encode(queries.trim(), Constants.UTF_8));
                buf.append("%22");
                return buf.toString();
            } catch (final UnsupportedEncodingException e) {
                logger.warn("Unsupported encoding.", e);
            }
            return null;
        }).filter(StringUtil::isNotBlank).orElse(url);
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
        doc.put(CACHE_MSG, ComponentUtil.getMessageManager().getMessage(locale, "labels.search_cache_msg", url, createdStr));

        doc.put(QUERIES, queries);

        String cache = DocumentUtil.getValue(doc, fessConfig.getIndexFieldCache(), String.class);
        if (cache != null) {
            final String mimetype = DocumentUtil.getValue(doc, fessConfig.getIndexFieldMimetype(), String.class);
            if (!ComponentUtil.getFessConfig().isHtmlMimetypeForCache(mimetype)) {
                cache = StringEscapeUtils.escapeHtml4(cache);
            }
            cache = ComponentUtil.getPathMappingHelper().replaceUrls(cache);
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
        final Object siteValue = docMap.get(fessConfig.getIndexFieldSite());
        if (siteValue != null) {
            final String site = siteValue.toString();
            final int size = fessConfig.getResponseMaxSitePathLengthAsInteger();
            if (size > -1) {
                return StringUtils.abbreviate(site, size);
            }
            return site;
        }
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
            final int size = fessConfig.getResponseMaxSitePathLengthAsInteger();
            if (size > -1) {
                return StringUtils.abbreviate(returnUrl, size);
            }
            return returnUrl;
        }
        return null;
    }

    public StreamResponse asContentResponse(final Map<String, Object> doc) {
        if (logger.isDebugEnabled()) {
            logger.debug("writing the content of: {}", doc);
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
        final CrawlingConfig config = crawlingConfigHelper.getCrawlingConfig(configId);
        if (config == null) {
            throw new FessSystemException("No crawlingConfig: " + configId);
        }
        final String url = DocumentUtil.getValue(doc, fessConfig.getIndexFieldUrl(), String.class);
        final CrawlerClientFactory crawlerClientFactory =
                config.initializeClientFactory(() -> ComponentUtil.getComponent(CrawlerClientFactory.class));
        final CrawlerClient client = crawlerClientFactory.getClient(url);
        if (client == null) {
            throw new FessSystemException("No CrawlerClient: " + configId + ", url: " + url);
        }
        return writeContent(configId, url, client);
    }

    protected StreamResponse writeContent(final String configId, final String url, final CrawlerClient client) {
        final StreamResponse response = new StreamResponse(StringUtil.EMPTY);
        final ResponseData responseData = client.execute(RequestDataBuilder.newRequestData().get().url(url).build());
        if (responseData.getHttpStatusCode() == 404) {
            response.httpStatus(responseData.getHttpStatusCode());
            CloseableUtil.closeQuietly(responseData);
            return response;
        }
        writeFileName(response, responseData);
        writeContentType(response, responseData);
        writeNoCache(response, responseData);
        response.stream(out -> {
            try (final InputStream is = new BufferedInputStream(responseData.getResponseBody())) {
                out.write(is);
            } catch (final IOException e) {
                if (!(e.getCause() instanceof ClientAbortException)) {
                    throw new FessSystemException("Failed to write a content. configId: " + configId + ", url: " + url, e);
                }
            } finally {
                CloseableUtil.closeQuietly(responseData);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Finished to write {}", url);
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

            final String contentDispositionType;
            if (inlineMimeTypeSet.contains(responseData.getMimeType())) {
                contentDispositionType = "inline";
            } else {
                contentDispositionType = "attachment";
            }

            final String encodedName = URLEncoder.encode(name, Constants.UTF_8).replace("+", "%20");
            final String contentDispositionValue;
            if (name.equals(encodedName)) {
                contentDispositionValue = contentDispositionType + "; filename=\"" + name + "\"";
            } else {
                contentDispositionValue = contentDispositionType + "; filename*=utf-8''" + encodedName;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("ResponseHeader: {}: {}", CONTENT_DISPOSITION, contentDispositionValue);
            }
            response.header(CONTENT_DISPOSITION, contentDispositionValue);
        } catch (final Exception e) {
            logger.warn("Failed to write a filename: {}", responseData, e);
        }
    }

    protected void writeContentType(final StreamResponse response, final ResponseData responseData) {
        final String mimeType = responseData.getMimeType();
        if (logger.isDebugEnabled()) {
            logger.debug("mimeType: {}", mimeType);
        }
        if (mimeType == null) {
            response.contentTypeOctetStream();
            return;
        }
        if (mimeType.startsWith("text/")) {
            final String charset = responseData.getCharSet();
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
        }
        return request.getRemoteAddr();
    }

    public FacetResponse getCachedFacetResponse(final String query) {
        final OptionalThing<FessUserBean> userBean = ComponentUtil.getComponent(FessLoginAssist.class).getSavedUserBean();
        final String permissionKey = userBean.map(user -> StreamUtil.stream(user.getPermissions())
                .get(stream -> stream.sorted().distinct().collect(Collectors.joining("\n")))).orElse(StringUtil.EMPTY);

        try {
            return facetCache.get(query + "\n" + permissionKey, () -> {
                final SearchHelper searchHelper = ComponentUtil.getSearchHelper();
                final SearchForm params = new SearchForm() {
                    @Override
                    public int getPageSize() {
                        return 0;
                    }

                    @Override
                    public int getStartPosition() {
                        return 0;
                    }
                };
                params.q = query;
                final SearchRenderData data = new SearchRenderData();
                searchHelper.search(params, data, userBean);
                if (logger.isDebugEnabled()) {
                    logger.debug("loaded facet data: {}", data);
                }
                return data.getFacetResponse();
            });
        } catch (final ExecutionException e) {
            throw new FessSystemException("Cannot load facet from cache.", e);
        }
    }

    public String createHighlightText(final HighlightField highlightField) {
        final Text[] fragments = highlightField.fragments();
        if (fragments != null && fragments.length != 0) {
            final String[] texts = new String[fragments.length];
            for (int i = 0; i < fragments.length; i++) {
                texts[i] = fragments[i].string();
            }
            final String value = StringUtils.join(texts, ELLIPSIS);
            if (StringUtil.isNotBlank(value) && !ComponentUtil.getFessConfig().endsWithFullstop(value)) {
                return value + ELLIPSIS;
            }
            return value;
        }
        return null;
    }

    public TextFragment[] createTextFragmentsByHighlight(final HighlightField[] fields) {
        final List<TextFragment> list = new ArrayList<>();
        for (final HighlightField field : fields) {
            final Text[] fragments = field.fragments();
            if (fragments != null) {
                for (final Text fragment : fragments) {
                    final String text = fragment.string();
                    if (text.length() > textFragmentPrefixLength + textFragmentSuffixLength) {
                        final String target =
                                text.replace(originalHighlightTagPre, StringUtil.EMPTY).replace(originalHighlightTagPost, StringUtil.EMPTY);
                        if (target.length() > textFragmentPrefixLength + textFragmentSuffixLength) {
                            list.add(new TextFragment(null, target.substring(0, textFragmentPrefixLength),
                                    target.substring(target.length() - textFragmentSuffixLength), null));
                        }
                    }
                }
            }
        }
        return list.toArray(n -> new TextFragment[n]);
    }

    public TextFragment[] createTextFragmentsByQuery() {
        return LaRequestUtil.getOptionalRequest().map(req -> {
            @SuppressWarnings("unchecked")
            final Set<String> querySet = (Set<String>) req.getAttribute(Constants.HIGHLIGHT_QUERIES);
            if (querySet != null) {
                return querySet.stream().map(s -> new TextFragment(null, s, null, null)).toArray(n -> new TextFragment[n]);
            }
            return new TextFragment[0];
        }).orElse(new TextFragment[0]);
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

    public void addInlineMimeType(final String mimeType) {
        inlineMimeTypeSet.add(mimeType);
    }

    public ActionHook getActionHook() {
        return actionHook;
    }

    public void setActionHook(final ActionHook actionHook) {
        this.actionHook = actionHook;
    }

    public void setEncodeUrlLink(final boolean encodeUrlLink) {
        this.encodeUrlLink = encodeUrlLink;
    }

    public void setUrlLinkEncoding(final String urlLinkEncoding) {
        this.urlLinkEncoding = urlLinkEncoding;
    }

    public void setOriginalHighlightTagPre(final String originalHighlightTagPre) {
        this.originalHighlightTagPre = originalHighlightTagPre;
    }

    public void setOriginalHighlightTagPost(final String originalHighlightTagPost) {
        this.originalHighlightTagPost = originalHighlightTagPost;
    }

    public void setCacheTemplateName(final String cacheTemplateName) {
        this.cacheTemplateName = cacheTemplateName;
    }

    public void setFacetCacheDuration(final long facetCacheDuration) {
        this.facetCacheDuration = facetCacheDuration;
    }

    public static class ActionHook {

        public ActionResponse godHandPrologue(final ActionRuntime runtime, final Function<ActionRuntime, ActionResponse> func) {
            return func.apply(runtime);
        }

        public ActionResponse godHandMonologue(final ActionRuntime runtime, final Function<ActionRuntime, ActionResponse> func) {
            return func.apply(runtime);
        }

        public void godHandEpilogue(final ActionRuntime runtime, final Consumer<ActionRuntime> consumer) {
            consumer.accept(runtime);
        }

        public ActionResponse hookBefore(final ActionRuntime runtime, final Function<ActionRuntime, ActionResponse> func) {
            return func.apply(runtime);
        }

        public void hookFinally(final ActionRuntime runtime, final Consumer<ActionRuntime> consumer) {
            consumer.accept(runtime);
        }
    }

    // #:~:text=[prefix-,]textStart[,textEnd][,-suffix]
    public static class TextFragment {
        private final String prefix;
        private final String textStart;
        private final String textEnd;
        private final String suffix;

        TextFragment(final String prefix, final String textStart, final String textEnd, final String suffix) {
            this.prefix = prefix;
            this.textStart = textStart == null ? StringUtil.EMPTY : textStart;
            this.textEnd = textEnd;
            this.suffix = suffix;
        }

        public String toURLString() {
            final StringBuilder buf = new StringBuilder();
            buf.append("text=");
            if (StringUtil.isNotBlank(prefix)) {
                buf.append(encodeToString(prefix)).append("-,");
            }
            buf.append(encodeToString(textStart));
            if (StringUtil.isNotBlank(textEnd)) {
                buf.append(',').append(encodeToString(textEnd));
            }
            if (StringUtil.isNotBlank(suffix)) {
                buf.append(",-").append(encodeToString(suffix));
            }
            return buf.toString();
        }

        private String encodeToString(final String text) {
            return URLEncoder.encode(text, Constants.CHARSET_UTF_8);
        }
    }
}
