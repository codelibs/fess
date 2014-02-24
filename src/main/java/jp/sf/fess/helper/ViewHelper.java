/*
 * Copyright 2009-2014 the CodeLibs Project and the Others.
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

package jp.sf.fess.helper;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import jp.sf.fess.Constants;
import jp.sf.fess.FessSystemException;
import jp.sf.fess.entity.FacetQueryView;
import jp.sf.fess.helper.UserAgentHelper.UserAgentType;
import jp.sf.fess.util.ResourceUtil;

import org.apache.commons.lang.StringUtils;
import org.codelibs.core.util.DynamicProperties;
import org.codelibs.core.util.StringUtil;
import org.seasar.framework.util.URLUtil;
import org.seasar.robot.util.CharUtil;
import org.seasar.struts.taglib.S2Functions;
import org.seasar.struts.util.RequestUtil;
import org.seasar.struts.util.ServletContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;

public class ViewHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(ViewHelper.class);

    protected static final String GOOGLE_MOBILE_TRANSCODER_LINK = "http://www.google.co.jp/gwt/n?u=";

    @Resource
    protected BrowserTypeHelper browserTypeHelper;

    @Resource
    protected PathMappingHelper pathMappingHelper;

    @Resource
    protected UserAgentHelper userAgentHelper;

    @Resource
    protected DynamicProperties crawlerProperties;

    public int mobileDescriptionLength = 50;

    public int pcDescriptionLength = 200;

    public int mobileTitleLength = 50;

    public int pcTitleLength = 50;

    public boolean encodeUrlLink = false;

    public String urlLinkEncoding = Constants.UTF_8;

    public String[] highlightingFields = new String[] { "hl_content", "digest" };

    public String highlightTagPre = "<em>";

    public String highlightTagPost = "</em>";

    protected boolean useSession = true;

    private final Map<String, String> pageCacheMap = new ConcurrentHashMap<String, String>();

    private final Map<String, String> initFacetParamMap = new HashMap<String, String>();

    private final Map<String, String> initMltParamMap = new HashMap<String, String>();

    private final Map<String, String> initGeoParamMap = new HashMap<String, String>();

    private final List<FacetQueryView> facetQueryViewList = new ArrayList<FacetQueryView>();

    public String cacheTemplateName = "cache";

    private String getString(final Map<String, Object> doc, final String key) {
        final Object value = doc.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public String getContentTitle(final Map<String, Object> document) {
        int size;
        if (browserTypeHelper.isMobile()) {
            size = mobileTitleLength;
        } else {
            size = pcTitleLength;
        }

        String title;
        if (StringUtil.isNotBlank(getString(document, "title"))) {
            title = getString(document, "title");
        } else {
            title = getString(document, "url");
        }
        return StringUtils.abbreviate(title, size);
    }

    public String getContentDescription(final Map<String, Object> document) {
        final HttpServletRequest request = RequestUtil.getRequest();
        final String[] queries = request == null ? StringUtil.EMPTY_STRINGS
                : (String[]) request.getAttribute(Constants.HIGHLIGHT_QUERIES);
        int size;
        if (browserTypeHelper.isMobile()) {
            size = mobileDescriptionLength;
        } else {
            size = pcDescriptionLength;
        }

        for (final String field : highlightingFields) {
            final String text = getString(document, field);
            if (StringUtil.isNotBlank(text)) {
                return highlight(S2Functions.h(StringUtils.abbreviate(
                        removeEmTag(text), size)), queries);
            }
        }

        return StringUtil.EMPTY;
    }

    protected String removeEmTag(final String str) {
        return str.replaceAll("<em>", StringUtil.EMPTY).replaceAll("</em>",
                StringUtil.EMPTY);
    }

    protected String highlight(final String content, final String[] queries) {
        if (StringUtil.isBlank(content) || queries == null
                || queries.length == 0) {
            return content;
        }
        String newContent = content;
        for (final String query : queries) {
            newContent = Pattern
                    .compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE)
                    .matcher(newContent)
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
                        buf.append(URLEncoder.encode(String.valueOf(c),
                                urlLinkEncoding));
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
                    url = url.replaceFirst("file:/+",
                            crawlerProperties.getProperty(
                                    "file.protocol.winlocal.ie", "file://"));
                } else {
                    url = url.replaceFirst("file:/+", crawlerProperties
                            .getProperty("file.protocol.ie", "file://"));
                }
                break;
            case FIREFOX:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", crawlerProperties
                            .getProperty("file.protocol.winlocal.firefox",
                                    "file://"));
                } else {
                    url = url
                            .replaceFirst("file:/+", crawlerProperties
                                    .getProperty("file.protocol.firefox",
                                            "file://///"));
                }
                break;
            case CHROME:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", crawlerProperties
                            .getProperty("file.protocol.winlocal.chrome",
                                    "file://"));
                } else {
                    url = url.replaceFirst("file:/+", crawlerProperties
                            .getProperty("file.protocol.chrome", "file://"));
                }
                break;
            case SAFARI:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", crawlerProperties
                            .getProperty("file.protocol.winlocal.safari",
                                    "file://"));
                } else {
                    url = url.replaceFirst("file:/+", crawlerProperties
                            .getProperty("file.protocol.safari", "file:////"));
                }
                break;
            case OPERA:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", crawlerProperties
                            .getProperty("file.protocol.winlocal.opera",
                                    "file://"));
                } else {
                    url = url.replaceFirst("file:/+", crawlerProperties
                            .getProperty("file.protocol.opera", "file://"));
                }
                break;
            default:
                if (isLocalFile) {
                    url = url.replaceFirst("file:/+", crawlerProperties
                            .getProperty("file.protocol.winlocal.other",
                                    "file://"));
                } else {
                    url = url.replaceFirst("file:/+", crawlerProperties
                            .getProperty("file.protocol.other", "file://"));
                }
                break;
            }

            if (encodeUrlLink) {
                return appendQueryParameter(document, url);
            } else {
                url = url.replace("+", "%2B");
                try {
                    return URLDecoder.decode(
                            appendQueryParameter(document, url),
                            urlLinkEncoding);
                } catch (final Exception e) {
                    throw new FessSystemException("Unsupported encoding: "
                            + urlLinkEncoding, e);
                }
            }
        }

        if (browserTypeHelper.isMobile()) {
            final String mobileTrasncoder = crawlerProperties.getProperty(
                    Constants.MOBILE_TRANSCODER_PROPERTY, StringUtil.EMPTY);
            if (Constants.GOOGLE_MOBILE_TRANSCODER.equals(mobileTrasncoder)) {
                return getGoogleMobileTranscoderLink(appendQueryParameter(
                        document, url));
            }
        }

        return appendQueryParameter(document, url);
    }

    protected String appendQueryParameter(final Map<String, Object> document,
            final String url) {
        if (Constants.TRUE.equals(crawlerProperties
                .get(Constants.APPEND_QUERY_PARAMETER_PROPERTY))) {
            final String mimetype = getString(document, "mimetype");
            if (StringUtil.isNotBlank(mimetype)) {
                if ("application/pdf".equals(mimetype)) {
                    return appendSearchWord(url, "search");
                } else {
                    // TODO others..
                    return url;
                }
            }
        }
        return url;
    }

    protected String appendSearchWord(final String url,
            final String searchWordKey) {
        final String query = RequestUtil.getRequest().getParameter("query");
        if (StringUtil.isNotBlank(query)) {
            String separator;
            if (url.indexOf('?') >= 0) {
                separator = "&";
            } else {
                separator = "?";
            }
            return url + separator + searchWordKey + "="
                    + URLUtil.encode(query, urlLinkEncoding);

        }
        return url;
    }

    protected String getGoogleMobileTranscoderLink(final String url) {
        final StringBuilder buf = new StringBuilder(255);
        buf.append(GOOGLE_MOBILE_TRANSCODER_LINK);
        try {
            buf.append(URLEncoder.encode(url, Constants.UTF_8));
        } catch (final UnsupportedEncodingException e) {
            return url;
        }
        return buf.toString();
    }

    public String getPagePath(final String page) {
        final Locale locale = RequestUtil.getRequest().getLocale();
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

    private String getLocalizedPagePath(final String page, final String lang,
            final String country) {
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
        final String realPath = ServletContextUtil.getServletContext()
                .getRealPath(path);
        final File file = new File(realPath);
        return file.isFile();
    }

    public String createCacheContent(final Map<String, Object> doc) {

        final FileTemplateLoader loader = new FileTemplateLoader(new File(
                ResourceUtil.getViewTemplatePath(StringUtil.EMPTY)));
        final Handlebars handlebars = new Handlebars(loader);

        try {
            final Template template = handlebars.compile(cacheTemplateName);
            final Context hbsContext = Context.newContext(doc);
            return template.apply(hbsContext);
        } catch (final Exception e) {
            logger.warn("Failed to create a cache response.", e);
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

    public void addInitMltParam(final String key, final String value) {
        initMltParamMap.put(value, key);
    }

    public Map<String, String> getInitMltParamMap() {
        return initMltParamMap;
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
