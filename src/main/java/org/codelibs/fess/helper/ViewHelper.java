/*
 * Copyright 2009-2015 the CodeLibs Project and the Others.
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

import java.io.File;
import java.io.Serializable;
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

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.CoreLibConstants;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.core.net.URLUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.FessSystemException;
import org.codelibs.fess.entity.FacetQueryView;
import org.codelibs.fess.helper.UserAgentHelper.UserAgentType;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ResourceUtil;
import org.codelibs.robot.util.CharUtil;
import org.lastaflute.taglib.function.LaFunctions;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.util.LaServletContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.ibm.icu.text.SimpleDateFormat;

public class ViewHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(ViewHelper.class);

    @Resource
    protected PathMappingHelper pathMappingHelper;

    @Resource
    protected UserAgentHelper userAgentHelper;

    @Resource
    protected DynamicProperties crawlerProperties;

    public int descriptionLength = 200;

    public int titleLength = 50;

    public int sitePathLength = 50;

    public boolean encodeUrlLink = false;

    public String urlLinkEncoding = Constants.UTF_8;

    public String[] highlightingFields = new String[] { "hl_content", "digest" };

    public boolean useSolrHighlight = false;

    public String solrHighlightTagPre = "<em>";

    public String solrHighlightTagPost = "</em>";

    public String highlightTagPre = "<em>";

    public String highlightTagPost = "</em>";

    protected boolean useSession = true;

    private final Map<String, String> pageCacheMap = new ConcurrentHashMap<String, String>();

    private final Map<String, String> initFacetParamMap = new HashMap<String, String>();

    private final Map<String, String> initGeoParamMap = new HashMap<String, String>();

    private final List<FacetQueryView> facetQueryViewList = new ArrayList<FacetQueryView>();

    public String cacheTemplateName = "cache";

    private String escapedSolrHighlightPre = null;

    private String escapedSolrHighlightPost = null;

    @PostConstruct
    public void init() {
        if (useSolrHighlight) {
            escapedSolrHighlightPre = LaFunctions.h(solrHighlightTagPre);
            escapedSolrHighlightPost = LaFunctions.h(solrHighlightTagPost);
        }
    }

    private String getString(final Map<String, Object> doc, final String key) {
        final Object value = doc.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public String getContentTitle(final Map<String, Object> document) {
        final int size = titleLength;
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        String title;
        if (StringUtil.isNotBlank(getString(document, fieldHelper.titleField))) {
            title = getString(document, fieldHelper.titleField);
        } else {
            title = getString(document, fieldHelper.urlField);
        }
        return StringUtils.abbreviate(title, size);
    }

    public String getContentDescription(final Map<String, Object> document) {
        final HttpServletRequest request = LaRequestUtil.getRequest();
        final String[] queries = request == null ? StringUtil.EMPTY_STRINGS : (String[]) request.getAttribute(Constants.HIGHLIGHT_QUERIES);
        final int size = descriptionLength;

        for (final String field : highlightingFields) {
            final String text = getString(document, field);
            if (StringUtil.isNotBlank(text)) {
                if (useSolrHighlight) {
                    return escapeHighlight(text);
                } else {
                    return highlight(LaFunctions.h(StringUtils.abbreviate(removeSolrHighlightTag(text), size)), queries);
                }
            }
        }

        return StringUtil.EMPTY;
    }

    protected String escapeHighlight(final String text) {
        return LaFunctions.h(text).replaceAll(escapedSolrHighlightPre, solrHighlightTagPre)
                .replaceAll(escapedSolrHighlightPost, solrHighlightTagPost);
    }

    protected String removeSolrHighlightTag(final String str) {
        return str.replaceAll(solrHighlightTagPre, StringUtil.EMPTY).replaceAll(solrHighlightTagPost, StringUtil.EMPTY);
    }

    protected String highlight(final String content, final String[] queries) {
        if (StringUtil.isBlank(content) || queries == null || queries.length == 0) {
            return content;
        }
        String newContent = content;
        for (final String query : queries) {
            newContent =
                    Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE).matcher(newContent)
                            .replaceAll(highlightTagPre + query + highlightTagPost);
        }
        return newContent;
    }

    public String getUrlLink(final Map<String, Object> document) {
        // file protocol
        String url = getString(document, "url");

        if (url == null) {
            // TODO should redirect to a invalid page?
            return "#";
        }

        final boolean isSmbUrl = url.startsWith("smb:");

        // replacing url with mapping data
        url = pathMappingHelper.replaceUrl(url);

        if (url.startsWith("smb:")) {
            url = url.replace("smb:", "file:");
        }

        if (url.startsWith("http:") && isSmbUrl) {
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
                    url = url.replaceFirst("file:/+", crawlerProperties.getProperty("file.protocol.winlocal.ie", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", crawlerProperties.getProperty("file.protocol.ie", "file://"));
                }
                break;
            case FIREFOX:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", crawlerProperties.getProperty("file.protocol.winlocal.firefox", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", crawlerProperties.getProperty("file.protocol.firefox", "file://///"));
                }
                break;
            case CHROME:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", crawlerProperties.getProperty("file.protocol.winlocal.chrome", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", crawlerProperties.getProperty("file.protocol.chrome", "file://"));
                }
                break;
            case SAFARI:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", crawlerProperties.getProperty("file.protocol.winlocal.safari", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", crawlerProperties.getProperty("file.protocol.safari", "file:////"));
                }
                break;
            case OPERA:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", crawlerProperties.getProperty("file.protocol.winlocal.opera", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", crawlerProperties.getProperty("file.protocol.opera", "file://"));
                }
                break;
            default:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", crawlerProperties.getProperty("file.protocol.winlocal.other", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", crawlerProperties.getProperty("file.protocol.other", "file://"));
                }
                break;
            }

            if (encodeUrlLink) {
                return appendQueryParameter(document, url);
            } else {
                url = url.replace("+", "%2B");
                try {
                    return URLDecoder.decode(appendQueryParameter(document, url), urlLinkEncoding);
                } catch (final Exception e) {
                    throw new FessSystemException("Unsupported encoding: " + urlLinkEncoding, e);
                }
            }
        }

        return appendQueryParameter(document, url);
    }

    protected String appendQueryParameter(final Map<String, Object> document, final String url) {
        if (Constants.TRUE.equals(crawlerProperties.get(Constants.APPEND_QUERY_PARAMETER_PROPERTY))) {
            final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
            final String mimetype = getString(document, fieldHelper.mimetypeField);
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
        final String[] queries = (String[]) LaRequestUtil.getRequest().getAttribute(Constants.HIGHLIGHT_QUERIES);
        if (queries != null) {
            final StringBuilder buf = new StringBuilder(url.length() + 100);
            buf.append(url).append("#search=%22");
            for (int i = 0; i < queries.length; i++) {
                if (i != 0) {
                    buf.append(' ');
                }
                buf.append(URLUtil.encode(queries[i], urlLinkEncoding));
            }
            buf.append("%22");
            return buf.toString();
        }
        return url;
    }

    public String getPagePath(final String page) {
        final Locale locale = LaRequestUtil.getRequest().getLocale();
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
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final FileTemplateLoader loader = new FileTemplateLoader(new File(ResourceUtil.getViewTemplatePath(StringUtil.EMPTY)));
        final Handlebars handlebars = new Handlebars(loader);

        Locale locale = LaRequestUtil.getRequest().getLocale();
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        String url = (String) doc.get("urlLink");
        if (url == null) {
            url = ComponentUtil.getMessageManager().getMessage(locale, "labels.search_unknown");
        }
        Object created = doc.get(fieldHelper.createdField);
        if (created instanceof Date) {
            final SimpleDateFormat sdf = new SimpleDateFormat(CoreLibConstants.DATE_FORMAT_ISO_8601_EXTEND);
            created = sdf.format((Date) created);
        } else {
            created = ComponentUtil.getMessageManager().getMessage(locale, "labels.search_unknown");
        }
        doc.put("cacheMsg", ComponentUtil.getMessageManager().getMessage(locale, "labels.search_cache_msg", new Object[] { url, created }));

        doc.put("queries", queries);

        String cache = (String) doc.get(fieldHelper.cacheField);
        if (cache != null) {
            cache = pathMappingHelper.replaceUrls(cache);
            if (queries != null && queries.length > 0) {
                doc.put("hlCache", replaceHighlightQueries(cache, queries));
            } else {
                doc.put("hlCache", cache);
            }
        } else {
            doc.put(fieldHelper.cacheField, StringUtil.EMPTY);
            doc.put("hlCache", StringUtil.EMPTY);
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
        final Object urlLink = docMap.get("urlLink");
        if (urlLink != null) {
            return StringUtils.abbreviate(urlLink.toString().replaceFirst("^[a-zA-Z0-9]*:/?/*", ""), sitePathLength);
        }
        return null;
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
