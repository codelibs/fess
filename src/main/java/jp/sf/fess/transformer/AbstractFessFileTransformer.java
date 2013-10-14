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

import jcifs.smb.ACE;
import jcifs.smb.SID;
import jp.sf.fess.Constants;
import jp.sf.fess.db.exentity.CrawlingConfig;
import jp.sf.fess.helper.CrawlingConfigHelper;
import jp.sf.fess.helper.CrawlingSessionHelper;
import jp.sf.fess.helper.LabelTypeHelper;
import jp.sf.fess.helper.PathMappingHelper;
import jp.sf.fess.helper.SambaHelper;
import jp.sf.fess.taglib.FessFunctions;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.codelibs.core.util.DynamicProperties;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.SerializeUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotCrawlAccessException;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.client.smb.SmbClient;
import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.exbhv.AccessResultDataBhv;
import org.seasar.robot.entity.AccessResultData;
import org.seasar.robot.entity.ExtractData;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.seasar.robot.entity.UrlQueue;
import org.seasar.robot.extractor.Extractor;
import org.seasar.robot.util.CrawlingParameterUtil;
import org.seasar.robot.util.LruHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFessFileTransformer extends
        AbstractFessXpathTransformer {
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

    public Map<String, String> parentEncodingMap = Collections
            .synchronizedMap(new LruHashMap<String, String>(1000));

    protected Map<String, String> metaContentMapping;

    protected abstract Extractor getExtractor(ResponseData responseData);

    protected void putResultDataBody(final Map<String, Object> dataMap,
            final String key, final Object value) {
        if (!dataMap.containsKey(key)) {
            dataMap.put(key, value);
        }
    }

    @Override
    public ResultData transform(final ResponseData responseData) {
        if (responseData == null || responseData.getResponseBody() == null) {
            throw new RobotCrawlAccessException("No response body.");
        }

        final Extractor extractor = getExtractor(responseData);
        final InputStream in = responseData.getResponseBody();
        final Map<String, String> params = new HashMap<String, String>();
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY,
                getResourceName(responseData));
        params.put(HttpHeaders.CONTENT_TYPE, responseData.getMimeType());
        params.put(HttpHeaders.CONTENT_ENCODING, responseData.getCharSet());
        final StringBuilder contentBuf = new StringBuilder(1000);
        final StringBuilder contentMetaBuf = new StringBuilder(1000);
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        try {
            final ExtractData extractData = extractor.getText(in, params);
            if (ignoreEmptyContent
                    && StringUtil.isBlank(extractData.getContent())) {
                return null;
            }
            contentBuf.append(extractData.getContent());
            // meta
            for (final String key : extractData.getKeySet()) {
                final String[] values = extractData.getValues(key);
                if (values != null) {
                    if (contentMetaBuf.length() > 0) {
                        contentMetaBuf.append(' ');
                    }
                    final String joinValue = StringUtils.join(values, ' ');
                    if (StringUtil.isNotBlank(joinValue)) {
                        contentMetaBuf.append(joinValue);
                        if (metaContentMapping != null) {
                            final String solrField = metaContentMapping
                                    .get(key);
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
            final RobotCrawlAccessException rcae = new RobotCrawlAccessException(
                    "Could not get a text from " + responseData.getUrl(), e);
            rcae.setLogLevel(RobotCrawlAccessException.WARN);
            throw rcae;
        } finally {
            IOUtils.closeQuietly(in);
        }
        final String content = contentBuf.toString();
        final String contentMeta = contentMetaBuf.toString();

        if (StringUtil.isBlank(content)) {
            return null;
        }

        final ResultData resultData = new ResultData();
        resultData.setTransformerName(getName());

        final CrawlingSessionHelper crawlingSessionHelper = SingletonS2Container
                .getComponent("crawlingSessionHelper");
        final String sessionId = crawlingSessionHelper
                .getCanonicalSessionId(responseData.getSessionId());
        final Date documentExpires = crawlingSessionHelper.getDocumentExpires();
        final PathMappingHelper pathMappingHelper = SingletonS2Container
                .getComponent("pathMappingHelper");
        final String url = pathMappingHelper.replaceUrl(sessionId,
                responseData.getUrl());
        final SambaHelper sambaHelper = SingletonS2Container
                .getComponent("sambaHelper");
        final DynamicProperties crawlerProperties = SingletonS2Container
                .getComponent("crawlerProperties");
        final boolean useAclAsRole = crawlerProperties.getProperty(
                Constants.USE_ACL_AS_ROLE, Constants.FALSE).equals(
                Constants.TRUE);
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
        // segment
        putResultDataBody(dataMap, "segment", sessionId);
        // content
        final StringBuilder buf = new StringBuilder();
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
            putResultDataBody(dataMap, "content", body);
        } else {
            putResultDataBody(dataMap, "content", "");
        }
        final String cache = normalizeContent(content);
        if (enableCache) {
            // cache 
            putResultDataBody(dataMap, "cache", cache);
        }
        // digest
        putResultDataBody(dataMap, "digest", Constants.DIGEST_PREFIX
                + abbreviate(cache, maxDigestLength));
        // title
        if (url.endsWith("/")) {
            if (StringUtil.isNotBlank(content)) {
                putResultDataBody(dataMap, "title",
                        abbreviate(body, maxTitleLength));
            } else {
                putResultDataBody(dataMap, "title", noTitleLabel);
            }
        } else {
            final String u = decodeUrlAsName(url, url.startsWith("file:"));
            final int pos = u.lastIndexOf('/');
            if (pos == -1) {
                putResultDataBody(dataMap, "title", u);
            } else {
                putResultDataBody(dataMap, "title", u.substring(pos + 1));
            }
        }
        // host
        putResultDataBody(dataMap, "host", getHost(url));
        // site
        putResultDataBody(dataMap, "site",
                getSite(url, responseData.getCharSet()));
        // url
        putResultDataBody(dataMap, "url", url);
        // tstamp
        putResultDataBody(dataMap, "tstamp", "NOW");
        // TODO anchor
        putResultDataBody(dataMap, "anchor", "");
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
        if (useAclAsRole && responseData.getUrl().startsWith("smb://")) {
            final ACE[] aces = (ACE[]) responseData.getMetaDataMap().get(
                    SmbClient.SMB_ACCESS_CONTROL_ENTRIES);
            if (aces != null) {
                for (final ACE item : aces) {
                    final SID sid = item.getSID();
                    roleTypeList.add(sambaHelper.getAccountId(sid));
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("smbUrl:" + responseData.getUrl()
                            + " roleType:" + roleTypeList.toString());
                }
            }
        }
        putResultDataBody(dataMap, "role", roleTypeList);
        // TODO date
        // TODO lang
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

        try {
            resultData.setData(SerializeUtil.fromObjectToBinary(dataMap));
        } catch (final Exception e) {
            throw new RobotCrawlAccessException("Could not serialize object: "
                    + url, e);
        }
        resultData.setEncoding(charsetName);

        return resultData;
    }

    protected String abbreviate(final String str, final int maxWidth) {
        String newStr = StringUtils.abbreviate(str, maxWidth);
        try {
            if (newStr.getBytes(Constants.UTF_8).length > maxWidth
                    + abbreviationMarginLength) {
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

        name = name.replaceAll("/+$", "");
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
                    final String pageEnc = getParentEncoding(parentUrl,
                            sessionId);
                    if (pageEnc != null) {
                        enc = pageEnc;
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

    protected String getParentEncoding(final String parentUrl,
            final String sessionId) {
        final String key = sessionId + ":" + parentUrl;
        String enc = parentEncodingMap.get(key);
        if (enc != null) {
            return enc;
        }

        final AccessResultDataCB cb = new AccessResultDataCB();
        cb.query().queryAccessResult().setSessionId_Equal(sessionId);
        cb.query().queryAccessResult().setUrl_Equal(parentUrl);
        cb.specify().columnEncoding();
        final AccessResultData accessResultData = SingletonS2Container
                .getComponent(AccessResultDataBhv.class).selectEntity(cb);
        if (accessResultData != null && accessResultData.getEncoding() != null) {
            enc = accessResultData.getEncoding();
            parentEncodingMap.put(key, enc);
            return enc;
        }
        return null;
    }

    @Override
    protected String getHost(final String url) {
        if (StringUtil.isBlank(url)) {
            return ""; // empty
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
            return ""; // empty
        }

        if (url.startsWith("file:////")) {
            final String value = decodeUrlAsName(url.substring(9), true);
            return StringUtils.abbreviate("\\\\" + value.replace('/', '\\'),
                    maxSiteLength);
        } else if (url.startsWith("file:")) {
            final String value = decodeUrlAsName(url.substring(5), true);
            if (value.length() > 2 && value.charAt(2) == ':') {
                // Windows
                return StringUtils.abbreviate(
                        value.substring(1).replace('/', '\\'), maxSiteLength);
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
                throw new RobotSystemException(
                        "Could not create an instanced from bytes.", e);
            }
        }
        return new HashMap<String, Object>();
    }

    public void addMetaContentMapping(final String metaname,
            final String solrField) {
        if (metaContentMapping == null) {
            metaContentMapping = new HashMap<String, String>();
        }
        metaContentMapping.put(metaname, solrField);
    }
}
