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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.codelibs.core.collection.LruHashMap;
import org.codelibs.core.io.SerializeUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.es.exentity.CrawlingConfig;
import org.codelibs.fess.es.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingSessionHelper;
import org.codelibs.fess.helper.FieldHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.SambaHelper;
import org.codelibs.fess.taglib.FessFunctions;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.robot.client.smb.SmbClient;
import org.codelibs.robot.entity.AccessResult;
import org.codelibs.robot.entity.AccessResultData;
import org.codelibs.robot.entity.ExtractData;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.entity.ResultData;
import org.codelibs.robot.entity.UrlQueue;
import org.codelibs.robot.exception.RobotCrawlAccessException;
import org.codelibs.robot.exception.RobotSystemException;
import org.codelibs.robot.extractor.Extractor;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.util.CrawlingParameterUtil;
import org.lastaflute.di.core.SingletonLaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcifs.smb.ACE;
import jcifs.smb.SID;

public abstract class AbstractFessFileTransformer extends AbstractFessXpathTransformer {
    private static final Logger logger = LoggerFactory // NOPMD
            .getLogger(AbstractFessFileTransformer.class);

    public String encoding = null;

    public String noTitleLabel = "No title.";

    public int abbreviationMarginLength = 10;

    public boolean ignoreEmptyContent = false;

    public int maxTitleLength = 100;

    public int maxDigestLength = 200;

    public boolean appendMetaContentToContent = true;

    public boolean appendBodyContentToContent = true;

    public boolean enableCache = false;

    public Map<String, String> parentEncodingMap = Collections.synchronizedMap(new LruHashMap<String, String>(1000));

    protected Map<String, String> metaContentMapping;

    protected abstract Extractor getExtractor(ResponseData responseData);

