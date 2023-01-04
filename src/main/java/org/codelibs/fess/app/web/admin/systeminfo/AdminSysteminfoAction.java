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
package org.codelibs.fess.app.web.admin.systeminfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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

/**
 * @author Keiichi Watanabe
 */
public class AdminSysteminfoAction extends FessAdminAction {

    public static final String ROLE = "admin-systeminfo";

    private static final String MASKED_VALUE = "XXXXXXXX";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
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

    protected void registerEnvItems(final RenderData data) {
        RenderDataUtil.register(data, "envItems", getEnvItems());
    }

    protected void registerPropItems(final RenderData data) {
        RenderDataUtil.register(data, "propItems", getPropItems());
    }

    protected void registerFessPropItems(final RenderData data) {
        RenderDataUtil.register(data, "fessPropItems", getFessPropItems(fessConfig));
    }

    protected void registerBugReportItems(final RenderData data) {
        RenderDataUtil.register(data, "bugReportItems", getBugReportItems());
    }

    public static List<Map<String, String>> getEnvItems() {
        final List<Map<String, String>> itemList = new ArrayList<>();
        for (final Map.Entry<String, String> entry : System.getenv().entrySet()) {
            itemList.add(createItem(entry.getKey(), entry.getValue()));
        }
        return itemList;
    }

    public static List<Map<String, String>> getPropItems() {
        final List<Map<String, String>> itemList = new ArrayList<>();
        for (final Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            itemList.add(createItem(entry.getKey(), entry.getValue()));
        }
        return itemList;
    }

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

    protected static boolean isMaskedValue(final String key) {
        return "http.proxy.password".equals(key) //
                || "ldap.admin.security.credentials".equals(key) //
                || "spnego.preauth.password".equals(key) //
                || "app.cipher.key".equals(key) //
                || "oic.client.id".equals(key) //
                || "oic.client.secret".equals(key);
    }

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

    private static boolean isBugReportTarget(final Object key) {
        if ("snapshot.path".equals(key) || "label.value".equals(key)) {
            return false;
        }
        return true;
    }

    protected static Map<String, String> createPropItem(final String key) {
        return createItem(key, System.getProperty(key));
    }

    protected static Map<String, String> createItem(final Object label, final Object value) {
        final Map<String, String> map = new HashMap<>(2);
        map.put(Constants.ITEM_LABEL, label != null ? label.toString() : StringUtil.EMPTY);
        map.put(Constants.ITEM_VALUE, value != null ? value.toString() : StringUtil.EMPTY);
        return map;
    }

}
