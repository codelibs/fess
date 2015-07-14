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

package org.codelibs.fess.transformer;

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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xpath.objects.XObject;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.exentity.CrawlingConfig;
import org.codelibs.fess.es.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingSessionHelper;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.OverlappingHostHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.taglib.FessFunctions;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.robot.RobotCrawlAccessException;
import org.codelibs.robot.RobotSystemException;
import org.codelibs.robot.builder.RequestDataBuilder;
import org.codelibs.robot.client.fs.ChildUrlsException;
import org.codelibs.robot.entity.AccessResultData;
import org.codelibs.robot.entity.RequestData;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.entity.ResultData;
import org.codelibs.robot.entity.UrlQueue;
import org.codelibs.robot.util.CrawlingParameterUtil;
import org.codelibs.robot.util.ResponseDataUtil;
import org.cyberneko.html.parsers.DOMParser;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class FessXpathTransformer extends AbstractFessXpathTransformer {
    private static final Logger logger = LoggerFactory.getLogger(FessXpathTransformer.class);

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
                throw new RobotCrawlAccessException("Could not parse " + responseData.getUrl(), e);
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
                throw new RobotCrawlAccessException("Could not serialize object: " + responseData.getUrl(), e);
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
        if (StringUtil.isNotBlank(canonicalXpath)) {
            final String canonicalUrl = getCanonicalUrl(responseData, document);
            if (canonicalUrl != null && !canonicalUrl.equals(responseData.getUrl())) {
                final Set<RequestData> childUrlSet = new HashSet<>();
                childUrlSet.add(RequestDataBuilder.newRequestData().get().url(canonicalUrl).build());
                throw new ChildUrlsException(childUrlSet);
            }
        }

        final CrawlingSessionHelper crawlingSessionHelper = ComponentUtil.getCrawlingSessionHelper();
        final String sessionId = crawlingSessionHelper.getCanonicalSessionId(responseData.getSessionId());
        final Long documentExpires = crawlingSessionHelper.getDocumentExpires();
        final PathMappingHelper pathMappingHelper = ComponentUtil.getPathMappingHelper();
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(responseData.getSessionId());
        final SystemHelper systemHelper = ComponentUtil.getSystemHelper();
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        final FileTypeHelper fileTypeHelper = ComponentUtil.getFileTypeHelper();
        String url = responseData.getUrl();
        final String indexingTarget = crawlingConfig.getIndexingTarget(url);
        url = pathMappingHelper.replaceUrl(sessionId, url);

        final Map<String, String> fieldConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.FIELD);

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
            putResultDataBody(dataMap, fieldHelper.configIdField, configId);
        }
        //  expires
        if (documentExpires != null) {
            putResultDataBody(dataMap, fieldHelper.expiresField, new Date(documentExpires));
        }
        // lang
        final String lang = systemHelper.normalizeLang(getSingleNodeValue(document, langXpath, true));
        if (lang != null) {
            putResultDataBody(dataMap, fieldHelper.langField, lang);
        }
        // title
        // content
        putResultDataBody(dataMap, fieldHelper.contentField, getDocumentContent(responseData, document));
        if (Constants.TRUE.equalsIgnoreCase(fieldConfigMap.get(fieldHelper.cacheField)) || enableCache) {
            String charSet = responseData.getCharSet();
            if (charSet == null) {
                charSet = Constants.UTF_8;
            }
            try {
                // cache
                putResultDataBody(dataMap, fieldHelper.cacheField, new String(InputStreamUtil.getBytes(responseData.getResponseBody()),
                        charSet));
                putResultDataBody(dataMap, fieldHelper.hasCacheField, Constants.TRUE);
            } catch (final Exception e) {
                logger.warn("Failed to write a cache: " + sessionId + ":" + responseData, e);
            }
        }
        // digest
        putResultDataBody(dataMap, fieldHelper.digestField, getDocumentDigest(responseData, document));
        // segment
        putResultDataBody(dataMap, fieldHelper.segmentField, sessionId);
        // host
        putResultDataBody(dataMap, fieldHelper.hostField, getHost(url));
        // site
        putResultDataBody(dataMap, fieldHelper.siteField, getSite(url, urlEncoding));
        // url
        putResultDataBody(dataMap, fieldHelper.urlField, url);
        // created
        putResultDataBody(dataMap, fieldHelper.createdField, Constants.NOW);
        // anchor
        putResultDataBody(dataMap, fieldHelper.anchorField, getAnchorList(document, responseData));
        // mimetype
        final String mimeType = responseData.getMimeType();
        putResultDataBody(dataMap, fieldHelper.mimetypeField, mimeType);
        if (fileTypeHelper != null) {
            // filetype
            putResultDataBody(dataMap, fieldHelper.filetypeField, fileTypeHelper.get(mimeType));
        }
        // contentLength
        putResultDataBody(dataMap, fieldHelper.contentLengthField, Long.toString(responseData.getContentLength()));
        //  lastModified
        if (responseData.getLastModified() != null) {
            putResultDataBody(dataMap, fieldHelper.lastModifiedField, FessFunctions.formatDate(responseData.getLastModified()));
        }
        // indexingTarget
        putResultDataBody(dataMap, Constants.INDEXING_TARGET, indexingTarget);
        //  boost
        putResultDataBody(dataMap, fieldHelper.boostField, crawlingConfig.getDocumentBoost());
        // label: labelType
        final Set<String> labelTypeSet = new HashSet<String>();
        for (final String labelType : crawlingConfig.getLabelTypeValues()) {
            labelTypeSet.add(labelType);
        }
        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();
        labelTypeSet.addAll(labelTypeHelper.getMatchedLabelValueSet(url));
        putResultDataBody(dataMap, fieldHelper.labelField, labelTypeSet);
        // role: roleType
        final List<String> roleTypeList = new ArrayList<String>();
        for (final String roleType : crawlingConfig.getRoleTypeValues()) {
            roleTypeList.add(roleType);
        }
        putResultDataBody(dataMap, fieldHelper.roleField, roleTypeList);
        // id
        putResultDataBody(dataMap, fieldHelper.idField, crawlingSessionHelper.generateId(dataMap));
        // parentId
        String parentUrl = responseData.getParentUrl();
        if (StringUtil.isNotBlank(parentUrl)) {
            parentUrl = pathMappingHelper.replaceUrl(sessionId, parentUrl);
            putResultDataBody(dataMap, fieldHelper.urlField, parentUrl);
            putResultDataBody(dataMap, fieldHelper.parentIdField, crawlingSessionHelper.generateId(dataMap));
            putResultDataBody(dataMap, fieldHelper.urlField, url); // set again
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
        final String canonicalUrl = getSingleNodeValue(document, canonicalXpath, false);
        if (StringUtil.isNotBlank(canonicalUrl)) {
            return canonicalUrl;
        }
        return null;
    }

    protected String getDocumentDigest(final ResponseData responseData, final Document document) {
        final String digest = getSingleNodeValue(document, digestXpath, false);
        if (StringUtil.isNotBlank(digest)) {
            return digest;
        }

        final String body = normalizeContent(removeCommentTag(getSingleNodeValue(document, contentXpath, prunedCacheContent)));
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
        return normalizeContent(getSingleNodeValue(document, contentXpath, true));
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
        for (final String name : prunedTagList) {
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

    protected String replaceOverlappingHost(final String url) {
        try {
            // remove overlapping host
            final OverlappingHostHelper overlappingHostHelper = ComponentUtil.getOverlappingHostHelper();
            return overlappingHostHelper.convert(url);
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
                requestData.setUrl(replaceOverlappingHost(url));
            }
        }
        return urlList;
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
                throw new RobotSystemException("Could not create an instanced from bytes.", e);
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
}
