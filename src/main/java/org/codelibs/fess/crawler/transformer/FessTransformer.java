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

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.collection.LruHashMap;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.GroovyUtil;
import org.slf4j.Logger;

public interface FessTransformer {

    public static Map<String, String> parentEncodingMap = Collections.synchronizedMap(new LruHashMap<String, String>(1000));

    FessConfig getFessConfig();

    Logger getLogger();

    public default String getHost(final String u) {
        if (StringUtil.isBlank(u)) {
            return StringUtil.EMPTY; // empty
        }

        String url = u;
        final String originalUrl = url;

        int idx = url.indexOf("://");
        if (idx >= 0) {
            url = url.substring(idx + 3);
        }

        idx = url.indexOf('/');
        if (idx >= 0) {
            url = url.substring(0, idx);
        }

        if (url.equals(originalUrl)) {
            return getFessConfig().getCrawlerDocumentUnknownHostname();
        }

        return url;
    }

    public default String getSite(final String u, final String encoding) {
        if (StringUtil.isBlank(u)) {
            return StringUtil.EMPTY; // empty
        }

        String url = u;
        int idx = url.indexOf("://");
        if (idx >= 0) {
            url = url.substring(idx + 3);
        }

        idx = url.indexOf('?');
        if (idx >= 0) {
            url = url.substring(0, idx);
        }

        if (encoding != null) {
            String enc;
            if (StringUtil.isNotBlank(getFessConfig().getCrawlerDocumentSiteEncoding())) {
                if (getFessConfig().isCrawlerDocumentUseSiteEncodingOnEnglish()) {
                    if ("ISO-8859-1".equalsIgnoreCase(encoding) || "US-ASCII".equalsIgnoreCase(encoding)) {
                        enc = getFessConfig().getCrawlerDocumentSiteEncoding();
                    } else {
                        enc = encoding;
                    }
                } else {
                    enc = getFessConfig().getCrawlerDocumentSiteEncoding();
                }
            } else {
                enc = encoding;
            }

            try {
                url = URLDecoder.decode(url, enc);
            } catch (final Exception e) {}
        }

        return StringUtils.abbreviate(url, getMaxSiteLength());
    }

    public default void putResultDataBody(final Map<String, Object> dataMap, final String key, final Object value) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.getIndexFieldUrl().equals(key)) {
            dataMap.put(key, value);
        } else if (dataMap.containsKey(key)) {
            if (getFessConfig().isCrawlerDocumentAppendData()) {
                final Object oldValue = dataMap.get(key);
                final Object[] oldValues = (Object[]) oldValue;
                if (value.getClass().isArray()) {
                    final Object[] newValues = (Object[]) value;
                    final Object[] values = Arrays.copyOf(oldValues, oldValues.length + newValues.length);
                    for (int i = 0; i < newValues.length; i++) {
                        values[values.length - 1 + i] = newValues[i];
                    }
                    dataMap.put(key, values);
                } else {
                    final Object[] values = Arrays.copyOf(oldValues, oldValues.length + 1);
                    values[values.length - 1] = value;
                    dataMap.put(key, values);
                }
            } else {
                dataMap.put(key, value);
            }
        } else {
            dataMap.put(key, value);
        }
    }

    public default void putResultDataWithTemplate(final Map<String, Object> dataMap, final String key, final Object value,
            final String template) {
        Object target = value;
        if (template != null) {
            final Map<String, Object> paramMap = new HashMap<>(dataMap.size() + 1);
            paramMap.putAll(dataMap);
            paramMap.put("value", target);
            target = evaluateValue(template, paramMap);
        }
        if (key != null && target != null) {
            if (target.getClass().isArray()) {
                if (key.endsWith("_m")) {
                    putResultDataBody(dataMap, key, target);
                } else {
                    putResultDataBody(dataMap, key, Arrays.toString((Object[]) target));
                }
            } else {
                if (key.endsWith("_m")) {
                    putResultDataBody(dataMap, key, new Object[] { target });
                } else {
                    putResultDataBody(dataMap, key, target);
                }
            }
        }
    }

    public default String evaluateValue(final String template, final Map<String, Object> paramMap) {
        if (StringUtil.isEmpty(template)) {
            return StringUtil.EMPTY;
        }

        final Object value = GroovyUtil.evaluate(template, paramMap);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public default int getMaxSiteLength() {
        return getFessConfig().getCrawlerDocumentMaxSiteLengthAsInteger();
    }

    public default String getFileName(final String url, final String encoding) {
        if (StringUtil.isBlank(url)) {
            return StringUtil.EMPTY;
        }

        String u = decodeUrlAsName(url, url.startsWith("file:"));

        int idx = u.lastIndexOf('?');
        if (idx >= 0) {
            u = u.substring(0, idx);
        }

        idx = u.lastIndexOf('#');
        if (idx >= 0) {
            u = u.substring(0, idx);
        }

        idx = u.lastIndexOf('/');
        if (idx >= 0) {
            if (u.length() > idx + 1) {
                u = u.substring(idx + 1);
            } else {
                u = StringUtil.EMPTY;
            }
        }

        try {
            u = URLDecoder.decode(u, encoding);
        } catch (final Exception e) {
            // ignore
        }
        return u;
    }

    public default String decodeUrlAsName(final String url, final boolean escapePlus) {
        if (url == null) {
            return null;
        }

        final FessConfig fessConfig = getFessConfig();
        String enc = Constants.UTF_8;
        if (StringUtil.isBlank(fessConfig.getCrawlerDocumentFileNameEncoding())) {
            final UrlQueue<?> urlQueue = CrawlingParameterUtil.getUrlQueue();
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
            enc = fessConfig.getCrawlerDocumentFileNameEncoding();
        }

        final String escapedUrl = escapePlus ? url.replace("+", "%2B") : url;
        try {
            return URLDecoder.decode(escapedUrl, enc);
        } catch (final Exception e) {
            return url;
        }
    }

    public default String getParentEncoding(final String parentUrl, final String sessionId) {
        final String key = sessionId + ":" + parentUrl;
        String enc = parentEncodingMap.get(key);
        if (enc != null) {
            return enc;
        }

        final AccessResult<?> accessResult = ComponentUtil.getDataService().getAccessResult(sessionId, parentUrl);
        if (accessResult != null) {
            final AccessResultData<?> accessResultData = accessResult.getAccessResultData();
            if (accessResultData != null && accessResultData.getEncoding() != null) {
                enc = accessResultData.getEncoding();
                parentEncodingMap.put(key, enc);
                return enc;
            }
        }
        return null;
    }
}