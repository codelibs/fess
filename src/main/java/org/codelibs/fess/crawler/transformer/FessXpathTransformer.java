/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.objects.XObject;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.io.SerializeUtil;
import org.codelibs.core.lang.StringUtil;
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
import org.codelibs.fess.crawler.util.ResponseDataUtil;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.DuplicateHostHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class FessXpathTransformer extends XpathTransformer implements FessTransformer {
    private static final Logger logger = LoggerFactory.getLogger(FessXpathTransformer.class);

    private static final int UTF8_BOM_SIZE = 3;

    public boolean prunedCacheContent = true;

    public Map<String, String> convertUrlMap = new HashMap<>();

    protected FessConfig fessConfig;

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
        final File tempFile = ResponseDataUtil.createResponseBodyFile(responseData);
        try {
            final DOMParser parser = getDomParser();
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(tempFile));
                final byte[] bomBytes = new byte[UTF8_BOM_SIZE];
                bis.mark(UTF8_BOM_SIZE);
                bis.read(bomBytes); // NOSONAR
                if (!isUtf8BomBytes(bomBytes)) {
                    bis.reset();
                }
                final InputSource is = new InputSource(bis);
                if (responseData.getCharSet() != null) {
                    is.setEncoding(responseData.getCharSet());
                }
                parser.parse(is);
            } catch (final Exception e) {
                throw new CrawlingAccessException("Could not parse " + responseData.getUrl(), e);
            } finally {
                IOUtils.closeQuietly(bis);
            }

            final Document document = parser.getDocument();

            final Map<String, Object> dataMap = new HashMap<String, Object>();
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

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(tempFile);
                responseData.setResponseBody(fis);
                putAdditionalData(dataMap, responseData, document);
            } catch (final FileNotFoundException e) {
                logger.warn(tempFile + " does not exist.", e);
                putAdditionalData(dataMap, responseData, document);
            } finally {
                IOUtils.closeQuietly(fis);
            }

            try {
                resultData.setData(SerializeUtil.fromObjectToBinary(dataMap));
            } catch (final Exception e) {
                throw new CrawlingAccessException("Could not serialize object: " + responseData.getUrl(), e);
            }
            resultData.setEncoding(charsetName);
        } finally {
            if (!tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
        }
    }

    protected void putAdditionalData(final Map<String, Object> dataMap, final ResponseData responseData, final Document document) {
        // canonical
        if (StringUtil.isNotBlank(fessConfig.getCrawlerDocumentHtmlCannonicalXpath())) {
            final String canonicalUrl = getCanonicalUrl(responseData, document);
            if (canonicalUrl != null && !canonicalUrl.equals(responseData.getUrl())) {
                final Set<RequestData> childUrlSet = new HashSet<>();
                childUrlSet.add(RequestDataBuilder.newRequestData().get().url(canonicalUrl).build());
                throw new ChildUrlsException(childUrlSet);
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
        String url = responseData.getUrl();
        final String indexingTarget = crawlingConfig.getIndexingTarget(url);
        url = pathMappingHelper.replaceUrl(sessionId, url);
        final String mimeType = responseData.getMimeType();

        final Map<String, String> fieldConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.FIELD);

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
        final String lang = systemHelper.normalizeLang(getSingleNodeValue(document, fessConfig.getCrawlerDocumentHtmlLangXpath(), true));
        if (lang != null) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldLang(), lang);
        }
        // title
        // content
        putResultDataBody(dataMap, fessConfig.getIndexFieldContent(), getDocumentContent(responseData, document));
        if ((Constants.TRUE.equalsIgnoreCase(fieldConfigMap.get(fessConfig.getIndexFieldCache())) || fessConfig
                .isCrawlerDocumentCacheEnable()) && fessConfig.isSupportedDocumentCacheMimetypes(mimeType)) {
            if (responseData.getContentLength() > 0
                    && responseData.getContentLength() <= fessConfig.getCrawlerDocumentCacheMaxSizeAsInteger().longValue()) {
                String charSet = responseData.getCharSet();
                if (charSet == null) {
                    charSet = Constants.UTF_8;
                }
                try {
                    // cache
                    putResultDataBody(dataMap, fessConfig.getIndexFieldCache(),
                            new String(InputStreamUtil.getBytes(responseData.getResponseBody()), charSet));
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
        putResultDataBody(dataMap, fessConfig.getIndexFieldDigest(), getDocumentDigest(responseData, document));
        // segment
        putResultDataBody(dataMap, fessConfig.getIndexFieldSegment(), sessionId);
        // host
        putResultDataBody(dataMap, fessConfig.getIndexFieldHost(), getHost(url));
        // site
        putResultDataBody(dataMap, fessConfig.getIndexFieldSite(), getSite(url, urlEncoding));
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
        final Set<String> labelTypeSet = new HashSet<String>();
        for (final String labelType : crawlingConfig.getLabelTypeValues()) {
            labelTypeSet.add(labelType);
        }
        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();
        labelTypeSet.addAll(labelTypeHelper.getMatchedLabelValueSet(url));
        putResultDataBody(dataMap, fessConfig.getIndexFieldLabel(), labelTypeSet);
        // role: roleType
        final List<String> roleTypeList = new ArrayList<String>();
        for (final String roleType : crawlingConfig.getRoleTypeValues()) {
            roleTypeList.add(roleType);
        }
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
        final Map<String, String> xpathConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.XPATH);
        final Map<String, String> scriptConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.SCRIPT);
        for (final Map.Entry<String, String> entry : xpathConfigMap.entrySet()) {
            final String key = entry.getKey();
            final String value = getSingleNodeValue(document, entry.getValue(), true);
            putResultDataWithTemplate(dataMap, key, value, scriptConfigMap.get(key));
        }
        final Map<String, String> valueConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.VALUE);
        for (final Map.Entry<String, String> entry : valueConfigMap.entrySet()) {
            final String key = entry.getKey();
            putResultDataWithTemplate(dataMap, key, entry.getValue(), scriptConfigMap.get(key));
        }
    }

    protected String getCanonicalUrl(final ResponseData responseData, final Document document) {
        final String canonicalUrl = getSingleNodeValue(document, fessConfig.getCrawlerDocumentHtmlCannonicalXpath(), false);
        if (StringUtil.isNotBlank(canonicalUrl)) {
            return canonicalUrl;
        }
        return null;
    }

    protected String getDocumentDigest(final ResponseData responseData, final Document document) {
        final String digest = getSingleNodeValue(document, fessConfig.getCrawlerDocumentHtmlDigestXpath(), false);
        if (StringUtil.isNotBlank(digest)) {
            return digest;
        }

        final String body =
                normalizeContent(removeCommentTag(getSingleNodeValue(document, fessConfig.getCrawlerDocumentHtmlContentXpath(),
                        prunedCacheContent)));
        return StringUtils.abbreviate(body, fessConfig.getCrawlerDocumentHtmlMaxDigestLengthAsInteger());
    }

    String removeCommentTag(final String content) {
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

    private String getDocumentContent(final ResponseData responseData, final Document document) {
        return normalizeContent(getSingleNodeValue(document, fessConfig.getCrawlerDocumentHtmlContentXpath(), true));
    }

    protected String getSingleNodeValue(final Document document, final String xpath, final boolean pruned) {
        StringBuilder buf = null;
        NodeList list = null;
        try {
            list = getXPathAPI().selectNodeList(document, xpath);
            for (int i = 0; i < list.getLength(); i++) {
                if (buf == null) {
                    buf = new StringBuilder(1000);
                } else {
                    buf.append(' ');
                }
                final Node node = list.item(i);
                if (pruned) {
                    final Node n = pruneNode(node.cloneNode(true));
                    buf.append(n.getTextContent());
                } else {
                    buf.append(node.getTextContent());
                }
            }
        } catch (final Exception e) {
            logger.warn("Could not parse a value of " + xpath);
        }
        if (buf == null) {
            return null;
        }
        return buf.toString();
    }

    protected Node pruneNode(final Node node) {
        final NodeList nodeList = node.getChildNodes();
        final List<Node> childNodeList = new ArrayList<Node>();
        final List<Node> removedNodeList = new ArrayList<Node>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node childNode = nodeList.item(i);
            if (isPrunedTag(childNode.getNodeName())) {
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

    protected boolean isPrunedTag(final String tagName) {
        for (final String name : getCrawlerDocumentHtmlPrunedTags()) {
            if (name.equalsIgnoreCase(tagName)) {
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
        return buf.toString();
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

    protected String[] getCrawlerDocumentHtmlPrunedTags() {
        return fessConfig.getCrawlerDocumentHtmlPrunedTagsAsArray();
    }

}
