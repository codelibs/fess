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
package org.codelibs.fess.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.Constants;
import org.codelibs.fess.opensearch.config.exentity.CrawlingConfig.ConfigName;
import org.lastaflute.core.security.PrimaryCipher;
import org.lastaflute.core.security.exception.CipherFailureException;

/**
 * Utility class for parameter operations.
 */
public class ParameterUtil {
    private static final String CIPHER_PREFIX = Constants.CIPHER_PREFIX;

    /** The XPath field prefix. */
    protected static final String XPATH_PREFIX = "field.xpath.";

    /** The meta field prefix. */
    protected static final String META_PREFIX = "field.meta.";

    /** The value field prefix. */
    protected static final String VALUE_PREFIX = "field.value.";

    /** The script field prefix. */
    protected static final String SCRIPT_PREFIX = "field.script.";

    /** The client prefix. */
    protected static final String CLIENT_PREFIX = "client.";

    /** The config prefix. */
    protected static final String CONFIG_PREFIX = "config.";

    /** The field config prefix. */
    protected static final String FIELD_PREFIX = "field.config.";

    /**
     * Protected constructor.
     */
    protected ParameterUtil() {
        // nothing
    }

    /**
     * Parses parameter string into a map.
     *
     * @param value the parameter string
     * @return the parameter map
     */
    public static Map<String, String> parse(final String value) {
        return parse(value, null);
    }

    /**
     * Parses parameter string into a map, naming the owner of the parameters when a value cannot be decrypted.
     *
     * <p>A value that was encrypted with another {@code app.cipher.key} cannot be decrypted. The exception then
     * names the owner and the parameter key, and points to {@code app.cipher.key}. Neither the encrypted value
     * nor the cipher key is part of the message, and the original exception, whose message repeats the
     * encrypted value, is not chained.</p>
     *
     * @param value the parameter string
     * @param owner a description of the setting the parameters belong to, such as its type, name and ID; may be null
     * @return the parameter map
     * @throws CipherFailureException if an encrypted value cannot be decrypted
     */
    public static Map<String, String> parse(final String value, final String owner) {
        final Map<String, String> paramMap = new LinkedHashMap<>();
        if (value != null) {
            int unknownKey = 0;
            final Pattern properyPattern = Pattern.compile(ComponentUtil.getFessConfig().getAppEncryptPropertyPattern());
            final PrimaryCipher cipher = ComponentUtil.getPrimaryCipher();
            final String[] lines = value.split("[\r\n]");
            for (final String line : lines) {
                if (StringUtil.isNotBlank(line)) {
                    final int pos = line.indexOf('=');
                    if (pos == 0) {
                        paramMap.put("unknown." + (unknownKey + 1), line.substring(pos + 1).trim());
                        unknownKey++;
                    } else if (pos > 0) {
                        final String key = line.substring(0, pos).trim();
                        if (pos < line.length()) {
                            String data = line.substring(pos + 1).trim();
                            if (properyPattern.matcher(key).matches() && data.startsWith(CIPHER_PREFIX)) {
                                try {
                                    data = cipher.decrypt(data.substring(CIPHER_PREFIX.length()));
                                } catch (final CipherFailureException e) {
                                    throw new CipherFailureException("Failed to decrypt a parameter: "
                                            + (owner != null ? "owner=" + owner + ", " : "") + "key=" + key
                                            + ". The cipher key (app.cipher.key) may differ from the key used when the value was saved.",
                                            e.getCause());
                                }
                            }
                            paramMap.put(key, data);
                        } else {
                            paramMap.put(key, StringUtil.EMPTY);
                        }
                    } else {
                        paramMap.put(line.trim(), StringUtil.EMPTY);
                    }
                }
            }
        }
        return paramMap;
    }

    /**
     * Appends a parameter line unless the key is already present.
     *
     * <p>Used where a setting is created without a screen having filled the defaults in, so a
     * value the caller supplied - including an empty one, which is a deliberate choice - is left
     * alone.</p>
     *
     * @param parameters the parameter string, may be null or blank
     * @param key the parameter key
     * @param value the value to add when the key is absent
     * @return the parameter string containing the key
     */
    public static String addIfAbsent(final String parameters, final String key, final String value) {
        if (parameters != null && parse(parameters).containsKey(key)) {
            return parameters;
        }
        final String line = key + "=" + value;
        if (StringUtil.isBlank(parameters)) {
            return line;
        }
        if (parameters.endsWith("\n") || parameters.endsWith("\r")) {
            return parameters + line;
        }
        return parameters + "\n" + line;
    }

