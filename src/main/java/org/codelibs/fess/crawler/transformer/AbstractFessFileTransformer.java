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

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.SerializeUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Tuple3;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.impl.TikaExtractor;
import org.codelibs.fess.crawler.transformer.impl.AbstractTransformer;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.codelibs.fess.es.config.exentity.CrawlingConfig;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.Param.Config;
import org.codelibs.fess.helper.CrawlingConfigHelper;
import org.codelibs.fess.helper.CrawlingInfoHelper;
import org.codelibs.fess.helper.DocumentHelper;
import org.codelibs.fess.helper.FileTypeHelper;
import org.codelibs.fess.helper.LabelTypeHelper;
import org.codelibs.fess.helper.PathMappingHelper;
import org.codelibs.fess.helper.PermissionHelper;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.taglib.FessFunctions;
import org.codelibs.fess.util.ComponentUtil;

public abstract class AbstractFessFileTransformer extends AbstractTransformer implements FessTransformer {

    private static final Logger logger = LogManager.getLogger(AbstractFessFileTransformer.class);

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
        final CrawlingConfigHelper crawlingConfigHelper = ComponentUtil.getCrawlingConfigHelper();
        final CrawlingConfig crawlingConfig = crawlingConfigHelper.get(responseData.getSessionId());
        final Extractor extractor = getExtractor(responseData);
        final String mimeType = responseData.getMimeType();
        final StringBuilder contentMetaBuf = new StringBuilder(1000);
        final Map<String, Object> dataMap = new HashMap<>();
        final Map<String, Object> metaDataMap = new HashMap<>();
        String content;
        try (final InputStream in = responseData.getResponseBody()) {
            final ExtractData extractData = getExtractData(extractor, in, createExtractParams(responseData, crawlingConfig));
            content = extractData.getContent();
            if (fessConfig.isCrawlerDocumentFileIgnoreEmptyContent() && StringUtil.isBlank(content)) {
                return null;
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("ExtractData: {}", extractData);
            }
            // meta
            extractData.getKeySet().stream().filter(k -> extractData.getValues(k) != null).forEach(key -> {
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

                final Tuple3<String, String, String> mapping = fessConfig.getCrawlerMetadataNameMapping(key);
                if (mapping != null) {
                    if (Constants.MAPPING_TYPE_ARRAY.equalsIgnoreCase(mapping.getValue2())) {
                        dataMap.put(mapping.getValue1(), values);
                    } else if (Constants.MAPPING_TYPE_STRING.equalsIgnoreCase(mapping.getValue2())) {
                        final String joinedValue = StringUtils.join(values, ' ');
                        dataMap.put(mapping.getValue1(), joinedValue.trim());
                    } else if (values.length == 1) {
                        try {
                            if (Constants.MAPPING_TYPE_LONG.equalsIgnoreCase(mapping.getValue2())) {
                                dataMap.put(mapping.getValue1(), Long.parseLong(values[0]));
                            } else if (Constants.MAPPING_TYPE_DOUBLE.equalsIgnoreCase(mapping.getValue2())) {
                                dataMap.put(mapping.getValue1(), Double.parseDouble(values[0]));
                            } else if (Constants.MAPPING_TYPE_DATE.equalsIgnoreCase(mapping.getValue2())
                                    || Constants.MAPPING_TYPE_PDF_DATE.equalsIgnoreCase(mapping.getValue2())) {
                                final String dateFormate;
                                if (StringUtil.isNotBlank(mapping.getValue3())) {
                                    dateFormate = mapping.getValue3();
                                } else if (Constants.MAPPING_TYPE_PDF_DATE.equalsIgnoreCase(mapping.getValue2())) {
                                    dateFormate = mapping.getValue2();
                                } else {
                                    dateFormate = Constants.DATE_OPTIONAL_TIME;
                                }
                                final Date dt = FessFunctions.parseDate(values[0], dateFormate);
                                if (dt != null) {
                                    dataMap.put(mapping.getValue1(), FessFunctions.formatDate(dt));
                                } else {
                                    logger.warn("Failed to parse {}", mapping);
                                }
                            } else {
                                logger.warn("Unknown mapping type: {}={}", key, mapping);
                            }
                        } catch (final Exception e) {
                            logger.warn("Failed to parse {}", values[0], e);
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

        final CrawlingInfoHelper crawlingInfoHelper = ComponentUtil.getCrawlingInfoHelper();
        final String sessionId = crawlingInfoHelper.getCanonicalSessionId(responseData.getSessionId());
        final PathMappingHelper pathMappingHelper = ComponentUtil.getPathMappingHelper();
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
        final String fileName = getFileName(url, urlEncoding);
        if (StringUtil.isNotBlank(fileName) && fessConfig.isCrawlerDocumentAppendFilename()) {
            buf.append(' ').append(fileName);
        }
        final String bodyBase = buf.toString().trim();
        responseData.addMetaData(Extractor.class.getSimpleName(), extractor);
        final String body = documentHelper.getContent(crawlingConfig, responseData, bodyBase, dataMap);
        putResultDataBody(dataMap, fessConfig.getIndexFieldContent(), body);
        if ((Constants.TRUE.equalsIgnoreCase(fieldConfigMap.get(fessConfig.getIndexFieldCache()))
                || fessConfig.isCrawlerDocumentCacheEnabled()) && fessConfig.isSupportedDocumentCacheMimetypes(mimeType)) {
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
        if (!hasTitle(dataMap)) {
            final String titleField = fessConfig.getIndexFieldTitle();
            dataMap.remove(titleField);
            if (url.endsWith("/")) {
                if (StringUtil.isNotBlank(content)) {
                    putResultDataBody(dataMap, titleField, documentHelper.getDigest(responseData, body, dataMap,
                            fessConfig.getCrawlerDocumentFileMaxTitleLengthAsInteger()));
                } else {
                    putResultDataBody(dataMap, titleField, fessConfig.getCrawlerDocumentFileNoTitleLabel());
                }
            } else if (StringUtil.isBlank(fileName)) {
                putResultDataBody(dataMap, titleField, decodeUrlAsName(url, url.startsWith("file:")));
            } else {
                putResultDataBody(dataMap, titleField, fileName);
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
        final Date lastModified = getLastModified(dataMap, responseData);
        if (lastModified != null) {
            dataMap.put(fessConfig.getIndexFieldLastModified(), lastModified); // overwrite
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
        final LabelTypeHelper labelTypeHelper = ComponentUtil.getLabelTypeHelper();
        putResultDataBody(dataMap, fessConfig.getIndexFieldLabel(), labelTypeHelper.getMatchedLabelValueSet(url));
        // role: roleType
        final List<String> roleTypeList = getRoleTypes(responseData);
        stream(crawlingConfig.getPermissions()).of(stream -> stream.forEach(p -> roleTypeList.add(p)));
        putResultDataBody(dataMap, fessConfig.getIndexFieldRole(), roleTypeList);
        // virtualHosts
        putResultDataBody(dataMap, fessConfig.getIndexFieldVirtualHost(),
                stream(crawlingConfig.getVirtualHosts()).get(stream -> stream.filter(StringUtil::isNotBlank).collect(Collectors.toList())));
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
        // thumbnail
        putResultDataBody(dataMap, fessConfig.getIndexFieldThumbnail(), responseData.getUrl());

        // from config
        final String scriptType = crawlingConfig.getScriptType();
        final Map<String, String> scriptConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.SCRIPT);
        final Map<String, String> metaConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.META);
        for (final Map.Entry<String, String> entry : metaConfigMap.entrySet()) {
            final String key = entry.getKey();
            final String[] values = entry.getValue().split(",");
            for (final String value : values) {
                putResultDataWithTemplate(dataMap, key, metaDataMap.get(value), scriptConfigMap.get(key), scriptType);
            }
        }
        final Map<String, String> valueConfigMap = crawlingConfig.getConfigParameterMap(ConfigName.VALUE);
        for (final Map.Entry<String, String> entry : valueConfigMap.entrySet()) {
            final String key = entry.getKey();
            putResultDataWithTemplate(dataMap, key, entry.getValue(), scriptConfigMap.get(key), scriptType);
        }

        return dataMap;
    }

    protected Date getLastModified(final Map<String, Object> dataMap, final ResponseData responseData) {
        final Object lastModifiedObj = dataMap.get(fessConfig.getIndexFieldLastModified());
        if (lastModifiedObj instanceof Date) {
            return (Date) lastModifiedObj;
        }
        if (lastModifiedObj instanceof String) {
            final Date lastModified = FessFunctions.parseDate(lastModifiedObj.toString());
            if (lastModified != null) {
                return lastModified;
            }
        } else if ((lastModifiedObj instanceof final String[] lastModifieds) && (lastModifieds.length > 0)) {
            final Date lastModified = FessFunctions.parseDate(lastModifieds[0]);
            if (lastModified != null) {
                return lastModified;
            }
        }

        return responseData.getLastModified();
    }

    protected boolean hasTitle(final Map<String, Object> dataMap) {
        final Object titleObj = dataMap.get(fessConfig.getIndexFieldTitle());
        if (titleObj != null) {
            if (titleObj instanceof String[]) {
                return stream((String[]) titleObj).get(stream -> stream.anyMatch(StringUtil::isNotBlank));
            }
            return StringUtil.isNotBlank(titleObj.toString());
        }
        return false;
    }

    protected Map<String, String> createExtractParams(final ResponseData responseData, final CrawlingConfig crawlingConfig) {
        final Map<String, String> params = new HashMap<>(crawlingConfig.getConfigParameterMap(ConfigName.CONFIG));
        params.put(ExtractData.RESOURCE_NAME_KEY, getResourceName(responseData));
        params.put(ExtractData.CONTENT_TYPE, responseData.getMimeType());
        params.put(ExtractData.CONTENT_ENCODING, responseData.getCharSet());
        params.put(ExtractData.URL, responseData.getUrl());
        final Map<String, String> configParam = crawlingConfig.getConfigParameterMap(ConfigName.CONFIG);
        if (configParam != null) {
            final String keepOriginalBody = configParam.get(Config.KEEP_ORIGINAL_BODY);
            if (StringUtil.isNotBlank(keepOriginalBody)) {
                params.put(TikaExtractor.NORMALIZE_TEXT,
                        Constants.TRUE.equalsIgnoreCase(keepOriginalBody) ? Constants.FALSE : Constants.TRUE);
            }
        }
        return params;
    }

    protected ExtractData getExtractData(final Extractor extractor, final InputStream in, final Map<String, String> params) {
        try {
            return extractor.getText(in, params);
        } catch (final RuntimeException e) {
            if (!fessConfig.isCrawlerIgnoreContentException()) {
                throw e;
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Could not get a text.", e);
            }
        }
        return new ExtractData();
    }

    protected String getResourceName(final ResponseData responseData) {
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
            }
            if (pos == -1) {
                return value;
            }
            return "localhost";
        }
        if (url.startsWith("file:")) {
            return "localhost";
        }

        return getHost(url);
    }

    protected List<String> getRoleTypes(final ResponseData responseData) {
        final List<String> roleTypeList = new ArrayList<>();
        final PermissionHelper permissionHelper = ComponentUtil.getPermissionHelper();

        roleTypeList.addAll(permissionHelper.getSmbRoleTypeList(responseData));
        roleTypeList.addAll(permissionHelper.getFileRoleTypeList(responseData));
        roleTypeList.addAll(permissionHelper.getFtpRoleTypeList(responseData));

        return roleTypeList;
    }

    protected String getSiteOnFile(final String url, final String encoding) {
        if (StringUtil.isBlank(url)) {
            return StringUtil.EMPTY; // empty
        }

        if (url.startsWith("file:////")) {
            final String value = decodeUrlAsName(url.substring(9), true);
            return abbreviateSite("\\\\" + value.replace('/', '\\'));
        }
        if (url.startsWith("file:")) {
            final String value = decodeUrlAsName(url.substring(5), true);
            if (value.length() > 2 && value.charAt(2) == ':') {
                // Windows
                return abbreviateSite(value.substring(1).replace('/', '\\'));
            }
            // Unix
            return abbreviateSite(value);
        }
        if (url.startsWith("smb:") || url.startsWith("smb1:")) {
            final String value = url.replaceFirst("^smb.?:/+", StringUtil.EMPTY);
            return abbreviateSite("\\\\" + value.replace('/', '\\'));
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
