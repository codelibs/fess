/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.slf4j.Logger;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public interface FessTransformer {

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

        try {
            final Object value = new GroovyShell(new Binding(paramMap)).evaluate(template);
            if (value == null) {
                return null;
            }
            return value.toString();
        } catch (final Exception e) {
            getLogger().warn("Invalid value format: " + template, e);
            return null;
        }
    }

    public default int getMaxSiteLength() {
        return getFessConfig().getCrawlerDocumentMaxSiteLengthAsInteger();
    }

}