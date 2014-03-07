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

package jp.sf.fess.transformer;

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

import javax.xml.transform.TransformerException;

import jp.sf.fess.Constants;
import jp.sf.fess.db.exentity.CrawlingConfig;
import jp.sf.fess.db.exentity.CrawlingConfig.ConfigName;
import jp.sf.fess.helper.CrawlingConfigHelper;
import jp.sf.fess.helper.CrawlingSessionHelper;
import jp.sf.fess.helper.FileTypeHelper;
import jp.sf.fess.helper.LabelTypeHelper;
import jp.sf.fess.helper.OverlappingHostHelper;
import jp.sf.fess.helper.PathMappingHelper;
import jp.sf.fess.helper.SystemHelper;
import jp.sf.fess.taglib.FessFunctions;
import jp.sf.fess.util.ComponentUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xpath.objects.XObject;
import org.codelibs.core.util.StringUtil;
import org.cyberneko.html.parsers.DOMParser;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.SerializeUtil;
import org.seasar.robot.RobotCrawlAccessException;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.client.fs.ChildUrlsException;
import org.seasar.robot.entity.AccessResultData;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.seasar.robot.entity.UrlQueue;
import org.seasar.robot.util.CrawlingParameterUtil;
import org.seasar.robot.util.ResponseDataUtil;
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

    public String langXpath = "//HTML/@lang";

    public String digestXpath = "//META[@name='description']/@content";

    public String canonicalXpath = "//LINK[@rel='canonical']/@href";

    public List<String> prunedTagList = new ArrayList<String>();

    public boolean prunedCacheContent = true;

    public int maxDigestLength = 200;

    public int maxCacheLength = 2621440; //  2.5Mbytes

    public boolean enableCache = false;

    public Map<String, String> convertUrlMap = new HashMap<String, String>();

    @Override
    protected void storeData(final ResponseData responseData,
            final ResultData resultData) {
        final File tempFile = ResponseDataUtil
                .createResponseBodyFile(responseData);
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

        final CrawlingSessionHelper crawlingSessionHelper = ComponentUtil
                .getCrawlingSessionHelper();
        final String sessionId = crawlingSessionHelper
                .getCanonicalSessionId(responseData.getSessionId());
        final Date documentExpires = crawlingSessionHelper.getDocumentExpires();
        final PathMappingHelper pathMappingHelper = ComponentUtil
                .getPathMappingHelper();
        final String url = pathMappingHelper.replaceUrl(sessionId,
                responseData.getUrl());
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil
                .getCrawlingConfigHelper();
        final CrawlingConfig crawlingConfig = crawlingConfigHelper
                .get(responseData.getSessionId());
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final FileTypeHelper fileTypeHelper = ComponentUtil.getFileTypeHelper();

        final Map<String, String> fieldConfigMap = crawlingConfig
                .getConfigParameterMap(ConfigName.FIELD);

        String urlEncoding;
        final UrlQueue urlQueue = CrawlingParameterUtil.getUrlQueue();
        if (urlQueue != null && urlQueue.getEncoding() != null) {
            urlEncoding = urlQueue.getEncoding();
        } else {
            urlEncoding = responseData.getCharSet();
        }

        // cid
        final String configId = crawlingConfig.getConfigId();
        if (configId != null) {
            putResultDataBody(dataMap, systemHelper.configIdField, configId);
        }
        //  expires
        if (documentExpires != null) {
            putResultDataBody(dataMap, systemHelper.expiresField,
                    FessFunctions.formatDate(documentExpires));
        }
        // lang
        final String lang = systemHelper.normalizeLang(getSingleNodeValue(
                document, langXpath, true));
        if (lang != null) {
            putResultDataBody(dataMap, systemHelper.langField, lang);
        }
        // title
        // content
        putResultDataBody(dataMap, "content",
                getDocumentContent(responseData, document));
        if (Constants.TRUE.equalsIgnoreCase(fieldConfigMap.get("cache"))
                || enableCache) {
            String charSet = responseData.getCharSet();
            if (charSet == null) {
                charSet = Constants.UTF_8;
            }
            try {
                // cache 
                putResultDataBody(
                        dataMap,
                        "cache",
                        new String(InputStreamUtil.getBytes(responseData
                                .getResponseBody()), charSet));
                putResultDataBody(dataMap, systemHelper.hasCacheField,
                        Constants.TRUE);
            } catch (final Exception e) {
                logger.warn("Failed to write a cache: " + sessionId + ":"
                        + responseData, e);
            }
        }
        // digest
        putResultDataBody(dataMap, "digest",
                getDocumentDigest(responseData, document));
        // segment
        putResultDataBody(dataMap, "segment", sessionId);
        // host
        putResultDataBody(dataMap, "host", getHost(url));
        // site
        putResultDataBody(dataMap, "site", getSite(url, urlEncoding));
        // url
        putResultDataBody(dataMap, "url", url);
        // created
        putResultDataBody(dataMap, "created", "NOW");
        // anchor
        putResultDataBody(dataMap, "anchor",
                getAnchorList(document, responseData));
        // mimetype
        final String mimeType = responseData.getMimeType();
        putResultDataBody(dataMap, "mimetype", mimeType);
        if (fileTypeHelper != null) {
            // filetype
            putResultDataBody(dataMap, fileTypeHelper.getFieldName(),
                    fileTypeHelper.get(mimeType));
        }
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
        // label: labelType
        final Set<String> labelTypeSet = new HashSet<String>();
        for (final String labelType : crawlingConfig.getLabelTypeValues()) {
            labelTypeSet.add(labelType);
        }
        final LabelTypeHelper labelTypeHelper = ComponentUtil
                .getLabelTypeHelper();
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

        // from config
        final Map<String, String> xpathConfigMap = crawlingConfig
                .getConfigParameterMap(ConfigName.XPATH);
        final Map<String, String> scriptConfigMap = crawlingConfig
                .getConfigParameterMap(ConfigName.SCRIPT);
        for (final Map.Entry<String, String> entry : xpathConfigMap.entrySet()) {
            final String key = entry.getKey();
            final String value = getSingleNodeValue(document, entry.getValue(),
                    true);
            putResultDataWithTemplate(dataMap, key, value,
                    scriptConfigMap.get(key));
        }
        final Map<String, String> valueConfigMap = crawlingConfig
                .getConfigParameterMap(ConfigName.VALUE);
        for (final Map.Entry<String, String> entry : valueConfigMap.entrySet()) {
            final String key = entry.getKey();
            putResultDataWithTemplate(dataMap, key, entry.getValue(),
                    scriptConfigMap.get(key));
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
            final OverlappingHostHelper overlappingHostHelper = ComponentUtil
                    .getOverlappingHostHelper();
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
