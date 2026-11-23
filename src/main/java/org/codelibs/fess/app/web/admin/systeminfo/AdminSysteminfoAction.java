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
package org.codelibs.fess.app.web.admin.systeminfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.ConfigEnvUtil;
import org.codelibs.fess.util.SecretKeyUtil;
import org.codelibs.fess.util.RenderDataUtil;
import org.lastaflute.core.direction.ObjectiveConfig;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.ruts.process.ActionRuntime;

import jakarta.annotation.Resource;

/**
 * Admin action for System Info.
 *
 */
public class AdminSysteminfoAction extends FessAdminAction {

    /**
     * Default constructor.
     */
    public AdminSysteminfoAction() {
    }

    /** Role name for admin system info operations */
    public static final String ROLE = "admin-systeminfo";

    /** The placeholder shown in place of a sensitive value. */
    public static final String MASKED_VALUE = "XXXXXXXX";

    /** The start of a {@code -Dkey=} option: at the start of the text or after a space or a quote. */
    private static final Pattern JVM_OPTION_PATTERN = Pattern.compile("(?<![^\\s\"'])([\"']?)-D([^\\s=\"']+)=");

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** System properties for retrieving configuration information. */
    @Resource
    protected DynamicProperties systemProperties;

    private static final String[] bugReportLabels =
            { "file.separator", "file.encoding", "java.runtime.version", "java.vm.info", "java.vm.name", "java.vm.vendor",
                    "java.vm.version", "os.arch", "os.name", "os.version", "user.country", "user.language", "user.timezone" };

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameSysteminfo()));
    }

    @Override
    protected String getActionRole() {
        return ROLE;
    }

    // ===================================================================================
    //                                                                              Index
    //                                                                      ==============
    /**
     * Displays the system information page with environment, properties, and configuration details.
     *
     * @return HTML response for the system info page
     */
    @Execute
    @Secured({ ROLE, ROLE + VIEW })
    public HtmlResponse index() {
        return asHtml(path_AdminSysteminfo_AdminSysteminfoJsp).renderWith(data -> {
            registerEnvItems(data);
            registerPropItems(data);
            registerFessPropItems(data);
            registerBugReportItems(data);
        });
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============

    /**
     * Registers environment variables for rendering.
     *
     * @param data the render data to populate
     */
    protected void registerEnvItems(final RenderData data) {
        RenderDataUtil.register(data, "envItems", getEnvItems());
    }

    /**
     * Registers system properties for rendering.
     *
     * @param data the render data to populate
     */
    protected void registerPropItems(final RenderData data) {
        RenderDataUtil.register(data, "propItems", getPropItems());
    }

    /**
     * Registers Fess-specific properties for rendering.
     *
     * @param data the render data to populate
     */
    protected void registerFessPropItems(final RenderData data) {
        RenderDataUtil.register(data, "fessPropItems", getFessPropItems(fessConfig));
    }

    /**
     * Registers bug report items for rendering.
     *
     * @param data the render data to populate
     */
    protected void registerBugReportItems(final RenderData data) {
        RenderDataUtil.register(data, "bugReportItems", getBugReportItems());
    }

    /**
     * Gets a list of environment variables as key-value pairs.
     *
     * @return list of environment variable items
     */
    public static List<Map<String, String>> getEnvItems() {
        final List<Map<String, String>> itemList = new ArrayList<>();
        for (final Map.Entry<String, String> entry : System.getenv().entrySet()) {
            itemList.add(createMaskedItem(entry.getKey(), entry.getValue()));
        }
        return itemList;
    }

    /**
     * Gets a list of system properties as key-value pairs.
     *
     * @return list of system property items
     */
    public static List<Map<String, String>> getPropItems() {
        final List<Map<String, String>> itemList = new ArrayList<>();
        for (final Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            itemList.add(createMaskedItem(entry.getKey(), entry.getValue()));
        }
        return itemList;
    }

    /**
     * Gets a list of Fess-specific configuration properties as key-value pairs.
     *
     * @param fessConfig the Fess configuration object
     * @return list of Fess property items
     */
    public static List<Map<String, String>> getFessPropItems(final FessConfig fessConfig) {
        final List<Map<String, String>> itemList = new ArrayList<>();
        ComponentUtil.getSystemProperties().entrySet().stream().forEach(e -> {
            final String k = e.getKey().toString();
            final String value;
            if (isMaskedValue(k)) {
                value = MASKED_VALUE;
            } else {
                value = e.getValue().toString();
            }
            itemList.add(createItem(k, value));
        });
        if (fessConfig instanceof final ObjectiveConfig config) {
            config.keySet().stream().forEach(k -> {
                final String value;
                if (isMaskedValue(k)) {
                    value = MASKED_VALUE;
                } else {
                    value = config.get(k);
                }
                itemList.add(createItem(k, value));
            });
        }
        return itemList;
    }

    /**
     * Checks if a property value should be masked for security reasons.
     *
     * <p>A {@code -Dfess.system.<key>} or {@code -Dfess.config.<key>} JVM argument carries the value
     * of the setting {@code <key>}, so it is masked under the same rule as that setting. Without
     * this the JVM property list showed, in cleartext, a password that App Properties masks.
     *
     * @param name the property key to check, optionally spelled as a JVM argument
     * @return true if the value should be masked, false otherwise
     */
    public static boolean isMaskedValue(final String name) {
        // the variable of a setting (FESS_CONFIG_*, FESS_SYSTEM_*) is how a secret is handed over, whatever the setting is
        return ConfigEnvUtil.isConfigEnvName(name) || SecretKeyUtil.isSecretKey(toSettingKey(name));
    }

    private static String toSettingKey(final String name) {
        if (name != null) {
            if (name.startsWith(Constants.SYSTEM_PROP_PREFIX)) {
                return name.substring(Constants.SYSTEM_PROP_PREFIX.length());
            }
            if (name.startsWith(Constants.FESS_CONFIG_PREFIX)) {
                return name.substring(Constants.FESS_CONFIG_PREFIX.length());
            }
        }
        return name;
    }

    /**
     * Delegates to {@link SecretKeyUtil#isPrivateKeyMaterial(String)}.
     *
     * @param key the property key to check
     * @return true if the key matches
     */
    protected static boolean isPrivateKeyMaterial(final String key) {
        return SecretKeyUtil.isPrivateKeyMaterial(key);
    }

    /**
     * Delegates to {@link SecretKeyUtil#isSsoClientCredential(String)}.
     *
     * @param key the property key to check
     * @return true if the key matches
     */
    protected static boolean isSsoClientCredential(final String key) {
        return SecretKeyUtil.isSsoClientCredential(key);
    }

    /**
     * Delegates to {@link SecretKeyUtil#isEmbeddingApiKey(String)}.
     *
     * @param key the property key to check
     * @return true if the key matches
     */
    protected static boolean isEmbeddingApiKey(final String key) {
        return SecretKeyUtil.isEmbeddingApiKey(key);
    }

    /**
     * Delegates to {@link SecretKeyUtil#isLlmApiKey(String)}.
     *
     * @param key the property key to check
     * @return true if the key matches
     */
    protected static boolean isLlmApiKey(final String key) {
        return SecretKeyUtil.isLlmApiKey(key);
    }

    /**
     * Delegates to {@link SecretKeyUtil#isWebhookUrls(String)}.
     *
     * @param key the property key to check
     * @return true if the key matches
     */
    protected static boolean isWebhookUrls(final String key) {
        return SecretKeyUtil.isWebhookUrls(key);
    }

    /**
     * Delegates to {@link SecretKeyUtil#isSecretName(String)}.
     *
     * @param key the property key to check
     * @return true if the key matches
     */
    protected static boolean isSecretName(final String key) {
        return SecretKeyUtil.isSecretName(key);
    }

    /**
     * Gets a list of items relevant for bug reports.
     *
     * @return list of bug report items
     */
    public static List<Map<String, String>> getBugReportItems() {
        final List<Map<String, String>> itemList = new ArrayList<>();
        for (final String label : bugReportLabels) {
            itemList.add(createPropItem(label));
        }

        final DynamicProperties systemProperties = ComponentUtil.getSystemProperties();
        for (final Map.Entry<Object, Object> entry : systemProperties.entrySet()) {
            final String k = entry.getKey().toString();
            if (isBugReportTarget(k)) {
                final String value;
                if (isMaskedValue(k)) {
                    value = MASKED_VALUE;
                } else {
                    value = entry.getValue().toString();
                }
                itemList.add(createItem(k, value));
            }
        }

        return itemList;
    }

    /**
     * Checks if a property should be included in bug reports.
     *
     * @param key the property key to check
     * @return true if the property should be included, false otherwise
     */
    private static boolean isBugReportTarget(final Object key) {
        if ("snapshot.path".equals(key) || "label.value".equals(key)) {
            return false;
        }
        return true;
    }

    /**
     * Creates a property item from a system property key.
     *
     * @param key the property key
     * @return map containing the key-value pair
     */
    protected static Map<String, String> createPropItem(final String key) {
        return createItem(key, System.getProperty(key));
    }

    /**
     * Creates a key-value item map for display, replacing the value with the masked
     * placeholder when the key is sensitive, and the value of each sensitive
     * {@code -Dkey=value} option inside a value that is an option string.
     *
     * @param label the item label, which is the key checked by {@link #isMaskedValue(String)}
     * @param value the item value
     * @return map containing the key and the possibly masked value
     */
    protected static Map<String, String> createMaskedItem(final Object label, final Object value) {
        if (label != null && isMaskedValue(label.toString())) {
            return createItem(label, MASKED_VALUE);
        }
        return createItem(label, value instanceof final String text ? maskJvmOptions(text) : value);
    }

    /**
     * Masks the value of every {@code -Dkey=value} option in an option string whose key is
     * sensitive under {@link #isMaskedValue(String)}, and keeps the rest readable. An
     * environment variable such as {@code JAVA_TOOL_OPTIONS} or {@code FESS_JAVA_OPTS} is named
     * by its purpose, not by what it carries, so the variable name says nothing about the
     * secrets passed as {@code -Dfess.config.<key>=...} in it.
     *
     * <p>A value ends at the next white space, or at the closing quote when it starts with a
     * quote or when the whole option is quoted. This is not a shell parser: an unterminated
     * quote masks the rest of the text.
     *
     * @param text the option string, which may be null
     * @return the text with the values of sensitive options replaced by the placeholder
     */
    protected static String maskJvmOptions(final String text) {
        if (text == null) {
            return null;
        }
        final Matcher matcher = JVM_OPTION_PATTERN.matcher(text);
        final StringBuilder masked = new StringBuilder(text.length());
        int copied = 0;
        int from = 0;
        while (matcher.find(from)) {
            final int valueStart = matcher.end();
            if (isMaskedValue(matcher.group(2))) {
                masked.append(text, copied, valueStart).append(MASKED_VALUE);
                copied = findOptionValueEnd(text, valueStart, matcher.group(1));
                from = copied;
            } else {
                from = valueStart;
            }
        }
        return masked.append(text, copied, text.length()).toString();
    }

    private static int findOptionValueEnd(final String text, final int valueStart, final String optionQuote) {
        if (!optionQuote.isEmpty()) {
            // "-Dkey=value": the value ends before the quote that closes the option.
            final int close = text.indexOf(optionQuote, valueStart);
            return close < 0 ? text.length() : close;
        }
        if (valueStart < text.length() && (text.charAt(valueStart) == '"' || text.charAt(valueStart) == '\'')) {
            // -Dkey="value": the value is the quoted text, quotes included.
            final int close = text.indexOf(text.charAt(valueStart), valueStart + 1);
            return close < 0 ? text.length() : close + 1;
        }
        int end = valueStart;
        while (end < text.length() && !Character.isWhitespace(text.charAt(end))) {
            end++;
        }
        return end;
    }

    /**
     * Creates a key-value item map for display.
     *
     * @param label the item label
     * @param value the item value
     * @return map containing the formatted key-value pair
     */
    protected static Map<String, String> createItem(final Object label, final Object value) {
        final Map<String, String> map = new HashMap<>(2);
        map.put(Constants.ITEM_LABEL, label != null ? label.toString() : StringUtil.EMPTY);
        map.put(Constants.ITEM_VALUE, value != null ? value.toString() : StringUtil.EMPTY);
        return map;
    }

}
