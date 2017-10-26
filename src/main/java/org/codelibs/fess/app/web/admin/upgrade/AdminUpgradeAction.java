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
package org.codelibs.fess.app.web.admin.upgrade;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.config.exbhv.DataConfigBhv;
import org.codelibs.fess.es.config.exbhv.DataConfigToRoleBhv;
import org.codelibs.fess.es.config.exbhv.ElevateWordBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigToRoleBhv;
import org.codelibs.fess.es.config.exbhv.LabelToRoleBhv;
import org.codelibs.fess.es.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.es.config.exbhv.RoleTypeBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigToRoleBhv;
import org.codelibs.fess.es.user.exbhv.RoleBhv;
import org.codelibs.fess.util.UpgradeUtil;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.client.IndicesAdminClient;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.ruts.process.ActionRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminUpgradeAction extends FessAdminAction {

    // ===================================================================================
    //                                                                            Constant
    //
    private static final Logger logger = LoggerFactory.getLogger(AdminUpgradeAction.class);

    private static final String VERSION_11_0 = "11.0";

    private static final String VERSION_11_1 = "11.1";

    private static final String VERSION_11_2 = "11.2";

    private static final String VERSION_11_3 = "11.3";

    // ===================================================================================
    //                                                                           Attribute
    //
    @Resource
    protected RoleBhv roleBhv;

    @Resource
    protected RoleTypeBhv roleTypeBhv;

    @Resource
    protected LabelToRoleBhv labelToRoleBhv;

    @Resource
    protected LabelTypeBhv labelTypeBhv;

    @Resource
    protected WebConfigToRoleBhv webConfigToRoleBhv;

    @Resource
    protected WebConfigBhv webConfigBhv;

    @Resource
    protected FileConfigToRoleBhv fileConfigToRoleBhv;

    @Resource
    protected FileConfigBhv fileConfigBhv;

    @Resource
    protected DataConfigToRoleBhv dataConfigToRoleBhv;

    @Resource
    protected DataConfigBhv dataConfigBhv;

    @Resource
    protected ElevateWordBhv elevateWordBhv;

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    private ScheduledJobService scheduledJobService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameUpgrade()));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============

    @Execute
    public HtmlResponse index() {
        saveToken();
        return asIndexHtml();
    }

    private HtmlResponse asIndexHtml() {
        return asHtml(path_AdminUpgrade_AdminUpgradeJsp).useForm(UpgradeForm.class);
    }

    @Execute
    public HtmlResponse reindexOnly(final UpgradeForm form) {
        validate(form, messages -> {}, () -> {
            return asIndexHtml();
        });
        verifyToken(() -> asIndexHtml());
        if (startReindex(Constants.ON.equalsIgnoreCase(form.replaceAliases))) {
            saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));
        }
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse upgradeFrom(final UpgradeForm form) {
        validate(form, messages -> {}, () -> {
            return asIndexHtml();
        });
        verifyToken(() -> asIndexHtml());

        if (VERSION_11_0.equals(form.targetVersion)) {
            try {
                upgradeFrom11_0();
                upgradeFrom11_1();
                upgradeFrom11_2();
                upgradeFrom11_3();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_11_0, e.getLocalizedMessage()));
            }
        } else if (VERSION_11_1.equals(form.targetVersion)) {
            try {
                upgradeFrom11_1();
                upgradeFrom11_2();
                upgradeFrom11_3();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_11_1, e.getLocalizedMessage()));
            }
        } else if (VERSION_11_2.equals(form.targetVersion)) {
            try {
                upgradeFrom11_2();
                upgradeFrom11_3();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_11_2, e.getLocalizedMessage()));
            }
        } else if (VERSION_11_3.equals(form.targetVersion)) {
            try {
                upgradeFrom11_3();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_11_3, e.getLocalizedMessage()));
            }
        } else {
            saveError(messages -> messages.addErrorsUnknownVersionForUpgrade(GLOBAL));
        }
        return redirect(getClass());
    }

    private void upgradeFrom11_0() {
    }

    private void upgradeFrom11_1() {
        final IndicesAdminClient indicesClient = fessEsClient.admin().indices();
        final String configIndex = ".fess_config";

        // update mapping
        UpgradeUtil.addFieldMapping(indicesClient, configIndex, "thumbnail_queue", "thumbnail_id",
                "{\"properties\":{\"thumbnail_id\":{\"type\":\"keyword\"}}}");
    }

    private void upgradeFrom11_2() {
        final IndicesAdminClient indicesClient = fessEsClient.admin().indices();
        final String configIndex = ".fess_config";
        final String searchLogIndex = "fess_log";

        // update mapping
        UpgradeUtil.addMapping(indicesClient, configIndex, "related_content", "fess_indices/.fess_config");
        UpgradeUtil.addMapping(indicesClient, configIndex, "related_query", "fess_indices/.fess_config");

        // update mapping
        if (UpgradeUtil.addFieldMapping(indicesClient, searchLogIndex, "search_log", "virtualHost",
                "{\"properties\":{\"virtualHost\":{\"type\":\"keyword\"}}}")) {
            UpgradeUtil.putMapping(indicesClient, searchLogIndex, "search_log", "{\"dynamic_templates\": ["
                    + "{\"search_fields\": {\"path_match\": \"searchField.*\",\"mapping\": {\"type\": \"keyword\"}}}"//
                    + "]}");
        }
        UpgradeUtil.addFieldMapping(indicesClient, configIndex, "web_config", "virtualHost",
                "{\"properties\":{\"virtualHost\":{\"type\":\"keyword\"}}}");
        UpgradeUtil.addFieldMapping(indicesClient, configIndex, "data_config", "virtualHost",
                "{\"properties\":{\"virtualHost\":{\"type\":\"keyword\"}}}");
        UpgradeUtil.addFieldMapping(indicesClient, configIndex, "file_config", "virtualHost",
                "{\"properties\":{\"virtualHost\":{\"type\":\"keyword\"}}}");
        UpgradeUtil.addFieldMapping(indicesClient, configIndex, "key_match", "virtualHost",
                "{\"properties\":{\"virtualHost\":{\"type\":\"keyword\"}}}");
        UpgradeUtil.addFieldMapping(indicesClient, configIndex, "label_type", "virtualHost",
                "{\"properties\":{\"virtualHost\":{\"type\":\"keyword\"}}}");
    }

    private void upgradeFrom11_3() {
        final IndicesAdminClient indicesClient = fessEsClient.admin().indices();
        final String configIndex = ".fess_config";

        UpgradeUtil.addFieldMapping(indicesClient, configIndex, "related_content", "sortOrder",
                "{\"properties\":{\"sortOrder\":{\"type\":\"integer\"}}}");
    }

    private void upgradeFromAll() {
        final IndicesAdminClient indicesClient = fessEsClient.admin().indices();
        final String crawlerIndex = fessConfig.getIndexDocumentCrawlerIndex();

        // .crawler
        if (UpgradeUtil.existsIndex(indicesClient, crawlerIndex, true, true)) {
            UpgradeUtil.deleteIndex(indicesClient, crawlerIndex, response -> {});
        }
    }

    private boolean startReindex(final boolean replaceAliases) {
        final String docIndex = "fess";
        final String fromIndex = fessConfig.getIndexDocumentUpdateIndex();
        final String toIndex = docIndex + "." + new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        if (fessEsClient.createIndex(docIndex, "doc", toIndex)) {
            fessEsClient.admin().cluster().prepareHealth(toIndex).setWaitForYellowStatus().execute(ActionListener.wrap(response -> {
                fessEsClient.addMapping(docIndex, "doc", toIndex);
                fessEsClient.reindex(fromIndex, toIndex, replaceAliases);
                if (replaceAliases && !fessEsClient.updateAlias(toIndex)) {
                    logger.warn("Failed to update aliases for " + fromIndex + " and " + toIndex);
                }
            }, e -> {
                logger.warn("Failed to reindex from " + fromIndex + " to " + toIndex, e);
            }));
            return true;
        }
        saveError(messages -> messages.addErrorsFailedToReindex(GLOBAL, fromIndex, toIndex));
        return false;
    }

}