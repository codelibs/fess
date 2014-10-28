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

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jp.sf.fess.helper.FieldHelper;
import jp.sf.fess.util.ComponentUtil;

import org.apache.commons.lang.StringUtils;
import org.codelibs.core.util.StringUtil;
import org.codelibs.robot.transformer.impl.XpathTransformer;
import org.seasar.framework.util.OgnlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFessXpathTransformer extends XpathTransformer {
    private static final Logger logger = LoggerFactory
            .getLogger(AbstractFessXpathTransformer.class);

    public int maxSiteLength = 50;

    public String unknownHostname = "unknown";

    public String siteEncoding;

    public boolean replaceSiteEncodingWhenEnglish = false;

    public boolean appendResultData = true;

    protected String getHost(final String u) {
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
            return unknownHostname;
        }

        return url;
    }

    protected String getSite(final String u, final String encoding) {
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
            if (siteEncoding != null) {
                if (replaceSiteEncodingWhenEnglish) {
                    if ("ISO-8859-1".equalsIgnoreCase(encoding)
                            || "US-ASCII".equalsIgnoreCase(encoding)) {
                        enc = siteEncoding;
                    } else {
                        enc = encoding;
                    }
                } else {
                    enc = siteEncoding;
                }
            } else {
                enc = encoding;
            }

            try {
                url = URLDecoder.decode(url, enc);
            } catch (final Exception e) {
            }
        }

        return StringUtils.abbreviate(url, maxSiteLength);
    }

    protected String normalizeContent(final String content) {
        if (content == null) {
            return StringUtil.EMPTY; // empty
        }
        return content.replaceAll("\\s+", " ");
    }

    protected void putResultDataBody(final Map<String, Object> dataMap,
            final String key, final Object value) {
        final FieldHelper fieldHelper = ComponentUtil.getFieldHelper();
        if (fieldHelper.urlField.equals(key)) {
            dataMap.put(key, value);
        } else if (dataMap.containsKey(key)) {
            if (appendResultData) {
                final Object oldValue = dataMap.get(key);
                if (key.endsWith("_m")) {
                    final Object[] oldValues = (Object[]) oldValue;
                    if (value.getClass().isArray()) {
                        final Object[] newValues = (Object[]) value;
                        final Object[] values = Arrays.copyOf(oldValues,
                                oldValues.length + newValues.length);
                        for (int i = 0; i < newValues.length; i++) {
                            values[values.length - 1 + i] = newValues[i];
                        }
                        dataMap.put(key, values);
                    } else {
                        final Object[] values = Arrays.copyOf(oldValues,
                                oldValues.length + 1);
                        values[values.length - 1] = value;
                        dataMap.put(key, values);
                    }
                } else {
                    dataMap.put(key, oldValue + " " + value);
                }
            } else {
                dataMap.put(key, value);
            }
        } else {
            dataMap.put(key, value);
        }
    }

    protected void putResultDataWithTemplate(final Map<String, Object> dataMap,
            final String key, final Object value, final String template) {
        Object target = value;
        if (template != null) {
            final Map<String, Object> paramMap = new HashMap<>(
                    dataMap.size() + 1);
            paramMap.putAll(dataMap);
            paramMap.put("value", target);
            target = evaluateValue(template, paramMap);
        }
        if (key != null && target != null) {
            if (target.getClass().isArray()) {
                if (key.endsWith("_m")) {
                    putResultDataBody(dataMap, key, target);
                } else {
                    putResultDataBody(dataMap, key,
                            Arrays.toString((Object[]) target));
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

    protected String evaluateValue(final String template,
            final Map<String, Object> paramMap) {
        if (StringUtil.isEmpty(template)) {
            return StringUtil.EMPTY;
        }

        try {
            final Object exp = OgnlUtil.parseExpression(template);
            final Object value = OgnlUtil.getValue(exp, paramMap);
            if (value == null) {
                return null;
            }
            return value.toString();
        } catch (final Exception e) {
            logger.warn("Invalid value format: " + template, e);
            return null;
        }
    }
}