/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
import org.elasticsearch.action.ActionListener;
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

    // private static final String VERSION_12_0 = "12.0";

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
        if (startReindex(isCheckboxEnabled(form.replaceAliases))) {
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

        /*
        if (VERSION_12_0.equals(form.targetVersion)) {
            try {
                upgradeFrom12_0();
                upgradeFromAll();

                saveInfo(messages -> messages.addSuccessStartedDataUpdate(GLOBAL));

                systemHelper.reloadConfiguration();
            } catch (final Exception e) {
                logger.warn("Failed to upgrade data.", e);
                saveError(messages -> messages.addErrorsFailedToUpgradeFrom(GLOBAL, VERSION_12_0, e.getLocalizedMessage()));
            }
        } else {
            saveError(messages -> messages.addErrorsUnknownVersionForUpgrade(GLOBAL));
        }
        */
        saveError(messages -> messages.addErrorsUnknownVersionForUpgrade(GLOBAL));
        return redirect(getClass());
    }

    /*
    private void upgradeFrom12_0() {
    }

    private void upgradeFromAll() {
    }
    */

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