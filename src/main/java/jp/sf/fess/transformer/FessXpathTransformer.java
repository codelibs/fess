/*
 * Copyright 2009-2013 the Fess Project and the Others.
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

package jp.sf.fess.transformer;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.TransformerException;

import jp.sf.fess.Constants;
import jp.sf.fess.db.exentity.CrawlingConfig;
import jp.sf.fess.helper.CrawlingConfigHelper;
import jp.sf.fess.helper.CrawlingSessionHelper;
import jp.sf.fess.helper.LabelTypeHelper;
import jp.sf.fess.helper.OverlappingHostHelper;
import jp.sf.fess.helper.PathMappingHelper;
import jp.sf.fess.taglib.FessFunctions;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xpath.objects.XObject;
import org.cyberneko.html.parsers.DOMParser;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.SerializeUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotCrawlAccessException;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.client.fs.ChildUrlsException;
import org.seasar.robot.entity.AccessResultData;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class FessXpathTransformer extends AbstractFessXpathTransformer {
    private static final Logger logger = LoggerFactory
            .getLogger(FessXpathTransformer.class);

    private static final int UTF8_BOM_SIZE = 3;

    public String cacheXpath = "//BODY";

    public String contentXpath = "//BODY";

    public String digestXpath = "//META[@name='description']/@content";

    public String canonicalXpath = "//LINK[@rel='canonical']/@href";

    public List<String> prunedTagList = new ArrayList<String>();

    public boolean prunedCacheContent = true;

    public int maxDigestLength = 200;

    public int maxCacheLength = 2621440; //  2.5Mbytes

    public boolean enableCache = false;

    public Map<String, String> convertUrlMap = new HashMap<String, String>();

    protected void putResultDataBody(final Map<String, Object> dataMap,
            final String key, final Object value) {
        dataMap.put(key, value);
    }

    @Override
    protected void storeData(final ResponseData responseData,
            final ResultData resultData) {
        final File tempFile = createResponseBodyFile(responseData);
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
                throw new RobotCrawlAccessException("Could not parse "
                        + responseData.getUrl(), e);
            } finally {
                IOUtils.closeQuietly(bis);
            }

            final Document document = parser.getDocument();

            final Map<String, Object> dataMap = new HashMap<String, Object>();
            for (final Map.Entry<String, String> entry : fieldRuleMap
                    .entrySet()) {
                final String path = entry.getValue();
                try {
                    final XObject xObj = getXPathAPI().eval(document, path);
                    final int type = xObj.getType();
                    switch (type) {
                    case XObject.CLASS_BOOLEAN:
                        final boolean b = xObj.bool();
                        putResultDataBody(dataMap, entry.getKey(),
                                Boolean.toString(b));
                        break;
                    case XObject.CLASS_NUMBER:
                        final double d = xObj.num();
                        putResultDataBody(dataMap, entry.getKey(),
                                Double.toString(d));
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
                        final Node value = getXPathAPI().selectSingleNode(
                                document, entry.getValue());
                        putResultDataBody(dataMap, entry.getKey(),
                                value != null ? value.getTextContent() : null);
                        break;
                    }
                } catch (final TransformerException e) {
                    logger.warn("Could not parse a value of " + entry.getKey()
                            + ":" + entry.getValue());
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
                throw new RobotCrawlAccessException(
                        "Could not serialize object: " + responseData.getUrl(),
                        e);
            }
            resultData.setEncoding(charsetName);
        } finally {
            if (!tempFile.delete()) {
                logger.warn("Could not delete a temp file: " + tempFile);
            }
        }
    }

    protected void putAdditionalData(final Map<String, Object> dataMap,
            final ResponseData responseData, final Document document) {
        // canonical
        if (StringUtil.isNotBlank(canonicalXpath)) {
            final String canonicalUrl = getCanonicalUrl(responseData, document);
            if (canonicalUrl != null
                    && !canonicalUrl.equals(responseData.getUrl())) {
                final Set<String> childUrlSet = new HashSet<String>();
                childUrlSet.add(canonicalUrl);
                throw new ChildUrlsException(childUrlSet);
            }
        }

        final CrawlingSessionHelper crawlingSessionHelper = SingletonS2Container
                .getComponent("crawlingSessionHelper");
        final String sessionId = crawlingSessionHelper
                .getCanonicalSessionId(responseData.getSessionId());
        final Date documentExpires = crawlingSessionHelper.getDocumentExpires();
        final PathMappingHelper pathMappingHelper = SingletonS2Container
                .getComponent("pathMappingHelper");
        final String url = pathMappingHelper.replaceUrl(sessionId,
                responseData.getUrl());
        final CrawlingConfigHelper crawlingConfigHelper = SingletonS2Container
                .getComponent("crawlingConfigHelper");
        final CrawlingConfig crawlingConfig = crawlingConfigHelper
                .get(responseData.getSessionId());

        // cid
        final String configId = crawlingConfig.getConfigId();
        if (configId != null) {
            putResultDataBody(dataMap, crawlingConfigHelper.getConfigIdField(),
                    configId);
        }
        //  expires
        if (documentExpires != null) {
            putResultDataBody(dataMap, crawlingSessionHelper.getExpiresField(),
                    FessFunctions.formatDate(documentExpires));
        }
        // title
        // content
        putResultDataBody(dataMap, "content",
                getDocumentContent(responseData, document));
        if (enableCache) {
            // cache 
            putResultDataBody(dataMap, "cache",
                    getDocumentCache(responseData, document));
        }
        // digest
        putResultDataBody(dataMap, "digest",
                getDocumentDigest(responseData, document));
        // segment
        putResultDataBody(dataMap, "segment", sessionId);
        // host
        putResultDataBody(dataMap, "host", getHost(url));
        // site
        putResultDataBody(dataMap, "site",
                getSite(url, responseData.getCharSet()));
        // url
        putResultDataBody(dataMap, "url", url);
        // created
        putResultDataBody(dataMap, "created", "NOW");
        // anchor
        putResultDataBody(dataMap, "anchor",
                getAnchorList(document, responseData));
        // mimetype
        putResultDataBody(dataMap, "mimetype", responseData.getMimeType());
        // contentLength
        putResultDataBody(dataMap, "contentLength",
                Long.toString(responseData.getContentLength()));
        //  lastModified
        putResultDataBody(dataMap, "lastModified",
                FessFunctions.formatDate(responseData.getLastModified()));
        // indexingTarget
        putResultDataBody(dataMap, Constants.INDEXING_TARGET,
                crawlingConfig.getIndexingTarget(url));
        //  boost
        putResultDataBody(dataMap, "boost", crawlingConfig.getDocumentBoost());
        // type: browserType
        final List<String> browserTypeList = new ArrayList<String>();
        for (final String browserType : crawlingConfig.getBrowserTypeValues()) {
            browserTypeList.add(browserType);
        }
        putResultDataBody(dataMap, "type", browserTypeList);
        // label: labelType
        final Set<String> labelTypeSet = new HashSet<String>();
        for (final String labelType : crawlingConfig.getLabelTypeValues()) {
            labelTypeSet.add(labelType);
        }
        final LabelTypeHelper labelTypeHelper = SingletonS2Container
                .getComponent("labelTypeHelper");
        labelTypeSet.addAll(labelTypeHelper.getMatchedLabelValueSet(url));
        putResultDataBody(dataMap, "label", labelTypeSet);
        // role: roleType
        final List<String> roleTypeList = new ArrayList<String>();
        for (final String roleType : crawlingConfig.getRoleTypeValues()) {
            roleTypeList.add(roleType);
        }
        putResultDataBody(dataMap, "role", roleTypeList);
        // id
        putResultDataBody(dataMap, "id",
                crawlingSessionHelper.generateId(dataMap));
        // parentId
        String parentUrl = responseData.getParentUrl();
        if (StringUtil.isNotBlank(parentUrl)) {
            parentUrl = pathMappingHelper.replaceUrl(sessionId, parentUrl);
            putResultDataBody(dataMap, "url", parentUrl);
            putResultDataBody(dataMap, "parentId",
                    crawlingSessionHelper.generateId(dataMap));
            putResultDataBody(dataMap, "url", url); // set again
        }

    }

    protected String getCanonicalUrl(final ResponseData responseData,
            final Document document) {
        final String canonicalUrl = getSingleNodeValue(document,
                canonicalXpath, false);
        if (StringUtil.isNotBlank(canonicalUrl)) {
            return canonicalUrl;
        }
        return null;
    }

    protected String getDocumentDigest(final ResponseData responseData,
            final Document document) {
        final String digest = getSingleNodeValue(document, digestXpath, false);
        if (StringUtil.isNotBlank(digest)) {
            return digest;
        }

        final String body = normalizeContent(removeCommentTag(getSingleNodeValue(
                document, contentXpath, prunedCacheContent)));
        return StringUtils.abbreviate(body, maxDigestLength);
    }

    String removeCommentTag(final String content) {
        if (content == null) {
            return "";
        }
        String value = content;
        int pos = value.indexOf("<!--");
        while (pos >= 0) {
            final int lastPos = value.indexOf("-->", pos);
            if (lastPos >= 0) {
                if (pos == 0) {
                    value = " " + value.substring(lastPos + 3);
                } else {
                    value = value.substring(0, pos) + " "
                            + value.substring(lastPos + 3);
                }
            } else {
                break;
            }
            pos = value.indexOf("<!--");
        }
        return value;
    }

    private String getDocumentContent(final ResponseData responseData,
            final Document document) {
        return normalizeContent(getSingleNodeValue(document, contentXpath, true));
    }

    private String getDocumentCache(final ResponseData responseData,
            final Document document) {
        final InputStream is = responseData.getResponseBody();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] bytes = new byte[1024];
        try {
            int length = is.read(bytes);
            while (length != -1) {
                if (length != 0) {
                    baos.write(bytes, 0, length);
                }
                if (baos.size() > maxCacheLength) {
                    break;
                }
                length = is.read(bytes);
            }
        } catch (final IOException e) {
            logger.warn("Could not create a cache data.", e);
        } finally {
            bytes = null;
            IOUtils.closeQuietly(is);
        }

        try {
            return baos.toString(responseData.getCharSet());
        } catch (final UnsupportedEncodingException e) {
            logger.warn("Unsupported encoding: " + responseData.getCharSet(), e);
            return baos.toString();
        }
    }

    protected String getSingleNodeValue(final Document document,
            final String xpath, final boolean pruned) {
        Node value = null;
        try {
            value = getXPathAPI().selectSingleNode(document, xpath);
        } catch (final Exception e) {
            logger.warn("Could not parse a value of " + xpath);
        }
        if (value == null) {
            return null;
        }
        if (pruned) {
            final Node node = pruneNode(value.cloneNode(true));
            return node.getTextContent();
        } else {
            return value.getTextContent();
        }
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
        for (final String name : prunedTagList) {
            if (name.equalsIgnoreCase(tagName)) {
                return true;
            }
        }
        return false;
    }

    protected String getMultipleNodeValue(final Document document,
            final String xpath) {
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

    protected String replaceOverlappingHost(final String url) {
        try {
            // remove overlapping host
            final OverlappingHostHelper overlappingHostHelper = SingletonS2Container
                    .getComponent("overlappingHostHelper");
            return overlappingHostHelper.convert(url);
        } catch (final Exception e) {
            return url;
        }
    }

    protected List<String> getAnchorList(final Document document,
            final ResponseData responseData) {
        List<String> anchorList = new ArrayList<String>();
        final String baseHref = getBaseHref(document);
        try {
            final URL url = new URL(baseHref != null ? baseHref
                    : responseData.getUrl());
            for (final Map.Entry<String, String> entry : childUrlRuleMap
                    .entrySet()) {
                anchorList.addAll(getUrlFromTagAttribute(url, document,
                        entry.getKey(), entry.getValue(),
                        responseData.getCharSet()));
            }
            anchorList = convertChildUrlList(anchorList);
        } catch (final Exception e) {
            logger.warn("Could not parse anchor tags.", e);
            //        } finally {
            //            xpathAPI.remove();
        }
        return anchorList;
    }

    @Override
    protected List<String> convertChildUrlList(final List<String> urlList) {

        final List<String> newUrlList = new ArrayList<String>();
        if (urlList != null) {
            for (String url : urlList) {
                for (final Map.Entry<String, String> entry : convertUrlMap
                        .entrySet()) {
                    url = url.replaceAll(entry.getKey(), entry.getValue());
                }

                newUrlList.add(replaceOverlappingHost(url));
            }
        }
        return newUrlList;
    }

    public void addPrunedTag(final String tagName) {
        if (StringUtil.isNotBlank(tagName)) {
            prunedTagList.add(tagName);
        }
    }

    @Override
    public Object getData(final AccessResultData accessResultData) {
        final byte[] data = accessResultData.getData();
        if (data != null) {
            try {
                return SerializeUtil.fromBinaryToObject(data);
            } catch (final Exception e) {
                throw new RobotSystemException(
                        "Could not create an instanced from bytes.", e);
            }
        }
        return new HashMap<String, Object>();
    }

    @Override
    protected boolean isValidPath(final String path) {
        return super.isValidPath(path);
    }

    @Override
    protected void addChildUrlFromTagAttribute(final List<String> urlList,
            final URL url, final String attrValue, final String encoding) {
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
        return b[0] == (byte) 0xEF && b[1] == (byte) 0xBB
                && b[2] == (byte) 0xBF;
    }
}
