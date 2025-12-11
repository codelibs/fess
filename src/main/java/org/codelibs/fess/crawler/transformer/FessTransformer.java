/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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

import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.collection.LruHashMap;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.Constants;
import org.codelibs.fess.crawler.entity.AccessResult;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.codelibs.fess.crawler.util.FieldConfigs;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;

/**
 * Interface for transforming and processing crawled documents in Fess.
 * Provides utility methods for URL processing, site extraction, data mapping,
 * and field configuration handling during the document transformation process.
 */
public interface FessTransformer {

    /**
     * Synchronized LRU cache for storing parent URL encodings.
     * Maps session+parent URL keys to their corresponding character encodings.
     */
    Map<String, String> parentEncodingMap = Collections.synchronizedMap(new LruHashMap<>(1000));

    /**
     * Gets the Fess configuration instance.
     *
     * @return the Fess configuration object
     */
    FessConfig getFessConfig();

    /**
     * Gets the logger instance for this transformer.
     *
     * @return the logger instance
     */
    Logger getLogger();

    /**
     * Extracts the host name from a URL string.
     * Removes protocol and path components to return just the hostname.
     *
     * @param u the URL string to extract host from
     * @return the host name, or empty string if URL is blank, or unknown hostname if parsing fails
     */
    default String getHost(final String u) {
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

    /**
     * Extracts and processes the site path from a URL with proper encoding handling.
     * Removes protocol, query parameters, and applies URL decoding based on encoding settings.
     *
     * @param u the URL string to process
     * @param encoding the character encoding to use for URL decoding
     * @return the processed site path, abbreviated if necessary
     */
    default String getSite(final String u, final String encoding) {
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
            if (StringUtil.isNotBlank(getFessConfig().getCrawlerDocumentSiteEncoding())
                    && (!getFessConfig().isCrawlerDocumentUseSiteEncodingOnEnglish() || "ISO-8859-1".equalsIgnoreCase(encoding)
                            || "US-ASCII".equalsIgnoreCase(encoding))) {
                enc = getFessConfig().getCrawlerDocumentSiteEncoding();
            } else {
                enc = encoding;
            }

            try {
                url = URLDecoder.decode(url, enc);
            } catch (final Exception e) {
                // Failed to decode URL, using original URL as-is
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Failed to decode URL with encoding {}: {}", enc, url, e);
                }
            }
        }

