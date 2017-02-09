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

import javax.annotation.PostConstruct;
import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.XObject;
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
import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class FessXpathTransformer extends XpathTransformer implements FessTransformer {
    private static final Logger logger = LoggerFactory.getLogger(FessXpathTransformer.class);

    private static final String META_NAME_ROBOTS_CONTENT = "//META[@name=\"robots\" or @name=\"ROBOTS\"]/@content";

    private static final String META_ROBOTS_NONE = "none";

    private static final String META_ROBOTS_NOINDEX = "noindex";

    private static final String META_ROBOTS_NOFOLLOW = "nofollow";

    private static final int UTF8_BOM_SIZE = 3;

    public boolean prunedContent = true;

    public Map<String, String> convertUrlMap = new HashMap<>();

    protected FessConfig fessConfig;

    protected boolean useGoogleOffOn = true;

    @PostConstruct
    public void init() {
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

        if (!fessConfig.isCrawlerIgnoreMetaRobots()) {
            processMetaRobots(responseData, resultData, document);
        }

        final Map<String, Object> dataMap = new LinkedHashMap<>();
        for (final Map.Entry<String, String> entry : fieldRuleMap.entrySet()) {
            final String path = entry.getValue();
            try {
                final XObject xObj = getXPathAPI().eval(document, path);
                final int type = xObj.getType();
                switch (type) {
                case XObject.CLASS_BOOLEAN:
                    final boolean b = xObj.bool();
                    putResultDataBody(dataMap, entry.getKey(), Boolean.toString(b));
                    break;
                case XObject.CLASS_NUMBER:
                    final double d = xObj.num();
                    putResultDataBody(dataMap, entry.getKey(), Double.toString(d));
                    break;
                case XObject.CLASS_STRING:
                    final String str = xObj.str();
                    putResultDataBody(dataMap, entry.getKey(), str);
                    break;
                case XObject.CLASS_NULL:
                case XObject.CLASS_UNKNOWN:
                case XObject.CLASS_NODESET:
                case XObject.CLASS_RTREEFRAG:
                case XObject.CLASS_UNRESOLVEDVARIABLE:
                default:
                    final Node value = getXPathAPI().selectSingleNode(document, entry.getValue());
                    putResultDataBody(dataMap, entry.getKey(), value != null ? value.getTextContent() : null);
                    break;
                }
            } catch (final TransformerException e) {
                logger.warn("Could not parse a value of " + entry.getKey() + ":" + entry.getValue());
            }
        }

        putAdditionalData(dataMap, responseData, document);

        try {
            resultData.setData(SerializeUtil.fromObjectToBinary(dataMap));
        } catch (final Exception e) {
            throw new CrawlingAccessException("Could not serialize object: " + responseData.getUrl(), e);
        }
        resultData.setEncoding(charsetName);
    }

    protected void processMetaRobots(final ResponseData responseData, final ResultData resultData, final Document document) {
        try {
            final Node value = getXPathAPI().selectSingleNode(document, META_NAME_ROBOTS_CONTENT);
            if (value != null) {
                final String content = value.getTextContent().toLowerCase(Locale.ROOT);
                boolean noindex = false;
                boolean nofollow = false;
                if (content.contains(META_ROBOTS_NONE)) {
                    noindex = true;
                    nofollow = true;
                } else {
                    if (content.contains(META_ROBOTS_NOINDEX)) {
                        noindex = true;
                    }
                    if (content.contains(META_ROBOTS_NOFOLLOW)) {
                        nofollow = true;
                    }
                }

                if (noindex && nofollow) {
                    logger.info("META(robots=noindex,nofollow): " + responseData.getUrl());
                    throw new ChildUrlsException(Collections.emptySet(), "#processMetaRobots(Document)");
                } else if (noindex) {
                    logger.info("META(robots=noindex): " + responseData.getUrl());
                    storeChildUrls(responseData, resultData);
                    throw new ChildUrlsException(resultData.getChildUrlSet(), "#processMetaRobots(Document)");
                } else if (nofollow) {
                    logger.info("META(robots=nofollow): " + responseData.getUrl());
                    responseData.setNoFollow(true);
                }
            }
        } catch (final TransformerException e) {
            logger.warn("Could not parse a value of " + META_NAME_ROBOTS_CONTENT);
        }

    }

    protected void putAdditionalData(final Map<String, Object> dataMap, final ResponseData responseData, final Document document) {
        // canonical
        if (StringUtil.isNotBlank(fessConfig.getCrawlerDocumentHtmlCannonicalXpath())) {
            final String canonicalUrl = getCanonicalUrl(responseData, document);
            if (canonicalUrl != null && !canonicalUrl.equals(responseData.getUrl())) {
                final Set<RequestData> childUrlSet = new HashSet<>();
                childUrlSet.add(RequestDataBuilder.newRequestData().get().url(canonicalUrl).build());
                throw new ChildUrlsException(childUrlSet, this.getClass().getName()
                        + "#putAdditionalData(Map<String, Object>, ResponseData, Document)");
            }
        }

        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();
        final String sessionId = crawlingInfoHelper.getCanonicalSessionId(responseData.getSessionId());
        final PathMappingHelper pathMappingHelper = ComponentUtil.getPathMappingHelper();
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(responseData.getSessionId());
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
        final String lang = systemHelper.normalizeLang(getSingleNodeValue(document, getLangXpath(fessConfig, xpathConfigMap), true));
        if (lang != null) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldLang(), lang);
        }
        // title
        // content
        final String body = getSingleNodeValue(document, getContentXpath(fessConfig, xpathConfigMap), prunedContent);
        putResultDataBody(dataMap, fessConfig.getIndexFieldContent(), documentHelper.getContent(responseData, body, dataMap));
        if ((Constants.TRUE.equalsIgnoreCase(fieldConfigMap.get(fessConfig.getIndexFieldCache())) || fessConfig
                .isCrawlerDocumentCacheEnabled()) && fessConfig.isSupportedDocumentCacheMimetypes(mimeType)) {
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
                    logger.warn("Failed to write a cache: " + sessionId + ":" + responseData, e);
                }
            } else {
                logger.debug("Content size is too large({} > {}): {}", responseData.getContentLength(),
                        fessConfig.getCrawlerDocumentCacheMaxSizeAsInteger(), responseData.getUrl());
            }
        }
        // digest
        final String digest = getSingleNodeValue(document, getDigestXpath(fessConfig, xpathConfigMap), false);
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
        final String fileName = getFileName(url, urlEncoding);
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
        final Set<String> labelTypeSet = new HashSet<>();
        for (final String labelType : crawlingConfig.getLabelTypeValues()) {
            labelTypeSet.add(labelType);
        }
        labelTypeSet.addAll(labelTypeHelper.getMatchedLabelValueSet(url));
        putResultDataBody(dataMap, fessConfig.getIndexFieldLabel(), labelTypeSet);
        // role: roleType
        final List<String> roleTypeList = new ArrayList<>();
        stream(crawlingConfig.getPermissions()).of(stream -> stream.forEach(p -> roleTypeList.add(p)));
        putResultDataBody(dataMap, fessConfig.getIndexFieldRole(), roleTypeList);
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

        // from config
        final Map<String, String> scriptConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.SCRIPT);
        xpathConfigMap.entrySet().stream().filter(e -> !e.getKey().startsWith("default.")).forEach(e -> {
            final String key = e.getKey();
            final String value = getSingleNodeValue(document, e.getValue(), true);
            putResultDataWithTemplate(dataMap, key, value, scriptConfigMap.get(key));
        });
        crawlingConfig.getConfigParameterMap(ConfigName.VALUE).entrySet().stream().forEach(e -> {
            final String key = e.getKey();
            final String value = e.getValue();
            putResultDataWithTemplate(dataMap, key, value, scriptConfigMap.get(key));
        });
    }

    protected String getLangXpath(final FessConfig fessConfig, final Map<String, String> xpathConfigMap) {
        final String xpath = xpathConfigMap.get("default.lang");
        if (StringUtil.isNotBlank(xpath)) {
            return xpath;
        }
        return fessConfig.getCrawlerDocumentHtmlLangXpath();
    }

    protected String getContentXpath(final FessConfig fessConfig, final Map<String, String> xpathConfigMap) {
        final String xpath = xpathConfigMap.get("default.content");
        if (StringUtil.isNotBlank(xpath)) {
            return xpath;
        }
        return fessConfig.getCrawlerDocumentHtmlContentXpath();
    }

    protected String getDigestXpath(final FessConfig fessConfig, final Map<String, String> xpathConfigMap) {
        final String xpath = xpathConfigMap.get("default.digest");
        if (StringUtil.isNotBlank(xpath)) {
            return xpath;
        }
        return fessConfig.getCrawlerDocumentHtmlDigestXpath();
    }

    protected String getCanonicalUrl(final ResponseData responseData, final Document document) {
        final String canonicalUrl = getSingleNodeValue(document, fessConfig.getCrawlerDocumentHtmlCannonicalXpath(), false);
        if (StringUtil.isBlank(canonicalUrl)) {
            return null;
        }
        if (canonicalUrl.startsWith("/")) {
            return normalizeCanonicalUrl(responseData.getUrl(), canonicalUrl);
        }
        return canonicalUrl;
    }

    protected String normalizeCanonicalUrl(final String baseUrl, final String canonicalUrl) {
        try {
            return new URL(new URL(baseUrl), canonicalUrl).toString();
        } catch (final MalformedURLException e) {
            logger.warn("Invalid canonical url: " + baseUrl + " : " + canonicalUrl, e);
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
            if (lastPos >= 0) {
                if (pos == 0) {
                    value = " " + value.substring(lastPos + 3);
                } else {
                    value = value.substring(0, pos) + " " + value.substring(lastPos + 3);
                }
            } else {
                break;
            }
            pos = value.indexOf("<!--");
        }
        return value;
    }

    protected String getSingleNodeValue(final Document document, final String xpath, final boolean pruned) {
        StringBuilder buf = null;
        NodeList list = null;
        try {
            list = getXPathAPI().selectNodeList(document, xpath);
            for (int i = 0; i < list.getLength(); i++) {
                if (buf == null) {
                    buf = new StringBuilder(1000);
                }
                Node node = list.item(i).cloneNode(true);
                if (useGoogleOffOn) {
                    node = processGoogleOffOn(node, new ValueHolder<>(true));
                }
                if (pruned) {
                    node = pruneNode(node);
                }
                parseTextContent(node, buf);
            }
        } catch (final Exception e) {
            logger.warn("Could not parse a value of " + xpath);
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

    protected Node pruneNode(final Node node) {
        final NodeList nodeList = node.getChildNodes();
        final List<Node> childNodeList = new ArrayList<>();
        final List<Node> removedNodeList = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node childNode = nodeList.item(i);
            if (isPrunedTag(childNode)) {
                removedNodeList.add(childNode);
            } else {
                childNodeList.add(childNode);
            }
        }

        for (final Node childNode : removedNodeList) {
            node.removeChild(childNode);
        }

        for (final Node childNode : childNodeList) {
            pruneNode(childNode);
        }

        return node;
    }

    protected boolean isPrunedTag(final Node node) {
        for (final PrunedTag prunedTag : fessConfig.getCrawlerDocumentHtmlPrunedTagsAsArray()) {
            if (prunedTag.matches(node)) {
                return true;
            }
        }
        return false;
    }

    protected String getMultipleNodeValue(final Document document, final String xpath) {
        NodeList nodeList = null;
        final StringBuilder buf = new StringBuilder(100);
        try {
            nodeList = getXPathAPI().selectNodeList(document, xpath);
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                buf.append(node.getTextContent());
                buf.append("\n");
            }
        } catch (final Exception e) {
            logger.warn("Could not parse a value of " + xpath);
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
            final URL url = new URL(baseHref != null ? baseHref : responseData.getUrl());
            for (final Map.Entry<String, String> entry : childUrlRuleMap.entrySet()) {
                for (final String u : getUrlFromTagAttribute(url, document, entry.getKey(), entry.getValue(), responseData.getCharSet())) {
                    anchorList.add(RequestDataBuilder.newRequestData().get().url(u).build());
                }
            }
            anchorList = convertChildUrlList(anchorList);
        } catch (final Exception e) {
            logger.warn("Could not parse anchor tags.", e);
            //        } finally {
            //            xpathAPI.remove();
        }

        final List<String> urlList = new ArrayList<>(anchorList.size());
        for (final RequestData requestData : anchorList) {
            urlList.add(requestData.getUrl());
        }
        return urlList;
    }

    @Override
    protected List<RequestData> convertChildUrlList(final List<RequestData> urlList) {
        if (urlList != null) {
            for (final RequestData requestData : urlList) {
                String url = requestData.getUrl();
                for (final Map.Entry<String, String> entry : convertUrlMap.entrySet()) {
                    url = url.replaceAll(entry.getKey(), entry.getValue());
                }
                requestData.setUrl(replaceDuplicateHost(url));
            }
        }
        return urlList;
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
    protected boolean isValidPath(final String path) {
        return super.isValidPath(path);
    }

    @Override
    protected void addChildUrlFromTagAttribute(final List<String> urlList, final URL url, final String attrValue, final String encoding) {
        final String urlValue = attrValue.trim();
        URL childUrl;
        String u = null;
        try {
            childUrl = new URL(url, urlValue);
            u = encodeUrl(normalizeUrl(childUrl.toExternalForm()), encoding);
        } catch (final MalformedURLException e) {
            final int pos = urlValue.indexOf(':');
            if (pos > 0 && pos < 10) {
                u = encodeUrl(normalizeUrl(urlValue), encoding);
            }
        }

        if (u == null) {
            logger.warn("Ignored child URL: " + attrValue + " in " + url);
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug(attrValue + " -> " + u);
        }
        if (StringUtil.isNotBlank(u)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Add Child: " + u);
            }
            urlList.add(u);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Skip Child: " + u);
            }
        }
    }

    private boolean isUtf8BomBytes(final byte[] b) {
        return b[0] == (byte) 0xEF && b[1] == (byte) 0xBB && b[2] == (byte) 0xBF;
    }

    public void setUseGoogleOffOn(final boolean useGoogleOffOn) {
        this.useGoogleOffOn = useGoogleOffOn;
    }

}
