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

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.codelibs.core.io.SerializeUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.client.smb.SmbClient;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.transformer.impl.AbstractTransformer;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.DocumentHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.SambaHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcifs.smb.ACE;
import jcifs.smb.SID;

public abstract class AbstractFessFileTransformer extends AbstractTransformer implements FessTransformer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFessFileTransformer.class);

    protected Map<String, String> metaContentMapping;

    protected FessConfig fessConfig;

    protected abstract Extractor getExtractor(ResponseData responseData);

    @Override
    public ResultData transform(final ResponseData responseData) {
        if (responseData == null || !responseData.hasResponseBody()) {
            throw new CrawlingAccessException("No response body.");
        }

        final ResultData resultData = new ResultData();
        resultData.setTransformerName(getName());
        try {
            resultData.setData(SerializeUtil.fromObjectToBinary(generateData(responseData)));
        } catch (final Exception e) {
            throw new CrawlingAccessException("Could not serialize object", e);
        }
        resultData.setEncoding(fessConfig.getCrawlerCrawlingDataEncoding());

        return resultData;
    }

    protected Map<String, Object> generateData(final ResponseData responseData) {
        final Extractor extractor = getExtractor(responseData);
        final Map<String, String> params = new HashMap<>();
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, getResourceName(responseData));
        final String mimeType = responseData.getMimeType();
        params.put(HttpHeaders.CONTENT_TYPE, mimeType);
        params.put(HttpHeaders.CONTENT_ENCODING, responseData.getCharSet());
        final StringBuilder contentMetaBuf = new StringBuilder(1000);
        final Map<String, Object> dataMap = new HashMap<>();
        final Map<String, Object> metaDataMap = new HashMap<>();
        String content;
        try (final InputStream in = responseData.getResponseBody()) {
            final ExtractData extractData = getExtractData(extractor, in, params);
            content = extractData.getContent();
            if (fessConfig.isCrawlerDocumentFileIgnoreEmptyContent() && StringUtil.isBlank(content)) {
                return null;
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("ExtractData: " + extractData);
            }
            // meta
            extractData.getKeySet().stream()//
                    .filter(k -> extractData.getValues(k) != null)//
                    .forEach(key -> {
                        final String[] values = extractData.getValues(key);
                        metaDataMap.put(key, values);

                        // meta -> content
                            if (fessConfig.isCrawlerMetadataContentIncluded(key)) {
                                final String joinedValue = StringUtils.join(values, ' ');
                                if (StringUtil.isNotBlank(joinedValue)) {
                                    if (contentMetaBuf.length() > 0) {
                                        contentMetaBuf.append(' ');
                                    }
                                    contentMetaBuf.append(joinedValue.trim());
                                }
                            }

                            final Pair<String, String> mapping = fessConfig.getCrawlerMetadataNameMapping(key);
                            if (mapping != null) {
                                if (Constants.MAPPING_TYPE_ARRAY.equalsIgnoreCase(mapping.getSecond())) {
                                    dataMap.put(mapping.getFirst(), values);
                                } else if (Constants.MAPPING_TYPE_STRING.equalsIgnoreCase(mapping.getSecond())) {
                                    final String joinedValue = StringUtils.join(values, ' ');
                                    dataMap.put(mapping.getFirst(), joinedValue.trim());
                                } else if (values.length == 1) {
                                    try {
                                        if (Constants.MAPPING_TYPE_LONG.equalsIgnoreCase(mapping.getSecond())) {
                                            dataMap.put(mapping.getFirst(), Long.parseLong(values[0]));
                                        } else if (Constants.MAPPING_TYPE_DOUBLE.equalsIgnoreCase(mapping.getSecond())) {
                                            dataMap.put(mapping.getFirst(), Double.parseDouble(values[0]));
                                        } else {
                                            logger.warn("Unknown mapping type: {}={}", key, mapping);
                                        }
                                    } catch (final NumberFormatException e) {
                                        logger.warn("Failed to parse " + values[0], e);
                                    }
                                }
                            }

                        });
        } catch (final Exception e) {
            final CrawlingAccessException rcae = new CrawlingAccessException("Could not get a text from " + responseData.getUrl(), e);
            rcae.setLogLevel(CrawlingAccessException.WARN);
            throw rcae;
        }
        if (content == null) {
            content = StringUtil.EMPTY;
        }
        final String contentMeta = contentMetaBuf.toString().trim();

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
        String url = responseData.getUrl();
        final String indexingTarget = crawlingConfig.getIndexingTarget(url);
        url = pathMappingHelper.replaceUrl(sessionId, url);

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
        // segment
        putResultDataBody(dataMap, fessConfig.getIndexFieldSegment(), sessionId);
        // content
        final StringBuilder buf = new StringBuilder(content.length() + 1000);
        if (fessConfig.isCrawlerDocumentFileAppendBodyContent()) {
            buf.append(content);
        }
        if (fessConfig.isCrawlerDocumentFileAppendMetaContent()) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append(contentMeta);
        }
        final String bodyBase = buf.toString().trim();
        final String body = documentHelper.getContent(responseData, bodyBase, dataMap);
        putResultDataBody(dataMap, fessConfig.getIndexFieldContent(), body);
        if ((Constants.TRUE.equalsIgnoreCase(fieldConfigMap.get(fessConfig.getIndexFieldCache())) || fessConfig
                .isCrawlerDocumentCacheEnabled()) && fessConfig.isSupportedDocumentCacheMimetypes(mimeType)) {
            if (responseData.getContentLength() > 0
                    && responseData.getContentLength() <= fessConfig.getCrawlerDocumentCacheMaxSizeAsInteger().longValue()) {

                final String cache = content.trim().replaceAll("[ \\t\\x0B\\f]+", " ");
                // text cache
                putResultDataBody(dataMap, fessConfig.getIndexFieldCache(), cache);
                putResultDataBody(dataMap, fessConfig.getIndexFieldHasCache(), Constants.TRUE);
            }
        }
        // digest
        putResultDataBody(dataMap, fessConfig.getIndexFieldDigest(),
                documentHelper.getDigest(responseData, bodyBase, dataMap, fessConfig.getCrawlerDocumentFileMaxDigestLengthAsInteger()));
        // title
        final String fileName = getFileName(url, urlEncoding);
        if (!dataMap.containsKey(fessConfig.getIndexFieldTitle())) {
            if (url.endsWith("/")) {
                if (StringUtil.isNotBlank(content)) {
                    putResultDataBody(
                            dataMap,
                            fessConfig.getIndexFieldTitle(),
                            documentHelper.getDigest(responseData, body, dataMap,
                                    fessConfig.getCrawlerDocumentFileMaxTitleLengthAsInteger()));
                } else {
                    putResultDataBody(dataMap, fessConfig.getIndexFieldTitle(), fessConfig.getCrawlerDocumentFileNoTitleLabel());
                }
            } else {
                if (StringUtil.isBlank(fileName)) {
                    putResultDataBody(dataMap, fessConfig.getIndexFieldTitle(), decodeUrlAsName(url, url.startsWith("file:")));
                } else {
                    putResultDataBody(dataMap, fessConfig.getIndexFieldTitle(), fileName);
                }
            }
        }
        // host
        putResultDataBody(dataMap, fessConfig.getIndexFieldHost(), getHostOnFile(url));
        // site
        putResultDataBody(dataMap, fessConfig.getIndexFieldSite(), getSiteOnFile(url, urlEncoding));
        // filename
        if (StringUtil.isNotBlank(fileName)) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldFilename(), fileName);
        }
        // url
        putResultDataBody(dataMap, fessConfig.getIndexFieldUrl(), url);
        // created
        final Date now = systemHelper.getCurrentTime();
        putResultDataBody(dataMap, fessConfig.getIndexFieldCreated(), now);
        // TODO anchor
        putResultDataBody(dataMap, fessConfig.getIndexFieldAnchor(), StringUtil.EMPTY);
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
        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();
        labelTypeSet.addAll(labelTypeHelper.getMatchedLabelValueSet(url));
        putResultDataBody(dataMap, fessConfig.getIndexFieldLabel(), labelTypeSet);
        // role: roleType
        final List<String> roleTypeList = getRoleTypes(responseData);
        stream(crawlingConfig.getPermissions()).of(stream -> stream.forEach(p -> roleTypeList.add(p)));
        putResultDataBody(dataMap, fessConfig.getIndexFieldRole(), roleTypeList);
        // TODO date
        // lang
        if (StringUtil.isNotBlank(fessConfig.getCrawlerDocumentFileDefaultLang())) {
            putResultDataBody(dataMap, fessConfig.getIndexFieldLang(), fessConfig.getCrawlerDocumentFileDefaultLang());
        }
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

        return dataMap;
    }

    private ExtractData getExtractData(final Extractor extractor, final InputStream in, final Map<String, String> params) {
        try {
            return extractor.getText(in, params);
        } catch (RuntimeException e) {
            if (!fessConfig.isCrawlerIgnoreContentException()) {
                throw e;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Could not get a text.", e);
            }
        }
        return new ExtractData();
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

    protected String getHostOnFile(final String url) {
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

        return getHost(url);
    }

    protected List<String> getRoleTypes(final ResponseData responseData) {
        final List<String> roleTypeList = new ArrayList<>();

        if (fessConfig.isSmbRoleFromFile() && responseData.getUrl().startsWith("smb://")) {
            final SambaHelper sambaHelper = ComponentUtil.getSambaHelper();
            final ACE[] aces = (ACE[]) responseData.getMetaDataMap().get(SmbClient.SMB_ACCESS_CONTROL_ENTRIES);
            if (aces != null) {
                for (final ACE item : aces) {
                    final SID sid = item.getSID();
                    final String accountId = sambaHelper.getAccountId(sid);
                    if (accountId != null) {
                        roleTypeList.add(accountId);
                    }
                }
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("smbUrl:" + responseData.getUrl() + " roleType:" + roleTypeList.toString());
                }
            }
        }

        return roleTypeList;
    }

    protected String getSiteOnFile(final String url, final String encoding) {
        if (StringUtil.isBlank(url)) {
            return StringUtil.EMPTY; // empty
        }

        if (url.startsWith("file:////")) {
            final String value = decodeUrlAsName(url.substring(9), true);
            return StringUtils.abbreviate("\\\\" + value.replace('/', '\\'), getMaxSiteLength());
        } else if (url.startsWith("file:")) {
            final String value = decodeUrlAsName(url.substring(5), true);
            if (value.length() > 2 && value.charAt(2) == ':') {
                // Windows
                return StringUtils.abbreviate(value.substring(1).replace('/', '\\'), getMaxSiteLength());
            } else {
                // Unix
                return StringUtils.abbreviate(value, getMaxSiteLength());
            }
        }

        return getSite(url, encoding);
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

    public void addMetaContentMapping(final String metaname, final String dynamicField) {
        if (metaContentMapping == null) {
            metaContentMapping = new HashMap<>();
        }
        metaContentMapping.put(metaname, dynamicField);
    }

}