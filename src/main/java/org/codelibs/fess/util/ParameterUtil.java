/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
package org.codelibs.fess.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.es.config.exentity.CrawlingConfig.ConfigName;
import org.codelibs.fess.exception.FessSystemException;

public class ParameterUtil {
    protected static final String XPATH_PREFIX = "field.xpath.";

    protected static final String META_PREFIX = "field.meta.";

    protected static final String VALUE_PREFIX = "field.value.";

    protected static final String SCRIPT_PREFIX = "field.script.";

    protected static final String CLIENT_PREFIX = "client.";

    protected static final String CONFIG_PREFIX = "config.";

    protected static final String FIELD_PREFIX = "field.config.";

    protected ParameterUtil() {
        // nothing
    }

    public static Map<String, String> parse(final String value) {
        final Map<String, String> paramMap = new LinkedHashMap<>();
        if (value != null) {
            final String[] lines = value.split("[\r\n]");
            for (final String line : lines) {
                if (StringUtil.isNotBlank(line)) {
                    final int pos = line.indexOf('=');
                    if (pos == 0) {
                        throw new FessSystemException("Invalid parameter. The key is null.");
                    } else if (pos > 0) {
                        if (pos < line.length()) {
                            paramMap.put(line.substring(0, pos).trim(), line.substring(pos + 1).trim());
                        } else {
                            paramMap.put(line.substring(0, pos).trim(), StringUtil.EMPTY);
                        }
                    } else {
                        paramMap.put(line.trim(), StringUtil.EMPTY);
                    }
                }
            }
        }
        return paramMap;
    }

    public static void loadConfigParams(final Map<String, Object> paramMap, final String configParam) {
        final Map<String, String> map = ParameterUtil.parse(configParam);
        if (!map.isEmpty()) {
            paramMap.putAll(map);
        }
    }

    public static Map<ConfigName, Map<String, String>> createConfigParameterMap(final String configParameters) {
        final Map<ConfigName, Map<String, String>> map = new HashMap<>();
        final Map<String, String> configConfigMap = new LinkedHashMap<>();
        final Map<String, String> clientConfigMap = new LinkedHashMap<>();
        final Map<String, String> xpathConfigMap = new LinkedHashMap<>();
        final Map<String, String> metaConfigMap = new LinkedHashMap<>();
        final Map<String, String> valueConfigMap = new LinkedHashMap<>();
        final Map<String, String> scriptConfigMap = new LinkedHashMap<>();
        final Map<String, String> fieldConfigMap = new LinkedHashMap<>();
        map.put(ConfigName.CONFIG, configConfigMap);
        map.put(ConfigName.CLIENT, clientConfigMap);
        map.put(ConfigName.XPATH, xpathConfigMap);
        map.put(ConfigName.META, metaConfigMap);
        map.put(ConfigName.VALUE, valueConfigMap);
        map.put(ConfigName.SCRIPT, scriptConfigMap);
        map.put(ConfigName.FIELD, fieldConfigMap);
        for (final Map.Entry<String, String> entry : ParameterUtil.parse(configParameters).entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith(CONFIG_PREFIX)) {
                configConfigMap.put(key.substring(CONFIG_PREFIX.length()), entry.getValue());
            } else if (key.startsWith(CLIENT_PREFIX)) {
                clientConfigMap.put(key.substring(CLIENT_PREFIX.length()), entry.getValue());
            } else if (key.startsWith(XPATH_PREFIX)) {
                xpathConfigMap.put(key.substring(XPATH_PREFIX.length()), entry.getValue());
            } else if (key.startsWith(META_PREFIX)) {
                metaConfigMap.put(key.substring(META_PREFIX.length()), entry.getValue());
            } else if (key.startsWith(VALUE_PREFIX)) {
                valueConfigMap.put(key.substring(VALUE_PREFIX.length()), entry.getValue());
            } else if (key.startsWith(SCRIPT_PREFIX)) {
                scriptConfigMap.put(key.substring(SCRIPT_PREFIX.length()), entry.getValue());
            } else if (key.startsWith(FIELD_PREFIX)) {
                fieldConfigMap.put(key.substring(FIELD_PREFIX.length()), entry.getValue());
            }
        }

        return map;
    }
}
