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
package org.codelibs.fess.crawler.transformer;

import static org.codelibs.core.stream.StreamUtil.stream;

import java.io.BufferedInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.xml.xpath.XPathEvaluationResult;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathNodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.io.SerializeUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.ValueHolder;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.transformer.impl.XpathTransformer;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.Param.Config;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.Param.XPath;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.DocumentHelper;
import org.codelibs.fess.helper.DuplicateHostHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.PrunedTag;
import org.codelibs.nekohtml.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class FessXpathTransformer extends XpathTransformer implements FessTransformer {

    private static final Logger logger = LogManager.getLogger(FessXpathTransformer.class);

    private static final String X_ROBOTS_TAG = "X-Robots-Tag";

    private static final String META_NAME_THUMBNAIL_CONTENT = "//META[@name=\"thumbnail\" or @name=\"THUMBNAIL\"]/@content";

    private static final String META_PROPERTY_OGIMAGE_CONTENT = "//META[@property=\"og:image\"]/@content";

    private static final String META_NAME_ROBOTS_CONTENT = "//META[@name=\"robots\" or @name=\"ROBOTS\"]/@content";

    private static final String ROBOTS_TAG_NONE = "none";

    private static final String ROBOTS_TAG_NOINDEX = "noindex";

    private static final String ROBOTS_TAG_NOFOLLOW = "nofollow";

    private static final int UTF8_BOM_SIZE = 3;

    public boolean prunedContent = true;

    protected Map<String, String> convertUrlMap = new LinkedHashMap<>();

    protected FessConfig fessConfig;

    protected boolean useGoogleOffOn = true;

    protected Map<String, Boolean> fieldPrunedRuleMap = new HashMap<>();

    protected Map<String, PrunedTag[]> prunedTagsCache = new HashMap<>();

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        fessConfig = ComponentUtil.getFessConfig();
    }

    @Override
    public FessConfig getFessConfig() {
        return fessConfig;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    protected void storeData(final ResponseData responseData, final ResultData resultData) {
        final DOMParser parser = getDomParser();
        try (final BufferedInputStream bis = new BufferedInputStream(responseData.getResponseBody())) {
            final byte[] bomBytes = new byte[UTF8_BOM_SIZE];
            bis.mark(UTF8_BOM_SIZE);
            final int size = bis.read(bomBytes);
            if (size < 3 || !isUtf8BomBytes(bomBytes)) {
                bis.reset();
            }
            final InputSource is = new InputSource(bis);
            if (responseData.getCharSet() != null) {
                is.setEncoding(responseData.getCharSet());
            }
            parser.parse(is);
        } catch (final Exception e) {
            throw new CrawlingAccessException("Could not parse " + responseData.getUrl(), e);
        }

        final Document document = parser.getDocument();

        processMetaRobots(responseData, resultData, document);
        processXRobotsTag(responseData, resultData);

        final Map<String, Object> dataMap = new LinkedHashMap<>();
        for (final Map.Entry<String, String> entry : fieldRuleMap.entrySet()) {
            final String path = entry.getValue();
            try {
                final XPathEvaluationResult<?> xObj = getXPathAPI().eval(document, path);
                switch (xObj.type()) {
                case BOOLEAN:
                    final Boolean b = (Boolean) xObj.value();
                    putResultDataBody(dataMap, entry.getKey(), b.toString());
                    break;
                case NUMBER:
                    final Number d = (Number) xObj.value();
                    putResultDataBody(dataMap, entry.getKey(), d.toString());
                    break;
                case STRING:
                    final String str = (String) xObj.value();
                    putResultDataBody(dataMap, entry.getKey(), str);
                    break;
                default:
                    final Boolean isPruned = fieldPrunedRuleMap.get(entry.getKey());
                    Node value = getXPathAPI().selectSingleNode(document, entry.getValue());
                    if (value != null && isPruned != null && isPruned.booleanValue()) {
                        value = pruneNode(value, getCrawlingConfig(responseData));
                    }
                    putResultDataBody(dataMap, entry.getKey(), value != null ? value.getTextContent() : null);
                    break;
                }
            } catch (final XPathExpressionException e) {
                logger.warn("Could not parse a value of {}:{}", entry.getKey(), entry.getValue(), e);
            }
        }

        putAdditionalData(dataMap, responseData, document);
        normalizeData(responseData, dataMap);

        try {
            resultData.setData(SerializeUtil.fromObjectToBinary(dataMap));
        } catch (final Exception e) {
            throw new CrawlingAccessException("Could not serialize object: " + responseData.getUrl(), e);
        }
        resultData.setEncoding(charsetName);
    }

    protected void normalizeData(final ResponseData responseData, final Map<String, Object> dataMap) {
        final Object titleObj = dataMap.get(fessConfig.getIndexFieldTitle());
        if (titleObj != null) {
            dataMap.put(fessConfig.getIndexFieldTitle(),
                    ComponentUtil.getDocumentHelper().getTitle(responseData, titleObj.toString(), dataMap));
        }
    }

    protected void processMetaRobots(final ResponseData responseData, final ResultData resultData, final Document document) {
        final Map<String, String> configMap = getConfigPrameterMap(responseData, ConfigName.CONFIG);
        final String ignore = configMap.get(Config.IGNORE_ROBOTS_TAGS);
        if (ignore == null) {
            if (fessConfig.isCrawlerIgnoreRobotsTags()) {
                return;
            }
        } else if (Boolean.parseBoolean(ignore)) {
            return;
        }

        // meta tag
        try {
            final Node value = getXPathAPI().selectSingleNode(document, META_NAME_ROBOTS_CONTENT);
            if (value != null) {
                boolean noindex = false;
                boolean nofollow = false;
                final String content = value.getTextContent().toLowerCase(Locale.ROOT);
                if (content.contains(ROBOTS_TAG_NONE)) {
                    noindex = true;
                    nofollow = true;
                } else {
                    if (content.contains(ROBOTS_TAG_NOINDEX)) {
                        noindex = true;
                    }
                    if (content.contains(ROBOTS_TAG_NOFOLLOW)) {
                        nofollow = true;
                    }
                }
                if (noindex && nofollow) {
                    logger.info("META(robots=noindex,nofollow): {}", responseData.getUrl());
                    throw new ChildUrlsException(Collections.emptySet(), "#processMetaRobots");
                }
                if (noindex) {
                    logger.info("META(robots=noindex): {}", responseData.getUrl());
                    storeChildUrls(responseData, resultData);
                    throw new ChildUrlsException(resultData.getChildUrlSet(), "#processMetaRobots");
                }
                if (nofollow) {
                    logger.info("META(robots=nofollow): {}", responseData.getUrl());
                    responseData.setNoFollow(true);
                }
            }
        } catch (final XPathExpressionException e) {
            logger.warn("Could not parse a value of {}", META_NAME_ROBOTS_CONTENT, e);
        }

    }

    protected void processXRobotsTag(final ResponseData responseData, final ResultData resultData) {
        final Map<String, String> configMap = getConfigPrameterMap(responseData, ConfigName.CONFIG);
        final String ignore = configMap.get(Config.IGNORE_ROBOTS_TAGS);
        if (ignore == null) {
            if (fessConfig.isCrawlerIgnoreRobotsTags()) {
                return;
            }
        } else if (Boolean.parseBoolean(ignore)) {
            return;
        }

        // X-Robots-Tag
        responseData.getMetaDataMap().entrySet().stream().filter(e -> X_ROBOTS_TAG.equalsIgnoreCase(e.getKey()) && e.getValue() != null)
                .forEach(e -> {
                    boolean noindex = false;
                    boolean nofollow = false;
                    final String value = e.getValue().toString().toLowerCase(Locale.ROOT);
                    if (value.contains(ROBOTS_TAG_NONE)) {
                        noindex = true;
                        nofollow = true;
                    } else {
                        if (value.contains(ROBOTS_TAG_NOINDEX)) {
                            noindex = true;
                        }
                        if (value.contains(ROBOTS_TAG_NOFOLLOW)) {
                            nofollow = true;
                        }
                    }
                    if (noindex && nofollow) {
                        logger.info("HEADER(robots=noindex,nofollow): {}", responseData.getUrl());
                        throw new ChildUrlsException(Collections.emptySet(), "#processXRobotsTag");
                    }
                    if (noindex) {
                        logger.info("HEADER(robots=noindex): {}", responseData.getUrl());
                        storeChildUrls(responseData, resultData);
                        throw new ChildUrlsException(resultData.getChildUrlSet(), "#processXRobotsTag");
                    }
                    if (nofollow) {
                        logger.info("HEADER(robots=nofollow): {}", responseData.getUrl());
                        responseData.setNoFollow(true);
                    }
                });
    }

    protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(responseData.getSessionId());
        return crawlingConfig.getConfigParameterMap(config);
    }

    protected boolean isValidUrl(final String urlStr) {
        if (StringUtil.isBlank(urlStr)) {
            return false;
        }
        final String value;
        if (urlStr.startsWith("://")) {
            value = "http" + urlStr;
        } else if (urlStr.startsWith("//")) {
            value = "http:" + urlStr;
        } else {
            value = urlStr;
        }
        try {
            final URL url = new java.net.URL(value);
            final String host = url.getHost();
            if (StringUtil.isBlank(host) || "http".equalsIgnoreCase(host) || "https".equalsIgnoreCase(host)) {
                return false;
            }
        } catch (final MalformedURLException e) {
            return false;
        }
        return true;
    }

    protected boolean isValidCanonicalUrl(final String url, final String canonicalUrl) {
        if (url.startsWith("https:") && canonicalUrl.startsWith("http:")) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid Canonical Url(https->http): {} -> {}", url, canonicalUrl);
            }
            return false;
        }
        return true;
    }

    protected void putAdditionalData(final Map<String, Object> dataMap, final ResponseData responseData, final Document document) {
        // canonical
        final String canonicalUrl = getCanonicalUrl(responseData, document);
        if (canonicalUrl != null && !canonicalUrl.equals(responseData.getUrl()) && isValidUrl(canonicalUrl)
                && isValidCanonicalUrl(responseData.getUrl(), canonicalUrl)) {
            final Set<RequestData> childUrlSet = new HashSet<>();
            childUrlSet.add(RequestDataBuilder.newRequestData().get().url(canonicalUrl).build());
            logger.info("CANONICAL: {} -> {}", responseData.getUrl(), canonicalUrl);
            throw new ChildUrlsException(childUrlSet, this.getClass().getName() + "#putAdditionalData");
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();
        final String sessionId = crawlingInfoHelper.getCanonicalSessionId(responseData.getSessionId());
        final PathMappingHelper pathMappingHelper = ComponentUtil.getPathMappingHelper();
        final CrawlingConfig crawlingConfig = getCrawlingConfig(responseData);
        final Date documentExpires = crawlingInfoHelper.getDocumentExpires(crawlingConfig);
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final FileTypeHelper fileTypeHelper = ComponentUtil.getFileTypeHelper();
        final DocumentHelper documentHelper = ComponentUtil.getDocumentHelper();
        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();
        String url = responseData.getUrl();
        final String indexingTarget = crawlingConfig.getIndexingTarget(url);
        url = pathMappingHelper.replaceUrl(sessionId, url);
        final String mimeType = responseData.getMimeType();

        final Map<String, String> fieldConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.FIELD);
        final Map<String, String> xpathConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.XPATH);

        String urlEncoding;
        final UrlQueue<?> urlQueue = CrawlingParameterUtil.getUrlQueue();
        if (urlQueue != null && urlQueue.getEncoding() != null) {
            urlEncoding = urlQueue.getEncoding();
        } else {
            urlEncoding = responseData.getCharSet();
        }

        // cid
        final String configId = crawlingConfig.getConfigId();
        if (configId != null) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldConfigId(), configId);
        }
        //  expires
        if (documentExpires != null) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldExpires(), documentExpires);
        }
        // lang
        final String lang = systemHelper.normalizeHtmlLang(
                getSingleNodeValue(document, getLangXpath(fessConfig, xpathConfigMap), node -> pruneNode(node, crawlingConfig)));
        if (lang != null) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldLang(), lang);
        }
        // title
        // content
        final String body = getSingleNodeValue(document, getContentXpath(fessConfig, xpathConfigMap),
                prunedContent ? node -> pruneNode(node, crawlingConfig) : node -> node);
        final String fileName = getFileName(url, urlEncoding);
        putResultDataContent(dataMap, responseData, fessConfig, crawlingConfig, documentHelper, body, fileName);
        if ((Constants.TRUE.equalsIgnoreCase(fieldConfigMap.get(fessConfig.getIndexFieldCache()))
                || fessConfig.isCrawlerDocumentCacheEnabled()) && fessConfig.isSupportedDocumentCacheMimetypes(mimeType)) {
            if (responseData.getContentLength() > 0
                    && responseData.getContentLength() <= fessConfig.getCrawlerDocumentCacheMaxSizeAsInteger().longValue()) {
                String charSet = responseData.getCharSet();
                if (charSet == null) {
                    charSet = Constants.UTF_8;
                }
                try (final BufferedInputStream is = new BufferedInputStream(responseData.getResponseBody())) {
                    // cache
                    putResultDataBody(dataMap, fessConfig.getIndexFieldCache(), new String(InputStreamUtil.getBytes(is), charSet));
                    putResultDataBody(dataMap, fessConfig.getIndexFieldHasCache(), Constants.TRUE);
                } catch (final Exception e) {
                    logger.warn("Failed to write a cache: {}:{}", sessionId, responseData, e);
                }
            } else {
                logger.debug("Content size is too large({} > {}): {}", responseData.getContentLength(),
                        fessConfig.getCrawlerDocumentCacheMaxSizeAsInteger(), responseData.getUrl());
            }
        }
        // digest
        final String digest = getSingleNodeValue(document, getDigestXpath(fessConfig, xpathConfigMap), node -> node);
        if (StringUtil.isNotBlank(digest)) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldDigest(), digest);
        } else {
            putResultDataBody(dataMap, fessConfig.getIndexFieldDigest(),
                    documentHelper.getDigest(responseData, body, dataMap, fessConfig.getCrawlerDocumentHtmlMaxDigestLengthAsInteger()));
        }
        // segment
        putResultDataBody(dataMap, fessConfig.getIndexFieldSegment(), sessionId);
        // host
        putResultDataBody(dataMap, fessConfig.getIndexFieldHost(), getHost(url));
        // site
        putResultDataBody(dataMap, fessConfig.getIndexFieldSite(), getSite(url, urlEncoding));
        // filename
        if (StringUtil.isNotBlank(fileName)) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldFilename(), fileName);
        }
        // url
        putResultDataBody(dataMap, fessConfig.getIndexFieldUrl(), url);
        // created
        final Date now = systemHelper.getCurrentTime();
        putResultDataBody(dataMap, fessConfig.getIndexFieldCreated(), now);
        // anchor
        putResultDataBody(dataMap, fessConfig.getIndexFieldAnchor(), getAnchorList(document, responseData));
        // mimetype
        putResultDataBody(dataMap, fessConfig.getIndexFieldMimetype(), mimeType);
        if (fileTypeHelper != null) {
            // filetype
            putResultDataBody(dataMap, fessConfig.getIndexFieldFiletype(), fileTypeHelper.get(mimeType));
        }
        // content_length
        putResultDataBody(dataMap, fessConfig.getIndexFieldContentLength(), Long.toString(responseData.getContentLength()));
        // last_modified
        final Date lastModified = responseData.getLastModified();
        if (lastModified != null) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldLastModified(), lastModified);
            // timestamp
            putResultDataBody(dataMap, fessConfig.getIndexFieldTimestamp(), lastModified);
        } else {
            // timestamp
            putResultDataBody(dataMap, fessConfig.getIndexFieldTimestamp(), now);
        }
        // indexingTarget
        putResultDataBody(dataMap, Constants.INDEXING_TARGET, indexingTarget);
        //  boost
        putResultDataBody(dataMap, fessConfig.getIndexFieldBoost(), crawlingConfig.getDocumentBoost());
        // label: labelType
        putResultDataBody(dataMap, fessConfig.getIndexFieldLabel(), labelTypeHelper.getMatchedLabelValueSet(url));
        // role: roleType
        final List<String> roleTypeList = new ArrayList<>();
        stream(crawlingConfig.getPermissions()).of(stream -> stream.forEach(p -> roleTypeList.add(p)));
        putResultDataBody(dataMap, fessConfig.getIndexFieldRole(), roleTypeList);
        // virtualHosts
        putResultDataBody(dataMap, fessConfig.getIndexFieldVirtualHost(),
                stream(crawlingConfig.getVirtualHosts()).get(stream -> stream.filter(StringUtil::isNotBlank).collect(Collectors.toList())));
        // id
        putResultDataBody(dataMap, fessConfig.getIndexFieldId(), crawlingInfoHelper.generateId(dataMap));
        // parentId
        String parentUrl = responseData.getParentUrl();
        if (StringUtil.isNotBlank(parentUrl)) {
            parentUrl = pathMappingHelper.replaceUrl(sessionId, parentUrl);
            putResultDataBody(dataMap, fessConfig.getIndexFieldUrl(), parentUrl);
            putResultDataBody(dataMap, fessConfig.getIndexFieldParentId(), crawlingInfoHelper.generateId(dataMap));
            putResultDataBody(dataMap, fessConfig.getIndexFieldUrl(), url); // set again
        }
        // thumbnail
        final String thumbnailUrl = getThumbnailUrl(responseData, document);
        if (StringUtil.isNotBlank(thumbnailUrl)) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldThumbnail(), thumbnailUrl);
        }

        // from config
        final String scriptType = crawlingConfig.getScriptType();
        final Map<String, String> scriptConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.SCRIPT);
        xpathConfigMap.entrySet().stream().filter(e -> !e.getKey().startsWith("default.")).forEach(e -> {
            final String key = e.getKey();
            final String value = getSingleNodeValue(document, e.getValue(), node -> pruneNode(node, crawlingConfig));
            putResultDataWithTemplate(dataMap, key, value, scriptConfigMap.get(key), scriptType);
        });
        crawlingConfig.getConfigParameterMap(ConfigName.VALUE).entrySet().stream().forEach(e -> {
            final String key = e.getKey();
            final String value = e.getValue();
            putResultDataWithTemplate(dataMap, key, value, scriptConfigMap.get(key), scriptType);
        });
    }

    protected void putResultDataContent(final Map<String, Object> dataMap, final ResponseData responseData, final FessConfig fessConfig,
            final CrawlingConfig crawlingConfig, final DocumentHelper documentHelper, final String body, final String fileName) {
        final String content = documentHelper.getContent(crawlingConfig, responseData, body, dataMap);
        if (StringUtil.isNotBlank(fileName) && fessConfig.isCrawlerDocumentAppendFilename()) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldContent(), content + " " + fileName);
        } else {
            putResultDataBody(dataMap, fessConfig.getIndexFieldContent(), content);
        }
    }

    protected CrawlingConfig getCrawlingConfig(final ResponseData responseData) {
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        return crawlingConfigHelper.get(responseData.getSessionId());
    }

    protected String getLangXpath(final FessConfig fessConfig, final Map<String, String> xpathConfigMap) {
        final String xpath = xpathConfigMap.get(XPath.DEFAULT_LANG);
        if (StringUtil.isNotBlank(xpath)) {
            return xpath;
        }
        return fessConfig.getCrawlerDocumentHtmlLangXpath();
    }

    protected String getContentXpath(final FessConfig fessConfig, final Map<String, String> xpathConfigMap) {
        final String xpath = xpathConfigMap.get(XPath.DEFAULT_CONTENT);
        if (StringUtil.isNotBlank(xpath)) {
            return xpath;
        }
        return fessConfig.getCrawlerDocumentHtmlContentXpath();
    }

    protected String getDigestXpath(final FessConfig fessConfig, final Map<String, String> xpathConfigMap) {
        final String xpath = xpathConfigMap.get(XPath.DEFAULT_DIGEST);
        if (StringUtil.isNotBlank(xpath)) {
            return xpath;
        }
        return fessConfig.getCrawlerDocumentHtmlDigestXpath();
    }

    protected String getCanonicalUrl(final ResponseData responseData, final Document document) {
        final Map<String, String> configMap = getConfigPrameterMap(responseData, ConfigName.CONFIG);
        String xpath = configMap.get(Config.HTML_CANONICAL_XPATH);
        if (xpath == null) {
            xpath = fessConfig.getCrawlerDocumentHtmlCanonicalXpath();
        }
        if (StringUtil.isBlank(xpath)) {
            return null;
        }
        final String canonicalUrl = getSingleNodeValue(document, xpath, node -> node);
        if (StringUtil.isBlank(canonicalUrl)) {
            return null;
        }
        return normalizeCanonicalUrl(responseData.getUrl(), canonicalUrl);
    }

    protected String normalizeCanonicalUrl(final String baseUrl, final String canonicalUrl) {
        try {
            final URL u = new URL(baseUrl);
            return new URL(u, canonicalUrl.startsWith(":") ? u.getProtocol() + canonicalUrl : canonicalUrl).toString();
        } catch (final MalformedURLException e) {
            logger.warn("Invalid canonical url: {} : {}", baseUrl, canonicalUrl, e);
        }
        return null;
    }

    protected String removeCommentTag(final String content) {
        if (content == null) {
            return StringUtil.EMPTY;
        }
        String value = content;
        int pos = value.indexOf("<!--");
        while (pos >= 0) {
            final int lastPos = value.indexOf("-->", pos);
            if (lastPos < 0) {
                break;
            }
            if (pos == 0) {
                value = " " + value.substring(lastPos + 3);
            } else {
                value = value.substring(0, pos) + " " + value.substring(lastPos + 3);
            }
            pos = value.indexOf("<!--");
        }
        return value;
    }

    protected String getSingleNodeValue(final Document document, final String xpath, final UnaryOperator<Node> pruneFunc) {
        StringBuilder buf = null;
        XPathNodes list = null;
        try {
            list = getXPathAPI().selectNodeList(document, xpath);
            for (int i = 0; i < list.size(); i++) {
                if (buf == null) {
                    buf = new StringBuilder(1000);
                }
                Node node = list.get(i).cloneNode(true);
                if (useGoogleOffOn) {
                    node = processGoogleOffOn(node, new ValueHolder<>(true));
                }
                node = pruneFunc.apply(node);
                parseTextContent(node, buf);
            }
        } catch (final Exception e) {
            logger.warn("Could not parse a value of {}", xpath);
        }
        if (buf == null) {
            return null;
        }
        return buf.toString().trim();
    }

    protected void parseTextContent(final Node node, final StringBuilder buf) {
        if (node.hasChildNodes()) {
            final NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node childNode = nodeList.item(i);
                parseTextContent(childNode, buf);
            }
        } else if (node.getNodeType() == Node.TEXT_NODE) {
            final String value = node.getTextContent();
            if (value != null) {
                final String content = value.trim();
                if (content.length() > 0) {
                    buf.append(' ').append(content);
                }
            }
        }
    }

    protected Node processGoogleOffOn(final Node node, final ValueHolder<Boolean> flag) {
        final NodeList nodeList = node.getChildNodes();
        List<Node> removedNodeList = null;
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == Node.COMMENT_NODE) {
                final String comment = childNode.getNodeValue().trim();
                if (comment.startsWith("googleoff:")) {
                    flag.setValue(false);
                } else if (comment.startsWith("googleon:")) {
                    flag.setValue(true);
                }
            }

            if (!flag.getValue() && childNode.getNodeType() == Node.TEXT_NODE) {
                if (removedNodeList == null) {
                    removedNodeList = new ArrayList<>();
                }
                removedNodeList.add(childNode);
            } else {
                processGoogleOffOn(childNode, flag);
            }
        }

        if (removedNodeList != null) {
            removedNodeList.stream().forEach(n -> node.removeChild(n));
        }

        return node;
    }

    protected Node pruneNode(final Node node, final CrawlingConfig crawlingConfig) {
        PrunedTag[] prunedTags = null;
        if (crawlingConfig != null) {
            final String configId = crawlingConfig.getConfigId();
            prunedTags = prunedTagsCache.get(configId);
            if (prunedTags == null) {
                final Map<String, String> configMap = crawlingConfig.getConfigParameterMap(ConfigName.CONFIG);
                final String value = configMap.get(CrawlingConfig.Param.Config.HTML_PRUNED_TAGS);
                if (StringUtil.isNotBlank(value)) {
                    prunedTags = PrunedTag.parse(value);
                }
                if (prunedTags == null) {
                    prunedTags = fessConfig.getCrawlerDocumentHtmlPrunedTagsAsArray();
                }
                prunedTagsCache.put(configId, prunedTags);
            }
        }
        if (prunedTags == null) {
            prunedTags = fessConfig.getCrawlerDocumentHtmlPrunedTagsAsArray();
        }
        return pruneNodeByTags(node, prunedTags);
    }

    protected Node pruneNodeByTags(final Node node, final PrunedTag[] prunedTags) {
        final NodeList nodeList = node.getChildNodes();
        final List<Node> childNodeList = new ArrayList<>();
        final List<Node> removedNodeList = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node childNode = nodeList.item(i);
            if (isPrunedTag(childNode, prunedTags)) {
                removedNodeList.add(childNode);
            } else {
                childNodeList.add(childNode);
            }
        }

        for (final Node childNode : removedNodeList) {
            node.removeChild(childNode);
        }

        for (final Node childNode : childNodeList) {
            pruneNodeByTags(childNode, prunedTags);
        }

        return node;
    }

    protected boolean isPrunedTag(final Node node, final PrunedTag[] prunedTags) {
        for (final PrunedTag prunedTag : prunedTags) {
            if (prunedTag.matches(node)) {
                return true;
            }
        }
        return false;
    }

    protected String getMultipleNodeValue(final Document document, final String xpath) {
        XPathNodes nodeList = null;
        final StringBuilder buf = new StringBuilder(100);
        try {
            nodeList = getXPathAPI().selectNodeList(document, xpath);
            for (int i = 0; i < nodeList.size(); i++) {
                final Node node = nodeList.get(i);
                buf.append(node.getTextContent());
                buf.append("\n");
            }
        } catch (final Exception e) {
            logger.warn("Could not parse a value of {}", xpath, e);
        }
        return buf.toString().trim();
    }

    protected String replaceDuplicateHost(final String url) {
        try {
            // remove duplicate host
            final DuplicateHostHelper duplicateHostHelper = ComponentUtil.getDuplicateHostHelper();
            return duplicateHostHelper.convert(url);
        } catch (final Exception e) {
            return url;
        }
    }

    protected List<String> getAnchorList(final Document document, final ResponseData responseData) {
        List<RequestData> anchorList = new ArrayList<>();
        final String baseHref = getBaseHref(document);
        try {
            final URL url = getBaseUrl(responseData.getUrl(), baseHref);
            for (final Map.Entry<String, String> entry : childUrlRuleMap.entrySet()) {
                for (final String u : getUrlFromTagAttribute(url, document, entry.getKey(), entry.getValue(), responseData.getCharSet())) {
                    anchorList.add(RequestDataBuilder.newRequestData().get().url(u).build());
                }
            }
            anchorList = convertChildUrlList(anchorList);
        } catch (final Exception e) {
            logger.warn("Could not parse anchor tags.", e);
        }

        final List<String> urlList = new ArrayList<>(anchorList.size());
        for (final RequestData requestData : anchorList) {
            urlList.add(requestData.getUrl());
        }
        return urlList;
    }

    protected URL getBaseUrl(final String currentUrl, final String baseHref) throws MalformedURLException {
        if (baseHref != null) {
            return getURL(currentUrl, baseHref);
        }
        return new URL(currentUrl);
    }

    @Override
    protected List<RequestData> convertChildUrlList(final List<RequestData> urlList) {
        if (urlList != null) {
            final PathMappingHelper pathMappingHelper = getPathMappingHelper();
            for (final RequestData requestData : urlList) {
                String url = requestData.getUrl();
                for (final Map.Entry<String, String> entry : convertUrlMap.entrySet()) {
                    url = url.replaceAll(entry.getKey(), entry.getValue());
                }
                url = pathMappingHelper.replaceUrl(url);
                requestData.setUrl(replaceDuplicateHost(url));
            }
        }
        return urlList;
    }

    protected PathMappingHelper getPathMappingHelper() {
        return ComponentUtil.getPathMappingHelper();
    }

    @Override
    public Object getData(final AccessResultData<?> accessResultData) {
        final byte[] data = accessResultData.getData();
        if (data != null) {
            try {
                return SerializeUtil.fromBinaryToObject(data);
            } catch (final Exception e) {
                throw new CrawlerSystemException("Could not create an instanced from bytes.", e);
            }
        }
        return new HashMap<String, Object>();
    }

    @Override
    protected void addChildUrlFromTagAttribute(final List<String> urlList, final URL url, final String attrValue, final String encoding) {
        final String urlValue = attrValue.trim();
        URL childUrl;
        String u = null;
        try {
            childUrl = new URL(url, urlValue.startsWith(":") ? url.getProtocol() + urlValue : urlValue);
            u = encodeUrl(normalizeUrl(childUrl.toExternalForm()), encoding);
        } catch (final MalformedURLException e) {
            final int pos = urlValue.indexOf(':');
            if (pos > 0 && pos < 10) {
                u = encodeUrl(normalizeUrl(urlValue), encoding);
            }
        }

        if (u == null) {
            logger.warn("Ignored child URL: {} in {}", attrValue, url);
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("{} -> {}", attrValue, u);
        }
        if (StringUtil.isNotBlank(u)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Add Child: {}", u);
            }
            urlList.add(u);
        } else if (logger.isDebugEnabled()) {
            logger.debug("Skip Child: {}", u);
        }
    }

    private boolean isUtf8BomBytes(final byte[] b) {
        return b[0] == (byte) 0xEF && b[1] == (byte) 0xBB && b[2] == (byte) 0xBF;
    }

    public void setUseGoogleOffOn(final boolean useGoogleOffOn) {
        this.useGoogleOffOn = useGoogleOffOn;
    }

    protected String getThumbnailUrl(final ResponseData responseData, final Document document) {
        // TODO PageMap
        try {
            // meta thumbnail
            final Node thumbnailNode = getXPathAPI().selectSingleNode(document, META_NAME_THUMBNAIL_CONTENT);
            if (thumbnailNode != null) {
                final String content = thumbnailNode.getTextContent();
                if (StringUtil.isNotBlank(content)) {
                    final URL thumbnailUrl = getURL(responseData.getUrl(), content);
                    if (thumbnailUrl != null) {
                        return thumbnailUrl.toExternalForm();
                    }
                }
            }

            // meta og:image
            final Node ogImageNode = getXPathAPI().selectSingleNode(document, META_PROPERTY_OGIMAGE_CONTENT);
            if (ogImageNode != null) {
                final String content = ogImageNode.getTextContent();
                if (StringUtil.isNotBlank(content)) {
                    final URL thumbnailUrl = getURL(responseData.getUrl(), content);
                    if (thumbnailUrl != null) {
                        return thumbnailUrl.toExternalForm();
                    }
                }
            }

            final XPathNodes imgNodeList = getXPathAPI().selectNodeList(document, fessConfig.getThumbnailHtmlImageXpath());
            String firstThumbnailUrl = null;
            for (int i = 0; i < imgNodeList.size(); i++) {
                final Node imgNode = imgNodeList.get(i);
                if (logger.isDebugEnabled()) {
                    logger.debug("img tag: {}", imgNode);
                }
                final NamedNodeMap attributes = imgNode.getAttributes();
                final String thumbnailUrl = getThumbnailSrc(responseData.getUrl(), attributes);
                final Integer height = getAttributeAsInteger(attributes, "height");
                final Integer width = getAttributeAsInteger(attributes, "width");
                if (!fessConfig.isThumbnailHtmlImageUrl(thumbnailUrl)) {
                    continue;
                }
                if (height != null && width != null) {
                    try {
                        if (fessConfig.validateThumbnailSize(width, height)) {
                            return thumbnailUrl;
                        }
                    } catch (final Exception e) {
                        logger.debug("Failed to parse {} at {}", imgNode, responseData.getUrl(), e);
                    }
                } else if (firstThumbnailUrl == null) {
                    firstThumbnailUrl = thumbnailUrl;
                }
            }

            if (firstThumbnailUrl != null) {
                return firstThumbnailUrl;
            }
        } catch (final Exception e) {
            logger.warn("Failed to retrieve thumbnail url from {}", responseData.getUrl(), e);
        }
        return null;
    }

    protected String getThumbnailSrc(final String url, final NamedNodeMap attributes) {
        final Node srcNode = attributes.getNamedItem("src");
        if (srcNode != null) {
            try {
                final URL thumbnailUrl = getURL(url, srcNode.getTextContent());
                if (thumbnailUrl != null) {
                    return thumbnailUrl.toExternalForm();
                }
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse thumbnail url for {} : {}", url, attributes, e);
                }
            }
        }
        return null;
    }

    protected Integer getAttributeAsInteger(final NamedNodeMap attributes, final String name) {
        final Node namedItem = attributes.getNamedItem(name);
        if (namedItem == null) {
            return null;
        }
        final String value = namedItem.getTextContent();
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            if (value.endsWith("%") || value.endsWith("px")) {
                return null;
            }
            return 0;
        }
    }

    protected URL getURL(final String currentUrl, final String url) throws MalformedURLException {
        if (url != null) {
            if (url.startsWith("://")) {
                final String protocol = currentUrl.split(":")[0];
                return new URL(protocol + url);
            }
            if (url.startsWith("//")) {
                final String protocol = currentUrl.split(":")[0];
                return new URL(protocol + ":" + url);
            }
            if (url.startsWith("/") || url.indexOf(':') == -1) {
                return new URL(new URL(currentUrl), url);
            }
            return new URL(url);
        }
        return null;
    }

    public void addFieldRule(final String name, final String xpath, final boolean isPruned) {
        addFieldRule(name, xpath);
        fieldPrunedRuleMap.put(name, isPruned);
    }

    public void setConvertUrlMap(final Map<String, String> convertUrlMap) {
        this.convertUrlMap.putAll(convertUrlMap);
    }

    public void addConvertUrl(final String regex, final String replacement) {
        this.convertUrlMap.put(regex, replacement);
    }
}
