/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.stream.Stream;

import javax.xml.xpath.XPathEvaluationResult;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathNodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
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
import org.codelibs.fess.crawler.serializer.DataSerializer;
import org.codelibs.fess.crawler.transformer.impl.XpathTransformer;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.codelibs.fess.crawler.util.FieldConfigs;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.DocumentHelper;
import org.codelibs.fess.helper.DuplicateHostHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.Param.Config;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.Param.XPath;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.PrunedTag;
import org.codelibs.nekohtml.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import jakarta.annotation.PostConstruct;

/**
 * A transformer implementation for processing HTML documents using XPath expressions.
 * This class extends XpathTransformer to provide Fess-specific document processing capabilities
 * including content extraction, metadata processing, and robots tag handling.
 */
public class FessXpathTransformer extends XpathTransformer implements FessTransformer {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(FessXpathTransformer.class);

    /** HTTP header name for robots tag */
    private static final String X_ROBOTS_TAG = "X-Robots-Tag";

    /** XPath expression for extracting thumbnail content from meta tags */
    private static final String META_NAME_THUMBNAIL_CONTENT = "//META[@name=\"thumbnail\" or @name=\"THUMBNAIL\"]/@content";

    /** XPath expression for extracting Open Graph image content from meta tags */
    private static final String META_PROPERTY_OGIMAGE_CONTENT = "//META[@property=\"og:image\"]/@content";

    /** XPath expression for extracting robots content from meta tags */
    private static final String META_NAME_ROBOTS_CONTENT = "//META[@name=\"robots\" or @name=\"ROBOTS\"]/@content";

    /** Robots tag value indicating no indexing or following */
    private static final String ROBOTS_TAG_NONE = "none";

    /** Robots tag value indicating no indexing */
    private static final String ROBOTS_TAG_NOINDEX = "noindex";

    /** Robots tag value indicating no following of links */
    private static final String ROBOTS_TAG_NOFOLLOW = "nofollow";

    /** Size of UTF-8 BOM (Byte Order Mark) in bytes */
    private static final int UTF8_BOM_SIZE = 3;

    /** Flag indicating whether content should be pruned */
    public boolean prunedContent = true;

    /** Map containing URL conversion rules (regex patterns to replacement strings) */
    protected Map<String, String> convertUrlMap = new LinkedHashMap<>();

    /** Fess configuration instance */
    protected FessConfig fessConfig;

    /** Data serializer for converting objects to binary format */
    protected DataSerializer dataSerializer;

    /** Flag indicating whether to process Google on/off comments */
    protected boolean useGoogleOffOn = true;

    /** Map storing field pruning rules */
    protected Map<String, Boolean> fieldPrunedRuleMap = new HashMap<>();

    /** Cache for storing parsed pruned tags by configuration ID */
    protected Map<String, PrunedTag[]> prunedTagsCache = new HashMap<>();

    /**
     * Default constructor.
     */
    public FessXpathTransformer() {
        super();
    }

    /**
     * Initializes the transformer after dependency injection.
     * Sets up the Fess configuration and data serializer components.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initialize {}", this.getClass().getSimpleName());
        }
        fessConfig = ComponentUtil.getFessConfig();
        dataSerializer = ComponentUtil.getComponent("dataSerializer");
    }

    /**
     * Returns the Fess configuration instance.
     *
     * @return the Fess configuration
     */
    @Override
    public FessConfig getFessConfig() {
        return fessConfig;
    }

    /**
     * Returns the logger instance for this class.
     *
     * @return the logger instance
     */
    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * Stores parsed data from response into result data.
     * Processes HTML content using XPath expressions and handles robots tags.
     *
     * @param responseData the response data from crawling
     * @param resultData the result data to store processed information
     */
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

        Map<String, Object> dataMap = new LinkedHashMap<>();
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

        dataMap = processAdditionalData(dataMap, responseData, document);
        normalizeData(responseData, dataMap);