        return abbreviateSite(url);
    }

    /**
     * Puts data into the result data map, handling value appending if configured.
     * If data appending is enabled and the key already exists, values are combined into arrays.
     *
     * @param dataMap the data map to modify
     * @param key the key to store the value under
     * @param value the value to store
     */
    default void putResultDataBody(final Map<String, Object> dataMap, final String key, final Object value) {
        final FessConfig fessConfig = ComponentUtil.getFessConfig();
        if (fessConfig.getIndexFieldUrl().equals(key) || !dataMap.containsKey(key) || !getFessConfig().isCrawlerDocumentAppendData()) {
            dataMap.put(key, value);
        } else {
            final Object oldValue = dataMap.get(key);
            final Object[] oldValues;
            if (oldValue instanceof Object[]) {
                oldValues = (Object[]) oldValue;
            } else if (oldValue instanceof Collection<?>) {
                oldValues = ((Collection<?>) oldValue).toArray();
            } else {
                oldValues = new Object[] { oldValue };
            }
            if (value.getClass().isArray()) {
                // Handle both Object[] and primitive arrays safely
                final int newLength = Array.getLength(value);
                final Object[] values = Arrays.copyOf(oldValues, oldValues.length + newLength);
                for (int i = 0; i < newLength; i++) {
                    values[oldValues.length + i] = Array.get(value, i);
                }
                dataMap.put(key, values);
            } else {
                final Object[] values = Arrays.copyOf(oldValues, oldValues.length + 1);
                values[values.length - 1] = value;
                dataMap.put(key, values);
            }
        }
    }

    /**
     * Puts data into the result data map after processing it through a template script.
     * The template is evaluated using the specified script engine with the value and context.
     *
     * @param dataMap the data map to modify
     * @param key the key to store the processed value under
     * @param value the original value to process
     * @param template the template script to evaluate
     * @param scriptType the type of script engine to use
     */
    default void putResultDataWithTemplate(final Map<String, Object> dataMap, final String key, final Object value, final String template,
            final String scriptType) {
        Object target = value;
        if (template != null) {
            final Map<String, Object> contextMap = new HashMap<>();
            contextMap.put("doc", dataMap);
            final Map<String, Object> paramMap = new HashMap<>(dataMap.size() + 2);
            paramMap.putAll(dataMap);
            paramMap.put("value", target);
            paramMap.put("context", contextMap);
            target = evaluateValue(scriptType, template, paramMap);
        }
        if (key != null && target != null) {
            putResultDataBody(dataMap, key, target);
        }
    }

    /**
     * Evaluates a template script using the specified script engine and parameters.
     *
     * @param scriptType the type of script engine to use
     * @param template the template script to evaluate
     * @param paramMap the parameters to pass to the script
     * @return the result of script evaluation, or empty string if template is empty
     */
    default Object evaluateValue(final String scriptType, final String template, final Map<String, Object> paramMap) {
        if (StringUtil.isEmpty(template)) {
            return StringUtil.EMPTY;
        }

        return ComponentUtil.getScriptEngineFactory().getScriptEngine(scriptType).evaluate(template, paramMap);
    }

    /**
     * Gets the maximum allowed length for site strings from configuration.
     *
     * @return the maximum site length as configured
     */
    default int getMaxSiteLength() {
        return getFessConfig().getCrawlerDocumentMaxSiteLengthAsInteger();
    }

    /**
     * Abbreviates a site string to the maximum allowed length if configured.
     *
     * @param value the site string to abbreviate
     * @return the abbreviated string, or original if no length limit is set
     */
    default String abbreviateSite(final String value) {
        final int maxSiteLength = getMaxSiteLength();
        if (maxSiteLength > -1) {
            return StringUtils.abbreviate(value, maxSiteLength);
        }
        return value;
    }

    /**
     * Extracts the filename from a URL, handling various protocols and URL decoding.
     * Processes HTTP, HTTPS, file, SMB, and FTP URLs appropriately.
     *
     * @param url the URL to extract filename from
     * @param encoding the character encoding (currently unused in this method)
     * @return the extracted filename, or empty string if none found
     */
    default String getFileName(final String url, final String encoding) {
        if (StringUtil.isBlank(url)) {
            return StringUtil.EMPTY;
        }

        int idx = 0;
        String u = url;
        if (u.startsWith("https:") || u.startsWith("http:")) {
            idx = u.lastIndexOf('?');
            if (idx >= 0) {
                u = u.substring(0, idx);
            }

            idx = u.lastIndexOf('#');
            if (idx >= 0) {
                u = u.substring(0, idx);
            }
        }
        if (!ComponentUtil.getProtocolHelper().shouldSkipUrlDecode(u)) {
            u = decodeUrlAsName(u, u.startsWith("file:"));
        }
        idx = u.lastIndexOf('/');
        if (idx >= 0) {
            if (u.length() > idx + 1) {
                u = u.substring(idx + 1);
            } else {
                u = StringUtil.EMPTY;
            }
        }
        return u;
    }

    /**
     * Decodes a URL as a name using appropriate character encoding.
     * Handles encoding detection from parent URLs and configuration settings.
     *
     * @param url the URL to decode
     * @param escapePlus whether to escape plus signs before decoding
     * @return the decoded URL name, or original URL if decoding fails
     */
    default String decodeUrlAsName(final String url, final boolean escapePlus) {
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

    /**
     * Gets the character encoding for a parent URL from cache or data service.
     * Caches encoding information to improve performance on subsequent requests.
     *
     * @param parentUrl the parent URL to get encoding for
     * @param sessionId the session ID for the crawling session
     * @return the character encoding, or null if not found
     */
    default String getParentEncoding(final String parentUrl, final String sessionId) {
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

    /**
     * Processes field configurations to handle field overwriting rules.
     * Creates a new data map with fields processed according to their configuration.
     *
     * @param dataMap the original data map to process
     * @param fieldConfigs the field configurations to apply
     * @return a new data map with configurations applied
     */
    default Map<String, Object> processFieldConfigs(final Map<String, Object> dataMap, final FieldConfigs fieldConfigs) {
        final Map<String, Object> newDataMap = new LinkedHashMap<>();
        for (final Map.Entry<String, Object> e : dataMap.entrySet()) {
            if (fieldConfigs.getConfig(e.getKey()).map(FieldConfigs.Config::isOverwrite).orElse(false)
                    && e.getValue() instanceof final Object[] values && values.length > 0) {
                newDataMap.put(e.getKey(), values[values.length - 1]);
            } else {
                newDataMap.put(e.getKey(), e.getValue());
            }
        }
        return newDataMap;
    }

}
