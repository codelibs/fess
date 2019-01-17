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
package org.codelibs.fess.app.web.admin.upgrade;

import javax.annotation.Resource;

import org.codelibs.fess.app.service.ScheduledJobService;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.client.FessEsClient;
import org.codelibs.fess.es.config.exbhv.DataConfigBhv;
import org.codelibs.fess.es.config.exbhv.ElevateWordBhv;
import org.codelibs.fess.es.config.exbhv.FileConfigBhv;
import org.codelibs.fess.es.config.exbhv.LabelTypeBhv;
import org.codelibs.fess.es.config.exbhv.RoleTypeBhv;
import org.codelibs.fess.es.config.exbhv.WebConfigBhv;
import org.codelibs.fess.es.user.exbhv.RoleBhv;
import org.codelibs.fess.util.ComponentUtil;
import org.codelibs.fess.util.UpgradeUtil;
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

    private static final String VERSION_12_0 = "12.0";

    private static final String VERSION_12_1 = "12.1";

    private static final String VERSION_12_2 = "12.2";

    private static final String VERSION_12_3 = "12.3";

    // ===================================================================================
    //                                                                           Attribute
    //
    @Resource
    protected RoleBhv roleBhv;

    @Resource
    protected RoleTypeBhv roleTypeBhv;

    @Resource
    protected LabelTypeBhv labelTypeBhv;

    @Resource
    protected WebConfigBhv webConfigBhv;

    @Resource
    protected FileConfigBhv fileConfigBhv;

    @Resource
    protected DataConfigBhv dataConfigBhv;

    @Resource
    protected ElevateWordBhv elevateWordBhv;

    @Resource
    protected FessEsClient fessEsClient;

    @Resource
    protected ScheduledJobService scheduledJobService;

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
    public HtmlResponse upgradeFrom(final UpgradeForm form) {
        validate(form, messages -> {}, this::asIndexHtml);
        verifyToken(this::asIndexHtml);

        if (VERSION_12_0.equals(form.targetVersion)) {
            try {
                upgradeFrom12_0();
                upgradeFrom12_1();
                upgradeFrom12_2();
                upgradeFrom12_3();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_12_0, e.getLocalizedMessage()));
            }
        } else if (VERSION_12_1.equals(form.targetVersion)) {
            try {
                upgradeFrom12_1();
                upgradeFrom12_2();
                upgradeFrom12_3();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_12_1, e.getLocalizedMessage()));
            }
        } else if (VERSION_12_2.equals(form.targetVersion)) {
            try {
                upgradeFrom12_2();
                upgradeFrom12_3();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_12_2, e.getLocalizedMessage()));
            }
        } else if (VERSION_12_3.equals(form.targetVersion)) {
            try {
                upgradeFrom12_3();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_12_2, e.getLocalizedMessage()));
            }
        } else {
            saveError(messages -> messages.addErrorsUnknownVersionForUpgrade(GLOBAL));
        }
        return redirect(getClass());
    }

    private void upgradeFrom12_0() {
        // nothing
    }

    private void upgradeFrom12_1() {
        final IndicesAdminClient indicesClient = fessEsClient.admin().indices();

        UpgradeUtil.putMapping(indicesClient, "fess_log.search_log", "search_log", "{\"dynamic_templates\": ["
                + "{\"documents\": {\"path_match\": \"documents.*\",\"mapping\": {\"type\": \"keyword\"}}}"//
                + "]}");
        UpgradeUtil.addFieldMapping(indicesClient, "fess_log.click_log", "click_log", "urlId",
                "{\"properties\":{\"urlId\":{\"type\":\"keyword\"}}}");
    }

    private void upgradeFrom12_2() {
        // nothing
    }

    private void upgradeFrom12_3() {
        ComponentUtil.getThumbnailManager().migrate();
    }

    private void upgradeFromAll() {
        // nothing
    }

}