        try {
            resultData.setRawData(dataMap);
            resultData.setSerializer(dataSerializer::fromObjectToBinary);
        } catch (final Exception e) {
            throw new CrawlingAccessException("Could not serialize object: " + responseData.getUrl(), e);
        }
        resultData.setEncoding(charsetName);
    }

    /**
     * Normalizes the extracted data, particularly handling title normalization.
     *
     * @param responseData the response data from crawling
     * @param dataMap the data map containing extracted field values
     */
    protected void normalizeData(final ResponseData responseData, final Map<String, Object> dataMap) {
        final Object titleObj = dataMap.get(fessConfig.getIndexFieldTitle());
        if (titleObj != null) {
            dataMap.put(fessConfig.getIndexFieldTitle(),
                    ComponentUtil.getDocumentHelper().getTitle(responseData, titleObj.toString(), dataMap));
        }
    }

    /**
     * Processes robots meta tags in the HTML document.
     * Handles noindex, nofollow, and none directives.
     *
     * @param responseData the response data from crawling
     * @param resultData the result data to store processed information
     * @param document the parsed HTML document
     */
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

    /**
     * Processes X-Robots-Tag HTTP headers.
     * Handles noindex, nofollow, and none directives from HTTP headers.
     *
     * @param responseData the response data from crawling
     * @param resultData the result data to store processed information
     */
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
        responseData.getMetaDataMap()
                .entrySet()
                .stream()
                .filter(e -> X_ROBOTS_TAG.equalsIgnoreCase(e.getKey()) && e.getValue() != null)
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

    /**
     * Retrieves configuration parameter map for the given configuration name.
     *
     * @param responseData the response data from crawling
     * @param config the configuration name to retrieve
     * @return map of configuration parameters
     */
    protected Map<String, String> getConfigPrameterMap(final ResponseData responseData, final ConfigName config) {
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(responseData.getSessionId());
        return crawlingConfig.getConfigParameterMap(config);
    }

    /**
     * Validates if the given URL string is a valid URL.
     *
     * @param urlStr the URL string to validate
     * @return true if the URL is valid, false otherwise
     */
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
            final URI uri = URI.create(value);
            final String host = uri.getHost();
            if (StringUtil.isBlank(host) || "http".equalsIgnoreCase(host) || "https".equalsIgnoreCase(host)) {
                return false;
            }
        } catch (final IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * Validates if the canonical URL is valid relative to the original URL.
     * Specifically checks for HTTPS to HTTP downgrades.
     *
     * @param url the original URL
     * @param canonicalUrl the canonical URL to validate
     * @return true if the canonical URL is valid, false otherwise
     */
    protected boolean isValidCanonicalUrl(final String url, final String canonicalUrl) {
        if (url.startsWith("https:") && canonicalUrl.startsWith("http:")) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid Canonical Url(https->http): {} -> {}", url, canonicalUrl);
            }
            return false;
        }
        return true;
    }

    /**
     * Processes additional data including canonical URLs, content extraction, and metadata.
     *
     * @param dataMap the data map to populate
     * @param responseData the response data from crawling
     * @param document the parsed HTML document
     * @return the processed data map
     */
    protected Map<String, Object> processAdditionalData(final Map<String, Object> dataMap, final ResponseData responseData,
            final Document document) {
        // canonical
        final String canonicalUrl = getCanonicalUrl(responseData, document);
        if (canonicalUrl != null && !canonicalUrl.equalsIgnoreCase(responseData.getUrl()) && isValidUrl(canonicalUrl)
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

        final FieldConfigs fieldConfigs = new FieldConfigs(crawlingConfig.getConfigParameterMap(ConfigName.FIELD));
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
        if ((fieldConfigs.getConfig(fessConfig.getIndexFieldCache())
                .map(org.codelibs.fess.crawler.util.FieldConfigs.Config::isCache)
                .orElse(false) || fessConfig.isCrawlerDocumentCacheEnabled()) && fessConfig.isSupportedDocumentCacheMimetypes(mimeType)) {
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

        return processFieldConfigs(dataMap, fieldConfigs);
    }

    /**
     * Puts content data into the result data map.
     *
     * @param dataMap the data map to populate
     * @param responseData the response data from crawling
     * @param fessConfig the Fess configuration
     * @param crawlingConfig the crawling configuration
     * @param documentHelper the document helper for content processing
     * @param body the extracted body content
     * @param fileName the file name if applicable
     */
    protected void putResultDataContent(final Map<String, Object> dataMap, final ResponseData responseData, final FessConfig fessConfig,
            final CrawlingConfig crawlingConfig, final DocumentHelper documentHelper, final String body, final String fileName) {
        final String content = documentHelper.getContent(crawlingConfig, responseData, body, dataMap);
        if (StringUtil.isNotBlank(fileName) && fessConfig.isCrawlerDocumentAppendFilename()) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldContent(), content + " " + fileName);
        } else {
            putResultDataBody(dataMap, fessConfig.getIndexFieldContent(), content);
        }
    }

    /**
     * Retrieves the crawling configuration for the given response data.
     *
     * @param responseData the response data from crawling
     * @return the crawling configuration
     */
    protected CrawlingConfig getCrawlingConfig(final ResponseData responseData) {
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        return crawlingConfigHelper.get(responseData.getSessionId());
    }

    /**
     * Gets the XPath expression for extracting language information.
     *
     * @param fessConfig the Fess configuration
     * @param xpathConfigMap the XPath configuration map
     * @return the XPath expression for language extraction
     */
    protected String getLangXpath(final FessConfig fessConfig, final Map<String, String> xpathConfigMap) {
        final String xpath = xpathConfigMap.get(XPath.DEFAULT_LANG);
        if (StringUtil.isNotBlank(xpath)) {
            return xpath;
        }
        return fessConfig.getCrawlerDocumentHtmlLangXpath();
    }

    /**
     * Gets the XPath expression for extracting content.
     *
     * @param fessConfig the Fess configuration
     * @param xpathConfigMap the XPath configuration map
     * @return the XPath expression for content extraction
     */
    protected String getContentXpath(final FessConfig fessConfig, final Map<String, String> xpathConfigMap) {
        final String xpath = xpathConfigMap.get(XPath.DEFAULT_CONTENT);
        if (StringUtil.isNotBlank(xpath)) {
            return xpath;
        }
        return fessConfig.getCrawlerDocumentHtmlContentXpath();
    }

    /**
     * Gets the XPath expression for extracting digest information.
     *
     * @param fessConfig the Fess configuration
     * @param xpathConfigMap the XPath configuration map
     * @return the XPath expression for digest extraction
     */
    protected String getDigestXpath(final FessConfig fessConfig, final Map<String, String> xpathConfigMap) {
        final String xpath = xpathConfigMap.get(XPath.DEFAULT_DIGEST);
        if (StringUtil.isNotBlank(xpath)) {
            return xpath;
        }
        return fessConfig.getCrawlerDocumentHtmlDigestXpath();
    }

    /**
     * Extracts the canonical URL from the HTML document.
     *
     * @param responseData the response data from crawling
     * @param document the parsed HTML document
     * @return the canonical URL if found, null otherwise
     */
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

    /**
     * Normalizes the canonical URL relative to the base URL.
     *
     * @param baseUrl the base URL
     * @param canonicalUrl the canonical URL to normalize
     * @return the normalized canonical URL
     */
    protected String normalizeCanonicalUrl(final String baseUrl, final String canonicalUrl) {
        try {
            final URI baseUri = URI.create(baseUrl);
            final String resolveTarget = canonicalUrl.startsWith(":") ? baseUri.getScheme() + canonicalUrl : canonicalUrl;
            return baseUri.resolve(resolveTarget).toString();
        } catch (final IllegalArgumentException e) {
            logger.warn("Invalid canonical url: {} : {}", baseUrl, canonicalUrl, e);
        }
        return null;
    }

    /**
     * Removes HTML comment tags from the content.
     *
     * @param content the content to process
     * @return the content with comment tags removed
     */
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

    /**
     * Extracts text content from a single node using XPath expression.
     *
     * @param document the parsed HTML document
     * @param xpath the XPath expression to evaluate
     * @param pruneFunc the function to apply for node pruning
     * @return the extracted text content
     */
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

    /**
     * Recursively parses text content from a node and its children.
     *
     * @param node the node to parse
     * @param buf the StringBuilder to append content to
     */
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

    /**
     * Processes Google on/off comment directives in the node.
     *
     * @param node the node to process
     * @param flag the flag indicating whether content should be included
     * @return the processed node
     */
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

    /**
     * Prunes unwanted tags from the node based on configuration.
     *
     * @param node the node to prune
     * @param crawlingConfig the crawling configuration containing pruning rules
     * @return the pruned node
     */
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

    /**
     * Prunes nodes based on the specified pruned tags.
     *
     * @param node the node to prune
     * @param prunedTags the array of pruned tag configurations
     * @return the pruned node
     */
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

    /**
     * Checks if a node matches any of the pruned tag configurations.
     *
     * @param node the node to check
     * @param prunedTags the array of pruned tag configurations
     * @return true if the node should be pruned, false otherwise
     */
    protected boolean isPrunedTag(final Node node, final PrunedTag[] prunedTags) {
        for (final PrunedTag prunedTag : prunedTags) {
            if (prunedTag.matches(node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts text content from multiple nodes using XPath expression.
     *
     * @param document the parsed HTML document
     * @param xpath the XPath expression to evaluate
     * @return the concatenated text content from all matching nodes
     */
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

    /**
     * Replaces duplicate hosts in the URL using the duplicate host helper.
     *
     * @param url the URL to process
     * @return the URL with duplicate hosts replaced
     */
    protected String replaceDuplicateHost(final String url) {
        try {
            // remove duplicate host
            final DuplicateHostHelper duplicateHostHelper = ComponentUtil.getDuplicateHostHelper();
            return duplicateHostHelper.convert(url);
        } catch (final Exception e) {
            return url;
        }
    }

    /**
     * Extracts anchor URLs from the HTML document.
     *
     * @param document the parsed HTML document
     * @param responseData the response data from crawling
     * @return list of anchor URLs found in the document
     */
    protected List<String> getAnchorList(final Document document, final ResponseData responseData) {
        List<RequestData> anchorList = new ArrayList<>();
        final String baseHref = getBaseHref(document);
        try {
            final java.net.URL url = getBaseUrl(responseData.getUrl(), baseHref);
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

    /**
     * Gets the base URL for resolving relative URLs.
     *
     * @param currentUrl the current URL
     * @param baseHref the base href value from HTML
     * @return the base URL
     * @throws Exception if the URL is malformed
     */
    protected java.net.URL getBaseUrl(final String currentUrl, final String baseHref) throws Exception {
        if (baseHref != null) {
            final URI uri = getURI(currentUrl, baseHref);
            return uri.toURL();
        }
        return URI.create(currentUrl).toURL();
    }

    /**
     * Gets child URL extraction rules from configuration.
     *
     * @param responseData the response data from crawling
     * @param resultData the result data
     * @return stream of tag-attribute pairs for URL extraction
     */
    @Override
    protected Stream<Pair<String, String>> getChildUrlRules(final ResponseData responseData, final ResultData resultData) {
        final Map<String, String> configMap = getConfigPrameterMap(responseData, ConfigName.CONFIG);
        final String ruleString = configMap.get(Config.HTML_CHILD_URL_RULES);
        if (StringUtil.isBlank(ruleString)) {
            return childUrlRuleMap.entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue()));
        }
        return Arrays.stream(ruleString.split(","))
                .map(s -> s.split(":"))
                .filter(v -> v.length == 2)
                .map(v -> new Pair<String, String>(v[0].trim(), v[1].trim()));
    }

    /**
     * Converts and processes child URLs using path mapping and URL conversion rules.
     *
     * @param urlList the list of request data containing URLs to convert
     * @return the converted list of request data
     */
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

    /**
     * Gets the path mapping helper for URL transformations.
     *
     * @return the path mapping helper instance
     */
    protected PathMappingHelper getPathMappingHelper() {
        return ComponentUtil.getPathMappingHelper();
    }

    /**
     * Deserializes data from access result data.
     *
     * @param accessResultData the access result data containing serialized data
     * @return the deserialized object
     */
    @Override
    public Object getData(final AccessResultData<?> accessResultData) {
        final byte[] data = accessResultData.getData();
        if (data != null) {
            try {
                return dataSerializer.fromBinaryToObject(data);
            } catch (final Exception e) {
                throw new CrawlerSystemException("Could not create an instanced from bytes.", e);
            }
        }
        return new HashMap<String, Object>();
    }

    /**
     * Adds child URL from tag attribute value.
     *
     * @param urlList the list to add URLs to
     * @param url the base URL for resolving relative URLs
     * @param attrValue the attribute value containing the URL
     * @param encoding the character encoding
     */
    @Override
    protected void addChildUrlFromTagAttribute(final List<String> urlList, final java.net.URL url, final String attrValue,
            final String encoding) {
        final String urlValue = attrValue.trim();
        String u = null;
        try {
            // Convert URL to URI for modern URL handling
            final URI uri = url.toURI();
            final String resolveTarget = urlValue.startsWith(":") ? uri.getScheme() + urlValue : urlValue;
            final URI childUri = uri.resolve(resolveTarget);
            u = encodeUrl(normalizeUrl(childUri.toString()), encoding);
        } catch (final IllegalArgumentException | URISyntaxException e) {
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

    /**
     * Checks if the byte array contains UTF-8 BOM (Byte Order Mark).
     *
     * @param b the byte array to check
     * @return true if the bytes represent UTF-8 BOM, false otherwise
     */
    private boolean isUtf8BomBytes(final byte[] b) {
        return b[0] == (byte) 0xEF && b[1] == (byte) 0xBB && b[2] == (byte) 0xBF;
    }

    /**
     * Sets whether to process Google on/off comment directives.
     *
     * @param useGoogleOffOn true to enable Google on/off processing, false to disable
     */
    public void setUseGoogleOffOn(final boolean useGoogleOffOn) {
        this.useGoogleOffOn = useGoogleOffOn;
    }

    /**
     * Extracts thumbnail URL from the HTML document.
     * Looks for thumbnail meta tags, Open Graph images, and image tags.
     *
     * @param responseData the response data from crawling
     * @param document the parsed HTML document
     * @return the thumbnail URL if found, null otherwise
     */
    protected String getThumbnailUrl(final ResponseData responseData, final Document document) {
        // TODO PageMap
        try {
            // meta thumbnail
            final Node thumbnailNode = getXPathAPI().selectSingleNode(document, META_NAME_THUMBNAIL_CONTENT);
            if (thumbnailNode != null) {
                final String content = thumbnailNode.getTextContent();
                if (StringUtil.isNotBlank(content)) {
                    final URI thumbnailUri = getURI(responseData.getUrl(), content);
                    if (thumbnailUri != null) {
                        return thumbnailUri.toString();
                    }
                }
            }

            // meta og:image
            final Node ogImageNode = getXPathAPI().selectSingleNode(document, META_PROPERTY_OGIMAGE_CONTENT);
            if (ogImageNode != null) {
                final String content = ogImageNode.getTextContent();
                if (StringUtil.isNotBlank(content)) {
                    final URI thumbnailUri = getURI(responseData.getUrl(), content);
                    if (thumbnailUri != null) {
                        return thumbnailUri.toString();
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

    /**
     * Extracts thumbnail source URL from image tag attributes.
     *
     * @param url the base URL for resolving relative URLs
     * @param attributes the named node map of image tag attributes
     * @return the thumbnail source URL if found, null otherwise
     */
    protected String getThumbnailSrc(final String url, final NamedNodeMap attributes) {
        final Node srcNode = attributes.getNamedItem("src");
        if (srcNode != null) {
            try {
                final URI thumbnailUri = getURI(url, srcNode.getTextContent());
                if (thumbnailUri != null) {
                    return thumbnailUri.toString();
                }
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse thumbnail url for {} : {}", url, attributes, e);
                }
            }
        }
        return null;
    }

    /**
     * Gets an attribute value as an integer.
     *
     * @param attributes the named node map of attributes
     * @param name the attribute name
     * @return the attribute value as Integer, null if not found or not parseable
     */
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

    /**
     * Creates a URI object from the current URL and a relative or absolute URL string.
     *
     * @param currentUrl the current URL as base
     * @param url the URL string to process
     * @return the URI object
     * @throws URISyntaxException if the URI is malformed
     */
    protected URI getURI(final String currentUrl, final String url) throws URISyntaxException {
        if (url != null) {
            if (url.startsWith("://")) {
                final String protocol = currentUrl.split(":")[0];
                return URI.create(protocol + url);
            }
            if (url.startsWith("//")) {
                final String protocol = currentUrl.split(":")[0];
                return URI.create(protocol + ":" + url);
            }
            if (url.startsWith("/") || url.indexOf(':') == -1) {
                return URI.create(currentUrl).resolve(url);
            }
            return URI.create(url);
        }
        return null;
    }

    /**
     * Adds a field extraction rule with pruning option.
     *
     * @param name the field name
     * @param xpath the XPath expression for extraction
     * @param isPruned whether the extracted content should be pruned
     */
    public void addFieldRule(final String name, final String xpath, final boolean isPruned) {
        addFieldRule(name, xpath);
        fieldPrunedRuleMap.put(name, isPruned);
    }

    /**
     * Sets the URL conversion map for transforming URLs.
     *
     * @param convertUrlMap the map of regex patterns to replacement strings
     */
    public void setConvertUrlMap(final Map<String, String> convertUrlMap) {
        this.convertUrlMap.putAll(convertUrlMap);
    }

    /**
     * Adds a URL conversion rule.
     *
     * @param regex the regular expression pattern to match
     * @param replacement the replacement string
     */
    public void addConvertUrl(final String regex, final String replacement) {
        convertUrlMap.put(regex, replacement);
    }
}