    /**
     * Encrypts sensitive parameter values.
     *
     * @param value the parameter string
     * @return the encrypted parameter string
     */
    public static String encrypt(final String value) {
        final StringBuilder buf = new StringBuilder();
        final Pattern properyPattern = Pattern.compile(ComponentUtil.getFessConfig().getAppEncryptPropertyPattern());
        final PrimaryCipher cipher = ComponentUtil.getPrimaryCipher();
        ParameterUtil.parse(value).entrySet().stream().map(e -> {
            final String k = e.getKey();
            final String v = e.getValue();
            if (properyPattern.matcher(k).matches() && !v.startsWith(CIPHER_PREFIX)) {
                return new Pair<>(k, CIPHER_PREFIX + cipher.encrypt(v));
            }
            return new Pair<>(k, v);
        }).forEach(e -> {
            if (buf.length() > 0) {
                buf.append('\n');
            }
            buf.append(e.getFirst());
            buf.append('=');
            buf.append(e.getSecond());
        });
        return buf.toString();
    }

    /**
     * Encrypts a credential that is stored in a field of its own, such as the password of an authentication
     * setting, with the same {@code {cipher}} form and key ({@code app.cipher.key}) as the encrypted parameters.
     *
     * <p>A null or empty value is returned as it is. Any other value is encrypted, whatever it starts with: a value
     * that was submitted from a screen or the API is plain text even when it looks like an encrypted one, and
     * decrypting the result gives back exactly what was submitted. A stored value must be decrypted with
     * {@link #decryptCredential(String, String, String)} before it is saved again.</p>
     *
     * @param value the plain text credential
     * @return the encrypted credential
     */
    public static String encryptCredential(final String value) {
        if (StringUtil.isEmpty(value)) {
            return value;
        }
        return CIPHER_PREFIX + ComponentUtil.getPrimaryCipher().encrypt(value);
    }

    /**
     * Decrypts a credential that {@link #encryptCredential(String)} stored. A value without the
     * {@code {cipher}} prefix, such as one stored before the credential was encrypted, is plain text and is
     * returned as it is.
     *
     * <p>A value that was encrypted with another {@code app.cipher.key} cannot be decrypted. The exception then
     * names the owner and the field, and points to {@code app.cipher.key}. Neither the encrypted value nor the
     * cipher key is part of the message, and the original exception, whose message repeats the encrypted value,
     * is not chained.</p>
     *
     * @param value the stored credential
     * @param owner a description of the setting the credential belongs to, such as its type and ID; may be null
     * @param field the name of the field that holds the credential
     * @return the plain text credential
     * @throws CipherFailureException if the credential cannot be decrypted
     */
    public static String decryptCredential(final String value, final String owner, final String field) {
        if (StringUtil.isEmpty(value) || !value.startsWith(CIPHER_PREFIX)) {
            return value;
        }
        try {
            return ComponentUtil.getPrimaryCipher().decrypt(value.substring(CIPHER_PREFIX.length()));
        } catch (final CipherFailureException e) {
            throw new CipherFailureException("Failed to decrypt a credential: " + (owner != null ? "owner=" + owner + ", " : "") + "field="
                    + field + ". The cipher key (app.cipher.key) may differ from the key used when the value was saved.", e.getCause());
        }
    }

    /**
     * Loads configuration parameters into a map.
     *
     * @param paramMap the parameter map
     * @param configParam the configuration parameter string
     */
    public static void loadConfigParams(final Map<String, Object> paramMap, final String configParam) {
        final Map<String, String> map = ParameterUtil.parse(configParam);
        if (!map.isEmpty()) {
            paramMap.putAll(map);
        }
    }

    /**
     * Creates a configuration parameter map.
     *
     * @param configParameters the configuration parameters
     * @return the configuration parameter map
     */
    public static Map<ConfigName, Map<String, String>> createConfigParameterMap(final String configParameters) {
        return createConfigParameterMap(configParameters, null);
    }

    /**
     * Creates a configuration parameter map, naming the owner of the parameters when a value cannot be decrypted.
     *
     * @param configParameters the configuration parameters
     * @param owner a description of the setting the parameters belong to; may be null
     * @return the configuration parameter map
     * @see #parse(String, String)
     */
    public static Map<ConfigName, Map<String, String>> createConfigParameterMap(final String configParameters, final String owner) {
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
        for (final Map.Entry<String, String> entry : ParameterUtil.parse(configParameters, owner).entrySet()) {
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
