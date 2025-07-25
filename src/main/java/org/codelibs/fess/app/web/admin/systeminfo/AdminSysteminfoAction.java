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

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.DynamicProperties;
import org.codelibs.fess.Constants;
import org.codelibs.fess.annotation.Secured;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.mylasta.direction.FessConfig;
import org.codelibs.fess.util.ComponentUtil;
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
        super();
    }

    /** Role name for admin system info operations */
    public static final String ROLE = "admin-systeminfo";

    private static final String MASKED_VALUE = "XXXXXXXX";

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
            itemList.add(createItem(entry.getKey(), entry.getValue()));
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
            itemList.add(createItem(entry.getKey(), entry.getValue()));
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
     * @param key the property key to check
     * @return true if the value should be masked, false otherwise
     */
    protected static boolean isMaskedValue(final String key) {
        return "http.proxy.password".equals(key) //
                || "ldap.admin.security.credentials".equals(key) //
                || "spnego.preauth.password".equals(key) //
                || "app.cipher.key".equals(key) //
                || "oic.client.id".equals(key) //
                || "oic.client.secret".equals(key);
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
            if (isBugReportTarget(entry.getKey())) {
                itemList.add(createItem(entry.getKey(), entry.getValue()));
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