    @Override
    public ResultData transform(final ResponseData responseData) {
        if (responseData == null || responseData.getResponseBody() == null) {
            throw new RobotCrawlAccessException("No response body.");
        }

        final Extractor extractor = getExtractor(responseData);
        final InputStream in = responseData.getResponseBody();
        final Map<String, String> params = new HashMap<String, String>();
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, getResourceName(responseData));
        final String mimeType = responseData.getMimeType();
        params.put(HttpHeaders.CONTENT_TYPE, mimeType);
        params.put(HttpHeaders.CONTENT_ENCODING, responseData.getCharSet());
        final StringBuilder contentMetaBuf = new StringBuilder(1000);
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        final Map<String, Object> metaDataMap = new HashMap<>();
        String content;
        try {
            final ExtractData extractData = extractor.getText(in, params);
            content = extractData.getContent();
            if (ignoreEmptyContent && StringUtil.isBlank(content)) {
                return null;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("ExtractData: " + extractData);
            }
            // meta
            for (final String key : extractData.getKeySet()) {
                final String[] values = extractData.getValues(key);
                if (values != null) {
                    metaDataMap.put(key, values);
                    if (contentMetaBuf.length() > 0) {
                        contentMetaBuf.append(' ');
                    }
                    final String joinValue = StringUtils.join(values, ' ');
                    if (StringUtil.isNotBlank(joinValue)) {
                        contentMetaBuf.append(joinValue);
                        if (metaContentMapping != null) {
                            final String solrField = metaContentMapping.get(key);
                            if (StringUtil.isNotBlank(solrField)) {
                                if (solrField.endsWith("_m")) {
                                    dataMap.put(solrField, values);
                                } else {
                                    dataMap.put(solrField, joinValue);
                                }
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            final RobotCrawlAccessException rcae = new RobotCrawlAccessException("Could not get a text from " + responseData.getUrl(), e);
            rcae.setLogLevel(RobotCrawlAccessException.WARN);
            throw rcae;
        } finally {
            IOUtils.closeQuietly(in);
        }
        if (content == null) {
            content = StringUtil.EMPTY;
        }
        final String contentMeta = contentMetaBuf.toString();

        final ResultData resultData = new ResultData();
        resultData.setTransformerName(getName());

        final CrawlingSessionHelper crawlingSessionHelper = ComponentUtil.getCrawlingSessionHelper();
        final String sessionId = crawlingSessionHelper.getCanonicalSessionId(responseData.getSessionId());
        final Long documentExpires = crawlingSessionHelper.getDocumentExpires();
        final PathMappingHelper pathMappingHelper = ComponentUtil.getPathMappingHelper();
        final SambaHelper sambaHelper = ComponentUtil.getSambaHelper();
        final DynamicProperties crawlerProperties = ComponentUtil.getCrawlerProperties();
        final boolean useAclAsRole = crawlerProperties.getProperty(Constants.USE_ACL_AS_ROLE, Constants.FALSE).equals(Constants.TRUE);
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(responseData.getSessionId());
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
        // segment
        putResultDataBody(dataMap, fieldHelper.segmentField, sessionId);
        // content
        final StringBuilder buf = new StringBuilder(content.length() + 1000);
        if (appendBodyContentToContent) {
            buf.append(content);
        }
        if (appendMetaContentToContent) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append(contentMeta);
        }
        final String body = normalizeContent(buf.toString());
        if (StringUtil.isNotBlank(body)) {
            putResultDataBody(dataMap, fieldHelper.contentField, body);
        } else {
            putResultDataBody(dataMap, fieldHelper.contentField, StringUtil.EMPTY);
        }
        if (Constants.TRUE.equalsIgnoreCase(fieldConfigMap.get(fieldHelper.cacheField)) || enableCache) {
            final String cache = content.trim().replaceAll("[ \\t\\x0B\\f]+", " ");
            // text cache
            putResultDataBody(dataMap, fieldHelper.cacheField, cache);
            putResultDataBody(dataMap, fieldHelper.hasCacheField, Constants.TRUE);
        }
        // digest
        putResultDataBody(dataMap, fieldHelper.digestField, Constants.DIGEST_PREFIX
                + abbreviate(normalizeContent(content), maxDigestLength));
        // title
        if (!dataMap.containsKey(fieldHelper.titleField)) {
            if (url.endsWith("/")) {
                if (StringUtil.isNotBlank(content)) {
                    putResultDataBody(dataMap, fieldHelper.titleField, abbreviate(body, maxTitleLength));
                } else {
                    putResultDataBody(dataMap, fieldHelper.titleField, noTitleLabel);
                }
            } else {
                final String u = decodeUrlAsName(url, url.startsWith("file:"));
                final int pos = u.lastIndexOf('/');
                if (pos == -1) {
                    putResultDataBody(dataMap, fieldHelper.titleField, u);
                } else {
                    putResultDataBody(dataMap, fieldHelper.titleField, u.substring(pos + 1));
                }
            }
        }
        // host
        putResultDataBody(dataMap, fieldHelper.hostField, getHost(url));
        // site
        putResultDataBody(dataMap, fieldHelper.siteField, getSite(url, urlEncoding));
        // url
        putResultDataBody(dataMap, fieldHelper.urlField, url);
        // created
        putResultDataBody(dataMap, fieldHelper.createdField, Constants.NOW);
        // TODO anchor
        putResultDataBody(dataMap, fieldHelper.anchorField, StringUtil.EMPTY);
        // mimetype
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
        if (useAclAsRole && responseData.getUrl().startsWith("smb://")) {
            final ACE[] aces = (ACE[]) responseData.getMetaDataMap().get(SmbClient.SMB_ACCESS_CONTROL_ENTRIES);
            if (aces != null) {
                for (final ACE item : aces) {
                    final SID sid = item.getSID();
                    roleTypeList.add(sambaHelper.getAccountId(sid));
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("smbUrl:" + responseData.getUrl() + " roleType:" + roleTypeList.toString());
                }
            }
        }
        putResultDataBody(dataMap, fieldHelper.roleField, roleTypeList);
        // TODO date
        // TODO lang
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
        final Map<String, String> scriptConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.SCRIPT);
        final Map<String, String> metaConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.META);
        for (final Map.Entry<String, String> entry : metaConfigMap.entrySet()) {
            final String key = entry.getKey();
            final String[] values = entry.getValue().split(",");
            for (final String value : values) {
                putResultDataWithTemplate(dataMap, key, metaDataMap.get(value), scriptConfigMap.get(key));
            }
        }
        final Map<String, String> valueConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.VALUE);
        for (final Map.Entry<String, String> entry : valueConfigMap.entrySet()) {
            final String key = entry.getKey();
            putResultDataWithTemplate(dataMap, key, entry.getValue(), scriptConfigMap.get(key));
        }

        try {
            resultData.setData(SerializeUtil.fromObjectToBinary(dataMap));
        } catch (final Exception e) {
            throw new RobotCrawlAccessException("Could not serialize object: " + url, e);
        }
        resultData.setEncoding(charsetName);

        return resultData;
    }

    protected String abbreviate(final String str, final int maxWidth) {
        String newStr = StringUtils.abbreviate(str, maxWidth);
        try {
            if (newStr.getBytes(Constants.UTF_8).length > maxWidth + abbreviationMarginLength) {
                newStr = StringUtils.abbreviate(str, maxWidth / 2);
            }
        } catch (final UnsupportedEncodingException e) {
            // NOP
        }
        return newStr;
    }

    private String getResourceName(final ResponseData responseData) {
        String name = responseData.getUrl();
        final String enc = responseData.getCharSet();

        if (name == null || enc == null) {
            return null;
        }

        name = name.replaceAll("/+$", StringUtil.EMPTY);
        final int idx = name.lastIndexOf('/');
        if (idx >= 0) {
            name = name.substring(idx + 1);
        }
        try {
            return URLDecoder.decode(name, enc);
        } catch (final Exception e) {
            return name;
        }
    }

    protected String decodeUrlAsName(final String url, final boolean escapePlus) {
        if (url == null) {
            return null;
        }

        String enc = Constants.UTF_8;
        if (encoding == null) {
            final UrlQueue urlQueue = CrawlingParameterUtil.getUrlQueue();
            if (urlQueue != null) {
                final String parentUrl = urlQueue.getParentUrl();
                if (StringUtil.isNotEmpty(parentUrl)) {
                    final String sessionId = urlQueue.getSessionId();
                    final String pageEnc = getParentEncoding(parentUrl, sessionId);
                    if (pageEnc != null) {
                        enc = pageEnc;
                    } else if (urlQueue.getEncoding() != null) {
                        enc = urlQueue.getEncoding();
                    }
                }
            }
        } else {
            enc = encoding;
        }

        final String escapedUrl = escapePlus ? url.replace("+", "%2B") : url;
        try {
            return URLDecoder.decode(escapedUrl, enc);
        } catch (final Exception e) {
            return url;
        }
    }

    protected String getParentEncoding(final String parentUrl, final String sessionId) {
        final String key = sessionId + ":" + parentUrl;
        String enc = parentEncodingMap.get(key);
        if (enc != null) {
            return enc;
        }

        AccessResult accessResult = ComponentUtil.getDataService().getAccessResult(sessionId, parentUrl);
        if (accessResult != null) {
            AccessResultData accessResultData = accessResult.getAccessResultData();
            if (accessResultData != null && accessResultData.getEncoding() != null) {
                enc = accessResultData.getEncoding();
                parentEncodingMap.put(key, enc);
                return enc;
            }
        }
        return null;
    }

    @Override
    protected String getHost(final String url) {
        if (StringUtil.isBlank(url)) {
            return StringUtil.EMPTY; // empty
        }

        if (url.startsWith("file:////")) {
            final String value = decodeUrlAsName(url.substring(9), true);
            final int pos = value.indexOf('/');
            if (pos > 0) {
                return value.substring(0, pos);
            } else if (pos == -1) {
                return value;
            } else {
                return "localhost";
            }
        } else if (url.startsWith("file:")) {
            return "localhost";
        }

        return super.getHost(url);
    }

    @Override
    protected String getSite(final String url, final String encoding) {
        if (StringUtil.isBlank(url)) {
            return StringUtil.EMPTY; // empty
        }

        if (url.startsWith("file:////")) {
            final String value = decodeUrlAsName(url.substring(9), true);
            return StringUtils.abbreviate("\\\\" + value.replace('/', '\\'), maxSiteLength);
        } else if (url.startsWith("file:")) {
            final String value = decodeUrlAsName(url.substring(5), true);
            if (value.length() > 2 && value.charAt(2) == ':') {
                // Windows
                return StringUtils.abbreviate(value.substring(1).replace('/', '\\'), maxSiteLength);
            } else {
                // Unix
                return StringUtils.abbreviate(value, maxSiteLength);
            }
        }

        return super.getSite(url, encoding);
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

    public void addMetaContentMapping(final String metaname, final String solrField) {
        if (metaContentMapping == null) {
            metaContentMapping = new HashMap<String, String>();
        }
        metaContentMapping.put(metaname, solrField);
    }
}
